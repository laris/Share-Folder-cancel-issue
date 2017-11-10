package com.ami.kvm.jviewer.common;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface IAVIOutputStream {

    /**
     * Supported video encodings.
     */
    public static enum VideoFormat {

        RAW, JPG, PNG;
    }
	/**
	 * Used with frameRate to specify the time scale that this stream will use.
	 * Dividing frameRate by timeScale gives the number of samples per second.
	 * For video streams, this is the frame rate. For audio streams, this rate
	 * corresponds to the time needed to play nBlockAlign bytes of audio, which
	 * for PCM audio is the just the sample rate.
	 * <p>
	 * The default value is 1.
	 *
	 * @param newValue
	 */
	public abstract void setTimeScale(long newValue);

	/**
	 * Returns the time scale of this media.
	 *
	 * @return time scale
	 */
	public abstract long getTimeScale();

	/**
	 * Sets the rate of video frames in time scale units.
	 * <p>
	 * The default value is 30. Together with the default value 1 of timeScale
	 * this results in 30 frames pers second.
	 *
	 * @param newValue
	 */
	public abstract void setFrameRate(long newValue);

	/**
	 * Returns the frame rate of this media.
	 *
	 * @return frame rate
	 */
	public abstract long getFrameRate();

	/**
	 * Sets the compression quality of the video track.
	 * A value of 0 stands for "high compression is important" a value of
	 * 1 for "high image quality is important".
	 * <p>
	 * Changing this value affects frames which are subsequently written
	 * to the AVIOutputStream. Frames which have already been written
	 * are not changed.
	 * <p>
	 * This value has only effect on videos encoded with JPG format.
	 * <p>
	 * The default value is 0.9.
	 *
	 * @param newValue
	 */
	public abstract void setVideoCompressionQuality(float newValue);

	/**
	 * Returns the video compression quality.
	 *
	 * @return video compression quality
	 */
	public abstract float getVideoCompressionQuality();

	/**
	 * Sets the dimension of the video track.
	 * <p>
	 * You need to explicitly set the dimension, if you add all frames from
	 * files or input streams.
	 * <p>
	 * If you add frames from buffered images, then AVIOutputStream
	 * can determine the video dimension from the image width and height.
	 *
	 * @param width Must be greater than 0.
	 * @param height Must be greater than 0.
	 */
	public abstract void setVideoDimension(int width, int height);

	/**
	 * Gets the dimension of the video track.
	 * <p>
	 * Returns null if the dimension is not known.
	 */
	public abstract Dimension getVideoDimension();

	/**
	 * Writes a frame to the video track.
	 * <p>
	 * If the dimension of the video track has not been specified yet, it
	 * is derived from the first buffered image added to the AVIOutputStream.
	 *
	 * @param image The frame image.
	 *
	 * @throws IllegalArgumentException if the duration is less than 1, or
	 * if the dimension of the frame does not match the dimension of the video
	 * track.
	 * @throws IOException if writing the image failed.
	 */
	public abstract boolean writeFrame(BufferedImage image) throws IOException;

	/**
	 * Writes a frame from a file to the video track.
	 * <p>
	 * This method does not inspect the contents of the file.
	 * For example, Its your responsibility to only add JPG files if you have
	 * chosen the JPEG video format.
	 * <p>
	 * If you add all frames from files or from input streams, then you
	 * have to explicitly set the dimension of the video track before you
	 * call finish() or close().
	 *
	 * @param file The file which holds the image data.
	 *
	 * @throws IllegalStateException if the duration is less than 1.
	 * @throws IOException if writing the image failed.
	 */
	public abstract void writeFrame(File file) throws IOException;

	/**
	 * Writes a frame to the video track.
	 * <p>
	 * This method does not inspect the contents of the file.
	 * For example, its your responsibility to only add JPG files if you have
	 * chosen the JPEG video format.
	 * <p>
	 * If you add all frames from files or from input streams, then you
	 * have to explicitly set the dimension of the video track before you
	 * call finish() or close().
	 *
	 * @param in The input stream which holds the image data.
	 *
	 * @throws IllegalArgumentException if the duration is less than 1.
	 * @throws IOException if writing the image failed.
	 */
	public abstract void writeFrame(InputStream in) throws IOException;

	/**
	 * Closes the movie file as well as the stream being filtered.
	 *
	 * @exception IOException if an I/O error has occurred
	 */
	public abstract void close() throws IOException;

	/**
	 * Finishes writing the contents of the AVI output stream without closing
	 * the underlying stream. Use this method when applying multiple filters
	 * in succession to the same output stream.
	 *
	 * @exception IllegalStateException if the dimension of the video track
	 * has not been specified or determined yet.
	 * @exception IOException if an I/O exception has occurred
	 */
	public abstract void finish() throws IOException;

	public abstract void initVideoFrames();

	public abstract void setOutputFile(File file);

	public abstract void setVideoFormat(VideoFormat jpg);

	public abstract void setSingleVideo(boolean singleVideo);

	public abstract boolean isStreamClosed();

	public abstract void setStreamClosed(boolean b);

}