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
import java.nio.BufferUnderflowException;
import java.util.LinkedList;

import com.ami.kvm.imageredir.cd.CDImage;
import com.ami.kvm.jviewer.Debug;

/**
 * This thread performs the following
 *	#	Initializes Request Handler and Cache Manager threads.
 *	#	Handles the read requests from the server.
 *	#	Reads the sectors needed from the bank.
 *	#	After reading the sectors, the sector data cache is rearranged such that the accessed sector bank appears in the first position of the cache.
 *	#	Notifies Request Handler thread in case of a sector miss.
 */
public class ReadAheadCache extends Thread{

	public static final int MAX_CACHE_SIZE = 256;
	public static final int MAX_BANK_SIZE = 64;
	private LinkedList<SectorBank> sectorDataCache = null;

	private RequestHandler requestHandler = null;
	private CacheManager cacheManager = null;
	private ISOReader isoReader = null;
	private RandomAccessFile imageFile = null;
	private boolean running = false;
	private boolean cacheLocked = false;
	private boolean fileReadWait = false;
	private byte[] sectorData = null;
	private final Object cacheSync = new Object();
	private final Object readSync = new Object();
	private final Object cacheLock = new Object();
	private final Object fileLock = new Object();
	private final Object reqSync = new Object();
	private int numSectors;
	private long startingSector;

	/**
	 * Constructor
	 */
	public ReadAheadCache(RandomAccessFile imageFile) {
		this.imageFile = imageFile;
		if(getSectorDataCache() == null)
			setSectorDataCache(new LinkedList<SectorBank>());
		if(requestHandler == null){
			requestHandler = new RequestHandler(this);
			requestHandler.startThread();
			requestHandler.setName("RequestHandler");
		}
		if(cacheManager == null){
			cacheManager = new CacheManager(this);
			cacheManager.startThread();
			cacheManager.setName("CacheManager");
		}
		isoReader = new ISOReader();
	}

	public void run(){
		while(running){
			synchronized (cacheSync) {
				try {
					sectorData = readSectorData();
					synchronized (readSync) {
						readSync.notifyAll();
					}
					cacheSync.wait();
				} catch (InterruptedException e) {
					Debug.out.println(e);
				}
			}
		}
	}

	/**
	 * Reads the requested sector data from cache.
	 * If the data is not available in cache, then it is read directly from the file.
	 * @return - a byte array containing the requested sector data.
	 */
	public byte[] readSectorData(){
		byte[] data = null;
		int dataBufferSize = CDImage.CD_BLOCK_SIZE * numSectors;
		long sectorKey = getSectorKey(startingSector);
		int offset = (int) (CDImage.CD_BLOCK_SIZE *(startingSector - sectorKey));
		byte[] availableBuffer = null;
		byte[] remainingBuffer = null;
		if(sectorKey >= 0){
			if(isCacheLocked() == false){
				SectorBank sectorBank = getSectorBank(sectorKey);
				data = new byte[dataBufferSize];
				if((startingSector + numSectors) > (sectorKey + MAX_BANK_SIZE)){
					int excess = (int) ((startingSector + numSectors) - (sectorKey + MAX_BANK_SIZE));
					int available = (int) ((sectorKey + MAX_BANK_SIZE) - startingSector);
					long startRemaining = (startingSector + available);
					int availableBufferSize = CDImage.CD_BLOCK_SIZE * available;
					int remainingBufferSize = CDImage.CD_BLOCK_SIZE * excess;
					byte[] dataBuffer = sectorBank.getSectorData();

					availableBuffer = new byte[availableBufferSize];
					remainingBuffer = new byte[remainingBufferSize];
					if(dataBuffer != null){
						if(dataBuffer.length >= (offset + availableBuffer.length)){
							System.arraycopy(dataBuffer, offset, availableBuffer, 0, availableBuffer.length);
							rearrangeSectorBank(sectorBank);
						}
						else
							availableBuffer = null;
					}
					sectorKey = getSectorKey(startRemaining);
					if(sectorKey >= 0){
						offset = (int) (CDImage.CD_BLOCK_SIZE *(startRemaining - sectorKey));
						try{
							sectorBank = getSectorBank(sectorKey);
							byte[] remainingDataBuffer = getSectorBank(sectorKey).getSectorData();
							if(remainingDataBuffer.length >= (offset + remainingBuffer.length)){
								System.arraycopy(remainingDataBuffer, offset, remainingBuffer, 0, remainingBuffer.length);
								rearrangeSectorBank(sectorBank);
							}
							else
								remainingBuffer = null;
						}catch(NullPointerException npe){
							Debug.out.println(npe);
							remainingBuffer = null;
						}catch (BufferUnderflowException e) {
							remainingBuffer = null;
							Debug.out.println(e);
						}
					}
					else
						remainingBuffer = null;
				}
				if(availableBuffer != null && remainingBuffer != null){
					System.arraycopy(availableBuffer, 0, data, 0, availableBuffer.length);
					System.arraycopy(remainingBuffer, 0, data, availableBuffer.length, remainingBuffer.length);
				}
				else{
					sectorKey = getSectorKey(startingSector);
					if(sectorKey >= 0){
						byte[] dataBuffer = null;
						try{
							sectorBank = getSectorBank(sectorKey);
							dataBuffer = sectorBank.getSectorData();
						}catch(NullPointerException npe){
							dataBuffer = null;
							Debug.out.println(npe);
						}

						if(dataBuffer != null){
							if(dataBuffer.length >= (offset+dataBufferSize)){
								try{
									System.arraycopy(dataBuffer, offset, data, 0, dataBufferSize);
									rearrangeSectorBank(sectorBank);
								}
								catch(ArrayIndexOutOfBoundsException ae){
									Debug.out.println(ae);
									data = null;
								}
								catch(Exception e){
									Debug.out.println(e);
									data = null;
								}
							}
							else
								data = null;
						}
						else
							data = null;
					}
				}
			}
			else
				data = null;
		}
		if(data == null){
			try {
				//Set the missing sector data and set the sectorMiss flag to true,
				//so that it will stop the RequestHandler thread from filling the cache,
				//and wait until notified.
				getRequestHandler().setMissingBankInfo((startingSector/ReadAheadCache.MAX_BANK_SIZE) * ReadAheadCache.MAX_BANK_SIZE);
				getRequestHandler().setSectorMiss(true);
				data = new byte[dataBufferSize];
				data = readFromFile(startingSector, numSectors);
			} catch (IOException e) {
				data = null;
				Debug.out.println(e);
			}
			synchronized (getReqSync()) {
				getReqSync().notifyAll();
			}
		}
		return data;
	}

	/**
	 * @return the sectorData
	 */
	public byte[] getSectorData() {
		return sectorData;
	}

	/**
	 * Gets the sector key corresponding to the cache bank,
	 * which contains the data starting from the given sector number.
	 * @param startingSector - the sector for which the key should be identified.
	 * @return - the sector key.
	 */
	private long getSectorKey(long startingSector){
		long sectorKey = (startingSector/ReadAheadCache.MAX_BANK_SIZE) * ReadAheadCache.MAX_BANK_SIZE;
		if(getSectorBank(sectorKey) == null){
			sectorKey = -1;
		}
		return sectorKey;
	}

	/**
	 * Adds sector data to the cache bank.
	 * @param sectorData - the data to be added to the cache bank.
	 * @param startingSector - the starting sector number to be used
	 * as the sector bank key
	 * @return true if success and false otherwise
	 */
	public boolean addToCache(byte[] sectorData, long startingSector){
		boolean status = false;
		if((getSectorDataCache()).size() <= MAX_CACHE_SIZE) {
			if(getSectorBank(startingSector) == null) {
				SectorBank sectorBank = new SectorBank();
				sectorBank.setSectorKey(startingSector);
				sectorBank.setSectorData(sectorData);
				getSectorDataCache().addFirst(sectorBank);
				status = true;
			}
		}

		return status;
	}

	/**
	 * Gets the sector bank object corresponding to a sector key
	 * @param sectorKey - the sector key of the sector bank object in the cache.
	 * @return SectorBank object.
	 */
	public SectorBank getSectorBank(long sectorKey){
		SectorBank sectorBank = null;
		try{
			for(int index =0; index < getSectorDataCache().size(); index++){
				SectorBank temp = getSectorDataCache().get(index);
				if(temp.getSectorKey() == sectorKey){
					sectorBank = temp;
					break;
				}
			}
		}catch(Exception e){
			Debug.out.println(e);
			sectorBank = null;
		}
		return sectorBank;
	}

	/**
	 * Moves a particular sector bank to the top of the cache, there by sorting
	 * the cache in the order of most recently accessed banks.
	 * @param sectorBank - the sector bank object to be rearranged.
	 */
	public void rearrangeSectorBank(SectorBank sectorBank){
		if(sectorBank != null){
			int sectorBankIndex = getSectorDataCache().indexOf(sectorBank);
			if( sectorBankIndex >=0 && sectorBankIndex < getSectorDataCache().size()){
				try{
					SectorBank element = getSectorDataCache().remove(sectorBankIndex);
					getSectorDataCache().addFirst(element);
				}catch(Exception e){
					Debug.out.println(e);
				}
			}
		}
	}

	/**
	 * Read data from Image file
	 * @param startingSector - the sector number from which reading should start.
	 * @param numSectors - the number of sectors to be read from the given sector.
	 * @return - a byte array containing the requested sector data.
	 * @throws IOException
	 */
	public byte[] readFromFile(long startingSector, int numSectors)
			throws IOException{
		byte[] sectorData = new byte[CDImage.CD_BLOCK_SIZE * numSectors];
		if(imageFile != null){
			//When the ReadAheadCache thread is reading data from file,set the flag to true.
			//This will make the RequestHandler thread stop filling the cache and wait
			//until notified. This will prevent data corruption.
			fileReadWait = true;
			synchronized (fileLock) {
				if(isoReader == null)
					isoReader = new ISOReader();
				sectorData = isoReader.readSectorData(imageFile, startingSector, numSectors);
				//Once file read is finished, set the fileReadWait flag to false and notify the
				//waiting thread.
				fileReadWait = false;
				fileLock.notifyAll();
			}
		}
		return sectorData;
	}

	/**
	 * @return the sectorDataCache
	 */
	public LinkedList<SectorBank> getSectorDataCache() {
		return sectorDataCache;
	}

	/**
	 * @param sectorDataCache the sectorDataCache to set
	 */
	public void setSectorDataCache(LinkedList<SectorBank> sectorDataCache) {
		this.sectorDataCache = sectorDataCache;
	}

	/**
	 * @return the cacheManager
	 */
	public RequestHandler getRequestHandler() {
		return requestHandler;
	}

	/**
	 * @return the cacheManager
	 */
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void startThread(){
		running = true;
		this.start();
	}
	public void stopThread(){
		running = false;
	}

	/**
	 * @return the cacheSync
	 */
	public Object getCacheSync() {
		return cacheSync;
	}

	/**
	 * @return the readSync
	 */
	public Object getReadSync() {
		return readSync;
	}

	public void setSectorRequest(long startingSector, int numSectors){
		this.startingSector = startingSector;
		this.numSectors = numSectors;
	}

	/**
	 * @return the cacheLocked
	 */
	public boolean isCacheLocked() {
		return cacheLocked;
	}

	/**
	 * @param cacheLocked the cacheLocked to set
	 */
	public void setCacheLocked(boolean cacheLocked) {
		this.cacheLocked = cacheLocked;
	}

	/**
	 * @return the cacheLock
	 */
	public Object getCacheLock() {
		return cacheLock;
	}

	/**
	 * @return the fileLock
	 */
	public Object getFileLock() {
		return fileLock;
	}

	/**
	 * @return the fileReadWait
	 */
	public boolean isFileReadWait() {
		return fileReadWait;
	}

	/**
	 * @return the reqSync object
	 */
	public Object getReqSync() {
		return reqSync;
	}
}
