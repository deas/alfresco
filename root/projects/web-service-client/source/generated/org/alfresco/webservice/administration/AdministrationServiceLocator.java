/**
 * AdministrationServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.administration;

public class AdministrationServiceLocator extends org.apache.axis.client.Service implements org.alfresco.webservice.administration.AdministrationService {

/**
 * Administration service.
 */

    public AdministrationServiceLocator() {
    }


    public AdministrationServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AdministrationServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AdministrationService
    private java.lang.String AdministrationService_address = "http://localhost:8080/alfresco/api/AdministrationService";

    public java.lang.String getAdministrationServiceAddress() {
        return AdministrationService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AdministrationServiceWSDDServiceName = "AdministrationService";

    public java.lang.String getAdministrationServiceWSDDServiceName() {
        return AdministrationServiceWSDDServiceName;
    }

    public void setAdministrationServiceWSDDServiceName(java.lang.String name) {
        AdministrationServiceWSDDServiceName = name;
    }

    public org.alfresco.webservice.administration.AdministrationServiceSoapPort getAdministrationService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AdministrationService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAdministrationService(endpoint);
    }

    public org.alfresco.webservice.administration.AdministrationServiceSoapPort getAdministrationService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.alfresco.webservice.administration.AdministrationServiceSoapBindingStub _stub = new org.alfresco.webservice.administration.AdministrationServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getAdministrationServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAdministrationServiceEndpointAddress(java.lang.String address) {
        AdministrationService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.alfresco.webservice.administration.AdministrationServiceSoapPort.class.isAssignableFrom(serviceEndpointInterface)) {
                org.alfresco.webservice.administration.AdministrationServiceSoapBindingStub _stub = new org.alfresco.webservice.administration.AdministrationServiceSoapBindingStub(new java.net.URL(AdministrationService_address), this);
                _stub.setPortName(getAdministrationServiceWSDDServiceName());
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
        if ("AdministrationService".equals(inputPortName)) {
            return getAdministrationService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/administration/1.0", "AdministrationService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/administration/1.0", "AdministrationService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AdministrationService".equals(portName)) {
            setAdministrationServiceEndpointAddress(address);
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
