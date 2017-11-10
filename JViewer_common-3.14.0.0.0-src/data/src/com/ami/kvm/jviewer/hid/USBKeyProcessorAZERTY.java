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

import com.ami.kvm.jviewer.gui.JVMenu;

public class USBKeyProcessorAZERTY extends USBCrossMapKeyProcessor
{
	protected HashMap<Integer, Integer> azertyMap;

	protected static byte modifiers = 0;

	/**
	 * Creates a new instance of USBKeyProcessor
	 */
	public USBKeyProcessorAZERTY(int hostLayoutIndex)
	{
		super();
		azertyMap = new HashMap<Integer, Integer>();

		azertyMap.put(KeyEvent.VK_A, KeyEvent.VK_Q);
		azertyMap.put(KeyEvent.VK_Z, KeyEvent.VK_W);
		azertyMap.put(KeyEvent.VK_W, KeyEvent.VK_Z);
		azertyMap.put(KeyEvent.VK_Q, KeyEvent.VK_A);
		azertyMap.put(KeyEvent.VK_M, KeyEvent.VK_SEMICOLON);
		azertyMap.put(KeyEvent.VK_COMMA, KeyEvent.VK_M);
		azertyMap.put(KeyEvent.VK_SEMICOLON, KeyEvent.VK_COMMA);
		azertyMap.put(KeyEvent.VK_COLON, KeyEvent.VK_PERIOD);
		azertyMap.put(KeyEvent.VK_RIGHT_PARENTHESIS, KeyEvent.VK_MINUS);
		azertyMap.put(KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_OPEN_BRACKET);
		azertyMap.put(KeyEvent.VK_DOLLAR, KeyEvent.VK_CLOSE_BRACKET);
		azertyMap.put(KeyEvent.VK_ASTERISK, KeyEvent.VK_BACK_SLASH);
		if(hostLayoutIndex == JVMenu.LANGUAGE_FRENCH)
			azertyMap.put(KeyEvent.VK_EXCLAMATION_MARK, KeyEvent.VK_SLASH);
		else if(hostLayoutIndex == JVMenu.LANGUAGE_FRENCH_BELGIUM){
			azertyMap.put(KeyEvent.VK_EQUALS, KeyEvent.VK_SLASH);
			azertyMap.put(KeyEvent.VK_MINUS, KeyEvent.VK_EQUALS);
			azertyMap.put(KeyEvent.VK_DEAD_DIAERESIS, KeyEvent.VK_OPEN_BRACKET);
		}
		if(System.getProperty("os.name").equals("Linux"))
		{
			azertyMap.put(KeyEvent.VK_AMPERSAND, KeyEvent.VK_1);
			azertyMap.put(KeyEvent.VK_QUOTE, KeyEvent.VK_4);
			azertyMap.put(KeyEvent.VK_LEFT_PARENTHESIS, KeyEvent.VK_5);
			azertyMap.put(KeyEvent.VK_MINUS, KeyEvent.VK_6);
			azertyMap.put(KeyEvent.VK_UNDERSCORE, KeyEvent.VK_8);
		}
		setLocalKeyCodeMap(azertyMap);
		setHostLayoutIndex(hostLayoutIndex);
	}
}
