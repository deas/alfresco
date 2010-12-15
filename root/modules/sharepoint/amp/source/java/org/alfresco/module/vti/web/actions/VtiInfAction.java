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
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.web.VtiAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiInfAction returns the information to determine the entry point for
* the Microsoft FrontPage Server Extensions.</p>
*
* @author Michael Shavnev
*/
public class VtiInfAction implements VtiAction
{
    private static final long serialVersionUID = 429709350002602411L;

    private final static Log logger = LogFactory.getLog(VtiBaseAction.class);

    /**
     * <p>Return the information to determine the entry point for 
     * the Microsoft FrontPage Server Extensions.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            OutputStream outputStream = response.getOutputStream();
            outputStream.write("<!-- FrontPage Configuration Information\n".getBytes());
            outputStream.write(" FPVersion=\"14.00.0.000\"\n".getBytes());
            outputStream.write("FPShtmlScriptUrl=\"_vti_bin/shtml.dll/_vti_rpc\"\n".getBytes());
            outputStream.write("FPAuthorScriptUrl=\"_vti_bin/_vti_aut/author.dll\"\n".getBytes());
            outputStream.write("FPAdminScriptUrl=\"_vti_bin/_vti_adm/admin.dll\"\n".getBytes());
            outputStream.write("TPScriptUrl=\"_vti_bin/owssvr.dll\"\n".getBytes());
            outputStream.write("-->".getBytes());
            outputStream.close();
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