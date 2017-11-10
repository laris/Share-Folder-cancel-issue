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
 * RedirProtocol.java
 *
 * Created on June 3, 2005, 4:22 PM
 */

package com.ami.iusb.protocol;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.ami.iusb.RedirProtocolException;

/**
 * RedirProtocol objects are used by {@link #PacketMaster}s to parse incoming
 * data into {@link #RedirPacket}s.
 * @author andrewm@ami.com
 */
abstract public class RedirProtocol
{
    /** Creates a new instance of RedirProtocol */
    protected RedirProtocol()
    {
    	//Nothing to do
    }

    /**
     * Parse the available data in <b>inputBuffer</b> and return a
     * {@link #RedirPacket} of a type that is appropriate for this
     * protocol.
     * @param inputBuffer The source of raw data to convert into packets
     * @returns A {@link #RedirPacket} of a type appropriate to this protocol
     * @throws RedirProtocolException on protocol errors
     */
    abstract public RedirPacket getPacket( ByteBuffer inputBuffer ) throws RedirProtocolException, BufferUnderflowException;

}
