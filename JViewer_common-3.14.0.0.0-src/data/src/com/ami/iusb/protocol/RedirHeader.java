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

package com.ami.iusb.protocol;

import java.nio.ByteBuffer;

import com.ami.iusb.RedirProtocolException;


abstract public class RedirHeader
{
	protected int headerLen;
	protected int command;
	protected long dataLen;
	protected int status;
	protected int serverID;
	protected int serverVer;

	abstract public void write( ByteBuffer buffer );
	abstract public void read( ByteBuffer rawBuffer ) throws RedirProtocolException;
}
