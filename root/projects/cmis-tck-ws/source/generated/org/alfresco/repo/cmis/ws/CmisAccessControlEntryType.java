/**
 * CmisAccessControlEntryType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisAccessControlEntryType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.alfresco.repo.cmis.ws.CmisAccessControlPrincipalType principal;

    private java.lang.String[] permission;

    private boolean direct;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisAccessControlEntryType() {
    }

    public CmisAccessControlEntryType(
           org.alfresco.repo.cmis.ws.CmisAccessControlPrincipalType principal,
           java.lang.String[] permission,
           boolean direct,
           org.apache.axis.message.MessageElement [] _any) {
           this.principal = principal;
           this.permission = permission;
           this.direct = direct;
           this._any = _any;
    }


    /**
     * Gets the principal value for this CmisAccessControlEntryType.
     * 
     * @return principal
     */
    public org.alfresco.repo.cmis.ws.CmisAccessControlPrincipalType getPrincipal() {
        return principal;
    }


    /**
     * Sets the principal value for this CmisAccessControlEntryType.
     * 
     * @param principal
     */
    public void setPrincipal(org.alfresco.repo.cmis.ws.CmisAccessControlPrincipalType principal) {
        this.principal = principal;
    }


    /**
     * Gets the permission value for this CmisAccessControlEntryType.
     * 
     * @return permission
     */
    public java.lang.String[] getPermission() {
        return permission;
    }


    /**
     * Sets the permission value for this CmisAccessControlEntryType.
     * 
     * @param permission
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
     * Gets the direct value for this CmisAccessControlEntryType.
     * 
     * @return direct
     */
    public boolean isDirect() {
        return direct;
    }


    /**
     * Sets the direct value for this CmisAccessControlEntryType.
     * 
     * @param direct
     */
    public void setDirect(boolean direct) {
        this.direct = direct;
    }


    /**
     * Gets the _any value for this CmisAccessControlEntryType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisAccessControlEntryType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisAccessControlEntryType)) return false;
        CmisAccessControlEntryType other = (CmisAccessControlEntryType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.principal==null && other.getPrincipal()==null) || 
             (this.principal!=null &&
              this.principal.equals(other.getPrincipal()))) &&
            ((this.permission==null && other.getPermission()==null) || 
             (this.permission!=null &&
              java.util.Arrays.equals(this.permission, other.getPermission()))) &&
            this.direct == other.isDirect() &&
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
        if (getPrincipal() != null) {
            _hashCode += getPrincipal().hashCode();
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
        _hashCode += (isDirect() ? Boolean.TRUE : Boolean.FALSE).hashCode();
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
        new org.apache.axis.description.TypeDesc(CmisAccessControlEntryType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisAccessControlEntryType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("principal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "principal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisAccessControlPrincipalType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("permission");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "permission"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("direct");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "direct"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
