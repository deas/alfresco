/**
 * CmisPermissionMapping.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisPermissionMapping  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.alfresco.repo.cmis.ws.EnumAllowableActionsKey key;

    /* Multiple entries are OR'ed together. Any
     * 						permission that specified is
     * 						sufficient to provide rights required
     * 						in key */
    private java.lang.String[] permission;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisPermissionMapping() {
    }

    public CmisPermissionMapping(
           org.alfresco.repo.cmis.ws.EnumAllowableActionsKey key,
           java.lang.String[] permission,
           org.apache.axis.message.MessageElement [] _any) {
           this.key = key;
           this.permission = permission;
           this._any = _any;
    }


    /**
     * Gets the key value for this CmisPermissionMapping.
     * 
     * @return key
     */
    public org.alfresco.repo.cmis.ws.EnumAllowableActionsKey getKey() {
        return key;
    }


    /**
     * Sets the key value for this CmisPermissionMapping.
     * 
     * @param key
     */
    public void setKey(org.alfresco.repo.cmis.ws.EnumAllowableActionsKey key) {
        this.key = key;
    }


    /**
     * Gets the permission value for this CmisPermissionMapping.
     * 
     * @return permission   * Multiple entries are OR'ed together. Any
     * 						permission that specified is
     * 						sufficient to provide rights required
     * 						in key
     */
    public java.lang.String[] getPermission() {
        return permission;
    }


    /**
     * Sets the permission value for this CmisPermissionMapping.
     * 
     * @param permission   * Multiple entries are OR'ed together. Any
     * 						permission that specified is
     * 						sufficient to provide rights required
     * 						in key
     */
    public void setPermission(java.lang.String[] permission) {
        this.permission = permission;
    }

    public java.lang.String getPermission(int i) {
        return this.permission[i];
    }

    public void setPermission(int i, java.lang.String _value) {
        this.permission[i] = _value;
    }


    /**
     * Gets the _any value for this CmisPermissionMapping.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisPermissionMapping.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisPermissionMapping)) return false;
        CmisPermissionMapping other = (CmisPermissionMapping) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.key==null && other.getKey()==null) || 
             (this.key!=null &&
              this.key.equals(other.getKey()))) &&
            ((this.permission==null && other.getPermission()==null) || 
             (this.permission!=null &&
              java.util.Arrays.equals(this.permission, other.getPermission()))) &&
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
        if (getKey() != null) {
            _hashCode += getKey().hashCode();
        }
        if (getPermission() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPermission());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPermission(), i);
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
        new org.apache.axis.description.TypeDesc(CmisPermissionMapping.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPermissionMapping"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumAllowableActionsKey"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("permission");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "permission"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
