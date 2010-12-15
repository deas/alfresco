/**
 * MultiFilingServicePort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public interface MultiFilingServicePort extends java.rmi.Remote {
    public org.alfresco.repo.cmis.ws.AddObjectToFolderResponse addObjectToFolder(org.alfresco.repo.cmis.ws.AddObjectToFolder parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.RemoveObjectFromFolderResponse removeObjectFromFolder(org.alfresco.repo.cmis.ws.RemoveObjectFromFolder parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
}
