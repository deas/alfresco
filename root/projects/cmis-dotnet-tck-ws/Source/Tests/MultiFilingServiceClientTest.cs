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
using System.Collections.Generic;
using WcfCmisWSTests.CmisServices;
using System.ServiceModel;

namespace WcfCmisWSTests
{
    ///
    /// author: Dmitry Velichkevich
    ///
    public class MultiFilingServiceClientTest : BaseServiceClientTest
    {
        public void testAddingAndRemovingObjectToFolder()
        {
            string rootFolderId = getAndAssertRootFolder();
            MultifiledObject multifilledDocument = null;
            if (!isCapabilityMultifilingEnabled())
            {
                try
                {
                    multifilledDocument = createAndAssertMultifilledDocument(rootFolderId, 10);
                    deleteMultiFilledDocumentAndLogIfFailed(multifilledDocument, "Multi-filled Document was created with Not Allowed Multi-filling capability but it can't be deleted. Error cause message: ");
                    Assert.Fail("Multi-filled Document Object was created with Not Allowed Multi-filling capability");
                }
                catch (FaultException<cmisFaultType> e)
                {
                    assertException(e, enumServiceException.notSupported);
                }
            }
            else
            {
                multifilledDocument = createAndAssertMultifilledDocument(rootFolderId, 10);
                deleteAndAssertMultifilledDocument(multifilledDocument);
            }
        }

        public void testAddingInvalidObjectToFolder()
        {
            if (!isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Multi-filing capability is not supported! Test will be skipped...");
            }
            try
            {
                cmisExtensionType extensions = new cmisExtensionType();
                multifilingServiceClient.addObjectToFolder(getAndAssertRepositoryId(), INVALID_OBJECT_ID, getAndAssertRootFolder(), false, ref extensions);
                Assert.Fail("Inexistent Object was added to Folder Object");
            }
            catch (FaultException<cmisFaultType> e)
            {
                HashSet<enumServiceException> exceptions = new HashSet<enumServiceException>();
                exceptions.Add(enumServiceException.invalidArgument);
                exceptions.Add(enumServiceException.objectNotFound);
                assertException(e, exceptions);
            }
        }

        public void testAddingObjectToInvalidFolder()
        {
            if (!isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Multi-filing capability is not supported! Test will be skipped...");
            }
            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), null);
            try
            {
                cmisExtensionType extensions = new cmisExtensionType();
                multifilingServiceClient.addObjectToFolder(getAndAssertRepositoryId(), document.ObjectId, INVALID_OBJECT_ID, false, ref extensions);
                deleteObjectAndLogIfFailed(document, "Document Object was created but it can't be deleted. Error cause message: ");
                Assert.Fail("Document Object was added to Inexistent Folder Object");
            }
            catch (Exception e)
            {
                deleteAndAssertObject(document, true);
                if (e is FaultException<cmisFaultType>)
                {
                    HashSet<enumServiceException> exceptions = new HashSet<enumServiceException>();
                    exceptions.Add(enumServiceException.invalidArgument);
                    exceptions.Add(enumServiceException.objectNotFound);
                    assertException((FaultException<cmisFaultType>)e, exceptions);
                }
                else
                {
                    throw e;
                }
            }
        }

        public void testAddingFolderObjectToFolder()
        {
            if (!isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Multi-filing capability is not supported! Test will be skipped...");
            }
            string rootFolderId = getAndAssertRootFolder();
            FileableObject parentFolder = createAndAssertFolder(rootFolderId);
            FileableObject childFolder = createAndAssertFolder(rootFolderId);
            try
            {
                cmisExtensionType extensions = new cmisExtensionType();
                multifilingServiceClient.addObjectToFolder(getAndAssertRepositoryId(), childFolder.ObjectId, parentFolder.ObjectId, false, ref extensions);
                deleteObjectAndLogIfFailed(childFolder, "Folder Object was created but it can't be deleted. Error cause message: ");
                deleteObjectAndLogIfFailed(parentFolder, "Folder Object was created but it can't be deleted. Error cause message: ");
                Assert.Fail("Folder Object can't be Multi-filled");
            }
            catch (Exception e)
            {
                if (e is FaultException<cmisFaultType>)
                {
                    deleteAndAssertObject(parentFolder, true);
                    deleteAndAssertObject(childFolder, true);
                    HashSet<enumServiceException> expected = new HashSet<enumServiceException>();
                    expected.Add(enumServiceException.invalidArgument);
                    expected.Add(enumServiceException.notSupported);
                    expected.Add(enumServiceException.storage);
                    assertException((FaultException<cmisFaultType>)e, expected);
                }
                else
                {
                    throw e;
                }
            }
        }

        public void testAddingObjectToFolderConstraintsObservance()
        {
            if (!isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Multi-filing capability is not supported! Test will be skipped...");
            }
            string folderTypeWithAllowedList = searchForFolderTypeWithAllowingList(getAndAssertRootFolder());
            FileableObject folder = (null == folderTypeWithAllowedList) ? (null) : (createAndAssertObject(true, getAndAssertRootFolder(), folderTypeWithAllowedList));
            if (!isValueNotSet(folder))
            {
                string[] allowedTypeIds = (string[])searchAndAssertPropertyByName(getAndAssertObjectProperties(folder.ObjectId, folder, true).Items, ALLOWED_CHILDREN_TYPE_IDS, true);
                cmisTypeContainer[] typeDefs = getAndAssertTypeDescendants(getAndAssertBaseDocumentTypeId(), -1, true);
                string notAllowedDocumentTypeId = enumerateAndAssertTypesForAction(typeDefs, new SearchForNotAllowedDocumentTypeIdAction(allowedTypeIds), true);
                if (!isValueNotSet(notAllowedDocumentTypeId))
                {
                    FileableObject document = createAndAssertObject(false, getAndAssertRootFolder(), notAllowedDocumentTypeId);
                    try
                    {
                        cmisExtensionType extensions = new cmisExtensionType();
                        multifilingServiceClient.addObjectToFolder(getAndAssertRepositoryId(), document.ObjectId, folder.ObjectId, false, ref extensions);
                        deleteObjectAndLogIfFailed(document, ("Document with Restricted by " + folderTypeWithAllowedList + " Folder Object Type Id was created but it can't be deleted. Error cause message: "));
                        deleteObjectAndLogIfFailed(folder, ("Folder Object with " + folderTypeWithAllowedList + " Type Id was created but it can't be deleted. Error cause message: "));
                        Assert.Fail("Object was added to Folder that is not Allow Children Objects with " + notAllowedDocumentTypeId + " Type Id");
                    }
                    catch (Exception e)
                    {
                        try
                        {
                            deleteAndAssertObject(document, true);
                        }
                        catch (Exception e1)
                        {
                            deleteAndAssertObject(folder, true);
                            throw e1;
                        }
                        if (e is FaultException<cmisFaultType>)
                        {
                            assertException((FaultException<cmisFaultType>)e, enumServiceException.constraint);
                        }
                        else
                        {
                            throw e;
                        }
                    }
                }
                deleteAndAssertObject(folder, true);
            }
        }

        public void testRemovingInvalidObjectFromFolder()
        {
            if (!isCapabilityUnfilingEnabled() && !isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Un-Filling and Multi-filling capabilities are not supported. Test will be skipped...");
            }
            try
            {
                cmisExtensionType extensions = new cmisExtensionType();
                multifilingServiceClient.removeObjectFromFolder(getAndAssertRepositoryId(), INVALID_OBJECT_ID, getAndAssertRootFolder(), ref extensions);
                Assert.Fail("Inexistent Object was removed from folder");
            }
            catch (FaultException<cmisFaultType> e)
            {
                HashSet<enumServiceException> exceptions = new HashSet<enumServiceException>();
                exceptions.Add(enumServiceException.invalidArgument);
                exceptions.Add(enumServiceException.objectNotFound);
                assertException(e, exceptions);
            }
        }

        public void testRemovingObjectFromFolderAgainsUnfilingCapability()
        {
            string rootFolderId = getAndAssertRootFolder();
            if (!isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Multi-Filing capability is not supported");
            }
            MultifiledObject multiFilledDocument = createAndAssertMultifilledDocument(rootFolderId, 1);
            cmisExtensionType extensions = new cmisExtensionType();
            if (!isCapabilityUnfilingEnabled())
            {
                logger.log("Un-Filing capability is not supported!");
                try
                {
                    multifilingServiceClient.removeObjectFromFolder(getAndAssertRepositoryId(), multiFilledDocument.DocumentObject.ObjectId, null, ref extensions);
                    deleteMultiFilledDocumentAndLogIfFailed(multiFilledDocument, "Multi-filled Document was created but it can't be deleted. Error cause message: ");
                    Assert.Fail("Un-Filing capability is not supported but Object was removed from all Parent Folder Objects");
                }
                catch (Exception e)
                {
                    deleteAndAssertMultifilledDocument(multiFilledDocument);
                    if (e is FaultException<cmisFaultType>)
                    {
                        assertException((FaultException<cmisFaultType>)e, null);
                    }
                    else
                    {
                        throw e;
                    }
                }
            }
            else
            {
                logger.log("Un-Filing is supported!");
                multifilingServiceClient.removeObjectFromFolder(getAndAssertRepositoryId(), multiFilledDocument.DocumentObject.ObjectId, null, ref extensions);
                deleteAndAssertMultifilledDocument(multiFilledDocument);
            }
        }

        public void testDeletingOfMultiFilledObject()
        {
            if (!isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Multi-Filing capability is not supported");
            }
            MultifiledObject document = createAndAssertMultifilledDocument(getAndAssertRootFolder(), 5);
            deleteAndAssertObject(document.DocumentObject, true);
            foreach (CmisObject parent in document.Parents)
            {
                deleteAndAssertObject(parent, false);
            }
        }

        public void testAddingObjectWithAllVersions()
        {
            if (!isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Multi-Filing capability is not supported");
            }
            if (!isVersioningAllowed())
            {
                Assert.Skip("Versioning is not supported for " + getAndAssertDocumentTypeId() + " Document Type");
            }
            MultifiledObject document = createAndAssertMultifilledDocument(getAndAssertRootFolder(), 5);
            string versionedMultiFilledDocument = createAndAssertVersionedDocument(document.DocumentObject.ObjectId, 5, true);
            FileableObject folder = createAndAssertFolder(getAndAssertRootFolder());
            cmisExtensionType extensions = new cmisExtensionType();
            multifilingServiceClient.addObjectToFolder(getAndAssertRepositoryId(), document.DocumentObject.ObjectId, folder.ObjectId, true, ref extensions);
            cmisObjectInFolderListType children = getAndAssertChildren(folder.ObjectId, "*", null, 10L, 0L, null);
            if (!isVersionSpecificFiling())
            {
                logger.log("Children were received. Version Specific Filing is not supported. 5 Versions were created for document");
                Assert.AreEqual(1, children.objects.Length, "Amount of Children more than 1");
            }
            else
            {
                logger.log("Children were received. Version Specific Filing is supported. 5 Versions were created for document");
                Assert.IsTrue((6 == children.objects.Length), "Amount of Children is not equal to 6");
            }
            deleteAndAssertObject(versionedMultiFilledDocument, true);
            deleteAndAssertObject(folder, false);
            foreach (CmisObject parent in document.Parents)
            {
                deleteAndAssertObject(parent, false);
            }
        }

        public void testInvalidRepositoryIdUsing()
        {
            if (!isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Multi-filling is not supported. Test will be skipped...");
            }
            string rootFolderId = getAndAssertRootFolder();
            MultifiledObject document = createAndAssertMultifilledDocument(rootFolderId, 2);
            FileableObject folder = createAndAssertFolder(rootFolderId);
            logger.log("Invalid Repository Id using for Add Object To Folder service");
            cmisExtensionType extensions = new cmisExtensionType();
            try
            {
                multifilingServiceClient.addObjectToFolder(INVALID_REPOSITORY_ID, document.DocumentObject.ObjectId, folder.ObjectId, false, ref extensions);
                deleteMultiFilledDocumentAndLogIfFailed(document, "Multi-filled Document was created but it can't be deleted. Error cause message: ");
                deleteObjectAndLogIfFailed(folder, "Folder was created but it can't be deleted. Error cause message: ");
                Assert.Fail("Document Object was moved ignoring Invalid Repository Id");
            }
            catch (Exception e)
            {
                deleteAndAssertObject(folder, true);
                if (e is FaultException<cmisFaultType>)
                {
                    assertException((FaultException<cmisFaultType>)e, enumServiceException.invalidArgument);
                }
                else
                {
                    throw e;
                }
            }
            logger.log("Invalid Repository Id using for Remove Object From Folder service");
            try
            {
                multifilingServiceClient.removeObjectFromFolder(INVALID_REPOSITORY_ID, document.DocumentObject.ObjectId, document.Parents[0].ObjectId, ref extensions);
                deleteMultiFilledDocumentAndLogIfFailed(document, "Document was created but it can't be deleted. Error cause message: ");
                Assert.Fail("Document Object was moved ignoring Invalid Repository Id");
            }
            catch (Exception e)
            {
                if (e is FaultException<cmisFaultType>)
                {
                    assertException((FaultException<cmisFaultType>)e, enumServiceException.invalidArgument);
                }
                else
                {
                    throw e;
                }
            }
        }
    }
}
