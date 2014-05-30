package info.sodapanda.sodaplayer;

import info.sodapanda.sodaplayer.pojo.RoomInfo;

public class Status {
	
	//当前所在房间的信息
	private static RoomInfo roominfo = new RoomInfo("", "", "", "", null, null, 0, 0, "", false, "", "", 0, "", "", "", "", null);
	
	/**
	 * 设置当前正在观看的房间的信息
	 * @param roominfo 当前正在观看的房间的信息
	 */
	public static void setRoomInfo(RoomInfo mroominfo){
		roominfo = mroominfo;
	}
	
	/**
	 * 得到当前正在观看的房间的信息
	 * @return 房间信息
	 */
	public static RoomInfo getRoomInfo(){
		return roominfo;
	}
	
	/**
	 * 清空临时数据
	 */
	public static void clearData(){
		roominfo = null;
//		is_playing = false;
	}

}
