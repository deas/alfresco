/**
 * AuthoringService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.authoring;

public interface AuthoringService extends javax.xml.rpc.Service {

/**
 * Provides support for collaborative editing of content.
 */
    public java.lang.String getAuthoringServiceAddress();

    public org.alfresco.webservice.authoring.AuthoringServiceSoapPort getAuthoringService() throws javax.xml.rpc.ServiceException;

    public org.alfresco.webservice.authoring.AuthoringServiceSoapPort getAuthoringService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
