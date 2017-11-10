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

import java.awt.Container;

public interface IOEMStandAloneConnectionDialog {

	/**
	 * To Customize standalone connection dialog
	 * @param container
	 */
	public abstract void customizeDialogComponents(Container container);
	public abstract String[] getSupportedLocales();

}