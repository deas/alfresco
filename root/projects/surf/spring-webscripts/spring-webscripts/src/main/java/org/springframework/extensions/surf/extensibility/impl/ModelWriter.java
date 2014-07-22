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
package org.springframework.extensions.surf.extensibility.impl;

import java.io.IOException;
import java.io.Writer;

import org.springframework.extensions.surf.extensibility.ExtensibilityContent;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirective;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;

/**
 * <p>An extension of the {@link Writer} class that is used with the {@link ExtensibilityModel}. Rather than writing directly to
 * an output stream this writes to {@link ExtensibilityContent} instances in the {@link ExtensibilityModel}. This allows rendered 
 * content to be removed, replaced or added to as extensions to the model are processed.</p>
 * 
 * @author David Draper
 */
public class ModelWriter extends Writer
{
    /**
     * <p>The {@link ExtensibilityContent} that will be used for all abstract methods defined by {@link Writer}.</p> 
     */
    private ExtensibilityContent currentBufferElement = null;
    
    /**
     * <p>Sets the current {@link ExtensibilityContent} to be written to. This typically occurs when the processing of
     * a new {@link ExtensibilityDirective} begins.</p>
     * 
     * @param bufferElement The element to start writing to.
     */
    public void setCurrentBufferElement(ExtensibilityContent bufferElement)
    {
        this.currentBufferElement = bufferElement;
    }
    
    /**
     * <p>Returns the current {@link ExtensibilityContent} being written to.</p>
     * @return
     */
    public ExtensibilityContent getCurrentBufferElement()
    {
        return this.currentBufferElement;
    }
    
    /**
     * <p>Writes to the current {@link ExtensibilityContent} instance.</p>
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException
    {
        ExtensibilityContent currentElement = getCurrentBufferElement();
        if (currentElement != null)
        {
            currentElement.write(cbuf, off, len);
        }
    }

    /**
     * <p>Flushes the current {@link ExtensibilityContent} instance.</p>
     */
    @Override
    public void flush() throws IOException
    {
        this.currentBufferElement.flush();
    }

    /**
     * <p>Closes the current {@link ExtensibilityContent} instance.</p>
     */
    @Override
    public void close() throws IOException
    {
        this.currentBufferElement.flush();
    }

    /**
     * <p>Writes to the current {@link ExtensibilityContent} instance.</p>
     */
    @Override
    public void write(int c) throws IOException
    {
        char[] writeBuffer = new char[1];
        writeBuffer[0] = (char) c;
        write(writeBuffer, 0, 1);
    }
    
    /**
     * <p>Writes to the current {@link ExtensibilityContent} instance.</p>
     */
    @Override
    public void write(String str, int off, int len) throws IOException
    {
        char cbuf[] = new char[len];
        str.getChars(off, (off + len), cbuf, 0);
        write(cbuf, 0, len);
    }
}
