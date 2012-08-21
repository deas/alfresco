/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.web.scripts;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Slingleton scripting host object provided to retrieve the value of the
 * Google Docs enabled status configuration from the Alfresco repository.
 * 
 * @author Kevin Roast
 */
@SuppressWarnings("serial")
public class GoogleDocsStatus extends SingletonValueProcessorExtension<Boolean>
{
    private static Log logger = LogFactory.getLog(GoogleDocsStatus.class);
    
    
    /**
     * @return the enabled status of the Google Docs subsystem
     */
    public boolean getEnabled()
    {
        return getSingletonValue();
    }
    
    @Override
    protected Boolean retrieveValue(String userId, String storeId) throws ConnectorServiceException
    {
        boolean enabled = false;
        
        // initiate a call to retrieve the subsystem status from the repository
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        final Connector conn = rc.getServiceRegistry().getConnectorService().getConnector("alfresco", userId, ServletUtil.getSession());
        final Response response = conn.call("/api/googledocs/status");
        if (response.getStatus().getCode() == Status.STATUS_OK)
        {
            try
            {
                // extract sync mode
                JSONObject json = new JSONObject(response.getResponse());
                if (json.has("data") && json.getJSONObject("data").has("enabled"))
                {
                    enabled = json.getJSONObject("data").getBoolean("enabled");
                    logger.info("Successfully retrieved Google Docs subsystem status from Alfresco: " + enabled);
                }
                else
                {
                    logger.error("Unexpected response from '/api/googledocs/status' - did not contain expected JSON values.");
                }
            }
            catch (JSONException e)
            {
                throw new AlfrescoRuntimeException(e.getMessage(), e);
            }
        }
        else
        {
            throw new AlfrescoRuntimeException("Unable to retrieve Google Docs subsystem status from Alfresco: " + response.getStatus().getCode());
        }
        
        return enabled;
    }

    @Override
    protected String getValueName()
    {
        return "Google Docs subsystem status";
    }
}