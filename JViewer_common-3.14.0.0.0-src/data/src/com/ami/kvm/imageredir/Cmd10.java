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
public class Cmd10 {
	private byte reserved6;
	private short length;
	private byte reserved9[]; // size is 3

	public Cmd10(){
		reserved9 = new byte[3];
	}
	public byte getReserved6() {
		return reserved6;
	}
	public void setReserved6(byte reserved6) {
		this.reserved6 = reserved6;
	}
	public short getLength() {
		return length;
	}
	public void setLength(short length) {
		this.length = length;
	}
	public byte[] getReserved9() {
		return reserved9;
	}
	public void setReserved9(byte reserved9[]) {
		this.reserved9 = reserved9;
	}

}