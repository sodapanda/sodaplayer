package info.sodapanda.sodaplayer.activities;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.Status;
import info.sodapanda.sodaplayer.events.OnPlayWeiVideo;
import info.sodapanda.sodaplayer.pojo.LogedUser;
import info.sodapanda.sodaplayer.socket.BusProvider;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

public class VideoListFragment extends Fragment {
	LayoutInflater inflater;
	View fragView;
	ListView videoListView;
	Bus bus = BusProvider.getBus();
	VideoListAdapter adapter;
	ArrayList<WeiVideo> weiVideoList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		fragView = inflater.inflate(R.layout.video_list_layout, container, false);
		weiVideoList = new ArrayList<VideoListFragment.WeiVideo>();

		videoListView = (ListView) fragView.findViewById(R.id.video_listview);
		TextView no_weivideo_text = (TextView) fragView.findViewById(R.id.no_weivideo_text);
		
		adapter = new VideoListAdapter();
		videoListView.setAdapter(adapter);
		videoListView.setEmptyView(no_weivideo_text);
		updateVideoList();
		return fragView;
	}

	private void updateVideoList() {
		weiVideoList.clear();
//		HttpApi.getMyVideo("HomeActivity", Status.getRoomInfo().getuserid() + "", LogedUser.getUser_id() + "", "1",
//				"10", new QcJsonRh() {
//
//					@Override
//					public void onResponse(JSONObject jsonmsg) {
//						// 获取视频更新推送过来的vid
//						Intent intent = getActivity().getIntent();
//						int id = intent.getIntExtra("vid", -1);
//
//						String code = jsonmsg.optString("code");
//						String msg = jsonmsg.optString("msg");
//						if (!code.equals("200")) {
//							Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
//						}
//						Log.i("test", "视频列表 " + jsonmsg.toString());
//						JSONObject data = jsonmsg.optJSONObject("data");
//						if (data == null) {
//							return;
//						}
//						String count = data.optString("count");
//						JSONArray list = data.optJSONArray("list");
//						for (int i = 0; i < list.length(); i++) {
//							JSONObject thisItem = list.optJSONObject(i);
//							String uid = thisItem.optString("uid");
//							String vid = thisItem.optString("vid");
//							String description = thisItem.optString("description");
//							String video = thisItem.optString("video");
//							String duration = thisItem.optString("duration");
//							String cover = thisItem.optString("cover");
//							String praise = thisItem.optString("praise");
//							int is_praise = thisItem.optInt("is_praise");
//							WeiVideo thisVideo = new WeiVideo(uid, vid, description, duration, video, cover, praise,
//									is_praise);
//							weiVideoList.add(thisVideo);
//
//							// vid匹配时，自动播放
//							if (id != -1 && vid.equals(id+"")) {
//								bus.post(new OnPlayWeiVideo(thisVideo.getVideo()));
//							}
//						}
//
//						adapter.notifyDataSetChanged();
//					}
//				});
	}

	class WeiVideo {
		public String getVid() {
			return vid;
		}

		public String getUid() {
			return uid;
		}

		public String getVideo_key() {
			return video_key;
		}

		public String getCover_key() {
			return cover_key;
		}

		public String getDescription() {
			return description;
		}

		public String getDuration() {
			return duration;
		}

		public String getSize() {
			return size;
		}

		public String getPersistent_id() {
			return persistent_id;
		}

		public String getParsistent_status() {
			return parsistent_status;
		}

		public String getPraise() {
			return praise;
		}

		public int getIs_praise() {
			return is_praise;
		}

		public String getVideo() {
			return video;
		}

		public String getCover() {
			return cover;
		}

		String vid;
		String uid;
		String video_key;
		String cover_key;
		String description;
		String duration;
		String size;
		String persistent_id;
		String parsistent_status;
		String praise;
		int is_praise;
		String video;
		String cover;

		public WeiVideo(String uid, String vid, String description, String duration, String video, String cover,
				String praise, int is_praise) {
			this.uid = uid;
			this.vid = vid;
			this.description = description;
			this.duration = duration;
			this.video = video;
			this.cover = cover;
			this.praise = praise;
			this.is_praise = is_praise;
		}

	}

	class VideoListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return weiVideoList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final WeiVideo thisVideo = weiVideoList.get(position);

			final ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.video_list_item, null);
				holder = new ViewHolder();
				holder.video_cover = (ImageView) convertView.findViewById(R.id.video_cover);
				holder.video_description = (TextView) convertView.findViewById(R.id.video_description);
				holder.video_length_text = (TextView) convertView.findViewById(R.id.video_length_text);
				holder.like_heart_img = (ImageView) convertView.findViewById(R.id.like_heart_img);
				holder.like_num = (TextView) convertView.findViewById(R.id.like_num);
				holder.like_btn = convertView.findViewById(R.id.like_btn);
				holder.del_btn = convertView.findViewById(R.id.del_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Picasso.with(getActivity()).load(thisVideo.getCover()).into(holder.video_cover);
			holder.video_description.setText(thisVideo.getDescription());
			holder.video_length_text.setText(thisVideo.getDuration() + "秒");
			holder.like_num.setText(thisVideo.getPraise());
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					bus.post(new OnPlayWeiVideo(thisVideo.getVideo()));
				}
			});

			if (thisVideo.getIs_praise() == 1) {// 已经点赞
				Log.i("test", "已经点赞 ");
				holder.like_btn.setSelected(true);
			} else {
				Log.i("test", "没有点赞 " + thisVideo.getIs_praise());
				holder.like_btn.setSelected(false);
			}

			holder.like_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!holder.like_btn.isSelected()) {
//						HttpApi.praiseVideo(LogedUser.getUser_id() + "", thisVideo.getVid(), new QcJsonRh() {
//
//							@Override
//							public void onResponse(JSONObject jsonMsg) {
//								String code = jsonMsg.optString("code");
//								if (code.equals("200")) {
//									holder.like_btn.setSelected(true);
//									JSONObject data = jsonMsg.optJSONObject("data");
//									if (data == null) {
//										return;
//									}
//									String praise = data.optString("praise");
//									holder.like_num.setText(praise);
//
//								} else {
//									String msg = jsonMsg.optString("msg");
//									Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
//								}
//							}
//						});
					}
				}
			});

			if (LogedUser.getUser_id() == Status.getRoomInfo().getuserid()) {
				holder.del_btn.setVisibility(View.VISIBLE);
				holder.del_btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
//						HttpApi.removeVideo(LogedUser.getUser_id() + "", thisVideo.getVid(), new QcJsonRh() {
//
//							@Override
//							public void onResponse(JSONObject jsonMsg) {
//								String code = jsonMsg.optString("code");
//								if (code.equals("200")) {
//									Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_LONG).show();
//									updateVideoList();
//									((ChatRoomActivity) getActivity()).stopWeiVideo();
//								} else {
//									Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_LONG).show();
//								}
//
//							}
//						});
					}
				});
			}
			return convertView;
		}

		class ViewHolder {
			ImageView video_cover;
			TextView video_description;
			TextView video_length_text;
			ImageView like_heart_img;
			TextView like_num;
			View like_btn;
			View del_btn;
		}

	}
}
