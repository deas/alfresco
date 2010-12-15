/**
 * AuthenticationService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.authentication;

public interface AuthenticationService extends javax.xml.rpc.Service {

/**
 * Provides simple authentication capability.
 */
    public java.lang.String getAuthenticationServiceAddress();

    public org.alfresco.webservice.authentication.AuthenticationServiceSoapPort getAuthenticationService() throws javax.xml.rpc.ServiceException;

    public org.alfresco.webservice.authentication.AuthenticationServiceSoapPort getAuthenticationService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
