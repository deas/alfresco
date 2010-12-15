/**
 * DeleteTreeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class DeleteTreeResponse  implements java.io.Serializable {
    private org.alfresco.repo.cmis.ws.DeleteTreeResponseFailedToDelete failedToDelete;

    public DeleteTreeResponse() {
    }

    public DeleteTreeResponse(
           org.alfresco.repo.cmis.ws.DeleteTreeResponseFailedToDelete failedToDelete) {
           this.failedToDelete = failedToDelete;
    }


    /**
     * Gets the failedToDelete value for this DeleteTreeResponse.
     * 
     * @return failedToDelete
     */
    public org.alfresco.repo.cmis.ws.DeleteTreeResponseFailedToDelete getFailedToDelete() {
        return failedToDelete;
    }


    /**
     * Sets the failedToDelete value for this DeleteTreeResponse.
     * 
     * @param failedToDelete
     */
    public void setFailedToDelete(org.alfresco.repo.cmis.ws.DeleteTreeResponseFailedToDelete failedToDelete) {
        this.failedToDelete = failedToDelete;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DeleteTreeResponse)) return false;
        DeleteTreeResponse other = (DeleteTreeResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.failedToDelete==null && other.getFailedToDelete()==null) || 
             (this.failedToDelete!=null &&
              this.failedToDelete.equals(other.getFailedToDelete())));
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
        if (getFailedToDelete() != null) {
            _hashCode += getFailedToDelete().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DeleteTreeResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteTreeResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failedToDelete");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "failedToDelete"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">>deleteTreeResponse>failedToDelete"));
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
