/**
 * CmisTypeDefinitionListType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisTypeDefinitionListType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.alfresco.repo.cmis.ws.CmisTypeDefinitionType[] types;

    private boolean hasMoreItems;

    private java.math.BigInteger numItems;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisTypeDefinitionListType() {
    }

    public CmisTypeDefinitionListType(
           org.alfresco.repo.cmis.ws.CmisTypeDefinitionType[] types,
           boolean hasMoreItems,
           java.math.BigInteger numItems,
           org.apache.axis.message.MessageElement [] _any) {
           this.types = types;
           this.hasMoreItems = hasMoreItems;
           this.numItems = numItems;
           this._any = _any;
    }


    /**
     * Gets the types value for this CmisTypeDefinitionListType.
     * 
     * @return types
     */
    public org.alfresco.repo.cmis.ws.CmisTypeDefinitionType[] getTypes() {
        return types;
    }


    /**
     * Sets the types value for this CmisTypeDefinitionListType.
     * 
     * @param types
     */
    public void setTypes(org.alfresco.repo.cmis.ws.CmisTypeDefinitionType[] types) {
        this.types = types;
    }

    public org.alfresco.repo.cmis.ws.CmisTypeDefinitionType getTypes(int i) {
        return this.types[i];
    }

    public void setTypes(int i, org.alfresco.repo.cmis.ws.CmisTypeDefinitionType _value) {
        this.types[i] = _value;
    }


    /**
     * Gets the hasMoreItems value for this CmisTypeDefinitionListType.
     * 
     * @return hasMoreItems
     */
    public boolean isHasMoreItems() {
        return hasMoreItems;
    }


    /**
     * Sets the hasMoreItems value for this CmisTypeDefinitionListType.
     * 
     * @param hasMoreItems
     */
    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }


    /**
     * Gets the numItems value for this CmisTypeDefinitionListType.
     * 
     * @return numItems
     */
    public java.math.BigInteger getNumItems() {
        return numItems;
    }


    /**
     * Sets the numItems value for this CmisTypeDefinitionListType.
     * 
     * @param numItems
     */
    public void setNumItems(java.math.BigInteger numItems) {
        this.numItems = numItems;
    }


    /**
     * Gets the _any value for this CmisTypeDefinitionListType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisTypeDefinitionListType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisTypeDefinitionListType)) return false;
        CmisTypeDefinitionListType other = (CmisTypeDefinitionListType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.types==null && other.getTypes()==null) || 
             (this.types!=null &&
              java.util.Arrays.equals(this.types, other.getTypes()))) &&
            this.hasMoreItems == other.isHasMoreItems() &&
            ((this.numItems==null && other.getNumItems()==null) || 
             (this.numItems!=null &&
              this.numItems.equals(other.getNumItems()))) &&
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
        if (getTypes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTypes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getTypes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += (isHasMoreItems() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getNumItems() != null) {
            _hashCode += getNumItems().hashCode();
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
        new org.apache.axis.description.TypeDesc(CmisTypeDefinitionListType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisTypeDefinitionListType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("types");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "types"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisTypeDefinitionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hasMoreItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "hasMoreItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "numItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
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
