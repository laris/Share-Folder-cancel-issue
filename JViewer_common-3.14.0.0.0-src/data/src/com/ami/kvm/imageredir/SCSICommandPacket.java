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

public class SCSICommandPacket {

	public static final int SCSI_TEST_UNIT_READY = 0x00;
	public static final int SCSI_READ_CAPACITY = 0x25;
	public static final int SCSI_READ_10 = 0x28;
	public static final int SCSI_READ_12 = 0xA8;
	public static final int SCSI_READ_TOC = 0x43;
	public static final int SCSI_START_STOP_UNIT = 0x1B;
	public static final int SCSI_MEDIUM_REMOVAL = 0x1E;

	private int opCode;
	private byte lun;
	private int lba;
	private Cmd10 cmd10;
	private Cmd12 cmd12;

	public SCSICommandPacket(SCSICommandPacket packet)
	{
		this.setOpCode(packet.getOpCode());
		this.setLun(packet.getLun());
		this.setLba(packet.getLba());
		this.getCmd10().setReserved6(packet.getCmd10().getReserved6());
		this.getCmd10().setLength(packet.getCmd10().getLength());
		System.arraycopy(packet.getCmd10().getReserved9(), 0, this.getCmd10().getReserved9(), 0, packet.getCmd10().getReserved9().length);
		this.getCmd12().setLength32(packet.getCmd12().getLength32());
		System.arraycopy(packet.getCmd12().getReserved10(), 0, this.getCmd12().getReserved10(), 0, packet.getCmd12().getReserved10().length);
	}

	public SCSICommandPacket(ByteBuffer request)
	{

		//read opcode
		this.setOpCode((int)request.get() & 0xff);

		//read lun
		this.setLun(request.get());

		// read lba
		this.setLba(request.getInt());



		// based on the opcode value read assign values to cmd10 or cmd12
		if(this.getOpCode() == SCSI_READ_10 || this.getOpCode() == SCSI_READ_TOC)
		{
			this.setCmd10(new Cmd10());
			// creating object to access Cmd10 members
			this.getCmd10().setReserved6(request.get());

			//read length
			this.getCmd10().setLength(request.getShort());

			//read reserved9
			int bufferLength = getCmd10().getReserved9().length;
			byte[] test = new byte[bufferLength]; // size of reserved9 is 3
			this.getCmd10().setReserved9(new byte[bufferLength]);
			request.get(test, 0, bufferLength);

			for(int x = 0; x < test.length; x++)
			{
				this.getCmd10().getReserved9()[x] = test[x];
			}
		}
		else if(this.getOpCode() == SCSI_READ_12){
			// creating object to access Cmd12 members
			this.setCmd12(new Cmd12());
			this.getCmd12().setLength32(request.getInt());

			//read reserved10
			int bufferLength = getCmd12().getReserved10().length;
			byte[] testReserve = new byte[bufferLength]; // size of reserved10 is 2
			this.getCmd12().setReserved10(new byte[bufferLength]);
			request.get(testReserve, 0, bufferLength);

			for(int x = 0; x < testReserve.length; x++)
			{
				this.getCmd12().getReserved10()[x] = testReserve[x];

			}

		}

	}

	public SCSICommandPacket() {

	}

	public Cmd10 getCmd10() {
		return cmd10;
	}

	public void setCmd10(Cmd10 cmd10) {
		this.cmd10 = cmd10;
	}

	public Cmd12 getCmd12() {
		return cmd12;
	}

	public void setCmd12(Cmd12 cmd12) {
		this.cmd12 = cmd12;
	}

	public int getOpCode() {
		return opCode;
	}

	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}

	public byte getLun() {
		return lun;
	}

	public void setLun(byte lun) {
		this.lun = lun;
	}

	public int getLba() {
		return lba;
	}

	public void setLba(int lba) {
		this.lba = lba;
	}


}