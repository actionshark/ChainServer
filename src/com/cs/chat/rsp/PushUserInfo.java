package com.cs.chat.rsp;

import com.cs.chat.RspMsg;
import com.cs.chat.UserInfo;

public class PushUserInfo extends RspMsg {
	public String account;
	public int id;
	public String nickname;
	public long latestLogin;
	
	public void parseFrom(UserInfo ui) {
		account = ui.account;
		id = ui.id;
		nickname = ui.nickname;
		latestLogin = ui.latestLogin;
	}
}
