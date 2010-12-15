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
package org.alfresco.cmis.test.ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.alfresco.repo.cmis.ws.ACLServiceLocator;
import org.alfresco.repo.cmis.ws.ACLServicePortBindingStub;
import org.alfresco.repo.cmis.ws.DiscoveryServiceLocator;
import org.alfresco.repo.cmis.ws.DiscoveryServicePortBindingStub;
import org.alfresco.repo.cmis.ws.MultiFilingServiceLocator;
import org.alfresco.repo.cmis.ws.MultiFilingServicePortBindingStub;
import org.alfresco.repo.cmis.ws.NavigationServiceLocator;
import org.alfresco.repo.cmis.ws.NavigationServicePortBindingStub;
import org.alfresco.repo.cmis.ws.ObjectServiceLocator;
import org.alfresco.repo.cmis.ws.ObjectServicePortBindingStub;
import org.alfresco.repo.cmis.ws.PolicyServiceLocator;
import org.alfresco.repo.cmis.ws.PolicyServicePortBindingStub;
import org.alfresco.repo.cmis.ws.RelationshipServiceLocator;
import org.alfresco.repo.cmis.ws.RelationshipServicePortBindingStub;
import org.alfresco.repo.cmis.ws.RepositoryServiceLocator;
import org.alfresco.repo.cmis.ws.RepositoryServicePortBindingStub;
import org.alfresco.repo.cmis.ws.VersioningServiceLocator;
import org.alfresco.repo.cmis.ws.VersioningServicePortBindingStub;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.ws.axis.security.WSDoAllSender;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.RequestData;

/**
 * This factory controls CMIS services callers creation and configuration. Also it introduce some cache functionality to improve performance and clarity
 * 
 * @author Dmitry Velichkevich
 */
public class CmisServicesFactory
{
    private static final int TIMEOUT = 60000;
    private static final String DELIMITER = "|";

    private AbstractServiceClient repositoryServiceDescriptor;
    private AbstractServiceClient discoveryServiceDescriptor;
    private AbstractServiceClient objectServiceDescriptor;
    private AbstractServiceClient navigationServiceDescriptor;
    private AbstractServiceClient multiFilingServiceDescriptor;
    private AbstractServiceClient versioningServiceDescriptor;
    private AbstractServiceClient relationshipServiceDescriptor;
    private AbstractServiceClient aclServiceDescriptor;
    private AbstractServiceClient policyServiceDescriptor;

    private Map<String, Stub> servicesCache = new HashMap<String, Stub>();

    public CmisServicesFactory()
    {
    }

    public AbstractServiceClient getRepositoryServiceDescriptor()
    {
        return repositoryServiceDescriptor;
    }

    public void setRepositoryServiceDescriptor(AbstractServiceClient repositoryServiceDescriptor)
    {
        this.repositoryServiceDescriptor = repositoryServiceDescriptor;
    }

    public AbstractServiceClient getNavigationServiceDescriptor()
    {
        return navigationServiceDescriptor;
    }

    public void setNavigationServiceDescriptor(AbstractServiceClient navigationServiceDescriptor)
    {
        this.navigationServiceDescriptor = navigationServiceDescriptor;
    }

    public AbstractServiceClient getObjectServiceDescriptor()
    {
        return objectServiceDescriptor;
    }

    public void setObjectServiceDescriptor(AbstractServiceClient objectServiceDescriptor)
    {
        this.objectServiceDescriptor = objectServiceDescriptor;
    }

    public AbstractServiceClient getMultiFilingServiceDescriptor()
    {
        return multiFilingServiceDescriptor;
    }

    public void setMultiFilingServiceDescriptor(AbstractServiceClient multiFilingServiceDescriptor)
    {
        this.multiFilingServiceDescriptor = multiFilingServiceDescriptor;
    }

    public AbstractServiceClient getVersioningServiceDescriptor()
    {
        return versioningServiceDescriptor;
    }

    public void setVersioningServiceDescriptor(AbstractServiceClient versioningServiceDescriptor)
    {
        this.versioningServiceDescriptor = versioningServiceDescriptor;
    }

    public AbstractServiceClient getRelationshipServiceDescriptor()
    {
        return relationshipServiceDescriptor;
    }

    public void setRelationshipServiceDescriptor(AbstractServiceClient relationshipServiceDescriptor)
    {
        this.relationshipServiceDescriptor = relationshipServiceDescriptor;
    }

    public AbstractServiceClient getDiscoveryServiceDescriptor()
    {
        return discoveryServiceDescriptor;
    }

    public void setDiscoveryServiceDescriptor(AbstractServiceClient discoveryServiceDescriptor)
    {
        this.discoveryServiceDescriptor = discoveryServiceDescriptor;
    }

    public void setAclServiceDescriptor(AbstractServiceClient aclServiceDescriptor)
    {
        this.aclServiceDescriptor = aclServiceDescriptor;
    }

    public void setPolicyServiceDescriptor(AbstractServiceClient policyServiceDescriptor)
    {
        this.policyServiceDescriptor = policyServiceDescriptor;
    }

    /**
     * Gets port for Repository Service with the default configured access URL
     * 
     * @return - RepositoryServicePortBindingStub
     * @throws ServiceException
     */
    public RepositoryServicePortBindingStub getRepositoryService() throws ServiceException
    {
        return getRepositoryService(repositoryServiceDescriptor.getServerUrl() + repositoryServiceDescriptor.getService().getPath(), null, null);
    }

    public RepositoryServicePortBindingStub getRepositoryService(String address) throws ServiceException
    {
        return getRepositoryService(address, null, null);
    }

    public RepositoryServicePortBindingStub getRepositoryService(String username, String password) throws ServiceException
    {
        return getRepositoryService(null, username, password);
    }

    /**
     * Gets port for Repository Service
     * 
     * @param address - address where service resides
     * @return - RepositoryServicePortBindingStub
     * @throws ServiceException
     */
    public RepositoryServicePortBindingStub getRepositoryService(String address, String username, String password) throws ServiceException
    {
        if (null == address)
        {
            address = repositoryServiceDescriptor.getServerUrl() + repositoryServiceDescriptor.getService().getPath();
        }
        if ((null == username) || (null == password))
        {
            username = repositoryServiceDescriptor.getUsername();
            password = repositoryServiceDescriptor.getPassword();
        }
        String uniqueIdentifier = getUniqueIdentifier(address, username, password);
        RepositoryServicePortBindingStub result = (RepositoryServicePortBindingStub) servicesCache.get(uniqueIdentifier);
        if (result == null)
        {
            RepositoryServiceLocator locator = new RepositoryServiceLocator(getEngineConfiguration(username, password));
            locator.setRepositoryServicePortEndpointAddress(address);
            result = (RepositoryServicePortBindingStub) locator.getRepositoryServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);
            servicesCache.put(uniqueIdentifier, result);
        }
        return result;
    }

    /**
     * Gets port for Discovery Service with the default configured access URL
     * 
     * @return - DiscoveryServicePortBindingStub
     * @throws ServiceException
     */
    public DiscoveryServicePortBindingStub getDiscoveryService() throws ServiceException
    {
        return getDiscoveryService(discoveryServiceDescriptor.getServerUrl() + discoveryServiceDescriptor.getService().getPath(), null, null);
    }

    public DiscoveryServicePortBindingStub getDiscoveryService(String address) throws ServiceException
    {
        return getDiscoveryService(address, null, null);
    }

    public DiscoveryServicePortBindingStub getDiscoveryService(String username, String password) throws ServiceException
    {
        return getDiscoveryService(null, username, password);
    }

    /**
     * Gets port for Discovery Service
     * 
     * @param address - address where service resides
     * @return DiscoveryServicePortBindingStub
     * @throws ServiceException
     */
    public DiscoveryServicePortBindingStub getDiscoveryService(String address, String username, String password) throws ServiceException
    {
        if (null == address)
        {
            address = discoveryServiceDescriptor.getServerUrl() + discoveryServiceDescriptor.getService().getPath();
        }
        if ((null == username) || (null == password))
        {
            username = discoveryServiceDescriptor.getUsername();
            password = discoveryServiceDescriptor.getPassword();
        }
        String uniqueIdentifier = getUniqueIdentifier(address, username, password);
        DiscoveryServicePortBindingStub result = (DiscoveryServicePortBindingStub) servicesCache.get(uniqueIdentifier);
        if (null == result)
        {
            DiscoveryServiceLocator locator = new DiscoveryServiceLocator(getEngineConfiguration(username, password));
            locator.setDiscoveryServicePortEndpointAddress(address);
            result = (DiscoveryServicePortBindingStub) locator.getDiscoveryServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);
            servicesCache.put(uniqueIdentifier, result);
        }
        return result;
    }

    /**
     * Gets port for Object Service with the default configured access URL
     * 
     * @return - ObjectServicePortBindingStub
     * @throws ServiceException
     */
    public ObjectServicePortBindingStub getObjectService() throws ServiceException
    {
        return getObjectService(objectServiceDescriptor.getServerUrl() + objectServiceDescriptor.getService().getPath(), null, null);
    }

    public ObjectServicePortBindingStub getObjectService(String address) throws ServiceException
    {
        return getObjectService(address, null, null);
    }

    public ObjectServicePortBindingStub getObjectService(String username, String password) throws ServiceException
    {
        return getObjectService(null, username, password);
    }

    /**
     * Gets port for Object Service
     * 
     * @param address - address where service resides
     * @return - ObjectServicePortBindingStub
     * @throws ServiceException
     */
    public ObjectServicePortBindingStub getObjectService(String address, String username, String password) throws ServiceException
    {
        if (null == address)
        {
            address = objectServiceDescriptor.getServerUrl() + objectServiceDescriptor.getService().getPath();
        }
        if ((null == username) || (null == password))
        {
            username = objectServiceDescriptor.getUsername();
            password = objectServiceDescriptor.getPassword();
        }
        String uniqueIdentifier = getUniqueIdentifier(address, username, password);
        ObjectServicePortBindingStub result = (ObjectServicePortBindingStub) servicesCache.get(uniqueIdentifier);
        if (null == result)
        {
            ObjectServiceLocator locator = new ObjectServiceLocator(getEngineConfiguration(username, password));
            locator.setObjectServicePortEndpointAddress(address);
            result = (ObjectServicePortBindingStub) locator.getObjectServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);
            servicesCache.put(uniqueIdentifier, result);
        }
        return result;
    }

    /**
     * Gets port for Navigation Service with the default configured access URL
     * 
     * @return - NavigationServicePortBindingStub
     * @throws ServiceException
     */
    public NavigationServicePortBindingStub getNavigationService() throws ServiceException
    {
        return getNavigationService(navigationServiceDescriptor.getServerUrl() + navigationServiceDescriptor.getService().getPath(), null, null);
    }

    public NavigationServicePortBindingStub getNavigationService(String address) throws ServiceException
    {
        return getNavigationService(address, null, null);
    }

    public NavigationServicePortBindingStub getNavigationService(String username, String password) throws ServiceException
    {
        return getNavigationService(null, username, password);
    }

    /**
     * Gets port for Navigation Service
     * 
     * @param address - address where service resides
     * @return - RepositoryServicePortBindingStub
     * @throws ServiceException
     */
    public NavigationServicePortBindingStub getNavigationService(String address, String username, String password) throws ServiceException
    {
        if (null == address)
        {
            address = navigationServiceDescriptor.getServerUrl() + navigationServiceDescriptor.getService().getPath();
        }
        if ((null == username) || (null == password))
        {
            username = navigationServiceDescriptor.getUsername();
            password = navigationServiceDescriptor.getPassword();
        }
        String uniqueIdentifier = getUniqueIdentifier(address, username, password);
        NavigationServicePortBindingStub result = (NavigationServicePortBindingStub) servicesCache.get(uniqueIdentifier);
        if (null == result)
        {
            NavigationServiceLocator locator = new NavigationServiceLocator(getEngineConfiguration(username, password));
            locator.setNavigationServicePortEndpointAddress(address);
            result = (NavigationServicePortBindingStub) locator.getNavigationServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);
            servicesCache.put(uniqueIdentifier, result);
        }
        return result;
    }

    /**
     * Gets port for MultiFiling Service with the default configured access URL
     * 
     * @return - MultiFilingServicePortBindingStub
     * @throws ServiceException
     */
    public MultiFilingServicePortBindingStub getMultiFilingServicePort() throws ServiceException
    {
        return getMultiFilingService(multiFilingServiceDescriptor.getServerUrl() + multiFilingServiceDescriptor.getService().getPath(), null, null);
    }

    public MultiFilingServicePortBindingStub getMultiFilingService(String address) throws ServiceException
    {
        return getMultiFilingService(address, null, null);
    }

    public MultiFilingServicePortBindingStub getMultiFilingService(String username, String password) throws ServiceException
    {
        return getMultiFilingService(null, username, password);
    }

    /**
     * Gets port for MultiFiling Service
     * 
     * @param address - address where service resides
     * @return - MultiFilingServicePortBindingStub
     * @throws ServiceException
     */
    public MultiFilingServicePortBindingStub getMultiFilingService(String address, String username, String password) throws ServiceException
    {
        if (null == address)
        {
            address = multiFilingServiceDescriptor.getServerUrl() + multiFilingServiceDescriptor.getService().getPath();
        }
        if ((null == username) || (null == password))
        {
            username = multiFilingServiceDescriptor.getUsername();
            password = multiFilingServiceDescriptor.getPassword();
        }
        String uniqueIdentifier = getUniqueIdentifier(address, username, password);
        MultiFilingServicePortBindingStub result = (MultiFilingServicePortBindingStub) servicesCache.get(uniqueIdentifier);
        if (null == result)
        {
            MultiFilingServiceLocator locator = new MultiFilingServiceLocator(getEngineConfiguration(username, password));
            locator.setMultiFilingServicePortEndpointAddress(address);
            result = (MultiFilingServicePortBindingStub) locator.getMultiFilingServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);
            servicesCache.put(uniqueIdentifier, result);
        }
        return result;
    }

    /**
     * Gets port for Versioning Service with the default configured access URL
     * 
     * @return - VersioningServicePortBindingStub
     * @throws ServiceException
     */
    public VersioningServicePortBindingStub getVersioningService() throws ServiceException
    {
        return getVersioningService(versioningServiceDescriptor.getServerUrl() + versioningServiceDescriptor.getService().getPath(), null, null);
    }

    public VersioningServicePortBindingStub getVersioningService(String address) throws ServiceException
    {
        return getVersioningService(address, null, null);
    }

    public VersioningServicePortBindingStub getVersioningService(String username, String password) throws ServiceException
    {
        return getVersioningService(null, username, password);
    }

    /**
     * Gets port for Versioning Service
     * 
     * @param address - address where service resides
     * @return - VersioningServicePortBindingStub
     * @throws ServiceException
     */
    public VersioningServicePortBindingStub getVersioningService(String address, String username, String password) throws ServiceException
    {
        if (null == address)
        {
            address = versioningServiceDescriptor.getServerUrl() + versioningServiceDescriptor.getService().getPath();
        }
        if ((null == username) || (null == password))
        {
            username = versioningServiceDescriptor.getUsername();
            password = versioningServiceDescriptor.getPassword();
        }
        String uniqueIdentifier = getUniqueIdentifier(address, username, password);
        VersioningServicePortBindingStub result = (VersioningServicePortBindingStub) servicesCache.get(uniqueIdentifier);
        if (null == result)
        {
            VersioningServiceLocator locator = new VersioningServiceLocator(getEngineConfiguration(username, password));
            locator.setVersioningServicePortEndpointAddress(address);
            result = (VersioningServicePortBindingStub) locator.getVersioningServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);
            servicesCache.put(uniqueIdentifier, result);
        }
        return result;
    }

    /**
     * Gets port for Relationship Service with the default configured access URL
     * 
     * @return - RelationshipServicePortBindingStub
     * @throws ServiceException
     */
    public RelationshipServicePortBindingStub getRelationshipService() throws ServiceException
    {
        return getRelationshipService(relationshipServiceDescriptor.getServerUrl() + relationshipServiceDescriptor.getService().getPath(), null, null);
    }

    public RelationshipServicePortBindingStub getRelationshipService(String address) throws ServiceException
    {
        return getRelationshipService(address, null, null);
    }

    public RelationshipServicePortBindingStub getRelationshipService(String username, String password) throws ServiceException
    {
        return getRelationshipService(null, username, password);
    }

    /**
     * Gets port for Relationship Service
     * 
     * @param address - address where service resides
     * @return - RelationshipServicePortBindingStub
     * @throws ServiceException
     */
    public RelationshipServicePortBindingStub getRelationshipService(String address, String username, String password) throws ServiceException
    {
        if (null == address)
        {
            address = relationshipServiceDescriptor.getServerUrl() + relationshipServiceDescriptor.getService().getPath();
        }
        if ((null == username) || (null == password))
        {
            username = relationshipServiceDescriptor.getUsername();
            password = relationshipServiceDescriptor.getPassword();
        }
        String uniqueIdentifier = getUniqueIdentifier(address, username, password);
        RelationshipServicePortBindingStub result = (RelationshipServicePortBindingStub) servicesCache.get(uniqueIdentifier);
        if (null == result)
        {
            RelationshipServiceLocator locator = new RelationshipServiceLocator(getEngineConfiguration(username, password));
            locator.setRelationshipServicePortEndpointAddress(address);
            result = (RelationshipServicePortBindingStub) locator.getRelationshipServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);
            servicesCache.put(uniqueIdentifier, result);
        }
        return result;
    }

    /**
     * Gets port for Relationship Service with the default configured access URL
     * 
     * @return - RelationshipServicePortBindingStub
     * @throws ServiceException
     */
    public ACLServicePortBindingStub getACLService() throws ServiceException
    {
        return getACLService(aclServiceDescriptor.getServerUrl() + aclServiceDescriptor.getService().getPath(), null, null);
    }

    public ACLServicePortBindingStub getACLService(String address) throws ServiceException
    {
        return getACLService(address, null, null);
    }

    public ACLServicePortBindingStub getACLService(String username, String password) throws ServiceException
    {
        return getACLService(null, username, password);
    }

    /**
     * Gets port for Relationship Service
     * 
     * @param address - address where service resides
     * @return - RelationshipServicePortBindingStub
     * @throws ServiceException
     */
    public ACLServicePortBindingStub getACLService(String address, String username, String password) throws ServiceException
    {
        if (null == address)
        {
            address = aclServiceDescriptor.getServerUrl() + aclServiceDescriptor.getService().getPath();
        }
        if ((null == username) || (null == password))
        {
            username = aclServiceDescriptor.getUsername();
            password = aclServiceDescriptor.getPassword();
        }
        String uniqueIdentifier = getUniqueIdentifier(address, username, password);
        ACLServicePortBindingStub result = (ACLServicePortBindingStub) servicesCache.get(uniqueIdentifier);
        if (null == result)
        {
            ACLServiceLocator locator = new ACLServiceLocator(getEngineConfiguration(username, password));
            locator.setACLServicePortEndpointAddress(address);
            result = (ACLServicePortBindingStub) locator.getACLServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);
            servicesCache.put(uniqueIdentifier, result);
        }
        return result;
    }

    /**
     * Gets port for Relationship Service with the default configured access URL
     * 
     * @return - RelationshipServicePortBindingStub
     * @throws ServiceException
     */
    public PolicyServicePortBindingStub getPolicyService() throws ServiceException
    {
        return getPolicyService(policyServiceDescriptor.getServerUrl() + policyServiceDescriptor.getService().getPath(), null, null);
    }

    public PolicyServicePortBindingStub getPolicyService(String address) throws ServiceException
    {
        return getPolicyService(address, null, null);
    }

    public PolicyServicePortBindingStub getPolicyService(String username, String password) throws ServiceException
    {
        return getPolicyService(null, username, password);
    }

    /**
     * Gets port for Relationship Service
     * 
     * @param address - address where service resides
     * @return - RelationshipServicePortBindingStub
     * @throws ServiceException
     */
    public PolicyServicePortBindingStub getPolicyService(String address, String username, String password) throws ServiceException
    {
        if (null == address)
        {
            address = policyServiceDescriptor.getServerUrl() + policyServiceDescriptor.getService().getPath();
        }
        if ((null == username) || (null == password))
        {
            username = policyServiceDescriptor.getUsername();
            password = policyServiceDescriptor.getPassword();
        }
        String uniqueIdentifier = getUniqueIdentifier(address, username, password);
        PolicyServicePortBindingStub result = (PolicyServicePortBindingStub) servicesCache.get(uniqueIdentifier);
        if (null == result)
        {
            PolicyServiceLocator locator = new PolicyServiceLocator(getEngineConfiguration(username, password));
            locator.setPolicyServicePortEndpointAddress(address);
            result = (PolicyServicePortBindingStub) locator.getPolicyServicePort();
            result.setMaintainSession(true);
            result.setTimeout(TIMEOUT);
            servicesCache.put(uniqueIdentifier, result);
        }
        return result;
    }

    public EngineConfiguration getEngineConfiguration(String username, String password)
    {
        final SimpleProvider engineConfiguration;
        WSDoAllSender wsDoAllSender = new WSDoAllSender()
        {
            private static final long serialVersionUID = 3313512765705136489L;

            @Override
            public WSPasswordCallback getPassword(String username, int doAction, String clsProp, String refProp, RequestData reqData) throws WSSecurityException
            {
                WSPasswordCallback passwordCallback = null;
                try
                {
                    passwordCallback = super.getPassword(username, doAction, clsProp, refProp, reqData);
                }
                catch (WSSecurityException e)
                {
                    passwordCallback = new WSPasswordCallback(username, WSPasswordCallback.USERNAME_TOKEN);
                    try
                    {
                        CallbackHandler callbackHandler = (CallbackHandler) getOption(refProp);
                        callbackHandler.handle(new Callback[] { passwordCallback });
                    }
                    catch (Exception e2)
                    {
                        throw new WSSecurityException("WSHandler: password callback failed", e);
                    }
                }
                return passwordCallback;
            }
        };

        wsDoAllSender.setOption(WSHandlerConstants.ACTION, WSConstants.USERNAME_TOKEN_LN + " " + WSConstants.TIMESTAMP_TOKEN_LN);
        wsDoAllSender.setOption(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        wsDoAllSender.setOption(WSHandlerConstants.PW_CALLBACK_REF, new PasswordCallBackHandler(password));
        wsDoAllSender.setOption(WSHandlerConstants.USER, username);

        engineConfiguration = new SimpleProvider();
        engineConfiguration.deployTransport(new QName("", "http"), new HTTPSender());
        engineConfiguration.setGlobalRequest(wsDoAllSender);
        return engineConfiguration;
    }

    public class PasswordCallBackHandler implements CallbackHandler
    {
        private String password;

        public PasswordCallBackHandler(String password)
        {
            super();
            this.password = password;
        }

        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
        {
            for (int i = 0; i < callbacks.length; i++)
            {
                if (callbacks[i] instanceof WSPasswordCallback)
                {
                    WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                    pc.setPassword(password);
                }
                else
                {
                    throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
                }
            }
        }
    }

    private String getUniqueIdentifier(String address, String username, String password)
    {
        return address + DELIMITER + username + DELIMITER + password;
    }

}
