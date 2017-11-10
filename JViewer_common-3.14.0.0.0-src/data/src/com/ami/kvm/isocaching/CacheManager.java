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

import java.util.NoSuchElementException;

import com.ami.kvm.jviewer.Debug;

/**
 * This threads manages the cache by removing the obsolete cache banks
 * when free slots are required.
 */
public class CacheManager extends Thread{

	public static final int MAX_FREE_BANKS_COUNT = 128;
	private ReadAheadCache readAheadCache = null;
	private int freeBankCount = ReadAheadCache.MAX_CACHE_SIZE;
	private boolean running = false;
	private boolean sortCache = false;
	private boolean cleanUpCache = false;  

	public CacheManager(ReadAheadCache readAheadCache){
		this.readAheadCache = readAheadCache;
	}
	public void run(){
		while(running){
			synchronized (readAheadCache.getCacheLock()) {
				if(isCleanUpCache()){	
					readAheadCache.setCacheLocked(true);
					cleanUpCache();
					readAheadCache.setCacheLocked(false);
					readAheadCache.getCacheLock().notifyAll();
					setCleanUpCache(false);
				}
				try {
					readAheadCache.getCacheLock().wait();
				} catch (InterruptedException e) {
					Debug.out.println(e);
				}
			}
		}
	}

	/**
	 * Removes the obsolete entries from the cache.
	 */
	public void cleanUpCache(){
		int bankCount = 0;
		while(bankCount < MAX_FREE_BANKS_COUNT &&
				readAheadCache.getSectorDataCache().size() > 0){
			try{
				readAheadCache.getSectorDataCache().removeLast().getSectorKey();
			}catch(NoSuchElementException nse){
				Debug.out.println(nse);
				break;
			}catch(Exception e){
				Debug.out.println(e);
				break;
			}
			bankCount++;
		}
		setFreeBankCount(bankCount);
		System.gc();
	}

	public void startThread(){
		running = true;
		this.start();
	}
	public void stopThread(){
		running = false;
	}
	/**
	 * @return the freeBankCount
	 */
	public int getFreeBankCount() {
		return freeBankCount;
	}

	/**
	 * @param freeBankCount the freeBankCount to set
	 */
	public void setFreeBankCount(int freeBankCount) {
		this.freeBankCount = freeBankCount;
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
	 * @return the sortCache
	 */
	public boolean isSortCache() {
		return sortCache;
	}
	/**
	 * @param sortCache the sortCache to set
	 */
	public void setSortCache(boolean sortCache) {
		this.sortCache = sortCache;
	}
	/**
	 * @return the cleanUpCache
	 */
	public boolean isCleanUpCache() {
		return cleanUpCache;
	}
	/**
	 * @param cleanUpCache the cleanUpCache to set
	 */
	public void setCleanUpCache(boolean cleanUpCache) {
		this.cleanUpCache = cleanUpCache;
	}
}
