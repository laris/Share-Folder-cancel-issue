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

package com.ami.kvm.jviewer.gui;

import java.awt.event.KeyEvent;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenuItem;

import com.ami.kvm.jviewer.Debug;

public class AddMacro {

	private ByteBuffer macroBuffer;
	public static final int MACRO_COUNT = 20;
	public static final int SINGLE_MACRO_LENGTH = 6;
	public static final int SIZE_OF_MACRO = 960;//{20*6*2*4}//Macro count * single macro length * size of single key * size of int
	protected HashMap<Integer, Integer> standardMapInvert;
	protected HashMap<Integer, Integer> keypadMapInvert;
	protected HashMap<Integer, Integer> leftMapInvert;
	protected HashMap<Integer, Integer> rightMapInvert;
	protected HashMap<String, String> macroMap;

	public static final String UNKNOWN_KEY = "Unknown";	

	/**
	 * Constructor
	 * @param macroBuffer - Byte buffer containing the macro key codes and key location values
	 */
	public AddMacro (ByteBuffer macroBuffer)
	{
		this.macroBuffer = macroBuffer;
		if(macroMap == null)
			macroMap = new HashMap<String, String>();
	}

	/**
	 * Parse the key code and key location location values from the
	 * byte buffer and populate the macro map with the user defined macros and 
	 * their key events.  
	 */
	public void parseDataToMenu()
	{
		macroBuffer.position(0);
		macroBuffer.order(ByteOrder.BIG_ENDIAN);

		for(int i=0;i<MACRO_COUNT;i++)
		{
			String macroString= new String();
			String macroKeycode = new String();

			for(int j=0;j<SINGLE_MACRO_LENGTH;j++)
			{
				try{
					int code =(int)macroBuffer.getInt();	
					int keyLocation = (int)macroBuffer.getInt();
					
					if(code != 0 && keyLocation != 0) {
						String keyText = KeyEvent.getKeyText(code);
						//CHNAGE THIS CONDITION TO AVOID INVALID KEY CODE
						if(keyText.startsWith(UNKNOWN_KEY)){
							keyText ="";
							Debug.out.println("UNKNOWN Code : "+code);
							break;
						}
						if(keyText.equals("NumPad +")){
							keyText = "NumPad Plus";
						}
						if(macroString.length() != 0){
							macroString = macroString.concat("+");
						}
						if(keyLocation != KeyEvent.KEY_LOCATION_STANDARD)
						{
							if(keyLocation == KeyEvent.KEY_LOCATION_LEFT){
								keyText=keyText.concat("(Left)");
							}
							else if(keyLocation == KeyEvent.KEY_LOCATION_RIGHT){
								keyText=keyText.concat("(Right)");
							}
						}
						macroString = macroString.concat(keyText);

						String CodeString = new String(Integer.toString(code));
						String keylocationString = new String(Integer.toString(keyLocation));
						if(macroKeycode.length() != 0)
							macroKeycode = macroKeycode.concat("+");
						macroKeycode = macroKeycode.concat(CodeString);
						macroKeycode = macroKeycode.concat("+"+keylocationString);
					}

				}catch (BufferOverflowException boe) {
					Debug.out.println(boe);
				}
				catch(BufferUnderflowException bue){
					Debug.out.println(bue);
				}
			}	
			if(macroString.length() != 0 && macroKeycode.length() != 0)
				macroMap.put(macroString, macroKeycode);
		}
		removeMacroMenu();
		removeToolbarMacro();
		addMacroMenu(macroMap);
		addToolbarMacro(macroMap);
	}

	/**
	 * Create the user defined macro menu items for each macro in the macroMap,
	 * and add them to the macro menu.
	 * @param macroMap2 - The hash map containing the macros and 
	 * their corresponding key events
	 */
	public void addMacroMenu(HashMap<String, String> macroMap2) {
		JMenuItem menuItem;
		if(macroMap2.size() > 0)
		{
			Set set = macroMap.entrySet();
			Iterator i = set.iterator();
			JViewerApp.getInstance().getJVMenu().getMacroSubMenu().addSeparator();
			while(i.hasNext()){
				Map.Entry me = (Map.Entry)i.next();
				String menustring = (String) me.getKey();
				menuItem = new JMenuItem(menustring);
				menuItem.addActionListener(JViewerApp.getInstance().getJVMenu().m_menuListener);
				menuItem.addMouseListener(JViewerApp.getInstance().getJVMenu().m_menuStatus);
				menuItem.setActionCommand("HK_"+menustring);
				JViewerApp.getInstance().getJVMenu().getMacroSubMenu().add(menuItem,2);
				JViewerApp.getInstance().getJVMenu().m_menuItems.put(menustring, menuItem);
				JViewerApp.getInstance().getJVMenu().m_menuItems_setenabled.put(menustring, true);
				//In case of partial permission or redirection paused, disable the menuitems
				if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_PARTIAL 
						|| JViewerApp.getInstance().isM_userPause()){
					menuItem.setEnabled(false);
					JViewerApp.getInstance().getJVMenu().m_menuItems_setenabled.put(menustring, false);
				}
			}
		}
	}

	/**
	 * Create the user defined macro menu items for each macro in the macroMap,
	 * and add them to the Hotkey button popup menu on the toolbar.
	 * @param macroMap2 - The hash map containing the macros and 
	 * their corresponding key events
	 */
	public void addToolbarMacro(HashMap<String, String> macroMap2) {
		if(macroMap2.size() > 0)
		{
			Set set = macroMap.entrySet();
			Iterator i = set.iterator();
			JViewerApp.getInstance().getM_wndFrame().getToolbar().getM_popupMenu().addSeparator();
			while(i.hasNext()){
				Map.Entry me = (Map.Entry)i.next();
				String menustring = (String) me.getKey();
				JViewerApp.getInstance().getM_wndFrame().getToolbar().addHotkeyPoupMenuItem(menustring);
			}
		}
	}
	/**
	 * Removes the user defined macro menu items from the macro menu. 
	 */
	public void removeMacroMenu() {
		JViewerApp.getInstance().getJVMenu().getMacroSubMenu().removeAll();
		JMenuItem menuItem = JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_ADD_HOTKEYS);
		JViewerApp.getInstance().getJVMenu().getMacroSubMenu().add(menuItem);
	}

	/**
	 * Remove the user defined macro menuitems from the popup menu on the Hotkey button
	 * on the toolbar.
	 */
	public void removeToolbarMacro(){
		JViewerApp.getInstance().getM_wndFrame().getToolbar().removeHotkeyPoupMenuItem();
	}
	/**
	 * Parse the  key events available inn the macroMap, and send them to the BMC. 
	 */
	public void parseKeycodeSendBMC()
	{
		byte keycode[] = new byte[SIZE_OF_MACRO];
		Arrays.fill(keycode,(byte) 0);
		ByteBuffer keyevent =  ByteBuffer.wrap(keycode);
		Set set = macroMap.entrySet();
		Iterator i = set.iterator();
		int loop=1;
		while(i.hasNext()){
			Map.Entry me = (Map.Entry)i.next();
			String KeyCode = (String) me.getValue();
			String[] split_keyevent = KeyCode.split("[+]");

			for(int len=0;len<split_keyevent.length;len++)
			{
				if(len < split_keyevent.length )
				{
					int data = Integer.parseInt(split_keyevent[len]);
					keyevent.putInt(data);
				}
			}
			//move position to next macro 
			//value 12 = 6*2 (SINGLE_MACRO_LENGTH * SIZE OF KEY)[Each key occupies 2 position(1 for key code another one for key location)]
			//4 represents size of each key (int value)
			keyevent.position((loop*(12)*4));
			loop++;
		}
		JViewerApp.getInstance().getKVMClient().sendUserMacroData(keycode);
	}

	/**
	 * Returns the byte buffer storing the macro data
	 * @return - the macro buffer.
	 */
	public ByteBuffer getMacroBuffer() {
		return macroBuffer;
	}

	/**
	 * Sets the byte buffer storing the macro data.
	 * @param macroBuffer - the macro buffer object to be set.
	 */
	public void setMacroBuffer(ByteBuffer macroBuffer) {
		this.macroBuffer = macroBuffer;
	}

	/**
	 * Returns the HashMap storing the macro data
	 * @return - the macro buffer.
	 */
	public HashMap<String, String> getMacroMap() {
		return macroMap;
	}

	/**
	 * Sets the HashMap storing the macro data.
	 * @param macroBuffer - the macro buffer object to be set.
	 */
	public void setMacroMap(HashMap<String, String> macroMap2) {
		macroMap = macroMap2;
	}

}
