package info.sodapanda.sodaplayer.utils;

import info.sodapanda.sodaplayer.R;

public class RoomLvToResId {
	public static int getLvIconResId(String lv_str) {
		int lv = Integer.parseInt(lv_str);
		if (lv >= resids.length) {
			return R.drawable.qc_room_lv01;
		}
		return resids[lv];
	}

	static int[] resids = new int[] { R.drawable.qc_room_lv01,
			R.drawable.qc_room_lv02, R.drawable.qc_room_lv03,
			R.drawable.qc_room_lv04, R.drawable.qc_room_lv05,
			R.drawable.qc_room_lv06, R.drawable.qc_room_lv07,
			R.drawable.qc_room_lv08, R.drawable.qc_room_lv09,
			R.drawable.qc_room_lv10, R.drawable.qc_room_lv11,
			R.drawable.qc_room_lv12, R.drawable.qc_room_lv13,
			R.drawable.qc_room_lv14, R.drawable.qc_room_lv15,
			R.drawable.qc_room_lv16, R.drawable.qc_room_lv17,
			R.drawable.qc_room_lv18, R.drawable.qc_room_lv19,
			R.drawable.qc_room_lv20, R.drawable.qc_room_lv21,
			R.drawable.qc_room_lv22, R.drawable.qc_room_lv23,
			R.drawable.qc_room_lv24, R.drawable.qc_room_lv25,
			R.drawable.qc_room_lv26, R.drawable.qc_room_lv27,
			R.drawable.qc_room_lv28, R.drawable.qc_room_lv29,
			R.drawable.qc_room_lv30, R.drawable.qc_room_lv31,
			R.drawable.qc_room_lv32, R.drawable.qc_room_lv33,
			R.drawable.qc_room_lv34, R.drawable.qc_room_lv35,
			R.drawable.qc_room_lv36 };
}
