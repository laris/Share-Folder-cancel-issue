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
package com.ami.kvm.jviewer.videorecord;

import java.net.HttpURLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import com.ami.kvm.jviewer.JViewer;
public class URLHandler {

	/** Session Cookie for maintaining a session */
	protected String sesCookie = null;

	//status of the connection HTTP or HTTPS
	int secureConnect;

	/** The connection object to connect to the requested URL. */
	protected HttpURLConnection conn;

	/** The http req. */
	protected String httpReq = "";

	/**
	 * Instantiates a URL handler. Sets the SSL protocol and adds a SSL
	 * Provider. Installs the all trusting trust manager.
	 */
	public URLHandler() {
		// initial asp calls will validate the server, so don't validate the host name for playerapp and downloadapp.
		if (JViewer.isplayerapp() || JViewer.isdownloadapp()) {
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		}
	}
}
