package com.cs.main;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DataBaseUtil {
	private static MongoClient sClient;
	private static MongoDatabase sDatabase;
	
	public static final String COLL_USERINFO = "userinfo";
	public static final String COLL_LINEUP = "lineup";
	
	public static final Object LOCK = new Object();
	
	static {
		sClient = new MongoClient("127.0.0.1", 27017); 
		sDatabase = sClient.getDatabase("chain");
	}
	
	public static MongoDatabase getDatabase() {
		return sDatabase;
	}
}
