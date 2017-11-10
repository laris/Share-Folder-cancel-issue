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
// StandAloneApp connection module
//

package com.ami.kvm.jviewer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.ami.kvm.jviewer.ClientConfig;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.common.oem.IOEMManager;
import com.ami.kvm.jviewer.videorecord.URLProcessor;
import com.ami.vmedia.VMApp;

/**
 * The StandAloneConnectionDialog creates and shows the StandAloneApp connection dialog through which
 * user can provide required information to establish a connection with the Host.
 */
@SuppressWarnings("serial")
public class StandAloneConnectionDialog  extends JDialog implements ActionListener, ItemListener{

	private final int WIDTH = 410;
	private final int HEIGHT = 280;
	private final int SECURE_CONNECT = 1;
	private final int HTTPS_CONNECTION_SUCCESS = 0;
	private final int ADMIN_USER = 4;
	private final int OEM_PROPRIETARY_USER = 5;
	/* Comparison using string values won't work in case of various localization
	 * So going with index values for application type combobox rather than constant Strings */
	private final int KVM_VMEDIA = 0;
	private final int VMEDIA_APP = 1;
	private final int MANAGE_VIDEO = 2;

	private String hostIP = null;
	private String username = null;
	private String password = null;
	private String webSessionToken = null;
	private String csrfToken = null;
	private static String selectedLocale = "EN";
	private int secWebPort = JViewer.HTTPS_PORT;// default https port number for secure web port.

	private JPanel textPanel;
	private JPanel btnPanel;
	private JPanel helpPanel;
	private JPanel progressPanel;
	private JPanel bottomPanel;
	private JTable videoTable;
	private JLabel ipLabel;
	private JLabel portLabel;
	private JLabel unameLabel;
	private JLabel passwdLabel;
	private JLabel localeLabel;
	private JLabel appTypeLabel;
	private JTextField ipText;
	private JTextField portText;
	private JTextField unameText;
	private JPasswordField passwdText;
	private JButton launchBtn;
	private JButton cancelBtn;
	private JComboBox localeList;
	private JComboBox appTypeList;
	private JLabel helpTextLabel;
	private DefaultTableModel model;
	private JRadioButton playVideoRBtn;
	private JRadioButton saveVideoRBtn;
	private ButtonGroup videoOption;
	private JProgressBar launchProgressBar;

	private URLProcessor urlProcessor;
	private Validator validator;
	private DialogKeyListener keyListener;
	private DialogWindowListener windowListener;
	private TextFieldFocusListener textFieldFocusListener;
	private boolean validate = true;

	private boolean firstCheck = true;
	private boolean webLogIn = false;
	private boolean buttonsEnabled = false;
	private String[][] videoFile;
	private int remotePathSupport = 0;
	private int appType = KVM_VMEDIA;

	/**
	 * The COnstructor.
	 * @param parent - The parent frame on which the dialog will be shown.
	 * @param hostIP - The IP address of the host.
	 * @param username - user name to log into the BMC.
	 * @param password - password to log into the BMC.
	 */
	public StandAloneConnectionDialog(JFrame parent, String hostIP, int webPort, String username,String password) {

		super(parent, LocaleStrings.getString("S_1_SACD")+JViewer.getTitle(), false);

		this.username = username;
		this.password = password;
		this.hostIP = hostIP;
		this.secWebPort = webPort;
		selectedLocale = JViewer.getLanguage();
		windowListener = new DialogWindowListener();
		addWindowListener(windowListener);
		constructDialog();
		enableDialog();
		// if all the necessary arguments are given and no unknown argument is found
		// then we can proceed validation
		if ( (JViewer.getArgLength() > 0)
				&& (!JViewer.isUnknownArgs())
				&& (JViewer.isStandAloneSupportedApps( JViewer.getLaunch() )) ) {
			showDialog();
		}
	}


	/**
	 * Constructs the StandAloneConnection dialog.
	 */

	private void constructDialog(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		keyListener = new DialogKeyListener();
		textFieldFocusListener = new TextFieldFocusListener();
		setBounds(new Rectangle((screenSize.width - WIDTH)/2, (screenSize.height - HEIGHT)/2, WIDTH, HEIGHT));
		setLayout(new BorderLayout());
		setResizable(false);
		textPanel = new JPanel();

		textPanel.setLayout(new GridBagLayout());
		GridBagConstraints gridCons = new GridBagConstraints();

		gridCons.fill = GridBagConstraints.HORIZONTAL;
		gridCons.insets = new Insets(10, 10, 5, 10);
		gridCons.gridx = 0;
		gridCons.gridy = 0;
		gridCons.gridwidth = 1;
		gridCons.gridheight = 1;
		gridCons.weightx = 0.0;

		ipLabel = new JLabel(LocaleStrings.getString("S_2_SACD")+" : ");
		textPanel.add(ipLabel, gridCons);

		gridCons.insets = new Insets(5, 10, 5, 10);
		gridCons.gridy = 1;

		portLabel = new JLabel(LocaleStrings.getString("S_3_SACD")+" : ");
		textPanel.add(portLabel, gridCons);

		gridCons.gridy = 2;

		unameLabel = new JLabel(LocaleStrings.getString("S_4_SACD")+" : ");
		textPanel.add(unameLabel, gridCons);

		gridCons.gridy = 3;

		passwdLabel = new JLabel(LocaleStrings.getString("S_5_SACD")+" : ");
		textPanel.add(passwdLabel, gridCons);

		gridCons.gridy = 4;

		localeLabel = new JLabel(LocaleStrings.getString("S_21_SACD")+" : ");
		textPanel.add(localeLabel, gridCons);

		gridCons.gridy = 5;

		appTypeLabel = new JLabel(LocaleStrings.getString("S_34_SACD")+" : ");
		textPanel.add(appTypeLabel, gridCons);

		gridCons.insets = new Insets(10, 10, 5, 10);
		gridCons.fill = GridBagConstraints.HORIZONTAL;
		gridCons.gridx = 1;
		gridCons.gridy = 0;
		gridCons.gridwidth = 1;
		gridCons.gridheight = 1;
		gridCons.weightx = 1.0;

		ipText = new JTextField();
		if(hostIP == null)
			ipText.setText("");
		else
			ipText.setText(hostIP);
		ipText.addKeyListener(keyListener);
		ipText.addFocusListener(textFieldFocusListener);
		textPanel.add(ipText, gridCons);

		gridCons.insets = new Insets(5, 10, 5, 10);
		gridCons.gridy = 1;

		portText = new JTextField();
		if(secWebPort < 0)
			portText.setText("");
		else
			portText.setText(String.valueOf(secWebPort));
		portText.addKeyListener(keyListener);
		portText.addFocusListener(textFieldFocusListener);
		textPanel.add(portText, gridCons);

		gridCons.gridy = 2;

		unameText = new JTextField();
		unameText.setText(username);
		unameText.addKeyListener(keyListener);
		unameText.addFocusListener(textFieldFocusListener);
		textPanel.add(unameText, gridCons);

		gridCons.gridy = 3;

		passwdText = new JPasswordField();
		passwdText.setText(password);
		passwdText.addKeyListener(keyListener);
		passwdText.addFocusListener(textFieldFocusListener);
		textPanel.add(passwdText, gridCons);

		gridCons.gridy = 4;
		localeList = new JComboBox(JViewer.getSupportedLocales());
		Locale locale = new Locale(selectedLocale.toLowerCase());
		String language = locale.getDisplayLanguage(new Locale(selectedLocale));
		localeList.setSelectedItem(language+JVMenu.LOCALE_CODE_START_DELIM+
									selectedLocale+JVMenu.LOCALE_CODE_END_DELIM);
		localeList.setAutoscrolls(true);
		localeList.addItemListener(this);
		textPanel.add(localeList, gridCons);

		gridCons.gridy = 5;
		appTypeList = new JComboBox(getStandAloneAppType());
		appTypeList.setSelectedIndex(KVM_VMEDIA);
		appTypeList.setAutoscrolls(true);
		appTypeList.addItemListener(this);
		textPanel.add(appTypeList, gridCons);

		getContentPane().add(textPanel, BorderLayout.NORTH);

		helpPanel = new JPanel();
		helpTextLabel = new JLabel(LocaleStrings.getString("S_40_SACD"));
		helpPanel.add(helpTextLabel);

		progressPanel = new JPanel();
		btnPanel = new JPanel();
		btnPanel.setLayout(new GridBagLayout());

		gridCons = new GridBagConstraints();
		gridCons.insets = new Insets(2, 15, 5, 10);
		gridCons.weightx = 0.0;

		launchBtn = new JButton(LocaleStrings.getString("S_6_SACD"));
		btnPanel.add(launchBtn, gridCons);

		gridCons.insets = new Insets(2, 5, 5, 10);
		gridCons.gridx = GridBagConstraints.RELATIVE;

		cancelBtn = new JButton(LocaleStrings.getString("S_7_SACD"));
		cancelBtn.addActionListener(this);
		cancelBtn.addKeyListener(keyListener);
		btnPanel.add(cancelBtn, gridCons);

		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridBagLayout());
		gridCons = new GridBagConstraints();
		gridCons.insets = new Insets(5, 10, 5, 10);
		gridCons.weightx = 1.0;
		gridCons.gridy = 0;
		gridCons.gridwidth = 1;
		gridCons.gridheight = 1;

		bottomPanel.add(helpPanel, gridCons);
		gridCons.insets = new Insets(0, 0, 10, 0);
		gridCons.weightx = 1.0;

		gridCons.gridy = 1;
		gridCons.fill = GridBagConstraints.NONE;
		bottomPanel.add(btnPanel, gridCons);
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		this.setAlwaysOnTop(true);
		this.pack();
		JViewerApp.getOEMManager().getOEMStandAloneConnectionDialog().customizeDialogComponents(getContentPane());
	}

	private void clearDialog() {
		getContentPane().removeAll();
		textPanel.removeAll();
		textPanel.updateUI();
		getContentPane().add(textPanel,BorderLayout.NORTH);
		getContentPane().add(btnPanel);
	}

	public abstract class MyTableModel extends AbstractTableModel {

		public boolean isEditable() {
			return false;
		}
	}

	private void constructTableDialog() {
		clearDialog();

		Object columnNames[] = { LocaleStrings.getString("S_32_SACD"),
				LocaleStrings.getString("S_33_SACD") };

		model = new DefaultTableModel(null, columnNames);
		videoTable = new JTable(model) {

			public boolean isCellEditable(int rowIndex, int vColIndex) {
				return false;
			}
		};
		textPanel.setLayout(new GridBagLayout());
		GridBagConstraints gridCons = new GridBagConstraints();
		GridBagConstraints rbtnGridCons = new GridBagConstraints();

		gridCons.fill = GridBagConstraints.BOTH;
		gridCons.insets = new Insets(10, 10, 5, 10);
		gridCons.gridx = 0;
		gridCons.gridy = 0;
		gridCons.gridwidth = 1;
		gridCons.gridheight = 1;
		gridCons.weightx = 1.0;
		gridCons.weighty = 0.5;

		JTableHeader header = videoTable.getTableHeader();
		header.setFont(new Font("Tahoma", Font.BOLD, 12));

		//un comment the below line to restrice user to select single selection
		// videoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		videoTable.setCellEditor(null);

		videoTable.addKeyListener(keyListener);
		videoTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (!buttonsEnabled) {
					enableDialog();
					buttonsEnabled = true;
				}
			}
		});
		videoTable.setBackground(Color.white);
		JTableHeader tableHeader = videoTable.getTableHeader();

		Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border style = BorderFactory.createTitledBorder(border, "",
				TitledBorder.LEFT, TitledBorder.ABOVE_TOP);
		tableHeader.setBorder(style);
		videoTable.getTableHeader().setPreferredSize(new Dimension(50, 23));

		JScrollPane pane = new JScrollPane(videoTable);
		gridCons.gridy = 1;
		textPanel.add(pane, gridCons);

		helpPanel.removeAll();
		helpPanel.setLayout(new GridBagLayout());
		rbtnGridCons = new GridBagConstraints();
		rbtnGridCons.insets = new Insets(0, 10, 5, 10);
		rbtnGridCons.fill = GridBagConstraints.HORIZONTAL;
		rbtnGridCons.gridx = 0;
		rbtnGridCons.gridy = 0;
		rbtnGridCons.gridwidth = 1;
		rbtnGridCons.gridheight = 1;
		rbtnGridCons.weightx = 1.0;
		videoOption = new ButtonGroup();

		playVideoRBtn = new JRadioButton(LocaleStrings.getString("S_30_SACD"));
		playVideoRBtn.setSelected(true);
		videoOption.add(playVideoRBtn);
		helpPanel.add(playVideoRBtn, rbtnGridCons);

		rbtnGridCons.gridx = 1;
		saveVideoRBtn = new JRadioButton(LocaleStrings.getString("S_31_SACD"));
		videoOption.add(saveVideoRBtn);
		helpPanel.add(saveVideoRBtn, rbtnGridCons);
		helpPanel.revalidate();
		helpPanel.repaint();

		gridCons.gridy = 2;
		gridCons.weighty = 0.0;
		textPanel.add(helpPanel, gridCons);

		gridCons.gridy = 3;
		textPanel.add(btnPanel, gridCons);

		launchBtn.setText(LocaleStrings.getString("A_3_GLOBAL"));


		textPanel.updateUI();
		getContentPane().add(textPanel);
		this.setTitle(LocaleStrings.getString("S_29_SACD"));

	}
	/**
	 * Shows the StandAloneConnection dialog box if any of the runtime arguments, host IP address, user name,
	 * or password is invalid. This dialog will prompt the user to enter the correct values.
	 */

	private void showDialog(){
		disableDialog();
		validate = true;
		validator = new Validator();
		validator.start();
		this.setVisible(true);
	}
	/**
	 * Returns the StandAloneConnectionDialog object.
	 * @return - StandAloneConnectionDialog object.
	 */
	private JDialog getDialog(){
		return this;
	}

	public JProgressBar getProgressBar(){
		return launchProgressBar;
	}
	/**
	 * Enables all the controls in the Dialog.
	 */
	private void enableDialog(){
		if (JViewer.isStandAloneApp() || JViewer.isVMApp())
			setDialogTitle(LocaleStrings.getString("S_1_SACD"));
		ipText.setEditable(true);
		portText.setEditable(true);
		unameText.setEditable(true);
		passwdText.setEditable(true);
		launchBtn.setEnabled(true);
		launchBtn.addActionListener(this);
		launchBtn.addKeyListener(keyListener);
		String launch = JViewer.getLaunch();
		// changes will be performed only one time, if valid value for -launch parameter is given
		if(JViewer.isStandAloneSupportedApps(launch) && firstCheck){
			// updating the apptype combo box according to the application type opted.
			// Also for enabling / disabling appType combo box if command line input is given
			if (launch.compareToIgnoreCase(JViewer.APP_TYPE_JVIEWER) == 0) {
				appTypeList.setSelectedIndex(KVM_VMEDIA);
				JViewer.setApptype(JViewer.APP_TYPE_STAND_ALONE);
			} else if (launch.compareToIgnoreCase(JViewer.APP_TYPE_VMAPP) == 0) {
				appTypeList.setSelectedIndex(VMEDIA_APP);
				JViewer.setApptype(JViewer.APP_TYPE_VMAPP);
			} else if (launch.compareToIgnoreCase(JViewer.APP_TYPE_DOWNLOAD_SAVE) == 0) {
				appTypeList.setSelectedIndex(MANAGE_VIDEO);
				JViewer.setApptype(JViewer.APP_TYPE_DOWNLOAD_SAVE);
			} else if (launch.compareToIgnoreCase(JViewer.APP_TYPE_PLAYER) == 0) {
				appTypeList.setSelectedIndex(MANAGE_VIDEO);
				JViewer.setApptype(JViewer.APP_TYPE_PLAYER);
			}
			appTypeList.setEnabled(false);
		// if no valid argument is found for -launch parameter the we can assume it as double click launch
		// the selection is left to user's desire
		}else{
			/** following condition is required to avoid disabled apptype combobox get enabled
			*   in case of propmting for missing credentials and waiting for user input */
			 if(!JViewer.isStandAloneSupportedApps(launch)){
				appTypeList.setEnabled(true);
			 }
		}

		// for enabling / disabling Language combo box if valid command line input is given for -lang / -localization parameter
		// changes also will be performed only once.
		if( (!JViewer.isDefaultLang()) && firstCheck){
			localeList.setEnabled(false);
		}else{
			/** following condition is required to avoid disabled language combobox get enabled
			*   in case of prompting for missing credentials and waiting for user input */
			if(JViewer.isDefaultLang()){
				localeList.setEnabled(true);
			}
		}
		showProgress(false);
		this.setVisible(true);
	}

	/**
	 * Disables all the controls in the Dialog.
	 */
	private void disableDialog(){
		setDialogTitle(LocaleStrings.getString("S_8_SACD"));
		launchBtn.requestFocus();
		ipText.setEditable(false);
		portText.setEditable(false);
		unameText.setEditable(false);
		passwdText.setEditable(false);
		launchBtn.setEnabled(false);
		launchBtn.removeActionListener(this);
		launchBtn.removeKeyListener(keyListener);
		localeList.setEnabled(false);
		appTypeList.setEnabled(false);
		showProgress(true);
	}

	/**
	 * Shows the progress bar on StanAloneConnection dialog, while connecting to the web server.
	 * @param state - if true the progress bar will be shown, and hides the progress bar if false.
	 * 
	 */
	private void showProgress(boolean state){
		GridBagConstraints gridCons = new GridBagConstraints();
		gridCons.fill = GridBagConstraints.HORIZONTAL;
		gridCons.weightx = 1.0;
		gridCons.gridy = 0;
		gridCons.gridwidth = 1;
		gridCons.gridheight = 1;
		if(state){
			helpTextLabel.setText("");
			bottomPanel.remove(helpPanel);
			progressPanel.removeAll();
			progressPanel.setLayout(new GridBagLayout());
			gridCons.insets = new Insets(15, 0, 0, 0);
			gridCons.ipady = 5;
			launchProgressBar = new JProgressBar();
			progressPanel.add(launchProgressBar, gridCons);
			launchProgressBar.setString(LocaleStrings.getString("S_8_SACD"));
			launchProgressBar.setStringPainted(true);
			launchProgressBar.setIndeterminate(true);
			gridCons.insets = new Insets(10, 10, 5, 10);
			gridCons.ipady = 0;
			bottomPanel.add(progressPanel, gridCons);
		}
		else{
			bottomPanel.remove(progressPanel);
			if(JViewer.isStandAloneApp() || JViewer.isVMApp()){
				gridCons.insets = new Insets(5, 10, 5, 10);
				helpTextLabel.setText(LocaleStrings.getString("S_40_SACD"));
				bottomPanel.add(helpPanel, gridCons);
			}
			else{
				helpTextLabel.setText("");
			}
			this.repaint();
		}
	}

	/**
	 * Gets the web session token using https request.
	 * @return Web session token.
	 */
	private int getWebSessionToken() {
		int ret = -1;
		urlProcessor = new URLProcessor(null, SECURE_CONNECT);
		urlProcessor.setHostIP(hostIP);
		ret = urlProcessor.processRequest(JViewer.getProtocol()+"://"+hostIP+":"+secWebPort+
				"/rpc/WEBSES/create.asp?WEBVAR_USERNAME="+username+"&WEBVAR_PASSWORD="+password);

		try{
			if(ret == URLProcessor.INVALID_CREDENTIALS){
				validate = false;
				enableDialog();
				ipText.setText(JViewer.getIp());
				unameText.setText(null);
				passwdText.setText(null);
				JOptionPane.showMessageDialog(getDialog(), LocaleStrings.getString("S_10_SACD"),
						JViewer.getTitle()+LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
				unameText.requestFocus();
			}
			else if(ret == HTTPS_CONNECTION_SUCCESS){
				getProgressBar().setString(LocaleStrings.getString("S_44_SACD"));
				webSessionToken = urlProcessor.getValue("'SESSION_COOKIE' : '", ',');
				webSessionToken.trim();
				webSessionToken = webSessionToken.substring(0, webSessionToken.lastIndexOf('\''));
				JViewer.setWebSessionToken(webSessionToken);
				JViewer.setPassword(password);
			}
			else if(ret == URLProcessor.HTTP_REQUEST_FAILURE){
				showProgress(false);
				JOptionPane.showMessageDialog(getDialog(), LocaleStrings.getString("S_11_SACD"),
						LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
				windowListener.windowClosing(null);
			}
			else if(ret == URLProcessor.INVALID_SERVER_CERT){
				windowListener.windowClosing(null);
			}
		}catch(Exception e){
			Debug.out.println(e);
			showProgress(false);
			JOptionPane.showMessageDialog(getDialog(), LocaleStrings.getString("S_11_SACD"),
					LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
			windowListener.windowClosing(null);
		}
		return ret;
	}

	/**
	 * Gets the KVM privilege of the given user.
	 * @return the user privilege.
	 */
	private int getKVMPrivilege(){
		int kvmPrivilege = 0;
		urlProcessor = new URLProcessor(webSessionToken, SECURE_CONNECT);
		int ret = urlProcessor.processRequest(JViewer.getProtocol()+"://"+this.hostIP+":"+secWebPort+"/rpc/getrole.asp");
		try{
			if(ret == HTTPS_CONNECTION_SUCCESS){
				getProgressBar().setString(LocaleStrings.getString("S_45_SACD"));
				kvmPrivilege = Integer.parseInt(urlProcessor.getValue("'EXTENDED_PRIV' : ", ' '));
			} else if(ret == URLProcessor.INVALID_SERVER_CERT){
				windowListener.windowClosing(null);
			}
		}catch (Exception e) {
			Debug.out.println(e);
			kvmPrivilege = JViewer.EXT_PRIV_UNDEF;
		}
		JViewer.setKVMPrivilege(kvmPrivilege);
		if(JViewer.isStandAloneApp()){
			if(!JViewer.isKVMEnabled()){
				resetDialogAndShowMessage(LocaleStrings.getString("S_12_SACD") + username +
						LocaleStrings.getString("S_13_SACD"));
				ret = -1;
			}
		}
		else if(JViewer.isVMApp()) {
			if(JViewer.isVMediaEnabled() == false) {
				resetDialogAndShowMessage(LocaleStrings.getString("S_12_SACD") + username +
						LocaleStrings.getString("S_43_SACD"));
				ret = -1;
			}
		}
		return ret;
	}

	/**
	 * Gets the adviser session token value using https request and
	 * using https request and. 
	 */
	private void getAdviserSessionToken() {
		String adviserSessionToken = null;
		urlProcessor = new URLProcessor(webSessionToken, SECURE_CONNECT);
		int ret = urlProcessor.processRequest(JViewer.getProtocol()+"://"+this.hostIP+":"+secWebPort+"/rpc/getsessiontoken.asp");
		try{
			if(ret == HTTPS_CONNECTION_SUCCESS){
				getProgressBar().setString(LocaleStrings.getString("S_49_SACD"));
				adviserSessionToken = urlProcessor.getValue("'SESSION_TOKEN' : '",',');
				adviserSessionToken.trim();
				adviserSessionToken = adviserSessionToken.substring(0, adviserSessionToken.lastIndexOf('\''));
				JViewer.setSessionCookies(adviserSessionToken);// set adviser session cookie.
			}

			else if(ret == URLProcessor.HTTP_REQUEST_FAILURE){
				showProgress(false);
				JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_11_SACD"),
						LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
				windowListener.windowClosing(null);
			}
			else if(ret == URLProcessor.INVALID_SERVER_CERT){
				windowListener.windowClosing(null);
			}
		}catch(Exception e){
			Debug.out.println(e);
			showProgress(false);
			JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_11_SACD"),
					LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
			windowListener.windowClosing(null);
		}

	}


	/**
	 * Gets the adviser configuration values using https request and
	 * sets this value for JViewer parameter.
	 */
	private void getAdviserConfig(){
		int kvmServiceStatus;
		int kvmSecureChannel;
		int kvmPort;
		int webport;
		int tempRetryCount = JViewer.KVM_DEF_RECONN_RETRY;
		int tempRetryTime = JViewer.KVM_DEF_RECONN_INTERVAL;
		long oemFetureStatus = 0;
		byte kvmLicenseStatus = 0;
		String keyboardLayout = JViewer.AUTO_DETECT_KEYBOARD;
		urlProcessor = new URLProcessor(webSessionToken, SECURE_CONNECT);
		try{
			int ret = urlProcessor.processRequest(JViewer.getProtocol()+"://"+this.hostIP+":"+secWebPort+"/rpc/getadvisercfg.asp");
			if(ret == HTTPS_CONNECTION_SUCCESS){
				//Checking KVM Service status, whether KVM service is enabled or not.
				getProgressBar().setString(LocaleStrings.getString("S_46_SACD"));
				kvmServiceStatus = Integer.parseInt(urlProcessor.getValue("'V_STR_KVM_STATUS' : ", ','));
				if(kvmServiceStatus == 0){
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_14_SACD"),
							LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
					windowListener.windowClosing(null);
				}
				kvmSecureChannel = Integer.parseInt(urlProcessor.getValue("'V_STR_SECURE_CHANNEL' : ", ','));
				JViewer.setSecureChannel(kvmSecureChannel);// set kvm secure channel value.

				kvmPort = Integer.parseInt(urlProcessor.getValue("'V_STR_KVM_PORT' : ", ','));
				webport = Integer.parseInt(urlProcessor.getValue("'V_STR_WEB_PORT' : ", ','));

				if (JViewer.isSinglePortEnabled() == true)
				{
					// set WEB port number as KVM Port .As SinglePort feature is Enabled.
					JViewer.setKVMPort(webport);
					// set SSL as true.As SinglePort feature is Enabled.
					JViewer.setUseSSL(true);
				}
				else
				{
					JViewer.setKVMPort(kvmPort);// set KVM port number.

					// Set KVM SSl status.
					if(kvmSecureChannel == 0)
						JViewer.setUseSSL(false);
					else if(kvmSecureChannel == 1)
						JViewer.setUseSSL(true);
				}
				//Set OEM Feature status value
				try{
					oemFetureStatus = Long.parseLong(urlProcessor.getValue("'V_STR_OEM_FEATURE_STATUS' : ", ' '));
					JViewer.setOEMFeatureStatus(oemFetureStatus);
				}catch (NumberFormatException nfe) {
					Debug.out.println(nfe);
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_36_SACD"),
							LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					oemFetureStatus = 0;
				}catch (Exception e) {
					Debug.out.println(e);
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_36_SACD"),
							LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					oemFetureStatus = 0;
				}

				// set reconnect configuration values if Reconnect feature is enabled
				if((JViewer.getOEMFeatureStatus() & JViewerApp.KVM_RECONNECT_SUPPORT) == JViewerApp.KVM_RECONNECT_SUPPORT){

					JViewer.setKVMReconnectEnabled(true);
					try{
						tempRetryCount = Integer.parseInt(urlProcessor.getValue("'V_STR_RETRY_COUNT' : ", ','));
					}catch (NumberFormatException nfe) {
						Debug.out.println(nfe);
						JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_40_SACD"),
								LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					}catch (Exception e) {
						Debug.out.println(e);
						JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_40_SACD"),
								LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					}
					JViewer.setRetryCount(tempRetryCount);//set retry count if Reconnect feature is enabled

					try{
						tempRetryTime = Integer.parseInt(urlProcessor.getValue("'V_STR_RETRY_INTERVAL' : ", ','));
					}catch (NumberFormatException nfe) {
						Debug.out.println(nfe);
						JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_39_SACD"),
								LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					}catch (Exception e) {
						Debug.out.println(e);
						JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_39_SACD"),
								LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					}
					JViewer.setRetryInterval(tempRetryTime);//set retry interval if Reconnect feature is enabled

				}

				//Set KVM License status value
				try{
					kvmLicenseStatus = Byte.parseByte(urlProcessor.getValue("'V_STR_KVM_LICENSE_STATUS' : ", ','));
					JViewer.setKVMLicenseStatus(kvmLicenseStatus);
				}catch (NumberFormatException nfe) {
					Debug.out.print(nfe);
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_37_SACD"),
							LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					JViewer.setKVMLicenseStatus((byte)0);
				}catch (Exception e) {
					Debug.out.print(e);
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_37_SACD"),
							LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					JViewer.setKVMLicenseStatus((byte)0);
				}

				// Set Host Physical Keyboard layout option
				try{
					keyboardLayout = urlProcessor.getValue("'V_STR_KEYBOARD_LAYOUT' : ", ',');
					if(keyboardLayout.startsWith("'") && keyboardLayout.endsWith("'")){
						try{
							int start = keyboardLayout.indexOf('\'') + 1;
							int end = keyboardLayout.lastIndexOf('\'');
							keyboardLayout = keyboardLayout.substring(start, end);
						}catch (IndexOutOfBoundsException iobe) {
							Debug.out.println(iobe);
							keyboardLayout = JViewer.AUTO_DETECT_KEYBOARD;
						}
						catch (Exception e) {
							Debug.out.println(e);
							keyboardLayout = JViewer.AUTO_DETECT_KEYBOARD;
						}
					}
				}catch (Exception e) {
					Debug.out.println(e);
					keyboardLayout = JViewer.AUTO_DETECT_KEYBOARD;
				}
				JViewer.setKeyboardLayout(keyboardLayout);

				// after getting oemFetureStatus, kvm cd/hd numbers should be updated.
				JViewer.setKVM_Num_CD(JViewer.getKVM_Num_CD());// set number of CD
				JViewer.setKVM_Num_HD(JViewer.getKVM_Num_HD());// set number of HD
			}
			else if(ret == URLProcessor.HTTP_REQUEST_FAILURE){
				JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_15_SACD"),
						LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
				windowListener.windowClosing(null);
			}
			else if(ret == URLProcessor.INVALID_SERVER_CERT){
				windowListener.windowClosing(null);
			}
		}catch(Exception e){
			Debug.out.println(e);
			JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_15_SACD"),
					LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
			windowListener.windowClosing(null);
		}
	}

	/**
	 * Parses the video file information from web
	 * Displays the video file information in table
	 */
	private String[][] findVideoData(String fileInfo) {

		String fileParam = null;
		String infoParam = null;
		if(JViewer.isRestService() == true){
			fileParam = "\"file\": \"";
			infoParam = "\"fileinfo\": \"";
		}
		else{
			fileParam = "'FILE_NAME' : '";
			infoParam = "'FILE_INFO' : '";
		}

		String search;
		String line,temp;
		int startIndex = 0;
		int endIndex = 0, end = 0;
		int rows = 0;
		int columns = 0;
		int MAX_COLUMN = 2;
		int index = -1;

		int availRows = getOccurances(fileInfo, fileParam);
		if (availRows > 0) {
			videoFile = new String[availRows][MAX_COLUMN];
			temp = fileInfo;
			while (rows < availRows) {

				columns = 0;
				line = null;
				search = null;
				search = fileParam;

				line = temp = temp.substring(endIndex);
				startIndex = line.indexOf(fileParam);
				line = line.substring(startIndex);

				end = endIndex = line.indexOf("}");

				if (startIndex > 0 && endIndex > 0) {
					line = line.substring(0, endIndex);

					while (columns < MAX_COLUMN) {

						if (search == null) {
							search = infoParam;
							line = line.substring(line.indexOf(search));
						}

						endIndex = line.indexOf("\",", search.length());
						if (endIndex < 0) {

							endIndex = line.indexOf('\\');
						}

						videoFile[rows][columns] = line.substring(
								search.length(), endIndex).trim();
						videoFile[rows][columns].trim();
						index = videoFile[rows][columns].indexOf('\\');
						if(index >= 0)
						{
							String parse = videoFile[rows][columns].substring(0, index)+videoFile[rows][columns].substring(index+1);
							videoFile[rows][columns] = parse;

						}
						if (videoFile[rows][columns].indexOf('\'') > 0)
							videoFile[rows][columns] = videoFile[rows][columns]
									.substring(0, videoFile[rows][columns]
											.indexOf('\''));

						if (search.equalsIgnoreCase(infoParam)) {
							index = videoFile[rows][0].lastIndexOf('/');
							if(index == -1)
								index = 0;
							else
								index++;
							model.addRow(new Object[] { videoFile[rows][0].substring(index),
									videoFile[rows][1] });

						}

						columns++;
						search = null;
					}
				}
				endIndex = startIndex + end + 1;
				rows++;
			}
		}
		//If Video files not available show message and Quit
		else{
			JOptionPane.showMessageDialog (
					this,
					LocaleStrings.getString("S_28_SACD"),
					LocaleStrings.getString("A_6_GLOBAL"),
					JOptionPane.INFORMATION_MESSAGE);
			windowListener.windowClosing(null);
		}

		return videoFile;

	}

	/**
	 * Gets the total number of occurances in the given sting
	*/
	public int getOccurances(String data, String search) {

		int count = 0;
		int idx = 0;
		String responseData = new String(data);

		while ((idx = responseData.indexOf(search, idx)) != -1) {
			count++;
			idx += search.length();
		}
		return count;

	}

	/**
	 * Gets the recorded video files information using https request and sets
	 * this value for video Files information.
	 */
	private void getVideoInfo() {

		String fileInfo = null;
		int ret = -1;

		constructTableDialog();

		urlProcessor = new URLProcessor(webSessionToken, SECURE_CONNECT);
		try {
			if(JViewer.isRestService() == true){
				urlProcessor.setHostIP(hostIP);
				ret = urlProcessor.restProcessRequest(JViewer.getProtocol()+"://"+this.hostIP+":"+secWebPort+"/api/logs/video");
			}
			else
				ret = urlProcessor.processRequest(JViewer.getProtocol()+"://" + this.hostIP+ ":" + secWebPort + "/rpc/getvideoinfo.asp");

			if (ret == HTTPS_CONNECTION_SUCCESS) {

				fileInfo = new String(urlProcessor.getData());
				findVideoData(fileInfo);
				// make sure that dialog components are updated properly.
				getContentPane().invalidate();
				getContentPane().validate();
			} else if (ret == URLProcessor.HTTP_REQUEST_FAILURE) {
				JOptionPane.showMessageDialog(this,
						LocaleStrings.getString("S_25_SACD"),
						LocaleStrings.getString("S_9_SACD"),
						JOptionPane.ERROR_MESSAGE);
				windowListener.windowClosing(null);
			} else if(ret == URLProcessor.INVALID_SERVER_CERT){
				windowListener.windowClosing(null);
			}
		} catch (Exception e) {
			Debug.out.println(e);
			JOptionPane.showMessageDialog(this,
					LocaleStrings.getString("S_25_SACD"),
					LocaleStrings.getString("S_9_SACD"),
					JOptionPane.ERROR_MESSAGE);
			windowListener.windowClosing(null);
		}
	}

	/**
	 * Gets the VMedia configuration values using https request and
	 * sets this value for JViewer parameter.
	 */
	private void getVMediaConfig(){
		String value;
		int vMediaSSL;

		urlProcessor = new URLProcessor(webSessionToken, SECURE_CONNECT);
		int ret = urlProcessor.processRequest(JViewer.getProtocol()+"://"+this.hostIP+":"+secWebPort+"/rpc/getvmediacfg.asp");

		if(ret == HTTPS_CONNECTION_SUCCESS){
			try{
				getProgressBar().setString(LocaleStrings.getString("S_47_SACD"));
				value = urlProcessor.getValue("'V_SINGLE_PORT_ENABLED' : ", ',');
				// set Single Port Feature status.
				if(1 == Integer.parseInt(value))
				{
					JViewer.setSinglePortEnabled(true);
				}
				else
				{
					JViewer.setSinglePortEnabled(false);
				}
				value = urlProcessor.getValue("'V_MEDIA_LICENSE_STATUS' : ", ',');
				JViewer.setMediaLicenseStatus((byte)Integer.parseInt(value));

				if(!JViewer.isSinglePortEnabled())
				{
					value = urlProcessor.getValue("'V_STR_SECURE_CHANNEL' : ", ',');
					vMediaSSL = Integer.parseInt(value);
					JViewer.setVMSecureChannel(vMediaSSL);//set VMedia securechannel

					if(vMediaSSL == 0){
						JViewer.setVMUseSSL(false);
						value = urlProcessor.getValue("'V_STR_CD_PORT' : ", ',');
						JViewer.setCdserver_port(Integer.parseInt(value));//set VMedia Non-SSL cdserver port
						value = urlProcessor.getValue("'V_STR_HD_PORT' : ", ',');
						JViewer.setHdserver_port(Integer.parseInt(value));//set HDServer port
					}
					else if(vMediaSSL == 1){
						JViewer.setVMUseSSL(true);
						value = urlProcessor.getValue("'V_STR_CD_SECURE_PORT' : ", ',');
						JViewer.setCdserver_port(Integer.parseInt(value));//set VMedia SSL cdserver port
						value = urlProcessor.getValue("'V_STR_HD_SECURE_PORT' : ", ',');
						JViewer.setHdserver_port(Integer.parseInt(value));//set VMedia SSL HDServer port
					}

				}
				value = urlProcessor.getValue("'V_NUM_CD' : ", ',');
				JViewer.setNum_CD((byte) Integer.parseInt(value));// set number of CD

				value = urlProcessor.getValue("'V_NUM_HD' : ", ',');
				JViewer.setNum_HD((byte) Integer.parseInt(value));// set number of HD

				value = urlProcessor.getValue("'V_CD_STATUS' : ", ',');
				JViewer.setCD_State(Integer.parseInt(value));// set cd-media service status.
				value = urlProcessor.getValue("'V_HD_STATUS' : ", ',');
				JViewer.setHD_State(Integer.parseInt(value));// set hd-media service status.

				value = urlProcessor.getValue("'V_KVM_NUM_CD' : ", ',');
				JViewer.setKVM_Num_CD((byte) Integer.parseInt(value));// set number of CD

				value = urlProcessor.getValue("'V_KVM_NUM_HD' : ", ',');
				JViewer.setKVM_Num_HD((byte) Integer.parseInt(value));// set number of HD
				value = urlProcessor.getValue("'V_POWER_SAVE_MODE' : ", ' ');//Last value in response data
				if(value != null){
					JViewer.setPowerSaveMode((byte) Integer.parseInt(value));// set power save mode status
				}
				else{
					Debug.out.println("V_POWER_SAVE_MODE not found in the response data");
				}
			}catch(NumberFormatException nfe){
				Debug.out.println(nfe);
				JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_16_SACD"),
						LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
				windowListener.windowClosing(null);
			}
			catch (NullPointerException npe) {
				Debug.out.println(npe);
				JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_16_SACD"),
						LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
				windowListener.windowClosing(null);
			}
		}
		else if(ret == URLProcessor.HTTP_REQUEST_FAILURE){
			JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_16_SACD"),
					LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
			windowListener.windowClosing(null);
		}
		else if(ret == URLProcessor.INVALID_SERVER_CERT){
			windowListener.windowClosing(null);
		}
	}

	/**
	 * Logout the web session once all the required configuration data are obtained.
	 */

	public void logoutWebSession() {

		if(JViewer.getWebSessionToken() == null)
			return;

		if(JViewer.isKVMReconnectEnabled())
		{
			if(JViewerApp.getInstance().getRetryConnection())
			{
				return;
			}
		}

		if (!(JViewer.isStandAloneApp() || JViewer.isVMApp())) {
			if (JViewerApp.getInstance().getVideorecordapp() != null)
				JViewerApp.getInstance().getVideorecordapp()
						.lockVideoFile(false);
		}
		if(webLogIn)
		{
			int ret = -1;
			urlProcessor = new URLProcessor(JViewer.getWebSessionToken(), SECURE_CONNECT);
			if(JViewer.isRestService() == true)
				ret = urlProcessor.restProcessRequest(JViewer.getProtocol()+"://"+this.hostIP+":"+secWebPort+"/api/session");
			else
				ret = urlProcessor.processRequest(JViewer.getProtocol()+"://"+this.hostIP+":"+secWebPort+"/rpc/WEBSES/logout.asp");

			if(ret != HTTPS_CONNECTION_SUCCESS){
				JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_17_SACD"),
						LocaleStrings.getString("S_18_SACD"), JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				webLogIn = false;
				URLProcessor.setCsrfToken(null);
				if(JViewerApp.getInstance().getM_webSession_token() != null) {
					JViewerApp.getInstance().setM_webSession_token(null);
				}
			}
		}
		// since all the asp requests to the server is over, closing the socket.
		try {
			if(URLProcessor.getSocket() != null){
				URLProcessor.getSocket().close();
				URLProcessor.setSocket(null);
			}
		} catch (IOException e) {
			Debug.out.println("Exception: while closing the socket : " + e);
		}
	}

	/**
	 * Display the available video files in the table for user selection
         */
	private void manageVideo() {
		String[] file;
		int rowCounts = videoTable.getSelectedRowCount();
		int rows[] = videoTable.getSelectedRows();
		int row = 0;
		file = new String[rowCounts];
		if (rowCounts > 0) {

			try {
				while (row < rowCounts) {

					file[row] = videoFile[rows[row]][0];

					row++;
				}
			} catch (Exception e) {
				Debug.out.println(e);
			}

			if (file.length > 0) {
				JViewer.setVideoFile(file);
				int status = checkFileAccess();
				if (status == URLProcessor.RECORDING_IN_PROGRESS){
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("U_3_VR"),
							LocaleStrings.getString("A_6_GLOBAL"), JOptionPane.INFORMATION_MESSAGE);
				} else if (status == URLProcessor.FILE_NOT_FOUND){
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("Z_3_URLP"),
							LocaleStrings.getString("A_5_GLOBAL"), JOptionPane.ERROR_MESSAGE);
				} else {
					this.setVisible(false);
					this.dispose();
					JViewerApp.getInstance().constructUI();
					JViewer.recording();
				}
			} else {
				JOptionPane.showMessageDialog(this,
						LocaleStrings.getString("S_34_SACD"),
						LocaleStrings.getString("S_26_SACD"),
						JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this,
					LocaleStrings.getString("S_34_SACD"),
					LocaleStrings.getString("S_26_SACD"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	/**
	 * Will launch application type respective to user selection
     */
	private void selectAppType(){
		if(appType ==KVM_VMEDIA){
			JViewer.setApptype(JViewer.APP_TYPE_STAND_ALONE);
			onConnectBtn();
		}
		else if(appType == VMEDIA_APP){
			JViewer.setApptype(JViewer.APP_TYPE_VMAPP);
			onConnectBtn();
		}
		//Launches the Manage Video redirection App
		else if(appType == MANAGE_VIDEO){
			//if video file is selected and the button text is "OK"
			if(launchBtn.getText().equals(LocaleStrings.getString("A_3_GLOBAL"))){
				//if play video option is selected
				if(playVideoRBtn.isSelected()){
					//for Player App single selection only is allowed
					if(videoTable.getSelectedRowCount() == 1) {
						JViewer.setApptype(JViewer.APP_TYPE_PLAYER);
						manageVideo();
					} else {
						JOptionPane.showMessageDialog(JViewerApp.getInstance().getConnectionDialog(),
								LocaleStrings.getString("S_35_SACD"),
								LocaleStrings.getString("S_26_SACD"),
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
				//if download video option is selected
				else if(saveVideoRBtn.isSelected()){
					JViewer.setApptype(JViewer.APP_TYPE_DOWNLOAD_SAVE);
					manageVideo();
				}
			}
			//shows the manage video dialog
			else{
				JViewer.setApptype(JViewer.APP_TYPE_DOWNLOAD_SAVE);
				onConnectBtn();
			}
		}
	}

	/**
	 * Action handler for the connect and cancel buttons.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(launchBtn)) {
			selectAppType();
		}
		else if (e.getSource().equals(cancelBtn)) {
			onCancelBtn();
		}
	}

	/**
	 * Invoked when the Connect button in the StandAloneConnection dialog is pressed.
	 * It will get the input values, and launch the JViewer, if all the inputs are valid.
	 */
	private void onConnectBtn(){
		hostIP = ipText.getText();
		secWebPort = JViewer.getWebPortNumber(portText.getText());
		username = unameText.getText();
		password = " ";
		char[] passwd = passwdText.getPassword();
		for(int i =0; i < passwd.length; i++){
			password +=passwd[i];
			passwd[i] = 0;
		}
		password  = password.trim();
		showDialog();
	}

	/**
	 * Invoked when the data required to launch the JViewer have been obtained.
	 * This will dispose the connection dialog, get the required library files
	 * and initiates redirection.
	 */
	private void onConnectionSuccess(){
		validate = false;
		showProgress(false);
		if(!JViewer.isplayerapp() && !JViewer.isdownloadapp()) {
			this.setVisible(false);
			this.dispose();
		}
		if (JViewer.isStandAloneApp()) {
			JViewerApp.getInstance().constructUI();
			JViewer.redirect();
		}
		else if(JViewer.isVMApp()){
			VMApp.launchApp();
			//temporary
			logoutWebSession();
		}
	}

	/**
	 * Invoked when the Cancel button in the StandAloneConnection dialog is pressed.
	 * It will dispose the dialog and end the application.
	 */
	private void onCancelBtn(){
		if(isWebLogIn()){
			logoutWebSession();
		}
		this.dispose();
		windowListener.windowClosing(null);
	}

	private String getDialogTitle(){
		return this.getTitle().trim();
	}
	private void setDialogTitle(String title){
		this.setTitle(title);
	}

	/**
	 * @return the windowListener
	 */
	public DialogWindowListener getWindowListener() {
		return windowListener;
	}

	class DialogKeyListener extends KeyAdapter{
		public void keyPressed(KeyEvent ke){
			if(ke.getKeyCode() == KeyEvent.VK_ENTER){
				if(ke.getSource().equals(cancelBtn)){
					onCancelBtn();
				}
				else{
					selectAppType();
				}
			}

		}
	}


	class DialogWindowListener extends WindowAdapter{
		public void windowClosing(WindowEvent e) {
			if(webLogIn)
				logoutWebSession();
			JViewer.exit(0);
		}
	}

	class TextFieldFocusListener extends FocusAdapter{
		public void focusGained(FocusEvent fe){
			if(fe.getSource().toString().contains("JTextField") ||
					fe.getSource().toString().contains("JPasswordField")){
				JTextField textField = (JTextField) fe.getSource();
				textField.selectAll();
			}
		}
	}
	/**
	 * The following class validates the input credentials and initiates the https connection to the BMC
	 */
	class Validator extends Thread{
		@Override
		public void run() {
			while(validate){
				String errorString = "";
				JTextField component = null;
				if(hostIP == null || hostIP.length() == 0){
					errorString += "\n* "+LocaleStrings.getString("S_2_SACD");
					component = ipText;
				}
				else{
					if(JViewer.getServerIP(hostIP) == null){
						errorString += "\n* "+LocaleStrings.getString("S_2_SACD");
						ipText.selectAll();
						component = ipText;
					}
					else{
						JViewer.setServerIP(JViewer.getServerIP(hostIP));
						hostIP = JViewer.getIp();
					}
				}
				if(secWebPort < 0){
					errorString += "\n* "+LocaleStrings.getString("S_3_SACD");
					portText.setText("");
					if(component == null)
						component = portText;
				}
				else {
					// update the port number in JViewer class
					JViewer.setWebPort(secWebPort);
					//Standalone application will always use websecure
					JViewer.setWebSecure(1);
				}
				if(username == null || username.length() == 0){
					errorString += "\n* "+LocaleStrings.getString("S_4_SACD");
					if(component == null)
						component = unameText;
				}
				else {
					// update the username in JViewer class
					JViewer.setUsername(username);
				}
				/* for supporting null passwords */
//				if(password == null || password.length() == 0){
//					errorString += "\n* "+LocaleStrings.getString("S_5_SACD");
//					if(component == null)
//						component = passwdText;
//				}
				if(errorString.length()!=0){
					validate = false;
					firstCheck = false;
					enableDialog();

					//While launching the StandAloneApp, show the error message dialog of missing credentials
					//only if some of the arguments are specified and some are not, or if its not being validated
					//for the first time. This will prevent the pop up being displayed while launching the
					//StandAloneApp by double clicking.
					if(JViewer.getArgLength() > 0 || !firstCheck){
						JOptionPane.showMessageDialog(getDialog(), LocaleStrings.getString("S_19_SACD")+
								errorString, LocaleStrings.getString("S_9_SACD"),
								JOptionPane.INFORMATION_MESSAGE);
					}
					firstCheck = false;
					component.requestFocus();
					component.selectAll();
					return;
				}
				else {
					if(JViewer.isStandalone() || JViewer.isVMApp()){
						//make standalone app to allow error pop-up show on top if any error
						getDialog().setAlwaysOnTop(false);
						//load libraries inorder to aviod launching same jar twice to same IP
						JViewer.setClientCfg(new ClientConfig());
						getDialog().setAlwaysOnTop(true);
					}
					JViewer.setWebSecure(SECURE_CONNECT);
					int status = JViewerApp.getOEMManager().getOEMAuthentication().authenticate(username, password);
					if (status == IOEMManager.AMI_CODE) {
						//SSLv3 has been Disabled, no need to Force standalone application to use SSLv3 to use for https RPC calls
						//System.setProperty("https.protocols", "SSLv3");
						// try for rest service, if it fails restGetCSRToken() will return negative value
						int ret = restGetCSRFToken();
						if (ret < 0) {
							// if it fails due to invalid credentials, return from here and prompt with new connection dialog
							if(ret == URLProcessor.INVALID_CREDENTIALS){
								validate = false;
								return;
							}
							// incase of failure, set rest service to false and fall back to ASP calls.
							JViewer.setRestService(false);
							if (getWebSessionToken() < 0) {
								validate = false;
								return;
							}
							else
								webLogIn = true;
							if (getKVMPrivilege() < 0) {
								validate = false;
								logoutWebSession();
								return;
							}
							getVMediaConfig();
							getAdviserConfig();
							getAdviserSessionToken();
						}
						else {
							// incase of success, remember the rest service value.
							JViewer.setRestService(true);
							webLogIn = true;
							//Check whether the user is privileged to launch KVM sessions.
							if(JViewer.isStandAloneApp()){
								if(!JViewer.isKVMEnabled()){
									resetDialogAndShowMessage(LocaleStrings.getString("S_12_SACD") + username +
											LocaleStrings.getString("S_13_SACD"));
									validate = false;
									logoutWebSession();
									return;
								}
							}
							else if(JViewer.isVMApp()){
								//Check whether the user is privileged to launch VMedia sessions.
								if(!JViewer.isVMediaEnabled()){
									resetDialogAndShowMessage(LocaleStrings.getString("S_12_SACD") + username +
											LocaleStrings.getString("S_43_SACD"));
									validate = false;
									logoutWebSession();
									return;
								}
							}
							restGetVMediaConfig();
							if (JViewer.isStandAloneApp()) {
								//requires only for video session							
								restGetAdviserConfig();
							}
							restGetAdviserSessionToken();
						}
						if (JViewer.isStandAloneApp()) {
							if(JViewer.getKVMLicenseStatus() != JViewer.LICENSED)
							{
								Debug.out.println("JVIEWER LICENSE STATUS : "+JViewer.getKVMLicenseStatus());
								JOptionPane.showMessageDialog(getDialog(), LocaleStrings.getString("1_6_JVIEWER"),
										LocaleStrings.getString("1_3_JVIEWER"),
										JOptionPane.ERROR_MESSAGE);
								windowListener.windowClosing(null);
							}
						}
						else if(JViewer.isVMApp()){
							if(JViewer.getMediaLicenseStatus() != JViewer.LICENSED)
							{
								Debug.out.println("VMEDIA LICENSE STATUS : "+JViewer.getMediaLicenseStatus());
								JOptionPane.showMessageDialog(getDialog(), LocaleStrings.getString("1_7_JVIEWER"),
										LocaleStrings.getString("1_3_JVIEWER"),
										JOptionPane.ERROR_MESSAGE);
								windowListener.windowClosing(null);
							}
						}
						//get video file information only for player/download App
						else {
							getVideoInfo();
							String launch = JViewer.getLaunch();
							// For enabling / disabling radio buttons in Manage Video app
							if(JViewer.isStandAloneSupportedApps(launch)){
								if (launch.compareToIgnoreCase(JViewer.APP_TYPE_DOWNLOAD_SAVE) == 0) {
									playVideoRBtn.setEnabled(false);
									saveVideoRBtn.setSelected(true);
								} else if (launch.compareToIgnoreCase(JViewer.APP_TYPE_PLAYER) == 0) {
									playVideoRBtn.setSelected(true);
									saveVideoRBtn.setEnabled(false);
								}
							}
						}
						if (!JViewer.isSinglePortEnabled()
								&& (JViewer.isStandAloneApp()
										|| JViewer.isVMApp())) {
								logoutWebSession();
						}
						onConnectionSuccess();
					} else if (status == IOEMManager.OEM_CUSTOMIZED){
						onConnectionSuccess();
					} else {
						validate = false;
						return;
					}

				}
			}
		}
	}


	/**
	 * This method replaces all the occurrences of the given pattern using a replacement pattern.
	 * If the pattern appears continuously in the source string, the entire sequence will be replaced
	 * with a single occurrence of  the replacement pattern. The method will return the source string
	 * itself if the source string doesn't contain the pattern to be replaced.<br><br>
	 * <b>Example:</b><br>
	 * replaceAllPattern("a*b***c", "*", "-") returns a-b-c.
	 * @param source - Source string.
	 * @param pattern - Pattern to be replaced.
	 * @param replacement - Replacement pattern.
	 * @return Modified string.
	 */
	public static String replaceAllPattern( String source, String pattern, String replacement){
		int index = 0;
		int count = 1;
		String temp = "";
		for(int i = 0; i < count; i++){
			if((index = source.indexOf(pattern, index)) >= 0){
				index += pattern.length();
				if(source.indexOf(pattern, index) == index){
					count++;
				}
				temp+=pattern;
			}
		}
		try{
			if(temp.length() > 0){
				source = source.replaceFirst(temp, replacement);
			}
			else
				return source;
		}catch (NullPointerException npe) {
			Debug.out.println("REPLACE ALL PATTERN : ");
			Debug.out.println(npe);
			return source;
		}
		return replaceAllPattern(source, pattern, replacement);
	}

	/**
	 * Extracts the libraries from the corresponding jar files into the lib directory.
	 */
	public static boolean getWrapperLibrary(String libName){
		String libSource = null;
		if(JViewer.is64bit()){//64 bit OS
			if(System.getProperty("os.name").startsWith("Windows"))// Windows
				libSource = "lib/win64/";
			else if(System.getProperty("os.name").startsWith("Linux"))// Linux
				libSource = "lib/linux64/";
			else if(System.getProperty("os.name").startsWith("Mac"))// Mac
				libSource = "lib/mac64/";
		}

		if(libName != null){
			String currPath = null;
			currPath = JViewer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			//Remove an extra / that comes in the path
			currPath = currPath.substring(0, currPath.lastIndexOf('/'));
			//If there is any white space in a directory name, it will be represented
			//as %20 in the currPath, in Linux and Mac file system. It should replaced with a '\'.
			if(currPath.contains("%20")){
				currPath = currPath.replaceAll("%20", "\\ ");
			}

			String destinationPath = "Jar"+File.separator+JViewer.getIp()+File.separator+"lib";

			//If there is any occurrence of ':' in a directory name,
			//it should replaced with a '_'.
			if(System.getProperty("os.name").startsWith("Windows")){
				if(destinationPath.contains(":")){
					destinationPath = replaceAllPattern(destinationPath, ":", "_");
				}
			}
			destinationPath = currPath + File.separator +destinationPath;
			File destinationFile = new File(destinationPath);
			if(!destinationFile.exists())
				destinationFile.mkdirs();

			File libraryFile = new File(destinationFile+File.separator+libName);
			try {
				InputStream inStream = com.ami.kvm.jviewer.JViewer.class.getResourceAsStream(libSource+libName); // get the input stream
				//Avoid library files from being overwritten on Unix file systems.
				if(JViewerApp.getInstance().isLinuxClient() ||
						JViewerApp.getInstance().isMacClient()){
					//If the library files are already loaded, return false.
					if(!isUnixFileClosed(libraryFile)){
						inStream.close();
						return false;
					}
				}
				FileOutputStream fileOutStream = new FileOutputStream(libraryFile);
				// write contents of 'inStream' to 'fileOutStream'
				while (inStream.available() > 0) {
					fileOutStream.write(inStream.read());
				}
				fileOutStream.close();
				inStream.close();
			} catch (FileNotFoundException e) {
				Debug.out.println(e);
				return false;
			} catch (IOException e) {
				Debug.out.println(e);
				return false;
			}
			catch (Exception e){
				Debug.out.println(e);
				return false;
			}
		}
		return true;
	}
	
	public void itemStateChanged(ItemEvent ie) {
		if(ie.getSource().equals(localeList) && ie.getStateChange() == ItemEvent.DESELECTED){
			selectedLocale = (String) localeList.getSelectedItem();
			String pattern = JVMenu.LOCALE_CODE_START_DELIM;
			int startIndex = selectedLocale.indexOf(pattern)+pattern.length();
			int endIndex = selectedLocale.indexOf(JVMenu.LOCALE_CODE_END_DELIM);
			selectedLocale = selectedLocale.substring(startIndex, endIndex);
			JViewer.setLanguage(selectedLocale);
			//removing the itemListener from localeList to avoid the itemStateChange()
			// event being triggered when the item in the JComboBox list gets rearranged
			//in the onLanguageChange() method.
			localeList.removeItemListener(this);
			onLanguageChange();
			//adding item listener back again.
			localeList.addItemListener(this);
		}
		if(ie.getSource().equals(appTypeList) && ie.getStateChange() == ItemEvent.DESELECTED){
			appType = appTypeList.getSelectedIndex();
			if(appType == -1){ // just in case of no option is selected
				appType = KVM_VMEDIA;
			}
			if(appType == KVM_VMEDIA)
				helpTextLabel.setText(LocaleStrings.getString("S_40_SACD"));
			if(appType == VMEDIA_APP)
				helpTextLabel.setText(LocaleStrings.getString("S_41_SACD"));
			if(appType == MANAGE_VIDEO)
				helpTextLabel.setText(LocaleStrings.getString("S_42_SACD"));
			// This will Causes dialog Window to be sized to fit the preferred size and layouts.
			this.pack();
		}
	}

	private void onLanguageChange(){
		this.setTitle(LocaleStrings.getString("S_1_SACD"));
		ipLabel.setText(LocaleStrings.getString("S_2_SACD")+" : ");
		portLabel.setText(LocaleStrings.getString("S_3_SACD")+" : ");
		unameLabel.setText(LocaleStrings.getString("S_4_SACD")+" : ");
		passwdLabel.setText(LocaleStrings.getString("S_5_SACD")+" : ");
		localeLabel.setText(LocaleStrings.getString("S_21_SACD")+" : ");
		appTypeLabel.setText(LocaleStrings.getString("S_34_SACD")+" : ");
		// kbdLayoutLabel.setText(LocaleStrings.getString("S_21_SACD")+" : ");
		launchBtn.setText(LocaleStrings.getString("S_6_SACD"));
		cancelBtn.setText(LocaleStrings.getString("S_7_SACD"));
		helpTextLabel.setText(LocaleStrings.getString("S_40_SACD"));
		/* Following piece of code is required for proper localization update
		 * in language combobox upon changing to new language */
		String[] newItems = JViewer.getSupportedLocales();
		int selectedIndex = localeList.getSelectedIndex();
		localeList.removeAllItems();
		for(String newItem : newItems){
			localeList.addItem(newItem);
		}
		localeList.setSelectedIndex(selectedIndex);
		/* Following piece of code is required for proper localization update
		 * in Application Type combobox upon changing to new language */
		String[] apptypeItems = getStandAloneAppType();
		int selectedIndexAppType = appTypeList.getSelectedIndex();
		appTypeList.removeAllItems();
		for(String apptypeItem : apptypeItems){
			appTypeList.addItem(apptypeItem);
		}
		appTypeList.setSelectedIndex(selectedIndexAppType);
		// This will Causes dialog Window to be sized to fit the preferred size and layouts.
		this.pack();
	}

	public static String getSelectedLocale() {
		return selectedLocale;
	}
	public static void setSelectedLocale(String selectedLocale) {
		StandAloneConnectionDialog.selectedLocale = selectedLocale;
	}

	public int getRemotePathSupport() {
		return remotePathSupport;
	}

	public void setRemotePathSupport(int remotePathSupport) {
		this.remotePathSupport = remotePathSupport;
	}

	public boolean isWebLogIn() {
		return webLogIn;
	}

	public void setWebLogIn(boolean webLogIn) {
		this.webLogIn = webLogIn;
	}

	public URLProcessor getUrlProcessor() {
		return urlProcessor;
	}

	private String[] getStandAloneAppType(){
		/* Following piece of code is required for proper localization update
		 * in Application Type combobox */
		String[] apptype = {LocaleStrings.getString("S_38_SACD"),
				LocaleStrings.getString("S_39_SACD"),
				LocaleStrings.getString("S_27_SACD") };
		return apptype;
	}

	/**
	 * Clears the connection dialog state and shows the given error message
	 * @param message - Error message to be shown after clearing the dialog.
	 */

	private void resetDialogAndShowMessage(String message) {
		validate = false;
		enableDialog();
		ipText.setText(JViewer.getIp());
		unameText.setText(null);
		passwdText.setText(null);
		JOptionPane.showMessageDialog(getDialog(), message, LocaleStrings.getString("S_9_SACD"),
				JOptionPane.ERROR_MESSAGE);
		unameText.requestFocus();

	}

	/**
	 * Gets the web session token using https request (uses REST service).
	 * @return Web session token.
	 */

	public int restGetCSRFToken() {
		urlProcessor = new URLProcessor(null, SECURE_CONNECT);
		urlProcessor.setHostIP(hostIP);
		int ret = urlProcessor.restProcessRequest(JViewer.getProtocol()+"://"+hostIP+":"+secWebPort+
				"/api/session?username="+username+"&password="+password);
		try{
			if(ret == HTTPS_CONNECTION_SUCCESS){
				getProgressBar().setString(LocaleStrings.getString("S_48_SACD"));
				int extendedPrivilege = -1;
				extendedPrivilege = Integer.parseInt(urlProcessor.getValue("\"extendedpriv\": ", ','));
				JViewer.setKVMPrivilege(extendedPrivilege);
				webSessionToken = urlProcessor.getValue("Set-Cookie: ",';');
				JViewer.setWebSessionToken(webSessionToken);
				csrfToken = urlProcessor.getValue("\"CSRFToken\": ", ' ');
				csrfToken.trim();
				URLProcessor.setCsrfToken(csrfToken.substring(1, csrfToken.length()-1));
				JViewer.setPassword(password);
			}
			else if(ret == URLProcessor.INVALID_CREDENTIALS){
				validate = false;
				enableDialog();
				ipText.setText(JViewer.getIp());
				unameText.setText(null);
				passwdText.setText(null);
				JOptionPane.showMessageDialog(getDialog(), LocaleStrings.getString("S_10_SACD"),
						JViewer.getTitle()+LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
				unameText.requestFocus();
			}
			else if(ret == URLProcessor.INVALID_SERVER_CERT){
				windowListener.windowClosing(null);
			}
		}catch(Exception e){
			Debug.out.println(e);
			showProgress(false);
			JOptionPane.showMessageDialog(getDialog(), LocaleStrings.getString("S_11_SACD"),
					LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
			windowListener.windowClosing(null);
		}
		return ret;
	}

	/**
	 * Gets the VMedia configuration values using https request and
	 * sets this value for JViewer parameter (uses REST service).
	 */
	private void restGetVMediaConfig(){
		String value;
		int vMediaSSL;
		long oemFetureStatus = 0;
		urlProcessor = new URLProcessor(webSessionToken, SECURE_CONNECT);
		urlProcessor.setHostIP(hostIP);
		//GET https://ip:port/api/settings/media/instance
		int ret = urlProcessor.restProcessRequest(JViewer.getProtocol()+"://"+this.hostIP+":"+secWebPort+"/api/settings/media/instance");

		if(ret == HTTPS_CONNECTION_SUCCESS){
			try{
				getProgressBar().setString(LocaleStrings.getString("S_47_SACD"));
				value = urlProcessor.getValue("\"single_port_enabled\": ", ',');
				// set Single Port Feature status.
				if(1 == Integer.parseInt(value))
				{
					JViewer.setSinglePortEnabled(true);
					if(JViewer.getWebSecure() == 0)
						JViewer.setUseSSL(false);
					else if(JViewer.getWebSecure() == 1)
						JViewer.setUseSSL(true);
				}
				else
				{
					JViewer.setSinglePortEnabled(false);
				}
				value = urlProcessor.getValue("\"license\": ", ',');
				JViewer.setMediaLicenseStatus((byte)Integer.parseInt(value));
				if(!JViewer.isSinglePortEnabled())
				{
					value = urlProcessor.getValue("\"secure_channel\": ", ',');
					vMediaSSL = Integer.parseInt(value);
					JViewer.setVMSecureChannel(vMediaSSL);//set VMedia securechannel

					if(vMediaSSL == 0){
						JViewer.setVMUseSSL(false);
						value = urlProcessor.getValue("\"cd_port\": ", ',');
						JViewer.setCdserver_port(Integer.parseInt(value));//set VMedia Non-SSL cdserver port
						value = urlProcessor.getValue("\"hd_port\": ", ',');
						JViewer.setHdserver_port(Integer.parseInt(value));//set HDServer port
					}
					else if(vMediaSSL == 1){
						JViewer.setVMUseSSL(true);
						value = urlProcessor.getValue("\"cd_secure_port\": ", ',');
						JViewer.setCdserver_port(Integer.parseInt(value));//set VMedia SSL cdserver port
						value = urlProcessor.getValue("\"hd_secure_port\": ", ',');
						JViewer.setHdserver_port(Integer.parseInt(value));//set VMedia SSL HDServer port
					}
				}

				//Set OEM Feature status value
				try{
					oemFetureStatus = Long.parseLong(urlProcessor.getValue("\"oemFeature\": ", ' '));
					JViewer.setOEMFeatureStatus(oemFetureStatus);
				}catch (NumberFormatException nfe) {
					Debug.out.println(nfe);
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_36_SACD"),
							LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					oemFetureStatus = 0;
				}catch (Exception e) {
					Debug.out.println(e);
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_36_SACD"),
							LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					oemFetureStatus = 0;
				}

				value = urlProcessor.getValue("\"num_cd\": ", ',');
				JViewer.setNum_CD((byte) Integer.parseInt(value));// set number of CD

				value = urlProcessor.getValue("\"num_hd\": ", ',');
				JViewer.setNum_HD((byte) Integer.parseInt(value));// set number of HD

				value = urlProcessor.getValue("\"cd_status\": ", ',');
				JViewer.setCD_State(Integer.parseInt(value));// set cd-media service status.
				value = urlProcessor.getValue("\"hd_status\": ", ',');
				JViewer.setHD_State(Integer.parseInt(value));// set hd-media service status.

				value = urlProcessor.getValue("\"kvm_num_cd\": ", ',');
				JViewer.setKVM_Num_CD((byte) Integer.parseInt(value));// set number of CD

				value = urlProcessor.getValue("\"kvm_num_hd\": ", ',');
				JViewer.setKVM_Num_HD((byte) Integer.parseInt(value));// set number of HD
				value = urlProcessor.getValue("\"power_save_mode\": ", ',');//Last value in response data
				if(value != null){
					JViewer.setPowerSaveMode((byte) Integer.parseInt(value));// set power save mode status
				}
				else{
					Debug.out.println("power_save_mode not found in the response data");
				}
			}catch(NumberFormatException nfe){
				Debug.out.println(nfe);
				JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_16_SACD"),
						LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
				windowListener.windowClosing(null);
			}
			catch (NullPointerException npe) {
				Debug.out.println(npe);
				JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_16_SACD"),
						LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
				windowListener.windowClosing(null);
			}
		}
		else if(ret == URLProcessor.HTTP_REQUEST_FAILURE){
			JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_16_SACD"),
					LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
			windowListener.windowClosing(null);
		}
		else if(ret == URLProcessor.INVALID_SERVER_CERT){
			windowListener.windowClosing(null);
		}
	}

	/**
	 * Gets the adviser configuration values using https request and
	 * sets this value for JViewer parameter (uses REST service).
	 */
	private void restGetAdviserConfig(){
		int kvmServiceStatus;
		int kvmSecureChannel;
		int kvmPort;
		int webport;
		int tempRetryCount = JViewer.KVM_DEF_RECONN_RETRY;
		int tempRetryTime = JViewer.KVM_DEF_RECONN_INTERVAL;
		byte kvmLicenseStatus = 0;
		String keyboardLayout = JViewer.AUTO_DETECT_KEYBOARD;
		urlProcessor = new URLProcessor(webSessionToken, SECURE_CONNECT);
		urlProcessor.setHostIP(hostIP);
		//GET https://ip:port/api/settings/media/adviser
		try{
			int ret = urlProcessor.restProcessRequest(JViewer.getProtocol()+"://"+this.hostIP+":"+secWebPort+"/api/settings/media/adviser");
			if(ret == HTTPS_CONNECTION_SUCCESS){
				getProgressBar().setString(LocaleStrings.getString("S_46_SACD"));
				//Checking KVM Service status, whether KVM service is enabled or not.
				kvmServiceStatus = Integer.parseInt(urlProcessor.getValue("\"status\": ", ','));
				if(kvmServiceStatus == 0){
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_14_SACD"),
							LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
					windowListener.windowClosing(null);
				}
				kvmSecureChannel = Integer.parseInt(urlProcessor.getValue("\"secure_channel\": ", ','));
				JViewer.setSecureChannel(kvmSecureChannel);// set kvm secure channel value.

				kvmPort = Integer.parseInt(urlProcessor.getValue("\"kvm_port\": ", ','));
				webport = Integer.parseInt(urlProcessor.getValue("\"web_port\": ", ','));

				if (JViewer.isSinglePortEnabled() == true)
				{
					// set WEB port number as KVM Port .As SinglePort feature is Enabled.
					JViewer.setKVMPort(webport);


					if(JViewer.getWebSecure() == 0)
						JViewer.setUseSSL(false);
					else if(JViewer.getWebSecure() == 1)
						JViewer.setUseSSL(true);
				}
				else
				{
					JViewer.setKVMPort(kvmPort);// set KVM port number.

					// Set KVM SSl status.
					if(kvmSecureChannel == 0)
						JViewer.setUseSSL(false);
					else if(kvmSecureChannel == 1)
						JViewer.setUseSSL(true);
				}

				// set reconnect configuration values if Reconnect feature is enabled
				if((JViewer.getOEMFeatureStatus() & JViewerApp.KVM_RECONNECT_SUPPORT) == JViewerApp.KVM_RECONNECT_SUPPORT){

					JViewer.setKVMReconnectEnabled(true);
					try{
						tempRetryCount = Integer.parseInt(urlProcessor.getValue("\"retry_count\": ", ','));
					}catch (NumberFormatException nfe) {
						Debug.out.println(nfe);
						JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_40_SACD"),
								LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					}catch (Exception e) {
						Debug.out.println(e);
						JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_40_SACD"),
								LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					}
					JViewer.setRetryCount(tempRetryCount);//set retry count if Reconnect feature is enabled

					try{
						tempRetryTime = Integer.parseInt(urlProcessor.getValue("\"retry_interval\": ", ','));
					}catch (NumberFormatException nfe) {
						Debug.out.println(nfe);
						JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_39_SACD"),
								LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					}catch (Exception e) {
						Debug.out.println(e);
						JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_39_SACD"),
								LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					}
					JViewer.setRetryInterval(tempRetryTime);//set retry interval if Reconnect feature is enabled

				}

				//Set KVM License status value
				try{
					kvmLicenseStatus = Byte.parseByte(urlProcessor.getValue("\"license\": ", ','));
					JViewer.setKVMLicenseStatus(kvmLicenseStatus);
				}catch (NumberFormatException nfe) {
					Debug.out.print(nfe);
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_37_SACD"),
							LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					JViewer.setKVMLicenseStatus((byte)0);
				}catch (Exception e) {
					Debug.out.print(e);
					JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_37_SACD"),
							LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
					JViewer.setKVMLicenseStatus((byte)0);
				}

				// Set Host Physical Keyboard layout option
				try{
					keyboardLayout = urlProcessor.getValue("\"keyboard_layout\": ", ' ');
					/* Here (in Rest Service) the response will be obtained as double quoted values,
					 * rather than single quoted values. Hence if we try to parse by assuming
					 * it as single quoted value, Following scenario will occur:
					 *
					 * Even if proper keyboard layout is configured in web UI, here in JViewer
					 * it goes for AutoDetect ("AD"), rather then reflecting the changes configured
					 *
					 * So the proposed fix is to parse the value using double quotes rather than single quotes */
					if(keyboardLayout.startsWith("\"") && keyboardLayout.endsWith("\"")){
						try{
							int start = keyboardLayout.indexOf('\"') + 1;
							int end = keyboardLayout.lastIndexOf('\"');
							keyboardLayout = keyboardLayout.substring(start, end);
						}catch (IndexOutOfBoundsException iobe) {
							Debug.out.println(iobe);
							keyboardLayout = JViewer.AUTO_DETECT_KEYBOARD;
						}
						catch (Exception e) {
							Debug.out.println(e);
							keyboardLayout = JViewer.AUTO_DETECT_KEYBOARD;
						}
					}
				}catch (Exception e) {
					Debug.out.println(e);
					keyboardLayout = JViewer.AUTO_DETECT_KEYBOARD;
				}
				JViewer.setKeyboardLayout(keyboardLayout);

				// after getting oemFetureStatus, kvm cd/hd numbers should be updated.
				JViewer.setKVM_Num_CD(JViewer.getKVM_Num_CD());// set number of CD
				JViewer.setKVM_Num_HD(JViewer.getKVM_Num_HD());// set number of HD
			}
			else if(ret == URLProcessor.HTTP_REQUEST_FAILURE){
				JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_15_SACD"),
						LocaleStrings.getString("S_9_SACD"),JOptionPane.ERROR_MESSAGE);
				windowListener.windowClosing(null);
			}
			else if(ret == URLProcessor.INVALID_SERVER_CERT){
				windowListener.windowClosing(null);
			}
		}catch(Exception e){
			Debug.out.println(e);
			JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_15_SACD"),
					LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
			windowListener.windowClosing(null);
		}
	}

	/**
	 * Gets the adviser session token value using https request and
	 * using https request (uses REST service).
	 */
	private void restGetAdviserSessionToken() {
		String adviserSessionToken = null;

		urlProcessor = new URLProcessor(webSessionToken, SECURE_CONNECT);
		urlProcessor.setHostIP(hostIP);
		int ret = urlProcessor.restProcessRequest(JViewer.getProtocol()+"://"+this.hostIP+":"+secWebPort+"/api/kvm/token");
		try{
			if(ret == HTTPS_CONNECTION_SUCCESS){
				getProgressBar().setString(LocaleStrings.getString("S_49_SACD"));
				adviserSessionToken = urlProcessor.getValue("\"token\": ", ',');
				JViewer.setSessionCookies(adviserSessionToken.substring(1, adviserSessionToken.length()-1));
				//JViewer.setSessionCookies(adviserSessionToken);// set adviser session cookie.
			}

			else if(ret == URLProcessor.HTTP_REQUEST_FAILURE){
				showProgress(false);
				JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_11_SACD"),
						LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
				windowListener.windowClosing(null);
			}
			else if(ret == URLProcessor.INVALID_SERVER_CERT){
				windowListener.windowClosing(null);
			}
		}catch(Exception e){
			Debug.out.println(e);
			showProgress(false);
			JOptionPane.showMessageDialog(this, LocaleStrings.getString("S_11_SACD"),
					LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
			windowListener.windowClosing(null);
		}

	}

	public void setWebSessionToken(String webSessionToken) {
		this.webSessionToken = webSessionToken;
	}

	/**
	 * Check whether a file is closed in Unix file system
	 * @param file - the file to be verified.
	 * @return true if file is closed; false otherwise
	 */
	public static boolean isUnixFileClosed(File file) {
		Process plsof = null;
		BufferedReader reader = null;
		try {
			//create a process to run the lsof command in linux, to list the open files.
			plsof = new ProcessBuilder(new String[]{"lsof", "|", "grep", file.getAbsolutePath()}).start();
			reader = new BufferedReader(new InputStreamReader(plsof.getInputStream()));
			String line;
			while((line=reader.readLine())!=null) {
				//if the file is present in the list of open files, it means file is already open.
				if(line.contains(file.getAbsolutePath())) {
					reader.close();//close the InputStreamReader
					plsof.destroy();// destroy the process.
					return false;
				}
			}
		} catch(Exception ex) {
			Debug.out.println(ex);
			return false;
		}
		try {
			reader.close();//close the InputStreamReader
		} catch (IOException e) {
			Debug.out.println(e);
		}
		plsof.destroy();// destroy the process.
		return true;//the file is not opened.
	}

	/**
	 * Check whether requested video file is
	 * being processed by video recording process.
	 */
	private int checkFileAccess() {
		int secureConnect = 1; // StandAlone will always uses secure connection.
		int retVal = URLProcessor.FILE_NOT_FOUND;
		String[] VideFilename = JViewer.getVideoFile();
		String url = JViewer.getProtocol()+"://" + hostIP + ":" + secWebPort;
		// In Manage Video Table, only one video file can be selected.
		// Multiple selection is not allowed.
		// So the String array will always contains single element.
		// Hence accessing here using 0th index value.
		if ((VideFilename[0] != null) && (VideFilename[0].length() > 0)) {
			URLProcessor urlProcessor = new URLProcessor(JViewer.getWebSessionToken(), secureConnect);

			// if reset service is running use rest functions
			if (JViewer.isRestService()) {
				url += "/api/logs/video-log?file_name=" + VideFilename[0] + "&file_access="
						+ URLProcessor.CHECK_FILE_ACCESS;
				retVal = urlProcessor.restProcessRequest(url);
			} else {
				url += "/rpc/downloadvideo.asp?FILE_NAME=" + VideFilename[0] + "&FILE_ACCESS="
						+ URLProcessor.CHECK_FILE_ACCESS;
				retVal = urlProcessor.processRequest(url);
			}
		}

		return retVal;
	}
}
