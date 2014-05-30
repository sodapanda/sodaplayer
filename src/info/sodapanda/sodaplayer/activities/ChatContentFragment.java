package info.sodapanda.sodaplayer.activities;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.Status;
import info.sodapanda.sodaplayer.events.AddPrivateItemEvent;
import info.sodapanda.sodaplayer.events.AddPublicItemEvent;
import info.sodapanda.sodaplayer.socket.in.NoticeMsg;
import info.sodapanda.sodaplayer.views.PrivateChatContentView;
import info.sodapanda.sodaplayer.views.PubChatContentView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class ChatContentFragment extends Fragment {
	private ScrollView pubchaScrollView;
	private View fragView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragView = inflater.inflate(R.layout.chat_content_layout, container, false);
		
		initPriChatView();
		addNotice();
		return fragView;
	}
	
	private void initPriChatView(){
		pubchaScrollView = (ScrollView) fragView.findViewById(R.id.pubchatcontentview);
	}
	
	/**
	 * 显示主播欢迎
	 */
	private void addNotice(){
		String common_notice = Status.getRoomInfo().getPublic_notice();
		String private_notice = Status.getRoomInfo().getPrivate_notice();
		
		NoticeMsg pubNotice = new NoticeMsg(common_notice);
		NoticeMsg priNotice = new NoticeMsg(private_notice);
		PubChatContentView pubView = (PubChatContentView) fragView.findViewById(R.id.pub_view);
		PrivateChatContentView priView = (PrivateChatContentView) fragView.findViewById(R.id.pri_view);
		pubView.getPubChatEvent(new AddPublicItemEvent(pubNotice));
		priView.getPrivateChatEvent(new AddPrivateItemEvent(priNotice));
	}
	
	public void setPriVisibal(int flag) {
		if (flag == View.VISIBLE) {
			pubchaScrollView.setVisibility(View.GONE);

		} else {
			pubchaScrollView.setVisibility(View.VISIBLE);

		}
	}
}
