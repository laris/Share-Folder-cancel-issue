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
 // Auto detect bandwidth dialog module.
 // This module implements the wait dialog for bandwidth auto detection process.
 //

 package com.ami.kvm.jviewer.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ami.kvm.jviewer.Debug;

 /**
  * AutoBWDlg dialog class
  */
 public class AutoBWDlg extends JDialog {

	private static final long serialVersionUID = 1L;
	JLabel m_msg;
	public final static int WIDTH = 300;
	public final static int HEIGHT = 100;

	 /**
	  * The constructor
	  *
	  * @param frame parent window frame
	  */
	 public AutoBWDlg(JFrame frame) {

		 super(frame, LocaleStrings.getString("9_1_BW")+"...", false);

		 m_msg = new JLabel(LocaleStrings.getString("9_1_BW")+"."+LocaleStrings.getString("9_2_BW"));
		 JPanel msgPanel = new JPanel();
		 msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.X_AXIS));
		 msgPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		 msgPanel.add(m_msg);

		 Container c = getContentPane();
		 c.setLayout(new GridLayout(1, 1));
		 c.add(msgPanel);

		 setSize(WIDTH, HEIGHT);
		 setLocation(JViewerApp.getInstance().getPopUpWindowPosition(WIDTH,HEIGHT));
		 setLocationRelativeTo( null );
	 }

	 /**
	  * Set message
	  *
	  * @param msg new message.
	  */
	 public void setMessage(String msg) {

		 m_msg.setText(msg);
	 }

	 /**
	  * Close the dialog after a brief delay
	  */
	 public void done() {
		 try
		 {
			 Thread.sleep(1000);
			 m_msg.setText(LocaleStrings.getString("9_3_BW")+".");
			 Thread.sleep(1000);
			 m_msg.setText(LocaleStrings.getString("9_4_BW")+".");
			 Thread.sleep(200);
		 }
		 catch(Exception e) {
			 Debug.out.println(e);
		 }
		 dispose();
	 }
 }
