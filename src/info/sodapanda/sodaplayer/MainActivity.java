package info.sodapanda.sodaplayer;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends FragmentActivity {
	private FFmpegVideoView player_surface;
	
	int width;
	int height;
	
	Button button_start;
	Button button_stop;
	
	FrameLayout surface_frame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//surfaceView
		Display display = getWindowManager().getDefaultDisplay();
		int screen_width = display.getWidth();
		width=screen_width;
		height = (int) (screen_width * 0.75f);
		player_surface = new FFmpegVideoView(this);
		player_surface.setLayoutParams(new LayoutParams(width, height));
		surface_frame = (FrameLayout) findViewById(R.id.surface_frame);
		surface_frame.addView(player_surface);
		
		button_start = (Button) findViewById(R.id.button_start);
		button_stop = (Button) findViewById(R.id.button_stop);
		
		final ArrayList<String> rtmplist = new ArrayList<String>();
		rtmplist.add("rtmp://115.231.101.165/live4/2576");
		button_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				player_surface.startPlayer(rtmplist);
			}
		});
		
		button_stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				player_surface.stop();
			}
		});
	}

	@Override
	public void onBackPressed() {
		player_surface.stop();
		super.onBackPressed();
	}
	
	static {
		System.loadLibrary("ffmpeg");
		System.loadLibrary("main");
	}
}
