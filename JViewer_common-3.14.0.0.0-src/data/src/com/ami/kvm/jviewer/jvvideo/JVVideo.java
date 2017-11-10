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

////////////////////////////////////////////////////////////////////////////////
//
// JViewer video handler module.
// Gets raw video frames, uncompresses and paints on JViewer view.
//
package com.ami.kvm.jviewer.jvvideo;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Timer;

import javax.imageio.ImageIO;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.common.ISOCCreateBuffer;
import com.ami.kvm.jviewer.common.ISOCFrameHdr;
import com.ami.kvm.jviewer.common.ISOCJVVideo;
import com.ami.kvm.jviewer.gui.FrameRateTask;
import com.ami.kvm.jviewer.gui.JVMenu;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.JViewerView;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.kvm.jviewer.gui.PowerStatusMonitor;
import com.ami.kvm.jviewer.gui.VideoRecord;
import com.ami.kvm.jviewer.hid.USBMouseRep;
import com.ami.kvm.jviewer.kvmpkts.Mousecaliberation;



/**
 * JViewer video handler class.
 */
public class JVVideo {

	public static final int MIN_X_RESOLUTION = 300;
	public static final int MIN_Y_RESOLUTION = 200;
	public static final int MAX_X_RESOLUTION = 1920;
	public static final int MAX_Y_RESOLUTION = 1200;

	private Timer m_frTmr = null; // frame rate update timer
	private int m_frameRate; // frame rate
	private JViewerView m_view; // reference to video screen view
	private ISOCFrameHdr m_frameHdr;
	private boolean newFrame = false;
	private PowerStatusMonitor psMonitor = null;
	private Timer psMonitorTimer = null;
	ISOCJVVideo VideoSOC;

	/**
	 * The constructor
	 */
	public JVVideo() {

		// initialize module variables.
		m_view = JViewerApp.getInstance().getRCView();
		m_view.setRCView(MAX_X_RESOLUTION, MAX_Y_RESOLUTION,0);
		m_frameHdr = JViewerApp.getSoc_manager().getSOCFramehdr();
		//get the SOCvideo instance
		VideoSOC = JViewerApp.getSoc_manager().getSOCJVVideo();
		//Set the frame header for the SOC
		VideoSOC.setframehdr(m_frameHdr);
		//create  and initialize the buffer
		VideoSOC.VideoBuffer_Initialize();
		if(!JViewer.isWebPreviewer() && !JViewer.isBSODViewer()) {
			// check the state of the timer before starting a new one
			if(m_frTmr == null){
				m_frTmr = new Timer();
			}
			else{
				m_frTmr.cancel();
			}
			JViewerApp.getInstance().setFrameRateTask(new FrameRateTask());
			// start frame rate timer
			m_frTmr.schedule(JViewerApp.getInstance().getFrameRateTask(), 0, 1000);
			m_frameRate = 0;

		}
	}

	/**
	 * New frame event handler
	 *
	 * @param buf
	 *            new frame
	 */
	public int onNewFrame(ByteBuffer frameByteBuf) {

		// increment frame rate counter.
		m_frameRate++;
		// parse frame header
		m_frameHdr.setHeader(frameByteBuf);
		JViewerApp.getInstance().setSocframeHdr(m_frameHdr);
		//validate the frame check intial preconditions
		VideoSOC.Newframevalidate();
		//set the newFrame flag.
		setNewFrame(true);
		// Check for resolution change		
		if ((m_view.viewWidth() != m_frameHdr.getresX())
				|| (m_view.viewHeight() != m_frameHdr.getresY())) {
			if (!onResolutionChange()) {
				return 0;
			}
		}

		//Decompress the buffer
		ByteBuffer CompBuffer = VideoSOC.decompressframe(m_frameHdr,frameByteBuf);
		//Handling Hardware cursor drawing
		VideoSOC.SOC_Specific(CompBuffer);
		
		if (VideoRecord.Recording_Started == true)
		{
			synchronized(JViewerApp.getInstance().getM_videorecord().obj) {
				JViewerApp.getInstance().getM_videorecord().obj.notify();
			}
		}
		//Notify the power status monitor when new frame arrives.
		if (psMonitor != null && psMonitor.isMonitorRunning())
		{
			synchronized(psMonitor.getSyncObj()) {
				psMonitor.getSyncObj().notify();
			}
		}
		m_view.repaint();
		return 0;
	}


	/**
	 * @return the newFrame
	 */
	public boolean isNewFrame() {
		return newFrame;
	}

	/**
	 * @param newFrame the newFrame to set
	 */
	public void setNewFrame(boolean newFrame) {
		this.newFrame = newFrame;
	}



	/**
	 * Resolution change event handler
	 */
	public boolean onResolutionChange() {

		Debug.out.println("Resolution change " + m_frameHdr.getresX() + ":"
				+ m_frameHdr.getresY());

		if ((m_frameHdr.getresX() < MIN_X_RESOLUTION)
				|| (m_frameHdr.getresX() > MAX_X_RESOLUTION)
				|| (m_frameHdr.getresY() < MIN_Y_RESOLUTION)
				|| (m_frameHdr.getresY()> MAX_Y_RESOLUTION)) {
			Debug.out.println("Invalid resolution");
			return false;
		}
		else if(JViewer.isWebPreviewer() || JViewer.isBSODViewer() || JViewer.isplayerapp() || JViewer.isdownloadapp()){
			m_view.setRCView(m_frameHdr.getresX(), m_frameHdr.getresY(), m_frameHdr.getFrameType());
			if(JViewer.isplayerapp())
				JViewerApp.getInstance().setResolutionStatus(m_frameHdr.getresX(), m_frameHdr.getresY());
			return true;
		}
		if(JViewer.isjviewerapp() || JViewer.isStandAloneApp()){

			//setresolution change only if its 0. on launch no need to set
			if(JViewerApp.getInstance().getResolutionChanged() != -1)
				JViewerApp.getInstance().setResolutionChanged(1);
			else
				JViewerApp.getInstance().setResolutionChanged(0);
			JViewerApp.getInstance().setResolutionStatus(m_frameHdr.getresX(), m_frameHdr.getresY());
			setFullScreenMode();// set the fullscreen menu option.
			setZoomOptionStatus();// set the zoom option
			// recreate the buffered image with new image sizes.
			m_view.setRCView(m_frameHdr.getresX(), m_frameHdr.getresY(), m_frameHdr.getFrameType());
			//Reset mouse cursor to initial positionin case of Relative mouse mode alone
			if(JViewerApp.getInstance().getRCView().GetUSBMouseMode() == USBMouseRep.RELATIVE_MOUSE_MODE)
				Mousecaliberation.resetCursor();
			//If zoom option is Fit to Video, reset the window size when video resolution is changed. 
			if(JViewerApp.getInstance().getZoomOption() == JVMenu.FIT_TO_HOST_RES){
				JViewerApp.getInstance().onChangeZoomOptions(JVMenu.FIT_TO_HOST_RES);
			}

			if (m_view.getMouseRedirStatus()) {
				m_view.USBsyncCursor(false);
				m_view.USBsyncCursor(true);
			}

			if(JViewerApp.getInstance().getRCView().GetUSBMouseMode() == USBMouseRep.RELATIVE_MOUSE_MODE){
				if (JViewerApp.getInstance().getJVMenu().getMenuItem(
						JVMenu.MOUSE_CLIENTCURSOR_CONTROL).isSelected()){
					JViewerApp.getInstance().OnShowCursor(false);
					JViewerApp.getInstance().OnShowCursor(true);
				}
				else{
					JViewerApp.getInstance().OnShowCursor(false);
				}
			}
		}
		// refresh heap
		System.gc();
		return true;
	}

	/**
	 * Compresson change event handler
	 */
	public void onCompressionChange() {

		VideoSOC.soccompressionchange();
		/*JVMenu menu = JViewerApp.getInstance().getMainWindow().getMenu();
		menu.SetMenuSelected(JVMenu.previous_compression, false);
		ISOCFrameHdr m_frame_hdr = (ISOCFrameHdr)m_frameHdr;
		//switch (m_frameHdr.compressionType) {
		switch (m_frame_hdr.compressionType){
		case Compression.SOFT_COMPRESSION_NONE:
			menu.notifyMenuStateSelected(JVMenu.VIDEO_COMPRESSION_NONE, true);
			break;

		case Compression.SOFT_COMPRESSION_QLZW:
			menu.notifyMenuStateSelected(JVMenu.VIDEO_COMPRESSION_TYPEI, true);
			break;

		case Compression.SOFT_COMPRESSION_RLE:
			menu.notifyMenuStateSelected(JVMenu.VIDEO_COMPRESSION_TYPEII, true);
			break;

		case Compression.SOFT_COMPRESSION_RLE_QLZW:
			menu.notifyMenuStateSelected(JVMenu.VIDEO_COMPRESSION_BOTH, true);
			break;
		}*/
	}


	/**
	 * Blank screen request handler.
	 *
	 * @param ctrlMsg
	 *            control message.
	 */
	public void onBlankScreen() {

		VideoSOC.SOCBlankscreen();
		try {
			((ISOCCreateBuffer) JViewerApp.getInstance().getPrepare_buf()).clearImage();
		} catch (Exception e) {
			// To be fixed
			Debug.out.println(e);
			JViewerApp.getInstance().getKVMClient().m_isBlank = false;
		}

		URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/nosignal.jpg");
		BufferedImage img = null;
		try {
			img = ImageIO.read(imageURL);
		} catch (IOException e) {
			Debug.out.println(e);
		}

		JViewerApp.getInstance().getPrepare_buf().SetImage(img);
		//Set the value of m_cur_width and m_cur_height as the widht and height of the
		//No Signal image respectively.
		m_view.setM_cur_width(img.getWidth());
		m_view.setM_cur_height(img.getHeight());
		if (VideoRecord.Recording_Started == true)
		{
			synchronized(JViewerApp.getInstance().getM_videorecord().obj) {
				JViewerApp.getInstance().getM_videorecord().obj.notify();
			}
		}
		m_view.repaint();
		JViewerApp.getInstance().setResolutionStatus(0, 0);
		setNewFrame(false);
		setZoomOptionStatus();
		setFullScreenMode();
		//start the power status monitor thread when blank screen comes. 
		if(JViewer.isjviewerapp() || JViewer.isStandAloneApp()){
			startPsMonitorTaskAndTimer();
		}		
	}

	/**
	 * reset video
	 */
	public void reset() {

		m_view.repaint();
		m_frTmr.cancel();
		m_frTmr = null;
		JViewerApp.getInstance().setFrameRateTask(null);
		VideoSOC.SOCreset();

		JViewerApp.getInstance().setAppWndLabel("0 fps");
	}

	/**
	 * refresh video
	 */
	public void refresh() {
		if((m_frTmr == null) || (JViewerApp.getInstance().getFrameRateTask() == null)) {
			// start frame rate timer
			m_frameRate = 0;
			m_frTmr = new Timer();
			JViewerApp.getInstance().setFrameRateTask(new FrameRateTask());
			m_frTmr.schedule(JViewerApp.getInstance().getFrameRateTask(), 0, 1000);
		}
	}

	public ISOCFrameHdr getM_frameHdr() {
		return (ISOCFrameHdr)m_frameHdr;
	}

	public ISOCJVVideo getVideoSOC() {
		return VideoSOC;
	}

	public void setVideoSOC(ISOCJVVideo videoSOC) {
		VideoSOC = videoSOC;
	}

	public int getM_frameRate() {
		return m_frameRate;
	}

	public void setM_frameRate(int rate) {
		m_frameRate = rate;
	}
	/**
	 * Checks whether the client resolution and host resolution are same,
	 * and according to that, enables or disables the fullscreen menu item, 
	 * and sets the tool bar icon status.
	 */
	public void setFullScreenMode(){
		
		Dimension sd = JViewerApp.getInstance().getCurrentMonitorResolution();
		if (sd.height == m_frameHdr.getresY() && sd.width == m_frameHdr.getresX()){
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_FULL_SCREEN, true);
			JViewerApp.getInstance().getM_wndFrame().getToolbar().getFullScreenBtn().setToolTipText(LocaleStrings.getString("Q_6_JVT"));
		}
		else {
			if (JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.VIDEO_FULL_SCREEN).isSelected()){
				if(isNewFrame())
					JViewerApp.getInstance().OnVideoFullScreen(false);
			}
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_FULL_SCREEN, false);
			JViewerApp.getInstance().getM_wndFrame().getToolbar().getFullScreenBtn().setToolTipText(LocaleStrings.getString("D_29_JVAPP"));
		}
	}
	/**
	 * Set the appropriate zoom option when the resolution changes
	 */
	public void setZoomOptionStatus(){
		//return if JVIewer windows is out of focus
		if(JViewerView.syncLEDFlag == true){
			//Reset flag if any zoom option change occurs while focus is lost.
			//This will ensure that the proper frame size is set once the focus is gained.
			JViewerApp.getInstance().getM_wndFrame().setResizeFrame(false);
			return;
		}
		//Set the flag to true before teh zoom options are validated.
		JViewerApp.getInstance().setRenderFitToHost(true);
		//Disable both FIT_TO_HOST_RES and FIT_TO_CLIENT_RES zoom options if
		//Balnk screen comes
		if(!isNewFrame()){
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.FIT_TO_HOST_RES, false);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.FIT_TO_CLIENT_RES, false);
		}
		else{
			if(JViewerApp.getInstance().getRCView().GetUSBMouseMode() == USBMouseRep.OTHER_MOUSE_MODE){
				if(JViewerApp.getInstance().getZoomOption() == JVMenu.FIT_TO_HOST_RES){
					JViewerApp.getInstance().onChangeZoomOptions(JVMenu.ACTUAL_SIZE);
					if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_FIT_TO_HOST_SCREEN) == JViewerApp.OEM_FIT_TO_HOST_SCREEN){
						JViewerApp.getInstance().setZoomOption(JVMenu.FIT_TO_HOST_RES);
						JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.FIT_TO_HOST_RES).setSelected(true);
					}
				}
				else{
					JViewerApp.getInstance().setZoomOption(JVMenu.ACTUAL_SIZE);
				}
				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.FIT_TO_HOST_RES, false);
				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.FIT_TO_CLIENT_RES, false);
				JViewerApp.getInstance().getM_wndFrame().getToolbar().enableZoomSlider(false);
				return;
			}
			else{
				if (JViewerApp.getInstance().getJVMenu().getMenuItem(
						JVMenu.FIT_TO_CLIENT_RES).isSelected()){
					JViewerApp.getInstance().onChangeZoomOptions(JVMenu.FIT_TO_CLIENT_RES);
				}
				else if(JViewerApp.getInstance().getJVMenu().getMenuItem(
						JVMenu.FIT_TO_HOST_RES).isSelected()){
					JViewerApp.getInstance().onChangeZoomOptions(JVMenu.FIT_TO_HOST_RES);
				}
			}

			JViewerApp.getInstance().getM_wndFrame().getToolbar().enableZoomSlider(true);
			Dimension screenSize = JViewerApp.getInstance().getCurrentMonitorResolution();
			GraphicsConfiguration gc = JViewerApp.getInstance().getM_wndFrame().getGraphicsConfiguration();
			Insets screenInsets = new Insets(0, 0, 0, 0);
			try{
				screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
			}catch (NullPointerException ne) {
				
			}

			int addedWidth = JViewer.getMainFrame().getInsets().left + JViewer.getMainFrame().getInsets().left+
								JViewerApp.getInstance().getRCView().getInsets().left +
								JViewerApp.getInstance().getRCView().getInsets().right;

			int addedHeight = JViewer.getMainFrame().getInsets().top+
			JViewerApp.getInstance().getM_wndFrame().getWindowMenu().getMenuBar().getHeight()+
			JViewerApp.getInstance().getM_wndFrame().getToolbar().getToolBar().getHeight()+
			JViewerApp.getInstance().getM_wndFrame().getToolbar().getToolBar().getInsets().top+
			JViewerApp.getInstance().getM_wndFrame().getToolbar().getToolBar().getInsets().bottom+
			JViewerApp.getInstance().getM_wndFrame().getM_status().getStatusBar().getHeight()+
			JViewerApp.getInstance().getM_wndFrame().getM_status().getStatusBar().getInsets().top+
			JViewerApp.getInstance().getM_wndFrame().getM_status().getStatusBar().getInsets().bottom+
			screenInsets.top+screenInsets.bottom+screenInsets.left+screenInsets.right;

			//Disable FIT_TO_HOST_RES zoom options if host resolution is greater that client resolution
			if((screenSize.height <= (m_frameHdr.getresY()+addedHeight)) ||
					(screenSize.width <= m_frameHdr.getresX()+ addedWidth)){
				// fit to host option can't be rendered.
				JViewerApp.getInstance().setRenderFitToHost(false);
				//if FIT_TO_HOST_RES zoom optionis already selected, set it as ACTUAL_SIZE
				if(JViewerApp.getInstance().getZoomOption() == JVMenu.FIT_TO_HOST_RES){
					JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.ACTUAL_SIZE).setSelected(true);
					JViewerApp.getInstance().onChangeZoomOptions(JVMenu.ACTUAL_SIZE);
					if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_FIT_TO_HOST_SCREEN) == JViewerApp.OEM_FIT_TO_HOST_SCREEN){
						JViewerApp.getInstance().setZoomOption(JVMenu.FIT_TO_HOST_RES);
						JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.FIT_TO_HOST_RES).setSelected(true);
					}
				}
				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.FIT_TO_HOST_RES, false);
			}
			else{
				if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_FIT_TO_HOST_SCREEN) == JViewerApp.OEM_FIT_TO_HOST_SCREEN
						&& JViewerApp.getInstance().getZoomOption() != JVMenu.ACTUAL_SIZE){
					JViewerApp.getInstance().onChangeZoomOptions(JVMenu.FIT_TO_HOST_RES);
				}
				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.FIT_TO_HOST_RES, true);
			}

			//Disable FIT_TO_CLIENT_RES zoom options if host resolution is lesser that client resolution
			if(screenSize.height > m_frameHdr.getresY() || screenSize.width > m_frameHdr.getresX()){
				//if FIT_TO_CLIENT_RES zoom optionis already selected, set it as ACTUAL_SIZE
				if(JViewerApp.getInstance().getZoomOption() == JVMenu.FIT_TO_CLIENT_RES){
					JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.ACTUAL_SIZE).setSelected(true);
					JViewerApp.getInstance().onChangeZoomOptions(JVMenu.ACTUAL_SIZE);
				}
				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.FIT_TO_CLIENT_RES, false);
			}
			else
				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.FIT_TO_CLIENT_RES, true);
		}
		if(JViewerApp.getInstance().getZoomOption() != null &&
				(JViewerApp.getInstance().getZoomOption() != JVMenu.ACTUAL_SIZE ) &&
				(JViewerApp.getInstance().getZoomOption() != JVMenu.ZOOM_OPTION_NONE)){
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.ZOOM_IN, false);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.ZOOM_OUT, false);
		}
	}

	/**
	 * @return the psMonitor
	 */
	public PowerStatusMonitor getPsMonitor() {
		return psMonitor;
	}

	/**
	 * @param psMonitor the psMonitor to set
	 */
	public void setPsMonitor(PowerStatusMonitor psMonitor) {
		this.psMonitor = psMonitor;
	}

	/**
	 * @return the psMonitorTimer
	 */
	public Timer getPsMonitorTimer() {
		return psMonitorTimer;
	}

	/**
	 * @param psMonitorTimer the psMonitorTimer to set
	 */
	public void setPsMonitorTimer(Timer psMonitorTimer) {
		this.psMonitorTimer = psMonitorTimer;
	}

	public void stopPsMonitorTaskAndTimer() {
		if((psMonitor != null) && (!psMonitor.isMonitorRunning())) {
			psMonitor.cancel();
			psMonitor = null;

			if(psMonitorTimer != null) {
				psMonitorTimer.cancel();
				psMonitorTimer.purge();
				psMonitorTimer = null;
			}
		}
	}

	public void startPsMonitorTaskAndTimer() {
		if(psMonitor != null) {
			return;
		}
		psMonitor = new PowerStatusMonitor();
		if(!psMonitor.isMonitorRunning()) {
			if(psMonitorTimer != null) {
				return;
			}
			psMonitorTimer = new Timer(true);
			try {
				// schedule the psMonitor for every 30 seconds
				psMonitorTimer.schedule(psMonitor, 0, PowerStatusMonitor.WAIT_TIME);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
