package info.sodapanda.sodaplayer.socket.out;


import info.sodapanda.sodaplayer.socket.MessageWraper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.util.Log;

import com.squareup.wire.ByteString;

public class LoginMessage extends DDMessage {
	String archives_id;
	String domain;
	String uid;
	String token="";
	String dev_type="{\"type\":1}";
	short type = 101;

	public LoginMessage(String archives_id, String domain, String uid) {
		this.archives_id = archives_id;
		this.domain = domain;
		this.uid = uid;
		if(uid.equals("0")){//游客登录
			int rand = (int) (Math.random()*9000+1000);
			this.token = "游客"+rand;
			Log.i("pipi","产生的匿名token"+this.token);
		}else{
//			token = HttpApi.getArchivesToken(uid, archives_id);
			//TODO 登陆获取token
			Log.i("pipi","聊天服务器登录token "+token+" uid "+uid);
		}
	}

	@Override
	public byte[] getMessageByte() {
		ArrayList<ByteString> params_list = new ArrayList<ByteString>();
		try {
			params_list.add(ByteString.of(archives_id.getBytes("UTF-8")));
			params_list.add(ByteString.of(domain.getBytes("UTF-8")));
			params_list.add(ByteString.of(uid.getBytes("UTF-8")));
			params_list.add(ByteString.of(token.getBytes("UTF-8")));
			params_list.add(ByteString.of(dev_type.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new MessageWraper(params_list,type).getWrapedMessage();
	}
}
