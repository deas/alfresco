/**
 * GetContentChangesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetContentChangesResponse  implements java.io.Serializable {
    private org.alfresco.repo.cmis.ws.CmisObjectListType objects;

    private java.lang.String changeLogToken;

    public GetContentChangesResponse() {
    }

    public GetContentChangesResponse(
           org.alfresco.repo.cmis.ws.CmisObjectListType objects,
           java.lang.String changeLogToken) {
           this.objects = objects;
           this.changeLogToken = changeLogToken;
    }


    /**
     * Gets the objects value for this GetContentChangesResponse.
     * 
     * @return objects
     */
    public org.alfresco.repo.cmis.ws.CmisObjectListType getObjects() {
        return objects;
    }


    /**
     * Sets the objects value for this GetContentChangesResponse.
     * 
     * @param objects
     */
    public void setObjects(org.alfresco.repo.cmis.ws.CmisObjectListType objects) {
        this.objects = objects;
    }


    /**
     * Gets the changeLogToken value for this GetContentChangesResponse.
     * 
     * @return changeLogToken
     */
    public java.lang.String getChangeLogToken() {
        return changeLogToken;
    }


    /**
     * Sets the changeLogToken value for this GetContentChangesResponse.
     * 
     * @param changeLogToken
     */
    public void setChangeLogToken(java.lang.String changeLogToken) {
        this.changeLogToken = changeLogToken;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetContentChangesResponse)) return false;
        GetContentChangesResponse other = (GetContentChangesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.objects==null && other.getObjects()==null) || 
             (this.objects!=null &&
              this.objects.equals(other.getObjects()))) &&
            ((this.changeLogToken==null && other.getChangeLogToken()==null) || 
             (this.changeLogToken!=null &&
              this.changeLogToken.equals(other.getChangeLogToken())));
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
        if (getObjects() != null) {
            _hashCode += getObjects().hashCode();
        }
        if (getChangeLogToken() != null) {
            _hashCode += getChangeLogToken().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetContentChangesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getContentChangesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objects");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "objects"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisObjectListType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changeLogToken");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "changeLogToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
