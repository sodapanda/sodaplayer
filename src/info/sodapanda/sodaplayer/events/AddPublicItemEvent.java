package info.sodapanda.sodaplayer.events;

import info.sodapanda.sodaplayer.socket.in.ViewableMessage;

public class AddPublicItemEvent {
	private ViewableMessage msg;
	public AddPublicItemEvent(ViewableMessage msg){
		this.msg = msg;
	}
	
	public ViewableMessage getMessage(){
		return msg;
	}
}
