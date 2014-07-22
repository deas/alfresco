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

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;


/**
 * A map of mimetypes indexed by format.
 * 
 * @author davidc
 */
public class FormatMap implements InitializingBean
{
    private FormatRegistry registry;
    private String agent;
    private Map<String, String> formats;
    private Map<String, String> mimetypes;
    

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
     * Sets the User Agent for which the formats apply
     * 
     * @param agent
     */
    public void setAgent(String agent)
    {
        this.agent = agent;
    }
    
    /**
     * Sets the formats
     * 
     * @param formats
     */
    public void setFormats(Map<String, String> formats)
    {
        this.formats = formats;
    }
    
    /**
     * Sets the mimetypes
     *  
     * @param mimetypes
     */
    public void setMimetypes(Map<String, String> mimetypes)
    {
        this.mimetypes = mimetypes;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception
    {
        if (formats != null)
        {
            registry.addFormats(agent, formats);
        }
        if (mimetypes != null)
        {
            registry.addMimetypes(agent, mimetypes);
        }
    }
    
}
