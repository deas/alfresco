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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;


/**
 * Web Script Response
 * 
 * @author davidc
 */
public interface WebScriptResponse
{
    // API Formats
    public static final String HTML_FORMAT = "html";
    public static final String ATOM_FORMAT = "atom";
    public static final String RSS_FORMAT = "rss";
    public static final String XML_FORMAT = "xml";
    public static final String JSON_FORMAT = "json";
    public static final String OPENSEARCH_DESCRIPTION_FORMAT = "opensearchdescription";

    // Headers
    public static final String HEADER_LOCATION = "Location";
    
    
    /**
     * Sets the Response Status
     * 
     * @param status
     */
    public void setStatus(int status);
    
    /**
     * Set a response header with the given name and value.  If the header has
     * already been set, the new value overwrites the previous one.
     * 
     * @param name  header name
     * @param value  header value
     */
    public void setHeader(String name, String value);

    /**
     * Adds a response header with the given name and value.  This method
     * allows a response header to have multiple values.
     * 
     * @param name  header name
     * @param value  header value
     */
    public void addHeader(String name, String value);
    
    /**
     * Sets the Content Type
     * 
     * @param contentType
     */
    public void setContentType(String contentType);
    
    /**
     * Sets the Content Encoding
     * 
     * @param contentEncoding
     */
    public void setContentEncoding(String contentEncoding);
    
    /**
     * Sets the Cache control
     * 
     * @param  cache  cache control
     */
    public void setCache(Cache cache);
    
    /**
     * Gets the Writer
     * 
     * @return writer
     * @throws IOException
     */
    public Writer getWriter() throws IOException;
    
    /**
     * Gets the Output Stream
     * 
     * @return output stream
     * @throws IOException
     */
    public OutputStream getOutputStream() throws IOException;
    
    /**
     * Clears response buffer
     */
    public void reset();
        
    /**
     * Encode a script URL
     * 
     * Note: Some Web Script Runtime environments (e.g. JSR-168, JSF) require urls to be re-written.
     * 
     * @param url  to encode
     * @return encoded url
     */
    public String encodeScriptUrl(String url);

    /**
     * Encode a resource URL
     * 
     * Note: Some Web Script Runtime environments (e.g. JSR-268, Surf) require urls to be re-written.
     * 
     * @param url  to encode
     * @return encoded url
     */
    public String encodeResourceUrl(String url);
    
    /**
     * Return a client side javascript function to build urls to this service
     *  
     * @param name      Generated function name
     *  
     * @return javascript function definition
     */
    public String getEncodeScriptUrlFunction(String name);

    /**
     * Return a client side javascript function to build resource urls for this service
     *  
     * @param name      Generated function name
     *  
     * @return javascript function definition
     */
    public String getEncodeResourceUrlFunction(String name);
    
    /**
     * Gets the initiating runtime
     * 
     * @return  runtime that constructed this response
     */
    public Runtime getRuntime();
        
}
