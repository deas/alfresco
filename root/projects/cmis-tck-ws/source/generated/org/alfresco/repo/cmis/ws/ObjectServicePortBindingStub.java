/**
 * ObjectServicePortBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

import java.util.Iterator;

import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;

public class ObjectServicePortBindingStub extends org.apache.axis.client.Stub implements org.alfresco.repo.cmis.ws.ObjectServicePort {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[17];
        _initOperationDesc1();
        _initOperationDesc2();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createDocument");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "createDocument"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createDocument"), org.alfresco.repo.cmis.ws.CreateDocument.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createDocumentResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.CreateDocumentResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "createDocumentResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createDocumentFromSource");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "createDocumentFromSource"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createDocumentFromSource"), org.alfresco.repo.cmis.ws.CreateDocumentFromSource.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createDocumentFromSourceResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.CreateDocumentFromSourceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "createDocumentFromSourceResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createFolder");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "createFolder"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createFolder"), org.alfresco.repo.cmis.ws.CreateFolder.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createFolderResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.CreateFolderResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "createFolderResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createRelationship");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "createRelationship"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createRelationship"), org.alfresco.repo.cmis.ws.CreateRelationship.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createRelationshipResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.CreateRelationshipResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "createRelationshipResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createPolicy");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "createPolicy"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createPolicy"), org.alfresco.repo.cmis.ws.CreatePolicy.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createPolicyResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.CreatePolicyResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "createPolicyResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAllowableActions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getAllowableActions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getAllowableActions"), org.alfresco.repo.cmis.ws.GetAllowableActions.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getAllowableActionsResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.GetAllowableActionsResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getAllowableActionsResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getObject");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getObject"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObject"), org.alfresco.repo.cmis.ws.GetObject.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.GetObjectResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getObjectResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getProperties");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getProperties"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getProperties"), org.alfresco.repo.cmis.ws.GetProperties.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getPropertiesResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.GetPropertiesResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getPropertiesResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRenditions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getRenditions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getRenditions"), org.alfresco.repo.cmis.ws.GetRenditions.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getRenditionsResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.CmisRenditionType[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getRenditionsResponse"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "renditions"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getObjectByPath");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getObjectByPath"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectByPath"), org.alfresco.repo.cmis.ws.GetObjectByPath.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectByPathResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.GetObjectByPathResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getObjectByPathResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getContentStream");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getContentStream"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getContentStream"), org.alfresco.repo.cmis.ws.GetContentStream.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getContentStreamResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.GetContentStreamResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "getContentStreamResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("updateProperties");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "updateProperties"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">updateProperties"), org.alfresco.repo.cmis.ws.UpdateProperties.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">updatePropertiesResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.UpdatePropertiesResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "updatePropertiesResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("moveObject");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "moveObject"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">moveObject"), org.alfresco.repo.cmis.ws.MoveObject.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">moveObjectResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.MoveObjectResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "moveObjectResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteObject");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "deleteObject"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteObject"), org.alfresco.repo.cmis.ws.DeleteObject.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteObjectResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.DeleteObjectResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "deleteObjectResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteTree");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "deleteTree"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteTree"), org.alfresco.repo.cmis.ws.DeleteTree.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteTreeResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.DeleteTreeResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "deleteTreeResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setContentStream");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "setContentStream"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">setContentStream"), org.alfresco.repo.cmis.ws.SetContentStream.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">setContentStreamResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.SetContentStreamResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "setContentStreamResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteContentStream");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "deleteContentStream"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteContentStream"), org.alfresco.repo.cmis.ws.DeleteContentStream.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteContentStreamResponse"));
        oper.setReturnClass(org.alfresco.repo.cmis.ws.DeleteContentStreamResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "deleteContentStreamResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFault"),
                      "org.alfresco.repo.cmis.ws.CmisFaultType",
                      new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType"), 
                      true
                     ));
        _operations[16] = oper;

    }

    public ObjectServicePortBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public ObjectServicePortBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public ObjectServicePortBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        addBindings0();
        addBindings1();
    }

    private void addBindings0() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisAccessControlEntryType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisAccessControlEntryType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisAccessControlListType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisAccessControlListType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisAccessControlPrincipalType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisAccessControlPrincipalType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisACLCapabilityType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisACLCapabilityType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisAllowableActionsType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisAllowableActionsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChangeEventType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisChangeEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoice");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisChoice.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceBoolean");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisChoiceBoolean.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceDateTime");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisChoiceDateTime.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceDecimal");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisChoiceDecimal.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceHtml");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisChoiceHtml.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceId");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisChoiceId.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceInteger");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisChoiceInteger.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceString");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisChoiceString.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceUri");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisChoiceUri.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisListOfIdsType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisListOfIdsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisObjectType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisObjectType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPermissionDefinition");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPermissionDefinition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPermissionMapping");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPermissionMapping.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertiesType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertiesType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisProperty");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisProperty.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyBoolean");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyBoolean.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyBooleanDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyBooleanDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDateTime");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyDateTime.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDateTimeDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyDateTimeDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDecimal");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyDecimal.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDecimalDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyDecimalDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyHtml");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyHtml.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyHtmlDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyHtmlDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyId");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyId.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyIdDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyIdDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyInteger");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyInteger.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyIntegerDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyIntegerDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyString");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyString.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyStringDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyStringDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyUri");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyUri.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyUriDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisPropertyUriDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisQueryType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisQueryType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisRenditionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisRenditionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisRepositoryCapabilitiesType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisRepositoryInfoType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisRepositoryInfoType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisTypeDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisTypeDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisTypeDocumentDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisTypeDocumentDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisTypeFolderDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisTypeFolderDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisTypePolicyDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisTypePolicyDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisTypeRelationshipDefinitionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisTypeRelationshipDefinitionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumACLPropagation");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumACLPropagation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumAllowableActionsKey");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumAllowableActionsKey.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumBaseObjectTypeIds");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumBasicPermissions");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumBasicPermissions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityACL");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumCapabilityACL.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityChanges");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumCapabilityChanges.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityContentStreamUpdates");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumCapabilityContentStreamUpdates.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityJoin");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumCapabilityJoin.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityQuery");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumCapabilityQuery.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityRendition");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumCapabilityRendition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCardinality");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumCardinality.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumContentStreamAllowed");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumContentStreamAllowed.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumDateTimeResolution");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumDateTimeResolution.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumDecimalPrecision");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumDecimalPrecision.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumIncludeRelationships");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumIncludeRelationships.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumPropertiesBase");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumPropertiesBase.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumPropertiesDocument");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumPropertiesDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumPropertiesFolder");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumPropertiesFolder.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumPropertiesPolicy");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumPropertiesPolicy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumPropertiesRelationship");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumPropertiesRelationship.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumPropertyType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumPropertyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumRelationshipDirection");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumRelationshipDirection.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumRenditionKind");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumRenditionKind.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumSupportedPermissions");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumSupportedPermissions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumTypeOfChanges");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumTypeOfChanges.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumUnfileObject");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumUnfileObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumUpdatability");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumUpdatability.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumUsers");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumUsers.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumVersioningState");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumVersioningState.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">>deleteTreeResponse>failedToDelete");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.DeleteTreeResponseFailedToDelete.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">addObjectToFolder");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.AddObjectToFolder.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">addObjectToFolderResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.AddObjectToFolderResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">applyACL");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.ApplyACL.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">applyACLResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.ApplyACLResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">applyPolicy");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.ApplyPolicy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">applyPolicyResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.ApplyPolicyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">cancelCheckOut");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CancelCheckOut.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">cancelCheckOutResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CancelCheckOutResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">checkIn");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CheckIn.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">checkInResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CheckInResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">checkOut");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CheckOut.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">checkOutResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CheckOutResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createDocument");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CreateDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createDocumentFromSource");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CreateDocumentFromSource.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createDocumentFromSourceResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CreateDocumentFromSourceResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createDocumentResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CreateDocumentResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createFolder");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CreateFolder.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createFolderResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CreateFolderResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createPolicy");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CreatePolicy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createPolicyResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CreatePolicyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createRelationship");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CreateRelationship.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">createRelationshipResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CreateRelationshipResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteContentStream");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.DeleteContentStream.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings1() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteContentStreamResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.DeleteContentStreamResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteObject");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.DeleteObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteObjectResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.DeleteObjectResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteTree");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.DeleteTree.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteTreeResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.DeleteTreeResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getACL");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetACL.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getACLResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetACLResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getAllowableActions");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetAllowableActions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getAllowableActionsResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetAllowableActionsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getAllVersions");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetAllVersions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getAllVersionsResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisObjectType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisObjectType");
            qName2 = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "objects");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getAppliedPolicies");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetAppliedPolicies.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getAppliedPoliciesResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisObjectType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisObjectType");
            qName2 = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "objects");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getCheckedOutDocs");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetCheckedOutDocs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getCheckedOutDocsResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetCheckedOutDocsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getChildren");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetChildren.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getChildrenResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetChildrenResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getContentChanges");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetContentChanges.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getContentChangesResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetContentChangesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getContentStream");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetContentStream.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getContentStreamResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetContentStreamResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getDescendants");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetDescendants.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getDescendantsResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectInFolderContainerType");
            qName2 = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "objects");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getFolderParent");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetFolderParent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getFolderParentResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetFolderParentResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getFolderTree");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetFolderTree.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getFolderTreeResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectInFolderContainerType");
            qName2 = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "objects");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObject");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectByPath");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetObjectByPath.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectByPathResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetObjectByPathResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectOfLatestVersion");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetObjectOfLatestVersion.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectOfLatestVersionResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetObjectOfLatestVersionResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectParents");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetObjectParents.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectParentsResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisObjectParentsType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectParentsType");
            qName2 = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "parents");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectRelationships");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetObjectRelationships.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectRelationshipsResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetObjectRelationshipsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetObjectResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getProperties");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetProperties.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getPropertiesOfLatestVersion");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetPropertiesOfLatestVersion.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getPropertiesOfLatestVersionResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetPropertiesOfLatestVersionResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getPropertiesResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetPropertiesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getRenditions");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetRenditions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getRenditionsResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisRenditionType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisRenditionType");
            qName2 = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "renditions");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getRepositories");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetRepositories.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getRepositoriesResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisRepositoryEntryType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisRepositoryEntryType");
            qName2 = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "repositories");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getRepositoryInfo");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetRepositoryInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getRepositoryInfoResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetRepositoryInfoResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getTypeChildren");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetTypeChildren.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getTypeChildrenResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetTypeChildrenResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getTypeDefinition");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetTypeDefinition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getTypeDefinitionResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetTypeDefinitionResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getTypeDescendants");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.GetTypeDescendants.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getTypeDescendantsResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisTypeContainer[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisTypeContainer");
            qName2 = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "types");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">moveObject");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.MoveObject.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">moveObjectResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.MoveObjectResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">query");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.Query.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">queryResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.QueryResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">removeObjectFromFolder");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.RemoveObjectFromFolder.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">removeObjectFromFolderResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.RemoveObjectFromFolderResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">removePolicy");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.RemovePolicy.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">removePolicyResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.RemovePolicyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">setContentStream");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.SetContentStream.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">setContentStreamResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.SetContentStreamResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">updateProperties");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.UpdateProperties.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">updatePropertiesResponse");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.UpdatePropertiesResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisACLType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisACLType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisContentStreamType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisContentStreamType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisExtensionType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisFaultType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisFaultType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectInFolderContainerType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectInFolderListType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisObjectInFolderListType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectInFolderType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisObjectInFolderType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectListType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisObjectListType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectParentsType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisObjectParentsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisRepositoryEntryType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisRepositoryEntryType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisTypeContainer");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisTypeContainer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisTypeDefinitionListType");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.CmisTypeDefinitionListType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "enumServiceException");
            cachedSerQNames.add(qName);
            cls = org.alfresco.repo.cmis.ws.EnumServiceException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public org.alfresco.repo.cmis.ws.CreateDocumentResponse createDocument(org.alfresco.repo.cmis.ws.CreateDocument parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "createDocument"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.CreateDocumentResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.CreateDocumentResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.CreateDocumentResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.CreateDocumentFromSourceResponse createDocumentFromSource(org.alfresco.repo.cmis.ws.CreateDocumentFromSource parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "createDocumentFromSource"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.CreateDocumentFromSourceResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.CreateDocumentFromSourceResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.CreateDocumentFromSourceResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.CreateFolderResponse createFolder(org.alfresco.repo.cmis.ws.CreateFolder parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "createFolder"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.CreateFolderResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.CreateFolderResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.CreateFolderResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.CreateRelationshipResponse createRelationship(org.alfresco.repo.cmis.ws.CreateRelationship parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "createRelationship"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.CreateRelationshipResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.CreateRelationshipResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.CreateRelationshipResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.CreatePolicyResponse createPolicy(org.alfresco.repo.cmis.ws.CreatePolicy parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "createPolicy"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.CreatePolicyResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.CreatePolicyResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.CreatePolicyResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.GetAllowableActionsResponse getAllowableActions(org.alfresco.repo.cmis.ws.GetAllowableActions parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getAllowableActions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.GetAllowableActionsResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.GetAllowableActionsResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.GetAllowableActionsResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.GetObjectResponse getObject(org.alfresco.repo.cmis.ws.GetObject parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getObject"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.GetObjectResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.GetObjectResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.GetObjectResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.GetPropertiesResponse getProperties(org.alfresco.repo.cmis.ws.GetProperties parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getProperties"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.GetPropertiesResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.GetPropertiesResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.GetPropertiesResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.CmisRenditionType[] getRenditions(org.alfresco.repo.cmis.ws.GetRenditions parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getRenditions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.CmisRenditionType[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.CmisRenditionType[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.CmisRenditionType[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.GetObjectByPathResponse getObjectByPath(org.alfresco.repo.cmis.ws.GetObjectByPath parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getObjectByPath"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.GetObjectByPathResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.GetObjectByPathResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.GetObjectByPathResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.GetContentStreamResponse getContentStream(org.alfresco.repo.cmis.ws.GetContentStream parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getContentStream"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            if(_call.getResponseMessage() != null) {
                Iterator iterator = _call.getResponseMessage().getAttachments();
                if(iterator.hasNext()){
                    byte b[];
                    try
                    {
                        AttachmentPart part = (AttachmentPart) iterator.next();
                        DataHandler dataHandler = part.getDataHandler();
                        b = new byte[dataHandler.getInputStream().available()];
                        dataHandler.getInputStream().read(b);
                        ((org.alfresco.repo.cmis.ws.GetContentStreamResponse) _resp).getContentStream().setStream(b);
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
            try {
                return (org.alfresco.repo.cmis.ws.GetContentStreamResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.GetContentStreamResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.GetContentStreamResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.UpdatePropertiesResponse updateProperties(org.alfresco.repo.cmis.ws.UpdateProperties parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "updateProperties"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.UpdatePropertiesResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.UpdatePropertiesResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.UpdatePropertiesResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.MoveObjectResponse moveObject(org.alfresco.repo.cmis.ws.MoveObject parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "moveObject"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.MoveObjectResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.MoveObjectResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.MoveObjectResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.DeleteObjectResponse deleteObject(org.alfresco.repo.cmis.ws.DeleteObject parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteObject"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.DeleteObjectResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.DeleteObjectResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.DeleteObjectResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.DeleteTreeResponse deleteTree(org.alfresco.repo.cmis.ws.DeleteTree parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteTree"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.DeleteTreeResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.DeleteTreeResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.DeleteTreeResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.SetContentStreamResponse setContentStream(org.alfresco.repo.cmis.ws.SetContentStream parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "setContentStream"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.SetContentStreamResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.SetContentStreamResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.SetContentStreamResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.repo.cmis.ws.DeleteContentStreamResponse deleteContentStream(org.alfresco.repo.cmis.ws.DeleteContentStream parameters) throws java.rmi.RemoteException, org.alfresco.repo.cmis.ws.CmisFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteContentStream"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.repo.cmis.ws.DeleteContentStreamResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.repo.cmis.ws.DeleteContentStreamResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.repo.cmis.ws.DeleteContentStreamResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.repo.cmis.ws.CmisFaultType) {
              throw (org.alfresco.repo.cmis.ws.CmisFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
