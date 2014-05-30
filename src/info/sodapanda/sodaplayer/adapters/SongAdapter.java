package info.sodapanda.sodaplayer.adapters;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.Status;
import info.sodapanda.sodaplayer.pojo.LogedUser;
import info.sodapanda.sodaplayer.pojo.Song;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SongAdapter extends BaseAdapter {
	private String dotey_id = "";
	private String uid = "";
	private String archives_id = "";

	private class SongHolder {
		TextView song_name;
		TextView song_singer;
		Button choose_song;
	}

	private Activity context;
	private List<Song> list;
	private LayoutInflater mInflater;

	public SongAdapter(Activity context, List<Song> list, String dotey_id,
			String uid, String archives_id) {
		super();
		this.context = context;
		setList(list);
		this.dotey_id = dotey_id;
		this.uid = uid;
		this.archives_id = archives_id;
	}

	public void setList(List<Song> list) {
		this.list = list;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int index) {

		return list.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		final SongHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.kk_choice_song_item, null);
			holder = new SongHolder();
			holder.song_name = (TextView) convertView
					.findViewById(R.id.song_name);
			holder.song_singer = (TextView) convertView
					.findViewById(R.id.song_singer);
			holder.choose_song = (Button) convertView
					.findViewById(R.id.song_operate);
			convertView.setTag(holder);
		} else {
			holder = (SongHolder) convertView.getTag();
		}
		final Song song = list.get(index);
		holder.song_name.setText(song.getName());
		holder.song_singer.setText(song.getAuthor());
		holder.choose_song.setTag(song.getId() + "");
		holder.choose_song.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog(song);
			}
		});
		return convertView;
	}

	public void dialog(final Song cSong) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("每首歌曲需预扣1000橙币，在主播接受点歌后收取，是否确认？");
		builder.setTitle("提示");
		builder.setPositiveButton("确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						chooseSong(cSong);
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	public void chooseSong(Song cSong) {
//		HttpApi.demandSong(dotey_id + "", LogedUser.getUser_id() + "", Status
//				.getRoomInfo().getArchivesId() + "", cSong.getId(),
//				cSong.getName(), cSong.getAuthor(), new QcJsonRh() {
//
//					@Override
//					public void onResponse(JSONObject jsonMsg) {
//						try {
//							String code = jsonMsg.getString("code");
//							String msg = jsonMsg.getString("msg");
//							if (code.equals("200")) {
//								Toast.makeText(context, "点歌成功",
//										Toast.LENGTH_SHORT).show();
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//				});
	}
}
