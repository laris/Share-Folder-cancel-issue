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
// JViewer menu component module.
//

package com.ami.kvm.jviewer.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.KeyStroke;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.common.oem.IOEMManager;
import com.ami.kvm.jviewer.hid.USBMouseRep;
import com.ami.kvm.jviewer.kvmpkts.CfgBandwidth;
import com.ami.kvm.jviewer.kvmpkts.KVMClient;
import com.ami.vmedia.VMApp;


/**
 * JViewer menu component class.
 */
public abstract class JVMenu {
	public static boolean INITIAL_MENU_STATUS = true;

	public static final String VIDEO = "Video";
	public static final String VIDEO_PAUSE_REDIRECTION = "VideoPauseRedirection";
	public static final String VIDEO_RESUME_REDIRECTION = "VideoResumeRedirection";
	public static final String VIDEO_REFRESH = "VideoRefresh";
	public static final String VIDEO_CAPTURE_SCREEN = "VideoCaptureScreen";
	public static final String VIDEO_HOST_DISPLAY_UNLOCK = "VideoHostDisplayUnlock";
	public static final String VIDEO_HOST_DISPLAY_LOCK = "VideoHostDisplayLock";
	public static final String VIDEO_FULL_SCREEN = "VideoFullScreen";
	public static final String VIDEO_EXIT = "VideoExit";

	public static final String KEYBOARD = "Keyboard";
	public static final String KEYBOARD_RIGHT_CTRL_KEY = "KeyboardHoldRightCtrlKey";
	public static final String KEYBOARD_RIGHT_ALT_KEY = "KeyboardHoldRightAltKey";
	public static final String KEYBOARD_LEFT_CTRL_KEY = "KeyboardHoldLeftCtrlKey";
	public static final String KEYBOARD_LEFT_ALT_KEY = "KeyboardHoldLeftAltKey";
	public static final String KEYBOARD_LEFT_WINKEY_PRESSHOLD = "KeyboardLeftWindowsKeyHoldDown";
	public static final String KEYBOARD_LEFT_WINKEY_PRESSRELEASE = "KeyboardLeftWindowsKeyPressRelease";
	public static final String KEYBOARD_RIGHT_WINKEY_PRESSHOLD = "KeyboardRightWindowsKeyHoldDown";
	public static final String KEYBOARD_RIGHT_WINKEY_PRESSRELEASE = "KeyboardRightWindowsKeyPressRelease";
	public static final String KEYBOARD_CTRL_ALT_DEL = "KeyboardCtrlAltDel";
	public static final String KEYBOARD_CONTEXT_MENU = "KeyboardContextMenu";
	public static final String KEYBOARD_HOTKEYS = "Hotkeys";
	public static final String KEYBOARD_ADD_HOTKEYS = "Add Hotkeys";
	public static final String KEYBOARD_FULL_KEYBOARD = "Full Keyborad Support";
	public static final String LINUX_HOST = "Linux host";
	public static final String WINDOWS_HOST = "Windows host";

	public static final String AUTOMATIC_LANGUAGE="Auto detect";
	public static final String KEYBOARD_LAYOUT = "Keyboard Layout";
	
	public static final String PHYSICAL_KEYBOARD = "PhysicalKeyboard";
	public static final String PKBRD_LANGUAGE_ENGLISH_US		= "US";
	public static final String PKBRD_LANGUAGE_ENGLISH_UK		= "GB";
	public static final String PKBRD_LANGUAGE_FRENCH_FRANCE		= "FR";
	public static final String PKBRD_LANGUAGE_FRENCH_BELGIUM	= "FR-BE";
	public static final String PKBRD_LANGUAGE_GERMAN_GER		= "DE";
	public static final String PKBRD_LANGUAGE_GERMAN_SWISS		= "DE-CH";
	public static final String PKBRD_LANGUAGE_JAPANESE			= "JP";
	public static final String PKBRD_LANGUAGE_SPANISH			= "ES";
	public static final String PKBRD_LANGUAGE_ITALIAN			= "IT";
	public static final String PKBRD_LANGUAGE_DANISH			= "DA";
	public static final String PKBRD_LANGUAGE_FINNISH			= "FI";
	public static final String PKBRD_LANGUAGE_NORWEGIAN			= "NO";
	public static final String PKBRD_LANGUAGE_PORTUGUESE		= "PT";
	public static final String PKBRD_LANGUAGE_SWEDISH			= "SV";
	public static final String PKBRD_LANGUAGE_DUTCH_NL			= "NL-NL";
	public static final String PKBRD_LANGUAGE_DUTCH_BE			= "NL-BE";
	public static final String PKBRD_LANGUAGE_TURKISH_F			= "TR_F";
	public static final String PKBRD_LANGUAGE_TURKISH_Q			= "TR_Q";
	public static final String PKBRD_NONE="NONE";

	//On adding new sofkeyboard command string should start with "SKBD_"
	public static final String SOFTKEYBOARD = "SoftKeyboard";
	public static final String SKBRD_LANGUAGE="SKBD_";
	public static final String SKBRD_LANGUAGE_ENGLISH_US="SKBD_English(United States)";
	public static final String SKBRD_LANGUAGE_ENGLISH_UK="SKBD_English(United Kingdom)";
	public static final String SKBRD_LANGUAGE_SPANISH="SKBD_Spanish";
	public static final String SKBRD_LANGUAGE_FRENCH="SKBD_French";
	public static final String SKBRD_LANGUAGE_GERMAN_GER="SKBD_German(Germany)";
	public static final String SKBRD_LANGUAGE_ITALIAN="SKBD_Italian";
	public static final String SKBRD_LANGUAGE_DANISH="SKBD_Danish";
	public static final String SKBRD_LANGUAGE_FINNISH="SKBD_Finnish";
	public static final String SKBRD_LANGUAGE_GERMAN_SWITZ="SKBD_German(Switzerland)";
	public static final String SKBRD_LANGUAGE_NORWEGIAN_NOR="SKBD_Norwegian(Norway)";
	public static final String SKBRD_LANGUAGE_PORTUGUESE="SKBD_Portuguese(Portugal)";
	public static final String SKBRD_LANGUAGE_SWEDISH="SKBD_Swedish";
	public static final String SKBRD_LANGUAGE_HEBREW="SKBD_Hebrew";
	public static final String SKBRD_LANGUAGE_FRENCH_BELGIUM="SKBD_French(Belgium)";
	public static final String SKBRD_LANGUAGE_DUTCH_BELGIUM="SKBD_Dutch(Belgium)";
	public static final String SKBRD_LANGUAGE_DUTCH_NL="SKBD_Dutch(Netherlands)";
	public static final String SKBRD_LANGUAGE_RUSSIAN="SKBD_Russian";
	public static final String SKBRD_LANGUAGE_JAPANESE_Q="SKBD_Japanese - Q";
	public static final String SKBRD_LANGUAGE_JAPANESE_H="SKBD_Japanese Hiragana";
	public static final String SKBRD_LANGUAGE_JAPANESE_K="SKBD_Japanese Katakana";
	public static final String SKBRD_LANGUAGE_TURKISH_F="SKBD_Turkish - F";
	public static final String SKBRD_LANGUAGE_TURKISH_Q="SKBD_Turkish - Q";
	public static final int LANGUAGE_ENGLISH_US 	= 0;
	public static final int LANGUAGE_ENGLISH_UK 	= 1;
	public static final int LANGUAGE_SPANISH 		= 2;
	public static final int LANGUAGE_FRENCH 		= 3;
	public static final int LANGUAGE_GERMAN_GER 	= 4;
	public static final int LANGUAGE_ITALIAN 		= 5;
	public static final int LANGUAGE_DANISH 		= 6;
	public static final int LANGUAGE_FINNISH 		= 7;
	public static final int LANGUAGE_GERMAN_SWISS	= 8;
	public static final int LANGUAGE_NORWEGIAN_NOR  = 9;
	public static final int LANGUAGE_PORTUGUESE		= 10;
	public static final int LANGUAGE_SWEDISH		= 11;
	public static final int LANGUAGE_HEBREW			= 12;
	public static final int LANGUAGE_FRENCH_BELGIUM	= 13;
	public static final int LANGUAGE_DUTCH_BELGIUM	= 14;
	public static final int LANGUAGE_RUSSIAN		= 15;
	public static final int LANGUAGE_JAPANESE_Q		= 16;
	public static final int LANGUAGE_TURKISH_F		= 17;
	public static final int LANGUAGE_TURKISH_Q		= 18;
	public static final int LANGUAGE_JAPANESE_H		= 19;
	public static final int LANGUAGE_JAPANESE_K		= 20;
	public static final int LANGUAGE_DUTCH_NL		= 21;
	
	public static final byte RELATIVE_MODE			= 1;
	public static final byte ABSOLUTE_MODE			= 2;
	public static final byte OTHER_MODE			= 3;

	public static final String OPTION = "Options";
	public static final String MOUSE = "Mouse";
	public static final String MOUSE_CLIENTCURSOR_CONTROL = "MouseShowCursor";
	public static final String CALIBRATEMOUSETHRESHOLD = "Calibrate Mouse Threshold";
	public static final String MOUSE_MODE ="MouseMode";
	public static final String MOUSE_RELATIVE_MODE = "Relative";
	public static final String MOUSE_ABSOLUTE_MODE = "Absolute";
	public static final String MOUSE_OTHER_MODE = "Other";

	public static final String OPTIONS_BANDWIDTH = "Bandwidth";
	public static final String OPTIONS_BANDWIDTH_AUTO_DETECT = "OptionsBandwidthAutoDetect";
	public static final String OPTIONS_BANDWIDTH_256KBPS = "OptionsBandwidth256Kbps";
	public static final String OPTIONS_BANDWIDTH_512KBPS = "OptionsBandwidth512Kbps";
	public static final String OPTIONS_BANDWIDTH_1MBPS = "OptionsBandwidth1Mbps";
	public static final String OPTIONS_BANDWIDTH_10MBPS = "OptionsBandwidth10Mbps";
	public static final String OPTIONS_BANDWIDTH_100MBPS = "OptionsBandwidth100Mbps";
	public static final String OPTIONS_KEYBOARD_MOUSE_ENCRYPTION = "OptionsKeyboardMouseEncryption";

	public static final String ZOOM = "Video Zoom";
	public static final String ZOOM_IN = "Zoom In";
	public static final String ZOOM_OUT = "Zoom Out";
	public static final String ACTUAL_SIZE = "ActualSize";
	public static final String FIT_TO_CLIENT_RES = "FitToClientResolution";
	public static final String FIT_TO_HOST_RES = "FitToHostResolution";
	public static final String ZOOM_OPTION_NONE = "ZoomOption None";

	public static final String OPTIONS_GUI_LANGUAGE = "GUI Languages";
	public static final String OPTIONS_GUI_LANGUAGE_LOCALE ="Locale";
	public static final String LOCALE_CODE_START_DELIM = " - [";
	public static final String LOCALE_CODE_END_DELIM = "]";
	public static final String OPTIONS_IPMI_COMMAND = "IPMICommand";
	public static final String OPTIONS_REQUEST_FULL_PERMISSION = "RequestFullPermission";
	public static final String OPTIONS_BLOCK_FULL_PERMISSION = "BlockFullPermission";
	public static final String OPTIONS_BLOCK_WITH_VIDEO_ONLY = "AllowonlyVideo";
	public static final String OPTIONS_BLOCK_WITH_DENY = "DenyAccess";
	
	public static final String MEDIA = "Media";
	public static final String DEVICE_MEDIA_DIALOG = "DeviceMediaDialog";

	public static final String HELP_ABOUT_RCONSOLE = "HelpAboutJViewer";

	public static final String VIDEO_RECORD = "VideoRecord";
    public static final String VIDEO_RECORD_SETTINGS = "VideoRecordSettings";
    public static final String VIDEO_RECORD_START = "VideoRecordStart";
    public static final String VIDEO_RECORD_STOP = "VideoRecordStop";
	
    public static final String POWER_CONTROL = "Power";
    public static final String POWER_RESET_SERVER = "Reset Server";
    public static final String POWER_OFF_IMMEDIATE = "Immediate Shutdown";
    public static final String POWER_OFF_ORDERLY = "Orderly Shutdown";
    public static final String POWER_ON_SERVER = "Power On Server";
    public static final String POWER_CYCLE_SERVER = "Power Cycle Server";
    
    public static final String ACTIVE_USERS = "Active Users";
	public JLabel menu_string;
    protected  JVMenuListener m_menuListener = new JVMenuListener();
    protected static JVMenuStatusListener m_menuStatus = new JVMenuStatusListener();
    protected   	 Hashtable<String, JMenuItem> m_menuItems ;
    protected  Hashtable<String, JMenu> m_menu;
    protected static Hashtable<String, Boolean> m_menuItems_setselected = new Hashtable<String, Boolean>();
    protected static Hashtable<String, Boolean> m_menuItems_setenabled = new Hashtable<String, Boolean>();
    protected static Hashtable<String, String> m_menustatusbar_text = new Hashtable<String, String>();
    protected static Hashtable<String, Character> menuMnemonics = new Hashtable<String, Character>();
    protected static Hashtable<String, KeyStroke> menuAccelerator = new Hashtable<String, KeyStroke>();
    public static String previous_bandwidth = JVMenu.OPTIONS_BANDWIDTH_100MBPS;

    // menu states
    public static int m_mouseMode = USBMouseRep.ABSOLUTE_MOUSE_MODE;
    protected static JComboBox combo;
    public JSlider slider;
    public JLabel label_size;
    private JLabel label_Text;
    public static double m_scale = 1.0;
    public static int keyBoardLayout = -1;
    public static int softkeyBoardLayout = -1;

	protected static String[] KVMPartialExceptionMenuItems = {VIDEO_PAUSE_REDIRECTION, VIDEO_RESUME_REDIRECTION,
								VIDEO_REFRESH, VIDEO_CAPTURE_SCREEN, VIDEO_FULL_SCREEN, VIDEO_EXIT, 
								OPTIONS_BANDWIDTH_AUTO_DETECT,OPTIONS_BANDWIDTH_256KBPS, 
								OPTIONS_BANDWIDTH_512KBPS, OPTIONS_BANDWIDTH_1MBPS, 
								OPTIONS_BANDWIDTH_10MBPS, OPTIONS_BANDWIDTH_100MBPS,
								ZOOM_IN, ZOOM_OUT, ACTUAL_SIZE, FIT_TO_CLIENT_RES, FIT_TO_HOST_RES,
								ZOOM_OPTION_NONE, VIDEO_RECORD_SETTINGS, VIDEO_RECORD_START,
								VIDEO_RECORD_STOP, HELP_ABOUT_RCONSOLE
								};
	
	protected static String[] KVMPartialExceptionSOCMenuItems;
	protected static String[] KVMPartialExceptionOEMMenuItems;
	private JMenu macroSubMenu;
    /**
     * The constructor.
     */
    public JVMenu() {
    	m_menuItems = new Hashtable<String, JMenuItem>();
    	m_menu = new Hashtable<String, JMenu>();

    }

    /**
     * Get menu item
     *
     * @param name item label
     */
    public JMenuItem getMenuItem(String name) {
    	return (JMenuItem)m_menuItems.get(name);
    }

    /**
     * Get menu item
     *
     * @param name item label
     */
    public JMenu getMenu(String name) {
    	return (JMenu)m_menu.get(name);
    }

    /**
     * Get Menu Enable item
     * This method is used to get the Menuitem Enable state
     *
     * @param name item label
     */
    public Boolean getMenuEnable(String name) {
    	return m_menuItems_setenabled.get(name);
    }

    /**
     * Set Menu Enable item
     * This method is used to Set the Menuitem Enable state
     *
     * @param name item label
     */
    public Boolean SetMenuEnable(String name,Boolean state) {
    	return m_menuItems_setenabled.put(name,state);
    }

    /**
     * Get Menu Selected  item(For checkbox menuitem)
     * This method is used to get the Menuitem slected state
     *
     * @param name item label
     */

    public Boolean getMenuSelected(String name) {
    	return m_menuItems_setselected.get(name);
    }

    /**
     * Set Menu Selected  item(For checkbox menuitem)
     * This method is used to Set the Menuitem selected state
     *
     * @param name item label
     */

    public Boolean SetMenuSelected(String name,Boolean state) {
    	return m_menuItems_setselected.put(name,state);
    }

    /**1
     * Notify the MenuItem Enable state
     * @param name - MEnuitem to be enabled
     * @param redir - state of the menuitem
     */

    public void notifyMenuStateEnable(String name,boolean redir) {
		if(getMenuItem(name) != null) {
		    	getMenuItem(name).setEnabled(redir);
		    	SetMenuEnable( name, redir);
		}
    }

    public void notifyMenuEnable(String name,boolean redir) {
    	getMenu(name).setEnabled(redir);
    	SetMenuEnable( name, redir);
    }
    /**
     * Notify the Menuitem selected state for
     * @param name
     * @param redir
     */

    public void notifyMenuStateSelected(String name,boolean redir) {
    	getMenuItem(name).setSelected(redir);
       	SetMenuSelected( name, redir);
    }

	/***
	* Based on the mouse mode enable or disable the calibearation Menu item
	* @param mousemode
	*/
	public void notifyMouseMode( byte mousemode) {

		if( mousemode == USBMouseRep.RELATIVE_MOUSE_MODE ) {
			notifyMenuStateEnable(CALIBRATEMOUSETHRESHOLD, true);
		} else {
			 notifyMenuStateEnable(CALIBRATEMOUSETHRESHOLD, false);
		}
	}

    /**
     * Refresh menu
     */
    public void refreshMenu() {

    		Set st = m_menuItems_setselected.entrySet();
    	    Iterator itr = st.iterator();
    	    Set st2 = m_menuItems_setenabled.entrySet();
    	    Iterator itr2 = st2.iterator();
    	    Object keyvalue = null ;
	    	Object state;
	    	boolean state_bool = false;

    	    while (itr.hasNext()) {
				  try {
					  Map.Entry me = (Map.Entry)itr.next();
					  keyvalue = me.getKey();
					  state = me.getValue();
					  state_bool =((Boolean) state).booleanValue();
				  }catch(Exception e) {
					  Debug.out.println(e);
				  }
				  notifyMenuStateSelected((String)keyvalue,  state_bool);
    	    }

    	    while (itr2.hasNext())
    	    {
    	    	try{
    	    	  	Map.Entry me = (Map.Entry)itr2.next();
    	    	  	keyvalue = me.getKey();
    	    	  	state = me.getValue();
    	    	  	state_bool =((Boolean) state).booleanValue();
    	    	  }catch(Exception e){
    	    		  Debug.out.println(e);
    	    	  }
    	    	  try{
    	    	  notifyMenuStateEnable((String)keyvalue,  state_bool);
    	    	  }catch(Exception e){
    	    		  notifyMenuEnable((String)keyvalue,  state_bool);
    	    		  Debug.out.println(e);
    	    	  }
    	    }
    }
    /**
     * Enable menu
     */
    public void enableMenu(String exceptMenu[],boolean enable, boolean updateMenuState) {
    	
	Set st = m_menuItems_setenabled.entrySet();
	Iterator itr = st.iterator();
	String keyvalue = null ;
	Object state;
	boolean state_bool = false;

	int index ,match =0;
	
	while (itr.hasNext())
	{
		try{
			Map.Entry me = (Map.Entry)itr.next();
			keyvalue = (String) me.getKey();
			state = me.getValue();
			state_bool =((Boolean) state).booleanValue();
		}catch(Exception e){
	    		Debug.out.println(e);
		}
		try{
			state_bool = enable;
			match=0;

			if(exceptMenu != null){
				for(index=0; index < exceptMenu.length; index++){
					if (keyvalue.equals(exceptMenu[index])) {
						match=1;
						break;	// once a match is found no need to iterate the loop further					
					}
				}
			}
			if(keyvalue.startsWith(OPTIONS_GUI_LANGUAGE_LOCALE))
				continue;

			if(keyvalue.startsWith(SKBRD_LANGUAGE)){
				if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_ALLOWED){
					notifyMenuStateEnable((String)keyvalue,  true);
					continue;
				}
			}

			if(1 != match ){
				if(updateMenuState)
					notifyMenuStateEnable((String)keyvalue,  state_bool);
				else if(getMenuItem((String)keyvalue) != null)
					getMenuItem((String)keyvalue).setEnabled(enable);
			}	
		}catch(Exception e){
			Debug.out.println(e);
			//notifyMenuEnable((String)keyvalue,  state_bool);
		}
	}
   }
	/*
	* Changes menu items text language
	*/
	public void changeMenuItemLanguage() {

		Set st = m_menuItems.entrySet();
		Iterator itr = st.iterator();
		Object keyvalue = null ;

		String key = null;
		String text = null;
		String stringStart = null;
		String stringEnd = null;

		JMenuItem menu;

		while (itr.hasNext())
		{
			try{
				Map.Entry me = (Map.Entry)itr.next();
				keyvalue = me.getKey();
				menu = JViewerApp.getInstance().getJVMenu().getMenuItem(keyvalue.toString());
				String pattern = LOCALE_CODE_START_DELIM;
				String menuString = menu.getText();
				//To Change Language name's language eg : English - [EN] 
				//We need to handle localization the menu items only in this case.
				//So we add a check to identify those menu items.
				if(menu.getActionCommand().startsWith(OPTIONS_GUI_LANGUAGE_LOCALE)){
					if(menuString.contains(pattern) && menuString.endsWith(JVMenu.LOCALE_CODE_END_DELIM)){
						//Retrieve locale code
						stringEnd = getLocaleCode(menuString);
						//check for 2 letter locale code
						if(stringEnd.length() == 2){
							//Localize the language name
							Locale locale = new Locale(stringEnd.toLowerCase());
							stringStart = locale.getDisplayLanguage(new Locale(JViewer.getLanguage()));
							key = stringStart.concat(pattern).concat(stringEnd).concat(JVMenu.LOCALE_CODE_END_DELIM);
							if(key != null){
								menu.setText(key);
							}
						}
					}
				}
				else{
					key = LocaleStrings.getStringKey(menu.getText());

					if(menu.equals(getMenuItem(HELP_ABOUT_RCONSOLE)))
						menu.setText(LocaleStrings.getString("F_69_JVM")+JViewer.getTitle());
					else if(key !=null){
						text = LocaleStrings.getString(key);
						if(text == null)
							text = LocaleStrings.getSOCString(key);
						menu.setText(text);
					}
				}
			}catch(Exception e){
				Debug.out.println(e);
			}
		}
	}
	/*
	* Changes menus text language
	*/
	public void changeMenuLanguage() {

		Set st = m_menu.entrySet();
		Iterator itr = st.iterator();
		Object keyvalue = null ;
		String text = null;

		String key = null;
		JMenu menu = null ;

		while (itr.hasNext())
		{
			try{
				Map.Entry me = (Map.Entry)itr.next();
				keyvalue = me.getKey();
				menu = JViewerApp.getInstance().getJVMenu().getMenu(keyvalue.toString());

				key = LocaleStrings.getStringKey(menu.getText());
				if(key !=null){
					text = LocaleStrings.getString(key);
					if(text == null)
						text = LocaleStrings.getSOCString(key);
					menu.setText(text);
				}
			}catch(Exception e){
				Debug.out.println(e);
			}
		}
	}
	/*
	* Changes status text language
	*/
	public void changeStatusBarLanguage() {

		Set st = m_menustatusbar_text.entrySet();
		Iterator itr = st.iterator();
		Object keyvalue = null ;
		Object value = null;

		String key = null;
		String text = null;
		String stringStart = null;
		String stringEnd = null;

		while (itr.hasNext())
		{
			try{
				Map.Entry me = (Map.Entry)itr.next();
				keyvalue = me.getKey();
				value = me.getValue();

				//To Change Language name's language eg : English - [EN]
				if(value.toString().startsWith(LocaleStrings.getPreviousLocaleString("F_119_JVM"))){
					//Retrive Language code
					stringEnd = getLocaleCode(value.toString());

					Locale locale = new Locale(stringEnd.toLowerCase());
					stringStart = locale.getDisplayLanguage(new Locale(JViewer.getLanguage()));
					key = LocaleStrings.getString("F_119_JVM")+stringStart+JVMenu.LOCALE_CODE_START_DELIM+
							stringEnd+JVMenu.LOCALE_CODE_END_DELIM;

					if(key != null){
						m_menustatusbar_text.put(keyvalue.toString(), key);
					}
				}
				else if(value.toString().startsWith(LocaleStrings.getPreviousLocaleString("F_75_JVM"))){
					text = localizeKeboardLayoutMenuStatus(value.toString());
					if(value.toString().endsWith(LocaleStrings.getPreviousLocaleString("F_76_JVM")))
						text += LocaleStrings.getString("F_76_JVM");
					else if(value.toString().endsWith(LocaleStrings.getPreviousLocaleString("F_116_JVM")))
						text += LocaleStrings.getString("F_116_JVM");
					m_menustatusbar_text.put(keyvalue.toString(), text);
					
				}
				else{
					key = LocaleStrings.getStringKey(value.toString());
					if(key !=null){
						text = LocaleStrings.getString(key);
						if(text == null)
							text = LocaleStrings.getSOCString(key);
						m_menustatusbar_text.put(keyvalue.toString(), text);
					}
				}
			}catch(Exception e){
				Debug.out.println(e);
			}
		}
	}

	/**
	 * Localize the keyboard layout menu item status bar string
	 * @param value
	 * @return localized string
	 */
	private String localizeKeboardLayoutMenuStatus(String value){
		String statusText = LocaleStrings.getString("F_75_JVM");

		if(value.contains(LocaleStrings.getPreviousLocaleString("F_77_JVM")))
			statusText += LocaleStrings.getString("F_77_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_78_JVM")))
			statusText += LocaleStrings.getString("F_78_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_79_JVM")))
			statusText += LocaleStrings.getString("F_79_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_80_JVM")))
			statusText += LocaleStrings.getString("F_80_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_81_JVM")))
			statusText += LocaleStrings.getString("F_81_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_82_JVM")))
			statusText += LocaleStrings.getString("F_82_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_83_JVM")))
			statusText += LocaleStrings.getString("F_83_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_84_JVM")))
			statusText += LocaleStrings.getString("F_84_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_85_JVM")))
			statusText += LocaleStrings.getString("F_85_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_86_JVM")))
			statusText += LocaleStrings.getString("F_86_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_87_JVM")))
			statusText += LocaleStrings.getString("F_87_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_88_JVM")))
			statusText += LocaleStrings.getString("F_88_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_89_JVM")))
			statusText += LocaleStrings.getString("F_89_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_90_JVM")))
			statusText += LocaleStrings.getString("F_90_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_91_JVM")))
			statusText += LocaleStrings.getString("F_91_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_92_JVM")))
			statusText += LocaleStrings.getString("F_92_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_93_JVM")))
			statusText += LocaleStrings.getString("F_93_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_94_JVM")))
			statusText += LocaleStrings.getString("F_94_JVM");
		else if(value.contains(LocaleStrings.getPreviousLocaleString("F_95_JVM")))
			statusText += LocaleStrings.getString("F_95_JVM");

		return statusText;
	}

    /*
     * Listener class for menu events.
     */
    private  class JVMenuListener implements ActionListener {

		/**
		 * Action event handler.
		 *
		 * @param e action event.
		 */
        public void actionPerformed(ActionEvent e) {

			if(JViewerApp.getOEMManager().getOEMJVMenu().handleMenuEvents(e)
					== IOEMManager.OEM_CUSTOMIZED){
				return;
			}
			String cmdStr = e.getActionCommand();
			JViewerApp RCApp = JViewerApp.getInstance();

			if (cmdStr.equals(VIDEO_PAUSE_REDIRECTION)) {
				RCApp.setM_userPause(true);
			    RCApp.OnVideoPauseRedirection();
			}
			else if (cmdStr.equals(VIDEO_RESUME_REDIRECTION)) {
			    RCApp.OnVideoResumeRedirection();
			}
			else if (cmdStr.equals(VIDEO_REFRESH))
			{
				RCApp.OnVideoRefreshRedirection();
			}
			else if (cmdStr.equals(VIDEO_HOST_DISPLAY_UNLOCK)) {
				RCApp.onSendHostLock(JViewerApp.HOST_DISPLAY_UNLOCK);
			}
			else if (cmdStr.equals(VIDEO_HOST_DISPLAY_LOCK)) {
				RCApp.onSendHostLock(JViewerApp.HOST_DISPLAY_LOCK);
			}
			else if (cmdStr.equals(VIDEO_CAPTURE_SCREEN))
			{
				RCApp.onVideoCaptureScreen();
			}
			else if (cmdStr.equals(VIDEO_FULL_SCREEN)) {
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();
			    RCApp.OnVideoFullScreen(evtSrc.getState());
			}
			else if (cmdStr.equals(VIDEO_EXIT)) {
			    RCApp.OnVideoExit();
			}
			else if (cmdStr.equals(KEYBOARD_RIGHT_CTRL_KEY)) {
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();
			    RCApp.OnKeyboardHoldRightCtrlKey(evtSrc.getState());
			}
			else if (cmdStr.equals(KEYBOARD_RIGHT_ALT_KEY)) {
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();
			    RCApp.OnKeyboardHoldRightAltKey(evtSrc.getState());
			}
			else if (cmdStr.equals(KEYBOARD_LEFT_CTRL_KEY)) {
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();
			    RCApp.OnKeyboardHoldLeftCtrlKey(evtSrc.getState());
			}
			else if (cmdStr.equals(KEYBOARD_LEFT_ALT_KEY)) {
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();
			    RCApp.OnKeyboardHoldLeftAltKey(evtSrc.getState());
			}
			else if (cmdStr.equals(KEYBOARD_LEFT_WINKEY_PRESSHOLD)) {
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();
			    RCApp.OnKeyboardLeftWindowsKeyHoldDown(evtSrc.getState());
			}
			else if (cmdStr.equals(KEYBOARD_LEFT_WINKEY_PRESSRELEASE)) {
			    RCApp.OnKeyboardLeftWindowsKeyPressRelease();
			}
			else if (cmdStr.equals(KEYBOARD_RIGHT_WINKEY_PRESSHOLD)) {
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();
			    RCApp.OnKeyboardRightWindowsKeyHoldDown(evtSrc.getState());
			}
			else if (cmdStr.equals(KEYBOARD_RIGHT_WINKEY_PRESSRELEASE)) {
			    RCApp.OnKeyboardRightWindowsKeyPressRelease();
			}
			else if (cmdStr.equals(KEYBOARD_CTRL_ALT_DEL)) {
				if((RCApp.getJVMenu().getMenuSelected(JVMenu.KEYBOARD_LEFT_CTRL_KEY) || RCApp.getJVMenu().getMenuSelected(JVMenu.KEYBOARD_RIGHT_CTRL_KEY ))
						&& (RCApp.getJVMenu().getMenuSelected(JVMenu.KEYBOARD_LEFT_ALT_KEY) || RCApp.getJVMenu().getMenuSelected(JVMenu.KEYBOARD_RIGHT_ALT_KEY)))
					return ;
				else
			    RCApp.OnKeyboardAltCtrlDel();
			}
			else if (cmdStr.equals(KEYBOARD_CONTEXT_MENU)) {
				  RCApp.OnKeyboardContextMenu();
			}
			else if(cmdStr.equals(KEYBOARD_FULL_KEYBOARD)){
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();
				SetMenuSelected(KEYBOARD_FULL_KEYBOARD, evtSrc.getState());
				JViewerApp.getInstance().setFullKeyboardEnabled(evtSrc.getState());
				enableMenuMnemonics(evtSrc.getState());
				enableMenuAccelerator(evtSrc.getState());
			}
			else if(cmdStr.equals(WINDOWS_HOST)){
				// need this block to match windows host action command, or else SOC_Menu_ActionMethod will be called.
			}
			else if(cmdStr.equals(LINUX_HOST)){
				// need this block to match linux host action command, or else SOC_Menu_ActionMethod will be called.
			}
			else if(cmdStr.equals(KEYBOARD_ADD_HOTKEYS)){
				RCApp.OnAddMacro();
			}
			else if (cmdStr.equals(MOUSE_CLIENTCURSOR_CONTROL)) {
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();
				RCApp.OnShowCursor(evtSrc.getState());
			}
			else if (cmdStr.equals(CALIBRATEMOUSETHRESHOLD)) {
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();
				if (JViewerApp.getInstance().GetRedirectionState() == JViewerApp.REDIR_STARTED)
					RCApp.OnCalibareteMouse(evtSrc.getState());
				else
					JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(CALIBRATEMOUSETHRESHOLD, false);
			}
			else if (cmdStr.equals(MOUSE_ABSOLUTE_MODE)) {
				if(m_mouseMode != ABSOLUTE_MODE)
					RCApp.OnSendMouseMode(ABSOLUTE_MODE);
			}
			else if (cmdStr.equals(MOUSE_RELATIVE_MODE)) {
				if(m_mouseMode != RELATIVE_MODE)
					RCApp.OnSendMouseMode(RELATIVE_MODE);
			}
			else if (cmdStr.equals(MOUSE_OTHER_MODE)) {
				if(m_mouseMode != OTHER_MODE)
					RCApp.OnSendMouseMode(OTHER_MODE);
			}
			else if (cmdStr.equals(OPTIONS_BANDWIDTH_AUTO_DETECT)) {
				RCApp.OnOptionsBandwidthAutoDetect();
			}
			else if (cmdStr.equals(OPTIONS_BANDWIDTH_256KBPS)) {
				RCApp.OnOptionsBandwidth(CfgBandwidth.BANDWIDTH_256KBPS);
			}
			else if (cmdStr.equals(OPTIONS_BANDWIDTH_512KBPS)) {
				RCApp.OnOptionsBandwidth(CfgBandwidth.BANDWIDTH_512KBPS);
			}
			else if (cmdStr.equals(OPTIONS_BANDWIDTH_1MBPS)) {
				RCApp.OnOptionsBandwidth(CfgBandwidth.BANDWIDTH_1MBPS);
			}
			else if (cmdStr.equals(OPTIONS_BANDWIDTH_10MBPS)) {
				RCApp.OnOptionsBandwidth(CfgBandwidth.BANDWIDTH_10MBPS);
			}
			else if (cmdStr.equals(OPTIONS_BANDWIDTH_100MBPS)) {
				RCApp.OnOptionsBandwidth(CfgBandwidth.BANDWIDTH_100MBPS);
			}
			else if (cmdStr.equals(OPTIONS_KEYBOARD_MOUSE_ENCRYPTION)) {
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();
			    RCApp.OnOptionsKeyboardMouseEncryption(evtSrc.getState());
			}
			else if (cmdStr.startsWith(OPTIONS_IPMI_COMMAND)){
				RCApp.invokeIPMICommandDialog();
			}
			else if (cmdStr.startsWith(OPTIONS_GUI_LANGUAGE_LOCALE)) {
				JMenuItem menu = (JMenuItem) e.getSource();
				String langCode = getLocaleCode(menu.getText());
				RCApp.OnGUILanguageChange(langCode);
			}
			else if(cmdStr.equals(OPTIONS_REQUEST_FULL_PERMISSION)){
				/* Disabling the menu item to prevent user from clicking
				** when the request is in already progress */
				JMenuItem menu = (JMenuItem) e.getSource();
				menu.setEnabled(false);
				RCApp.onSendFullPermissionRequest();
			}
			else if (cmdStr.equals(OPTIONS_BLOCK_WITH_VIDEO_ONLY)) {
				JCheckBoxMenuItem chbox_VideoOnly = (JCheckBoxMenuItem) e.getSource();
				// update the value in map with respective to current selection state of the video only checkbox menu
				SetMenuSelected(JVMenu.OPTIONS_BLOCK_WITH_VIDEO_ONLY, (chbox_VideoOnly.isSelected() ? true : false));
				// notify the deny checkbox menu for unselection.
				notifyMenuStateSelected(JVMenu.OPTIONS_BLOCK_WITH_DENY, false);
			}
			else if (cmdStr.equals(OPTIONS_BLOCK_WITH_DENY)) {
				JCheckBoxMenuItem chbox_Deny = (JCheckBoxMenuItem) e.getSource();
				// update the value in map with respective to current selection state of the deny checkbox menu
				SetMenuSelected(JVMenu.OPTIONS_BLOCK_WITH_DENY, (chbox_Deny.isSelected() ? true : false));
				// notify the video only checkbox menu for unselection.
				notifyMenuStateSelected(JVMenu.OPTIONS_BLOCK_WITH_VIDEO_ONLY, false);
			}
			else if (cmdStr.equals(HELP_ABOUT_RCONSOLE)) {
			    RCApp.OnHelpAboutJViewer();
			}
			else if (cmdStr.equals(DEVICE_MEDIA_DIALOG)) {
				Debug.out.println("MEDIA LICENSE STATUS : "+JViewer.getMediaLicenseStatus());
				if(JViewer.getMediaLicenseStatus() == JViewer.LICENSED)
				{				
					RCApp.OnvMedia(VMApp.CD_MEDIA);
				}
				else
				{
					InfoDialog.showDialog(JViewer.getMainFrame(), LocaleStrings.getString("F_136_JVM"),
							LocaleStrings.getString("2_4_KVMCLIENT"), InfoDialog.INFORMATION_DIALOG);
				}
			}
			else if (cmdStr.equals(AUTOMATIC_LANGUAGE)) {
				JCheckBoxMenuItem evtSrc = (JCheckBoxMenuItem) e.getSource();

				RCApp.onAutoKeyboardLayout(evtSrc.getState(),true);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_ENGLISH_US)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_ENGLISH_US);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_ENGLISH_UK)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_ENGLISH_UK);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_FRENCH_FRANCE)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_FRENCH_FRANCE);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_FRENCH_BELGIUM)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_FRENCH_BELGIUM);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_GERMAN_GER)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_GERMAN_GER);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_GERMAN_SWISS)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_GERMAN_SWISS);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_JAPANESE)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_JAPANESE);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_SPANISH)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_SPANISH);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_ITALIAN)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_ITALIAN);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_DANISH)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_DANISH);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_FINNISH)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_FINNISH);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_NORWEGIAN)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_NORWEGIAN);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_PORTUGUESE)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_PORTUGUESE);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_SWEDISH)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_SWEDISH);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_DUTCH_NL)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_DUTCH_NL);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_DUTCH_BE)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_DUTCH_BE);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_TURKISH_F)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_TURKISH_F);
			}
			else if(cmdStr.equals(PKBRD_LANGUAGE_TURKISH_Q)){
				JViewerApp.getInstance().setKeyProcessor(PKBRD_LANGUAGE_TURKISH_Q);
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_ENGLISH_US)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_ENGLISH_US);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_ENGLISH_US;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_ENGLISH_UK)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_ENGLISH_UK);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_ENGLISH_UK;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_SPANISH)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_SPANISH);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_SPANISH;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_FRENCH)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_FRENCH);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_FRENCH;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_GERMAN_GER)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_GERMAN_GER);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_GERMAN_GER;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_ITALIAN)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_ITALIAN);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_ITALIAN;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_DANISH)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_DANISH);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_DANISH;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_FINNISH)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_FINNISH);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_FINNISH;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_GERMAN_SWITZ)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_GERMAN_SWISS);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_GERMAN_SWISS;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_NORWEGIAN_NOR)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_NORWEGIAN_NOR);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_NORWEGIAN_NOR;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_PORTUGUESE)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_PORTUGUESE);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_PORTUGUESE;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_SWEDISH)) {

				RCApp.OnSkbrdDisplay(LANGUAGE_SWEDISH);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_SWEDISH;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_HEBREW)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_HEBREW);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_HEBREW;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_FRENCH_BELGIUM)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_FRENCH_BELGIUM);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_FRENCH_BELGIUM;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_DUTCH_BELGIUM)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_DUTCH_BELGIUM);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_DUTCH_BELGIUM;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_DUTCH_NL)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_DUTCH_NL);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_DUTCH_NL;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_RUSSIAN)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_RUSSIAN);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_RUSSIAN;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_JAPANESE_Q)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_JAPANESE_Q);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_JAPANESE_Q;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_JAPANESE_H)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_JAPANESE_H);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_JAPANESE_H;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_JAPANESE_K)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_JAPANESE_K);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_JAPANESE_K;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_TURKISH_F)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_TURKISH_F);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_TURKISH_F;
			}
			else if (cmdStr.equals(SKBRD_LANGUAGE_TURKISH_Q)) {
				RCApp.OnSkbrdDisplay(LANGUAGE_TURKISH_Q);
				softkeyBoardLayout = keyBoardLayout = LANGUAGE_TURKISH_Q;
			} else if (cmdStr.equals(VIDEO_RECORD_START)) {
				if (JViewerApp.getInstance().getM_videorecord() == null) {
					JViewerApp.getInstance().setM_videorecord(new VideoRecord());
					JViewerApp.getInstance().getM_videorecord().StoreLocation = JViewerApp.getInstance().VIDEO_RECORD_DEFAULT_PATH;
					VideoRecord.RecordStopTimer = JViewerApp.getInstance().VIDEO_RECORD_DEFAULT_TIME;
				}
				RCApp.getM_videorecord().OnVideoRecordStart();
			} else if (cmdStr.equals(VIDEO_RECORD_STOP)) {
				RCApp.getM_videorecord().OnVideoRecordStop();
			} else if (cmdStr.equals(VIDEO_RECORD_SETTINGS)) {
				RCApp.OnVideoRecordSettings();
			} else if (cmdStr.equals(ZOOM_IN)) {
				RCApp.OnVideoZoomIn();
			} else if (cmdStr.equals(ZOOM_OUT)) {
				RCApp.OnVideoZoomOut();
			} else if (cmdStr.equals(ACTUAL_SIZE) || cmdStr.equals(FIT_TO_CLIENT_RES) || cmdStr.equals(FIT_TO_HOST_RES)) {
				if (!cmdStr.equals(JViewerApp.getInstance().getZoomOption()))
					RCApp.onChangeZoomOptions(cmdStr);
			} else if (cmdStr.equals(POWER_RESET_SERVER)) {
				RCApp.onSendPowerControlCommand(POWER_RESET_SERVER);
			} else if (cmdStr.equals(POWER_OFF_IMMEDIATE)) {
				RCApp.onSendPowerControlCommand(POWER_OFF_IMMEDIATE);
			} else if (cmdStr.equals(POWER_OFF_ORDERLY)) {
				RCApp.onSendPowerControlCommand(POWER_OFF_ORDERLY);
			} else if (cmdStr.equals(POWER_ON_SERVER)) {
				RCApp.onSendPowerControlCommand(POWER_ON_SERVER);
			} else if (cmdStr.equals(POWER_CYCLE_SERVER)) {
				RCApp.onSendPowerControlCommand(POWER_CYCLE_SERVER);
			} else if (cmdStr.startsWith("HK_")) {
				Set set = JViewerApp.getInstance().getAddMacro().macroMap.entrySet();
				String menuString = cmdStr.substring(3);
				Iterator i = set.iterator();
				while (i.hasNext()) {
					Map.Entry me = (Map.Entry) i.next();
					String keystring = (String) me.getKey();
					if (menuString.equals(keystring)) {
						JViewerApp.getInstance().OnsendMacrokeycode((String) me.getValue());
					}
				}
			} else {
				RCApp.getSoc_App().SOC_Menu_ActionMethod(e);
			}
		}
	}

	/*
	 * Mouse listener for menu and Menu item.
	 */
	private static class JVMenuStatusListener extends MouseAdapter {

		/**
		 * Mouse enter event handler.
		 *
		 * @param e mouse enter event
		 */
		public void mouseEntered(MouseEvent e) {

			String menuBarLabel;
			try
			{
				JMenu evtSrcItem = (JMenu) e.getSource();
				menuBarLabel = evtSrcItem.getActionCommand();
			}catch(Exception ex)
			{
				Debug.out.println(e);
				menuBarLabel = ((JMenuItem) e.getSource()).getActionCommand();
			}

			JViewerApp RCApp = JViewerApp.getInstance();
			String test = (String)m_menustatusbar_text.get(menuBarLabel);
			RCApp.setStatus(test);
		}

		/**
		 * Mouse exit event handler.
		 *
		 * @param e mouse exit event.
		 */
		public void mouseExited(MouseEvent e) {
			JViewerApp.getInstance().resetStatus();
		}
	}

	protected JMenu createMenu(String menuName, String actionCommand, char mnemonic, String statusText){
		JMenu menu = new JMenu(menuName);
		if(mnemonic !=' '){
			menu.setMnemonic(mnemonic);
			menuMnemonics.put(actionCommand, mnemonic);
		}
		if(actionCommand != null && actionCommand.length() !=0){
			menu.setActionCommand(actionCommand);
			m_menu.put(actionCommand,menu);
			if(statusText != null){
				menu.addMouseListener(m_menuStatus);
				m_menustatusbar_text.put(actionCommand, statusText);
			}
		}
		return menu;
	}
	/*
	 * Construct video menu.
	 *
	 * return menu
	 */
	protected JMenuItem createMenuItem( String menuName, char mnemonic, int keyCode, int modifiers, 
			String actionCommand, String status)
	{
		JMenuItem menuItem = new JMenuItem(menuName);
		menuItem.addActionListener(m_menuListener);
		menuItem.addMouseListener(m_menuStatus);
		if(mnemonic != ' '){
			menuItem.setMnemonic(mnemonic);
			menuMnemonics.put(actionCommand, mnemonic);
		}
		if(keyCode !=0 && modifiers != 0){
			menuItem.setAccelerator(KeyStroke.getKeyStroke(keyCode,modifiers));
			menuAccelerator.put(actionCommand, KeyStroke.getKeyStroke(keyCode,modifiers));
		}
		menuItem.setActionCommand(actionCommand);
		m_menuItems.put(actionCommand, menuItem);
		m_menustatusbar_text.put(actionCommand, status);
		if(INITIAL_MENU_STATUS)
			m_menuItems_setenabled.put(actionCommand, true);
		return menuItem;
	}
	
	protected JMenuItem createMenuItem( String menuName, char mnemonic, String actionCommand, String status)
	{
		JMenuItem menuItem = new JMenuItem(menuName);
		menuItem.addActionListener(m_menuListener);
		menuItem.addMouseListener(m_menuStatus);
		if(mnemonic != ' '){
			menuItem.setMnemonic(mnemonic);
			menuMnemonics.put(actionCommand, mnemonic);
		}
		menuItem.setActionCommand(actionCommand);
		m_menuItems.put(actionCommand, menuItem);
		m_menustatusbar_text.put(actionCommand, status);
		if(INITIAL_MENU_STATUS)
			m_menuItems_setenabled.put(actionCommand, true);
		return menuItem;
	}
	
	protected JMenuItem createMenuItem( String menuName, char mnemonic, String actionCommand, String status, boolean state)
	{
		JMenuItem menuItem = new JMenuItem(menuName);
		menuItem.addActionListener(m_menuListener);
		menuItem.addMouseListener(m_menuStatus);
		if(mnemonic != ' '){
			menuItem.setMnemonic(mnemonic);
			menuMnemonics.put(actionCommand, mnemonic);
		}
		menuItem.setActionCommand(actionCommand);
		menuItem.setEnabled(state);
		m_menuItems.put(actionCommand, menuItem);
		m_menustatusbar_text.put(actionCommand, status);
		return menuItem;
	}
	
	protected JCheckBoxMenuItem  createCheckBoxMenuItem( String menuName, char mnemonic, int keyCode, int modifiers, 
			String actionCommand, String status)
	{
		JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem(menuName);
		checkBoxMenuItem.addActionListener(m_menuListener);
		checkBoxMenuItem.addMouseListener(m_menuStatus);
		if(mnemonic != ' '){
			checkBoxMenuItem.setMnemonic(mnemonic);
			menuMnemonics.put(actionCommand, mnemonic);
		}
		checkBoxMenuItem.setActionCommand(actionCommand);
		if(keyCode != 0 && modifiers != 0){
			checkBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(keyCode,modifiers));
			menuAccelerator.put(actionCommand, KeyStroke.getKeyStroke(keyCode,modifiers));
		}
		m_menuItems.put(actionCommand, checkBoxMenuItem);
		m_menustatusbar_text.put(actionCommand, status);
		if(INITIAL_MENU_STATUS)
		{
			m_menuItems_setselected.put(actionCommand, false);
			m_menuItems_setenabled.put(actionCommand, true);
		}
		
		return checkBoxMenuItem;
	}
	
	protected JCheckBoxMenuItem  createCheckBoxMenuItem( String menuName, char mnemonic, String actionCommand, String status)
	{
		JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem(menuName);
		checkBoxMenuItem.addActionListener(m_menuListener);
		checkBoxMenuItem.addMouseListener(m_menuStatus);
		if(mnemonic != ' '){
			checkBoxMenuItem.setMnemonic(mnemonic);
			menuMnemonics.put(actionCommand, mnemonic);
		}
		checkBoxMenuItem.setActionCommand(actionCommand);
		m_menuItems.put(actionCommand, checkBoxMenuItem);
		m_menustatusbar_text.put(actionCommand, status);
		if(INITIAL_MENU_STATUS)
		{
			m_menuItems_setselected.put(actionCommand, false);
			m_menuItems_setenabled.put(actionCommand, true);
		}
		
		return checkBoxMenuItem;
	}
	
	protected JRadioButtonMenuItem createRadioButtonMenu(String menuName,  String actionCommand, String status)
	{
		JRadioButtonMenuItem radioButtonMenu = new JRadioButtonMenuItem(menuName);
		radioButtonMenu.addActionListener(m_menuListener);
		radioButtonMenu.addMouseListener(m_menuStatus);
		radioButtonMenu.setActionCommand(actionCommand);
		m_menuItems.put(actionCommand, radioButtonMenu);
		m_menustatusbar_text.put(actionCommand, status);
		if(INITIAL_MENU_STATUS)
		{
			m_menuItems_setselected.put(actionCommand, false);
			m_menuItems_setenabled.put(actionCommand, true);
		}		
		return radioButtonMenu;		
	}
	
	
	protected JMenu constructVideoMenu() {

		JMenu menu;
		JMenuItem menuItem;
		menu = createMenu(LocaleStrings.getString("F_1_JVM"), VIDEO, 'V', LocaleStrings.getString("F_2_JVM"));

		// construct pause redirection menu item
		menuItem = createMenuItem(LocaleStrings.getString("F_3_JVM"), 'P', KeyEvent.VK_P, Event.ALT_MASK, VIDEO_PAUSE_REDIRECTION, LocaleStrings.getString("F_4_JVM"));
		menu.add(menuItem);
		
		menuItem = createMenuItem(LocaleStrings.getString("F_5_JVM"), 'R', KeyEvent.VK_R, Event.ALT_MASK, VIDEO_RESUME_REDIRECTION, LocaleStrings.getString("F_6_JVM"));
		menu.add(menuItem);

		menuItem = createMenuItem(LocaleStrings.getString("F_7_JVM"), 'e', KeyEvent.VK_E, Event.ALT_MASK, VIDEO_REFRESH, LocaleStrings.getString("F_8_JVM"));
		menu.add(menuItem);
		menuItem = createMenuItem(LocaleStrings.getString("F_134_JVM"), 'n', KeyEvent.VK_UNDEFINED, KeyEvent.VK_UNDEFINED, VIDEO_HOST_DISPLAY_UNLOCK, LocaleStrings.getString("F_135_JVM"));
		menu.add(menuItem);
		menuItem = createMenuItem(LocaleStrings.getString("F_137_JVM"), 'n', KeyEvent.VK_UNDEFINED, KeyEvent.VK_UNDEFINED, VIDEO_HOST_DISPLAY_LOCK, LocaleStrings.getString("F_138_JVM"));
		menu.add(menuItem);
		menuItem = createMenuItem(LocaleStrings.getString("F_121_JVM"), 'S', KeyEvent.VK_S, Event.ALT_MASK, VIDEO_CAPTURE_SCREEN, LocaleStrings.getString("F_121_JVM"));
		menu.add(menuItem);
		menu.addSeparator();

		// construct full screen menu item
		menuItem = createCheckBoxMenuItem(LocaleStrings.getString("F_9_JVM"), 'F', KeyEvent.VK_F, Event.ALT_MASK, VIDEO_FULL_SCREEN, LocaleStrings.getString("F_10_JVM"));
		if( JViewer.isStandalone() )
			menu.add(menuItem);
		menuItem.setEnabled(false);
		menu.addSeparator();
		
		// construct exit menu item
		menuItem = createMenuItem(LocaleStrings.getString("F_11_JVM"), ' ', VIDEO_EXIT, LocaleStrings.getString("F_12_JVM"));
		menu.add(menuItem);

		return menu;
	}

	/*
	 * Construct keyboard menu.
	 *
	 * return menu
	 */
	protected JMenu constructKeyboardMenu() {

		JMenu menu;
		JMenu subMenu;
		JMenuItem menuItem;
		ButtonGroup group;

		menu = createMenu(LocaleStrings.getString("F_13_JVM"), KEYBOARD, 'K', LocaleStrings.getString("F_14_JVM"));

		// construct hold right control key menu item
		menuItem = createCheckBoxMenuItem(LocaleStrings.getString("F_15_JVM"), 'C', KEYBOARD_RIGHT_CTRL_KEY, LocaleStrings.getString("F_18_JVM"));
		menu.add(menuItem);

		// construct hold right alt key menu item
		menuItem = createCheckBoxMenuItem(LocaleStrings.getString("F_16_JVM"), 'A', KEYBOARD_RIGHT_ALT_KEY, LocaleStrings.getString("F_19_JVM"));
		menu.add(menuItem);

		// construct hold left control key menu item
		menuItem = createCheckBoxMenuItem(LocaleStrings.getString("F_17_JVM"), 't', KEYBOARD_LEFT_CTRL_KEY, LocaleStrings.getString("F_20_JVM"));
		menuItem.setDisplayedMnemonicIndex(11);
		menu.add(menuItem);

		// construct hold left alt key menu item
		menuItem = createCheckBoxMenuItem(LocaleStrings.getString("F_120_JVM"), 'l', KEYBOARD_LEFT_ALT_KEY, LocaleStrings.getString("F_21_JVM"));
		menuItem.setDisplayedMnemonicIndex(11);
		menu.add(menuItem);
		menu.addSeparator();

		// construct left windows key submenu
		subMenu = createMenu(LocaleStrings.getString("F_22_JVM"), LocaleStrings.getString("F_22_JVM"), ' ', null);

		// construct hold down menu item
		menuItem = createCheckBoxMenuItem(LocaleStrings.getString("F_23_JVM"), ' ', KEYBOARD_LEFT_WINKEY_PRESSHOLD, LocaleStrings.getString("F_25_JVM"));
		subMenu.add(menuItem);

		// construct press and release menu item
		menuItem = createMenuItem(LocaleStrings.getString("F_24_JVM"), ' ',KEYBOARD_LEFT_WINKEY_PRESSRELEASE, LocaleStrings.getString("F_26_JVM"));
		subMenu.add(menuItem);
		menu.add(subMenu);

		// construct right windows key submenu
		subMenu = createMenu(LocaleStrings.getString("F_27_JVM"), LocaleStrings.getString("F_27_JVM"), ' ', null);

		// construct hold down menu item
		menuItem = createCheckBoxMenuItem(LocaleStrings.getString("F_23_JVM"), ' ', KEYBOARD_RIGHT_WINKEY_PRESSHOLD, LocaleStrings.getString("F_28_JVM"));
		subMenu.add(menuItem);

		// construct press and release menu item
		menuItem = createMenuItem(LocaleStrings.getString("F_24_JVM"), ' ',KEYBOARD_RIGHT_WINKEY_PRESSRELEASE, LocaleStrings.getString("F_29_JVM"));
		subMenu.add(menuItem);
		menu.add(subMenu);

		// construct alt + ctrl + del menu item
		menuItem = createMenuItem("Ctrl+Alt+Del", ' ',KEYBOARD_CTRL_ALT_DEL, LocaleStrings.getString("F_30_JVM"));
		menu.add(menuItem);

		menuItem = createMenuItem(LocaleStrings.getString("F_31_JVM"), ' ',KEYBOARD_CONTEXT_MENU, LocaleStrings.getString("F_32_JVM"));
		menu.add(menuItem);

		// construct Hotkeys submenu
		macroSubMenu = createMenu(LocaleStrings.getString("F_33_JVM"), LocaleStrings.getString("F_33_JVM"), ' ', null);

		//construct Add Hotkeys menuitem
		menuItem = createMenuItem(LocaleStrings.getString("F_34_JVM"), ' ',KEYBOARD_ADD_HOTKEYS, LocaleStrings.getString("F_35_JVM"));
		macroSubMenu.add(menuItem);
		menu.add(macroSubMenu);

		menu.addSeparator();
		//full keyboard support menuitem
		menuItem = createCheckBoxMenuItem(LocaleStrings.getString("F_130_JVM"), ' ', KEYBOARD_FULL_KEYBOARD, LocaleStrings.getString("F_131_JVM"));
		menu.add(menuItem);

		return menu;
	}

	/*
	 * Construct mouse menu.
	 *
	 * return menu.
	 */
	protected JMenu constructMouseMenu() {

		JMenu menu;
		JMenu subMenu;
		JMenuItem menuItem;
		ButtonGroup group;

		menu = createMenu(LocaleStrings.getString("F_36_JVM"), MOUSE, 'u', LocaleStrings.getString("F_114_JVM"));

		//construct sync cursor key menu item
		menuItem = createCheckBoxMenuItem( LocaleStrings.getString("F_37_JVM"), ' ', KeyEvent.VK_C, Event.ALT_MASK, MOUSE_CLIENTCURSOR_CONTROL,  LocaleStrings.getString("F_38_JVM"));
		menu.add(menuItem);

		menuItem = createCheckBoxMenuItem( LocaleStrings.getString("F_39_JVM"), ' ', KeyEvent.VK_T, Event.ALT_MASK, CALIBRATEMOUSETHRESHOLD, LocaleStrings.getString("F_40_JVM"));
		menu.add(menuItem);

		//Construct mouse mode menu to change the mouse mode from the JViewer 
		subMenu = createMenu(LocaleStrings.getString("F_41_JVM"), LocaleStrings.getString("F_41_JVM"), ' ', null);
		//Creating group radio menu item for relative and absolute mouse mode
		group = new ButtonGroup();
		// construct absolute mouse mode menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_42_JVM"), MOUSE_ABSOLUTE_MODE, LocaleStrings.getString("F_43_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct relative mouse mode menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_44_JVM"), MOUSE_RELATIVE_MODE, LocaleStrings.getString("F_45_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		
		//construct other mouse mode menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_46_JVM"), MOUSE_OTHER_MODE, LocaleStrings.getString("F_47_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		menu.add(subMenu);
		
		return menu;
	}

	/*
	 * Construct options menu.
	 *
	 * return menu.
	 */
	protected JMenu constructOptionsMenu() {

		JMenu menu;
		JMenu subMenu;
		JMenuItem menuItem;
		ButtonGroup group;

		menu = createMenu(LocaleStrings.getString("F_48_JVM"), OPTION, 'O', LocaleStrings.getString("F_49_JVM"));
		
		// construct bandwidth submenu
		subMenu = createMenu(LocaleStrings.getString("F_50_JVM"), null, ' ', null);
		m_menustatusbar_text.put(OPTIONS_BANDWIDTH, LocaleStrings.getString("F_51_JVM"));
		m_menu.put(OPTIONS_BANDWIDTH, subMenu);//Added the Bandwidth sub menu to the Hash table

		// construct auto detect menu item
		menuItem = createMenuItem(LocaleStrings.getString("F_52_JVM"), ' ',OPTIONS_BANDWIDTH_AUTO_DETECT, LocaleStrings.getString("F_53_JVM"));
		subMenu.add(menuItem);
		subMenu.addSeparator();

		group = new ButtonGroup();

		// construct 256 kbps menu item
		menuItem = createRadioButtonMenu("256 Kbps", OPTIONS_BANDWIDTH_256KBPS, LocaleStrings.getString("F_54_JVM")+" 256 Kbps "+LocaleStrings.getString("F_55_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct 512 kbps menu item
		menuItem = createRadioButtonMenu("512 Kbps", OPTIONS_BANDWIDTH_512KBPS, LocaleStrings.getString("F_54_JVM")+" 512 Kbps "+LocaleStrings.getString("F_55_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct 1 mbps menu item
		menuItem = createRadioButtonMenu("1 Mbps", OPTIONS_BANDWIDTH_1MBPS, LocaleStrings.getString("F_54_JVM")+" 1 Mbps "+LocaleStrings.getString("F_55_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct 10 mbps menu item
		menuItem = createRadioButtonMenu("10 Mbps", OPTIONS_BANDWIDTH_10MBPS, LocaleStrings.getString("F_54_JVM")+" 10 Mbps "+LocaleStrings.getString("F_55_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct 100 mbps menu item
		menuItem = createRadioButtonMenu("100 Mbps", OPTIONS_BANDWIDTH_100MBPS, LocaleStrings.getString("F_54_JVM")+" 100 Mbps "+LocaleStrings.getString("F_55_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		menu.add(subMenu);


		// construct keyboard encryption menu item 
		// This menu item need not be created in case where singleport app is connected via SSL socket
		if(!JViewer.isUseSSL() || !JViewer.isSinglePortEnabled()){
			menuItem = createCheckBoxMenuItem(LocaleStrings.getString("F_56_JVM"), ' ', OPTIONS_KEYBOARD_MOUSE_ENCRYPTION, LocaleStrings.getString("F_57_JVM"));
			menu.add(menuItem);
		}

//		 construct bandwidth submenu
		subMenu = createMenu(LocaleStrings.getString("F_58_JVM"), null, ' ', null);
		
		m_menustatusbar_text.put(ZOOM, LocaleStrings.getString("F_59_JVM"));
		m_menu.put(ZOOM, subMenu);//Added the Zoom sub menu to the Hash table

		menuItem = createMenuItem(LocaleStrings.getString("F_60_JVM"), ' ',ZOOM_IN, LocaleStrings.getString("F_61_JVM"));
		subMenu.add(menuItem);

		menuItem = createMenuItem(LocaleStrings.getString("F_62_JVM"), ' ',ZOOM_OUT, LocaleStrings.getString("F_63_JVM"));
		subMenu.add(menuItem);
		subMenu.addSeparator();
		ButtonGroup zoomGroup = new ButtonGroup();
		//Actual Size
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_122_JVM"), ACTUAL_SIZE, LocaleStrings.getString("F_123_JVM"));
		zoomGroup.add(menuItem);
		menuItem.setSelected(true);
		subMenu.add(menuItem);
		//Fit to Client Resolution
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_124_JVM"), FIT_TO_CLIENT_RES, LocaleStrings.getString("F_125_JVM"));
		zoomGroup.add(menuItem);
		subMenu.add(menuItem);
		//Fit to Host Resolution
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_126_JVM"), FIT_TO_HOST_RES, LocaleStrings.getString("F_127_JVM"));
		zoomGroup.add(menuItem);
		subMenu.add(menuItem);
		//None option
		//This menu item is added as a dummy menu item, which will be selected when the zoom
		//value is chaged to any value other then 100%. So this itm should not be added to the 
		//Zomm menu.
		menuItem = createRadioButtonMenu(ZOOM_OPTION_NONE, ZOOM_OPTION_NONE, ZOOM_OPTION_NONE);
		zoomGroup.add(menuItem);
		menu.add(subMenu);

		menu.addSeparator();
		//Construct Send IPMI Command menuitem
		menuItem = createMenuItem(LocaleStrings.getString("F_128_JVM"), ' ',OPTIONS_IPMI_COMMAND, LocaleStrings.getString("F_129_JVM"));
		menu.add(menuItem);
		menu.addSeparator();
		
		// construct GUI Language submenu
		subMenu = createMenu(LocaleStrings.getString("F_117_JVM"), null, ' ', null);
		m_menustatusbar_text.put(OPTIONS_GUI_LANGUAGE, LocaleStrings.getString("F_118_JVM"));
		m_menu.put(OPTIONS_GUI_LANGUAGE, subMenu);//Added the GUI Language sub menu to the Hash table

		group = new ButtonGroup();
		String language = null;
		//get supported locale languages to populate menu
		String[] newItems = JViewer.getSupportedLocales();

		for(int i =0;i<newItems.length ; i++){
			language = getLocaleCode(newItems[i]);
			menuItem = createRadioButtonMenu(newItems[i], OPTIONS_GUI_LANGUAGE_LOCALE + language, LocaleStrings.getString("F_119_JVM")+newItems[i]);
			group.add(menuItem);
			subMenu.add(menuItem);
		}

		notifyMenuStateSelected(OPTIONS_GUI_LANGUAGE_LOCALE + JViewer.getLanguage(), true);
		menu.add(subMenu);
		return menu;
	}

	protected JMenu constructDeviceRedirMenu() {

		JMenu menu;
		JMenuItem menuItem;

		menu = createMenu(LocaleStrings.getString("F_64_JVM"), MEDIA, 'd', LocaleStrings.getString("F_65_JVM"));

		menuItem = createMenuItem( LocaleStrings.getString("F_66_JVM"), 'V', DEVICE_MEDIA_DIALOG, LocaleStrings.getString("F_65_JVM"));
		menu.add(menuItem);

		return menu;
	}


	/*
	 * Construct help menu.
	 *
	 * return menu.
	 */
	protected JMenu constructHelpMenu() {

		JMenu menu;
		JMenuItem menuItem;

		menu = createMenu(LocaleStrings.getString("F_67_JVM"), HELP_ABOUT_RCONSOLE, 'H', LocaleStrings.getString("F_68_JVM"));

		// construct about jviewer menu item
		menuItem = createMenuItem(LocaleStrings.getString("F_69_JVM")+JViewer.getTitle(), ' ', KeyEvent.VK_F1, Event.CTRL_MASK, HELP_ABOUT_RCONSOLE, LocaleStrings.getString("F_68_JVM"));
		menu.add(menuItem);

		return menu;
	}

	protected JMenu constructKeyboardLayoutMenu()
	{
		JMenu menu,subMenu;
		JMenuItem menuItem, hostMenuItem;
		ButtonGroup group;

		menu = createMenu(LocaleStrings.getString("F_70_JVM"), KEYBOARD_LAYOUT, 'L', LocaleStrings.getString("F_71_JVM"));

		//construct auto keyboard layout menu item
		menuItem = createCheckBoxMenuItem(LocaleStrings.getString("F_72_JVM"), ' ', AUTOMATIC_LANGUAGE, LocaleStrings.getString("F_73_JVM"));
		menu.add(menuItem);

		// construct physical keyboard submenu
		subMenu = createMenu(LocaleStrings.getString("F_115_JVM"), null, ' ', null);
		m_menu.put(PHYSICAL_KEYBOARD,subMenu);
		m_menustatusbar_text.put(PHYSICAL_KEYBOARD, LocaleStrings.getString("F_115_JVM"));
		group = new ButtonGroup();
		if(INITIAL_MENU_STATUS)
		{
			m_menuItems_setenabled.put(PHYSICAL_KEYBOARD, true);
		}
			
		// for host platform
		hostMenuItem = createMenu(LocaleStrings.getString("F_147_JVM"), " ", ' ', LocaleStrings.getString("F_148_JVM"));
		// for windows host
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_143_JVM"), WINDOWS_HOST, LocaleStrings.getString("F_145_JVM"));
		group.add(menuItem);
		hostMenuItem.add(menuItem);
		menuItem.setSelected(true);
				
		// for linux host
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_144_JVM"), LINUX_HOST, LocaleStrings.getString("F_146_JVM"));
		group.add(menuItem);
		hostMenuItem.add(menuItem);
		subMenu.add(hostMenuItem);
		subMenu.addSeparator();
		
		group = new ButtonGroup();
		// construct English - US menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_77_JVM"), PKBRD_LANGUAGE_ENGLISH_US, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_77_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct English - UK menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_78_JVM"), PKBRD_LANGUAGE_ENGLISH_UK, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_78_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		
		// construct French menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_80_JVM"), PKBRD_LANGUAGE_FRENCH_FRANCE, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_80_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct French menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_90_JVM"), PKBRD_LANGUAGE_FRENCH_BELGIUM, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_90_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct German(Germany) menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_81_JVM"), PKBRD_LANGUAGE_GERMAN_GER, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_81_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		// construct German(Switzerland) menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_85_JVM"), PKBRD_LANGUAGE_GERMAN_SWISS, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_85_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		
		// construct Japanese menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_93_JVM"), PKBRD_LANGUAGE_JAPANESE, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_93_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		
		// construct Spanish menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_79_JVM"), PKBRD_LANGUAGE_SPANISH, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_79_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Italian menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_82_JVM"), PKBRD_LANGUAGE_ITALIAN, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_82_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Danish menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_83_JVM"), PKBRD_LANGUAGE_DANISH, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_83_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Finnish menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_84_JVM"), PKBRD_LANGUAGE_FINNISH, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_84_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Norwegian menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_86_JVM"), PKBRD_LANGUAGE_NORWEGIAN, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_86_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Portuguese menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_87_JVM"), PKBRD_LANGUAGE_PORTUGUESE, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_87_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		
		// construct Swedish menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_88_JVM"), PKBRD_LANGUAGE_SWEDISH, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_88_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Dutch-Netherland menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_142_JVM"), PKBRD_LANGUAGE_DUTCH_NL, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_142_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		
		// construct Dutch Belgium menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_91_JVM"), PKBRD_LANGUAGE_DUTCH_BE, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_91_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Turkish-F menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_94_JVM"), PKBRD_LANGUAGE_TURKISH_F, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_94_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Turkish-Q menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_95_JVM"), PKBRD_LANGUAGE_TURKISH_Q, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_95_JVM")+LocaleStrings.getString("F_116_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		//construct none layout
		//This menu item is added as a dummy menu item which will be selected when the auto detect keyboard layout is enabled
		menuItem = createRadioButtonMenu(PKBRD_NONE, PKBRD_NONE, PKBRD_NONE);
		group.add(menuItem);
		menu.add(subMenu);
		
		// construct soft keyboard submenu
		subMenu = createMenu(LocaleStrings.getString("F_74_JVM"), null, ' ', null);
		m_menu.put(SOFTKEYBOARD,subMenu);
		m_menustatusbar_text.put(SOFTKEYBOARD, LocaleStrings.getString("F_74_JVM"));
		group = new ButtonGroup();
		if(INITIAL_MENU_STATUS)
		{
			m_menuItems_setenabled.put(SOFTKEYBOARD, true);
		}

		// construct English - US menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_77_JVM"), SKBRD_LANGUAGE_ENGLISH_US, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_77_JVM")+LocaleStrings.getString("F_76_JVM"));
        group.add(menuItem);
		subMenu.add(menuItem);

		// construct English - Uk menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_78_JVM"), SKBRD_LANGUAGE_ENGLISH_UK, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_78_JVM")+LocaleStrings.getString("F_76_JVM"));
        group.add(menuItem);
		subMenu.add(menuItem);

		// construct Spanish menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_79_JVM"), SKBRD_LANGUAGE_SPANISH, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_79_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct French menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_80_JVM"), SKBRD_LANGUAGE_FRENCH, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_80_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct German(Germany) menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_81_JVM"), SKBRD_LANGUAGE_GERMAN_GER, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_81_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		// construct Italian menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_82_JVM"), SKBRD_LANGUAGE_ITALIAN, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_82_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Danish menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_83_JVM"), SKBRD_LANGUAGE_DANISH, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_83_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Finnish menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_84_JVM"), SKBRD_LANGUAGE_FINNISH, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_84_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct German(Switzerland) menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_85_JVM"), SKBRD_LANGUAGE_GERMAN_SWITZ, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_85_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Norwegian(Norway) menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_86_JVM"), SKBRD_LANGUAGE_NORWEGIAN_NOR, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_86_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Portuguese(Portugal) menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_87_JVM"), SKBRD_LANGUAGE_PORTUGUESE, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_87_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		// construct Swedish menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_88_JVM"), SKBRD_LANGUAGE_SWEDISH, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_88_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

//		 construct Hebrew menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_89_JVM"), SKBRD_LANGUAGE_HEBREW, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_89_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

//		 construct French Belgium menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_90_JVM"), SKBRD_LANGUAGE_FRENCH_BELGIUM, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_90_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

//		 construct Dutch Netherland menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_142_JVM"), SKBRD_LANGUAGE_DUTCH_NL, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_142_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

//		 construct Dutch Belgium menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_91_JVM"), SKBRD_LANGUAGE_DUTCH_BELGIUM, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_91_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

//		 construct Russian menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_92_JVM"), SKBRD_LANGUAGE_RUSSIAN, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_92_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

//		 construct Japanese menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_139_JVM"), SKBRD_LANGUAGE_JAPANESE_Q, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_139_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		
//		 construct Japanese Hiragana menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_140_JVM"), SKBRD_LANGUAGE_JAPANESE_H, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_140_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

//		 construct Japanese Katakana menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_141_JVM"), SKBRD_LANGUAGE_JAPANESE_K, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_141_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
//		 construct Turkish - F menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_94_JVM"), SKBRD_LANGUAGE_TURKISH_F, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_94_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);
		
//		 construct Turkish - Q menu item
		menuItem = createRadioButtonMenu(LocaleStrings.getString("F_95_JVM"), SKBRD_LANGUAGE_TURKISH_Q, LocaleStrings.getString("F_75_JVM")+LocaleStrings.getString("F_95_JVM")+LocaleStrings.getString("F_76_JVM"));
		group.add(menuItem);
		subMenu.add(menuItem);

		menu.add(subMenu);
		return menu;

}

    protected JMenu constructVideoRecordMenu() {
        JMenu menu;
        JMenuItem menuItem;

        menu = createMenu(LocaleStrings.getString("F_96_JVM"), VIDEO_RECORD, 'i', LocaleStrings.getString("F_97_JVM"));
		
		// construct Video Record Settings menu item
		// Status is not set as the menu is added in the m_menuItems_setenabled hash table for menu update
        menuItem = createMenuItem(LocaleStrings.getString("F_98_JVM"), ' ', VIDEO_RECORD_START, LocaleStrings.getString("F_99_JVM"));
        menu.add(menuItem);
        // construct Video Record Stop menu item
        menuItem = createMenuItem(LocaleStrings.getString("F_100_JVM"), ' ', VIDEO_RECORD_STOP, LocaleStrings.getString("F_101_JVM"), false);
        menu.add(menuItem);
        menu.addSeparator();
        // construct Video Record Settings menu item
        menuItem = createMenuItem(LocaleStrings.getString("F_102_JVM"), ' ', VIDEO_RECORD_SETTINGS, LocaleStrings.getString("F_103_JVM"));
        menu.add(menuItem);
        return menu;
}
    
    protected JMenu constructPowerMenu(){
    	JMenu menu;
        JMenuItem menuItem;

		menu = createMenu(LocaleStrings.getString("F_104_JVM"), POWER_CONTROL, 'w', LocaleStrings.getString("F_105_JVM"));
		
		// construct Reset Server menu item
        menuItem = createMenuItem(LocaleStrings.getString("F_106_JVM"), ' ', POWER_RESET_SERVER, LocaleStrings.getString("F_106_JVM"));       
        menu.add(menuItem);
                
		//construct immediate shutdown menu item
		menuItem = createMenuItem(LocaleStrings.getString("F_107_JVM"), ' ', POWER_OFF_IMMEDIATE, LocaleStrings.getString("F_107_JVM"));
        menu.add(menuItem);
        //construct orderly shutdown menu item
        menuItem = createMenuItem(LocaleStrings.getString("F_108_JVM"), ' ', POWER_OFF_ORDERLY, LocaleStrings.getString("F_108_JVM"));
        menu.add(menuItem);
                
        //construct power on menu item
        menuItem = createMenuItem(LocaleStrings.getString("F_109_JVM"), ' ', POWER_ON_SERVER, LocaleStrings.getString("F_109_JVM"));
        menu.add(menuItem);
                
        //construct power cycle menu item
        menuItem = createMenuItem(LocaleStrings.getString("F_110_JVM"), ' ', POWER_CYCLE_SERVER, LocaleStrings.getString("F_110_JVM"));
        menu.add(menuItem);
        
        return menu;
    	
    }
    protected JMenu constructUserMenu(){
    	
		JMenu menu;
		//create active user menu
		menu = createMenu(LocaleStrings.getString("F_111_JVM"), ACTIVE_USERS, 'A', LocaleStrings.getString("F_112_JVM"));
		
		int numUsers = KVMClient.getNumUsers();
		String[] userData = KVMClient.getUserData();
		if(userData != null && numUsers != 0){
			
			ImageIcon menuIcon;
			for (int i = 0;i <  numUsers;i++){
				menuIcon = null;
				String display = (userData[i].substring(userData[i].indexOf(":")+1, userData[i].length())).trim();
				String index = (userData[i].substring(0,userData[i].indexOf(":")-1)).trim();
				if(Integer.parseInt(index) == JViewerApp.getInstance().getCurrentSessionId()){
					URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/green.png");
					if(imageURL != null)
						menuIcon = new ImageIcon(imageURL);
				}
				menu.add(new JMenuItem(display,menuIcon));
			}
		}
    	return menu;
    	
    }

    /*
     * Creates createCustomActiveuserMenuItem with buttons
     */
    public JMenuItem createCustomActiveuserMenuItem(final int index,String label,Icon icon){

    	JMenuItem item = null;
    	if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_ALLOWED)
    		item = new JMenuItem(label+"           ",icon); 
    	else
    		item = new JMenuItem(label,icon); 
    	item.setLayout( new FlowLayout(FlowLayout.RIGHT, 5, 0) );


    	//Only master session can Kill other session
    	//If icon is valid that means its current session so no need to add Kill button
    	if((KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_ALLOWED) && (icon == null)){

    		//error.png is big insize so resize it. same png has been used in another place too
    		URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/error.png");
    		ImageIcon killIcon = new ImageIcon(imageURL);
    		Image img = killIcon.getImage();
    		Image newimg = img.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH);  
    		ImageIcon newIcon = new ImageIcon(newimg); 

    		JButton kill = new JButton(newIcon);
    		kill.setToolTipText(LocaleStrings.getString("H_19_KVMS"));
    		kill.setMargin(new Insets(0, 2, 0, 2) );
    		kill.setBorder(null);
    		item.add( kill );

    		kill.addMouseListener(new MouseAdapter() {
    			public void mousePressed(MouseEvent evt) {
    				JViewerApp.getInstance().OnTerminateActiveuser(index);
    			}

    		});
    	}

    	//for chat icon
    	//    	JButton chat = new JButton("chat");
    	//    	chat.setMargin(new Insets(0, 2, 0, 2) );
    	//    	item.add( chat );


    	return item;
    }

    /**
     * updates the number of menu items when users are added or removed
     */
    public void updateUserMenu(){
    	if(getMenu(ACTIVE_USERS)== null)
    		return;
    	JMenu menu = JViewerApp.getInstance().getM_frame().getMenu().getMenu(ACTIVE_USERS);
    	menu.removeAll();
    	int numUsers = KVMClient.getNumUsers();
		String[] userData = KVMClient.getUserData();
		ImageIcon menuIcon;		
		for (int i = 0;i <  numUsers;i++){
			menuIcon = null;
			String display = (userData[i].substring(userData[i].indexOf(":")+1, userData[i].length())).trim();
			String index = (userData[i].substring(0,userData[i].indexOf(":")-1)).trim();
			if(Integer.parseInt(index) == JViewerApp.getInstance().getCurrentSessionId()){
				URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/green.png");
				if(imageURL != null)
					menuIcon = new ImageIcon(imageURL);
			}

			menu.add(createCustomActiveuserMenuItem(Integer.parseInt(index),display,menuIcon));
		}
	}

	/**
	 * Enable/Disable the mnemonics associated with menus and menu items.
	 * 
	 * @param status
	 *            - enable mnemonics if true, disable mnemonics if false.
	 */
	protected void enableMenuMnemonics(boolean status) {
		Set menuSet = menuMnemonics.entrySet();
		Iterator itr = menuSet.iterator();
		Map.Entry menuMnemonicEntry;
		String menuString = null;
		Character mnemonic;

		while (itr.hasNext()) {
			menuMnemonicEntry = (Map.Entry) itr.next();
			menuString = (String) menuMnemonicEntry.getKey();
			mnemonic = (Character) menuMnemonicEntry.getValue();
			if (status) {
				if (getMenu(menuString) != null) {
					getMenu(menuString).setMnemonic('\0');
				} else if (getMenuItem(menuString) != null) {
					getMenuItem(menuString).setMnemonic('\0');
				}
			} else {
				if (getMenu(menuString) != null) {
					getMenu(menuString).setMnemonic(mnemonic.charValue());
				} else if (getMenuItem(menuString) != null) {
					getMenuItem(menuString).setMnemonic(mnemonic.charValue());
				}
			}
		}
	}

	/**
	 * Enable/Disable the accelerators associated with menus and menu items.
	 * 
	 * @param status
	 *            - enable menu accelerators if true, disable menu accelerators
	 *            if false.
	 */
	protected void enableMenuAccelerator(boolean status) {
		Set menuSet = menuAccelerator.entrySet();
		Iterator itr = menuSet.iterator();
		Map.Entry menuAccelerartorEntry;
		String menuItemString;
		KeyStroke accelerator;

		while (itr.hasNext()) {
			menuAccelerartorEntry = (Map.Entry) itr.next();
			menuItemString = (String) menuAccelerartorEntry.getKey();
			accelerator = (KeyStroke) menuAccelerartorEntry.getValue();
			if (status) {
				if (getMenuItem(menuItemString) != null) {
					getMenuItem(menuItemString).setAccelerator(null);
				}
			} else {
				if (getMenuItem(menuItemString) != null) {
					getMenuItem(menuItemString).setAccelerator(accelerator);
				}
			}
		}
	}
    protected JLabel constructZoomLabelText(){
        label_Text = new JLabel(LocaleStrings.getString("F_113_JVM")+"100%");
        return label_Text;
    }

	/**
	 * Add Request Full Permission menu item to the Options menu
	 */
	protected void addFullPermissionMenuItem(){
		JMenu menu;
		JMenuItem menuItem;
		if(getMenuItem(JVMenu.OPTIONS_REQUEST_FULL_PERMISSION) == null){
			menu = getMenu(OPTION);
			menuItem = createMenuItem(LocaleStrings.getString("F_132_JVM"), ' ',OPTIONS_REQUEST_FULL_PERMISSION, LocaleStrings.getString("F_133_JVM"));
			menu.add(menuItem);
		}
	}

	/**
	 * Remove Request Full Permission menu item from the Options menu
	 */
	public void removeFullPermissionMenuItem() {
		JViewerApp RCApp = JViewerApp.getInstance();

		if (getMenuItem(JVMenu.OPTIONS_REQUEST_FULL_PERMISSION) != null) {
			JMenuItem menuItem = getMenuItem(JVMenu.OPTIONS_REQUEST_FULL_PERMISSION);
			getMenu(JVMenu.OPTION).remove(menuItem);
			m_menuItems.remove(JVMenu.OPTIONS_REQUEST_FULL_PERMISSION);
		}
		RCApp.setFullPermissionRequest(false);
	}

	/**
	 * Add Block Permission menu item to the Options menu
	 */
	protected void addBlockPermissionMenuItem(){
		JMenu parent = getMenu(OPTION); // Option menu
		JMenu menu = getMenu(OPTIONS_BLOCK_FULL_PERMISSION);
		// create the menu only if not available already
		if(menu == null){
			JCheckBoxMenuItem blockPermission_deny, blockPermission_partial;
			
			menu = /* Block Permission Menu */createMenu(LocaleStrings.getString("F_149_JVM"), OPTIONS_BLOCK_FULL_PERMISSION,' ', LocaleStrings.getString("F_150_JVM"));
			parent.add(menu);

			blockPermission_partial = /* Allow Only Video checkbox menu */ createCheckBoxMenuItem(LocaleStrings.getString("H_2_KVMS"), ' ', OPTIONS_BLOCK_WITH_VIDEO_ONLY, LocaleStrings.getString("H_2_KVMS"));
			blockPermission_deny = /* Deny Permisiion checkbox menu */ createCheckBoxMenuItem(LocaleStrings.getString("H_3_KVMS"), ' ', OPTIONS_BLOCK_WITH_DENY, LocaleStrings.getString("H_3_KVMS"));

			menu.add(blockPermission_partial);
			menu.add(blockPermission_deny);
			// By default none of the options will be selected.
			// if following line of were removed then KVM sharing response dialog won't
			// appear properly in master session.
			notifyMenuStateSelected(JVMenu.OPTIONS_BLOCK_WITH_VIDEO_ONLY, false);
			notifyMenuStateSelected(JVMenu.OPTIONS_BLOCK_WITH_DENY, false);
		} else {
			menu.setVisible(true);
		}
	}

	/**
	 * Remove Block Permission menu item from the Options menu
	 */
	public void removeBlockPermissionMenuItem() {
		JMenu menu = getMenu(OPTIONS_BLOCK_FULL_PERMISSION);
		// Remove the menu only if it's not removed already
		if (menu != null) {
			// clear the selection state otherwise KVM sharing won't work properly
			// when receiving from permission from master session.
			notifyMenuStateSelected(JVMenu.OPTIONS_BLOCK_WITH_VIDEO_ONLY, false);
			notifyMenuStateSelected(JVMenu.OPTIONS_BLOCK_WITH_DENY, false);
			menu.setVisible(false); // hide the menu
		}
	}

    public void setZoomLabelText(String text){
    	label_Text.setText(LocaleStrings.getString("F_113_JVM")+text);
    }
	public Hashtable<String, JMenuItem> getM_menuItems() {
		return m_menuItems;
	}

	public void setM_menuItems(Hashtable<String, JMenuItem> items) {
		m_menuItems = items;
	}

	protected JLabel constructString() {
		menu_string = null;
		menu_string = new JLabel();
		menu_string.setVisible(true);
		return menu_string;
	}


	protected int RemoveString() {
		menu_string = null;
		return 0;
	}

	public Hashtable<String, JMenu> getM_menu() {
		return m_menu;
	}

	public void setM_menu(Hashtable<String, JMenu> m_menu) {
		this.m_menu = m_menu;
	}

	/**
	 * @return the menuMnemonics
	 */
	public static Hashtable<String, Character> getMenuMnemonics() {
		return menuMnemonics;
	}

	/**
	 * @return the menuAccelerator
	 */
	public static Hashtable<String, KeyStroke> getMenuAccelerator() {
		return menuAccelerator;
	}

	public JMenu getMacroSubMenu() {
		return macroSubMenu;
	}

	public void setMacroSubMenu(JMenu macroSubMenu) {
		this.macroSubMenu = macroSubMenu;
	}
	
	/**
	 * Used to find the index of a particular menu item in a menu.
	 * @param component
	 * @return component index
	 */
	public static int getComponentIndex(Component component) {
		int index = -1;
		Container c = null;
		if ((component != null) && ((c = component.getParent()) != null)) {
			for (index = 0; index < c.getComponentCount(); index++) {
				if (c.getComponent(index) == component)
					return index;
			}
		} else {
			Debug.out.println("getComponentIndex() : component/component.getParent() is null");
		}
		return index;
	}

	/**
	 * Gets the locale code from the end of an input menu name String.
	 * The menu name string follows the format like English - [EN] 
	 * @param menuString
	 * @return the locale code.
	 */
	private String getLocaleCode(String menuString){
		String pattern = LOCALE_CODE_START_DELIM;
		int startIndex = menuString.indexOf(pattern)+pattern.length();
		int endIndex = menuString.indexOf(JVMenu.LOCALE_CODE_END_DELIM);
		String language = menuString.substring(startIndex, endIndex);
		return language;
	}
}
