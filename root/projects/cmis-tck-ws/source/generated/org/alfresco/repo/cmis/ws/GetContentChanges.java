/**
 * GetContentChanges.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetContentChanges  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String changeLogToken;

    private java.lang.Boolean includeProperties;

    private java.lang.String filter;

    private java.lang.Boolean includePolicyIds;

    private java.lang.Boolean includeACL;

    private java.math.BigInteger maxItems;

    /* This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions */
    private org.alfresco.repo.cmis.ws.CmisExtensionType extension;

    public GetContentChanges() {
    }

    public GetContentChanges(
           java.lang.String repositoryId,
           java.lang.String changeLogToken,
           java.lang.Boolean includeProperties,
           java.lang.String filter,
           java.lang.Boolean includePolicyIds,
           java.lang.Boolean includeACL,
           java.math.BigInteger maxItems,
           org.alfresco.repo.cmis.ws.CmisExtensionType extension) {
           this.repositoryId = repositoryId;
           this.changeLogToken = changeLogToken;
           this.includeProperties = includeProperties;
           this.filter = filter;
           this.includePolicyIds = includePolicyIds;
           this.includeACL = includeACL;
           this.maxItems = maxItems;
           this.extension = extension;
    }


    /**
     * Gets the repositoryId value for this GetContentChanges.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this GetContentChanges.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the changeLogToken value for this GetContentChanges.
     * 
     * @return changeLogToken
     */
    public java.lang.String getChangeLogToken() {
        return changeLogToken;
    }


    /**
     * Sets the changeLogToken value for this GetContentChanges.
     * 
     * @param changeLogToken
     */
    public void setChangeLogToken(java.lang.String changeLogToken) {
        this.changeLogToken = changeLogToken;
    }


    /**
     * Gets the includeProperties value for this GetContentChanges.
     * 
     * @return includeProperties
     */
    public java.lang.Boolean getIncludeProperties() {
        return includeProperties;
    }


    /**
     * Sets the includeProperties value for this GetContentChanges.
     * 
     * @param includeProperties
     */
    public void setIncludeProperties(java.lang.Boolean includeProperties) {
        this.includeProperties = includeProperties;
    }


    /**
     * Gets the filter value for this GetContentChanges.
     * 
     * @return filter
     */
    public java.lang.String getFilter() {
        return filter;
    }


    /**
     * Sets the filter value for this GetContentChanges.
     * 
     * @param filter
     */
    public void setFilter(java.lang.String filter) {
        this.filter = filter;
    }


    /**
     * Gets the includePolicyIds value for this GetContentChanges.
     * 
     * @return includePolicyIds
     */
    public java.lang.Boolean getIncludePolicyIds() {
        return includePolicyIds;
    }


    /**
     * Sets the includePolicyIds value for this GetContentChanges.
     * 
     * @param includePolicyIds
     */
    public void setIncludePolicyIds(java.lang.Boolean includePolicyIds) {
        this.includePolicyIds = includePolicyIds;
    }


    /**
     * Gets the includeACL value for this GetContentChanges.
     * 
     * @return includeACL
     */
    public java.lang.Boolean getIncludeACL() {
        return includeACL;
    }


    /**
     * Sets the includeACL value for this GetContentChanges.
     * 
     * @param includeACL
     */
    public void setIncludeACL(java.lang.Boolean includeACL) {
        this.includeACL = includeACL;
    }


    /**
     * Gets the maxItems value for this GetContentChanges.
     * 
     * @return maxItems
     */
    public java.math.BigInteger getMaxItems() {
        return maxItems;
    }


    /**
     * Sets the maxItems value for this GetContentChanges.
     * 
     * @param maxItems
     */
    public void setMaxItems(java.math.BigInteger maxItems) {
        this.maxItems = maxItems;
    }


    /**
     * Gets the extension value for this GetContentChanges.
     * 
     * @return extension   * This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions
     */
    public org.alfresco.repo.cmis.ws.CmisExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this GetContentChanges.
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
        if (!(obj instanceof GetContentChanges)) return false;
        GetContentChanges other = (GetContentChanges) obj;
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
            ((this.changeLogToken==null && other.getChangeLogToken()==null) || 
             (this.changeLogToken!=null &&
              this.changeLogToken.equals(other.getChangeLogToken()))) &&
            ((this.includeProperties==null && other.getIncludeProperties()==null) || 
             (this.includeProperties!=null &&
              this.includeProperties.equals(other.getIncludeProperties()))) &&
            ((this.filter==null && other.getFilter()==null) || 
             (this.filter!=null &&
              this.filter.equals(other.getFilter()))) &&
            ((this.includePolicyIds==null && other.getIncludePolicyIds()==null) || 
             (this.includePolicyIds!=null &&
              this.includePolicyIds.equals(other.getIncludePolicyIds()))) &&
            ((this.includeACL==null && other.getIncludeACL()==null) || 
             (this.includeACL!=null &&
              this.includeACL.equals(other.getIncludeACL()))) &&
            ((this.maxItems==null && other.getMaxItems()==null) || 
             (this.maxItems!=null &&
              this.maxItems.equals(other.getMaxItems()))) &&
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
        if (getChangeLogToken() != null) {
            _hashCode += getChangeLogToken().hashCode();
        }
        if (getIncludeProperties() != null) {
            _hashCode += getIncludeProperties().hashCode();
        }
        if (getFilter() != null) {
            _hashCode += getFilter().hashCode();
        }
        if (getIncludePolicyIds() != null) {
            _hashCode += getIncludePolicyIds().hashCode();
        }
        if (getIncludeACL() != null) {
            _hashCode += getIncludeACL().hashCode();
        }
        if (getMaxItems() != null) {
            _hashCode += getMaxItems().hashCode();
        }
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetContentChanges.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getContentChanges"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "repositoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changeLogToken");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "changeLogToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeProperties");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "includeProperties"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("filter");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "filter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includePolicyIds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "includePolicyIds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeACL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "includeACL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "maxItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
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
