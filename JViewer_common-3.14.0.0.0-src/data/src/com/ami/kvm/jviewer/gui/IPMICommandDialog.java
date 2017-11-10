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
// This module implements the GUI using which user can send IPMI commands from
// JViewer to the BMC.
//
package com.ami.kvm.jviewer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.ami.kvm.jviewer.Debug;

public class IPMICommandDialog extends JDialog implements ActionListener{

	private final int WIDTH = 800;
	private final int HEIGHT = 500;

	private JPanel summaryPanel;
	private JPanel commandPanel;
	private JScrollPane summaryScrollPane;
	private JLabel commandLabel;
	private JTextArea hexCommandArea;
	private JTextArea asciiCommandArea;
	private JButton sendButton;
	private JButton clearButton;
	private JTable summaryTable;
	private DefaultTableModel summaryTableModel;
	private IPMICommandDialogKeyListener dialogKeyListener;
	private IPMICommandDialogMouseListener dialogMouseListener;
	private IPMICommandDialogPopupMenuActionListener popupMenuActionListener;
	protected static int SEQUENCE_LIMIT = 1000;
	private static Hashtable<Integer, String[]> savedSummary = null;
	private static byte sequenceData = 1;
	private static int sequenceNum = 1;
	private String errorStirng = null;
	private boolean scrollToBottom = false;
	private int scrollMax = 0;
	private JVPopupMenu popupMenu = null;
	private Component rightClickComponent = null;
	private JTextArea textAreaCell;

	/**
	 * IPMICommandDialog constructor
	 */
	public IPMICommandDialog(JFrame parent){
		super(parent, LocaleStrings.getString("AG_1_IPMI"), false);
		dialogKeyListener = new IPMICommandDialogKeyListener();
		dialogMouseListener = new IPMICommandDialogMouseListener();
		popupMenuActionListener = new IPMICommandDialogPopupMenuActionListener();
		initDialogPopupMenu();
	}

	/**
	 * Construct the IPMICommandDialog GUI
	 */
	public void showDialog(){
		Point dp =  JViewerApp.getInstance().getPopUpWindowPosition(WIDTH,HEIGHT);
		//Display softkeyboard respect to Jviewer window
		setBounds(new Rectangle(dp.x , dp.y , WIDTH, HEIGHT));
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		if(summaryPanel == null)
			summaryPanel = createSummaryPanel();
		getContentPane().add(summaryPanel, BorderLayout.CENTER);
		if(commandPanel == null)
			commandPanel = createCommandPanel();
		getContentPane().add(commandPanel, BorderLayout.SOUTH);
		if(savedSummary !=null)
			restoreSummary();
		setVisible(true);
		this.addWindowListener(new IPMICommandDialogWindowListener()); 
	}

	/**
	 * Create the components using which the command transfer summary is displayed. 
	 * @return JPanel summaryPanel
	 */
	private JPanel createSummaryPanel(){
		Color borderColor = new Color(237, 237 ,237);
		MatteBorder border;
		Object[][] tableData = new Object[][]{};
		String colNames[] = {LocaleStrings.getString("AG_9_IPMI"), LocaleStrings.getString("AG_10_IPMI")};
		summaryTableModel = new DefaultTableModel(tableData, colNames);
		summaryTable = new JTable(summaryTableModel){

			//set background color for request data
			public Component prepareRenderer(TableCellRenderer renderer,int rowIndex,
					int colIndex) {
				TextWrapTableCellRenderer tableCell;
				Border outside = new MatteBorder(0, 0, 2, 0, Color.GRAY);
				Border inside = new EmptyBorder(0, 1, 0, 1);
				Border highlight = new CompoundBorder(outside, inside);
				tableCell = (TextWrapTableCellRenderer) super.prepareRenderer(renderer, rowIndex, colIndex);
				if(rowIndex % 2 == 0){
					tableCell.setBackground(new Color(230, 230, 230));
					tableCell.setBorder(null);
				}
				else{
					tableCell.setBackground(Color.WHITE);
					tableCell.setBorder(highlight);
				}
				return tableCell;
			}
		};
		summaryTable.setShowHorizontalLines(false);
		summaryTable.setFocusable(false);
		summaryTable.setCellSelectionEnabled(true);
		summaryTable.setColumnSelectionAllowed(true);
		summaryTable.setRowSelectionAllowed(true);
		summaryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		summaryTable.addMouseListener(dialogMouseListener);
		setSummaryTableCellRenderer();
		setSummaryTableCellEditor();
		summaryScrollPane = new JScrollPane(summaryTable,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		summaryScrollPane.getVerticalScrollBar().addAdjustmentListener(new DialogAdjustmentListener());

		JPanel summaryPanel = new JPanel(new BorderLayout());
		border = new MatteBorder(5, 5, 5, 5, borderColor);
		summaryPanel.setBorder(border);
		summaryPanel.add(summaryScrollPane, BorderLayout.CENTER);
		return summaryPanel;
	}

	/**
	 * Set the TableCellRenderer for the summary table
	 */
	private void setSummaryTableCellRenderer(){
		TableCellRenderer cellRenderer = new TextWrapTableCellRenderer();
		for(int columnIndex = 0; columnIndex < summaryTable.getColumnCount(); columnIndex++)
			summaryTable.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
	}

	/**
	 * Set the TableCellEditor for the summary table
	 */
	private void setSummaryTableCellEditor(){
		TableCellEditor cellEditor = new TextCellEditor();
		for(int columnIndex = 0; columnIndex < summaryTable.getColumnCount(); columnIndex++)
			summaryTable.getColumnModel().getColumn(columnIndex).setCellEditor(cellEditor);
	}

	/**
	 * Create the components for sending IPMI commands to the BMC
	 * @return JPanel commandPanel
	 */
	private JPanel createCommandPanel(){
		Color borderColor = new Color(237, 237 ,237);
		MatteBorder border;
		JPanel commandPanel = new JPanel(new BorderLayout(10,10));
		commandLabel = new JLabel(LocaleStrings.getString("AG_2_IPMI"));
		commandPanel.add(commandLabel, BorderLayout.NORTH);
		commandPanel.add(createTextPanel(), BorderLayout.CENTER);
		commandPanel.add(createButtonPanel(), BorderLayout.SOUTH);
		border = new MatteBorder(5, 5, 5, 5, borderColor);
		commandPanel.setBorder(border);
		return commandPanel;
	}

	/**
	 * Create the panel that contains Hex and ASCII command text areas
	 * @return
	 */
	private JPanel createTextPanel(){
		JPanel textPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridCons = new GridBagConstraints();

		gridCons.fill = GridBagConstraints.HORIZONTAL;
		gridCons.insets = new Insets(1, 1, 1, 1);
		gridCons.gridx = 0;
		gridCons.gridy = 0;
		gridCons.gridwidth = 1;
		gridCons.gridheight = 1;
		gridCons.weightx = 1.0;
		gridCons.weighty = 1.0;
		JLabel hexLabel = new JLabel(LocaleStrings.getString("AG_9_IPMI"));
		hexLabel.setHorizontalAlignment(JLabel.CENTER);
		hexLabel.setVerticalAlignment(JLabel.CENTER);
		textPanel.add(hexLabel, gridCons);

		gridCons.insets = new Insets(1, 1, 2, 2);
		gridCons.gridy = 1;
		hexCommandArea = new JTextArea(2, 25);
		hexCommandArea.setAutoscrolls(true);
		hexCommandArea.setLineWrap(true);
		hexCommandArea.setWrapStyleWord(true);
		hexCommandArea.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		hexCommandArea.addKeyListener(dialogKeyListener);
		hexCommandArea.addMouseListener(dialogMouseListener);
		JScrollPane hexCommandScrollPane = new JScrollPane(hexCommandArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		textPanel.add(hexCommandScrollPane, gridCons);

		gridCons.insets = new Insets(1, 1, 1, 1);
		gridCons.gridx = 1;
		gridCons.gridy = 0;
		JLabel asciiLabel = new JLabel(LocaleStrings.getString("AG_10_IPMI"));
		asciiLabel.setHorizontalAlignment(JLabel.CENTER);
		asciiLabel.setVerticalAlignment(JLabel.CENTER);
		textPanel.add(asciiLabel, gridCons);

		gridCons.insets = new Insets(1, 2, 2, 1);
		gridCons.gridy = 1;
		asciiCommandArea = new JTextArea(2, 25);
		asciiCommandArea.setRows(2);
		asciiCommandArea.setAutoscrolls(true);
		asciiCommandArea.setLineWrap(true);
		asciiCommandArea.setWrapStyleWord(true);
		asciiCommandArea.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		asciiCommandArea.addKeyListener(dialogKeyListener);
		asciiCommandArea.addMouseListener(dialogMouseListener);

		JScrollPane asciiCommandScrollPane = new JScrollPane(asciiCommandArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		textPanel.add(asciiCommandScrollPane, gridCons);

		return textPanel;
	}

	/**
	 * Create a panel which contains the buttons
	 * @return buttonPanel
	 */
	private JPanel createButtonPanel(){
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gridCons = new GridBagConstraints();

		gridCons.fill = GridBagConstraints.HORIZONTAL;
		gridCons.insets = new Insets(1, 1, 1, 1);
		gridCons.gridx = 0;
		gridCons.gridy = 0;
		gridCons.gridwidth = 1;
		gridCons.gridheight = 1;
		gridCons.weightx = 1.0;
		buttonPanel.add(new JPanel(), gridCons);

		gridCons.weightx = 0.0;
		gridCons.gridx = 1;
		gridCons.insets = new Insets(1, 1, 1, 1);
		sendButton = new JButton(LocaleStrings.getString("AG_3_IPMI"));
		sendButton.addActionListener(this);
		sendButton.addKeyListener(dialogKeyListener);
		buttonPanel.add(sendButton, gridCons);

		gridCons.insets = new Insets(1, 5, 1, 1);
		gridCons.gridx = 2;
		clearButton = new JButton(LocaleStrings.getString("AG_12_IPMI"));
		clearButton.addActionListener(this);
		clearButton.addKeyListener(dialogKeyListener);
		buttonPanel.add(clearButton, gridCons);

		return buttonPanel;
	}

	/**
	 * Send the IPMI command to the BMC
	 */
	private void sendCommand(){
		String commandText = hexCommandArea.getText();

		if(commandText.length() > 0){
			int[] hexValues = formCommandArray(hexCommandArea.getText());
			byte[] commands = null;
			if(hexValues != null){
				commands = new byte[hexValues.length];
				for(int index = 0; index < hexValues.length; index++)
					commands[index] = (byte) hexValues[index];
			}

			if(commands == null && errorStirng != null){
				hexCommandArea.setText("");
				asciiCommandArea.setText("");
				InfoDialog.showDialog(this, errorStirng, LocaleStrings.getString("AG_6_IPMI"),
						InfoDialog.ERROR_DIALOG);
				errorStirng  = null;
			}
			else if(commands.length < 2){
				hexCommandArea.setText("");
				asciiCommandArea.setText("");
				errorStirng = LocaleStrings.getString("AG_11_IPMI");
				InfoDialog.showDialog(this, errorStirng, LocaleStrings.getString("AG_6_IPMI"),
						InfoDialog.ERROR_DIALOG);
				errorStirng = null;
			}
			else{
				JViewerApp.getInstance().onSendIPMICommand(sequenceData, commands);
				appendRequestText();
				sequenceData = (byte) ++sequenceNum;
				resetSequence();
			}
		}
	}

	private void resetSequence() {
		int sequenceLimit = JViewerApp.getOEMManager().getOEMIoemipmiCommandDialog().getOEMSEQUENCE_LIMIT();
		if(sequenceData > sequenceLimit){
			sequenceNum = 1;
			sequenceData = 1;
		}
	}

	/**
	 * Clear the command transfer summary
	 */
	private void clearSummary(){
		for(int row = summaryTable.getRowCount()-1; row >=0 ; row--)
			summaryTableModel.removeRow(row);
		sequenceNum = 1;
		sequenceData = 1;
	}
	/**
	 * Action Event handler
	 */
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource().equals(sendButton)){
			sendCommand();
		}
		else if(ae.getSource().equals(clearButton)){
			clearSummary();
		}
	}

	/**
	 * COnvert string input to an integer array of hexa decimal values
	 * @param commands
	 * @return
	 */
	private int[] formCommandArray(String commands){
		int[] hexValues = null;
		String command =  commands;
		String[]commandArray = null;
		try{
			if(command.contains("0x")){
				command = command.replaceAll("0x ", "");
				command = command.replaceAll("0x", "");
			}
			else if(command.startsWith(" ")){
				command = command.replaceFirst(" ", "");
			}
			else if (command.contains("\n")) {
				command = command.replaceAll("\n", "");
			}
			commandArray = command.split(" ");
			if(commandArray.length >= 1)
				hexValues = new int[commandArray.length];
			else{
				hexCommandArea.setText("");
				asciiCommandArea.setText("");
				return null;
			}
			for(int index = 0;index < commandArray.length; index++){
				try{
					commandArray[index] = commandArray[index].trim();
					hexValues[index] = Integer.parseInt(commandArray[index], 16);
				}catch(NumberFormatException nfe){
					errorStirng = LocaleStrings.getString("AG_6_IPMI")+" \""+commandArray[index]+"\".\n"+
							LocaleStrings.getString("AG_7_IPMI")+"\n"+
							"( "+LocaleStrings.getString("AG_8_IPMI")+"0x2a 0x1f )";
					Debug.out.println("Invalid command");
					Debug.out.println(nfe);
					return null;
				}
			}
		}catch (NullPointerException ne) {
			errorStirng = LocaleStrings.getString("AG_6_IPMI")+" \""+commands+"\".\n"+
					LocaleStrings.getString("AG_7_IPMI")+"\n"+
					"( "+LocaleStrings.getString("AG_8_IPMI")+"0x2a 0x1f )";
			Debug.out.println(ne);
			return null;
		}
		return hexValues;
	}

	/**
	 * Convert hexadecimal sequence to ASCII characters
	 * @param command - hexadecimal sequence
	 * @return ASCII string
	 */
	private String convertHexToASCII(String command){
		String asciiString = "";
		int[] intValues = formCommandArray(command);
		if(intValues == null)
			return null;
		for(int index = 0;index < intValues.length; index++){
			if(intValues[index] < 0x20)
				asciiString +=".    ";
			else
				asciiString += (char)intValues[index]+"    ";
			if(index > 0 && ((index+1)%16) == 0)
				asciiString += "\n";
		}
		return asciiString;
	}

	/**
	 * Handles IPMI command response message
	 * @param sequence - response packet sequence number
	 * @param response - response message from BMC
	 */
	public void onIPMICommandRespose(byte sequence, String response){
		appendResponseText((int)sequence, response);
	}
	/**
	 * Append request command being send to the command transfer summary.
	 */
	private void appendRequestText(){
		if(hexCommandArea.getText().equals(""))
			return;
		int rowIndex = (2*sequenceNum)-2;
		String decimalValues = convertHexToASCII(hexCommandArea.getText());
		if(decimalValues != null)
			decimalValues = "\n" + decimalValues;
		else
			decimalValues = "";
		Object[] requestData = new Object[]{LocaleStrings.getString("AG_4_IPMI")+" :\n"+hexCommandArea.getText(), decimalValues};
		summaryTableModel.insertRow(rowIndex, requestData);
		hexCommandArea.setText("");
		asciiCommandArea.setText("");
		scrollToBottom = true;
		summaryTable.setFocusable(true);
	}

	/**
	 * Append response message received from BMC to the command transfer summary. 
	 * @param sequence - response packet sequence number
	 * @param response - response message from BMC
	 */
	public void appendResponseText(int sequence, String response){
		int rowIndex = (2*sequence)-1;
		String decimalValues = convertHexToASCII(response);
		if(decimalValues != null)
			decimalValues = "\n" + decimalValues;
		else
			decimalValues = "";
		Object[] responseData = new Object[]{LocaleStrings.getString("AG_5_IPMI")+" :\n"+response, decimalValues};
		summaryTableModel.insertRow(rowIndex, responseData);
		scrollToBottom = true;
	}

	/**
	 * Save the IPMI command transfer summary. 
	 */
	private void saveCommandSummary(){
		int rowCount = summaryTableModel.getRowCount();
		int columnCount = summaryTableModel.getColumnCount();
		String[][] rowData = new String[rowCount][columnCount];
		savedSummary = new Hashtable<Integer, String[]>();
		for(int row = 0; row < rowCount; row++){
			for(int column =0; column < columnCount; column++)
				rowData[row][column] = (String) summaryTableModel.getValueAt(row, column);
			savedSummary.put(row, rowData[row]);
		}
	}

	/**
	 * Restore the IPMI command transfer summary
	 */
	private void restoreSummary(){
		if(summaryTableModel != null){
			int rowCount = savedSummary.size();
			String[] rowData;
			for(int row = 0; row < rowCount; row++){
				rowData = savedSummary.get(row);
				rowData = localizeSummaryData(rowData);
				summaryTableModel.addRow(rowData);
			}
		}
		savedSummary = null;
	}

	/**
	 * Localize the IPMI Command transfer summary data. 
	 * @param rowData - data to be localized
	 * @return
	 */
	private String[] localizeSummaryData(String[] rowData){
		if(rowData[0].startsWith(LocaleStrings.getPreviousLocaleString("AG_4_IPMI"))){
			rowData[0] = rowData[0].replaceAll(LocaleStrings.getPreviousLocaleString("AG_4_IPMI"), 
					LocaleStrings.getString("AG_4_IPMI"));
		}
		else if(rowData[0].startsWith(LocaleStrings.getPreviousLocaleString("AG_5_IPMI"))){
			rowData[0] = rowData[0].replaceAll(LocaleStrings.getPreviousLocaleString("AG_5_IPMI"), 
					LocaleStrings.getString("AG_5_IPMI"));
		}
		if(rowData[0].contains(LocaleStrings.getPreviousLocaleString("D_49_JVAPP"))){
			rowData[0] = rowData[0].replaceAll(LocaleStrings.getPreviousLocaleString("D_49_JVAPP"), 
					LocaleStrings.getString("D_49_JVAPP"));
		}
		return rowData;
	}

	/**
	 * Performs the close operation of the IPMI dialog.
	 */
	public void closeIPMICommandDialog(){
		saveCommandSummary();
		IPMICommandDialog nullDialog = null;
		JViewerApp.getInstance().setIPMIDialog(nullDialog);
		this.dispose();
	}
	/**
	 * Initialize the pop-up menu
	 */
	private void initDialogPopupMenu(){
		if(popupMenu == null){
			popupMenu = new JVPopupMenu();
			popupMenu.createEditPopup();
			popupMenu.addActionListener(popupMenuActionListener);
		}
	}

	private class IPMICommandDialogKeyListener extends KeyAdapter{
		public void keyPressed(KeyEvent ke){
			if(ke.getKeyCode() == KeyEvent.VK_ENTER){
				if(ke.getSource().equals(sendButton) || 
						ke.getSource().equals(hexCommandArea)||
						ke.getSource().equals(asciiCommandArea)){
					sendCommand();
					ke.consume();
				}
				else if(ke.getSource().equals(clearButton)){
					clearSummary();
					ke.consume();
				}
			}
		}
		public void keyReleased(KeyEvent ke){
			String asciiString = null;
			String hexString = "";
			char[] asciiArray;
			if(ke.getSource().equals(asciiCommandArea)){
				hexCommandArea.setText("");
				asciiString = asciiCommandArea.getText();
				asciiString = asciiString.replaceAll(" ", "");
				asciiArray = asciiString.toCharArray();
				for(int index = 0; index < asciiArray.length; index++)
					hexString += Integer.toHexString(asciiArray[index])+" ";
				hexCommandArea.setText(hexString);
			}
			else if(ke.getSource().equals(hexCommandArea)){
				asciiCommandArea.setText("");
				asciiString = convertHexToASCII(hexCommandArea.getText());
				asciiCommandArea.setText(asciiString);
			}
		}
	}

	private class IPMICommandDialogWindowListener extends WindowAdapter{
		public void windowClosing(WindowEvent e) {
			closeIPMICommandDialog();
		}
	}

	/**
	 * Customized TextCellRenderer 
	 */
	private class TextWrapTableCellRenderer extends JTextArea implements TableCellRenderer{  

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column){ 
			this.setText((String)value);
			this.setLineWrap(true);
			this.setWrapStyleWord(true);

			//set the JTextArea to the width of the table column
			setSize(table.getColumnModel().getColumn(column).getWidth(),getPreferredSize().height);
			if (table.getRowHeight(row) < getPreferredSize().height){
				//set the height of the table row to the calculated height of the JTextArea
				table.setRowHeight(row, getPreferredSize().height);
			}
			return this;
		}
	}

	private class TextCellEditor extends AbstractCellEditor implements TableCellEditor {

		Border outside = new MatteBorder(0, 0, 2, 0, Color.GRAY);
		Border inside = new EmptyBorder(0, 1, 0, 1);
		Border highlight = new CompoundBorder(outside, inside);

		public TextCellEditor() {
			textAreaCell = new JTextArea();
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int rowIndex, int vColIndex) {
			if(rowIndex % 2 == 0){
				textAreaCell.setBackground(new Color(230, 230, 230));
				textAreaCell.setBorder(null);
			}
			else{
				textAreaCell.setBackground(Color.WHITE);
				textAreaCell.setBorder(highlight);
			}

			textAreaCell.setLineWrap(true);
			textAreaCell.setText((String)value);
			textAreaCell.setEditable(false);
			textAreaCell.addMouseListener(dialogMouseListener);
			return textAreaCell;
		}

		public Object getCellEditorValue() {
			return ((JTextArea)textAreaCell).getText();
		}
	} 

	private class DialogAdjustmentListener implements AdjustmentListener{

		public void adjustmentValueChanged(AdjustmentEvent adj) {
			//Set the vertical scroll bar to the bottom when the table contents get updated.
			if(scrollToBottom ||
					scrollMax < summaryScrollPane.getVerticalScrollBar().getMaximum()){
				scrollMax = summaryScrollPane.getVerticalScrollBar().getMaximum();
				summaryScrollPane.getVerticalScrollBar().setValue(scrollMax);
				scrollToBottom = false;
			}
		}
	}

	private class IPMICommandDialogMouseListener extends MouseAdapter{

		public void mouseReleased(MouseEvent e) {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable clipboardContents = clipboard.getContents(null);
			rightClickComponent = e.getComponent();
			if(rightClickComponent != null){
				popupMenu.enableAll();
				if(rightClickComponent instanceof JTextArea){
					JTextArea textArea = (JTextArea) rightClickComponent;
					if(!textArea.isEditable()){
						popupMenu.getMenuItem(JVPopupMenu.CUT).setEnabled(false);
						popupMenu.getMenuItem(JVPopupMenu.PASTE).setEnabled(false);
					}
					if(textArea.getSelectedText() == null){
						popupMenu.getMenuItem(JVPopupMenu.CUT).setEnabled(false);
						popupMenu.getMenuItem(JVPopupMenu.COPY).setEnabled(false);
					}
					if ((clipboardContents.equals(null)) || !clipboardContents.isDataFlavorSupported(DataFlavor.stringFlavor))
						popupMenu.getMenuItem(JVPopupMenu.PASTE).setEnabled(false);
				}
				else{
					popupMenu.getMenuItem(JVPopupMenu.COPY).setEnabled(false);
					popupMenu.getMenuItem(JVPopupMenu.CUT).setEnabled(false);
					popupMenu.getMenuItem(JVPopupMenu.PASTE).setEnabled(false);
				}
				if(isPopupTrigger(e)){
					popupMenu.show(rightClickComponent, e.getX(), e.getY());
				}
			}
		}

		private boolean isPopupTrigger(MouseEvent e){
			boolean popupTrigger = false;
			if(e.isPopupTrigger())
				popupTrigger = true;
			return popupTrigger;
		}

	}

	private class IPMICommandDialogPopupMenuActionListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			if(rightClickComponent.equals(textAreaCell)){
				JTextArea textArea = (JTextArea) rightClickComponent;
				if(actionCommand.equals(JVPopupMenu.COPY)){
					textArea.copy();
				}
			}
			else if(rightClickComponent.equals(hexCommandArea) || rightClickComponent.equals(asciiCommandArea)){
				JTextArea textArea = (JTextArea)rightClickComponent;
				if(actionCommand.equals(JVPopupMenu.CUT)){
					textArea.cut();
				}
				else if(actionCommand.equals(JVPopupMenu.COPY)){
					textArea.copy();
				}
				else if(actionCommand.equals(JVPopupMenu.PASTE)){
					textArea.paste();
				}
			}
		}
	}
}
