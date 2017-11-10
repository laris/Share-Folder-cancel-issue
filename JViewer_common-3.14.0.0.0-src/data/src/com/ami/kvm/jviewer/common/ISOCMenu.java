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
package com.ami.kvm.jviewer.common;

import java.util.Hashtable;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * Menu INterface to creae the SOC related Menus
 */
public interface ISOCMenu {
	public void constructsocMenu(JMenuBar Menubar);
	public void constructsocMenu(JPopupMenu menu);
	public void SetSOCMenuItem(Hashtable<String, JMenuItem> items);
	public void SetSOCMenu(Hashtable<String, JMenu> items);
	public void initKVMPartialExceptionSOCMenuItems();
}
