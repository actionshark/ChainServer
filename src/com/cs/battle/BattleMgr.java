package com.cs.battle;

import java.util.ArrayList;
import java.util.List;

import com.cs.chat.UserInfo;
import com.cs.chat.rsp.BattleEnd;
import com.cs.chat.rsp.BattleStart;
import com.cs.chat.rsp.BattleWaiting;
import com.cs.chat.rsp.ShowToast;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.talk.TalkServer;
import com.js.thread.ThreadUtil;

public class BattleMgr {
	public static final String TAG = BattleMgr.class.getSimpleName();
	
	private static TalkServer<UserInfo> sTalkServer;
	
	private static class RandNode {
		public UserInfo ui;
		public long timeoutPoint;
	}
	
	private static final List<RandNode> sRandWaiting = new ArrayList<RandNode>();
	
	private static final List<Battle> sBattles = new ArrayList<Battle>();
	
	public static synchronized void start(TalkServer<UserInfo> ts) {
		sTalkServer = ts;
		
		ThreadUtil.getVice().run(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (BattleMgr.class) {
						for (int i = sRandWaiting.size() - 1; i >= 0; i--) {
							UserInfo ui = sRandWaiting.get(i).ui;
							
							if (sTalkServer.findUser(ui.id) != ui || ui.getBattle() != null) {
								sRandWaiting.remove(i);
							}
						}
						
						for (int i = sRandWaiting.size() - 1; i > 0 ; i -= 2) {
							UserInfo a = sRandWaiting.get(i).ui;
							UserInfo b = sRandWaiting.get(i - 1).ui;
							
							RandBattle battle = new RandBattle();
							battle.startTime = System.currentTimeMillis();
							
							battle.as.add(a);
							a.setBattle(battle);
							
							battle.bs.add(b);
							b.setBattle(battle);
							
							BattleStart bs = new BattleStart();
							bs.battleType = RandBattle.TAG;
							bs.send(a.client);
							bs.send(b.client);
							
							sRandWaiting.remove(i);
							sRandWaiting.remove(i - 1);
							
							sBattles.add(battle);
						}
						
						for (int i = sRandWaiting.size() - 1; i >= 0; i--) {
							RandNode rn = sRandWaiting.get(i);
							
							if (rn.timeoutPoint < System.currentTimeMillis()) {
								sRandWaiting.remove(i);
								
								BattleEnd be = new BattleEnd();
								be.send(rn.ui.client);
							}
						}
					}
				} catch (Exception e) {
					Logger.getInstance().print(TAG, Level.E, e);
				}
			}
		}, 1000, 1000);
		
		ThreadUtil.getVice().run(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (BattleMgr.class) {
						for (int i = sBattles.size() - 1; i >= 0; i--) {
							Battle battle = sBattles.get(i);
							
							if (System.currentTimeMillis() - battle.startTime > 600000l) {
								sBattles.remove(i);
								
								ShowToast st = new ShowToast();
								st.message = "战斗结束";
								
								for (UserInfo ui : battle.as) {
									if (ui.getBattle() == battle) {
										ui.setBattle(null);
										st.send(ui.client);
									}
								}
								
								for (UserInfo ui : battle.bs) {
									if (ui.getBattle() == battle) {
										ui.setBattle(null);
										st.send(ui.client);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					Logger.getInstance().print(TAG, Level.E, e);
				}
			}
		}, 1000, 1000);
	}
	
	public static synchronized String requestRandBattle(UserInfo ui) {
		if (sTalkServer.findUser(ui.id) != ui) {
			return "您已离线";
		}
		
		if (ui.getBattle() != null) {
			return "已经在战斗中";
		}
		

		for (int i = sRandWaiting.size() - 1; i >= 0; i--) {
			if (sRandWaiting.get(i).ui == ui) {
				return "已经在匹配中";
			}
		}
		
		RandNode rn = new RandNode();
		rn.ui = ui;
		rn.timeoutPoint = System.currentTimeMillis() + 60000;
		sRandWaiting.add(rn);
		
		BattleWaiting bw = new BattleWaiting();
		bw.battleType = RandBattle.TAG;
		bw.timeoutPoint = rn.timeoutPoint;
		bw.send(ui.client);
		
		return null;
	}
	
	public static synchronized void removeRandBattle(UserInfo ui) {
		for (int i = sRandWaiting.size() - 1; i >= 0; i--) {
			if (sRandWaiting.get(i).ui == ui) {
				sRandWaiting.remove(i);
			}
		}
		
		BattleEnd be = new BattleEnd();
		be.send(ui.client);
	}
}
