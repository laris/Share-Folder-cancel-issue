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

///////////////////////////////////////////////////////////////////////////////
//
// NullReader implements KVMReader interface. This reads the payload  of
// any kvm message from network. The data is ignored.
//

package com.ami.kvm.jviewer.kvmpkts;

import java.net.Socket;
import java.nio.ByteBuffer;

import com.ami.kvm.jviewer.Debug;

/**
 * Null reader class implementation.
 */
public class NullReader implements KVMReader {

	private KVMClient m_client;
	private ByteBuffer m_nullBuf;
	private int m_nullIx;
	private int m_readCnt;
	private int m_readSz;
	private int m_numRead;

	/**
	 * The constructor
	 *
	 * @param client -
	 *            KVM client
	 */
	public NullReader(KVMClient client) {

		m_client = client;
		m_nullBuf = ByteBuffer.allocate(1024 * 256);
	}

	/**
	 * initialize the reader
	 */
	public void initialize() {

		m_readSz = m_client.m_pktHdr.pktSize;
		m_numRead = m_client.m_pktHdr.status;
		m_nullIx = 0;
		m_readCnt = 0;
		m_nullBuf.position(0);
		m_nullBuf.limit(m_readSz);
		m_client.onStartReading();
	}

	/**
	 * read control message
	 *
	 * @param sockCh
	 *            socket channel
	 * @return the status
	 */
	public int read(Socket sockCh) {

		try {
			byte[] read_frameByteBuf = new byte[m_nullBuf.remaining()];
			m_nullIx = m_client.read_data(sockCh, read_frameByteBuf);
			m_nullBuf.put(read_frameByteBuf);

      if (m_nullIx < 0) {
         return -1;
      }
		} catch (Exception e) {
			Debug.out.println("CTRL_RDR ");
			Debug.out.println(e);
			return -1;
		}

		try {
			if (m_readSz <= m_nullIx) {
				m_readCnt++;
				
				if (m_numRead <= m_readCnt) {
					m_client.onStopReading();
					// set next state
					KVMReader reader = m_client.getHdrReader();
					reader.initialize();
					m_client.setState(reader);
				} else {
					m_nullIx = 0;
					m_nullBuf.position(0);
				}
			}
		} catch (Exception e) {
			Debug.out.println("CTRL_RDR[2]");
			Debug.out.println(e);
			return -2;
		}
		return 0;
	}
}
