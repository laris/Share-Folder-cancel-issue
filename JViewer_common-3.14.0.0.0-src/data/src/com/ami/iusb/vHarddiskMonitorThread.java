package com.ami.iusb;

import com.ami.iusb.protocol.IUSBSCSI;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.vmedia.VMApp;

public class vHarddiskMonitorThread extends Thread
{
	private HarddiskRedir harddiskRedir = null;
	private String[] harddiskList = null;
	private String[] harddiskFixedList = null;
	private String[] harddiskUSBList = null;
	private String harddiskSource = null;
	private boolean harddiskThreadStatus = false;
	private boolean isMediaMatched = false;
	private int hddevice_no = 0;

	public vHarddiskMonitorThread(int device_no) {
		hddevice_no = device_no;
	}

	/**
	 * Function used to start monitoring thread
	 *
	 */
	public void startharddiskMonitor()
	{
		harddiskThreadStatus = true;
		this.start();
	}

	/**
	 * Function used to stop monitoring thread
	 *
	 */
	public void stopHarddiskMonitor()
	{
		harddiskThreadStatus = false;
	}

	/** Main execution loop for monitoring removal of physical drive
     *  when the media redirection is active. This thread will detect
     *  removal of physical drive and stop the media redirection */
	public void run()
	{
		long rcvdIdleTime = 0;
		long sentIdleTime = 0;
		harddiskRedir = new HarddiskRedir(true);
		harddiskSource = VMApp.getInstance().getIUSBRedirSession().getHarddiskSource(hddevice_no);
		if(VMApp.getInstance().getIUSBRedirSession().isHarddiskPhysicalDrive(hddevice_no)){
			String harddiskSourceSplit = harddiskSource.substring(0, harddiskSource.lastIndexOf("-"));
			harddiskSource = harddiskSourceSplit.trim();
		}

		while (harddiskThreadStatus)
		{
			HarddiskRedir hdRedir = VMApp.getInstance().getIUSBRedirSession().getHarddiskSession(hddevice_no);
			/* Check if the HD redirection session has been idle for more than CONNECTION_IDLE_TIME.
			 * If no packets are received or sent for more than CONNECTION_IDLE_TIME, then try to send
			 * a keep alive packet to the server. If there is an I/O exception, it means the connection is lost.
			 * So stop the HD redirection session. 
			 */
			if((rcvdIdleTime >= IUSBRedirSession.CONNECTION_IDLE_TIME) ||
					(sentIdleTime >= IUSBRedirSession.CONNECTION_IDLE_TIME)){
				try{
					if(hdRedir != null)
					{
						VMApp.getInstance().getIUSBRedirSession().getHarddiskSession(hddevice_no).sendCommadToServer(IUSBSCSI.IUSB_SCSI_OPCODE_KEEP_ALIVE);
					}
				}catch(Exception e){
					Debug.out.println(e);
					VMApp.getInstance().getIUSBRedirSession().setHDStopMode(IUSBRedirSession.STOP_ON_CONNECTION_LOSS,hddevice_no);
					if(hdRedir != null)
					{
						VMApp.getInstance().getIUSBRedirSession().getHarddiskSession(hddevice_no).stopRedirectionAbnormal();
					}
					break;
				}
			}
			//Perform the following block of code if it is a physical device redirection.
			if(VMApp.getInstance().getIUSBRedirSession().isHarddiskPhysicalDrive(hddevice_no)){
				try
				{
					harddiskUSBList = harddiskRedir.getUSBHDDList();
					if((true == JViewer.IsClientAdmin()) && ((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_REDIR_RD_WR_MODE) != JViewerApp.OEM_REDIR_RD_WR_MODE)){
						harddiskFixedList = harddiskRedir.getHarddiskFixedList();
					}
					int devicelength = 0;
					int usbdevicelength=0;
					int fixeddevicelength = 0;

					if(harddiskUSBList != null)
						usbdevicelength = harddiskUSBList.length;

					if(harddiskFixedList != null)
						fixeddevicelength  = harddiskFixedList.length;

					devicelength = usbdevicelength + fixeddevicelength;
					harddiskList = new String[devicelength];

					for(int k=0;k<usbdevicelength;k++)
					{
						if(harddiskUSBList[k] != null)
							harddiskList[k] = harddiskUSBList[k];
					}
					for(int k=0;k<fixeddevicelength;k++)
					{
						String[] harddisksplit = harddiskFixedList[k].split("-");
						if(harddisksplit != null)
							harddiskList[k+usbdevicelength] = harddisksplit[0];
					}

					if (!VMApp.getInstance().isHDRedirRunning(hddevice_no))
						break;

					if(harddiskList == null) {
						VMApp.getInstance().getIUSBRedirSession().StopHarddiskImageRedir(hddevice_no, IUSBRedirSession.STOP_ON_DEVICE_REMOVAL);
						VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_HD_USB, hddevice_no).updateDeviceControlPanel();
					}
					else {//Check whether redirected device is removed from host.
						String sourceSub = (harddiskSource.indexOf('-')>=0) ? harddiskSource.substring(harddiskSource.indexOf('-'),harddiskSource.length()) :harddiskSource;
						for(int i=0; i < harddiskList.length; i++)
						{
							/* We use startsWith() instead of equals(). In Linux, /dev/sda1 will disappear from list
							 * once redirection is activated. But existence of /dev/sda should be enough to prove
							 * the device is sill present */
							String redirSub = (harddiskList[i].indexOf('-')>=0)? harddiskList[i].substring(harddiskList[i].indexOf('-'),harddiskList[i].length())  :harddiskList[i];
							if(sourceSub.startsWith(redirSub)){
								isMediaMatched = true;
								break;
							}
						}
						/* Incase if multiple HD/USB media is connected, this
						 * condition will be used to track the removal of redirected media
						 */
						if(!isMediaMatched) {
							Debug.out.println("REDIRECTED Harddisk MEDIUM REMOVAL HAS BEEN DETECTED, STOPPING REDIRECTION TO PREVENT FURTHER DAMAGE");
							VMApp.getInstance().getIUSBRedirSession().StopHarddiskRedir(hddevice_no, IUSBRedirSession.STOP_ON_DEVICE_REMOVAL);
							VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_HD_USB, hddevice_no).updateDeviceControlPanel();
						}
						else {
							isMediaMatched = false;
						}
					}
				}
				catch (Exception e) {
					stopHDAbnormal();
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
			if(hdRedir != null){
				if(hdRedir.getLastPktRcvdTime() > 0)
					rcvdIdleTime = JViewerApp.getInstance().getCurrentTime() - hdRedir.getLastPktRcvdTime();
				if(hdRedir.getLastPktSentTime() > 0)
					sentIdleTime = JViewerApp.getInstance().getCurrentTime() - hdRedir.getLastPktSentTime();
			}
		}
		return;
	}

	/**
	 * Stops the hard disk redirection in abnormal case.
	 */
	private void stopHDAbnormal(){

		VMApp.getInstance().getIUSBRedirSession().stopHarddiskAbnormal(hddevice_no);
	}
}
