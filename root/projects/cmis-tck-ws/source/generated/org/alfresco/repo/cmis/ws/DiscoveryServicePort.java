/**
 * DiscoveryServicePort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public interface DiscoveryServicePort extends java.rmi.Remote {
    public org.alfresco.repo.cmis.ws.QueryResponse query(org.alfresco.repo.cmis.ws.Query parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetContentChangesResponse getContentChanges(org.alfresco.repo.cmis.ws.GetContentChanges parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
}
