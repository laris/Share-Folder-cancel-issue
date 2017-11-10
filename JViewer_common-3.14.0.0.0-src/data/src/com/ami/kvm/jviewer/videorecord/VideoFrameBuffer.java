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

import java.util.concurrent.ArrayBlockingQueue;

import com.ami.kvm.jviewer.Debug;

public class VideoFrameBuffer {
	public static final int QUEUE_SIZE = 20;
	private ArrayBlockingQueue<VideoFrame> vidFrameBuf;

	/**
	 * Constructor.
	 */
	public VideoFrameBuffer(){
		vidFrameBuf = new ArrayBlockingQueue<VideoFrame>(QUEUE_SIZE);
	}
	/**
	 * Adds the video frame to the buffer if there is a free slot available. 
	 * @param videoFrame - The video frame object to be added to the buffer.
	 */
	public void put(VideoFrame videoFrame){
		try {
			vidFrameBuf.put(videoFrame);
		} catch (InterruptedException e) {
			Debug.out.println(e);
		}
	}

	/**
	 * Checks whether the given video frame is already available in the buffer.
	 * @param videoFrame - The video frame objects which needs to be checked for
	 * its containment in the buffer.
	 * @return true - if the given video frame available
	 * false - otherwise.
	 */
	public boolean contains(VideoFrame videoFrame){
		return vidFrameBuf.contains(videoFrame);
	}

	/**
	 * Remove and return the first added video frame object present in the buffer.
	 * @return - the first added video frame object present in the buffer.
	 */
	public VideoFrame take(){
		VideoFrame videoFrame = null;
		try {
			videoFrame = vidFrameBuf.take();
		} catch (InterruptedException e) {
			Debug.out.println(e);
		}
		return videoFrame;
	}

	/**
	 * Returns the number of elements available at the moment in the buffer.
	 * * @return - The size of the buffer.
	 */
	public int size(){
		return vidFrameBuf.size();
	}
}
