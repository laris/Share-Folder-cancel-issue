/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2009, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
 ****************************************************************/

package com.ami.kvm.jviewer.videorecord;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.common.ISOCFrameHdr;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.kvm.jviewer.gui.RecorderToolBar;
import com.ami.kvm.jviewer.kvmpkts.IVTPPktHdr;
import com.ami.kvm.jviewer.kvmpkts.KVMClient;

public class DisplayVideoDataTask extends TimerTask{

	public static boolean run = true;
	private ISOCFrameHdr frameHdr;
	private Date startTime;
	private VideoFrameBuffer videoFrameBuffer = null;
	private static int videoDuration = 0;
	public static Object playLock = new Object();
	public static boolean isPaused = false;
	private static int counter = 0;
	public static Timer timer;


	public DisplayVideoDataTask(byte[] buffer){

		frameHdr = JViewerApp.getSoc_manager().getSOCFramehdr();

		if(JViewer.isdownloadapp()){
			/*
			 * Using model dialog here will block the execution of video buffer
			 * processing thread. (ie.,) This constructor will never return.
			 * This will result in writing endless blank (black perhaps?) images
			 * in temp folder. The video download process will never be
			 * completed due to this behavior. So using modeless dialog instead.
			 */
			JViewerApp.getInstance().getVideorecordapp().showInformationDialog(LocaleStrings.getString("Y_1_DVDT"), false);
		}

	}

/**
 * thread proces the video buffer and
 */

	public void run() {

		long prevTimeStamp = 0;
		long currTimeStamp = 0;
		byte code;
		byte[] header = new byte[frameHdr.getFrameHeadersize()];
		long diff = 0;
		long currTime = 0;
		long processingDelay = 0;
		videoFrameBuffer  = JViewerApp.getInstance().getVideorecordapp().getVideoFrameBuffer();

		startTime = new Date(System.currentTimeMillis()/1000);
		run = true;
		if(JViewer.isplayerapp() && videoDuration != 0)
		{
		   TimerTask task = new TimerTask() {
				@Override
				public void run() {
					update();
				}
			};
		    timer = new Timer();
		    timer.scheduleAtFixedRate(task, 0, 100);		
		    RecorderToolBar.updateComponents(videoDuration);
		}
		while(run)
		{

			ByteBuffer headerBuffer = ByteBuffer.wrap(header);
			headerBuffer.clear();
			currTime = System.currentTimeMillis();
			try{
				if(videoFrameBuffer.size() <= 0){
					run = false;
					if(JViewer.isplayerapp())
						onenablemenu();
					this.cancel();
					continue;
				}
				VideoFrame videoFrame = videoFrameBuffer.take();
				if(videoFrame == null){
					run = false;
					if(JViewer.isplayerapp())
						onenablemenu();
					this.cancel();
					continue;
				}
				if(processingDelay < 0)
					processingDelay = 0;
				currTimeStamp = videoFrame.getTimeStamp();

				if(prevTimeStamp == 0)
					prevTimeStamp = currTimeStamp;

				code = videoFrame.getCode();

				//Reduce all processing delays while calculating the sleep time.
				diff = (currTimeStamp - prevTimeStamp) - (processingDelay);

				/*Value 0x55 = video data
				 * Value 0Xaa = nochange*/
				if ((code == 85) || (code == -86) || (code == 170)) //0x55 video screen
				{
					if (diff > 0)
					{
						try {
							Thread.sleep(diff);
						} catch (InterruptedException e) {
							Debug.out.println(e);
						}
					}
					if ((code == -86))
						continue;
				}
				if ((code == 170))//0xaa no change
				{
					continue;
				}
				else if(code == 102) //0x66 blank screen
				{
					if (diff > 0)
					{
						try {
							Thread.sleep(diff);
						} catch (InterruptedException e) {
							Debug.out.println(e);
						}
					}
					JViewerApp.getInstance().getVidClnt().onBlankScreen();
					continue;
				}
				else if(code == 119) //0x77 Control packet will be handled in case of Pilot SOC.
				{

					//Send Color Palette information for the video frame in SOC IVTP command.
					ByteBuffer paletteBuf = null;
					byte[] palette = videoFrame.getPalette();
					paletteBuf = ByteBuffer.wrap(palette);
					paletteBuf.order(ByteOrder.LITTLE_ENDIAN);
					IVTPPktHdr ivtpHdr = new  IVTPPktHdr((short)0x1001,palette.length, (short)0x0);
					JViewerApp.getInstance().getSockvmclient().onSocControlMessage(ivtpHdr,paletteBuf);

					//Send Attribute information for the video frame in SOC IVTP command.
					ByteBuffer attributeBuf = null;
					byte[] attribute = videoFrame.getAttribute();
					attributeBuf = ByteBuffer.wrap(attribute);
					attributeBuf.order(ByteOrder.LITTLE_ENDIAN);
					ivtpHdr = new  IVTPPktHdr((short)0x1000,attribute.length, (short)0x0);
					JViewerApp.getInstance().getSockvmclient().onSocControlMessage(ivtpHdr,attributeBuf);
					continue;
				}
				prevTimeStamp = currTimeStamp;
				header = videoFrame.getHeader();
				headerBuffer = ByteBuffer.wrap(header);
				headerBuffer.position(frameHdr.getFrameHeadersize());
				frameHdr.setHeader(headerBuffer);
				headerBuffer.position(0);
				
				//handle large frame sizes to avoid OutOfMemoryError
				if(frameHdr.getFrameSize() > KVMClient.MAX_FRAGMENT_SIZE){
					run = false;
					if(JViewer.isplayerapp())
						onenablemenu();
					this.cancel();
					break;
				}
				byte [] framedata = new byte[frameHdr.getFrameSize()];
				framedata = videoFrame.getFrameData();

				ByteBuffer buffer = ByteBuffer.allocate(frameHdr.getFrameSize()+frameHdr.getFrameHeadersize());

				buffer.order(ByteOrder.LITTLE_ENDIAN);
				buffer.put(header, 0, frameHdr.getFrameHeadersize());
				buffer.put(framedata);

				buffer.position(frameHdr.getFrameSize()+frameHdr.getFrameHeadersize());
				synchronized (getPlayLock()) {
					if(isPaused() == true) {
						playLock.wait();
					}
				}
				JViewerApp.getInstance().getVidClnt().onNewFrame(buffer);
				//The miscellaneous delays caused during the processing of the
				//frame buffer should be considered while calculating the total delay.
				//The time delay caused by the thread sleep (diff), should be
				//reduced from this, as it is induced by us.
				processingDelay = (System.currentTimeMillis() - currTime) - diff;
			}
			catch(Exception e){
				Debug.out.println(e);
			}
		}
		// calculate the duration after all the frames are received.
		if(JViewer.isdownloadapp()) {
			calculateDuration();
		}

	}

	private void onenablemenu()
	{
		//Re-initialize JVVideo so that the frames are rendered properly if the video is replayed.
		JViewerApp.getInstance().initilizeJVVideo();
		RecorderToolBar toolBar = (RecorderToolBar)JViewerApp.getInstance().getM_wndFrame().getToolbar();
		toolBar.replayButton.setIcon(new ImageIcon(com.ami.kvm.jviewer.JViewer.class.getResource("res/play.png")));
		toolBar.replayButton.setToolTipText(LocaleStrings.getString("R_1_RT"));
		toolBar.replayButton.setActionCommand("");
	}

	/**
	 * Method calculates the duration of the Video to be recorded and set this duration in video record class
	 *
	 */
	public void calculateDuration(){

		 long duration =((new Date(System.currentTimeMillis()/1000)).getTime() - startTime.getTime());
		 JViewerApp.getInstance().getVideorecordapp().setDuration(duration);
	}

	/**
	 * @return the playLock
	 */
	public static Object getPlayLock() {
	    return playLock;
	}

	public static void pause() {
	    timer.cancel();
	}
	
	public static void resume() {
	    if(JViewer.isplayerapp() && videoDuration != 0)
	    {
	    TimerTask task = new TimerTask() {
		      @Override
		      public void run() {
			  update(); 
		      }
		    };
	    timer = new Timer();
	    timer.scheduleAtFixedRate(task, 0, 100);
	    }
	}
	
    public static void update() {
	
	    if (counter <= (videoDuration*10)) {
		RecorderToolBar.getSlider().setValue(++counter);
		RecorderToolBar.getLabel().setText(Integer.toString(counter/10) + " / " + Integer.toString(videoDuration));
	    } else {
			timer.cancel();
	    }
	
    }

	/**
	 * @return the videoDuration
	 */
	public static int getVideoDuration() {
	    return videoDuration;
	}

	/**
	 * @param videoDuration the videoDuration to set
	 */
	public static void setVideoDuration(int videoDuration) {
	    DisplayVideoDataTask.videoDuration = videoDuration;
	}

	/**
	 * @return the counter
	 */
	public static int getCounter() {
	    return counter;
	}

	/**
	 * @param counter the counter to set
	 */
	public static void setCounter(int counter) {
	    DisplayVideoDataTask.counter = counter;
	}

	/**
	 * @return the isPaused
	 */
	public static boolean isPaused() {
	    return isPaused;
	}

	/**
	 * @param isPaused the isPaused to set
	 */
	public static void setPaused(boolean isPaused) {
	    DisplayVideoDataTask.isPaused = isPaused;
	}
}
