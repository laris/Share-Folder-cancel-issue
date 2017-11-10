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
// KVM client extends Client abstract class. This module implements socket
// creation for KVM data and manages KVM data communication. It provides
// framing functionality for raw video data and indicates JVVideo for furthur
// processing. This module also provides an interface for sending client
// requests to server.
//

package com.ami.kvm.jviewer.kvmpkts;

import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.common.oem.IOEMManager;
import com.ami.kvm.jviewer.communication.Client;
import com.ami.kvm.jviewer.communication.NWListener;
import com.ami.kvm.jviewer.gui.AddMacro;
import com.ami.kvm.jviewer.gui.InfoDialog;
import com.ami.kvm.jviewer.gui.JVFrame;
import com.ami.kvm.jviewer.gui.JVMenu;
import com.ami.kvm.jviewer.gui.JViewerApp;
import com.ami.kvm.jviewer.gui.JViewerView;
import com.ami.kvm.jviewer.gui.KVMShareDialog;
import com.ami.kvm.jviewer.gui.KVMSharing;
import com.ami.kvm.jviewer.gui.LocaleStrings;
import com.ami.kvm.jviewer.hid.USBMessage;
import com.ami.kvm.jviewer.jvvideo.JVVideo;
import com.ami.vmedia.VMApp;
import com.ami.vmedia.gui.VMDialog;



/**
 * KVM client class.
 */
public class KVMClient extends Client {
	//Use the maximum X and Y resolution values defined in JVVideo class.
	public static final int MAX_FRAGMENT_SIZE 	= (9 *1024 *1024)/2;// full screen size is 9MB so it should be half of it;//(2 * JVVideo.MAX_X_RESOLUTION * JVVideo.MAX_Y_RESOLUTION);// + (1024*1024);
	public static final int USER_DATA_LENGTH = 134;
	//Keep timeout to 6000 inorder to detect network loss immediatly
	private static final int TIMEOUT_VALUE = 6000;

	public  IVTPPktHdr		m_pktHdr;
	public  short 			m_fragNum;
	public  ByteBuffer		m_ctrlMsg;
	private HeaderReader 	m_hdrReader;			// state header reader
	private FragNumReader 	m_fragNumReader;		// state fragment size reader
	private FragReader   	m_fragReader;			// state fragment reader
	private CtrlReader      m_ctrlReader;			// state control message reader
	private NullReader		m_nullReader;			// state null reader
	private KVMReader       m_reader;				// current reader.
	private JVVideo 		m_vidClnt;				// video client that takes frames
	private NWListener 		m_NWListener;			// network listner
 	private volatile Thread m_NWThread;				// network thread
	private boolean 		m_redirection = false;
	private KMCrypt			m_kmCrypt;				// keyboard/mouse crypt handler
	private boolean			m_encrypt = false;
	

	private static String[] userData;
	private static int numUsers = 0;
	
	/* Error codes for validate video session */
	public static final int INVALID_SESSION					= 0x00;
	public static final int VALID_SESSION					= 0x01;
	public static final int KVM_DISABLED					= 0x02;
	public static final int STOP_SESSION_IMMEDIATE			= 0x02;
	public static final int INVALID_VIDEO_SESSION_INFO		= 0x03;
	public static final int INVALID_CDROM_SESSION_TOKEN		= 0x04;
	public static final int INVALID_FLOPPY_SESSION_TOKEN	= 0x05;
	public static final int INVALID_CLIENT_IP				= 0x06;
	public static final int INVALID_MAC_ADDR				= 0x07;
	public static final int SESSION_UNREGISTERED			= 0x08;
	public static boolean DISABLE_ENCRPT_FLAG 				= false;
	public static final int STOP_SESSION_CONF_CHANGE		= 0x05;
	public static final int STOP_SESSION_WEB_LOGOUT			= 0x07;
	public static final int STOP_SESSION_LICENSE_EXP		= 0x08;
	public static final int STOP_SESSION_TIMED_OUT			= 0x09;
	public static final int STOP_SESSION_KVM_DSCONNECT		= 0x0A;
	public static final int STOP_SESSION_WEBSERVER_RESTART	= 0x0B;
	public static final int STOP_SESSION_WARM_RESET	= 0x0C;
	public static final int STOP_SESSION_RESTORE_DEFAULT	= 0x0D;
	public  IVTPPktHdr		m_pktPreLoginHdr 				= null;

	public static final int HOST_LOCK_FEATURE_DISABLED 		= 0x00;
	public static final int HOST_LOCK_FEATURE_ENABLED 		= 0x01;
	public static final byte GET_HOST_LOCK_STATUS 			= 0x02;

	public static final byte MAX_SESSION_REACHED			= 0x00;
	public static final byte SAME_KVM_CLIENT_USER			= 0x01;
	public static final int TERMINATE_SESSION_PKT_SIZE 		= 5;

	private boolean m_bUseSSL = false;
	public boolean m_isBlank = false;
	private boolean Host_OS_shutdown = false;
	private long m_startTS;
	private long m_stopTS;

	private ConfPkt[] confPacket = null;
	private UserDataPacket userDataPacket = null;

	private short hostLockStatus = -1;
	byte hostLockFeatureStatus = HOST_LOCK_FEATURE_ENABLED;
	private boolean stopSignalReceived = false;

	/**
	 * The constructor
	 */
	public KVMClient(byte[] ipAddr, int port, JVVideo vidClnt, boolean bUseSSL) {

		super(ipAddr, port);
		m_vidClnt = vidClnt;
		m_hdrReader 	= new HeaderReader(this);
		m_fragNumReader = new FragNumReader(this);
		m_fragReader 	= new FragReader(this);
		m_ctrlReader 	= new CtrlReader(this);
		m_nullReader	= new NullReader(this);
		//sockvmclient.setSOCKVMclient(this,ipAddr, port,  vidClnt);
		m_bUseSSL = bUseSSL;
		m_reader = m_hdrReader;
		m_kmCrypt = new KMCrypt();
	}

	/**
	 * Read notification event handler
	 */
	public void onReadEvent() {
		Socket sock = getSocket();
		if(JViewer.isKVMReconnectEnabled())
		{
			if(JViewerApp.getInstance().getLocalport() != sock.getLocalPort())
				return ;
		}
		int iStatus = m_reader.read(sock);
		//JFrame mframe = JViewerApp.getInstance().getMainWindow();
		// Modified for JInternalFrmae
		//JInternalFrame mframe = JViewerApp.getInstance().getM_frame();
		JInternalFrame mframe = JViewerApp.getInstance().getMainWindow();

		if (-1 == iStatus)	{//	Socket failure
			try {
				if(JViewer.isKVMReconnectEnabled() && (JViewer.isjviewerapp() || JViewer.isStandAloneApp()))
				{
					if((JViewerApp.getInstance().GetRedirectionState()!= JViewerApp.REDIR_STOPPING) &&
					(JViewerApp.getInstance().GetRedirectionState()!= JViewerApp.REDIR_STOPPED))
					{
						if(!JViewerApp.getInstance().getRetryConnection())
						{
							JViewerApp.getInstance().onReconnect();
						}
					}
				}
				else
				{
					Thread.sleep(1000);
				}
			} catch(Exception e) {
				Debug.out.println(e);
			}
			if(!JViewer.isKVMReconnectEnabled())
			{
				//VIdeo Socket error shouldn't be shown for Web Previewer and when client system time is changed.
				if(!JViewer.isWebPreviewer()){
					JViewerApp.getInstance().setRedirectionStatus(JViewerApp.REDIR_STOPPED);
					JOptionPane.showMessageDialog (
							mframe,
							LocaleStrings.getString("2_1_KVMCLIENT"),
							LocaleStrings.getString("2_2_KVMCLIENT"),
							JOptionPane.ERROR_MESSAGE);
					JViewerApp.getInstance().getMainWindow().windowClosed();
				}
			}
		 }
		else{
			if(JViewer.isKVMReconnectEnabled())
			{
				JViewerApp.getInstance().setLastPcktRecvd();
			}
		}
	}

	/**
	 * Set reader state.
	 *
	 * @param reader - KVM reader.
	 */
	public void setState(KVMReader reader) {

		m_reader = reader;
	}

	/**
	 * HeaderReader getter method.
	 *
	 * @return Header reader
	 */
	public HeaderReader getHdrReader() {

		return m_hdrReader;
	}

	/**
	 * FragNumReader getter method.
	 *
	 * @return Fragment Number Reader
	 */
	public FragNumReader getFragNumReader() {

		return m_fragNumReader;
	}

	/**
	 * FragReader getter method.
	 *
	 * @return Fragment Reader
	 */
	public FragReader getFragReader() {

		return m_fragReader;
	}

	/**
	 * CtrlReader getter method.
	 *
	 * @return Controll Reader
	 */
	public CtrlReader getCtrlReader() {

		return m_ctrlReader;
	}

	/**
	 * NullReader getter method
	 *
	 * @return Null Reader
	 */
	public NullReader getNullReader() {

		return m_nullReader;
	}

	/**
	 * New frame handler
	 *
	 * @param frame New frame to be handled.
	 */
	public boolean onNewFrame(ByteBuffer frame) {
		
		if (JViewerApp.getInstance().m_refresh) {
			JViewerApp.getInstance().m_refresh = false;
			JViewerApp.getInstance().OnVideoPauseRedirection();
		}

		if ( m_vidClnt.onNewFrame(frame) != 0) {
			Debug.out.println("onNewFrame failed.");
			return false;
		}

		return true;
	}

	/**
	 * Control message handler
	 */
	public void onControlMessage() {
		Debug.out.println("CMDTYPE::"+m_pktHdr.type);

		if(JViewer.isKVMReconnectEnabled())
		{
			JViewerApp.getInstance().setLastPcktRecvd();
		}

		if(JViewerApp.getOEMManager() != null){
			if(JViewerApp.getOEMManager().getOEMKvmClient() != null){
				if(JViewerApp.getOEMManager().getOEMKvmClient().handleControlPackets(m_pktHdr, m_ctrlMsg)
						== IOEMManager.OEM_CUSTOMIZED){
					Debug.out.println("OEM handled CMDTYPE::"+m_pktHdr.type );
					return;
				}
			}
		}

		switch (m_pktHdr.type) {

		case IVTPPktHdr.IVTP_BLANK_SCREEN:
			Debug.out.println("*** Blank screen");
			if(!m_isBlank) {
				m_vidClnt.onBlankScreen();
				m_isBlank = true;
			}

			break;
		case IVTPPktHdr.IVTP_STOP_SESSION_IMMEDIATE:
			Debug.out.println("*** Stop session Status : "+m_pktHdr.status());

			stopSignalReceived = true;
			if(JViewer.isKVMReconnectEnabled())
			{
				JViewerApp.getInstance().setSessionLive(false);
			}

			//Stop redirection before showing pop-up to clear session in adviser side
			if(JViewerApp.getInstance().getKVMShareDialog() != null)
			{
				//Close the KVM sharing request dialog.if opened
				if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_ALLOWED)
				{
					if(JViewerApp.getInstance().getKVMShareDialog().getKVMShareResponseDialog() != null)
						JViewerApp.getInstance().getKVMShareDialog().disposeKVMShareResponseDialog();
				}
			}

			JViewerApp.getInstance().OnVideoStopRedirection();
			JViewerApp.getInstance().getM_frame().windowClosed();

			if(m_pktHdr.status() == STOP_SESSION_CONF_CHANGE)
				JOptionPane.showMessageDialog( JViewerApp.getInstance().getRCView(),
						LocaleStrings.getString("2_3_KVMCLIENT"),
						LocaleStrings.getString("2_4_KVMCLIENT"),
						JOptionPane.ERROR_MESSAGE
						);
			else if(m_pktHdr.status() == STOP_SESSION_WEB_LOGOUT){
				if(JViewer.isjviewerapp()){
					JOptionPane.showMessageDialog( JViewerApp.getInstance().getRCView(),
							LocaleStrings.getString("2_5_KVMCLIENT") + JViewer.getTitle() +
							LocaleStrings.getString("2_22_KVMCLIENT"),
							LocaleStrings.getString("2_4_KVMCLIENT"),
							JOptionPane.ERROR_MESSAGE
							);
				}
			}
			else if(m_pktHdr.status() == STOP_SESSION_IMMEDIATE)
				JOptionPane.showMessageDialog( JViewerApp.getInstance().getRCView(),
						LocaleStrings.getString("2_6_KVMCLIENT"),
						LocaleStrings.getString("2_4_KVMCLIENT"),
						JOptionPane.ERROR_MESSAGE
						);
			else if(m_pktHdr.status() == STOP_SESSION_LICENSE_EXP)
				JOptionPane.showMessageDialog( JViewerApp.getInstance().getRCView(),
						LocaleStrings.getString("2_26_KVMCLIENT")+
						LocaleStrings.getString("2_6_KVMCLIENT"),
						LocaleStrings.getString("2_4_KVMCLIENT"),
						JOptionPane.ERROR_MESSAGE
						);
			else if(m_pktHdr.status() == STOP_SESSION_TIMED_OUT)
				JOptionPane.showMessageDialog( JViewerApp.getInstance().getRCView(),
						LocaleStrings.getString("2_27_KVMCLIENT"),
						LocaleStrings.getString("2_4_KVMCLIENT"),
						JOptionPane.ERROR_MESSAGE
						);
			else if(m_pktHdr.status() == STOP_SESSION_KVM_DSCONNECT)
				JOptionPane.showMessageDialog( JViewerApp.getInstance().getRCView(),
						LocaleStrings.getString("2_28_KVMCLIENT"),
						LocaleStrings.getString("2_4_KVMCLIENT"),
						JOptionPane.ERROR_MESSAGE
						);
			else if(m_pktHdr.status() == STOP_SESSION_WEBSERVER_RESTART){
				JOptionPane.showMessageDialog( JViewerApp.getInstance().getRCView(),
						LocaleStrings.getString("2_30_KVMCLIENT"),
						LocaleStrings.getString("2_4_KVMCLIENT"),
						JOptionPane.ERROR_MESSAGE
						);
			}
			else if(m_pktHdr.status() == STOP_SESSION_WARM_RESET){
				JOptionPane.showMessageDialog( JViewerApp.getInstance().getRCView(),
						LocaleStrings.getString("2_31_KVMCLIENT"),
						LocaleStrings.getString("2_4_KVMCLIENT"),
						JOptionPane.ERROR_MESSAGE
						);
			}
			else if(m_pktHdr.status() == STOP_SESSION_RESTORE_DEFAULT){
				JOptionPane.showMessageDialog( JViewerApp.getInstance().getRCView(),
						LocaleStrings.getString("2_32_KVMCLIENT"),
						LocaleStrings.getString("2_4_KVMCLIENT"),
						JOptionPane.ERROR_MESSAGE
						);
			}
			//Close the application.
			JViewer.exit(0);

			break;

		case IVTPPktHdr.IVTP_ENCRYPTION_STATUS:
			Debug.out.println("*** Encryption Status");
			JViewerApp.getInstance().OnEncryptionStatus();
			break;

		case IVTPPktHdr.IVTP_INITIAL_ENCRYPTION_STATUS:
			Debug.out.println("*** Initial Encryption Status");
			JViewerApp.getInstance().OnInitialEncryptionStatus();
			break;
		case IVTPPktHdr.IVTP_GET_USB_MOUSE_MODE:
			Debug.out.println("*** Get Mouse Mode Response");
			JViewerApp.getInstance().OnGetMouseMode(m_ctrlMsg.get());
			break;
		case IVTPPktHdr.IVTP_VALIDATE_VIDEO_SESSION_RESPONSE:
			Debug.out.println("*** ADVISER_VALIDATE_VIDEO_SESSION_RESPONSE:"+m_pktHdr.pktSize);
			byte response = m_ctrlMsg.get();
			byte sindex = -1;
			if(m_pktHdr.pktSize>1)
				sindex = m_ctrlMsg.get();
		
			JViewerApp.getInstance().OnValidateVideoSessionResp(response,sindex);
			break;
		case IVTPPktHdr.IVTP_GET_KEYBD_LED:
			if(!JViewer.isWebPreviewer() || !JViewer.isBSODViewer()) {
				byte status = m_ctrlMsg.get();
				//If the LED status is received when the JViewer has lost
				//focus, that status will be ignored. 
				if(!JViewerView.Lost_focus_flag){
					JViewerApp.getInstance().onKeybdLED(status);
					JViewerApp.getInstance().getM_wndFrame().getM_status().setKeyboardLEDStatus(status);
					JViewerApp.getInstance().getM_fsFrame().getM_menuBar().getLedStatusBar().setLEDStatus(status);
				}
			}
			break;
		case IVTPPktHdr.IVTP_SESSION_ACCEPTED:
			//The status field of the IVTP packet header will be set with the SOC ID of the BMC.
                        //Compare it against that of the JViewer, and if a mismatch is found, then quit the application.
			if((m_pktHdr.status > 0) && (m_pktHdr.status != JViewerApp.getInstance().getSoc_manager().getSOCID())){
				JViewerApp.getInstance().getM_frame().generalErrorMessage(LocaleStrings.getString("A_5_GLOBAL"),
						LocaleStrings.getString("2_33_KVMCLIENT")+JViewer.getSOC(m_pktHdr.status)+" "+LocaleStrings.getString("2_35_KVMCLIENT")+".\n"+
								LocaleStrings.getString("2_34_KVMCLIENT")+JViewerApp.getInstance().getSoc_manager().getSOC()+" "+LocaleStrings.getString("2_35_KVMCLIENT")+".");
				JViewer.exit(0);
			}
			// For displaying HID Initialization Dialog properly.
			// Flag will be updated again when KVM Privilege is received.
			KVMShareDialog.isMasterSession = true;
			if(JViewer.isWebPreviewer())
				JViewerApp.getInstance().onSendWebPreviewerSession();
			else{
				//Adviser will send Active sessions MAC Address List to detect second client from same machine.
				if(m_pktHdr.pktSize>0)
				{
					byte[] macBuffer = null;
					macBuffer =new byte[m_pktHdr.pktSize];
					m_ctrlMsg.get(macBuffer);
					String macAddresses = new String(macBuffer);
						if(!JViewerApp.getInstance().getRetryConnection())
						{
							if(null != macAddresses){
								int pos = 0;
								int macLen = IVTPPktHdr.CLINET_OWN_MAC_LENGTH -1;
								int entries = m_pktHdr.pktSize /macLen ;
								String[] mac =new String[entries];

								for(int index=0;index<entries;index++){
									pos = (index*macLen);
									mac[index] =macAddresses.substring(pos, (pos + macLen)).trim();
								}

								try {
									if(null == JViewerApp.getInstance().getMacAddress(JViewerApp.COMPARE_MAC_ADDRESS, mac))
									{
										//Second Client from same machine ,So close the session.
										JVFrame frame = JViewerApp.getInstance().getMainWindow();
										// Set m_redirection flag in KVMClient to true, so that the stop session
										// command can be send to the adviser.
										JViewerApp.getInstance().getKVMClient().setM_redirection(true);
										JViewerApp.getInstance().OnVideoStopRedirection();

										JOptionPane.showMessageDialog(frame,LocaleStrings.getString("D_30_JVAPP")+JViewer.getIp()+
												LocaleStrings.getString("D_31_JVAPP"),LocaleStrings.getString("D_32_JVAPP"),
												JOptionPane.INFORMATION_MESSAGE);
										JViewerApp.getInstance().OnVideoExit();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
				}

				JViewerApp.getInstance().OnsendWebsessionToken();
			}
			break;
		case IVTPPktHdr.IVTP_MAX_SESSION_CLOSING:
			JViewerApp.getInstance().OnVideoStopRedirection();
			if(m_pktHdr.status() == MAX_SESSION_REACHED)
			{
				JOptionPane.showMessageDialog( JViewerApp.getInstance().getRCView(),
						LocaleStrings.getString("2_11_KVMCLIENT"),
						LocaleStrings.getString("2_4_KVMCLIENT"),
						JOptionPane.ERROR_MESSAGE);
			}
			else if(m_pktHdr.status() == SAME_KVM_CLIENT_USER)
			{
				JOptionPane.showMessageDialog(JViewerApp.getInstance().getRCView(),
						LocaleStrings.getString("D_30_JVAPP")+JViewer.getIp()+
						LocaleStrings.getString("D_31_JVAPP"),LocaleStrings.getString("D_32_JVAPP"),
						JOptionPane.INFORMATION_MESSAGE);
			}
			if(JViewer.isStandalone())
	    		JViewer.exit(0);
	    	else
	    		JViewerApp.getInstance().getMainWindow().dispose();
			break;
		case IVTPPktHdr.IVTP_KVM_SOCKET_STATUS:
			JViewerApp.getInstance().onStopConcurrentSession();
			break;
		case IVTPPktHdr.IVTP_KVM_SHARING:
		case IVTPPktHdr.IVTP_SET_NEXT_MASTER:
			if(m_pktHdr.pktSize>0)
			{
				ByteBuffer userDataBuffer = null;
				byte[] otherUserNameBuffer = null;
				otherUserNameBuffer =new byte[USER_DATA_LENGTH ];
				m_ctrlMsg.get(otherUserNameBuffer);
				byte[] tempBuffer = new byte[UserDataPacket.USER_NAME_LENGTH];
				userDataBuffer = ByteBuffer.wrap(otherUserNameBuffer);
				userDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
				userDataBuffer.get(tempBuffer);
				KVMSharing.KVM_CLIENT_USERNAME = new String(tempBuffer).trim();
				tempBuffer = new byte[UserDataPacket.CLIENT_IP_LENGTH];
				userDataBuffer.get(tempBuffer);
				KVMSharing.KVM_CLIENT_IP = new String(tempBuffer).trim();
				KVMSharing.KVM_CLIENT_SESSION_INDEX = Integer.toString(userDataBuffer.get());
				KVMSharing.KVM_CLIENT_IPMI_PRIV = userDataBuffer.getInt();
				Debug.out.println(KVMSharing.KVM_CLIENT_USERNAME+" : "+KVMSharing.KVM_CLIENT_IP+" : "+KVMSharing.KVM_CLIENT_SESSION_INDEX+" :: "+KVMSharing.KVM_CLIENT_IPMI_PRIV);
			}

			if(m_pktHdr.type == IVTPPktHdr.IVTP_KVM_SHARING){
				JViewerApp.getInstance().OnKvmPrevilage(m_pktHdr.status);
			}
			else{
				JViewerApp.getInstance().onGetFullPermissionRequest(m_pktHdr.status);
			}
			//Update active user menu list
			JViewerApp.getInstance().getJVMenu().updateUserMenu();
			break;
		case IVTPPktHdr.IVTP_WEB_PREVIEWER_CAPTURE_STATUS:
			JViewerApp.getInstance().setWebPreviewerCaptureStatus(m_ctrlMsg.get());
			break;
		case IVTPPktHdr.IVTP_POWER_STATUS:
			if(JViewer.isjviewerapp()|| JViewer.isStandAloneApp()){
				byte pwrStatus = (byte)m_pktHdr.status();

				if(Host_OS_shutdown == false) 
				{
					Debug.out.println("POWER STATUS : "+pwrStatus);
					JViewerApp.getInstance().onGetPowerControlStatus(pwrStatus);
				}
				else
				{
					if(pwrStatus == 1) //host OS still power on, request to get power status again
					{
						try 
						{
							Thread.sleep(2000);
							sendPowerStatusRequest();
						} catch(Exception e) {
							Debug.out.println(e);
						}
					}
					else
					{
						JViewerApp.getInstance().onGetPowerControlStatus(pwrStatus);
						Host_OS_shutdown = false;
					}
				}
			}
			break;
		case IVTPPktHdr.IVTP_POWER_CONTROL_RESPONSE:
			byte pwrCtrlResponse = (byte) m_pktHdr.status();
			Debug.out.println("RESPONSE : "+pwrCtrlResponse);
			JViewerApp.getInstance().onPowerControlResponse(pwrCtrlResponse);
			break;
		case IVTPPktHdr.IVTP_CONF_SERVICE_STATUS:
			readConfServiceData();
			break;
		case IVTPPktHdr.IVTP_MOUSE_MEDIA_INFO:
			try{
				onreadmouse_media_count();
			}catch(Exception e){
				Debug.out.println(e);
			}
            break;
		case IVTPPktHdr.IVTP_GET_ACTIVE_CLIENTS:
			if(JViewer.isjviewerapp()|| JViewer.isStandAloneApp()){
				readUserData();
				JViewerApp.getInstance().getJVMenu().updateUserMenu();
				sendFullScreenRequest();
			}
			break;
		case IVTPPktHdr.ADVISER_GET_USER_MACRO:
			processUserMacroPacket(m_ctrlMsg);
			break;
		case IVTPPktHdr.IVTP_IPMI_RESPONSE_PKT:
			JViewerApp.getInstance().onGetIPMICommandResponse(m_ctrlMsg, m_pktHdr.status);
			break;
		case IVTPPktHdr.IVTP_DISPLAY_CONTROL_STATUS:
			JFrame frame = JViewer.getMainFrame();
			short data = m_pktHdr.status();
			if(data == hostLockStatus)
				break;
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK, false);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_HOST_DISPLAY_LOCK, false);
			if (data == JViewerApp.HOST_DISPLAY_UNLOCK)
			{
				//Host display is Unlocked now
				if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_ALLOWED)
					JViewerApp.getInstance().changeHostDisplayLockStatus(JViewerApp.HOST_DISPLAY_UNLOCK);
				else
					JViewerApp.getInstance().getM_wndFrame().getToolbar().turnOnHostDisplayButton(true);
				if (hostLockStatus > 0 && hostLockStatus != JViewerApp.HOST_DISPLAY_UNLOCKED_AND_DISABLED){
					InfoDialog.showDialog(frame, LocaleStrings.getString("2_23_KVMCLIENT"),
							LocaleStrings.getString("2_25_KVMCLIENT"),
							InfoDialog.INFORMATION_DIALOG);
				}
				setHostLockStatus(data);
			}
			else if (data == JViewerApp.HOST_DISPLAY_LOCK)
			{
				//Host display is locked now
				if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_ALLOWED)
					JViewerApp.getInstance().changeHostDisplayLockStatus(JViewerApp.HOST_DISPLAY_LOCK);
				else
					JViewerApp.getInstance().getM_wndFrame().getToolbar().turnOnHostDisplayButton(false);
				if (hostLockStatus >= 0 && hostLockStatus != JViewerApp.HOST_DISPLAY_LOCK &&
						hostLockStatus != JViewerApp.HOST_DISPLAY_LOCKED_AND_DISABLED){
					InfoDialog.showDialog(frame, LocaleStrings.getString("2_24_KVMCLIENT"),
							LocaleStrings.getString("2_25_KVMCLIENT"),
							InfoDialog.INFORMATION_DIALOG);
				}
				setHostLockStatus(data);
			}
			else if (data == JViewerApp.HOST_DISPLAY_UNLOCKED_AND_DISABLED)
			{
				JViewerApp.getInstance().changeHostDisplayLockStatus(JViewerApp.HOST_DISPLAY_UNLOCKED_AND_DISABLED);
				if (hostLockStatus > 0 && hostLockStatus != JViewerApp.HOST_DISPLAY_LOCKED_AND_DISABLED){
					InfoDialog.showDialog(frame, LocaleStrings.getString("2_29_KVMCLIENT"),
							LocaleStrings.getString("2_25_KVMCLIENT"),
							InfoDialog.INFORMATION_DIALOG);
				}
				setHostLockStatus(data);
			}
			else if (data == JViewerApp.HOST_DISPLAY_LOCKED_AND_DISABLED)
			{
				JViewerApp.getInstance().changeHostDisplayLockStatus(JViewerApp.HOST_DISPLAY_LOCKED_AND_DISABLED);
				if (hostLockStatus >= 0 && hostLockStatus != JViewerApp.HOST_DISPLAY_LOCK &&
						hostLockStatus != JViewerApp.HOST_DISPLAY_LOCKED_AND_DISABLED){
					InfoDialog.showDialog(frame, LocaleStrings.getString("2_24_KVMCLIENT"),
							LocaleStrings.getString("2_25_KVMCLIENT"),
							InfoDialog.INFORMATION_DIALOG);
				}
				setHostLockStatus(data);
			}
			break;
		case IVTPPktHdr.IVTP_MEDIA_LICENSE_STATUS:
			JViewerApp.getInstance().onMediaLicenseStatus((byte) m_pktHdr.status());
			break;
		case IVTPPktHdr.IVTP_MEDIA_FREE_INSTANCE_STATUS:
			getVMediaFreeInstanceStatus(m_ctrlMsg);
			break;
		case IVTPPktHdr.IVTP_KEEP_ALIVE_PKT:
			break;	
		case IVTPPktHdr.IVTP_CONNECTION_FAILED:
			JViewerApp.getInstance().onConnFailed();
			break;
		default:
			JViewerApp.getInstance().getSockvmclient().onSocControlMessage(m_pktHdr, m_ctrlMsg);

		}
	}
	public void sendFullScreenRequest(){
		if (!m_redirection)  {
			return;
		}  

		//request full screen here, with status 1 inorder to avoid updating last packet received time.
		//If last packet received time is updated at server side, kvm session timeout counter will restart causing invalid session timeout update.
		OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_GET_FULL_SCREEN,0, (short)1);
	}

	/**
	 * Redirection status
	 *
	 * @return redirection status
	 */
	public boolean redirection() {
		return m_redirection;
	}

	public  int OnFormIVTPHdr_Send(short packettype, int packetsize, short status){

		IVTPPktHdr ivtphdr = new IVTPPktHdr(packettype, packetsize, status);
		int ret = sendMessage(ivtphdr.array(), ivtphdr.size());
		if(ret != ivtphdr.size()) {
			return -1;
		}
		return ret;

	}

	/**
	 * Resume redirection
	 */
	public void resumeRedirection() {

		m_redirection = true;
		//m_pause = false;
		if (!m_redirection) return;
		// send resume video redirection command
		OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_RESUME_REDIRECTION, 0, (short)0);
		// For refreshing the window title (fps)
		m_vidClnt.refresh();
		sendLEDRequest();
	}
	/**
	 * stop video redirection cmd redirection
	 */
	public void Stop_Cmd_Redirection() {
		if (!m_redirection) return;
		// send stop video redirection command
		OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_STOP_SESSION_IMMEDIATE,0, (short)0);
	}


	/**
	 * Send LED Request to Server
	 */
	public void sendLEDRequest() {

		if (!m_redirection) return;

		// send resume video redirection command
		OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_GET_KEYBD_LED,0, (short)0);
	}
	/**
	 * Send power status request packet to server.
	 */
	public void sendPowerStatusRequest(){
		if (!m_redirection) 
			return;
		//send power control status packet
		OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_POWER_STATUS,0, (short)0);
	}
	/**
	 * Send power control command packet to server.
	 */
	public void sendPowerControlCommand(byte command){
		if(command == IVTPPktHdr.IVTP_POWER_CONTROL_SOFT_RESET ||
			command == IVTPPktHdr.IVTP_POWER_CONTROL_OFF_IMMEDIATE) {
			Host_OS_shutdown = true;
		}
		Debug.out.println("sendPowerControlCommand, command = " + command);
		OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_POWER_CONTROL_REQUEST,0, (short)command);
	}

	/**
	 * Start redirection
	 */
	public int startRedirection() {

		// check if redirection is active.
		if (m_redirection) return 0;

		if(JViewer.isSinglePortEnabled()){
			if(JViewer.isKVMReconnectEnabled())
			{
				if(JViewerApp.getInstance().getRetryConnection())
				{
					if(0 > JViewerApp.getInstance().getSinglePortKvm().startConnect())
					{
						return -1;
					}
				}
				JViewerApp.getInstance().setLocalport(JViewerApp.getInstance().getSinglePortKvm().getHttpsock().getLocalPort());
			}
			SetSocket(JViewerApp.getInstance().getSinglePortKvm().getHttpsock());

			String Kvm_own_ip = m_sock.getLocalAddress().toString();

			String[] split = Kvm_own_ip.split("/");
			KVMSharing.KVM_CLIENT_OWN_IP = split[1];
			
			// if the IP is 127.0.0.1 get the IP from InetAddress.
			if (KVMSharing.KVM_CLIENT_OWN_IP.compareToIgnoreCase("127.0.0.1") == 0) {
				try
				{
					KVMSharing.KVM_CLIENT_OWN_IP = InetAddress.getLocalHost().getHostAddress().toString();
				}
				catch(Exception e)
				{}
			}
			//If IP is "0.0.0.0" Check for IPV6 Ip
			if (KVMSharing.KVM_CLIENT_OWN_IP.compareToIgnoreCase("0.0.0.0") == 0) {
				KVMSharing.KVM_CLIENT_OWN_IP = getipv6IP();
			}
			Debug.out.println("SPKVM m_sock.socket().getLocalAddress()"
					+ Kvm_own_ip.toString());
			Debug.out.println("SPKVM m_sock.socket().getLocalAddress()"
					+ KVMSharing.KVM_CLIENT_OWN_IP);
		}
		else{
			if (-1 == connectVideo(m_bUseSSL)) {
				return -1;
			}
		}

		Debug.out.println("Video socket approval received");
    	// create network listener
  		m_NWListener = new NWListener(this);
  		m_NWThread = new Thread(m_NWListener, "listener");
  		m_NWListener.startListener();
  		m_NWThread.start();
		return 0;
	}



	public int OnValidVideoSession()
	{
		m_redirection = true;
		JViewerApp.getSoc_manager().getSOCApp().SOC_Session_validated();
		if(!JViewer.isWebPreviewer() && !JViewer.isBSODViewer()){
			sendPowerStatusRequest();// send power status request.
			onSendLockScreen(GET_HOST_LOCK_STATUS);
			OnGetUserMacro();
			Debug.out.println("KVM Redirection Started!");
		}
		return 0;

	}

	private void OnGetUserMacro() {
		sendGetUserMacro();
	}
	public int sendGetUserMacro()
	{
		IVTPPktHdr usermacro;
		usermacro = new IVTPPktHdr(IVTPPktHdr.ADVISER_GET_USER_MACRO, 0, (short)0);
		if( sendMessage(usermacro.array(), usermacro.size()) != usermacro.size() )
			return 1;
	    return 0;
	}
	/**
	 * Get the status of keyboard/mouse encryption.
	 *
	 * @return true if enabled, false otherwise.
	 */
	public boolean isKMEncryptionEnabled() {

		return m_encrypt;
	}

	/**
	 * Enable/disable keyboard/mouse encryption
	 * This is used to when the client receives a keyboard/mouse encryption
	 * notification message - encryption status / initial encryption status.
	 *
	 * @param flag true to enable and false to disable.
	 */
	public void notifyEncryption(boolean flag) {

		if (!m_redirection) return;
		m_encrypt = flag;
	}


	/**
	 * Stop redirection
	 */
	public void stopRedirection() {

		if (!m_redirection)
		{
			if(JViewerApp.getInstance().GetRedirectionState()!= JViewerApp.REDIR_STOPPING)
				return;
		}
		try {
			Thread.sleep(1000);
		} catch(Exception e) {
			Debug.out.println(e);
		}

		// close connection and destroy listener thread
       	VideoSocketclose();
        m_NWListener.destroyListener();
		m_redirection = false;
		try{
		m_vidClnt.reset();
		}catch(Exception e){
			Debug.out.println(e);
		}
		m_kmCrypt.close();
	}

	/**
	 * Pause redirection
	 */
	public void pauseRedirection() {

		if (!m_redirection) return;

		OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_PAUSE_REDIRECTION, 0, (short)0);
		m_vidClnt.reset();
		m_redirection = false;
	}


	/**
	 * Media Redirection started/stop redirection
	 */
	public void MediaRedirectionState(byte state) {

		if (!m_redirection) return;
		OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_MEDIA_STATE, 0, (short)state);
	}

	public int read_data(Socket sockch,byte[] read_buf)
	{
		 int    dwIndex = 0;
    	 int bytes_to_read = read_buf.length;
    	 int m_readIx = -1;

		 while(bytes_to_read != 0) {
			 try {
				 m_readIx = sockch.getInputStream().read(read_buf, dwIndex, bytes_to_read);
			} catch (IOException e) {
				Debug.out.println(e);
				if(JViewer.isKVMReconnectEnabled()&& (JViewer.isjviewerapp() || JViewer.isStandAloneApp()))
				{
					JViewerApp.getInstance().onReconnect();
				}
				return -1;
			}

		     if(m_readIx <= 0) {
		    	 //System.out.println("SOCKET FAILURE");
		    	 return m_readIx;
		     }
		     bytes_to_read  = bytes_to_read - m_readIx;
		     dwIndex = dwIndex +  m_readIx;
		 }
		 m_readIx = read_buf.length;
		 return m_readIx;
	}


	/**
	 * Send keyboard/mouse packet
	 *
	 * @param kmPkt keyboard/mouse packet
	 */
	public int sendKMMessage(USBMessage msg) {

	//Block keyboard and mouse packets from being sent to the adviser when server is powered OFF,
	//or when KVM client session is having partial permission alone.
		if(KVMSharing.KVM_REQ_GIVEN != KVMSharing.KVM_REQ_ALLOWED ||
				JViewerApp.getInstance().getPowerStatus() != JViewerApp.SERVER_POWER_ON){
			return -1;
		}

		if (m_redirection) {

			byte[] msgArray;
			if ((m_encrypt == true) && (m_kmCrypt.isInitialized())) {
				msgArray = msg.encryptedReport(m_kmCrypt);
			} else {

				msgArray = msg.report();
			}
			if(JViewerApp.getInstance().getM_USBKeyRep().KeyBoardDataNull) {
				JViewerApp.getInstance().getM_USBKeyRep().KeyBoardDataNull = false;
				return 0;	
			}
			if(msgArray == null)
				return 0;
			if (sendMessage(msgArray, msgArray.length) != msgArray.length) {
				Debug.out.println("Error sending USB Keyboard/Mouse packet");

				/*JOptionPane.showMessageDialog (
						null,
						"Error sending keyboard/mouse data! Please restart video redirection.",
						"Connection Failure",
						JOptionPane.ERROR_MESSAGE);*/
				//System.exit(0);
				return -1;
			}
		}

		return 0;
	}

	public int SendKVMPrevilage(byte kvmPrivilege, String userDetails)
	{
		// To send the status of the packet
		IVTPPktHdr KVMPrevilage;
		ByteBuffer userDataBuffer = null;
		int ret = -1;
		byte[] tempBuffer = null;
		int pos = -1;
		String userName = null;
		String clientIP = null;
		int sessionIndex;
		//short sample= (short) ((high_byte<<8)+low_byte) ;
		Debug.out.println("User2_status"+kvmPrivilege);
		short sendStatus = (short) (KVMSharing.STATUS_KVM_PRIV_REQ_MASTER+(kvmPrivilege<<8));
		Debug.out.println("send_status"+sendStatus);
		pos = userDetails.indexOf(':');
		userName = userDetails.substring(0, pos).trim();
		clientIP = userDetails.substring(pos+2, userDetails.indexOf(':', pos+2)).trim();
		sessionIndex = Integer.parseInt(userDetails.substring(userDetails.lastIndexOf(':')+1,
						userDetails.length()).trim());
		tempBuffer = new byte[UserDataPacket.USER_NAME_LENGTH];
		tempBuffer = userName.getBytes();
		userDataBuffer = ByteBuffer.allocate(USER_DATA_LENGTH);
		userDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		userDataBuffer.position(0);
		userDataBuffer.put(tempBuffer);
		for(pos = userDataBuffer.position(); pos < UserDataPacket.USER_NAME_LENGTH; pos++)
			userDataBuffer.put((byte) 0);
		tempBuffer = new byte[UserDataPacket.CLIENT_IP_LENGTH];
		
		tempBuffer = clientIP.getBytes();
		userDataBuffer.put(tempBuffer);
		for(pos = userDataBuffer.position(); pos < (UserDataPacket.USER_NAME_LENGTH +
				UserDataPacket.CLIENT_IP_LENGTH); pos++)
			userDataBuffer.put((byte) 0);
		userDataBuffer.put((byte) sessionIndex);
		userDataBuffer.rewind();
		userDataBuffer.position(0);
		KVMPrevilage = new IVTPPktHdr(IVTPPktHdr.IVTP_KVM_SHARING, userDataBuffer.limit(), (short)sendStatus);

		ByteBuffer reportBuffer = ByteBuffer.allocate(KVMPrevilage.size() + KVMPrevilage.pktSize());
		reportBuffer.position(0);
		reportBuffer.put(KVMPrevilage.array());
		reportBuffer.put(userDataBuffer.array());
		reportBuffer.position(0);
		byte[] report = new byte[reportBuffer.limit()];
		reportBuffer.get(report,0,reportBuffer.limit());
		ret =sendMessage(report, report.length);
		if (report.length != ret) {
			return -1;
		}
		return 0;

	}
	public int sendKVMFullPermissionRequest(){
		IVTPPktHdr fullPermissionReqHdr = new IVTPPktHdr(IVTPPktHdr.IVTP_SET_NEXT_MASTER, 0, (short) 0);
		if(sendMessage(fullPermissionReqHdr.array(), fullPermissionReqHdr.size()) !=
			fullPermissionReqHdr.size()){
			return -1;
		}
		return 0;
	}
	
	public int sendKeepAliveRequest(){
		IVTPPktHdr keepAliveReqHdr = new IVTPPktHdr(IVTPPktHdr.IVTP_KEEP_ALIVE_PKT, 0, (short) 0);
		try
		{
			if(sendMessage(keepAliveReqHdr.array(), keepAliveReqHdr.size()) !=
					keepAliveReqHdr.size()){
					return 0;//Returning 0 to prevent another reconnect request.
				}
			getSocket().getOutputStream().flush();
		}
		catch (IOException e)
		{
			Debug.out.println(e);
			return -1;
		}

		return 0;
	}

	public void checkReconnect()
	{
		if (!JViewerApp.getInstance().getRetryConnection())
		{
			m_redirection = false;
			JViewerApp.getInstance().setSessionLive(false);
			JViewerApp.getInstance().setRetryConnection(true);
			JViewerApp.getInstance().OnVideoStopRedirection();
		}
		//close vmedia dialog if opened 
		if(JViewerApp.getInstance().getVMDialog() != null){
			JViewerApp.getInstance().getVMDialog().showDialog(false);
		}
		//disable all the menus
		JViewerApp.getInstance().updateKVMMenuOnReconnect(false);
		/* Show Reconnecting Dialog to the User */
		InfoDialog.showDialog(JViewer.getMainFrame(), LocaleStrings.getString("D_64_JVAPP"),InfoDialog.UNDECORATED_DIALOG, JViewer.getRetryInterval());

		synchronized (JViewerApp.getInstance().getRetryCountSync()) {
			try {
				JViewerApp.getInstance().getRetryCountSync().wait(); // wait for first retry time interval to complete
				JViewerApp.getInstance().setCurrentRetryCount(JViewerApp.getInstance().getCurrentRetryCount() + 1);
			}
			catch (InterruptedException e) {
				Debug.out.println(e);
			}
		}
	}

	public int SendMouseMode(byte mouseMode)
	{
		// sending the mouse mode in status of the packet
		IVTPPktHdr MouseMode;
		Debug.out.println("MouseMode"+mouseMode);
		MouseMode = new IVTPPktHdr(IVTPPktHdr.IVTP_SET_MOUSE_MODE, 0, mouseMode);
		if( sendMessage(MouseMode.array(), MouseMode.size()) != MouseMode.size() )
			return 1;
		return 0;
}
	/**
	 * Set keyboard/mouse encryption
	 *
	 * @param set action enable/disable based on true/false.
	 */
	public void setEncryption(boolean state) {

		if (!m_redirection) return;
		if (state) {
			m_encrypt = true;
			OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_ENABLE_ENCRYPTION, 0, (short)0);
		} else {
			m_encrypt = false;
			DISABLE_ENCRPT_FLAG = true;
			OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_DISABLE_ENCRYPTION, 0, (short)0);
		}
	}


	/**
	 * Get keyboard/mouse crypt handler.
	 *
	 * @return keyboard/mouse crypt handler.
	 */
	public KMCrypt getCrypt() {

		return m_kmCrypt;
	}

	/**
	 * Set bandwidth
	 *
	 * @param bandwidth new bandwidth value
	 */
	public int setBandwidth(int bandwidth) {

		if (!m_redirection) return -1;

		CfgBandwidth cfgBw = new CfgBandwidth(bandwidth);
		if (cfgBw.size() != sendMessage(cfgBw.report(), cfgBw.size())) {

			Debug.out.println("Failed to send Config Bandwidth message");
			return -1;
		}
		return 0;
	}

	/**
	 * Send auto detect bandwidth packet
	 */
	public int autoDetect() {

		if (!m_redirection) return -1;

		AutoDetectBw ad = new AutoDetectBw(CfgBandwidth.BANDWIDTH_100MBPS);

		if (ad.size() != sendMessage(ad.report(), ad.size())) {
			Debug.out.println("Failed to send auto detect message");
			return -1;
		}

		return 0;
	}

	/**
	 * Start reading event handler
	 * This happens when bandwidth auto detect process started.
	 */
	public void onStartReading() {

		m_startTS = System.currentTimeMillis();
	}

	/**
	 * Stop reading event handler
	 * This happens when bandwidth auto detect process finished.
	 */
	public void onStopReading() {

		m_stopTS = System.currentTimeMillis();
		int bandwidth = CfgBandwidth.determineBandwidth(m_stopTS - m_startTS);
		Debug.out.println("Time " + (m_stopTS - m_startTS) + ", Bandwidth " + bandwidth);
		String bwMsg = "unknown";
		JViewerApp.getInstance().OnOptionsBandwidth(bandwidth);

		switch (bandwidth) {

			case CfgBandwidth.BANDWIDTH_100MBPS:
				bwMsg = "100mbps";
				break;
	
			case CfgBandwidth.BANDWIDTH_10MBPS:
				bwMsg = "10mbps";
				break;
	
			case CfgBandwidth.BANDWIDTH_1MBPS:
				bwMsg = "1mbps";
				break;
	
			case CfgBandwidth.BANDWIDTH_512KBPS:
				bwMsg = "512kbps";
				break;
	
			case CfgBandwidth.BANDWIDTH_256KBPS:
				bwMsg = "256kbps";
				break;
		}

		JViewerApp.getInstance().updateBandwidthMsg(bwMsg);
	}

	/***
	 * Get the Soc reader from the SocKVmclient
	 * @param s
	 * @return
	 */

	public KVMReader getSocReader(short s) {
		 return (KVMReader) JViewerApp.getInstance().getSockvmclient().getSOCReader(s);
	}

	public boolean isM_redirection() {
		return m_redirection;
	}

	public void setM_redirection(boolean m_redirection) {
		this.m_redirection = m_redirection;
	}

	/**
	 * Receive the user defined macro packet, and process the data.
	 * @param msg - The byte buffer contains user macro data.
	 */
	public void processUserMacroPacket(ByteBuffer msg) {
		if(JViewerApp.getInstance().getAddMacro() == null)
		{
			JViewerApp.getInstance().setAddMacro(new AddMacro(msg)) ;
		}
		else
		{
			JViewerApp.getInstance().getAddMacro().setMacroBuffer(msg);
		}
		JViewerApp.getInstance().getAddMacro().parseDataToMenu();
		//Initially menu will be disabled to avoid exception. once Hotkeys list pkt received from BMC enable the menu
		if(KVMSharing.KVM_REQ_GIVEN != KVMSharing.KVM_REQ_PARTIAL && !KVMShareDialog.isMasterSession)
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.KEYBOARD_ADD_HOTKEYS, true);

		return;
	}

	/**
	 * Send the user defined macro data to the adviser.
	 * @param keyEvents - the list of key codes, and key locations
	 * @return - 0 if success, -1 otherwise.
	 */
	public int sendUserMacroData(byte[] keyEvents)
	{
		IVTPPktHdr macropacket = new IVTPPktHdr( IVTPPktHdr.ADVISER_SET_USER_MACRO,
				AddMacro.SIZE_OF_MACRO, (short)0);
		ByteBuffer bf = ByteBuffer.allocate(macropacket.size() + macropacket.pktSize() );
		bf.position(0);
		bf.put(macropacket.array());
		
		bf.put(keyEvents);
		bf.position(0);
		byte[] report = new byte[bf.limit()];
		bf.get(report,0,bf.limit());
		if (report.length != sendMessage(report, report.length)) {
			return -1;
		}

		return 0;
	}
	/**
	 * Read the Service configuration data and create the ConfPkt object to store the conf data.
	 */
	public void readConfServiceData(){
		if(m_pktHdr.pktSize>0){
			byte[] confDataBuffer = new byte[ConfPkt.CONF_PKT_SIZE*ConfPkt.CONF_SERVICE_COUNT];
			m_ctrlMsg.get(confDataBuffer);
			int iteration = 0;
			if(confPacket == null)
			{
				confPacket = new ConfPkt[ConfPkt.CONF_SERVICE_COUNT];
				byte[] tempDataBuffer = new byte[ConfPkt.CONF_PKT_SIZE];
				for(int i= 0; i<ConfPkt.CONF_SERVICE_COUNT;i++){
					for(int j = 0;j<ConfPkt.CONF_PKT_SIZE; j++)
						tempDataBuffer[j] = confDataBuffer[j+iteration];

					confPacket[i]= new ConfPkt(tempDataBuffer);
					iteration += ConfPkt.CONF_PKT_SIZE;
				}
			}
			else{
				ConfPkt[] tempconfPacket = new ConfPkt[ConfPkt.CONF_SERVICE_COUNT];
				byte[] tempDataBuffer = new byte[ConfPkt.CONF_PKT_SIZE];
				for(int index= 0; index<ConfPkt.CONF_SERVICE_COUNT;index++){
					for(int j = 0;j<ConfPkt.CONF_PKT_SIZE; j++)
						tempDataBuffer[j] = confDataBuffer[j+iteration];

					tempconfPacket[index]= new ConfPkt(tempDataBuffer);
					compareConfData(tempconfPacket[index], index);
					iteration += ConfPkt.CONF_PKT_SIZE;
				}
			}
		}
	}

	/**
	 * Compare the new changes in Service configuration data already stored conf data and update the changes.
	 */
	public void compareConfData(ConfPkt compObj, int index){

		if(JViewer.isStandAloneApp() && compObj.getServiceName().equals("web"))
			return;// no need to update web service configuration changes for StandAloneApp.
		String errorString = "";
		if(compObj.getCurrentState() != confPacket[index].getCurrentState()){
			if(compObj.getCurrentState() == 0)
				errorString += "* "+LocaleStrings.getString("2_12_KVMCLIENT")+" : "+LocaleStrings.getString("2_13_KVMCLIENT")+"\n";
			else
				errorString += "* "+LocaleStrings.getString("2_12_KVMCLIENT")+" : "+LocaleStrings.getString("2_14_KVMCLIENT")+"\n";
			confPacket[index].setCurrentState(compObj.getCurrentState());
			onChangeState(compObj,compObj.getServiceName());
		}
		if(! compObj.getInterfaceName().equals(confPacket[index].getInterfaceName())){
			errorString += "* "+LocaleStrings.getString("2_15_KVMCLIENT")+"\n";
			confPacket[index].setInterfaceName(compObj.getInterfaceName().toCharArray());
		}
		if(compObj.getNonSecureAccessPort() != confPacket[index].getNonSecureAccessPort()){

			errorString += "* "+LocaleStrings.getString("2_16_KVMCLIENT")+"\n";
			confPacket[index].setNonSecureAccessPort(compObj.getNonSecureAccessPort());
			onChangeNonSecurePort(compObj,compObj.getServiceName());
		}
		if(compObj.getSecureAccessPort() != confPacket[index].getSecureAccessPort()){
			errorString += "* "+LocaleStrings.getString("2_17_KVMCLIENT")+"\n";
			confPacket[index].setSecureAccessPort(compObj.getSecureAccessPort());
			onChangeSecurePort(compObj,compObj.getServiceName());
		}
		if(compObj.getSessionInactivityTimeout() != confPacket[index].getSessionInactivityTimeout()){
			errorString += "* "+LocaleStrings.getString("2_18_KVMCLIENT")+"\n";
			confPacket[index].setSessionInactivityTimeout(compObj.getSessionInactivityTimeout());
		}
		if(compObj.getMaxAllowedSessions() != confPacket[index].getMaxAllowedSessions()){
			errorString += "* "+LocaleStrings.getString("2_19_KVMCLIENT")+"\n";
			confPacket[index].setMaxAllowedSessions(compObj.getMaxAllowedSessions());
		}
		if(compObj.getMaxSessionInactivityTimeout() != confPacket[index].getMaxSessionInactivityTimeout()){
			errorString += "* "+LocaleStrings.getString("2_20_KVMCLIENT")+"\n";
			confPacket[index].setMaxSessionInactivityTimeout(compObj.getMaxSessionInactivityTimeout());
		}
		if(compObj.getMinSessionInactivityTimeout() != confPacket[index].getMinSessionInactivityTimeout()){
			errorString += "* "+LocaleStrings.getString("2_21_KVMCLIENT")+"\n";
			confPacket[index].setMinSessionInactivityTimeout(compObj.getMinSessionInactivityTimeout());
		}
		if(! errorString.equals(null) && (errorString.length()!= 0))
			confPacket[index].showConfDataChangeMsg(errorString);

	}
	/**
	 * Set the new changes (Service configuration data)in conf data and update the changes.
	 */
	public void onChangeState(ConfPkt compObj,String serviceName ){

		if(serviceName.equals(ConfPkt.SERV_CD_MEDIA)){
			JViewer.setCD_State(compObj.getCurrentState());
		}
		if(serviceName.equals(ConfPkt.SERV_HD_MEDIA)){
			JViewer.setHD_State(compObj.getCurrentState());
		}
	}
	/**
	 * Set the new changes (onChangeNonSecureport)in conf data and update the changes.
	 */
	public void onChangeSecurePort(ConfPkt compObj,String serviceName ){

		if(!JViewerApp.getInstance().isM_bVMUseSSL())
			return;
		if(serviceName.compareTo(ConfPkt.SERV_CD_MEDIA)==0){
			JViewerApp.getInstance().setM_cdPort(compObj.getSecureAccessPort());
		}
		if(serviceName.compareTo(ConfPkt.SERV_HD_MEDIA)==0){
			JViewerApp.getInstance().setM_hdPort(compObj.getSecureAccessPort());
		}
		if(JViewerApp.getInstance().getVMDialog() != null)
			JViewerApp.getInstance().getVMDialog().setVisible(false);
	}
	public void onChangeNonSecurePort(ConfPkt compObj,String serviceName ){

		if(JViewerApp.getInstance().isM_bVMUseSSL())
			return;

		if(serviceName.compareTo(ConfPkt.SERV_CD_MEDIA)==0){
			JViewerApp.getInstance().setM_cdPort(compObj.getNonSecureAccessPort());
		}
		if(serviceName.compareTo(ConfPkt.SERV_HD_MEDIA)==0){
			JViewerApp.getInstance().setM_hdPort(compObj.getNonSecureAccessPort());
		}
		if(JViewerApp.getInstance().getVMDialog() != null)
			JViewerApp.getInstance().getVMDialog().setVisible(false);
	}
	
	private void onreadmouse_media_count() {

		boolean isMediaRunning = false;
		boolean changed = false;
		byte Mouse_media[] = new byte[m_pktHdr.pktSize()];
		m_ctrlMsg.get(Mouse_media);
		ByteBuffer dataBuffer = ByteBuffer.wrap(Mouse_media);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byte mouse_mode = 0;   /** Says whether to use Absolute mouse or relative mouse */
		int attach_cd = 0;                  /** Attach mode for CD */
		int attach_hd = 0;                  /** Attach mode for HD */
		int enable_boot_once = 0;           /** Enable boot once or not */
		int num_cd = 0;         /** Number of CD Instances */
		int num_hd = 0;               /** Number of HD Instances */
		int lmedia_enable = 0;        /** Enable the LMedia Feature */
		int powerSaveMode = 0;        /** Power COnsumption Feature status*/
		int kvm_cd = 0;
		int kvm_hd = 0;
		VMDialog vmDialog = JViewerApp.getInstance().getVMDialog();
		if(JViewer.isKVMReconnectEnabled())
		{
			JViewer.setRetryCount(dataBuffer.getInt());
			JViewer.setRetryInterval(dataBuffer.getInt());
		}
		mouse_mode =dataBuffer.get();
		hostLockFeatureStatus = dataBuffer.get();
		hostLockFeatureStatus = (byte) (hostLockFeatureStatus & ((byte)1 << 0));

		attach_cd =dataBuffer.getInt();
		attach_hd=dataBuffer.getInt();
		enable_boot_once=dataBuffer.getInt();
		num_cd=dataBuffer.getInt();
		num_hd=dataBuffer.getInt();
		lmedia_enable=dataBuffer.getInt();
		dataBuffer.getInt();	//rmedia skip
		dataBuffer.getInt();	//sd server skip
		powerSaveMode = dataBuffer.getInt();	//power consumption
		kvm_cd=dataBuffer.getInt();
		kvm_hd=dataBuffer.getInt();

		JViewer.setPowerSaveMode((byte) powerSaveMode);

		// compare getnum_CD agains num_cd
		if(JViewer.getNum_CD() != num_cd){
			changed = true;
		}
		// compare getnum_HD agains num_hd
		if(JViewer.getNum_HD() != num_hd){
			changed = true;
		}
		
		if(changed != true)
		{
			if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_KVM_MAX_DEVICE_COUNT) == 
				JViewerApp.OEM_KVM_MAX_DEVICE_COUNT)
			{
				// compare getKVM_Num_CD agains kvm_cd
				if(JViewer.getKVM_Num_CD() != kvm_cd){
					changed = true;
				}
				// compare getKVM_Num_HD agains kvm_hd				
				if(JViewer.getKVM_Num_HD() != kvm_hd){
					changed = true;
				}
			}
		}
		if(JViewerApp.getInstance().getRCView().GetUSBMouseMode() != mouse_mode){
			JViewerApp.getInstance().getRCView().removeKMListener();
			JViewerApp.getInstance().getRCView().USBsyncCursor(false);
			JViewerApp.getInstance().OnGetMouseMode((byte)mouse_mode);
		}
		else if(hostLockFeatureStatus == HOST_LOCK_FEATURE_ENABLED){
			JViewerApp.getInstance().changeHostDisplayLockStatus(hostLockStatus);
		}
		else if(hostLockFeatureStatus != HOST_LOCK_FEATURE_ENABLED){
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK, false);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_HOST_DISPLAY_LOCK, false);
		}
		/*This packet will be received either in case of mouse mode change, host lock feature state change,
		 *  or in case of VMedia configuration change. If mouse mode, VMedia instance count, and host lock
		 *  feature state change, has not been changed, then some other configuration related to VMedia must have been
		 *  changed. So if VMedia redirection is in progress, set changed to true, for showing the information message.
		 */
		else if(!changed){
			isMediaRunning = JViewerApp.getInstance().syncVMediaRedirection();
			if(isMediaRunning)
				changed = true;
		}
		if(changed){
			isMediaRunning = JViewerApp.getInstance().syncVMediaRedirection();
			JFrame parent = JViewer.getMainFrame();
			if(JViewerApp.getInstance().getVMDialog() != null && 
					JViewerApp.getInstance().getVMDialog().isShowing()){
				InfoDialog.showDialog(parent, LocaleStrings.getString("2_7_KVMCLIENT"),
						LocaleStrings.getString("2_10_KVMCLIENT"), 
						InfoDialog.INFORMATION_DIALOG);

				if(isMediaRunning)
					JViewerApp.getInstance().getM_frame().stopVMediaRedirection(null);
			}
			else{
				if(isMediaRunning){
					InfoDialog.showDialog(parent, LocaleStrings.getString("2_7_KVMCLIENT")+
							LocaleStrings.getString("2_9_KVMCLIENT"),
							LocaleStrings.getString("2_10_KVMCLIENT"),
							InfoDialog.INFORMATION_DIALOG);
					JViewerApp.getInstance().getM_frame().stopVMediaRedirection(null);
				}
				else
					InfoDialog.showDialog(parent, LocaleStrings.getString("2_7_KVMCLIENT"),
							LocaleStrings.getString("2_10_KVMCLIENT"),
							InfoDialog.INFORMATION_DIALOG);
			}

			//Set free device count as num of device instances. This is required in case
			//that free device instance packet is not received. This will get updated to
			//the proper value if the free device instance packet is received afterwards.
			if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_KVM_MAX_DEVICE_COUNT) == 
				JViewerApp.OEM_KVM_MAX_DEVICE_COUNT)
			{
				JViewer.setKVM_Num_CD((byte) kvm_cd);
				JViewer.setKVM_Num_HD((byte) kvm_hd);
				JViewerApp.getInstance().setFreeCDNum(kvm_cd);
				JViewerApp.getInstance().setFreeHDNum(kvm_hd);
			}
			else{
				JViewer.setNum_CD((byte) num_cd);
				JViewer.setNum_HD((byte) num_hd);
				JViewerApp.getInstance().setFreeCDNum(num_cd);
				JViewerApp.getInstance().setFreeHDNum(num_hd);
			}

			if(vmDialog != null )
				vmDialog.reInitialize();
			VMApp.getInstance().getIUSBRedirSession().updateCDToolbarButtonStatus(false);
			VMApp.getInstance().getIUSBRedirSession().updateHDToolbarButtonStatus(false);

			JViewerApp.getInstance().updateFreeDeviceStatus();
		}
	}
/**
 * Read the user data (userid and ip address) of active clients logged on to the BMC.  
 */
 public void readUserData(){
		if(m_pktHdr.pktSize>0)
		{
			byte[] dataBuffer = null;
			dataBuffer =new byte[m_pktHdr.pktSize];
			m_ctrlMsg.get(dataBuffer);
			ByteBuffer dataBUF = ByteBuffer.wrap(dataBuffer);
			dataBUF.order(ByteOrder.LITTLE_ENDIAN);
			userDataPacket = new UserDataPacket(dataBUF);
			userData = userDataPacket.getUserInfo();
			numUsers = userDataPacket.getNumUsers();
		}
	}
 
	 /**
	  * @return the userDataPacket
	  */
	 public UserDataPacket getUserDataPacket() {
		 return userDataPacket;
	 }

	/**
	 * @return the numUsers
	 */
	public static int getNumUsers() {
		return numUsers;
	}

	/**
	 * @return the userData
	 */
	public static String[] getUserData() {
		return userData;
	}

	/**
	 * Send the next Master sessions information to the adviser
	 * @param masterDataBuffer - data byte buffer that containd teh next master info
	 * @return 0 if success -1 otherwise
	 */
	public int sendNextMasterInfo(ByteBuffer masterDataBuffer)
	{
		int ret = -1;
		IVTPPktHdr masterPacket = new IVTPPktHdr( IVTPPktHdr.IVTP_SET_NEXT_MASTER,masterDataBuffer.limit() , (short)0);
		ByteBuffer reportBuffer = ByteBuffer.allocate(masterPacket.size() + masterPacket.pktSize());
		reportBuffer.position(0);
		reportBuffer.put(masterPacket.array());
		reportBuffer.put(masterDataBuffer.array());
		reportBuffer.position(0);
		byte[] report = new byte[reportBuffer.limit()];
		reportBuffer.get(report,0,reportBuffer.limit());
		
		ret =sendMessage(report, report.length);
		if (report.length != ret) {
			return -1;
		}
		return 0;
	}
	/**
	 *
	 * This method is used to send the Host display status
	 * @param state
	 * @return
	 */
	public int onSendLockScreen(byte state)
	{
		byte LockStatus = 0;
		LockStatus = state;
			
		IVTPPktHdr LockScreenHdr  = new IVTPPktHdr(IVTPPktHdr.IVTP_DISPLAY_LOCK_SET, 1, (short)0);
	
		ByteBuffer lockbuff = ByteBuffer.allocate(LockScreenHdr.size() + 1);

		lockbuff.position(0);
		lockbuff.put(LockScreenHdr.array());
		lockbuff.put(LockStatus);
		lockbuff.position(0);
		
		byte[] lockreport = new byte[lockbuff.limit()];
		lockbuff.get(lockreport,0,lockbuff.limit());
		if (lockreport.length != sendMessage(lockreport,
				lockreport.length)) 
		{
			Debug.out.println("Failed to send the lockstatus to the card");
			return -1;
		}
		
		return 0;
	}

	/**
	 * @return the hostlockStatus
	 */
	public short getHostLockStatus() {
		return hostLockStatus;
	}

	/**
	 * @param hostlockStatus the hostlockStatus to set
	 */
	public void setHostLockStatus(short hostlockStatus) {
		this.hostLockStatus = hostlockStatus;
	}

	/**
	 * @return the hostLockFeatureStatus
	 */
	public byte getHostLockFeatureStatus() {
		return hostLockFeatureStatus;
	}

	/**
	 * @return the stopSignalRecieved
	 */
	public boolean isStopSignalRecieved() {
		return stopSignalReceived;
	}
	/**
	 * @SendKeyBoardLang : This method is used to send the keyboard language
	 * @param keyboard language
	 * @return
	 */
	public int sendKeyBoardLang(){
		String lang = JViewer.getKeyboardLayout();
		Debug.out.println("KBD lang"+lang);

		IVTPPktHdr langHdr  = new IVTPPktHdr(IVTPPktHdr.IVTP_SET_KBD_LANG,lang.length(), (short)0);
		ByteBuffer language = ByteBuffer.allocate(langHdr.size()+langHdr.pktSize());

		language.order(ByteOrder.LITTLE_ENDIAN);
		language.position(0);
		language.put(langHdr.array());
		language.put(lang.getBytes());
		language.position(0);

		byte[] report = new byte[language.limit()];
		language.get(report,0,language.limit());
		if (report.length != sendMessage(report,report.length)) 
		{
			System.out.println("Failed to send the keyboard language to the card");
			return -1;
		}
		return 0;
	}
	
	/**
	 * Get the number of free instances in the VMedia dialog, for each device.
	 * @param dataBuffer - packet data
	 */
	private void getVMediaFreeInstanceStatus(ByteBuffer dataBuffer){
		int numFreeCD = 0;
		int numFreeHD = 0;

		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		numFreeCD = dataBuffer.getInt();
		numFreeHD = dataBuffer.getInt();

		JViewerApp.getInstance().setFreeCDNum(numFreeCD);
		JViewerApp.getInstance().setFreeHDNum(numFreeHD);
		JViewerApp.getInstance().updateFreeDeviceStatus();
		
	}

	public void setSocketTimeout()
	{
		if(JViewer.isKVMReconnectEnabled())
		{
			try {
				getSocket().setSoTimeout(TIMEOUT_VALUE);
			}
			catch (SocketException e) 
			{
				// TODO Auto-generated catch block
				Debug.out.println(e);
			}
		}
	}

	public boolean isHost_OS_shutdown() {
		return Host_OS_shutdown;
	}

	public void setHost_OS_shutdown(boolean host_OS_shutdown) {
		Host_OS_shutdown = host_OS_shutdown;
	}

	/*
	 *Send terminate IVTP command to video server  
	 */
	public int SendKillSession(byte sessType, int sessIndex){
		int ret = 0;
		ByteBuffer userDataBuffer = null;
		IVTPPktHdr killSession = null;
		
		userDataBuffer = ByteBuffer.allocate(TERMINATE_SESSION_PKT_SIZE);
		userDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		userDataBuffer.position(0);
		userDataBuffer.put((byte)sessType); //Session type KVM(value 5)
		userDataBuffer.putInt(sessIndex);
		userDataBuffer.rewind();
		userDataBuffer.position(0);
		killSession = new IVTPPktHdr(IVTPPktHdr.IVTP_KVM_DISCONNECT, userDataBuffer.limit(), (short)0);

		ByteBuffer reportBuffer = ByteBuffer.allocate(killSession.size() + killSession.pktSize());
		reportBuffer.position(0);
		reportBuffer.put(killSession.array());
		reportBuffer.put(userDataBuffer.array());
		reportBuffer.position(0);
		byte[] report = new byte[reportBuffer.limit()];
		reportBuffer.get(report,0,reportBuffer.limit());

		ret =sendMessage(report, report.length);
		if (report.length != ret) {
			return -1;
		}
		return ret;
	}
}
