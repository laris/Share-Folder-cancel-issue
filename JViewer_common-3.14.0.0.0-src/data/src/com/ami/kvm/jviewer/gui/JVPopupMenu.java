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
////////////////////////////////////////////////////////////////////////////////
//
// This module creates the right-click pop-up menu to be displayed in the JViewer.
//
package com.ami.kvm.jviewer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

public class JVPopupMenu extends JPopupMenu{
	//Action commands
	public static final String CUT = "Cut";
	public static final String COPY = "Copy";
	public static final String PASTE = "Paste";

	protected HashMap<String, Object> popupMenuItems;
	/**
	 * Constructor
	 */
	public JVPopupMenu(){
		popupMenuItems = new HashMap<String, Object>();
	}

	/**
	 * Adds a menu item to the pop-up menu
	 * @param menuItemText - the text to be displayed on the menu item
	 * @param menuItemActionCommand - the action command associated with the menu item.
	 * @param mnemonic - the mnemonic associated with the menu item.
	 * @param accelerator - the accelerator associated with the menu item
	 */
	public void addMenuItem(String menuItemText, String menuItemActionCommand, char mnemonic, KeyStroke accelerator){
		this.add(createMenuItem(menuItemText, menuItemActionCommand, mnemonic, accelerator));
	}
	/**
	 *  Adds a menu item to the pop-up menu
	 * @param menuItemText - the text to be displayed on the menu item
	 * @param menuItemActionCommand - the action command associated with the menu item.
	 * @param mnemonic - the mnemonic associated with the menu item.
	 * @param index - the index (position) at which the menu item should be added in the pop-up menu
	 * @param accelerator - the accelerator associated with the menu item
	 */
	public void addMenuItem(String menuItemText, String menuItemActionCommand, char mnemonic, int index, KeyStroke accelerator){
		this.add(createMenuItem(menuItemText, menuItemActionCommand, mnemonic, accelerator), index);
	}

	/**
	 * Add menu item separator in the pop-up menu.
	 */
	public void addMenuItemSeparator(){
		this.addSeparator();
	}

	/**
	 * Creates the menu item with the given properties.
	 * @param menuItemText - the text to be displayed on the menu item
	 * @param menuItemActionCommand - the action command associated with the menu item.
	 * @param mnemonic - the mnemonic associated with the menu item.
	 * @param accelerator - the accelerator associated with the menu item
	 * @return
	 */
	private JMenuItem createMenuItem(String menuItemText, String menuItemActionCommand, char mnemonic, KeyStroke accelerator){
		JMenuItem menuItem = new JMenuItem(menuItemText);
		menuItem.setActionCommand(menuItemActionCommand);
		menuItem.setMnemonic(mnemonic);
		menuItem.setAccelerator(accelerator);
		popupMenuItems.put(menuItemActionCommand, menuItem);
		return menuItem;
	}

	/**
	 * Creates a basic pop-up menu with the CUt, Copy, Paste functionalities. 
	 */
	public void createEditPopup(){
		addMenuItem(LocaleStrings.getString("AI_1_JVPM"), CUT, ' ',
				KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		addMenuItem(LocaleStrings.getString("AI_2_JVPM"), COPY, ' ',
				KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		addMenuItem(LocaleStrings.getString("AI_3_JVPM"), PASTE, ' ',
				KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
	}

	/**
	 * Adds the action listener to a menu item.
	 * @param al - the action listener to be added.
	 */
	public void addActionListener(ActionListener al){
		Set<String> keySet = popupMenuItems.keySet();
		Iterator itr = keySet.iterator();
		JMenuItem menuItem;
		while(itr.hasNext()){
			menuItem = (JMenuItem) popupMenuItems.get(itr.next());
			if(menuItem != null)
				menuItem.addActionListener(al);
		}
	}

	/**
	 * Gets the menu item associated the given action command
	 * @param actionCommand - the action command associated with the requested menu item
	 * @return
	 */
	public JMenuItem getMenuItem(String actionCommand){
		return (JMenuItem)popupMenuItems.get(actionCommand);
	}

	/**
	 * Enables all the menu items in a pop-up menu.
	 */
	public void enableAll(){
		Set<String> keySet = popupMenuItems.keySet();
		Iterator itr = keySet.iterator();
		JMenuItem menuItem;
		while(itr.hasNext()){
			menuItem = (JMenuItem) popupMenuItems.get(itr.next());
			if(menuItem != null)
				menuItem.setEnabled(true);
		}
	}
}
