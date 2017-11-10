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
 // FragNumReader implements KVMReader interface. This reads the fragment number
 // part from network. This state is called after reading fragment header.
 // It also decides next state.
 //

 package com.ami.kvm.jviewer.kvmpkts;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.ami.kvm.jviewer.Debug;


/**
 * Fragment number reader class implementation.
 */
public class FragNumReader implements KVMReader {

	private KVMClient 	m_client;
	private ByteBuffer	m_fragNumBuf;
	private int 		m_fragNumIx;

	/**
	 * The constructor
	 *
	 * @param client - KVM client
	 */
	public FragNumReader(KVMClient client) {

		m_client = client;
		m_fragNumBuf = ByteBuffer.allocate(2);
		m_fragNumBuf.order(ByteOrder.LITTLE_ENDIAN);
		m_fragNumIx = 0;
	}

	/**
	 * initialize the reader
	 */
	public void initialize() {
		m_fragNumIx = 0;
		m_fragNumBuf.clear();
	}


 	/**
 	 * read fragment number
 	 *
 	 * @param sockCh socket channel
 	 * @return the status
 	 */
	public int read(Socket sockCh) {
		// try to read 2 bytes, which gives the fragmenet size.
		try {
			byte[] read_fragNumBuf = new byte[m_fragNumBuf.remaining()];
		    m_fragNumIx = m_client.read_data(sockCh,read_fragNumBuf);
		    m_fragNumBuf.put(read_fragNumBuf);

		    if (m_fragNumIx < 0) {
		       return -1;
        }

			if (2 <= m_fragNumIx) {
				m_client.m_fragNum = m_fragNumBuf.getShort(0);
				// set next state
				KVMReader reader = m_client.getFragReader();
				reader.initialize();
				m_client.setState(reader);
			}
		} catch (Exception e) {
			Debug.out.println("ON_READ_FRAG_SZ ");
			Debug.out.println(e);
			return -1;
		}
 		return 0;
 	}
}
