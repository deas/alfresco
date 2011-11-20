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

import org.alfresco.module.vti.metadata.dic.VtiConstraint;
import org.alfresco.module.vti.metadata.dic.VtiProperty;
import org.alfresco.module.vti.metadata.dic.VtiType;
import org.alfresco.module.vti.web.VtiEncodingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling OpenService Method
 *
 * @author andreyak
 */
public class OpenServiceMethod extends AbstractMethod
{
    
    private static Log logger = LogFactory.getLog(OpenServiceMethod.class);
            
    private static final String METHOD_NAME = "open service";

    public String getName()
    {
        return METHOD_NAME;
    }

    /**
     * Provides meta-information for a Web site to the client application
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
        String service_name = request.getParameter("service_name");

        // Office 2008/2011 for Mac special case
        if (service_name == null)
        {
            service_name = "";
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Opening service with name: '" + service_name + "'");
        }
        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.beginList("service");
        response.addParameter("service_name=" + (service_name.equals("/") ? "" : VtiEncodingUtils.encode(service_name)));
        response.addParameter("meta_info=");
        response.beginList();
        response.writeMetaDictionary(VtiProperty.SERVICE_CASESENSITIVEURLS, VtiType.INT, VtiConstraint.X, "0");
        response.writeMetaDictionary(VtiProperty.SERVICE_LONGFILENAMES, VtiType.INT, VtiConstraint.X, "1");
        response.writeMetaDictionary(VtiProperty.SERVICE_WELCOMENAMES, VtiType.VECTOR, VtiConstraint.X, "index.html");
        response.writeMetaDictionary(VtiProperty.SERVICE_USERNAME, VtiType.STRING, VtiConstraint.X, VtiEncodingUtils.encode(vtiHandler.getUserName()));
        response.writeMetaDictionary(VtiProperty.SERVICE_SERVERTZ, VtiType.STRING, VtiConstraint.X, vtiHandler.getServertimeZone());
        response.writeMetaDictionary(VtiProperty.SERVICE_SOURCECONTROLSYSTEM, VtiType.STRING, VtiConstraint.R, "lw");
        response.writeMetaDictionary(VtiProperty.SERVICE_SOURCECONTROLVERSION, VtiType.STRING, VtiConstraint.R, "V1");
        response.writeMetaDictionary(VtiProperty.SERVICE_DOCLIBWEBVIEWENABLED, VtiType.INT, VtiConstraint.X, "1");
        response.writeMetaDictionary(VtiProperty.SERVICE_SOURCECONTROLCOOKIE, VtiType.STRING, VtiConstraint.X, "fp_internal");
        response.writeMetaDictionary(VtiProperty.SERVICE_SOURCECONTROLPROJECT, VtiType.STRING, VtiConstraint.X, "&#60;STS-based Locking&#62;");
        response.endList();
        response.endList();
        response.endVtiAnswer();

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }
}