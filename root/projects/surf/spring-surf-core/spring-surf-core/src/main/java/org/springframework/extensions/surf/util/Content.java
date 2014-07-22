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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


/**
 * Content
 * 
 * @author dcaruana
 */
public interface Content
{
	/**
	 * Gets content as a string
	 * 
	 * @return  content as a string
	 * @throws IOException
	 */
    public String getContent() throws IOException;

    /**
     * Gets the content mimetype
     * 
     * @return mimetype
     */
    public String getMimetype();
    
    /**
     * Gets the content encoding
     * 
     * @return  encoding
     */
    public String getEncoding();
    
    /**
     * Gets the content length (in bytes)
     * 
     * @return  length
     */
    public long getSize();

    /**
     * Gets the content input stream
     * 
     * @return  input stream
     */
    public InputStream getInputStream();

    /**
     * Gets the content reader (which is sensitive to encoding)
     * 
     * @return
     */
    public Reader getReader() throws IOException;
}
