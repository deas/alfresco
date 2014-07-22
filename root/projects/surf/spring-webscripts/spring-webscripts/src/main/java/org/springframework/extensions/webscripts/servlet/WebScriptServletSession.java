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

package org.springframework.extensions.webscripts.servlet;

import javax.servlet.http.HttpSession;

import org.springframework.extensions.webscripts.WebScriptSession;


/**
 * HTTP based Web Script Session
 * 
 * @author davidc
 */
public class WebScriptServletSession implements WebScriptSession
{
    protected HttpSession session;
    
    /**
     * Construct
     * 
     * @param session
     */
    public WebScriptServletSession(HttpSession session)
    {
        this.session = session;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.WebScriptSession#getId()
     */
    public String getId() 
    {
        return session.getId();
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.WebScriptSession#getValue(java.lang.String)
     */
    public Object getValue(String name)
    {
        return session.getAttribute(name);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.WebScriptSession#removeValue(java.lang.String)
     */
    public void removeValue(String name)
    {
        session.removeAttribute(name);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.WebScriptSession#setValue(java.lang.String, java.lang.Object)
     */
    public void setValue(String name, Object value)
    {
        session.setAttribute(name, value);
    }
    
}
