/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2017, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
 ****************************************************************/

package com.ami.iusb;

//import java.nio.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.Dimension;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.ami.kvm.jviewer.Debug;

/*
* java default string is UTF8
*/

public class MountFolder extends Thread{
	public String folderPath = null;
	public String imagePath = null;
	public long imageSize = 0;
	public long imageCreateTime = 0;
	RandomAccessFile pImg = null;
	MasterBootRecord ptable = null;
	public int action_flag = 0;
	private static int ACTIONFLAGCREATE = 1;
	public int image_result = 0;
	//private boolean paused = false;
	//private final Object controlSyncThread = new Object();

	//create image constructor
	public MountFolder(long imageSize, String folderPath, String imagePath) {
		
		this.imageSize = imageSize;
		this.folderPath = folderPath;
		this.imagePath = imagePath;
	}

	public void createMBR() {
			this.ptable = new MasterBootRecord(imagePath, folderPath, imageSize);
			imageCreateTime = ptable.imageCreateTime;
	}
	
	//Create random access file for image
	public void createAccessFile() {
			try {
				pImg = new RandomAccessFile(imagePath, "rw");
				ptable = new MasterBootRecord(pImg);
			}
			catch(IOException e) {
				e.printStackTrace(); 
			}
	}

	public void run(){
		image_result = 0;
		if(action_flag == ACTIONFLAGCREATE) {
			createImage();
			if(ptable.folder_size == 0)//for special case, mount a emtpy folder to the host
				ptable.complete_percent = 100;
		}
		else {
			syncToDisk();
		}

	}

	public int getPercent(){
		if(ptable != null)
			return ptable.complete_percent;
		else
			return 100;//if ptable is null, that means image create done.
						//and the ptable will close
	}

	public boolean ImageSizeCheck() {
		File temp = new File(folderPath);
		long folder_size = 0;
		long tmp_imageSize = (long)(imageSize * 1024L * 1024L);

		folder_size = MasterBootRecord.getFileFolderSize(temp);
		if(folder_size > tmp_imageSize ) {
			return false;
		}
		else {
			createMBR();
			ptable.folder_size = folder_size;
			return true;
		}
	}

	public void stopRun() {
		ptable.setStopFlag();
	}

	public void pauseRun() {
		ptable.setPauseFlag();
	}

	public void resumeRun() {
		ptable.setResumeFlag();
	}

	public void createImage() {

		ptable.getImageFilePointer();
		image_result = ptable.createImage(folderPath);
	}

	public void stopAccessImage() {
	
		if(ptable != null) {
			ptable.closeChannel();
			ptable = null;
		}

		try {
			if(pImg != null) {
				pImg.close();
				pImg = null;
			}
		}		
		catch(Exception e) {
			e.printStackTrace(); 
		}
	}

	public boolean checkValidPath() {
		Path image = Paths.get(imagePath);
		Path folder = Paths.get(folderPath);

		// image path cannot be the same as or inside the folder path
		return !image.startsWith(folder);
	}
	
	public void syncToDisk() {
		try {
			File path_f = new File(folderPath);	
			if(!path_f.exists())
				path_f.mkdirs();

			ptable.imageCreateTime = imageCreateTime;
			ptable.getBootSectorInfo(0);

			ptable.getFileList(path_f.getPath());
			ptable.folderSizeCalculate();

			if(ptable.using_fat16)
				image_result = ptable.listFilesFromImage(ptable.rootStartAddr, path_f.getPath() +  File.separator, null, MasterBootRecord.NON_MULTI_CLUSTER);
			else {
				//This is for FAT 32 Root Directory multi cluster check
				//if using FAT 32, need to check root start addr FAT table, 
				//make sure that root directory is multi cluster or not
				long fat_index = 0;
				long tmp_cluster_index= 0;// folder start cluster 
				long tmp_data_addr = 0;
				long[] result = new long[1];//FAT cluster index array

				tmp_data_addr = ptable.rootStartAddr;
				//based on data addr, calculate fat index in FAT table
				fat_index = (tmp_data_addr - ptable.data_start_addr) / (ptable.sector_size * ptable.sectors_per_cluster) + ptable.FAT_RESERVED_RECORD_NUM;
				//save fat_index into array
				result[0] = fat_index;
	
				try {
					for(;;) {
						//go to FAT table, read next fat index data
						pImg.seek(ptable.fat_start_addr + fat_index * ptable.FAT_RECORD_SIZE);
						tmp_cluster_index = (long)ptable.ChangeToLittleEndian(pImg.readInt(),4, MasterBootRecord.LITTLE_ENDIAN_TYPE);
			
						if(tmp_cluster_index != 0x0FFFFFFF) {//FAT 32, end of character
							result = ptable.extendLong(result, tmp_cluster_index);

							//based on fat index data, calculate data addr and then go to step 1
							tmp_data_addr = ptable.data_start_addr + (tmp_cluster_index - ptable.FAT_RESERVED_RECORD_NUM) * (ptable.sector_size * ptable.sectors_per_cluster);
							//based on data addr, calculate fat index in FAT table
							fat_index = (tmp_data_addr - ptable.data_start_addr) / (ptable.sector_size * ptable.sectors_per_cluster) + ptable.FAT_RESERVED_RECORD_NUM;
						}
						else
							break;
					}
				}	
				catch(IOException e) {
					e.printStackTrace();	
				}
	
				if(result.length > 1) {
					for( int i = 0 ; i < result.length ; i++ ) {
						long tmp_root_addr = 0;
						tmp_root_addr = ptable.data_start_addr + (result[i] - ptable.FAT_RESERVED_RECORD_NUM) * (ptable.sector_size * ptable.sectors_per_cluster);
			 		}

			 		//root directory using multi cluster, need to using for loop to go through all cluster
					long tmp_root_addr = 0;
					for( int i = 0 ; i < result.length ; i++ ) {
						tmp_root_addr = ptable.data_start_addr + (result[i] - ptable.FAT_RESERVED_RECORD_NUM) * (ptable.sector_size * ptable.sectors_per_cluster);
						image_result = ptable.listFilesFromImage(tmp_root_addr, path_f.getPath() +  File.separator, result, i);
						if(image_result < 0)
							return;
					}
				}
				else {
					//root directory just using 1 cluster.
					image_result = ptable.listFilesFromImage(ptable.rootStartAddr, path_f.getPath() +  File.separator, null, MasterBootRecord.NON_MULTI_CLUSTER);
				}
			}
			ptable.delFileCheck();

		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

}

class ImageProgress extends Thread{
	private JProgressBar progressBar;

	private MountFolder folder = null;
	public boolean progress_complete = false;//progress complete
	public int progress_result = 0;
    private static int ACTIONFLAGSYNC = 2;
	private boolean synchronizePause = false;
	private final Object controlSyncThread = new Object();
	private boolean userChoose = false; //true - resume, false - break sync

	//for create image constructor
	public ImageProgress(long imageSize, String folderPath, String imagePath,
				JProgressBar progressBar) {
		folder = new MountFolder(imageSize, folderPath, imagePath);
		this.progressBar = progressBar;
	}

	public boolean getImageSizeCheck() {
		if(folder.ImageSizeCheck() == false)
			return false;
		else
			return true;
	}

	public boolean checkValidPath() {
		return folder.checkValidPath();
	}

	public long getImageCreateTime() {
		return folder.imageCreateTime;
	}

	public void setImageCreateTime(long imageCreateTime) {
		folder.imageCreateTime = imageCreateTime;
	}

	public boolean getUserChoose(){
		return userChoose;
	}

	public void setUserChoose(boolean userChoose){
		this.userChoose = userChoose;
	}

	public void setActionFlag(int action_flag) {
		//action_flag == 1, means create Image
		//action_flag == 2, means synchronized to disk
		folder.action_flag = action_flag;
		if(action_flag == ACTIONFLAGSYNC)
			folder.createAccessFile();
	}

	public void pauseProgress() {
		folder.pauseRun();
		synchronized(controlSyncThread) {
            synchronizePause = true;
            controlSyncThread.notifyAll();
        }
	}

	public void resumeProgress() {
		folder.resumeRun();
		synchronized(controlSyncThread) {
	        synchronizePause = false;
			try {
				//Delay 10 wait folder closed
			 	Thread.sleep(10);
			}
			catch (Exception e) {
				Debug.out.println("resumeProgress delay error!");
			}
            controlSyncThread.notifyAll();
        }
	}

	public void resumeProgressBar() {
		if(getUserChoose()){
			setUserChoose(false);
			while(folder.getPercent() <= 100)
			{
				if(folder.getPercent() >= 95) 
				{
					folder.stopAccessImage();
					folder = null;
					this.stop();
					JOptionPane.getRootFrame().dispose();
					break;
				}
				try {
					//Delay 15 milliseconds make CPU happy
				 	Thread.sleep(15);
				}
				catch (Exception e) {
					Debug.out.println("resumeProgressBar delay error!");
				}
			}
		
		}
		
	}
	public void stopProgress() {
		if(!getUserChoose()){
			if(folder != null) {
				if(folder.isAlive()) {
					folder.stopRun();
					try {
						//progress thread will waiting mount folder thread stop
						folder.join();
					}
					catch(InterruptedException e) {
						Debug.out.println("Waiting mount folder thread stop Error!!");
					}
				}
				folder.stopAccessImage();
				folder = null;
			}
			this.stop();
			JOptionPane.getRootFrame().dispose();
		}
	}

	public void run() {
		folder.start();
		long time1, time2;

		while(folder.getPercent() <= 100)
		{
			synchronized(controlSyncThread) {
				if (synchronizePause) {
					try {
						controlSyncThread.wait();
					}catch (InterruptedException e) {
						Debug.out.println("Progress bar run  error!");
					}
				}else{
					if(folder.getPercent() == 100) {
						progressBar.setValue(folder.getPercent());
						break;
					}
					progressBar.setValue(folder.getPercent());
					try {
						//Delay 10 milliseconds make CPU happy
				 		Thread.sleep(10);
					}
					catch (Exception e) {
					}

					if( !folder.isAlive() && folder.getPercent() < 100 ) {
						progress_result = folder.image_result;
						JOptionPane.getRootFrame().dispose();
					}
		   		}
			}
		}
		progress_complete = true;
		JOptionPane.getRootFrame().dispose();
	}
}

