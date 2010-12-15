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
using System.Text;
using System.Configuration;
using System.Reflection;
using System.Net.Security;
using System.ServiceModel;
using System.Collections.Generic;
using System.Security.Cryptography.X509Certificates;

using WcfCmisWSTests.CmisServices;

using System.Xml;

///
/// author: Dmitry Velichkevich
///
namespace WcfCmisWSTests
{
    public enum enumTypesOfFileableObjects
    {
        any,
        documents,
        folders,
        relationships
    }

    public class BaseServiceClientTest
    {
        public const string TEXTUAL_ZERO = "0";
        public const string ANY_PROPERTY_FILTER = "*";

        public const string NAME_PROPERTY = "cmis:name";
        public const string TYPE_ID_PROPERTY = "cmis:objectTypeId";
        public const string OBJECT_IDENTIFIER_PROPERTY = "cmis:objectId";
        public const string CHECKED_OUT_PROPERTY = "cmis:isVersionSeriesCheckedOut";
        public const string LATEST_VERSION_PROPERTY = "cmis:isLatestVersion";
        public const string MAJOR_VERSION_PROPERTY = "cmis:isMajorVersion";
        public const string RELATIONSHIP_SOURCE_OBJECT_ID = "cmis:sourceId";
        public const string RELATIONSHIP_TARGET_OBJECT_ID = "cmis:targetId";
        public const string ALLOWED_CHILDREN_TYPE_IDS = "cmis:allowedChildObjectTypeIds";
        public const string CONTENT_STREAM_NAME_PROPERTY = "cmis:contentStreamFileName";
        public const string PARENT_OBJECT_IDENTIFIER = "cmis:parentObjectId";
        public const string SOURCE_OBJECT_ID = "cmis:sourceId";
        public const string TARGET_OBJECT_ID = "cmis:targetId";
        public const string VERSION_SERIES_ID = "cmis:versionSeriesId";
        public const string PERMISSION_READ = "cmis:read";
        public const string PERMISSION_WRITE = "cmis:write";
        public const string PERMISSION_ALL = "cmis:all";


        public const string INVALID_OBJECT_ID = "Invalid Object Id";
        public const string INVALID_REPOSITORY_ID = "Wrong Repository Id";

        private const int ARRAY_BASED_STRUCTURE_BEING_INDEX = 0;
        private const int MAXIMUM_ODD_OBJECTS_AMOUNT = 5;
        private const int NEAR_ZERO_RANDOM_MAXIMUM = 3;

        private const string DELIMETER = "/";

        private const string CMIS_OBJECTS_DELETION_FAILED_MESSAGE_PATTERN = "Object with Id={1} was not deleted";

        private const string CHECKOUT_RESULT_AND_CONTENT_STATE_MESSAGE = "Actual content stream appearance and results of check outing service calling are different: ";
        private const string NAME_PROPERTIES_ARE_NOT_EQUAL = "Expected name property is not equal to actual name property";

        private static string baseDocumentTypeId = null;
        private static string baseFolderTypeId = null;
        private static string baseRelationshipTypeId = null;
        private static string basePolicyTypeId = null;

        private static string documentQueryName = null;
        private static System.Nullable<bool> contentStreamAllowed = null;
        private static System.Nullable<bool> contentStreamRequired = null;
        private static System.Nullable<bool> versioningAllowed = null;

        private static string repositoryId = null;
        private static string rootFolderId = null;
        private static string documentTypeId = null;
        private static string folderTypeId = null;
        private static string relationshipTypeId = null;
        private static string relationshipSourceTypeId = null;
        private static string relationshipTargetTypeId = null;
        private static string policyTypeId = null;
        private static string policyControllableTypeId = null;

        private static System.Nullable<bool> unfilingEnabled = null;
        private static System.Nullable<bool> multifilingEnabled = null;
        private static System.Nullable<bool> capabilityGetDescendants = null;
        private static System.Nullable<bool> pwcUpdatable = null;
        private static System.Nullable<bool> allVersionsSearchable = null;
        private static System.Nullable<bool> pwcSearchable = null;
        private static System.Nullable<bool> versionSpecificFiling = null;
        private static System.Nullable<bool> renditionsEnabled = null;
        private static System.Nullable<bool> relationshipsAllowed = null;
        private static System.Nullable<bool> policiesAllowed = null;

        private static System.Nullable<enumCapabilityContentStreamUpdates> contentStreamUpdates = null;
        private static System.Nullable<enumCapabilityJoin> capabilityJoin = null;
        private static System.Nullable<enumCapabilityQuery> queryAllowed = null;
        private static System.Nullable<enumCapabilityACL> capabilityACL = null;
        private static System.Nullable<enumACLPropagation> aclPropagation = null;
        private static System.Nullable<enumCapabilityChanges> capabilityChanges = null;

        protected static RepositoryServicePortClient repositoryServiceClient = CmisClientFactory.getInstance().getRepositoryServiceClient();
        protected static MultiFilingServicePortClient multifilingServiceClient = CmisClientFactory.getInstance().getMultiFilingServiceClient();
        protected static ObjectServicePortClient objectServiceClient = CmisClientFactory.getInstance().getObjectServiceClient();
        protected static VersioningServicePortClient versioningServiceClient = CmisClientFactory.getInstance().getVersioningServiceClient();
        protected static NavigationServicePortClient navigationServiceClient = CmisClientFactory.getInstance().getNavigationServiceClient();
        protected static DiscoveryServicePortClient discoveryServiceClient = CmisClientFactory.getInstance().getDiscoveryServiceClient();
        protected static RelationshipServicePortClient relationshipServiceClient = CmisClientFactory.getInstance().getRelationshipServiceClient();
        protected static ACLServicePortClient aclServiceClient = CmisClientFactory.getInstance().getACLServiceClient();
        protected static PolicyServicePortClient policyServiceClient = CmisClientFactory.getInstance().getPolicyServiceClient();

        protected static CmisLogger logger = CmisLogger.getInstance(2);

        protected const string CHANGED_NAME = "ChangedTestName";
        protected const string CHECKIN_COMMENT = "Checked In with CmisTest";
        protected const string CHECKIN_CONTENT_TEXT = "Check In test result entry";
        protected static byte[] checkinContentEntry = Encoding.GetEncoding(FileableObject.DEFAULT_ENCODING).GetBytes(CHECKIN_CONTENT_TEXT);

        private const string RENDITION_FILTER_NONE = "cmis:none";
        private const string IMAGE_BASE_MIMETYPE = "image/";
        private const string RENDITION_FILTER_DELIMITER = ",";
        private const string RENDITION_FILTER_WILDCARD = "*";
        private const string RENDITION_FILTER_SUBTYPES_POSTFIX = "/*";

        protected string aclUsername = ConfigurationSettings.AppSettings["credentials.acl.username"];
        protected string aclPassword = ConfigurationSettings.AppSettings["credentials.acl.password"];
        protected string aclPrincipalId = ConfigurationSettings.AppSettings["credentials.acl.principalId"];

        public virtual void initialize(string testName)
        {
            // Stub
        }

        public virtual void release(string testName)
        {
            // Stub
        }

        public static bool isValueNotSet(object checkingValue)
        {
            if (null == checkingValue)
            {
                return true;
            }

            Type valueType = checkingValue.GetType();

            if (checkingValue is string)
            {
                return string.Empty == (string)checkingValue;
            }
            else
            {
                if (checkingValue is Array)
                {
                    return (checkingValue as Array).Length < 1;
                }
                else
                {
                    if (null != valueType.GetMethod("GetEnumerator"))
                    {
                        System.Reflection.PropertyInfo countProperty = valueType.GetProperty("Count", typeof(int));
                        return ((null != countProperty) && countProperty.CanRead) ? ((int)countProperty.GetValue(checkingValue, null) < 1) : (true);
                    }
                }
            }

            return false;
        }

        public static string getAndAssertRepositoryId()
        {
            if (null == repositoryId)
            {
                logger.log("[RepositoryService->getRepositories]");
                logger.log("Getting Repositories");
                cmisRepositoryEntryType[] repositories = repositoryServiceClient.getRepositories(null);
                Assert.IsNotNull(repositories, "getRepositories() service method had returned \"undefined\" result");
                Assert.IsTrue((repositories.Length > 0), "Repositories list is empty");
                foreach (cmisRepositoryEntryType repository in repositories)
                {
                    assertRepositoryEntry(repository);
                }
                repositoryId = repositories[0].repositoryId;
            }
            return repositoryId;
        }

        private static void assertRepositoryEntry(cmisRepositoryEntryType repository)
        {
            Assert.IsNotNull(repository, "One of the Repository description entries has \"not set\" state");
            Assert.IsNotNull(repository.repositoryId, "Repository id is undefined");
            Assert.IsNotNull(repository.repositoryName, "Repository name is undefined");
        }

        public static cmisRepositoryInfoType getAndAssertRepositoryInfo()
        {
            string repositoryId = getAndAssertRepositoryId();
            logger.log("Getting RepositoryInfo");
            logger.log("[RepositoryService->getRepositoryInfo]");
            cmisRepositoryInfoType result = repositoryServiceClient.getRepositoryInfo(repositoryId, null);
            getRepositoryPropertiesAndCapabilities(result);
            logger.log("RepositoryInfo was successfully received");
            return result;
        }

        private static void getRepositoryPropertiesAndCapabilities(cmisRepositoryInfoType repositoryInfo)
        {
            Assert.IsNotNull((rootFolderId = repositoryInfo.rootFolderId), "Repository Root Folder Id is undefined");
            cmisRepositoryCapabilitiesType capabilities = repositoryInfo.capabilities;
            Assert.IsNotNull(capabilities, "Repository Info Capabilities are null");
            allVersionsSearchable = capabilities.capabilityAllVersionsSearchable;
            contentStreamUpdates = capabilities.capabilityContentStreamUpdatability;
            capabilityGetDescendants = capabilities.capabilityGetDescendants;
            capabilityJoin = capabilities.capabilityJoin;
            capabilityACL = capabilities.capabilityACL;
            multifilingEnabled = capabilities.capabilityMultifiling;
            pwcSearchable = capabilities.capabilityPWCSearchable;
            pwcUpdatable = capabilities.capabilityPWCUpdatable;
            queryAllowed = capabilities.capabilityQuery;
            unfilingEnabled = capabilities.capabilityUnfiling;
            versionSpecificFiling = capabilities.capabilityVersionSpecificFiling;
            capabilityChanges = capabilities.capabilityChanges;
            renditionsEnabled = enumCapabilityRendition.read.Equals(capabilities.capabilityRenditions);
            aclPropagation = repositoryInfo.aclCapability.propagation;
        }

        public bool isAllVersionsSearchable()
        {
            if (null == allVersionsSearchable)
            {
                getAndAssertRepositoryInfo();
            }
            return (bool)allVersionsSearchable;
        }

        public bool isCapabilityUnfilingEnabled()
        {
            if (unfilingEnabled == null)
            {
                getAndAssertRepositoryInfo();
            }
            return (bool)unfilingEnabled;
        }

        public bool isCapabilityMultifilingEnabled()
        {
            if (multifilingEnabled == null)
            {
                getAndAssertRepositoryInfo();
            }
            return (bool)multifilingEnabled;
        }

        public static bool isContentStreamAllowed()
        {
            if (contentStreamAllowed == null)
            {
                getAndAssertBaseTypesProperties();
            }
            return (bool)contentStreamAllowed;
        }

        public static bool isContentStreamRequired()
        {
            if (null == contentStreamRequired)
            {
                getAndAssertBaseTypesProperties();
            }
            return (bool)contentStreamRequired;
        }

        public static bool isVersioningAllowed()
        {
            if (versioningAllowed == null)
            {
                getAndAssertBaseTypesProperties();
            }
            return (bool)versioningAllowed;
        }

        public bool isPWCSearchable()
        {
            if (pwcSearchable == null)
            {
                getAndAssertRepositoryInfo();
            }
            return (bool)pwcSearchable;
        }

        public bool isPwcUpdatable()
        {
            if (null == pwcUpdatable)
            {
                getAndAssertRepositoryInfo();
            }
            return (bool)pwcUpdatable;
        }

        public bool isRenditionsEnabled()
        {
            if (null == renditionsEnabled)
            {
                getAndAssertRepositoryInfo();
            }
            return (bool)renditionsEnabled;
        }

        public static bool areRelationshipsAllowed()
        {
            if (null == relationshipsAllowed)
            {
                getAndAssertBaseTypesProperties();
            }
            return (null != relationshipsAllowed) ? ((bool)relationshipsAllowed) : (false);
        }

        public static bool arePoliciesAllowed()
        {
            if (null == policiesAllowed)
            {
                getAndAssertBaseTypesProperties();
            }
            return (null != policiesAllowed) ? ((bool)policiesAllowed) : (false);
        }

        public enumCapabilityACL getCapabilityACL()
        {
            if (capabilityACL == null)
            {
                getAndAssertRepositoryInfo();
            }
            return (enumCapabilityACL)capabilityACL;
        }

        public enumACLPropagation getACLPropagation()
        {
            if (aclPropagation == null)
            {
                getAndAssertRepositoryInfo();
            }
            return (enumACLPropagation)aclPropagation;
        }

        public enumCapabilityChanges getCapabilityChanges()
        {
            if (capabilityChanges == null)
            {
                getAndAssertRepositoryInfo();
            }
            return (enumCapabilityChanges)capabilityChanges;
        }

        public bool isVersionSpecificFiling()
        {
            if (null == versionSpecificFiling)
            {
                getAndAssertRepositoryInfo();
            }
            return (bool)versionSpecificFiling;
        }

        public enumCapabilityContentStreamUpdates getContentStreamUpdatesPolicy()
        {
            if (null == contentStreamUpdates)
            {
                getAndAssertRepositoryInfo();
            }
            return (enumCapabilityContentStreamUpdates)contentStreamUpdates;
        }

        public enumCapabilityJoin getJoinCapability()
        {
            if (null == capabilityJoin)
            {
                getAndAssertRepositoryInfo();
            }
            return (enumCapabilityJoin)capabilityJoin;
        }

        public enumCapabilityQuery getQueryAllowed()
        {
            if (queryAllowed == null)
            {
                getAndAssertRepositoryInfo();
            }
            return (enumCapabilityQuery)queryAllowed;
        }

        public static string getAndAssertDocumentTypeId()
        {
            if (documentTypeId == null)
            {
                getAndAssertBaseTypesProperties();
            }
            return documentTypeId;
        }

        public string getAndAssertFolderTypeId()
        {
            if (folderTypeId == null)
            {
                getAndAssertBaseTypesProperties();
            }
            return folderTypeId;
        }

        public string getAndAssertRelationshipTypeId()
        {
            if (relationshipTypeId == null)
            {
                getAndAssertBaseTypesProperties();
            }
            return relationshipTypeId;
        }

        public string getAndAssertRelationshipSourceTypeId()
        {
            if (relationshipSourceTypeId == null)
            {
                getAndAssertBaseTypesProperties();
            }
            return relationshipSourceTypeId;
        }

        public string getAndAssertRelationshipTargetTypeId()
        {
            if (relationshipTargetTypeId == null)
            {
                getAndAssertBaseTypesProperties();
            }
            return relationshipTargetTypeId;
        }

        public string getAndAssertPolicyTypeId()
        {
            if (null == policyTypeId)
            {
                getAndAssertBaseTypesProperties();
            }
            return policyTypeId;
        }

        public string getAndAssertPolicyControllableTypeId()
        {
            if (null == policyControllableTypeId)
            {
                getAndAssertBaseTypesProperties();
            }
            return policyControllableTypeId;
        }

        public string getAndAssertDocumentQueryName()
        {
            if (documentQueryName == null)
            {
                getAndAssertBaseTypesProperties();
            }
            return documentQueryName;
        }

        public static string getAndAssertRootFolder()
        {
            if (rootFolderId == null)
            {
                getAndAssertRepositoryInfo();
            }

            return rootFolderId;
        }

        public static string getAndAssertBaseFolderTypeId()
        {
            if (null == baseFolderTypeId)
            {
                getAndAssertBaseTypesProperties();
            }
            return baseFolderTypeId;
        }

        public static string getAndAssertBaseDocumentTypeId()
        {
            if (null == baseDocumentTypeId)
            {
                getAndAssertBaseTypesProperties();
            }
            return baseDocumentTypeId;
        }

        public static string getAndAssertBaseRelationshipTypeId()
        {
            if (null == baseRelationshipTypeId)
            {
                getAndAssertBaseTypesProperties();
            }
            return baseRelationshipTypeId;
        }

        private static void getAndAssertBaseTypesProperties()
        {
            if (null == baseDocumentTypeId)
            {
                cmisTypeDefinitionType[] baseTypeDefinitions = getAndAssertTypeChildren(null, true, 0, 0);
                foreach (cmisTypeDefinitionType typeDef in baseTypeDefinitions)
                {
                    baseDocumentTypeId = assertBaseTypeSearch(baseDocumentTypeId, typeDef, typeof(cmisTypeDocumentDefinitionType), "Document");
                    baseFolderTypeId = assertBaseTypeSearch(baseFolderTypeId, typeDef, typeof(cmisTypeFolderDefinitionType), "Folder");
                    baseRelationshipTypeId = assertBaseTypeSearch(baseRelationshipTypeId, typeDef, typeof(cmisTypeRelationshipDefinitionType), "Relationship");
                    basePolicyTypeId = assertBaseTypeSearch(basePolicyTypeId, typeDef, typeof(cmisTypePolicyDefinitionType), "Policy");
                }

                Assert.IsNotNull(baseDocumentTypeId, "Base Document Type Id was not found");
                Assert.IsNotNull(baseFolderTypeId, "Base Folder Type Id was not found");
                policiesAllowed = null != basePolicyTypeId;
                relationshipsAllowed = null != baseRelationshipTypeId;
            }

            if (null == documentTypeId)
            {
                documentTypeId = enumerateAndAssertTypesForAction(getAndAssertTypeDescendants(baseDocumentTypeId, -1, true), new BestDocumentSearchAction(), false);

                if (documentTypeId == null)
                {
                    documentTypeId = baseDocumentTypeId;
                }

                cmisTypeDocumentDefinitionType typeDefinition = (cmisTypeDocumentDefinitionType)getAndAssertTypeDefinition(documentTypeId);
                contentStreamAllowed = enumContentStreamAllowed.allowed.Equals(typeDefinition.contentStreamAllowed)
                    || enumContentStreamAllowed.required.Equals(typeDefinition.contentStreamAllowed);
                contentStreamRequired = enumContentStreamAllowed.required.Equals(typeDefinition.contentStreamAllowed);
                versioningAllowed = typeDefinition.versionable;
                documentQueryName = typeDefinition.queryName;
                policyControllableTypeId = (typeDefinition.controllablePolicy) ? (documentTypeId) : (null);
            }

            if (null == folderTypeId)
            {
                folderTypeId = enumerateAndAssertTypesForAction(getAndAssertTypeDescendants(baseFolderTypeId, -1, true), new CreateableAndFilableFolderSearchAction(), false);
                if (null == folderTypeId)
                {
                    folderTypeId = baseFolderTypeId;
                }
                cmisTypeDefinitionType typeDefinition = getAndAssertTypeDefinition(folderTypeId);
                policyControllableTypeId = ((null == policyControllableTypeId) && typeDefinition.controllablePolicy) ? (folderTypeId) : (null);
            }

            if ((null == relationshipTypeId) && (null != baseRelationshipTypeId))
            {
                relationshipTypeId = enumerateAndAssertTypesForAction(getAndAssertTypeDescendants(baseRelationshipTypeId, -1, true), new BestRelationshipSearchAction(), true);

                if (null == relationshipTypeId)
                {
                    relationshipSourceTypeId = documentTypeId;
                    relationshipTargetTypeId = documentTypeId;
                    relationshipTypeId = baseRelationshipTypeId;
                }
            }

            if ((null == policyTypeId) && (null != basePolicyTypeId))
            {
                policyTypeId = enumerateAndAssertTypesForAction(getAndAssertTypeDescendants(basePolicyTypeId, -1, true), new BestPolicyTypeSearchAction(), true);
            }
        }

        protected static string enumerateAndAssertTypesForAction(cmisTypeContainer[] rootContainers, TypeAction action, bool firstIsValid)
        {
            Queue<cmisTypeContainer> queue = new Queue<cmisTypeContainer>(rootContainers);
            string actionResult = null;
            while (queue.Count > 0)
            {
                cmisTypeContainer typeContainer = queue.Dequeue();
                assertTypeContainer(typeContainer);
                string currentResult = action.perform(typeContainer.type);
                if (null != currentResult)
                {
                    if (firstIsValid)
                    {
                        return currentResult;
                    }
                    else
                    {
                        actionResult = currentResult;
                    }
                }

                if (null != typeContainer.children)
                {
                    foreach (cmisTypeContainer container in typeContainer.children)
                    {
                        assertTypeContainer(container);
                        queue.Enqueue(container);
                    }
                }
            }
            return actionResult;
        }

        protected class TypeAction
        {
            public virtual string perform(cmisTypeDefinitionType typeDefinition)
            {
                return null;
            }
        }

        protected class BestDocumentSearchAction : TypeAction
        {
            private int maxIndex = 0;

            public override string perform(cmisTypeDefinitionType typeDefinition)
            {
                cmisTypeDocumentDefinitionType documentType = (cmisTypeDocumentDefinitionType)typeDefinition;
                int currentTypeIndex = getTypeIndex(documentType);
                if (currentTypeIndex > maxIndex)
                {
                    maxIndex = currentTypeIndex;
                    return documentType.id;
                }
                return null;
            }
        }

        protected class CreateableAndFilableFolderSearchAction : TypeAction
        {
            private int previousPower = 0;

            public override string perform(cmisTypeDefinitionType typeDefinition)
            {
                if (typeDefinition.creatable)
                {
                    int currentPower = 1;
                    currentPower += (typeDefinition.fileable) ? (1) : (0);
                    currentPower += (typeDefinition.controllablePolicy) ? (2) : (0);
                    currentPower += (typeDefinition.controllableACL) ? (2) : (0);
                    if (currentPower > previousPower)
                    {
                        previousPower = currentPower;
                        return typeDefinition.id;
                    }
                    return null;
                }
                return null;
            }
        }

        protected class BestRelationshipSearchAction : TypeAction
        {
            public override string perform(cmisTypeDefinitionType typeDefinition)
            {
                cmisTypeRelationshipDefinitionType relationshipType = (cmisTypeRelationshipDefinitionType)typeDefinition;
                if (relationshipType.creatable)
                {
                    string sourceType = getDocumentOrFolderCreatebleType(relationshipType.allowedSourceTypes);
                    string targetType = getDocumentOrFolderCreatebleType(relationshipType.allowedTargetTypes);

                    if ((sourceType == null) && isValueNotSet(relationshipType.allowedSourceTypes))
                    {
                        sourceType = documentTypeId;
                    }

                    if ((targetType == null) && isValueNotSet(relationshipType.allowedTargetTypes))
                    {
                        targetType = documentTypeId;
                    }

                    if ((sourceType != null) && (targetType != null))
                    {
                        relationshipSourceTypeId = sourceType;
                        relationshipTargetTypeId = targetType;
                        return typeDefinition.id;
                    }
                }
                return null;
            }
        }

        protected class BestPolicyTypeSearchAction : TypeAction
        {
            public override string perform(cmisTypeDefinitionType typeDefinition)
            {
                cmisTypePolicyDefinitionType convertedType = (cmisTypePolicyDefinitionType)typeDefinition;
                return (typeDefinition.creatable) ? (typeDefinition.id) : (null);
            }
        }

        protected class SearchForNotAllowedDocumentTypeIdAction : TypeAction
        {
            private HashSet<string> allowedTypeIds;

            public SearchForNotAllowedDocumentTypeIdAction(string[] allowedTypeIds)
            {
                this.allowedTypeIds = new HashSet<string>(allowedTypeIds);
            }

            public override string perform(cmisTypeDefinitionType typeDefinition)
            {
                if (typeDefinition.creatable && typeDefinition.fileable && !allowedTypeIds.Contains(typeDefinition.id))
                {
                    return typeDefinition.id;
                }
                return null;
            }
        }

        protected static cmisTypeDefinitionType getAndAssertTypeDefinition(string typeId)
        {
            logger.log("[RepositoryService->getTypeDefinition()]");
            logger.log("Getting Type Definition for " + typeId + " type");
            cmisTypeDefinitionType typeDefinition = repositoryServiceClient.getTypeDefinition(getAndAssertRepositoryId(), typeId, null);
            Assert.IsNotNull(typeDefinition, "Definition of Document type is undefined");
            Assert.IsNotNull(typeDefinition.id, "Invalid Type Definition with undefined Id property");
            Assert.IsFalse("".Equals(typeDefinition.id), "Invalid Type Definition with unspecified Id property");
            Assert.AreEqual(typeId, typeDefinition.id, ("Type Definition has another Type Id than was specified in request. Expected: " + typeId + ", actual: " + typeDefinition.id));
            Assert.IsNotNull(typeDefinition.creatable, ("Invalid Type Definition with undefined Creatable property was returned for " + typeId + " type"));
            if (typeDefinition is cmisTypeDocumentDefinitionType && !baseDocumentTypeId.Equals(typeDefinition.id))
            {
                Assert.IsNotNull(typeDefinition.baseId, ("Invalid Base Type Id was return with Get Type Definition response for " + typeId + " type"));
                Assert.AreEqual(enumBaseObjectTypeIds.cmisdocument, typeDefinition.baseId, "Document Type Definition has invalid Base Type Id property. Expected: " + baseDocumentTypeId + ", actual: " + typeDefinition.baseId);
            }
            else if (typeDefinition is cmisTypeFolderDefinitionType && !baseFolderTypeId.Equals(typeDefinition.id))
            {
                Assert.IsNotNull(typeDefinition.baseId, ("Invalid Base Type Id was return with Get Type Definition response for " + typeId + " type"));
                Assert.AreEqual(enumBaseObjectTypeIds.cmisfolder, typeDefinition.baseId, "Folder Type Definition has invalid Base Type Id property. Expected: " + baseFolderTypeId + ", actual: " + typeDefinition.baseId);
            }
            else if (typeDefinition is cmisTypeRelationshipDefinitionType && !baseRelationshipTypeId.Equals(typeDefinition.id))
            {
                Assert.IsNotNull(typeDefinition.baseId, ("Invalid Base Type Id was return with Get Type Definition response for " + typeId + " type"));
                if (enumBaseObjectTypeIds.cmisrelationship != typeDefinition.baseId)
                {
                    Assert.Fail("Relationship Type Definition has invalid Base Type Id property. Expected: " + baseRelationshipTypeId + ", actual: " + typeDefinition.baseId);
                }
            }

            logger.log("Type Definition was received successfully. Type Id: " + typeDefinition.id + ", Base Type Id: " + typeDefinition.baseId);

            return typeDefinition;
        }

        private static string assertBaseTypeSearch(string baseTypeId, cmisTypeDefinitionType typeDef, Type expectedType, string baseObject)
        {
            if ((null != baseTypeId) && typeDef.GetType().Equals(expectedType))
            {
                Assert.Fail("Get Type Children response contains more than 1 Base " + baseObject + " Type Definition");
            }
            return (typeDef.GetType().Equals(expectedType)) ? (typeDef.id) : (baseTypeId);
        }

        protected static cmisTypeContainer[] getAndAssertTypeDescendants(string parentTypeId, long depth, bool includePropertyDefinitions)
        {
            cmisTypeContainer[] types = null;
            try
            {
                types = repositoryServiceClient.getTypeDescendants(getAndAssertRepositoryId(), parentTypeId, Convert.ToString(depth), includePropertyDefinitions, null);
            }
            catch (Exception e)
            {
                Assert.Fail(e.StackTrace);
            }
            Assert.IsNotNull(types, "Get Type Descendants response is undefined");
            Assert.IsTrue((types.Length > 0), "Get Type Descendants response is empty");
            foreach (cmisTypeContainer container in types)
            {
                assertTypeContainer(container);
            }
            return types;
        }

        private static void assertTypeContainer(cmisTypeContainer container)
        {
            Assert.IsNotNull(container, "One of the Type Containers from Get Type Descendants response is solely in 'not set' state");
            Assert.IsNotNull(container.type, "Undefined Type in one of Type Containers from Get Type Descendants response");
            Assert.IsNotNull(container.type.id, "Undefined Type Id in one of Type Containers from Get Type Descendants response");
        }

        protected static cmisTypeDefinitionType[] getAndAssertTypeChildren(string parentTypeId, bool includePropertiesDefinitions, long maxItems, long skipCount)
        {
            cmisTypeDefinitionListType response = repositoryServiceClient.getTypeChildren(getAndAssertRepositoryId(), parentTypeId, includePropertiesDefinitions, Convert.ToString(maxItems), Convert.ToString(skipCount), null);
            cmisTypeDefinitionType[] result = response.types;

            Assert.IsNotNull(result, "Get Type Children response is undefined");
            Assert.IsTrue((result.Length > 0), "Get Type Children response is empty");
            foreach (cmisTypeDefinitionType typeDef in result)
            {
                Assert.IsNotNull(typeDef, "One of the Type Definitions is solely in 'not set' state in Get Type Children response");
                Assert.IsNotNull(typeDef.id, "Invalid Type Definition in Get Type Children response");
                if (null != parentTypeId)
                {
                    if (null != typeDef.parentId)
                    {
                        Assert.AreEqual(parentTypeId, typeDef.parentId, ("One of the Type Definitions has no '" + parentTypeId + "' as Parent Type Id in Get Type Children response"));
                    }
                    else
                    {
                        if (!getAndAssertBaseFolderTypeId().Equals(typeDef.id) && !getAndAssertBaseDocumentTypeId().Equals(typeDef.id) && !getAndAssertBaseRelationshipTypeId().Equals(typeDef.id))
                        {
                            Assert.Fail("Parent Id of one of the Not Base Type Definitions is undefined");
                        }
                    }
                }
            }
            return result;
        }

        private static string getDocumentOrFolderCreatebleType(string[] types)
        {
            if (null != types)
            {
                string repositoryId = getAndAssertRepositoryId();
                foreach (string type in types)
                {
                    cmisTypeDefinitionType typeDefinition = repositoryServiceClient.getTypeDefinition(repositoryId, type, null);
                    if (typeDefinition.creatable && (enumBaseObjectTypeIds.cmisdocument.Equals(typeDefinition.baseId) ||
                                                     enumBaseObjectTypeIds.cmisfolder.Equals(typeDefinition.baseId)))
                    {
                        return typeDefinition.id;
                    }
                }
            }
            return null;
        }

        private static int getTypeIndex(cmisTypeDocumentDefinitionType documentType)
        {
            int result = 0;
            if (documentType.creatable)
            {
                result += 1000000;
            }
            if (documentType.fileable)
            {
                result += 100000;
            }
            if (documentType.versionable)
            {
                result += 10000;
            }
            bool contentStreamAllowed = enumContentStreamAllowed.allowed.Equals(documentType.contentStreamAllowed) || enumContentStreamAllowed.required.Equals(documentType.contentStreamAllowed);
            if (contentStreamAllowed)
            {
                result += 1000;
            }
            if (documentType.queryable)
            {
                result += 100;
            }
            if (documentType.controllableACL)
            {
                result += 10;
            }
            if (documentType.controllablePolicy)
            {
                result += 10;
            }
            return result;
        }

        protected static FileableObject createAndAssertObject(bool folder, string parentFolderId, string objectTypeId)
        {
            return createAndAssertObject(folder, parentFolderId, objectTypeId, true);
        }

        protected static FileableObject createAndAssertObject(bool folder, string parentFolderId, string objectTypeId, cmisAccessControlListType addACEs, cmisAccessControlListType removeACEs)
        {
            enumTypesOfFileableObjects objectType = (folder) ? (enumTypesOfFileableObjects.folders) : (enumTypesOfFileableObjects.documents);
            return createAndAssertObject(new FileableObject(objectType, parentFolderId, objectTypeId, null, addACEs, removeACEs));
        }

        protected static FileableObject createAndAssertObject(bool folder, string parentFolderId, string objectTypeId, bool validate)
        {
            enumTypesOfFileableObjects objectType = (folder) ? (enumTypesOfFileableObjects.folders) : (enumTypesOfFileableObjects.documents);
            return createAndAssertObject(new FileableObject(objectType, objectTypeId, parentFolderId), validate);
        }

        protected FileableObject createAndAssertObject(string parentFolderId, System.Nullable<enumVersioningState> initialVersion)
        {
            return createAndAssertObject(parentFolderId, initialVersion, true);
        }

        protected FileableObject createAndAssertObject(string parentFolderId, System.Nullable<enumVersioningState> initialVersion, bool validate)
        {
            return createAndAssertObject(new FileableObject(enumTypesOfFileableObjects.documents, parentFolderId, getAndAssertDocumentTypeId(), null, initialVersion, false), validate);
        }

        protected static FileableObject createAndAssertObject(FileableObject creator)
        {
            return createAndAssertObject(creator, true);
        }

        protected static FileableObject createAndAssertObject(FileableObject creator, bool validate)
        {
            creator = (FileableObject)creator.createObject(true);
            try
            {
                if (validate)
                {
                    getAndAssertObjectProperties(creator.ObjectId, creator, true);
                }
            }
            catch (Exception e)
            {
                if (isValueNotSet(creator.ObjectId))
                {
                    Assert.Fail("Object was not created. Error cause message: " + e.Message);
                }
                else
                {
                    Assert.Fail("Newly created Object with Id='" + creator.ObjectId + "' has invalid Properties set. Error cause message: " + e.Message);
                }
            }
            return creator;
        }

        protected string createAndAssertVersionedDocument(string existentDocument, int versionsAmount, bool finishWithMajor)
        {
            string versionedDocumentId = existentDocument;
            byte[] contentEntry = null;
            bool createNew = isValueNotSet(existentDocument);
            FileableObject documentCreator = null;
            string name = null;
            if (createNew)
            {
                logger.log("Creating versioned Document object with " + versionsAmount + " versions");
                documentCreator = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertRootFolder(), getAndAssertDocumentTypeId(),
                    null, null, false);
                versionedDocumentId = createAndAssertObject(documentCreator).ObjectId;
                contentEntry = documentCreator.ContentStream.stream;
                name = documentCreator.ObjectName;
            }
            else
            {
                logger.log("Creating " + versionsAmount + " versions for existent document. Document id = " + existentDocument);
                cmisPropertiesType properties = getAndAssertObjectProperties(existentDocument, null, false);
                name = (string)searchAndAssertPropertyByName(properties.Items, NAME_PROPERTY, false);
                if (isContentStreamAllowed())
                {
                    cmisContentStreamType contentStream = receiveAndAssertContentStream(existentDocument, null);
                    contentEntry = (!isValueNotSet(contentStream)) ? (contentStream.stream) : (null);
                }
                documentCreator = new FileableObject(enumTypesOfFileableObjects.documents, (string)searchAndAssertPropertyByName(properties.Items, TYPE_ID_PROPERTY, false), null, properties, null, false);
                documentCreator.setId(existentDocument);
            }

            for (int i = 0; i < versionsAmount; i++)
            {
                versionedDocumentId = checkOutAndAssert(versionedDocumentId, documentCreator.ObjectTypeId, contentEntry);
                documentCreator.setId(versionedDocumentId);
                versionedDocumentId = checkInAndAssert(versionedDocumentId, (finishWithMajor && ((versionsAmount - 1) == i)));
                contentEntry = checkinContentEntry;
            }

            if (createNew)
            {
                logger.log("New versioned document was successfully created. Versioned document id = " + versionedDocumentId);
            }
            else
            {
                logger.log(versionsAmount + " verstions was/were successfully created for Document. Source document id = " + existentDocument + ". Versioned document id = " + versionedDocumentId);
            }
            return versionedDocumentId;
        }

        protected string checkInAndAssert(string documentId, bool major)
        {
            return checkInAndAssert(documentId, major, false);
        }

        protected string checkInAndAssert(string documentId, bool major, bool assertProperties)
        {
            if (isVersioningAllowed())
            {
                assertCheckedOutDocument(documentId, getAndAssertDocumentTypeId(), null, true);
            }
            logger.log("[VersioningService->checkIn]");
            logger.log("Checkining document='" + documentId + "'");
            cmisContentStreamType checkInContent = FileableObject.createCmisDocumentContent((CHANGED_NAME + ".txt"), checkinContentEntry);
            cmisExtensionType extension = new cmisExtensionType();
            versioningServiceClient.checkIn(getAndAssertRepositoryId(), ref documentId, major, new cmisPropertiesType(), ((isContentStreamAllowed()) ? (checkInContent) : (null)), CHECKIN_COMMENT, null, null, null, ref extension);
            if (assertProperties)
            {
                getAndAssertLatestVersionProperties(documentId, ANY_PROPERTY_FILTER, major);
                if (isContentStreamAllowed())
                {
                    receiveAndAssertContentStream(documentId, checkInContent.stream);
                }
            }
            logger.log("Document was Checked In successfully");
            logger.log("");
            return documentId;
        }

        protected RelationshipObject createAndAssertRelationship(string typeId, string folderId, bool sourceIsNull, bool targetIsNull, bool onlySourceAndTargetObjects)
        {
            return createAndAssertRelationship(false, true, typeId, folderId, sourceIsNull, targetIsNull, onlySourceAndTargetObjects);
        }

        protected RelationshipObject createAndAssertRelationship(bool reset, bool createPropertiesIfNull, string typeId, string folderId, bool sourceIsNull, bool targetIsNull, bool onlySourceAndTargetObjects)
        {
            cmisTypeDefinitionType sourceTypeDefinition = getAndAssertTypeDefinition(getAndAssertRelationshipSourceTypeId());
            FileableObject sourceObject = null;
            if (!sourceIsNull)
            {
                if (enumBaseObjectTypeIds.cmisdocument.Equals(sourceTypeDefinition.baseId))
                {
                    sourceObject = createAndAssertObject(new FileableObject(enumTypesOfFileableObjects.documents, sourceTypeDefinition.id, folderId));
                }
                else
                {
                    sourceObject = createAndAssertObject(new FileableObject(enumTypesOfFileableObjects.folders, sourceTypeDefinition.id, folderId));
                }
                Assert.IsNotNull(sourceObject.ObjectId, "Id of Created Source Object is undefined");
            }

            cmisTypeDefinitionType targetTypeDefinition = getAndAssertTypeDefinition(getAndAssertRelationshipTargetTypeId());
            FileableObject targetObject = null;
            if (!targetIsNull)
            {
                if (enumBaseObjectTypeIds.cmisdocument.Equals(targetTypeDefinition.baseId))
                {
                    targetObject = createAndAssertObject(new FileableObject(enumTypesOfFileableObjects.documents, targetTypeDefinition.id, folderId));
                }
                else
                {
                    targetObject = createAndAssertObject(new FileableObject(enumTypesOfFileableObjects.folders, targetTypeDefinition.id, folderId));
                }
                Assert.IsNotNull(targetObject.ObjectId, "Id of Created Target Object is undefined");
            }
            RelationshipObject result = new RelationshipObject(folderId, relationshipTypeId, sourceObject, targetObject);
            if (!onlySourceAndTargetObjects)
            {
                result = (RelationshipObject)result.createObject(createPropertiesIfNull, reset, folderId, relationshipTypeId);
                Assert.IsNotNull(result.ObjectId, "Id of Created Relationship is undefined");
                logger.log("Relationship was successfully created");
            }
            logger.log("");
            return result;
        }

        public void deleteAndAssertRelationship(RelationshipObject relationship)
        {
            deleteAndAssertObject(relationship, false);
            deleteAndAssertObject(relationship.SourceObject, true);
            deleteAndAssertObject(relationship.TargetObject, true);
        }

        public MultifiledObject createAndAssertMultifilledDocument(string primaryParentId, int parentsAmount)
        {
            FileableObject document = createAndAssertObject(new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertDocumentTypeId(), primaryParentId));
            FileableObject[] parents = new FileableObject[parentsAmount];
            logger.log("Adding parents for document, documentId='" + document.ObjectId + "'");
            string repositoryId = getAndAssertRepositoryId();
            for (int i = 0; i < parentsAmount; i++)
            {
                parents[i] = createAndAssertObject(new FileableObject(enumTypesOfFileableObjects.folders, getAndAssertFolderTypeId(), primaryParentId));
                logger.log("[MultiFilingService->addObjectToFolder]");
                logger.log("Adding Parent for Document, documentId='" + document.ObjectId + "',parentId='" + parents[i].ObjectId + "'");
                try
                {
                    cmisExtensionType extension = new cmisExtensionType();
                    multifilingServiceClient.addObjectToFolder(repositoryId, document.ObjectId, parents[i].ObjectId, false, ref extension);
                }
                catch (System.Exception e)
                {
                    Assert.Fail("Adding Parent for Document with Id=" + document.ObjectId + " was failed. Error: " + e.Message);
                }
                logger.log("");
            }
            logger.log("Parents were successfully added");
            return new MultifiledObject(document, parents);
        }

        public void deleteAndAssertMultifilledDocument(MultifiledObject multifiledDocument)
        {
            if (isValueNotSet(multifiledDocument.DocumentObject))
            {
                return;
            }
            if (!isValueNotSet(multifiledDocument.Parents))
            {
                logger.log("Removing Parents for Document, documentId='" + multifiledDocument.DocumentObject.ObjectId + "'");
                string repositoryId = getAndAssertRepositoryId();
                foreach (FileableObject parent in multifiledDocument.Parents)
                {
                    if (isValueNotSet(parent) || isValueNotSet(parent.ObjectId))
                    {
                        logger.log("Invalid undefined Parent Object was faced");
                        continue;
                    }
                    logger.log("");
                    logger.log("Removing Parent with parentId='" + parent.ObjectId + "'");
                    logger.log("[MultiFilingService->removeObjectFromFolder]");
                    cmisExtensionType extension = new cmisExtensionType();
                    multifilingServiceClient.removeObjectFromFolder(repositoryId, multifiledDocument.DocumentObject.ObjectId, parent.ObjectId, ref extension);
                    logger.log("");
                    deleteAndAssertFolder(parent, false);
                }
                logger.log("Parents were successfully removed");
            }
            else
            {
                logger.log("Parents' list of Document with Id='" + multifiledDocument.DocumentObject.ObjectId + "' is empty");
            }

            deleteAndAssertObject(multifiledDocument.DocumentObject, true);
        }

        public FileableObject createAndAssertFolder(string parentFolderId)
        {
            return createAndAssertObject(new FileableObject(enumTypesOfFileableObjects.folders, getAndAssertFolderTypeId(), parentFolderId));
        }

        public void assertDocumentParents(string documentId, string[] expectedParentsIds)
        {
            receiveAndAssertObjectParents(new ParentsReceiver(documentId, false), expectedParentsIds);
        }

        public void assertFolderParents(string folderId, string[] expectedParentsIds, bool allParents)
        {
            receiveAndAssertObjectParents(new ParentsReceiver(folderId, true), expectedParentsIds);
        }

        public ObjectsHierarchy createAndAssertFilesHierarchy(FileableObject rootFolder, int depth, int minimalChildrenAmount, int maximumChildrenAmount, enumTypesOfFileableObjects allowedObjects)
        {
            if ((depth <= 0) || (maximumChildrenAmount <= 0) || (minimalChildrenAmount > maximumChildrenAmount))
            {
                logger.log("Invalid linear parameter(s) for Hierarchy were specified. Expected: depth > 0; maximumChildrenAmount > 0; minimalChildrenAmount <= maximumChildrenAmount. Hierarchy can't be created");
                return null;
            }
            logger.log("Creating objects hierarchy. Depth=" + depth + ", Root Folder Id='" + rootFolder.ObjectId + "', Allowed Objects Type='" + allowedObjects + "'");
            ObjectsHierarchy hierarchy = new ObjectsHierarchy(rootFolder, allowedObjects);
            List<FileableObject> folders = new List<FileableObject>();
            folders.Add(rootFolder);
            Random randomCounter = new Random();
            for (int i = 0; i < depth; i++)
            {
                List<FileableObject> nextLayerFolders = new List<FileableObject>();
                int requiredFolderContainerNumber = randomCounter.Next(folders.Count);
                int currentObjectNumber = 0;
                foreach (FileableObject currentLevelFolder in folders)
                {
                    int maximum = calculateMaximumChildrenAmount(randomCounter, minimalChildrenAmount, maximumChildrenAmount);
                    int foldersMaximum = randomCounter.Next(Convert.ToInt32(Math.Round(Math.Sqrt(maximum))) + 1);
                    foldersMaximum = ((0 >= foldersMaximum) && (i < (depth - 1)) && (currentObjectNumber++ == requiredFolderContainerNumber)) ? (1) : (foldersMaximum);
                    for (int j = 0; j < maximum; j++)
                    {
                        bool folder = (enumTypesOfFileableObjects.folders == allowedObjects) || ((enumTypesOfFileableObjects.any == allowedObjects) && (j < foldersMaximum));
                        string objectTypeId = (folder) ? (getAndAssertFolderTypeId()) : (getAndAssertDocumentTypeId());
                        FileableObject currentObject = createAndAssertObject(folder, currentLevelFolder.ObjectId, objectTypeId);
                        hierarchy.tryAddObject(currentObject);
                        if (folder)
                        {
                            nextLayerFolders.Add(currentObject);
                        }
                    }
                }
                folders = nextLayerFolders;
            }
            logger.log(hierarchy.getObjectsAmount() + " Object(s) were included to Hierarchy successfully");
            logger.log("");
            return hierarchy;
        }

        private int calculateMaximumChildrenAmount(Random randomCounter, int minimalChildrenAmount, int maximumChildrenAmount)
        {
            int result = randomCounter.Next(MAXIMUM_ODD_OBJECTS_AMOUNT);
            result = ((minimalChildrenAmount < 0) && ((minimalChildrenAmount + result) < 0)) ? (0) : (result + minimalChildrenAmount);
            return ((maximumChildrenAmount > 0) && (result > maximumChildrenAmount)) ? (maximumChildrenAmount) : (result);
        }

        public static cmisPropertiesType getAndAssertObjectProperties(string objectId, FileableObject expectedProperties, bool assertResults)
        {
            return getAndAssertObjectProperties(objectId, ANY_PROPERTY_FILTER, expectedProperties, assertResults);
        }

        public static cmisPropertiesType getAndAssertObjectProperties(string objectId, string filter, FileableObject expectedProperties, bool assertResults)
        {
            logger.log("[ObjectService->getProperties]");
            logger.log("Getting properties for object, objectId='" + objectId);

            cmisPropertiesType result = objectServiceClient.getProperties(getAndAssertRepositoryId(), objectId, filter, null);
            if (assertResults)
            {
                assertProperties(expectedProperties, result);
            }
            if (!isValueNotSet(result) && !isValueNotSet(result.Items))
            {
                logger.log("Properties were received. Properties amount=" + result.Items.Length);
            }
            else
            {
                logger.log("Properties are empty");
            }
            logger.log("");
            return result;
        }

        protected static void assertProperties(FileableObject expectedProperties, cmisPropertiesType properties)
        {
            cmisObjectType cmisObject = new cmisObjectType();
            cmisObject.properties = properties;
            assertObject(expectedProperties, cmisObject, false, enumIncludeRelationships.none);
        }

        protected static void assertObject(FileableObject expectedProperties, cmisObjectType actualObject, bool includeAllowableActions, enumIncludeRelationships relationshipsType)
        {
            Assert.IsFalse(isValueNotSet(actualObject), "Returned Object is undefined");
            Assert.IsFalse(isValueNotSet(actualObject.properties), "Properties of returned Object are undefined");
            Assert.IsFalse(isValueNotSet(actualObject.properties.Items), "Properties of returned Object are empty");
            object propertyValue = searchAndAssertPropertyByName(actualObject.properties.Items, NAME_PROPERTY, false);
            Assert.AreEqual(expectedProperties.ObjectName, propertyValue, ("Object Name Property value differs to Property value returned with Get Properties Response. Expected: " + expectedProperties.ObjectName + ", actual: " + propertyValue));
            propertyValue = searchAndAssertPropertyByName(actualObject.properties.Items, TYPE_ID_PROPERTY, false);
            Assert.AreEqual(expectedProperties.ObjectTypeId, propertyValue, ("Object Type Id Property value differs to Property value returned with Get Properties Response. Expected: " + expectedProperties.ObjectName + ", actual: " + propertyValue));
            propertyValue = searchAndAssertPropertyByName(actualObject.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false);
            Assert.AreEqual(expectedProperties.ObjectId, propertyValue, ("Expected Object Id='" + expectedProperties.ObjectId + "' is not equal to actual Object Id='" + propertyValue + "'"));
            //TODO: uncomment when problem will be found
            //assertActualVersioningState(actualObject.properties, expectedProperties.InitialVersion);
            if (includeAllowableActions)
            {
                Assert.IsNotNull(actualObject.allowableActions, "Allowable Actions was not returned in Get Object Properties response");
                Assert.IsTrue(!isValueNotSet(actualObject.allowableActions.canGetProperties) && actualObject.allowableActions.canGetProperties, ("Properties of Object with id='" + expectedProperties.ObjectId + "' Reading is not allowed but Properties were returned"));
                logger.log("Allowable actions were checked successfully");
            }
            else
            {
                Assert.IsTrue(isValueNotSet(actualObject.allowableActions), "Allowable Actions were not requested but were returned in response of Get Properties");
            }
            assertRelationships(expectedProperties.ObjectId, actualObject.relationship, relationshipsType);
            logger.log("Relationships for " + relationshipsType + " direction were checked successfully");
        }

        /// <summary>
        /// Calculates result with searchAndAssertPropertyByName(cmisProperty[], string, bool) and third parameter equal to 'true'
        /// </summary>
        /// <param name="properties"></param>
        /// <param name="propertyName"></param>
        /// <returns></returns>
        public static object searchAndAssertPropertyByName(cmisProperty[] properties, string propertyName, bool multivalued)
        {
            return searchAndAssertPropertyByName(properties, propertyName, multivalued, true);
        }

        public static object searchAndAssertPropertyByName(cmisProperty[] properties, string propertyName, bool multivalued, bool failOnPropertyUndefined)
        {
            foreach (cmisProperty property in properties)
            {
                Assert.IsNotNull(property, "One of the Object Properties is solely in \"not set\" state");
                string name = getPropertyName(property);
                Assert.IsTrue(((null != name) && !"".Equals(name)), "One of the Properties name is undefined");
                if (name.Equals(propertyName))
                {
                    PropertyInfo propertyValueDescription = property.GetType().GetProperty("value");
                    Assert.IsNotNull(propertyValueDescription, ("Can't receive Property Value from \"" + name + "\" property"));
                    Assert.IsTrue(propertyValueDescription.CanRead, ("Value from \"" + name + "\" Property is not Readable"));
                    Array values = propertyValueDescription.GetValue(property, null) as Array;
                    if (failOnPropertyUndefined)
                    {
                        Assert.IsFalse(isValueNotSet(values), ("Values from \"" + name + "\" Property are undefined"));
                    }
                    object result = null;
                    if (!isValueNotSet(values))
                    {
                        result = (multivalued) ? (values) : (values.GetValue(new long[] { 0 }));
                    }
                    return result;
                }
            }

            logger.log(propertyName + " Property was not found");
            if (failOnPropertyUndefined)
            {
                Assert.Fail("Expected \"" + propertyName + "\" property was not found");
            }
            return null;
        }

        protected string searchForFolderTypeWithAllowingList(string rootFolderId)
        {
            FileableObject folder = createAndAssertFolder(rootFolderId);
            cmisPropertiesType properties = getAndAssertObjectProperties(folder.ObjectId, ANY_PROPERTY_FILTER, folder, true);
            string[] allowedObjectIds = (string[])searchAndAssertPropertyByName(properties.Items, ALLOWED_CHILDREN_TYPE_IDS, true, false);
            object rootFolderTypeId = searchAndAssertPropertyByName(properties.Items, TYPE_ID_PROPERTY, false, false);
            if ((null == allowedObjectIds) || (allowedObjectIds.Length < 1) || isValueNotSet(allowedObjectIds[0]))
            {
                try
                {
                    deleteAndAssertObject(folder, false);
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
                cmisTypeDefinitionType[] folderTypes = getAndAssertTypeChildren(getAndAssertBaseFolderTypeId(), true, 0, 0);
                foreach (cmisTypeDefinitionType folderTypeDefinition in folderTypes)
                {
                    if (folderTypeDefinition.creatable && folderTypeDefinition.fileable && !rootFolderTypeId.Equals(folderTypeDefinition.id))
                    {
                        return folderTypeDefinition.id;
                    }
                }
            }
            else
            {
                return rootFolderTypeId as string;
            }
            return null;
        }

        protected static void assertActualVersioningState(cmisPropertiesType result, System.Nullable<enumVersioningState> versioningState)
        {
            if (isValueNotSet(versioningState))
            {
                return;
            }
            object propertyCheckedOut = searchAndAssertPropertyByName(result.Items, CHECKED_OUT_PROPERTY, false, false);
            object propertyVersioningState = searchAndAssertPropertyByName(result.Items, MAJOR_VERSION_PROPERTY, false, false);
            if (!isVersioningAllowed())
            {
                logger.log("Versioning for Document Type is not allowed!");
                Assert.IsTrue(isValueNotSet(propertyCheckedOut) || (!(bool)propertyCheckedOut), ("Invalid Checked Out Property state. Expected: 'undefined' or 'false', actual: '" + propertyCheckedOut + "'"));
                Assert.IsTrue(isValueNotSet(propertyVersioningState), ("Invalid Versioning State Property. Expected: undefined, actual: " + (((null != propertyVersioningState) && (bool)propertyVersioningState) ? (enumVersioningState.major) : (enumVersioningState.minor))));
            }
            else
            {
                if (enumVersioningState.checkedout == versioningState)
                {
                    Assert.IsTrue(!isValueNotSet(propertyCheckedOut) && (bool)propertyCheckedOut, "Expected Versioning State of Document is Checked Out but Document is not Checked Out");
                }
                else
                {
                    Assert.IsNotNull(propertyVersioningState, ("Versioning State Property is undefined. Expected Property value: " + versioningState));
                    enumVersioningState actualState = ((bool)propertyVersioningState) ? (enumVersioningState.major) : (enumVersioningState.minor);
                    Assert.AreEqual(versioningState, actualState, ("Expected Versioning State of Document: " + versioningState + ", actual: " + actualState));
                }
            }
        }

        public static void assertRelationships(string objectId, cmisObjectType[] relationships, System.Nullable<enumIncludeRelationships> direction)
        {
            if ((null == direction) || (enumIncludeRelationships.none == direction))
            {
                Assert.IsTrue(isValueNotSet(relationships), ("Relationships for " + direction + " direction were returned for Object with Id=" + objectId));
            }
            else
            {
                Assert.IsFalse(isValueNotSet(relationships), ("Relationships for " + direction + " direction for Object with Id=" + objectId + " were not returned"));
                foreach (cmisObjectType relationship in relationships)
                {
                    Assert.IsFalse(isValueNotSet(relationship), ("One of the Relationship Objects for Object with Id=" + objectId + " is in 'not set' state solely"));
                    Assert.IsFalse(isValueNotSet(relationship.properties), "Properties of one of the Relationship Objects are undefined");
                    Assert.IsFalse(isValueNotSet(relationship.properties.Items), "Properties of one of the Relationship Object are empty");

                    string sourceId = (string)searchAndAssertPropertyByName(relationship.properties.Items, RELATIONSHIP_SOURCE_OBJECT_ID, false);
                    string targetId = (string)searchAndAssertPropertyByName(relationship.properties.Items, RELATIONSHIP_TARGET_OBJECT_ID, false);

                    objectId = getVersionSeries(objectId);
                    sourceId = getVersionSeries(sourceId);
                    targetId = getVersionSeries(targetId);

                    switch (direction)
                    {
                        case enumIncludeRelationships.source:
                            {
                                Assert.AreEqual(objectId, sourceId, ("Relationship objects collection with specified Document object id = " + objectId + " as source object are invalid"));
                                break;
                            }
                        case enumIncludeRelationships.target:
                            {
                                Assert.AreEqual(objectId, targetId, ("Relationship objects collection with specified Document object id = " + objectId + " as target object are invalid"));
                                break;
                            }
                        case enumIncludeRelationships.both:
                            {
                                Assert.IsTrue((objectId.Equals(sourceId) || objectId.Equals(targetId)), ("Relationship Object for " + direction + " direction has not " + objectId + " as either Source nor Target Object Id Property"));
                                break;
                            }
                    }
                }
            }
        }

        private static string getVersionSeries(string objectId)
        {
            cmisPropertiesType properties = getAndAssertObjectProperties(objectId, null, false);
            foreach (cmisProperty property in properties.Items)
            {
                if (property is cmisPropertyId)
                {
                    cmisPropertyId idProperty = (cmisPropertyId)property;
                    if (property != null && property.propertyDefinitionId != null && VERSION_SERIES_ID.Equals(property.propertyDefinitionId) &&
                        idProperty.value != null && idProperty.value.Length > 0 && idProperty.value[0] != null && idProperty.value[0].Length > 0)
                    {
                        objectId = idProperty.value[0];
                        break;
                    }
                }
            }
            return objectId;
        }

        public static string getPropertyName(cmisProperty property)
        {
            if (null == property)
            {
                return null;
            }

            string result = property.propertyDefinitionId;
            result = (null == result) ? (property.localName) : (result);
            return (null == result) ? (property.displayName) : (result);
        }

        public static void deleteAndAssertObject(CmisObject targetObject, bool allVersions)
        {
            deleteAndAssertObject(targetObject.ObjectId, allVersions);
        }

        /// <summary>
        /// Calculates result with deleteAndAssertObject(string, bool) second parameter equal to 'true'
        /// </summary>
        /// <param name="documentId"></param>
        public static void deleteAndAssertObject(string objectId)
        {
            deleteAndAssertObject(objectId, true);
        }

        public static void deleteAndAssertObject(string objectId, bool allVersions)
        {
            logger.log("[ObjectService->deleteObject]");
            string repositoryId = getAndAssertRepositoryId();
            logger.log("Deleting Object from repository, repositoryId='" + repositoryId + "', Object Id=" + objectId + "'");
            cmisExtensionType extension = new cmisExtensionType();
            objectServiceClient.deleteObject(repositoryId, objectId, allVersions, ref extension);
            assertObjectAbsence(objectId);
            logger.log("Object was successfully deleted");
            logger.log("");
        }

        /// <summary>
        /// Deletes Document or Folder object (<b>bjectCreator</b> instance) or Relationship object (<b>RelationshipObject</b> instance)
        /// </summary>
        /// <param name="targetObject"></param>
        /// <param name="messagePrefix"></param>
        protected void deleteObjectAndLogIfFailed(CmisObject targetObject, string messagePrefix)
        {
            try
            {
                if (targetObject is FileableObject)
                {
                    deleteAndAssertObject(targetObject, true);
                }
                else
                {
                    if (targetObject is RelationshipObject)
                    {
                        deleteAndAssertRelationship((RelationshipObject)targetObject);
                    }
                    else
                    {
                        logger.log("Invalid Object was specified for deletion. Object Type name: " + targetObject.GetType().Name);
                    }
                }
            }
            catch (Exception e)
            {
                logger.log(messagePrefix + e.Message);
            }
        }

        protected void deleteMultiFilledDocumentAndLogIfFailed(MultifiledObject document, string messagePrefix)
        {
            try
            {
                deleteAndAssertMultifilledDocument(document);
            }
            catch (Exception e)
            {
                logger.log(messagePrefix + e.Message);
            }
        }

        protected static void assertObjectAbsence(string objectId)
        {
            try
            {
                getAndAssertObjectProperties(objectId, null, false);
                Assert.Fail(string.Format(CMIS_OBJECTS_DELETION_FAILED_MESSAGE_PATTERN, new object[] { objectId }));
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }
                // TODO: It is necessary to check exception type!!!
            }
        }

        public void deleteAndAssertFolder(FileableObject folderObject, bool notEmptyBehaviour)
        {
            deleteAndAssertFolder(folderObject.ObjectId, notEmptyBehaviour);
        }

        public void deleteAndAssertFolder(string folderId, bool notEmptyBehaviour)
        {
            try
            {
                logger.log("[ObjectService->deleteObject]");
                string repositoryId = getAndAssertRepositoryId();
                logger.log("Deleting folder in repositoryId='" + repositoryId + "',folderId=" + folderId + "'");
                cmisExtensionType extension = new cmisExtensionType();
                objectServiceClient.deleteObject(repositoryId, folderId, false, ref extension);
                determineAssertionFailed(notEmptyBehaviour, "Not empty Folder was deleted");
                logger.log("Folder was successfully deleted");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }
                determineAssertionFailed(!notEmptyBehaviour, e.Message);
            }
            if (!notEmptyBehaviour)
            {
                assertObjectAbsence(folderId);
            }
        }

        protected void determineAssertionFailed(bool assertionCondition, string message)
        {
            if (assertionCondition)
            {
                Assert.Fail(message);
            }
        }

        public string[] deleteAndAssertHierarchy(ObjectsHierarchy hierarchy, enumUnfileObject unfilingStrategy, bool continueOnFailure)
        {
            logger.log("[ObjectService->deleteTree]");
            logger.log("Delete Tree with Root Folder Id='" + hierarchy.RootFolder.ObjectId + "'. It is " + ((continueOnFailure) ? ("") : ("not ")) + "allowed Continue On Failure");
            deleteTreeResponseFailedToDelete response = objectServiceClient.deleteTree(getAndAssertRepositoryId(), hierarchy.RootFolder.ObjectId, true, unfilingStrategy, continueOnFailure, null);
            string[] undeletedObjectsIds = response != null ? response.objectIds : null;
            if (isValueNotSet(undeletedObjectsIds))
            {
                foreach (FileableObject currentObject in hierarchy.toObjectsList())
                {
                    assertObjectAbsence(currentObject.ObjectId);
                }
                logger.log("Objects Tree deletion was performed. Deletion was completed with fail. Undeleted Object Amount: " + undeletedObjectsIds);
            }
            else
            {
                foreach (string objectId in undeletedObjectsIds)
                {
                    try
                    {
                        getAndAssertObjectProperties(objectId, null, false);
                    }
                    catch (Exception)
                    {
                        Assert.Fail("Undeleted Object with Id='" + objectId + "' is not exist");
                    }
                }
                logger.log("Objects Tree was successfully deleted. Deleted elements Amount=" + hierarchy.getObjectsAmount());
            }
            assertObjectAbsence(hierarchy.RootFolder.ObjectId);
            logger.log("");
            return undeletedObjectsIds;
        }

        public string checkOutAndAssert(string documentId, string objectTypeId, byte[] expectedContent)
        {
            cmisExtensionType extension = new cmisExtensionType();
            logger.log("[VersioningService->checkOut]");
            bool copied = versioningServiceClient.checkOut(getAndAssertRepositoryId(), ref documentId, ref extension);
            assertCheckedOutDocument(documentId, objectTypeId, expectedContent, copied);
            logger.log("Document was successfully Checked Out. PWC Id='" + documentId + "'");
            logger.log("");
            return documentId;
        }

        public void assertCheckedOutDocument(string documentId, string objectTypeId, byte[] expectedContent, bool copied)
        {
            Assert.IsNotNull(documentId, "PWC Id is undefined");
            if (!isValueNotSet(expectedContent))
            {
                cmisContentStreamType contentStream = receiveAndAssertContentStream(documentId, expectedContent, true, null);
                if (isValueNotSet(contentStream))
                {
                    Assert.IsFalse(copied, (CHECKOUT_RESULT_AND_CONTENT_STATE_MESSAGE + "Content marked as Copied but actually it was not setted to PWC Object"));
                }
                else
                {
                    Assert.IsTrue(copied, (CHECKOUT_RESULT_AND_CONTENT_STATE_MESSAGE + "Content marked as Not Copied but actually it was setted to PWC Object"));
                }
            }
            FileableObject expectedObject = new FileableObject(enumTypesOfFileableObjects.documents, objectTypeId, (string)null);
            expectedObject.InitialVersion = enumVersioningState.checkedout;
            cmisPropertiesType properties = getAndAssertObjectProperties(documentId, expectedObject, false);
            object property = searchAndAssertPropertyByName(properties.Items, CHECKED_OUT_PROPERTY, false);
            Assert.IsTrue(!isValueNotSet(property) && (bool)property, "Checked Out Document has Checked Out Property that equal to 'false'");
        }

        protected cmisContentStreamType receiveAndAssertContentStream(string documentId, byte[] expectedContent)
        {
            return receiveAndAssertContentStream(documentId, expectedContent, false, null);
        }

        protected cmisContentStreamType receiveAndAssertContentStream(string documentId, byte[] expectedContent, bool constraintExceptionExpected, string streamId)
        {
            Assert.IsFalse(isValueNotSet(documentId), "Document Id is undefined");
            logger.log("[ObjectService->getContentStream]");
            logger.log("Receiving ContentStream for Document with Id='" + documentId + "'");
            cmisContentStreamType actualStream = null;
            try
            {
                actualStream = objectServiceClient.getContentStream(getAndAssertRepositoryId(), documentId, streamId, null, null, null);
            }
            catch (FaultException<cmisFaultType> e)
            {
                if (constraintExceptionExpected)
                {
                    assertException(e, enumServiceException.constraint);
                    return null;
                }
                else
                {
                    throw e;
                }
            }
            if (!isValueNotSet(actualStream))
            {
                if (!isValueNotSet(expectedContent))
                {
                    // TODO: TRANSACTION related problem!!!
                    Assert.IsTrue(expectedContent.Length == actualStream.stream.Length, "Actual Content length isn't equal to Expected Content length");
                    string expectedContentText = Encoding.GetEncoding(FileableObject.DEFAULT_ENCODING).GetString(expectedContent);
                    string actualContentText = Encoding.GetEncoding(FileableObject.DEFAULT_ENCODING).GetString(actualStream.stream);
                    Assert.AreEqual(expectedContentText, actualContentText, ("Incorrect Content was returned. Expected: " + expectedContentText + ", but actual: " + actualContentText));
                }
                logger.log("Content Stream was received, content was compared to expected value, content size=" + actualStream.stream.Length);
            }
            else
            {
                logger.log("Content Stream was returned as undefined");
            }
            logger.log("");
            return actualStream;
        }

        protected cmisAccessControlListType createSimpleACL(string principalId, string permission)
        {
            cmisAccessControlListType acList = new cmisAccessControlListType();
            cmisAccessControlPrincipalType principal = new cmisAccessControlPrincipalType();
            principal.principalId = principalId;
            cmisAccessControlEntryType ace = new cmisAccessControlEntryType();
            ace.principal = principal;
            ace.permission = new string[] { permission };
            ace.direct = true;
            acList.permission = new cmisAccessControlEntryType[] { ace };
            return acList;
        }

        public void cancelCheckOutAndAssert(string checkedOutDocumentId)
        {
            logger.log("[VersioningService->cancelCheckOut]");
            logger.log("Canceling check out document, objectId='" + checkedOutDocumentId + "'");
            cmisExtensionType extension = new cmisExtensionType();
            versioningServiceClient.cancelCheckOut(getAndAssertRepositoryId(), checkedOutDocumentId, ref extension);
            assertObjectAbsence(checkedOutDocumentId);
            logger.log("Check out was cancelled successfully");
        }

        public cmisPropertiesType getAndAssertLatestVersionProperties(String objectId, string filter, bool major)
        {
            logger.log("[VersioningService->getPropertiesOfLatestVersion]");
            logger.log("Getting properties of latest version, versionSeriesId='" + objectId + "'");
            cmisPropertiesType response = versioningServiceClient.getPropertiesOfLatestVersion(getAndAssertRepositoryId(), objectId, major, filter, null);
            asserLatestVersionProperties(objectId, filter, response, major);
            logger.log("Properties of latest version were successfully received");
            return response;
        }

        private void asserLatestVersionProperties(String objectId, string filter, cmisPropertiesType response, bool major)
        {
            Assert.IsFalse(isValueNotSet(response), "Properties holder Object is undefined");
            if (isValueNotSet(filter) || "".Equals(filter) || ANY_PROPERTY_FILTER.Equals(filter))
            {
                object actual = searchAndAssertPropertyByName(response.Items, major ? MAJOR_VERSION_PROPERTY : LATEST_VERSION_PROPERTY, false);
                Assert.IsTrue(((null != actual) && (bool)actual), (objectId + " Object is not Latest Version Object"));
                assertActualVersioningState(response, (major) ? (enumVersioningState.major) : (enumVersioningState.minor));
            }
            else
            {
                assertPropertiesByFilter(response, filter);
            }
        }

        protected void assertPropertiesByFilter(cmisPropertiesType properties, string filter)
        {
            if (isValueNotSet(filter) || "".Equals(filter) || "*".Equals(filter))
            {
                return;
            }
            Assert.IsFalse(isValueNotSet(properties), "Properties are undefined");
            Assert.IsFalse(isValueNotSet(properties.Items), "Properties are empty");
            string[] tokens = filter.Split(new string[] { ",", ", " }, StringSplitOptions.RemoveEmptyEntries);
            HashSet<string> trimmedTokens = new HashSet<string>();
            foreach (string token in tokens)
            {
                trimmedTokens.Add(token.Trim());
            }
            foreach (cmisProperty property in properties.Items)
            {
                Assert.IsFalse(isValueNotSet(property), "One of the Properties is in 'not set' state solely");
                string name = getPropertyName(property);
                Assert.IsFalse(isValueNotSet(name), "One of the Properties has no Name Property attribute");
                Assert.IsTrue(trimmedTokens.Contains(name), (filter + " filter does not allow " + name + " Property"));
            }
        }

        private void receiveAndAssertObjectParents(ParentsReceiver receiver, string[] expectedParentsIds)
        {
            cmisObjectType[] actualParents = receiver.receiveParents();
            Assert.IsFalse(isValueNotSet(actualParents), "Parent(s) from response is/are undefined");
            assertObjectCollectionsConsitence(actualParents, expectedParentsIds);
            logger.log("Parents were successfully received");
        }

        public void assertObjectCollectionsConsitence(cmisObjectType[] actualObjects, string[] expectedIds)
        {
            Assert.IsTrue((expectedIds.Length == actualObjects.Length), "Size of Expected Object Collection is not equal to Actual Object Collection. Collections of Expected and Actual Objects are not consistent");
            HashSet<string> expectedIdsSet = new HashSet<string>(expectedIds);
            foreach (cmisObjectType actualObject in actualObjects)
            {
                Assert.IsFalse(isValueNotSet(actualObject), "One of the Parent Object is in 'not set' state solely");
                Assert.IsFalse(isValueNotSet(actualObject.properties), "Properties of one of the Parent Object are undefined");
                Assert.IsFalse(isValueNotSet(actualObject.properties.Items), "Properties of one of the Parent Object are empty");
                string objectId = (string)searchAndAssertPropertyByName(actualObject.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false);
                Assert.IsTrue(expectedIdsSet.Contains(objectId), ("objectId='" + objectId + "' is not in expected Object Ids set. Collections of Expected and Actual Objects are not consistent"));
            }
        }

        protected cmisObjectInFolderListType getAndAssertChildren(string rootFolderId, string filter, string orderBy, long maxItems, long skipCount, string renditionFilter)
        {
            logger.log("Receiving children for folder, folderId=" + rootFolderId + "', filter='" + filter + "'");
            logger.log("[NavigationService->getChildren]");
            cmisObjectInFolderListType children = navigationServiceClient.getChildren(
                                                  getAndAssertRepositoryId(), rootFolderId, filter,
                                                  orderBy, false, enumIncludeRelationships.none, renditionFilter, false, Convert.ToString(maxItems), Convert.ToString(skipCount), null);
            Assert.IsNotNull(children, "Get Children response is undefined");
            Assert.IsFalse(isValueNotSet(children.objects), "Get Children response is empty");
            foreach (cmisObjectInFolderType objectInFolder in children.objects)
            {
                Assert.IsFalse(isValueNotSet(objectInFolder), "One of the Objects in Folder is in 'not set' state solely");
                Assert.IsFalse(isValueNotSet(objectInFolder.@object), "One of the Objects is in 'not set' state solely");
            }
            return children;
        }

        protected void getPropertiesUsingCredentials(string objectId, string username, string password)
        {
            cmisPropertiesType response = null;
            logger.log("[ObjectService->getProperties]");
            response = CmisClientFactory.getInstance(username, password).getObjectServiceClient().getProperties(getAndAssertRepositoryId(), objectId, "*", null);
            Assert.IsTrue(response != null && response.Items != null && response.Items.Length > 0, "No properties were returned");
        }

        protected string updatePropertiesUsingCredentials(string objectId, string username, string password)
        {
            string changeToken = "";
            cmisExtensionType extension = new cmisExtensionType();
            cmisPropertiesType objectProperties = new cmisPropertiesType();
            objectProperties.Items = new cmisProperty[1];
            objectProperties.Items[0] = new cmisPropertyString();
            objectProperties.Items[0].propertyDefinitionId = NAME_PROPERTY;
            ((cmisPropertyString)objectProperties.Items[0]).value = new string[] { FileableObject.generateObjectName(false, "_renamed") };
            logger.log("[ObjectService->updateProperties]");
            objectServiceClient.updateProperties(repositoryId, ref objectId, ref changeToken, objectProperties, ref extension);
            Assert.IsNotNull(objectId, "Returned objectId is null");
            return objectId;
        }

        public void assertException(FaultException<cmisFaultType> actualException, enumServiceException expectedException)
        {
            HashSet<enumServiceException> exceptionsSet = new HashSet<enumServiceException>();
            exceptionsSet.Add(expectedException);
            assertException(actualException, exceptionsSet);
        }

        public void assertException(FaultException<cmisFaultType> actualException, HashSet<enumServiceException> expectedExceptions)
        {
            if (expectedExceptions == null || expectedExceptions.Count < 1)
            {
                return;
            }
            enumServiceException actualExceptionType = enumServiceException.runtime;
            bool found = false;
            if (actualException.Detail != null && actualException.Detail.Nodes != null)
            {
                foreach (XmlNode node in actualException.Detail.Nodes)
                {
                    if (node != null && node.Name != null && node.Name.Equals("type") && node.InnerText != null)
                    {
                        try
                        {
                            actualExceptionType = (enumServiceException)Enum.Parse(typeof(enumServiceException), node.InnerText, true);
                            found = true;
                        }
                        catch (Exception)
                        {
                        }
                    }
                }
            }
            if (!found || !expectedExceptions.Contains(actualExceptionType))
            {
                Assert.Fail("Received exception '" + actualExceptionType + "' is not in set of expected exceptions: " + expectedExceptions.ToString() + "'");
            }
        }

        protected void assertRenditions(cmisObjectType cmisObject, string filter, string[] expectedKinds, string[] expectedMimetypes)
        {
            Assert.IsNotNull(cmisObject, "cmisObject is null");

            if (filter == null || filter.Equals(RENDITION_FILTER_NONE))
            {
                Assert.IsTrue(cmisObject.rendition == null || cmisObject.rendition.Length == 0, "Rendition are not empty for empty filter");
            }
            else
            {
                if (cmisObject.rendition != null)
                {
                    logger.log(cmisObject.rendition.Length + " renditions received for filter '" + filter + "'");
                    foreach (cmisRenditionType rendition in cmisObject.rendition)
                    {
                        assertRendition(rendition);
                        if (!filter.Equals(RENDITION_FILTER_WILDCARD))
                        {
                            assertContains(rendition, expectedKinds, expectedMimetypes);
                        }
                    }
                }
            }
        }

        protected void assertRendition(cmisRenditionType rendition)
        {
            Assert.IsNotNull(rendition, "Rendition is null");
            Assert.IsNotNull(rendition.streamId, "Rendition streamId is null");
            Assert.IsNotNull(rendition.kind, "Rendition kind is null");
            Assert.IsNotNull(rendition.mimetype, "Rendition mimetype is null");
            if (rendition.mimetype.StartsWith(IMAGE_BASE_MIMETYPE))
            {
                Assert.IsNotNull("Rendition width is null", rendition.width);
                Assert.IsNotNull("Rendition height is null", rendition.height);
            }
            logger.log("Rendition(kind='" + rendition.kind + "',mimetype='" + rendition.mimetype + "')");
        }

        protected void assertContains(cmisRenditionType rendition, string[] expectedKinds, string[] expectedMimetypes)
        {
            bool contains = false;
            if (expectedKinds != null)
            {
                foreach (string kind in expectedKinds)
                {
                    if (rendition.kind.Equals(kind))
                    {
                        contains = true;
                        break;
                    }
                }
            }
            if (expectedMimetypes != null)
            {
                foreach (string mimetype in expectedMimetypes)
                {
                    if (mimetype.EndsWith(RENDITION_FILTER_SUBTYPES_POSTFIX))
                    {
                        string baseMimetype = getBaseType(mimetype);
                        if (rendition.mimetype.StartsWith(baseMimetype))
                        {
                            contains = true;
                            break;
                        }
                    }
                    if (rendition.mimetype.Equals(mimetype))
                    {
                        contains = true;
                        break;
                    }
                }
            }
            Assert.IsTrue(contains, "Received rendition doesn't satisfy the filter conditions");
        }

        protected string[] getMimeTypes(cmisRenditionType[] renditions)
        {
            HashSet<string> result = null;
            if (renditions != null)
            {
                result = new HashSet<string>();
                foreach (cmisRenditionType rendition in renditions)
                {
                    result.Add(rendition.mimetype);
                }
            }
            return result != null ? toArray(result) : null;
        }

        protected string[] toArray(ICollection<string> collection)
        {
            string[] result = null;
            if (collection != null)
            {
                result = new string[collection.Count];
                collection.CopyTo(result, 0);
            }
            return result;
        }

        protected string[] getBaseMimeTypes(cmisRenditionType[] renditions)
        {
            HashSet<string> result = null;
            if (renditions != null)
            {
                result = new HashSet<string>();
                foreach (cmisRenditionType rendition in renditions)
                {
                    result.Add(getBaseType(rendition.mimetype) + RENDITION_FILTER_SUBTYPES_POSTFIX);
                }
            }
            return result != null ? toArray(result) : null;
        }

        protected string getBaseType(string mimetype)
        {
            string baseMymetype = mimetype;
            int subTypeIndex = mimetype.IndexOf("/");
            if (subTypeIndex > 0 || subTypeIndex < mimetype.Length)
            {
                baseMymetype = mimetype.Substring(0, subTypeIndex);
            }
            return baseMymetype;
        }

        protected string[] getKinds(cmisRenditionType[] renditions)
        {
            HashSet<string> result = null;
            if (renditions != null)
            {
                result = new HashSet<string>();
                foreach (cmisRenditionType rendition in renditions)
                {
                    result.Add(rendition.kind);
                }
            }
            return result != null ? toArray(result) : null;
        }

        public static string createFilter(string[] kinds, string[] mimetypes)
        {
            StringBuilder filter = new StringBuilder();
            if (kinds != null)
            {
                foreach (string kind in kinds)
                {
                    filter.Append(kind);
                    filter.Append(RENDITION_FILTER_DELIMITER);
                }
            }
            if (mimetypes != null)
            {
                foreach (string mimetype in mimetypes)
                {
                    filter.Append(mimetype);
                    filter.Append(RENDITION_FILTER_DELIMITER);
                }
            }
            filter.Remove(filter.Length - 1, 1);
            return filter.ToString();
        }

        protected List<RenditionData> getTestRenditions(string objectId)
        {
            logger.log("[ObjectService->getRenditions]");
            cmisRenditionType[] allRenditions = objectServiceClient.getRenditions(getAndAssertRepositoryId(), objectId, RENDITION_FILTER_WILDCARD, "200", "0", null);

            List<RenditionData> testRenditions = null;
            if (allRenditions != null && allRenditions.Length > 0)
            {
                string[] kinds = getKinds(allRenditions);
                string[] mimetypes = getMimeTypes(allRenditions);
                string[] baseMimeTypes = getBaseMimeTypes(allRenditions);
                testRenditions = new List<RenditionData>();
                testRenditions.Add(new RenditionData(RENDITION_FILTER_WILDCARD, null, null));
                testRenditions.Add(new RenditionData(new string[] { kinds[0] }, null));
                testRenditions.Add(new RenditionData(kinds, null));
                testRenditions.Add(new RenditionData(null, new string[] { mimetypes[0] }));
                testRenditions.Add(new RenditionData(new string[] { kinds[0] }, new string[] { mimetypes[0] }));
                testRenditions.Add(new RenditionData(null, new string[] { baseMimeTypes[0] }));
                testRenditions.Add(new RenditionData(new string[] { kinds[0] }, new string[] { baseMimeTypes[0] }));
            }
            return testRenditions;
        }

        public abstract class CmisObject
        {
            private string objectId;
            private string objectTypeId;
            private string objectParentId;
            private cmisPropertiesType objectProperties;
            private enumTypesOfFileableObjects objectType;
            private Dictionary<string, cmisProperty> properties = new Dictionary<string, cmisProperty>();

            private static Dictionary<string, Type> PROPERTIES_TYPE_BY_NAME_MAPPING = new Dictionary<string, Type>();
            static CmisObject()
            {
                PROPERTIES_TYPE_BY_NAME_MAPPING.Add(NAME_PROPERTY, typeof(cmisPropertyString));
                PROPERTIES_TYPE_BY_NAME_MAPPING.Add(CONTENT_STREAM_NAME_PROPERTY, typeof(cmisPropertyString));

                PROPERTIES_TYPE_BY_NAME_MAPPING.Add(TYPE_ID_PROPERTY, typeof(cmisPropertyId));
                PROPERTIES_TYPE_BY_NAME_MAPPING.Add(OBJECT_IDENTIFIER_PROPERTY, typeof(cmisPropertyId));
                PROPERTIES_TYPE_BY_NAME_MAPPING.Add(SOURCE_OBJECT_ID, typeof(cmisPropertyId));
                PROPERTIES_TYPE_BY_NAME_MAPPING.Add(TARGET_OBJECT_ID, typeof(cmisPropertyId));
            }

            public CmisObject(enumTypesOfFileableObjects objectType)
            {
                this.objectType = objectType;
            }

            public CmisObject(enumTypesOfFileableObjects objectType, string objectParentId, string objectTypeId)
                : this(objectType)
            {
                this.objectTypeId = objectTypeId;
                this.objectParentId = objectParentId;
            }

            public CmisObject(enumTypesOfFileableObjects objectType, string objectParentId, string objectTypeId, cmisPropertiesType objectProperties)
                : this(objectType, objectParentId, objectTypeId)
            {
                this.objectProperties = objectProperties;
            }

            public bool IsDocument
            {
                get
                {
                    return enumTypesOfFileableObjects.documents == objectType;
                }
            }

            public bool IsFolder
            {
                get
                {
                    return enumTypesOfFileableObjects.folders == objectType;
                }
            }

            public bool IsRelationship
            {
                get
                {
                    return enumTypesOfFileableObjects.relationships == objectType;
                }
            }

            public bool IsFilableObject
            {
                get
                {
                    return (enumTypesOfFileableObjects.documents == objectType) || (enumTypesOfFileableObjects.folders == objectType);
                }
            }

            public string ObjectId
            {
                get
                {
                    return objectId;
                }
            }

            protected void setObjectId(string objectId)
            {
                this.objectId = objectId;
            }

            public string ObjectTypeId
            {
                get
                {
                    return objectTypeId;
                }
                set
                {
                    objectTypeId = value;
                }
            }

            public string ObjectParentId
            {
                get
                {
                    return objectParentId;
                }
                set
                {
                    objectParentId = value;
                }
            }

            public cmisPropertiesType getObjectProperties()
            {
                return getObjectProperties(true);
            }

            public virtual cmisPropertiesType getObjectProperties(bool createIfNull)
            {
                if ((null == objectProperties) && createIfNull)
                {
                    objectProperties = new cmisPropertiesType();
                    addProperty(TYPE_ID_PROPERTY, ObjectTypeId);
                }
                if (!isValueNotSet(objectProperties) && (properties.Count > 0))
                {
                    if (isValueNotSet(objectProperties.Items))
                    {
                        objectProperties.Items = new cmisProperty[properties.Values.Count];
                    }
                    else
                    {
                        if (objectProperties.Items.Length != properties.Values.Count)
                        {
                            cmisProperty[] newProperties = null;
                            Array.Resize(ref newProperties, properties.Values.Count);
                            objectProperties.Items = newProperties;
                        }
                    }
                    properties.Values.CopyTo(objectProperties.Items, 0);
                }
                return objectProperties;
            }

            public CmisObject createObject(bool createPropertiesIfNull, bool reset, string objectParentId, string objectTypeId)
            {
                if (reset)
                {
                    resetObject();
                }
                this.objectTypeId = objectTypeId;
                objectProperties = getObjectProperties(createPropertiesIfNull);
                this.objectParentId = objectParentId;
                return performCreation();
            }

            protected virtual void resetObject()
            {
                objectId = null;
                objectTypeId = null;
                objectParentId = null;
                objectProperties = null;
                properties.Clear();
            }

            protected abstract CmisObject performCreation();

            public virtual cmisProperty addProperty(string propertyName, object propertyValue)
            {
                if (TYPE_ID_PROPERTY.Equals(propertyName))
                {
                    ObjectTypeId = (string)propertyValue;
                }
                cmisProperty requiredProperty = null;
                Type propertyType = null;
                if (PROPERTIES_TYPE_BY_NAME_MAPPING.TryGetValue(propertyName, out propertyType))
                {
                    requiredProperty = createPropertyFromClass(propertyType, propertyName, propertyValue);
                }
                else
                {
                    throw new Exception("Class for " + propertyName + " property was not found");
                }
                if (properties.ContainsKey(propertyName))
                {
                    properties.Remove(propertyName);
                }
                properties.Add(propertyName, requiredProperty);
                return requiredProperty;
            }

            public cmisProperty removeProperty(string propertyName)
            {
                cmisProperty removingProperty = null;
                if (properties.TryGetValue(propertyName, out removingProperty))
                {
                    properties.Remove(propertyName);
                }
                return removingProperty;
            }

            // Static methods

            public static cmisPropertiesType addNamePropertyToObject(cmisPropertiesType properties, string name)
            {
                return addPropertyToObject(properties, NAME_PROPERTY, name);
            }

            public static cmisPropertiesType addPropertyToObject(cmisPropertiesType properties, string propertyName, object propertyValue)
            {
                if ((null == propertyName) || !PROPERTIES_TYPE_BY_NAME_MAPPING.ContainsKey(propertyName))
                {
                    return properties;
                }
                if (null == properties)
                {
                    properties = new cmisPropertiesType();
                    properties.Items = new cmisProperty[2];
                }

                cmisProperty newProperty = null;
                int elementsAmount = 0;
                Type propertyType = null;
                if (!PROPERTIES_TYPE_BY_NAME_MAPPING.TryGetValue(propertyName, out propertyType))
                {
                    throw new Exception("Class for " + propertyName + " property was not found");
                }
                foreach (cmisProperty property in properties.Items)
                {
                    bool propertyNotUndefined = !isValueNotSet(property);
                    if (!propertyNotUndefined || (property.GetType().Equals(propertyType) && propertyName.Equals(getPropertyName(property))))
                    {
                        if (propertyNotUndefined)
                        {
                            newProperty = property;
                        }
                        break;
                    }
                    elementsAmount++;
                }
                newProperty = isValueNotSet(newProperty) ? (createPropertyFromClass(propertyType, propertyName, propertyValue)) : (newProperty);
                if (properties.Items.Length <= elementsAmount)
                {
                    cmisProperty[] extendedProperties = properties.Items;
                    Array.Resize(ref extendedProperties, (elementsAmount + 1));
                    extendedProperties[elementsAmount] = newProperty;
                    properties.Items = extendedProperties;
                }
                else
                {
                    properties.Items[elementsAmount] = newProperty;
                }

                return properties;
            }

            private static cmisProperty createPropertyFromClass(Type propertyType, string propertyName, object propertyValue)
            {
                cmisProperty result = (cmisProperty)propertyType.GetConstructor(new Type[0]).Invoke(null);
                result.propertyDefinitionId = propertyName;
                PropertyInfo propertyDescription = result.GetType().GetProperty("value");
                if (propertyDescription.CanWrite)
                {
                    object[] values = null;
                    if (propertyValue.GetType().IsArray)
                    {
                        values = (object[])propertyValue;
                    }
                    else
                    {
                        try
                        {
                            ConstructorInfo arrayConstructor = propertyDescription.PropertyType.GetConstructor(new Type[] { typeof(int) });
                            values = (object[])arrayConstructor.Invoke(new object[] { 1 });
                        }
                        catch (Exception e)
                        {
                            throw new Exception("Can't set " + propertyDescription.Name + " property value. Error cause message: " + e.Message);
                        }
                        values[0] = propertyValue;
                    }
                    propertyDescription.SetValue(result, values, null);
                }
                return result;
            }

            public static cmisProperty removePropertyFromObject(cmisPropertiesType properties, string propertyName)
            {
                if (isValueNotSet(properties) || isValueNotSet(properties.Items) || isValueNotSet(propertyName) || !PROPERTIES_TYPE_BY_NAME_MAPPING.ContainsKey(propertyName))
                {
                    return null;
                }
                cmisProperty result = null;
                for (int i = 0; i < properties.Items.Length; i++)
                {
                    cmisProperty property = properties.Items[i];
                    bool propertyIsNull = isValueNotSet(property);
                    if (propertyIsNull || propertyName.Equals(getPropertyName(property)))
                    {
                        if (!propertyIsNull)
                        {
                            result = property;
                            properties.Items[i] = null;
                        }
                        break;
                    }
                }
                return result;
            }
        }

        public class FileableObject : CmisObject
        {
            public const string DEFAULT_ENCODING = "utf-8";
            public const string DEFAULT_DATETIME_FORMAT = "dd.MM.yyyy hh:mm:ss.ms";
            public const string TEST_DOCUMENT_CONTENT_ENTRY_TEXT = "Test document Content Entry. Creation date time: {0}. Unique timestamp: {1}";

            private const string FOLDER_NAME_PATTERN = "TestFolder ({0}){1}";
            private const string DOCUMENT_NAME_PATTERN = "TestDocument ({0}){1}.txt";

            private const string TEXT_DOCUMENT_MIMETYPE = "text/plain";

            private bool withoutContentStream;
            private bool setContentStreamForcibly;
            private string objectName = null;
            private string postfix;
            private string textualContentStream = null;
            private cmisContentStreamType contentStream = null;
            private System.Nullable<enumVersioningState> initialVersion;
            private cmisAccessControlListType addACEs = null;
            private cmisAccessControlListType removeACEs = null;

            private CmisLogger logger = CmisLogger.getInstance(2);

            // TODO: <Array> policies
            // <Array> ACE addACEs
            // <Array> ACE removeACEs
            public FileableObject(enumTypesOfFileableObjects objectType, string objectParentId, string objectTypeId, cmisPropertiesType properties)
                : base(objectType, objectParentId, objectTypeId, properties)
            {
            }

            public FileableObject(enumTypesOfFileableObjects objectType, string objectParentId, string objectTypeId, cmisPropertiesType properties, cmisAccessControlListType addACEs, cmisAccessControlListType removeACEs)
                : base(objectType, objectParentId, objectTypeId, properties)
            {
                this.addACEs = addACEs;
                this.removeACEs = removeACEs;
            }

            public FileableObject(enumTypesOfFileableObjects objectType, string objectParentId, string objectTypeId, cmisPropertiesType objectProperties, System.Nullable<enumVersioningState> initialVersion, bool setContentStreamForcibly)
                : base(objectType, objectParentId, objectTypeId, objectProperties)
            {
                this.initialVersion = initialVersion;
                this.setContentStreamForcibly = setContentStreamForcibly;
            }

            public FileableObject(enumTypesOfFileableObjects objectType, string objectTypeId, string objectParentId)
                : base(objectType, objectParentId, objectTypeId)
            {
            }

            public CmisObject createObject(bool createPropertiesIfNull)
            {
                return createObject(createPropertiesIfNull, false, ObjectParentId, ObjectTypeId);
            }

            public CmisObject createObject(string postfix)
            {
                return createObject(postfix, ObjectParentId);
            }

            public CmisObject createObject(string postfix, string objectParentId)
            {
                this.postfix = postfix;
                return createObject(true, true, objectParentId, ObjectTypeId);
            }

            protected override CmisObject performCreation()
            {
                string repositoryId = getAndAssertRepositoryId();
                string result = null;
                if (IsFolder)
                {
                    logger.log("[ObjectService->createFolder]");
                    logger.log("Creating folder in repository with repositoryId='" + repositoryId + "',objectParentId='" + ObjectParentId + "'");
                    cmisExtensionType extension = new cmisExtensionType();
                    result = objectServiceClient.createFolder(repositoryId, getObjectProperties(false), ObjectParentId, null, addACEs, removeACEs, ref extension);
                    logger.log("Folder was created, folderId='" + result + "'");
                }
                else
                {
                    logger.log("[ObjectService->createDocument]");
                    logger.log("Creating document in repository with repositoryId='" + repositoryId + "',objectParentId='" + ObjectParentId + "'");
                    cmisExtensionType extension = new cmisExtensionType();
                    result = objectServiceClient.createDocument(repositoryId, getObjectProperties(false), ObjectParentId, ContentStream, InitialVersion, null, addACEs, removeACEs, ref extension);
                    logger.log("Document was created, objectId='" + result + "'");
                }
                logger.log("");
                setObjectId(result);
                return this;
            }

            protected override void resetObject()
            {
                base.resetObject();
                objectName = null;
                contentStream = null;
                textualContentStream = null;
            }

            public void setId(string objectId)
            {
                base.setObjectId(objectId);
            }

            public string ObjectName
            {
                get
                {
                    if (null == objectName)
                    {
                        objectName = generateObjectName(IsFolder, postfix);
                        logger.log("Generated Name='" + objectName + "'");
                        postfix = null;
                        return objectName;
                    }
                    return objectName;
                }
                set
                {
                    objectName = value;
                }
            }

            public override cmisPropertiesType getObjectProperties(bool createIfNull)
            {
                addProperty(NAME_PROPERTY, ObjectName);
                return base.getObjectProperties(createIfNull);
            }

            public cmisContentStreamType ContentStream
            {
                get
                {
                    if (!withoutContentStream && (null == contentStream))
                    {
                        if (setContentStreamForcibly || isContentStreamAllowed())
                        {
                            byte[] stream = getContentEtry();
                            textualContentStream = Encoding.GetEncoding(DEFAULT_ENCODING).GetString(stream);
                            contentStream = createCmisDocumentContent(ObjectName, stream);
                        }
                    }
                    return contentStream;
                }
                set
                {
                    contentStream = value;
                }
            }

            public string ContentStreamText
            {
                get
                {
                    return textualContentStream;
                }
            }

            public bool WithoutContentStream
            {
                get
                {
                    return withoutContentStream;
                }
                set
                {
                    withoutContentStream = value;
                }
            }

            public bool SetContentStreamForcibly
            {
                get
                {
                    return setContentStreamForcibly;
                }
                set
                {
                    setContentStreamForcibly = value;
                }
            }

            public System.Nullable<enumVersioningState> InitialVersion
            {
                get
                {
                    return initialVersion;
                }
                set
                {
                    initialVersion = value;
                }
            }

            public override cmisProperty addProperty(string propertyName, object propertyValue)
            {
                if (NAME_PROPERTY.Equals(propertyName))
                {
                    ObjectName = (string)propertyValue;
                }
                return base.addProperty(propertyName, propertyValue);
            }

            // Static methods

            public static string generateObjectName(bool folder, string postfix)
            {
                postfix = (null != postfix) ? (postfix) : (string.Empty);
                string pattern = (folder) ? (FOLDER_NAME_PATTERN) : (DOCUMENT_NAME_PATTERN);
                string[] parameters = new string[] { DateTime.Now.Ticks.ToString(), postfix };
                string result = string.Format(pattern, parameters);
                return result;
            }

            public static byte[] getContentEtry()
            {
                DateTime currentTime = DateTime.Now;
                string content = string.Format(TEST_DOCUMENT_CONTENT_ENTRY_TEXT, currentTime.ToString(DEFAULT_DATETIME_FORMAT), currentTime.Ticks);
                return Encoding.GetEncoding(DEFAULT_ENCODING).GetBytes(content);
            }

            public static cmisContentStreamType createCmisDocumentContent(string name, byte[] content)
            {
                cmisContentStreamType result = new cmisContentStreamType();
                result.filename = name;
                result.length = content.Length.ToString();
                result.mimeType = TEXT_DOCUMENT_MIMETYPE;
                result.stream = content;
                return result;
            }
        }

        public class RelationshipObject : CmisObject
        {
            private FileableObject sourceObject;
            private FileableObject targetObject;

            public RelationshipObject(string objectParentId, string objectTypeId, FileableObject sourceObject, FileableObject targetObject)
                : base(enumTypesOfFileableObjects.relationships, objectParentId, objectTypeId)
            {
                this.sourceObject = sourceObject;
                this.targetObject = targetObject;
            }

            public void setRelationshipId(string id)
            {
                setObjectId(id);
            }

            public FileableObject SourceObject
            {
                get
                {
                    return sourceObject;
                }
                set
                {
                    sourceObject = value;
                }
            }

            public FileableObject TargetObject
            {
                get
                {
                    return targetObject;
                }
                set
                {
                    targetObject = value;
                }
            }

            public CmisObject createObject(bool createPropertiesIfNull)
            {
                return createObject(createPropertiesIfNull, false, ObjectParentId, ObjectTypeId);
            }

            public CmisObject createObject(FileableObject sourceObject, FileableObject targetObject)
            {
                this.sourceObject = sourceObject;
                this.targetObject = targetObject;
                return createObject(false, false, ObjectParentId, ObjectTypeId);
            }

            protected override CmisObject performCreation()
            {
                logger.log("[ObjectService->createRelationship]");
                logger.log("Creating Relationship, Source Object Id='" + sourceObject.ObjectId + "',Target Object Id='" + targetObject.ObjectId + "'");
                cmisExtensionType extension = new cmisExtensionType();
                cmisPropertiesType properties = FileableObject.addPropertyToObject(null, TYPE_ID_PROPERTY, relationshipTypeId);
                properties = FileableObject.addPropertyToObject(properties, SOURCE_OBJECT_ID, sourceObject.ObjectId);
                properties = FileableObject.addPropertyToObject(properties, TARGET_OBJECT_ID, targetObject.ObjectId);
                string relationshipId = objectServiceClient.createRelationship(getAndAssertRepositoryId(), properties, null, null, null, ref extension);
                setObjectId(relationshipId);
                return this;
            }
        }

        public class MultifiledObject
        {
            private FileableObject documentObject;
            private FileableObject[] parents;

            public MultifiledObject(FileableObject documentObject, FileableObject[] parents)
            {
                this.documentObject = documentObject;
                this.parents = parents;
            }

            public FileableObject DocumentObject
            {
                get
                {
                    return documentObject;
                }
            }

            public FileableObject[] Parents
            {
                get
                {
                    return parents;
                }
            }
        }

        public class ObjectsHierarchy
        {
            private enumTypesOfFileableObjects allowedObjects;
            private FileableObject hierarchyRootFolder;
            private List<FileableObject> objects = new List<FileableObject>();
            private List<string> objectsIds = new List<string>();
            private List<string> folderIds = new List<string>();
            private List<string> documentIds = new List<string>();

            public ObjectsHierarchy(FileableObject rootFolder, enumTypesOfFileableObjects allowedObjects)
            {
                this.allowedObjects = allowedObjects;
                hierarchyRootFolder = rootFolder;
            }

            public string[] toIdsArray()
            {
                return objectsIds.ToArray();
            }

            public List<FileableObject> toObjectsList()
            {
                return objects;
            }

            public void tryAddObject(FileableObject newObject)
            {
                bool allowed = !isValueNotSet(newObject) && ((enumTypesOfFileableObjects.any == allowedObjects) || (newObject.IsFolder && (enumTypesOfFileableObjects.folders == allowedObjects))
                                                             || (!newObject.IsFolder && (enumTypesOfFileableObjects.documents == allowedObjects)));
                if (allowed)
                {
                    objects.Add(newObject);
                    objectsIds.Add(newObject.ObjectId);
                    if (newObject.IsFolder)
                    {
                        folderIds.Add(newObject.ObjectId);
                    }
                    else
                    {
                        documentIds.Add(newObject.ObjectId);
                    }
                }
            }

            public void clearObjects()
            {
                objects.Clear();
                objectsIds.Clear();
                folderIds.Clear();
                documentIds.Clear();
            }

            public int getObjectsAmount()
            {
                return objects.Count;
            }

            public FileableObject RootFolder
            {
                get
                {
                    return hierarchyRootFolder;
                }
            }

            public enumTypesOfFileableObjects AllowedObjects
            {
                get
                {
                    return allowedObjects;
                }
            }

            public List<string> DocumentIds
            {
                get
                {
                    return documentIds;
                }
            }

            public List<string> FolderIds
            {
                get
                {
                    return folderIds;
                }
            }
        }

        public class ParentsReceiver
        {
            private bool folder;
            private string descendantObjectId = null;

            private CmisLogger logger = CmisLogger.getInstance(2);

            public ParentsReceiver(bool folder)
                : this(null, folder)
            {
            }

            public ParentsReceiver(string descendantObjectId, bool folder)
            {
                this.folder = folder;
                this.descendantObjectId = descendantObjectId;
            }

            public cmisObjectType[] receiveParents()
            {
                if (IsFolder)
                {
                    logger.log("[NavigationService->getFolderParent]");
                    logger.log("Receiving FolderParent, descendantObjectId=" + DescendantObjectId);
                    return new cmisObjectType[] { navigationServiceClient.getFolderParent(getAndAssertRepositoryId(), DescendantObjectId, ANY_PROPERTY_FILTER, null) };
                }
                else
                {
                    logger.log("[NavigationService->getObjectParents]");
                    logger.log("Receiving ObjectParents, descendantObjectId=" + DescendantObjectId);
                    cmisObjectParentsType[] response = navigationServiceClient.getObjectParents(getAndAssertRepositoryId(), DescendantObjectId, ANY_PROPERTY_FILTER, false, enumIncludeRelationships.none, null, false, null);
                    cmisObjectType[] result = new cmisObjectType[response.Length];
                    if (response != null)
                    {
                        for (int i = 0; i < response.Length; ++i)
                        {
                            result[i] = response[i] != null ? response[i].@object : null;
                        }
                    }
                    return result;
                }
            }

            public bool IsFolder
            {
                get
                {
                    return folder;
                }
            }

            public string DescendantObjectId
            {
                get
                {
                    if (descendantObjectId == null)
                    {
                        descendantObjectId = createAndAssertObject(false, getAndAssertRootFolder(), getAndAssertDocumentTypeId()).ObjectId;
                    }

                    return descendantObjectId;
                }
                set
                {
                    descendantObjectId = value;
                }
            }
        }

        public class RenditionData
        {
            private string filter;
            private string[] expectedKinds;
            private string[] expectedMimetypes;

            public string getFilter()
            {
                return filter;
            }
            public string[] getExpectedKinds()
            {
                return expectedKinds;
            }
            public string[] getExpectedMimetypes()
            {
                return expectedMimetypes;
            }
            public RenditionData(string[] expectedKinds, string[] expectedMimetypes)
            {
                this.filter = createFilter(expectedKinds, expectedMimetypes);
                this.expectedKinds = expectedKinds;
                this.expectedMimetypes = expectedMimetypes;
            }
            public RenditionData(string filter, string[] expectedKinds, string[] expectedMimetypes)
            {
                this.filter = filter;
                this.expectedKinds = expectedKinds;
                this.expectedMimetypes = expectedMimetypes;
            }
        }

    }
}
