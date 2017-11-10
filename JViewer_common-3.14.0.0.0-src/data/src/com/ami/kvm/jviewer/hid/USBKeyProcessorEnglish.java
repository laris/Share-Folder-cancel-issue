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
/*
 * USBKeyProcessor.java
 *
 * Created on January 13, 2005, 11:19 AM
 * Modified to G4 on Feb 17, 2006, 11:55 AM
 */

package com.ami.kvm.jviewer.hid;

import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.gui.LocaleStrings;

public class USBKeyProcessorEnglish implements KeyProcessor
{
    protected HashMap<Integer, Integer> standardMap;
    protected HashMap<Integer, Integer> keypadMap;
    protected HashMap<Integer, Integer> leftMap;
    protected HashMap<Integer, Integer> rightMap;
    //The modifier member should be initialized only at the beginning, 
    //and this member should be common for all the instances of this class. 
    protected static byte modifiers = 0;
    protected boolean autoKeybreakModeOn = false;
    final int	USB_KEYBOARD_REPORT_SIZE = 8;

    /**
     * Creates a new instance of USBKeyProcessor
     */
    public USBKeyProcessorEnglish()
    {
        standardMap = new HashMap<Integer, Integer>();
        keypadMap = new HashMap<Integer, Integer>();
        leftMap = new HashMap<Integer, Integer>();
        rightMap = new HashMap<Integer, Integer>();

        /* You can take my word for the fact that this was far more boring  */
        /* for me to write than it is for you to look at.  It's annoying,   */
        /* but unavoidable.  Sun makes no promises as to the values of any  */
        /* of the VK_* constants, and reserves the right to change them at  */
        /* any time, so here we are.  Keeping them in a hashmap should make */
        /* the lookup pretty quick at least.                                */
        standardMap.put( KeyEvent.VK_A,  4 );
        standardMap.put( KeyEvent.VK_B,  5 );
        standardMap.put( KeyEvent.VK_C,  6 );
        standardMap.put( KeyEvent.VK_D,  7 );
        standardMap.put( KeyEvent.VK_E,  8 );
        standardMap.put( KeyEvent.VK_F,  9 );
        standardMap.put( KeyEvent.VK_G, 10 );
        standardMap.put( KeyEvent.VK_H, 11 );
        standardMap.put( KeyEvent.VK_I, 12 );
        standardMap.put( KeyEvent.VK_J, 13 );
        standardMap.put( KeyEvent.VK_K, 14 );
        standardMap.put( KeyEvent.VK_L, 15 );
        standardMap.put( KeyEvent.VK_M, 16 );
        standardMap.put( KeyEvent.VK_N, 17 );
        standardMap.put( KeyEvent.VK_O, 18 );
        standardMap.put( KeyEvent.VK_P, 19 );
        standardMap.put( KeyEvent.VK_Q, 20 );
        standardMap.put( KeyEvent.VK_R, 21 );
        standardMap.put( KeyEvent.VK_S, 22 );
        standardMap.put( KeyEvent.VK_T, 23 );
        standardMap.put( KeyEvent.VK_U, 24 );
        standardMap.put( KeyEvent.VK_V, 25 );
        standardMap.put( KeyEvent.VK_W, 26 );
        standardMap.put( KeyEvent.VK_X, 27 );
        standardMap.put( KeyEvent.VK_Y, 28 );
        standardMap.put( KeyEvent.VK_Z, 29 );
        standardMap.put( KeyEvent.VK_1, 30 );
        standardMap.put( KeyEvent.VK_2, 31 );
        standardMap.put( KeyEvent.VK_3, 32 );
        standardMap.put( KeyEvent.VK_4, 33 );
        standardMap.put( KeyEvent.VK_5, 34 );
        standardMap.put( KeyEvent.VK_6, 35 );
        standardMap.put( KeyEvent.VK_7, 36 );
        standardMap.put( KeyEvent.VK_8, 37 );
        standardMap.put( KeyEvent.VK_9, 38 );
        standardMap.put( KeyEvent.VK_0, 39 );
        standardMap.put( KeyEvent.VK_ENTER, 40 );
        standardMap.put( KeyEvent.VK_ESCAPE, 41 );
        standardMap.put( KeyEvent.VK_BACK_SPACE, 42 );
        standardMap.put( KeyEvent.VK_TAB, 43 );
        standardMap.put( KeyEvent.VK_SPACE, 44 );
        standardMap.put( KeyEvent.VK_MINUS, 45 );
        standardMap.put( KeyEvent.VK_EQUALS, 46 );
        standardMap.put( KeyEvent.VK_OPEN_BRACKET, 47 );
        standardMap.put( KeyEvent.VK_CLOSE_BRACKET, 48 );
        standardMap.put( KeyEvent.VK_BACK_SLASH, 49 );
        //standardMap.put( KeyEvent.VK_???, 50 );
        standardMap.put( KeyEvent.VK_SEMICOLON, 51 );
        standardMap.put( KeyEvent.VK_QUOTE, 52 );
        standardMap.put( KeyEvent.VK_BACK_QUOTE, 53 );
        standardMap.put( KeyEvent.VK_COMMA, 54 );
        standardMap.put( KeyEvent.VK_PERIOD, 55 );
        standardMap.put( KeyEvent.VK_SLASH, 56 );
        standardMap.put( KeyEvent.VK_CAPS_LOCK, 57 );
        standardMap.put( KeyEvent.VK_F1, 58 );
        standardMap.put( KeyEvent.VK_F2, 59 );
        standardMap.put( KeyEvent.VK_F3, 60 );
        standardMap.put( KeyEvent.VK_F4, 61 );
        standardMap.put( KeyEvent.VK_F5, 62 );
        standardMap.put( KeyEvent.VK_F6, 63 );
        standardMap.put( KeyEvent.VK_F7, 64 );
        standardMap.put( KeyEvent.VK_F8, 65 );
        standardMap.put( KeyEvent.VK_F9, 66 );
        standardMap.put( KeyEvent.VK_F10, 67 );
        standardMap.put( KeyEvent.VK_F11, 68 );
        standardMap.put( KeyEvent.VK_F12, 69 );
        standardMap.put( KeyEvent.VK_PRINTSCREEN, 70 );
        standardMap.put( KeyEvent.VK_SCROLL_LOCK, 71 );
        standardMap.put( KeyEvent.VK_PAUSE, 72 );
        standardMap.put( KeyEvent.VK_INSERT, 73 );
        standardMap.put( KeyEvent.VK_HOME, 74 );
        standardMap.put( KeyEvent.VK_PAGE_UP, 75 );
        standardMap.put( KeyEvent.VK_DELETE, 76 );
        standardMap.put( KeyEvent.VK_END, 77 );
        standardMap.put( KeyEvent.VK_PAGE_DOWN, 78 );
        standardMap.put( KeyEvent.VK_RIGHT, 79 );
        standardMap.put( KeyEvent.VK_LEFT, 80 );
        standardMap.put( KeyEvent.VK_DOWN, 81 );
        standardMap.put( KeyEvent.VK_UP, 82 );
        standardMap.put( KeyEvent.VK_SUBTRACT, 86 );

        keypadMap.put( KeyEvent.VK_NUM_LOCK, 83 );
        keypadMap.put( KeyEvent.VK_DIVIDE, 84 );
        keypadMap.put( KeyEvent.VK_MULTIPLY, 85 );
        keypadMap.put( KeyEvent.VK_SUBTRACT, 86 );
        keypadMap.put( KeyEvent.VK_ADD, 87 );
        keypadMap.put( KeyEvent.VK_ENTER, 88 );
        keypadMap.put( KeyEvent.VK_NUMPAD1, 89 );
        keypadMap.put( KeyEvent.VK_END, 89 );
        keypadMap.put( KeyEvent.VK_NUMPAD2, 90 );
        keypadMap.put( KeyEvent.VK_DOWN, 90 );
        keypadMap.put( KeyEvent.VK_KP_DOWN, 90 );
        keypadMap.put( KeyEvent.VK_NUMPAD3, 91 );
        keypadMap.put( KeyEvent.VK_PAGE_DOWN, 91 );
        keypadMap.put( KeyEvent.VK_NUMPAD4, 92 );
        keypadMap.put( KeyEvent.VK_LEFT, 92 );
        keypadMap.put( KeyEvent.VK_KP_LEFT, 92 );
        keypadMap.put( KeyEvent.VK_NUMPAD5, 93 );
        keypadMap.put( KeyEvent.VK_BEGIN, 93 );
        keypadMap.put( KeyEvent.VK_NUMPAD6, 94 );
        keypadMap.put( KeyEvent.VK_RIGHT, 94 );
        keypadMap.put( KeyEvent.VK_KP_RIGHT, 94 );
        keypadMap.put( KeyEvent.VK_NUMPAD7, 95 );
        keypadMap.put( KeyEvent.VK_HOME, 95 );
        keypadMap.put( KeyEvent.VK_NUMPAD8, 96 );
        keypadMap.put( KeyEvent.VK_UP, 96 );
        keypadMap.put( KeyEvent.VK_KP_UP, 96 );
        keypadMap.put( KeyEvent.VK_NUMPAD9, 97 );
        keypadMap.put( KeyEvent.VK_PAGE_UP, 97 );
        keypadMap.put( KeyEvent.VK_NUMPAD0, 98 );
        keypadMap.put( KeyEvent.VK_INSERT, 98 );
        keypadMap.put( KeyEvent.VK_DECIMAL, 99 );
        keypadMap.put( KeyEvent.VK_DELETE, 76 );
        //map.put( KeyEvent.VK_???, 100 );
        //map.put( KeyEvent.VK_???, 101 );
        //map.put( KeyEvent.VK_???, 102 );
        standardMap.put(KeyEvent.VK_LESS, 100);
        keypadMap.put( KeyEvent.VK_EQUALS, 103 );

        standardMap.put( 525, 101 ); //keycode for rightclick event in the keyboard(Apps key)adjacent to right alt
        standardMap.put( KeyEvent.VK_F13, 104 );
        standardMap.put( KeyEvent.VK_F14, 105 );
        standardMap.put( KeyEvent.VK_F15, 106 );
        standardMap.put( KeyEvent.VK_F16, 107 );
        standardMap.put( KeyEvent.VK_F17, 108 );
        standardMap.put( KeyEvent.VK_F18, 109 );
        standardMap.put( KeyEvent.VK_F19, 110 );
        standardMap.put( KeyEvent.VK_F20, 111 );
        standardMap.put( KeyEvent.VK_F21, 112 );
        standardMap.put( KeyEvent.VK_F22, 113 );
        standardMap.put( KeyEvent.VK_F23, 114 );
        standardMap.put( KeyEvent.VK_F24, 115 );
//        map.put( KeyEvent.VK_???, 116 );
        standardMap.put( KeyEvent.VK_HELP, 117 );
//        map.put( KeyEvent.VK_???, 118 );
//        map.put( KeyEvent.VK_???, 119 );
        standardMap.put( KeyEvent.VK_STOP, 120 );
        standardMap.put( KeyEvent.VK_AGAIN, 121 );
        standardMap.put( KeyEvent.VK_UNDO, 122 );
        standardMap.put( KeyEvent.VK_CUT, 123 );
        standardMap.put( KeyEvent.VK_COPY, 124 );
        standardMap.put( KeyEvent.VK_PASTE, 125 );
        standardMap.put( KeyEvent.VK_FIND, 126 );
//        map.put( KeyEvent.VK_???, 127 );
        /* Skip a bunch... */
        standardMap.put( KeyEvent.VK_CANCEL, 155 );
        standardMap.put( KeyEvent.VK_CLEAR, 156 );
//        map.put( KeyEvent.VK_???, 157 );
//        map.put( KeyEvent.VK_???, 158 );
        standardMap.put( KeyEvent.VK_SEPARATOR, 159 );
        standardMap.put(VK_102KEY, 100);
       //japanese special keys for softkeyboard layout
        standardMap.put(999, 135);
        standardMap.put(240, 136);
        standardMap.put(220, 137);
        standardMap.put(28, 138);
        standardMap.put(29, 139);
        standardMap.put(243, 53);
        standardMap.put(244, 53);
        standardMap.put(242, 57); // caps lock
        standardMap.put(263, 53); // for VK_BACK_QUOTE in japanese
        /* Skip some more... */

        leftMap.put( KeyEvent.VK_CONTROL, 224 );
        leftMap.put( KeyEvent.VK_SHIFT, 225 );
        leftMap.put( KeyEvent.VK_ALT, 226 );
        leftMap.put( KeyEvent.VK_WINDOWS, 227 );

        rightMap.put( KeyEvent.VK_CONTROL, 228 );
        rightMap.put( KeyEvent.VK_SHIFT, 229 );
        rightMap.put( KeyEvent.VK_ALT, 230 );
        rightMap.put( KeyEvent.VK_WINDOWS, 231 );



    }

    public void setAutoKeybreakMode( boolean state )
    {
        autoKeybreakModeOn = state;
    }

    public boolean getAutoKeybreakMode()
    {
        return( autoKeybreakModeOn );
    }

    public byte[] convertKeyCode( int keyCode, int keyLocation, boolean keyPress, char keyChar)
    {
        Integer usbKeyCode = new Integer(0);
        byte[] outputKeycodes = new byte[ 6 ];
        boolean sendAutoKeybreak = false;

        switch( keyLocation )
        {
            case KeyEvent.KEY_LOCATION_LEFT:
                usbKeyCode = leftMap.get( keyCode );
                break;

            case KeyEvent.KEY_LOCATION_NUMPAD:
            	usbKeyCode = keypadMap.get( keyCode );
                break;

            case KeyEvent.KEY_LOCATION_RIGHT:
                usbKeyCode = rightMap.get( keyCode );
                break;

            case KeyEvent.KEY_LOCATION_STANDARD:
                usbKeyCode = standardMap.get( keyCode );
                break;

            case KeyEvent.KEY_LOCATION_UNKNOWN:
                System.err.println(LocaleStrings.getString("AD_1_USBKP"));
                break;

            default:
                System.err.println(LocaleStrings.getString("AD_2_USBKP"));
                break;
        }

        if( usbKeyCode != null ) {

            switch( keyCode )
            {
                case KeyEvent.VK_CONTROL:
                    if( keyLocation == KeyEvent.KEY_LOCATION_LEFT ) {
                        if( keyPress )
                            modifiers |= MOD_LEFT_CTRL;
                        else
                            modifiers &= ~MOD_LEFT_CTRL;
                    } else {
                        if( keyPress )
                            modifiers |= MOD_RIGHT_CTRL;
                        else
                            modifiers &= ~MOD_RIGHT_CTRL;
                    }
                    break;

                case KeyEvent.VK_SHIFT:
                    if( keyLocation == KeyEvent.KEY_LOCATION_LEFT ) {
                        if( keyPress )
                            modifiers |= MOD_LEFT_SHIFT;
                        else {
                            modifiers &= ~MOD_LEFT_SHIFT;
                            modifiers &= ~MOD_RIGHT_SHIFT;
                        }
                    } else {
                        if( keyPress )
                            modifiers |= MOD_RIGHT_SHIFT;
                        else {
                            modifiers &= ~MOD_RIGHT_SHIFT;
                            modifiers &= ~MOD_LEFT_SHIFT;
                        }
                    }
                    break;

                case KeyEvent.VK_ALT:
                    if( keyLocation == KeyEvent.KEY_LOCATION_LEFT ) {
                        if( keyPress )
                            modifiers |= MOD_LEFT_ALT;
                        else
                            modifiers &= ~MOD_LEFT_ALT;
                    } else {
                        if( keyPress )
                            modifiers |= MOD_RIGHT_ALT;
                        else
                            modifiers &= ~MOD_RIGHT_ALT;
                    }
                    break;

                case KeyEvent.VK_WINDOWS:
                    if( keyLocation == KeyEvent.KEY_LOCATION_LEFT ) {
                        if( keyPress )
                            modifiers |= MOD_LEFT_WIN;
                        else
                            modifiers &= ~MOD_LEFT_WIN;
                    } else {
                        if( keyPress )
                            modifiers |= MOD_RIGHT_WIN;
                        else
                            modifiers &= ~MOD_RIGHT_WIN;
                    }
                    break;

                default:
                    if( keyPress || (keyCode == KeyEvent.VK_PRINTSCREEN)) {
                        outputKeycodes[ 0 ] = usbKeyCode.byteValue();
                        if( autoKeybreakModeOn )
                            sendAutoKeybreak = true;
                    } else {
                        if( !autoKeybreakModeOn ||
                                ( keyCode == KeyEvent.VK_NUM_LOCK ) ||
                                ( keyCode == KeyEvent.VK_CAPS_LOCK ) ||
                                ( keyCode == KeyEvent.VK_SCROLL_LOCK ) )
                            outputKeycodes[ 0 ] = 0;
                        else
                            return( null );
                    }
                    break;
            }

            return( USBKeyboardRepPkt( outputKeycodes, modifiers, sendAutoKeybreak ) );
        } else {
        	Debug.out.println(LocaleStrings.getString("AD_3_USBKP") + KeyEvent.getKeyText( keyCode ) );
            return( null );
        }
    }


	/**
	 * Generates a USB_KEYBOARD_REPORT_T structure and returns the bytes
	 *
	 * @param crypt crypt handler to be used.
	 * @return encrypted USB message array.
	 */

	public byte[] USBKeyboardRepPkt(byte[] Keys, byte Modifiers, boolean AutoKeyBreak)
	{
		/*
			A typical C structure of USB keyboard report
			typedef struct {
				BYTE	Modifiers;
				BYTE	Reserved;
				BYTE	Keys[6];
			} USB_KEYBOARD_REPORT;
		*/

		ByteBuffer	USBReport;
		USBReport = ByteBuffer.allocate(USB_KEYBOARD_REPORT_SIZE);
		USBReport.order(ByteOrder.LITTLE_ENDIAN);
		/* Add modifiers */
		USBReport.put(Modifiers);
		/* Add reserved field. Here we use this field for autokeybreak */
		USBReport.put( (AutoKeyBreak)?(byte)1:(byte)0 );
		/* Add key data */
		USBReport.put( Keys );
		return ( USBReport.array() );
	}

	/**
	 * @return the modifiers
	 */
	public static byte getModifiers() {
		return modifiers;
	}

	/**
	 * @param modifiers the modifiers to set
	 */
	public static void setModifiers(byte modifiers) {
		USBKeyProcessorEnglish.modifiers = modifiers;
	}
}
