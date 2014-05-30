package info.sodapanda.sodaplayer.views;

import info.sodapanda.sodaplayer.events.AddPublicItemEvent;
import info.sodapanda.sodaplayer.socket.BusProvider;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class PubChatContentView extends LinearLayout {

	public PubChatContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBus();
	}

	public PubChatContentView(Context context) {
		super(context);
		initBus();
	}

	Bus bus = BusProvider.getBus();

	private void initBus() {
		bus.register(this);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		bus.unregister(this);
		Log.i("test","bus注销");
	}

	@Subscribe
	public void getPubChatEvent(AddPublicItemEvent event) {
		if(getChildCount()>=20){
			removeViewAt(0);
		}
		
		addView(event.getMessage().getMyView(getContext(), this));
		Log.i("test","收到消息");
		final ScrollView parent = (ScrollView) getParent();
		parent.post(new Runnable() {
			
			@Override
			public void run() {
				parent.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

}
