package info.sodapanda.sodaplayer.activities;

import info.sodapanda.sodaplayer.R;
import info.sodapanda.sodaplayer.Status;
import info.sodapanda.sodaplayer.adapters.SongAdapter;
import info.sodapanda.sodaplayer.pojo.LogedUser;
import info.sodapanda.sodaplayer.pojo.Song;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SongActivity extends Activity{
	ListView song_list;
	ImageView back;
	List<Song> songList=new ArrayList<Song>();
	SongAdapter songAdapter ;
	TextView empty_view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qc_choice_song);
		findViewById();
		setView();
		
		TextView title_bar_text = (TextView) findViewById(R.id.title_bar_text);
		title_bar_text.setText("点歌");
	}
	
	public void onBack(View v){
		finish();
	}
	
    public void  findViewById(){
    	song_list = (ListView)findViewById(R.id.song_list);
    	empty_view = (TextView)findViewById(R.id.empty_view);
    }
    public void  setView(){
//    	dotey_id = getIntent().getStringExtra("dotey_id");
//    	uid = getIntent().getStringExtra("uid");
//    	archives_id = getIntent().getStringExtra("archives_id");
	    songAdapter =new SongAdapter(SongActivity.this,songList,Status.getRoomInfo().getuserid()+"",LogedUser.getUser_id()+"",Status.getRoomInfo().getArchivesId()+"");
	    song_list.setAdapter(songAdapter);
    	getSongJson();
    }

    public void  getSongJson(){
//		HttpApi.getDoteySong(Status.getRoomInfo().getuserid()+ "", "1", "100", new QcJsonRh() {
//			
//			@Override
//			public void onResponse(JSONObject jsonMsg) {
//				try {
//					String code = jsonMsg.optString("code");
//					String msg = jsonMsg.optString("msg");
//					if (code.equals("200")) {
//						JSONObject data= jsonMsg.getJSONObject("data");
//						JSONArray songlist=data.getJSONArray("list");
//						for(int i=0;i<songlist.length();i++){
//							JSONObject thissong= songlist.getJSONObject(i);
//							String song_id=thissong.isNull("song_id")?"0":thissong.getString("song_id");
//							String name=thissong.isNull("name")?"0":thissong.getString("name");
//							String pipiegg=thissong.isNull("pipiegg")?"0":thissong.getString("pipiegg");
//							String singer=thissong.isNull("singer")?"0":thissong.getString("singer");
//							String create_time=thissong.isNull("create_time")?"0":thissong.getString("create_time");
//							Song song =new Song(name,singer,pipiegg,song_id,create_time);
//							songList.add(song);
//						}
//						if(songList.size()==0){
//							empty_view.setVisibility(View.VISIBLE);
//						}else{
//							empty_view.setVisibility(View.INVISIBLE);
//						}
//////					    songAdapter =new SongAdapter(SongActivity.this,songList,dotey_id,uid,archives_id);
////					    song_list.setAdapter(songAdapter);
//					} 
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				songAdapter.notifyDataSetChanged();
//			}
//		});
	}

}
