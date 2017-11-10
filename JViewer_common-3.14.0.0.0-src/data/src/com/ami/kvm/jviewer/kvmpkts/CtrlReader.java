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
 // CtrlReader implements KVMReader interface. This reads the control message
 // part from network. This state is called after reading control
 // message header.
 //

 package com.ami.kvm.jviewer.kvmpkts;

import java.net.Socket;
import java.nio.ByteBuffer;

import com.ami.kvm.jviewer.Debug;


/**
 * Control message reader class implementation.
 */
public class CtrlReader implements KVMReader {

	private static final int CTRL_MSG_BODY_SIZE = 1024;
	private KVMClient 	m_client;
	private int 		m_ctrlIx;
	private ByteBuffer	m_ctrlBuf;

	/**
	 * The constructor
	 *
	 * @param client - KVM client
	 */
	public CtrlReader(KVMClient client) {

		m_client = client;
		m_ctrlIx = 0;
		m_ctrlBuf = ByteBuffer.allocate(CTRL_MSG_BODY_SIZE);
	}

	/**
	 * initialize the reader
	 */
	public void initialize() {

		if (m_client.m_pktHdr.pktSize > CTRL_MSG_BODY_SIZE) {
			Debug.out.println("Error Case... got a packet bigger than allocated buffer");
			Debug.out.dump(m_client.m_pktHdr.array(), IVTPPktHdr.HDR_SIZE);
			m_ctrlIx = 0;
			m_ctrlBuf.clear();
			m_ctrlBuf.limit(CTRL_MSG_BODY_SIZE);
			m_ctrlBuf.position(0);
		} else {
			m_ctrlIx = 0;
			m_ctrlBuf.clear();
			m_ctrlBuf.limit(m_client.m_pktHdr.pktSize);
			m_ctrlBuf.position(0);
		}
	}

 	/**
 	 * read control message
 	 *
 	 * @param sockCh socket channel
 	 * @return the status
 	 	 */
 	public int read(Socket sockCh)  {

 		if ((m_client.m_pktHdr.pktSize > CTRL_MSG_BODY_SIZE) || (m_client.m_pktHdr.pktSize < 0)) {
 			try {
 				byte[] read_ctrlbuf = new byte[m_ctrlBuf.remaining()];

 				m_ctrlIx = m_client.read_data(sockCh,read_ctrlbuf);

 			    //m_ctrlIx = read_ctrlbuf.length;
 			    m_ctrlBuf.put(read_ctrlbuf);
 			    m_ctrlBuf.limit(m_ctrlIx);
 			    m_ctrlBuf.position(0);

 			    if (m_ctrlIx < 0) {
 			       return -1;
          }

 			}
 			catch (Exception e) {
 				Debug.out.println("CTRL_RDR 1 ");
 				Debug.out.println(e);
 				return -1;
 			}
 			Debug.out.println("1.Invalid pktSize in m_pktHdr: " + m_client.m_pktHdr.pktSize);
			return -1;
		}
 		
		if (m_client.m_pktHdr.pktSize <= 0) {
 			Debug.out.println("3.Invalid pktSize in m_pktHdr: " + m_client.m_pktHdr.pktSize);
			return -1;
		}
		try {
			byte[] read_ctrlbuf = new byte[m_ctrlBuf.remaining()];
				m_ctrlIx = m_client.read_data(sockCh,read_ctrlbuf);
			    //m_ctrlIx = read_ctrlbuf.length;
			    m_ctrlBuf.put(read_ctrlbuf);
			    m_ctrlBuf.limit(m_ctrlIx);
			    m_ctrlBuf.position(0);

			if (m_client.m_pktHdr.pktSize <= m_ctrlIx) {
				m_client.m_ctrlMsg = m_ctrlBuf;
				m_client.onControlMessage();
				m_client.m_ctrlMsg.position(0);
				// set next status
				KVMReader reader = m_client.getHdrReader();
				reader.initialize();
				m_client.setState(reader);
			}
		} catch (Exception e) {
			Debug.out.println("CTRL_RDR 2");
			Debug.out.println(e);
			return -1;
		}
 		return 0;
 	}

}
