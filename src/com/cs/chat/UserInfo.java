package com.cs.chat;

import com.cs.battle.Battle;

public class UserInfo extends com.js.talk.UserInfo {
	public static final String KEY_ID = "id";
	public static final String KEY_ACCOUNT = "account";
	public static final String KEY_NICKNAME = "nickname";
	public static final String KEY_DEVICE_NAME = "deviceName";
	public static final String KEY_CREATE_TIME = "createTime";
	public static final String KEY_LATEST_LOGIN = "latestLogin";
	public static final String KEY_LOGIN_TIMES = "loginTimes";
	
	public String account;
	public String nickname;
	public String deviceName;
	
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
