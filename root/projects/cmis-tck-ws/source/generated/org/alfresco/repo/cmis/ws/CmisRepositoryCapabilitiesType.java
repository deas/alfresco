/**
 * CmisRepositoryCapabilitiesType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisRepositoryCapabilitiesType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.alfresco.repo.cmis.ws.EnumCapabilityACL capabilityACL;

    private boolean capabilityAllVersionsSearchable;

    private org.alfresco.repo.cmis.ws.EnumCapabilityChanges capabilityChanges;

    private org.alfresco.repo.cmis.ws.EnumCapabilityContentStreamUpdates capabilityContentStreamUpdatability;

    private boolean capabilityGetDescendants;

    private boolean capabilityGetFolderTree;

    private boolean capabilityMultifiling;

    private boolean capabilityPWCSearchable;

    private boolean capabilityPWCUpdatable;

    private org.alfresco.repo.cmis.ws.EnumCapabilityQuery capabilityQuery;

    private org.alfresco.repo.cmis.ws.EnumCapabilityRendition capabilityRenditions;

    private boolean capabilityUnfiling;

    private boolean capabilityVersionSpecificFiling;

    private org.alfresco.repo.cmis.ws.EnumCapabilityJoin capabilityJoin;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisRepositoryCapabilitiesType() {
    }

    public CmisRepositoryCapabilitiesType(
           org.alfresco.repo.cmis.ws.EnumCapabilityACL capabilityACL,
           boolean capabilityAllVersionsSearchable,
           org.alfresco.repo.cmis.ws.EnumCapabilityChanges capabilityChanges,
           org.alfresco.repo.cmis.ws.EnumCapabilityContentStreamUpdates capabilityContentStreamUpdatability,
           boolean capabilityGetDescendants,
           boolean capabilityGetFolderTree,
           boolean capabilityMultifiling,
           boolean capabilityPWCSearchable,
           boolean capabilityPWCUpdatable,
           org.alfresco.repo.cmis.ws.EnumCapabilityQuery capabilityQuery,
           org.alfresco.repo.cmis.ws.EnumCapabilityRendition capabilityRenditions,
           boolean capabilityUnfiling,
           boolean capabilityVersionSpecificFiling,
           org.alfresco.repo.cmis.ws.EnumCapabilityJoin capabilityJoin,
           org.apache.axis.message.MessageElement [] _any) {
           this.capabilityACL = capabilityACL;
           this.capabilityAllVersionsSearchable = capabilityAllVersionsSearchable;
           this.capabilityChanges = capabilityChanges;
           this.capabilityContentStreamUpdatability = capabilityContentStreamUpdatability;
           this.capabilityGetDescendants = capabilityGetDescendants;
           this.capabilityGetFolderTree = capabilityGetFolderTree;
           this.capabilityMultifiling = capabilityMultifiling;
           this.capabilityPWCSearchable = capabilityPWCSearchable;
           this.capabilityPWCUpdatable = capabilityPWCUpdatable;
           this.capabilityQuery = capabilityQuery;
           this.capabilityRenditions = capabilityRenditions;
           this.capabilityUnfiling = capabilityUnfiling;
           this.capabilityVersionSpecificFiling = capabilityVersionSpecificFiling;
           this.capabilityJoin = capabilityJoin;
           this._any = _any;
    }


    /**
     * Gets the capabilityACL value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityACL
     */
    public org.alfresco.repo.cmis.ws.EnumCapabilityACL getCapabilityACL() {
        return capabilityACL;
    }


    /**
     * Sets the capabilityACL value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityACL
     */
    public void setCapabilityACL(org.alfresco.repo.cmis.ws.EnumCapabilityACL capabilityACL) {
        this.capabilityACL = capabilityACL;
    }


    /**
     * Gets the capabilityAllVersionsSearchable value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityAllVersionsSearchable
     */
    public boolean isCapabilityAllVersionsSearchable() {
        return capabilityAllVersionsSearchable;
    }


    /**
     * Sets the capabilityAllVersionsSearchable value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityAllVersionsSearchable
     */
    public void setCapabilityAllVersionsSearchable(boolean capabilityAllVersionsSearchable) {
        this.capabilityAllVersionsSearchable = capabilityAllVersionsSearchable;
    }


    /**
     * Gets the capabilityChanges value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityChanges
     */
    public org.alfresco.repo.cmis.ws.EnumCapabilityChanges getCapabilityChanges() {
        return capabilityChanges;
    }


    /**
     * Sets the capabilityChanges value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityChanges
     */
    public void setCapabilityChanges(org.alfresco.repo.cmis.ws.EnumCapabilityChanges capabilityChanges) {
        this.capabilityChanges = capabilityChanges;
    }


    /**
     * Gets the capabilityContentStreamUpdatability value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityContentStreamUpdatability
     */
    public org.alfresco.repo.cmis.ws.EnumCapabilityContentStreamUpdates getCapabilityContentStreamUpdatability() {
        return capabilityContentStreamUpdatability;
    }


    /**
     * Sets the capabilityContentStreamUpdatability value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityContentStreamUpdatability
     */
    public void setCapabilityContentStreamUpdatability(org.alfresco.repo.cmis.ws.EnumCapabilityContentStreamUpdates capabilityContentStreamUpdatability) {
        this.capabilityContentStreamUpdatability = capabilityContentStreamUpdatability;
    }


    /**
     * Gets the capabilityGetDescendants value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityGetDescendants
     */
    public boolean isCapabilityGetDescendants() {
        return capabilityGetDescendants;
    }


    /**
     * Sets the capabilityGetDescendants value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityGetDescendants
     */
    public void setCapabilityGetDescendants(boolean capabilityGetDescendants) {
        this.capabilityGetDescendants = capabilityGetDescendants;
    }


    /**
     * Gets the capabilityGetFolderTree value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityGetFolderTree
     */
    public boolean isCapabilityGetFolderTree() {
        return capabilityGetFolderTree;
    }


    /**
     * Sets the capabilityGetFolderTree value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityGetFolderTree
     */
    public void setCapabilityGetFolderTree(boolean capabilityGetFolderTree) {
        this.capabilityGetFolderTree = capabilityGetFolderTree;
    }


    /**
     * Gets the capabilityMultifiling value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityMultifiling
     */
    public boolean isCapabilityMultifiling() {
        return capabilityMultifiling;
    }


    /**
     * Sets the capabilityMultifiling value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityMultifiling
     */
    public void setCapabilityMultifiling(boolean capabilityMultifiling) {
        this.capabilityMultifiling = capabilityMultifiling;
    }


    /**
     * Gets the capabilityPWCSearchable value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityPWCSearchable
     */
    public boolean isCapabilityPWCSearchable() {
        return capabilityPWCSearchable;
    }


    /**
     * Sets the capabilityPWCSearchable value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityPWCSearchable
     */
    public void setCapabilityPWCSearchable(boolean capabilityPWCSearchable) {
        this.capabilityPWCSearchable = capabilityPWCSearchable;
    }


    /**
     * Gets the capabilityPWCUpdatable value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityPWCUpdatable
     */
    public boolean isCapabilityPWCUpdatable() {
        return capabilityPWCUpdatable;
    }


    /**
     * Sets the capabilityPWCUpdatable value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityPWCUpdatable
     */
    public void setCapabilityPWCUpdatable(boolean capabilityPWCUpdatable) {
        this.capabilityPWCUpdatable = capabilityPWCUpdatable;
    }


    /**
     * Gets the capabilityQuery value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityQuery
     */
    public org.alfresco.repo.cmis.ws.EnumCapabilityQuery getCapabilityQuery() {
        return capabilityQuery;
    }


    /**
     * Sets the capabilityQuery value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityQuery
     */
    public void setCapabilityQuery(org.alfresco.repo.cmis.ws.EnumCapabilityQuery capabilityQuery) {
        this.capabilityQuery = capabilityQuery;
    }


    /**
     * Gets the capabilityRenditions value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityRenditions
     */
    public org.alfresco.repo.cmis.ws.EnumCapabilityRendition getCapabilityRenditions() {
        return capabilityRenditions;
    }


    /**
     * Sets the capabilityRenditions value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityRenditions
     */
    public void setCapabilityRenditions(org.alfresco.repo.cmis.ws.EnumCapabilityRendition capabilityRenditions) {
        this.capabilityRenditions = capabilityRenditions;
    }


    /**
     * Gets the capabilityUnfiling value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityUnfiling
     */
    public boolean isCapabilityUnfiling() {
        return capabilityUnfiling;
    }


    /**
     * Sets the capabilityUnfiling value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityUnfiling
     */
    public void setCapabilityUnfiling(boolean capabilityUnfiling) {
        this.capabilityUnfiling = capabilityUnfiling;
    }


    /**
     * Gets the capabilityVersionSpecificFiling value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityVersionSpecificFiling
     */
    public boolean isCapabilityVersionSpecificFiling() {
        return capabilityVersionSpecificFiling;
    }


    /**
     * Sets the capabilityVersionSpecificFiling value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityVersionSpecificFiling
     */
    public void setCapabilityVersionSpecificFiling(boolean capabilityVersionSpecificFiling) {
        this.capabilityVersionSpecificFiling = capabilityVersionSpecificFiling;
    }


    /**
     * Gets the capabilityJoin value for this CmisRepositoryCapabilitiesType.
     * 
     * @return capabilityJoin
     */
    public org.alfresco.repo.cmis.ws.EnumCapabilityJoin getCapabilityJoin() {
        return capabilityJoin;
    }


    /**
     * Sets the capabilityJoin value for this CmisRepositoryCapabilitiesType.
     * 
     * @param capabilityJoin
     */
    public void setCapabilityJoin(org.alfresco.repo.cmis.ws.EnumCapabilityJoin capabilityJoin) {
        this.capabilityJoin = capabilityJoin;
    }


    /**
     * Gets the _any value for this CmisRepositoryCapabilitiesType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisRepositoryCapabilitiesType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisRepositoryCapabilitiesType)) return false;
        CmisRepositoryCapabilitiesType other = (CmisRepositoryCapabilitiesType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.capabilityACL==null && other.getCapabilityACL()==null) || 
             (this.capabilityACL!=null &&
              this.capabilityACL.equals(other.getCapabilityACL()))) &&
            this.capabilityAllVersionsSearchable == other.isCapabilityAllVersionsSearchable() &&
            ((this.capabilityChanges==null && other.getCapabilityChanges()==null) || 
             (this.capabilityChanges!=null &&
              this.capabilityChanges.equals(other.getCapabilityChanges()))) &&
            ((this.capabilityContentStreamUpdatability==null && other.getCapabilityContentStreamUpdatability()==null) || 
             (this.capabilityContentStreamUpdatability!=null &&
              this.capabilityContentStreamUpdatability.equals(other.getCapabilityContentStreamUpdatability()))) &&
            this.capabilityGetDescendants == other.isCapabilityGetDescendants() &&
            this.capabilityGetFolderTree == other.isCapabilityGetFolderTree() &&
            this.capabilityMultifiling == other.isCapabilityMultifiling() &&
            this.capabilityPWCSearchable == other.isCapabilityPWCSearchable() &&
            this.capabilityPWCUpdatable == other.isCapabilityPWCUpdatable() &&
            ((this.capabilityQuery==null && other.getCapabilityQuery()==null) || 
             (this.capabilityQuery!=null &&
              this.capabilityQuery.equals(other.getCapabilityQuery()))) &&
            ((this.capabilityRenditions==null && other.getCapabilityRenditions()==null) || 
             (this.capabilityRenditions!=null &&
              this.capabilityRenditions.equals(other.getCapabilityRenditions()))) &&
            this.capabilityUnfiling == other.isCapabilityUnfiling() &&
            this.capabilityVersionSpecificFiling == other.isCapabilityVersionSpecificFiling() &&
            ((this.capabilityJoin==null && other.getCapabilityJoin()==null) || 
             (this.capabilityJoin!=null &&
              this.capabilityJoin.equals(other.getCapabilityJoin()))) &&
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
        if (getCapabilityACL() != null) {
            _hashCode += getCapabilityACL().hashCode();
        }
        _hashCode += (isCapabilityAllVersionsSearchable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getCapabilityChanges() != null) {
            _hashCode += getCapabilityChanges().hashCode();
        }
        if (getCapabilityContentStreamUpdatability() != null) {
            _hashCode += getCapabilityContentStreamUpdatability().hashCode();
        }
        _hashCode += (isCapabilityGetDescendants() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isCapabilityGetFolderTree() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isCapabilityMultifiling() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isCapabilityPWCSearchable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isCapabilityPWCUpdatable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getCapabilityQuery() != null) {
            _hashCode += getCapabilityQuery().hashCode();
        }
        if (getCapabilityRenditions() != null) {
            _hashCode += getCapabilityRenditions().hashCode();
        }
        _hashCode += (isCapabilityUnfiling() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isCapabilityVersionSpecificFiling() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getCapabilityJoin() != null) {
            _hashCode += getCapabilityJoin().hashCode();
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
        new org.apache.axis.description.TypeDesc(CmisRepositoryCapabilitiesType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisRepositoryCapabilitiesType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityACL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityACL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityACL"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityAllVersionsSearchable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityAllVersionsSearchable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityChanges");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityChanges"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityChanges"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityContentStreamUpdatability");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityContentStreamUpdatability"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityContentStreamUpdates"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityGetDescendants");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityGetDescendants"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityGetFolderTree");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityGetFolderTree"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityMultifiling");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityMultifiling"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityPWCSearchable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityPWCSearchable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityPWCUpdatable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityPWCUpdatable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityQuery");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityQuery"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityQuery"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityRenditions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityRenditions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityRendition"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityUnfiling");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityUnfiling"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityVersionSpecificFiling");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityVersionSpecificFiling"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capabilityJoin");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "capabilityJoin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCapabilityJoin"));
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
