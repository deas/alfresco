/**
 * CmisTypePolicyDefinitionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisTypePolicyDefinitionType  extends org.alfresco.repo.cmis.ws.CmisTypeDefinitionType  implements java.io.Serializable {
    public CmisTypePolicyDefinitionType() {
    }

    public CmisTypePolicyDefinitionType(
           java.lang.String id,
           java.lang.String localName,
           org.apache.axis.types.URI localNamespace,
           java.lang.String displayName,
           java.lang.String queryName,
           java.lang.String description,
           org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds baseId,
           java.lang.String parentId,
           boolean creatable,
           boolean fileable,
           boolean queryable,
           boolean fulltextIndexed,
           boolean includedInSupertypeQuery,
           boolean controllablePolicy,
           boolean controllableACL,
           org.alfresco.repo.cmis.ws.CmisPropertyBooleanDefinitionType[] propertyBooleanDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyDateTimeDefinitionType[] propertyDateTimeDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyDecimalDefinitionType[] propertyDecimalDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyIdDefinitionType[] propertyIdDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyIntegerDefinitionType[] propertyIntegerDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyHtmlDefinitionType[] propertyHtmlDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyStringDefinitionType[] propertyStringDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyUriDefinitionType[] propertyUriDefinition,
           org.apache.axis.message.MessageElement [] _any) {
        super(
            id,
            localName,
            localNamespace,
            displayName,
            queryName,
            description,
            baseId,
            parentId,
            creatable,
            fileable,
            queryable,
            fulltextIndexed,
            includedInSupertypeQuery,
            controllablePolicy,
            controllableACL,
            propertyBooleanDefinition,
            propertyDateTimeDefinition,
            propertyDecimalDefinition,
            propertyIdDefinition,
            propertyIntegerDefinition,
            propertyHtmlDefinition,
            propertyStringDefinition,
            propertyUriDefinition,
            _any);
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisTypePolicyDefinitionType)) return false;
        CmisTypePolicyDefinitionType other = (CmisTypePolicyDefinitionType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj);
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CmisTypePolicyDefinitionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisTypePolicyDefinitionType"));
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
