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
using System.Text;
using WcfCmisWSTests.CmisServices;
using System.ServiceModel;
using System.Collections.Generic;

namespace WcfCmisWSTests
{
    ///
    /// author: Dmitry Velichkevich
    /// author: Stas Sokolovsky
    ///
    public class ObjectServiceClientTest : BaseServiceClientTest
    {
        private const string REPLACED_CONTENT_ENTRY = "Replaced Content Entry";

        private const string INVALID_FOLDER_PATH_MESSAGE_PATTERN = "Folder was received by invalid Folder Path='{0}'";

        private const int TEST_HIERARCHY_DEPTH = 4;

        private const int MINIMAL_TEST_OBJECTS_LEVEL_AMOUNT = 2;

        public void testDocumentCreationAndDeletion()
        {
            FileableObject document = null;
            try
            {
                document = createAndAssertObject(false, getAndAssertRootFolder(), getAndAssertDocumentTypeId());
            }
            catch (Exception e)
            {
                Assert.Fail("Document creation failed. Cause error message: " + e.Message);
            }
            try
            {
                deleteAndAssertObject(document, true);
            }
            catch (Exception e)
            {
                Assert.Fail("Document deletion failed. Cause error message: " + e.Message);
            }
        }

        public void testDocumentCreationFromSource()
        {
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.none);
            string sourceDocumentId = document.ObjectId;
            FileableObject folder = createAndAssertFolder(getAndAssertRootFolder());
            string newName = "ObjectFromSource" + document.ObjectName;
            cmisPropertiesType properties = FileableObject.addPropertyToObject(null, NAME_PROPERTY, newName);
            cmisExtensionType extension = new cmisExtensionType();
            logger.log("[ObjectService->createDocumentFromSource]");
            logger.log("Creating document from source, sourceObjectId='" + sourceDocumentId + "', targetFolderId='" + folder.ObjectId + "', new name='" + newName + "'");
            string createdDocumentId = objectServiceClient.createDocumentFromSource(getAndAssertRepositoryId(), document.ObjectId, properties, folder.ObjectId, enumVersioningState.none, null, null, null, ref extension);
            Assert.IsNotNull(createdDocumentId, "Response is null");
            document.setId(createdDocumentId);
            document.ObjectName = newName;
            getAndAssertObjectProperties(createdDocumentId, document, true);
            logger.log("Document was successfully created");
            deleteAndAssertObject(sourceDocumentId);
            deleteAndAssertObject(createdDocumentId);
        }

        public void testDocumentCreationWithoutProperties()
        {
            FileableObject document = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertDocumentTypeId(), getAndAssertRootFolder());
            try
            {
                // TODO: applyPolicies, addACEs, removeACEs
                cmisExtensionType extension = new cmisExtensionType();
                string documentId = objectServiceClient.createDocument(getAndAssertRepositoryId(), null, document.ObjectParentId, document.ContentStream, null, null, null, null, ref extension);
                try
                {
                    deleteAndAssertObject(documentId);
                }
                catch (Exception e)
                {
                    logger.log("Invalid Document creation state. Document was created but it can't be deleted. Trouble message: " + e.Message);
                }
                Assert.Fail("Document was created without Mandatory Properties input parameter");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
        }

        public void testDocumentCreationAgainstUnfilingCapability()
        {
            if (isCapabilityUnfilingEnabled())
            {
                try
                {
                    FileableObject document = createAndAssertObject(false, null, getAndAssertDocumentTypeId());
                    deleteObjectAndLogIfFailed(document, "Can't delete Unfiled Document. Cause error message: ");
                }
                catch (FaultException<cmisFaultType> e)
                {
                    Assert.Fail("Can't create Unfiled Document with enabled Unfiling capability. Cause error message: " + e.Message);
                }
            }
            else
            {
                try
                {
                    FileableObject document = createAndAssertObject(false, null, getAndAssertDocumentTypeId());
                    deleteObjectAndLogIfFailed(document, "Invalid created Document state. Document was created but can't be deleted. Cause error message: ");
                    Assert.Fail("Unfiling is not supported but Unfiled Document was created");
                }
                catch (System.Exception e)
                {
                    if (e is Assert.AssertionException)
                    {
                        throw e;
                    }
                }
            }
        }

        public void testDocumentCreationWithDifferentContentStreamValues()
        {
            FileableObject document = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertDocumentTypeId(), getAndAssertRootFolder());
            document.SetContentStreamForcibly = true;
            if (isContentStreamAllowed())
            {
                try
                {
                    document = createAndAssertObject(document);
                }
                catch (Exception e)
                {
                    Assert.Fail("Content Stream is Allowed but Document was not created with Content Stream parameter. Error cause message: " + e.Message);
                }
                try
                {
                    deleteAndAssertObject(document, true);
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
                document.SetContentStreamForcibly = false;
                try
                {
                    document = createAndAssertObject(document);
                    determineAssertionFailed(isContentStreamRequired(), "Content Stream is Required but Document was created without Content Stream input parameter");
                }
                catch (Exception e)
                {
                    determineAssertionFailed(!isContentStreamRequired(), ("Content Stream is Allowed and not Required but Document was not created without Content Stream input parameter. Error cause message: " + e.Message));
                }
                try
                {
                    deleteAndAssertObject(document, true);
                }
                catch (System.Exception e)
                {
                    Assert.Fail(e.Message);
                }
            }
            else
            {
                try
                {
                    document = createAndAssertObject(document);
                    deleteObjectAndLogIfFailed(document, "Document with not Allowed Content Stream was created with sent Content Stream input parameter but this Document can't be deleted. Error cause message: ");
                    Assert.Fail("Content is not Allowed but Document was created");
                }
                catch (Exception e)
                {
                    if (e is Assert.AssertionException)
                    {
                        throw e;
                    }
                }
            }
        }

        private void checkDocumentToNotAllowedVersioningAttribute(FileableObject document)
        {
            document = createAndAssertObject(document);
            deleteObjectAndLogIfFailed(document, "Document with Not Allowed Versioning attribute was created but can't be deleted. Error cause message: ");
            Assert.Fail("Versioning is Not Allowed but Document was created with Versioning input parameter");
        }

        public void testVersionedDocumentCreationAndDeletion()
        {
            FileableObject documentCheckedout = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertRootFolder(), getAndAssertDocumentTypeId(), null, enumVersioningState.checkedout, false);
            FileableObject documentMinor = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertRootFolder(), getAndAssertDocumentTypeId(), null, enumVersioningState.minor, false);
            FileableObject documentMajor = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertRootFolder(), getAndAssertDocumentTypeId(), null, enumVersioningState.major, false);
            if (isVersioningAllowed())
            {
                assertVersioningStateForNewDocument(documentCheckedout);
                assertVersioningStateForNewDocument(documentMinor);
                assertVersioningStateForNewDocument(documentMajor);
            }
        }

        public void testDocumentCreationWithACL()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            cmisAccessControlListType acl = createSimpleACL(aclPrincipalId, PERMISSION_READ);
            FileableObject document = createAndAssertObject(false, getAndAssertRootFolder(), getAndAssertDocumentTypeId(), acl, null);

            getPropertiesUsingCredentials(document.ObjectId, aclUsername, aclPassword);

            deleteAndAssertObject(document, true);
        }

        private void checkForEqualProperty(object expectedProperty, cmisPropertiesType actualProperties, string property)
        {
            object actualProperty = searchAndAssertPropertyByName(actualProperties.Items, property, false);
            Assert.AreEqual(expectedProperty, actualProperty, ("Property '" + property + "' is not equals for objects. Expected: " + expectedProperty + ", actual: " + actualProperty));
        }

        private void assertVersioningStateForNewDocument(FileableObject documentCreator)
        {
            if (null == documentCreator.InitialVersion)
            {
                documentCreator.InitialVersion = enumVersioningState.minor;
            }
            documentCreator = createAndAssertObject(documentCreator, false);
            cmisPropertiesType actualProperties = objectServiceClient.getProperties(getAndAssertRepositoryId(), documentCreator.ObjectId, ANY_PROPERTY_FILTER, null);
            checkForEqualProperty(documentCreator.ObjectId, actualProperties, OBJECT_IDENTIFIER_PROPERTY);
            bool major;
            switch (documentCreator.InitialVersion)
            {
                case enumVersioningState.checkedout:
                    checkForEqualProperty(true, actualProperties, CHECKED_OUT_PROPERTY);
                    break;
                case enumVersioningState.major:
                    major = true;
                    actualProperties = versioningServiceClient.getPropertiesOfLatestVersion(getAndAssertRepositoryId(), documentCreator.ObjectId, major, ANY_PROPERTY_FILTER, null);
                    Assert.IsTrue((bool)searchAndAssertPropertyByName(actualProperties.Items, MAJOR_VERSION_PROPERTY, false), "Expected major Object version");
                    break;
                case enumVersioningState.minor:
                    major = false;
                    actualProperties = versioningServiceClient.getPropertiesOfLatestVersion(getAndAssertRepositoryId(), documentCreator.ObjectId, major, ANY_PROPERTY_FILTER, null);
                    Assert.IsFalse((bool)searchAndAssertPropertyByName(actualProperties.Items, MAJOR_VERSION_PROPERTY, false), "Expected minor Object version");
                    break;
            }
            deleteAndAssertObject(documentCreator, true);
        }

        public void testDocumentCreationWithConstrainsObservance()
        {
            FileableObject document = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertFolderTypeId(), getAndAssertRootFolder());
            try
            {
                document = createAndAssertObject(document);
                deleteObjectAndLogIfFailed(document, "Document created with invalid Type Id can't be deleted. Cause error message: ");
                Assert.Fail("Document with invalid Type Id was created");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }
            }
            assertCreatingNotAllowedObject(false);
            assertPropertyBoundOuting(document, enumTypesOfFileableObjects.documents);
        }

        private void assertPropertyBoundOuting(CmisObject currentObject, enumTypesOfFileableObjects objectType)
        {
            cmisPropertyStringDefinitionType stringProperty = (findUpdatablePropertyWithLimitations(currentObject.ObjectTypeId, false) as cmisPropertyStringDefinitionType);
            if (null != stringProperty)
            {
                StringBuilder tooBigProperty = generateTooBigPropertyValue(stringProperty);
                currentObject = new FileableObject(objectType, currentObject.ObjectTypeId, currentObject.ObjectParentId);
                currentObject.addProperty(stringProperty.id, tooBigProperty.ToString());
                try
                {
                    if (currentObject.IsFilableObject)
                    {
                        currentObject = createAndAssertObject((FileableObject)currentObject);
                    }
                    else
                    {
                        currentObject = createAndAssertRelationship(currentObject.ObjectTypeId, currentObject.ObjectParentId, false, false, false);
                    }
                    string textualObjectType = (currentObject.IsFilableObject) ? ((currentObject.IsFolder) ? ("Folder") : ("Document")) : ("Relationship");
                    deleteObjectAndLogIfFailed(currentObject, (textualObjectType + " with too big String Property was created but can't be deleted. Cause error message: "));
                    Assert.Fail(textualObjectType + " with too big '" + stringProperty.id + "' String Property was created");
                }
                catch (Exception e)
                {
                    if (e is Assert.AssertionException)
                    {
                        throw e;
                    }
                }
            }
        }

        private StringBuilder generateTooBigPropertyValue(cmisPropertyStringDefinitionType stringProperty)
        {
            StringBuilder result = new StringBuilder("");
            int maximum = Convert.ToInt32(stringProperty.maxLength) + 13;
            for (int i = 0; i < maximum; i++)
            {
                result.Append("A");
            }
            return result;
        }

        private cmisPropertyDefinitionType findUpdatablePropertyWithLimitations(string objectTypeId, bool updatableWhenCheckedOut)
        {
            cmisTypeDefinitionType objectTypeDefinition = getAndAssertTypeDefinition(objectTypeId);
            foreach (cmisPropertyDefinitionType property in objectTypeDefinition.Items)
            {
                if (!updatableWhenCheckedOut && (enumUpdatability.readwrite == property.updatability))
                {
                    cmisPropertyStringDefinitionType result = (property is cmisPropertyStringDefinitionType) ? (property as cmisPropertyStringDefinitionType) : (null);
                    if (!isValueNotSet(result) && !isValueNotSet(result.maxLength) && !result.maxLength.Equals("-1"))
                    {
                        return result;
                    }
                }
                else
                {
                    if (updatableWhenCheckedOut && (enumUpdatability.whencheckedout == property.updatability))
                    {
                        return property;
                    }
                }
            }
            return null;
        }

        private void assertCreatingNotAllowedObject(bool testingFolder)
        {
            string rootFolderId = getAndAssertRootFolder();
            string folderTypeIdWithAllowedList = searchForFolderTypeWithAllowingList(rootFolderId);
            FileableObject folder = (null == folderTypeIdWithAllowedList) ? (null) : (createAndAssertObject(true, getAndAssertRootFolder(), folderTypeIdWithAllowedList));
            if (!isValueNotSet(folder))
            {
                string[] allowedObjectIds = (string[])searchAndAssertPropertyByName(getAndAssertObjectProperties(folder.ObjectId, folder, true).Items, ALLOWED_CHILDREN_TYPE_IDS, true);
                enumTypesOfFileableObjects objectType = (testingFolder) ? (enumTypesOfFileableObjects.folders) : (enumTypesOfFileableObjects.documents);
                string notAllowedTypeId = enumerateAndAssertTypesForAction(getAndAssertTypeDescendants(null, -1, true), new CheckingForNotAllowableAction(objectType, new HashSet<string>(allowedObjectIds)), true);
                if (null != notAllowedTypeId)
                {
                    FileableObject currentObject = null;
                    try
                    {
                        currentObject = createAndAssertObject(testingFolder, folder.ObjectId, notAllowedTypeId);
                        string textualObjectType = (testingFolder) ? ("Folder") : ("Document");
                        deleteObjectAndLogIfFailed(currentObject, (textualObjectType + " with invalid Parent object was created but can't be deleted. Cause error message: "));
                        deleteObjectAndLogIfFailed(folder, "Creatable and Filable Folder with not empty Allowed Child Type Ids property was created but it can't be deleted. Cause error message: ");
                        Assert.Fail(textualObjectType + " was created in Folder which has no " + textualObjectType + "'s Type Id in it Allowed Child Type Ids property list");
                    }
                    catch (Exception e)
                    {
                        if (e is Assert.AssertionException)
                        {
                            if (null != currentObject)
                            {
                                deleteObjectAndLogIfFailed(currentObject, "Not Allowed document was created in in Folder but it can't be deleted. Error cause message: ");
                            }
                            deleteObjectAndLogIfFailed(currentObject, "Creatable and Fileable Folder was created but it can't be deleted");
                            Assert.Fail(e.Message);
                        }

                    }
                }
            }
            if (null != folder)
            {
                try
                {
                    deleteAndAssertObject(folder, true);
                }
                catch (Exception e)
                {
                    Assert.Fail("Creatable and Filable Folder was created but it can't be deleted. Cause error message: " + e.Message);
                }
            }
        }

        protected class CheckingForNotAllowableAction : TypeAction
        {
            private System.Collections.Generic.HashSet<string> allowedTypeIds;
            private enumTypesOfFileableObjects objectType;

            public CheckingForNotAllowableAction(enumTypesOfFileableObjects objectType, System.Collections.Generic.HashSet<string> allowedTypeIds)
            {
                this.objectType = objectType;
                this.allowedTypeIds = allowedTypeIds;
            }

            public override string perform(cmisTypeDefinitionType typeDefinition)
            {
                if (typeDefinition.creatable && !allowedTypeIds.Contains(typeDefinition.id))
                {
                    if ((enumTypesOfFileableObjects.folders == objectType) && (typeDefinition is cmisTypeFolderDefinitionType))
                    {
                        return (typeDefinition.fileable) ? (typeDefinition.id) : (null);
                    }
                    return (typeDefinition is cmisTypeDocumentDefinitionType) ? (typeDefinition.id) : (null);
                }
                return null;
            }
        }

        public void testFolderCreationAndDeletion()
        {
            FileableObject folderObject = null;
            try
            {
                folderObject = createAndAssertFolder(getAndAssertRootFolder());
            }
            catch (Exception e)
            {
                Assert.Fail("Folder was not created. Error cause message: " + e.Message);
            }
            try
            {
                deleteAndAssertObject(folderObject, true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
        }

        public void testFolderCreationWithoutProperties()
        {
            try
            {
                // TODO: applyPolicies, addACEs, removeACEs
                cmisExtensionType extension = new cmisExtensionType();
                string folderId = objectServiceClient.createFolder(getAndAssertRepositoryId(), null, getAndAssertRootFolder(), null, null, null, ref extension);
                try
                {
                    deleteAndAssertFolder(folderId, false);
                }
                catch (Exception e)
                {
                    logger.log("Folder without Properties input parameter was created but it can't be deleted. Error cause message: " + e.Message);
                }
            }
            catch (Exception e)
            {

            }
        }

        public void testFolderCreationWithoutParentId()
        {
            try
            {
                FileableObject folder = createAndAssertFolder(null);
                deleteObjectAndLogIfFailed(folder, "Unfiled Folder was created but it can't be deleted. Error cause message: ");
                Assert.Fail("Unfiled Folder Object was created");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
        }

        public void testFolderCreationWithACL()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            cmisAccessControlListType acl = createSimpleACL(aclPrincipalId, PERMISSION_READ);
            FileableObject folder = createAndAssertObject(true, getAndAssertRootFolder(), getAndAssertFolderTypeId(), acl, null);

            getPropertiesUsingCredentials(folder.ObjectId, aclUsername, aclPassword);

            deleteAndAssertObject(folder.ObjectId, true);
        }

        // TODO: applyPolicies

        public void testFolderCreationConstrantsObservance()
        {
            FileableObject folderObject = new FileableObject(enumTypesOfFileableObjects.folders, getAndAssertDocumentTypeId(), getAndAssertRootFolder());
            try
            {
                folderObject = createAndAssertObject(folderObject);
                deleteObjectAndLogIfFailed(folderObject, "Folder with Not Folder Type Id was created but it can't be deleted. Error cause message: ");
                Assert.Fail("Folder with Not Folder Type Id was created");
            }
            catch (System.Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
            assertPropertyBoundOuting(folderObject, enumTypesOfFileableObjects.folders);
            assertCreatingNotAllowedObject(true);
            // TODO: controllablePolicy, controllableACL etc
        }

        public void testRelationshipCreationAndDeletion()
        {
            RelationshipObject relationship = null;
            try
            {
                relationship = createAndAssertRelationship(getAndAssertRelationshipTypeId(), getAndAssertRootFolder(), false, false, false);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
            try
            {
                deleteAndAssertRelationship(relationship);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
        }

        public void testRelationshipCreationConstraintsObservance()
        {
            string rootFolderId = getAndAssertRootFolder();
            string relationshipTypeId = getAndAssertRelationshipTypeId();
            try
            {
                RelationshipObject relationship = createAndAssertRelationship(relationshipTypeId, rootFolderId, false, true, false);
                deleteObjectAndLogIfFailed(relationship, "Relationship without Target Object Id was created but it can't be deleted. Error cause message: ");
                Assert.Fail("Relationship without Target Object Id was created");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
            try
            {
                RelationshipObject relationship = createAndAssertRelationship(relationshipTypeId, rootFolderId, true, false, false);
                deleteObjectAndLogIfFailed(relationship, "Relationship without Source Object Id was created but it can't be deleted. Error cause message: ");
                Assert.Fail("Relationship without Source Object Id was created");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
            try
            {
                RelationshipObject relationship = createAndAssertRelationship(relationshipTypeId, rootFolderId, true, true, false);
                deleteObjectAndLogIfFailed(relationship, "Relationship without Source and Target Object Ids was created but it can't be deleted. Error cause message: ");
                Assert.Fail("Relationship without Source and Target Object Ids was created");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
        }

        // TODO: !!! POLICY !!!

        public void testAllowableActionsReceving()
        {
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null);
            string repositoryId = getAndAssertRepositoryId();
            try
            {
                assertAllowableActionsReceiving(repositoryId, document.ObjectId);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
            try
            {
                deleteAndAssertObject(document, true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
        }

        private void assertAllowableActionsReceiving(string repositoryId, string documentId)
        {
            logger.log("[ObjectService->getAllowableActions]");
            logger.log("Receiving AllowableActions for document with Id='" + documentId + "'");
            cmisAllowableActionsType response = objectServiceClient.getAllowableActions(repositoryId, documentId, null);
            Assert.IsNotNull(response, "Allowable Actions response is undefined");
            logger.log("AllowableActions were successfully received");
            logger.log("");
            Assert.IsTrue(response.canGetProperties, "AllowableActions->canGetProperties is False for created Document");
            Assert.IsTrue(response.canDeleteObject, "AllowableActions->canDeleteObject is False for created Document");
            Assert.IsTrue(response.canMoveObject, "AllowableActions->canMoveObject is False for created Document");
        }

        public void testAllowableActionsReceivingForInvalidObjectId()
        {
            try
            {
                assertAllowableActionsReceiving(getAndAssertRepositoryId(), INVALID_OBJECT_ID);
                Assert.Fail("Allowable Actions were returned for Invalid Object Id");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
        }

        public void testPropertiesReceiving()
        {
            string rootFolderId = getAndAssertRootFolder();
            FileableObject currentObject = createAndAssertObject(rootFolderId, null);
            try
            {
                getAndAssertObjectProperties(currentObject.ObjectId, currentObject, true);
            }
            catch (Exception e)
            {
                Assert.Fail("Document Object Properties receiving was failed. Error cause message: " + e.Message);
            }
            try
            {
                deleteAndAssertObject(currentObject, true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
            currentObject = createAndAssertObject(true, rootFolderId, getAndAssertFolderTypeId());
            try
            {
                getAndAssertObjectProperties(currentObject.ObjectId, currentObject, true);
            }
            catch (Exception e)
            {
                Assert.Fail("Folder Object Properties receiving was failed. Error cause message: " + e.Message);
            }
            try
            {
                deleteAndAssertObject(currentObject, true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
        }

        public void testObjectReceiving()
        {
            string rootFolderId = getAndAssertRootFolder();
            FileableObject document = createAndAssertObject(rootFolderId, null);
            logger.log("[ObjectService->getAllowableActions]");
            cmisObjectType result = objectServiceClient.getObject(getAndAssertRepositoryId(), document.ObjectId, "*", false, enumIncludeRelationships.none, null, false, false, null);
            Assert.IsNotNull(result, "Response is null");
            assertProperties(document, result.properties);
            deleteAndAssertObject(document, true);
        }

        public void testObjectReceivingIncludeRenditions()
        {
            if (!isRenditionsEnabled())
            {
                Assert.Skip("Renditions are not supported");
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null);
            List<RenditionData> testRenditions = getTestRenditions(document.ObjectId);
            if (testRenditions == null)
            {
                Assert.Skip("No renditions found for document type");
            }
            foreach (RenditionData renditionData in testRenditions)
            {
                cmisObjectType cmisObject = objectServiceClient.getObject(getAndAssertRepositoryId(), document.ObjectId, "*", false, enumIncludeRelationships.none, renditionData.getFilter(), false, false, null);
                Assert.IsNotNull(cmisObject, "Response is null");
                assertRenditions(cmisObject, renditionData.getFilter(), renditionData.getExpectedKinds(), renditionData.getExpectedMimetypes());
            }
            deleteAndAssertObject(document, true);
        }

        public void testRenditionsReceiving()
        {
            if (!isRenditionsEnabled())
            {
                Assert.Skip("Renditions are not supported");
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null);

            logger.log("[ObjectService->getRenditions]");
            cmisRenditionType[] renditions = objectServiceClient.getRenditions(getAndAssertRepositoryId(), document.ObjectId, "*", "200", "0", null);
            if (renditions != null)
            {
                cmisObjectType cmisObject = new cmisObjectType();
                cmisObject.rendition = renditions;
                assertRenditions(cmisObject, "*", null, null);
            }
            deleteAndAssertObject(document, true);
        }

        public void testPropertiesReceivingForInvalidObjectId()
        {
            try
            {
                getAndAssertObjectProperties(INVALID_OBJECT_ID, null, false);
                Assert.Fail("Properties were received for Invalid Object Id");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
        }

        public void testPropertiesReceivingWithAllowableActions()
        {
            FileableObject document = null;
            try
            {
                document = createAndAssertObject(getAndAssertRootFolder(), null);
                getAndAssertObjectProperties(document.ObjectId, document, true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
            finally
            {
                if (null != document)
                {
                    try
                    {
                        deleteAndAssertObject(document, true);
                    }
                    catch (Exception e)
                    {
                        Assert.Fail(e.Message);
                    }
                }
            }
        }

        public void testPropertiesReceivingWithRelationships()
        {
            FileableObject folder = null;
            try
            {
                folder = createAndAssertObject(true, getAndAssertRootFolder(), getAndAssertFolderTypeId());
                getAndAssertObjectProperties(folder.ObjectId, folder, true);
                getAndAssertObjectProperties(folder.ObjectId, folder, true);
                getAndAssertObjectProperties(folder.ObjectId, folder, true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
            finally
            {
                if (null != folder)
                {
                    try
                    {
                        deleteAndAssertObject(folder, true);
                    }
                    catch (Exception e)
                    {
                        Assert.Fail(e.Message);
                    }
                }
            }
        }

        public void testFilteredPropertiesReceiving()
        {
            FileableObject document = null;
            try
            {
                document = createAndAssertObject(getAndAssertRootFolder(), null);
                string filter = OBJECT_IDENTIFIER_PROPERTY + "," + NAME_PROPERTY + "," + TYPE_ID_PROPERTY;
                cmisPropertiesType properties = getAndAssertObjectProperties(document.ObjectId, filter, document, true);
                assertPropertiesByFilter(properties, filter);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
            finally
            {
                if (null != document)
                {
                    try
                    {
                        deleteAndAssertObject(document, true);
                    }
                    catch (Exception e)
                    {
                        Assert.Fail(e.Message);
                    }
                }
            }
        }

        public void testPropertiesReceivingWithInvalidFilter()
        {
            FileableObject folder = null;
            try
            {
                folder = createAndAssertObject(true, getAndAssertRootFolder(), getAndAssertFolderTypeId());
                logger.log("Getting properties with not valid filter");
                getAndAssertObjectProperties(folder.ObjectId, ",,,,,,,,,", folder, false);
                Assert.Fail("Properties were received for not valid filter");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
            finally
            {
                if (null != folder)
                {
                    try
                    {
                        deleteAndAssertObject(folder, true);
                    }
                    catch (Exception e)
                    {
                        Assert.Fail(e.Message);
                    }
                }
            }
        }

        // TODO: includeACLs

        public void testFolderByPathReceiving()
        {
            assertFolderByPathReceiving(null, getAndAssertRepositoryId(), ANY_PROPERTY_FILTER, false, enumIncludeRelationships.none, 5);
        }

        private void deleteHierarchyAndTerminateIfFail(ObjectsHierarchy folders)
        {
            try
            {
                deleteAndAssertHierarchy(folders, enumUnfileObject.delete, false);
            }
            catch (Exception e)
            {
                Assert.Fail("Can't delete Objects Hierarchy with Root Folder Id='" + folders.RootFolder.ObjectId + "'. Error cause message: " + e.Message);
            }
        }

        private string createFoldersPath(ObjectsHierarchy folders)
        {
            StringBuilder result = new StringBuilder("/" + folders.RootFolder.ObjectName);
            foreach (FileableObject folder in folders.toObjectsList())
            {
                result.Append("/" + folder.ObjectName);
            }
            logger.log("Path to folder: '" + result.ToString() + "'");
            return result.ToString();
        }

        public void testFolderByPathReceivingWithInvalidPath()
        {
            ObjectsHierarchy folders = null;
            try
            {
                folders = createAndAssertFilesHierarchy(createAndAssertFolder(getAndAssertRootFolder()), 5, 1, 1, enumTypesOfFileableObjects.folders);
            }
            catch (Exception e)
            {
                Assert.Fail("Can't create Objects Hierarchy. Error cause message: " + e.Message);
            }
            StringBuilder invalidPath = new StringBuilder(createFoldersPath(folders));
            invalidPath.Remove(0, 1);
            string target = invalidPath.ToString();
            string repositoryId = getAndAssertRepositoryId();
            assertObjectByPathReceivingException(repositoryId, target, ANY_PROPERTY_FILTER, false, enumIncludeRelationships.none, folders, null, INVALID_FOLDER_PATH_MESSAGE_PATTERN, target);
            invalidPath.Insert(0, "//");
            target = invalidPath.ToString();
            assertObjectByPathReceivingException(repositoryId, target, ANY_PROPERTY_FILTER, false, enumIncludeRelationships.none, folders, null, INVALID_FOLDER_PATH_MESSAGE_PATTERN, target);
            invalidPath.Remove(0, 1);
            invalidPath.Remove(invalidPath.ToString().LastIndexOf("/"), 1);
            target = invalidPath.ToString();
            assertObjectByPathReceivingException(repositoryId, target, ANY_PROPERTY_FILTER, false, enumIncludeRelationships.none, folders, null, INVALID_FOLDER_PATH_MESSAGE_PATTERN, target);
            assertObjectByPathReceivingException(repositoryId, null, ANY_PROPERTY_FILTER, false, enumIncludeRelationships.none, folders, null, INVALID_FOLDER_PATH_MESSAGE_PATTERN, "undefined");
            deleteHierarchyAndTerminateIfFail(folders);
        }

        private void assertObjectByPathReceivingException(string repositoryId, string path, string filter, bool includreActions, enumIncludeRelationships relationships, ObjectsHierarchy folders, Exception expectedException, string failMessage, string target)
        {
            try
            {
                objectServiceClient.getObjectByPath(repositoryId, path, filter, includreActions, relationships, null, false, false, null); // TODO: includeACL
                deleteHierarchyAndLogIfFail(folders);
                Assert.Fail(string.Format(failMessage, target));
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
        }

        private void deleteHierarchyAndLogIfFail(ObjectsHierarchy folders)
        {
            try
            {
                deleteAndAssertHierarchy(folders, enumUnfileObject.delete, false);
            }
            catch (Exception e)
            {
                logger.log("Can't delete Objects Hierarchy with Root Folder Id='" + folders.RootFolder.ObjectId + "'. Error cause message: " + e.Message);
            }
        }

        public void testFolderByPathReceivingWithAllowableActions()
        {
            string repositoryId = getAndAssertRepositoryId();
            string filter = OBJECT_IDENTIFIER_PROPERTY + "," + NAME_PROPERTY + "," + TYPE_ID_PROPERTY;
            cmisObjectType resultObject = assertFolderByPathReceiving(null, repositoryId, filter, true, enumIncludeRelationships.none, 5);
            assertPropertiesByFilter(resultObject.properties, filter);
        }

        private cmisObjectType assertFolderByPathReceiving(ObjectsHierarchy hierarchy, string repositoryId, string filter, bool includeAllowableActions, enumIncludeRelationships relationships, int hierarchyDepth)
        {
            ObjectsHierarchy folders = hierarchy;
            try
            {
                if (null == folders)
                {
                    folders = createAndAssertFilesHierarchy(createAndAssertFolder(getAndAssertRootFolder()), hierarchyDepth, 1, 1, enumTypesOfFileableObjects.folders);
                }
                cmisObjectType result = objectServiceClient.getObjectByPath(repositoryId, createFoldersPath(folders), filter, includeAllowableActions, relationships, null, false, false, null); // TODO: includeACL
                List<FileableObject> range = folders.toObjectsList().GetRange((folders.getObjectsAmount() - 1), 1);
                IEnumerator<FileableObject> enumerator = range.GetEnumerator();
                enumerator.MoveNext();
                assertObject(enumerator.Current, result, includeAllowableActions, relationships);
                return result;
            }
            catch (Exception e)
            {
                if (null != folders)
                {
                    deleteHierarchyAndLogIfFail(folders);
                    folders = null;
                }
                Assert.Fail(e.Message);
                return null;
            }
            finally
            {
                if ((null != folders) && (null == hierarchy))
                {
                    deleteHierarchyAndTerminateIfFail(folders);
                }
            }
        }

        public void testObjectByPathReceivingWithFilteredProperties()
        {
            string filter = OBJECT_IDENTIFIER_PROPERTY + "," + TYPE_ID_PROPERTY + "," + NAME_PROPERTY;
            cmisObjectType resultObject = assertFolderByPathReceiving(null, getAndAssertRepositoryId(), filter, false, enumIncludeRelationships.none, 5);
            assertPropertiesByFilter(resultObject.properties, filter);
        }

        public void testObjectByPathReceivingWithWrongFilter()
        {
            ObjectsHierarchy folders = null;
            try
            {
                folders = createAndAssertFilesHierarchy(createAndAssertFolder(getAndAssertRootFolder()), 5, 1, 1, enumTypesOfFileableObjects.folders);
            }
            catch (Exception e)
            {
                Assert.Fail("Can't create Folders Hierarchy. Error cause message: " + e.Message);
            }
            assertObjectByPathReceivingException(getAndAssertRepositoryId(), createFoldersPath(folders), ",,,,,", false, enumIncludeRelationships.none, folders, null, "Folder was received with invalid Filter input parameter", string.Empty); // TODO: Expected Exception!!!
            deleteHierarchyAndTerminateIfFail(folders);
        }

        public void testContentStreamReceiving()
        {
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null);
            if (!isContentStreamAllowed())
            {
                try
                {
                    receiveAndAssertContentStream(document.ObjectId, Encoding.GetEncoding(FileableObject.DEFAULT_ENCODING).GetBytes(document.ContentStreamText));
                    deleteObjectAndLogIfFailed(document, "Document can't be deleted. Error cause message: ");
                    Assert.Fail("Content Stream for Document Type is Not Allowed but it was successfully returned");
                }
                catch (Exception e)
                {
                    if (e is Assert.AssertionException)
                    {
                        throw e;
                    }

                }
            }
            else
            {
                try
                {
                    receiveAndAssertContentStream(document.ObjectId, Encoding.GetEncoding(FileableObject.DEFAULT_ENCODING).GetBytes(document.ContentStreamText));
                }
                catch (Exception e)
                {
                    deleteObjectAndLogIfFailed(document, "Document can't be deleted. Error cause message: ");
                    Assert.Fail("Can't receive Content Stream for Document with Id='" + document.ObjectId + "'. Error cause message: " + e.Message);
                }
            }
            try
            {
                deleteAndAssertObject(document, true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
        }

        public void testContentStreamReceivingIncludeRenditions()
        {
            if (!isRenditionsEnabled())
            {
                Assert.Skip("Renditions are not supported");
            }
            if (!isContentStreamAllowed())
            {
                Assert.Skip("Content stream isn't allowed");
            }

            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null);

            logger.log("[ObjectService->getRenditions]");
            cmisRenditionType[] allRenditions = objectServiceClient.getRenditions(getAndAssertRepositoryId(), document.ObjectId, "*", "200", "0", null);
            if (allRenditions != null)
            {
                foreach (cmisRenditionType rendition in allRenditions)
                {
                    assertRendition(rendition);
                    receiveAndAssertContentStream(document.ObjectId, null, false, rendition.streamId);
                }
            }
            else
            {
                Assert.Skip("No renditions found for object type");
            }


            deleteAndAssertObject(document, true);
        }

        public void testGetContentStreamPortioned()
        {
            if (isContentStreamAllowed())
            {
                FileableObject document = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.none);
                string documentId = document.ObjectId;
                byte[] byteContent = document.ContentStream.stream;
                byte[] firstPortion = new byte[7];
                Array.Copy(byteContent, 2, firstPortion, 0, 7);
                byte[] secondPortion = new byte[6];
                Array.Copy(byteContent, byteContent.Length - 6, secondPortion, 0, 6);

                logger.log("[ObjectService->getContentStream]");
                logger.log("Getting content stream for document, documentId='" + documentId + "'");
                cmisContentStreamType response = objectServiceClient.getContentStream(getAndAssertRepositoryId(), documentId, "", null, null, null);
                Assert.IsTrue(response != null && response.stream != null, "No content stream was returned");
                Assert.AreEqual(byteContent, response.stream, "Invalid range of content was returned");
                logger.log("ContentStream was successfully received");

                logger.log("[ObjectService->getContentStream]");
                logger.log("Getting content stream for document, documentId='" + documentId + "', Offset=2, Length=7");
                response = objectServiceClient.getContentStream(getAndAssertRepositoryId(), documentId, "", "2", "7", null);
                Assert.IsTrue(response != null && response.stream != null, "No content stream was returned");
                Assert.AreEqual(firstPortion, response.stream, "Invalid range of content was returned");
                logger.log("ContentStream was successfully received");

                logger.log("[ObjectService->getContentStream]");
                logger.log("Getting content stream for document, documentId='" + documentId + "', Offset=" + (byteContent.Length - 6) + ", Length=10");
                response = objectServiceClient.getContentStream(getAndAssertRepositoryId(), documentId, "", Convert.ToString(byteContent.Length - 6), "10", null);
                Assert.IsTrue(response != null && response.stream != null, "No content stream was returned");
                Assert.AreEqual(secondPortion, response.stream, "Invalid range of content was returned");
                logger.log("ContentStream was successfully received");

                deleteAndAssertObject(documentId);
            }
            else
            {
                Assert.Skip("testGetContentStream was skipped: Content stream isn't allowed");
            }
        }

        public void testContentStreamReceivingForInvalidObjectId()
        {
            try
            {
                receiveAndAssertContentStream(INVALID_OBJECT_ID, null);
                Assert.Fail("Content Stream was received for Not Existent Object");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
            FileableObject folder = null;
            try
            {
                folder = createAndAssertFolder(getAndAssertRootFolder());
            }
            catch (Exception e)
            {
                Assert.Fail("Can't create Folder Object. Error cause message: " + e.Message);
            }
            try
            {
                receiveAndAssertContentStream(folder.ObjectId, null);
                deleteObjectAndLogIfFailed(folder, "Can't delete Folder Object. Error cause message: ");
                Assert.Fail("Content Stream was received from Folder Object");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
            try
            {
                deleteAndAssertObject(folder, true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
        }

        public void testContentStreamReceivingFromEmptyDocument()
        {
            if (isContentStreamRequired())
            {
                Assert.Skip("Can't create Document Object without Content Stream because Content Stream is Required. Test will be skipped...");
            }
            FileableObject document = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertDocumentTypeId(), getAndAssertRootFolder());
            document.WithoutContentStream = true;
            try
            {
                document = createAndAssertObject(document);
            }
            catch (Exception e)
            {
                Assert.Fail("Can't create Document without Content Stream input parameter. Error cause message: " + e.Message);
            }
            try
            {
                receiveAndAssertContentStream(document.ObjectId, document.ContentStream.stream);
                deleteObjectAndLogIfFailed(document, "Document without Content Stream was created but it can't be deleted");
                Assert.Fail("Content Stream was received from Empty Document");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
            try
            {
                deleteAndAssertObject(document, true);
            }
            catch (Exception e)
            {
                Assert.Fail("Can't delete created Document without Content Stream. Error cause message: " + e.Message);
            }
        }

        // TODO: streamId
        // TODO: getRenditions()

        public void testPropertiesUpdating()
        {
            string repositoryId = getAndAssertRepositoryId();
            string rootFolderId = getAndAssertRootFolder();
            FileableObject document = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertRootFolder(), getAndAssertDocumentTypeId(), null, enumVersioningState.none, false);
            assertObjectPropertiesUpdating(repositoryId, document, FileableObject.generateObjectName(false, "_Updated_Properties"));
            assertObjectPropertiesUpdating(repositoryId, new FileableObject(enumTypesOfFileableObjects.folders, getAndAssertFolderTypeId(), getAndAssertRootFolder()), FileableObject.generateObjectName(true, "_Updated_Properties"));
        }

        private void assertObjectPropertiesUpdating(string repositoryId, FileableObject objectCreator, string expectedChangedObjectName)
        {
            objectCreator = createAndAssertObject(objectCreator);
            try
            {
                assertPropertiesUpdating(repositoryId, objectCreator, expectedChangedObjectName, true);
            }
            catch (Exception e)
            {
                deleteObjectAndLogIfFailed(objectCreator, string.Empty);
                throw e;
            }
            deleteAndAssertObject(objectCreator, true);
        }

        private void assertPropertiesUpdating(string repositoryId, FileableObject currentObject, string expectedChangedName, bool createPropertiesIfNull)
        {
            logger.log("[ObjectService->updateProperties]");
            string changeToken = "";
            string objectId = currentObject.ObjectId;
            currentObject.addProperty(NAME_PROPERTY, expectedChangedName);
            object property = currentObject.removeProperty(TYPE_ID_PROPERTY);
            cmisExtensionType extension = new cmisExtensionType();
            objectServiceClient.updateProperties(repositoryId, ref objectId, ref changeToken, currentObject.getObjectProperties(createPropertiesIfNull), ref extension);
            currentObject.setId(objectId);
            currentObject.addProperty(TYPE_ID_PROPERTY, ((cmisPropertyId)property).value[0]);
            cmisPropertiesType properties = getAndAssertObjectProperties(objectId, currentObject, true);
            property = searchAndAssertPropertyByName(properties.Items, NAME_PROPERTY, false);
            Assert.AreEqual(expectedChangedName, property, "Updated Name Property='" + property + "' doesn't equal to expected Object Name='" + expectedChangedName + "'");
            logger.log("Property 'Name' was successfully updated, new value='" + expectedChangedName + "'");
            logger.log("");
        }

        public void testPropertiesUpdatingForInvalidObjectId()
        {
            try
            {
                assertPropertiesUpdating(getAndAssertRepositoryId(), new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertDocumentTypeId(), getAndAssertRootFolder()), FileableObject.generateObjectName(false, DateTime.Now.Ticks.ToString()), false);
                Assert.Fail("Properties were updated for inexistent Object");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
        }

        public void testPropertiesUpdatingWithoutProperties()
        {
            FileableObject folder = null;
            try
            {
                folder = createAndAssertFolder(getAndAssertRootFolder());
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
            try
            {
                assertPropertiesUpdating(getAndAssertRepositoryId(), new FileableObject(enumTypesOfFileableObjects.folders, folder.ObjectTypeId, folder.ObjectParentId), FileableObject.generateObjectName(false, DateTime.Now.Ticks.ToString()), false);
                deleteObjectAndLogIfFailed(folder, "Document Object was created but it can't be deleted. Error cause message: ");
                Assert.Fail("Properties Updating without Properties input parameter was performed");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
            try
            {
                deleteAndAssertObject(folder, true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
        }

        // TODO: changeToken

        public void testReadonlyPropertiesUpdating()
        {
            FileableObject folder = null;
            try
            {
                folder = createAndAssertFolder(getAndAssertRootFolder());
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
            try
            {
                string objectId = folder.ObjectId;
                string changeToken = null; // TODO: cnageToken
                cmisExtensionType extension = new cmisExtensionType();
                objectServiceClient.updateProperties(getAndAssertRepositoryId(), ref objectId, ref changeToken, folder.getObjectProperties(true), ref extension);
                folder.setId(objectId);
                deleteObjectAndLogIfFailed(folder, "Folder Object was created but it can't be deleted. Error cause message: ");
                Assert.Fail("Read Only Property was Updated");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
            try
            {
                deleteAndAssertObject(folder, true);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
        }

        public void testPropertiesUpdatingConstraintsObservance()
        {
            string objectTypeId = getAndAssertFolderTypeId();
            cmisPropertyStringDefinitionType propertyString = (findUpdatablePropertyWithLimitations(objectTypeId, false) as cmisPropertyStringDefinitionType);
            enumTypesOfFileableObjects objectType = enumTypesOfFileableObjects.folders;
            if (null == propertyString)
            {
                propertyString = (findUpdatablePropertyWithLimitations((objectTypeId = getAndAssertDocumentTypeId()), false) as cmisPropertyStringDefinitionType);
                objectType = enumTypesOfFileableObjects.documents;
            }
            FileableObject currentObject = new FileableObject(objectType, objectTypeId, getAndAssertRootFolder());
            string repositoryId = getAndAssertRepositoryId();
            string rootFolderId = getAndAssertRootFolder();
            if (null != propertyString)
            {
                try
                {
                    currentObject = createAndAssertObject(currentObject);
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
                try
                {
                    currentObject.addProperty(propertyString.id, generateTooBigPropertyValue(propertyString).ToString());
                    assertPropertiesUpdating(repositoryId, currentObject, FileableObject.generateObjectName(false, DateTime.Now.Ticks.ToString()), true);
                    deleteObjectAndLogIfFailed(currentObject, "Can't delete created Object. Error cause message: ");
                    Assert.Fail("Too big String Property was updated");
                }
                catch (Exception e)
                {
                    if (e is Assert.AssertionException)
                    {
                        throw e;
                    }

                }
                try
                {
                    deleteAndAssertObject(currentObject, true);
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
            }

            currentObject.ObjectTypeId = getAndAssertDocumentTypeId();
            cmisPropertyDefinitionType property = findUpdatablePropertyWithLimitations(currentObject.ObjectTypeId, true);
            if (null != property)
            {
                try
                {
                    currentObject = createAndAssertObject(currentObject);
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
                try
                {
                    currentObject.addProperty(property.id, null);
                    assertPropertiesUpdating(repositoryId, currentObject, FileableObject.generateObjectName(false, DateTime.Now.Ticks.ToString()), true);
                    deleteObjectAndLogIfFailed(currentObject, "Document Object was created but it can't be deleted. Error cause message: ");
                    Assert.Fail("Properties Updating with ");
                }
                catch (Exception e)
                {
                    if (e is Assert.AssertionException)
                    {
                        throw e;
                    }

                }
                try
                {
                    deleteAndAssertObject(currentObject, true);
                }
                catch (Exception e)
                {
                    Assert.Fail(e.Message);
                }
            }
        }

        public void testObjectMoving()
        {
            assertObjectMoving(ObjectStateEnum.FOLDER, ObjectStateEnum.FOLDER, false, null);
            assertObjectMoving(ObjectStateEnum.DOCUMENT, ObjectStateEnum.FOLDER, false, null);
        }

        internal enum ObjectStateEnum
        {
            NULL,
            FOLDER,
            DOCUMENT
        }

        private void assertObjectMoving(ObjectStateEnum movingObjectState, ObjectStateEnum targetObjectState, bool shouldBeFailed, string failMessage)
        {
            string rootFolderId = getAndAssertRootFolder();
            FileableObject movingObject = null;
            FileableObject targetObject = null;
            try
            {
                if (ObjectStateEnum.NULL != movingObjectState)
                {
                    movingObject = (ObjectStateEnum.FOLDER == movingObjectState) ? (createAndAssertFolder(rootFolderId)) : (createAndAssertObject(rootFolderId, null));
                }
            }
            catch (Exception e)
            {
                if (null == movingObject)
                {
                    deleteObjectAndLogIfFailed(movingObject, "Moving Object was created but it can't be deleted. Error cause message: ");
                }
                Assert.Fail(e.Message);
            }
            try
            {
                if (ObjectStateEnum.NULL != targetObjectState)
                {
                    targetObject = (ObjectStateEnum.FOLDER == targetObjectState) ? (createAndAssertFolder(rootFolderId)) : (createAndAssertObject(rootFolderId, null));
                }
            }
            catch (Exception e)
            {
                deleteObjectAndLogIfFailed(movingObject, "Moving Object was created but it can't be deleted. Error cause message: ");
                if (null != targetObject)
                {
                    deleteObjectAndLogIfFailed(targetObject, "Target Object was created but it can't be deleted. Error cause message: ");
                }
                Assert.Fail(e.Message);
            }
            bool failed = false;
            try
            {
                performAndAssertObjectMoving(movingObject, targetObject, rootFolderId);
                if (shouldBeFailed)
                {
                    deleteObjectAndLogIfFailed(movingObject, "Moving Object was created but it can't be deleted. Error cause message: ");
                    deleteObjectAndLogIfFailed(targetObject, "Target Object was created but it can't be deleted. Error cause message: ");
                    Assert.Fail(failMessage);
                }
            }
            catch (Exception e)
            {
                if (shouldBeFailed)
                {
                    if (e is Assert.AssertionException)
                    {
                        throw e;
                    }
                }
                else
                {
                    failed = true;
                    Assert.Fail(e.Message);
                }
            }
            finally
            {
                if (failed || (!failed && !shouldBeFailed))
                {
                    try
                    {
                        deleteAndAssertHierarchy(new ObjectsHierarchy(targetObject, enumTypesOfFileableObjects.any), enumUnfileObject.delete, false);
                    }
                    catch (Exception e)
                    {
                        Assert.Fail(e.Message);
                    }
                }
            }
        }

        /**
         * 
         * Move mobingObject from folder sourceObject to folder targetObject
         */
        private void performAndAssertObjectMoving(FileableObject movingObject, FileableObject targetObject, string sourceObject)
        {
            performAndAssertObjectMoving(movingObject, targetObject, sourceObject, new string[] { targetObject.ObjectId });
        }

        private void performAndAssertObjectMoving(FileableObject movingObject, FileableObject targetObject, string sourceObject, string[] parentIds)
        {
            logger.log("[ObjectService->moveObject]");
            string sourceId = sourceObject;
            logger.log("Moving Object. Moving Object Id='" + movingObject.ObjectId + ", Target Object Id='" + targetObject.ObjectId + "', Source Object Id='" + sourceId + "'");
            string objectId = (isValueNotSet(movingObject)) ? (null) : (movingObject.ObjectId);
            string targetId = (isValueNotSet(targetObject)) ? (null) : (targetObject.ObjectId);
            cmisExtensionType extension = new cmisExtensionType();
            objectServiceClient.moveObject(getAndAssertRepositoryId(), ref objectId, targetId, sourceId, ref extension);
            movingObject.setId(objectId);
            if (movingObject.IsDocument)
            {
                assertDocumentParents(movingObject.ObjectId, parentIds);
            }
            else
            {
                assertFolderParents(movingObject.ObjectId, parentIds, true);
            }
            logger.log("Document was successfully moved");
            logger.log("");
        }

        public void testObjectMovingWithInvalidMovingAndTargetObjects()
        {
            assertObjectMoving(ObjectStateEnum.DOCUMENT, ObjectStateEnum.DOCUMENT, true, "Document Object can't hold any Document Child Object");
            assertObjectMoving(ObjectStateEnum.FOLDER, ObjectStateEnum.DOCUMENT, true, "Document Object can't hold any Folder Child Object");
            assertObjectMoving(ObjectStateEnum.DOCUMENT, ObjectStateEnum.NULL, true, "Document Object was moved to Inexistent Object");
            assertObjectMoving(ObjectStateEnum.FOLDER, ObjectStateEnum.NULL, true, "Folder Object was moved to Inexistent Object");
            assertObjectMoving(ObjectStateEnum.NULL, ObjectStateEnum.FOLDER, true, "Inexistent Object was moved to Folder Object");
            assertObjectMoving(ObjectStateEnum.NULL, ObjectStateEnum.NULL, true, "Inexistent Object was moved to Inexistent Object");
        }

        public void testObjectMovingFromSourceFolder()
        {
            if (!isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Multi-Filling capability is not supported. Test will be skipped...");
            }
            string rootFolderId = getAndAssertRootFolder();
            MultifiledObject multiFilledDocument = createAndAssertMultifilledDocument(rootFolderId, 2);
            FileableObject folder = createAndAssertFolder(rootFolderId);
            try
            {
                performAndAssertObjectMoving(multiFilledDocument.DocumentObject, folder, multiFilledDocument.Parents[1].ObjectId, new string[] { multiFilledDocument.Parents[0].ObjectId, multiFilledDocument.DocumentObject.ObjectParentId, folder.ObjectId });
            }
            catch (Exception e)
            {
                deleteMultiFilledDocumentAndLogIfFailed(multiFilledDocument, string.Empty);
                deleteObjectAndLogIfFailed(folder, string.Empty);
                Assert.Fail(e.Message);
            }
            try
            {
                deleteAndAssertMultifilledDocument(new MultifiledObject(multiFilledDocument.DocumentObject, new FileableObject[] { multiFilledDocument.Parents[0], folder }));
            }
            catch (Exception e)
            {
                deleteAndAssertObject(folder, true);
                Assert.Fail(e.Message);
            }
        }

        public void testObjectMovingFromInvalidSourceFolder()
        {
            if (!isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Multi-Filling capability is not supported. Test will be skipped...");
            }
            string rootFolderId = getAndAssertRootFolder();
            FileableObject document = createAndAssertObject(rootFolderId, null);
            FileableObject folder = createAndAssertFolder(rootFolderId);
            FileableObject notParentFolder = createAndAssertFolder(rootFolderId);
            try
            {
                performAndAssertObjectMoving(document, folder, notParentFolder.ObjectId);
                Assert.Fail("Object was moved from Not Parent Folder");
            }
            catch (Exception e)
            {
                deleteAndAssertObject(document, true);
                deleteAndAssertObject(folder, true);
                deleteAndAssertObject(notParentFolder, true);
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
        }

        public void testObjectMovingToNotAllowedFolder()
        {
            string rootFolderId = getAndAssertRootFolder();
            string folderTypeIdWithAllowedList = searchForFolderTypeWithAllowingList(rootFolderId);
            if (isValueNotSet(folderTypeIdWithAllowedList))
            {
                Assert.Skip("Folder with Allowed Child Type Ids property list was not found. Test will be skipped...");
            }
            FileableObject folder = createAndAssertObject(true, rootFolderId, folderTypeIdWithAllowedList);
            cmisTypeContainer[] types = getAndAssertTypeDescendants(getAndAssertBaseDocumentTypeId(), -1, true);
            string[] allowedTypeIds = (string[])searchAndAssertPropertyByName(getAndAssertObjectProperties(folder.ObjectId, folder, true).Items, ALLOWED_CHILDREN_TYPE_IDS, true);
            string notAllowedDocumentTypeId = enumerateAndAssertTypesForAction(types, new SearchForNotAllowedDocumentTypeIdAction(allowedTypeIds), true);
            if (isValueNotSet(notAllowedDocumentTypeId))
            {
                deleteObjectAndLogIfFailed(folder, string.Empty);
                Assert.Skip("Not Allowed Document Type Id was not found. Test will be skipped...");
            }
            FileableObject document = createAndAssertObject(false, rootFolderId, notAllowedDocumentTypeId);
            try
            {
                performAndAssertObjectMoving(document, folder, null);
                deleteObjectAndLogIfFailed(document, string.Empty);
                deleteObjectAndLogIfFailed(folder, string.Empty);
                Assert.Fail("Document Object that is not in Allowed Child Type Ids list of the Folder Object was Moved to this Folder");
            }
            catch (Exception e)
            {
                try
                {
                    deleteAndAssertObject(folder, true);
                }
                catch (Exception)
                {
                    deleteAndAssertObject(document, true);
                }
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
        }

        public void testObjectDeletionWithWrongId()
        {
            try
            {
                cmisExtensionType extension = new cmisExtensionType();
                objectServiceClient.deleteObject(getAndAssertRepositoryId(), INVALID_OBJECT_ID, true, ref extension);
                Assert.Fail("Inexistent Object was deleted");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
        }

        public void testNotEmptyFolderDeletion()
        {
            FileableObject folder = createAndAssertFolder(getAndAssertRootFolder());
            FileableObject document = createAndAssertObject(folder.ObjectId, null);
            deleteAndAssertFolder(folder, true);
            deleteAndAssertObject(document, true);
            deleteAndAssertFolder(folder, false);
        }

        public void testObjectDeletionWithAllVersionsAttribute()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip("Versioning for Best Document Type is Not Allowed. Test will be skipped...");
            }
            logger.log("Testing Source Object Deletion with All Versions");
            assertVersionObjectsExistent(true);
            logger.log("Testing All Versions Leaving after Source Object Deletion");
            assertVersionObjectsExistent(false);
        }

        private void assertVersionObjectsExistent(bool afterAllVersionsDeletion)
        {
            string versionedDocumentId = createAndAssertVersionedDocument(null, 5, true);
            versionedDocumentId = createAndAssertVersionedDocument(versionedDocumentId, 5, true);
            versionedDocumentId = createAndAssertVersionedDocument(versionedDocumentId, 4, false);
            cmisObjectType[] versions = versioningServiceClient.getAllVersions(getAndAssertRepositoryId(), versionedDocumentId, ANY_PROPERTY_FILTER, false, null);
            Assert.IsFalse(isValueNotSet(versions), "No one Version Object was returned for Versioned Document");
            deleteAndAssertObject(versionedDocumentId, afterAllVersionsDeletion);
            foreach (cmisObjectType versionObject in versions)
            {
                Assert.IsFalse(isValueNotSet(versionObject), "One of the Version Objects is in 'not set' state solely");
                Assert.IsFalse(isValueNotSet(versionObject.properties), "One of the Version Object have no Properties");
                Assert.IsFalse(isValueNotSet(versionObject.properties.Items), "Properties list of one of the Version Objects is empty");
                object id = searchAndAssertPropertyByName(versionObject.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false);
                if (afterAllVersionsDeletion)
                {
                    assertObjectAbsence((string)id);
                }
                else
                {
                    getAndAssertObjectProperties(versionedDocumentId, null, false);
                }
            }
        }

        public void testTreeCreationDeletion()
        {
            FileableObject folder = createAndAssertFolder(getAndAssertRootFolder());
            ObjectsHierarchy hierarchy = createAndAssertFilesHierarchy(folder, 3, 2, 4, enumTypesOfFileableObjects.any);
            try
            {
                deleteAndAssertHierarchy(hierarchy, enumUnfileObject.delete, false);
            }
            catch (Exception e)
            {
                Assert.Fail("Tree Deletion was failed. Error cause message: " + e.Message);
            }
        }

        public void testTreeDeletionForWrongFolderId()
        {
            try
            {
                deleteAndAssertHierarchy(new ObjectsHierarchy(new FileableObject(enumTypesOfFileableObjects.folders, getAndAssertFolderTypeId(), getAndAssertRootFolder()), enumTypesOfFileableObjects.any), enumUnfileObject.delete, false);
                Assert.Fail("Inexistent Objects Tree was deleted");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }

            }
        }

        public void testTreeDeletionWithNotFolderObjectId()
        {
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null);
            try
            {
                deleteAndAssertHierarchy(new ObjectsHierarchy(document, enumTypesOfFileableObjects.any), enumUnfileObject.delete, false);
                deleteObjectAndLogIfFailed(document, string.Empty);
                Assert.Fail("Object Tree with Not Folder Object was deleted");
            }
            catch (Exception e)
            {
                if (e is Assert.AssertionException)
                {
                    throw e;
                }
                deleteAndAssertObject(document, true);

            }
        }

        public void testTreeDeletionAgainstUnfileObjectParameter()
        {
            string rootFolderId = getAndAssertRootFolder();
            ObjectsHierarchy hierarchy = createAndAssertFilesHierarchy(createAndAssertFolder(rootFolderId), 3, 2, 4, enumTypesOfFileableObjects.any);
            if (isCapabilityMultifilingEnabled())
            {
                checkSingleFiledDeletion(rootFolderId, hierarchy);
                hierarchy = createAndAssertFilesHierarchy(createAndAssertFolder(rootFolderId), 3, 2, 4, enumTypesOfFileableObjects.any);
            }
            string[] undeletedIds = null;
            if (isCapabilityUnfilingEnabled())
            {
                try
                {
                    undeletedIds = deleteAndAssertHierarchy(hierarchy, enumUnfileObject.unfile, false);
                }
                catch (Exception e)
                {
                    deleteHierarchyAndLogIfFail(hierarchy);
                    Assert.Fail("Unfilling capability is Allowed but Documents and Folders hierarchy can't be deleted with Unfile input parameter. Error cause message: " + e.Message);
                }
                Assert.IsTrue(isValueNotSet(undeletedIds), "Unfilling is allowed but some Objects were not Deleted/Unfilled");
            }
        }

        // TODO: Add capability of testing multi-filled document deletion with primary parent in hierarchy
        private void checkSingleFiledDeletion(string rootFolderId, ObjectsHierarchy hierarchy)
        {
            string[] folders = hierarchy.FolderIds.ToArray();
            string repositoryId = getAndAssertRepositoryId();
            FileableObject folder = createAndAssertFolder(rootFolderId);
            string[] multiFilledDocuments = new string[folders.Length];
            for (int i = 0; i < folders.Length; i++)
            {
                FileableObject document = null;
                try
                {
                    document = createAndAssertObject(folder.ObjectId, null);
                }
                catch (Exception e)
                {
                    deleteHierarchyAndLogIfFail(new ObjectsHierarchy(folder, enumTypesOfFileableObjects.any));
                    deleteHierarchyAndLogIfFail(hierarchy);
                    Assert.Fail(e.Message);
                }
                multiFilledDocuments[i] = document.ObjectId;
                try
                {
                    cmisExtensionType extension = new cmisExtensionType();
                    multifilingServiceClient.addObjectToFolder(repositoryId, document.ObjectId, folders[i], true, ref extension);
                }
                catch (Exception e)
                {
                    deleteHierarchyAndLogIfFail(new ObjectsHierarchy(folder, enumTypesOfFileableObjects.any));
                    deleteHierarchyAndLogIfFail(hierarchy);
                    Assert.Fail("Object with Id='" + folders[i] + "' can't be added to another Folder Object. Error cause message: " + e.Message);
                }
            }
            string[] undeletedIds = null;
            try
            {
                undeletedIds = deleteAndAssertHierarchy(hierarchy, enumUnfileObject.deletesinglefiled, true);
            }
            catch (Exception e)
            {
                deleteHierarchyAndLogIfFail(new ObjectsHierarchy(folder, enumTypesOfFileableObjects.any));
                deleteHierarchyAndLogIfFail(hierarchy);
                Assert.Fail("Can't delete Documents and Folders hierarchy with Delete Single Filed input parameter. Error cause message: " + e.Message);
            }
            Assert.IsTrue(isValueNotSet(undeletedIds) || (undeletedIds.Length < 1), "Multi-filling capability is Allowed but hierarchy with Multi-filled Documents and Delete Single Filled input parameter was not deleted");
            cmisObjectType[] objects = null;
            cmisObjectInFolderListType response = null;
            try
            {
                response = navigationServiceClient.getChildren(repositoryId, folder.ObjectId, ANY_PROPERTY_FILTER, null, false, enumIncludeRelationships.none, null, false, "0", "0", null);
            }
            catch (Exception e)
            {
                deleteHierarchyAndLogIfFail(new ObjectsHierarchy(folder, enumTypesOfFileableObjects.any));
                Assert.Fail("It is impossible to Receive Children Multi-filled Objects. Error cause message: " + e.Message);
            }
            Assert.IsNotNull(response, "Response is null");
            Assert.IsNotNull(response.objects, "Response is empty");
            objects = new cmisObjectType[response.objects.Length];
            for (int i = 0; i < response.objects.Length; ++i)
            {
                objects[i] = response.objects[i] != null ? response.objects[i].@object : null;
            }
            assertObjectCollectionsConsitence(objects, multiFilledDocuments);
            deleteAndAssertHierarchy(new ObjectsHierarchy(folder, enumTypesOfFileableObjects.any), enumUnfileObject.delete, false);
        }

        public void testContentStreamSending()
        {
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.none);
            if (!isContentStreamAllowed())
            {
                try
                {
                    setAndAssertNewContent(REPLACED_CONTENT_ENTRY, document.ObjectName, document.ObjectId, true, false, enumServiceException.streamNotSupported, "Content Stream is not Allowed for Document Type but it was set without errors");
                }
                catch (Exception e)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    throw e;
                }
            }
            logger.log("Setting new Content Stream to Document");
            setAndAssertNewContent(REPLACED_CONTENT_ENTRY, document.ObjectName, document.ObjectId, true, true, null, string.Empty);
            logger.log("New Content Stream was set successfully");
            logger.log("");
            logger.log("Deleting created Document");
            deleteAndAssertObject(document, true);
            logger.log("Document was successfully deleted");
        }

        private void setAndAssertNewContent(string textualContent, string documentName, string documentId, bool overrideContent, bool failOnException, enumServiceException expectedException, string message)
        {
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>(new enumServiceException[] { expectedException });
            setAndAssertNewContent(textualContent, documentName, documentId, overrideContent, failOnException, expectedExceptions, message);
        }

        private void setAndAssertNewContent(string textualContent, string documentName, string documentId, bool overrideContent, bool failOnException, HashSet<enumServiceException> expectedExceptions, string message)
        {
            try
            {
                byte[] replacedContent = (isValueNotSet(textualContent)) ? (null) : (Encoding.GetEncoding(FileableObject.DEFAULT_ENCODING).GetBytes(textualContent));
                logger.log("[ObjectService->setContentStream]");
                logger.log("Sending new Content to Document with Id='" + documentId + "'. New Content='" + textualContent + "'");
                // TODO: changeToken
                cmisExtensionType extension = new cmisExtensionType();
                string changeToken = "";
                objectServiceClient.setContentStream(getAndAssertRepositoryId(), ref documentId, overrideContent, ref changeToken, FileableObject.createCmisDocumentContent(documentName, replacedContent), ref extension);
                if (!failOnException)
                {
                    try
                    {
                        deleteAndAssertObject(documentId, true);
                    }
                    catch (Exception e)
                    {
                        logger.log(e.Message);
                    }
                    Assert.Fail(message);
                }
                receiveAndAssertContentStream(documentId, replacedContent);
            }
            catch (FaultException<cmisFaultType> e)
            {
                if (failOnException)
                {
                    Assert.Fail(e.Message);
                }
                else
                {
                    assertException(e, expectedExceptions);
                }
            }
        }

        public void testContentStreamSendingToInvalidDocument()
        {
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>(new enumServiceException[] { enumServiceException.invalidArgument, enumServiceException.objectNotFound });
            setAndAssertNewContent(REPLACED_CONTENT_ENTRY, string.Empty, INVALID_OBJECT_ID, true, false, expectedExceptions, "New Content was set to Inexistent Document Object");
        }

        public void testUndefinedContentStreamSending()
        {
            if (!isContentStreamAllowed())
            {
                Assert.Skip("Content Stream is Not Allowed for current Document Type");
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null);
            if (isContentStreamRequired())
            {
                try
                {
                    setAndAssertNewContent(null, document.ObjectName, document.ObjectId, true, false, enumServiceException.storage, "Content Stream is Required for Document Type but undefined Content Stream was Accepted without errors");
                }
                catch (Exception e)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    throw e;
                }
            }
        }

        public void testContentStreamSendingWithFalseOverrideFlag()
        {
            if (!isContentStreamAllowed())
            {
                Assert.Skip("Content Stream is Not Allowed for current Document Type");
            }
            FileableObject document = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertRootFolder(), getAndAssertDocumentTypeId(), null, enumVersioningState.none, false);
            if (!isContentStreamRequired())
            {
                document.WithoutContentStream = true;
                document = createAndAssertObject(document);
                try
                {
                    setAndAssertNewContent(REPLACED_CONTENT_ENTRY, document.ObjectName, document.ObjectId, false, true, null, string.Empty);
                }
                finally
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                }
            }
            document.WithoutContentStream = false;
            document.SetContentStreamForcibly = true;
            document = createAndAssertObject(document);
            try
            {
                setAndAssertNewContent(REPLACED_CONTENT_ENTRY, document.ObjectName, document.ObjectId, false, false, enumServiceException.contentAlreadyExists, "Override Flag is 'false' but Content Stream was overridden without errors");
            }
            finally
            {
                deleteObjectAndLogIfFailed(document, string.Empty);
            }
        }

        public void testContentStreamDeletion()
        {
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null);
            cmisExtensionType extension = new cmisExtensionType();
            string changeToken = "";
            if (!isContentStreamAllowed())
            {
                try
                {
                    string documentId = document.ObjectId;
                    // TODO: changeToken
                    objectServiceClient.deleteContentStream(getAndAssertRepositoryId(), ref documentId, ref changeToken, ref extension);
                    document.setId(documentId);
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    Assert.Fail("Content Stream is Not Allowed for Document Type but Deletion was performed");
                }
                catch (FaultException<cmisFaultType> e)
                {
                    try
                    {
                        assertException(e, new HashSet<enumServiceException>(new enumServiceException[] { enumServiceException.invalidArgument, enumServiceException.objectNotFound, enumServiceException.runtime }));
                    }
                    catch (Exception e1)
                    {
                        deleteObjectAndLogIfFailed(document, string.Empty);
                        throw e1;
                    }
                }
            }
            else
            {
                string documentId = document.ObjectId;
                // TODO: changeToken
                try
                {
                    objectServiceClient.deleteContentStream(getAndAssertRepositoryId(), ref documentId, ref changeToken, ref extension);
                }
                catch (Exception e)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    Assert.Fail(e.Message);
                }
                Assert.IsNotNull(documentId, "Document Id is undefined after Contet deletion");
            }
            deleteAndAssertObject(document, true);
        }

        public void testContentStreamDeletionFromInvalidDocument()
        {
            try
            {
                string documentId = INVALID_OBJECT_ID;
                // TODO: changeToken
                cmisExtensionType extension = new cmisExtensionType();
                string changeToken = "";
                objectServiceClient.deleteContentStream(getAndAssertRepositoryId(), ref documentId, ref changeToken, ref extension);
                Assert.Fail("Content was deleted from inexistent Document Object");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, new HashSet<enumServiceException>(new enumServiceException[] { enumServiceException.invalidArgument, enumServiceException.objectNotFound }));
            }
        }

        public void testContentStreamDeletionWhenItIsRequired()
        {
            if (!isContentStreamRequired())
            {
                Assert.Skip("Content Stream is Not Required for Document Type");
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null);
            try
            {
                string documentId = document.ObjectId;
                cmisExtensionType exstension = new cmisExtensionType();
                string changeToken = null;
                objectServiceClient.deleteContentStream(getAndAssertRepositoryId(), ref documentId, ref changeToken, ref exstension);
                document.setId(documentId);
                Assert.Fail("Content Stream was deleted from Document Object which Type Requires Content Stream");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.constraint);
            }
            finally
            {
                deleteAndAssertObject(document, true);
            }
        }

        public void testInvalidRepositoryIdUsing()
        {
            string parentFolderId = getAndAssertRootFolder();
            try
            {
                cmisExtensionType extension = new cmisExtensionType();
                logger.log("");
                logger.log("[ObjectService->createDocument]");
                string id = objectServiceClient.createDocument(INVALID_REPOSITORY_ID, FileableObject.addPropertyToObject(null, TYPE_ID_PROPERTY, getAndAssertDocumentTypeId()), parentFolderId, FileableObject.createCmisDocumentContent(string.Empty, FileableObject.getContentEtry()), null, null, null, null, ref extension);
                try
                {
                    deleteAndAssertObject(id);
                }
                catch (Exception e)
                {
                    logger.log(e.Message);
                }
                Assert.Fail("Document was created in Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.invalidArgument);
            }
            try
            {
                cmisExtensionType extension = new cmisExtensionType();
                logger.log("");
                logger.log("[ObjectService->createFolder]");
                string id = objectServiceClient.createFolder(INVALID_REPOSITORY_ID, FileableObject.addPropertyToObject(null, TYPE_ID_PROPERTY, getAndAssertFolderTypeId()), parentFolderId, null, null, null, ref extension);
                try
                {
                    deleteAndAssertObject(id);
                }
                catch (Exception e)
                {
                    logger.log(e.Message);
                }
                Assert.Fail("Folder was created in Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.invalidArgument);
            }
            // TODO: createPolicy
            RelationshipObject relationship = null;
            try
            {
                string relationshipTypeId = getAndAssertRelationshipTypeId();
                relationship = createAndAssertRelationship(relationshipTypeId, parentFolderId, false, false, true);
                cmisPropertiesType properties = FileableObject.addPropertyToObject(null, TYPE_ID_PROPERTY, relationshipTypeId);
                FileableObject.addPropertyToObject(properties, SOURCE_OBJECT_ID, relationship.SourceObject.ObjectId);
                FileableObject.addPropertyToObject(properties, TARGET_OBJECT_ID, relationship.TargetObject.ObjectId);
                cmisExtensionType extension = new cmisExtensionType();
                logger.log("");
                logger.log("[ObjectService->createRelationship]");
                string id = objectServiceClient.createRelationship(INVALID_REPOSITORY_ID, FileableObject.addPropertyToObject(null, TYPE_ID_PROPERTY, relationshipTypeId), null, null, null, ref extension);
                relationship.setRelationshipId(id);
                deleteObjectAndLogIfFailed(relationship, string.Empty);
                Assert.Fail("Relationship was created in Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                try
                {
                    assertException(e, enumServiceException.invalidArgument);
                }
                catch (Exception e1)
                {
                    deleteObjectAndLogIfFailed(relationship.SourceObject, "Relationship Source Object can't be deleted. Error cause message: ");
                    deleteObjectAndLogIfFailed(relationship.TargetObject, "Relationship Target Object can't be deleted. Error cause message: ");
                    throw e1;
                }
            }
            FileableObject document = createAndAssertObject(parentFolderId, null);
            try
            {
                logger.log("");
                logger.log("[ObjectService->getAllowableActions]");
                objectServiceClient.getAllowableActions(INVALID_REPOSITORY_ID, document.ObjectId, null);
                deleteObjectAndLogIfFailed(document, string.Empty);
                Assert.Fail("Allowable Actions were received from Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                try
                {
                    assertException(e, enumServiceException.invalidArgument);
                }
                catch (Exception e1)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    throw e1;
                }
            }
            try
            {
                // TODO: streamId
                logger.log("");
                logger.log("[ObjectService->getContentStream]");
                objectServiceClient.getContentStream(INVALID_OBJECT_ID, document.ObjectId, null, null, null, null);
                deleteObjectAndLogIfFailed(document, string.Empty);
                Assert.Fail("Content Stream was received from Document Object located in Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                try
                {
                    assertException(e, enumServiceException.invalidArgument);
                }
                catch (Exception e1)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    throw e1;
                }
            }
            try
            {
                logger.log("");
                logger.log("[ObjectService->getProperties]");
                objectServiceClient.getProperties(INVALID_REPOSITORY_ID, document.ObjectId, ANY_PROPERTY_FILTER, null);
                deleteObjectAndLogIfFailed(document, string.Empty);
                Assert.Fail("Document Object Properties were received from Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                try
                {
                    assertException(e, enumServiceException.invalidArgument);
                }
                catch (Exception e1)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    throw e1;
                }
            }
            string documentId = document.ObjectId;
            try
            {
                // TODO: changeToken
                string changeToken = null;
                cmisExtensionType extension = new cmisExtensionType();
                logger.log("");
                logger.log("[ObjectService->updateProperties]");
                objectServiceClient.updateProperties(INVALID_REPOSITORY_ID, ref documentId, ref changeToken, FileableObject.addPropertyToObject(null, NAME_PROPERTY, FileableObject.generateObjectName(false, DateTime.Now.Ticks.ToString())), ref extension);
                document.setId(documentId);
                deleteObjectAndLogIfFailed(document, string.Empty);
                Assert.Fail("Properties were updated for Document Object from Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                try
                {
                    assertException(e, enumServiceException.invalidArgument);
                }
                catch (Exception e1)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    throw e1;
                }
            }
            try
            {
                // TODO: changeToken
                string changeToken = "";
                cmisExtensionType extension = new cmisExtensionType();
                logger.log("");
                logger.log("[ObjectService->deleteContentStream]");
                objectServiceClient.deleteContentStream(INVALID_REPOSITORY_ID, ref documentId, ref changeToken, ref extension);
                document.setId(documentId);
                deleteObjectAndLogIfFailed(document, string.Empty);
                Assert.Fail("Content Stream was deleted from Document located in Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                try
                {
                    assertException(e, enumServiceException.invalidArgument);
                }
                catch (Exception e1)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    throw e1;
                }
            }
            try
            {
                cmisExtensionType extension = new cmisExtensionType();
                string changeToken = "";
                logger.log("");
                logger.log("[ObjectService->setContentStream]");
                objectServiceClient.setContentStream(INVALID_REPOSITORY_ID, ref documentId, true, ref changeToken, FileableObject.createCmisDocumentContent(string.Empty, FileableObject.getContentEtry()), ref extension);
                document.setId(documentId);
                deleteObjectAndLogIfFailed(document, string.Empty);
                Assert.Fail("Content Stream was sent to Document Object in Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                try
                {
                    assertException(e, enumServiceException.invalidArgument);
                }
                catch (Exception e1)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    throw e1;
                }
            }
            try
            {
                cmisExtensionType extension = new cmisExtensionType();
                logger.log("");
                logger.log("[ObjectService->getFolderByPath]");
                objectServiceClient.getObjectByPath(INVALID_REPOSITORY_ID, "/", ANY_PROPERTY_FILTER, false, enumIncludeRelationships.none, null, false, false, extension);
                Assert.Fail("Root Folder was received By Path from Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.invalidArgument);
            }
            FileableObject folder = createAndAssertFolder(parentFolderId);
            try
            {
                cmisExtensionType extension = new cmisExtensionType();
                logger.log("");
                logger.log("[ObjectService->moveObject]");
                objectServiceClient.moveObject(INVALID_REPOSITORY_ID, ref documentId, folder.ObjectId, null, ref extension);
                deleteObjectAndLogIfFailed(document, string.Empty);
                deleteObjectAndLogIfFailed(folder, string.Empty);
                Assert.Fail("Document Object was moved to another Folder Object in Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                try
                {
                    assertException(e, enumServiceException.invalidArgument);
                }
                catch (Exception e1)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    deleteObjectAndLogIfFailed(folder, string.Empty);
                    throw e1;
                }
            }
            try
            {
                cmisExtensionType extension = new cmisExtensionType();
                logger.log("");
                logger.log("[ObjectService->deleteObject]");
                objectServiceClient.deleteObject(INVALID_REPOSITORY_ID, document.ObjectId, true, ref extension);
                deleteObjectAndLogIfFailed(document, string.Empty);
                Assert.Fail("Document Object was deleted from Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                try
                {
                    assertException(e, enumServiceException.invalidArgument);
                }
                catch (Exception e1)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    throw e1;
                }
            }
            try
            {
                logger.log("");
                logger.log("[ObjectService->deleteTree]");
                objectServiceClient.deleteTree(INVALID_REPOSITORY_ID, folder.ObjectId, true, enumUnfileObject.delete, true, null);
                deleteObjectAndLogIfFailed(document, string.Empty);
                deleteObjectAndLogIfFailed(folder, string.Empty);
                Assert.Fail("Objects Tree was deleted in Invalid Repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                try
                {
                    assertException(e, enumServiceException.invalidArgument);
                }
                catch (Exception e1)
                {
                    deleteObjectAndLogIfFailed(document, string.Empty);
                    deleteObjectAndLogIfFailed(folder, string.Empty);
                    throw e1;
                }
            }
            deleteAndAssertObject(document, true);
            deleteAndAssertObject(folder, true);
        }
    }
}
