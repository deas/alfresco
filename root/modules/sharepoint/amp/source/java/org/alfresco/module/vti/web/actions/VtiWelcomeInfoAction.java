/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.module.vti.web.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.util.UrlUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiWelcomeInfoAction provides a bit of user facing welcome
*  information if they try visiting the Vti endpoint in a browser.</p>
* <p>This prevents someone visiting the Vti endpoint root in their
*  browser from receiving an empty page, which may incorrectly 
*  lead them to believe that Vti isn't working when it is.</p>
* 
* TODO Decide if this needs to be localised, or if that isn't
*  needed as this action is only ever seen by accident.
* 
* @author Nick Burch
*/
public class VtiWelcomeInfoAction implements VtiAction
{
    private static final long serialVersionUID = 42971231932602411L;

    private final static Log logger = LogFactory.getLog(VtiBaseAction.class);
    
    private String template = "/alfresco/templates/org/alfresco/vti/welcome-info.html.ftl";

    private SysAdminParams sysAdminParams;
    private TemplateService templateService;
    
    public void setSysAdminParams(SysAdminParams sysAdminParams)
    {
       this.sysAdminParams = sysAdminParams;
    }
    public void setTemplateService(TemplateService templateService)
    {
       this.templateService = templateService;
    }
    
    /**
     * <p>Return the information to determine the entry point for 
     * the Microsoft FrontPage Server Extensions.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        // Build the model
        Map<String,Object> model = new HashMap<String, Object>();
        model.put("alfrescoUrl", UrlUtil.getAlfrescoUrl(sysAdminParams));
        model.put("shareUrl", UrlUtil.getShareUrl(sysAdminParams));
        
        // Get the welcome template
        InputStream templateStream = getClass().getResourceAsStream(template);
        if(templateStream == null)
        {
           logger.warn("Welcome template missing: " + template);
           return;
        }
        StringBuilder template = new StringBuilder();
        try
        {
           InputStreamReader r = new InputStreamReader(templateStream, Charset.forName("UTF-8"));
           int read;
           char[] c = new char[4096];
           while( (read = r.read(c)) != -1 )
           {
              template.append(c, 0, read);
           }
           r.close();
        }
        catch(IOException e)
        {
           if (logger.isDebugEnabled())
           {
               logger.debug("Action IO exception", e);
           }
        }
        finally
        {
           try { templateStream.close(); } catch(Exception e) {}
        }
        
        try
        {
           String res = templateService.processTemplateString(
                 "freemarker", template.toString(), model
           );
           PrintWriter r = response.getWriter();
           r.append(res);
           r.close();
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Action IO exception", e);
            }
        }
    }
}