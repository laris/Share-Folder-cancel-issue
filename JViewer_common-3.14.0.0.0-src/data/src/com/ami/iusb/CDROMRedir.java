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

package com.ami.iusb;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.ami.iusb.protocol.CDROMProtocol;
import com.ami.iusb.protocol.IUSBSCSI;
import com.ami.iusb.protocol.PacketMaster;
import com.ami.kvm.imageredir.IUSBHeader;
import com.ami.kvm.imageredir.cd.CDImage;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.InfoDialog;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.kvm.jviewer.gui.StandAloneConnectionDialog;
import com.ami.kvm.jviewer.kvmpkts.IVTPPktHdr;
import com.ami.vmedia.VMApp;

/**
 * A <code>CDROMRedir</code> object contains all the information and threads
 * to perform CDROM redirection. It also provides some informative accessor
 * methods.<BR>
 * <BR>
 *
 * Starting redirection via {@link #startRedirection} starts the cdrom
 * redirection thread, and handles all incoming requests for cdrom information
 * and sends the responses. This thread is stopped via {@link #stopRedirection}.
 * <BR>
 * <BR>
 *
 * The actual interaction with the CD-ROM drive takes place inside native
 * methods that we access via JNI. We do all the network I/O in java, and pass
 * the SCSI requests to the native code. We pass the response right back to the
 * remote cdserver.
 *
 */
public class CDROMRedir extends Thread {

	private PacketMaster 	packetMaster;
	private CDROMProtocol 	protocol;
	private ByteBuffer		packetReadBuffer;
	private ByteBuffer 		packetWriteBuffer;
	private boolean 		physicalDrive;
	private int  			CDDevice_no;
	private boolean 		running = false;
	private boolean 		stopRunning = false;
	private long 			nativeReaderPointer = -1;
	private Thread 			redirThread;
	private String 			sourceCDROM = null;
	private int 			nBytesRedirected = 0;
	private boolean			cdReconnect = false;

	private static final int DEVICE_REDIRECTION_ACK = 0xf1;
	private static final int AUTH_CMD = 0xf2;
	private static final int MAX_READ_SECTORS = 0x40;
	private static final int MAX_READ_SIZE = 2048 * MAX_READ_SECTORS;
	private static final int MAX_READ_DATA_SIZE = 1024;
	private static final int SCSI_GET_STATUS_COUNT_SEC = 50;
	private  boolean cdImageEjected = false;
	private  boolean cdServiceRestarted = false;
	private  boolean cdImageRedirected = false;
	private vCDMonitorThread vMThread = null;
	private int cdInstanceNum;
	private String errorMessage = " ";
	private long lastPktRcvdTime = 0;
	private long lastPktSentTime = 0;
	
	// Native library call
	private native String[] listCDROMDrives();
	private native void newCDROMReader(boolean physicalCD);
	private native void deleteCDROMReader();
	private native boolean openCDROM(byte[] a);
	private native void closeCDROM();
	private native int executeCDROMSCSICmd(ByteBuffer scsiRequest, ByteBuffer scsiResponse);
	private native String getVersion();
	private static Object syncObj = new Object();
	private  boolean confModified = false;
	private  boolean cdRedirectionKilled = false;
	private CDImage newCDImage = null;
	private  boolean cdStoppedByUser = false;
	private	int cdromRedirStatus;
	private	int cdROMDeviceStatus;
	private int cdStopMode;
	private boolean command_sent = false; //check this flag the notification comand send to host if the Get_notification_scsi_support flas is et
	private boolean Get_notification_scsi_support = false;
	private int  Get_notification_scsi_counter = 0;

	/***
	 *
	 * Loading the Library for Acccesing the CDROM
	 */

	static {
		try {
			if( !JViewer.isdownloadapp() && !JViewer.isplayerapp()){
				if(JViewer.isjviewerapp()){ // Normal JViwer
					System.loadLibrary("javacdromwrapper");
				}
				else { //For SPXMultiViewer and StandAloneApp
					loadWrapperLibrary();
				}
			}
		} catch (UnsatisfiedLinkError e) {
			System.err.println(LocaleStrings.getString("4_2_CDROMREDIR"));
		}
	}

	/**
	 * Loads the javawrapper library files. 
	 */
	private static void loadWrapperLibrary(){
		String libPath = null;
		File libFile = null;
		//Get the current working path of the JViewer, in the case of StandAloneApp to load the libraries
		String currPath = JViewer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		currPath = currPath.substring(0, currPath.lastIndexOf('/'));
		//If there is any white space in a directory name, it will be represented 
		//as %20 in the currPath, in Linux and Mac file system. It should replaced with a '\'. 
		if(currPath.contains("%20")){
			currPath = currPath.replaceAll("%20", "\\ ");
		}

		String commonPath = File.separator+"Jar"+File.separator+
							JViewer.getIp()+File.separator+"lib"+File.separator;
		//Replace all ':' characters from the common path with '_'. This is because in Windows file system,
		//file and directory names and are not allowed to contain ':'. In this case the getIP() method
		//might return IPV6 address which will contain : and will lead to error.
		if(System.getProperty("os.name").startsWith("Windows")){
			if(JViewer.isStandAloneApp() || JViewer.isVMApp()){
				commonPath = StandAloneConnectionDialog.replaceAllPattern(commonPath, ":", "_");
				libPath = currPath+commonPath+"javacdromwrapper.dll";
				libFile = new File(libPath);
				if(false == StandAloneConnectionDialog.getWrapperLibrary("javacdromwrapper.dll")){
					Debug.out.println("Unable to extract the javacdromwrapper.dll");
					libPath = null;
				}
			}
			else
				libPath = System.getProperty("user.dir")+commonPath+"javacdromwrapper.dll";
		}
		else if(System.getProperty("os.name").startsWith("Linux")){
			if(JViewer.isStandAloneApp() || JViewer.isVMApp()){
				libPath = currPath+commonPath+"libjavacdromwrapper.so";
				libFile = new File(libPath);
				if(false == StandAloneConnectionDialog.getWrapperLibrary("libjavacdromwrapper.so")){
					Debug.out.println("Unable to extract the libjavacdromwrapper.so");
					libPath = null;
				}
			}
			else
				libPath = System.getProperty("user.dir")+commonPath+"libjavacdromwrapper.so";
		}
		else if(System.getProperty("os.name").startsWith("Mac")){
			if(JViewer.isStandAloneApp() || JViewer.isVMApp()){
				libPath = currPath+commonPath+"libjavacdromwrapper.jnilib";
				libFile = new File(libPath);
				if(false == StandAloneConnectionDialog.getWrapperLibrary("libjavacdromwrapper.jnilib")){
					Debug.out.println("libjavacdromwrapper.jnilib");
					libPath = null;
				}
			}
			else
				libPath = System.getProperty("user.dir")+commonPath+"libjavacdromwrapper.jnilib";
		}

		try {
			System.load(libPath);
		} catch(UnsatisfiedLinkError ule){
			Debug.out.println(ule);
			showLibraryLoadError();
		}
		catch (Exception e) {
			Debug.out.println(e);
			showLibraryLoadError();
		}
	}

	/**
	 * Creates a new instance of CDROMRedir
	 *
	 * @param physicalDrive
	 *            True for actual cdrom drives, false for iso images
	 */
	public CDROMRedir(boolean physicalDrive) {
		this.physicalDrive = physicalDrive;
		cdromRedirStatus = IUSBRedirSession.DEVICE_REDIR_STATUS_IDLE;
		cdROMDeviceStatus = IUSBRedirSession.DEVICE_FREE;
		cdStopMode = IUSBRedirSession.STOP_NORMAL;
		protocol = new CDROMProtocol();
		packetReadBuffer = ByteBuffer.allocateDirect(MAX_READ_DATA_SIZE);
		packetWriteBuffer = ByteBuffer.allocateDirect(MAX_READ_SIZE	+ IUSBHeader.HEADER_LEN
				+ IUSBSCSI.IUSB_SCSI_PKT_SIZE_WITHOUT_HEADER);
	}

	/**
	 * Method Used to establish the socket connection
	 *
	 * @param host -
	 *            serverip
	 * @param port -
	 *            cdserver connceting port
	 * @param bVMUseSSL -
	 *            ssl/nonssl
	 * @throws IOException
	 */

	private int cdromConnect(String host, int port, boolean bVMUseSSL)throws IOException {
		packetMaster = new PacketMaster(host, port, false, protocol, bVMUseSSL);
		packetMaster.setupBuffers(packetReadBuffer, packetWriteBuffer);
		packetMaster.setBufferEndianness(ByteOrder.LITTLE_ENDIAN,ByteOrder.LITTLE_ENDIAN);
		if(JViewer.isSinglePortEnabled()){

			if( JViewerApp.getInstance().getSinglePortKvm().setHTTPConnect("CDMEDIA") < 0){			
				VMApp.getInstance().getIUSBRedirSession().setCDStopMode(IUSBRedirSession.STOP_PORT_NOT_IN_LISTEN,CDDevice_no);
				return -1;
			}
			packetMaster.setSock(JViewerApp.getInstance().getSinglePortKvm().getHttpsock());
		}
		else{
			if(packetMaster.connectVmedia(bVMUseSSL) < 0 ){			
				VMApp.getInstance().getIUSBRedirSession().setCDStopMode(IUSBRedirSession.STOP_PORT_NOT_IN_LISTEN,CDDevice_no);
				return -1;
			}
		}
		return 0;
	}

	/**
	 * Method is Used to close the established conection
	 *
	 */
	private void cdromDisconnect() {
		try {
			/* Close the connection */
			packetMaster.VmediaSockclose();
		} catch (IOException e) {
			System.err.println(LocaleStrings.getString("4_6_CDROMREDIR")+ e.getMessage());
		}
	}

	/**
	 * Start CD-ROM redirection and create the CD-ROM redirection thread
	 *
	 * @param host
	 *            Hostname or IP of the remote cdserver application
	 * @param cdromDrive
	 *            Complete path to the device we should treat as the CD-ROM
	 *            drive, or full path to the iso file for image redirection
	 * @return true if redirection starts correctly
	 * @return false if redirection is not started
	 * @throws RedirProtocolException
	 *             if unexpected packets are encountered
	 * @throws RedirectionException
	 *             on network errors
	 */
	public boolean startRedirection(String host, String cdromDrive,int cddevice_no,String token, int port, boolean bVMuseSSL)
			throws RedirectionException {

		if (running)
			return (true);
		CDDevice_no = cddevice_no;
		try {
			/* Connect the network socket */
			if(cdromConnect(host, port, bVMuseSSL) < 0)
				return false;
			SendAuth_SessionToken(token);
			IUSBSCSI.SendMediaInfo(packetMaster, packetWriteBuffer, cdromDrive);
			/* Get the first request from the card - it has a special value */
			IUSBSCSI request = recvRequest();
			
			cdInstanceNum = request.instanceNum;
			if (request.opcode == DEVICE_REDIRECTION_ACK) {
				/* Did we get the connection? */
				if(request.connectionStatus == IUSBRedirSession.CONNECTION_PERM_DENIED){
					cdromDisconnect();
					setErrorMessage(LocaleStrings.getString("4_17_CDROMREDIR"));
					return (false);
				}
				if(request.connectionStatus == IUSBRedirSession.CONNECTION_MAX_USER){
					cdromDisconnect();
					setErrorMessage(LocaleStrings.getString("4_18_CDROMREDIR"));
					return (false);
				}
				else if (request.connectionStatus == IUSBRedirSession.LICENSE_EXPIRED) {
					cdromDisconnect();
					setErrorMessage(LocaleStrings.getString("F_136_JVM"));
					return (false);
				}
				else if (request.connectionStatus != IUSBRedirSession.CONNECTION_ACCEPTED) {
					if( ( request.connectionStatus == IUSBRedirSession.CONNECTION_INVALID_SESSION_TOKEN ) && ( JViewer.isVMApp() == true ) ){
						stopRedirection();
						setErrorMessage(LocaleStrings.getString("6_52_IUSBREDIR"));
					}else if (request.m_otherIP != null) {
						//for local/remote media connection
						cdromDisconnect();
						if((request.m_otherIP.equalsIgnoreCase("127.0.0.1")) ||
								(request.m_otherIP.equalsIgnoreCase("::1"))) {
							setErrorMessage(LocaleStrings.getString("4_19_CDROMREDIR"));
						}
						else {
							setErrorMessage(LocaleStrings.getString("4_7_CDROMREDIR") + request.m_otherIP);
						}
					}
					return (false);
				}
			} else {
				cdromDisconnect();
				throw new RedirProtocolException(LocaleStrings.getString("4_8_CDROMREDIR")+ request.opcode);
			}
		} catch (IOException e) {
			Debug.out.println(e);
			throw new RedirectionException(e.getMessage());
		}

		/* Create the CD-ROM reader */
		if(((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) != 
				JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) ||
				isPhysicalDevice()){
			if (nativeReaderPointer == -1)
				newCDROMReader(physicalDrive);
		}

		sourceCDROM = cdromDrive;
		File Drive = new File(sourceCDROM);
		if(((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) != 
				JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) && !isPhysicalDevice())
		{
			if(!Drive.exists())
			{
				setErrorMessage(LocaleStrings.getString("4_10_CDROMREDIR"));
				//System.err.println(LocaleStrings.GetString("Cannot open CD"));
				deleteCDROMReader();
				cdromDisconnect();
				return( false );
			}
		}
		if(((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) != 
				JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) ||
				isPhysicalDevice()){
			try {
				//opening the CDROM device using native library method call
				if (!openCDROM(cdromDrive.getBytes("UTF-8"))){
					setErrorMessage(LocaleStrings.getString("4_11_CDROMREDIR"));
					deleteCDROMReader();
					cdromDisconnect();
					return (false);
				}
			} catch (UnsupportedEncodingException e) {
				Debug.out.println(LocaleStrings.getString("4_12_CDROMREDIR"));
				Debug.out.println(e);
			}
		}
		else{
			File file = new File(cdromDrive);
			// to call the executeSCSI java function the image needs to be opened in java.
			if(!((newCDImage = new CDImage(file)).isOpened())) 
			{
				setErrorMessage(LocaleStrings.getString("6_9_IUSBREDIR"));
				cdromDisconnect();
				return (false);
			}
		}

		if(!JViewer.isVMApp())
			JViewerApp.getInstance().getKVMClient().MediaRedirectionState((byte) 1);
		nBytesRedirected = 0;
		/* Start the CD-ROM redirection thread */
		redirThread = new Thread(this);
		redirThread.start();
		IUSBRedirSession iusbRedirSession;
		iusbRedirSession = VMApp.getInstance().getIUSBRedirSession();

		//CD Redirection will always occur in read only mode, irrespective of whether its Physical device or ISO image
		//that is being redirected.
		if (!JViewer.isVMApp()) {
			if (JViewerApp.getInstance().getRetryConnection() == false) {
				iusbRedirSession.updateRedirectionStatus(VMApp.DEVICE_TYPE_CDROM, cddevice_no,
						IUSBRedirSession.READ_ONLY);
			}
		} else {
			iusbRedirSession.updateRedirectionStatus(VMApp.DEVICE_TYPE_CDROM, cddevice_no, IUSBRedirSession.READ_ONLY);
		}
		cdromRedirStatus = IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED;

		if(iusbRedirSession.isCDROMPhysicalDrive(cddevice_no))
		{
			vMThread = new vCDMonitorThread(CDDevice_no);
			vMThread.startCDROMMonitor();
		}
		running = true;
		return (true);
	}

	/**
	 * Stop CD-ROM redirection and join the CD-ROM redirection thread
	 * @return
	 */
	public boolean stopRedirection() {
		if(!isPhysicalDevice() && newCDImage != null)
		{
			newCDImage.closeImage();
			newCDImage = null;
		}
		//Stop the CD redirection monitor thread
		if(vMThread != null){
			vMThread.stopCDROMMonitor();
		}
		if (running) {
			if(Get_notification_scsi_support)
			{
				command_sent=true;
				do{
					try {
						this.sleep(SCSI_GET_STATUS_COUNT_SEC);
					} catch (InterruptedException e) {
					}
					if(Get_notification_scsi_counter > SCSI_GET_STATUS_COUNT_SEC)
					{
						command_sent=false;
                                                Get_notification_scsi_counter = 0;
					}
					Get_notification_scsi_counter++;
				}while(command_sent);
				Get_notification_scsi_support=false;
			}
			try {
				// If the cdServerRestarted flag is set, then media server has stopped/restarted and notified JViewer about it.
				// So no need to send MEDIA_SESSION_DISCONNECT command to media server.
				if(isCdServiceRestarted() == false) {
					IUSBSCSI.sendCommandToMediaServer(packetMaster, packetWriteBuffer, null, IUSBSCSI.MEDIA_SESSION_DISCONNECT);
				}
			} catch (Exception e) {
				Debug.out.println("Sending MEDIA_SESSION_DISCONNECT command to media server failed : " + e);
			}

			stopRunning = true;
			cdromDisconnect();
			try {
				redirThread.join();
			} catch (InterruptedException e) {
				System.err.println(LocaleStrings.getString("4_14_CDROMREDIR"));
			}
			if(!JViewer.isVMApp())
			JViewerApp.getInstance().getKVMClient().MediaRedirectionState((byte) 0);
			running = false;
			stopRunning = false;
			if(((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) != 
					JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) ||
					isPhysicalDevice()){
				closeCDROM();
				deleteCDROMReader();
			}
		}
		nBytesRedirected = 0;
		cdromRedirStatus = IUSBRedirSession.DEVICE_REDIR_STATUS_IDLE;
		return true;
	}

	/**
	 * Response packet reeceived from the CDServer
	 *
	 * @return
	 * @throws IOException
	 * @throws RedirectionException
	 */
	private IUSBSCSI recvRequest() throws IOException, RedirectionException {
		return ((IUSBSCSI) packetMaster.receivePacket());
	}

	/***
	 * Returns true if redirection is currently active
	 * @return
	 */
	public boolean isRedirActive() {
		return (running);
	}

	/***
	 *
	 * Must be called to stop CDROM redirection thread abnormally
	 *
	 */
	public void stopRedirectionAbnormal() {
		//Stop the CD redirection monitor thread
		if(vMThread != null){
			vMThread.stopCDROMMonitor();
		}
		if (running) {
			if(Get_notification_scsi_support)
			{
				command_sent=true;
				do{
					try {
						this.sleep(SCSI_GET_STATUS_COUNT_SEC);
					} catch (InterruptedException e) {
					}
					if(Get_notification_scsi_counter > SCSI_GET_STATUS_COUNT_SEC)
					{
						command_sent=false;
                                                Get_notification_scsi_counter = 0;
					}
					Get_notification_scsi_counter++;
				}while(command_sent);
				Get_notification_scsi_support=false;
			}
			// closing image
			if(!isPhysicalDevice() && newCDImage != null)
			{
				newCDImage.closeImage();
				newCDImage = null;
			}
			stopRunning = true;
			cdromDisconnect();
			running = false;
			stopRunning = false;
			if(((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) != 
					JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) ||
					isPhysicalDevice()){
				closeCDROM();
				deleteCDROMReader();
			}
				JViewerApp.getInstance().reportCDROMAbnormal(CDDevice_no);
		}
		cdromRedirStatus = IUSBRedirSession.DEVICE_REDIR_STATUS_IDLE;
	}

	/**
	 * Main execution loop for the CD-ROM redirection thread. Don't mess with
	 * this, use {@link #startRedirection} and {@link #stopRedirection} to start
	 * and stop this thread.
	 */
	public void run() {
		IUSBSCSI request;
		IUSBSCSI response;
		int nTempLen = 0;
		int dataLen = 0;
		while (!stopRunning) {
			try {
				packetWriteBuffer.rewind();
				/* Get a request from the card */
				request = recvRequest();

				if (request == null){
					continue;
				}

				//Update the last packet received time for the CD redirection session.
				lastPktRcvdTime = JViewerApp.getInstance().getCurrentTime();
				if(((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) != 
						JViewerApp.OEM_JAVA_CD_IMAGE_REDIR) ||
						isPhysicalDevice()) // executeSCSI native method call
					dataLen = executeCDROMSCSICmd(packetReadBuffer,	packetWriteBuffer);
				else{ // executeSCSI java method call
					if(request.opcode == IUSBSCSI.IUSB_SCSI_OPCODE_KEEP_ALIVE){
						IUSBSCSI.sendCommandToMediaServer(packetMaster, packetWriteBuffer, null, IUSBSCSI.IUSB_SCSI_OPCODE_KEEP_ALIVE);
						//Set the last packet sent time, when responding to keep alive packets.
						lastPktSentTime = JViewerApp.getInstance().getCurrentTime();
						continue;
					}
					newCDImage.executeSCSICmd(packetReadBuffer, packetWriteBuffer);
					dataLen = newCDImage.getDataLength() + IUSBHeader.IUSB_HEADER_SIZE; // size of header is 61
				}

				if(dataLen >= 0 && dataLen <= packetWriteBuffer.capacity())
				{
					packetWriteBuffer.limit(dataLen);
				}

				// need to set the position to 0 because we have modified the response byte buffer inside the executeSCSI function
				packetWriteBuffer.position(0);

				if(request.opcode == IUSBSCSI.OPCODE_KILL_REDIR)
				{
					cdRedirectionKilled = true;
					return;
				}
				else if(request.opcode == IUSBSCSI.MEDIA_SESSION_DISCONNECT) {
					cdServiceRestarted = true;
					return;
				}

				/* Form the IUSB response packet */
				response = new IUSBSCSI(packetWriteBuffer, true);
				if((request.opcode == IUSBSCSI.IUSB_SCSI_OPCODE_GET_NOTIF_STAT) && (response.dataLen == IUSBSCSI.IUSB_SCSI_OPCODE_GET_NOTIF_STAT_PKT_SIZE))
                {
					Get_notification_scsi_support=true;
                }
				if((command_sent == true) && (request.opcode == IUSBSCSI.IUSB_SCSI_OPCODE_GET_NOTIF_STAT) && (response.dataLen == IUSBSCSI.IUSB_SCSI_OPCODE_GET_NOTIF_STAT_PKT_SIZE))
				{
					response.data.put(34, (byte)0x0);//sending the eject command medium change to the host via GEtEVENTSTATUSNotification
					response.data.put(33, (byte)0x1);
					response.data.put(36, (byte)0x0);
				}
				/* Send the IUSB response packet */
				packetMaster.sendPacket(response);
				//Set the last packet sent time, when responding to iUSB request packets.
				lastPktSentTime = JViewerApp.getInstance().getCurrentTime();
				nTempLen += dataLen;
				nBytesRedirected += (nTempLen / 1024);
				nTempLen = nTempLen % 1024;
				if((command_sent == true) && (request.opcode == IUSBSCSI.IUSB_SCSI_OPCODE_GET_NOTIF_STAT) && (response.dataLen == IUSBSCSI.IUSB_SCSI_OPCODE_GET_NOTIF_STAT_PKT_SIZE))
				{
					command_sent =false;
				}
				//handle eject command after sending response to host to avoid host throw error on eject
				if (request.opcode == IUSBSCSI.OPCODE_EJECT)// eject command for CD image/drive
				{
					if (request.Lba == 2) 
						cdImageEjected = true;
				} 
			} catch (Exception e) {
				Debug.out.println(e);
				if (!stopRunning) {
					//KVM reconnect is enabled
					if(JViewer.isKVMReconnectEnabled()){
						if((JViewerApp.getInstance().GetRedirectionState()!= JViewerApp.REDIR_STOPPING) &&
								(JViewerApp.getInstance().GetRedirectionState()!= JViewerApp.REDIR_STOPPED)){
							//Reconnect in progress
							if(JViewerApp.getInstance().getRetryConnection()){
								//Just return from here when reconnect in progress.
								//so that after reconnect media redirection will be connected back
								Debug.out.println("\n CD redierction thread returned .because of KVM Reconnect in progress\n");
								return;
							}
						}
					}
					synchronized (getSyncObj()) {
						try {
							if(!confModified)
							{

								getSyncObj().wait(10000);//wait for 10 seconds to check if service configuration change packet arrives from adviser
								if(cdStoppedByUser == true)
								{
									//If user disconnect exit from here
									return;
								}
							}
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					if(!confModified){
						VMApp.getInstance().getIUSBRedirSession().setCDStopMode(IUSBRedirSession.STOP_ON_CONNECTION_LOSS,CDDevice_no);
					}
					else
						confModified = false;
					Get_notification_scsi_support=false;
					stopRedirectionAbnormal();
					return;
				}
			}
		}
		Debug.out.println("Exiting the CDROM/ISO Redirection thread");
		return;
	}

	/**
	 * For redirection sessions with an actual cdrom drive, this returns an
	 * array of strings that represent cdrom device paths Not valid for iso
	 * image redirection sessions.
	 *
	 * @return An array of strings of cdrom drives on success
	 * @return null on error
	 * @throws RedirectionException
	 * if this is not a physical cdrom drive redirection session
	 */
	public String[] getCDROMList() throws RedirectionException {
		if (!physicalDrive) {
			DisplayErrorMsg(LocaleStrings.getString("4_15_CDROMREDIR"));
			return (null);
		}

		if (nativeReaderPointer == -1)
			newCDROMReader(true);

		String[] cdromlist = listCDROMDrives();

		if (nativeReaderPointer == -1)
			deleteCDROMReader();

		return (cdromlist);
	}

	/**
	 * Query the LIBCDROM native library and get its version
	 * @return
	 */
	public String getLIBCDROMVersion() {
		String version;

		if (nativeReaderPointer == -1) {
			newCDROMReader(false);
			version = getVersion();
			deleteCDROMReader();
		} else
			version = getVersion();

		return (version);
	}

	/**
	 * Handle errors during redirection
	 */
	public void DisplayErrorMsg(String str) {
		if(JViewer.isVMApp()){
			VMApp.getInstance().generalErrorMessage(
					LocaleStrings.getString("4_16_CDROMREDIR"), str);
		}else{
			JViewerApp.getInstance().getMainWindow().generalErrorMessage(
					LocaleStrings.getString("4_16_CDROMREDIR"), str);
		}
	}

	/**
	 * Handle errors during redirection
	 */
	public void handleError(String str) {
		DisplayErrorMsg(str);
	}

	/***
	 * return the SourceDrive name of the CDROM
	 * @return
	 */

	public String getSourceDrive() {
		return sourceCDROM;
	}

	/**
	 * Returns true if this session is for an actual cdrom drive, and false for
	 * ISO images
	 */
	public boolean isPhysicalDevice() {
		return (physicalDrive);
	}

	/***
	 * Send the Authentication token packet to CDserver for validate the client
	 * @param session_token - Session token received as argument
	 * @throws RedirectionException
	 * @throws IOException
	 */

	public void SendAuth_SessionToken(String session_token) throws RedirectionException, IOException {
		
		int dataLen = 0;
		int session_token_type = JViewerApp.getInstance().getSessionTokenType();

		if (session_token_type == IVTPPktHdr.WEB_SESSION_TOKEN)
		{
			dataLen = IUSBSCSI.IUSB_SCSI_PKT_SIZE - IUSBHeader.HEADER_LEN + IUSBRedirSession.WEB_AUTH_PKT_MAX_SIZE;
			packetWriteBuffer.clear();
			packetWriteBuffer.limit(IUSBSCSI.IUSB_SCSI_PKT_SIZE	+ IUSBRedirSession.WEB_AUTH_PKT_MAX_SIZE);
		}
		else if (session_token_type == IVTPPktHdr.SSI_SESSION_TOKEN)
		{
			dataLen = IUSBSCSI.IUSB_SCSI_PKT_SIZE - IUSBHeader.HEADER_LEN + IUSBRedirSession.SSI_AUTH_PKT_MAX_SIZE;
			packetWriteBuffer.clear();
			packetWriteBuffer.limit(IUSBSCSI.IUSB_SCSI_PKT_SIZE	+ IUSBRedirSession.SSI_AUTH_PKT_MAX_SIZE);
		}

		IUSBHeader AuthPktIUSBHeader = new IUSBHeader(dataLen);
		AuthPktIUSBHeader.write(packetWriteBuffer);
		packetWriteBuffer.position(IUSBSCSI.IUSB_SCSI_OPCODE_INDEX); // Opcode for SCSI command packet;
		packetWriteBuffer.put((byte) (AUTH_CMD & 0xff));
		packetWriteBuffer.position(IUSBSCSI.IUSB_SCSI_PKT_SIZE);
		packetWriteBuffer.put((byte) 0); // authpacket flags;
		packetWriteBuffer.put(session_token.getBytes());
		packetWriteBuffer.position(23);
		packetWriteBuffer.put((byte)CDDevice_no);
		packetWriteBuffer.position(0);
		IUSBSCSI pkt = new IUSBSCSI(packetWriteBuffer, true);
		packetMaster.sendPacket(pkt);
	}

	/**
	 * Method returns the data transfer rate
	 * @return
	 */
	public int getBytesRedirected() {
		return nBytesRedirected;
	}
	public static Object getSyncObj() {
		return syncObj;
	}
	/**
	 * @return the confModified
	 */
	public boolean isConfModified() {
		return confModified;
	}
	/**
	 * @param confModified the confModified to set
	 */
	public void setConfModified(boolean confModified) {
		this.confModified = confModified;
	}
	/**
	 * @return the cdInstanceNum
	 */
	public int getCdInstanceNum() {
		return cdInstanceNum;
	}
	/**
	 * @return the cdImageRedirected
	 */
	public boolean isCdImageRedirected() {
		return cdImageRedirected;
	}
	/**
	 * @param cdImageRedirected the cdImageRedirected to set
	 */
	public void setCdImageRedirected(boolean cdImageRedirected) {
		this.cdImageRedirected = cdImageRedirected;
	}
	/**
	 * @return the cdImageEjected
	 */
	public boolean isCdImageEjected() {
		return cdImageEjected;
	}
	/**
	 * @return the cdRedirectionKilled
	 */
	public boolean isCdRedirectionKilled() {
		return cdRedirectionKilled;
	}
	/**
	 * @param cdReconnect the cdReconnect to set
	 */
	public void setCdReconnect(boolean cdReconnect)
	{
		this.cdReconnect = cdReconnect;
	}
	/**
	 * @return the cdReconnect
	 */
	public boolean getCdReconnect()
	{
		return cdReconnect;
	}
	public boolean isCdStoppedByUser() {
		return cdStoppedByUser;
	}
	public void setCdStoppedByUser(boolean cdStoppedByUser) {
		this.cdStoppedByUser = cdStoppedByUser;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public void setCdRedirectionKilled(boolean cdRedirectionKilled) {
		this.cdRedirectionKilled = cdRedirectionKilled;
	}
	/**
	 * @return the cdromRedirStatus
	 */
	public int getCdromRedirStatus() {
		return cdromRedirStatus;
	}
	/**
	 * @param cdromRedirStatus the cdromRedirStatus to set
	 */
	public void setCdromRedirStatus(int cdromRedirStatus) {
		this.cdromRedirStatus = cdromRedirStatus;
	}
	/**
	 * @return the cdROMDeviceStatus
	 */
	public int getCdROMDeviceStatus() {
		return cdROMDeviceStatus;
	}
	/**
	 * @param cdROMDeviceStatus the cdROMDeviceStatus to set
	 */
	public void setCdROMDeviceStatus(int cdROMDeviceStatus) {
		this.cdROMDeviceStatus = cdROMDeviceStatus;
	}
	/**
	 * @return the cdStopMode
	 */
	public int getCdStopMode() {
		return this.cdStopMode;
	}
	/**
	 * @param cdStopMode the cdStopMode to set
	 */
	public void setCdStopMode(int cdStopMode) {
		this.cdStopMode = cdStopMode;
	}
	public boolean isCdServiceRestarted() {
		return cdServiceRestarted;
	}

	/**
	 * @return the lastPktRcvdTime
	 */
	public long getLastPktRcvdTime() {
		return lastPktRcvdTime;
	}
	/**
	 * @param lastPktRcvdTime the lastPktRcvdTime to set
	 */
	public void setLastPktRcvdTime(long lastPktRcvdTime) {
		this.lastPktRcvdTime = lastPktRcvdTime;
	}
	/**
	 * @return the lastPktSentTime
	 */
	public long getLastPktSentTime() {
		return lastPktSentTime;
	}
	/**
	 * @param lastPktSentTime the lastPktSentTime to set
	 */
	public void setLastPktSentTime(long lastPktSentTime) {
		this.lastPktSentTime = lastPktSentTime;
	}

	/**
	 * Sends the specified IUSB command to the media server.
	 * @param iUSBCmd - The IUSB command to be sent to the media server.
	 * @throws RedirectionException
	 * @throws IOException
	 */
	public void sendCommandToServer(int iUSBCmd) throws RedirectionException, IOException{
		IUSBSCSI.sendCommandToMediaServer(packetMaster, packetWriteBuffer, null, iUSBCmd);
	}
	/**
	 * Shows the error message if loading the native library fails
	 */
	private static void showLibraryLoadError(){
		if(JViewer.isVMApp() || JViewer.isStandalone()){
			JOptionPane.showMessageDialog(VMApp.getVMFrame(), LocaleStrings.getString("6_53_IUSBREDIR"),
					LocaleStrings.getString("1_3_JVIEWER"), JOptionPane.ERROR_MESSAGE);
			VMApp.exit(0);
		}
	}
}
