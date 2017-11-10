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
// Debug message module
//



package com.ami.kvm.jviewer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ami.kvm.jviewer.gui.DebugLogFileChooser;
import com.ami.kvm.jviewer.gui.InfoDialog;
import com.ami.kvm.jviewer.gui.LocaleStrings;
/**
 * Debug message control module
 */
public class Debug {
	public static Debug out = new Debug();
	public static int MODE;
	public static int VERIFYSSLCERTS;

	public static final int CREATE_LOG = 0;
	public static final int DEBUG = 1;
	public static final int RELEASE = 2;
	
	public static final int SKIP_VERIFICATION = 0;
	public static final int VERIFY = 1;

	private File logFile = null;
	private FileWriter fWriter = null;
	private PrintWriter pWriter = null;

	private boolean runTimeLogging = false;
	/**
	 * Constructor
	 */
	private Debug() {
 		MODE = RELEASE;
 		VERIFYSSLCERTS = VERIFY;
		if(MODE == CREATE_LOG){
			initLog(logFile);
		}
	}

	/**
	 * Print given object with.
	 */
	public void print(Object e) {
		if (MODE == DEBUG) {
			//print exception stack trace
			if(e.getClass().getName().contains("Exception")){
				Exception except = (Exception) e;
				System.err.println("\nEXCEPTION !!!");
				except.printStackTrace();
			}
			else if(e.getClass().getName().contains("Error")){
				Error error = (Error) e;
				System.err.println("\nERROR !!!");
				error.printStackTrace();
			}
			else
				System.out.print(e);
		}
		else if(MODE == CREATE_LOG){
			try {
				if(fWriter == null)
					fWriter = new FileWriter(logFile, true);
				//log exception stack trace
				if(e.getClass().getName().contains("Exception")){
					Exception except = (Exception) e;
					if(pWriter == null)
						pWriter = new PrintWriter(fWriter, true);
					pWriter.append("\nEXCEPTION !!!\n");
					except.printStackTrace(pWriter);
					pWriter.append('\n');
				}
				else if(e.getClass().getName().contains("Error")){
					Error error = (Error) e;
					if(pWriter == null)
						pWriter = new PrintWriter(fWriter, true);
					pWriter.append("\nERROR !!!\n");
					error.printStackTrace(pWriter);
					pWriter.append('\n');
				}
				else
					fWriter.append((CharSequence) e);
			} catch (IOException ioe) {
				System.err.println("ERROR WHILE WRITTING TO LOG FILE!!!");
				ioe.printStackTrace();
				closeLog();
			}
			catch(ClassCastException cce){
				try {
					fWriter.append((CharSequence)e.toString());
				} catch (IOException ioe) {
					System.err.println("ERROR WHILE WRITTING TO LOG FILE!!!");
					ioe.printStackTrace();
					closeLog();
				}

			}
			catch (Exception ex) {
				System.err.println("ERROR WHILE WRITTING TO LOG FILE!!!");
				ex.printStackTrace();
				closeLog();
			}
		}
	}

	/**
	 * Print given object with new line.
	 */
	public void println(Object e) {
		if (MODE == DEBUG) {
			//print exception stack trace
			if(e.getClass().getName().contains("Exception")){
				Exception except = (Exception) e;
				System.err.println("\nEXCEPTION !!!");
				except.printStackTrace();
			}
			else if(e.getClass().getName().contains("Error")){
				Error error = (Error) e;
				System.err.println("\nERROR !!!");
				error.printStackTrace();
			}
			else
				System.out.println(e);
		}
		else if(MODE == CREATE_LOG){
			try {
				if(fWriter == null)
					fWriter = new FileWriter(logFile, true);
				//log exception stack trace
				if(e.getClass().getName().contains("Exception")){
					Exception except = (Exception) e;
					if(pWriter == null)
						pWriter = new PrintWriter(fWriter, true);
					pWriter.append(("[ "+ new java.util.Date()).toString()+" ] ");
					pWriter.append("\nEXCEPTION !!!\n");
					except.printStackTrace(pWriter);
					pWriter.append('\n');
				}
				else if(e.getClass().getName().contains("Error")){
					Error error = (Error) e;
					if(pWriter == null)
						pWriter = new PrintWriter(fWriter, true);
					pWriter.append(("[ "+ new java.util.Date()).toString()+" ] ");
					pWriter.append("\nERROR !!!\n");
					error.printStackTrace(pWriter);
					pWriter.append('\n');
				}
				else{
					fWriter.append(("[ "+ new java.util.Date()).toString()+" ] ");
					fWriter.append((CharSequence) e);
					fWriter.append('\n');
				}
			} catch (IOException ioe) {
				System.err.println("ERROR WHILE WRITTING TO LOG FILE!!!");
				ioe.printStackTrace();
				closeLog();
			}
			catch(ClassCastException cce){
				try {
					fWriter.append((CharSequence)e.toString());
				} catch (IOException ioe) {
					System.err.println("ERROR WHILE WRITTING TO LOG FILE!!!");
					ioe.printStackTrace();
					closeLog();
				}

			}
			catch (Exception ex) {
				System.err.println("ERROR WHILE WRITTING TO LOG FILE!!!");
				ex.printStackTrace();
				closeLog();
			}
		}
	}

	/**
	 * Print given error msg.
	 */
	public void printError(Object e) {
		if (MODE == DEBUG) {
			System.err.println(e);
		}
		else if(MODE == CREATE_LOG){
			println(e);
		}
	}

	/**
	 * Print hex dump
	 *
	 * @param buffer byte array to be printed
	 */
	public void dump(byte[] buffer) {
		if (MODE == DEBUG || MODE == CREATE_LOG) {
			dump(buffer, buffer.length);
		}
	}

	/**
	 * Print hex dump
	 *
	 * @param buffer byte array to be printed
	 * @param length buffer length
	 */
	public void dump(byte[] buffer, int length) {
		if (MODE == DEBUG || MODE == CREATE_LOG) {
			dump(buffer, 0, length);
		}
	}

	/**
	 * Print hex dump
	 *
	 * @param buffer byte array to be printed
	 * @param offset offset in byte array
	 * @param length buffer length
	 */
	public void dump(byte[] buffer, int offset, int length) {
		if (MODE == DEBUG || MODE == CREATE_LOG) {
			if ((buffer.length - offset) < length) {
				println("Invalid buffer");
				return;
			}

			StringBuffer hexString = new StringBuffer(2 * length);

			// convert byte array to hex string
			for (int i = offset; i < (offset + length); i++) {

				appendHexPair(buffer[i], hexString);
			}

			// print hex string
			for (int j = 0; j < hexString.length(); j += 60) {

				int end = j + 60;
				if (end > hexString.length()) {
					end = hexString.length();
				}

				println(hexString.substring(j, end));
			}
		}
	}


	/**
	 * Get Hexadicimal string representation of the IPMI response
	 * @param buffer byte array to be printed
	 * @param offset offset in byte array
	 * @param length buffer length
	 * @return Hexadicimal string representation of the IPMI response
	 */
	public String dumpIPMI(byte[] buffer, int offset, int length) {
		String ipmiData = "";

		if ((buffer.length - offset) < length) {
			println("Invalid buffer");
			return null;
		}

		StringBuffer hexString = new StringBuffer(2 * length);

		// convert byte array to hex string
		for (int i = offset; i < (offset + length); i++) {

			appendHexPair(buffer[i], hexString);
		}
		String tempString = hexString.substring(3, hexString.length());
		for(int i = 0; i < tempString.length(); i+=48){
			int end = i + 48;
			if (end > tempString.length()){
				end = tempString.length();
			}
			ipmiData += tempString.substring(i, end) + "\n";
		}
		return ipmiData;
	}
	/**
	 * append given byte to hex string
	 *
	 * @param b byte
	 * @param hexString hex string
	 */
	public static void appendHexPair(byte b, StringBuffer hexString) {

		char highNibble = kHexChars[(b & 0xF0) >> 4];
		char lowNibble = kHexChars[b & 0x0F];

		hexString.append(highNibble);
		hexString.append(lowNibble);
		hexString.append(' ');
	}

	public String getTimeStamp(){
		String timeStamp = "";
		Date currDate = new Date();
		SimpleDateFormat dateFormater = new SimpleDateFormat("'on_'dd-MM-yyyy'_at_'HH-mm-ss-SSS");
		timeStamp =  dateFormater.format(currDate);
		return timeStamp;
	}
	public void closeLog(){
		try {
			if(pWriter != null )
				pWriter.close();
			pWriter = null;
			if(fWriter != null)
				fWriter.close();
			fWriter = null;
		if(runTimeLogging){
			InfoDialog.showDialog(JViewer.getMainFrame(),
					LocaleStrings.getString("AH_1_DBG")+logFile,
					LocaleStrings.getString("AH_2_DBG"),
					InfoDialog.INFORMATION_DIALOG);
			runTimeLogging = false;
		}
		} catch (IOException ioe) {
			System.err.println("ERROR WHILE CLOSING LOG FILE WRITTER!!!");
			pWriter = null;
			fWriter = null;
			ioe.printStackTrace();
		}
	}
	private static final char kHexChars[] =
	{ '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F' };

	/**
	 * Initialize the DebugLogFileChooser Object and start the thread.
	 */
	public void initDebugLogFileChooser(){
		DebugLogFileChooser debugLogFileChooser = new DebugLogFileChooser();
		initLogFile();
		debugLogFileChooser.start();
	}

	/**
	 * Initialize debug logging mechanism.
	 * @param logFile - The file in which console output should be logged.
	 */
	public void initLog(File logFile){

		if(logFile == null)
			initLogFile();
		else{
			this.logFile = logFile;
			runTimeLogging = true;
		}
		try {
			fWriter = new FileWriter(this.logFile, true);
		} catch (IOException e) {
			System.err.println("ERROR WHILE CREATING LOG FILE!!!");
			e.printStackTrace();
			fWriter = null;
		}
		Debug.MODE = Debug.CREATE_LOG;
	}

	/**
	 * Initializes the log file.
	 */
	public void initLogFile(){
		logFile = new File(System.getProperty("user.home")+
					File.separator+"JViewer_log_to-"+JViewer.getIp()+"-"+getTimeStamp()+".log");
	}

	/**
	 * @return the logFile
	 */
	public File getLogFile() {
		return logFile;
	}

	/**
	 * @return the runTimeLogging
	 */
	public boolean isRunTimeLogging() {
		return runTimeLogging;
	}

	/**
	 * @param runTimeLogging the runTimeLogging to set
	 */
	public void setRunTimeLogging(boolean runTimeLogging) {
		this.runTimeLogging = runTimeLogging;
	}
}

