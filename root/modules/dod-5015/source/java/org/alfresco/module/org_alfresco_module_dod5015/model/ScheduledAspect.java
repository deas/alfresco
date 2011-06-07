/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.model;

import org.alfresco.module.org_alfresco_module_dod5015.disposition.DispositionService;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * Behaviour associated with the scheduled aspect
 * 
 * @author Roy Wetherall
 */
public class ScheduledAspect implements RecordsManagementModel,
                                        NodeServicePolicies.OnAddAspectPolicy
{
    /** Policy component */
    private PolicyComponent policyComponent;
    
    private DispositionService dispositionService;
    
    /** Node service */
    private NodeService nodeService;
    
    /**
     * Set the policy component
     * @param policyComponent   policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    public void setDispositionService(DispositionService dispositionService)
    {
        this.dispositionService = dispositionService;
    }
    
    /**
     * Set node service
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Bean initialisation method
     */
    public void init()
    {
        policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnAddAspectPolicy.QNAME,
                ASPECT_SCHEDULED,
                new JavaBehaviour(this, "onAddAspect", NotificationFrequency.TRANSACTION_COMMIT));
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy#onAddAspect(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName)
     */
    @Override
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (nodeService.exists(nodeRef) == true && 
            dispositionService.getAssociatedDispositionSchedule(nodeRef) == null)
        {
           dispositionService.createDispositionSchedule(nodeRef, null);           
        }
    }
}
