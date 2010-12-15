/**
 * CmisProperty.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisProperty  implements java.io.Serializable {
    private java.lang.String propertyDefinitionId;  // attribute

    private java.lang.String localName;  // attribute

    private java.lang.String displayName;  // attribute

    private java.lang.String queryName;  // attribute

    public CmisProperty() {
    }

    public CmisProperty(
           java.lang.String propertyDefinitionId,
           java.lang.String localName,
           java.lang.String displayName,
           java.lang.String queryName) {
           this.propertyDefinitionId = propertyDefinitionId;
           this.localName = localName;
           this.displayName = displayName;
           this.queryName = queryName;
    }


    /**
     * Gets the propertyDefinitionId value for this CmisProperty.
     * 
     * @return propertyDefinitionId
     */
    public java.lang.String getPropertyDefinitionId() {
        return propertyDefinitionId;
    }


    /**
     * Sets the propertyDefinitionId value for this CmisProperty.
     * 
     * @param propertyDefinitionId
     */
    public void setPropertyDefinitionId(java.lang.String propertyDefinitionId) {
        this.propertyDefinitionId = propertyDefinitionId;
    }


    /**
     * Gets the localName value for this CmisProperty.
     * 
     * @return localName
     */
    public java.lang.String getLocalName() {
        return localName;
    }


    /**
     * Sets the localName value for this CmisProperty.
     * 
     * @param localName
     */
    public void setLocalName(java.lang.String localName) {
        this.localName = localName;
    }


    /**
     * Gets the displayName value for this CmisProperty.
     * 
     * @return displayName
     */
    public java.lang.String getDisplayName() {
        return displayName;
    }


    /**
     * Sets the displayName value for this CmisProperty.
     * 
     * @param displayName
     */
    public void setDisplayName(java.lang.String displayName) {
        this.displayName = displayName;
    }


    /**
     * Gets the queryName value for this CmisProperty.
     * 
     * @return queryName
     */
    public java.lang.String getQueryName() {
        return queryName;
    }


    /**
     * Sets the queryName value for this CmisProperty.
     * 
     * @param queryName
     */
    public void setQueryName(java.lang.String queryName) {
        this.queryName = queryName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisProperty)) return false;
        CmisProperty other = (CmisProperty) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.propertyDefinitionId==null && other.getPropertyDefinitionId()==null) || 
             (this.propertyDefinitionId!=null &&
              this.propertyDefinitionId.equals(other.getPropertyDefinitionId()))) &&
            ((this.localName==null && other.getLocalName()==null) || 
             (this.localName!=null &&
              this.localName.equals(other.getLocalName()))) &&
            ((this.displayName==null && other.getDisplayName()==null) || 
             (this.displayName!=null &&
              this.displayName.equals(other.getDisplayName()))) &&
            ((this.queryName==null && other.getQueryName()==null) || 
             (this.queryName!=null &&
              this.queryName.equals(other.getQueryName())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getPropertyDefinitionId() != null) {
            _hashCode += getPropertyDefinitionId().hashCode();
        }
        if (getLocalName() != null) {
            _hashCode += getLocalName().hashCode();
        }
        if (getDisplayName() != null) {
            _hashCode += getDisplayName().hashCode();
        }
        if (getQueryName() != null) {
            _hashCode += getQueryName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CmisProperty.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisProperty"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("propertyDefinitionId");
        attrField.setXmlName(new javax.xml.namespace.QName("", "propertyDefinitionId"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("localName");
        attrField.setXmlName(new javax.xml.namespace.QName("", "localName"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("displayName");
        attrField.setXmlName(new javax.xml.namespace.QName("", "displayName"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("queryName");
        attrField.setXmlName(new javax.xml.namespace.QName("", "queryName"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
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
