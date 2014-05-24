package info.sodapanda.sodaplayer;

import java.util.ArrayList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements SurfaceHolder.Callback{
	AudioTrack audioTrack;
//	String filename;
	private ArrayList<String> rtmpUrlList;

	public SurfaceView player_surface;
	
	int width;
	int height;
	Thread play_thread;
	boolean isDestroyed=false;
	
	public static boolean is_playing = false;
	int error;
	Handler handler;
	AudioManager audioMan;
	private OnAudioFocusChangeListener audioFocusListener;
//	ProgressDialog pd;
	
	private PlayerListener mListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		initPd();
		initReconnectListener();
		
		//surfaceView
		Display display = getWindowManager().getDefaultDisplay();
		int screen_width = display.getWidth();
		width=screen_width;
		height = (int) (screen_width * 0.75f);
		player_surface = new SurfaceView(this);
		player_surface.getHolder().addCallback(this);
		player_surface.setLayoutParams(new LayoutParams(width, height));
		handler = new Handler();
		
		audioMan = (AudioManager) getSystemService(AUDIO_SERVICE);
		audioFocusListener = new OnAudioFocusChangeListener() {
			
			@Override
			public void onAudioFocusChange(int focusChange) {
				if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
					muteAudio();
				}else if(focusChange==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
					muteAudio();
				}else if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
					muteAudio();
				}else if(focusChange == AudioManager.AUDIOFOCUS_GAIN){
					resumeAudio();
				}
			}
		};
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		isDestroyed = true;
	}
	
	private void initReconnectListener(){
		mListener = new ReconnectListener();
	}
	
//	private void initPd(){
//		pd = new ProgressDialog(this);
//		pd.setMessage("视频加载中");
//		pd.setCancelable(false);
//	}
	
	private void muteAudio(){
		if(audioTrack == null){
			return;
		}
		audioTrack.setStereoVolume(0f, 0f);
	}
	
	private void resumeAudio(){
		audioTrack.setStereoVolume(1f, 1f);
	}
	
	public byte[] initAdudioTrack(int sample_rate){
		Log.i("soda","java 得到采样率 "+sample_rate);
		int buffer_size=AudioTrack.getMinBufferSize(sample_rate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		if(buffer_size<4096){
			buffer_size = 4096;
		}
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sample_rate,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT, buffer_size,AudioTrack.MODE_STREAM);

		Log.e("soda", "音频Buffer_size "+buffer_size);
		byte[] bytes = new byte[buffer_size];
		return bytes;
	}
	
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

	private void start(final String filename) {
		//判断一下视频是不是正在播放，正在播放的话直接return 
		if(play_thread!=null && play_thread.isAlive()){
//			handler.handle("已经开始了");
			return ;
		}
		if(isDestroyed){
			Log.i("soda","播放界面已经没了");
			return;
		}
		player_surface.setVisibility(View.VISIBLE);
//		togglePd();
		audioMan.requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		error=0;
		play_thread = new Thread(new Runnable() {
			public void run() {
				is_playing = true;
				error=openfile(filename);
				is_playing = false;
				if(error !=0){//打开文件错误
					runOnUiThread(new Runnable() {
						public void run() {
							Log.i("soda","打开文件错误");
//							if(pd.isShowing()){
//								pd.dismiss();
//							}
							stop();
							restartplay();
						}
					});
				}
			}
		});
		play_thread.start();
	}
	
	public void startPlayer(ArrayList<String> rtmpUrlList){
		this.rtmpUrlList = rtmpUrlList;
		String filename = rtmpUrlList.get(0);
		start(filename);
	}
	
	private void restartplay(){
		Log.i("pipi","重新连接");
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mListener.onDisconnected();
			}
		});
	}
	
	public void stop() {
		audioMan.abandonAudioFocus(audioFocusListener);
		nativestop();
		player_surface.setVisibility(View.INVISIBLE);
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
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		Log.i("soda","setupsurface 发生变化"+setupsurface(holder.getSurface(), width, height));
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("soda","surfaceDestroyed");
		nativedisablevidio();
	}
	
//	public void togglePd(){
//		if(pd==null){
//			return;
//		}
//		if(pd.isShowing()){
//			pd.dismiss();
//		}else{
//			pd.show();
//		}
//	}
	
	@Override
	public void onBackPressed() {
		stop();
		super.onBackPressed();
	}
	
	//===========
	//从Native到java的调用
	//===========
	/**
	 * 视频连接成功的回调
	 */
	public void onNativeConnected(){
		Log.i("soda","视频连接上了");
		mListener.onConnected();
	}
	//**视频异常断开 */
	public void onNativeDisConnected(){
		mListener.onDisconnected();
	}
	
	//**视频正常断开 */
	public void onNativeFinish(){
		mListener.onFinish();
	}

	public native int openfile(String filename);
	public native int setupsurface(Surface surface,int width,int height);
	public native int nativestop();
	public native int nativedisablevidio();
	
	class ReconnectListener implements PlayerListener{
		int retry_time = 0;

		@Override
		public void onConnected() {
			retry_time = 0;
		}

		@Override
		public void onDisconnected() {
			if(retry_time<rtmpUrlList.size()){
				Log.i("test","第"+retry_time+"次重试,地址是"+rtmpUrlList.get(retry_time));
				start(rtmpUrlList.get(retry_time));
			}else{
				Log.i("test","重试"+retry_time+"失败 退出");
				stop();
				retry_time = 0;
				Toast.makeText(MainActivity.this, "视频连接失败", Toast.LENGTH_LONG).show();
			}
			retry_time++;
		}

		@Override
		public void onFinish() {
			
		}
		
	}

}
