/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2015, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
 ****************************************************************/

package com.ami.vmedia.gui;

import java.util.TimerTask;

import javax.swing.JTable;

import com.ami.iusb.IUSBRedirSession;
import com.ami.vmedia.VMApp;

/**
 * This class creates a timer task which will run like a thread and<br>
 * updates the number of bytes read during device redirection.
 * @author deepakmn
 *
 */
public class UpdateBytesRead extends TimerTask{
	private final int BYTES_READ_COL = 3;
	private IUSBRedirSession iusbRedirSession;
	private JTable statusTable;
	private JTable devStatusTable;

	/**
	 * Tread implementation.
	 */
	@Override
	public void run() {
		iusbRedirSession = VMApp.getInstance().getIUSBRedirSession();
		updateReadBytes();
	}

	/**
	 * Gets the number of bytes read during the redirection of the devices<br>
	 * and updates the value in the status table for each device. 
	 */
	public void updateReadBytes()
	{
		statusTable = VMApp.getVMPane().getStatusTabPanel().getCDStatusTable().getStatusTable();
		devStatusTable = VMApp.getVMPane().getCDStatusTable();
		for(int index=0;index<VMApp.getInstance().getNumCD();index++) {
			if(iusbRedirSession.cdromSession[index] != null){
				if( iusbRedirSession.getCDROMRedirStatus(index) == IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED ){
					statusTable.setValueAt(iusbRedirSession.getCDROMReadBytes(index)+" "+"KB", index, BYTES_READ_COL);
					devStatusTable.setValueAt(iusbRedirSession.getCDROMReadBytes(index)+" "+"KB", index, BYTES_READ_COL);
				}
			}
		}
		statusTable = VMApp.getVMPane().getStatusTabPanel().getHDStatusTable().getStatusTable();
		devStatusTable = VMApp.getVMPane().getHDStatusTable();
		for(int index=0;index<VMApp.getInstance().getNumHD();index++){
			if(iusbRedirSession.hardDiskSession[index] != null){
				if( iusbRedirSession.getHarddiskRedirStatus(index) == IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED ){
					statusTable.setValueAt(iusbRedirSession.getHarddiskReadBytes(index)+" "+"KB", index, BYTES_READ_COL);
					devStatusTable.setValueAt(iusbRedirSession.getHarddiskReadBytes(index)+" "+"KB", index, BYTES_READ_COL);
				}
			}
		}
	}

}
