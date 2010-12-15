/**
 * CmisChangeEventType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisChangeEventType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.alfresco.repo.cmis.ws.EnumTypeOfChanges changeType;

    private java.util.Calendar changeTime;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisChangeEventType() {
    }

    public CmisChangeEventType(
           org.alfresco.repo.cmis.ws.EnumTypeOfChanges changeType,
           java.util.Calendar changeTime,
           org.apache.axis.message.MessageElement [] _any) {
           this.changeType = changeType;
           this.changeTime = changeTime;
           this._any = _any;
    }


    /**
     * Gets the changeType value for this CmisChangeEventType.
     * 
     * @return changeType
     */
    public org.alfresco.repo.cmis.ws.EnumTypeOfChanges getChangeType() {
        return changeType;
    }


    /**
     * Sets the changeType value for this CmisChangeEventType.
     * 
     * @param changeType
     */
    public void setChangeType(org.alfresco.repo.cmis.ws.EnumTypeOfChanges changeType) {
        this.changeType = changeType;
    }


    /**
     * Gets the changeTime value for this CmisChangeEventType.
     * 
     * @return changeTime
     */
    public java.util.Calendar getChangeTime() {
        return changeTime;
    }


    /**
     * Sets the changeTime value for this CmisChangeEventType.
     * 
     * @param changeTime
     */
    public void setChangeTime(java.util.Calendar changeTime) {
        this.changeTime = changeTime;
    }


    /**
     * Gets the _any value for this CmisChangeEventType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisChangeEventType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisChangeEventType)) return false;
        CmisChangeEventType other = (CmisChangeEventType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.changeType==null && other.getChangeType()==null) || 
             (this.changeType!=null &&
              this.changeType.equals(other.getChangeType()))) &&
            ((this.changeTime==null && other.getChangeTime()==null) || 
             (this.changeTime!=null &&
              this.changeTime.equals(other.getChangeTime()))) &&
            ((this._any==null && other.get_any()==null) || 
             (this._any!=null &&
              java.util.Arrays.equals(this._any, other.get_any())));
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
        if (getChangeType() != null) {
            _hashCode += getChangeType().hashCode();
        }
        if (getChangeTime() != null) {
            _hashCode += getChangeTime().hashCode();
        }
        if (get_any() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(get_any());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(get_any(), i);
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
        new org.apache.axis.description.TypeDesc(CmisChangeEventType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChangeEventType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changeType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "changeType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumTypeOfChanges"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changeTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "changeTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
