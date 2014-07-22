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

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.util.I18NUtil;


/**
 * Web Script Status
 *
 * Records the outcome of a Web Script.
 * 
 * @author davidc
 */
public class Status
{
    /** Status code constants */
    public static final int STATUS_CONTINUE = HttpServletResponse.SC_CONTINUE;
    public static final int STATUS_SWITCHING_PROTOCOLS = HttpServletResponse.SC_SWITCHING_PROTOCOLS;
    public static final int STATUS_OK = HttpServletResponse.SC_OK;
    public static final int STATUS_CREATED = HttpServletResponse.SC_CREATED;
    public static final int STATUS_ACCEPTED = HttpServletResponse.SC_ACCEPTED;
    public static final int STATUS_NON_AUTHORITATIVE_INFORMATION = HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION;
    public static final int STATUS_NO_CONTENT = HttpServletResponse.SC_NO_CONTENT;
    public static final int STATUS_RESET_CONTENT = HttpServletResponse.SC_RESET_CONTENT;
    public static final int STATUS_PARTIAL_CONTENT = HttpServletResponse.SC_PARTIAL_CONTENT;
    public static final int STATUS_MULTIPLE_CHOICES = HttpServletResponse.SC_MULTIPLE_CHOICES;
    public static final int STATUS_MOVED_PERMANENTLY = HttpServletResponse.SC_MOVED_PERMANENTLY;
    public static final int STATUS_MOVED_TEMPORARILY = HttpServletResponse.SC_MOVED_TEMPORARILY;
    public static final int STATUS_FOUND = HttpServletResponse.SC_FOUND;
    public static final int STATUS_SEE_OTHER = HttpServletResponse.SC_SEE_OTHER;
    public static final int STATUS_NOT_MODIFIED = HttpServletResponse.SC_NOT_MODIFIED;
    public static final int STATUS_USE_PROXY = HttpServletResponse.SC_USE_PROXY;
    public static final int STATUS_TEMPORARY_REDIRECT = HttpServletResponse.SC_TEMPORARY_REDIRECT;
    public static final int STATUS_BAD_REQUEST = HttpServletResponse.SC_BAD_REQUEST;
    public static final int STATUS_UNAUTHORIZED = HttpServletResponse.SC_UNAUTHORIZED;
    public static final int STATUS_PAYMENT_REQUIRED = HttpServletResponse.SC_PAYMENT_REQUIRED;
    public static final int STATUS_FORBIDDEN = HttpServletResponse.SC_FORBIDDEN;
    public static final int STATUS_NOT_FOUND = HttpServletResponse.SC_NOT_FOUND;
    public static final int STATUS_METHOD_NOT_ALLOWED = HttpServletResponse.SC_METHOD_NOT_ALLOWED;
    public static final int STATUS_NOT_ACCEPTABLE = HttpServletResponse.SC_NOT_ACCEPTABLE;
    public static final int STATUS_PROXY_AUTHENTICATION_REQUIRED = HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED;
    public static final int STATUS_REQUEST_TIMEOUT = HttpServletResponse.SC_REQUEST_TIMEOUT;
    public static final int STATUS_CONFLICT = HttpServletResponse.SC_CONFLICT;
    public static final int STATUS_GONE = HttpServletResponse.SC_GONE;
    public static final int STATUS_LENGTH_REQUIRED = HttpServletResponse.SC_LENGTH_REQUIRED;
    public static final int STATUS_PRECONDITION_FAILED = HttpServletResponse.SC_PRECONDITION_FAILED;
    public static final int STATUS_REQUEST_ENTITY_TOO_LARGE = HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE;
    public static final int STATUS_REQUEST_URI_TOO_LONG = HttpServletResponse.SC_REQUEST_URI_TOO_LONG;
    public static final int STATUS_UNSUPPORTED_MEDIA_TYPE = HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
    public static final int STATUS_REQUESTED_RANGE_NOT_SATISFIABLE = HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE;
    public static final int STATUS_EXPECTATION_FAILED = HttpServletResponse.SC_EXPECTATION_FAILED;
    public static final int STATUS_INTERNAL_SERVER_ERROR = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    public static final int STATUS_NOT_IMPLEMENTED = HttpServletResponse.SC_NOT_IMPLEMENTED;
    public static final int STATUS_BAD_GATEWAY = HttpServletResponse.SC_BAD_GATEWAY;
    public static final int STATUS_SERVICE_UNAVAILABLE = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
    public static final int STATUS_GATEWAY_TIMEOUT = HttpServletResponse.SC_GATEWAY_TIMEOUT;
    public static final int STATUS_HTTP_VERSION_NOT_SUPPORTED = HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED;
    
    
    private Throwable exception = null;
    private String location = "";
    private int code = HttpServletResponse.SC_OK;
    private String message = "";
    private boolean redirect = false;

    /**
     * Helper method to set the code and message.  
     * <p>
     * Redirect is set to true.
     * 
     * @param code      code
     * @param message   message
     */
    public void setCode(int code, String message)
    {
        this.code = code;
        this.message = message;
        this.redirect = true;
    }
    
    /**
     * @param exception
     */
    public void setException(Throwable exception)
    {
        this.exception = exception;
    }

    /**
     * @return  exception
     */
    public Throwable getException()
    {
        return exception;
    }
    
    /**
     * @param message
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return  message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param redirect  redirect to status code response
     */
    public void setRedirect(boolean redirect)
    {
        this.redirect = redirect;
    }

    /**
     * @return redirect to status code response
     */
    public boolean getRedirect()
    {
        return redirect;
    }

    /**
     * @see javax.servlet.http.HTTPServletResponse
     * 
     * @param code  status code
     */
    public void setCode(int code)
    {
        this.code = code;
    }

    /**
     * @return  status code
     */
    public int getCode()
    {
        return code;
    }

    /**
     * Gets the short name of the status code
     * 
     * @return  status code name
     */
    public String getCodeName()
    {
        String codeName =  I18NUtil.getMessage("webscript.code." + code + ".name");
        return codeName == null ? "" : codeName;
    }
    
    /**
     * @see javax.servlet.http.HTTPServletResponse
     * 
     * @param location  location response-header
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * @return  location
     */
    public String getLocation()
    {
        return location;
    }
    
    /**
     * Gets the description of the status code
     * 
     * @return  status code description
     */
    public String getCodeDescription()
    {
        String codeDescription = I18NUtil.getMessage("webscript.code." + code + ".description");
        return codeDescription == null ? "" : codeDescription;
    }

    @Override
    public String toString()
    {
        return Integer.toString(code);
    }
}
