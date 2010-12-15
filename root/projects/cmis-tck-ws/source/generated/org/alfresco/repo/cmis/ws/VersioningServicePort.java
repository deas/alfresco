/**
 * VersioningServicePort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public interface VersioningServicePort extends java.rmi.Remote {
    public org.alfresco.repo.cmis.ws.CheckOutResponse checkOut(org.alfresco.repo.cmis.ws.CheckOut parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CancelCheckOutResponse cancelCheckOut(org.alfresco.repo.cmis.ws.CancelCheckOut parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CheckInResponse checkIn(org.alfresco.repo.cmis.ws.CheckIn parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetObjectOfLatestVersionResponse getObjectOfLatestVersion(org.alfresco.repo.cmis.ws.GetObjectOfLatestVersion parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.GetPropertiesOfLatestVersionResponse getPropertiesOfLatestVersion(org.alfresco.repo.cmis.ws.GetPropertiesOfLatestVersion parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
    public org.alfresco.repo.cmis.ws.CmisObjectType[] getAllVersions(org.alfresco.repo.cmis.ws.GetAllVersions parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType;
}
