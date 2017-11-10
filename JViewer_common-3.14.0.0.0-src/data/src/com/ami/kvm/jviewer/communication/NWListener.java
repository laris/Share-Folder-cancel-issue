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
// Listener module
// - listens for incoming data on registered channels.
//	 channels can be video, cd, ide, etc.
//

package com.ami.kvm.jviewer.communication;

import com.ami.kvm.jviewer.Debug;


/**
 * This class is a thread that waits for network read events.
 * On an event it calls appropriate handler implemented in
 * Video client or any other client.
 */
public class NWListener implements Runnable {

	private boolean m_run = false;
	private Client m_clnt;

	/**
	 * Constructor.
	 * create <socket channel, client> map.
	 * create selector.
	 * set thread start condition.
	 */
	public NWListener(Client clnt)
	{
		m_clnt = clnt;
		m_run = true;
	}

	/**
	 * Start listener
	 */
	public void startListener()
	{
		m_run = true;
	}

	/**
	 * Stop thread.
	 */
	public void destroyListener()
	{
		// set stop condition for thread
		m_run = false;
	}

	/**
	 * Thread routine.
	 * wait for incoming data for all registered clients.
	 * inform the client on read ready.
	 */
	public void run() {
		// thread function
		while (m_run)
		{
			try{
				m_clnt.onReadEvent();
			}catch (Exception e) {
				Debug.out.println(e);
			}
		} // end thread loop
	}
}
