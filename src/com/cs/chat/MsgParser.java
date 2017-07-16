package com.cs.chat;

import com.js.log.Level;
import com.js.log.Logger;

import net.sf.json.JSONObject;

public class MsgParser {
	public static final String TAG = MsgParser.class.getSimpleName();
	
	public static ReqMsg parse(byte[] data, int offset, int length) {
		try {
			String str = new String(data, offset, length);
			Logger.getInstance().print(TAG, Level.V, str);
			JSONObject jo = JSONObject.fromObject(str);
			
			String uri = jo.getString(ChatMsg.KEY_URI);
			Class<?> cls = Class.forName("com.cs.chat.req." + uri);
			ReqMsg msg = (ReqMsg) cls.newInstance();
			
			msg.mParams = jo;
			msg.uri = uri;
			
			return msg;
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
		
		return null;
	}
}
