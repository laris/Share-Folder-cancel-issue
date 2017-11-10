package com.ami.kvm.jviewer.kvmpkts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import com.ami.kvm.jviewer.gui.JViewerApp;

public class UserDataPacket {
	public static final int USER_DATA_LENGTH = 134;//64+65+1+4(UserName+ClientIP+SessionID+ipmi priv)
	public static final int USER_NAME_LENGTH = 64;
	public static final int CLIENT_IP_LENGTH = 65;//64 + 1 for null termination
	private UserData userData = null;
	private Hashtable<String, UserData> userDataTable = null;
	private int numUsers = 1;
	private String[] userInfo = null;
	
	public UserDataPacket(ByteBuffer userListBuffer){
		byte[] tempBuffer = new byte[USER_DATA_LENGTH];
		String keyString = null;
		
		numUsers = userListBuffer.capacity()/USER_DATA_LENGTH;
		userDataTable = new Hashtable<String, UserData>();
		userInfo = new String[numUsers];
		
		for(int count = 0; count < numUsers; count++){
			userListBuffer.get(tempBuffer);
			userData = new UserData(tempBuffer);
			keyString = userData.getSessionIndex()+ " : " +userData.getUserName() +"( "+ JViewerApp.getInstance().getIpmiPrivText(userData.getIpmiPriv())+") : " + userData.getClientIP();
			userDataTable.put(keyString, userData);
			userInfo[count] = keyString;
		}		
	}
	
	/**
	 * @return the userDataTable
	 */
	public Hashtable<String, UserData> getUserDataTable() {
		return userDataTable;
	}

	/**
	 * @param userDataTable the userDataTable to set
	 */
	public void setUserDataTable(Hashtable<String, UserData> userDataTable) {
		this.userDataTable = userDataTable;
	}

	/**
	 * @return the userInfo
	 */
	public String[] getUserInfo() {
		return userInfo;
	}
	
	/**
	 * @return the numUsers
	 */
	public int getNumUsers() {
		return numUsers;
	}
	
	public UserData getUserData(String keyString){
		UserData usrData = userDataTable.get(keyString);
		return usrData;
	}

	public ByteBuffer createUserDataBuffer(String keyString){
		ByteBuffer masterDataBuffer = null;
		byte[] tempBuffer = null;
		int pos = -1;
		userData = userDataTable.get(keyString);
		tempBuffer = new byte[USER_NAME_LENGTH];
		tempBuffer = userData.getUserName().getBytes();
		masterDataBuffer = ByteBuffer.allocate(USER_DATA_LENGTH);
		masterDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		masterDataBuffer.position(0);
		masterDataBuffer.put(tempBuffer);
		for(pos = masterDataBuffer.position(); pos < USER_NAME_LENGTH; pos++)
			masterDataBuffer.put((byte) 0);
		tempBuffer = new byte[CLIENT_IP_LENGTH];
		tempBuffer = userData.getClientIP().getBytes();
		masterDataBuffer.put(tempBuffer);
		for(pos = masterDataBuffer.position(); pos < (USER_NAME_LENGTH + CLIENT_IP_LENGTH); pos++)
			masterDataBuffer.put((byte) 0);
		masterDataBuffer.put((byte) userData.getSessionIndex());
		masterDataBuffer.rewind();
		return masterDataBuffer;
	}

	class UserData{
		private String userName;
		private String clientIP;
		private int sessionIndex;
		private int ipmiPriv;
		public UserData(byte[] userData){
			ByteBuffer userDataBuffer = null;
			byte[] tempBuffer = new byte[USER_NAME_LENGTH];
			userDataBuffer = ByteBuffer.wrap(userData);
			userDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
			userDataBuffer.get(tempBuffer);
			userName = new String(tempBuffer).trim();
			tempBuffer = new byte[CLIENT_IP_LENGTH];
			userDataBuffer.get(tempBuffer);
			clientIP = new String(tempBuffer).trim();
			sessionIndex = userDataBuffer.get();
			ipmiPriv = userDataBuffer.getInt();
		}
		
		/**
		 * @return the userName
		 */
		public String getUserName() {
			return userName;
		}

		/**
		 * @return the clientIP
		 */
		public String getClientIP() {
			return clientIP;
		}

		/**
		 * @return the sessionIndex
		 */
		public int getSessionIndex() {
			return sessionIndex;
		}
		/**
		 * @return the ipmiPriv
		 */
		public int getIpmiPriv() {
			return ipmiPriv;
		}	
	}
}
