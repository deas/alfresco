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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling ServerVersion Method
 *
 * @author Michael Shavnev
 */
public class ServerVersionMethod extends AbstractMethod
{
    private static final int major = 14;
    private static final int minor = 0;
    private static final int phase = 0;
    private static final int increment = 4730;    
    
    public static final String version = major + "." + minor + "." + phase + "." + increment;
    
    private static Log logger = LogFactory.getLog(ServerVersionMethod.class);
    
    public String getName()
    {
        return "server version";
    }

    /**
     * Returns the exact version of Microsoft Windows SharePoint Services
     * that are emulated on a Web server
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
        response.beginVtiAnswer(getName(), version);
        response.beginList(getName());
        response.addParameter("major ver=" + major);
        response.addParameter("minor ver=" + minor);
        response.addParameter("phase ver=" + phase);
        response.addParameter("ver incr=" + increment);
        response.endList();
        response.addParameter("source control=1");
        response.endVtiAnswer();

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

}
