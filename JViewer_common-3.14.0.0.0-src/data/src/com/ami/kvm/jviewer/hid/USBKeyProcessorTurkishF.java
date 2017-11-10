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

public class USBKeyProcessorTurkishF extends USBCrossMapKeyProcessor {
	protected HashMap<Integer, Integer> turkishFMap;
	public USBKeyProcessorTurkishF() {
		super();
		turkishFMap = new HashMap<Integer, Integer>();
		turkishFMap.put(KeyEvent.VK_F, KeyEvent.VK_Q);
		turkishFMap.put(KeyEvent.VK_G, KeyEvent.VK_W);
		turkishFMap.put(KeyEvent.VK_I, KeyEvent.VK_R);
		turkishFMap.put(KeyEvent.VK_O, KeyEvent.VK_T);
		turkishFMap.put(KeyEvent.VK_D, KeyEvent.VK_Y);
		turkishFMap.put(KeyEvent.VK_R, KeyEvent.VK_U);
		turkishFMap.put(KeyEvent.VK_N, KeyEvent.VK_I);
		turkishFMap.put(KeyEvent.VK_H, KeyEvent.VK_O);
		turkishFMap.put(KeyEvent.VK_Q, KeyEvent.VK_CLOSE_BRACKET);
		turkishFMap.put(KeyEvent.VK_W, KeyEvent.VK_OPEN_BRACKET);
		turkishFMap.put(KeyEvent.VK_U, KeyEvent.VK_A);
		turkishFMap.put(KeyEvent.VK_E, KeyEvent.VK_D);
		turkishFMap.put(KeyEvent.VK_A, KeyEvent.VK_F);
		turkishFMap.put(KeyEvent.VK_T, KeyEvent.VK_H);
		turkishFMap.put(KeyEvent.VK_K, KeyEvent.VK_J);
		turkishFMap.put(KeyEvent.VK_M, KeyEvent.VK_K);
		turkishFMap.put(KeyEvent.VK_Y, KeyEvent.VK_SEMICOLON);
		turkishFMap.put(KeyEvent.VK_J, KeyEvent.VK_Z);
		turkishFMap.put(KeyEvent.VK_V, KeyEvent.VK_C);
		turkishFMap.put(KeyEvent.VK_C, KeyEvent.VK_V);
		turkishFMap.put(KeyEvent.VK_Z, KeyEvent.VK_N);
		turkishFMap.put(KeyEvent.VK_S, KeyEvent.VK_M);
		turkishFMap.put(KeyEvent.VK_B, KeyEvent.VK_COMMA);
		setLocalKeyCodeMap(turkishFMap);
		setHostLayoutIndex(JVMenu.LANGUAGE_TURKISH_F);
	}
}
