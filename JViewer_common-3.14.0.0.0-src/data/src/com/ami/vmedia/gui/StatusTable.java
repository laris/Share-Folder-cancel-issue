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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import com.ami.iusb.IUSBRedirSession;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.gui.JVPopupMenu;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.vmedia.VMApp;

/**
 * This class creates the table which shows the device redirection status.
 * @author deepakmn
 *
 */
public class StatusTable{

	private final int TABLE_ROWS = 5;
	private final String DISCONNECT = "Disconnect";
	private Object tableData[];
	private String tableStringData[][];
	private int numInstnces;
	private int deviceType;
	private int deviceIndex;
	private String	columnNames[] = {LocaleStrings.getString("AL_1_ST"),
			LocaleStrings.getString("AL_2_ST"),
			LocaleStrings.getString("AL_3_ST"),
			LocaleStrings.getString("AL_4_ST"),
			LocaleStrings.getString("AL_16_ST")};
	private JTable statusTable;
	private JVPopupMenu popupMenu;

	/**
	 * Constructor.
	 * @param devType - the type of the device which is to be redirected.<br>
	 * 						VMApp.DEVICE_TYPE_CDROM - for CD/DVD devices.<br>
	 * 						VMApp.DEVICE_TYPE_HD_USB - for Hard disk/USB devices.
	 */
	public StatusTable(int devType){
		tableData = new Object[TABLE_ROWS];
		switch (devType) {
		case VMApp.DEVICE_TYPE_CDROM:
			tableData[0] = LocaleStrings.getString("AJ_1_DCP");
			tableData[1] = LocaleStrings.getString("AL_8_ST");
			tableData[2] = LocaleStrings.getString("AL_8_ST");
			tableData[3] = LocaleStrings.getString("AL_8_ST");
			tableData[4] = LocaleStrings.getString("AL_8_ST");
			numInstnces = VMApp.getInstance().getNumCD();
			break;
		case VMApp.DEVICE_TYPE_HD_USB:
			tableData[0] = LocaleStrings.getString("AJ_3_DCP");
			tableData[1] = LocaleStrings.getString("AL_8_ST");
			tableData[2] = LocaleStrings.getString("AL_8_ST");
			tableData[3] = LocaleStrings.getString("AL_8_ST");
			tableData[4] = LocaleStrings.getString("AL_8_ST");
			numInstnces = VMApp.getInstance().getNumHD();
			break;
		}
		tableStringData = new String[numInstnces][TABLE_ROWS];

		//Update the status table based on the no of devices
		for(int devNo = 0; devNo < numInstnces; devNo++) {
			for(int tableRow = 0; tableRow < TABLE_ROWS;  tableRow++){
				if(tableRow == 0){
					String tData = (String) tableData[0];
					tableStringData[devNo][tableRow] = tData;
					tableStringData[devNo][tableRow] = tableStringData[devNo][tableRow].concat(" "+(devNo+1));
				}
				else{
					tableStringData[devNo][tableRow] = (String) tableData[tableRow];
				}
			}
		}

		statusTable = new JTable(tableStringData,columnNames) {
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int rowIndex, int vColIndex)
			{
				return false;
			}
		};

		statusTable.addMouseListener(new StatusTableMouseListener());
		statusTable.setShowGrid(false);
		statusTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		statusTable.setCellSelectionEnabled(false);
		statusTable.setRowSelectionAllowed(true);
		statusTable.setCellEditor(null);
		statusTable.setEnabled(true);
		initPopupMenu();
	}

	/**
	 * @return the statusTable
	 */
	public JTable getStatusTable() {
		return statusTable;
	}
	/**
	 * Creates the right click pop up menu which appears on the table.
	 */
	private void initPopupMenu(){
		popupMenu = new JVPopupMenu();
		popupMenu.addMenuItem(LocaleStrings.getString("AJ_5_DCP"), DISCONNECT, ' ', null);
		popupMenu.addActionListener(new StatusTablePopupActionListener());
	}
	/**
	 * The mouse listener object to be associated with the pop up menu.
	 * @author deepakmn
	 *
	 */
	private class StatusTableMouseListener extends MouseAdapter{
		public void mouseReleased(MouseEvent e) {
			JTable invoker = (JTable) e.getComponent();
			deviceIndex = invoker.getSelectedRow();
			if(e.isPopupTrigger()){
				// No rows selected
				if(invoker.getSelectedRow() < 0)
					return;
				//Get the device label given in the first column of the Status table
				String deviceLabel = (String) invoker.getModel().getValueAt(invoker.getSelectedRow(), 0);
				deviceLabel = deviceLabel.substring(0, (deviceLabel.indexOf(": ")+2));
				//Using the device label, find out the device type.
				//If device label is CD/DVD Media : 
				if(deviceLabel.equals(LocaleStrings.getString("AJ_1_DCP"))){
					deviceType = VMApp.DEVICE_TYPE_CDROM;
				}
				//If device label is Hard disk/USB Key Media : 
				else if(deviceLabel.equals(LocaleStrings.getString("AJ_3_DCP"))){
					deviceType = VMApp.DEVICE_TYPE_HD_USB;
				}
				else {
					Debug.out.println("Wrong Device Label : "+deviceLabel);
					return;
				}

				if(VMApp.getInstance().getIUSBRedirSession().getDeviceRedirStatus(deviceType, deviceIndex)==
						IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED){
					popupMenu.show(invoker, e.getX(), e.getY());
				}
			}
		}
	}
	/**
	 * The action listener associated with the menu item in the pop up menu.
	 * @author deepakmn
	 *
	 */
	private class StatusTablePopupActionListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals(DISCONNECT)){
				VMApp.getInstance().getRedirectionController().handleDeviceRedirection(deviceType, deviceIndex);
			}
		}
	}
}
