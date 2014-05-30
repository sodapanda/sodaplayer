package info.sodapanda.sodaplayer.socket.in;

import info.sodapanda.sodaplayer.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NoticeMsg extends ViewableMessage {
	String noticeText;

	public NoticeMsg(String notice) {
		noticeText = notice;
	}

	@Override
	public View getMyView(Context context,ViewGroup parent) {
		TextView noticeView = new TextView(context);
		noticeView.setText(noticeText);
		noticeView.setTextColor(context.getResources().getColor(R.color.qc_font_green));
		return noticeView;
	}
}
