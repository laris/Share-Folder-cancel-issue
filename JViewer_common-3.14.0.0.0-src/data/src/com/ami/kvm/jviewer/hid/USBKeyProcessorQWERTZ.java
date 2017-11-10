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

public class USBKeyProcessorQWERTZ extends USBCrossMapKeyProcessor{
	protected HashMap<Integer, Integer> qwertzMap;
	public USBKeyProcessorQWERTZ(int hostLayoutIndex){
		super();
		qwertzMap = new HashMap<Integer, Integer>();
		qwertzMap.put(KeyEvent.VK_Y, KeyEvent.VK_Z);
		qwertzMap.put(KeyEvent.VK_Z, KeyEvent.VK_Y);
		if(hostLayoutIndex == JVMenu.LANGUAGE_GERMAN_GER){
			qwertzMap.put(KeyEvent.VK_DEAD_ACUTE, KeyEvent.VK_EQUALS);
			qwertzMap.put(KeyEvent.VK_PLUS, KeyEvent.VK_CLOSE_BRACKET);
			qwertzMap.put(KeyEvent.VK_NUMBER_SIGN, KeyEvent.VK_BACK_SLASH);
			qwertzMap.put(KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_BACK_QUOTE);
			qwertzMap.put(KeyEvent.VK_MINUS, KeyEvent.VK_SLASH);
			qwertzMap.put(KeyEvent.VK_SLASH, KeyEvent.VK_MINUS);
		}
		else if(hostLayoutIndex == JVMenu.LANGUAGE_GERMAN_SWISS){
			qwertzMap.put(KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_EQUALS);
			qwertzMap.put(KeyEvent.VK_DEAD_DIAERESIS, KeyEvent.VK_CLOSE_BRACKET);
			qwertzMap.put(KeyEvent.VK_DOLLAR, KeyEvent.VK_BACK_SLASH);
			qwertzMap.put(KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_EQUALS);
		}
		setLocalKeyCodeMap(qwertzMap);
		setHostLayoutIndex(hostLayoutIndex);
	}
}
