package info.sodapanda.sodaplayer.socket;

import info.sodapanda.sodaplayer.Status;
import info.sodapanda.sodaplayer.events.AddPrivateItemEvent;
import info.sodapanda.sodaplayer.events.AddPublicItemEvent;
import info.sodapanda.sodaplayer.events.BeForbidEvent;
import info.sodapanda.sodaplayer.events.MarqueeEvent;
import info.sodapanda.sodaplayer.events.MuchGiftEvent;
import info.sodapanda.sodaplayer.events.StartPlayEvent;
import info.sodapanda.sodaplayer.events.StopPlayEvent;
import info.sodapanda.sodaplayer.pojo.LogedUser;
import info.sodapanda.sodaplayer.socket.in.AddAdminMsg;
import info.sodapanda.sodaplayer.socket.in.DelAdminMsg;
import info.sodapanda.sodaplayer.socket.in.DisForbidMessage;
import info.sodapanda.sodaplayer.socket.in.ForbidMessage;
import info.sodapanda.sodaplayer.socket.in.GiftMessage;
import info.sodapanda.sodaplayer.socket.in.KickMessage;
import info.sodapanda.sodaplayer.socket.in.LogMessage;
import info.sodapanda.sodaplayer.socket.in.NoticeMsg;
import info.sodapanda.sodaplayer.socket.in.ReChatMessage;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.wire.Wire;

public class MessageParser {
	short msgid;
	Handler handler;
	Bus bus;
	Wire wire;
	strAvg message;
	JSONObject jsonMsg;

	public MessageParser() {
		handler = new Handler(Looper.getMainLooper());
		bus = BusProvider.getBus();
		wire = new Wire();
	}

	/**
	 * 对单个包进行去标志位
	 * 
	 * @param msgByte
	 * @return 返回包内文本内容utf-8
	 */
	private strAvg diswrap(ByteBuffer msg_buff, int package_len) {
		strAvg message = null;
		short version = msg_buff.getShort();
		msgid = msg_buff.getShort();
		int body_len = msg_buff.getInt();
		Log.i("pipi", "收到消息类型 " + msgid);
		Log.i("pipi", "收到消息长度 " + body_len);

		if (body_len + 8 != package_len) {
			return null;
		}
		
		try {
			byte[] body_bytes = new byte[body_len];
			msg_buff.get(body_bytes);
			message = wire.parseFrom(body_bytes, strAvg.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}

	public void parseMessage(ByteBuffer msg_buff, int len) {
		// 将数据打包成JsonObject处理后通过handler发送
		message = diswrap(msg_buff, len);
		if (message == null) {
			Log.i("pipi", "消息未能成功创建");
			return;
		}
		createMsg();
	}

	private void createMsg() {
		try {
			if (msgid == 1102) {// 聊天信息
				String json_userinfo_string = null;
				String type = null;
				try {
					json_userinfo_string = new String(message.strs.get(3).toByteArray(), "UTF-8");
					type = new String(message.strs.get(2).toByteArray(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				JSONObject json_userinfo = new JSONObject(json_userinfo_string);
				Log.i("pipi", "收到聊天消息 type" + type + "\n内容 " + json_userinfo.toString());
				if (type.equals("common") || type.equals("private")) {
					String nickname = json_userinfo.getString("to_nickname");
					String content = json_userinfo.getString("content");
					int dUserID = json_userinfo.getInt("to_uid");
					String dNickName = json_userinfo.getString("to_nickname");
					int sUserID = json_userinfo.getInt("from_uid");
					String sNickName = json_userinfo.getString("from_nickname");
					JSONObject from_json = json_userinfo.optJSONObject("from_json");
					int s_vip = -1;
					if (from_json != null && from_json.has("vip")) {// 如果有vip的话
						JSONObject vip = from_json.getJSONObject("vip");
						s_vip = vip.getInt("t");
						Log.i("pipi", "说话的人的vip等级 " + s_vip);
					}
					ReChatMessage rechatMessage = new ReChatMessage(nickname, content, type, dUserID, dNickName,
							sUserID, 0, sNickName, "", s_vip);
					// activate(rechatMessage);
					if (type.equals("common")) {
						final AddPublicItemEvent event = new AddPublicItemEvent(rechatMessage);
						// bus.post(event);
						// postOnUiThread(event);
						handler.post(new Runnable() {

							@Override
							public void run() {
								bus.post(event);
							}
						});
					}
					if (type.equals("private")) {
						final AddPrivateItemEvent event = new AddPrivateItemEvent(rechatMessage);
						// bus.post(event);
						handler.post(new Runnable() {

							@Override
							public void run() {
								bus.post(event);
							}
						});
					}

				} else if (type.equals("localroom")) {// 房间内通知
					String local_type = json_userinfo.getString("type");
					if (local_type.equals("stop_live")) {// 关闭消息
						handler.post(new Runnable() {

							@Override
							public void run() {
								bus.post(new StopPlayEvent());
							}
						});
					} else if (local_type.equals("start_live")) {// 开播消息
						handler.post(new Runnable() {

							@Override
							public void run() {
								bus.post(new StartPlayEvent());
							}
						});
					} else if (local_type.equals("upgrade_user")) {

					}
				} else if (type.equals("boradcast")) {// 全服广播
				}
			} else if (msgid == 1108) {// 送礼消息
				String json_content_str = "";
				try {
					json_content_str = new String(message.strs.get(2).toByteArray(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				Log.i("pipi", "礼物消息 \n" + json_content_str);
				JSONObject json_content = new JSONObject(json_content_str);
				final String sNickName = json_content.getString("nickname");// 赠送者昵称
				final String dNickName = json_content.getString("to_nickname");
				final String pName = json_content.getString("zh_description");// 礼物名称
				String count = json_content.getString("gift_num");
				final int count_int = Integer.valueOf(count);
				String sUserid = json_content.getString("uid");
				String dUserid = json_content.getString("to_uid");
				String dotey_uid = json_content.getString("dotey_uid");// 直播间主播ID
				int pipiegg = json_content.getInt("pipiegg");
				int suerid_int = Integer.parseInt(sUserid);
				int duserid_int = Integer.parseInt(dUserid);
//				final String picture = HttpApi.DOMAIN+"images/gift/" + json_content.getString("picture");
				boolean is_super_gift = (pipiegg > 8000) ? true : false;
//				GiftMessage giftMsg = new GiftMessage(sNickName, dNickName, pName, count, picture, suerid_int,
//						duserid_int, is_super_gift);

				// 送礼发生在所在直播间
				if (dotey_uid.equals(Status.getRoomInfo().getuserid() + "")) {
					// activate(giftMsg);
//					final AddPublicItemEvent event = new AddPublicItemEvent(giftMsg);
					// bus.post(event);
					handler.post(new Runnable() {

						@Override
						public void run() {
//							bus.post(event);
						}
					});
					if (count_int == 50 || count_int == 99 || count_int == 100 || count_int == 300 || count_int == 520
							|| count_int == 999 || count_int == 1314 || count_int == 3344) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								Log.i("pipi", "大量礼物事件");
//								bus.post(new MuchGiftEvent(count_int, picture));
							}
						});
					}
					// 没有发生在所在直播间
				} else if (pipiegg > 8000) {
//					final AddPublicItemEvent event = new AddPublicItemEvent(giftMsg);
					// bus.post(event);
					handler.post(new Runnable() {

						@Override
						public void run() {
//							bus.post(event);
						}
					});
				}

				if (json_content.has("gift_type") && json_content.getString("gift_type").equals("truckGifts")) {// 如果有gift_type
																												// 是跑道
					handler.post(new Runnable() {

						@Override
						public void run() {
//							bus.post(new MarqueeEvent(pName, count_int, dNickName, sNickName, picture));
						}
					});
				}

				// 中奖消息
				if (json_content.has("gift_type") && json_content.getString("gift_type").equals("luckGifts")) {
					JSONArray gift_award = json_content.getJSONArray("gift_award");
					if (gift_award.length() > 0) {// 产生了中奖消息
						for (int i = 0; i < gift_award.length(); i++) {
							JSONObject this_award = gift_award.getJSONObject(i);
							String award = this_award.getString("award");// 几倍
							int zh_name = this_award.getInt("zh_name");// 共多少奖励

							boolean isShow = false;// 是不是需要显示
							if (dotey_uid.equals(Status.getRoomInfo().getuserid() + "") || award.equals("500")) {
								isShow = true;
							}
							if (isShow) {
								final AddPublicItemEvent event = new AddPublicItemEvent(new NoticeMsg("[中奖]恭喜"
										+ sNickName + "送出" + pName + "后 喜中" + award + "倍大奖 共" + zh_name + "个橙币"));
								// bus.post(event);
								handler.post(new Runnable() {

									@Override
									public void run() {
										bus.post(event);
									}
								});
							}
						}
					}
				}

			} else if (msgid == 1618) {
				try {
					String num = new String(message.strs.get(0).toByteArray(), "UTF-8");
					String uid_n = new String(message.strs.get(1).toByteArray(), "UTF-8");
					String json_info = new String(message.strs.get(2).toByteArray(), "UTF-8");
					Log.i("pipi", "用户列表\nnum=" + num + "\nuid_n=" + uid_n + "\njsoninfo=" + json_info + "\n");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else if (msgid == 1616) {// 欢迎消息解析
				try {
					String uid = new String(message.strs.get(0).toByteArray(), "UTF-8");
					String json_info = new String(message.strs.get(1).toByteArray(), "UTF-8");
					String status = new String(message.strs.get(2).toByteArray(), "UTF-8");
					Log.i("pipi", "1616消息 " + json_info + " " + status + " uid " + uid);
					JSONObject userinfojson = new JSONObject(json_info);
					String nickNmae = userinfojson.getString("nk");
					Integer uid_int = Integer.valueOf(uid);
					Integer stauts_int = Integer.valueOf(status);

					int vip_type = -1;
					int vip_hide = -1;
					if (userinfojson.has("vip")) {
						JSONObject vip = userinfojson.getJSONObject("vip");
						vip_type = vip.getInt("t");
						vip_hide = vip.getInt("h");
					}

					String car_name = null;
					String car_img = null;
					if (userinfojson.has("car")) {
						JSONObject car = userinfojson.getJSONObject("car");
						car_name = car.getString("n");
//						car_img = HttpApi.DOMAIN + "images" + car.getString("img");
					}

					LogMessage logmsg = new LogMessage(nickNmae, uid_int, vip_type, stauts_int, car_name, car_img,
							vip_hide);
					final AddPublicItemEvent event = new AddPublicItemEvent(logmsg);
					if (stauts_int != 2 && vip_hide != 1) {// 2是不显示登录欢迎消息
															// vip_hide=1隐藏
					// activate(logmsg);
						handler.post(new Runnable() {

							@Override
							public void run() {
								bus.post(event);
							}
						});
					}

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else if (msgid == 1107) {// 对用户的特权操作
				String to_uid = null;
				String nickname = null;
				String to_nickname = null;
				int type = -1;
				try {
					to_uid = new String(message.strs.get(4).toByteArray(), "UTF-8");
					nickname = new String(message.strs.get(3).toByteArray(), "UTF-8");
					to_nickname = new String(message.strs.get(5).toByteArray(), "UTF-8");
					type = Integer.valueOf(new String(message.strs.get(6).toByteArray(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				switch (type) {
				case 5:// 禁言
					ForbidMessage msg = new ForbidMessage(to_nickname,nickname);
					final AddPublicItemEvent event = new AddPublicItemEvent(msg);
					handler.post(new Runnable() {

						@Override
						public void run() {
							bus.post(event);
						}
					});
					break;
				case 4:// 解禁发言
					DisForbidMessage disMsg = new DisForbidMessage(to_nickname, nickname);
					final AddPublicItemEvent disevent = new AddPublicItemEvent(disMsg);
					handler.post(new Runnable() {

						@Override
						public void run() {
							bus.post(disevent);
						}
					});
					break;
				case 8:// 有人被踢
				{
					if (LogedUser.isMe(Integer.parseInt(to_uid))) {// 自己被踢出
						handler.post(new Runnable() {

							@Override
							public void run() {
								bus.post(new BeForbidEvent());
							}
						});
					} else {// 不是自己被踢
					// activate(new KickMessage(to_nickname, nickname,
					// Integer.valueOf(to_uid)));
						KickMessage kickMsg = new KickMessage(to_nickname, nickname, Integer.valueOf(to_uid));
						final AddPublicItemEvent kickEvent = new AddPublicItemEvent(kickMsg);
						handler.post(new Runnable() {

							@Override
							public void run() {
								bus.post(kickEvent);
							}
						});
					}
				}
					break;
				case 7:// 有人被设置房管
					// activate(new AddAdminMsg(nickname, to_nickname));
					final AddAdminMsg addAdminMsg = new AddAdminMsg(nickname, to_nickname);
					handler.post(new Runnable() {

						@Override
						public void run() {
							bus.post(addAdminMsg);
						}
					});
					break;
				case 6:
					// activate(new DelAdminMsg(nickname, to_nickname));
					final DelAdminMsg delMsg = new DelAdminMsg(nickname, to_nickname);
					handler.post(new Runnable() {

						@Override
						public void run() {
							bus.post(delMsg);
						}
					});
					break;
				default:
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
