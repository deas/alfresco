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
package org.alfresco.module.vti.web.fp;

import java.io.IOException;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.web.VtiEncodingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling "url to web url" method
 * 
 * @author PavelYur
 */
public class UrlToWebUrlMethod extends AbstractMethod
{
    private static Log logger = LogFactory.getLog(UrlToWebUrlMethod.class);
    
    /**
     * Given a URL for a file, returns the URL of the Web site to which 
     * the file belongs, and the subsite, if applicable
     * 
     * @param request Vti Frontpage request ({@link VtiFpRequest})
     * @param response Vti Frontpage response ({@link VtiFpResponse})
     */
    protected void doExecute(VtiFpRequest request, VtiFpResponse response) throws VtiMethodException, IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Start method execution. Method name: " + getName());
        }
        String url = request.getParameter("url", "");

        if (url != null && url.length() > 0)
        {
            String alfrescoContext = request.getAlfrescoContextName();
            String[] relativeUrls = null;
            try
            {
                relativeUrls = vtiHandler.decomposeURL(url, alfrescoContext);
            }
            catch (VtiHandlerException e)
            {
                throw new VtiMethodException(e);
            }

            response.beginVtiAnswer(getName(), ServerVersionMethod.version);
            response.addParameter("webUrl=" + VtiEncodingUtils.encode(relativeUrls[0]));
            response.addParameter("fileUrl=" + VtiEncodingUtils.encode(relativeUrls[1]));
            response.endVtiAnswer();
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

    /**
     * returns methods name
     */
    public String getName()
    {
        return "url to web url";
    }

}
