/**
 * CmisTypeDocumentDefinitionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisTypeDocumentDefinitionType  extends org.alfresco.repo.cmis.ws.CmisTypeDefinitionType  implements java.io.Serializable {
    private boolean versionable;

    private org.alfresco.repo.cmis.ws.EnumContentStreamAllowed contentStreamAllowed;

    public CmisTypeDocumentDefinitionType() {
    }

    public CmisTypeDocumentDefinitionType(
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
           boolean versionable,
           org.alfresco.repo.cmis.ws.EnumContentStreamAllowed contentStreamAllowed) {
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
        this.versionable = versionable;
        this.contentStreamAllowed = contentStreamAllowed;
    }


    /**
     * Gets the versionable value for this CmisTypeDocumentDefinitionType.
     * 
     * @return versionable
     */
    public boolean isVersionable() {
        return versionable;
    }


    /**
     * Sets the versionable value for this CmisTypeDocumentDefinitionType.
     * 
     * @param versionable
     */
    public void setVersionable(boolean versionable) {
        this.versionable = versionable;
    }


    /**
     * Gets the contentStreamAllowed value for this CmisTypeDocumentDefinitionType.
     * 
     * @return contentStreamAllowed
     */
    public org.alfresco.repo.cmis.ws.EnumContentStreamAllowed getContentStreamAllowed() {
        return contentStreamAllowed;
    }


    /**
     * Sets the contentStreamAllowed value for this CmisTypeDocumentDefinitionType.
     * 
     * @param contentStreamAllowed
     */
    public void setContentStreamAllowed(org.alfresco.repo.cmis.ws.EnumContentStreamAllowed contentStreamAllowed) {
        this.contentStreamAllowed = contentStreamAllowed;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisTypeDocumentDefinitionType)) return false;
        CmisTypeDocumentDefinitionType other = (CmisTypeDocumentDefinitionType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.versionable == other.isVersionable() &&
            ((this.contentStreamAllowed==null && other.getContentStreamAllowed()==null) || 
             (this.contentStreamAllowed!=null &&
              this.contentStreamAllowed.equals(other.getContentStreamAllowed())));
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
        _hashCode += (isVersionable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getContentStreamAllowed() != null) {
            _hashCode += getContentStreamAllowed().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CmisTypeDocumentDefinitionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisTypeDocumentDefinitionType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("versionable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "versionable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contentStreamAllowed");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "contentStreamAllowed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumContentStreamAllowed"));
        elemField.setNillable(false);
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
