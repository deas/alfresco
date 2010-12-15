/**
 * CmisObjectInFolderContainerType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisObjectInFolderContainerType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.alfresco.repo.cmis.ws.CmisObjectInFolderType objectInFolder;

    private org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType[] children;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisObjectInFolderContainerType() {
    }

    public CmisObjectInFolderContainerType(
           org.alfresco.repo.cmis.ws.CmisObjectInFolderType objectInFolder,
           org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType[] children,
           org.apache.axis.message.MessageElement [] _any) {
           this.objectInFolder = objectInFolder;
           this.children = children;
           this._any = _any;
    }


    /**
     * Gets the objectInFolder value for this CmisObjectInFolderContainerType.
     * 
     * @return objectInFolder
     */
    public org.alfresco.repo.cmis.ws.CmisObjectInFolderType getObjectInFolder() {
        return objectInFolder;
    }


    /**
     * Sets the objectInFolder value for this CmisObjectInFolderContainerType.
     * 
     * @param objectInFolder
     */
    public void setObjectInFolder(org.alfresco.repo.cmis.ws.CmisObjectInFolderType objectInFolder) {
        this.objectInFolder = objectInFolder;
    }


    /**
     * Gets the children value for this CmisObjectInFolderContainerType.
     * 
     * @return children
     */
    public org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType[] getChildren() {
        return children;
    }


    /**
     * Sets the children value for this CmisObjectInFolderContainerType.
     * 
     * @param children
     */
    public void setChildren(org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType[] children) {
        this.children = children;
    }

    public org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType getChildren(int i) {
        return this.children[i];
    }

    public void setChildren(int i, org.alfresco.repo.cmis.ws.CmisObjectInFolderContainerType _value) {
        this.children[i] = _value;
    }


    /**
     * Gets the _any value for this CmisObjectInFolderContainerType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisObjectInFolderContainerType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisObjectInFolderContainerType)) return false;
        CmisObjectInFolderContainerType other = (CmisObjectInFolderContainerType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.objectInFolder==null && other.getObjectInFolder()==null) || 
             (this.objectInFolder!=null &&
              this.objectInFolder.equals(other.getObjectInFolder()))) &&
            ((this.children==null && other.getChildren()==null) || 
             (this.children!=null &&
              java.util.Arrays.equals(this.children, other.getChildren()))) &&
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
        if (getObjectInFolder() != null) {
            _hashCode += getObjectInFolder().hashCode();
        }
        if (getChildren() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChildren());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChildren(), i);
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
        new org.apache.axis.description.TypeDesc(CmisObjectInFolderContainerType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectInFolderContainerType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectInFolder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "objectInFolder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectInFolderType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("children");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "children"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectInFolderContainerType"));
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
