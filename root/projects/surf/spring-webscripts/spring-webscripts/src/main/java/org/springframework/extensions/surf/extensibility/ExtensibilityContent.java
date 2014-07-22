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
package org.springframework.extensions.surf.extensibility;

import java.io.IOException;

/**
 * <p>Defines an object that can be have character strings appended to it and flushed. At 
 * its most basic level a <code>ExtensibilityContent</code> could just be a <code>StringBuilder</code>
 * but it is provided to allow <code>ContentModelElements</code> the capability to accept 
 * character strings that are then manipulated before being flushed.</p>
 * 
 * <p>This interface has primarily been introduced to support the capability of building up merged JSON
 * strings which can then be output as a single JavaScript command</p>
 *  
 * @author David Draper
 */
public interface ExtensibilityContent
{
    /**
     * <p>Should be implemented to allow content to be appended</p>
     */
    public void write(char[] cbuf, int off, int len) throws IOException;
    
    /**
     * <p>Should be implemented to flush the content that has been constructed</p>
     */
    public void flush();
}
