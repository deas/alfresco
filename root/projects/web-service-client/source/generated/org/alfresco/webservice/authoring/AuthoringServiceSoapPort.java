/**
 * AuthoringServiceSoapPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.authoring;

public interface AuthoringServiceSoapPort extends java.rmi.Remote {

    /**
     * Checkout a content resource for editing.
     */
    public org.alfresco.webservice.authoring.CheckoutResult checkout(org.alfresco.webservice.types.Predicate items, org.alfresco.webservice.types.ParentReference destination) throws java.rmi.RemoteException, org.alfresco.webservice.authoring.AuthoringFault;

    /**
     * Checkin a content resource.
     */
    public org.alfresco.webservice.authoring.CheckinResult checkin(org.alfresco.webservice.types.Predicate items, org.alfresco.webservice.types.NamedValue[] comments, boolean keepCheckedOut) throws java.rmi.RemoteException, org.alfresco.webservice.authoring.AuthoringFault;

    /**
     * Checkin an external content resource thus replacing the existing
     * working content.
     */
    public org.alfresco.webservice.types.Reference checkinExternal(org.alfresco.webservice.types.Reference items, org.alfresco.webservice.types.NamedValue[] comments, boolean keepCheckedOut, org.alfresco.webservice.types.ContentFormat format, byte[] content) throws java.rmi.RemoteException, org.alfresco.webservice.authoring.AuthoringFault;

    /**
     * Cancels the checkout.
     */
    public org.alfresco.webservice.authoring.CancelCheckoutResult cancelCheckout(org.alfresco.webservice.types.Predicate items) throws java.rmi.RemoteException, org.alfresco.webservice.authoring.AuthoringFault;

    /**
     * Locks a content resource.
     */
    public org.alfresco.webservice.types.Reference[] lock(org.alfresco.webservice.types.Predicate items, boolean lockChildren, org.alfresco.webservice.authoring.LockTypeEnum lockType) throws java.rmi.RemoteException, org.alfresco.webservice.authoring.AuthoringFault;

    /**
     * Unlocks a content resource.
     */
    public org.alfresco.webservice.types.Reference[] unlock(org.alfresco.webservice.types.Predicate items, boolean unlockChildren) throws java.rmi.RemoteException, org.alfresco.webservice.authoring.AuthoringFault;

    /**
     * Gets the lock status of the specified nodes.
     */
    public org.alfresco.webservice.authoring.LockStatus[] getLockStatus(org.alfresco.webservice.types.Predicate items) throws java.rmi.RemoteException, org.alfresco.webservice.authoring.AuthoringFault;

    /**
     * Create a snapshot of the specified node(s) in the version store.
     */
    public org.alfresco.webservice.authoring.VersionResult createVersion(org.alfresco.webservice.types.Predicate items, org.alfresco.webservice.types.NamedValue[] comments, boolean versionChildren) throws java.rmi.RemoteException, org.alfresco.webservice.authoring.AuthoringFault;

    /**
     * Gets the version history for the specfied node.
     */
    public org.alfresco.webservice.types.VersionHistory getVersionHistory(org.alfresco.webservice.types.Reference node) throws java.rmi.RemoteException, org.alfresco.webservice.authoring.AuthoringFault;

    /**
     * Revert a node to the snapshot of the specified version.
     */
    public void revertVersion(org.alfresco.webservice.types.Reference node, java.lang.String versionLabel) throws java.rmi.RemoteException, org.alfresco.webservice.authoring.AuthoringFault;

    /**
     * Delete all snapshot versions of the specified node.
     */
    public org.alfresco.webservice.types.VersionHistory deleteAllVersions(org.alfresco.webservice.types.Reference node) throws java.rmi.RemoteException, org.alfresco.webservice.authoring.AuthoringFault;
}
