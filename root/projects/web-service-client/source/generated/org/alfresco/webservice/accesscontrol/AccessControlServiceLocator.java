/**
 * AccessControlServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.accesscontrol;

public class AccessControlServiceLocator extends org.apache.axis.client.Service implements org.alfresco.webservice.accesscontrol.AccessControlService {

/**
 * Access control service.
 */

    public AccessControlServiceLocator() {
    }


    public AccessControlServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AccessControlServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AccessControlService
    private java.lang.String AccessControlService_address = "http://localhost:8080/alfresco/api/AccessControlService";

    public java.lang.String getAccessControlServiceAddress() {
        return AccessControlService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AccessControlServiceWSDDServiceName = "AccessControlService";

    public java.lang.String getAccessControlServiceWSDDServiceName() {
        return AccessControlServiceWSDDServiceName;
    }

    public void setAccessControlServiceWSDDServiceName(java.lang.String name) {
        AccessControlServiceWSDDServiceName = name;
    }

    public org.alfresco.webservice.accesscontrol.AccessControlServiceSoapPort getAccessControlService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AccessControlService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAccessControlService(endpoint);
    }

    public org.alfresco.webservice.accesscontrol.AccessControlServiceSoapPort getAccessControlService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.alfresco.webservice.accesscontrol.AccessControlServiceSoapBindingStub _stub = new org.alfresco.webservice.accesscontrol.AccessControlServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getAccessControlServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAccessControlServiceEndpointAddress(java.lang.String address) {
        AccessControlService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.alfresco.webservice.accesscontrol.AccessControlServiceSoapPort.class.isAssignableFrom(serviceEndpointInterface)) {
                org.alfresco.webservice.accesscontrol.AccessControlServiceSoapBindingStub _stub = new org.alfresco.webservice.accesscontrol.AccessControlServiceSoapBindingStub(new java.net.URL(AccessControlService_address), this);
                _stub.setPortName(getAccessControlServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("AccessControlService".equals(inputPortName)) {
            return getAccessControlService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AccessControlService".equals(portName)) {
            setAccessControlServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
