/**
 * DeleteTreeResponseFailedToDelete.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class DeleteTreeResponseFailedToDelete  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private java.lang.String[] objectIds;

    private org.apache.axis.message.MessageElement [] _any;

    public DeleteTreeResponseFailedToDelete() {
    }

    public DeleteTreeResponseFailedToDelete(
           java.lang.String[] objectIds,
           org.apache.axis.message.MessageElement [] _any) {
           this.objectIds = objectIds;
           this._any = _any;
    }


    /**
     * Gets the objectIds value for this DeleteTreeResponseFailedToDelete.
     * 
     * @return objectIds
     */
    public java.lang.String[] getObjectIds() {
        return objectIds;
    }


    /**
     * Sets the objectIds value for this DeleteTreeResponseFailedToDelete.
     * 
     * @param objectIds
     */
    public void setObjectIds(java.lang.String[] objectIds) {
        this.objectIds = objectIds;
    }

    public java.lang.String getObjectIds(int i) {
        return this.objectIds[i];
    }

    public void setObjectIds(int i, java.lang.String _value) {
        this.objectIds[i] = _value;
    }


    /**
     * Gets the _any value for this DeleteTreeResponseFailedToDelete.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this DeleteTreeResponseFailedToDelete.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DeleteTreeResponseFailedToDelete)) return false;
        DeleteTreeResponseFailedToDelete other = (DeleteTreeResponseFailedToDelete) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.objectIds==null && other.getObjectIds()==null) || 
             (this.objectIds!=null &&
              java.util.Arrays.equals(this.objectIds, other.getObjectIds()))) &&
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
        if (getObjectIds() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getObjectIds());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getObjectIds(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
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
        new org.apache.axis.description.TypeDesc(DeleteTreeResponseFailedToDelete.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">>deleteTreeResponse>failedToDelete"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectIds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "objectIds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
