/**
 * ACLServicePort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public interface ACLServicePort extends java.rmi.Remote {
    public org.alfresco.repo.cmis.ws.GetACLResponse getACL(org.alfresco.repo.cmis.ws.GetACL parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.ApplyACLResponse applyACL(org.alfresco.repo.cmis.ws.ApplyACL parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
}
