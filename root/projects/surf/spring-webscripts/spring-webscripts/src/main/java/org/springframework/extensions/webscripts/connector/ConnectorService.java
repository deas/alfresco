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

package org.springframework.extensions.webscripts.connector;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.AuthenticatorDescriptor;
import org.springframework.extensions.config.RemoteConfigElement.ConnectorDescriptor;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.config.RemoteConfigElement.IdentityType;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.CredentialVaultProviderException;
import org.springframework.extensions.surf.exception.WebScriptsPlatformException;
import org.springframework.extensions.surf.util.ReflectionHelper;

/**
 * The ConnectorService acts as a singleton that can be used to
 * build any of the objects utilized by the Connector layer.
 * <p>
 * This class is mounted as a Spring Bean within the
 * Web Script Framework so that developers can access it from the
 * application context.
 * 
 * @author muzquiano
 * @author Kevin Roast
 */
public class ConnectorService implements ApplicationContextAware
{
    private static final String PREFIX_CONNECTOR_SESSION = "_alfwsf_consession_";
    private static final String PREFIX_VAULT_SESSION     = "_alfwsf_vaults_";
    
    private static Log logger = LogFactory.getLog(ConnectorService.class);
    
    private ConfigService configService;
    private RemoteConfigElement remoteConfig;
    private ApplicationContext applicationContext;
    
    /** Lock to provide protection around Remote config lookup */
    private ReadWriteLock configLock = new ReentrantReadWriteLock();
    
    
    /**
     * Sets the config service.
     * 
     * @param configService the new config service
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /**
     * Sets the Spring application context
     * 
     * @param applicationContext    the Spring application context
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    /**
     * Gets the config service.
     * 
     * @return the config service
     */
    public ConfigService getConfigService()
    {
        return this.configService;
    }
    
    /**
     * Return the RemoteConfigElement instance
     * 
     * @return RemoteConfigElement
     */
    public RemoteConfigElement getRemoteConfig()
    {
        this.configLock.readLock().lock();
        try
        {
            if (this.remoteConfig == null)
            {
                this.configLock.readLock().unlock();
                this.configLock.writeLock().lock();
                try
                {
                    // check again as multiple threads could have been waiting on the write lock
                    if (this.remoteConfig == null)
                    {
                        // retrieve and cache the remote configuration block
                        this.remoteConfig = (RemoteConfigElement) getConfigService().getConfig("Remote").getConfigElement("remote");
                        if (this.remoteConfig == null)
                        {
                            throw new WebScriptsPlatformException("The 'Remote' configuration was not found.");
                        }
                    }
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
        return this.remoteConfig;
    }
    
    
    /////////////////////////////////////////////////////////////////
    // Connectors

    /**
     * Retrieves a Connector to a given endpoint.
     * <p>
     * This Connector has no given user context and will not pass any
     * authentication credentials. Therefore only endpoints that do not
     * require authentication or have "declared" authentication as part
     * of the endpoint config should be used.  
     * 
     * This Connector also will not manage connector session state.
     * 
     * Thus, it is generally less preferred to use these connectors
     * over those provided by getConnector(endpointId, session)
     *  
     * @param endpointId the endpoint id
     * 
     * @return the connector
     */
    public Connector getConnector(String endpointId)
        throws ConnectorServiceException
    {
        if (endpointId == null)
        {
            throw new IllegalArgumentException("EndpointId cannot be null.");
        }
        
        return getConnector(endpointId, (UserContext)null, (HttpSession)null);
    }
    
    /**
     * Retrieves a Connector to a given endpoint.
     * <p>
     * This Connector has no given user context and will not pass any
     * authentication credentials. Therefore only endpoints that do not
     * require authentication or have "declared" authentication as part
     * of the endpoint config should be used.
     *
     * Cookie and token state will be session bound and reusable on
     * subsequent invocations. 
     * 
     * @param endpointId the endpoint id
     * @param session the HTTP session
     * 
     * @return the connector
     */    
    public Connector getConnector(String endpointId, HttpSession session)
        throws ConnectorServiceException
    {
        if (endpointId == null)
        {
            throw new IllegalArgumentException("EndpointId cannot be null.");
        }
        
        return getConnector(endpointId, (String)null, session);
    }    
    
    /**
     * Retrieves a Connector for the given endpoint that is scoped
     * to the given user.
     * <p>
     * If the provided endpoint is configured to use an Authenticator,
     * then the Connector instance returned will be wrapped as an
     * AuthenticatingConnector.
     * <p>
     * Cookie and token state will be session bound and reusable on
     * subsequent invocations. 
     * 
     * @param endpointId    the endpoint id
     * @param userId        the user id (optional)
     * @param session       the session
     * 
     * @return the connector
     */
    public Connector getConnector(String endpointId, String userId, HttpSession session)
        throws ConnectorServiceException
    {
        if (endpointId == null)
        {
            throw new IllegalArgumentException("EndpointId cannot be null.");
        }
        if (session == null)
        {
            throw new IllegalArgumentException("HttpSession cannot be null.");
        }
        
        // retrieve credentials from the vault
        Credentials credentials = null;
        if (userId != null)
        {
            try
            {
                CredentialVault vault = (CredentialVault) this.getCredentialVault(session, userId);
                if (vault != null)
                {
                    credentials = vault.retrieve(endpointId);
                }
            }
            catch (CredentialVaultProviderException cvpe)
            {
                throw new ConnectorServiceException("Unable to acquire credential vault", cvpe);
            }
        }        
        
        // get connector session and build user context
        ConnectorSession connectorSession = this.getConnectorSession(session, endpointId);
        UserContext userContext = new UserContext(userId, credentials, connectorSession);
        
        return getConnector(endpointId, userContext, session);
    }

    /**
     * Retrieves a Connector for the given endpoint that is scoped
     * to the given user context.
     * <p>
     * A user context is a means of wrapping the Credentials and
     * ConnectorSession objects for a given user.  If they are provided,
     * then context will be drawn from them and stored back.
     * 
     * @param endpointId the endpoint id
     * @param userContext the user context
     * @param session the http session (optional, if present will persist connector session)
     * 
     * @return the connector
     * 
     * @throws ConnectorServiceException
     */
    public Connector getConnector(String endpointId, UserContext userContext, HttpSession session)
        throws ConnectorServiceException
    {
        if (endpointId == null)
        {
            throw new IllegalArgumentException("EndpointId cannot be null.");
        }
        
        // load the endpoint
        EndpointDescriptor endpointDescriptor = getRemoteConfig().getEndpointDescriptor(endpointId);
        if (endpointDescriptor == null)
        {
            throw new ConnectorServiceException(
                    "Unable to find endpoint definition for endpoint id: " + endpointId);
        }

        // load the connector
        String connectorId = (String)endpointDescriptor.getConnectorId();
        if (connectorId == null)
        {
            throw new ConnectorServiceException(
                    "The connector id property on the endpoint definition '" + endpointId + "' was empty");
        }
        ConnectorDescriptor connectorDescriptor = getRemoteConfig().getConnectorDescriptor(connectorId);
        if (connectorDescriptor == null)
        {
            throw new ConnectorServiceException(
                    "Unable to find connector definition for connector id: " + connectorId + " on endpoint id: " + endpointId);
        }
        
        // get the endpoint url
        String url = endpointDescriptor.getEndpointUrl();
        
        // build the connector
        Connector connector = buildConnector(connectorDescriptor, url);
        if (connector == null)
        {
            throw new ConnectorServiceException(
                    "Unable to construct Connector for class: " + connectorDescriptor.getImplementationClass() + ", connector id: " + connectorId);
        }
        
        // if an authenticator is configured for the connector, then we
        // will wrap the connector with an AuthenticatingConnector type
        // which will do a re-attempt if our credential fails
        String authId = connectorDescriptor.getAuthenticatorId();
        if (authId != null)
        {
            AuthenticatorDescriptor authDescriptor = getRemoteConfig().getAuthenticatorDescriptor(authId);
            if (authDescriptor == null)
            {
                throw new ConnectorServiceException(
                        "Unable to find authenticator definition for authenticator id: " + authId + " on connector id: " + connectorId);
            }
            String authClass = authDescriptor.getImplementationClass();
            Authenticator authenticator = buildAuthenticator(authClass);
            
            // wrap the connector
            connector = new AuthenticatingConnector(connector, authenticator);
        }
        
        // set credentials onto the connector
        // credentials are either "declared", "user", or "none":
        //  "declared" indicates that pre-set fixed declarative user credentials are to be used
        //  "user" indicates that the current user's credentials should be drawn from the vault and used
        //  "none" means that we don't include any credentials
        IdentityType identity = endpointDescriptor.getIdentity();
        switch (identity)
        {
            case DECLARED:
            {
                Credentials credentials = null;
                if (userContext != null && userContext.getCredentials() != null)
                {
                    // reuse previously vaulted credentials
                    credentials = userContext.getCredentials();
                }                
                if (credentials == null)
                {
                    // create new credentials for this declared user
                    String username = (String) endpointDescriptor.getUsername();
                    String password = (String) endpointDescriptor.getPassword();
                    
                    credentials = new CredentialsImpl(endpointId);
                    credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
                    credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, password);
                    
                    // store credentials in vault if we persisting against a user session
                    if (session != null)
                    {
                        try
                        {
                            CredentialVault vault = getCredentialVault(session, username);
                            if(vault != null)
                            {
                                vault.store(credentials);
                            }
                        }
                        catch(CredentialVaultProviderException cvpe)
                        {
                            throw new ConnectorServiceException("Unable to acquire credential vault", cvpe);
                        }
                    }
                }
                connector.setCredentials(credentials);
                
                break;
            }

            case USER:
            {
                Credentials credentials = null;
                
                if (userContext != null)
                {
                    if (userContext.getCredentials() != null)
                    {
                        // reuse previously vaulted credentials
                        credentials = userContext.getCredentials();
                    }
                    else if (endpointDescriptor.getExternalAuth() && userContext.getUserId() != null)
                    {
                        // Propagate the user ID if we are using external authentication
                        credentials = new CredentialsImpl(endpointId);
                        credentials.setProperty(Credentials.CREDENTIAL_USERNAME, userContext.getUserId());
                    }
                }
                
                if (credentials != null)
                {
                    connector.setCredentials(credentials);
                }
                else if (logger.isDebugEnabled())
                {
                    if (userContext != null)
                    {
                        logger.debug("Unable to find credentials for user: " + userContext.getUserId() + " and endpoint: " + endpointId);
                    }
                    else
                    {
                        logger.debug("Unable to find credentials for endpoint: " + endpointId);
                    }
                }
            }
        }
        
        // Establish Connector Session
        ConnectorSession connectorSession = null;
        if (userContext != null && userContext.getConnectorSession() != null)
        {
            // reuse previously session-bound connector session
            connectorSession = userContext.getConnectorSession();
        }
        if (connectorSession == null)
        {
            // create a new "temporary" connector session
            // this will not get bound back into the session
            connectorSession = new ConnectorSession(endpointId);
        }
        connector.setConnectorSession(connectorSession);
        
        return connector;
    }

    
    /////////////////////////////////////////////////////////////////
    // Authenticators
    
    /**
     * Returns the implementation of an Authenticator with a given id
     * 
     * @param id the id
     * 
     * @return the authenticator
     * 
     * @throws ConnectorServiceException
     */
    public Authenticator getAuthenticator(String id) throws ConnectorServiceException
    {
        if (id == null)
        {
            throw new IllegalArgumentException("Authenticator ID cannot be null.");
        }
        
        AuthenticatorDescriptor descriptor = getRemoteConfig().getAuthenticatorDescriptor(id);
        if (descriptor == null)
        {
            throw new ConnectorServiceException(
                    "Unable to find authenticator for id: " + id);
        }
        
        return buildAuthenticator(descriptor.getImplementationClass());
    }

    
    /////////////////////////////////////////////////////////////////
    // Connector Sessions

    /**
     * Returns the ConnectorSession bound to the current HttpSession for the given endpoint
     * 
     * @param session the session
     * @param endpointId the endpoint id
     * 
     * @return the connector session
     */
    public ConnectorSession getConnectorSession(HttpSession session, String endpointId)
    {
        if (session == null)
        {
            throw new IllegalArgumentException("HttpSession cannot be null.");
        }
        
        String key = getSessionEndpointKey(endpointId);
        ConnectorSession cs = (ConnectorSession)session.getAttribute(key);
        if (cs == null)
        {
            cs = new ConnectorSession(key);
            session.setAttribute(key, cs);
        }
        
        return cs;
    }

    /**
     * Removes the ConnectorSession from the HttpSession for the given endpoint 
     * 
     * @param session the session
     * @param endpointId the endpoint id
     */
    public void removeConnectorSession(HttpSession session, String endpointId)
    {
        if (session == null)
        {
            throw new IllegalArgumentException("HttpSession cannot be null.");
        }
        
        String key = getSessionEndpointKey(endpointId);
        session.removeAttribute(key);
    }
    
    
    /////////////////////////////////////////////////////////////////
    // CredentialVaults
    
    /**
     * Retrieves the user-scoped CredentialVault for the given user
     * 
     * If a vault doesn't yet exist, a vault of the default type
     * will be instantiated
     * 
     * @param session   HttpSession
     * @param userId    the user id
     * 
     * @return the credential vault
     * 
     * @throws CredentialVaultProviderException the credential vault provider exception
     */
    public CredentialVault getCredentialVault(HttpSession session, String userId) 
        throws CredentialVaultProviderException
    {
        return getCredentialVault(session, userId, null);
    }

    /**
     * Retrieves the user-scoped CredentialVault for the given user id
     * and given vault id
     * 
     * @param session   HttpSession
     * @param userId    the user id
     * @param vaultProviderId the vault provider id
     * 
     * @return the credential vault
     * 
     * @throws CredentialVaultProviderException the credential vault provider exception
     */
    public CredentialVault getCredentialVault(HttpSession session, String userId, String vaultProviderId)
        throws CredentialVaultProviderException
    {
        if (session == null)
        {
            throw new IllegalArgumentException("HttpSession cannot be null.");
        }
        
        if (userId == null)
        {
            throw new IllegalArgumentException("UserId is mandatory.");
        }
        
        if (vaultProviderId == null)
        {
        	vaultProviderId = this.getRemoteConfig().getDefaultCredentialVaultProviderId();
        }
        
        CredentialVaultProvider provider = (CredentialVaultProvider)applicationContext.getBean(vaultProviderId);
        if (provider == null)
        {
            throw new CredentialVaultProviderException("Unable to find credential vault provider: " + vaultProviderId); 
        }
        
        // session cache binding key
        String cacheKey = PREFIX_VAULT_SESSION + provider.generateKey(vaultProviderId, userId);
        
        // pull the credential vault from session
        CredentialVault vault = (CredentialVault)session.getAttribute(cacheKey);
        
        // if no existing vault, build a new one
        if (vault == null)
        {
            vault = (CredentialVault)provider.provide(userId);
            
            // load the vault
            vault.load();
            
            // place onto session
            session.setAttribute(cacheKey, vault);
        }
        
        return vault;
    }
    
    
    /**
     * Internal method for building an Authenticator.
     * 
     * @param className the class name
     * 
     * @return the authenticator
     */
    private Authenticator buildAuthenticator(String className)
        throws ConnectorServiceException
    {
        Authenticator auth = (Authenticator)ReflectionHelper.newObject(className);
        if (auth == null)
        {
            throw new ConnectorServiceException("Unable to instantiate Authenticator: " + className);
        }
        
        // Set the application context for the Authenticator object
        // TODO: Authenticators should be Spring beans, but due to legacy config they are constructed from class names
        if (auth instanceof ApplicationContextAware)
        {
            ((ApplicationContextAware)auth).setApplicationContext(applicationContext);
        }
        
        return auth;
    }

    /**
     * Internal method for building a Connector.
     * 
     * Connectors are not cached.  A new Connector will be constructed each time.
     * 
     * @param descriptor the descriptor
     * @param url the url
     * 
     * @return the connector
     */
    private Connector buildConnector(ConnectorDescriptor descriptor, String url)
    {
        Class[] argTypes = new Class[] { descriptor.getClass(), url.getClass() };
        Object[] args = new Object[] { descriptor, url };
        Connector conn = (Connector) ReflectionHelper.newObject( descriptor.getImplementationClass(), argTypes, args);
        
        // Set the application context for the Connector object
        // TODO: connectors should be Spring beans, but due to legacy config they are constructed from class names
        if (conn instanceof ApplicationContextAware)
        {
            ((ApplicationContextAware)conn).setApplicationContext(applicationContext);
        }
        
        return conn;
    }
    
    /**
     * Internal method for building a endpoint key for storage within the session
     * 
     * @param endpointId the endpoint id
     * 
     * @return the session endpoint key
     */
    private static String getSessionEndpointKey(String endpointId)
    {
        return PREFIX_CONNECTOR_SESSION + endpointId;        
    }
}