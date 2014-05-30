package info.sodapanda.sodaplayer.socket.in;

import info.sodapanda.sodaplayer.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisForbidMessage extends ViewableMessage {
	String sNickName;
	String dNickName;
	
	public DisForbidMessage(String dNickName,String sNickName){
		this.sNickName = sNickName;
		this.dNickName = dNickName;
	}

	@Override
	public View getMyView(Context context,ViewGroup parent) {
		TextView textView = new TextView(context);
		textView.setTextColor(context.getResources().getColor(R.color.qc_font_green));
		textView.setText("【系统通知】"+dNickName+"被"+sNickName+"恢复发言");
		return textView;
	}
}
