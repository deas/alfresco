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

package org.springframework.extensions.webscripts.connector;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Representation of the response from a remote HTTP API call.
 * 
 * @author Kevin Roast
 */
public class Response
{
    private String data;
    private InputStream is;
    private ResponseStatus status;
    private String encoding = null;

    /**
     * Instantiates a new response.
     * 
     * @param status the status
     */
    public Response(ResponseStatus status)
    {
        this.status = status;
    }

    /**
     * Instantiates a new response.
     * 
     * @param data the data
     * @param status the status
     */
    public Response(String data, ResponseStatus status)
    {
        this.data = data;
        this.status = status;
    }

    /**
     * Instantiates a new response.
     * 
     * @param is the is
     * @param status the status
     */
    public Response(InputStream is, ResponseStatus status)
    {
        this.is = is;
        this.status = status;
    }

    /**
     * Sets the encoding.
     * 
     * @param encoding the new encoding
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * Gets the response.
     * 
     * @return the data stream from the response object - will be null on error
     *         or if the response has already been streamed to an OutputStream.
     */
    public String getResponse()
    {
        return this.data;
    }
    
    /**
     * Gets the text of the response.
     * 
     * @return the text
     */
    public String getText()
    {
        return this.getResponse();
    }

    /**
     * Gets the response stream.
     * 
     * @return the response InputStream if set during construction, else will be null.
     */
    public InputStream getResponseStream()
    {
        try
        {
            return (this.is != null ? this.is : new ByteArrayInputStream(
                    this.data.getBytes(encoding)));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(
                    "UnsupportedEncodingException: " + encoding);
        }
    }

    /**
     * Gets the status.
     * 
     * @return Status object representing the response status and any error information
     */
    public ResponseStatus getStatus()
    {
        return this.status;
    }

    /**
     * Gets the encoding.
     * 
     * @return the response encoding
     */
    public String getEncoding()
    {
        return this.encoding;
    }

    @Override
    public String toString()
    {
        return this.data;
    }
}
