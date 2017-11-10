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
// SystemTimeMonitor thread class will monitor the system time. It will check for every 5 seconds, whether the system
// time is modified, and if so it will close the application showing an error message.
//
package com.ami.kvm.jviewer.gui;

import java.util.Date;

import javax.swing.JOptionPane;

import com.ami.kvm.jviewer.Debug;

public class SystemTimeMonitor extends Thread{
	
	private static boolean timeChanged = false;
	private Date currentTime = null;
	private Date pastTime = null;	
	private final long SLEEP_TIME = 5000;
	//Sleep time shift due to thread scheduling problems.
	private final long SLEEP_TIME_SHIFT = 1000;
	public void run(){
		while(true){
			currentTime = new Date(System.currentTimeMillis());
			if(pastTime != null){
				long timeDeviation = currentTime.getTime() - pastTime.getTime();	
				// deviation should be around 5000 millisecond(5 sec).
				// + or - 1000 millisecs(1 sec) variation is considered to avoid 
				// inconsistencies which are caused due to java thread scheduling problems.			
				if(timeDeviation <= (SLEEP_TIME - SLEEP_TIME_SHIFT) || timeDeviation >= (SLEEP_TIME + SLEEP_TIME_SHIFT) ){
					//Set time changed flag as true.
					timeChanged = true;
					//Set redirection status as REIDR_STOPPED, this will avoid unwanted message dialogs, while window closing.
					JViewerApp.getInstance().setRedirectionStatus(JViewerApp.REDIR_STOPPED); 
					// Exit the application.
					JViewerApp.getInstance().getMainWindow().windowClosed();
				}
			}
			try {
				Thread.sleep(SLEEP_TIME);//5 second sleep
			} catch (InterruptedException e) {
				//If interrupted from sleep, catch excexption and continue.
				Debug.out.println("Thread.Sleep interrupted\n"+e);
			}
			pastTime = currentTime;
		}
	}
	/**
	 * @return the timeChanged
	 */
	public static boolean isTimeChanged() {
		return timeChanged;
	}
}
