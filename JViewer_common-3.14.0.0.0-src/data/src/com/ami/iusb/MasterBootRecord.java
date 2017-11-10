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

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.EOFException;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.text.ParseException;

public class MasterBootRecord
{
	private RandomAccessFile pImg;
	private FileChannel ImgChannel;//image file
	private RandomAccessFile fileRaf;
	private FileChannel fileChannel;//image file
	
 	private FileList[] FileListArray;
	long g_fat_index = 0;//the last used cluster index
	long imageCreateTime = 0;
	long FileListCreatTime = 0;
	long fat32_free_cluster = 0;

	public static int FAT_RESERVED_RECORD_NUM = 0;
	public static int FAT_RECORD_SIZE = 0;
	public static int PARTITION_TABLE_SIZE = 16;
	public static int BOOT_INDICATOR = 0X1BE;
	public static int BOOT_SECTOR_SIZE = 512;
	public static int ROOT_DIR_ENTRY_SIZE = 32;
	public static int LITTLE_ENDIAN_TYPE = 0;
	public static int BIG_ENDIAN_TYPE = 1;
	public static byte FILE_ATTRI_HIDDEN = 0x2;
	public static byte FILE_ATTRI_SYSTEM = 0x4;
	public static byte FILE_ATTRI_SUBFOLDER = 0x10;
	public static byte FILE_ATTRI_FILE = 0x20;
	public static int UNSIGNED_BYTE_MASK = 0x000000FF;
	public static int UNSIGNED_SHORT_MASK = 0x0000FFFF;
	public static long UNSIGNED_INT_MASK = 0x00000000FFFFFFFF;
	public static int LFN_CHAR_LEN_IN_BYTE = 26;
	public static int LFN_CHAR_SIZE = 13;
	public static int NON_MULTI_CLUSTER = -1;

	/* MBR, Partition table structure */
	public int first_byte = 0;			//using to define this partition is active(0x80) or non-active(0x00)
	public int[] start_chs = new int[3];
	public int partition_type = 0;		//System ID, using to verify what this partition is
	public int[] end_chs = new int[3];
	public long start_sector = 0;
	public long length_sector = 0;		//using to define partition size

	/* FAT Boot sector structure also called DBR */
	public int[] jmp = new int[3];
	public char[] oem = new char[8];
	public int sector_size = 0;
	public int sectors_per_cluster = 0;
	public int reserved_sector = 0;
	public int number_of_fat = 0;
	public int root_dir_entry = 0;
	public int total_block = 0;// if zero, later field is used
	public int media_descriptor = 0;
	public int fat_size_sector = 0;
	public int sector_per_track = 0;
	public int number_of_head = 0;
	public long hidden_sector = 0;
	public long total_sector_long = 0;
	public long fat32_fat_size_sector = 0;
	public int fat32_flag = 0;					//only for FAT 32
	public int fat32_version = 0;				//only for FAT 32
	public long fat32_root_start_cluster = 0;	//only for FAT 32
	public int fat32_fsinfo_addr = 0;			//only for FAT 32
	public int fat32_backup_fsinfo_addr = 0;	//only for FAT 32
	public byte[] fat32_reserved = new byte[12];//only for FAT 32
	public int drive_number = 0;
	public int current_head = 0;
	public int boot_signature = 0;
	public long volume_id = 0;
	public char[] volume_label = new char[11];
	public char[] fs_type = new char[8];
	public char[] boot_code;
	
	public int boot_sector_signature;

	public long fat_start_addr = 0;
	public long rootStartAddr = 0;
	public long data_start_addr = 0;
	public long fat32_fat2_addr = 0;
	public long fat32_fsinfo1_addr = 0;
	public long fat32_fsinfo2_addr = 0;
	public byte lfn_sequence = 0x31;
	public boolean using_fat16 = false;
	
	public long folder_size = 0;
	public long copyed_size = 0;
	public int complete_percent = 0;
	public boolean user_stop_flag = false;
	
	private int lfNameSec1 = 10;
	private int lfNameSec2 = 12;
	private int lfNameSec3 = 4;

	private final Object controlSynchronized = new Object();
	private boolean userPausedSync = false;


	public MasterBootRecord(RandomAccessFile pImg)
	{
		this.pImg = pImg;
	}

	public MasterBootRecord(String image, String folder, long size)
	{
		folder_size = 0;
		copyed_size = 0;
	
		byte[] tmp_ary = new byte[BOOT_SECTOR_SIZE];
		ByteBuffer buf = null;
		
		try {
			//if the old image file exists, need to delete first
			File temp = new File(image);
			if(temp.exists()) {
				temp.delete();
			}

			//if using 1024 * 1024 * 1028, it will over than 2G and the Host OS
			//will not understand this image
			pImg = new RandomAccessFile(image, "rw");
			pImg.setLength(size * 1024L * 1024L);

			File time_f = new File(image);
			imageCreateTime = time_f.lastModified();
		}
		catch(Exception e) {
			e.printStackTrace(); 
		}
		ImgChannel = pImg.getChannel();

		if(size <= 2 * 1024L) {
			FAT_RESERVED_RECORD_NUM = 2;//FAT 16 default using 2 record in FAT table, but FAT 32 using 3
			FAT_RECORD_SIZE = 2;
			boot_code = new char[448];
			using_fat16 = true;
		}
		else {
			FAT_RESERVED_RECORD_NUM = 3;
			FAT_RECORD_SIZE = 4;
			boot_code = new char[420];
			using_fat16 = false;
		}
		
		try {
			if(using_fat16) {//FAT16
				jmp[0] = 0xEB; jmp[1] = 0x3C; jmp[2] = 0x90; 
			}
			else {//FAT32
				jmp[0] = 0xEB; jmp[1] = 0x58; jmp[2] = 0x90; 
			}
			
			oem[0] = 0x4D; oem[1] = 0x53; oem[2] = 0x44;
			oem[3] = 0x4F; oem[4] = 0x53; oem[5] = 0x35;
			oem[6] = 0x2E; oem[7] = 0x30;
			
			sector_size = ChangeToLittleEndian(512, 2, LITTLE_ENDIAN_TYPE);
			// volume size in the range of 0MB-32MB
			if(size > 0L && size <= 32L)
				sectors_per_cluster = 1;
			// volume size in the range of 33MB-64MB
			else if(size > 32L && size <= 64L)
				sectors_per_cluster = 2;
			// volume size in the range of 65MB-128MB
			else if(size > 64L && size <= 128L)
				sectors_per_cluster = 4;
			// volume size in the range of 129MB-255MB
			else if(size > 128L && size <= 255L)
				sectors_per_cluster = 8;
			// volume size in the range of 256MB-511MB
			else if(size > 255L && size <= 511L)
				sectors_per_cluster = 16;
			// volume size in the range of 512MB-1023MB
			else if(size > 511L && size <= 1023L)
				sectors_per_cluster = 32;
			// volume size in the range of 1024MB-2047MB
			else if(size > 1023L && size <= 2047L)
				sectors_per_cluster = 64;
			// volume size in the range of 2048MB-8191MB
			else if(size > 2047L && size <= 8191L)	//FAT 32, 4 KB
				sectors_per_cluster = 8;
			// volume size greater than or equal to 8GB, and less than 16GB
			else if(size > 8191L && size <= 16383L)	//FAT 32, 8 KB
				sectors_per_cluster = 16;
			// volume size greater than or equal to 16GB, and less than 32GB
			else if (size > 16383L && size <= 32767L)//FAT 32, 16 KB
				sectors_per_cluster = 32;
			// volume size greater than or equal to 32GB
			else if(size > 32767L)				//FAT 32, 32 KB
				sectors_per_cluster = 64;

			if(using_fat16) //reserved_sector == FAT start addr
				reserved_sector = ChangeToLittleEndian(6, 2, LITTLE_ENDIAN_TYPE);
			else {
				reserved_sector = ChangeToLittleEndian(38, 2, LITTLE_ENDIAN_TYPE);
			}
			
			number_of_fat = 2;

			if(using_fat16) {
				root_dir_entry = ChangeToLittleEndian(512, 2, LITTLE_ENDIAN_TYPE);
			}
			else {
				root_dir_entry = ChangeToLittleEndian(0, 2, LITTLE_ENDIAN_TYPE);
			}
			
			total_block = ChangeToLittleEndian(0, 2, LITTLE_ENDIAN_TYPE);
			media_descriptor = 0xF8;

			if(using_fat16)
				fat_size_sector = ChangeToLittleEndian( (int)calculateFATTableSize(), 2, LITTLE_ENDIAN_TYPE);
			else
				fat_size_sector = ChangeToLittleEndian(0, 2, LITTLE_ENDIAN_TYPE);
			
			sector_per_track = ChangeToLittleEndian(63, 2, LITTLE_ENDIAN_TYPE);
			number_of_head  = ChangeToLittleEndian(255, 2, LITTLE_ENDIAN_TYPE);
			if(using_fat16)
				hidden_sector  = ChangeToLittleEndian(63, 4, LITTLE_ENDIAN_TYPE);
			else 
				hidden_sector = ChangeToLittleEndian(0, 4, LITTLE_ENDIAN_TYPE);
			total_sector_long = ChangeToLittleEndian((int)( pImg.length() / 512), 4, LITTLE_ENDIAN_TYPE);
			
			if( !using_fat16 ) {//only FAT32 had follow field
				fat32_fat_size_sector = ChangeToLittleEndian( (int)calculateFATTableSize(), 4, LITTLE_ENDIAN_TYPE);

				fat32_flag = 0;
				fat32_version = 0;
				fat32_root_start_cluster = ChangeToLittleEndian(2, 4, LITTLE_ENDIAN_TYPE);
				fat32_fsinfo_addr = ChangeToLittleEndian(1, 2, LITTLE_ENDIAN_TYPE);
				fat32_backup_fsinfo_addr = ChangeToLittleEndian(6, 2, LITTLE_ENDIAN_TYPE);
				for(int i = 0 ; i < fat32_reserved.length ; i++)
					fat32_reserved[i] = 0;
			}
			
			drive_number = 0x80;
			current_head = 0x0;
			boot_signature = 0x29;
			volume_id = ChangeToLittleEndian(0x1263EBDD, 4, LITTLE_ENDIAN_TYPE);
			volume_label[0] = 0x4E; volume_label[1] = 0x4F; volume_label[2] = 0x20; 
			volume_label[3] = 0x4E; volume_label[4] = 0x41; volume_label[5] = 0x4D; 
			volume_label[6] = 0x45; volume_label[7] = 0x20; volume_label[8] = 0x20; 
			volume_label[9] = 0x20; volume_label[10] = 0x20; 
			fs_type[0] = 0x46; fs_type[1] = 0x41; fs_type[2] = 0x54; 
			if(using_fat16) {//fill string - FAT16
				fs_type[3] = 0x31; fs_type[4] = 0x36; 
			}
			else { //fill string - FAT32
				fs_type[3] = 0x33; fs_type[4] = 0x32; 
			}
			fs_type[5] = 0x20; fs_type[6] = 0x20; fs_type[7] = 0x20; 
			if(using_fat16)
				fat16_boot_code_init();
			else
				fat32_boot_code_init();
			
			boot_sector_signature = ChangeToLittleEndian(0xAA55, 2, LITTLE_ENDIAN_TYPE);

			for(int i = 0 ; i < 3 ; i++)
				tmp_ary[i] = (byte)jmp[i];
			for(int i = 0 ; i < 8 ; i++)
				tmp_ary[i+3] = (byte)oem[i];
			tmp_ary[11] = (byte) ( (sector_size & 0xFF00) >> 8);
			tmp_ary[12] = (byte) (sector_size & 0x00FF);
			tmp_ary[13] = (byte)sectors_per_cluster;
			tmp_ary[14] = (byte) ( (reserved_sector & 0xFF00) >> 8);
			tmp_ary[15] = (byte) (reserved_sector & 0x00FF);
			tmp_ary[16] = (byte)number_of_fat;
			tmp_ary[17] = (byte) ( (root_dir_entry & 0xFF00) >> 8);
			tmp_ary[18] = (byte) (root_dir_entry & 0x00FF);
			tmp_ary[19] = (byte) ( (total_block & 0xFF00) >> 8);
			tmp_ary[20] = (byte) (total_block & 0x00FF);
			tmp_ary[21] = (byte)media_descriptor;
			tmp_ary[22] = (byte) ( (fat_size_sector & 0xFF00) >> 8);
			tmp_ary[23] = (byte) (fat_size_sector & 0x00FF);
			tmp_ary[24] = (byte) ( (sector_per_track & 0xFF00) >> 8);
			tmp_ary[25] = (byte) (sector_per_track & 0x00FF);
			tmp_ary[26] = (byte) ( (number_of_head & 0xFF00) >> 8);
			tmp_ary[27] = (byte) (number_of_head & 0x00FF);
			tmp_ary[28] = (byte) ((hidden_sector & 0xFF000000) >> 24);
			tmp_ary[29] = (byte) ((hidden_sector & 0x00FF0000) >> 16);
			tmp_ary[30] = (byte) ((hidden_sector & 0x0000FF00) >> 8);
			tmp_ary[31] = (byte)  (hidden_sector & 0x000000FF);
			tmp_ary[32] = (byte) ((total_sector_long & 0xFF000000) >> 24);
			tmp_ary[33] = (byte) ((total_sector_long & 0x00FF0000) >> 16);
			tmp_ary[34] = (byte) ((total_sector_long & 0x0000FF00) >> 8);
			tmp_ary[35] = (byte)  (total_sector_long & 0x000000FF);
			if(using_fat16) {//FAT16
				tmp_ary[36] = (byte)drive_number;
				tmp_ary[37] = (byte)current_head;
				tmp_ary[38] = (byte)boot_signature;
				tmp_ary[39] = (byte) ((volume_id & 0xFF000000) >> 24);
				tmp_ary[40] = (byte) ((volume_id & 0x00FF0000) >> 16);
				tmp_ary[41] = (byte) ((volume_id & 0x0000FF00) >> 8);
				tmp_ary[42] = (byte)  (volume_id & 0x000000FF);
				for(int i = 0 ; i < volume_label.length ; i++)
					tmp_ary[i+43] = (byte)volume_label[i];
				for(int i = 0 ; i < fs_type.length ; i++)
					tmp_ary[i+54] = (byte)fs_type[i];
				for(int i = 0 ; i < boot_code.length ; i++)
					tmp_ary[i+61] = (byte)boot_code[i];
			}
			else {//FAT32
				tmp_ary[36] = (byte) ((fat32_fat_size_sector & 0xFF000000) >> 24);
				tmp_ary[37] = (byte) ((fat32_fat_size_sector & 0x00FF0000) >> 16);
				tmp_ary[38] = (byte) ((fat32_fat_size_sector & 0x0000FF00) >> 8);
				tmp_ary[39] = (byte)  (fat32_fat_size_sector & 0x000000FF);
				tmp_ary[40] = (byte) fat32_flag;
				tmp_ary[41] = (byte) fat32_flag;
				tmp_ary[42] = (byte) fat32_version;
				tmp_ary[43] = (byte) fat32_version;
				tmp_ary[44] = (byte) ((fat32_root_start_cluster & 0xFF000000) >> 24);
				tmp_ary[45] = (byte) ((fat32_root_start_cluster & 0x00FF0000) >> 16);
				tmp_ary[46] = (byte) ((fat32_root_start_cluster & 0x0000FF00) >> 8);
				tmp_ary[47] = (byte)  (fat32_root_start_cluster & 0x000000FF);
				tmp_ary[48] = (byte) ( (fat32_fsinfo_addr & 0xFF00) >> 8);
				tmp_ary[49] = (byte) (fat32_fsinfo_addr & 0x00FF);
				tmp_ary[50] = (byte) ( (fat32_backup_fsinfo_addr & 0xFF00) >> 8);
				tmp_ary[51] = (byte) (fat32_backup_fsinfo_addr & 0x00FF);
				for(int i = 0 ; i < fat32_reserved.length ; i++)
					tmp_ary[i+52] = (byte)fat32_reserved[i];
				tmp_ary[64] = (byte)drive_number;
				tmp_ary[65] = (byte)current_head;
				tmp_ary[66] = (byte)boot_signature;
				tmp_ary[67] = (byte) ((volume_id & 0xFF000000) >> 24);
				tmp_ary[68] = (byte) ((volume_id & 0x00FF0000) >> 16);
				tmp_ary[69] = (byte) ((volume_id & 0x0000FF00) >> 8);
				tmp_ary[70] = (byte)  (volume_id & 0x000000FF);
				for(int i = 0 ; i < volume_label.length ; i++)
					tmp_ary[i+71] = (byte)volume_label[i];
				for(int i = 0 ; i < fs_type.length ; i++)
					tmp_ary[i+82] = (byte)fs_type[i];
				for(int i = 0 ; i < boot_code.length ; i++)
					tmp_ary[i+90] = (byte)boot_code[i];

			}
			tmp_ary[510] = (byte) ( (boot_sector_signature & 0xFF00) >> 8);
			tmp_ary[511] = (byte) (boot_sector_signature & 0x00FF);

			buf = ByteBuffer.wrap(tmp_ary);
			ImgChannel.position( 0 );
			ImgChannel.write(buf);

			getBootSectorInfo(0);
			fillFATtag();
			g_fat_index = 2;
			
			if( !using_fat16 ) {
				byte[] fsinfo_ary = new byte[BOOT_SECTOR_SIZE];
				//FSInfo tag
				fsinfo_ary[0] = (byte)0x52; fsinfo_ary[1] = (byte)0x52; 
				fsinfo_ary[2] = (byte)0x61; fsinfo_ary[3] = (byte)0x41;
				for(int i = 0 ; i < 480 ; i++)
					fsinfo_ary[i + 4] = (byte)0x0;
				//FSInfo sign
				fsinfo_ary[484] = (byte)0x72; fsinfo_ary[485] = (byte)0x72; 
				fsinfo_ary[486] = (byte)0x41; fsinfo_ary[487] = (byte)0x61; 

				fsinfo_ary[488] = (byte)0xFF;
				fsinfo_ary[489] = (byte)0xFF;
				fsinfo_ary[490] = (byte)0xFF;
				fsinfo_ary[491] = (byte)0xFF;
				
				fsinfo_ary[492] = (byte) (g_fat_index & 0x000000FF);
				fsinfo_ary[493] = (byte) ((g_fat_index & 0x0000FF00) >> 8);
				fsinfo_ary[494] = (byte) ((g_fat_index & 0x00FF0000) >> 16);
				fsinfo_ary[495] = (byte) ((g_fat_index & 0xFF000000) >> 24);

				for(int i = 0 ; i < 14 ; i++)
					fsinfo_ary[i + 496] = (byte)0x0;

				fsinfo_ary[510] = (byte)0x55;
				fsinfo_ary[511] = (byte)0xAA;

				buf = ByteBuffer.wrap(fsinfo_ary);
				ImgChannel.position( 0x200 );
				ImgChannel.write(buf);

				//for sector 2
				pImg.seek(0x5FE);
				pImg.writeByte(0x55);
				pImg.writeByte(0xAA);

				//for backup MBR
				buf = ByteBuffer.wrap(tmp_ary);
				ImgChannel.position( 0xC00 );
				ImgChannel.write(buf);	

				//for backup FSInfo
				buf = ByteBuffer.wrap(fsinfo_ary);
				ImgChannel.position( 0xE00 );
				ImgChannel.write(buf);

				//for sector 8
				pImg.seek(0x11FE);
				pImg.writeByte(0x55);
				pImg.writeByte(0xAA);				
			}

		}
		catch(Exception e) {
			e.printStackTrace(); 
		} 
	}

	public void closeChannel() {
		try {
			if( fileChannel != null ) {
				fileChannel.close();
				fileChannel = null;
			}
			if( fileRaf != null ) {
				fileRaf.close();
				fileRaf = null;
			}
			
			if( ImgChannel != null ) {
				ImgChannel.close();
				ImgChannel = null;
			}
			if( pImg != null ) {
				pImg.close();
				pImg = null;
			}
		}
		catch(Exception e) {
			e.printStackTrace(); 
		} 
	}

	/*fill FAT tag into FAT table starter address*/
	public void fillFATtag()
	{
		try {
			if(using_fat16) {
				pImg.seek(fat_start_addr);
				//for FAT start coding => 0xF8FFFFFF
				pImg.writeInt(0xF8FFFFFF);
			
				//for second FAT start coding =>  0xF8FFFFFF
				pImg.seek(fat_start_addr + fat_size_sector * sector_size);
				pImg.writeInt(0xF8FFFFFF);

			}
			else {
				pImg.seek(fat_start_addr);
				pImg.writeInt(0xF8FFFF0F);
				pImg.writeInt(0xFFFFFFFF);
				pImg.writeInt(0xFFFFFF0F);//for FAT 32 root directory
			
				pImg.seek(fat32_fat2_addr);
				pImg.writeInt(0xF8FFFF0F);
				pImg.writeInt(0xFFFFFFFF);
				pImg.writeInt(0xFFFFFF0F);//for FAT 32 root directory
			}
		}
		catch(Exception e) {
			e.printStackTrace(); 
		} 
	}

	/*based on total image size to calculate how many cluster wil using
	and this data is for FAT table cluster index*/
	public long calculateFATTableSize()
	{
		int tmp_sector_size = 512;
		long tmp_cluster_count = 0, real_fat_sector = 0;
		try {
			
			tmp_cluster_count = ( (long)pImg.length() + number_of_fat * FAT_RECORD_SIZE * 2 ) / 
							( sectors_per_cluster * tmp_sector_size + FAT_RECORD_SIZE * 2 );

			real_fat_sector = ( ( tmp_cluster_count + 2 ) * FAT_RECORD_SIZE ) / tmp_sector_size;
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		return real_fat_sector;
	}

	public void getBootSectorInfo(int start_sector)
	{
		try
		{
			pImg.seek(BOOT_SECTOR_SIZE * start_sector);
			for(int i = 0 ; i < 3 ; i++)
				jmp[i] = pImg.readUnsignedByte();

			for(int i = 0 ; i < 8 ; i++) {
				oem[i] = (char)pImg.readUnsignedByte();
			}

			sector_size = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
			sectors_per_cluster = pImg.readUnsignedByte();
			reserved_sector = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
			number_of_fat = pImg.readUnsignedByte();
			root_dir_entry = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
			total_block = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
			media_descriptor = pImg.readUnsignedByte();
			fat_size_sector = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
			sector_per_track = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
			number_of_head = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
			hidden_sector = ChangeToLittleEndian(pImg.readInt(),4, LITTLE_ENDIAN_TYPE);
			total_sector_long = ChangeToLittleEndian(pImg.readInt(),4, LITTLE_ENDIAN_TYPE);

			if( ((total_sector_long * sector_size) / 1000 / 1024 / 1024) <= 2 ) {
				using_fat16 = true;
				FAT_RESERVED_RECORD_NUM = 2;
				FAT_RECORD_SIZE = 2;
				boot_code = new char[448];
			}
			else {
				using_fat16 = false;
				FAT_RESERVED_RECORD_NUM = 3;
				FAT_RECORD_SIZE = 4;
				boot_code = new char[420];
			}
			
			if( !using_fat16 ) {
				fat32_fat_size_sector = ChangeToLittleEndian(pImg.readInt(),4, LITTLE_ENDIAN_TYPE);
				fat32_flag = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
				fat32_version = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
				fat32_root_start_cluster = ChangeToLittleEndian(pImg.readInt(),4, LITTLE_ENDIAN_TYPE);
				fat32_fsinfo_addr = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
				fat32_backup_fsinfo_addr = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
				for(int i = 0 ; i < fat32_reserved.length ; i++)
					fat32_reserved[i] = 0;
			}
			drive_number = pImg.readUnsignedByte();
			current_head = pImg.readUnsignedByte();
			boot_signature = pImg.readUnsignedByte();
			volume_id = ChangeToLittleEndian(pImg.readInt(),4, LITTLE_ENDIAN_TYPE);

			for(int i = 0 ; i < 10 ; i++) {
				volume_label[i] = (char)pImg.readUnsignedByte();
			}
			
			for(int i = 0 ; i < 8 ; i++) {
				fs_type[i] = (char)pImg.readUnsignedByte();
			}
			for(int i = 0 ; i < boot_code.length ; i++) {
				boot_code[i] = (char)pImg.readUnsignedByte();
			}
			
			boot_sector_signature = ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);

			fat_start_addr = BOOT_SECTOR_SIZE * start_sector + reserved_sector * sector_size;
			if(using_fat16) {
				rootStartAddr = fat_start_addr + fat_size_sector * number_of_fat * sector_size;
				data_start_addr = rootStartAddr + root_dir_entry * ROOT_DIR_ENTRY_SIZE;
			}
			else {
				fat32_fsinfo1_addr = 0x200 + 0x1E8;//0x200 is MBR size, 0x1E8 is jump bytes
				fat32_fsinfo2_addr = fat32_fsinfo1_addr + 0xC00;//NOT using
				fat32_fat2_addr = fat_start_addr + fat32_fat_size_sector * sector_size;
				//according FAT32 wiki, it does not had root directory, but normaily will assign 1 cluster as 
				//root directory
				rootStartAddr = fat_start_addr + fat32_fat_size_sector * number_of_fat * sector_size;
				data_start_addr = rootStartAddr + sectors_per_cluster * sector_size;
				fat32_free_cluster = ( total_sector_long * sector_size - rootStartAddr) / (sectors_per_cluster * sector_size);
			}
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
	}

	public void folderSizeCalculate() 
	{
		long find_cluster = 0; 
		long start_addr = fat_start_addr + FAT_RESERVED_RECORD_NUM * FAT_RECORD_SIZE;
		long used_cluster = 0, border = 0;

		if(using_fat16)
			border = ( fat_size_sector * sector_size ) / FAT_RECORD_SIZE;
		else
			border = ( fat32_fat_size_sector * sector_size ) / FAT_RECORD_SIZE;

		try {
			pImg.seek( start_addr );
			do
			{
				if(using_fat16)
					find_cluster = (short)ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
				else
					find_cluster = (int)ChangeToLittleEndian(pImg.readInt(),4, MasterBootRecord.LITTLE_ENDIAN_TYPE);

				if(find_cluster != 0x00) {
					used_cluster++;
				}
				border--;
			} while (border > 0);
			
			folder_size = used_cluster * (sectors_per_cluster * sector_size);
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}

	/*
	input : 
		int input - need to change integer
		int byteNum - how many byte for variable - input
		int endianType - 0, means little endian, 1, means big endian
	*/
	public static int ChangeToLittleEndian(int input, int byteNum,int endianType)
	{
		int result = 0;

		byte[] data = new byte[byteNum];
		ByteBuffer buf = ByteBuffer.wrap(data);
		if(endianType == LITTLE_ENDIAN_TYPE)
			buf.order(ByteOrder.LITTLE_ENDIAN);
		else
			buf.order(ByteOrder.BIG_ENDIAN);

		if(byteNum == 4)
			buf.putInt(input);
		else if(byteNum == 2)
			buf.putShort((short)input);

		for(int i = byteNum - 1 ; i > -1 ; i--) {
			result += (data[i] & 0x000000ff) << (8*(byteNum-1-i));

		}
		return result;
	}

	/*
	using for loop to get previous all Long File Name packages
	until the sequence field & 0x40 equal 0x40
	*/
	public String getLFNName(long rootAddr, int rootIndex, long[] rootAddr_array, int rootAddr_array_index)
	{
		char[] name = new char[0];
		
		for(;;) {
			LongFileName lfn = new LongFileName();

			if(rootAddr_array_index != NON_MULTI_CLUSTER && rootIndex < 0) {
				//multi cluster for root directory, and the rootIndex is the first record of root directory
				//got previous root directory address
				if(rootAddr_array_index != 0) {
					rootIndex = (sectors_per_cluster * sector_size) / ROOT_DIR_ENTRY_SIZE - 1;
					rootAddr = data_start_addr + (long)(rootAddr_array[rootAddr_array_index - 1] - FAT_RESERVED_RECORD_NUM) 
								* (sector_size * sectors_per_cluster);
				}
			}

			char[] tmp_name = lfn.getLFNpackage(pImg, rootAddr +  ROOT_DIR_ENTRY_SIZE * rootIndex, lfn);
			int length = name.length + tmp_name.length;
			char[] tmp1 = new char[name.length];
			
			System.arraycopy(name, 0, tmp1, 0, tmp1.length);
			name = new char[length];
			System.arraycopy(tmp1, 0, name, 0, tmp1.length);
			System.arraycopy(tmp_name, 0, name, tmp1.length, tmp_name.length);
			
			if( (lfn.getSequence() & (byte)0x40) == 0x40) 
				break;
			else
				rootIndex--;
		}

		return String.valueOf(name);
	}

	public String getNameFromImg(RootDirectory rootDir, long rootAddr, int rootIndex,
		long[] rootAddr_array, int rootAddr_array_index)
	{
		boolean lfn_check = false;
		String str_result = null;
		LongFileName lfn = new LongFileName();
		byte checksum = lfn.LfnChecksum(rootDir);

		if(rootAddr_array_index == NON_MULTI_CLUSTER)
			lfn_check = lfn.LFNCheck(pImg, rootAddr +  ROOT_DIR_ENTRY_SIZE * (rootIndex - 1), checksum);
		else {
			//multi cluster for root directory, and the rootIndex is the first record of root directory
			if( (rootIndex - 1) < 0) {
				if(rootAddr_array_index != 0) {
					//if this root directory is the first record
					//get previous root directory address data
					long previous_addr = data_start_addr + (long)(rootAddr_array[rootAddr_array_index - 1] - FAT_RESERVED_RECORD_NUM) 
										* (sector_size * sectors_per_cluster);
					int tmp_index = (sectors_per_cluster * sector_size) / ROOT_DIR_ENTRY_SIZE - 1;
					lfn_check = lfn.LFNCheck(pImg, previous_addr +	ROOT_DIR_ENTRY_SIZE * tmp_index , checksum);	
				}
			}
			else
				lfn_check = lfn.LFNCheck(pImg, rootAddr +  ROOT_DIR_ENTRY_SIZE * (rootIndex - 1), checksum);
		}			

		if(lfn_check) {
			str_result = getLFNName(rootAddr, rootIndex - 1, rootAddr_array, rootAddr_array_index);
		}
		else {
			/*
			SFN 8 + 3 upper / lower rule
			0x10 => upper.lower
			0x08 => lower.upper
			0x18 => lower.lower
			0x00 => upper.upper
			for windows or linux OS, 
			both of they sometime had extension name, 
			sometime they all does not had
			so, do not care about file or folder
			*/
			String filename = null, extname = null;
			char[] tmp = new char[0];
			char[] tmp_ext = new char[0];

			//get filename
			for(int i = 0 ; i < rootDir.filename.length ; i++) {
				if(rootDir.filename[i] != 0x20)//space
					tmp = lfn.extend(tmp,(char)rootDir.filename[i]);
			}
			filename = String.valueOf(tmp);
			//change filename as lower
			if(rootDir.upper == (byte)0x18 || rootDir.upper == (byte)0x08) //name need to change to lower
				filename = filename.toLowerCase();

			//get extension name
			for(int i = 0 ; i < rootDir.ext.length ; i++) {
				if(rootDir.ext[i] != 0x20)//space
					tmp_ext = lfn.extend(tmp_ext, (char)rootDir.ext[i]);
			}
			//change extension name as lower
			extname = String.valueOf(tmp_ext);
			if(rootDir.upper == (byte)0x18 || rootDir.upper == (byte)0x10) //extension name need to chang to lower
				extname = extname.toLowerCase();

			if( extname != null && extname.length() > 0)
				str_result = filename + "." + extname;
			else
				str_result = filename;//for linux, no extension name
		}

		return str_result;
	}

	public int listFilesFromImage(long rootAddr, String path, long[] rootAddr_array, int rootAddr_array_index)
	{	
		int lfnPackNum = 0;
		int rootBorder = 0;
		long[] cluster_ary = new long[0];//FAT cluster index array

		if( using_fat16 ) {
			if(rootAddr == rootStartAddr)
				rootBorder = root_dir_entry;
			else
				rootBorder = (sectors_per_cluster * sector_size) / ROOT_DIR_ENTRY_SIZE;
		}
		else
			rootBorder = (sectors_per_cluster * sector_size) / ROOT_DIR_ENTRY_SIZE;


		for(int i = 0 ; i < rootBorder ; i++){
		 	synchronized(controlSynchronized){
				if (userPausedSync) {
					try{
						i--;
						controlSynchronized.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						Debug.out.println("listFilesFromImage synchronized Error!!");
					}
				} else{
					if(user_stop_flag)//user pressed cancel or X button 
						break;
			
				RootDirectory rootDir = new RootDirectory();
				long addr = 0;
				long create_time = 0, modify_time = 0;
				
				String filename = null;
				boolean modify = false, create = false;
				
				addr = rootAddr + (( ROOT_DIR_ENTRY_SIZE * i ) & UNSIGNED_INT_MASK);
				rootDir.readFileRootDirectory(pImg, addr);

				if(rootDir.filename[0] != 0x00) {
					if( (byte)rootDir.attributes == 0x0F)//parts of Long File Name packages
						continue;
					if ( (byte)(rootDir.filename[0] & UNSIGNED_BYTE_MASK) == (byte)0xE5) {//for delete file or folder
						continue;
					}
					else { 
						//for create or update
						filename = getNameFromImg(rootDir, rootAddr, i, rootAddr_array, rootAddr_array_index);
						create_time = getUTCTimeInMillisecond(rootDir.create_date,rootDir.create_time);
						modify_time = getUTCTimeInMillisecond(rootDir.modify_date,rootDir.modify_time);
						if(filename == null) {
							System.out.println("Got some wired error - listFilesFromImage()");
							return -1;
						}

						if( (rootDir.attributes == FILE_ATTRI_SUBFOLDER) || 
							(rootDir.attributes == (FILE_ATTRI_SUBFOLDER | FILE_ATTRI_HIDDEN) ) || 
							(rootDir.attributes == (FILE_ATTRI_SUBFOLDER | FILE_ATTRI_FILE)) ) { //folder
	
							if(filename.equals(".") || filename.equals("..")) {
								continue;
							}
							
							//check folder exist in the disk
							folderExistCheck(path, filename);

							cluster_ary = mulit_cluster(rootDir);
							if(cluster_ary[0] == 0x0) //means empty folder
								continue;

							for(int j = 0 ; j < cluster_ary.length ; j++) {
								if(cluster_ary.length > 1) {
									//multi cluster folder
									listFilesFromImage(data_start_addr + (cluster_ary[j] - FAT_RESERVED_RECORD_NUM) *
											sectors_per_cluster * sector_size, 
											(path + filename + File.separatorChar) , cluster_ary, j);
								}
								else {
									listFilesFromImage(data_start_addr + (cluster_ary[j] - FAT_RESERVED_RECORD_NUM) * 
											sectors_per_cluster * sector_size, 
											(path + filename + File.separatorChar) , cluster_ary, NON_MULTI_CLUSTER);
								}
							}
						}
						else if( (rootDir.attributes == FILE_ATTRI_FILE) || 
								(rootDir.attributes == FILE_ATTRI_FILE + FILE_ATTRI_HIDDEN) ) { //file

							String abs_path = path + filename;
							File tmp_f = new File(abs_path);
							boolean file_exist = false;
							/*setting file list array flag as true, means this filie had been go through
							   also means this file (save in the image) existed in the disk
							   if TRUE - this file saved in the image existed in the disk, also need to check modify time or create time
							   if FALSE - this filie saved in the image, does not exist in the disk
							*/
							for(int j = 0 ; j < FileListArray.length ; j++) {
								if(abs_path.equals(FileListArray[j].filePath)) {
									FileListArray[j].validateFileList = true;
									break;
								}
							}

							/*if using move operation, the file create time and modify time will not change, 
							and access date also can not using
							so, neet to add one more condition - check file exist
							this condition will put after time check, becuase if time check does not change
							it will also check exist or not*/
							if(!tmp_f.exists())
								file_exist = true;
						
							if( (imageCreateTime < modify_time) || (imageCreateTime < create_time) ) {					
								writeFileIntoDisk(path, filename, rootDir);
							}
							else if(file_exist){
								writeFileIntoDisk(path, filename, rootDir);
							}

							if(user_stop_flag)//user pressed cancel or X button 
								break;
						
							copyed_size += rootDir.file_size;

							if( ((int) (copyed_size * 100.0 / folder_size)) <= 95 )
								complete_percent = (int) (copyed_size * 100.0 / folder_size);
							else
								complete_percent = 95;//because of user may add file into image
													//and that will make file size in the image
													//greater than original 
													//so, here's bigest percent is 92
													//for check delete option
							}
						}
					}
				}
			}
		}
		return 0;
	}

	/*for delete file / foler
	if there is file or folder does not checked
	that means user delete it from image
	so, it's need to delete it from disks*/
	public void delFileCheck()
	{
		complete_percent = 98;//simulate complete percent
			
		for(int i = 0 ; i < FileListArray.length ; i++) {
	  		synchronized(controlSynchronized){
				if (userPausedSync) {
					try{
						i--;
						controlSynchronized.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						Debug.out.println("delFileCheck synchronized Error!!");
					}
				} else{
					if(FileListArray[i].validateFileList == false) {
						File tmp = new File(FileListArray[i].filePath);
						deleteAll(tmp);
					}
				}
				complete_percent = 100;//simulate complete percent
	 		}
	 	}
	}

	public void deleteAll(File path) 
	{
		if (!path.exists())
			return;
		
		if (path.isFile()) {
			path.delete();
			return;
		}
		
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			deleteAll(files[i]);
		}
		path.delete();
	}

	/*This function is using for list all of the file / folder and save absolute path into array
	   And the goal is for delete file, that means if some file does not go through
	   means user had been delete it.
	*/
	public FileList[] listFolder(File folder, FileList[] tmp_array)
	{
		if(folder == null)
			return tmp_array;

		for ( File fileEntry : folder.listFiles()) {
			FileList tmp = new FileList();
			tmp.fileName = fileEntry.getName();
			tmp.filePath = fileEntry.getPath();
				
			if (fileEntry.isDirectory()) {
				tmp_array = extendFileList(tmp_array, tmp);
				tmp_array = listFolder(fileEntry, tmp_array);
			}
			else {
				tmp_array = extendFileList(tmp_array, tmp);
			}
		}
		return tmp_array;

	}

	/*add new FileList object into FileList array
	this is using for synchronize file into disk
	before synchronize file into disk, we need to create FileList arry for check delete*/
	public FileList[] extendFileList(FileList[] array, FileList addr)
	{
		int length = array.length;
		FileList[] newarray = new FileList[length + 1];
		System.arraycopy(array, 0, newarray, 0, length);
		newarray[length] = addr;

		return newarray;
	}

	public void getFileList(String path) 
	{
		FileListArray = new FileList[0];
		FileListCreatTime = System.currentTimeMillis();
		File file_tmp = new File(path);
		
		FileListArray = listFolder(file_tmp, FileListArray);

		try
		{
			Thread.sleep(1000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();	
		}
	}

	/* Check this folder is used multi cluster to save file 
	  and check method is go to FAT table, get this folder fat data, 
	  if this folder fat data is FAT end of character, ex : FAT 16 is 0xFFFF, FAT 32 is 0x0FFFFFFF
	  it's means does not using multi cluster
	  if not FAT end of character, that means using multi cluster to save file
	  check rule as follw : 
	  	1. based on root directory start cluster, calculate data addr
		2. based on data addr, calculate fat index in FAT table
		3. go to FAT table, read next fat index data
	  		a. if fat index data  == 0x0FFFFFFF, means no next cluster
	  		b. if fat index data !=0x0FFFFFFF, go to step 4
		4. based on fat index data, calculate data addr and then go to step 2
	  
	*/
	public long[] mulit_cluster(RootDirectory rootDir)
	{
		long tmp_cluster_index= 0;// folder start cluster 
		long fat_index = 0;
		long[] result = new long[1];//FAT cluster index array
		long tmp_data_addr = 0;

		//1. based on root directory start cluster, 
		if(using_fat16) {
			result[0] = rootDir.start_cluster;
			tmp_cluster_index = rootDir.start_cluster;
		}
		else {
			tmp_cluster_index =  ( rootDir.start_cluster | (rootDir.fat32_reserved << 16) );
			result[0] = tmp_cluster_index;
		}

		if(tmp_cluster_index == 0)//empty folder, does not need to handle
			return result;

		try {
			
			for(;;) {
				//calculate data addr
				tmp_data_addr = data_start_addr + (tmp_cluster_index - FAT_RESERVED_RECORD_NUM) * (sector_size * sectors_per_cluster);
				//2. based on data addr, calculate fat index in FAT table
				fat_index = (tmp_data_addr - data_start_addr) / (sector_size * sectors_per_cluster) + FAT_RESERVED_RECORD_NUM;

				/*		
				3. go to FAT table, read next fat index data
			  		a. if fat index data  == 0x0FFFFFFF, means no next cluster
	  				b. if fat index data !=0x0FFFFFFF
	  			*/
				pImg.seek(fat_start_addr + fat_index * FAT_RECORD_SIZE);			
				if( using_fat16 ) {
					tmp_cluster_index = (long)ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);			
					if(tmp_cluster_index != 0xFFFF)//FAT 16, end of character
						result = extendLong(result, tmp_cluster_index);
					else
						break;
				}
				else {
					tmp_cluster_index = (long)ChangeToLittleEndian(pImg.readInt(),4, MasterBootRecord.LITTLE_ENDIAN_TYPE);			
					if(tmp_cluster_index != 0x0FFFFFFF)//FAT 32, end of character
						result = extendLong(result, tmp_cluster_index);
					else
						break;
				}
			}
		}	
		catch(IOException e) {
			e.printStackTrace();	

		}

		return result;
	}

	/*add new Long into Long array*/
	public long[] extendLong(long array[], long addr)
	{
		int length = array.length;
		long[] newarray = new long[length + 1];
		System.arraycopy(array, 0, newarray, 0, length);
		newarray[length] = addr;

		return newarray;
	}

	public void folderExistCheck(String path, String foldername)
	{
		String abs_path = new String(path + foldername);
		File path_f = new File(abs_path);

		if(!path_f.exists()) {
			path_f.mkdirs();
		}		
		else {
			for(int i = 0 ; i < FileListArray.length ; i++) {
				if(abs_path.equals(FileListArray[i].filePath)) {
					FileListArray[i].validateFileList = true;
					break;
				}
			}
		}		
	}

	private void writeFileIntoDisk(String path, String filename, RootDirectory rd)
	{
		String abs_filename = new String(path + filename);

		if(ImgChannel == null)
			ImgChannel = pImg.getChannel();

		long bytes_read = 0;//this time to read bytes
		long byte_to_read = sector_size * sectors_per_cluster;//every time need to read
		long file_left = rd.file_size; //how many bytes still not read
		long cluster_size = sector_size * sectors_per_cluster; 
		long cluster_left = cluster_size;//this cluster still not to read
		long cluster_addr = 0; 
		long pos = 0;
		long fat_eoc = 0;

		//first, delete old file.
		try {
			File tmp_file = new File(abs_filename);
			if(tmp_file.exists()) {
				if(!tmp_file.delete()){
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if( using_fat16 ) { 
			cluster_addr = rd.start_cluster;
			fat_eoc = 0xFFFF;
		}
		else {
			cluster_addr = ( rd.start_cluster | (rd.fat32_reserved << 16) );
			fat_eoc = 0x0FFFFFFF;
		}
		
		try{
			fileRaf =  new RandomAccessFile(abs_filename, "rw");
			fileChannel = fileRaf.getChannel();
			pos = data_start_addr + cluster_size * (cluster_addr - FAT_RESERVED_RECORD_NUM);
			ImgChannel.position( pos );
			while( (file_left > 0) & (cluster_addr != fat_eoc) ) {
				
				if(user_stop_flag)//user pressed cancel or X button 
					break;
				
				byte_to_read = sector_size * sectors_per_cluster;
				
				if(byte_to_read > file_left)
					byte_to_read = file_left;
				if(byte_to_read > cluster_left)
					byte_to_read = cluster_left;

				ByteBuffer buf = ByteBuffer.allocate((int)byte_to_read);
				buf.clear();//memset buffer
				bytes_read = ImgChannel.read(buf);
				buf.flip();//setting buffer pointer position as 0
				fileChannel.write(buf);

				cluster_left -= bytes_read;
				file_left -= bytes_read;
				if(cluster_left == 0) {
					// if we have read the whole cluster, read next cluster # from FAT
					ImgChannel.position( fat_start_addr + cluster_addr * FAT_RECORD_SIZE);
					if( using_fat16 )
						cluster_addr = (long)ChangeToLittleEndian(pImg.readUnsignedShort(),2, LITTLE_ENDIAN_TYPE);
					else
						cluster_addr = (long)ChangeToLittleEndian(pImg.readInt(),4, LITTLE_ENDIAN_TYPE);

					ImgChannel.position( data_start_addr + cluster_size * (cluster_addr - FAT_RESERVED_RECORD_NUM) );
					cluster_left = cluster_size;
				}
				
				if(user_stop_flag)//user pressed cancel or X button 
					break;

			}
			fileChannel.force(true);
			
			fileChannel.close();
			fileChannel = null;
			fileRaf.close();
			fileRaf = null;
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
	}

	/*go to FAT table, finding free cluster index - data area index
	if finded, fill 0xFFFF(FAT 16) / 0xFFFFFFF0(FAT 32)
	and then return cluster index */
	public long find_free_cluster_from_fat()
	{
		long cluster_index = g_fat_index; 
		long find_cluster = 0; 
		byte[] cluster_fake_content;
		long tmp_addr = 0;

		if( using_fat16) {
			cluster_fake_content = new byte[2];
			cluster_fake_content[0] = (byte)0xFF;
			cluster_fake_content[1] = (byte)0xFF;
		}
		else {
			cluster_fake_content = new byte[4];
			cluster_fake_content[0] = (byte)0xFF;
			cluster_fake_content[1] = (byte)0xFF;
			cluster_fake_content[2] = (byte)0xFF;
			cluster_fake_content[3] = (byte)0x0F;
		}

		try {
			do
			{
				tmp_addr = fat_start_addr + cluster_index * FAT_RECORD_SIZE;
				pImg.seek(tmp_addr);

				if(using_fat16)
					find_cluster = (short)ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
				else
					find_cluster = (int)ChangeToLittleEndian(pImg.readInt(),4, MasterBootRecord.LITTLE_ENDIAN_TYPE);

				if(find_cluster == 0x00) {
					pImg.seek(tmp_addr);
					pImg.write(cluster_fake_content);
					
					if( !using_fat16) {
						pImg.seek(fat32_fat2_addr + cluster_index * FAT_RECORD_SIZE);
						pImg.write(cluster_fake_content);
					}
					g_fat_index = cluster_index;
					break;
				}
				else {
					cluster_index++;
				}
			} while (find_cluster != 0x00);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return cluster_index;
	}

	/*fill file / folder date and time*/
	void fillDateTime(String filepath, RootDirectory rootDir) 
	{
		Path pfile = Paths.get(filepath);
		try{
			BasicFileAttributes ra = Files.readAttributes(pfile, BasicFileAttributes.class);
			//fill last access time
			setDateTime(rootDir, ra.lastAccessTime().toMillis(), 2);
			//fill last modify time
			setDateTime(rootDir, ra.lastModifiedTime().toMillis(), 1);
			//fill create time
			setDateTime(rootDir, ra.creationTime().toMillis(), 0);	
			
		}catch(IOException e)
		{
			e.printStackTrace();	
		}
	}

	public RandomAccessFile getImageFilePointer() {
		return pImg;
	}
	
	public int createImage(String name)
	{
		int abcd = 0;
		File folder = new File(name);

		return scanFolder(folder, rootStartAddr, 0);

	}

	public void setStopFlag() {
		user_stop_flag = true;
	}
	public void setPauseFlag() {
		userPausedSync = true;
	}
	public void setResumeFlag() {
		synchronized(controlSynchronized) {
			userPausedSync = false;
			controlSynchronized.notifyAll();
		}
	}

	public int scanFolder( File folder, long rootAddr, long previous_rootAddr )
	{
		int result = 0;
		
		if(folder == null) {
			//prevent null pointer exception error
			return -1;
		}

		//need add "." + ".." root directory into it
		//if does not add both it, will generate wired error
		if(rootAddr != rootStartAddr) {
			RootDirectory dotDir = new RootDirectory();
			long old_cluster_index = 0;
			dotDir.filename[0] = 0x2E;
			dotDir.attributes = 0x10;
			dotDir.upper = 0x0;
			//fill file last access time, modify time and create time
			setDateTime(dotDir, imageCreateTime, 2);
			setDateTime(dotDir, imageCreateTime, 1);
			setDateTime(dotDir, imageCreateTime, 0);
			old_cluster_index = (rootAddr - data_start_addr) / (sector_size * sectors_per_cluster) + FAT_RESERVED_RECORD_NUM;
			dotDir.start_cluster = old_cluster_index & 0x0000FFFF;
			dotDir.fat32_reserved = (old_cluster_index & 0xFFFF0000) >> 16;
			dotDir.file_size = 0x0;
			fillRootDirectory(dotDir, null, rootAddr);

			RootDirectory doubledotDir = new RootDirectory();
			doubledotDir.filename[0] = 0x2E;
			doubledotDir.filename[1] = 0x2E;
			doubledotDir.attributes = 0x10;
			doubledotDir.upper = 0x0;
			//fill file last access time, modify time and create time
			setDateTime(doubledotDir, imageCreateTime, 2);
			setDateTime(doubledotDir, imageCreateTime, 1);
			setDateTime(doubledotDir, imageCreateTime, 0);
			if(previous_rootAddr != rootStartAddr)
				old_cluster_index = (previous_rootAddr - data_start_addr) / (sector_size * sectors_per_cluster) + FAT_RESERVED_RECORD_NUM;
			else
				old_cluster_index = 0;
			doubledotDir.start_cluster = old_cluster_index & 0x0000FFFF;
			doubledotDir.fat32_reserved = (old_cluster_index & 0xFFFF0000) >> 16;
	
			dotDir.file_size = 0x0;
			fillRootDirectory(doubledotDir, null, rootAddr);
		}


		for ( File fileEntry : folder.listFiles()) {
			if(user_stop_flag)//user pressed cancel or X button 
				break;
			
			//using to check symbolic link
			//because of windows and linux using different way to handle it
			//so we will not support and just return
			Path symbol_link = Paths.get(fileEntry.getPath());
			if(Files.isSymbolicLink(symbol_link)) {
				continue;
			}

			RootDirectory rootDir = new RootDirectory();
			//split filename and extension file based on . - pointer
			String[] nameArray = ( (fileEntry.getName()).toUpperCase() ).split("\\.");
			//fill root directory time and date
			long fileModifyTime = fileEntry.lastModified();
			boolean sfn_check = false;
			long addr = 0, start_cluster_addr = 0;

			//check file for LongFileName
			sfn_check = SFNCheck(fileEntry, rootDir);
			if(!sfn_check) {
				int pack_num = 0, byteNum = 0;
				LongFileName lfn = new LongFileName();
				byte checksum = 0;
				byte[] fileNameArray = new byte[0];

				//if using Long File Name, the root directory some field wiil fill fixed value, like filename, upper
				rootDir.upper = 0x0;
				byteNum = lfn.getLNFByteNum(fileEntry.getName());
				pack_num = lfn.getLfnPackNum();
				rootDir.filename[0] = 0x5F;
				rootDir.filename[1] = 0x7E;
				rootDir.filename[2] = lfn_sequence++;
				checksum = lfn.LfnChecksum(rootDir);
				fileNameArray = lfn.getByteInFilename();
				addr = fillLFNRootDirectory(fileNameArray , pack_num, byteNum, checksum, rootAddr);
				if(addr < 0) {
					result = (int)addr;
					return result;
				}
				else if(addr > 0)
					rootAddr = addr;
			}
			else {
				//fill root directory filename, extension, and attribute
				if (fileEntry.isDirectory()) {
					//because of the above SFN check function, we decide "if folder using pointer - ., it is using Long File Name package"
					//so, here just fill folder name into rootDir.filename 
					byte[] name = setFileNameAndExt( (fileEntry.getName()).toUpperCase() );
					for(int i = 0 ; i < name.length ; i++)
						rootDir.filename[i] = name[i];
				} else {
					if(nameArray.length == 1) {
						//no extension name
						byte[] name = setFileNameAndExt(nameArray[0]);
						for(int i = 0 ; i < name.length ; i++)
							rootDir.filename[i] = name[i];						
					}
					else if(nameArray.length == 2) {
						byte[] name = setFileNameAndExt(nameArray[0]);
						for(int i = 0 ; i < name.length ; i++)
							rootDir.filename[i] = name[i];
						byte[] ext = setFileNameAndExt(nameArray[1]);
						for(int i = 0 ; i < ext.length ; i++)
							rootDir.ext[i] = ext[i];
					}
				}
			}
			//fill file or folder data / time
			fillDateTime(fileEntry.getPath(), rootDir);		

			//fill file / folder start cluster index
			start_cluster_addr = (long)( find_free_cluster_from_fat() & UNSIGNED_INT_MASK);
			if( using_fat16 )
				rootDir.start_cluster = start_cluster_addr;
			else {
				rootDir.start_cluster = start_cluster_addr & 0x0000FFFF;
				rootDir.fat32_reserved = (start_cluster_addr & 0xFFFF0000) >> 16;
			}
				
			if (fileEntry.isDirectory()) {
				rootDir.attributes = FILE_ATTRI_SUBFOLDER;
			}
			else {
				rootDir.attributes = FILE_ATTRI_FILE;
				rootDir.file_size = (int)fileEntry.length();
			}

			addr = fillRootDirectory(rootDir, null, rootAddr);
			if(addr < 0) {
				result = (int)addr;
				return result;
			}
			else if(addr > 0)
				rootAddr = addr;

			if (fileEntry.isDirectory()) {
				result = scanFolder(fileEntry, data_start_addr + (start_cluster_addr - FAT_RESERVED_RECORD_NUM) * (sector_size * sectors_per_cluster), rootAddr );
				if(result < 0)
					return result; 
			}
			else {
				writeFileIntoImg(rootDir, fileEntry);
				
				if(user_stop_flag)//user pressed cancel or X button 
					break;
				
				copyed_size += fileEntry.length();
				complete_percent = (int) (copyed_size * 100.0 / folder_size);
			}	
		}
		return result;
	}

	public void writeFileIntoImg(RootDirectory rootDir, File filename)
	{
		long byte_to_read = sector_size * sectors_per_cluster; //every time need to read
		long file_size = rootDir.file_size; //how many bytes still not read
		long file_left = file_size;	//how many file size still not write
		long cluster_size = byte_to_read;	//cluster max size
		long cluster_left = cluster_size;//this cluster still not to read
 		long cluster_addr = 0, start_cluster_addr = 0, cluster_tmp = 0;
		long next_addr = 0;
 		long init_addr = 0;
		long read_byte = 0;

		if( using_fat16 ) {
			cluster_addr = start_cluster_addr = rootDir.start_cluster;
		}
		else {
			cluster_addr = start_cluster_addr = ((rootDir.fat32_reserved << 16) | rootDir.start_cluster);
		}

		try{
			fileRaf =  new RandomAccessFile(filename.getPath(), "rw");
			fileChannel = fileRaf.getChannel();
			//based on cluster_addr to find data area, and then read file writing into image
			init_addr= data_start_addr + cluster_size * (cluster_addr - FAT_RESERVED_RECORD_NUM);
			//go to first data cluster for write file content into image
			ImgChannel.position( init_addr );
			next_addr = init_addr;

			while( (file_left > 0) ) {
				if(user_stop_flag)//user pressed cancel or X button 
					break;
				
				byte_to_read = sector_size * sectors_per_cluster;

				if(byte_to_read > file_left)
					byte_to_read = file_left;
				if(byte_to_read > cluster_left)
					byte_to_read = cluster_left;
				read_byte = 0;
				read_byte = ImgChannel.transferFrom(fileChannel, next_addr, byte_to_read);
				
				file_left -= read_byte;
				cluster_left -= read_byte;
				
				if(cluster_left == 0) {
					//because of the data area is full, need to find next data area.
					if( using_fat16 )
						cluster_tmp = ( find_free_cluster_from_fat() & UNSIGNED_SHORT_MASK );
					else
						cluster_tmp = ( find_free_cluster_from_fat() & UNSIGNED_INT_MASK );

					//for FAT table, update previous cluster value as next cluster
					pImg.seek(fat_start_addr + cluster_addr * FAT_RECORD_SIZE);
					if( using_fat16 )
						pImg.writeShort(ChangeToLittleEndian((int)cluster_tmp,FAT_RECORD_SIZE, MasterBootRecord.LITTLE_ENDIAN_TYPE));
					else {
						//update FAT 1 table
						pImg.writeInt(ChangeToLittleEndian((int)cluster_tmp,FAT_RECORD_SIZE, MasterBootRecord.LITTLE_ENDIAN_TYPE));

						//update FAT 2 tabel
						pImg.seek(fat32_fat2_addr + cluster_addr * FAT_RECORD_SIZE);
						pImg.writeInt(ChangeToLittleEndian((int)cluster_tmp,FAT_RECORD_SIZE, MasterBootRecord.LITTLE_ENDIAN_TYPE));

						//for FAT 32, update FSInfo
						fat32_free_cluster -= 1;
						pImg.seek(fat32_fsinfo1_addr);
						pImg.writeInt(ChangeToLittleEndian((int)fat32_free_cluster, 4, LITTLE_ENDIAN_TYPE));
						// + 1 means next free cluster
						pImg.writeInt(ChangeToLittleEndian((int)(g_fat_index + 1), 4, LITTLE_ENDIAN_TYPE));
					}
					//go to next free data area
					cluster_addr = cluster_tmp;
					next_addr = data_start_addr + cluster_size * (cluster_addr - FAT_RESERVED_RECORD_NUM);

					cluster_left = cluster_size;
				}
				
				if(user_stop_flag)//user pressed cancel or X button 
					break;
			}


			ImgChannel.force(true);
				
			fileChannel.close();
			fileChannel = null;
			fileRaf.close();
			fileRaf = null;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/*add new byte item into byte array*/
	public byte[] expandByteArray(byte[] a, int size) {
		if(size <= a.length) {
			return a;
		}

		byte[] t = new byte[size];
		System.arraycopy(a, 0, t, 0, a.length);
		return t;
	}

	/*transfer long to byte array*/
	public byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(0, x);
		return buffer.array();  
	}

	public long fillLFNRootDirectory(byte[] byteInFilename, int pack_num , int byteNum, byte checksum, long rootAddr )
	{
		int str_border = 0;
		str_border = byteInFilename.length;
		long addr = 0;
		byte sequence = 0;
		byte[] character = new byte[str_border+LFN_CHAR_LEN_IN_BYTE];
		
		for(int i = pack_num ; i > 0 ; i--) {
			LongFileName lfn = new LongFileName();
			lfn.setSequence(intToByte(i));
			lfn.setChecksum(checksum);
			
			int k = 0;
			for(int j = ( i - 1) * LFN_CHAR_LEN_IN_BYTE ; j < str_border ; j++, k++) {
					character[k] = byteInFilename[j];
					lfn.setLfnCharacter(character);
			}
			
			if(i == pack_num) {//The last len package sequence need to do it.
				sequence = lfn.getSequence();
				sequence |= 0x40;
				lfn.setSequence(sequence); 
				for( k += 2 ; k < LFN_CHAR_LEN_IN_BYTE ; k++) {
					//k += 2 means the last character need to using 0x0000 as ending character
					character[k] = (byte)0xFF;
					lfn.setLfnCharacter(character);
				}
			}
			//estimate next string border
			str_border = (i - 1) * LFN_CHAR_LEN_IN_BYTE;
			addr = fillRootDirectory(null, lfn, rootAddr);

			if(addr < 0) //got some error, just return
				return addr;
			else if(addr > 0) 
				rootAddr = addr;
		}
		return rootAddr;
	}

	public long fillRootDirectory(RootDirectory rootDir ,LongFileName lfn, long rootAddr)
	{
		long addr = 0;
		try {

			int rootIndex = 0;
			int rootBorder = 0;
			pImg.seek(rootAddr);
			byte[] tmp_ary = new byte[ROOT_DIR_ENTRY_SIZE];
			byte[] charArray = new byte[26];

			//find root directory border
			if( using_fat16 ) {
				if(rootAddr == rootStartAddr)
					rootBorder = root_dir_entry;
				else
					rootBorder = (sectors_per_cluster * sector_size) / ROOT_DIR_ENTRY_SIZE;
			}
			else
				rootBorder = (sectors_per_cluster * sector_size) / ROOT_DIR_ENTRY_SIZE;

			//find free root directory to save it.
			for(rootIndex = 0 ; rootIndex < rootBorder ; rootIndex++) {
				byte first_B = (byte)pImg.readUnsignedByte();
				//because of read 1 byte and find free root directory, so come back 1 byte
				pImg.seek( pImg.getFilePointer() - 1 );
				if(first_B == 0x00 || first_B == 0xe5) {
					break;
				}
				else
					pImg.seek( pImg.getFilePointer() + ROOT_DIR_ENTRY_SIZE );
			}

			if(rootIndex == rootBorder) {
				//root directory with multi cluster
				//if folder using multi cluster saving file list
				//the folder's FAT will change to next cluster index data
				if( using_fat16) {
					if(rootAddr == rootStartAddr) {
						System.out.println("root dir entry can not save too much file with FAT16, Please using FAT32");
						return -2;
					}
				}
				long cluster_index = 0, old_cluster_index = 0;
				cluster_index = (long)( find_free_cluster_from_fat() & UNSIGNED_INT_MASK);
				
				//using data addr to got old cluster index in FAT table, and then update it in FAT table
				old_cluster_index = (rootAddr - data_start_addr) / (sector_size * sectors_per_cluster) + FAT_RESERVED_RECORD_NUM;
				pImg.seek(fat_start_addr + old_cluster_index * FAT_RECORD_SIZE);
				
				if( using_fat16 )
					pImg.writeShort(ChangeToLittleEndian((int)cluster_index,2, MasterBootRecord.LITTLE_ENDIAN_TYPE));
				else {
					pImg.writeInt(ChangeToLittleEndian((int)cluster_index,4, MasterBootRecord.LITTLE_ENDIAN_TYPE));
					//for FAT 32, update FAT 2 table data
					pImg.seek(fat32_fat2_addr + old_cluster_index * FAT_RECORD_SIZE);
					pImg.writeInt(ChangeToLittleEndian((int)cluster_index,4, MasterBootRecord.LITTLE_ENDIAN_TYPE));

					//update FSInfo table
					fat32_free_cluster -= 1;
					pImg.seek(fat32_fsinfo1_addr);
					pImg.writeInt(ChangeToLittleEndian((int)fat32_free_cluster, 4, LITTLE_ENDIAN_TYPE));
					// + 1 means next free cluster
					pImg.writeInt(ChangeToLittleEndian((int)(g_fat_index + 1), 4, LITTLE_ENDIAN_TYPE));
				}

				//when find next free data area, go to there
				addr = data_start_addr + (cluster_index - FAT_RESERVED_RECORD_NUM) * (sector_size * sectors_per_cluster);
				pImg.seek(addr);

			}

			//write Long File Name or root directory into image
			if(lfn == null) {
				for(int i = 0 ; i < rootDir.filename.length ; i++)
					tmp_ary[i] = rootDir.filename[i];
				for(int i = 0 ; i < rootDir.ext.length ; i++)
					tmp_ary[i + 8] = rootDir.ext[i];
				tmp_ary[11] = rootDir.attributes;
				tmp_ary[12] = rootDir.upper;
				tmp_ary[13] = rootDir.create_millsec;
				tmp_ary[14] = (byte) (rootDir.create_time & 0x00FF);
				tmp_ary[15] = (byte) ((rootDir.create_time & 0xFF00) >> 8);
				tmp_ary[16] = (byte) (rootDir.create_date & 0x00FF);
				tmp_ary[17] = (byte) ((rootDir.create_date & 0xFF00) >> 8);
				tmp_ary[18] = (byte) (rootDir.access_date & 0x00FF);
				tmp_ary[19] = (byte) ((rootDir.access_date & 0xFF00) >> 8);
				tmp_ary[20] = (byte) (rootDir.fat32_reserved & 0x00FF);
				tmp_ary[21] = (byte) ((rootDir.fat32_reserved & 0xFF00) >> 8);
				tmp_ary[22] = (byte) (rootDir.modify_time & 0x00FF);
				tmp_ary[23] = (byte) ((rootDir.modify_time & 0xFF00) >> 8);
				tmp_ary[24] = (byte) (rootDir.modify_date & 0x00FF);
				tmp_ary[25] = (byte) ((rootDir.modify_date & 0xFF00) >> 8);
				tmp_ary[26] = (byte) (rootDir.start_cluster & 0x00FF);
				tmp_ary[27] = (byte) ((rootDir.start_cluster & 0xFF00) >> 8);
				tmp_ary[28] = (byte) (rootDir.file_size & 0x000000FF);
				tmp_ary[29] = (byte) ((rootDir.file_size & 0x0000FF00) >> 8);
				tmp_ary[30] = (byte) ((rootDir.file_size & 0x00FF0000) >> 16);
				tmp_ary[31] = (byte) ((rootDir.file_size & 0xFF000000) >> 24);
			}
			else {
			    charArray = lfn.getLfnCharacter();	
				tmp_ary[0] = lfn.getSequence();
				for(int i = 0 ; i < lfNameSec1 ; i++)
					tmp_ary[i + 1] = charArray[i];
				tmp_ary[11] = lfn.getAttribute();
				tmp_ary[12] = lfn.getType();
				tmp_ary[13] = lfn.getChecksum();
				for(int i = 0 ; i < lfNameSec2 ; i++)
					tmp_ary[i + 14] = charArray[i + 10];
				tmp_ary[26] = (short)0x00;
				tmp_ary[27] = (short)0x00;
				for(int i = 0 ; i < lfNameSec3 ; i++)
					tmp_ary[i + 28] = charArray[i + 22];
			}

			ByteBuffer buf = ByteBuffer.allocate( 32 );
			buf.clear();//memset buffer
			buf.put( tmp_ary );
			buf.flip();

			while(buf.hasRemaining()) {
				ImgChannel.write(buf);
			}
			ImgChannel.force(true);

		}
		catch(EOFException e1)
		{
			e1.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return addr;
	}

	/*
	for file Long File Name check condition
		1. using multi pointer - "."
		2. filename > 8
		3. extension file name > 3
		4. both upper and lower 
		5. non ascii character
		6. special symbols, except '-', '_'
		
	for folder Long File Name check condition	
		1. filename > 8
		2. non ascii character
		3. special symbols, include '.'
		
		ps : normally, folder name will not include pointer - .
		but actually, it will support it and also can using SFN to packet it.
		in this code flow, we will uisng Long File Name to packet it.
	*/
	public boolean SFNCheck(File fileEntry, RootDirectory rootDir)
	{
		String str = fileEntry.getName();
		String[] nameArray = str.split("\\.");
		Pattern pattern_lower = Pattern.compile("[_a-z0-9-[.]]*"); //check  all lower with dot
		Pattern pattern_upper = Pattern.compile("[_A-Z0-9-[.]]*");// check all upper with dot
		Pattern pattern_name_lower = Pattern.compile("[_a-z0-9-]*");
		Pattern pattern_name_upper = Pattern.compile("[_A-Z0-9-]*");
		Pattern pattern_ext_lower = Pattern.compile("[_a-z0-9-]*");
		Pattern pattern_ext_upper = Pattern.compile("[_A-Z0-9-]*");

		Matcher matcher_name_lower, matcher_name_upper;
		Matcher matcher_ext_lower, matcher_ext_upper;
		if ( !fileEntry.isDirectory()) {
			if(nameArray.length > 2) {
				return false;
			}

			//for linux file or directory, means does not has extension
 			if(nameArray.length == 1) {
 				if(nameArray[0].length() > 8) {
					return false;
				}
				matcher_name_lower = pattern_name_lower.matcher(nameArray[0]);
				matcher_name_upper = pattern_name_upper.matcher(nameArray[0]);
				if(matcher_name_lower.matches())
					rootDir.upper = 0x18;
				else if(matcher_name_upper.matches()) {
					rootDir.upper = 0x0;
				}
				else 
					return false;
			}
			else if(nameArray.length == 2) {
				if(nameArray[0].length() > 8 || nameArray[1].length() > 3) {
 					return false;
 				}
			
				matcher_name_lower = pattern_name_lower.matcher(nameArray[0]);
				matcher_name_upper = pattern_name_upper.matcher(nameArray[0]);
				matcher_ext_lower = pattern_ext_lower.matcher(nameArray[1]);
				matcher_ext_upper = pattern_ext_upper.matcher(nameArray[1]);
				/*
					0x10 => upper.lower
					0x08 => lower.upper
					0x18 => lower.lower
					0x00 => upper.upper
				*/
				if(matcher_name_lower.matches() && matcher_ext_lower.matches()) {
					rootDir.upper = 0x18;
				}
				else if (matcher_name_upper.matches() && matcher_ext_upper.matches()) {
					rootDir.upper = 0x0;
				}
				else if (matcher_name_upper.matches() && matcher_ext_lower.matches()) {
					rootDir.upper = 0x10;
				}
				else if(matcher_name_lower.matches() && matcher_ext_upper.matches()) {
					rootDir.upper = 0x08;
				}
				else {
					return false;
				}
 			}
		}
		else {
			if(str.indexOf('.') > 0) {
				return false;
			}
			if(str.length() > 8) {
				return false;
			}

			matcher_name_lower = pattern_name_lower.matcher(str);
			matcher_name_upper = pattern_name_upper.matcher(str);
 			if( matcher_name_lower.matches() ) {
				rootDir.upper = 0x18;
 			}
			else if( matcher_name_upper.matches() ) {
				rootDir.upper = 0x0;
			}
			else {
				return false;
			}
		}
		return true;
	}

	/*transfer string into utf8 byte array*/
	public byte[] setFileNameAndExt(String name)
	{
		char[] c = name.toCharArray();
		ByteBuffer bb = Charset.forName("UTF-8").encode(CharBuffer.wrap(c));
		byte[] b = new byte[bb.remaining()];
		bb.get(b);

		return b;
	}

	/*
	input : 
		File fileEntry - file entry pointer
		int timeType - 0, create time and date
					   1, modify time and date
					   2, access date
	*/
	public void setDateTime(RootDirectory rootDir, long FileTime, int timeType )
	{
		int y,M,d,h,m,s;

		short sDate = 0, sTime = 0;
		//SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		SimpleDateFormat year = new SimpleDateFormat("yyyy");
		SimpleDateFormat month = new SimpleDateFormat("MM");
		SimpleDateFormat day = new SimpleDateFormat("dd");
		SimpleDateFormat hour = new SimpleDateFormat("HH");
		SimpleDateFormat min = new SimpleDateFormat("mm");
		SimpleDateFormat sec = new SimpleDateFormat("ss");

		y = ( Integer.valueOf( year.format(FileTime)) - 1980 );
		M = Integer.valueOf( month.format(FileTime));
		d = Integer.valueOf( day.format(FileTime));
		h = Integer.valueOf( hour.format(FileTime));
		m = Integer.valueOf( min.format(FileTime));
		s = Integer.valueOf( sec.format(FileTime));

		sDate |= intToByte(y) << 9;
		sDate |= intToByte(M) << 5;
		sDate |= intToByte(d);

		sTime |= intToByte(h) << 11;
		sTime |= intToByte(m) << 5;
		sTime |= intToByte(s) >> 1;
		
		if(timeType == 1) {
			rootDir.modify_date = sDate;
			rootDir.modify_time = sTime;
		}
		else if(timeType == 0){
			rootDir.create_date = sDate;
			rootDir.create_time = sTime;
			rootDir.create_millsec = 0;			
		} else {
			rootDir.access_date = sDate;
			}
		
	}

	public long getUTCTimeInMillisecond(long date, long time)
	{
		long y,M,d,h,m,s;
		long milliseconds = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		y =  ((date >> 9) + 1980) ;
		M = ((date >> 5 & 0xF));
		d =  (date & 0x1F);
		h =  (time >> 11) & 0x1F;
		m =  (time >> 5 & 0x3F);
		s =  ((time & 0x1F) * 2);

		/* remove milli-seconds, because of using file class - lastmodified()
		 will not got milli second, and the result sometime will reduce 1000, because of FAT time 
		 indicates the binary number of two-second periods (0-29), representing seconds 0 to 58
		 */	
		try {
			String dateInString = String.valueOf(y) + "/" + String.valueOf(M) + "/" + String.valueOf(d) +
				" " + String.valueOf(h) + ":" + String.valueOf(m) + ":" + String.valueOf(s);
		
			Date date_tmp = sdf.parse(dateInString);
			Calendar c1 = Calendar.getInstance();
			c1.setTime(date_tmp);
			milliseconds = c1.getTimeInMillis();
		}catch(ParseException e)
		{
			e.printStackTrace();	
		}	

		return milliseconds;
	}

	/*using to calculate all folder / file size*/
	public static long getFileFolderSize(File dir) {
		long size = 0;

		Path symbol_link = null;
		symbol_link = Paths.get(dir.getPath());

		if(Files.isSymbolicLink(symbol_link)) {
			return 0;
		}

		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				symbol_link = Paths.get(file.getPath());
				if(Files.isSymbolicLink(symbol_link)) {
//					System.out.println(file.getPath());
				}
				
				else if (file.isFile()) {
					size += file.length();
				}
				else {
					size += getFileFolderSize(file);
				}
			}
		} else if (dir.isFile()) {
			size += dir.length();
		}
		return size;
	}

	/*Transfer int to byte array*/
	public static byte intToByte(int integer) 
	{
		byte[] result = new byte[4];
		result[0] = (byte)((integer & 0xFF000000) >> 24);
		result[1] = (byte)((integer & 0x00FF0000) >> 16);
		result[2] = (byte)((integer & 0x0000FF00) >> 8);
		result[3] = (byte)(integer & 0x000000FF);

		return result[3];
	}

	/*Transfer Byte Array to Int*/
	public static int ByteArray2Int(byte[] b) 
	{
		int value=0;
		for(int i=3;i>-1;i--) {
			value += ( b[i] & UNSIGNED_BYTE_MASK ) << ( 8 * ( 4 - 1 - i ) );
		}
		return value;
	}

	public void fat16_boot_code_init()
	{
		boot_code[0] = 0x33; boot_code[1] = 0xC9; boot_code[2] = 0x8E; boot_code[3] = 0xD1;
		boot_code[4] = 0xBC; boot_code[5] = 0xF0; boot_code[6] = 0x7B; boot_code[7] = 0x8E; 
		boot_code[8] = 0xD9; boot_code[9] = 0xB8; boot_code[10] = 0x00; boot_code[11] = 0x20; 
		boot_code[12] = 0x8E; boot_code[13] = 0xC0; boot_code[14] = 0xFC; boot_code[15] = 0xBD;
		boot_code[16] = 0x00; boot_code[17] = 0x7C; boot_code[18] = 0x38; boot_code[19] = 0x4E; 
		boot_code[20] = 0x24; boot_code[21] = 0x7D; boot_code[22] = 0x24; boot_code[23] = 0x8B; 
		boot_code[24] = 0xC1; boot_code[25] = 0x99; boot_code[26] = 0xE8; boot_code[27] = 0x3C;
		boot_code[28] = 0x01; boot_code[29] = 0x72; boot_code[30] = 0x1C; boot_code[31] = 0x83; 
		boot_code[32] = 0xEB; boot_code[33] = 0x3A; boot_code[34] = 0x66; boot_code[35] = 0xA1; 
		boot_code[36] = 0x1C; boot_code[37] = 0x7C; boot_code[38] = 0x26; boot_code[39] = 0x66;
		boot_code[40] = 0x3B; boot_code[41] = 0x07; boot_code[42] = 0x26; boot_code[43] = 0x8A; 
		boot_code[44] = 0x57; boot_code[45] = 0xFC; boot_code[46] = 0x75; boot_code[47] = 0x06;
		boot_code[48] = 0x80; boot_code[49] = 0xCA; boot_code[50] = 0x02; boot_code[51] = 0x88;
		boot_code[52] = 0x56; boot_code[53] = 0x02; boot_code[54] = 0x80; boot_code[55] = 0xC3; 
		boot_code[56] = 0x10; boot_code[57] = 0x73; boot_code[58] = 0xEB; boot_code[59] = 0x33; 
		boot_code[60] = 0xC9; boot_code[61] = 0x8A; boot_code[62] = 0x46; boot_code[63] = 0x10; 
		boot_code[64] = 0x98; boot_code[65] = 0xF7; boot_code[66] = 0x66; boot_code[67] = 0x16; 
		boot_code[68] = 0x03; boot_code[69] = 0x46; boot_code[70] = 0x1C; boot_code[71] = 0x13;
		boot_code[72] = 0x56; boot_code[73] = 0x1E; boot_code[74] = 0x03; boot_code[75] = 0x46;
		boot_code[76] = 0x0E; boot_code[77] = 0x13; boot_code[78] = 0xD1; boot_code[79] = 0x8B;
		boot_code[80] = 0x76; boot_code[81] = 0x11; boot_code[82] = 0x60; boot_code[83] = 0x89; 
		boot_code[84] = 0x46; boot_code[85] = 0xFC; boot_code[86] = 0x89; boot_code[87] = 0x56; 
		boot_code[88] = 0xFE; boot_code[89] = 0xB8; boot_code[90] = 0x20; boot_code[91] = 0x00; 
		boot_code[92] = 0xF7; boot_code[93] = 0xE6; boot_code[94] = 0x8B; boot_code[95] = 0x5E; 
		boot_code[96] = 0x0B; boot_code[97] = 0x03; boot_code[98] = 0xC3; boot_code[99] = 0x48; 
		boot_code[100] = 0xF7; boot_code[101] = 0xF3; boot_code[102] = 0x01; boot_code[103] = 0x46; 
		boot_code[104] = 0xFC; boot_code[105] = 0x11; boot_code[106] = 0x4E; boot_code[107] = 0xFE; 
		boot_code[108] = 0x61; boot_code[109] = 0xBF; boot_code[110] = 0x00; boot_code[111] = 0x00; 
		boot_code[112] = 0xE8; boot_code[113] = 0xE6; boot_code[114] = 0x00; boot_code[115] = 0x72; 
		boot_code[116] = 0x39; boot_code[117] = 0x26; boot_code[118] = 0x38; boot_code[119] = 0x2D; 
		boot_code[120] = 0x74; boot_code[121] = 0x17; boot_code[122] = 0x60; boot_code[123] = 0xB1; 
		boot_code[124] = 0x0B; boot_code[125] = 0xBE; boot_code[126] = 0xA1; boot_code[127] = 0x7D; 
		boot_code[128] = 0xF3; boot_code[129] = 0xA6; boot_code[130] = 0x61; boot_code[131] = 0x74; 
		boot_code[132] = 0x32; boot_code[133] = 0x4E; boot_code[134] = 0x74; boot_code[135] = 0x09; 
		boot_code[136] = 0x83; boot_code[137] = 0xC7; boot_code[138] = 0x20; boot_code[139] = 0x3B; 
		boot_code[140] = 0xFB; boot_code[141] = 0x72; boot_code[142] = 0xE6; boot_code[143] = 0xEB; 
		boot_code[144] = 0xDC; boot_code[145] = 0xA0; boot_code[146] = 0xFB; boot_code[147] = 0x7D; 
		boot_code[148] = 0xB4; boot_code[149] = 0x7D; boot_code[150] = 0x8B; boot_code[151] = 0xF0; 
		boot_code[152] = 0xAC; boot_code[153] = 0x98; boot_code[154] = 0x40; boot_code[155] = 0x74; 
		boot_code[156] = 0x0C; boot_code[157] = 0x48; boot_code[158] = 0x74; boot_code[159] = 0x13; 
		boot_code[160] = 0xB4; boot_code[161] = 0x0E; boot_code[162] = 0xBB; boot_code[163] = 0x07; 
		boot_code[164] = 0x00; boot_code[165] = 0xCD; boot_code[166] = 0x10; boot_code[167] = 0xEB; 
		boot_code[168] = 0xEF; boot_code[169] = 0xA0; boot_code[170] = 0xFD; boot_code[171] = 0x7D; 
		boot_code[172] = 0xEB; boot_code[173] = 0xE6; boot_code[174] = 0xA0; boot_code[175] = 0xFC; 
		boot_code[176] = 0x7D; boot_code[177] = 0xEB; boot_code[178] = 0xE1; boot_code[179] = 0xCD; 
		boot_code[180] = 0x16; boot_code[181] = 0xCD; boot_code[182] = 0x19; boot_code[183] = 0x26; 
		boot_code[184] = 0x8B; boot_code[185] = 0x55; boot_code[186] = 0x1A; boot_code[187] = 0x52; 
		boot_code[188] = 0xB0; boot_code[189] = 0x01; boot_code[190] = 0xBB; boot_code[191] = 0x00; 
		boot_code[192] = 0x00; boot_code[193] = 0xE8; boot_code[194] = 0x3B; boot_code[195] = 0x00; 
		boot_code[196] = 0x72; boot_code[197] = 0xE8; boot_code[198] = 0x5B; boot_code[199] = 0x8A; 
 		boot_code[200] = 0x56; boot_code[201] = 0x24; boot_code[202] = 0xBE; boot_code[203] = 0x0B; 
		boot_code[204] = 0x7C; boot_code[205] = 0x8B; boot_code[206] = 0xFC; boot_code[207] = 0xC7; 
		boot_code[208] = 0x46; boot_code[209] = 0xF0; boot_code[210] = 0x3D; boot_code[211] = 0x7D; 
		boot_code[212] = 0xC7; boot_code[213] = 0x46; boot_code[214] = 0xF4; boot_code[215] = 0x29; 
		boot_code[216] = 0x7D; boot_code[217] = 0x8C; boot_code[218] = 0xD9; boot_code[219] = 0x89; 
		boot_code[220] = 0x4E; boot_code[221] = 0xF2; boot_code[222] = 0x89; boot_code[223] = 0x4E; 
		boot_code[224] = 0xF6; boot_code[225] = 0xC6; boot_code[226] = 0x06; boot_code[227] = 0x96; 
		boot_code[228] = 0x7D; boot_code[229] = 0xCB; boot_code[230] = 0xEA; boot_code[231] = 0x03; 
		boot_code[232] = 0x00; boot_code[233] = 0x00; boot_code[234] = 0x20; boot_code[235] = 0x0F; 
		boot_code[236] = 0xB6; boot_code[237] = 0xC8; boot_code[238] = 0x66; boot_code[239] = 0x8B; 
		boot_code[240] = 0x46; boot_code[241] = 0xF8; boot_code[242] = 0x66; boot_code[243] = 0x03; 
		boot_code[244] = 0x46; boot_code[245] = 0x1C; boot_code[246] = 0x66; boot_code[247] = 0x8B; 
		boot_code[248] = 0xD0; boot_code[249] = 0x66; boot_code[250] = 0xC1; boot_code[251] = 0xEA; 
		boot_code[252] = 0x10; boot_code[253] = 0xEB; boot_code[254] = 0x5E; boot_code[255] = 0x0F; 	
		boot_code[256] = 0xB6; boot_code[257] = 0xC8; boot_code[258] = 0x4A; boot_code[259] = 0x4A;
		boot_code[260] = 0x8A; boot_code[261] = 0x46; boot_code[262] = 0x0D; boot_code[263] = 0x32; 
		boot_code[264] = 0xE4; boot_code[265] = 0xF7; boot_code[266] = 0xE2; boot_code[267] = 0x03; 
		boot_code[268] = 0x46; boot_code[269] = 0xFC; boot_code[270] = 0x13; boot_code[271] = 0x56; 
		boot_code[272] = 0xFE; boot_code[273] = 0xEB; boot_code[274] = 0x4A; boot_code[275] = 0x52; 
		boot_code[276] = 0x50; boot_code[277] = 0x06; boot_code[278] = 0x53; boot_code[279] = 0x6A; 
		boot_code[280] = 0x01; boot_code[281] = 0x6A; boot_code[282] = 0x10; boot_code[283] = 0x91; 
		boot_code[284] = 0x8B; boot_code[285] = 0x46; boot_code[286] = 0x18; boot_code[287] = 0x96; 
		boot_code[288] = 0x92; boot_code[289] = 0x33; boot_code[290] = 0xD2; boot_code[291] = 0xF7; 
		boot_code[292] = 0xF6; boot_code[293] = 0x91; boot_code[294] = 0xF7; boot_code[295] = 0xF6; 
		boot_code[296] = 0x42; boot_code[297] = 0x87; boot_code[298] = 0xCA; boot_code[299] = 0xF7;
		boot_code[300] = 0x76; boot_code[301] = 0x1A; boot_code[302] = 0x8A; boot_code[303] = 0xF2; 
		boot_code[304] = 0x8A; boot_code[305] = 0xE8; boot_code[306] = 0xC0; boot_code[307] = 0xCC; 
		boot_code[308] = 0x02; boot_code[309] = 0x0A; boot_code[310] = 0xCC; boot_code[311] = 0xB8; 
		boot_code[312] = 0x01; boot_code[313] = 0x02; boot_code[314] = 0x80; boot_code[315] = 0x7E; 
		boot_code[316] = 0x02; boot_code[317] = 0x0E; boot_code[318] = 0x75; boot_code[319] = 0x04; 
		boot_code[320] = 0xB4; boot_code[321] = 0x42; boot_code[322] = 0x8B; boot_code[323] = 0xF4; 
		boot_code[324] = 0x8A; boot_code[325] = 0x56; boot_code[326] = 0x24; boot_code[327] = 0xCD; 
		boot_code[328] = 0x13; boot_code[329] = 0x61; boot_code[330] = 0x61; boot_code[331] = 0x72; 
		boot_code[332] = 0x0B; boot_code[333] = 0x40; boot_code[334] = 0x75; boot_code[335] = 0x01; 
		boot_code[336] = 0x42; boot_code[337] = 0x03; boot_code[338] = 0x5E; boot_code[339] = 0x0B;
		boot_code[340] = 0x49; boot_code[341] = 0x75; boot_code[342] = 0x06; boot_code[343] = 0xF8; 
		boot_code[344] = 0xC3; boot_code[345] = 0x41; boot_code[346] = 0xBB; boot_code[347] = 0x00; 
		boot_code[348] = 0x00; boot_code[349] = 0x60; boot_code[350] = 0x66; boot_code[351] = 0x6A; 
		boot_code[352] = 0x00; boot_code[353] = 0xEB; boot_code[354] = 0xB0; boot_code[355] = 0x42; 
		boot_code[356] = 0x4F; boot_code[357] = 0x4F; boot_code[358] = 0x54; boot_code[359] = 0x4D; 
		boot_code[360] = 0x47; boot_code[361] = 0x52; boot_code[362] = 0x20; boot_code[363] = 0x20; 
		boot_code[364] = 0x20; boot_code[365] = 0x20; boot_code[366] = 0x0D; boot_code[367] = 0x0A; 
		boot_code[368] = 0x52; boot_code[369] = 0x65; boot_code[370] = 0x6D; boot_code[371] = 0x6F; 
		boot_code[372] = 0x76; boot_code[373] = 0x65; boot_code[374] = 0x20; boot_code[375] = 0x64; 
		boot_code[376] = 0x69; boot_code[377] = 0x73; boot_code[378] = 0x6B; boot_code[379] = 0x73;
		boot_code[380] = 0x20; boot_code[381] = 0x6F; boot_code[382] = 0x72; boot_code[383] = 0x20; 
		boot_code[384] = 0x6F; boot_code[385] = 0x74; boot_code[386] = 0x68; boot_code[387] = 0x65; 
		boot_code[388] = 0x72; boot_code[389] = 0x20; boot_code[390] = 0x6D; boot_code[391] = 0x65; 
		boot_code[392] = 0x64; boot_code[393] = 0x69; boot_code[394] = 0x61; boot_code[395] = 0x2E; 
		boot_code[396] = 0xFF; boot_code[397] = 0x0D; boot_code[398] = 0x0A; boot_code[399] = 0x44;
		boot_code[400] = 0x69; boot_code[401] = 0x73; boot_code[402] = 0x6B; boot_code[403] = 0x20; 
		boot_code[404] = 0x65; boot_code[405] = 0x72; boot_code[406] = 0x72; boot_code[407] = 0x6F; 
		boot_code[408] = 0x72; boot_code[409] = 0xFF; boot_code[410] = 0x0D; boot_code[411] = 0x0A; 
		boot_code[412] = 0x50; boot_code[413] = 0x72; boot_code[414] = 0x65; boot_code[415] = 0x73; 
		boot_code[416] = 0x73; boot_code[417] = 0x20; boot_code[418] = 0x61; boot_code[419] = 0x6E;
		boot_code[420] = 0x79; boot_code[421] = 0x20; boot_code[422] = 0x6B; boot_code[423] = 0x65; 
		boot_code[424] = 0x79; boot_code[425] = 0x20; boot_code[426] = 0x74; boot_code[427] = 0x6F; 
		boot_code[428] = 0x20; boot_code[429] = 0x72; boot_code[430] = 0x65; boot_code[431] = 0x73; 
		boot_code[432] = 0x74; boot_code[433] = 0x61; boot_code[434] = 0x72; boot_code[435] = 0x74; 
		boot_code[436] = 0x0D; boot_code[437] = 0x0A; boot_code[438] = 0x00; boot_code[439] = 0x00;
		boot_code[440] = 0x00; boot_code[441] = 0x00; boot_code[442] = 0x00; boot_code[443] = 0x00; 
		boot_code[444] = 0x00; boot_code[445] = 0xAC; boot_code[446] = 0xCB; boot_code[447] = 0xD8;

	}

	public void fat32_boot_code_init()
	{
		boot_code[0] = 0x33; boot_code[1] = 0xC9; boot_code[2] = 0x8E; boot_code[3] = 0xD1;
		boot_code[4] = 0xBC; boot_code[5] = 0xF4; boot_code[6] = 0x7B; boot_code[7] = 0x8E; 
		boot_code[8] = 0xC1; boot_code[9] = 0x8E; boot_code[10] = 0xD9; boot_code[11] = 0xBD; 
		boot_code[12] = 0x00; boot_code[13] = 0x7C; boot_code[14] = 0x88; boot_code[15] = 0x4E;
		boot_code[16] = 0x02; boot_code[17] = 0x8A; boot_code[18] = 0x56; boot_code[19] = 0x40; 
		boot_code[20] = 0xB4; boot_code[21] = 0x41; boot_code[22] = 0xBB; boot_code[23] = 0xAA; 
		boot_code[24] = 0x55; boot_code[25] = 0xCD; boot_code[26] = 0x13; boot_code[27] = 0x72;
		boot_code[28] = 0x10; boot_code[29] = 0x81; boot_code[30] = 0xFB; boot_code[31] = 0x55; 
		boot_code[32] = 0xAA; boot_code[33] = 0x75; boot_code[34] = 0x0A; boot_code[35] = 0xF6; 
		boot_code[36] = 0xC1; boot_code[37] = 0x01; boot_code[38] = 0x74; boot_code[39] = 0x05;
		boot_code[40] = 0xFE; boot_code[41] = 0x46; boot_code[42] = 0x02; boot_code[43] = 0xEB; 
		boot_code[44] = 0x2D; boot_code[45] = 0x8A; boot_code[46] = 0x56; boot_code[47] = 0x40;
		boot_code[48] = 0xB4; boot_code[49] = 0x08; boot_code[50] = 0xCD; boot_code[51] = 0x13;
		boot_code[52] = 0x73; boot_code[53] = 0x05; boot_code[54] = 0xB9; boot_code[55] = 0xFF; 
		boot_code[56] = 0xFF; boot_code[57] = 0x8A; boot_code[58] = 0xF1; boot_code[59] = 0x66; 
		boot_code[60] = 0x0F; boot_code[61] = 0xB6; boot_code[62] = 0xC6; boot_code[63] = 0x40; 
		boot_code[64] = 0x66; boot_code[65] = 0x0F; boot_code[66] = 0xB6; boot_code[67] = 0xD1; 
		boot_code[68] = 0x80; boot_code[69] = 0xE2; boot_code[70] = 0x3F; boot_code[71] = 0xF7;
		boot_code[72] = 0xE2; boot_code[73] = 0x86; boot_code[74] = 0xCD; boot_code[75] = 0xC0;
		boot_code[76] = 0xED; boot_code[77] = 0x06; boot_code[78] = 0x41; boot_code[79] = 0x66;
		boot_code[80] = 0x0F; boot_code[81] = 0xB7; boot_code[82] = 0xC9; boot_code[83] = 0x66; 
		boot_code[84] = 0xF7; boot_code[85] = 0xE1; boot_code[86] = 0x66; boot_code[87] = 0x89; 
		boot_code[88] = 0x46; boot_code[89] = 0xF8; boot_code[90] = 0x83; boot_code[91] = 0x7E; 
		boot_code[92] = 0x16; boot_code[93] = 0x00; boot_code[94] = 0x75; boot_code[95] = 0x38; 
		boot_code[96] = 0x83; boot_code[97] = 0x7E; boot_code[98] = 0x2A; boot_code[99] = 0x00; 
		boot_code[100] = 0x77; boot_code[101] = 0x32; boot_code[102] = 0x66; boot_code[103] = 0x8B; 
		boot_code[104] = 0x46; boot_code[105] = 0x1C; boot_code[106] = 0x66; boot_code[107] = 0x83; 
		boot_code[108] = 0xC0; boot_code[109] = 0x0C; boot_code[110] = 0xBB; boot_code[111] = 0x00; 
		boot_code[112] = 0x80; boot_code[113] = 0xB9; boot_code[114] = 0x01; boot_code[115] = 0x00; 
		boot_code[116] = 0xE8; boot_code[117] = 0x2B; boot_code[118] = 0x00; boot_code[119] = 0xE9; 
		boot_code[120] = 0x2C; boot_code[121] = 0x03; boot_code[122] = 0xA0; boot_code[123] = 0xFA; 
		boot_code[124] = 0x7D; boot_code[125] = 0xB4; boot_code[126] = 0x7D; boot_code[127] = 0x8B; 
		boot_code[128] = 0xF0; boot_code[129] = 0xAC; boot_code[130] = 0x84; boot_code[131] = 0xC0; 
		boot_code[132] = 0x74; boot_code[133] = 0x17; boot_code[134] = 0x3C; boot_code[135] = 0xFF; 
		boot_code[136] = 0x74; boot_code[137] = 0x09; boot_code[138] = 0xB4; boot_code[139] = 0x0E; 
		boot_code[140] = 0xBB; boot_code[141] = 0x07; boot_code[142] = 0x00; boot_code[143] = 0xCD; 
		boot_code[144] = 0x10; boot_code[145] = 0xEB; boot_code[146] = 0xEE; boot_code[147] = 0xA0; 
		boot_code[148] = 0xFB; boot_code[149] = 0x7D; boot_code[150] = 0xEB; boot_code[151] = 0xE5; 
		boot_code[152] = 0xA0; boot_code[153] = 0xF9; boot_code[154] = 0x7D; boot_code[155] = 0xEB; 
		boot_code[156] = 0xE0; boot_code[157] = 0x98; boot_code[158] = 0xCD; boot_code[159] = 0x16; 
		boot_code[160] = 0xCD; boot_code[161] = 0x19; boot_code[162] = 0x66; boot_code[163] = 0x60; 
		boot_code[164] = 0x80; boot_code[165] = 0x7E; boot_code[166] = 0x02; boot_code[167] = 0x00; 
		boot_code[168] = 0x0F; boot_code[169] = 0x84; boot_code[170] = 0x20; boot_code[171] = 0x00; 
		boot_code[172] = 0x66; boot_code[173] = 0x6A; boot_code[174] = 0x00; boot_code[175] = 0x66; 
		boot_code[176] = 0x50; boot_code[177] = 0x06; boot_code[178] = 0x53; boot_code[179] = 0x66; 
		boot_code[180] = 0x68; boot_code[181] = 0x10; boot_code[182] = 0x00; boot_code[183] = 0x01; 
		boot_code[184] = 0x00; boot_code[185] = 0xB4; boot_code[186] = 0x42; boot_code[187] = 0x8A; 
		boot_code[188] = 0x56; boot_code[189] = 0x40; boot_code[190] = 0x8B; boot_code[191] = 0xF4; 
		boot_code[192] = 0xCD; boot_code[193] = 0x13; boot_code[194] = 0x66; boot_code[195] = 0x58; 
		boot_code[196] = 0x66; boot_code[197] = 0x58; boot_code[198] = 0x66; boot_code[199] = 0x58; 
 		boot_code[200] = 0x66; boot_code[201] = 0x58; boot_code[202] = 0xEB; boot_code[203] = 0x33; 
		boot_code[204] = 0x66; boot_code[205] = 0x3B; boot_code[206] = 0x46; boot_code[207] = 0xF8; 
		boot_code[208] = 0x72; boot_code[209] = 0x03; boot_code[210] = 0xF9; boot_code[211] = 0xEB; 
		boot_code[212] = 0x2A; boot_code[213] = 0x66; boot_code[214] = 0x33; boot_code[215] = 0xD2; 
		boot_code[216] = 0x66; boot_code[217] = 0x0F; boot_code[218] = 0xB7; boot_code[219] = 0x4E;
		boot_code[220] = 0x18; boot_code[221] = 0x66; boot_code[222] = 0xF7; boot_code[223] = 0xF1; 
		boot_code[224] = 0xFE; boot_code[225] = 0xC2; boot_code[226] = 0x8A; boot_code[227] = 0xCA; 
		boot_code[228] = 0x66; boot_code[229] = 0x8B; boot_code[230] = 0xD0; boot_code[231] = 0x66; 
		boot_code[232] = 0xC1; boot_code[233] = 0xEA; boot_code[234] = 0x10; boot_code[235] = 0xF7; 
		boot_code[236] = 0x76; boot_code[237] = 0x1A; boot_code[238] = 0x86; boot_code[239] = 0xD6; 
		boot_code[240] = 0x8A; boot_code[241] = 0x56; boot_code[242] = 0x40; boot_code[243] = 0x8A; 
		boot_code[244] = 0xE8; boot_code[245] = 0xC0; boot_code[246] = 0xE4; boot_code[247] = 0x06; 
		boot_code[248] = 0x0A; boot_code[249] = 0xCC; boot_code[250] = 0xB8; boot_code[251] = 0x01; 
		boot_code[252] = 0x02; boot_code[253] = 0xCD; boot_code[254] = 0x13; boot_code[255] = 0x66; 	
		boot_code[256] = 0x61; boot_code[257] = 0x0F; boot_code[258] = 0x82; boot_code[259] = 0x75;
		boot_code[260] = 0xFF; boot_code[261] = 0x81; boot_code[262] = 0xC3; boot_code[263] = 0x00; 
		boot_code[264] = 0x02; boot_code[265] = 0x66; boot_code[266] = 0x40; boot_code[267] = 0x49; 
		boot_code[268] = 0x75; boot_code[269] = 0x94; boot_code[270] = 0xC3; boot_code[271] = 0x42; 
		boot_code[272] = 0x4F; boot_code[273] = 0x4F; boot_code[274] = 0x54; boot_code[275] = 0x4D; 
		boot_code[276] = 0x47; boot_code[277] = 0x52; boot_code[278] = 0x20; boot_code[279] = 0x20; 
		boot_code[280] = 0x20; boot_code[281] = 0x20; boot_code[282] = 0x00; boot_code[283] = 0x00; 
		boot_code[284] = 0x00; boot_code[285] = 0x00; boot_code[286] = 0x00; boot_code[287] = 0x00; 
		boot_code[288] = 0x00; boot_code[289] = 0x00; boot_code[290] = 0x00; boot_code[291] = 0x00; 
		boot_code[292] = 0x00; boot_code[293] = 0x00; boot_code[294] = 0x00; boot_code[295] = 0x00; 
		boot_code[296] = 0x00; boot_code[297] = 0x00; boot_code[298] = 0x00; boot_code[299] = 0x00; 
		boot_code[300] = 0x00; boot_code[301] = 0x00; boot_code[302] = 0x00; boot_code[303] = 0x00;
		boot_code[304] = 0x00; boot_code[305] = 0x00; boot_code[306] = 0x00; boot_code[307] = 0x00; 
		boot_code[308] = 0x00; boot_code[309] = 0x00; boot_code[310] = 0x00; boot_code[311] = 0x00; 
		boot_code[312] = 0x00; boot_code[313] = 0x00; boot_code[314] = 0x00; boot_code[315] = 0x00; 
		boot_code[316] = 0x00; boot_code[317] = 0x00; boot_code[318] = 0x00; boot_code[319] = 0x00; 
		boot_code[320] = 0x00; boot_code[321] = 0x00; boot_code[322] = 0x00; boot_code[323] = 0x00; 
		boot_code[324] = 0x00; boot_code[325] = 0x00; boot_code[326] = 0x00; boot_code[327] = 0x00; 
		boot_code[328] = 0x00; boot_code[329] = 0x00; boot_code[330] = 0x00; boot_code[331] = 0x00; 
		boot_code[332] = 0x00; boot_code[333] = 0x00; boot_code[334] = 0x00; boot_code[335] = 0x00; 
		boot_code[336] = 0x00; boot_code[337] = 0x00; boot_code[338] = 0x0D; boot_code[339] = 0x0A; 
		boot_code[340] = 0x52; boot_code[341] = 0x65; boot_code[342] = 0x6D; boot_code[343] = 0x6F;
		boot_code[344] = 0x76; boot_code[345] = 0x65; boot_code[346] = 0x20; boot_code[347] = 0x64; 
		boot_code[348] = 0x69; boot_code[349] = 0x73; boot_code[350] = 0x6B; boot_code[351] = 0x73; 
		boot_code[352] = 0x20; boot_code[353] = 0x6F; boot_code[354] = 0x72; boot_code[355] = 0x20; 
		boot_code[356] = 0x6F; boot_code[357] = 0x74; boot_code[358] = 0x68; boot_code[359] = 0x65; 
		boot_code[360] = 0x72; boot_code[361] = 0x20; boot_code[362] = 0x6D; boot_code[363] = 0x65; 
		boot_code[364] = 0x64; boot_code[365] = 0x69; boot_code[366] = 0x61; boot_code[367] = 0x2E; 
		boot_code[368] = 0xFF; boot_code[369] = 0x0D; boot_code[370] = 0x0A; boot_code[371] = 0x44; 
		boot_code[372] = 0x69; boot_code[373] = 0x73; boot_code[374] = 0x6B; boot_code[375] = 0x20; 
		boot_code[376] = 0x65; boot_code[377] = 0x72; boot_code[378] = 0x72; boot_code[379] = 0x6F; 
		boot_code[380] = 0x72; boot_code[381] = 0xFF; boot_code[382] = 0x0D; boot_code[383] = 0x0A;
		boot_code[384] = 0x50; boot_code[385] = 0x72; boot_code[386] = 0x65; boot_code[387] = 0x73; 
		boot_code[388] = 0x73; boot_code[389] = 0x20; boot_code[390] = 0x61; boot_code[391] = 0x6E; 
		boot_code[392] = 0x79; boot_code[393] = 0x20; boot_code[394] = 0x6B; boot_code[395] = 0x65; 
		boot_code[396] = 0x79; boot_code[397] = 0x20; boot_code[398] = 0x74; boot_code[399] = 0x6F; 
		boot_code[400] = 0x20; boot_code[401] = 0x72; boot_code[402] = 0x65; boot_code[403] = 0x73;
		boot_code[404] = 0x74; boot_code[405] = 0x61; boot_code[406] = 0x72; boot_code[407] = 0x74; 
		boot_code[408] = 0x0D; boot_code[409] = 0x0A; boot_code[410] = 0x00; boot_code[411] = 0x00; 
		boot_code[412] = 0x00; boot_code[413] = 0x00; boot_code[414] = 0x00; boot_code[415] = 0xAC; 
		boot_code[416] = 0xCB; boot_code[417] = 0xD8; boot_code[418] = 0x00; boot_code[419] = 0x00; 
	}	
}

