package info.sodapanda.sodaplayer.events;

public class ToGiftEvent {
	String nickname;
	int userid;
	public ToGiftEvent(String name,int id){
		this.nickname = name;
		this.userid = id;
	}
	
	public String getNickName(){
		return nickname;
	}
	public int getUid(){
		return userid;
	}
}
