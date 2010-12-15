/**
 * GetObjectOfLatestVersion.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class GetObjectOfLatestVersion  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String objectId;

    private java.lang.Boolean major;

    private java.lang.String filter;

    private java.lang.Boolean includeAllowableActions;

    private org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships;

    private java.lang.String renditionFilter;

    private java.lang.Boolean includePolicyIds;

    private java.lang.Boolean includeACL;

    /* This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions */
    private org.alfresco.repo.cmis.ws.CmisExtensionType extension;

    public GetObjectOfLatestVersion() {
    }

    public GetObjectOfLatestVersion(
           java.lang.String repositoryId,
           java.lang.String objectId,
           java.lang.Boolean major,
           java.lang.String filter,
           java.lang.Boolean includeAllowableActions,
           org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships,
           java.lang.String renditionFilter,
           java.lang.Boolean includePolicyIds,
           java.lang.Boolean includeACL,
           org.alfresco.repo.cmis.ws.CmisExtensionType extension) {
           this.repositoryId = repositoryId;
           this.objectId = objectId;
           this.major = major;
           this.filter = filter;
           this.includeAllowableActions = includeAllowableActions;
           this.includeRelationships = includeRelationships;
           this.renditionFilter = renditionFilter;
           this.includePolicyIds = includePolicyIds;
           this.includeACL = includeACL;
           this.extension = extension;
    }


    /**
     * Gets the repositoryId value for this GetObjectOfLatestVersion.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this GetObjectOfLatestVersion.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the objectId value for this GetObjectOfLatestVersion.
     * 
     * @return objectId
     */
    public java.lang.String getObjectId() {
        return objectId;
    }


    /**
     * Sets the objectId value for this GetObjectOfLatestVersion.
     * 
     * @param objectId
     */
    public void setObjectId(java.lang.String objectId) {
        this.objectId = objectId;
    }


    /**
     * Gets the major value for this GetObjectOfLatestVersion.
     * 
     * @return major
     */
    public java.lang.Boolean getMajor() {
        return major;
    }


    /**
     * Sets the major value for this GetObjectOfLatestVersion.
     * 
     * @param major
     */
    public void setMajor(java.lang.Boolean major) {
        this.major = major;
    }


    /**
     * Gets the filter value for this GetObjectOfLatestVersion.
     * 
     * @return filter
     */
    public java.lang.String getFilter() {
        return filter;
    }


    /**
     * Sets the filter value for this GetObjectOfLatestVersion.
     * 
     * @param filter
     */
    public void setFilter(java.lang.String filter) {
        this.filter = filter;
    }


    /**
     * Gets the includeAllowableActions value for this GetObjectOfLatestVersion.
     * 
     * @return includeAllowableActions
     */
    public java.lang.Boolean getIncludeAllowableActions() {
        return includeAllowableActions;
    }


    /**
     * Sets the includeAllowableActions value for this GetObjectOfLatestVersion.
     * 
     * @param includeAllowableActions
     */
    public void setIncludeAllowableActions(java.lang.Boolean includeAllowableActions) {
        this.includeAllowableActions = includeAllowableActions;
    }


    /**
     * Gets the includeRelationships value for this GetObjectOfLatestVersion.
     * 
     * @return includeRelationships
     */
    public org.alfresco.repo.cmis.ws.EnumIncludeRelationships getIncludeRelationships() {
        return includeRelationships;
    }


    /**
     * Sets the includeRelationships value for this GetObjectOfLatestVersion.
     * 
     * @param includeRelationships
     */
    public void setIncludeRelationships(org.alfresco.repo.cmis.ws.EnumIncludeRelationships includeRelationships) {
        this.includeRelationships = includeRelationships;
    }


    /**
     * Gets the renditionFilter value for this GetObjectOfLatestVersion.
     * 
     * @return renditionFilter
     */
    public java.lang.String getRenditionFilter() {
        return renditionFilter;
    }


    /**
     * Sets the renditionFilter value for this GetObjectOfLatestVersion.
     * 
     * @param renditionFilter
     */
    public void setRenditionFilter(java.lang.String renditionFilter) {
        this.renditionFilter = renditionFilter;
    }


    /**
     * Gets the includePolicyIds value for this GetObjectOfLatestVersion.
     * 
     * @return includePolicyIds
     */
    public java.lang.Boolean getIncludePolicyIds() {
        return includePolicyIds;
    }


    /**
     * Sets the includePolicyIds value for this GetObjectOfLatestVersion.
     * 
     * @param includePolicyIds
     */
    public void setIncludePolicyIds(java.lang.Boolean includePolicyIds) {
        this.includePolicyIds = includePolicyIds;
    }


    /**
     * Gets the includeACL value for this GetObjectOfLatestVersion.
     * 
     * @return includeACL
     */
    public java.lang.Boolean getIncludeACL() {
        return includeACL;
    }


    /**
     * Sets the includeACL value for this GetObjectOfLatestVersion.
     * 
     * @param includeACL
     */
    public void setIncludeACL(java.lang.Boolean includeACL) {
        this.includeACL = includeACL;
    }


    /**
     * Gets the extension value for this GetObjectOfLatestVersion.
     * 
     * @return extension   * This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions
     */
    public org.alfresco.repo.cmis.ws.CmisExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this GetObjectOfLatestVersion.
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
        if (!(obj instanceof GetObjectOfLatestVersion)) return false;
        GetObjectOfLatestVersion other = (GetObjectOfLatestVersion) obj;
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
            ((this.objectId==null && other.getObjectId()==null) || 
             (this.objectId!=null &&
              this.objectId.equals(other.getObjectId()))) &&
            ((this.major==null && other.getMajor()==null) || 
             (this.major!=null &&
              this.major.equals(other.getMajor()))) &&
            ((this.filter==null && other.getFilter()==null) || 
             (this.filter!=null &&
              this.filter.equals(other.getFilter()))) &&
            ((this.includeAllowableActions==null && other.getIncludeAllowableActions()==null) || 
             (this.includeAllowableActions!=null &&
              this.includeAllowableActions.equals(other.getIncludeAllowableActions()))) &&
            ((this.includeRelationships==null && other.getIncludeRelationships()==null) || 
             (this.includeRelationships!=null &&
              this.includeRelationships.equals(other.getIncludeRelationships()))) &&
            ((this.renditionFilter==null && other.getRenditionFilter()==null) || 
             (this.renditionFilter!=null &&
              this.renditionFilter.equals(other.getRenditionFilter()))) &&
            ((this.includePolicyIds==null && other.getIncludePolicyIds()==null) || 
             (this.includePolicyIds!=null &&
              this.includePolicyIds.equals(other.getIncludePolicyIds()))) &&
            ((this.includeACL==null && other.getIncludeACL()==null) || 
             (this.includeACL!=null &&
              this.includeACL.equals(other.getIncludeACL()))) &&
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
        if (getObjectId() != null) {
            _hashCode += getObjectId().hashCode();
        }
        if (getMajor() != null) {
            _hashCode += getMajor().hashCode();
        }
        if (getFilter() != null) {
            _hashCode += getFilter().hashCode();
        }
        if (getIncludeAllowableActions() != null) {
            _hashCode += getIncludeAllowableActions().hashCode();
        }
        if (getIncludeRelationships() != null) {
            _hashCode += getIncludeRelationships().hashCode();
        }
        if (getRenditionFilter() != null) {
            _hashCode += getRenditionFilter().hashCode();
        }
        if (getIncludePolicyIds() != null) {
            _hashCode += getIncludePolicyIds().hashCode();
        }
        if (getIncludeACL() != null) {
            _hashCode += getIncludeACL().hashCode();
        }
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetObjectOfLatestVersion.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">getObjectOfLatestVersion"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "repositoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "objectId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("major");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "major"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("filter");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "filter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeAllowableActions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "includeAllowableActions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeRelationships");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "includeRelationships"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumIncludeRelationships"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("renditionFilter");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "renditionFilter"));
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
