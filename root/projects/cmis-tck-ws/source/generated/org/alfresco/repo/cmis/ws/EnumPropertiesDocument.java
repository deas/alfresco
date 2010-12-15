/**
 * EnumPropertiesDocument.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class EnumPropertiesDocument implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected EnumPropertiesDocument(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _value1 = "cmis:isImmutable";
    public static final java.lang.String _value2 = "cmis:isLatestVersion";
    public static final java.lang.String _value3 = "cmis:isMajorVersion";
    public static final java.lang.String _value4 = "cmis:isLatestMajorVersion";
    public static final java.lang.String _value5 = "cmis:versionLabel";
    public static final java.lang.String _value6 = "cmis:versionSeriesId";
    public static final java.lang.String _value7 = "cmis:isVersionSeriesCheckedOut";
    public static final java.lang.String _value8 = "cmis:versionSeriesCheckedOutBy";
    public static final java.lang.String _value9 = "cmis:versionSeriesCheckedOutId";
    public static final java.lang.String _value10 = "cmis:checkinComment";
    public static final java.lang.String _value11 = "cmis:contentStreamLength";
    public static final java.lang.String _value12 = "cmis:contentStreamMimeType";
    public static final java.lang.String _value13 = "cmis:contentStreamFileName";
    public static final java.lang.String _value14 = "cmis:contentStreamId";
    public static final EnumPropertiesDocument value1 = new EnumPropertiesDocument(_value1);
    public static final EnumPropertiesDocument value2 = new EnumPropertiesDocument(_value2);
    public static final EnumPropertiesDocument value3 = new EnumPropertiesDocument(_value3);
    public static final EnumPropertiesDocument value4 = new EnumPropertiesDocument(_value4);
    public static final EnumPropertiesDocument value5 = new EnumPropertiesDocument(_value5);
    public static final EnumPropertiesDocument value6 = new EnumPropertiesDocument(_value6);
    public static final EnumPropertiesDocument value7 = new EnumPropertiesDocument(_value7);
    public static final EnumPropertiesDocument value8 = new EnumPropertiesDocument(_value8);
    public static final EnumPropertiesDocument value9 = new EnumPropertiesDocument(_value9);
    public static final EnumPropertiesDocument value10 = new EnumPropertiesDocument(_value10);
    public static final EnumPropertiesDocument value11 = new EnumPropertiesDocument(_value11);
    public static final EnumPropertiesDocument value12 = new EnumPropertiesDocument(_value12);
    public static final EnumPropertiesDocument value13 = new EnumPropertiesDocument(_value13);
    public static final EnumPropertiesDocument value14 = new EnumPropertiesDocument(_value14);
    public java.lang.String getValue() { return _value_;}
    public static EnumPropertiesDocument fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        EnumPropertiesDocument enumeration = (EnumPropertiesDocument)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static EnumPropertiesDocument fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(EnumPropertiesDocument.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumPropertiesDocument"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
