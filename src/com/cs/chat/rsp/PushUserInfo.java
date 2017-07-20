package com.cs.chat.rsp;

import com.cs.chat.RspMsg;
import com.cs.chat.UserInfo;

public class PushUserInfo extends RspMsg {
	public String account;
	public int id;
	public String nickname; 
	
	public long createTime;
	public long latestLogin;
	public int loginTimes;
	
	public void parseFrom(UserInfo ui) {
		account = ui.account;
		id = ui.id;
		nickname = ui.nickname;
		
		createTime = ui.createTime;
		latestLogin = ui.latestLogin;
		loginTimes = ui.loginTimes;
	}
}
