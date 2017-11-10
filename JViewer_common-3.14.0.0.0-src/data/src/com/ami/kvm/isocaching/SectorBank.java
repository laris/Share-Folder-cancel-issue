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

import java.nio.ByteBuffer;
import java.util.Date;
import com.ami.kvm.imageredir.cd.CDImage;

public class SectorBank {

	public static final int MAX_SECTOR_COUNT = 64;
	public static final long MAX_BUFFER_SIZE = CDImage.CD_BLOCK_SIZE * MAX_SECTOR_COUNT; 

	private long sectorKey;
	private byte[] sectorData;

	private final Object lock = new Object();

	/**
	 * Default Constructor
	 */
	public SectorBank() {
	}

	/**
	 * Constructor
	 * This creates an new Date object and sets it as the time stamp for the SectroBank object.<br>
	 * The sector data will be set as the ByteBufer which is passed as parameter.
	 * @param sectorDataBuffer The sector data byte buffer to be set
	 */
	public SectorBank(ByteBuffer sectorDataBuffer) {
		setSectorData(sectorData);
	}

	/**
	 * Constructor
	 * @param timeStamp The time stamp to be set.
	 * @param sectorDataBuffer The sector data byte buffer to be set.
	 */
	public SectorBank(Date timeStamp, ByteBuffer sectorDataBuffer) {
		setSectorData(sectorData);
	}


	/**
	 * @return the sectorKey
	 */
	public long getSectorKey() {
		return sectorKey;
	}

	/**
	 * @param sectorKey the sectorKey to set
	 */
	public void setSectorKey(long sectorKey) {
		this.sectorKey = sectorKey;
	}

	/**
	 * @return the sectorData
	 */
	public byte[] getSectorData() {
		synchronized (lock) {
			return sectorData;
		}
	}

	/**
	 * @param sectorData the sectorData to set
	 */
	public void setSectorData(byte[] sectorData) {
		synchronized (lock) {
			this.sectorData = sectorData;
		}
	}
}
