/**
 * CmisChoiceUri.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisChoiceUri  extends org.alfresco.repo.cmis.ws.CmisChoice  implements java.io.Serializable {
    private org.apache.axis.types.URI[] value;

    private org.alfresco.repo.cmis.ws.CmisChoiceUri[] choice;

    public CmisChoiceUri() {
    }

    public CmisChoiceUri(
           java.lang.String displayName,
           org.apache.axis.types.URI[] value,
           org.alfresco.repo.cmis.ws.CmisChoiceUri[] choice) {
        super(
            displayName);
        this.value = value;
        this.choice = choice;
    }


    /**
     * Gets the value value for this CmisChoiceUri.
     * 
     * @return value
     */
    public org.apache.axis.types.URI[] getValue() {
        return value;
    }


    /**
     * Sets the value value for this CmisChoiceUri.
     * 
     * @param value
     */
    public void setValue(org.apache.axis.types.URI[] value) {
        this.value = value;
    }

    public org.apache.axis.types.URI getValue(int i) {
        return this.value[i];
    }

    public void setValue(int i, org.apache.axis.types.URI _value) {
        this.value[i] = _value;
    }


    /**
     * Gets the choice value for this CmisChoiceUri.
     * 
     * @return choice
     */
    public org.alfresco.repo.cmis.ws.CmisChoiceUri[] getChoice() {
        return choice;
    }


    /**
     * Sets the choice value for this CmisChoiceUri.
     * 
     * @param choice
     */
    public void setChoice(org.alfresco.repo.cmis.ws.CmisChoiceUri[] choice) {
        this.choice = choice;
    }

    public org.alfresco.repo.cmis.ws.CmisChoiceUri getChoice(int i) {
        return this.choice[i];
    }

    public void setChoice(int i, org.alfresco.repo.cmis.ws.CmisChoiceUri _value) {
        this.choice[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisChoiceUri)) return false;
        CmisChoiceUri other = (CmisChoiceUri) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              java.util.Arrays.equals(this.value, other.getValue()))) &&
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
        if (getValue() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getValue());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getValue(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
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
        new org.apache.axis.description.TypeDesc(CmisChoiceUri.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceUri"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("choice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "choice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChoiceUri"));
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
