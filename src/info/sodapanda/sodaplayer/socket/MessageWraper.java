package info.sodapanda.sodaplayer.socket;


import info.sodapanda.sodaplayer.socket.strAvg.Builder;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.util.Log;

import com.squareup.wire.ByteString;

public class MessageWraper {
	private ArrayList<ByteString> params_list;
	private short type;
	
	public MessageWraper(ArrayList<ByteString> params_list,short type) {
		this.params_list = params_list;
		this.type = type;
	}

	public byte[] getWrapedMessage() {
		//body
		strAvg.Builder builder = new Builder();
		strAvg msg = builder.strs(params_list).build();
		byte[] body_bytes = msg.toByteArray();
		
		//head
		short version = 0;
		int len = body_bytes.length;
		ByteBuffer byte_buff = ByteBuffer.allocate(8);
		byte_buff.putShort(version);
		byte_buff.putShort(type);
		byte_buff.putInt(len);
		byte[] head_bytes = byte_buff.array();
		
		//packet
		ByteBuffer packet_buf = ByteBuffer.allocate(body_bytes.length+head_bytes.length);
		packet_buf.put(head_bytes);
		packet_buf.put(body_bytes);
		byte[] packet_bytes = packet_buf.array();
		Log.i("pipi","发送的消息数据 "+ packet_bytes.length);
		return packet_bytes;
	}
}
