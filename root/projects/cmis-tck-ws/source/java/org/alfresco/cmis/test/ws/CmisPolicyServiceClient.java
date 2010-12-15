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

import java.util.HashSet;
import java.util.Set;

import org.alfresco.repo.cmis.ws.ApplyPolicy;
import org.alfresco.repo.cmis.ws.ApplyPolicyResponse;
import org.alfresco.repo.cmis.ws.CmisObjectType;
import org.alfresco.repo.cmis.ws.CmisTypeDefinitionType;
import org.alfresco.repo.cmis.ws.CmisTypeDocumentDefinitionType;
import org.alfresco.repo.cmis.ws.EnumServiceException;
import org.alfresco.repo.cmis.ws.GetAppliedPolicies;
import org.alfresco.repo.cmis.ws.PolicyServicePort;
import org.alfresco.repo.cmis.ws.RemovePolicy;
import org.alfresco.repo.cmis.ws.RemovePolicyResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Dmitry Velichkevich
 */
public class CmisPolicyServiceClient extends AbstractServiceClient
{
    private static final Log LOGGER = LogFactory.getLog(CmisPolicyServiceClient.class);

    private String policyId;
    private String objectId;

    public CmisPolicyServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    public CmisPolicyServiceClient()
    {
    }

    @Override
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        if (arePoliciesSupported())
        {
            String policyTypeId = getAndAssertPolicyTypeId();
            String policyControllableTypeId = getAndAssertPolicyControllableTypeId();
            if ((null != policyTypeId) && (null != policyControllableTypeId))
            {
                policyId = createAndAssertPolicy("TestPolicy", policyTypeId, null, getAndAssertRootFolderId());
            }
            if (null != policyControllableTypeId)
            {
                objectId = createAndAssertObject(policyControllableTypeId);
            }
            else
            {
                objectId = createAndAssertDocument();
            }
        }
    }

    private String createAndAssertObject(String typeId) throws Exception
    {
        CmisTypeDefinitionType typeDefinition = getAndAssertTypeDefinition(typeId);
        if (typeDefinition instanceof CmisTypeDocumentDefinitionType)
        {
            return createAndAssertDocument(generateTestFileName(), typeId, getAndAssertRootFolderId(), null, TEST_CONTENT, null);
        }
        else
        {
            return createAndAssertFolder(generateTestFolderName(), typeId, getAndAssertRootFolderId(), null);
        }
    }

    @Override
    public void invoke() throws Exception
    {
        if (!arePoliciesSupported() || (null == policyId))
        {
            LOGGER.warn("Policies not supported or no creatable Policy object-type was found! Client invocation will be skipped");
            throw new RuntimeException("Skipped invocation");
        }
        else
        {
            LOGGER.info("Invoking client...");
            PolicyServicePort policyServicePort = getServicesFactory().getPolicyService(getProxyUrl() + getService().getPath());
            String repositoryId = getAndAssertRepositoryId();
            policyServicePort.applyPolicy(new ApplyPolicy(repositoryId, policyId, objectId, null));
            policyServicePort.getAppliedPolicies(new GetAppliedPolicies(repositoryId, objectId, "*", null));
            policyServicePort.removePolicy(new RemovePolicy(repositoryId, policyId, objectId, null));
        }
    }

    @Override
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
        if (null != objectId)
        {
            deleteAndAssertObject(objectId);
        }
        if (null != policyId)
        {
            deleteAndAssertObject(policyId);
        }
    }

    /**
     * Main method to start client
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-context.xml");
        AbstractServiceClient client = (CmisPolicyServiceClient) applicationContext.getBean("cmisPolicyServiceClient");
        try
        {
            client.initialize();
            client.invoke();
            client.release();
        }
        catch (Exception e)
        {
            LOGGER.error("Some error occured during client running. Exception message: " + e.getMessage());
        }
    }

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        initialize();
    }

    @Override
    protected void onTearDown() throws Exception
    {
        release();
        super.onTearDown();
    }

    public void testPolicyAppyingAndRemoving() throws Exception
    {
        if (!arePoliciesSupported())
        {
            LOGGER.warn("Policies are not supported! Test will be skipped...");
        }
        else
        {
            if (null == getAndAssertPolicyTypeId())
            {
                LOGGER.warn("No creatable Policy object-type was found! Test will be skipped...");
            }
            else
            {
                if (null == getAndAssertPolicyControllableTypeId())
                {
                    LOGGER.warn("No Policy controllable object-type was found! Test will be skipped...");
                }
                else
                {
                    applyAndAssertPolicy(policyId, objectId);
                    removeAndAssertPolicy(policyId, objectId);
                }
            }
        }
    }

    private void applyAndAssertPolicy(String policyId, String objectId)
    {
        ApplyPolicyResponse applyPolicyResponse = null;
        try
        {
            applyPolicyResponse = getServicesFactory().getPolicyService().applyPolicy(new ApplyPolicy(getAndAssertRepositoryId(), policyId, objectId, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull(applyPolicyResponse);
    }

    private void removeAndAssertPolicy(String policyId, String objectId)
    {
        RemovePolicyResponse removePolicyResponse = null;
        try
        {
            removePolicyResponse = getServicesFactory().getPolicyService().removePolicy(new RemovePolicy(getAndAssertRepositoryId(), policyId, objectId, null));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
        assertNotNull(removePolicyResponse);
    }

    public void testAppliedPoliciesReceiving() throws Exception
    {
        if (!arePoliciesSupported())
        {
            LOGGER.warn("Policies are not supported! Test will be skipped...");
        }
        else
        {
            if (null == getAndAssertPolicyTypeId())
            {
                LOGGER.warn("No creatable Policy object-type was found! Primitive testing of Applied Policies Receiving");
                Set<EnumServiceException> exceptions = new HashSet<EnumServiceException>();
                exceptions.add(EnumServiceException.notSupported);
                exceptions.add(EnumServiceException.runtime);
                CmisObjectType[] appliedPolicies = getAndAssertAppliedPolicies(objectId, exceptions);
                assertTrue("No Policy object was applied but applied Policies response is not empty!", (null == appliedPolicies) || (0 == appliedPolicies.length));
            }
            else
            {
                if (null == getAndAssertPolicyControllableTypeId())
                {
                    LOGGER.warn("No Policy controllable object-type was found! Test will be skipped...");
                }
                else
                {
                    applyAndAssertPolicy(policyId, objectId);
                    getAndAssertAppliedPolicies(policyId, true);
                    removeAndAssertPolicy(policyId, objectId);
                    getAndAssertAppliedPolicies(policyId, false);
                }
            }
        }
    }

    private void getAndAssertAppliedPolicies(String policyId, boolean mustBeApplied) throws Exception
    {
        CmisObjectType[] appliedPolicies = getAndAssertAppliedPolicies(objectId, null);
        assertNotNull("Applied Policies repsonse is undefined", appliedPolicies);
        assertTrue(("No applied Policies were found! At least one Policy with Id='" + policyId + "' must be returned!"), appliedPolicies.length > 0);
        boolean found = false;
        for (CmisObjectType object : appliedPolicies)
        {
            assertNotNull("One of the Policy objects is in 'not set' state solely", object);
            assertNotNull("One of the Policy objects' properties are undefined", object.getProperties());
            found = getIdProperty(object.getProperties(), PROP_OBJECT_ID).equals(policyId);
            if (found)
            {
                break;
            }
        }
        if (mustBeApplied)
        {
            assertTrue("Expected applied Policy object with Id='" + policyId + "' was not found!", found);
        }
        else
        {
            assertFalse("Unexpected applied Policy object with Id='" + policyId + "' was found!", found);
        }
    }

    private CmisObjectType[] getAndAssertAppliedPolicies(String objectId, Set<EnumServiceException> expectedExceptions)
    {
        CmisObjectType[] appliedPolicies = null;
        try
        {
            appliedPolicies = getServicesFactory().getPolicyService().getAppliedPolicies(new GetAppliedPolicies(getAndAssertRepositoryId(), objectId, "*", null));
        }
        catch (Exception e)
        {
            if (null != expectedExceptions)
            {
                assertException("Receiving Applied Policies", e, expectedExceptions);
            }
            else
            {
                fail(e.toString());
            }
        }
        return appliedPolicies;
    }
}
