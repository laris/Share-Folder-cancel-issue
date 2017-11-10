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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.kvmpkts.Mousecaliberation;
import com.ami.kvm.jviewer.hid.USBMouseRep;
import com.ami.kvm.jviewer.kvmpkts.KVMClient;
import com.ami.vmedia.VMApp;
public class JVToolBar extends JToolBar implements MouseListener {

	//	Local variables

	/**
	 * 
	 */
	private JButton   altTab;
	private JToolBar  m_toolbar;
	public  JButton   mouseBtn;
	private  JButton   cdBtn;
	private  JButton   hardddiskBtn;
	private  JButton   kbdBtn;
	private  JButton   playPauseBtn;
	private JButton   fullScreenBtn;
	private  JButton   videoRecordBtn;
	private  JButton   powerBtn;
	private  JButton   hostDisplayBtn;
	private  JSlider   slider_zoom;
	private  JButton   zoomBtn;

	private JPopupMenu m_popupMenu;
	private JPopupMenu m_zoomPopupMenu;
	private  JButton 	m_hotKeyBtn;
	private JMenuItem   altCtrlDelBtn;
	protected static Hashtable<JButton ,String> m_menutoolbar_text = new Hashtable<JButton,String>();

	public JButton usersBtn;
	private JPopupMenu userPopupMenu;

	protected JButton createButton(String iconPath, String tooltip, int prefX, int prefY, int maxX, int maxY)
	{
		URL imageURL;
		if (iconPath == null)
			imageURL = null;
		else
			imageURL = com.ami.kvm.jviewer.JViewer.class.getResource(iconPath);
		JButton button = new JButton() { public Point getToolTipLocation(MouseEvent event) { return new Point(getWidth(), getHeight()-40); } };
		if (imageURL != null) {
			button.setIcon(new ImageIcon(imageURL));
		}
		button.setToolTipText(tooltip);
		//button.setPreferredSize(new Dimension(prefX, prefY));
		button.setPreferredSize(new Dimension(maxX, maxY));
		button.setMaximumSize(new Dimension(maxX, maxY));
		button.setMinimumSize(new Dimension(maxX, maxY));
		button.setVisible(true);
		button.setRequestFocusEnabled(false);
		button.addMouseListener(this);
		return button;
	}

	@SuppressWarnings("serial")
	public JVToolBar ()  {

		m_toolbar = new JToolBar();
		ToolTipManager.sharedInstance().setInitialDelay(0);

		mouseBtn  	   = createButton ("res/Mouse2Btn.png", LocaleStrings.getString("Q_1_JVT"), 23, 23, 33, 33); // number are preferred X, Y and Max x and Y
		m_menutoolbar_text.put(mouseBtn,"Q_1_JVT");

		setCDBtn(createButton ("res/CD.png", LocaleStrings.getString("Q_2_JVT"), 23, 23, 33, 33));
		m_menutoolbar_text.put(getCDBtn(),"Q_2_JVT" );

		setHardddiskBtn(createButton ("res/HD.png", LocaleStrings.getString("Q_4_JVT"), 23, 23, 33, 33));
		m_menutoolbar_text.put(getHardddiskBtn() , "Q_4_JVT");

		setKbdBtn(createButton ("res/keyboard.png", LocaleStrings.getString("Q_5_JVT"), 23, 23, 33, 33));
		m_menutoolbar_text.put(getKbdBtn(),"Q_5_JVT");

		setFullScreenBtn(createButton ("res/Maxi.png", LocaleStrings.getString("Q_6_JVT"), 23, 23, 33, 33));
		m_menutoolbar_text.put(getFullScreenBtn(),"Q_6_JVT");

		//altTab		   = createButton (null, "Send Alt+Tab", 100, 20, 120, 20);
		setVideoRecordBtn(createButton ("res/start_record.png", LocaleStrings.getString("Q_7_JVT"), 23, 23, 33, 33));
		m_menutoolbar_text.put(getVideoRecordBtn(),"Q_7_JVT");

		setPlayPauseBtn(createButton ("res/pause.png", LocaleStrings.getString("Q_8_JVT"), 23, 23, 33, 33));
		m_menutoolbar_text.put(getPlayPauseBtn(),"Q_8_JVT");

		setM_hotKeyBtn(createButton ("res/Hot-keys.png", LocaleStrings.getString("Q_10_JVT"), 23, 23, 33, 33));
		m_menutoolbar_text.put(getM_hotKeyBtn(),"Q_10_JVT");

		usersBtn	= createButton ("res/user.png", LocaleStrings.getString("Q_11_JVT"), 23, 23, 33, 33);
		m_menutoolbar_text.put(usersBtn,"Q_11_JVT");

		setPowerBtn(createButton("res/poweroff.png", LocaleStrings.getString("Q_12_JVT"), 23, 23, 33, 33));
		m_menutoolbar_text.put(getPowerBtn(),"Q_12_JVT");

		setHostDisplayBtn(createButton("res/monitorOn.png", LocaleStrings.getString("Q_21_JVT"), 23, 23, 33, 33));
		m_menutoolbar_text.put(getHostDisplayBtn(),"Q_21_JVT");
		setZoomBtn(createButton("res/zoom.png", LocaleStrings.getString("Q_25_JVT"), 23, 23, 33, 33));
		m_menutoolbar_text.put(getZoomBtn(),"Q_25_JVT");

		altCtrlDelBtn = new JMenuItem("Ctrl + Alt + Del");
		altCtrlDelBtn.setToolTipText(LocaleStrings.getString("Q_13_JVT")+" Ctrl+Alt+Del "+LocaleStrings.getString("Q_14_JVT"));
		altCtrlDelBtn.setVisible(true);
		altCtrlDelBtn.setRequestFocusEnabled(false);
		altCtrlDelBtn.addMouseListener(this);

		m_popupMenu = new JPopupMenu();
		m_popupMenu.add(altCtrlDelBtn);
		m_zoomPopupMenu = new JPopupMenu();
		m_zoomPopupMenu.add(constructZoomMenu());
		m_toolbar.add(getPlayPauseBtn());
		m_toolbar.addSeparator();
		m_toolbar.add(getFullScreenBtn());
		m_toolbar.addSeparator();
		m_toolbar.add(getCDBtn());
		m_toolbar.add(getHardddiskBtn());
		m_toolbar.addSeparator();
		m_toolbar.add(mouseBtn);
		m_toolbar.add(getKbdBtn());
		m_toolbar.addSeparator();
		m_toolbar.add(getVideoRecordBtn());
		m_toolbar.addSeparator();
		m_toolbar.add(getM_hotKeyBtn());
		m_toolbar.addSeparator();
		m_toolbar.add(getZoomBtn());
		m_toolbar.addSeparator();
		// m_toolbar.add(textlabel);
		m_toolbar.add(Box.createHorizontalGlue());
		m_toolbar.add(usersBtn);
		m_toolbar.addSeparator();
		m_toolbar.add(getHostDisplayBtn());
		m_toolbar.add(Box.createHorizontalStrut(1));
		m_toolbar.add(getPowerBtn());
		Border m_raised = new EtchedBorder(BevelBorder.LOWERED);  
		m_toolbar.setBorder(m_raised);
		m_toolbar.setFloatable(false);
		m_toolbar.repaint();


	}

	protected JSlider constructZoomMenu() {
		if(getSlider_zoom() == null)
		{
			setSlider_zoom(new JSlider(50,150));
			getSlider_zoom().setValue(100);
			getSlider_zoom().setMaximumSize(new Dimension(200, Short.MAX_VALUE));
			getSlider_zoom().setMinimumSize(new Dimension(200, Short.MAX_VALUE));

			getSlider_zoom().setBorder(BorderFactory.createEtchedBorder());
			getSlider_zoom().setPaintLabels(true);
			getSlider_zoom().setMajorTickSpacing(50);
			getSlider_zoom().setMaximum(150);
			getSlider_zoom().setMinimum(50);
			getSlider_zoom().setMinorTickSpacing(0);
			getSlider_zoom().setToolTipText("100%");

			getSlider_zoom().setFocusable(false);
			getSlider_zoom().addChangeListener(new MyChangeAction());
			getSlider_zoom().setRequestFocusEnabled(false);

			//solved changing Zoom Size will make mouse cursor inconsist
			getSlider_zoom().addMouseListener(this);
		}
		return getSlider_zoom();
	}

	public void setZoomLabel(int value){
		String str = Integer.toString(value);
		JViewerApp.getInstance().getM_wndFrame().getMenu().setZoomLabelText(str+"%");
		getSlider_zoom().setToolTipText(str+"%");
	}

	private  class MyChangeAction implements ChangeListener {
		public void stateChanged(ChangeEvent ce)
		{
			if(!JViewerApp.getInstance().getKVMClient().redirection())
				return;
			int value = getSlider_zoom().getValue();
			setZoomLabel(value);
			BigDecimal b = BigDecimal.valueOf((value),1);
			JVMenu.m_scale = b.doubleValue()/10;
			JViewerApp.getInstance().getRCView().setScaleFactor(JVMenu.m_scale, JVMenu.m_scale);

			if(value >= 50 && value <= 150)
			{
				JViewerApp.getInstance().getJVMenu().SetMenuEnable(JVMenu.ZOOM_IN, true);
				JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.ZOOM_IN).setEnabled(true);
				JViewerApp.getInstance().getJVMenu().SetMenuEnable(JVMenu.ZOOM_OUT, true);
				JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.ZOOM_OUT).setEnabled(true);

			}
			if(value >= 150){
				JViewerApp.getInstance().getJVMenu().SetMenuEnable(JVMenu.ZOOM_IN, false);
				JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.ZOOM_IN).setEnabled(false);
			}
			if(value <= 50){
				JViewerApp.getInstance().getJVMenu().SetMenuEnable(JVMenu.ZOOM_OUT, false);
				JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.ZOOM_OUT).setEnabled(false);
			}
			if(JViewerApp.getInstance().getRCView().GetUSBMouseMode() == USBMouseRep.RELATIVE_MOUSE_MODE){
				changeShowCursorOnZoom();
			}
			//No zoom options will be selected if zoom is not 100%
			if(JVMenu.m_scale != 1.0){
				JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.ZOOM_OPTION_NONE).setSelected(true);
				JViewerApp.getInstance().setZoomOption(JVMenu.ZOOM_OPTION_NONE);
			}
			//Actual size zoom option is selected when the zoom value is 100%
			else{
				JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.ACTUAL_SIZE).setSelected(true);
				JViewerApp.getInstance().setZoomOption(JVMenu.ACTUAL_SIZE);
			}
			JViewerApp.getInstance().getRCView().revalidate();
			JViewerApp.getInstance().getRCView().repaint();
		}
	}
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("static-access")
	public void mousePressed(MouseEvent e) {

		if(e.getSource() == mouseBtn)
		{
			if(!JViewerApp.getInstance().getKVMClient().redirection())
				return;
			if(JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.MOUSE_CLIENTCURSOR_CONTROL).isSelected())
			{
				if(JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.MOUSE_CLIENTCURSOR_CONTROL).isEnabled())
				{
					JViewerApp.getInstance().OnShowCursor(false);
				}
			}
			else
			{
				JViewerApp.getInstance().OnShowCursor(true);
			}
		}
		else if(e.getSource()== getKbdBtn())
		{
			if(!JViewerApp.getInstance().getKVMClient().redirection())
				return;

			JVMenu menu = JViewerApp.getInstance().getJVMenu();
			AutoKeyboardLayout autokeylayout;
			if(JViewerApp.getInstance().getAutokeylayout() != null )
				autokeylayout = JViewerApp.getInstance().getAutokeylayout();
			else
			{
				autokeylayout = new AutoKeyboardLayout();
				JViewerApp.getInstance().setAutokeylayout(autokeylayout);
			}

			if(menu.getMenuSelected(menu.AUTOMATIC_LANGUAGE) == false && menu.keyBoardLayout >= menu.LANGUAGE_ENGLISH_US)
			{
				JViewerApp.getInstance().OnSkbrdDisplay(menu.keyBoardLayout);
			}
			else 
			{   
				menu.notifyMenuStateSelected(JVMenu.AUTOMATIC_LANGUAGE, true);
				menu.getMenuItem(JVMenu.PKBRD_NONE).setSelected(true);
				if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_FRENCH)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_FRENCH);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_GERMAN)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_GERMAN_GER);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_ENGLISH_US)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_ENGLISH_US);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_JAPANESE)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_JAPANESE_Q);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_SPANISH)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_SPANISH);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_ENGLISH_UK)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_ENGLISH_UK);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_GERMAN_SWISS)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_GERMAN_SWISS);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_FRENCH_BELGIUM)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_FRENCH_BELGIUM);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_ITALIAN)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_ITALIAN);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_NORWEGIAN)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_NORWEGIAN_NOR);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_DANISH)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_DANISH);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_SWEDISH)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_SWEDISH);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_FINNISH)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_FINNISH);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_DUTCH_NL)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_DUTCH_NL);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_DUTCH_BE)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_DUTCH_BELGIUM);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_PORTUGUESE)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_PORTUGUESE);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_TURKISH_F)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_TURKISH_F);
				}
				else if(autokeylayout.getKeyboardType() == autokeylayout.KBD_TYPE_TURKISH_Q)
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_TURKISH_Q);
				}
				else
				{
					JViewerApp.getInstance().OnSkbrdDisplay(menu.LANGUAGE_ENGLISH_US);
				}
			}
		}
		else if(e.getSource()== altCtrlDelBtn)
		{
			if(!JViewerApp.getInstance().getKVMClient().redirection())
				return;
			if((JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_LEFT_CTRL_KEY) || JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_RIGHT_CTRL_KEY ))
					&& (JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_LEFT_ALT_KEY) || JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_RIGHT_ALT_KEY)))
				return ;
			else
			{
				JViewerApp.getInstance().OnKeyboardAltCtrlDel();
			}
		}
		else if(e.getSource()== getM_hotKeyBtn())
		{
			m_popupMenu.show(e.getComponent(), 0, e.getComponent().getHeight());  // this x and y valuse is hardcoded to display this popup menu as drop down menu
		}
		else if(e.getSource()== getPlayPauseBtn())
		{
			if(JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.VIDEO_RESUME_REDIRECTION).isEnabled())
			{
				JViewerApp.getInstance().OnVideoResumeRedirection();
			}else{
				if(JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.VIDEO_PAUSE_REDIRECTION).isEnabled())
				{
					JViewerApp.getInstance().setM_userPause(true);
					JViewerApp.getInstance().OnVideoPauseRedirection();
				}
			}
		}
		else if(e.getSource()== usersBtn)
		{
			userPopupMenu = new JPopupMenu();
			int numUsers = KVMClient.getNumUsers();
			String[] userData = KVMClient.getUserData();
			if(userData != null && numUsers != 0)
			{			
				ImageIcon menuIcon;
				for (int i = 0;i <  numUsers;i++)
				{
					menuIcon = null;
					String display = (userData[i].substring(userData[i].indexOf(":")+1, userData[i].length())).trim();
					String index = (userData[i].substring(0,userData[i].indexOf(":")-1)).trim();
					if(Integer.parseInt(index) == JViewerApp.getInstance().getCurrentSessionId()){
						URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/green.png");
						if(imageURL != null)
							menuIcon = new ImageIcon(imageURL);
					}
					userPopupMenu.add(JViewerApp.getInstance().getJVMenu().createCustomActiveuserMenuItem(Integer.parseInt(index),display,menuIcon));
				}
			}
			userPopupMenu.show(e.getComponent(), 0, e.getComponent().getHeight());  // this x and y valuse is hardcoded to display this popup menu as drop down menu			
		}	
		else if(e.getSource().equals(getPowerBtn()))
		{
			//jviewer session doesn't have privilege to execute power commands
			if(!JViewer.isPowerPrivEnabled())
			{
				InfoDialog.showDialog(JViewerApp.getInstance().getMainWindow(), LocaleStrings.getString("D_62_JVAPP"),
						LocaleStrings.getString("A_6_GLOBAL"),InfoDialog.INFORMATION_DIALOG);
				return ;
			}

			if(JViewerApp.getInstance().getM_frame().getMenu().getMenuEnable(JVMenu.POWER_ON_SERVER))
			{
				JViewerApp.getInstance().onSendPowerControlCommand(JVMenu.POWER_ON_SERVER);
			}
			else if(JViewerApp.getInstance().getM_frame().getMenu().getMenuEnable(JVMenu.POWER_OFF_IMMEDIATE))
			{
				JViewerApp.getInstance().onSendPowerControlCommand(JVMenu.POWER_OFF_IMMEDIATE);
			}
		}
		else if (e.getSource().equals(getHostDisplayBtn())) {
			if(JViewerApp.getInstance().getM_frame().getMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK).isEnabled())
				JViewerApp.getInstance().onSendHostLock(JViewerApp.HOST_DISPLAY_UNLOCK);
			else if(JViewerApp.getInstance().getM_frame().getMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_LOCK).isEnabled())
				JViewerApp.getInstance().onSendHostLock(JViewerApp.HOST_DISPLAY_LOCK);
		}
		else if(e.getSource().equals(getZoomBtn()))
		{
			m_zoomPopupMenu.show(e.getComponent(), 0, e.getComponent().getHeight()); 
		}
		return ;
	}

	public void mouseReleased(MouseEvent e) {
		/*		if(e.getSource()== m_hotKeyBtn)
			m_popupMenu.show(false);*/
		//solved changing Zoom Size will make mouse cursor inconsist
		if(e.getSource() == getSlider_zoom()) {			
			boolean cursurMenuState = JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.MOUSE_CLIENTCURSOR_CONTROL);
			if (getSlider_zoom().getValue() == 100 && cursurMenuState) {
				Mousecaliberation.resetCursor();
			}
		}
		else if(e.getSource()== getFullScreenBtn())
		{
			if(JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.VIDEO_FULL_SCREEN).isEnabled())
			{
				if(JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.VIDEO_FULL_SCREEN).isSelected())
				{
					JViewerApp.getInstance().OnVideoFullScreen(false);
					JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.VIDEO_FULL_SCREEN,false);
				}
				else
				{
					JViewerApp.getInstance().OnVideoFullScreen(true);
					JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.VIDEO_FULL_SCREEN,true);
				}
			}
			else
			{
				getFullScreenBtn().setToolTipText(LocaleStrings.getString("Q_16_JVT"));
			}
		}
		else if(e.getSource() == getCDBtn()){
			invokeVMDialog(VMApp.CD_MEDIA);
		}
		else if(e.getSource() == getHardddiskBtn()){
			invokeVMDialog(VMApp.HD_MEDIA);
		}

		else if(e.getSource() == getVideoRecordBtn()) {
			if (JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.VIDEO_RECORD_START).isEnabled()) {
				if (JViewerApp.getInstance().getM_videorecord() == null) {
					JViewerApp.getInstance().setM_videorecord(new VideoRecord());
					JViewerApp.getInstance().getM_videorecord().StoreLocation = JViewerApp.getInstance().VIDEO_RECORD_DEFAULT_PATH;
					VideoRecord.RecordStopTimer = JViewerApp.getInstance().VIDEO_RECORD_DEFAULT_TIME;
				}
				JViewerApp.getInstance().getM_videorecord().OnVideoRecordStart();
			} else if (JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.VIDEO_RECORD_STOP).isEnabled()) {
				JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.VIDEO_RECORD_STOP, false);
				JViewerApp.getInstance().getM_videorecord().OnVideoRecordStop();
			}
			//Don't show video record settings dialog in video recording is in progress. 
			if (!VideoRecord.Recording_Started || !VideoRecord.Record_Processing) {
				JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.VIDEO_RECORD_SETTINGS, false);
			}
		}
	}

	public void changeMacrowsStatusOnPauseResume(boolean status){
		// Remove the mouse listener from toolbar buttons while pausing 
		// and add the mouse listener while resuming.
		setButtonEnabled(mouseBtn, status);
		setButtonEnabled(getKbdBtn(), status);
		setButtonEnabled(getCDBtn(), status);
		setButtonEnabled(getHardddiskBtn(), status);
		setButtonEnabled(getM_hotKeyBtn(), status);
		setButtonEnabled(getVideoRecordBtn(), status);
		setButtonEnabled(getPowerBtn(), status);

		/*
		 * Following method call is executed only for a second client session with, partial kvm privilage.		 
		 */
		if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_PARTIAL )
			OnChangeToolbarIconState_KVMPartial();
		if(JViewerApp.getInstance().getZoomOption() == JVMenu.ACTUAL_SIZE)
			enableZoomSlider(status);
	}

	/**
	 * Enable or disable the Keyboard, Mouse, and Hotkey button on the toolbar,while partial access is given to the concurrent session in KVM sharing.
	 */
	public void OnChangeToolbarIconState_KVMPartial()
	{
		boolean powerBtnStatus = false;
		JVMenu menu = JViewerApp.getInstance().getM_wndFrame().getMenu();
		setButtonEnabled(getKbdBtn(), menu.getMenuItem(JVMenu.SKBRD_LANGUAGE_ENGLISH_US).isEnabled());
		setButtonEnabled(mouseBtn, menu.getMenuItem(JVMenu.MOUSE_CLIENTCURSOR_CONTROL).isEnabled());		
		setButtonEnabled(getM_hotKeyBtn(), menu.getMenuItem(JVMenu.KEYBOARD_CTRL_ALT_DEL).isEnabled());
		setButtonEnabled(getCDBtn(), menu.getMenuItem(JVMenu.DEVICE_MEDIA_DIALOG).isEnabled());
		setButtonEnabled(getHardddiskBtn(), menu.getMenuItem(JVMenu.DEVICE_MEDIA_DIALOG).isEnabled());
		// Enable power button mouse event on either power off or power on is enabled. if KVM_REQ_GIVEN is KVM_REQ_PARTIAL then disable power button.
		if(KVMSharing.KVM_REQ_GIVEN != KVMSharing.KVM_REQ_PARTIAL)
			powerBtnStatus = (menu.getMenuItem(JVMenu.POWER_OFF_IMMEDIATE).isEnabled() ||
					menu.getMenuItem(JVMenu.POWER_ON_SERVER).isEnabled());
		setButtonEnabled(getPowerBtn(), powerBtnStatus);
	}

	/**
	 * Enable/Disable the tool bar buttons when power status changes.
	 * @param status boolean (true/false).
	 */
	public void changeToolbarButtonStateOnPowerStatus(boolean status){
		JVMenu menu = JViewerApp.getInstance().getM_wndFrame().getMenu();
		setButtonEnabled(getPlayPauseBtn(), true);
		setButtonEnabled(getFullScreenBtn(),  status);
		setButtonEnabled(getKbdBtn(),  menu.getMenuItem(JVMenu.SKBRD_LANGUAGE_ENGLISH_US).isEnabled());
		setButtonEnabled(getCDBtn(),  menu.getMenuItem(JVMenu.DEVICE_MEDIA_DIALOG).isEnabled());
		setButtonEnabled(getHardddiskBtn(),  menu.getMenuItem(JVMenu.DEVICE_MEDIA_DIALOG).isEnabled());
		setButtonEnabled(mouseBtn,  menu.getMenuItem(JVMenu.MOUSE_CLIENTCURSOR_CONTROL).isEnabled());
		//No need to check the menu status for the here, applying teh value of status itself
		//is enough, because the up on teh button click event video record settings dialog 
		//will be shown only if the video record settings menu is enabled.
		setButtonEnabled(getM_hotKeyBtn(),  menu.getMenuItem(JVMenu.KEYBOARD_CTRL_ALT_DEL).isEnabled());
		if(JViewerApp.getInstance().getZoomOption() == JVMenu.ACTUAL_SIZE)
			enableZoomSlider(status);
	}
	/**
	 * Enable or disable a toolbar button and add or remove its MouseListener.  
	 * @param button - Toolbar button to be enabled or disabled.
	 * @param state - Enable(true) or disable(false).
	 */
	public void setButtonEnabled(JButton button, boolean state){
		MouseListener[] mListeners = button.getMouseListeners();
		if(!button.equals(getPowerBtn()))
			button.setEnabled(state);
		if(button.equals(getPlayPauseBtn()))
			return;
		if(state){
			for(MouseListener ml : mListeners){
				if(ml.equals(this))
					return;
			}
			button.addMouseListener(this);
		}
		else{
			for(MouseListener ml : mListeners){
				if(ml.equals(this))
					button.removeMouseListener(this);
			}
		}
	}

	public void turnOnPowerButton(boolean state){
		if(state){
			URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/poweron.png");
			getPowerBtn().setIcon(new ImageIcon(imageURL));
			getPowerBtn().setToolTipText(LocaleStrings.getString("Q_17_JVT"));
		}
		else{
			URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/poweroff.png");
			getPowerBtn().setIcon(new ImageIcon(imageURL));
			getPowerBtn().setToolTipText(LocaleStrings.getString("Q_18_JVT"));
		}
	}

	public void turnOnHostDisplayButton(boolean status){
		if(status){
			URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/monitorOn.png");
			getHostDisplayBtn().setIcon(new ImageIcon(imageURL));
			getHostDisplayBtn().setToolTipText(LocaleStrings.getString("Q_22_JVT"));
		}
		else{
			URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/monitorOff.png");
			getHostDisplayBtn().setIcon(new ImageIcon(imageURL));
			getHostDisplayBtn().setToolTipText(LocaleStrings.getString("Q_23_JVT"));
		}
	}
	public JToolBar getToolBar() {
		return m_toolbar;
	}

	public JButton getHardddiskBtn() {
		return hardddiskBtn;
	}

	public void setHardddiskBtn(JButton hardddiskBtn) {
		this.hardddiskBtn = hardddiskBtn;
	}
	/**
	 * @return the m_popupMenu
	 */
	public JPopupMenu getM_popupMenu() {
		return m_popupMenu;
	}

	public void changeMacrowsStatus(boolean status){
		mouseBtn.setEnabled(status);
		getCDBtn().setEnabled(status);
		getHardddiskBtn().setEnabled(status);
		getKbdBtn().setEnabled(status);
		getPlayPauseBtn().setEnabled(status);
		getFullScreenBtn().setEnabled(status);
		altCtrlDelBtn.setEnabled(status);
		//altTab.setEnabled(status);
		getVideoRecordBtn().setEnabled(status);
		getM_hotKeyBtn().setEnabled(status);
		getPowerBtn().setEnabled(status);
		getHostDisplayBtn().setEnabled(status);

		if(getSlider_zoom() != null)
			getSlider_zoom().setValue(100);

	}

	/**
	 * This will disable/enable all the toolbar Menu button depending upon status
	 * false - disable menu
	 * true - enable menu	
	**/
	public void changeMacroStatusOnReconnect(boolean status){
		// Remove the mouse listener from toolbar buttons while reconnecting 
		// and add the mouse listener after reconnecting successfully.
		setButtonEnabled(getPlayPauseBtn(),status );
		setButtonEnabled(mouseBtn, status);
		setButtonEnabled(getKbdBtn(), status);
		setButtonEnabled(getCDBtn(), status);
		setButtonEnabled(getHardddiskBtn(), status);
		setButtonEnabled(getM_hotKeyBtn(), status);
		setButtonEnabled(getVideoRecordBtn(), status);
		setButtonEnabled(getPowerBtn(), status);
		setButtonEnabled( getFullScreenBtn(), status);
		setButtonEnabled( getZoomBtn(), status);
	}

	/**
	 * Add the menu item for each user defined hot key macro defined, on to the 
	 * pop up menu on the hotkey button.
	 * @param menuString
	 */
	public void addHotkeyPoupMenuItem(String menuString){
		JMenuItem popupMenuItem = new JMenuItem(menuString);
		popupMenuItem.addActionListener(JViewerApp.getInstance().getJVMenu().m_menuListener);
		popupMenuItem.setActionCommand("HK_"+menuString);
		m_popupMenu.add(popupMenuItem,2);
	}

	/**
	 * Remove all the user defined hot key macro menuitems from teh popup menu on the
	 * hot key button, except the alt+ctrl+del menuitem.   
	 */
	public void removeHotkeyPoupMenuItem(){
		m_popupMenu.removeAll();
		m_popupMenu.add(altCtrlDelBtn);
	}

	/*
	 * Changes toolbar items text language
	 */
	public void changeToolBarItemLanguage() {
		Set st = m_menutoolbar_text.entrySet();
		Iterator itr = st.iterator();
		Object value = null ;
		JButton btn ;

		while (itr.hasNext())
		{
			try{
				Map.Entry me = (Map.Entry)itr.next();
				value = me.getValue();
				btn = (JButton) me.getKey();
				btn.setToolTipText(LocaleStrings.getString(value.toString()));
			}catch(Exception e){
				Debug.out.println(e);
			}
		}
	}

	/**
	 * Enable/Disable zoom slider
	 * @param status - true to enable zoom slider; false otherwise.
	 */
	public void enableZoomSlider(boolean status){
		boolean enableZoom = true;
		if(status && JViewerApp.getInstance().getRCView().GetUSBMouseMode() != USBMouseRep.OTHER_MOUSE_MODE){
			if(getSlider_zoom().getMouseListeners().equals(null))
				getSlider_zoom().addMouseListener(this);
			setZoomLabel(getSlider_zoom().getValue());
			zoomBtn.setToolTipText(LocaleStrings.getString("Q_25_JVT"));
			enableZoom = true;
		}
		else if(!status){
			resetZoom();
			getSlider_zoom().removeMouseListener(this);
			getSlider_zoom().setToolTipText(LocaleStrings.getString("Q_19_JVT"));
			zoomBtn.setToolTipText(LocaleStrings.getString("Q_19_JVT"));
			JViewerApp.getInstance().getM_wndFrame().getMenu().setZoomLabelText(LocaleStrings.getString("Q_20_JVT"));
			enableZoom = false;
		}
		setButtonEnabled(zoomBtn, enableZoom);
		getSlider_zoom().setEnabled(enableZoom);
		JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.ZOOM_IN, enableZoom);
		JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.ZOOM_OUT, enableZoom);
	}

	/**
	 * Reset the video zoom to 100%
	 */
	public void resetZoom(){
		getSlider_zoom().setValue(100);
		JViewerApp.getInstance().getRCView().revalidate();
		JViewerApp.getInstance().getRCView().repaint();
		JVMenu.m_scale = 1.0;
		JViewerApp.getInstance().getRCView().setScaleFactor(JVMenu.m_scale, JVMenu.m_scale);
	}

	/**
	 * Change the state of show cursor menu and toolbar button when zoom is not 100 %
	 */
	public void changeShowCursorOnZoom(){
		if(JVMenu.m_scale != 1.0){
			URL imageURLMouse = com.ami.kvm.jviewer.JViewer.class.getResource("res/Mouse2Btn-gray.png");
			JViewerApp.getInstance().getM_wndFrame().getToolbar().mouseBtn.setIcon(new ImageIcon(imageURLMouse));
			JViewerApp.getInstance().getM_wndFrame().getToolbar().mouseBtn.setToolTipText(LocaleStrings.getString("D_48_JVAPP"));
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.MOUSE_CLIENTCURSOR_CONTROL, false);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.MOUSE_CLIENTCURSOR_CONTROL, false);
			JViewerApp.getInstance().showCursor = false;
		}
		else if(JVMenu.m_scale == 1.0){
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.MOUSE_CLIENTCURSOR_CONTROL, true);
			JViewerApp.getInstance().getM_wndFrame().getToolbar().mouseBtn.setToolTipText(LocaleStrings.getString("D_15_JVAPP"));
		}
	}

	/**
	 * Remove VMedia buttons from toolbar.
	 */
	public void removeVMediaButtons(){
		m_toolbar.remove(getCDBtn());
		m_toolbar.remove(getHardddiskBtn());
	}

	/**
	 * Removes the host dispalay status button from toolbar.
	 */
	public void removeHostDisplayStatusButtons(){
		m_toolbar.remove(getHostDisplayBtn());
	}

	private void invokeVMDialog(int selectedTab){
		Debug.out.println("MEDIA LICENSE STATUS : "+JViewer.getMediaLicenseStatus());
		if(JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.DEVICE_MEDIA_DIALOG).isEnabled())
		{
			if(JViewer.getMediaLicenseStatus() == JViewer.LICENSED)
				JViewerApp.getInstance().OnvMedia(selectedTab);
			else{
				InfoDialog.showDialog(JViewer.getMainFrame(), LocaleStrings.getString("F_136_JVM"),
						LocaleStrings.getString("2_4_KVMCLIENT"), InfoDialog.INFORMATION_DIALOG);
			}
		}
	}

	/**
	 * @return the cdBtn
	 */
	public JButton getCDBtn() {
		return cdBtn;
	}

	/**
	 * @param cdBtn the cdBtn to set
	 */
	public void setCDBtn(JButton cdBtn) {
		this.cdBtn = cdBtn;
	}

	/**
	 * @return the kbdBtn
	 */
	public JButton getKbdBtn() {
		return kbdBtn;
	}

	/**
	 * @param kbdBtn the kbdBtn to set
	 */
	public void setKbdBtn(JButton kbdBtn) {
		this.kbdBtn = kbdBtn;
	}

	/**
	 * @return the playPauseBtn
	 */
	public JButton getPlayPauseBtn() {
		return playPauseBtn;
	}

	/**
	 * @param playPauseBtn the playPauseBtn to set
	 */
	public void setPlayPauseBtn(JButton playPauseBtn) {
		this.playPauseBtn = playPauseBtn;
	}

	/**
	 * @return the fullScreenBtn
	 */
	public JButton getFullScreenBtn() {
		return fullScreenBtn;
	}

	/**
	 * @param fullScreenBtn the fullScreenBtn to set
	 */
	public void setFullScreenBtn(JButton fullScreenBtn) {
		this.fullScreenBtn = fullScreenBtn;
	}

	/**
	 * @return the videoRecordBtn
	 */
	public JButton getVideoRecordBtn() {
		return videoRecordBtn;
	}

	/**
	 * @param videoRecordBtn the videoRecordBtn to set
	 */
	public void setVideoRecordBtn(JButton videoRecordBtn) {
		this.videoRecordBtn = videoRecordBtn;
	}

	/**
	 * @return the powerBtn
	 */
	public JButton getPowerBtn() {
		return powerBtn;
	}

	/**
	 * @param powerBtn the powerBtn to set
	 */
	public void setPowerBtn(JButton powerBtn) {
		this.powerBtn = powerBtn;
	}

	/**
	 * @return the hostDisplayBtn
	 */
	public JButton getHostDisplayBtn() {
		return hostDisplayBtn;
	}

	/**
	 * @param hostDisplayBtn the hostDisplayBtn to set
	 */
	public void setHostDisplayBtn(JButton hostDisplayBtn) {
		this.hostDisplayBtn = hostDisplayBtn;
	}

	/**
	 * @return the slider_zoom
	 */
	public JSlider getSlider_zoom() {
		return slider_zoom;
	}

	/**
	 * @param slider_zoom the slider_zoom to set
	 */
	public void setSlider_zoom(JSlider slider_zoom) {
		this.slider_zoom = slider_zoom;
	}

	/**
	 * @return the m_hotKeyBtn
	 */
	public JButton getM_hotKeyBtn() {
		return m_hotKeyBtn;
	}

	/**
	 * @param m_hotKeyBtn the m_hotKeyBtn to set
	 */
	public void setM_hotKeyBtn(JButton m_hotKeyBtn) {
		this.m_hotKeyBtn = m_hotKeyBtn;
	}

	/*
	 * @param state to update play/pause button
	 */
	public void updatePlayPauseButton(boolean state){

		if(state == true){
			URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/play.png");
			getPlayPauseBtn().setIcon(new ImageIcon(imageURL));
			getPlayPauseBtn().setToolTipText(LocaleStrings.getString("Q_9_JVT"));
		}
		else{
			URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/pause.png");
			getPlayPauseBtn().setIcon(new ImageIcon(imageURL));
			getPlayPauseBtn().setToolTipText(LocaleStrings.getString("Q_8_JVT"));
		}
		getPlayPauseBtn().setEnabled(true);
	}

	public JButton getZoomBtn() {
		return zoomBtn;
	}

	public void setZoomBtn(JButton zoomBtn) {
		this.zoomBtn = zoomBtn;
	}

	public JPopupMenu getM_zoomPopupMenu() {
		return m_zoomPopupMenu;
	}

	public void setM_zoomPopupMenu(JPopupMenu m_zoomPopupMenu) {
		this.m_zoomPopupMenu = m_zoomPopupMenu;
	}
}

