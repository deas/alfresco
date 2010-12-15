/**
 * CmisPropertyDecimalDefinitionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisPropertyDecimalDefinitionType  extends org.alfresco.repo.cmis.ws.CmisPropertyDefinitionType  implements java.io.Serializable {
    private org.alfresco.repo.cmis.ws.CmisPropertyDecimal defaultValue;

    private java.math.BigDecimal maxValue;

    private java.math.BigDecimal minValue;

    private org.alfresco.repo.cmis.ws.EnumDecimalPrecision precision;

    private org.alfresco.repo.cmis.ws.CmisChoiceDecimal[] choice;

    public CmisPropertyDecimalDefinitionType() {
    }

    public CmisPropertyDecimalDefinitionType(
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
           org.alfresco.repo.cmis.ws.CmisPropertyDecimal defaultValue,
           java.math.BigDecimal maxValue,
           java.math.BigDecimal minValue,
           org.alfresco.repo.cmis.ws.EnumDecimalPrecision precision,
           org.alfresco.repo.cmis.ws.CmisChoiceDecimal[] choice) {
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
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.precision = precision;
        this.choice = choice;
    }


    /**
     * Gets the defaultValue value for this CmisPropertyDecimalDefinitionType.
     * 
     * @return defaultValue
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyDecimal getDefaultValue() {
        return defaultValue;
    }


    /**
     * Sets the defaultValue value for this CmisPropertyDecimalDefinitionType.
     * 
     * @param defaultValue
     */
    public void setDefaultValue(org.alfresco.repo.cmis.ws.CmisPropertyDecimal defaultValue) {
        this.defaultValue = defaultValue;
    }


    /**
     * Gets the maxValue value for this CmisPropertyDecimalDefinitionType.
     * 
     * @return maxValue
     */
    public java.math.BigDecimal getMaxValue() {
        return maxValue;
    }


    /**
     * Sets the maxValue value for this CmisPropertyDecimalDefinitionType.
     * 
     * @param maxValue
     */
    public void setMaxValue(java.math.BigDecimal maxValue) {
        this.maxValue = maxValue;
    }


    /**
     * Gets the minValue value for this CmisPropertyDecimalDefinitionType.
     * 
     * @return minValue
     */
    public java.math.BigDecimal getMinValue() {
        return minValue;
    }


    /**
     * Sets the minValue value for this CmisPropertyDecimalDefinitionType.
     * 
     * @param minValue
     */
    public void setMinValue(java.math.BigDecimal minValue) {
        this.minValue = minValue;
    }


    /**
     * Gets the precision value for this CmisPropertyDecimalDefinitionType.
     * 
     * @return precision
     */
    public org.alfresco.repo.cmis.ws.EnumDecimalPrecision getPrecision() {
        return precision;
    }


    /**
     * Sets the precision value for this CmisPropertyDecimalDefinitionType.
     * 
     * @param precision
     */
    public void setPrecision(org.alfresco.repo.cmis.ws.EnumDecimalPrecision precision) {
        this.precision = precision;
    }


    /**
     * Gets the choice value for this CmisPropertyDecimalDefinitionType.
     * 
     * @return choice
     */
    public org.alfresco.repo.cmis.ws.CmisChoiceDecimal[] getChoice() {
        return choice;
    }


    /**
     * Sets the choice value for this CmisPropertyDecimalDefinitionType.
     * 
     * @param choice
     */
    public void setChoice(org.alfresco.repo.cmis.ws.CmisChoiceDecimal[] choice) {
        this.choice = choice;
    }

    public org.alfresco.repo.cmis.ws.CmisChoiceDecimal getChoice(int i) {
        return this.choice[i];
    }

    public void setChoice(int i, org.alfresco.repo.cmis.ws.CmisChoiceDecimal _value) {
        this.choice[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisPropertyDecimalDefinitionType)) return false;
        CmisPropertyDecimalDefinitionType other = (CmisPropertyDecimalDefinitionType) obj;
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
            ((this.maxValue==null && other.getMaxValue()==null) || 
             (this.maxValue!=null &&
              this.maxValue.equals(other.getMaxValue()))) &&
            ((this.minValue==null && other.getMinValue()==null) || 
             (this.minValue!=null &&
              this.minValue.equals(other.getMinValue()))) &&
            ((this.precision==null && other.getPrecision()==null) || 
             (this.precision!=null &&
              this.precision.equals(other.getPrecision()))) &&
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
        if (getMaxValue() != null) {
            _hashCode += getMaxValue().hashCode();
        }
        if (getMinValue() != null) {
            _hashCode += getMinValue().hashCode();
        }
        if (getPrecision() != null) {
            _hashCode += getPrecision().hashCode();
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
        new org.apache.axis.description.TypeDesc(CmisPropertyDecimalDefinitionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDecimalDefinitionType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("defaultValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "defaultValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDecimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "maxValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("minValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "minValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("precision");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "precision"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumDecimalPrecision"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("choice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "choice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceDecimal"));
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
