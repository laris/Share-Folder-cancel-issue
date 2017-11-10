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
// JViewer view module.
// This module is responsible for displaying screens and capturing user actions
// including keyboard and mouse events.
//

package com.ami.kvm.jviewer.gui;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.hid.KeyProcessor;
import com.ami.kvm.jviewer.hid.USBKeyProcessorEnglish;
import com.ami.kvm.jviewer.hid.USBKeyboardRep;
import com.ami.kvm.jviewer.hid.USBMouseRep;
import com.ami.kvm.jviewer.kvmpkts.KVMClient;
import com.ami.kvm.jviewer.kvmpkts.Mousecaliberation;



/**
 * JViewer view class.
 */
public class JViewerView extends JComponent {
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_VIEW_WIDTH = 1600;//1024;
	public static final int DEFAULT_VIEW_HEIGHT = 1200;//768;
	public static float MOUSE_ACCELERATION=2.0F;	// x times
	public static int ACCEL_THRESHOLD=4; 	// x pixels

	public static boolean firsttime = true;
	public static float RemainingX = 0.0F;
	public static float RemainingY = 0.0F;
	public static boolean Lost_focus_flag= false;
	public static int Mouse_X =0;
	public static int Mouse_Y =0;
	private int m_act_width;	// Actual resolution width at host
	private int m_act_height;	// Actual resolution height at host
	private short m_cur_width;	// Width for Cursor reference
	private short m_cur_height;	// Height for Cursor reference
	private int m_width; // component width
	private int m_height; // component height
	public RCMouseListener m_mouseListener; // mouse event handler
	public RCKeyListener m_keyListener; // keyboard event handler
	public RCFocusListener m_focuslistener;
	public RCMouseWheelListener	m_mousewheelListener;	//Mouse wheel handler
	public static  boolean syncLEDFlag = false;
	private USBKeyboardRep m_USBKeyRep;
	private USBMouseRep m_USBMouseRep;
	byte m_USBMouseMode = USBMouseRep.INVALID_MOUSE_MODE;
	private boolean initMouseMode = true;

	public static final int NUMLOCK		 = 0x01;
	public static final int CAPSLOCK	 = 0x02;
	public static final int SCROLLLOCK 	 = 0x04;
	/* required for relative usb mouse redirection */
    private int lastX = -1;
    private int lastY = -1;
    public Robot robbie;
	private int curX = 0;
	private int curY = 0;
	private boolean m_bLeftCtrlDown = false;
	private boolean m_bRightCtrlDown = false;
	private boolean m_bMouseRedirRunning = false;
	private short keyPressedFlag = 0;
	private BufferedImage localImage = new BufferedImage( DEFAULT_VIEW_WIDTH,DEFAULT_VIEW_HEIGHT,BufferedImage.TYPE_INT_RGB);
	private BufferedImage invisCursorImg = null;
	private Cursor invisibleCursor = null;
	public MouseEvent mouse_event;
    public MouseEvent sentmouse_event;
    public Thread mouse_thread;
    private Timer m_mouseTmr;
    
    private byte[] bdata;
    private int keyCode;
    private int keyLocation;
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private boolean mouseCursorOut = false;
    private boolean isCtrlPressed = false;
    private int ctrlKeyLocation;
    
	/**
	 * The constructor.
	 *
	 * @param width
	 *            view width.
	 * @param height
	 *            view height.
	 */
	public JViewerView(int width, int height) {
		if(JViewer.isjviewerapp() || JViewer.isStandAloneApp()){
			/* USB Keyboard report  */
			m_USBKeyRep = new USBKeyboardRep();
			m_USBMouseRep = new USBMouseRep();
			// hook listeners
			m_mouseListener = new RCMouseListener();
			m_keyListener = new RCKeyListener();
			m_focuslistener = new RCFocusListener();
			m_mousewheelListener = new RCMouseWheelListener();
			// set view focusable to capture keyboard events
			setFocusable(true);
			setFocusTraversalKeysEnabled(false);
			addFocusListener(new RCFocusListener());
			addMouseWheelListener(new RCMouseWheelListener());
			m_mouseTmr = new Timer();
			m_mouseTmr.schedule(new MousesendTask(), 0, 60);
		}
	}

	/**
	 * Add mouse and keyboard listeners
	 */
	public void addKMListeners() {
		//Do not add KeyListener or MouseListener until HID initialization is completed.
		if(JViewerApp.getInstance().getHidInitDialog() != null)
			return;
		addKeyListener();
		addMouseListeners();
	}

	/**
	 * Add mouse and keyboard listeners
	 */
	public void addKeyListener() {
		//Do not add KeyListener until HID initialization is completed.
		if(JViewerApp.getInstance().getHidInitDialog() != null)
			return;
		if(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG)
			return;
		KeyListener[] keyListeners = getKeyListeners();
		//add KeyListener if no KeyListener is already added to JViewrView
		if(keyListeners.length == 0){
			setkeyprocessor();
			addKeyListener(m_keyListener);
		}
	}


	public void setkeyprocessor(){
		KeyProcessor m_keyprocesor = JViewerApp.getInstance().getKeyProcesssor();
		JViewerApp.getInstance().getKeyProcesssor().setAutoKeybreakMode(true);
		m_USBKeyRep.setM_USBKeyProcessor(m_keyprocesor);
	}

	/**
	 * Add mouse and keyboard listeners
	 */
	public void removeKeyListener() {
		if(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG)
			return;
		removeKeyListener(m_keyListener);
		//Fix for F10 key press invoking menu item after keylistener removed
		JViewerApp.getInstance().getM_wndFrame().getWindowMenu().getMenuBar().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10,0), "none");
	}


	// usb is in inconsistant state and mouse moves are getting associated with
	// some key. press ctrl key to release any abnormal key state.
	public void flushHID() {
		KeyEvent evt = new KeyEvent(this, Event.KEY_PRESS, (new Date()).getTime(), 0x82, 17, '0');
		m_keyListener.keyPressed(evt);
		evt = new KeyEvent(this, Event.KEY_PRESS, (new Date()).getTime(), 0, 17, '0');
		m_keyListener.keyReleased(evt);
	}

	/**
	 * Remove Key & Mouse Listeners (in Pause video)
	 */
	public void removeKMListener() {
		removeKeyListener(m_keyListener);
		removeMouseListeners();
	}

	/**
	 * Adds the MouseListener and MouseMotionListener to JViewerView
	 */
	public void addMouseListeners(){
		//Do not add MouseListener until HID initialization is completed.
		if(JViewerApp.getInstance().getHidInitDialog() != null)
			return;
		if(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG)
			return;
		MouseListener[] mouseListeners = getMouseListeners();
		MouseMotionListener[] mouseMotionListeners = getMouseMotionListeners();
		//add MouseListener if no MouseListener is already added to JViewrView
		if(mouseListeners.length == 0)
			addMouseListener(m_mouseListener);
		//add MouseMotionListener if no MouseMotionListener is already added to JViewrView
		if(mouseMotionListeners.length == 0)
			addMouseMotionListener(m_mouseListener);
	}
	
	/**
	 * Removes the MouseListener and MouseMotionListener from JViewerView
	 */
	public void removeMouseListeners(){
		if(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG)
			return;
		removeMouseListener(m_mouseListener);
		removeMouseMotionListener(m_mouseListener);
	}

	/**
	 * Width of current view.
	 *
	 * @return the view width.
	 */
	public int viewWidth() {
		return m_width;
	}

	/**
	 * Height of current view.
	 *
	 * @return the view height.
	 */
	public int viewHeight() {
		return m_height;
	}

	/**
	 * Set the view size. Recreate the view image buffer with new size.
	 *
	 * @param width
	 *            view width;
	 * @param height
	 *            view height;
	 * @param type
	 *            image type
	 */
	public void setRCView(int width, int height, int type) {

		//m_act_width = m_width = width;
		m_width = width;
		m_act_width = JViewerApp.getInstance().getSocframeHdr().getwidth();
		m_act_height = m_height = height;
		m_cur_width = JViewerApp.getInstance().getSocframeHdr().getcurwidth();
		m_cur_height = JViewerApp.getInstance().getSocframeHdr().getcurheight();
		Debug.out.println("View - width: " + m_width + " height: " + m_height);
		// set view size
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		// construct buffered image
		JViewerApp.getInstance().getPrepare_buf().prepareBufImage(width,height,type);
	}

	/**
	 * @return the m_cur_width
	 */
	public short getM_cur_width() {
		return m_cur_width;
	}

	/**
	 * @param m_cur_width the m_cur_width to set
	 */
	public void setM_cur_width(int m_cur_width) {
		this.m_cur_width = (short) m_cur_width;
	}

	/**
	 * @return the m_cur_height
	 */
	public short getM_cur_height() {
		return m_cur_height;
	}

	/**
	 * @param m_cur_height the m_cur_height to set
	 */
	public void setM_cur_height(int m_cur_height) {
		this.m_cur_height = (short) m_cur_height;
	}

	/**
	 * Component update override. This override is to optimize painting. update
	 * is allowed to do only painting.
	 *
	 * @param g
	 * Graphics context
	 */
	public void update(Graphics g) {
		paint(g);
	}

	public Dimension getPreferredSize()
	{
		BufferedImage m_image = JViewerApp.getInstance().getPrepare_buf().getM_image();
		if(m_image != null)
		{
			return new Dimension((int)(m_image.getWidth(this) +
					(m_image.getWidth(this) * (scaleX - 1))),
					(int)(m_image.getHeight(this) +
							(m_image.getHeight(this) * (scaleY -1 ))));
		}
		else
			return new Dimension(1024,768);
	}
	/**
	 * Component paint override
	 *
	 * @param g
	 *            Graphics context
	 */
	public void paint(Graphics g) {

		try {
			BufferedImage m_image = JViewerApp.getInstance().getPrepare_buf().getM_image();
			if(m_image == null)
				return;
			localImage = m_image;
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;

			if(m_image.getType() == BufferedImage.TYPE_USHORT_565_RGB){
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			}else{
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			}
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			AffineTransform tx = null;
			//clientScreenSize will contain the client screen resolution as a Dimension object.
			Dimension clientScreenSize = JViewerApp.getInstance().getCurrentMonitorResolution();
			//clientWindowSize will contain the scroll pane size as a Dimension object.
			Dimension clientWindowSize =JViewerApp.getInstance().getMainWindow().m_viewSP.getSize();
			if(m_USBMouseMode == USBMouseRep.OTHER_MOUSE_MODE ||
					JViewerApp.getInstance().getZoomOption() == JVMenu.FIT_TO_CLIENT_RES){
				scaleX = 1.0;
				scaleY = 1.0;
				//Scale down the image, if Image size is greater than the size of the scrollPane or the client
				//screen resolution.
				if(m_image.getWidth()>=clientWindowSize.width || m_image.getWidth()>=clientScreenSize.width)
					scaleX = (double)(clientWindowSize.width)/m_image.getWidth();
				if(m_image.getHeight()>=clientWindowSize.height || m_image.getHeight()>=clientScreenSize.height)
					scaleY = (double)(clientWindowSize.height)/m_image.getHeight();
			}
			tx = AffineTransform.getScaleInstance(scaleX, scaleY);
			if(JViewer.isdownloadapp()){
				return;
			}
			g2.drawImage(m_image,tx,this);

			if(m_USBMouseMode == USBMouseRep.RELATIVE_MOUSE_MODE) {
				if(JViewerApp.getInstance().showCursor ) {
					if(JViewerView.Mouse_X <= m_width && JViewerView.Mouse_Y <= m_height) {
						URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/cursor.gif");
						BufferedImage img = null;
						try {
							img = ImageIO.read(imageURL);
						} catch (IOException e) {
							Debug.out.println(e);
						}
						g.drawImage(img,JViewerView.Mouse_X,JViewerView.Mouse_Y, null);
					}
				}
			}
		} catch (Exception e) {
			Debug.out.println(e);
		}
	}

	/**
	 * Show/Hide Mouse Cursor
	 *
	 * @param show -
	 *            true to Show the mouse cursor and false to Hide the mouse
	 *            cursor
	 */
	public void ShowCursor(boolean show)
	{
			if(m_USBMouseMode == USBMouseRep.RELATIVE_MOUSE_MODE)
			{
				Mouse_X=0;
				Mouse_Y=0;
				if(robbie == null){
					try {
						robbie = new Robot();
					}
					catch( AWTException e )
					{
						//JOptionPane.showMessageDialog(parent, "Cannot create robot for mouse repositioning, aborting calibration!");
						robbie = null;
						return;
					}
				}
				Mousecaliberation.resetCursor();
				if(invisCursorImg == null){
					invisCursorImg = createInvisibleCursor();
				}
				if(invisibleCursor == null)
					invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor( invisCursorImg,
							new Point( 0, 0 ), "Invisible Cursor" );

				setCursor( invisibleCursor );
				return;
			}
			else if(m_USBMouseMode == USBMouseRep.OTHER_MOUSE_MODE){

				if(show){
					if(robbie == null){
						try {
							robbie = new Robot();
						}
						catch( AWTException e )
						{
							Debug.out.println(e);
							//JOptionPane.showMessageDialog(parent, "Cannot create robot for mouse repositioning, aborting calibration!");
							robbie = null;
							return;
						}
					}
					JVStatusBar.OTHER_MOUISE_MODE_MSG = LocaleStrings.getString("E_1_JVIEW")+LocaleStrings.getString("E_7_JVIEW")+
														LocaleStrings.getString("E_2_JVIEW");
					JViewerApp.getInstance().getM_wndFrame().getM_status().resetStatus();
					if(invisCursorImg == null)
						invisCursorImg = createInvisibleCursor();
					if(invisibleCursor == null)
						invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor( invisCursorImg,
							new Point( 0, 0 ), "Invisible Cursor" );
					setCursor( invisibleCursor );
				}
				else{
					setCursor( null );
					JVStatusBar.OTHER_MOUISE_MODE_MSG = LocaleStrings.getString("E_1_JVIEW")+LocaleStrings.getString("E_7_JVIEW")+
														LocaleStrings.getString("E_3_JVIEW");
					JViewerApp.getInstance().getM_wndFrame().getM_status().resetStatus();
				}
				return;
			}
			//Clear the text on menu_string label 
	        if( show )
	        {
	        	if(m_USBMouseMode == USBMouseRep.RELATIVE_MOUSE_MODE)
	        		Mousecaliberation.resetCursor();	        	
	        	else
				setCursor( null );
			}
			else
			{
				if(invisCursorImg == null)
					invisCursorImg = createInvisibleCursor();
				if(invisibleCursor == null)
					invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor( invisCursorImg,
							new Point( 0, 0 ), "Invisible Cursor" );
				setCursor( invisibleCursor );
			}
	}


	/**
	 * Set USB Mouse mode
	 * @param mode Absolute or Relative
	 */
	public void SetUSBMouseMode(byte mode) {
		if(initMouseMode){
			initMouseMode = false;
		}
		else{
			InfoDialog.showDialog(JViewer.getMainFrame(), LocaleStrings.getString("E_6_JVIEW"),(long) 5000);
		}
		
		m_USBMouseMode = mode;
		JVMenu.m_mouseMode = mode;
	}

	/**
	 * Get USB Mouse Mode
	 * @return Returns Absolute or Relative Mouse modes. If the Mouse mode is not
	 *  initialized, it returns invalid mouse mode.
	 */
	public byte GetUSBMouseMode() {
		return m_USBMouseMode;
	}

    public BufferedImage getImage()
    {
        return localImage;
    }
	/**
	 * MOuse resync in relative mode
	 * @param x
	 * @param y
	 */
	public void mouse_resync_linux_mode(int x, int y)
	{
		if(JViewerView.Lost_focus_flag)
			return;
		if(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG )
			m_mouseListener.splitandsend(-m_width, -m_height, true);
		else{	
			//System.out.println("CALL SPLIT_AND_SEND_MOVE_RESYNC_LINUXMODE");
			m_mouseListener.splitandsend_Move(-m_width, -m_height, true);
		}
		JViewerView.Mouse_X =0;
		JViewerView.Mouse_Y = 0;
		// Find the current position of the local mouse
		// and send as dx,dy so that the host cursor will sync
		// to the local mouse.
		Point currMousePos = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(currMousePos, JViewerView.this);
		lastX = (int)(currMousePos.getX());
		lastY = (int)(currMousePos.getY());
		if(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG )
			m_mouseListener.splitandsend(lastX, lastY, true);
		else
			m_mouseListener.splitandsend_Move (lastX, lastY, true);
	}

	/**
	 * Synchronize mouse cursor.
	 */
	public boolean USBsyncCursor(boolean state) {

		if(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG)
			return false;
		if( state )	{
			/* check if kvm redirection is already running. The control may come here more than once if user tries to start
			mouse redirection using mouse double click */
			if( m_bMouseRedirRunning == true )
				return true;

			/* If invalid mouse mode, we have not yet received mouse mode response pkt
			from Video server
			*/
			if( m_USBMouseMode == USBMouseRep.INVALID_MOUSE_MODE )	{
				JViewerApp.getInstance().getMainWindow().generalErrorMessage(LocaleStrings.getString("E_4_JVIEW"),
						LocaleStrings.getString("E_5_JVIEW"));
				return false;
			}

			/* Turn ON Mouse redirection */
			addMouseListeners();
			m_bMouseRedirRunning = true;
		}
		else {
			/* Turn OFF Mouse redirection */
			removeMouseListeners();
			m_bMouseRedirRunning = false;
		} /* if G4 */
		return true;
	}

	/**
	 * Return the state whetehr the mouse redirection is running
	 * @return
	 */
	public boolean getMouseRedirStatus()
	{
		return m_bMouseRedirRunning;
	}

	/**
	 * Keyboard event listener class.
	 */
	class RCKeyListener extends KeyAdapter {
		public void keyTyped(KeyEvent e) {
			m_USBKeyRep.setKeyChar(e.getKeyChar());
		}

		/**
		 * Key press event handler.
		 *
		 * @param e
		 *            key event.
		 */
		public void keyPressed(KeyEvent e) {
			// A modifier which is set without the corresponding key being pressed, or menu item being selected
			//(Control key menus or satusbar toggle buttons), should be reset before a key event is being sent.
			byte modifiers = USBKeyProcessorEnglish.getModifiers();
			if(e.getKeyCode() == KeyEvent.VK_CAPS_LOCK ||
					e.getKeyCode() == KeyEvent.VK_NUM_LOCK ||
					e.getKeyCode() == KeyEvent.VK_SCROLL_LOCK){
				if(JViewerApp.getInstance().getPowerStatus() ==
						JViewerApp.SERVER_POWER_OFF){
					e.consume();
					JViewerApp.getInstance().resetLEDStatus();
					return;
				}
			}
			// If client OS is windows then consume the ALTGRAPH
			if(JViewerApp.getInstance().isWindowsClient()) {
				if(e.getKeyCode() != KeyEvent.VK_ALT_GRAPH && e.getModifiers() == KeyEvent.ALT_GRAPH_MASK){
					e.consume();
					return;
				}
			}
			if(((modifiers & KeyProcessor.MOD_LEFT_CTRL) == KeyProcessor.MOD_LEFT_CTRL)&&
					((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != KeyEvent.CTRL_DOWN_MASK)&&
					!JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_LEFT_CTRL_KEY)){
				modifiers &= ~KeyProcessor.MOD_LEFT_CTRL;
			}
			if(((modifiers & KeyProcessor.MOD_RIGHT_CTRL) == KeyProcessor.MOD_RIGHT_CTRL)&&
					((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != KeyEvent.CTRL_DOWN_MASK)&&
					!JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_RIGHT_CTRL_KEY)){
				modifiers &= ~KeyProcessor.MOD_RIGHT_CTRL;
			}
			if(((modifiers & KeyProcessor.MOD_LEFT_ALT) == KeyProcessor.MOD_LEFT_ALT)&&
					((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != KeyEvent.ALT_DOWN_MASK)&&
					!JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_LEFT_ALT_KEY)){
				modifiers &= ~KeyProcessor.MOD_LEFT_ALT;
			}
			if(((modifiers & KeyProcessor.MOD_RIGHT_ALT) == KeyProcessor.MOD_RIGHT_ALT)&&
					((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != KeyEvent.ALT_DOWN_MASK)&&
					!JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_RIGHT_ALT_KEY)){
				modifiers &= ~KeyProcessor.MOD_RIGHT_ALT;
			}
			if(((modifiers & KeyProcessor.MOD_LEFT_SHIFT) == KeyProcessor.MOD_LEFT_SHIFT)&&
					((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != KeyEvent.SHIFT_DOWN_MASK)){
				modifiers &= ~KeyProcessor.MOD_LEFT_SHIFT;
			}
			if(((modifiers & KeyProcessor.MOD_RIGHT_SHIFT) == KeyProcessor.MOD_RIGHT_SHIFT)&&
					((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != KeyEvent.SHIFT_DOWN_MASK)){
				modifiers &= ~KeyProcessor.MOD_RIGHT_SHIFT;
			}
			USBKeyProcessorEnglish.setModifiers(modifiers);
			Class cl;
			try {
				cl = Class.forName("java.awt.AWTEvent");
				Field f = cl.getDeclaredField("bdata");
				f.setAccessible(true);
				bdata  = (byte[])f.get(e);
			} catch (Exception e1) {
				Debug.out.println(e1);
			}
			
			keyCode = e.getKeyCode();
			keyLocation = e.getKeyLocation();
			keyPressedFlag++;
			KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
			byte led_status=0;
			
			if(e.getKeyCode() == KeyEvent.VK_WINDOWS)
				return;
			if ((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK) {
			//For windows client the AltGr key combination comes as CTRL+ALT. So when ALT_DOWN_MASK
			//is detected, and the key comes with CTRL_DOWN_MASK, we allow the key combinatoin to pass through,
			//even when full kekyboard support is disabled. Also it should be noted that, menu accelerators with
			//ALT key combination will not get triggered using AltGr.
				if(JViewerApp.getInstance().isWindowsClient() && 
						((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != KeyEvent.CTRL_DOWN_MASK)){
					if(!JViewerApp.getInstance().isFullKeyboardEnabled()){
						if (JViewerApp.getInstance().isFullScreenMode()) {
							/*
							 * In full screen mode, top menu is not associated with
							 * the view and so the regular shortcuts will not work.
							 * So the following keyboard shortcuts are required only
							 * in full screen mode. All the follwoing keyboard
							 * shortcuts are combined with ALT Key
							 */
							OnInvokeMenuShortCutFullscreen(e);
							e.consume();
						}
						//else return, so that the menu will handle the ALT+key combination as
						//menu accelerator, and perfom the menu action.
						return;
					}
				}
				// For Linux and Mac clients, process ALT+key combinations as menu item
				// accelerators if full keyboard support is disabled.
				else{
					if(!JViewerApp.getInstance().isFullKeyboardEnabled()){
						if (JViewerApp.getInstance().isFullScreenMode()) {
							/*
							 * In full screen mode, top menu is not associated with
							 * the view and so the regular shortcuts will not work.
							 * So the following keyboard shortcuts are required only
							 * in full screen mode. All the follwoing keyboard
							 * shortcuts are combined with ALT Key
							 */
							OnInvokeMenuShortCutFullscreen(e);
							e.consume();
						}
						//else return, so that the menu will handle the ALT+key combination as
						//menu accelerator, and perfom the menu action.
						return;
					}
				}
			}
			
			//  For Linux client Windows host and Linux client Linux host, when ALTGR is pressed, send alt to the host.
			if(JViewerApp.getInstance().isLinuxClient() && (e.getKeyCode() == KeyEvent.VK_ALT_GRAPH)) {
				m_USBKeyRep.set(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT, true);
				kvmClnt.sendKMMessage(m_USBKeyRep);
				return;
			}
			
			// if the host is linux and client is windows
			if(JViewerApp.getInstance().isWindowsClient() && JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.LINUX_HOST).isSelected()){
				// If CTRL is pressed, don't send it to the key processor just enable the isCtrlPressed flag.
				if(isCtrlPressed() == false && e.getKeyCode() == KeyEvent.VK_CONTROL){
					setCtrlKeyLocation(e.getKeyLocation());
					setCtrlPressed(true);
					return;
				}
				
				// If the Previous key event was CTRL and the current key event is ALT then send ALT alone.
				if((isCtrlPressed() == true) && (e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK){
					m_USBKeyRep.setKeyChar(e.getKeyChar());
					m_USBKeyRep.set(keyCode, keyLocation, true);
					kvmClnt.sendKMMessage(m_USBKeyRep);
					return;
				}else{
					//If the Previous key event was CTRL and the current key event is not ALT then send CTRL along with the current Key event.
					if(isCtrlPressed() == true)
					{
						// only if the previous key event was CTRL, the CTRL key event should be sent
						m_USBKeyRep.set(KeyEvent.VK_CONTROL, getCtrlKeyLocation(), true );
						kvmClnt.sendKMMessage(m_USBKeyRep);
					}
					// Current key event
					m_USBKeyRep.setKeyChar(e.getKeyChar());
					m_USBKeyRep.set(keyCode, keyLocation, true);
				}

			}
			/* If ALT+C is pressed when the mouse mode is other_mouse_mode, no need to send it to the KeyProcessor */
			if ((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK &&
					e.getKeyCode() == KeyEvent.VK_C && m_USBMouseMode == USBMouseRep.OTHER_MOUSE_MODE &&
					JViewerApp.getInstance().isFullKeyboardEnabled()) {
				boolean cursorMenuState = JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.MOUSE_CLIENTCURSOR_CONTROL);
				JViewerApp.getInstance().OnShowCursor(!cursorMenuState);
				e.consume();
				return;
			}
			
			
			
			/*
			 *Press CTRL+SHIFT+L to enable or disable debug logging 
			 */
			if(((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) &&
					((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK)){
				if(e.getKeyCode() == KeyEvent.VK_L){
					if(Debug.MODE != Debug.CREATE_LOG){
						Debug.out.initDebugLogFileChooser();
					}
					else{
						if(Debug.out.isRunTimeLogging()){
							Debug.MODE = Debug.RELEASE;
							Debug.out.closeLog();
						}
					}
					e.consume();
					return;
				}
			}

				/* Need to remember Ctrl key state to avoid CTRL+ESC condition */
			if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
				if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_LEFT)
					m_bLeftCtrlDown = true;
				else
					m_bRightCtrlDown = true;
				if (e.getKeyCode() == KeyEvent.VK_F1 && !JViewerApp.getInstance().isFullKeyboardEnabled()) {
					JViewerApp.getInstance().OnHelpAboutJViewer();
					e.consume();
					return;
				}
			}

			//Avoid Windows Security Dialog to pop out
			if (e.getKeyCode()== KeyEvent.VK_DELETE) {
				JVMenu menu = JViewerApp.getInstance().getJVMenu();
						if((menu.getMenuSelected(JVMenu.KEYBOARD_LEFT_CTRL_KEY) &&menu.getMenuSelected(JVMenu.KEYBOARD_RIGHT_CTRL_KEY)&&
								menu.getMenuSelected(JVMenu.KEYBOARD_RIGHT_ALT_KEY)) ||(menu.getMenuSelected(JVMenu.KEYBOARD_LEFT_CTRL_KEY) &&menu.getMenuSelected(JVMenu.KEYBOARD_RIGHT_CTRL_KEY)&&
										menu.getMenuSelected(JVMenu.KEYBOARD_LEFT_ALT_KEY)))
					return;
			}
			led_status=JViewerApp.getInstance().getHostKeyboardLEDStatus();
			if(e.getKeyCode() == KeyEvent.VK_NUM_LOCK || e.getKeyCode()== KeyEvent.VK_CAPS_LOCK || e.getKeyCode() == KeyEvent.VK_SCROLL_LOCK )
			{
				if(e.getKeyCode() == KeyEvent.VK_NUM_LOCK)
					led_status=(byte) (led_status ^ NUMLOCK);

				if(e.getKeyCode()== KeyEvent.VK_CAPS_LOCK)
					led_status=(byte) (led_status ^ CAPSLOCK);

				if(e.getKeyCode() == KeyEvent.VK_SCROLL_LOCK)
					led_status=(byte) (led_status ^ SCROLLLOCK);

				JViewerApp.getInstance().getM_wndFrame().getM_status().setKeyboardLEDStatus(led_status);
				JViewerApp.getInstance().getM_fsFrame().getM_menuBar().getLedStatusBar().setLEDStatus(led_status);
				JViewerApp.getInstance().setHostKeyboardLEDStatus(led_status);

				if(JViewerApp.getInstance().getAutokeylayout() != null)
					if(JViewerApp.getInstance().getAutokeylayout().OnkeyPressed(e))
						return;
			}
			if(bdata != null && bdata[14] == 115 )// to slove mac auto keyboard issue
				keyCode = 226;
			m_USBKeyRep.setKeyChar(e.getKeyChar());
			if( (System.getProperty("os.name").startsWith("Mac")) && (e.getKeyCode()== KeyEvent.VK_CAPS_LOCK) ) {
				m_USBKeyRep.set(keyCode, keyLocation, false);
				kvmClnt.sendKMMessage(m_USBKeyRep);
			}
			if( (System.getProperty("os.name").startsWith("Linux")) && ((keyCode >= KeyEvent.VK_A)&&(keyCode <= KeyEvent.VK_Z)) ) {
				m_USBKeyRep.set(keyCode, KeyEvent.KEY_LOCATION_STANDARD, true);
				kvmClnt.sendKMMessage(m_USBKeyRep);
			}
			else{
				m_USBKeyRep.set(keyCode,keyLocation,true);
				kvmClnt.sendKMMessage(m_USBKeyRep);
			}
			e.consume();
		}

		private void OnInvokeMenuShortCutFullscreen(KeyEvent e) {
			
			switch (e.getKeyCode()) {
				case KeyEvent.VK_F:
					JViewerApp.getInstance().OnVideoFullScreen(false);
					break;
				case KeyEvent.VK_E:
					JViewerApp.getInstance().OnVideoRefreshRedirection();
					break;
				case KeyEvent.VK_P:
					JViewerApp.getInstance().setM_userPause(true);
					JViewerApp.getInstance().OnVideoPauseRedirection();
					break;
				case KeyEvent.VK_R:
					JViewerApp.getInstance().OnVideoResumeRedirection();
					break;
				case KeyEvent.VK_T:
					if(m_USBMouseMode == USBMouseRep.RELATIVE_MOUSE_MODE) {
						if (JViewerApp.getInstance().GetRedirectionState() == JViewerApp.REDIR_STARTED) {
							boolean mouseThresState = JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.CALIBRATEMOUSETHRESHOLD);
							JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.CALIBRATEMOUSETHRESHOLD,!mouseThresState);
							JViewerApp.getInstance().OnCalibareteMouse(!mouseThresState);
							JViewerApp.getInstance().getRCView().requestFocus();
						}
					}
					break;
				case KeyEvent.VK_C:
					boolean cursurMenuState = JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.MOUSE_CLIENTCURSOR_CONTROL);
					JViewerApp.getInstance().OnShowCursor(!cursurMenuState);
					break;
				case KeyEvent.VK_N:
					if(JViewerApp.getInstance().getKVMClient().getHostLockStatus() == JViewerApp.HOST_DISPLAY_UNLOCK){
						if(JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_LOCK).isEnabled()){
							JViewerApp.getInstance().onSendHostLock(JViewerApp.HOST_DISPLAY_LOCK);
						}
					}
					else if(JViewerApp.getInstance().getKVMClient().getHostLockStatus() == JViewerApp.HOST_DISPLAY_LOCK){
						if(JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK).isEnabled()){
							JViewerApp.getInstance().onSendHostLock(JViewerApp.HOST_DISPLAY_UNLOCK);
						}
					}
					break;
				default:
					JViewerApp.getInstance().getSoc_App().OnInvokeSocMenuShortCutFullscreen(e);
			}
		}

		/**
		 * Key release event handler.
		 *
		 * @param e
		 *            key event.
		 */
		public void keyReleased(KeyEvent e) {
			
			Class cl;
			try {
				cl = Class.forName("java.awt.AWTEvent");
				Field f = cl.getDeclaredField("bdata");
				f.setAccessible(true);
				bdata  = (byte[])f.get(e);				
				
			} catch (Exception e1) {
				Debug.out.println(e1);
			}
			setCtrlPressed(false);
			keyCode = e.getKeyCode();
			keyLocation = e.getKeyLocation();
			
			if(bdata != null && bdata[14] == 115)// to slove mac auto keyboard issue
				keyCode = 226;
			m_USBKeyRep.set(keyCode, keyLocation, false);
			KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();

			//  For Linux client Windows host and Linux client Linux host, when ALTGR is pressed, alt is send to the host. So release the ALT key	
			if(JViewerApp.getInstance().isLinuxClient() &&
					((e.getModifiers() & KeyEvent.ALT_GRAPH_MASK) == KeyEvent.ALT_GRAPH_MASK)) {
				m_USBKeyRep.set(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT, false);
				kvmClnt.sendKMMessage(m_USBKeyRep);
				return;
			}

			/* Need to remember Ctrl key state to avoid CTRL+ESC condition */
			if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK) {
				if (keyLocation == KeyEvent.KEY_LOCATION_LEFT)
					m_bLeftCtrlDown = false;
				else
					m_bRightCtrlDown = false;
			}
			if(keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_CONTROL &&
					keyCode != KeyEvent.VK_ALT && keyCode != KeyEvent.VK_ALT_GRAPH){
				if(JViewerApp.getInstance().getAutokeylayout() != null){
					if(JViewerApp.getInstance().getAutokeylayout().OnkeyReleased(e)){
						e.consume();
						m_USBKeyRep.setKeyChar(KeyProcessor.NULL_CHAR);
						return;
					}
				}
			}
			if(JViewerApp.getInstance().isWindowsClient()) {
				if(keyCode != KeyEvent.VK_ALT_GRAPH && e.getModifiers() == KeyEvent.ALT_GRAPH_MASK){
					e.consume();
					m_USBKeyRep.setKeyChar(KeyProcessor.NULL_CHAR);
					return;
				}
			}
			m_USBKeyRep.setKeyChar(e.getKeyChar());
			if( (System.getProperty("os.name").startsWith("Mac")) && (e.getKeyCode()== KeyEvent.VK_CAPS_LOCK) ) {
				m_USBKeyRep.set(keyCode, keyLocation, true);
				kvmClnt.sendKMMessage(m_USBKeyRep);
			}
			m_USBKeyRep.set(keyCode, keyLocation, false);
			kvmClnt.sendKMMessage(m_USBKeyRep);
			//For Printscreen send key once. In linux host it will print screen thrice
			if(e.getKeyCode() == KeyEvent.VK_PRINTSCREEN)
			{
				if(keyPressedFlag == 0)
					keyPressedFlag++;
			}
			
			if(keyPressedFlag == 0){					
				m_USBKeyRep.set(e.getKeyCode(), e.getKeyLocation(), true);
				kvmClnt.sendKMMessage(m_USBKeyRep);
				m_USBKeyRep.set(e.getKeyCode(), e.getKeyLocation(), false);
				kvmClnt.sendKMMessage(m_USBKeyRep);					
			}					
			else
				keyPressedFlag--;
		
			e.consume();
			m_USBKeyRep.setKeyChar(KeyProcessor.NULL_CHAR);
		}
	}

	/**
	 * Mouse event listener class.
	 */
	public class RCMouseListener extends MouseInputAdapter {

		public static final byte MOUSE_MOVE = 0;
		public static final byte LBUTTON_DOWN = 0x01;
		public static final byte RBUTTON_DOWN = 0x02;
		public static final byte MBUTTON_DOWN = 0x04;
		private byte m_btnStatus = 0;

		public byte getM_btnStatus() {
			return m_btnStatus;
		}
		/**
		 * Mouse press event handler.
		 *
		 * @param e
		 *            mouse press event.
		 */
		public void mousePressed(MouseEvent e) {
			
			//  do not process the mouse pressed event if the mouse is in grey area.
			if( curX > (m_cur_width*scaleX) || curY > (m_cur_height*scaleY) || 
					JViewerApp.getInstance().getPowerStatus() != JViewerApp.SERVER_POWER_ON)
				return;

			if (m_USBMouseMode == USBMouseRep.OTHER_MOUSE_MODE)
			{
				//when mouse mode in "OTHER" and SHOW CURSOR is disabled don't send data to Host
				if(!JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.MOUSE_CLIENTCURSOR_CONTROL).isSelected())
						return;
			}

			requestFocus();// for solving the toolbar button focus issue
			curX = e.getX();
			curY = e.getY();
			curX = m_USBMouseMode == USBMouseRep.ABSOLUTE_MOUSE_MODE ? e.getX() : 0;
			curY = m_USBMouseMode == USBMouseRep.ABSOLUTE_MOUSE_MODE ? e.getY() : 0;

			if (curX < 0)
				curX = 0;
			if (curY < 0)
				curY = 0;

			switch (e.getButton()) {

			case MouseEvent.BUTTON1: // left mouse button
				m_btnStatus |= LBUTTON_DOWN;
				break;

			case MouseEvent.BUTTON2: // middle button
				m_btnStatus |= MBUTTON_DOWN;
				break;

			case MouseEvent.BUTTON3: // right button
				m_btnStatus |= RBUTTON_DOWN;
				break;

			default:
				return;
			}
			KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
			m_USBMouseRep.set(m_btnStatus, curX/scaleX, curY/scaleY,
					m_USBMouseMode, m_cur_width, m_cur_height, (byte) 0);
			kvmClnt.sendKMMessage(m_USBMouseRep);
			repaint();
		}

		/**
		 * Mouse release event handler.
		 *
		 * @param e
		 *            mouse release event.
		 */
		public void mouseReleased(MouseEvent e) {
	
			//  do not process the mouse pressed event if the mouse is in grey area.
			if( curX > (m_cur_width*scaleX) || curY > (m_cur_height*scaleY) ||
					JViewerApp.getInstance().getPowerStatus() != JViewerApp.SERVER_POWER_ON)
				return;
			
			if (m_USBMouseMode == USBMouseRep.OTHER_MOUSE_MODE)
			{
				//when mouse mode in "OTHER" and SHOW CURSOR is disabled don't send data to Host
				if(!JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.MOUSE_CLIENTCURSOR_CONTROL).isSelected())
						return;
			}

			curX = e.getX();
			curY = e.getY();

			curX = m_USBMouseMode == USBMouseRep.ABSOLUTE_MOUSE_MODE ? e.getX() : 0;
			curY = m_USBMouseMode == USBMouseRep.ABSOLUTE_MOUSE_MODE ? e.getY() : 0;

			if (curX < 0)
				curX = 0;
			if (curY < 0)
				curY = 0;

			switch (e.getButton()) {

			case MouseEvent.BUTTON1:
				m_btnStatus &= ~LBUTTON_DOWN;
				break;

			case MouseEvent.BUTTON2:
				m_btnStatus &= ~MBUTTON_DOWN;
				break;

			case MouseEvent.BUTTON3:
				m_btnStatus &= ~RBUTTON_DOWN;
				break;

			default:
				return;
			}
			KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
			m_USBMouseRep.set(m_btnStatus, curX/scaleX, curY/scaleY,
					m_USBMouseMode, m_cur_width, m_cur_height, (byte) 0);
			kvmClnt.sendKMMessage(m_USBMouseRep);
			repaint();
		}

		/**
		 * Mouse move event handler.
		 *
		 * @param e
		 *            mouse move event.
		 */
		public void mouseMoved(MouseEvent e) {
			JViewerApp.getInstance().getRCView().setMouse_event(e);
			return;
	    }

		/**
		 * Mouse drag event handler
		 *
		 * @param e
		 *            mouse drag event
		 */
		public void mouseDragged(MouseEvent e) {
			JViewerApp.getInstance().getRCView().setMouse_event(e);
			return;
		}
		/**
		 * Mouse enter event handler
		 *
		 * @param e mouse enter event
		 */
		public void mouseEntered(MouseEvent e) {
			mouseCursorOut = false;
			if(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG)
				return;
			if(JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.MOUSE_CLIENTCURSOR_CONTROL).isSelected())
				JViewerApp.showCursor = true;
			if((m_USBMouseMode == USBMouseRep.RELATIVE_MOUSE_MODE) ||
					(m_USBMouseMode == USBMouseRep.ABSOLUTE_MOUSE_MODE && !JViewerApp.showCursor) ||
					(m_USBMouseMode == USBMouseRep.OTHER_MOUSE_MODE && JViewerApp.showCursor)){
				//set invisible cursor when mouse comes in to the viewer.
				if(invisCursorImg == null)
					invisCursorImg = createInvisibleCursor();
				if(invisibleCursor == null)
					invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor( invisCursorImg,
							new Point( 0, 0 ), "Invisible Cursor" );
				setCursor( invisibleCursor );
			}
			if(m_USBMouseMode == USBMouseRep.RELATIVE_MOUSE_MODE && !Mousecaliberation.isCursorReset()){
				Mousecaliberation.resetCursor();
			}
		}

		/**
		 * Mouse exit event handler
		 * 
		 * @param e mouse exit event
		 */
		public void mouseExited(MouseEvent e) {
			mouseCursorOut = true;
			if(m_USBMouseMode == USBMouseRep.OTHER_MOUSE_MODE &&
					JViewerApp.getInstance().showCursor){
				Point pointCenter = calculateCenter( );
				if(robbie == null){
					try {
						robbie = new Robot();
					}
					catch( AWTException awe )
					{
						Debug.out.println(awe);
						robbie = null;
						return;
					}
				}
				robbie.mouseMove( pointCenter.x, pointCenter.y );
				return;
			}
			if(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG)
				return;
			//set default cursor when mouse goes out of teh viewer.
			if (m_USBMouseMode != USBMouseRep.OTHER_MOUSE_MODE) {
				invisCursorImg = null;
				setCursor(null);
			JViewerApp.getInstance().showCursor = false;
			repaint();
			}
		}

	    public int  splitandsend_Move(int xDisp, int yDisp, boolean isLinuxMode )
		{
			boolean XDiff_flag = false, YDiff_flag = false;
			//Mouse movement will be skipped only if the JViewerView has lost focus,
	    	//and the mouse cursor is out of the JViewerView
	    	if(JViewerView.Lost_focus_flag && mouseCursorOut)
	    		return 0;
	    	int ret = splitandsend(xDisp, yDisp,isLinuxMode );
	    	
	    	if(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG)
	    		return ret;
			Point p = JViewerApp.getInstance().getMainWindow().m_viewSP.getLocationOnScreen();
			int viewPositionX = p.x-JViewerApp.getInstance().getMainWindow().m_viewSP.getHorizontalScrollBar().getValue();
			int viewPositionY = p.y-JViewerApp.getInstance().getMainWindow().m_viewSP.getVerticalScrollBar().getValue();
			Point currMousePos = MouseInfo.getPointerInfo().getLocation();
			int Endx = p.x+JViewerApp.getInstance().getSocframeHdr().getresX();
			int Endy = p.y+JViewerApp.getInstance().getSocframeHdr().getresY();

			int XDiff = currMousePos.x -(viewPositionX+Mouse_X);
			int YDiff = currMousePos.y -(viewPositionY+Mouse_Y);

			if(robbie == null){
				try {
					robbie = new Robot();
				}
				catch( AWTException e )
				{
					Debug.out.println(e);
					//JOptionPane.showMessageDialog(parent, "Cannot create robot for mouse repositioning, aborting calibration!");
					robbie = null;
					return ret;
				}
			}
			if((XDiff <= -2 || XDiff >= 2 ) && currMousePos.x < Endx)
			{
				removeMouseListeners();
				robbie.mouseMove(viewPositionX+Mouse_X, currMousePos.y);
				if(System.getProperty("os.name").startsWith("Mac"))
					robbie.delay(30);// set a 30 millisecond delay between consecutive mouse movements.
				currMousePos = MouseInfo.getPointerInfo().getLocation();
				Point AftercurrMousePos = MouseInfo.getPointerInfo().getLocation();
				ret=1;
				lastX = AftercurrMousePos.x-viewPositionX ;
				addMouseListeners();
				XDiff_flag = true;
			}
			if((YDiff <= -2 || YDiff >= 2 ) && currMousePos.y < Endy)
			{
				removeMouseListeners();
				currMousePos = MouseInfo.getPointerInfo().getLocation();
				robbie.mouseMove(currMousePos.x,viewPositionY+Mouse_Y);
				if(System.getProperty("os.name").startsWith("Mac"))
					robbie.delay(30);// set a 30 millisecond delay between consecutive mouse movements.
				Point AftercurrMousePos = MouseInfo.getPointerInfo().getLocation();
				ret=1;
				lastY =AftercurrMousePos.y-viewPositionY ;
				addMouseListeners();
				YDiff_flag = true;
			}
			if( !XDiff_flag )
				lastX = curX;
			
			if( !YDiff_flag )
				lastY = curY;
			
			return ret;
	    }

	    public int  splitandsend(int xDisp, int yDisp, boolean isLinuxMode )
	    {
	    	//Mouse movement will be skipped only if the JViewerView has lost focus,
	    	//and the mouse cursor is out of the JViewerView
	    	if(JViewerView.Lost_focus_flag && mouseCursorOut)
	    		return 0;

	    	int ret=0;
			// compute the mouse move direction.
			int xDir = (xDisp >= 0) ? 1 : -1;
			int yDir = (yDisp >= 0) ? 1 : -1;

			removeMouseListeners();
			xDisp = Math.abs(xDisp);
			yDisp = Math.abs(yDisp);

			int x, y;

			// create multiple mouse events if the displacement is large.
			// max mouse movement an event can transfer is 126 both horiz/vert
			do {
			    x = (xDisp > 126) ? 126 : xDisp;
			    y = (yDisp > 126) ? 126 : yDisp;

			    xDisp -= x;
			    yDisp -= y;

			    	if( isLinuxMode ) {
			    	sendto_linux_target((x * xDir), (y * yDir));
				    int dx=(x * xDir);
				    int dy=(y * yDir);
				    if(firsttime)
					{
				    	Mouse_X = lastX;
				    	Mouse_Y = lastY;
						firsttime=false;
					}

					if((ABS(dx)+ABS(dy)) >= JViewerView.ACCEL_THRESHOLD)
					{
						float Rounded_Accel_X = (dx*JViewerView.MOUSE_ACCELERATION);
						float Rounded_Accel_Y = (dy*JViewerView.MOUSE_ACCELERATION);

						RemainingX += (Rounded_Accel_X % 1);
						RemainingY += (Rounded_Accel_Y % 1);
						Mouse_X  += (int)Rounded_Accel_X;
						Mouse_Y += (int)Rounded_Accel_Y;
						if (ABS(RemainingX) >= 1.0 && Mouse_X > 0)
						{
							if (RemainingX >= 1.0)
							{
								Mouse_X += 1;
								RemainingX -= 1.0;
							}
							else
							{
								Mouse_X -= 1;
								RemainingX += 1.0;
							}
						}

						if (ABS(RemainingY) >= 1.0 && Mouse_Y > 0)
						{
							if (RemainingY >= 1.0)
							{
								Mouse_Y += 1;
								RemainingY -= 1.0;
							}
							else
							{
								Mouse_Y -= 1;
								RemainingY += 1.0;
							}
						}
					}
					else
					{
						Mouse_X += 	dx;
						Mouse_Y += 	dy;
					}


					Point p = JViewerApp.getInstance().getMainWindow().m_viewSP.getLocationOnScreen();

					Point currMousePos = MouseInfo.getPointerInfo().getLocation();
					if(robbie == null){
						try {
							robbie = new Robot();
						}
						catch( AWTException e )
						{
							Debug.out.println(e);
							//JOptionPane.showMessageDialog(parent, "Cannot create robot for mouse repositioning, aborting calibration!");
							robbie = null;
							return ret;
						}
					}

					if(Mouse_X <= 0)
					{
						Mouse_X = 0;
						robbie.mouseMove(p.x, currMousePos.y);
						if(System.getProperty("os.name").startsWith("Mac"))
							robbie.delay(30);// set a 30 millisecond delay between consecutive mouse movements.
					}

					if(Mouse_Y <= 0)
					{
						Mouse_Y = 0;
						robbie.mouseMove(currMousePos.x,p.y);
						if(System.getProperty("os.name").startsWith("Mac"))
							robbie.delay(30);// set a 30 millisecond delay between consecutive mouse movements.
					}

					if(Mouse_X >= m_cur_width ) {
						Mouse_X =  m_cur_width - 1;
					}
					if(Mouse_Y >= m_cur_height) {
						Mouse_Y =  m_cur_height - 1;
					}

					repaint();
				}
				else {
					KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
					m_USBMouseRep.set( m_btnStatus, (x * xDir)/scaleX, (y * yDir)/scaleY,
						m_USBMouseMode, m_cur_width, m_cur_height, (byte) 0 );
					kvmClnt.sendKMMessage(m_USBMouseRep);
				}
			} while ((xDisp > 0) || (yDisp > 0));
			addMouseListeners();
			return ret;
		}

	    private int ABS(int x)
	    {
	    	return ( (x<0)?-x:x );
	    }

	    private float ABS(float x)
	    {
	    	return ( (x<0)?-x:x );
	    }

	    private void sendto_linux_target(int dx, int dy)
	    {
			KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
			m_USBMouseRep.set( m_btnStatus, dx/scaleX, dy/scaleY, 
			m_USBMouseMode, m_cur_width, m_cur_height, (byte) 0 );
			kvmClnt.sendKMMessage(m_USBMouseRep);
		}


	}

	/**
	 * Mouse event listener class.
	 */
	class RCFocusListener extends FocusAdapter
	{
		public void focusGained(FocusEvent e)
		{
			Lost_focus_flag= false;
			if(m_USBMouseMode == USBMouseRep.RELATIVE_MOUSE_MODE) 
				Mousecaliberation.resetCursor(); 
		}

		public void focusLost(FocusEvent e)
		{
			Lost_focus_flag= true;
		}
	}


	/**
	 * Mouse Wheel event listener class
	 */

	class RCMouseWheelListener implements MouseWheelListener{

		public void mouseWheelMoved(MouseWheelEvent e)
		{
	        byte wheelrotation = (byte) e.getWheelRotation();

	        if(wheelrotation<0)
	        	wheelrotation=1;
	        else
	        	wheelrotation=-1;

	        KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
			m_USBMouseRep.set((byte)0, (byte)0, (byte)0, 
					m_USBMouseMode, m_cur_width, m_cur_height,wheelrotation);
			kvmClnt.sendKMMessage(m_USBMouseRep);
		}
	}

	public USBKeyboardRep getM_USBKeyRep() {
		return m_USBKeyRep;
	}

	public void setM_USBKeyRep(USBKeyboardRep keyRep) {
		m_USBKeyRep = keyRep;
	}
	public MouseEvent getMouse_event() { 
		return mouse_event; 
 	} 
	 
 	public void setMouse_event(MouseEvent mouse_event) { 
		this.mouse_event = mouse_event; 
 	} 
 	 
	public USBMouseRep getM_USBMouseRep() { 
		return m_USBMouseRep; 
	} 
 
	public void setM_USBMouseRep(USBMouseRep mouseRep) { 
		m_USBMouseRep = mouseRep; 
	} 
	
	class MousesendTask extends TimerTask {
		public void run() {
			MouseEvent mousevent = JViewerApp.getInstance().getRCView().getMouse_event();
			MouseEvent mousesentevent = JViewerApp.getInstance().getRCView().getSentmouse_event();
			byte m_USBMouseMode = GetUSBMouseMode();
			USBMouseRep m_USBMouseRep = getM_USBMouseRep();

			byte mouseButtonStatus = JViewerApp.getInstance().getRCView().m_mouseListener.getM_btnStatus();

			if(m_cur_width <= 0)
				m_cur_width = JViewerApp.getInstance().getSocframeHdr().getcurwidth();
			if(m_cur_height <= 0)
				m_cur_height = JViewerApp.getInstance().getSocframeHdr().getcurheight();

			if(mousevent != null && mousesentevent != null)
			{
				long last_sent_event = mousesentevent.getWhen();
				long sent_event = mousevent.getWhen();
				if(last_sent_event == sent_event)
				{
					return;
				}
			}
			if(mousevent != null ){
				curX = mousevent.getX();
				curY = mousevent.getY();

				if (curX < 0)
					curX = 0;
				if (curY < 0)
					curY = 0;

				if( m_USBMouseMode == USBMouseRep.ABSOLUTE_MOUSE_MODE ){

					//  do not send the mouse move if it is in grey area.
					if( curX < (m_cur_width*scaleX) && curY < (m_cur_height*scaleY) && 
							JViewerApp.getInstance().getPowerStatus() == JViewerApp.SERVER_POWER_ON)
					{
						KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
						m_USBMouseRep.set(mouseButtonStatus, curX/scaleX, curY/scaleY, 
								m_USBMouseMode, m_cur_width, m_cur_height, (byte) 0);
						kvmClnt.sendKMMessage(m_USBMouseRep);
						JViewerApp.getInstance().getRCView().setSentmouse_event(mousevent);
					}
				}
				else if (m_USBMouseMode == USBMouseRep.RELATIVE_MOUSE_MODE ){

					// calculate displacement
					int xDisp = curX - lastX;
					int yDisp = curY - lastY;
					int ret = 0;
					//  do not send the mouse move if it is in grey area.
					if( curX < (m_cur_width*scaleX) && curY < (m_cur_height*scaleY) && 
							JViewerApp.getInstance().getPowerStatus() == JViewerApp.SERVER_POWER_ON)
					{
						ret = JViewerApp.getInstance().getRCView().m_mouseListener.splitandsend_Move(xDisp,yDisp,true);
					}
					JViewerApp.getInstance().getRCView().setSentmouse_event(mousevent);
					// store current mouse location to find the displacemen
					//for next mouse move.
					if(ret == 0)
					{
//						lastX = curX;
//						lastY = curY;
					}
				}
				//Handle mouse events in Other mouse mode.
				else if(m_USBMouseMode == USBMouseRep.OTHER_MOUSE_MODE){
					int robotDX = 0;
					int robotDY = 0;
					int mousePrevX = 0;
					int mousePrevY = 0;

					if(!JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.MOUSE_CLIENTCURSOR_CONTROL)||
							JViewerApp.getInstance().getPowerStatus() == JViewerApp.SERVER_POWER_OFF
							|| Lost_focus_flag){
						return;
					}
					else{
						/* If this is the very first mouse movement */
						if( lastX == -1 )
						{
							/* Set the base point and last position as the center of the screen */
							//JRedirPanel redirPanel = (JRedirPanel)( e.getSource() );
							Point windowCenter = calculateCenter( );
							SwingUtilities.convertPointToScreen(windowCenter, JViewerView.this.getTopLevelAncestor());
							SwingUtilities.convertPointFromScreen(windowCenter, JViewerView.this);
							lastX = windowCenter.x;
							lastY = windowCenter.y;
						}

						/* If this movement came from the robot, then ignore the movement
						 * and reset the robot position change data */
						//System.out.println("LASTX-CURX : "+(lastX - curX)+" :: LASTY-CURY : "+(lastY - curY));
						//System.out.println("ROBOTDX : "+robotDX+" :: ROBOTDY : "+robotDY);
						if( ( ( lastX - curX ) == robotDX ) && ( ( lastY - curY ) == robotDY ) )
						{
							robotDX = 0;
							robotDY = 0;
							lastX = curX;
							lastY = curY;
							return;
						}

						/*Update the robot position change for which we are watching */
						robotDX += ( curX - lastX );
						robotDY += ( curY - lastY );
						/* We have to keep track of change in position, so we have to
						 * keep track of our last position */
						lastX = curX;
						lastY = curY;

						/*
				OK. Here there are 3 different forms of co-ordinates.
				a. co-ordinates relative to the top level window.
				b. co-ordinates relative to the view in which the
				   redirected image is displayed
				c. co-ordinates relative to entire screen.

				First we get the co-ordinates relative to top level window, calculate
				the center position and move back the mouse to that position for every
				mouse move. But unfortunately, there are no API that takes this co-ordinates
				to move the mouse. So, we convert this co-ordinates to that of screen and use
				the API to move the mouse.

				Now, we need the displacement mouse move values to send to the card. In
				MouseEvent object we get mouse co-ordinates relative to the view i.e. 3rd form.
				So,	we need to convert this co-ordinates to that of view and calculate
				the difference to get the actual mouse moved values.

						 */
						/* get the center point of actual toplevel window */
						Point pointCenter = calculateCenter( );

						/* Convert them to screen co-ordinates */
						SwingUtilities.convertPointToScreen(pointCenter, JViewer.getMainFrame());

						/* Reposition mouse cursor (must be absolute desktop coordinates) */
						if(robbie == null){
							try {
								robbie = new Robot();
							}
							catch( AWTException e )
							{
								Debug.out.println(e);
								//JOptionPane.showMessageDialog(parent, "Cannot create robot for mouse repositioning, aborting calibration!");
								robbie = null;
								return;
							}
						}
						robbie.mouseMove( pointCenter.x, pointCenter.y );

						/* Now again convert this co-ordinates to that of VIEW. */
						SwingUtilities.convertPointFromScreen(pointCenter, JViewerView.this);

						mousePrevX = pointCenter.x;
						mousePrevY = pointCenter.y;

						/* Update the mouse processor's copy of the base point (relative) *
						input.mouseProcessor.setBasePoint( windowCenter.x, windowCenter.y ); */
						//make sure that prev values are not beyond the size of view
						if (mousePrevX >= m_width) {
							mousePrevX = m_width - 1;
						}

						if (mousePrevY >= m_height) {
							mousePrevY = m_height - 1;
						}

						// calculate displacement
						int xDisp = curX - mousePrevX;
						int yDisp = curY - mousePrevY;

						// store current mouse location to find the displacement
						// for next mouse move.
						mousePrevX = curX;
						mousePrevY = curY;

						// compute the mouse move direction.
						int xDir = (xDisp >= 0) ? 1 : -1;
						int yDir = (yDisp >= 0) ? 1 : -1;

						xDisp = Math.abs(xDisp);
						yDisp = Math.abs(yDisp);


						// When numerous short packets are transmitted UEFI is not happy, so let's keep a threshold limit & will only
						// send the mouse movement if it has been reached

						int x, y;
						KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();

						// create multiple mouse events if the displacement is large.
						// max mouse movement an event can transfer is 126 both horiz/vert
						do {

							x = (xDisp > 126) ? 126 : xDisp;
							y = (yDisp > 126) ? 126 : yDisp;

							xDisp -= x;
							yDisp -= y;
							m_USBMouseRep.set(mouseButtonStatus, (x * xDir), (y * yDir), m_USBMouseMode, m_cur_width, m_cur_height, (byte) 0);
							kvmClnt.sendKMMessage(m_USBMouseRep);
						} while ((xDisp > 0) || (yDisp > 0));

						JViewerApp.getInstance().getRCView().setSentmouse_event(mousevent);
					}
				}
				repaint();
			}

		}
	}

	public MouseEvent getSentmouse_event() {
		return sentmouse_event;
	}

	public void setSentmouse_event(MouseEvent sentmouse_event) {
		this.sentmouse_event = sentmouse_event;
	}

	/**
	 * Calculate the center point of the screen.
	 * @return - a point object, which is the center point of the screen 
	 */
	private Point calculateCenter()
	{
		Point viewLoc = JViewerApp.getInstance().getRCView().getLocationOnScreen();
		Point viewCenter = new Point( ( (m_cur_width + viewLoc.x)/ 2 ), ( (m_cur_height + viewLoc.y) / 2 ) );
		return viewCenter;
	}
	/**
	 * Sets the value of video scale factors, scaleX and scaleY.
	 * @param scaleX - value of scale factor in X coordinate
	 * @param scaleY - value of scale factor in Y coordinate
	 */
	public void setScaleFactor(double scaleX, double scaleY){
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	/**
	 * @return the scaleX
	 */
	public double getScaleX() {
		return scaleX;
	}

	/**
	 * @param scaleX the scaleX to set
	 */
	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	/**
	 * @return the scaleY
	 */
	public double getScaleY() {
		return scaleY;
	}

	/**
	 * @param scaleY the scaleY to set
	 */
	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	/**
	 * @return the mouseCursorOut
	 */
	public boolean isMouseCursorOut() {
		return mouseCursorOut;
	}

	/**
	 * @param mouseCursorOut the mouseCursorOut to set
	 */
	public void setMouseCursorOut(boolean mouseCursorOut) {
		this.mouseCursorOut = mouseCursorOut;
	}

	/**
	 * Creates the invisible cursor image.
	 * @return Buffered image to be set as the invisible cursor.
	 */
	public BufferedImage createInvisibleCursor(){
		BufferedImage img = new BufferedImage (32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics(); 
		AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f);
		g.setComposite(composite);
		return img;
	}

	/**
	 * @return the isCtrlPressed
	 */
	public boolean isCtrlPressed() {
		return isCtrlPressed;
	}

	/**
	 * @param isCtrlPressed the isCtrlPressed to set
	 */
	public void setCtrlPressed(boolean bool) {
		isCtrlPressed = bool;
	}

	/**
	 * @return the ctrlKeyLocation
	 */
	public int getCtrlKeyLocation() {
		return ctrlKeyLocation;
	}

	/**
	 * @param ctrlKeyLocation the ctrlKeyLocation to set
	 */
	public void setCtrlKeyLocation(int ctrlKeyLocation) {
		this.ctrlKeyLocation = ctrlKeyLocation;
	}

}

