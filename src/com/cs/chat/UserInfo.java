package com.cs.chat;

import com.cs.battle.Battle;

public class UserInfo extends com.js.talk.UserInfo {
	public static final String KEY_ID = "id";
	public static final String KEY_ACCOUNT = "account";
	public static final String KEY_NICKNAME = "nickname";
	public static final String KEY_CREATE_TIME = "create_time";
	public static final String KEY_LATEST_LOGIN = "latest_login";
	public static final String KEY_LOGIN_TIMES = "login_times";
	
	public String account;
	public String nickname;
	
	public long createTime;
	public long latestLogin;
	public int loginTimes;
	
	private Battle mBattle;
	
	public Battle getBattle() {
		return mBattle;
	}
	
	public void setBattle(Battle battle) {
		mBattle = battle;
	}
}
