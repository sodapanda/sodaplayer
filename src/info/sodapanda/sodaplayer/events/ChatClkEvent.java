package info.sodapanda.sodaplayer.events;

public class ChatClkEvent {
	String nick_name;
	int userid;

	public ChatClkEvent(String nick_name, int userid) {
		super();
		this.nick_name = nick_name;
		this.userid = userid;
	}

	public String getNick_name() {
		return nick_name;
	}

	public int getUserid() {
		return userid;
	}
}
