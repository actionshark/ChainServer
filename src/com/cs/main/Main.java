package com.cs.main;

import com.cs.chat.ChatServer;
import com.cs.chat.UserInfo;
import com.js.log.FileLogger;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.network.NetServer;
import com.js.talk.TalkServer;

public class Main {
	public static void main(String[] args) {
		int port = 20001;
		
		String[] logFiles = new String[] {
			"log1.txt", "log2.txt", "log3.txt",
		};
		
		int logLength = 1024 * 1024 * 16;
		
		Level logLevel = Level.D;
		
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			if (arg.equals("--port")) {
				String arg1 = args[++i];
				
				port = Integer.parseInt(arg1);
				
				System.out.println("port is " + port);
			} else if (arg.startsWith("--log_file_")) {
				int count = Integer.parseInt(arg.substring(9));
				logFiles = new String[count];
				
				for (int j = 0; j < count; j++) {
					logFiles[j] = args[++i];
					
					System.out.println(String.format("log file %d : %s", j, logFiles[j]));
				}
			} else if (arg.equals("--log_size")) {
				logLength = Integer.parseInt(args[++i]);
				
				System.out.println(String.format("log file length limit : %d B", logLength));
			} else if (arg.equals("--log_level")) {
				logLevel = Level.valueOf(args[++i]);
				
				System.out.println("log level is " + logLevel.name());
			}
		}
		
		FileLogger fl = new FileLogger();
		fl.setFiles(logFiles);
		fl.setLengthLimit(logLength);
		fl.setLevelLimit(logLevel);
		Logger.setInstance(fl);
		
		NetServer ns = new NetServer();
		ns.setPort(port);
		
		TalkServer<UserInfo> ts = new TalkServer<UserInfo>();
		ts.setServer(ns);
		
		ChatServer cs = new ChatServer();
		cs.setServer(ts);
		cs.start();
	}
}
