/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2016, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
 ****************************************************************/

package com.ami.kvm.jviewer.videorecord;

import com.ami.kvm.jviewer.common.ISOCFrameHdr;
import com.ami.kvm.jviewer.gui.JViewerApp;

public class VideoFrame {
	ISOCFrameHdr frameHeader;
	private long timeStamp;
	private byte code;
	private byte[] header;
	private byte[] pad = new byte[3];
	private byte[] frameData;
	private byte[] palette = null;
	private byte[] attribute = null;

	/**
	 * Constructor.
	 */
	public VideoFrame(){
		pad = new byte[3];
		frameHeader = JViewerApp.getSoc_manager().getSOCFramehdr();
		header = new byte[frameHeader.getFrameHeadersize()];
	}

	/**
	 * @return the timeStamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timestamp the timeStamp to set
	 */
	public void setTimeStamp(long timestamp) {
		this.timeStamp = timestamp;
	}

	/**
	 * @return the code
	 */
	public byte getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(byte code) {
		this.code = code;
	}

	/**
	 * @return the header
	 */
	public byte[] getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(byte[] header) {
		this.header = header;
	}

	/**
	 * @return the pad
	 */
	public byte[] getPad() {
		return pad;
	}

	/**
	 * @param pad the pad to set
	 */
	public void setPad(byte[] pad) {
		this.pad = pad;
	}

	/**
	 * @return the frameData
	 */
	public byte[] getFrameData() {
		return frameData;
	}

	/**
	 * @param framedata the frameData to set
	 */
	public void setFrameData(byte[] frameData) {
		this.frameData = frameData;
	}

	/**
	 * Sets the color palette value for the frame(Pilot SOC specific). 
	 * @param palette - the palette value to set
	 */
	public void setPalette(byte[] palette){
		if(this.palette == null){
			this.palette = new byte[1024];
		}
		this.palette = palette;
	}

	/**
	 * @return the palette
	 */
	public byte[] getPalette(){
		return palette;
	}

	/**
	 * Sets the attribute value for the frame(Pilot SOC specific). 
	 * @param attribute - the attribute value to set.
	 */
	public void setAttribute(byte[] attribute){
		if(this.attribute == null){
			this.attribute = new byte[16];
		}
		this.attribute = attribute;
	}

	/**
	 * @return the attribute
	 */
	public byte[] getAttribute() {
		return attribute;
	}
}
