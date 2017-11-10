/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2015, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
 ****************************************************************/
package com.ami.vmedia.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * This class constructs the status bar that will show the status messages in<br>
 * the VMedia Application.
 * @author deepakmn
 *
 */
public class VMStatusBar extends JPanel {
	private JLabel statusLabel = null;
	/**
	 * Constructor.
	 */
	public VMStatusBar(){
		Border panelbBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		setAutoscrolls(false);
		setVisible(true);
		setBorder(panelbBorder);
		setLayout(new BorderLayout());
		statusLabel = new JLabel("");
		add(statusLabel, BorderLayout.CENTER);
	}

	/**
	 * Sets the message to be displayed on the status bar.
	 * @param message -- the message to be displayed on the status bar.
	 */
	public void setStatusMessage(String message){
		statusLabel.setText(message);
		statusLabel.setForeground(Color.BLUE);
		statusLabel.setVisible(true);
	}
	
	/**
	 * Gets the message to be displayed on the status bar.
	 */
	public String getStatusMessage(){
		return statusLabel.getText();

	}
}
