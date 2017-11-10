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

import com.ami.iusb.IUSBRedirSession;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.vmedia.gui.DevicePanel;

/**
 * This class takes care of the redirection operation of the CD and HD/USB <br>
 * devices and images
 * @author deepakmn
 *
 */
public class RedirectionController implements Runnable {
	private String sessionToken = null;
	private int					cdPort;
	private int					hdPort;
	private boolean				useSSL;
	// For accessing deviceType and deviceIndex values inside Thread
	private int deviceType;
	private int deviceIndex;
	private static String SHAREFOLDERSPLITSTRING = " : ";

	/**
	 * Constructor
	 */
	public RedirectionController(){
		cdPort = JViewer.getCDPort();
		hdPort = JViewer.getHDPort();
		useSSL = JViewer.isVMUseSSL();
		deviceType = deviceIndex = -1;
	}

	/**
	 * update the redirection Status for CD and HD/USB Devices and images.
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param NumberOfDevice - total no of device supported for the given deviceType.	 * 
	 */
	public void updateRedirectionStatus(int deviceType, int NumberOfDevice)
	{
		int deviceIndex=0;
		boolean status=false;
		for(deviceIndex=0; deviceIndex< NumberOfDevice; deviceIndex++)
		{
			switch (deviceType) {
			case VMApp.DEVICE_TYPE_CDROM:
				status = JViewerApp.getInstance().IsCDROMRedirRunning(deviceIndex);
				break;
			case VMApp.DEVICE_TYPE_HD_USB:
				status = JViewerApp.getInstance().IsHarddiskRedirRunning(deviceIndex);
				break;
			default :
				Debug.out.printError("Invalid Device type");
				return;
			}
			VMApp.getVMPane().getStatusTabPanel().updateStatusTable(deviceType, deviceIndex,status);
			VMApp.getVMPane().updateDeviceStatusTable(deviceType, deviceIndex, status);
			VMApp.getVMPane().getDeviceControlPanel(deviceType, deviceIndex).updateDeviceControlPanel();
		}
	}

	/**
	 * Handles the redirection for CD, and HD/USB Devices and images.
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param deviceIndex - the device instance number.
	 */
	public void handleDeviceRedirection(int deviceType, int deviceIndex) {
		// Update values so that it can be read by thread
		this.deviceIndex = deviceIndex;
		this.deviceType = deviceType;
		// Spawned thread will get terminated as soon as it's execution
		// completes.
		//
		// Since we are preventing user from clicking the button again when an
		// connection request is already in active, there won't be a possibility
		// of this method is called again before the thread finishes processing.
		// This will eliminate the necessity of introducing new flags to monitor
		// thread execution.
		new Thread(VMApp.getInstance().getRedirectionController(), "VMApp redirectionController").start();
	}

	/**
	 * Enables / Disables connect button in all the CD, and HD/USB tabs to
	 * prevent user from interrupting controls during operation
	 * 
	 * @param status - Boolean value. Enables connect button if true. Disables otherwise.
	 */
	private void updateConnectButton(boolean status) {
		int[] deviceType = { VMApp.DEVICE_TYPE_CDROM, VMApp.DEVICE_TYPE_HD_USB } /* Type of device */,
				devCount = { VMApp.getInstance().getNumCD(), VMApp.getInstance().getNumHD() } /* Device instance count */;
		int devCountIndex = 0; /* for manipulating devCount array */
		for (int devType : deviceType) {
			for (int deviceIndex = 0; deviceIndex < devCount[devCountIndex]; deviceIndex++) {
				VMApp.getVMPane().getDeviceControlPanel(devType, deviceIndex).getConnectButton().setEnabled(status);
			}
			devCountIndex++;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 * 
	 * When the connect button in VMedia Dialog is clicked, the actionPerformed
	 * method of VMActionListener is triggered on the Event Dispatch Thread.
	 * This thread is the one and only thread responsible for the UI. Since
	 * connection establishment code execution takes so long, it blocks the
	 * event dispatch thread and prevents it from doing its job. ( Such as
	 * dispatching other events, and painting the UI )
	 * 
	 * This effectively freezes the GUI until the completion of long running
	 * code. So executing the long-running piece of code in a separate thread.
	 */
	// @Override
	public void run() {
		IUSBRedirSession iUSBSession = VMApp.getInstance().getIUSBRedirSession();
		sessionToken = JViewer.getKVMToken();
		boolean bRet = false, isPhysical = false;
		DevicePanel devPanel = VMApp.getVMPane().getDeviceControlPanel(deviceType, deviceIndex).getDevicePanel();
		String source = null;
		int port = 0;

		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			port = cdPort;
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			port = hdPort;
			break;
		}

		if (devPanel.isImageSelected()) {
			Debug.out.println("Starting image redirection " + devPanel.getImagePath());
			isPhysical = false;
			source = devPanel.getImagePath();
		}
		else if (devPanel.isFolderMountSelected()) {
			isPhysical = false;
			//combine size, folder path and temp image path, and using special string " : " to split them
			source = devPanel.sizeTxt.getText() + SHAREFOLDERSPLITSTRING + devPanel.folderTxt.getText() + SHAREFOLDERSPLITSTRING + devPanel.imageTxt.getText();
		}
		else{
			Debug.out.println("Starting device redirection "+devPanel.getSelectedDeviceString());
			isPhysical = true;
			source = devPanel.getSelectedDeviceString();
		}

		updateConnectButton(false); // Disable all the connect / disconnect
									// buttons to prevent user interrupt during
									// operation

		if (iUSBSession.getDeviceRedirStatus(deviceType, deviceIndex) == IUSBRedirSession.DEVICE_REDIR_STATUS_IDLE) {

			// Disable all the controls to disable interrupting during operation
			devPanel.disableAll();
			// Connecting....
			VMApp.getVMPane().getDeviceControlPanel(deviceType, deviceIndex).getConnectButton().setText(LocaleStrings.getString("AJ_7_DCP"));
			bRet = iUSBSession.startDeviceRedirection(deviceType, sessionToken, port, deviceIndex, useSSL, source,
					isPhysical);
			if (bRet) {
				// Save the image file path list when image files are
				// redirected.
				if (!isPhysical) {
					String[] pathList = devPanel.getImagePathList();
					devPanel.saveImagePath(pathList, deviceType, deviceIndex);
				} else {
					// Disable same device listed in other instances
					VMApp.getVMPane().physicalDriveChangeState(deviceType, deviceIndex, source, false);
				}
				/*
				 * Set the image redirection status. If physical device is
				 * redirected (isPhysical == TRUE), the image redirection status
				 * if set to FALSE(!isPhysical). If image file is redirected
				 * (isPhysical == FALSE), the image redirection status if set to
				 * TRUE(!isPhysical).
				 */
				iUSBSession.setImageRedirected(deviceType, deviceIndex, !(isPhysical));
				VMApp.getVMPane().getStatusTabPanel().updateStatusTable(deviceType, deviceIndex, true);
				VMApp.getVMPane().updateDeviceStatusTable(deviceType, deviceIndex, true);
			}
			// enable all controls back.
			devPanel.enableAll();
			Debug.out.println("redirection should be running");
		} else {
			// Disconnecting....
			VMApp.getVMPane().getDeviceControlPanel(deviceType, deviceIndex).getConnectButton().setText(LocaleStrings.getString("AJ_8_DCP"));
			iUSBSession.setImageRedirected(deviceType, deviceIndex, !(isPhysical));
			iUSBSession.setDeviceStopMode(deviceType, deviceIndex, true);
			if (isPhysical) {
				// Enable same device listed in other instances
				VMApp.getVMPane().physicalDriveChangeState(deviceType, deviceIndex, source, true);
			}

			iUSBSession.stopDeviceRedirection(deviceType, deviceIndex);
			VMApp.getVMPane().getStatusTabPanel().updateStatusTable(deviceType, deviceIndex, false);
			VMApp.getVMPane().updateDeviceStatusTable(deviceType, deviceIndex, false);
		}

		VMApp.getVMPane().getDeviceControlPanel(deviceType, deviceIndex).updateDeviceControlPanel();
		updateConnectButton(true); // Enable all the connect / disconnect
									// buttons state back to normal since we
									// have done processing

		/* <-------- The Thread will get terminated after this --------> */
	}
}
