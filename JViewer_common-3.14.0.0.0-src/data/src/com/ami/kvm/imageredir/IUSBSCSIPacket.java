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


import java.nio.ByteBuffer;

public class IUSBSCSIPacket {

	private IUSBHeader  header;
	private int	readLen;
	private int tagNo;
	private byte dataDir;
	private SCSICommandPacket commandPkt;
	private SCSIStatusPacket statusPkt;
	private int	dataLen;
	private byte data;
	private int[] values;

	public IUSBSCSIPacket(ByteBuffer request)
	{
		request.rewind(); // to reset the position
		this.setHeader(new IUSBHeader(request));
		this.setReadLen(request.getInt());
		this.setTagNo(request.getInt());
		this.setDataDir(request.get());
		this.setCommandPkt(new SCSICommandPacket(request));
		this.setStatusPkt(new SCSIStatusPacket(request));
		this.setDataLen(request.getInt());
		request.rewind();
	}

	public int getReadLen() {
		return readLen;
	}


	public void setReadLen(int readLen) {
		this.readLen = readLen & 0xff;
	}


	public int getTagNo() {
		return tagNo;
	}


	public void setTagNo(int tagNo) {
		this.tagNo = tagNo & 0xff;
	}


	public int getDataDir() {
		return dataDir;
	}


	public void setDataDir(int dataDir) {
		this.dataDir = (byte) dataDir;
	}


	public int getDataLen() {
		return dataLen;
	}


	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}


	public int getData() {
		return data;
	}


	public void setData(int data) {
		this.data = (byte) data;
	}


	public IUSBHeader getHeader() {
		return header;
	}


	public void setHeader(IUSBHeader header) {
		this.header = header;
	}


	public SCSICommandPacket getCommandPkt() {
		return commandPkt;
	}


	public void setCommandPkt(SCSICommandPacket commandPkt) {
		this.commandPkt = commandPkt;
	}


	public SCSIStatusPacket getStatusPkt() {
		return statusPkt;
	}


	private void setStatusPkt(SCSIStatusPacket statusPkt) {
		this.statusPkt = statusPkt;
	}


	public int[] getValues() {
		return values;
	}


	public void setValues(int[] values) {
		this.values = values;
	}

}
