/**
 * PolicyServicePort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public interface PolicyServicePort extends java.rmi.Remote {
    public org.alfresco.repo.cmis.ws.ApplyPolicyResponse applyPolicy(org.alfresco.repo.cmis.ws.ApplyPolicy parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.RemovePolicyResponse removePolicy(org.alfresco.repo.cmis.ws.RemovePolicy parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CmisObjectType[] getAppliedPolicies(org.alfresco.repo.cmis.ws.GetAppliedPolicies parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
}
