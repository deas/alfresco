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
using System.Linq;
using System.Text;
using System.Collections.Generic;
using WcfCmisWSTests.CmisServices;
using System.ServiceModel;
using WcfCmisWSTests;

namespace WcfCmisTests
{
    public class PolicyServiceClientTest : BaseServiceClientTest
    {
        private string policyId;
        private FileableObject policyControllableObject;

        public override void initialize(string testName)
        {
            if (arePoliciesAllowed())
            {
                string typeId = getAndAssertPolicyControllableTypeId();
                if (null != typeId)
                {
                    cmisExtensionType extension = new cmisExtensionType();
                    cmisPropertiesType properties = FileableObject.addPropertyToObject(null, NAME_PROPERTY, FileableObject.generateObjectName(true, "Policy"));
                    FileableObject.addPropertyToObject(properties, TYPE_ID_PROPERTY, getAndAssertPolicyTypeId());
                    policyId = objectServiceClient.createPolicy(getAndAssertRepositoryId(), properties, getAndAssertRootFolder(), null, null, null, ref extension);
                }
                if (null != typeId)
                {
                    cmisTypeDefinitionType typeDefinition = getAndAssertTypeDefinition(typeId);
                    policyControllableObject = createAndAssertObject(typeDefinition is cmisTypeFolderDefinitionType, getAndAssertRootFolder(), typeId);
                }
                else
                {
                    policyControllableObject = createAndAssertObject(getAndAssertRootFolder(), null);
                }
            }
        }

        public override void release(string testName)
        {
            if (null != policyControllableObject)
            {
                deleteAndAssertObject(policyControllableObject.ObjectId);
            }
            if (null != policyId)
            {
                deleteAndAssertObject(policyId);
            }
        }

        public void testPolicyApllyingAndRemoving()
        {
            if (arePoliciesAllowed())
            {
                if (null == getAndAssertPolicyTypeId())
                {
                    Assert.Skip("No creatable Policy object-type was found");
                }
                else
                {
                    if (null == policyControllableObject)
                    {
                        Assert.Skip("No Policy controllable object-type was found");
                    }
                    else
                    {
                        applyAndAssertPolicy(policyId, policyControllableObject.ObjectId);
                        removeAndAssertPolicy(policyId, policyControllableObject.ObjectId);
                    }
                }
            }
            else
            {
                Assert.Skip("Policies are not supported");
            }
        }

        private void applyAndAssertPolicy(string policyId, string objectId)
        {
            cmisExtensionType extension = new cmisExtensionType();
            try
            {
                logger.log("[PolicyService->applyPolicy]");
                logger.log("Applying to Object with Id='" + objectId + "' Policy with Id='" + policyId + "'");
                policyServiceClient.applyPolicy(getAndAssertRepositoryId(), policyId, objectId, ref extension);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
        }

        private void removeAndAssertPolicy(string policyId, string objectId)
        {
            cmisExtensionType extension = new cmisExtensionType();
            try
            {
                logger.log("[PolicyService->removePolicy]");
                logger.log("Removing from Object with Id='" + objectId + "' Policy with Id='" + policyId + "'");
                policyServiceClient.removePolicy(getAndAssertRepositoryId(), policyId, objectId, ref extension);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
        }

        public void testAppliedPoliciesReceiving()
        {
            if (arePoliciesAllowed())
            {
                if (null == getAndAssertPolicyTypeId())
                {
                    logger.log("WARNING: no creatable Policy object-type was found! Primitive testing of Get Applied Policies");
                    HashSet<enumServiceException> exceptions = new HashSet<enumServiceException>();
                    exceptions.Add(enumServiceException.runtime);
                    exceptions.Add(enumServiceException.notSupported);
                    cmisObjectType[] appliedPolicies = getAndAssertAppliedPolicies(policyControllableObject.ObjectId, exceptions);
                    Assert.IsTrue((null == appliedPolicies) || (0 == appliedPolicies.Length), "No Policy was applied to object but Applied Policies response is not empty");
                }
                else
                {
                    if (null == policyControllableObject)
                    {
                        Assert.Skip("No Policy controllable object was found");
                    }
                    else
                    {
                        applyAndAssertPolicy(policyId, policyControllableObject.ObjectId);
                        getAndAssertAppliedPolicies(policyControllableObject.ObjectId, policyId, true);
                        removeAndAssertPolicy(policyId, policyControllableObject.ObjectId);
                        getAndAssertAppliedPolicies(policyControllableObject.ObjectId, policyId, false);
                    }
                }
            }
            else
            {
                Assert.Skip("Policies are not supported");
            }
        }

        private cmisObjectType[] getAndAssertAppliedPolicies(string objectId, string mandatoryPolicyId, bool mustBeApplied)
        {
            cmisObjectType[] result = getAndAssertAppliedPolicies(objectId, null);
            Assert.IsNotNull(result, "Applied Policies response is undefined");
            Assert.IsTrue(result.Length > 0, "Applied Policies response is empty");
            bool found = false;
            foreach (cmisObjectType cmisObject in result)
            {
                Assert.IsNotNull(cmisObject, "One of the Policy objects is in 'not set' state solely");
                Assert.IsNotNull(cmisObject.properties, "Properties of one of the Policy objects are undefined");
                Assert.IsNotNull(cmisObject.properties.Items, "Properties of one of the Policy objects are undefined");
                string id = (string)searchAndAssertPropertyByName(cmisObject.properties.Items, OBJECT_IDENTIFIER_PROPERTY, false, true);
                found = id.Equals(mandatoryPolicyId);
                if (found)
                {
                    break;
                }
            }
            if (mustBeApplied)
            {
                Assert.IsTrue(found, ("Expected Policy with Id='" + mandatoryPolicyId + "' was not applied to Object with Id='" + objectId + "'"));
            }
            else
            {
                Assert.IsFalse(found, "Unexpected Policy with Id='" + mandatoryPolicyId + "' was found between applied to Object with Id='" + objectId + "' policies after this Policy removing");
            }
            return result;
        }

        private cmisObjectType[] getAndAssertAppliedPolicies(string objectId, HashSet<enumServiceException> expectedExceptions)
        {
            cmisObjectType[] appliedPolicies = null;
            try
            {
                appliedPolicies = policyServiceClient.getAppliedPolicies(getAndAssertRepositoryId(), objectId, "*", null);
            }
            catch (FaultException<cmisFaultType> e)
            {
                assertException(e, expectedExceptions);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
            return appliedPolicies;
        }
    }
}
