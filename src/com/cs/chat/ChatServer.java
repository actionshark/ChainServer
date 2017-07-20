package com.cs.chat;

import com.cs.chat.req.Login;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.talk.ITalkServerListener;
import com.js.talk.TalkClient;
import com.js.talk.TalkServer;

public class ChatServer {
	public static final String TAG = ChatServer.class.getSimpleName();
	
	private TalkServer<UserInfo> mServer;
	
	public void setServer(TalkServer<UserInfo> server) {
		mServer = server;
	}
	
	public TalkServer<UserInfo> geTalkServer() {
		return mServer;
	}
	
	public void start() {
		mServer.start();
		mServer.setListener(new ITalkServerListener<UserInfo>() {
			@Override
			public void onReceived(TalkServer<UserInfo> server, TalkClient client,
					UserInfo ui, byte[] data, int offset, int length) {

				ReqMsg msg = MsgParser.parse(data, offset, length);
				if (msg == null) {
					return;
				}
				
				if (msg instanceof Login) {
					((Login) msg).client = client;
				}
				
				try {
					msg.perform(ChatServer.this, ui);
				} catch (Exception e) {
					Logger.getInstance().print(TAG, Level.E, e);
				}
			}
			
			@Override
			public void onLeaved(TalkServer<UserInfo> server, TalkClient client,
					UserInfo ui) {
				
			}
		});
	}
}
