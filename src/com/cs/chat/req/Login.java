package com.cs.chat.req;

import org.bson.Document;

import com.cs.chat.ChatServer;
import com.cs.chat.ReqMsg;
import com.cs.chat.UserInfo;
import com.cs.chat.rsp.ShowToast;
import com.cs.chat.rsp.PushUserInfo;
import com.cs.main.DataBaseUtil;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.talk.TalkClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class Login extends ReqMsg {
	public static final String TAG = "Login";
	
	public TalkClient client;
	
	@Override
	public void perform(ChatServer server, UserInfo ui) throws Exception {
		ui = new UserInfo();
		ui.account = mParams.getString(UserInfo.KEY_ACCOUNT);
		
		synchronized (DataBaseUtil.LOCK) {
			MongoCollection<Document> coll = DataBaseUtil.getDatabase()
				.getCollection(DataBaseUtil.COLL_USERINFO);
			FindIterable<Document> it = coll.find(new BasicDBObject(
				UserInfo.KEY_ACCOUNT, ui.account));
			MongoCursor<Document> cur = it.iterator();
			
			long now = System.currentTimeMillis();
			
			if (cur.hasNext()) {
				Document doc = cur.next();
				cur.close();
				
				ui.id = doc.getInteger(UserInfo.KEY_ID);
				
				UserInfo oui = server.geTalkServer().findUser(ui.id);
				if (oui != null) {
					if (oui.client == client) {
						Logger.getInstance().print(TAG, Level.D,
							String.format("user %d has login alreay", ui.id));
						return;
					} else {
						server.geTalkServer().removeUser(ui.id);
					}
				}
				
				ui.nickname = doc.getString(UserInfo.KEY_NICKNAME);
				ui.createTime = doc.getLong(UserInfo.KEY_CREATE_TIME);
				ui.latestLogin = now;
				doc.put(UserInfo.KEY_LATEST_LOGIN, ui.latestLogin);
				ui.loginTimes = doc.getInteger(UserInfo.KEY_LOGIN_TIMES) + 1;
				doc.put(UserInfo.KEY_LOGIN_TIMES, ui.loginTimes);
				
				coll.replaceOne(Filters.eq(UserInfo.KEY_ID, ui.id), doc);
			} else {
				cur.close();
				
				ui.id = 1;
				
				it = coll.find().sort(new BasicDBObject(UserInfo.KEY_ID, -1)).limit(1);
				cur = it.iterator();
				if (cur.hasNext()) {
					Document doc = cur.next();
					cur.close();
					ui.id = doc.getInteger(UserInfo.KEY_ID) + 1;
				}
				
				ui.createTime = now;
				ui.latestLogin = now;
				ui.loginTimes = 1;
				
				Document doc = new Document();
				doc.put(UserInfo.KEY_ACCOUNT, ui.account);
				doc.put(UserInfo.KEY_ID, ui.id);
				doc.put(UserInfo.KEY_CREATE_TIME, ui.createTime);
				doc.put(UserInfo.KEY_LATEST_LOGIN, ui.latestLogin);
				doc.put(UserInfo.KEY_LOGIN_TIMES, ui.loginTimes);
				
				coll.insertOne(doc);
			}
		}
		
		ui.client = client;
		server.geTalkServer().addUser(ui);
		
		PushUserInfo usin = new PushUserInfo();
		usin.parseFrom(ui);
		usin.send(ui.client);
		
		ShowToast st = new ShowToast();
		st.message = "登录成功";
		st.send(ui.client);
	}
}
