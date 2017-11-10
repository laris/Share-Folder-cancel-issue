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

class KVMRequestDialogThread extends Thread {
        // This method is called when the thread runs
	KVMShareDialog kVMDialog =  JViewerApp.getInstance().getKVMShareDialog();
		public String message;
		public void run() {

			message = JViewerApp.getInstance().getMessage();
			if(kVMDialog != null)
				kVMDialog.showInformationDialog(message);
			JViewerApp.getInstance().setMessage("");
		}

}
class KVMResponseDialogThread extends Thread {
    // This method is called when the thread runs
	KVMShareDialog kVMDialog =  null;
	public String message;
	public KVMResponseDialogThread() {
		// TODO Auto-generated constructor stub
		kVMDialog =  new KVMShareDialog();
		JViewerApp.getInstance().setKVMDialog(kVMDialog);
	}
    public void run() {
    	kVMDialog.setUserStatus(KVMShareDialog.FIRST_USER);
    	if(JViewerApp.getInstance().isFullPermissionRequest()){
    		kVMDialog.constructDialog(KVMShareDialog.KVM_FULL_PERMISSION_REQUEST);
    		JViewerApp.getInstance().setFullPermissionRequest(false);
    	}
    	else
    		kVMDialog.constructDialog(KVMShareDialog.KVM_SHARING);
		kVMDialog.showDialog();


    	}
}
