/**
 * GetAllowableActionsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetAllowableActionsResponse  implements java.io.Serializable {
    private org.alfresco.repo.cmis.ws.CmisAllowableActionsType allowableActions;

    public GetAllowableActionsResponse() {
    }

    public GetAllowableActionsResponse(
           org.alfresco.repo.cmis.ws.CmisAllowableActionsType allowableActions) {
           this.allowableActions = allowableActions;
    }


    /**
     * Gets the allowableActions value for this GetAllowableActionsResponse.
     * 
     * @return allowableActions
     */
    public org.alfresco.repo.cmis.ws.CmisAllowableActionsType getAllowableActions() {
        return allowableActions;
    }


    /**
     * Sets the allowableActions value for this GetAllowableActionsResponse.
     * 
     * @param allowableActions
     */
    public void setAllowableActions(org.alfresco.repo.cmis.ws.CmisAllowableActionsType allowableActions) {
        this.allowableActions = allowableActions;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetAllowableActionsResponse)) return false;
        GetAllowableActionsResponse other = (GetAllowableActionsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.allowableActions==null && other.getAllowableActions()==null) || 
             (this.allowableActions!=null &&
              this.allowableActions.equals(other.getAllowableActions())));
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
        if (getAllowableActions() != null) {
            _hashCode += getAllowableActions().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetAllowableActionsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getAllowableActionsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allowableActions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "allowableActions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisAllowableActionsType"));
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
