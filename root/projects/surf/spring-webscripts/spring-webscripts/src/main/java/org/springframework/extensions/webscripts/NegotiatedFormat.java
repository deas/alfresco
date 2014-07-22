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

package org.springframework.extensions.webscripts;


/**
 * Map between media type and format
 * 
 * This class is immutable.
 * 
 * @author davidc
 */
public final class NegotiatedFormat
{
    final private MediaType mediaType;
    final private String format;
    
    /**
     * Construct
     * 
     * @param mediaType
     * @param format
     */
    public NegotiatedFormat(MediaType mediaType, String format)
    {
        this.mediaType = mediaType;
        this.format = format;
    }

    /**
     * @return  media type
     */
    public MediaType getMediaType()
    {
        return mediaType;
    }
    
    /**
     * @return  format
     */
    public String getFormat()
    {
        return format;
    }
    
    /**
     * Negotiate Format - given a list of accepted media types, return the format that's
     * most suitable
     * 
     * @param accept  comma-seperated list of accepted media types
     * @param negotiatedFormats  list of available formats
     * @return  most suitable format (or null, if none)
     */
    public static String negotiateFormat(String accept, NegotiatedFormat[] negotiatedFormats)
    {
        String format = null;
        float match = 0.0f;
        String[] acceptTypes = accept.split(",");
        for (String acceptType : acceptTypes)
        {
            MediaType acceptMediaType = new MediaType(acceptType);
            for (NegotiatedFormat negotiatedFormat : negotiatedFormats)
            {
                float negotiatedMatch = negotiatedFormat.getMediaType().compare(acceptMediaType);
                if (negotiatedMatch > match)
                {
                    match = negotiatedMatch;
                    format = negotiatedFormat.getFormat();
                }
            }
        }
        return format;
    }
}