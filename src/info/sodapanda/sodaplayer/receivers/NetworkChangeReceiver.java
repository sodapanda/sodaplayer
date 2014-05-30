package info.sodapanda.sodaplayer.receivers;

import info.sodapanda.sodaplayer.events.NetworkConnEvent;
import info.sodapanda.sodaplayer.events.NetworkDisconnEvent;
import info.sodapanda.sodaplayer.socket.BusProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.squareup.otto.Bus;

public class NetworkChangeReceiver extends BroadcastReceiver{
	Bus bus = BusProvider.getBus();

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = connMan.getActiveNetworkInfo();
		boolean isConn = activeNetwork!=null && activeNetwork.isConnected();
		Log.i("test","网络情况变化了"+isConn);
		if(isConn){
			bus.post(new NetworkConnEvent());
		}else{
			bus.post(new NetworkDisconnEvent());
		}
	}
	
}
