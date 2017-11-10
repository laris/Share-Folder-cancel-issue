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
// This file defines video packet types.
//

package com.ami.kvm.jviewer.kvmpkts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * IVTP Packet header
 */
public class IVTPPktHdr {

	public static final int HDR_SIZE = 8;
	public static final int MAX_AVICA_PARAMS 		= 8;
	public static final int MAX_USERNAME_LEN 		= 32;
	public static final int STD_DIGEST_LEN 			= 16;
	public static final int MAX_CHALLENGE_LEN 		= 32;
	//
	// List of commands used between the Client and
	// Adaptive Video Server (ADVISER)
	//
	public static final short IVTP_HID_PKT							= 	0x0001;
	public static final short IVTP_SET_BANDWIDTH					=	0x0002;
	public static final short IVTP_SET_FPS							=	0x0003;
	public static final short IVTP_PAUSE_REDIRECTION				=	0x0004;
	public static final short IVTP_REFRESH_VIDEO_SCREEN				=	0x0005;
	public static final short IVTP_RESUME_REDIRECTION				=	0x0006;
	public static final short IVTP_SET_COMPRESSION_TYPE				=	0x0007;
	public static final short IVTP_STOP_SESSION_IMMEDIATE			=	0x0008;
	public static final short IVTP_BLANK_SCREEN						=	0x0009;
	public static final short IVTP_GET_USB_MOUSE_MODE				=	0x000a;
	public static final short IVTP_GET_FULL_SCREEN					=	0x000b;
	public static final short IVTP_ENABLE_ENCRYPTION				=	0x000c;
	public static final short IVTP_DISABLE_ENCRYPTION				=	0x000d;
	public static final short IVTP_ENCRYPTION_STATUS				=	0x000e;
	public static final short IVTP_INITIAL_ENCRYPTION_STATUS		=	0x000f;
	public static final short IVTP_BW_DETECT_REQ					=	0x0010;
	public static final short IVTP_BW_DETECT_RESP					=	0x0011;
	public static final short IVTP_VALIDATE_VIDEO_SESSION			=	0x0012;
	public static final short IVTP_VALIDATE_VIDEO_SESSION_RESPONSE	=	0x0013;
	public static final short IVTP_GET_KEYBD_LED					=	0x0014;
	public static final short IVTP_GET_WEB_TOKEN					=	0x0015;
	public static final short IVTP_MAX_SESSION_CLOSING				=	0x0016;
	public static final short IVTP_SESSION_ACCEPTED					=	0x0017;
	public static final short IVTP_MEDIA_STATE						=	0x0018;
	public static final short IVTP_VIDEO_FRAGMENT					=	0x0019;
	public static final short IVTP_WEB_PREVIEWER_SESSION			=   0x001A;
	public static final short IVTP_WEB_PREVIEWER_CAPTURE_STATUS		=   0x001B;
	public static final short IVTP_SET_MOUSE_MODE					= 	0x001C;
	public static final short IVTP_KVM_SHARING						=	0x0020;
	public static final short IVTP_KVM_SOCKET_STATUS				=	0x0021;
	
	public static final short IVTP_POWER_STATUS						=	0x0022;	
	public static final short IVTP_POWER_CONTROL_REQUEST			=	0x0023;
	public static final short IVTP_POWER_CONTROL_RESPONSE			=	0x0024;	
	
	public static final short IVTP_CONF_SERVICE_STATUS				=	0x0025;
	
	public static final short IVTP_MOUSE_MEDIA_INFO					=	0x0026;
	public static final short IVTP_GET_ACTIVE_CLIENTS				=	0x0027;

	public static final byte IVTP_POWER_CONTROL_OFF_IMMEDIATE 			=	0x00;
	public static final byte IVTP_POWER_CONTROL_ON						=	0x01;
	public static final byte IVTP_POWER_CONTROL_CYCLE					=	0x02;
	public static final byte IVTP_POWER_CONTROL_HARD_RESET				=	0x03;
	public static final byte IVTP_POWER_CONTROL_SOFT_RESET				=	0x05;
	
	public static final byte ADVISER_GET_USER_MACRO					= 0x28;
	public static final byte ADVISER_SET_USER_MACRO					= 0x29;
	
	public static final byte IVTP_IPMI_REQUEST_PKT					= 0x30;
	public static final byte IVTP_IPMI_RESPONSE_PKT					= 0x31;
	public static final byte IVTP_SET_NEXT_MASTER					= 0x0032;

	public static final short IVTP_DISPLAY_LOCK_SET					= 0x0033;
	public static final short IVTP_DISPLAY_CONTROL_STATUS			= 0x0034;
	public static final short IVTP_MEDIA_LICENSE_STATUS			= 0x0035;
	public static final short IVTP_KVM_DISCONNECT			= 0x0036;
	public static final short IVTP_SET_KBD_LANG						= 0x0037;
	public static final short IVTP_MEDIA_FREE_INSTANCE_STATUS			= 0x0038;
	public static final short IVTP_KEEP_ALIVE_PKT				= 0X0039;
	public static final short IVTP_CONNECTION_COMPLETE_PKT			= 0X003A;
	public static final short IVTP_CONNECTION_FAILED			= 0X003B;

	public static final short ENCRYPTION_ENABLED					= 255;
	public static final byte SESSION_TOKEN_LEN						= 16;
	public static final byte WEB_HASH_SIZE							= 16;
	public static final short SSI_HASH_SIZE							= 128 + 2;// +2 added for padding
	public static final short CLIENT_USERNAME_LENGTH				= 128 + 1; //Restricted to 128 as of now +1 added for padding
	public static final short CLINET_OWN_IP_LENGTH					= 64 + 1;// +1 added for padding
	public static final short CLINET_OWN_MAC_LENGTH					= 48 + 1;// +1 added for padding
	public static final short VIDEO_PACKET_SIZE						= SSI_HASH_SIZE + CLINET_OWN_IP_LENGTH + CLIENT_USERNAME_LENGTH + CLINET_OWN_MAC_LENGTH;
	public static final byte WEB_SESSION_TOKEN						= 0;
	public static final byte SSI_SESSION_TOKEN						= 1;

	// member variables provide public access
	public short 	type;
	public int 		pktSize;
	public short 	status;
	private ByteBuffer m_buf;

	/**
	 * Constructor
	 */
	public IVTPPktHdr() {

		this((byte)0, 0, (short)0);
	}

	/**
	 * Constructor
	 *
	 * @param type packet type
	 * @param pktSize packet size
	 * @param status packet status
	 */
	public IVTPPktHdr(short t, int pS, short s) {

		type = t;
		pktSize = pS;
		status = s;
		byte[] buf = new byte[HDR_SIZE];
		m_buf = ByteBuffer.wrap(buf);
		m_buf.order(ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * Set the header contents
	 *
	 * @param buf header values buffer
	 */
	public void set(ByteBuffer buf) {

		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.flip();
		type = buf.getShort();
		pktSize = buf.getInt();
		status = buf.getShort();
	}

	/**
	 * Set packet size
	 *
	 * @param size new packet size
	 */
	public void setSize(int pS) {

		pktSize = pS;
	}

	/**
	 * Set packet status
	 *
	 * @param status
	 */
	public void setStatus(short s) {

		status = s;
	}

	/**
	 * Get type
	 *
	 * @return type packet type.
	 */
	public short type() {
		return type;
	}

	/**
	 * Get packet size
	 *
	 * @return packet size
	 */
	public int pktSize() {
		return pktSize;
	}

	/**
	 * Get status
	 *
	 * @return status
	 */
	public short status() {
		return status;
	}

	/**
	 * Return the size of the header
	 *
	 * @return the size of packet the header.
	 */
	public int size() {

		return HDR_SIZE;	// 7 bytes length
	}

	/**
	 * Get the header as an array
	 *
	 * @return byte array
	 */
	public byte[] array() {

		m_buf.position(0);
		m_buf.putShort(type);
		m_buf.putInt(pktSize);
		m_buf.putShort(status);
		return m_buf.array();
	}
}
