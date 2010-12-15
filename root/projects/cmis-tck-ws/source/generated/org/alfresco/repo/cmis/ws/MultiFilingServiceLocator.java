/**
 * MultiFilingServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class MultiFilingServiceLocator extends org.apache.axis.client.Service implements org.alfresco.repo.cmis.ws.MultiFilingService {

    public MultiFilingServiceLocator() {
    }


    public MultiFilingServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MultiFilingServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MultiFilingServicePort
    private java.lang.String MultiFilingServicePort_address = "http://cmis/services/MultiFilingService.MultiFilingServicePort";

    public java.lang.String getMultiFilingServicePortAddress() {
        return MultiFilingServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MultiFilingServicePortWSDDServiceName = "MultiFilingServicePort";

    public java.lang.String getMultiFilingServicePortWSDDServiceName() {
        return MultiFilingServicePortWSDDServiceName;
    }

    public void setMultiFilingServicePortWSDDServiceName(java.lang.String name) {
        MultiFilingServicePortWSDDServiceName = name;
    }

    public org.alfresco.repo.cmis.ws.MultiFilingServicePort getMultiFilingServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MultiFilingServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMultiFilingServicePort(endpoint);
    }

    public org.alfresco.repo.cmis.ws.MultiFilingServicePort getMultiFilingServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.alfresco.repo.cmis.ws.MultiFilingServicePortBindingStub _stub = new org.alfresco.repo.cmis.ws.MultiFilingServicePortBindingStub(portAddress, this);
            _stub.setPortName(getMultiFilingServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMultiFilingServicePortEndpointAddress(java.lang.String address) {
        MultiFilingServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.alfresco.repo.cmis.ws.MultiFilingServicePort.class.isAssignableFrom(serviceEndpointInterface)) {
                org.alfresco.repo.cmis.ws.MultiFilingServicePortBindingStub _stub = new org.alfresco.repo.cmis.ws.MultiFilingServicePortBindingStub(new java.net.URL(MultiFilingServicePort_address), this);
                _stub.setPortName(getMultiFilingServicePortWSDDServiceName());
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
        if ("MultiFilingServicePort".equals(inputPortName)) {
            return getMultiFilingServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/ws/200908/", "MultiFilingService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/ws/200908/", "MultiFilingServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MultiFilingServicePort".equals(portName)) {
            setMultiFilingServicePortEndpointAddress(address);
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
