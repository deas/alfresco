/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.identifier;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Records management identifier service
 * 
 * @author Roy Wetherall
 */
public interface IdentifierService
{
    /** Context value names */
    public static final String CONTEXT_NODEREF = "noderef";
    public static final String CONTEXT_PARENT_NODEREF = "parentndoeref";
    public static final String CONTEXT_ORIG_TYPE = "origionaltype";  
    
    /**
     * Register an identifier generator implementation with the service.
     * 
     * @param identifierGenerator   identifier generator implementation
     */
    void register(IdentifierGenerator identifierGenerator);
    
    /**
     * Generate an identifier for a node with the given type and parent.
     * 
     * @param type      type of the node
     * @param parent    parent of the ndoe
     * @return String   generated identifier
     */
    String generateIdentifier(QName type, NodeRef parent);
    
    /**
     * Generate an identifier for the given node.
     * 
     * @param nodeRef   node reference
     * @return String   generated identifier
     */
    String generateIdentifier(NodeRef nodeRef);
}
