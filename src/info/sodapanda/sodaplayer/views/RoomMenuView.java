package info.sodapanda.sodaplayer.views;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.Status;
import info.sodapanda.sodaplayer.events.MenuClked;
import info.sodapanda.sodaplayer.events.ToChatEvent;
import info.sodapanda.sodaplayer.events.ToGiftEvent;
import info.sodapanda.sodaplayer.pojo.LogedUser;
import info.sodapanda.sodaplayer.socket.BusProvider;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;

public class RoomMenuView extends LinearLayout {
	LayoutInflater inflater;
	View rootView;
	TextView room_mem_name;
	TextView chat_btn;
	TextView send_gift_btn;
	TextView shut_up_btn;
	TextView kick_btn;
	Bus bus = BusProvider.getBus();
	

	public RoomMenuView(Context context) {
		super(context);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		inflateView();
	}
	
	private void inflateView(){
		rootView = inflater.inflate(R.layout.qc_room_menu_layout, this, true);
		room_mem_name = (TextView) rootView.findViewById(R.id.room_mem_name);
		chat_btn = (TextView) rootView.findViewById(R.id.chat_btn);
		send_gift_btn = (TextView) rootView.findViewById(R.id.send_gift_btn);
		shut_up_btn = (TextView) rootView.findViewById(R.id.shut_up_btn);
		kick_btn = (TextView) rootView.findViewById(R.id.kick_btn);
		
	}
	
	public void startMenu(final String nickname,final int uid){
		room_mem_name.setText(nickname);
		
		chat_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bus.post(new ToChatEvent(nickname, uid));
				bus.post(new MenuClked());
			}
			
		});
		
		send_gift_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bus.post(new ToGiftEvent(nickname, uid));
				bus.post(new MenuClked());

			}
		});
		//TODO 聊天用户昵称菜单的动作
		
//		shut_up_btn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				bus.post(new MenuClked());
//
//				HttpApi.forbiden("ChatRoomActivity", Status.getRoomInfo()
//						.getArchivesId() + "", LogedUser.getUser_id() + "", uid
//						+ "", nickname, "5", new QcJsonRh() {
//
//					@Override
//					public void onResponse(JSONObject jsonmsg) {
//						String code = jsonmsg.optString("code");
//						String msg = jsonmsg.optString("msg");
//						if(!code.equals("200")){
//							Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
//							return ;
//						}
//						JSONObject data = jsonmsg.optJSONObject("data");
//						int status = data.optInt("status");
//						if(status ==1){//操作成功
//							Toast.makeText(getContext(), "禁言操作成功", Toast.LENGTH_SHORT).show();
//						}else if(status == 0){
//							Toast.makeText(getContext(), "只有主播和房管可使用此权限", Toast.LENGTH_SHORT).show();
//						}else if(status == 2){
//							Toast.makeText(getContext(), "对方防御此操作", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});
//			}
//		});
//		
//		kick_btn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				bus.post(new MenuClked());
//
//				HttpApi.forbiden("ChatRoomActivity", Status.getRoomInfo()
//						.getArchivesId() + "", LogedUser.getUser_id() + "", uid
//						+ "", nickname, "8", new QcJsonRh() {
//
//					@Override
//					public void onResponse(JSONObject response) {
//						String code = response.optString("code");
//						String msg = response.optString("msg");
//						if(!code.equals("200")){
//							Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
//							return;
//						}
//						JSONObject data = response.optJSONObject("data");
//						int status = data.optInt("status");
//						if(status ==1){//操作成功
//							Toast.makeText(getContext(), "禁言操作成功", Toast.LENGTH_SHORT).show();
//						}else if(status == 0){
//							Toast.makeText(getContext(), "只有主播和房管可使用此权限", Toast.LENGTH_SHORT).show();
//						}else if(status == 2){
//							Toast.makeText(getContext(), "对方防御此操作", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});
//			}
//		});
	}

}
