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
// JViewer Frame window menu component.
//

package com.ami.kvm.jviewer.gui;


import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.common.ISOCMenu;
import com.ami.kvm.jviewer.common.oem.IOEMJVMenu;

/**
 * Window Menu class.
 */
public class WindowMenu extends JVMenu {

    private JMenuBar m_menuBar;
    private ISOCMenu m_soc_menu;
    private IOEMJVMenu oemMenu; 
    private JLabel messageLbl;

	/**
	 * The constructor.
	 */
	public WindowMenu()
	{
		m_soc_menu = JViewerApp.getSoc_manager().getSOCmenu();
		oemMenu = JViewerApp.getOEMManager().getOEMJVMenu();
		m_soc_menu.SetSOCMenuItem(m_menuItems);
		m_soc_menu.SetSOCMenu(m_menu);
		oemMenu.setOEMMenuItem(m_menuItems);
		oemMenu.setOEMMenu(m_menu);
		constructUserIf();
	}

    /**
     * Get the menu bar.
     */
	public JMenuBar getMenuBar()
	{
		return m_menuBar;
	}

	/**
	 *
	 * Construct user interface.
	 *
	 */
	private void constructUserIf()
	{
		m_menuBar 		= new JMenuBar();
		m_menuBar.add(constructVideoMenu());
		m_menuBar.add(constructKeyboardMenu());
		m_menuBar.add(constructMouseMenu());
		m_menuBar.add(constructOptionsMenu());
		m_menuBar.add(constructDeviceRedirMenu());
		m_menuBar.add(constructKeyboardLayoutMenu());
		//if(JViewer.VideoRecord_Flag)
		m_menuBar.add(constructVideoRecordMenu());
		m_menuBar.add(constructPowerMenu());
		m_menuBar.add(constructUserMenu());
		m_menuBar.add(constructHelpMenu());
		m_menuBar.add(Box.createHorizontalStrut(50));
		m_menuBar.add(Box.createHorizontalStrut(50));
		m_menuBar.add(constructZoomLabelText());
		m_menuBar.add(Box.createHorizontalStrut(10));
		m_menuBar.add(Box.createHorizontalGlue());
		m_menuBar.add(constructString());
		m_menuBar.add(constructMessage(),BorderLayout.EAST);
		m_menuBar.add(Box.createHorizontalStrut(30));
		//create SOC specific menu items.
		m_soc_menu.initKVMPartialExceptionSOCMenuItems();
		m_soc_menu.constructsocMenu(m_menuBar);
		//create OEM specific menu items.
		oemMenu.initKVMPartialExceptionOEMMenuItems();
		oemMenu.customizeOEMMenu(m_menuBar);		
		JVMenu.INITIAL_MENU_STATUS = false;
	}
	private JLabel constructMessage() {
		messageLbl = new JLabel("");		
		return messageLbl;
	}

	public void setMessage(String message) {		
		messageLbl.setText(message);
	}	
}
