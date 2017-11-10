package com.ami.kvm.jviewer.gui;

import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;

public class VideoRecord {


	public int Frame_Interval;
	public int Frame_No;
	public String StoreLocation;
	public  String videoType = ".avi";
	public  String videoName;
	public String Temp_store_Path;
	public String Final_store_Path;
	private String  dateString;
	private String timeString;
	public Timer m_recordTimer;
	public JDialog videoPath = null;
	public VideoRecordSettings m_videoPath;
	public static boolean Recording_Started = false;
	public static boolean Record_Processing = false;
	public static boolean Record_Interrupted = false;
	public static boolean file_creat_success = true;	
	public boolean singleVideo = true;
	
	public VideoRecordStart m_videorecord;
	public final Object obj = new Object();
	public static int fps = 0;
	public static float Avg_fps = 0;
	public static int TotalFrames = 0;
	public static int RecordStopTimer = 1;
	public Date TimeToRun = new Date(0);
	JViewerApp RCApp = JViewerApp.getInstance();

	/**
	 * Constructor
	 */
	public VideoRecord(){
		StoreLocation = JViewerApp.getInstance().VIDEO_RECORD_DEFAULT_PATH;
		RecordStopTimer = JViewerApp.getInstance().VIDEO_RECORD_DEFAULT_TIME;
	}

	public void deleteFiles(int count, String path) {
		
		for(;count<VideoRecord.TotalFrames;count++)
		{
			File file=getFile(count,path);
			if(file.isFile())
			{
				file.delete();
			}
		}
	}

	/**
	 * This method is called when images are created
	*/

	public File getFile(int count,String path) {

		String fileName="file"+count+".jpeg";
		File file=new File(path,fileName);
		return file;
	}

	public void  mktmp_videoPath() {

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		DateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");
		Date currDate = new Date();
		timeString = timeFormat.format(currDate);
		dateString = dateFormat.format(currDate);
		Final_store_Path = StoreLocation+File.separator+videoName;
		Temp_store_Path = System.getProperty("java.io.tmpdir")+File.separator+videoName+"-on-"+dateString+"-at-"+timeString;

		boolean success = (new File(Temp_store_Path)).mkdirs();
		if (success) {
		 Debug.out.println("Directories: " + Temp_store_Path + " created");
		}
	}

	public String getDate() {
		return dateString;
	}

	public void setDate(String dateTime) {
		this.dateString = dateTime;
	}
	public String getTime(){
		return timeString;
	}

	/**
	 * This method is called when Disk space runs low while Video Recording.
	 */
	public void OnLowDiskSpace(File infile, String errorMsg){
		File tempDirectory = infile.getParentFile();
		Record_Processing = false;
		if(!tempDirectory.delete()){
			RCApp.getM_videorecord().deleteFiles(0, tempDirectory.getPath());
			tempDirectory.delete();
		}
		if(JViewer.isdownloadapp())
			JViewerApp .getInstance().getVideorecordapp().disposeInformationDialog();
		JInternalFrame mframe = JViewerApp.getInstance().getMainWindow();
		JOptionPane.showMessageDialog (
				mframe,
				errorMsg,
				LocaleStrings.getString("U_1_VR"),
				JOptionPane.ERROR_MESSAGE);
		if( !JViewer.isdownloadapp()){
			JViewerApp.getInstance().getM_wndFrame().getM_status().resetStatus();//Reset the status bar
			JViewerApp.getInstance().getM_wndFrame().getWindowMenu().setMessage("");//Hide the message in the menu bar
			VideoRecord.TotalFrames = 0;
			//Enable video record settings menu once recording is interrupted.
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_RECORD_SETTINGS,true);
		}
	}
	
	
	/**
	 * This method is called when 'Start Record' menu is clicked
	 */

	public void OnVideoRecordStart()
	{
		TotalFrames = 0;
		TimeToRun = new Date((System.currentTimeMillis()/1000) + RecordStopTimer);
		m_videorecord = new VideoRecordStart();
		m_recordTimer = null;
		VideoRecord.Recording_Started = true;
		m_recordTimer=new Timer(true);
		if(JViewer.isdownloadapp()){
			videoName = LocaleStrings.getString("U_6_VR");
		}
		else{
			videoName = LocaleStrings.getString("U_7_VR");
			RCApp.getM_wndFrame().getToolbar().getVideoRecordBtn().setIcon(new ImageIcon(com.ami.kvm.jviewer.JViewer.class.getResource("res/stop_record.png")));
			RCApp.getM_wndFrame().getToolbar().getVideoRecordBtn().setToolTipText(LocaleStrings.getString("Q_24_JVT"));
			RCApp.getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_RECORD_START, false);
			RCApp.getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_RECORD_STOP, true);
			RCApp. getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_RECORD_SETTINGS, false);
			RCApp.getM_wndFrame().getM_status().setStatus(LocaleStrings.getString("U_2_VR"));//Set the status bar
			RCApp.getM_wndFrame().getWindowMenu().setMessage(LocaleStrings.getString("U_3_VR"));//Add message to the menubar
		}
		mktmp_videoPath();

		m_recordTimer.schedule(m_videorecord, 0, 1);

	}

	/**
	 * This method is called when 'Stop Record' menu is clicked
	 */

	public void OnVideoRecordStop()
	{
		m_videorecord.cancel();
		m_recordTimer.cancel();	//Cancel the recording timer
		VideoRecord.Recording_Started = false;
		if(!JViewer.isdownloadapp()){
			RCApp.getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_RECORD_STOP, false);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_RECORD_START, false);
		}

		if((JViewer.isStandAloneApp())||(JViewer.isjviewerapp())){
		RCApp.getM_wndFrame().getToolbar().getVideoRecordBtn().setIcon(new ImageIcon(com.ami.kvm.jviewer.JViewer.class.getResource("res/start_record.png")));
		RCApp.getM_wndFrame().getToolbar().getVideoRecordBtn().setToolTipText(LocaleStrings.getString("Q_7_JVT"));
		}
		
		if(!VideoRecord.Record_Interrupted){
			VideoRecord.Record_Processing = true;
			// Use the Average FPS calculated to create the movie
			try{
				Avg_fps = fps / TotalFrames;
			}catch(ArithmeticException ae){
				Debug.out.println(ae);
				Avg_fps = 1;

			}

			fps = 0;
			//Avg_fps = 1;

			// A simple work around for managing the user viewability
			if (Avg_fps >= 5)
				Avg_fps /= 3;

			// If the FPS goes too low, then set to minimum of 1
			if (Avg_fps < 1)
			{
				Avg_fps = 1;
			}

			//String str = "file:///"+Temp_store_Path;
			try {
				if(!JViewer.isdownloadapp())
				{
					RCApp.getM_wndFrame().getWindowMenu().setMessage(LocaleStrings.getString("U_4_VR"));	//Add message to the menubar
					JViewerApp.getInstance().getM_wndFrame().getM_status().resetStatus();	//Reset the status bar
				}

				file_creat_success = true;
				File dir = new File(Final_store_Path);
				if (!dir.exists()) {
					if (dir.mkdirs()) {
						Debug.out.println("Directories: " + Final_store_Path + " created");
					}
					else
					{
						Debug.out.println("Directories: " + Final_store_Path + " Failed");
						Final_store_Path = JViewerApp.getInstance().VIDEO_RECORD_DEFAULT_PATH+File.separator+videoName;
						dir = new File(Final_store_Path);
						dir.mkdirs();
						file_creat_success = false;
						Debug.out.println("Directories: " + Final_store_Path + " created");
					}
				}
				else
				{
					Debug.out.println("Directories: " + Final_store_Path + " Already exist");
				}

				m_videorecord.makeVideo(Final_store_Path+File.separator+LocaleStrings.getString("F_1_JVM")+"_"+getDate()+"_"+getTime()+videoType);

			} catch (MalformedURLException e) {
				Debug.out.println(e);
			}
		}
		VideoRecord.Record_Interrupted = false;
		RCApp.getMainWindow().repaint();

	}

	public void VideoRecordsettings() {
		Debug.out.println("OnVideoRecordSettings");
		Point dp =  JViewerApp.getInstance().getPopUpWindowPosition(VideoRecordSettings.WIDTH,VideoRecordSettings.HEIGHT);
		if(m_videoPath == null)
			m_videoPath= new VideoRecordSettings(JViewer.getMainFrame()); //Enable the settings dialog box
		//Handle dialog close event
		m_videoPath.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				m_videoPath.close();
			}
		});
		if( JViewer.isStandalone() ) {

			m_videoPath.setLocation(dp);
			m_videoPath.setVisible(true);
		} else {
			JOptionPane optionPane = new JOptionPane(m_videoPath.getJContentPane(),JOptionPane.PLAIN_MESSAGE);
		    optionPane.setOptions(new Object[] {});
		    videoPath = optionPane.createDialog(RCApp.getMainWindow(), LocaleStrings.getString("U_5_VR"));
		    videoPath.setSize(465, 243);

		    videoPath.setTitle(LocaleStrings.getString("U_5_VR"));
		    videoPath.setVisible(true);
		    videoPath.setSize( 750, 520 );
			videoPath.setLocation(dp);
		    videoPath.setLocationRelativeTo(null);
		    videoPath.setVisible(true);
		    m_videoPath = (VideoRecordSettings) videoPath;

		}
	}
	public Timer getM_recordTimer() {
		return m_recordTimer;
	}
	public void setM_recordTimer(Timer m_recordTimer) {
		this.m_recordTimer = m_recordTimer;
	}
	public VideoRecordStart getM_videorecord() {
		return m_videorecord;
	}
	public void setM_videorecord(VideoRecordStart m_videorecord) {
		this.m_videorecord = m_videorecord;
	}
	public VideoRecordSettings getM_videoPath() {
		return m_videoPath;
	}
	public void setM_videoPath(VideoRecordSettings m_videoPath) {
		this.m_videoPath = m_videoPath;
	}

}



