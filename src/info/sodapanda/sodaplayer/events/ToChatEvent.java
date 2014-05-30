package info.sodapanda.sodaplayer.events;

public class ToChatEvent {
	String dNickName;
	int dUserId;

	public ToChatEvent(String dNickName, int dUserId) {
		this.dNickName = dNickName;
		this.dUserId = dUserId;
	}

	public String getNickName() {
		return dNickName;
	}

	public int getUserid() {
		return dUserId;
	}
}
