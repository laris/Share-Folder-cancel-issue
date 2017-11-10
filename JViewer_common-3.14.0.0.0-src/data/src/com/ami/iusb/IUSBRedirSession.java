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
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.JVFrame;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.vmedia.VMApp;

public class IUSBRedirSession
{
	/** size of each authentication packets */
	public static final byte AUTH_USER_PKT_SIZE = 98; /** size of user credentials auth packet */
	public static final short WEB_AUTH_PKT_MAX_SIZE	= AUTH_USER_PKT_SIZE; /** Max of all above auth packet sizes */
	public static final short SSI_AUTH_PKT_MAX_SIZE	= AUTH_USER_PKT_SIZE+112;
	public static final int 	DEVICE_REDIR_STATUS_IDLE = 0;
	public static final int 	DEVICE_REDIR_STATUS_CONNECTED = 1;

	public static final int JVIEWER = 2; // 0-> LMedia 1->RMedia 3->H5Viewer 
	public static final int VMAPP = 4; // 5->VMCLI
	public static final int MAX_IMAGE_LENGTH = 256;
	/** client type is of int type. Integer.SIZE/8 is equal to sizeof(int) **/
	public static final int DEVICE_INFO_MAX_SIZE = MAX_IMAGE_LENGTH +( Integer.SIZE/8 );// max image length + size of client type

	public static final byte STOP_NORMAL = 0x00;
	public static final byte STOP_ON_EJECT = 0x01;
	public static final byte STOP_ON_TERMINATE = 0x02;
	public static final byte STOP_ON_DEVICE_REMOVAL = 0x03;
	public static final byte STOP_ON_SERVER_RESTART = 0x04;
	public static final byte STOP_ON_CONNECTION_LOSS =0x05;
	public static final byte STOP_PORT_NOT_IN_LISTEN =0x06;

	public static final byte DEVICE_USED = 0x00;
	public static final byte DEVICE_FREE = 0x01;

	public static final int CONNECTION_ACCEPTED = 1;
	public static final int CONNECTION_PERM_DENIED = 5;
	public static final int CONNECTION_INVALID_SESSION_TOKEN = 3;
	public static final int CONNECTION_MAX_USER = 8;
	public static final int LICENSE_EXPIRED = 13;

	public static final int LINUX_DRIVE_TYPE_USB = 0;
	public static final int LINUX_DRIVE_TYPE_HDD = 1;
	public static final int WINDOWS_DRIVE_TYPE_USB = 2;
	public static final int WINDOWS_DRIVE_TYPE_HDD = 3;
	public static final byte MEDIA_TYPE_HDD = 0x00;
	public static final byte MEDIA_TYPE_USB = (byte) 0x80;
	public static final int CONNECTION_IDLE_TIME = 60000;
	
	public static final int	READ_WRITE		=	0;
	public static final int	READ_ONLY		=	2;

	public CDROMRedir cdromSession[] ;
	public HarddiskRedir hardDiskSession[];

	private boolean cdButtonRedirState = false;
	private boolean hdButtonRedirState = false;

	private JTable statusTable;
	private JTable devStatusTable;
	private final int BYTES_READ_COL = 3;
	private final int REDIR_MODE_COL = 4;

	public static final int HDFOLDERREDIRLENGTH = 3;
	private static String SHAREFOLDERSPLITSTRING = " : ";

	/** Creates a new instance of videoRedirSession
	 *  @param host Hostname or IP address of the remote server */
	public IUSBRedirSession()
	{
		int cdnum = 0, hdnum = 0;

		cdnum = VMApp.getInstance().getNumCD();
		hdnum = VMApp.getInstance().getNumHD();
		cdromSession = new CDROMRedir[cdnum];
		hardDiskSession = new HarddiskRedir[hdnum];
	}
	/**
	 * Start CDROM redirection
	 * returns true if successful or false on failure.
	 */
	public boolean StartCDROMRedir(String token,int port,int cddevice_no, boolean bVMUseSSL,String cdromDrive)
	{
		String host = JViewer.getIp();
		if( host == null ) {
			showErrorMessage(LocaleStrings.getString("6_6_IUSBREDIR"),
					LocaleStrings.getString("6_1_IUSBREDIR")+LocaleStrings.getString("6_6_IUSBREDIR"));
			return false;
		}
		if( cdromDrive.length() == 0 ) {
			showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
					LocaleStrings.getString("6_2_IUSBREDIR")+LocaleStrings.getString("6_6_IUSBREDIR"));
			return false;
		}
		try
		{
			if( cdromSession[cddevice_no] != null ) {
				if( cdromSession[cddevice_no].isRedirActive() ) {
					showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
							LocaleStrings.getString("6_3_IUSBREDIR"));
					return false;
				}
				cdromSession[cddevice_no] = null;
				System.gc();
			}

			cdromSession[cddevice_no] = new CDROMRedir( true );
			if( !cdromSession[cddevice_no].startRedirection( host, cdromDrive ,cddevice_no,token,port,bVMUseSSL ) )
			{
				// check if there is an error message
				if(cdromSession[cddevice_no].getErrorMessage() == null && cdromSession[cddevice_no].getErrorMessage().length() == 0) {
					showErrorMessage(LocaleStrings.getString("6_6_IUSBREDIR"), 
							LocaleStrings.getString("6_4_IUSBREDIR"));
				} else if(cdromSession[cddevice_no].getErrorMessage() != " "){ // if so display the error message
					showErrorMessage(LocaleStrings.getString("6_6_IUSBREDIR"),
							cdromSession[cddevice_no].getErrorMessage());
				}

				// clear the error message
				cdromSession[cddevice_no].setErrorMessage(" ");

				StopCDROMRedir(cddevice_no, STOP_NORMAL);
				return false;
			}

		}
		catch( UnsatisfiedLinkError e )
		{
			/* We couldn't load the native CD-ROM library */
			showErrorMessage(LocaleStrings.getString("6_6_IUSBREDIR"),
					LocaleStrings.getString("6_5_IUSBREDIR"));
			StopCDROMRedir(cddevice_no, STOP_NORMAL);
			return false;
		}
		catch( RedirectionException e )
		{
			/* Something funky happened... */
			showErrorMessage(LocaleStrings.getString("6_6_IUSBREDIR"), e.getMessage());
			StopCDROMRedir(cddevice_no, STOP_NORMAL);
			return false;
		}
		catch(Exception e){
			/* Something funky happened... */
			showErrorMessage(LocaleStrings.getString("6_6_IUSBREDIR"), e.getMessage());
			StopCDROMRedir(cddevice_no, STOP_NORMAL);
			return false;
		}
		return true;
	}

	/**
	 * Stop CDROM redirection
	 * returns true if successful or false on failure.
	 */
	public boolean StopCDROMRedir(int cddevice_no, int stopMode)
	{
		int ret = 0;
		if( cdromSession[cddevice_no] != null ) {

			if ( cdromSession[cddevice_no].stopRedirection() == false ) {
				showErrorMessage(LocaleStrings.getString("6_33_IUSBREDIR"), 
						LocaleStrings.getString("6_7_IUSBREDIR"));
				return false;
			}

			if(cdromSession[cddevice_no].getCdReconnect() == true)
			{
				return true;
			}
			ret = cdromSession[cddevice_no].getCdStopMode();
			// Check whether port is in listen, if "port is not in listen" no need to update device status
			if(ret != IUSBRedirSession.STOP_PORT_NOT_IN_LISTEN){
				setCDStopMode(stopMode, cddevice_no);
			}
			cdromSession[cddevice_no] = null;

			/* Manually invoke garbage collector to reclaim memory from
			 * the cdromSession.  In particular, we want back the directly
			 * allocated memory, as there is an upper limit to how much
			 * of that we can have allocated at any given time, and directly
			 * allocated memory that hasn't yet been garbage collected counts
			 * against that limit. */
			System.gc();
		}
		return true;
	}

	/**
	 * Start ISO redirection
	 * returns true if successful or false on failure.
	 */
	public boolean StartISORedir(String token, int port,int cddevice_no, boolean bVMUseSSL,String filename)
	{
		String host = JViewer.getIp();
		if( host == null ) {
			showErrorMessage(LocaleStrings.getString("6_8_IUSBREDIR"),
					LocaleStrings.getString("6_1_IUSBREDIR")+
					LocaleStrings.getString("6_8_IUSBREDIR"));
			return false;
		}
		if( filename.length() == 0 ) {
			showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
					LocaleStrings.getString("6_9_IUSBREDIR")+
					LocaleStrings.getString("6_8_IUSBREDIR"));
			return false;
		}
		else if(!filename.toLowerCase().endsWith( ".iso" ) && !filename.toLowerCase().endsWith( ".nrg" )) {
			showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
					LocaleStrings.getString("6_9_IUSBREDIR")+LocaleStrings.getString("6_49_IUSBREDIR"));
			return false;
		}

		try
		{
			if( cdromSession[cddevice_no] != null ) {
				if( cdromSession[cddevice_no].isRedirActive() ) {
					showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
							LocaleStrings.getString("6_3_IUSBREDIR"));
					return false;
				}
				cdromSession[cddevice_no] = null;
				System.gc();
			}
			/* Create CDROM redirection object for ISO9660 image */
			cdromSession[cddevice_no] = new CDROMRedir( false );

			/* start redirection */
			if( !cdromSession[cddevice_no].startRedirection( host, filename ,cddevice_no,token,port,bVMUseSSL ) ) {
				/* Uh-oh, redirect failed */
				// check if there is an error message
				if(cdromSession[cddevice_no].getErrorMessage() == null && cdromSession[cddevice_no].getErrorMessage().length() == 0) {
					showErrorMessage(LocaleStrings.getString("6_8_IUSBREDIR"),
							LocaleStrings.getString("6_11_IUSBREDIR"));
				} else if( cdromSession[cddevice_no].getErrorMessage() != " " ){ // if so display the error message
					showErrorMessage(LocaleStrings.getString("6_8_IUSBREDIR"),
							cdromSession[cddevice_no].getErrorMessage());
				}

				// clear the error message
				cdromSession[cddevice_no].setErrorMessage(" ");

				StopISORedir(cddevice_no, STOP_NORMAL);
				return false;
			}
		}
		catch( UnsatisfiedLinkError e )
		{
			/* We couldn't load the native CD-ROM library */
			showErrorMessage(LocaleStrings.getString("6_8_IUBREDIR"),
					LocaleStrings.getString("6_12_IUSBREDIR"));
			StopISORedir(cddevice_no, STOP_NORMAL);
			return false;
		}
		catch( RedirectionException e )
		{
			showErrorMessage(LocaleStrings.getString("6_8_IUSBREDIR"), e.getMessage());
			StopISORedir(cddevice_no, STOP_NORMAL);
			return false;
		}
		catch(Exception e)
		{
			showErrorMessage(LocaleStrings.getString("6_8_IUSBREDIR"), e.getMessage());
			StopISORedir(cddevice_no, STOP_NORMAL);
			return false;
		}
		return true;
	}

	/**
	 * Stop ISO redirection
	 * returns true if successful or false on failure.
	 */
	public boolean StopISORedir(int cddevice_no, int stopMode)
	{
		return StopCDROMRedir(cddevice_no, stopMode);
	}

	/**
	 * Start Harddisk redirection
	 * returns true if successful or false on failure.
	 */
	public boolean StartHarddiskRedir(String token,int port,int device_no, boolean bVMUseSSL,String hardDrive,byte MediaType )
	{
		String host = JViewer.getIp();
		if( host == null ) {
			showErrorMessage(LocaleStrings.getString("6_13_IUSBREDIR"),
					LocaleStrings.getString("6_1_IUSBREDIR"));
			return false;
		}
		if( hardDrive.length() == 0 ) {
			showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
					LocaleStrings.getString("6_14_IUSBREDIR")+LocaleStrings.getString("6_13_IUSBREDIR"));
			return false;
		}
		try
		{
			if( hardDiskSession[device_no] != null ) {
				if( hardDiskSession[device_no].isRedirActive() ) {
					showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
							LocaleStrings.getString("6_15_IUSBREDIR"));
					return false;
				}
				hardDiskSession[device_no] = null;
				System.gc();
			}
			hardDiskSession[device_no] = new HarddiskRedir( true );
			hardDiskSession[device_no].setDrive_Type(MediaType);
			/* User selected a drive - let's redirect it */
			if( !hardDiskSession[device_no].startRedirection( host, hardDrive ,device_no,token,port,bVMUseSSL ) ) {
				// check if there is an error message
				if(hardDiskSession[device_no].getErrorMessage() == null && hardDiskSession[device_no].getErrorMessage().length() == 0) {
					showErrorMessage(LocaleStrings.getString("6_13_IUSBREDIR"),
							LocaleStrings.getString("6_16_IUSBREDIR"));
				} else if( hardDiskSession[device_no].getErrorMessage()!= " " ){ // if so display the error message
					showErrorMessage(LocaleStrings.getString("6_13_IUSBREDIR"),
							hardDiskSession[device_no].getErrorMessage());
				}

				// clear the error message
				hardDiskSession[device_no].setErrorMessage(" ");

				StopHarddiskRedir(device_no, STOP_NORMAL);
				return false;
			}
		}
		catch( UnsatisfiedLinkError e )
		{
			Debug.out.println(e);
			/* We couldn't load the native Floppy library */
			showErrorMessage(LocaleStrings.getString("6_13_IUSBREDIR"),
					LocaleStrings.getString("6_17_IUSBREDIR") );
			StopHarddiskRedir(device_no, STOP_NORMAL);
			return false;
		}
		catch( RedirectionException e )
		{
			/* Something funky happened... */
			showErrorMessage(LocaleStrings.getString("6_13_IUSBREDIR"), e.getMessage());
			StopHarddiskRedir(device_no, STOP_NORMAL);
			return false;
		}
		return true;
	}

	/**
	 * Stop Harddisk/USB redirection
	 * returns true if successful or false on failure.
	 */
	public boolean StopHarddiskRedir(int device_no, int stopMode)
	{
		int ret=0;
		if( hardDiskSession[device_no] != null ) {
			if ( hardDiskSession[device_no].stopRedirection() == false ) {
				showErrorMessage(LocaleStrings.getString("6_34_IUSBREDIR"),
						LocaleStrings.getString("6_36_IUSBREDIR") );
				return false;
			}

			if(hardDiskSession[device_no].getHdReconnect() == true)
			{
				return true;
			}
			ret = hardDiskSession[device_no].getHdStopMode();
			// Check whether port is in listen, if "port is not in listen" no need to update device status
			if(ret != IUSBRedirSession.STOP_PORT_NOT_IN_LISTEN){
				setHDStopMode(stopMode, device_no);
			}
			hardDiskSession[device_no] = null;

			/* Manually invoke garbage collector to reclaim memory from
			 * the cdromSession.  In particular, we want back the directly
			 * allocated memory, as there is an upper limit to how much
			 * of that we can have allocated at any given time, and directly
			 * allocated memory that hasn't yet been garbage collected counts
			 * against that limit. */
			System.gc();
		}
		return true;
	}

	/**
	 * Start Hardisk/Floppy image redirection
	 * returns true if successful or false on failure.
	 */
	public boolean StartharddiskImageRedir(String token,int port,int device_no, boolean bVMUseSSL,String filename,byte mediatype)
	{
		String host = JViewer.getIp();
		if( host == null ) {
			showErrorMessage(LocaleStrings.getString("6_27_IUSBREDIR"),
					LocaleStrings.getString("6_1_IUSBREDIR"));
			return false;
		}

		if( filename.length() == 0 ) {
			showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
					LocaleStrings.getString("6_9_IUSBREDIR")+LocaleStrings.getString("6_27_IUSBREDIR"));
			return false;
		}
		else if(filename.indexOf(SHAREFOLDERSPLITSTRING) != -1) {
			String[] tmpAry = filename.split(SHAREFOLDERSPLITSTRING);
			/* Need Size, folder path and image path. If any one empty can't start redirection. */
			if(tmpAry.length != HDFOLDERREDIRLENGTH) {
				showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
					LocaleStrings.getString("6_54_IUSBREDIR") + LocaleStrings.getString("6_51_IUSBREDIR"));
				return false;
			}
			else {
				if(tmpAry[0].length() == 0 || tmpAry[1].length() == 0 || tmpAry[2].length() == 0) {
					showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
						LocaleStrings.getString("6_54_IUSBREDIR") + LocaleStrings.getString("6_51_IUSBREDIR"));
					return false;
				}
				
				if( !imageSizeCheck(tmpAry[0]) ) {
					showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
						LocaleStrings.getString("6_55_IUSBREDIR") + LocaleStrings.getString("6_51_IUSBREDIR"));
					return false;
				}
			}
		}
		else if(!filename.toLowerCase().endsWith( ".img" ) && !filename.toLowerCase().endsWith( ".ima" )) {
			showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
					LocaleStrings.getString("6_9_IUSBREDIR")+LocaleStrings.getString("6_51_IUSBREDIR"));
			return false;
		}
		try
		{
			if( hardDiskSession[device_no] != null ) {
				if( hardDiskSession[device_no].isRedirActive() ) {
					showErrorMessage(LocaleStrings.getString("6_32_IUSBREDIR"),
							LocaleStrings.getString("6_28_IUSBREDIR"));
					return false;
				}
				hardDiskSession[device_no] = null;
				System.gc();
			}
			/* Create Floppy redirection object for Floppy image */
			hardDiskSession[device_no] = new HarddiskRedir( false );
			hardDiskSession[device_no].setDrive_Type(mediatype);
			/* start redirection */
			if( !hardDiskSession[device_no].startRedirection( host, filename,device_no,token,port,bVMUseSSL  ) ) {
				//because of add new feature - mount folder, it will make image harddisk hangs and waiting for image create complete
				//if the share privilege coming and got partially, the harddisk session will be clean first, so need to add one more check
				if(hardDiskSession[device_no] != null) {
					/* Uh-oh, redirect failed */
					// check if there is an error message
					if(hardDiskSession[device_no].getErrorMessage() == null && hardDiskSession[device_no].getErrorMessage().length() == 0) {
						showErrorMessage(LocaleStrings.getString("6_27_IUSBREDIR"),
								LocaleStrings.getString("6_29_IUSBREDIR") );
					} else if( hardDiskSession[device_no].getErrorMessage() != " " ){ // if so display the error message
						showErrorMessage(LocaleStrings.getString("6_27_IUSBREDIR"),
								hardDiskSession[device_no].getErrorMessage());
					}

					// clear the error message
					hardDiskSession[device_no].setErrorMessage(" ");

					StopHarddiskImageRedir(device_no, STOP_NORMAL);
					return false;
				}
				else
					return false;
			}
		}
		catch( UnsatisfiedLinkError e )
		{
			/* We couldn't load the native Floppy library */
			showErrorMessage(LocaleStrings.getString("6_27_IUSBREDIR"),
					LocaleStrings.getString("6_30_IUSBREDIR"));
			StopHarddiskImageRedir(device_no, STOP_NORMAL);
			return false;
		}
		catch( RedirectionException e )
		{
			showErrorMessage(LocaleStrings.getString("6_27_IUSBREDIR"), 
					e.getMessage());
			StopHarddiskImageRedir(device_no, STOP_NORMAL);
			return false;
		}
		return true;
	}
	  	  
	private static boolean imageSizeCheck(String number){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher matcher = pattern.matcher(number);
		
		if(!matcher.matches()) {
			Debug.out.println("imageSizeCheck(), match failed");
			return false;
		}
		
		int tmp =  Integer.valueOf(number);
		
		if(tmp <= 0){
			Debug.out.println("imageSizeCheck(), less then 0");
			return false;
		}
		
		return (tmp >= 4 && tmp <= 512);
	}
	


	/**
	 * Starts the redirection for a particular device instance.
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_FLOPPY - for Floppy devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param token - the kvm session token
	 * @param port - server port number for the particular device 
	 * @param deviceNo - the device instance number.
	 * @param bVMUseSSL - true if secure connection is enabled, false otherwise
	 * @param source - the redirection source device or image file name.
	 * @param isPhysical - true if physical device is redirected, false if image is redirected.
	 * @return true f redirection success and false otherwise.
	 */
	public boolean startDeviceRedirection(int deviceType, String token, int port,int deviceNo, boolean bVMUseSSL,String source, boolean isPhysical){
		boolean status = false;
		switch(deviceType){
		case VMApp.DEVICE_TYPE_CDROM:
			if(isPhysical)
				status = StartCDROMRedir(token, port, deviceNo, bVMUseSSL, source);
			else
				status = StartISORedir(token, port, deviceNo, bVMUseSSL, source);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			byte mediaType;
			if(isPhysical){
				mediaType = getMediaType(source);
				status = StartHarddiskRedir(token, port, deviceNo, bVMUseSSL, source, mediaType);
			}
			else{
				mediaType = MEDIA_TYPE_USB;//USB key emulation
				status = StartharddiskImageRedir(token, port, deviceNo, bVMUseSSL, source, mediaType);
			}
			break;
		}
		return status;
	}

	/**
	 * Stops the device redirection for a particular device instance.
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_FLOPPY - for Floppy devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param deviceIndex - the device instance number.
	 * @return
	 */
	public boolean stopDeviceRedirection(int deviceType, int deviceIndex){

		boolean status = false;
		int stopMode = STOP_NORMAL;
		switch(deviceType){
		case VMApp.DEVICE_TYPE_CDROM:
			synchronized (CDROMRedir.getSyncObj()) {
				CDROMRedir.getSyncObj().notify();
			}
			StopCDROMRedir(deviceIndex, stopMode);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			synchronized (HarddiskRedir.getSyncObj()) {
				HarddiskRedir.getSyncObj().notify();
			}
			StopHarddiskRedir(deviceIndex, stopMode);
			break;
		}
		return status;

	}

	/**
	 * Stop Floppy image redirection
	 * returns true if successful or false on failure.
	 */
	public boolean StopHarddiskImageRedir(int device_no, int stopMode)
	{
		return StopHarddiskRedir(device_no, stopMode);
	}

	/** Query the native cdrom library and get its version
	 *  @return The version number if the native library is present
	 *  @return "Not present" if the native library is not present */
	public String getLIBCDROMVersion(int cddevice_no)
	{
		String version;

		try
		{
			if( cdromSession[cddevice_no] == null )
			{
				cdromSession[cddevice_no] = new CDROMRedir( false );
				version = cdromSession[cddevice_no].getLIBCDROMVersion();
				cdromSession[cddevice_no] = null;

				/* Manually invoke garbage collector to reclaim memory from
				 * the cdromSession.  In particular, we want back the directly
				 * allocated memory, as there is an upper limit to how much
				 * of that we can have allocated at any given time, and directly
				 * allocated memory that hasn't yet been garbage collected counts
				 * against that limit. */
				System.gc();
			}
			else
				version = cdromSession[cddevice_no].getLIBCDROMVersion();
		}
		catch( UnsatisfiedLinkError e )
		{
			version = LocaleStrings.getString("6_31_IUSBREDIR");
		}

		return( version );
	}

	/**
	 * File filter for ISO images and directories
	 */
	class ISOImageFilter extends javax.swing.filechooser.FileFilter
	{
		public boolean accept(File file )
		{
			String filename = file.getName();
			return filename.toLowerCase().endsWith( ".iso" ) || file.isDirectory();
		}

		public String getDescription()
		{
			return "*.iso; *.ISO";
		}
	}

	/**
	 * File filter for NRG images and directories
	 */
	class NRGImageFilter extends javax.swing.filechooser.FileFilter
	{
		public boolean accept(File file )
		{
			String filename = file.getName();
			return filename.toLowerCase().endsWith( ".nrg" ) || file.isDirectory();
		}

		public String getDescription()
		{
			return "*.nrg; *.NRG";
		}
	}

	/**
	 * File filter for ISO & NRG images and directories
	 */
	class CDImageFilter extends FileFilter{

		public boolean accept(File file) {
			boolean accept = false;
			if(new ISOImageFilter().accept(file) || new NRGImageFilter().accept(file))
				accept = true;
			return accept;
		}

		public String getDescription() {
			return "ISO(*.iso, *.ISO), NRG(*.nrg, *.NRG)";
		}

	}

	/** Prompt the user to select a CD image from their file system
	 * @param string
	 *  @return A <code>String</code> containing the complete file path
	 *  of the selected CD image
	 *  @return <code>null</code> if the user hit the cancel button */
	public String cdImageSelector(String dirpath)
	{
		JVFrame frame = JViewerApp.getInstance().getMainWindow();
		JFileChooser chooser = new JFileChooser(dirpath);
		chooser.setFileFilter( new CDImageFilter() );

		if( chooser.showOpenDialog( frame ) == JFileChooser.APPROVE_OPTION )
			return( chooser.getSelectedFile().getAbsolutePath() );
		else
			return( null );
	}

	public boolean isCDROMPhysicalDrive(int cddevice_no)
	{
		if(cdromSession[cddevice_no] != null)
		{
			if(cdromSession[cddevice_no].isPhysicalDevice() == true)
				return true;
		}
		return false;
	}

	public boolean isHarddiskPhysicalDrive(int device_no)
	{
		if(hardDiskSession[device_no] != null)
		{
			if(hardDiskSession[device_no].isPhysicalDevice() == true)
				return true;
		}
		return false;
	}

	/***
	 *  File filter for file images (stuff ending with .img) and directories
	 *
	 */
	class FloppyImageFilter extends javax.swing.filechooser.FileFilter
	{
		public boolean accept( java.io.File f )
		{
			String filename = f.getName();
			return (filename.toLowerCase().endsWith( ".img" ) ||
					filename.toLowerCase().endsWith( ".ima" ) ||
					f.isDirectory());
		}

		public String getDescription()
		{
			return "IMG(*.img, *.IMG), IMA(*.ima, *.IMA)";
		}
	}

	/** Prompt the user to select a floppy image from their filesystem
	 * @param string
	 *  @return A <code>String</code> containing the completely file path
	 *  of the selected image
	 *  @return <code>null</code> if the user hit the cancel button */
	public String floppyImageSelector(String dirpath)
	{
		JVFrame mainFrame = JViewerApp.getInstance().getMainWindow();
		JFileChooser chooser = new JFileChooser(dirpath);
		chooser.setFileFilter( new FloppyImageFilter() );
		if( chooser.showOpenDialog( mainFrame ) == JFileChooser.APPROVE_OPTION )
			return( chooser.getSelectedFile().getAbsolutePath() );
		else
			return( null );
	}

	/**
	 * Stop the CDROMRedirection abnormally
	 *
	 */
	public void stopCDROMAbnormal(int device_no)
	{
		try{

			/* Manually invoke garbage collector to reclaim memory from
			 * the cdromSession.  In particular, we want back the directly
			 * allocated memory, as there is an upper limit to how much
			 * of that we can have allocated at any given time, and directly
			 * allocated memory that hasn't yet been garbage collected counts
			 * against that limit. */
			System.gc();
			cdromSession[device_no].setCdromRedirStatus(DEVICE_REDIR_STATUS_IDLE);
			cdromSession[device_no] = null;
		}catch(ArrayIndexOutOfBoundsException aie){
			System.gc();
			Debug.out.println(aie);
		}
	}

	/**
	 * Stop the floppy Redirection abnormally
	 *
	 */
	public void stopHarddiskAbnormal(int device_no)
	{
		try {

			/* Manually invoke garbage collector to reclaim memory from
			 * the cdromSession.  In particular, we want back the directly
			 * allocated memory, as there is an upper limit to how much
			 * of that we can have allocated at any given time, and directly
			 * allocated memory that hasn't yet been garbage collected counts
			 * against that limit. */
			System.gc();
			hardDiskSession[device_no].setHarddiskRedirStatus(DEVICE_REDIR_STATUS_IDLE);
			hardDiskSession[device_no] = null;
		} catch (ArrayIndexOutOfBoundsException aie) {
			System.gc();
			Debug.out.println(aie);
		}
	}

	/**
	 * Return the CDROM Redirection status
	 * @return
	 */
	public int getCDROMRedirStatus(int device_no)
	{
		int ret = DEVICE_REDIR_STATUS_IDLE;
		try{
			if(cdromSession[device_no] != null)
				ret =  cdromSession[device_no].getCdromRedirStatus();
		}catch (ArrayIndexOutOfBoundsException aie) {
			Debug.out.println(aie);
			ret = DEVICE_REDIR_STATUS_IDLE;
		}catch (Exception e) {
			Debug.out.println(e);
			ret = DEVICE_REDIR_STATUS_IDLE;
		}
		return ret;
	}

	/**
	 * Return the Harddisk Redirection status
	 * @return
	 */
	public int getHarddiskRedirStatus(int device_no)
	{
		int ret = DEVICE_REDIR_STATUS_IDLE;
		try{
			if(hardDiskSession[device_no] != null)
				ret =  hardDiskSession[device_no].getHarddiskRedirStatus();
		}catch (ArrayIndexOutOfBoundsException aie) {
			Debug.out.println(aie);
			ret = DEVICE_REDIR_STATUS_IDLE;
		}catch (Exception e) {
			Debug.out.println(e);
			ret = DEVICE_REDIR_STATUS_IDLE;
		}
		return ret;
	}

	/**
	 * * Return the Device Redirection status.
	 *  - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param deviceNo - the device instance number.
	 * @return the device redirection status
	 */
	public int getDeviceRedirStatus(int deviceType, int deviceNo)
	{
		int ret = DEVICE_REDIR_STATUS_IDLE;
		try{
			switch(deviceType){
			case VMApp.DEVICE_TYPE_CDROM:
				ret = getCDROMRedirStatus(deviceNo);
				break;
			case VMApp.DEVICE_TYPE_HD_USB:
				ret = getHarddiskRedirStatus(deviceNo); 
				break;
			}
		}catch (ArrayIndexOutOfBoundsException aie) {
			Debug.out.println(aie);
			ret = DEVICE_REDIR_STATUS_IDLE;
		}catch(Exception e){
			Debug.out.println(e);
			ret = DEVICE_REDIR_STATUS_IDLE;
		}
		return ret;
	}
	/**
	 * Return the CDROM source drive name
	 * @return
	 */
	public String getCDROMSource(int cddevice_no)
	{
		if ( cdromSession[cddevice_no] != null )
			return cdromSession[cddevice_no].getSourceDrive();

		return new String("");
	}

	/**
	 * Return the Floppy source drive name
	 * @return
	 */
	public String getHarddiskSource(int device_no)
	{
		if ( hardDiskSession[device_no] != null )
			return hardDiskSession[device_no].getSourceDrive();

		return new String("");
	}

	/**
	 * Returns the redirect source device or file name
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_FLOPPY - for Floppy devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param deviceIndex - the device instance number.
	 * @return the redirected device source. 
	 */
	public String getDeviceRedirSource(int deviceType, int deviceIndex){
		String source = null;
		switch(deviceType){
		case VMApp.DEVICE_TYPE_CDROM:
			source = getCDROMSource(deviceIndex);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			source = getHarddiskSource(deviceIndex);
			break;
		}
		return source;
	}
	/**
	 * Return the data transfer rate of the CDROM Redirection
	 * @return
	 */
	public int getCDROMReadBytes(int cddevice_no)
	{
		if( cdromSession[cddevice_no] != null )
			return cdromSession[cddevice_no].getBytesRedirected();

		return 0;
	}

	/**
	 * Return the data transfer rate of the Floppy Redirection
	 * @return
	 */
	public int getHarddiskReadBytes(int device_no)
	{
		if( hardDiskSession[device_no] != null )
			return hardDiskSession[device_no].getBytesRedirected();

		return 0;
	}
	/**
	 * Returns the Image redirection status for a particular device
	 * @param deviceType - type of device for which image redirection status is required.
	 * @param deviceIndex - instance number of the device.
	 * @return the image redirection status.
	 */
	public boolean isImageRedirected(int deviceType, int deviceIndex){
		boolean isImgRedir = false;
		switch(deviceType){
		case VMApp.DEVICE_TYPE_CDROM:
			isImgRedir = getCdromSession(deviceIndex).isCdImageRedirected();
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			isImgRedir = getHarddiskSession(deviceIndex).isHdImageRedirected();
			break;
		}
		return isImgRedir;
	}
	/**
	 * Sets the image redirection status for a particular device.
	 * @param deviceType - type of device for which image redirection status is to be set.
	 * @param deviceIndex - instance number of the device.
	 * @param isImgRedir -the image redirection status.
	 */
	public void setImageRedirected(int deviceType, int deviceIndex, boolean isImgRedir){
		switch(deviceType){
		case VMApp.DEVICE_TYPE_CDROM:
			getCdromSession(deviceIndex).setCdImageRedirected(isImgRedir);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			getHarddiskSession(deviceIndex).setHdImageRedirected(isImgRedir);
			break;
		}
	}

	/**
	 * Gets the CDROMRedir object
	 * @param cddevice_no - the CD device instance number 
	 * @return the CDROMRedir object
	 */
	public CDROMRedir getCdromSession(int cddevice_no) {
		CDROMRedir cdROMSession = null;
		if(cddevice_no < cdromSession.length)
			cdROMSession = cdromSession[cddevice_no];
		return cdROMSession;
	}

	/**
	 * Sets the CDROMRedir object
	 * @param cdromSession - the CDROMRedir object to set.
	 * @param cddevice_no - the CD device instance number.
	 */
	public void setCdromSession(CDROMRedir cdromSession,int cddevice_no) {
		this.cdromSession[cddevice_no] = cdromSession;
	}

	/**
	 * Gets the HarddiskRedir object
	 * @param device_no - the hard disk device instance number
	 * @return the HarddiskRedir object.
	 */
	public HarddiskRedir getHarddiskSession(int device_no) {
		HarddiskRedir hardDiskRedir = null;
		if(device_no < hardDiskSession.length)
			hardDiskRedir = hardDiskSession[device_no];
		return hardDiskRedir;
	}

	/**
	 * Sets the HarddiskRedir object
	 * @param harddiskSession - the HarddiskRedir object to set.
	 * @param device_no - the floppy device instance number.
	 */
	public void setHarddiskSession(HarddiskRedir harddiskSession,int device_no) {
		this.hardDiskSession[device_no] = harddiskSession;
	}
	
	public int getCDInstanceNumber(int devNum){
		return cdromSession[devNum].getCdInstanceNum();
	}
	public int getHDInstanceNumber(int devNum){
		return hardDiskSession[devNum].getHdInstanceNum();
	}
	public int getDeviceInstanceNumber(int deviceType, int deviceIndex){
		int ret = 0;
		switch(deviceType){
		case VMApp.DEVICE_TYPE_CDROM:
			ret = getCDInstanceNumber(deviceIndex);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			ret = getHDInstanceNumber(deviceIndex);
			break;
		}
		return ret;
	}
	public void updateCDToolbarButtonStatus(boolean status){
		if(status != cdButtonRedirState){
			if(status){
				URL imageCDR = com.ami.kvm.jviewer.JViewer.class.getResource("res/CDR.png");
				JViewerApp.getInstance().getM_wndFrame().getToolbar().getCDBtn().setIcon(new ImageIcon(imageCDR));
				JViewerApp.getInstance().getM_wndFrame().getToolbar().getCDBtn().setToolTipText(LocaleStrings.getString("G_21_VMD"));
				cdButtonRedirState = true;
			}
			else{
				URL imageCD = com.ami.kvm.jviewer.JViewer.class.getResource("res/CD.png");
				JViewerApp.getInstance().getM_wndFrame().getToolbar().getCDBtn().setIcon(new ImageIcon(imageCD));
				JViewerApp.getInstance().getM_wndFrame().getToolbar().getCDBtn().setToolTipText(LocaleStrings.getString("G_20_VMD"));
				cdButtonRedirState = false;
			}
		}
	}

	public void updateHDToolbarButtonStatus(boolean status){
		if(status != hdButtonRedirState){
			if(status){
				URL imageHDR = com.ami.kvm.jviewer.JViewer.class.getResource("res/HDR.png");
				JViewerApp.getInstance().getM_wndFrame().getToolbar().getHardddiskBtn().setIcon(new ImageIcon(imageHDR));
				JViewerApp.getInstance().getM_wndFrame().getToolbar().getHardddiskBtn().setToolTipText(LocaleStrings.getString("G_25_VMD"));
				hdButtonRedirState = true;
			}
			else{
				URL imageHD = com.ami.kvm.jviewer.JViewer.class.getResource("res/HD.png");
				JViewerApp.getInstance().getM_wndFrame().getToolbar().getHardddiskBtn().setIcon(new ImageIcon(imageHD));
				JViewerApp.getInstance().getM_wndFrame().getToolbar().getHardddiskBtn().setToolTipText(LocaleStrings.getString("G_24_VMD"));
				hdButtonRedirState = false;
			}
		}
	}
	/**
	 * Returns the status whether the particular CD device is used or free.
	 * @param instanceNum - The device instance for which the status is to be checked. 
	 * @return IUSBRedirSession.DEVICE_USED - if the device is being used.
	 * 			IUSBRedirSession.DEVICE_FREE - if the device is free.
	 */
	public int getCDROMDeviceStatus(int instanceNum) {
		int ret = DEVICE_FREE;
		try{
			if(cdromSession[instanceNum] != null)
				ret = cdromSession[instanceNum].getCdROMDeviceStatus();
		}catch(Exception e){
			Debug.out.println(e);
			ret = DEVICE_FREE;
		}
		return ret;
	}
	/**
	 * Sets the status whether the particular CD device is used or free.
	 * @param instanceNum - The device instance for which the status is to be set.
	 * @param deviceStatus - IUSBRedirSession.DEVICE_USED - if the device is being used.
	 * 						 IUSBRedirSession.DEVICE_FREE - if the device is free.
	 */
	public void setCDROMDeviceStatus(int instanceNum, int deviceStatus) {
		try{
			cdromSession[instanceNum].setCdROMDeviceStatus(deviceStatus);
		}catch(Exception e){
			Debug.out.println(e);
		}
	}

	/**
	 * Returns the status whether the particular hard disk device is used or free.
	 * @param instanceNum - The device instance for which the status is to be checked. 
	 * @return IUSBRedirSession.DEVICE_USED - if the device is being used.
	 * 			IUSBRedirSession.DEVICE_FREE - if the device is free.
	 */
	public int getHardDiskDeviceStatus(int instanceNum) {
		int ret = DEVICE_FREE;
		try{
			if(hardDiskSession[instanceNum] != null)
				ret = hardDiskSession[instanceNum].getHardDiskDeviceStatus();
		}catch(Exception e){
			Debug.out.println(e);
			ret = DEVICE_FREE;
		}
		return ret;
	}
	/**
	 * Sets the status whether the particular hard disk device is used or free.
	 * @param instanceNum - The device instance for which the status is to be set.
	 * @param deviceStatus - IUSBRedirSession.DEVICE_USED - if the device is being used.
	 * 						 IUSBRedirSession.DEVICE_FREE - if the device is free.
	 */
	public void setHardDiskDeviceStatus(int instanceNum, int deviceStatus) {
		try{
			hardDiskSession[instanceNum].setHardDiskDeviceStatus(deviceStatus);
		}catch(Exception e){
		}
	}
	public int getDeviceStatus(int deviceType, int deviceIndex){
		int devStatus = DEVICE_FREE;
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			devStatus = getCDROMDeviceStatus(deviceIndex);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			devStatus = getHardDiskDeviceStatus(deviceIndex);
			break;
		}
		return devStatus;
	}
	public void setDeviceStatus(int deviceType, int deviceIndex, int deviceStatus) {
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			setCDROMDeviceStatus(deviceIndex, deviceStatus);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			setHardDiskDeviceStatus(deviceIndex, deviceStatus);
			break;
		}
	}
	/**
	 * Specifies whether the redirection stop was triggered by the user or not.
	 * @param deviceType - type of the device for which redirection stop is invoked.
	 * @param deviceIndex - device instance number
	 * @param isStoppedByUser - TRUE if the redirection stop is induced by the user. FALSE otherwise.
	 */
	public void setDeviceStopMode(int deviceType, int deviceIndex, boolean isStoppedByUser){
		switch(deviceType){
		case VMApp.DEVICE_TYPE_CDROM:
			getCdromSession(deviceIndex).setCdStoppedByUser(isStoppedByUser);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			getHarddiskSession(deviceIndex).setHdStoppedByUser(isStoppedByUser);
			break;
		}
	}
	public byte getMediaType(String driveString) {
		byte mediaType = 0;
		if(driveString != null) {
			String[] driveList = getEntireHarddiskList();
			if(System.getProperty("os.name").startsWith("Win"))
			{
				//For example, if the driveString is like "PhysicalDrive-1-[G-H-I-J-K] - USB"
				//drive letter exists between (indexOf('-')+1) and (indexOf('[')-1)
				//So we get the drive letter value as 1 here.
				String physicalDriveNo = driveString.substring(driveString.indexOf('-')+1, driveString.indexOf('[')-1);
				int redirectedDrive = Integer.parseInt(physicalDriveNo);		
				for (String drive : driveList) {
					//String drive = driveList[i];
					Debug.out.println("DRIVE : "+drive);
					//In the drive name first character denotes the drive number
					String driveNo = drive.substring(0,1);
					int driveno = Integer.parseInt(driveNo);
					if(driveno == redirectedDrive){
						//In the drive name second character denotes the drive type
						String driveTypeStr =  drive.substring(1,2);
						int driveType= Integer.parseInt(driveTypeStr);
						if(driveType == WINDOWS_DRIVE_TYPE_HDD) {
							mediaType = MEDIA_TYPE_HDD;//FIXED HARDDISK
						} else if(driveType == WINDOWS_DRIVE_TYPE_USB) {
							mediaType = MEDIA_TYPE_USB;//USB KEY
						}
					}
				}
			} else if(System.getProperty("os.name").equals("Linux")) {
				for (String drive : driveList) {
					String driveName = drive.substring(0,drive.length()-1);
					String[] redirectedDrive = driveString.split("-");
					String redirDriveName = redirectedDrive[0].trim();
					//int driveno = Integer.parseInt(Drive_no);
					Debug.out.println("string::"+driveString+"Drive_name::"+driveName);
					Debug.out.println("COMPARED::"+driveName.compareTo(redirDriveName));
					if(driveName.compareTo(redirDriveName) == 0) {
						String driveTypeStr =  drive.substring(drive.length()-1,drive.length());
						int driveType= Integer.parseInt(driveTypeStr);
						if(driveType == LINUX_DRIVE_TYPE_HDD) {
							mediaType = (byte)MEDIA_TYPE_HDD;//FIXED HARDDISK
						} else if(driveType == LINUX_DRIVE_TYPE_USB) {
							mediaType = MEDIA_TYPE_USB;//USB KEY
						}
					}
				}
			}
		}
		return mediaType;
	}

	/**
	 * Get the Fixed HDD from the native library
	 * @return
	 */
	public String[] getEntireHarddiskList()
	{
		String hardDiskList[] = null;
		HarddiskRedir harddiskObj = new HarddiskRedir(true);
		hardDiskList = harddiskObj.getEntireHarddiskList();
		System.gc();
		return hardDiskList;
	}

	private void showErrorMessage(String title, String message){

		if(!JViewer.isVMApp()){
			JViewerApp.getInstance().getMainWindow().generalErrorMessage(title, message);
		}
		else{
			VMApp.getInstance().generalErrorMessage(title, message);
		}
	}
	/**
	 * @return the stopMessage
	 */
	public int getCDStopMode(int index) {
		int ret;
		try{
			if(cdromSession[index] == null)
				return STOP_NORMAL;
			ret =  cdromSession[index].getCdStopMode();
		}catch (ArrayIndexOutOfBoundsException aie) {
			Debug.out.println(aie);
			aie.printStackTrace();
			ret = STOP_NORMAL;
		}catch (Exception e) {
			e.printStackTrace();
			Debug.out.println(e);
			ret = STOP_NORMAL;
		}
		return ret;
	}
	/**
	 * @param stopMessage the stopMessage to set
	 */
	public void setCDStopMode(int mode, int index) {
		try {
			if(null != cdromSession[index]){
				cdromSession[index].setCdStopMode(mode);
			}
			showStopStatus(VMApp.DEVICE_TYPE_CDROM,index, mode);
		} catch (Exception e) {
			Debug.out.println(e);
			e.printStackTrace();
			if(null != cdromSession[index]){
				cdromSession[index].setCdStopMode(STOP_NORMAL);
			}
		}
	}

	/**
	 * @return the hdStopMode
	 */
	public int getHDStopMode(int index) {
		int ret;
		try{
			if(hardDiskSession[index] == null)
				return STOP_NORMAL;
			ret =  hardDiskSession[index].getHdStopMode();
		}catch (ArrayIndexOutOfBoundsException aie) {
			Debug.out.println(aie);
			ret = STOP_NORMAL;
		}catch (Exception e) {
			Debug.out.println(e);
			ret = STOP_NORMAL;
		}
		return ret;
	}
	/**
	 * @param hdStopMode the hdStopMode to set
	 */
	public void setHDStopMode(int mode, int index) {
		try {
			if(null != hardDiskSession[index]){
				hardDiskSession[index].setHdStopMode(mode);
			}
			showStopStatus(VMApp.DEVICE_TYPE_HD_USB,index, mode);
		} catch (Exception e) {
			Debug.out.println(e);
			if(null != hardDiskSession[index]){
				hardDiskSession[index].setHdStopMode(STOP_NORMAL);
			}
		}
	}
	
	/**
	 * Updates the status in the status table if a redirection is stopped.
	 * @param index - device instance number
	 * @param stopMode - the reason of stopping the redirection.<br>
	 * 					IUSBRedirSession.STOP_NORMAL - for normal stop operation.<br>
						IUSBRedirSession.STOP_ON_EJECT - for stop due to device eject in the host.<br>
						IUSBRedirSession.STOP_ON_TERMINATE - for stop due to redirection termination from the server.<br>
						IUSBRedirSession.STOP_ON_DEVICE_REMOVAL - for stop due to device removal in the client.<br>;
	 */
	private void showStopStatus(int mediatype,int index, int stopMode){
		if(mediatype == VMApp.DEVICE_TYPE_CDROM){
			statusTable = VMApp.getVMPane().getStatusTabPanel().getCDStatusTable().getStatusTable();
			devStatusTable = VMApp.getVMPane().getCDStatusTable();
		}
		else if(mediatype == VMApp.DEVICE_TYPE_HD_USB){
			statusTable = VMApp.getVMPane().getStatusTabPanel().getHDStatusTable().getStatusTable();
			devStatusTable = VMApp.getVMPane().getHDStatusTable();
		}
		else
			return;

		if(stopMode == IUSBRedirSession.STOP_NORMAL){
			statusTable.setValueAt(LocaleStrings.getString("AL_8_ST"), index, BYTES_READ_COL);
			devStatusTable.setValueAt(LocaleStrings.getString("AL_8_ST"), index, BYTES_READ_COL);
		}
		else if(stopMode == IUSBRedirSession.STOP_ON_EJECT){
			statusTable.setValueAt("<html><font color=\"Red\" font size =\"3\"><b>"+
					LocaleStrings.getString("AL_10_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
			devStatusTable.setValueAt("<html><font color=\"Red\" font size =\"3\"><b>"+
					LocaleStrings.getString("AL_10_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
		}
		else if(stopMode == IUSBRedirSession.STOP_ON_TERMINATE){
			statusTable.setValueAt("<html><font color=\"Red\"><font size =\"3\"><b>"+
					LocaleStrings.getString("AL_11_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
			devStatusTable.setValueAt("<html><font color=\"Red\"><font size =\"3\"><b>"+
					LocaleStrings.getString("AL_11_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
		}
		else if(stopMode == IUSBRedirSession.STOP_ON_DEVICE_REMOVAL){
			statusTable.setValueAt("<html><font color=\"Red\" font size =\"3\"><b>"+
					LocaleStrings.getString("AL_12_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
			devStatusTable.setValueAt("<html><font color=\"Red\" font size =\"3\"><b>"+
					LocaleStrings.getString("AL_12_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
		}
		else if(stopMode == IUSBRedirSession.STOP_ON_SERVER_RESTART){
			statusTable.setValueAt("<html><font color=\"Red\" font size =\"3\"><b>"+
					LocaleStrings.getString("AL_13_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
			devStatusTable.setValueAt("<html><font color=\"Red\" font size =\"3\"><b>"+
					LocaleStrings.getString("AL_13_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
		}
		else if(stopMode ==  IUSBRedirSession.STOP_ON_CONNECTION_LOSS ){
			statusTable.setValueAt("<html><font color=\"Red\" font size =\"3\"><b>"+
					LocaleStrings.getString("AL_14_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
			devStatusTable.setValueAt("<html><font color=\"Red\" font size =\"3\"><b>"+
					LocaleStrings.getString("AL_14_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
		}
		else if(stopMode ==  IUSBRedirSession.STOP_PORT_NOT_IN_LISTEN ){
			statusTable.setValueAt("<html><font color=\"Red\" font size =\"3\"><b>"+
					LocaleStrings.getString("AL_15_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
			devStatusTable.setValueAt("<html><font color=\"Red\" font size =\"3\"><b>"+
					LocaleStrings.getString("AL_15_ST")+
					"</b></font></html>", index, BYTES_READ_COL);
		}
		// Reset the redirection mode status as not connected
		statusTable.setValueAt(LocaleStrings.getString("AL_8_ST"), index, REDIR_MODE_COL);
		devStatusTable.setValueAt(LocaleStrings.getString("AL_8_ST"), index, REDIR_MODE_COL);
	}

	/**
	 * Updates current mode of redirection in VMedia status table.
	 * 
	 * @param mediatype
	 *            VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 *            VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param index
	 *            the device instance number.
	 * @param mode
	 *            READ_ONLY - if the device is redirected in Read Only mode.<br>
	 *            READ_WRITE - if the device is redirected in Read/Write mode.
	 */
	public void updateRedirectionStatus(int mediatype, int index, int mode) {
		String redirMode = null;
		if (mediatype == VMApp.DEVICE_TYPE_CDROM) {
			statusTable = VMApp.getVMPane().getStatusTabPanel().getCDStatusTable().getStatusTable();
			devStatusTable = VMApp.getVMPane().getCDStatusTable();
			if ((index < 0) || (index > cdromSession.length)) {
				Debug.out.println("Invalid index value..!!");
				return;
			}
		} else if (mediatype == VMApp.DEVICE_TYPE_HD_USB) {
			statusTable = VMApp.getVMPane().getStatusTabPanel().getHDStatusTable().getStatusTable();
			devStatusTable = VMApp.getVMPane().getHDStatusTable();
			if ((index < 0) || (index > hardDiskSession.length)) {
				Debug.out.println("Invalid index value..!!");
				return;
			}
		} else {
			Debug.out.println("Invalid media type..!!");
			return;
		}
		if (mode == READ_ONLY) {
			redirMode = LocaleStrings.getString("AL_17_ST");
		} else if (mode == READ_WRITE) {
			redirMode = LocaleStrings.getString("AL_18_ST");
		} else {
			Debug.out.println("Invalid redirection mode..!!");
			return;
		}
		statusTable.setValueAt(redirMode, index, REDIR_MODE_COL);
		devStatusTable.setValueAt(redirMode, index, REDIR_MODE_COL);
	}
}
