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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;


/**
 * Full screen menu bar class.
 */
public class FSMenuBar extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final String PIN = "Pin";
	private static final String UNPIN = "Unpin";
	private static final String MENU = LocaleStrings.getString("B_2_FMB");
	private static final String LED_STATUS = "LED Status";
	private static final String MINIMIZE = LocaleStrings.getString("B_4_FMB");
	private static final String TO_FRAME = LocaleStrings.getString("B_5_FMB");
	private static final String CLOSE = LocaleStrings.getString("B_6_FMB");

	private static final int MENUBAR_HEIGHT = 20;
	private FSFrame m_frame;
	private FSMenu m_fsMenu;
	private JPopupMenu m_popupMenu;
	private MouseEnterExit m_msEE;
	private ButtonListener m_btnListener;
	private int m_slide = 0;
	private int slideStatusBar = 0;
	private int m_fsMenuX;// Fullscreen menubar X- position.
	private int m_fsMenuY;//Fullscreen menubar X- position.
	private int m_fsMenuWidth;// Fullscreen menubar width.
	private Timer m_timer;
	private Timer ledStatusTimer;
	private boolean m_show;
	private boolean showLEDStatusBar = false;
	private JComponent m_contentPane;
	private JLabel m_idLbl;
	private JButton m_menuBtn;
	private JPanel m_autoPanel;
	private JButton m_autoPin;
	private JButton m_autoUnpin;
	private JButton showLEDStatus;
	private FSLEDStatusBar ledStatusBar;
	private boolean m_auto;
	private boolean fsToolbarEnabled = false;

	public FSMenuBar()
	{
		/*Dummy constructor used by JVFrame to call
		 * cancelTimer method which stops the timer
		 * thread execution
		 */
	}

	/**
	 * The constructor.
	 */
	public FSMenuBar(FSFrame frame)
	{
		super(JViewer.getMainFrame());
		ledStatusBar = new FSLEDStatusBar(this);
		Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		m_fsMenuWidth = 400;
		m_fsMenuX = (sd.width - m_fsMenuWidth)/2;
		setBounds(m_fsMenuX, 0, m_fsMenuWidth, m_slide);
		setVisible(false);
		setUndecorated(true);
		setResizable(false);
		setFocusable(false);
		setFocusableWindowState(false);
		m_msEE = new MouseEnterExit();
		addMouseListener(m_msEE);
		addMouseListener( new WindowMouseEvents() );
		m_btnListener = new ButtonListener();
		m_contentPane = (JComponent)getContentPane();
		m_contentPane.setBackground(new Color(250, 250, 250));
		m_contentPane.setLayout(new BoxLayout(m_contentPane, BoxLayout.X_AXIS));
		m_contentPane.add(Box.createRigidArea(new Dimension(5, 0)));
		m_autoPin = createButton("res/pin.jpg",	 LocaleStrings.getString("B_1_FMB"), PIN);
		m_autoUnpin = createButton("res/unpin.jpg", LocaleStrings.getString("B_1_FMB"), UNPIN);
		m_autoPanel = new JPanel();
		m_autoPanel.setLayout(new BoxLayout(m_autoPanel, BoxLayout.X_AXIS));
		m_autoPanel.setPreferredSize(new Dimension(15, 15));
		m_autoPanel.setMaximumSize(new Dimension(15, 15));
		m_autoPanel.add(m_autoPin);
		m_auto = false;
		m_contentPane.add(m_autoPanel);

		m_contentPane.add(Box.createRigidArea(new Dimension(5, 0)));
		m_menuBtn = createButton("res/menu.jpg", MENU, MENU);
		m_fsMenu = new FSMenu();
		m_popupMenu = m_fsMenu.getPopupMenu();
		m_popupMenu.addPopupMenuListener(new PopupMenuEventListener());
		m_menuBtn.addMouseListener(new PopupListener());
		m_contentPane.add(m_menuBtn);

		m_contentPane.add(Box.createRigidArea(new Dimension(5, 0)));
		showLEDStatus = createButton("res/lock.png", LocaleStrings.getString("B_7_FMB"), LED_STATUS);
		m_contentPane.add(showLEDStatus);
		m_contentPane.add(Box.createRigidArea(new Dimension(5, 0)));

		m_contentPane.add(createSeparator());

		m_contentPane.add(Box.createRigidArea(new Dimension(5, 0)));
		m_idLbl = new JLabel(LocaleStrings.getString("B_3_FMB"));
		m_idLbl.setForeground(new Color(0, 100, 100));
		m_idLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		m_idLbl.setHorizontalAlignment(SwingConstants.CENTER);
		m_idLbl.setPreferredSize(new Dimension(287, 15));
		m_idLbl.setMinimumSize(new Dimension(287, 15));
		m_idLbl.setMaximumSize(new Dimension(287, 15));		
		m_contentPane.add(m_idLbl);

		m_contentPane.add(Box.createRigidArea(new Dimension(5, 0)));		
		m_contentPane.add(createButton("res/min.jpg", MINIMIZE, MINIMIZE));
		m_contentPane.add(Box.createRigidArea(new Dimension(1, 0)));
		m_contentPane.add(createButton("res/max.jpg", TO_FRAME, TO_FRAME));
		m_contentPane.add(Box.createRigidArea(new Dimension(1, 0)));
		m_contentPane.add(createButton("res/close.jpg", CLOSE, CLOSE));
	}

	/**
	 * Get full screen menu
	 *
	 * @return full screen menu
	 */
	public JVMenu getFSMenu()
	{
		return m_fsMenu;
	}

	/**
	 * @return the ledStatusBar
	 */
	public FSLEDStatusBar getLedStatusBar() {
		return ledStatusBar;
	}

	/**
	 * Slide down menu bar to show.
	 */
	public void showMenu()
	{

		m_show = true;
		if(m_timer != null)
			m_timer.cancel();
		m_timer = new Timer();
		m_timer.schedule(new SlideTask(), 0, 20);
		if(fsToolbarEnabled)
			showLEDStatusBar(true);
	}

	/**
	 * Slide up menu bar to hide.
	 */
	public void hideMenu()
	{

		m_show = false;
		if(m_timer != null)
			m_timer.cancel();
		m_timer = new Timer();
		m_timer.schedule(new SlideTask(), 0, 20);
		if(fsToolbarEnabled)
			showLEDStatusBar(false);
	}

	/**
	 * Set server identification label on menu bar.
	 */
	public void setIDLabel(String idLbl)
	{
		/*
		 * 	To adjust the full screen menubar label size, 
		 * to show longer texts when redirection is paused or stopped
		 */
		int width;
		int height = 15;
		int startpoint = JViewer.getMainFrame().getLocationOnScreen().x; //Get JViewer window  x position
		Dimension sd = JViewerApp.getInstance().getCurrentMonitorResolution();
		
		if(System.getProperty("os.name").startsWith("Mac")){
			width = 199;
			m_fsMenuWidth = 332;
			m_fsMenuX = startpoint + (sd.width- m_fsMenuWidth)/2;
			m_fsMenuY = 20;
		}
		else{
			width = 187;
			m_fsMenuWidth = 320;
			m_fsMenuX = startpoint + (sd.width/2 - (m_fsMenuWidth/2));
			m_fsMenuY = 0;
		}

		m_idLbl.setPreferredSize(new Dimension(width, height));
		m_idLbl.setMinimumSize(new Dimension(width, height));
		m_idLbl.setMaximumSize(new Dimension(width, height));
		m_idLbl.setText(idLbl); 
		setBounds(m_fsMenuX, m_fsMenuY, m_fsMenuWidth, m_slide);
	}
	/**
	 * Method used to cancel timer thread.
	 */
	public void cancelTimer()
	{
		if(m_timer != null)
		{
			m_timer.cancel();
			m_timer = null;
		}
		if(ledStatusTimer != null)
		{
			ledStatusTimer.cancel();
			ledStatusTimer = null;
		}
	}

	/**
	 * Shows the LED status bar
	 * @param show - if true shows the status bar, else hides the status bar.
	 */
	private void showLEDStatusBar( boolean show){
		showLEDStatusBar = show;
		if(ledStatusTimer != null)
			ledStatusTimer.cancel();

		ledStatusTimer = new Timer();
		ledStatusTimer.schedule(new StatusBarSlideTask(), 0, 20);
	}

	/*
	 * Slide effect.
	 */
	private void slide()
	{
		if(System.getProperty("os.name").startsWith("Mac"))
			setBounds(m_fsMenuX, 20, m_fsMenuWidth, m_slide);
		else
			setBounds(m_fsMenuX, 0, m_fsMenuWidth, m_slide);
		if(showLEDStatusBar)
			slideStatusBar();
		setVisible(true);
	}

	/*
	 * Slide effect.
	 */
	private void slideStatusBar()
	{
		if(slideStatusBar < FSLEDStatusBar.HEIGHT){
			int ledStatusX = m_fsMenuX+((m_fsMenuWidth - FSLEDStatusBar.WIDTH)/2);
			if(System.getProperty("os.name").startsWith("Mac"))
				ledStatusBar.setBounds(ledStatusX, m_slide+20, FSLEDStatusBar.WIDTH, slideStatusBar);
			else
				ledStatusBar.setBounds(ledStatusX, m_slide, FSLEDStatusBar.WIDTH, slideStatusBar);
			ledStatusBar.setVisible(true);
		}
	}

	/*
	 * Create menu bar button.
	 */
	private JButton createButton(String image, String tip, String actionCmd)
	{
		URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource(image);
		JButton btn = new JButton();
		if (imageURL != null) {
			btn.setIcon(new ImageIcon(imageURL));
		}

		btn.setToolTipText(tip);
		btn.setPreferredSize(new Dimension(15, 15));
		btn.setMinimumSize(new Dimension(15, 15));
		btn.setMaximumSize(new Dimension(15, 15));
		btn.addMouseListener(m_msEE);
		btn.setActionCommand(actionCmd);
		btn.addActionListener(m_btnListener);
		return btn;
	}
	/**
	 * Create separator on the fsMenubar
	 **/
	private JLabel createSeparator(){

		JLabel m_separatorLbl = new JLabel("|");
		m_separatorLbl.setForeground(new Color(200, 200, 200));
		m_separatorLbl.setFont(new Font("Arial", Font.PLAIN, 18));
		return m_separatorLbl;
	}

	/**
	 * Slide task to provide sliding effect for menu bar.
	 */
	class SlideTask extends TimerTask
	{
		/**
		 * Task routine.
		 */
		public void run() {

			if (m_show) {
				if (m_slide < MENUBAR_HEIGHT) {
					m_slide++;
					slide();
				}
				else {
					m_timer.cancel();
					if(!m_auto)
						JViewerApp.getInstance().getRCView().requestFocus();
				}
			}
			else {
				if (m_slide > 1) {
						m_slide--;
						slide();
				}
				else {
					m_timer.cancel();
					
					if(m_auto){
						JViewerApp.getInstance().getRCView().requestFocus();
					}
				}
			}
		}
	}

	/**
	 * Slide task to provide sliding effect for LED status bar.
	 */
	class StatusBarSlideTask extends TimerTask
	{
		/**
		 * Task routine.
		 */
		public void run() {

			if (showLEDStatusBar) {
				if (slideStatusBar <= FSLEDStatusBar.HEIGHT) {
						slideStatusBar++;
						slideStatusBar();
				}
				else {
					if(ledStatusTimer != null)
						ledStatusTimer.cancel();
					if(!m_auto)
						JViewerApp.getInstance().getRCView().requestFocus();
				}
			}
			else {
				if (slideStatusBar >= 1) {
					slideStatusBar--;
					slideStatusBar();
				}
				else {
					if(ledStatusTimer != null)
						ledStatusTimer.cancel();
					if(m_auto)
						JViewerApp.getInstance().getRCView().requestFocus();
				}
			}
		}
	}

	/**
	 *
	 *	Mouse adapter for the Fullscreen menubar
	 */
	class WindowMouseEvents extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			JViewerApp.getInstance().getRCView().requestFocus();
		}

	}
	/**
	 * Mouse enter and exit lister.
	 */
	class MouseEnterExit extends MouseAdapter
	{
		/**
		 * Mouse enter event handler.
		 */
		public void mouseEntered(MouseEvent e)
		{
			if (m_auto) {
				m_timer.cancel();
				showMenu();
			}
		}

		/**
		 * Mouse exit event handler.
		 */
		public void mouseExited(MouseEvent e)
		{
			if (m_auto) {
				m_timer.cancel();
				hideMenu();
			}
		}
	}

	/**
	 * Popup menu button event class.
	 */
	class PopupListener extends MouseAdapter
	{
		/**
		 * Mouse press event handler.
		 */
		public void mousePressed(MouseEvent e) {

			Debug.out.println("Popup event");
			m_popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * Popup menu listener class.
	 */
	class PopupMenuEventListener implements PopupMenuListener {

		boolean m_autoCanceled = false;

		/**
		 * Popup cancel event handler.
		 */
		public void popupMenuCanceled(PopupMenuEvent e) {

			Debug.out.println("Popup canceled");
			if (m_autoCanceled) {
				m_auto = true;
				m_autoCanceled = false;
				//hideMenu();
			}
		}

		/**
		 * Popup visible event handler.
		 */
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

			Debug.out.println("Popup visible");
			if (m_auto) {
				m_auto = false;
				m_autoCanceled = true;
			}
		}

		/**
		 * Popup invisible event handler.
		 */
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

			Debug.out.println("Popup invisible");
			if (m_autoCanceled) {
				m_auto = true;
				m_autoCanceled = false;
				hideMenu();
			}
			JViewerApp.getInstance().getRCView().requestFocus();
		}
	}

	/**
	 * Menu bar buttons action implementation.
	 */
	class ButtonListener implements ActionListener {

		/**
		 * Button action handler.
		 */
		public void actionPerformed(ActionEvent e) {

			JViewerApp RCApp = JViewerApp.getInstance();
			String cmd = e.getActionCommand();

			if (cmd.equals(UNPIN)) {
				Debug.out.println("Disable Auto hide");
				m_autoPanel.removeAll();
				m_autoPanel.add(m_autoPin);
				m_autoPanel.repaint();
				m_auto = false;
				ledStatusBar.setAutoHide(false);
				JViewerApp.getInstance().getRCView().requestFocus();
			}
			else if (cmd.equals(PIN)) {
				Debug.out.println("Enable Auto hide");
				m_autoPanel.removeAll();
				m_autoPanel.add(m_autoUnpin);
				m_auto = true;
				ledStatusBar.setAutoHide(true);
				JViewerApp.getInstance().getRCView().requestFocus();
			}
			else if (cmd.equals(TO_FRAME)) {
				Debug.out.println("Toggle to frame window");
				m_auto = false;
				RCApp.OnVideoFullScreen(false);
			}
			else if (cmd.equals(MINIMIZE)) {
				Debug.out.println("Minimize");
				//Modified for JInternalFrame
				if(JViewer.isStandalone())
					JViewer.getMainFrame().setState(Frame.ICONIFIED);
			}
			else if (cmd.equals(CLOSE)) {
				Debug.out.println("Close");
				RCApp.OnVideoExit();
			}
			else if (cmd.equals(MENU)) {
				Debug.out.println("Menu");
			}
			else if(cmd.equals(LED_STATUS)){
				JButton button = (JButton) e.getSource();
				if(showLEDStatusBar){
					button.setToolTipText(LocaleStrings.getString("B_7_FMB"));
					showLEDStatusBar(false);
				}
				else{
					button.setToolTipText(LocaleStrings.getString("B_8_FMB"));
					showLEDStatusBar(true);
				}
				if(fsToolbarEnabled)
					fsToolbarEnabled = false;
				else
					fsToolbarEnabled = true;
			}
			else {
				JViewerApp.getInstance().getRCView().requestFocus();
			}
		}
	}
}
