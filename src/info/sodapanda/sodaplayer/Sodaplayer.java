package info.sodapanda.sodaplayer;

import android.content.Context;

public class Sodaplayer {
	private static Context context;
	
	public static void init(Context contex){
		context = contex;
	}
	
	public static Context getAppCxt(){
		return context;
	}
}
