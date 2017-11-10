/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2015, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
 ****************************************************************/
 
package com.ami.kvm.jviewer.common.oem;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public interface IOEMJVMenu {
	/** 
	 * To customize JVMenu
	 * @param menuBar
	 */
	public void customizeOEMMenu(JComponent menuBar);

	/**
	 * To handle oem specific menu events
	 * @param e
	 * @return AMI_CODE if ami should handle the request <br/>
	 * OEM_CUSTOMIZED if the oem customer has handled the request.
	 */
	public int handleMenuEvents(ActionEvent e);

	/**
	 * Set's oem menu item
	 * @param items
	 */
	public void setOEMMenuItem(Hashtable<String, JMenuItem> items);

	/**
	 * Set's oem menu
	 * @param items
	 */
	public void setOEMMenu(Hashtable<String, JMenu> items);

	/**
	 * To initialize KVMPartialExceptionOEMMenuItems
	 */
	public void initKVMPartialExceptionOEMMenuItems();

	/**
	 * To change menu on power control
	 */
	public void enableMenuOnPowerControls();

	/**
	 * To change menu on KVMPartial
	 * @param state
	 */
	public void enableMenuOnKVMPartial(boolean state);

	/**
	 * To change menu status
	 * @param name
	 * @param redir
	 */
	public void notifyMenuStateEnable(String name, boolean redir);

	/**
	 * Enable menu
	 * @param exceptMenu
	 * @param enable
	 * @param updateMenuState
	 */
	public void enableMenu(String exceptMenu[], boolean enable, boolean updateMenuState);

	/**
	 * Change menu items on pause/resume
	 * @param str
	 * @return
	 */
	public int oemchangeMenuItemsStatusOnPauseResume(String str);

	/**
	 * @return oemPowerControlExceptionList
	 */
	public String[] getOemPowerControlExceptionList();
}
