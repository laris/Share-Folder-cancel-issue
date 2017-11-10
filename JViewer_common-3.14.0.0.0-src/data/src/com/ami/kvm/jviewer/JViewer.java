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
////////////////////////////////////////////////////////////////////////////////
//
// JViewer main module.
//

package com.ami.kvm.jviewer;

import java.awt.Dimension;
import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.ami.kvm.jviewer.common.oem.IOEMManager;
import com.ami.kvm.jviewer.gui.JVMenu;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.kvm.jviewer.gui.StandAloneConnectionDialog;
import com.ami.vmedia.VMApp;


/**
 * JViewer main module class.
 */
public class JViewer {
	public final static String APP_TYPE_JVIEWER = "JViewer";
	public final static String APP_TYPE_VMAPP = "VMApp";
	public final static String APP_TYPE_PLAYER = "PlayVideo";
	public final static String APP_TYPE_DOWNLOAD_SAVE = "SaveVideo";
	public final static String APP_TYPE_STAND_ALONE = "StandAlone";
	public final static String APP_TYPE_WEB_PREVIEW = "WebPreview";
	public final static String APP_TYPE_BSOD_VIEWER = "BSODScreen";
	public final static String DEFAULT_LOCALE = "EN";
	public final static int HTTPS_PORT = 443;
	public final static int INVALID_PORT = -1;
	public static final int MIN_FRAME_WIDTH = 800;
	public static final int MIN_FRAME_HEIGHT = 600;
	public static final int EXT_PRIV_UNDEF = -1;
	public static final int KVM_DEF_RECONN_RETRY = 3;
	public static final int KVM_DEF_RECONN_INTERVAL = 15;
	public static final int MEDIA_SERVICE_ENABLED = 1;
	// Java version
	public static final int MIN_JAVA_VERSION = 7;
	public static final String JAVA_RUNTIME_VERSION = System.getProperty("java.runtime.version");
	//BIT manipulation for  privilege
	//Extended privilege
	public static final byte KVM_ENABLED = 0;
	public static final byte VMEDIA_ENABLED = 1;

	//User privilege
	public static final byte POWER_OPTION_PRIV = 8;

	public static final int LICENSED = 1;
	public static final String AUTO_DETECT_KEYBOARD = "AD";

	//The following String array holds the list of supported SOCs.
	//The name of the SOC should be given in the index corresponding to the
	//SOC ID used. (Assuming the index starts from 1).
	public static String[] SOC = {"PILOT", "AST"};
	private static String OPTION_USAGE = LocaleStrings.getString("1_2_JVIEWER")+"JViewer.jar" +
										" < -apptype StandAlone> < -hostname "+
										LocaleStrings.getString("S_2_SACD")+" > < -u "+
										LocaleStrings.getString("S_4_SACD")+" > < -p "+
										LocaleStrings.getString("S_5_SACD")+"> < -webport "+
										LocaleStrings.getString("S_3_SACD")+"> <-localization/-lang "+
										LocaleStrings.getString("S_21_SACD")+"> <-launch "+
										LocaleStrings.getString("S_34_SACD")+">";

	private static JFrame mainFrame;
	private static JDesktopPane mainPane;
	//Default apptype will be set to StandAlone.
	//This will avoid problems while launching the StandAlone app by double clicking
	//the JViewer.jar or form cmmand prompt or terminal.
	private static String apptype = null;

	private static boolean standalone;

	private static String title = "JViewer";
	private static String ip;
	private static byte[] ServerIp;
	private static int kvmPort = 7578;
	private static int SecureChannel = 0;
	private static int VMSecureChannel = 0;
	private static int cdserver_port = 0;
	private static int hdserver_port = 0;
	private static byte Num_CD = 1;
	private static byte Num_HD = 1;
	private static int CD_State = 0;
	private static int HD_State = 0;
	private static boolean useSSL = false;
	private static boolean VMUseSSL = false;
	private static String webSessionToken = null;
	private static String kvmToken= null;
	private static int kvmTokenType = 0;
	private static String lang= null;
	private static int webPort = INVALID_PORT; // default value set to an invalid port number.
	private static int webSecure = 0;
	private static String keyboardLayout = AUTO_DETECT_KEYBOARD;// Auto-Detect Keyboard layout
	private static String[] videoFile = null;
	private static boolean isKVMReconnectEnabled = false;
	private static int retryCount = KVM_DEF_RECONN_RETRY;
	private static int retryInterval = KVM_DEF_RECONN_INTERVAL;
	private static String username = null;
	private static String password = null;
	/* -launch parameter value
	 * Used in StandAlone application for launching required application type from commandline / terminal */
	private static String launch = null;
	private static boolean isSinglePortEnabled = false;
	private static int argLength = 0;
	private static int kvmPrivilege = EXT_PRIV_UNDEF;
	private static boolean unknownParam = false;
	private static String unknownParams = "";
	private static long OEMFeatureStatus = 0;
	private static byte KVMLicenseStatus = 0;
	private static byte MediaLicenseStatus = 0;
	private static boolean isClientAdmin = false;
	private static byte KVM_Num_CD = 1;
	private static byte KVM_Num_HD = 1;
	private static byte powerSaveMode = 0;
	private static ClientConfig clientCfg;

	//Supported Host Physical keyboard layouts.
	private static final Set<String> KEYBOARD_LAYOUTS = new HashSet<String>(Arrays.asList
			(new String[] {"AD" /*Auto Detect*/, "US" /*English US*/, "GB" /*English UK*/, "FR" /*French France*/,
						"FR-BE" /*French Belgium*/, "DE" /*German Germany*/, "DE-CH" /*German Switzerland*/, "JP" /*Japanese*/,
						"ES" /*Spanish*/, "IT" /*Italian*/, "DA" /*Danish*/, "FI" /*Finnish*/, "NO" /*Norwegian*/,
						"PT" /*Portuguese*/, "SV" /*Swedish*/, "NL-NL" /*Dutch Netherland*/, "NL-BE" /*Dutch Belgium*/,
						"TR_F" /*Turkish F*/, "TR_Q" /*Turkish Q*/}));

	/*apptype - first argument
	 * apptype = "JViewer" // Jviewer Application
	 * apptype = "PlayVideo" //Video play only
	 * apptype = "DownloadVideo"//Video play and save
	 * apptype = "StandAlone"//JViewer StandAlone Application
	 */
	private static boolean restService = false;
	/* Used for enabling / disabling language combo box in StandAlone application's input dialog */
	private static boolean defaultlang = false;

	//Modified for JInternalFrame
	/**
	 * main method. JViewer starts execution from here.
	 *
	 * @param args command line arguments.
	 */
	public static void main(String[] args) {
		mainFrame = new JFrame("JViewer");
		mainFrame.setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		standalone = true;
		argLength = args.length;

		ParseCmd(args);
		if(lang == null){
			setLanguage(JViewer.DEFAULT_LOCALE);
			/* For disabling language combo box under Standalone Application input dialog */
			setDefaultLang(true);
		}
		// Only 64-bit JVM is supported. If launched using 32-bit JVM block the execution.
		if (!is64bit()) {
			JOptionPane.showMessageDialog(null,
					LocaleStrings.getString("1_11_JVIEWER") + "64-bit " + LocaleStrings.getString("1_10_JVIEWER"),
					LocaleStrings.getString("1_3_JVIEWER"), JOptionPane.ERROR_MESSAGE);
			JViewer.exit(0);
		}
		// For showing unsupported java version message in proper localization
		String[] javaVersion = JAVA_RUNTIME_VERSION.split("\\.|_|-b");
		if (Integer.parseInt(javaVersion[1/* Major Version */], 10/* Decimal value */) < MIN_JAVA_VERSION) {
			JOptionPane.showMessageDialog(null, LocaleStrings.getString("1_9_JVIEWER") + LocaleStrings.getString("1_10_JVIEWER") + JAVA_RUNTIME_VERSION + "\n" + LocaleStrings.getString("1_11_JVIEWER") + LocaleStrings.getString("1_10_JVIEWER") + MIN_JAVA_VERSION + LocaleStrings.getString("1_12_JVIEWER"),
					LocaleStrings.getString("1_3_JVIEWER"),
					JOptionPane.ERROR_MESSAGE);
			JViewer.exit(0);
		}
		if(apptype == null)
			apptype = APP_TYPE_STAND_ALONE;
		// if invalid apptype found
		if( !( isStandAloneApp()
				|| isBSODViewer()
				|| isWebPreviewer()
				|| isStandAloneSupportedApps(apptype) ) ){ /* other apptypes, can validated using isStandAloneSupportedApps() method */
			JOptionPane.showMessageDialog(null, LocaleStrings.getString("1_4_JVIEWER"),
					LocaleStrings.getString("S_9_SACD"),
					JOptionPane.ERROR_MESSAGE);
		}
		// -launch parameter is applicable only for StandAlone apptype
		if( (!isStandAloneApp()) && (isStandAloneSupportedApps(getLaunch())) ){
				JOptionPane.showMessageDialog(null, LocaleStrings.getString("1_1_JVIEWER"),
						LocaleStrings.getString("S_9_SACD"),
						JOptionPane.ERROR_MESSAGE);
				JViewer.exit(0);
		}
		if(args.length >0){// Should not go in when StandAloneApp is launched by double clicking the jar.
			Debug.out.println("JViewer Arguments\n");
			for(int p=0;p<args.length-1;p+=2)
			{
				Debug.out.println(args[p]+" : "+args[p+1]);
			}
		}
		// if vmedia max count for kvm feature is enabled then cd/hdnum should be updated with kvm cd/hd count.
		if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_KVM_MAX_DEVICE_COUNT) ==
				JViewerApp.OEM_KVM_MAX_DEVICE_COUNT) {
			VMApp.getInstance().setNumCD(KVM_Num_CD);
			VMApp.getInstance().setNumHD(KVM_Num_HD);
		}
		setIsClientAdmin(isClientUserAdmin());
		if(isjviewerapp())
		{
			JViewerApp.getInstance().getConnection().setWebSSLVerify(false);
			Debug.out.println("JViewer Application Initialised");
			redirect();
		}
		else if(isplayerapp())
		{
			Debug.out.println("Player Application  Initialised");
			recording();
		}
		else if(isdownloadapp())
		{
			Debug.out.println("Download and save Application  Initialised");
			recording();
		}
		else if(isStandAloneApp()){
			Debug.out.println("Stand Alone Application  Initialised");
			launchStandAlone();
		}
		else if(isVMApp()){
			launchStandAlone();
		}

		else
		{
			printUsage();
		}

	}

	/**
	 * Checks the application type is Jviewer else return false
	 * @return	true- JViewer APP
	 * 			false - If not JVIewer App
	 */
	public static boolean isjviewerapp() {
		if(apptype != null) {
			if(apptype.compareToIgnoreCase(JViewer.APP_TYPE_JVIEWER) == 0)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks the appliaction type is Player else retuirn false
	 * @return	true- JViewer APP
	 * 			false - If not JVIewer App
	 */
	public static boolean isplayerapp() {
		if(apptype != null) {
			if(apptype.compareToIgnoreCase(JViewer.APP_TYPE_PLAYER) == 0)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks the appliaction type is Download and save else retuirn false
	 * @return	true- JViewer APP
	 * 			false - If not JVIewer App
	 */
	public static boolean isdownloadapp() {
		if(apptype != null) {
			if(apptype.compareToIgnoreCase(JViewer.APP_TYPE_DOWNLOAD_SAVE) == 0)
			{
				return true;
			}
		}
		return false;
	}

	public static boolean IsBitSet(int value, int pos)
	{
	   return (value & (1 << pos)) != 0;
	}

	/**
	 * Checks whether application type is Stand Alone App.
	 * @return  true if application type is Stand Alone App; false otherwise.
	 */
	public static boolean isStandAloneApp(){
		if(apptype != null) {
			if(apptype.compareToIgnoreCase(JViewer.APP_TYPE_STAND_ALONE) == 0)
			{
				return true;
			}
		}
		return false;
	}

	public static String getIp() {
		return ip;
	}

	/**
	 * Returns -launch parameter value.
	 * @return  launch
	 */
	public static String getLaunch() {
		return launch;
	}

	/**
	 * Returns defaultlang value
	 * @return  true if localization is set by default value; false otherwise.
	 */
	public static boolean isDefaultLang(){
		return defaultlang;
	}

	/**
	 * enables defaultlang flag
	 * @param status Enables / Disables the flag value
	 */
	public static void setDefaultLang(boolean status){
		JViewer.defaultlang = status;
	}

	/**
	 * Validates supported apptypes for -launch parameter.
	 * @param apptype value to be checked.
	 * @return  true if argument is valid apptype; false otherwise.
	 */
	public static boolean isStandAloneSupportedApps(String apptype){
		if(apptype != null) {
			if( (apptype.compareToIgnoreCase(JViewer.APP_TYPE_JVIEWER) == 0)
					|| (apptype.compareToIgnoreCase(JViewer.APP_TYPE_PLAYER) == 0)
					|| (apptype.compareToIgnoreCase(JViewer.APP_TYPE_DOWNLOAD_SAVE) == 0)
					|| (apptype.compareToIgnoreCase(JViewer.APP_TYPE_VMAPP) == 0) ) {
				return true;
			}
		}
		return false;
	}

	public static void launch(JFrame frame, JDesktopPane pane,String[] args) {
		standalone = false;
		mainFrame = frame;
		mainPane = pane;
		redirect();
	}

	public static void recording(){

		String port=Integer.toString(webPort);
		JViewerApp.getInstance().Ondisplayvideo(ip,port,webSessionToken,webSecure);
	}

	/**
	 * Launches the JVIewer Application in Stand Alone Mode
	 */
	public static void launchStandAlone(){

		JViewerApp.getInstance().onLaunchStandAloneApp(ip, webPort, username, password);
	}

	/**
	 * Launches the JVIewer Application in Stand Alone Mode
	 */
	public static void launchVMApp(){

		JViewerApp.getInstance().OnvMedia(VMApp.CD_MEDIA);
	}

	//Added for JInternalFrame
	/**
	 *
	 * @param args
	 */
	public static void redirect() {

		if(isKVMEnabled()){
			//If singleport is enabled no need to validate web certificate again. if its launched form web and standalone application.
			if(JViewer.isSinglePortEnabled() == true)
			{
				JViewerApp.getInstance().getConnection().setKvmSSLVerify(false);
			}
			JViewerApp.getInstance().OnConnectToServer(ip, kvmPort, kvmToken, kvmTokenType, useSSL,
					VMUseSSL, cdserver_port, hdserver_port,Num_CD, Num_HD,CD_State, HD_State,
					webSessionToken, webPort);
			VMApp.getInstance().createIUSBRedirectionSession();;
		}
		else{
			JOptionPane.showMessageDialog(null, LocaleStrings.getString("1_5_JVIEWER"),
					LocaleStrings.getString("1_3_JVIEWER"),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	public static void ParseCmd(String[] args){

		int i=0;
		int enabled = -1;
		String arg = null;
		while (i < args.length && args[i].startsWith("-"))
 		{
			try{
				arg = args[i++];
				//apptype
				if(arg.contains("-oem")) {
					if(JViewerApp.getOEMManager().handleOemArguments(args, i) == IOEMManager.OEM_CUSTOMIZED) {
						continue;
					}
				}
				if (arg.equals("-apptype"))
				{
					if (i < args.length)
					{
						apptype = args[i++];
					}
					else
						System.err.println("-apptype"+LocaleStrings.getString("1_4_JVIEWER"));
				}
				else if (arg.equals("-launch"))
				{
					if (i < args.length)
					{
						launch = args[i++];
						// validating -launch parameter value
						if(!isStandAloneSupportedApps(launch)){
							JOptionPane.showMessageDialog(null, LocaleStrings.getString("1_1_JVIEWER")+" -launch",
									LocaleStrings.getString("S_9_SACD"),
									JOptionPane.ERROR_MESSAGE);
							printUsage();
						}
					}
				}
				//get JViewer title from JViewer argumment
				else if(arg.equals("-title")){
					if(args[i] != null)
						title = args[i];
					else
						title = "JViewer";
					i++;
				}
				//hostname
				else if(arg.equals("-hostname"))
				{
					ip =args[i];
					ServerIp = getServerIP(args[i++]);
					if (ServerIp == null) {
						if(!isStandAloneApp()){
							printUsage();
							return;
						}
					}
				}
				//kvmtoken
				else if(arg.equals("-kvmtoken"))
				{
					try {
						kvmToken = args[i++];
					} catch (NumberFormatException e) {
						Debug.out.println(e);
						printUsage();
						return;
					}
				}
				//kvmtokentype
			else if(arg.equals("-kvmtokentype"))
			{
				try {
					kvmTokenType = Integer.parseInt(args[i++]);
				} catch (NumberFormatException e) {
					Debug.out.println(e);
					printUsage();
					return;
				}
			}
				//kvmsecure
				else if(arg.equals("-kvmsecure"))
				{
					try {
						SecureChannel = Integer.parseInt(args[i++]);
						if (SecureChannel != 0 && SecureChannel != 1){
							printUsage();
							return;
						}
						useSSL = (SecureChannel == 1) ? true : false;
					}
					catch (NumberFormatException e) {
						Debug.out.println(e);
						printUsage();
						return;
					}
				}
				//kvmport
				else if(arg.equals("-kvmport"))
				{
					try {
						kvmPort = Integer.parseInt(args[i++]);
					} catch (NumberFormatException e) {
						Debug.out.println(e);
						printUsage();
						return;
					}
				}
				//vmsecure
				else if(arg.equals("-vmsecure"))
				{
					try {
						VMSecureChannel = Integer.parseInt(args[i++]);
						if (VMSecureChannel != 0 && VMSecureChannel != 1) {
							printUsage();
							return;
						}
						VMUseSSL = (VMSecureChannel == 1) ? true : false;
					}
					catch (NumberFormatException e) {
						Debug.out.println(e);
						printUsage();
						return;
					}
				}
				//cdstate
				else if(arg.equals("-cdstate"))
				{
					try {
						CD_State = Integer.parseInt(args[i++]);
					}
					catch (NumberFormatException e) {
						Debug.out.println(e);
						printUsage();
						return;
					}
				}
				//cdport
				else if(arg.equals("-cdport"))
				{
					try {
						cdserver_port = Integer.parseInt(args[i++]);
					} catch (NumberFormatException e) {
						Debug.out.println(e);
						printUsage();
						return;
					}
				}
				//cdnum
				else if(arg.equals("-cdnum"))
				{
					try {
						Num_CD = (byte) Integer.parseInt(args[i++]);
						//update cd num for vmapp use
						VMApp.getInstance().setNumCD(Num_CD);
					} catch (NumberFormatException e) {
						printUsage();
						return;
					}
				}
				//hdstate
				else if(arg.equals("-hdstate"))
				{
					try {
						HD_State = Integer.parseInt(args[i++]);
					}
					catch (NumberFormatException e) {
						printUsage();
						return;
					}
				}
				//hdport
				else if(arg.equals("-hdport"))
				{
					try {
						hdserver_port = Integer.parseInt(args[i++]);
					} catch (NumberFormatException e) {
						printUsage();
						return;
					}
				}
				//hdnum
				else if(arg.equals("-hdnum"))
				{
					try {
						Num_HD = (byte) Integer.parseInt(args[i++]);
						//update hd num for vmapp use
						VMApp.getInstance().setNumHD(Num_HD);
					} catch (NumberFormatException e) {
						Debug.out.println(e);
						printUsage();
						return;
					}
				}
				//lang
				else if(arg.equals("-localization") || arg.equals("-lang"))
				{
					lang = args[i++];
					if(lang.equals(null) || lang.length() == 0){
						if(isStandAloneApp())
							JOptionPane.showMessageDialog(null, LocaleStrings.getString("1_1_JVIEWER")
									+arg+" !!!\n"+OPTION_USAGE,LocaleStrings.getString("1_3_JVIEWER"),
									JOptionPane.ERROR_MESSAGE);
						printUsage();
						return;
					}
					// Either -localization or -lang is supported. Not both together.
					else if(Arrays.asList(args).contains("-localization")
							&& Arrays.asList(args).contains("-lang")){
						JOptionPane.showMessageDialog(null, LocaleStrings.getString("1_8_JVIEWER")
								+" -localization / -lang",LocaleStrings.getString("A_5_GLOBAL"),
								JOptionPane.ERROR_MESSAGE);
						JViewer.exit(0);
					}
					JViewer.setLanguage(lang.toUpperCase());
				}
				//webcookie
				else if(arg.equals("-webcookie"))
				{
					try {
						webSessionToken = args[i++];
					}
					catch (NumberFormatException e) {
						Debug.out.println(e);
						printUsage();
						return;
					}
				}
				//websecure
				else if(arg.equals("-websecure"))
				{
					try {
						webSecure = Integer.parseInt(args[i++]);
					}
					catch (NumberFormatException e) {
						Debug.out.println(e);
						printUsage();
						return;
					}
				}
				//webport
				else if(arg.equals("-webport"))
				{
					webPort = getWebPortNumber(args[i++]);
				}
				else if(arg.equals("-keyboardlayout"))
				{
					keyboardLayout = args[i++];
				}
				//retry count
				else if(arg.equals("-retrycount"))
				{
					try {
						setRetryCount(Integer.parseInt(args[i++]));
					}
					catch (NumberFormatException e) {
						printUsage();
						return;
					}
				}
				//retry interval
				else if(arg.equals("-retryinterval"))
				{
					try {
						setRetryInterval(Integer.parseInt(args[i++]));
					}
					catch (NumberFormatException e) {
						printUsage();
						return;
					}
				}

				//videofile
				else if(arg.equals("-videofile"))
				{
					try {
						if(videoFile == null)
							videoFile = new String[1];
						videoFile[0] = args[i++];
					}
					catch (NumberFormatException e) {
						Debug.out.println(e);
						printUsage();
						return;
					}
				}
				// username and password for StandAlone app
				else if(arg.equalsIgnoreCase("-u")){
					username = args[i++];
				}
				else if(arg.equalsIgnoreCase("-p")){
					int pos = i++;
					try{
						if(! args[pos].startsWith("-"))
							password = args[pos];
						else
							i--;
					}
					catch(Exception e){
					}
				}
				else if(arg.equals("-singleportenabled"))
				{
					try {
						enabled = Integer.parseInt(args[i++]);
						if (enabled != 0 && enabled != 1){
							printUsage();
							return;
						}
						isSinglePortEnabled = (enabled == 1) ? true : false;
					}
					catch (NumberFormatException e) {
						Debug.out.println(e);
						printUsage();
						return;
					}
				}
				else if(arg.equals("-extendedpriv")){
					kvmPrivilege = Integer.parseInt(args[i++]);
				}
				//kvmcdnum
				else if(arg.equals("-kvmcdnum"))
				{
					try {
						KVM_Num_CD = (byte) Integer.parseInt(args[i++]);

					} catch (NumberFormatException e) {
						printUsage();
						return;
					}
				}
				//KVMhdnum
				else if(arg.equals("-kvmhdnum"))
				{
					try {
						KVM_Num_HD = (byte) Integer.parseInt(args[i++]);
					} catch (NumberFormatException e) {
						printUsage();
						return;
					}
				}
				else if(arg.equals("-powersavemode")){
					try {
						powerSaveMode = (byte) Integer.parseInt(args[i++]);
					} catch (NumberFormatException e) {
						printUsage();
						return;
					}
				}
				else{
					unknownParam = true;
					if(unknownParams.length() <= 0)
						unknownParams += arg;
					else
						unknownParams += ", "+arg;
					printUsage();
					i++;
				}

			}catch(Exception e){
				Debug.out.println(e);
				if(isStandAloneApp())
					JOptionPane.showMessageDialog(null, LocaleStrings.getString("1_1_JVIEWER")
							+arg+" !!!\n"+OPTION_USAGE,LocaleStrings.getString("1_3_JVIEWER"),
							JOptionPane.ERROR_MESSAGE);
				printUsage();
			}

			// for processing duplicate arguments
			if( Collections.frequency(Arrays.asList(args), arg) > 1 ){
					JOptionPane.showMessageDialog(null, LocaleStrings.getString("1_8_JVIEWER")+" "+arg,
							LocaleStrings.getString("A_5_GLOBAL"),
							JOptionPane.ERROR_MESSAGE);
					JViewer.exit(0);
			}
		}

		if(isStandAloneApp() || apptype == null){
			if(unknownParams.length() > 0)
			JOptionPane.showMessageDialog(null, LocaleStrings.getString("1_1_JVIEWER")
					+unknownParams+" !!!\n"+OPTION_USAGE,LocaleStrings.getString("1_1_JVIEWER"), 
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * print usage message.
	 */
	public static void printUsage() {
		if(isStandAloneApp() || apptype == null){
			Debug.out.println(OPTION_USAGE);
		}
		else{
			Debug.out.println("Invalid arguments, please try again");
			Debug.out
			.println("Usage: java -jar JViewer.jar <apptype> <ip address> <KVM port number> <token> <ssl for KVM> <ssl for vmedia> <Number of parallel CD/DVD Redirection>  <Number of parallel Hard disk Redirection> <cdserver port> <hdserver port> <user privileges> <language> <token>");
			Debug.out
			.println("<apptype> JViewer for JViewer App,Player for playing the video,SAveVideo for download and save video in cleint system");
			Debug.out
			.println("<ssl for KVM> 1 for secure connection and 0 for non-secure ");
			Debug.out
			.println("<ssl for vmedia> 1 for secure connection and 0 for non-secure ");
			Debug.out.println("<user privileges> ");
			Debug.out
			.println("                  0x00000020 - VKVM permissions only");
			Debug.out
			.println("                  0x00000040 - VMedia permissions only");
			Debug.out
			.println("                  0x00000060 - VKVM & VMedia permissions");
			Debug.out.println("<language> ");
			Debug.out.println("                  EN - English");
		}
		if(JViewer.isStandalone()){
			if(!unknownParam)
				JViewer.exit(0);
			else
				unknownParam = false;
		}
		else
			JViewerApp.getInstance().getMainWindow().dispose();
	}

    /*
     * Get server ip address in byte array.
     *
     * @param ipStr ip address in string format.
     * @return server ip address.
     */
    public static byte[] getServerIP(String ipStr)
    {
    	byte[] ipDgt = null ;
    	try {
			ip = ipStr;
			InetAddress hostAddress = InetAddress.getByName(ipStr);
			ipStr = hostAddress.getHostAddress();
			ipDgt = InetAddress.getByName(ipStr).getAddress();
			Debug.out.println("Resolving to IPAddress " + ipStr);

			for(int i=0;i<ipDgt.length;i++)
				Debug.out.print(ipDgt[i]);
		} catch (UnknownHostException e) {
			ip = null;
			Debug.out.println("Error Resolving IP address");
			Debug.out.println(e);
		}

        return ipDgt;
    }

	/**
	 * @param hostName the hostName to set
	 */
	public static void setServerIP(byte[] hostName) {
		JViewer.ServerIp = hostName;
	}

	public static JFrame getMainFrame() {
		return mainFrame;
	}

	public static void setMainFrame(JFrame mainFrame) {
		JViewer.mainFrame = mainFrame;
	}

	public static JDesktopPane getMainPane() {
		return mainPane;
	}

	public static void setMainPane(JDesktopPane mainPane) {
		JViewer.mainPane = mainPane;
	}

	public static boolean isStandalone() {
		return standalone;
	}

	public static void setStandalone(boolean standalone) {
		JViewer.standalone = standalone;
	}
	/**
	 * @param ip the ip to set
	 */
	public static void setIp(String ip) {
		JViewer.ip = ip;
	}

	/**
	 * @param kvmport the port to set
	 */
	public static void setKVMPort(int kvmport) {
		JViewer.kvmPort = kvmport;
	}

	public static String getKVMToken() {
		return JViewer.kvmToken;
	}

	/**
	 * @param sessionCookies the sessionCookies to set
	 */
	public static void setSessionCookies(String sessionCookies) {
		JViewer.kvmToken = sessionCookies;
	}

	public static String getWebSessionToken() {
		return JViewer.webSessionToken;
	}
	/**
	 * @param webSessionToken the webSessionToken to set
	 */
	public static void setWebSessionToken(String webSessionToken) {
		JViewer.webSessionToken = webSessionToken;
	}
	/**
	 * @param secureChannel the secureChannel to set
	 */
	public static void setSecureChannel(int secureChannel) {
		SecureChannel = secureChannel;
	}

	/**
	 * @param vMSecureChannel the vMSecureChannel to set
	 */
	public static void setVMSecureChannel(int vMSecureChannel) {
		VMSecureChannel = vMSecureChannel;
	}

	/**
	 * @param returns the cdserver_port
	 */
	public static int getCDPort() {
		return cdserver_port;
	}

	/**
	 * @param cdserver_port the cdserver_port to set
	 */
	public static void setCdserver_port(int cdserver_port) {
		JViewer.cdserver_port = cdserver_port;
	}

	/**
	 * @param returns the hdserver_port
	 */
	public static int getHDPort() {
		return hdserver_port;
	}

	/**
	 * @param hdserver_port the hdserver_port to set
	 */
	public static void setHdserver_port(int hdserver_port) {
		JViewer.hdserver_port = hdserver_port;
	}

	public static boolean isKVMReconnectEnabled() {
		return isKVMReconnectEnabled;
	}

	public static void setKVMReconnectEnabled(boolean setKVMReconnectEnabled) {
		JViewer.isKVMReconnectEnabled = setKVMReconnectEnabled;
	}

	/**
	 * @return the retryCount
	 */
	public static int getRetryCount() {
		return retryCount;
	}

	/**
	 * @param retry_count the retry_count to set
	 */
	public static void setRetryCount(int retry_count) {
		JViewer.retryCount = retry_count;
	}

	/**
	 * @return the retryInterval
	 */
	public static int getRetryInterval() {
		return retryInterval;
	}

	/**
	 * @param retry_interval the retry_interval to set
	 */
	public static void setRetryInterval(int retry_interval) {
		JViewer.retryInterval = retry_interval * 1000;
	}

	/**
	 * @return the num_CD
	 */
	public static byte getNum_CD() {
		return Num_CD;
	}

	/**
	 * @return the num_HD
	 */
	public static byte getNum_HD() {
		return Num_HD;
	}

	/**
	 * @param num_CD the num_CD to set
	 */
	public static void setNum_CD(byte num_CD) {
		Num_CD = num_CD;
		if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_KVM_MAX_DEVICE_COUNT) !=
				JViewerApp.OEM_KVM_MAX_DEVICE_COUNT)
			VMApp.getInstance().setNumCD(num_CD);
	}

	/**
	 * @param num_HD the num_HD to set
	 */
	public static void setNum_HD(byte num_HD) {
		Num_HD = num_HD;
		if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_KVM_MAX_DEVICE_COUNT) !=
				JViewerApp.OEM_KVM_MAX_DEVICE_COUNT)
			VMApp.getInstance().setNumHD(num_HD);
	}

	public static int getCDState(){
		return CD_State;
	}
	/**
	 * @param cD_State the cD_State to set
	 */
	public static void setCD_State(int cD_State) {
		CD_State = cD_State;
	}

	/**
	 * @returns true is CD service is enabled and false otherwise.
	 */
	public static boolean isCdServiceEnabled() {
		return ((CD_State == MEDIA_SERVICE_ENABLED)? true: false);
	}

	public static int getHDState(){
		return HD_State;
	}

	/**
	 * @param hD_State the hD_State to set
	 */
	public static void setHD_State(int hD_State) {
		HD_State = hD_State;
	}

	/**
	 * @returns true is HD service is enabled and false otherwise.
	 */
	public static boolean isHdServiceEnabled() {
		return ((HD_State == MEDIA_SERVICE_ENABLED)? true: false);
	}

	/**
	 * @return the useSSL
	 */
	public static boolean isUseSSL() {
		return useSSL;
	}

	/**
	 * @param useSSL the useSSL to set
	 */
	public static void setUseSSL(boolean useSSL) {
		JViewer.useSSL = useSSL;
	}

	/**
	 * @return the vMUseSSL
	 */
	public static boolean isVMUseSSL() {
		return VMUseSSL;
	}

	/**
	 * @param vMUseSSL the vMUseSSL to set
	 */
	public static void setVMUseSSL(boolean vMUseSSL) {
		VMUseSSL = vMUseSSL;
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
		/* If mentioned language isn't available in supported locale list
		 * default language will be loaded */
		try{
			if( !getSupportedLocaleCodes().contains(language) ){
				Debug.out.println("Language isn't available: "+JViewer.getLanguage());
				JOptionPane.showMessageDialog(null, LocaleStrings.getString("AC_4_LS"),
						LocaleStrings.getString("AC_1_LS"), JOptionPane.ERROR_MESSAGE);

				// Sets the localization language to default
				language = JViewer.DEFAULT_LOCALE;

				/* For disabling language combo box under Standalone Application input dialog */
				setDefaultLang(true);
			}
		}catch(Exception e){
			Debug.out.printError(e);
			language = JViewer.DEFAULT_LOCALE;
		}

		JViewer.lang = language;
		LocaleStrings.setLanguageID(lang);
		/* For selecting opted language under StandAlone input dialog */
		StandAloneConnectionDialog.setSelectedLocale(lang);
		/* In case of a language is missing from the resource bundle,
		 * some swing components are not updated with proper localization.
		 * Hence to avoid that following line is required */
		JComponent.setDefaultLocale( new Locale(lang.toLowerCase()) );
	}

	/**
	 * @return the argLength
	 */
	public static int getArgLength() {
		return argLength;
	}

	/**
	 * @return the webPort
	 */
	public static int getWebPortNumber(String port) {
		int wPort;
		try {
			wPort = Integer.parseInt(port);
		}
		catch (NumberFormatException e) {
			wPort = INVALID_PORT;
			Debug.out.println("Invalid port number");
			Debug.out.println(e);
		}
		return wPort;
	}

	/**
	 * @param webPort the webPort to set
	 */
	public static void setWebPort(int webPort) {
		JViewer.webPort = webPort;
	}

	public static boolean isSinglePortEnabled() {
		return isSinglePortEnabled;
	}

	public static void setSinglePortEnabled(boolean isSinglePortEnabled) {
		JViewer.isSinglePortEnabled = isSinglePortEnabled;
	}
	public static void setApptype(String type)  {
		apptype = type;
	}
	public static boolean isWebPreviewer() {
		if(apptype != null) {
			if(apptype.compareToIgnoreCase(JViewer.APP_TYPE_WEB_PREVIEW) == 0)
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isBSODViewer() {
		if(apptype != null) {
			if(apptype.compareToIgnoreCase(JViewer.APP_TYPE_BSOD_VIEWER) == 0)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the keyboardLayout
	 */
	public static String getKeyboardLayout() {
		return keyboardLayout;
	}

	/**
	 * @param keyboardLayout the keyboardLayout to set
	 */
	public static void setKeyboardLayout(String keyboardLayout) {
		if(KEYBOARD_LAYOUTS.contains(keyboardLayout))
			JViewer.keyboardLayout = keyboardLayout;
		else
			JViewer.keyboardLayout = "AD";
	}

	public static String[] getVideoFile() {
		return videoFile;
	}

	public static void setVideoFile(String[] videoFile) {
		JViewer.videoFile = videoFile;
	}

	/**
	 * @return the kvmPrevilege
	 */
	public static int getKVMPrivilege() {
		return kvmPrivilege;
	}

	/**
	 * @param kvmPrivilege the kvmPrevilege to set
	 */
	public static void setKVMPrivilege(int kvmPrivilege) {
		JViewer.kvmPrivilege = kvmPrivilege;
	}

	/**
	 * Returns whether KVM privilege is enabled or not.
	 * @return true if KVM privilege is enabled, and false otherwise.
	 */
	public static boolean isKVMEnabled(){
		//if the 0th bit in kvm privilege is 1, then KVM is enabled
		return IsBitSet(kvmPrivilege,KVM_ENABLED);
	}

	/**
	 * Returns whether VMedia privilege is enabled or not.
	 * @return true if VMedia privilege is enabled, and false otherwise.
	 */
	public static boolean isVMediaEnabled(){
		//if the 1st bit in kvm previlege 1, then VMedia is enabled
		return IsBitSet(kvmPrivilege,VMEDIA_ENABLED);
	}

	/**
	 * Returns whether Power option privilege is enabled or not.
	 * @return true if Power option privilege is enabled, and false otherwise.
	 */
	public static boolean isPowerPrivEnabled(){
		//if the 8th bit in kvm previlege 1, then power privilege is enabled
		return IsBitSet(kvmPrivilege,POWER_OPTION_PRIV);
	}

	/**
	 * Get the title given to the application
	 */
	public static String getTitle(){
		return title;
	}

	/**
	 * Returns the current status denoting whether the OEM specifc features are enabled or disabled
	 */
	public static long getOEMFeatureStatus(){
		return OEMFeatureStatus;
	}

	/**
	 * @param oEMFeatureStatus the oEMFeatureStatus to set
	 */
	public static void setOEMFeatureStatus(long OEMFeatureStatus) {
		JViewer.OEMFeatureStatus = OEMFeatureStatus;
	}
	/**
	 * Returns the current status denoting whether the KVM has Valid License or not
	 */
	public static byte getKVMLicenseStatus(){
		return KVMLicenseStatus;
	}

	/**
	 * @param KVMLicenseStatus the KVMLicenseStatus to set
	 */
	public static void setKVMLicenseStatus(byte KVMLicenseStatus) {
		JViewer.KVMLicenseStatus = KVMLicenseStatus;
	}
	/**
	 * Returns the current status denoting whether the Media has Valid License or not
	 */
	public static byte getMediaLicenseStatus(){
		return MediaLicenseStatus;
	}

	/**
	 * @param MediaLicenseStatus the MediaLicenseStatus to set
	 */
	public static void setMediaLicenseStatus(byte MediaLicenseStatus) {
		JViewer.MediaLicenseStatus = MediaLicenseStatus;
	}

	/**
	 * @return the webSecure
	 */
	public static int getWebSecure() {
		return webSecure;
	}

	public static boolean isWebSecure() {
		return ((getWebSecure() == 1)? true : false);
	}
	/**
	 * @param webSecure the webSecure to set
	 */
	public static void setWebSecure(int webSecure) {
		JViewer.webSecure = webSecure;
	}

	public static void exit(int status){
		if(Debug.MODE == Debug.CREATE_LOG)
			Debug.out.closeLog();
		System.exit(status);
	}

	public static void setIsClientAdmin(boolean isClientAdmin) {
		JViewer.isClientAdmin = isClientAdmin;
	}

	public static boolean IsClientAdmin() {
		return isClientAdmin;
	}

	private static boolean isClientUserAdmin(){
		boolean ret = false;
		String osName = System.getProperty("os.name");
		String command = null;

		if (osName.startsWith("Windows")) {
			command = "reg query \"HKU\\S-1-5-19\"";
		}
		else if (osName.startsWith("Linux")) {
			String userName = System.getProperty("user.name");
			command = "id -u "+userName;
		}
		else if(osName.startsWith("Mac"))
			ret = true;

		if(command == null)
			return ret;

		try{
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();                            // Wait for for command to finish
			int exitValue = p.exitValue();          // If exit value 0, then admin user.
			if (osName.startsWith("Windows")) {
				if (0 == exitValue)
					ret = true;
			}
			else if ((osName.startsWith("Linux")) || (osName.startsWith("Mac"))) {
				BufferedReader in = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String line = null;
				while ((line = in.readLine()) != null) {

					if (line.equals("0"))
					{
						ret = true;
						break;
					}
				}
				in.close();
			}
		} catch (Exception e) {
			Debug.out.println("Not able find client user privilege");
		}
		return ret ;
	}

	public static byte getKVM_Num_CD() {
		return KVM_Num_CD;
	}

	public static void setKVM_Num_CD(byte kVM_Num_CD) {
		KVM_Num_CD = kVM_Num_CD;
		if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_KVM_MAX_DEVICE_COUNT) ==
				JViewerApp.OEM_KVM_MAX_DEVICE_COUNT)
			VMApp.getInstance().setNumCD(kVM_Num_CD);
	}

	public static byte getKVM_Num_HD() {
		return KVM_Num_HD;
	}

	public static void setKVM_Num_HD(byte kVM_Num_HD) {
		KVM_Num_HD = kVM_Num_HD;
		if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_KVM_MAX_DEVICE_COUNT) ==
				JViewerApp.OEM_KVM_MAX_DEVICE_COUNT)
			VMApp.getInstance().setNumHD(kVM_Num_HD);
	}

	/**
	 * @return the powerSaveMode
	 */
	public static byte getPowerSaveMode() {
		return powerSaveMode;
	}

	/**
	 * @param powerSaveMode the powerSaveMode to set
	 */
	public static void setPowerSaveMode(byte powerSaveMode) {
		JViewer.powerSaveMode = powerSaveMode;
	}

	/**
	 * Returns whether power save mode is enabled or not
	 * @return true if power save mode is enabled; false otherwise.
	 */
	public static boolean isPowerSaveModeEnabled(){
		return getPowerSaveMode() == 1 ? true : false;
	}
	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		JViewer.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		JViewer.password = password;
	}

	public static int getWebPort() {
		return webPort;
	}

	public static boolean isVMApp(){
		if(apptype != null) {
			if(apptype.compareToIgnoreCase(JViewer.APP_TYPE_VMAPP) == 0)
			{
				return true;
			}
		}
		return false;
	}

	public static int getKvmPort() {
		return kvmPort;
	}

	public static boolean isRestService() {
		return restService;
	}

	public static void setRestService(boolean restService) {
		JViewer.restService = restService;
	}

	/**
	 * Checks for the presence of unknown arguments
	 * @return true if unknown arguments found, false otherwise
	 */
	public static boolean isUnknownArgs() {
		return ( unknownParams.length() > 0 ? true : false );
	}
	/**
	 * Gets the SOC name corresponding to the SOC ID. 
	 * @param socID - The SOC ID.
	 * @return String that specifies the SOC name corresponding to the SOC ID. 
	 */
	public static String getSOC(int socID){
		//(socID - 1) is done to match the SOC ID with the index of SOC array,
		//which starts with 0
		return SOC[socID -1]; 
	}

	/**
	 * This method searches the com.ami.kvm.jviewer.oem.lang package and gets
	 * all the class file names that starts with Resource_ to find out
	 * all the resource files available for the various supported languages.
	 * The two letters available at the end of the resource file name will
	 * give the locale code(Eg: EN for English). All the supported locale
	 * will thus be identified and the supported locale codes will be returned
	 * as a String array.
	 *
	 * @return - a String array of supported locale codes.
	 */
	public static String[] getSupportedLocales(){

		/* Following table illustrates the logic behind the parsing. Based on resource bundle
		 * Availability for the given locale code either opted language or default language
		 * will be loaded.
		 * ===============================================================================
		 * | Common        | SOC           | OEM           | Result                      |
		 * ===============================================================================
		 * | Not Available | Available     | Available     | Opted lang will be Loaded   |
		 * -------------------------------------------------------------------------------
		 * | Available     | Not Available | Available     | Opted lang will be Loaded   |
		 * -------------------------------------------------------------------------------
		 * | Not Available                 | Available     | Opted lang will be Loaded   |
		 * -------------------------------------------------------------------------------
		 * | Available / Not Available     | Not Available | Default lang will be Loaded |
		 * -------------------------------------------------------------------------------
		 * | Not Available                                 | Default lang will be Loaded |
		 * -------------------------------------------------------------------------------
		 * | Available                                     | Opted lang will be Loaded   |
		 * ===============================================================================
		 * Note: When loading default language, the combo box (for language option) in StandAlone input
		 * dialog will not be disabled. */

		String[] supportedLocales = {"English - [EN]"};
		ArrayList<String> classNames = getClassesNames("com.ami.kvm.jviewer.oem.lang");
		if(!classNames.isEmpty()){
			Object[] classList = classNames.toArray();
			List localeList = new List();
			for(Object className : classList){
				//A language option will be added to the supported locale list only if,
				//the resource bundles of the specific language are found in the oem packages
				String fileName = (String) className;
				fileName = fileName.substring(fileName.indexOf("OEMResources"));
				//Ignoring the classes whose names do not start with Resource_
				if(fileName.startsWith("OEMResources_", (fileName.lastIndexOf('.')+1))){
					int beginIndex = fileName.lastIndexOf('_')+1;
					int endIndex = fileName.length();
					String localeCode = fileName.substring(beginIndex, endIndex);
					Locale locale = new Locale(localeCode.toLowerCase());
					/* The display language will be the same as locale code.
					 * Eg: English will be displayed as English,
					 * French will be displayed as francais */
					String language = locale.getDisplayLanguage(locale);
					localeList.add(language+JVMenu.LOCALE_CODE_START_DELIM+localeCode+JVMenu.LOCALE_CODE_END_DELIM);
				}
			}
			supportedLocales = localeList.getItems();
		}
		return supportedLocales;
	}

	/**
	 * Gets the list of locale codes of supported languages.
	 *
	 * @return - an arraylist of supported locale codes.
	 */
	public static ArrayList<String> getSupportedLocaleCodes(){
		String[] localeList = getSupportedLocales();
		ArrayList<String> supportedLocaleList= new ArrayList<String>();
		try {
			for(String locale : localeList){
				int startIndex = locale.indexOf(JVMenu.LOCALE_CODE_START_DELIM);
				int endIndex = locale.indexOf(JVMenu.LOCALE_CODE_END_DELIM);
				/* Eg: For english lanuage the entry will be like
				 * English - [EN]
				 * following code will be used to obtain locale code alone from the entry. (Eg: 'EN' for english language) */
				if( startIndex != -1 && endIndex != -1 )
				supportedLocaleList.add(locale.substring(startIndex + JVMenu.LOCALE_CODE_START_DELIM.length(), endIndex));
			}
			if(supportedLocaleList.isEmpty()){
				supportedLocaleList.add(JViewer.DEFAULT_LOCALE);
			}
		/* There is a possibility of NullPointerException and ArrayIndexOutOfBoundException occurrence
		*  So just wanna make sure of catching exceptions in that case */
		}catch(Exception e){
			Debug.out.printError(e);
			supportedLocaleList.add(JViewer.DEFAULT_LOCALE);
		}
		return supportedLocaleList;
	}

	/**
	 * Gets the list of the names of all the class files available in a given package.
	 *
	 * @param packageName - name of the package form which class names should be retrieved.
	 * @return - an array list of class names.
	 */
	public static ArrayList<String> getClassesNames(String packageName) {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			ArrayList<String> dirs = new ArrayList<String>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(resource.getFile());
			}
			TreeSet<String> classes = new TreeSet<String>();
			for (String directory : dirs) {
				classes.addAll(findClasses(directory, packageName));
			}
			ArrayList<String> classList = new ArrayList<String>();
			for (String clazz : classes) {
				classList.add(clazz);
			}
			return classList;
		}
		catch (IOException ie) {
			Debug.out.println(ie);
			return null;
		}
	}

	/**
	 * Recursive method used to find all classes in a given directory and sub directories.
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static TreeSet<String> findClasses(String directory, String packageName){
		TreeSet<String> classes = new TreeSet<String>();
		/*
		 * To find all the class names with in a given package, which is part of a jar file,
		 * the following code will be used. This code sequence will not work while debugging
		 * the code in Eclipse.
		 */
		if(Debug.MODE == Debug.RELEASE || Debug.MODE == Debug.CREATE_LOG){
			//if (directory.startsWith("file:") && directory.contains("!")) {
			if (directory.contains("!")) {
				String [] split = directory.split("!");
				URL jar = null;
				try {
					jar = new URL(split[0]);
				} catch (MalformedURLException e) {
					Debug.out.println(e);
				}
				ZipInputStream zip = null;
				try {
					zip = new ZipInputStream(jar.openStream());
				} catch (IOException e) {
					Debug.out.println(e);
				}
				ZipEntry entry = null;
				try {
					while ((entry = zip.getNextEntry()) != null) {
						if (entry.getName().endsWith(".class")) {
							String className = entry.getName().replaceAll("[$].*", "").replaceAll("[.]class", "").replace('/', '.');
							// Add classes which are inside the specified package alone.
							if(className.contains(packageName)) {
								classes.add(className);
							}
						}
					}
				} catch (IOException e) {
					Debug.out.println(e);
				}
			}
		}
		/*
		 * While debugging in Eclipse the Debug.Mode must be set as DEBUG. This is because the above code won't
		 * work wile debugging in Eclipse. To find all the classes in a package while debugging from Eclipse,
		 * the following code should be used.
		 */
		else if(Debug.MODE == Debug.DEBUG){
			if(System.getProperty("os.name").startsWith("Windows")){
				if(directory.startsWith("/"))
					directory = directory.substring(directory.indexOf('/')+1, directory.length());
			}
			//Replace %20 with space.
			if(directory.contains("%20")){
				if(System.getProperty("os.name").startsWith("Windows"))
					directory = directory.replaceAll("%20", " ");
				else
					directory = directory.replaceAll("%20", "\\ ");
			}
		}

		File dir = new File(directory);
		if (!dir.exists()) {
			return classes;
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file.getAbsolutePath(), packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
			}
		}
		return classes;
	}
	
	public static ClientConfig getClientCfg() {
		return clientCfg;
	}

	public static void setClientCfg(ClientConfig clientCfg) {
		JViewer.clientCfg = clientCfg;
	}

	/**
	 * @return the protocol
	 */
	public static String getProtocol() {
		return (webSecure == 1) ? "https" : "http";
	}

	public static boolean is64bit() {
		return System.getProperty("os.arch").contains("64");
	}
	
}
