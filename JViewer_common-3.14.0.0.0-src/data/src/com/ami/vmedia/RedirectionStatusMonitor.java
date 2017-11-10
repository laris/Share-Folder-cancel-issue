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
import com.ami.vmedia.gui.DevicePanel;

/**
 * This thread monitors the status of the currently active redirections.
 * @author deepakmn
 *
 */
public class RedirectionStatusMonitor extends Thread{
	private boolean cdRedirected = false;
	private boolean hdRedirected = false;
	private IUSBRedirSession iusbRedirSession = null;
	private boolean run = false;
	/**
	 * Constructor.
	 */
	public RedirectionStatusMonitor(){
		iusbRedirSession = VMApp.getInstance().getIUSBRedirSession();
		run = true;
		start();
	}
	/**
	 * Thread implementation
	 */
	public void run(){
		DevicePanel devPanel = null ;
		while(run){
			// Added sleep to make CPU happy
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {				
				Debug.out.println(e);
			}
			
			if(iusbRedirSession != null)
			{
				try{
					int numCD = 0,numHD = 0;

					numCD = VMApp.getInstance().getNumCD();
					numHD = VMApp.getInstance().getNumHD();
					cdRedirected = false;
					hdRedirected = false;
					for(int cdItr = 0;cdItr < numCD;cdItr++)
					{
						try {
							if(iusbRedirSession.getCdromSession(cdItr) != null)
							{
								devPanel = VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_CDROM,cdItr).getDevicePanel();
								//Check whether the CD redirection session is terminated.
								if(iusbRedirSession.getCdromSession(cdItr).isCdRedirectionKilled()){
									//reset the flag to avoid multiple stops.
									iusbRedirSession.getCdromSession(cdItr).setCdRedirectionKilled(false);
									iusbRedirSession.StopCDROMRedir(cdItr, IUSBRedirSession.STOP_ON_TERMINATE);
									if(!(devPanel.isImageSelected())){
										//Enable same device listed in other instances
										VMApp.getVMPane().physicalDriveChangeState(VMApp.DEVICE_TYPE_CDROM,cdItr, devPanel.getSelectedDeviceString(), true);
									}
									VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_CDROM, cdItr).updateDeviceControlPanel();
								}
								//Check whether the redirected CD is ejected from the host.
								else if(iusbRedirSession.getCdromSession(cdItr).isCdImageEjected()){
									if(iusbRedirSession.getCdromSession(cdItr).isCdImageRedirected()){
										iusbRedirSession.StopISORedir(cdItr, IUSBRedirSession.STOP_ON_EJECT);
									}
									else{
										iusbRedirSession.StopCDROMRedir(cdItr, IUSBRedirSession.STOP_ON_EJECT);
									}
									if(!(devPanel.isImageSelected())){
										//Enable same device listed in other instances
										VMApp.getVMPane().physicalDriveChangeState(VMApp.DEVICE_TYPE_CDROM,cdItr, devPanel.getSelectedDeviceString(), true);
									}
									VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_CDROM, cdItr).updateDeviceControlPanel();
								}
								//Check whether the CD server is stopped/restarted.
								else if(iusbRedirSession.getCdromSession(cdItr).isCdServiceRestarted()){
									if(iusbRedirSession.getCdromSession(cdItr).isCdImageRedirected()){
										iusbRedirSession.StopISORedir(cdItr, IUSBRedirSession.STOP_ON_SERVER_RESTART);
									}
									else{
										iusbRedirSession.StopCDROMRedir(cdItr, IUSBRedirSession.STOP_ON_SERVER_RESTART);
									}
									if(!(devPanel.isImageSelected())){
										//Enable same device listed in other instances
										VMApp.getVMPane().physicalDriveChangeState(VMApp.DEVICE_TYPE_CDROM,cdItr, devPanel.getSelectedDeviceString(), true);
									}
									VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_CDROM, cdItr).updateDeviceControlPanel();
								}
								else{
									cdRedirected = true;
								}
							}
						} catch (Exception e) {
							Debug.out.println(e);
							VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_CDROM, cdItr).updateDeviceControlPanel();
						}
					}
					if(!JViewer.isVMApp() && !cdRedirected)
						iusbRedirSession.updateCDToolbarButtonStatus(cdRedirected);

					for(int hdItr = 0;hdItr < numHD;hdItr++)
					{
						//Check whether the HD redirection session is terminated.
						try {
							if(iusbRedirSession.getHarddiskSession(hdItr) != null){
								devPanel = VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_HD_USB,hdItr).getDevicePanel();
								if(iusbRedirSession.getHarddiskSession(hdItr).isHdRedirectionKilled()){
									//reset the flag to avoid multiple stops
									iusbRedirSession.getHarddiskSession(hdItr).setHdRedirectionKilled(false);
									iusbRedirSession.StopHarddiskImageRedir(hdItr, IUSBRedirSession.STOP_ON_TERMINATE);
									if(!(devPanel.isImageSelected())){
										//Enable same device listed in other instances
										VMApp.getVMPane().physicalDriveChangeState(VMApp.DEVICE_TYPE_HD_USB,hdItr, devPanel.getSelectedDeviceString(), true);
									}
									VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_HD_USB, hdItr).updateDeviceControlPanel();
								}
								//Check whether the hard disk or the USB got ejected from the host.
								else if(iusbRedirSession.getHarddiskSession(hdItr).isHdImageEjected()){
									if(iusbRedirSession.getHarddiskSession(hdItr).isHdImageRedirected()){
										iusbRedirSession.StopHarddiskImageRedir(hdItr, IUSBRedirSession.STOP_ON_EJECT);
									}
									else{
										iusbRedirSession.StopHarddiskRedir(hdItr, IUSBRedirSession.STOP_ON_EJECT);
									}
									if(!(devPanel.isImageSelected())){
										//Enable same device listed in other instances
										VMApp.getVMPane().physicalDriveChangeState(VMApp.DEVICE_TYPE_HD_USB,hdItr, devPanel.getSelectedDeviceString(), true);
									}
									VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_HD_USB, hdItr).updateDeviceControlPanel();
								}
								//Check whether the harddisk service is stopped/restarted
								else if(iusbRedirSession.getHarddiskSession(hdItr).isHdServiceRestarted()){
									if(iusbRedirSession.getHarddiskSession(hdItr).isHdImageRedirected()){
										iusbRedirSession.StopHarddiskImageRedir(hdItr, IUSBRedirSession.STOP_ON_SERVER_RESTART);
									}
									else{
										iusbRedirSession.StopHarddiskRedir(hdItr, IUSBRedirSession.STOP_ON_SERVER_RESTART);
									}
									if(!(devPanel.isImageSelected())){
										//Enable same device listed in other instances
										VMApp.getVMPane().physicalDriveChangeState(VMApp.DEVICE_TYPE_HD_USB,hdItr, devPanel.getSelectedDeviceString(), true);
									}
									VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_HD_USB, hdItr).updateDeviceControlPanel();
								}
								else{
									hdRedirected = true;
								}
							}
						} catch (Exception e) {
							Debug.out.println(e);
							VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_HD_USB, hdItr).updateDeviceControlPanel();
						}
					}
					if(!JViewer.isVMApp() && !hdRedirected)
						iusbRedirSession.updateHDToolbarButtonStatus(hdRedirected);
				}catch(Exception e){
					Debug.out.println(e);
				}
			}
		}
	}
	/**
	 * Stops the redirection thread
	 */
	public void stopRedirectionStatusMonitior(){
		run = false;
	}
}
