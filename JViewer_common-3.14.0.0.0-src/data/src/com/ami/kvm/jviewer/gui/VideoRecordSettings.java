package com.ami.kvm.jviewer.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.videorecord.DisplayVideoDataTask;
class VideoRecordSettings extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	public int Frame_Interval;
	public int Frame_No;
	public String StoreLocation = null;
	public  String videoType = ".avi";
	private JPanel jContentPane = null;
	private JLabel jVideoLengthLabel = null;
	private JTextField videoLengthText = null;
	private JLabel path = null;
	private JTextField videoStorageLocationTextField = null;
	private JButton Browse = null;
	private JFileChooser fc;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JCheckBox recordMode = null;
	private JLabel recordModeLabel = null;
	private final int MAX_VIDEO_LENGTH = 1800;
	
	public final static int WIDTH = 450;
	public final static int HEIGHT = 250;

	/**
	 * @param owner
	 */
	public VideoRecordSettings(JFrame owner) {
		super(owner);
		StoreLocation = JViewerApp.getInstance().getM_videorecord().StoreLocation;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(WIDTH, HEIGHT);
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		Point dp =  JViewerApp.getInstance().getPopUpWindowPosition(WIDTH,HEIGHT);
		this.setLocation(dp);
		this.setTitle(LocaleStrings.getString("U_5_VR"));
		this.setModal(false);
		this.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridConstraints.insets = new Insets(5, 10, 5, 10);
		gridConstraints.gridheight = 1;
		gridConstraints.gridwidth = 1;
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		gridConstraints.weightx = 1.0;
		this.getContentPane().add(getVideoLengthPanel(), gridConstraints);
		gridConstraints.gridy = 1;
		this.getContentPane().add(getVideoLocationPanel(), gridConstraints);
		gridConstraints.gridy = 2;
		this.getContentPane().add(getDecisionControlsPanel(), gridConstraints);
	}

	/*
	 * Close the dialog box
	 */
	public void close(){
		this.setVisible(false);
		//Set the saved configuraion values to the text fields.
		videoLengthText.setText(String.valueOf(VideoRecord.RecordStopTimer));
		videoStorageLocationTextField.setText(JViewerApp.getInstance().getM_videorecord().StoreLocation);
		this.dispose();
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	public JPanel getJContentPane() {
		if (jContentPane == null)
		{
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			GridBagConstraints gridConstraints = new GridBagConstraints();
			gridConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridConstraints.insets = new Insets(2, 5, 5, 5);
			gridConstraints.gridheight = 1;
			gridConstraints.gridwidth = 1;
			gridConstraints.gridx = 0;
			gridConstraints.gridy = 0;
			gridConstraints.weightx = 1.0;
			
			jContentPane.add(getVideoLengthLabel(), null);
			jContentPane.add(getSecondsLabel(), null);
			jContentPane.add(getStorageLocationLabel(), null);
			jContentPane.add(getVideoLenghtText(), null);
			jContentPane.add(getVideoStorageLocationTextField(), null);
			jContentPane.add(getBrowse(), null);
			jContentPane.add(getRecordMode(),null);
			jContentPane.add(getRecordModeLabel(),null);
			jContentPane.add(getOkButton(), null);
			jContentPane.add(getCancelButton(), null);
		}
		return jContentPane;
	}

	/**
	 * Returns the JPanel container, which contains the controls for specifying the video length.
	 * @return JPanel object
	 */
	private JPanel getVideoLengthPanel(){
		JPanel videoLengthPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridConstraints.insets = new Insets(2, 5, 2, 5);
		gridConstraints.gridheight = 1;
		gridConstraints.gridwidth = 1;
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		gridConstraints.weightx = 0.0;
		videoLengthPanel.add(getVideoLengthLabel(),gridConstraints);
		gridConstraints.ipadx = 30;
		gridConstraints.gridx = 1;
		videoLengthPanel.add(getVideoLenghtText(), gridConstraints);
		gridConstraints.ipadx = 0;
		gridConstraints.gridx = 2;
		videoLengthPanel.add(getSecondsLabel(), gridConstraints);
		gridConstraints.weightx = 1.0;
		gridConstraints.gridx = 3;
		videoLengthPanel.add(new JPanel(), gridConstraints);
		return videoLengthPanel;
	}

	/**
	 * Returns the JPanel container, which contains the controls for specifying the video storage location.
	 * @return JPanel object
	 */
	private JPanel getVideoLocationPanel(){
		JPanel videoLocationPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridConstraints.insets = new Insets(2, 2, 2, 5);
		gridConstraints.gridheight = 1;
		gridConstraints.gridwidth = 1;
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		gridConstraints.weightx = 1.0;
		videoLocationPanel.add(getStorageLocationLabel(), gridConstraints);
		gridConstraints.insets = new Insets(0, 2, 0, 0);
		gridConstraints.gridy = 1;
		videoLocationPanel.add(getVideoLocationBrowsePanel(), gridConstraints);
		return videoLocationPanel;
	}

	/**
	 * Returns the JPanel container, which contains the controls for browsing
	 * nd specifying the video storage location.
	 * @return JPanel object
	 */
	private JPanel getVideoLocationBrowsePanel(){
		JPanel videoLocationBrowsePanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridConstraints.insets = new Insets(5, 0, 2, 5);
		gridConstraints.gridheight = 1;
		gridConstraints.gridwidth = 1;
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		gridConstraints.weightx = 1.0;
		gridConstraints.ipady = 5;
		videoLocationBrowsePanel.add(getVideoStorageLocationTextField(), gridConstraints);
		gridConstraints.insets = new Insets(5, 5, 2, 5);
		gridConstraints.gridx = 1;
		gridConstraints.ipady = 0;
		gridConstraints.weightx = 0.0;
		videoLocationBrowsePanel.add(getBrowse(), gridConstraints);
		return videoLocationBrowsePanel;
	}
	
	/**
	 * Returns the JPanel container, which contains the controls for controlling the video record operation.
	 * @return JPanel object
	 */
	private JPanel getDecisionControlsPanel(){
		JPanel decisionControlsPanel =new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridConstraints.insets = new Insets(2, 5, 2, 5);
		gridConstraints.gridheight = 1;
		gridConstraints.gridwidth = 1;
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		gridConstraints.weightx = 1.0;
		decisionControlsPanel.add(new JPanel(), gridConstraints);
		gridConstraints.gridx = 2;
		gridConstraints.weightx = 0.0;
		decisionControlsPanel.add(getOkButton(), gridConstraints);
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 1;
		gridConstraints.insets = new Insets(1, 5, 1, 5);
		decisionControlsPanel.add(getRecordMode(), gridConstraints);
		gridConstraints.gridy = 2;
		gridConstraints.weightx = 1.0;
		decisionControlsPanel.add(getRecordModeLabel(), gridConstraints);
		gridConstraints.gridx = 2;
		gridConstraints.weightx = 0.0;
		decisionControlsPanel.add(getCancelButton(), gridConstraints);
		return decisionControlsPanel;
	}
	/**
	 * This method initializes Label 'jVideoLengthLabel'
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getVideoLengthLabel()
	{
		jVideoLengthLabel = new JLabel();
		jVideoLengthLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		jVideoLengthLabel.setHorizontalAlignment(SwingConstants.LEFT);
		jVideoLengthLabel.setText(LocaleStrings.getString("V_1_VRS"));

		return jVideoLengthLabel;
	}

	/**
	 * This method initializes Text Field that gets the Number of frames
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getVideoLenghtText() {
		if (videoLengthText == null)
		{
			videoLengthText = new JTextField();
			videoLengthText.setText(String.valueOf(JViewerApp.getInstance().VIDEO_RECORD_DEFAULT_TIME));
			videoLengthText.addKeyListener(new KeyAdapter() {// Limiting no;Of chars to four.
				public void keyReleased(KeyEvent ke) {
					String input = videoLengthText.getText();
					if(input.length() >= String.valueOf(MAX_VIDEO_LENGTH).length()){
						if(Integer.parseInt(input) > MAX_VIDEO_LENGTH){
							showMessage(LocaleStrings.getString("V_2_VRS")+MAX_VIDEO_LENGTH+")");	
							input = String.valueOf(MAX_VIDEO_LENGTH);
							ke.consume();
							videoLengthText.setText(input);
							videoLengthText.selectAll();
						}
					}
				}
			});
		}
		return videoLengthText;
	}

	/**
	 * This method initializes Label 'jVideoLengthLabel'
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getSecondsLabel()
	{
		jVideoLengthLabel = new JLabel();
		jVideoLengthLabel.setHorizontalAlignment(SwingConstants.LEFT);
		jVideoLengthLabel.setText(LocaleStrings.getString("V_3_VRS"));

		return jVideoLengthLabel;
	}

	/**
	 * This method initializes Label 'path'
	 *
	 * @return javax.swing.JLabel
	 */
	private JLabel getStorageLocationLabel()
	{
		path = new JLabel();
		path.setHorizontalAlignment(SwingConstants.LEFT);
		path.setText(LocaleStrings.getString("V_4_VRS"));
		return path;
	}

	/**
	 * This method initializes Text Field that gets the Path
	 *
	 * @return javax.swing.JTextField
	 */
	JTextField getVideoStorageLocationTextField() {
		if (videoStorageLocationTextField == null)
		{
			videoStorageLocationTextField = new JTextField(StoreLocation);
			
			videoStorageLocationTextField.addActionListener(this);
			videoStorageLocationTextField.setEditable(false);
			videoStorageLocationTextField.setBackground(Color.WHITE);
		}
		return videoStorageLocationTextField;
	}

	/**
	 * This method initializes Browse Button
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBrowse() {
		if (Browse == null)
		{
			Browse = new JButton();
			Browse.setText(LocaleStrings.getString("A_1_DP"));
			Browse.addActionListener(this);

		}
		return Browse;
	}

	/**
	 * This method initializes Label 'Video Type'
	 *
	 * @return javax.swing.JLabel
	 */
	private JCheckBox getRecordMode(){
		
		 recordMode = new JCheckBox(LocaleStrings.getString("V_5_VRS"));
		 recordMode.setBorder(null);
		 recordMode.setSelected(true);
		 recordMode.addActionListener(this);
		 return recordMode ;

	}
	private JLabel getRecordModeLabel()
	{
		recordModeLabel = new JLabel();
		recordModeLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		recordModeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		recordModeLabel.setText(LocaleStrings.getString("V_6_VRS"));

		return recordModeLabel;
	}


	/**
	 * This method initializes okButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null)
		{
			okButton = new JButton();
			okButton.setText(LocaleStrings.getString("A_3_GLOBAL"));
			okButton.addActionListener(this);
		}
		return okButton;
	}
	/**
	 * This method initializes cancelButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null)
		{
			cancelButton = new JButton();
			cancelButton.setText(LocaleStrings.getString("A_4_GLOBAL"));
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}

	/* This method shows a pop up message box,displaying an error message
	 * @param Message String
	 */
	public void showMessage(String str) {
		JOptionPane.showMessageDialog (
				jContentPane,
				str,
				LocaleStrings.getString("V_7_VRS"),
				JOptionPane.ERROR_MESSAGE);
	}
	/* check if the no of frames given lies within the range of 1-1000
	 * @PARAM Input string
	 * @return True:if the number is valid, false: if it is not.
	 */
	private boolean isValidNumber(String str) {
		int num=Integer.parseInt(str);
		if( (num<1) || (num>MAX_VIDEO_LENGTH) )
			return false;
		else
			return true;

	}

	/* Check if the given string is a valid file/path name
	 * @param Input string
	 * @return True:If the path is valid, false: If it is not
	 */
	private boolean directoryCheck(String str) {
		File file=new File(str);
		if(file.isDirectory())
			return true;
		else
			return false;

	}
	/* check if the no of frames given is a valid integer
	 * @PARAM Input string
	 * @return True:if the number is valid, false: if it is not.
	 */
	private boolean isParsableToInt(String str)
	{
		try
		{
			Integer.parseInt(str);
			return true;
		}catch(NumberFormatException nfe){
			Debug.out.println(nfe);
			return false;
		}
	}
	public void actionPerformed(ActionEvent e)
	{

		if(e.getSource()== Browse)
		{
			fc = new JFileChooser(JViewerApp.getInstance().getM_videorecord().StoreLocation);
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			customizefilechooser customfilechooser = new customizefilechooser();
			customfilechooser.customizeFileChooser(fc);

			int returnVal = fc.showDialog(VideoRecordSettings.this,LocaleStrings.getString("V_8_VRS"));
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if(fc.getSelectedFile().exists())
					StoreLocation = fc.getSelectedFile().toString();
				else
					StoreLocation = fc.getSelectedFile().getParent();
			}
			videoStorageLocationTextField.setText(StoreLocation);
		}

		if(e.getSource()== okButton){
			if(videoStorageLocationTextField.getText().length()==0)
			{
				showMessage(LocaleStrings.getString("V_9_VRS"));
			}
			else if(!directoryCheck(StoreLocation)) {
				showMessage(LocaleStrings.getString("V_9_VRS"));
			}
			else if((videoLengthText.getText().length()==0))
			{
				showMessage(LocaleStrings.getString("V_10_VRS"));
			}
			else if(!isParsableToInt(videoLengthText.getText()) || (!isValidNumber(videoLengthText.getText())))
			{
				showMessage(LocaleStrings.getString("V_2_VRS")+MAX_VIDEO_LENGTH+")");
				videoLengthText.requestFocus();
				videoLengthText.setText(String.valueOf(MAX_VIDEO_LENGTH));
				videoLengthText.selectAll();
			}
			else
			{
				JViewerApp.getInstance().getM_videorecord().StoreLocation = StoreLocation;
				VideoRecord.RecordStopTimer = Integer.parseInt(videoLengthText.getText());
				close();

				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_RECORD_START, true);
				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_RECORD_STOP, false);
				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_RECORD_SETTINGS, true);
			}
		}

		if(e.getSource() == cancelButton){
			close();
		}

		// check if the current action event is for recordmode checkbox
		if (e.getSource() == recordMode) {
			// if the checkbox is not selected, show information and update singlevideo value based on user response
			if (!recordMode.isSelected()) {
				if (JOptionPane.showConfirmDialog(jContentPane, LocaleStrings.getString("V_11_VRS"), LocaleStrings.getString("V_7_VRS"),
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.OK_OPTION) {
					JViewerApp.getInstance().getM_videorecord().singleVideo = false;
				} else {
					JViewerApp.getInstance().getM_videorecord().singleVideo = true;
					recordMode.setSelected(true);
				}
			} else { // if the checkbox is selected then update the value of singlevideo to true
				JViewerApp.getInstance().getM_videorecord().singleVideo = true;
				recordMode.setSelected(true);
			}

		}
	}	
}  //  @jve:decl-index=0:visual-constraint="10,10"



 class VideoRecordStart extends TimerTask {

	 JViewerApp RCApp = JViewerApp.getInstance();
	 private int init_fps = 0;
	 private Date CurrTime = new Date(0);
	 private Date RunTime = new Date(0);
	 private int num_blank_frames = 0;
	 private static final int VIDEO_FPS = 3;
	 /**
	 * This method executes the recording timer.
	 */

	 public void run()
	 {

		 //Keep on capturing a frame at least in every 333 millisecond (1000 ms/3 frames)
		 //so that,a minimum of 3 frames are captured per second. This will make
		 //a real time video (solves the VLC issue also).
		 int waitTime = 1000/VIDEO_FPS;

		 // Compare the Time each iteration to make sure we are good
		 CurrTime = new Date(System.currentTimeMillis()/1000);
		 RunTime = RCApp.getM_videorecord().TimeToRun;
		 //Display remaining seconds
		 if(!JViewer.isdownloadapp())
			 RCApp.getM_wndFrame().getWindowMenu().setMessage(LocaleStrings.getString("U_3_VR")+"("+ (RunTime.getTime()- CurrTime.getTime())+" Sec)");

		 if (CurrTime.before(RunTime)&& !JViewer.isdownloadapp()){
			 captureFrames();
			 // Wait until notified by Video Drawing thread
			 try
			 {
				 synchronized(RCApp.getM_videorecord().obj) {
					 RCApp.getM_videorecord().obj.wait(waitTime);
				 }
			 } catch (Exception e1) {
				 Debug.out.println(e1);
			 }
		 }
		 //When only one frame is present, the run time will be 1 sec.
		 //So current time should be compared for equal to case also in order
		 //to capture all the frames.
		 else if((DisplayVideoDataTask.run || CurrTime.getTime() <= RunTime.getTime())&& JViewer.isdownloadapp()){
			 // Wait until notified by Video Drawing thread
			 try
			 {
				 synchronized(RCApp.getM_videorecord().obj) {
					 if(VideoRecord.TotalFrames == 0 && JViewerApp.getInstance().getVideorecordapp().getVideoFrameBuffer().size() == 0){
						/*
						 * For the first frame wait (without timeout) until
						 * notified by video drawing thread. This will prevent
						 * writing blank frames by restricting frame capture
						 * before video drawing thread finishes rendering first
						 * frame.
						 */
						 RCApp.getM_videorecord().obj.wait();
					 } else {
						/*
						 * We will be going for frame capture even if video
						 * drawing thread fails to notify within stipulated time
						 * interval. Thus we will be able to maintain 3FPS
						 * capturing speed.
						 */
						 RCApp.getM_videorecord().obj.wait(waitTime);
					 }
				 }
			 } catch (Exception e1) {
				 Debug.out.println(e1);
			 }
			 //Avoid frame capture which might be triggered during video recording in some 
			 //abnormal termination situations.
			 if(VideoRecord.Recording_Started)
				 captureFrames();
		 }
		 else{
			 this.cancel();
			 RCApp.getM_videorecord().OnVideoRecordStop();
		 }
	 }

	/**
	 * Method is used to create the Video from the JPEG Image
	 *
	 * @param movFile
	 * @throws MalformedURLException
	 */
	public  void makeVideo(String movFile) throws MalformedURLException
    {
    	CurrTime = new Date(System.currentTimeMillis()/1000);
    	long duration;
	    	// Difference between time duration given in video record settings and current time.
	    	// This will be used to calculate time duration if the video recording was stopped before
	    	// the time duration specified in video record settings. 
	    	long timeDeviation = (RunTime.getTime()- CurrTime.getTime()); 
	    	if(timeDeviation<0)
	    		timeDeviation = 0;
	    	if(!JViewer.isdownloadapp()&& !JViewer.isplayerapp())
	    		duration = VideoRecord.RecordStopTimer - timeDeviation;// calculate actual recorded duration.
	    	else
	    		duration = JViewerApp.getInstance().getVideorecordapp().getDuration();

    	String imagePath = JViewerApp.getInstance().getM_videorecord().Temp_store_Path;
    	ImagesToVideo imageToMovie = new ImagesToVideo(movFile, imagePath,VideoRecord.TotalFrames, duration);//added extra parameter to pass duration
    }

    /**
	 * This method is called when images are created
	*/

	public File getFile(int count,String path) {
		String fileName="file"+count+".jpeg";
		File file=new File(path,fileName);
		return file;
	}

	private void captureFrames() {

		// Check if the current frame is a blank screen
		if (JViewerApp.getInstance().getKVMClient().m_isBlank == true) {
			// if the current video recording is single video mode then keep count of the number of blank screen files
			// if the blank screen count == total num of frames then the video file is full of blank screen.
			// no need to created a video file in this case.
			
			// if the current video recording is multiple video mode and if its a blank screen, then no need to write it to a .jpeg file.
			// this is to avoid the creation of blank video files when there is a change in host resoultion.
			num_blank_frames ++;
			Debug.out.println("num_blank_frames " + num_blank_frames);
			if (JViewerApp.getInstance().getM_videorecord().singleVideo == false) {
				return;
			}
		}

		// Capture the buffer to a jpeg image
		File infile = getFile(VideoRecord.TotalFrames, RCApp.getM_videorecord().Temp_store_Path);
		VideoRecord.TotalFrames++;
		try {
			infile.createNewFile();
			ImageIO.write(RCApp.getRCView().getImage(), "jpeg", infile); //write the currently redirected image into a file
		} catch (IOException e) {
			Debug.out.println(e);
			String errorMsg = LocaleStrings.getString("V_12_VRS");
			this.cancel();
			RCApp.getM_videorecord().OnLowDiskSpace(infile, errorMsg);
			VideoRecord.Record_Interrupted = true;
			RCApp.getM_videorecord().OnVideoRecordStop();
		}

		init_fps = RCApp.getVidClnt().getM_frameRate();

		VideoRecord.fps += init_fps;

	}

	public int getNum_blank_frames() {
		return num_blank_frames;
	}

	public void setNum_blank_frames(int num_blank_frames) {
		this.num_blank_frames = num_blank_frames;
	}

 }
