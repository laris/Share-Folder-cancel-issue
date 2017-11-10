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

public class SCSIStatusPacket {

	private byte overAllStatus;
	private byte senseKey;
	private byte senseCode;
	private byte senseCodeQ;

	public SCSIStatusPacket(SCSIStatusPacket packet)
	{
		this.setOverAllStatus(packet.getOverAllStatus());
		this.setSenseKey(packet.getSenseKey());
		this.setSenseCode(packet.getSenseCode());
		this.setSenseCodeQ(packet.getSenseCodeQ());
	}

	public SCSIStatusPacket() {

	}

	public SCSIStatusPacket(ByteBuffer request){

		this.setOverAllStatus(request.get());
		this.setSenseKey(request.get());
		this.setSenseCode(request.get());
		this.setSenseCodeQ(request.get());

	}

	public byte getOverAllStatus() {
		return overAllStatus;
	}

	public void setOverAllStatus(int overAllStatus) {
		this.overAllStatus = (byte)overAllStatus;
	}

	public byte getSenseKey() {
		return senseKey;
	}

	public void setSenseKey(int senseKey) {
		this.senseKey = (byte)senseKey;
	}

	public byte getSenseCode() {
		return senseCode;
	}

	public void setSenseCode(int senseCode) {
		this.senseCode = (byte)senseCode;
	}

	public byte getSenseCodeQ() {
		return senseCodeQ;
	}

	public void setSenseCodeQ(int senseCodeQ) {
		this.senseCodeQ = (byte)senseCodeQ;
	}


}
