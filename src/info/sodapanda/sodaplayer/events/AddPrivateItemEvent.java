package info.sodapanda.sodaplayer.events;

import info.sodapanda.sodaplayer.socket.in.ViewableMessage;

public class AddPrivateItemEvent {
	private ViewableMessage msg;
	public AddPrivateItemEvent(ViewableMessage msg){
		this.msg = msg;
	}
	
	public ViewableMessage getMessage(){
		return msg;
	}
}
