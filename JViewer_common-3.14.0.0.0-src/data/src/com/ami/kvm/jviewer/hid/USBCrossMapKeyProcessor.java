/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2014, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
****************************************************************/

package com.ami.kvm.jviewer.hid;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.gui.JVMenu;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;

public class USBCrossMapKeyProcessor extends USBKeyProcessorEnglish {
	boolean shiftModified = false;
	boolean altGrModified = false;
	boolean leftCTRLModified = false;
	boolean leftCTRLHeldDown = false;
	boolean rightCTRLModified = false;
	boolean rightCTRLHeldDown = false;
	boolean altGrHeldDown = false;
	boolean shiftHeldDown = false;
	boolean autoKeyBreakMode = false;
	byte[] usbKeyRepPkt = null;
	private int pressedKeyCode = KeyEvent.VK_UNDEFINED;
	private char pressedKeyChar = NULL_CHAR;
	protected boolean autoKeybreakModeOn = false;
	protected HashMap<Integer, Integer> localKeyCodeMap;
	protected int hostLayoutIndex = -1;
	protected final int commonKeys[] = { KeyEvent.VK_ESCAPE, KeyEvent.VK_F1,
			KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4, KeyEvent.VK_F5,
			KeyEvent.VK_F6, KeyEvent.VK_F7, KeyEvent.VK_F8, KeyEvent.VK_F9,
			KeyEvent.VK_F10, KeyEvent.VK_F11, KeyEvent.VK_F12, KeyEvent.VK_F13,
			KeyEvent.VK_F14, KeyEvent.VK_F15, KeyEvent.VK_F16, KeyEvent.VK_F17,
			KeyEvent.VK_F18, KeyEvent.VK_F19, KeyEvent.VK_F20, KeyEvent.VK_F21,
			KeyEvent.VK_F23, KeyEvent.VK_F24, KeyEvent.VK_TAB,
			KeyEvent.VK_CAPS_LOCK, KeyEvent.VK_SHIFT, KeyEvent.VK_CONTROL,
			KeyEvent.VK_ALT, KeyEvent.VK_SPACE, KeyEvent.VK_ALT_GRAPH,
			KeyEvent.VK_WINDOWS, KeyEvent.VK_CONTEXT_MENU, KeyEvent.VK_ENTER,
			KeyEvent.VK_BACK_SPACE, KeyEvent.VK_PRINTSCREEN,
			KeyEvent.VK_SCROLL_LOCK, KeyEvent.VK_PAUSE, KeyEvent.VK_INSERT,
			KeyEvent.VK_HOME, KeyEvent.VK_PAGE_UP, KeyEvent.VK_DELETE,
			KeyEvent.VK_END, KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_NUM_LOCK,
			KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT,
			KeyEvent.VK_RIGHT, KeyEvent.VK_NUMPAD0, KeyEvent.VK_NUMPAD1,
			KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3, KeyEvent.VK_NUMPAD4,
			KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD7,
			KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9, KeyEvent.VK_CONVERT,
			KeyEvent.VK_NONCONVERT, KeyEvent.VK_ALPHANUMERIC, KeyEvent.VK_KATAKANA,
			KeyEvent.VK_HIRAGANA, KeyEvent.VK_FULL_WIDTH, KeyEvent.VK_HALF_WIDTH,
			KeyEvent.VK_INPUT_METHOD_ON_OFF };
	protected final HashMap<Integer, Integer> keyCodeMap;
	public USBCrossMapKeyProcessor(){
		super();
		keyCodeMap = new HashMap<Integer, Integer>(){{
			put(0, KeyEvent.VK_BACK_QUOTE);
			put(1, KeyEvent.VK_1);
			put(2, KeyEvent.VK_2);
			put(3, KeyEvent.VK_3);
			put(4, KeyEvent.VK_4);
			put(5, KeyEvent.VK_5);
			put(6, KeyEvent.VK_6);
			put(7, KeyEvent.VK_7);
			put(8, KeyEvent.VK_8);
			put(9, KeyEvent.VK_9);
			put(10, KeyEvent.VK_0);
			put(11, KeyEvent.VK_MINUS);
			put(12, KeyEvent.VK_EQUALS);
			put(13, KeyEvent.VK_Q);
			put(14, KeyEvent.VK_W);
			put(15, KeyEvent.VK_E);
			put(16, KeyEvent.VK_R);
			put(17, KeyEvent.VK_T);
			put(18, KeyEvent.VK_Y);
			put(19, KeyEvent.VK_U);
			put(20, KeyEvent.VK_I);
			put(21, KeyEvent.VK_O);
			put(22, KeyEvent.VK_P);
			put(23, KeyEvent.VK_OPEN_BRACKET);
			put(24, KeyEvent.VK_CLOSE_BRACKET);
			put(25, KeyEvent.VK_BACK_SLASH);
			put(26, KeyEvent.VK_A);
			put(27, KeyEvent.VK_S);
			put(28, KeyEvent.VK_D);
			put(29, KeyEvent.VK_F);
			put(30, KeyEvent.VK_G);
			put(31, KeyEvent.VK_H);
			put(32, KeyEvent.VK_J);
			put(33, KeyEvent.VK_K);
			put(34, KeyEvent.VK_L);
			put(35, KeyEvent.VK_SEMICOLON);
			put(36, KeyEvent.VK_QUOTE);
			put(37, VK_102KEY);
			put(38, KeyEvent.VK_Z);
			put(39, KeyEvent.VK_X);
			put(40, KeyEvent.VK_C);
			put(41, KeyEvent.VK_V);
			put(42, KeyEvent.VK_B);
			put(43, KeyEvent.VK_N);
			put(44, KeyEvent.VK_M);
			put(45, KeyEvent.VK_COMMA);
			put(46, KeyEvent.VK_PERIOD);
			put(47, KeyEvent.VK_SLASH);
			put(48, 999); // for japanese language \ character
		}};
	}
	public byte[] convertKeyCode( int keyCode, int keyLocation, boolean keyPress, char keyChar)
	{
		Integer usbKeyCode = new Integer(0);
		byte[] outputKeycodes = new byte[ 6 ];
		boolean sendAutoKeybreak = false;
		int convertedKeyCode = KeyEvent.VK_UNDEFINED;
		autoKeyBreakMode = autoKeybreakModeOn;

		convertedKeyCode = convertToHostLayout(keyCode, keyPress, keyChar);

		switch( keyLocation )
		{
		case KeyEvent.KEY_LOCATION_LEFT:
			if(convertedKeyCode != KeyEvent.VK_UNDEFINED)
				usbKeyCode = leftMap.get( convertedKeyCode );
			else if(keyCode != KeyEvent.VK_UNDEFINED)
				usbKeyCode = leftMap.get( keyCode );
			break;

		case KeyEvent.KEY_LOCATION_NUMPAD:
			if(convertedKeyCode == KeyEvent.VK_PERIOD)
				usbKeyCode = standardMap.get(convertedKeyCode);
			else if(convertedKeyCode == KeyEvent.VK_COMMA)
				usbKeyCode = standardMap.get(convertedKeyCode);
			else{
				usbKeyCode = standardMap.get(keyCode);
				if(usbKeyCode == null)
					usbKeyCode = keypadMap.get(keyCode);
			}
			break;

		case KeyEvent.KEY_LOCATION_RIGHT:
			if(convertedKeyCode != KeyEvent.VK_UNDEFINED)
				usbKeyCode = rightMap.get( convertedKeyCode );
			else if(keyCode != KeyEvent.VK_UNDEFINED)
				usbKeyCode = rightMap.get( keyCode );
			break;

		case KeyEvent.KEY_LOCATION_STANDARD:
			if(convertedKeyCode != KeyEvent.VK_UNDEFINED)
				usbKeyCode = standardMap.get( convertedKeyCode );
			else if(keyCode != KeyEvent.VK_UNDEFINED)
				usbKeyCode = standardMap.get( keyCode );
			break;

		case KeyEvent.KEY_LOCATION_UNKNOWN:
			if((keyPress)) {
				if(convertedKeyCode != KeyEvent.VK_UNDEFINED)
					usbKeyCode = standardMap.get( convertedKeyCode );
				else if(keyCode != KeyEvent.VK_UNDEFINED)
					usbKeyCode = standardMap.get( keyCode );
			} else {
				System.err.println(LocaleStrings.getString("AD_1_USBKP"));
			}
			break;

		default:
			System.err.println(LocaleStrings.getString("AD_2_USBKP"));
			break;
		}

		if( usbKeyCode != null ) {
			switch( convertedKeyCode )
			{
			case KeyEvent.VK_CONTROL:
				if( keyLocation == KeyEvent.KEY_LOCATION_LEFT ) {
					if( keyPress ){
						modifiers |= MOD_LEFT_CTRL;
						leftCTRLHeldDown = true;
					}
					else{
						leftCTRLHeldDown = false;
						modifiers &= ~MOD_LEFT_CTRL;
					}
				}
				else {
					if( keyPress ){
						modifiers |= MOD_RIGHT_CTRL;
						rightCTRLHeldDown = true;
					}
					else{
						rightCTRLHeldDown = false;
						modifiers &= ~MOD_RIGHT_CTRL;
					}
				}
				break;

			case KeyEvent.VK_SHIFT:
				if( keyLocation == KeyEvent.KEY_LOCATION_LEFT ) {
					if( keyPress ){
						modifiers |= MOD_LEFT_SHIFT;
						shiftHeldDown = true;
					}
					else {
						shiftHeldDown = false;
						modifiers &= ~MOD_LEFT_SHIFT;
						modifiers &= ~MOD_RIGHT_SHIFT;
					}
				} else {
					if( keyPress ){
						modifiers |= MOD_RIGHT_SHIFT;
						shiftHeldDown = true;
					}
					else {
						shiftHeldDown = false;
						modifiers &= ~MOD_RIGHT_SHIFT;
						modifiers &= ~MOD_LEFT_SHIFT;
					}
				}
				break;

			case KeyEvent.VK_ALT:
				if( keyLocation == KeyEvent.KEY_LOCATION_LEFT ) {
					if( keyPress )
						modifiers |= MOD_LEFT_ALT;
					else
						modifiers &= ~MOD_LEFT_ALT;
				} else {
					if( keyPress ){
						modifiers |= MOD_RIGHT_ALT;
						altGrHeldDown = true;
					}
					else{
						altGrHeldDown = false;
						modifiers &= ~MOD_RIGHT_ALT;
					}
				}
				break;

			case KeyEvent.VK_WINDOWS:
				if( keyLocation == KeyEvent.KEY_LOCATION_LEFT ) {
					if( keyPress )
						modifiers |= MOD_LEFT_WIN;
					else
						modifiers &= ~MOD_LEFT_WIN;
				} else {
					if( keyPress )
						modifiers |= MOD_RIGHT_WIN;
					else
						modifiers &= ~MOD_RIGHT_WIN;
				}
				break;

			default:
				if( keyPress || (convertedKeyCode == KeyEvent.VK_PRINTSCREEN)) {
					outputKeycodes[ 0 ] = usbKeyCode.byteValue();
					if( autoKeybreakModeOn )
						sendAutoKeybreak = true;
				} else {
					if( !autoKeybreakModeOn ||
							( convertedKeyCode == KeyEvent.VK_NUM_LOCK ) ||
							( convertedKeyCode == KeyEvent.VK_CAPS_LOCK ) ||
							( convertedKeyCode == KeyEvent.VK_SCROLL_LOCK ) )
						outputKeycodes[ 0 ] = 0;
					else{
						autoKeybreakModeOn = autoKeyBreakMode;
						return( null );
					}
				}
				break;
			}

			//Set the current modifier key status for the default key processor also.

			USBKeyProcessorEnglish.setModifiers(modifiers);
			usbKeyRepPkt = USBKeyboardRepPkt( outputKeycodes, modifiers, sendAutoKeybreak );
			autoKeybreakModeOn = autoKeyBreakMode;
			return( usbKeyRepPkt);
		} else {
			Debug.out.println(LocaleStrings.getString("AD_3_USBKP") + KeyEvent.getKeyText( convertedKeyCode ) );
			autoKeybreakModeOn = autoKeyBreakMode;
			return( null );
		}
	}

	/**
	 * Converts the key codes send from a Client keyboard layout to the corresponding key code in 
	 * Host keyboard layout. 
	 * @param keyCode - the key code to be converted
	 * @param keyPress - true if key is pressed; false otherwise. 
	 * @return
	 */
	private int convertToHostLayout(int keyCode, boolean keyPress, char keyChar){
		Integer convertedKeyCode = null;
		int index = -1;
		if(keyPress){
			//KEY PRESSED
			//CAPS LOCK ENABLED
			if((JViewerApp.getInstance().getClientKeyboardLEDStatus() & JViewerApp.CAPSLOCK) == JViewerApp.CAPSLOCK){
				//Check for character in Shifted Caps character set.
				index = shiftedCapsCharSet[hostLayoutIndex].indexOf(keyChar);
				if(index >= 0){
					//Character found in Shifted Caps character set.
					//First character is absent in French-France and Japanese Shifted Caps character set.
					if (hostLayoutIndex == JVMenu.LANGUAGE_FRENCH ||
					hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_K ||
					hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_H) {
						index += 1;// First character is absent in French-France, Japanese-Katakana, and Japanese Hiragana
					}
					else if (hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_Q) {
						if (index == shiftedCharSet[hostLayoutIndex].indexOf(" ")) { // if the character at the index is " ", then the index should be -1
							index = -1;
						}
						else {
							index += 1;// First character is absent in Japanese-QWERTY
						}
					}
					convertedKeyCode = keyCodeMap.get(index);
					if(keyCode == KeyEvent.VK_BACK_SLASH && hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_Q) {
						convertedKeyCode = 220;
					}
					if((modifiers & MOD_LEFT_SHIFT) != MOD_LEFT_SHIFT || 
							(modifiers & MOD_RIGHT_SHIFT) != MOD_RIGHT_SHIFT){//Shift not enabled
						enableShift(true);//Enable shift
						shiftModified = true;
					}
					// if altgr is enabled in menu then don't disable altgr modifier
					if((modifiers & MOD_RIGHT_ALT) == MOD_RIGHT_ALT && (JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_RIGHT_ALT_KEY).isSelected() == false)){
						enableAltGr(false);
						altGrModified = true;
					}
					// if control is enabled in menu then don't disable control modifier
					if((modifiers & MOD_LEFT_CTRL) == MOD_LEFT_CTRL && (JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_LEFT_CTRL_KEY).isSelected() == false)){
						enableLeftCTRL(false);
						leftCTRLModified = true;
					}
				}
				else{
					index = -1;
					/*Character not found in Shifted Caps Character set.
					 * So check in Unshifted Caps Character set.
					 */
					index = normalCapsCharSet[hostLayoutIndex].indexOf(keyChar);
					if(index >= 0){
						// Character found in Unshifted Caps Character set.
						// First character is absent in Japanese
						if (hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_Q ||
						hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_K ||
						hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_H) {
							index += 1;
						}
						convertedKeyCode = keyCodeMap.get(index);
						if(keyCode == KeyEvent.VK_BACK_SLASH && hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_Q) {
							convertedKeyCode = 220;
						}
						if((modifiers & MOD_LEFT_SHIFT) == MOD_LEFT_SHIFT || 
								(modifiers & MOD_RIGHT_SHIFT) == MOD_RIGHT_SHIFT){//Shift enabled
							enableShift(false);//Disable shift
							shiftModified = true;
						}
						// if altgr is enabled in menu then don't disable altgr modifier
						if((modifiers & MOD_RIGHT_ALT) == MOD_RIGHT_ALT && (JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_RIGHT_ALT_KEY).isSelected() == false)){
							enableAltGr(false);
							altGrModified = true;
						}
						// if control is enabled in menu then don't disable control modifier
						if((modifiers & MOD_LEFT_CTRL) == MOD_LEFT_CTRL && (JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_LEFT_CTRL_KEY).isSelected() == false)){
							enableLeftCTRL(false);
							leftCTRLModified = true;
						}
					}
				}
			}
			else{//CAPS LOCK DISABLED
				//Check for character in Shifted character set.
				index = shiftedCharSet[hostLayoutIndex].indexOf(keyChar);
				if(index >= 0){
					//Character found in Shifted Character set.
					// First character is absent in French-France and Japanese Shifted Character set.
					if (hostLayoutIndex == JVMenu.LANGUAGE_FRENCH ||
					hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_K ||
					hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_H) {
						index += 1;
					}
					else if (hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_Q) {
						if (index == shiftedCharSet[hostLayoutIndex].indexOf(" ")) { // if the character at the index is " ", then the index should be -1
							index = -1;	
						}
						else {
							index += 1;
						}
					}
					convertedKeyCode = keyCodeMap.get(index);
					if(keyCode == KeyEvent.VK_BACK_SLASH && hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_Q) {
						convertedKeyCode = 220;
					}
					if((modifiers & MOD_LEFT_SHIFT) != MOD_LEFT_SHIFT || 
							(modifiers & MOD_RIGHT_SHIFT) != MOD_RIGHT_SHIFT){//Shift not enabled
						enableShift(true);//Enable shift
						shiftModified = true;
					}
					// if altgr is enabled in menu then don't disable altgr modifier
					if((modifiers & MOD_RIGHT_ALT) == MOD_RIGHT_ALT && (JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_RIGHT_ALT_KEY).isSelected() == false)){
						enableAltGr(false);
						altGrModified = true;
					}
					// if control is enabled in menu then don't disable control modifier
					if((modifiers & MOD_LEFT_CTRL) == MOD_LEFT_CTRL && (JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_LEFT_CTRL_KEY).isSelected() == false)){
						enableLeftCTRL(false);
						leftCTRLModified = true;
					}
					if(hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_Q && index == -1) {
						enableShift(false);// disable shift
						shiftModified = false;
					}
				}
				
				else{
					index = -1;
					/*Character not found in Shifted Character set.
					 * So check in Unshifted Character set.
					 */
					index = normalCharSet[hostLayoutIndex].indexOf(keyChar);
					if(index >= 0){
						// Character found in Unshifted Character set.
						// First character is absent in Japanese
						if (hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_Q ||
						hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_K ||
						hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_H) {
							index += 1;
						}
						convertedKeyCode = keyCodeMap.get(index);
						if(keyCode == KeyEvent.VK_BACK_SLASH && hostLayoutIndex == JVMenu.LANGUAGE_JAPANESE_Q) {
							convertedKeyCode = 220;
						}
						if((modifiers & MOD_LEFT_SHIFT) == MOD_LEFT_SHIFT || 
								(modifiers & MOD_RIGHT_SHIFT) == MOD_RIGHT_SHIFT){//Shift enabled
							enableShift(false);//Disable shift
							shiftModified = true;
						}
						// if altgr is enabled in menu then don't disable altgr modifier
						if((modifiers & MOD_RIGHT_ALT) == MOD_RIGHT_ALT && (JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_RIGHT_ALT_KEY).isSelected() == false)){
							enableAltGr(false);
							altGrModified = true;
						}
						// if control is enabled in menu then don't disable control modifier
						if((modifiers & MOD_LEFT_CTRL) == MOD_LEFT_CTRL && (JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_LEFT_CTRL_KEY).isSelected() == false)){
							enableLeftCTRL(false);
							leftCTRLModified = true;
						}
					}
				}
			}
			if(index < 0){
				/*Character not found in normal character set.
				 * So check for the character among AltGr characters too.
				 */
				int altGrIx = altGrCharSet[hostLayoutIndex].indexOf(keyChar);
				if(altGrIx >= 0)// char in AltGr
					index = Integer.parseInt(altGrIndex[hostLayoutIndex][altGrIx]);
				if(index >= 0){
					index -= 16;
					// Character found in AltGr Character set.
					convertedKeyCode = keyCodeMap.get(index);
					if((modifiers & MOD_RIGHT_ALT) != MOD_RIGHT_ALT)//AltGr disabled
						enableAltGr(true);//Enable AltGr
					if((modifiers & MOD_LEFT_SHIFT) == MOD_LEFT_SHIFT || 
							(modifiers & MOD_RIGHT_SHIFT) == MOD_RIGHT_SHIFT){//Shift enabled
						enableShift(false);//Disable shift
						shiftModified = true;
					}
				}

				// For AltGr + Shift key combinations in language turkish Q,F
				else if (index < 0){

					int altShiftGrIx = shiftedAltGrCharSet[hostLayoutIndex].indexOf(keyChar);
					if (altShiftGrIx >= 0)// char in AltShiftGr
						index = Integer.parseInt(shiftedAltGrIndex[hostLayoutIndex][altShiftGrIx]);
					if (index >= 0) {
						index -= 16;
						// Character found in AltShiftGr Character set.
						convertedKeyCode = keyCodeMap.get(index);
						if ((modifiers & MOD_RIGHT_ALT) != MOD_RIGHT_ALT)// AltGr
																			// disabled
							enableAltGr(true);// Enable AltGr

						if ((modifiers & MOD_LEFT_SHIFT) == MOD_LEFT_SHIFT
								|| (modifiers & MOD_RIGHT_SHIFT) == MOD_RIGHT_SHIFT) {// Shift
																						// enabled
							/*
							 * enableShift(false);//Disable shift 
							 * shiftModified = true;
							 */
						} else { // if shift disabled, enable shift
							enableShift(true);
							shiftModified = true;
						}
					}
				}

				/*
				 * Check whether the key has been triggered with CTRL modifier,
				 * so that it gives a non-printable character.
				 */
				if((modifiers & MOD_LEFT_CTRL) == MOD_LEFT_CTRL || 
						(modifiers & MOD_RIGHT_CTRL) == MOD_RIGHT_CTRL){
					//Since the key is triggered along with CTRL modifier, discard the non-printable character,
					//get the corresponding key code from local key code map, and use that key code.
					//This should be done only if AltGr is not pressed and held. Because AltGr is internally 
					//treated as CTRL+ALT 
					if(!altGrHeldDown)
						convertedKeyCode = getLocalKeyCode(keyCode);
				}
				/*if the KeyEvent key code is one among the common(non-printable) keys,
				* assign the same key code as converted key code.
				*/
				if(convertedKeyCode == null){
					convertedKeyCode = isCommonKey(keyCode);
				}
			}
			//If converted keycode is undefined, then check for a match in the local keymap.
			if(convertedKeyCode == KeyEvent.VK_UNDEFINED){
				convertedKeyCode = getLocalKeyCode(keyCode);
			}
			//If a match is still not found the convertedKeyCode is set as undefined.
			if(convertedKeyCode == null)
				convertedKeyCode = KeyEvent.VK_UNDEFINED;
			
			pressedKeyCode = convertedKeyCode;
			pressedKeyChar = keyChar;
		}
		else{//KEY RELEASED
			if (keyChar == pressedKeyChar && ((keyCode != KeyEvent.VK_SHIFT)&& // SHIFT key will not be released when SHIFT + CAPSLOCK is pressed
					(keyCode != KeyEvent.VK_CONTROL)))//This is to disable the CTRL key when the ALTGr combination if pressed from Windows Client
				convertedKeyCode = pressedKeyCode;
			else{
				//Press event has not triggered or there is no valid char during press event.
				//This could happen in the case of dead keys. So check in the local keymap for the keycode.
				//If a match is not found then the current keycode will be assigned as the converted keycode.
				convertedKeyCode = getLocalKeyCode(keyCode);
			}
			if(convertedKeyCode == KeyEvent.VK_UNDEFINED){
				/*if the KeyEvent key code is one among the common(non-printable) keys,
				 * assign the same key code as converted key code.
				 */
				convertedKeyCode = isCommonKey(keyCode);
			}
			if((modifiers & MOD_LEFT_SHIFT) == MOD_LEFT_SHIFT || 
					(modifiers & MOD_RIGHT_SHIFT) == MOD_RIGHT_SHIFT || shiftModified){//Shift enabled
				if(shiftHeldDown)//shift held down
					enableShift(true);//keep shift enabled
				else
					enableShift(false);//disable shift
			}
			if((modifiers & MOD_RIGHT_ALT) == MOD_RIGHT_ALT || altGrModified){//AltGr enabled
				if(altGrHeldDown)//AltGr held down. 
					enableAltGr(true);//keep AltGr enabled.
				else if (JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_RIGHT_ALT_KEY).isSelected() == false){ // if altgr is enabled in menu then don't disable altgr modifier
					enableAltGr(false);//disable AltGr.
				}
			}
			if((modifiers & MOD_LEFT_CTRL) == MOD_LEFT_CTRL || leftCTRLModified){//Left CTRL enabled
				if(leftCTRLHeldDown)//Left CTRL held down. 
					enableLeftCTRL(true);//keep Left CTRL enabled.
				else if (JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_RIGHT_CTRL_KEY).isSelected() == false){ // if control is enabled in menu then don't disable control modifier
					enableLeftCTRL(false);//disable Left CTRL.
				}
			}
			shiftModified = false;
			altGrModified = false;
			leftCTRLModified = false;
			//Solves the issue with RHEL 7.2 host trigerring print screen continuously, when print screen key is sent from client.
			if(KeyEvent.VK_PRINTSCREEN != keyCode)
				autoKeybreakModeOn = false;
			pressedKeyCode = KeyEvent.VK_UNDEFINED;
			pressedKeyChar = NULL_CHAR;
		}
		return convertedKeyCode;
	}
	/**
	 * Enable Shift key modifier
	 * @param state - true if the Shift modifier needs to be enabled; false otherwise.
	 */
	private void enableShift(boolean state){
		if(state){
			modifiers |= MOD_LEFT_SHIFT;
			modifiers |= MOD_RIGHT_SHIFT;
		}
		else{
			modifiers &= ~MOD_LEFT_SHIFT;
			modifiers &= ~MOD_RIGHT_SHIFT;
		}
	}

	/**
	 * Enable AltGr key modifier
	 * @param state - true if the Shift modifier needs to be enabled; false otherwise.
	 */
	private void enableAltGr(boolean state){
		if(state){
			modifiers |= MOD_RIGHT_ALT;
		}
		else{
			modifiers &= ~MOD_RIGHT_ALT;
		}
	}

	/**
	 * Enable/Disable Left CTRL key modifier
	 * @param state - true if the Shift modifier needs to be enabled; false otherwise.
	 */
	private void enableLeftCTRL(boolean state){
		if(state){
			modifiers |= MOD_LEFT_CTRL;
		}
		else{
			modifiers &= ~MOD_LEFT_CTRL;
		}
	}

	/**
	 * Checks if the given key code is one among the common(non-printable) keys for all layouts.
	 * Return the same key code if it is among common key codes, else return undefined key code.
	 * @param keyCode the key code to be checked.
	 * @return the converted key code.
	 */
	private int isCommonKey(int keyCode) {
		int commonKeyCode = KeyEvent.VK_UNDEFINED;
		/*if the KeyEvent key code is one among the common(non-printable) keys.
		 * 
		 */
		for(int commonIndex = 0; commonIndex < commonKeys.length; commonIndex++)
			if(keyCode == commonKeys[commonIndex]){
				commonKeyCode = keyCode;
				break;
			}
		return commonKeyCode;
	}

	public void setAutoKeybreakMode(boolean state) {
		autoKeybreakModeOn = state;
	}

	public boolean getAutoKeybreakMode() {
		return (autoKeybreakModeOn);
	}

	/**
	 * This method is used to get the cross mapped key code, when a key is
	 * pressed along with CTRL modifier.
	 * 
	 * @param keyCode
	 *            - the key code to be converted.
	 * @return converted key code.
	 */
	protected int getLocalKeyCode(int keyCode) {
		Integer localKeyCode = null;
		if (localKeyCodeMap != null)
			localKeyCode = localKeyCodeMap.get(keyCode);
		// If key code not found in local map, use the same key code.
		if (localKeyCode == null)
			localKeyCode = keyCode;
		return localKeyCode;
	}

	public HashMap<Integer, Integer> getLocalKeyCodeMap() {
		return localKeyCodeMap;
	}

	public void setLocalKeyCodeMap(HashMap<Integer, Integer> localKeyCodeMap) {
		this.localKeyCodeMap = localKeyCodeMap;
	}

	/**
	 * @return the hostLayoutIndex
	 */
	public int getHostLayoutIndex() {
		return hostLayoutIndex;
	}

	/**
	 * @param hostLayoutIndex
	 *            the hostLayoutIndex to set
	 */
	public void setHostLayoutIndex(int hostLayoutIndex) {
		this.hostLayoutIndex = hostLayoutIndex;
	}
}
