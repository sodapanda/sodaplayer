package info.sodapanda.sodaplayer.socket.out;

import info.sodapanda.sodaplayer.socket.MessageWraper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.squareup.wire.ByteString;

public class PubNoDChatMessage extends DDMessage {
	private String content;
	
	private String archives_id;
	private String domain;
	private String from_uid;
	private String from_nickname;
	private String to_uid;
	private String to_nickname;
	private short type = 102;
	private String chat_type;



	public PubNoDChatMessage(String content, String archives_id, String domain,
			String from_uid, String from_nickname, String to_uid,
			String to_nickname, String chat_type) {
		super();
		this.content = content;
		this.archives_id = archives_id;
		this.domain = domain;
		this.from_uid = from_uid;
		this.from_nickname = from_nickname;
		this.to_uid = to_uid;
		this.to_nickname = to_nickname;
		this.chat_type = chat_type;
	}



	@Override
	public byte[] getMessageByte() {
		
		ArrayList<ByteString> params_list = new ArrayList<ByteString>();
		try {
			params_list.add(ByteString.of(archives_id.getBytes("UTF-8")));
			params_list.add(ByteString.of(domain.getBytes("UTF-8")));
			params_list.add(ByteString.of(from_uid.getBytes("UTF-8")));
			params_list.add(ByteString.of(from_nickname.getBytes("UTF-8")));
			params_list.add(ByteString.of(to_uid.getBytes("UTF-8")));
			params_list.add(ByteString.of(to_nickname.getBytes("UTF-8")));
			params_list.add(ByteString.of(content.getBytes("UTF-8")));
			params_list.add(ByteString.of(chat_type.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new MessageWraper(params_list, type).getWrapedMessage();
	}
}
