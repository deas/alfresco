/**
 * EnumPropertyType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class EnumPropertyType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected EnumPropertyType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "boolean";
    public static final java.lang.String _value2 = "id";
    public static final java.lang.String _value3 = "integer";
    public static final java.lang.String _value4 = "datetime";
    public static final java.lang.String _value5 = "decimal";
    public static final java.lang.String _value6 = "html";
    public static final java.lang.String _value7 = "string";
    public static final java.lang.String _value8 = "uri";
    public static final EnumPropertyType value1 = new EnumPropertyType(_value1);
    public static final EnumPropertyType value2 = new EnumPropertyType(_value2);
    public static final EnumPropertyType value3 = new EnumPropertyType(_value3);
    public static final EnumPropertyType value4 = new EnumPropertyType(_value4);
    public static final EnumPropertyType value5 = new EnumPropertyType(_value5);
    public static final EnumPropertyType value6 = new EnumPropertyType(_value6);
    public static final EnumPropertyType value7 = new EnumPropertyType(_value7);
    public static final EnumPropertyType value8 = new EnumPropertyType(_value8);
    public java.lang.String getValue() { return _value_;}
    public static EnumPropertyType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        EnumPropertyType enumeration = (EnumPropertyType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static EnumPropertyType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(EnumPropertyType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumPropertyType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
