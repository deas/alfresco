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
package org.alfresco.cmis.test.ws;

import org.alfresco.repo.cmis.ws.ACLServicePort;
import org.alfresco.repo.cmis.ws.ApplyACL;
import org.alfresco.repo.cmis.ws.CmisACLType;
import org.alfresco.repo.cmis.ws.CmisAccessControlEntryType;
import org.alfresco.repo.cmis.ws.CmisAccessControlListType;
import org.alfresco.repo.cmis.ws.CmisAccessControlPrincipalType;
import org.alfresco.repo.cmis.ws.DeleteObject;
import org.alfresco.repo.cmis.ws.EnumACLPropagation;
import org.alfresco.repo.cmis.ws.EnumCapabilityACL;
import org.alfresco.repo.cmis.ws.GetACL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Client for ACL Service
 */
public class CmisAclServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(CmisAclServiceClient.class);

    private String documentId;

    public CmisAclServiceClient()
    {
    }

    public CmisAclServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        documentId = createAndAssertDocument();
    }

    @Override
    @SuppressWarnings("unused")
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        ACLServicePort aclService = getServicesFactory().getACLService(getProxyUrl() + getService().getPath());

        /* Add permission */
        CmisAccessControlListType acList = new CmisAccessControlListType();
        CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType(aclPrincipalId, null);
        CmisAccessControlEntryType ace = new CmisAccessControlEntryType(principal, new String[] { PERMISSION_ALL }, true, null);
        acList.setPermission(new CmisAccessControlEntryType[] { ace });
        aclService.applyACL(new ApplyACL(getAndAssertRepositoryId(), documentId, acList, null, EnumACLPropagation.objectonly, null));

        /* Get permission */
        CmisACLType aclType = aclService.getACL(new GetACL(getAndAssertRepositoryId(), documentId, true, null)).getACL();

        /* Remove permission */
        acList = new CmisAccessControlListType();
        principal = new CmisAccessControlPrincipalType(aclPrincipalId, null);
        ace = new CmisAccessControlEntryType(principal, new String[] { PERMISSION_ALL }, true, null);
        acList.setPermission(new CmisAccessControlEntryType[] { ace });
        aclService.applyACL(new ApplyACL(getAndAssertRepositoryId(), documentId, null, acList, EnumACLPropagation.objectonly, null));
    }

    @Override
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }

        deleteAndAssertObject(documentId);
    }

    @Override
    protected void onSetUp() throws Exception
    {
        documentId = createAndAssertDocument();
        super.onSetUp();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        getServicesFactory().getObjectService().deleteObject(new DeleteObject(getAndAssertRepositoryId(), documentId, true, null));
        super.onTearDown();
    }

    public void testReadPermission() throws Exception
    {
        if (!EnumCapabilityACL.manage.equals(getAndAssertCapabilities().getCapabilityACL()))
        {
            logger.info("Repository doesn't support 'manage ACL' capability. Test will be skipped...");
            return;
        }
        if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
        {
            logger.info("ACL Credentials or ACL PrincipalId were not set. Test will be skipped...");
            return;
        }

        ACLServicePort aclService = getServicesFactory().getACLService();

        CmisAccessControlListType acList = new CmisAccessControlListType();
        CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType(aclPrincipalId, null);
        CmisAccessControlEntryType ace = new CmisAccessControlEntryType(principal, new String[] { PERMISSION_READ }, true, null);
        acList.setPermission(new CmisAccessControlEntryType[] { ace });
        LOGGER.info("[ACLService->applyACL]");
        aclService.applyACL(new ApplyACL(getAndAssertRepositoryId(), documentId, acList, null, getAndAssertACLPrapagation(), null));

        getPropertiesUsingCredentials(documentId, aclUsername, aclPassword);
    }

    public void testWritePermission() throws Exception
    {
        if (!EnumCapabilityACL.manage.equals(getAndAssertCapabilities().getCapabilityACL()))
        {
            logger.info("Repository doesn't support 'manage ACL' capability. Test will be skipped...");
            return;
        }
        if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
        {
            logger.info("ACL Credentials or ACL PrincipalId were not set. Test will be skipped...");
            return;
        }

        ACLServicePort aclService = getServicesFactory().getACLService();

        CmisAccessControlListType acList = new CmisAccessControlListType();
        CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType(aclPrincipalId, null);
        CmisAccessControlEntryType ace = new CmisAccessControlEntryType(principal, new String[] { PERMISSION_WRITE }, true, null);
        acList.setPermission(new CmisAccessControlEntryType[] { ace });
        LOGGER.info("[ACLService->applyACL]");
        aclService.applyACL(new ApplyACL(getAndAssertRepositoryId(), documentId, acList, null, getAndAssertACLPrapagation(), null));

        updatePropertiesUsingCredentials(documentId, aclUsername, aclPassword);
    }

    public void testPermissionPropagation() throws Exception
    {
        if (!EnumCapabilityACL.manage.equals(getAndAssertCapabilities().getCapabilityACL()))
        {
            logger.info("Repository doesn't support 'manage ACL' capability. Test will be skipped...");
            return;
        }
        if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
        {
            logger.info("ACL Credentials or ACL PrincipalId were not set. Test will be skipped...");
            return;
        }
        if (!EnumACLPropagation.propagate.equals(getAndAssertACLPrapagation()))
        {
            logger.info("ACL Propagation is not supported. Test will be skipped...");
            return;
        }

        String folderId = createAndAssertFolder();
        String documentId = createAndAssertDocument(generateTestFileName(), getAndAssertDocumentTypeId(), folderId, null, TEST_CONTENT, null);

        ACLServicePort aclService = getServicesFactory().getACLService();

        CmisAccessControlListType acList = new CmisAccessControlListType();
        CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType(aclPrincipalId, null);
        CmisAccessControlEntryType ace = new CmisAccessControlEntryType(principal, new String[] { PERMISSION_READ }, true, null);
        acList.setPermission(new CmisAccessControlEntryType[] { ace });
        LOGGER.info("[ACLService->applyACL]");
        aclService.applyACL(new ApplyACL(getAndAssertRepositoryId(), folderId, acList, null, getAndAssertACLPrapagation(), null));

        getPropertiesUsingCredentials(documentId, aclUsername, aclPassword);
    }

    public void testGetACEs() throws Exception
    {
        if (!EnumCapabilityACL.manage.equals(getAndAssertCapabilities().getCapabilityACL()))
        {
            logger.info("Repository doesn't support 'manage ACL' capability. Test will be skipped...");
            return;
        }
        if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
        {
            logger.info("ACL Credentials or ACL PrincipalId were not set. Test will be skipped...");
            return;
        }

        ACLServicePort aclService = getServicesFactory().getACLService();
        CmisAccessControlListType acList = new CmisAccessControlListType();
        CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType(aclPrincipalId, null);
        CmisAccessControlEntryType ace = new CmisAccessControlEntryType(principal, new String[] { PERMISSION_WRITE }, true, null);
        acList.setPermission(new CmisAccessControlEntryType[] { ace });
        LOGGER.info("[ACLService->applyACL]");
        aclService.applyACL(new ApplyACL(getAndAssertRepositoryId(), documentId, acList, null, getAndAssertACLPrapagation(), null));

        LOGGER.info("[ACLService->getACL]");
        CmisACLType aclType = aclService.getACL(new GetACL(getAndAssertRepositoryId(), documentId, true, null)).getACL();
        assertTrue("No ACE were returned", aclType != null && aclType.getACL() != null && aclType.getACL().getPermission() != null);
        boolean contains = false;
        for (CmisAccessControlEntryType receivedAce : aclType.getACL().getPermission())
        {
            assertTrue("Incorrect ACE was returned", receivedAce != null && receivedAce.getPermission() != null && receivedAce.getPrincipal() != null);
            if (receivedAce.getPrincipal().getPrincipalId() != null && receivedAce.getPrincipal().getPrincipalId().equals(aclPrincipalId))
            {
                for (String permission : receivedAce.getPermission())
                {
                    assertNotNull("Incorrect permission was returned", permission);
                    if (permission.equals(PERMISSION_WRITE))
                    {
                        contains = true;
                    }
                }
            }
        }
        assertTrue("Response doesn't contain expected permission", contains);
    }

    public void testAddAndRemovePermissionConstraints() throws Exception
    {
        if (!EnumCapabilityACL.manage.equals(getAndAssertCapabilities().getCapabilityACL()))
        {
            logger.info("Repository doesn't support 'manage ACL' capability. Test will be skipped...");
            return;
        }
        if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
        {
            logger.info("ACL Credentials or ACL PrincipalId were not set. Test will be skipped...");
            return;
        }

        ACLServicePort aclService = getServicesFactory().getACLService();

        CmisAccessControlListType acList = new CmisAccessControlListType();
        CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType("Incorrect principal", null);
        CmisAccessControlEntryType ace = new CmisAccessControlEntryType(principal, new String[] { "Incorrect permission" }, true, null);
        acList.setPermission(new CmisAccessControlEntryType[] { ace });

        try
        {
            LOGGER.info("[ACLService->applyACL]");
            aclService.applyACL(new ApplyACL(INVALID_REPOSITORY_ID, documentId, acList, null, getAndAssertACLPrapagation(), null));
            fail("Exception expected");
        }
        catch (Exception e)
        {
        }

        try
        {
            LOGGER.info("[ACLService->applyACL]");
            aclService.applyACL(new ApplyACL(getAndAssertRepositoryId(), "IncorrectObjectId", acList, null, getAndAssertACLPrapagation(), null));
            fail("Exception expected");
        }
        catch (Exception e)
        {
        }

        try
        {
            LOGGER.info("[ACLService->applyACL]");
            aclService.applyACL(new ApplyACL(getAndAssertRepositoryId(), documentId, acList, null, getAndAssertACLPrapagation(), null));
            fail("Exception expected");
        }
        catch (Exception e)
        {
        }
    }

    public void testGetACEsConstraints() throws Exception
    {
        if (!EnumCapabilityACL.manage.equals(getAndAssertCapabilities().getCapabilityACL()))
        {
            logger.info("Repository doesn't support 'manage ACL' capability. Test will be skipped...");
            return;
        }
        if (aclPrincipalId == null || aclUsername == null || aclPassword == null)
        {
            logger.info("ACL Credentials or ACL PrincipalId were not set. Test will be skipped...");
            return;
        }

        ACLServicePort aclService = getServicesFactory().getACLService();

        try
        {
            LOGGER.info("[ACLService->getACL]");
            aclService.getACL(new GetACL(INVALID_REPOSITORY_ID, documentId, true, null)).getACL();
            fail("Exception expected");
        }
        catch (Exception e)
        {
        }

        try
        {
            LOGGER.info("[ACLService->getACL]");
            aclService.getACL(new GetACL(getAndAssertRepositoryId(), "IncorrectObjectId", true, null)).getACL();
            fail("Exception expected");
        }
        catch (Exception e)
        {
        }
    }

}
