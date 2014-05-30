package info.sodapanda.sodaplayer.views;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.activities.ChatRoomActivity;
import info.sodapanda.sodaplayer.events.MarqueeEvent;
import info.sodapanda.sodaplayer.socket.BusProvider;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

public class TopMarqueeLayout extends RelativeLayout {
	TextView s_nickname;
	TextView d_nickname;
	TextView gift_count;
	TextView gift_name;
	ImageView gift_img;
	
	Queue<MarqueeEvent> queue = new LinkedList<MarqueeEvent>();
	Bus bus;
	Animation mAnim = AnimationUtils.loadAnimation(getContext(),R.anim.playanim);
	
	ChatRoomActivity activity;
	
	public TopMarqueeLayout(Context context) {
		super(context);
		init();
	}

	public TopMarqueeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TopMarqueeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		bus = BusProvider.getBus();
		bus.register(this);
		LayoutInflater.from(getContext()).inflate(R.layout.topmarquee_layout, this,true);
		s_nickname = (TextView) findViewById(R.id.s_nickname);
		d_nickname = (TextView) findViewById(R.id.d_nickname);
		gift_count = (TextView) findViewById(R.id.gift_count);
		gift_name = (TextView) findViewById(R.id.gift_name);
		gift_img = (ImageView) findViewById(R.id.gift_img);
		activity = (ChatRoomActivity) getContext();
		Log.i("dudu","is context chatroom activity " + (getContext() instanceof ChatRoomActivity));
	}
	
	private void changeMarquee(MarqueeEvent e){
		if (mAnim.hasEnded() || !(mAnim.hasStarted())) {

			s_nickname.setText(e.getsNickName());
			d_nickname.setText(e.getdNickName());
			gift_count.setText(e.getGiftCount() + "个");
			gift_name.setText(e.getGiftName());
			Picasso.with(getContext()).load(e.getPic()).into(gift_img);
			activity.showMqrquee();
			startAnimation(mAnim);
		}else{
			queue.offer(e);
		}
	}
	
	@Override
	protected void onAnimationEnd() {
		MarqueeEvent e = queue.poll();
		if(e!=null){
			changeMarquee(e);
		}else{
			activity.hideMarquee();
		}
	}
	
	@Subscribe
	public void reMarqueeEvent(MarqueeEvent e){
		setVisibility(View.VISIBLE);
		changeMarquee(e);
	}
}
