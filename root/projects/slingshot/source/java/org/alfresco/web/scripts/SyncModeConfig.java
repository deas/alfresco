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

import org.alfresco.web.site.EditionInterceptor.EditionInfo;
import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Slingleton scripting host object provided to retrieve the value of the
 * Sync Mode configuration from the Alfresco repository.
 * 
 * @author Kevin Roast
 */
@SuppressWarnings("serial")
public class SyncModeConfig extends BaseProcessorExtension implements Serializable
{
    private static Log logger = LogFactory.getLog(SyncModeConfig.class);
    
    /** Map of Alfresco tenant domains to Sync Mode config values */
    private final Map<String, String> syncModeConfigs = new HashMap<String, String>();
    
    /** Lock for access to config data */
    private final ReadWriteLock configLock = new ReentrantReadWriteLock();
    
    
    /**
     * @return the Sync Mode configuration from the Alfresco repository.<p>
     *         Will be one of: CLOUD, ON_PREMISE, OFF
     */
    public String getValue()
    {
        return getSyncModeConfig();
    }

    /**
     * Return the dictionary for the current user context. Takes into account the current user
     * tenant domain and will retrieve the data dictionary from the remote Alfresco tier.
     * 
     * @return the dictionary for the current user context
     */
    private String getSyncModeConfig()
    {
        String syncModeConfig;
        
        // resolve the tenant domain from the user ID
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        final String userId = rc.getUserId();
        if (userId == null || AuthenticationUtil.isGuest(userId))
        {
            throw new AlfrescoRuntimeException("User ID must exist and cannot be guest.");
        }
        String storeId = "";            // default domain
        int idx = userId.indexOf('@');
        if (idx != -1)
        {
            // assume MT so partition by user domain
            storeId = userId.substring(idx);
        }
        
        // NOTE: currently there is a single RRW lock for all syncModeConfigs -
        // in a heavily multi-tenant scenario (especially ones with new tenants
        // being created often) the first access of a new tenant dictionary would
        // potentially slow other tenant users access to their dictionary.
        // In this situation a lock per tenant would be preferable.
        this.configLock.readLock().lock();
        try
        {
            syncModeConfig = syncModeConfigs.get(storeId);
            if (syncModeConfig == null)
            {
                this.configLock.readLock().unlock();
                this.configLock.writeLock().lock();
                try
                {
                    // check again, as more than one thread could have been waiting on the Write lock 
                    syncModeConfig = syncModeConfigs.get(storeId);
                    if (syncModeConfig == null)
                    {
                       // Sync requires Enterprise features. Sync config is missing on other editions, so default to off.
                       String edition = ((EditionInfo)ThreadLocalRequestContext.getRequestContext().getValue("editionInfo")).getEdition();

                        if ("ENTERPRISE".equals(edition))
                        {
                            // initiate a call to retrieve the sync mode from the repository
                            final Connector conn = rc.getServiceRegistry().getConnectorService().getConnector("alfresco", userId, ServletUtil.getSession());
                            final Response response = conn.call("/enterprise/sync/config");
                            if (response.getStatus().getCode() == Status.STATUS_OK)
                            {
                                logger.info("Successfully retrieved Sync Mode configuration from Alfresco." +
                                            (storeId.length() != 0 ? (" - for domain: " + storeId) : ""));

                                // extract sync mode
                                final JSONObject json = new JSONObject(response.getResponse());
                                if (json.has("syncMode"))
                                {
                                    syncModeConfig = json.getString("syncMode");
                                }
                                else
                                {
                                    logger.error("Unexpected response from '/enterprise/sync/config' - did not contain expected 'syncMode' value.");
                                }

                                // store the config data against the tenant domain
                                this.syncModeConfigs.put(storeId, syncModeConfig);
                            }
                            else
                            {
                               throw new AlfrescoRuntimeException("Unable to retrieve Sync Mode configuration from Alfresco: " + response.getStatus().getCode());
                            }
                        }
                        else
                        {
                           this.syncModeConfigs.put(storeId, "OFF");
                        }
                    }
                }
                catch (ConnectorServiceException cerr)
                {
                    throw new AlfrescoRuntimeException("Unable to retrieve Sync Mode configuration from Alfresco: " + cerr.getMessage());
                }
                catch (Exception err)
                {
                    throw new AlfrescoRuntimeException("Failed processing Sync Mode configuration from Alfresco: " + err.getMessage());
                }
                finally
                {
                    this.configLock.readLock().lock();
                    this.configLock.writeLock().unlock();
                }
            }
        }
        finally
        {
            this.configLock.readLock().unlock();
        }
        return syncModeConfig;
    }
}
