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

package com.ami.kvm.jviewer.gui;

import javax.swing.ImageIcon;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.videorecord.URLHandler;
import com.ami.kvm.jviewer.videorecord.URLProcessor;


public class OEMResourceURLProcessor extends URLHandler{

	private final String oemPath = "/res/oem";
	private final String restOemPath = "/api/logs/help-data";
	private final String copyrightFileName = "copyright_";
	private final String copyrightFileExtn = ".txt";
	private final String logoFileName = "jviewerlogo.jpg";

	private String serverIp;
	private String protocol;

	/**
	 * 	Set the session cookie for the HTTP request.
	 * 	@param sessionCookie
	 **/
	public OEMResourceURLProcessor(String sessionCookie, String serverIp){
		sesCookie = sessionCookie;
		this.serverIp = serverIp;
		if(JViewer.isWebSecure())
			protocol = "https://";
		else
			protocol = "http://";
	}

	public String getOemCopyright(){
		String copyright = null;
		String url = null;

		if(JViewer.isRestService())
			url = protocol + this.serverIp + (this.oemPath.startsWith("/")? "": "/")
			+ this.restOemPath + "?file="+copyrightFileName +JViewer.getLanguage()+copyrightFileExtn;
		else
			url = protocol + this.serverIp + (this.oemPath.startsWith("/")? "": "/")
				+ this.oemPath + (this.oemPath.endsWith("/")? "": "/")
								+ copyrightFileName +JViewer.getLanguage()+copyrightFileExtn;

		byte[] bytes = getOemResourceBytes(url);
		if (bytes != null){
			copyright = new String(bytes);
			Debug.out.println(url + " is available url");
		}
		return copyright;
	}

	public ImageIcon getOemLogo(){
		ImageIcon logo = null;
		String url = null;

		if(JViewer.isRestService())
			url = protocol + this.serverIp + (this.oemPath.startsWith("/")? "": "/")
			+ this.restOemPath + "?file="+logoFileName;
		else
			url = protocol + this.serverIp + (this.oemPath.startsWith("/")? "": "/")
					+ this.oemPath + (this.oemPath.endsWith("/")? "": "/")
					+ logoFileName;
		byte[] bytes = getOemResourceBytes(url);
		if (bytes != null){
			logo = new ImageIcon(bytes);
			Debug.out.println(url + " is available url");
		}
		return logo;
	}

	public byte[] getOemResourceBytes(String urlStr){

		byte[] resBytes = null;
		URLProcessor processor = null;
		String webToken = JViewerApp.getInstance().getM_webSession_token();
		// incase of rest service, websession token is needed for getting help content
		// incase of libmodhapi, websession token isn't needed.
		if(JViewer.isRestService() == true && webToken == null)
		{
			JViewerApp.getInstance().getConnectionDialog().restGetCSRFToken();
			webToken = JViewerApp.getInstance().getM_webSession_token();
		}
		processor = new URLProcessor(webToken, JViewer.getWebSecure());
		// call process requests, which will create the needed socket and writes the requests to socket
		int ret = processor.processRequest(urlStr);

		if(ret == 0)
		{
			// if ret = 0 then processrequest is success and has dumped the needed content in data buffer
			resBytes = processor.getData();
		}
		return resBytes;
	}
}
