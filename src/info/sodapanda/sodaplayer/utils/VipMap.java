package info.sodapanda.sodaplayer.utils;

import info.sodapanda.sodaplayer.R;

public class VipMap {
	int vip1Res = R.drawable.vip1;
	int vip2Res = R.drawable.vip2;

	public int getVipRes(int vipType) {
		switch (vipType) {
		case 1:
			return vip1Res;
		case 2:
			return vip2Res;

		default:
			return -1;
		}
	}
}
