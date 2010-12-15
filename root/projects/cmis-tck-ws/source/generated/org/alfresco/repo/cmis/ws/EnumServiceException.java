/**
 * EnumServiceException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class EnumServiceException implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected EnumServiceException(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _constraint = "constraint";
    public static final java.lang.String _nameConstraintViolation = "nameConstraintViolation";
    public static final java.lang.String _contentAlreadyExists = "contentAlreadyExists";
    public static final java.lang.String _filterNotValid = "filterNotValid";
    public static final java.lang.String _invalidArgument = "invalidArgument";
    public static final java.lang.String _notSupported = "notSupported";
    public static final java.lang.String _objectNotFound = "objectNotFound";
    public static final java.lang.String _permissionDenied = "permissionDenied";
    public static final java.lang.String _runtime = "runtime";
    public static final java.lang.String _storage = "storage";
    public static final java.lang.String _streamNotSupported = "streamNotSupported";
    public static final java.lang.String _updateConflict = "updateConflict";
    public static final java.lang.String _versioning = "versioning";
    public static final EnumServiceException constraint = new EnumServiceException(_constraint);
    public static final EnumServiceException nameConstraintViolation = new EnumServiceException(_nameConstraintViolation);
    public static final EnumServiceException contentAlreadyExists = new EnumServiceException(_contentAlreadyExists);
    public static final EnumServiceException filterNotValid = new EnumServiceException(_filterNotValid);
    public static final EnumServiceException invalidArgument = new EnumServiceException(_invalidArgument);
    public static final EnumServiceException notSupported = new EnumServiceException(_notSupported);
    public static final EnumServiceException objectNotFound = new EnumServiceException(_objectNotFound);
    public static final EnumServiceException permissionDenied = new EnumServiceException(_permissionDenied);
    public static final EnumServiceException runtime = new EnumServiceException(_runtime);
    public static final EnumServiceException storage = new EnumServiceException(_storage);
    public static final EnumServiceException streamNotSupported = new EnumServiceException(_streamNotSupported);
    public static final EnumServiceException updateConflict = new EnumServiceException(_updateConflict);
    public static final EnumServiceException versioning = new EnumServiceException(_versioning);
    public java.lang.String getValue() { return _value_;}
    public static EnumServiceException fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        EnumServiceException enumeration = (EnumServiceException)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static EnumServiceException fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(EnumServiceException.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "enumServiceException"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
