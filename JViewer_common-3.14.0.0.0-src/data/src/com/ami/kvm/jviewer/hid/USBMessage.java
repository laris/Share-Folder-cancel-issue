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
// Structure to hold USB message header and constants. This is extended by
// keyboard and mouse specific message structures
//

package com.ami.kvm.jviewer.hid;

import com.ami.kvm.jviewer.kvmpkts.IVTPPktHdr;
import com.ami.kvm.jviewer.kvmpkts.KMCrypt;

public abstract class USBMessage {

	public static final int IUSB_HID_HDR_SIZE 		= 34;
	public static final int USB_PKT_KEYBDREP_SIZE	= 8;
	public static final int USB_PKT_MOUSE_REL_REP_SIZE 	= 4;
	public static final int USB_PKT_MOUSE_ABS_REP_SIZE 	= 6;
	public static final int USB_PKT_ENC_KEYBDREP_SIZE = 8;
	public static final int USB_PKT_ENC_MOUSEREP_SIZE = 8;
	public static final byte IUSB_HDR_SIZE			= 32;
	public static final byte IUSB_PROTO_KEYBD_DATA	= 0x10;
	public static final byte IUSB_PROTO_MOUSE_DATA 	= 0x20;
	public static final byte IUSB_DEVICE_KEYBD       = 0x30;
	public static final byte IUSB_DEVICE_MOUSE       = 0x31;
	public static final byte IUSB_MAJOR_NUM	       = 0x01;
	public static final byte IUSB_MINOR_NUM	       = 0x00;
	public static final byte IUSB_KEYBD_DEVNUM	   = 0x02;
	public static final byte IUSB_KEYBD_IFNUM	   = 0x00;
	public static final byte IUSB_MOUSE_DEVNUM	   = 0x02;
	public static final byte IUSB_MOUSE_IFNUM	   = 0x01;
	public static final int IUSB_FROM_REMOTE	   = 0x80;
	public static final int IUSB_TO_REMOTE	   		= 0x00;

	// ivtp packet header
	public IVTPPktHdr m_vHdr;
	// usb packet header
	public byte[] m_signature = {	'I', 'U', 'S', 'B', ' ', ' ', ' ', ' ' };
	public byte m_devType;
	public byte m_protocol;
	public int m_dataLen;
	public byte[] m_rsvd = new byte[2];

	/**
	 * Generate byte array of USB message.
	 *
	 * @return USB message array.
	 */
	public abstract byte[] report();

	/**
	 * Generate byte array of encrypted USB message.
	 *
	 * @param crypt crypt handler to be used.
	 * @return encrypted USB message array.
	 */
	public abstract byte[] encryptedReport(KMCrypt crypt);
}