/**
 * RepositoryServicePort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public interface RepositoryServicePort extends java.rmi.Remote {
    public org.alfresco.repo.cmis.ws.CmisRepositoryEntryType[] getRepositories(org.alfresco.repo.cmis.ws.GetRepositories parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetRepositoryInfoResponse getRepositoryInfo(org.alfresco.repo.cmis.ws.GetRepositoryInfo parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetTypeChildrenResponse getTypeChildren(org.alfresco.repo.cmis.ws.GetTypeChildren parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CmisTypeContainer[] getTypeDescendants(org.alfresco.repo.cmis.ws.GetTypeDescendants parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetTypeDefinitionResponse getTypeDefinition(org.alfresco.repo.cmis.ws.GetTypeDefinition parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
}
