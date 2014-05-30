package info.sodapanda.sodaplayer.socket;

import android.os.Handler;
import android.os.Looper;

public abstract class ChatHandler {
	
	public void handleOnMain(final boolean isSuccess){
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			
			@Override
			public void run() {
				handle(isSuccess);
			}
		});
	}
	
	public abstract void handle(boolean isSuccess);
}
