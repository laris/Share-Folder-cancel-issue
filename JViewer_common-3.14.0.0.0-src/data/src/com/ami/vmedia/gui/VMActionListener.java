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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.vmedia.VMApp;
import com.ami.kvm.jviewer.gui.customizefilechooser;

/**
 * The ActionListner class for the connect button in the VMedia dialog. 
 * @author deepakmn
 *
 */
public class VMActionListener implements ActionListener {

	public static final String CD_IMAGE_PATH_ACTION_CMD = "CD_IMAGE_PATH_ADDRESS_";
	public static final String HD_IMAGE_PATH_ACTION_CMD = "HD_IMAGE_PATH_ADDRESS_";
	public static final String CD_CONNECT_ACTION_CMD = "CONNECT_CD_" ;
	public static final String HD_CONNECT_ACTION_CMD = "CONNECT_HD_" ;
	public static final String CD_BROWSE_ACTION_CMD = "BROWSE_CD_" ;
	public static final String HD_BROWSE_ACTION_CMD = "BROWSE_HD_" ;
	public static final String HD_FOLDER_BROWSE_ACTION_CMD = "FOLDER_BROWSE_HD_" ;
	public static final String HD_TEMP_IMAGE_BROWSE_ACTION_CMD = "TEMP_IMAGE_BROWSE_HD_" ;

	private JFileChooser fc;
	public String StoreLocation = null;
	/**
	 * ActionEvent handler.
	 */
	public void actionPerformed(ActionEvent ae) {
		String actionCommand = ae.getActionCommand();
		if(actionCommand.startsWith(CD_BROWSE_ACTION_CMD)){
			int deviceIndex = getDeviceIndex(actionCommand);
			DevicePanel devicePanel = VMApp.getVMPane().getDeviceControlPanel(
					VMApp.DEVICE_TYPE_CDROM, deviceIndex).getDevicePanel();
			if(devicePanel.getSelectedImagePath() == null)
				devicePanel.setSelectedImagePath(VMApp.cdImagePath[deviceIndex][0]);
			String imagePath = VMApp.getInstance().getIUSBRedirSession().
					cdImageSelector(devicePanel.getSelectedImagePath());
			if( imagePath != null ){
				devicePanel.setImagePath(imagePath, VMApp.DEVICE_TYPE_CDROM, deviceIndex);
				devicePanel.setSelectedImagePath(imagePath);//the currently browsed image path is set as the selected path
			}
		}
		else if(actionCommand.startsWith(HD_BROWSE_ACTION_CMD)){
			int deviceIndex = getDeviceIndex(actionCommand);
			DevicePanel devicePanel = VMApp.getVMPane().getDeviceControlPanel(
					VMApp.DEVICE_TYPE_HD_USB, deviceIndex).getDevicePanel();
			if(devicePanel.getSelectedImagePath() == null)
				devicePanel.setSelectedImagePath(VMApp.hdImagePath[deviceIndex][0]);
			String imagePath = VMApp.getInstance().getIUSBRedirSession().
					floppyImageSelector(devicePanel.getSelectedImagePath());
			if( imagePath != null ){
				devicePanel.setImagePath(imagePath, VMApp.DEVICE_TYPE_HD_USB, deviceIndex);
				devicePanel.setSelectedImagePath(imagePath);//the currently browsed image path is set as the selected path
			}
		}
		else if(actionCommand.startsWith(CD_CONNECT_ACTION_CMD)){
			int deviceIndex = getDeviceIndex(actionCommand);
			if(!JViewer.isCdServiceEnabled())
			{
				Component parent = null;
				if(JViewer.isVMApp()){
					VMApp.getInstance();
					parent = VMApp.getVMFrame();
				}
				parent = JViewerApp.getInstance().getVMDialog();

				Debug.out.println("cdrom service disabled");
				JOptionPane.showMessageDialog( parent, LocaleStrings.getString("G_27_VMD"), LocaleStrings.getString("G_26_VMD"), JOptionPane.ERROR_MESSAGE );
				return;
			}
			VMApp.getInstance().getRedirectionController().handleDeviceRedirection(VMApp.DEVICE_TYPE_CDROM, deviceIndex);
		}
		else if(actionCommand.startsWith(HD_CONNECT_ACTION_CMD)){
			int deviceIndex = getDeviceIndex(actionCommand);
			if(!JViewer.isHdServiceEnabled())
			{
				Component parent = null;
				if(JViewer.isVMApp()){
					VMApp.getInstance();
					parent = VMApp.getVMFrame();
				}
				else
					parent = JViewerApp.getInstance().getVMDialog();

				Debug.out.println("harddisk service disabled");
				JOptionPane.showMessageDialog( parent, LocaleStrings.getString("G_31_VMD"), LocaleStrings.getString("G_30_VMD"), JOptionPane.ERROR_MESSAGE );
				return;
			}
			VMApp.getInstance().getRedirectionController().handleDeviceRedirection(VMApp.DEVICE_TYPE_HD_USB, deviceIndex);
		}
		else if(actionCommand.startsWith(HD_FOLDER_BROWSE_ACTION_CMD)){
			int deviceIndex = getDeviceIndex(actionCommand);
			DevicePanel devicePanel = VMApp.getVMPane().getDeviceControlPanel(
				VMApp.DEVICE_TYPE_HD_USB, deviceIndex).getDevicePanel();
			fc = new JFileChooser(StoreLocation);
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			customizefilechooser customfilechooser = new customizefilechooser();
			customfilechooser.customizeFileChooser(fc);

			Component parent = null;
			
			if(JViewer.isVMApp()){
				VMApp.getInstance();
				parent = VMApp.getVMFrame();
			}
			else {
				parent = JViewerApp.getInstance().getVMDialog();
			}

			int returnVal = fc.showDialog(parent, LocaleStrings.getString("V_8_VRS"));
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if(fc.getSelectedFile().exists())
					StoreLocation = fc.getSelectedFile().toString();
				else
					StoreLocation = fc.getSelectedFile().getParent();
			}

			try {
				// resolves symbolic links of Linux OS in the path
				StoreLocation = Paths.get(StoreLocation).toRealPath().toString();
			} catch (IOException ioe) {
				Debug.out.println(ioe);
			}

			devicePanel.folderTxt.setText(StoreLocation);
		}
		else if(actionCommand.startsWith(HD_TEMP_IMAGE_BROWSE_ACTION_CMD)){
			int deviceIndex = getDeviceIndex(actionCommand);
			DevicePanel devicePanel = VMApp.getVMPane().getDeviceControlPanel(
				VMApp.DEVICE_TYPE_HD_USB, deviceIndex).getDevicePanel();
			fc = new JFileChooser(StoreLocation);
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			customizefilechooser customfilechooser = new customizefilechooser();
			customfilechooser.customizeFileChooser(fc);

			Component parent = null;
			if(JViewer.isVMApp()){
				VMApp.getInstance();
				parent = VMApp.getVMFrame();
			}
			else
				parent = JViewerApp.getInstance().getVMDialog();

			int returnVal = fc.showDialog(parent, LocaleStrings.getString("V_8_VRS"));
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if(fc.getSelectedFile().exists())
					StoreLocation = fc.getSelectedFile().toString();
				else
					StoreLocation = fc.getSelectedFile().getParent();
			}

			try {
				// resolves symbolic links of Linux OS in the path
				StoreLocation = Paths.get(StoreLocation).toRealPath().toString();
			} catch (IOException ioe) {
				Debug.out.println(ioe);
			}

			devicePanel.imageTxt.setText(StoreLocation);
		}

	}

	/**
	 * Get the device index from the action command value
	 * @param actionCommand - the action command assigned to a control.
	 * @return the device index number.
	 */
	private int getDeviceIndex(String actionCommand){
		int deviceIndex = 0;
		String index = actionCommand.substring(actionCommand.lastIndexOf('_')+1, actionCommand.length());
		deviceIndex = Integer.parseInt(index);
		return deviceIndex;
	}

}
