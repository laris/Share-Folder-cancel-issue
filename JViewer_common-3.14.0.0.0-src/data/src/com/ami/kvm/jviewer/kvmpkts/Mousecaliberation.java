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

package com.ami.kvm.jviewer.kvmpkts;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.gui.JVMenu;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.JViewerView;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.kvm.jviewer.hid.USBMouseRep;


public class Mousecaliberation {

	public static boolean Threshold_Started = false;
	public static int PREV_ACCEL_THRESHOLD=4;
	public static float PREV_MOUSE_ACCELERATION=2.0F;
	public static boolean THRESHOLDFLAG= false;
	public static boolean ACCELERATION_FLAG = false;

	RCMouseListener m_mouseListener; // mouse event handler
	RCKeyListener m_keyListener; // keyboard event handler

    static public Robot robbie;    
    static int curX = 0;
	static int curY = 0;
	static int lastX = -1;
    static int lastY = -1;

    public static int i;
    public static int j;
    public static final String THRESH_MSG = LocaleStrings.getString("3_1_MC");
	public static final String ACCEL_MSG  = LocaleStrings.getString("3_2_MC");
	public static final String THRESH_CONF = LocaleStrings.getString("3_3_MC");
	public static final String ACCEL_CONF = LocaleStrings.getString("3_4_MC");

	private Timer 			m_frTmr1;
	private Timer 			m_frTmr2;
	private boolean first_time = true;
	private static boolean cursorReset = true;

	/**
	 * Removing the existing keylistener and adding the keylistener for MOuse caliberation
	 *
	 */
	public Mousecaliberation(){
		
		m_mouseListener = new RCMouseListener();
		m_keyListener = new RCKeyListener();
		
	}

	/**
	 * Claibearting the mouse threshold,User give the threshold value based on the value
	 * input  given the mouse movement will be done based on the input given
	 * @param state - true / false - start or stop the calibearation
	 */
	public void OnCalibareteMouseThreshold(boolean state) {
		JInternalFrame parent = JViewerApp.getInstance().getMainWindow();
		if(Threshold_Started)
		{
			OnCalibareteMouseAcceleration(false);
			Threshold_Started= false;
			return;
		}
		//		adding the listener for mouse calibearion mouse event and keyboard events
		if(first_time){
			JViewerApp.getInstance().getRCView().addKeyListener(m_keyListener);
			JViewerApp.getInstance().getRCView().addMouseListener(m_mouseListener);
			JViewerApp.getInstance().getRCView().addMouseMotionListener(m_mouseListener);
			first_time = false;
		}

		if (state)
		{
			if( JViewerApp.getInstance().getRCView().GetUSBMouseMode() == USBMouseRep.RELATIVE_MOUSE_MODE )
			{
				if(robbie == null){
					try {
						robbie = new Robot();
					}
					catch( AWTException e )
					{
						System.err.println(LocaleStrings.getString("3_11_MC"));
						Debug.out.println(e);
						robbie = null;
						return;
					}
				}
			}

			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_PAUSE_REDIRECTION, false);
			
			THRESHOLDFLAG = true;
			PREV_ACCEL_THRESHOLD = JViewerView.ACCEL_THRESHOLD;
			PREV_MOUSE_ACCELERATION = JViewerView.MOUSE_ACCELERATION;
			JViewerView.ACCEL_THRESHOLD = 1;
			JViewerView.MOUSE_ACCELERATION = 1.0F; //Set to 1 so that we can find out mouse threshold
			JViewerApp.getInstance().OnShowCursor(false);
			ShowCursor_caliberate(true);			
			resetCursor();//reset the cursor
			
			JOptionPane.showMessageDialog(parent, THRESH_MSG, LocaleStrings.getString("3_5_MC"), 
					JOptionPane.INFORMATION_MESSAGE);
			m_frTmr2 = new Timer();
			m_frTmr2.schedule(new FrameRateTask1(), 0, 750);
		}
		else
		{			
			if(Threshold_Started)
			{
				m_frTmr1.cancel();
				robbie=null;
				JViewerApp.getInstance().getJVMenu().menu_string.setText("");
				JInternalFrame frame = JViewerApp.getInstance().getMainWindow();
				UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
				int ret = JOptionPane.showConfirmDialog(frame,ACCEL_CONF,LocaleStrings.getString("3_5_MC"), 
														JOptionPane.YES_NO_OPTION);
				if(ret == JOptionPane.YES_OPTION)
				{
					Mousecaliberation.resetCursor();
					JViewerApp.getInstance().getRCView().removeKeyListener(m_keyListener);
					JViewerApp.getInstance().getRCView().removeMouseListener(m_mouseListener);
					JViewerApp.getInstance().getRCView().removeMouseMotionListener(m_mouseListener);
					OnCalibareteMouseAcceleration(true);
					
				}

				if( ret == JOptionPane.NO_OPTION  || ret == JOptionPane.CLOSED_OPTION)
				{
					JViewerView.MOUSE_ACCELERATION=PREV_MOUSE_ACCELERATION;
					JViewerView.ACCEL_THRESHOLD = PREV_ACCEL_THRESHOLD;
					JViewerApp.getInstance().getRCView().removeKeyListener(m_keyListener);
					JViewerApp.getInstance().getRCView().removeMouseListener(m_mouseListener);
					JViewerApp.getInstance().getRCView().removeMouseMotionListener(m_mouseListener);
					JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_PAUSE_REDIRECTION, true);
					ShowCursor_caliberate(false);
				}
			}
			else
			{


				m_frTmr2.cancel();
				robbie=null;
				JViewerApp.getInstance().OnShowCursor(false);
				JViewerApp.getInstance().getJVMenu().menu_string.setText("");								
				JInternalFrame frame = JViewerApp.getInstance().getMainWindow();
				UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
				int ret = JOptionPane.showConfirmDialog(frame, THRESH_CONF,LocaleStrings.getString("3_5_MC"),
														JOptionPane.YES_NO_OPTION);				
				if(ret == JOptionPane.YES_OPTION)
				{	
					JViewerApp.getInstance().getRCView().mouse_resync_linux_mode(0,0);
					JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_PAUSE_REDIRECTION, true);
					Threshold_Started = true;
					JViewerView.MOUSE_ACCELERATION = PREV_MOUSE_ACCELERATION;
					OnCalibareteMouseAcceleration(true);
					

				}
				if( ret == JOptionPane.NO_OPTION || ret ==  JOptionPane.CLOSED_OPTION)
				{
						JViewerView.ACCEL_THRESHOLD = PREV_ACCEL_THRESHOLD;
						JViewerView.MOUSE_ACCELERATION = PREV_MOUSE_ACCELERATION;
						JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_PAUSE_REDIRECTION, true);
						JViewerApp.getInstance().getRCView().removeKeyListener(m_keyListener);
						JViewerApp.getInstance().getRCView().removeMouseListener(m_mouseListener);
						JViewerApp.getInstance().getRCView().removeMouseMotionListener(m_mouseListener);
						JViewerApp.getInstance().getRCView().addKMListeners();
						JViewerApp.getInstance().getVidClnt().setFullScreenMode();//to set the fullscreen menu option.
						first_time = true;
						ShowCursor_caliberate(false);
				}				
				THRESHOLDFLAG = false;
			}
		}	
		
	}

	/**
	 * Drawing the mouse in the GUI for caliberation
	 *
	 */
	public void ShowCursor_caliberate(boolean show)
	{
		URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/hvcur.gif");
		BufferedImage img = null;
		try {
			if(show)
			img = ImageIO.read(imageURL);
			else
				img = new BufferedImage( 32, 32, BufferedImage.TYPE_INT_ARGB_PRE );
		} catch (IOException e) {
			Debug.out.println(e);
		}
		Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor( img,
								new Point( 0, 0 ), "Invisible Cursor" );
		JViewerApp.getInstance().getRCView().setCursor( invisibleCursor );
	}

	/**
	 * Claibearting the mouse Acceleration,User give the threshold value based on the value
	 * input  given the mouse movement will be done based on the input given
	 * @param state - true / false - start or stop the calibearation
	 */
	public void OnCalibareteMouseAcceleration(boolean state) {
		JInternalFrame parent = JViewerApp.getInstance().getMainWindow();
		if (state)
		{
			if(robbie == null){
				try {
					robbie = new Robot();
				}
				catch( AWTException e )
				{
					System.err.println(LocaleStrings.getString("3_11_MC"));
					Debug.out.println(e);
					robbie = null;
					return;
				}
			}

			PREV_MOUSE_ACCELERATION = JViewerView.MOUSE_ACCELERATION;
			ShowCursor_caliberate(true);
			if(JViewerView.Lost_focus_flag)
				JViewerView.Lost_focus_flag = false;	
			resetCursor();//reset the cursor
			ACCELERATION_FLAG = true;
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_PAUSE_REDIRECTION, false);
			JViewerView.MOUSE_ACCELERATION = 1.0F;			
			JOptionPane.showMessageDialog(parent, ACCEL_MSG, LocaleStrings.getString("3_5_MC"), 
					JOptionPane.INFORMATION_MESSAGE);
			JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.CALIBRATEMOUSETHRESHOLD).setSelected(true);
			m_frTmr1 = new Timer();
			m_frTmr1.schedule(new FrameRateTask(), 0, 750);


		}
		else
		{
			m_frTmr1.cancel();
			ACCELERATION_FLAG = false;
			robbie=null;
			JViewerApp.getInstance().OnShowCursor(false);
			JViewerApp.getInstance().getJVMenu().menu_string.setText("");
			
			JInternalFrame frame = JViewerApp.getInstance().getMainWindow();
			UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
			int ret = JOptionPane.showConfirmDialog(frame,ACCEL_CONF,LocaleStrings.getString("3_5_MC"), 
													JOptionPane.YES_NO_OPTION);
			
			if(ret == JOptionPane.YES_OPTION)
			{
				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_PAUSE_REDIRECTION, true);
				JViewerApp.getInstance().getRCView().removeKeyListener(m_keyListener);
				JViewerApp.getInstance().getRCView().removeMouseListener(m_mouseListener);
				JViewerApp.getInstance().getRCView().removeMouseMotionListener(m_mouseListener);
			}
			if( ret == JOptionPane.NO_OPTION )
			{
				JViewerView.MOUSE_ACCELERATION = PREV_MOUSE_ACCELERATION;
				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_PAUSE_REDIRECTION, true);
				JViewerApp.getInstance().getRCView().removeKeyListener(m_keyListener);
				JViewerApp.getInstance().getRCView().removeMouseListener(m_mouseListener);
				JViewerApp.getInstance().getRCView().removeMouseMotionListener(m_mouseListener);
			}
			first_time = true;
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.CALIBRATEMOUSETHRESHOLD, false);			
			Mousecaliberation.resetCursor();
			ShowCursor_caliberate(false);

		}
		JViewerApp.getInstance().getVidClnt().setFullScreenMode();
	}

	/**
	 * @return the resetCursor
	 */
	public static boolean isCursorReset() {
		return cursorReset;
	}

	/**
	 * @param cursorReset the resetCursor to set
	 */
	public static void setCursorReset(boolean cursorReset) {
		Mousecaliberation.cursorReset = cursorReset;
	}

	/**
	 * Resets the client and host cursor to the initial position.
	 */
	public static void resetCursor(){
		if(!JViewerApp.getInstance().getVidClnt().isNewFrame() || JViewerView.Lost_focus_flag){
			cursorReset = false;
			return;
		}
		int m_width = JViewerApp.getInstance().getRCView().viewWidth();
		int m_height = JViewerApp.getInstance().getRCView().viewHeight();
		int moveX = JViewerApp.getInstance().getMainWindow().getM_viewSP().getHorizontalScrollBar().getValue();
		int moveY = JViewerApp.getInstance().getMainWindow().getM_viewSP().getVerticalScrollBar().getValue();
		if(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG){
			//set the cursor to the initial position.
			JViewerApp.getInstance().getRCView().m_mouseListener.splitandsend(-m_width, -m_height, true);
			//Move the cursor to the top left corner of the visible area of the scroll pane. 
			JViewerApp.getInstance().getRCView().m_mouseListener.splitandsend((moveX)/2, (moveY)/2, true);
			cursorReset = true;
			i = getViewPoint().x;
			j = getViewPoint().y;
			if(robbie == null){
				try {
					robbie = new Robot();
				}
				catch( AWTException e )
				{
					System.err.println(LocaleStrings.getString("3_11_MC"));
					Debug.out.println(e);
					robbie = null;
					return;
				}
			}
			robbie.mouseMove(i+1, j+1);
		}
		else{
			//set the cursor to the initial position.
			JViewerApp.getInstance().getRCView().m_mouseListener.splitandsend_Move(-m_width, -m_height, true);
			//Move the cursor to the top left corner of the visible area of the scroll pane. 
			JViewerApp.getInstance().getRCView().m_mouseListener.splitandsend_Move((moveX+1)/2, (moveY+1)/2, true);
			cursorReset = true;
		}
	}
	
	/**
	 * Get the initial point of screen coordinate of the viewer
	 * @return
	 */
	public static Point getViewPoint(){
		Point viewPoint;
		if(JViewerApp.getInstance().isFullScreenMode())
			viewPoint = JViewerApp.getInstance().getRCView().getLocationOnScreen();
		else
			viewPoint = JViewerApp.getInstance().getMainWindow().getM_viewSP().getLocationOnScreen();
		return viewPoint;
	}
}
	/**
	 *
	 * Timer task for generating the mouse movement based for Mouse accelearation caliberation
	 *
	 */
	class FrameRateTask extends TimerTask
	{
		/**
		 * Task routine.
		 */
		static int top = 1, right = 1, flip = 0;
		Point viewPoint;
		
		/**
		 * FrameRateTask Constructor
		 */
		public FrameRateTask(){
			viewPoint = Mousecaliberation.getViewPoint();
			Mousecaliberation.i = viewPoint.x;
			Mousecaliberation.j = viewPoint.y;
		}

		public void run()
		{
			if(!JViewerView.Lost_focus_flag)
			{
				int Height = JViewerApp.getInstance().getSocframeHdr().getheight();
				int width = JViewerApp.getInstance().getSocframeHdr().getheight();
				int viewHeight;
				int viewWidth;
				
				if(JViewerApp.getInstance().isFullScreenMode())
					JViewerApp.getInstance().getM_fsFrame().getM_menuBar().setIDLabel(
							LocaleStrings.getString("3_6_MC")+" - "+JViewerView.MOUSE_ACCELERATION+"  :  "+
							LocaleStrings.getString("3_7_MC")+" - "+JViewerView.ACCEL_THRESHOLD);
				else
					JViewerApp.getInstance().getJVMenu().menu_string.setText("***"+
							LocaleStrings.getString("3_6_MC")+" "+LocaleStrings.getString("3_8_MC")+"*** "+
							LocaleStrings.getString("3_6_MC")+" : "+JViewerView.MOUSE_ACCELERATION +"   "+
							LocaleStrings.getString("3_7_MC")+" : "+(JViewerView.ACCEL_THRESHOLD)+"    ");
				

				float Rounded_Accel_X = (JViewerView.ACCEL_THRESHOLD * JViewerView.MOUSE_ACCELERATION);
				float Rounded_Accel_Y = (JViewerView.ACCEL_THRESHOLD * JViewerView.MOUSE_ACCELERATION);

				BigDecimal rate=new BigDecimal(Rounded_Accel_X);
				BigDecimal stepped_value;
				if ((Rounded_Accel_X % 1) >= 0.5)
				{
					stepped_value = rate.setScale(0, BigDecimal.ROUND_HALF_UP);
				}
				else
				{
					stepped_value = rate.setScale(0, BigDecimal.ROUND_HALF_DOWN);
				}
				Rounded_Accel_X = stepped_value.floatValue();

				rate=new BigDecimal(Rounded_Accel_Y);
				if ((Rounded_Accel_Y % 1) >= 0.5)
				{
					stepped_value = rate.setScale(0, BigDecimal.ROUND_HALF_UP);
				}
				else
				{
					stepped_value = rate.setScale(0, BigDecimal.ROUND_HALF_DOWN);
				}
				Rounded_Accel_Y = stepped_value.floatValue();

				Mousecaliberation.i += (int)(Rounded_Accel_X);
				Mousecaliberation.j += (int)(Rounded_Accel_Y);
					
				if(JViewerApp.getInstance().isFullScreenMode()){
					viewHeight = JViewerApp.getInstance().getRCView().getHeight();
					viewWidth = JViewerApp.getInstance().getRCView().getWidth();
				}
				else{
					viewHeight = JViewerApp.getInstance().getMainWindow().getM_viewSP().getHeight();
					viewWidth = JViewerApp.getInstance().getRCView().getWidth();
				}
				if(Mousecaliberation.j > Height || Mousecaliberation.j > viewHeight || Mousecaliberation.i > viewWidth)
				{
					if(!JViewerView.Lost_focus_flag)
					{
						int initX = JViewerApp.getInstance().getMainWindow().getM_viewSP().getHorizontalScrollBar().getValue() - width;
						int initY = JViewerApp.getInstance().getMainWindow().getM_viewSP().getVerticalScrollBar().getValue() - Height;
						JViewerApp.getInstance().getRCView().m_mouseListener.splitandsend(initX, initY, true);
						Mousecaliberation.robbie.mouseMove(viewPoint.x+1,viewPoint.y+1);
						Mousecaliberation.i = viewPoint.x+1;
						Mousecaliberation.j = viewPoint.y+1;
					}
				}
				Mousecaliberation.robbie.mouseMove(Mousecaliberation.i,Mousecaliberation.j);
				JViewerApp.getInstance().getRCView().m_mouseListener.splitandsend(JViewerView.ACCEL_THRESHOLD, JViewerView.ACCEL_THRESHOLD, true);
			}
		}
	}

	/**
	 *
	 * Timer task for generating the mouse movement based for Mouse Threshold caliberation
	 *
	 */
	class FrameRateTask1 extends TimerTask
	{
		static int top = 1, right = 1, flip = 0;
		Point viewPoint;
		
		/**
		 * FrameRateTask1 Constructor
		 */
		public FrameRateTask1(){
			viewPoint = Mousecaliberation.getViewPoint();
			Mousecaliberation.i = viewPoint.x;
			Mousecaliberation.j = viewPoint.y;
		}
		
		public void run()
		{
			if(!JViewerView.Lost_focus_flag) {
				
				JInternalFrame frame = JViewerApp.getInstance().getMainWindow();				
				int Height = JViewerApp.getInstance().getSocframeHdr().getheight();
				int viewHeight;
				int viewWidth;
				Rectangle bound_frame = JViewerApp.getInstance().getMainWindow().getBounds();				
				Mousecaliberation.robbie.mouseMove(Mousecaliberation.i,Mousecaliberation.j);
				if(JViewerApp.getInstance().isFullScreenMode())
					JViewerApp.getInstance().getM_fsFrame().getM_menuBar().setIDLabel(
							LocaleStrings.getString("3_6_MC")+" - "+JViewerView.MOUSE_ACCELERATION+"  :  "+
							LocaleStrings.getString("3_7_MC")+" - "+JViewerView.ACCEL_THRESHOLD);
				else
					JViewerApp.getInstance().getJVMenu().menu_string.setText("***"+
							LocaleStrings.getString("3_7_MC")+" "+LocaleStrings.getString("3_8_MC")+"*** "+
							LocaleStrings.getString("3_6_MC")+" : "+JViewerView.MOUSE_ACCELERATION +"   "+
							LocaleStrings.getString("3_7_MC")+" : "+(JViewerView.ACCEL_THRESHOLD)+"    ");				
				if (flip == 0) {
					Mousecaliberation.i += JViewerView.ACCEL_THRESHOLD;
					flip = 1;
				}
				else {
					Mousecaliberation.j += JViewerView.ACCEL_THRESHOLD;
					flip = 0;
				}
				if(JViewerApp.getInstance().isFullScreenMode()){
					viewHeight = JViewerApp.getInstance().getRCView().getHeight();
					viewWidth = JViewerApp.getInstance().getRCView().getWidth();
				}
				else{
					viewHeight = JViewerApp.getInstance().getMainWindow().getM_viewSP().getHeight();
					viewWidth = JViewerApp.getInstance().getRCView().getWidth();
				}
				
				if(Mousecaliberation.j > Height || Mousecaliberation.j > viewHeight || Mousecaliberation.i > viewWidth)
				{
					if(!JViewerView.Lost_focus_flag)
					{						
						Mousecaliberation.robbie.mouseMove(frame.getInsets().left+bound_frame.x,viewPoint.y+bound_frame.y);
						Mousecaliberation.i = viewPoint.x+1;
						Mousecaliberation.j = viewPoint.y+1;
					}
				}
			}
		}
	}

	class RCMouseListener extends MouseInputAdapter
	{
		public void mouseMoved(MouseEvent e) {				

			if(!Mousecaliberation.ACCELERATION_FLAG)
			{
				Mousecaliberation.curX = e.getX();
				Mousecaliberation.curY = e.getY();

				if (Mousecaliberation.curX < 0)
					Mousecaliberation.curX = 0;
				if (Mousecaliberation.curY < 0)
					Mousecaliberation.curY = 0;
				if(Mousecaliberation.isCursorReset()){
					Mousecaliberation.lastX = Mousecaliberation.curX;
					Mousecaliberation.lastY = Mousecaliberation.curY;
					Mousecaliberation.setCursorReset(false);
				}
				// calculate displacement
			    int xDisp = Mousecaliberation.curX - Mousecaliberation.lastX;
			    int yDisp = Mousecaliberation.curY - Mousecaliberation.lastY;
			    
			    JViewerApp.getInstance().getRCView().m_mouseListener.splitandsend(xDisp,yDisp,true);

			    // store current mouse location to find the displacement
			    // for next mouse move.
			    Mousecaliberation.lastX = Mousecaliberation.curX;
			    Mousecaliberation.lastY = Mousecaliberation.curY;
			}
		}
	}


	class RCKeyListener extends KeyAdapter
	{

		public void keyPressed(KeyEvent e) {
			
			JInternalFrame frame = JViewerApp.getInstance().getMainWindow();
			
			if ((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != KeyEvent.ALT_DOWN_MASK)
			{
				switch(e.getKeyCode()){
				case KeyEvent.VK_ADD:
				case KeyEvent.VK_PLUS:
				case KeyEvent.VK_EQUALS:
					if(Mousecaliberation.THRESHOLDFLAG)
						JViewerView.ACCEL_THRESHOLD++;
					else
					{
						BigDecimal rate=new BigDecimal(JViewerView.MOUSE_ACCELERATION);
						BigDecimal cost=new BigDecimal("1.0");
						BigDecimal stepped_value = rate.add(cost).setScale(2, BigDecimal.ROUND_HALF_UP);
						JViewerView.MOUSE_ACCELERATION=stepped_value.floatValue();
					}					
					e.consume();
					Mousecaliberation.resetCursor();//reset the cursor
					break;
				case KeyEvent.VK_SUBTRACT:
				case KeyEvent.VK_MINUS:

					if(Mousecaliberation.THRESHOLDFLAG)
					{
						JViewerView.ACCEL_THRESHOLD--;
						if (JViewerView.ACCEL_THRESHOLD <= 0)
						{
							JViewerView.ACCEL_THRESHOLD = 1;
							JOptionPane.showMessageDialog(frame, LocaleStrings.getString("3_9_MC"), 
									LocaleStrings.getString("3_5_MC"), JOptionPane.INFORMATION_MESSAGE);
						}
					}
					else
					{
						BigDecimal rate=new BigDecimal(JViewerView.MOUSE_ACCELERATION);
						BigDecimal cost=new BigDecimal("1.0");
						BigDecimal stepped_value = rate.subtract(cost).setScale(2, BigDecimal.ROUND_HALF_UP);
						JViewerView.MOUSE_ACCELERATION=stepped_value.floatValue();
						if (JViewerView.MOUSE_ACCELERATION <= 0)
						{
							JOptionPane.showMessageDialog(frame, LocaleStrings.getString("3_10_MC"),
									LocaleStrings.getString("3_5_MC"), JOptionPane.INFORMATION_MESSAGE);
							JViewerView.MOUSE_ACCELERATION = 1;
						}
					}					
					e.consume();
					Mousecaliberation.resetCursor();//reset the cursor
					break;
					
				}				
			

			}

			if ((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK)
			{
				if(!Mousecaliberation.THRESHOLDFLAG){					
					BigDecimal rate=new BigDecimal(JViewerView.MOUSE_ACCELERATION);
					BigDecimal cost=new BigDecimal("0.1");
					BigDecimal stepped_value;
					switch(e.getKeyCode()){
					case KeyEvent.VK_ADD:
					case KeyEvent.VK_PLUS:						
							stepped_value = rate.add(cost).setScale(2, BigDecimal.ROUND_HALF_UP);
							JViewerView.MOUSE_ACCELERATION=stepped_value.floatValue();							
							e.consume();
						break;
					case KeyEvent.VK_SUBTRACT:
					case KeyEvent.VK_MINUS:
						stepped_value = rate.subtract(cost).setScale(2, BigDecimal.ROUND_HALF_UP);

						JViewerView.MOUSE_ACCELERATION=stepped_value.floatValue();

						if (JViewerView.MOUSE_ACCELERATION < 0)
							JViewerView.MOUSE_ACCELERATION = 1;
						e.consume();
						
						break;
					}						

					Mousecaliberation.resetCursor();//reset the cursor					
					
				}

					if (JViewerApp.getInstance().isFullScreenMode() ||
							JViewerApp.getInstance().isFullKeyboardEnabled())
					{
						/*
						 * In full screen mode, top menu is not associated with
						 * the view and so the regular shortcuts will not work.
						 * So the following keyboard shortcuts are required only
						 * in full screen mode. All the follwoing keyboard
						 * shortcuts are combined with ALT Key
						 */
						if (e.getKeyCode() == KeyEvent.VK_T)
						{
							if (JViewerApp.getInstance().GetRedirectionState() == JViewerApp.REDIR_STARTED)
							{
							   if(!JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.CALIBRATEMOUSETHRESHOLD))
							    {
									JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.CALIBRATEMOUSETHRESHOLD,true);
									JViewerApp.getInstance().getMousecaliberation().OnCalibareteMouseThreshold(true);
									JViewerApp.getInstance().getRCView().requestFocus();
								}
								else
								{
									JViewerApp.getInstance().getMousecaliberation().OnCalibareteMouseThreshold(false);
									JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.CALIBRATEMOUSETHRESHOLD,false);
									JViewerApp.getInstance().getRCView().requestFocus();
								}
							}
						}
						e.consume();
					}					
				return;
			}
			return;
		}
	}
