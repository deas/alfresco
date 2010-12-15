/**
 * ActionServiceSoapPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.action;

public interface ActionServiceSoapPort extends java.rmi.Remote {

    /**
     * Gets the available condition definitions.
     */
    public org.alfresco.webservice.action.ActionItemDefinition[] getConditionDefinitions() throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;

    /**
     * Gets the available action definitions.
     */
    public org.alfresco.webservice.action.ActionItemDefinition[] getActionDefinitions() throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;

    /**
     * Get a named action item definition.
     */
    public org.alfresco.webservice.action.ActionItemDefinition getActionItemDefinition(java.lang.String name, org.alfresco.webservice.action.ActionItemDefinitionType definitionType) throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;

    /**
     * Gets the availble action types.
     */
    public org.alfresco.webservice.action.RuleType[] getRuleTypes() throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;

    /**
     * Get a named rule type.
     */
    public org.alfresco.webservice.action.RuleType getRuleType(java.lang.String name) throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;

    /**
     * Gets the actions saved against a reference.
     */
    public org.alfresco.webservice.action.Action[] getActions(org.alfresco.webservice.types.Reference reference, org.alfresco.webservice.action.ActionFilter filter) throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;

    /**
     * Save actions against a given reference.
     */
    public org.alfresco.webservice.action.Action[] saveActions(org.alfresco.webservice.types.Reference reference, org.alfresco.webservice.action.Action[] actions) throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;

    /**
     * Removes saved actions.
     */
    public void removeActions(org.alfresco.webservice.types.Reference reference, org.alfresco.webservice.action.Action[] actions) throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;

    /**
     * Executes actions.
     */
    public org.alfresco.webservice.action.ActionExecutionResult[] executeActions(org.alfresco.webservice.types.Predicate predicate, org.alfresco.webservice.action.Action[] actions) throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;

    /**
     * Gets the rules for a reference.
     */
    public org.alfresco.webservice.action.Rule[] getRules(org.alfresco.webservice.types.Reference reference, org.alfresco.webservice.action.RuleFilter ruleFilter) throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;

    /**
     * Save rules.
     */
    public org.alfresco.webservice.action.Rule[] saveRules(org.alfresco.webservice.types.Reference reference, org.alfresco.webservice.action.Rule[] rules) throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;

    /**
     * Remove saved rules.
     */
    public void removeRules(org.alfresco.webservice.types.Reference reference, org.alfresco.webservice.action.Rule[] rules) throws java.rmi.RemoteException, org.alfresco.webservice.action.ActionFault;
}
