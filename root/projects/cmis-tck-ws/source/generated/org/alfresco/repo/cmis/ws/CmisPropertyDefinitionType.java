/**
 * CmisPropertyDefinitionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisPropertyDefinitionType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private java.lang.String id;

    private java.lang.String localName;

    private org.apache.axis.types.URI localNamespace;

    private java.lang.String displayName;

    private java.lang.String queryName;

    private java.lang.String description;

    private org.alfresco.repo.cmis.ws.EnumPropertyType propertyType;

    private org.alfresco.repo.cmis.ws.EnumCardinality cardinality;

    private org.alfresco.repo.cmis.ws.EnumUpdatability updatability;

    private java.lang.Boolean inherited;

    private boolean required;

    private boolean queryable;

    private boolean orderable;

    private java.lang.Boolean openChoice;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisPropertyDefinitionType() {
    }

    public CmisPropertyDefinitionType(
           java.lang.String id,
           java.lang.String localName,
           org.apache.axis.types.URI localNamespace,
           java.lang.String displayName,
           java.lang.String queryName,
           java.lang.String description,
           org.alfresco.repo.cmis.ws.EnumPropertyType propertyType,
           org.alfresco.repo.cmis.ws.EnumCardinality cardinality,
           org.alfresco.repo.cmis.ws.EnumUpdatability updatability,
           java.lang.Boolean inherited,
           boolean required,
           boolean queryable,
           boolean orderable,
           java.lang.Boolean openChoice,
           org.apache.axis.message.MessageElement [] _any) {
           this.id = id;
           this.localName = localName;
           this.localNamespace = localNamespace;
           this.displayName = displayName;
           this.queryName = queryName;
           this.description = description;
           this.propertyType = propertyType;
           this.cardinality = cardinality;
           this.updatability = updatability;
           this.inherited = inherited;
           this.required = required;
           this.queryable = queryable;
           this.orderable = orderable;
           this.openChoice = openChoice;
           this._any = _any;
    }


    /**
     * Gets the id value for this CmisPropertyDefinitionType.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this CmisPropertyDefinitionType.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the localName value for this CmisPropertyDefinitionType.
     * 
     * @return localName
     */
    public java.lang.String getLocalName() {
        return localName;
    }


    /**
     * Sets the localName value for this CmisPropertyDefinitionType.
     * 
     * @param localName
     */
    public void setLocalName(java.lang.String localName) {
        this.localName = localName;
    }


    /**
     * Gets the localNamespace value for this CmisPropertyDefinitionType.
     * 
     * @return localNamespace
     */
    public org.apache.axis.types.URI getLocalNamespace() {
        return localNamespace;
    }


    /**
     * Sets the localNamespace value for this CmisPropertyDefinitionType.
     * 
     * @param localNamespace
     */
    public void setLocalNamespace(org.apache.axis.types.URI localNamespace) {
        this.localNamespace = localNamespace;
    }


    /**
     * Gets the displayName value for this CmisPropertyDefinitionType.
     * 
     * @return displayName
     */
    public java.lang.String getDisplayName() {
        return displayName;
    }


    /**
     * Sets the displayName value for this CmisPropertyDefinitionType.
     * 
     * @param displayName
     */
    public void setDisplayName(java.lang.String displayName) {
        this.displayName = displayName;
    }


    /**
     * Gets the queryName value for this CmisPropertyDefinitionType.
     * 
     * @return queryName
     */
    public java.lang.String getQueryName() {
        return queryName;
    }


    /**
     * Sets the queryName value for this CmisPropertyDefinitionType.
     * 
     * @param queryName
     */
    public void setQueryName(java.lang.String queryName) {
        this.queryName = queryName;
    }


    /**
     * Gets the description value for this CmisPropertyDefinitionType.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this CmisPropertyDefinitionType.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the propertyType value for this CmisPropertyDefinitionType.
     * 
     * @return propertyType
     */
    public org.alfresco.repo.cmis.ws.EnumPropertyType getPropertyType() {
        return propertyType;
    }


    /**
     * Sets the propertyType value for this CmisPropertyDefinitionType.
     * 
     * @param propertyType
     */
    public void setPropertyType(org.alfresco.repo.cmis.ws.EnumPropertyType propertyType) {
        this.propertyType = propertyType;
    }


    /**
     * Gets the cardinality value for this CmisPropertyDefinitionType.
     * 
     * @return cardinality
     */
    public org.alfresco.repo.cmis.ws.EnumCardinality getCardinality() {
        return cardinality;
    }


    /**
     * Sets the cardinality value for this CmisPropertyDefinitionType.
     * 
     * @param cardinality
     */
    public void setCardinality(org.alfresco.repo.cmis.ws.EnumCardinality cardinality) {
        this.cardinality = cardinality;
    }


    /**
     * Gets the updatability value for this CmisPropertyDefinitionType.
     * 
     * @return updatability
     */
    public org.alfresco.repo.cmis.ws.EnumUpdatability getUpdatability() {
        return updatability;
    }


    /**
     * Sets the updatability value for this CmisPropertyDefinitionType.
     * 
     * @param updatability
     */
    public void setUpdatability(org.alfresco.repo.cmis.ws.EnumUpdatability updatability) {
        this.updatability = updatability;
    }


    /**
     * Gets the inherited value for this CmisPropertyDefinitionType.
     * 
     * @return inherited
     */
    public java.lang.Boolean getInherited() {
        return inherited;
    }


    /**
     * Sets the inherited value for this CmisPropertyDefinitionType.
     * 
     * @param inherited
     */
    public void setInherited(java.lang.Boolean inherited) {
        this.inherited = inherited;
    }


    /**
     * Gets the required value for this CmisPropertyDefinitionType.
     * 
     * @return required
     */
    public boolean isRequired() {
        return required;
    }


    /**
     * Sets the required value for this CmisPropertyDefinitionType.
     * 
     * @param required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }


    /**
     * Gets the queryable value for this CmisPropertyDefinitionType.
     * 
     * @return queryable
     */
    public boolean isQueryable() {
        return queryable;
    }


    /**
     * Sets the queryable value for this CmisPropertyDefinitionType.
     * 
     * @param queryable
     */
    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
    }


    /**
     * Gets the orderable value for this CmisPropertyDefinitionType.
     * 
     * @return orderable
     */
    public boolean isOrderable() {
        return orderable;
    }


    /**
     * Sets the orderable value for this CmisPropertyDefinitionType.
     * 
     * @param orderable
     */
    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }


    /**
     * Gets the openChoice value for this CmisPropertyDefinitionType.
     * 
     * @return openChoice
     */
    public java.lang.Boolean getOpenChoice() {
        return openChoice;
    }


    /**
     * Sets the openChoice value for this CmisPropertyDefinitionType.
     * 
     * @param openChoice
     */
    public void setOpenChoice(java.lang.Boolean openChoice) {
        this.openChoice = openChoice;
    }


    /**
     * Gets the _any value for this CmisPropertyDefinitionType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisPropertyDefinitionType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisPropertyDefinitionType)) return false;
        CmisPropertyDefinitionType other = (CmisPropertyDefinitionType) obj;
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
            ((this.propertyType==null && other.getPropertyType()==null) || 
             (this.propertyType!=null &&
              this.propertyType.equals(other.getPropertyType()))) &&
            ((this.cardinality==null && other.getCardinality()==null) || 
             (this.cardinality!=null &&
              this.cardinality.equals(other.getCardinality()))) &&
            ((this.updatability==null && other.getUpdatability()==null) || 
             (this.updatability!=null &&
              this.updatability.equals(other.getUpdatability()))) &&
            ((this.inherited==null && other.getInherited()==null) || 
             (this.inherited!=null &&
              this.inherited.equals(other.getInherited()))) &&
            this.required == other.isRequired() &&
            this.queryable == other.isQueryable() &&
            this.orderable == other.isOrderable() &&
            ((this.openChoice==null && other.getOpenChoice()==null) || 
             (this.openChoice!=null &&
              this.openChoice.equals(other.getOpenChoice()))) &&
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
        if (getPropertyType() != null) {
            _hashCode += getPropertyType().hashCode();
        }
        if (getCardinality() != null) {
            _hashCode += getCardinality().hashCode();
        }
        if (getUpdatability() != null) {
            _hashCode += getUpdatability().hashCode();
        }
        if (getInherited() != null) {
            _hashCode += getInherited().hashCode();
        }
        _hashCode += (isRequired() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isQueryable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isOrderable() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getOpenChoice() != null) {
            _hashCode += getOpenChoice().hashCode();
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
        new org.apache.axis.description.TypeDesc(CmisPropertyDefinitionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertyDefinitionType"));
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
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
        elemField.setFieldName("propertyType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "propertyType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumPropertyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardinality");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cardinality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumCardinality"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("updatability");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "updatability"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "enumUpdatability"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inherited");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "inherited"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("required");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "required"));
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
        elemField.setFieldName("orderable");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "orderable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("openChoice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "openChoice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
