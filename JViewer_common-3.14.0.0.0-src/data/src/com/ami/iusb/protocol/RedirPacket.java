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
/*
 * RedirPacket.java
 *
 * Created on November 22, 2004, 12:44 PM
 */

package com.ami.iusb.protocol;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.ami.iusb.EncryptionException;

/**
 * RedirPacket serves to bundle up the basics of packet send, receive, and
 * construction that are constant across all kinds of packet.  Packet
 * encryption code is also contained here.
 * @author andrewm@ami.com
 */
abstract public class RedirPacket
{
	protected RedirHeader header;
	protected boolean encrypted = false;
	protected Cipher packetCipher;
	protected SecretKeySpec encryptionKey;

	protected RedirPacket()
	{
	//Nothing to do
	}


	public int getPacketStatus()
	{
		return( header.status );
	}

	public abstract void writePacket( ByteBuffer buffer ) throws EncryptionException;
	public abstract void readData( ByteBuffer buffer ) throws BufferUnderflowException;

}