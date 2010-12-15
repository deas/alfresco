/**
 * CmisPropertiesType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

import java.util.Arrays;

public class CmisPropertiesType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    
    private org.alfresco.repo.cmis.ws.CmisPropertyBoolean[] propertyBoolean;
    private org.alfresco.repo.cmis.ws.CmisPropertyId[] propertyId;
    private org.alfresco.repo.cmis.ws.CmisPropertyInteger[] propertyInteger;
    private org.alfresco.repo.cmis.ws.CmisPropertyDateTime[] propertyDateTime;
    private org.alfresco.repo.cmis.ws.CmisPropertyDecimal[] propertyDecimal;
    private org.alfresco.repo.cmis.ws.CmisPropertyHtml[] propertyHtml;
    private org.alfresco.repo.cmis.ws.CmisPropertyString[] propertyString;
    private org.alfresco.repo.cmis.ws.CmisPropertyUri[] propertyUri;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisPropertiesType() {
    }

    public CmisPropertiesType(
           org.alfresco.repo.cmis.ws.CmisPropertyBoolean[] propertyBoolean,
           org.alfresco.repo.cmis.ws.CmisPropertyId[] propertyId,
           org.alfresco.repo.cmis.ws.CmisPropertyInteger[] propertyInteger,
           org.alfresco.repo.cmis.ws.CmisPropertyDateTime[] propertyDateTime,
           org.alfresco.repo.cmis.ws.CmisPropertyDecimal[] propertyDecimal,
           org.alfresco.repo.cmis.ws.CmisPropertyHtml[] propertyHtml,
           org.alfresco.repo.cmis.ws.CmisPropertyString[] propertyString,
           org.alfresco.repo.cmis.ws.CmisPropertyUri[] propertyUri,
           org.apache.axis.message.MessageElement [] _any) {
           this.propertyBoolean = propertyBoolean;
           this.propertyId = propertyId;
           this.propertyInteger = propertyInteger;
           this.propertyDateTime = propertyDateTime;
           this.propertyDecimal = propertyDecimal;
           this.propertyHtml = propertyHtml;
           this.propertyString = propertyString;
           this.propertyUri = propertyUri;
           this._any = _any;
    }

    public void setPropertyBoolean(org.alfresco.repo.cmis.ws.CmisPropertyBoolean[] propertyBoolean)
    {
        this.propertyBoolean = propertyBoolean;
    }

    public void setPropertyId(org.alfresco.repo.cmis.ws.CmisPropertyId[] propertyId)
    {
        this.propertyId = propertyId;
    }

    public void setPropertyInteger(org.alfresco.repo.cmis.ws.CmisPropertyInteger[] propertyInteger)
    {
        this.propertyInteger = propertyInteger;
    }

    public void setPropertyDateTime(org.alfresco.repo.cmis.ws.CmisPropertyDateTime[] propertyDateTime)
    {
        this.propertyDateTime = propertyDateTime;
    }

    public void setPropertyDecimal(org.alfresco.repo.cmis.ws.CmisPropertyDecimal[] propertyDecimal)
    {
        this.propertyDecimal = propertyDecimal;
    }

    public void setPropertyHtml(org.alfresco.repo.cmis.ws.CmisPropertyHtml[] propertyHtml)
    {
        this.propertyHtml = propertyHtml;
    }
    
    public void setPropertyString(org.alfresco.repo.cmis.ws.CmisPropertyString[] propertyString)
    {
        this.propertyString = propertyString;
    }

    public void setPropertyUri(org.alfresco.repo.cmis.ws.CmisPropertyUri[] propertyUri)
    {
        this.propertyUri = propertyUri;
    }

    public org.alfresco.repo.cmis.ws.CmisPropertyBoolean[] getPropertyBoolean()
    {
        return propertyBoolean;
    }

    public org.alfresco.repo.cmis.ws.CmisPropertyId[] getPropertyId()
    {
        return propertyId;
    }

    public org.alfresco.repo.cmis.ws.CmisPropertyInteger[] getPropertyInteger()
    {
        return propertyInteger;
    }

    public org.alfresco.repo.cmis.ws.CmisPropertyDateTime[] getPropertyDateTime()
    {
        return propertyDateTime;
    }

    public org.alfresco.repo.cmis.ws.CmisPropertyDecimal[] getPropertyDecimal()
    {
        return propertyDecimal;
    }

    public org.alfresco.repo.cmis.ws.CmisPropertyString[] getPropertyString()
    {
        return propertyString;
    }

    public org.alfresco.repo.cmis.ws.CmisPropertyUri[] getPropertyUri()
    {
        return propertyUri;
    }

    public org.alfresco.repo.cmis.ws.CmisPropertyHtml[] getPropertyHtml()
    {
        return propertyHtml;
    }
    
    /**
     * Gets the propertyBoolean value for this CmisPropertiesType.
     * 
     * @return propertyBoolean
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyBoolean getPropertyBoolean(int index)
    {
        return propertyBoolean[index];
    }


    /**
     * Sets the propertyBoolean value for this CmisPropertiesType.
     * 
     * @param propertyBoolean
     */
    public void setPropertyBoolean(int index, org.alfresco.repo.cmis.ws.CmisPropertyBoolean propertyBoolean)
    {
        if (this.propertyBoolean == null || this.propertyBoolean.length == 0)
        {
            this.propertyBoolean = new CmisPropertyBoolean[] { propertyBoolean };
        }
        else if (this.propertyBoolean[this.propertyBoolean.length - 1] != null)
        {
            CmisPropertyBoolean[] tmp = new CmisPropertyBoolean[this.propertyBoolean.length + 1];
            System.arraycopy(this.propertyBoolean, 0, tmp, 0, this.propertyBoolean.length);
            tmp[this.propertyBoolean.length] = propertyBoolean;
            this.propertyBoolean = tmp;
        }
        else
        {
            this.propertyBoolean[this.propertyBoolean.length - 1] = propertyBoolean;
        }
    }


    /**
     * Gets the propertyId value for this CmisPropertiesType.
     * 
     * @return propertyId
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyId getPropertyId(int index)
    {
        return propertyId[index];
    }


    /**
     * Sets the propertyId value for this CmisPropertiesType.
     * 
     * @param propertyId
     */
    public void setPropertyId(int index, org.alfresco.repo.cmis.ws.CmisPropertyId propertyId)
    {
        if (this.propertyId == null || this.propertyId.length == 0)
        {
            this.propertyId = new CmisPropertyId[] { propertyId };
        }
        else if (this.propertyId[this.propertyId.length - 1] != null)
        {
            CmisPropertyId[] tmp = new CmisPropertyId[this.propertyId.length + 1];
            System.arraycopy(this.propertyId, 0, tmp, 0, this.propertyId.length);
            tmp[this.propertyId.length] = propertyId;
            this.propertyId = tmp;
        }
        else
        {
            this.propertyId[this.propertyId.length - 1] = propertyId;
        }
    }


    /**
     * Gets the propertyInteger value for this CmisPropertiesType.
     * 
     * @return propertyInteger
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyInteger getPropertyInteger(int index)
    {
        return propertyInteger[index];
    }


    /**
     * Sets the propertyInteger value for this CmisPropertiesType.
     * 
     * @param propertyInteger
     */
    public void setPropertyInteger(int index, org.alfresco.repo.cmis.ws.CmisPropertyInteger propertyInteger)
    {
        if (this.propertyInteger == null || this.propertyInteger.length == 0)
        {
            this.propertyInteger = new CmisPropertyInteger[] { propertyInteger };
        }
        else if (this.propertyInteger[this.propertyInteger.length - 1] != null)
        {
            CmisPropertyInteger[] tmp = new CmisPropertyInteger[this.propertyInteger.length + 1];
            System.arraycopy(this.propertyInteger, 0, tmp, 0, this.propertyInteger.length);
            tmp[this.propertyInteger.length] = propertyInteger;
            this.propertyInteger = tmp;
        }
        else
        {
            this.propertyInteger[this.propertyInteger.length - 1] = propertyInteger;
        }
    }


    /**
     * Gets the propertyDateTime value for this CmisPropertiesType.
     * 
     * @return propertyDateTime
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyDateTime getPropertyDateTime(int index)
    {
        return propertyDateTime[index];
    }


    /**
     * Sets the propertyDateTime value for this CmisPropertiesType.
     * 
     * @param propertyDateTime
     */
    public void setPropertyDateTime(int index, org.alfresco.repo.cmis.ws.CmisPropertyDateTime propertyDateTime)
    {
        if (this.propertyDateTime == null || this.propertyDateTime.length == 0)
        {
            this.propertyDateTime = new CmisPropertyDateTime[] { propertyDateTime };
        }
        else if (this.propertyDateTime[this.propertyDateTime.length - 1] != null)
        {
            CmisPropertyDateTime[] tmp = new CmisPropertyDateTime[this.propertyDateTime.length + 1];
            System.arraycopy(this.propertyDateTime, 0, tmp, 0, this.propertyDateTime.length);
            tmp[this.propertyDateTime.length] = propertyDateTime;
            this.propertyDateTime = tmp;
        }
        else
        {
            this.propertyDateTime[this.propertyDateTime.length - 1] = propertyDateTime;
        }
    }


    /**
     * Gets the propertyDecimal value for this CmisPropertiesType.
     * 
     * @return propertyDecimal
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyDecimal getPropertyDecimal(int index)
    {
        return propertyDecimal[index];
    }


    /**
     * Sets the propertyDecimal value for this CmisPropertiesType.
     * 
     * @param propertyDecimal
     */
    public void setPropertyDecimal(int index, org.alfresco.repo.cmis.ws.CmisPropertyDecimal propertyDecimal)
    {
        if (this.propertyDecimal == null || this.propertyDecimal.length == 0)
        {
            this.propertyDecimal = new CmisPropertyDecimal[] { propertyDecimal };
        }
        else if (this.propertyDecimal[this.propertyDecimal.length - 1] != null)
        {
            CmisPropertyDecimal[] tmp = new CmisPropertyDecimal[this.propertyDecimal.length + 1];
            System.arraycopy(this.propertyDecimal, 0, tmp, 0, this.propertyDecimal.length);
            tmp[this.propertyDecimal.length] = propertyDecimal;
            this.propertyDecimal = tmp;
        }
        else
        {
            this.propertyDecimal[this.propertyDecimal.length - 1] = propertyDecimal;
        }
    }


    /**
     * Gets the propertyHtml value for this CmisPropertiesType.
     * 
     * @return propertyHtml
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyHtml getPropertyHtml(int index)
    {
        return propertyHtml[index];
    }


    /**
     * Sets the propertyHtml value for this CmisPropertiesType.
     * 
     * @param propertyHtml
     */
    public void setPropertyHtml(int index, org.alfresco.repo.cmis.ws.CmisPropertyHtml propertyHtml)
    {
        if (this.propertyHtml == null || this.propertyHtml.length == 0)
        {
            this.propertyHtml = new CmisPropertyHtml[] { propertyHtml };
        }
        else if (this.propertyHtml[this.propertyHtml.length - 1] != null)
        {
            CmisPropertyHtml[] tmp = new CmisPropertyHtml[this.propertyHtml.length + 1];
            System.arraycopy(this.propertyHtml, 0, tmp, 0, this.propertyHtml.length);
            tmp[this.propertyHtml.length] = propertyHtml;
            this.propertyHtml = tmp;
        }
        else
        {
            this.propertyHtml[this.propertyHtml.length - 1] = propertyHtml;
        }
    }

    /**
     * Gets the propertyString value for this CmisPropertiesType.
     * 
     * @return propertyString
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyString getPropertyString(int index)
    {
        return propertyString[index];
    }


    /**
     * Sets the propertyString value for this CmisPropertiesType.
     * 
     * @param propertyString
     */
    public void setPropertyString(int index, org.alfresco.repo.cmis.ws.CmisPropertyString propertyString)
    {
        if (this.propertyString == null || this.propertyString.length == 0)
        {
            this.propertyString = new CmisPropertyString[] { propertyString };
        }
        else if (this.propertyString[this.propertyString.length - 1] != null)
        {
            CmisPropertyString[] tmp = new CmisPropertyString[this.propertyString.length + 1];
            System.arraycopy(this.propertyString, 0, tmp, 0, this.propertyString.length);
            tmp[this.propertyString.length] = propertyString;
            this.propertyString = tmp;
        }
        else
        {
            this.propertyString[this.propertyString.length - 1] = propertyString;
        }
    }


    /**
     * Gets the propertyUri value for this CmisPropertiesType.
     * 
     * @return propertyUri
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyUri getPropertyUri(int index)
    {
        return propertyUri[index];
    }


    /**
     * Sets the propertyUri value for this CmisPropertiesType.
     * 
     * @param propertyUri
     */
    public void setPropertyUri(int index, org.alfresco.repo.cmis.ws.CmisPropertyUri propertyUri)
    {
        if (this.propertyUri == null || this.propertyUri.length == 0)
        {
            this.propertyUri = new CmisPropertyUri[] { propertyUri };
        }
        else if (this.propertyUri[this.propertyUri.length - 1] != null)
        {
            CmisPropertyUri[] tmp = new CmisPropertyUri[this.propertyUri.length + 1];
            System.arraycopy(this.propertyUri, 0, tmp, 0, this.propertyUri.length);
            tmp[this.propertyUri.length] = propertyUri;
            this.propertyUri = tmp;
        }
        else
        {
            this.propertyUri[this.propertyUri.length - 1] = propertyUri;
        }
    }

    /**
     * Gets the _any value for this CmisPropertiesType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement[] get_any()
    {
        return _any;
    }


    /**
     * Sets the _any value for this CmisPropertiesType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement[] _any)
    {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof CmisPropertiesType))
            return false;
        CmisPropertiesType other = (CmisPropertiesType) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null)
        {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true
                && ((this.propertyBoolean == null && other.getPropertyBoolean() == null) || (this.propertyBoolean != null && Arrays.equals(this.propertyBoolean, other
                        .getPropertyBoolean())))
                && ((this.propertyId == null && other.getPropertyId() == null) || (this.propertyId != null && Arrays.equals(this.propertyId, other.getPropertyId())))
                && ((this.propertyInteger == null && other.getPropertyInteger() == null) || (this.propertyInteger != null && Arrays.equals(this.propertyInteger, other
                        .getPropertyInteger())))
                && ((this.propertyDateTime == null && other.getPropertyDateTime() == null) || (this.propertyDateTime != null && Arrays.equals(this.propertyDateTime, other
                        .getPropertyDateTime())))
                && ((this.propertyDecimal == null && other.getPropertyDecimal() == null) || (this.propertyDecimal != null && Arrays.equals(this.propertyDecimal, other
                        .getPropertyDecimal())))
                && ((this.propertyHtml == null && other.getPropertyHtml() == null) || (this.propertyHtml != null && Arrays.equals(this.propertyHtml, other.getPropertyHtml())))
                && ((this.propertyString == null && other.getPropertyString() == null) || (this.propertyString != null && Arrays.equals(this.propertyString, other
                        .getPropertyString())))
                && ((this.propertyUri == null && other.getPropertyUri() == null) || (this.propertyUri != null && Arrays.equals(this.propertyUri, other.getPropertyUri())))
                && ((this._any == null && other.get_any() == null) || (this._any != null && java.util.Arrays.equals(this._any, other.get_any())));
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
        if (getPropertyBoolean() != null) {
            _hashCode += getPropertyBoolean().hashCode();
        }
        if (getPropertyId() != null) {
            _hashCode += getPropertyId().hashCode();
        }
        if (getPropertyInteger() != null) {
            _hashCode += getPropertyInteger().hashCode();
        }
        if (getPropertyDateTime() != null) {
            _hashCode += getPropertyDateTime().hashCode();
        }
        if (getPropertyDecimal() != null) {
            _hashCode += getPropertyDecimal().hashCode();
        }
        if (getPropertyHtml() != null) {
            _hashCode += getPropertyHtml().hashCode();
        }
        if (getPropertyString() != null) {
            _hashCode += getPropertyString().hashCode();
        }
        if (getPropertyUri() != null) {
            _hashCode += getPropertyUri().hashCode();
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
        new org.apache.axis.description.TypeDesc(CmisPropertiesType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertiesType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyBoolean");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyBoolean"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyBoolean"));
        elemField.setMinOccurs(0);
        elemField.setMaxOccursUnbounded(true);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyId"));
        elemField.setMinOccurs(0);
        elemField.setMaxOccursUnbounded(true);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyInteger");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyInteger"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyInteger"));
        elemField.setMinOccurs(0);
        elemField.setMaxOccursUnbounded(true);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDateTime"));
        elemField.setMinOccurs(0);
        elemField.setMaxOccursUnbounded(true);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyDecimal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyDecimal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDecimal"));
        elemField.setMinOccurs(0);
        elemField.setMaxOccursUnbounded(true);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyHtml");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyHtml"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyHtml"));
        elemField.setMinOccurs(0);
        elemField.setMaxOccursUnbounded(true);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyString");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyString"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyString"));
        elemField.setMinOccurs(0);
        elemField.setMaxOccursUnbounded(true);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyUri");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyUri"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyUri"));
        elemField.setMinOccurs(0);
        elemField.setMaxOccursUnbounded(true);
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