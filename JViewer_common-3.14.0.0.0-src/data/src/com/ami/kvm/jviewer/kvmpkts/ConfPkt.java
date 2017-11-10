/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2009, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
****************************************************************/


////////////////////////////////////////////////////////////////////////////////
//ConfPkt class gets service configuration data in the structure obtained 
//from adviser, and stores the data into the class members. 

package com.ami.kvm.jviewer.kvmpkts;

import java.awt.Component;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.ami.iusb.CDROMRedir;
import com.ami.iusb.HarddiskRedir;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.InfoDialog;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;

public class ConfPkt {
	public static final int  NAME_MAX_LENGTH		= 17;

	private char[] serviceName = new char[NAME_MAX_LENGTH];
	private byte currentState;
	private char[] interfaceName = new char[NAME_MAX_LENGTH];
	private int nonSecureAccessPort;
	private int secureAccessPort;
	private int sessionInactivityTimeout;
	private byte maxAllowedSessions;
	private byte currentActiveSession;
	private int maxSessionInactivityTimeout;
	private int minSessionInactivityTimeout;
	
	public static final String SERV_CD_MEDIA="cd-media";
	public static final String SERV_HD_MEDIA="hd-media";

	public static final int CONF_PKT_SIZE			= 57;
	public static final short CONF_SERVICE_COUNT		= 5;
	
	public static final short CHANGE_CURR_STATE 		= 0;
	public static final short CHANGE_IFACE_NAME 		= 1;
	public static final short CHANGE_NON_SEC_PORT 		= 2;
	public static final short CHANGE_SEC_PORT 		= 3;
	public static final short CHANGE_TIME_OUT 		= 4;
	public static final short CHANGE_MAX_SESSION 		= 5;
	public static final short CHANGE_CURR_ACTIVE_SESS 	= 6;
	public static final short CHANGE_MAX_TIME_OUT 		= 7;
	public static final short CHANGE_MIN_TIME_OUT 		= 8;
	
	public static boolean confModified = false;
	public ConfPkt(byte[] dataBuf){
		ByteBuffer dataBuffer = ByteBuffer.wrap(dataBuf);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byte[] temp = new byte[NAME_MAX_LENGTH];
		dataBuffer.get(temp);
		serviceName = new String(temp).toCharArray();
		currentState = dataBuffer.get();
		dataBuffer.get(temp);
		interfaceName = new String(temp).toCharArray();
		nonSecureAccessPort = dataBuffer.getInt();
		secureAccessPort = dataBuffer.getInt();
		sessionInactivityTimeout = dataBuffer.getInt();
		maxAllowedSessions = dataBuffer.get();
		currentActiveSession = dataBuffer.get();
		maxSessionInactivityTimeout = dataBuffer.getInt();
		minSessionInactivityTimeout = dataBuffer.getInt();
		
	}	

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return new String(serviceName).trim();
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(char[] serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the currentState
	 */
	public byte getCurrentState() {
		return currentState;
	}

	/**
	 * @param currentState the currentState to set
	 */
	public void setCurrentState(byte currentState) {
		this.currentState = currentState;
	}

	/**
	 * @return the interfaceName
	 */
	public String getInterfaceName() {
		return new String(interfaceName).trim();
	}

	/**
	 * @param interfaceName the interfaceName to set
	 */
	public void setInterfaceName(char[] interfaceName) {
		this.interfaceName = interfaceName;
	}

	/**
	 * @return the nonSecureAccessPort
	 */
	public int getNonSecureAccessPort() {
		return nonSecureAccessPort;
	}

	/**
	 * @param nonSecureAccessPort the nonSecureAccessPort to set
	 */
	public void setNonSecureAccessPort(int nonSecureAccessPort) {
		this.nonSecureAccessPort = nonSecureAccessPort;
	}

	/**
	 * @return the secureAccessPort
	 */
	public int getSecureAccessPort() {
		return secureAccessPort;
	}

	/**
	 * @param secureAccessPort the secureAccessPort to set
	 */
	public void setSecureAccessPort(int secureAccessPort) {
		this.secureAccessPort = secureAccessPort;
	}

	/**
	 * @return the sessionInactivityTimeout
	 */
	public int getSessionInactivityTimeout() {
		return sessionInactivityTimeout;
	}

	/**
	 * @param sessionInactivityTimeout the sessionInactivityTimeout to set
	 */
	public void setSessionInactivityTimeout(int sessionInactivityTimeout) {
		this.sessionInactivityTimeout = sessionInactivityTimeout;
	}

	/**
	 * @return the maxAllowedSessions
	 */
	public byte getMaxAllowedSessions() {
		return maxAllowedSessions;
	}

	/**
	 * @param maxAllowedSessions the maxAllowedSessions to set
	 */
	public void setMaxAllowedSessions(byte maxAllowedSessions) {
		this.maxAllowedSessions = maxAllowedSessions;
	}

	/**
	 * @return the currentActiveSession
	 */
	public byte getCurrentActiveSession() {
		return currentActiveSession;
	}

	/**
	 * @param currentActiveSession the currentActiveSession to set
	 */
	public void setCurrentActiveSession(byte currentActiveSession) {
		this.currentActiveSession = currentActiveSession;
	}

	/**
	 * @return the maxSessionInactivityTimeout
	 */
	public int getMaxSessionInactivityTimeout() {
		return maxSessionInactivityTimeout;
	}

	/**
	 * @param maxSessionInactivityTimeout the maxSessionInactivityTimeout to set
	 */
	public void setMaxSessionInactivityTimeout(int maxSessionInactivityTimeout) {
		this.maxSessionInactivityTimeout = maxSessionInactivityTimeout;
	}

	/**
	 * @return the minSessionInactivityTimeout
	 */
	public int getMinSessionInactivityTimeout() {
		return minSessionInactivityTimeout;
	}	

	/**
	 * @param minSessionInactivityTimeout the minSessionInactivityTimeout to set
	 */
	public void setMinSessionInactivityTimeout(int minSessionInactivityTimeout) {
		this.minSessionInactivityTimeout = minSessionInactivityTimeout;
	}

	/**
	 * Shows the message dialog when there is any change in a Service conf data.
	 * @param message
	 */
	public void showConfDataChangeMsg(String message) {
		// TODO Auto-generated method stub
		confModified = true;
		JViewerApp.getInstance().syncVMediaRedirection();
		
		Component parent = JViewer.getMainFrame();
		//If VMedia dialog is opened, then set it as the parent component for the InfoDialog
		if(JViewerApp.getInstance().getVMDialog() != null)
			parent = JViewerApp.getInstance().getVMDialog();
		//If VideoRecordSettings dialog is opened, then set it as the parent component for the InfoDialog
		else if(JViewerApp.getInstance().getM_videorecord()!= null){
			if(JViewerApp.getInstance().getM_videorecord().getM_videoPath() != null)
				parent = JViewerApp.getInstance().getM_videorecord().getM_videoPath();
		}
		String serviceName = getServiceName();
		InfoDialog.showDialog(parent, serviceName+LocaleStrings.getString("X_1_CP")+"\n"+message+
				LocaleStrings.getString("X_2_CP"),LocaleStrings.getString("X_3_CP"),
				InfoDialog.INFORMATION_DIALOG);
		if(serviceName.equalsIgnoreCase("kvm") || serviceName.equalsIgnoreCase("web"))
			JViewerApp.getInstance().getM_frame().windowClosed();
		
		synchronized (CDROMRedir.getSyncObj()) {
			CDROMRedir.getSyncObj().notify();
		}
		synchronized (HarddiskRedir.getSyncObj()) {
			HarddiskRedir.getSyncObj().notify();
		}
		
	}
	
}
