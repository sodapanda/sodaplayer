package info.sodapanda.sodaplayer.pojo;

public class GiftItem {
	public GiftItem(String gift_img_url, String gift_name, String gift_price,int giftId) {
		super();
		this.gift_img_url = gift_img_url;
		this.gift_name = gift_name;
		this.gift_price = gift_price;
		this.giftId = giftId;
	}

	private String gift_img_url;
	private String gift_name;
	private String gift_price;
	private int giftId;
	
	public String getGift_img_url() {
		return gift_img_url;
	}

	public String getGift_name() {
		return gift_name;
	}

	public String getGift_price() {
		return gift_price;
	}
	public int getGiftId(){
		return giftId;
	}

}
