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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.vmedia.VMApp;

/**
 * This class creates the frame for the VMedia application when it is
 * launched as a stand alone application. 
 * @author deepakmn
 *
 */
public class VMFrame extends JFrame {
	private static final int VMFRAME_WIDTH = 800;
	private static final int VMFRAME_HEIGHT = 700;
	private VMPane vmPane;
	private VMStatusBar statusPanel;
	/**
	 * Constructor.
	 */
	public VMFrame(){
		super(LocaleStrings.getString("G_1_VMD")+" - [ "+JViewer.getIp()+" ]");
		setSize(VMFRAME_WIDTH, VMFRAME_HEIGHT);
		VMApp.setWidth(VMFRAME_WIDTH);
		VMApp.setHeight(VMFRAME_HEIGHT);
		setLayout(new BorderLayout());
		vmPane = new VMPane();
		VMApp.setVMPane(vmPane);
		statusPanel = VMApp.getVMStatusPanel();
		add(vmPane, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.SOUTH);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeVMFrame();
			}
		});
		setIconImage(vmPane.getImageIcon("jviewer.jpg").getImage());
	}

	/**
	 * Close the VMFrame and the application.
	 */
	private void closeVMFrame(){
		if(VMApp.getInstance().isCDRedirRunning()||
				VMApp.getInstance().isHDRedireRunning()){
			UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
			int ret = JOptionPane.showConfirmDialog(JViewer.getMainFrame(), LocaleStrings.getString("C_7_JVF"),
					LocaleStrings.getString("C_8_JVF"), JOptionPane.YES_NO_OPTION);

			if( ret == JOptionPane.CLOSED_OPTION )
			{
				Debug.out.println("Returning CLOSED_OPTION\n");
				return;
			}
			if( ret == JOptionPane.NO_OPTION )
			{
				Debug.out.println("Returning NO_OPTION\n");
				return;
			}
		}
		VMApp.exit(0);
	}
}
