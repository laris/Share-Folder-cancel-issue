package com.ami.kvm.jviewer.kvmpkts;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.gui.JViewerApp;

public class KeepAlive extends Thread {
	//keep alive packet need to be send before socket read time out value
	private final int SLEEP_TIME = 2000;
	private long sentIdleTime,recvIdleTime;

	public void run() {
		while(true)
		{
			try {
				Thread.sleep(SLEEP_TIME);
				if(JViewerApp.getInstance().getSessionLive())
				{
					sentIdleTime = (JViewerApp.getInstance().getCurrentTime() 
								- JViewerApp.getInstance().getLastPckSent());
					recvIdleTime = (JViewerApp.getInstance().getCurrentTime() 
							- JViewerApp.getInstance().getLastPcktRecvd());

					if((sentIdleTime > SLEEP_TIME) || (recvIdleTime > SLEEP_TIME))
					{
						JViewerApp.getInstance().onSendKeepAliveRequest();
						Debug.out.println("\nSent KeepAlive Packet - " + sentIdleTime );
					}

				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
		}	
			
	}
	
}
