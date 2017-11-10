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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 *
 * Interface for JViewerApp
 * Method for invoking shortcut menu in fullscreen for the SOCrelated menu
 * Method for invoking action listener for SOC related Menu
 */
public interface ISOCApp {
	public void OnInvokeSocMenuShortCutFullscreen(KeyEvent e);
	public void SOC_Menu_ActionMethod(ActionEvent e);
	public int SOC_Session_validated();
}
