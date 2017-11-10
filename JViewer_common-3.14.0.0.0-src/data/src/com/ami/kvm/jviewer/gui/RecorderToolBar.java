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
////////////////////////////////////////////////////////////////////////////////////////
//This module implements the toolbar for the VideoPlayerApp 
//
package com.ami.kvm.jviewer.gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.videorecord.DisplayVideoDataTask;
@SuppressWarnings("serial")
public class RecorderToolBar extends JVToolBar implements MouseListener{
	
	private JToolBar m_recToolbar;
	private int counter = 0;
	public JButton replayButton;	
	public static final String PAUSE = "PAUSE";
	public static final String PLAY = "PLAY";
	private static JSlider slider;
	private static JPanel panel;
	private static JLabel label;
	public RecorderToolBar(){
		m_recToolbar = new JToolBar();
		panel = new JPanel();
		label = new JLabel("0");
		slider = new JSlider(0, 200);
		slider.setEnabled(false);
		Dimension d = slider.getPreferredSize();  
		slider.setPreferredSize(new Dimension(d.width+500,d.height));
		slider.setMajorTickSpacing(20);
		slider.setVisible(false);
		label.setVisible(false);
		panel.add(slider);
		panel.add(label);
		panel.setVisible(false);
        	replayButton = createButton("res/pause.png",
        	LocaleStrings.getString("R_2_RT"), 33, 33, 33, 33);
		
		m_recToolbar.add(replayButton);
		
		Border m_raised = new EtchedBorder(BevelBorder.LOWERED);		
        m_recToolbar.setBorder(m_raised);
        m_recToolbar.setFloatable(false);        
        m_recToolbar.repaint();
	}
	public JToolBar getToolBar() {
		return m_recToolbar;
	}
	public void mouseClicked(MouseEvent e) {
		
	}
	public void mouseEntered(MouseEvent e) {
		
	}
	public void mouseExited(MouseEvent e) {
		
	}

    public void mousePressed(MouseEvent e) {
	if (replayButton.getActionCommand().equals(PAUSE)) {
	    if (DisplayVideoDataTask.getVideoDuration() != 0 && JViewer.isplayerapp()) {
		counter = DisplayVideoDataTask.getCounter();
		DisplayVideoDataTask.pause();
	    }
	    replayButton.setIcon(new ImageIcon(com.ami.kvm.jviewer.JViewer.class.getResource("res/play.png")));
	    replayButton.setToolTipText(LocaleStrings.getString("R_1_RT"));
	    replayButton.setActionCommand(PLAY);
	    DisplayVideoDataTask.setPaused(true);
	} else if (replayButton.getActionCommand().equals(PLAY)) {
	    if (DisplayVideoDataTask.getVideoDuration() != 0 && JViewer.isplayerapp()) {
		DisplayVideoDataTask.resume();
		DisplayVideoDataTask.setCounter(counter);
	    }
	    replayButton.setIcon(new ImageIcon(com.ami.kvm.jviewer.JViewer.class.getResource("res/pause.png")));
	    replayButton.setToolTipText(LocaleStrings.getString("R_2_RT"));
	    replayButton.setActionCommand(PAUSE);
	    synchronized (DisplayVideoDataTask.getPlayLock()) {
		DisplayVideoDataTask.setPaused(false);
		DisplayVideoDataTask.getPlayLock().notifyAll();
	    }
	} else {
	    JViewerApp.getInstance().getVideorecordapp().OnVideorecordStartRedirection();
	}
	}
	public void mouseReleased(MouseEvent e) {
	}
	/**
	 * Disables the given ToolBar button
	 * @param button - The button to be disabled
	 */
	public void disableButton(JButton button){
		button.setEnabled(false);
		button.removeMouseListener(this);
	}
	/**
	 * Enables the given ToolBar button
	 * @param button - The button to be enabled
	 */
	public void enableButton(JButton button){
		button.setEnabled(true);
		button.addMouseListener(this);
	}
	/**
	 * @return the panel
	 */
	public static JPanel getPanel() {
	    return panel;
	}
	/**
	 * @return the label
	 */
	public static JLabel getLabel() {
	    return label;
	}
	/**
	 * @param label the label to set
	 */
	public static void setLabel(JLabel label) {
	    RecorderToolBar.label = label;
	}
	/**
	 * @return the slider
	 */
	public static JSlider getSlider() {
	    return slider;
	}
	/**
	 * @param slider the slider to set
	 */
	public static void setSlider(JSlider slider) {
	    RecorderToolBar.slider = slider;
	}
	
	public static void updateComponents(int duration) {
	    label.setVisible(true);
	    slider.setVisible(true);
	    panel.setVisible(true);
	    slider.setMaximum(duration * 10);
	}
}
