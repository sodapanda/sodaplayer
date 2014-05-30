package info.sodapanda.sodaplayer.socket.in;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewableMessage {
	public abstract View getMyView(Context context,ViewGroup parent);
}
