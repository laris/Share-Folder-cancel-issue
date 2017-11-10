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
// USB mouse report structure.
//

package com.ami.kvm.jviewer.hid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.kvmpkts.IVTPPktHdr;
import com.ami.kvm.jviewer.kvmpkts.KMCrypt;

public class USBMouseRep extends USBMessage {

    private static final int DIRABS_MAX_SCALED_X = 32767;
    private static final int DIRABS_MAX_SCALED_Y = 32767;

	public static byte		INVALID_MOUSE_MODE	= -1;
	public static byte		RELATIVE_MOUSE_MODE	= 1;
	public static byte		ABSOLUTE_MOUSE_MODE	= 2;
	public static byte		OTHER_MOUSE_MODE	= 3;

	private byte m_MouseMode = INVALID_MOUSE_MODE;
	public static byte m_btnStatus=0;
	public double m_xDisp;
	public double m_yDisp;
	private ByteBuffer m_repBuf;
	private byte[] m_report;
	private static int SeqNum=0;
	private	double m_screenwidth;
	private double m_screenheight;
	private byte m_wheelrotation;


	/**
	 * The constructor.
	 */
	public USBMouseRep() {

		this((byte)0, 0, 0,(byte)0);
	}

	/**
	 * The constructor.
	 */
	public USBMouseRep(byte btnStatus, int xDisp, int yDisp,byte wheelrotation) {

		m_btnStatus = btnStatus;
		m_xDisp = xDisp;
		m_yDisp = yDisp;
		m_wheelrotation = wheelrotation;
	}

	/**
	 * Set report contents
	 *
	 * @param btnStatus button status
	 * @param xDisp x displacement
	 * @param yDisp y displacement
	 */
	public void set(byte btnStatus, double xDisp, double yDisp, byte MouseMode,int view_width, int view_height,byte Wheelrotation ) {

		m_btnStatus = btnStatus;
		m_xDisp = xDisp;
		m_yDisp = yDisp;
		m_MouseMode = MouseMode;
		m_screenwidth = view_width;
		m_screenheight = view_height;
		//check this condition only for pilot-ii or also
		if(m_screenwidth == 832) {
			m_screenwidth = 800;
		}
		m_wheelrotation = Wheelrotation;
	}

	/**
	 * Generate byte array of USB ABsolute mouse packet.
	 *
	 * @return USB Absolute packet array.
	 */
	public byte[] ABSreport(KMCrypt crypt) {

		short X = (short)( ( m_xDisp * DIRABS_MAX_SCALED_X ) / m_screenwidth + 0.5 );
		short Y = (short)( ( m_yDisp * DIRABS_MAX_SCALED_Y ) / m_screenheight + 0.5 );

		if ( crypt != null ) {
			m_vHdr = new IVTPPktHdr(IVTPPktHdr.IVTP_HID_PKT,
							IUSB_HID_HDR_SIZE - 1 + USB_PKT_ENC_MOUSEREP_SIZE, (short)0);
			m_report = new byte[IVTPPktHdr.HDR_SIZE
								+ IUSB_HID_HDR_SIZE - 1
								+ USB_PKT_ENC_MOUSEREP_SIZE];
		} else {
			m_vHdr = new IVTPPktHdr(IVTPPktHdr.IVTP_HID_PKT,
							IUSB_HID_HDR_SIZE - 1 + USB_PKT_MOUSE_ABS_REP_SIZE, (short)0);
			m_report = new byte[IVTPPktHdr.HDR_SIZE
								+ IUSB_HID_HDR_SIZE - 1
								+ USB_PKT_MOUSE_ABS_REP_SIZE];
		}

		int DataPktLen = IUSB_HID_HDR_SIZE - 1 + USB_PKT_MOUSE_ABS_REP_SIZE - IUSB_HDR_SIZE;
		/* Byte Buffer */
		m_repBuf = ByteBuffer.wrap(m_report);
		m_repBuf.order(ByteOrder.LITTLE_ENDIAN);
		m_repBuf.position(0);
		// ivtp packet header
		if ( crypt != null ) {
			m_vHdr.setSize(IUSB_HID_HDR_SIZE - 1 + USB_PKT_ENC_MOUSEREP_SIZE);
			m_vHdr.setStatus((short)IVTPPktHdr.ENCRYPTION_ENABLED);
		} else {
			m_vHdr.setSize(IUSB_HID_HDR_SIZE - 1 + USB_PKT_MOUSE_ABS_REP_SIZE);
			m_vHdr.setStatus((short)0);
		}
		m_repBuf.put(m_vHdr.array());

		// usb header
		m_repBuf.put(m_signature);
		m_repBuf.put(IUSB_MAJOR_NUM);
		m_repBuf.put(IUSB_MINOR_NUM);
		m_repBuf.put(IUSB_HDR_SIZE);
		m_repBuf.put( (byte)0 ); /* CheckSum. we initialize it to 0 for now. */
		m_repBuf.putInt(DataPktLen);
		m_repBuf.put( (byte)0 );
		m_repBuf.put(IUSB_DEVICE_MOUSE);
		m_repBuf.put(IUSB_PROTO_MOUSE_DATA);
		m_repBuf.put((byte)(IUSB_FROM_REMOTE & 0xff));
		m_repBuf.put(IUSB_MOUSE_DEVNUM );
		m_repBuf.put(IUSB_MOUSE_IFNUM );
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );
		m_repBuf.putInt( SeqNum );
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)0 );
		m_repBuf.put( (byte)( USB_PKT_MOUSE_ABS_REP_SIZE & 0xff ) ); /* DataLen */

		/* Absolute mouse data */
		/*
			typedef struct
			{
				BYTE	Event;
				WORD	X;
				WORD	Y;
			} USB_ABS_MOUSE_REPORT_T;
		*/
		if ( crypt != null ) {
			byte [] orig_data = new byte[USB_PKT_MOUSE_ABS_REP_SIZE];
			ByteBuffer tmpbuf = ByteBuffer.wrap(orig_data);
			tmpbuf.order(ByteOrder.LITTLE_ENDIAN);
			/* write the mouse buffer data to the temporary buffer */
			tmpbuf.put(m_btnStatus);
			tmpbuf.putShort( X );
			tmpbuf.putShort( Y );
			tmpbuf.put(m_wheelrotation );
			/* Encrypt the data */
			byte[] encrypt_data = crypt.encrypt(orig_data);
			/* if any error send unencrypted data */

			if( encrypt_data == null )
				return ABSreport(null);
			/* write encrypted data */
			m_repBuf.put(encrypt_data);
		} else {
			m_repBuf.put(m_btnStatus);
			m_repBuf.putShort( X );
			m_repBuf.putShort( Y );
			m_repBuf.put(m_wheelrotation );
		}

        /* Calculate modulo100 checksum */
        int temp = 0;
        for( int i = IVTPPktHdr.HDR_SIZE; i < (IUSB_HDR_SIZE+IVTPPktHdr.HDR_SIZE); i++ )
            temp = ( temp + (int)( m_repBuf.get( i ) & 0xff ) ) & 0xff;

        m_repBuf.put(IVTPPktHdr.HDR_SIZE+11, (byte)-( (byte)( temp & 0xff ) ) );
		SeqNum++;
		//Debug.out.dump(m_report);
		return m_report;
	}

	/**
	 * Generate byte array of USB Relative mouse packet.
	 *
	 * @return USB Relative packet array.
	 */
	public byte[] RELreport(KMCrypt crypt) {

		/* For encrypted data the report size will be always 8 */
		if ( crypt != null ) {
			m_vHdr = new IVTPPktHdr(IVTPPktHdr.IVTP_HID_PKT,
						IUSB_HID_HDR_SIZE - 1 + USB_PKT_ENC_MOUSEREP_SIZE, (short)0);
			m_report = new byte[IVTPPktHdr.HDR_SIZE
								+ IUSB_HID_HDR_SIZE - 1
								+ USB_PKT_ENC_MOUSEREP_SIZE];
		} else {
			m_vHdr = new IVTPPktHdr(IVTPPktHdr.IVTP_HID_PKT,
						IUSB_HID_HDR_SIZE - 1 + USB_PKT_MOUSE_REL_REP_SIZE, (short)0);
			m_report = new byte[IVTPPktHdr.HDR_SIZE
								+ IUSB_HID_HDR_SIZE - 1
								+ USB_PKT_MOUSE_REL_REP_SIZE];
		}
		int DataPktLen = IUSB_HID_HDR_SIZE - 1 + USB_PKT_MOUSE_REL_REP_SIZE - IUSB_HDR_SIZE;
		/* Byte Buffer */
		m_repBuf = ByteBuffer.wrap(m_report);
		m_repBuf.order(ByteOrder.LITTLE_ENDIAN);
		m_repBuf.position(0);
		// ivtp packet header
		if ( crypt != null ) {
			m_vHdr.setSize(IUSB_HID_HDR_SIZE - 1 + USB_PKT_ENC_MOUSEREP_SIZE);
			m_vHdr.setStatus((short)IVTPPktHdr.ENCRYPTION_ENABLED);
		} else {
			m_vHdr.setSize(IUSB_HID_HDR_SIZE - 1 + USB_PKT_MOUSE_REL_REP_SIZE);
			m_vHdr.setStatus((short)0);
		}
		try{
			m_repBuf.put(m_vHdr.array());
			// usb header
			m_repBuf.put(m_signature);
			m_repBuf.put(IUSB_MAJOR_NUM);
			m_repBuf.put(IUSB_MINOR_NUM);
			m_repBuf.put(IUSB_HDR_SIZE);
			m_repBuf.put( (byte)0 ); /* CheckSum. we initialize it to 0 for now. */
			m_repBuf.putInt(DataPktLen);
			m_repBuf.put( (byte)0 );
			m_repBuf.put(IUSB_DEVICE_MOUSE);
			m_repBuf.put(IUSB_PROTO_MOUSE_DATA);
			m_repBuf.put((byte)(IUSB_FROM_REMOTE & 0xff));
			m_repBuf.put(IUSB_MOUSE_DEVNUM );
			m_repBuf.put(IUSB_MOUSE_IFNUM );
			m_repBuf.put( (byte)0 );
			m_repBuf.put( (byte)0 );
			m_repBuf.putInt( SeqNum );
			m_repBuf.put( (byte)0 );
			m_repBuf.put( (byte)0 );
			m_repBuf.put( (byte)0 );
			m_repBuf.put( (byte)0 );
			m_repBuf.put( (byte)( USB_PKT_MOUSE_REL_REP_SIZE & 0xff ) ); /* DataLen */
			/* Relative mouse data */
			/*
				typedef struct
				{
					BYTE	Event;
					BYTE	X;
					BYTE	Y;
				} USB_ABS_MOUSE_REPORT_T;
			*/
			if ( crypt != null ) {
				byte [] orig_data = new byte[USB_PKT_MOUSE_REL_REP_SIZE];
				ByteBuffer tmpbuf = ByteBuffer.wrap(orig_data);
				/* write the mouse buffer data to the temporary buffer */
				tmpbuf.put(m_btnStatus);
				tmpbuf.put( (byte) m_xDisp );
				tmpbuf.put( (byte) m_yDisp );
				tmpbuf.put(m_wheelrotation );
				/* Encrypt the data */
				byte[] encrypt_data = crypt.encrypt(orig_data);
	
				/* if any error send unencrypted data */
				if( encrypt_data == null )
					return RELreport(null);
	
				/* write encrypted data */
				m_repBuf.put(encrypt_data);
			} else {
				m_repBuf.put(m_btnStatus);
				m_repBuf.put( (byte) m_xDisp );
				m_repBuf.put( (byte) m_yDisp );
				m_repBuf.put(m_wheelrotation );
	
			}
	
	        /* Calculate modulo100 checksum */
	        int temp = 0;
	        for( int i = IVTPPktHdr.HDR_SIZE; i < (IUSB_HDR_SIZE+IVTPPktHdr.HDR_SIZE); i++ )
	            temp = ( temp + (int)( m_repBuf.get( i ) & 0xff ) ) & 0xff;
	
	        m_repBuf.put(IVTPPktHdr.HDR_SIZE+11, (byte)-( (byte)( temp & 0xff ) ) );
			SeqNum++;
			return m_report;
		}catch(Exception e){
			Debug.out.println(e);
			return null;
		}
	}

	/**
	 * Generate byte array of USB message.
	 *
	 * @return USB message array.
	 */
	public byte[] report() {

		if ( m_MouseMode == ABSOLUTE_MOUSE_MODE ) {
			return ( ABSreport(null) );
		} else {
			return ( RELreport(null) );
		}
	}

	/**
	 * Generate byte array of encrypted USB message.
	 *
	 * @param crypt crypt handler to be used.
	 * @return encrypted USB message array.
	 */
	public byte[] encryptedReport(KMCrypt crypt) {
		if ( m_MouseMode == ABSOLUTE_MOUSE_MODE ) {
			return ( ABSreport(crypt) );
		}
		else {
			return ( RELreport(crypt) );
		}
	}
}
