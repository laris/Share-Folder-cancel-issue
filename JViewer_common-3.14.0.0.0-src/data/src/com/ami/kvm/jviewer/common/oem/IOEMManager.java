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

import java.awt.Point;
public interface IOEMManager {
	// Status Codes
	public static final int AMI_CODE = 0x00;
	public static final int OEM_CUSTOMIZED = 0x01;
	public static final int FAILURE = 0x02;

	/**
	 * @return OEMKVMClient
	 */
	public IOEMKvmClient getOEMKvmClient();

	/**
	 * @return OEMJVMenu
	 */
	public IOEMJVMenu getOEMJVMenu();

	/**
	 * @return OEMAuthentication
	 */
	public IOEMAuthentication getOEMAuthentication();

	/**
	 * @return oemipmiCommandDialog
	 */
	public IOEMIPMICommandDialog getOEMIoemipmiCommandDialog();

	/**
	 * @return connectionDialog
	 */
	public IOEMStandAloneConnectionDialog getOEMStandAloneConnectionDialog();

	/**
	 * Handles OEM specific command arguments
	 * @param args
	 * @param index
	 * @return AMI_CODE if ami should handle the request <br/>
	 * OEM_CUSTOMIZED if the oem customer has handled the request
	 */
	public int handleOemArguments(String[] args, int index);

	/**
	 * Sets FSFrame title
	 * @param label
	 * @return AMI_CODE if ami should handle the request <br/>
	 * OEM_CUSTOMIZED if the oem customer has handled the request
	 */
	public int setFSFrameTitle(String label);

	/**
	 * Sets WndFrame title
	 * @param label
	 * @return AMI_CODE if ami should handle the request <br/>
	 * OEM_CUSTOMIZED if the oem customer has handled the request
	 */
	public int setWndFrameTitle(String label);
	
	/**
	 * Returns the position where the Jviewer window should be placed
	 */
	public Point getWindowPosition();

}
