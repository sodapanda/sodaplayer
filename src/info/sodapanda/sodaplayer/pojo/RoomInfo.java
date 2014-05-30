package info.sodapanda.sodaplayer.pojo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomInfo {
	String public_notice;
	String public_notice_url;
	String private_notice;
	String private_notice_url;
	int userid;
	int  archives_id;
	String nickName;
	boolean is_showing;
	String next_time;
	String title;
	String live_date;
	String end_time;
	
	/**
	 * 聊天服务器地址
	 */
	String domain;
	
	/**
	 * 聊天服务器端口
	 */
	int data_port;
	
	/**
	 * rtmp视频地址
	 */
	String rtmp_url;
	ArrayList<String> rtmpUrlList = new ArrayList<String>();
	
	
	ArrayList<RoomGiftRenkItem> roomgiftList;
	ArrayList<RoomRenkItem> roomRenkListThis;
	
	public boolean getIsShowing(){
		return is_showing;
	}
	
	
	public ArrayList<String> getRtmpRrlList(){
		return rtmpUrlList;
	}
	
	public void setIsShowing(boolean isShowing){
		this.is_showing = isShowing;
	}
	
	public String getNextTime(){
		return next_time;
	}
	
	public String getRtmpUrl(){
		return rtmp_url;
	}
	
	public int getuserid(){
		return userid;
	}
	public int getArchivesId(){
		return archives_id;
	}
	public String getNickname(){
		return nickName;
	}
	public String getPublic_notice() {
		return public_notice;
	}

	public String getPublic_notice_url() {
		return public_notice_url;
	}
	
	public String getDomain(){
		return domain;
	}

	public String getTitle(){
		return this.title;
	}
	
	public String getlive_date(){
		return live_date;
	}
	
	public String getPrivate_notice() {
		return private_notice;
	}

	public String getPrivate_notice_url() {
		return private_notice_url;
	}

	public ArrayList<RoomGiftRenkItem> getRoomgiftList() {
		return roomgiftList;
	}

	public ArrayList<RoomRenkItem> getRoomRenkListThis() {
		return roomRenkListThis;
	}

	public ArrayList<RoomRenkItem> getRoomRenkListWeek() {
		return roomRenkListWeek;
	}

	public ArrayList<RoomRenkItem> getRoomRenkListMonth() {
		return roomRenkListMonth;
	}

	public ArrayList<RoomRenkItem> getRoomRenkListSuper() {
		return roomRenkListSuper;
	}
	
	public int getPort(){
		return data_port;
	}


	ArrayList<RoomRenkItem> roomRenkListWeek;
	ArrayList<RoomRenkItem> roomRenkListMonth;
	ArrayList<RoomRenkItem> roomRenkListSuper;

	public RoomInfo(String public_notice, String public_notice_url,
			String private_notice, String private_notice_url,
			JSONArray giftRenkArray, JSONArray renklistArray, int userid,
			int archvies_id, String nickName, boolean is_showing,
			String next_time, String domain, int data_port, String rtmp_url,
			String title, String live_date, String end_time,ArrayList<String> rtmpUrlList) {
		this.public_notice = public_notice;
		this.data_port = data_port;
		this.rtmpUrlList = rtmpUrlList;
		this.rtmp_url = rtmp_url;
		this.public_notice_url = public_notice_url;
		this.private_notice = private_notice;
		this.private_notice_url = private_notice_url;
		this.nickName = nickName;
		this.userid = userid;
		this.archives_id = archvies_id;
		this.is_showing = is_showing;
		this.next_time =next_time;
		this.domain = domain;
		this.title = title;
		this.live_date = live_date;
		this.end_time = end_time;
		
		roomgiftList = new ArrayList<RoomInfo.RoomGiftRenkItem>();
		roomRenkListThis = new ArrayList<RoomInfo.RoomRenkItem>();
		roomRenkListWeek = new ArrayList<RoomInfo.RoomRenkItem>();
		roomRenkListMonth = new ArrayList<RoomInfo.RoomRenkItem>();
		roomRenkListSuper = new ArrayList<RoomInfo.RoomRenkItem>();
		try {
			for (int i = 0; i < giftRenkArray.length(); i++) {
				JSONObject thisGiftrenkItem = giftRenkArray.getJSONObject(i);
				RoomGiftRenkItem giftRenkItem = new RoomGiftRenkItem();
				giftRenkItem.index = thisGiftrenkItem.getInt("id");
				giftRenkItem.giftId = thisGiftrenkItem.getInt("gift_id");
				giftRenkItem.giftCount = thisGiftrenkItem.getInt("gift_count");
				giftRenkItem.diffvalue = thisGiftrenkItem.getInt("diffvalue");
				giftRenkItem.pic_url = thisGiftrenkItem.getString("pic");
				giftRenkItem.giftName = thisGiftrenkItem.getString("gift_name");
				roomgiftList.add(giftRenkItem);
			}
			for (int i = 0; i < renklistArray.length(); i++) {
				JSONArray thisarray = renklistArray.getJSONArray(i);
				for(int j=0;j<thisarray.length();j++){
					JSONObject thisrenkjson = thisarray.getJSONObject(j);
					RoomRenkItem thisitem = new RoomRenkItem();
					thisitem.pic_url = thisrenkjson.getString("avatar");
					thisitem.level = thisrenkjson.getInt("user_rank");
					thisitem.nickName = thisrenkjson.getString("nickname");
					thisitem.gold = thisrenkjson.getInt("dedication");
					if (i == 0) {// 本场
						roomRenkListThis.add(thisitem);
					} else if (i == 1) {// 本周
						roomRenkListWeek.add(thisitem);
					} else if (i == 2) {// 本月
						roomRenkListMonth.add(thisitem);
					} else if (i == 3) {// 超级
						roomRenkListSuper.add(thisitem);
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public class RoomGiftRenkItem {
		public int getIndex() {
			return index;
		}
		public int getGiftId() {
			return giftId;
		}
		public int getGiftCount() {
			return giftCount;
		}
		public int getDiffvalue() {
			return diffvalue;
		}
		public String getPic_url() {
			return pic_url;
		}
		public String getGiftName() {
			return giftName;
		}
		int index;
		int giftId;
		int giftCount;
		int diffvalue;
		String pic_url;
		String giftName;
	}

	public class RoomRenkItem {
		public String getPic_url() {
			return pic_url;
		}
		public String getNickName() {
			return nickName;
		}
		public int getLevel() {
			return level;
		}
		public int getGold() {
			return gold;
		}
		String pic_url;
		String nickName;
		int level;
		int gold;
	}
}
