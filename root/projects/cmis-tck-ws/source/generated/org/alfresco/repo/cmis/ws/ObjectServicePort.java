/**
 * ObjectServicePort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public interface ObjectServicePort extends java.rmi.Remote {
    public org.alfresco.repo.cmis.ws.CreateDocumentResponse createDocument(org.alfresco.repo.cmis.ws.CreateDocument parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CreateDocumentFromSourceResponse createDocumentFromSource(org.alfresco.repo.cmis.ws.CreateDocumentFromSource parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CreateFolderResponse createFolder(org.alfresco.repo.cmis.ws.CreateFolder parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CreateRelationshipResponse createRelationship(org.alfresco.repo.cmis.ws.CreateRelationship parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CreatePolicyResponse createPolicy(org.alfresco.repo.cmis.ws.CreatePolicy parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetAllowableActionsResponse getAllowableActions(org.alfresco.repo.cmis.ws.GetAllowableActions parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetObjectResponse getObject(org.alfresco.repo.cmis.ws.GetObject parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetPropertiesResponse getProperties(org.alfresco.repo.cmis.ws.GetProperties parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CmisRenditionType[] getRenditions(org.alfresco.repo.cmis.ws.GetRenditions parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetObjectByPathResponse getObjectByPath(org.alfresco.repo.cmis.ws.GetObjectByPath parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetContentStreamResponse getContentStream(org.alfresco.repo.cmis.ws.GetContentStream parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.UpdatePropertiesResponse updateProperties(org.alfresco.repo.cmis.ws.UpdateProperties parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.MoveObjectResponse moveObject(org.alfresco.repo.cmis.ws.MoveObject parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.DeleteObjectResponse deleteObject(org.alfresco.repo.cmis.ws.DeleteObject parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.DeleteTreeResponse deleteTree(org.alfresco.repo.cmis.ws.DeleteTree parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.SetContentStreamResponse setContentStream(org.alfresco.repo.cmis.ws.SetContentStream parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.DeleteContentStreamResponse deleteContentStream(org.alfresco.repo.cmis.ws.DeleteContentStream parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
}
