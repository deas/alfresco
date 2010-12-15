/**
 * GetTypeDescendants.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetTypeDescendants  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String typeId;

    private java.math.BigInteger depth;

    private java.lang.Boolean includePropertyDefinitions;

    /* This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions */
    private org.alfresco.repo.cmis.ws.CmisExtensionType extension;

    public GetTypeDescendants() {
    }

    public GetTypeDescendants(
           java.lang.String repositoryId,
           java.lang.String typeId,
           java.math.BigInteger depth,
           java.lang.Boolean includePropertyDefinitions,
           org.alfresco.repo.cmis.ws.CmisExtensionType extension) {
           this.repositoryId = repositoryId;
           this.typeId = typeId;
           this.depth = depth;
           this.includePropertyDefinitions = includePropertyDefinitions;
           this.extension = extension;
    }


    /**
     * Gets the repositoryId value for this GetTypeDescendants.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this GetTypeDescendants.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the typeId value for this GetTypeDescendants.
     * 
     * @return typeId
     */
    public java.lang.String getTypeId() {
        return typeId;
    }


    /**
     * Sets the typeId value for this GetTypeDescendants.
     * 
     * @param typeId
     */
    public void setTypeId(java.lang.String typeId) {
        this.typeId = typeId;
    }


    /**
     * Gets the depth value for this GetTypeDescendants.
     * 
     * @return depth
     */
    public java.math.BigInteger getDepth() {
        return depth;
    }


    /**
     * Sets the depth value for this GetTypeDescendants.
     * 
     * @param depth
     */
    public void setDepth(java.math.BigInteger depth) {
        this.depth = depth;
    }


    /**
     * Gets the includePropertyDefinitions value for this GetTypeDescendants.
     * 
     * @return includePropertyDefinitions
     */
    public java.lang.Boolean getIncludePropertyDefinitions() {
        return includePropertyDefinitions;
    }


    /**
     * Sets the includePropertyDefinitions value for this GetTypeDescendants.
     * 
     * @param includePropertyDefinitions
     */
    public void setIncludePropertyDefinitions(java.lang.Boolean includePropertyDefinitions) {
        this.includePropertyDefinitions = includePropertyDefinitions;
    }


    /**
     * Gets the extension value for this GetTypeDescendants.
     * 
     * @return extension   * This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions
     */
    public org.alfresco.repo.cmis.ws.CmisExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this GetTypeDescendants.
     * 
     * @param extension   * This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions
     */
    public void setExtension(org.alfresco.repo.cmis.ws.CmisExtensionType extension) {
        this.extension = extension;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetTypeDescendants)) return false;
        GetTypeDescendants other = (GetTypeDescendants) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.repositoryId==null && other.getRepositoryId()==null) || 
             (this.repositoryId!=null &&
              this.repositoryId.equals(other.getRepositoryId()))) &&
            ((this.typeId==null && other.getTypeId()==null) || 
             (this.typeId!=null &&
              this.typeId.equals(other.getTypeId()))) &&
            ((this.depth==null && other.getDepth()==null) || 
             (this.depth!=null &&
              this.depth.equals(other.getDepth()))) &&
            ((this.includePropertyDefinitions==null && other.getIncludePropertyDefinitions()==null) || 
             (this.includePropertyDefinitions!=null &&
              this.includePropertyDefinitions.equals(other.getIncludePropertyDefinitions()))) &&
            ((this.extension==null && other.getExtension()==null) || 
             (this.extension!=null &&
              this.extension.equals(other.getExtension())));
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
        if (getRepositoryId() != null) {
            _hashCode += getRepositoryId().hashCode();
        }
        if (getTypeId() != null) {
            _hashCode += getTypeId().hashCode();
        }
        if (getDepth() != null) {
            _hashCode += getDepth().hashCode();
        }
        if (getIncludePropertyDefinitions() != null) {
            _hashCode += getIncludePropertyDefinitions().hashCode();
        }
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetTypeDescendants.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getTypeDescendants"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "repositoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("typeId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "typeId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("depth");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "depth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includePropertyDefinitions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "includePropertyDefinitions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "cmisExtensionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
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
