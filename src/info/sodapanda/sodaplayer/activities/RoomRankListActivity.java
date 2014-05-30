package info.sodapanda.sodaplayer.activities;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.utils.LvImgMap;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class RoomRankListActivity extends Activity {
	ArrayList<RankItem> benchangList = new ArrayList<RoomRankListActivity.RankItem>();
	ArrayList<RankItem> zhoubangList = new ArrayList<RoomRankListActivity.RankItem>();
	ArrayList<RankItem> yuebangList = new ArrayList<RoomRankListActivity.RankItem>();
	ArrayList<RankItem> chaojiList = new ArrayList<RoomRankListActivity.RankItem>();
	TabHost tabhost;
	LayoutInflater inflater;
	RankListAdapter benchangAdapter = new RankListAdapter(benchangList);
	RankListAdapter zhoubangAdapter = new RankListAdapter(zhoubangList);
	RankListAdapter yuebangAdapter = new RankListAdapter(yuebangList);
	RankListAdapter chaojiAdapter = new RankListAdapter(chaojiList);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = getLayoutInflater();
		setContentView(R.layout.roomranklist_layout);
		
		TextView title_bar_text = (TextView) findViewById(R.id.title_bar_text);
		title_bar_text.setText("粉丝榜");
		updateRankList();
		initTabHost();
		initListViews();
	}
	
	private void initTabHost(){
		tabhost = (TabHost) findViewById(R.id.rank_tabhost);
		tabhost.setup();
		
		String[] tabNameList = new String[]{"本场","周榜","月榜","超粉榜"};
		int[] tabId = new int[]{R.id.benchang_list,R.id.zhoubang_list,R.id.yuebang_list,R.id.chaojibang_list};
		
		for(int i = 0 ;i<tabNameList.length;i++){
			View indicatorView = inflater.inflate(R.layout.gift_tab_item, null);
			TextView name = (TextView) indicatorView.findViewById(R.id.tab_title);
			name.setText(tabNameList[i]);
			tabhost.addTab(tabhost.newTabSpec(tabNameList[i]).setContent(tabId[i]).setIndicator(indicatorView));
		}
	}
	
	private void initListViews(){
		ListView benchang_list = (ListView) findViewById(R.id.benchang_list);
		ListView zhoubang_list = (ListView) findViewById(R.id.zhoubang_list);
		ListView yuebang_list = (ListView) findViewById(R.id.yuebang_list);
		ListView chaojibang_list = (ListView) findViewById(R.id.chaojibang_list);
		
		benchang_list.setAdapter(benchangAdapter);
		zhoubang_list.setAdapter(zhoubangAdapter);
		yuebang_list.setAdapter(yuebangAdapter);
		chaojibang_list.setAdapter(chaojiAdapter);
	}
	
	private void updateRankList(){
//		HttpApi.getArchivesTops(Status.getRoomInfo().getArchivesId()+"", new QcJsonRh() {
//			
//			@Override
//			public void onResponse(JSONObject jsonMsg) {
//				JSONObject data = jsonMsg.optJSONObject("data");
//				if(data == null){
//					return;
//				}
//				
//				JSONArray list = data.optJSONArray("list");
//				for(int i = 0;i<list.length();i++){
//					JSONObject thisBang = list.optJSONObject(i);
//					String name = thisBang.optString("name");
//					JSONArray alist = thisBang.optJSONArray("list");
//					for(int j = 0;j<alist.length();j++){
//						JSONObject item = alist.optJSONObject(j);
//						int uid = item.optInt("uid");
//						int archives_id = item.optInt("archives_id");
//						int dedication = item.optInt("dedication");
//						String avatar = item.optString("avatar");
//						int user_rank = item.optInt("user_rank");
//						String nickname = item.optString("nickname");
//						RankItem thisRankItem = new RankItem(uid, dedication, archives_id, avatar, user_rank, nickname);
//						if(name.equals("本场粉丝榜")){
//							benchangList.add(thisRankItem);
//							benchangAdapter.notifyDataSetChanged();
//						}else if(name.equals("周榜")){
//							zhoubangList.add(thisRankItem);
//							zhoubangAdapter.notifyDataSetChanged();
//						}else if(name.equals("月榜")){
//							yuebangList.add(thisRankItem);
//							yuebangAdapter.notifyDataSetChanged();
//						}else if(name.equals("超粉榜")){
//							chaojiList.add(thisRankItem);
//							chaojiAdapter.notifyDataSetChanged();
//						}
//					}
//				}
//			}
//		});
		
	}
	
	public void onBack(View v){
		finish();
	}
	
	class RankListAdapter extends BaseAdapter{
		ArrayList<RankItem> ranlist = new ArrayList<RoomRankListActivity.RankItem>();
		
		public RankListAdapter(ArrayList<RankItem> ranklist){
			this.ranlist = ranklist;
		}

		@Override
		public int getCount() {
			return ranlist.size();
		}

		@Override
		public Object getItem(int p) {
			return p;
		}

		@Override
		public long getItemId(int p) {
			return p;
		}

		@Override
		public View getView(int position, View convertview, ViewGroup parent) {
			RankItemViewHolder holder;
			final RankItem thisItem = ranlist.get(position);
			
			if(convertview == null){
				convertview = inflater.inflate(R.layout.roomranklist_item, parent,false);
				holder = new RankItemViewHolder();
				holder.rank_item_id = (TextView) convertview.findViewById(R.id.rank_item_id);
				holder.rank_item_lv = (ImageView) convertview.findViewById(R.id.rank_item_lv);
				holder.rank_item_nickname = (TextView) convertview.findViewById(R.id.rank_item_nickname);
				holder.roomlist_index_text = (TextView) convertview.findViewById(R.id.roomlist_index_text);
				holder.roomrank_star_avatar = (ImageView) convertview.findViewById(R.id.roomrank_star_avatar);
				convertview.setTag(holder);
			}else{
				holder = (RankItemViewHolder) convertview.getTag();
			}
			
			holder.rank_item_id.setText(thisItem.uid+"");
			holder.rank_item_lv.setImageResource(new LvImgMap().getLvRes(thisItem.user_rank));
			holder.rank_item_nickname.setText(thisItem.nickname);
			holder.roomlist_index_text.setText(position+1+"");
			if(position <= 2){
				holder.roomlist_index_text.setBackgroundResource(R.drawable.pink_circle);
			}else{
				holder.roomlist_index_text.setBackgroundResource(R.drawable.gray_circle);
			}
			
			Picasso.with(RoomRankListActivity.this).load(thisItem.avatar).into(holder.roomrank_star_avatar);
			convertview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//TODO 从房间内排行榜跳转到用户主页
//					UserData user = new UserData();
//					user.setUid(thisItem.uid);
//					Intent intent = new Intent(RoomRankListActivity.this, UserPageActivity.class);
//					intent.putExtra("UserData", user);
//					startActivity(intent);
				}
			});
			
			
			return convertview;
		}
		
		class RankItemViewHolder{
			TextView roomlist_index_text;
			ImageView roomrank_star_avatar;
			TextView rank_item_nickname;
			ImageView rank_item_lv;
			TextView rank_item_id;
		}
		
	}
	
	/**
	 * 排行榜的条目
	 * @author qishui
	 *
	 */
	class RankItem{
		public RankItem(int uid, int dedication,int archives_id, String avatar, int user_rank,
				String nickname) {
			super();
			this.uid = uid;
			this.archives_id = archives_id;
			this.avatar = avatar;
			this.user_rank = user_rank;
			this.nickname = nickname;
		}
		int uid;
		int dedication;
		int archives_id;
		String avatar;
		int user_rank;
		String nickname;
		
	}
}
