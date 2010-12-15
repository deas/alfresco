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
using System;
using System.Net;
using System.Net.Security;
using System.ServiceModel;
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;
using System.Configuration;
using System.Reflection;
using WcfCmisWSTests.CmisServices;

///
/// author: Stas Sokolovsky
///
namespace WcfCmisWSTests
{
    public class CmisClientFactory
    {
        private RepositoryServicePortClient repositoryServiceClient = null;

        private NavigationServicePortClient navigationServiceClient = null;

        private ObjectServicePortClient objectServiceClient = null;

        private VersioningServicePortClient versioningServiceClient = null;

        private DiscoveryServicePortClient discoveryServiceClient = null;

        private MultiFilingServicePortClient multiFilingServiceClient = null;

        private RelationshipServicePortClient relationshipServiceClient = null;

        private ACLServicePortClient aclServiceClient = null;

        private PolicyServicePortClient policyServiceClient = null;

        private static Dictionary<string, CmisClientFactory> factoryCache = new Dictionary<string, CmisClientFactory>();

        private static string defaultUsername = "";

        private static string defaultPassword = "";

        private string username = defaultUsername;

        private string password = defaultPassword;

        private CmisClientFactory(string username, string password)
        {
            this.username = username;
            this.password = password;
        }

        static CmisClientFactory()
        {
            ServicePointManager.ServerCertificateValidationCallback = delegate(object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors sslPolicyErrors)
            {
                return true;
            };
            string username = ConfigurationSettings.AppSettings["credentials.username"];
            string password = ConfigurationSettings.AppSettings["credentials.password"];
            if (username != null && password != null)
            {
                defaultUsername = username;
                defaultPassword = password;
            }
        }

        public static CmisClientFactory getInstance()
        {
            return getInstance(defaultUsername, defaultPassword);
        }

        public static CmisClientFactory getInstance(string username, string password)
        {
            CmisClientFactory result = null;
            if (!factoryCache.TryGetValue(username + ":" + password, out result))
            {
                result = new CmisClientFactory(username, password);
                factoryCache.Add(username + ":" + password, result);
            }
            return (CmisClientFactory)result;
        }

        public ObjectServicePortClient getObjectServiceClient()
        {
            if (objectServiceClient == null)
            {
                objectServiceClient = new ObjectServicePortClient();
                setCredentials(objectServiceClient, username, password);
            }
            return objectServiceClient;
        }

        public NavigationServicePortClient getNavigationServiceClient()
        {
            if (navigationServiceClient == null)
            {
                navigationServiceClient = new NavigationServicePortClient();
                setCredentials(navigationServiceClient, username, password);
            }
            return navigationServiceClient;
        }

        public DiscoveryServicePortClient getDiscoveryServiceClient()
        {
            if (discoveryServiceClient == null)
            {
                discoveryServiceClient = new DiscoveryServicePortClient();
                setCredentials(discoveryServiceClient, username, password);
            }
            return discoveryServiceClient;
        }

        public MultiFilingServicePortClient getMultiFilingServiceClient()
        {
            if (multiFilingServiceClient == null)
            {
                multiFilingServiceClient = new MultiFilingServicePortClient();
                setCredentials(multiFilingServiceClient, username, password);
            }
            return multiFilingServiceClient;
        }

        public RepositoryServicePortClient getRepositoryServiceClient()
        {
            if (repositoryServiceClient == null)
            {
                repositoryServiceClient = new RepositoryServicePortClient();
                setCredentials(repositoryServiceClient, username, password);
            }
            return repositoryServiceClient;
        }

        public VersioningServicePortClient getVersioningServiceClient()
        {
            if (versioningServiceClient == null)
            {
                versioningServiceClient = new VersioningServicePortClient();
                setCredentials(versioningServiceClient, username, password);
            }
            return versioningServiceClient;
        }

        public RelationshipServicePortClient getRelationshipServiceClient()
        {
            if (relationshipServiceClient == null)
            {
                relationshipServiceClient = new RelationshipServicePortClient();
                setCredentials(relationshipServiceClient, username, password);
            }
            return relationshipServiceClient;
        }

        public ACLServicePortClient getACLServiceClient()
        {
            if (aclServiceClient == null)
            {
                aclServiceClient = new ACLServicePortClient();
                setCredentials(aclServiceClient, username, password);
            }
            return aclServiceClient;
        }

        public PolicyServicePortClient getPolicyServiceClient()
        {
            if (null == policyServiceClient)
            {
                policyServiceClient = new PolicyServicePortClient();
                setCredentials(policyServiceClient, username, password);
            }
            return policyServiceClient;
        }

        public string getCurrentUser()
        {
            return username;
        }

        private void setCredentials<T>(ClientBase<T> client, string username, string password) where T : class
        {
            if (username != null && !username.Equals(""))
            {
                client.ClientCredentials.UserName.UserName = username;
            }
            if (password != null && !password.Equals(""))
            {
                client.ClientCredentials.UserName.Password = password;
            }
        }

    }
}

