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
// Full screen frame module.
//

package com.ami.kvm.jviewer.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;

import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.common.oem.IOEMManager;
import com.ami.kvm.jviewer.kvmpkts.Mousecaliberation;

/**
 * Full screen frame.
 */
public class FSFrame extends JVFrame {
	private static final long serialVersionUID = 1L;
	private FSMenuBar m_menuBar;
	private String m_label;

	/**
	 * The constructor.
	 */
	public FSFrame() {
		// Modified for JInternalFrame
		if(JViewer.isStandalone()) 
			JViewer.getMainFrame().setUndecorated(true);

		setResizable(false);
		Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(sd);
		m_menuBar = new FSMenuBar(this);
		
		
	}

	/**
	 * Get FSMenu Object
	 *
	 * @return menu
	 */
	public JVMenu getMenu() {
		return m_menuBar.getFSMenu();
	}

    /**
     * Set status Msg in the status bar.
     *
     * @param status message.
     */
    public void setStatus(String msg) {
    	// TBD
    }

    /**
     * Reset status Msg in the status bar.
     */
    public void resetStatus() {
    	// TBD
    }

    /**
     * Exit the JViewer application Window.
     */
	public void exitApp() {
		windowClosed();
	}

	/**
	 * Set window label for the JViewer title
	 *
	 * @param label label string
	 */
	public void setWndLabel(String label) {
		m_label = label;
		String resolution = JViewerApp.getInstance().getResolutionStatus();

		if (!Mousecaliberation.THRESHOLDFLAG && !Mousecaliberation.ACCELERATION_FLAG) {
			if (resolution != null)
				m_menuBar.setIDLabel(m_serverIP + " - " + "[" + resolution + "] - " + label);
			else
				m_menuBar.setIDLabel(m_serverIP + " - " + label);
			if (JViewerApp.getOEMManager().setFSFrameTitle(label) == IOEMManager.AMI_CODE) {
				setTitle(JViewer.getTitle() + "[" + m_serverIP + "] - " + label);
			}
		}
	}

	/**
	 * Show override-displaying the window Menubar
	 */
	public void showWindow() {
		m_menuBar = new FSMenuBar(this);
		m_menuBar.showMenu();
		if(JViewer.isStandalone()) {
			URL imageURL = com.ami.kvm.jviewer.JViewer.class.getResource("res/jviewer.jpg");
			//Modified for JInternalFrame
			JViewer.getMainFrame().setIconImage(Toolkit.getDefaultToolkit().getImage(imageURL));	
		}
	}

	/**
	 * Hide override-Hiding the Window Mwnubar
	 */
	public void hideWindow() {
		//Cancelling timer thread before fullscreen dispose
		m_menuBar.cancelTimer();
		m_menuBar.dispose();

	}
	/**
	 * Refresh window label
	 */
	public void refreshTitle() {
		if (JViewerApp.getOEMManager().setFSFrameTitle(m_label) == IOEMManager.AMI_CODE) {
			setTitle(JViewer.getTitle() + " [" + m_serverIP + "] - " + m_label);
		}
	}

	/**
	 * Return the Full screen Menubar
	 * @return
	 */
	public FSMenuBar getM_menuBar() {
		return m_menuBar;
	}
}
