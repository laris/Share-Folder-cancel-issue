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
// Bandwidth auto detect packet
//

package com.ami.kvm.jviewer.kvmpkts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AutoDetectBw {

	public static final int AUTO_DETECT_BW_SIZE = 4;

	public IVTPPktHdr m_vHdr;
	public int m_load;
	ByteBuffer m_repBuf;
	private byte[] m_report;

	/**
	 * The constructor
	 *
	 * @param bandwidth bandwidth detection load
	 */
	public AutoDetectBw(int load) {

		m_load = load;
		m_report = new byte[IVTPPktHdr.HDR_SIZE + AUTO_DETECT_BW_SIZE];
		// ivtp packet header
		m_vHdr = new IVTPPktHdr(IVTPPktHdr.IVTP_BW_DETECT_REQ,
						AUTO_DETECT_BW_SIZE, (short)0);
		m_repBuf = ByteBuffer.wrap(m_report);
		m_repBuf.order(ByteOrder.LITTLE_ENDIAN);
		m_repBuf.limit(m_report.length);
	}

	/**
	 * Size of the message
	 *
	 * @return auto detect bandwidth message size
	 */
	public int size() {
		return (IVTPPktHdr.HDR_SIZE + AUTO_DETECT_BW_SIZE);
	}
	/**
	 * Generate byte array of Auto Detect Bandwidth packet
	 *
	 * @return byte array
	 */
	public byte[] report() {

		m_repBuf.position(0);
		// ivtp packet header
		m_repBuf.put(m_vHdr.array());
		// bandwidth
		m_repBuf.putInt(m_load);
		return m_report;
	}
}