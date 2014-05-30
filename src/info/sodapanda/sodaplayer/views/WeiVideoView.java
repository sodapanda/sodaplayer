package info.sodapanda.sodaplayer.views;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.Status;
import info.sodapanda.sodaplayer.events.OnstopWeiVideo;
import info.sodapanda.sodaplayer.events.StartPlayEvent;
import info.sodapanda.sodaplayer.events.StopPlayEvent;
import info.sodapanda.sodaplayer.socket.BusProvider;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class WeiVideoView extends RelativeLayout {
	LayoutInflater inflater;
	Display display;
	Activity context;
	ScaleVideoView videoView;
	FrameLayout wei_video_holder;
	Bus bus = BusProvider.getBus();
	View live_indicator;

	public WeiVideoView(Context context) {
		super(context);
		bus.register(this);
		setVisibility(View.GONE);
		this.context = (Activity) context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflateLayout();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		bus.unregister(this);
	}
	
	private void inflateLayout(){
		inflater.inflate(R.layout.wei_video_layout, this, true);
		wei_video_holder = new FrameLayout(context);
		live_indicator = findViewById(R.id.live_indicator);
		display = context.getWindowManager().getDefaultDisplay();
		int screen_width = display.getWidth();
		int videoView_height = (int) (screen_width * 0.75);

        wei_video_holder.setLayoutParams(new FrameLayout.LayoutParams(
                screen_width, videoView_height));

		videoView = new ScaleVideoView(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
        videoView.setLayoutParams(lp);
		
		videoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.setLooping(true);
			}
		});
		
		wei_video_holder.addView(videoView);
        addView(wei_video_holder);
		
		if(Status.getRoomInfo().getIsShowing()){
			live_indicator.setVisibility(View.VISIBLE);
		}
		
		live_indicator.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bus.post(new OnstopWeiVideo());
			}
		});
	}
	
	public void startVideo(String path){
		setVisibility(View.VISIBLE);
		videoView.setVideoPath(path);
		videoView.start();
	}
	
	public void stopVideo(){
		setVisibility(View.GONE);
		videoView.stopPlayback();
	}
	
	@Subscribe
	public void liveStart(StartPlayEvent e){
		live_indicator.setVisibility(View.VISIBLE);
	}
	
	@Subscribe
	public void liveStop(StopPlayEvent e){
		live_indicator.setVisibility(View.GONE);
	}

}
