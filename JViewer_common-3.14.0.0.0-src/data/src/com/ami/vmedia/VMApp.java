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

import java.awt.Point;
import java.util.Timer;

import javax.swing.JOptionPane;

import com.ami.iusb.CDROMRedir;
import com.ami.iusb.HarddiskRedir;
import com.ami.iusb.IUSBRedirSession;
import com.ami.iusb.RedirectionException;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.InfoDialog;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.kvm.jviewer.gui.SinglePortKVM;
import com.ami.vmedia.gui.UpdateBytesRead;
import com.ami.vmedia.gui.VMDialog;
import com.ami.vmedia.gui.VMFrame;
import com.ami.vmedia.gui.VMPane;
import com.ami.vmedia.gui.VMStatusBar;

/**
 * This is the main class from which the control flow initiates for the
 * VMedia application. This is a singleton class, which provides access to
 * its members using a getInstance() method in the class.
 * @author deepakmn
 *
 */
public class VMApp {

	public static final int	DEVICE_TYPE_CDROM=0;
	public static final int	DEVICE_TYPE_HD_USB=2;
	public static final int DEFAULT_NUM_DRIVES=1;
	public static final int IMAGE_TYPE_CDROM = 1;
	public static final int IMAGE_TYPE_HARDDISK = 3;
	public static final int MAX_IMAGE_PATH_COUNT = 5;

	private static int height = 0;
	private static int width = 0;
	private static Point windowPos = new Point(0,0);

	public static final int CD_MEDIA = 0;
	public static final int HD_MEDIA = 1;
	public static final int STATUS_TAB = 2;

	public static String cdImagePath[][];
	public static String hdImagePath[][];

	private static VMApp vmApp = new VMApp();
	private static VMPane vmPane = null;
	private static VMStatusBar statusPanel = null;
	private static VMFrame vmFrame = null;
	private static VMDialog vmDialog = null;
	private int numCD;
	private int numHD;
	private int freeCDNum = 0;
	private int freeHDNum = 0;
	private String[] cdDriveList;
	private String[] hdDriveList;
	private static String lang= null;
	private static String ip;
	private IUSBRedirSession iusbRedirSession = null;
	private static DeviceDetector devDetector = null;
	private static RedirectionStatusMonitor redirectionStatusMonitor = null;
	private static String webSessionToken;
	private static String kvmToken;

	private RedirectionController redirectionController = null;
	private static Timer updateBytesTimer = null;

	/**
	 * Launches the VMedia application.
	 * If the main application type is VMApp, the it launches as a 
	 * stand alone application. If it is launched from the JViewer, 
	 * it launches as a child dialog.
	 */
	public static void launchApp(){

		statusPanel = new VMStatusBar();
		//Launches as a separate window application.
		if(JViewer.isVMApp()) {
			vmFrame = new VMFrame();
		}
		else 
		{ //Launches a child dialog.
			vmDialog = new VMDialog();
			VMApp.setVMDialog(vmDialog);
			JViewerApp.getInstance().setVMDialog(vmDialog);
		}
		updateBytesTimer = new Timer();
		updateBytesTimer.schedule(new UpdateBytesRead(), 0, 1000);
		// incase of singleport enabled, we need singleportkvm object for media redirection.
		if(JViewer.isVMApp() && JViewer.isSinglePortEnabled())
		{
			// skip repeated validation of certificate.
			JViewerApp.getInstance().getConnection().setKvmSSLVerify(false);
			// should pass webport only for singleport communication
			JViewerApp.getInstance().setSinglePortKvm(new SinglePortKVM( JViewer.getIp(), JViewer.getWebPort(), JViewer.getWebPort(),JViewer.isUseSSL()));
		}
	}
	/**
	 * Constructor
	 */

	public VMApp(){
		webSessionToken = JViewer.getWebSessionToken();
		kvmToken = JViewer.getKVMToken();
		redirectionController = new RedirectionController();
	}
	/**
	 * Gets the static singleton object of this class.
	 * @return the single ton object of VMApp
	 */
	public static VMApp getInstance(){
		return vmApp;
	}

	/**
	 * @return the vmPane
	 */
	public static VMPane getVMPane() {
		return vmPane;
	}

	public static void setVMPane(VMPane pane) {
		vmPane = pane;
	}
	/**
	 * @return statusPanel
	 */
	public static VMStatusBar getVMStatusPanel(){
		return statusPanel;
	}
	/**
	 * @return the vmFrame
	 */
	public static VMFrame getVMFrame() {
		return vmFrame;
	}

	/**
	 * @return the vmDialog
	 */
	public static VMDialog getVMDialog() {
		return vmDialog;
	}
	/**
	 * @return the vmDialog
	 */
	public static void setVMDialog(VMDialog vmDialog) {
		VMApp.vmDialog = vmDialog;
	}
	/**
	 * @return the numCD
	 */
	public int getNumCD() {
		return numCD;
	}

	/**
	 * @param numCD the numCD to set
	 */
	public void setNumCD(int numCD) {
		String[][] tempPath = null;
		int lastCDNum = this.numCD;
		tempPath = cdImagePath;
		this.numCD = numCD;
		cdImagePath = new String[numCD][MAX_IMAGE_PATH_COUNT];
		if(lastCDNum > numCD)
			lastCDNum = numCD;
		if(tempPath != null){
			for(int count = 0; count < lastCDNum; count++){
				cdImagePath[count] = tempPath[count];
			}
		}
	}

	/**
	 * @return the numHD
	 */
	public int getNumHD() {
		return numHD;
	}

	/**
	 * @param numHD the numHD to set
	 */
	public void setNumHD(int numHD) {
		String[][] tempPath = null;
		int lastHDNum = this.numHD;
		tempPath = hdImagePath;
		this.numHD = numHD;
		hdImagePath = new String[numHD][MAX_IMAGE_PATH_COUNT];
		if(lastHDNum > numHD)
			lastHDNum = numHD;
		if(tempPath != null){
			for(int count = 0; count < lastHDNum; count++){
				hdImagePath[count] = tempPath[count];
			}
		}
	}
	public int getFreeCDNum() {
		return freeCDNum;
	}

	public void setFreeCDNum(int num) {
		freeCDNum = num;
	}

	public int getFreeHDNum() {
		return freeHDNum;
	}

	public void setFreeHDNum(int num) {
		freeHDNum = num;
	}

	/**
	 * Upadte the free devices status in VMedia dialog.
	 */
	public void updateFreeDeviceStatus(){
	//To update the free device status. This support needs to be added in VMApp.
		if(vmFrame != null && vmFrame.isShowing()){
			/*vmFrame.updateFreeCDStatus();
			vmFrame.updateFreeFDStatus();
			vmFrame.updateFreeHDStatus();*/
		}
	}

	/**
	 * Converts a given number to Roman numeral format. This uses the Unicode representation
	 * So it is possible to get Roman numeral equivalent up to integer 12.
	 * @param number - number to be converted to Roman numeral
	 * @return Roman numeral String
	 */
	public String getRomanNumber(int number){
		String romanNumber = null;
		int roman = 8544;
		if(number < 12){
			if (number > 0)
				roman += number;
			char ch = (char)roman;
			romanNumber = String.valueOf(ch);
		}
		else
			romanNumber = String.valueOf(number);
		return romanNumber;
	}

	/**
	 * Gets the language code.
	 * @return - language code.
	 */
	public static String getLanguage() {
		return lang;
	}

	/**
	 * @param language the language to set
	 */
	public static void setLanguage(String language) {
		VMApp.lang = language;
		LocaleStrings.setLanguageID(lang);
		//StandAloneConnectionDialog.setSelectedLocale(lang);
	}

	/**
	 * Sets the localization language as English - US (EN)
	 */
	public static void setDefaultLanguage() {
		VMApp.lang = "EN";
		//StandAloneConnectionDialog.setSelectedLocale(lang);
	}
	/**
	 * Gets the BMC IP.
	 * @return
	 */
	public static String getIp() {
		return ip;
	}

	/**
	 * Creates the {@link IUSBRedirSession} object to be used for
	 * VMedia redirection.
	 */
	public void createIUSBRedirectionSession(){
		if(iusbRedirSession != null){
			//if object is not null and count mismatch need to reinitilize
			if((iusbRedirSession.cdromSession.length) != VMApp.getInstance().getNumCD()||
					(iusbRedirSession.hardDiskSession.length) != VMApp.getInstance().getNumHD()){
				iusbRedirSession =  null;
			}
		}
		
		if(iusbRedirSession == null){
			iusbRedirSession = new IUSBRedirSession();
		}
	}

	/**
	 * @return the iusbRedirSession
	 */
	public IUSBRedirSession getIUSBRedirSession() {
		return iusbRedirSession;
	}
	/**
	 * Get the CDROM list from the native library
	 * @return a list of physical CD/DVD drives.
	 */
	public String[] getCDROMList()
	{
		String cdromList[] = null;
		try {
			CDROMRedir cdromObj = new CDROMRedir(true);
			cdromList = cdromObj.getCDROMList();
			System.gc();
		} catch(RedirectionException e) {
			Debug.out.println("Exception occured while getCDROMList()");
			Debug.out.println(e);
		}
		catch (Error err) {
			Debug.out.println("Exception occured while getCDROMList()");
			Debug.out.println(err);
		}
		catch (Exception e) {
			Debug.out.println("Exception occured while getCDROMList()");
			Debug.out.println(e);
		}
		return cdromList;
	}

	/**
	 * Get the Removable USB/HDD list from the native library
	 * @return list of removable hard disk drives.
	 */
	public String[] getHDDUSBList()
	{
		String hardDiskList[] = null;
		//Get the drive list only if the client has Administrator user privilege.
		try {
			if(JViewer.IsClientAdmin()){
				HarddiskRedir harddiskObj = new HarddiskRedir(true);
				hardDiskList = harddiskObj.getUSBHDDList();
				System.gc();
			}
		} catch (Error err) {
			Debug.out.println("Exception occured while getHDDUSBList()");
			Debug.out.println(err);
		}
		catch (Exception e) {
			Debug.out.println("Exception occured while getHDDUSBList()");
			Debug.out.println(e);
		}
		return hardDiskList;
	}
	/**
	 * Get the Fixed HDD from the native library
	 * @return the list of fixed hard disk drives
	 */
	public String[] getHarddiskFixedList()
	{
		String hardDiskList[] = null;
		if(true == JViewer.IsClientAdmin())
		{
			//if redirection in Read/Write mode is enabled, then  the fixed hard disk drives should not be avoided.
			try {
				if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_REDIR_RD_WR_MODE) != JViewerApp.OEM_REDIR_RD_WR_MODE)
				{
					HarddiskRedir harddiskObj = new HarddiskRedir(true);
					hardDiskList = harddiskObj.getHarddiskFixedList();
					System.gc();
				}
			} catch (Error err) {
				Debug.out.println("Exception occured while getHarddiskFixedList()");
				Debug.out.println(err);
			}
			catch (Exception e) {
				Debug.out.println("Exception occured while getHarddiskFixedList()");
				Debug.out.println(e);
			}
		}
		return hardDiskList;
	}


	/**
	 * Get the Fixed HDD from the native library
	 * @return the list of all the available  hard disk drives 
	 */
	public String[] getEntireHarddiskList()
	{
		String hardDiskList[] = null;
		try {
			HarddiskRedir harddiskObj = new HarddiskRedir(true);
			hardDiskList = harddiskObj.getEntireHarddiskList();
			System.gc();
		} catch (Error err) {
			Debug.out.println("Exception occured while getEntireHarddiskList()");
			Debug.out.println(err);
		}
		catch (Exception e) {
			Debug.out.println("Exception occured while getEntireHarddiskList()");
			Debug.out.println(e);
		}
		return hardDiskList;
	}

	/**
	 * gets the  physical device drives list based on the device type
	 * @param deviceType - the type of the device for which the drives list
	 * 						has to be retrieved.
	 * @return the list of physical device drives for a particular device
	 */
	public String[] getDeviceDriveList(int deviceType){
		String[] driveList = null;
		if(deviceType == VMApp.DEVICE_TYPE_CDROM){
			cdDriveList = getCDROMList();
			driveList = cdDriveList;
		}
		else if(deviceType == VMApp.DEVICE_TYPE_HD_USB){
			String[] hdRemovableList = getHDDUSBList();
			String[] hdFixedList = null;

			if( (true == JViewer.IsClientAdmin()) &&( (JViewer.getOEMFeatureStatus() & JViewerApp.OEM_REDIR_RD_WR_MODE) != JViewerApp.OEM_REDIR_RD_WR_MODE)){
				 hdFixedList = getHarddiskFixedList();
			}
			int i=0;

			if(hdRemovableList != null || hdFixedList != null) {
				int deviceLen = 0;

				if(hdRemovableList != null)
					deviceLen =  deviceLen+hdRemovableList.length;
				if(hdFixedList != null)
					deviceLen =  deviceLen + hdFixedList.length;

				hdDriveList = new String[deviceLen] ;

				if(hdFixedList != null) {
					for(int j=0;j<hdFixedList.length;j++,i++) {
						hdDriveList[i] = hdFixedList[j];
						hdDriveList[i]=hdDriveList[i].concat(LocaleStrings.getString("G_16_VMD"));
					}
				}

				if(hdRemovableList != null) {
					for(int k=0;k<hdRemovableList.length;k++) {
						hdDriveList[i] = hdRemovableList[k];
						hdDriveList[i] = hdDriveList[i].concat(" - USB");
						i++;
					}
				}
			}
			else{
				// No harddrives found set it as null
				hdDriveList = null;
			}
			driveList = hdDriveList;
		}
		return driveList;
	}

	/**
	 * @return the cdDriveList
	 */
	public String[] getCDDriveList() {
		return cdDriveList;
	}
	/**
	 * @param cdDriveList the cdDriveList to set
	 */
	public void setCDDriveList(String[] cdDriveList) {
		this.cdDriveList = cdDriveList;
	}
	/**
	 * @return the hdDriveList
	 */
	public String[] getHDDriveList() {
		return hdDriveList;
	}
	/**
	 * @param hdDriveList the hdDriveList to set
	 */
	public void setHDDriveList(String[] hdDriveList) {
		this.hdDriveList = hdDriveList;
	}


	/**
	 * @return the webSessionToken
	 */
	public static String getWebSessionToken() {
		return webSessionToken;
	}
	/**
	 * @param webSessionToken the webSessionToken to set
	 */
	public static void setWebSessionToken(String webSessionToken) {
		VMApp.webSessionToken = webSessionToken;
	}
	/**
	 * @return the kvmToken
	 */
	public static String getKVMToken() {
		return kvmToken;
	}
	/**
	 * @return the redirectionController
	 */
	public RedirectionController getRedirectionController() {
		return redirectionController;
	}
	/**
	 * @param kvmToken the kvmToken to set
	 */
	public static void setKVMToken(String kvmToken) {
		VMApp.kvmToken = kvmToken;
	}

	/**
	 * @return the updateBytesTimer
	 */
	public static Timer getUpdateBytesTimer() {
		return updateBytesTimer;
	}
	/**
	 * Returns the status whether a particular CD instance is redirected.
	 * @param deviceIndex - device instance number
	 * @return true if redirected, false otherwise.
	 */
	public boolean isCDRedirRunning(int deviceIndex){
		boolean isRunning = false;
		if(iusbRedirSession.getCDROMRedirStatus(deviceIndex) ==
				IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED){
			isRunning = true;
		}
		return isRunning;
	}

	/**
	 * Returns the status whether a particular HD instance is redirected.
	 * @param deviceIndex - device instance number
	 * @return true if redirected, false otherwise.
	 */
	public boolean isHDRedirRunning(int deviceIndex){
		boolean isRunning = false;
		if(iusbRedirSession.getHarddiskRedirStatus(deviceIndex) ==
				IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED){
			isRunning = true;
		}
		return isRunning;
	}
	/**
	 * Returns the status whether at least one CD redirection instance is active.
	 * @return  - true if at least one CD redirection instance is active.<br>
	 * 			- false if none of the CD redirection instances are active.
	 */
	public boolean isCDRedirRunning(){
		boolean isRunning = false;
		try {
			for(int deviceIndex = 0; deviceIndex < numCD; deviceIndex++)
				if(iusbRedirSession.getCDROMRedirStatus(deviceIndex) ==
				IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED){
					isRunning = true;
				}
		} catch (Exception e) {
			Debug.out.println(e);
			isRunning = false;
		}
		return isRunning;
	}
	/**
	 * Returns the status whether at least one hard disk redirection instance is active.
	 * @return  - true if at least one hard disk redirection instance is active.<br>
	 * 			- false if none of the hard disk redirection instances are active.
	 */
	public boolean isHDRedireRunning(){
		boolean isRunning = false;
		try {
			for(int deviceIndex = 0; deviceIndex < numHD; deviceIndex++)
				if(iusbRedirSession.getHarddiskRedirStatus(deviceIndex) ==
				IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED){
					isRunning = true;
				}
		} catch (Exception e) {
			Debug.out.println(e);
			isRunning = false;
		}
		return isRunning;
	}
	/** Display an error message with the specified title and text
	 *  @param title The title of the error dialog
	 *  @param message The main text of the error dialog */
	public void generalErrorMessage( String title, String message )
	{
		// display the error message in non blocking dialog.
		InfoDialog.showDialog(vmFrame,  message, title,InfoDialog.ERROR_DIALOG);
	}

	/**
	 * Initialize the DeviceDetectot thread
	 */
	public void initDeviceDetector(){
		if(devDetector == null){
			devDetector = new DeviceDetector();
			devDetector.setName("DeviceDetector");
		}
	}

	/**
	 * Initialize the RedirectionStatusMonitor thread
	 */
	public void initRedirectionStatusMonitor(){
		if(redirectionStatusMonitor == null){
			redirectionStatusMonitor = new RedirectionStatusMonitor();
			redirectionStatusMonitor.setName("RedirectionStatusMonitor");
		}
	}

	/**
	 * Stop the DeviceDetectot thread
	 */
	public static void stopDeviceDetector(){
		if(devDetector != null)
			devDetector.stopDeviceDetector();
		devDetector = null;
	}
	/**
	 * stop the RedirectionStatisMonitor thread.
	 */
	public static void stopRedirectionStatusMonitor() {
		if(redirectionStatusMonitor != null)
			redirectionStatusMonitor.stopRedirectionStatusMonitior();
		redirectionStatusMonitor = null;
	}
	/**
	 * Exit VMApp
	 */
	public static void exit(int mode) {
		stopDeviceDetector();
		stopRedirectionStatusMonitor();
		System.exit(mode);
	}

	public static int getWidth() {
		return width;
	}

	public static int getHeight() {
		return height;
	}

	public static void setWidth(int newWidth) {
		width = newWidth;
	}

	public static void setHeight(int newHeight) {
		height = newHeight;
	}
	public static Point getWindowPos() {
		return windowPos;
	}
	public static void setWindowPos(Point windowPos) {
		VMApp.windowPos = windowPos;
	}

}
