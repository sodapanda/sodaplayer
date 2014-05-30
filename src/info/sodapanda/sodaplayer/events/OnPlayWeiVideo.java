package info.sodapanda.sodaplayer.events;

public class OnPlayWeiVideo {
	public String videoUrl;
	
	public OnPlayWeiVideo(String url){
		this.videoUrl = url;
	}
	public String getVideoUrl(){
		return videoUrl;
	}
}
