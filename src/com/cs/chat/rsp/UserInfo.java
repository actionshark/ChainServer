package com.cs.chat.rsp;

import com.cs.chat.RspMsg;
import com.cs.chat.UserInfor;

public class UserInfo extends RspMsg {
	public String account;
	public int id;
	public String nickname; 
	
	public long createTime;
	public long latestLogin;
	public int loginTimes;
	
	public void parseFrom(UserInfor ui) {
		account = ui.account;
		id = ui.id;
		nickname = ui.nickname;
		
		createTime = ui.createTime;
		latestLogin = ui.latestLogin;
		loginTimes = ui.loginTimes;
	}
}
