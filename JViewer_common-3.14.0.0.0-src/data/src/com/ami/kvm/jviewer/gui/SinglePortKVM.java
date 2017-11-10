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
// StandAloneApp connection module
//

package com.ami.kvm.jviewer.gui;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.swing.JOptionPane;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.communication.Connection;
import com.ami.kvm.jviewer.videorecord.URLProcessor;


/**
 * The StandAloneConnectionDialog creates and shows the StandAloneApp connection dialog through which
 * user can provide required information to establish a connection with the Host. 
 */
public class SinglePortKVM {	


	private String webSessionToken = null;
	private int secWebPort ;	
	private String tunnelHost = null;
	private int tunnelPort ;
	private boolean m_bUseSSL = false;
	private URLProcessor urlProcessor;

	public OutputStream outStream ;
	public InputStream inStream ;
	public Socket httpsock ;

	private String Service = null;
	public static final String VIDEO = "VIDEO";
	public static final String HTTP = "http";
	public static final String HTTPS = "https";
	public SSLSocket sslsock = null;
	public SSLContext context = null;

	public Socket getHttpsock() {
		return httpsock;
	}

	public void setHttpsock(Socket httpsock) {
		this.httpsock = httpsock;
	}

	public byte reply[] = new byte[200];


	public OutputStream getOutstream() {
		return outStream;
	}

	public void setOutstream(OutputStream outStream) {
		this.outStream = outStream;
	}

	public InputStream getInstream() {
		return inStream;
	}

	public void setInstream(InputStream inStream) {
		this.inStream = inStream;
	}

	public SSLSocket getSSLsock() {
		return sslsock;
	}
	public void setSSLsock(SSLSocket sslsock) {
		this.sslsock = sslsock;
	}
	/**
	 * The COnstructor.
	 * @param parent - The parent frame on which the dialog will be shown.
	 * @param hostIP - The IP address of the host.
	 * @param username - user name to log into the BMC.
	 * @param password - password to log into the BMC.
	 */
	public SinglePortKVM(String tunnelHost, int tunnelPort, int secWebPort,boolean m_bUseSSL) {				

		this.tunnelHost = tunnelHost;
		this.tunnelPort	=	tunnelPort;
		this.secWebPort = secWebPort;	
		this.m_bUseSSL = m_bUseSSL;
	//	this.m_bVMUseSSL = m_bVMUseSSL;
	}	

	public int  startConnect(){	

		if ( 0 > setHTTPConnect(VIDEO)){
			Debug.out.println(LocaleStrings.getString("AE_10_SPKVM"));
			return -1;
		}
		return 0;
	}

	public int getWebSessionToken() {
		JViewerApp.getInstance().setM_webSession_token(null);
		urlProcessor = new URLProcessor(null, 1);
		int ret = -1;
		String csrfToken = null;
		if(JViewer.isRestService() == true) {
			ret = urlProcessor.restProcessRequest(JViewer.getProtocol()+"://"+tunnelHost+":"+secWebPort+
					"/api/session?username="+JViewer.getUsername()+"&password="+JViewer.getPassword());
			try{
				if(ret == 0){
					webSessionToken = urlProcessor.getValue("Set-Cookie: ",';');
					JViewerApp.getInstance().setM_webSession_token(webSessionToken);
					csrfToken = urlProcessor.getValue("\"CSRFToken\": ", ' ');
					csrfToken.trim();
					URLProcessor.setCsrfToken(csrfToken.substring(1, csrfToken.length()-1));
				}
				else{
					Debug.out.println("getWebSessionToken failed with return value:"+ret);
				}
			}catch(Exception e){
				Debug.out.println(e);
			}
		}else {
			ret = urlProcessor.processRequest(JViewer.getProtocol()+"://"+tunnelHost+":"+secWebPort+
					"/rpc/WEBSES/create.asp?WEBVAR_USERNAME="+JViewer.getUsername()+"&WEBVAR_PASSWORD="+JViewer.getPassword());
			try{
				if(ret == 0){
					webSessionToken = urlProcessor.getValue("'SESSION_COOKIE' : '", ',');
					webSessionToken.trim();				
					webSessionToken = webSessionToken.substring(0, webSessionToken.lastIndexOf('\''));
					JViewerApp.getInstance().setM_webSession_token(webSessionToken);
				}
				else{
					Debug.out.println("getWebSessionToken failed with return value:"+ret);
				}
			}catch(Exception e){
				Debug.out.println(e);
			}
		}
		return ret;
	}

	/**
	 * Sets the web session token.
	 */
	public int setHTTPConnect(String service) {

		int ret =0;

		Socket sock =null;
		Service = service;
		setHttpsock(null);
		setOutstream(null);
		setInstream(null);

		if(JViewerApp.getInstance().getM_webSession_token() == null)
		{
			ret = getWebSessionToken();
			if( ret < 0)
				return ret;
			else if(ret == 0)
				JViewerApp.getInstance().getConnectionDialog().setWebLogIn(true);
		}

		try {
			// serviceValue variable being passed represents which service calls the createSocket() method
			// serviceValue will be 0, if nonssl socket should be created.
			// Incase of SSLSocket for web requests, serviceValue should be 1
			//Incase of SSLSocket for video/media request, serviceValue should be 2

			int serviceValue = (m_bUseSSL == true) ? (JViewerApp.KVMService): JViewerApp.NonSSLService;
			sock = JViewerApp.getInstance().getConnection().createSocket(InetAddress.getByName(tunnelHost), tunnelPort, serviceValue);
			if (sock == null) {
				if((m_bUseSSL) && (JViewerApp.getInstance().getConnection().getConnErrCode() == Connection.SUCCESS))
					JViewer.exit(0);
				
				return -1;
			}
			setHttpsock(sock);
			setOutstream(sock.getOutputStream());
			setInstream(sock.getInputStream());
			ret = doTunnelHandshake(tunnelHost, secWebPort);
			if(ret < 0 ){
				Debug.out.println(LocaleStrings.getString("AE_10_SPKVM"));
			}

		} catch (UnknownHostException e) {

			Debug.out.println(e);
			ret = -1;
		} catch (IOException e) {

			Debug.out.println(e);
			ret =-1;
		}		
		catch(Exception e)
		{
			Debug.out.println(e);
			ret =-1;
		}
		//call logout only for standalone application and VMApp
		if(JViewer.isStandAloneApp() || JViewer.isVMApp())
			JViewerApp.getInstance().getConnectionDialog().logoutWebSession();
		return ret;
	}

	/**
	 * Disconnect connection
	 */
	public void DisconnectService(Socket sock) {

		String HttpReq = null;
		int data = 0;

		HttpReq = "JVIEWER DISCONNECT Cookie "
			+ JViewerApp.getInstance().getM_webSession_token()
			+ "\r\n\r\n";		

		if(WriteToConnectedSock(HttpReq) == 0){
			HttpReq = null;
		}
		else{
			Debug.out.println(LocaleStrings.getString("AE_11_SPKVM"));
		}

		try {
			data = ReadFromConnectedSock( );
		} catch (IOException e) {
			Debug.out.println(e);
		}
		if(data < 0 )
			return ;

		return ;

	}

	/**
	 * Makes tunnel through WebServert. 
	 */
	private int doTunnelHandshake( String host, int port) throws IOException
	{
		int data = 0;
		String HttpReq = null;

		HttpReq = FormHttpRequest(" ",host,port);	

		if(WriteToConnectedSock(HttpReq) == 0){
			HttpReq = null;
		}
		else{
			Debug.out.println(LocaleStrings.getString("AE_10_SPKVM"));
			return -1;
		}

		HttpReq = "JVIEWER "+Service+ " cookie "
		+ JViewerApp.getInstance().getM_webSession_token()
		+ "\r\n\r\n";


		if(WriteToConnectedSock(HttpReq) == 0){
			HttpReq = null;
		}
		else{
			Debug.out.println(LocaleStrings.getString("AE_10_SPKVM"));
			return -1;
		}

		data = ReadFromConnectedSock( );
		if(data < 0 )
			return -1;

		/* tunneling Handshake was successful! */
		return 0;
	}



	/**
	 * Forms Http Connect Request. 
	 */
	private String FormHttpRequest(String type,String host, int port) {

		String HttpReq = null; 
		String Protocol= null;
		
		if(true == m_bUseSSL)
			Protocol = " HTTPS/1.1\r\n";
		else
			Protocol = " HTTP/1.1\r\n";
		
		HttpReq = "CONNECT"+ type + host + ":" + port + Protocol
		//+ "Proxy-Authorization:Basic "
		//+ "authorization:basic "
		//+ username
		//+ ":"
		//+ password	
		+" cookie "
		+ JViewerApp.getInstance().getM_webSession_token()
		//+ " "
		+ "\r\n\r\n";
		//+ "\n";

		return HttpReq;
	}

	/*Parse error code from web respone
	 * NOTe :- ERROR String should be in the following format "ERROR:12\\r\\n"
	 */
	public String GetErrorCode(String ErrMsg) throws IOException{
		String Err = null;
		int loc = -1;

		if(null == ErrMsg)
			return Err;

		StringBuffer ErrStr   = new StringBuffer ((String) ErrMsg);

		for ( loc = 0; loc < ErrStr.length(); loc++){
			if (ErrStr.charAt (loc) == '\\')
				break;
		}

		try{
			if(loc > 0)
			{
				Err = "AE_"+ErrMsg.substring((ErrMsg.indexOf(":")+1),loc)+"_SPKVM";
			}
		}catch(Exception e){
			Debug.out.println(e);
		}

		return Err;
	}

	/**
	 * read data To Connected Sock.
	 * @throws IOException 
	 */
	public int ReadFromConnectedSock() throws IOException{

		/*
		 * We need to store the reply so we can create a detailed
		 * error message to the user.
		 */

		int            replyLen = 0;
		int            newlinesSeen = 0;
		boolean        headerDone = false;     /* Done on first newline */
		String ErrMsg = null;

		while (newlinesSeen < 1) {

			int i = getInstream().read();		
			if (i < 0) {
				return i;
				//throw new IOException("Unexpected EOF from proxy");
			}		
			if(0 == getInstream().available()){
				headerDone = true;
				++newlinesSeen;
			} else if (i != '\r') {
				newlinesSeen = 0;
				if (!headerDone && replyLen < reply.length) {
					reply[replyLen++] = (byte) i;
				}
			}
		}	
		/*
		 * Converting the byte array to a string is slightly wasteful
		 * in the case where the connection was successful, but it's
		 * insignificant compared to the network overhead.
		 */
		String replyStr = null;
		try {
			replyStr = new String(reply, 0, replyLen, "ASCII7");
		} catch (UnsupportedEncodingException ignored) {
			Debug.out.println(ignored);
			replyStr = new String(reply, 0, replyLen);
		}

		/* We check for Connection Established because our proxy returns 
		 * HTTP/1.1 instead of 1.0 */
		if(replyStr.contains("ERROR") == true){ 
			try{
				ErrMsg = LocaleStrings.getString(GetErrorCode(replyStr.trim()));
			}catch(Exception e){
				Debug.out.println(e);
				ErrMsg = LocaleStrings.getString("AE_4_SPKVM");
			}
			if(!Service.contains(VIDEO))
			{
				JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(), 
						ErrMsg,LocaleStrings.getString("D_3_JVAPP"),JOptionPane.INFORMATION_MESSAGE);
				return -1;
			}

			JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(), 
					ErrMsg,LocaleStrings.getString("AE_10_SPKVM"),JOptionPane.INFORMATION_MESSAGE);
			JViewer.exit(0);
		}	
		Debug.out.dump(replyStr.getBytes());
		return replyLen;
	}

	/**
	 * Write data To Connected Sock.
	 */
	public int WriteToConnectedSock(String writedata){

		byte b[];

		try {
			/*
			 * We really do want ASCII7 -- the http protocol doesn't change
			 * with locale.
			 */
			b = writedata.getBytes("ASCII7");
		} catch (UnsupportedEncodingException ignored) {
			/*
			 * If ASCII7 isn't there, something serious is wrong, but
			 * Paranoia Is Good (tm)
			 */
			Debug.out.println(ignored);
			b = writedata.getBytes();
		}		

		try {
			getOutstream().write(b);
			getOutstream().flush();

		} catch (IOException e) {
			Debug.out.println(e);
			return -1;
		}
		return 0;
	}
}

