/**
 * CmisAllowableActionsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.repo.cmis.ws;

public class CmisAllowableActionsType  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType {
    private java.lang.Boolean canDeleteObject;

    private java.lang.Boolean canUpdateProperties;

    private java.lang.Boolean canGetFolderTree;

    private java.lang.Boolean canGetProperties;

    private java.lang.Boolean canGetObjectRelationships;

    private java.lang.Boolean canGetObjectParents;

    private java.lang.Boolean canGetFolderParent;

    private java.lang.Boolean canGetDescendants;

    private java.lang.Boolean canMoveObject;

    private java.lang.Boolean canDeleteContentStream;

    private java.lang.Boolean canCheckOut;

    private java.lang.Boolean canCancelCheckOut;

    private java.lang.Boolean canCheckIn;

    private java.lang.Boolean canSetContentStream;

    private java.lang.Boolean canGetAllVersions;

    private java.lang.Boolean canAddObjectToFolder;

    private java.lang.Boolean canRemoveObjectFromFolder;

    private java.lang.Boolean canGetContentStream;

    private java.lang.Boolean canApplyPolicy;

    private java.lang.Boolean canGetAppliedPolicies;

    private java.lang.Boolean canRemovePolicy;

    private java.lang.Boolean canGetChildren;

    private java.lang.Boolean canCreateDocument;

    private java.lang.Boolean canCreateFolder;

    private java.lang.Boolean canCreateRelationship;

    private java.lang.Boolean canCreatePolicy;

    private java.lang.Boolean canDeleteTree;

    private java.lang.Boolean canGetRenditions;

    private java.lang.Boolean canGetACL;

    private java.lang.Boolean canApplyACL;

    private org.apache.axis.message.MessageElement [] _any;

    public CmisAllowableActionsType() {
    }

    public CmisAllowableActionsType(
           java.lang.Boolean canDeleteObject,
           java.lang.Boolean canUpdateProperties,
           java.lang.Boolean canGetFolderTree,
           java.lang.Boolean canGetProperties,
           java.lang.Boolean canGetObjectRelationships,
           java.lang.Boolean canGetObjectParents,
           java.lang.Boolean canGetFolderParent,
           java.lang.Boolean canGetDescendants,
           java.lang.Boolean canMoveObject,
           java.lang.Boolean canDeleteContentStream,
           java.lang.Boolean canCheckOut,
           java.lang.Boolean canCancelCheckOut,
           java.lang.Boolean canCheckIn,
           java.lang.Boolean canSetContentStream,
           java.lang.Boolean canGetAllVersions,
           java.lang.Boolean canAddObjectToFolder,
           java.lang.Boolean canRemoveObjectFromFolder,
           java.lang.Boolean canGetContentStream,
           java.lang.Boolean canApplyPolicy,
           java.lang.Boolean canGetAppliedPolicies,
           java.lang.Boolean canRemovePolicy,
           java.lang.Boolean canGetChildren,
           java.lang.Boolean canCreateDocument,
           java.lang.Boolean canCreateFolder,
           java.lang.Boolean canCreateRelationship,
           java.lang.Boolean canCreatePolicy,
           java.lang.Boolean canDeleteTree,
           java.lang.Boolean canGetRenditions,
           java.lang.Boolean canGetACL,
           java.lang.Boolean canApplyACL,
           org.apache.axis.message.MessageElement [] _any) {
           this.canDeleteObject = canDeleteObject;
           this.canUpdateProperties = canUpdateProperties;
           this.canGetFolderTree = canGetFolderTree;
           this.canGetProperties = canGetProperties;
           this.canGetObjectRelationships = canGetObjectRelationships;
           this.canGetObjectParents = canGetObjectParents;
           this.canGetFolderParent = canGetFolderParent;
           this.canGetDescendants = canGetDescendants;
           this.canMoveObject = canMoveObject;
           this.canDeleteContentStream = canDeleteContentStream;
           this.canCheckOut = canCheckOut;
           this.canCancelCheckOut = canCancelCheckOut;
           this.canCheckIn = canCheckIn;
           this.canSetContentStream = canSetContentStream;
           this.canGetAllVersions = canGetAllVersions;
           this.canAddObjectToFolder = canAddObjectToFolder;
           this.canRemoveObjectFromFolder = canRemoveObjectFromFolder;
           this.canGetContentStream = canGetContentStream;
           this.canApplyPolicy = canApplyPolicy;
           this.canGetAppliedPolicies = canGetAppliedPolicies;
           this.canRemovePolicy = canRemovePolicy;
           this.canGetChildren = canGetChildren;
           this.canCreateDocument = canCreateDocument;
           this.canCreateFolder = canCreateFolder;
           this.canCreateRelationship = canCreateRelationship;
           this.canCreatePolicy = canCreatePolicy;
           this.canDeleteTree = canDeleteTree;
           this.canGetRenditions = canGetRenditions;
           this.canGetACL = canGetACL;
           this.canApplyACL = canApplyACL;
           this._any = _any;
    }


    /**
     * Gets the canDeleteObject value for this CmisAllowableActionsType.
     * 
     * @return canDeleteObject
     */
    public java.lang.Boolean getCanDeleteObject() {
        return canDeleteObject;
    }


    /**
     * Sets the canDeleteObject value for this CmisAllowableActionsType.
     * 
     * @param canDeleteObject
     */
    public void setCanDeleteObject(java.lang.Boolean canDeleteObject) {
        this.canDeleteObject = canDeleteObject;
    }


    /**
     * Gets the canUpdateProperties value for this CmisAllowableActionsType.
     * 
     * @return canUpdateProperties
     */
    public java.lang.Boolean getCanUpdateProperties() {
        return canUpdateProperties;
    }


    /**
     * Sets the canUpdateProperties value for this CmisAllowableActionsType.
     * 
     * @param canUpdateProperties
     */
    public void setCanUpdateProperties(java.lang.Boolean canUpdateProperties) {
        this.canUpdateProperties = canUpdateProperties;
    }


    /**
     * Gets the canGetFolderTree value for this CmisAllowableActionsType.
     * 
     * @return canGetFolderTree
     */
    public java.lang.Boolean getCanGetFolderTree() {
        return canGetFolderTree;
    }


    /**
     * Sets the canGetFolderTree value for this CmisAllowableActionsType.
     * 
     * @param canGetFolderTree
     */
    public void setCanGetFolderTree(java.lang.Boolean canGetFolderTree) {
        this.canGetFolderTree = canGetFolderTree;
    }


    /**
     * Gets the canGetProperties value for this CmisAllowableActionsType.
     * 
     * @return canGetProperties
     */
    public java.lang.Boolean getCanGetProperties() {
        return canGetProperties;
    }


    /**
     * Sets the canGetProperties value for this CmisAllowableActionsType.
     * 
     * @param canGetProperties
     */
    public void setCanGetProperties(java.lang.Boolean canGetProperties) {
        this.canGetProperties = canGetProperties;
    }


    /**
     * Gets the canGetObjectRelationships value for this CmisAllowableActionsType.
     * 
     * @return canGetObjectRelationships
     */
    public java.lang.Boolean getCanGetObjectRelationships() {
        return canGetObjectRelationships;
    }


    /**
     * Sets the canGetObjectRelationships value for this CmisAllowableActionsType.
     * 
     * @param canGetObjectRelationships
     */
    public void setCanGetObjectRelationships(java.lang.Boolean canGetObjectRelationships) {
        this.canGetObjectRelationships = canGetObjectRelationships;
    }


    /**
     * Gets the canGetObjectParents value for this CmisAllowableActionsType.
     * 
     * @return canGetObjectParents
     */
    public java.lang.Boolean getCanGetObjectParents() {
        return canGetObjectParents;
    }


    /**
     * Sets the canGetObjectParents value for this CmisAllowableActionsType.
     * 
     * @param canGetObjectParents
     */
    public void setCanGetObjectParents(java.lang.Boolean canGetObjectParents) {
        this.canGetObjectParents = canGetObjectParents;
    }


    /**
     * Gets the canGetFolderParent value for this CmisAllowableActionsType.
     * 
     * @return canGetFolderParent
     */
    public java.lang.Boolean getCanGetFolderParent() {
        return canGetFolderParent;
    }


    /**
     * Sets the canGetFolderParent value for this CmisAllowableActionsType.
     * 
     * @param canGetFolderParent
     */
    public void setCanGetFolderParent(java.lang.Boolean canGetFolderParent) {
        this.canGetFolderParent = canGetFolderParent;
    }


    /**
     * Gets the canGetDescendants value for this CmisAllowableActionsType.
     * 
     * @return canGetDescendants
     */
    public java.lang.Boolean getCanGetDescendants() {
        return canGetDescendants;
    }


    /**
     * Sets the canGetDescendants value for this CmisAllowableActionsType.
     * 
     * @param canGetDescendants
     */
    public void setCanGetDescendants(java.lang.Boolean canGetDescendants) {
        this.canGetDescendants = canGetDescendants;
    }


    /**
     * Gets the canMoveObject value for this CmisAllowableActionsType.
     * 
     * @return canMoveObject
     */
    public java.lang.Boolean getCanMoveObject() {
        return canMoveObject;
    }


    /**
     * Sets the canMoveObject value for this CmisAllowableActionsType.
     * 
     * @param canMoveObject
     */
    public void setCanMoveObject(java.lang.Boolean canMoveObject) {
        this.canMoveObject = canMoveObject;
    }


    /**
     * Gets the canDeleteContentStream value for this CmisAllowableActionsType.
     * 
     * @return canDeleteContentStream
     */
    public java.lang.Boolean getCanDeleteContentStream() {
        return canDeleteContentStream;
    }


    /**
     * Sets the canDeleteContentStream value for this CmisAllowableActionsType.
     * 
     * @param canDeleteContentStream
     */
    public void setCanDeleteContentStream(java.lang.Boolean canDeleteContentStream) {
        this.canDeleteContentStream = canDeleteContentStream;
    }


    /**
     * Gets the canCheckOut value for this CmisAllowableActionsType.
     * 
     * @return canCheckOut
     */
    public java.lang.Boolean getCanCheckOut() {
        return canCheckOut;
    }


    /**
     * Sets the canCheckOut value for this CmisAllowableActionsType.
     * 
     * @param canCheckOut
     */
    public void setCanCheckOut(java.lang.Boolean canCheckOut) {
        this.canCheckOut = canCheckOut;
    }


    /**
     * Gets the canCancelCheckOut value for this CmisAllowableActionsType.
     * 
     * @return canCancelCheckOut
     */
    public java.lang.Boolean getCanCancelCheckOut() {
        return canCancelCheckOut;
    }


    /**
     * Sets the canCancelCheckOut value for this CmisAllowableActionsType.
     * 
     * @param canCancelCheckOut
     */
    public void setCanCancelCheckOut(java.lang.Boolean canCancelCheckOut) {
        this.canCancelCheckOut = canCancelCheckOut;
    }


    /**
     * Gets the canCheckIn value for this CmisAllowableActionsType.
     * 
     * @return canCheckIn
     */
    public java.lang.Boolean getCanCheckIn() {
        return canCheckIn;
    }


    /**
     * Sets the canCheckIn value for this CmisAllowableActionsType.
     * 
     * @param canCheckIn
     */
    public void setCanCheckIn(java.lang.Boolean canCheckIn) {
        this.canCheckIn = canCheckIn;
    }


    /**
     * Gets the canSetContentStream value for this CmisAllowableActionsType.
     * 
     * @return canSetContentStream
     */
    public java.lang.Boolean getCanSetContentStream() {
        return canSetContentStream;
    }


    /**
     * Sets the canSetContentStream value for this CmisAllowableActionsType.
     * 
     * @param canSetContentStream
     */
    public void setCanSetContentStream(java.lang.Boolean canSetContentStream) {
        this.canSetContentStream = canSetContentStream;
    }


    /**
     * Gets the canGetAllVersions value for this CmisAllowableActionsType.
     * 
     * @return canGetAllVersions
     */
    public java.lang.Boolean getCanGetAllVersions() {
        return canGetAllVersions;
    }


    /**
     * Sets the canGetAllVersions value for this CmisAllowableActionsType.
     * 
     * @param canGetAllVersions
     */
    public void setCanGetAllVersions(java.lang.Boolean canGetAllVersions) {
        this.canGetAllVersions = canGetAllVersions;
    }


    /**
     * Gets the canAddObjectToFolder value for this CmisAllowableActionsType.
     * 
     * @return canAddObjectToFolder
     */
    public java.lang.Boolean getCanAddObjectToFolder() {
        return canAddObjectToFolder;
    }


    /**
     * Sets the canAddObjectToFolder value for this CmisAllowableActionsType.
     * 
     * @param canAddObjectToFolder
     */
    public void setCanAddObjectToFolder(java.lang.Boolean canAddObjectToFolder) {
        this.canAddObjectToFolder = canAddObjectToFolder;
    }


    /**
     * Gets the canRemoveObjectFromFolder value for this CmisAllowableActionsType.
     * 
     * @return canRemoveObjectFromFolder
     */
    public java.lang.Boolean getCanRemoveObjectFromFolder() {
        return canRemoveObjectFromFolder;
    }


    /**
     * Sets the canRemoveObjectFromFolder value for this CmisAllowableActionsType.
     * 
     * @param canRemoveObjectFromFolder
     */
    public void setCanRemoveObjectFromFolder(java.lang.Boolean canRemoveObjectFromFolder) {
        this.canRemoveObjectFromFolder = canRemoveObjectFromFolder;
    }


    /**
     * Gets the canGetContentStream value for this CmisAllowableActionsType.
     * 
     * @return canGetContentStream
     */
    public java.lang.Boolean getCanGetContentStream() {
        return canGetContentStream;
    }


    /**
     * Sets the canGetContentStream value for this CmisAllowableActionsType.
     * 
     * @param canGetContentStream
     */
    public void setCanGetContentStream(java.lang.Boolean canGetContentStream) {
        this.canGetContentStream = canGetContentStream;
    }


    /**
     * Gets the canApplyPolicy value for this CmisAllowableActionsType.
     * 
     * @return canApplyPolicy
     */
    public java.lang.Boolean getCanApplyPolicy() {
        return canApplyPolicy;
    }


    /**
     * Sets the canApplyPolicy value for this CmisAllowableActionsType.
     * 
     * @param canApplyPolicy
     */
    public void setCanApplyPolicy(java.lang.Boolean canApplyPolicy) {
        this.canApplyPolicy = canApplyPolicy;
    }


    /**
     * Gets the canGetAppliedPolicies value for this CmisAllowableActionsType.
     * 
     * @return canGetAppliedPolicies
     */
    public java.lang.Boolean getCanGetAppliedPolicies() {
        return canGetAppliedPolicies;
    }


    /**
     * Sets the canGetAppliedPolicies value for this CmisAllowableActionsType.
     * 
     * @param canGetAppliedPolicies
     */
    public void setCanGetAppliedPolicies(java.lang.Boolean canGetAppliedPolicies) {
        this.canGetAppliedPolicies = canGetAppliedPolicies;
    }


    /**
     * Gets the canRemovePolicy value for this CmisAllowableActionsType.
     * 
     * @return canRemovePolicy
     */
    public java.lang.Boolean getCanRemovePolicy() {
        return canRemovePolicy;
    }


    /**
     * Sets the canRemovePolicy value for this CmisAllowableActionsType.
     * 
     * @param canRemovePolicy
     */
    public void setCanRemovePolicy(java.lang.Boolean canRemovePolicy) {
        this.canRemovePolicy = canRemovePolicy;
    }


    /**
     * Gets the canGetChildren value for this CmisAllowableActionsType.
     * 
     * @return canGetChildren
     */
    public java.lang.Boolean getCanGetChildren() {
        return canGetChildren;
    }


    /**
     * Sets the canGetChildren value for this CmisAllowableActionsType.
     * 
     * @param canGetChildren
     */
    public void setCanGetChildren(java.lang.Boolean canGetChildren) {
        this.canGetChildren = canGetChildren;
    }


    /**
     * Gets the canCreateDocument value for this CmisAllowableActionsType.
     * 
     * @return canCreateDocument
     */
    public java.lang.Boolean getCanCreateDocument() {
        return canCreateDocument;
    }


    /**
     * Sets the canCreateDocument value for this CmisAllowableActionsType.
     * 
     * @param canCreateDocument
     */
    public void setCanCreateDocument(java.lang.Boolean canCreateDocument) {
        this.canCreateDocument = canCreateDocument;
    }


    /**
     * Gets the canCreateFolder value for this CmisAllowableActionsType.
     * 
     * @return canCreateFolder
     */
    public java.lang.Boolean getCanCreateFolder() {
        return canCreateFolder;
    }


    /**
     * Sets the canCreateFolder value for this CmisAllowableActionsType.
     * 
     * @param canCreateFolder
     */
    public void setCanCreateFolder(java.lang.Boolean canCreateFolder) {
        this.canCreateFolder = canCreateFolder;
    }


    /**
     * Gets the canCreateRelationship value for this CmisAllowableActionsType.
     * 
     * @return canCreateRelationship
     */
    public java.lang.Boolean getCanCreateRelationship() {
        return canCreateRelationship;
    }


    /**
     * Sets the canCreateRelationship value for this CmisAllowableActionsType.
     * 
     * @param canCreateRelationship
     */
    public void setCanCreateRelationship(java.lang.Boolean canCreateRelationship) {
        this.canCreateRelationship = canCreateRelationship;
    }


    /**
     * Gets the canCreatePolicy value for this CmisAllowableActionsType.
     * 
     * @return canCreatePolicy
     */
    public java.lang.Boolean getCanCreatePolicy() {
        return canCreatePolicy;
    }


    /**
     * Sets the canCreatePolicy value for this CmisAllowableActionsType.
     * 
     * @param canCreatePolicy
     */
    public void setCanCreatePolicy(java.lang.Boolean canCreatePolicy) {
        this.canCreatePolicy = canCreatePolicy;
    }


    /**
     * Gets the canDeleteTree value for this CmisAllowableActionsType.
     * 
     * @return canDeleteTree
     */
    public java.lang.Boolean getCanDeleteTree() {
        return canDeleteTree;
    }


    /**
     * Sets the canDeleteTree value for this CmisAllowableActionsType.
     * 
     * @param canDeleteTree
     */
    public void setCanDeleteTree(java.lang.Boolean canDeleteTree) {
        this.canDeleteTree = canDeleteTree;
    }


    /**
     * Gets the canGetRenditions value for this CmisAllowableActionsType.
     * 
     * @return canGetRenditions
     */
    public java.lang.Boolean getCanGetRenditions() {
        return canGetRenditions;
    }


    /**
     * Sets the canGetRenditions value for this CmisAllowableActionsType.
     * 
     * @param canGetRenditions
     */
    public void setCanGetRenditions(java.lang.Boolean canGetRenditions) {
        this.canGetRenditions = canGetRenditions;
    }


    /**
     * Gets the canGetACL value for this CmisAllowableActionsType.
     * 
     * @return canGetACL
     */
    public java.lang.Boolean getCanGetACL() {
        return canGetACL;
    }


    /**
     * Sets the canGetACL value for this CmisAllowableActionsType.
     * 
     * @param canGetACL
     */
    public void setCanGetACL(java.lang.Boolean canGetACL) {
        this.canGetACL = canGetACL;
    }


    /**
     * Gets the canApplyACL value for this CmisAllowableActionsType.
     * 
     * @return canApplyACL
     */
    public java.lang.Boolean getCanApplyACL() {
        return canApplyACL;
    }


    /**
     * Sets the canApplyACL value for this CmisAllowableActionsType.
     * 
     * @param canApplyACL
     */
    public void setCanApplyACL(java.lang.Boolean canApplyACL) {
        this.canApplyACL = canApplyACL;
    }


    /**
     * Gets the _any value for this CmisAllowableActionsType.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this CmisAllowableActionsType.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CmisAllowableActionsType)) return false;
        CmisAllowableActionsType other = (CmisAllowableActionsType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.canDeleteObject==null && other.getCanDeleteObject()==null) || 
             (this.canDeleteObject!=null &&
              this.canDeleteObject.equals(other.getCanDeleteObject()))) &&
            ((this.canUpdateProperties==null && other.getCanUpdateProperties()==null) || 
             (this.canUpdateProperties!=null &&
              this.canUpdateProperties.equals(other.getCanUpdateProperties()))) &&
            ((this.canGetFolderTree==null && other.getCanGetFolderTree()==null) || 
             (this.canGetFolderTree!=null &&
              this.canGetFolderTree.equals(other.getCanGetFolderTree()))) &&
            ((this.canGetProperties==null && other.getCanGetProperties()==null) || 
             (this.canGetProperties!=null &&
              this.canGetProperties.equals(other.getCanGetProperties()))) &&
            ((this.canGetObjectRelationships==null && other.getCanGetObjectRelationships()==null) || 
             (this.canGetObjectRelationships!=null &&
              this.canGetObjectRelationships.equals(other.getCanGetObjectRelationships()))) &&
            ((this.canGetObjectParents==null && other.getCanGetObjectParents()==null) || 
             (this.canGetObjectParents!=null &&
              this.canGetObjectParents.equals(other.getCanGetObjectParents()))) &&
            ((this.canGetFolderParent==null && other.getCanGetFolderParent()==null) || 
             (this.canGetFolderParent!=null &&
              this.canGetFolderParent.equals(other.getCanGetFolderParent()))) &&
            ((this.canGetDescendants==null && other.getCanGetDescendants()==null) || 
             (this.canGetDescendants!=null &&
              this.canGetDescendants.equals(other.getCanGetDescendants()))) &&
            ((this.canMoveObject==null && other.getCanMoveObject()==null) || 
             (this.canMoveObject!=null &&
              this.canMoveObject.equals(other.getCanMoveObject()))) &&
            ((this.canDeleteContentStream==null && other.getCanDeleteContentStream()==null) || 
             (this.canDeleteContentStream!=null &&
              this.canDeleteContentStream.equals(other.getCanDeleteContentStream()))) &&
            ((this.canCheckOut==null && other.getCanCheckOut()==null) || 
             (this.canCheckOut!=null &&
              this.canCheckOut.equals(other.getCanCheckOut()))) &&
            ((this.canCancelCheckOut==null && other.getCanCancelCheckOut()==null) || 
             (this.canCancelCheckOut!=null &&
              this.canCancelCheckOut.equals(other.getCanCancelCheckOut()))) &&
            ((this.canCheckIn==null && other.getCanCheckIn()==null) || 
             (this.canCheckIn!=null &&
              this.canCheckIn.equals(other.getCanCheckIn()))) &&
            ((this.canSetContentStream==null && other.getCanSetContentStream()==null) || 
             (this.canSetContentStream!=null &&
              this.canSetContentStream.equals(other.getCanSetContentStream()))) &&
            ((this.canGetAllVersions==null && other.getCanGetAllVersions()==null) || 
             (this.canGetAllVersions!=null &&
              this.canGetAllVersions.equals(other.getCanGetAllVersions()))) &&
            ((this.canAddObjectToFolder==null && other.getCanAddObjectToFolder()==null) || 
             (this.canAddObjectToFolder!=null &&
              this.canAddObjectToFolder.equals(other.getCanAddObjectToFolder()))) &&
            ((this.canRemoveObjectFromFolder==null && other.getCanRemoveObjectFromFolder()==null) || 
             (this.canRemoveObjectFromFolder!=null &&
              this.canRemoveObjectFromFolder.equals(other.getCanRemoveObjectFromFolder()))) &&
            ((this.canGetContentStream==null && other.getCanGetContentStream()==null) || 
             (this.canGetContentStream!=null &&
              this.canGetContentStream.equals(other.getCanGetContentStream()))) &&
            ((this.canApplyPolicy==null && other.getCanApplyPolicy()==null) || 
             (this.canApplyPolicy!=null &&
              this.canApplyPolicy.equals(other.getCanApplyPolicy()))) &&
            ((this.canGetAppliedPolicies==null && other.getCanGetAppliedPolicies()==null) || 
             (this.canGetAppliedPolicies!=null &&
              this.canGetAppliedPolicies.equals(other.getCanGetAppliedPolicies()))) &&
            ((this.canRemovePolicy==null && other.getCanRemovePolicy()==null) || 
             (this.canRemovePolicy!=null &&
              this.canRemovePolicy.equals(other.getCanRemovePolicy()))) &&
            ((this.canGetChildren==null && other.getCanGetChildren()==null) || 
             (this.canGetChildren!=null &&
              this.canGetChildren.equals(other.getCanGetChildren()))) &&
            ((this.canCreateDocument==null && other.getCanCreateDocument()==null) || 
             (this.canCreateDocument!=null &&
              this.canCreateDocument.equals(other.getCanCreateDocument()))) &&
            ((this.canCreateFolder==null && other.getCanCreateFolder()==null) || 
             (this.canCreateFolder!=null &&
              this.canCreateFolder.equals(other.getCanCreateFolder()))) &&
            ((this.canCreateRelationship==null && other.getCanCreateRelationship()==null) || 
             (this.canCreateRelationship!=null &&
              this.canCreateRelationship.equals(other.getCanCreateRelationship()))) &&
            ((this.canCreatePolicy==null && other.getCanCreatePolicy()==null) || 
             (this.canCreatePolicy!=null &&
              this.canCreatePolicy.equals(other.getCanCreatePolicy()))) &&
            ((this.canDeleteTree==null && other.getCanDeleteTree()==null) || 
             (this.canDeleteTree!=null &&
              this.canDeleteTree.equals(other.getCanDeleteTree()))) &&
            ((this.canGetRenditions==null && other.getCanGetRenditions()==null) || 
             (this.canGetRenditions!=null &&
              this.canGetRenditions.equals(other.getCanGetRenditions()))) &&
            ((this.canGetACL==null && other.getCanGetACL()==null) || 
             (this.canGetACL!=null &&
              this.canGetACL.equals(other.getCanGetACL()))) &&
            ((this.canApplyACL==null && other.getCanApplyACL()==null) || 
             (this.canApplyACL!=null &&
              this.canApplyACL.equals(other.getCanApplyACL()))) &&
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
        if (getCanDeleteObject() != null) {
            _hashCode += getCanDeleteObject().hashCode();
        }
        if (getCanUpdateProperties() != null) {
            _hashCode += getCanUpdateProperties().hashCode();
        }
        if (getCanGetFolderTree() != null) {
            _hashCode += getCanGetFolderTree().hashCode();
        }
        if (getCanGetProperties() != null) {
            _hashCode += getCanGetProperties().hashCode();
        }
        if (getCanGetObjectRelationships() != null) {
            _hashCode += getCanGetObjectRelationships().hashCode();
        }
        if (getCanGetObjectParents() != null) {
            _hashCode += getCanGetObjectParents().hashCode();
        }
        if (getCanGetFolderParent() != null) {
            _hashCode += getCanGetFolderParent().hashCode();
        }
        if (getCanGetDescendants() != null) {
            _hashCode += getCanGetDescendants().hashCode();
        }
        if (getCanMoveObject() != null) {
            _hashCode += getCanMoveObject().hashCode();
        }
        if (getCanDeleteContentStream() != null) {
            _hashCode += getCanDeleteContentStream().hashCode();
        }
        if (getCanCheckOut() != null) {
            _hashCode += getCanCheckOut().hashCode();
        }
        if (getCanCancelCheckOut() != null) {
            _hashCode += getCanCancelCheckOut().hashCode();
        }
        if (getCanCheckIn() != null) {
            _hashCode += getCanCheckIn().hashCode();
        }
        if (getCanSetContentStream() != null) {
            _hashCode += getCanSetContentStream().hashCode();
        }
        if (getCanGetAllVersions() != null) {
            _hashCode += getCanGetAllVersions().hashCode();
        }
        if (getCanAddObjectToFolder() != null) {
            _hashCode += getCanAddObjectToFolder().hashCode();
        }
        if (getCanRemoveObjectFromFolder() != null) {
            _hashCode += getCanRemoveObjectFromFolder().hashCode();
        }
        if (getCanGetContentStream() != null) {
            _hashCode += getCanGetContentStream().hashCode();
        }
        if (getCanApplyPolicy() != null) {
            _hashCode += getCanApplyPolicy().hashCode();
        }
        if (getCanGetAppliedPolicies() != null) {
            _hashCode += getCanGetAppliedPolicies().hashCode();
        }
        if (getCanRemovePolicy() != null) {
            _hashCode += getCanRemovePolicy().hashCode();
        }
        if (getCanGetChildren() != null) {
            _hashCode += getCanGetChildren().hashCode();
        }
        if (getCanCreateDocument() != null) {
            _hashCode += getCanCreateDocument().hashCode();
        }
        if (getCanCreateFolder() != null) {
            _hashCode += getCanCreateFolder().hashCode();
        }
        if (getCanCreateRelationship() != null) {
            _hashCode += getCanCreateRelationship().hashCode();
        }
        if (getCanCreatePolicy() != null) {
            _hashCode += getCanCreatePolicy().hashCode();
        }
        if (getCanDeleteTree() != null) {
            _hashCode += getCanDeleteTree().hashCode();
        }
        if (getCanGetRenditions() != null) {
            _hashCode += getCanGetRenditions().hashCode();
        }
        if (getCanGetACL() != null) {
            _hashCode += getCanGetACL().hashCode();
        }
        if (getCanApplyACL() != null) {
            _hashCode += getCanApplyACL().hashCode();
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
        new org.apache.axis.description.TypeDesc(CmisAllowableActionsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "cmisAllowableActionsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canDeleteObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canDeleteObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canUpdateProperties");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canUpdateProperties"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetFolderTree");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetFolderTree"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetProperties");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetProperties"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetObjectRelationships");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetObjectRelationships"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetObjectParents");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetObjectParents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetFolderParent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetFolderParent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetDescendants");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetDescendants"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canMoveObject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canMoveObject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canDeleteContentStream");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canDeleteContentStream"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canCheckOut");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canCheckOut"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canCancelCheckOut");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canCancelCheckOut"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canCheckIn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canCheckIn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canSetContentStream");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canSetContentStream"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetAllVersions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetAllVersions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canAddObjectToFolder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canAddObjectToFolder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canRemoveObjectFromFolder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canRemoveObjectFromFolder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetContentStream");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetContentStream"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canApplyPolicy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canApplyPolicy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetAppliedPolicies");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetAppliedPolicies"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canRemovePolicy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canRemovePolicy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetChildren");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetChildren"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canCreateDocument");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canCreateDocument"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canCreateFolder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canCreateFolder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canCreateRelationship");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canCreateRelationship"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canCreatePolicy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canCreatePolicy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canDeleteTree");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canDeleteTree"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetRenditions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetRenditions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canGetACL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canGetACL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canApplyACL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://docs.oasis-open.org/ns/cmis/core/200908/", "canApplyACL"));
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
