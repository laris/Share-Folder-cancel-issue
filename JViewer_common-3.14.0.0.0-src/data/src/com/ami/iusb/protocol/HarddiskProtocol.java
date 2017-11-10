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
 * FloppyProtocol.java
 *
 * Created on July 13, 2005, 5:39 PM
 */

package com.ami.iusb.protocol;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.ami.iusb.RedirProtocolException;
import com.ami.kvm.imageredir.IUSBHeader;

public class HarddiskProtocol extends RedirProtocol
{
    /**
     * Creates a new instance of HarddiskProtocol
     *
     */
    public HarddiskProtocol()
    {
    		// Nothing to do
    }

    /**
     * Receives the data from the HDServer Reqeust Packet
     */
    public RedirPacket getPacket( ByteBuffer inputBuffer ) throws RedirProtocolException
    {
        IUSBHeader header = new IUSBHeader();
        RedirPacket packet;

        try
        {
            /* Parse the header info out of the buffer */
            header.read( inputBuffer );
            /* There is only one kind of packet that can come on this connection */
            packet = new IUSBSCSI( header );
            packet.readData( inputBuffer );
        }
        catch( BufferUnderflowException e )
        {
            /* If there is not enough data to read in an entire packet,
             * return null */
            return( null );
        }
        return( packet );
    }
}
