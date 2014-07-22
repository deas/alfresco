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

public interface ContentModelElement extends ExtensibilityModelElement
{
    public static final String TYPE = "CONTENT";
    
    /**
     * <p>This should be implemented to return the next buffer element to write to.
     * Depending upon the implementation this method could always return the same
     * buffer element. The buffer element in this case should be an object that is and
     * implementation of <code>ExtensibilityContent</code>.</p>
     * 
     * @return An <code>ExtensibilityContent</code> object to write to.
     */
    public ExtensibilityContent getNextContentBufferElement();
    
    /**
     * <p>Flushes the content of the all the buffer elements</p>
     * 
     * @return A single String which is the output of all the buffer elements.
     */
    public String flushContent();
}
