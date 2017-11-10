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

package com.ami.kvm.jviewer.gui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import com.ami.kvm.jviewer.Debug;
import com.ami.kvm.jviewer.JViewer;
import com.ami.kvm.jviewer.common.IAVIOutputStream;

public class ImagesToVideo extends TimerTask {

	/**
	 * @param args
	 *            the command line arguments
	 */
	private IAVIOutputStream out = null;

	private String movFile;
	private String imagePath;
	private long numOfFrames;
	private long duration;
	private int i = 1;
	private long count;
	private double scalex;
	private double scaley;
	private File frameFile;
	private BufferedImage image;
	private boolean singleVideo;
	public Timer imagesToVideo;
	private int currentFile = -1;
	private int totalFiles = -1;

	public ImagesToVideo(String movFile, String imagePath, long numOfFrames, long duration) {
		this.movFile = movFile;
		this.imagePath = imagePath;
		this.numOfFrames = numOfFrames;
		this.duration = duration;
		singleVideo = JViewerApp.getInstance().getM_videorecord().singleVideo;

		if (JViewerApp.getInstance().getVideorecordapp() != null) {
			this.currentFile = JViewerApp.getInstance().getVideorecordapp().getFileIndex();
			this.totalFiles = JViewerApp.getInstance().getVideorecordapp().getVideotmpstorepath().length;
		}

		// create outputstream object only if there is a atleast a single non blank frame.
		if ((numOfFrames > 0) && (numOfFrames > JViewerApp.getInstance().getM_videorecord().getM_videorecord().getNum_blank_frames())) {
			out = (IAVIOutputStream) JViewerApp.getInstance().createObject("com.ami.kvm.jviewer.avistream.AVIOutputStream");
			out.initVideoFrames();
			out.setOutputFile(new File(movFile));
			out.setVideoFormat(IAVIOutputStream.VideoFormat.JPG);
			out.setSingleVideo(singleVideo);
			// Setting a moderate compression level will reduce file size without affecting video quality.
			out.setVideoCompressionQuality(0.75f);
			out.setTimeScale(duration);
			out.setFrameRate(numOfFrames);
		}

		image = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_RGB);
		imagesToVideo = new Timer(true);
		imagesToVideo.schedule(this, 0);

	}

	public void run() {
		try {
			// convert to video only if there is a atleast a single non blank frame. 
			if (numOfFrames > 0 && numOfFrames > JViewerApp.getInstance().getM_videorecord().getM_videorecord().getNum_blank_frames()) {
				for (count = 0; count < numOfFrames; count++) {
					if (!singleVideo && out.isStreamClosed())
						multipleVideos();
					String fileName = "file" + count + ".jpeg";
					frameFile = new File(imagePath, fileName);
					image = ImageIO.read(frameFile);
					if (singleVideo && (image.getWidth() != 1024 || image.getHeight() != 768)) {
						image = singleVideo(image);
					}
					if (out.writeFrame(image))//delete each frame as it is added to the avi stream.	
						frameFile.delete();
				}
			}
			if (out != null) {
				out.close();
			}
			this.cancel();
			if (!JViewer.isdownloadapp()) {
				JViewerApp.getInstance().getM_wndFrame().getWindowMenu().setMessage(""); //Hide the message in the menu bar
			}
			JInternalFrame mframe = JViewerApp.getInstance().getMainWindow();
			VideoRecord.Record_Processing = false;
			if (JViewerApp.getInstance().getM_frame().videoStopError != null)
				JViewerApp.getInstance().getM_frame().videoStopError.dispose();
			else {
				if (JViewer.isdownloadapp()) {
					JViewerApp.getInstance().getVideorecordapp().disposeInformationDialog();
					if (VideoRecord.file_creat_success == true) {
						JOptionPane.showMessageDialog(mframe, LocaleStrings.getString("L_1_ITOV") + LocaleStrings.getString("L_7_ITOV") + movFile
								+ LocaleStrings.getString("L_2_ITOV"), LocaleStrings.getString("L_3_ITOV"), JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(mframe, LocaleStrings.getString("L_1_ITOV") + LocaleStrings.getString("L_8_ITOV")
								+ LocaleStrings.getString("L_7_ITOV") + movFile + LocaleStrings.getString("L_2_ITOV"),
								LocaleStrings.getString("L_3_ITOV"), JOptionPane.INFORMATION_MESSAGE);
					}
					JViewerApp.getInstance().getVideorecordapp().setFileIndex(++currentFile);

					if (currentFile < totalFiles) {
						//Files available to convert notify thread to start conversion
						JViewerApp.getInstance().getVideorecordapp().availToConvert = true;
						synchronized (JViewerApp.getInstance().getVideorecordapp().convert) {
							JViewerApp.getInstance().getVideorecordapp().convert.notify();
						}
					} else {
						JViewerApp.getInstance().getM_frame().windowClosed();
					}
				} else {
					//Delete folder in java temp
					File directory = new File(JViewerApp.getInstance().getM_videorecord().Temp_store_Path);
					directory.delete();
					if (!(numOfFrames > 0 && numOfFrames > JViewerApp.getInstance().getM_videorecord().getM_videorecord().getNum_blank_frames())) {
						InfoDialog.showDialog(JViewer.getMainFrame(), LocaleStrings.getString("L_9_ITOV"), LocaleStrings.getString("L_3_ITOV"),
								InfoDialog.INFORMATION_DIALOG);
					} else if (VideoRecord.file_creat_success == true) {
						InfoDialog.showDialog(JViewer.getMainFrame(),
								LocaleStrings.getString("L_7_ITOV") + movFile + "\n" + LocaleStrings.getString("L_4_ITOV")
										+ (int) VideoRecord.Avg_fps + LocaleStrings.getString("L_5_ITOV"), LocaleStrings.getString("L_3_ITOV"),
								InfoDialog.INFORMATION_DIALOG);
					} else {
						InfoDialog.showDialog(
								JViewer.getMainFrame(),
								LocaleStrings.getString("L_8_ITOV") + LocaleStrings.getString("L_7_ITOV") + movFile + "\n"
										+ LocaleStrings.getString("L_4_ITOV") + (int) VideoRecord.Avg_fps + LocaleStrings.getString("L_5_ITOV"),
								LocaleStrings.getString("L_3_ITOV"), InfoDialog.INFORMATION_DIALOG);
					}
				}
			}

			//Enable video record settings menu once recording is completed.
			if (!JViewer.isdownloadapp()) {
				if (JViewerApp.getInstance().GetRedirectionState() == JViewerApp.REDIR_STARTED
						|| JViewerApp.getInstance().GetRedirectionState() == JViewerApp.REDIR_STARTING) {
					JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_RECORD_START, true);
					JViewerApp.getInstance().getJVMenu().notifyMenuStateEnable(JVMenu.VIDEO_RECORD_SETTINGS, true);
				}
			}

		} catch (IOException ie) {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				Debug.out.println(e);
			}
			this.cancel();
			String errorMsg = LocaleStrings.getString("L_6_ITOV");
			JViewerApp.getInstance().getM_videorecord().OnLowDiskSpace(frameFile, errorMsg);
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				Debug.out.println(e);
			}

		}

	}

	private BufferedImage singleVideo(BufferedImage img) {
		BufferedImage tempimg = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_RGB);
		scalex = 1024 / (double) img.getWidth();
		scaley = 768 / (double) img.getHeight();
		AffineTransform affine = AffineTransform.getScaleInstance(scalex, scaley);
		Graphics2D g2d = tempimg.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.drawRenderedImage(img, affine);
		return (tempimg);

	}

	private void multipleVideos() {
		count--;
		out.setStreamClosed(false);
		out = (IAVIOutputStream) JViewerApp.getInstance().createObject("com.ami.kvm.jviewer.avistream.AVIOutputStream");
		out.initVideoFrames();
		out.setOutputFile(new File(movFile + "_" + i + ".avi"));
		out.setVideoFormat(IAVIOutputStream.VideoFormat.JPG);
		out.setSingleVideo(singleVideo);
		out.setFrameRate(numOfFrames);
		out.setTimeScale(duration);
		out.setVideoCompressionQuality(1f);
		i++;
	}

}
