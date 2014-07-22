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


/**
 * Web Script Status (version 2.x)
 * 
 * NOTE: PROVIDED FOR BACKWARDS COMPATIBILITY ONLY - see org.alfresco.web.scripts.Statust
 * 
 * @author davidc
 * @deprecated
 */
public class WebScriptStatus
{
    private Status status;;
    
    /**
     * Construct
     * 
     * @param status
     */
    public WebScriptStatus(Status status)
    {
        this.status = status;
    }
    
    /**
     * @param exception
     */
    public void setException(Throwable exception)
    {
        status.setException(exception);
    }

    /**
     * @return  exception
     */
    public Throwable getException()
    {
        return status.getException();
    }
    
    /**
     * @param message
     */
    public void setMessage(String message)
    {
        status.setMessage(message);
    }

    /**
     * @return  message
     */
    public String getMessage()
    {
        return status.getMessage();
    }

    /**
     * @param redirect  redirect to status code response
     */
    public void setRedirect(boolean redirect)
    {
        status.setRedirect(redirect);
    }

    /**
     * @return redirect to status code response
     */
    public boolean getRedirect()
    {
        return status.getRedirect();
    }

    /**
     * @see javax.servlet.http.HTTPServletResponse
     * 
     * @param code  status code
     */
    public void setCode(int code)
    {
        status.setCode(code);
    }

    /**
     * @return  status code
     */
    public int getCode()
    {
        return status.getCode();
    }

    /**
     * Gets the short name of the status code
     * 
     * @return  status code name
     */
    public String getCodeName()
    {
        return status.getCodeName();
    }
    
    /**
     * Gets the description of the status code
     * 
     * @return  status code description
     */
    public String getCodeDescription()
    {
        return status.getCodeDescription();
    }
    
}
