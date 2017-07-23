package com.cs.chat.req;

import com.cs.battle.BattleMgr;
import com.cs.chat.ChatServer;
import com.cs.chat.ReqMsg;
import com.cs.chat.UserInfo;
import com.cs.chat.rsp.ShowToast;

public class RandBattleReq extends ReqMsg {
	@Override
	public void perform(ChatServer server, UserInfo ui) throws Exception {
		String err = BattleMgr.requestRandBattle(ui);
		if (err != null) {
			ShowToast st = new ShowToast();
			st.message = "请求随机匹配失败：" + err;
			st.send(ui.client);
		}
	}
}
