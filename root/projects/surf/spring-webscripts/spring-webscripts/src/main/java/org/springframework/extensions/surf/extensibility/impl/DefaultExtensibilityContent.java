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

import org.springframework.extensions.surf.extensibility.ExtensibilityContent;
import org.springframework.extensions.surf.util.StringBuilderWriter;

/**
 * <p>The default <code>ExtensibilityContent</code> implementation. This class basically
 * wraps a <code>StringBuilder</code> and delegates to its <code>append</code> and <code>flush</code>
 * methods</p>
 * 
 * @author David Draper
 */
public class DefaultExtensibilityContent implements ExtensibilityContent
{
    /**
     * <p>The <code>StringBuilder</code> that is wrapped to provide the content</p>
     */
    private StringBuilderWriter content = new StringBuilderWriter(128);
    
    public void write(char[] cbuf) throws IOException
    {
        content.write(cbuf);
    }

    public void flush()
    {
        content.flush();
    }

    @Override
    public String toString()
    {
        return content.toString();
    }

    public void write(char[] cbuf, int off, int len) throws IOException
    {
        content.write(cbuf, off, len);
    }
}
