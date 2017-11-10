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
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import com.ami.kvm.jviewer.Debug;


public class LongFileName
{
	private byte sequence = 0;
	private byte attribute = 0;
	private byte type = 0;
	private byte checksum;
	//public short first_cluster = 0;
	private byte[] lfnCharacter = new byte[26];
	private int lfnPackNum = 0;
	private byte[] byteInFilename = new byte[0];
	private static final int LFN_CHECK_ARRAY_NUMBER = 11;

	public LongFileName()
	{
		attribute = (short) 0x0f;
	}

	public byte getSequence(){
		return this.sequence;
	}
	public void setSequence(byte val){
		this.sequence = val;
	}

	public byte getAttribute(){
		return this.attribute;
	}

	public byte getType(){
		return this.type;
	}

	public byte getChecksum(){
		return this.checksum;
	}
	public void setChecksum(byte val){
		this.checksum = val;
	}

	public byte[] getLfnCharacter(){
		return this.lfnCharacter;
	}
	public void setLfnCharacter(byte[] val){
		this.lfnCharacter = val;
	}

	public int getLfnPackNum(){
		return this.lfnPackNum;
	}

	public byte[] getByteInFilename(){
		return this.byteInFilename;
	}
	public void setByteInFilename(byte[] val){
		this.byteInFilename = val;
	}

	/*add new byte item into byte array*/
	public byte[] combine(byte[] dst, byte[] src){
		int length = dst.length + src.length - 2, srcIndex = 0;
		byte[] result = new byte[length];

		System.arraycopy(dst, 0, result, 0, dst.length);

		if(src != null && src.length > 0) {
			System.arraycopy(src, 0, result, dst.length, src.length - 2);
		}

		return result;
	}

	/*using to change file name string to the byte array,
	and also estimate how many LFN package will using*/
	public int getLNFByteNum(String name) {
		char[] ch = name.toCharArray();
		
		int byteNum = 0;
		for(int i = 0 ; i < ch.length ; i++) {
			byte[] c;
			c = transferToUnicode(ch[i]);
			for(int j = 0 ; j < c.length ; j++) {
				if( (c[j] & MasterBootRecord.UNSIGNED_BYTE_MASK) != 0xFF && (c[j] & MasterBootRecord.UNSIGNED_BYTE_MASK) != 0xFE)
					byteNum++;
			}
			byteInFilename = combine(byteInFilename , c);
		}
		
		if( byteNum % MasterBootRecord.LFN_CHAR_LEN_IN_BYTE  == 0 ) {
			lfnPackNum = (byteNum / MasterBootRecord.LFN_CHAR_LEN_IN_BYTE);
		}
		else if((byteNum + 2) % MasterBootRecord.LFN_CHAR_LEN_IN_BYTE  == 0)// lfn ending character is 0x0000
			lfnPackNum = ( byteNum + 2 ) / MasterBootRecord.LFN_CHAR_LEN_IN_BYTE;
		else
			lfnPackNum = ( byteNum + 2 ) / MasterBootRecord.LFN_CHAR_LEN_IN_BYTE + 1;
		
		return byteNum;
	}
	
	public byte[] transferToUnicode(char c) {
		Character ch = new Character(c);
		String sCh = ch.toString();
		char[] ca = sCh.toCharArray();
		ByteBuffer bb = Charset.forName("unicode").encode(CharBuffer.wrap(ca));
		byte[] b = new byte[bb.remaining()];
		bb.get(b);

		//transfer byte array from Big endian to little endian
		int temp = MasterBootRecord.ByteArray2Int(b);
		ByteBuffer byteBuffer = ByteBuffer.allocate(b.length);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byteBuffer.putInt(temp);
		byte[] result = byteBuffer.array();

		return result;
	}

	//Get Long File Name on a FAT Volumeo
	//When you create a file that has a long file name, Windows Server 2003 creates a conventional 8.3 name for the file 
	//and one or more secondary folder entries for the file, one for each set of 13 characters in the long file name.
	public char[] getLFNpackage(RandomAccessFile pImg, long rootStartAddr, LongFileName sub_lfn)
	{
		char[] lfnCharSizeFive = new char[5];
		char[] lfnCharSizeSix = new char[6];
		char[] lfnCharSizeTwo = new char[2];
		char[] result_name = new char[0];
		try {
			pImg.seek(rootStartAddr);
			sub_lfn.sequence = (byte)pImg.readUnsignedByte();

			for(int i = 0 ; i < lfnCharSizeFive.length ; i++)	{
				lfnCharSizeFive[i] = (char)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
			}
	
			sub_lfn.attribute = (byte)pImg.readUnsignedByte();
			sub_lfn.type = (byte)pImg.readUnsignedByte();
			sub_lfn.checksum = (byte)pImg.readUnsignedByte();

			for(int i = 0 ; i < lfnCharSizeSix.length ; i++) {
				lfnCharSizeSix[i] = (char)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
			}

			//first_cluster = (short)pImg.readUnsignedShort();
			
			for(int i = 0 ; i < lfnCharSizeTwo.length ; i++) {
				lfnCharSizeTwo[i] = (char)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
			}

			for(int i = 0 ; i < MasterBootRecord.LFN_CHAR_SIZE ; i++) {
				if(i < 5 ) {
					if( lfnCharSizeFive[i] != 0x0) {
						result_name = extend(result_name, lfnCharSizeFive[i]);
					}
					else 
						break;
				}
				else if(i < 11 ) {
					if( lfnCharSizeSix[i - 5] != 0x0)  {
						result_name = extend(result_name, lfnCharSizeSix[i - 5]);
					}
					else 
						break;
				}
				else  {
					if ( lfnCharSizeTwo[i - 11] != 0x0) {
						result_name = extend(result_name, lfnCharSizeTwo[i - 11]);
					}
					else 
						break;
				}
			}
		}
		catch(IOException e)
		{
			Debug.out.println(e);
		}
		return result_name;
	}

	public byte LfnChecksum(RootDirectory rootDir)
	{
		byte sum = 0;
		byte[] lfnCheckArray = new byte[LFN_CHECK_ARRAY_NUMBER];
		
		System.arraycopy(rootDir.filename, 0, lfnCheckArray, 0, rootDir.filename.length);//arraycopy(srcArray, srcArrayStartIndex, dstArray, dstArrayStartIndex, copyLength)
		System.arraycopy(rootDir.ext, 0, lfnCheckArray, rootDir.filename.length, rootDir.ext.length);

		for(int i = 0 ; i < 11 ; i++)
			sum = MasterBootRecord.intToByte( (((sum & 1) << 7) | ((sum & 0xFE) >> 1)) + (int)( lfnCheckArray[i] & MasterBootRecord.UNSIGNED_BYTE_MASK ) );

		return sum;
	}

	/*
	check previous package attribute and LFN checksum
	using to make sure previous package is the LNF or not
	the check role is follow two 
	1. using previous package's attribute field is equal 0xf or not
	2. using checksum
	*/
	public boolean LFNCheck(RandomAccessFile pImg, long rootStartAddr, byte check)
	{
		byte sequence = 0;
		char[] lfnCharSizeFive = new char[5];
		byte attribute = 0;
		byte type = 0;
		byte checksum = 0;
		char[] lfnCharSizeSix = new char[6];
		//short first_cluster = 0;
		char[] lfnCharSizeTwo = new char[2];

		try {
			pImg.seek(rootStartAddr);
			sequence = (byte)pImg.readUnsignedByte();

			for(int i = 0 ; i < lfnCharSizeFive.length ; i++)	
				lfnCharSizeFive[i] = (char)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
	
			attribute = (byte)pImg.readUnsignedByte();
			type = (byte)pImg.readUnsignedByte();
			checksum = (byte)pImg.readUnsignedByte();

			for(int i = 0 ; i < lfnCharSizeSix.length ; i++) 
				lfnCharSizeSix[i] = (char)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);

			//first_cluster = (short)pImg.readUnsignedShort();
			
			for(int i = 0 ; i < lfnCharSizeTwo.length ; i++) 
				lfnCharSizeTwo[i] = (char)MasterBootRecord.ChangeToLittleEndian(pImg.readUnsignedShort(),2, MasterBootRecord.LITTLE_ENDIAN_TYPE);
		}
		catch(IOException e)
		{
			Debug.out.println(e);
		}
		if(attribute == 0x0F && checksum == check) {
			return true;
		}
		else {
			return false;
		}
	}

	public final char[] extend(char array[], char ch)
	{
		int length = array.length;
		char[] tempArray = new char[length + 1];
		System.arraycopy(array, 0, tempArray, 0, length);
		tempArray[length] = ch;

		return tempArray;
	}

	public void debug()
	{
		System.out.printf("0x%2H ",sequence & MasterBootRecord.UNSIGNED_BYTE_MASK);
		for(int i = 0 ; i < 10 ; i++)
		System.out.printf("0x%2H ",attribute & MasterBootRecord.UNSIGNED_BYTE_MASK);
		System.out.printf("0x%2H ",type & MasterBootRecord.UNSIGNED_BYTE_MASK);
		System.out.printf("0x%2H ",checksum & MasterBootRecord.UNSIGNED_BYTE_MASK);//chechsum
		for(int i = 0 ; i < 12 ; i++) {
			if(i == 1)
				System.out.println();
		}
		//System.out.printf("0x%2H ",first_cluster);
		//System.out.printf("0x%2H ",first_cluster);
		for(int i = 0 ; i < 4 ; i++)
		System.out.println();
		System.out.println();
	}
}
