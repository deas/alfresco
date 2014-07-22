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

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.exception.PlatformRuntimeException;

/**
 * Web Script Exceptions.
 * 
 * @author David Caruana
 */
public class WebScriptException extends PlatformRuntimeException implements StatusTemplateFactory
{
    private static final long serialVersionUID = -7338963365877285084L;

    private int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    private StatusTemplateFactory statusTemplateFactory;


    public WebScriptException(String msgId)
    {
       super(msgId);
    }

    public WebScriptException(int status, String msgId)
    {
        this(msgId);
        this.status = status;
    }
    
    public WebScriptException(String msgId, Throwable cause)
    {
       super(msgId, cause);
    }

    public WebScriptException(int status, String msgId, Throwable cause)
    {
       super(msgId, cause);
       this.status = status;
    }

    public WebScriptException(String msgId, Object ... args)
    {
        super(msgId, args);
    }

    public WebScriptException(int status, String msgId, Object ... args)
    {
        super(msgId, args);
        this.status = status;
    }

    public WebScriptException(String msgId, Throwable cause, Object ... args)
    {
        super(msgId, args, cause);
    }

    public WebScriptException(int status, String msgId, Throwable cause, Object ... args)
    {
        super(msgId, args, cause);
        this.status = status;
    }

    /**
     * Attach an advanced description of the status code associated to this exception
     * 
     * @param template  status template
     * @param model  template model
     * @deprecated
     */
    public void setStatusTemplate(final StatusTemplate statusTemplate, final Map<String, Object> statusModel)
    {
        setStatusTemplateFactory(new StatusTemplateFactory()
        {

            public Map<String, Object> getStatusModel()
            {
                return statusModel;
            }

            public StatusTemplate getStatusTemplate()
            {
                return statusTemplate;
            }
        });
    }

    
    /**
     * Associates a factory for the lazy retrieval of an advanced description of the status code associated with this
     * exception
     * 
     * @param statusTemplateFactory
     *            the factory to set
     */
    public void setStatusTemplateFactory(StatusTemplateFactory statusTemplateFactory)
    {
        this.statusTemplateFactory = statusTemplateFactory;
    }

    /**
     * Get status code
     * 
     * @return  status code
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * Get status template
     * 
     * @return  template
     */
    public StatusTemplate getStatusTemplate()
    {
        return this.statusTemplateFactory == null ? null : this.statusTemplateFactory.getStatusTemplate();
    }

    /**
     * Get status model
     * 
     * @return  model
     */
    public Map<String, Object> getStatusModel()
    {
        Map <String,Object> statusModel = null;
        if (this.statusTemplateFactory != null)
        {
            statusModel = this.statusTemplateFactory.getStatusModel();
        }
        return statusModel == null ? Collections.<String, Object> emptyMap() : statusModel;
    }

}
