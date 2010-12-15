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
using WcfCmisWSTests.CmisServices;
using System.Collections.Generic;
using System.ServiceModel;
using WcfCmisWSTests.CmisServices;

namespace WcfCmisWSTests
{
    ///
    /// author: Dmitry Velichkevich
    ///
    public class RepositoryServiceClientTest : BaseServiceClientTest
    {
        private const string BASE_TYPE_DOCUMENT = "cmis:document";

        private const string BASE_TYPE_FOLDER = "cmis:folder";

        private const string BASE_TYPE_RELATIONSHIP = "cmis:relationship";

        public void testGetRepositories()
        {
            getAndAssertRepositoryId();
        }

        public void testGetRepositoryInfo()
        {
            cmisRepositoryInfoType repositoryInfo = getAndAssertRepositoryInfo();
            assertRepositoryInfoReponse(repositoryInfo, getAndAssertRepositoryId());
        }

        public void testGetTypeChildren()
        {
            assertGetTypeChildrenResponse(repositoryServiceClient, 100, 0);
        }

        public void testGetTypeChildrenPagination()
        {
            cmisTypeDefinitionType[] types = assertGetTypeChildrenResponse(repositoryServiceClient, 10, 0);
            if (types.Length > 1)
            {
                types = assertGetTypeChildrenResponse(repositoryServiceClient, 2, 0);
                logger.log("Getting type children for param maxItems=2");
                Assert.IsTrue(types.Length == 2, "number of returned objects(" + types.Length + ") is not equal to expected(2)");
                logger.log("Getting type children for params maxItems=1, skipCount=1");
                cmisTypeDefinitionType[] skippedTypes = assertGetTypeChildrenResponse(repositoryServiceClient, 1, 1);
                Assert.IsTrue(skippedTypes.Length == 1, "number of returned objects(" + types.Length + ") is not equal to expected(1)");
                Assert.AreEqual(types[1].id, skippedTypes[0].id, "returned type(" + skippedTypes[0].id + ") is not equal to expected(" + types[1].id + ")");
            }
            else
            {
                Assert.Skip("number of types is less than expected for test ( < 2)");
            }

        }

        public void testGetTypeDescedants()
        {
            cmisTypeContainer[] types = assertGetTypeDescendantsResponse(repositoryServiceClient, null, -1);
            assertBaseTypes(types);
        }

        public void testGetTypeDescedantsDepthLimited()
        {
            assertGetTypeDescendantsResponse(repositoryServiceClient, null, 2);
        }

        public void testGetTypeDescedantsSpecificType()
        {
            assertGetTypeDescendantsResponse(repositoryServiceClient, BASE_TYPE_DOCUMENT, -1);
        }

        public void testGetTypeDefinition()
        {
            RepositoryServicePortClient client = repositoryServiceClient;

            cmisTypeDefinitionType[] types = assertGetTypeChildrenResponse(client, 10, 0);
            string typeId = types[new Random().Next(types.Length)].id;

            Assert.IsNotNull(typeId, "typeId is null");
            Assert.IsTrue(typeId.Length > 1, "typeId is empty");
            logger.log("Getting TypeDefinition for typeId=" + typeId);
            logger.log("[RepositoryService->getTypeDefinition]");

            cmisTypeDefinitionType response = client.getTypeDefinition(getAndAssertRepositoryId(), typeId, null);
            Assert.IsNotNull(response.id, "cmisTypeDefinitionType->typeId is null");
            Assert.IsNotNull(response.Items, "cmisTypeDefinitionType->Items is null");
            Assert.IsTrue(response.Items.Length > 1, "response->Items is empty");
            logger.log("TypeDefinition was successfully received, count of PropertyDefinitionTypes=" + response.Items.Length);
        }

        public void testWrongRepositoryIdUsing()
        {
            logger.log("Testing wrong repositoryId");
            try
            {
                logger.log("[RepositoryService->getRepositoryInfo]");
                repositoryServiceClient.getRepositoryInfo(INVALID_REPOSITORY_ID, null);
                Assert.Fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.invalidArgument);
                logger.log("Expected error was returned");
            }
            try
            {
                logger.log("[RepositoryService->getTypeDescendants]");
                repositoryServiceClient.getTypeDescendants(INVALID_REPOSITORY_ID, null, "-1", false, null);
                Assert.Fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.invalidArgument);
                logger.log("Expected error was returned");
            }
            try
            {
                logger.log("[RepositoryService->getTypeDefinition]");
                repositoryServiceClient.getTypeDefinition(INVALID_REPOSITORY_ID, null, null);
                Assert.Fail("Repository with specified Id was not described with RepositoryService");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.invalidArgument);
                logger.log("Expected error was returned");
            }
            logger.log("Wrong repositoryId was successfully tested, all methods threw exceptions");
        }

        private cmisTypeDefinitionType[] assertGetTypeChildrenResponse(RepositoryServicePortClient client, int maxItems, int skipCount)
        {
            string repositoryId = getAndAssertRepositoryId();
            logger.log("Getting types for repositoryId=" + repositoryId);
            logger.log("[RepositoryService->getTypeChildren]");
            cmisTypeDefinitionListType types = client.getTypeChildren(repositoryId, BASE_TYPE_DOCUMENT, false, Convert.ToString(maxItems), Convert.ToString(skipCount), null);
            Assert.IsNotNull(types, "types response is null");
            Assert.IsNotNull(types.types, "types are undefined");
            logger.log("Types were recieved, number of types=" + types.types.Length);
            return types.types;
        }

        private cmisTypeContainer[] assertGetTypeDescendantsResponse(RepositoryServicePortClient client, string type, int depth)
        {
            string repositoryId = getAndAssertRepositoryId();
            logger.log("Getting type descedants for repositoryId=" + repositoryId);
            logger.log("[RepositoryService->getTypeDescedants]");
            cmisTypeContainer[] types = client.getTypeDescendants(repositoryId, type, Convert.ToString(depth), false, null);
            Assert.IsNotNull(types, "types response is null");
            Assert.IsTrue(types.Length >= 1, "types response is empty");
            foreach (cmisTypeContainer cmisType in types)
            {
                assertTypeContainer(cmisType, depth);
            }
            return types;
        }

        private void assertTypeContainer(cmisTypeContainer container, int depth)
        {
            Assert.IsNotNull(container, "container is null");
            Assert.IsNotNull(container.type, "container.type is null");
            if (depth == 0)
            {
                Assert.IsTrue((container.children == null || container.children.Length == 0), "descedants were returned for depth=" + (depth + 1) + ", but expected for depth=" + depth);
            }
            else if (container.children != null)
            {
                foreach (cmisTypeContainer childContainer in container.children)
                {
                    if (childContainer != null && childContainer.type != null)
                    {
                        Assert.AreEqual(container.type.id, childContainer.type.parentId, "child type='" + childContainer.type.id + "' has parent type = '" + childContainer.type.parentId + ", but expected '" + container.type.id + "'");
                    }
                    assertTypeContainer(childContainer, depth - 1);
                }
            }
        }

        private static void assertRepositoryInfoReponse(cmisRepositoryInfoType repositoryInfo, string repositoryId)
        {
            Assert.IsNotNull(repositoryInfo, "repositoryInfo is null");
            Assert.AreEqual(repositoryId, repositoryInfo.repositoryId, "repositoryId is not equal to repositoryId from Repository Info request");
            Assert.IsNotNull(repositoryInfo.cmisVersionSupported, "Supported CMIS version is not specified");
            Assert.IsNotNull(repositoryInfo.cmisVersionSupported, "Unsupported CMIS implementation version");
            Assert.IsNotNull(repositoryInfo.productName, "Product Name property is undefined");
            Assert.IsNotNull(repositoryInfo.productVersion, "Product Version property is undefined");
            Assert.IsNotNull(repositoryInfo.repositoryDescription, "Repository Description property is undefined");
            Assert.IsNotNull(repositoryInfo.repositoryName, "Repository Name property is undefined");
            Assert.IsNotNull(repositoryInfo.rootFolderId, "Repository Root Folder Id is undefined");
            Assert.IsNotNull(repositoryInfo.vendorName, "Repository Vendor Name property is undefined");
            Assert.IsNotNull(repositoryInfo.aclCapability, "ACL Capability is undefined");
            Assert.IsNotNull(repositoryInfo.changesIncomplete, "Changes Incomplete is undefined");

            assertAndLogRepositoryCapabilities(repositoryInfo);
        }

        private static void assertAndLogRepositoryCapabilities(cmisRepositoryInfoType repositoryInfo)
        {
            cmisRepositoryCapabilitiesType capabilities = repositoryInfo.capabilities;
            Assert.IsNotNull(capabilities, "Repository Info Capabilities are null");
            Assert.IsNotNull(repositoryInfo.aclCapability, "Change Incomplete property is undefined");
            Assert.IsNotNull(repositoryInfo.changesIncomplete, "Change Incomplete property is undefined");
            Assert.IsNotNull(repositoryInfo.changesIncompleteSpecified, "Change Incomplete Specified property is undefined");
            Assert.IsNotNull(capabilities.capabilityACL, "ACL capability is undefined");
            Assert.IsNotNull(capabilities.capabilityChanges, "Changes capability is undefined");
            Assert.IsNotNull(capabilities.capabilityRenditions, "Renditions capability is undefined");
            Assert.IsNotNull(capabilities.capabilityAllVersionsSearchable, "All Versions Searchable capability is undefined");
            Assert.IsNotNull(capabilities.capabilityContentStreamUpdatability, "Content Stream Updatability capability is undefined");
            Assert.IsNotNull(capabilities.capabilityGetDescendants, "Get Descendants capability is undefined");
            Assert.IsNotNull(capabilities.capabilityGetFolderTree, "Get Folder Tree capability is undefined");
            Assert.IsNotNull(capabilities.capabilityJoin, "Join capability is undefined");
            Assert.IsNotNull(capabilities.capabilityMultifiling, "Multi filing capability is undefined");
            Assert.IsNotNull(capabilities.capabilityPWCSearchable, "PWC Searchable capability is undefined");
            Assert.IsNotNull(capabilities.capabilityPWCUpdatable, "PWC Updatable capability is undefined");
            Assert.IsNotNull(capabilities.capabilityQuery, "Query capability is undefined");
            Assert.IsNotNull(capabilities.capabilityUnfiling, "Unfiling capability is undefined");
            Assert.IsNotNull(capabilities.capabilityVersionSpecificFiling, "Version Specific Filing capability is undefined");

            logger.log("Repository capabilities:");
            logger.log("ACL capability:  " + capabilities.capabilityACL);
            logger.log("All Versions Searchable capability: " + capabilities.capabilityAllVersionsSearchable);
            logger.log("Changes capability: " + capabilities.capabilityChanges);
            logger.log("Content Stream Updatability capability: " + capabilities.capabilityContentStreamUpdatability);
            logger.log("Get Descendants capability: " + capabilities.capabilityGetDescendants);
            logger.log("Join capability: " + capabilities.capabilityJoin);
            logger.log("Multi filing capability: " + capabilities.capabilityMultifiling);
            logger.log("PWC Searchable capability: " + capabilities.capabilityPWCSearchable);
            logger.log("PWC Updatable capability: " + capabilities.capabilityPWCUpdatable);
            logger.log("Query capability: " + capabilities.capabilityQuery);
            logger.log("Renditions capability: " + capabilities.capabilityRenditions);
            logger.log("Unfiling capability: " + capabilities.capabilityUnfiling);
            logger.log("Version Specific Filing capability: " + capabilities.capabilityVersionSpecificFiling);
            logger.log("Get Folder Tree capability: " + capabilities.capabilityGetFolderTree);
        }

        private void assertBaseTypes(cmisTypeContainer[] containers)
        {
            HashSet<string> types = new HashSet<string>();
            foreach (cmisTypeContainer container in containers)
            {
                Assert.IsNotNull(container, "container is null");
                Assert.IsNotNull(container.type, "container.type is null");
                types.Add(container.type.id);
            }
            Assert.IsTrue(types.Contains(BASE_TYPE_DOCUMENT), "Base type '" + BASE_TYPE_DOCUMENT + "' is not found");
            Assert.IsTrue(types.Contains(BASE_TYPE_FOLDER), "Base type '" + BASE_TYPE_FOLDER + "' is not found");
            Assert.IsTrue(types.Contains(BASE_TYPE_RELATIONSHIP), "Base type '" + BASE_TYPE_RELATIONSHIP + "' is not found");
        }
    }
}
