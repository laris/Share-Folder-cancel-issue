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

/**
 *
 *Commmon Interface manager to initate the Instance for the SOC interface
 */
public interface ISOCManager {
	public String getSOC();
	public int getSOCID();
	public String getSOCVersion();
	public ISOCApp getSOCApp();
	public ISOCMenu getSOCmenu();
	public ISOCKvmClient getSOCKvmClient();
	public ISOCJVVideo getSOCJVVideo();
	public ISOCCreateBuffer getSOCCreateBuffer();
	public ISOCFrameHdr getSOCFramehdr();
}
