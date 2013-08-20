/**
 * ContentServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.content;

public class ContentServiceSoapBindingStub extends org.apache.axis.client.Stub implements org.alfresco.webservice.content.ContentServiceSoapPort {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[5];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("read");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "items"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"), org.alfresco.webservice.types.Predicate.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "property"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "Content"));
        oper.setReturnClass(org.alfresco.webservice.content.Content[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "content"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentFault"),
                      "org.alfresco.webservice.content.ContentFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentFault"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("write");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "node"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Reference"), org.alfresco.webservice.types.Reference.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "property"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "content"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"), byte[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "format"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ContentFormat"), org.alfresco.webservice.types.ContentFormat.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "Content"));
        oper.setReturnClass(org.alfresco.webservice.content.Content.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "content"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentFault"),
                      "org.alfresco.webservice.content.ContentFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentFault"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("writeWithAttachment");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "node"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Reference"), org.alfresco.webservice.types.Reference.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "property"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "format"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ContentFormat"), org.alfresco.webservice.types.ContentFormat.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "Content"));
        oper.setReturnClass(org.alfresco.webservice.content.Content.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "content"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentFault"),
                      "org.alfresco.webservice.content.ContentFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentFault"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("clear");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "items"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"), org.alfresco.webservice.types.Predicate.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "property"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "Content"));
        oper.setReturnClass(org.alfresco.webservice.content.Content[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "content"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentFault"),
                      "org.alfresco.webservice.content.ContentFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentFault"), 
                      true
                     ));
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("transform");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "source"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Reference"), org.alfresco.webservice.types.Reference.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "property"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "destinationReference"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Reference"), org.alfresco.webservice.types.Reference.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "destinationProperty"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "destinationFormat"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "ContentFormat"), org.alfresco.webservice.types.ContentFormat.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "Content"));
        oper.setReturnClass(org.alfresco.webservice.content.Content.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "content"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentFault"),
                      "org.alfresco.webservice.content.ContentFault",
                      new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentFault"), 
                      true
                     ));
        _operations[4] = oper;

    }

    public ContentServiceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public ContentServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public ContentServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "Content");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.content.Content.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentFault");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.content.ContentFault.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "ContentSegment");
            cachedSerQNames.add(qName);
            cls = org.alfresco.webservice.content.ContentSegment.class;
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


    /**
     * Retrieves content from the repository.
     */
    public org.alfresco.webservice.content.Content[] read(org.alfresco.webservice.types.Predicate items, java.lang.String property) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/content/1.0/read");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "read"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {items, property});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.content.Content[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.content.Content[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.content.Content[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.content.ContentFault) {
              throw (org.alfresco.webservice.content.ContentFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }


    /**
     * Writes content to the repository.
     */
    public org.alfresco.webservice.content.Content write(org.alfresco.webservice.types.Reference node, java.lang.String property, byte[] content, org.alfresco.webservice.types.ContentFormat format) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/content/1.0/write");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "write"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {node, property, content, format});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.content.Content) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.content.Content) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.content.Content.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.content.ContentFault) {
              throw (org.alfresco.webservice.content.ContentFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }


    /**
     * Writes attached content to the repository.
     */
    public org.alfresco.webservice.content.Content writeWithAttachment(org.alfresco.webservice.types.Reference node, java.lang.String property, org.alfresco.webservice.types.ContentFormat format) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/content/1.0/writeWithAttachment");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "writeWithAttachment"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {node, property, format});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.content.Content) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.content.Content) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.content.Content.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.content.ContentFault) {
              throw (org.alfresco.webservice.content.ContentFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }


    /**
     * Clears content from the repository.
     */
    public org.alfresco.webservice.content.Content[] clear(org.alfresco.webservice.types.Predicate items, java.lang.String property) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/content/1.0/clear");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "clear"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {items, property});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.content.Content[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.content.Content[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.content.Content[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.content.ContentFault) {
              throw (org.alfresco.webservice.content.ContentFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }


    /**
     * Transforms content from one mimetype to another.
     */
    public org.alfresco.webservice.content.Content transform(org.alfresco.webservice.types.Reference source, java.lang.String property, org.alfresco.webservice.types.Reference destinationReference, java.lang.String destinationProperty, org.alfresco.webservice.types.ContentFormat destinationFormat) throws java.rmi.RemoteException, org.alfresco.webservice.content.ContentFault {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.alfresco.org/ws/service/content/1.0/transform");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/service/content/1.0", "transform"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {source, property, destinationReference, destinationProperty, destinationFormat});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.alfresco.webservice.content.Content) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.alfresco.webservice.content.Content) org.apache.axis.utils.JavaUtils.convert(_resp, org.alfresco.webservice.content.Content.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.alfresco.webservice.content.ContentFault) {
              throw (org.alfresco.webservice.content.ContentFault) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
