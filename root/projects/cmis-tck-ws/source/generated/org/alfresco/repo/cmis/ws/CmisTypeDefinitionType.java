/**
 * CmisTypeDefinitionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisTypeDefinitionType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private java.lang.String id;

    private java.lang.String localName;

    private org.apache.axis.types.URI localNamespace;

    private java.lang.String displayName;

    private java.lang.String queryName;

    private java.lang.String description;

    private org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds baseId;

    /* This is the id for the parent type definition. If
     * 						this is a base type,
     * 						this is not present. */
    private java.lang.String parentId;

    private boolean creatable;

    private boolean fileable;

    private boolean queryable;

    private boolean fulltextIndexed;

    private boolean includedInSupertypeQuery;

    private boolean controllablePolicy;

    private boolean controllableACL;

    private org.alfresco.repo.cmis.ws.CmisPropertyBooleanDefinitionType[] propertyBooleanDefinition;

    private org.alfresco.repo.cmis.ws.CmisPropertyDateTimeDefinitionType[] propertyDateTimeDefinition;

    private org.alfresco.repo.cmis.ws.CmisPropertyDecimalDefinitionType[] propertyDecimalDefinition;

    private org.alfresco.repo.cmis.ws.CmisPropertyIdDefinitionType[] propertyIdDefinition;

    private org.alfresco.repo.cmis.ws.CmisPropertyIntegerDefinitionType[] propertyIntegerDefinition;

    private org.alfresco.repo.cmis.ws.CmisPropertyHtmlDefinitionType[] propertyHtmlDefinition;

    private org.alfresco.repo.cmis.ws.CmisPropertyStringDefinitionType[] propertyStringDefinition;

    private org.alfresco.repo.cmis.ws.CmisPropertyUriDefinitionType[] propertyUriDefinition;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisTypeDefinitionType() {
    }

    public CmisTypeDefinitionType(
           java.lang.String id,
           java.lang.String localName,
           org.apache.axis.types.URI localNamespace,
           java.lang.String displayName,
           java.lang.String queryName,
           java.lang.String description,
           org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds baseId,
           java.lang.String parentId,
           boolean creatable,
           boolean fileable,
           boolean queryable,
           boolean fulltextIndexed,
           boolean includedInSupertypeQuery,
           boolean controllablePolicy,
           boolean controllableACL,
           org.alfresco.repo.cmis.ws.CmisPropertyBooleanDefinitionType[] propertyBooleanDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyDateTimeDefinitionType[] propertyDateTimeDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyDecimalDefinitionType[] propertyDecimalDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyIdDefinitionType[] propertyIdDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyIntegerDefinitionType[] propertyIntegerDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyHtmlDefinitionType[] propertyHtmlDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyStringDefinitionType[] propertyStringDefinition,
           org.alfresco.repo.cmis.ws.CmisPropertyUriDefinitionType[] propertyUriDefinition,
           org.apache.axis.message.MessageElement [] _any) {
           this.id = id;
           this.localName = localName;
           this.localNamespace = localNamespace;
           this.displayName = displayName;
           this.queryName = queryName;
           this.description = description;
           this.baseId = baseId;
           this.parentId = parentId;
           this.creatable = creatable;
           this.fileable = fileable;
           this.queryable = queryable;
           this.fulltextIndexed = fulltextIndexed;
           this.includedInSupertypeQuery = includedInSupertypeQuery;
           this.controllablePolicy = controllablePolicy;
           this.controllableACL = controllableACL;
           this.propertyBooleanDefinition = propertyBooleanDefinition;
           this.propertyDateTimeDefinition = propertyDateTimeDefinition;
           this.propertyDecimalDefinition = propertyDecimalDefinition;
           this.propertyIdDefinition = propertyIdDefinition;
           this.propertyIntegerDefinition = propertyIntegerDefinition;
           this.propertyHtmlDefinition = propertyHtmlDefinition;
           this.propertyStringDefinition = propertyStringDefinition;
           this.propertyUriDefinition = propertyUriDefinition;
           this._any = _any;
    }


    /**
     * Gets the id value for this CmisTypeDefinitionType.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this CmisTypeDefinitionType.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the localName value for this CmisTypeDefinitionType.
     * 
     * @return localName
     */
    public java.lang.String getLocalName() {
        return localName;
    }


    /**
     * Sets the localName value for this CmisTypeDefinitionType.
     * 
     * @param localName
     */
    public void setLocalName(java.lang.String localName) {
        this.localName = localName;
    }


    /**
     * Gets the localNamespace value for this CmisTypeDefinitionType.
     * 
     * @return localNamespace
     */
    public org.apache.axis.types.URI getLocalNamespace() {
        return localNamespace;
    }


    /**
     * Sets the localNamespace value for this CmisTypeDefinitionType.
     * 
     * @param localNamespace
     */
    public void setLocalNamespace(org.apache.axis.types.URI localNamespace) {
        this.localNamespace = localNamespace;
    }


    /**
     * Gets the displayName value for this CmisTypeDefinitionType.
     * 
     * @return displayName
     */
    public java.lang.String getDisplayName() {
        return displayName;
    }


    /**
     * Sets the displayName value for this CmisTypeDefinitionType.
     * 
     * @param displayName
     */
    public void setDisplayName(java.lang.String displayName) {
        this.displayName = displayName;
    }


    /**
     * Gets the queryName value for this CmisTypeDefinitionType.
     * 
     * @return queryName
     */
    public java.lang.String getQueryName() {
        return queryName;
    }


    /**
     * Sets the queryName value for this CmisTypeDefinitionType.
     * 
     * @param queryName
     */
    public void setQueryName(java.lang.String queryName) {
        this.queryName = queryName;
    }


    /**
     * Gets the description value for this CmisTypeDefinitionType.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this CmisTypeDefinitionType.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the baseId value for this CmisTypeDefinitionType.
     * 
     * @return baseId
     */
    public org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds getBaseId() {
        return baseId;
    }


    /**
     * Sets the baseId value for this CmisTypeDefinitionType.
     * 
     * @param baseId
     */
    public void setBaseId(org.alfresco.repo.cmis.ws.EnumBaseObjectTypeIds baseId) {
        this.baseId = baseId;
    }


    /**
     * Gets the parentId value for this CmisTypeDefinitionType.
     * 
     * @return parentId   * This is the id for the parent type definition. If
     * 						this is a base type,
     * 						this is not present.
     */
    public java.lang.String getParentId() {
        return parentId;
    }


    /**
     * Sets the parentId value for this CmisTypeDefinitionType.
     * 
     * @param parentId   * This is the id for the parent type definition. If
     * 						this is a base type,
     * 						this is not present.
     */
    public void setParentId(java.lang.String parentId) {
        this.parentId = parentId;
    }


    /**
     * Gets the creatable value for this CmisTypeDefinitionType.
     * 
     * @return creatable
     */
    public boolean isCreatable() {
        return creatable;
    }


    /**
     * Sets the creatable value for this CmisTypeDefinitionType.
     * 
     * @param creatable
     */
    public void setCreatable(boolean creatable) {
        this.creatable = creatable;
    }


    /**
     * Gets the fileable value for this CmisTypeDefinitionType.
     * 
     * @return fileable
     */
    public boolean isFileable() {
        return fileable;
    }


    /**
     * Sets the fileable value for this CmisTypeDefinitionType.
     * 
     * @param fileable
     */
    public void setFileable(boolean fileable) {
        this.fileable = fileable;
    }


    /**
     * Gets the queryable value for this CmisTypeDefinitionType.
     * 
     * @return queryable
     */
    public boolean isQueryable() {
        return queryable;
    }


    /**
     * Sets the queryable value for this CmisTypeDefinitionType.
     * 
     * @param queryable
     */
    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
    }


    /**
     * Gets the fulltextIndexed value for this CmisTypeDefinitionType.
     * 
     * @return fulltextIndexed
     */
    public boolean isFulltextIndexed() {
        return fulltextIndexed;
    }


    /**
     * Sets the fulltextIndexed value for this CmisTypeDefinitionType.
     * 
     * @param fulltextIndexed
     */
    public void setFulltextIndexed(boolean fulltextIndexed) {
        this.fulltextIndexed = fulltextIndexed;
    }


    /**
     * Gets the includedInSupertypeQuery value for this CmisTypeDefinitionType.
     * 
     * @return includedInSupertypeQuery
     */
    public boolean isIncludedInSupertypeQuery() {
        return includedInSupertypeQuery;
    }


    /**
     * Sets the includedInSupertypeQuery value for this CmisTypeDefinitionType.
     * 
     * @param includedInSupertypeQuery
     */
    public void setIncludedInSupertypeQuery(boolean includedInSupertypeQuery) {
        this.includedInSupertypeQuery = includedInSupertypeQuery;
    }


    /**
     * Gets the controllablePolicy value for this CmisTypeDefinitionType.
     * 
     * @return controllablePolicy
     */
    public boolean isControllablePolicy() {
        return controllablePolicy;
    }


    /**
     * Sets the controllablePolicy value for this CmisTypeDefinitionType.
     * 
     * @param controllablePolicy
     */
    public void setControllablePolicy(boolean controllablePolicy) {
        this.controllablePolicy = controllablePolicy;
    }


    /**
     * Gets the controllableACL value for this CmisTypeDefinitionType.
     * 
     * @return controllableACL
     */
    public boolean isControllableACL() {
        return controllableACL;
    }


    /**
     * Sets the controllableACL value for this CmisTypeDefinitionType.
     * 
     * @param controllableACL
     */
    public void setControllableACL(boolean controllableACL) {
        this.controllableACL = controllableACL;
    }


    /**
     * Gets the propertyBooleanDefinition value for this CmisTypeDefinitionType.
     * 
     * @return propertyBooleanDefinition
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyBooleanDefinitionType[] getPropertyBooleanDefinition() {
        return propertyBooleanDefinition;
    }


    /**
     * Sets the propertyBooleanDefinition value for this CmisTypeDefinitionType.
     * 
     * @param propertyBooleanDefinition
     */
    public void setPropertyBooleanDefinition(org.alfresco.repo.cmis.ws.CmisPropertyBooleanDefinitionType[] propertyBooleanDefinition) {
        this.propertyBooleanDefinition = propertyBooleanDefinition;
    }


    /**
     * Gets the propertyDateTimeDefinition value for this CmisTypeDefinitionType.
     * 
     * @return propertyDateTimeDefinition
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyDateTimeDefinitionType[] getPropertyDateTimeDefinition() {
        return propertyDateTimeDefinition;
    }


    /**
     * Sets the propertyDateTimeDefinition value for this CmisTypeDefinitionType.
     * 
     * @param propertyDateTimeDefinition
     */
    public void setPropertyDateTimeDefinition(org.alfresco.repo.cmis.ws.CmisPropertyDateTimeDefinitionType[] propertyDateTimeDefinition) {
        this.propertyDateTimeDefinition = propertyDateTimeDefinition;
    }


    /**
     * Gets the propertyDecimalDefinition value for this CmisTypeDefinitionType.
     * 
     * @return propertyDecimalDefinition
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyDecimalDefinitionType[] getPropertyDecimalDefinition() {
        return propertyDecimalDefinition;
    }


    /**
     * Sets the propertyDecimalDefinition value for this CmisTypeDefinitionType.
     * 
     * @param propertyDecimalDefinition
     */
    public void setPropertyDecimalDefinition(org.alfresco.repo.cmis.ws.CmisPropertyDecimalDefinitionType[] propertyDecimalDefinition) {
        this.propertyDecimalDefinition = propertyDecimalDefinition;
    }


    /**
     * Gets the propertyIdDefinition value for this CmisTypeDefinitionType.
     * 
     * @return propertyIdDefinition
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyIdDefinitionType[] getPropertyIdDefinition() {
        return propertyIdDefinition;
    }


    /**
     * Sets the propertyIdDefinition value for this CmisTypeDefinitionType.
     * 
     * @param propertyIdDefinition
     */
    public void setPropertyIdDefinition(org.alfresco.repo.cmis.ws.CmisPropertyIdDefinitionType[] propertyIdDefinition) {
        this.propertyIdDefinition = propertyIdDefinition;
    }


    /**
     * Gets the propertyIntegerDefinition value for this CmisTypeDefinitionType.
     * 
     * @return propertyIntegerDefinition
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyIntegerDefinitionType[] getPropertyIntegerDefinition() {
        return propertyIntegerDefinition;
    }


    /**
     * Sets the propertyIntegerDefinition value for this CmisTypeDefinitionType.
     * 
     * @param propertyIntegerDefinition
     */
    public void setPropertyIntegerDefinition(org.alfresco.repo.cmis.ws.CmisPropertyIntegerDefinitionType[] propertyIntegerDefinition) {
        this.propertyIntegerDefinition = propertyIntegerDefinition;
    }


    /**
     * Gets the propertyHtmlDefinition value for this CmisTypeDefinitionType.
     * 
     * @return propertyHtmlDefinition
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyHtmlDefinitionType[] getPropertyHtmlDefinition() {
        return propertyHtmlDefinition;
    }


    /**
     * Sets the propertyHtmlDefinition value for this CmisTypeDefinitionType.
     * 
     * @param propertyHtmlDefinition
     */
    public void setPropertyHtmlDefinition(org.alfresco.repo.cmis.ws.CmisPropertyHtmlDefinitionType[] propertyHtmlDefinition) {
        this.propertyHtmlDefinition = propertyHtmlDefinition;
    }


    /**
     * Gets the propertyStringDefinition value for this CmisTypeDefinitionType.
     * 
     * @return propertyStringDefinition
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyStringDefinitionType[] getPropertyStringDefinition() {
        return propertyStringDefinition;
    }


    /**
     * Sets the propertyStringDefinition value for this CmisTypeDefinitionType.
     * 
     * @param propertyStringDefinition
     */
    public void setPropertyStringDefinition(org.alfresco.repo.cmis.ws.CmisPropertyStringDefinitionType[] propertyStringDefinition) {
        this.propertyStringDefinition = propertyStringDefinition;
    }


    /**
     * Gets the propertyUriDefinition value for this CmisTypeDefinitionType.
     * 
     * @return propertyUriDefinition
     */
    public org.alfresco.repo.cmis.ws.CmisPropertyUriDefinitionType[] getPropertyUriDefinition() {
        return propertyUriDefinition;
    }


    /**
     * Sets the propertyUriDefinition value for this CmisTypeDefinitionType.
     * 
     * @param propertyUriDefinition
     */
    public void setPropertyUriDefinition(org.alfresco.repo.cmis.ws.CmisPropertyUriDefinitionType[] propertyUriDefinition) {
        this.propertyUriDefinition = propertyUriDefinition;
    }


    /**
     * Gets the _any value for this CmisTypeDefinitionType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisTypeDefinitionType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisTypeDefinitionType)) return false;
        CmisTypeDefinitionType other = (CmisTypeDefinitionType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.localName==null && other.getLocalName()==null) || 
             (this.localName!=null &&
              this.localName.equals(other.getLocalName()))) &&
            ((this.localNamespace==null && other.getLocalNamespace()==null) || 
             (this.localNamespace!=null &&
              this.localNamespace.equals(other.getLocalNamespace()))) &&
            ((this.displayName==null && other.getDisplayName()==null) || 
             (this.displayName!=null &&
              this.displayName.equals(other.getDisplayName()))) &&
            ((this.queryName==null && other.getQueryName()==null) || 
             (this.queryName!=null &&
              this.queryName.equals(other.getQueryName()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.baseId==null && other.getBaseId()==null) || 
             (this.baseId!=null &&
              this.baseId.equals(other.getBaseId()))) &&
            ((this.parentId==null && other.getParentId()==null) || 
             (this.parentId!=null &&
              this.parentId.equals(other.getParentId()))) &&
            this.creatable == other.isCreatable() &&
            this.fileable == other.isFileable() &&
            this.queryable == other.isQueryable() &&
            this.fulltextIndexed == other.isFulltextIndexed() &&
            this.includedInSupertypeQuery == other.isIncludedInSupertypeQuery() &&
            this.controllablePolicy == other.isControllablePolicy() &&
            this.controllableACL == other.isControllableACL() &&
            ((this.propertyBooleanDefinition==null && other.getPropertyBooleanDefinition()==null) || 
             (this.propertyBooleanDefinition!=null &&
              this.propertyBooleanDefinition.equals(other.getPropertyBooleanDefinition()))) &&
            ((this.propertyDateTimeDefinition==null && other.getPropertyDateTimeDefinition()==null) || 
             (this.propertyDateTimeDefinition!=null &&
              this.propertyDateTimeDefinition.equals(other.getPropertyDateTimeDefinition()))) &&
            ((this.propertyDecimalDefinition==null && other.getPropertyDecimalDefinition()==null) || 
             (this.propertyDecimalDefinition!=null &&
              this.propertyDecimalDefinition.equals(other.getPropertyDecimalDefinition()))) &&
            ((this.propertyIdDefinition==null && other.getPropertyIdDefinition()==null) || 
             (this.propertyIdDefinition!=null &&
              this.propertyIdDefinition.equals(other.getPropertyIdDefinition()))) &&
            ((this.propertyIntegerDefinition==null && other.getPropertyIntegerDefinition()==null) || 
             (this.propertyIntegerDefinition!=null &&
              this.propertyIntegerDefinition.equals(other.getPropertyIntegerDefinition()))) &&
            ((this.propertyHtmlDefinition==null && other.getPropertyHtmlDefinition()==null) || 
             (this.propertyHtmlDefinition!=null &&
              this.propertyHtmlDefinition.equals(other.getPropertyHtmlDefinition()))) &&
            ((this.propertyStringDefinition==null && other.getPropertyStringDefinition()==null) || 
             (this.propertyStringDefinition!=null &&
              this.propertyStringDefinition.equals(other.getPropertyStringDefinition()))) &&
            ((this.propertyUriDefinition==null && other.getPropertyUriDefinition()==null) || 
             (this.propertyUriDefinition!=null &&
              this.propertyUriDefinition.equals(other.getPropertyUriDefinition()))) &&
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getLocalName() != null) {
            _hashCode += getLocalName().hashCode();
        }
        if (getLocalNamespace() != null) {
            _hashCode += getLocalNamespace().hashCode();
        }
        if (getDisplayName() != null) {
            _hashCode += getDisplayName().hashCode();
        }
        if (getQueryName() != null) {
            _hashCode += getQueryName().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getBaseId() != null) {
            _hashCode += getBaseId().hashCode();
        }
        if (getParentId() != null) {
            _hashCode += getParentId().hashCode();
        }
        _hashCode += (isCreatable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isFileable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isQueryable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isFulltextIndexed() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isIncludedInSupertypeQuery() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isControllablePolicy() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isControllableACL() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getPropertyBooleanDefinition() != null) {
            _hashCode += getPropertyBooleanDefinition().hashCode();
        }
        if (getPropertyDateTimeDefinition() != null) {
            _hashCode += getPropertyDateTimeDefinition().hashCode();
        }
        if (getPropertyDecimalDefinition() != null) {
            _hashCode += getPropertyDecimalDefinition().hashCode();
        }
        if (getPropertyIdDefinition() != null) {
            _hashCode += getPropertyIdDefinition().hashCode();
        }
        if (getPropertyIntegerDefinition() != null) {
            _hashCode += getPropertyIntegerDefinition().hashCode();
        }
        if (getPropertyHtmlDefinition() != null) {
            _hashCode += getPropertyHtmlDefinition().hashCode();
        }
        if (getPropertyStringDefinition() != null) {
            _hashCode += getPropertyStringDefinition().hashCode();
        }
        if (getPropertyUriDefinition() != null) {
            _hashCode += getPropertyUriDefinition().hashCode();
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
        new org.apache.axis.description.TypeDesc(CmisTypeDefinitionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisTypeDefinitionType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("localName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "localName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("localNamespace");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "localNamespace"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("displayName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "displayName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "queryName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "baseId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumBaseObjectTypeIds"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parentId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "parentId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creatable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "creatable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fileable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "fileable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("queryable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "queryable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fulltextIndexed");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "fulltextIndexed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includedInSupertypeQuery");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "includedInSupertypeQuery"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("controllablePolicy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "controllablePolicy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("controllableACL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "controllableACL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyBooleanDefinition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyBooleanDefinition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyBooleanDefinitionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyDateTimeDefinition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyDateTimeDefinition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDateTimeDefinitionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyDecimalDefinition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyDecimalDefinition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDecimalDefinitionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyIdDefinition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyIdDefinition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyIdDefinitionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyIntegerDefinition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyIntegerDefinition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyIntegerDefinitionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyHtmlDefinition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyHtmlDefinition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyHtmlDefinitionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyStringDefinition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyStringDefinition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyStringDefinitionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("propertyUriDefinition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyUriDefinition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyUriDefinitionType"));
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
