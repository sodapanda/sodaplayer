package info.sodapanda.sodaplayer.adapters;


import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.Status;
import info.sodapanda.sodaplayer.events.ToChatEvent;
import info.sodapanda.sodaplayer.events.ToGiftEvent;
import info.sodapanda.sodaplayer.pojo.Audience;
import info.sodapanda.sodaplayer.pojo.LogedUser;
import info.sodapanda.sodaplayer.socket.BusProvider;
import info.sodapanda.sodaplayer.utils.LvImgMap;
import info.sodapanda.sodaplayer.utils.RichLvMap;
import info.sodapanda.sodaplayer.utils.VipMap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

public class AudiencAdapter extends BaseAdapter {
	ArrayList<Audience> audList;
	LayoutInflater inflater;
	Bus bus;
	Activity activity;
	Timer timer;

	public AudiencAdapter(Activity activity) {
		audList = new ArrayList<Audience>();
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		bus = BusProvider.getBus();
		this.activity = activity;
		updateList();//开始轮询更新在线列表
	}

	static class ViewHolder {
		TextView nicknameTextView;
		TextView audienceid;
		ImageView vip_icon;
		View mem_action_layout;
		ImageView r_lv;
		View shutup_view;
		View kickout_view;
		View chat_view;
		View gift_view;
		View user_info;
		ImageView avatar;
		ImageView capacity_icon;
	}

	@Override
	public int getCount() {
		return audList.size();
	}

	@Override
	public Object getItem(int position) {
		return audList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Audience thisAud = audList.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.qc_room_mem_item_user, null);
			viewHolder = new ViewHolder();
			viewHolder.audienceid = (TextView) convertView.findViewById(R.id.userid);
			viewHolder.vip_icon = (ImageView) convertView.findViewById(R.id.vip_icon);
			viewHolder.nicknameTextView = (TextView) convertView.findViewById(R.id.name);
			viewHolder.mem_action_layout = convertView.findViewById(R.id.mem_action_layout);
			viewHolder.r_lv = (ImageView) convertView.findViewById(R.id.r_lv);
			viewHolder.shutup_view = convertView.findViewById(R.id.shutup_view);
			viewHolder.kickout_view = convertView.findViewById(R.id.kickout_view);
			viewHolder.chat_view = convertView.findViewById(R.id.chat_view);
			viewHolder.gift_view = convertView.findViewById(R.id.gift_view);
			viewHolder.user_info = convertView.findViewById(R.id.user_info);
			viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			viewHolder.capacity_icon = (ImageView) convertView.findViewById(R.id.capacity_icon);
			convertView.setTag(R.id.viewholderid,viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.viewholderid);
		}
		convertView.setTag(R.id.uidid,thisAud.getUid());
		viewHolder.audienceid.setText(thisAud.getUid()+"");
		viewHolder.nicknameTextView.setText(thisAud.getNk());
		Picasso.with(activity).load(thisAud.getAvatar()).into(viewHolder.avatar);
		if(thisAud.getPk()==3){//如果是主播显示主播等级
			viewHolder.r_lv.setImageResource(new LvImgMap().getLvRes(thisAud.getRk()));
		}else{
			viewHolder.r_lv.setImageResource(new RichLvMap().getRichLevelRes(thisAud.getRk()));
		}
		viewHolder.mem_action_layout.setVisibility(View.GONE);

		if (thisAud.getVip() != -1) {
			viewHolder.vip_icon.setVisibility(View.VISIBLE);
			viewHolder.vip_icon.setImageResource(new VipMap().getVipRes(thisAud.getVip()));
		}else{
			viewHolder.vip_icon.setVisibility(View.GONE);
		}
		
		if(thisAud.getPk()==3){
			viewHolder.capacity_icon.setImageResource(R.drawable.zhubo);
			viewHolder.capacity_icon.setVisibility(View.VISIBLE);
		}else if(thisAud.getPk() == 2){
			viewHolder.capacity_icon.setImageResource(R.drawable.fangguan);
			viewHolder.capacity_icon.setVisibility(View.VISIBLE);
		}else if(thisAud.getPk() == 4){
			viewHolder.capacity_icon.setImageResource(R.drawable.zongguan);
			viewHolder.capacity_icon.setVisibility(View.VISIBLE);
		}else{
			viewHolder.capacity_icon.setVisibility(View.GONE);
		}

		viewHolder.shutup_view.setOnClickListener(new OnClickListener() {//禁言

			@Override
			public void onClick(View v) {
				if(!LogedUser.isLoged()){
					Toast.makeText(activity, "请先登录", Toast.LENGTH_LONG).show();
					return;
				}
//				HttpApi.forbiden("ChatRoomActivity", Status.getRoomInfo()
//						.getArchivesId() + "", LogedUser.getUser_id() + "", thisAud.getUid()
//						+ "", thisAud.getNk(), "5", new QcJsonRh() {
//
//					@Override
//					public void onResponse(JSONObject jsonmsg) {
//						String code = jsonmsg.optString("code");
//						String msg = jsonmsg.optString("msg");
//						if(!code.equals("200")){
//							Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
//							return ;
//						}
//						JSONObject data = jsonmsg.optJSONObject("data");
//						int status = data.optInt("status");
//						if(status ==1){//操作成功
//							Toast.makeText(activity, "禁言操作成功", Toast.LENGTH_SHORT).show();
//						}else if(status == 0){
//							Toast.makeText(activity, "只有主播和房管可使用此权限", Toast.LENGTH_SHORT).show();
//						}else if(status == 2){
//							Toast.makeText(activity, "对方防御此操作", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});
			}
		});

		viewHolder.kickout_view.setOnClickListener(new OnClickListener() {//踢出 

			@Override
			public void onClick(View v) {
				if(!LogedUser.isLoged()){
					Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
					return;
				}
//				HttpApi.forbiden("ChatRoomActivity", Status.getRoomInfo()
//						.getArchivesId() + "", LogedUser.getUser_id() + "", thisAud.getUid()
//						+ "", thisAud.getNk(), "8", new QcJsonRh() {
//
//					@Override
//					public void onResponse(JSONObject response) {
//						String code = response.optString("code");
//						String msg = response.optString("msg");
//						if(!code.equals("200")){
//							Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
//							return;
//						}
//						JSONObject data = response.optJSONObject("data");
//						int status = data.optInt("status");
//						if(status ==1){//操作成功
//							Toast.makeText(activity, "禁言操作成功", Toast.LENGTH_SHORT).show();
//						}else if(status == 0){
//							Toast.makeText(activity, "只有主播和房管可使用此权限", Toast.LENGTH_SHORT).show();
//						}else if(status == 2){
//							Toast.makeText(activity, "对方防御此操作", Toast.LENGTH_SHORT).show();
//						}
//					}
//				});
			}
		});

		viewHolder.chat_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bus.post(new ToChatEvent(thisAud.getNk(), thisAud.getUid()));
			}
		});

		viewHolder.gift_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bus.post(new ToGiftEvent(thisAud.getNk(), thisAud.getUid()));
			}
		});
		
		viewHolder.user_info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(DuduApplication.getInstance(), NameCardActivity.class);
//				intent.putExtra("userid", thisAud.getUid());
//				intent.putExtra("from_chatroom", true);
//				activity.startActivity(intent);
//				UserData user = new UserData();
//				user.setUid(thisAud.getUid());
//				Intent intent = new Intent(activity, UserPageActivity.class);
//				intent.putExtra("UserData", user);
//				activity.startActivity(intent);
			}
		});

		return convertView;
	}
	
	private void updateList(){
		TimerTask task = new TimerTask() {
//			int total=0;
			@Override
			public void run() {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					
					@Override
					public void run() {
						if(Status.getRoomInfo()==null){
							return;
						}
						
//						HttpApi.getArchivesUser("ChatRoomActivity", Status.getRoomInfo().getArchivesId() + "", "1", "50", new QcStringRh() {
//
//							@Override
//							public void onResponse(String responsestr) {
//								Log.i("test", "观众 " + responsestr);
//								try {
//									JSONObject response = new JSONObject(responsestr);
//									String msg = response.getString("msg");
//									if(msg.equals("success")){
//										audList.clear();//清空房间内用户列表
//										AudiencAdapter.this.notifyDataSetChanged();
//										JSONObject data = response.getJSONObject("data");
////										total = data.getInt("total");
//										JSONArray dotey = data.getJSONArray("dotey");
//										addToAudList(dotey);
//										//管理员不用重复显示
//										JSONArray user = data.getJSONArray("user");
//										addToAudList(user);
//									}
//								} catch (JSONException e) {
//								}
//								activity.runOnUiThread(new Runnable() {
//									
//									@Override
//									public void run() {
//										AudiencAdapter.this.notifyDataSetChanged();//通知数据源变化
////										bus.post(new ChangeAudiencMapEvent(total));//通知人数变化
//									}
//								});
//							}
//						});
					}
				});
			}
		};
		
		timer = new Timer();
		timer.schedule(task, 0, 15000);
	}
	
	public void stopTimer(){
		timer.cancel();
	}
	
	private void addToAudList(JSONArray audArray){
		for (int i =0 ;i<audArray.length();i++){
			int vip=-1;
			try {
				JSONObject thisobj = audArray.getJSONObject(i);
				int uid = thisobj.getInt("uid");
				String avatar = thisobj.getString("avatar");
				String nk = thisobj.getString("nk");
				int rk = thisobj.getInt("rk");
				int pk = thisobj.getInt("pk");
				if(thisobj.has("vip")){
					vip = thisobj.getInt("vip");
				}
				Audience aud = new Audience(uid, nk, rk, pk, vip,avatar);
				audList.add(aud);
				notifyDataSetChanged();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
