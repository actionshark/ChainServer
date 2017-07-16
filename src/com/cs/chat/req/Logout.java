package com.cs.chat.req;

import com.cs.chat.ChatServer;
import com.cs.chat.ReqMsg;
import com.cs.chat.UserInfor;

public class Logout extends ReqMsg {
	@Override
	public void perform(ChatServer server, UserInfor ui) throws Exception {
		server.geTalkServer().removeUser(ui.id);
	}
}
