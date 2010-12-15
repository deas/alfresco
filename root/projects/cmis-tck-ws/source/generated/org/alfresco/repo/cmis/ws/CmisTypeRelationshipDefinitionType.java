/**
 * CmisTypeRelationshipDefinitionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisTypeRelationshipDefinitionType  extends org.alfresco.repo.cmis.ws.CmisTypeDefinitionType  implements java.io.Serializable {
    private java.lang.String[] allowedSourceTypes;

    private java.lang.String[] allowedTargetTypes;

    public CmisTypeRelationshipDefinitionType() {
    }

    public CmisTypeRelationshipDefinitionType(
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
           org.apache.axis.message.MessageElement [] _any,
           java.lang.String[] allowedSourceTypes,
           java.lang.String[] allowedTargetTypes) {
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
        this.allowedSourceTypes = allowedSourceTypes;
        this.allowedTargetTypes = allowedTargetTypes;
    }


    /**
     * Gets the allowedSourceTypes value for this CmisTypeRelationshipDefinitionType.
     * 
     * @return allowedSourceTypes
     */
    public java.lang.String[] getAllowedSourceTypes() {
        return allowedSourceTypes;
    }


    /**
     * Sets the allowedSourceTypes value for this CmisTypeRelationshipDefinitionType.
     * 
     * @param allowedSourceTypes
     */
    public void setAllowedSourceTypes(java.lang.String[] allowedSourceTypes) {
        this.allowedSourceTypes = allowedSourceTypes;
    }

    public java.lang.String getAllowedSourceTypes(int i) {
        return this.allowedSourceTypes[i];
    }

    public void setAllowedSourceTypes(int i, java.lang.String _value) {
        this.allowedSourceTypes[i] = _value;
    }


    /**
     * Gets the allowedTargetTypes value for this CmisTypeRelationshipDefinitionType.
     * 
     * @return allowedTargetTypes
     */
    public java.lang.String[] getAllowedTargetTypes() {
        return allowedTargetTypes;
    }


    /**
     * Sets the allowedTargetTypes value for this CmisTypeRelationshipDefinitionType.
     * 
     * @param allowedTargetTypes
     */
    public void setAllowedTargetTypes(java.lang.String[] allowedTargetTypes) {
        this.allowedTargetTypes = allowedTargetTypes;
    }

    public java.lang.String getAllowedTargetTypes(int i) {
        return this.allowedTargetTypes[i];
    }

    public void setAllowedTargetTypes(int i, java.lang.String _value) {
        this.allowedTargetTypes[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisTypeRelationshipDefinitionType)) return false;
        CmisTypeRelationshipDefinitionType other = (CmisTypeRelationshipDefinitionType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.allowedSourceTypes==null && other.getAllowedSourceTypes()==null) || 
             (this.allowedSourceTypes!=null &&
              java.util.Arrays.equals(this.allowedSourceTypes, other.getAllowedSourceTypes()))) &&
            ((this.allowedTargetTypes==null && other.getAllowedTargetTypes()==null) || 
             (this.allowedTargetTypes!=null &&
              java.util.Arrays.equals(this.allowedTargetTypes, other.getAllowedTargetTypes())));
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
        if (getAllowedSourceTypes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAllowedSourceTypes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAllowedSourceTypes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAllowedTargetTypes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAllowedTargetTypes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAllowedTargetTypes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CmisTypeRelationshipDefinitionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisTypeRelationshipDefinitionType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allowedSourceTypes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "allowedSourceTypes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        elemField.setMinOccurs(0);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allowedTargetTypes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "allowedTargetTypes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        elemField.setMinOccurs(0);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
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
