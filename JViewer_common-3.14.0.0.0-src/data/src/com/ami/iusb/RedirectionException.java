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
/*
 * RedirectionException.java
 *
 * Created on February 17, 2005, 3:04 PM
 */

package com.ami.iusb;

/**
 * A general exception for redirection.
 * @author andrewm@ami.com
 */
@SuppressWarnings("serial")
public class RedirectionException extends Exception
{
    /** Creates a new instance of RedirectionException
     *  @param msg A message describing this exception */
    public RedirectionException( String msg )
    {
        super( msg );
    }
}
