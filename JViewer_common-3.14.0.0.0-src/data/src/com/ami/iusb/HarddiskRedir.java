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

import java.util.UUID;

import javax.swing.JOptionPane;

import com.ami.iusb.protocol.HarddiskProtocol;
import com.ami.iusb.protocol.IUSBSCSI;
import com.ami.iusb.protocol.PacketMaster;
import com.ami.kvm.imageredir.IUSBHeader;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.InfoDialog;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.kvm.jviewer.gui.StandAloneConnectionDialog;
import com.ami.kvm.jviewer.kvmpkts.IVTPPktHdr;
import com.ami.vmedia.VMApp;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import javax.swing.JDialog;
import javax.swing.JButton;

public class HarddiskRedir extends Thread {

	private PacketMaster packetMaster;
	private HarddiskProtocol protocol;
	private ByteBuffer packetReadBuffer;
	private ByteBuffer packetWriteBuffer;
	private boolean physicalDevice;
	private int  HDDevice_no;
	private boolean running = false;
	private boolean stopRunning = false;
	private long nativeReaderPointer = -1;
	private Thread redirThread;
	private String sourceHarddisk = null;
	private int nBytesRedirected = 0;
	private int hdInstanceNum;
	private boolean hdReconnect = false;

	private static final int DEVICE_REDIRECTION_ACK = 0xf1;
	private static final int AUTH_CMD = 0xf2;
	private static final int SET_HARDDISK_TYPE = 0xf4;
	private static final int MAX_READ_SECTORS = 256;
	private static final int MAX_READ_SIZE = 512 * MAX_READ_SECTORS;	
	private boolean hdImageEjected = false;
	private  boolean hdImageRedirected = false;
	private  boolean hdServiceRestarted = false;
	public static String PHYSICAL_DRIVE = LocaleStrings.getString("A_5_DP");
	public static String LOGICAL_DRIVE = LocaleStrings.getString("A_6_DP");
	public static final int SECTOR_RANGE_ERROR	=	-1;
	public static final int ALREADY_IN_USE	=	-3;
	public static final int MEDIA_ERROR	=	-4;

	int  Drive_Type;
	String[] ListDrive_USB = null;
	String[] ListDrive_Fixed = null;
	private vHarddiskMonitorThread vMThread = null;
	private String errorMessage = " ";
	private long lastPktRcvdTime = 0;
	private long lastPktSentTime = 0;
	
	// Native library call
	private native String[] listHardDrivesFixed();
	private native String[] listHardDrives();
	private native void newHarddiskReader(boolean physicalDevice);
	private native void deleteHarddiskReader();
	private native int openHarddisk(byte[] bs,boolean physicalDevice);
	private native void closeHarddisk();
	private native int executeHarddiskSCSICmd(ByteBuffer scsiRequest, ByteBuffer scsiResponse);
	private native String getVersion();
	private static Object syncObj = new Object();
	private  boolean confModified = false;
	private boolean hdRedirectionKilled = false;
	private  boolean hdStoppedByUser = false;
	private	int harddiskRedirStatus;
	private	int hardDiskDeviceStatus;
	private int hdStopMode;

	private String folderPath = null;
	private String imagePath = null;
	private long imageSize = 0;
	private long ImageCreateTime = 0;
	private boolean folderMounted = false;
	public ImageProgress progress = null;
	private static final int ACTIONFLAGCREATE = 1;
	private static final int ACTIONFLAGSYNC = 2;
	private static String SHAREFOLDERSPLITSTRING = " : ";
	/***
	 * Loading the Library for Acccesing the hard disk
	 */
	static {
		try {
			if( !JViewer.isdownloadapp() && !JViewer.isplayerapp())
			{
				if(JViewer.isjviewerapp()){// Normal JViewer
					System.loadLibrary("javaharddiskwrapper");
				}
				else { //For SPXMultiViewer and StandAloneApp
					loadWrapperLibrary();
				}
			}
		} catch (UnsatisfiedLinkError e) {
			System.err.println(LocaleStrings.getString("AB_2_HDREDIR"));
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
				//Get the current working path of the JViewer, in the case of StandAloneApp to load the libraries
				commonPath = StandAloneConnectionDialog.replaceAllPattern(commonPath, ":", "_");
				libPath = currPath +commonPath+"javaharddiskwrapper.dll";	
				libFile = new File(libPath);
				if(false == StandAloneConnectionDialog.getWrapperLibrary("javaharddiskwrapper.dll")){
					Debug.out.println("Unable to extract the javaharddiskwrapper.dll");
					libPath = null;
				}
			}
			else
				libPath = System.getProperty("user.dir")+commonPath+"javaharddiskwrapper.dll";
		}
		else if(System.getProperty("os.name").startsWith("Linux")){
			if(JViewer.isStandAloneApp() || JViewer.isVMApp()){
				libPath = currPath+commonPath+"libjavaharddiskwrapper.so";
				libFile = new File(libPath);
				if(false == StandAloneConnectionDialog.getWrapperLibrary("libjavaharddiskwrapper.so")){
					Debug.out.println("Unable to extract the libjavaharddiskwrapper.so");
					libPath = null;
				}
			}
			else
				libPath = System.getProperty("user.dir")+commonPath+"libjavaharddiskwrapper.so";
		}
		else if(System.getProperty("os.name").startsWith("Mac")){
			if(JViewer.isStandAloneApp() || JViewer.isVMApp()){
				libPath = currPath+commonPath+"libjavaharddiskwrapper.jnilib";
				libFile = new File(libPath);
				if(false == StandAloneConnectionDialog.getWrapperLibrary("libjavaharddiskwrapper.jnilib")){
					Debug.out.println("Unable to extract the libjavaharddiskwrapper.jnlib");
					libPath = null;
				}

			}
			else
			libPath = System.getProperty("user.dir")+commonPath+"libjavaharddiskwrapper.jnilib";
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
	 * Creates a new instance of HarddiskRedir
	 * @param physicalDevice
	 */
	public HarddiskRedir(boolean physicalDevice) {
		this.physicalDevice = physicalDevice;
		harddiskRedirStatus = IUSBRedirSession.DEVICE_REDIR_STATUS_IDLE;
		hardDiskDeviceStatus = IUSBRedirSession.DEVICE_FREE;
		hdStopMode = IUSBRedirSession.STOP_NORMAL;
		protocol = new HarddiskProtocol();
		packetReadBuffer = ByteBuffer.allocateDirect(MAX_READ_SIZE
				+ IUSBHeader.HEADER_LEN
				+ IUSBSCSI.IUSB_SCSI_PKT_SIZE_WITHOUT_HEADER);
		packetWriteBuffer = ByteBuffer.allocateDirect(MAX_READ_SIZE
				+ IUSBHeader.HEADER_LEN
				+ IUSBSCSI.IUSB_SCSI_PKT_SIZE_WITHOUT_HEADER);
	}

	/**
	 * Creating the request buffer and response buffer for hard disk data transfer
	 *
	 * @param host
	 * @param port
	 * @param bVMUseSSL
	 * @throws IOException
	 */
	private int harddiskConnect(String host, int port, boolean bVMUseSSL)
			throws IOException {
		packetMaster = new PacketMaster(host, port, false, protocol, bVMUseSSL);
		packetMaster.setupBuffers(packetReadBuffer, packetWriteBuffer);
		packetMaster.setBufferEndianness(ByteOrder.LITTLE_ENDIAN, ByteOrder.LITTLE_ENDIAN);

		if(JViewer.isSinglePortEnabled()){

			if( JViewerApp.getInstance().getSinglePortKvm().setHTTPConnect("HDMEDIA") < 0){			
				VMApp.getInstance().getIUSBRedirSession().setHDStopMode(IUSBRedirSession.STOP_PORT_NOT_IN_LISTEN,HDDevice_no);
				return -1;
			}
			packetMaster.setSock(JViewerApp.getInstance().getSinglePortKvm().getHttpsock());

		}
		else{
			if(packetMaster.connectVmedia(bVMUseSSL) < 0 ){	
				VMApp.getInstance().getIUSBRedirSession().setHDStopMode(IUSBRedirSession.STOP_PORT_NOT_IN_LISTEN, HDDevice_no);
				return -1;
			}
		}
		return 0;
	}

	/**
	 * Closing the Vmedia socket close
	 *
	 */
	private void harddiskDisconnect() {
		try {
			/* Close the connection */
			packetMaster.VmediaSockclose();
		} catch (IOException e) {
			System.err.println(LocaleStrings.getString("AB_5_HDREDIR")+ e.getMessage());
		}
	}

	public void stopCreateImageProgress() {
		if(progress != null) {
			progress.stopProgress();
		}
	}

	/*
	action_flag - 1, using create image progress
	action_flag - 2, using synchronized to disk progress
	return value - false, failed to create / synchronized
	*/
	public boolean createImageProgressBar(int action_flag) {
		//create progress bar and then create image
		JLabel progressState = null;
		JProgressBar progressBar = new JProgressBar(0, 100);;
		JDialog dialog = null;
		JButton cancelBtn = new JButton(LocaleStrings.getString("AB_14_HDREDIR"));
		JPanel panel = new JPanel();
		Object[] options = {LocaleStrings.getString("AB_14_HDREDIR")};
		String progressTitle, popupTitle;
		int result = 0;
		
		progress = new ImageProgress(imageSize, folderPath, imagePath, progressBar);
		panel.setLayout(new BorderLayout());
		
		if(action_flag == ACTIONFLAGCREATE) {//create image progress
			try{
				if (!progress.checkValidPath()) {
					progress = null;
					System.gc();
					setErrorMessage(LocaleStrings.getString("AB_27_HDREDIR"));
					Debug.out.println(LocaleStrings.getString("AB_27_HDREDIR"));

					return false;
				}

				//it will create image error cause there have any file size get fail.
				if ( !progress.getImageSizeCheck() ) {
					//delete if any old image exist in same path with same name
					File imageFile = new File(imagePath);
					if(imageFile.exists()) {
						imageFile.delete();
					}
					progress = null;
					System.gc();
					setErrorMessage(LocaleStrings.getString("AB_17_HDREDIR"));
					return false;
				}			
				progressTitle = LocaleStrings.getString("AB_15_HDREDIR");
				popupTitle = LocaleStrings.getString("AB_16_HDREDIR");
				progress.setActionFlag(ACTIONFLAGCREATE);
			}
			catch (Exception e) {
				setErrorMessage(LocaleStrings.getString("AB_26_HDREDIR"));
				Debug.out.println("Get folder size error: " + e.getMessage());  
				return false;
			}
		}
		else {//synchronized to disk progress	
			boolean folderWritable = checkFolderWritable(folderPath);			
			if(folderWritable == false) {				
				//folder read-only ,so it dose not need to synchronized back to client side
				//just call system.gc and return
				progress = null;
				System.gc();
				return true;
			} 
		
			int dialogResult = JOptionPane.showConfirmDialog (null, 
										LocaleStrings.getString("AB_18_HDREDIR"),//message
										LocaleStrings.getString("AB_19_HDREDIR"),//icon type
										JOptionPane.YES_NO_OPTION);//button type
										
			if(dialogResult == JOptionPane.NO_OPTION || dialogResult == JOptionPane.CLOSED_OPTION){
			//double confirm user's action,make sure it's user want do it					
				dialogResult = JOptionPane.showConfirmDialog (null, 
												LocaleStrings.getString("AB_25_HDREDIR"),//message
												LocaleStrings.getString("AB_19_HDREDIR"),//icon type
												JOptionPane.YES_NO_OPTION);//button type
				if(dialogResult == JOptionPane.YES_OPTION){  
					//user choices does need to synchronized to disk
					//just call system.gc and return
					progress = null;
					System.gc();
					return true;
				}
			}
			progressTitle = LocaleStrings.getString("AB_20_HDREDIR");
			popupTitle = LocaleStrings.getString("AB_21_HDREDIR");
			progress.setActionFlag(ACTIONFLAGSYNC);
			progress.setImageCreateTime(ImageCreateTime);


		}
		progressState = new JLabel(progressTitle);
		progressBar.setValue(0);
		panel.add(progressState, BorderLayout.NORTH);
		panel.add(progressBar, BorderLayout.CENTER);

		//Here will start progressing, and also start to accessing create image / synchronized to disk
		progress.start();
		if(action_flag == ACTIONFLAGCREATE) {
			ImageCreateTime = progress.getImageCreateTime();
		}

		result = JOptionPane.showOptionDialog(null,
						panel,//message content
						popupTitle,//title
						JOptionPane.OK_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						null,//custom icon
						options,
						options[0]);

		//popup WARNING_MESSAGE to notice user if cancel sync files wil be removed
		if(result == 0 && action_flag == ACTIONFLAGSYNC){
			progress.pauseProgress();
			result = JOptionPane.showConfirmDialog (null, 
												LocaleStrings.getString("AB_29_HDREDIR"),//message
												LocaleStrings.getString("AB_19_HDREDIR"),//icon type
												JOptionPane.OK_CANCEL_OPTION,//button type
												JOptionPane.WARNING_MESSAGE);
			if(result == JOptionPane.CANCEL_OPTION)
			{
				progress.resumeProgress();

				panel.add(progressState, BorderLayout.CENTER);
				panel.add(progressBar, BorderLayout.SOUTH);

		    	result = JOptionPane.showOptionDialog(null,
							panel,//message content
							popupTitle,//title
							JOptionPane.OK_OPTION,
							JOptionPane.PLAIN_MESSAGE,
							null,
							new Object[]{},//no option
							new Object());//no option

				progress.resumeProgressBar();
			}
			progress.resumeProgress();
		}

		if( (progress != null) && (!progress.getUserChoose()) )
			progress.stopProgress();
		if(progress.progress_complete == false) {
			//user press cancel or X button
			if(progress.progress_result == -1) {
				setErrorMessage(LocaleStrings.getString("6_40_IUSBREDIR") + LocaleStrings.getString("AB_22_HDREDIR"));
			}
			else if(progress.progress_result == -2)
				setErrorMessage(LocaleStrings.getString("6_40_IUSBREDIR") + LocaleStrings.getString("AB_23_HDREDIR"));
			else
				setErrorMessage(LocaleStrings.getString("6_40_IUSBREDIR"));
			progress = null;
			System.gc();
			return false;
		}
		else {
			progress = null;
			System.gc();
			return true;
		}
	}

	/**
	 * Starting the hard disk redirection
	 *
	 * @param host -
	 *            Hostname to be connect
	 * @param hard diskDrive -
	 *            client drive name or drive path
	 * @param token -
	 *            session token
	 * @param port -
	 *            port to be connected
	 * @param bVMuseSSL -
	 *            ssl or nonssl socket
	 * @return
	 * @throws RedirectionException
	 */
	public boolean startRedirection(String host, String hardDrive,int hddevice_no,
			String token, int port, boolean bVMuseSSL)
			throws RedirectionException {
		boolean folderWritable = false;
		if (running)
			return (true);

		if(!isPhysicalDevice()) 
		{
			if((hardDrive.indexOf(SHAREFOLDERSPLITSTRING) != -1))
			{
				ImageCreateTime = 0;
				String[] tmpAry = hardDrive.split(SHAREFOLDERSPLITSTRING);
				//tmpAry[0] - size
				//tmpAry[1] - folder path
				//tmpAry[2] - temp image path
				if( tmpAry[2].endsWith(File.separator) )
					tmpAry[2] = tmpAry[2] + "AMI_" + hddevice_no + ".img";
				else
					tmpAry[2] = tmpAry[2] + File.separator + "AMI_" + hddevice_no + ".img";
		
				imageSize = Integer.parseInt(tmpAry[0]);
				folderPath = tmpAry[1];
				imagePath = tmpAry[2];	
				folderWritable = checkFolderWritable(folderPath);
				if(folderWritable == false) {
					JOptionPane.showMessageDialog(null, 
										LocaleStrings.getString("AB_24_HDREDIR"),//message
										LocaleStrings.getString("AB_19_HDREDIR"),//title
										JOptionPane.ERROR_MESSAGE);//icon type
				} 				

				if(createImageProgressBar(ACTIONFLAGCREATE)){//success to create Image file
					hardDrive = imagePath;
					folderMounted = true;

				}
				else {
					File tmp = new File(imagePath);
					if(tmp.exists()) {
						tmp.delete();
		
					}
//					setErrorMessage(LocaleStrings.getString("6_40_IUSBREDIR"));
					
					return (false);
				}
				if(folderWritable == false){
					//can't write,mount as read-only image
					File tmp = new File(imagePath);
					tmp.setReadOnly();
				}
			}
		
		}

		HDDevice_no = hddevice_no;
		IUSBRedirSession iusbRedirSession = null;
		iusbRedirSession = VMApp.getInstance().getIUSBRedirSession();
		try {
			if(harddiskConnect(host, port, bVMuseSSL) < 0)
				return false;
			SendAuth_SessionToken(token);
			IUSBSCSI.SendMediaInfo(packetMaster, packetWriteBuffer, hardDrive);
			/* Get the first request from the card - it has a special value */
			IUSBSCSI request = recvRequest();
			hdInstanceNum = request.instanceNum;
			
			if (request.opcode == DEVICE_REDIRECTION_ACK) {
				/* Did we get the connection? */
				if(request.connectionStatus == IUSBRedirSession.CONNECTION_PERM_DENIED){
					harddiskDisconnect();
					setErrorMessage(LocaleStrings.getString("4_17_CDROMREDIR"));
					return (false);
				}
				else if (request.connectionStatus == IUSBRedirSession.CONNECTION_MAX_USER) {
					harddiskDisconnect();
					setErrorMessage(LocaleStrings.getString("AB_8_HDREDIR") + request.m_otherIP);
					return (false);
				}
				else if (request.connectionStatus == IUSBRedirSession.LICENSE_EXPIRED) {
					harddiskDisconnect();
					setErrorMessage(LocaleStrings.getString("F_136_JVM"));
					return (false);
				}
				else if (request.connectionStatus != IUSBRedirSession.CONNECTION_ACCEPTED) {
					harddiskDisconnect();

					if( ( request.connectionStatus == IUSBRedirSession.CONNECTION_INVALID_SESSION_TOKEN ) && ( JViewer.isVMApp() == true )){
						stopRedirection();
						setErrorMessage(LocaleStrings.getString("6_52_IUSBREDIR"));
					}
					else if (request.m_otherIP != null) {
						//for local/remote media connection
						if((request.m_otherIP.equalsIgnoreCase("127.0.0.1")) ||
								(request.m_otherIP.equalsIgnoreCase("::1")))
							setErrorMessage(LocaleStrings.getString("4_19_CDROMREDIR"));
						else
							setErrorMessage(LocaleStrings.getString("4_7_CDROMREDIR") + request.m_otherIP);
					}
					return (false);
				}
			} else {
				harddiskDisconnect();
				throw new RedirectionException(LocaleStrings.getString("4_8_CDROMREDIR")+ request.opcode);
			}
		} catch (IOException e) {
			throw new RedirectionException(e.getMessage());
		}

		/* Create the hard disk reader */
		if (nativeReaderPointer == -1)
			newHarddiskReader(physicalDevice);
		  
		if(System.getProperty("os.name").startsWith("Windows"))
		{
			sourceHarddisk = hardDrive =hardDrive.trim();
			if(isPhysicalDevice()){
				String remove = hardDrive.substring(hardDrive.indexOf('['));
				hardDrive = hardDrive.substring(0,hardDrive.indexOf('['));
				//An extra space ' ' and '-' is added to the drive type to make it look like
				//"Physical Drive-0" or "Ligical Drive-0".
				//While sending it to the library, it should be like "PhysicalDrive0" or
				//LogicalDrive-0. So here we remove those extra charecters included.
				String removeSpace = "";
				int j = 0;
				//Remove the first ' ' character in drive name.
				while(j < hardDrive.lastIndexOf('-')+1){
					char ch = hardDrive.charAt(j++);
					if(' ' == ch)
						continue;
					removeSpace += ch;
				}
				hardDrive = removeSpace;
				//Remove the first '-' character in drive name with ''.
				// While redirecting the whole physical drive, the drive name should be in the format PhysicalDrive1 without any space inbetween to work properly.
				hardDrive = hardDrive.replaceFirst("-", "");
				j=0;
				while(j <remove.indexOf(']')){
					char ch = remove.charAt(j);
					if(ch != '[' && ch != ']' &&ch != '-')
					hardDrive+=ch;
					j++;
				}
				hardDrive += remove.substring(j+1);
			}
		}
		else
		{
			if(isPhysicalDevice())
			{
				String[] sourceHarddisk_temp = hardDrive.split("-");
				sourceHarddisk = hardDrive;
				hardDrive = sourceHarddisk_temp[0].trim();
			}
			else
			{
				sourceHarddisk = hardDrive =hardDrive.trim();
			}
		}
		/* Open the Harddrive */
		try{
			String HDDrive = hardDrive;
			//for locale language change. Library expects in english format
			if(JViewer.getLanguage() != JViewer.DEFAULT_LOCALE){
				if(hardDrive.startsWith(LocaleStrings.getString("AB_9_HDREDIR")))
				{
					HDDrive = "PhysicalDrive"+hardDrive.substring(hardDrive.indexOf('-')-1);
				}
				else if(hardDrive.startsWith(LocaleStrings.getString("AB_10_HDREDIR")))
				{
					HDDrive = "LogicalDrive"+hardDrive.substring(hardDrive.indexOf('-')-1);
				}
			}
			int Open_ret = openHarddisk(HDDrive.getBytes("UTF-8"),isPhysicalDevice());
			if (Open_ret < 0) {
				if(Open_ret == ALREADY_IN_USE){
					if(iusbRedirSession.isHarddiskPhysicalDrive(HDDevice_no))
					{
						setErrorMessage(LocaleStrings.getString("6_37_IUSBREDIR"));
					}
					else
					{
						setErrorMessage(LocaleStrings.getString("AB_12_HDREDIR"));
					}
				}
				else if(Open_ret == MEDIA_ERROR || Open_ret == SECTOR_RANGE_ERROR){
					setErrorMessage(LocaleStrings.getString("AB_11_HDREDIR"));
				}
				System.err.println(LocaleStrings.getString("AB_6_HDREDIR"));
				deleteHarddiskReader();
				harddiskDisconnect();
				return (false);
			}
			if(Open_ret == IUSBRedirSession.READ_ONLY) {
				//make sure it action from HD/USB image
				if((folderMounted == false) && ((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_REDIR_RD_WR_MODE) == JViewerApp.OEM_REDIR_RD_WR_MODE)){
					if(iusbRedirSession.isHarddiskPhysicalDrive(HDDevice_no))
					{
						setErrorMessage(LocaleStrings.getString("6_37_IUSBREDIR"));
					}
					else
					{
						setErrorMessage(LocaleStrings.getString("AB_13_HDREDIR"));
					}
					System.err.println(LocaleStrings.getString("AB_6_HDREDIR")+Open_ret);
					deleteHarddiskReader();
					harddiskDisconnect();
					return (false);

				}
				if (!JViewer.isVMApp()) {
					if (JViewerApp.getInstance().getRetryConnection() == false) {
						iusbRedirSession.updateRedirectionStatus(VMApp.DEVICE_TYPE_HD_USB, hddevice_no,
								IUSBRedirSession.READ_ONLY);
					}
				} else {
					iusbRedirSession.updateRedirectionStatus(VMApp.DEVICE_TYPE_HD_USB, hddevice_no,
							IUSBRedirSession.READ_ONLY);
				}
			} else if (Open_ret == IUSBRedirSession.READ_WRITE) {
				if (!JViewer.isVMApp()) {
					if (JViewerApp.getInstance().getRetryConnection() == false) {
						iusbRedirSession.updateRedirectionStatus(VMApp.DEVICE_TYPE_HD_USB, hddevice_no,
								IUSBRedirSession.READ_WRITE);
					}
				} else {
					iusbRedirSession.updateRedirectionStatus(VMApp.DEVICE_TYPE_HD_USB, hddevice_no,
							IUSBRedirSession.READ_WRITE);
				}
				if(folderMounted == true){
					JOptionPane.showMessageDialog(null, 
											LocaleStrings.getString("AB_28_HDREDIR"),//message
											LocaleStrings.getString("A_6_GLOBAL"),//title
											JOptionPane.INFORMATION_MESSAGE);//icon type
				}
			}
		}catch(UnsupportedEncodingException e){System.out.println(LocaleStrings.getString("4_12_CDROMREDIR"));}
		if(!JViewer.isVMApp())
			JViewerApp.getInstance().getKVMClient().MediaRedirectionState((byte) 1);
		nBytesRedirected = 0;
		/* Start the hard disk redirection thread */
		redirThread = new Thread(this);
		redirThread.start();
		vMThread = new vHarddiskMonitorThread(HDDevice_no);
		vMThread.startharddiskMonitor();

		running = true;
		harddiskRedirStatus = IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED;
		return (true);
	}

	/**
	 * Stop the running hard disk redirection
	 *
	 * @return
	 */

	public boolean stopRedirection() {
		//Stop the HD redirection monitor thread
		if(vMThread != null){
			vMThread.stopHarddiskMonitor();
		}
		if (running) {
			try {
				// If the hdServerRestarted flag is set, then media server has stopped/restarted and notified JViewer about it.
				// So no need to send MEDIA_SESSION_DISCONNECT command to media server.
				if(hdServiceRestarted == false) {
					IUSBSCSI.sendCommandToMediaServer(packetMaster, packetWriteBuffer, null, IUSBSCSI.MEDIA_SESSION_DISCONNECT);
				}
			} catch (Exception e) {
				Debug.out.println("Sending MEDIA_SESSION_DISCONNECT command to media server failed : " + e);
			}
			
			stopRunning = true;
			harddiskDisconnect();
			try {
				redirThread.join();
			} catch (InterruptedException e) {
				System.err.println(LocaleStrings.getString("AB_7_HDREDIR"));
			}
			if(!JViewer.isVMApp())
				JViewerApp.getInstance().getKVMClient().MediaRedirectionState( (byte) 0);
			harddiskDisconnect();
			running = false;
			stopRunning = false;
			closeHarddisk();
			deleteHarddiskReader();
			checkFolderMountStatus();
		}
		nBytesRedirected = 0;
		harddiskRedirStatus = IUSBRedirSession.DEVICE_REDIR_STATUS_IDLE;
		return true;
	}
	
	private void checkFolderMountStatus() {
		if(folderMounted) {
			createImageProgressBar(ACTIONFLAGSYNC);
				
			File tmp = new File(imagePath);
			if(tmp.exists()) {
				tmp.delete();
			}
			folderMounted = false;
		}
	}

	/***
	 *
	 * @return calling the receive method in the packet master
	 * @throws IOException
	 * @throws RedirectionException
	 */
	private IUSBSCSI recvRequest() throws IOException, RedirectionException {
		return ((IUSBSCSI) packetMaster.receivePacket());
	}

	/***
	 * return the hard disk redirection is running or not
	 *
	 * @return
	 */
	public boolean isRedirActive() {
		return (running);
	}

	/***
	 * Return the Source drive name
	 * @return
	 */

	public String getSourceDrive() {
		return sourceHarddisk;
	}


	/****
	 * Must be called to stop hard disk redirection thread abnormally.
	 *
	 */

	public void stopRedirectionAbnormal() {
		//Stop the HD redirection monitor thread
		if(vMThread != null){
			vMThread.stopHarddiskMonitor();
		}
		if (running) {
			stopRunning = true;
			harddiskDisconnect();
			running = false;
			stopRunning = false;
			closeHarddisk();
			deleteHarddiskReader();
			JViewerApp.getInstance().reportHarddiskAbnormal(HDDevice_no);
			checkFolderMountStatus();
		}
		harddiskRedirStatus = IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED;
	}

	/**
	 * Creating a thread to send/receive the packet from the HDserver
	 */
	public void run() {
		IUSBSCSI request;
		IUSBSCSI response;
		int nTempLen = 0;

		while (!stopRunning) {
			try {
				packetWriteBuffer.rewind();
				/* Get a request from the card */
				request = recvRequest();
				
				if (request == null){
					continue;
				}
				//Update the last packet received time for the HD recirection session.
				lastPktRcvdTime = JViewerApp.getInstance().getCurrentTime();
				/* Execute the hard disk request */
				int dataLen = executeHarddiskSCSICmd(packetReadBuffer,	packetWriteBuffer);
				packetWriteBuffer.limit(dataLen);
				if (request.opcode == SET_HARDDISK_TYPE)
				{
					packetWriteBuffer.position(11);
					packetWriteBuffer.putInt(31);
					dataLen = dataLen+1;
					packetWriteBuffer.limit(dataLen);
					packetWriteBuffer.position(packetWriteBuffer.limit()-1);
					packetWriteBuffer.put((byte)getDrive_Type());
				}
				packetWriteBuffer.position(0);

				if(request.opcode == IUSBSCSI.OPCODE_KILL_REDIR)
				{
					Debug.out.println("EXIT COMMAND RECEIVED IN Harddisk : "+request.opcode );
					hdRedirectionKilled = true;
				}
				else if(request.opcode == IUSBSCSI.MEDIA_SESSION_DISCONNECT) {
					hdServiceRestarted = true;
					return;
				}
				/* Form the IUSB response packet */
				response = new IUSBSCSI(packetWriteBuffer, true);
				/* Send the IUSB response packet */
				packetMaster.sendPacket(response);
				//Set the last packet sent time, when responding to iUSB request packets.
				lastPktSentTime = JViewerApp.getInstance().getCurrentTime();
				nTempLen += dataLen;
				nBytesRedirected += (nTempLen / 1024);
				nTempLen = nTempLen % 1024;
				//handle eject command after sending response to host to avoid host throw error on eject
				if (request.opcode == IUSBSCSI.OPCODE_EJECT) { // eject command for hard disk imgae or hard disk drive
					if (request.Lba == 2)
						hdImageEjected = true;
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
								Debug.out.println("\n Harddisk redierction thread returned .because of KVM Reconnect in progress\n");
								return;
							}
						}
					}
					synchronized (getSyncObj()) {
						try {
							if(!confModified)
							{
								getSyncObj().wait(10000);//wait for 10 seconds to check if service configuration change packet arrives from adviser
								if(hdStoppedByUser == true)
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
						VMApp.getInstance().getIUSBRedirSession().setHDStopMode(IUSBRedirSession.STOP_ON_CONNECTION_LOSS,HDDevice_no);
					}
					else
						confModified = false;
					stopRedirectionAbnormal();
					return;
				}
			}
		}
		return;
	}

	/****
	 * Getting the hard disk list from the Native call
	 * if any drive name drive name duplicated removing here and copying in another array
	 *
	 * @return
	 */
	public String[] getEntireHarddiskList() {
		if (!physicalDevice) {
			DisplayErrorMsg("Cannot get Hard disk drive list during Hard disk IMAGE redirection");
			return (null);
		}

		if (nativeReaderPointer == -1)
			newHarddiskReader(true);

		String[] ListDrive_original = listHardDrives();
		String[] ListDriveFixed_original = listHardDrivesFixed();
		String[] ListDrive_filter = null;

		if( ListDrive_original != null && ListDriveFixed_original != null)
			ListDrive_filter = new String[(ListDrive_original.length + ListDriveFixed_original.length)];
		else if(ListDriveFixed_original == null)
			ListDrive_filter = new String[ListDrive_original.length];
		else if(ListDrive_original == null)
			ListDrive_filter = new String[ListDriveFixed_original.length];

		int counter=0;
		if(ListDrive_original != null)
		{
			for(int j=0;j<ListDrive_original.length ;j++)
			{
				ListDrive_filter[counter]= ListDrive_original[j];
				counter++;
			}
		}
		if(ListDriveFixed_original != null)
		{
			for(int j=0;j<ListDriveFixed_original.length ;j++)
			{
				ListDrive_filter[counter]= ListDriveFixed_original[j];
				counter++;
			}
		}
		return (ListDrive_filter);
	}

	/****
	 * Getting the USB/HDD removalble drive  from the Native call
	 * if any drive name drive name duplicated removing here and copying in another array
	 *
	 * @return
	 */
	public String[] getUSBHDDList() {
		if (!physicalDevice) {
			DisplayErrorMsg("Cannot get Hard Disk drive list during hard disk IMAGE redirection");
			return (null);
		}

		if (nativeReaderPointer == -1)
			newHarddiskReader(true);

		String[] ListDrive_original = listHardDrives();

		if(ListDrive_original == null)
			return ListDrive_original;
		PHYSICAL_DRIVE = LocaleStrings.getString("A_5_DP");
		LOGICAL_DRIVE = LocaleStrings.getString("A_6_DP");

		if(System.getProperty("os.name").startsWith("Win"))
		{
			for (int i = 0; i < ListDrive_original.length; i++) {
				String Drive = ListDrive_original[i];
				String Drive_no = Drive.substring(0,1);
				String Drive_name = Drive.substring(2);
				String Drive_name_append = "[";
				String Physicaldevice;
				int j=0;
				while(j < Drive_name.length()-1){
					Drive_name_append += (Drive_name.charAt(j)+"-");
					j++;
				}
				if (Drive_name.charAt(j) == 'l') {
					Drive_name_append = Drive_name_append.substring(0, Drive_name_append.length() - 1);
					Drive_name_append += "]";
					Physicaldevice = LOGICAL_DRIVE;
				} else {
					Drive_name_append += Drive_name.charAt(j)+"]";
					Physicaldevice = PHYSICAL_DRIVE;
				}
				String Wholedrive = Physicaldevice.concat("-").concat(Drive_no).concat("-").concat(Drive_name_append);
				ListDrive_original[i] = Wholedrive;
			}
		}
		else if(System.getProperty("os.name").equals("Linux")){
			for (int i = 0; i < ListDrive_original.length; i++) {
				String Drive = ListDrive_original[i];
				String Drive_name = Drive.substring(0,Drive.length()-1);
				ListDrive_original[i] = Drive_name;
			}
		}
		
		return (ListDrive_original);
	}


	/****
	 * Getting the HD list from the Native call
	 * if any drive name drive name duplicated removing here and copying in another array
	 *
	 * @return
	 */
	public String[] getHarddiskFixedList() {
		if (!physicalDevice) {
			DisplayErrorMsg("Cannot get hard disk drive list during Hard disk IMAGE redirection");
			return (null);
		}

		if (nativeReaderPointer == -1)
			newHarddiskReader(true);

		String[] ListDrive_original =listHardDrivesFixed();

		if(ListDrive_original == null)
			return null;
		
		PHYSICAL_DRIVE = LocaleStrings.getString("A_5_DP");
		LOGICAL_DRIVE = LocaleStrings.getString("A_6_DP");

		if(System.getProperty("os.name").startsWith("Win"))
		{
			for (int i = 0; i < ListDrive_original.length; i++) {
				String Drive = ListDrive_original[i];
				String Drive_no = Drive.substring(0,1);
				String Drive_name = Drive.substring(2);
				String Drive_name_append = "[";
				String Physicaldevice;
				int j=0;
				while(j < Drive_name.length()-1){
					Drive_name_append += (Drive_name.charAt(j)+"-");
					j++;
				}
				if (Drive_name.charAt(j) == 'l') {
					Drive_name_append = Drive_name_append.substring(0, Drive_name_append.length() - 1);
					Drive_name_append += "]";
					Physicaldevice = LOGICAL_DRIVE;
				} else {
					Drive_name_append += Drive_name.charAt(j)+"]";
					Physicaldevice = PHYSICAL_DRIVE;
				}
				String Wholedrive = Physicaldevice.concat("-").concat(Drive_no).concat("-").concat(Drive_name_append);
				ListDrive_original[i] = Wholedrive;
			}
		}
		else if(System.getProperty("os.name").equals("Linux")){
			for (int i = 0; i < ListDrive_original.length; i++) {
				String Drive = ListDrive_original[i];
				String Drive_name = Drive.substring(0,Drive.length()-1);
				ListDrive_original[i] = Drive_name;
			}
		}
		return (ListDrive_original);
	}

	/**
	 * Getting the libhd Version from the Native call
	 *
	 * @return
	 */
	public String getLIBHARDDISKVersion() {
		String version;

		if (nativeReaderPointer == -1) {
			newHarddiskReader(false);
			version = getVersion();
			deleteHarddiskReader();
		} else
			version = getVersion();

		return (version);
	}

	/**
	 * Display error messages
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

	/**
	 * Return the hard disk drive is physical or image redirection
	 * @return
	 */
	public boolean isPhysicalDevice() {
		return (physicalDevice);
	}

	/***
	 * Send the Authentication token packet to  the HDServer validate the client
	 * @param session_token - Session token received as argument
	 * @throws RedirectionException
	 * @throws IOException
	 */
	public void SendAuth_SessionToken(String session_token)
			throws RedirectionException, IOException {

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
		packetWriteBuffer.position(22);
		packetWriteBuffer.put((byte)getDrive_Type());
		packetWriteBuffer.put((byte)HDDevice_no);
		packetWriteBuffer.position(0);
		IUSBSCSI pkt = new IUSBSCSI(packetWriteBuffer, true);
		packetMaster.sendPacket(pkt);
	}

	/**
	 * Return the data transfer rate of the HD redirection
	 * @return
	 */
	public int getBytesRedirected() {
		return nBytesRedirected;
	}

	public int getDrive_Type() {
		Debug.out.println("GEt DRIVE TYPE:"+Drive_Type);
		return Drive_Type;
	}

	/**
	 * @return the syncObj
	 */
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
	public void setDrive_Type(int drive_Type) {
		Drive_Type = drive_Type;
	}
	/**
	 * @return the hdInstanceNum
	 */
	public int getHdInstanceNum() {
		return hdInstanceNum;
	}
	/**
	 * @return the hdImageRedirected
	 */
	public boolean isHdImageRedirected() {
		return hdImageRedirected;
	}
	/**
	 * @param hdImageRedirected the hdImageRedirected to set
	 */
	public void setHdImageRedirected(boolean hdImageRedirected) {
		this.hdImageRedirected = hdImageRedirected;
	}
	/**
	 * @return the hdImageEjected
	 */
	public boolean isHdImageEjected() {
		return hdImageEjected;
	}
	/**
	 * @return the hdRedirectionKilled
	 */
	public boolean isHdRedirectionKilled() {
		return hdRedirectionKilled;
	}
	/**
	 * @param hdReconnect the hdReconnect to set
	 */
	public void setHdReconnect(boolean hdReconnect)
	{
		this.hdReconnect = hdReconnect;
	}
	/**
	 * @return the hdReconnect
	 */
	public boolean getHdReconnect()
	{
		return hdReconnect;
	}
	public boolean isHdStoppedByUser() {
		return hdStoppedByUser;
	}
	public void setHdStoppedByUser(boolean hdStoppedByUser) {
		this.hdStoppedByUser = hdStoppedByUser;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public void setHdRedirectionKilled(boolean hdRedirectionKilled) {
		this.hdRedirectionKilled = hdRedirectionKilled;
	}
	/**
	 * @return the harddiskRedirStatus
	 */
	public int getHarddiskRedirStatus() {
		return harddiskRedirStatus;
	}
	/**
	 * @param harddiskRedirStatus the harddiskRedirStatus to set
	 */
	public void setHarddiskRedirStatus(int harddiskRedirStatus) {
		this.harddiskRedirStatus = harddiskRedirStatus;
	}
	/**
	 * @return the hardDiskDeviceStatus
	 */
	public int getHardDiskDeviceStatus() {
		return hardDiskDeviceStatus;
	}
	/**
	 * @param hardDiskDeviceStatus the hardDiskDeviceStatus to set
	 */
	public void setHardDiskDeviceStatus(int hardDiskDeviceStatus) {
		this.hardDiskDeviceStatus = hardDiskDeviceStatus;
	}
	/**
	 * @return the hdStopMode
	 */
	public int getHdStopMode() {
		return hdStopMode;
	}
	/**
	 * @param hdStopMode the hdStopMode to set
	 */
	public void setHdStopMode(int hdStopMode) {
		this.hdStopMode = hdStopMode;
	}
	public boolean isHdServiceRestarted() {
		return hdServiceRestarted;
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
	public void sendCommadToServer(int iUSBCmd) throws RedirectionException, IOException{
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
	/**
	 * @param folderPath - Check the folder of path
	 * @return Writable in the folder of path
	 */
	public boolean checkFolderWritable(String folderPath) {
		String uuid = UUID.randomUUID().toString().replace("-", "");

		//generate a random file name by uuid

		File file = new File(folderPath + File.separator + uuid);  		
		try {
			if (file.createNewFile()) {  
				Debug.out.println("File named " + folderPath+ File.separator + uuid   + " created successfully !");  						
				file.delete();
				return(true);
			} else {  
				Debug.out.println("File with name " + folderPath+ File.separator +uuid   + " already exists !"); 
				return(false);
			}  
		} catch (Exception e) {
			Debug.out.println("File named " + folderPath+ File.separator + uuid   + " created error :" + e.getMessage());  		
			return(false);
		}
	}

}
