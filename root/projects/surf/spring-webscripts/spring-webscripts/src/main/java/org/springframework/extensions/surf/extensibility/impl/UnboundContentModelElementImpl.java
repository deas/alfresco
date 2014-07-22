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

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.extensions.surf.extensibility.ContentModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityContent;

public class UnboundContentModelElementImpl extends ModelElementImpl implements ContentModelElement
{
    public static final String TYPE = "UNBOUND";
    
    private static final String ID_PREFIX = "unbound-";
    private static final AtomicLong IDGEN = new AtomicLong();
    
    public UnboundContentModelElementImpl()
    {
        super(ID_PREFIX + Long.toHexString(IDGEN.getAndIncrement()), TYPE);
    }

    private ExtensibilityContent content = new DefaultExtensibilityContent();
    
    /**
     * <p>This always returns the same <code>DefaultExtensibilityContentImpl</code> that is associated with the 
     * current instance.</p>
     */
    public ExtensibilityContent getNextContentBufferElement()
    {
        return content;
    }

    public String flushContent()
    {
        return content.toString();
    }
    
    public String getType()
    {
        return TYPE;
    }
}
