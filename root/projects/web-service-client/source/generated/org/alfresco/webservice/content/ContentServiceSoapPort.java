/**
 * ContentServiceSoapPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.content;

public interface ContentServiceSoapPort extends java.rmi.Remote {

    /**
     * Retrieves content from the repository.
     */
    public org.alfresco.webservice.content.Content[] read(org.alfresco.webservice.types.Predicate items, java.lang.String property) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault;

    /**
     * Writes content to the repository.
     */
    public org.alfresco.webservice.content.Content write(org.alfresco.webservice.types.Reference node, java.lang.String property, byte[] content, org.alfresco.webservice.types.ContentFormat format) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault;

    /**
     * Writes the attached content into the repository.
     */
    public org.alfresco.webservice.content.Content writeWithAttachment(org.alfresco.webservice.types.Reference node, java.lang.String property, org.alfresco.webservice.types.ContentFormat format) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault;

    /**
     * Clears content from the repository.
     */
    public org.alfresco.webservice.content.Content[] clear(org.alfresco.webservice.types.Predicate items, java.lang.String property) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault;

    /**
     * Transforms content from one mimetype to another.
     */
    public org.alfresco.webservice.content.Content transform(org.alfresco.webservice.types.Reference source, java.lang.String property, org.alfresco.webservice.types.Reference destinationReference, java.lang.String destinationProperty, org.alfresco.webservice.types.ContentFormat destinationFormat) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault;
}
