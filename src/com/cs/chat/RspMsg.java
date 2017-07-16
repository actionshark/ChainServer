package com.cs.chat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.js.log.Level;
import com.js.log.Logger;
import com.js.talk.TalkClient;

import net.sf.json.JSONObject;

public abstract class RspMsg extends ChatMsg {
	public static final String TAG = RspMsg.class.getSimpleName();
	
	public RspMsg() {
		uri = this.getClass().getSimpleName();
	}
	
	public void send(TalkClient tc) {
		try {
			JSONObject jo = new JSONObject();
			Field[] fields = this.getClass().getFields();
			
			for (Field field : fields) {
				int mod = field.getModifiers();
				
				if ((mod & Modifier.STATIC) == 0) {
					String name = field.getName();
					Object value = field.get(this);
					
					if (value != null) {
						jo.put(name, value);
					}
				}
			}
			
			String str = jo.toString();
			Logger.getInstance().print(TAG, Level.V, str);
			byte[] bs = str.getBytes();
			tc.send(bs);
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
	}
}
