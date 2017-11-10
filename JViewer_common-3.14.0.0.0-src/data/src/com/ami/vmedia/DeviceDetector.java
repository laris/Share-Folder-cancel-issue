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

package com.ami.vmedia;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;

/**
 * This thread detects whether a device is added or removed at run time,<br>
 * in the client machine, and invokes the methods to update the devices<br>
 * listed in the GUI to reflect the change. 
 * @author deepakmn
 *
 */
public class DeviceDetector extends Thread {
	private static final long SLEEP_TIME = 1000;
	private String[] cdDriveList;
	private String[] hdDriveList;
	private boolean run;
	boolean cdStateFirstTime = JViewer.isCdServiceEnabled();
	boolean hdStateFirstTime = JViewer.isHdServiceEnabled();
	/**
	 * Constructor
	 */
	public DeviceDetector(){
		cdDriveList = VMApp.getInstance().getCDDriveList();
		hdDriveList = VMApp.getInstance().getHDDriveList();
		run = true;
		start();
	}

	/**
	 * Thread implementation
	 */
	public void run(){
		while(run){
			if(VMApp.getVMPane() != null){
				if(VMApp.getVMPane().getVmDevicePane()!= null){
					if(VMApp.getVMPane().getVmDevicePane().isShowing() == true){ // this will avoid unnecessary device detection if vmedia dialog is not shown
						//display read&write mode enabled and fixed drive not possible info in status bar
						
						//Update client machine device list only when particular device tab is selected
						if(VMApp.getVMPane().getVmDevicePane().getSelectedIndex() == VMApp.CD_MEDIA){
							VMApp.getVMPane().showMessageForSelectedTab(VMApp.CD_MEDIA);
							if(JViewer.isCdServiceEnabled()) {
								if(isDeviceDriveListUpdated(VMApp.DEVICE_TYPE_CDROM)){
									VMApp.getVMPane().updateDeviceControlPanel(VMApp.DEVICE_TYPE_CDROM, cdDriveList);
								}
								if(cdStateFirstTime){
									VMApp.getVMPane().updateDeviceControls(VMApp.DEVICE_TYPE_CDROM, true);
									cdStateFirstTime = false;
								}
							}else if (!JViewer.isCdServiceEnabled()){
								if(cdStateFirstTime == false) {
									VMApp.getVMPane().updateDeviceControls(VMApp.DEVICE_TYPE_CDROM, false);
									cdStateFirstTime = true;
								}
							}
						}
						else if(VMApp.getVMPane().getVmDevicePane().getSelectedIndex() == VMApp.HD_MEDIA){
							VMApp.getVMPane().showMessageForSelectedTab(VMApp.HD_MEDIA);
							if(JViewer.isHdServiceEnabled()) {
								if(isDeviceDriveListUpdated(VMApp.DEVICE_TYPE_HD_USB)){
									VMApp.getVMPane().updateDeviceControlPanel(VMApp.DEVICE_TYPE_HD_USB, hdDriveList);
								}
								if(hdStateFirstTime){
									VMApp.getVMPane().updateDeviceControls(VMApp.DEVICE_TYPE_HD_USB, true);
									hdStateFirstTime = false;
								}
							}else if (!JViewer.isHdServiceEnabled()){
								if(hdStateFirstTime == false) {
									VMApp.getVMPane().updateDeviceControls(VMApp.DEVICE_TYPE_HD_USB, false);
									hdStateFirstTime = true;
								}
							}
						}
					}
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						Debug.out.println(e);
					}
				}
			}
		}
	}

	/**
	 * Checks whether a device is newly added to the client or<br>
	 * any existing device is removed from the client
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @return true if any device is newly added in the client, false otherwise.
	 */
	public boolean isDeviceDriveListUpdated(int deviceType){
		boolean update = false;
		String[] newDeviceDriveList = null;
		String[] oldDeviceDriveList = null;
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			oldDeviceDriveList = cdDriveList;
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			oldDeviceDriveList = hdDriveList;
			break;
		}
		//Get the current drives list and compare it with the existing list.
		newDeviceDriveList = VMApp.getInstance().getDeviceDriveList(deviceType);
		if(oldDeviceDriveList != null || newDeviceDriveList != null){
			try {
				//if both current drive list and new drive list has the same size
				//check the individual elements to find the difference.
				if(newDeviceDriveList.length == oldDeviceDriveList.length){
					for(int index = 0; index < oldDeviceDriveList.length; index++){
						//if a difference is found, 
						if(!(oldDeviceDriveList[index].equals(newDeviceDriveList[index]))){
							update = true;
							updateDeviceDriveList(deviceType, newDeviceDriveList);
							break;
						}
					}
				}
				//if the current drive list and new drive list has different size
				else{
					update = true;
					updateDeviceDriveList(deviceType, newDeviceDriveList);
				}
			} catch (ArrayIndexOutOfBoundsException aiobe) {
				update = true;
				updateDeviceDriveList(deviceType, newDeviceDriveList);
			}catch (NullPointerException npe) {
				update = true;
				updateDeviceDriveList(deviceType, newDeviceDriveList);
			}
		}
		return update;
	}

	/**
	 * Updates the existing drives list with the new drives list.
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param newDeviceDriveList - the new drives list.
	 */
	private void updateDeviceDriveList(int deviceType, String[] newDeviceDriveList){
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			cdDriveList = newDeviceDriveList;
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			hdDriveList = newDeviceDriveList;
			break;
		}
	}

	/**
	 * Stops the thread
	 */
	public void stopDeviceDetector(){
		run = false;
	}
}