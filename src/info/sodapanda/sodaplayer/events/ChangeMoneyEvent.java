package info.sodapanda.sodaplayer.events;

public class ChangeMoneyEvent {
	String money;
	public ChangeMoneyEvent(String money){
		this.money = money;
	}
	
	public String getMoney(){
		return money;
	}
}
