package info.sodapanda.sodaplayer.activities;

import info.sodapanda.sodaplayer.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class ChatRoomMoreFragment extends Fragment {
	View fragView;
	View chongzhi_layout;
	View diange_layout;
	View fenxibang_layout;
	ChatRoomActivity activity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragView = inflater.inflate(R.layout.qc_room_more_table, container, false);
		
		chongzhi_layout = fragView.findViewById(R.id.chongzhi_layout);
		diange_layout = fragView.findViewById(R.id.diange_layout);
		
		fenxibang_layout = fragView.findViewById(R.id.fenxibang_layout);
		fenxibang_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), RoomRankListActivity.class);
				startActivity(intent);
			}
		});
		
		activity = (ChatRoomActivity) getActivity();
		diange_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SongActivity.class);
				startActivity(intent);
			}
		});
		
		chongzhi_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.jumpToPay();
			}
		});
		
		return fragView;
	}
}
