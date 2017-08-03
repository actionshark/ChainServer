package com.cs.chat.req;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.cs.chat.ChatServer;
import com.cs.chat.ReqMsg;
import com.cs.chat.UserInfo;
import com.cs.chat.rsp.ShowToast;
import com.cs.chat.rsp.PushUserInfo;
import com.cs.main.DataBaseUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class EditNickname extends ReqMsg {
	public static final int MIN_LEN = 1;
	public static final int MAX_LEN = 20;

	@Override
	public void perform(ChatServer server, UserInfo ui) throws Exception {
		String nickname = mParams.getString(UserInfo.KEY_NICKNAME);
		if (nickname == null || nickname.length() < MIN_LEN || nickname.length() > MAX_LEN) {
			ShowToast st = new ShowToast();
			st.message = String.format("昵称长度为%d到%d", MIN_LEN, MAX_LEN);
			st.send(ui.client);
			return;
		}

		MongoCollection<Document> coll = DataBaseUtil.getDatabase().getCollection(
			DataBaseUtil.COLL_USERINFO);

		FindIterable<Document> fi = coll.find(new BasicDBObject(UserInfo.KEY_NICKNAME, nickname));

		MongoCursor<Document> cur = fi.iterator();

		if (cur.hasNext()) {
			cur.close();

			ShowToast st = new ShowToast();
			st.message = "昵称已被占用";
			st.send(ui.client);
			return;
		}

		Bson filter = new BasicDBObject(UserInfo.KEY_ID, ui.id);
		Document update = new Document();
		update.append("$set", new Document(UserInfo.KEY_NICKNAME, nickname));
		coll.updateOne(filter, update);
		
		ui.nickname = nickname;
		
		PushUserInfo usin = new PushUserInfo();
		usin.parseFrom(ui);
		usin.send(ui.client);
		
		ShowToast st = new ShowToast();
		st.message = "修改昵称成功";
		st.send(ui.client);
	}
}
