package info.sodapanda.sodaplayer.activities;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.adapters.AudiencAdapter;
import info.sodapanda.sodaplayer.pojo.LogedUser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AudienceFragment extends Fragment {
	String TAG = "ChatRoomActivity";
	AudiencAdapter adapter;
	private ListView audienc_listview;
	View openedView=null;
	View fragView;
	ChatRoomActivity activity;
	//TODO 观众列表下拉加载更多
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragView = inflater.inflate(R.layout.audience_fragment_layout, container, false);
		audienc_listview = (ListView) fragView.findViewById(R.id.audienc_listview);
		activity = (ChatRoomActivity) getActivity();

		initAdapter();
		return fragView;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		adapter.stopTimer();
	}
	
	private void initAdapter(){
		adapter = new AudiencAdapter(getActivity());
		audienc_listview.setAdapter(adapter);

		audienc_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				if(!LogedUser.isLoged()){
					activity.jumpToLog();
					return;
				}
				int thisuid = (Integer) view.getTag(R.id.uidid);
				if (thisuid!=0 && !LogedUser.isMe(thisuid)) {
					if (view == openedView) {
						openedView.findViewById(R.id.mem_action_layout).setVisibility(View.GONE);
						openedView = null;
					} else {
						if (openedView != null) {
							openedView.findViewById(R.id.mem_action_layout).setVisibility(View.GONE);
						}
						view.findViewById(R.id.mem_action_layout).setVisibility(View.VISIBLE);
						openedView = view;
					}
				}

			}
		});
	}
}
