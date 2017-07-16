package com.cs.chat.req;

import org.bson.Document;

import com.cs.chat.ChatServer;
import com.cs.chat.ReqMsg;
import com.cs.chat.UserInfor;
import com.cs.chat.rsp.ShowToast;
import com.cs.chat.rsp.UserInfo;
import com.cs.main.DataBaseUtil;
import com.js.talk.TalkClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import net.sf.json.JSONObject;

public class Login extends ReqMsg {
	public TalkClient client;
	
	@Override
	public void perform(ChatServer server, UserInfor ui) throws Exception {
		ui = new UserInfor();
		ui.account = mParams.getString(UserInfor.KEY_ACCOUNT);
		
		synchronized (DataBaseUtil.LOCK) {
			MongoCollection<Document> coll = DataBaseUtil.getDatabase()
				.getCollection(DataBaseUtil.COLL_USERINFO);
			FindIterable<Document> it = coll.find(new BasicDBObject(
				UserInfor.KEY_ACCOUNT, ui.account));
			MongoCursor<Document> cur = it.iterator();
			
			long now = System.currentTimeMillis();
			
			if (cur.hasNext()) {
				Document doc = cur.next();
				cur.close();
				
				ui.id = doc.getInteger(UserInfor.KEY_ID);
				ui.nickname = doc.getString(UserInfor.KEY_NICKNAME);
				ui.createTime = doc.getLong(UserInfor.KEY_CREATE_TIME);
				ui.latestLogin = now;
				doc.put(UserInfor.KEY_LATEST_LOGIN, ui.latestLogin);
				ui.loginTimes = doc.getInteger(UserInfor.KEY_LOGIN_TIMES) + 1;
				doc.put(UserInfor.KEY_LOGIN_TIMES, ui.loginTimes);
				
				coll.replaceOne(Filters.eq(UserInfor.KEY_ID, ui.id), doc);
			} else {
				cur.close();
				
				ui.id = 1;
				
				it = coll.find().sort(new BasicDBObject(UserInfor.KEY_ID, -1)).limit(1);
				cur = it.iterator();
				if (cur.hasNext()) {
					Document doc = cur.next();
					cur.close();
					ui.id = doc.getInteger(UserInfor.KEY_ID) + 1;
				}
				
				ui.createTime = now;
				ui.latestLogin = now;
				ui.loginTimes = 1;
				
				Document doc = new Document();
				doc.put(UserInfor.KEY_ACCOUNT, ui.account);
				doc.put(UserInfor.KEY_ID, ui.id);
				doc.put(UserInfor.KEY_CREATE_TIME, ui.createTime);
				doc.put(UserInfor.KEY_LATEST_LOGIN, ui.latestLogin);
				doc.put(UserInfor.KEY_LOGIN_TIMES, ui.loginTimes);
				
				coll.insertOne(doc);
			}
		}
		
		ui.client = client;
		server.geTalkServer().addUser(ui);
		
		JSONObject jo = new JSONObject();
		
		jo.put(KEY_URI, "UserInfo");
		jo.put(UserInfor.KEY_ACCOUNT, ui.account);
		jo.put(UserInfor.KEY_ID, ui.id);
		jo.put(UserInfor.KEY_NICKNAME, ui.nickname);
		jo.put(UserInfor.KEY_CREATE_TIME, ui.createTime);
		jo.put(UserInfor.KEY_LATEST_LOGIN, ui.latestLogin);
		jo.put(UserInfor.KEY_LOGIN_TIMES, ui.loginTimes);
		
		UserInfo usin = new UserInfo();
		usin.parseFrom(ui);
		usin.send(ui.client);
		
		ShowToast st = new ShowToast();
		st.message = "登录成功";
		st.send(ui.client);
	}
}
