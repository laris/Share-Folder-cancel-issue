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

package com.ami.kvm.imageredir.cd;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.ami.kvm.imageredir.IUSBSCSIPacket;
import com.ami.kvm.imageredir.Image;
import com.ami.kvm.imageredir.SCSICommandPacket;
import com.ami.kvm.isocaching.ReadAheadCache;
import com.ami.kvm.jviewer.Debug;

public class CDImage extends Image{

	public static final int CD_BLOCK_SIZE = 2048;
	private static final int ISO_PRIMARY_VOLUME_DESCRIPTOR_LBA = 16;
	private static final int UDF_DOMAIN_ID_LBA = 35;
	private static final int UDF_DOMAIN_ID_OFFSET = 217;
	private static final int ISO_ID_SIZE = 5;
	private static final int UDF_DOMAIN_ID_SIZE = 19;
	private static final String ISO_ID = "CD001";
	private static final String UDF_DOMAIN_ID = "*OSTA UDF Compliant";

	private static final int MSF_BIT = 0x02;
	public  int dataLength = 0;
	public  byte[] append = new byte[20];

	// position at which data length byte is present
	private final int DATA_LENGTH_INDEX = 57;
	// position at which we need to start writing the data to be added
	private final int CURRENT_INDEX = 61;	
	// position of overAllStatus - 53
	private final int OVERALL_STATUS_INDEX = 53;
	// position of senseKey - 54
	private final int SENSE_KEY_INDEX = 54;
	// position of senseCode - 55
	private final int SENSE_CODE_INDEX = 55;
	// position of senseCodeQ - 56
	private final int SENSE_CODE_Q_INDEX = 56;

	private ReadAheadCache readAheadCache = null;

	private boolean firstTime = false;
	/**
	 * Constructor
	 * @param imageFile - the Image file to be opened.
	 */
	public CDImage(File imageFile){
		if(openImage(imageFile, "r") == true)
		{
			if(validateISOImage() == SUCCESS)
			{
				setOpened(true);
				setFirstTime(true);
			}
			else
			{
				setOpened(false);
			}
		}
		this.mediaChange = true;

	}

	/**
	 * Validates the image file by checking its signature.
	 * @return SUCCESS upon successful completion and the failure code on failure.
	 */
	public int validateISOImage(){
		int ret = SUCCESS;
		byte readBlockData[] = new byte[CD_BLOCK_SIZE];
		byte isoIDBuffer[] = new byte[ISO_ID_SIZE];
		byte udfDomainIDData[] = new byte[UDF_DOMAIN_ID_SIZE];
		ByteBuffer blockBuffer =ByteBuffer.wrap(readBlockData);
		String isoID = null;
		String udfDomainID = null;
		blockSize = CD_BLOCK_SIZE;
		try {
			//ISO Image primary volume descriptor is in LBA 16
			imageFile.seek(ISO_PRIMARY_VOLUME_DESCRIPTOR_LBA * CD_BLOCK_SIZE);
			ret = imageFile.read(readBlockData);
			if(0 == ret || -1 == ret)
				ret = UNREADABLE_MEDIA;
			blockBuffer.position(1);
			blockBuffer.get(isoIDBuffer);
			isoID = new String(isoIDBuffer);
			if(!isoID.equals(ISO_ID)){
				blockBuffer.clear();
				blockBuffer.rewind();
				//UDF image Domain Identifier is available in LBA 35
				imageFile.seek((UDF_DOMAIN_ID_LBA * CD_BLOCK_SIZE));
				ret = imageFile.read(readBlockData);
				if(0 == ret || -1 == ret)
					ret = UNREADABLE_MEDIA;
				//UDF image Domain Identifier is available at byte position 217
				blockBuffer.position(UDF_DOMAIN_ID_OFFSET);
				blockBuffer.get(udfDomainIDData);
				udfDomainID = new String(udfDomainIDData);
				if(udfDomainID.equals(UDF_DOMAIN_ID)){
					ret = SUCCESS;
					return ret;
				}
				ret = WRONG_MEDIA;
			}
			else{
				ret = SUCCESS;
			}

		} catch (IOException e) {
			ret = MEDIA_ERROR;
			Debug.out.println(e);
		}
		return ret;
	}

	/**
	 * Reads the CDROM image capacity
	 * Gets the information from the CDROM image like Total number of
	 * sectors and Size of each sector.
	 * @return SUCCESS upon successful completion and the failure code on failure.
	 */
	public int readCapacity() {
		int ret = SUCCESS;
		long imageFileSize;
		blockSize = CD_BLOCK_SIZE;
		try {
			imageFile.seek(0);
			imageFileSize = imageFile.length();
			imageFile.seek(imageFileSize);
			totalSectors = imageFileSize/CD_BLOCK_SIZE;
		} catch (IOException e) {
			ret = MEDIA_ERROR;
			Debug.out.println(e);
		}
		ret = SUCCESS;
		return ret;
	}

	/**
	 * Reads the requested sector data from the image file.
	 * @param sectorNo - the sector number from which reading should start.
	 * @param numSectors - the number of sectors to be read from the given sector.
	 * @return - a byte array containing the requested sector data. 
	 */
	public byte[] readCDImage(long sectorNo,int numSectors){
		byte readData[] = new byte[CD_BLOCK_SIZE*numSectors];
		if (readAheadCache == null) {
			readAheadCache = new ReadAheadCache(this.imageFile);
			readAheadCache.startThread();
			readAheadCache.setName("ReadAheadCache");
		}
		synchronized (readAheadCache.getReadSync()) {
			try {
				synchronized (readAheadCache.getCacheSync()) {
					readAheadCache.setSectorRequest(sectorNo, numSectors);
					readAheadCache.getCacheSync().notifyAll();
				}
				readAheadCache.getReadSync().wait();
				readData = readAheadCache.getSectorData();
			} catch (InterruptedException e) {
				Debug.out.println(e);
			}
			catch (Exception e) {
				readData = null;
				Debug.out.println(e);
			}
		}
		if (readData == null){
			closeImage();
		}
		return readData;
	}

	/**
	 * ExecuteSCSICmd function:
	 * 
	 * Arguments	: request  - ByteBuffer
	 * 			  	  response - ByteBuffer
	 * 
	 * Return value : returns the state of the media. Refer error codes @Image class
	 * 
	 * Description  : It processes the request based on the opcode received and forms the response
	 * 				  based on the opcode, and data is appended to the response.
	 * 
	 */
	public int executeSCSICmd(ByteBuffer requests, ByteBuffer response)
	{

		int val = SUCCESS; 
		SCSICommandPacket packet = null; 
		byte[] readBuffer = null;
		long startingSectorNumber = 0;
		long numberOfSectors = 0; 
		int maxTOCSize = 0; 

		IUSBSCSIPacket requestPacket = null;

		// creating a response byte buffer with the help of request byte array
		int transfer = Math.min(response.remaining(), requests.remaining());
		ByteBuffer temp = requests.duplicate();
		temp.limit(temp.position() + transfer);
		response.put(temp);

		// creating request IUSBSCSIPacket from request bytebuffer
		requestPacket = new IUSBSCSIPacket(requests);

		packet = requestPacket.getCommandPkt();
		// Debug.out.println(packet.getOpCode());
		switch (packet.getOpCode()) {
		case SCSICommandPacket.SCSI_TEST_UNIT_READY:
			if(isFirstTime() == true) {
				setFirstTime(false);
				val = MEDIUM_CHANGE;
			} else {
			val = testUnitReady();
			}
			setDataLength(0);

			break;
		case SCSICommandPacket.SCSI_READ_CAPACITY:

			if(isFirstTime() == true) {
				setFirstTime(false);
				val = MEDIUM_CHANGE;
				break;
			}
			val = readCapacity();
			// data length -- 8, index of data length is 57
			response.put(DATA_LENGTH_INDEX, (byte) 8);
			// the limit is 61 now. we need to add totalSectors and blockSize, so increasing the limit to 70
			response.limit(CURRENT_INDEX + 9);
			// we need to write the data starting from 61st position so setting the position to 61.
			response.position(CURRENT_INDEX);

			response.putInt(mac2blong((int) (totalSectors - 1)));
			response.putInt(mac2blong((int) blockSize));


			// data of length 8 is added to the response
			setDataLength(8);


			break;

		case SCSICommandPacket.SCSI_READ_10:
		case SCSICommandPacket.SCSI_READ_12:
			startingSectorNumber = packet.getLba();
			numberOfSectors = ((packet.getOpCode() == SCSICommandPacket.SCSI_READ_10) ? requestPacket
					.getCommandPkt().getCmd10().getLength() : packet.getCmd12()
					.getLength32());


			if (startingSectorNumber >= 0) {
				startingSectorNumber = mac2blong(packet.getLba());
			} else { // mac2blong doesn't work as expected for negative numbers.
				// for that we are shifting the bits as needed.
				byte[] tempBuff = ByteBuffer.allocate(8).putLong(startingSectorNumber).array();

				tempBuff[0] = tempBuff[1] = tempBuff[2] = tempBuff[3] = 0;

				byte temp6 = tempBuff[6];
				byte temp7 = tempBuff[7];

				tempBuff[7] = tempBuff[4];
				tempBuff[6] = tempBuff[5];

				tempBuff[4] = temp7; 
				tempBuff[5] = temp6;

				startingSectorNumber = java.nio.ByteBuffer.wrap(tempBuff).getLong();

			}
			numberOfSectors = mac2bshort(packet.getCmd10().getLength());

			readBuffer = readCDImage(startingSectorNumber, (int) numberOfSectors);

			// data length , index of data length is 57
			response.putInt(DATA_LENGTH_INDEX, readBuffer.length);

			// data buffer is added to the response, so the data length is size of data buffer
			setDataLength(readBuffer.length);

			break;

		case SCSICommandPacket.SCSI_READ_TOC:

			val = readTOC(packet);
			maxTOCSize = packet.getCmd10().getLength();
			if (dataLength > maxTOCSize)
				dataLength = maxTOCSize;
			// data length should be added, index of data length is 57
			response.put(DATA_LENGTH_INDEX, (byte) getDataLength());
			break;
		case SCSICommandPacket.SCSI_START_STOP_UNIT:
			val = SUCCESS;
			break;
		case SCSICommandPacket.SCSI_MEDIUM_REMOVAL:
		default:
			val = UNSUPPORTED_COMMAND;
			break;
		}


		setErrorStatus(val, response);

		// based on the opcode append the read buffer array or append array
		if (requestPacket.getCommandPkt().getOpCode() == SCSICommandPacket.SCSI_READ_10
				|| requestPacket.getCommandPkt().getOpCode() == SCSICommandPacket.SCSI_READ_12) {
			response.position(CURRENT_INDEX);
			response.limit(response.position() + readBuffer.length);
			response.put(readBuffer);
		} else if (requestPacket.getCommandPkt().getOpCode() == SCSICommandPacket.SCSI_READ_TOC) {
			response.position(CURRENT_INDEX);
			response.limit(response.position() + append.length);
			response.put(append);
		}

		return val;
	}

	/**
	 * Checks whether the Image file is ready to be used.
	 * @return
	 */
	private int testUnitReady() {

		int retVal;
		retVal = readCapacity();

		if(retVal != SUCCESS){
			return retVal;
		}

		return SUCCESS;
	}

	/**
	 * readTOC function 
	 * 
	 * Arguments: packet - SCSICommandPacket
	 * 
	 * Description: Reads the TOC(Table Of Contents) of the image file,
	 * and updates the response packet
	 * 
	 */
	private int readTOC(SCSICommandPacket packet) {

		int	startTrack;
		int nLength = 4;
		byte[] TOC = new byte[20];
		int allocLength = 0;

		startTrack = packet.getCmd10().getReserved6();
		allocLength = mac2bshort((char) packet.getCmd10().getLength());

		if( (startTrack > 1) && (startTrack != 0xaa) )
		{
			return 0;
		}

		TOC[2] = 1;
		TOC[3] = 1;

		if (startTrack <= 1)
		{
			TOC[nLength++] = 0;	 // Reserved
			TOC[nLength++] = 0x14; // ADR, control
			TOC[nLength++] = 1;    // Track number
			TOC[nLength++] = 0;    // Reserved

			if( (packet.getLun() & MSF_BIT) == 1)
			{
				/* Send in MSF Format */
				/* Logical ABS ADDR = (((MIN*60)+SEC)*75 + FRAC ) - 150 */
				TOC[nLength++] = 0; // reserved
				TOC[nLength++] = 0; // minute
				TOC[nLength++] = 2; // second
				TOC[nLength++] = 0; // frame
			}
			else
			{
				/* Send Direct Absolute address  */
				TOC[nLength++] = 0; // reserved
				TOC[nLength++] = 0; // minute
				TOC[nLength++] = 0; // second
				TOC[nLength++] = 0; // frame
			}
		}

		TOC[nLength++] = 0;		// Reserved
		TOC[nLength++] = 0x16;  // ADR, control
		TOC[nLength++] = (byte) 0xaa;  // Track number
		TOC[nLength++] = 0;		// Reserved

		TOC[nLength++] = 0;
		TOC[nLength++] = (byte) (((totalSectors + 150) / 75) / 60); // minute
		TOC[nLength++] = (byte)(((totalSectors + 150) / 75) % 60); // second
		TOC[nLength++] = (byte)((totalSectors + 150) % 75);		// frame;


		if(nLength > allocLength)
		{
			nLength = allocLength;
		}

		TOC[0] = (byte) (((nLength-2) >> 8) & 0xff);
		TOC[1] = (byte) ((nLength-2) & 0xff);
		setDataLength(nLength);
		System.arraycopy(TOC, 0, append, 0, TOC.length);

		return SUCCESS;
	}

	/**
	 * setErrorStatus Function:
	 * 
	 * Arguments: status value - int
	 * 			: response buffer - ByteBuffer
	 * 
	 * Description: Sets error values based on the media status.
	 * 
	 * 
	 */
	private void setErrorStatus(int val, ByteBuffer response) {

		switch(val){

		case SUCCESS:
			response.put(OVERALL_STATUS_INDEX, (byte) 0);
			response.put(SENSE_KEY_INDEX, (byte) 0);
			response.put(SENSE_CODE_INDEX, (byte) 0);
			response.put(SENSE_CODE_Q_INDEX, (byte) 0);
			break;

		case SECTOR_RANGE_ERROR:
			response.put(OVERALL_STATUS_INDEX, (byte) 1);
			response.put(SENSE_KEY_INDEX, (byte) 0x05);
			response.put(SENSE_CODE_INDEX, (byte) 0x21);
			response.put(SENSE_CODE_Q_INDEX, (byte) 0x00);
			break;

		case WRONG_MEDIA:
			response.put(OVERALL_STATUS_INDEX, (byte) 0);
			response.put(SENSE_KEY_INDEX, (byte) 0x03);
			response.put(SENSE_CODE_INDEX, (byte) 0x30);
			response.put(SENSE_CODE_Q_INDEX, (byte) 0x01);
			break;

		case MEDIUM_CHANGE:
			response.put(OVERALL_STATUS_INDEX, (byte) 1);
			response.put(SENSE_KEY_INDEX, (byte) 0x06);
			response.put(SENSE_CODE_INDEX, (byte) 0x28);
			response.put(SENSE_CODE_Q_INDEX, (byte) 0x00);
			break;

		case MEDIA_ERROR:
			response.put(OVERALL_STATUS_INDEX, (byte) 1);
			response.put(SENSE_KEY_INDEX, (byte) 0x03);
			response.put(SENSE_CODE_INDEX, (byte) 0x11);
			response.put(SENSE_CODE_Q_INDEX, (byte) 0x00);
			break;

		case NO_MEDIA:
			response.put(OVERALL_STATUS_INDEX, (byte) 1);
			response.put(SENSE_KEY_INDEX, (byte) 0x02);
			response.put(SENSE_CODE_INDEX, (byte) 0x3A);
			response.put(SENSE_CODE_Q_INDEX, (byte) 0x00);
			break;

		case INVALID_PARAMS:
			response.put(OVERALL_STATUS_INDEX, (byte) 1);
			response.put(SENSE_KEY_INDEX, (byte) 0x05);
			response.put(SENSE_CODE_INDEX, (byte) 0x26);
			response.put(SENSE_CODE_Q_INDEX, (byte) 0x00);
			break;

		case UNREADABLE_MEDIA:
			response.put(OVERALL_STATUS_INDEX, (byte) 1);
			response.put(SENSE_KEY_INDEX, (byte) 0x03);
			response.put(SENSE_CODE_INDEX, (byte) 0x30);
			response.put(SENSE_CODE_Q_INDEX, (byte) 0x02);
			break;

		case REMOVAL_PREVENTED:
			response.put(OVERALL_STATUS_INDEX, (byte) 1);
			response.put(SENSE_KEY_INDEX, (byte) 0x05);
			response.put(SENSE_CODE_INDEX, (byte) 0x53);
			response.put(SENSE_CODE_Q_INDEX, (byte) 0x02);
			break;

		case UNSUPPORTED_COMMAND:
		default:
			response.put(OVERALL_STATUS_INDEX, (byte) 1);
			response.put(SENSE_KEY_INDEX, (byte) 0x05);
			response.put(SENSE_CODE_INDEX, (byte) 0x20);
			response.put(SENSE_CODE_Q_INDEX, (byte) 0x00);
			break;
		}

	}

	public byte mac2bshort(char x)
	{
		return (byte) ((((x) >> 8) | ((x) << 8)));
	}

	public short mac2bshort(int x)
	{
		return (short) ((((short)(x) >> 8) | ((short)(x) << 8)));
	}

	public int mac2blong(int x)
	{
		return ( ((x) >> 24) | ((x) << 24) | (((x) & 0x00ff0000) >> 8) | (((x) & 0x0000ff00) << 8));
	}

	/**
	 * @return the dataLength
	 */
	public int getDataLength() {
		return dataLength;
	}

	/**
	 * @param dataLength the dataLength to set
	 */
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	/**
	 * Close the image file
	 */
	public void closeImage(){
		try {
			if(imageFile != null){
				imageFile.close();
				imageFile = null;
			}
			if(readAheadCache != null){
				if(readAheadCache.getRequestHandler() != null) {
					//set the stop thread flag before waking up the thread in wait state.
					readAheadCache.getRequestHandler().stopThread();
					synchronized (readAheadCache.getCacheLock()) {
						readAheadCache.getCacheLock().notifyAll();
					}
				}
				if(readAheadCache.getCacheManager() != null) {
					//set the stop thread flag before waking up the thread in wait state.
					readAheadCache.getCacheManager().stopThread();
					synchronized (readAheadCache.getCacheSync()) {
						readAheadCache.getCacheSync().notifyAll();
					}
				}
				readAheadCache.stopThread();
				readAheadCache = null;
			}
		} catch (IOException e) {
			imageFile = null;
			Debug.out.println(e);
		}
	}

	/**
	 * @return the firstTime
	 */
	public boolean isFirstTime() {
		return firstTime;
	}

	/**
	 * @param firstTime the firstTime to set
	 */
	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
}
