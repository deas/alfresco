/**
 * CmisPropertyDateTimeDefinitionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisPropertyDateTimeDefinitionType  extends org.alfresco.repo.cmis.ws.CmisPropertyDefinitionType  implements java.io.Serializable {
    private org.alfresco.repo.cmis.ws.CmisPropertyDateTime defaultValue;

    private org.alfresco.repo.cmis.ws.EnumDateTimeResolution resolution;

    private org.alfresco.repo.cmis.ws.CmisChoiceDateTime[] choice;

    public CmisPropertyDateTimeDefinitionType() {
    }

    public CmisPropertyDateTimeDefinitionType(
           java.lang.String id,
           java.lang.String localName,
           org.apache.axis.types.URI localNamespace,
           java.lang.String displayName,
           java.lang.String queryName,
           java.lang.String description,
           org.alfresco.repo.cmis.ws.EnumPropertyType propertyType,
           org.alfresco.repo.cmis.ws.EnumCardinality cardinality,
           org.alfresco.repo.cmis.ws.EnumUpdatability updatability,
           java.lang.Boolean inherited,
           boolean required,
           boolean queryable,
           boolean orderable,
           java.lang.Boolean openChoice,
           org.apache.axis.message.MessageElement [] _any,
           org.alfresco.repo.cmis.ws.CmisPropertyDateTime defaultValue,
           org.alfresco.repo.cmis.ws.EnumDateTimeResolution resolution,
           org.alfresco.repo.cmis.ws.CmisChoiceDateTime[] choice) {
        super(
            id,
            localName,
            localNamespace,
            displayName,
            queryName,
            description,
            propertyType,
            cardinality,
            updatability,
            inherited,
            required,
            queryable,
            orderable,
            openChoice,
            _any);
        this.defaultValue = defaultValue;
        this.resolution = resolution;
        this.choice = choice;
    }


    /**
     * Gets the defaultValue value for this CmisPropertyDateTimeDefinitionType.
     * 
     * @return defaultValue
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyDateTime getDefaultValue() {
        return defaultValue;
    }


    /**
     * Sets the defaultValue value for this CmisPropertyDateTimeDefinitionType.
     * 
     * @param defaultValue
     */
    public void setDefaultValue(org.alfresco.repo.cmis.ws.CmisPropertyDateTime defaultValue) {
        this.defaultValue = defaultValue;
    }


    /**
     * Gets the resolution value for this CmisPropertyDateTimeDefinitionType.
     * 
     * @return resolution
     */
    public org.alfresco.repo.cmis.ws.EnumDateTimeResolution getResolution() {
        return resolution;
    }


    /**
     * Sets the resolution value for this CmisPropertyDateTimeDefinitionType.
     * 
     * @param resolution
     */
    public void setResolution(org.alfresco.repo.cmis.ws.EnumDateTimeResolution resolution) {
        this.resolution = resolution;
    }


    /**
     * Gets the choice value for this CmisPropertyDateTimeDefinitionType.
     * 
     * @return choice
     */
    public org.alfresco.repo.cmis.ws.CmisChoiceDateTime[] getChoice() {
        return choice;
    }


    /**
     * Sets the choice value for this CmisPropertyDateTimeDefinitionType.
     * 
     * @param choice
     */
    public void setChoice(org.alfresco.repo.cmis.ws.CmisChoiceDateTime[] choice) {
        this.choice = choice;
    }

    public org.alfresco.repo.cmis.ws.CmisChoiceDateTime getChoice(int i) {
        return this.choice[i];
    }

    public void setChoice(int i, org.alfresco.repo.cmis.ws.CmisChoiceDateTime _value) {
        this.choice[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisPropertyDateTimeDefinitionType)) return false;
        CmisPropertyDateTimeDefinitionType other = (CmisPropertyDateTimeDefinitionType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.defaultValue==null && other.getDefaultValue()==null) || 
             (this.defaultValue!=null &&
              this.defaultValue.equals(other.getDefaultValue()))) &&
            ((this.resolution==null && other.getResolution()==null) || 
             (this.resolution!=null &&
              this.resolution.equals(other.getResolution()))) &&
            ((this.choice==null && other.getChoice()==null) || 
             (this.choice!=null &&
              java.util.Arrays.equals(this.choice, other.getChoice())));
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
        if (getDefaultValue() != null) {
            _hashCode += getDefaultValue().hashCode();
        }
        if (getResolution() != null) {
            _hashCode += getResolution().hashCode();
        }
        if (getChoice() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChoice());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChoice(), i);
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
        new org.apache.axis.description.TypeDesc(CmisPropertyDateTimeDefinitionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDateTimeDefinitionType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("defaultValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "defaultValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resolution");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "resolution"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumDateTimeResolution"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("choice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "choice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceDateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
