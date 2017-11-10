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

/**
 2.0 - Initial version.

 2.0.1
 	#Updated the library from SP2 to package
 2.0.2
 	#setautokeybreak mode is true
 2.0.3
 	#Image display path is not editable in VMedia box
 	#Autodetect Menu item is selected disabled the Softkeyboard
 1.0.7
 	#Modidfied the vrsion no to package no
 1.0.7
 	#Added the ISOImage and Floppy Image path reminder in the Vmedia dilog
 	#Modified the Status bar alignment to Box layout,if the screen size changes the LED state should be displayed properly
 1.0.9
   # Bug Fix : ID's 12520,12514,12455,12454,11462,12454 ,12091 and Refresh option enabled in Fullscreen menu.
 1.0.10
   # Bug Fix :Id's 12470 and 12472
 1.0.11
   # Buf Fix : ID's 12661 ,12879 and  12844
 1.0.15
   # Bug fix : Added AMI logo in Help,Alt+Gr issue
 1.0.16
  # LIBHARDDISK Library  source files renamed
 1.0.17
   # Removed unwanted file in res folder
 1.0.18
   # Bug Fix for Auto detect (Bug ID :13162)
1.0.20
	#Moved the Compression menu to SOC 
1.0.23
	#Both Shift key pressed with some other key,release event not receiving in the keylistennner so modifier flag not reset
1.0.24
	#Added the setharddisk command no in macro as 0xf4 
	#while disconnecting harddisk set the state to notconnected in the VMedia dialogs
1.0.25
	# Num,Caps,Scroll options do not syn with host machine.
	# HardDisk/USB key emulation-Select USB or hard Disk-Virtual media window buttons gets grayed out  
	# The device name shown on the multiple Hard disk/USB Key Redirecton dialogue is not correct  
	# Add one space between Hard and disk  
	# Press the windows keys using client keyboard invoke windows menu popup in both host and client windows.  
	# Improper message displayed for redirection already in use  
	# Media encryption-enabled by one client but the other client dont know when the console is opened
1.0.26
	#ContextMenu added  
1.0.27
	# Zoom Feaatue added
	#Video Recording
	#Macros Added
1.0.28
    # Bug fixes in Mouse Acceleration option.
 1.0.29
 	#Bug fixes:
 	1. Ctrl+ESC issue
 	2. Code cleanup in GEtframe and setframe
 	3.IP is not displayed on the Console
 1.0.32
 	#Moved the SOC command to the SOC package
	#Mouse Drag issue in relative mode
 1.0.34
 	#Added Multiple instance CD/FD?HD support
 	#Zoom In and out name Modified
 1.0.35
 	#removed the thread  to find the removed physical drive for HDredirection 
 1.0.36
 	#Issue fixed detecting USB Harddisk
 1.0.37
 	#Isue fixed display the redirected Fixed harddisk
 1.0.41
 	#Bug fixes:
 	1.NUM,CAPS,SCROLL lock LED blink issue
 	2.Mouse Calibration menu disable when pause redirection	
 1.0.42
	# Bug Fixes:
	# Add one space between Hard and disk 
	# In Java console, when clicking the Ctrl+Alt+Del in the bottom righ tof console, keyboard gets hanged. 
	# When Java console is minimized, the menus shrink and are not readable and accessible. 
	# In Java console,when we click settings under Video recording, the popup title needs to be edited. 
	# Shotcut key Alt - k not working for turn off Host display
	# Modified the Gettable panel to getjpanel for VMedia dialog to be displayed in SPX MultiViewer
1.3.0
	#Bug Fixes:
	1.When "Pause Redirection" is selected in the Java console window, it can't mount cd/dvd media successfully.
	2.when redirection is paused, and Auto detect in bandwidth is given it doesnt work even after redirection is resumed.
	3.In Java console,when we copy and paste files the mouse doesn't sync and unable to access the console thereafter
1.4.0
	#Bug Fix
	Stress Keyboard /mouse encryption in console - multiple sesssion are running is displayed
1.5.0
	#Bug Fixes
	1.Mouse calibration is not stable after changing the resolution in linux
	2.ALT+F did not work for more than 2 times while pressing continously
1.6.0
	#Bug Fixes:
	1. If Media 1 is redirected - Media 2 should be disabled in the virtual media wizard
	2.When Java console is minimized, the menus shrink and are not readable and accessible.
	3.Short cut Zoom option not able to use when we launch java console
	4.Shortcut option-Display a softkeyboard option was not displaying properly
1.6.1
	#Bug Fixes: Updating the user messages appropriately based on firmware conditions
1.8.
	#Modified  the Harddisk reader Opendevice return value
1.11.0
	# KB/Mouse Encryption Fail
1.13.0
	#  Alt + slash option alone not working in Dutch(Belgium) keyboard 
	#  In Java console,the Redirection icons representation does not vary when they are enabled and disabled. 
1.14.0
	#IDE harddisk in linux not finding,hard coded /dev/sd devices added /dev/hd also
1.15.0
	#  In Jviewer the prompt message on clicking Connect CD/DVD needs to be edited for spell check. 
	#  When Java console is open and virtual media configurations are reset, the error message needs to be edited. 
1.16.0
	#unable to access virtual media wizard in java console after connect and Disconnect the devices
	#USB Key-Reading Bytes still shows even we unplug USB key
	#File path for Video Record can be accepted
	#cosmetic issue in video record settings
	#Icon text is not updated for mouse show cursor
1.20.0
	#Modified the mouse sent event to the host reducing the timer thread polling
1.21.0
	#Japanes keyboard Issue Fixed
1.23.0
	#Video rcording Issue   fixed
1.24.0
	#Video recording Issue for window close event
1.25.0
	#ALT+N is not working in full screen mode.
	#shift + key input sends key information twice	
1.26.0	
	#Pause and play redirection button is not working in Java console.
1.30.0
	#Added the Fullscreen concurrent session information
1.31.0
	#Multiple instance CD/FD/HD redirection is disconnect,not updating the VMEdia dialog
1.32.0
	#Jviewer will work as JViewer application,Video player and video downloader.
1.34.0
	#JViewer auto-keyboard layout issue in Mac-OS cilent fixed.
	#Hebrew softkeyboard layout modified.
	#Error message shown when two sessions launched from same client to same host corrected. 
1.35.0
	#Host mouse cursor, client mouse cursor and JViewer draw cursor sychronized for RHEL
	#Fullscreen menubar for Mac OS client modified.
	#JVToolbar Pause button gaining focus on clicking JViewerView issue solved. 
1.38.0
	#CD eject in the host and supported Image eject support
	#Mouse sync issue
1.39.0
	#Mouse caliberation issue fixed
1.40.0
	#Invalid mouse mode pop-up fixed
	#KVM-sharing timer counter reset problem
1.41.0
	#Send Mouse button status for drag
	#update the Toolbars CD/Floppy/HD Icon for Image redirection 
1.43.0
	# Fixed BufferOverFlowException issue for relative mouse mode.	   
1.47.0
	#disabling menu's till first user allows access to second user	
1.48.0
	#Server Power control Options added
1.49.0	
	#Video recording stop on host reboot issue fixed.
	#Recorded AVI file is palyable in VLC media player also.
	#Video Download app file chooser dialog can't be closed issue solved.
1.51.0
	#Media redirection and Power Control options for KVM session with "Video Only" privilege, disabled.
	#Mouse calibration operation is not allowed if zoom value is not equal to 100%.
	#Input to Video Length textfield of Video record settings dialog limited to 4 characters. 
1.52.0
	#Service configuration change in the web to reflect in jviewer 
	#Parameter parsing added for each argument need to specify key value
	#mouse mode change & cd,fd&hd count updated at runtime
1.53.0
	# Exit menu disables while pausing redirection issue fixed
	# Service configuration changes for vmedia handled using thread synchronization.
	# Moved the mouse mode change message dialog into JViewerApp.
1.56.0
	#CD/FD/HD/Adviserd stop and start is fixed if the service is active and inactive
	#setting the adviser KVM session count in the NCML
	#SYnc the error messages if the servie s co
1.57.0
	#Activ client menu added	
1.58.0
	#Active client Images added
	#if the user list display two entry the current user indication green dot shows for both the user in the toolbar
	#In MAC Client Fullscreen MEnubar properly arranged 
1.59.0
	#Notiy thread for recording video if the blank screen comes from the adviser
1.60.0
	#Added the interface method and member to disable the SOC menu if the User given partial permission
1.61.0
	#Added the Imagepath combo fox-replaced the existing imagepath is already found
	#MenuItem and Toolbar icons disabled if the Host is off
1.62.0
	#Service Config packet size change modification
1.66.0
	#Video Record Settings menu gets enabled when host resolution changes, while video recording is in progress
	 issue fixed.
	#Power On server menu gets enabled on video pause and resume issue fixed.
	#Mouse mode change information dialog not shown when the mode is changed for the first time issue fixed. 
1.67.0
	#Modifier key status in JViewer invalid aftert host reboot, issue fixed. 
1.68.0
    #Dynamic load OEM logo and copyright string from web. when About dialog is shown.
1.69.0
	#Video Recorder App recording blank screen during Auto Video Record, issue fixed.
	#Included softawre keyboard support for Turkish-F and Turkish-Q keyborad language layouts.
1.70.0
	#System Time Monitor thread added to varify whether client system time has been 
	cahnged, to avoid abnormal conditions in JViewer.
	#Video Recording:Length of recorded video differs when video record duration is 
	given under 1 minute(60 second), issue fixed.
1.72.0
	# Server Power control icon is functioning the in client2 during 2nd client 
	requesting permission from 1st client, issue fixed
	# Message dialog shown stating that Device redirected in read only mode, when
	 CD-media is redirected.
	# No popup message for changing the mouse mode from Relative to Absolute mode,
	when ther is no video redirection(blank screen), issue fixed.
	# Included needed changes for Service Configuration Pkt size change in libncml.
1.73.0
	# Power status in JViewer updated immediately after Server Power ON or
	Server Power OFF event.
	# Trying to launch a third KVM session, after launching 2 sessions in same client,
	leads to Maximum no: of session reached message to be displayed, issue fixed.
	# Host not waking up from sleep mode if mouse is moved in JViewer, issue fixed.
	# Handled Thread.sleep() time deviation problem by setting time thresholds.
1.74.0
	# Java preview option shows connection failed status when host video is in
	sleep mode, issue fixed
	# When we Pause Console 1 and launch Console 2, the permission given by
	console 1 doesnt reflect in console 2, issue fixed.
1.75.0
	# User defined macros can be added to Keyboard menu and toolbar hotkey button popup. 
1.76.0
	# Other mouse mode added to JViewer.
1.78.0
	# Other mouse mode mouse movement made smoother.
1.79.0
	# JViewer could be launched as a Stand Alone App. 
1.81.0
	# Language localization(Multi-langugae support) implemented in JViewer.
1.82.0
	# Added Language localization support (Multi-langugae support) for StandAloneApp.
1.83.0
	# Video Recorder App recording only a blank screen, when the video dump file on
	BMC has only a single frame (mostly No Signal frame); issue fixed.
1.84.0
	#MouseCalibration implementation breaks in fullscreen mode; issue fixed.
	# Web Privewer not updating properly while SLES 11.0 installation with host resolution
	1600 X 1200. Issue fixed.
1.85.0
	# Client machine IP is displayed as 0.0.0.0 when console session is launched with ipv6
	address, issue fixed.
1.86.0
	# Soft keyboard is not working in JViewer when keyboard/mouse encryption is enabled and
	KVM sharing is initiated.
1.88.0
	# Stop command send to adviser before pop-up message appears in JViewer when adviser 
	restarts to clear session properly.
1.89.0
	# Show cursor fuctionality in JViewer disabled when the zoom value varies form 100%
	in Relative mouse mode.
1.91.0
	# Stand Alone Application is not able to launch using valid IPV6 address; issue fixed.
1.92.0
	# The cache of the files browsed in Virtual Media diaolg doesn't store the latest one if more than 6 files were browsed; issue fixed.
1.93.0
	# User Deifned Macro : Mac Meta key issue and Alt+F4 issue fixed.
1.94.0
	# Mouse pointer is not visible in java console, when the mouse hovers over menu items, in Mac client with Relative mouse mode; issue fixed.
1.95.0
	# Video recorded using Video Recording feature in JViewer is not able to be played
	 using VLC Media player; isseu fixed. 
1.96.0
	#Single Port KVM support
1.97.0
	# VMedia menu and toolbar buttons enabled even if host is powered off.
1.98.0
	# Added apptype macro for WebPreviewer.
1.99.0
	# All the message dialogs that block JViewer control flow are replaced with InfoDialogs
1.100.0
	#BSODVIewer applet implemenattion.
1.101.0
	#modified session token length to 128 for MAXtoken len changed in server side
1.102.0
	French launguage localization support added.
1.105.0
	 Virtual media unable open when repeat connect/disconnect image, issue fixed.
1.106.0
	Directory name and file name used to store recorded video file modified.
1.107.0
	Changed power off command send from power status button as Immediate Power Off, instead of Orederly Power Off.
1.108.0
	Mouse cursor automatically moves to the upper left corner in relative mode issue fixed.
1.109.0
	LED status bar implemented for JViewer in full screen mode.
1.110.0
	Corrected the misspelled words in various user information messages.
1.111.0
	The mouse cursor won't resync at the initial position, after using the scroll bars to scroll the view; issue fixed
1.112.0
	#Single Port KVM support for HTTPS	
1.113.0
	The semaphor lock on video dump file in Auto video recording , is released while closing the video player or recorder application.
1.114.0
	#StandAlone changes for HTTPS Session when singleport Feature Enabled.
1.115.0
	Unable to launch jviewer using java version 7; issue fixed.
1.116.0
	Unable to view the virtual media wizard when the user changes the virtual media devices configuration; issue fixed
1.117.0
	# The supported localization languages are not listed in language selection combo box in Stand Alone application connection dialog; isseu fixed.
	# User is given provision to close Stand Alone application connection dialog while connecting.
1.119.0
	# websessin cookie parsing modified to avoid BMC_IP_ADDR data with session cookie
1.120.0
	#Client Physical Keyboard Language Layout Support
1.121.0
	Deployment of JViewer Stand Alone application as a single jar file.
1.22.0
	#GUI Language localization menu
1.23.0
	Stand Alone Application moidifed as a configurable feature in PRJ file.
1.124.0
	Unable to add Print Screen macro and, unable to prevent dupliacte entries, in User Defined Macro feature; issue fixed
1.125.0
	Capture JViewer screen feature implemented.
1.126.0
	JPEG file filter added for Save File dialog in Capture Screen feature.
1.130.0
	#Player and download application as standalone application	
	#Multiple video files download from BMC
1.131.0
	Added new zoom options to the Zoom menu.
1.132.0
	Added download button for BSOD screen download
1.134.0
	Added support for sending IPMI raw commands to BMC
1.135.0
	Moved BSOD related soc function into soc package
1.136.0
	Enable/Disble KVM and Vmedia redirection based on KVM privilege argument.
1.137.0
	Added condition to avoid Webpreviewer and BSODApp from recievinf power status nad active user count
1.139.0
	Semaphore lock on the video file removed once the video fiel is downloaded using Video Player or Video Download StandAloneApp. 
1.141.0
	Multi-language support: corrections made for some of the strings - incorrect, missing or misspelled translations.
1.142.0
	Webpreviewer screen doesn't display the message fully when the host is in sleep mode; issue solved.
1.143.0
	# In the java console all the 3 zoom options are enabled when the client and host are in same resolution; issue fixed
	# Localization language menu items in GUI Localization option gets disabled in some scenarios; issue fixed.
	# Setting zoom options to Actual Size while switching back to window mode from fullscreen mode.
1.144.0
	Allowing CTRL and ALT key combination to be send to host via Physical keboard itself.
1.145.0
	#After the web session logout the jviewer launches with the blank screen(singleport app enabled).
1.146.0
	Added completion code to the response when an Invalid IPMI command is send to the BMC.
1.147.0
	# Info dialog message for Service Configuration changes being blocked by VMedia and Video Record Settings dialog; issue fixed.
	# Proper message has been added when trying floppy redirection and floppy srevice has been disabled.
	# Keybord shortcut mnemonic added fro Active Users menu.
1.148.0
	Added Full Keyboard Support Option.
1.149.0
	BSOD: Download button is scrambled if user scrolls BSOD page; issue fixed
1.151.0
	#Client machine IP is displayed as 0.0.0.0 when console session is launched with ipv6
	address, issue fixed.when singleport feature is enabled.
1.152.0
	#Manage video in standalone java:Error message displays when the user closes the video player.
1.153.0
	In java console extra frame appears when we use remote desktop connection from another client; issue fixed
1.154.0
	Localization faliure causes JViewer to crash; issue fixed
1.155.0
	In English Client to German Host Physical keyboard layout translation, Numpad .(dot) triggers ,(comma) in the host; issue fixed
1.156.0
	#download and player app hangs some times when playing/downloading videofile
1.157.0
	#Message dialog is dispalyed when the cuncurrent session is closed and full permission is received by a session with partial permission.
	#Minimum size set for the Video record settings dialog
	#Handled MissingResourceException while localizing SOC menus 
1.161.0
	#JViewer hangs when trying to close teh application, after a video storage location out of space error occurs, while recording; issue fixed.
1.163.0
	# Title argument added to provide custom titles to JViewer application if required.
	# Replaced the use of word JViewer in the application GUI related strings with teh custom title, if specified. 
	# Added support to avoid unknown parameters and continue lunching teh application normally.
	# Unable to click the ok button of popup box in JViewer, while changing the service as enable or disable; issue fixed
1.165.0
	# Auto resizing KVM Client window feature modified to work based on OEM Specific Feature, byte argument value.
	# Feature can be configured in PRJ under KVM feature.
1.166.0
	# Physical keyboard feature not working properly with Auto Detect keyboard layout; issue fixed
1.167.0
	Sign script moved into Jviewer_signkey pkg
1.168.0
	# Increased JViewer maximum session count.
	# Included a menu item Request Full Permission to request full permission, at any point of time.
1.169.0
	# Added web support to get OEM specific feature status, as a JViewer argument in jnlp file.
	# Enabled OEM specific feature support in JViewer StandAlone application.
1.170.0
	# Stopping active VMedia redirections, while giving away master privilege to another session.
	# Handled Master previlege request while closing JViewer
1.171.0
	#Handled exception cases :Issue  After the web session logout the jviewer launches with the blank screen.
1.172.0
	# Missing resource exception thrown while web logout fixed.
1.173.0
	# Stand Allone Application showing VMedia configuration error; issue fixed.
	# Softkeyboard mehu not enab;ed while disabling Auto keyboard layout.
1.177.0
	# Display proper message when virtual media instance count change.
	# Fit to Host Resolution zoom option does not work during system reboot; issue fixed.

1.181.0
	# Added message dialogs to be shown when image files are redirected in Read-Only mode.
	# Added message dialogs to be shown when already opened iamge file with read-write access is being redirected via VMedia.
	# When Client and Host screen resolutions are same, and zoom option selected is "Fit to Host Resolution", JViewer is not showing scroll bars; issue fixed.
1.183.0
	Video Record Settings dialog modified to retain the input values (storage path & time duration), even if closed and reopened.
1.184.0
	Soft Power Off Power Control command number modified to make it compatible with IPMI spec.

1.186.0
	VMedia dialog modified as a modeless dialog.
1.187.0
	Modified Host Monitor lock feature and added it to JViewer common code.
1.188.0
	Information dialog shown in JViewer if VMedia configuration changed from Web UI.
1.189.0
	Fixed screen data missing issue in JViewer view while zoom option selected in Fit to Host Resolution
1.190.0
	Fix for Other Mouse Mode issue in the RAID controller configuration window.
1.191.0
	Reverted changes done to change minimum frame size as 300 X 200. (Commit rivision : 37635) 
1.192.0
	Fit to Host Resolution zoom option implementation modified.
1.193.0
	The Start Video Record menuitem will remain enabled , if the Video Record Settings are provided once.
1.194.0
	Removed references to SystemTimeMointor thread.
1.195.0
	# Added connection status label in VMedia dialog to show the host device instance to which the media is being redirected.
	# Avoid multiple VMedia dialogs being opened using VMedia toolbar buttons, or Virtual media menu.
	# VMedia dialog contents localized when the GUI language is changed, while the VMedia dialog is already open.

1.197.0
	Fixed the issues with Fit to Host resolution zoom option, when the Auto Resize JViewer window configuration is not selected in PRJ.   
1.199.0
	Full Access permission given to the requesting session, on KVM sharing request timeout.
1.200.0
	Power Option menus getting enabled when a power OFF event occurs, in a Partial privileged KVM session; issue fixed
1.201.0
	JViewer window restores from minimized state, when the zoom option is Fit to Host, and a resolution change occurs in host; issue fixed.
1.202.0
	Resetting zoom options on JViewer focus lost and focus gain.
1.204.0
	Autokeyboard layout selected by default.
1.205.0
	VMedia dialog Device label displays device ID as Roman numerals.
1.206.0
	Support to open NRG CD Image files added in VMedia dialog.
1.207.0
	# ALT Gr + 102 key in Software keyboard displays proper charecter
1.208.0
	#Added feature to open device always read,write only
1.209.0
	# CD Images (NRG & ISO) can be browsed for redirection in VMedia dialog using same file filter.
	# Roman numerals in VMedia dialog Device label generated using UTF characters.
	# Fixed isseus regarding Dead Circumflex key in French to German keyprocessor.
	# Strings related to CD modified ans CD/DVD Media.
1.210.0
	#Add/Remove power related menu depends on user previlige.
1.211.0
	# Information dialog displays user name and IP of previous master, when a full permission request is granted to the KVM session with partial permission,
	and no master session is present; issue fixed.
	# Added proper pop-up message for image redirection.
1.212.0
	# JViewer mouse will resync to initial position on resolution change, only if the mouse mode is Relative.
	# Fixed NullPoinertException when launching Video Download and Vidoe Player apps.
1.213.0
	IMA file support added for Floppy redirection using VMedia.
1.214.0
	The Keyboard/Mouse Encryption menu item will not be available in the Options menu in JViewer single port application launched to connect via SSL socket.
1.215.0
	useSSl variable getter method added in JViewer.
1.216.0
	Physical keyboard layout selected in remote session configuration page will be selected in JViewer Stand Alone application also.

1.217.0
	# Separate menu items added to lock and unlock Host Display.
	# Toolbar button added to dipslay teh Host Display lock status, and to lock or unlock Host Display.
	# Resuming video redirection after pausing, does not work correctly in certain phases of POST; issue fixed.
1.218.0
	# Handled two more cases in Host Display Control status.
	# OEM Specific feature configurations listed under separate menu in PRJ
1.219.0
	Removed duplicate entry of "Context Menu" menuitem from Keyboard menu. 
1.220.0
	Resizable property of JViewer Frame removed for "Fit to Host Resolution" & "Fit to Client Resolution" zoom options.
1.221.0
	# Stopping VMedia redirection when master previlege is lost due to KVM Previlege request time out.
	# Adding KeyListener to JViewerView only if it is not already added.
1.222.0
	Enabled Debug logging in KVM CLinet.
1.223.0
	Multiple KVM redirection sessions to same BMC denied only when launched from same client, with same user name, and same user domain.
1.224.0
	Autodetect keyboard layout issues with Linux client fixed.
1.225.0
	Closing JViewer on KVM session time out.
1.226.0
	Added support to terminate KVM and VMedia redirection sessions using IPMI command.
1.227.0
	# Added function to set physical keyboard layout language in configuration file.
1.228.0
	# Set physical keyboard layout language in configuration file only iffeature enabled in PRJ.	
1.230.0
	# Updating the Vmedia device free slot in JViewer dynamically based on the Server connection accepted.
1.231.0
	# Number of VMedia free slots updated in JViewer.
1.232.0
	# Disable all the menu itmes, except Exit and Help, in JViewer while waiting for KVM permission.
1.233.0
	# VMedia dialog (if open) will be closed while giving away KVM full access permission.
	# IOException while writting to JViewer Debug log fixed.
1.234.0
	Adding mouse listener to JViewer toolbar icons only if its not already added.
1.235.0
	# Changing Mouse mode makes the JViewer stuck; issue fixed.
	# KVM permission response dialog in master session will not close, if the requesting session gets closed, while KVM sharing; issue fixed.
1.237.0
	Added new IVTP command packet for getting the Vmedia device free slot count in JViewer.
1.238.0
	# Fix : Power button in tool bar works in Partial access client.
	# Fix : Invoking Full Permission after master dead is printing Full Virtual Console access granted by null user with IP address null.
	# Fix : Invoking Full Permission after master dead is not enabling power button if host is power off.
1.239.0
	Fixed build error.
1.240.0
	# Video Pause, Resume, and Refersh menu items disabled after Full Permission Request; issue fixed.
	# Exception in JViewer when changing VMedia instance count; issue fixed.
	# File handles on image files closed properly in VMedia close due to Vmedia configuration change.
	# Video Record Start and Video record settings menu items are enabled after video recording, only if video rediection is not paused.
1.241.0
	Japanese software keyboard layout modified.
1.242.0
	Japanese software keyboard layout, key face characters modified using Unicode Character Set.
1.243.0
	KVM Client frame size update behaviour on Fit to Host zoom option modified.
1.244.0
	Updated active user list display to show proper information.
1.245.0
	Fixed SSL exception in standalone app rpc calls by setting system propery for https.protocol as SSLv3.
1.246.0
	JViewer Active User list not updating when switching from full screen to window mode; issue fixed.
1.247.0
	StandAloneConnection dialog will wait for user to select between KVM or Manage Video.
1.248.0
	Added code to save Auto Detect as host physical keyboard in configuration file
1.249.0
	#StandAlone JViewer App hangs if you enter an invalid Host IP address; issue fixed
	#Stand Alone App Manage Video option not able to download the recorded video; issue fixed
1.250.0
	Mouse synchronisation fails in Relative mouse mode when Show Cursor is enabled for the very first time; issue fixed.
1.251.0
	In Relative mouse mode, mouse movement is not sent to host, when mose is moved over the view, with a menu expanded in JViewer; issue fixed.
1.252.0
	# Zoom In & Zoom Out menu items become disabled when the Fit to Host zoom option can not be rendered; issue fixed.
	# Keyboard and mouse packets are not sent if the KVM session is have partial permission or if the server is powered OFF.
	# Select Next Master dialog trying to send null data when being disposed; issue fixed.
1.253.0
	Host Monitor Lock menu items should be grayed out if feature is disabled; issue fixed.
1.254.0
	Video is not displayed fully in Web previewew screen as in java console; issue fixed.
1.255.0
	Capture screen implementation modified to capture the screen at the moment of triggering the event.
1.256.0
	# JViewer slave session displays two pop up dialogs when Partial access permission is received; issue fixed
	# Soft Keyboard - Finnish - Symbol Mismatch fixed.
1257.0
	JViewer Mouse movement is not working in high resolution mode 1920x1200; issue fixed.
1258.0
	GUI Localization : String changes.
1.259.0
	# Physical Hard Disk Redirection fails; issue fixed
	# Jviewer- Getting wrong response message in send IPMI command; issue fixed
1.260.0
	# Hotkeys:Not able to view all 20 short-cut key, issue fixed.
1.261.0
	Video Socket Error comes when the VMedia device instance count is modified from 0 to non-zero; issue fixed.
1.262.0
	Mouse doesn't sync, when console launched initially and mouse mode is changed to relative; issue fixed.
1.263.0
	StandAlone app tries for MD5 Digest authentication only if the OEM feature MD5_DIGEST_WEB_AUTH is enabled.
1.264.0
	# Stand Alone Connection dialog help text localization issue fixed.
	# If an invalid host IP is given, the stand alone connection dialog appears inaccessible sometimes; issue fixed.
	# Stand Alone connection dialog title updation removed.
1.265.0
	Virtual Media connections will be closed after Changing Local Monitor off Control setting; issue fixed
1.266.0
	In Java console, while adding new macro, if we give Alt Key, the focus goes out of the text box; issue fixed
1.267.0
	# Modified same client check to use Mac adress instead of ip address
1.268.0
	JViewer Window rendering issues with Fit to Host resolution in Windows 8 client fixed.
1.269.0
	Disabled zoom slider when the mouse mode is Other mouse mode.
1.270.0
	Other mouse mode implementation modified to fix mouse hiding issue in Linux clients.
1.271.0
	Video Recording implementation modifed to start even when Host is Powered Off.
1.272.0
	Detect client user privilege to list available media in vmedia dialog.
1.273.0
	KVM reconnect feature
1.276.0
	# softkeyboard will be enabled always. but softkeyboard keys will be disabled on power off
	# Enter key is not working for the second popup window, while doing power operation in JViewer : fixed
	# fixed issue : pop-up's in JViewer will be hidden by softkeyboard.softkeyboard will be minimised when jviewer minimized
	# Proper softkeyboard was not displayed depends on selected once after selecting Auto-Detect in physical keyboard.
	# Displaying a message in vmedia dialog.when JViewer is not launched with admin/root privilege.
	# Fixed:- Virtual console sharing privilege popup window is not closed in master session, once master session has aborted.
1.277.0
	# Video record : Frames will be saved in java temp folder ,frames will be added into video files which will be saved in user selected path.
1.278.0
	# Help contents in Jviewer are not updated according to GUI language.	
	# Fixed : Java preview screen doesn't displays the message fully 
1.279.0
	#update in singleport connection request
1.280.0
	# removed softkeyboard to be always on top.	
1.283.0
	#The Display of KVM will not Fit to Client Resolution anymore when we change mouse mode from "Other mode" to "Absolute mode" : fixed
1.284.0
	# Fixed build time warnings due to type cast issue, and use of deprecated API.
	# Fixed build time warning due to use of unsupported command option in build script.
1.286.0
	# Video Player App skipping frames with frame size zero; issue fixed.
	# Video Player App skipping some frames while repaly; issue fixed.
1.287.0
	Added fix to solve the MD5 digest calculation droping leading zeroes issue.
1.288.0
	Added Physical Keyborad support for more language layouts.
1.289.0
	#Fixed KVM master session is not able to terminate from web .when two sessions active.
1.290.0
	#Fixed issue :After exit/session terminated if window is not closed Jviewer tries to reconnect.
1.291.0
	Device instances already in use not updated dynamically in Vmedia dialog; issue fixed.
1.292.0
	COntinous vmedia connect/disconnect leads to KVM reconnect due to socket timeout.
1.293.0
	Handled device ejection from host.  
1.294.0
	IMA file type support added for harddisk image redirection.
1.295.0
	Added support to perform CD/DVD image redirection using Java implementation.
1.296.0
	#added ivtp pkt for connection failed when singleport is enabled.
	#On KVM reconnect if IP/Mac address changed Session will be terminated.
	#webpreview issue when KVM reconnect feature enabled
1.297.0
	connecetion failed handle case
1.298.0
	Fixed data corruption issue in CD/DVD image redirection using Java implementation.
1.299.0
	# Allow Empty password login support for standalone application	
1.300.0
	# Added support for Dutch-Belgium physical keyboard layout.
	# Fixed CTRL+key combination issue in Cross mapping key processor.
1.302.0
	Allow all types of users in Mac client to perform physical device redirection
	using VMedia dialog.
1.303.0
	#Jviewer hangs completely and closes after a while on disconnecting any media device
	 from virtual media wizard during vmedia server restart
1.304.0
	Softkeyboard Number pad decimal key character set to ',' for the appropriate layouts.
1.305.0
	Number keypad keys not working properly in BIOS setup; issue fixed.
1.306.0
	JViewer displays in-appropriate message while reconnection made with server; issue fixed.
1.307.0
	Added code to disable the cd/fd/hd service and update Vmedia dialog when the service is disabled in the Web UI.
1.308.0
	Zoom in and Zoom out menu option updated properly after JViewer focus lost and focus gain.
1.309.0
	reverting 1.307.0
1.310.0
	Support added for IBM java
1.311.0
	Hot keys update during reconnect and Full permission request issue fixed
1.312.0
	Launching MAXsess+1 crashing video server issue fix
1.313.0
	Added support to enable or disable JViewer Debug logging at run time.
1.314.0
	Modifed other mouse mode implemenation to fix mouse flickering issue.
1.315.0
	Fixed issue in PrtScrn key function . Print screen key was sent thrice.
1.316.0
	ALT+C not working when full keyboard support is enabled; issue fixed.
1.317.0
	Avoided the cases where the exceptions occur during the localization of the GUI.
1.318.0
	Blank Screen displayed while launching Jviewer with Stand alone KVM application; issue fixed.
1.319.0
	Keyboard LED status out of sync while host powered OFF; issue fixed.
1.320.0
	Alt+ Printscreen was not working. only with full screen board it will work now
1.322.0
	Showing single error message incase of media redirection failure.
1.323.0
	Fixed build break.
1.324.0
	Moved AVIStream to a separate SPX package.
1.325.0
	Added IAVIOutputStream interface.
1.326.0
	Added code to show proper error message on media redirection fails with invalid media license
1.327.0
	Added support for Physical keyboard layout crossmapping for Linux clients and hosts.
1.328.0
	#fixed mouse trailing on KVM reconnect
	#fixed Video recording: name of downloaded video file is redundant
1.331.0
	Enabled Japanese Physical Keyboard support.
1.332.0
	Added ISO redirection Read Ahead Caching support.
1.333.0
	Added support for KVM virtual device count
1.334.0
	added HDD disk partiton redirection support
1.335.0
	Enter key not working in JOptionPane; issue fixed.
1.336.0
		updated server connection failure case for media redirection with singleport enabled
1.377.0
	Stop jviewer on webserver restart
1.338.0
	Ctrl+c is not working when selecting Linux Host in JViewer; issue fixed.
1.339.0
	receive get asp call response till respond end reached insted with max buffer.
	list video file list properly with n entries.
1.340.0
	fix for recorded video file not displaying video full time duration if screen dosen't have change.
1.341.0
	While launching JViewer the message dialog is not displayed if reconnection is in progress; issue fixed.
1.342.0
	Video file in temp folder is not deleted when player app is closed abruptly; issue fixed.
1.343.0
	KVM hangs and session gets terminated with Java 6 on Host reboot with active cd redirection; issue fixed.
1.346.0
	display proper error on media redirion failure with singleport connection
1.347.0
	reset media session terminat flag to avoid multiple popup on termination
1.349.0
    download and play huge  size video file.
1.350.0
	unable to redirect new iso file in slot II; issue fixed
1.351.0
	Jviewer get closed abruptly if we redirect invalid nrg file; issue fixed
2.1.0
	video file name redundant; issue fixed.
2.2.0
	updated ipaddress to hold ipv6 address, modified pop up message when connection already in use.
2.3.0
	moved windows host, linux host options under keyboard layout menu.
2.4.0
	cpu memory used not reclaimed; issue fixed.
2.5.0
	added proper translations for french, locale for JComponents.
2.6.0
	restricted buffer size in dowload app to avoid out of memory error
2.7.0
	Alt+T key combination does not work during Mouse Calibration operation, when Full Keyboard support id enabled in JViewer; issue fixed.
2.8.0
	Enabled right click pop-up menu for IPMI Command Dialog in JViewer.
2.9.0
	If media redirection in progress with remote/local media. instead of showing loopback ip in info dialog it will show  in use by remote/local media	
2.10.0
	logout websession once tunneling success and create websession on media redirection start
2.11.0
        updated messages while downloading video
2.12.0
	Added pause/play button and slider to player app.
2.13.0
	Added HttpsURLConnection to create ssl socket
2.14.0
	updated physical keyboard layout langauges list
2.15.0
	added user agent details for login, logout audit in BMC.
2.16.0
	trusted connection server validation : standalone application changes
2.17.0
	The host screen resolution will be displayed on the JViewer title bar.
2.18.0
	Proper error message shown when inavlid Floppy or HDD/USB images are being redirected.
2.19.0
	trusted connection server validation : singleport, player and download application changes
2.20.0
	Included support to use Dual-monitor in client. window  and pop-up will be placed according to Jviewer window	
2.21.0
	reused socket for player and download application.
2.22.0
	delay in displaying the windows and popup's in linux client; fixed.
2.23.0
	added proper error message for invalid ip
2.24.0
	updated proper webport value
2.25.0
	closed socket after logout websession; returned proper error status on file not found
2.26.0
	player/download app related changes; fixed exceptions.
2.27.0
	Mouse cursor drag issue when Fit to Client zoom option is selected, fixed.
2.31.0
	French localization fixes
2.32.0
	Logical drives associated with a redirected physical drive available fro redirection; issue fixed.
2.33.0
	# Client Dual Monitor support: JViewer window auto shifts to primary window on closing vmedia wizard; issue fixed.
	# If Redirect media in Read/Write mode feature enabled, show status message in VMedia dialog that physical hard disk drives won't be listed.
2.34.0
	No confirmation dialog appears while closing KVM client with active VMedia redirection; issue fixed
2.38.0
	send MEDIA_SESSION_DISCONNECT packet on media disconnect.
2.39.0
	KVM attempts another reconnect, after a successfull reconnection; issue fixed.
2.40.0
        Added proper error message on reconnect failure.
2.42.0
	If KVM sharing dialog times out with active media redirection in progress, proper privilege is not transferred to the slave session; issue fixed.
2.43.0
	Handle kvm reconnect retry,count asp failure and reconnect request failures.
2.44.0
	Video record shortcut key should show only start and stop and not allow settings in it. also on starting recording there is no change of color; issue fixed.
2.45.0
	Added support for customising common package using JViewer_OEM package.
2.46.0
	#Display host video resolution in playerapp title bar
	#Video recording file not created when the logical drive configured as destination path is redirected post starting redirection
2.47.0
	# blocked jviewer to move to top left corner on focus gain only if resolution was not changed when jviewer was not in focus
	# Added menu to block KVM request in master session and show proper information in slave session
	#display user priviledge on KVM privilege request dialog
2.48.0
	#fixed : CD/DVD drive unplug from client after redirection is not detected
	#Display remaining video recording time
	#reconnecting media redirection failes if redirected device data copy in host
	# KVm will detect reconnect after 15 seconds. reduced socket timeout to 6 seconds to detect n/w loss in 6 sec
2.49.0
	Virtual Media dialog redesigned to work as a stand alone application.
2.50.0
	altgr selected from menu doesn't work as expected. Added condition to check if altgr is enabled in menu before disabling modifiers.
2.51.0
	handled oem related exceptions to make webpreviewer work
    Removed OEM specific method declarations from common interface
2.52.0
	Added jviewer video record changes that were missed in VMapp merge
	Jviewer proceeds even if the security pop up is closed instead of clicking cancel.
	checking/unchecking normalize video checkbox doesn't work as expected in jviewer video recording; fixed.
2.53.0
	Fixed build error.
2.54.0
	Omit blank video file in video recording while changing screen resolution.
2.55.0
	#On master reconnect if a request in progress .requestested session will get notified about reconnect and provided partial permission
	# fixed issue in media redirection reconnect if master loses full permission
	#fixed issues in reconnect with multiple session
2.56.0
	Reduced VMedia dialog size when launched from JViewer App.
	Added codition to check if the user has extended privilege before launching VMapp.
	Harddisk image redirection gives null pointer exception; issue fixed
	while launching vmapp from jar, icon resouce file was not loaded properly; issue fixed.
2.57.0
	Added missed debug import statement.
2.58.0
	Modified french translation strings
2.59.0
	jviewer video record fixes; video record fails if video record settings is simply closed without entering proper values.
	jviewer standalone app with singleport enabled fails, singleport status not updated properly.
	vmedia device count updated based on vmedia max count for kvm feature.
2.60.0
	Pendrive, harddisks connected in linux client didn't list in JViewer Vmapp dialog and VMapp, issue fixed.
	Redirection status on device eject, removal and session kill didn't updated properly, issue fixed.
	Device list is not updated properly on device insert removal on already connected instance and in free instances of vmedia dialog. issue fixed.
2.61.0
	# JViewer Client keyboard LED sync issues fixed.
	# Avoid web certificate validation on KVM/VMedia socket connect, if singleport is enabled.
2.62.0
	update kvm cd/hd/fd num when kvm max count feature is disabled.
2.63.0
	added option to block incoming request in KVMsharing request dialog
2.64.0
	playerapp/downloadapp fixes:
	videolist not updated properly in the connection dialog. GUI not updated properly. Revalidate the dialog once video list is received.
	player app doesn't replay the video. Timer is started without checking whether it is already running or not.
	download app doesn't convert video with no changes. Duration was not updated properly. Calculate duration once all the frames are received. 
2.65.0
	terminate slave kvm session from Master KVm session from "active user list"
	Instead of showing play and pause button in jviewer made it like toggle
2.66.0
	On receiving session disconnect packet from media servers on server stop/restart update the vmedia instance and device status.
2.67.0
	JViewer Zoom tool bar button added.
2.68.0
	with singleport enabled, vmapp fails. singleportkvm object was null. issue fixed.
2.69.0
	HID initialization will be notified to the user through an information popup.
2.70.0
	HID initialization notification popup will appear only if power save mode is enabled.
2.71.0
	Added oem handle to customize where Jviewer window should be placed.
2.72.0
	Vmedia config changed messsage shown on local monitor off. issue fixed.
	cd instance num was compared with kvm cd instance num.
2.73.0
	 Updated JViewer and VMApp to work with REST service.
2.74.0
	Added skip repositioning jviewer to top left of the current monitor as prj configurable.
2.75.0
	Updated JViewer and VMApp to work with REST service; getting web session token and logout related changes
2.76.0
	Updated help menu to work with Rest service
2.77.0
	Updated code for player./download app to work with Rest service
2.78.0
	set websession cooike for singleport to work webpreiviewer 
2.79.0
	Application will be launched based on IPMI user privilege is the Extended Privilege support is disabled.
2.80.0
	StandAlone application library extraction issue fixed.
2.81.0
	Fixed issues with showing Vmedia configurations update changes.
2.82.0
	AltGr key combinations will be sent from JViewer to the Host even when the full keyboard support is disabled.
2.83.0
	Fixed keyboard LED status sync issues caused by wrong FrameRateTask scheduling.
2.84.0
	Fixed GUI localization issues with OEM customized Localization resource bundles.
2.85.0
	Localization fixes: Looking up resource files from jar file lists all the available files inside the jar instead of resource files alone. Issue fixed.
2.86.0
	Changed the Localization language display string in StandAlone dialog as well as Localization Menu
2.87.0
	Restricting localization language menu item changes to the relevant menu items only.
2.88.0
	#Fixed issue in HDD redirection(accessing library)
	#fixed issue in vmediadialog update after reconnect with media redirection
	#fixed issue in showing "reconnect in progreee" dialog after reconnect success
	#avoided several exception by checking againt null before use
	#Added condition to skip device detection if vmedia dialog is not opened
	#avoid going for reconnect if user stopping jviewer or jviewer received stop command from video server
2.89.0
	#if redirection state is stopped/stopping blocked reconnect
2.90.0
	Fixed Client keyboard LED status sync back issues while closing JViewer.
2.93.0
	Free media instance count not recevied properly in JViewer for Floppy and HD instances; issue fixed.
2.94.0
	JViewer : Full Screen option is not working; issue fixed.
2.95.0
	On Disabling the CD Service other services - FD and HD is also blocked and unable to be redirected; issue fixed.
	* updated proper condition check for fd and hd.
	* updated proper variable on change in cd/fd/hd state.
2.96.0
	Fix to avoid comparing mac address with 00-00-00-00-00-00
2.97.0
	JViewer Menu accelerators (Shortcut keys) not working on Linux and Mac Clients; issue fixed.
2.98.0
	Message text modified, on the information dialog, that appears while trying to close JViewer without giving permission to requesting sessions.
2.99.0
	SOC resource bundle translation fix:
	If OEM bundle doesn't exist for the chosen language then default language is chosen for OEM and returned before changing SOC resource bundle.
	SOC resource bundle should be changed for the chosen language irrespective of whether OEM bundle has been changed or not.
2.100.0
	JViewer StandAlone App crashes on Linux platform if VMApp also launched from same directory; issue fixed.
2.101.0
	Added sleep in VMApp related threads to reduce CPU overhead.
	Added thread names useful for debugging
2.102.0
	#Adding Request information to progress bar 
2.103.0
	Added changes to display proper error message for redirecting the wrong file format in CD/FD/HD redirection field in JViewer
2.104.0
	Added changes to load default language resource if specified language is missing
2.105.0
	Added support to new parameter -launch in the StandAlone application for launching required application type from commandline / terminal.
	Enhanched command line argument validation.
2.106.0
	Synchronizing keyboard LED status based on client keyboard LED status.
2.107.0
	Physical Keyboard menu update fix for rest service.
2.108.0
	Adding .keep file to empty directories to satisfy source controls like git.
2.109.0
	Fix for partial localization in case of missing resource bundle.
	-- A JViewer instance points to Null reference during runtime (Inside getSupportedLocales() method in StandAloneConnectionDialog.java)
	   So moved the method along with it's dependencies to JViewer.java
	-- AppType[String] value (in StandAloneConnectionDialog.java) can't be used for comparison in case of localization. 
	   So changed the signature to integer value and the comparison will be done using index values rather than String constants.
2.110.0
	Fix JViewer crash on media eject.
	* validated nativereaderpointer before trying to delete media object in jni
	* handle eject command after sending response to host to avoid host throw error on eject
2.111.0
	-- Fix for Readonly Privilege KVM is always active while previous request is processing. 
	-- Updated the copyright information in the help content.
2.112.0
	Validated websession cookie in singleport disabled case.
2.113.0
	Not to Force Java standalone application to use SSLv3
2.115.0
	Updated VMedia Redirection status after changing GUI language.
	
2.118.0
	Fix for F10 key press invoking menu item on Host reboot.
2.119.0
	Fixed build; Added proper import for keystroke.
2.120.0
	Fixed dual monitor jviewer full screen title bar is not properly displayed 	
2.122.0
	Added changes for issue: AVR reconnect happens if AVR is opened during BMC online flash.
2.123.0
	TLSv1.2 support
2.124.0
	Reverted changes to avoid reconnect failure.
2.125.0
	set socket time out for connection handshake to avoid hang
2.126.0
	avoid showing alt+c info dialog for partial prmission session
2.127.0
	Added logical drive validation to device redirection status check.
2.128.0
	Method to improve KVM security on blocking incoming KVM privilege request.
2.129.0
	Added changes to display retrycount and timeinterval in reconnect popup message.
2.130.0
	Added code to detect BMC reboot / Connection Loss for VMApp
2.132.0
	Added changes to update vmedia gui when vmedia configuration is changed
2.133.0
	Fix for rKVM will disconnect when OS execute reboot script with shared NIC.
2.134.0
	Fix for NullPointerException thrown in VMedia Dialog.
2.135.0
	removed delay before sending connection complete packet
2.136.0
	Handled Device Redirection code in a separate thread to prevent VMedia dialog gets hang.
2.137.0
	KVM sharing: partial request session behaves different if H5viewer master session closed without giving permission
2.138.0
	Fix for device connection status when media is disconnected
2.139.0
	Blocked HID initialization pop up for slave session
2.140.0
	Dual moniotr issue fixed :KVM sharing request and response dialog was not showin according to jviewer window
2.141.0
	Fix for Retry interval goes beyond configured interval time.
2.142.0
	Seding Keep Alive IUSB command to cdserver from Java CD Image redirection is enabled.
2.143.0
	Fix for improper time update in confirmation dialog.
2.144.0
	Fixed issues with loading proper keyprocessor, and issues with crossmapping dead keys.
2.145.0
	Connect button string update in VMedia Dialog.
2.146.0
	added restart status new codes and strings
2.147.0
	Blocked video recording when video redirection is paused.
2.148.0
	KVM reconnect fails on the last retry; issue fixed.
2.149.0
	reinitilize iusb object to adopt recent vmedia instance count
2.150.0
	increased media socket timeout
2.151.0
	Showing information message when the video file download is in progress for Video Player/Recorder app.
2.152.0
	Restricted video file access for Manage Video during active recording in progress.
2.153.0
	RHEL 7.2 host triggers print screen continuously when print screen is sent from KVM client; issue fixed.
2.154.0
	Request websession token only while launching KVM through JViewerApplication
2.155.0
	Playback buffering added for Video Player App.
2.156.0
	Fixed data corruption issue in Java ISO redirection with read ahead caching.
2.157.0
	Added support to send redirected media info to media server.
2.158.0
	Removed floppy code references.
2.159.0
	Fixed HID initialization dialog is not visible when powersaver mode is enabled.
2.160.0
	Added changes to close the socket after verifying getServerResponseData() return value. Review #15810
2.161.0
	Fixed unable to download video using downloader app.
2.162.0
	blocked launching multiple instances of standalone application from same jar file and path
2.163.0
	update physical/logical device list on detecting device insert/remove in runtime
2.164.0
	Adding keylistener while syncing LED status, with OEM_SYNC_WITH_CLIENT_LED_STATUS feature enabled.
2.165.0
	increased max fragment buffer size to accomadate 9 MB of frame in full screen
2.166.0
	Added keep alive mechanism for media redirection.
2.167.0
	Fix for video player playing video even after time exceeds if video is blank screen
2.168.0
	fix for HDD drive redirection in GUI languages other then english
	restrict opening multiple ipmi dialog
2.169.0
	master session goes to reconnect when kvm privilege blocked with "access denied" and second session launched	
2.170.0
	consider the free device instance count when updating the vmedia dialog
2.171.0
	Fix for issue: when a media is redirected from webpage and if virtual media wizard is not opened already, media sections in virtual media wizard are not updated
2.172.0
	OEMException list concatenation is not included properly, fixed the System.arraycopy parameter.
2.173.0
	Fixes download app issue: when no change in host video, the download app fails.
2.174.0
	Displayed reconnect failure message for client ip and mac address change.
2.175.0
	Fit to Host zoom option fails when HID Initialization pop-up message is displayed while launching JViewer; issue fixed.
2.176.0
	1.close softkeyaboard on KVM stopp command received before showing information dialog as softkeyboard will set always top	
	2. skip secure connection certificate validation code in debug time
2.177.0
	Modified keep alive mechanism for media redirection.
2.179.0
	Proper error message will be shown, if the user tries to use wrong StandAloneApp jar file to connect to a BMC.
2.180.0
	Adding back key listener after keyboard LED sync.
2.181.0
	change video record settings dialog to modeless so that it doesn't block other dialogs
2.182.0
	display hdd/usb device redirecion not possible if jviewer is not launched as administator	
2.183.0
	don't update vmedia dialog based on free instances for vmapp. VMapp doesn't receive any ivtp commands.
2.184.0
	Fixed block permission (deny access) is not working properly.
2.185.0
	JViewer video player app, progress slider synch improved.
2.186.0
	fixes russian softkeyboard, replaced | with / as russian doesn't contain | symbol
2.187.0
	Added bpp information for PILOT SOC.
2.188.0
	* Modified language resource bundle loading mechanism, giving preference to OEM language resource bundles.
	* KVM client titlebar label updated properly, if bpp info is not available.
2.189.0
	On updating active client list, avoid updating last received packet time by setting status as "1"
2.190.0
	added date and time with log in debug log file mode
2.192.0
	allow http session with singleport enabled
2.193.0
	Issues related to saving configurations in the video record settings dialog fixed.
2.194.0
	Upgraded JDK version from 1.5 to 1.7
	Removed deprecated API from UserDefMacro.java
2.195.0
	The Physical/Logical drive selection option will be available only if HD/USB physical devices are connected to client.
2.196.0
	Blocked JViewer execution in 32-bit JVM.
2.197.0
	Proper error message shown if reconnect failure happens due to BMC or server restart while reconnect in progress.
2.198.0
	Quit player/download app when the file size is less than or equal to zero
2.199.0
	Fixed random increase in CPU utilization issue, when using JViewer media redirection.
2.200.0
	Removed ReadOnly mode popup from VMedia dialog / VMApp pane.
	Added additional column in VMedia status table named redirection mode.
2.201.0 
	Added fix to show grant permission dialog even if JViewer is in minimized state
2.202.0
	Fixed vmapp launch issue when video server(adviser) support is not enabled in BMC
2.203.0
	Removed set socket time out from media redirection sockets. Implemented alternate mechanism to detect socket disconnect.
2.204.0
	Added detailed information about video file download progress for Player / Downloader application.
2.205.0
	Fixed connection not terminating if JViewer is in paused state while terminating session from JViewer 
	This also fixed reconnect not initiated if JViewer is paused state
2.206.0
	Added user notification for active slave sessions when master session is closed without selecting next master.
3.0.0.0.0
	Upgraded to 5 Digit format.
3.1.0.0.0
	Added Fix for KVM Window freezes on clicking the KVM menu while KVM Reconnect is in Progress.
3.2.0.0.0
	Added folder redirection for sharing the client-side folder to the host-side computer.
3.3.0.0.0
	Fixed the UI of USB Redirection in JViewer which doesn't show correctly after adding the Folder Redirection feature.
3.4.0.0.0
	fix for enabling show client cursor on lauch if relative mouse mode is enabled	
3.5.0.0.0
	Fix share folder grater than 512mb, image will be created and can't connect again.
3.6.0.0.0
	Fix for When the user redirect that permission denied folder , it have to notify user 			
3.7.0.0.0
	Fix UI will hang up when user cancelling create image progress will reach 100 percent
3.8.0.0.0
	Added a constraint about image path and folder path of Folder Redirection.
3.9.0.0.0
	Add user notification after folder redirection for shareing,to avoid the user synchronizes the host-side folder back to the client-side
3.10.0.0.0
	Fix share folder mount on maintrunk issues
3.11.0.0.0
	Fix code version
3.12.0.0.0
	fix for listing physical/logical drives on insert and removable from client machine.single/multiple device
3.13.0.0.0
	fixed build break	
3.14.0.0.0
	Fix wrong pop up message in HD image redirection
3.14.0.0.0
	Fix files will be moved when user cancels the process of synchronization
**/

package com.ami.kvm.jviewer.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.omg.CORBA.LocalObject;

import com.ami.iusb.CDROMRedir;
import com.ami.iusb.HarddiskRedir;
import com.ami.iusb.IUSBRedirSession;
import com.ami.kvm.jviewer.ClientConfig;
import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.common.ISOCApp;
import com.ami.kvm.jviewer.common.ISOCCreateBuffer;
import com.ami.kvm.jviewer.common.ISOCFrameHdr;
import com.ami.kvm.jviewer.common.ISOCKvmClient;
import com.ami.kvm.jviewer.common.ISOCManager;
import com.ami.kvm.jviewer.common.oem.IOEMManager;
import com.ami.kvm.jviewer.communication.Connection;
import com.ami.kvm.jviewer.hid.KeyProcessor;
import com.ami.kvm.jviewer.hid.USBKeyProcessorEnglish;
import com.ami.kvm.jviewer.hid.USBKeyboardRep;
import com.ami.kvm.jviewer.hid.USBMouseRep;
import com.ami.kvm.jviewer.jvvideo.JVVideo;
import com.ami.kvm.jviewer.kvmpkts.CfgBandwidth;
import com.ami.kvm.jviewer.kvmpkts.IVTPPktHdr;
import com.ami.kvm.jviewer.kvmpkts.KMCrypt;
import com.ami.kvm.jviewer.kvmpkts.KVMClient;
import com.ami.kvm.jviewer.kvmpkts.KeepAlive;
import com.ami.kvm.jviewer.kvmpkts.Mousecaliberation;
import com.ami.kvm.jviewer.kvmpkts.UserDataPacket;
import com.ami.kvm.jviewer.videorecord.URLProcessor;
import com.ami.kvm.jviewer.videorecord.VideoRecordApp;
import com.ami.vmedia.VMApp;
import com.ami.vmedia.gui.VMDialog;



/**
 * JViewer frame module class.
 */
public class JViewerApp {

	private static JViewerApp m_rcApp = new JViewerApp();
	private JVFrame m_frame;
	private KVMClient m_KVMClnt;
	private ISOCKvmClient sockvmclient;
	private static IOEMManager oemManager;
	private VideoRecordApp videorecordapp;
	private JViewerView m_view;
	private String m_encToken;
	private boolean m_wndMode = true;
	private WindowFrame m_wndFrame;
	private FSFrame m_fsFrame;
	private byte hostLEDStatus = 0; // Host Keyboard LED state backup
	private byte clientLEDStatus = 0; // Host Keyboard LED state backup.
									// Value for this variable should be assigned only in getClientLEDStatus()
	private byte initClientLEDStatus = 0;//The Client Keyboard LED status when JViewer gains focus.
	private boolean kbdLEDSyncing = false;//The flag which denotes whether already a client LED sync process is in progress.
	private String m_session_token;
	private int m_session_token_type = 0; // Web token as default
	private byte[] m_serverIP;
	private int m_serverPort;
	private int m_RedirectionState = REDIR_STOPPED;
	private boolean m_bUseSSL = false;
	private int m_cdPort;
	private int m_hdPort;
	private int m_cdNum = 1;
	private int m_hdNum = 1;
	private int freeCDNum = 0;
	private int freeHDNum = 0;
	private int KVMCDNum = 0;
	private int KVMHDNum = 0;
	private int m_cdStatus;
	private int m_hdStatus;
	private int selectedVMTab = VMApp.CD_MEDIA;
	private boolean m_bVMUseSSL = false;
	private USBKeyboardRep m_USBKeyRep;	
	private KeepAlive       m_liveListener = null;    // KeepAlive Listener

	private KeyProcessor keyprocessor = null;
	
	private ISOCApp soc_App;
	private ISOCFrameHdr socframeHdr;
	private static ISOCManager soc_manager;
	private JLabel label;
	private JDialog dialog;
	private VideoRecord m_videorecord;
	private Mousecaliberation Mousecaliberation;
	private String Message;
	private String serverIP;
	private SoftKeyboard softKeyboard;
	private AutoBWDlg m_autoBWDlg;
	public static byte WEB_PREVIEWER_CAPTURE_SUCCESS = 0;
	public static byte WEB_PREVIEWER_CAPTURE_FAILURE = -1;
	public static byte WEB_PREVIEWER_CAPTURE_IN_PROGRESS = -2;
	public static byte WEB_PREVIEWER_CONNECT_FAILURE = -3;
	public static byte WEB_PREVIEWER_INVALID_SERVERIP = -4;
	public static byte WEB_PREVIEWER_HOST_POWER_OFF = -5;
	public static byte WEB_PREVIEWER_INVALID_WEBPORT = -6;
	public static byte WEB_PREVIEWER_INVALID_WEBSECURE = -7;
	public static byte m_webPreviewer_cap_status = WEB_PREVIEWER_CAPTURE_IN_PROGRESS;
	private KVMShareDialog kVMDialog =  null;
	private int m_zoomSliderValue;// Zoom slider value
	private boolean m_userPause = false;//Flag to check whether video redirection is paused by user.
	public static final int MAX_IMAGE_PATH_COUNT = 5;
	public static String Imagepath_CD[][];
	public static String Imagepath_Harddsik[][];
	public static boolean showCursor = false;
	public static int REDIR_STOPPED = 0x00;
	public static int REDIR_STARTING = 0x01;
	public static int REDIR_STARTED = 0x02;
	public static int REDIR_STOPPING = 0x03;
	public static int REDIR_PAUSING = 0x04;
	public static int REDIR_PAUSED = 0x05;
	public static final int NUMLOCK = 0x01;
	public static final int CAPSLOCK = 0x02;
	public static final int SCROLLLOCK = 0x04;
	public boolean m_refresh = false;
	private VMDialog vmDialog = null;
	public JDialog mediaDlg = null;
	public JVVideo vidClnt;
	public ISOCCreateBuffer prepare_buf;
	public static final byte OEM_FIT_TO_HOST_SCREEN = 0x01;
	public static final byte OEM_REDIR_RD_WR_MODE = 0x02;
	public static final byte OEM_SHOW_HOST_CURSOR_DEFAULT= 0x04;
	public static final byte OEM_SET_PHYSICAL_KBD_LANG= 0x08;
	public static final byte OEM_MD5_DIGEST_WEB_AUTH = 0x10;
	public static final byte KVM_RECONNECT_SUPPORT = 0x20;
	public static final byte OEM_JAVA_CD_IMAGE_REDIR = 0x40;
	public static final int OEM_KVM_MAX_DEVICE_COUNT =  0x80;
	public static final int OEM_SKIP_REPOSITIONING_JVIEWER_WINDOW =  0x100;
	public static final int OEM_SYNC_WITH_CLIENT_LED_STATUS =  0x200;

	public static final byte SERVER_POWER_ON = 1;
	public static final byte SERVER_POWER_OFF = 0;

	public static final byte HOST_DISPLAY_UNLOCK = 0x00;
	public static final byte HOST_DISPLAY_LOCK = 0x01;
	public static final byte HOST_DISPLAY_UNLOCKED_AND_DISABLED = 0x02;
	public static final byte HOST_DISPLAY_LOCKED_AND_DISABLED = 0x03;

	public static final int GET_MAC_ADDRESS = 1;
	public static final int COMPARE_MAC_ADDRESS = 2;
	public static final int VM_DISCONNECT = 0;
	public static final int VM_RECONNECT = 1;
	public static final int LEFT_FRAME_BORDER_SIZE = 8; //pixels

	private byte powerStatus;	

	private StandAloneConnectionDialog connectionDialog;
	private SinglePortKVM singlePortKvm;
	private AutoKeyboardLayout  autokeylayout = null;

	private String currentVersion = "3.14.0.0.0";
	private UserDefMacro userDefMacro = null;
	private AddMacro addMacro = null;
	private IPMICommandDialog ipmiDialog = null;
	private String zoomOption;
	private boolean fullKeyboardEnabled = false;
	private boolean fullPermissionRequest = false;
	private boolean renderFitToHost = true;
	private Hashtable<String, JDialog> responseDialogTable = null;
	public int currentSessionId = -1;
	private long lastPcktSent;
	private long lastPcktRecvd;
	private boolean sessionLive = false;
	private boolean retryConnection = false;
	public int currentRetryCount = 0;
	private int localport;
	private String resolutionStatus = null;
	private Dimension initialDimension = new Dimension(-1, -1);
	private Connection connection;
	private FrameRateTask frameRateTask = null;
	private String bppString = null;
	// These variables NonSSLService, WebService, KVMService
	// is used to represent the service from which createSocket() is being called.
	// for any service which requires nonssl socket, 0 should be passed. (NonSSLService)
	// In case of SSL and web request 1 should be passed.(WebService)
	// incased of SSL and Video/media request 2 should be passed.(KVMService)
	public static final int NonSSLService = 0;
	public static final int WebService = 1;
	public static final int KVMService = 2;

	public String VIDEO_RECORD_DEFAULT_PATH = System.getProperty("user.home");
	public int VIDEO_RECORD_DEFAULT_TIME = 20;
	private int resolutionChanged = -1;
	/* IPMI Privilege levels */
	private int PRIV_LEVEL_NO_ACCESS= 0x0F;
	private int PRIV_LEVEL_PROPRIETARY=0x05;
	private int PRIV_LEVEL_ADMIN=0x04;
	private int PRIV_LEVEL_OPERATOR	=0x03;
	private int PRIV_LEVEL_USER=0x02;
	private int PRIV_LEVEL_CALLBACK=0x01;
	private int PRIV_LEVEL_RESERVED=0x00;
	private InfoDialog hidInitDialog = null;
 
	/**
	 * Object used as a lock for proper synchronization of retry count value between
	 * InfoDialog thread and checkReconnect() / OnVideoStartRedirection() methods
	 */
	private Object retryCountSync = new Object();

	/**
	 * @return retryCountSync
	 */
	public Object getRetryCountSync() {
		return retryCountSync;
	}

	private static final byte SESSION_TYPE_VKVM = 5;

	public Object createObject(String className) {
		Object object = null;
		try {
			Class classDefinition = Class.forName(className);
			object = classDefinition.newInstance();
		} catch (InstantiationException e) {
			Debug.out.println(e);
		} catch (IllegalAccessException e) {
			Debug.out.println(e);
		} catch (ClassNotFoundException e) {
			Debug.out.println(e);
		}
		return object;
	}

	/**
	 * Get JViewer frame singleton instance.
	 *
	 * @return singleton instance of JViewer frame.
	 */
	public static JViewerApp getInstance() {
		return m_rcApp;
	}

	/**
	 * The constructor.
	 */
	private JViewerApp() {
		try{
			soc_manager = (ISOCManager) createObject("com.ami.kvm.jviewer.soc.SOCManager");
			oemManager = (IOEMManager) createObject("com.ami.kvm.jviewer.oem.OEMManager");
			prepare_buf = soc_manager.getSOCCreateBuffer();
			soc_App = soc_manager.getSOCApp();
			socframeHdr = soc_manager.getSOCFramehdr();
			connection = new Connection();
		}catch(NullPointerException ne){
			Debug.out.println(ne);
			if(JViewer.isStandAloneApp()){
				JOptionPane.showMessageDialog(m_frame, LocaleStrings.getString("D_1_JVAPP"), LocaleStrings.getString("D_2_JVAPP"), JOptionPane.ERROR_MESSAGE);
				JViewer.exit(0);
			}
		}
		// construct JViewer view.
        m_view = new JViewerView(JViewerView.DEFAULT_VIEW_WIDTH, JViewerView.DEFAULT_VIEW_HEIGHT);

		// Don't construct GUI components for WebPreviewer and until the StandAloneApp
		// connection is success.This is done to postpone the UI creation until the 
		//language settings is seected from the StandAloneConnection dialog.
		if(!JViewer.isStandAloneApp() && !JViewer.isWebPreviewer() && !JViewer.isBSODViewer())
			constructUI();

	}

	/**
	 * Construct the JViewer user interface.
	 */
	public void constructUI(){
		m_wndFrame = new WindowFrame();
		if(!JViewer.isplayerapp() && !JViewer.isdownloadapp()){
			m_fsFrame = new FSFrame();
			m_USBKeyRep = m_view.getM_USBKeyRep();
			m_zoomSliderValue = 100;
			if(!JViewer.isVMediaEnabled()){
				m_wndFrame.getWindowMenu().getMenuBar().remove(m_wndFrame.getWindowMenu().
						getMenu(JVMenu.MEDIA));
				m_fsFrame.getM_menuBar().remove(m_fsFrame.getM_menuBar().getFSMenu().
						getMenu(JVMenu.MEDIA));
				m_wndFrame.getToolbar().removeVMediaButtons();
			}
			if(!JViewer.isPowerPrivEnabled()){
				m_wndFrame.getWindowMenu().getMenuBar().remove(m_wndFrame.getWindowMenu().
						getMenu(JVMenu.POWER_CONTROL));
				m_fsFrame.getM_menuBar().remove(m_fsFrame.getM_menuBar().getFSMenu().
						getMenu(JVMenu.POWER_CONTROL));
			}
		}
	}

	/**
	 * Get JViewer view.
	 *
	 * @return JViewer view.
	 */
	public JViewerView getRCView() {
		return m_view;
	}

	/**
	 * Get KVM client.
	 *
	 * @return KVMClient
	 */
	public KVMClient getKVMClient() {
		return m_KVMClnt;
	}

	/**
	 * Get Default Menu.
	 *
	 * @return menu handle
	 */
	public JVMenu getJVMenu() {
		if (m_wndMode)
			return (((JVFrame) m_wndFrame).getMenu());
		else
			return (((JVFrame) m_fsFrame).getMenu());
	}

	/**
	 * Get main window.
	 *
	 * @return main window
	 */
	public JVFrame getMainWindow() {
		if (m_wndMode)
			return ((JVFrame) m_wndFrame);
		else
			return ((JVFrame) m_fsFrame);
	}

	/**
	 * Get encryption token.
	 *
	 * @return encryption token.
	 */
	public String getToken() {
		return m_encToken;
	}

	/**
	 * Get session token.
	 *
	 * @return session token.
	 */
	public String getSessionToken() {
		return m_session_token;
	}

	/**
	 * Get session token type.
	 *
	 * @return session token type.
	 */
	public int getSessionTokenType() {
		return m_session_token_type;
	}

	/**
	 * Set status.
	 *
	 * @param status
	 *            message.
	 */
	public void setStatus(String msg) {
		m_frame.setStatus(msg);
	}

	/**
	 * Reset status.
	 */
	public void resetStatus() {
		m_frame.resetStatus();
	}

	/**
	 * Return the Windows is in fullscreen or not
	 * @return
	 */
	public boolean isFullScreenMode() {
		return !m_wndMode;
	}

	/**
	 * Refresh application window title with new values
	 *
	 * @param additional
	 *            label
	 */
	public void refreshAppWndLabel() {
		try {
			m_frame.refreshTitle();
		} catch (Exception e) {
			Debug.out.println("Not able to refresh the title");
			Debug.out.println(e);
		}
	}

	/**
	 * Set application window title
	 *
	 * @param additional
	 *            label
	 */
	public void setAppWndLabel(String label) {
		try {
			m_frame.setWndLabel(label);
		} catch (Exception e) {
			Debug.out.println("Not able to set the Window Label");
			Debug.out.println(e);
		}
	}

	/**
	 * Return the redirectio state
	 *
	 * @return
	 */
	public int GetRedirectionState() {
		return m_RedirectionState;
	}
	/**
	 * Set the kvm redirection status.
	 * @param status - any one among (REDIR_STOPPED, REDIR_STARTING, REDIR_STARTED, REDIR_STOPPING, REDIR_PAUSING). 
	 */
	public void setRedirectionStatus(int status){
		m_RedirectionState = status;
	}

	/**
	 * @return the powerStatus
	 */
	public byte getPowerStatus() {
		return powerStatus;
	}

	public void setPowerStatus(byte status) {
		powerStatus = status;
	}

	public void Ondisplayvideo(String ip,String webPort, String sessionCookies, int secureConnect) {

		//Assigning the window frame to the common frame object
		m_frame = m_wndFrame;
		//Setting the scroll paneview Enable/Disable the Viewer pane display
		m_wndFrame.attachView();
		//Attaching the created panel to the frame Basedd on the application standalone or Multiviewer
		attachFrame();
		OnVideoRecordStartRedirection( ip, webPort, sessionCookies, secureConnect);
	}

	public void initilizeJVVideo() {
		// Creating the JVVideo object for process the video data rendering
		vidClnt = new JVVideo();
		setVidClnt(vidClnt); // Setting the Video client object for accesing in
								// SOC package
		sockvmclient.SetVidoclnt(vidClnt);
	}
	/**
	 * Start video redirection request handler
	 * @param secureconnect
	 */
	public void OnVideoRecordStartRedirection(String ip, String webPort,String sessionCookies, int secureConnect) {
		Debug.out.println("OnVideoRecordStartRedirection");
		m_RedirectionState = REDIR_STARTING;

		m_KVMClnt = new KVMClient(m_serverIP, m_serverPort, vidClnt, m_bUseSSL);
		sockvmclient = JViewerApp.getSoc_manager().getSOCKvmClient();

		sockvmclient.SetKVMClient(m_KVMClnt);
		sockvmclient.SOCKVM_reader();
		initilizeJVVideo();

		if(JViewer.isdownloadapp())
			m_videorecord = new VideoRecord();

		//Invoke the Video Record Object to save the video in client
		videorecordapp = new VideoRecordApp();

		JVFrame.setServerIP(m_serverIP, m_RedirectionState);
		// start the video record
		if (-1 == videorecordapp.startVideorecordRedirection( ip, webPort, secureConnect, sessionCookies)) {
			JViewerApp.getInstance().getM_frame().windowClosed();

		}
	}

	/**
	 * Launch the JViewer in Stand Alone Mode
	 * @param hostIP - IP address of the host.
	 * @param username - log in id required for user authentication.
	 * @param password - password required for user authentication.	 * 
	 */
	public void onLaunchStandAloneApp(String hostIP, int webPort, String username, String password){
		connectionDialog = new StandAloneConnectionDialog(null, hostIP, webPort, username, password);		
	}

	/**
	 * @return the connectionDialog
	 */
	public StandAloneConnectionDialog getConnectionDialog() {
		return connectionDialog;
	}

	/**
	 * Sets the stand alone connection dialog object.
	 * @param dialog - The stand aloen connection dialog.
	 */
	public void setConnectionDialog(StandAloneConnectionDialog dialog) {
		connectionDialog = dialog;
	}
	/**
	 * @return the singlePortKvm
	 */
	public SinglePortKVM getSinglePortKvm() {
		return singlePortKvm;
	}

	/**
	 * Connect to server request handler.
	 */
	public void OnConnectToServer(String serverIP, int serverPort,
			String token, int token_type, boolean bUseSSL, boolean bVMUseSSL,
			int cdserver_port, int hdserver_port,byte num_cd,byte num_hd,
			int cdstatus,int hdstatus, String webSessionTok,int webSecPort) {

		m_serverIP = JViewer.getServerIP(serverIP);
		m_serverPort = serverPort;
		m_encToken = token;
		m_session_token = token;
		m_session_token_type = token_type;
		m_frame = m_wndFrame;
		m_wndMode = true;
		m_bUseSSL = bUseSSL;
		if(JViewer.isSinglePortEnabled())
		{
			singlePortKvm = new SinglePortKVM( serverIP, serverPort, webSecPort,m_bUseSSL);
			singlePortKvm.startConnect();

		}
		else
		{
			m_cdPort = cdserver_port;
			m_hdPort = hdserver_port;
		}

		m_cdStatus = cdstatus;
		m_hdStatus = hdstatus;
		m_bVMUseSSL = bVMUseSSL;
		if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_KVM_MAX_DEVICE_COUNT) == 
			JViewerApp.OEM_KVM_MAX_DEVICE_COUNT)
		{
			setFreeCDNum(JViewer.getKVM_Num_CD());
			setFreeHDNum(JViewer.getKVM_Num_HD());
		}
		else{
			setFreeCDNum(num_cd);
			setFreeHDNum(num_hd);
		}
		m_wndFrame.attachView();
		attachFrame();

		// update menu state
		JVMenu menu = m_frame.getMenu();
		menu.notifyMenuStateEnable(JVMenu.VIDEO_PAUSE_REDIRECTION, false);
		menu.notifyMenuStateEnable(JVMenu.VIDEO_RESUME_REDIRECTION, true);
		menu.notifyMenuStateSelected(JVMenu.VIDEO_FULL_SCREEN, false);
		menu.notifyMenuStateEnable(JVMenu.KEYBOARD_ADD_HOTKEYS,false);
		// start redirection
		OnVideoStartRedirection();
		if((JViewer.getOEMFeatureStatus() & OEM_FIT_TO_HOST_SCREEN) == OEM_FIT_TO_HOST_SCREEN){
			setZoomOption(JVMenu.FIT_TO_HOST_RES);
		}
	}
	/*
	 * Below function used by webpreviewer applet. 
	 * WebPreviewer need connection to start the full video capture.
	 */
	public void OnConnectToServer(String serverIP, int serverPort, int webSecPort, String token, boolean bUseSSL, 
			String webSessionTok) {
		m_serverIP = JViewer.getServerIP(serverIP);
		m_serverPort = serverPort;
		m_encToken = token;
		m_session_token = token;
		m_bUseSSL = bUseSSL;
		if(JViewer.isSinglePortEnabled()){
			singlePortKvm = new SinglePortKVM( serverIP, m_serverPort,webSecPort,bUseSSL);
			singlePortKvm.startConnect();
		}
		// start redirection
		OnVideoStartRedirection();
	}

	/*
	 * Initiates redirection of BSODViewer applet.
	 */
	public void OnConnectToServer(byte[] serverIP) {
		m_serverIP = serverIP;
		// start redirection
		OnVideoStartRedirection();
	}

	/**
	 * 
	 */
	private void attachFrame() {
		if(JViewer.isStandalone()) {
			if( !isM_wndMode()) {
				JViewer.getMainFrame().dispose();
				if(!JViewer.getMainFrame().isDisplayable())
					JViewer.getMainFrame().setUndecorated(true);
				//remove scrollbars
				getMainWindow().m_viewSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				getMainWindow().m_viewSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			} else {
				if(!JViewer.getMainFrame().isDisplayable())
					JViewer.getMainFrame().setUndecorated(false);
			}	
			JViewer.getMainFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
			JViewer.getMainFrame().setContentPane(m_frame);
			//set frame size to resolution width and height.
			Dimension clientRes = getCurrentMonitorResolution();
			JViewer.getMainFrame().setSize(clientRes.width, clientRes.height);
			JViewer.getMainFrame().setVisible(true);
			JViewerApp.getInstance().getRCView().requestFocus(); 
		} else {
			JViewer.getMainPane().add(m_frame);
		}
	}

	/**
	 * 
	 */
	private void detachFrame() {
		if(JViewer.isStandalone()) {
			// Added for JInternalFrame
			JViewer.getMainFrame().getContentPane().remove(m_frame);
			JViewer.getMainFrame().dispose();
		}
	}
	
	/**
	 * Start video redirection request handler
	 */
	public void OnVideoStartRedirection() {
		Debug.out.println("OnVideoStartRedirection");
		m_RedirectionState = REDIR_STARTING;

		if(currentRetryCount <= 0)
		{
			vidClnt = new JVVideo();
			setVidClnt(vidClnt);
		}
		m_KVMClnt = new KVMClient(m_serverIP, m_serverPort, vidClnt, m_bUseSSL);
		sockvmclient = JViewerApp.getSoc_manager().getSOCKvmClient();
		if(currentRetryCount <= 0) 
			sockvmclient.SetVidoclnt(vidClnt);
		sockvmclient.SetKVMClient(m_KVMClnt);
		sockvmclient.SOCKVM_reader();

		//solved Java console button disable when open Java console continuely
		if(!JViewer.isWebPreviewer() && !JViewer.isBSODViewer()) {
			if (!m_KVMClnt.redirection()){
				JViewerApp.getInstance().getM_wndFrame().getToolbar().changeMacrowsStatus(false);
				JViewerApp.getInstance().getM_wndFrame().getM_status().enableStatusBar(false);
			}
		}

		// start redirection
		if(!JViewer.isBSODViewer()){
			if(JViewer.isKVMReconnectEnabled()&& (JViewer.isjviewerapp() || JViewer.isStandAloneApp()))
			{
				/*
				 * During initial connection, the value of the currentRetryCount will be 0,
				 * and it will get incremented to 1. When network loss is detected, and the
				 * JViewer attempts to reconnect the currentRetryCount value will be 1, and it
				 * will range upto N (the configured retry count). So the condition must be 
				 * currentRetryCount <= N.
				 */
				while (currentRetryCount <= JViewer.getRetryCount())
				{
					if(-1 == m_KVMClnt.startRedirection())
					{
						if(InfoDialog.getUndecDialog() == null) {
							InfoDialog.showDialog(JViewer.getMainFrame(), LocaleStrings.getString("D_64_JVAPP"), InfoDialog.UNDECORATED_DIALOG,JViewer.getRetryInterval() );
						}
						//This condition is added to prevent the JViewer from waiting infinitely
						//when the Nth retry also fails.
						if(currentRetryCount < JViewer.getRetryCount()){
							synchronized (retryCountSync) {
								try {
									retryCountSync.wait(); // wait for Nth retry time interval to complete.
									// Where 1 < N < RetryCount
								} catch (InterruptedException e) {
									Debug.out.println(e);
								}
							}
						}
						currentRetryCount++;
						continue;
					}
					else
						break;
				}

				//The reconnect failure message should be shown only after the Nth retry attempt
				//also is completed. So the condition must be currentRetryCount > N (the configured retry count).
				if (currentRetryCount > JViewer.getRetryCount()){
					Debug.out.println("Maximum number of retry to reconnect is reached");
					m_RedirectionState = REDIR_STOPPED;
					if(!(JViewer.isWebPreviewer() || JViewer.isBSODViewer())) {
						JOptionPane.showMessageDialog(m_frame, LocaleStrings.getString("D_66_JVAPP"), 
								LocaleStrings.getString("D_4_JVAPP"), JOptionPane.ERROR_MESSAGE);
						JViewerApp.getInstance().getM_frame().windowClosed();
					} else{
						JViewerApp.getInstance().setWebPreviewerCaptureStatus(WEB_PREVIEWER_CONNECT_FAILURE);
					}
				}
			}
			else
			{
				if (-1 == m_KVMClnt.startRedirection()){
					m_RedirectionState = REDIR_STOPPED;
					if(!(JViewer.isWebPreviewer() || JViewer.isBSODViewer())) {
						JOptionPane.showMessageDialog(m_frame, LocaleStrings.getString("D_3_JVAPP"), 
								LocaleStrings.getString("D_4_JVAPP"), JOptionPane.ERROR_MESSAGE);
						JViewerApp.getInstance().getM_frame().windowClosed();
					} else{
					JViewerApp.getInstance().setWebPreviewerCaptureStatus(WEB_PREVIEWER_CONNECT_FAILURE);
					}
				}
			}
		}
		if(!JViewer.isWebPreviewer() && !JViewer.isBSODViewer())
		{

		JVFrame.setServerIP(m_serverIP, m_RedirectionState);
		//update menu state
		updateKVMMenuOnReconnect(true);
		//enable auto detect keyboard layout by default. 
		if(JViewer.getKeyboardLayout().equalsIgnoreCase(JViewer.AUTO_DETECT_KEYBOARD))
			onAutoKeyboardLayout(true,false);
		m_view.addKeyListener();
		// update menu state
		JVMenu menu = m_frame.getMenu();
		menu.notifyMenuStateEnable(JVMenu.VIDEO_PAUSE_REDIRECTION, true);
		menu.notifyMenuStateEnable(JVMenu.VIDEO_RESUME_REDIRECTION, false);
		m_RedirectionState = REDIR_STARTED;
		if (!m_KVMClnt.redirection()){
			String commonExceptionList[] = {JVMenu.VIDEO_EXIT,JVMenu.HELP_ABOUT_RCONSOLE,JVMenu.VIDEO_FULL_SCREEN};
			String exceptionList[]= getExceptionMenuList(commonExceptionList);
			m_frame.getMenu().enableMenu(exceptionList,true, true);
			JViewerApp.getInstance().getM_wndFrame().getToolbar().changeMacrowsStatus(false);
			JViewerApp.getInstance().getM_wndFrame().getM_status().enableStatusBar(false);
		}
		//start the system time monitor thread.
			m_frame.getMenu().SetMenuSelected(JVMenu.OPTIONS_GUI_LANGUAGE_LOCALE+JViewer.getLanguage(), true);
			//If redirection is paused by user, maintain the paused state. This is to fix the issue during reconnect.
			if(m_userPause){
				OnVideoPauseRedirection();
			}
		}			
	}

	/**
	 * Stop video redirection request handler
	 */
	public void OnVideoStopRedirection() {
		Debug.out.println("OnVideoStopRedirection");
		if(m_RedirectionState == REDIR_STOPPED)
			return;
		m_RedirectionState = REDIR_STOPPING;
		m_KVMClnt.Stop_Cmd_Redirection();
		if(JViewer.isjviewerapp() || JViewer.isStandAloneApp())
		{
			if(getSoftKeyboard() != null)
			{
				getSoftKeyboard().closeSoftKeyboard();
			}
			JVFrame.setServerIP(null, m_RedirectionState);
			OnUSBMouseSyncCursor(false);
			// update menu state
			m_frame.getMenu().notifyMenuStateEnable(JVMenu.VIDEO_PAUSE_REDIRECTION,	false);
			m_frame.getMenu().notifyMenuStateEnable(JVMenu.VIDEO_RESUME_REDIRECTION, true);

			if (isFullScreenMode()) {
				OnVideoFullScreen(false);
			}
		}
		if(!JViewer.isdownloadapp() && !JViewer.isplayerapp()){
			if(JViewer.isSinglePortEnabled() && JViewer.isStandAloneApp())
				getConnectionDialog().logoutWebSession();
			m_KVMClnt.stopRedirection();
			m_view.removeKMListener();
			m_RedirectionState = REDIR_STOPPED;
		}
	}

	/**
	 * @return the m_userPause
	 */
	public boolean isM_userPause() {
		return m_userPause;
	}

	/**
	 * @param m_userPause the m_userPause to set
	 */
	public void setM_userPause(boolean m_userPause) {
		this.m_userPause = m_userPause;
	}

	private void changeMenuItemsStatusOnPauseResume(JVMenu menu, boolean status) {
		Set<String>  set =JVMenu.m_menuItems_setenabled.keySet();
		Iterator<String> itr = set.iterator();
		String str;
		while (itr.hasNext()) {
			str = itr.next();
			if(JViewerApp.getOEMManager().getOEMJVMenu().oemchangeMenuItemsStatusOnPauseResume(str) == IOEMManager.OEM_CUSTOMIZED) {
				continue;
			}
			if(JVMenu.VIDEO_RESUME_REDIRECTION == str ) {
				menu.notifyMenuStateEnable(str,!status);
			}
			else if(JVMenu.VIDEO_CAPTURE_SCREEN == str || JVMenu.VIDEO_FULL_SCREEN == str ||
					JVMenu.VIDEO_EXIT == str ||  JVMenu.HELP_ABOUT_RCONSOLE == str ||
					str.startsWith(JVMenu.OPTIONS_GUI_LANGUAGE_LOCALE))
			{
				continue;
			}
			else if(JVMenu.VIDEO_RECORD_START == str
					|| JVMenu.VIDEO_RECORD_SETTINGS ==str
					|| JVMenu.VIDEO_RECORD_STOP == str)  {


				//Checks whether video redirection pause is user initiated.
				if(JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.VIDEO_RECORD_STOP).isEnabled()&& isM_userPause()) {

					JViewerApp.getInstance().getM_videorecord().OnVideoRecordStop();
					InfoDialog.showDialog(m_frame, LocaleStrings.getString("D_5_JVAPP"),
							LocaleStrings.getString("D_6_JVAPP"),
							InfoDialog.ERROR_DIALOG);
					menu.notifyMenuStateEnable(JVMenu.VIDEO_RECORD_STOP,false);
				}
				else {
					//if Video recording is in progress don't change menu status.
					if(VideoRecord.Recording_Started || VideoRecord.Record_Processing)
						continue;
					//Change the state of Video Record Start menu, only if it is enabled, setting menu is always updated in this case. 
					else if(JVMenu.VIDEO_RECORD_SETTINGS == str && 
							JViewerApp.getInstance().getMainWindow().getMenu().getMenuItem(JVMenu.VIDEO_RECORD_START).isEnabled()){
						menu.notifyMenuStateEnable(str,status);
					}
					else if (JVMenu.VIDEO_RECORD_SETTINGS == str) {
						menu.notifyMenuStateEnable(str,status);
					}
					else if(JVMenu.VIDEO_RECORD_START == str)
					{
						menu.notifyMenuStateEnable(str,status);
					}
				}
				continue;
			}
			else if(JVMenu.CALIBRATEMOUSETHRESHOLD ==str) {

				if(JViewerApp.getInstance().getM_view().m_USBMouseMode == USBMouseRep.RELATIVE_MOUSE_MODE){
					menu.notifyMenuStateEnable(str,status);
				}
				else{
					menu.notifyMenuStateEnable(str,false);
				}
				continue;
			}
			else if(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK == str || JVMenu.VIDEO_HOST_DISPLAY_LOCK == str){
				if(status){//On resume, set the current status of host lock
					changeHostDisplayLockStatus(getKVMClient().getHostLockStatus());
				}
				else{// On pause, disable the menu item.
					menu.getMenuItem(str).setEnabled(status);
				}
				continue;
			}

			// Maintane the power menu status on pause and resume.
			else if(JVMenu.POWER_ON_SERVER == str  && powerStatus == SERVER_POWER_ON){
				// If power status is ON then we don't need to enable the POWER ON SERVER menu item.
				// When power status is OFF, pause and resume operations won't happen. So there is no need
				//to handle that case.

				if(status)
					continue;
			}
			else {
				if(status){
					try{
						if(menu.getMenuItem(str) != null && menu.getMenuEnable(str) != null)
							menu.getMenuItem(str).setEnabled(menu.getMenuEnable(str));
					}catch(Exception e){
						Debug.out.println(e);
					}
				}
				else{
					try{
						if(menu.getMenuItem(str) != null)
							menu.getMenuItem(str).setEnabled(status);
					}catch(Exception e){
						Debug.out.println(e);
					}
				}
			}
		}
		if(status){
			if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_PARTIAL)
				OnChangeMenuState_KVMPartial(menu, !status);
			//set zoom options
			getVidClnt().setZoomOptionStatus();
		}
	}

	/**
	 * Pause video redirection request handler
	 */
	public void OnVideoPauseRedirection() {
		if (m_RedirectionState != JViewerApp.REDIR_STARTED)
			return;
		
		Debug.out.println("OnVideoPauseRedirection");
		m_RedirectionState = REDIR_PAUSING;
		m_KVMClnt.pauseRedirection();
		if(!JViewer.isWebPreviewer() || !JViewer.isBSODViewer())
		{
			JVFrame.setServerIP(null, m_RedirectionState);
			// Added for MultiViewer
			JViewerApp.getInstance().refreshAppWndLabel();
			
			// update menu state
			changeMenuItemsStatusOnPauseResume(m_frame.getMenu(), false);
			JViewerApp.getInstance().getM_wndFrame().getToolbar().updatePlayPauseButton(true);

			//To change status bar Macrows state
			JViewerApp.getInstance().getM_wndFrame().getToolbar().changeMacrowsStatusOnPauseResume(false);
			if(getInstance().getSoftKeyboard() != null)
				getInstance().getSoftKeyboard().OnUpdateKeyState(false);
		}
		m_RedirectionState = REDIR_PAUSED;
	}

	/**
	 * Resume video redirection request handler
	 */
	public void OnVideoResumeRedirection() {
		Debug.out.println("OnVideoResume");

		m_RedirectionState = REDIR_STARTING;
		m_KVMClnt.resumeRedirection();
		if(!JViewer.isWebPreviewer() || !JViewer.isBSODViewer())
		{
			JVFrame.setServerIP(m_serverIP, m_RedirectionState);// (Add Resume status)
			JViewerApp.getInstance().refreshAppWndLabel();
		
			setM_userPause(false);
			changeMenuItemsStatusOnPauseResume(m_frame.getMenu(), true);

			if (m_view.GetUSBMouseMode() == USBMouseRep.RELATIVE_MOUSE_MODE) {
				m_view.m_mouseListener.splitandsend(-socframeHdr.getwidth(),
						-socframeHdr.getheight(), true);
			}
			JViewerApp.getInstance().getM_wndFrame().getToolbar().updatePlayPauseButton(false);
		
			//To change status bar Macrows state
			JViewerApp.getInstance().getM_wndFrame().getToolbar().changeMacrowsStatusOnPauseResume(true);
			if(getInstance().getSoftKeyboard() != null)
				getInstance().getSoftKeyboard().OnUpdateKeyState(true);
		}
		m_RedirectionState = REDIR_STARTED;
	}

	/**
	 * Refresh video request handler
	 */
	public void OnVideoRefreshRedirection() {
		Debug.out.println("OnVideoRefresh");
		if(JViewer.isjviewerapp() || JViewer.isStandAloneApp()){
		if (m_RedirectionState == REDIR_STOPPED) {
			if (m_KVMClnt.m_isBlank == true) {
				OnVideoResumeRedirection();
				OnVideoPauseRedirection();
			} else {
				m_refresh = true;
				OnVideoResumeRedirection();
			}
		} else {
			OnVideoPauseRedirection();
			OnVideoResumeRedirection();
		}
		}
	}

	/**
	 * Captures the current screen and saves it to the client system.
	 */
	public void onVideoCaptureScreen(){
		BufferedImage capturedFrame = null;
		//For jviewer and standalone app get image from view
		//write the currently redirected image into a file
		if(JViewer.isjviewerapp() || JViewer.isStandAloneApp()){
			capturedFrame = copyScreenBuffer(JViewerApp.getInstance().getRCView().getImage());
		}
		//For BSOD  app get image from buffer
		else{
			capturedFrame = copyScreenBuffer(JViewerApp.getInstance().getPrepare_buf().getM_image());
		}
		CaptureScreen captureScreen = new CaptureScreen(capturedFrame);
		captureScreen.start();
	}
	
	/**
	 * Create a copy of the content of the source BufferedImage.
	 * @param sourceImage - the source BufferedImage object to be copied.
	 * @return A copy of the source BufferedImage
	 */
	public BufferedImage copyScreenBuffer(BufferedImage sourceImage){
		ColorModel colorModel = sourceImage.getColorModel();
		boolean isAlphaPremultiplied = sourceImage.isAlphaPremultiplied();
		WritableRaster raster = sourceImage.copyData(null);
		return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
	}

	/**
	 * Full screen request handler
	 */
	public void OnVideoFullScreen(boolean state) {
		Debug.out.println("OnVideoFullScreen");

		if (m_wndMode) {
			m_wndFrame.detachView();
			detachFrame();
			m_frame = m_fsFrame;
			m_wndMode = false;
			m_fsFrame.attachView();
			attachFrame();
			m_fsFrame.showWindow();
			//Reload the user defined macro menus, when switching to full screen mode.
			HashMap<String, String> macroMap = null;
			if(addMacro != null)
				macroMap = getAddMacro().getMacroMap();
			if(macroMap != null){
				// Remove user defined macros.
				getAddMacro().removeMacroMenu();
				// Add user defined macros.
				getAddMacro().addMacroMenu(macroMap);
			}

			/*Disable Keyboard, Mouse, and Keyboard Layout menus and the 
			 * Keyboard/Mouse encryption menu item while partial access 
			 * is given to the concurrent session in KVM sharing.
			 */
			if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_PARTIAL )	
				OnChangeMenuState_KVMPartial(m_fsFrame.getMenu(), false);
			m_frame.getMenu().notifyMenuStateEnable(JVMenu.FIT_TO_CLIENT_RES, false);
			m_frame.getMenu().notifyMenuStateEnable(JVMenu.FIT_TO_HOST_RES, false);
		} else {
			m_fsFrame.detachView();
			detachFrame();
			m_fsFrame.hideWindow();
			m_frame = m_wndFrame;
			m_wndMode = true;
			m_wndFrame.attachView();
			attachFrame();
			//Reload the user defined macro menus, when switching to window mode.
			HashMap<String, String> macroMap = null;
			if(addMacro != null)
				macroMap = getAddMacro().getMacroMap();
			if(macroMap != null){
				// Remove user defined macros.
				getAddMacro().removeMacroMenu();
				// Add user defined macros.
				getAddMacro().addMacroMenu(macroMap);
			}

			/*Disable Keyboard, Mouse, and Keyboard Layout menus and the 
			 * Keyboard/Mouse encryption menu item and disable the 
			 * Keyboard, Mouse, and Hotkey button on the toolbar,while partial 
			 * access is given to the concurrent session in KVM sharing.
			 */
			if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_PARTIAL && !KVMShareDialog.isMasterSession){
				OnChangeMenuState_KVMPartial(m_wndFrame.getMenu(), false);
				getM_wndFrame().getToolbar().OnChangeToolbarIconState_KVMPartial();
			}

			if(JViewerApp.getInstance().getRCView().GetUSBMouseMode() == USBMouseRep.OTHER_MOUSE_MODE){
				GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
				JViewer.getMainFrame().setMaximizedBounds(graphEnv.getMaximumWindowBounds());
				JViewer.getMainFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
			}
			getVidClnt().setZoomOptionStatus();
			//set the zoom options to ACTUAL_SIZE when switching back to window mode
			onChangeZoomOptions(JVMenu.ACTUAL_SIZE);
			getJVMenu().notifyMenuStateSelected(JVMenu.ACTUAL_SIZE, true);
		}

		// For updating the Window label status in Pause state
		if (m_frame.getMenu().getMenuItem(JVMenu.VIDEO_PAUSE_REDIRECTION).isEnabled()) {
			JViewerApp.getInstance().setAppWndLabel("0 fps");
		}

		m_frame.getMenu().notifyMenuStateSelected(JVMenu.VIDEO_FULL_SCREEN, state);
		m_frame.getMenu().notifyMenuStateEnable(JVMenu.VIDEO_FULL_SCREEN, true);
		m_frame.getMenu().refreshMenu();
		//Dispose user macro dialog, if open, while switching between
		// full screen mode and window mode.  
		if(userDefMacro != null && userDefMacro.isAddMacro()){
			userDefMacro.onCloseDialog();
		}
		//Maintain menu mneumonics and accelerartor status when switching between fullscreen and normal mode.
		if(JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_FULL_KEYBOARD) != null){
			JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.KEYBOARD_FULL_KEYBOARD).setSelected(isFullKeyboardEnabled());
			JViewerApp.getInstance().getJVMenu().enableMenuAccelerator(isFullKeyboardEnabled());
			JViewerApp.getInstance().getJVMenu().enableMenuMnemonics(isFullKeyboardEnabled());
		}
		JViewerApp.getInstance().getJVMenu().updateUserMenu();
	}

	/**
	 * Hold right control key request handler
	 */
	public void OnKeyboardHoldRightCtrlKey(boolean state) {
		Debug.out.println("OnKeyboardHoldRightCtrlKey");

		if (!m_KVMClnt.redirection())
			return;

		m_USBKeyRep.set(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT, state);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		// update menu option
		m_frame.getMenu().notifyMenuStateSelected(JVMenu.KEYBOARD_RIGHT_CTRL_KEY, state);
		
		if(state)
			this.getM_wndFrame().getM_status().getRightCtrl().setForeground(Color.red);
		else
			this.getM_wndFrame().getM_status().getRightCtrl().setForeground(Color.gray);
		this.getM_wndFrame().getM_status().getRightCtrl().setSelected(state);

		if(getSoftKeyboard() != null) {
			getSoftKeyboard().syncHoldKey();
		}
	}

	/**
	 * Hold right alt key request handler
	 */
	public void OnKeyboardHoldRightAltKey(boolean state) {
		Debug.out.println("OnKeyboardHoldRightAltKey");

		if (!m_KVMClnt.redirection())
			return;

		m_USBKeyRep.set(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT, state);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		// update menu option
		m_frame.getMenu().notifyMenuStateSelected(
				JVMenu.KEYBOARD_RIGHT_ALT_KEY, state);
		//update Status bar
		if(state)
			this.getM_wndFrame().getM_status().getRightAlt().setForeground(Color.red);
		else
			this.getM_wndFrame().getM_status().getRightAlt().setForeground(Color.gray);
		this.getM_wndFrame().getM_status().getRightAlt().setSelected(state);

		if(getSoftKeyboard() != null) {
			getSoftKeyboard().syncHoldKey();
		}
	}

	/**
	 * Hold left shift key request handler
	 */
	public void OnKeyboardHoldLeftShiftKey(boolean state) {
		Debug.out.println("OnKeyboardHoldLeftShiftKey");

		if (!m_KVMClnt.redirection())
			return;

		m_USBKeyRep.set(KeyEvent.VK_SHIFT, KeyEvent.KEY_LOCATION_LEFT, state);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
	}

	/**
	 *  Hold right shift key request handler
	 */
	public void OnKeyboardHoldRightShiftKey(boolean state) {
		Debug.out.println("OnKeyboardHoldRightShiftKey");

		if (!m_KVMClnt.redirection())
			return;

		m_USBKeyRep.set(KeyEvent.VK_SHIFT, KeyEvent.KEY_LOCATION_RIGHT, state);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
	}

	/**
	 * Hold left control key request handler
	 */
	public void OnKeyboardHoldLeftCtrlKey(boolean state) {
		Debug.out.println("OnKeyboardHoldLeftCtrlKey");

		if (!m_KVMClnt.redirection())
			return;

		m_USBKeyRep.set(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_LEFT, state);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		// update menu option
		m_frame.getMenu().notifyMenuStateSelected(
				JVMenu.KEYBOARD_LEFT_CTRL_KEY, state);		
		//update Status bar
		if(state)
			this.getM_wndFrame().getM_status().getLeftCtrl().setForeground(Color.red);
		else
			this.getM_wndFrame().getM_status().getLeftCtrl().setForeground(Color.gray);
		this.getM_wndFrame().getM_status().getLeftCtrl().setSelected(state);

		if(getSoftKeyboard() != null) {
			getSoftKeyboard().syncHoldKey();
		}		
	}

	/**
	 * Hold left alt key request handler
	 */
	public void OnKeyboardHoldLeftAltKey(boolean state) {
		Debug.out.println("OnKeyboardHoldLeftAltKey");

		if (!m_KVMClnt.redirection())
			return;

		m_USBKeyRep.set(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_LEFT, state);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		// update menu option
		m_frame.getMenu().notifyMenuStateSelected(JVMenu.KEYBOARD_LEFT_ALT_KEY,
				state);		
		//update Status bar
		if(state)
			this.getM_wndFrame().getM_status().getLeftAlt().setForeground(Color.red);
		else
			this.getM_wndFrame().getM_status().getLeftAlt().setForeground(Color.gray);
		this.getM_wndFrame().getM_status().getLeftAlt().setSelected(state);
				
		if(getSoftKeyboard() != null) {
			getSoftKeyboard().syncHoldKey();
		}
	}

	/**
	 * Left windows key hold down request handler
	 */
	public void OnKeyboardLeftWindowsKeyHoldDown(boolean state) {
		Debug.out.println("OnKeyboardLeftWindowsKeyHoldDown");

		if (!m_KVMClnt.redirection())
			return;

		m_USBKeyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_LEFT, state);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		// update menu option
		m_frame.getMenu().notifyMenuStateSelected(
				JVMenu.KEYBOARD_LEFT_WINKEY_PRESSHOLD, state);
		
		if(getSoftKeyboard() != null) {
			getSoftKeyboard().syncHoldKey();
		}	
	}

	/**
	 * Left windows key press and release request handler
	 */
	public void OnKeyboardLeftWindowsKeyPressRelease() {
		Debug.out.println("OnKeyboardLeftWindowsKeyPressRelease");

		if (!m_KVMClnt.redirection())
			return;

		m_USBKeyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_LEFT, true);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_LEFT, false);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
	}

	/**
	 * Right windows key hold down request handler
	 */
	public void OnKeyboardRightWindowsKeyHoldDown(boolean state) {
		Debug.out.println("OnKeyboardRightWindowsKeyHoldDown");

		if (!m_KVMClnt.redirection())
			return;

		m_USBKeyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_RIGHT, state);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		// update menu option
		m_frame.getMenu().notifyMenuStateSelected(JVMenu.KEYBOARD_RIGHT_WINKEY_PRESSHOLD, state);
		
		if(getSoftKeyboard() != null) {
			getSoftKeyboard().syncHoldKey();
		}
	}

	/**
	 * Right windows key press and release request handler
	 */
	public void OnKeyboardRightWindowsKeyPressRelease() {
		Debug.out.println("OnKeyboardRightWindowsKeyPressRelease");

		if (!m_KVMClnt.redirection())
			return;

		m_USBKeyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_RIGHT, true);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(KeyEvent.VK_WINDOWS, KeyEvent.KEY_LOCATION_RIGHT, false);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
	}

	/**
	 * Alt ctrl del request handler
	 */
	public void OnKeyboardAltCtrlDel() {
		Debug.out.println("OnKeyboardAltCtrlDel");

		if (!m_KVMClnt.redirection())
			return;

		m_USBKeyRep.set(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_LEFT, true);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_LEFT, true);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(KeyEvent.VK_DELETE, KeyEvent.KEY_LOCATION_STANDARD,	true);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(KeyEvent.VK_DELETE, KeyEvent.KEY_LOCATION_STANDARD, false);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_LEFT, false);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_LEFT, false);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);

	}

	/**
	 * Method to send the Context Menu key Event
	 *
	 */
	public void OnKeyboardContextMenu() {
		// TODO Auto-generated method stub
		m_USBKeyRep.set(KeyEvent.VK_CONTEXT_MENU, KeyEvent.KEY_LOCATION_STANDARD, true);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
		m_USBKeyRep.set(KeyEvent.VK_CONTEXT_MENU, KeyEvent.KEY_LOCATION_STANDARD, false);
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
	}

	/**
	 * Reset the Modifiers
	 */
	public void resetModifiers(){
		byte modifiers = USBKeyProcessorEnglish.getModifiers();
		if((modifiers & KeyProcessor.MOD_LEFT_CTRL) == KeyProcessor.MOD_LEFT_CTRL &&
		!JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_LEFT_CTRL_KEY)){
			JViewerApp.getInstance().OnKeyboardHoldLeftCtrlKey(false);
		}
		if((modifiers & KeyProcessor.MOD_RIGHT_CTRL) == KeyProcessor.MOD_RIGHT_CTRL &&
				!JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_RIGHT_CTRL_KEY)){
			JViewerApp.getInstance().OnKeyboardHoldRightCtrlKey(false);
		}
		if(((modifiers & KeyProcessor.MOD_LEFT_ALT) == KeyProcessor.MOD_LEFT_ALT)&&
				!JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_LEFT_ALT_KEY)){
			JViewerApp.getInstance().OnKeyboardHoldLeftAltKey(false);
		}
		if(((modifiers & KeyProcessor.MOD_RIGHT_ALT) == KeyProcessor.MOD_RIGHT_ALT)&&
				!JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.KEYBOARD_RIGHT_ALT_KEY)){
			JViewerApp.getInstance().OnKeyboardHoldRightAltKey(false);
		}
		if(((modifiers & KeyProcessor.MOD_LEFT_SHIFT) == KeyProcessor.MOD_LEFT_SHIFT)){
			JViewerApp.getInstance().OnKeyboardHoldLeftShiftKey(false);
		}
		if(((modifiers & KeyProcessor.MOD_RIGHT_SHIFT) == KeyProcessor.MOD_RIGHT_SHIFT)){
			JViewerApp.getInstance().OnKeyboardHoldRightShiftKey(false);
		}
		USBKeyProcessorEnglish.setModifiers(modifiers);
	}

	/**
	 * Sending Macro keyevent to the Host
	 * @param string
	 */
	public void OnsendMacrokeycode(String string) {
		// TODO Auto-generated method stub		
		String[] keycodesplit = string.split("[+]");
		for(int j=0;j<keycodesplit.length;)
		{
			int keycode = Integer.parseInt(keycodesplit[j++]);
			int keylocation = Integer.parseInt(keycodesplit[j++]);
			sendKeyEvent( keycode, keylocation, true);
		}
		for(int j=0;j<keycodesplit.length;)
		{
			int keycode = Integer.parseInt(keycodesplit[j++]);
			int keylocation = Integer.parseInt(keycodesplit[j++]);
			sendKeyEvent( keycode, keylocation, false);
		}
	}

	public void sendKeyEvent(int keycode,int keylocation,boolean status){
		if (!m_KVMClnt.redirection()) return;

		m_USBKeyRep.set(keycode, keylocation, status );
		m_KVMClnt.sendKMMessage(m_USBKeyRep);
	}

	/**
	 * soft keyboard request handler
	 */

	public void OnSkbrdDisplay(int langindex) {
		if (softKeyboard != null) {
			softKeyboard.m_skmouselistener.close();
			softKeyboard.dispose();
		}
		if(langindex == -1 ) {
			if(softKeyboard != null) {
				softKeyboard.setVisible(false);
				softKeyboard =null;
			}
		}
		else {
			softKeyboard = new SoftKeyboard(langindex,JViewer.getMainFrame());
			// Enable/disable key's in softkeyboard depends on power state
			if(powerStatus == SERVER_POWER_ON)
				softKeyboard.OnUpdateKeyState(true);
			else
				softKeyboard.OnUpdateKeyState(false);
		}
	}

	/**
	 * Encryption status message handler. Encryption status message is received
	 * from server when a client enables keyboard/mouse encryption.
	 */
	public void OnEncryptionStatus(){
		if( !m_frame.getMenu().getMenuItem(JVMenu.OPTIONS_KEYBOARD_MOUSE_ENCRYPTION).isEnabled()){
			m_frame.getMenu().notifyMenuStateEnable(JVMenu.OPTIONS_KEYBOARD_MOUSE_ENCRYPTION, true);
		}

		// ignore encryption notification if encryption is enabled by this  client.
		if (m_KVMClnt.isKMEncryptionEnabled())
			return;

		// notify KVM client
		m_KVMClnt.notifyEncryption(true);
		// update menu option
		m_frame.getMenu().notifyMenuStateSelected(JVMenu.OPTIONS_KEYBOARD_MOUSE_ENCRYPTION, true);
		if (KVMClient.DISABLE_ENCRPT_FLAG) {
			InfoDialog.showDialog(m_frame, LocaleStrings.getString("D_11_JVAPP"),
					LocaleStrings.getString("D_12_JVAPP"),
					InfoDialog.INFORMATION_DIALOG);
			KVMClient.DISABLE_ENCRPT_FLAG = false;
			//initilize encryption
			m_KVMClnt.getCrypt().initialize(m_encToken, KMCrypt.ENCRYPT);
		} else {
			// notify the user about change in encryption status
			InfoDialog.showDialog(m_frame, LocaleStrings.getString("D_13_JVAPP"),
					LocaleStrings.getString("D_12_JVAPP"),
					InfoDialog.INFORMATION_DIALOG);
		}
	}

	/**
	 * Initial encryption status message handler. This message is received
	 * during the session establishment if there are any other clients connected
	 * to server with keyboard/mouse encryption enabled. In such case we need to
	 * enable the same for our client also.
	 */
	public void OnInitialEncryptionStatus() {

		// We just notify about the encryption here. As soon as we
		// receive first video frame which carries place holder, we generate
		// encryption key.
		// notify KVM client
		m_KVMClnt.notifyEncryption(true);
		// update menu option
		m_frame.getMenu().notifyMenuStateSelected(JVMenu.OPTIONS_KEYBOARD_MOUSE_ENCRYPTION, true);		
		//initilize encryption
		m_KVMClnt.getCrypt().initialize(m_encToken, KMCrypt.ENCRYPT);
		// notify the user about change in encryption status
		InfoDialog.showDialog(m_frame, LocaleStrings.getString("D_13_JVAPP"),
				LocaleStrings.getString("D_12_JVAPP"),
				InfoDialog.INFORMATION_DIALOG);
	}

	/**
	 * Keyboard encryption request handler
	 */
	public void OnOptionsKeyboardMouseEncryption(boolean state) {
		Debug.out.println("OnOptionsKeyboardMouseEncryption");

		m_KVMClnt.setEncryption(state);
		// update menu option
		m_frame.getMenu().notifyMenuStateSelected(JVMenu.OPTIONS_KEYBOARD_MOUSE_ENCRYPTION, state);
		
		if(m_frame.getMenu().getMenuSelected(JVMenu.OPTIONS_KEYBOARD_MOUSE_ENCRYPTION)){
			m_frame.getMenu().notifyMenuStateEnable(JVMenu.OPTIONS_KEYBOARD_MOUSE_ENCRYPTION, !state);
		}
		
		if (true == state) {
			m_KVMClnt.getCrypt().initialize(m_encToken, KMCrypt.ENCRYPT);
		}
		else {
			m_KVMClnt.getCrypt().close();
		}
	}

	/**
	 * Sync cursor request handler
	 */
	public void OnUSBMouseSyncCursor(boolean state) {
		Debug.out.println("OnUSBMouseSyncCursor");
		JViewerApp.showCursor = true;
		m_view.USBsyncCursor(state);
		m_frame.getMenu().refreshMenu();
	}

	/**
	 * Hide cursor request handler
	 */
	public void OnShowCursor(boolean state) {
		Debug.out.println("OnShowCursor");	
			
		getJVMenu().notifyMenuStateEnable( JVMenu.MOUSE_CLIENTCURSOR_CONTROL, true);
		getJVMenu().notifyMenuStateSelected(JVMenu.MOUSE_CLIENTCURSOR_CONTROL, state);
		
		if (state){
			if(JVMenu.m_scale != 1.0 || zoomOption == JVMenu.FIT_TO_CLIENT_RES){
				if(m_view.GetUSBMouseMode() == USBMouseRep.RELATIVE_MOUSE_MODE){
					showCursor=false;
					getJVMenu().notifyMenuStateSelected(JVMenu.MOUSE_CLIENTCURSOR_CONTROL, false);
					getJVMenu().notifyMenuStateEnable(JVMenu.MOUSE_CLIENTCURSOR_CONTROL, false);
					URL imageURLMouse = com.ami.kvm.jviewer.JViewer.class.getResource("res/Mouse2Btn-gray.png");
					getM_wndFrame().getToolbar().mouseBtn.setIcon(new ImageIcon(imageURLMouse));
					JViewerApp.getInstance().getM_wndFrame().getToolbar().mouseBtn.setToolTipText(LocaleStrings.getString("D_48_JVAPP"));
					return;
				}
			}
			else{
				showCursor=true;
				m_view.ShowCursor(true);
				URL imageURLMouse = com.ami.kvm.jviewer.JViewer.class.getResource("res/Mouse2Btn.png");
				getM_wndFrame().getToolbar().mouseBtn.setIcon(new ImageIcon(imageURLMouse));
				JViewerApp.getInstance().getM_wndFrame().getToolbar().mouseBtn.setToolTipText(LocaleStrings.getString("D_14_JVAPP"));
			}
		}
		else{
			showCursor=false;
			m_view.ShowCursor(false);
			URL imageURLMouse = com.ami.kvm.jviewer.JViewer.class.getResource("res/Mouse2Btn-gray.png");
			getM_wndFrame().getToolbar().mouseBtn.setIcon(new ImageIcon(imageURLMouse));
			if(JVMenu.m_scale != 1.0 || zoomOption == JVMenu.FIT_TO_CLIENT_RES){
				if(m_view.GetUSBMouseMode() == USBMouseRep.RELATIVE_MOUSE_MODE){
					getJVMenu().notifyMenuStateSelected(JVMenu.MOUSE_CLIENTCURSOR_CONTROL, false);
					getJVMenu().notifyMenuStateEnable(JVMenu.MOUSE_CLIENTCURSOR_CONTROL, false);
					JViewerApp.getInstance().getM_wndFrame().getToolbar().mouseBtn.setToolTipText(LocaleStrings.getString("D_48_JVAPP"));
				}
			}
			else
				JViewerApp.getInstance().getM_wndFrame().getToolbar().mouseBtn.setToolTipText(LocaleStrings.getString("D_15_JVAPP"));
		}
	}

	/**
	 * Bandwidth auto detect request handler
	 */
	public void OnOptionsBandwidthAutoDetect() {
		Debug.out.println("OnOptionsBandwidthAutoDetect");
		m_KVMClnt.autoDetect();
		
		if( JViewer.isStandalone() ) {
			m_autoBWDlg = new AutoBWDlg(JViewer.getMainFrame());
			m_autoBWDlg.setVisible(true);
			m_autoBWDlg.setLocation(JViewerApp.getInstance().getPopUpWindowPosition(AutoBWDlg.WIDTH,AutoBWDlg.HEIGHT));
		} else {
			JPanel panel = new JPanel();
			label = new JLabel(LocaleStrings.getString("9_1_BW")+" ...");
			panel.add(label);
			JOptionPane optionPane = new JOptionPane(panel,JOptionPane.PLAIN_MESSAGE);
		    optionPane.setOptions(new Object[] {});
			dialog = optionPane.createDialog(JViewerApp.getInstance().getMainWindow(), LocaleStrings.getString("9_1_BW")+"...");
			dialog.setResizable(false);
		    dialog.setSize(AutoBWDlg.WIDTH, AutoBWDlg.HEIGHT);
		    dialog.setLocation(JViewerApp.getInstance().getPopUpWindowPosition(AutoBWDlg.WIDTH,AutoBWDlg.HEIGHT));
		    dialog.setVisible(true);
		}
	}

	/**
	 * Update bandwidth detection dialog
	 *
	 * @param new
	 *            bandwidth
	 */
	public void updateBandwidthMsg(String newBW) {
		
		if( JViewer.isStandalone()) {
			if ((m_autoBWDlg != null) && (m_autoBWDlg.isVisible())) {
				m_autoBWDlg.setMessage(LocaleStrings.getString("9_1_BW") + " - "+ newBW);
			}

			m_autoBWDlg.done();
		} else {
			try
			 {
				 label.setText(LocaleStrings.getString("9_1_BW") + " - "+newBW);
				 Thread.sleep(1000);
				 label.setText(LocaleStrings.getString("9_3_BW")+"...");
				 Thread.sleep(1000);
				 label.setText(LocaleStrings.getString("D_16_JVAPP")+newBW+LocaleStrings.getString("D_17_JVAPP"));
				 Thread.sleep(1000);
				 dialog.dispose();
			 }
			 catch(Exception e) {
				 Debug.out.println(e);
			 }
		}
	}
	
	public void OnOptionsBandwidth(int bandWidth)
	{
		m_KVMClnt.setBandwidth(bandWidth);
		m_frame.getMenu().SetMenuSelected(JVMenu.previous_bandwidth, false);
		
		switch(bandWidth) {
			case CfgBandwidth.BANDWIDTH_256KBPS:
				Debug.out.println("OnOptionsBandwidth256Kbps");				
				JVMenu.previous_bandwidth = JVMenu.OPTIONS_BANDWIDTH_256KBPS;
				m_frame.getMenu().notifyMenuStateSelected(JVMenu.OPTIONS_BANDWIDTH_256KBPS, true);
				break;
			case CfgBandwidth.BANDWIDTH_512KBPS:
				Debug.out.println("OnOptionsBandwidth512Kbps");
				JVMenu.previous_bandwidth = JVMenu.OPTIONS_BANDWIDTH_512KBPS;
				m_frame.getMenu().notifyMenuStateSelected(JVMenu.OPTIONS_BANDWIDTH_512KBPS, true);
				break;
			case CfgBandwidth.BANDWIDTH_1MBPS:
				Debug.out.println("OnOptionsBandwidth1Mbps");
				JVMenu.previous_bandwidth = JVMenu.OPTIONS_BANDWIDTH_1MBPS;
				m_frame.getMenu().notifyMenuStateSelected(JVMenu.OPTIONS_BANDWIDTH_1MBPS, true);
				break;
			case CfgBandwidth.BANDWIDTH_10MBPS:
				Debug.out.println("OnOptionsBandwidth10Mbps");
				JVMenu.previous_bandwidth = JVMenu.OPTIONS_BANDWIDTH_10MBPS;
				m_frame.getMenu().notifyMenuStateSelected(JVMenu.OPTIONS_BANDWIDTH_10MBPS, true);
				break;
			case CfgBandwidth.BANDWIDTH_100MBPS:
				Debug.out.println("OnOptionsBandwidth100Mbps");	
				JVMenu.previous_bandwidth = JVMenu.OPTIONS_BANDWIDTH_100MBPS;
				m_frame.getMenu().notifyMenuStateSelected(JVMenu.OPTIONS_BANDWIDTH_100MBPS, true);
				break;
		}		
	}

	/**
	 * Invoke the IPMI Command Dialog 
	 */
	public void invokeIPMICommandDialog(){
		//if IPMI dialog is already open, then return
		if(ipmiDialog != null && ipmiDialog.isShowing())
			return;

		ipmiDialog = new IPMICommandDialog(JViewer.getMainFrame());
		ipmiDialog.showDialog();
	}

	/**
	 * @return the ipmiDialog
	 */
	public IPMICommandDialog getIPMIDialog() {
		return ipmiDialog;
	}

	/**
	 * @param ipmiDialog the ipmiDialog to set
	 */
	public void setIPMIDialog(IPMICommandDialog ipmiDialog) {
		this.ipmiDialog = ipmiDialog;
	}

	/**
	 * Send IPMI request command to BMC
	 * @param sequence - sequence number of the command
	 * @param commands - commands to be send
	 * @return 0 if success -1 otherwise
	 */
	public int onSendIPMICommand(byte sequence, byte[] commands){
		IVTPPktHdr IPMICommandHdr = new IVTPPktHdr(IVTPPktHdr.IVTP_IPMI_REQUEST_PKT, commands.length+1, (short)0);
		ByteBuffer IPMICommandBuffer = ByteBuffer.allocate(IPMICommandHdr.size() + commands.length+1);
		byte[] IPMICommandReport;
		IPMICommandBuffer.position(0);
		IPMICommandBuffer.put(IPMICommandHdr.array());
		IPMICommandBuffer.put(sequence);
		IPMICommandBuffer.put(commands);
		IPMICommandBuffer.position(0);
		IPMICommandReport = new byte[IPMICommandBuffer.limit()];
		IPMICommandBuffer.get(IPMICommandReport, 0, IPMICommandBuffer.limit());
		if (IPMICommandReport.length != getKVMClient().sendMessage(IPMICommandReport, IPMICommandReport.length)) {
			Debug.out.println("Failed to send IPMI command");
			return -1;
		}
		return 0;
	}

	/**
	 * Handles the IPMI response message received from BMC
	 * @param responseMessage - response message from BMC
	 * @param responseStatus - status code
	 */
	public void onGetIPMICommandResponse(ByteBuffer responseMessage, int responseStatus){
		String response = new String();
		byte sequence = 0;
		responseMessage.order(ByteOrder.LITTLE_ENDIAN);
		sequence = responseMessage.get();
		if(responseStatus == 0){
			byte[] responseBuffer = new byte[responseMessage.limit()-1];
			responseMessage.order(ByteOrder.LITTLE_ENDIAN);
			responseMessage.position(1);
			responseMessage.get(responseBuffer);

			response = Debug.out.dumpIPMI(responseBuffer, 0, responseBuffer.length);
			if(response.equals("") || response.length() == 0){
				String hexCompletionCode  = Integer.toHexString(responseStatus & 0xFF);
				response = LocaleStrings.getString("D_50_JVAPP")+" : 0x "+hexCompletionCode.toUpperCase();
			}
		}
		else{
			//Show the last byte of the responseStatus as the Completion Code
			String hexCompletionCode  = Integer.toHexString(responseStatus & 0xFF);
			response = LocaleStrings.getString("D_50_JVAPP")+" : 0x "+hexCompletionCode.toUpperCase();
		}
		
		ipmiDialog.onIPMICommandRespose(sequence, response);
	}

	/*
	 * OnGUILanguageChange : To change GUI Text and Menu text language
	 */
	public void OnGUILanguageChange(String lang){

		JViewer.setLanguage(lang);
		Locale locale = new Locale(lang.toLowerCase());
		JComponent.setDefaultLocale(locale);
		getJVMenu().changeMenuLanguage();
		getJVMenu().changeMenuItemLanguage();
		getJVMenu().changeStatusBarLanguage();
		JViewerApp.getInstance().getM_wndFrame().getToolbar().changeToolBarItemLanguage();
		JViewerApp.getInstance().getM_wndFrame().getToolbar().setZoomLabel(getM_wndFrame().getToolbar().getSlider_zoom().getValue());
		JViewerApp.getInstance().getRCView().repaint();
		if(ipmiDialog != null){
			ipmiDialog.closeIPMICommandDialog();
			ipmiDialog = null;
			invokeIPMICommandDialog();
		}
		if(getVMDialog() != null){
			vmDialog.reInitialize();
			VMApp.getInstance().getRedirectionController().updateRedirectionStatus(VMApp.DEVICE_TYPE_CDROM,VMApp.getInstance().getNumCD());
			VMApp.getInstance().getRedirectionController().updateRedirectionStatus(VMApp.DEVICE_TYPE_HD_USB,VMApp.getInstance().getNumHD());	
		}
	}

	/**
	 * Send the full permission request
	 */
	public void onSendFullPermissionRequest(){
		KVMSharing.KVM_CLIENT_USERNAME = null;
		KVMSharing.KVM_CLIENT_IP = null;
		KVMSharing.KVM_CLIENT_SESSION_INDEX = null;
		KVMSharing.KVM_CLIENT_IPMI_PRIV = 0;
		if(getKVMClient().sendKVMFullPermissionRequest() < 0){
			InfoDialog.showDialog(m_frame, LocaleStrings.getString("D_53_JVAPP"),
					LocaleStrings.getString("D_54_JVAPP"), InfoDialog.ERROR_DIALOG);
		}
	}
	public void onGetFullPermissionRequest(short status){		
		fullPermissionRequest = true;
		OnKvmPrevilage(status);
	}

	public void onSendKeepAliveRequest(){
		if(getKVMClient().sendKeepAliveRequest() < 0){
			JViewerApp.getInstance().onReconnect();
		}
	}

	/*
	 * provides coordinates where window should be placed
	 */
	public Point getWindowPostionToSet(){
		Point position = JViewerApp.getOEMManager().getWindowPosition();
		
		// if the position is not null, then oem has customized where jviewer window should be placed.
		if(position != null) {
			return position;
		}

		// if the feature OEM_SKIP_REPOSITION_JVIEWER_WINDOW is enabled then jviewer should be placed in the same position. So return the current location on screen.
		// if the feature is disabled, then caculate the initial position of the current monitor and return.
		if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_SKIP_REPOSITIONING_JVIEWER_WINDOW) == 
				JViewerApp.OEM_SKIP_REPOSITIONING_JVIEWER_WINDOW) {
			return JViewer.getMainFrame().getLocationOnScreen();
		}

		Component owner = JViewer.getMainFrame();
		Point screen = new Point();
		int width = 0;
		int startpoint = 0;

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		
		for(GraphicsDevice curGs : gs)
		{
			DisplayMode dm = curGs.getDisplayMode();

			if(dm != null){
				screen.x = dm.getWidth();
				screen.y = dm.getHeight();
			}

			//Possible of more than 2 monitors
			width += screen.x;
			if(owner.isShowing()){
				if((owner.getLocationOnScreen().x ) < width)
					break;
			}
			startpoint = screen.x+ 1;
			
		}
		screen.x = startpoint;
		//Windows position y co-ordinate should be zero
		screen.y = 0;
		return screen;
	}

	/*
	 * Provided co-ordinate where window pop-up should display
	 */
	public Point getPopUpWindowPosition(int width,int height) {

		Component owner = null;
		int xLoc = 0;
		int yLoc = 0;
		
		owner = JViewer.getMainFrame();
		int xScreen = owner.getLocationOnScreen().x;
		int yScreen = owner.getLocationOnScreen().y;
		Dimension resolution =Toolkit.getDefaultToolkit().getScreenSize();

		//Place pop-up in middle of current monitor
		//xLoc = (xScreen > resolution.width) ? (xScreen + (owner.getWidth() - width)/2) : (((xScreen + owner.getWidth()) - width)/2);
		//Place in-middle of jviewer window
		xLoc =  (xScreen + (owner.getWidth() - width)/2);
		yLoc = (yScreen > resolution.height) ? (yScreen + (owner.getHeight() - height)/2 ): (((yScreen + owner.getHeight()) - height) /2);
		Point P = new Point(xLoc, yLoc);

		return P;
	}
	
	
	/*
	 * returns current monitor resolution
	 */
	public Dimension getCurrentMonitorResolution() {

		Component owner = JViewer.getMainFrame();
		Dimension screen = new Dimension();
		int width = 0;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		
		for(GraphicsDevice curGs : gs)
		{
			DisplayMode dm = curGs.getDisplayMode();

			if(dm != null){
				screen.width = dm.getWidth();
				screen.height = dm.getHeight();
			}

			//Possible of more than 2 monitors
			width += screen.width;
			//Consider the left frame border size also while calculating the frame location on screen.
			//The calculated position could end up in the screen border if we exclude this.
			if(owner.isShowing()){
				if((owner.getLocationOnScreen().x + LEFT_FRAME_BORDER_SIZE) < width)
					break;
			}
			
		}
		initialDimension.setSize(screen.width, screen.height);
		return screen;
	}

	/**
	 * About jviewer request handler
	 */
	public void OnHelpAboutJViewer() {
		Debug.out.println("OnHelpAboutJViewer");
		OEMResourceURLProcessor urlProcessor = new OEMResourceURLProcessor(getM_webSession_token(), JViewer.getIp());
		String copyright = urlProcessor.getOemCopyright();
		ImageIcon logo = urlProcessor.getOemLogo();
		if (copyright != null && copyright.length() > 0){
				String target = "<=socversion=>";
				if (copyright.contains(target)){
					copyright = copyright.replace(target, this.getSoc_manager().getSOCVersion());
				}
				target = "<=soc=>";
				if (copyright.contains(target)){
					copyright = copyright.replace(target, this.getSoc_manager().getSOC());
				}
				target = "<=jviewerversion=>";
				if (copyright.contains(target)){
					copyright = copyright.replace(target, currentVersion);
				}
		}else{
			copyright = LocaleStrings.getString("D_18_JVAPP") + currentVersion + "\n"
						+ LocaleStrings.getString("D_19_JVAPP") + getSoc_manager().getSOCVersion()
						+ LocaleStrings.getString("D_20_JVAPP")+getSoc_manager().getSOC() + "\n"
						+ LocaleStrings.getString("D_21_JVAPP");
		}
		if (logo == null){
			logo = new ImageIcon(com.ami.kvm.jviewer.JViewer.class.getResource("res/ami.jpg"));
		}
		JOptionPane.showMessageDialog(m_frame,
				copyright, LocaleStrings.getString("D_22_JVAPP")+JViewer.getTitle(), JOptionPane.INFORMATION_MESSAGE, logo);

	}
	

	/**
	 * About jviewer active user ahndler
	 */
	public void OnTerminateActiveuser(int userindex) {
		int numUsers = KVMClient.getNumUsers();
		if(userindex >= numUsers)
			return;

		String[] userData = KVMClient.getUserData();
		String index = (userData[userindex].substring(0,userData[userindex].indexOf(":")-1)).trim();
		int sessIndex = Integer.parseInt(index);
		if ( sessIndex == JViewerApp.getInstance().getCurrentSessionId())
			return;

		getKVMClient().SendKillSession(SESSION_TYPE_VKVM, sessIndex);
	}

	/**
	 * Exit request handler
	 */
	public void OnVideoExit() {
		Debug.out.println("OnVideoExit");
		m_frame.exitApp();
	}

	/**
	 * Max Session status message handler. Max Session status message is
	 * received from server when a client count reaches the maximum session
	 * limit.
	 */
	public void onMaxSession() {
		// notify the user about maximum session reached
		JOptionPane.showMessageDialog(m_frame, LocaleStrings.getString("D_23_JVAPP"),
				LocaleStrings.getString("D_24_JVAPP"),
				JOptionPane.INFORMATION_MESSAGE);
		m_frame.exitApp();
	}

	public void OnGetMouseMode(byte mouse_mode) {
		Debug.out.println("Mouse mode response packet received. Mouse Mode:" + mouse_mode);
		m_view.SetUSBMouseMode(mouse_mode);
		/*
		 * If Absolute mode Show Cursor is disabled If Relative mode Show Cursor
		 * Menu Option by default is "Not Selected"
		 */
		OnUSBMouseSyncCursor(true);
		if (mouse_mode == USBMouseRep.RELATIVE_MOUSE_MODE) {
			JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable( JVMenu.CALIBRATEMOUSETHRESHOLD, true);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected( JVMenu.MOUSE_RELATIVE_MODE, true);
			JViewerApp.getInstance().OnShowCursor(true);
			JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected( JVMenu.MOUSE_CLIENTCURSOR_CONTROL, true);
			/*Allow window to be resizable*/
			if(!JViewer.getMainFrame().isResizable() && !isFullScreenMode() && zoomOption == JVMenu.ACTUAL_SIZE ){
				JViewer.getMainFrame().setResizable(true);
				JViewerApp.getInstance().getMainWindow().m_viewSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				JViewerApp.getInstance().getMainWindow().m_viewSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			}
		}
		else if( mouse_mode == USBMouseRep.OTHER_MOUSE_MODE){
				GraphicsConfiguration gc = JViewerApp.getInstance().getM_wndFrame().getGraphicsConfiguration();
				Dimension screenSize = getCurrentMonitorResolution();
				Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
				int screenHeight = screenSize.height;
				int screenWidth = screenSize.width;
				int systemComponentsHeight = screenInsets.top + screenInsets.bottom;
				int systemComponentsWidth = screenInsets.left+screenInsets.right;
				int frameHeight = screenHeight - systemComponentsHeight;
				int frameWidth = screenWidth - systemComponentsWidth;
				JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected(JVMenu.MOUSE_OTHER_MODE, true);
				//Set the scroll pane view position ot initial position.
				
				JViewer.getMainFrame().setLocation(getWindowPostionToSet());
				/*Code to set window as not resizable*/
				JViewerApp.getInstance().getMainWindow().m_viewSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				JViewerApp.getInstance().getMainWindow().m_viewSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
				GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
				JViewer.getMainFrame().setMaximizedBounds(graphEnv.getMaximumWindowBounds());
				JViewer.getMainFrame().setSize(frameWidth, frameHeight);
				JViewer.getMainFrame().setResizable(false);
				JViewerApp.getInstance().OnShowCursor(false);
				JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected( JVMenu.MOUSE_CLIENTCURSOR_CONTROL, false);
				if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_ALLOWED){
					InfoDialog.showDialog(JViewer.getMainFrame(),LocaleStrings.getString("E_7_JVIEW")+LocaleStrings.getString("E_3_JVIEW"),
						LocaleStrings.getString("F_46_JVM"), InfoDialog.INFORMATION_DIALOG);
				}
		}
		else if(mouse_mode == USBMouseRep.ABSOLUTE_MOUSE_MODE){
				/*Allow window to be resizable*/
				if(!JViewer.getMainFrame().isResizable() && !isFullScreenMode() && zoomOption == JVMenu.ACTUAL_SIZE){
					JViewer.getMainFrame().setResizable(true);
					JViewerApp.getInstance().getMainWindow().m_viewSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					JViewerApp.getInstance().getMainWindow().m_viewSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				}
				JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected( JVMenu.MOUSE_ABSOLUTE_MODE, true);
				JViewerApp.getInstance().OnShowCursor(true);
				JViewerApp.getInstance().getJVMenu().notifyMenuStateSelected( JVMenu.MOUSE_CLIENTCURSOR_CONTROL, true);
		}
		getVidClnt().setZoomOptionStatus();
		getM_wndFrame().getM_status().resetStatus();
		JViewerApp.getInstance().getJVMenu().notifyMouseMode(mouse_mode);
	}

	/**
	 *
	 * Method is used to check the Response of the session valid/Invalid
	 *
	 * @param status
	 */

	public void OnValidateVideoSessionResp(byte status,byte sindex) {
		Debug.out.println("OnValidateVideoSessionResp");
		if (status == KVMClient.INVALID_SESSION) {
			m_frame.generalErrorMessage(LocaleStrings.getString("D_25_JVAPP"),
										LocaleStrings.getString("D_26_JVAPP"));
			JViewer.exit(0);
		}
		else if(status == KVMClient.KVM_DISABLED){
			m_frame.generalErrorMessage(LocaleStrings.getString("D_25_JVAPP"),
					LocaleStrings.getString("1_5_JVIEWER"));
			JViewer.exit(0);
		}
		else if(status == KVMClient.INVALID_VIDEO_SESSION_INFO){
			m_frame.generalErrorMessage(LocaleStrings.getString("D_25_JVAPP"),
					LocaleStrings.getString("D_65_JVAPP"));
			JViewer.exit(0);
		}else if(status == KVMClient.INVALID_CLIENT_IP){
			m_frame.generalErrorMessage(LocaleStrings.getString("D_25_JVAPP"),
					LocaleStrings.getString("D_70_JVAPP"));
			JViewer.exit(0);
		}else if(status == KVMClient.INVALID_MAC_ADDR){
			m_frame.generalErrorMessage(LocaleStrings.getString("D_25_JVAPP"),
					LocaleStrings.getString("D_71_JVAPP"));
			JViewer.exit(0);
		}
		else if(status == KVMClient.SESSION_UNREGISTERED){
			m_frame.generalErrorMessage(LocaleStrings.getString("D_25_JVAPP"),
					LocaleStrings.getString("D_72_JVAPP"));
			JViewer.exit(0);
		}

		if(JViewer.isKVMReconnectEnabled()&& (JViewer.isjviewerapp() || JViewer.isStandAloneApp()))
		{
			if(getRetryConnection())
			{
				InfoDialog.undecDialogClose();
			}
			else{
				//close if any undecorated dialog
				InfoDialog.undecDialogClose();
				//HID inistialization dialog should not be displayed after every reconnect.
				//So HID initialization dialog will be shown if the retry dialog is not visible.
				//The HID initialization popup should be shown only if the power save mode is enabled.
				if(JViewer.isPowerSaveModeEnabled() && KVMShareDialog.isMasterSession ){
					JViewerApp.getInstance().getRCView().removeKMListener();
					hidInitDialog = InfoDialog.showDialog(JViewer.getMainFrame(), 
							LocaleStrings.getString("D_69_JVAPP"),
							(long) 5000, InfoDialog.MODELESS_WAIT_DIALOG);
				}
			}
			setSessionLive(true);
			if(m_liveListener == null) {
				m_liveListener = new KeepAlive();
				m_liveListener.start();
			}
			currentRetryCount = 0;
			//Don't invoke refresh if user has paused the redirection. Invoking refresh will resume the redirection.
			if(m_userPause == false)
				JViewerApp.getInstance().OnVideoRefreshRedirection();

			//validate websession token only for JViewer Application
			if( JViewer.isjviewerapp() ) {
				// TODO: validate websession cookie incase of rest service
				if((JViewer.isRestService() == false) && (validateSessionToken() == false))
				{
					Debug.out.println("Invalid web Session token");
					m_frame.generalErrorMessage(LocaleStrings.getString("AE_10_SPKVM"),
							LocaleStrings.getString("AE_4_SPKVM"));
					JViewer.exit(0);
				}

				//Don't send websession token, while reconnecting
				if( getRetryConnection() != true ){
					//Sending the websession token only for JViewerApp
					String web_session_token = JViewerApp.getInstance().getM_webSession_token();
					byte[] web_token = new byte[web_session_token.length()];
					web_token = web_session_token.getBytes();
					Debug.out.dump(web_token);
					IVTPPktHdr WebSessTokenPkt = new IVTPPktHdr(IVTPPktHdr.IVTP_GET_WEB_TOKEN, web_token.length, (short) 0);
					ByteBuffer wbf = ByteBuffer.allocate(WebSessTokenPkt.size() + web_token.length);
					wbf.position(0);
					wbf.put(WebSessTokenPkt.array());
					wbf.put(web_token);
					wbf.position(0);
					byte[] wreport = new byte[wbf.limit()];
					wbf.get(wreport);

					if (wreport.length != getKVMClient().sendMessage(wreport, wreport.length)) {
						Debug.out.println("Failed to web Session token to the card");
						return ;
					}
				}
			}

			setRetryConnection(false);
		}

		m_KVMClnt.setSocketTimeout();
		//session id of JViewer
		currentSessionId = sindex;

		m_KVMClnt.OnValidVideoSession();
		if(!JViewer.isWebPreviewer() && m_KVMClnt.redirection()){

			String commonExceptionList[] = {JVMenu.VIDEO_EXIT,JVMenu.HELP_ABOUT_RCONSOLE,JVMenu.VIDEO_FULL_SCREEN};
			String exceptionList[]= getExceptionMenuList(commonExceptionList);
			m_frame.getMenu().enableMenu(exceptionList,true, true);
			if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_PARTIAL){
				JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.OPTIONS_KEYBOARD_MOUSE_ENCRYPTION, false);
			}
			else{
				//Incase of partial KVMSharing no need to enable status bar
				JViewerApp.getInstance().getM_wndFrame().getM_status().enableStatusBar(true);
			}

			JViewerApp.getInstance().getM_wndFrame().getToolbar().changeMacrowsStatus(true);

			//If redirection is paused by user, maintain the paused state. This is to fix the issue during reconnect.
			if(m_userPause){
				m_RedirectionState = REDIR_STARTED;
				OnVideoPauseRedirection();
			}
		}

	}

	/**
	 * Method used to check and update LED status on host
	 *
	 * @param status -
	 *            Host keyboard LED status
	 */
	public void onKeybdLED(byte ledStatus) {
		if(getClientKeyboardLEDStatus() != ledStatus){
			if (!JViewerView.syncLEDFlag || powerStatus == SERVER_POWER_OFF) {

				// Received LED status from host and comparing with the status from
				// client. Setting appropriate keyevent to client keyboard LED.
				try {
					//If the host keyboard LED status is to be synced with the client.
					if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_SYNC_WITH_CLIENT_LED_STATUS)!=
							JViewerApp.OEM_SYNC_WITH_CLIENT_LED_STATUS){
						setClientKeyboardLEDStatus(ledStatus);
					}
				} catch (Exception e) {
					Debug.out.println(e);
				}
			}
			setHostKeyboardLEDStatus(ledStatus);
			if(getSoftKeyboard() != null)
				getSoftKeyboard().syncKbdLED();//Synchronize Softkeyboard LED status with Host LED status;
			//If the client keyboard LED status is to be synced with the host.
			if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_SYNC_WITH_CLIENT_LED_STATUS)==
					JViewerApp.OEM_SYNC_WITH_CLIENT_LED_STATUS){
				if((frameRateTask != null) &&
						!isKbdLEDSyncing()){//don't try to send client LED status to host if already a sync process is in progress.
					frameRateTask.setSyncLEDFromClient(true);
				}
			}
		}
		//Add the keyListener back if it is not added.
		if (!(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG)) {
			m_view.addKeyListener();
		}
	}

	/**
	* Resets the keyboard LED status if keyboard LEDs are enabled.
	*/
	public void resetLEDStatus(){
		if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_SYNC_WITH_CLIENT_LED_STATUS)!=
				JViewerApp.OEM_SYNC_WITH_CLIENT_LED_STATUS){
			setClientKeyboardLEDStatus((byte) 0);//resetting all LEDs
			setHostKeyboardLEDStatus((byte) 0);
		}
	}

	/**
	 * Return the LED status of the Host
	 * @return
	 */
	public byte getHostKeyboardLEDStatus() {
		return hostLEDStatus;
	}

	/**
	 * Set the LED status of the Host
	 * @param ledStatus
	 */
	public void setHostKeyboardLEDStatus(byte ledStatus) {
		hostLEDStatus = ledStatus;
	}

	/**
	 * Return the keyboard LED status of the Client
	 * @return
	 */
	public byte getClientKeyboardLEDStatus() {
		ClientConfig clientCfg = new ClientConfig();
		clientLEDStatus = clientCfg.ReadKeybdLEDStatus();
		clientCfg = null;
		return clientLEDStatus;
	}

	/**
	 * Set the keyboard LED status of the Client
	 * @param ledStatus
	 */
	public void setClientKeyboardLEDStatus(byte ledStatus) {
		try {
			//Don't add or remove KeyListener from JViewer while Mousealibration is in progress.
			if (!(Mousecaliberation.THRESHOLDFLAG || Mousecaliberation.ACCELERATION_FLAG)) {
				m_view.removeKeyListener();
			}
			//Client keyboard LED sync is not supported for Mac client.
			if(!isMacClient()){
				if(frameRateTask != null)
					frameRateTask.updateKeyboardLEDStatus(ledStatus);
				else{//If frameRatetask is null, set the client LED status directly 
					ClientConfig clientCfg = new ClientConfig();
					try{
						clientCfg.setKeyboardLEDStatus(ledStatus);
					}catch(Exception e){
						Debug.out.println(e);
					}finally{
						clientCfg = null;
					}
				}
				Thread.sleep(1000);
			}
			if(getSoftKeyboard() != null)
				getSoftKeyboard().syncKbdLED();
		} catch (Exception e) {
			Debug.out.println(e);
		}
	}

	/**
	 * Gets the client keyboard LED status before the JViewer gains
	 * focus.
	 * @return the initClientLEDStatus
	 */
	public byte getInitClientLEDStatus() {
		return initClientLEDStatus;
	}

	/**
	 * Sets the value of the initial Client keyboard LED status
	 * @param initClientLEDStatus the initClientLEDStatus to set
	 */
	public void setInitClientLEDStatus(byte initClientLEDStatus) {
		this.initClientLEDStatus = initClientLEDStatus;
	}

	/**
	 * Sends the client keyboard LED status to the host.
	 */
	public void sendClientLEDStatus(){
		byte clientLED = getClientKeyboardLEDStatus();
		byte hostLED = getHostKeyboardLEDStatus();
		int keyCode = KeyEvent.VK_UNDEFINED;
		int keyLocation = KeyEvent.KEY_LOCATION_STANDARD;
		try {
			//set keyboard LED syncing flag to true, so that multiple simutaneous
			//updates can be avoided.
			setKbdLEDSyncing(true);
			if(hostLED != clientLED){
				//If the client num lock status is not equal to that in the host.
				if((clientLED & NUMLOCK) != (hostLED & NUMLOCK)){
					keyCode = KeyEvent.VK_NUM_LOCK;
					keyLocation = KeyEvent.KEY_LOCATION_NUMPAD;
					sendKeyEvent(keyCode, keyLocation, true);
					sendKeyEvent(keyCode, keyLocation, false);
				}

				Thread.sleep(100);//stops flooding adviser
				//If the client caps lock status is not equal to that in the host.
				if((clientLED & CAPSLOCK)!=(hostLED & CAPSLOCK)){
					keyCode = KeyEvent.VK_CAPS_LOCK;
					keyLocation = KeyEvent.KEY_LOCATION_STANDARD;
					sendKeyEvent(keyCode, keyLocation, true);
					sendKeyEvent(keyCode, keyLocation, false);
				}
				Thread.sleep(100);//stops flooding adviser
				//If the client scroll lock status is not equal to that in the host.
				if((clientLED & SCROLLLOCK) != (hostLED & SCROLLLOCK)){
					keyCode = KeyEvent.VK_SCROLL_LOCK;
					keyLocation = KeyEvent.KEY_LOCATION_STANDARD;
					sendKeyEvent(keyCode, keyLocation, true);
					sendKeyEvent(keyCode, keyLocation, false);
				}
				Thread.sleep(100);//stops flooding adviser
				setHostKeyboardLEDStatus(clientLED);
			}
		} catch (InterruptedException ie) {
			Debug.out.println(ie);
		}
		catch (Exception e) {
			Debug.out.println(e);
		}
		setKbdLEDSyncing(false);
	}
	/**
	 * Gets the status of the flag which denotes whether already a client LED sync
	 * process is in progress.
	 * @return the kbdLEDSyncing
	 */
	public boolean isKbdLEDSyncing() {
		return kbdLEDSyncing;
	}

	/**
	 * Sets the flag which denotes whether already a client LED sync
	 * process is in progress.
	 * @param kbdLEDSyncing the kbdLEDSyncing to set
	 */
	public void setKbdLEDSyncing(boolean kbdLEDSyncing) {
		this.kbdLEDSyncing = kbdLEDSyncing;
	}

	/**
	 * Method stop the CD/HD Image, If eject cmd initiated by the host
	 */
	public void stopRedirection_ISoImage() {
		int cdnum = 0,hdnum = 0;
		IUSBRedirSession iusbRedirSession = VMApp.getInstance().getIUSBRedirSession();


		cdnum = VMApp.getInstance().getNumCD();
		hdnum = VMApp.getInstance().getNumHD();
		if (iusbRedirSession.cdromSession != null) {
			for(int k=0;k < cdnum;k++){
				if (VMApp.getInstance().getIUSBRedirSession().cdromSession[k].isCdImageRedirected() &&
						iusbRedirSession.cdromSession[k].isCdImageEjected())
					iusbRedirSession.StopISORedir(k, IUSBRedirSession.STOP_ON_EJECT);
			}
		}
		if (iusbRedirSession.hardDiskSession != null) {
			for(int k=0;k < hdnum ;k++){
			if (VMApp.getInstance().getIUSBRedirSession().hardDiskSession[k].isHdImageRedirected() &&
					VMApp.getInstance().getIUSBRedirSession().hardDiskSession[k].isHdImageEjected())
				iusbRedirSession.StopHarddiskImageRedir(k, IUSBRedirSession.STOP_ON_EJECT);
			}
		}

	}

	/**
	 * Method to caliberate the mouse if the mouse in relative mode
	 * @param state
	 */
	public void OnCalibareteMouse(boolean state) {
		if(JVMenu.m_scale != 1.0){//zoom not equal top 100%
			JOptionPane.showMessageDialog(getM_frame(), LocaleStrings.getString("D_27_JVAPP"),
					LocaleStrings.getString("D_28_JVAPP") , JOptionPane.ERROR_MESSAGE);
			getJVMenu().notifyMenuStateSelected(JVMenu.CALIBRATEMOUSETHRESHOLD, false);
			return;
		}
		Mousecaliberation.resetCursor();
		if (Mousecaliberation == null)
			Mousecaliberation = new Mousecaliberation();

		JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_FULL_SCREEN, false);
		JViewerApp.getInstance().getM_wndFrame().getToolbar().getFullScreenBtn().setToolTipText(
				LocaleStrings.getString("D_29_JVAPP"));
		getJVMenu().notifyMenuStateSelected(JVMenu.CALIBRATEMOUSETHRESHOLD, true);
		//remove the default keylistener because ading new keylistener and mouse lisener for MOuse caliberation
		JViewerApp.getInstance().getRCView().removeKMListener();
		Mousecaliberation.OnCalibareteMouseThreshold(state);
	}

	public void OnSendKVMPrevilage(byte kvmPrivilege, String userDetails)
	{
		if(kvmPrivilege == KVMSharing.KVM_REQ_ALLOWED){
			if(JViewerApp.getInstance().IsCDROMRedirRunning() ||
					JViewerApp.getInstance().IsHarddiskRedirRunning()){
				if(!getM_frame().stopVMediaRedirection(LocaleStrings.getString("D_58_JVAPP"))){
					kvmPrivilege = KVMSharing.KVM_REQ_PARTIAL;
				}
			}
		}
		m_KVMClnt.SendKVMPrevilage(kvmPrivilege, userDetails);
		if(isFullPermissionRequest())
			setFullPermissionRequest(false);
	}
	/**
	 * Updates the concurrent session status as one of the sessions close during KVM sharing. 
	 */
	public void onStopConcurrentSession(){
		//Show information dialog, when concurrent session is closed, and full permisssion is received.
		if( KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_PARTIAL){
			InfoDialog.showDialog(JViewer.getMainFrame(), LocaleStrings.getString("D_51_JVAPP"),
					LocaleStrings.getString("D_52_JVAPP"), InfoDialog.INFORMATION_DIALOG);
		}
		KVMSharing.KVM_REQ_GIVEN = KVMSharing.KVM_REQ_DENIED;
		KVMShareDialog.isMasterSession = true;
		//if user has paused the session, then no need to update the controls.
		if(m_KVMClnt.redirection()){
			OnChangeMenuState_KVMPartial(getJVMenu(), true);
			JViewerApp.getInstance().getM_wndFrame().getToolbar().OnChangeToolbarIconState_KVMPartial();
		}
	}
	/**
	 * Send the Websessiontoken to the server
	 * @return
	 */
	public int OnsendWebsessionToken() {
		int session_token_type = 0;
		int PktLen = 0, TokenLen = 0;

		if(JViewer.isKVMReconnectEnabled())
		{
			if(getRetryConnection())
				return 0;

			if ((getKVMClient().OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_CONNECTION_COMPLETE_PKT, 0, (short) 0)) == -1)
				return -1;
		}

		session_token_type = JViewerApp.getInstance().getSessionTokenType();

		/*As we are using token length as 128. IVTPPktHdr.VIDEO_PACKET_SIZE  will be used for ssi and normal case.*/
		PktLen = IVTPPktHdr.HDR_SIZE + IVTPPktHdr.VIDEO_PACKET_SIZE;
		TokenLen = IVTPPktHdr.SSI_HASH_SIZE;


		/* Sending the session token */
		IVTPPktHdr VideoSessTokenPkt = new IVTPPktHdr(IVTPPktHdr.IVTP_VALIDATE_VIDEO_SESSION, PktLen, (short)0);
		String session_token = JViewerApp.getInstance().getSessionToken();
		ByteBuffer bf = ByteBuffer.allocate(PktLen);

		// Calculate digest
		byte[] hashed_token = new byte[TokenLen];
		hashed_token = session_token.getBytes();

		bf.position(0);
		bf.put(VideoSessTokenPkt.array());
		bf.put((byte)0);
		bf.put(session_token.getBytes());
		Debug.out.println("Hashed token");
		Debug.out.dump(hashed_token);

		for (int i=bf.position(); i < (IVTPPktHdr.HDR_SIZE+TokenLen); i++)
			bf.put((byte)0);

		bf.put(KVMSharing.KVM_CLIENT_OWN_IP.getBytes());
		for (int i=bf.position(); i < (IVTPPktHdr.HDR_SIZE+TokenLen+IVTPPktHdr.CLINET_OWN_IP_LENGTH); i++)
			bf.put((byte)0);
		
		KVMSharing.KVM_CLIENT_OWN_USERNAME = getClientUserName();
		bf.put(KVMSharing.KVM_CLIENT_OWN_USERNAME .getBytes());
		for (int i=bf.position(); i < (IVTPPktHdr.HDR_SIZE+TokenLen+IVTPPktHdr.CLINET_OWN_IP_LENGTH+IVTPPktHdr.CLIENT_USERNAME_LENGTH); i++ )
			bf.put((byte) 0);

		String[] Mac = null;
		try {
			KVMSharing.KVM_CLIENT_OWN_MAC = getMacAddress(GET_MAC_ADDRESS, Mac);
			//Mac address format differs in linux and window,so replace : to -
			if(KVMSharing.KVM_CLIENT_OWN_MAC.contains(":")){
				KVMSharing.KVM_CLIENT_OWN_MAC = KVMSharing.KVM_CLIENT_OWN_MAC.replaceAll(":", "-");
			}
		} catch (Exception e) {
			Debug.out.println("Failed to get mac Address");
			return -1;
		}

		bf.put(KVMSharing.KVM_CLIENT_OWN_MAC .getBytes());
		for (int i=bf.position(); i < bf.limit(); i++ )
			bf.put((byte) 0);

		bf.position(0);
		byte[] report = new byte[bf.limit()];
		bf.get(report);

		if (report.length != getKVMClient().sendMessage(report, report.length)) {
			Debug.out.println("Failed to send Session token to the card");
			return -1;
		}

		// send resume video redirection command
		if ((getKVMClient().OnFormIVTPHdr_Send(IVTPPktHdr.IVTP_RESUME_REDIRECTION, 0, (short) 0)) == -1)
			return -1;

		return 0;
	}
	
	public int onReconnect()
	{
		int PktLen = 0, TokenLen = 0;
		Debug.out.println("<-Reconnect invoked->");
		if((getKVMClient().isStopSignalRecieved() == true) || ((m_RedirectionState == REDIR_STOPPED)|| (m_RedirectionState == REDIR_STOPPING)))
		{
			Debug.out.println("Stop signal has been reached so no need reconnect");
			return 0;
		}

		if(KVMSharing.KVM_REQ_GIVEN != KVMSharing.KVM_REQ_DENIED && currentRetryCount < JViewer.getRetryCount() )
		{
			m_KVMClnt.checkReconnect();	
		}
		else
		{
			Debug.out.println("<-Reconnect not possbile as max retry count is reached->");
			// call the function to close
			JViewer.exit(0);
		}
		String commonExceptionList[] = {JVMenu.VIDEO_EXIT,JVMenu.HELP_ABOUT_RCONSOLE,JVMenu.VIDEO_FULL_SCREEN};
		String exceptionList[]= getExceptionMenuList(commonExceptionList);
		m_frame.getMenu().enableMenu(exceptionList,false, false);
		JViewerApp.getInstance().OnVideoStartRedirection();
		PktLen = IVTPPktHdr.HDR_SIZE + IVTPPktHdr.VIDEO_PACKET_SIZE+1;
		TokenLen = IVTPPktHdr.SSI_HASH_SIZE;

		/* Sending the session token */
		IVTPPktHdr VideoSessTokenPkt = new IVTPPktHdr(IVTPPktHdr.IVTP_CONNECTION_COMPLETE_PKT, IVTPPktHdr.VIDEO_PACKET_SIZE+1, (short)0);
		String session_token = JViewerApp.getInstance().getSessionToken();
		ByteBuffer bf = ByteBuffer.allocate(PktLen);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.position(0);
		bf.put(VideoSessTokenPkt.array());
		//bf.put((byte)0);
		bf.put(session_token.getBytes());
		for (int i=bf.position(); i < (IVTPPktHdr.HDR_SIZE+TokenLen); i++)
			bf.put((byte)0);

		bf.put(KVMSharing.KVM_CLIENT_OWN_IP.getBytes());
		for (int i=bf.position(); i < (IVTPPktHdr.HDR_SIZE+TokenLen+IVTPPktHdr.CLINET_OWN_IP_LENGTH); i++)
			bf.put((byte)0);

		KVMSharing.KVM_CLIENT_OWN_USERNAME = getClientUserName();
		bf.put(KVMSharing.KVM_CLIENT_OWN_USERNAME .getBytes());
		for (int i=bf.position(); i < (IVTPPktHdr.HDR_SIZE+TokenLen+IVTPPktHdr.CLINET_OWN_IP_LENGTH+IVTPPktHdr.CLIENT_USERNAME_LENGTH); i++ )
			bf.put((byte) 0);

		String[] Mac = null;
		try {
			KVMSharing.KVM_CLIENT_OWN_MAC = getMacAddress(GET_MAC_ADDRESS, Mac);
			//Mac address format differs in linux and window,so replace : to -
			if(KVMSharing.KVM_CLIENT_OWN_MAC.contains(":")){
				KVMSharing.KVM_CLIENT_OWN_MAC = KVMSharing.KVM_CLIENT_OWN_MAC.replaceAll(":", "-");
			}
		} catch (Exception e) {
			Debug.out.println("Failed to get mac Address");
			return -1;
		}

		bf.put(KVMSharing.KVM_CLIENT_OWN_MAC .getBytes());
		for (int i=bf.position(); i < IVTPPktHdr.HDR_SIZE+TokenLen+IVTPPktHdr.CLINET_OWN_IP_LENGTH+IVTPPktHdr.CLIENT_USERNAME_LENGTH+IVTPPktHdr.CLINET_OWN_MAC_LENGTH; i++ )
			bf.put((byte) 0);

		bf.put((byte)JViewerApp.getInstance().getCurrentSessionId());

		bf.position(0);
		byte[] report = new byte[bf.limit()];
		bf.get(report);
		if (report.length != getKVMClient().sendMessage(report, report.length)) {
			Debug.out.println("Failed to send Reconnect Session token to the card");
			return -1;
		}

		return 1;
	}

	public int onSendWebPreviewerSession() {

		if(JViewer.isKVMReconnectEnabled())
		{
			IVTPPktHdr connComplete = new IVTPPktHdr(IVTPPktHdr.IVTP_CONNECTION_COMPLETE_PKT, 0, (short) 0);

			// Send connection complete pkt
			if (getKVMClient().sendMessage(connComplete.array(), connComplete.size()) != connComplete.size())
				return -1;
		}

		IVTPPktHdr webPreviewSession = new IVTPPktHdr(IVTPPktHdr.IVTP_WEB_PREVIEWER_SESSION, 0, (short) 0);

		// Send webPreview session
		if (getKVMClient().sendMessage(webPreviewSession.array(), webPreviewSession.size()) != webPreviewSession.size())
			return -1;
		
		return 0;
	}
	
	public void setWebPreviewerCaptureStatus(byte status)
	{
		m_webPreviewer_cap_status = status;
	}
	
	public byte getWebPreviewerCaptureStatus()
	{
		return m_webPreviewer_cap_status;
	}

	/**
	 * USer KVM previlage for the user
	 */
	public void OnKvmPrevilage(short User_command) {

		byte User_command_low_byte = (byte) User_command;
		byte User_command_high_byte = (byte) (User_command>>8);


		//from Advsd to client1
		if(User_command_low_byte == KVMSharing.STATUS_KVM_PRIV_SWITCH_MASTER){
			if(kVMDialog !=null){
				if(kVMDialog.getKVMShareRequestDialog() != null)
					kVMDialog.disposeKVMShareReqestDialog();
			}
			if(User_command_high_byte == KVMSharing.KVM_REQ_ALLOWED){
				onKVMFullPermission();
			}
			else{
				onKVMPartialPermission(KVMSharing.KVM_REQ_PARTIAL);
			}
		}


		if(User_command_low_byte == KVMSharing.STATUS_KVM_PRIV_REQ_MASTER)
		{
			String userDetails = KVMSharing.KVM_CLIENT_USERNAME+" : "+
					KVMSharing.KVM_CLIENT_IP+" : "+KVMSharing.KVM_CLIENT_SESSION_INDEX;

			// create the block permission menu if not available already
			// if following line of were removed then KVM sharing response dialog won't
			// appear properly in master session.
			if(getJVMenu().getMenu(JVMenu.OPTIONS_BLOCK_FULL_PERMISSION) == null){
				getJVMenu().addBlockPermissionMenuItem();
			}

			//If blocked with patial privilege selected then send partial permission
			if(getJVMenu().getMenuSelected(JVMenu.OPTIONS_BLOCK_WITH_VIDEO_ONLY) == true)
			{
				System.out.println("KVM master permission request blocked : Partial Access");
				OnSendKVMPrevilage(KVMSharing.KVM_REQ_BLOCKED_PARTIAL, userDetails);
				return;
			// If blocked with deny access selected then send deny permission
			} else if(getJVMenu().getMenuSelected(JVMenu.OPTIONS_BLOCK_WITH_DENY) == true){
				System.out.println("KVM master permission request blocked : Deny Access");
				OnSendKVMPrevilage(KVMSharing.KVM_REQ_BLOCKED_DENY, userDetails);
				return;
			}

			Thread thread=null;
			thread = new KVMResponseDialogThread();
			thread.start();
			KVMSharing.KVM_PRIV_RES_USER = KVMSharing.KVM_PRIV_MASTER_USER;
			return;
		}
		else
		{
			//from Advsd to client2 wait state
			if(User_command_low_byte == KVMSharing.STATUS_KVM_PRIV_WAIT_SLAVE)
			{
				String exceptionList[]= {JVMenu.VIDEO_EXIT, JVMenu.HELP_ABOUT_RCONSOLE};
				kVMDialog = new KVMShareDialog();
				kVMDialog.setUserStatus(KVMShareDialog.SECOND_USER);
				if(fullPermissionRequest)
					kVMDialog.constructDialog(KVMShareDialog.KVM_FULL_PERMISSION_REQUEST);
				else
					kVMDialog.constructDialog(KVMShareDialog.KVM_SHARING);
				kVMDialog.showDialog();
				KVMSharing.KVM_PRIV_RES_USER = KVMSharing.KVM_PRIV_SLAVE_USER;
				//disable menu items while waiting for initial permission alone.
				if(!fullPermissionRequest)
					getJVMenu().enableMenu(exceptionList, false, true);
				return;
			}
			else
			{
				//from Advsd to client1, if the client2 quit before the permission given
				if(User_command_low_byte == KVMSharing.STATUS_KVM_PRIV_REQ_CANCEL)
				{
					Debug.out.println("Cancel Packet received");
					if(kVMDialog != null)
						kVMDialog.disposeKVMShareResponseDialog();
					if(fullPermissionRequest)
						setFullPermissionRequest(false);
					return;

				}
				else
				{
					//from Advsd to client1, if the client1 doesnt response
					if(User_command_low_byte == KVMSharing.STATUS_KVM_PRIV_REQ_TIMEOUT_TO_MASTER)
					{
						Thread thread=null;
						thread = new KVMRequestDialogThread();				
						this.setMessage(LocaleStrings.getString("D_33_JVAPP")+KVMSharing.KVM_CLIENT_USERNAME+
								LocaleStrings.getString("D_34_JVAPP")+KVMSharing.KVM_CLIENT_IP+
								LocaleStrings.getString("D_35_JVAPP"));
						thread.start();
						if(getM_frame().getConfirmationLabel() != null){
							Window confirmationDialog = SwingUtilities.getWindowAncestor(getM_frame().
									getConfirmationLabel());
							confirmationDialog.setVisible(false);
							confirmationDialog.dispose();
						}
						//setting partial access in case of reuest time out
						KVMSharing.KVM_REQ_GIVEN = KVMSharing.KVM_REQ_PARTIAL;
						KVMShareDialog.isMasterSession = false;
						//close if there is any kvm rsponse dialog is open
						if(kVMDialog !=null){
							if(kVMDialog.getKVMShareRequestDialog() != null)
								kVMDialog.disposeKVMShareReqestDialog();
						}
						getM_frame().onStopVMediaRedirection(VM_DISCONNECT);
						if(m_wndMode){
							OnChangeMenuState_KVMPartial(getM_wndFrame().getMenu(), false);
							getM_wndFrame().getToolbar().OnChangeToolbarIconState_KVMPartial();
						}
						else{
							OnChangeMenuState_KVMPartial(getM_fsFrame().getMenu(), false);
						}
						if(isFullPermissionRequest()){
							setFullPermissionRequest(false);
						}
						else{
							getJVMenu().removeBlockPermissionMenuItem();
							getJVMenu().addFullPermissionMenuItem();
						}
						return;
					}
					else
					{
						//from advsd to client2, packet with permission status
						if(User_command_low_byte == KVMSharing.STATUS_KVM_PRIV_RESPONSE_TO_SLAVE)
						{
							Debug.out.println("#########Got from user2########### and usercommand byte is:"+User_command_high_byte);
							if(kVMDialog != null)
								kVMDialog.disposeKVMShareReqestDialog();

							Thread thread=null;
							thread = new KVMRequestDialogThread();

							if(User_command_high_byte == KVMSharing.KVM_REQ_ALLOWED)
							{
								onKVMFullPermission();
							}
							else if((User_command_high_byte == KVMSharing.KVM_REQ_PARTIAL) ||
									(User_command_high_byte == KVMSharing.KVM_REQ_BLOCKED_PARTIAL))
							{
								onKVMPartialPermission(User_command_high_byte);
							}
							else if((User_command_high_byte == KVMSharing.KVM_REQ_PROGRESS) ||
									User_command_high_byte == KVMSharing.KVM_REQ_MASTER_RECONN)
							{
								JVFrame frame = JViewerApp.getInstance().getMainWindow();
								if(User_command_high_byte == KVMSharing.KVM_REQ_PROGRESS)
									InfoDialog.showDialog(frame, LocaleStrings.getString("D_59_JVAPP"), LocaleStrings.getString("D_32_JVAPP"), InfoDialog.INFORMATION_DIALOG);
								else if(User_command_high_byte == KVMSharing.KVM_REQ_MASTER_RECONN)
									InfoDialog.showDialog(frame, LocaleStrings.getString("D_68_JVAPP"), LocaleStrings.getString("D_32_JVAPP"), InfoDialog.INFORMATION_DIALOG);
								onKVMPartialPermission(KVMSharing.KVM_REQ_PROGRESS);
							}
							else if(User_command_high_byte == KVMSharing.KVM_REQ_TIMEOUT)
							{
								Debug.out.println("IVTPPktHdr.KVM_REQ_TIMEOUT");
								kVMDialog.disposeKVMShareReqestDialog();
								this.setMessage(LocaleStrings.getString("D_41_JVAPP")+KVMSharing.KVM_CLIENT_USERNAME+
										LocaleStrings.getString("D_36_JVAPP")+KVMSharing.KVM_CLIENT_IP);
								thread.start();
								//setting full access in case of reuest time out
								KVMSharing.KVM_REQ_GIVEN = KVMSharing.KVM_REQ_ALLOWED;
								KVMShareDialog.isMasterSession = true;
								if(m_wndMode){
									OnChangeMenuState_KVMPartial(getM_wndFrame().getMenu(), true);
									getM_wndFrame().getToolbar().OnChangeToolbarIconState_KVMPartial();
								}
								else{
									OnChangeMenuState_KVMPartial(getM_fsFrame().getMenu(), false);
								}
								if(isFullPermissionRequest()){
									setFullPermissionRequest(false);
									getJVMenu().removeFullPermissionMenuItem();
									getJVMenu().addBlockPermissionMenuItem();
								}
							}
							else if(User_command_high_byte == KVMSharing.KVM_REQ_DENIED || User_command_high_byte == KVMSharing.KVM_REQ_BLOCKED_DENY)
							{
								KVMSharing.KVM_REQ_GIVEN = KVMSharing.KVM_REQ_DENIED;
								// Set m_redirection flag in KVMClient to true, so that the stop session
								// command can be send to the adviser.
								JViewerApp.getInstance().getKVMClient().setM_redirection(true);
								OnVideoStopRedirection();

								if(User_command_high_byte == KVMSharing.KVM_REQ_DENIED){
									Debug.out.println("IVTPPktHdr.KVM_REQ_DENIED");
									JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(),
											LocaleStrings.getString("D_42_JVAPP"));
								} else {
									Debug.out.println("IVTPPktHdr.KVM_REQ_BLOCKED_DENY");
									JOptionPane.showMessageDialog(JViewerApp.getInstance().getMainWindow(),
											LocaleStrings.getString("D_67_JVAPP") + "\n" +LocaleStrings.getString("D_42_JVAPP"));
								}

								kVMDialog.disposeKVMShareReqestDialog();
								JViewerApp.getInstance().getM_frame().windowClosed();
							}

							else if(User_command_high_byte == KVMSharing.KVM_NOT_MASTER){
								onKVMPartialPermission(User_command_high_byte);
								InfoDialog.showDialog(JViewerApp.getInstance().getMainWindow(),LocaleStrings.getString("D_55_JVAPP"),
										LocaleStrings.getString("D_56_JVAPP"),
										InfoDialog.INFORMATION_DIALOG);
							}
							else if(User_command_high_byte == KVMSharing.KVM_MASTER_TERMINATED){
								InfoDialog.showDialog(JViewerApp.getInstance().getMainWindow(),LocaleStrings.getString("D_73_JVAPP"),
										LocaleStrings.getString("D_32_JVAPP"),
										InfoDialog.INFORMATION_DIALOG);
							}
							return;
						}
					}
				}
			}
		}
	}


	public boolean OnCheckSameClient(String ip) {
		byte[] ipDgt = null;
		byte[] ipDgt_own= null;
		InetAddress hostAddress = null;
		InetAddress hostAddress_own = null;
		boolean SameIP = false;
		try {
			hostAddress = InetAddress.getByName(ip);
			hostAddress_own = InetAddress.getByName(KVMSharing.KVM_CLIENT_OWN_IP);

			String ipStr = hostAddress.getHostAddress();
			String ipStr_own = hostAddress_own.getHostAddress();
			ipDgt = InetAddress.getByName(ipStr).getAddress();
			ipDgt_own = InetAddress.getByName(ipStr_own).getAddress();
			
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			Debug.out.println(e1);
		}catch (Exception e) {
			// TODO: handle exception
			Debug.out.println(e);
		}

		try {
			if (java.net.InetAddress.getByAddress(ipDgt).equals(
				      java.net.InetAddress.getByAddress(ipDgt_own)))
			{
				Debug.out.println("Equals");
				SameIP =true;
				//JOptionPane.showMessageDialog(null,"ALready One Session is running",LocaleStrings.GetString("H_10_KVMS"),JOptionPane.INFORMATION_MESSAGE);
				//System.exit(0);
			}
			else
			{
				Debug.out.println("Not Equals");
				SameIP = false;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SameIP;

	}
	/**
	 * Enable or disable Keyboard, Mouse, and Keyboard layout menus and the 
	 * Keyboard/Mouse encryption menu item while partial access is given to 
	 * the concurrent session in KVM sharing.
	 * @param menu - JVMenu
	 * @param state - boolean
	 */
	private void OnChangeMenuState_KVMPartial(JVMenu menu, boolean state)
	{
		JViewerApp.getOEMManager().getOEMJVMenu().enableMenuOnKVMPartial(state);
		if(powerStatus == SERVER_POWER_OFF){
			if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_PARTIAL){
				m_frame.getMenu().notifyMenuStateEnable(JVMenu.POWER_ON_SERVER, false);
				m_frame.getMenu().notifyMenuStateEnable(JVMenu.DEVICE_MEDIA_DIALOG, false);
			}
			else if (state) {
				// if state is true and KVM_REG_GIVEN is no KVM_REQ_PARTIAL then KVM Full access is granted, so enable power and media dialogs. 
				m_frame.getMenu().notifyMenuStateEnable(JVMenu.POWER_ON_SERVER, true);
				m_frame.getMenu().notifyMenuStateEnable(JVMenu.DEVICE_MEDIA_DIALOG, true);
				enablePowerControls(state);// Preserve the Power menu items status.
			}
			return;
		}
		/*Exception list is the list of menu oitems that should be excluded while
		changing the state of menu items, when a user is given partial acess 
		permission in KVM sharing*/

		String []commonExceptionList = JVMenu.KVMPartialExceptionMenuItems;
		String exceptionList[]= getExceptionMenuList(commonExceptionList);
		menu.enableMenu(exceptionList, state, true);
		if(state)
			enablePowerControls(state);// Preserve the Power menu items status.
	}

	/**
	 * @return the selectedTab
	 */
	public int getSelectedVMTab() {
		return selectedVMTab;
	}
	/**
	 * @param selectedTab the selectedTab to set
	 */
	public void setSelectedVMTab(int selectedTab) {
		this.selectedVMTab = selectedTab;
	}
	/**
	 * Invoke the dialog, Vmedia menuitem clieck from the Menu
	 *
	 */
	public void OnvMedia(int selectedTab) {
		setSelectedVMTab(selectedTab);
		if(!JViewer.isVMApp()){
			vmDialog = VMApp.getVMDialog();
			//Do not open another instace of the dialog if it is already opened.
			if(vmDialog != null && vmDialog.isShowing())
				return;
			//If the dialog obect is not NULL, but dialog is not visible, make it visible.
			else if (vmDialog != null && !vmDialog.isShowing()) {
				//set dialog position to display
				vmDialog.setLocation(JViewerApp.getInstance().getPopUpWindowPosition(vmDialog.getWidth(), vmDialog.getHeight()));
				vmDialog.showDialog(true);
				return;
			}
		}
		VMApp.launchApp();
	}
	/**
	 * Returns the VMDialog object
	 * @return the vmDialog
	 */
	public VMDialog getVMDialog() {
		return vmDialog;
	}

	public void setVMDialog(VMDialog vmDialog){
		this.vmDialog = vmDialog;
	}

	/**
	 * Invoke the VideoRecording Settings dialog, Settings menu item click from the Menu
	 *
	 */
	public void OnVideoRecordSettings()
	{
		if(m_videorecord == null)
			m_videorecord = new VideoRecord();
		m_videorecord.VideoRecordsettings();
	}
	/**
	 * Abnormal CDROM Redirection failure occurs
	 *
	 */
	public void reportCDROMAbnormal(int device_no) {
		VMApp.getInstance().getIUSBRedirSession().stopCDROMAbnormal(device_no);
		if(JViewer.isVMApp()){
			VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_CDROM,device_no).updateDeviceControlPanel();
			VMApp.getInstance().getIUSBRedirSession().updateCDToolbarButtonStatus(false);
		}
		else{
			if (getVMDialog() != null) {
				VMApp.getVMPane().getDeviceControlPanel(VMApp.DEVICE_TYPE_CDROM,device_no).updateDeviceControlPanel();
				if(JViewer.isjviewerapp() || JViewer.isStandalone()) {
					VMApp.getVMPane().updateJVToolbar(VMApp.DEVICE_TYPE_CDROM);
				}
			}
		}
	}

	/**
	 * Abnormal Harddisk Redirection failure occurs
	 *
	 */
	public void reportHarddiskAbnormal(int device_no) {
		VMApp.getInstance().getIUSBRedirSession().stopHarddiskAbnormal(device_no);
		if(JViewer.isVMApp()){
			VMApp.getVMPane().getDeviceControlPanel( VMApp.DEVICE_TYPE_HD_USB,device_no).updateDeviceControlPanel();
			VMApp.getInstance().getIUSBRedirSession().updateHDToolbarButtonStatus(false);
		}
		else{
			if (getVMDialog() != null) {
				VMApp.getVMPane().getDeviceControlPanel( VMApp.DEVICE_TYPE_HD_USB,device_no).updateDeviceControlPanel();
				if(JViewer.isjviewerapp() || JViewer.isStandalone()) {
					VMApp.getVMPane().updateJVToolbar(VMApp.DEVICE_TYPE_HD_USB);
				}
			}
		}
	}
	/**
	 *	Return the state of the CDRedirection
	 * @return
	 */
	public boolean IsCDROMRedirRunning(int device_no) {
		try{
		if (VMApp.getInstance().getIUSBRedirSession().getCDROMRedirStatus(device_no) == IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED)
			return true;
		}catch(Exception e){
			Debug.out.println(e);
		}
		return false;
	}

	public boolean IsCDROMRedirRunning() {
		int cdnum = 0;

			cdnum = VMApp.getInstance().getNumCD();
		for(int h=0;h< cdnum;h++) {
			try{
			if (VMApp.getInstance().getIUSBRedirSession().getCDROMRedirStatus(h) == IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED)
				return true;
			}catch(Exception e){
				Debug.out.println(e);
				return false;
			}
		}
		return false;
	}

	/**
	 * Return the state of the Harddisk/USB redirection
	 * @return
	 */
	public boolean IsHarddiskRedirRunning(int device_no) {
		try{
		if (VMApp.getInstance().getIUSBRedirSession().getHarddiskRedirStatus(device_no) == IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED)
			return true;
		}catch(Exception e){
			Debug.out.println(e);
		}
		return false;
	}

	public boolean IsHarddiskRedirRunning() {
		int hdnum = 0;
		hdnum = VMApp.getInstance().getNumHD();
		for(int h=0;h < hdnum;h++) {
			try{
			if (VMApp.getInstance().getIUSBRedirSession().getHarddiskRedirStatus(h) == IUSBRedirSession.DEVICE_REDIR_STATUS_CONNECTED)
				return true;
			}catch(Exception e){
				return false;
			}
		}
		return false;
	}


	/***
	 * Return the CDRedirection connection port
	 * @return
	 */
	public int getCDPort() {
		return m_cdPort;
	}

	/**
	 * Return the HarddiskRedirection connection port
	 * @return
	 */
	public int getHDPort() {
		return m_hdPort;
	}
	/***
	 * Return the CDRedirection connection Status
	 * @return
	 */
	public int getCDStatus() {
		return m_cdStatus;
	}

	/**
	 * Return the HarddiskRedirection connection Status
	 * @return
	 */
	public int getHDStatus() {
		return m_hdStatus;
	}

	/**
	 * Return the websession token string
	 * @return
	 */
	public String getM_webSession_token() {
		return JViewer.getWebSessionToken();
	}

	public void setM_webSession_token(String m_webSession_token) {
		JViewer.setWebSessionToken(m_webSession_token);
	}

	//Added for JInternalFrame
	/**
	 * 
	 * @return
	 */
	public JVFrame getM_frame() {
		return m_frame;
	}
	
	/**
	 * Return the Fullscreen frame instance
	 * @return
	 */
	public FSFrame getM_fsFrame() {
		return m_fsFrame;
	}

	/**
	 * Return the state of the JViewer Fullscreen / window mode
	 *
	 * @return
	 */
	public boolean isM_wndMode() {
		return m_wndMode;
	}

	/**
	 * Set the windowmode whetehr it is fullscreen or window mode
	 * @param mode
	 */
	public void setM_wndMode(boolean mode) {
		m_wndMode = mode;
	}

	/**
	 * Return the SOC App instance
	 * @return
	 */
	public ISOCApp getSoc_App() {
		return soc_App;
	}

	/**
	 * Set the  SOC App instance
	 * @param soc_App
	 */
	public void setSoc_App(ISOCApp soc_App) {
		this.soc_App = soc_App;
	}

	/**
	 * Return the Viewpane instance
	 * @return
	 */
	public JViewerView getM_view() {
		return m_view;
	}

	/**
	 * Set the Viewpane instance
	 * @return
	 */
	public void setM_view(JViewerView m_view) {
		this.m_view = m_view;
	}

	/**
	 * Return the SOCmanager common interface instance
	 * @return
	 */
	public static ISOCManager getSoc_manager() {
		return soc_manager;
	}

	/**
	 * Return the SOCFrameHdr interface instance
	 * @return
	 */
	public ISOCFrameHdr getSocframeHdr() {
		return socframeHdr;
	}

	/**
	 * Set the SOCFrameHdr interface instance
	 * @param socframeHdr
	 */
	public void setSocframeHdr(ISOCFrameHdr socframeHdr) {
		this.socframeHdr = socframeHdr;
	}

	/**
	 * Get Window frame instance
	 * @return
	 */
	public WindowFrame getM_wndFrame() {
		return m_wndFrame;
	}

	/**
	 * Set Window frame instance
	 * @param frame
	 */
	public void setM_wndFrame(WindowFrame frame) {
		m_wndFrame = frame;
	}

	/**
	 * Return the JVVideo interface instance
	 * @return
	 */
	public JVVideo getVidClnt() {
		return vidClnt;
	}

	/**
	 * Set the JVVideo interface instance
	 * @return
	 */
	public void setVidClnt(JVVideo vidClnt) {
		this.vidClnt = vidClnt;
	}

	/**
	 * Return the Imagebuffer create instance
	 * @return
	 */
	public ISOCCreateBuffer getPrepare_buf() {
		return prepare_buf;
	}

	public ISOCKvmClient getSockvmclient() {
		return sockvmclient;
	}

	public void setSockvmclient(ISOCKvmClient sockvmclient) {
		this.sockvmclient = sockvmclient;
	}

	/**
	 * Enable /Disable Auto Detect keyboard layout
	 * @param state - true to enable and false to disable
	 */
	public void onAutoKeyboardLayout(boolean state,boolean isMenu){
		getJVMenu().getMenuItem(JVMenu.PKBRD_NONE).setSelected(true);
	
		if(state == true) {
			JVMenu.keyBoardLayout *= -1;
			getJVMenu().notifyMenuStateSelected(JVMenu.AUTOMATIC_LANGUAGE, true);

			JViewer.setKeyboardLayout(JViewer.AUTO_DETECT_KEYBOARD);
			if(isMenu == true){
				if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_SET_PHYSICAL_KBD_LANG) ==
					JViewerApp.OEM_SET_PHYSICAL_KBD_LANG){
					//Set keyboard layout language as default.
					String msg = LocaleStrings.getString("S_22_SACD")+" "+LocaleStrings.getString("S_21_SACD");
					InfoDialog.showDialog(m_frame, LocaleStrings.getString("M_1_ID")+msg+"?",
							msg,
							InfoDialog.CONFIRMATION_DIALOG,5000,InfoDialog.HOST_KBD_LANG);
				}
			}

			if(getAutokeylayout() == null){
				setAutokeylayout(new AutoKeyboardLayout());
				getAutokeylayout().setKeyboardType(AutoKeyboardLayout.KBD_TYPE_AUTO);
				getAutokeylayout().setHostKeyboardType(AutoKeyboardLayout.KBD_TYPE_AUTO);
			}
			else{
				getAutokeylayout().setKeyboardType(AutoKeyboardLayout.KBD_TYPE_AUTO);
				getAutokeylayout().setHostKeyboardType(AutoKeyboardLayout.KBD_TYPE_AUTO);
				getAutokeylayout().initKeyProcessor();
				getAutokeylayout().getKeyboardType();
				getAutokeylayout().ongetKeyprocessor();
			}
			OnSkbrdDisplay(-1);
		}
		else {
			if(JVMenu.keyBoardLayout != -1)
				JVMenu.keyBoardLayout *= -1;
			JVMenu.keyBoardLayout = JVMenu.softkeyBoardLayout;
			getJVMenu().getMenu(JVMenu.SOFTKEYBOARD).setEnabled(true);
			getJVMenu().notifyMenuStateSelected(JVMenu.AUTOMATIC_LANGUAGE, false);
			getJVMenu().SetMenuEnable(JVMenu.SOFTKEYBOARD, true);
			getJVMenu().m_menuItems_setenabled.put(JVMenu.SOFTKEYBOARD, true);
		}
		JViewerApp.getInstance().getM_USBKeyRep().setM_USBKeyProcessor(JViewerApp.getInstance().getKeyProcesssor());
	}

	public AutoKeyboardLayout getAutokeylayout() {
		return autokeylayout;
	}

	public void setAutokeylayout(AutoKeyboardLayout autokeylayout) {
		this.autokeylayout = autokeylayout;
	}

	public KeyProcessor getKeyProcesssor() {
		if(autokeylayout == null) {
			autokeylayout = new AutoKeyboardLayout();
		}
		keyprocessor = autokeylayout.ongetKeyprocessor();
		keyprocessor.setAutoKeybreakMode(true);
		return keyprocessor;
	}
	
	public void setKeyProcessor(String keyboardLayout){
		int keyBoardType = AutoKeyboardLayout.KBD_TYPE_ENGLISH_US;

		if(JViewer.getKeyboardLayout().equals(keyboardLayout))
			return;

		if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_ENGLISH_US))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_ENGLISH_US;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_FRENCH_FRANCE))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_FRENCH;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_GERMAN_GER))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_GERMAN;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_JAPANESE))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_JAPANESE;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_SPANISH))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_SPANISH;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_ENGLISH_UK))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_ENGLISH_UK;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_GERMAN_SWISS))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_GERMAN_SWISS;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_FRENCH_BELGIUM))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_FRENCH_BELGIUM;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_ITALIAN))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_ITALIAN;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_DANISH))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_DANISH;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_FINNISH))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_FINNISH;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_NORWEGIAN))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_NORWEGIAN;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_PORTUGUESE))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_PORTUGUESE;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_SWEDISH))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_SWEDISH;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_DUTCH_NL))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_DUTCH_NL;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_DUTCH_BE))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_DUTCH_BE;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_TURKISH_F))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_TURKISH_F;
		else if(keyboardLayout.equalsIgnoreCase(JVMenu.PKBRD_LANGUAGE_TURKISH_Q))
			keyBoardType = AutoKeyboardLayout.KBD_TYPE_TURKISH_Q;

		if(autokeylayout == null)
			autokeylayout = new AutoKeyboardLayout();

		getJVMenu().notifyMenuStateSelected(JVMenu.AUTOMATIC_LANGUAGE, false);
		//enable Softkeyboard menu
		if(getJVMenu().getMenuEnable(JVMenu.SOFTKEYBOARD) == false){
			getJVMenu().notifyMenuEnable(JVMenu.SOFTKEYBOARD, true);
			JVMenu.keyBoardLayout *= -1;
		}
		if (softKeyboard != null) {
			softKeyboard.m_skmouselistener.close();
			softKeyboard.dispose();
		}
		autokeylayout.setHostKeyboardType(keyBoardType);
		JViewer.setKeyboardLayout(keyboardLayout);
		getM_USBKeyRep().setM_USBKeyProcessor(getKeyProcesssor());

		if((JViewer.getOEMFeatureStatus() & JViewerApp.OEM_SET_PHYSICAL_KBD_LANG) ==
			JViewerApp.OEM_SET_PHYSICAL_KBD_LANG){
			//Set keyboard layout language as default.
			String msg = LocaleStrings.getString("S_22_SACD")+" "+LocaleStrings.getString("S_21_SACD");
			InfoDialog.showDialog(m_frame, LocaleStrings.getString("M_1_ID")+msg+"?",
					msg,
					InfoDialog.CONFIRMATION_DIALOG,5000,InfoDialog.HOST_KBD_LANG);
		}
	}

	public USBKeyboardRep getM_USBKeyRep() {
		return m_USBKeyRep;
	}

	public void setM_USBKeyRep(USBKeyboardRep keyRep) {
		m_USBKeyRep = keyRep;
	}

	public SoftKeyboard getSoftKeyboard() {
		return softKeyboard;
	}

	public void setSoftKeyboard(SoftKeyboard softKeyboard) {
		this.softKeyboard = softKeyboard;
	}

	public VideoRecord getM_videorecord() {
		return m_videorecord;
	}
	public void setM_videorecord(VideoRecord m_videorecord) {
		this.m_videorecord = m_videorecord;
	}

	public AddMacro getAddMacro() {
		return addMacro;
	}
	public void setAddMacro(AddMacro addMacro) {
		this.addMacro = addMacro;
	}
	
	/**
	 * @return the userDefMacro
	 */
	public UserDefMacro getUserDefMacro() {
		return userDefMacro;
	}

	public void OnAddMacro() {
		if(JViewerApp.getInstance().getAddMacro() == null)
			return;
		userDefMacro = new UserDefMacro(JViewer.getMainFrame());
	}

	public void OnVideoZoomIn() {

		m_zoomSliderValue = getM_wndFrame().getToolbar().getSlider_zoom().getValue();
		if(JVMenu.m_scale <= 1.5F) {
			BigDecimal rate=new BigDecimal(JVMenu.m_scale);
			BigDecimal cost=new BigDecimal("0.1");
			BigDecimal stepped_value = rate.add(cost).setScale(2, BigDecimal.ROUND_HALF_UP);
			JVMenu.m_scale=stepped_value.floatValue();
			m_zoomSliderValue += 10;
			getM_wndFrame().getToolbar().getSlider_zoom().setValue(m_zoomSliderValue);
			JViewerApp.getInstance().getRCView().revalidate();
	        JViewerApp.getInstance().getRCView().repaint();
		}

        if(JVMenu.m_scale >= 0.5F) {
			getJVMenu().SetMenuEnable(JVMenu.ZOOM_OUT, true);
			getJVMenu().getMenuItem(JVMenu.ZOOM_OUT).setEnabled(true);
		} 
        
        if(JVMenu.m_scale >= 1.5F) {
        	getJVMenu().SetMenuEnable(JVMenu.ZOOM_IN, false);
			getJVMenu().getMenuItem(JVMenu.ZOOM_IN).setEnabled(false);
		}
		//solved changing Zoom Size will make mouse cursor inconsist
		if(m_zoomSliderValue == 100){
			boolean cursurMenuState = JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.MOUSE_CLIENTCURSOR_CONTROL);
			if (cursurMenuState)
				Mousecaliberation.resetCursor();
		}
	}
	public void OnVideoZoomOut() {

		m_zoomSliderValue = getM_wndFrame().getToolbar().getSlider_zoom().getValue();
		if(JVMenu.m_scale >= 0.5F)
		{
			BigDecimal rate=new BigDecimal(JVMenu.m_scale);
			BigDecimal cost=new BigDecimal("0.1");
			BigDecimal stepped_value = rate.subtract(cost).setScale(2, BigDecimal.ROUND_HALF_UP);
			JVMenu.m_scale=stepped_value.floatValue();
			m_zoomSliderValue -= 10;
			getM_wndFrame().getToolbar().getSlider_zoom().setValue(m_zoomSliderValue);
			JViewerApp.getInstance().getRCView().revalidate();
	        JViewerApp.getInstance().getRCView().repaint();
		}

		if(JVMenu.m_scale <= 0.5F) {
			getJVMenu().SetMenuEnable(JVMenu.ZOOM_OUT, false);
			getJVMenu().getMenuItem(JVMenu.ZOOM_OUT).setEnabled(false);
		} 
		if(JVMenu.m_scale >= 0.5F) {
        	getJVMenu().SetMenuEnable(JVMenu.ZOOM_IN, true);
			getJVMenu().getMenuItem(JVMenu.ZOOM_IN).setEnabled(true);
		}

		//solved changing Zoom Size will make mouse cursor inconsist
		if(m_zoomSliderValue == 100){
			boolean cursurMenuState = JViewerApp.getInstance().getJVMenu().getMenuSelected(JVMenu.MOUSE_CLIENTCURSOR_CONTROL);			
			if (cursurMenuState)
				Mousecaliberation.resetCursor();
		}
	}

	/**
	 * Change the zoom option settings
	 * @param option - The option to be set.
	 */
	public void onChangeZoomOptions(String option){
		//return if JVIewer windows is out of focus
		if(JViewerView.syncLEDFlag == true){
			// When JViewer is in out of focus and resolution change has occurred in
			// Host, we need to recalculate the zoom options again.
			// Updating the following flag for that purpose.
			JViewerApp.getInstance().setResolutionChanged(1);
 			return;
		}

		GraphicsConfiguration gc = JViewerApp.getInstance().getM_wndFrame().getGraphicsConfiguration();
		Dimension screen = getCurrentMonitorResolution();
		Point screenPos= getWindowPostionToSet();
		Insets screenInsets = new Insets(0, 0, 0, 0);
		try{
			screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		}catch (NullPointerException e) {
		}

		int systemComponentsHeight = screenInsets.top + screenInsets.bottom;
		int systemComponentsWidth = screenInsets.left+screenInsets.right;
		int frameHeight = screen.height - systemComponentsHeight;
		int frameWidth = screen.width - systemComponentsWidth;

		if(option.equals(JVMenu.ACTUAL_SIZE)){
			if(zoomOption != JVMenu.ZOOM_OPTION_NONE){
				//set the JViewer frame size to the size prior to focus loss.
				if(!isRenderFitToHost() && m_wndFrame.isResizeFrame()){
					JViewer.getMainFrame().setSize(m_wndFrame.getFrameDimension());
				}
				else{//set the JViewer frame size to the size of the desktop.
					JViewer.getMainFrame().setSize(frameWidth, frameHeight);
					JViewer.getMainFrame().setLocation(screenPos);
				}
			}
			setZoomOption(option);
			getM_wndFrame().getToolbar().resetZoom();
			if(getRCView().GetUSBMouseMode() != USBMouseRep.OTHER_MOUSE_MODE){
				JViewer.getMainFrame().setResizable(true);
				JViewerApp.getInstance().getMainWindow().m_viewSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				JViewerApp.getInstance().getMainWindow().m_viewSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			}
			getM_wndFrame().getToolbar().enableZoomSlider(true);
			getJVMenu().notifyMenuStateEnable(JVMenu.ZOOM_IN, true);
			getJVMenu().notifyMenuStateEnable(JVMenu.ZOOM_OUT, true);
			getJVMenu().notifyMenuStateSelected(JVMenu.ACTUAL_SIZE, true);
		}
		else if(option.equals(JVMenu.FIT_TO_CLIENT_RES)){
			getM_wndFrame().getToolbar().resetZoom();

			JViewer.getMainFrame().setLocation(screenPos);
			/*Code to set window as not resizable*/
			JViewerApp.getInstance().getMainWindow().m_viewSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			JViewerApp.getInstance().getMainWindow().m_viewSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			JViewer.getMainFrame().setSize(frameWidth, frameHeight);
			JViewerApp.getInstance().getRCView().revalidate();
			JViewerApp.getInstance().getRCView().repaint();
			JViewer.getMainFrame().setResizable(false);
			setZoomOption(option);
			getM_wndFrame().getToolbar().enableZoomSlider(false);
			if(getRCView().GetUSBMouseMode() == USBMouseRep.RELATIVE_MOUSE_MODE){
				getM_wndFrame().getToolbar().changeShowCursorOnZoom();
			}
			getJVMenu().notifyMenuStateEnable(JVMenu.ZOOM_IN, false);
			getJVMenu().notifyMenuStateEnable(JVMenu.ZOOM_OUT, false);
			getJVMenu().notifyMenuStateSelected(JVMenu.FIT_TO_CLIENT_RES, true);
		}
		else if(option.equals(JVMenu.FIT_TO_HOST_RES)){
			int videoResX = getSocframeHdr().getresX();
			int videoResY = getSocframeHdr().getresY();
			//addedHeight is the height of the titlebar, menubar, toolbar and the statusbar, that should be taken into account
			//while calculating teh window height.
			int addedHeight = JViewer.getMainFrame().getInsets().top+
			JViewerApp.getInstance().getM_wndFrame().getWindowMenu().getMenuBar().getHeight()+
			JViewerApp.getInstance().getM_wndFrame().getToolbar().getToolBar().getHeight()+
			JViewerApp.getInstance().getM_wndFrame().getToolbar().getToolBar().getInsets().top+
			JViewerApp.getInstance().getM_wndFrame().getToolbar().getToolBar().getInsets().bottom+
			JViewerApp.getInstance().getM_wndFrame().getM_status().getStatusBar().getHeight()+
			JViewerApp.getInstance().getM_wndFrame().getM_status().getStatusBar().getInsets().top+
			JViewerApp.getInstance().getM_wndFrame().getM_status().getStatusBar().getInsets().bottom;

			int addedWidth = JViewer.getMainFrame().getInsets().left + JViewer.getMainFrame().getInsets().left+
								getRCView().getInsets().left + getRCView().getInsets().right;


			Dimension clientScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
			
			frameWidth = videoResX + addedWidth;
			frameHeight = videoResY + addedHeight;

			if((frameWidth + systemComponentsWidth) < clientScreenSize.width &&
					(frameHeight + systemComponentsHeight) < clientScreenSize.height){
				getM_wndFrame().getToolbar().resetZoom();
				JViewer.getMainFrame().setLocation(screenPos);
				JViewer.getMainFrame().setExtendedState(JFrame.NORMAL);
				if(videoResX >= JViewer.MIN_FRAME_WIDTH && videoResY >= JViewer.MIN_FRAME_HEIGHT)
					JViewer.getMainFrame().setSize(frameWidth, frameHeight);
				else
					JViewer.getMainFrame().setSize(JViewer.MIN_FRAME_WIDTH, JViewer.MIN_FRAME_HEIGHT);
				JViewerApp.getInstance().getMainWindow().m_viewSP.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				JViewerApp.getInstance().getMainWindow().m_viewSP.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
				JViewer.getMainFrame().setResizable(false);

				setZoomOption(option);
				if(getRCView().GetUSBMouseMode() == USBMouseRep.RELATIVE_MOUSE_MODE)
					getM_wndFrame().getToolbar().changeShowCursorOnZoom();
				getM_wndFrame().getToolbar().enableZoomSlider(false);
				getJVMenu().notifyMenuStateEnable(JVMenu.ZOOM_IN, false);
				getJVMenu().notifyMenuStateEnable(JVMenu.ZOOM_OUT, false);
				getJVMenu().notifyMenuStateSelected(JVMenu.FIT_TO_HOST_RES, true);
			}
		}
		getM_wndFrame().getM_status().resetStatus();
		m_wndFrame.setResizeFrame(false);
		// After calculating, resetting the flag.
		JViewerApp.getInstance().setResolutionChanged(0);
	}
	/**
	 * @return the zoomOption
	 */
	public String getZoomOption() {
		return zoomOption;
	}

	/**
	 * @param zoomOption the zoomOption to set
	 */
	public void setZoomOption(String zoomOption) {
		this.zoomOption = zoomOption;
	}

	/**
	 * Invoked once the power status is recieved from the server.
	 * @param pwrStatus - SERVER_POWER_ON or SERVER_POWER_OFF 
	 */
	public void onGetPowerControlStatus(byte pwrStatus){
		if(pwrStatus == SERVER_POWER_ON){// server power on
			powerStatus = SERVER_POWER_ON;
			try{
				getVidClnt().stopPsMonitorTaskAndTimer();
			}catch(Exception e){
				Debug.out.println(e);
				e.printStackTrace();
			}
			if(JViewerApp.getInstance().getSoftKeyboard() != null)
			{
				JViewerApp.getInstance().getSoftKeyboard().OnUpdateKeyState(true);
			}
		}
		else if(pwrStatus == SERVER_POWER_OFF){// server power off
			powerStatus = SERVER_POWER_OFF;
			try{                           
				getVidClnt().startPsMonitorTaskAndTimer();
			}catch(Exception e){
				e.printStackTrace();
			}
			if(JViewerApp.getInstance().getSoftKeyboard() != null)
			{
				JViewerApp.getInstance().getSoftKeyboard().OnUpdateKeyState(false);
			}
		}
		enablePowerControls(true);		
	}
	/**
	 * Sends the power control command to the KVMClient.
	 * @param command command to be executed.
	 */
	public void onSendPowerControlCommand(String command){

		//jviewer session doesn't have privilege to execute power commands
		if(!JViewer.isPowerPrivEnabled())
			return ;

		JInternalFrame frame = JViewerApp.getInstance().getMainWindow();
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
		int ret = JOptionPane.showConfirmDialog(frame,LocaleStrings.getString("D_43_JVAPP")+command+
					LocaleStrings.getString("D_44_JVAPP"), LocaleStrings.getString("D_45_JVAPP"), 
					JOptionPane.YES_NO_OPTION);
		if(ret == JOptionPane.YES_OPTION){
			enablePowerControls(false);
			if(JViewerApp.getInstance().getSoftKeyboard() != null)
			{
				JViewerApp.getInstance().getSoftKeyboard().OnUpdateKeyState(false);
			}
			if(command.equals(JVMenu.POWER_RESET_SERVER))
				 m_KVMClnt.sendPowerControlCommand(IVTPPktHdr.IVTP_POWER_CONTROL_HARD_RESET);
			else if(command.equals(JVMenu.POWER_OFF_IMMEDIATE))
				 m_KVMClnt.sendPowerControlCommand(IVTPPktHdr.IVTP_POWER_CONTROL_OFF_IMMEDIATE);
			else if(command.equals(JVMenu.POWER_OFF_ORDERLY))
				m_KVMClnt.sendPowerControlCommand(IVTPPktHdr.IVTP_POWER_CONTROL_SOFT_RESET);
			else if(command.equals(JVMenu.POWER_ON_SERVER))
				m_KVMClnt.sendPowerControlCommand(IVTPPktHdr.IVTP_POWER_CONTROL_ON);
			else if(command.equals(JVMenu.POWER_CYCLE_SERVER))
				m_KVMClnt.sendPowerControlCommand(IVTPPktHdr.IVTP_POWER_CONTROL_CYCLE);			
		}
		
	}
	/**
	 * Enables or disables power control menu items and power status toolbar button. 
	 * It also updates the state of these controls according to the power status. 
	 * @param state - true - to enable the controls; false to disable them.
	 */
	public void enablePowerControls(boolean state){
		JViewerApp.getOEMManager().getOEMJVMenu().enableMenuOnPowerControls();
		m_wndFrame.getToolbar().getPowerBtn().setEnabled(state);
		if(state){
			if(powerStatus == SERVER_POWER_OFF){
				String exceptmenu[]={JVMenu.VIDEO_CAPTURE_SCREEN, JVMenu.VIDEO_EXIT, JVMenu.HELP_ABOUT_RCONSOLE,
										JVMenu.POWER_ON_SERVER, JVMenu.DEVICE_MEDIA_DIALOG, JVMenu.VIDEO_RECORD_SETTINGS,
										JVMenu.VIDEO_RECORD_START, JVMenu.VIDEO_RECORD_STOP};
				String[] OEMExceptMenu = JViewerApp.getOEMManager().getOEMJVMenu().getOemPowerControlExceptionList();
				if(OEMExceptMenu != null)
				{
					String[] commonExceptMenu = new String[exceptmenu.length + OEMExceptMenu.length];
					System.arraycopy(exceptmenu, 0, commonExceptMenu, 0, exceptmenu.length);
					System.arraycopy(OEMExceptMenu, 0, commonExceptMenu, exceptmenu.length, OEMExceptMenu.length);
					m_frame.getMenu().enableMenu(commonExceptMenu, false, false);// disable menus when server power off
				} else {
					m_frame.getMenu().enableMenu(exceptmenu, false, false);// disable menus when server power off
				}
				m_wndFrame.getToolbar().turnOnPowerButton(false);
				if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_PARTIAL){
					m_frame.getMenu().notifyMenuStateEnable(JVMenu.POWER_ON_SERVER, false);
					m_frame.getMenu().notifyMenuStateEnable(JVMenu.DEVICE_MEDIA_DIALOG, false);
				}
				else{
					m_frame.getMenu().notifyMenuStateEnable(JVMenu.POWER_ON_SERVER, true);
					m_frame.getMenu().notifyMenuStateEnable(JVMenu.DEVICE_MEDIA_DIALOG, true);
					m_wndFrame.getToolbar().setButtonEnabled(m_wndFrame.getToolbar().getPowerBtn(), true);
				}
				m_wndFrame.getToolbar().changeToolbarButtonStateOnPowerStatus(false);
			}
			else{
				//Enable gui controls if redirection is not paused. 
				if(m_RedirectionState == REDIR_STARTED){
					changeMenuItemsStatusOnPauseResume(m_frame.getMenu(), true);// change menu staes to default state.
					m_wndFrame.getToolbar().changeToolbarButtonStateOnPowerStatus(true);
					m_frame.getMenu().notifyMenuStateEnable(JVMenu.POWER_ON_SERVER, false);				
					m_wndFrame.getToolbar().turnOnPowerButton(true);
					getVidClnt().setZoomOptionStatus();
				}
			}
		}
	}
	/**
	 * Displays success or failure message after recieving the power control response from the server.
	 * @param response - response from the server.
	 */
	public void onPowerControlResponse(byte response){
		if(response != 0)
			InfoDialog.showDialog(JViewerApp.getInstance().getMainWindow(),
					LocaleStrings.getString("D_46_JVAPP"), LocaleStrings.getString("D_45_JVAPP"),
					InfoDialog.ERROR_DIALOG);
		else
			InfoDialog.showDialog(JViewerApp.getInstance().getMainWindow(), 
					LocaleStrings.getString("D_47_JVAPP"), LocaleStrings.getString("D_45_JVAPP"),
					InfoDialog.INFORMATION_DIALOG);
		enablePowerControls(true);
	}
	public Mousecaliberation getMousecaliberation() {
		return Mousecaliberation;
	}

	public void setMousecaliberation(Mousecaliberation mousecaliberation) {
		Mousecaliberation = mousecaliberation;
	}


	public int getM_cdStatus() {
		return m_cdStatus;
	}
	public int getM_hdStatus() {
		return m_hdStatus;
	}
	public void setM_cdStatus(int status) {
		m_cdStatus = status;		
	}
	public void setM_hdStatus(int status) {
		m_hdStatus = status;
	}
	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public VideoRecordApp getVideorecordapp() {
		return videorecordapp;
	}

	public void setVideorecordapp(VideoRecordApp videorecordapp) {
		this.videorecordapp = videorecordapp;
	}
	public int getM_hdPort() {
		return m_hdPort;
	}
	public void setM_hdPort(int port) {
		m_hdPort = port;
	}
	public int getM_cdPort() {
		return m_cdPort;
	}
	public void setM_cdPort(int port) {
		m_cdPort = port;
	}
	public boolean isM_bVMUseSSL() {
		return m_bVMUseSSL;
	}
	public void setM_bVMUseSSL(boolean useSSL) {
		m_bVMUseSSL = useSSL;
	}

	/**
	 * @return the fullKeyboardEnabled
	 */
	public boolean isFullKeyboardEnabled() {
		return fullKeyboardEnabled;
	}

	/**
	 * @param fullKeyboardEnabled the fullKeyboardEnabled to set
	 */
	public void setFullKeyboardEnabled(boolean fullKeyboardEnabled) {
		this.fullKeyboardEnabled = fullKeyboardEnabled;
	}

	/**
	 * @return the fullPermissionRequest
	 */
	public boolean isFullPermissionRequest() {
		return fullPermissionRequest;
	}

	/**
	 * @param fullPermissionRequest the fullPermissionRequest to set
	 */
	public void setFullPermissionRequest(boolean fullPermissionRequest) {
		this.fullPermissionRequest = fullPermissionRequest;
	}

	public boolean syncVMediaRedirection(){
		boolean isMediaRunning = false;
		int cdnum = 0,hdnum = 0;

		cdnum = VMApp.getInstance().getNumCD();
		hdnum = VMApp.getInstance().getNumHD();
		synchronized (CDROMRedir.getSyncObj()) {
			for(int count = 0; count< cdnum ;count++)
				if(VMApp.getInstance().getIUSBRedirSession().getCdromSession(count) != null)
				{
					VMApp.getInstance().getIUSBRedirSession().getCdromSession(count).setConfModified(true);
					if(VMApp.getInstance().getIUSBRedirSession().getCdromSession(count).isRedirActive())
						isMediaRunning = true;	
				}	
			CDROMRedir.getSyncObj().notifyAll();
		}
		synchronized (HarddiskRedir.getSyncObj()) {
			for(int count = 0; count< hdnum;count++)
				if(VMApp.getInstance().getIUSBRedirSession().getHarddiskSession(count) != null)
				{
					VMApp.getInstance().getIUSBRedirSession().getHarddiskSession(count).setConfModified(true);
					if(VMApp.getInstance().getIUSBRedirSession().getHarddiskSession(count).isRedirActive())
						isMediaRunning = true;
				}
			HarddiskRedir.getSyncObj().notifyAll();
		}
		return isMediaRunning;
	}
    // Used to send the mouse mode data to the adviserd 
	public void OnSendMouseMode(byte mouseMode)
	{
		// validation for the redirection flag. If redirection is not active, it return simply
		if (!m_KVMClnt.redirection()) return;
		// Sending mouse mode to the adviserd
		if (m_KVMClnt.SendMouseMode(mouseMode) == 1)
			Debug.out.println("Mouse mode send failured");
		Debug.out.println("Mouse mode send success");
	}

	public void OnSelectKVMMaster(){
		if((KVMClient.getNumUsers() > 1 )&& (KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_ALLOWED)){
			if(kVMDialog == null)
				kVMDialog = new KVMShareDialog();
			kVMDialog.constructDialog(KVMShareDialog.KVM_SELECT_MASTER);
			kVMDialog.showDialog();
		}
	}
	/**
	 * @return the kVMDialog
	 */
	public KVMShareDialog getKVMShareDialog() {
		return kVMDialog;
	}

	/**
	 * @return the responseDialogTable
	 */
	public Hashtable<String, JDialog> getResponseDialogTable() {
		return responseDialogTable;
	}

	/**
	 * Initialize the KVM response Dialog HashTable
	 */
	public void initResponseDialogTable() {
		if(responseDialogTable == null)
			responseDialogTable = new Hashtable<String, JDialog>();
	}

	/**
	 * @param kVMDialog the kVMDialog to set
	 */
	public void setKVMDialog(KVMShareDialog kVMDialog) {
		this.kVMDialog = kVMDialog;
	}

	public void sendSelectedMasterInfo(String masterInfo){
		ByteBuffer masterDataBuffer = getKVMClient().getUserDataPacket().createUserDataBuffer(masterInfo);
		getKVMClient().sendNextMasterInfo(masterDataBuffer);
	}
	
	/**
	 * Handle the changes to be done when Full KVM Privilege is receved.
	 */
	private void onKVMFullPermission(){
		getJVMenu().getMenu(JVMenu.SOFTKEYBOARD).setEnabled(true);
		JViewerApp.getInstance().getM_wndFrame().getToolbar().getKbdBtn().setEnabled(
				getJVMenu().getMenuItem(JVMenu.SKBRD_LANGUAGE_ENGLISH_US).isEnabled());
		JViewerApp.getInstance().getM_wndFrame().getM_status().enableStatusBar(true);
		Thread thread=null;
		thread = new KVMRequestDialogThread();
		if ((KVMSharing.KVM_CLIENT_USERNAME != null) && (KVMSharing.KVM_CLIENT_IP != null)){
			if(KVMSharing.KVM_REQ_GIVEN != KVMSharing.KVM_REQ_ALLOWED)
				this.setMessage(LocaleStrings.getString("D_38_JVAPP")+KVMSharing.KVM_CLIENT_USERNAME+
						LocaleStrings.getString("D_39_JVAPP")+KVMSharing.KVM_CLIENT_IP);
		}
		else	// No Client and Username, so there is no other concurrent master user to provide the access.
		{
			if(getKVMShareDialog() != null)
				this.setMessage(LocaleStrings.getString("D_51_JVAPP"));
		}
		if(this.getMessage() != null && this.getMessage().length() > 0)
			thread.start();
		KVMSharing.KVM_REQ_GIVEN = KVMSharing.KVM_REQ_ALLOWED;
		KVMShareDialog.isMasterSession = true;
		if(isFullPermissionRequest()){
			JViewerApp.getInstance().getJVMenu().removeFullPermissionMenuItem();
			JViewerApp.getInstance().getJVMenu().addBlockPermissionMenuItem();
			m_KVMClnt.sendGetUserMacro();
			if(m_wndMode){
				OnChangeMenuState_KVMPartial(getM_wndFrame().getMenu(), true);
				getM_wndFrame().getToolbar().OnChangeToolbarIconState_KVMPartial();
			}
			else{
				OnChangeMenuState_KVMPartial(getM_fsFrame().getMenu(), true);
			}
		}
		//Do media reconnect only if we have full permission after reconnect
		getM_frame().onStopVMediaRedirection(VM_RECONNECT);
	}
	
	/**
	 * Handle the changes to be done when Partial KVM Privilege is receved.
	 */
	private void onKVMPartialPermission(byte permission){

		byte kvmpermission = (permission == KVMSharing.KVM_REQ_BLOCKED_PARTIAL)?KVMSharing.KVM_REQ_PARTIAL : permission;

		Debug.out.println(" onKVMPartialPermissionpermission: permission"+permission+"  kvmpermission:"+kvmpermission);
		JViewerApp.getOEMManager().getOEMKvmClient().enableMenuOnKVMPartialPermission(kvmpermission);
		if(kvmpermission == KVMSharing.KVM_REQ_PARTIAL)
		{
			Thread thread=null;
			thread = new KVMRequestDialogThread();
			if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_ALLOWED){
				//Disconnect media if we lost full permission on reconnect by change 
				getM_frame().onStopVMediaRedirection(VM_DISCONNECT);
				this.setMessage(LocaleStrings.getString("D_57_JVAPP")+
						KVMSharing.KVM_CLIENT_USERNAME+LocaleStrings.getString("D_39_JVAPP")
						+KVMSharing.KVM_CLIENT_IP);
			}
			else if(permission == KVMSharing.KVM_REQ_BLOCKED_PARTIAL){
				this.setMessage(LocaleStrings.getString("D_67_JVAPP")+"\n"+LocaleStrings.getString("D_40_JVAPP")+
						KVMSharing.KVM_CLIENT_USERNAME+LocaleStrings.getString("D_39_JVAPP")
						+KVMSharing.KVM_CLIENT_IP);
			}
			else{
				this.setMessage(LocaleStrings.getString("D_40_JVAPP")+
						KVMSharing.KVM_CLIENT_USERNAME+LocaleStrings.getString("D_39_JVAPP")
						+KVMSharing.KVM_CLIENT_IP);
			}
			thread.start();
		}
		KVMSharing.KVM_REQ_GIVEN = KVMSharing.KVM_REQ_PARTIAL;
		KVMShareDialog.isMasterSession = false;
		if(getVMDialog() != null && getVMDialog().isShowing()){
			getVMDialog().disposeVMDialog();
		}
		// Close softkeyboard if opened
		if(getSoftKeyboard() != null)
		{
			getSoftKeyboard().closeSoftKeyboard();
		}
		if(m_wndMode){
			OnChangeMenuState_KVMPartial(getM_wndFrame().getMenu(), false);
			getM_wndFrame().getToolbar().OnChangeToolbarIconState_KVMPartial();
		}
		else{
			OnChangeMenuState_KVMPartial(getM_fsFrame().getMenu(), false);
		}
		if(isFullPermissionRequest()){
			setFullPermissionRequest(false);
		}
		else{
			getJVMenu().removeBlockPermissionMenuItem();
			getJVMenu().addFullPermissionMenuItem();
		}
		JViewerApp.getInstance().getJVMenu().getMenu(JVMenu.SOFTKEYBOARD).setEnabled(false);
		JViewerApp.getInstance().getM_wndFrame().getToolbar().getKbdBtn().setEnabled(false);
		JViewerApp.getInstance().getM_wndFrame().getM_status().enableStatusBar(false);
		// Enable the menu item for requesting the permission next time.
		JViewerApp.getInstance().getJVMenu().getMenuItem(JVMenu.OPTIONS_REQUEST_FULL_PERMISSION).setEnabled(true);
	}
	public void onSendHostLock(byte state) {
		m_KVMClnt.onSendLockScreen(state);
		changeHostDisplayLockStatus(state);
	}

	public void changeHostDisplayLockStatus(short status){
		getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_HOST_DISPLAY_LOCK, false);
		getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK, false);
		if(status == HOST_DISPLAY_UNLOCK || status == HOST_DISPLAY_UNLOCKED_AND_DISABLED){
			if(status == HOST_DISPLAY_UNLOCK)
				getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_HOST_DISPLAY_LOCK, true);
			getM_wndFrame().getToolbar().turnOnHostDisplayButton(true);

			getJVMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_LOCK).setMnemonic('n');
			getJVMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_LOCK).setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.ALT_MASK));
			getJVMenu().getMenuMnemonics().put(JVMenu.VIDEO_HOST_DISPLAY_LOCK, 'n');
			getJVMenu().getMenuAccelerator().put(JVMenu.VIDEO_HOST_DISPLAY_LOCK, 
					KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.ALT_MASK));
			
			getJVMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK).setMnemonic('\0');
			getJVMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK).setAccelerator(null);	
			getJVMenu().getMenuMnemonics().remove(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK);
			getJVMenu().getMenuAccelerator().remove(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK);
		}
		else if(status == HOST_DISPLAY_LOCK || status == HOST_DISPLAY_LOCKED_AND_DISABLED){
			if(status == HOST_DISPLAY_LOCK)
				getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK, true);
			getM_wndFrame().getToolbar().turnOnHostDisplayButton(false);

			getJVMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK).setMnemonic('n');
			getJVMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK).setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.ALT_MASK));
			getJVMenu().getMenuMnemonics().put(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK, 'n');
			getJVMenu().getMenuAccelerator().put(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK, 
					KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.ALT_MASK));
			
			getJVMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_LOCK).setMnemonic('\0');
			getJVMenu().getMenuItem(JVMenu.VIDEO_HOST_DISPLAY_LOCK).setAccelerator(null);
			getJVMenu().getMenuMnemonics().remove(JVMenu.VIDEO_HOST_DISPLAY_LOCK);
			getJVMenu().getMenuAccelerator().remove(JVMenu.VIDEO_HOST_DISPLAY_LOCK);
		}
		//Disable menu items in case of power OFF status, and partial access previlege.
		if(KVMSharing.KVM_REQ_GIVEN == KVMSharing.KVM_REQ_PARTIAL ||
				powerStatus == SERVER_POWER_OFF){
			getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_HOST_DISPLAY_UNLOCK, false);
			getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_HOST_DISPLAY_LOCK, false);
		}
	}

	/**
	 * Conacatinate the common menu item exception list and SOC menu item exception list.
	 * @param commonExceptionList - list of exception menus.
	 * @return
	 */
	public String[] getExceptionMenuList(String[] commonExceptionList){
		String SOCExceptionList[] = JVMenu.KVMPartialExceptionSOCMenuItems;
		String OEMExceptionList[] = JVMenu.KVMPartialExceptionOEMMenuItems;
		String exceptionList[] = null;
		// check if SOC and OEM exceptions list are not null
		// If both aren't null then merge and return the array.
		if (SOCExceptionList != null && OEMExceptionList != null) {
			exceptionList = new String[commonExceptionList.length + SOCExceptionList.length + OEMExceptionList.length];
			System.arraycopy(commonExceptionList, 0, exceptionList, 0, commonExceptionList.length);
			System.arraycopy(SOCExceptionList, 0, exceptionList, commonExceptionList.length, SOCExceptionList.length);
			System.arraycopy(OEMExceptionList, 0, exceptionList, commonExceptionList.length + SOCExceptionList.length, OEMExceptionList.length);
		} // If case has failed, that means either of SOC/OEM exception list are null.
		// Check if SOC exception list is not null, if so then OEM exception list is null.
		// Copy SOC exception list alone and return
		else if(SOCExceptionList != null) { 
			exceptionList = new String[commonExceptionList.length + SOCExceptionList.length];
			System.arraycopy(commonExceptionList, 0, exceptionList, 0, commonExceptionList.length);
			System.arraycopy(SOCExceptionList, 0, exceptionList, commonExceptionList.length, SOCExceptionList.length);
		}// If and else if case has failed, that means either of SOC exception list is null.
		// Check if OEM exception list is not null, if so copy OEM exception list alone and return 
		else if(OEMExceptionList != null){ 
				exceptionList = new String[commonExceptionList.length + OEMExceptionList.length];
				System.arraycopy(commonExceptionList, 0, exceptionList, 0, commonExceptionList.length);
				System.arraycopy(OEMExceptionList, 0, exceptionList, commonExceptionList.length, OEMExceptionList.length);
		} // If and else if cases have failed, so return the commonExpectionList without modifying. 
		else {
			return commonExceptionList;
		}
		return exceptionList;
	}

	public void onMediaLicenseStatus(byte state){
		Debug.out.println("ON MEDIA LICENSE STATE : "+state);
		Debug.out.println("CURRENT MEDIA LICENSE STATUS : "+JViewer.getMediaLicenseStatus());
		if(JViewer.getMediaLicenseStatus() != state)
		{
			JViewer.setMediaLicenseStatus(state);
			if(JViewerApp.getInstance().IsCDROMRedirRunning() ||
					JViewerApp.getInstance().IsHarddiskRedirRunning()){
				getM_frame().stopVMediaRedirection(LocaleStrings.getString("D_60_JVAPP"));
			}
		}
	}

/**
	Returns the client user name with user domain if available.
*/
	public String getClientUserName(){
		String clientUserName = "";
		String userName = "";
		String userDomain = null;
		userName = System.getProperty("user.name");
		if(System.getProperty("os.name").startsWith("Windows")){
			userDomain = System.getenv("USERDOMAIN");
			userDomain = userDomain.trim();
		}
		if(userDomain != null && userDomain.length() > 0)
			clientUserName = userDomain + "\\" + userName;
		else
			clientUserName = userName;
		return clientUserName;
	}

	public void confirmationDialogResponse(int type){
		if (type == InfoDialog.HOST_KBD_LANG)
			m_KVMClnt.sendKeyBoardLang();
	}

	public int getFreeCDNum() {
		return freeCDNum;
	}

	public void setFreeCDNum(int num) {
		freeCDNum = num;
		VMApp.getInstance().setFreeCDNum(num);
	}

	public int getFreeHDNum() {
		return freeHDNum;
	}

	public void setFreeHDNum(int num) {
		freeHDNum = num;
		VMApp.getInstance().setFreeHDNum(num);
	}

	/**
	 * Upadte the free devices status in VMedia dialog.
	 */
	public void updateFreeDeviceStatus(){
		if(vmDialog != null){
			VMApp.getVMPane().updateFreeDeviceStatus(VMApp.DEVICE_TYPE_CDROM);
			VMApp.getVMPane().updateFreeDeviceStatus(VMApp.DEVICE_TYPE_HD_USB);
		}
	}

	/**
	 * @return Gives the status if the Fit to Host zoom option can be rendered or not.
	 */
	public boolean isRenderFitToHost() {
		return renderFitToHost;
	}

	/**
	 * Set the flag to true if the Fit to Host zoom option can be rendered. Set teh flag as false otherwise.
	 * @param renderFitToHost the renderFitToHost to set
	 */
	public void setRenderFitToHost(boolean renderFitToHost) {
		this.renderFitToHost = renderFitToHost;
	}
	public int getCurrentSessionId() {
		return currentSessionId;
	}

	public void setCurrentSessionId(int currentSessionId) {
		this.currentSessionId = currentSessionId;
	}

	/**
	 * Get Client Mac Address, Compre with incominf MAc addresses list.
	 * @param MODE(GET_MAC_ADDRESS - to get client MAC,COMPARE_MAC_ADDRESS- compare client mac with others)
	 * @param Mac - List of Mac Addresses to compare with client(only for COMPARE_MAC_ADDRESS MODE else null)
	 */
	public String getMacAddress(int MODE,String[] Mac) throws Exception {
		String macAddress = null;
		String command = "ifconfig";

		if(MODE == COMPARE_MAC_ADDRESS){
			if(Mac.length <= 0)
				return null;
		}

		String osName = System.getProperty("os.name");

		if (osName.startsWith("Windows")) {
			command = "ipconfig /all";
		}
		else if (osName.startsWith("Linux") || osName.startsWith("Mac")) {
			command = "/sbin/ifconfig -a";
		}
		else {// Note: Unsupported system.
			Debug.out.println("The current operating system '" + osName + "' is not supported.");
			return null;
		}

		Process pid = Runtime.getRuntime().exec(command);
		BufferedReader in = new BufferedReader(new InputStreamReader(pid.getInputStream()));
		Pattern p = Pattern.compile("([\\w]{1,2}(-|:)){5}[\\w]{1,2}");
		while (true) {
			String line = in.readLine();

			if (line == null){
				break;
			}

			if(MODE == GET_MAC_ADDRESS){
				if(line.contains(KVMSharing.KVM_CLIENT_OWN_IP) && macAddress != null) {
					break;
				}
			}

			Matcher m = p.matcher(line);
			if (m.find()) {
				macAddress = m.group();

				if(macAddress.contains(":"))
					macAddress = macAddress.replaceAll(":", "-");

				if(MODE == COMPARE_MAC_ADDRESS){

					for (int i=0;i<Mac.length;i++){
						//Should not comapre against MAC address if its zero 
						if((Mac[i]!= null)&& (macAddress.compareToIgnoreCase("00-00-00-00-00-00") != 0)){
							if(Mac[i].compareToIgnoreCase(macAddress)==0)
								return null;
						}
					}
				}
				else
					continue;
			}
		}
		in.close();
		return macAddress;
	}

	/* Return the current time */
	public long getCurrentTime()
	{
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis();
	}

	public void setLocalport(int localport) {
		this.localport = localport;
	}

	public int getLocalport() {
		return localport;
	}

	public long getLastPckSent(){
		return lastPcktSent;
	}

	public void setLastPckSent(){
		this.lastPcktSent = getCurrentTime() ;
	}

	/**
	 * @param lastPcktRecvd the lastPcktRecvd to set
	 */
	public void setLastPcktRecvd() {
		this.lastPcktRecvd = getCurrentTime();
	}

	/**
	 * @return the lastPcktRecvd
	 */
	public long getLastPcktRecvd() {
		return lastPcktRecvd;
	}

	public boolean getSessionLive(){
		return sessionLive;
	}

	public void setSessionLive(boolean sessionStatus){
		this.sessionLive = sessionStatus;
	}

	public boolean getRetryConnection(){
		return retryConnection;
	}

	public void setRetryConnection(boolean retryStatus){
		this.retryConnection = retryStatus;
	}
	
	public void onConnFailed(){
		m_RedirectionState = REDIR_STOPPED;
		if(!(JViewer.isWebPreviewer() || JViewer.isBSODViewer())) {
			JOptionPane.showMessageDialog(m_frame, LocaleStrings.getString("D_3_JVAPP"), 
					LocaleStrings.getString("D_4_JVAPP"), JOptionPane.ERROR_MESSAGE);
			JViewerApp.getInstance().getM_frame().windowClosed();
		} else{
			JViewerApp.getInstance().setWebPreviewerCaptureStatus(WEB_PREVIEWER_CONNECT_FAILURE);
		}
	}

	/**
	 * @return true if the Client OS is Linux
	 */
	public boolean isLinuxClient() {
		return System.getProperty("os.name").toLowerCase().contains("linux");
	}

	/**
	 * @return true if the Client OS is Windows
	 */
	public boolean isWindowsClient() {
		return  System.getProperty("os.name").toLowerCase().contains("windows");
	}
	
	/**
	 * @return true if the Client OS is Macintosh OS X
	 */
	public boolean isMacClient() {
		return System.getProperty("os.name").toLowerCase().contains("mac");
	}


	/**
	 * @return the hostResolution
	 */
	public String getResolutionStatus() {
		return resolutionStatus;
	}
	
	/*
	** Set current bpp.
	** bppString - bpp value 
	*/
	public void setBPPString(String bppString){
		//Add a hyphen(-) as prefix.
		if(bppString != null && bppString != " ")
			this.bppString = " - "+bppString;
		else
			this.bppString = "";
	}
	
	/*
	** @return the current bpp
	*/
	public String getBPPString(){
			return this.bppString;
	}
	/**
	 * @param resolutionStatus the hostResolution to set
	 */
	public void setResolutionStatus(int resX, int resY) {
		if(resX <= 0 || resY <= 0)
			this.resolutionStatus = null;
		else
			this.resolutionStatus = resX +" x "+ resY;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public String getCurrentVersion() {
		return currentVersion;
	}

	/**
	 * @return the oemManager
	 */
	public static IOEMManager getOEMManager() {
		return oemManager;
	}

	public int getResolutionChanged() {
		return resolutionChanged;
	}

	public void setResolutionChanged(int resolutionChanged) {
		this.resolutionChanged = resolutionChanged;
	}
	public String getIpmiPrivText(int ipmiPriv){
		String ipmi = null;
		
		if(ipmiPriv == PRIV_LEVEL_ADMIN)
			ipmi = LocaleStrings.getString("H_15_KVMS");
		else if(ipmiPriv == PRIV_LEVEL_USER)
			ipmi = LocaleStrings.getString("H_16_KVMS");
		else if(ipmiPriv == PRIV_LEVEL_OPERATOR)
			ipmi = LocaleStrings.getString("H_17_KVMS");
		else if(ipmiPriv == PRIV_LEVEL_PROPRIETARY)
			ipmi = LocaleStrings.getString("H_18_KVMS");
		return ipmi;
	}

	/**
	 * @return the frameRateTask
	 */
	public FrameRateTask getFrameRateTask() {
		return frameRateTask;
	}

	/**
	 * Sets the FrameRateTask object.
	 * @param frameRateTask - the FrameRateTask object to be set.
	 */
	public void setFrameRateTask(FrameRateTask frameRateTask) {
		this.frameRateTask = frameRateTask;
	}

	public void setSinglePortKvm(SinglePortKVM singlePortKvm) {
		this.singlePortKvm = singlePortKvm;
	}

	/**
	 * @return the hidInitDialog
	 */
	public InfoDialog getHidInitDialog() {
		return hidInitDialog;
	}

	/**
	 * @param hidInitDialog the hidInitDialog to set
	 */
	public void setHidInitDialog(InfoDialog hidInitDialog) {
		this.hidInitDialog = hidInitDialog;
	}
	
	
	/**
	 * Checks if the web session token value is valid or not 
	 */
	private boolean validateSessionToken() {
		boolean valid = true;
		// validate the session cookie using validate.asp rpc call
		URLProcessor urlProcessor = new URLProcessor(JViewer.getWebSessionToken(), 1 /*SECURE_CONNECT*/);
		int ret = urlProcessor.processRequest(JViewer.getProtocol()+"://"+JViewer.getIp()+":"+JViewer.HTTPS_PORT+"/rpc/WEBSES/validate.asp");
		try{
			if(ret == URLProcessor.INVALID_SESSION_TOKEN){
				valid = false;
			}
		}catch(Exception e){
			Debug.out.println(e);
		}
		return valid;
	}

	/**
	 *  @return the current reconnect retry value
	 */
	public int getCurrentRetryCount(){
		return currentRetryCount;
	}

	/**
	 * @param retryCount - Current reconnect retry count value
	 */
	public void setCurrentRetryCount(int retryCount){
		currentRetryCount = retryCount;
	}

	public void updateKVMMenuOnReconnect(boolean state) {

		Hashtable<String, JMenu> menu = JViewerApp.getInstance().getJVMenu().getM_menu();
		for(Map.Entry m:menu.entrySet()){
			JViewerApp.getInstance().getM_frame().getMenu().notifyMenuEnable((String)m.getKey(),  state);
		}
		JViewerApp.getInstance().getM_wndFrame().getToolbar().changeMacroStatusOnReconnect(state);

	}
}

