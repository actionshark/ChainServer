package com.cs.chat.req;

import com.cs.battle.BattleMgr;
import com.cs.chat.ChatServer;
import com.cs.chat.ReqMsg;
import com.cs.chat.UserInfo;

public class RandBattleCancel extends ReqMsg {
	@Override
	public void perform(ChatServer server, UserInfo ui) throws Exception {
		BattleMgr.removeRandBattle(ui);
	}
}
