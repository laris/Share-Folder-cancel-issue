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

////////////////////////////////////////////////////////////////////////////////
//
// KVM sharing Macro base class.
//

package com.ami.kvm.jviewer.gui;

public class KVMSharing {

	public static final byte STATUS_KVM_PRIV_REQ_CANCEL		=0x00; // reserved. not used for now.
	public static final byte STATUS_KVM_PRIV_REQ_MASTER		=0x01; // to user 1
	public static final byte STATUS_KVM_PRIV_WAIT_SLAVE	=0x02; // to user 2
	public static final byte STATUS_KVM_PRIV_REQ_TIMEOUT_TO_MASTER		=0x03; // to user 1
	public static final byte STATUS_KVM_PRIV_RESPONSE_TO_SLAVE		=0x04; // to user 2
	public static final byte STATUS_KVM_PRIV_RES_USER1		=0x05; // from user 1
	public static final byte STATUS_KVM_PRIV_SWITCH_MASTER		=0x06; // To user 1

	public static final byte KVM_INIT_SESSION 				= -1;//The initial value for KVM permission.
	public static final byte KVM_REQ_ALLOWED 				= 0;
	public static final byte KVM_REQ_DENIED 				= 1;
	public static final byte KVM_REQ_PARTIAL 				= 2;
	public static final byte KVM_REQ_TIMEOUT 				= 3;//by default gra
	public static final byte KVM_NOT_MASTER					= 4;
	public static final byte KVM_REQ_PROGRESS				= 5;
	public static final byte KVM_REQ_BLOCKED_PARTIAL		= 6;
	public static final byte KVM_REQ_MASTER_RECONN			= 7; // master is in reconnect
	public static final byte KVM_REQ_BLOCKED_DENY			= 8;
	public static final byte KVM_MASTER_TERMINATED			= 9;

	public static String KVM_CLIENT_USERNAME;
	public static String KVM_CLIENT_IP;
	public static String KVM_CLIENT_SESSION_INDEX;
	public static int KVM_CLIENT_IPMI_PRIV;
	public static String KVM_CLIENT_OWN_USERNAME;
	public static String KVM_CLIENT_OWN_IP;
	public static String KVM_CLIENT_OWN_MAC;
	public static byte KVM_REQ_GIVEN = KVM_INIT_SESSION;// by default init value;

	public static  byte KVM_PRIV_RES_USER = 0x00;
	public static  final byte KVM_PRIV_MASTER_USER = 0x01;
	public static  final byte KVM_PRIV_SLAVE_USER = 0x02;
}
