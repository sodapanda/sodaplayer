package info.sodapanda.sodaplayer.pojo;

import info.sodapanda.sodaplayer.views.data.PointsInfo;
import android.graphics.Bitmap;

public class MuchGift {
	Bitmap gift_img;
	PointsInfo points_info;
	
	public MuchGift(Bitmap img,PointsInfo info){
		this.gift_img = img;
		this.points_info = info;
	}
	
	public Bitmap getImg(){
		return gift_img;
	}
	
	public PointsInfo getInfo(){
		return points_info;
	}
}
