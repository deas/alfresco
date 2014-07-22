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

/**
 * <p>A {@link ContentModelElement} that despite accepting unbounded content (that is content not defined by an extensibility 
 * element) does not actually output it when flushed. This is added to the model during extension processing to ensure that
 * unbounded extension content does not reach the output stream. This is important as the file content order of an extension
 * does not define it's location in the output stream so all content must either be bounded by extensibility directives or
 * will be ignored.</p>
 * 
 * @author David Draper
 */
public class DiscardUnboundContentModelElementImpl extends ModelElementImpl implements ContentModelElement
{
    public static final String TYPE = "DISCARD-UNBOUND";
    
    private static final String ID_PREFIX = "discard-unbound-";
    private static final AtomicLong IDGEN = new AtomicLong();
    
    public DiscardUnboundContentModelElementImpl()
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

    /**
     * <p>Regardless of whatever has been written to the {@link ExtensibilityContent} object this method will always
     * return the empty String to ensure that the unbounded content is "discarded".</p>
     * 
     * @return This always returns an empty String.
     */
    public String flushContent()
    {
        return "";
    }
    
    public String getType()
    {
        return TYPE;
    }
}
