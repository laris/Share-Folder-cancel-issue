/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2017, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
 ****************************************************************/

package com.ami.iusb;

import java.nio.*;
import java.io.*;

public class RootDirectory
{
	public byte[] filename = new byte[8];
	public byte[] ext = new byte[3];
	public byte attributes = 0;
	public byte upper = 0;//upper or lower, 0x0  all upper, 0x18 all lower, 
						//0x08 name upper ext lower, 0x04 name lower ext upper

	public byte create_millsec = 0;
	public short create_time = 0;
	public short create_date = 0;
	public short access_date = 0;
	public long fat32_reserved = 0;
	public short modify_time = 0;
	public short modify_date = 0;
	public long start_cluster = 0;
	public int file_size = 0;

	public RootDirectory()
	{
		for(int i = 0 ; i < 8 ; i++)
			filename[i] = 0x20;
		
		for(int i = 0 ; i < 3 ; i++)
			ext[i] = 0x20;
		attributes = 0;
		upper = 0;

		create_millsec = 0;
		create_time = 0;
		create_date = 0;
		access_date = 0;
		fat32_reserved = 0;
		modify_time = 0;
		modify_date = 0;
		start_cluster = 0;
		file_size = 0;
	}

	/*read 32 byte for root directory from image file*/
	public void readFileRootDirectory(RandomAccessFile pImg, long rootStartAddr)
	{
		try {
			pImg.seek(rootStartAddr);
			for(int i = 0 ; i < 8 ; i++) {				
				filename[i] = (byte)pImg.readUnsignedByte();
			}
			for(int i = 0 ; i < 3 ; i++) {
				ext[i] = (byte)pImg.readUnsignedByte();
			}

			attributes = (byte)pImg.readUnsignedByte();
			upper = (byte)pImg.readUnsignedByte();
			create_millsec = (byte)pImg.readUnsignedByte(); 

			create_time = (short)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
			create_date = (short)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
			access_date = (short)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
			fat32_reserved = (short)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
			modify_time = (short)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
			modify_date = (short)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
			start_cluster = (long)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
			file_size = MasterBootRecord.ChangeToLittleEndian(pImg.readInt(),4, MasterBootRecord.LITTLE_ENDIAN_TYPE);
	
		}
		catch(IOException e)
		{
			System.out.println("readFileRootDirectory() => " + e);
		}
	}
}
