package info.sodapanda.sodaplayer;

public interface PlayerListener {
//	public void handle(String msg);
	public void onConnected();
	public void onDisconnected();
	public void onFinish();
}
