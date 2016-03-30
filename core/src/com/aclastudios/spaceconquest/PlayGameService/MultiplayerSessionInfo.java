package com.aclastudios.spaceconquest.PlayGameService;

import java.util.ArrayList;


/**
 * Storage class to store all information needed for google play services in
 * @author Wong
 *
 */
public class MultiplayerSessionInfo {
	
	public String mId;
	public String mIncomingInvitationId;
	public String mRoomId;
	public ArrayList mParticipants;
	public String mName;
	public int mState=1000;

	public boolean isServer;
	public String serverAddress;
	public int serverPort=0;

	
	public final int ROOM_NULL=1000;
	public final int ROOM_WAIT=1001;
	public final int ROOM_PLAY=1002;
	public final int ROOM_MENU=1003;
	
	public MultiplayerSessionInfo(){
	}

//	public MMServer getServer() {
//		return server;
//	}
//
//	public void setServer(MMServer server) {
//		this.server = server;
//	}
//
//	public MMClient getClient() {
//		return client;
//	}
//
//	public void setClient(MMClient client) {
//		this.client = client;
//	}
	
	public void endSession(){
		mId=null;
		mIncomingInvitationId=null;
		mRoomId=null;
		mParticipants=null;
		mState=ROOM_MENU;
		mName = null;

		isServer=false;
		serverAddress=null;
		serverPort=0;

//		server=null;
//		client=null;

	}


}