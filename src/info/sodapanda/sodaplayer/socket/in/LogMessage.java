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
import android.text.method.LinkMovementMethod;
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

public class LogMessage extends ViewableMessage {
	private String nickName;
	private int userid;
	private int vip;
	private String carname;
	private String car_image_url;
	private Context context;
	private LayoutInflater inflater;
	private TextView chat_message_item;

	public LogMessage(String nickNmae, int userid,int vip,int msg,String carname,String car_image_url,int vip_hide) {
		this.nickName = nickNmae;
		this.userid = userid;
		this.vip = vip;
		this.carname = carname ;
		this.car_image_url = car_image_url ;
	}

	@Override
	public View getMyView(Context context,ViewGroup parent){
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.chat_msg_item, parent,false);
		chat_message_item = (TextView) itemView.findViewById(R.id.chat_message_item);
		chat_message_item.setText("");
		chat_message_item.setMovementMethod(LinkMovementMethod.getInstance());
		SpannableString vipStr = new SpannableString("vip");
		//VIP图标
		if(vip==1){//vip1
			Drawable d = context.getResources().getDrawable(R.drawable.vip1);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			vipStr.setSpan(new ImageSpan(d), 0, vipStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}else if(vip == 2){//vip2
			Drawable d = context.getResources().getDrawable(R.drawable.vip2);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			vipStr.setSpan(new ImageSpan(d), 0, vipStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		//昵称文字 
		SpannableString nickname_span = new SpannableString(nickName);
		nickname_span.setSpan(new NicknameSpan() {
			
			@Override
			public void onClick(View arg0) {
				if (!LogedUser.isMe(userid)) {
					BusProvider.getBus().post(new ChatClkEvent(nickName, userid));
				}
			}
		}, 0, nickname_span.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		if(vip!=-1){//有VIP
			if(carname!=null){//有车
				chat_message_item.append("欢迎VIP");
				chat_message_item.append(vipStr);
				chat_message_item.append(nickname_span);
				chat_message_item.append("坐着");
				chat_message_item.append(carname);
				chat_message_item.append("进入直播间");
				appendCarImg(car_image_url);
			}else{//没车
				chat_message_item.append("欢迎VIP");
				chat_message_item.append(vipStr);
				chat_message_item.append(nickname_span);
				chat_message_item.append("进入直播间");
			}
		}else{//不是VIP
			if(carname==null){//没车
				chat_message_item.append(nickname_span);
				chat_message_item.append("进入直播间");
			}else{//有车
				chat_message_item.append(nickname_span);
				chat_message_item.append("坐着");
				chat_message_item.append(carname);
				chat_message_item.append("进入直播间");
				appendCarImg(car_image_url);
			}
		}

		return itemView;
	}
	
	private void appendCarImg(String car_img){
		if(car_img==null){
			return;
		}

		String [] allowed={".*"};
		new AsyncHttpClient().get(car_img, null, new BinaryHttpResponseHandler(allowed){
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					ByteArrayInputStream in = new ByteArrayInputStream(arg2);
					Drawable d = Drawable.createFromStream(in, "car_img");
					//让车辆图片的显示一直是70dp 先算出来57DP是多少dp，然后设置给 setBounds
					int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,70, context.getResources().getDisplayMetrics());
//					d.setBounds(0, 0, d.getIntrinsicWidth(),d.getIntrinsicHeight());
					int realwidth = d.getIntrinsicWidth();
					int realheight = d.getIntrinsicHeight();
					int displayHeight = (realheight * px) / realwidth;
					d.setBounds(0, 0, px,displayHeight);
					SpannableString car_img_span = new SpannableString("car_img");
					car_img_span.setSpan(new ImageSpan(d), 0,car_img_span.length(),	Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
					chat_message_item.append(car_img_span);
					Log.i("pipi", "汽车图片下载完了");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	abstract class NicknameSpan extends ClickableSpan{
		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(context.getResources().getColor(R.color.qc_room_font_red));
			ds.setUnderlineText(false);
		}
	}
}
