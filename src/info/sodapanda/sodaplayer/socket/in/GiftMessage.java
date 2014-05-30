package info.sodapanda.sodaplayer.socket.in;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.events.ChatClkEvent;
import info.sodapanda.sodaplayer.pojo.LogedUser;
import info.sodapanda.sodaplayer.socket.BusProvider;

import java.io.ByteArrayInputStream;

import org.apache.http.Header;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.squareup.otto.Bus;


public class GiftMessage extends ViewableMessage {
	String sNickName;
	String dNickName;
	String pName;
	String count;
	int suserid;
	int duserid;
	Context context;
	String giftUrl;
	private View msgview;
	boolean is_super_gift;
	Bus bus;
	TextView chat_item;

	public GiftMessage(String sNickName, String dNickName, String pName,String count, String giftUrl,int sUserid,int dUserid,boolean is_super_gift) {
		super();
		this.sNickName = sNickName;
		this.dNickName = dNickName;
		this.pName = pName;
		this.count = count;
		this.giftUrl = giftUrl;
		this.suserid= sUserid;
		this.duserid = dUserid;
		this.is_super_gift = is_super_gift;
		bus = BusProvider.getBus();
	}

	@Override
	public View getMyView(Context context,ViewGroup parent) {
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(LogedUser.isMe(suserid)){
			sNickName = "我";
		}
	
		if (LogedUser.isMe(duserid)) {
			dNickName = "我";
		}
		msgview = inflater.inflate(R.layout.chat_msg_item, parent,false);
		chat_item = (TextView) msgview.findViewById(R.id.chat_message_item);
		
		if(is_super_gift){
			chat_item.append("[超礼]");
		}
		//来源昵称
		SpannableString snickname_sp = new SpannableString(sNickName);
		snickname_sp.setSpan(new NicknameSpan() {
			
			@Override
			public void onClick(View widget) {
				if (!LogedUser.isMe(suserid)) {
					bus.post(new ChatClkEvent(sNickName, suserid));
				}
			}
		}, 0, snickname_sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		chat_item.append(snickname_sp);
		chat_item.append("送给");
		
		//接收者昵称
		SpannableString dnickname_sp = new SpannableString(dNickName);
		dnickname_sp.setSpan(new NicknameSpan() {
			
			@Override
			public void onClick(View widget) {
				if (!LogedUser.isMe(duserid)) {
					bus.post(new ChatClkEvent(dNickName, duserid));
				}
			}
		}, 0, dnickname_sp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		chat_item.append(dnickname_sp);
		chat_item.append(count+"个"+pName);
		
		//礼物图片
		String [] allowed={".*"};
		new AsyncHttpClient().get(giftUrl, null, new BinaryHttpResponseHandler(allowed){
			
//			@Override
//			public void onSuccess(byte[] binaryData) {
//				ByteArrayInputStream in = new ByteArrayInputStream(binaryData);
//				Drawable d = Drawable.createFromStream(in, "car_img");
//				d.setBounds(0, 0, 50, 50);
//
//				SpannableString car_img_span = new SpannableString("car_img");
//				car_img_span.setSpan(new ImageSpan(d), 0, car_img_span.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//				chat_item.append(car_img_span);
//				Log.i("pipi","礼物图片下载完了");
//			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					ByteArrayInputStream in = new ByteArrayInputStream(arg2);
					Drawable d = Drawable.createFromStream(in, "car_img");
					
					int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,70, GiftMessage.this.context.getResources().getDisplayMetrics());
					int realwidth = d.getIntrinsicWidth();
					int realheight = d.getIntrinsicHeight();
					int displayHeight = (realheight * px) / realwidth;
					d.setBounds(0, 0, px,displayHeight);
//					d.setBounds(0, 0, 50, 50);
					SpannableString car_img_span = new SpannableString("car_img");
					car_img_span.setSpan(new ImageSpan(d), 0,car_img_span.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
					chat_item.append(car_img_span);
					Log.i("pipi", "礼物图片下载完了");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		return msgview;
	}
	
	abstract class NicknameSpan extends ClickableSpan{
		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(context.getResources().getColor(R.color.qc_room_font_red));
			ds.setUnderlineText(false);
		}
	}
}
