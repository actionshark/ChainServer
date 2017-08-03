package com.cs.chat.req;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.cs.chat.ChatServer;
import com.cs.chat.ReqMsg;
import com.cs.chat.UserInfo;
import com.cs.main.DataBaseUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class UploadLineup extends ReqMsg {
	@Override
	public void perform(ChatServer server, UserInfo ui) throws Exception {
		String data = mParams.getString("data");
		Document doc = new Document();
		doc.put("data", data);
		
		MongoCollection<Document> coll = DataBaseUtil.getDatabase().getCollection(
			DataBaseUtil.COLL_LINEUP);
		
		FindIterable<Document> fi = coll.find(new BasicDBObject(UserInfo.KEY_ID, ui.id));
		MongoCursor<Document> cur = fi.iterator();
		
		if (cur.hasNext()) {
			Bson filter = new BasicDBObject(UserInfo.KEY_ID, ui.id);
			Document update = new Document();
			update.append("$set", doc);
			coll.updateOne(filter, update);
		} else {
			doc.put(UserInfo.KEY_ID, ui.id);
			coll.insertOne(doc);
		}
		
		cur.close();
	}
}
