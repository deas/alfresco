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

package org.springframework.extensions.webscripts.portlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponseImpl;
import org.springframework.extensions.webscripts.ui.common.StringUtils;


/**
 * JSR-168 Web Script Response
 * 
 * @author davidc
 */
public class WebScriptPortletResponse extends WebScriptResponseImpl
{
    /** Portlet response */
    private RenderResponse res;
    
    
    /**
     * Construct
     * 
     * @param res
     */
    WebScriptPortletResponse(Runtime container, RenderResponse res)
    {
        super(container);
        this.res = res;
    }

    /**
     * Gets the Portlet Render Response
     * 
     * @return  render response
     */
    public RenderResponse getRenderResponse()
    {
        return res;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#setStatus(int)
     */
    public void setStatus(int status)
    {
    }
         
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#setHeader(java.lang.String, java.lang.String)
     */
    public void setHeader(String name, String value)
    {
        // NOTE: not applicable
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#addHeader(java.lang.String, java.lang.String)
     */
    public void addHeader(String name, String value)
    {
        // NOTE: not applicable
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#setContentType(java.lang.String)
     */
    public void setContentType(String contentType)
    {
        res.setContentType(contentType);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#setContentEncoding(java.lang.String)
     */
    public void setContentEncoding(String contentEncoding)
    {
        // NOTE: not applicable
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#getCache()
     */
    public void setCache(Cache cache)
    {
        // NOTE: Not applicable
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#reset()
     */
    public void reset()
    {
        try
        {
            res.reset();
        }
        catch(IllegalStateException e)
        {
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#getWriter()
     */
    public Writer getWriter() throws IOException
    {
        return res.getWriter();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException
    {
        return res.getPortletOutputStream();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#encodeScriptUrl(java.lang.String)
     */
    public String encodeScriptUrl(String url)
    {
        WebScriptRequest req = new WebScriptPortletRequest(getRuntime(), null, url, null);
        PortletURL portletUrl = res.createActionURL();
        portletUrl.setParameter("scriptUrl", req.getServicePath());
        String[] parameterNames = req.getParameterNames();
        for (String parameterName : parameterNames)
        {
            portletUrl.setParameter("arg." + parameterName, req.getParameter(parameterName));
        }
        return portletUrl.toString();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#getEncodeScriptUrlFunction(java.lang.String)
     */
    public String getEncodeScriptUrlFunction(String name)
    {
        PortletURL portletUrl = res.createActionURL();
        
        String func = ENCODE_FUNCTION.replace("$name$", name);
        func = func.replace("$actionUrl$", portletUrl.toString());
        return StringUtils.encodeJavascript(func);
    }
    
    private static final String ENCODE_FUNCTION = 
            "{ $name$: function(url) {" + 
            " var out = \"$actionUrl$\";" + 
            " var argsIndex = url.indexOf(\"?\");" + 
            " if (argsIndex == -1)" + 
            " {" + 
            "    out += \"&scriptUrl=\" + escape(url);" + 
            " }" + 
            " else" + 
            " {" + 
            "    out += \"&scriptUrl=\" + escape(url.substring(0, argsIndex));" + 
            "    var args = url.substring(argsIndex + 1).split(\"&\");" + 
            "    for (var i=0; i<args.length; i++)" + 
            "    {" + 
            "       out += \"&arg.\" + args[i];" + 
            "    }" + 
            " }" + 
            " return out; } }"; 
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.WebScriptResponse#encodeResourceUrl(java.lang.String)
     */
    public String encodeResourceUrl(String url)
    {
        WebScriptRequest req = new WebScriptPortletRequest(getRuntime(), null, url, null);
        
        ResourceURL resourceUrl = res.createResourceURL();
        resourceUrl.setResourceID(url);
        
        resourceUrl.setParameter("scriptUrl", req.getServicePath());
        String[] parameterNames = req.getParameterNames();
        for (String parameterName : parameterNames)
        {
            resourceUrl.setParameter("arg." + parameterName, req.getParameter(parameterName));
        }
        return resourceUrl.toString();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptResponse#getEncodeScriptUrlFunction(java.lang.String)
     */
    public String getEncodeResourceUrlFunction(String name)
    {
        ResourceURL resourceUrl = res.createResourceURL();
        resourceUrl.setResourceID("__RESOURCEID__");
        
        String func = ENCODE_FUNCTION.replace("$name$", name);
        func = func.replace("$resourceUrl$", resourceUrl.toString());
        return StringUtils.encodeJavascript(func);
    }

    private static final String ENCODE_RESOURCE_URL_FUNCTION = 
        "{ $name$: function(url) {" + 
        " var out = \"$resourceUrl$\";" + 
        " var i = out.indexOf(\"__RESOURCEID__\");" +
        " out = out.substring(0, i) + url + out.substring(i, out.length);" +
        " return out; } }"; 
    
}
