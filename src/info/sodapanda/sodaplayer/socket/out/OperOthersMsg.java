package info.sodapanda.sodaplayer.socket.out;

import info.sodapanda.sodaplayer.socket.MessageWraper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.util.Log;

import com.squareup.wire.ByteString;


public class OperOthersMsg extends DDMessage {
	String uid;
	String nickname;
	String to_uid;
	String to_nickname;
	String type;
	String period="60";
	short type_short = 103;

	public OperOthersMsg(String uid, String nickname, String to_uid,String to_nickname, String type) {
		this.uid = uid;
		this.nickname = nickname;
		this.to_uid = to_uid;
		this.to_nickname = to_nickname;
		this.type = type;
		
		Log.i("pipi","uid"+uid);
		Log.i("pipi","nickname"+nickname);
		Log.i("pipi","to_uid"+to_uid);
		Log.i("pipi","to_nickname"+to_nickname);
		Log.i("pipi","type"+type);
	}


	@Override
	public byte[] getMessageByte() {
		ArrayList<ByteString> params_list = new ArrayList<ByteString>();
		try {
			params_list.add(ByteString.of(uid.getBytes("UTF-8")));
			params_list.add(ByteString.of(nickname.getBytes("UTF-8")));
			params_list.add(ByteString.of(to_uid.getBytes("UTF-8")));
			params_list.add(ByteString.of(to_nickname.getBytes("UTF-8")));
			params_list.add(ByteString.of(type.getBytes("UTF-8")));
			params_list.add(ByteString.of(period.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new MessageWraper(params_list, type_short).getWrapedMessage();
	}
}
