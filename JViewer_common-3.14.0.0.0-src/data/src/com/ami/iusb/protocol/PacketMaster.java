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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Selector;
import javax.net.ssl.SSLSocket;

import com.ami.iusb.EncryptionException;
import com.ami.iusb.RedirProtocolException;
import com.ami.iusb.RedirectionException;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.communication.Connection;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
/**
 * The PacketMaster class is responsible for sending and receiving packets
 * on a specified host and port.  A {@link #RedirProtocol} object tells it
 * how to convert incoming data into packets of the correct type.  The
 * PacketMaster class also handles SSL encryption/decryption if desired.<BR><BR>
 * To use a PacketMaster, first create one with a constructor.  Then call
 * one of the {@link #setupBuffers} methods to tell the packetMaster how large
 * the buffers it uses should be.  The endianness of the buffers can be specified
 * with {@link #setBufferEndianness}.  Call the {@link #connect} method to
 * establish the connection to the remote host.<BR><BR>
 * At this point, the PacketMaster is ready to send and receive packets via
 * calls to {@link #sendPacket} and {@link #receivePacket}.<BR><BR>
 * When you are done with a PacketMaster, call the {@link #close} method
 * to disconnect the sockets and selectors.
 *
 * @author andrewm@ami.com
 */
public class PacketMaster
{
	private Socket packetSock;
	private Socket Sock;
	private SSLSocket packetSockssl;
	private Selector packetSel;
    private RedirProtocol protocol;
    private ByteBuffer netIn;
    private ByteBuffer netOut;
    private ByteBuffer appIn;
    private ByteBuffer appOut;
    private String host;
    private int port;
	private boolean wakeup = false;

    private static final int IUSB_HDR_SIZE = 32;

    /**
     * Creates a new instance of PacketMaster
     * @param host The host with which this PacketMaster will communicate
     * @param port The port to which we will connect
     * @param blocking True if we should block on socket actions, false for
     * non-blocking socket actions
     * @param sslEncryption True to enable SSL encryption on the socket
     * (not yet tested), false for a normal connection.
     */
    public PacketMaster( String host, int port, boolean blocking, RedirProtocol protocol, boolean sslEncryption )
    {
        /* Save the networking information */
        this.host = host;
        this.port = port;
        /* Save the passed protocol */
        this.protocol = protocol;
    }

    /**
     * Instructs this PacketMaster to allocate input and output buffers
     * of the specified sizes.
     * @param inBufferSize The size of the buffer the PacketMaster should
     * use to read in raw packets from the network
     * @param outBufferSize The size of the buffer the PacketMaster should
     * use to write out raw packets to the network
     */
    public void setupBuffers( int inBufferSize, int outBufferSize )
    {
    	/* Network input and output buffers should be of the specified size */
    	netIn = ByteBuffer.allocateDirect( inBufferSize );
    	netOut = ByteBuffer.allocateDirect( outBufferSize );
    	/* We'return not doing SSL - application buffers are the same as
    	 * the network buffers */
    	appIn = netIn;
    	appOut = netOut;
    	Debug.out.println( "No SSL, allocating only netIn and netOut..." );
    	Debug.out.println( "Allocated netOut buffer of " + netOut.capacity() + " bytes" );
    	Debug.out.println( "Allocated netIn buffer of " + netIn.capacity() + " bytes" );
    }

    /**
     * Instructs this PacketMaster to use the passed buffers for input
     * and ouput from the remote host.
     * @param inBuffer The buffer PacketMaster should use to read packet
     * data from the network
     * @param outBuffer The buffer PacketMaster should use to write packet
     * data onto the network.
     */
    public void setupBuffers( ByteBuffer inBuffer, ByteBuffer outBuffer )
    {
    	/* We'return not doing SSL - application buffers are the same as
    	 * the network buffers */
    	netIn = inBuffer;
    	netOut = outBuffer;
    	/* Use the passed input and output buffers for application data */
    	appIn = netIn;
    	appOut = netOut;
    	/* Clear the passed buffers in preparation for using them */
    	netIn.clear();
    	netOut.clear();
    	Debug.out.println( "No SSL, using provided buffers exclusively" );
    }

    /**
     * Set the endianness of the input and output buffers.  Buffers default
     * to big endian.
     * @param inByteOrder The byte order of the input buffer
     * @param outByteOrder The byte order of the output buffer
     */
    public void setBufferEndianness( ByteOrder inByteOrder, ByteOrder outByteOrder )
    {
        appIn.order( inByteOrder );
        appOut.order( outByteOrder );
    }

    /**
     * Based on the SSL flag call the Connect method
     * @param useSSL - SSl communication true/false
     */
    public int connectVmedia(boolean useSSL)
	{
		try {
			// service variable being passed represents which service calls the createSocket() method
			// service value will be 0, if nonssl socket should be created.
			// Incase of SSLSocket for web requests, service value should be 1
			//Incase of SSLSocket for video/media request, service value should be 2

			int service = (useSSL == true) ? (JViewerApp.KVMService): JViewerApp.NonSSLService;
			// createSocket() will create SSLSocket if useSSL is true, if useSSl is false, nonssl socket will be created.
			packetSock = JViewerApp.getInstance().getConnection().createSocket(InetAddress.getByName(host), port, service);
		} catch (IOException e) {
			Debug.out.println(e);
			return -1;
		}

		if(packetSock == null) {
			if((useSSL) && (JViewerApp.getInstance().getConnection().getConnErrCode() == Connection.SUCCESS)) {
				JViewerApp.getInstance().getVMDialog().disposeVMDialog();
			}
			else {
				return -1;
			}
		}
		setSock(packetSock);
		return 0;
	}

    /**
     * Method return the created socket descriptor
     * @return
     */
	public Socket getSock()
	{
		return Sock;
	}

	/**
	 *	Method sets the creating socket descriptor
	 * @param sock
	 */
	public void setSock(Socket sock)
	{
		Sock = sock;
	}

     /**
     * Close any sockets opened by this PacketMaster
     * @throws IOException on network errors
     */
    public void VmediaSockclose() throws IOException
    {
    	if(JViewer.isSinglePortEnabled())
    	{
    		if(getSock() != null)
    			getSock().close();
    	}
    	else{
    		if(packetSock!= null)
    			packetSock.close();

    		if(packetSockssl != null)
    			packetSockssl.close();
    	}
    }

    /**
     * Interrupt any blocking operation the PacketMaster may currently be
     * undertaking.  This is useful when called from another thread trying
     * to join a thread blocking in a PacketMaster action, usually
     * {@link #receivePacket}.
     */
    public void wakeup()
    {
        wakeup = true;

        if(packetSel.isOpen())
        	packetSel.wakeup();
    }

    /**
     * Send a packet over the network to the remote server.  This method
     * blocks if this PacketMaster has been configured to do so in the constructor.
     * @param packet The filled out RedirPacket to send
     * @throws IOException on network errors
     * @throw EncryptionException on SSL encryption errors (when SSL is enabled)
     */
    public void sendPacket( RedirPacket packet ) throws IOException, EncryptionException
    {
        /* Clear the buffers we're going to use */
    	 /* Clear the buffers we're going to use */
        appOut.clear();
        netOut.clear();
        /* Write the packet data, properly aligned, into the appOut buffer */
        packet.writePacket( appOut );
        appOut.flip();
		/* Send the netOut buffer.  netOut == appOut for non-SSL connections */
    	byte[] write_buf = new byte[netOut.remaining()];
		netOut.get(write_buf);
		getSock().getOutputStream().write(write_buf);
    }

    /**
     * Receive a packet over the network.  The packet is read into the input
     * buffer that was specified through a call to {@link #setupBuffers}, and
     * then converted to an appropriate packet by the passed {@link #RedirProtocol}.
     * @param clearBuffer If true, the raw data corresponding to the pack read
     * will be automatically cleared from the input buffer after the method call.
     * If false, the data will be left in the buffer until a call to
     * {@link #clearBuffer}.
     * @return the received packet
     * @return null if the receive was interrupted by a call to {@link #wakeup}
     * @throws IOException on network errors
     * @throws RedirProtocolException on protocol errors
     * @throws RedirectionException on timeout on read
     */
    public RedirPacket receivePacket( boolean clearBuffer ) throws IOException, RedirectionException
    {
        int rc = 0;
        RedirPacket packet = null;

        /* If we already have data in out buffer... */
        if( appIn.position() != 0 ) {
            /* ...see if we can make a packet from it */
            appIn.flip();
            packet = protocol.getPacket( appIn );
            if( packet == null ) {
                /* This isn't enough to make a whole packet.  New data
                 * should go just after the data already read. */
                appIn.position( appIn.limit() );
                appIn.limit( appIn.capacity() );
            }
            else
            	 Debug.out.println("Got a packet!");
        }

        /* If we don't have a complete packet yet, read more data */
        while( packet == null )
        {
        	byte[] readheader_buf = new byte[IUSB_HDR_SIZE];
        	//Reading the header data
        	rc = read_data(readheader_buf);

        	if(rc < 0)
        		throw new IOException(LocaleStrings.getString("8_2_PACKETMAST"));

        	//Readed header data is converted from byte[] to bytebuffer
        	netIn.put(readheader_buf);
        	netIn.position(12);//setting the buffer position to read the dattapacket length

        	long    dataPacketLen = ( (long)netIn.getInt() ) & 0xffffffff;
        	byte[] readdata_buf = new byte[(int) dataPacketLen];
			 //reading the datta part
        	rc = 0;
        	rc = read_data(readdata_buf);

        	if(rc < 0)
        		throw new IOException(LocaleStrings.getString("8_2_PACKETMAST"));

        	netIn.position(IUSB_HDR_SIZE);
        	//appending the data with header data
        	netIn.put(readdata_buf,0,readdata_buf.length);
        	netIn.position(0);
        	netIn.limit(readheader_buf.length+readdata_buf.length);

        	/* Construct a packet from the data */
        	packet = protocol.getPacket( netIn );
        	if( packet == null ) {
	           Debug.out.println( "Can't build a packet from input data" );
	           appIn.position( appIn.limit() );
	           appIn.limit( appIn.capacity() );
        	}
        	else
	    	   Debug.out.println("Got a packet!");
        }

        if( clearBuffer )
            appIn.compact();

        return( packet );
    }


    /**
     * Receive a packet over the network.  The packet is read into the input
     * buffer that was specified through a call to {@link #setupBuffers}, and
     * then converted to an appropriate packet by the passed {@link #RedirProtocol}.
     * The raw data will be automatically cleared from the buffer.
     * @return the received packet
     * @return null if the receive was interrupted by a call to {@link #wakeup}
     * @throws IOException on network errors
     * @throws RedirProtocolException on protocol errors
     * @throws RedirectionException on timeout on read
     */
    public RedirPacket receivePacket() throws IOException, RedirectionException
    {
        return( receivePacket( true ) );
    }

    /***
     * Clear data that's already been used from the buffer
     *
     */
    public void clearBuffer()
    {
        appIn.compact();
    }

    /**
     * Read the data from the socket descriptor
     * @param read_buf
     * @return
     */
    public int read_data(byte[] read_buf)
	{
    	int    dwIndex = 0;
    	int bytes_to_read = read_buf.length;
    	int m_readIx = 0;
    	//Header read 32 bytes
    	while(bytes_to_read != 0)
    	{
    		try
    		{
    			m_readIx = getSock().getInputStream().read(read_buf, dwIndex, bytes_to_read);
    		}
    		catch (IOException e)
    		{}

    		if(m_readIx <= 0)
    		{
    			System.out.println("SOCKET FAILURE");
    			return m_readIx;
    		}
    		bytes_to_read  = bytes_to_read - m_readIx;
    		dwIndex = dwIndex +  m_readIx;
    	}
    	m_readIx = read_buf.length;
		return m_readIx;
	}
}
