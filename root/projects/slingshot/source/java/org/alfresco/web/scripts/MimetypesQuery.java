/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
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

/**
 * Slingleton templating host object provided to allows templates access to
 *  the Alfresco Repository Mimetypes information. 
 * <p>
 * Service object that maintains no state other than the mimetypes info itself.
 * 
 * @author Nick Burch
 */
@SuppressWarnings("serial")
public class MimetypesQuery extends BaseProcessorExtension implements Serializable
{
    private static Log logger = LogFactory.getLog(MimetypesQuery.class);
    
    /** 
     * Map of Mimetypes to their details.
     * Mimetypes are not per-tenant, so we only need one map 
     */
    private Map<String,Mimetype> mimetypes; 
    
    /** Lock for access to mimetypes */
    private final ReadWriteLock mimetypesLock = new ReentrantReadWriteLock();
    
    /**
     * Get all human readable mimetype descriptions, indexed by mimetype
     *
     * @return the map of displays indexed by mimetype
     */
    public Map<String,String> getDisplaysByMimetype()
    {
        Map<String, String> descriptions = new HashMap<String, String>();
        Map<String, Mimetype> mimetypes = getMimetypes();
        
        for (Mimetype mimetype : mimetypes.values())
        {
            descriptions.put(mimetype.getMimetype(), mimetype.getDescription());
        }
        
        return descriptions;
    }
    
    /**
     * Gets all the human readable mimetype descriptions, sorted, along
     *  with their mimetypes.
     */
    public Map<String,String> getMimetypesByDisplay()
    {
        // Sorted by key, case insensitive
        Map<String, String> types = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2)
            {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });
        
        Map<String, Mimetype> mimetypes = getMimetypes();
        for (Mimetype mimetype : mimetypes.values())
        {
            types.put(mimetype.getDescription(), mimetype.getMimetype());
        }
        
        return types;
    }
    
    /**
     * Get the extension for the specified mimetype  
     * 
     * @param mimetype a valid mimetype
     * @return Returns the default extension for the mimetype, or null if the mimetype is unknown
     */
    public String getExtension(String mimetype)
    {
        Mimetype mt = getMimetypes().get(mimetype);
        if (mt != null)
        {
            return mt.getDefaultExtension();
        }
        return null;
    }
    
    @Override
    public String toString()
    {
        try
        {
            String out = "";
            final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
            final String userId = rc.getUserId();
            if (userId != null && !AuthenticationUtil.isGuest(userId))
            {
                int idx = userId.indexOf('@');
                if (idx != -1)
                {
                    out = "Mimetypes for user domain: " + userId.substring(idx) + "\r\n";
                }
            }
            return out + getMimetypes().toString();
        }
        catch (Throwable e)
        {
            return super.toString();
        }
    }
    
    /**
     * Return the Mimetypes Details, retrieving as needed from the remote Alfresco tier.
     */
    private Map<String,Mimetype> getMimetypes()
    {
        // We don't need to worry about tenants, as the mimetypes are repository
        //  wide across all of them
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        final String userId = rc.getUserId();
        
        // Get the lock read lock on the mimetypes, then fetch if needed
        this.mimetypesLock.readLock().lock();
        try
        {
            if (mimetypes == null)
            {
                this.mimetypesLock.readLock().unlock();
                this.mimetypesLock.writeLock().lock();
                try
                {
                    // check again, as more than one thread could have been waiting on the Write lock 
                    if (mimetypes == null)
                    {
                        // initiate a call to retrieve the dictionary from the repository
                        final Connector conn = rc.getServiceRegistry().getConnectorService().getConnector("alfresco", userId, ServletUtil.getSession());
                        final Response response = conn.call("/api/mimetypes/descriptions");
                        if (response.getStatus().getCode() == Status.STATUS_OK)
                        {
                            logger.info("Successfully retrieved mimetypes information from Alfresco.");
                            
                            mimetypes = new HashMap<String, Mimetype>(128);
                            
                            // Extract mimetype information
                            final JSONObject json = new JSONObject(response.getResponse());
                            final JSONObject data = json.getJSONObject("data");
                            
                            Iterator<String> types = data.keys();
                            while (types.hasNext())
                            {
                                // The type is the key
                                String mimetype = types.next();
                                
                                // The details come from the value
                                Mimetype details = new Mimetype(mimetype, data.getJSONObject(mimetype));
                                
                                mimetypes.put(mimetype, details);
                            }
                            
                            logger.debug("Completed processing of mimetypes information");
                        }
                        else
                        {
                           throw new AlfrescoRuntimeException("Unable to retrieve mimetypes information from Alfresco: " + response.getStatus().getCode());
                        }
                    }
                }
                catch (ConnectorServiceException cerr)
                {
                    throw new AlfrescoRuntimeException("Unable to retrieve mimetypes information from Alfresco: " + cerr.getMessage());
                }
                catch (Exception err)
                {
                    throw new AlfrescoRuntimeException("Failed processing dictionary information from Alfresco: " + err.getMessage());
                }
                finally
                {
                    this.mimetypesLock.readLock().lock();
                    this.mimetypesLock.writeLock().unlock();
                }
            }
        }
        finally
        {
            this.mimetypesLock.readLock().unlock();
        }
        return mimetypes;
    }

    /**
     * Holds the information returned on a mimetype, from the repository
     *  mimetypes information webscript
     */
    private static class Mimetype
    {
        private final String mimetype;
        private final String description;
        private final String defaultExtension;
        private final List<String> additionalExtensions;
        
        private Mimetype(String mimetype, JSONObject json) throws JSONException
        {
            this.mimetype = mimetype;
            this.description = json.getString("description");
            
            JSONObject ext = json.getJSONObject("extensions");
            defaultExtension = ext.getString("default");
            
            JSONArray additional = ext.getJSONArray("additional");
            additionalExtensions = new ArrayList<String>(additional.length());
            for (int i=0; i<additional.length(); i++)
            {
                additionalExtensions.add(additional.getString(i));
            }
        }

        public String getMimetype()
        {
            return mimetype;
        }

        public String getDescription()
        {
            return description;
        }

        public String getDefaultExtension()
        {
            return defaultExtension;
        }

        public List<String> getAdditionalExtensions()
        {
            return additionalExtensions;
        }
    }
}
