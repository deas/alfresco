/**
 * CmisObjectParentsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisObjectParentsType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.alfresco.repo.cmis.ws.CmisObjectType object;

    private java.lang.String relativePathSegment;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisObjectParentsType() {
    }

    public CmisObjectParentsType(
           org.alfresco.repo.cmis.ws.CmisObjectType object,
           java.lang.String relativePathSegment,
           org.apache.axis.message.MessageElement [] _any) {
           this.object = object;
           this.relativePathSegment = relativePathSegment;
           this._any = _any;
    }


    /**
     * Gets the object value for this CmisObjectParentsType.
     * 
     * @return object
     */
    public org.alfresco.repo.cmis.ws.CmisObjectType getObject() {
        return object;
    }


    /**
     * Sets the object value for this CmisObjectParentsType.
     * 
     * @param object
     */
    public void setObject(org.alfresco.repo.cmis.ws.CmisObjectType object) {
        this.object = object;
    }


    /**
     * Gets the relativePathSegment value for this CmisObjectParentsType.
     * 
     * @return relativePathSegment
     */
    public java.lang.String getRelativePathSegment() {
        return relativePathSegment;
    }


    /**
     * Sets the relativePathSegment value for this CmisObjectParentsType.
     * 
     * @param relativePathSegment
     */
    public void setRelativePathSegment(java.lang.String relativePathSegment) {
        this.relativePathSegment = relativePathSegment;
    }


    /**
     * Gets the _any value for this CmisObjectParentsType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisObjectParentsType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisObjectParentsType)) return false;
        CmisObjectParentsType other = (CmisObjectParentsType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.object==null && other.getObject()==null) || 
             (this.object!=null &&
              this.object.equals(other.getObject()))) &&
            ((this.relativePathSegment==null && other.getRelativePathSegment()==null) || 
             (this.relativePathSegment!=null &&
              this.relativePathSegment.equals(other.getRelativePathSegment()))) &&
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
        if (getObject() != null) {
            _hashCode += getObject().hashCode();
        }
        if (getRelativePathSegment() != null) {
            _hashCode += getRelativePathSegment().hashCode();
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
        new org.apache.axis.description.TypeDesc(CmisObjectParentsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectParentsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("object");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "object"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisObjectType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("relativePathSegment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "relativePathSegment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
