package info.sodapanda.sodaplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
	private FFmpegVideoView player_surface;
	
	int width;
	int height;
	
	Button button_start;
	Button button_stop;
	
	EditText filename;
	
	FrameLayout surface_frame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		filename = (EditText) findViewById(R.id.filename);

		//surfaceView
		Display display = getWindowManager().getDefaultDisplay();
		int screen_width = display.getWidth();
		width=screen_width;
		height = (int) (screen_width * 0.75f);
		player_surface = new FFmpegVideoView(this,new PlayCallback() {
            @Override
            public void onConnecting() {

            }

            @Override
            public void onConnected() {

            }

            @Override
            public void onStop() {

            }
        });
		player_surface.setLayoutParams(new LayoutParams(width, height));
		surface_frame = (FrameLayout) findViewById(R.id.surface_frame);
		surface_frame.addView(player_surface);
		
		button_start = (Button) findViewById(R.id.button_start);
		button_stop = (Button) findViewById(R.id.button_stop);
		
		final ArrayList<String> rtmplist = new ArrayList<String>();
		rtmplist.add("rtmp://115.231.101.160/live1/1/1000001");
		
		final ArrayList<String> rtmplist2 = new ArrayList<String>();
		rtmplist2.add("rtmp://115.231.101.160/live1/testnms");
		button_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String rtmpurl = filename.getText().toString();
				rtmplist.add(0, rtmpurl);
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
