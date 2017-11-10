/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2014, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
 ****************************************************************/

package com.ami.kvm.jviewer.gui;

import java.io.File;

import javax.swing.JFileChooser;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;

public class DebugLogFileChooser extends Thread{


	private JFileChooser saveFileChooser;
	private File saveFile;
	private boolean run = true;

	public void run(){
		saveFileChooser = new JFileChooser();
		saveFile = Debug.out.getLogFile();
		saveFileChooser.setSelectedFile(saveFile);
		while(run){
			int retVal = saveFileChooser.showSaveDialog(JViewer.getMainFrame());
			if(retVal == JFileChooser.APPROVE_OPTION){// save button pressed
				saveFile = saveFileChooser.getSelectedFile();
				Debug.out.initLog(saveFile);
			}
			run = false;// quit the thread;
		}
	}

	public File getSelectedFile(){
		return saveFile;	
	}

	/**
	 * @return the saveFile
	 */
	public File getSaveFile() {
		return saveFile;
	}

	/**
	 * @param saveFile the saveFile to set
	 */
	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	}

}
