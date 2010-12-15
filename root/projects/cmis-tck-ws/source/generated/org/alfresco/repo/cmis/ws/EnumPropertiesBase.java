/**
 * EnumPropertiesBase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class EnumPropertiesBase implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected EnumPropertiesBase(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "cmis:name";
    public static final java.lang.String _value2 = "cmis:objectId";
    public static final java.lang.String _value3 = "cmis:objectTypeId";
    public static final java.lang.String _value4 = "cmis:baseTypeId";
    public static final java.lang.String _value5 = "cmis:createdBy";
    public static final java.lang.String _value6 = "cmis:creationDate";
    public static final java.lang.String _value7 = "cmis:lastModifiedBy";
    public static final java.lang.String _value8 = "cmis:lastModificationDate";
    public static final java.lang.String _value9 = "cmis:changeToken";
    public static final EnumPropertiesBase value1 = new EnumPropertiesBase(_value1);
    public static final EnumPropertiesBase value2 = new EnumPropertiesBase(_value2);
    public static final EnumPropertiesBase value3 = new EnumPropertiesBase(_value3);
    public static final EnumPropertiesBase value4 = new EnumPropertiesBase(_value4);
    public static final EnumPropertiesBase value5 = new EnumPropertiesBase(_value5);
    public static final EnumPropertiesBase value6 = new EnumPropertiesBase(_value6);
    public static final EnumPropertiesBase value7 = new EnumPropertiesBase(_value7);
    public static final EnumPropertiesBase value8 = new EnumPropertiesBase(_value8);
    public static final EnumPropertiesBase value9 = new EnumPropertiesBase(_value9);
    public java.lang.String getValue() { return _value_;}
    public static EnumPropertiesBase fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        EnumPropertiesBase enumeration = (EnumPropertiesBase)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static EnumPropertiesBase fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EnumPropertiesBase.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumPropertiesBase"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
