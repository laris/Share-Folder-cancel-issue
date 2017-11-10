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

import java.awt.image.BufferedImage;

/**
 * Create the Image buffer base on  the Image type
 *
 */

public interface ISOCCreateBuffer {
	public void prepareBufImage(int m_width, int m_height, int type) ;
	public BufferedImage getM_image();
	public void SetImage(BufferedImage image);
	public void clearImage();
}
