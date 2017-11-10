/****************************************************************
 **                                                            **
 **    (C) Copyright 2012-2015, American Megatrends Inc.       **
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
// This module implements the full screen LED status bar for the JViewer.
//

package com.ami.kvm.jviewer.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import com.ami.kvm.jviewer.hid.USBKeyboardRep;
import com.ami.kvm.jviewer.kvmpkts.KVMClient;

public class FSLEDStatusBar extends JDialog {
	private static final byte NUM_LOCK = 0x01;
	private static final byte CAPS_LOCK = 0x02;
	private static final byte SCROLL_LOCK = 0x04;
	private static final String NUM = "NUM";
	private static final String CAPS = "CAPS";
	private static final String SCROLL = "SCROLL";
	private boolean autoHide = false;
	private JLabel numLockLED;
	private JLabel capsLockLED;
	private JLabel scrollLockLED;
	private Color bgColor = null;
	public static final int WIDTH = 160;
	public	 static final int HEIGHT = 20;

	/**
	 * FSLEDStatusBar constructor
	 * @param owner - owner component.
	 */
	public FSLEDStatusBar(JDialog owner){
		super(owner);
		bgColor = new Color(250, 250, 250, 0);
		numLockLED = createLEDLabel(NUM);
		capsLockLED = createLEDLabel(CAPS);
		scrollLockLED = createLEDLabel(SCROLL);
		setLayout(new GridLayout(1, 3));
		add(numLockLED);
		add(capsLockLED);
		add(scrollLockLED);
		setFocusable(false);
		setFocusableWindowState(false);
		setResizable(false);
		setUndecorated(true);
		setVisible(false);
	}

	/**
	 * Creates the label for each lock LED
	 * @param text - The text to be displayed on the LED label
	 * @return - LED label object.
	 */
	private JLabel createLEDLabel(String text){
		JLabel label = new JLabel(text);
		label.setName(text);
		label.setFont(new Font("Dialog", Font.BOLD, 10));
		label.setForeground(Color.gray);
		label.setBackground(bgColor);
		label.setRequestFocusEnabled(false);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.setFocusTraversalKeysEnabled(false);
		label.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		label.addMouseListener(new FSToolBarMouseListener());
		return label;
	}

	/**
	 * Sends the key code corresponding to the specific lock to the host.
	 * @param keyCode - key code send to the host.
	 * @param location - key location send to the host.
	 * @param keyPress - true if key press, false if key release
	 */
	private void sendLEDStatusFromStatusBar(int keyCode, int location, boolean keyPress ) {
		KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
		USBKeyboardRep keyRep = new USBKeyboardRep();
		keyRep.set(keyCode, location, keyPress);
		kvmClnt.sendKMMessage(keyRep);
	}

	/**
	 * Sets the status of the LED 
	 * @param led - label for which status needs to be set.
	 * @param status - label for which status needs to be set.
	 */
	private void setLEDStatus(JLabel led, boolean status) {
		if (status == true) {
			led.setForeground(Color.red);
		} else {
			led.setForeground(Color.gray);
		}
		led.repaint();
	}

	/**
	 * Sets the LED status.
	 * @param status
	 */
	public void setLEDStatus(byte status) {
		// Num Lock
		if ((status & NUM_LOCK) != 0x00) {
			setLEDStatus(numLockLED, true);
		} else {
			setLEDStatus(numLockLED, false);
		}

		// Caps Lock
		if ((status & CAPS_LOCK) != 0x00) {
			setLEDStatus(capsLockLED, true);
		} else {
			setLEDStatus(capsLockLED, false);
		}

		// Scroll Lock
		if ((status & SCROLL_LOCK) != 0x00) {
			setLEDStatus(scrollLockLED, true);
		} else {
			setLEDStatus(scrollLockLED, false);
		}
	}

	/**
	 * Gets auto hide property value.
	 * @return the autoHide
	 */
	public boolean isAutoHide() {
		return autoHide;
	}

	/**
	 * Sets auto hide property for FSLEDStatusBar
	 * @param autoHide the autoHide to set
	 */
	public void setAutoHide(boolean autoHide) {
		this.autoHide = autoHide;
	}

	/**
	 *MouseListener for FSLEDStatusbar
	 */
	class FSToolBarMouseListener extends MouseAdapter{
		JLabel ledLabel;
		String labelName;
		int keyCode;
		int keyLocation;
		/**
		 * mouse released event handler
		 */
		public void mousePressed(MouseEvent evt) {
			ledLabel = (JLabel) evt.getSource();
			labelName = ledLabel.getName();
			if(labelName == NUM){
				keyCode = KeyEvent.VK_NUM_LOCK;
				keyLocation = KeyEvent.KEY_LOCATION_NUMPAD;
			}
			else if(labelName == CAPS){
				keyCode = KeyEvent.VK_CAPS_LOCK;
				keyLocation = KeyEvent.KEY_LOCATION_STANDARD;
			}
			else if(labelName == SCROLL){
				keyCode = KeyEvent.VK_SCROLL_LOCK;
				keyLocation = KeyEvent.KEY_LOCATION_STANDARD;
			}
			sendLEDStatusFromStatusBar(keyCode, keyLocation, true);
			JViewerApp.getInstance().getRCView().requestFocus();
		}
		/**
		 * mouse pressed event handler
		 */
		public void mouseReleased(MouseEvent evt) {
			ledLabel = (JLabel) evt.getSource();
			labelName = ledLabel.getName();
			if(labelName == NUM){
				keyCode = KeyEvent.VK_NUM_LOCK;
				keyLocation = KeyEvent.KEY_LOCATION_NUMPAD;
			}
			else if(labelName == CAPS){
				keyCode = KeyEvent.VK_CAPS_LOCK;
				keyLocation = KeyEvent.KEY_LOCATION_STANDARD;
			}
			else if(labelName == SCROLL){
				keyCode = KeyEvent.VK_SCROLL_LOCK;
				keyLocation = KeyEvent.KEY_LOCATION_STANDARD;
			}
			sendLEDStatusFromStatusBar(keyCode, keyLocation, false);
			JViewerApp.getInstance().getRCView().requestFocus();
		}
		/**
		 * Mouse entered event handler.
		 */
		public void mouseEntered(MouseEvent e)
		{
			if(autoHide)
				JViewerApp.getInstance().getM_fsFrame().getM_menuBar().showMenu();
		}

		/**
		 * Mouse exit event handler.
		 */
		public void mouseExited(MouseEvent e)
		{
			if(autoHide)
				JViewerApp.getInstance().getM_fsFrame().getM_menuBar().hideMenu();
		}
	}

}
