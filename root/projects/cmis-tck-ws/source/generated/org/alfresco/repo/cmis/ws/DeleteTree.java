/**
 * DeleteTree.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class DeleteTree  implements java.io.Serializable {
    private java.lang.String repositoryId;

    private java.lang.String folderId;

    private java.lang.Boolean allVersions;

    private org.alfresco.repo.cmis.ws.EnumUnfileObject unfileObjects;

    private java.lang.Boolean continueOnFailure;

    /* This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions */
    private org.alfresco.repo.cmis.ws.CmisExtensionType extension;

    public DeleteTree() {
    }

    public DeleteTree(
           java.lang.String repositoryId,
           java.lang.String folderId,
           java.lang.Boolean allVersions,
           org.alfresco.repo.cmis.ws.EnumUnfileObject unfileObjects,
           java.lang.Boolean continueOnFailure,
           org.alfresco.repo.cmis.ws.CmisExtensionType extension) {
           this.repositoryId = repositoryId;
           this.folderId = folderId;
           this.allVersions = allVersions;
           this.unfileObjects = unfileObjects;
           this.continueOnFailure = continueOnFailure;
           this.extension = extension;
    }


    /**
     * Gets the repositoryId value for this DeleteTree.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this DeleteTree.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the folderId value for this DeleteTree.
     * 
     * @return folderId
     */
    public java.lang.String getFolderId() {
        return folderId;
    }


    /**
     * Sets the folderId value for this DeleteTree.
     * 
     * @param folderId
     */
    public void setFolderId(java.lang.String folderId) {
        this.folderId = folderId;
    }


    /**
     * Gets the allVersions value for this DeleteTree.
     * 
     * @return allVersions
     */
    public java.lang.Boolean getAllVersions() {
        return allVersions;
    }


    /**
     * Sets the allVersions value for this DeleteTree.
     * 
     * @param allVersions
     */
    public void setAllVersions(java.lang.Boolean allVersions) {
        this.allVersions = allVersions;
    }


    /**
     * Gets the unfileObjects value for this DeleteTree.
     * 
     * @return unfileObjects
     */
    public org.alfresco.repo.cmis.ws.EnumUnfileObject getUnfileObjects() {
        return unfileObjects;
    }


    /**
     * Sets the unfileObjects value for this DeleteTree.
     * 
     * @param unfileObjects
     */
    public void setUnfileObjects(org.alfresco.repo.cmis.ws.EnumUnfileObject unfileObjects) {
        this.unfileObjects = unfileObjects;
    }


    /**
     * Gets the continueOnFailure value for this DeleteTree.
     * 
     * @return continueOnFailure
     */
    public java.lang.Boolean getContinueOnFailure() {
        return continueOnFailure;
    }


    /**
     * Sets the continueOnFailure value for this DeleteTree.
     * 
     * @param continueOnFailure
     */
    public void setContinueOnFailure(java.lang.Boolean continueOnFailure) {
        this.continueOnFailure = continueOnFailure;
    }


    /**
     * Gets the extension value for this DeleteTree.
     * 
     * @return extension   * This is an extension element to hold any
     * 							repository or
     * 							vendor-specific extensions
     */
    public org.alfresco.repo.cmis.ws.CmisExtensionType getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this DeleteTree.
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
        if (!(obj instanceof DeleteTree)) return false;
        DeleteTree other = (DeleteTree) obj;
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
            ((this.folderId==null && other.getFolderId()==null) || 
             (this.folderId!=null &&
              this.folderId.equals(other.getFolderId()))) &&
            ((this.allVersions==null && other.getAllVersions()==null) || 
             (this.allVersions!=null &&
              this.allVersions.equals(other.getAllVersions()))) &&
            ((this.unfileObjects==null && other.getUnfileObjects()==null) || 
             (this.unfileObjects!=null &&
              this.unfileObjects.equals(other.getUnfileObjects()))) &&
            ((this.continueOnFailure==null && other.getContinueOnFailure()==null) || 
             (this.continueOnFailure!=null &&
              this.continueOnFailure.equals(other.getContinueOnFailure()))) &&
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
        if (getFolderId() != null) {
            _hashCode += getFolderId().hashCode();
        }
        if (getAllVersions() != null) {
            _hashCode += getAllVersions().hashCode();
        }
        if (getUnfileObjects() != null) {
            _hashCode += getUnfileObjects().hashCode();
        }
        if (getContinueOnFailure() != null) {
            _hashCode += getContinueOnFailure().hashCode();
        }
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DeleteTree.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", ">deleteTree"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "repositoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("folderId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "folderId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allVersions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "allVersions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unfileObjects");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "unfileObjects"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumUnfileObject"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("continueOnFailure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/messaging/200908/", "continueOnFailure"));
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
