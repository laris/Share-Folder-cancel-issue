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
// USB keyboard report structure.
//

package com.ami.kvm.jviewer.hid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.kvmpkts.IVTPPktHdr;
import com.ami.kvm.jviewer.kvmpkts.KMCrypt;

public class USBKeyboardRep extends USBMessage {

	private int keyCode;
	private int keyLocation;
	private boolean keyPress;
	private char keyChar = KeyProcessor.NULL_CHAR;
	private byte[] m_report;
	private byte[] m_encReport;
	private ByteBuffer m_repBuf;
	private KeyProcessor	m_USBKeyProcessor;
	public boolean KeyBoardDataNull;
	static int SeqNum=0;

	/**
	 * The constructor.
	 */
	public USBKeyboardRep() {
		this(0, 0, true, KeyProcessor.NULL_CHAR);
	}

	/**
	 * The constructor.
	 */
	public USBKeyboardRep(int keyCode, int keyLocation, boolean keyPress, char keyChar) {
		// ivtp packet header
		m_vHdr = new IVTPPktHdr(IVTPPktHdr.IVTP_HID_PKT,
						IUSB_HID_HDR_SIZE - 1 + USB_PKT_KEYBDREP_SIZE, (short)0);
		// usb header
		m_devType = IUSB_DEVICE_KEYBD;
		m_protocol = IUSB_PROTO_KEYBD_DATA;
		m_dataLen = USB_PKT_KEYBDREP_SIZE;
		m_report = new byte[IVTPPktHdr.HDR_SIZE + IUSB_HID_HDR_SIZE - 1 + USB_PKT_KEYBDREP_SIZE];
		m_encReport = new byte[IVTPPktHdr.HDR_SIZE 	+ IUSB_HID_HDR_SIZE - 1 + USB_PKT_ENC_KEYBDREP_SIZE];

	}

	/**
	 * Set report contents
	 *
	 * @param keyType key type
	 * @param key0 first byte
	 * @param key1 second byte
	 * @param key2 third byte
	 */
	public void set(int keyCode, int keyLocation, boolean keyPress ) {

		this.keyCode = keyCode;
		this.keyLocation = keyLocation;
		this.keyPress = keyPress;

	}
	public void setKeyChar(char keyChar) {

		this.keyChar = keyChar;

	}

	/**
	 * Generate byte array of USB message.
	 *
	 * @return USB message array.
	 */
	public byte[] report() {

		int DataPktLen = IUSB_HID_HDR_SIZE - 1 + USB_PKT_KEYBDREP_SIZE - IUSB_HDR_SIZE;
		m_repBuf = ByteBuffer.wrap(m_report);
		m_repBuf.order(ByteOrder.LITTLE_ENDIAN);
		m_repBuf.position(0);
		// ivtp packet header
		m_vHdr.setSize(IUSB_HID_HDR_SIZE - 1 + USB_PKT_KEYBDREP_SIZE);
		m_vHdr.setStatus((short)0);
		m_repBuf.put(m_vHdr.array());
		// usb header
		m_repBuf.put(m_signature);
		m_repBuf.put(IUSB_MAJOR_NUM);
		m_repBuf.put(IUSB_MINOR_NUM);
		m_repBuf.put(IUSB_HDR_SIZE);
		m_repBuf.put( (byte)0 ); /* CheckSum. we initialize it to 0 for now. */
		m_repBuf.putInt(DataPktLen);
		m_repBuf.put( (byte)0 );
		m_repBuf.put(IUSB_DEVICE_KEYBD);
		m_repBuf.put(IUSB_PROTO_KEYBD_DATA);
		m_repBuf.put((byte)(IUSB_FROM_REMOTE & 0xff));
		m_repBuf.put(IUSB_KEYBD_DEVNUM);
		m_repBuf.put(IUSB_KEYBD_IFNUM);
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );
		m_repBuf.putInt( SeqNum );
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );

		m_repBuf.put( (byte)( USB_PKT_KEYBDREP_SIZE & 0xff ) ); /* DataLen */
		if(m_USBKeyProcessor == null)
			m_USBKeyProcessor = JViewerApp.getInstance().getKeyProcesssor();
		/* Keyboard report */
		byte [] keybd_report_data = m_USBKeyProcessor.convertKeyCode(this.keyCode, this.keyLocation, this.keyPress, this.keyChar);

		if( keybd_report_data == null ) {
			m_repBuf.put( new byte[6] );
			KeyBoardDataNull = true;
		}	
		else
			m_repBuf.put( keybd_report_data );

        /* Calculate modulo100 checksum */
        int temp = 0;
        for( int i = IVTPPktHdr.HDR_SIZE; i < (IUSB_HDR_SIZE+IVTPPktHdr.HDR_SIZE); i++ )
            temp = ( temp + (int)( m_repBuf.get( i ) & 0xff ) ) & 0xff;

        m_repBuf.put(IVTPPktHdr.HDR_SIZE+11, (byte)-( (byte)( temp & 0xff ) ) );
		/* increment the sequnce number */
		SeqNum++;
		return m_report;
	}

	/**
	 * Generate byte array of encrypted USB message.
	 *
	 * @param crypt crypt handler to be used.
	 * @return encrypted USB message array.
	 */
	public byte[] encryptedReport(KMCrypt crypt)
	{
		int DataPktLen = IUSB_HID_HDR_SIZE - 1 + USB_PKT_KEYBDREP_SIZE - IUSB_HDR_SIZE;
		m_repBuf = ByteBuffer.wrap(m_encReport);
		m_repBuf.order(ByteOrder.LITTLE_ENDIAN);
		m_repBuf.position(0);
		// ivtp packet header
		m_vHdr.setSize(IUSB_HID_HDR_SIZE - 1 + USB_PKT_ENC_KEYBDREP_SIZE);
		m_vHdr.setStatus((short)IVTPPktHdr.ENCRYPTION_ENABLED);
		m_repBuf.put(m_vHdr.array());
		// usb header
		m_repBuf.put(m_signature);
		m_repBuf.put(IUSB_MAJOR_NUM);
		m_repBuf.put(IUSB_MINOR_NUM);
		m_repBuf.put(IUSB_HDR_SIZE);
		m_repBuf.put( (byte)0 ); /* CheckSum. we initialize it to 0 for now. */
		m_repBuf.putInt(DataPktLen);
		m_repBuf.put( (byte)0 );
		m_repBuf.put(IUSB_DEVICE_KEYBD);
		m_repBuf.put(IUSB_PROTO_KEYBD_DATA);
		m_repBuf.put((byte)(IUSB_FROM_REMOTE & 0xff));
		m_repBuf.put(IUSB_KEYBD_DEVNUM);
		m_repBuf.put(IUSB_KEYBD_IFNUM);
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );
		m_repBuf.putInt( SeqNum );
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );

		m_repBuf.put( (byte)( USB_PKT_KEYBDREP_SIZE & 0xff ) ); /* DataLen */
		/* Keyboard report */
		byte [] keybd_report_data = m_USBKeyProcessor.convertKeyCode(this.keyCode, this.keyLocation, this.keyPress, this.keyChar);
		/* if any error occurs send the unencrypted data */
		if( keybd_report_data == null )
			return (report());
		/* Encrypt the data */
		byte[] crypt_data = crypt.encrypt(keybd_report_data);
		/* if any error, send unencrypted data */
		if(crypt_data == null)
			return(report());

		m_repBuf.put( crypt_data );
        /* Calculate modulo100 checksum */
        int temp = 0;
        for( int i = IVTPPktHdr.HDR_SIZE; i < (IUSB_HDR_SIZE+IVTPPktHdr.HDR_SIZE); i++ )
            temp = ( temp + (int)( m_repBuf.get( i ) & 0xff ) ) & 0xff;

        m_repBuf.put(IVTPPktHdr.HDR_SIZE+11, (byte)-( (byte)( temp & 0xff ) ) );
		/* increment the sequnce number */
		SeqNum++;
		return m_encReport;
	}

    public void setAutoKeybreakMode( boolean state )
    {
        m_USBKeyProcessor.setAutoKeybreakMode( state );
    }

    public boolean getAutoKeybreakMode()
    {
        return( m_USBKeyProcessor.getAutoKeybreakMode() );
    }

	public KeyProcessor getM_USBKeyProcessor() {
		return m_USBKeyProcessor;
	}

	public void setM_USBKeyProcessor(KeyProcessor keyProcessor) {
		m_USBKeyProcessor = keyProcessor;
	}

}
