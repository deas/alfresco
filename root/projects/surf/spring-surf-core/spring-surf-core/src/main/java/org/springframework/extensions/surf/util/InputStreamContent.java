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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import org.springframework.util.FileCopyUtils;


/**
 * Input Stream based Content
 */
public class InputStreamContent implements Content, Serializable
{
    private static final long serialVersionUID = -7729633986840536282L;
    
    private InputStream stream;    
    private String mimetype;
    private String encoding;
    
    /** cached result - to ensure we only read it once */
    private String content;
    
    
    /**
     * Constructor
     * 
     * @param stream    content input stream
     * @param mimetype  content mimetype
     */
    public InputStreamContent(InputStream stream, String mimetype, String encoding)
    {
        this.stream = stream;
        this.mimetype = mimetype;
        this.encoding = encoding;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.util.Content#getContent()
     */
    public String getContent()
        throws IOException
    {
        // ensure we only try to read the content once - as this method may be called several times
        // but the inputstream can only be processed a single time
        if (this.content == null)
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
            FileCopyUtils.copy(stream, os);  // both streams are closed
            byte[] bytes = os.toByteArray();
            // get the encoding for the string
            String encoding = getEncoding();
            // create the string from the byte[] using encoding if necessary
            this.content = (encoding == null) ? new String(bytes) : new String(bytes, encoding);
        }
        return this.content;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.util.Content#getInputStream()
     */
    public InputStream getInputStream()
    {
        return stream;
    }

    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.util.Content#getReader()
     */
    public Reader getReader()
        throws IOException
    {
        return (encoding == null) ? new InputStreamReader(stream) : new InputStreamReader(stream, encoding);
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.util.Content#getSize()
     */
    public long getSize()
    {
        return -1;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.util.Content#getMimetype()
     */
    public String getMimetype()
    {
        return mimetype;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.util.Content#getEncoding()
     */
    public String getEncoding()
    {
        return encoding;
    }

}