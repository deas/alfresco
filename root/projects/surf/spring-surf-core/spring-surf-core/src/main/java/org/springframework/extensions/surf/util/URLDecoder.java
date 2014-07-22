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

package org.springframework.extensions.surf.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * UTF-8 URL character decoder. Based on an optimized and modified version of the JDK
 * source for the class java.net.URLDecoder.
 * 
 * @author kevinr
 */
public final class URLDecoder
{
    /**
     * Decodes a <code>application/x-www-form-urlencoded</code> string using a specific 
     * encoding scheme.
     * <p>
     * UTF-8 encoding is used to determine what characters are represented by any
     * consecutive sequences of the form "<code>%<i>xy</i></code>".
     * <p>
     * The '+' plus sign is NOT converted to space on the assumption that a sensible
     * URLEncoder class {@link URLEncoder} has been used!
     *
     * @param s the non-null <code>String</code> to decode
     * 
     * @return the decoded <code>String</code>
     */
    public static String decode(final String s) 
    {
        final int len = s.length();
        StringBuilder sb = null;
        int i = 0;
        
        char c;
        byte[] bytes = null;
        while (i < len)
        {
            c = s.charAt(i);
            if (c == '%')
            {
                /*
                 * Starting with this instance of %, process all
                 * consecutive substrings of the form %xy. Each
                 * substring %xy will yield a byte. Convert all
                 * consecutive  bytes obtained this way to whatever
                 * character(s) they represent in the provided
                 * encoding.
                 */
                try
                {
                    if (sb == null)
                    {
                        final String soFar = s.substring(0, i);
                        sb = new StringBuilder(len + 16);
                        sb.append(soFar);
                    }
                    
                    // (numChars-i)/3 is an upper bound for the number
                    // of remaining bytes
                    if (bytes == null)
                    {
                        bytes = new byte[(len-i)/3];
                    }
                    int pos = 0;
                    
                    while ( ((i+2) < len) && (c=='%') )
                    {
                        bytes[pos++] = (byte)Integer.parseInt(s.substring(i+1,i+3),16);
                        i += 3;
                        if (i < len)
                        {
                            c = s.charAt(i);
                        }
                    }
                    
                    // A trailing, incomplete byte encoding such as
                    // "%x" will cause an exception to be thrown
                    if ((i < len) && (c=='%'))
                    {
                        throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
                    }
                    
                    sb.append(new String(bytes, 0, pos, "UTF-8"));
                }
                catch (UnsupportedEncodingException encErr)
                {
                    // this should not happen on any currently support JVMs!
                    throw new IllegalStateException("URLDecoder: Unable to generate UTF-8 String");
                }
                catch (NumberFormatException numErr)
                {
                    throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - " 
                            + numErr.getMessage());
                }
            }
            else
            {
                i++;
                if (sb != null)
                {
                    sb.append(c);
                }
            }
        }
        
        return (sb != null ? sb.toString() : s);
    }
}
