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

///////////////////////////////////////////////////////////////////
//CaptureScreen class deals with the screen capture implementation
///////////////////////////////////////////////////////////////////

package com.ami.kvm.jviewer.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;

public class CaptureScreen extends Thread{

	private JFileChooser saveFileChooser;
	private File saveFile;
	private boolean run = true;
	private BufferedImage capturedFrame = null;
	
	public CaptureScreen(BufferedImage capturedFrame){
		this.capturedFrame = capturedFrame;
	}

	public void run(){
		saveFileChooser = new JFileChooser();
		saveFile = new File("CapturedScreen.jpeg");
		saveFileChooser.setSelectedFile(saveFile);
		saveFileChooser.setFileFilter(new ImageFilter());
		saveFileChooser.setAcceptAllFileFilterUsed(false);
		while(run){
			int retVal = saveFileChooser.showSaveDialog(JViewerApp.getInstance().getRCView());
			if(retVal == JFileChooser.APPROVE_OPTION){// save button pressed
				saveFile = saveFileChooser.getSelectedFile();
				//ensure that the file is saved with .jpeg extension only
				if(!saveFile.getName().endsWith(".jpeg"))
					saveFile = new File(saveFile.getPath().concat(".jpeg"));
				saveImage();
			}
			else// cancel button pressed
				run = false;// quit the thread;
		}
	}

	/**
	 * Saves the captured screen image in the client system.
	 */
	private void saveImage(){
		try {
			//if selected file already exists.
			if(saveFile.exists()){
				int ret = JOptionPane.showConfirmDialog(saveFileChooser, LocaleStrings.getString("AF_1_CS")+" \""+
						saveFile.getName()+"\" "+LocaleStrings.getString("AF_2_CS"), LocaleStrings.getString("F_121_JVM"),
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(ret == JOptionPane.YES_OPTION){
					//replace the existing file.
					ImageIO.write(capturedFrame, "jpeg",saveFile);
					run = false;
				}
				else //save with a different name or location
					run = true;
			}
			else{//create a new file with the given name.
				saveFile.createNewFile();
				ImageIO.write(capturedFrame, "jpeg",saveFile);
				run = false;
			}
		}catch (IOException e) {
			Debug.out.println(e);
		}
	}

	private class ImageFilter extends FileFilter {

		//Accept all directories and all jpeg files.
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = getExtension(f);
			if (extension != null) {
				if (extension.equals("jpeg"))
					return true;
				else
					return false;
			}

			return false;
		}

		private String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 &&  i < s.length() - 1) {
				ext = s.substring(i+1).toLowerCase();
			}
			return ext;
		}

		//The description of this filter
		public String getDescription() {
			return "JPEG";
		}
	}

}
