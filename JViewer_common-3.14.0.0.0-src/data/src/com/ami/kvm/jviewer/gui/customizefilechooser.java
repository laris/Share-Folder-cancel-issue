package com.ami.kvm.jviewer.gui;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import com.ami.kvm.jviewer.JViewer;
public class customizefilechooser {


/**
	 * Customize file chooser
	 * @param parent
	 */

	public  void customizeFileChooser(Container parent) {
	     Component[] c = parent.getComponents();
	     for(int j = 0; j < c.length; j++) {
	    	  if(unpackDialogComponents(c[j]).equals("MetalFileChooserUI$3")){ // remove filename textbox
	         	c[j].setEnabled(false);
	            c[j].getParent().setVisible(false);
	         }

	         if(unpackDialogComponents(c[j]).equals("JComboBox")){ //remove filetype combobox
	         	c[j].setEnabled(false);
	            c[j].getParent().setVisible(false);
	         }
	         if(((Container)c[j]).getComponentCount() > 0)
	        	 customizeFileChooser((Container)c[j]);
	     }
	 }


	 private String unpackDialogComponents(Component c) {
	     String s = c.getClass().getName();
	     int dot = s.lastIndexOf(".");
	     if(dot != -1)
	         s = s.substring(dot+1);
	     return s;
	 }

}