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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.ami.iusb.IUSBRedirSession;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.vmedia.VMApp;

/**
 * This class creates the panel which holds the status table for each<br>
 * device type.
 * @author deepakmn
 *
 */
public class StatusTabPanel extends JPanel {
	private StatusTable cdStatusTable;
	private StatusTable hdStatusTable;
	private JScrollPane vmCDScrollPane;
	private JScrollPane vmHDScrollPane;
	/**
	 * Constructor.
	 */
	public StatusTabPanel() {
		super(new GridBagLayout());
		GridBagConstraints gCons = new GridBagConstraints();
		gCons.insets = new Insets(2, 0, 2, 0);
		gCons.gridx = 0;
		gCons.fill = GridBagConstraints.BOTH;
		gCons.ipady = 400;
		gCons.gridy = 0;
		gCons.gridwidth = 1;
		gCons.gridheight = 1;
		gCons.weightx = 1.0;
		gCons.weighty = 1.0;
		setVMCDScrollPane(createStatusScrollPane(VMApp.DEVICE_TYPE_CDROM));
		add(getVMCDScrollPane(), gCons);
		gCons.gridy = 1;
		setVMHDScrollPane(createStatusScrollPane(VMApp.DEVICE_TYPE_HD_USB));
		add(getVMHDScrollPane(), gCons);
		setVisible(true);
	}

	/**
	 * Creates the scroll pane which will contain the status table<br>
	 * for each device type.
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @return the JVScrollPane object.
	 */
	public JScrollPane createStatusScrollPane(int deviceType){
		JScrollPane vmStatusScrollPane;
		StatusTable vmStatusTable;
		Border paneBorder = BorderFactory.createEtchedBorder();
		TitledBorder panelTitle = null;
		String titleSting = "";
		vmStatusTable = new StatusTable(deviceType);
		vmStatusScrollPane = new JScrollPane(vmStatusTable.getStatusTable(),
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		switch(deviceType){
		case VMApp.DEVICE_TYPE_CDROM:
			setCDStatusTable(vmStatusTable);
			titleSting = LocaleStrings.getString("AK_3_VMP")+" "+LocaleStrings.getString("AK_2_VMP");
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			setHDStatusTable(vmStatusTable);
			titleSting = LocaleStrings.getString("AK_5_VMP")+" "+LocaleStrings.getString("AK_2_VMP");
			break;
		}
		panelTitle = BorderFactory.createTitledBorder(paneBorder,
				titleSting,
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.ABOVE_TOP
				);
		vmStatusScrollPane.setBorder(panelTitle);
		return vmStatusScrollPane;
	}

	/**
	 * Updates the data in the Status table fields 
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param deviceIndex - the device instance number.
	 * @param isConnected - true if the device is redirected, false otherwise.
	 */
	public void updateStatusTable(int deviceType, int deviceIndex, boolean isConnected){
		IUSBRedirSession iusbRedirSession = VMApp.getInstance().getIUSBRedirSession();
		JTable statusTable = null;
		String targetDevice = LocaleStrings.getString("AL_8_ST");
		String source = LocaleStrings.getString("AL_8_ST");
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			statusTable = getCDStatusTable().getStatusTable();
			if(isConnected)
				targetDevice = LocaleStrings.getString("AL_5_ST")+" : "+
						iusbRedirSession.getCDInstanceNumber(deviceIndex);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			statusTable = getHDStatusTable().getStatusTable();
			if(isConnected)
				targetDevice = LocaleStrings.getString("AL_7_ST")+" : "+
						iusbRedirSession.getHDInstanceNumber(deviceIndex);
			break;
		}
		if(isConnected){
			source = iusbRedirSession.getDeviceRedirSource(deviceType, deviceIndex);
		}else {
			source = LocaleStrings.getString("AL_8_ST");
			targetDevice = LocaleStrings.getString("AL_8_ST");
		}
		statusTable.setValueAt(targetDevice, deviceIndex, 1);
		statusTable.setValueAt(source, deviceIndex, 2);
	}

	/**
	 * @return the cdStatusTable
	 */
	public StatusTable getCDStatusTable() {
		return cdStatusTable;
	}

	/**
	 * @param cdStatusTable the cdStatusTable to set
	 */
	public void setCDStatusTable(StatusTable cdStatusTable) {
		this.cdStatusTable = cdStatusTable;
	}

	/**
	 * @return the hdStatusTable
	 */
	public StatusTable getHDStatusTable() {
		return hdStatusTable;
	}

	/**
	 * @param hdStatusTable the hdStatusTable to set
	 */
	public void setHDStatusTable(StatusTable hdStatusTable) {
		this.hdStatusTable = hdStatusTable;
	}

	/**
	 * @return the vmCDScrollPane
	 */
	public JScrollPane getVMCDScrollPane() {
		return vmCDScrollPane;
	}

	/**
	 * @param vmCDScrollPane the vmCDScrollPane to set
	 */
	public void setVMCDScrollPane(JScrollPane vmCDScrollPane) {
		this.vmCDScrollPane = vmCDScrollPane;
	}

	/**
	 * @return the vmHDScrollPane
	 */
	public JScrollPane getVMHDScrollPane() {
		return vmHDScrollPane;
	}

	/**
	 * @param vmHDScrollPane the vmHDScrollPane to set
	 */
	public void setVMHDScrollPane(JScrollPane vmHDScrollPane) {
		this.vmHDScrollPane = vmHDScrollPane;
	}
	/**
	 * Gets the status scroll pane corresponding to a given device type. 
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @return the JScrollPane object.
	 */
	public JScrollPane getDeviceScrollPane(int deviceType){
		JScrollPane devScrollPane = null;
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			devScrollPane = getVMCDScrollPane();
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			devScrollPane = getVMHDScrollPane();
			break;
		}
		return devScrollPane;
	}
}
