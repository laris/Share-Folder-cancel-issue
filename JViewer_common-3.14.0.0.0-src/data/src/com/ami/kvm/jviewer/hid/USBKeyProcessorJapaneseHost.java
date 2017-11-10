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
import com.ami.kvm.jviewer.gui.AutoKeyboardLayout;
import com.ami.kvm.jviewer.gui.JVMenu;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;

public class USBKeyProcessorJapaneseHost extends USBCrossMapKeyProcessor{
	protected HashMap<Integer, Integer> japaneseMap;

	public USBKeyProcessorJapaneseHost(){
		super();
		japaneseMap = new HashMap<Integer, Integer>();
		japaneseMap.put(123,123);
        japaneseMap.put(121,121);
        japaneseMap.put(112,112);
        japaneseMap.put(514,61);
        japaneseMap.put(512,91);
        japaneseMap.put(91,93);
        japaneseMap.put(92,220);
        japaneseMap.put(93,92);        
        japaneseMap.put(513,222);
        japaneseMap.put(115,135);
        japaneseMap.put(135,135);
        japaneseMap.put(125,137);
        japaneseMap.put(243,192);
        japaneseMap.put(244,192);        
        japaneseMap.put(KeyEvent.VK_INPUT_METHOD_ON_OFF,KeyEvent.VK_AGAIN);
        setLocalKeyCodeMap(japaneseMap);
        setHostLayoutIndex(JVMenu.LANGUAGE_JAPANESE_Q);
	}
}
