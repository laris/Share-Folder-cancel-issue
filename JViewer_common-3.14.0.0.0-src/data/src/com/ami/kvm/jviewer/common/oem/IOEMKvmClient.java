/****************************************************************
 **                                                            **
 **    (C) Copyright 2006-2015, American Megatrends Inc.       **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross,             **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600          **
 **                                                            **
 ****************************************************************/

package com.ami.kvm.jviewer.common.oem;

import java.nio.ByteBuffer;

import com.ami.kvm.jviewer.kvmpkts.IVTPPktHdr;

public interface IOEMKvmClient {
	/**
	 * Handles oem specific IVTP packets
	 * @param m_pktHdr
	 * @param m_ctrlMsg
	 * @return AMI_CODE if ami should handle the request <br/>
	 * OEM_CUSTOMIZED if the oem customer has handled the request
	 */
	public int handleControlPackets(IVTPPktHdr m_pktHdr, ByteBuffer m_ctrlMsg);

	/**
	 * To change menu on KVMPartialPermission
	 * @param permission
	 */
	public void enableMenuOnKVMPartialPermission(byte permission);
}
