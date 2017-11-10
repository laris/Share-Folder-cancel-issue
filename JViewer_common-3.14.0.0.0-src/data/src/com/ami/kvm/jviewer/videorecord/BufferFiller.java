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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.JOptionPane;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.common.ISOCFrameHdr;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.kvm.jviewer.kvmpkts.IVTPPktHdr;

public class BufferFiller extends Thread{
	private static final int VIDEO_LENGTH_TAG_SIZE = 4; // to represent the length of the tag used "len="
	private static final int RESERVED_BYTES_VIDEO_LENGTH = 20; //to represent the number of bytes reserved in video file to save video duration

	private boolean run = false;
	private VideoFrame videoFrame = null;
	private FileInputStream fileReader = null;
	private VideoFrameBuffer videoFrameBuffer = null;

	/**
	 * Constructor.
	 */
	public BufferFiller(){
		this.setName("BufferFillerThread");
		videoFrameBuffer = JViewerApp.getInstance().getVideorecordapp().getVideoFrameBuffer();
		int fileIndex = JViewerApp.getInstance().getVideorecordapp().getFileIndex();

		byte[] videoLengthData = new byte[RESERVED_BYTES_VIDEO_LENGTH];
		try {
			fileReader = new FileInputStream(JViewerApp.getInstance().getVideorecordapp().getVideotmpstorepath()[fileIndex]);
			try {
				// try reading the first 20 bytes and check if the tag "len=" is present
				// if the tag is present then, the video file contains the video duration in the first 20 bytes
				// if so set the duration from the read video duration and continue
				fileReader.read(videoLengthData, 0, videoLengthData.length);
				if((new String(videoLengthData, 0, VIDEO_LENGTH_TAG_SIZE).equals("len=")) == true)
				{
					DisplayVideoDataTask.setVideoDuration((int)(Double.parseDouble(new String(videoLengthData, VIDEO_LENGTH_TAG_SIZE, videoLengthData.length - VIDEO_LENGTH_TAG_SIZE))));
				}
				else
				{
					// if no such tag is present then the video duration is not present in the video file.
					// we need to read the file from the beginning so close and open the inputstream
					fileReader.close();
					fileReader = new FileInputStream(JViewerApp.getInstance().getVideorecordapp().getVideotmpstorepath()[fileIndex]);
				}
			} catch (IOException e) {
				Debug.out.println(e);
				if(fileReader != null){
					try {
						fileReader.close();
						fileReader = null;
					} catch (IOException ie) {
						fileReader = null;
						Debug.out.println(e);
					}
				}
				JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(),
						LocaleStrings.getString("AN_1_BF"), LocaleStrings.getString("A_5_GLOBAL"),
						JOptionPane.ERROR_MESSAGE);
				JViewer.exit(0);
			}
		} catch (FileNotFoundException e) {
			Debug.out.println(e);
			JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(),
					LocaleStrings.getString("AN_1_BF"), LocaleStrings.getString("A_5_GLOBAL"),
					JOptionPane.ERROR_MESSAGE);
			JViewer.exit(0);
		}catch (Exception e) {
			Debug.out.println(e);
			JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(),
					LocaleStrings.getString("AN_1_BF"), LocaleStrings.getString("A_5_GLOBAL"),
					JOptionPane.ERROR_MESSAGE);
			JViewer.exit(0);
		}
		run = true;
		this.start();
	}

	/**
	 * This thread will parse the video DAT file and add individual frames to the
	 * video frame buffer.
	 * Each video frame will be reprosented as an object, which contains informations such as
	 *  - Time Stamp
	 *  - Code
	 *  - SOC specific frame information
	 *  - Video frame header
	 *  - Frame data.
	 */
	public void run(){
		while(run){
			int retval = 0;
			byte[] timeStamp = new byte[4];
			byte[] code = new byte[1];
			byte[] pad = new byte[3];
			ISOCFrameHdr frameHdr = JViewerApp.getSoc_manager().getSOCFramehdr();
			byte[] header = new byte[frameHdr.getFrameHeadersize()];
			byte[] pktHdr = new byte[8];
			try {
				retval = fileReader.read(timeStamp);

				//No more data to read from the file.
				if(retval <= 0){
					/* Adding the last frame to the buffer, when the end of file is reached.
					 * The frame will be added if it is not already in the buffer.
					 * Sometimes the frame will be skipped if it is invalid. 
					 * Adding the last frame ensures that the frame delays are calculated properly.
					 * This is most required when the recorded file consist of no video changes.
					 */
					if((videoFrame != null) && (videoFrameBuffer.contains(videoFrame) == false)){
						videoFrameBuffer.put(videoFrame);
					}
					run = false;
					continue;
				}
				videoFrame = new VideoFrame();
				ByteBuffer timeStampBuffer = ByteBuffer.wrap(timeStamp);
				timeStampBuffer.order(ByteOrder.LITTLE_ENDIAN);
				timeStampBuffer.position(0);
				//Converting signed integer value to unsigned long
				videoFrame.setTimeStamp(timeStampBuffer.getInt() & 0x00000000ffffffffL);

				retval = fileReader.read(code);
				if(retval <= 0){
					run = false;
					continue;
				}
				videoFrame.setCode(code[0]);

				retval = fileReader.read(pad);
				if(retval <= 0){
					run = false;
					continue;
				}

				/*0xAA - no change
				 * 0x66 - Balnk Screen
				 * 
				 */
				if ((code[0] == -86)) 
				{
					continue;
				}
				else if(code[0] == 102 || (code[0] == 170)){
					videoFrameBuffer.put(videoFrame);
					continue;
				}
				//This condition will occur in case of Pilot SOC.
				if(code[0] == 119) //0x77 Control packet
				{

					retval = fileReader.read(pktHdr);
					if(retval <= 0){
						run = false;
						continue;
					}

					ByteBuffer pktBuf = ByteBuffer.wrap(pktHdr);
					pktBuf.order(ByteOrder.LITTLE_ENDIAN);
					pktBuf.position(0);
					IVTPPktHdr ivtpHdr = new IVTPPktHdr(pktBuf.getShort(), pktBuf.getInt(), pktBuf.getShort());
					if(ivtpHdr.type == 0x1001){//Pilot SOC specific IVTP command corresponding to Color Palette.
						byte[] palette = new byte[1024];
						retval = fileReader.read(palette);
						if(retval <= 0){
							run = false;
							continue;
						}
						videoFrame.setPalette(palette);//Set the Color Palette data for the video frame.
						byte[] attribute = new byte[16];
						retval = fileReader.read(attribute);
						if(retval <= 0){
							run = false;
							continue;
						}
						videoFrame.setAttribute(attribute);//Set the Attribute data for the video frame.
					}
					videoFrameBuffer.put(videoFrame);
					continue;
				}
				retval = fileReader.read(header);
				if(retval <= 0){
					run = false;
					continue;
				}
				videoFrame.setHeader(header);
				ByteBuffer headerBuf = ByteBuffer.wrap(header);
				headerBuf.clear();
				headerBuf.position(frameHdr.getFrameHeadersize());
				frameHdr.setHeader(headerBuf);
				headerBuf.position(0);
				byte [] frameData = new byte[frameHdr.getFrameSize()];
				retval = fileReader.read(frameData);
				if(retval < 0 || (retval < frameHdr.getFrameSize())){
					run = false;
					continue;
				}
				videoFrame.setFrameData(frameData);
				videoFrameBuffer.put(videoFrame);//Adding new entry to the buffer.

			} catch (IOException e) {
				Debug.out.println(e);
				run = false;
				continue;
			}
			catch(Exception e){
				Debug.out.println(e);
				run = false;
				continue;
			}
		}

		try {
			fileReader.close();
			fileReader = null;
		} catch (IOException e) {
			fileReader = null;
			Debug.out.println(e);
		}
	}
	/**
	 * @return the fileReader
	 */
	public FileInputStream getFileReader() {
		return fileReader;
	}
	/**
	 * @return the run
	 */
	public boolean isRunning() {
		return run;
	}
	/**
	 * Stops the BufferFiller thread.
	 */
	public void stopRunning(){
		run = false;
	}
}

