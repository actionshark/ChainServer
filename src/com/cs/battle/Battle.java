package com.cs.battle;

import java.util.ArrayList;
import java.util.List;

import com.cs.chat.UserInfo;

public abstract class Battle {
	public final List<UserInfo> as = new ArrayList<UserInfo>();
	public final List<UserInfo> bs = new ArrayList<UserInfo>();
	
	public long startTime;
}
