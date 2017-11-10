package com.ami.kvm.jviewer.gui;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;

public class Macrows {

	 public JPanel	  m_statusBar;

	
    @SuppressWarnings("serial")
    public Macrows (JVStatusBar m_status)  {
		ToolTipManager.sharedInstance().setInitialDelay(0);
	}

    public JPanel getM_statusBar() {
		return m_statusBar;
	}

	public void setM_statusBar(JPanel bar) {
		m_statusBar = bar;
	}
}