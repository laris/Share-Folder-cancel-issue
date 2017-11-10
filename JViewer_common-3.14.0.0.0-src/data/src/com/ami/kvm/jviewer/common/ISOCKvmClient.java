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
package com.ami.kvm.jviewer.common;

import java.io.FileInputStream;
import java.nio.ByteBuffer;

import com.ami.kvm.jviewer.jvvideo.JVVideo;
import com.ami.kvm.jviewer.kvmpkts.IVTPPktHdr;
import com.ami.kvm.jviewer.kvmpkts.KVMClient;

/**
 *
 *	SOCKVMclient Interface for processing SOC commands
 *	Get the SOC reader for reading the buffer
 */
public interface ISOCKvmClient {
	public void onSocControlMessage(IVTPPktHdr m_pktHdr, ByteBuffer m_ctrlMsg);
	public  Object getSOCReader(short s);
	public void SetKVMClient(KVMClient client);
	public void SetVidoclnt(JVVideo video);
	public void SOCKVM_reader();
	public void OnreadSOCVideoRecordData(FileInputStream fc_read);
}
