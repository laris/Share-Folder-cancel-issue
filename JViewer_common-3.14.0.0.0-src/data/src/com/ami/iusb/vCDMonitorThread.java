package com.ami.iusb;

import com.ami.iusb.protocol.IUSBSCSI;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.vmedia.VMApp;

public class vCDMonitorThread extends Thread
{
	private CDROMRedir cdromRedir = null;
	private String[] cdromList = null;
	private String cdromSource = null;
	private boolean cdromThreadStatus = false;
	private boolean isMediaMatched = false;
	private int cddevice_no = 0;
	
	public vCDMonitorThread(int cddevice_no) {
		this.cddevice_no = cddevice_no;
	}

	/**
	 * Function used to start monitoring thread
	 *
	 */
	public void startCDROMMonitor()
	{
		cdromThreadStatus = true;
		this.start();
	}

	/**
	 * Function used to stop monitoring thread
	 *
	 */
	public void stopCDROMMonitor()
	{
		cdromThreadStatus = false;
	}

	/** Main execution loop for monitoring removal of physical drive
     *  when the media redirection is active. This thread will detect
     *  removal of physical drive and stop the media redirection */
	public void run()
	{
		cdromRedir = new CDROMRedir(true);
		cdromSource = VMApp.getInstance().getIUSBRedirSession().getCDROMSource(cddevice_no);
		cdromThreadStatus = true;
		long rcvdIdleTime = 0;
		long sentIdleTime = 0;

		while (cdromThreadStatus)
		{
			CDROMRedir cdRedir = VMApp.getInstance().getIUSBRedirSession().getCdromSession(cddevice_no);
			/* Check if the CD redirection session has been idle for more than CONNECTION_IDLE_TIME.
			 * If no packets are received or sent for more than CONNECTION_IDLE_TIME, then try to send
			 * a keep alive packet to the server. If there is an I/O exception, it means the connection is lost.
			 * So stop the CD redirection session. 
			 */
			if((rcvdIdleTime > IUSBRedirSession.CONNECTION_IDLE_TIME) ||
					(sentIdleTime > IUSBRedirSession.CONNECTION_IDLE_TIME)){
				try{
					if(cdRedir != null){
						cdRedir.sendCommandToServer(IUSBSCSI.IUSB_SCSI_OPCODE_KEEP_ALIVE);
					}
				}catch(Exception e){
					Debug.out.println(e);
					VMApp.getInstance().getIUSBRedirSession().setCDStopMode(IUSBRedirSession.STOP_ON_CONNECTION_LOSS,cddevice_no);
					if(cdRedir != null){
						cdRedir.stopRedirectionAbnormal();
					}
					break;
				}
			}
			//Perform the following block of code if it is a physical device redirection.
			if(VMApp.getInstance().getIUSBRedirSession().isCDROMPhysicalDrive(cddevice_no)){
				try
				{
					// If cdromResirection is not running, Come out this while loop.
					if(!VMApp.getInstance().isCDRedirRunning(cddevice_no))
						break;

					cdromList = cdromRedir.getCDROMList();

					if (cdromList == null) {
						VMApp.getInstance().getIUSBRedirSession().StopCDROMRedir(cddevice_no, IUSBRedirSession.STOP_ON_DEVICE_REMOVAL);
						VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_CDROM, cddevice_no).updateDeviceControlPanel();
					}
					else {//Check whether redirected device is removed from client.
						for(int i=0; i < cdromList.length; i++) {
							if(cdromSource.equals(cdromList[i])) {
								isMediaMatched = true;//Redirected drive is still connected.
								break;
							}
						}
						/* Incase if multiple CDROM media is connected, this
						 * condition will be used to track the removal of redirected media
						 */
						if(!isMediaMatched) {
							Debug.out.println("REDIRECTED CDROM MEDIUM REMOVAL HAS BEEN DETECTED, STOPPING REDIRECTION TO PREVENT FURTHER DAMAGE");
							VMApp.getInstance().getIUSBRedirSession().StopCDROMRedir(cddevice_no, IUSBRedirSession.STOP_ON_DEVICE_REMOVAL);
							VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_CDROM, cddevice_no).updateDeviceControlPanel();

						}
						else {
							isMediaMatched = false;
						}
					}
				}
				catch (RedirectionException e)
				{
					stopCDRedirAbnormal();
					Debug.out.println(e);
				}
				catch (Exception e) {
					stopCDRedirAbnormal();
					Debug.out.println(e);
				}
			}
			/* Thread sleep to make CPU happy */
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				Debug.out.println(e);
			}
			//Calculate the receive and send idle time.
			if(cdRedir != null){
				if(cdRedir.getLastPktRcvdTime() > 0)
					rcvdIdleTime = JViewerApp.getInstance().getCurrentTime() - cdRedir.getLastPktRcvdTime();
				if(cdRedir.getLastPktSentTime() > 0)
					sentIdleTime = JViewerApp.getInstance().getCurrentTime() - cdRedir.getLastPktSentTime();
			}
		}
		return;
	}
/**
	 * Stops the CD redirection in abnormal case.
	 */
	private void stopCDRedirAbnormal(){
		if(!JViewer.isVMApp()){
			if(JViewerApp.getInstance().getVMDialog() != null)
				JViewerApp.getInstance().getVMDialog().setVisible(false);
		}
		VMApp.getInstance().getIUSBRedirSession().stopCDROMAbnormal(cddevice_no);
	}
}
