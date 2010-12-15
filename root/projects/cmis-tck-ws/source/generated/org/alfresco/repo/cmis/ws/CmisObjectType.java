/**
 * CmisObjectType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisObjectType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private org.alfresco.repo.cmis.ws.CmisPropertiesType properties;

    private org.alfresco.repo.cmis.ws.CmisAllowableActionsType allowableActions;

    private org.alfresco.repo.cmis.ws.CmisObjectType[] relationship;

    private org.alfresco.repo.cmis.ws.CmisChangeEventType changeEventInfo;

    private org.alfresco.repo.cmis.ws.CmisAccessControlListType acl;

    private java.lang.Boolean exactACL;

    private org.alfresco.repo.cmis.ws.CmisListOfIdsType policyIds;

    private org.alfresco.repo.cmis.ws.CmisRenditionType[] rendition;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisObjectType() {
    }

    public CmisObjectType(
           org.alfresco.repo.cmis.ws.CmisPropertiesType properties,
           org.alfresco.repo.cmis.ws.CmisAllowableActionsType allowableActions,
           org.alfresco.repo.cmis.ws.CmisObjectType[] relationship,
           org.alfresco.repo.cmis.ws.CmisChangeEventType changeEventInfo,
           org.alfresco.repo.cmis.ws.CmisAccessControlListType acl,
           java.lang.Boolean exactACL,
           org.alfresco.repo.cmis.ws.CmisListOfIdsType policyIds,
           org.alfresco.repo.cmis.ws.CmisRenditionType[] rendition,
           org.apache.axis.message.MessageElement [] _any) {
           this.properties = properties;
           this.allowableActions = allowableActions;
           this.relationship = relationship;
           this.changeEventInfo = changeEventInfo;
           this.acl = acl;
           this.exactACL = exactACL;
           this.policyIds = policyIds;
           this.rendition = rendition;
           this._any = _any;
    }


    /**
     * Gets the properties value for this CmisObjectType.
     * 
     * @return properties
     */
    public org.alfresco.repo.cmis.ws.CmisPropertiesType getProperties() {
        return properties;
    }


    /**
     * Sets the properties value for this CmisObjectType.
     * 
     * @param properties
     */
    public void setProperties(org.alfresco.repo.cmis.ws.CmisPropertiesType properties) {
        this.properties = properties;
    }


    /**
     * Gets the allowableActions value for this CmisObjectType.
     * 
     * @return allowableActions
     */
    public org.alfresco.repo.cmis.ws.CmisAllowableActionsType getAllowableActions() {
        return allowableActions;
    }


    /**
     * Sets the allowableActions value for this CmisObjectType.
     * 
     * @param allowableActions
     */
    public void setAllowableActions(org.alfresco.repo.cmis.ws.CmisAllowableActionsType allowableActions) {
        this.allowableActions = allowableActions;
    }


    /**
     * Gets the relationship value for this CmisObjectType.
     * 
     * @return relationship
     */
    public org.alfresco.repo.cmis.ws.CmisObjectType[] getRelationship() {
        return relationship;
    }


    /**
     * Sets the relationship value for this CmisObjectType.
     * 
     * @param relationship
     */
    public void setRelationship(org.alfresco.repo.cmis.ws.CmisObjectType[] relationship) {
        this.relationship = relationship;
    }

    public org.alfresco.repo.cmis.ws.CmisObjectType getRelationship(int i) {
        return this.relationship[i];
    }

    public void setRelationship(int i, org.alfresco.repo.cmis.ws.CmisObjectType _value) {
        this.relationship[i] = _value;
    }


    /**
     * Gets the changeEventInfo value for this CmisObjectType.
     * 
     * @return changeEventInfo
     */
    public org.alfresco.repo.cmis.ws.CmisChangeEventType getChangeEventInfo() {
        return changeEventInfo;
    }


    /**
     * Sets the changeEventInfo value for this CmisObjectType.
     * 
     * @param changeEventInfo
     */
    public void setChangeEventInfo(org.alfresco.repo.cmis.ws.CmisChangeEventType changeEventInfo) {
        this.changeEventInfo = changeEventInfo;
    }


    /**
     * Gets the acl value for this CmisObjectType.
     * 
     * @return acl
     */
    public org.alfresco.repo.cmis.ws.CmisAccessControlListType getAcl() {
        return acl;
    }


    /**
     * Sets the acl value for this CmisObjectType.
     * 
     * @param acl
     */
    public void setAcl(org.alfresco.repo.cmis.ws.CmisAccessControlListType acl) {
        this.acl = acl;
    }


    /**
     * Gets the exactACL value for this CmisObjectType.
     * 
     * @return exactACL
     */
    public java.lang.Boolean getExactACL() {
        return exactACL;
    }


    /**
     * Sets the exactACL value for this CmisObjectType.
     * 
     * @param exactACL
     */
    public void setExactACL(java.lang.Boolean exactACL) {
        this.exactACL = exactACL;
    }


    /**
     * Gets the policyIds value for this CmisObjectType.
     * 
     * @return policyIds
     */
    public org.alfresco.repo.cmis.ws.CmisListOfIdsType getPolicyIds() {
        return policyIds;
    }


    /**
     * Sets the policyIds value for this CmisObjectType.
     * 
     * @param policyIds
     */
    public void setPolicyIds(org.alfresco.repo.cmis.ws.CmisListOfIdsType policyIds) {
        this.policyIds = policyIds;
    }


    /**
     * Gets the rendition value for this CmisObjectType.
     * 
     * @return rendition
     */
    public org.alfresco.repo.cmis.ws.CmisRenditionType[] getRendition() {
        return rendition;
    }


    /**
     * Sets the rendition value for this CmisObjectType.
     * 
     * @param rendition
     */
    public void setRendition(org.alfresco.repo.cmis.ws.CmisRenditionType[] rendition) {
        this.rendition = rendition;
    }

    public org.alfresco.repo.cmis.ws.CmisRenditionType getRendition(int i) {
        return this.rendition[i];
    }

    public void setRendition(int i, org.alfresco.repo.cmis.ws.CmisRenditionType _value) {
        this.rendition[i] = _value;
    }


    /**
     * Gets the _any value for this CmisObjectType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisObjectType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisObjectType)) return false;
        CmisObjectType other = (CmisObjectType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.properties==null && other.getProperties()==null) || 
             (this.properties!=null &&
              this.properties.equals(other.getProperties()))) &&
            ((this.allowableActions==null && other.getAllowableActions()==null) || 
             (this.allowableActions!=null &&
              this.allowableActions.equals(other.getAllowableActions()))) &&
            ((this.relationship==null && other.getRelationship()==null) || 
             (this.relationship!=null &&
              java.util.Arrays.equals(this.relationship, other.getRelationship()))) &&
            ((this.changeEventInfo==null && other.getChangeEventInfo()==null) || 
             (this.changeEventInfo!=null &&
              this.changeEventInfo.equals(other.getChangeEventInfo()))) &&
            ((this.acl==null && other.getAcl()==null) || 
             (this.acl!=null &&
              this.acl.equals(other.getAcl()))) &&
            ((this.exactACL==null && other.getExactACL()==null) || 
             (this.exactACL!=null &&
              this.exactACL.equals(other.getExactACL()))) &&
            ((this.policyIds==null && other.getPolicyIds()==null) || 
             (this.policyIds!=null &&
              this.policyIds.equals(other.getPolicyIds()))) &&
            ((this.rendition==null && other.getRendition()==null) || 
             (this.rendition!=null &&
              java.util.Arrays.equals(this.rendition, other.getRendition()))) &&
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
        if (getProperties() != null) {
            _hashCode += getProperties().hashCode();
        }
        if (getAllowableActions() != null) {
            _hashCode += getAllowableActions().hashCode();
        }
        if (getRelationship() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRelationship());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRelationship(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getChangeEventInfo() != null) {
            _hashCode += getChangeEventInfo().hashCode();
        }
        if (getAcl() != null) {
            _hashCode += getAcl().hashCode();
        }
        if (getExactACL() != null) {
            _hashCode += getExactACL().hashCode();
        }
        if (getPolicyIds() != null) {
            _hashCode += getPolicyIds().hashCode();
        }
        if (getRendition() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRendition());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRendition(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
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
        new org.apache.axis.description.TypeDesc(CmisObjectType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisObjectType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("properties");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "properties"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisPropertiesType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allowableActions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "allowableActions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisAllowableActionsType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("relationship");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "relationship"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisObjectType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changeEventInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "changeEventInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisChangeEventType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acl");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "acl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisAccessControlListType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exactACL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "exactACL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("policyIds");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "policyIds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisListOfIdsType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rendition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "rendition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisRenditionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
