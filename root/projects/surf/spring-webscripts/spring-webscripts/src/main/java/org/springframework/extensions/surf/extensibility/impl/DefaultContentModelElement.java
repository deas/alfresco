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

import org.springframework.extensions.surf.extensibility.ContentModelElement;
import org.springframework.extensions.surf.extensibility.ValidatedContentModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityContent;

public class DefaultContentModelElement extends ModelElementImpl implements ValidatedContentModelElement
{
    public DefaultContentModelElement(String id, String directiveName)
    {
        super(id, directiveName);
    }

    public void parseContent()
    {
        // No parsing required for default content.
    }

    private ExtensibilityContent content = new DefaultExtensibilityContent();
    
    public ExtensibilityContent getNextContentBufferElement()
    {
        return content;
    }

    public boolean validateContent()
    {
        // No validation required for default content
        return true;
    }

    public String flushContent()
    {
        return content.toString();
    }

    @Override
    public int hashCode()
    {
        return this.getId().length();
    }

    /**
     * <p>Overrides the default to loosen the parameters slightly. Two <code>ContentModelElement</code>
     * objects are equal if they both implement the <code>ContentModelElement</code> and have the same "id"
     * attribute. This means that two <code>ContentModelElement</code> instances are still considered equal
     * even if they are not implemented by the same class. This makes searching for content in the content
     * model easier.</p> 
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean equals = (obj instanceof ContentModelElement &&
                          this.getId().equals(((ContentModelElement) obj).getId()));
        
        return equals;
    }
    
    public String getType()
    {
        return ContentModelElement.TYPE;
    }
}
