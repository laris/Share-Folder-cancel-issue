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
// FragReader implements KVMReader interface. This reads the fragment
// part from network. This state is called after reading fragment size.
// It also decides next state.
//

package com.ami.kvm.jviewer.kvmpkts;

import java.net.Socket;
import java.nio.ByteBuffer;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.jvvideo.JVVideo;


/**
 * Fragment reader class implementation.
 */
public class FragReader implements KVMReader {

	private KVMClient 	m_client;
	private byte[]		m_frameBuf;
	private ByteBuffer	m_frameByteBuf;
	private int			m_fragIx;
	private int			m_frameIx;

	/**
	 * The constructor
	 *
	 * @param client - KVM client
	 */
	public FragReader(KVMClient client) {

		m_client = client;
		//Use the maximum X and Y resolution values defined in JVVideo class.
		m_frameBuf = new byte[4 * JVVideo.MAX_X_RESOLUTION * JVVideo.MAX_Y_RESOLUTION];
		m_frameIx = 0;
		m_frameByteBuf = ByteBuffer.wrap(m_frameBuf);
	}

	/**
	 * initialize the reader
	 */
	public void initialize() {

		// if this is the first fragment, initialize the frame index.
		if ((m_client.m_fragNum & 0x7fff) == 0) {
			m_frameIx = 0;
		}

		// prepare the frame buffer to read the video data.
		m_frameByteBuf.position(m_frameIx);
		m_frameByteBuf.limit(m_frameIx + m_client.m_pktHdr.pktSize - 2);
		m_fragIx = 0;
	}

 	/**
 	 * read fragment size
 	 *
 	 * @param sockCh socket channel
 	 * @return the status
 	 */
	public int read(Socket sockCh){

		try {
			byte[] read_frameByteBuf = new byte[m_frameByteBuf.remaining()];
			m_fragIx = m_client.read_data(sockCh,read_frameByteBuf);
			m_frameByteBuf.put(read_frameByteBuf);

      if (m_fragIx < 0) {
         return -1;
      }

		} catch (Exception e) {
			Debug.out.println("FRAG_RDR ");
			Debug.out.println(e);
			return -1;
		}

		// check if fragment received fully
		if (m_fragIx == (m_client.m_pktHdr.pktSize - 2)) {

			m_frameIx += m_fragIx;
			// check if this is the last fragment
			if (0 != (m_client.m_fragNum & 0x8000)) {
				m_frameByteBuf.limit(m_frameIx);
				// as this is the last fragment, call the frame handler.
				m_client.onNewFrame(m_frameByteBuf);
				m_frameIx = 0;
			}
			// set next state
			KVMReader reader = m_client.getHdrReader();
			reader.initialize();
			m_client.setState(reader);
		}
 		return 0;
 	}
}


