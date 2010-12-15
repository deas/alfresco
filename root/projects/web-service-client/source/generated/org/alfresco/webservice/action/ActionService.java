/**
 * ActionService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.action;

public interface ActionService extends javax.xml.rpc.Service {

/**
 * Provides Action and Rule manipulation methods.
 */
    public java.lang.String getActionServiceAddress();

    public org.alfresco.webservice.action.ActionServiceSoapPort getActionService() throws javax.xml.rpc.ServiceException;

    public org.alfresco.webservice.action.ActionServiceSoapPort getActionService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
