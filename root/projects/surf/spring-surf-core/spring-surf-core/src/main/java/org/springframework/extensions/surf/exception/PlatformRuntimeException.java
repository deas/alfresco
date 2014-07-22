/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.surf.exception;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Base exception for Spring Surf platform (i18n enabled)
 * 
 * @author gavinc
 */
public class PlatformRuntimeException extends RuntimeException
{
    /**
     * Serial version UUID
     */
    private static final long serialVersionUID = 3834594313622859827L;

    /**
     * Helper factory method making use of variable argument numbers
     */
    public static PlatformRuntimeException create(String msgId, Object ...objects)
    {
        return new PlatformRuntimeException(msgId, objects);
    }

    /**
     * Helper factory method making use of variable argument numbers
     */
    public static PlatformRuntimeException create(Throwable cause, String msgId, Object ...objects)
    {
        return new PlatformRuntimeException(msgId, objects, cause);
    }
    
    /**
     * Utility to convert a general Throwable to a RuntimeException.  No conversion is done if the
     * throwable is already a <tt>RuntimeException</tt>.
     * 
     * @see #create(Throwable, String, Object...)
     */
    public static RuntimeException makeRuntimeException(Throwable e, String msgId, Object ...objects)
    {
        if (e instanceof RuntimeException)
        {
            return (RuntimeException) e;
        }
        // Convert it
        return PlatformRuntimeException.create(e, msgId, objects);
    }
    
    /**
     * Constructor
     * 
     * @param msgId     the message id
     */
    public PlatformRuntimeException(String msgId)
    {
        super(resolveMessage(msgId, null));
    }
    
    /**
     * Constructor
     * 
     * @param msgId         the message id
     * @param msgParams     the message parameters
     */
    public PlatformRuntimeException(String msgId, Object[] msgParams)
    {
        super(resolveMessage(msgId, msgParams));
    }

    /**
     * Constructor
     * 
     * @param msgId     the message id
     * @param cause     the exception cause
     */
    public PlatformRuntimeException(String msgId, Throwable cause)
    {
        super(resolveMessage(msgId, null), cause);
    }
    
    /**
     * Constructor
     * 
     * @param msgId         the message id
     * @param msgParams     the message parameters
     * @param cause         the exception cause
     */
    public PlatformRuntimeException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(resolveMessage(msgId, msgParams), cause);
    }
    
    /**
     * Resolves the message id to the localised string.
     * <p>
     * If a localised message can not be found then the message Id is
     * returned.
     * 
     * @param messageId     the message Id
     * @param params        message parameters
     * @return              the localised message (or the message id if none found)
     */
    private static String resolveMessage(String messageId, Object[] params)
    {
        String message = I18NUtil.getMessage(messageId, params);
        if (message == null)
        {
            // If a localised string cannot be found then return the messageId
            message = messageId;
        }
        return buildErrorLogNumber(message);
    }
    
    /**
     * Generate an error log number - based on MMDDXXXX - where M is month,
     * D is day and X is an atomic integer count.
     * 
     * @param message       Message to prepend the error log number to 
     * 
     * @return message with error log number prefix
     */
    private static String buildErrorLogNumber(String message)
    {
        // ensure message is not null
        if (message == null)
        {
            message= "";
        }
        
        Date today = new Date();
        StringBuilder buf = new StringBuilder(message.length() + 10);
        padInt(buf, today.getMonth(), 2);
        padInt(buf, today.getDate(), 2);
        padInt(buf, errorCounter.getAndIncrement(), 4);
        buf.append(' ');
        buf.append(message);
        return buf.toString();
    }
    
    /**
     * Helper to zero pad a number to specified length 
     */
    private static void padInt(StringBuilder buffer, int value, int length)
    {
        String strValue = Integer.toString(value);
        for (int i = length - strValue.length(); i > 0; i--)
        {
            buffer.append('0');
        }
        buffer.append(strValue);
    }
    
    private static AtomicInteger errorCounter = new AtomicInteger();
}
