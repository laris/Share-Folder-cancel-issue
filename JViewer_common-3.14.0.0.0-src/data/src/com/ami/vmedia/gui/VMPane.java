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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ami.iusb.IUSBRedirSession;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.vmedia.VMApp;
/**
 * This class creates the panel that holds all the components in the VMApp
 * @author deepakmn
 *
 */
public class VMPane extends JPanel{

	public static final String CD_DEV_CTRL_PANEL_KEY = "CD_DEV_CTRL_PANEL_";
	public static final String HD_DEV_CTRL_PANEL_KEY = "HD_DEV_CTRL_PANEL_";

	private StatusTable cdStatusTable;
	private StatusTable hdStatusTable;
	private StatusTabPanel statusTabPanel;
	private JTabbedPane vmDevicePane;
	private int numCD;
	private int numHD;
	private HashMap<String, Object> deviceControlPanelMap;

	/**
	 * Constructor.
	 */
	public VMPane(){
		initializeVMPane();
	}
	/**
	 * Initializes all the members and constructs all the components of the panel.
	 */
	public void initializeVMPane(){
		deviceControlPanelMap = new HashMap<String, Object>();
		VMApp.getInstance().createIUSBRedirectionSession();
		numCD = VMApp.getInstance().getNumCD();
		numHD = VMApp.getInstance().getNumHD();
		setSize(VMApp.getWidth(), VMApp.getHeight());
		constructUI();
		setVisible(true);
		VMApp.getInstance().initDeviceDetector();
		VMApp.getInstance().initRedirectionStatusMonitor();
	}
	/**
	 * Contructs the UI of the Vmedia application.
	 */
	public void constructUI(){
		if(getComponentCount() > 0)
			removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints gridCons;
		gridCons = new GridBagConstraints();
		gridCons.fill = GridBagConstraints.BOTH;
		gridCons.weightx = 1.0;
		gridCons.weighty = 1.0;
		createDevicePane();
		add(vmDevicePane, gridCons);
	}

	/**
	 * Creates the scroll pane which holds the device control panels for aech device type.
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @return the JScrollPane object.
	 */
	private JScrollPane createControlScrollPane(int deviceType){
		int devCount = 0;
		String devPanelKey= "";
		JScrollPane vmControlScrollPane;
		JPanel controlPanel = new JPanel(new GridBagLayout());
		DeviceControlPanel deviceControlPanel = null;
		GridBagConstraints gridCons = new GridBagConstraints();
		Border paneBorder = BorderFactory.createLoweredBevelBorder();
		TitledBorder panelTitle = BorderFactory.createTitledBorder(paneBorder,
				LocaleStrings.getString("AK_1_VMP"),
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.ABOVE_TOP
				);
		gridCons.fill = GridBagConstraints.HORIZONTAL;
		gridCons.insets = new Insets(10, 10, 5, 10);
		gridCons.gridx = 0;
		gridCons.gridwidth = 1;
		gridCons.gridheight = 1;
		gridCons.weightx = 1.0;
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			devCount = numCD;
			devPanelKey = CD_DEV_CTRL_PANEL_KEY;
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			devCount = numHD;
			devPanelKey = HD_DEV_CTRL_PANEL_KEY;
			break;
		default:
			devCount = 0;
			break;
		}
		for(int index = 0; index < devCount; index++){
			String panelKey = devPanelKey;
			gridCons.gridy = index;
			panelKey += index;
			deviceControlPanel = new DeviceControlPanel(deviceType, index);
			deviceControlPanelMap.put(panelKey, deviceControlPanel);

			controlPanel.add(deviceControlPanel, gridCons);
		}
		vmControlScrollPane = new JScrollPane(controlPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		vmControlScrollPane.setMaximumSize(new Dimension(VMApp.getWidth(), 100));
		vmControlScrollPane.setBorder(panelTitle);
		return vmControlScrollPane;
	}
	/**
	 * Constructs the tab panes corresponding to each device. 
	 */
	private void createDevicePane(){
		vmDevicePane = new JTabbedPane();
		statusTabPanel = new StatusTabPanel();
		vmDevicePane.addTab(formatTabTitle(LocaleStrings.getString("AK_3_VMP")), getImageIcon("DVD_tab.png"),
				createVMDeviceTabPanel(VMApp.DEVICE_TYPE_CDROM));
		vmDevicePane.addTab(formatTabTitle(LocaleStrings.getString("AK_5_VMP")), getImageIcon("HD_tab.png"),
				createVMDeviceTabPanel(VMApp.DEVICE_TYPE_HD_USB));
		vmDevicePane.addTab(formatTabTitle(LocaleStrings.getString("AK_6_VMP")), getImageIcon("status.png"),
				statusTabPanel);
		// update the tab panes initially
		updateDeviceControls(VMApp.DEVICE_TYPE_CDROM, JViewer.isCdServiceEnabled());
		updateDeviceControls(VMApp.DEVICE_TYPE_HD_USB, JViewer.isHdServiceEnabled());

		vmDevicePane.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				JViewerApp.getInstance().setSelectedVMTab(vmDevicePane.getSelectedIndex());
				// update message for the selected tab
				showMessageForSelectedTab(vmDevicePane.getSelectedIndex());

			}
		});
		if(!JViewer.isVMApp())
			vmDevicePane.setSelectedIndex(JViewerApp.getInstance().getSelectedVMTab());
	}

	/**
	 * Forms the tab title.
	 * @param title - the title string to set.
	 * @return the formatted title string
	 */
	private String formatTabTitle(String title){
		String formattedTitle = "<html><body leftmargin=15 topmargin=8 marginwidth=10 marginheight=6>" +
				title+"</body></html>";
		return formattedTitle;
	}

	/**
	 * gets the image icon from the resource files.
	 * @param iconName file name of the image icon
	 * @return the ImageIcon object.
	 */
	public ImageIcon getImageIcon(String iconName) {
		URL imageUrl = null;
		ImageIcon imageIcon = null;

		try {
			imageUrl = com.ami.kvm.jviewer.JViewer.class.getResource("res/" + iconName);
		} catch (Exception e) {
			Debug.out.println(e);
		}

		if (imageUrl != null) {
			try {
				imageIcon = new ImageIcon(imageUrl);
			} catch (Exception e) {
				Debug.out.println(e);
			}
		}
		return imageIcon;
	}
	/**
	 * Creates the panel which holds the components inside each device tabs 
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_FLOPPY - for Floppy devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @return the JPanel object
	 */
	private JPanel createVMDeviceTabPanel(int deviceType){
		GridBagConstraints gCons = new GridBagConstraints();
		JPanel vmDeviceTabPanel = new JPanel(new GridBagLayout());
		JScrollPane vmStatusScrollPane;
		gCons.insets = new Insets(0, 0, 2, 0);
		gCons.gridx = 0;
		gCons.fill = GridBagConstraints.HORIZONTAL;
		gCons.ipady = 400;
		gCons.gridy = 0;
		gCons.gridwidth = 1;
		gCons.gridheight = 1;
		gCons.weightx = 1.0;
		gCons.weighty = 0.95;
		vmDeviceTabPanel.add(createControlScrollPane(deviceType), gCons);
		gCons.insets = new Insets(15, 0, 0, 0);
		gCons.ipady = 75;
		gCons.gridy = 1;
		gCons.weighty = 0.05;
		vmStatusScrollPane = createStatusScrollPane(deviceType);
		vmDeviceTabPanel.add(vmStatusScrollPane, gCons);
		updateFreeDeviceStatus(deviceType);
		return vmDeviceTabPanel;
	}

	/**
	 * Creates the scroll pane which holds the status table for each device.
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @return the JScrollpane object
	 */
	private JScrollPane createStatusScrollPane(int deviceType){
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
			cdStatusTable = vmStatusTable;
			titleSting = LocaleStrings.getString("AK_3_VMP")+" "+LocaleStrings.getString("AK_2_VMP");
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			hdStatusTable = vmStatusTable;
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
	 * Gets the device control panel for each device which is stored in a HashMap
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param panelIndex -  pnael index key which is used to retrieve the object from the HashMap
	 * @return
	 */
	public DeviceControlPanel getDeviceControlPanel(int deviceType, int panelIndex){
		DeviceControlPanel deviceControlPanel = null;
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			deviceControlPanel = (DeviceControlPanel) deviceControlPanelMap.get(CD_DEV_CTRL_PANEL_KEY+panelIndex);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			deviceControlPanel = (DeviceControlPanel) deviceControlPanelMap.get(HD_DEV_CTRL_PANEL_KEY+panelIndex);
			break;
		}
		return deviceControlPanel;
	}

	public JTable getCDStatusTable() {
		return cdStatusTable.getStatusTable();
	}
	public JTable getHDStatusTable() {
		return hdStatusTable.getStatusTable();
	}
	public void updateDeviceControlPanel(int deviceType, String[] driveList){
		DeviceControlPanel deviceControlPanel = null;
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			for(int deviceIndex = 0; deviceIndex < VMApp.getInstance().getNumCD(); deviceIndex++){
				deviceControlPanel = (DeviceControlPanel) deviceControlPanelMap.get(CD_DEV_CTRL_PANEL_KEY+deviceIndex);
				updateDevicePanel(deviceType, deviceIndex, driveList, deviceControlPanel);
			}
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			for(int deviceIndex = 0; deviceIndex < VMApp.getInstance().getNumHD(); deviceIndex++){
				deviceControlPanel = (DeviceControlPanel) deviceControlPanelMap.get(HD_DEV_CTRL_PANEL_KEY+deviceIndex);
				if(JViewer.IsClientAdmin() && System.getProperty("os.name").toLowerCase().contains("windows")){
					if(driveList != null){
						//If any of the entries in the drive list is corresponding to physical drives,
						//then make the Physical and Logical drive selection radio buttons visible.
						for(String drive : driveList){
							if(drive.startsWith(LocaleStrings.getString("A_5_DP"))){//physical drive
								if(deviceControlPanel.getPhysicalDrive() != null)
									deviceControlPanel.getPhysicalDrive().setVisible(true);
								if(deviceControlPanel.getLogicalDrive() != null)
									deviceControlPanel.getLogicalDrive().setVisible(true);
								break;
							}
						}
					}
					//If drive list is null, that means no physical devices are connected.
					//In this case, make the Physical and Logical drive selection radio buttons invisible. 
					else{
						if(deviceControlPanel.getPhysicalDrive() != null){
							deviceControlPanel.getPhysicalDrive().setVisible(false);
							//Set the Physical drive selection radio button selected.
							deviceControlPanel.getPhysicalDrive().setSelected(true);
						}
						if(deviceControlPanel.getLogicalDrive() != null)
							deviceControlPanel.getLogicalDrive().setVisible(false);
					}
				}
				updateDevicePanel(deviceType, deviceIndex, driveList, deviceControlPanel);
			}
			break;
		}
	}


	private void updateDevicePanel(int deviceType, int deviceIndex, String[] driveList, DeviceControlPanel deviceControlPanel){
		DevicePanel devicePanel = null;
		deviceControlPanel.getInstanceScrollPane().setViewportView(null);
		devicePanel = new DevicePanel(driveList, deviceType, deviceIndex);
		deviceControlPanel.getInstanceScrollPane().setViewportView(devicePanel);
		deviceControlPanel.setDevicePanel(devicePanel);
		deviceControlPanel.updateDeviceControlPanel();
		deviceControlPanel.revalidate();
		deviceControlPanel.repaint();
	}


	public void physicalDriveChangeState(int deviceType, int deviceIndex, String redirectedDrive, boolean state){
		int numDevice = 0;
		DeviceControlPanel devCtrlPanle = null;
		String devCtrlPanelKey = null;
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			numDevice = VMApp.getInstance().getNumCD();
			devCtrlPanelKey = CD_DEV_CTRL_PANEL_KEY;
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			numDevice = VMApp.getInstance().getNumHD();
			devCtrlPanelKey = HD_DEV_CTRL_PANEL_KEY;
			break;
		}
		for(int index = 0; index < numDevice; index ++){
			if(index == deviceIndex)
				continue;
			if(VMApp.getInstance().getIUSBRedirSession().getDeviceRedirStatus(deviceType, index) != IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED){
			devCtrlPanle = (DeviceControlPanel) deviceControlPanelMap.get(devCtrlPanelKey+index);
			devCtrlPanle.getDevicePanel().updatePhysicalDeviceState(deviceType,redirectedDrive, state);
			}
		}
	}

	public void updateJVToolbar(int deviceType){
		int maxDeviceCount = 0;
		boolean connected = false;
		JButton devButton = null;
		String defaultIcon = null;
		String connectedIcon = null;
		String defaultToolTip = null;
		String connectedToolTip = null;
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			maxDeviceCount = VMApp.getInstance().getNumCD();
			devButton = JViewerApp.getInstance().getM_wndFrame().getToolbar().getCDBtn();
			defaultIcon = "CD.png";
			defaultToolTip = LocaleStrings.getString("G_20_VMD");
			connectedIcon = "CDR.png";
			connectedToolTip = LocaleStrings.getString("G_21_VMD");
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			maxDeviceCount = VMApp.getInstance().getNumHD();
			devButton = JViewerApp.getInstance().getM_wndFrame().getToolbar().getHardddiskBtn();
			defaultIcon = "HD.png";
			defaultToolTip = LocaleStrings.getString("G_24_VMD");
			connectedIcon = "HDR.png";
			connectedToolTip = LocaleStrings.getString("G_25_VMD");
			break;
		}
		for(int devIndex = 0; devIndex < maxDeviceCount; devIndex++){
			if(VMApp.getInstance().getIUSBRedirSession().getDeviceRedirStatus(deviceType, devIndex) ==
					IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED){
				connected = true;
				break;
			}
		}
		if(connected){
			devButton.setIcon(getImageIcon(connectedIcon));
			devButton.setToolTipText(connectedToolTip);
		}
		else{
			devButton.setIcon(getImageIcon(defaultIcon));
			devButton.setToolTipText(defaultToolTip);
		}
	}
	public void updateDeviceStatusTable(int deviceType, int deviceIndex, boolean isConnected){
		IUSBRedirSession iusbRedirSession = VMApp.getInstance().getIUSBRedirSession();
		JTable statusTable = null;
		String targetDevice = LocaleStrings.getString("AL_8_ST");
		String source = LocaleStrings.getString("AL_8_ST");
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			statusTable = getCDStatusTable();
			if(isConnected)
				targetDevice = LocaleStrings.getString("AL_5_ST")+" : "+
						iusbRedirSession.getCDInstanceNumber(deviceIndex);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			statusTable = getHDStatusTable();
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
	 * Upadte the free devices status in VMedia dialog.
	 */
	public void updateFreeDeviceStatus(int deviceType){
		int freeSlot=0;
		int devCount = 0;
		int freeDevCount = 0;
		IUSBRedirSession iusbRedirSession = VMApp.getInstance().getIUSBRedirSession();
		JButton connectButton = null;
		DevicePanel devicePanel = null;
		switch(deviceType){
		case VMApp.DEVICE_TYPE_CDROM:
			if(!JViewer.isCdServiceEnabled())
				return;
			devCount = VMApp.getInstance().getNumCD();
			freeDevCount = VMApp.getInstance().getFreeCDNum();
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			if(!JViewer.isHdServiceEnabled())
				return;
			devCount = VMApp.getInstance().getNumHD();
			freeDevCount = VMApp.getInstance().getFreeHDNum();
			break;
		default:
			Debug.out.println("Invalid device type : "+deviceType);
			break;
		}
		for(int deviceIndex=0;deviceIndex<devCount;deviceIndex++) {
			connectButton = getDeviceControlPanel(deviceType, deviceIndex).getConnectButton();
			devicePanel = getDeviceControlPanel(deviceType, deviceIndex).getDevicePanel();
			/*Check whether the current VMedia instance in already connected. If so we don't update the
			controls since they will be already disabled.
			*/
			if( iusbRedirSession.getDeviceRedirStatus(deviceType, deviceIndex) != 
					IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED ) {
				//if not connected, then enable all the controls in the device control panel
				devicePanel.enableAll();
				connectButton.setEnabled(true);
				iusbRedirSession.setDeviceStatus(deviceType, deviceIndex, IUSBRedirSession.DEVICE_FREE);
				if(freeSlot < freeDevCount)
				{
					//The current VMedia instance is considered free and free slot count is incremented. 
					freeSlot++;
					continue;
				}
				else
				{
					/*If the current VMedia instance is free and its index is greater than the free device count,
					it means this device is already being used by another media redirection client. So it the
					controls on the device instance should be disabled.
					*/
					devicePanel.disableAll();
					connectButton.setEnabled(false);
					iusbRedirSession.setDeviceStatus(deviceType, deviceIndex, IUSBRedirSession.DEVICE_USED);
					devicePanel.revalidate();
					devicePanel.repaint();
				}
			}
		}
	}
	/**
	 * @return the statusTabPanel
	 */
	public StatusTabPanel getStatusTabPanel() {
		return statusTabPanel;
	}
	/**
	 * @param statusTabPanel the statusTabPanel to set
	 */
	public void setStatusTabPanel(StatusTabPanel statusTabPanel) {
		this.statusTabPanel = statusTabPanel;
	}
	public void setSelectedTab(int selectedTabIndex){
		vmDevicePane.setSelectedIndex(selectedTabIndex);
	}
	public JTabbedPane getVmDevicePane() {
		return vmDevicePane;
	}
	public void setVmDevicePane(JTabbedPane vmDevicePane) {
		this.vmDevicePane = vmDevicePane;
	}
	
	/**
	 * Upadte the enable/disble the controls in VMedia dialog for each device.
	 */
	public void updateDeviceControls(int deviceType, boolean state){
		int devCount = 0;
		JButton connectButton = null;
		DevicePanel devicePanel = null;
		switch(deviceType){
		case VMApp.DEVICE_TYPE_CDROM:
			devCount = VMApp.getInstance().getNumCD();
			//don't update vmedia dialog based on free instances for vmapp. free num will be 0 for vmapp
			if((!JViewer.isVMApp()) && devCount > VMApp.getInstance().getFreeCDNum())
				devCount = VMApp.getInstance().getFreeCDNum();
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			devCount = VMApp.getInstance().getNumHD();
			//don't update vmedia dialog based on free instances for vmapp. free num will be 0 for vmapp
			if((!JViewer.isVMApp()) && devCount > VMApp.getInstance().getFreeHDNum())
				devCount = VMApp.getInstance().getFreeHDNum();
			break;
		default:
			Debug.out.println("Invalid device type : "+deviceType);
			break;
		}
		for(int deviceIndex=0;deviceIndex<devCount;deviceIndex++) {
			connectButton = getDeviceControlPanel(deviceType, deviceIndex).getConnectButton();
			devicePanel = getDeviceControlPanel(deviceType, deviceIndex).getDevicePanel();
			/*Check whether the current VMedia instance in already connected. If so we don't update the
			controls since they will be already disabled.
			*/
			if(state) {
				//if not connected, then enable all the controls in the device control panel
				devicePanel.enableAll();
				connectButton.setEnabled(true);
				//iusbRedirSession.setDeviceStatus(deviceType, deviceIndex, IUSBRedirSession.DEVICE_FREE);
			}
			else
			{
				/*If the current VMedia instance is free and its index is greater than the free device count,
					it means this device is already being used by another media redirection client. So it the
					controls on the device instance should be disabled.
				 */
				devicePanel.disableAll();
				connectButton.setEnabled(false);
				//iusbRedirSession.setDeviceStatus(deviceType, deviceIndex, IUSBRedirSession.DEVICE_USED);
				devicePanel.revalidate();
				devicePanel.repaint();
			}
		}
	}

	/**
	 * Upadte the status bar message in VMedia dialog.
	 */
	public void showMessageForSelectedTab(int selectedIndex) {
		switch(selectedIndex) 
		{
		case VMApp.CD_MEDIA:
			if(JViewer.getCDState() == 0) /*if service is disabled*/
				VMApp.getVMStatusPanel().setStatusMessage(LocaleStrings.getString("G_27_VMD"));
			else {/*if service is enabled*/
				//clear status messages
				VMApp.getVMStatusPanel().setStatusMessage("");
			}
			break;
		case VMApp.HD_MEDIA:
			if(JViewer.getHDState() == 0)/*if service is disabled*/
				VMApp.getVMStatusPanel().setStatusMessage(LocaleStrings.getString("G_31_VMD"));
			else if (true == JViewer.IsClientAdmin()){ /*if service is enabled*/
				if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_REDIR_RD_WR_MODE) == 
						JViewerApp.OEM_REDIR_RD_WR_MODE) {
					VMApp.getVMStatusPanel().setStatusMessage(LocaleStrings.getString("G_36_VMD"));
				}
				else {
					VMApp.getVMStatusPanel().setStatusMessage("");
				}
			}
			else if (false == JViewer.IsClientAdmin()) /*if service is enabled and the client is NOT admin then show launch as admin the message*/
				VMApp.getVMStatusPanel().setStatusMessage(LocaleStrings.getString("G_35_VMD"));
			break;
		case VMApp.STATUS_TAB: /*for connection status tab clear all message*/
			VMApp.getVMStatusPanel().setStatusMessage("");
			break;
		default:
			Debug.out.println("Invalid index : " + selectedIndex);
		}
	}
}
