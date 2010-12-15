/**
 * ClassificationService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.classification;

public interface ClassificationService extends javax.xml.rpc.Service {

/**
 * Provides support for classifying content resources.
 */
    public java.lang.String getClassificationServiceAddress();

    public org.alfresco.webservice.classification.ClassificationServiceSoapPort getClassificationService() throws javax.xml.rpc.ServiceException;

    public org.alfresco.webservice.classification.ClassificationServiceSoapPort getClassificationService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
