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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Enumeration;

import java.awt.Font;
import java.awt.Label;
import java.awt.Dimension;
import javax.swing.JTextField;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.vmedia.VMApp;

/**
 * This class constructs the panel which will hold all the components<br>
 * for listing the devices to be redirected, for each device instance. 
 * @author deepakmn
 *
 */
public class DevicePanel extends JPanel {

	public static String PHYSICAL_DRIVE_WIN = LocaleStrings.getString("A_5_DP");
	public static String LOGICAL_DRIVE_WIN = LocaleStrings.getString("A_6_DP");
	public static final String HD_DRIVE_LIN = "/dev/sd";
	private static final long serialVersionUID = 1L;
	// The default amount of device selectors
	// CD/DVD device: CD Image Redirection
	// HD/USB device: HD/USB Image Redirection, Folder Redirection
	private static final int DEFAULT_NUM_CDROM_DEVICE = 1;
	private static final int DEFAULT_NUM_HD_USB_DEVICE = 2;
	private final Color DEV_PANEL_BG_COLOR = Color.WHITE;
	private String[] imagePathList;
	private	ButtonGroup deviceSelectionGroup;
	private JRadioButton[] selectDevice;
	private int totalDrives;
	private JPanel panelImage;
	private int deviceType;
	
	public JButton folderBrowseBtn;
	public JButton tmpImgBrowseBtn;
	public Label imgLabel;
	public Label sizeLabel;
	public JTextField sizeTxt;
	public JTextField folderTxt;
	public JTextField imageTxt;

	public boolean hd_flag = false;

	public	JButton browseButton;
	public JComboBox imageAddressBar;
	public  String selectedImagePath;

	/***
	 * Constructor.
	 * Creates the VMedia device display panel
	 * @param driveList - list of the drives to be  displayed
	 * @param deviceType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 * @param deviceIndex - device index determines the device instance for<br>
	 *						which the device panel is created.
	 */
	public DevicePanel(String driveList[], int deviceType, int deviceIndex) {
		/*
		 * If physical drives list length more than 1, i.e., it contains
		 * two elements, then the number of rows will be the length of the
		 * drives list + 1(For image redirection).
		 */
		super( new GridLayout(((driveList!=null && 
				(driveList.length > VMApp.DEFAULT_NUM_DRIVES))?
						(driveList.length+1):(VMApp.DEFAULT_NUM_DRIVES+1)),
						1,0,0) ); //including External Image
		this.deviceType = deviceType;
		int r_ix = 1;
		int drivesStartIndex = (deviceType == VMApp.DEVICE_TYPE_HD_USB)?DEFAULT_NUM_HD_USB_DEVICE: DEFAULT_NUM_CDROM_DEVICE;
		// build the radio buttons for each drive.
		deviceSelectionGroup = new ButtonGroup();
		addDeviceSelectors(deviceType, driveList);

		// construct panel for redirecting ISO/floppy/hard disk/USB images 
		//which will have a radio button, combo box, and a push button.
		panelImage = new JPanel(new BorderLayout(5, 5));
		panelImage.setBackground(DEV_PANEL_BG_COLOR);

		createImageAddressBar(deviceType, deviceIndex);

		JPanel browseBtnPanel = new JPanel(new GridBagLayout());
		browseBtnPanel.setBackground(DEV_PANEL_BG_COLOR);
		GridBagConstraints gcons =  new GridBagConstraints();
		gcons.insets = new Insets(0, 2, 0, 5);
		browseButton = createBrowseButton(deviceType, deviceIndex);		
		browseBtnPanel.add(browseButton, gcons);//put browse button into browse button panel
		
		panelImage.add(selectDevice[0], BorderLayout.WEST);
		panelImage.add(imageAddressBar, BorderLayout.CENTER);
		panelImage.add(browseBtnPanel, BorderLayout.EAST);

		setLayout(new GridBagLayout());
		GridBagConstraints gridCons = new GridBagConstraints();

		gridCons.gridx = 0;
		gridCons.gridy = 0;
		gridCons.gridwidth = 1;
		gridCons.gridheight = 1;
		gridCons.weightx = 1.0;
		gridCons.anchor = GridBagConstraints.WEST;
		gridCons.fill = GridBagConstraints.HORIZONTAL;
		//panelImage.add(createTopPanel(deviceType), BorderLayout.NORTH);
		add(panelImage, gridCons);
		
		if(deviceType == VMApp.DEVICE_TYPE_HD_USB) {
			panelImage = new JPanel(new BorderLayout(5, 5));
			panelImage.setBackground(DEV_PANEL_BG_COLOR);
			//for browse button
			browseBtnPanel = new JPanel(new GridBagLayout());
			browseBtnPanel.setBackground(DEV_PANEL_BG_COLOR);

			gcons.insets = new Insets(0, 2, 0, 5);
			folderBrowseBtn = getFolderBrowseBtn(deviceIndex);		
			browseBtnPanel.add(folderBrowseBtn, gcons);
			//put Radio button, AddressBar, and Browser button into panel
			panelImage.add(selectDevice[1], BorderLayout.WEST);
			panelImage.add(folderTxt, BorderLayout.CENTER);
			panelImage.add(browseBtnPanel, BorderLayout.EAST);

			gridCons.insets = new Insets(5, 0, 0, 0);
			gridCons.gridy = 1;
			add(panelImage, gridCons);

			JPanel jp1 = new JPanel(new BorderLayout());
			JPanel jp2 = new JPanel(new BorderLayout());
			panelImage = new JPanel(new BorderLayout());
			panelImage.setBackground(DEV_PANEL_BG_COLOR);
			//for browse button
			browseBtnPanel = new JPanel(new GridBagLayout());
			browseBtnPanel.setBackground(DEV_PANEL_BG_COLOR);

			gcons.insets = new Insets(0, 2, 0, 5);
			tmpImgBrowseBtn = getTmpImageBrowseBtn(deviceIndex);
			browseBtnPanel.add(tmpImgBrowseBtn, gcons);
			jp1.add(sizeLabel, "West");
			jp1.add(sizeTxt, "Center");
 			
			jp2.add(imgLabel,"West");
			jp2.add(imageTxt,"Center");
			jp2.add(browseBtnPanel,"East");
			
			panelImage.add(jp1,"West");
			panelImage.add(jp2,"Center");
	
			gridCons.insets = new Insets(2, 2, 0, 0);
			gridCons.gridy = 2;
			add(panelImage, gridCons);

			sizeLabel.setVisible(true);
			sizeTxt.setVisible(true);
			imgLabel.setVisible(true);
			folderBrowseBtn.setEnabled(false);
			folderTxt.setVisible(true);
			tmpImgBrowseBtn.setEnabled(false);
			imageTxt.setVisible(true);
			
			r_ix = 3;
			// while adding to panel 
			// gridy	0 - ISO
			//			1 - Folder redirection
			//			2 - Folder redirection image path
			//			3 to n - drive list			
			// totalDrives has original drive list length + 2( 1 for ISO , 1 for Folder redirection)
		}
		if(deviceType == VMApp.DEVICE_TYPE_HD_USB)
			gridCons.insets = new Insets(10, 0, 0, 0);
		else
			gridCons.insets = new Insets(5, 0, 0, 0);
		
		//add rest of the drives to the table
		//if(JViewer.IsClientAdmin()){
		gridCons.insets = new Insets(0, 0, 0, 0);
		for(int index = drivesStartIndex; index < totalDrives ; r_ix++ ,index++){
			gridCons.gridy = r_ix;
			add(selectDevice[index], gridCons);			
		}
		if(deviceType == VMApp.DEVICE_TYPE_HD_USB){
			//Physical Drives radio button will be select by default.
			//So the physical drives radio button alone should be visible.
			if(System.getProperty("os.name").toLowerCase().contains("windows")){
				int isPhysicalDrvSelected = 1;
				PHYSICAL_DRIVE_WIN = LocaleStrings.getString("A_5_DP");
				LOGICAL_DRIVE_WIN = LocaleStrings.getString("A_6_DP");
				if(VMApp.getVMPane() != null){
					DeviceControlPanel dCP = VMApp.getVMPane().getDeviceControlPanel(deviceType, deviceIndex);
					if( dCP != null){
						if(dCP.getLogicalDrive().isSelected()){
							isPhysicalDrvSelected = 0;
						}
					}
				}
				if(isPhysicalDrvSelected == 1)
					enableHardDiskDrives(PHYSICAL_DRIVE_WIN);
				else
					enableHardDiskDrives(LOGICAL_DRIVE_WIN);
			}
			else 
				enableHardDiskDrives(HD_DRIVE_LIN);
		}
		//}

		setBackground(DEV_PANEL_BG_COLOR);
	} // end constructor

	private JButton getFolderBrowseBtn(int deviceIndex) {
		if (folderBrowseBtn == null)
		{
			folderBrowseBtn = new JButton();
			folderBrowseBtn.setActionCommand(VMActionListener.HD_FOLDER_BROWSE_ACTION_CMD + deviceIndex);
			folderBrowseBtn.setText(LocaleStrings.getString("A_1_DP"));
			folderBrowseBtn.addActionListener(new VMActionListener());
		}
		return folderBrowseBtn;
	}

	private JButton getTmpImageBrowseBtn(int deviceIndex) {
		if (tmpImgBrowseBtn == null)
		{
			tmpImgBrowseBtn = new JButton();
			tmpImgBrowseBtn.setActionCommand(VMActionListener.HD_TEMP_IMAGE_BROWSE_ACTION_CMD + deviceIndex);
			tmpImgBrowseBtn.setText(LocaleStrings.getString("A_1_DP"));
			tmpImgBrowseBtn.addActionListener(new VMActionListener());
		}
		return tmpImgBrowseBtn;
	}

	/**
	 * Action listener for the device selection radio buttons.
	 * @author deepakmn
	 *
	 */

	class RadioButtonChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e)
		{
			// selected Hard Disk/USB tab
			if(hd_flag) {
				sizeLabel.setVisible(true);
				imgLabel.setVisible(true);
				// selected HD/USB Image radio button
				if (selectDevice[0].isSelected()) {
					if(folderBrowseBtn != null)
						folderBrowseBtn.setEnabled(false);

					if(tmpImgBrowseBtn != null)
						tmpImgBrowseBtn.setEnabled(false);

					if(sizeTxt != null)
						sizeTxt.setEnabled(false);
					
					if (browseButton != null)
						browseButton.setEnabled(true);
					if (imageAddressBar != null)
						imageAddressBar.setEnabled(true);
				}
				// selected Folder Path radio button
				else if (selectDevice[1].isSelected()) {
					if (folderBrowseBtn != null)
						folderBrowseBtn.setEnabled(true);

					if(tmpImgBrowseBtn != null)
						tmpImgBrowseBtn.setEnabled(true);

					if(sizeTxt != null) {
						sizeTxt.setEnabled(true);
						sizeTxt.setEditable(true);
					}
				}
				// unselected HD/USB Image or Folder Path radio button
				else {
					browseButton.setEnabled(false);
					folderBrowseBtn.setEnabled(false);
					tmpImgBrowseBtn.setEnabled(false);
					if (imageAddressBar != null)
						imageAddressBar.setEnabled(false);

					if(sizeTxt != null)
						sizeTxt.setEnabled(false);
				}
			}
			// selected CD/DVD tab
			else {
				// selected CD Image radio button
				if (selectDevice[0].isSelected()) {
					if (browseButton != null)
						browseButton.setEnabled(true);
					if (imageAddressBar != null)
						imageAddressBar.setEnabled(true);
				}
				// unselected CD Image radio button
				else {
					browseButton.setEnabled(false);
					if (imageAddressBar != null)
						imageAddressBar.setEnabled(false);
				}
			}
		}
	}

	/**
	 * Return the ISOImage radio button is selected or unselected
	 * @return
	 */
	public boolean isImageSelected() {
		if (selectDevice[0].getModel() == deviceSelectionGroup.getSelection())
			return true;
		return false;
	}

	public boolean isFolderMountSelected() {
		if (selectDevice[1].getModel() == deviceSelectionGroup.getSelection())
			return true;
		return false;
	}

	/***
	 * Set the image path specified by the user
	 * @param path - selected path
	 * @param devicetype - the redirected device type(CD/Floppy/HDD)
	 * @param deviceIndex - index of the device to determine for which <br>
	 * 						instance of the device, the path needs to be set.
	 */
	public void setImagePath(String path, int devicetype, int deviceIndex)
	{		
		int j = 0;
		//List of already saved paths .
		String[] savedPathList = null;
		switch(devicetype) {
		case VMApp.DEVICE_TYPE_CDROM:
			savedPathList = VMApp.cdImagePath[deviceIndex];
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			savedPathList = VMApp.hdImagePath[deviceIndex];
			break;
		}

		imagePathList = new String[VMApp.MAX_IMAGE_PATH_COUNT];
		if(imageAddressBar.getItemAt(0) == null){
			imageAddressBar.insertItemAt(path, 0);
			imagePathList[0] = path;
		}
		else{
			//if an image path, thats already in the combobox list, is browsed and selected,
			//remove the already existing entry.
			for(j=VMApp.MAX_IMAGE_PATH_COUNT-1; j>=0; j-- ) {
				if(imageAddressBar.getItemAt(j)!= null && imageAddressBar.getItemAt(j).equals(path)){
					imageAddressBar.removeItemAt(j);
					break;
				}
			}
			//Ensure that the number of paths shown in image path combobox list, is equal to the limit +1
			//while browsing. One extra entry is allowed while browsing to accommodate the newly selected
			//image path.
			if(imageAddressBar.getItemAt(VMApp.MAX_IMAGE_PATH_COUNT) != null && 
					!path.equals(imageAddressBar.getItemAt(VMApp.MAX_IMAGE_PATH_COUNT))){
				imageAddressBar.removeItemAt(VMApp.MAX_IMAGE_PATH_COUNT);
			}
			//Don't keep on adding new items browsing for a file.Add only the last item selected to the list.
			if(!path.equals(imageAddressBar.getItemAt(0))){
				imageAddressBar.insertItemAt(path, 0);
				for(j=0; j < VMApp.MAX_IMAGE_PATH_COUNT; j++){
					if(imageAddressBar.getItemAt(1)!= null && imageAddressBar.getItemAt(1).equals(savedPathList[j]))
						break;
				}
				if(j==VMApp.MAX_IMAGE_PATH_COUNT)
					imageAddressBar.removeItemAt(1);
			}

			//Store the paths in the combobox list into imagePathList array.
			for(j=0;j<VMApp.MAX_IMAGE_PATH_COUNT;j++){
				if((String) imageAddressBar.getItemAt(j) == null)
					break;
				imagePathList[j] = (String) imageAddressBar.getItemAt(j);
			}

		}
		imageAddressBar.setSelectedIndex(0);
	}

	/**
	 * Saves the path of the images redirected through VMedia redirection.
	 * @param pathList - list of redirected paths.
	 * @param devicetype - type of redirected device (CD/Floppy/HDD)
	 * @param deviceIndex - index of the device to determine for which instance of the
	 *						device, the path needs to be saved.
	 */
	public void saveImagePath(String[] pathList, int devicetype, int deviceIndex){
		//Ensure that the number of paths shown in image path combobox list, is equal to the limit.
		if(imageAddressBar.getItemAt(VMApp.MAX_IMAGE_PATH_COUNT) != null){
			imageAddressBar.removeItemAt(VMApp.MAX_IMAGE_PATH_COUNT);
		}
		switch(devicetype) {
		case VMApp.DEVICE_TYPE_CDROM:
			VMApp.cdImagePath[deviceIndex]= pathList;
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			VMApp.hdImagePath[deviceIndex] = pathList;
			break;
		}
	}

	/**
	 * Get the Image path name of the ISO redirection
	 * @return
	 */
	public String getImagePath()
	{
		String path = (String)imageAddressBar.getSelectedItem();

		if(path == null)			
			path = "";

		return path ;
	}

	/**
	 * Return the User selected device string its a Drive or ISO name
	 * @return
	 */
	public String getSelectedDeviceString()
	{
		for (Enumeration e=deviceSelectionGroup.getElements(); e.hasMoreElements(); )
		{
			JRadioButton b = (JRadioButton)e.nextElement();
			if (b.getModel() == deviceSelectionGroup.getSelection())
				return b.getText();
		}
		return "";
	}

	/**
	 *Method disable all component in the VMedia dialog
	 *
	 */
	public void disableAll()
	{
		//disable radio buttons
		for(int i=0; i<selectDevice.length;i++)
			selectDevice[i].setEnabled(false);

		//disable browse button
		browseButton.setEnabled(false);
		//disable text field button
		imageAddressBar.setEnabled(false);
		if(hd_flag) {
			sizeTxt.setEnabled(false);
			folderBrowseBtn.setEnabled(false);
			tmpImgBrowseBtn.setEnabled(false);
		}
	}

	/**
	 * Method Enable all component in the VMedia dialog
	 *
	 */
	public void enableAll(){
		//disable radio buttons
		for(int i=0; i<selectDevice.length;i++)
		{
			if ( !(isDeviceredirected(selectDevice[i].getText())))
			{
				selectDevice[i].setEnabled(true);
			}
			else{
				selectDevice[i].setEnabled(false);
			}
		}

		if(selectDevice[0].isSelected()) {
			//disable browse button
			browseButton.setEnabled(true);	
			//disable text field button
			imageAddressBar.setEnabled(true);
		}
		if(hd_flag) {
			if(selectDevice[1].isSelected()) {
				sizeTxt.setEnabled(true);
				folderBrowseBtn.setEnabled(true);
				tmpImgBrowseBtn.setEnabled(true);
			}
		}
	}
	
	/**
	 * check device is redirected in other instance
	 * @param source
	 */
	public boolean isDeviceredirected(String source){
		String redirectedDeviceLabel /* Device which is currently redirected */ = "", logicalDriveLetter = "";
		String[] physicalDrive = {};
		for(int i=0; i<selectDevice.length;i++){
			if(VMApp.getInstance().getIUSBRedirSession() != null){ // proceed if iusbRedirSession is not null
				// Proceed if any device redirected
				if(VMApp.getInstance().getIUSBRedirSession().getDeviceRedirStatus(deviceType, i) == VMApp.getInstance().getIUSBRedirSession().DEVICE_REDIR_STATUS_CONNECTED ){
					redirectedDeviceLabel = VMApp.getInstance().getIUSBRedirSession().getDeviceRedirSource(deviceType, i); // get the name of the device which is currently redirected
					// if the redirected device is physical drive then we need to update the state of respective logical drives.
					if(redirectedDeviceLabel.startsWith(PHYSICAL_DRIVE_WIN) ||
							redirectedDeviceLabel.startsWith(LocaleStrings.getPreviousLocaleString("A_5_DP"))){
						// check whether the source is a logical drive
						if((source.startsWith(LOGICAL_DRIVE_WIN)||
								redirectedDeviceLabel.startsWith(LocaleStrings.getPreviousLocaleString("A_6_DP")) &&
								source.indexOf('[')>0)){
							// A physical drive may contain more than one logical partition. So getting the list of partition letters of the physical drive.
							physicalDrive = redirectedDeviceLabel.substring(redirectedDeviceLabel.indexOf('[')+1,redirectedDeviceLabel.indexOf(']')).split("-");
							// A logical drive is consist of a single partition. So extracting partition letter from the device label.
							logicalDriveLetter = source.substring(source.indexOf('[')+1,source.indexOf(']'));
							// if any of the physical drive's partition letter matches the logical drive letter,
							// then the current logical drive is a part of a physical drive
							// which is already being redirected.
							for(String physicalDriveLetter : physicalDrive){
								if(physicalDriveLetter.equals(logicalDriveLetter)) return true;
							}
						}
					}
					// Check whether the given device label matches is redirected device's label
					// if so then the given device is redirected.
						String sourceSub = (source.indexOf('-')>=0) ? source.substring(source.indexOf('-'),source.length()) :source;
						String redirSub = (redirectedDeviceLabel.indexOf('-')>=0)? redirectedDeviceLabel.substring(redirectedDeviceLabel.indexOf('-'),redirectedDeviceLabel.length())  :redirectedDeviceLabel;
						if(sourceSub.equals(redirSub)){
							return true;
					}
				}
			}
		}
		return false; // Given device is not redirected otherwise.
	}

	/**
	 * Select the User selected drive or ISO image path name once redirection started,
	 * if user invoke the Vmedia dialog
	 * @param strRedirectedDevice
	 */
	@SuppressWarnings("unchecked")
	public void selectRadioButton(String strRedirectedDevice)
	{
		for (Enumeration e=deviceSelectionGroup.getElements(); e.hasMoreElements(); )
		{
			if(hd_flag) {
				JRadioButton b = (JRadioButton)e.nextElement();
				if(b.isSelected()) {
					b.setSelected(true);
					return;
				}
			}
			else {
				JRadioButton b = (JRadioButton)e.nextElement();
				if ( b.getText().equals(strRedirectedDevice) ) {
					b.setSelected(true);
					return;
				}
			}
		}

		// If control came here, that means, there is no radio button matching the supplied string. So, select
		// the first radio button by default which is a CDROM/Floppy/UsbKey Image.
		Enumeration e=deviceSelectionGroup.getElements();
		if( e.hasMoreElements() ) {
			JRadioButton b = (JRadioButton)e.nextElement();
			b.setSelected(true); // this will be the first radio button in the button group.
		}
	}

	/**
	 * Gets the path list, after rearranging, if required.
	 * @return the pathList
	 */
	public String[] getImagePathList() {
		int index = imageAddressBar.getSelectedIndex();
		String selectedItem = (String) imageAddressBar.getSelectedItem();
		//if a path from the already existing path list is selected, bring it
		//to the first position.
		if(index != 0)
			for(int i = index; i > 0; i--)
				imagePathList[i] = imagePathList[i-1];
		imagePathList[0] = selectedItem;
		return imagePathList;
	}

	/**
	 * @param pathList the pathList to set
	 */
	public void setImagePathList(String[] pathList) {
		this.imagePathList = pathList;
	}

	/**
	 * @return the selected image path.
	 */
	public String getSelectedImagePath() {
		return selectedImagePath;
	}

	/**
	 *  Sets the selected image path. 
	 * @param imagePathSelected
	 */
	public void setSelectedImagePath(String imagePathSelected) {
		selectedImagePath = imagePathSelected;
	}

	/**
	 * Creates the browse button in the device panel
	 * @param deviceType the redirected device type(CD/HDD)
	 * @param deviceIndex - index of the device to determine for which <br>
	 * 						instance of the device, the path needs to be set.
	 * @return the JButton object.
	 */
	private JButton createBrowseButton(int deviceType, int deviceIndex){
		JButton button = new JButton(LocaleStrings.getString("A_1_DP"));
		switch (deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			button.setActionCommand(VMActionListener.CD_BROWSE_ACTION_CMD+deviceIndex);
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			button.setActionCommand(VMActionListener.HD_BROWSE_ACTION_CMD+deviceIndex);
			break;
		default:
			break;
		}
		button.addActionListener(new VMActionListener());
		return button;

	}

	/**
	 * Creates the combo box which will list the image paths selected for redirection.
	 * @param deviceType - the redirected device type(CD/Floppy/HDD)
	 * @param deviceIndex - index of the device to determine for which <br>
	 * 						instance of the device, the path needs to be set.
	 * @return the JComboBox object.
	 */
	private JComboBox createImageAddressBar(int deviceType, int deviceIndex){
		switch(deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			imageAddressBar = new JComboBox(VMApp.cdImagePath[deviceIndex]);
			imageAddressBar.setActionCommand(VMActionListener.CD_IMAGE_PATH_ACTION_CMD+deviceIndex);
			imagePathList = VMApp.cdImagePath[deviceIndex];
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			imageAddressBar = new JComboBox(VMApp.hdImagePath[deviceIndex]);
			imageAddressBar.setActionCommand(VMActionListener.HD_IMAGE_PATH_ACTION_CMD+deviceIndex);
			imagePathList = VMApp.hdImagePath[deviceIndex];
			break;
		}
		imageAddressBar.setEditable(false);
		imageAddressBar.setBackground(DEV_PANEL_BG_COLOR);
		imageAddressBar.setBorder(null);
		imageAddressBar.setMaximumRowCount(VMApp.MAX_IMAGE_PATH_COUNT);
		imageAddressBar.addActionListener(new VMActionListener());

		return imageAddressBar;
	}

	/**
	 * Add the device selection radio buttons to the panel
	 * @param the redirected device type(CD/HDD)
	 * @param deviceIndex - index of the device to determine for which <br>
	 * 						instance of the device, the path needs to be set.
	 */
	public void addDeviceSelectors(int deviceType, String[] driveList){
		int index = 1;
		// If there is at least one actual drive, add it to the amount of total drive for creating radio buttons
		if(deviceType == VMApp.DEVICE_TYPE_HD_USB) 
			totalDrives = (driveList!=null)?driveList.length+DEFAULT_NUM_HD_USB_DEVICE:DEFAULT_NUM_HD_USB_DEVICE;
		else
			totalDrives = (driveList!=null)?driveList.length+DEFAULT_NUM_CDROM_DEVICE:DEFAULT_NUM_CDROM_DEVICE;

		selectDevice = new JRadioButton[totalDrives];
		switch(deviceType) {
		case VMApp.DEVICE_TYPE_CDROM:
			selectDevice[0] = new JRadioButton(LocaleStrings.getString("A_2_DP"));
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			selectDevice[0] = new JRadioButton(LocaleStrings.getString("A_4_DP"));
			selectDevice[1] = new JRadioButton(LocaleStrings.getString("A_7_DP"));
			imgLabel = new Label(LocaleStrings.getString("A_8_DP"),Label.RIGHT);
			imgLabel.setFont(new Font("Dialog",Font.BOLD,12));
			sizeLabel = new Label(LocaleStrings.getString("A_9_DP"),Label.RIGHT);
			sizeLabel.setFont(new Font("Dialog",Font.BOLD,12));
			sizeTxt = new JTextField(3);
			folderTxt = new JTextField(20);
			imageTxt = new JTextField(10);
			folderTxt.setEditable(false);
			folderTxt.setBackground(DEV_PANEL_BG_COLOR);
			imageTxt.setEditable(false);
			imageTxt.setBackground(DEV_PANEL_BG_COLOR);
 			index = 2;
			hd_flag = true;
			break;
		}
		selectDevice[0].setBackground(DEV_PANEL_BG_COLOR);
		selectDevice[0].addChangeListener(new RadioButtonChangeListener());
		deviceSelectionGroup.add( selectDevice[0] );
		selectDevice[0].setSelected(true);
		if(deviceType == VMApp.DEVICE_TYPE_HD_USB) {
			selectDevice[1].setBackground(DEV_PANEL_BG_COLOR);
			selectDevice[1].addChangeListener(new RadioButtonChangeListener());
			deviceSelectionGroup.add( selectDevice[1] );
		}

		// rest of the actual drives
		for(int r_ix = index,d_ix=0;r_ix<totalDrives;r_ix++,d_ix++)
		{
			selectDevice[r_ix] = new JRadioButton(driveList[d_ix]);
			selectDevice[r_ix].setBackground(DEV_PANEL_BG_COLOR);
			deviceSelectionGroup.add( selectDevice[r_ix] );
		}
	}

	/**
	 * Remove the device selection radio buttons from the panel.
	 */
	public void removeDeviceSelectors(){
		for(int index = 0; index < selectDevice.length; index++){
			deviceSelectionGroup.remove(selectDevice[index]);
			remove(selectDevice[index]);
		}
	}

	/**
	 * Enables device selection radio buttons corresponding to
	 * Physical/Logical drives.
	 * @param driveType - PHYSICAL_DRIVE if physical drives are to be enabled,
	 * 						and LOGICAL_DRIVE if logical drive are to be enabled.
	 */
	public void enableHardDiskDrives(String driveType){
		/*
		 * The radio buttons corresponding to the driveType(Physical/Logical)
		 * will be made visible. The rest of the radio buttons will be hidden.
		 * The loop index starts from 1 to avoid image selection radio button.
		 */
		for(int index = 2; index < selectDevice.length; index++){
			if(selectDevice[index].getText().startsWith(driveType)){
				selectDevice[index].setVisible(true);
			}else{
				selectDevice[index].setVisible(false);
			}
		}
	}

	/**
	 * Enable or disable the radio buttons corresponding the Logical Hard Drives
	 * in a redirected Physical Hard Drive. 
	 * @param strRedirectedDevice - Redirected device name.
	 * @param state - enable/disable state
	 */
	private void changeStateLogicalDrives(String strRedirectedDevice,boolean state){
		String[] drives = null ;
		//Get the various drive letters in the redirected physical hard drive.
		try{
			drives = strRedirectedDevice.substring(strRedirectedDevice.indexOf('[')+1,
					strRedirectedDevice.indexOf(']')).split("-");
		}catch(Exception e){
			Debug.out.println(e);
			drives = null;
			return;
		}
		//Compare the text with all the radio buttons in the list. 
		for (Enumeration e = deviceSelectionGroup.getElements(); e.hasMoreElements(); ){
			JRadioButton radioBtn = (JRadioButton)e.nextElement();
			String driveLetter = null;
			//Check if the radio button text starts with "LogicalDrive"
			if(radioBtn.getText().startsWith(LOGICAL_DRIVE_WIN)){
				
				try{
					driveLetter = radioBtn.getText().substring(
							radioBtn.getText().indexOf('[')+1,
							radioBtn.getText().indexOf(']'));
				}catch(Exception e1){
					Debug.out.println(e1);
					driveLetter = null;
					return;
				}
			}
			if(driveLetter != null){
				//Check whether drive letter of the Logical Drive matches with 
				//one of the drive letters in the physical drive.
				//If so change state of the radio button. 
				for(String drive : drives){
					if(driveLetter.equals(drive) && radioBtn.isEnabled() != state){
						radioBtn.setEnabled(state);
						break;
					}
				}
			}
		}
	}

	/**
	 * Enable or disable the radio button corresponding the Physical Hard Drive
	 * to which a redirected Logical Hard Drive belongs to. 
	 * @param strRedirectedDevice - Redirected device name.
	 * @param state - enable/disable state
	 */
	private void changeStatePhysicalDrives(String strRedirectedDevice,boolean state){

		String[] drives = null ;
		//Get the various drive letters in the redirected physical hard drive.
		try{
			drives = strRedirectedDevice.substring(strRedirectedDevice.indexOf('[')+1,
					strRedirectedDevice.indexOf(']')).split("-");
		}catch(Exception e){
			Debug.out.println(e);
			drives = null;
			return;
		}
		//Compare the text with all the radio buttons in the list. 
		for (Enumeration e = deviceSelectionGroup.getElements(); e.hasMoreElements(); ){
			JRadioButton radioBtn = (JRadioButton)e.nextElement();
			String driveLetter = null;
			//Check if the radio button text starts with "LogicalDrive"
			if(radioBtn.getText().startsWith(PHYSICAL_DRIVE_WIN)){
				
				try{
					driveLetter = radioBtn.getText().substring(
							radioBtn.getText().indexOf('[')+1,
							radioBtn.getText().indexOf(']'));
				}catch(Exception e1){
					Debug.out.println(e1);
					driveLetter = null;
					return;
				}
			}
			if(driveLetter != null){
				//Check whether drive letter of the Logical Drive matches with 
				//one of the drive letters in the physical drive.
				//If so change state of the radio button. 
				for(String drive : drives){
					if(driveLetter.contains(drive) && radioBtn.isEnabled() != state){
						radioBtn.setEnabled(state);
						break;
					}
				}
			}
		}
	}

	/**
	 * Updates the status of the physical device drives
	 * @param redirectedDrive
	 * @param state true to enable and false otherwise.
	 */
	public void updatePhysicalDeviceState(int deviceType,String redirectedDrive, boolean state){
		if(deviceType == VMApp.DEVICE_TYPE_HD_USB){
			if(System.getProperty("os.name").toLowerCase().contains("windows")) {
				changeStatePhysicalDrives(redirectedDrive, state);
				changeStateLogicalDrives(redirectedDrive, state);
			} else if(System.getProperty("os.name").toLowerCase().contains("linux")){
				changeStateHDDrives(redirectedDrive, state);
			}
		}
		else{
			for (Enumeration e = deviceSelectionGroup.getElements(); e.hasMoreElements(); ){
				JRadioButton radioBtn = (JRadioButton)e.nextElement();
				if(radioBtn.getText().equals(redirectedDrive))
					radioBtn.setEnabled(state);
			}
		}
	}

	private void changeStateHDDrives(String strRedirectedDevice, boolean state) {
		//Get the drive letter associated with the redirected logical hard drive.
		char driveLetter;
		try{
			driveLetter = strRedirectedDevice.charAt(HD_DRIVE_LIN.length());
		}catch(Exception e){
			Debug.out.println(e);
			return;
		}
		//Compare the text with all the radio buttons in the list.
		for (Enumeration e = deviceSelectionGroup.getElements(); e.hasMoreElements(); ){
			JRadioButton radioBtn = (JRadioButton)e.nextElement();
			//Check whether the radio button name starts with "PhysicalDrive"
			char drive; 
			try{
				drive = radioBtn.getText().charAt(HD_DRIVE_LIN.length());
			}catch(Exception e1){
				Debug.out.println(e1);
				return;
			}
			//Check whether any of the drive letters in the physical drive
			//matches with the drive letter of the redirected logical drive.
			//If so change the state of the radio button. 
			if(driveLetter == drive && radioBtn.isEnabled() != state){
				radioBtn.setEnabled(state);
				break;
			}
		}
	}
}
