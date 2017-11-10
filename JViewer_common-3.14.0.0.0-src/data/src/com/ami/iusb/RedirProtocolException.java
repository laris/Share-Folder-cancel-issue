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
 * ProtocolException.java
 *
 * Created on April 15, 2005, 2:46 PM
 */

package com.ami.iusb;

/**
 * This exception is thrown when there is a problem with the contents or
 * sequence of redirection packets or headers.  This could indicate a
 * problem with protocol versions, or cleverly disguised broken network I/O.
 * @author andrewm@ami.com
 */
@SuppressWarnings("serial")
public class RedirProtocolException extends RedirectionException
{
    /** Creates a new instance of ProtocolException
     *  @param msg A message describing this exception */
    public RedirProtocolException( String msg )
    {
        super( msg );
    }
}
