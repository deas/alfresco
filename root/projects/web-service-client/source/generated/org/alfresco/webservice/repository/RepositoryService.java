/**
 * RepositoryService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.repository;

public interface RepositoryService extends javax.xml.rpc.Service {

/**
 * Provides read and write operations against a repository.
 */
    public java.lang.String getRepositoryServiceAddress();

    public org.alfresco.webservice.repository.RepositoryServiceSoapPort getRepositoryService() throws javax.xml.rpc.ServiceException;

    public org.alfresco.webservice.repository.RepositoryServiceSoapPort getRepositoryService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
