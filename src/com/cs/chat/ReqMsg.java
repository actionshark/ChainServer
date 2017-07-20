package com.cs.chat;

import net.sf.json.JSONObject;

public abstract class ReqMsg extends ChatMsg {
	protected JSONObject mParams;
	
	public abstract void perform(ChatServer server, UserInfo ui) throws Exception;
}
