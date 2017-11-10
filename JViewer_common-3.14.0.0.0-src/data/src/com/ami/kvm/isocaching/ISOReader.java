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

package com.ami.kvm.isocaching;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.ami.kvm.imageredir.cd.CDImage;
import com.ami.kvm.jviewer.Debug;

public class ISOReader{

	/**
	 * Reads the requested number of sectors form the image file, 
	 * starting from a given sector and returns the data as a byte array. 
	 * @param imageFile - the image file from which data is to be read.
	 * @param startSector - the sector number from which reading should start.
	 * @param numSectors - the number of sectors to be read from the given sector.
	 * @return - a byte array containing the requested sector data.
	 * @throws IOException
	 */
	public byte[] readSectorData(RandomAccessFile imageFile, long startSector, int numSectors) 
			throws IOException{
		byte readData[] = null;
		if(imageFile != null){
			readData = new byte[CDImage.CD_BLOCK_SIZE * numSectors];
			try {
				imageFile.seek(CDImage.CD_BLOCK_SIZE * startSector);
				imageFile.read(readData);
			} catch (Exception e) {
				Debug.out.println(e);
			}
		}
		return readData;
	}
}