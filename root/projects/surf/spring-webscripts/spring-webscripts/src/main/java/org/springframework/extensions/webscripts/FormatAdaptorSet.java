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

import java.util.Set;

import org.springframework.beans.factory.InitializingBean;


/**
 * Set of Format Readers and Writers.
 * 
 * @author davidc
 */
public class FormatAdaptorSet implements InitializingBean
{
    private FormatRegistry registry;
    private Set<FormatReader<Object>> readers;
    private Set<FormatWriter<Object>> writers;

    /**
     * Sets the Format Registry
     * 
     * @param registry
     */
    public void setRegistry(FormatRegistry registry)
    {
        this.registry = registry;
    }
    
    /**
     * Sets the readers
     * 
     * @param readers
     */
    public void setReaders(Set<FormatReader<Object>> readers)
    {
        this.readers = readers;
    }

    /**
     * Sets the writers
     * 
     * @param writers
     */
    public void setWriters(Set<FormatWriter<Object>> writers)
    {
        this.writers = writers;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception
    {
        if (readers != null)
        {
            for (FormatReader<Object> reader : readers)
            {
                registry.addReader(reader);
            }
        }
        if (writers != null)
        {
            for (FormatWriter<Object> writer : writers)
            {
                registry.addWriter(writer);
            }
        }
    }
    
}
