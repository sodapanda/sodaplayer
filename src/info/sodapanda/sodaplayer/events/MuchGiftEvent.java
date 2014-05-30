package info.sodapanda.sodaplayer.events;

import info.sodapanda.sodaplayer.views.data.HeartPoints;
import info.sodapanda.sodaplayer.views.data.LovePoints;
import info.sodapanda.sodaplayer.views.data.PointsInfo;
import info.sodapanda.sodaplayer.views.data.SmailPoints;
import info.sodapanda.sodaplayer.views.data.TwoFlyPoints;
import info.sodapanda.sodaplayer.views.data.V1314Points;
import info.sodapanda.sodaplayer.views.data.V3344Points;
import info.sodapanda.sodaplayer.views.data.V520Points;
import info.sodapanda.sodaplayer.views.data.VPoints;

public class MuchGiftEvent {
	int count;
	String pic;
	public MuchGiftEvent(int count, String pic) {
		super();
		this.count = count;
		this.pic = pic;
	}
	public int getCount() {
		return count;
	}
	public String getPic() {
		return pic;
	}
	
	public PointsInfo getPointsInfo(){
		if(count ==50){
			return new VPoints();
		}else if(count ==99){
			return new HeartPoints();
		}else if(count ==100){
			return new SmailPoints();
		}else if(count == 300){
			return new LovePoints();
		}else if(count == 520){
			return new V520Points();
		}else if(count == 999){
			return new TwoFlyPoints();
		}else if(count == 1314){
			return new V1314Points();
		}else if(count ==3344){
			return new V3344Points();
		}else{
			return null;
		}
	}
}	
