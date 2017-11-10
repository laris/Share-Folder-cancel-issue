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
package com.ami.kvm.jviewer.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;

public class Connection {
	public static int SUCCESS = 0;
	public static int INVALID_SOCKET = -1;
	public static int SOCK_CREAT_FAILED = -2;

	private boolean validateSSLCert = true;
	private boolean webSSLVerfiy = true;
	private boolean kvmSSLVerify = true;
	private int connErrCode = INVALID_SOCKET;
	public static final int SSL_HANDSHAKE_TIMEOUT = 5000 ; // SSL Socket handshake timeout ( in milliseconds )
	public static final int SOCKET_CONNECT_TIMEOUT = 900 ; // SSL & NonSSL socket connect timeout ( in milliseconds )
	private boolean connectHTTPPort = false;

	public SSLSocketFactory getSocketFactory() {
		SSLContext context = null;
		SSLSocketFactory factory = null;
		try {
			//Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}
			} };
			// Force Java 7 to use TLSv1.2 protocol
			// Note: TLSv1.2 protocol isn't supported by Java 6 and earlier versions.
			SSLContext sslCtx = SSLContext.getInstance("TLSv1.2");
			sslCtx.init(null, trustAllCerts, new java.security.SecureRandom());
			context = sslCtx;
			factory = context.getSocketFactory();
			// Don't set this socket factory to default ssl socket factory.
			// If it is set by HttpsURLConnection.setDefaultSSLSocketFactory()
			// Then any further socket being created won't throw proper exception incase of untrusted server.
		} catch (NoSuchAlgorithmException e) {
			Debug.out.println(e);
		} catch (KeyManagementException e) {
			Debug.out.println(e);
		}
		return factory;
	}

	private SSLSocket createSSLSocket(InetAddress ipaddress, int port, int service) {
		SSLSocket socket = null;
		boolean handShakeSuccess = true;
		SSLSocketFactory factory;
		int option = 0, sslTimeOut = 0 /* Default timeout value for SSL Socket handshake */;
		// If webSSLVerify is true, we need to validate the incoming web request
		// If KVMSSLVerify is true, we need to validate the incoming video/media request
		if(service == JViewerApp.WebService)
			validateSSLCert = (Debug.VERIFYSSLCERTS == Debug.VERIFY) ? webSSLVerfiy  : false;
		else
			validateSSLCert = (Debug.VERIFYSSLCERTS == Debug.VERIFY) ? kvmSSLVerify : false;
		if(validateSSLCert == true)
		{
			// Re-initialization of the HttpsURLConnection's SSLSocket factory :
			// When JViewer launched from https session,
			// proper exception was not thrown. So the socket factory of HttpsURLConnection
			// has been re-initialized using getDefault() of SSLSocketFactory class
			// to overcome this issue.

			SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
			HttpsURLConnection.setDefaultSSLSocketFactory(sf);
			factory = HttpsURLConnection.getDefaultSSLSocketFactory();
			try {
				socket = (SSLSocket) factory.createSocket(ipaddress, port);
			} catch (UnknownHostException e) {
				Debug.out.println(e);
				socket = null;
			} catch (IOException e) {
				Debug.out.println(e);
				socket = null;
			} catch(NullPointerException e) {
				Debug.out.println(e);
				socket = null;
			}
			if(socket != null) {
				try {
					/* Adding timeout for socket handshake to avoid startHandshake() method getting hang during execution.
					** After timer expires SocketTimeoutException will be thrown */
					sslTimeOut = socket.getSoTimeout();
					socket.setSoTimeout(SSL_HANDSHAKE_TIMEOUT);
					socket.startHandshake();
				} catch(SocketTimeoutException e){
					Debug.out.println(e);
					try {
						/* Since the handshake is timed out, no need for trying to establish the connection again. 
						 * so simply closing the socket */
						//close the ssl socket and try for http connection
						connectHTTPPort = true;
						socket.close();
					} catch (IOException e1) {
						Debug.out.println(e1);
					} finally {
						socket = null; // Setting socket value as null for notifying the user with connection failure message
					}
				} catch (SSLException e) {
					Debug.out.println(e);
					handShakeSuccess = false;
					validateSSLCert = false;
				} catch (IOException e) {
					Debug.out.println(e);
					handShakeSuccess = false;
					validateSSLCert = false;
				} finally {
					try {
						if(socket != null){
							/*
							 * To avoid socket read / write operations getting affected by
							 * socket handshake timeout, reverting the timeout value to
							 * default value.
							 */
							socket.setSoTimeout(sslTimeOut);
						}
					} catch (SocketException e) {
						Debug.out.println(e);
					}
				}
				if(!handShakeSuccess) {
					// handshake failed because server validation failed.
					// notify the user that the connection is untrusted. And prompt whether to continue or quit.
					// if the user wants to continue create a socket using trust manager.
					// else return null. The caller of this function will check for null and close the dialog appropriately
					option = JOptionPane.showConfirmDialog(JViewerApp.getInstance().getConnectionDialog(), LocaleStrings.getString("Z_9_URLP")
							+ LocaleStrings.getString("Z_10_URLP"), LocaleStrings.getString("Z_8_URLP"), JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
					// incase if user clicks no or closes the dialog, return null. If the return value is null, the window listener will close the dialog.
					if(option != JOptionPane.YES_OPTION)
					{
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					}
				}
			}
		}
		if(validateSSLCert == false) {
			if(service == JViewerApp.WebService)
				webSSLVerfiy = validateSSLCert;
			else
				kvmSSLVerify = validateSSLCert;
			// Create a trust manager that does not validate certificate
			factory = getSocketFactory();
			try {
				// In case of unreachable host following method of socket creation
				// takes approx. 20.5 secs to timeout.
				//socket = (SSLSocket) factory.createSocket(ipaddress,port);
				socket = (SSLSocket) factory.createSocket(); // So creating a generic socket
				// Using connect method with configurable timeout value
				socket.connect(new InetSocketAddress(ipaddress, port), SOCKET_CONNECT_TIMEOUT);
			} catch (UnknownHostException e) {
				Debug.out.println(e);
				socket = null;
			} catch (IOException e) {
				Debug.out.println(e);
				socket = null;
			}
		}
		
		if(socket == null) {
			connErrCode = SOCK_CREAT_FAILED;
			//Donot show the error message dialog, if the reconnect is in progress.
			if(JViewerApp.getInstance().getConnectionDialog() != null &&
					!JViewerApp.getInstance().getRetryConnection()) {
				
				if(connectHTTPPort == true){
					
					option = JOptionPane.showConfirmDialog(JViewerApp.getInstance().getConnectionDialog(), LocaleStrings.getString("Z_12_URLP")
							, LocaleStrings.getString("Z_11_URLP"), JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
					// incase if user clicks no or closes the dialog, return null. If the return value is null, the window listener will close the dialog.
					if(option != JOptionPane.YES_OPTION)
					{
						connectHTTPPort = false;
					}
				}
				else{
					JOptionPane.showMessageDialog(JViewerApp.getInstance().getConnectionDialog(), LocaleStrings.getString("S_11_SACD"), 
							LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				//Donot show the error message dialog, if the reconnect is in progress.
				if(!JViewerApp.getInstance().getRetryConnection()){
					JOptionPane.showMessageDialog(JViewer.getMainFrame(), LocaleStrings.getString("S_11_SACD"), 
							LocaleStrings.getString("S_9_SACD"), JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		return socket;
	}

	private Socket createNonSSLSocket(InetAddress ipaddress, int port) {
		Socket sock = null;
		try {
			// In case of unreachable host following method of socket creation
			// takes approx. 20.5 secs to timeout.
			//sock = new Socket(ipaddress, port);
			sock = new Socket(); // So creating a generic socket
			// Using connect method with configurable timeout value
			sock.connect(new InetSocketAddress(ipaddress, port), SOCKET_CONNECT_TIMEOUT);
		} catch (Exception e) {
			connErrCode = SOCK_CREAT_FAILED;
			Debug.out.println(e);
			sock = null;
		}
		return sock;
	}
	public Socket createSocket(InetAddress ipaddress, int port, int service) {
		Socket sock = null;
		// service variable being passed represents which service calls the createSocket() method
		// service value will be 0, if nonssl socket should be created.
		// Incase of SSLSocket for web requests, service value should be 1
		//Incase of SSLSocket for video/media request, service value should be 2
		connErrCode = SUCCESS;
		if(service > 0)
		{
			sock = createSSLSocket(ipaddress, port, service);
			if((connectHTTPPort == true) && (sock == null)){
				//SSL socket creation failed and "connectHTTPPort" flag is set so reset to non-secure
				JViewer.setWebSecure(0);
				JViewer.setUseSSL(false);
				JViewer.setSecureChannel(0);
				JViewer.setVMSecureChannel(0);
				JViewer.setVMUseSSL(false);
				JViewer.setWebSecure(0);
				sock = createNonSSLSocket(ipaddress, port);
			}
		}
		else if (service == 0)
			sock = createNonSSLSocket(ipaddress, port);
		return sock;
	}

	public boolean isWebSSLVerify() {
		return webSSLVerfiy;
	}

	public void setWebSSLVerify(boolean webSSLVerify) {
		this.webSSLVerfiy = webSSLVerify;
	}

	public boolean isKvmSSLVerify() {
		return kvmSSLVerify;
	}

	public void setKvmSSLVerify(boolean kvmSSLVerify) {
		this.kvmSSLVerify = kvmSSLVerify;
	}

	public int getConnErrCode() {
		return connErrCode;
	}

}
