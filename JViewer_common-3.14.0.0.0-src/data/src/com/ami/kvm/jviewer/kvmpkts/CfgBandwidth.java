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
// Bandwidth configuration packet
//

package com.ami.kvm.jviewer.kvmpkts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.ami.kvm.jviewer.Debug;

public class CfgBandwidth {

	public static final int BANDWIDTH_100MBPS 	= (12800 * 1024); // LAN
	public static final int BANDWIDTH_10MBPS	= (1280 * 1024);  // LAN - cable modem
	public static final int BANDWIDTH_1MBPS		= (128 * 1024);   // LAN
	public static final int BANDWIDTH_512KBPS   = (64 * 1024);	  // ISDN
	public static final int BANDWIDTH_256KBPS   = (32 * 1024);    // modem
	public static final int CFG_BANDWIDTH_SIZE = 4;

	public IVTPPktHdr m_vHdr;
	public int m_bandwidth;
	ByteBuffer m_repBuf;
	private byte[] m_report;

	/**
	 * The constructor
	 *
	 * @param bandwidth bandwidth to be configured
	 */
	public CfgBandwidth(int bandwidth) {

		m_bandwidth = bandwidth;
		m_report = new byte[IVTPPktHdr.HDR_SIZE + CFG_BANDWIDTH_SIZE];
		// ivtp packet header
		m_vHdr = new IVTPPktHdr(IVTPPktHdr.IVTP_SET_BANDWIDTH,
						CFG_BANDWIDTH_SIZE, (short)0);
		m_repBuf = ByteBuffer.wrap(m_report);
		m_repBuf.order(ByteOrder.LITTLE_ENDIAN);
		m_repBuf.limit(m_report.length);
	}

	/**
	 * Size of the message
	 *
	 * @return config bandwidth message size
	 */
	public int size() {
		return (IVTPPktHdr.HDR_SIZE + CFG_BANDWIDTH_SIZE);
	}
	/**
	 * Generate byte array of Bandwidth Configuration packet
	 *
	 * @return byte array
	 */
	public byte[] report() {

		m_repBuf.position(0);
		// ivtp packet header
		m_repBuf.put(m_vHdr.array());
		// bandwidth
		m_repBuf.putInt(m_bandwidth);
		return m_report;
	}

	/**
	 * Determine bandwidth based on time taken in
	 * reading 100 mega-bits.
	 *
	 * @param tims time in mille seconds.
	 */
	public static int determineBandwidth(long tims) {

		// convert time to seconds
		double tis = (double)tims / 1000.0;
		// determine bits per second.
		double bps = (double)BANDWIDTH_100MBPS / tis;
		Debug.out.println("bps " + bps);

		if (bps < (((double)BANDWIDTH_256KBPS + (double)BANDWIDTH_512KBPS) / 2.0)) {
			return BANDWIDTH_256KBPS;
		}
		else if (bps < (((double)BANDWIDTH_512KBPS + (double)BANDWIDTH_1MBPS) / 2.0)) {
			return BANDWIDTH_512KBPS;
		}
		else if (bps < (((double)BANDWIDTH_1MBPS + (double)BANDWIDTH_10MBPS) / 2.0)) {
			return BANDWIDTH_1MBPS;
		}
		else if (bps < (((double)BANDWIDTH_10MBPS + (double)BANDWIDTH_100MBPS) / 2.0)) {
			return BANDWIDTH_10MBPS;
		}
		else {
			return BANDWIDTH_100MBPS;
		}

	}
}