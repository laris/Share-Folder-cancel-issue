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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.LocaleStrings;

public class URLProcessor extends URLHandler{

	private byte[] data;	
	
	private static final int FILE_LOCKED = -1;
	public static final int FILE_NOT_FOUND = -2;
	public static final int EMPTY_FILE = -3;
	public static final int RECORDING_IN_PROGRESS = -5; // To check file is being processed by recording process
	public static final int INVALID_SESSION_TOKEN = 2;
	public static final int HTTP_CONNECTION_SUCCESS = 0;
	private static final int HTTP_REQUEST_SUCCESS = 200;
	public static final int HTTP_REQUEST_FAILURE = -1;
	public static final int INVALID_CREDENTIALS = -6;
	public static final int INVALID_SERVER_CERT = -7;
	private static final int MAX_BUFFER_SIZE = 1048576; // 1MB
	private static final String DEF_DELIMITER = " ";
	private static final int HTTP_FILE_NOT_FOUND = 404;
	// Video File Access Input parameters used in ASP calls
	public static final int RESET_FILE_ACCESS = 0; // For unlocking video file
	public static final int SET_FILE_ACCESS = 1; // For locking video file
	public static final int CHECK_FILE_ACCESS = 4; // For checking whether video file is being processed
	/*
	** Rest Service will throw following status code if
	** requested video file is being processed by video
	** recording process.
	**/
	private static final int REST_RECORDING_IN_PROGRESS = 1442;
	
	private static final String UNAUTHORIZED = "401 Unauthorized";
	private static final String NOT_FOUND = "404 Not Found";
	private static final String DYNAMIC_DATA_END = "//Dynamic data end";
	private static final String ZERO = "0";

	private static final int SUCCESS = 0;
	private static final int FAILURE = -1;

	private String hostIP = null;
	private String uriString = null;
	private static Socket socket = null;
	// user agent details. DON'T CHANGE THESE VALUES
	private final String USER_AGENT_NAME = "JViewer";
	private final String USER_AGENT_TAG = "User-Agent";
	// windows systems uses \r\n whereas linux systems uses \n to represent new line.
	// the server compares the new lines against \r\n so use this variable to send \r\n explicitly.
	private final String NEWLINE = "\r\n";
	private static String csrfToken = null;
	private int downloadStatus = FAILURE;
	
	private final int KB = 1024; // 1024 bytes = 1 Kilo Byte
	private final int MB = 1024 * KB; // 1024 * 1Kilo Bytes = 1 Mega Byte
	private final int GB = 1024 * MB; // 1024 * 1Mega Bytes = 1 Giga Byte
	private final int ONE_SEC = 1000; // 1000 Milliseconds = 1 Second

	/**
	 * Set the session cookie for HTTP request
	 * @param Sessioncookie
	 * @param secureconnect
	 */

	public URLProcessor(String Sessioncookie, int secureConnect) {
		sesCookie = Sessioncookie;
		this.secureConnect = secureConnect;
	}

	public URLProcessor() {
	}

	/**
	 * Creates a socket if socket is not created already.
	 * Writes the request to the socket and parses the server response data.
	 * If the request is not an asp request, then it dumps all the content from the http response into data buffer
	 * @param urlPath url path of the current request
	 * @return 0 if success.
	 *	HTTP_REQUEST_FAILURE if there is any exception while writing or reading from the socket.
	 * 	FILE_LOCKED File found is already locked
	 * 	FILE_NOT_FOUND If the requested file is not present
	 */
	public int processRequest(String urlPath){

		int hapistatus = 0;
		int ret = -1;
		//when rest runs use rest functions
		if(JViewer.isRestService()){
			return restProcessRequest(urlPath);
		}

		// if this function is executed for the first time, then create a socket and use it for all asp requests.
		if(socket == null) {
			try {
				// service variable being passed represents which service calls the createSocket() method
				// service value will be 0, if nonssl socket should be created.
				// Incase of SSLSocket for web requests, service value should be 1
				//Incase of SSLSocket for video/media request, service value should be 2 
				int service = (JViewer.isWebSecure() == true) ? (JViewerApp.WebService): JViewerApp.NonSSLService;
				socket = JViewerApp.getInstance().getConnection().createSocket(InetAddress.getByName(JViewer.getIp()), JViewer.getWebPort(), service);
			} catch (UnknownHostException e) {
				Debug.out.println(e);
			}
			// return invalid server certificate, standalone connection dialog will close.
			if(socket == null) {
				return INVALID_SERVER_CERT;
			}
		}

		uriString = urlPath;
		// If there is any exception while writing to the socket, return request failure
		if(writeRequestToSocket(socket, null) != 0) {
			return HTTP_REQUEST_FAILURE;
		}

		// for asp calls just get the server response data and return value based on hapi status
		if(urlPath.contains(".asp"))
		{
			// If there is any exception while reading from socket, return request failure
			ret = getServerResponseData();
			if(ret != 0)
			{
				try {
					//close the socket if requested data not found
					if(socket != null)
					{
						socket.close();
						setSocket(null);
					}
				}
				catch(Exception e)
				{
					setSocket(null);
					Debug.out.println(e);
				}
				return ret;
			}
		}
		else
		{
			// for nonasp calls (eg: on clickin help menu)
			// call getDataFromSocket(), which parses the http header accordingly and dumps the data into databuffer
			return getDataFromSocket();
		}

		hapistatus = Integer.parseInt(getValueOf(new String(data), "HAPI_STATUS:", ' ').trim());

		/**retno from libvideocfg
		 * Setting the status
		 * -1 = File Found already locked
		 * -2 - File not found
		 * Reseting the satus
		 */
		if( hapistatus < SUCCESS )
		{
			if(hapistatus == INVALID_CREDENTIALS) // invalid user name or password in StandAloneApp.
			{ 
				return INVALID_CREDENTIALS;
			}
			if(JViewer.isdownloadapp() || JViewer.isplayerapp()){
					if(hapistatus == RECORDING_IN_PROGRESS){
						 return RECORDING_IN_PROGRESS;
					}
					JViewerApp.getInstance().getVideorecordapp().disposeInformationDialog();
					if(hapistatus == FILE_LOCKED)
					{
						JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(),
								LocaleStrings.getString("Z_2_URLP"), LocaleStrings.getString("A_5_GLOBAL"),
								JOptionPane.ERROR_MESSAGE);
						return FILE_LOCKED;
					}
					else if(hapistatus == FILE_NOT_FOUND)
					{
						JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(),
								LocaleStrings.getString("Z_3_URLP"), LocaleStrings.getString("A_5_GLOBAL"),
								JOptionPane.ERROR_MESSAGE);
						return FILE_NOT_FOUND;
					}
			}
		}
		
        return hapistatus;
    }
                                                                                      
  

	/** Reads the connection's InputStream and finds the value of the searched header.
	 *  
	 * @param search - Header value to be searched for.
	 * @param search - delimiter character that comes after the searched value.
	 * @return Returns the value of the searched header.
	 */
	public String getValue(String search, char delimiter){
		String value = null;
		String responseData = new String(data);			
		int start = responseData.indexOf(search);		
		int end = responseData.indexOf(delimiter, start+search.length());
		if(end <0){
			end = responseData.indexOf(DEF_DELIMITER, start+search.length());
		}

		if(start > 0 && end > 0)			{

			value = responseData.substring(start+search.length(), end).trim();				

		} 		
		return value;
	}
	
	public String getValueOf(String source, String search, char endDelimiter) {
		String value = "";
		if ((source != null) && (search != null) && (source.length() > 0) && (search.length() > 0)) {
			int start = source.indexOf(search) + search.length();
			int end = source.indexOf(endDelimiter, start);
			if ((start > 0) && (end > 0)) {
				value = source.substring(start, end).trim();
			}
		}
		return value;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data){
		this.data = data;
	}

	/**
	 * This function initiates the Downloader thread.
	 */
	public void downloadFile(String urlPath){
		Downloader downLoader = new Downloader(urlPath);
	}

	/**
	 * Connect to the URL Path Specified and Download the Video Stream data file
	 * @param urlPath
	 * @return
	 * @throws IOException
	 */

	public int  download(String urlPath) throws IOException {

		byte[] bucket;
		int bytesRead = 0;
		long totalBytesRead = 0;
		ArrayList<Long> avgSpeed = new ArrayList<Long>(0);
		String bytesReadInTotal = "";

		uriString = urlPath;
		boolean exceptionOccurred = false;

		if(socket == null) {
			return HTTP_REQUEST_FAILURE;
		}

		if(writeRequestToSocket(socket, null) != 0) {
			return HTTP_REQUEST_FAILURE;
		}

		File file = new File(JViewerApp.getInstance().getVideorecordapp().Videotmpstorepath[0]);
		FileOutputStream outStream = new FileOutputStream(file);
		InputStream inStream = new BufferedInputStream(socket.getInputStream());
		try {

			bucket = new byte[MAX_BUFFER_SIZE];

			if(getHTTPResponseHeader(inStream) != 0)
				return HTTP_REQUEST_FAILURE;

			if(data.toString().contains("404 Not Found"))
				return FILE_NOT_FOUND;

			int contentLength = Integer.parseInt(getValueOf(new String(data), "Content-Length:", '\r').trim());
			// Quit if the file doesn't have any data
			if(contentLength <= 0)
				return EMPTY_FILE;

			String msg = JViewerApp.getInstance().getVideorecordapp().getCurrentMsg();
			String currentSpeed = "";
			String currentBytes = "";
			String totalBytes = "";
			long startTime = JViewerApp.getInstance().getCurrentTime();
			long elapsedTime = 0L;
			long bytesReadPerSec = 0L;
			long speedInBps = 0L;

			if (msg != null && msg.length() > 0) { // Update only if valid info dialog component is found
				try {
					/* contentLength will contain total size of the video file in Bytes
					** Converting numeric value into user readable format */
					if ((totalBytes = formatBytes(contentLength)).length() > 0) {
						totalBytes = String.format(" / %s)", totalBytes);
					} else { // Error or Invalid value
						totalBytes = "";
					}
				} catch (Exception e) {
					Debug.out.println(e);
					totalBytes = "";
				}
			}

			while ((bytesRead = inStream.read(bucket)) != -1) {

				outStream.write(bucket, 0, bytesRead);

				totalBytesRead += bytesRead;

				if ((msg != null) && (msg.length() > 0) && (totalBytes != null) && (totalBytes.length() > 0)) {
					bytesReadPerSec += bytesRead; // Keep track of how many bytes of data read per second
					try {
						/* For calculating bandwidth speed, atleast one sec of
						 * time needs to be elapsed. Because of this, during
						 * initial launch only bytes read information will be
						 * displayed. bandwidth speed will be displayed once the
						 * calculation is finished.
						 */
						if ((elapsedTime = (JViewerApp.getInstance().getCurrentTime() - startTime)) >= ONE_SEC) {
							if(bytesReadPerSec > 0){
								// Calculate bandwidth speed in bytes read/sec format
								speedInBps = (bytesReadPerSec / (elapsedTime / ONE_SEC));
								avgSpeed.add(speedInBps);
								/* speed will contain Bytes of data read per second
								** Converting numeric value into user readable format */
								if ((currentSpeed = formatBytes(speedInBps)).length() > 0) {
									currentSpeed = String.format(" @ %s/s", currentSpeed);
								} else {
									currentSpeed = " @ 0B/s";
								}
							} else { // No data is read
								currentSpeed = " @ 0B/s";
							}
							// Reset the values here so that we can keep track
							// of how many bytes of data read per second
							bytesReadPerSec = 0;
							startTime = JViewerApp.getInstance().getCurrentTime();
						}
					} catch (Exception e) {
						Debug.out.println(e);
						currentSpeed = " @ 0B/s";
					}

					try {
						/* totalBytesRead will contain total size (in Bytes) of
						 * the video file downloaded so far. Converting such
						 * numeric value into user readable format */
						if ((currentBytes = formatBytes(totalBytesRead)).length() > 0) {
							if(totalBytesRead >= contentLength) {
								bytesReadInTotal = currentBytes;
							}
							currentBytes = String.format(" (%s%s", currentBytes, totalBytes);
						} else { // Error or Invalid value
							currentBytes = "";
							bytesReadInTotal = "";
						}
					} catch (Exception e) {
						e.printStackTrace();
						currentBytes = "";
						bytesReadInTotal = "";
					}
					// Update the String into Connecting and Downloading info dialog
					// String will be formatted like
					// Eg: Connecting and Downloading is in progress (8MB / 20MB) @ 1MB/s
					JViewerApp.getInstance().getVideorecordapp().updateMsg(msg + currentBytes + currentSpeed);
				}

				if(totalBytesRead >= contentLength) {
					Debug.out.println("Read till content length !");
					break;
				}

			}
		} catch(Exception ex) {
			Debug.out.println("Exception : while reading bytes from input stream " + ex );
			exceptionOccurred = true;
		} finally {
			try {
				outStream.close();

				// close the input stream only if there is any exception
				// closing the input stream will close the socket associated with the stream.
				// Then any further communication with the socket will fail.
				if(exceptionOccurred) {
					inStream.close();
					return -1;
				} else {
					try {
						if (avgSpeed.size() > 0) {
							Long speed = 0L;
							String msg = JViewerApp.getInstance().getVideorecordapp().getCurrentMsg();
							String speedInfo = "";
							// Calculate Avg. bandwidth speed and display the information to user
							if (msg != null && msg.length() > 0) {
								for (Long value : avgSpeed) {
									speed += value;
								}
								speed /= avgSpeed.size();
								if ((speedInfo = formatBytes(speed)).length() > 0) {
									// Msg will be displayed like,
									// Eg: Downloaded 33.4MB @ Avg. Speed of 2.0MB/s
									JViewerApp.getInstance().getVideorecordapp().updateMsg(String.format(LocaleStrings.getString("AA_8_VRA"), bytesReadInTotal, speedInfo+"/s"));
									Thread.sleep(ONE_SEC);
								}
							}
						}
					} catch (Exception e) {
						Debug.out.println(e);
					}
				}
			} catch(IOException ex) {
				Debug.out.println(ex);
				return -1;
			}
		}
		return 0;
	}

	public int writeRequestToSocket(Socket socket, String requestHeader){

		//use rest functions for rest service
		if(JViewer.isRestService()){
			return restWriteRequestToSocket(socket, requestHeader);
		}

		int ret = 0;
		PrintWriter writer = null;
		try {
			// Use NEWLINE to represent line ending.
			// don't use println(), use print() with NEWLINE for proper representation of newline independent of client system 
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.print("GET "+uriString+" HTTP/1.1" + NEWLINE);
			writer.print("Host: " + hostIP + NEWLINE);
			writer.print("Accept: */*" + NEWLINE);
			writer.print(USER_AGENT_TAG + ": " + USER_AGENT_NAME + NEWLINE);
			if(sesCookie != null){
				writer.print("Cookie: SessionCookie=" + sesCookie + NEWLINE);
			}
			if(requestHeader != null && requestHeader.length() > 0)
				writer.print(requestHeader + NEWLINE);
			writer.print(NEWLINE); // denotes end of the request.
			writer.flush();
		} catch (IOException e) {
			Debug.out.println(e);
			ret = -1;
		}
		return ret;
    }

    public int getServerResponseData(){
		int ret = 0;
		BufferedReader reader = null;
		String dataString = "";
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			for (String line = ""; (line = reader.readLine()) != null;) {
				// incase of rest calls, response end will be denoted by "0"
				if (line.startsWith(DYNAMIC_DATA_END) || line.equals(ZERO)){ // Stop when headers are completed. We're not interested in all the HTML.
					break;
				}
				if (line.contains(NOT_FOUND))
				{
					ret = FILE_NOT_FOUND;
					break;
				}
				else if (line.contains(UNAUTHORIZED))
				{
					ret = INVALID_CREDENTIALS;
					break;
				}
				dataString += line + "\n";
			}
		}
		catch(Exception e)
		{
			ret = -1;
			setSocket(null);
			Debug.out.println(e);
		}
		data = dataString.getBytes();
		return ret;
    }

	/**
	 * Reads the header from the passed inputstream
	 * @param stream the inputstream from which header is to be read
	 * @return 0 if success, -1 on failure
	 */
	public int getHTTPResponseHeader(InputStream stream){
		int c = 0;
		int ret = 0;
		String arr = null;
		try {
			try {
				while ((c = stream.read()) != -1){
					arr += (char)c;
					if(arr.contains("\r\n\r\n"))
					{
						Debug.out.println("Read till header end");
						break;
					}	
				}
			} catch (IOException e) {
				Debug.out.println(e);
				ret = -1;
			}
		}catch(Exception e){
			Debug.out.println(e);
			ret = -1;
		}
		if(ret == 0)
			data = arr.getBytes();
		return ret;
	}


    /**
     * @return the socket
     */
    public static Socket getSocket() {
        return socket;
    }

	public static void setSocket(Socket socket) {
		URLProcessor.socket = socket;
	}

	/**
	 * Parses the http header till "\r\n\r\n", gets the content length and reads data from response and dumps into data buffer
	 * @return 0 on success, -1 if any error occurs
	 */
	private int getDataFromSocket() {
		BufferedInputStream in = null;
		String response= null;
		
		try {
			in = new BufferedInputStream(socket.getInputStream());
		} catch (IOException e) {
			Debug.out.println(e);
			return HTTP_REQUEST_FAILURE;
		}
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		int c = 0;
		int ct = 0;

		if(getHTTPResponseHeader(in)!= 0)
			return HTTP_REQUEST_FAILURE;

		if (data != null) {
			response = new String(data);
		} else {
			return HTTP_REQUEST_FAILURE;
		}
		
		if(response.contains("404")|| (response.contains("404&nbsp;Not Found")))
			return FILE_NOT_FOUND;

		int length = Integer.parseInt(getValueOf(response, "Content-Length:", '\r').trim());

		try {
			while ((c = in.read()) != -1){
				ct++;
				if(ct >= length)
				{
					break;
				}
				byteArrayOut.write(c);
			}
		} catch (IOException e) {
			Debug.out.println(e);
			return HTTP_REQUEST_FAILURE;
		}
		data = byteArrayOut.toByteArray();

		try {
			byteArrayOut.flush();
			byteArrayOut.close();
			in.close();
			socket.close();
			setSocket(null);
		} catch (IOException e) {
			Debug.out.println(e);
			return HTTP_REQUEST_FAILURE;
		}
		return 0;
	}

	public String getUriString() {
		return uriString;
	}

	public void setUriString(String uriString) {
		this.uriString = uriString;
	}

	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}

	public int restProcessRequest(String urlPath){
		Debug.out.println("urlPath: " + urlPath);
		int ret = -1;

		// if this function is executed for the first time, then create a socket and use it for all asp requests.
		if(socket == null) {
			try {
				// service variable being passed represents which service calls the createSocket() method
				// service value will be 0, if nonssl socket should be created.
				// Incase of SSLSocket for web requests, service value should be 1
				//Incase of SSLSocket for video/media request, service value should be 2 
				int service = (JViewer.isWebSecure() == true) ? (JViewerApp.WebService): JViewerApp.NonSSLService;
				socket = JViewerApp.getInstance().getConnection().createSocket(InetAddress.getByName(JViewer.getIp()), JViewer.getWebPort(), service);
			} catch (UnknownHostException e) {
				Debug.out.println(e);
			}
			// return invalid server certificate, standalone connection dialog will close.
			if(socket == null) {
				return INVALID_SERVER_CERT;
			}
		}

		uriString = urlPath;
		// If there is any exception while writing to the socket, return request failure
		if(restWriteRequestToSocket(socket, null) != 0) {
			return HTTP_REQUEST_FAILURE;
		}
		if(urlPath.contains("/api/logs/help-data"))
			ret = getDataFromSocket();
		else {
			ret = getServerResponseData();
			if (ret == SUCCESS) {
				// Parse the error code in response data
				if (urlPath.contains("/api/logs/video-log") && urlPath.contains("file_access=" + CHECK_FILE_ACCESS)) {
					if (data != null) {
						String responseData = new String(data);
						if ((responseData != null) && (responseData.length() > 0)) {
							if (responseData.contains("code")) {
								try {
									if (Integer.parseInt(getValueOf(responseData, "\"code\": ", ' ').trim()) == REST_RECORDING_IN_PROGRESS) {
										ret = URLProcessor.RECORDING_IN_PROGRESS;
									}
								} catch (NumberFormatException e) {
									Debug.out.println(e);
								} catch (Exception e) {
									Debug.out.println(e);
								}
							}
						}
					}
				}
			}
			else
			{
				try {
					//close the socket if requested data not found
					if(socket != null)
					{
						socket.close();
						setSocket(null);
					}
				}
				catch(Exception e)
				{
					setSocket(null);
					Debug.out.println(e);
				}
			}
		}
		return ret;
	}

	public int restWriteRequestToSocket(Socket socket, String requestHeader){
		int ret = 0;
		PrintWriter writer = null;
		try {
			// Use NEWLINE to represent line ending.
			// don't use println(), use print() with NEWLINE for proper representation of newline independent of client system 
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			if(csrfToken == null && sesCookie == null) {
				writer.print("POST "+uriString+" HTTP/1.1" + NEWLINE);
			}
			else if(uriString.contains("/api/logs/video-log")){
				writer.print("PUT "+uriString+" HTTP/1.1" + NEWLINE);
			}
			else if(uriString.contains("/kvm/token") || uriString.contains("/settings/media/instance") || uriString.contains("/api/settings/media/adviser") || uriString.contains("/api/logs/video") || uriString.contains("/api/logs/help-data")){
				writer.print("GET "+uriString+" HTTP/1.1" + NEWLINE);
			}
			else{
				writer.print("DELETE "+uriString+" HTTP/1.1" + NEWLINE);
			}

			writer.print("Accept: */*" + NEWLINE);

			writer.print(USER_AGENT_TAG + ": " + USER_AGENT_NAME + NEWLINE);

			writer.print("Host: " + hostIP + NEWLINE);

			writer.print("Connection: Keep-Alive" + NEWLINE);

			if(uriString.contains("/api/session"))
				writer.print("Content-Length: 0" + NEWLINE);

			if(csrfToken != null){
				writer.print("X-CSRFTOKEN: " + csrfToken + NEWLINE);
			}

			if(sesCookie != null){
				writer.print("Cookie: " + sesCookie + NEWLINE);
			}
				if(requestHeader != null && requestHeader.length() > 0)
					writer.print(requestHeader + NEWLINE);

			writer.print(NEWLINE); // denotes end of the request.
			writer.flush();
		} catch (IOException e) {
			Debug.out.println(e);
			ret = -1;
		}
		return ret;
	}

	public static String getCsrfToken() {
		return csrfToken;
	}

	public static void setCsrfToken(String csrfToken) {
		URLProcessor.csrfToken = csrfToken;
	}

	/**
	 * @return the downloadStatus
	 */
	public int getDownloadStatus() {
		return downloadStatus;
	}

	/**
	 * This thread will invoke the file download from the web server.
	 */
	class Downloader extends Thread{
		String urlPath;
		public Downloader(String urlPath){
			this.urlPath = urlPath;
			this.start();
		}
		public void run(){
			try {
				downloadStatus = download(urlPath);
			} catch (IOException e) {
				Debug.out.println(e);
				downloadStatus = FAILURE;
			}catch (Exception e) {
				Debug.out.println(e);
				downloadStatus = FAILURE;
			}
			finally{
				// Dispose the displayed dialog while the video file is being downloaded.
				JViewerApp.getInstance().getVideorecordapp().disposeInformationDialog();
			}
		}
	}
	
	/**
	 * Formats the given data(in Bytes) into user readable format
	 * 
	 * @param dataInBytes
	 *            length (or) size in Bytes
	 * @return formatted data string with corresponding unit. Empty string in
	 *         case of any exception / invalid data
	 */
	public String formatBytes(long dataInBytes) {
		DecimalFormat numberFormat = new DecimalFormat("###.#"); // decimal round off with 1 digit
		String formattedString = "";
		try {
			if (dataInBytes >= GB) { // Unit Giga Bytes
				formattedString = numberFormat.format(dataInBytes/(float)GB) + "GB";
			} else if (dataInBytes >= MB) { // Unit Mega Bytes
				formattedString = numberFormat.format(dataInBytes/(float)MB) + "MB";
			} else if (dataInBytes >= KB) { // Unit Kilo Bytes
				formattedString = numberFormat.format(dataInBytes/(float)KB) + "KB";
			} else if (dataInBytes >= 0) { // Unit Bytes
				formattedString = dataInBytes + "B";
			}
		} catch (Exception e) {
			Debug.out.println(e);
			formattedString = "";
		}
		return formattedString;
	}
}
