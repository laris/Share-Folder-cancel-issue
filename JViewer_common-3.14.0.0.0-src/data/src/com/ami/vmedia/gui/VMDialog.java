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

import javax.swing.JDialog;

import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.vmedia.VMApp;

/**
 * This class renders the VMedia application as a child dialog when it is
 * launched from JViewer.
 * @author deepakmn
 *
 */
public class VMDialog extends JDialog {
	private static final int VMDIALOG_WIDTH = 700;
	private static final int VMDIALOG_HEIGHT = 500;
	private VMPane vmPane;
	private VMStatusBar statusPanel;

	/**
	 * Constructor.
	 */
	public VMDialog(){
		super(JViewer.getMainFrame(), LocaleStrings.getString("G_1_VMD"));
		setSize(VMDIALOG_WIDTH, VMDIALOG_HEIGHT);
		VMApp.setWidth(VMDIALOG_WIDTH);
		VMApp.setHeight(VMDIALOG_HEIGHT);
		setLocation(JViewerApp.getInstance().getPopUpWindowPosition(VMDIALOG_WIDTH, VMDIALOG_HEIGHT));
		setModal(false);
		vmPane = new VMPane();
		VMApp.setVMPane(vmPane);
		statusPanel = VMApp.getVMStatusPanel();
		add(vmPane, BorderLayout.CENTER);
		add(statusPanel, BorderLayout.SOUTH);
		setResizable(false);
		showDialog(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				showDialog(false);
			}
		});
	}

	/**
	 * Closes the VMedia dialog and disposes the dialog object.
	 */
	public void disposeVMDialog(){
		VMApp.getUpdateBytesTimer().cancel();
		VMApp.stopDeviceDetector();
		VMApp.stopRedirectionStatusMonitor();
		setVisible(false);
		this.dispose();
		VMApp.setVMDialog(null);
		JViewerApp.getInstance().setVMDialog(null);
	}
	/**
	 * Shows/Hides the VMedia dialog.
	 * @param status - the status whether the dialog should be shown or not.<br>
	 * 					true - to show the dialog.<br>
	 * 					false - to hide the dialog.
	 */
	public void showDialog(boolean status){
		if(status){
			vmPane.setSelectedTab(JViewerApp.getInstance().getSelectedVMTab());
		}
		setVisible(status);
	}
	/**
	 * Reinitializes the dialog components.
	 */
	public void reInitialize(){
		vmPane.initializeVMPane();
		vmPane.revalidate();
		vmPane.repaint();
		repaint();
	}
}
