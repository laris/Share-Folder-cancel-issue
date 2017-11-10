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

public interface IOEMAuthentication {
	/**
	 * This method does the basic authentication functions
	 * @param username
	 * @param password
	 * @return AMI_CODE if ami should handle the request <br/>
	 * OEM_CUSTOMIZED if the oem customer has handled the request.
	 */
	public int authenticate(String username, String password);
}
