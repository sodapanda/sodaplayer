package info.sodapanda.sodaplayer.socket;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class BusProvider {
	private static final Bus BUS = new Bus(ThreadEnforcer.MAIN);
	
	public static Bus getBus(){
		return BUS;
	}
	
	public void postOnMain(){
		
	}
}
