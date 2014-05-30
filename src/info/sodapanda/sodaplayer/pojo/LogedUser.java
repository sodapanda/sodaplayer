package info.sodapanda.sodaplayer.pojo;

import android.util.Log;

public class LogedUser {
	private static int user_id;
	private static int room_id;
	private static String user_name = "";
	private static int gender;
	private static String avatat = "";
	private static int money;
	private static int vip = -1;
	private static String session_id = "";
//	private static UserData userData = new UserData();

	public static void init(int user_id, int room_id, String user_name, int gender, String avatat, int money,
			String session_id, int vip) {
		LogedUser.user_id = user_id;
		LogedUser.vip = vip;
		LogedUser.room_id = room_id;
		LogedUser.user_name = user_name;
		LogedUser.gender = gender;
		LogedUser.avatat = avatat;
		LogedUser.money = money;
		LogedUser.session_id = session_id;
		Log.i("pipi", "登录用户的session_id 是" + session_id);
	}

	public static void updateUserinfo(int user_id, int room_id, String user_name, int gender, String avatat, int money) {
		LogedUser.user_id = user_id;
		LogedUser.room_id = room_id;
		LogedUser.user_name = user_name;
		LogedUser.gender = gender;
		LogedUser.avatat = avatat;
		LogedUser.money = money;
	}

	public static int getUser_id() {
		return user_id;
	}

	public static void setAvatar(String avatar_url) {
		LogedUser.avatat = avatar_url;
	}

	public static int getRoom_id() {
		return room_id;
	}

	public static String getUser_name() {
		return user_name;
	}

	public static int getGender() {
		return gender;
	}

	public static String getAvatat() {
		return avatat;
	}

	public static int getMoney() {
		return money;
	}

	public static void setMoney(int money) {
		LogedUser.money = money;
	}

	public static String getSessionId() {
		// Log.i("pipi", "取用户的session_id是" + session_id);
		return session_id;
	}

	public static int getVip() {
		return LogedUser.vip;
	}

	public static boolean isLoged() {
		if (user_id == 0) {// 如果id是0，说明没有登录
			return false;
		} else {
			return true;
		}
	}

	public static boolean isMe(int uid) {
		if (!isLoged()) {
			return false;
		}
		if (uid == user_id) {
			return true;
		}
		return false;
	}

//	public static UserData getUserData() {
//		return userData;
//	}
//
//	public static void setUserData(UserData userData) {
//		LogedUser.userData = userData;
//	}

	public static void logout() {
		user_id = 0;
	}
}
