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

///////////////////////////////////////////////////////////////////////////////
//This module monitors the power status when the host is shut down
//

package com.ami.kvm.jviewer.gui;

import java.util.TimerTask;

import com.ami.kvm.jviewer.Debug;

public class PowerStatusMonitor extends TimerTask{
	private boolean monitorRunning = false;	
	private final Object syncObj = new Object();
	public static final long WAIT_TIME = 30000;//(30 seconds)in milliseconds
	/**
	 * @return the monitorRunning
	 */
	public boolean isMonitorRunning() {
		return monitorRunning;
	}
	/**
	 * @return the syncObj
	 */
	public Object getSyncObj() {
		return syncObj;
	}
	@Override
	public void run() {
		// On blank screen.
		if(JViewerApp.getInstance().getKVMClient().m_isBlank){
			// Send power power status request to the adviser, to get the server power status. 
			JViewerApp.getInstance().getKVMClient().sendPowerStatusRequest();
		}
		monitorRunning = true;
		// Wait for 30 seconds, or until notified by Video Drawing thread when a new frame arrives.
		try
		{
			synchronized(getSyncObj()) {
				getSyncObj().wait(WAIT_TIME);
			}
		} catch (InterruptedException ie) {
			// TODO Auto-generated catch block
			Debug.out.println("PowerStatusMonitor wait Interrupted");
			Debug.out.println(ie);
		}
		//On new screen.
		if(JViewerApp.getInstance().getVidClnt().isNewFrame()){	
			monitorRunning = false;
			// Cancel the PowerStatusMonitor timer task.
			this.cancel();
			if(JViewerApp.getInstance().getVidClnt().getPsMonitorTimer() != null)
				// Cancel the timer which schedules this timer task.
				JViewerApp.getInstance().getVidClnt().getPsMonitorTimer().cancel();
			//set the power status in JViewer to SERVER_POWER_ON.
			JViewerApp.getInstance().onGetPowerControlStatus(JViewerApp.getInstance().SERVER_POWER_ON);	
		}    		
	}
}
