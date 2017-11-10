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
public class Cmd12 {
		private int length32;
		private byte reserved10[]; // size is 2

		public Cmd12(){
			reserved10 = new byte[2];
		}
		public int getLength32() {
			return length32;
		}
		public void setLength32(int length32) {
			this.length32 = length32;
		}
		public byte[] getReserved10() {
			return reserved10;
		}
		public void setReserved10(byte reserved10[]) {
			this.reserved10 = reserved10;
		}

}