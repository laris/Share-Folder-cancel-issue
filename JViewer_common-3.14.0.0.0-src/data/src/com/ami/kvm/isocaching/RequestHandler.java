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

import com.ami.kvm.imageredir.cd.CDImage;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.isocaching.ReadAheadCache;

/**
 * The Request Handler thread thread reads the sectors from the ISO file and
 * adds it to the bank. It also notifies the Cache Manager thread to clean up
 * cache banks when required.
 */
public class RequestHandler extends Thread{

	private boolean running = false;
	private ReadAheadCache readAheadCache = null;
	private boolean sectorMiss = false;
	private long missingStartSector;	

	public RequestHandler(ReadAheadCache readAheadCache){
		this.readAheadCache = readAheadCache;
	}

	public void run(){
		while(running){
			if(isSectorMiss()){
				byte[] sectorData = new byte[CDImage.CD_BLOCK_SIZE * ReadAheadCache.MAX_BANK_SIZE];
				long startingSector = missingStartSector;
				int numSectors = ReadAheadCache.MAX_BANK_SIZE;
				setSectorMiss(false);
				if(readAheadCache.getCacheManager().getFreeBankCount() <= 0 &&
						readAheadCache.getSectorDataCache().size() > 0){
					readAheadCache.getCacheManager().setCleanUpCache(true);

					synchronized(readAheadCache.getCacheLock()){
						readAheadCache.getCacheLock().notifyAll();
						if(readAheadCache.isCacheLocked()){
							try {
								readAheadCache.getCacheLock().wait();
							} catch (InterruptedException e) {
								Debug.out.println(e);
							}
						}
					}
				}
				int count = 0;
				int freeBanks = readAheadCache.getCacheManager().getFreeBankCount();
				for(count = 0; count < freeBanks; count ++){
					try {
						//If the image file is being already read, wait until notified by the
						//other thread which is accessing the file.
						if(readAheadCache.isFileReadWait()){
							synchronized (readAheadCache.getFileLock()) {
								try {
									readAheadCache.getFileLock().wait();
									sectorData = readAheadCache.readFromFile(startingSector, numSectors);
									readAheadCache.addToCache(sectorData, startingSector);
									startingSector = (startingSector+numSectors);
								} catch (InterruptedException e) {
									Debug.out.println(e);
								}
							}
						}
					} catch (IOException e) {
						Debug.out.println(e);
						break;
					}
					if(isSectorMiss())
					{
						count++;
						break;
					} 
				}
				readAheadCache.getCacheManager().setFreeBankCount(readAheadCache.getCacheManager().getFreeBankCount() - (count));
			}
			synchronized (readAheadCache.getReqSync()) {
				try {
					readAheadCache.getReqSync().wait();
				} catch (InterruptedException e) {
					Debug.out.println(e);
				}
			}
		}
	}

	public void startThread(){
		setRunning(true);
		this.start();
	}
	public void stopThread(){
		setRunning(false);
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @return the sectorMiss
	 */
	public boolean isSectorMiss() {
		return sectorMiss;
	}

	/**
	 * @param sectorMiss the sectorMiss to set
	 */
	public void setSectorMiss(boolean sectorMiss) {
		this.sectorMiss = sectorMiss;
	}

	public void setMissingBankInfo(long startingSector){
		this.missingStartSector = startingSector;
	}

}
