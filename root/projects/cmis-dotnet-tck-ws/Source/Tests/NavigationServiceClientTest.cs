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
using System.Collections;
using System.ServiceModel;
using WcfCmisWSTests.CmisServices;

namespace WcfCmisWSTests
{
    ///
    /// author: Stas Sokolovsky
    ///
    public class NavigationServiceClientTest : BaseServiceClientTest
    {
        private const int MINIMAL_CHILDREN = 2;
        private const int HIERARCHY_DEPTH = 2;
        private const int FOLDERS_DEPTH = 4;
        private const int MAXIMUM_CHILDREN = 4;
        private const int CHILDREN_AMOUNT = 2;
        private const int MAXIMUM_CHECKEDOUT_DOCS = 5;
        private const int MAX_REQUESTED_COUNT = 300;

        public void testDescendantsReceiving()
        {
            getAndAssertDescedansts(enumTypesOfFileableObjects.any, -1, ANY_PROPERTY_FILTER, enumVersioningState.none);
        }

        public void testDepthLimitedDescedantsReceving()
        {
            getAndAssertDescedansts(enumTypesOfFileableObjects.any, HIERARCHY_DEPTH, ANY_PROPERTY_FILTER, enumVersioningState.none);
        }

        public void testFilteredDescedantsReceving()
        {
            getAndAssertDescedansts(enumTypesOfFileableObjects.any, HIERARCHY_DEPTH, TYPE_ID_PROPERTY + "," + NAME_PROPERTY + "," + OBJECT_IDENTIFIER_PROPERTY, enumVersioningState.none);
        }

        public void testDescedantsRecevingIncludeRenditions()
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
            FileableObject rootFolder = createAndAssertFolder(getAndAssertRootFolder());
            TreeNode<string> objectsTree = createObjectsTree(rootFolder.ObjectId, enumVersioningState.none, enumTypesOfFileableObjects.any, FOLDERS_DEPTH, MINIMAL_CHILDREN, MAXIMUM_CHILDREN, -1);
            foreach (RenditionData renditionData in testRenditions)
            {
                getAndAssertDescedansts(rootFolder, objectsTree, enumTypesOfFileableObjects.any, -1, ANY_PROPERTY_FILTER, enumVersioningState.none, renditionData);
            }

            objectServiceClient.deleteTree(getAndAssertRepositoryId(), rootFolder.ObjectId, true, enumUnfileObject.delete, true, null);
            deleteAndAssertObject(document, true);
        }

        public void testVersioningDescendantsReceiving()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip("Versioning is disabled for current document type");
            }
            getAndAssertDescedansts(enumTypesOfFileableObjects.any, -1, ANY_PROPERTY_FILTER, enumVersioningState.major);
        }

        public void testFoldersTreeReceving()
        {
            getAndAssertDescedansts(enumTypesOfFileableObjects.folders, -1, ANY_PROPERTY_FILTER, enumVersioningState.none);
        }

        public void testDepthLimitedFoldersTreeReceving()
        {
            getAndAssertDescedansts(enumTypesOfFileableObjects.folders, HIERARCHY_DEPTH, ANY_PROPERTY_FILTER, enumVersioningState.none);
        }

        public void testFilteredFoldersTreeReceving()
        {
            getAndAssertDescedansts(enumTypesOfFileableObjects.any, HIERARCHY_DEPTH, NAME_PROPERTY + "," + OBJECT_IDENTIFIER_PROPERTY, enumVersioningState.none);
        }

        public void testChildrenReceving()
        {
            getAndAssertChildren(ANY_PROPERTY_FILTER, 0, MAX_REQUESTED_COUNT);
        }

        public void testFilteredChildrenReceving()
        {
            getAndAssertChildren(TYPE_ID_PROPERTY + "," + OBJECT_IDENTIFIER_PROPERTY, 0, MAX_REQUESTED_COUNT);
        }

        public void testChildrenRecevingIncludeRenditions()
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

            FileableObject rootFolder = createAndAssertFolder(getAndAssertRootFolder());
            ObjectsHierarchy hierarchy = createAndAssertFilesHierarchy(rootFolder, 1, MINIMAL_CHILDREN, MAXIMUM_CHILDREN, enumTypesOfFileableObjects.any);
            foreach (RenditionData renditionData in testRenditions)
            {
                getAndAssertChildren(hierarchy, ANY_PROPERTY_FILTER, 0, MAX_REQUESTED_COUNT, renditionData);
            }

            deleteAndAssertHierarchy(hierarchy, enumUnfileObject.delete, true);
            deleteAndAssertObject(document, true);
        }

        public void testChildrenRecevingPagination()
        {
            getAndAssertChildren(ANY_PROPERTY_FILTER, 0, 3);
            getAndAssertChildren(ANY_PROPERTY_FILTER, 1, 15);
        }

        public void testDocumentParentsReceving()
        {
            assertSingleParentReceiving(getAndAssertRootFolder());
        }

        public void testMultifiledDocumentParentsReceving()
        {
            if (!isCapabilityMultifilingEnabled())
            {
                Assert.Skip("Capability multifilling is disabled");
            }
            assertMultiFilledParentsReceving(getAndAssertRootFolder());
        }

        public void testFolderParentReceiving()
        {
            assertFolderParentReceiving();
        }

        public void testObjectParentsReceiving()
        {
            assertObjectParentsReceiving();
        }

        public void testCheckedOutDocumentsReceving()
        {
            if (!isVersioningAllowed())
            {
                Assert.Skip("Versioning is disabled for current document type");
            }
            string folderId = createAndAssertFolder(getAndAssertRootFolder()).ObjectId;
            string[] hierarchy = createCheckedOutDocumentsHierarchy(folderId, 1 + new Random().Next(MAXIMUM_CHECKEDOUT_DOCS));

            logger.log("Receiving CheckedOutDocuments for folder, folderId=" + folderId + "'");
            logger.log("[NavigationService->getCheckedoutDocs]");
            cmisObjectListType response = navigationServiceClient.getCheckedOutDocs(getAndAssertRepositoryId(), folderId, ANY_PROPERTY_FILTER, null, false, enumIncludeRelationships.none, null, TEXTUAL_ZERO, TEXTUAL_ZERO, null);
            Assert.IsNotNull(response, "Response is null");
            Assert.IsNotNull(response.objects, "Response is empty");
            cmisObjectType[] checkedOutDocuments = response.objects;
            assertObjectCollectionsConsitence(checkedOutDocuments, createObjectsCopy(hierarchy, null, 1, (hierarchy.Length - 1)));
            logger.log("CheckedOutDocuments were successfully received");
            logger.log("");

            objectServiceClient.deleteTree(getAndAssertRepositoryId(), folderId, true, enumUnfileObject.delete, true, null);
        }

        public void testPathSegments()
        {
            string folderId = createAndAssertFolder(getAndAssertRootFolder()).ObjectId;
            string documentId = createAndAssertObject(folderId, enumVersioningState.none).ObjectId;

            logger.log("[NavigationService->getChildren]");
            cmisObjectInFolderListType childrenResponse = navigationServiceClient.getChildren(getAndAssertRepositoryId(), getAndAssertRootFolder(), "*", null, false,
                    enumIncludeRelationships.none, null, true, Convert.ToString(MAX_REQUESTED_COUNT), TEXTUAL_ZERO, null);
            Assert.IsNotNull(childrenResponse.objects != null, "GetChildren response is NULL");

            string folderPathSegment = null;
            foreach (cmisObjectInFolderType objectInFolder in childrenResponse.objects)
            {
                Assert.IsNotNull(objectInFolder, "CmisObjectInFolder is NULL");
                Assert.IsNotNull(objectInFolder.pathSegment, "pathSegment is NULL");
                Assert.IsNotNull(objectInFolder.@object, "CmisObject is NULL");
                Assert.IsNotNull(objectInFolder.@object.properties, "Properties are NULL");
                if (folderId.Equals((string)searchAndAssertPropertyByName(objectInFolder.@object.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false)))
                {
                    folderPathSegment = objectInFolder.pathSegment;
                }
            }

            Assert.IsNotNull(folderPathSegment, "pathSegment is NULL");

            logger.log("[NavigationService->getObjectParents]");
            cmisObjectParentsType[] parentsResponse = navigationServiceClient.getObjectParents(getAndAssertRepositoryId(), documentId, "*",
                                    false, enumIncludeRelationships.none, null, true, null);
            Assert.IsNotNull(parentsResponse, "GetObjectParents response is NULL");
            Assert.IsTrue(parentsResponse.Length > 0, "GetObjectParents response is empty");
            string documentPathSegment = parentsResponse[0].relativePathSegment;
            Assert.IsNotNull(documentPathSegment, "pathSegment is NULL");

            string folderPath = "/" + folderPathSegment;
            string documentPath = "/" + folderPathSegment + "/" + documentPathSegment;

            logger.log("[ObjectService->getObjectByPath]");
            cmisObjectType objectByPath = objectServiceClient.getObjectByPath(getAndAssertRepositoryId(), folderPath, OBJECT_IDENTIFIER_PROPERTY, false, enumIncludeRelationships.none, null, null, null, null);
            Assert.IsNotNull(objectByPath, "GetObjectByPath response is NULL");
            Assert.IsNotNull(objectByPath.properties, "Object properties are NULL");
            string folderIdByPath = (string)searchAndAssertPropertyByName(objectByPath.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false);
            Assert.AreEqual(folderId, folderIdByPath, "Returned by path objectId is not equal to expected");

            logger.log("[ObjectService->getObjectByPath]");
            objectByPath = objectServiceClient.getObjectByPath(getAndAssertRepositoryId(), documentPath, OBJECT_IDENTIFIER_PROPERTY, false, enumIncludeRelationships.none, null, null, null, null);
            Assert.IsNotNull(objectByPath, "GetObjectByPath response is NULL");
            Assert.IsNotNull(objectByPath.properties, "Object properties are NULL");
            string documentIdByPath = (string)searchAndAssertPropertyByName(objectByPath.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false);
            Assert.AreEqual(documentId, documentIdByPath, "Returned by path objectId is not equal to expected");

            logger.log("[NavigationService->getDescedants]");
            cmisObjectInFolderContainerType[] descendantsResponse = navigationServiceClient.getDescendants(getAndAssertRepositoryId(), folderId, "2", ANY_PROPERTY_FILTER, false, enumIncludeRelationships.none, null, true, null);
            Assert.IsNotNull(descendantsResponse, "GetDescendants response is NULL");

            foreach (cmisObjectInFolderContainerType objectInFolderContainer in descendantsResponse)
            {
                Assert.IsNotNull(objectInFolderContainer, "CmisObjectInFolderContainer is NULL");
                Assert.IsNotNull(objectInFolderContainer.objectInFolder, "CmisObjectInFolder is NULL");
                Assert.IsNotNull(objectInFolderContainer.objectInFolder.pathSegment, "pathSegment is NULL");
                Assert.IsNotNull(objectInFolderContainer.objectInFolder.@object, "CmisObject is NULL");
                if (documentId.Equals((string)searchAndAssertPropertyByName(objectInFolderContainer.objectInFolder.@object.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false)))
                {
                    Assert.AreEqual(documentPathSegment, objectInFolderContainer.objectInFolder.pathSegment, "Returned by path objectId is not equal to expected");
                }
            }
        }

        public void testFilteringWithWrongFilter()
        {
            try
            {
                logger.log("Getting children with not valid filter");
                logger.log("[NavigationService->getChildren]");
                navigationServiceClient.getChildren(getAndAssertRepositoryId(), getAndAssertRootFolder(), ",,,,", null,
                false, enumIncludeRelationships.none, null, false, TEXTUAL_ZERO, TEXTUAL_ZERO, null);
                Assert.Fail("Childrens were returned for invalid filter value");
            }
            catch (FaultException)
            {
                logger.log("Expected error was returned");
                logger.log("");
            }
        }

        public void testWrongParentType()
        {
            string documentId = createAndAssertObject(getAndAssertRootFolder(), null).ObjectId;
            try
            {
                logger.log("Getting children for document");
                logger.log("[NavigationService->getChildren]");
                navigationServiceClient.getChildren(getAndAssertRepositoryId(), documentId, ANY_PROPERTY_FILTER,
                        null, false, enumIncludeRelationships.none, null, false, TEXTUAL_ZERO, TEXTUAL_ZERO, null);
                Assert.Fail("Childrens were returned for document");
            }
            catch (FaultException)
            {
                logger.log("Expected error was returned");
                logger.log("");
            }
            deleteAndAssertObject(documentId);
        }

        private string[] createCheckedOutDocumentsHierarchy(string folderId, int documentsAmount)
        {
            documentsAmount = 1;
            string[] hierarchy = new string[documentsAmount + 1];
            hierarchy[0] = folderId;

            for (int i = 0; i < documentsAmount; i++)
            {
                string documentId = createAndAssertObject(folderId, enumVersioningState.checkedout, false).ObjectId;
                hierarchy[i + 1] = documentId;
            }

            return hierarchy;
        }

        public void getAndAssertChildren(string filter, int skipCount, int maxItems)
        {
            FileableObject rootFolder = createAndAssertFolder(getAndAssertRootFolder());
            ObjectsHierarchy hierarchy = createAndAssertFilesHierarchy(rootFolder, 1, MINIMAL_CHILDREN, MAXIMUM_CHILDREN, enumTypesOfFileableObjects.any);
            getAndAssertChildren(hierarchy, filter, skipCount, maxItems, null);
            deleteAndAssertHierarchy(hierarchy, enumUnfileObject.delete, true);
        }

        public void getAndAssertChildren(ObjectsHierarchy hierarchy, string filter, int skipCount, int maxItems, RenditionData renditionData)
        {
            string[] foldersIds = hierarchy.FolderIds.ToArray();
            string[] documentsIds = hierarchy.DocumentIds.ToArray();
            string[] allCreatedObjectsIds = hierarchy.toIdsArray();
            string rootFolderId = hierarchy.RootFolder.ObjectId;

            cmisObjectInFolderListType children = getAndAssertChildren(rootFolderId, filter, null, maxItems, skipCount, renditionData != null ? renditionData.getFilter() : null);

            cmisObjectType[] childrenObjects = new cmisObjectType[children.objects.Length];
            for (int i = 0; i < children.objects.Length; ++i)
            {
                childrenObjects[i] = children.objects[i] != null ? children.objects[i].@object : null;
            }

            if (maxItems > 0)
            {
                int resultCount = (skipCount + maxItems) < allCreatedObjectsIds.Length ? maxItems : allCreatedObjectsIds.Length - skipCount;
                Assert.IsTrue(resultCount == childrenObjects.Length, "Count of returned items doesn't equal to expected count");
            }
            else
            {
                assertObjectsByType(enumTypesOfFileableObjects.any, documentsIds, foldersIds, allCreatedObjectsIds, childrenObjects);
            }
            foreach (cmisObjectType cmisObject in childrenObjects)
            {
                assertObjectProperties(cmisObject.properties, filter);
                if (renditionData != null)
                {
                    assertRenditions(cmisObject, renditionData.getFilter(), renditionData.getExpectedKinds(), renditionData.getExpectedMimetypes());
                }
            }
            logger.log("Children were successfully received");
            logger.log("");
        }

        private void getAndAssertDescedansts(enumTypesOfFileableObjects type, int depth, string filter, enumVersioningState versioningState)
        {
            FileableObject rootFolder = createAndAssertFolder(getAndAssertRootFolder());
            TreeNode<string> objectsTree = createObjectsTree(rootFolder.ObjectId, versioningState, type, FOLDERS_DEPTH, MINIMAL_CHILDREN, MAXIMUM_CHILDREN, depth);

            getAndAssertDescedansts(rootFolder, objectsTree, type, depth, filter, versioningState, null);

            objectServiceClient.deleteTree(getAndAssertRepositoryId(), rootFolder.ObjectId, true, enumUnfileObject.delete, true, null);
        }

        private void getAndAssertDescedansts(FileableObject rootFolder, TreeNode<string> objectsTree, enumTypesOfFileableObjects type, int depth, string filter, enumVersioningState versioningState, RenditionData renditionData)
        {
            cmisObjectInFolderContainerType[] returnedObjects = null;
            if (!enumTypesOfFileableObjects.folders.Equals(type))
            {
                logger.log("Receiving descendants for folder, folderId=" + rootFolder.ObjectId + "',depth='" + depth + "', filter='" + filter + "'");
                logger.log("[NavigationService->getDescendants]");
                returnedObjects = navigationServiceClient.getDescendants(
                                  getAndAssertRepositoryId(), rootFolder.ObjectId,
                                  Convert.ToString(depth), filter, false, enumIncludeRelationships.none, renditionData != null ? renditionData.getFilter() : null, false, null);
            }
            else
            {
                logger.log("Receiving folders tree, folderId=" + rootFolder.ObjectId + "',depth='" + depth + "', filter='" + filter + "'");
                logger.log("[NavigationService->getFoldersTree]");
                returnedObjects = navigationServiceClient.getFolderTree(
                                  getAndAssertRepositoryId(), rootFolder.ObjectId,
                                  Convert.ToString(depth), filter, false, enumIncludeRelationships.none, renditionData != null ? renditionData.getFilter() : null, false, null);
            }

            Assert.IsNotNull(returnedObjects, "returned ids are null");
            assertObjectsTree(returnedObjects, objectsTree);
            List<cmisObjectInFolderContainerType> objectsList = convertTreeToObjectsList(returnedObjects);
            foreach (cmisObjectInFolderContainerType cmisObject in objectsList)
            {
                Assert.IsNotNull(cmisObject, "CmisObjectInFolderContainer is null");
                Assert.IsNotNull(cmisObject.objectInFolder, "CmisObjectInFolder is null");
                Assert.IsNotNull(cmisObject.objectInFolder.@object, "CmisObject is null");
                assertObjectProperties(cmisObject.objectInFolder.@object.properties, filter);
                if (renditionData != null)
                {
                    assertRenditions(cmisObject.objectInFolder.@object, renditionData.getFilter(), renditionData.getExpectedKinds(), renditionData.getExpectedMimetypes());
                }
            }
            logger.log("Objects were successfully received");
            logger.log("");
        }

        private void assertObjectsByType(enumTypesOfFileableObjects type, string[] files, string[] folders,
                                         string[] allObjects, cmisObjectType[] actualObjects)
        {
            string[] objectsToAssert = null;
            if (enumTypesOfFileableObjects.any.Equals(type))
            {
                objectsToAssert = allObjects;
            }
            else if (enumTypesOfFileableObjects.folders.Equals(type))
            {
                objectsToAssert = folders;
            }
            else if (enumTypesOfFileableObjects.documents.Equals(type))
            {
                objectsToAssert = files;
            }

            assertObjectCollectionsConsitence(actualObjects, objectsToAssert);
        }

        private string[] createObjectsCopy(string[] source, string firstElemetn, int sourceBegin, int elementsAmount)
        {
            int absentElment = (firstElemetn != null) ? 1 : 0;
            string[] result = new string[elementsAmount + absentElment];
            result[0] = firstElemetn;
            Array.Copy(source, sourceBegin, result, absentElment, elementsAmount);
            return result;
        }

        private string[] uniteObjects(string[] objects1, string[] objects2)
        {
            List<string> result = new List<string>();
            result.AddRange(objects1);
            result.AddRange(objects2);
            return result.ToArray();
        }

        private void assertSingleParentReceiving(string rootFolderId)
        {
            string documentId = createAndAssertObject(rootFolderId, null).ObjectId;
            assertDocumentParents(documentId, new string[] { rootFolderId });
            deleteAndAssertObject(documentId);
        }

        private void assertMultiFilledParentsReceving(string rootFolderId)
        {
            MultifiledObject multifilledDocument = createAndAssertMultifilledDocument(rootFolderId, 10);
            string[] parents = new string[multifilledDocument.Parents.Length + 1];
            parents[0] = rootFolderId;

            int parentIndex = 1;
            foreach (FileableObject parentObject in multifilledDocument.Parents)
            {
                parents[parentIndex++] = parentObject.ObjectId;
            }

            assertDocumentParents(multifilledDocument.DocumentObject.ObjectId, parents);
            deleteAndAssertMultifilledDocument(multifilledDocument);
        }

        private void assertFolderParentReceiving()
        {
            FileableObject rootFolder = createAndAssertFolder(getAndAssertRootFolder());
            ObjectsHierarchy hierarchy = createAndAssertFilesHierarchy(rootFolder, 1, 1, 1, enumTypesOfFileableObjects.folders);
            string[] createdObjectsIds = hierarchy.toIdsArray();
            assertFolderParents(createdObjectsIds[createdObjectsIds.Length - 1], new string[] { rootFolder.ObjectId }, false);
            deleteAndAssertHierarchy(hierarchy, enumUnfileObject.delete, true);
        }

        private void assertObjectParentsReceiving()
        {
            FileableObject rootFolder = createAndAssertFolder(getAndAssertRootFolder());
            ObjectsHierarchy hierarchy = createAndAssertFilesHierarchy(rootFolder, 1, 2, 3, enumTypesOfFileableObjects.documents);
            string[] createdObjectsIds = hierarchy.toIdsArray();
            foreach (string objectId in createdObjectsIds)
            {
                assertDocumentParents(objectId, new string[] { rootFolder.ObjectId });
            }
            deleteAndAssertHierarchy(hierarchy, enumUnfileObject.delete, true);
        }

        protected TreeNode<string> createObjectsTree(string rootFolderId, enumVersioningState documentsInitialVersion, enumTypesOfFileableObjects returnObjectTypes, int depth,
                                                     int minLayerSize, int maxLayerSize, int returnToLevel)
        {
            if (returnToLevel <= 0)
            {
                returnToLevel = depth + 1;
            }
            Random randomCounter = new Random();
            TreeNode<string> root = new TreeNode<string>(rootFolderId, 0);

            if ((depth <= 0) || (maxLayerSize < 1) || (minLayerSize > maxLayerSize))
            {
                return root;
            }
            Stack<TreeNode<string>> foldersStack = new Stack<TreeNode<string>>();
            foldersStack.Push(root);
            while (foldersStack.Count > 0)
            {
                TreeNode<string> element = foldersStack.Pop();
                if (element.Level <= depth)
                {
                    int layerSize = minLayerSize + (int)(randomCounter.NextDouble() * (maxLayerSize - minLayerSize));
                    int foldersOnLayer = !returnObjectTypes.Equals(enumTypesOfFileableObjects.documents) ? 1 + (int)(randomCounter.NextDouble() * (layerSize - 1)) : 0;
                    int documentsOnLayer = layerSize - foldersOnLayer;
                    if (layerSize > 0 && element.Children == null)
                    {
                        element.Children = new Hashtable();
                    }
                    for (int i = 0; i < foldersOnLayer; i++)
                    {
                        string newFolderId = createAndAssertFolder(element.Element).ObjectId;
                        TreeNode<string> child = new TreeNode<string>(newFolderId, element.Level + 1);
                        if (element.Level <= returnToLevel && !returnObjectTypes.Equals(enumTypesOfFileableObjects.documents))
                        {
                            element.Children.Add(newFolderId, child);
                        }
                        if (element.Level < depth - 1)
                        {
                            foldersStack.Push(child);
                        }
                    }
                    for (int i = 0; i < documentsOnLayer; i++)
                    {
                        string newDocumentId = createAndAssertObject(element.Element, documentsInitialVersion).ObjectId;
                        if (element.Level <= returnToLevel && !returnObjectTypes.Equals(enumTypesOfFileableObjects.folders))
                        {
                            element.Children.Add(newDocumentId, new TreeNode<string>(newDocumentId, element.Level + 1));
                        }
                    }
                }
            }
            return root;
        }

        private void assertObjectsTree(cmisObjectInFolderContainerType[] receivedTree, TreeNode<string> expectedTreeRoot)
        {
            Assert.IsNotNull(receivedTree, "Objects from response are null");
            Assert.IsTrue(receivedTree.Length > 0, "No one Object was returned in response");

            TreeNode<string> currentTreeNode = expectedTreeRoot;
            Stack<KeyValuePair<cmisObjectInFolderContainerType, TreeNode<string>>> elementsStack = new Stack<KeyValuePair<cmisObjectInFolderContainerType, TreeNode<string>>>();
            cmisObjectInFolderContainerType root = new cmisObjectInFolderContainerType();
            root.children = receivedTree;
            elementsStack.Push(new KeyValuePair<cmisObjectInFolderContainerType, TreeNode<string>>(root, expectedTreeRoot));

            while (elementsStack.Count > 0)
            {
                KeyValuePair<cmisObjectInFolderContainerType, TreeNode<string>> element = elementsStack.Pop();
                Assert.IsNotNull(element.Key, "Expected tree element not found");
                Assert.IsNotNull(element.Value, "Received tree element not found");
                currentTreeNode = element.Value;
                Assert.IsTrue(getSize(element.Key.children) == getSize(currentTreeNode.Children), "Count of returned childs are not equal to expected count of childs");
                if (element.Key.children != null && currentTreeNode.Children != null)
                {
                    HashSet<string> receivedIds = new HashSet<string>();
                    foreach (cmisObjectInFolderContainerType objectInFolderContainer in element.Key.children)
                    {
                        string objectId = getAndAssertObjectId(objectInFolderContainer);
                        Assert.IsFalse(receivedIds.Contains(objectId), "Returned tree childs are not equal to expected childs");
                        receivedIds.Add(objectId);
                        TreeNode<string> childTreeNode = (TreeNode<string>)currentTreeNode.Children[objectId];
                        elementsStack.Push(new KeyValuePair<cmisObjectInFolderContainerType, TreeNode<string>>(objectInFolderContainer, childTreeNode));
                    }
                }
            }
        }

        private void assertObjectProperties(cmisPropertiesType properties, string filter)
        {
            if (filter == null)
            {
                return;
            }
            Assert.IsNotNull(filter, "Incorrect filter");
            Assert.IsNotNull(properties, "Object properties is null");
            Assert.IsNotNull(properties.Items, "Object properties is null");
            bool anyProperties = false;
            if (filter.Equals(ANY_PROPERTY_FILTER))
            {
                filter = NAME_PROPERTY + "," + OBJECT_IDENTIFIER_PROPERTY;
                anyProperties = true;
            }
            string[] expectedProperties = filter.Replace(" ", "").Split(",".ToCharArray());
            if (!anyProperties)
            {
                Assert.IsTrue(getSize(properties.Items) == getSize(expectedProperties), "Expected properties size doesn't equal to received properties size");
            }
            HashSet<string> propertiesSet = new HashSet<string>();
            foreach (cmisProperty property in properties.Items)
            {
                propertiesSet.Add(property.propertyDefinitionId);
            }
            foreach (string propertyId in expectedProperties)
            {
                Assert.IsTrue(propertiesSet.Contains(propertyId), "Expected property '" + propertyId + "' is not found for object");
            }

        }

        private List<cmisObjectInFolderContainerType> convertTreeToObjectsList(cmisObjectInFolderContainerType[] rootChildren)
        {
            List<cmisObjectInFolderContainerType> result = new List<cmisObjectInFolderContainerType>();
            Stack<cmisObjectInFolderContainerType> elementsStack = new Stack<cmisObjectInFolderContainerType>();
            foreach (cmisObjectInFolderContainerType objectInFolderContainer in rootChildren)
            {
                elementsStack.Push(objectInFolderContainer);
            }
            while (elementsStack.Count > 0)
            {
                cmisObjectInFolderContainerType element = elementsStack.Pop();
                result.Add(element);
                if (element.children != null)
                {
                    foreach (cmisObjectInFolderContainerType objectInFolderContainer in element.children)
                    {
                        elementsStack.Push(objectInFolderContainer);
                    }
                }
            }
            return result;
        }

        private int getSize(Object obj)
        {
            if (obj == null)
            {
                return 0;
            }
            if (obj is object[])
            {
                return ((object[])obj).Length;
            }
            if (obj is ICollection)
            {
                return ((ICollection)obj).Count;
            }
            return 1;
        }

        private string getAndAssertObjectId(cmisObjectInFolderContainerType objectInFolderContainer)
        {
            Assert.IsTrue(objectInFolderContainer != null && objectInFolderContainer.objectInFolder != null
                    && objectInFolderContainer.objectInFolder.@object != null, "Object from response is null");
            cmisObjectType cmisObject = objectInFolderContainer.objectInFolder.@object;
            return (string)searchAndAssertPropertyByName(cmisObject.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false);
        }

        protected class TreeNode<T>
        {
            private T element;

            private int level;

            private Hashtable children;

            public T Element
            {
                get { return element; }
                set { element = value; }
            }

            public int Level
            {
                get { return level; }
                set { level = value; }
            }

            public Hashtable Children
            {
                get { return children; }
                set { children = value; }
            }

            public TreeNode(T element, int level)
            {
                this.element = element;
                this.level = level;
            }

            public TreeNode()
            {
            }
        }

    }
}
