package info.sodapanda.sodaplayer.pojo;

/**
 * 可点曲目
 * @author sodapanda
 *
 */
public class Song {
	String MusicName;
	String author;
	String money="";
	String song_id="";
	String create_time="";
	public Song(String MusicName, String author ,String money,String song_id ,String create_time) {
		this.MusicName = MusicName;
		this.author = author;
		this.money = money;
		this.song_id = song_id;
		this.create_time = create_time;
	}

	public String getName() {
		return MusicName;
	}

	public String getAuthor() {
		return author;
	}
	
	public String getMoney() {
		return money;
	}
	public String getId() {
		return song_id;
	}
	public String getCreate_time() {
		return create_time;
	}
}
