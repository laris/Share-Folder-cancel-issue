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

///////////////////////////////////////////////////////////////////////////////
//
// KVMReader is an interface that is implemented by other readers to read
// network data.
//

package com.ami.kvm.jviewer.kvmpkts;

import java.net.Socket;


/**
 * KVMReader interface
 */
public interface KVMReader {

	/**
	 * initialize the reader
	 */
	public void initialize();

	/**
	 * read
	 * @return the status.
	 */
	public int read(Socket socket);

}