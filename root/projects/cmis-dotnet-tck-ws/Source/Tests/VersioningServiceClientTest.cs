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
using System.ServiceModel;
using System.Collections.Generic;
using WcfCmisWSTests.CmisServices;

namespace WcfCmisWSTests
{
    ///
    /// author: Dmitry Velichkevich
    ///
    public class VersioningServiceClientTest : BaseServiceClientTest
    {
        private const string VERSION_SERIES_CHECKEDOUT_PROPERTY = "cmis:isVersionSeriesCheckedOut";
        private const string VERSION_SERIES_ID_PROPERTY = "cmis:versionSeriesId";
        private const string VERSION_LABEL_PROPERTY = "cmis:versionLabel";
        private const string CREATION_DATE_PROPERTY = "cmis:creationDate";

        private const string INVALID_FILTER = "cmis:name, cmis:objectId, *objectType*";

        private const string EXPECTED_MESSAGE = " Expected exception message: ";
        private const string VERSIONING_NOT_SUPPORTTED_MESSAGE = "Versioning is not supported for document type";
        private const string INVALID_LATEST_MAJOR_VERSION_PROPERTIES_MESSAGE = "Properties that were returned by service method are not properties of latest major version";
        private const string CONSTRAINT_EXCEPTION_WAS_NOT_THREW_MESSAGE = "Versioning is not supported for document type but expected ConstraintViolationException was not threw";

        public void testDocumentCheckoutingAndCheckoutCanceling()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null, false);
            cancelCheckOutAndAssert(checkOutAndAssert(document.ObjectId, document.ObjectTypeId, document.ContentStream.stream));
            deleteAndAssertObject(document, true);
        }

        public void testDocumentCheckoutingForNotAllowedVersioning()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            string notVersionalbeTypeId = enumerateAndAssertTypesForAction(getAndAssertTypeDescendants(getAndAssertBaseDocumentTypeId(), -1, false), new NotVersionableDocumentTypeSearcher(), true);
            if (isValueNotSet(notVersionalbeTypeId))
            {
                Assert.Skip("Not Versionable Document Type was not found");
            }
            FileableObject document = createAndAssertObject(false, getAndAssertRootFolder(), notVersionalbeTypeId, false);
            checkoutAndAssertWithExpectedException(getAndAssertRepositoryId(), document.ObjectId, true, enumServiceException.constraint, "Not Allowed Versioning capability");
            deleteAndAssertObject(document, true);
        }

        private class NotVersionableDocumentTypeSearcher : TypeAction
        {
            public override string perform(cmisTypeDefinitionType typeDefinition)
            {
                if (typeDefinition.creatable && typeDefinition.fileable && !typeDefinition.versionable)
                {
                    return typeDefinition.id;
                }
                return null;
            }
        }

        public void testInvalidDocumentCheckouting()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(enumServiceException.invalidArgument);
            expectedExceptions.Add(enumServiceException.objectNotFound);
            checkoutAndAssertWithExpectedException(getAndAssertRepositoryId(), INVALID_OBJECT_ID, false, expectedExceptions, "Invalid Object Id");
        }

        public void testCheckoutingOfCheckedOutDocument()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.major, false);
            string documentId = document.ObjectId;
            string checkedoutDocumentId = checkOutAndAssert(documentId, document.ObjectTypeId, document.ContentStream.stream);
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(enumServiceException.storage);
            expectedExceptions.Add(enumServiceException.versioning);
            checkoutAndAssertWithExpectedException(getAndAssertRepositoryId(), checkedoutDocumentId, true, expectedExceptions, "Already Checked Out Document Id");
        }

        public void testCheckoutingNotCurrentObject()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            string versionedDocument = createAndAssertVersionedDocument(null, 6, true);
            string version = versionedDocument;
            versionedDocument = createAndAssertVersionedDocument(versionedDocument, 5, false);
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(enumServiceException.updateConflict);
            expectedExceptions.Add(enumServiceException.storage);
            checkoutAndAssertWithExpectedException(getAndAssertRepositoryId(), version, false, expectedExceptions, "Object Id from Version Series");
            deleteAndAssertObject(versionedDocument, true);
        }

        public void testContentCopiedCheckOutResult()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject document = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertDocumentTypeId(), getAndAssertRootFolder());
            document.WithoutContentStream = true;
            document = createAndAssertObject(document, false);
            cancelCheckOutAndAssert(checkOutAndAssert(document.ObjectId, document.ObjectTypeId, FileableObject.getContentEtry()));
            deleteAndAssertObject(document, true);
            document.WithoutContentStream = false;
            document = (FileableObject)document.createObject(true, true, document.ObjectParentId, document.ObjectTypeId);
            cancelCheckOutAndAssert(checkOutAndAssert(document.ObjectId, document.ObjectTypeId, document.ContentStream.stream));
            deleteAndAssertObject(document, true);
        }

        public void testCheckOutCancellingForInvalidObjectId()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(enumServiceException.invalidArgument);
            expectedExceptions.Add(enumServiceException.objectNotFound);
            cancelCheckoutAndAssertWithExpectedException(getAndAssertRepositoryId(), INVALID_OBJECT_ID, expectedExceptions, "Invalid Object Id");
        }

        public void testNotCheckedOutDocumentCheckOutCancelling()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null, false);
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(enumServiceException.versioning);
            expectedExceptions.Add(enumServiceException.updateConflict);
            cancelCheckoutAndAssertWithExpectedException(getAndAssertRepositoryId(), document.ObjectId, expectedExceptions, "Not Checked Out Object Id");
            deleteAndAssertObject(document, true);
        }

        public void testCheckIn()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject documentCreator = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertRootFolder(), getAndAssertDocumentTypeId(),
                    null, enumVersioningState.checkedout, true);
            string documentId = createAndAssertObject(documentCreator, false).ObjectId;
            documentId = checkInAndAssert(documentId, false);
            documentId = checkOutAndAssert(documentId, documentCreator.ObjectTypeId, checkinContentEntry);
            documentCreator.setId(documentId);
            documentId = checkInAndAssert(documentId, true);
            deleteAndAssertObject(documentId);
        }

        public void testInvalidObjectCheckining()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            cmisContentStreamType content = null;
            if (!isPwcUpdatable() && isContentStreamRequired())
            {
                content = FileableObject.createCmisDocumentContent("test.txt", FileableObject.getContentEtry());
            }
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(enumServiceException.invalidArgument);
            expectedExceptions.Add(enumServiceException.objectNotFound);
            checkInAndAssertWithExpectedException(getAndAssertRepositoryId(), INVALID_OBJECT_ID, null, null, content, null, expectedExceptions, "Invalid Object Id");
        }

        public void testNotVersionableAndNotCheckedOutDocumentCheckining()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            string notVersionalbeTypeId = enumerateAndAssertTypesForAction(getAndAssertTypeDescendants(getAndAssertBaseDocumentTypeId(), -1, false), new NotVersionableDocumentTypeSearcher(), true);
            if (isValueNotSet(notVersionalbeTypeId))
            {
                Assert.Skip("Not Versionable Document Type was not found");
            }
            FileableObject document = createAndAssertObject(false, getAndAssertRootFolder(), notVersionalbeTypeId);
            cmisContentStreamType content = null;
            if (!isPwcUpdatable() && isContentStreamRequired())
            {
                content = FileableObject.createCmisDocumentContent("test.txt", FileableObject.getContentEtry());
            }
            checkInAndAssertWithExpectedException(getAndAssertRepositoryId(), document.ObjectId, null, null, content, CHECKIN_COMMENT, enumServiceException.versioning, "Document with Not Versionable Type");
            deleteAndAssertObject(document, true);
        }

        public void testCheckInAgainstContentStreamAcceptation()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject documentCreator = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertRootFolder(), getAndAssertDocumentTypeId(), null, enumVersioningState.checkedout, true);
            string checkedOutDocumentId = createAndAssertObject(documentCreator, false).ObjectId;
            cmisContentStreamType content = FileableObject.createCmisDocumentContent("test.txt", FileableObject.getContentEtry());
            if (!isContentStreamAllowed())
            {
                checkInAndAssertWithExpectedException(getAndAssertRepositoryId(), checkedOutDocumentId, null, null, content, CHECKIN_COMMENT, enumServiceException.streamNotSupported, "Not Allowed Content Stream");
            }
            else
            {
                if (!isPwcUpdatable() && isContentStreamRequired())
                {
                    HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
                    expectedExceptions.Add(enumServiceException.storage);
                    expectedExceptions.Add(enumServiceException.constraint);
                    checkInAndAssertWithExpectedException(getAndAssertRepositoryId(), checkedOutDocumentId, null, null, null, CHECKIN_COMMENT, expectedExceptions, "Document with Required Content Stream when PWC Updatable capability not supported and Content Stream parameter was not introduced");
                }
                else
                {
                    checkedOutDocumentId = checkInAndAssert(checkedOutDocumentId, false);
                }
            }
            cancelCheckoutAndDeleteDocumentWithAssertion(checkedOutDocumentId);
        }

        public void testNotCheckedOutObjectCheckining()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null, false);
            cmisContentStreamType content = null;
            if (!isPwcUpdatable() && isContentStreamRequired())
            {
                content = FileableObject.createCmisDocumentContent("test.txt", FileableObject.getContentEtry());
            }
            checkInAndAssertWithExpectedException(getAndAssertRepositoryId(), document.ObjectId, null, null, null, CHECKIN_COMMENT, null, "Not Checked Out Document Id");
            deleteAndAssertObject(document, true);
        }

        public void testCheckInContentAgainstPwcUpdatableCapability()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            if (!isPwcUpdatable())
            {
                Assert.Skip("PWC is Not Updatable");
            }
            if (!isContentStreamAllowed())
            {
                Assert.Skip("Content Stream is Not Allowed for Document Type");
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.checkedout, false);
            cmisContentStreamType changedContent = FileableObject.createCmisDocumentContent("Modified.txt", FileableObject.getContentEtry());
            string objectId = document.ObjectId;
            string changeToken = null; // TODO
            cmisExtensionType extensions = new cmisExtensionType();
            objectServiceClient.setContentStream(getAndAssertRepositoryId(), ref objectId, true, ref changeToken, changedContent, ref extensions);
            document.setId(objectId);
            document.setId(checkInAndAssert(objectId, true));
            cancelCheckoutAndDeleteDocumentWithAssertion(document.ObjectId);
        }

        // TODO: policies
        // TODO: addACEs
        // TODO: removeACEs

        public void testLatestVersionPropertiesReceiving()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject documentCreator = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertRootFolder(), getAndAssertDocumentTypeId(), null, enumVersioningState.major, true);
            string documentId = createAndAssertObject(documentCreator, false).ObjectId;
            documentId = createAndAssertVersionedDocument(documentId, 6, true);
            cmisPropertiesType initialMajorVersionProperties = getAndAssertLatestVersionProperties(documentId, ANY_PROPERTY_FILTER, true);
            documentId = createAndAssertVersionedDocument(documentId, 7, false);
            getAndAssertLatestVersionProperties(documentId, ANY_PROPERTY_FILTER, true);
            cmisPropertiesType secondMajorVersionProperties = getAndAssertLatestVersionProperties(documentId, ANY_PROPERTY_FILTER, true);
            assertValuesEquality(NAME_PROPERTY, searchAndAssertPropertyByName(initialMajorVersionProperties.Items, NAME_PROPERTY, false), searchAndAssertPropertyByName(secondMajorVersionProperties.Items, NAME_PROPERTY, false));
            assertValuesEquality(MAJOR_VERSION_PROPERTY, searchAndAssertPropertyByName(initialMajorVersionProperties.Items, MAJOR_VERSION_PROPERTY, false), searchAndAssertPropertyByName(secondMajorVersionProperties.Items, MAJOR_VERSION_PROPERTY, false));
            deleteAndAssertObject(documentId, true);
        }

        public void testReceivingPropertiesOfInvalidObject()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(enumServiceException.invalidArgument);
            expectedExceptions.Add(enumServiceException.objectNotFound);
            assertLatestVersionPropertiesReceivingWithExcpectedException(getAndAssertRepositoryId(), INVALID_OBJECT_ID, ANY_PROPERTY_FILTER, false, expectedExceptions, "Invalid Object Id");
        }

        public void testReceivingPropertiesOfFolderObject()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject folder = createAndAssertFolder(getAndAssertRootFolder());
            assertLatestVersionPropertiesReceivingWithExcpectedException(getAndAssertRepositoryId(), folder.ObjectId, ANY_PROPERTY_FILTER, false, enumServiceException.invalidArgument, "Folder Object Id");
            deleteAndAssertObject(folder, false);
        }

        public void testLatestVersionPropertiesReceivingWithInvalidFilter()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            string documentId = createAndAssertVersionedDocument(null, 2, true);
            assertLatestVersionPropertiesReceivingWithExcpectedException(getAndAssertRepositoryId(), documentId, INVALID_FILTER, false, enumServiceException.filterNotValid, "Invalid Properties Filter");
            deleteAndAssertObject(documentId, true);
        }

        public void testLatestVersionPropertiesReceivingWithAndWithoutMajorVersion()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.minor, false);
            assertLatestVersionPropertiesReceivingWithExcpectedException(getAndAssertRepositoryId(), document.ObjectId, ANY_PROPERTY_FILTER, true, enumServiceException.objectNotFound, "Major parameter equal to 'true' and without Major Version in Verions Series");
            document.setId(createAndAssertVersionedDocument(document.ObjectId, 3, true));
            getAndAssertLatestVersionProperties(document.ObjectId, ANY_PROPERTY_FILTER, true);
            deleteAndAssertObject(document.ObjectId);
        }

        public void testFilteringOfLatestVersionPropertiesReceiving()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            string filter = OBJECT_IDENTIFIER_PROPERTY + "," + TYPE_ID_PROPERTY + "," + NAME_PROPERTY;
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.major, false);
            getAndAssertLatestVersionProperties(document.ObjectId, filter, true);
            deleteAndAssertObject(document, true);
        }

        public void testAllVersionsReceiving()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            string versionedDocumentId = createAndAssertVersionedDocument(null, 2, true);
            versionedDocumentId = createAndAssertVersionedDocument(versionedDocumentId, 3, true);
            versionedDocumentId = createAndAssertVersionedDocument(versionedDocumentId, 2, false);
            try
            {
                assertVersionsReceiving(versionedDocumentId, ANY_PROPERTY_FILTER, false, false);
            }
            catch (Exception e)
            {
                deleteAndAssertObject(versionedDocumentId);
                throw e;
            }
        }

        public void testAllVersionsReceivingWithAllowableActions()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            string versionedDocumentId = createAndAssertVersionedDocument(null, 2, true);
            versionedDocumentId = createAndAssertVersionedDocument(versionedDocumentId, 3, true);
            versionedDocumentId = createAndAssertVersionedDocument(versionedDocumentId, 2, false);
            try
            {
                assertVersionsReceiving(versionedDocumentId, ANY_PROPERTY_FILTER, true, false);
            }
            catch (Exception e)
            {
                deleteAndAssertObject(versionedDocumentId, true);
                throw e;
            }
        }

        public void testAllVersionsReceivingWithFilteredProperties()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            string versionedDocumentId = createAndAssertVersionedDocument(null, 2, true);
            versionedDocumentId = createAndAssertVersionedDocument(versionedDocumentId, 3, true);
            versionedDocumentId = createAndAssertVersionedDocument(versionedDocumentId, 2, false);
            string filter = OBJECT_IDENTIFIER_PROPERTY + "," + TYPE_ID_PROPERTY + "," + NAME_PROPERTY;
            try
            {
                assertVersionsReceiving(versionedDocumentId, filter, false, false);
            }
            catch (Exception e)
            {
                deleteAndAssertObject(versionedDocumentId, true);
                throw e;
            }
        }

        public void testAllVersionsReceivingWithExistentPwc()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            string versionedDocumentId = createAndAssertVersionedDocument(null, 2, true);
            versionedDocumentId = createAndAssertVersionedDocument(versionedDocumentId, 3, true);
            versionedDocumentId = createAndAssertVersionedDocument(versionedDocumentId, 2, false);
            string checkedOutDocumentId = versionedDocumentId;
            try
            {
                checkedOutDocumentId = checkOutAndAssert(versionedDocumentId, getAndAssertDocumentTypeId(), checkinContentEntry);
                assertVersionsReceiving(versionedDocumentId, ANY_PROPERTY_FILTER, false, true);
            }
            catch (Exception e)
            {
                cancelCheckoutAndDeleteDocumentWithAssertion(checkedOutDocumentId);
                throw e;
            }
        }

        public void testAllVersionsReceivingForInvalidObject()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(enumServiceException.invalidArgument);
            expectedExceptions.Add(enumServiceException.objectNotFound);
            assertAllVersionsReceivingWithExpectedException(getAndAssertRepositoryId(), INVALID_OBJECT_ID, ANY_PROPERTY_FILTER, false, expectedExceptions, "Invalid Object Id");
        }

        public void testAllVersionsReceivingWithInvalidFilter()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            string versionedDocumentId = createAndAssertVersionedDocument(null, 2, true);
            assertAllVersionsReceivingWithExpectedException(getAndAssertRepositoryId(), versionedDocumentId, INVALID_FILTER, false, enumServiceException.filterNotValid, "Invalid Properties Filter");
            deleteAndAssertObject(versionedDocumentId, true);
        }

        public void testAllVersionsReceivingFromFolderObject()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject folder = createAndAssertFolder(getAndAssertRootFolder());
            assertAllVersionsReceivingWithExpectedException(getAndAssertRepositoryId(), folder.ObjectId, ANY_PROPERTY_FILTER, false, null, "Folder Object Id");
            deleteAndAssertObject(folder, false);
        }

        private void assertLatestVersionPropertiesReceivingWithExcpectedException(string repositoryId, string versionSeriesId, string filter, bool major, enumServiceException expectedException, string caseMessage)
        {
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(expectedException);
            assertLatestVersionPropertiesReceivingWithExcpectedException(repositoryId, versionSeriesId, filter, major, expectedExceptions, caseMessage);
        }

        private void assertLatestVersionPropertiesReceivingWithExcpectedException(string repositoryId, string versionSeriesId, string filter, bool major, HashSet<enumServiceException> expectedExceptions, string caseMessage)
        {
            try
            {
                logger.log("[VersioningService->getPropertiesOfLatestVersion()]");
                versioningServiceClient.getPropertiesOfLatestVersion(repositoryId, versionSeriesId, major, filter, null);
                logger.log("Expected exception during Properties of Latest Version with " + caseMessage + " was not thrown");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, expectedExceptions);
            }
            catch (Exception e)
            {
                deleteAndAssertObject(versionSeriesId);
                throw e;
            }
        }

        private void assertValuesEquality(string propertyName, object leftValue, object rightValue)
        {
            if (isValueNotSet(leftValue))
            {
                Assert.IsTrue(isValueNotSet(rightValue), (propertyName + " property value from properties first time receiving is null whereas value from second properties receiving is " + rightValue));
            }

            if (!leftValue.GetType().Equals(rightValue.GetType()))
            {
                Assert.Fail(propertyName + " property data type from properties first time receiving is not similar to data type of the property received at second time");
            }

            Assert.AreEqual(leftValue, rightValue, (propertyName + " property values from first and second time receiving are not the same"));
        }

        private void checkInAndAssertWithExpectedException(string repositoryId, string checkedoutDocumentId, Nullable<bool> major, cmisPropertiesType properties, cmisContentStreamType content, string checkInComment, enumServiceException expectedException, string caseMessage)
        {
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(expectedException);
            checkInAndAssertWithExpectedException(repositoryId, checkedoutDocumentId, major, properties, content, checkInComment, expectedExceptions, caseMessage);
        }

        private void checkInAndAssertWithExpectedException(string repositoryId, string checkedoutDocumentId, Nullable<bool> major, cmisPropertiesType properties, cmisContentStreamType content, string checkInComment, HashSet<enumServiceException> expectedExceptions, string caseMessage)
        {
            try
            {
                cmisExtensionType extensions = new cmisExtensionType();
                logger.log("[VersioningService->checkIn()]");
                // TODO: applyPolicies, addACEs, removeACEs
                versioningServiceClient.checkIn(repositoryId, ref checkedoutDocumentId, major, properties, content, checkInComment, null, null, null, ref extensions);
                logger.log("Expected exception during All Versions Receiving with " + caseMessage + " was not thrown");
            }
            catch (FaultException<cmisFaultType> e)
            {
                if (expectedExceptions != null && expectedExceptions.Count > 0)
                {
                    assertException(e, expectedExceptions);
                }
            }
            catch (Exception e)
            {
                cancelCheckoutAndDeleteDocumentWithAssertion(checkedoutDocumentId);
                throw e;
            }
        }

        private void cancelCheckoutAndDeleteDocumentWithAssertion(string checkedoutDocumentId)
        {
            logger.log("Trying to cancel checkouting and deleting document. Checkedouted Document id = " + checkedoutDocumentId);
            cmisPropertiesType properties = objectPropertiesExists(checkedoutDocumentId);
            if (!isValueNotSet(properties) && !isValueNotSet(properties.Items))
            {
                object checkedout = searchAndAssertPropertyByName(properties.Items, CHECKED_OUT_PROPERTY, false);
                object versionSerriesId = checkedoutDocumentId;
                if (!isValueNotSet(checkedout) && (bool)checkedout)
                {
                    logger.log("Object was checkouted. Trying to receive VersionSeriesId");
                    versionSerriesId = searchAndAssertPropertyByName(properties.Items, VERSION_SERIES_ID_PROPERTY, false);
                    logger.log("Trying to cancel checkouting...");
                    cancelCheckOutAndAssert(checkedoutDocumentId);
                    logger.log("Checkouting was canceled. Original Document id = " + versionSerriesId);
                }
                else
                {
                    logger.log("Checkout canceling was skipped because Document was not checkouted");
                }
                if (!isValueNotSet(versionSerriesId) && !isValueNotSet(objectPropertiesExists((string)versionSerriesId)))
                {
                    deleteAndAssertObject((string)versionSerriesId);
                }
                else
                {
                    logger.log("Document deleting was skipped because Document id was not recognized");
                }
            }
            else
            {
                logger.log("Skipping checkout canceling and object deleting because object with " + checkedoutDocumentId + " id is not exists");
            }
        }

        private cmisPropertiesType objectPropertiesExists(string objectId)
        {
            try
            {
                return getAndAssertObjectProperties(objectId, null, false);
            }
            catch (Exception)
            {
                return null;
            }
        }

        private void assertAllVersionsReceivingWithExpectedException(string repositoryId, string versionSeriesId, string filter, bool allowableActions, enumServiceException expectedException, string caseMessage)
        {
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(expectedException);
            assertAllVersionsReceivingWithExpectedException(repositoryId, versionSeriesId, filter, allowableActions, expectedExceptions, caseMessage);
        }

        private void assertAllVersionsReceivingWithExpectedException(string repositoryId, string versionSeriesId, string filter, bool allowableActions, HashSet<enumServiceException> expectedExceptions, string caseMessage)
        {
            try
            {
                logger.log("[VersioningService->getAllVersions()]");
                versioningServiceClient.getAllVersions(repositoryId, versionSeriesId, filter, allowableActions, null);
                logger.log("Expected exception during All Versions Receiving with " + caseMessage + " was not thrown");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, expectedExceptions);
            }
            catch (Exception e)
            {
                deleteAndAssertObject(versionSeriesId);
                throw e;
            }
        }

        private void checkoutAndAssertWithExpectedException(string repositoryId, string documentId, bool forceCanceling, enumServiceException expectedException, string caseMessage)
        {
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(expectedException);
            checkoutAndAssertWithExpectedException(repositoryId, documentId, forceCanceling, expectedExceptions, caseMessage);
        }

        // FIXME: [BUG] because of changing our code in DMServicePortThrowsAdvice
        private void checkoutAndAssertWithExpectedException(string repositoryId, string documentId, bool forceCanceling, HashSet<enumServiceException> expectedExceptions, string caseMessage)
        {
            try
            {
                string checkedoutDocumentId = documentId;
                cmisExtensionType extensions = new cmisExtensionType();
                logger.log("[VersioningService->checkOut()]");
                versioningServiceClient.checkOut(repositoryId, ref checkedoutDocumentId, ref extensions);
                logger.log("Expected exception during Check Outing with " + caseMessage + " was not thrown");
                if (forceCanceling)
                {
                    cancelCheckOutAndAssert(checkedoutDocumentId);
                }
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, expectedExceptions);
            }
            catch (Exception e)
            {
                deleteAndAssertObject(documentId);
                throw e;
            }
        }

        private void cancelCheckoutAndAssertWithExpectedException(string repositoryId, string documentId, enumServiceException expectedException, string caseMessage)
        {
            HashSet<enumServiceException> expectedExceptions = new HashSet<enumServiceException>();
            expectedExceptions.Add(expectedException);
            cancelCheckoutAndAssertWithExpectedException(repositoryId, documentId, expectedExceptions, caseMessage);
        }

        private void cancelCheckoutAndAssertWithExpectedException(string repositoryId, string documentId, HashSet<enumServiceException> expectedExceptions, string caseMessage)
        {
            try
            {
                cmisExtensionType extensions = new cmisExtensionType();
                logger.log("[VersioningService->cancelCheckOut()]");
                versioningServiceClient.cancelCheckOut(repositoryId, documentId, ref extensions);
                logger.log("Expected exception during Check Out Cancelling with " + caseMessage + " was not thrown");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, expectedExceptions);
            }
            catch (Exception e)
            {
                deleteAndAssertObject(documentId);
                throw e;
            }
        }

        private void assertVersionsReceiving(string documentId, string filter, bool includeAllowableActions, bool hasPwc)
        {
            logger.log("[VersioningService->getAllVersions]");
            logger.log("Receiving all versions Document with Id='" + documentId + "'");
            cmisObjectType[] response = versioningServiceClient.getAllVersions(getAndAssertRepositoryId(), documentId, filter, includeAllowableActions, null);
            Assert.IsNotNull(response, "Get All Versions response is undefined");
            assertVersions(response, filter, includeAllowableActions, hasPwc);
            logger.log("All versions of document receiving was successfully checked");
            logger.log("");
        }

        private void assertVersions(cmisObjectType[] response, string filter, bool includeAllowableActions, bool pwcIncluded)
        {
            object lastVersionLabel = "";
            object lastCreationDate = null;
            for (int i = (pwcIncluded) ? (1) : (0); i < response.Length; i++)
            {
                cmisObjectType versionObject = response[i];
                if (!isValueNotSet(filter) && !string.Empty.Equals(filter) && !ANY_PROPERTY_FILTER.Equals(filter))
                {
                    assertPropertiesByFilter(versionObject.properties, filter);
                }
                else
                {
                    object cmisObjectId = searchAndAssertPropertyByName(versionObject.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false);
                    Assert.IsFalse(isValueNotSet(cmisObjectId), "Document Version Id property is undefined");
                    object versionLabel = searchAndAssertPropertyByName(versionObject.properties.Items, VERSION_LABEL_PROPERTY, false);
                    Assert.IsFalse(isValueNotSet(versionLabel), "Invalid Version Object");
                    Assert.AreNotEqual(lastVersionLabel, versionLabel, ("Invalid Version was found in Document Object Versions collection. Dublicated Version Object with Version Label='" + versionLabel + "'"));
                    lastVersionLabel = versionLabel;
                    object creationDate = searchAndAssertPropertyByName(versionObject.properties.Items, CREATION_DATE_PROPERTY, false);
                    if (!isValueNotSet(lastCreationDate))
                    {
                        Assert.IsFalse(isValueNotSet(creationDate), "Invalid CreationDate property value");
                        Assert.IsTrue((((System.DateTime)creationDate).CompareTo(lastCreationDate) <= 0), "Versions sorting by Creation Date Descending is invalid");
                    }
                    else
                    {
                        lastCreationDate = creationDate;
                    }
                }
                if (includeAllowableActions)
                {
                    assertAllowableActions(versionObject.allowableActions);
                }
            }
            if (pwcIncluded)
            {
                object property = searchAndAssertPropertyByName(response[0].properties.Items, VERSION_SERIES_CHECKEDOUT_PROPERTY, false);
                Assert.IsTrue((!isValueNotSet(property) && (bool)property), "Document Object was Checked Out but PWC was not returned");
            }
        }

        private void assertAllowableActions(cmisAllowableActionsType allowableActions)
        {
            Assert.IsFalse(isValueNotSet(allowableActions), "Allowable actions was not received");
            Assert.IsFalse(isValueNotSet(allowableActions.canGetProperties), "Invalid allowable actions property value");
            Assert.IsTrue(allowableActions.canGetProperties, "Invalid allowable actions descriptor was returned");
        }

        public void testInvalidRepositoryIdUsing()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip(VERSIONING_NOT_SUPPORTTED_MESSAGE);
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.major, false);
            string documentId = document.ObjectId;
            checkoutAndAssertWithExpectedException(INVALID_REPOSITORY_ID, documentId, true, enumServiceException.invalidArgument, "Invalid Repository Id");
            FileableObject checkedOutDocument = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.checkedout, false);
            cancelCheckoutAndAssertWithExpectedException(INVALID_REPOSITORY_ID, checkedOutDocument.ObjectId, enumServiceException.invalidArgument, "Invalid Repository Id");
            cancelCheckoutAndDeleteDocumentWithAssertion(checkedOutDocument.ObjectId);
            checkInAndAssertWithExpectedException(INVALID_REPOSITORY_ID, checkedOutDocument.ObjectId, false, null, null, CHECKIN_COMMENT, enumServiceException.invalidArgument, "Invalid Repository Id");
            assertLatestVersionPropertiesReceivingWithExcpectedException(INVALID_REPOSITORY_ID, documentId, ANY_PROPERTY_FILTER, false, enumServiceException.invalidArgument, "Invalid Repository Id");
            assertAllVersionsReceivingWithExpectedException(INVALID_REPOSITORY_ID, documentId, ANY_PROPERTY_FILTER, false, enumServiceException.invalidArgument, "Invalid Repository Id");

            deleteAndAssertObject(document, true);
        }
    }
}
