package info.sodapanda.sodaplayer;

import info.sodapanda.sodaplayer.pojo.RoomInfo;

public class Status {
	
	//当前所在房间的信息
	private static RoomInfo roominfo;
//	private static boolean is_playing=false;
	
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
	
//	/**
//	 * 状态设置为正在播放
//	 */
//	public static void setPlaying(){
//		is_playing = true;
//	}
	
//	/**
//	 * 状态设置为停止播放
//	 */
//	public static void setNotPlaying(){
//		is_playing = false;
//	}
//	
//	/**
//	 * 返回时候正在播放
//	 * @return
//	 */
//	public static boolean isPlaying(){
//		return is_playing;
//	}
	
	/**
	 * 清空临时数据
	 */
	public static void clearData(){
		roominfo = null;
//		is_playing = false;
	}

}
