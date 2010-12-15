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
    /// author: Stas Sokolovsky
    ///
    public class DiscoveryServiceClientTest : BaseServiceClientTest
    {
        private const int DOCS_COUNT = 3;

        private const int PAGE_SIZE = 2;

        private IList<ChangeEntry> testData;

        public override void release(string testName)
        {
            if (null != testData)
            {
                deleteTestData(testData);
                testData = null;
            }
        }

        public void testQueryAll()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.fulltextonly.Equals(getQueryAllowed()))
            {
                Assert.Skip("Metadata query isn't supported");
            }
            FileableObject[] createdIds = prepareTestData();
            string query = "SELECT * FROM " + getAndAssertDocumentQueryName();
            executeAndAssertQuery(query, PAGE_SIZE, 0, false, false, enumIncludeRelationships.none);
            removeTestData(createdIds);
        }

        public void testQueryOrderBy()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.fulltextonly.Equals(getQueryAllowed()))
            {
                Assert.Skip("Metadata query isn't supported");
            }
            FileableObject[] createdIds = prepareTestData();
            string query = "SELECT * FROM " + getAndAssertDocumentQueryName() + " ORDER BY " + NAME_PROPERTY + " ASC";
            cmisObjectType[] response = executeAndAssertQuery(query, PAGE_SIZE, 0, false, false, enumIncludeRelationships.none);
            string name1 = (string)searchAndAssertPropertyByName(response[0].properties.Items, NAME_PROPERTY, false);
            string name2 = (string)searchAndAssertPropertyByName(response[response.Length - 1].properties.Items, NAME_PROPERTY, false);
            Assert.IsTrue(name1.CompareTo(name2) <= 0, "returned results are non-ordered");
            removeTestData(createdIds);
        }

        public void testQueryField()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.fulltextonly.Equals(getQueryAllowed()))
            {
                Assert.Skip("Metadata query isn't supported");
            }
            FileableObject[] createdIds = prepareTestData();
            string query = "SELECT " + NAME_PROPERTY + " FROM " + getAndAssertDocumentQueryName();
            cmisObjectType[] response = executeAndAssertQuery(query, PAGE_SIZE, 0, false, false, enumIncludeRelationships.none);
            foreach (cmisObjectType currentObject in response)
            {
                Assert.IsNotNull(currentObject, "One of the Objects is in 'not set' state solely");
                Assert.IsNotNull(currentObject.properties, "Properties of One of the Objects are undefined");
                Assert.IsNotNull(currentObject.properties.Items, "Properties of One of the Objects are undefined");
                Assert.IsTrue((1 == currentObject.properties.Items.Length), "Invalid amount of the Fields were returned");
                Assert.IsNotNull(searchAndAssertPropertyByName(currentObject.properties.Items, NAME_PROPERTY, false), (NAME_PROPERTY + " Property was not found"));
            }
            removeTestData(createdIds);
        }

        public void testQuerySkipCount()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.fulltextonly.Equals(getQueryAllowed()))
            {
                Assert.Skip("Metadata query isn't supported");
            }
            FileableObject[] createdIds = prepareTestData();
            string query = "SELECT * FROM " + getAndAssertDocumentQueryName() + " ORDER BY " + NAME_PROPERTY + " ASC";
            cmisObjectType[] firstResult = executeAndAssertQuery(query, PAGE_SIZE, 0, false, false, enumIncludeRelationships.none);
            cmisObjectType[] secondResult = executeAndAssertQuery(query, PAGE_SIZE, 1, false, false, enumIncludeRelationships.none);
            string name1 = (string)searchAndAssertPropertyByName(firstResult[1].properties.Items, NAME_PROPERTY, false);
            string name2 = (string)searchAndAssertPropertyByName(secondResult[0].properties.Items, NAME_PROPERTY, false);
            Assert.IsTrue(name1.Equals(name2), "object1(" + name2 + ") in the second query is not equal to object2(" + name1 + ") in the first query");
            removeTestData(createdIds);
        }

        public void testQueryWhere()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.fulltextonly.Equals(getQueryAllowed()))
            {
                Assert.Skip("Metadata query isn't supported");
            }
            FileableObject[] createdIds = prepareTestData();
            string query = "SELECT * FROM " + getAndAssertDocumentQueryName();
            cmisObjectType[] response = executeAndAssertQuery(query, PAGE_SIZE, 0, false, false, enumIncludeRelationships.none);
            string name1 = (string)searchAndAssertPropertyByName(response[0].properties.Items, NAME_PROPERTY, false);
            string whereQuery = "SELECT * FROM " + getAndAssertDocumentQueryName() + " WHERE " + NAME_PROPERTY + "='" + name1 + "'";
            cmisObjectType[] whereResponse = executeAndAssertQuery(whereQuery, PAGE_SIZE, 0, false, false, enumIncludeRelationships.none);
            string name2 = (string)searchAndAssertPropertyByName(whereResponse[0].properties.Items, NAME_PROPERTY, false);
            Assert.IsTrue(name1.Equals(name2), "object1(" + name2 + ") is not equal to requested(" + name1 + ")");
            removeTestData(createdIds);
        }

        public void testQueryIncludeRenditions()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.fulltextonly.Equals(getQueryAllowed()))
            {
                Assert.Skip("Metadata query isn't supported");
            }
            if (!isRenditionsEnabled())
            {
                Assert.Skip("Renditions are not supported");
            }
            FileableObject[] createdObjects = prepareTestData();
            string name = createdObjects[0].ObjectName;
            List<RenditionData> testRenditions = getTestRenditions(createdObjects[0].ObjectId);
            if (testRenditions == null)
            {
                Assert.Skip("No renditions found for object type");
            }
            string query = "SELECT * FROM " + getAndAssertDocumentQueryName() + " WHERE " + NAME_PROPERTY + "='" + name + "'";
            foreach (RenditionData testRendition in testRenditions)
            {
                cmisObjectType[] response = executeAndAssertQuery(query, PAGE_SIZE, 0, false, false, enumIncludeRelationships.none, testRendition.getFilter());
                foreach (cmisObjectType cmisObject in response)
                {
                    assertRenditions(cmisObject, testRendition.getFilter(), testRendition.getExpectedKinds(), testRendition.getExpectedMimetypes());
                }
            }
            removeTestData(createdObjects);
        }

        public void testInvalidQuery()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.fulltextonly.Equals(getQueryAllowed()))
            {
                Assert.Skip("Metadata query isn't supported");
            }
            FileableObject[] createdIds = prepareTestData();
            string query = "SELECT * FROM NotValidType";
            try
            {
                executeAndAssertQuery(query, PAGE_SIZE, 0, false, false, enumIncludeRelationships.none);
                Assert.Fail("Query returned results for NotValidType");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.invalidArgument);
            }
            removeTestData(createdIds);
        }

        public void testQueryFullText()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.metadataonly.Equals(getQueryAllowed()))
            {
                Assert.Skip("Full-Text query isn't supported");
            }

            string name = FileableObject.generateObjectName(false, "TextSearch");
            string content = getRandomLiteralString(7) + " " + getRandomLiteralString(5);
            cmisContentStreamType contentStremType = FileableObject.createCmisDocumentContent(name, Encoding.GetEncoding(FileableObject.DEFAULT_ENCODING).GetBytes(content));
            FileableObject documentCreator = new FileableObject(enumTypesOfFileableObjects.documents, getAndAssertDocumentTypeId(), getAndAssertRootFolder());
            documentCreator.ContentStream = contentStremType;
            createAndAssertObject(documentCreator);
            string query = "SELECT * FROM " + getAndAssertDocumentQueryName() + " WHERE CONTAINS('" + content + "')";
            executeAndAssertQuery(query, PAGE_SIZE, 0, false, false, enumIncludeRelationships.none);
            deleteAndAssertObject(documentCreator.ObjectId);
        }

        public void testQueryPWCSearchable()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.fulltextonly.Equals(getQueryAllowed()))
            {
                throw new SkippedException("Metadata query isn't supported");
            }

            if (!isVersioningAllowed() || !isPWCSearchable())
            {
                Assert.Skip("Versioning isn't supported or PWCSearchable is not supported");
            }

            FileableObject document = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.checkedout, false);
            string query = "SELECT * FROM " + getAndAssertDocumentQueryName() + " WHERE  " + OBJECT_IDENTIFIER_PROPERTY + "='" + document.ObjectId + "'";
            cmisObjectType[] response = executeAndAssertQuery(query, PAGE_SIZE, 0, false, false, enumIncludeRelationships.none);
            Assert.IsNotNull(response, "response is null");
            Assert.IsTrue((response.Length >= 1), "Expected PWC was not found");
            bool found = false;
            foreach (cmisObjectType cmisObject in response)
            {
                string objectId = (string)searchAndAssertPropertyByName(cmisObject.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false);
                if (document.ObjectId.Equals(objectId))
                {
                    found = true;
                }
            }
            Assert.IsTrue(found, "WorkingCopy was not found in query results");
            cmisExtensionType extension = new cmisExtensionType();
            versioningServiceClient.cancelCheckOut(getAndAssertRepositoryId(), document.ObjectId, ref extension);
        }

        // TODO: includeRelationships
        // TODO: renditionsFilter

        public void testQueryWithAllowableActions()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.fulltextonly.Equals(getQueryAllowed()))
            {
                throw new SkippedException("Metadata query isn't supported");
            }

            FileableObject[] documents = prepareTestData(5);
            Dictionary<string, int> ids = new Dictionary<string, int>();
            int index = 0;
            foreach (FileableObject current in documents)
            {
                ids.Add(current.ObjectId, index++);
            }
            string query = "SELECT * FROM " + getAndAssertDocumentQueryName();
            cmisObjectType[] response = executeAndAssertQuery(query, 5, 0, false, true, enumIncludeRelationships.none);
            foreach (cmisObjectType currentObject in response)
            {
                Assert.IsNotNull(currentObject, "One of the Document Objects is in 'not set' state solely");
                Assert.IsNotNull(currentObject.properties, "Properties of one of the Document Objects are undefined");
                string currentId = (string)searchAndAssertPropertyByName(currentObject.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false);
                if (ids.TryGetValue(currentId, out index))
                {
                    assertObject(documents[index], currentObject, true, enumIncludeRelationships.none);
                }
            }
            removeTestData(documents);
        }

        public void testQueryAgainstAllVersionsParameter()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.fulltextonly.Equals(getQueryAllowed()))
            {
                throw new SkippedException("Metadata query isn't supported");
            }
            string query = "SELECT * FROM " + getAndAssertDocumentQueryName();
            if (isAllVersionsSearchable())
            {
                if (isVersioningAllowed())
                {
                    FileableObject folder = createAndAssertFolder(getAndAssertRootFolder());
                    FileableObject document = createAndAssertObject(folder.ObjectId, null);
                    createAndAssertVersionedDocument(document.ObjectId, 4, true);
                    createAndAssertVersionedDocument(document.ObjectId, 4, true);
                    createAndAssertVersionedDocument(document.ObjectId, 3, false);
                    query += " WHERE " + PARENT_OBJECT_IDENTIFIER + "='" + folder.ObjectId + "'";
                    cmisObjectType[] response = executeAndAssertQuery(query, 15, 0, true, false, enumIncludeRelationships.none);
                    Assert.IsTrue((12 == response.Length), ("Query with Search All Versions parameter returned invalid Result Set. Expected Objects amount: 12, but actual: " + response.Length));
                }
                else
                {
                    Assert.Skip("Versioning is not allowed");
                }
            }
            else
            {
                try
                {
                    discoveryServiceClient.query(getAndAssertRepositoryId(), query, true, false, enumIncludeRelationships.none, null, 0, 0, null);
                }
                catch (FaultException<cmisFaultType> e)
                {
                    assertException(e, enumServiceException.invalidArgument);
                }
            }
        }

        public void testQueryInInvalidRepository()
        {
            if (enumCapabilityQuery.none.Equals(getQueryAllowed()) || enumCapabilityQuery.fulltextonly.Equals(getQueryAllowed()))
            {
                throw new SkippedException("Metadata query isn't supported");
            }
            string query = "SELECT * FROM " + getAndAssertDocumentQueryName();
            try
            {
                discoveryServiceClient.query(INVALID_REPOSITORY_ID, query, false, false, enumIncludeRelationships.none, null, 0, 0, null);
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.invalidArgument);
            }
        }

        private cmisObjectType[] executeAndAssertQuery(string query, int pageSize, int skipCount, bool allVersions, bool allowableActions, enumIncludeRelationships relationships)
        {
            return executeAndAssertQuery(query, pageSize, skipCount, allVersions, allowableActions, relationships, null);
        }

        private cmisObjectType[] executeAndAssertQuery(string query, int pageSize, int skipCount, bool allVersions, bool allowableActions, enumIncludeRelationships relationships, string renditionFilter)
        {
            logger.log("Executing query '" + query + "', pageSize=" + pageSize + ", skipCount=" + skipCount);
            logger.log("[DiscoveryService->query]");
            cmisObjectListType response = discoveryServiceClient.query(getAndAssertRepositoryId(), query, allVersions, allowableActions, relationships, renditionFilter, pageSize, skipCount, null);
            Assert.IsNotNull(response, "response is null");
            Assert.IsNotNull(response.objects, "Objects are undefined");
            Assert.IsTrue(response.objects.Length > 0, "number of returned objects < 1");
            if ((DOCS_COUNT - skipCount) >= pageSize)
            {
                Assert.IsTrue((response.objects.Length > 0 && response.objects.Length <= PAGE_SIZE), "number of returned objects(" + response.objects.Length + ") is not equal to expected(0 < N <=" + pageSize + ")");
            }
            logger.log("Query successfully executed, number of returned objects=" + response.objects.Length);
            logger.log("");
            return response.objects;
        }

        private FileableObject[] prepareTestData()
        {
            return prepareTestData(DOCS_COUNT);
        }

        private FileableObject[] prepareTestData(int count)
        {
            FileableObject[] result = new FileableObject[count];
            for (int i = 0; i < count; ++i)
            {
                result[i] = createAndAssertObject(getAndAssertRootFolder(), null);
            }
            return result;
        }

        private void removeTestData(FileableObject[] ids)
        {
            foreach (FileableObject id in ids)
            {
                deleteAndAssertObject(id.ObjectId);
            }
        }

        private static string getRandomLiteralString(int length)
        {
            char[] result = new char[length];
            Random random = new Random(length);
            for (int i = 0; i < length; i++)
            {
                result[i] = (char)((int)'a' + random.Next(20));
            }
            return new string(result);
        }

        public void testGetContentChangesForCreate()
        {
            if (getCapabilityChanges().Equals(enumCapabilityChanges.none))
            {
                throw new SkippedException("Content Changes Capability is not supported");
            }
            //TODO: Change document creation to default versioning state after versioning problem will be fixed
            string changeLogToken = getAndAssertRepositoryInfo().latestChangeLogToken;
            string objectId = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.none).ObjectId;
            cmisObjectListType response = receiveAndAssertContentChanges(ref changeLogToken, null);
            assertContentChanges(response, objectId, enumTypeOfChanges.created);
            deleteAndAssertObject(objectId);
        }

        public void testGetContentChangesForUpdate()
        {
            if (getCapabilityChanges().Equals(enumCapabilityChanges.none))
            {
                throw new SkippedException("Content Changes Capability is not supported");
            }
            //TODO: Change document creation to default versioning state after versioning problem will be fixed            
            FileableObject currentObject = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.none);

            string changeLogToken = getAndAssertRepositoryInfo().latestChangeLogToken;

            logger.log("[ObjectService->updateProperties]");
            string changeToken = "";
            string objectId = currentObject.ObjectId;
            currentObject.addProperty(NAME_PROPERTY, FileableObject.generateObjectName(false, DateTime.Now.Ticks.ToString()));
            object property = currentObject.removeProperty(TYPE_ID_PROPERTY);
            cmisExtensionType extension = new cmisExtensionType();
            objectServiceClient.updateProperties(getAndAssertRepositoryId(), ref objectId, ref changeToken, currentObject.getObjectProperties(false), ref extension);

            cmisObjectListType response = receiveAndAssertContentChanges(ref changeLogToken, null);
            assertContentChanges(response, objectId, enumTypeOfChanges.updated);
            deleteAndAssertObject(objectId);
        }

        public void testGetContentChangesForDelete()
        {
            if (getCapabilityChanges().Equals(enumCapabilityChanges.none))
            {
                throw new SkippedException("Content Changes Capability is not supported");
            }
            //TODO: Change document creation to default versioning state after versioning problem will be fixed            
            string objectId = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.none).ObjectId;

            string changeLogToken = getAndAssertRepositoryInfo().latestChangeLogToken;
            deleteAndAssertObject(objectId);
            cmisObjectListType response = receiveAndAssertContentChanges(ref changeLogToken, null);
            assertContentChanges(response, objectId, enumTypeOfChanges.deleted);

        }

        public void testGetContentChangesForSecurity()
        {
            if (getCapabilityChanges().Equals(enumCapabilityChanges.none))
            {
                throw new SkippedException("Content Changes Capability is not supported");
            }
            //TODO: Change document creation to default versioning state after versioning problem will be fixed            
            string objectId = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.none).ObjectId;

            string changeLogToken = getAndAssertRepositoryInfo().latestChangeLogToken;
            cmisAccessControlListType acList = createSimpleACL(aclPrincipalId, PERMISSION_WRITE);
            logger.log("[ACLService->applyACL]");
            aclServiceClient.applyACL(getAndAssertRepositoryId(), objectId, acList, null, getACLPropagation(), null);

            cmisObjectListType response = receiveAndAssertContentChanges(ref changeLogToken, null);
            assertContentChanges(response, objectId, enumTypeOfChanges.security);

        }

        public void testGetContentChangesForInvalidChangeLogToken()
        {
            if (getCapabilityChanges().Equals(enumCapabilityChanges.none))
            {
                throw new SkippedException("Content Changes Capability is not supported");
            }
            try
            {
                string changeLogToken = "<Invalid Change Log Token>";
                logger.log("[DiscoveryService->getContentChanges]");
                discoveryServiceClient.getContentChanges(getAndAssertRepositoryId(), ref changeLogToken, false, ANY_PROPERTY_FILTER, false, false, null, null);
                Assert.Fail("Change Event Entries were received normally for Invalid Change Token");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.invalidArgument);
            }
        }

        public void testGetContentChangesWithSpecifiedAndNotSpecifiedChangeLogToken()
        {
            if (getCapabilityChanges().Equals(enumCapabilityChanges.none))
            {
                throw new SkippedException("Content Changes Capability is not supported");
            }
            string changeLogToken = getAndAssertRepositoryInfo().latestChangeLogToken;
            IList<ChangeEntry> testData = createTestData(1, false);
            receiveAndAssertContentChanges(ref changeLogToken, null);
            changeLogToken = null;
            receiveAndAssertContentChanges(ref changeLogToken, null);
        }

        public void testGetContentChangesPagination()
        {
            if (getCapabilityChanges().Equals(enumCapabilityChanges.none))
            {
                throw new SkippedException("Content Changes Capability is not supported");
            }
            string changeLogToken = getAndAssertRepositoryInfo().latestChangeLogToken;
            IList<ChangeEntry> testData = createTestData(4, false);
            IList<cmisObjectType> receivedItems = new List<cmisObjectType>();
            bool hasMore = true;
            while (hasMore)
            {
                cmisObjectListType items = receiveAndAssertContentChanges(ref changeLogToken, 3);
                if (items != null)
                {
                    hasMore = items.hasMoreItems;
                    if (items.objects != null)
                    {
                        foreach (cmisObjectType receivedObject in items.objects)
                        {
                            receivedItems.Add(receivedObject);
                        }
                    }
                }
                else
                {
                    hasMore = false;
                }
            }
            cmisObjectListType allReceivedObjects = new cmisObjectListType();
            allReceivedObjects.objects = new cmisObjectType[receivedItems.Count];
            receivedItems.CopyTo(allReceivedObjects.objects, 0);
            foreach (ChangeEntry changeEntry in testData)
            {
                assertContentChanges(allReceivedObjects, changeEntry.Id, changeEntry.ChangeType);
            }

        }

        public void testGetContentChangesForInvlaidRepositoryId()
        {
            if (getCapabilityChanges().Equals(enumCapabilityChanges.none))
            {
                throw new SkippedException("Content Changes Capability is not supported");
            }
            try
            {
                string changeLogToken = null;
                logger.log("[DiscoveryService->getContentChanges]");
                discoveryServiceClient.getContentChanges(INVALID_REPOSITORY_ID, ref changeLogToken, false, ANY_PROPERTY_FILTER, false, false, null, null);
                Assert.Fail("Content Changes were normally received for Invlaid Repository Id");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.invalidArgument);
            }
        }

        private cmisObjectListType receiveAndAssertContentChanges(ref string changeLogToken, Nullable<long> maxItems)
        {
            logger.log("[DiscoveryService->getContentChanges]");
            cmisObjectListType result = discoveryServiceClient.getContentChanges(getAndAssertRepositoryId(), ref changeLogToken, false, ANY_PROPERTY_FILTER, false, false, maxItems, null);
            Assert.IsNotNull(result, ("Get Content Changes response is undefined for '" + changeLogToken + "' Change Log Token"));
            Assert.IsFalse(isValueNotSet(result.objects), ("Get Content Changes response is empty for '" + changeLogToken + "' Change Log Token"));
            if (null != result.numItems)
            {
                int numItems = Convert.ToInt32(result.numItems);
                if (null != maxItems)
                {
                    Assert.IsTrue(numItems <= maxItems, ("Get Content Changes result contains not valid amount of Entries. Expectd amount: " + maxItems + ", but actual: " + result.numItems));
                }
            }
            return result;
        }

        private void assertContentChanges(cmisObjectListType actualObjects, string expectedObjectId, enumTypeOfChanges expectedChangeType)
        {
            bool found = false;
            foreach (cmisObjectType currentObject in actualObjects.objects)
            {
                Assert.IsNotNull(currentObject, "One of the Change Event Entries is in 'not set' state solely");
                Assert.IsNotNull(currentObject.properties, "Properties of one of the Change Event Entries are undefined");
                Assert.IsNotNull(currentObject.changeEventInfo, "ChangeEventInfo of one of the Change Event Entries is undefined");
                String id = (string)searchAndAssertPropertyByName(currentObject.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false);
                Assert.IsNotNull(id, "'cmis:objectId' property of one of the Change Event Entries is undefined");
                logger.log("Recived Change Log Entry [ChangeType='" + currentObject.changeEventInfo.changeType + "', ObjectId='" + id + "']");
                if (expectedObjectId.Equals(id) && expectedChangeType.Equals(currentObject.changeEventInfo.changeType))
                {
                    found = true;
                }
            }
            logger.log("");
            Assert.IsTrue(found, "Expected Change Event is not found in received items");
        }

        private void assertEntry(cmisObjectType entry, string filter)
        {
            Assert.IsNotNull(entry, "One of the Change Event Entries is in 'not set' state solely");
            Assert.IsNotNull(entry.properties, "Properties of one of the Change Event Entries are undefined");
            if (null != filter)
            {
                assertPropertiesByFilter(entry.properties, filter);
            }
            Assert.IsNotNull(entry.changeEventInfo, "Change Event Info for one of the Change Event Entries is undefined");
        }

        private IList<ChangeEntry> createTestData(int commonAmount, bool keepProperties)
        {
            IList<ChangeEntry> result = new List<ChangeEntry>();
            for (int i = 0; i < commonAmount; i++)
            {
                //TODO: Change document creation to default versioning state after versioning problem will be fixed            
                string objectId = createAndAssertObject(getAndAssertRootFolder(), enumVersioningState.none).ObjectId;

                ChangeEntry entry = new ChangeEntry(objectId, enumTypeOfChanges.created);
                result.Add(entry);
            }
            return result;
        }

        private string createAndAssertObject(bool folder)
        {
            string rootFolderId = getAndAssertRootFolder();
            FileableObject createdObject = (folder) ? (createAndAssertFolder(rootFolderId)) : (createAndAssertObject(rootFolderId, null));
            return createdObject.ObjectId;
        }

        private void deleteTestData(IList<ChangeEntry> testData)
        {
            foreach (ChangeEntry entry in testData)
            {
                string id = entry.Id;
                if (enumTypeOfChanges.deleted != entry.ChangeType)
                {
                    deleteAndAssertObject(entry.Id);
                }
            }
        }

        internal class ChangeEntry
        {
            private string id;

            public string Id
            {
                get { return id; }
                set { id = value; }
            }
            private enumTypeOfChanges changeType;

            public enumTypeOfChanges ChangeType
            {
                get { return changeType; }
                set { changeType = value; }
            }

            public ChangeEntry(string id, enumTypeOfChanges changeType)
            {
                this.id = id;
                this.changeType = changeType;
            }

        }
    }
}
