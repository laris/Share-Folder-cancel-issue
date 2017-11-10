/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2014, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
 ****************************************************************/

package com.ami.kvm.imageredir;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.ami.iusb.RedirProtocolException;
import com.ami.iusb.protocol.RedirHeader;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.gui.LocaleStrings;
public class IUSBHeader extends RedirHeader{

	public static final int IUSB_SIGNATURE_SIZE = 8;// IUSB Header signature size is 8 bytes
	public static final int IUSB_HEADER_SIZE = 61; // IUSB Header size is 61 bytes
	public static final int HEADER_LEN = 32;
	private static final String IUSB_HEADER = "IUSB    "; 
	private static final int IUSB_MAJOR = 1;
	private static final int IUSB_MINOR = 0;
	private byte signature[];
	private int major;
	private int minor;
	private int headerCheckSum;
	private long dataPacketLen;
	private int serverCaps;
	private int deviceType;
	private int protocol;
	private int direction;
	private int deviceNo;
	private int interfaceNo;
	private int clientData;
	private int instance;
	private long sequenceNo;
	private byte[] key;

	public IUSBHeader(IUSBHeader tempHeader){
		this.setSignature(new byte[IUSB_SIGNATURE_SIZE]);
		System.arraycopy(tempHeader.getSignature(), 0, this.getSignature(), 0, tempHeader.getSignature().length);
		this.setMajor(tempHeader.getMajor());
		this.setMinor(tempHeader.getMinor());
		this.setHeaderLen(tempHeader.getHeaderLen());
		this.setHeaderCheckSum(tempHeader.getHeaderCheckSum());
		this.setDataPktLen(tempHeader.getDataPktLen());
		this.setServerCaps(tempHeader.getServerCaps());
		this.setDeviceType(tempHeader.getDeviceType());
		this.setProtocol(tempHeader.getProtocol());
		this.setDirection(tempHeader.getDirection());
		this.setDeviceNo(tempHeader.getDeviceNo());
		this.setInterfaceNo(tempHeader.getInterfaceNo());
		this.setClientData(tempHeader.getClientData());
		this.setInstance(tempHeader.getInstance());
		this.setSeqNo(tempHeader.getSeqNo());
		this.setKey(tempHeader.getKey());
	}

	/**
	 *  Creates an empty IUSBHeader
	 *
	 */
	public IUSBHeader()
	{
		headerLen = HEADER_LEN;
		this.signature = new byte[IUSB_SIGNATURE_SIZE];
		this.key = new byte[4];
	}

	public IUSBHeader(int dataLen) {
		this.signature = IUSB_HEADER.getBytes();
		this.headerLen = HEADER_LEN;
		this.major = IUSB_MAJOR;
		this.minor = IUSB_MINOR;
		this.sequenceNo = 0; //FIXME...or not.  Seems to be ignored.
		this.direction = 0x80;
		this.dataPacketLen = dataLen;
		this.deviceType = 0x05;
		this.deviceNo = 0;
		this.interfaceNo = 0;
		this.protocol = 0x01;
		this.key = new byte[4];
	}

	public IUSBHeader(ByteBuffer request) {
		byte[] testByte = new byte[IUSB_SIGNATURE_SIZE];
		this.setSignature(new byte[IUSB_SIGNATURE_SIZE]);
		key = new byte[4];

		// copy data into signature array
		request.get(testByte, 0, IUSB_SIGNATURE_SIZE);
		// skip the distance of header object and copy int array to other members
		for(int x = 0; x < IUSB_SIGNATURE_SIZE; x++)
		{
			this.signature[x] = testByte[x];
		}

		major = ( (int)request.get() ) & 0xff;

		minor = ( (int)request.get() ) & 0xff;

		headerLen = ( (int)request.get() ) & 0xff;

		headerCheckSum = ( (int)request.get() ) & 0xff;

		dataPacketLen = ( (long)request.getInt() ) & 0xffffffff;

		serverCaps = ( (int)request.get() ) & 0xff;

		deviceType = ( (int)request.get() ) & 0xff;

		protocol = ( (int)request.get() ) & 0xff;

		direction = ( (int)request.get() ) & 0xff;

		deviceNo = ( (int)request.get() ) & 0xff;

		interfaceNo = ( (int)request.get() ) & 0xff;

		clientData =  ( (int)request.get() ) & 0xff;

		instance  = ( (int)request.get() ) & 0xff;

		sequenceNo = ( (long)request.getInt() ) & 0xffffffff;

		request.get( key );
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte signature[]) {
		this.signature = signature;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major =  major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getHeaderLen() {
		return headerLen;
	}

	public void setHeaderLen(int headerLen) {
		this.headerLen = headerLen;
	}

	public int getHeaderCheckSum() {
		return headerCheckSum;
	}

	public void setHeaderCheckSum(int headerCheckSum) {
		this.headerCheckSum =  headerCheckSum;
	}

	public long getDataPktLen() {
		return dataPacketLen;
	}

	public void setDataPktLen(long l) {
		this.dataPacketLen = l;
	}

	public int getServerCaps() {
		return serverCaps;
	}

	public void setServerCaps(int serverCaps) {
		this.serverCaps =  serverCaps;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType =  deviceType;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol =  protocol;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction =  direction;
	}

	public int getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(int deviceNo) {
		this.deviceNo =  deviceNo;
	}

	public int getInterfaceNo() {
		return interfaceNo;
	}

	public void setInterfaceNo(int interfaceNo) {
		this.interfaceNo =  interfaceNo;
	}

	public int getClientData() {
		return clientData;
	}

	public void setClientData(int clientData) {
		this.clientData =  clientData;
	}

	public int getInstance() {
		return instance;
	}

	public void setInstance(int instance) {
		this.instance =  instance;
	}

	public long getSeqNo() {
		return sequenceNo;
	}

	public void setSeqNo(long l) {
		this.sequenceNo = l;
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	/**
	 * Received request SCSI packet parsing the data fro the request buffer
	 */
	public void read( ByteBuffer rawBuffer ) throws RedirProtocolException, BufferUnderflowException
	{
		rawBuffer.get( signature );
		String signatureString = new String(signature);
		if( !signatureString.equals( "IUSB    ") ) {
			Debug.out.dump(signature);
			throw new RedirProtocolException(LocaleStrings.getString("7_1_IUSBH"));
		}

		major = ( (int)rawBuffer.get() ) & 0xff;

		minor = ( (int)rawBuffer.get() ) & 0xff;

		headerLen = ( (int)rawBuffer.get() ) & 0xff;

		headerCheckSum = ( (int)rawBuffer.get() ) & 0xff;

		dataPacketLen = ( (long)rawBuffer.getInt() ) & 0xffffffff;

		serverCaps = ( (int)rawBuffer.get() ) & 0xff;

		deviceType = ( (int)rawBuffer.get() ) & 0xff;

		protocol = ( (int)rawBuffer.get() ) & 0xff;

		direction = ( (int)rawBuffer.get() ) & 0xff;

		deviceNo = ( (int)rawBuffer.get() ) & 0xff;

		interfaceNo = ( (int)rawBuffer.get() ) & 0xff;

		clientData =  ( (int)rawBuffer.get() ) & 0xff;

		instance  = ( (int)rawBuffer.get() ) & 0xff;

		sequenceNo = ( (long)rawBuffer.getInt() ) & 0xffffffff;

		rawBuffer.get( key );
	}

	/**
	 * Sending the SCSI packet to the HOST Forming and
	 *  filling the SCSI packet with needed information
	 */
	public void write( ByteBuffer rawBuffer )
	{
		rawBuffer.put( signature );
		rawBuffer.put( (byte)( major & 0xff ) );
		rawBuffer.put( (byte)( minor & 0xff ) );
		rawBuffer.put( (byte)( headerLen & 0xff ) );
		rawBuffer.put( (byte)( headerCheckSum & 0xff ) );
		rawBuffer.putInt( (int)( dataPacketLen & 0xffffffff ) );
		rawBuffer.put( (byte)( serverCaps & 0xff ) );
		rawBuffer.put( (byte)( deviceType & 0xff ) );
		rawBuffer.put( (byte)( protocol & 0xff ) );
		rawBuffer.put( (byte)( direction & 0xff ) );
		rawBuffer.put( (byte)( deviceNo & 0xff ) );
		rawBuffer.put( (byte)( interfaceNo & 0xff ) );
		rawBuffer.put( (byte)( clientData & 0xff ) );
		rawBuffer.put( (byte)( instance & 0xff ) );
		rawBuffer.putInt( (int)( sequenceNo & 0xffffffff ) );
		rawBuffer.put( key );

		/* Calculate modulo100 checksum */
		int temp = 0;
		for( int i = 0; i < rawBuffer.limit(); i++ )
			temp = ( temp + (int)( rawBuffer.get( i ) & 0xff ) ) & 0xff;

		/* Plug the checksum into the header */
		rawBuffer.put( 11, (byte)-( (byte)( temp & 0xff ) ) );
	}
}
