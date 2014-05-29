#include <stdio.h>
#include <jni.h>
#include <unistd.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include <libavutil/opt.h>
#include <libswresample/swresample.h>
#include <libavutil/samplefmt.h>

#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/bitmap.h>
#include <pthread.h>
#include "threadqueue.h"

#define LOG_TAG "sodaplayer"
#define LOGE(...) __android_log_print(6, LOG_TAG, __VA_ARGS__);

ANativeWindow* window;//对应surfaceview的native层窗口对象

typedef struct VideoState{//解码过程中的数据结构
	int videoStream;
	AVCodecContext *pCodecCtx;
	AVFrame *pFrame;
	struct SwsContext *sws_ctx;
	AVFrame *RGBAFrame;
	ANativeWindow_Buffer windowBuffer;
	void* buffer;
	int audioStream;
	AVCodecContext *aCodecCtx;
	AVFrame decodec_frame;
	uint8_t **dst_data;
	struct SwrContext *swr_ctx;
	int64_t now_audio_dts;
	int sample_rate_src;
}VideoState;

int width=320;
int height=240;
int stop=0;
int timeout_flag =0;
int disable_video=0;

JavaVM *gJavaVm;
jobject gJavaobj;
VideoState *vs;
jbyteArray global_aarray;
jmethodID initAdudioTrack;
jmethodID onNativeConnected;
jmethodID onNativeDisConnected;
jmethodID onNativeFinish;

//队列
struct threadqueue *queue;
struct threadqueue *video_queue;
struct threadqueue *audio_queue;

static int call_back(void* ctx){
	if(timeout_flag){
		LOGE("回调中发现timeout\n");
		return 1;
	}else if(stop){
		LOGE("回调中发现退出\n");
		return 1;
	}else{
		return 0;
	}
}

//创建一个Android的bitmap对象
jobject createBitmap(JNIEnv *pEnv, int pWidth, int pHeight) {
	int i;
	//get Bitmap class and createBitmap method ID
	jclass javaBitmapClass = (jclass)(*pEnv)->FindClass(pEnv, "android/graphics/Bitmap");
	jmethodID mid = (*pEnv)->GetStaticMethodID(pEnv, javaBitmapClass, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
	//create Bitmap.Config
	//reference: https://forums.oracle.com/thread/1548728
	const wchar_t* configName = L"ARGB_8888";
	int len = wcslen(configName);
	jstring jConfigName;
	if (sizeof(wchar_t) != sizeof(jchar)) {
		//wchar_t is defined as different length than jchar(2 bytes)
		jchar* str = (jchar*)malloc((len+1)*sizeof(jchar));
		for (i = 0; i < len; ++i) {
			str[i] = (jchar)configName[i];
		}
		str[len] = 0;
		jConfigName = (*pEnv)->NewString(pEnv, (const jchar*)str, len);
	} else {
		//wchar_t is defined same length as jchar(2 bytes)
		jConfigName = (*pEnv)->NewString(pEnv, (const jchar*)configName, len);
	}
	jclass bitmapConfigClass = (*pEnv)->FindClass(pEnv, "android/graphics/Bitmap$Config");
	jobject javaBitmapConfig = (*pEnv)->CallStaticObjectMethod(pEnv, bitmapConfigClass,
			(*pEnv)->GetStaticMethodID(pEnv, bitmapConfigClass, "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;"), jConfigName);
	//create the bitmap
	return (*pEnv)->CallStaticObjectMethod(pEnv, javaBitmapClass, mid, pWidth, pHeight, javaBitmapConfig);
}

//当Android系统中对应播放窗口的Surfaceview创建的时候，在native层得到这个surface的引用地址
int Java_info_sodapanda_sodaplayer_MainActivity_setupsurface(JNIEnv* env,jobject thiz,jobject pSurface,int pwidth,int pheight){
	fprintf(stderr,"setupsurface 开始\n");
	window = ANativeWindow_fromSurface(env,pSurface);
	if(ANativeWindow_setBuffersGeometry(window,width,height,WINDOW_FORMAT_RGBA_8888)){
		fprintf(stderr,"创建window完成\n");
	};
	disable_video=0;
	return 0;
}

//从java层发送停止播放的消息到native层
int Java_info_sodapanda_sodaplayer_MainActivity_nativestop(JNIEnv* env,jobject thiz){
	stop=1;
	return 0;
}

//当Android中对应播放窗口的surface被销毁的时候，在native层停止对该窗口的操作
int Java_info_sodapanda_sodaplayer_MainActivity_nativedisablevidio(JNIEnv* env,jobject thiz){
	disable_video=1;
	return 0;
}

//从packet队列中取用的线程
void *getPacket(void* arg){
//	usleep(500000);//缓存一点数据
	fprintf(stderr,"getpacket线程开始\n");
	struct timespec *time = malloc(sizeof(struct timespec));
	time->tv_sec=10;//网络不好最多等10秒
	time->tv_nsec=0;

	while(1){
		struct threadmsg *msg = malloc(sizeof(struct threadmsg));
		msg->data=NULL;

		AVPacket pavpacket;
		thread_queue_get(queue,time,msg);

		if(msg->msgtype==-1){//正常退出
			LOGE("get线程正常退出\n");
			thread_queue_add(video_queue,NULL,-1);
			thread_queue_add(audio_queue,NULL,-1);
			break;
		}

		if(msg->data ==NULL){
			LOGE("get线程超时退出\n");
			thread_queue_add(video_queue,NULL,-1);
			thread_queue_add(audio_queue,NULL,-1);
			timeout_flag = 1;
			break;
		}

		AVPacket *packet_p = msg->data;
		pavpacket = *packet_p;

		if(pavpacket.stream_index==vs->videoStream){
			thread_queue_add(video_queue,packet_p,1);
		}else
		if(pavpacket.stream_index==vs->audioStream){
			thread_queue_add(audio_queue,packet_p,1);
		}

	}
	free(time);
	return NULL;
}

//视频线程
void *video_thread(void* arg){
	fprintf(stderr,"视频线程开始\n");
	struct timespec *time = malloc(sizeof(struct timespec));
	time->tv_sec=10;//网络不好最多等10秒
	time->tv_nsec=0;

	while(1){
		if(stop){
			break;
		}
		struct threadmsg *msg = malloc(sizeof(struct threadmsg));
		msg->data=NULL;

		AVPacket pavpacket;
		thread_queue_get(video_queue,time,msg);

		if(msg->msgtype==-1){//正常退出
			LOGE("视频线程正常退出\n");
			break;
		}

		if(msg->data ==NULL){
			LOGE("视频线程超时退出");
			timeout_flag = 1;
			break;
		}

		AVPacket *packet_p = msg->data;
		pavpacket = *packet_p;
		int64_t packet_dts = packet_p->dts;

//		LOGE("视频PTS = %d\nDTS = %d",packet_p->pts,packet_p->dts);
		if(packet_dts<=0){//dts值无效
			continue;
		}

		if(pavpacket.stream_index!=vs->videoStream){
			continue;
		}
		if(disable_video){
			continue;
		}

		int frame_finished=0;
		avcodec_decode_video2(vs->pCodecCtx, vs->pFrame, &frame_finished,&pavpacket);//将pavpacket中的数据解码成，放入pFram中
		if(frame_finished){
			sws_scale//对解码后的数据进行色彩空间转换，yuv420p 转为rgba8888
				(
					vs->sws_ctx,
					(uint8_t const * const *)(vs->pFrame)->data,
					(vs->pFrame)->linesize,
					0,
					vs->pCodecCtx->height,
					vs->RGBAFrame->data,
					vs->RGBAFrame->linesize
				);
			if (ANativeWindow_lock(window, &(vs->windowBuffer), NULL) < 0) {
				LOGE("cannot lock window");
				return NULL;
			}else if(!disable_video){
				memcpy((vs->windowBuffer).bits, vs->buffer,  width * height * 4);//将解码出来的数据复制到surfaceview对应的内存区域
				ANativeWindow_unlockAndPost(window);//释放对surface的锁，并且更新对应surface数据进行显示
			}
		}
		av_free_packet(&pavpacket);
		free(msg->data);
		free(msg);

		//延时操作
		int64_t delta = packet_dts - vs->now_audio_dts;
		if(delta<0){
			continue;
		}
		if(delta>0){
			usleep(66600);
		}
	}
	free(time);
	return NULL;
}

//音频线程
void *audio_thread(void* arg){
	LOGE("音频线程开启\n");

	JNIEnv *audioEnv;
	(*gJavaVm)->AttachCurrentThread(gJavaVm,&audioEnv,NULL);
	jclass javacls = (*audioEnv)->GetObjectClass(audioEnv,gJavaobj);
	jmethodID play = (*audioEnv)->GetMethodID(audioEnv,javacls,"playSound","([BI)V");

	struct timespec *time = malloc(sizeof(struct timespec));
	time->tv_sec=10;//网络不好最多等10秒
	time->tv_nsec=0;

	while(1){
		if(stop){
			break;
		}
		struct threadmsg *msg = malloc(sizeof(struct threadmsg));
		msg->data=NULL;

		AVPacket pavpacket;
		thread_queue_get(audio_queue,time,msg);

		if(msg->msgtype==-1){//正常退出
			break;
		}

		if(msg->data ==NULL){
			LOGE("音频线程超时退出");
			timeout_flag = 1;
			break;
		}

		AVPacket *packet_p = msg->data;
		pavpacket = *packet_p;

		if(pavpacket.stream_index!=vs->audioStream){
			continue;
		}


		vs->now_audio_dts = packet_p->dts;
		int len =0;
		int dst_linesize;
		while(pavpacket.size>0){
			int got_frame=0;
			len = avcodec_decode_audio4(vs->aCodecCtx,&(vs->decodec_frame),&got_frame,&pavpacket);

			//音频转化
			av_samples_alloc_array_and_samples(&(vs->dst_data),&dst_linesize,1,(vs->decodec_frame).nb_samples,AV_SAMPLE_FMT_S16,0);
			swr_convert(vs->swr_ctx,vs->dst_data,(vs->decodec_frame).nb_samples,(const uint8_t **)&(vs->decodec_frame).data[0],(vs->decodec_frame).nb_samples);
			if(len<0){
				return NULL;
			}
			pavpacket.size -= len;
			pavpacket.data += len;

			if(got_frame){
				jbyte *bytes = (*audioEnv)->GetByteArrayElements(audioEnv, global_aarray, NULL);
				memcpy(bytes,*(vs->dst_data),dst_linesize);
				(*audioEnv)->ReleaseByteArrayElements(audioEnv, global_aarray, bytes, 0);
				(*audioEnv)->CallVoidMethod(audioEnv,gJavaobj,play,global_aarray,dst_linesize);
			}
		}
		av_free_packet(&pavpacket);
		free(msg->data);
		free(msg);
	}
	(*gJavaVm)->DetachCurrentThread(gJavaVm);
	free(time);
	LOGE("音频线程退出\n");
	return NULL;
}



//启动播放器
int Java_info_sodapanda_sodaplayer_MainActivity_openfile(JNIEnv* env,jobject obj,jstring file){
	//初始化队列
	queue = malloc(sizeof(struct threadqueue));
	thread_queue_init(queue);
	video_queue = malloc(sizeof(struct threadqueue));
	thread_queue_init(video_queue);
	audio_queue = malloc(sizeof(struct threadqueue));
	thread_queue_init(audio_queue);

	stop=0;
	timeout_flag = 0;
	vs=av_malloc(sizeof (VideoState));

	fprintf(stderr,"开始执行openfile\n");
	jboolean isfilenameCopy;
	const char *filename = (*env)-> GetStringUTFChars(env, file, &isfilenameCopy);
	jclass cls = (*env)->GetObjectClass(env,obj);
	initAdudioTrack = (*env)->GetMethodID(env,cls,"initAdudioTrack","(I)[B");
	onNativeConnected = (*env)->GetMethodID(env,cls,"onNativeConnected","()V");
	if(onNativeConnected==NULL){
		LOGE("onNativeConnected获取methodid失败\n");
		return -1;
	}
	onNativeDisConnected = (*env)->GetMethodID(env,cls,"onNativeDisConnected","()V");
	onNativeFinish = (*env)->GetMethodID(env,cls,"onNativeFinish","()V");

	(*env)->GetJavaVM(env,&gJavaVm);
	gJavaobj = (*env)->NewGlobalRef(env,obj);

	//video
	AVFormatContext *pFormatCtx =NULL;
	AVCodecContext *pCodecCtx=NULL;
	AVCodec *pCodec=NULL;
	AVFrame *pFrame =NULL;
	int videoStream;
	AVDictionary *videoOptionsDict= NULL;
	struct SwsContext *sws_ctx =NULL;
	void* buffer;
	jobject bitmap;

	//audio
	AVCodecContext *aCodecCtx=NULL;
	AVCodec *aCodec=NULL;
	int audioStream;
	AVDictionary *audioOptionsDict = NULL;
	AVFrame decodec_frame;

	av_register_all();	//注册解码器等操作
	avformat_network_init();	//初始化网络
	pFormatCtx= avformat_alloc_context();
	pFormatCtx->max_analyze_duration=10000;//最长分析时间10000微秒
	pFormatCtx->interrupt_callback.callback = call_back;//设置中断回调函数
	pFormatCtx->interrupt_callback.opaque = pFormatCtx;//中断回调函数的参数

	//开始读取线程 提前开始为了捕获到打开文件的超时
	pthread_t rtid;
	pthread_create(&rtid,NULL,getPacket,env);

	//打开视频文件
	if(avformat_open_input(&pFormatCtx,filename, NULL, NULL)!=0){
		if(stop){
			return 0;
		}
		fprintf(stderr,"无法打开文件\n");
		return -1; // 无法打开视频文件
	}
	if(stop){
		return 0;
	}

	// 检索视频信息
	if(avformat_find_stream_info(pFormatCtx, NULL)<0){
		fprintf(stderr,"无法找到流信息\n");
		return -1;
	}

	av_dump_format(pFormatCtx, 0, filename, 0);//打印分析的视频信息

	videoStream = -1;
	audioStream = -1;

	int i =0;
	for (i=0;i<pFormatCtx->nb_streams;i++){//遍历寻找音频流和视频流
		if(videoStream<0 && pFormatCtx->streams[i]->codec->codec_type==AVMEDIA_TYPE_VIDEO){
			videoStream = i;
			fprintf(stderr,"videostream is %d\n",videoStream);
		}
		if(audioStream<0 && pFormatCtx->streams[i]->codec->codec_type==AVMEDIA_TYPE_AUDIO){
			audioStream = i;
			fprintf(stderr,"audiostream is %d\n",audioStream);
			vs->sample_rate_src = pFormatCtx->streams[i]->codec->sample_rate;
			LOGE("采样率是 %d\n",vs->sample_rate_src);
			if(vs->sample_rate_src > 0){
				jbyteArray aarray = (jbyteArray)((*env)->CallObjectMethod(env,obj,initAdudioTrack,vs->sample_rate_src));
				global_aarray = (*env)->NewGlobalRef(env,aarray);
				LOGE("initAdudioTrack返回\n");
			}

		}
	}

	if(videoStream==-1){
		fprintf(stderr,"无法找到视频流");
	}
	if(audioStream==-1 || vs->sample_rate_src<=0){
		fprintf(stderr,"无法找到音频流");
	}

	//打开音频解码器
	if(audioStream != -1 && vs->sample_rate_src>0){
		aCodecCtx = pFormatCtx->streams[audioStream]->codec;
		aCodec= avcodec_find_decoder(aCodecCtx->codec_id);

		if(avcodec_open2(aCodecCtx,aCodec,&audioOptionsDict)<0){
			fprintf(stderr,"无法打开解码器");
			return -1;
		}
	}

	//打开视频解码器
	if(videoStream != -1){
		pCodecCtx=pFormatCtx->streams[videoStream]->codec;
		pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
		if(avcodec_open2(pCodecCtx,pCodec,&videoOptionsDict)<0){
			fprintf(stderr,"无法打开视频解码器\n");
			return -1;
		}
	}
	pFrame = avcodec_alloc_frame();

	//视频转换
	sws_ctx = sws_getContext(
		pCodecCtx->width,
		pCodecCtx->height,
		pCodecCtx->pix_fmt,
		width,
		height,
		AV_PIX_FMT_RGBA,
		SWS_BILINEAR,
		NULL,
		NULL,
		NULL
	);

	//创建bitmap
	bitmap = createBitmap(env, width, height);
	AndroidBitmap_lockPixels(env, bitmap, &buffer);
	AVFrame *RGBAFrame;
	RGBAFrame = avcodec_alloc_frame();
	avpicture_fill((AVPicture *) RGBAFrame, buffer, AV_PIX_FMT_RGBA, width, height);
	ANativeWindow_Buffer windowBuffer;

	//原始音频转换
	struct SwrContext *swr_ctx;
	swr_ctx = swr_alloc();

	av_opt_set_int(swr_ctx, "in_sample_fmt", AV_SAMPLE_FMT_FLT, 0);
	av_opt_set_int(swr_ctx, "out_sample_fmt", AV_SAMPLE_FMT_S16, 0);
	av_opt_set_int(swr_ctx, "in_channel_layout", AV_CH_LAYOUT_MONO, 0);
	av_opt_set_int(swr_ctx, "out_channel_layout", AV_CH_LAYOUT_MONO, 0);

	swr_init(swr_ctx);
	uint8_t **dst_data =NULL;
	int dst_linesize=0;

	vs->RGBAFrame=RGBAFrame;
	vs->buffer=buffer;
	vs->pCodecCtx=pCodecCtx;
	vs->pFrame=pFrame;
	vs->sws_ctx=sws_ctx;
	vs->windowBuffer=windowBuffer;
	vs->videoStream=videoStream;
	vs->aCodecCtx=aCodecCtx;
	vs->audioStream=audioStream;
	vs->decodec_frame=decodec_frame;
	vs->dst_data=dst_data;
	vs->swr_ctx=swr_ctx;

	//视频线程
	pthread_t video_tid;
	if(videoStream!=-1){
		pthread_create(&video_tid,NULL,video_thread,NULL);
	}

	//音频线程
	pthread_t audio_tid;
	if(audioStream!=-1 && vs->sample_rate_src >0){
		pthread_create(&audio_tid,NULL,audio_thread,NULL);
	}

	//通知android界面dissmiss等待progress dialog
	(*env)->CallVoidMethod(env,obj,onNativeConnected);

	while(1){
		if(stop){//关闭线程
			//队列放空表示结束
			thread_queue_add(queue,NULL,-1);
			break;
		}

		AVPacket *packet_p = malloc(sizeof(AVPacket));
		//加入队列
		if(av_read_frame(pFormatCtx,packet_p)<0){//网络断开或停止视频
			thread_queue_add(queue,NULL,-1);
			break;
		}

		thread_queue_add(queue,packet_p,1);
	}

	LOGE("native主循环退出\n");
	thread_queue_add(queue,NULL,-1);//让get线程停止
	pthread_join(rtid,NULL);
	pthread_join(video_tid,NULL);
	pthread_join(audio_tid,NULL);

	LOGE("getpacket线程环退出\n");
	thread_queue_cleanup(queue,1);
	thread_queue_cleanup(video_queue,1);
	thread_queue_cleanup(audio_queue,1);

    if (dst_data)
        av_freep(&dst_data[0]);
    av_freep(&dst_data);
    av_free(vs);
    av_free(RGBAFrame);
    av_free(pFrame);
    avcodec_close(pCodecCtx);
    avcodec_close(aCodecCtx);
    avformat_close_input(&pFormatCtx);
    AndroidBitmap_unlockPixels(env,bitmap);
    LOGE("清理退出\n");
    if(timeout_flag){
    	return -1;
    }else{
    	return 0;
    }
}

