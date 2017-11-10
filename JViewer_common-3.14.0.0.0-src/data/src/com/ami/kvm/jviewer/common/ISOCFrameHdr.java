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

import java.nio.ByteBuffer;



/**
 * Frame header Interface
 *
 */
public interface ISOCFrameHdr {
	public short getheight();
	public short getwidth();
	public short getresX();
	public short getresY();
	public void setHeader(ByteBuffer frameByteBuf);
	public int getFrameType();
	public int getFrameHeadersize();
	public int getFrameSize();
	public short getcurwidth();
	public short getcurheight();
}
