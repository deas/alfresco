/**
 * NavigationServicePort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public interface NavigationServicePort extends java.rmi.Remote {
    public org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType[] getDescendants(org.alfresco.repo.cmis.ws.GetDescendants parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetChildrenResponse getChildren(org.alfresco.repo.cmis.ws.GetChildren parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetFolderParentResponse getFolderParent(org.alfresco.repo.cmis.ws.GetFolderParent parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType[] getFolderTree(org.alfresco.repo.cmis.ws.GetFolderTree parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CmisObjectParentsType[] getObjectParents(org.alfresco.repo.cmis.ws.GetObjectParents parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetCheckedOutDocsResponse getCheckedOutDocs(org.alfresco.repo.cmis.ws.GetCheckedOutDocs parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
}
