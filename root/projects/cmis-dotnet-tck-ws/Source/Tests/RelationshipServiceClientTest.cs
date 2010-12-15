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
using System.Text;

namespace WcfCmisWSTests
{
    ///
    /// author: Stas Sokolovsky
    ///
    public class RelationshipServiceClientTest : BaseServiceClientTest
    {
        public void testRelationshipsSourceReceiving()
        {
            getAndAssertRelationship(false, enumIncludeRelationships.source, null, null, false, null, null);
        }

        public void testRelationshipsTargetReceiving()
        {
            getAndAssertRelationship(false, enumIncludeRelationships.target, null, null, false, null, null);
        }

        public void testRelationshipsReceivingForInvalidObjectId()
        {
            try
            {
                logger.log("Receiving relationships for non-existent object");
                logger.log("[RelationshipService->getObjectRelationships]");
                cmisObjectListType response = relationshipServiceClient.getObjectRelationships(getAndAssertRepositoryId(), INVALID_OBJECT_ID, false, enumRelationshipDirection.either, null, null, false, null, null, null);
                Assert.Fail("Relationships were received for non-existent object");
            }
            catch (FaultException<cmisFaultType> e)
            {
                logger.log("Expected error was returned");
            }
        }

        public void testRelationshipsReceivingWithAllowableActions()
        {
            cmisObjectListType relationships = getAndAssertRelationship(false, enumIncludeRelationships.source, null, null, true, null, null);
            foreach (cmisObjectType currentObject in relationships.objects)
            {
                Assert.IsNotNull(currentObject.allowableActions, "Allowable Actions were not returned");
                Assert.IsTrue(currentObject.allowableActions.canDeleteObject, "Allowable Actions deny reading of Relationship Object Properties");
            }
        }

        public void testFilteredRelationshipsReceiving()
        {
            string filter = OBJECT_IDENTIFIER_PROPERTY + "," + TYPE_ID_PROPERTY + "," + SOURCE_OBJECT_ID + "," + TARGET_OBJECT_ID;
            cmisObjectListType response = getAndAssertRelationship(false, enumIncludeRelationships.source, null, filter, false, null, null);
            foreach (cmisObjectType currentObject in response.objects)
            {
                Assert.IsNotNull(currentObject.properties, "Properties of one of the Relationship Objects are undefined");
                cmisProperty[] properties = currentObject.properties.Items;
                Assert.IsTrue((4 == properties.Length), "Filter allows only 4 properties");
                searchAndAssertPropertyByName(properties, OBJECT_IDENTIFIER_PROPERTY, false);
                searchAndAssertPropertyByName(properties, TYPE_ID_PROPERTY, false);
                searchAndAssertPropertyByName(properties, SOURCE_OBJECT_ID, false);
                searchAndAssertPropertyByName(properties, TARGET_OBJECT_ID, false);
            }
        }

        public void testRelationshipsReceivingWithInvalidFilter()
        {
            try
            {
                logger.log("[RelationshipService->getObjectRelationships]");
                relationshipServiceClient.getObjectRelationships(getAndAssertRepositoryId(), getAndAssertRootFolder(), false, enumRelationshipDirection.source, null, "*INVALID_FILTER*", false, null, null, null);
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.filterNotValid);
            }
        }

        public void testRelationshipsPagination()
        {
            string typeId = getAndAssertBaseRelationshipTypeId();
            RelationshipObject relationship = createAndAssertRelationship(typeId, getAndAssertRootFolder(), false, false, true);
            RelationshipsSearcher searcher = new RelationshipsSearcher(getAndAssertRelationshipSourceTypeId(), getAndAssertRelationshipTargetTypeId());
            cmisTypeContainer[] descendants = getAndAssertTypeDescendants(typeId, -1, false);
            enumerateAndAssertTypesForAction(descendants, searcher, false);
            // TODO: resolve several relationships creation for one pair of source and target objects
        }

        private class RelationshipsSearcher : TypeAction
        {
            private string sourceTypeId;
            private string targetTypeId;

            private List<string> ids = new List<string>();

            public RelationshipsSearcher(string sourceTypeId, string targetTypeId)
            {
                this.sourceTypeId = sourceTypeId;
                this.targetTypeId = targetTypeId;
            }

            public virtual string perform(cmisTypeDefinitionType typeDefinition)
            {
                if (null != typeDefinition)
                {
                    if (contains((typeDefinition as cmisTypeRelationshipDefinitionType).allowedSourceTypes, sourceTypeId)
                        && contains((typeDefinition as cmisTypeRelationshipDefinitionType).allowedTargetTypes, targetTypeId))
                    {
                        ids.Add(typeDefinition.id);
                    }
                }
                return null;
            }

            private bool contains(string[] array, string element)
            {
                if ((null == array) || (array.Length < 1))
                {
                    return true;
                }
                foreach (string currentElement in array)
                {
                    if (currentElement.Equals(element))
                    {
                        return true;
                    }
                }
                return false;
            }

            public List<string> getIds()
            {
                return ids;
            }
        }

        public void testRelationshipsReceivingForInvalidRepositoryId()
        {
            try
            {
                logger.log("[RelationshipService->getObjectRelationships]");
                logger.log("Receiving relationships for incorrect repositoryId");
                cmisObjectListType response = relationshipServiceClient.getObjectRelationships(INVALID_REPOSITORY_ID, getAndAssertRootFolder(), false, enumRelationshipDirection.either, null, null, false, null, null, null);
                Assert.Fail("Relationships were received for non-existent repository");
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, enumServiceException.invalidArgument);
                logger.log("Expected error was returned");
            }
        }

        private cmisObjectListType getAndAssertRelationship(bool includeSubTypes, enumIncludeRelationships relationshipDirection, string typeId, string filter, bool allowableActions, Nullable<long> maxItems, Nullable<long> skipCount)
        {
            RelationshipObject relationship = createAndAssertRelationship(getAndAssertRelationshipTypeId(), getAndAssertRootFolder(), false, false, false);

            string objectId = null;
            enumRelationshipDirection direction = enumRelationshipDirection.either;

            switch (relationshipDirection)
            {
                case enumIncludeRelationships.source:
                    {
                        objectId = relationship.SourceObject.ObjectId;
                        direction = enumRelationshipDirection.source;
                        break;
                    }
                case enumIncludeRelationships.target:
                    {
                        objectId = relationship.TargetObject.ObjectId;
                        direction = enumRelationshipDirection.target;
                        break;
                    }
                case enumIncludeRelationships.both:
                    {
                        objectId = relationship.SourceObject.ObjectId;
                        break;
                    }
            }
            logger.log("[RelationshipService->getObjectRelationships]");
            logger.log("Getting relationships for object with objectId='" + objectId + "', direction=" + relationshipDirection);
            cmisObjectListType response = relationshipServiceClient.getObjectRelationships(getAndAssertRepositoryId(), objectId, includeSubTypes, direction, typeId, filter, allowableActions, maxItems, skipCount, null);
            Assert.IsNotNull(response, "Object Relationships were not returned");
            assertRelationships(objectId, response.objects, relationshipDirection);
            logger.log("Relationships were successfully received");
            logger.log("");
            deleteAndAssertRelationship(relationship);
            return response;
        }
    }
}
