
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

package com.ami.kvm.jviewer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;

/**
 * The InfoDialog dialog for setting the Information to the user.
 * 
 * @author shirleyh@ami.com
 */
public class InfoDialog {
	public static final int WAIT_DIALOG = 0;
	public static final int INFORMATION_DIALOG = 1;
	public static final int ERROR_DIALOG = 2;
	public static final int CONFIRMATION_DIALOG = 3;
	public static final int UNDECORATED_DIALOG = 4;//This dialog type is used only for reconnect message display
	public static final int MODELESS_WAIT_DIALOG = 5;

	public static final int HOST_KBD_LANG = 0;

	public static final long ONE_SEC = 1000;
	public static final long DISPLAY_DURATION = 5000;

	private JDialog infoDialog;
	private static InfoDialog undecDialog;
	private String infoText = null;
	private long displayDuration = DISPLAY_DURATION; // wait thread sleep time in milliseconds.'
	private WaitThread waitThread;
	private int dialogType = WAIT_DIALOG;
	private String title = null;
	private JFrame ownerFrame = null;
	private JDialog ownerDialog = null;
	private int type = HOST_KBD_LANG;
	private JLabel durationMsgArea = null;

	/**
	 * Return the InfoDialog property value.
	 * @return JDialog
	 */
	private JDialog createInfoDialog() {
		try {
			int xLoc = 0;
			int yLoc = 0;
			Component owner = null;
			//if owner is of type JFrame
			if(ownerFrame != null && ownerFrame.isShowing()){
				infoDialog = new JDialog(ownerFrame, title, false);
				owner = ownerFrame;
			}
			//if owner is of type JDialog
			else if(ownerDialog != null && ownerDialog.isShowing()){
				infoDialog = new JDialog(ownerDialog, title, false);
				owner = ownerDialog;
			}
			else{
				infoDialog = new JDialog(JViewer.getMainFrame(), title, false);
				owner = JViewer.getMainFrame();
			}
			if(dialogType == WAIT_DIALOG){
				infoDialog.setMinimumSize(new Dimension(240, 80));
				infoDialog.setModal(true);
				infoDialog.setUndecorated(true);
			}
			else if(dialogType == UNDECORATED_DIALOG ||
					dialogType == MODELESS_WAIT_DIALOG){
				infoDialog.setMinimumSize(new Dimension(240, 80));
				infoDialog.setModal(false);
				infoDialog.setUndecorated(true);
			}
			else{
				infoDialog.setMinimumSize(new Dimension(240, 120));
				infoDialog.setModal(false);
			}
			infoDialog.add(getinfoPane());
			infoDialog.pack();
			infoDialog.setResizable(false);
			infoDialog.requestFocus();
			xLoc = (((owner.getLocationOnScreen().x + owner.getWidth()) - infoDialog.getWidth())/2);
			yLoc = (((owner.getLocationOnScreen().y + owner.getHeight()) - infoDialog.getHeight())/2);
			infoDialog.setLocation(xLoc, yLoc);
			infoDialog.setLocationRelativeTo(owner);
			infoDialog.addWindowListener(new DiaologWindowListener());
			waitThread = new WaitThread();
			infoDialog.setVisible(true);


		} catch (java.lang.Throwable Exc) {
			Debug.out.println(Exc);
		}
		return infoDialog;
	}

	/**
	 * Return InfoDialog message displaying label
	 * @return JPanel
	 */
	private JPanel getMessagePane() {
		JPanel labelPanel = null;
		JLabel msgArea = null;
		String padding = "&nbsp;&nbsp;&nbsp;";
		String lineSapacing = "<p style=\"padding-top:5;\">";
		String lineSpacingClose = "</p>" ;
		try{
			//replace \n in the message text with <br> tag in HTML to break line.
			if(infoText.contains("\n"))
				infoText = infoText.replace("\n", padding+"<br>"+lineSapacing+padding);
			// set the label text in HTML format to support multi-line messages.
			msgArea = new JLabel("<html>"+lineSapacing+padding+infoText+padding+lineSpacingClose+"</html>");
			msgArea.setHorizontalAlignment(JLabel.CENTER);
			msgArea.setVerticalAlignment(JLabel.CENTER);
			msgArea.setVerticalTextPosition(JLabel.CENTER);
			labelPanel = new JPanel(new BorderLayout());
			labelPanel.add(msgArea, BorderLayout.CENTER);
			if(dialogType == CONFIRMATION_DIALOG || dialogType == UNDECORATED_DIALOG)
			{
				durationMsgArea = new JLabel();
				durationMsgArea.setHorizontalAlignment(JLabel.CENTER);
				durationMsgArea.setVerticalAlignment(JLabel.CENTER);
				labelPanel.add(durationMsgArea, BorderLayout.SOUTH);
			}
		}catch (java.lang.Throwable Exc) {
			Debug.out.println(Exc);
		}
		return labelPanel;
	}

	public JLabel getDurationMsgArea() {
		return durationMsgArea;
	}

	/**
	 * returns the OK button on the confirmation dialog.
	 * @return JButton.
	 */
	private JPanel getOKButton(){
		JPanel buttonPanel = null;
		JButton okButton = null;
		if(okButton == null){
			okButton = new JButton(LocaleStrings.getString("A_3_GLOBAL"));
			okButton.setSize(100, 25);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					closeDialog();
				}
			});
			okButton.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent ke){		
					if(ke.getKeyCode() == KeyEvent.VK_ENTER){
						closeDialog();
					}
				}
			});
			buttonPanel = new JPanel();
			buttonPanel.add(Box.createHorizontalGlue());
			buttonPanel.add(okButton);
			buttonPanel.add(Box.createHorizontalGlue());
		}
		return buttonPanel;
	}
	
	/**
	 * returns the YES,NO button on the confirmation dialog.
	 * @return JButton.
	 */
	private JPanel getYesNoButton(){
		JPanel buttonPanel = null;
		JButton yesButton = null;
		JButton noButton = null;
		if(yesButton == null){
			yesButton = new JButton(LocaleStrings.getString("A_7_GLOBAL"));
			yesButton.setSize(100, 25);
			yesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					closeDialog();
					JViewerApp.getInstance().confirmationDialogResponse(type);
				}
			});
			yesButton.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent ke){		
					if(ke.getKeyCode() == KeyEvent.VK_ENTER){
						closeDialog();
						JViewerApp.getInstance().confirmationDialogResponse(type);
					}
				}
			});
		}
		if(noButton == null){
			noButton = new JButton(LocaleStrings.getString("A_8_GLOBAL"));
			noButton.setSize(100, 25);
			noButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					closeDialog();
				}
			});
			noButton.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent ke){		
					if(ke.getKeyCode() == KeyEvent.VK_ENTER){
						closeDialog();
					}
				}
			});
		}
		buttonPanel = new JPanel();
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(Box.createVerticalStrut(40));
		buttonPanel.add(yesButton);
		buttonPanel.add(noButton);
		buttonPanel.add(Box.createHorizontalGlue());
		return buttonPanel;
	}
	/**
	 * Return the InfoDialog panel to display controls.
	 * 
	 * @return JPanel
	 */
	private JPanel getinfoPane() {
		JPanel infoPane = null;
		try {
			infoPane = new JPanel();
			infoPane.setLayout(new BorderLayout());
			infoPane.add(getMessagePane(), BorderLayout.CENTER);
			switch(dialogType){
			case INFORMATION_DIALOG :
			case ERROR_DIALOG :
				infoPane.add(getIconPanel(), BorderLayout.LINE_START);
				infoPane.add(getOKButton(), BorderLayout.SOUTH);
				break;
			case MODELESS_WAIT_DIALOG :
			case UNDECORATED_DIALOG :
			case WAIT_DIALOG :
				infoPane.setBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.GRAY));
				break;
			case CONFIRMATION_DIALOG:
				infoPane.add(getIconPanel(), BorderLayout.LINE_START);
				infoPane.add(getYesNoButton(), BorderLayout.SOUTH);
				break;
			}
		} catch (java.lang.Throwable Exc) {
			Debug.out.println(Exc);
		}
		return infoPane;
	}

	/**
	 * Returns the panel which shows the icon in the dialog
	 * @return JPanel
	 */
	private JPanel getIconPanel(){
		JPanel iconPanel = null;
		JLabel iconLabel = null;
		String iconPath = null;
		URL imageURL = null;
		try{
			iconPanel = new JPanel();
			iconLabel = new JLabel();
			switch(dialogType){
			case INFORMATION_DIALOG :
				iconPath = "res/information.png";
				break;
			case ERROR_DIALOG :
				iconPath = "res/error.png";
				break;
			default:
				iconPath = "res/information.png";
			}
			if(iconPath != null)
				imageURL = com.ami.kvm.jviewer.JViewer.class.getResource(iconPath);
			if(imageURL != null)
				iconLabel.setIcon(new ImageIcon(imageURL));
			iconLabel.setHorizontalAlignment(JLabel.CENTER);
			iconLabel.setVerticalAlignment(JLabel.CENTER);
			iconPanel.add(iconLabel);
		} catch (java.lang.Throwable Exc) {
			Debug.out.println(Exc);
		}
		return iconPanel;
	}
	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			createInfoDialog();
		} catch (java.lang.Throwable Exc) {
			Debug.out.println(Exc);
		}
	}

	/**
	 * Shows a message dialog box without a frame, which will block the GUI until it closes.
	 * The dialog will close itself after the specified time duration.
	 * @param owner - Owner of the dialog
	 * @param message - Message to be displayed.
	 * @param displayDuration - The duration till which the dialog will be visible.
	 */
	public static void showDialog(Component owner, String message, long displayDuration){
		InfoDialog dialog = new InfoDialog();
		setOwner(dialog, owner);
		dialog.infoText = message;
		dialog.displayDuration = displayDuration;
		dialog.dialogType = WAIT_DIALOG;
		dialog.initialize();
	}

	/**
	 * Shows a message dialog box without a frame, which will *NOT* block the GUI until it closes.
	 * The dialog will close itself after the specified time duration.
	 * @param owner - Owner of the dialog
	 * @param message - Message to be displayed.
	 * @param displayDuration - The duration till which the dialog will be visible.
	 * @return - InfoDialog object.
	 */
	public static InfoDialog showDialog(Component owner, String message, long displayDuration, int dialogType){
		InfoDialog dialog = new InfoDialog();
		setOwner(dialog, owner);
		dialog.infoText = message;
		dialog.displayDuration = displayDuration;
		dialog.dialogType = dialogType;
		dialog.initialize();
		return dialog;
	}
	/**
	 * Shows an information dialog, which will be displayed until the user closes it, or presses the OK button.
	 * @param owner - Owner of the dialog
	 * @param message - The message to be displayed.
	 * @param title - Title of the message dialog
	 * @param dialogType - Type of the dialog
	 */
	public static void showDialog(Component owner, String message, String title, int dialogType){
		InfoDialog dialog = new InfoDialog();
		setOwner(dialog, owner);
		dialog.infoText = message;
		dialog.dialogType = dialogType;
		dialog.displayDuration = 0;
		dialog.title = title;
		dialog.initialize();
	}
	/**
	 * Shows an information dialog, which will be displayed until the user closes it, or presses the OK button.
	 * @param owner - Owner of the dialog
	 * @param message - The message to be displayed.
	 * @param title - Title of the message dialog
	 * @param dialogType - Type of the dialog
	 * @param displayDuration - The duration till which the dialog will be visible.
	 * @param type - Type of function which invoked
	 */
	public static void showDialog(Component owner, String message, String title, int dialogType,long displayDuration,int type){
		InfoDialog dialog = new InfoDialog();
		setOwner(dialog, owner);
		dialog.infoText = message;
		dialog.dialogType = dialogType;
		dialog.displayDuration = displayDuration;
		dialog.title = title;
		dialog.type = type;
		dialog.initialize();
	}

	/**
	 * Shows an information dialog, which avoids user interaction
	 * @param owner - Owner of the dialog
	 * @param message - The message to be displayed.
	 * @param dialogType - Type of the dialog
	 * @param duration - The duration till which the dialog will be visible
	 */
	public static void showDialog(Component owner, String message, int dialogType, int duration){
		//Do not create another instance of the dialog, if one is already available.
		if(undecDialog == null){
			undecDialog = new InfoDialog();
			setOwner(undecDialog, owner);
			undecDialog.infoText = message;
			undecDialog.dialogType = dialogType;
			undecDialog.displayDuration = duration;
			undecDialog.initialize();
		}
	}

	public static void undecDialogClose(){
		if(undecDialog != null){
			undecDialog.closeDialog();
			undecDialog = null;
		}
	}

	/**
	 * Closes the information dialog.
	 */
	private void closeDialog(){
		if(waitThread != null){
			if(dialogType == CONFIRMATION_DIALOG || dialogType == UNDECORATED_DIALOG){
				displayDuration= 0;
			}
			else{
				synchronized (waitThread) {
					waitThread.notify();
				}
			}
			waitThread = null;
		}
		else if(infoDialog != null) {
			infoDialog.dispose();
			infoDialog = null;
		}
	}

	/**
	 * Sets the owner component of the InfoDialog
	 * @param dialog - InfoDialog object for which owner should be set.
	 * @param owner - owner to be set as the owner.
	 */
	public static void setOwner(InfoDialog dialog, Component owner){
		try {

			if(owner == null)
				return;
			//if component is an object of type JFrame or if the component is 
			//an object of a derivative class of JFrame
			if(owner.getClass().equals(Class.forName("javax.swing.JFrame")) ||
					owner.getClass().getGenericSuperclass().equals(Class.forName("javax.swing.JFrame"))){
				dialog.ownerFrame = (JFrame) owner;
			}
			//if component is an object of type JDialog or if the component is 
			//an object of a derivative class of JDialog
			else if(owner.getClass().equals(Class.forName("javax.swing.JDialog")) ||
					owner.getClass().getGenericSuperclass().equals(Class.forName("javax.swing.JDialog"))){
				dialog.ownerDialog = (JDialog) owner;
			}
			else{
				setOwner(dialog, owner.getParent());
			}
		} catch (ClassNotFoundException e) {
			Debug.out.println(e);
		}
	}
	/**
	 * This thread will display the information dialog.
	 */
	class WaitThread extends Thread
	{
		int wokeup =0;
		public WaitThread()
		{
			this.start();
		}
		public void run()
		{
			if (infoDialog == null)
				return;

			if(dialogType == CONFIRMATION_DIALOG || dialogType == UNDECORATED_DIALOG ){
				do	
				{
					try {
						if(wokeup ==0){
							synchronized (waitThread) {
								wokeup = 1;
							}
						}
						if(dialogType == CONFIRMATION_DIALOG)
						{
							durationMsgArea.setText(LocaleStrings.getString("M_2_ID")+ new Integer((int) (displayDuration/ONE_SEC)).toString() +LocaleStrings.getString("M_3_ID"));
						}
						else if(dialogType == UNDECORATED_DIALOG)
						{
							//Undecorated dialog box is used only to display reconnect message
							//When the client tries to reconnect with the server, reconnect message is displayed with the current retry count value and the time interval
							durationMsgArea.setText(JViewerApp.getInstance().getCurrentRetryCount() +LocaleStrings.getString("M_4_ID") + " ( " + new Integer((int) (displayDuration/ONE_SEC)).toString() +LocaleStrings.getString("M_5_ID")+ " )");
						}
							durationMsgArea.repaint();
							WaitThread.sleep(ONE_SEC);
							displayDuration= displayDuration - ONE_SEC;
							if(dialogType == UNDECORATED_DIALOG){
								if((JViewerApp.getInstance().getCurrentRetryCount() < JViewer.getRetryCount()) && (displayDuration == 0))
								{
									//Reset the displayduration value to configured retry interval value for each retry.
									displayDuration=JViewer.getRetryInterval();
									// 'N' Retry Completed. Where N = currentRetryCount + 1
									durationMsgArea.setText((JViewerApp.getInstance().getCurrentRetryCount() + 1)
											+ (LocaleStrings.getString("M_4_ID")));

									synchronized (JViewerApp.getInstance().getRetryCountSync()) {
										// Notify checkReconnect() / OnVideoStartRedirection() to perform attempt to establish connection
										JViewerApp.getInstance().getRetryCountSync().notify();
									}

									WaitThread.sleep(ONE_SEC);// Wait for the retrycount value to increment, for proper update in GUI.
								}

								if (JViewerApp.getInstance().getCurrentRetryCount() >= JViewer.getRetryCount()) {
									displayDuration = 0; // MAX Limit reached.. so breaking out
								}
							}
						} catch (InterruptedException e) {
					Debug.out.println(e);
					}		
				}while(displayDuration>0);
			}
			else{
				try {
					synchronized (waitThread) {
						waitThread.wait(displayDuration);
						displayDuration = 0;
					}
				} catch (InterruptedException e) {
					Debug.out.println(e);
				}
			}
			if((infoDialog != null) && (displayDuration<=0)){
				if(dialogType == MODELESS_WAIT_DIALOG){
					if(JViewerApp.getInstance().getHidInitDialog() != null){
						JViewerApp.getInstance().setHidInitDialog(null);
						JViewerApp.getInstance().getRCView().addKMListeners();
					}
				}
				infoDialog.dispose();
				infoDialog = null;
			}
		}
	}

	class DiaologWindowListener extends WindowAdapter{
		public void windowClosing(WindowEvent arg0) {
			closeDialog();
		}
	}

	/**
	 * @return the undecDialog
	 */
	public static InfoDialog getUndecDialog() {
		return undecDialog;
	}
}
