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
 * IUSBSCSI.java
 *
 * Created on January 27, 2005, 2:12 PM
 */

package com.ami.iusb.protocol;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.ami.iusb.EncryptionException;
import com.ami.iusb.IUSBRedirSession;
import com.ami.iusb.RedirProtocolException;
import com.ami.iusb.RedirectionException;
import com.ami.kvm.imageredir.IUSBHeader;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.LocaleStrings;



public final class IUSBSCSI extends RedirPacket
{

	public byte Lba;//For eject Lba is 0x00020000 and for load 0x00030000
	public int instanceNum;//get the instance no display in JViewer
	public int connectionStatus = -1;
	public int dataLen;
	public int opcode;
	public ByteBuffer data;
	public String m_otherIP;
	private boolean preBuffered;

	public static final int IUSB_SCSI_PKT_SIZE = 62; //this is including IUSB_HEADER(of size 32).
	public static final int IUSB_SCSI_PKT_SIZE_WITHOUT_HEADER = 30;
	public static final int IUSB_SCSI_OPCODE_INDEX = 41;
	public static final int OPCODE_EJECT = 0x1b;
	public static final int OPCODE_KILL_REDIR = 0xf6;
	public static final int MEDIA_SESSION_DISCONNECT = 0xf7;
	public static final int IUSB_SCSI_OPCODE_KEEP_ALIVE = 0xf3;
	public static final int DEVICE_INFO = 0xf8;
	public static final int IUSB_SCSI_OPCODE_GET_NOTIF_STAT= 0x4a;
	public static final int IUSB_SCSI_OPCODE_GET_NOTIF_STAT_PKT_SIZE = 37; 

    /**
     * Getting the USB header from the received Reuwst packet
     * @param header
     */
    public IUSBSCSI( IUSBHeader header )
    {
        this.header = header;
        instanceNum = header.getInstance();
        dataLen = (int)header.getDataPktLen();
    }

    /**
     *	Method Reads the data and assigned to the heade data
     * @param packetBuffer
     * @param preBuffered
     * @throws RedirProtocolException
     */
    public IUSBSCSI( ByteBuffer packetBuffer, boolean preBuffered ) throws RedirProtocolException
    {
        this.preBuffered = preBuffered;
        header = new IUSBHeader();
        header.read( packetBuffer );
        ( (IUSBHeader)header ).setDataPktLen(packetBuffer.limit() - header.headerLen);
        dataLen = (int)( (IUSBHeader)header ).getDataPktLen();
        data = packetBuffer.slice();
    }

    /***
     *Creating the packet to send to the CDServer
     */
    public void writePacket( ByteBuffer buffer )
    {
        /* Set direction to be FROM_REMOTE, as we're sending this now */
        ( (IUSBHeader)header ).setDirection(0x80);
        /* Write out the header into buffer */
        header.write( buffer );

        if( preBuffered )
        {
            buffer.limit( dataLen + IUSBHeader.HEADER_LEN );
            buffer.position( buffer.limit() );
        }
        else
            buffer.put( data );
    }

    /**
     *Parse the Readed request data
     */
    public void readData( ByteBuffer buffer ) throws BufferUnderflowException
    {
        /* Interpret data */
        byte [] otherIP = new byte[39];
        data = ByteBuffer.allocate( dataLen );

		if( buffer.remaining() < dataLen )
			throw new BufferUnderflowException();

        data.put( buffer );
        Lba = data.get(13);
        opcode = (int)data.get( 9 ) & 0xff;
        if( ( opcode == 0xf1 ) && ( dataLen > 30 ) )
        {
			connectionStatus = (int)data.get( 30 );
			try {
				data.position( 31 );
				data.get(otherIP);
				m_otherIP = new String(otherIP).trim();
			}
			catch(BufferUnderflowException e)
			{
				System.err.println( e.getMessage() );
				m_otherIP = new String(""); /* fill with empty string if any exception occurs */
				throw new BufferUnderflowException();
			}
		}
		else
			connectionStatus = -1;
	}

	/**
	 * Sends command to media server
	 * @param packetMaster instance of PacketMaster used.
	 * @param packetWriteBuffer the ByteBuffer containing response header.
	 * @param buffer the data to be written to the response.
	 * @param opcode opcode to be sent.
	 */
	public static void sendCommandToMediaServer(PacketMaster packetMaster, ByteBuffer packetWriteBuffer, byte[] buffer, int opcode) throws RedirectionException, IOException {

		int headerLength = IUSBSCSI.IUSB_SCSI_PKT_SIZE - IUSBHeader.HEADER_LEN;
		int totalLength = headerLength;

		packetWriteBuffer.clear();
		if(buffer != null && buffer.length >= 0) {
			if(buffer.length > (packetWriteBuffer.capacity() - IUSBHeader.HEADER_LEN))
			{
				Debug.out.println("Passed buffer length is greater than allocated bytebuffer capacity");
				return;
			}
			totalLength += buffer.length;
			packetWriteBuffer.limit(IUSBSCSI.IUSB_SCSI_PKT_SIZE	+ buffer.length);
		} else {
			packetWriteBuffer.limit(IUSBSCSI.IUSB_SCSI_PKT_SIZE);
		}

		if(packetMaster == null || packetWriteBuffer == null) {
			Debug.out.println("Error sending packet. PacketMaster/packetWriteBuffer is null!!!");
			return;
		}
		
		IUSBHeader iusbHeader = new IUSBHeader(totalLength);
		iusbHeader.write(packetWriteBuffer);
		packetWriteBuffer.position(IUSB_SCSI_OPCODE_INDEX); // Opcode for SCSI command packet;
		packetWriteBuffer.put((byte) (opcode & 0xff));
		if(buffer != null) {
			packetWriteBuffer.position(IUSBSCSI.IUSB_SCSI_PKT_SIZE);
			packetWriteBuffer.put(buffer);
		}
		packetWriteBuffer.position(0);
		IUSBSCSI response = new IUSBSCSI(packetWriteBuffer, true);
		/* Send the IUSB response packet */
		packetMaster.sendPacket(response);
	}

	/**
	 * return client is VMAPP or JViewer.
	 * @return
	 */
	public static int getClientType( ) {
		if( JViewer.isVMApp() == true){
			return IUSBRedirSession.VMAPP;
		}else{
			return IUSBRedirSession.JVIEWER;
		}
	}

	/**
	* send file (image/drive) name and client type to the server
	* @param filepath - filepath of the redirected image
	**/

	public static void SendMediaInfo(PacketMaster packetMaster, ByteBuffer packetWriteBuffer, String filepath) throws RedirectionException, IOException
	{
		int medium = -1;

		if(filepath == null ){
			throw new RedirectionException(LocaleStrings.getString("6_9_IUSBREDIR")+ LocaleStrings.getString("6_6_IUSBREDIR"));
		}

		if( filepath.length() == 0 || filepath.length() > IUSBRedirSession.MAX_IMAGE_LENGTH ) {
			throw new RedirectionException(LocaleStrings.getString("6_9_IUSBREDIR")+ LocaleStrings.getString("6_6_IUSBREDIR"));
		}

		//get file name from filpath
		if( System.getProperty("os.name").startsWith("Windows") )
		{
			if( filepath.lastIndexOf('\\') != -1){
				filepath = filepath.substring(filepath.lastIndexOf('\\')+1, filepath.length() );
			}
		}
		else if( System.getProperty("os.name").startsWith("Linux") )
		{
			if(filepath.lastIndexOf('/') != -1 ){
				filepath = filepath.substring(filepath.lastIndexOf('/')+1, filepath.length() );
			}
		}
		// null treminate file path
		filepath = filepath.concat("\0");

		ByteBuffer buffer = ByteBuffer.allocateDirect(IUSBRedirSession.DEVICE_INFO_MAX_SIZE);

		//set byte order
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		medium = getClientType();
		buffer.putInt(medium);
		buffer.put(filepath.getBytes());
		buffer.position(0);

		byte[] arr = new byte[buffer.remaining()];
		buffer.get(arr);

		sendCommandToMediaServer(packetMaster, packetWriteBuffer, arr, IUSBSCSI.DEVICE_INFO);
	}
}
