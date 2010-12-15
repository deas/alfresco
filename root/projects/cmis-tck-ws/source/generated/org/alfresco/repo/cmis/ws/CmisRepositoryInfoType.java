/**
 * CmisRepositoryInfoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisRepositoryInfoType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private java.lang.String repositoryId;

    private java.lang.String repositoryName;

    private java.lang.String repositoryDescription;

    private java.lang.String vendorName;

    private java.lang.String productName;

    private java.lang.String productVersion;

    private java.lang.String rootFolderId;

    private java.lang.String latestChangeLogToken;

    private org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType capabilities;

    private org.alfresco.repo.cmis.ws.CmisACLCapabilityType aclCapability;

    private java.lang.String cmisVersionSupported;

    private org.apache.axis.types.URI thinClientURI;

    private java.lang.Boolean changesIncomplete;

    private org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds[] changesOnType;

    /* If set, this field holds the principal who is
     * 						used for anonymous
     * 						access. This principal can then be passed to the
     * 						ACL services to
     * 						specify what permissions anonymous users should
     * 						have */
    private java.lang.String principalAnonymous;

    /* If set, this field holds the principal who is
     * 						used for everyone's access. This principal can then be passed
     * to
     * 						the
     * 						ACL services to
     * 						specify what permissions everyone should
     * 						have */
    private java.lang.String principalAnyone;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisRepositoryInfoType() {
    }

    public CmisRepositoryInfoType(
           java.lang.String repositoryId,
           java.lang.String repositoryName,
           java.lang.String repositoryDescription,
           java.lang.String vendorName,
           java.lang.String productName,
           java.lang.String productVersion,
           java.lang.String rootFolderId,
           java.lang.String latestChangeLogToken,
           org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType capabilities,
           org.alfresco.repo.cmis.ws.CmisACLCapabilityType aclCapability,
           java.lang.String cmisVersionSupported,
           org.apache.axis.types.URI thinClientURI,
           java.lang.Boolean changesIncomplete,
           org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds[] changesOnType,
           java.lang.String principalAnonymous,
           java.lang.String principalAnyone,
           org.apache.axis.message.MessageElement [] _any) {
           this.repositoryId = repositoryId;
           this.repositoryName = repositoryName;
           this.repositoryDescription = repositoryDescription;
           this.vendorName = vendorName;
           this.productName = productName;
           this.productVersion = productVersion;
           this.rootFolderId = rootFolderId;
           this.latestChangeLogToken = latestChangeLogToken;
           this.capabilities = capabilities;
           this.aclCapability = aclCapability;
           this.cmisVersionSupported = cmisVersionSupported;
           this.thinClientURI = thinClientURI;
           this.changesIncomplete = changesIncomplete;
           this.changesOnType = changesOnType;
           this.principalAnonymous = principalAnonymous;
           this.principalAnyone = principalAnyone;
           this._any = _any;
    }


    /**
     * Gets the repositoryId value for this CmisRepositoryInfoType.
     * 
     * @return repositoryId
     */
    public java.lang.String getRepositoryId() {
        return repositoryId;
    }


    /**
     * Sets the repositoryId value for this CmisRepositoryInfoType.
     * 
     * @param repositoryId
     */
    public void setRepositoryId(java.lang.String repositoryId) {
        this.repositoryId = repositoryId;
    }


    /**
     * Gets the repositoryName value for this CmisRepositoryInfoType.
     * 
     * @return repositoryName
     */
    public java.lang.String getRepositoryName() {
        return repositoryName;
    }


    /**
     * Sets the repositoryName value for this CmisRepositoryInfoType.
     * 
     * @param repositoryName
     */
    public void setRepositoryName(java.lang.String repositoryName) {
        this.repositoryName = repositoryName;
    }


    /**
     * Gets the repositoryDescription value for this CmisRepositoryInfoType.
     * 
     * @return repositoryDescription
     */
    public java.lang.String getRepositoryDescription() {
        return repositoryDescription;
    }


    /**
     * Sets the repositoryDescription value for this CmisRepositoryInfoType.
     * 
     * @param repositoryDescription
     */
    public void setRepositoryDescription(java.lang.String repositoryDescription) {
        this.repositoryDescription = repositoryDescription;
    }


    /**
     * Gets the vendorName value for this CmisRepositoryInfoType.
     * 
     * @return vendorName
     */
    public java.lang.String getVendorName() {
        return vendorName;
    }


    /**
     * Sets the vendorName value for this CmisRepositoryInfoType.
     * 
     * @param vendorName
     */
    public void setVendorName(java.lang.String vendorName) {
        this.vendorName = vendorName;
    }


    /**
     * Gets the productName value for this CmisRepositoryInfoType.
     * 
     * @return productName
     */
    public java.lang.String getProductName() {
        return productName;
    }


    /**
     * Sets the productName value for this CmisRepositoryInfoType.
     * 
     * @param productName
     */
    public void setProductName(java.lang.String productName) {
        this.productName = productName;
    }


    /**
     * Gets the productVersion value for this CmisRepositoryInfoType.
     * 
     * @return productVersion
     */
    public java.lang.String getProductVersion() {
        return productVersion;
    }


    /**
     * Sets the productVersion value for this CmisRepositoryInfoType.
     * 
     * @param productVersion
     */
    public void setProductVersion(java.lang.String productVersion) {
        this.productVersion = productVersion;
    }


    /**
     * Gets the rootFolderId value for this CmisRepositoryInfoType.
     * 
     * @return rootFolderId
     */
    public java.lang.String getRootFolderId() {
        return rootFolderId;
    }


    /**
     * Sets the rootFolderId value for this CmisRepositoryInfoType.
     * 
     * @param rootFolderId
     */
    public void setRootFolderId(java.lang.String rootFolderId) {
        this.rootFolderId = rootFolderId;
    }


    /**
     * Gets the latestChangeLogToken value for this CmisRepositoryInfoType.
     * 
     * @return latestChangeLogToken
     */
    public java.lang.String getLatestChangeLogToken() {
        return latestChangeLogToken;
    }


    /**
     * Sets the latestChangeLogToken value for this CmisRepositoryInfoType.
     * 
     * @param latestChangeLogToken
     */
    public void setLatestChangeLogToken(java.lang.String latestChangeLogToken) {
        this.latestChangeLogToken = latestChangeLogToken;
    }


    /**
     * Gets the capabilities value for this CmisRepositoryInfoType.
     * 
     * @return capabilities
     */
    public org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType getCapabilities() {
        return capabilities;
    }


    /**
     * Sets the capabilities value for this CmisRepositoryInfoType.
     * 
     * @param capabilities
     */
    public void setCapabilities(org.alfresco.repo.cmis.ws.CmisRepositoryCapabilitiesType capabilities) {
        this.capabilities = capabilities;
    }


    /**
     * Gets the aclCapability value for this CmisRepositoryInfoType.
     * 
     * @return aclCapability
     */
    public org.alfresco.repo.cmis.ws.CmisACLCapabilityType getAclCapability() {
        return aclCapability;
    }


    /**
     * Sets the aclCapability value for this CmisRepositoryInfoType.
     * 
     * @param aclCapability
     */
    public void setAclCapability(org.alfresco.repo.cmis.ws.CmisACLCapabilityType aclCapability) {
        this.aclCapability = aclCapability;
    }


    /**
     * Gets the cmisVersionSupported value for this CmisRepositoryInfoType.
     * 
     * @return cmisVersionSupported
     */
    public java.lang.String getCmisVersionSupported() {
        return cmisVersionSupported;
    }


    /**
     * Sets the cmisVersionSupported value for this CmisRepositoryInfoType.
     * 
     * @param cmisVersionSupported
     */
    public void setCmisVersionSupported(java.lang.String cmisVersionSupported) {
        this.cmisVersionSupported = cmisVersionSupported;
    }


    /**
     * Gets the thinClientURI value for this CmisRepositoryInfoType.
     * 
     * @return thinClientURI
     */
    public org.apache.axis.types.URI getThinClientURI() {
        return thinClientURI;
    }


    /**
     * Sets the thinClientURI value for this CmisRepositoryInfoType.
     * 
     * @param thinClientURI
     */
    public void setThinClientURI(org.apache.axis.types.URI thinClientURI) {
        this.thinClientURI = thinClientURI;
    }


    /**
     * Gets the changesIncomplete value for this CmisRepositoryInfoType.
     * 
     * @return changesIncomplete
     */
    public java.lang.Boolean getChangesIncomplete() {
        return changesIncomplete;
    }


    /**
     * Sets the changesIncomplete value for this CmisRepositoryInfoType.
     * 
     * @param changesIncomplete
     */
    public void setChangesIncomplete(java.lang.Boolean changesIncomplete) {
        this.changesIncomplete = changesIncomplete;
    }


    /**
     * Gets the changesOnType value for this CmisRepositoryInfoType.
     * 
     * @return changesOnType
     */
    public org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds[] getChangesOnType() {
        return changesOnType;
    }


    /**
     * Sets the changesOnType value for this CmisRepositoryInfoType.
     * 
     * @param changesOnType
     */
    public void setChangesOnType(org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds[] changesOnType) {
        this.changesOnType = changesOnType;
    }

    public org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds getChangesOnType(int i) {
        return this.changesOnType[i];
    }

    public void setChangesOnType(int i, org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds _value) {
        this.changesOnType[i] = _value;
    }


    /**
     * Gets the principalAnonymous value for this CmisRepositoryInfoType.
     * 
     * @return principalAnonymous   * If set, this field holds the principal who is
     * 						used for anonymous
     * 						access. This principal can then be passed to the
     * 						ACL services to
     * 						specify what permissions anonymous users should
     * 						have
     */
    public java.lang.String getPrincipalAnonymous() {
        return principalAnonymous;
    }


    /**
     * Sets the principalAnonymous value for this CmisRepositoryInfoType.
     * 
     * @param principalAnonymous   * If set, this field holds the principal who is
     * 						used for anonymous
     * 						access. This principal can then be passed to the
     * 						ACL services to
     * 						specify what permissions anonymous users should
     * 						have
     */
    public void setPrincipalAnonymous(java.lang.String principalAnonymous) {
        this.principalAnonymous = principalAnonymous;
    }


    /**
     * Gets the principalAnyone value for this CmisRepositoryInfoType.
     * 
     * @return principalAnyone   * If set, this field holds the principal who is
     * 						used for everyone's access. This principal can then be passed
     * to
     * 						the
     * 						ACL services to
     * 						specify what permissions everyone should
     * 						have
     */
    public java.lang.String getPrincipalAnyone() {
        return principalAnyone;
    }


    /**
     * Sets the principalAnyone value for this CmisRepositoryInfoType.
     * 
     * @param principalAnyone   * If set, this field holds the principal who is
     * 						used for everyone's access. This principal can then be passed
     * to
     * 						the
     * 						ACL services to
     * 						specify what permissions everyone should
     * 						have
     */
    public void setPrincipalAnyone(java.lang.String principalAnyone) {
        this.principalAnyone = principalAnyone;
    }


    /**
     * Gets the _any value for this CmisRepositoryInfoType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisRepositoryInfoType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisRepositoryInfoType)) return false;
        CmisRepositoryInfoType other = (CmisRepositoryInfoType) obj;
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
            ((this.repositoryName==null && other.getRepositoryName()==null) || 
             (this.repositoryName!=null &&
              this.repositoryName.equals(other.getRepositoryName()))) &&
            ((this.repositoryDescription==null && other.getRepositoryDescription()==null) || 
             (this.repositoryDescription!=null &&
              this.repositoryDescription.equals(other.getRepositoryDescription()))) &&
            ((this.vendorName==null && other.getVendorName()==null) || 
             (this.vendorName!=null &&
              this.vendorName.equals(other.getVendorName()))) &&
            ((this.productName==null && other.getProductName()==null) || 
             (this.productName!=null &&
              this.productName.equals(other.getProductName()))) &&
            ((this.productVersion==null && other.getProductVersion()==null) || 
             (this.productVersion!=null &&
              this.productVersion.equals(other.getProductVersion()))) &&
            ((this.rootFolderId==null && other.getRootFolderId()==null) || 
             (this.rootFolderId!=null &&
              this.rootFolderId.equals(other.getRootFolderId()))) &&
            ((this.latestChangeLogToken==null && other.getLatestChangeLogToken()==null) || 
             (this.latestChangeLogToken!=null &&
              this.latestChangeLogToken.equals(other.getLatestChangeLogToken()))) &&
            ((this.capabilities==null && other.getCapabilities()==null) || 
             (this.capabilities!=null &&
              this.capabilities.equals(other.getCapabilities()))) &&
            ((this.aclCapability==null && other.getAclCapability()==null) || 
             (this.aclCapability!=null &&
              this.aclCapability.equals(other.getAclCapability()))) &&
            ((this.cmisVersionSupported==null && other.getCmisVersionSupported()==null) || 
             (this.cmisVersionSupported!=null &&
              this.cmisVersionSupported.equals(other.getCmisVersionSupported()))) &&
            ((this.thinClientURI==null && other.getThinClientURI()==null) || 
             (this.thinClientURI!=null &&
              this.thinClientURI.equals(other.getThinClientURI()))) &&
            ((this.changesIncomplete==null && other.getChangesIncomplete()==null) || 
             (this.changesIncomplete!=null &&
              this.changesIncomplete.equals(other.getChangesIncomplete()))) &&
            ((this.changesOnType==null && other.getChangesOnType()==null) || 
             (this.changesOnType!=null &&
              java.util.Arrays.equals(this.changesOnType, other.getChangesOnType()))) &&
            ((this.principalAnonymous==null && other.getPrincipalAnonymous()==null) || 
             (this.principalAnonymous!=null &&
              this.principalAnonymous.equals(other.getPrincipalAnonymous()))) &&
            ((this.principalAnyone==null && other.getPrincipalAnyone()==null) || 
             (this.principalAnyone!=null &&
              this.principalAnyone.equals(other.getPrincipalAnyone()))) &&
            ((this._any==null && other.get_any()==null) || 
             (this._any!=null &&
              java.util.Arrays.equals(this._any, other.get_any())));
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
        if (getRepositoryName() != null) {
            _hashCode += getRepositoryName().hashCode();
        }
        if (getRepositoryDescription() != null) {
            _hashCode += getRepositoryDescription().hashCode();
        }
        if (getVendorName() != null) {
            _hashCode += getVendorName().hashCode();
        }
        if (getProductName() != null) {
            _hashCode += getProductName().hashCode();
        }
        if (getProductVersion() != null) {
            _hashCode += getProductVersion().hashCode();
        }
        if (getRootFolderId() != null) {
            _hashCode += getRootFolderId().hashCode();
        }
        if (getLatestChangeLogToken() != null) {
            _hashCode += getLatestChangeLogToken().hashCode();
        }
        if (getCapabilities() != null) {
            _hashCode += getCapabilities().hashCode();
        }
        if (getAclCapability() != null) {
            _hashCode += getAclCapability().hashCode();
        }
        if (getCmisVersionSupported() != null) {
            _hashCode += getCmisVersionSupported().hashCode();
        }
        if (getThinClientURI() != null) {
            _hashCode += getThinClientURI().hashCode();
        }
        if (getChangesIncomplete() != null) {
            _hashCode += getChangesIncomplete().hashCode();
        }
        if (getChangesOnType() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChangesOnType());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChangesOnType(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPrincipalAnonymous() != null) {
            _hashCode += getPrincipalAnonymous().hashCode();
        }
        if (getPrincipalAnyone() != null) {
            _hashCode += getPrincipalAnyone().hashCode();
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
        new org.apache.axis.description.TypeDesc(CmisRepositoryInfoType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisRepositoryInfoType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "repositoryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "repositoryName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repositoryDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "repositoryDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vendorName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "vendorName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "productName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "productVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rootFolderId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "rootFolderId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("latestChangeLogToken");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "latestChangeLogToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilities");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilities"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisRepositoryCapabilitiesType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aclCapability");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "aclCapability"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisACLCapabilityType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cmisVersionSupported");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisVersionSupported"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thinClientURI");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "thinClientURI"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changesIncomplete");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "changesIncomplete"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changesOnType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "changesOnType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumBaseObjectTypeIds"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("principalAnonymous");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "principalAnonymous"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("principalAnyone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "principalAnyone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
