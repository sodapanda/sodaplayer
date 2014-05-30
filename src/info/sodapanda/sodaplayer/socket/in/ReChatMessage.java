package info.sodapanda.sodaplayer.socket.in;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.Sodaplayer;
import info.sodapanda.sodaplayer.events.ChatClkEvent;
import info.sodapanda.sodaplayer.pojo.LogedUser;
import info.sodapanda.sodaplayer.socket.BusProvider;
import info.sodapanda.sodaplayer.utils.SmileyParser;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;

public class ReChatMessage extends ViewableMessage{
	String nickname;
	String content;
	int chatType;
	int dUserID;
	int s_vip;
	String dNickName;
	int sUserID;
	int sUserNum;
	String sNickName;
	String time;
	
	Bus bus;
	Context context;
	
	public String getNickname() {
		return nickname;
	}

	public String getContent() {
		return content;
	}

	public int getChatType() {
		return chatType;
	}

	public int getdUserID() {
		return dUserID;
	}

	public String getdNickName() {
		return dNickName;
	}

	public int getsUserID() {
		return sUserID;
	}

	public int getsUserNum() {
		return sUserNum;
	}

	public String getsNickName() {
		return sNickName;
	}

	public String getTime() {
		return time;
	}
	public int getVip(){
		return s_vip;
	}


	public ReChatMessage(String nickname, String content, String chatType_str,
			int dUserID, String dNickName, int sUserID, int sUserNum,
			String sNickName, String time,int vip) {
		super();
		this.nickname = nickname;
		this.content = content;
		if(chatType_str.equals("common")){
			this.chatType =0;
			if(!(dNickName.equals(""))){
				this.chatType = 1;
			}
		}
		if(chatType_str.equals("private")){
			this.chatType = 2;
		}
		this.dUserID = dUserID;
		this.dNickName = dNickName;
		this.sUserID = sUserID;
		this.sUserNum = sUserNum;
		this.sNickName = sNickName;
		this.time = time;
		this.s_vip = vip;
		bus = BusProvider.getBus();
	}

	@Override
	public View getMyView(Context context,ViewGroup parent){
		this.context = context;
		SmileyParser.init(Sodaplayer.getAppCxt());
		SmileyParser sparser = SmileyParser.getInstance();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View chatItemView = inflater.inflate(R.layout.chat_msg_item, parent, false);
		TextView chat_message_item = (TextView) chatItemView.findViewById(R.id.chat_message_item);
		chat_message_item.setMovementMethod(LinkMovementMethod.getInstance());
		
		if(s_vip==1){//vip1
			SpannableString vip = new SpannableString("vip");
			Drawable d = context.getResources().getDrawable(R.drawable.vip1);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			vip.setSpan(new ImageSpan(d), 0, vip.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			chat_message_item.append(vip);
		}else if(s_vip == 2){//vip2
			SpannableString vip = new SpannableString("vip");
			Drawable d = context.getResources().getDrawable(R.drawable.vip2);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			vip.setSpan(new ImageSpan(d), 0, vip.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			chat_message_item.append(vip);
		}
		
		if(LogedUser.isLoged() && sUserID == LogedUser.getUser_id()){
			sNickName = "我";
		}
		SpannableString sp_snickname = new SpannableString(sNickName);
		SpannableString sp_dnickname=null;
		
		sp_snickname.setSpan(new NicknameSpan() {
			
			@Override
			public void onClick(View widget) {
				if (sUserID != LogedUser.getUser_id()) {
					bus.post(new ChatClkEvent(sNickName, sUserID));
				}
			}
		}, 0,sp_snickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		if (chatType == 0) {// 无指向聊天
			chat_message_item.append(sp_snickname);
			chat_message_item.append("说:");
			chat_message_item.append(sparser.addSmileySpans(content));
		} else if (chatType == 1) {// 有向公开
			if(LogedUser.isMe(dUserID)){
				dNickName = "我";
			}
			sp_dnickname = new SpannableString(dNickName);
			sp_dnickname.setSpan(new NicknameSpan() {
				
				@Override
				public void onClick(View v) {
					if (!LogedUser.isMe(dUserID) ) {
						bus.post(new ChatClkEvent(dNickName, dUserID));
					}
				}
			}, 0, dNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			chat_message_item.append(sp_snickname);
			chat_message_item.append("对");
			chat_message_item.append(sp_dnickname);
			chat_message_item.append("说:");
			chat_message_item.append(sparser.addSmileySpans(content));
		} else if (chatType == 2 || chatType == 3) {// 有向私有
			chat_message_item.append(sp_snickname);
			chat_message_item.append("对");
			if(LogedUser.isMe(dUserID) ){
				dNickName = "我";
			}
			sp_dnickname = new SpannableString(dNickName);
			sp_dnickname.setSpan(new NicknameSpan() {
				
				@Override
				public void onClick(View v) {
					if (!LogedUser.isMe(dUserID)) {
						bus.post(new ChatClkEvent(dNickName, dUserID));
					}
				}
			}, 0, dNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			chat_message_item.append(sp_dnickname);
			chat_message_item.append("说:");
			chat_message_item.append(sparser.addSmileySpans(content));
		}
		return chatItemView;
	}
	
	abstract class NicknameSpan extends ClickableSpan{
		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(context.getResources().getColor(R.color.qc_room_font_red));
			ds.setUnderlineText(false);
		}
	}
}
