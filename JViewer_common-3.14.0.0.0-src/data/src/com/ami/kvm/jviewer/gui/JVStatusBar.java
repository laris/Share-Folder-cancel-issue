////////////////////////////////////////////////////////////////////////////////
//
// JViewer status bar component module.
//

package com.ami.kvm.jviewer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import com.ami.kvm.jviewer.hid.USBKeyboardRep;
import com.ami.kvm.jviewer.hid.USBMouseRep;
import com.ami.kvm.jviewer.kvmpkts.KVMClient;

/**
 * JViewer status bar component class.
 */
public class JVStatusBar {
	private JPanel statusBar;
	private JLabel statusMsg;
	private JToggleButton numLockLED;
	private JToggleButton capsLockLED;
	private JToggleButton scrollLockLED;
	
	private JToggleButton leftAlt;
	private JToggleButton rightAlt;
	private JToggleButton leftCtrl;
	private JToggleButton rightCtrl;
	
	
	private static final String INIT_STATUS_MSG_STRING = LocaleStrings.getString("N_1_JVS");
	private static final String RESET_STATUS_MSG_STRING = "";
	public static String OTHER_MOUISE_MODE_MSG = "";
	public static final int STATUSBAR_HEIGHT = 25;
	private JPanel statusMsgPanel;
	public JPanel iconPanel;

	/**
	 * The constructor.
	 */
	public JVStatusBar() {
		statusMsgPanel = new JPanel();
		statusMsg = new JLabel();

		initStatusMessage();
		statusMsgPanel.add(statusMsg);

		leftAlt = new JToggleButton("LALT", null, false);
		rightAlt = new JToggleButton("RALT", null, false);
		leftCtrl = new JToggleButton("LCTRL", null, false);
		rightCtrl = new JToggleButton("RCTRL", null, false);
		iconPanel = new JPanel();
		numLockLED = new JToggleButton("Num", null, false);
		capsLockLED = new JToggleButton("Caps", null, false);
		scrollLockLED = new JToggleButton("Scroll", null, false);
		initKeyboardLED();
		iconPanel.add(leftAlt);
		iconPanel.add(leftCtrl);
		iconPanel.add(rightAlt);
		iconPanel.add(rightCtrl);
		iconPanel.add(numLockLED);
		iconPanel.add(capsLockLED);
		iconPanel.add(scrollLockLED);
		statusBar = new JPanel();
		statusBar.setSize(0, STATUSBAR_HEIGHT);
		statusBar.setLayout(new BorderLayout());
		statusBar.add(statusMsgPanel,BorderLayout.WEST);
		statusBar.add(Box.createHorizontalGlue());
		statusBar.add(iconPanel, BorderLayout.EAST);

	}

	private void initStatusMessage() {
		statusMsg.setText(INIT_STATUS_MSG_STRING);
		statusMsg.setFocusTraversalKeysEnabled(false);
	}

	public void setStatus(String msg) {
		statusMsg.setText(msg);
		statusMsg.setForeground(Color.BLACK);
	}

	public String getStatus() {
		return statusMsg.getText();
	}

	public void resetStatus() {
		statusMsg.setText(RESET_STATUS_MSG_STRING);
		if(JViewerApp.getInstance().getRCView().GetUSBMouseMode() == USBMouseRep.OTHER_MOUSE_MODE){
			statusMsg.setText(OTHER_MOUISE_MODE_MSG);
			statusMsg.setForeground(Color.BLUE);
		}
		else if(JViewerApp.getInstance().getZoomOption() == JVMenu.FIT_TO_HOST_RES&&
				!JViewerApp.getInstance().getJVMenu().getMenuEnable(JVMenu.FIT_TO_HOST_RES)){
			statusMsg.setText(LocaleStrings.getString("N_2_JVS"));
			statusMsg.setForeground(Color.BLUE);
		}
	}
	
	private void initKeyboardLED() {
		numLockLED.setFont(new Font("Aharoni", 1, 12));
		numLockLED.setForeground(Color.gray);
		numLockLED.setRequestFocusEnabled(false);
		numLockLED.setHorizontalTextPosition(SwingConstants.CENTER);
		numLockLED.setHorizontalAlignment(SwingConstants.CENTER);
		numLockLED.setMaximumSize(new Dimension(40, 15));
		numLockLED.setMinimumSize(new Dimension(40, 15));
		numLockLED.setCursor(new Cursor(Cursor.HAND_CURSOR));
		numLockLED.setFocusTraversalKeysEnabled(false);
		numLockLED.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		numLockLED.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				sendLEDStatusFromStatusBar(KeyEvent.VK_NUM_LOCK, KeyEvent.KEY_LOCATION_NUMPAD, true);
			}

			public void mouseReleased(MouseEvent evt) {
				sendLEDStatusFromStatusBar(KeyEvent.VK_NUM_LOCK, KeyEvent.KEY_LOCATION_NUMPAD, false);
			}
		});

		capsLockLED.setFont(new Font("Aharoni", 1, 12));
		capsLockLED.setForeground(Color.gray);
		capsLockLED.setRequestFocusEnabled(false);
		capsLockLED.setHorizontalTextPosition(SwingConstants.CENTER);
		capsLockLED.setHorizontalAlignment(SwingConstants.CENTER);
		capsLockLED.setMaximumSize(new Dimension(40, 15));
		capsLockLED.setMinimumSize(new Dimension(40, 15));
		capsLockLED.setCursor(new Cursor(Cursor.HAND_CURSOR));
		capsLockLED.setFocusTraversalKeysEnabled(false);
		capsLockLED.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		capsLockLED.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				sendLEDStatusFromStatusBar(KeyEvent.VK_CAPS_LOCK, KeyEvent.KEY_LOCATION_STANDARD, true);
			}

			public void mouseReleased(MouseEvent evt) {
				sendLEDStatusFromStatusBar(KeyEvent.VK_CAPS_LOCK, KeyEvent.KEY_LOCATION_STANDARD, false);
			}
		});

		scrollLockLED.setFont(new Font("Aharoni", 1, 12));
		scrollLockLED.setForeground(Color.gray);
		scrollLockLED.setRequestFocusEnabled(false);
		scrollLockLED.setHorizontalTextPosition(SwingConstants.CENTER);
		scrollLockLED.setHorizontalAlignment(SwingConstants.CENTER);
		scrollLockLED.setMaximumSize(new Dimension(40, 15));
		scrollLockLED.setMinimumSize(new Dimension(40, 15));
		scrollLockLED.setCursor(new Cursor(Cursor.HAND_CURSOR));
		scrollLockLED.setFocusTraversalKeysEnabled(false);
		scrollLockLED.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		scrollLockLED.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				sendLEDStatusFromStatusBar(KeyEvent.VK_SCROLL_LOCK, KeyEvent.KEY_LOCATION_STANDARD, true);
			}

			public void mouseReleased(MouseEvent evt) {
				sendLEDStatusFromStatusBar(KeyEvent.VK_SCROLL_LOCK, KeyEvent.KEY_LOCATION_STANDARD, false);
			}
		});
		
		leftAlt.setFont(new Font("Aharoni", 1, 12));
		leftAlt.setForeground(Color.gray);
		leftAlt.setRequestFocusEnabled(false);
		leftAlt.setHorizontalTextPosition(SwingConstants.CENTER);
		leftAlt.setHorizontalAlignment(SwingConstants.CENTER);
		leftAlt.setMaximumSize(new Dimension(40, 15));
		leftAlt.setMinimumSize(new Dimension(40, 15));
		leftAlt.setCursor(new Cursor(Cursor.HAND_CURSOR));
		leftAlt.setFocusTraversalKeysEnabled(false);
		leftAlt.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		
		leftAlt.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(leftAlt.isSelected())
					JViewerApp.getInstance().OnKeyboardHoldLeftAltKey(true);
				else
					JViewerApp.getInstance().OnKeyboardHoldLeftAltKey(false);
			}
		});

		
		rightAlt.setFont(new Font("Aharoni", 1, 12));
		rightAlt.setForeground(Color.gray);
		rightAlt.setRequestFocusEnabled(false);
		rightAlt.setHorizontalTextPosition(SwingConstants.CENTER);
		rightAlt.setHorizontalAlignment(SwingConstants.CENTER);
		rightAlt.setMaximumSize(new Dimension(40, 15));
		rightAlt.setMinimumSize(new Dimension(40, 15));
		rightAlt.setCursor(new Cursor(Cursor.HAND_CURSOR));
		rightAlt.setFocusTraversalKeysEnabled(false);
		rightAlt.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		rightAlt.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(rightAlt.isSelected())
					JViewerApp.getInstance().OnKeyboardHoldRightAltKey(true);
				else
					JViewerApp.getInstance().OnKeyboardHoldRightAltKey(false);
			}
		});
		
		
		leftCtrl.setFont(new Font("Aharoni", 1, 12));
		leftCtrl.setForeground(Color.gray);
		leftCtrl.setRequestFocusEnabled(false);
		leftCtrl.setHorizontalTextPosition(SwingConstants.CENTER);
		leftCtrl.setHorizontalAlignment(SwingConstants.CENTER);
		leftCtrl.setMaximumSize(new Dimension(40, 15));
		leftCtrl.setMinimumSize(new Dimension(40, 15));
		leftCtrl.setCursor(new Cursor(Cursor.HAND_CURSOR));
		leftCtrl.setFocusTraversalKeysEnabled(false);
		leftCtrl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		leftCtrl.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(leftCtrl.isSelected())
					JViewerApp.getInstance().OnKeyboardHoldLeftCtrlKey(true);
				else
					JViewerApp.getInstance().OnKeyboardHoldLeftCtrlKey(false);
			}
		});
		
		
		rightCtrl.setFont(new Font("Aharoni", 1, 12));
		rightCtrl.setForeground(Color.gray);
		rightCtrl.setRequestFocusEnabled(false);
		rightCtrl.setHorizontalTextPosition(SwingConstants.CENTER);
		rightCtrl.setHorizontalAlignment(SwingConstants.CENTER);
		rightCtrl.setMaximumSize(new Dimension(40, 15));
		rightCtrl.setMinimumSize(new Dimension(40, 15));
		rightCtrl.setCursor(new Cursor(Cursor.HAND_CURSOR));
		rightCtrl.setFocusTraversalKeysEnabled(false);
		rightCtrl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
		rightCtrl.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(rightCtrl.isSelected())
					JViewerApp.getInstance().OnKeyboardHoldRightCtrlKey(true);
				else
					JViewerApp.getInstance().OnKeyboardHoldRightCtrlKey(false);
			}
		});
	}
	
	private void sendLEDStatusFromStatusBar(int keyCode, int location, boolean keyPress ) {
		KVMClient kvmClnt = JViewerApp.getInstance().getKVMClient();
		USBKeyboardRep keyRep = new USBKeyboardRep();
		keyRep.set(keyCode, location, keyPress);
		kvmClnt.sendKMMessage(keyRep);
	}
	
	private void setLEDStatus(JToggleButton led, boolean status) {
		if (status == true) {
			led.setForeground(Color.red);
		} else {
			led.setForeground(Color.gray);
		}
	}

	public void setKeyboardLEDStatus(byte status) {
		// Num Lock
		if ((status & 0x01) != 0x00) {
			setLEDStatus(numLockLED, true);
		} else {
			setLEDStatus(numLockLED, false);
		}

		// Caps Lock
		if ((status & 0x02) != 0x00) {
			setLEDStatus(capsLockLED, true);
		} else {
			setLEDStatus(capsLockLED, false);
		}
		
		// Scroll Lock
		if ((status & 0x04) != 0x00) {
			setLEDStatus(scrollLockLED, true);
		} else {
			setLEDStatus(scrollLockLED, false);
		}
	}

    /**
     * Get the status bar.
     *
     * @return the status bar.
     */
	public JPanel getStatusBar() {
		return statusBar;
	}
	
	public JToggleButton getLeftAlt() {
		return leftAlt;
	}
	
	public JToggleButton getRightAlt() {
		return rightAlt;
	}
	
	public JToggleButton getLeftCtrl() {
		return leftCtrl;
	}
	
	public JToggleButton getRightCtrl() {
		return rightCtrl;
	}
	public void enableStatusBar(boolean status){
		leftAlt.setEnabled(status);
		leftCtrl.setEnabled(status);
		rightAlt.setEnabled(status);
		rightCtrl.setEnabled(status);
		numLockLED.setEnabled(status);
		capsLockLED.setEnabled(status);
		scrollLockLED.setEnabled(status);
	}
}
