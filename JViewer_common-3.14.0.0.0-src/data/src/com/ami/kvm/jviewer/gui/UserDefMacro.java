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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.SoftBevelBorder;

import com.ami.kvm.jviewer.Debug;



public class UserDefMacro extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JPanel buttonPanel = null;
	private JList jList = null;
	private JButton jAddButton = null;
	private JButton jDeleteButton = null;
	private JButton jCloseButton = null;
	private JDialog jAddDialog = null;  //  @jve:decl-index=0:visual-constraint="695,232"
	private JLabel jAddLabel = null;
	private JTextArea jShowTextField = null;
	private JTextArea jUserInfo = null;
	private JButton jOKButton = null;
	private DefaultListModel model;
	private JScrollPane pane;
	private JButton jClearButton = null;
	private JButton jClearAllButton = null;
	private JButton jWindowsButton = null;
	private JButton jTabButton = null;
	private JButton jAltF4Button = null;
	private JButton jPrintScreen = null;

	private HashMap<String, String> macroMap;
	private HashMap<String, String> previousMap;

	private String code;

	private boolean addMacro = false;
	
	public final static int WIDTH = 615;
	public final static int HEIGHT = 355;

	/**
	 * This method initializes jList
	 *
	 * @return javax.swing.JList
	 */
	private JScrollPane getJList() {
		if (jList == null) {

			model = new DefaultListModel();
			try{
				macroMap = JViewerApp.getInstance().getAddMacro().getMacroMap();
				if(macroMap != null){
					Set set = macroMap.entrySet();

					Iterator i = set.iterator();
					while(i.hasNext()){
						Map.Entry me = (Map.Entry)i.next();
						model.addElement(me.getKey()) ;
					}
					jList = new JList(model);
					pane = new JScrollPane(jList);
					jList.setBounds(new Rectangle(5, 5, 600, 270));
					pane.setBounds(new Rectangle(5, 5, 600, 270));
					jList.setVisible(true);
					pane.setVisible(true);
				}
			}catch(NullPointerException npe){
				Debug.out.println(npe);
			}
		}
		return pane;
	}

	/**
	 * This method initializes JAddButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJAddButton() {
		if (jAddButton == null) {
			jAddButton = new JButton();
			jAddButton.setText(LocaleStrings.getString("T_2_UDM"));
			jAddButton.setSize(jAddButton.getPreferredSize());
			jAddButton.setVisible(true);
			jAddButton.addActionListener(new ActionListener() {
				@SuppressWarnings("static-access")
				public void actionPerformed(ActionEvent event) {

					if(model.size() < JViewerApp.getInstance().getAddMacro().MACRO_COUNT)
					{
						getJAddDialog();
						jShowTextField.setCaretPosition(0);
						jShowTextField.setRequestFocusEnabled(true);
						jShowTextField.requestFocus();
					}
					else
					{
						JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(),
								LocaleStrings.getString("T_3_UDM")+AddMacro.MACRO_COUNT+
								LocaleStrings.getString("T_4_UDM"), LocaleStrings.getString("T_1_UDM"),
								JOptionPane.INFORMATION_MESSAGE );
					}
				}
			});
		}
		return jAddButton;
	}

	/**
	 * This method initializes jDeleteButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJDeleteButton() {
		if (jDeleteButton == null) {
			jDeleteButton = new JButton();
			jDeleteButton.setText(LocaleStrings.getString("T_5_UDM"));
			jDeleteButton.setSize(jDeleteButton.getPreferredSize());
			jDeleteButton.setVisible(true);
			jDeleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					int check[] = jList.getSelectedIndices();
					// getSelectedValues() is deprecated in Java 1.7
					// Object[] keyname = jList.getSelectedValues();
					String[] keyname = new String[check.length];
					/*
					 * Note: we cannot delete the values in list based on index
					 * value. Because deleting an element will modify the index
					 * value. If more than one element is selected for deletion
					 * then we may get end up in attempt to delete an invalid or
					 * wrong index value. So deleting based on key value since
					 * we don't allow any duplicate entries in list.
					 */
					// getSelectedValues() equivalent
					for (int index = 0; index < check.length; index++) {
						keyname[index] = model.get(check[index]).toString();
					}
					/* Remove based on key value not index */
					for (int index = 0; index < check.length; index++) {
						// String remname = (String) keyname[j];
						// model.removeElement(remname);
						// macroMap.remove(remname);
						model.removeElement(keyname[index]);
						macroMap.remove(keyname[index]);
					}
				}
			});
		}
		return jDeleteButton;
	}

	/**
	 * This method initializes jCloseButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton();
			jCloseButton.setText(LocaleStrings.getString("T_6_UDM"));
			jCloseButton.setSize(jCloseButton.getPreferredSize());
			jCloseButton.setVisible(true);
			jCloseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onCloseDialog();
				}
			});
		}
		return jCloseButton;
	}

	/**
	 * This method is called when the user defined macro dialog is getting closed
	 */
	public void onCloseDialog(){
		JViewerApp.getInstance().getAddMacro().setMacroMap(macroMap);
		JViewerApp.getInstance().getAddMacro().parseKeycodeSendBMC();
		//remove old macro list
		JViewerApp.getInstance().getAddMacro().removeMacroMenu();
		JViewerApp.getInstance().getAddMacro().removeToolbarMacro();
		//add new macro list
		JViewerApp.getInstance().getAddMacro().addMacroMenu(macroMap);
		JViewerApp.getInstance().getAddMacro().addToolbarMacro(macroMap);
		addMacro = false;
		dispose();
	}

	/**
	 * This method initializes jAddDialog
	 *
	 * @return javax.swing.JDialog
	 */
	private JDialog getJAddDialog() {
		if (jAddDialog == null) {
			jAddDialog = new JDialog(this);
			jAddDialog.setSize(new Dimension(650, 260));
			jAddDialog.setTitle(LocaleStrings.getString("T_7_UDM"));
			jAddDialog.getContentPane().setLayout(new GridBagLayout());
			GridBagConstraints gridConstraints = new GridBagConstraints();
			gridConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridConstraints.insets = new Insets(2, 5, 5, 5);
			gridConstraints.gridheight = 1;
			gridConstraints.gridwidth = 1;
			gridConstraints.gridx = 0;
			gridConstraints.gridy = 0;
			gridConstraints.weightx = 1.0;
			jAddDialog.getContentPane().add(getUserInfoPanel(), gridConstraints);
			gridConstraints.insets = new Insets(5, 5, 5, 5);
			gridConstraints.gridy = 1;
			jAddDialog.getContentPane().add(getMacroButtonPanle(), gridConstraints);
			gridConstraints.gridy = 2;
			jAddDialog.getContentPane().add(getMacroDispalyTextPanel(), gridConstraints);
			gridConstraints.insets = new Insets(5, 5, 2, 5);
			gridConstraints.gridy = 3;
			jAddDialog.getContentPane().add(getControlButtonPanel(), gridConstraints);

			jAddDialog.setResizable(false);
			jAddDialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					addMacro = false;
					jShowTextField.setText("");
					jAddDialog.dispose();
				}
			});
		}
		addMacro = true;
		jAddDialog.setVisible(true);
		jAddDialog.setLocation(JViewerApp.getInstance().getPopUpWindowPosition(650, 260));
		return jAddDialog;
	}

	/**
	 * Returns the JPanel container, which contains the labels and text control which 
	 * displays the user information to use the Add Macro dialog 
	 * @return JPanel object
	 */
	private JPanel getUserInfoPanel(){
		JPanel userInfoPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridConstraints.insets = new Insets(5, 5, 5, 5);
		gridConstraints.gridheight = 1;
		gridConstraints.gridwidth = 1;
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		gridConstraints.weightx = 1.0;

		jAddLabel = new JLabel();
		jAddLabel.setText(LocaleStrings.getString("T_9_UDM"));
		jAddLabel.setSize(jAddLabel.getPreferredSize());
		userInfoPanel.add(jAddLabel, gridConstraints);

		jUserInfo = new JTextArea();
		jUserInfo.setEditable(false);
		jUserInfo.setCursor(null);
		jUserInfo.setOpaque(false);
		jUserInfo.setFocusable(false);
		jUserInfo.setFont(new Font("Dialog", Font.BOLD, 12));
		jUserInfo.setText(LocaleStrings.getString("T_8_UDM"));
		jUserInfo.setWrapStyleWord(true);
		jUserInfo.setSize(jUserInfo.getPreferredSize());
		gridConstraints.gridy = 1;
		userInfoPanel.add(jUserInfo, gridConstraints);
		
		return userInfoPanel;
	}

	/**
	 * Returns the JPanel container, which contains the Macro key buttons such as, Windows,
	 *  Tab, Alt+F4 , and Print Screen
	 * @return JPanel object
	 */	
	private JPanel getMacroButtonPanle(){
		JPanel macroButtonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridConstraints.insets = new Insets(5, 5, 5, 5);
		gridConstraints.gridheight = 1;
		gridConstraints.gridwidth = 1;
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;

		macroButtonPanel.add(getJWindowsButton(), gridConstraints);
		gridConstraints.gridx = 1;
		macroButtonPanel.add(getJTabButton(), gridConstraints);
		gridConstraints.gridx = 2;
		macroButtonPanel.add(getAltF4Button(), gridConstraints);
		gridConstraints.gridx = 3;
		macroButtonPanel.add(getPrintScreen(), gridConstraints);
		gridConstraints.gridx = 4;
		gridConstraints.weightx = 1.0;
		macroButtonPanel.add(new JPanel(), gridConstraints);
		return macroButtonPanel;
	}

	/**
	 * Returns the JPanel container, which contains the text field which displays
	 * the macro key combination being created
	 * @return JPanel object
	 */
	private JPanel getMacroDispalyTextPanel(){
		JPanel macroDisplayTextpPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridConstraints.insets = new Insets(5, 5, 5, 5);
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		gridConstraints.ipady = 10;
		gridConstraints.weightx = 1.0;
		macroDisplayTextpPanel.add(getJShowTextField(), gridConstraints);
		return macroDisplayTextpPanel;
	}
	
	/**
	 * Returns the JPanel container, which contains the Clear, Clear All, and Ok buttons 
	 * @return JPanel object
	 */
	private JPanel getControlButtonPanel(){
		JPanel controlButtonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridConstraints.insets = new Insets(5, 5, 5, 5);
		
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		gridConstraints.weightx = 1.0;		
		controlButtonPanel.add(new JPanel(), gridConstraints);
		gridConstraints.weightx = 0.0;
		gridConstraints.gridx = 1;
		controlButtonPanel.add(getJClearButton(), gridConstraints);
		gridConstraints.gridx = 2;
		controlButtonPanel.add(getJClearAllButton(), gridConstraints);
		gridConstraints.gridx = 3;
		controlButtonPanel.add(getJOKButton(), gridConstraints);
		return controlButtonPanel;
	}
	/**
	 * This method initializes jShowTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextArea getJShowTextField() {
		if (jShowTextField == null) {
			jShowTextField = new JTextArea();
			jShowTextField.enableInputMethods(false);
			jShowTextField.setEditable(false);
			jShowTextField.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

			jShowTextField.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent e) {
					//KEY TYPED
				}
				public void keyPressed(KeyEvent e) {

					if(e.getKeyCode() == KeyEvent.VK_TAB ||
							e.getKeyCode() == KeyEvent.VK_WINDOWS ||
							e.getKeyCode() == KeyEvent.VK_META ||
							e.getKeyCode() == KeyEvent.VK_UNDEFINED)
					{
						return;
					}
					displayHotkey(e.getKeyCode(), e.getKeyLocation());
					e.consume();
				}
				public void keyReleased(KeyEvent e) {
					//KEY RELEASED
				}
			});
		}
		else
			jShowTextField.setText("");
		return jShowTextField;
	}

	/**
	 * This method initializes jOKButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJOKButton() {
		if (jOKButton == null) {
			jOKButton = new JButton();
			jOKButton.setText(LocaleStrings.getString("A_3_GLOBAL"));
			jOKButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					String KeyMacro = jShowTextField.getText();
					if(model.size() < AddMacro.MACRO_COUNT)
					{
						for(int count = 0; count < model.size(); count++){
							if(model.elementAt(count).equals(KeyMacro)){
								JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(),
										LocaleStrings.getString("T_19_UDM"), LocaleStrings.getString("T_1_UDM"),
										JOptionPane.INFORMATION_MESSAGE );
								jShowTextField.setText("");
								jShowTextField.requestFocus();
								code = "";
								return;
							}
						}
						if(KeyMacro.length() > 0){
							macroMap.put(KeyMacro, code);
							model.addElement(KeyMacro);
						}
						code="";
						jShowTextField.setText("");
						jAddDialog.dispose();
						jAddDialog=null;
					}
					else
					{
						JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(),
								LocaleStrings.getString("T_3_UDM")+AddMacro.MACRO_COUNT+
								LocaleStrings.getString("T_4_UDM"),	LocaleStrings.getString("T_1_UDM"),
								JOptionPane.INFORMATION_MESSAGE );
					}
				}

			});
		}
		return jOKButton;
	}

	/**
	 * This method initializes jClearButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJClearButton() {
		if (jClearButton == null) {

			jClearButton = new JButton();
			jClearButton.setText(LocaleStrings.getString("T_10_UDM"));
			jClearButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String substring = new String();
					String current_text = jShowTextField.getText();
					String[] split_text = current_text.split("[+]");
					String[] split_Code = code.split("[+]");
					String New_text = new String() ;
					String New_Code = new String() ;
					if(split_text.length != 0)
					{
						for(int k=0;k<(split_text.length-1);k++)
						{
							New_text = New_text.concat(split_text[k]);
							New_text= New_text.concat("+");
						}

						for(int k=0;k<(split_Code.length-2);k++)
						{
							New_Code = New_Code.concat(split_Code[k]);
							New_Code= New_Code.concat("+");
						}
					}
					else
					{
						New_Code = "";
						code = "";
						substring="";
					}

					if(New_text.length() > 1)
					{
						int check = New_text.lastIndexOf('+');
						int check1 = New_Code.lastIndexOf('+');
						substring = New_text.substring(0,check);
						code = New_Code.substring(0,check1);
					}
					else{
						code = "";
					}
					jShowTextField.setText(substring);
					jShowTextField.setCaretPosition(0);
					jShowTextField.setRequestFocusEnabled(true);
					jShowTextField.requestFocus();
				}
			});
		}
		return jClearButton;
	}

	/**
	 * This method initializes jClearAllButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJClearAllButton() {
		if (jClearAllButton == null) {
			jClearAllButton = new JButton();
			jClearAllButton.setText(LocaleStrings.getString("T_11_UDM"));
			jClearAllButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jShowTextField.setText("");
					jShowTextField.requestFocus();
					code = "";
				}
			});
		}
		return jClearAllButton;
	}

	/**
	 * This method initializes jWindowsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJWindowsButton() {
		if (jWindowsButton == null) {
			jWindowsButton = new JButton();
			jWindowsButton.setText("Windows");
			jWindowsButton.addActionListener(new ActionListener() {
				@SuppressWarnings("static-access")
				public void actionPerformed(ActionEvent e) {

					String current_text = jShowTextField.getText();
					String current_code = Integer.toString(KeyEvent.VK_WINDOWS);
					if(current_text.length() == 0 )
					{
						code = code.concat(current_code);
						//Default location for Windows key event is set as Left.
						code = code.concat("+"+KeyEvent.KEY_LOCATION_LEFT);
						current_text = current_text.concat("Windows("+LocaleStrings.getString("T_12_UDM")+")");
					}
					else
					{
						String[] split_string = current_text.split("[+]");
						if(split_string.length < JViewerApp.getInstance().getAddMacro().SINGLE_MACRO_LENGTH)
						{
							code = code.concat("+"+current_code);
							//Default location for Windows key event is set as Left.
							code = code.concat("+"+KeyEvent.KEY_LOCATION_LEFT);
							current_text = current_text.concat("+Windows("+LocaleStrings.getString("T_12_UDM")+")");
						}
						else
						{
							maxMacroLimitMessage();
						}
					}
					jShowTextField.setText(current_text);
					jShowTextField.requestFocus();
				}
			});
		}
		return jWindowsButton;
	}

	/**
	 * This method initializes jTabButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJTabButton() {
		if (jTabButton == null) {
			jTabButton = new JButton();
			jTabButton.setText("Tab");
			jTabButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					String current_text = jShowTextField.getText();
					String current_code = Integer.toString(KeyEvent.VK_TAB);
					if(current_text.length() == 0 )
					{
						current_text = current_text.concat("Tab");
						code = code.concat(current_code);
						code = code.concat("+"+KeyEvent.KEY_LOCATION_STANDARD);

					}
					else
					{
						String[] split_string = current_text.split("[+]");
						if(split_string.length < AddMacro.SINGLE_MACRO_LENGTH)
						{
							code = code.concat("+"+current_code);
							code = code.concat("+"+KeyEvent.KEY_LOCATION_STANDARD);
							current_text = current_text.concat("+Tab");
						}
						else
						{
							maxMacroLimitMessage();
						}
					}
					jShowTextField.setText(current_text);
					jShowTextField.requestFocus();
				}
			});
		}
		return jTabButton;
	}

	
	/**
	 * This method initializes jAltF4Button
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAltF4Button(){
		if(jAltF4Button == null){
			jAltF4Button = new JButton();
			jAltF4Button.setText("Alt+F4");
			jAltF4Button.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					String current_text = jShowTextField.getText();
					String current_code = Integer.toString(KeyEvent.VK_ALT);
					if(current_text.length() == 0 )
					{
						current_text = current_text.concat("Alt("+LocaleStrings.getString("T_12_UDM")+")");
						code = code.concat(current_code);
						code = code.concat("+"+KeyEvent.KEY_LOCATION_LEFT);

						code = code.concat("+"+Integer.toString(KeyEvent.VK_F4));
						code = code.concat("+"+KeyEvent.KEY_LOCATION_STANDARD);
						current_text = current_text.concat("+F4");
					}
					else
					{
						String[] split_string = current_text.split("[+]");
						if(split_string.length < AddMacro.SINGLE_MACRO_LENGTH - 1)
						{
							code = code.concat("+"+current_code);
							code = code.concat("+"+KeyEvent.KEY_LOCATION_LEFT);
							current_text = current_text.concat("+Alt("+LocaleStrings.getString("T_12_UDM")+")");

							code = code.concat("+"+Integer.toString(KeyEvent.VK_F4));
							code = code.concat("+"+KeyEvent.KEY_LOCATION_STANDARD);
							current_text = current_text.concat("+F4");
						}
						else if(split_string.length == AddMacro.SINGLE_MACRO_LENGTH - 1)
						{
							JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(), 
									AddMacro.SINGLE_MACRO_LENGTH -1 +LocaleStrings.getString("T_17_UDM")+
									"Alt+F4"+LocaleStrings.getString("T_18_UDM"),LocaleStrings.getString("T_7_UDM"),
									JOptionPane.INFORMATION_MESSAGE);
						}
						else{
							maxMacroLimitMessage();
						}
					}
					jShowTextField.setText(current_text);
					jShowTextField.requestFocus();
				}
			});
		}
		return jAltF4Button;
	}

	/**
	 * This method initializes jPrintScreen
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getPrintScreen(){
		if(jPrintScreen == null){
			jPrintScreen = new JButton();
			jPrintScreen.setText("Print Screen");
			jPrintScreen.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {

					String current_text = jShowTextField.getText();
					String current_code = Integer.toString(KeyEvent.VK_PRINTSCREEN);
					if(current_text.length() == 0 )
					{
						current_text = current_text.concat("Print Screen");
						code = code.concat(current_code);
						code = code.concat("+"+KeyEvent.KEY_LOCATION_STANDARD);

					}
					else
					{
						String[] split_string = current_text.split("[+]");
						if(split_string.length < AddMacro.SINGLE_MACRO_LENGTH)
						{
							code = code.concat("+"+current_code);
							code = code.concat("+"+KeyEvent.KEY_LOCATION_STANDARD);
							current_text = current_text.concat("+Print Screen");
						}
						else
						{
							maxMacroLimitMessage();
						}
					}
					jShowTextField.setText(current_text);
					jShowTextField.requestFocus();
				}
			});
		}
		return jPrintScreen;
	}
	/**
	 * Constructor.
	 * @param owner
	 */
	public UserDefMacro(Frame owner) {

		super(owner);
		initialize();

		code = new String();
		if(macroMap == null){
			macroMap = new HashMap<String, String>();

		}
		HashMap<String, String> PreviousMap2 = JViewerApp.getInstance().getAddMacro().getMacroMap();
		previousMap = new HashMap<String, String>();
		Set set = PreviousMap2.entrySet();

		Iterator i = set.iterator();
		while(i.hasNext()){
			Map.Entry me = (Map.Entry)i.next();
			previousMap.put((String)me.getKey(), (String)me.getValue());
		}
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(WIDTH, HEIGHT);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(getJContentPane(), BorderLayout.CENTER);
		this.getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
		this.setTitle(LocaleStrings.getString("T_1_UDM"));
		setResizable(false);
		this.setVisible(true);
		this.setLocation(JViewerApp.getInstance().getPopUpWindowPosition(WIDTH, HEIGHT));
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				onCloseDialog();
			}
		});
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJList(), null);
			jContentPane.setVisible(true);
		}
		return jContentPane;
	}

	/**
	 * Returns the JPanel container, which contains the Add, Delete, and Close buttons.
	 * @return JPanel object
	 */
	private JPanel getButtonPanel(){
		if(buttonPanel == null){
			buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
			buttonPanel.add(getJAddButton(), null);
			buttonPanel.add(getJDeleteButton(), null);
			buttonPanel.add(getJCloseButton(), null);
			buttonPanel.setVisible(true);
		}
		return buttonPanel;
	}
	/**
	 * Displays the key event generated in the Add Macro dialog 
	 * @param keyCode - the key code of the key event.
	 * @param keyLocation - the key location of the key event.
	 */
	public void displayHotkey(int keyCode, int keyLocation){
		String keyText = null;
		String text = jShowTextField.getText();
		String[] split_string = text.split("[+]");

		keyText = KeyEvent.getKeyText(keyCode);
		if(keyText.equals("NumPad +")){
			keyText = "NumPad "+LocaleStrings.getString("T_16_UDM") ;
		}
		if(split_string.length < AddMacro.SINGLE_MACRO_LENGTH)
		{
			if(text.length() == 0)
			{
				jShowTextField.append(keyText);
				if(keyLocation != KeyEvent.KEY_LOCATION_STANDARD)
				{
					switch(keyLocation){

					case KeyEvent.KEY_LOCATION_LEFT:
						jShowTextField.append("("+LocaleStrings.getString("T_12_UDM")+")");
						break;
					case KeyEvent.KEY_LOCATION_RIGHT:
						jShowTextField.append("("+LocaleStrings.getString("T_13_UDM")+")");
						break;
					default:
						break;
					}
				}
				String current_code = Integer.toString(keyCode);
				code = code.concat(current_code);
				code = code.concat("+"+keyLocation);

			}
			else
			{

				String current_code = Integer.toString(keyCode);
				code = code.concat("+"+current_code);
				code = code.concat("+"+keyLocation);
				jShowTextField.append("+"+keyText);
				if(keyLocation != KeyEvent.KEY_LOCATION_STANDARD)
				{
					switch(keyLocation){

					case KeyEvent.KEY_LOCATION_LEFT:
						jShowTextField.append("("+LocaleStrings.getString("T_12_UDM")+")");
						break;
					case KeyEvent.KEY_LOCATION_RIGHT:
						jShowTextField.append("("+LocaleStrings.getString("T_13_UDM")+")");
						break;
					default:
						break;

					}
				}
			}
		}
		else
		{
			maxMacroLimitMessage();
		}
	}

	/**
	 * Returns the status whether add macro is enabled or not.
	 * @return  status of the addMacro
	 */
	public boolean isAddMacro() {
		return addMacro;
	}

	/**
	 * Show the message dialog when maximum user macro length is reached.
	 */
	private void maxMacroLimitMessage(){
		JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(), 
				LocaleStrings.getString("T_14_UDM")+AddMacro.SINGLE_MACRO_LENGTH+
				LocaleStrings.getString("T_15_UDM"),LocaleStrings.getString("T_7_UDM"),
				JOptionPane.INFORMATION_MESSAGE);
	}

} 

