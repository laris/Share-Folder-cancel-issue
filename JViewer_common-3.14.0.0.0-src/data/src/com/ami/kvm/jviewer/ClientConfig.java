/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2016, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
 ****************************************************************/
package com.ami.kvm.jviewer;

import java.io.File;

import javax.swing.JOptionPane;

import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.kvm.jviewer.gui.StandAloneConnectionDialog;
import com.ami.vmedia.VMApp;

/**
 * Class used for fetching client system
 * configuration details using Native library calls
 *
 * @author Mohammed Javith Akthar M
 */
public class ClientConfig {
	// Native library call
	private native String GetKeyboardName();
	private native byte GetLEDStatus();
	private native byte SetLEDStatus(byte status);
	private native String getVersion();

	/***
	 * Loading the Library for Acccesing the Floppy
	 */
	static {
		try {
			if( !JViewer.isdownloadapp() && !JViewer.isplayerapp())
			{
				if(JViewer.isjviewerapp()){ // Normal JViwer
					System.loadLibrary("javaclientconfwrapper");
				}
				else { //For SPXMultiViewer and StandAloneApp
					loadWrapperLibrary();
				}
			}
		} catch (UnsatisfiedLinkError e) {
			Debug.out.println(LocaleStrings.getString("5_1_CLIENTCFG"));
		}

	}

	/**
	 * Loads the javawrapper library files. 
	 */
	private static void loadWrapperLibrary(){
		String libPath = null;
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
		File libFile = null;
		//Replace all ':' characters from the common path with '_'. This is because in Windows file system,
		//file and directory names and are not allowed to contain ':'. In this case the getIP() method
		//might return IPV6 address which will contain : and will lead to error.
		if(System.getProperty("os.name").startsWith("Windows")){
			if(JViewer.isStandAloneApp() || JViewer.isVMApp()){
				commonPath = StandAloneConnectionDialog.replaceAllPattern(commonPath, ":", "_");
				libPath = currPath+commonPath+"javaclientconfwrapper.dll";
				libFile = new File(libPath);
				if(false == StandAloneConnectionDialog.getWrapperLibrary("javaclientconfwrapper.dll")){
					Debug.out.println("Unable to extract the javaclientconfwrapper.dll");
					libPath = null;
				}
			}
			else{
				libPath = System.getProperty("user.dir")+commonPath+"javaclientconfwrapper.dll";
			}
		}
		else if(System.getProperty("os.name").startsWith("Linux")){
			if(JViewer.isStandAloneApp() || JViewer.isVMApp()){
				libPath = currPath+commonPath+"libjavaclientconfwrapper.so";
				libFile = new File(libPath);
				if(false == StandAloneConnectionDialog.getWrapperLibrary("libjavaclientconfwrapper.so")){
					Debug.out.println("Unable to extract the libjavaclientconfwrapper.so");
					libPath = null;
				}
			}
			else{
				libPath = System.getProperty("user.dir")+commonPath+"libjavaclientconfwrapper.so";
			}
		}
		else if(System.getProperty("os.name").startsWith("Mac")){
			if(JViewer.isStandAloneApp() || JViewer.isVMApp()){
				libPath = currPath+commonPath+"libjavaclientconfwrapper.jnilib";
				libFile = new File(libPath);
				if(false == StandAloneConnectionDialog.getWrapperLibrary("libjavaclientconfwrapper.jnilib")){
					Debug.out.println("Unable to extract the libjavaclientconfwrapper.jnilib");
					libPath = null;
				}
			}
			else
				libPath = System.getProperty("user.dir")+commonPath+"libjavaclientconfwrapper.jnilib";
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
	 * Getting the lib floppy Version from the Native call
	 *
	 * @return
	 */

	public String getLIBVersion() {
		return (getVersion());
	}

	/**
	 * Read the LED status of the Client from the native library call
	 * @return LED state
	 */
	public byte ReadKeybdLEDStatus() {
		byte kbdLEDStatus = 0;
		try {
			kbdLEDStatus = GetLEDStatus();
		}
		catch(UnsatisfiedLinkError ule){
			Debug.out.println("UnsatisfiedLinkError");
			Debug.out.println(ule);
		}
		catch (Exception e) {
			Debug.out.println(e);
		}
		return kbdLEDStatus;
	}

	/**
	* Sets the keyboard LED status to the client machine
	 * @param ledStatus - the LED statsu to be set.
	*/
	public void setKeyboardLEDStatus(byte ledStatus){
		try {
			SetLEDStatus(ledStatus);
		}
		catch(UnsatisfiedLinkError ule){
			Debug.out.println("UnsatisfiedLinkError");
			Debug.out.println(ule);
		}
		catch (Exception e) {
			Debug.out.println(e);
		}
	}

	/**
	 * Return the client system keyboard layout
	 * @return
	 */
	public String ReadKeybdType()
	{
		String kbdType = null;
		try {
			kbdType = GetKeyboardName();
		}
		catch(UnsatisfiedLinkError ule){
			Debug.out.println("UnsatisfiedLinkError");
			Debug.out.println(ule);
		}
		catch (Exception e) {
			Debug.out.println(e);
		}
		return kbdType;
		
	}

	/**
	 * Shows the error message if loading the native library fails
	 */
	private static void showLibraryLoadError(){
		if(JViewer.isVMApp()){
			JOptionPane.showMessageDialog(VMApp.getVMFrame(), LocaleStrings.getString("5_3_CLIENTCFG"),
					LocaleStrings.getString("1_3_JVIEWER"), JOptionPane.ERROR_MESSAGE);
			VMApp.exit(0);
		}
		else{
			JOptionPane.showMessageDialog(JViewer.getMainFrame(), LocaleStrings.getString("5_3_CLIENTCFG"),
					LocaleStrings.getString("1_3_JVIEWER"), JOptionPane.ERROR_MESSAGE);
			if(JViewerApp.getInstance() != null){
				if(JViewerApp.getInstance().getM_frame() != null)
					JViewerApp.getInstance().getM_frame().windowClosed();
				else
					JViewer.exit(0);
			}
		}
	}

}
