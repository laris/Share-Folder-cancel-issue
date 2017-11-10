package com.ami.kvm.jviewer.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.TimerTask;
import javax.swing.ImageIcon;

import com.ami.iusb.IUSBRedirSession;
import com.ami.kvm.jviewer.ClientConfig;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.common.ISOCFrameHdr;
import com.ami.vmedia.VMApp;

public class FrameRateTask extends TimerTask {
	private static final int FETCH_RES_DELAY = 3;
	Dimension Client_System_prev;
	Dimension Client_System;
	boolean updateLEDStatus = false;
	boolean syncLEDFromClient = false;
	byte ledStatus = 0;
	int resWait = 0;
	@Override
	public void run() {
		if(resWait > 0)
			resWait--;
		try{
			JViewerApp.getInstance().setAppWndLabel(JViewerApp.getInstance().getVidClnt().getM_frameRate() + " fps");
			JViewerApp.getInstance().getVidClnt().setM_frameRate(0);
		} catch(Exception e)
		{
			Debug.out.println(e);
		}
		
		ISOCFrameHdr m_frameHdr = JViewerApp.getInstance().getSocframeHdr();
		//Thread runs with a one second delay. So the current screen resolution will be fetched once in
		//FETCH_RES_DELAY seconds.
		if(resWait == 0){
			Client_System = JViewerApp.getInstance().getCurrentMonitorResolution();
			resWait = FETCH_RES_DELAY;
		}

		if(Client_System_prev == null)
			Client_System_prev = Client_System;

		if(Client_System.width != Client_System_prev.width || Client_System.height != Client_System_prev.height) {
			JViewerApp.getInstance().getVidClnt().setFullScreenMode();// Set the fullscreen menu and toolbar icon status.
			JViewerApp.getInstance().setResolutionChanged(1);
			//Set zoom options when clint resolution is changed.
			JViewerApp.getInstance().getVidClnt().setZoomOptionStatus();
		}
		Client_System_prev = Client_System;

		/* While trying to set the client keyboard LED status from the main thread,
		 * during focus lost event, the GUI thread will block the focus change.
		 * The focus shift can't be achieved if the Alt+Tab key combination is used.
		 * So updating the keyboard LED status is moved to this thread.
		 */
		if(updateLEDStatus)
		{
			ClientConfig clientCfg = new ClientConfig();
			try{
				clientCfg.setKeyboardLEDStatus(ledStatus);
			}catch(Exception e){
				Debug.out.println(e);
			}finally{
				clientCfg = null;
			}
		}
		//Set the flag to false so that the LED status is not toggled continuously.
		updateLEDStatus = false;
		if(syncLEDFromClient){
			JViewerApp.getInstance().sendClientLEDStatus();
			syncLEDFromClient = false;
		}
	}

	/**
	 * Updates the LED status value and sets the flag which will cause the keyboard 
	 * LED status to be set in the client.
	 * @param ledStatus - the led status to be updated. 
	 */
	public void updateKeyboardLEDStatus(byte ledStatus){
		updateLEDStatus = true;
		this.ledStatus = ledStatus;
	}

	/**
	 * Gets the status of the flag which denotes whether to sync keyboard LED
	 * from client with the server.
	 * @return the syncLEDFromClient
	 */
	public boolean isSyncLEDFromClient() {
		return syncLEDFromClient;
	}

	/**
	 * Sets the flag which denotes whether to sync keyboard LED
	 * from client with the server.  
	 * @param syncLEDFromClient the syncLEDFromClient to set
	 */
	public void setSyncLEDFromClient(boolean syncLEDFromClient) {
		this.syncLEDFromClient = syncLEDFromClient;
	}
}
