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
package org.alfresco.web.site;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpSession;

import org.alfresco.web.site.servlet.MTAuthenticationFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.config.ConfigBootstrap;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.WebFrameworkServiceException;
import org.springframework.extensions.surf.mvc.AbstractWebFrameworkInterceptor;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;

/**
 * Framework interceptor responsible for checking repository license edition
 * and applying appropriate config overrides.
 * 
 * @author Kevin Roast
 */
public class EditionInterceptor extends AbstractWebFrameworkInterceptor
{
    private static Log logger = LogFactory.getLog(EditionInterceptor.class);
    
    private static EditionInfo EDITIONINFO = null;
    private static final ReadWriteLock editionLock = new ReentrantReadWriteLock();
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#preHandle(org.springframework.web.context.request.WebRequest)
     */
    @Override
    public void preHandle(WebRequest request) throws Exception
    {
        editionLock.readLock().lock();
        try
        {
            if (EDITIONINFO == null)
            {
                editionLock.readLock().unlock();
                editionLock.writeLock().lock();
                try
                {
                    // check again, as more than one thread could have been waiting on the Write lock 
                    if (EDITIONINFO == null)
                    {
                        // initiate a call to retrieve the edition and restrictions from the repository
                        RequestContext rc = ThreadLocalRequestContext.getRequestContext();
                        Connector conn = rc.getServiceRegistry().getConnectorService().getConnector("alfresco");
                        Response response = conn.call("/api/admin/restrictions?guest=true");
                        if (response.getStatus().getCode() == Status.STATUS_UNAUTHORIZED)
                        {
                            // if this occurs we may be running a multi-tennant repository
                            if (MTAuthenticationFilter.getCurrentServletRequest() != null)
                            {
                                HttpSession session = MTAuthenticationFilter.getCurrentServletRequest().getSession(false);
                                if (session != null)
                                {
                                    // we try now that a Session is aquired and we have an authenticated user
                                    // this is the only time that we can successfully retrieve the license information
                                    // when the repo is in multi-tennant mode - as guest auth is not supported otherwise
                                    conn = rc.getServiceRegistry().getConnectorService().getConnector(
                                            "alfresco", (String)session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID), session);
                                    response = conn.call("/api/admin/restrictions");
                                }
                            }
                        }
                        if (response.getStatus().getCode() == Status.STATUS_OK)
                        {
                            logger.info("Successfully retrieved license information from Alfresco.");
                            
                            EDITIONINFO = new EditionInfo(response.getResponse());
                            
                            // apply runtime config overrides based on the repository edition
                            String runtimeConfig = null;
                            if ("TEAM".equals(EDITIONINFO.getEdition()))
                            {
                                runtimeConfig = "classpath:alfresco/team-config.xml";
                            }
                            else if ("ENTERPRISE".equals(EDITIONINFO.getEdition()))
                            {
                                runtimeConfig = "classpath:alfresco/enterprise-config.xml";
                            }
                            if (runtimeConfig != null)
                            {
                                // manually instantiate a ConfigBootstrap object that will
                                // register our override config with the main config source
                                List<String> configs = new ArrayList<String>(1);
                                configs.add(runtimeConfig);
                                
                                ConfigService configservice = rc.getServiceRegistry().getConfigService();
                                ConfigBootstrap cb = new ConfigBootstrap();
                                cb.setBeanName("share-edition-config");
                                cb.setConfigService(configservice);
                                cb.setConfigs(configs);
                                cb.register();
                                configservice.reset();
                            }
                        }
                        else
                        {
                            logger.info("Unable to retrieve License information from Alfresco: " + response.getStatus().getCode());
                            // set a value so scripts have something to work with - the interceptor will retry later
                            ThreadLocalRequestContext.getRequestContext().setValue("editionInfo", new EditionInfo());
                        }
                    }
                }
                catch (JSONException err)
                {
                    throw new WebFrameworkServiceException("Unable to process response: " + err.getMessage(), err);
                }
                finally
                {
                    editionLock.readLock().lock();
                    editionLock.writeLock().unlock();
                }
            }
            if (EDITIONINFO != null)
            {
                ThreadLocalRequestContext.getRequestContext().setValue("editionInfo", EDITIONINFO);
            }
        }
        finally
        {
            editionLock.readLock().unlock();
        }
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#postHandle(org.springframework.web.context.request.WebRequest, org.springframework.ui.ModelMap)
     */
    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception
    {
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#afterCompletion(org.springframework.web.context.request.WebRequest, java.lang.Exception)
     */
    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception
    {
    }
    
    
    /**
     * Simple structure class wrapping the edition and license restriction information
     */
    public static class EditionInfo implements Serializable
    {
        private final long users;
        private final long documents;
        private final String edition;
        
        EditionInfo()
        {
            this.users = -1L;
            this.documents = -1L;
            this.edition = "UNKNOWN";
        }
        
        EditionInfo(String response) throws JSONException
        {
            JSONObject json = new JSONObject(response);
            this.users = json.optLong("users", -1L);
            this.documents = json.optLong("documents", -1L);
            this.edition = json.getString("licenseMode");
        }
        
        public long getUsers()
        {
            return this.users;
        }
        
        public long getDocuments()
        {
            return this.documents;
        }
        
        public String getEdition()
        {
            return this.edition;
        }

        @Override
        public String toString()
        {
            return "Users: " + this.users + "  Documents: " + this.documents + "  Edition: " + this.edition;
        }
    }
}