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


package com.ami.kvm.jviewer.gui;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import com.ami.kvm.jviewer.ClientConfig;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.hid.KeyProcessor;
import com.ami.kvm.jviewer.hid.USBKeyProcessorAZERTY;
import com.ami.kvm.jviewer.hid.USBKeyProcessorEnglish;
import com.ami.kvm.jviewer.hid.USBKeyProcessorJapanese;
import com.ami.kvm.jviewer.hid.USBKeyProcessorJapaneseHost;
import com.ami.kvm.jviewer.hid.USBKeyProcessorQWERTY;
import com.ami.kvm.jviewer.hid.USBKeyProcessorQWERTZ;
import com.ami.kvm.jviewer.hid.USBKeyProcessorTurkishF;
import com.ami.kvm.jviewer.hid.USBKeyboardRep;
import com.ami.kvm.jviewer.kvmpkts.KVMClient;


public class AutoKeyboardLayout {

	public static final int  KBD_TYPE_AUTO				= 0;
	public static final int  KBD_TYPE_FRENCH 			= 1036;
	public static final int  KBD_TYPE_SPANISH 			= 1034;//0x40A
	public static final int  KBD_TYPE_GERMAN			= 1031;//0x407
	public static final int  KBD_TYPE_ENGLISH_US		= 1033;//409
	public static final int  KBD_TYPE_JAPANESE 			= 1041;//411
	public static final int  KBD_TYPE_ENGLISH_UK		= 2057;//809
	public static final int  KBD_TYPE_GERMAN_SWISS		= 2055;//807
	public static final int  KBD_TYPE_FRENCH_BELGIUM	= 2060;//80C
	public static final int  KBD_TYPE_ITALIAN			= 1040;//410
	public static final int  KBD_TYPE_DANISH			= 1030;//406
	public static final int  KBD_TYPE_FINNISH			= 1035;//40B
	public static final int  KBD_TYPE_NORWEGIAN			= 1044;//414
	public static final int  KBD_TYPE_PORTUGUESE		= 2070;//816
	public static final int  KBD_TYPE_SWEDISH			= 1053;//41D
	public static final int  KBD_TYPE_DUTCH_NL			= 1043;//413
	public static final int  KBD_TYPE_DUTCH_BE			= 2067;//813
	public static final int  KBD_TYPE_TURKISH_F			= 66591;//1041F
	public static final int  KBD_TYPE_TURKISH_Q			= 1055;//41F
	
	private KeyProcessor m_keyprocessor = null;
	private int keyboardType;
	public static  boolean DEAD_FLAG 		= false;
	public static  boolean SHIFT_FLAG 		= false;
	public static  int SHIFT_KEY_POSITION 	= 0;
	public static  boolean ALT_GR_FLAG 		= false;
	public static  boolean PLUS_FLAG 		= false;
	public static  boolean JAPANESE_FLAG 	= false;
	public HashMap<Integer, Integer> French_linuxMap;
	public HashMap<Integer, Integer> French_WinMap;
	public HashMap<Integer, Integer> French_Alt_gr_linuxMap;
	public HashMap<Integer, Integer> Spanish_Map;
	public HashMap<Integer, Integer> Spanish_Alt_gr_linuxMap;
	public HashMap<Integer, Integer> German_Map;
	public HashMap<Integer, Integer> German_Map_Alt_gr_linuxMap;
	public HashMap<Integer, Integer> German_Swiss_Map;
	public HashMap<Integer, Integer> French_Belgium_Map;
	private byte[] bdata;
	private int hostKeyboardType = KBD_TYPE_AUTO;

	public AutoKeyboardLayout() {
		get_keybd_type();
		getHostKeyboardType();
		ongetKeyprocessor();
		JViewerApp.getInstance().getM_USBKeyRep().setM_USBKeyProcessor(ongetKeyprocessor());
		French_linuxMap = new HashMap<Integer, Integer>();
		French_Alt_gr_linuxMap = new HashMap<Integer, Integer>();
		French_WinMap = new HashMap<Integer, Integer>();
		Spanish_Map = new HashMap<Integer, Integer>();
		Spanish_Alt_gr_linuxMap = new HashMap<Integer, Integer>();
		German_Map = new HashMap<Integer, Integer>();
		German_Map_Alt_gr_linuxMap = new HashMap<Integer, Integer>();
		German_Swiss_Map = new HashMap<Integer, Integer>();
		French_Belgium_Map = new HashMap<Integer, Integer>();
		French_WinMap.put(178,192);
		French_WinMap.put(249,222);
		French_WinMap.put(37,222);
		French_linuxMap.put(339,192);//first key
		French_linuxMap.put(233,50);//2
		French_linuxMap.put(50,50);//2
		French_linuxMap.put(34,51);//3
		French_linuxMap.put(51,51);//3
		French_linuxMap.put(232,55);//7
		French_linuxMap.put(55,55);//7
		French_linuxMap.put(231,57);//9
		French_linuxMap.put(57,57);//9
		French_linuxMap.put(224,48);//0
		French_linuxMap.put(48,48);//0
		French_linuxMap.put(249,52);
		French_linuxMap.put(37,52);
		French_Alt_gr_linuxMap.put(38,49);
		French_Alt_gr_linuxMap.put(126,50);
		French_Alt_gr_linuxMap.put(35,51);
		French_Alt_gr_linuxMap.put(123,KeyEvent.VK_4);//for alt + 4
		French_Alt_gr_linuxMap.put(91,53);
		French_Alt_gr_linuxMap.put(124,54);
		French_Alt_gr_linuxMap.put(96,55);
		French_Alt_gr_linuxMap.put(92,56);
		French_Alt_gr_linuxMap.put(94,57);
		French_Alt_gr_linuxMap.put(64,48);
		French_Alt_gr_linuxMap.put(93,522);
		French_Alt_gr_linuxMap.put(125,61);
		French_Alt_gr_linuxMap.put(164,515);
		French_Alt_gr_linuxMap.put(8364,KeyEvent.VK_E);
		Spanish_Map.put(170,192);
		Spanish_Map.put(186,192);
		Spanish_Map.put(92,192);
	    Spanish_Map.put(231,92);
		Spanish_Map.put(199,92);
		Spanish_Map.put(125,92);
		Spanish_Map.put(241,59);
		Spanish_Map.put(209,59);
		Spanish_Alt_gr_linuxMap.put(92,192);
		Spanish_Alt_gr_linuxMap.put(124,KeyEvent.VK_1);
		Spanish_Alt_gr_linuxMap.put(64,KeyEvent.VK_2);
		Spanish_Alt_gr_linuxMap.put(35,KeyEvent.VK_3);
		Spanish_Alt_gr_linuxMap.put(126,KeyEvent.VK_4);
		Spanish_Alt_gr_linuxMap.put(189,KeyEvent.VK_5);
		Spanish_Alt_gr_linuxMap.put(172,KeyEvent.VK_6);
		Spanish_Alt_gr_linuxMap.put(KeyEvent.VK_CLOSE_BRACKET,93);
		Spanish_Alt_gr_linuxMap.put(123,KeyEvent.VK_DEAD_ACUTE);
		Spanish_Alt_gr_linuxMap.put(91,KeyEvent.VK_OPEN_BRACKET);
		Spanish_Alt_gr_linuxMap.put(8364,KeyEvent.VK_5);
		Spanish_Alt_gr_linuxMap.put(125,92);//for alt + \
		German_Map.put(223,45);
		German_Map.put(63,45);
		German_Map.put(92,47);//VK_BACK_SLASH
		German_Map.put(252,91);
		German_Map.put(220,91);
		German_Map.put(246,59);
		German_Map.put(214,59);
		German_Map.put(228,222);
		German_Map.put(196,222);
		German_Map.put(KeyEvent.VK_DEAD_CIRCUMFLEX,192);
		German_Map_Alt_gr_linuxMap.put(178,50);
		German_Map_Alt_gr_linuxMap.put(179,51);
		German_Map_Alt_gr_linuxMap.put(123,55);
		German_Map_Alt_gr_linuxMap.put(91,56);
		German_Map_Alt_gr_linuxMap.put(93,57);
		German_Map_Alt_gr_linuxMap.put(125,48);
		German_Map_Alt_gr_linuxMap.put(126,KeyEvent.VK_PLUS);
		German_Map_Alt_gr_linuxMap.put(181,KeyEvent.VK_M);
		German_Map_Alt_gr_linuxMap.put(64,KeyEvent.VK_Q);
		German_Map_Alt_gr_linuxMap.put(8364,KeyEvent.VK_E);
		German_Map_Alt_gr_linuxMap.put(92,47);
		German_Map_Alt_gr_linuxMap.put(124,153);//102 key
		German_Swiss_Map.put(167, KeyEvent.VK_BACK_QUOTE);
		German_Swiss_Map.put(176, KeyEvent.VK_BACK_QUOTE);
		German_Swiss_Map.put(39, KeyEvent.VK_MINUS);
		German_Swiss_Map.put(63, KeyEvent.VK_MINUS);
		German_Swiss_Map.put(252, KeyEvent.VK_OPEN_BRACKET);
		German_Swiss_Map.put(232, KeyEvent.VK_OPEN_BRACKET);
		German_Swiss_Map.put(246, KeyEvent.VK_SEMICOLON);
		German_Swiss_Map.put(233, KeyEvent.VK_SEMICOLON);
		German_Swiss_Map.put(228, KeyEvent.VK_QUOTE);
		German_Swiss_Map.put(224, KeyEvent.VK_QUOTE);
		German_Swiss_Map.put(45, KeyEvent.VK_SLASH);
		German_Swiss_Map.put(95, KeyEvent.VK_SLASH);

		French_Belgium_Map.put(178, KeyEvent.VK_BACK_QUOTE);
		French_Belgium_Map.put(179, KeyEvent.VK_BACK_QUOTE);
		French_Belgium_Map.put(249, KeyEvent.VK_QUOTE);
		French_Belgium_Map.put(37, KeyEvent.VK_QUOTE);
		French_Belgium_Map.put(181, KeyEvent.VK_BACK_SLASH);
		French_Belgium_Map.put(163, KeyEvent.VK_BACK_SLASH);
	}

	private void get_keybd_type()
	{
		String keybdLayout = null;
		ClientConfig clientCfg = new ClientConfig();
		keybdLayout = clientCfg.ReadKeybdType();
		Debug.out.println("Keybdtype" + keybdLayout);
		String OS_name = System.getProperty("os.name");
		Debug.out.println("Os_name" + OS_name);
		if (OS_name.equalsIgnoreCase("Linux")) {
			if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_ENGLISH_US))
				keyboardType = KBD_TYPE_ENGLISH_US;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_FRENCH_FRANCE))
				keyboardType = KBD_TYPE_FRENCH;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_GERMAN_GER))
				keyboardType = KBD_TYPE_GERMAN;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_SPANISH))
				keyboardType = KBD_TYPE_SPANISH;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_JAPANESE))
				keyboardType = KBD_TYPE_JAPANESE;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_ENGLISH_UK))
				keyboardType = KBD_TYPE_ENGLISH_UK;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_GERMAN_SWISS))
				keyboardType = KBD_TYPE_GERMAN_SWISS;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_FRENCH_BELGIUM))
				keyboardType = KBD_TYPE_FRENCH_BELGIUM;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_ITALIAN))
				keyboardType = KBD_TYPE_ITALIAN;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_DANISH))
				keyboardType = KBD_TYPE_DANISH;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_FINNISH))
				keyboardType = KBD_TYPE_FINNISH;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_NORWEGIAN))
				keyboardType = KBD_TYPE_NORWEGIAN;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_PORTUGUESE))
				keyboardType = KBD_TYPE_PORTUGUESE;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_SWEDISH))
				keyboardType = KBD_TYPE_SWEDISH;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_DUTCH_NL))
				keyboardType = KBD_TYPE_DUTCH_NL;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_DUTCH_BE))
				keyboardType = KBD_TYPE_DUTCH_BE;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_TURKISH_F))
				keyboardType = KBD_TYPE_TURKISH_F;
			else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_TURKISH_Q))
				keyboardType = KBD_TYPE_TURKISH_Q;
			else
				keyboardType = KBD_TYPE_ENGLISH_US;
		} else {
			try {
				keybdLayout = keybdLayout.substring(keybdLayout.length() - 5,
						keybdLayout.length());
				keyboardType = Integer.parseInt(keybdLayout, 16);
	    		Debug.out.println("JViewerView.KBD_TYPE"+keyboardType);
			} catch (Exception e) {
				Debug.out.println("Unknown Language");
				Debug.out.println(e);
			}
		}

	}
	
	private void getHostKeyboardType(){
		String keybdLayout = JViewer.getKeyboardLayout();
		if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_ENGLISH_US))
			hostKeyboardType = KBD_TYPE_ENGLISH_US;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_FRENCH_FRANCE))
			hostKeyboardType = KBD_TYPE_FRENCH;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_GERMAN_GER))
			hostKeyboardType = KBD_TYPE_GERMAN;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_SPANISH))
			hostKeyboardType = KBD_TYPE_SPANISH;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_JAPANESE))
			hostKeyboardType = KBD_TYPE_JAPANESE;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_ENGLISH_UK))
			hostKeyboardType = KBD_TYPE_ENGLISH_UK;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_GERMAN_SWISS))
			hostKeyboardType = KBD_TYPE_GERMAN_SWISS;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_FRENCH_BELGIUM))
			hostKeyboardType = KBD_TYPE_FRENCH_BELGIUM;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_ITALIAN))
			hostKeyboardType = KBD_TYPE_ITALIAN;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_DANISH))
			hostKeyboardType = KBD_TYPE_DANISH;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_FINNISH))
			hostKeyboardType = KBD_TYPE_FINNISH;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_NORWEGIAN))
			hostKeyboardType = KBD_TYPE_NORWEGIAN;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_PORTUGUESE))
			hostKeyboardType = KBD_TYPE_PORTUGUESE;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_SWEDISH))
			hostKeyboardType = KBD_TYPE_SWEDISH;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_DUTCH_NL))
			hostKeyboardType = KBD_TYPE_DUTCH_NL;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_DUTCH_BE))
			hostKeyboardType = KBD_TYPE_DUTCH_BE;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_TURKISH_F))
			hostKeyboardType = KBD_TYPE_TURKISH_F;
		else if (keybdLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_TURKISH_Q))
			hostKeyboardType = KBD_TYPE_TURKISH_Q;
		else
			hostKeyboardType = KBD_TYPE_AUTO;
	}

	public int getKeyboardType() 
	{
		get_keybd_type();
		return keyboardType;
	}

	public void setKeyboardType(int keyboardType) 
	{
		if(this.keyboardType != keyboardType){
			if(keyboardType == KBD_TYPE_AUTO)
				get_keybd_type();
			else
				this.keyboardType = keyboardType;
			m_keyprocessor = null;
		}
	}

	public void setHostKeyboardType(int keyboardType) 
	{
		if(hostKeyboardType != keyboardType){
			hostKeyboardType = keyboardType;
			m_keyprocessor = null;
		}
	}
	public boolean OnkeyTyped(KeyEvent e) 
	{
		int keycode = 0;
		int ascii_value = e.getKeyChar();		
		
		switch(keyboardType)
		{
			case KBD_TYPE_SPANISH:
				if(e.getKeyLocation() != KeyEvent.KEY_LOCATION_NUMPAD)
					 keycode = getKeyboardKeycode(Spanish_Map, ascii_value, KBD_TYPE_SPANISH);
				
				if(e.getModifiersEx()== KeyEvent.ALT_GRAPH_DOWN_MASK) {
					keycode = getKeyboardKeycode(Spanish_Alt_gr_linuxMap, ascii_value, KBD_TYPE_SPANISH);
					OnSendALTGR_Keyevent(keycode);
				}
				else {
					OnSend_Keyevent(keycode);
					return true;
				}
				break;
			
			case KBD_TYPE_FRENCH:
				if(e.getModifiers() == KeyEvent.SHIFT_MASK)
					SHIFT_FLAG = true;

				if(System.getProperty("os.name").equals("Linux")) {
					if(e.getKeyLocation() != KeyEvent.KEY_LOCATION_NUMPAD)
						keycode = getKeyboardKeycode(French_linuxMap, ascii_value, KBD_TYPE_FRENCH);
					if(DEAD_FLAG && keycode < 0)
						DEAD_FLAG = false;
				}

				if(e.getKeyLocation() != KeyEvent.KEY_LOCATION_NUMPAD)
					keycode = getKeyboardKeycode(French_WinMap, ascii_value, KBD_TYPE_FRENCH);

				if(e.getModifiersEx()== KeyEvent.ALT_GRAPH_DOWN_MASK) {
					keycode = getKeyboardKeycode(French_Alt_gr_linuxMap, ascii_value, KBD_TYPE_FRENCH);
					OnSendALTGR_Keyevent(keycode);
				}
				else {
					if(keycode > 0){
						OnSend_Keyevent(keycode);
						return true;
					}
				}
				break;

			case KBD_TYPE_FRENCH_BELGIUM:
				if(e.getModifiers() == KeyEvent.SHIFT_MASK)
					SHIFT_FLAG = true;

				if(e.getKeyLocation() != KeyEvent.KEY_LOCATION_NUMPAD)
					keycode = getKeyboardKeycode(French_Belgium_Map, ascii_value, KBD_TYPE_FRENCH);

				if(e.getModifiersEx()== KeyEvent.ALT_GRAPH_DOWN_MASK) {
					keycode = getKeyboardKeycode(French_Alt_gr_linuxMap, ascii_value, KBD_TYPE_FRENCH);
					OnSendALTGR_Keyevent(keycode);
				}
				else {
					if(keycode > 0){
						OnSend_Keyevent(keycode);
						return true;
					}
				}
				break;

			case KBD_TYPE_GERMAN_SWISS:
				if(e.getModifiers() == KeyEvent.SHIFT_MASK)
					SHIFT_FLAG = true;

				if(e.getKeyLocation() != KeyEvent.KEY_LOCATION_NUMPAD)
					keycode = getKeyboardKeycode(German_Swiss_Map, ascii_value, KBD_TYPE_GERMAN_SWISS);

				if(e.getModifiersEx()== KeyEvent.ALT_GRAPH_DOWN_MASK) {
					keycode = getKeyboardKeycode(French_Alt_gr_linuxMap, ascii_value, KBD_TYPE_FRENCH);
					OnSendALTGR_Keyevent(keycode);
				}
				else {
					if(keycode > 0){
						OnSend_Keyevent(keycode);
						return true;
					}
				}
				break;
				
			case KBD_TYPE_GERMAN:
				if(ascii_value == 223 || ascii_value == 63) {
					 OnSend_Keyevent(47);
					 return true;
				}

				if(e.getModifiers() == KeyEvent.SHIFT_MASK)
					SHIFT_FLAG = true;

				if(e.getKeyLocation() != KeyEvent.KEY_LOCATION_NUMPAD){
					keycode = getKeyboardKeycode(German_Map, ascii_value, KBD_TYPE_GERMAN);
					if(DEAD_FLAG && keycode < 0)
						DEAD_FLAG = false;
				}
				
				if(e.getModifiersEx()== KeyEvent.ALT_GRAPH_DOWN_MASK) {
					keycode = getKeyboardKeycode(German_Map_Alt_gr_linuxMap, ascii_value, KBD_TYPE_GERMAN);
					OnSendALTGR_Keyevent(keycode);
				}
				else {
					if(keycode > 0){
						OnSend_Keyevent(keycode);
						return true;
					}
				}
				break;
		}

		return false;
	}


	private void OnSendALTGR_Keyevent(int keycode) {
		KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
		USBKeyboardRep m_USBKeyRep =  JViewerApp.getInstance().getM_USBKeyRep();
		m_USBKeyRep.set(KeyEvent.VK_CONTROL,KeyEvent.KEY_LOCATION_LEFT, false );
		kvmClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT, false);
		kvmClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT, true );
		kvmClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(keycode, KeyEvent.KEY_LOCATION_STANDARD, true );
		kvmClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(keycode, KeyEvent.KEY_LOCATION_STANDARD, false );
		kvmClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT, false);
		kvmClnt.sendKMMessage(m_USBKeyRep);
		return;
	}


	private void OnSend_Keyevent(int keycode) {
		KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
		USBKeyboardRep m_USBKeyRep =  JViewerApp.getInstance().getM_USBKeyRep();
		m_USBKeyRep.set(keycode, KeyEvent.KEY_LOCATION_STANDARD, true);
		kvmClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(keycode, KeyEvent.KEY_LOCATION_STANDARD, false );
		kvmClnt.sendKMMessage(m_USBKeyRep);
		return;
	}

	private void OnSendShiftGrave_Keyevent(int keycode,int keylocation) {
		KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
		USBKeyboardRep m_USBKeyRep =  JViewerApp.getInstance().getM_USBKeyRep();
		m_USBKeyRep.set(16, keylocation, true );
		kvmClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(keycode, KeyEvent.KEY_LOCATION_STANDARD, true );
		kvmClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(16, keylocation, false );
		kvmClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(keycode, KeyEvent.KEY_LOCATION_STANDARD, false );
		kvmClnt.sendKMMessage(m_USBKeyRep);
		return;
	}

	@SuppressWarnings("unchecked")
	public boolean OnkeyPressed(KeyEvent e) {

		KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
		USBKeyboardRep m_USBKeyRep =  JViewerApp.getInstance().getM_USBKeyRep();
		switch(keyboardType)
		{
			case KBD_TYPE_FRENCH:
			case KBD_TYPE_SPANISH:
			case KBD_TYPE_GERMAN:
				int keyModifiers = e.getModifiersEx();
				
				if( keyModifiers == KeyEvent.ALT_GRAPH_DOWN_MASK )
					return true;
				
				if( keyModifiers == 640 )
					return true;
				
				if(keyboardType == KBD_TYPE_GERMAN) {
					if(e.getKeyChar()== '~' ) {
						OnSend_Keyevent(61);
						return true;
					}
					
					if(e.getKeyCode()== 521 )
						PLUS_FLAG=true;
				}
				break;
				
			default:
				return false;
		}
		return false; // To satisfy compiler. What to do :-(
	}

	@SuppressWarnings("unchecked")
	public boolean OnkeyReleased(KeyEvent e) {
		KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
		USBKeyboardRep m_USBKeyRep =  JViewerApp.getInstance().getM_USBKeyRep();

		if( ( e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK ) == KeyEvent.ALT_DOWN_MASK ) {
			if((keyboardType == KBD_TYPE_ENGLISH_US ) && !JViewerApp.getInstance().isFullKeyboardEnabled()){ 
				return true;
			}
		}

		int keyCode = e.getKeyCode();
		
		switch(keyboardType)
		{
			case KBD_TYPE_GERMAN:
				if(System.getProperty("os.name").equals("Linux")) {
					if(keyCode == KeyEvent.VK_DEAD_CIRCUMFLEX && !SHIFT_FLAG) {
						OnSend_Keyevent(192);
						DEAD_FLAG = true;
						return true;
					}
					
					if(keyCode == 521 && !PLUS_FLAG) {
						if(e.getModifiers() == KeyEvent.ALT_GRAPH_MASK){
							OnSendALTGR_Keyevent(KeyEvent.VK_PLUS);
							PLUS_FLAG = false;
							return true;
						}
					}
					
					if(keyCode == KeyEvent.VK_DEAD_ACUTE && !SHIFT_FLAG) {
						OnSend_Keyevent(61);
						DEAD_FLAG = true;
						return true;
					}
					
					if(keyCode == KeyEvent.VK_DEAD_ACUTE && SHIFT_FLAG) {
						OnSendShiftGrave_Keyevent(61,SHIFT_KEY_POSITION);
						DEAD_FLAG = true;
						return true;
					}
					
					if(DEAD_FLAG ) {
						OnSend_Keyevent(keyCode);
						DEAD_FLAG = false;
						SHIFT_FLAG = false;
						return true;
					}
					
					if(keyCode == KeyEvent.VK_SHIFT) {
						SHIFT_FLAG=true;
						SHIFT_KEY_POSITION=e.getKeyLocation();
						DEAD_FLAG = false;
					}
					else {
						SHIFT_FLAG=false;
						DEAD_FLAG = false;
						SHIFT_KEY_POSITION=0;
					}
					
					PLUS_FLAG = false;
				}
				break;
			case KBD_TYPE_FRENCH:
				if(System.getProperty("os.name").equals("Linux")) {
					if(keyCode== KeyEvent.VK_DEAD_CIRCUMFLEX && !SHIFT_FLAG) {
						OnSend_Keyevent(91);
						DEAD_FLAG = true;
						return true;
					}
					
					if(keyCode== KeyEvent.VK_DEAD_CIRCUMFLEX && SHIFT_FLAG) {
						OnSendShiftGrave_Keyevent(91,SHIFT_KEY_POSITION);
						DEAD_FLAG = true;
						return true;
					}
					
					if(DEAD_FLAG ) {
						OnSend_Keyevent(keyCode);
						DEAD_FLAG = false;
						SHIFT_FLAG = false;
						SHIFT_KEY_POSITION=0;
						return true;
					}
					
					if(keyCode== KeyEvent.VK_SHIFT) {
						SHIFT_FLAG=true;
						SHIFT_KEY_POSITION=e.getKeyLocation();
						DEAD_FLAG = false;
					}
					else {
						SHIFT_FLAG=false;
						SHIFT_KEY_POSITION=0;
						DEAD_FLAG = false;
					}
				}
				break;
			case KBD_TYPE_SPANISH:
				if(System.getProperty("os.name").equals("Linux")) {
					if(keyCode== KeyEvent.VK_DEAD_ACUTE && !SHIFT_FLAG  && !ALT_GR_FLAG) {
						OnSend_Keyevent(KeyEvent.VK_DEAD_ACUTE);
						if(!DEAD_FLAG)
							DEAD_FLAG = true;
							else
								DEAD_FLAG = false;
						return true;
					}
					
					if(keyCode== KeyEvent.VK_DEAD_GRAVE && !SHIFT_FLAG  && !ALT_GR_FLAG) {
						OnSend_Keyevent(128);
						if(!DEAD_FLAG)
						DEAD_FLAG = true;
						else
							DEAD_FLAG = false;
						return true;
					}
					
					if(keyCode== KeyEvent.VK_DEAD_ACUTE && SHIFT_FLAG  && !ALT_GR_FLAG) {
						OnSendShiftGrave_Keyevent(KeyEvent.VK_DEAD_ACUTE,SHIFT_KEY_POSITION);
						if(!DEAD_FLAG)
						DEAD_FLAG = true;
						else
							DEAD_FLAG = false;
						return true;
					}
					
					if(keyCode== KeyEvent.VK_DEAD_GRAVE && SHIFT_FLAG && !ALT_GR_FLAG) {
						OnSendShiftGrave_Keyevent(128,SHIFT_KEY_POSITION);
						if(!DEAD_FLAG)
							DEAD_FLAG = true;
							else
								DEAD_FLAG = false;
						return true;
					}
					
					if(DEAD_FLAG && !ALT_GR_FLAG) {
						OnSend_Keyevent(keyCode);
						DEAD_FLAG = false;
						SHIFT_FLAG = false;
						SHIFT_KEY_POSITION=0;
						return true;
					}
					
					if(keyCode== KeyEvent.VK_SHIFT) {
						SHIFT_FLAG=true;
						SHIFT_KEY_POSITION=e.getKeyLocation();
						DEAD_FLAG = false;
						ALT_GR_FLAG = false;
					}
					else {
						SHIFT_FLAG=false;
						SHIFT_KEY_POSITION=0;
						DEAD_FLAG = false;
						ALT_GR_FLAG = false;
					}
				}
				break;
		}
		return false;
	}

	public KeyProcessor ongetKeyprocessor() {
		if(m_keyprocessor == null){
			if(JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.AUTOMATIC_LANGUAGE))
				m_keyprocessor = selectKeyProcessor(keyboardType);
			else
				m_keyprocessor = selectKeyProcessor(hostKeyboardType);
		}
		return m_keyprocessor;
		// TODO Auto-generated method stub

	}
	
	private KeyProcessor selectKeyProcessor(int keyboardType){
		KeyProcessor selectedKeyProcessor;
		switch(keyboardType)
		{
		case KBD_TYPE_JAPANESE:
			selectedKeyProcessor = new USBKeyProcessorJapaneseHost();
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_JAPANESE, true);
			break;
		case  KBD_TYPE_GERMAN:
			selectedKeyProcessor = new USBKeyProcessorQWERTZ(JVMenu.LANGUAGE_GERMAN_GER);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_GERMAN_GER, true);
			break;
		case KBD_TYPE_FRENCH:
			selectedKeyProcessor = new USBKeyProcessorAZERTY(JVMenu.LANGUAGE_FRENCH);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_FRENCH_FRANCE, true);
			break;
		case KBD_TYPE_SPANISH:
			selectedKeyProcessor = new USBKeyProcessorQWERTY(JVMenu.LANGUAGE_SPANISH);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_SPANISH, true);
			break;
		case KBD_TYPE_ENGLISH_UK:
			selectedKeyProcessor = new USBKeyProcessorQWERTY(JVMenu.LANGUAGE_ENGLISH_UK);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_ENGLISH_UK, true);
			break;
		case KBD_TYPE_GERMAN_SWISS:
			selectedKeyProcessor = new USBKeyProcessorQWERTZ(JVMenu.LANGUAGE_GERMAN_SWISS);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_GERMAN_SWISS, true);
			break;
		case KBD_TYPE_FRENCH_BELGIUM:
			selectedKeyProcessor = new USBKeyProcessorAZERTY(JVMenu.LANGUAGE_FRENCH_BELGIUM);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_FRENCH_BELGIUM, true);
			break;
		case KBD_TYPE_ITALIAN:
			selectedKeyProcessor = new USBKeyProcessorQWERTY(JVMenu.LANGUAGE_ITALIAN);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_ITALIAN, true);
			break;
		case KBD_TYPE_DANISH:
			selectedKeyProcessor = new USBKeyProcessorQWERTY(JVMenu.LANGUAGE_DANISH);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_DANISH, true);
			break;
		case KBD_TYPE_FINNISH:
			selectedKeyProcessor = new USBKeyProcessorQWERTY(JVMenu.LANGUAGE_FINNISH);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_FINNISH, true);
			break;
		case KBD_TYPE_NORWEGIAN:
			selectedKeyProcessor = new USBKeyProcessorQWERTY(JVMenu.LANGUAGE_NORWEGIAN_NOR);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_NORWEGIAN, true);
			break;
		case KBD_TYPE_PORTUGUESE:
			selectedKeyProcessor = new USBKeyProcessorQWERTY(JVMenu.LANGUAGE_PORTUGUESE);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_PORTUGUESE, true);
			break;
		case KBD_TYPE_SWEDISH:
			selectedKeyProcessor = new USBKeyProcessorQWERTY(JVMenu.LANGUAGE_SWEDISH);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_SWEDISH, true);
			break;
		case KBD_TYPE_DUTCH_NL:
			selectedKeyProcessor = new USBKeyProcessorQWERTY(JVMenu.LANGUAGE_DUTCH_NL);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_DUTCH_NL, true);
			break;
		case KBD_TYPE_DUTCH_BE:
			selectedKeyProcessor = new USBKeyProcessorAZERTY(JVMenu.LANGUAGE_DUTCH_BELGIUM);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_DUTCH_BE, true);
			break;
		case KBD_TYPE_TURKISH_F:
			selectedKeyProcessor = new USBKeyProcessorTurkishF();
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_TURKISH_F, true);
			break;
		case KBD_TYPE_TURKISH_Q:
			selectedKeyProcessor = new USBKeyProcessorQWERTY(JVMenu.LANGUAGE_TURKISH_Q);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_TURKISH_Q, true);
			break;
		default:
			if(JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.AUTOMATIC_LANGUAGE))
				selectedKeyProcessor = new USBKeyProcessorEnglish();
			else
				selectedKeyProcessor = new USBKeyProcessorQWERTY(JVMenu.LANGUAGE_ENGLISH_US);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.PKBRD_LANGUAGE_ENGLISH_US, true);
		}
		return selectedKeyProcessor;
	}
	
	/**
	 * Initialize the key processor object to null
	 */
	public void initKeyProcessor(){
		m_keyprocessor = null;
	}
	
	private int getKeyboardKeycode(HashMap<Integer, Integer> Key_Map, int ascii_value, int KeyBoardType)
	{
		try
		{
			return Key_Map.get(ascii_value);
		}catch(Exception e)
		{
			switch(KeyBoardType)
			{
				case KBD_TYPE_FRENCH:
					Debug.out.println("Exception in KBD_TYPE_FRENCH"+e);
					break;
				case KBD_TYPE_SPANISH:
					Debug.out.println("Exception in KBD_TYPE_SPANISH"+e);
					break;
				case KBD_TYPE_GERMAN:
					Debug.out.println("Exception in KBD_TYPE_GERMAN"+e);
					break;
				case KBD_TYPE_ENGLISH_US:
					Debug.out.println("Exception in KBD_TYPE_ENGLISH"+e);
					break;
				case KBD_TYPE_JAPANESE:
					Debug.out.println("Exception in KBD_TYPE_JAPANESE"+e);
					break;
			}
			return -1;
		}		
	}

}
