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

import java.io.DataInputStream;
import java.nio.ByteBuffer;

/**
 *
 *Video related Interface
 */
public interface ISOCJVVideo {
	public void VideoBuffer_Initialize();
	public void Newframevalidate();
	public ByteBuffer decompressframe(ISOCFrameHdr hdr, ByteBuffer frameByteBuf);
	public void SOC_Specific(ByteBuffer compBuffer);
	public void setframehdr(ISOCFrameHdr hdr);
	public void SOCreset();
	public void SOCBlankscreen();
	public void soccompressionchange();
	public void socreadframeattributes(DataInputStream stream);
}
