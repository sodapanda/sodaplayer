package info.sodapanda.sodaplayer.events;

public class MarqueeEvent {
	String giftName;
	int giftCount;
	String dNickName;
//	int sRichLevel;
//	int sVip;
//	int sActorLevel;
//	int dRichLevel;
//	int dVip;
//	int dActorLevel;
	String sNickName;
//	String time;
	String pic;
	public MarqueeEvent(String giftName, int giftCount, String dNickName,String sNickName, String pic) {
		super();
		this.giftName = giftName;
		this.giftCount = giftCount;
		this.dNickName = dNickName;
//		this.sRichLevel = sRichLevel;
//		this.sVip = sVip;
//		this.sActorLevel = sActorLevel;
//		this.dRichLevel = dRichLevel;
//		this.dVip = dVip;
//		this.dActorLevel = dActorLevel;
		this.sNickName = sNickName;
//		this.time = time;
		this.pic = pic;
	}
	public String getGiftName() {
		return giftName;
	}
	public int getGiftCount() {
		return giftCount;
	}
	public String getdNickName() {
		return dNickName;
	}
//	public int getsRichLevel() {
//		return sRichLevel;
//	}
//	public int getsVip() {
//		return sVip;
//	}
//	public int getsActorLevel() {
//		return sActorLevel;
//	}
//	public int getdRichLevel() {
//		return dRichLevel;
//	}
//	public int getdVip() {
//		return dVip;
//	}
//	public int getdActorLevel() {
//		return dActorLevel;
//	}
	public String getsNickName() {
		return sNickName;
	}
//	public String getTime() {
//		return time;
//	}
	public String getPic() {
		return pic;
	}
	
}
