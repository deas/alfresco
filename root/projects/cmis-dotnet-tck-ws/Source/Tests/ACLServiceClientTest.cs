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
using System.Linq;
using System.Text;
using WcfCmisWSTests.CmisServices;
using System.ServiceModel;

namespace WcfCmisWSTests
{
    ///
    /// author: Stas Sokolovsky
    ///
    class ACLServiceClientTest : BaseServiceClientTest
    {
        private string documentId;

        public void initialize(string testname)
        {
            documentId = createAndAssertObject(false, getAndAssertRootFolder(), getAndAssertDocumentTypeId()).ObjectId;
        }

        public void release(string testname)
        {
            deleteAndAssertObject(documentId);
        }

        public void testReadPermission()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            cmisAccessControlListType acList = createSimpleACL(aclPrincipalId, PERMISSION_READ);
            logger.log("[ACLService->applyACL]");
            aclServiceClient.applyACL(getAndAssertRepositoryId(), documentId, acList, null, getACLPropagation(), null);

            getPropertiesUsingCredentials(documentId, aclUsername, aclPassword);

        }

        public void testWritePermission()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            cmisAccessControlListType acList = createSimpleACL(aclPrincipalId, PERMISSION_WRITE);
            logger.log("[ACLService->applyACL]");
            aclServiceClient.applyACL(getAndAssertRepositoryId(), documentId, acList, null, getACLPropagation(), null);

            updatePropertiesUsingCredentials(documentId, aclUsername, aclPassword);
        }

        public void testPermissionPropagation()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }
            if (!enumACLPropagation.propagate.Equals(getACLPropagation()))
            {
                Assert.Skip("ACL Propagation is not supported.");
            }

            string folderId = createAndAssertFolder(getAndAssertRootFolder()).ObjectId;
            string documentId = createAndAssertObject(folderId, null).ObjectId;

            cmisAccessControlListType acList = createSimpleACL(aclPrincipalId, PERMISSION_READ);
            logger.log("[ACLService->applyACL]");
            aclServiceClient.applyACL(getAndAssertRepositoryId(), folderId, acList, null, getACLPropagation(), null);

            getPropertiesUsingCredentials(documentId, aclUsername, aclPassword);

            deleteAndAssertObject(documentId);
            deleteAndAssertObject(folderId);
        }

        public void testGetACEs()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            cmisAccessControlListType acList = createSimpleACL(aclPrincipalId, PERMISSION_READ);
            logger.log("[ACLService->applyACL]");
            aclServiceClient.applyACL(getAndAssertRepositoryId(), documentId, acList, null, getACLPropagation(), null);

            logger.log("[ACLService->getACL]");
            cmisACLType aclType = aclServiceClient.getACL(getAndAssertRepositoryId(), documentId, true, null);
            Assert.IsTrue(aclType != null && aclType.acl != null && aclType.acl.permission != null, "No ACE were returned");
            bool contains = false;
            foreach (cmisAccessControlEntryType receivedAce in aclType.acl.permission)
            {
                Assert.IsTrue(receivedAce != null && receivedAce.permission != null && receivedAce.principal != null, "Incorrect ACE was returned");
                if (receivedAce.principal.principalId != null && receivedAce.principal.principalId.Equals(aclPrincipalId))
                {
                    foreach (String permission in receivedAce.permission)
                    {
                        Assert.IsNotNull(permission, "Incorrect permission was returned");
                        if (permission.Equals(PERMISSION_READ))
                        {
                            contains = true;
                        }
                    }
                }
            }
            Assert.IsTrue(contains, "Response doesn't contain expected permission");
        }

        public void testAddAndRemovePermissionConstraints()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            cmisAccessControlListType acList = createSimpleACL("Invalid principal", "Invalid permission");

            try
            {
                logger.log("[ACLService->applyACL]");
                aclServiceClient.applyACL(INVALID_REPOSITORY_ID, documentId, acList, null, getACLPropagation(), null);
                Assert.Skip("Exception expected");
            }
            catch (FaultException<cmisFaultType> e)
            {
            }

            try
            {
                logger.log("[ACLService->applyACL]");
                aclServiceClient.applyACL(getAndAssertRepositoryId(), INVALID_OBJECT_ID, acList, null, getACLPropagation(), null);
                Assert.Skip("Exception expected");
            }
            catch (FaultException<cmisFaultType> e)
            {
            }

            try
            {
                logger.log("[ACLService->applyACL]");
                aclServiceClient.applyACL(getAndAssertRepositoryId(), documentId, acList, null, getACLPropagation(), null);
                Assert.Skip("Exception expected");
            }
            catch (FaultException<cmisFaultType> e)
            {
            }

        }

        public void testGetACEsConstraints()
        {
            if (!enumCapabilityACL.manage.Equals(getCapabilityACL()))
            {
                Assert.Skip("Repository doesn't support 'manage ACL' capability.");
            }
            if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
            {
                Assert.Skip("ACL Credentials or ACL PrincipalId were not set.");
            }

            try
            {
                logger.log("[ACLService->applyACL]");
                aclServiceClient.getACL(INVALID_REPOSITORY_ID, documentId, true, null);
                Assert.Skip("Exception expected");
            }
            catch (FaultException<cmisFaultType> e)
            {
            }

            try
            {
                logger.log("[ACLService->applyACL]");
                aclServiceClient.getACL(getAndAssertRepositoryId(), INVALID_OBJECT_ID, true, null);
                Assert.Skip("Exception expected");
            }
            catch (FaultException<cmisFaultType> e)
            {
            }
        }

    }
}
