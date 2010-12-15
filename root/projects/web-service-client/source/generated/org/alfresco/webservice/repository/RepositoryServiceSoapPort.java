/**
 * RepositoryServiceSoapPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.repository;

public interface RepositoryServiceSoapPort extends java.rmi.Remote {

    /**
     * Creates a new repository store.
     */
    public org.alfresco.webservice.types.Store createStore(java.lang.String scheme, java.lang.String address) throws java.rmi.RemoteException, org.alfresco.webservice.repository.RepositoryFault;

    /**
     * Retrieves a list of stores where content resources are held.
     */
    public org.alfresco.webservice.types.Store[] getStores() throws java.rmi.RemoteException, org.alfresco.webservice.repository.RepositoryFault;

    /**
     * Executes a query against a store.
     */
    public org.alfresco.webservice.repository.QueryResult query(org.alfresco.webservice.types.Store store, org.alfresco.webservice.types.Query query, boolean includeMetaData) throws java.rmi.RemoteException, org.alfresco.webservice.repository.RepositoryFault;

    /**
     * Executes a query to retrieve the children of the specified
     * resource.
     */
    public org.alfresco.webservice.repository.QueryResult queryChildren(org.alfresco.webservice.types.Reference node) throws java.rmi.RemoteException, org.alfresco.webservice.repository.RepositoryFault;

    /**
     * Executes a query to retrieve the parents of the specified resource.
     */
    public org.alfresco.webservice.repository.QueryResult queryParents(org.alfresco.webservice.types.Reference node) throws java.rmi.RemoteException, org.alfresco.webservice.repository.RepositoryFault;

    /**
     * Executes a query to retrieve associated resources of the specified
     * resource.
     */
    public org.alfresco.webservice.repository.QueryResult queryAssociated(org.alfresco.webservice.types.Reference node, org.alfresco.webservice.repository.Association association) throws java.rmi.RemoteException, org.alfresco.webservice.repository.RepositoryFault;

    /**
     * Fetches the next batch of query results.
     */
    public org.alfresco.webservice.repository.QueryResult fetchMore(java.lang.String querySession) throws java.rmi.RemoteException, org.alfresco.webservice.repository.RepositoryFault;

    /**
     * Executes a CML script to manipulate the contents of a Repository
     * store.
     */
    public org.alfresco.webservice.repository.UpdateResult[] update(org.alfresco.webservice.types.CML statements) throws java.rmi.RemoteException, org.alfresco.webservice.repository.RepositoryFault;

    /**
     * Describes a content resource.
     */
    public org.alfresco.webservice.types.NodeDefinition[] describe(org.alfresco.webservice.types.Predicate items) throws java.rmi.RemoteException, org.alfresco.webservice.repository.RepositoryFault;

    /**
     * Gets a resource from the repository.
     */
    public org.alfresco.webservice.types.Node[] get(org.alfresco.webservice.types.Predicate where) throws java.rmi.RemoteException, org.alfresco.webservice.repository.RepositoryFault;
}
