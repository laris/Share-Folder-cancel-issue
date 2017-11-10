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

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.hid.KeyProcessor;
import com.ami.kvm.jviewer.hid.USBKeyProcessorEnglish;
import com.ami.kvm.jviewer.hid.USBKeyboardRep;
import com.ami.kvm.jviewer.kvmpkts.KVMClient;


@SuppressWarnings("serial")
public class SoftKeyboard extends JDialog {
	private JPanel JDialogContentPane;
	public JButton m_butKey[];
	public JToggleButton m_toggleKey[];
	private JButton japSpecialKeys[];
	private static int keycount;
	private USBKeyboardRep keyRep;
	KVMClient m_KVMClnt = JViewerApp.getInstance().getKVMClient();
	private Hashtable<Integer, Integer> m_ps2;
	private static int lngindex;
	SKMouseListener m_skmouselistener;	
	public boolean num_Firsttime = true;
	private boolean altGr_enable = false;
	byte clientKybdLED;
	public static boolean Japaneskeyflag = false;
	public static boolean HiraganaPRessed = true;
	
	private final int WIDTH = 622;
	private final int HEIGHT = 220;

	public static final int NUMLOCK = 0x01;
	public static final int CAPSLOCK = 0x02;
	public static final int SCROLLLOCK = 0x04;
	private static final HashSet<Integer> EXCEPT_LAYOUTS = new HashSet<Integer>(
			Arrays.asList(new Integer[] {JVMenu.LANGUAGE_DANISH, JVMenu.LANGUAGE_DUTCH_NL,
					JVMenu.LANGUAGE_FINNISH, JVMenu.LANGUAGE_GERMAN_GER,
					JVMenu.LANGUAGE_NORWEGIAN_NOR, JVMenu.LANGUAGE_RUSSIAN,
					JVMenu.LANGUAGE_SWEDISH, JVMenu.LANGUAGE_TURKISH_F,
					JVMenu.LANGUAGE_TURKISH_Q}));
	
	private String keylabel[] = { "esc", "F1", "F2", "F3", "F4", "F5", "F6",
			"F7", "F8", "F9", "F10", "F11", "F12", "psc", "slk", "brk" };

	private String modkeys[] = { "tab", "caps", "shift", "ctrl", "win", "alt",
			" ", "alt", "win", "ctrl", "shift", "ent", "bksp" };

	private String otherkeys[] = { "ins", "hm", "pup", "nlk", "/", "*", "-",
			"del", "end", "pdn", "^", "<=", "v", "=>", "+", "ent" };

	private String numpadkeyson = "7894561230.";

	private String numpadkeysoff[] = { "hm", "up", "pup", "lft", " ", "rgt",
			"end", "dn", "pdn", "Ins", "Del" };

	private int numpadkeysoffevent[] = { KeyEvent.VK_HOME, KeyEvent.VK_UP,
			KeyEvent.VK_PAGE_UP, KeyEvent.VK_LEFT, 0, KeyEvent.VK_RIGHT,
			KeyEvent.VK_END, KeyEvent.VK_DOWN, KeyEvent.VK_PAGE_DOWN,
			KeyEvent.VK_INSERT, KeyEvent.VK_DELETE };
	/**
	 * SoftKeyboard constructor comment.
	 */
	public SoftKeyboard(final int langindex,JFrame frame) {
		super(frame, null, false);
		lngindex = langindex;
		keycount = 63;		
		m_butKey = new JButton[98];// 1 more key for slk which would not be used
		m_toggleKey = new JToggleButton[11];// 92+11=103
		japSpecialKeys = new JButton[4];
		m_skmouselistener = new SKMouseListener();		
		keyRep = new USBKeyboardRep();
		keyRep.setM_USBKeyProcessor(new USBKeyProcessorEnglish());
		keyRep.setAutoKeybreakMode(true);
		m_KVMClnt = JViewerApp.getInstance().getKVMClient();
		m_ps2 = new Hashtable<Integer, Integer>();
		fillhashtable();
		displaykeypad();
	}


	public static void main(final String args[]) {
		//final SoftKeyboard sample = new SoftKeyboard(5);
	}

	public void fillhashtable() {
		m_ps2.put(0, KeyEvent.VK_ESCAPE);
		m_ps2.put(1, KeyEvent.VK_F1);
		m_ps2.put(2, KeyEvent.VK_F2);
		m_ps2.put(3, KeyEvent.VK_F3);
		m_ps2.put(4, KeyEvent.VK_F4);
		m_ps2.put(5, KeyEvent.VK_F5);
		m_ps2.put(6, KeyEvent.VK_F6);
		m_ps2.put(7, KeyEvent.VK_F7);
		m_ps2.put(8, KeyEvent.VK_F8);
		m_ps2.put(9, KeyEvent.VK_F9);
		m_ps2.put(10, KeyEvent.VK_F10);
		m_ps2.put(11, KeyEvent.VK_F11);
		m_ps2.put(12, KeyEvent.VK_F12);
		m_ps2.put(13, KeyEvent.VK_PRINTSCREEN);
		m_ps2.put(14, KeyEvent.VK_SCROLL_LOCK);
		m_ps2.put(15, KeyEvent.VK_PAUSE);
		m_ps2.put(16, KeyEvent.VK_BACK_QUOTE);
		m_ps2.put(17, KeyEvent.VK_1);
		m_ps2.put(18, KeyEvent.VK_2);
		m_ps2.put(19, KeyEvent.VK_3);
		m_ps2.put(20, KeyEvent.VK_4);
		m_ps2.put(21, KeyEvent.VK_5);
		m_ps2.put(22, KeyEvent.VK_6);
		m_ps2.put(23, KeyEvent.VK_7);
		m_ps2.put(24, KeyEvent.VK_8);
		m_ps2.put(25, KeyEvent.VK_9);
		m_ps2.put(26, KeyEvent.VK_0);
		m_ps2.put(27, KeyEvent.VK_MINUS);
		m_ps2.put(28, KeyEvent.VK_EQUALS);
		m_ps2.put(29, KeyEvent.VK_Q);
		m_ps2.put(30, KeyEvent.VK_W);
		m_ps2.put(31, KeyEvent.VK_E);
		m_ps2.put(32, KeyEvent.VK_R);
		m_ps2.put(33, KeyEvent.VK_T);
		m_ps2.put(34, KeyEvent.VK_Y);
		m_ps2.put(35, KeyEvent.VK_U);
		m_ps2.put(36, KeyEvent.VK_I);
		m_ps2.put(37, KeyEvent.VK_O);
		m_ps2.put(38, KeyEvent.VK_P);
		m_ps2.put(39, KeyEvent.VK_OPEN_BRACKET);
		m_ps2.put(40, KeyEvent.VK_CLOSE_BRACKET);
		m_ps2.put(41, KeyEvent.VK_BACK_SLASH);
		m_ps2.put(42, KeyEvent.VK_A);
		m_ps2.put(43, KeyEvent.VK_S);
		m_ps2.put(44, KeyEvent.VK_D);
		m_ps2.put(45, KeyEvent.VK_F);
		m_ps2.put(46, KeyEvent.VK_G);
		m_ps2.put(47, KeyEvent.VK_H);
		m_ps2.put(48, KeyEvent.VK_J);
		m_ps2.put(49, KeyEvent.VK_K);
		m_ps2.put(50, KeyEvent.VK_L);
		m_ps2.put(51, KeyEvent.VK_SEMICOLON);
		m_ps2.put(52, KeyEvent.VK_QUOTE);
		m_ps2.put(53, KeyProcessor.VK_102KEY);
		m_ps2.put(54, KeyEvent.VK_Z);
		m_ps2.put(55, KeyEvent.VK_X);
		m_ps2.put(56, KeyEvent.VK_C);
		m_ps2.put(57, KeyEvent.VK_V);
		m_ps2.put(58, KeyEvent.VK_B);
		m_ps2.put(59, KeyEvent.VK_N);
		m_ps2.put(60, KeyEvent.VK_M);
		m_ps2.put(61, KeyEvent.VK_COMMA);
		m_ps2.put(62, KeyEvent.VK_PERIOD);
		m_ps2.put(63, KeyEvent.VK_SLASH);
		m_ps2.put(64, KeyEvent.VK_TAB);
		m_ps2.put(65, KeyEvent.VK_CAPS_LOCK);
		m_ps2.put(66, KeyEvent.VK_SHIFT);
		m_ps2.put(67, KeyEvent.VK_CONTROL);
		m_ps2.put(68, KeyEvent.VK_WINDOWS);
		m_ps2.put(69, KeyEvent.VK_ALT);
		m_ps2.put(70, KeyEvent.VK_SPACE);
		m_ps2.put(71, KeyEvent.VK_ALT);
		m_ps2.put(72, KeyEvent.VK_WINDOWS);
		m_ps2.put(73, KeyEvent.VK_CONTROL);
		m_ps2.put(74, KeyEvent.VK_SHIFT);
		m_ps2.put(75, KeyEvent.VK_ENTER);
		m_ps2.put(76, KeyEvent.VK_BACK_SPACE);
		m_ps2.put(77, KeyEvent.VK_INSERT);
		m_ps2.put(78, KeyEvent.VK_HOME);
		m_ps2.put(79, KeyEvent.VK_PAGE_UP);
		m_ps2.put(80, KeyEvent.VK_NUM_LOCK);
		m_ps2.put(81, 111);
		m_ps2.put(82, 106);// numpad *
		m_ps2.put(83, 109);// numpad -
		m_ps2.put(84, KeyEvent.VK_DELETE);
		m_ps2.put(85, KeyEvent.VK_END);
		m_ps2.put(86, KeyEvent.VK_PAGE_DOWN);
		m_ps2.put(87, KeyEvent.VK_UP);
		m_ps2.put(88, KeyEvent.VK_LEFT);
		m_ps2.put(89, KeyEvent.VK_DOWN);
		m_ps2.put(90, KeyEvent.VK_RIGHT);
		m_ps2.put(91, 107);// numpad +
		m_ps2.put(92, KeyEvent.VK_ENTER);
		m_ps2.put(93, 103);// ...... 7
		m_ps2.put(94, 104);// ...... 8
		m_ps2.put(95, 105);// ...... 9
		m_ps2.put(96, 100);// ...... 4
		m_ps2.put(97, 101);// ...... 5
		m_ps2.put(98, 102);// ...... 6
		m_ps2.put(99, 97);// numpad 1
		m_ps2.put(100, 98);// ...... 2
		m_ps2.put(101, 99);// ...... 3
		m_ps2.put(102, 96);// ...... 0
		m_ps2.put(103, 110);// numpad .
		m_ps2.put(104, 999);
	}

	public void displaykeypad() {
		int i, j, k, m, x, y, p, q, z, w;
		i = j = k = m = x = y = p = q = z = w = 0;
		Font f;
		if (System.getProperty("os.name").equals("Linux")) {
			f = new Font("Dialog", Font.BOLD, 10);
		} else {
			f = new Font("Dialog", Font.BOLD, 12);
		}
		// Container (Panel) creation
		JDialogContentPane = new JPanel();
		JDialogContentPane.setName("JDialogContentPane");
		JDialogContentPane.setLayout(null);

		// Displaying keys as specified below
		for (i = 0; i < 64; i++) {
			if (i == 14) { // Scroll Lock
				m_toggleKey[0] = new JToggleButton();
				m_toggleKey[0].setName("Key" + i);
				m_toggleKey[0].setFont(f);
				m_toggleKey[0].setText(keylabel[i]);
				m_toggleKey[0].setBounds(8 + j, 9 + k, 27, 27);
				m_toggleKey[0].setMargin(new java.awt.Insets(0, 0, 0, 0));
				m_toggleKey[0].setHorizontalTextPosition(SwingConstants.CENTER);
				m_toggleKey[0].setVerticalTextPosition(SwingConstants.BOTTOM);
				m_toggleKey[0].addMouseListener(m_skmouselistener);
				
				m_toggleKey[0].setVisible(true);
				JDialogContentPane.add(m_toggleKey[0], m_toggleKey[0].getName());
				j += 27;
			} else {

					if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q ||
							lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
							lngindex == JVMenu.LANGUAGE_JAPANESE_K) {
						if (i < 16) {
							m_butKey[i] = new JButton();
							m_butKey[i].setName("Key" + i);
							m_butKey[i].setFont(f);
							m_butKey[i].setText(keylabel[i]);
						}
						else if(i == 16){
								japSpecialKeys[3] = new JButton();
								japSpecialKeys[3].setName("Key" + i);
								japSpecialKeys[3].setText("半/全");
								japSpecialKeys[3].setFont(new Font("Dialog", Font.PLAIN, 9));
								japSpecialKeys[3].setBounds(8 + j, 9 + k, 27, 27);
								japSpecialKeys[3].setMargin(new java.awt.Insets(0, 0, 0, 0));
								japSpecialKeys[3].setHorizontalTextPosition(SwingConstants.CENTER);
								japSpecialKeys[3].setVerticalTextPosition(SwingConstants.BOTTOM);
								japSpecialKeys[3].addMouseListener(m_skmouselistener);							
								japSpecialKeys[3].setVisible(true);
								JDialogContentPane.add(japSpecialKeys[3], japSpecialKeys[3].getName());
							j += 27;
							continue;
						}
						else {
							m_butKey[i] = new JButton();
							m_butKey[i].setName("Key" + i);
							m_butKey[i].setFont(f);
							// first key is absent in japanese language
							m_butKey[i].setText(String.valueOf(KeyProcessor.normalCharSet[lngindex]
									.charAt((i - 17))));// keylabel ends in 15
						}
						if(i > 16 && i < 29){
							if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q || lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
									lngindex == JVMenu.LANGUAGE_JAPANESE_K){
								m_butKey[i].setBounds(8 + j, 9 + k, 25, 27);
							}
							else
								m_butKey[i].setBounds(8 + j, 9 + k, 27, 27);
						}
						else
						m_butKey[i].setBounds(8 + j, 9 + k, 27, 27);
						m_butKey[i].setMargin(new java.awt.Insets(0, 0, 0, 0));
						m_butKey[i]
								.setHorizontalTextPosition(SwingConstants.CENTER);
						m_butKey[i].setVerticalTextPosition(SwingConstants.BOTTOM);
						m_butKey[i].addMouseListener(m_skmouselistener);
						m_butKey[i].setVisible(true);
						JDialogContentPane.add(m_butKey[i], m_butKey[i].getName());
					} else {
						m_butKey[i] = new JButton();
						m_butKey[i].setName("Key" + i);
						m_butKey[i].setFont(f);
						if (i < 16) {
							m_butKey[i].setText(keylabel[i]);
						} else {
							m_butKey[i].setText(String.valueOf(KeyProcessor.normalCharSet[lngindex].charAt((i - 16))));// keylabel ends in 15
						}
						m_butKey[i].setBounds(8 + j, 9 + k, 27, 27);
						m_butKey[i].setMargin(new java.awt.Insets(0, 0, 0, 0));
						m_butKey[i].setHorizontalTextPosition(SwingConstants.CENTER);
						m_butKey[i].setVerticalTextPosition(SwingConstants.BOTTOM);
						m_butKey[i].addMouseListener(m_skmouselistener);
						m_butKey[i].setVisible(true);
						JDialogContentPane.add(m_butKey[i], m_butKey[i].getName());
					}
				switch (i) {
				case 0:// starting from escape
					j += 43;
					break;
				case 4:// for space between escape and F1
					j += 38;
					break;
				case 8:// for space between F4 and F5
					j += 38;
					break;
				case 12:// for space between F8 and F9
					j += 38;
					break;
				case 15:// for printing second row starting from "~"
					k += 38;
					j = 0;
					break;
				case 28:// for printing third row starting from "Q"
					k += 28;
					j = 38;
					break;
				case 41:// for printing fourth row starting from "A"
					k += 28;
					j = 46;
					break;
				case 52:// for printing fifth row starting from "Z"
					k += 28;
					j = 32;
					break;
				default:// for default spacing between keys
					if(i > 16 && i < 29){
						if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q || lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
								lngindex == JVMenu.LANGUAGE_JAPANESE_K){
							j+=25;
						}
						else
							j += 27;
					}
					else
						j += 27;
					break;
				}
			}
		}

		// For displaying Modifier keys
		for (m = 64; m < 77; m++) {
			// System.out.println("Key ID : " + m + " Key Label : "+ modkeys[m -
			// 63]);
			if (m == 65 || m == 66 || m == 67 || m == 68 || m == 69 || m == 71
					|| m == 72 || m == 73 || m == 74) {
				if (m >= 71) {
					m_toggleKey[m - 65] = new JToggleButton();
					m_toggleKey[m - 65].setName("Key" + m);
					m_toggleKey[m - 65].setFont(f);
					m_toggleKey[m - 65].setText(modkeys[m - 64]);
				} else {
					m_toggleKey[m - 64] = new JToggleButton();
					m_toggleKey[m - 64].setName("Key" + m);
					m_toggleKey[m - 64].setFont(f);
					m_toggleKey[m - 64].setText(modkeys[m - 64]);
				}
				switch (m) {
				case 65:// caps lock
					m_toggleKey[m - 64].setBounds(8, 103, 46, 27);
					break;
				case 66:// left shift
					if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q ||
							lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
							lngindex == JVMenu.LANGUAGE_JAPANESE_K)
						m_toggleKey[m - 64].setBounds(8, 131, 59, 27);
					else
						m_toggleKey[m - 64].setBounds(8, 131, 32, 27);
					break;
				case 67:// left ctrlt
					m_toggleKey[m - 64].setBounds(8, 159, 38, 27);
					break;
				case 68:// left windows
					m_toggleKey[m - 64].setBounds(46, 159, 27, 27);
					break;
				case 69:// left alt
					m_toggleKey[m - 64].setBounds(73, 159, 30, 27);
					break;
				case 71:// right alt
					m_toggleKey[m - 65].setBounds(302, 159, 30, 27);
					break;
				case 72:// right windows
					m_toggleKey[m - 65].setBounds(332, 159, 27, 27);
					break;
				case 73:// right ctrlt
					m_toggleKey[m - 65].setBounds(359, 159, 38, 27);
					break;
				case 74:// right shift
					if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q ||
							lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
							lngindex == JVMenu.LANGUAGE_JAPANESE_K)
						m_toggleKey[m - 65].setBounds(364, 131, 33, 27);
					else
						m_toggleKey[m - 65].setBounds(337, 131, 60, 27);
					break;
				}
				if (m >= 70) {
					m_toggleKey[m - 65].setMargin(new java.awt.Insets(0, 0, 0, 0));
					m_toggleKey[m - 65].setHorizontalTextPosition(SwingConstants.CENTER);
					m_toggleKey[m - 65].setVerticalTextPosition(SwingConstants.BOTTOM);
					m_toggleKey[m - 65].addMouseListener(m_skmouselistener);
					m_toggleKey[m - 65].setVisible(true);				
									
					JDialogContentPane.add(m_toggleKey[m - 65], m_toggleKey[m - 65].getName());
				} else {
					m_toggleKey[m - 64].setMargin(new java.awt.Insets(0, 0, 0, 0));
					m_toggleKey[m - 64].setHorizontalTextPosition(SwingConstants.CENTER);
					m_toggleKey[m - 64].setVerticalTextPosition(SwingConstants.BOTTOM);
					m_toggleKey[m - 64].addMouseListener(m_skmouselistener);
					m_toggleKey[m - 64].setVisible(true);
					JDialogContentPane.add(m_toggleKey[m - 64], m_toggleKey[m - 64].getName());				
				}
			} else {
				keycount++;
				m_butKey[keycount] = new JButton();
				m_butKey[keycount].setName("Key" + m);
				m_butKey[keycount].setFont(f);
				m_butKey[keycount].setText(modkeys[m - 64]);
				switch (m) {
				case 64:// tab
					m_butKey[keycount].setBounds(8, 75, 38, 27);
					break;
				case 70:// space bar
					if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q ||
							lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
							lngindex == JVMenu.LANGUAGE_JAPANESE_K)
						m_butKey[keycount].setBounds(153, 159, 55, 27);
					else
						m_butKey[keycount].setBounds(103, 159, 169, 27);
					break;
				case 75:// Enter
					m_butKey[keycount].setBounds(351, 103, 46, 27);
					break;
				case 76:// BackSpace
					m_butKey[keycount].setBounds(359, 47, 38, 27);
					break;
				}
				m_butKey[keycount].setMargin(new java.awt.Insets(0, 0, 0, 0));
				m_butKey[keycount].setHorizontalTextPosition(SwingConstants.CENTER);
				m_butKey[keycount].setVerticalTextPosition(SwingConstants.BOTTOM);
				m_butKey[keycount].addMouseListener(m_skmouselistener);
				m_butKey[keycount].setVisible(true);
				JDialogContentPane.add(m_butKey[keycount], m_butKey[keycount].getName());
			}
		}

		// For Displaying otherkeys
		for (x = 77; x < 93; x++) {
			if (x == 80)// Num Lock
			{
				m_toggleKey[10] = new JToggleButton();
				m_toggleKey[10].setName("Key" + x);
				m_toggleKey[10].setFont(f);
				m_toggleKey[10].setText(otherkeys[x - 77]);
				m_toggleKey[10].setBounds(q + 408, p + 47, 27, 27);
				q += 27;
				m_toggleKey[10].setMargin(new java.awt.Insets(0, 0, 0, 0));
				m_toggleKey[10].setHorizontalTextPosition(SwingConstants.CENTER);
				m_toggleKey[10].setVerticalTextPosition(SwingConstants.BOTTOM);
				m_toggleKey[10].addMouseListener(m_skmouselistener);
				m_toggleKey[10].setRolloverEnabled(true);// ROLLOVER_ENABLED_CHANGED_PROPERTY;
				m_toggleKey[10].setVisible(true);
				JDialogContentPane.add(m_toggleKey[10], m_toggleKey[10].getName());
			} else {
				keycount++;
				m_butKey[keycount] = new JButton();
				m_butKey[keycount].setName("Key" + x);
				m_butKey[keycount].setFont(f);
				m_butKey[keycount].setText(otherkeys[x - 77]);
				if (x == 91 || x == 92)
					m_butKey[keycount].setBounds(q + 408, p + 47, 27, 55);
				else
					m_butKey[keycount].setBounds(q + 408, p + 47, 27, 27);
				switch (x) {
				case 79:// num lock
					q += 38;
					break;
				case 83:// "del"
					q = 0;
					p += 28;
					break;
				case 86:// "up arrow"
					q = 27;
					p += 56;
					break;
				case 87:// "left arrow"
					q = 0;
					p += 28;
					break;
				case 90:// "+"
					q = 173;
					p = 28;
					break;
				case 91:// "ent"
					q = 173;
					p += 56;
					break;
				default:
					q += 27;
					break;
				}
				m_butKey[keycount].setMargin(new java.awt.Insets(0, 0, 0, 0));
				m_butKey[keycount].setHorizontalTextPosition(SwingConstants.CENTER);
				m_butKey[keycount].setVerticalTextPosition(SwingConstants.BOTTOM);
				m_butKey[keycount].addMouseListener(m_skmouselistener);
				m_butKey[keycount].setRolloverEnabled(true);// ROLLOVER_ENABLED_CHANGED_PROPERTY;
				m_butKey[keycount].setVisible(true);
				JDialogContentPane.add(m_butKey[keycount], m_butKey[keycount].getName());
			}
		}

		// for Numpad keys
		for (y = 93; y < 104; y++) {

			keycount++;
			m_butKey[keycount] = new JButton();
			m_butKey[keycount].setName("Key" + y);
			m_butKey[keycount].setFont(f);
			if (m_toggleKey[10].isSelected() == true)
				m_butKey[keycount].setText(String.valueOf(numpadkeyson.charAt(y - 93)));
			else
				m_butKey[keycount].setText(numpadkeysoff[y - 93]);
			if (y == 102)
				m_butKey[keycount].setBounds(w + 500, z + 75, 54, 27);
			else
				m_butKey[keycount].setBounds(w + 500, z + 75, 27, 27);
			switch (y) {
			case 95:
			case 98:
			case 101:
				w = 0;
				z += 28;
				break;
			case 102:
				w += 54;
				break;
			default:
				w += 27;
				break;
			}
			m_butKey[keycount].setMargin(new java.awt.Insets(0, 0, 0, 0));
			m_butKey[keycount].setHorizontalTextPosition(SwingConstants.CENTER);
			m_butKey[keycount].setVerticalTextPosition(SwingConstants.BOTTOM);
			m_butKey[keycount].addMouseListener(m_skmouselistener);
			m_butKey[keycount].setRolloverEnabled(true);
			m_butKey[keycount].setVisible(true);
			JDialogContentPane.add(m_butKey[keycount], m_butKey[keycount].getName());
		}

		//Context Menu
		m_butKey[97] = new JButton();
		m_butKey[97].setName("Key" + 105);
		m_butKey[97].setText("CM");
		m_butKey[97].setFont(f);
		m_butKey[97].setMargin(new java.awt.Insets(0, 0, 0, 0));
		m_butKey[97].setHorizontalTextPosition(SwingConstants.CENTER);
		m_butKey[97].setVerticalTextPosition(SwingConstants.BOTTOM);
		m_butKey[97].addMouseListener(m_skmouselistener);
		m_butKey[97].setRolloverEnabled(true);
		m_butKey[97].setVisible(true);
		if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q ||
				lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
				lngindex == JVMenu.LANGUAGE_JAPANESE_K)
			m_butKey[97].setBounds(208, 159, 30, 27);
		else
			m_butKey[97].setBounds(272, 159, 30, 27);
		JDialogContentPane.add(m_butKey[97], m_butKey[97].getName());
		
		switch (lngindex) {
		case JVMenu.LANGUAGE_ENGLISH_US:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_77_JVM"));
			break;
		case JVMenu.LANGUAGE_ENGLISH_UK:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_78_JVM"));
			break;
		case JVMenu.LANGUAGE_SPANISH:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_79_JVM"));
			break;
		case JVMenu.LANGUAGE_FRENCH:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_80_JVM"));
			break;
		case JVMenu.LANGUAGE_GERMAN_GER:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_81_JVM"));
			break;
		case JVMenu.LANGUAGE_ITALIAN:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_82_JVM"));
			break;
		case JVMenu.LANGUAGE_DANISH:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_83_JVM"));
			break;
		case JVMenu.LANGUAGE_FINNISH:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_84_JVM"));
			break;
		case JVMenu.LANGUAGE_GERMAN_SWISS:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_85_JVM"));
			break;
		case JVMenu.LANGUAGE_NORWEGIAN_NOR:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_86_JVM"));
			break;
		case JVMenu.LANGUAGE_PORTUGUESE:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_87_JVM"));
			break;
		case JVMenu.LANGUAGE_SWEDISH:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_88_JVM"));
			break;
		case JVMenu.LANGUAGE_HEBREW:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_89_JVM"));
			break;
		case JVMenu.LANGUAGE_FRENCH_BELGIUM:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_90_JVM"));
			break;

		case JVMenu.LANGUAGE_DUTCH_BELGIUM:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_91_JVM"));
			break;

		case JVMenu.LANGUAGE_RUSSIAN:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_92_JVM"));
			break;
		case JVMenu.LANGUAGE_JAPANESE_Q:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_139_JVM"));
			break;

		case JVMenu.LANGUAGE_TURKISH_F:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_94_JVM"));
			break;
		case JVMenu.LANGUAGE_TURKISH_Q:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_95_JVM"));
			break;
		case JVMenu.LANGUAGE_JAPANESE_H:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_140_JVM"));
			break;
		case JVMenu.LANGUAGE_JAPANESE_K:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_141_JVM"));
			break;
		case JVMenu.LANGUAGE_DUTCH_NL:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_142_JVM"));
			break;			
		default:
			this.setTitle(LocaleStrings.getString("F_74_JVM")+" - "+LocaleStrings.getString("F_77_JVM"));
			break;

		}

		if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q ||
				lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
				lngindex == JVMenu.LANGUAGE_JAPANESE_K)
		{
			japSpecialKeys[0] = new JButton();
			japSpecialKeys[0].setName("Key125");
			japSpecialKeys[0].setMargin(new java.awt.Insets(0, 0, 0, 0));
			japSpecialKeys[0].setHorizontalTextPosition(SwingConstants.CENTER);
			japSpecialKeys[0].setVerticalTextPosition(SwingConstants.BOTTOM);
			japSpecialKeys[0].addMouseListener(m_skmouselistener);
			final Font jap = new Font("Dialog", Font.PLAIN, 10);
			japSpecialKeys[0].setFont(jap);
			japSpecialKeys[0].setText("変換");
			japSpecialKeys[0].setBounds(238, 159, 32, 27);
			japSpecialKeys[0].setVisible(true);
			JDialogContentPane.add(japSpecialKeys[0], japSpecialKeys[0].getName());
			keycount = 94;
			japSpecialKeys[1] = new JButton();
			//m_butKey[keycount].setName("Key" + i);
			//Font jap = new Font("Dialog", Font.BOLD,10);
			japSpecialKeys[1].setFont(jap);
			japSpecialKeys[1].setBounds(103, 159, 50, 27);
			//m_butKey[keycount].setBounds(359, 47, 38, 27);
			japSpecialKeys[1].setName("Key124");
			japSpecialKeys[1].setText("無変換");
			japSpecialKeys[1].setMargin(new java.awt.Insets(0, 0, 0, 0));
			//m_butKey[keycount].setHorizontalTextPosition(SwingConstants.CENTER);
			//m_butKey[keycount].setVerticalTextPosition(SwingConstants.BOTTOM);
			japSpecialKeys[1].addMouseListener(m_skmouselistener);
			japSpecialKeys[1].setVisible(true);
			JDialogContentPane.add(japSpecialKeys[1], japSpecialKeys[1].getName());
			japSpecialKeys[2] = new JButton();
			japSpecialKeys[2].setFont(jap);
			japSpecialKeys[2].setBounds(270, 159, 32, 27);
			japSpecialKeys[2].setName("Key126");
			japSpecialKeys[2].setText("ひ/カ");
			japSpecialKeys[2].setMargin(new java.awt.Insets(0, 0, 0, 0));
			japSpecialKeys[2].addMouseListener(m_skmouselistener);
			japSpecialKeys[2].setVisible(true);
			JDialogContentPane.add(japSpecialKeys[2], japSpecialKeys[2].getName());
			
			
			m_butKey[64] = new JButton();
			m_butKey[64].setName("Key" + 104);
			m_butKey[64].setFont(f);
			// first key is absent in japanese language
			m_butKey[64].setText(String.valueOf(KeyProcessor.normalCharSet[lngindex]
						.charAt((63 - 16))));// keylabel ends in 15
									
			m_butKey[64].setBounds(337, 131, 27, 27);
			m_butKey[64].setMargin(new java.awt.Insets(0, 0, 0, 0));
			m_butKey[64]
					.setHorizontalTextPosition(SwingConstants.CENTER);
			m_butKey[64].setVerticalTextPosition(SwingConstants.BOTTOM);
			m_butKey[64].addMouseListener(m_skmouselistener);
			m_butKey[64].setVisible(true);
			JDialogContentPane.add(m_butKey[64], m_butKey[64].getName());
			

		}
		
		//The japanses key for ¥ (Yen) symbol.
		if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q || lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
				lngindex == JVMenu.LANGUAGE_JAPANESE_K){
			Rectangle bounds = m_butKey[28].getBounds();
			bounds.x = bounds.x+25;
			m_butKey[53].setBounds(bounds);
			
		}
		syncKbdLED();// Synchronize Softkeyboard LED status with Host LED status;
		syncHoldKey();
		
		// this.toFront();
		this.setContentPane(JDialogContentPane);
		this.setName("SoftKeyboard");
		// this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		//Display softkeyboard respect to Jviewer window
		Point dp =  JViewerApp.getInstance().getPopUpWindowPosition(WIDTH,HEIGHT);
		this.setBounds(dp.x, dp.y, WIDTH, HEIGHT);
		this.setResizable(false);
		this.setFocusable(false);
		this.setFocusableWindowState(false);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(final WindowEvent e) {
				closeSoftKeyboard();
			}
		});

	}
	/* Closed keylistener and dispose softkeyboard*/
	public void closeSoftKeyboard()
	{
		m_skmouselistener.close();
		dispose();
	}

	/**
	 * Mouse event listener class.
	 */
	class SKMouseListener extends MouseInputAdapter {
		int butid;

		byte[] n_keys = new byte[4];

		public void mouseReleased(final MouseEvent e) {
			try {				
				Arrays.fill(n_keys, (byte) 0);
				
				// For  Normal Buttons(For Press & Release)
				if (e.getSource().getClass().getName() == "javax.swing.JButton"){

					final JButton but = (JButton) e.getSource();
					butid = new Integer(but.getName().substring(3));

					if(butid== 105 ) {
						keyRep.set(KeyEvent.VK_CONTEXT_MENU, KeyEvent.KEY_LOCATION_STANDARD, true );
						m_KVMClnt.sendKMMessage(keyRep);
						keyRep.set(KeyEvent.VK_CONTEXT_MENU, KeyEvent.KEY_LOCATION_STANDARD, false );
						m_KVMClnt.sendKMMessage(keyRep);
						return;
					}

					if (butid == 13) {
						// OnPrintScreen();
						//For Printscreen send key once. else In linux host it will print screen thrice
						// prntscrn Key MAKE
						//keyRep.set(KeyEvent.VK_PRINTSCREEN, KeyEvent.KEY_LOCATION_STANDARD, true);
						//m_KVMClnt.sendKMMessage(keyRep);
						// prntscrn Key BREAK
						keyRep.set(KeyEvent.VK_PRINTSCREEN, KeyEvent.KEY_LOCATION_STANDARD, false);
						m_KVMClnt.sendKMMessage(keyRep);
					} else if (butid == 15) {
						/*
						 * Arrays.fill(n_keys,(byte)0); n_keys[0] = 14;
						 * n_keys[1] = (byte)0x77; n_keys[2] = (byte)0xE1;
						 * n_keys[3] = (byte)0xF0;
						 */
						keyRep.set(KeyEvent.VK_PAUSE, KeyEvent.KEY_LOCATION_STANDARD, true);
						m_KVMClnt.sendKMMessage(keyRep);
						keyRep.set(KeyEvent.VK_PAUSE, KeyEvent.KEY_LOCATION_STANDARD, false);
						m_KVMClnt.sendKMMessage(keyRep);

					} else {
						if (butid >= 93 && butid <= 103
								&& m_toggleKey[10].isSelected() == true) {
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_NUMPAD, true);
							m_KVMClnt.sendKMMessage(keyRep);
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_NUMPAD, false);
							m_KVMClnt.sendKMMessage(keyRep);
						}
						else if(butid >= 93 && butid <= 103
								&& m_toggleKey[10].isSelected() == false){
							final int index = butid - 93;
							int  keyCode = numpadkeysoffevent[index];
							keyRep.set(keyCode, KeyEvent.KEY_LOCATION_NUMPAD, true);
							m_KVMClnt.sendKMMessage(keyRep);
							keyRep.set(keyCode, KeyEvent.KEY_LOCATION_NUMPAD, false);
							m_KVMClnt.sendKMMessage(keyRep);
						}
						else if (butid != 64 && butid != 0) {
							 //System.out.println("butidvalue::"+butid);
							 //System.out.println("buiid 1 :
							 //"+(m_ps2.get(butid)));
							
							if(butid==53){
								int keyCode = m_ps2.get(butid);
								if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q ||
										lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
										lngindex == JVMenu.LANGUAGE_JAPANESE_K)
									keyCode = 220;
									keyRep.set(keyCode, KeyEvent.KEY_LOCATION_STANDARD,
											true);
									m_KVMClnt.sendKMMessage(keyRep);
									keyRep.set(keyCode, KeyEvent.KEY_LOCATION_STANDARD,
											false);
									m_KVMClnt.sendKMMessage(keyRep);
								
								
							}
							else if(butid == 28 ){
								
									keyRep.set(61, KeyEvent.KEY_LOCATION_STANDARD,
											true);
									m_KVMClnt.sendKMMessage(keyRep);
									keyRep.set(61, KeyEvent.KEY_LOCATION_STANDARD,
											false);
									m_KVMClnt.sendKMMessage(keyRep);
							}
							else if(butid == 52){
								keyRep.set(222, KeyEvent.KEY_LOCATION_STANDARD,
										true);
								m_KVMClnt.sendKMMessage(keyRep);
								keyRep.set(222, KeyEvent.KEY_LOCATION_STANDARD,
										false);
								m_KVMClnt.sendKMMessage(keyRep);
							}
							else if(butid == 81 || butid == 82 || butid == 83 || butid == 91){// Numpad / * - +
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_NUMPAD,
										true);
								m_KVMClnt.sendKMMessage(keyRep);
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_NUMPAD, false);
								m_KVMClnt.sendKMMessage(keyRep);
							}
							else if(butid == 104){
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD,
										true);
								m_KVMClnt.sendKMMessage(keyRep);
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, false);
								m_KVMClnt.sendKMMessage(keyRep);
							}
							else{
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, true);
								m_KVMClnt.sendKMMessage(keyRep);
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, false);
								m_KVMClnt.sendKMMessage(keyRep);
						}
						} else if (butid == 64) { // Manually sending Tab key	
							// OnTab();
							keyRep.set(KeyEvent.VK_TAB, KeyEvent.KEY_LOCATION_STANDARD, true);
							m_KVMClnt.sendKMMessage(keyRep);
							keyRep.set(KeyEvent.VK_TAB, KeyEvent.KEY_LOCATION_STANDARD, false);
							m_KVMClnt.sendKMMessage(keyRep);

						} else if (butid == 0)// Manually sending Escape key
						{
							// OnEsc();
							keyRep.set(KeyEvent.VK_ESCAPE, KeyEvent.KEY_LOCATION_STANDARD, true);
							m_KVMClnt.sendKMMessage(keyRep);
							keyRep.set(KeyEvent.VK_ESCAPE, KeyEvent.KEY_LOCATION_STANDARD, false);
							m_KVMClnt.sendKMMessage(keyRep);
						}
						// For AltGr to be off when normal keys are pressed
						if (m_toggleKey[6].isSelected() == true)// Right Alt
						{
							if(JViewerApp.getInstance().getM_wndFrame().getM_status().getRightAlt().isSelected() == true) {
								if ((lngindex != JVMenu.LANGUAGE_ENGLISH_US && lngindex != JVMenu.LANGUAGE_RUSSIAN &&
									lngindex != JVMenu.LANGUAGE_JAPANESE_Q && lngindex != JVMenu.LANGUAGE_JAPANESE_H &&
									lngindex != JVMenu.LANGUAGE_JAPANESE_K)){
									altGr_enable = true;
									OnAltGrOn(KeyProcessor.altGrCharSet[lngindex]);
								}
								m_toggleKey[6].setSelected(true);
								OnModifier(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT,
										KeyEvent.KEY_PRESSED);
							}
							else {
								if (butid <= 76 && butid != 14 && butid != 65
										&& butid != 66 && butid != 67
										&& butid != 68 && butid != 69
										&& butid != 72 && butid != 73
										&& butid != 74) {
									// System.out.println("Right Altgr Release");
									OnRightAltRelease();
									m_toggleKey[6].setSelected(false);
									OnModifier(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT, KeyEvent.KEY_RELEASED);
								}
							}
						}
						// For disabling & releasing alt or shift on the event
						// of keypress of keys from 0 to 63 other than print screen and break key
						// 13-print screen,14-Scroll lock
						if (butid <= 64 || butid ==104 && !(butid == 13 || butid == 14)) {
							if (m_toggleKey[2].isSelected() == true) { // Left shift
								if (butid == 64 && (m_toggleKey[5].isSelected() == true || m_toggleKey[6]
												.isSelected() == true))// For Alt+Shift+tab Combination
								{
									
								} else {
									// System.out.println("Left Shift Key
									// Released");
									butid = new Integer(m_toggleKey[2].getName().substring(3));
									keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_LEFT, false);
									m_KVMClnt.sendKMMessage(keyRep);
									if (m_toggleKey[1].isSelected() == true){
										OnCaps(KeyProcessor.normalCapsCharSet[lngindex]);
									}
										
									else
									{
										OnCaps(KeyProcessor.normalCharSet[lngindex]);
									}
									m_toggleKey[2].setSelected(false);
									m_toggleKey[9].setSelected(false);
								}
							}

							if (m_toggleKey[9].isSelected() == true) { // Right shift
								if (butid == 64
										&& (m_toggleKey[5].isSelected() == true || m_toggleKey[6]
												.isSelected() == true)) { // For Alt+Shift+tab Combination
																								
								} else {
									// System.out.println("Right Shift Key
									// Released");
									butid = new Integer(m_toggleKey[2].getName().substring(3));
									keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_RIGHT, false);
									m_KVMClnt.sendKMMessage(keyRep);
									if (m_toggleKey[1].isSelected() == true){
										OnCaps(KeyProcessor.normalCapsCharSet[lngindex]);
									}
										
									else
									{
										OnCaps(KeyProcessor.normalCharSet[lngindex]);
									}
									m_toggleKey[2].setSelected(false);
									m_toggleKey[9].setSelected(false);
								}
							}

							if (m_toggleKey[5].isSelected() == true){ // Left Alt
								if (butid != 64 && butid != 0) {
									if(JViewerApp.getInstance().getM_wndFrame().getM_status().getLeftAlt().isSelected() == true) {
										OnModifier(KeyEvent.VK_ALT,
										KeyEvent.KEY_LOCATION_LEFT,
										KeyEvent.KEY_PRESSED);
									}
									else {
										// System.out.println("Inside Alt Release");
										OnModifier(KeyEvent.VK_ALT,
												KeyEvent.KEY_LOCATION_LEFT,
												KeyEvent.KEY_RELEASED);
										m_toggleKey[5].setSelected(false);
									}
								}
							}

							if (m_toggleKey[6].isSelected() == true) {// Right Alt
								if (butid != 64 && butid != 0) {
									if(JViewerApp.getInstance().getM_wndFrame().getM_status().getRightAlt().isSelected() == true) {
										if ((lngindex != JVMenu.LANGUAGE_ENGLISH_US && lngindex != JVMenu.LANGUAGE_RUSSIAN &&
												lngindex != JVMenu.LANGUAGE_JAPANESE_Q && lngindex != JVMenu.LANGUAGE_JAPANESE_H &&
												lngindex != JVMenu.LANGUAGE_JAPANESE_K)){
											altGr_enable = true;
											OnAltGrOn(KeyProcessor.altGrCharSet[lngindex]);
										}
										OnModifier(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT, 
												KeyEvent.KEY_PRESSED);
									}
									else {
										// System.out.println("Inside Alt Release");
										OnModifier(KeyEvent.VK_ALT,
												KeyEvent.KEY_LOCATION_RIGHT,
												KeyEvent.KEY_RELEASED);
										m_toggleKey[6].setSelected(false);
									}
								}
							}

							if (m_toggleKey[3].isSelected() == true) { // Left Ctrl
								if(JViewerApp.getInstance().getM_wndFrame().getM_status().getLeftCtrl().isSelected() == true) {
									butid = new Integer(m_toggleKey[3].getName().substring(3));
									keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_LEFT, true);
									m_KVMClnt.sendKMMessage(keyRep);								
								}
								else {
									// System.out.println("Inside Left Ctrl
									// Release");
									butid = new Integer(m_toggleKey[3].getName().substring(3));
									keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_LEFT, false);
									m_KVMClnt.sendKMMessage(keyRep);
									m_toggleKey[3].setSelected(false);
									//m_toggleKey[8].setSelected(false);
								}	
							}

							if (m_toggleKey[8].isSelected() == true) { // Right Ctrl
								if(JViewerApp.getInstance().getM_wndFrame().getM_status().getRightCtrl().isSelected() == true) {
									butid = new Integer(m_toggleKey[8].getName().substring(3));
									keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_RIGHT, true);
									m_KVMClnt.sendKMMessage(keyRep);								
								}
								else {
									// System.out.println("Inside Right Ctrl
									// Release");
									butid = new Integer(m_toggleKey[8].getName().substring(3));
									keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_RIGHT, false);
									m_KVMClnt.sendKMMessage(keyRep);
									m_toggleKey[8].setSelected(false);
									//m_toggleKey[3].setSelected(false);
								}
							}

						}
					}
				} else // For Toggle Buttons
				{
					final JToggleButton but = (JToggleButton) e.getSource();
					butid = new Integer(but.getName().substring(3));

					if (but.isSelected() == true)// For Key Pressed
					{
						if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q ||
								lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
								lngindex == JVMenu.LANGUAGE_JAPANESE_K)
						{
							if(butid == 16) {
								Japaneskeyflag = true;
								keyRep.set(192, KeyEvent.KEY_LOCATION_STANDARD, true );
								m_KVMClnt.sendKMMessage(keyRep);
								Japaneskeyflag = true;
								keyRep.set(192, KeyEvent.KEY_LOCATION_STANDARD, false );
								m_KVMClnt.sendKMMessage(keyRep);
								return;
							}
							if(butid == 124 )
							{
								Japaneskeyflag = true;
								keyRep.set(29, KeyEvent.KEY_LOCATION_STANDARD, true );
								m_KVMClnt.sendKMMessage(keyRep);
								Japaneskeyflag = true;
								keyRep.set(29, KeyEvent.KEY_LOCATION_STANDARD, false );
								m_KVMClnt.sendKMMessage(keyRep);
								//if(m_toggleKey[6].isSelected() || m_toggleKey[2].is)
								if(m_toggleKey[1].isSelected()&& (JViewerApp.getInstance().getHostKeyboardLEDStatus() & CAPSLOCK) == 2){
									if((JViewerApp.getInstance().getHostKeyboardLEDStatus()& CAPSLOCK) == CAPSLOCK){
										if (m_toggleKey[6].isSelected() == false) {
											if (m_toggleKey[2].isSelected() == true
													|| m_toggleKey[9].isSelected() == true)
												OnCaps(KeyProcessor.shiftedCapsCharSet[lngindex]);
											else {
												OnCaps(KeyProcessor.normalCapsCharSet[lngindex]);
												m_butKey[15].setText("pau");
											}
										}
									}
									else{
										if (m_toggleKey[6].isSelected() == false) {
											if (m_toggleKey[2].isSelected() == true
													|| m_toggleKey[9].isSelected() == true)
												OnCaps(KeyProcessor.shiftedCharSet[lngindex]);
											else {
												OnCaps(KeyProcessor.normalCharSet[lngindex]);
												m_butKey[15].setText("brk");
											}
										}
									}
								}
								else{
									if (m_toggleKey[6].isSelected() == false) {
										if (m_toggleKey[2].isSelected() == true
												|| m_toggleKey[9].isSelected() == true)
											OnCaps(KeyProcessor.shiftedCharSet[lngindex]);
										else {
											OnCaps(KeyProcessor.normalCharSet[lngindex]);
											m_butKey[15].setText("brk");
										}
									}
								}
									
								return;
							}
							
							if(butid == 125 )
							{
								Japaneskeyflag = true;
								keyRep.set(28, KeyEvent.KEY_LOCATION_STANDARD, true );
								m_KVMClnt.sendKMMessage(keyRep);
								Japaneskeyflag = true;
								keyRep.set(28, KeyEvent.KEY_LOCATION_STANDARD, false );
								m_KVMClnt.sendKMMessage(keyRep);
								return;
							}							
							
							if(butid == 126 )
							{

								Japaneskeyflag = true;
								keyRep.set(240, KeyEvent.KEY_LOCATION_STANDARD, true );
								m_KVMClnt.sendKMMessage(keyRep);
								Japaneskeyflag = true;
								keyRep.set(240, KeyEvent.KEY_LOCATION_STANDARD, false );
								m_KVMClnt.sendKMMessage(keyRep);
								return;
							}

						}
						switch (butid) {

						case 68:// Left Windows
							m_toggleKey[4].setSelected(true);
							keyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_LEFT, true);
							m_KVMClnt.sendKMMessage(keyRep);
							break;
						case 72:// Right Windows
							m_toggleKey[7].setSelected(true);
							keyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_RIGHT, true);
							m_KVMClnt.sendKMMessage(keyRep);
							break;
						case 65:// Caps Lock
								if (m_toggleKey[6].isSelected() == false) {
									if (m_toggleKey[2].isSelected() == true
											|| m_toggleKey[9].isSelected() == true)
										OnCaps(KeyProcessor.shiftedCapsCharSet[lngindex]);
									else {
										OnCaps(KeyProcessor.normalCapsCharSet[lngindex]);										
									}
								}
								m_butKey[15].setText("pau");
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, true);
								m_KVMClnt.sendKMMessage(keyRep);
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, false);
								m_KVMClnt.sendKMMessage(keyRep);
							break;

						case 80:// Num Lock
							OnNum(numpadkeyson);
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_NUMPAD, true);
							m_KVMClnt.sendKMMessage(keyRep);
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_NUMPAD, false);
							m_KVMClnt.sendKMMessage(keyRep);
							break;
						case 14:// Scroll Lock
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, true);
							m_KVMClnt.sendKMMessage(keyRep);
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, false);
							m_KVMClnt.sendKMMessage(keyRep);
							break;
						case 66:// Left Shift
								if (m_toggleKey[6].isSelected() == false) { // For AltGr
									m_toggleKey[9].setSelected(true);
									if (m_toggleKey[1].isSelected() == true)
										OnCaps(KeyProcessor.shiftedCapsCharSet[lngindex]);
									else {
										OnCaps(KeyProcessor.shiftedCharSet[lngindex]);
										m_butKey[15].setText("pau");
									}
								}
								else{ // altgr is enabled -- if both alt and shift are pressed
									if(lngindex == JVMenu.LANGUAGE_TURKISH_F || lngindex == JVMenu.LANGUAGE_TURKISH_Q){
										OnAltGrOn(KeyProcessor.shiftedAltGrCharSet[lngindex]);
									}
								}
							keyRep.set(16, 2, true);
							m_KVMClnt.sendKMMessage(keyRep);
							break;
						case 74:// Right Shift
							// JOptionPane.showMessageDialog(null,"Right Shift
							// Pressed","Invalid
							// Command",JOptionPane.ERROR_MESSAGE);
								if (m_toggleKey[6].isSelected() == false) {
									m_toggleKey[2].setSelected(true);
									if (m_toggleKey[1].isSelected() == true)
										OnCaps(KeyProcessor.shiftedCapsCharSet[lngindex]);
									else {
										OnCaps(KeyProcessor.shiftedCharSet[lngindex]);
										m_butKey[15].setText("pau");
									}
								}
									else{ // altgr is enabled -- if both alt and shift are pressed
										if(lngindex == JVMenu.LANGUAGE_TURKISH_F || lngindex == JVMenu.LANGUAGE_TURKISH_Q){
											OnAltGrOn(KeyProcessor.shiftedAltGrCharSet[lngindex]);
										}
								}
							//rb.keyPress(m_ps2.get(butid));
							keyRep.set(16, 2, true);
							m_KVMClnt.sendKMMessage(keyRep);

							break;
						case 69:// Left Alt
							/*
							if (m_toggleKey[6].isSelected() == true) {
								m_toggleKey[6].setSelected(false);
								keyRep.set(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT, false);
								m_KVMClnt.sendKMMessage(keyRep);
							} else {
							*/
							keyRep.set(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_LEFT, true);
							m_KVMClnt.sendKMMessage(keyRep);
							//}
							break;
						case 71: // Right Alt
							if ((lngindex != JVMenu.LANGUAGE_ENGLISH_US && lngindex != JVMenu.LANGUAGE_RUSSIAN &&
									lngindex != JVMenu.LANGUAGE_JAPANESE_Q && lngindex != JVMenu.LANGUAGE_JAPANESE_H &&
									lngindex != JVMenu.LANGUAGE_JAPANESE_K)){
								altGr_enable = true;
								if(m_toggleKey[2].isSelected() == true || m_toggleKey[9].isSelected() == true)
								{
									OnAltGrOn(KeyProcessor.shiftedAltGrCharSet[lngindex]);
								}
								else
								{
									OnAltGrOn(KeyProcessor.altGrCharSet[lngindex]);
								}
							}
							/*
							if (m_toggleKey[5].isSelected() == true) {
								m_toggleKey[5].setSelected(false);
								OnModifier(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_LEFT,
										KeyEvent.KEY_RELEASED);
							} else
							*/
							OnModifier(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT,
									KeyEvent.KEY_PRESSED);
							break;
						case 67:// Left Ctrl
							m_toggleKey[3].setSelected(true);
							butid = new Integer(m_toggleKey[3].getName().substring(3));
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_LEFT, true);
							m_KVMClnt.sendKMMessage(keyRep);
							break;
						case 73:// Right Ctrl
							m_toggleKey[8].setSelected(true);
							butid = new Integer(m_toggleKey[8].getName().substring(3));
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_RIGHT, true);
							m_KVMClnt.sendKMMessage(keyRep);
							break;
						default:
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, true);
							m_KVMClnt.sendKMMessage(keyRep);
						}
					} else // For Release of Toggle Buttons
					{
						if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q ||
								lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
								lngindex == JVMenu.LANGUAGE_JAPANESE_K)
						{
							if(butid == 16) {
								Japaneskeyflag = true;
								keyRep.set(192, KeyEvent.KEY_LOCATION_STANDARD, true );
								m_KVMClnt.sendKMMessage(keyRep);
								Japaneskeyflag = true;
								keyRep.set(192, KeyEvent.KEY_LOCATION_STANDARD, false );
								m_KVMClnt.sendKMMessage(keyRep);
								return;
							}							
							if(butid == 126 )
							{
								
								Japaneskeyflag = true;
								keyRep.set(240, KeyEvent.KEY_LOCATION_STANDARD, true );
								m_KVMClnt.sendKMMessage(keyRep);
								Japaneskeyflag = true;
								keyRep.set(240, KeyEvent.KEY_LOCATION_STANDARD, false );
								m_KVMClnt.sendKMMessage(keyRep);

								//if(m_toggleKey[6].isSelected() || m_toggleKey[2].is)
								return;
							}
							
							if(butid == 125 )
							{
								Japaneskeyflag = true;
								keyRep.set(28, KeyEvent.KEY_LOCATION_STANDARD, true );
								m_KVMClnt.sendKMMessage(keyRep);
								Japaneskeyflag = true;
								keyRep.set(28, KeyEvent.KEY_LOCATION_STANDARD, false );
								m_KVMClnt.sendKMMessage(keyRep);
								return;
							}
							if(butid == 124 )
							{
								Japaneskeyflag = true;
								keyRep.set(29, KeyEvent.KEY_LOCATION_STANDARD,true );
								m_KVMClnt.sendKMMessage(keyRep);

								Japaneskeyflag = true;
								keyRep.set(29, KeyEvent.KEY_LOCATION_STANDARD, false );
								m_KVMClnt.sendKMMessage(keyRep);
								//if(m_toggleKey[6].isSelected() || m_toggleKey[2].is)

								return;
							}
						}

						switch (butid) {

						case 68:// Left Windows
							if(JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.KEYBOARD_LEFT_WINKEY_PRESSHOLD).isSelected()) {
								m_toggleKey[4].setSelected(true);
								keyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_LEFT, true);
								m_KVMClnt.sendKMMessage(keyRep);
							}	 
							else {
								m_toggleKey[4].setSelected(false);
								keyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_LEFT, false);
								m_KVMClnt.sendKMMessage(keyRep);
							}			
							break;
						case 72:// Right Windows
							if(JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.KEYBOARD_RIGHT_WINKEY_PRESSHOLD).isSelected()) {
								m_toggleKey[7].setSelected(true);
								keyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_RIGHT, true);
								m_KVMClnt.sendKMMessage(keyRep);	
							}
							else {
								m_toggleKey[7].setSelected(false);
								keyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_RIGHT, false);
								m_KVMClnt.sendKMMessage(keyRep);
							}
							break;
						case 65:// Caps Lock
								if (m_toggleKey[6].isSelected() == false) {
									if (m_toggleKey[2].isSelected() == true
											|| m_toggleKey[9].isSelected() == true)
										OnCaps(KeyProcessor.shiftedCharSet[lngindex]);
									else {
										OnCaps(KeyProcessor.normalCharSet[lngindex]);										
									}
								}
								m_butKey[15].setText("brk");
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, true);
								m_KVMClnt.sendKMMessage(keyRep);
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, false);
								m_KVMClnt.sendKMMessage(keyRep);
							break;
						case 80:// Num Lock
							OnNum(numpadkeysoff);
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_NUMPAD, true);
							m_KVMClnt.sendKMMessage(keyRep);
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_NUMPAD, false);
							m_KVMClnt.sendKMMessage(keyRep);
							break;
						case 14:// Scroll Lock
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, true);
							m_KVMClnt.sendKMMessage(keyRep);
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, false);
							m_KVMClnt.sendKMMessage(keyRep);
							break;
						case 66:// Left Shift
								if (m_toggleKey[6].isSelected() == false) {
									m_toggleKey[9].setSelected(false);
									if (m_toggleKey[1].isSelected() == true)
										OnCaps(KeyProcessor.normalCapsCharSet[lngindex]);
									else {
										OnCaps(KeyProcessor.normalCharSet[lngindex]);
										m_butKey[15].setText("brk");
									}
								} // if alt is enabled when releasing shift, display alt char set
								else{ 
									OnAltGrOn(KeyProcessor.altGrCharSet[lngindex]);
								}
								
							keyRep.set(16, 2, false);
							m_KVMClnt.sendKMMessage(keyRep);
							break;
						case 74:// Right Shift
							{
								if (m_toggleKey[6].isSelected() == false) {
									m_toggleKey[2].setSelected(false);
									if (m_toggleKey[1].isSelected() == true)
										OnCaps(KeyProcessor.normalCapsCharSet[lngindex]);
									else {
										OnCaps(KeyProcessor.normalCharSet[lngindex]);
										m_butKey[15].setText("brk");
									}
								}// if alt is enabled when releasing shift, display alt char set
								else{
									OnAltGrOn(KeyProcessor.altGrCharSet[lngindex]);
								}
							}

							keyRep.set(16, 2, false);
							m_KVMClnt.sendKMMessage(keyRep);
							break;
						case 69:// Left Alt
							if(JViewerApp.getInstance().getM_wndFrame().getM_status().getLeftAlt().isSelected() == false) {
								OnModifier(KeyEvent.VK_ALT,
										KeyEvent.KEY_LOCATION_LEFT,
										KeyEvent.KEY_RELEASED);
								// rb.keyRelease(m_ps2.get(butid));
							}
							else {
								OnModifier(KeyEvent.VK_ALT,
										KeyEvent.KEY_LOCATION_LEFT,
										KeyEvent.KEY_PRESSED);
								m_toggleKey[5].setSelected(true);
							}
							break;
						case 71: // Right Alt
							if(JViewerApp.getInstance().getM_wndFrame().getM_status().getRightAlt().isSelected() == true) {
								if ((lngindex != JVMenu.LANGUAGE_ENGLISH_US && lngindex != JVMenu.LANGUAGE_RUSSIAN &&
										lngindex != JVMenu.LANGUAGE_JAPANESE_Q && lngindex != JVMenu.LANGUAGE_JAPANESE_H &&
										lngindex != JVMenu.LANGUAGE_JAPANESE_K)){
									altGr_enable = true;
									OnAltGrOn(KeyProcessor.altGrCharSet[lngindex]);
								}

								m_toggleKey[6].setSelected(true);
								OnModifier(KeyEvent.VK_ALT,
										KeyEvent.KEY_LOCATION_RIGHT,
										KeyEvent.KEY_PRESSED);
							}
							else {
								// System.out.println("Right Alt Key Released");
								if(lngindex != 16)
								OnRightAltRelease();
								OnModifier(KeyEvent.VK_ALT,
										KeyEvent.KEY_LOCATION_RIGHT,
										KeyEvent.KEY_RELEASED);
								// rb.keyRelease(m_ps2.get(butid));
							}
							break;
						case 67:// Left Ctrl
							if(JViewerApp.getInstance().getM_wndFrame().getM_status().getLeftCtrl().isSelected() == true) {
								m_toggleKey[3].setSelected(true);
								butid = new Integer(m_toggleKey[3].getName().substring(3));
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_LEFT, true);
								m_KVMClnt.sendKMMessage(keyRep);
							}
							else {
								m_toggleKey[3].setSelected(false);
								butid = new Integer(m_toggleKey[3].getName().substring(3));
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_LEFT, false);
								m_KVMClnt.sendKMMessage(keyRep);
							}
							break;
						case 73:// Right Ctrl
							if(JViewerApp.getInstance().getM_wndFrame().getM_status().getRightCtrl().isSelected() == true) {
								m_toggleKey[8].setSelected(true);
								butid = new Integer(m_toggleKey[8].getName().substring(3));
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_RIGHT, true);
								m_KVMClnt.sendKMMessage(keyRep);
							}
							else {
								m_toggleKey[8].setSelected(false);
								butid = new Integer(m_toggleKey[8].getName().substring(3));
								keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_RIGHT, false);
								m_KVMClnt.sendKMMessage(keyRep);
							}
							break;
						default:
							keyRep.set(m_ps2.get(butid), KeyEvent.KEY_LOCATION_STANDARD, false);
							m_KVMClnt.sendKMMessage(keyRep);
						}

					}
				}
			} catch (final Exception e1) {
				Debug.out.println(e1);
			}
		}


		public void OnPrintScreen() {
			// Print Screen make code

			Arrays.fill(n_keys, (byte) 0);
			n_keys[0] = 1;
			n_keys[1] = (byte) 0xE0;
			n_keys[2] = (byte) 0x12;
			n_keys[3] = (byte) 0x00;

			Arrays.fill(n_keys, (byte) 0);
			n_keys[0] = 1;
			n_keys[1] = (byte) 0xE0;
			n_keys[2] = (byte) 0x7C;
			n_keys[3] = (byte) 0x00;

			// Print Screen break code

			Arrays.fill(n_keys, (byte) 0);
			n_keys[0] = 2;
			n_keys[1] = (byte) 0xE0;
			n_keys[2] = (byte) 0xF0;
			n_keys[3] = (byte) 0x7C;

			Arrays.fill(n_keys, (byte) 0);
			n_keys[0] = 2;
			n_keys[1] = (byte) 0xE0;
			n_keys[2] = (byte) 0xF0;
			n_keys[3] = (byte) 0x12;

		}

		public void OnPause() {
			// System.out.println("Pause Key");
			// The pause key is make code
			Arrays.fill(n_keys, (byte) 0);
			n_keys[0] = 14;
			n_keys[1] = (byte) 0x77;
			n_keys[2] = (byte) 0xE1;
			n_keys[3] = (byte) 0xF0;

		}

		public void OnModifier(final int key, final int loc, final int state) {
			// Arrays.fill(n_keys, (byte) 0);
			if (KeyEvent.KEY_PRESSED == state)
				keyRep.set(key, loc, true);

			if (KeyEvent.KEY_RELEASED == state)
				keyRep.set(key, loc, false);

			m_KVMClnt.sendKMMessage(keyRep);

		}

		public void OnRightAltRelease() {
			if (lngindex > 0) {
				if (m_toggleKey[1].isSelected() == true) {
					if (m_toggleKey[2].isSelected() == true
							|| m_toggleKey[9].isSelected() == true)
						OnCaps(KeyProcessor.shiftedCapsCharSet[lngindex]);
					else
						OnCaps(KeyProcessor.normalCapsCharSet[lngindex]);
				} else if (m_toggleKey[2].isSelected() == true
						|| m_toggleKey[9].isSelected() == true)
					OnCaps(KeyProcessor.shiftedCharSet[lngindex]);
				else
					OnCaps(KeyProcessor.normalCharSet[lngindex]);
				AltGrOff();
			}
		}

		public void OnEsc() {
			// Esc make code
			Arrays.fill(n_keys, (byte) 0);
			n_keys[0] = 0;
			n_keys[1] = (byte) 0x76;
			n_keys[2] = (byte) 0x00;
			n_keys[3] = (byte) 0x00;

			// Esc break code
			Arrays.fill(n_keys, (byte) 0);
			n_keys[0] = 2;
			n_keys[1] = (byte) 0xF0;
			n_keys[2] = (byte) 0x76;
			n_keys[3] = (byte) 0x00;

		}

		public void OnTab() {
			// Tab make code
			Arrays.fill(n_keys, (byte) 0);
			n_keys[0] = 0;
			n_keys[1] = (byte) 0x0D;
			n_keys[2] = (byte) 0x00;
			n_keys[3] = (byte) 0x00;

			// Tab break code
			Arrays.fill(n_keys, (byte) 0);
			n_keys[0] = 2;
			n_keys[1] = (byte) 0xF0;
			n_keys[2] = (byte) 0x0D;
			n_keys[3] = (byte) 0x00;

		}

		public void OnCaps(final String arr) {
			int l = 0;
			int limit;
			int startIndex = 16;
			l=16;
			limit = 64;
			if(lngindex == JVMenu.LANGUAGE_JAPANESE_Q ||
					lngindex == JVMenu.LANGUAGE_JAPANESE_H ||
					lngindex == JVMenu.LANGUAGE_JAPANESE_K){// tochange the limits for Japanese softkeyboard
				//For japanese layouts start from index 17
				l = 17;
				limit = 65;
				startIndex = 17;
			}
			try{
				if(lngindex == JVMenu.LANGUAGE_FRENCH){
					//For French-France layout, shifted keys should mapped from index 17. 
					if(m_toggleKey[2].isSelected() == true || m_toggleKey[9].isSelected() == true){
						//key index 16 will not have any character when shift is pressed.
						m_butKey[l].setText(" ");
						l = 17;
						startIndex = 17;
					}
				}
				for (; l < limit; l++) {
					if (m_butKey[l].getName().equals("Key" + l)||m_butKey[l].getName().equals("Key" + 104)) {
						m_butKey[l].setText(String.valueOf(arr.charAt((l - startIndex))));// keylabel ends in 15
						m_butKey[l].setVisible(true);
						JDialogContentPane.repaint();
					}
				}
			}catch(final Exception e){
				Debug.out.println(e);
			}
			JDialogContentPane.repaint();
		}

		public void OnJapShift(final String arr) {
			for (int l = 17; l <64; l++) {
				if (m_butKey[l].getName().equals("Key" + l)) {
					m_butKey[l].setText(String.valueOf(arr.charAt((l - 16))));// keylabel ends in 15
					m_butKey[l].setVisible(true);
					JDialogContentPane.repaint();
				}
			}
			JDialogContentPane.repaint();
		}

		public void OnNum(final String arr) {
			for (int l = 83; l <= 93; l++) {
				if(l == 93){//Numpad Decimal(Del)
					//The layouts mentioned in the EXCEPT_LAYOUTS should have
					// , instead of . on the decimal key on the Number pad.
					if(EXCEPT_LAYOUTS.contains(lngindex)){
						m_butKey[l].setText(",");
						continue;
					}
				}
				m_butKey[l].setText(String.valueOf(arr.charAt((l - 83))));

			}
		}

		public void OnNum(final String[] arr) {
			for (int l = 83; l <= 93; l++) {
				m_butKey[l].setText(arr[l - 83]);

			}
		}

		public void OnAltGrOn(final String arr) {
			for (int l = 15; l <= 63; l++) {
				m_butKey[l].setText(" ");
			}
			for (int k = 64; k <= 74; k++) {
				switch (k) {
				case 64:
					m_butKey[64].setText("tab");
					break;
				case 65:
					m_toggleKey[1].setText(" ");
					break;
				case 66:
					m_toggleKey[2].setText(" ");
					break;
				case 67:
					m_toggleKey[3].setText(" ");
					break;
				case 69:
					m_toggleKey[5].setText(" ");
					break;
				case 72:
					m_toggleKey[8].setText(" ");
					break;
				case 73:
					m_toggleKey[9].setText(" ");
					break;
				case 74:
					m_butKey[65].setText(" ");
					break;				
				}
			}
			// if shift and alt is enabled
			if(m_toggleKey[2].isSelected() == true || m_toggleKey[9].isSelected() == true)
			{
				for (int i = 0; i < KeyProcessor.shiftedAltGrIndex[lngindex].length; i++) {
					int index = new Integer(KeyProcessor.shiftedAltGrIndex[lngindex][i]);
					if(index >= 0)
						m_butKey[index].setText(String.valueOf(arr.charAt(i)));
				}
			}
			else{ // if alt is enabled
				for (int i = 0; i < KeyProcessor.altGrIndex[lngindex].length; i++) {
					int index = new Integer(KeyProcessor.altGrIndex[lngindex][i]);
					if(index >= 0)
						m_butKey[index].setText(String.valueOf(arr.charAt(i)));
				}
			}
		}

		public void AltGrOff() {
			m_butKey[15].setText("brk");
			m_toggleKey[1].setText("caps");
			m_toggleKey[2].setText("shift");
			m_toggleKey[3].setText("ctrl");
			m_toggleKey[5].setText("alt");
			m_toggleKey[8].setText("ctrl");
			m_toggleKey[9].setText("shift");
			m_butKey[66].setText("ent");
		}

		// Release toggle keys if on
		public void close() {
			try {				
				if (m_toggleKey[2].isSelected() == true || m_toggleKey[9].isSelected() == true) {
					keyRep.set(16, 2, false);
					m_KVMClnt.sendKMMessage(keyRep);
				}
				if (m_toggleKey[3].isSelected() == true){
					OnModifier(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_LEFT,
							KeyEvent.KEY_RELEASED);
				}
					
				if (m_toggleKey[8].isSelected() == true){
					OnModifier(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT,
							KeyEvent.KEY_RELEASED);
				}
				if (m_toggleKey[4].isSelected() == true)
					OnModifier(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_LEFT,
							KeyEvent.KEY_RELEASED);
				if (m_toggleKey[5].isSelected() == true)
					OnModifier(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_LEFT,
							KeyEvent.KEY_RELEASED);
				if (m_toggleKey[6].isSelected() == true)
					OnModifier(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT,
							KeyEvent.KEY_RELEASED);
				if (m_toggleKey[7].isSelected() == true)
					OnModifier(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_RIGHT, 
							KeyEvent.KEY_RELEASED);
			} catch (final Exception e1) {
				Debug.out.println(e1);
			}
		}
	}
	public  int getLngindex() {
		return lngindex;
	}


	public  void setLngindex(int lngindex) {
		SoftKeyboard.lngindex = lngindex;
	}

 	/**
 	 * This method synchorines Softkeyboard hold key sets (Ctrl/Alt/Windows) with menu items.
 	 */
	public void syncHoldKey() {	
		//Left Ctrl
		if(JViewerApp.getInstance().getM_wndFrame().getM_status().getLeftCtrl().isSelected() == false) {
			m_toggleKey[3].setSelected(false);
		}
		else {
			m_toggleKey[3].setSelected(true);
		}

		//Right Ctrl
		if(JViewerApp.getInstance().getM_wndFrame().getM_status().getRightCtrl().isSelected() == false) {	
			m_toggleKey[8].setSelected(false);			
		}
		else {
			m_toggleKey[8].setSelected(true);			
		}

		//Left Alt
		if(JViewerApp.getInstance().getM_wndFrame().getM_status().getLeftAlt().isSelected() == false) {
			m_toggleKey[5].setSelected(false);	
		}
		else {
			m_toggleKey[5].setSelected(true);				
		}

		//Right Alt
		if(JViewerApp.getInstance().getM_wndFrame().getM_status().getRightAlt().isSelected() == false) {
	
			if(lngindex != 16)
				m_skmouselistener.OnRightAltRelease();
			m_skmouselistener.OnModifier(KeyEvent.VK_ALT,
					KeyEvent.KEY_LOCATION_RIGHT,
					KeyEvent.KEY_RELEASED);
			m_toggleKey[6].setSelected(false);		
		}
		else {
			if ((lngindex != JVMenu.LANGUAGE_ENGLISH_US && lngindex != JVMenu.LANGUAGE_RUSSIAN &&
				lngindex != JVMenu.LANGUAGE_JAPANESE_Q && lngindex != JVMenu.LANGUAGE_JAPANESE_H &&
				lngindex != JVMenu.LANGUAGE_JAPANESE_K)){
				altGr_enable = true;
				if(m_toggleKey[2].isSelected() == true || m_toggleKey[9].isSelected() == true)
				{
					m_skmouselistener.OnAltGrOn(KeyProcessor.shiftedAltGrCharSet[lngindex]);
				}else
				{
					m_skmouselistener.OnAltGrOn(KeyProcessor.altGrCharSet[lngindex]);
				}
			}

			m_toggleKey[6].setSelected(true);
			m_skmouselistener.OnModifier(KeyEvent.VK_ALT,
					KeyEvent.KEY_LOCATION_RIGHT,
					KeyEvent.KEY_PRESSED);
		}

		// Left Windows
		if(JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.KEYBOARD_LEFT_WINKEY_PRESSHOLD).isSelected()) {
			m_toggleKey[4].setSelected(true);
		}
		else {
			m_toggleKey[4].setSelected(false);
		}

		// Right Windows
		if(JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.KEYBOARD_RIGHT_WINKEY_PRESSHOLD).isSelected()) {
			m_toggleKey[7].setSelected(true);
		}
		else {
			m_toggleKey[7].setSelected(false);
		}
	}
		
	/**
	 * This method synchorines Softkeyboard LED status with Host keyboard LED status.
	 */
	
	public void syncKbdLED(){	
		//final FloppyRedir keystate = new FloppyRedir(true);
		byte newLEDStatus = JViewerApp.getInstance().getHostKeyboardLEDStatus();
		//System.out.println("SYNC ENTER");
		
		if(clientKybdLED != newLEDStatus){
			clientKybdLED = newLEDStatus;
			JDialogContentPane.setVisible(false);
			if ((clientKybdLED & NUMLOCK) == 1) {
				m_toggleKey[10].setSelected(true);
				m_skmouselistener.OnNum(numpadkeyson);
			}
			else{
				m_toggleKey[10].setSelected(false);
				m_skmouselistener.OnNum(numpadkeysoff);
			}
			if ((clientKybdLED & CAPSLOCK) == 2) {
				m_toggleKey[1].setSelected(true);
				if(m_toggleKey[2].isSelected() == true || m_toggleKey[9].isSelected() == true)
					m_skmouselistener.OnCaps(KeyProcessor.shiftedCapsCharSet[lngindex]);
				else
					m_skmouselistener.OnCaps(KeyProcessor.normalCapsCharSet[lngindex]);
				m_butKey[15].setText("pau");
			}
			else{
				m_toggleKey[1].setSelected(false);
				
				if(m_toggleKey[2].isSelected() == true || m_toggleKey[9].isSelected() == true)
					m_skmouselistener.OnCaps(KeyProcessor.shiftedCharSet[lngindex]);
				else
					m_skmouselistener.OnCaps(KeyProcessor.normalCharSet[lngindex]);
				m_butKey[15].setText("brk");
			}

		if ((clientKybdLED & SCROLLLOCK) == 4) {
			m_toggleKey[0].setSelected(true);
		}
		else
			m_toggleKey[0].setSelected(false);

			JDialogContentPane.setVisible(true);
		}
	}

	/**
	 * This method Updates key's state enable/disable on power status cahnge
	 */
	public void OnUpdateKeyState(Boolean state){
		for(int i=0;i<m_butKey.length;i++)
		{
			if(m_butKey[i]!=null)
				m_butKey[i].setEnabled(state);
			if(i < m_toggleKey.length)
			{
				if(m_toggleKey[i]!=null)
					m_toggleKey[i].setEnabled(state);
			}
			if(i< japSpecialKeys.length)
			{
				if(japSpecialKeys[i]!=null)
					japSpecialKeys[i].setEnabled(state);
			}
		}	
	}
}