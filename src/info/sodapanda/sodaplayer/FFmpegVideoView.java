package info.sodapanda.sodaplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class FFmpegVideoView extends SurfaceView implements SurfaceHolder.Callback{
	private Activity activity;
	public static boolean is_playing = false;
	
	AudioTrack audioTrack;
	private ArrayList<String> rtmpUrlList;
	private long instance;
	
	Thread play_thread;

    private PlayCallback playCallback;
	
	int retry_time = 0;

	
	public FFmpegVideoView(Context context,PlayCallback playCallback) {
		super(context);
        this.playCallback = playCallback;
		instance = getPlayInstance();
		Log.i("soda", "得到一个播放事例 "+instance);
		
		this.activity = (Activity) context;
		initSurfaceView();
	}
	
	private void initSurfaceView(){
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		setupsurface(holder.getSurface(), width, height,instance);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		nativedisablevidio(instance);
	}
	
	/**
	 * init AudioTrack called by native code
	 * @param sample_rate bit rate of source audio
	 * @return audio buffer byte array
	 */
	public byte[] initAdudioTrack(int sample_rate){
		Log.i("soda","java 得到采样率 "+sample_rate);
		int buffer_size=AudioTrack.getMinBufferSize(sample_rate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		if(buffer_size<8192){
			buffer_size = 8192;
		}
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sample_rate,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT, buffer_size,AudioTrack.MODE_STREAM);

		Log.e("soda", "音频Buffer_size "+buffer_size);
		byte[] bytes = new byte[buffer_size];
		return bytes;
	}
	
	/**
	 * start to play PCM audio in buffer, called by native code
	 * @param buffer PCM data to be played
	 * @param buff_size bytes of buffer
	 */
	public void playSound(byte[] buffer,int buff_size){
		if(audioTrack==null){
			Log.e("sodaplayer","audioTrack is null");
			return;
		}
		if(audioTrack.getPlayState()!=AudioTrack.PLAYSTATE_PLAYING){
			audioTrack.play();
		}
		audioTrack.write(buffer, 0, buff_size);
	}
	
	/**
	 * start player
	 * @param filename
	 */
	private void start(final String filename) {
		//判断一下视频是不是正在播放，正在播放的话直接return 
		if(play_thread!=null && play_thread.isAlive()){
			return ;
		}
        playCallback.onConnecting();
		play_thread = new Thread(new Runnable() {
			public void run() {
				is_playing = true;
				int error=openfile(filename,instance);
				is_playing = false;
				if(error !=0){//打开文件错误
					activity.runOnUiThread(new Runnable() {
						public void run() {
							Log.i("soda","打开文件错误");
							stop();
                            videoDisConnected();
						}
					});
				}
			}
		});
		play_thread.start();
	}
	
	public void startPlayer(ArrayList<String> rtmpUrlList) {
		if(rtmpUrlList==null || rtmpUrlList.size()<=0){
			return;
		}
		this.rtmpUrlList = rtmpUrlList;
		String filename = rtmpUrlList.get(0);
        Log.i("soda","播放地址 "+filename);
		start(filename);
	}

	/**
	 * stop play
	 */
	public void stop() {
		nativestop(instance);
		if (play_thread!=null) {
			try {
				play_thread.join();
				Log.e("soda", "play_thread 返回");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.i("soda","停止声音");
		if (audioTrack!=null) {
			audioTrack.flush();
			audioTrack.stop();
			audioTrack.release();
			audioTrack=null;
		}
        playCallback.onStop();
	}
	
	public void onNativeConnected(){
		Log.i("soda","视频连接上了");
        playCallback.onConnected();
		retry_time = 0;
	}
	
	//**视频reconnect */
	public void videoDisConnected() {
		if (retry_time < rtmpUrlList.size()) {
			Log.i("test","第" + retry_time + "次重试,地址是" + rtmpUrlList.get(retry_time));
			start(rtmpUrlList.get(retry_time));
		} else {
			Log.i("test", "重试" + retry_time + "失败 退出");
			stop();
			retry_time = 0;
			Toast.makeText(activity, "视频连接失败", Toast.LENGTH_LONG).show();
		}
		retry_time++;
	}
	
	public native int openfile(String filename,long instance);
	public native int setupsurface(Surface surface,int width,int height,long instance);
	public native int nativestop(long instance);
	public native int nativedisablevidio(long instance);
	public native long getPlayInstance();

}
