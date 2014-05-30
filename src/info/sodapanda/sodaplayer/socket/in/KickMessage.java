package info.sodapanda.sodaplayer.socket.in;

import info.sodapanda.sodaplayer.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class KickMessage extends ViewableMessage {
	String dNickName;
	String sNickName;
	int dUserID;
	
	public KickMessage(String dNickName, String sNickName,int dUserID) {
		this.dNickName = dNickName;
		this.sNickName = sNickName;
		this.dUserID = dUserID;
	}

	@Override
	public View getMyView(Context context,ViewGroup parent) {
		TextView textView = new TextView(context);
		textView.setTextColor(context.getResources().getColor(R.color.qc_font_green));
		textView.setText("【系统通知】"+dNickName+"被"+sNickName+"踢出房间");
		return textView;
	}
}
