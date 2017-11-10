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
// JViewer KVM Sharing Dialog component module.
//

package com.ami.kvm.jviewer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.kvmpkts.KVMClient;

/**
 * KVMShareDialog component class.
 */
public class KVMShareDialog{

	public static final boolean FIRST_USER = true;
	public static final boolean SECOND_USER = false;
	public static final byte KVM_SHARING = 0;
	public static final byte KVM_SELECT_MASTER = 1;
	public static final byte KVM_FULL_PERMISSION_REQUEST = 2;

	public static boolean isMasterSession;
	private int kvmsDecisionDuration = 30;

	private final int NORTH_PANEL_STRUT = 150;
	private JButton kvmsSubmitButton = null;
	private JLabel kvmsRequestLabel = null;
	private JDialog kvmsResponseDialog = null;
	private JDialog kvmsRequestDialog = null;
	private Timer kvmsTimer = null;
	private JLabel kvmsTimerLabel = null;
	private JLabel mediaRedirStatus = null;
	private JRadioButton fullAccessChoice = null;
	private JRadioButton partialAccessChoice = null;
	private JRadioButton denyAccessChoice = null;
	private JPanel kvmsNorthPanel = null;
	private JPanel kvmsCenterPanel = null;
	private JPanel kvmsSouthPanel = null;
	private JPanel kvmsResponsePanel = null;
	private JLabel kvmsResponseLabel = null;
	private JPanel kvmsRequestPanel = null;
	private ButtonGroup kvmsButtonGroup = null;
	private byte dialogType;	
	private String reqUserDetails = null;
	private JRadioButton blockRequests = null;
	private JComboBox blockPermission_comboBox = null;

	public KVMShareDialog(){
		kvmsRequestLabel = new JLabel();
		kvmsTimerLabel = new JLabel();
		mediaRedirStatus = new JLabel();
		fullAccessChoice = new JRadioButton(LocaleStrings.getString("H_1_KVMS"),false);
		partialAccessChoice = new JRadioButton(LocaleStrings.getString("H_2_KVMS"),false);
		denyAccessChoice = new JRadioButton(LocaleStrings.getString("H_3_KVMS"),false);
		blockRequests = new JRadioButton(LocaleStrings.getString("F_149_JVM"),false);
		blockPermission_comboBox = new JComboBox();
		blockPermission_comboBox.setModel(new DefaultComboBoxModel(new String[] {LocaleStrings.getString("H_2_KVMS"), LocaleStrings.getString("H_3_KVMS")}));
		blockPermission_comboBox.setSelectedIndex(0); // select the first element by default
		kvmsResponsePanel = new JPanel();
		kvmsRequestPanel = new JPanel();
	}
	/**
	 * Construct Dialog for first user
	 */
	private void constructResponseDialog(byte type)
	{
		dialogType = type;
		constructNorthPanel();
		constructCenterPanel();
		constructSouthPanel();
		kvmsResponsePanel.setLayout(new BorderLayout());
		kvmsResponsePanel.add(kvmsNorthPanel,BorderLayout.NORTH);
		kvmsResponsePanel.add(kvmsCenterPanel,BorderLayout.CENTER);
		kvmsResponsePanel.add(kvmsSouthPanel,BorderLayout.SOUTH);
		JFrame frame = JViewer.getMainFrame();
		kvmsResponseDialog = new JDialog(frame,LocaleStrings.getString("H_6_KVMS"),true);
		if(type != KVM_SELECT_MASTER)
			kvmsResponseDialog.setModal(false);
		kvmsResponseDialog.add(kvmsResponsePanel);
		kvmsResponseDialog.pack();
		kvmsResponseDialog.setResizable(false);
		kvmsResponseDialog.setLocation(JViewerApp.getInstance().getPopUpWindowPosition(kvmsResponseDialog.getWidth(),kvmsResponseDialog.getHeight()));
		kvmsResponseDialog.repaint();
		kvmsResponseDialog.addKeyListener(new KVMShareDialogKeyListener());
		//When JViewer window is in minimized state, then kvmsResponseDailog's x or y coordinates wil be negative.
		//Hence response dialog will not be visible if JViewer is in minimized state
		if( kvmsResponseDialog.getLocation().getX() < 0 || kvmsResponseDialog.getLocation().getY() <0 ) {
			// On setting relative component as null, will place the dialog to the center of the screen
			kvmsResponseDialog.setLocationRelativeTo(null);
		}
		kvmsResponseDialog.addWindowListener
		(
				new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						JViewerApp RCApp = JViewerApp.getInstance();
						Debug.out.println("Deny is Request");
						if(dialogType == KVM_FULL_PERMISSION_REQUEST)
							RCApp.OnSendKVMPrevilage(KVMSharing.KVM_REQ_PARTIAL, reqUserDetails);
						else if(dialogType == KVM_SHARING)
							RCApp.OnSendKVMPrevilage(KVMSharing.KVM_REQ_DENIED, reqUserDetails);
						getKVMShareResponseDialog().dispose();
						getTimer().stop();
						kvmsDecisionDuration = 30;
						if (reqUserDetails != null)
							JViewerApp.getInstance().getResponseDialogTable().remove(reqUserDetails);
						kvmsTimerLabel.setText("");
					}
				}
		);
		reqUserDetails = KVMSharing.KVM_CLIENT_USERNAME+" : "+
						KVMSharing.KVM_CLIENT_IP+" : "+KVMSharing.KVM_CLIENT_SESSION_INDEX;

		JViewerApp.getInstance().initResponseDialogTable();		
		JViewerApp.getInstance().getResponseDialogTable().put(reqUserDetails, kvmsResponseDialog);
	}

	/**
	 * Construct the North Panel of the KVM Share Dialog
	 */
	private void constructNorthPanel(){
		String panelLabel = "";
		if(kvmsNorthPanel == null)
			kvmsNorthPanel = new JPanel();
		else
			kvmsNorthPanel.removeAll();
		kvmsNorthPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		if(dialogType == KVM_SHARING || dialogType == KVM_FULL_PERMISSION_REQUEST){
			panelLabel = LocaleStrings.getString("H_4_KVMS")+" "+KVMSharing.KVM_CLIENT_USERNAME+"("+JViewerApp.getInstance().getIpmiPrivText(KVMSharing.KVM_CLIENT_IPMI_PRIV)+")"+
							" "+LocaleStrings.getString("H_5_KVMS")+" "+KVMSharing.KVM_CLIENT_IP;
		}
		else if(dialogType == KVM_SELECT_MASTER){
			panelLabel = LocaleStrings.getString("H_13_KVMS");
			kvmsNorthPanel.add(Box.createHorizontalStrut(NORTH_PANEL_STRUT));
		}
		if(kvmsResponseLabel == null)
			kvmsResponseLabel = new JLabel(panelLabel);
		else
			kvmsResponseLabel.setText(panelLabel);
		kvmsRequestLabel.setHorizontalTextPosition(JLabel.LEFT);
		kvmsNorthPanel.add(kvmsResponseLabel, FlowLayout.LEFT);
	}
	
	/**
	 * Construct the Center Panel of the Response Dialog 
	 */
	private void constructCenterPanel(){
		if(mediaRedirStatus == null)
			mediaRedirStatus = new JLabel();
		mediaRedirStatus.setText(LocaleStrings.getString("H_14_KVMS"));
		mediaRedirStatus.setForeground(Color.blue);
		if(kvmsCenterPanel == null)
			kvmsCenterPanel = new JPanel();
		else
			kvmsCenterPanel.removeAll();
		if(kvmsButtonGroup == null)
			kvmsButtonGroup = new ButtonGroup();
		else{
			Enumeration<AbstractButton>	buttons = kvmsButtonGroup.getElements();
			while(buttons.hasMoreElements()){
				kvmsButtonGroup.remove(buttons.nextElement());
			}
		}
		if(dialogType == KVM_SHARING || dialogType == KVM_FULL_PERMISSION_REQUEST){
			if(fullAccessChoice == null){
				fullAccessChoice = new JRadioButton(LocaleStrings.getString("H_1_KVMS"),false);
				fullAccessChoice.setMnemonic(KeyEvent.VK_A);
			}
			if(partialAccessChoice == null){
				partialAccessChoice = new JRadioButton(LocaleStrings.getString("H_2_KVMS"),false);
				partialAccessChoice.setMnemonic(KeyEvent.VK_V);
			}
			if(blockRequests == null){
				blockRequests = new JRadioButton(LocaleStrings.getString("F_149_JVM"),false);
				blockRequests.setMnemonic(KeyEvent.VK_B);
			}
			if(denyAccessChoice == null){
				denyAccessChoice = new JRadioButton(LocaleStrings.getString("H_3_KVMS"),false);
				denyAccessChoice.setMnemonic(KeyEvent.VK_D);
			}

			kvmsCenterPanel.setLayout(getGridBagLayout());

			RadioListener listener = new RadioListener();
			fullAccessChoice.addActionListener(listener);
			partialAccessChoice.addActionListener(listener);
			denyAccessChoice.addActionListener(listener);
			blockRequests.addActionListener(listener);

			kvmsButtonGroup.add(fullAccessChoice);
			kvmsButtonGroup.add(partialAccessChoice);
			kvmsButtonGroup.add(denyAccessChoice);
			kvmsButtonGroup.add(blockRequests);

			// deny access radio button won't be available for kvm full permission requests
			if(dialogType == KVM_FULL_PERMISSION_REQUEST){
				denyAccessChoice.removeActionListener(listener);
				kvmsButtonGroup.remove(denyAccessChoice);
				denyAccessChoice.setVisible(false);
			}
			//Default choice is full permission
			fullAccessChoice.setSelected(true);
		}
		else if(dialogType == KVM_SELECT_MASTER){
			int userCount = KVMClient.getNumUsers();
			String []userData = KVMClient.getUserData();
			JRadioButton []otherSessions = new JRadioButton[userCount];
			kvmsCenterPanel.setLayout(new GridLayout((userCount - 1),1));

			for(int count = 0; count < userCount; count++){
				String display = (userData[count].substring(userData[count].indexOf(":")+1, userData[count].length())).trim();
				String index = (userData[count].substring(0,userData[count].indexOf(":")-1)).trim();

	    		if(Integer.parseInt(index) == JViewerApp.getInstance().getCurrentSessionId()){
					continue;
	    		}
				otherSessions[count] = new JRadioButton(display);
				otherSessions[count].setActionCommand(userData[count]);
				kvmsCenterPanel.add(otherSessions[count]);
				kvmsButtonGroup.add(otherSessions[count]);
			}
		}

		kvmsCenterPanel.repaint();
	}
	
	/**
	 * Construct the South Panel of the KVMSharing Response dialog.
	 */
	private void constructSouthPanel(){
		
		if(kvmsSouthPanel == null){
			kvmsSouthPanel = new JPanel();
			kvmsSouthPanel.setLayout(new FlowLayout());
			if(kvmsSubmitButton == null)
				initSubmitButton();
			if(kvmsTimerLabel == null)
				kvmsTimerLabel = new JLabel();
			if(dialogType == KVM_SELECT_MASTER)
				kvmsTimerLabel.setText("(10 "+LocaleStrings.getString("H_7_KVMS")+" )");
			else
				kvmsTimerLabel.setText("(30 "+LocaleStrings.getString("H_7_KVMS")+" )");
			kvmsSouthPanel.add(kvmsSubmitButton);
			kvmsSouthPanel.add(kvmsTimerLabel);
		}
	}
	
	/**
	 * Initialize the OK button on the KVMSaring Response Dialog
	 */
	private void initSubmitButton(){
		kvmsSubmitButton = new JButton(LocaleStrings.getString("A_3_GLOBAL"));
		KVMShareDialogButtonListener kvmsButtonListener = new KVMShareDialogButtonListener();
		kvmsSubmitButton.addActionListener(kvmsButtonListener);
	}
	/**
	 * Adds layout to the centerpanel
	 */
	private GridBagLayout getGridBagLayout()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		kvmsCenterPanel.setLayout(gridBagLayout);

		GridBagConstraints gbc_AllowVirtualConsole = new GridBagConstraints();
		gbc_AllowVirtualConsole.anchor = GridBagConstraints.WEST;
		gbc_AllowVirtualConsole.insets = new Insets(0, 0, 5, 5);
		gbc_AllowVirtualConsole.gridx = 0;
		gbc_AllowVirtualConsole.gridy = 0;
		kvmsCenterPanel.add(fullAccessChoice, gbc_AllowVirtualConsole);

		GridBagConstraints gbc_AllowonlyVideo = new GridBagConstraints();
		gbc_AllowonlyVideo.anchor = GridBagConstraints.WEST;
		gbc_AllowonlyVideo.insets = new Insets(0, 0, 5, 5);
		gbc_AllowonlyVideo.gridx = 0;
		gbc_AllowonlyVideo.gridy = 1;
		kvmsCenterPanel.add(partialAccessChoice, gbc_AllowonlyVideo);

		GridBagConstraints gbc_DenyAccess = new GridBagConstraints();
		gbc_DenyAccess.anchor = GridBagConstraints.WEST;
		gbc_DenyAccess.insets = new Insets(0, 0, 5, 5);
		gbc_DenyAccess.gridx = 0;
		gbc_DenyAccess.gridy = 2;
		kvmsCenterPanel.add(denyAccessChoice, gbc_DenyAccess);

		GridBagConstraints gbc_BlockPrivilegeRequest = new GridBagConstraints();
		gbc_BlockPrivilegeRequest.anchor = GridBagConstraints.WEST;
		gbc_BlockPrivilegeRequest.insets = new Insets(0, 0, 5, 5);
		gbc_BlockPrivilegeRequest.gridx = 0;
		gbc_BlockPrivilegeRequest.gridy = 3;
		kvmsCenterPanel.add(blockRequests, gbc_BlockPrivilegeRequest);

		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.anchor = GridBagConstraints.WEST;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 3;
		kvmsCenterPanel.add(blockPermission_comboBox, gbc_comboBox);
		blockPermission_comboBox.setSelectedIndex(0);
		blockPermission_comboBox.setVisible(false);

		GridBagConstraints gbc_lblMediaRedirectionStatus = new GridBagConstraints();
		gbc_lblMediaRedirectionStatus.anchor = GridBagConstraints.WEST;
		gbc_lblMediaRedirectionStatus.insets = new Insets(0, 0, 5, 5);
		gbc_lblMediaRedirectionStatus.gridx = 0;
		gbc_lblMediaRedirectionStatus.gridy = 4;
		kvmsCenterPanel.add(mediaRedirStatus, gbc_lblMediaRedirectionStatus);

		return gridBagLayout;
	}
	
	/**
	 * Shows First user Request Dialog
	 */
	private void showResponseDialog()
	{
		kvmsResponseDialog.setVisible(true);
		kvmsResponseDialog.repaint();
		kvmsResponseDialog.requestFocus();
	}

	/**
	 * Construct Dialog for second user
	 */
	private void constructRequestDialog()
	{
		JFrame frame = JViewer.getMainFrame();
		kvmsRequestDialog = new JDialog(frame,"",false);
		kvmsRequestPanel.setLayout(new BorderLayout());
		kvmsRequestPanel.add(kvmsRequestLabel,BorderLayout.CENTER);
		kvmsRequestDialog.setSize(900,50);
		kvmsRequestDialog.add(kvmsRequestPanel);
		kvmsRequestDialog.setUndecorated(true);
		kvmsRequestDialog.setResizable(false);
		kvmsRequestDialog.setLocation(JViewerApp.getInstance().getPopUpWindowPosition(kvmsRequestDialog.getWidth(),kvmsRequestDialog.getHeight()));
		return;
	}

	/**
	 * Shows Second user Request Dialog
	 */
	private void showRequestDialog()
	{
		kvmsRequestLabel.setText("       "+LocaleStrings.getString("H_8_KVMS")+" "+KVMSharing.KVM_CLIENT_USERNAME+" "+LocaleStrings.getString("H_5_KVMS")+" "+KVMSharing.KVM_CLIENT_IP+"(30 "+LocaleStrings.getString("H_7_KVMS")+" )");
		kvmsRequestDialog.setVisible(true);
		kvmsRequestDialog.requestFocus();
	}

	/**
	 * Sets status bit to indicate client 1 or 2
	 * @param status true - First Client, false - Second Client
	 **/
	public void setUserStatus(boolean status)
	{
		isMasterSession = status;
	}

	/**
	 * Constructs KVMShareDialog componant
	 */
	public void constructDialog(byte type)
	{
		kvmsTimer = new Timer(1000,new KVMShareDialogListener());
		if(type == KVM_SELECT_MASTER)
			kvmsDecisionDuration = 10;
		else
			kvmsDecisionDuration = 30;
		if(isMasterSession)
		{
			this.constructResponseDialog(type);
		}
		else
		{
			this.constructRequestDialog();
		}
	}

	/**
	 * Shows KVMShareDialog
	 *
	 */
	public void showDialog()
	{
		kvmsTimer.start();
		if(isMasterSession)
		{
			this.showResponseDialog();
		}
		else
		{
			this.showRequestDialog();
		}
	}

	public void showInformationDialog(String message)
	{
		JVFrame frame = JViewerApp.getInstance().getMainWindow();
		JOptionPane.showMessageDialog(frame,message,LocaleStrings.getString("H_10_KVMS"),JOptionPane.INFORMATION_MESSAGE);
		return;
	}

	public JButton getOkButton()
	{
		return kvmsSubmitButton;
	}

	public JLabel getUserTwoTimerLabel()
	{
		return kvmsRequestLabel;
	}

	public JDialog getKVMShareResponseDialog()
	{
		return kvmsResponseDialog;
	}

	public JDialog getKVMShareRequestDialog()
	{
		return kvmsRequestDialog;
	}

	public Timer getTimer()
	{
		return kvmsTimer;
	}

	public JLabel getTimerLabel()
	{
		return kvmsTimerLabel;
	}

	public JRadioButton getRadioButtonOne()
	{
		return fullAccessChoice;
	}

	public JRadioButton getRadioButtonTwo()
	{
		return partialAccessChoice;
	}

	public JRadioButton getRadioButtonThree()
	{
		return denyAccessChoice;
	}
	
	public JRadioButton getRadioButtonFour()
	{
		return blockRequests;
	}

	/**
	 * @return the kvmsButtonGroup
	 */
	public ButtonGroup getKvmsButtonGroup() {
		return kvmsButtonGroup;
	}

	/**
	 * @return the Dialog type
	 */
	public byte getDialogType() {
		return dialogType;
	}

	/**
	 * Dispose the KVMSharing Response dialog
	 */
	public void disposeKVMShareResponseDialog()
	{
		if(kvmsTimer != null)
			kvmsTimer.stop();
		kvmsDecisionDuration = 30;
		if(kvmsResponseDialog != null)
		{
			kvmsResponseDialog.dispose();
			kvmsResponseDialog = null;
		}
		if (reqUserDetails != null)
			JViewerApp.getInstance().getResponseDialogTable().remove(reqUserDetails);
	}

	public void disposeKVMShareReqestDialog()
	{
		if(kvmsTimer != null)
			kvmsTimer.stop();
		kvmsDecisionDuration = 30;
		if(kvmsRequestDialog != null){
			kvmsRequestDialog.dispose();
			kvmsRequestDialog = null;
		}
	}

	/**
	 * Just for testing purpose
	 * @param args
	 */
	/*public static void main(String[] args) {

		KVMShareDialog kd = KVMShareDialog.getInstance();

		// Here we are setting to be first or second user
		//  value          client
		//  =====          ======
		//  true           First Client
		//  false          Second Client
		kd.setUserStatus(false);

		kd.constructDialog();
		kd.showDialog();
	}*/

	/**
	 * DialogListener class to update timer value
	 */

	class KVMShareDialogListener implements ActionListener
	{
		/**
		 * Method invoked by the Timer class once its start method is called
		 */
		public void actionPerformed(ActionEvent e) {
			/*Decrement duration time*/
			kvmsDecisionDuration--;

			/*Once duration reaches 0,stop timer and dispatch dialog*/
			if(kvmsDecisionDuration <= 0)
			{
				getTimer().stop();
				kvmsDecisionDuration = 30;
				if(KVMShareDialog.isMasterSession)
				{
					Debug.out.println("User didn't gave any input");
					if(getKVMShareResponseDialog() != null)
						getKVMShareResponseDialog().dispose();
					if (reqUserDetails != null)
						JViewerApp.getInstance().getResponseDialogTable().remove(reqUserDetails);
				}
				else if(!KVMShareDialog.isMasterSession)
				{
					if(getKVMShareRequestDialog() != null)
						getKVMShareRequestDialog().dispose();
					Debug.out.println("Time out grant full access");
				}
			}

			/*Checking whether first or second client*/
			else if(KVMShareDialog.isMasterSession)
			{
				/*Update dialog*/
				getTimerLabel().setText("("+kvmsDecisionDuration+" "+LocaleStrings.getString("H_7_KVMS")+")");
				getTimerLabel().repaint();
			}
			else if(!KVMShareDialog.isMasterSession)
			{
				/*Update second user label status*/
				if(getKVMShareRequestDialog() != null){
					getUserTwoTimerLabel().setText("       "+LocaleStrings.getString("H_8_KVMS")+" "+KVMSharing.KVM_CLIENT_USERNAME+" "+LocaleStrings.getString("H_5_KVMS")+KVMSharing.KVM_CLIENT_IP+"  ("+kvmsDecisionDuration+" "+LocaleStrings.getString("H_7_KVMS")+")");
					getKVMShareRequestDialog().repaint();
				}
			}
		}
	}

	/**
	 * KeyListener used in the Dialog
	 */

	class KVMShareDialogKeyListener implements KeyListener
	{
		public void keyPressed(KeyEvent ke)
		{
			if(KVMShareDialog.isMasterSession)
			{
				if((ke.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK)
				{	// if block permission radio button is clicked then make the block permission combobox visible
					if(ke.getKeyCode() == KeyEvent.VK_B){
						blockPermission_comboBox.setVisible(true);
						getRadioButtonFour().setSelected(true);
						getRadioButtonFour().repaint();
					}else{
						blockPermission_comboBox.setVisible(false); // hide the block permission combobox otherwise
						if(ke.getKeyCode() == KeyEvent.VK_A)
						{
							getRadioButtonOne().setSelected(true);
							getRadioButtonOne().repaint();
						}
						else if(ke.getKeyCode() == KeyEvent.VK_V)
						{
							getRadioButtonTwo().setSelected(true);
							getRadioButtonTwo().repaint();
						}
						else if(ke.getKeyCode() == KeyEvent.VK_D)
						{
							getRadioButtonThree().setSelected(true);
							getRadioButtonThree().repaint();
						}
					}
				}
				else if(ke.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Debug.out.println ("***********ENTER KEY DETECTED********");
					getOkButton().doClick(); //Invoke OK Button
				}
				return;
			}
		}

		public void keyTyped(KeyEvent ke)
		{

		}

		public void keyReleased(KeyEvent ke)
		{

		}
	}

	/**
	 * ButtonListener for OK and Cancel Button in the Dialog
	 */
	class KVMShareDialogButtonListener implements ActionListener
	{
		JViewerApp RCApp = JViewerApp.getInstance();

		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource().equals(getOkButton()))
			{
				if(getDialogType() == KVMShareDialog.KVM_SHARING ||
						getDialogType() == KVMShareDialog.KVM_FULL_PERMISSION_REQUEST){
					Debug.out.println("Submit Clicked");
					if(getRadioButtonOne().isSelected())
					{
						Debug.out.println("Allow KVM is Selected");
						RCApp.getM_frame().onStopVMediaRedirection(JViewerApp.VM_DISCONNECT);
						RCApp.getKVMClient().SendKVMPrevilage(KVMSharing.KVM_REQ_ALLOWED, reqUserDetails);
						if(RCApp.isFullPermissionRequest())
							RCApp.setFullPermissionRequest(false);
						//Close all the other KVM Privilege request dialogs, if any, since the Master privilege
						//has been given away.
						Set<Entry<String, JDialog>> entrySet = JViewerApp.getInstance().getResponseDialogTable().entrySet();
						Iterator<Entry<String, JDialog>> itr = entrySet.iterator();
						JDialog dialog = null;
						String keyString = null;
						while(itr.hasNext()){
							try{
								Map.Entry<String, JDialog> mapEntry = (Map.Entry<String, JDialog>) itr.next();
								dialog = mapEntry.getValue();
								keyString = mapEntry.getKey();
								if(dialog!= null)
									dialog.dispose();
							}catch (Exception ex) {
								Debug.out.println(ex);
							}
						}
						JViewerApp.getInstance().getResponseDialogTable().clear();
					}
					else if(getRadioButtonTwo().isSelected())
					{
						Debug.out.println("Allow only Video is Selected");
						RCApp.OnSendKVMPrevilage(KVMSharing.KVM_REQ_PARTIAL, reqUserDetails);
						getKVMShareResponseDialog().dispose();
						getTimer().stop();
						kvmsDecisionDuration = 30;
						if (reqUserDetails != null)
							JViewerApp.getInstance().getResponseDialogTable().remove(reqUserDetails);
					}
					else if(getRadioButtonThree().isSelected())
					{
						Debug.out.println("Deny is Selected");
						RCApp.OnSendKVMPrevilage(KVMSharing.KVM_REQ_DENIED, reqUserDetails);
						getKVMShareResponseDialog().dispose();
						getTimer().stop();
						kvmsDecisionDuration = 30;
						if (reqUserDetails != null)
							JViewerApp.getInstance().getResponseDialogTable().remove(reqUserDetails);
					}
					else if(getRadioButtonFour().isSelected())
					{
						// make sure the block permission menu is visible before proceeding
						if(JViewerApp.getInstance().getJVMenu().getMenu(JVMenu.OPTIONS_BLOCK_FULL_PERMISSION) == null){
							JViewerApp.getInstance().getJVMenu().addBlockPermissionMenuItem();
						}
						if(blockPermission_comboBox.getSelectedIndex() == 0 || blockPermission_comboBox.getSelectedIndex() == -1){
							Debug.out.println("Block requests : Allow only Video is Selected");
							//send response to the requestion session
							RCApp.OnSendKVMPrevilage(KVMSharing.KVM_REQ_BLOCKED_PARTIAL, reqUserDetails);
							//select the block menu
							JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.OPTIONS_BLOCK_WITH_VIDEO_ONLY, true);							
						}else{
							Debug.out.println("Block requests : Deny Access is Selected");
							//send response to the requestion session
							RCApp.OnSendKVMPrevilage(KVMSharing.KVM_REQ_BLOCKED_DENY, reqUserDetails);
							//select the block menu
							JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.OPTIONS_BLOCK_WITH_DENY, true);
						}
						//dispose request dialog
						getKVMShareResponseDialog().dispose();
						//stop timer
						getTimer().stop();
						//reset the timer
						kvmsDecisionDuration = 30;
						if (reqUserDetails != null)
							JViewerApp.getInstance().getResponseDialogTable().remove(reqUserDetails);
					}
					else
					{
						JOptionPane.showMessageDialog(getKVMShareResponseDialog(),LocaleStrings.getString("H_9_KVMS"),LocaleStrings.getString("A_5_GLOBAL"),JOptionPane.ERROR_MESSAGE);
					}
					kvmsTimerLabel.setText("");
				}
				else if(getDialogType() == KVMShareDialog.KVM_SELECT_MASTER){
					if(getKvmsButtonGroup().getSelection() != null){
						String selectedMaster = getKvmsButtonGroup().getSelection().getActionCommand();
						if(selectedMaster == null)
							return;

						RCApp.sendSelectedMasterInfo(selectedMaster);
					}
					getKVMShareResponseDialog().dispose();
					getTimer().stop();
					kvmsDecisionDuration = 30;
				}
				return;
			}
		}
	}
	class RadioListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			JRadioButton button = (JRadioButton) e.getSource();
			// update the block permission combobox visibilty properly
			blockPermission_comboBox.setVisible((button.equals(getRadioButtonFour()) ? true : false));
			// update the status text
			if (button.getText().contains(LocaleStrings.getString("H_1_KVMS"))){
				mediaRedirStatus.setText(LocaleStrings.getString("H_14_KVMS"));
			}else{
				mediaRedirStatus.setText("");
			}
		}
	}  
}


