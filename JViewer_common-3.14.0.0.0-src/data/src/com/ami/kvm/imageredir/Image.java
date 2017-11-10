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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

import com.ami.kvm.jviewer.Debug;

public class Image {

	/** @name ERROR_VALUES */

	/** Operation Successful */
	public static final byte SUCCESS				= 0;
	/** Requested Sector is out of range */
	public static final byte SECTOR_RANGE_ERROR		= -1;
	/** Wrong media */
	public static final byte WRONG_MEDIA			= -2;
	/** There was medium change since last operation */
	public static final byte MEDIUM_CHANGE			= -3;
	/** Error occurred while accessing media */
	public static final byte MEDIA_ERROR			= -4;
	/** No Media detected to perform an operation */
	public static final byte NO_MEDIA				= -5;
	/** Invalid request parameters */
	public static final byte INVALID_PARAMS			= -6;
	/** Unable to read media */
	public static final byte UNREADABLE_MEDIA		= -7;
	/** Unable to eject media */
	public static final byte REMOVAL_PREVENTED		= -8;
	/** Command not supported in this version */
	public static final byte UNSUPPORTED_COMMAND	= -9;

	public static final byte DEVICE_PATH_TOO_LONG	= -10;
	public static final byte DEVICE_ERROR			= -11;
	public static final byte DEVICE_ALREADY_OPEN	= -12;

	public static final byte MEDIUM_GETTING_READY	= -14;
	/** Media is already opened and is in use by some other program */
	public static final byte MEDIA_IN_USE			= -15;

	public static int MAX_DEVICE_PATH_SIZE			= 256;

	protected String imageFilePath = null;
	protected RandomAccessFile imageFile = null;
	protected long totalSectors;
	protected long blockSize;
	protected ByteOrder byteOrder;
	protected boolean opened = false;
	protected boolean mediaChange = false;

	FileReader fileReader = null;

	public boolean openImage(File file, String mode){
		boolean ret = true;
		try {
			imageFile = new RandomAccessFile(file, mode);
			imageFilePath = file.getPath();
		} catch (FileNotFoundException e) {
			Debug.out.println(e);
			ret = false;
		}
		return ret;
	}

	public void closeImage(){
		try {
			imageFile.close();
			imageFile = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			imageFile = null;
			Debug.out.println(e);
		}
	}

	/**
	 * @return the imageFilePath
	 */
	public String getImageFilePath() {
		return imageFilePath;
	}
	/**
	 * @param imageFilePath the imageFilePath to set
	 */
	public boolean setImageFilePath(String imageFilePath) {
		boolean ret;
		if(imageFilePath.length() < MAX_DEVICE_PATH_SIZE){
			this.imageFilePath = imageFilePath;
			ret = true;
		}
		else
			ret = false;
		return ret;
	}
	/**
	 * @return the imageFile
	 */
	public RandomAccessFile getImageFile() {
		return imageFile;
	}
	/**
	 * @param imageFile the imageFile to set
	 */
	public void setImageFile(RandomAccessFile imageFile) {
		this.imageFile = imageFile;
	}
	/**
	 * @return the totalSectors
	 */
	public long getTotalSectors() {
		return totalSectors;
	}
	/**
	 * @param totalSectors the totalSectors to set
	 */
	public void setTotalSectors(long totalSectors) {
		this.totalSectors = totalSectors;
	}
	/**
	 * @return the blockSize
	 */
	public long getBlockSize() {
		return blockSize;
	}
	/**
	 * @param blockSize the blockSize to set
	 */
	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}
	/**
	 * @return the byteOrder
	 */
	public ByteOrder getByteOrder() {
		return byteOrder;
	}
	/**
	 * @param byteOrder the byteOrder to set
	 */
	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	/**
	 * @return the opened
	 */
	public boolean isOpened() {
		return opened;
	}
	/**
	 * @param opened the opened to set
	 */
	public void setOpened(boolean opened) {
		this.opened = opened;
	}
	/**
	 * @return the mediaChange
	 */
	public boolean isMediaChange() {
		return mediaChange;
	}
	/**
	 * @param mediaChange the mediaChange to set
	 */
	public void setMediaChange(boolean mediaChange) {
		this.mediaChange = mediaChange;
	}

}
