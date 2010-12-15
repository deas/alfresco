/**
 * EnumCapabilityQuery.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class EnumCapabilityQuery implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected EnumCapabilityQuery(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _none = "none";
    public static final java.lang.String _metadataonly = "metadataonly";
    public static final java.lang.String _fulltextonly = "fulltextonly";
    public static final java.lang.String _bothseparate = "bothseparate";
    public static final java.lang.String _bothcombined = "bothcombined";
    public static final EnumCapabilityQuery none = new EnumCapabilityQuery(_none);
    public static final EnumCapabilityQuery metadataonly = new EnumCapabilityQuery(_metadataonly);
    public static final EnumCapabilityQuery fulltextonly = new EnumCapabilityQuery(_fulltextonly);
    public static final EnumCapabilityQuery bothseparate = new EnumCapabilityQuery(_bothseparate);
    public static final EnumCapabilityQuery bothcombined = new EnumCapabilityQuery(_bothcombined);
    public java.lang.String getValue() { return _value_;}
    public static EnumCapabilityQuery fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        EnumCapabilityQuery enumeration = (EnumCapabilityQuery)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static EnumCapabilityQuery fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(EnumCapabilityQuery.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityQuery"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
