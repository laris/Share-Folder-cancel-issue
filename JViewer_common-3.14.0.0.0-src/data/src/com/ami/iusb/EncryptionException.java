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

package com.ami.iusb;

/**
 *
 * @author andrewm@ami.com
 */
@SuppressWarnings("serial")
public class EncryptionException extends RedirectionException
{
    /** Creates a new instance of EncryptionException */
    public EncryptionException( String message )
    {
        super( message );
    }
}
