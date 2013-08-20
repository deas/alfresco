/**
 * AccessControlServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.accesscontrol;

public class AccessControlServiceSoapBindingStub extends org.apache.axis.client.Stub implements org.alfresco.webservice.accesscontrol.AccessControlServiceSoapPort {
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
        oper.setName("getACLs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "predicate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"), org.alfresco.webservice.types.Predicate.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "filter"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "ACE"), org.alfresco.webservice.accesscontrol.ACE.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "ACL"));
        oper.setReturnClass(org.alfresco.webservice.accesscontrol.ACL[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "acls"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addACEs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "predicate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"), org.alfresco.webservice.types.Predicate.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "aces"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "ACE"), org.alfresco.webservice.accesscontrol.ACE[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "ACL"));
        oper.setReturnClass(org.alfresco.webservice.accesscontrol.ACL[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "acls"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeACEs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "predicate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"), org.alfresco.webservice.types.Predicate.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "aces"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "ACE"), org.alfresco.webservice.accesscontrol.ACE[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "ACL"));
        oper.setReturnClass(org.alfresco.webservice.accesscontrol.ACL[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "acls"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getPermissions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "predicate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"), org.alfresco.webservice.types.Predicate.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "GetPermissionsResult"));
        oper.setReturnClass(org.alfresco.webservice.accesscontrol.GetPermissionsResult[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getClassPermissions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "classNames"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "GetClassPermissionsResult"));
        oper.setReturnClass(org.alfresco.webservice.accesscontrol.GetClassPermissionsResult[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("hasPermissions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "predicate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"), org.alfresco.webservice.types.Predicate.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "permissions"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "HasPermissionsResult"));
        oper.setReturnClass(org.alfresco.webservice.accesscontrol.HasPermissionsResult[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "results"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setInheritPermission");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "predicate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"), org.alfresco.webservice.types.Predicate.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "inheritPermission"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "ACL"));
        oper.setReturnClass(org.alfresco.webservice.accesscontrol.ACL[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "acls"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getOwners");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "predicate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"), org.alfresco.webservice.types.Predicate.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "OwnerResult"));
        oper.setReturnClass(org.alfresco.webservice.accesscontrol.OwnerResult[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "results"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setOwners");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "predicate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"), org.alfresco.webservice.types.Predicate.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "owner"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "OwnerResult"));
        oper.setReturnClass(org.alfresco.webservice.accesscontrol.OwnerResult[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "results"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAllAuthorities");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "filter"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AuthorityFilter"), org.alfresco.webservice.accesscontrol.AuthorityFilter.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "results"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getAuthorities");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "results"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("createAuthorities");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "parentAuthority"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "newAuthorites"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "NewAuthority"), org.alfresco.webservice.accesscontrol.NewAuthority[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "results"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteAuthorities");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "authorities"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addChildAuthorities");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "parentAuthority"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "authorities"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "results"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeChildAuthorities");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "parentAuthority"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "authorities"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getChildAuthorities");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "authority"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "filter"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "SiblingAuthorityFilter"), org.alfresco.webservice.accesscontrol.SiblingAuthorityFilter.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "results"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getParentAuthorities");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "authority"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "filter"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "SiblingAuthorityFilter"), org.alfresco.webservice.accesscontrol.SiblingAuthorityFilter.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "results"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"),
                      "org.alfresco.webservice.accesscontrol.AccessControlFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault"), 
                      true
                     ));
        _operations[16] = oper;

    }

    public AccessControlServiceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public AccessControlServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public AccessControlServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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
            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", ">ContentFormat>encoding");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", ">ContentFormat>mimetype");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", ">ResultSetRow>node");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.ResultSetRowNode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "AssociationDefinition");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.AssociationDefinition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Cardinality");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.Cardinality.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Category");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.Category.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ClassDefinition");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.ClassDefinition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Classification");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.Classification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ContentFormat");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.ContentFormat.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Name");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "NamedValue");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.NamedValue.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Node");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.Node.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "NodeDefinition");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.NodeDefinition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ParentReference");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.ParentReference.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Path");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.Predicate.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "PropertyDefinition");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.PropertyDefinition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Query");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.Query.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Reference");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.Reference.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ResultSet");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.ResultSet.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ResultSetMetaData");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.ResultSetMetaData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ResultSetRow");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.ResultSetRow.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "RoleDefinition");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.RoleDefinition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Store");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.Store.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "UUID");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ValueDefinition");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.ValueDefinition.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Version");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.Version.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "VersionHistory");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.types.VersionHistory.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessControlFault");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.accesscontrol.AccessControlFault.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AccessStatus");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.accesscontrol.AccessStatus.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "ACE");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.accesscontrol.ACE.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "ACL");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.accesscontrol.ACL.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "AuthorityFilter");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.accesscontrol.AuthorityFilter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "GetClassPermissionsResult");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.accesscontrol.GetClassPermissionsResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "GetPermissionsResult");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.accesscontrol.GetPermissionsResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "HasPermissionsResult");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.accesscontrol.HasPermissionsResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "NewAuthority");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.accesscontrol.NewAuthority.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "OwnerResult");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.accesscontrol.OwnerResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "SiblingAuthorityFilter");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.accesscontrol.SiblingAuthorityFilter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

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

    public org.alfresco.webservice.accesscontrol.ACL[] getACLs(org.alfresco.webservice.types.Predicate predicate, org.alfresco.webservice.accesscontrol.ACE filter) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/getACLs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "getACLs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {predicate, filter});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.accesscontrol.ACL[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.accesscontrol.ACL[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.accesscontrol.ACL[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.webservice.accesscontrol.ACL[] addACEs(org.alfresco.webservice.types.Predicate predicate, org.alfresco.webservice.accesscontrol.ACE[] aces) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/addACEs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "addACEs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {predicate, aces});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.accesscontrol.ACL[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.accesscontrol.ACL[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.accesscontrol.ACL[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.webservice.accesscontrol.ACL[] removeACEs(org.alfresco.webservice.types.Predicate predicate, org.alfresco.webservice.accesscontrol.ACE[] aces) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/removeACEs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "removeACEs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {predicate, aces});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.accesscontrol.ACL[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.accesscontrol.ACL[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.accesscontrol.ACL[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.webservice.accesscontrol.GetPermissionsResult[] getPermissions(org.alfresco.webservice.types.Predicate predicate) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/getPermissions");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "getPermissions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {predicate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.accesscontrol.GetPermissionsResult[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.accesscontrol.GetPermissionsResult[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.accesscontrol.GetPermissionsResult[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.webservice.accesscontrol.GetClassPermissionsResult[] getClassPermissions(java.lang.String[] classNames) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/getClassPermissions");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "getClassPermissions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {classNames});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.accesscontrol.GetClassPermissionsResult[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.accesscontrol.GetClassPermissionsResult[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.accesscontrol.GetClassPermissionsResult[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.webservice.accesscontrol.HasPermissionsResult[] hasPermissions(org.alfresco.webservice.types.Predicate predicate, java.lang.String[] permissions) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/hasPermissions");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "hasPermissions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {predicate, permissions});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.accesscontrol.HasPermissionsResult[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.accesscontrol.HasPermissionsResult[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.accesscontrol.HasPermissionsResult[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.webservice.accesscontrol.ACL[] setInheritPermission(org.alfresco.webservice.types.Predicate predicate, boolean inheritPermission) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/setInheritPermission");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "setInheritPermission"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {predicate, new java.lang.Boolean(inheritPermission)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.accesscontrol.ACL[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.accesscontrol.ACL[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.accesscontrol.ACL[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.webservice.accesscontrol.OwnerResult[] getOwners(org.alfresco.webservice.types.Predicate predicate) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/getOwners");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "getOwners"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {predicate});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.accesscontrol.OwnerResult[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.accesscontrol.OwnerResult[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.accesscontrol.OwnerResult[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.alfresco.webservice.accesscontrol.OwnerResult[] setOwners(org.alfresco.webservice.types.Predicate predicate, java.lang.String owner) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/setOwners");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "setOwners"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {predicate, owner});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.accesscontrol.OwnerResult[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.accesscontrol.OwnerResult[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.accesscontrol.OwnerResult[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String[] getAllAuthorities(org.alfresco.webservice.accesscontrol.AuthorityFilter filter) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/getAllAuthorities");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "getAllAuthorities"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {filter});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String[] getAuthorities() throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/getAuthorities");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "getAuthorities"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String[] createAuthorities(java.lang.String parentAuthority, org.alfresco.webservice.accesscontrol.NewAuthority[] newAuthorites) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/createAuthorities");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "createAuthorities"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parentAuthority, newAuthorites});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public void deleteAuthorities(java.lang.String[] authorities) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/deleteAuthorities");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "deleteAuthorities"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {authorities});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String[] addChildAuthorities(java.lang.String parentAuthority, java.lang.String[] authorities) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/addChildAuthorities");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "addChildAuthorities"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parentAuthority, authorities});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public void removeChildAuthorities(java.lang.String parentAuthority, java.lang.String[] authorities) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/removeChildAuthorities");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "removeChildAuthorities"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parentAuthority, authorities});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String[] getChildAuthorities(java.lang.String authority, org.alfresco.webservice.accesscontrol.SiblingAuthorityFilter filter) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/getChildAuthorities");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "getChildAuthorities"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {authority, filter});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String[] getParentAuthorities(java.lang.String authority, org.alfresco.webservice.accesscontrol.SiblingAuthorityFilter filter) throws java.rmi.RemoteException, org.alfresco.webservice.accesscontrol.AccessControlFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/accesscontrol/1.0/getParentAuthorities");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/accesscontrol/1.0", "getParentAuthorities"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {authority, filter});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.accesscontrol.AccessControlFault) {
              throw (org.alfresco.webservice.accesscontrol.AccessControlFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
