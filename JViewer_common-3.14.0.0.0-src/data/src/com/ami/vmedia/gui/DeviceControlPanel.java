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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.ami.iusb.IUSBRedirSession;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.vmedia.VMApp;

/**
 * This class creates the panels which contains the list of devices and<br>
 * the connection buttons for each device instance in the VMedia application 
 * @author deepakmn
 *
 */
public class DeviceControlPanel extends JPanel{

	private final int INSTANCE_PANEL_WIDTH = 400;
	private final int INSTANCE_PANEL_HEIGHT = 90;
	private JButton connectButton;
	private JScrollPane instanceScrollPane;
	private DevicePanel devicePanel;
	private JPanel connectPanel;
	private JPanel instancePanel;
	private IUSBRedirSession iusbRedirSession;

	private String deviceLabelText;

	private JRadioButton physicalDrive;
	private JRadioButton logicalDrive;
	private ButtonGroup driveSelectionGroup;
	private int deviceIndex;
	private int deviceType;
	private String redirectionSource = null;
	private int devCount;
	private int freeDevCount;

	/**
	 * Constructor - initializes the members, and initiates the<br>
	 * construction of the GUI components. 
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param deviceIndex - the device instance number.
	 */
	public DeviceControlPanel(int deviceType, int deviceIndex){
		iusbRedirSession = VMApp.getInstance().getIUSBRedirSession();
		this.deviceType = deviceType;
		this.deviceIndex = deviceIndex;
		String[] driveList = VMApp.getInstance().getDeviceDriveList(deviceType);
		switch(deviceType){
		case VMApp.DEVICE_TYPE_CDROM:
			deviceLabelText = LocaleStrings.getString("AJ_1_DCP");
			devCount = VMApp.getInstance().getNumCD();
			freeDevCount = VMApp.getInstance().getFreeCDNum();
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			deviceLabelText = LocaleStrings.getString("AJ_3_DCP");
			devCount = VMApp.getInstance().getNumHD();
			freeDevCount = VMApp.getInstance().getFreeHDNum();
			break;
		}
		constructDeviceControlPanel(deviceIndex, deviceType, driveList);
	}

	/**
	 * Constructs the device control panel and the various components in it.
	 * @param instanceNum - the device instance number.
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param driveList - the list of physical device drives for the corresponding device
	 */
	private void constructDeviceControlPanel(int instanceNum, int deviceType, String[] driveList){
		GridBagConstraints gridCons = new GridBagConstraints();
		GridBagConstraints baseGridCons = new GridBagConstraints();
		TitledBorder panelTitle;
		Border panelBorder = BorderFactory.createEtchedBorder();
		//Connect button
		connectButton = createConnectButton(deviceType, instanceNum);
		devicePanel = new DevicePanel(driveList, deviceType, instanceNum);
		instanceScrollPane = new JScrollPane(devicePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		instanceScrollPane.setPreferredSize(new Dimension(INSTANCE_PANEL_WIDTH, INSTANCE_PANEL_HEIGHT));
		//  cdrom table panel
		instancePanel = new JPanel();
		instancePanel.setLayout(new GridBagLayout());
		gridCons.insets = new Insets(0, 10, 0, 10);
		gridCons.gridx = 0;
		gridCons.gridwidth = 1;
		gridCons.gridheight = 1;
		gridCons.weightx = 0.5;
		gridCons.gridy = 0;
		panelTitle = BorderFactory.createTitledBorder(panelBorder,
				deviceLabelText+VMApp.getInstance().getRomanNumber(instanceNum),
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.ABOVE_TOP
				);
		instancePanel.setBorder(panelTitle);
		gridCons.gridwidth = 1;
		gridCons.weightx = 0.7;
		gridCons.fill = GridBagConstraints.HORIZONTAL;
		instancePanel.add(createPhysicalDevPanel(deviceType), gridCons);
		if(deviceType == VMApp.DEVICE_TYPE_HD_USB){
			if(JViewer.IsClientAdmin() && System.getProperty("os.name").toLowerCase().contains("windows")){
				if(driveList != null){
					//If any of the entries in the drive list is corresponding to physical drives,
					//then make the Physical and Logical drive selection radio buttons visible.
					for(String drive : driveList){
						if(drive.startsWith(LocaleStrings.getString("A_5_DP"))){//physical drive
							if(getPhysicalDrive() != null)
								getPhysicalDrive().setVisible(true);
							if(getLogicalDrive() != null)
								getLogicalDrive().setVisible(true);
							break;
						}
					}
				}
				//If drive list is null, that means no physical devices are connected.
				//In this case, make the Physical and Logical drive selection radio buttons invisible.
				else{
					if(getPhysicalDrive() != null)
						getPhysicalDrive().setVisible(false);
					if(getLogicalDrive() != null)
						getLogicalDrive().setVisible(false);
				}
			}
		}
		gridCons.insets = new Insets(0, 10, 0, 10);
		gridCons.gridy = 1;
		instancePanel.add(instanceScrollPane, gridCons);

		connectPanel = new JPanel(new BorderLayout(0, 4));
		connectPanel.add(connectButton, BorderLayout.CENTER);

		gridCons.insets = new Insets(5, 0, 0, 10);
		gridCons.weightx = 0.3;
		gridCons.gridx = 1;
		instancePanel.add(connectPanel, gridCons);

		setLayout(new GridBagLayout());
		baseGridCons.insets = new Insets(10, 5, 5, 0);
		baseGridCons.gridx = 0;
		baseGridCons.gridwidth = GridBagConstraints.RELATIVE;
		baseGridCons.gridheight = 1;
		baseGridCons.weightx = 1.0;
		baseGridCons.gridy = 0;
		baseGridCons.anchor = GridBagConstraints.FIRST_LINE_START;
		baseGridCons.fill = GridBagConstraints.HORIZONTAL;
		add(instancePanel, baseGridCons);
	}

	/**
	 * Create the connection button for each device instance.
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param deviceIndex - the device instance number.
	 * @return
	 */
	private JButton createConnectButton(int deviceType, int deviceIndex){
		JButton button = new JButton(LocaleStrings.getString("AJ_4_DCP"));
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			button.setActionCommand(VMActionListener.CD_CONNECT_ACTION_CMD+deviceIndex);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			button.setActionCommand(VMActionListener.HD_CONNECT_ACTION_CMD+deviceIndex);
			break;
		default:
			break;
		}
		button.addActionListener(new VMActionListener());
		return button;
	}

	/**
	 * @return the instanceScrollPane
	 */
	public JScrollPane getInstanceScrollPane() {
		return instanceScrollPane;
	}

	/**
	 * @return the connectPanel
	 */
	public JPanel getConnectPanel() {
		return connectPanel;
	}

	/**
	 * @return the connectButton
	 */
	public JButton getConnectButton() {
		return connectButton;
	}

	/**
	 * @return the devicePanel
	 */
	public DevicePanel getDevicePanel() {
		return devicePanel;
	}

	/**
	 * @param devicePanel the devicePanel to set
	 */
	public void setDevicePanel(DevicePanel devicePanel) {
		this.devicePanel = devicePanel;
	}

	/**
	 * Update the state of the components on the device control panel<br>
	 * by enabling or disabling them based on the device redirection status. 
	 */
	public void updateDeviceControlPanel(){
		redirectionSource = iusbRedirSession.getDeviceRedirSource(deviceType, deviceIndex);
		//Disable the components on the device control panel
		//when  the device instance is redirected.
		if(iusbRedirSession.getDeviceRedirStatus(deviceType, deviceIndex) ==
				IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED){
			devicePanel.disableAll();
			connectButton.setText(LocaleStrings.getString("AJ_5_DCP"));
			devicePanel.selectRadioButton(redirectionSource);
			// only admin privileged user can perform physical device redirection.
			if((JViewer.IsClientAdmin()) && (deviceType==VMApp.DEVICE_TYPE_HD_USB) && (JViewerApp.getInstance().isWindowsClient())) 
			{	
				physicalDrive.setEnabled(false);
				logicalDrive.setEnabled(false);
			}
		}
		//Enable the components on the device control panel
		//when  the device instance is redirected.
		else if( iusbRedirSession.getDeviceStatus(deviceType, deviceIndex) ==
				IUSBRedirSession.DEVICE_FREE){
			devicePanel.enableAll();
			connectButton.setText(LocaleStrings.getString("AJ_4_DCP"));
			devicePanel.selectRadioButton(redirectionSource);
			// only admin privileged user can perform physical device redirection.
			if((JViewer.IsClientAdmin()) && (deviceType==VMApp.DEVICE_TYPE_HD_USB) && (JViewerApp.getInstance().isWindowsClient())) 
			{	
				physicalDrive.setEnabled(true);
				logicalDrive.setEnabled(true);
			}
		}
		if (!JViewer.isVMApp())
			VMApp.getVMPane().updateJVToolbar(deviceType);
		VMApp.getVMPane().physicalDriveChangeState(deviceType, deviceIndex, redirectionSource, false);
	}
	/**
	 * Creates the panel which hold the radio buttons to select whether the<br>
	 * Physical device drives or the logical device drives should be listed.
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @return the JPanel object.
	 */
	private JPanel createPhysicalDevPanel(int deviceType){
		JPanel physDevPanel;
		GridBagConstraints gridCons;
		RadioButtonListener radioButtonListener = new RadioButtonListener();
		physDevPanel = new JPanel();
		//topPanel.setBackground(DEV_PANEL_BG_COLOR);
		physDevPanel.setLayout(new GridBagLayout());
		gridCons = new GridBagConstraints();
		if(deviceType == VMApp.DEVICE_TYPE_HD_USB){
			// Add the Physical Drive and Logical Drive options only if the
			// user has administrator privilege.
			if(JViewer.IsClientAdmin() && System.getProperty("os.name").toLowerCase().contains("windows")){
				driveSelectionGroup = new ButtonGroup();
				physicalDrive = new JRadioButton(LocaleStrings.getString("A_5_DP"));
				physicalDrive.addActionListener(radioButtonListener);
				driveSelectionGroup.add(physicalDrive);
				physicalDrive.setSelected(true);
				physDevPanel.add(physicalDrive, gridCons);
				physicalDrive.setVisible(false);
				logicalDrive = new JRadioButton(LocaleStrings.getString("A_6_DP"));
				logicalDrive.addActionListener(radioButtonListener);
				driveSelectionGroup.add(logicalDrive);
				physDevPanel.add(logicalDrive, gridCons);
				logicalDrive.setVisible(false);
			}
		}
		return physDevPanel;
	}
	public JRadioButton getLogicalDrive() {
		return logicalDrive;
	}
	public JRadioButton getPhysicalDrive() {
		return physicalDrive;
	}
	/**
	 * The action listener class for the radio button click events.
	 * @author deepakmn
	 *
	 */
	class RadioButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			JRadioButton rBtn = (JRadioButton) e.getSource();
			if(rBtn.getText().startsWith(DevicePanel.PHYSICAL_DRIVE_WIN)){
				getDevicePanel().enableHardDiskDrives(DevicePanel.PHYSICAL_DRIVE_WIN);
			}
			else if(rBtn.getText().startsWith(DevicePanel.LOGICAL_DRIVE_WIN)){
				getDevicePanel().enableHardDiskDrives(DevicePanel.LOGICAL_DRIVE_WIN);
			}
			getDevicePanel().revalidate();
			getDevicePanel().repaint();
		}

	}
}
