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

package com.ami.kvm.jviewer.kvmpkts;

import java.net.Socket;
import java.nio.ByteBuffer;

import com.ami.kvm.jviewer.Debug;


/**
 * Header reader class implementation.
 */
public class HeaderReader implements KVMReader {

	private KVMClient 	m_client;
	private byte[] 		m_hdrBuf;
	private ByteBuffer 	m_hdrByteBuf;
	private IVTPPktHdr	m_pktHdr;
	private int			m_hdrIx;

	/**
	 * The constructor
	 *
	 * @param client - KVM client
	 */
	public HeaderReader(KVMClient client) {

		m_client = client;
		m_hdrBuf = new byte[IVTPPktHdr.HDR_SIZE];
		m_hdrByteBuf = ByteBuffer.wrap(m_hdrBuf);
		m_pktHdr = new IVTPPktHdr();
		m_hdrIx = 0;
	}

	/**
	 * initialize the reader
	 */
	public void initialize() {

		m_hdrIx = 0;
		m_hdrByteBuf.clear();
	}

	/**
	 * read header data
	 *
 	 * @param sockCh socket channel
 	 * @return the status
 	 */
 	public int read(Socket sockCh) {
		// read the header. we may get partial header.
		try {
			byte[] read_hdrByteBuf = new byte[m_hdrByteBuf.remaining()];
		    m_hdrIx = m_client.read_data(sockCh,read_hdrByteBuf);
		    m_hdrByteBuf.put(read_hdrByteBuf);

			if(m_hdrIx < 0)	{
				//Thread.sleep(1);
				return -1;
			}

			if (IVTPPktHdr.HDR_SIZE <= m_hdrIx) {
				m_pktHdr.set(m_hdrByteBuf);
				m_client.m_pktHdr = m_pktHdr;
				KVMReader reader = null;
				Boolean success =	OnGetReader(m_pktHdr.type);	// set next state

				if(!success) {
					if (m_pktHdr.pktSize > 0) {
						// received header is of a control message and it has body.
						Debug.out.println("Control\n");
						reader = m_client.getCtrlReader();
						reader.initialize();
						m_client.setState(reader);
					}
					else {
						Debug.out.println("Control No Body\n");
						// received header is of a control message with no body.
						m_client.onControlMessage();
						// set next status
						reader = m_client.getHdrReader();
						reader.initialize();
						m_client.setState(reader);
					}
				}
			}
		} catch (Exception e) {
			Debug.out.println("HDR_RDR ");
			Debug.out.println(e);
			return -1;
		}
		return 0;
	}

	private Boolean OnGetReader(short type) {
		KVMReader reader;
		if (type == IVTPPktHdr.IVTP_VIDEO_FRAGMENT) {
			// received header is of a video fragment.
			if(m_client.m_isBlank == true) {
				m_client.m_isBlank = false;
			}
			Debug.out.println("Video Fragment\n");
			reader = m_client.getFragNumReader();
			reader.initialize();
			m_client.setState(reader);
		} else if (type == IVTPPktHdr.IVTP_BW_DETECT_RESP) {
			// received header is of bandwidth detection packet.
			Debug.out.println("BW Detect\n");
			reader = m_client.getNullReader();
			reader.initialize();
			m_client.setState(reader);

		}else {
			Debug.out.println("GET SOC READER :: Header type : "+m_pktHdr.type);
			reader = m_client.getSocReader(m_pktHdr.type);
			if(reader == null)
				return false;
			reader.initialize();
			m_client.setState(reader);
		}
		return true;
	}
}
