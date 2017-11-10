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

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.JVFrame;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.kvm.jviewer.gui.RecorderToolBar;
import com.ami.kvm.jviewer.gui.VideoRecord;
import com.ami.kvm.jviewer.gui.customizefilechooser;

public class VideoRecordApp {

    public String Videostorepath;
    public String[] Videotmpstorepath;
    public String[] VideFilename;
    public byte[] writedata;
    public Timer displayTaskTimer;
    public boolean availToConvert = false;
    public final Object convert = new Object();

    public DisplayVideoDataTask displayTask;
    private long duration;

    private JDialog infoDialog;
    private JPanel infoMessagePanel;
    private JLabel infoMessageLabel;
    private URLProcessor downloadurl;
    private String urlPath = null;
    private int currentFileIndex;
    private VideoFrameBuffer videoFrmBuf = null;
    private BufferFiller fillerThread = null;

	/**
	 * Methos is used to start the vido recording process usch as downlaod and
	 * play the vuideo in the Jviewer
	 * 
	 * @param ip
	 * @param secureconnect
	 * @param sessionCookies
	 * @param videofilename
	 * @return
	 */
	public int startVideorecordRedirection(String ip, String webPort,int secureConnect, String sessionCookies) {

		int retval = 0, count = 0;
		int filecount = JViewer.getVideoFile().length;

		Videotmpstorepath = new String[filecount];
		while (count < filecount) {
			if(JViewer.getVideoFile()[count] != null){
				// Show the downlaod process state to the USer in the JViewer

				if(JViewer.isRestService() == true)
					retval = restOnconnectHTTPDownloadFile(ip, webPort, secureConnect,sessionCookies, count);
				else
					retval = OnconnectHTTPDownloadFile(ip, webPort, secureConnect,sessionCookies, count);

				count++;

				if (retval < 0) {
					return -1;
				}
			}
		}
		if(JViewerApp.getInstance().getConnectionDialog()!=null){
			if (JViewerApp.getInstance().getConnectionDialog().isWebLogIn()) {
				JViewerApp.getInstance().getConnectionDialog().logoutWebSession();
			}
		}

		currentFileIndex = 0;

		if (JViewer.isdownloadapp()) {
			// start thread to convert process
			availToConvert = true;
			Thread thread = null;
			thread = new videoConvertThread();
			thread.start();
		} else {
			// processing the video buffer,such as drawing the buffer in the
			// Scrreen
			OnVideorecordStartRedirection();
			JViewerApp.getInstance().setRedirectionStatus(JViewerApp.REDIR_STARTED);
		}
		return 0;
	}
	public class videoConvertThread extends Thread {
		public void run() {
			int currentFile = 0;
			int totalFiles = 0;

			while (availToConvert) {
				if (VideoRecord.Record_Processing
						|| VideoRecord.Recording_Started) {
					try {
						synchronized (convert) {
							convert.wait();
						}
					} catch (Exception e1) {
						Debug.out.println(e1);
					}
					System.gc();
					JViewerApp.getInstance().initilizeJVVideo();
				}

				currentFile = JViewerApp.getInstance().getVideorecordapp().getFileIndex();
				totalFiles = JViewerApp.getInstance().getVideorecordapp().getVideotmpstorepath().length;

				if (currentFile < totalFiles) {
					if (JViewerApp.getInstance().getM_videorecord().getM_recordTimer() != null)
						JViewerApp.getInstance().getM_videorecord().getM_recordTimer().cancel();
				}
				JOptionPane.showMessageDialog (
						JViewerApp.getInstance().getMainWindow(),
						LocaleStrings.getString("AA_6_VRA")+" : "+ VideFilename[currentFile],
						LocaleStrings.getString("A_6_GLOBAL"),
						JOptionPane.INFORMATION_MESSAGE);

				// if the app is downlaod and save ask the client folder path to
				// save
				if (JViewer.isdownloadapp()) {
					Videostorepath = OngetFoldername(); // get folder name to store the created AVI video
					if (Videostorepath == null) {
						return;
					}
				}
				// processing the video buffer,such as drawing the buffer in the Scrreen
				OnVideorecordStartRedirection();
				JViewerApp.getInstance().setRedirectionStatus(JViewerApp.REDIR_STARTED);

			}
		}
	}

	/**
	 * Delete the temporary vide data file dowloaded from the BMC.
	 */
	public void Ondeletetmpfile() {
		int index = 0, filecount = JViewer.getVideoFile().length;
		File tempvideofile = null;
		
		// when the player app is closed abruptly, the file input stream will not be closed.
		// if the file input stream is not closed then tempvideofile.delete() will fail.
		// so the file will not be deleted from the temp location.
		// Added condition to close the file input stream before deleting the file.
		if(fillerThread != null && fillerThread.isRunning() == true) {
			fillerThread.stopRunning();
		}
		try {
			if(fillerThread.getFileReader() != null){
				fillerThread.getFileReader().close();
			}
		} catch (IOException ie) {
			Debug.out.println(ie);
		}
		catch (Exception e) {
			Debug.out.println(e);
		}
		
		try {
			while (index < filecount) {

				tempvideofile = new File(Videotmpstorepath[index]);
				tempvideofile.delete();
				index++;
			}
		} catch (Exception e) {// to handle the exception thrown when trying to delete the
			// temp file i fhttp connetion is not established.
			Debug.out.println(e);
			return;
		}

	}

	/**
	 * Methos connecs to the URL request and downlaod the File from the BMC
	 *
	 * @param ip
	 * @param secureconnect
	 * @param sessionCookies
	 * @param videofilename
	 * @return
	 */
	private int restOnconnectHTTPDownloadFile(String ip, String webPort, int secureConnect, String sessionCookies,
			int index) {
		int ret = -1;
		String downLoadURL = null;
		VideFilename = JViewer.getVideoFile();

		downloadurl = new URLProcessor(sessionCookies,secureConnect);

		if(secureConnect == 0)
			urlPath = "http://" + ip +":"+webPort + "/api/logs/video-log?file_name="
			+ VideFilename[index];
		else if(secureConnect == 1)
			urlPath = "https://" + ip +":"+webPort + "/api/logs/video-log?file_name="
			+ VideFilename[index];

		//lock video file to be downloaded
		ret = lockVideoFile(true);
		if (ret < 0) {
			Debug.out.println("Lock setting failed");
			return -1;
		}

		String path = System.getProperty("java.io.tmpdir");
		Videotmpstorepath[index] = path + "video" + System.currentTimeMillis();

		if(secureConnect == 0)
			downLoadURL = "http://" + ip +":"+webPort+"/api/logs/video-data?file="+VideFilename[index];
		else if(secureConnect == 1){
			downLoadURL = "https://" + ip +":"+webPort+"/api/logs/video-data?file="+VideFilename[index];
		}

		// Downloading the file from the BMC
		try {
			downloadurl.downloadFile(downLoadURL);
			//This information dialog is a modal dialog, which will be disposed once
			//the file download is completed. Till that the execution will be blocked.
			//But the file download will procede in the background. Once the file download
			//is completed, the dialog will be disposed and the execution will procede.
			showInformationDialog(LocaleStrings.getString("AA_1_VRA"));
			//Here the download status will be retreived.
			ret = downloadurl.getDownloadStatus();
		} catch (Exception e) {
			Debug.out.println(e);
		}
		if (ret < 0) {
			if(ret == URLProcessor.EMPTY_FILE)
			{
				JOptionPane.showMessageDialog(JViewerApp.getInstance()
						.getMainWindow(), LocaleStrings.getString("AA_2_VRA") + LocaleStrings.getString("AA_7_VRA"), LocaleStrings.getString("A_5_GLOBAL"),
						JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(JViewerApp.getInstance()
						.getMainWindow(), LocaleStrings.getString("AA_2_VRA"), LocaleStrings.getString("A_5_GLOBAL"),
						JOptionPane.ERROR_MESSAGE);
			}
			ret = lockVideoFile(false);
			return -1;
		}

		ret = lockVideoFile(false);
		if (ret < 0) {
			Debug.out.println("Lock Resetting failed");
			return -1;
		}
		return 0; //On succes
	}
    private int OnconnectHTTPDownloadFile(String ip, String webPort, int secureConnect, String sessionCookies,
			 int index) {
    int ret = -1;
		VideFilename = JViewer.getVideoFile();
    String downLoadURL = null;
    
    downloadurl = new URLProcessor(sessionCookies,secureConnect);

    if(secureConnect == 0)
    	urlPath = "http://" + ip +":"+webPort + "/rpc/downloadvideo.asp?FILE_NAME="
				+ VideFilename[index];
    else if(secureConnect == 1)
    	urlPath = "https://" + ip +":"+webPort + "/rpc/downloadvideo.asp?FILE_NAME="
			+ VideFilename[index];
    
     ret = lockVideoFile(true);

    if (ret < 0) {
    	return -1;
    }
	
    String path = System.getProperty("java.io.tmpdir");
    Videotmpstorepath[index] = path + "video" + System.currentTimeMillis();

    if(secureConnect == 0)
    	downLoadURL = "http://" + ip +":"+webPort+ "/video/" +VideFilename[index];
    else if(secureConnect == 1)
    	downLoadURL = "https://" + ip +":"+webPort+ "/video/" +VideFilename[index];
    // Downloading the file from the BMC
    try {
    	downloadurl.downloadFile(downLoadURL);
    	//This information dialog is a modal dialog, which will be disposed once
    	//the file download is completed. Till that the execution will be blocked.
    	//But the file download will procede in the background. Once the file download
    	//is completed, the dialog will be disposed and the execution will procede.
    	showInformationDialog(LocaleStrings.getString("AA_1_VRA"));
    	//Here the download status will be retreived.
    	ret = downloadurl.getDownloadStatus();
    } catch (Exception e) {
    	Debug.out.println(e);
    }

    if (ret < 0) {
    	JOptionPane.showMessageDialog(JViewerApp.getInstance()
    			.getMainWindow(), LocaleStrings.getString("AA_2_VRA"), LocaleStrings.getString("A_5_GLOBAL"),
    			JOptionPane.ERROR_MESSAGE);
    	 ret = lockVideoFile(false);
    	return -1;
    }

     ret = lockVideoFile(false);

    if (ret < 0) {
    	return -1;
    }

    return 0; //On succes
}

    /**
     * Method get the folder name until the USer given the folder path
     *
     * @return
     */
    public String OngetFoldername() {

	    JVFrame frame = JViewerApp.getInstance().getMainWindow();
	    String storepath = null;
	    int returnVal = 1;
	    JFileChooser fc;

	    fc = new JFileChooser();
	    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    customizefilechooser customfilechooser = new customizefilechooser();
	    customfilechooser.customizeFileChooser(fc);

	    while (true) {
			returnVal = fc.showDialog(frame,LocaleStrings.getString("V_8_VRS"));
			
			if (returnVal == JFileChooser.APPROVE_OPTION)
				break;
			else{
				int ret = JOptionPane.showConfirmDialog(frame, LocaleStrings.getString("AA_4_VRA"),
							LocaleStrings.getString("AA_5_VRA"), JOptionPane.YES_NO_OPTION, 
							JOptionPane.QUESTION_MESSAGE);
				if(ret == JOptionPane.YES_OPTION){
					storepath = null;
					JViewerApp.getInstance().getM_wndFrame().windowClosed();
				}
			}
		}
			
			storepath = fc.getSelectedFile().toString();		
			return storepath;
    }


    /**
     * Starts the DisplayVideoDataTask(TimerTask)
     */

    public void OnVideorecordStartRedirection() {

	VideoRecord record;
	if (JViewer.isdownloadapp()) {
			JViewerApp.getInstance().setM_videorecord(new VideoRecord());
		record = JViewerApp.getInstance().getM_videorecord();
		record.StoreLocation = JViewerApp.getInstance().getVideorecordapp().Videostorepath;
		record.OnVideoRecordStart();

	} else {
		RecorderToolBar toolBar = (RecorderToolBar) JViewerApp
					.getInstance().getM_wndFrame().getToolbar();
		toolBar.replayButton.setIcon(new ImageIcon(com.ami.kvm.jviewer.JViewer.class.getResource("res/pause.png")));
		toolBar.replayButton.setToolTipText(LocaleStrings.getString("R_2_RT"));
		toolBar.replayButton.setActionCommand(RecorderToolBar.PAUSE);
	}

	videoFrmBuf = new VideoFrameBuffer();
	fillerThread = new BufferFiller();

	//Induce a small delay so that display task starts reading frames after frames are added to buffer. 
	try {
		Thread.sleep(200);
	} catch (InterruptedException e) {
		Debug.out.println(e);
	}
	displayTask = new DisplayVideoDataTask(JViewerApp.getInstance()
			.getVideorecordapp().getWritedata());
	// reset the counter to zero. to reset the slider to initial position
	DisplayVideoDataTask.setCounter(0);
	displayTaskTimer = new Timer();
	displayTaskTimer.schedule(displayTask, 0);
    }

    public int writedatatofile(String path)
    {

	     try
	     {
		 File file = new File(path);
		 FileOutputStream fos = new FileOutputStream(file);
		 /*
		  * To write byte array to a file, use
		  * void write(byte[] bArray) method of Java FileOutputStream class.
		  *
		  * This method writes given byte array to a file.
		  */

		 if(writedata != null)
		 {
		     try{
			 fos.write(writedata);
		     }catch(IOException e)
		     {
		    	 Debug.out.println(e);
		    	 fos.close();
		    	 return -1;
		     }
		 }

    	      	/*
    	      	 * Close FileOutputStream using,
    	      	 * void close() method of Java FileOutputStream class.
    	      	 *
    	      	 */
		 try{
		     fos.close();
		  }catch(IOException e)
		  {
		      return -1;
		  }

	     }
	     catch(FileNotFoundException ex)
	     {
	      Debug.out.println("FileNotFoundException : " + ex);
	      return -1;
	     }
	     catch(Exception ex)
	     {
	    	 Debug.out.println(ex);
	    	 return -1;
	     }

	    return 0;
	 }

	/**
	 * Method to show the information to the user, while converting the raw
	 * video frame to video.
	 *
	 * @param Msg
	 *            User notification message.
	 *
	 * @param modal
	 *            If true, then modal dialog will be used. otherwise the
	 *            dialog will be modeless.
	 */
	public void showInformationDialog(String Msg, boolean modal) {
		JFrame frame = JViewer.getMainFrame();
		infoDialog = new JDialog(frame, "", modal);
		infoMessagePanel = new JPanel();
		infoMessageLabel = new JLabel(Msg);
		infoMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoMessageLabel.setFont(new Font("Arial", Font.BOLD, 14));
		infoMessagePanel.setLayout(new BorderLayout());
		infoMessagePanel.add(infoMessageLabel, BorderLayout.CENTER);
		infoDialog.setSize(700, 50);
		infoDialog.setLocationRelativeTo(frame);
		infoDialog.add(infoMessagePanel);
		infoDialog.setUndecorated(true);
		infoDialog.setResizable(false);
		infoDialog.setVisible(true);
		infoDialog.requestFocus();
		infoMessageLabel.setText(Msg);
	}
	
	/**
	 * Updates the string in (Connecting and Downloading in progress) info
	 * dialog
	 *
	 * @param msg
	 *            String to be updated
	 */
	public void updateMsg(String msg) {
		if (infoMessageLabel != null && msg != null) {
			infoMessageLabel.setText(msg);
		}
	}

	
	/**
	 * Fetches current display string (Connecting and Downloading in progress)
	 * info dialog
	 *
	 * @return
	 *         Current display string.
	 *         Empty String if the infoMessageLabel component is null.
	 */
	public String getCurrentMsg() {
		String msg = "";
		if (infoMessageLabel != null) {
			msg = infoMessageLabel.getText();
		}
		return msg;
	}

	/**
	 * Method to show the information to the user, while converting the raw
	 * video frame to video.
	 *
	 * @param Msg
	 *            User notification message
	 *
	 * Note: Modal dialog will be used here.
	 */
	public void showInformationDialog(String Msg) {
		showInformationDialog(Msg, true);
	}


    /**
     * Method to Get the read video buffer frim BMC
     * @return
     */
    public byte[] getWritedata() {
	return writedata;
    }

    /**
     * Method to Set the read video buffer frim BMC
     * @return
     */
    public void setWritedata(byte[] writedata) {
	this.writedata = writedata;
    }


    /**
     * Method is used to dispose the user information displayed in the
     * frame,while converting the Raw data to video
     *
     */
    public void disposeInformationDialog() {
    	if(infoDialog != null)
    		infoDialog.dispose();
    }

    /**
     * @param duration
     * the duration to set
     */
    public void setDuration(long duration) {
	this.duration = duration;
    }

    /**
     * @return the duration
     */
    public long getDuration() {
	return duration;
    }

	public String[] getVideotmpstorepath() {
        return Videotmpstorepath;
    }

	public void setVideotmpstorepath(String[] videotmpstorepath) {
        Videotmpstorepath = videotmpstorepath;
    }

	/**
	 * Lock the video file while downloading
	 * @param lock
	 */
	public int lockVideoFile(boolean lock){
		//if reset service is running use rest functions
		if(JViewer.isRestService())
			return restLockVideoFile(lock);

		String URL = urlPath + "&FILE_ACCESS=";
		if(lock)
			URL += URLProcessor.SET_FILE_ACCESS;
		else
			URL += URLProcessor.RESET_FILE_ACCESS;
		return downloadurl.processRequest(URL);
	}

	/**
	 * Lock the video file while downloading
	 * @param lock
	 */
	public int restLockVideoFile(boolean lock){
		String URL = urlPath + "&file_access=";
		if(lock)
			URL += URLProcessor.SET_FILE_ACCESS;
		else
			URL += URLProcessor.RESET_FILE_ACCESS;
		return downloadurl.restProcessRequest(URL);
	}

	public int getFileIndex() {
		return currentFileIndex;
	}

	public void setFileIndex(int fileIndex) {
		this.currentFileIndex = fileIndex;
	}

	/**
	 * @return the videoFrmBuf
	 */
	public VideoFrameBuffer getVideoFrameBuffer() {
		return videoFrmBuf;
	}

	/**
	 * @param videoFrmBuf the videoFrmBuf to set
	 */
	public void setVideoFrameBuffer(VideoFrameBuffer videoFrmBuf) {
		this.videoFrmBuf = videoFrmBuf;
	}
}
