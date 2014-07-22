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

import org.springframework.extensions.surf.extensibility.ExtensibilityModelElement;

public abstract class ModelElementImpl implements ExtensibilityModelElement
{
    public ModelElementImpl(String id, String directiveName)
    {
        this.id = id;
        this.directiveName = directiveName;
    }
    
    private String directiveName;
    
    public String getDirectiveName()
    {
        return directiveName;
    }
    
    private String id;
    
    public String getId()
    {
        return id;
    }
    
    @Override
    public int hashCode()
    {
        return this.id.length() + this.getClass().getName().length();
    }

    /**
     * <p>Two <code>ModelElements</code> are considered equal if they are of the same type, 
     * have the same identifier and have the same index and depth</p>
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean equals = false;
        if (obj instanceof ExtensibilityModelElement)
        {
            ExtensibilityModelElement that = (ExtensibilityModelElement)obj;
            equals = (this.id.equals(that.getId()) &&
                      this.getClass().equals(that.getClass()));
        }
        return equals;
    }

    @Override
    public String toString()
    {
        return "(ModelType=" + this.getClass().getSimpleName() + ", directive=" + this.directiveName + ", id=" + this.id + ")\n";
    }
}
