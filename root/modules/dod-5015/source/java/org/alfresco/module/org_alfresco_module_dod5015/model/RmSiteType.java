/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.model;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;

/**
 * Behaviour associated with the RM Site type
 * 
 * @author Roy Wetherall
 */
public class RmSiteType implements RecordsManagementModel,
                                   NodeServicePolicies.OnCreateNodePolicy
{
	/** Constant values */
	public static final String COMPONENT_DOCUMENT_LIBRARY = "documentLibrary";
    public static final String DEFAULT_SITE_NAME = "rm";
	
    /** Policy component */
    private PolicyComponent policyComponent;
    
    /** Site service */
    private SiteService siteService;
    
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
    
    /**
     * Set the site service
     * @param siteService	site service
     */
    public void setSiteService(SiteService siteService) 
    {
		this.siteService = siteService;
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
                NodeServicePolicies.OnCreateNodePolicy.QNAME,
                TYPE_RM_SITE,
                new JavaBehaviour(this, "onCreateNode", NotificationFrequency.FIRST_EVENT));
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy#onCreateNode(org.alfresco.service.cmr.repository.ChildAssociationRef)
     */
	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) 
	{
		final NodeRef rmSite = childAssocRef.getChildRef();
        
        // Do not execute behaviour if this has been created in the archive store
        if(rmSite.getStoreRef().equals(StoreRef.STORE_REF_ARCHIVE_SPACESSTORE) == true)
        {
            // This is not the spaces store - probably the archive store
            return;
        }
        
        if (nodeService.exists(rmSite) == true)
        {
            AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>()
            {
                public Object doWork()
                {
                	SiteInfo siteInfo = siteService.getSite(rmSite);
                	if (siteInfo != null)
                	{	                
	                	// Create the file plan component
	                	siteService.createContainer(siteInfo.getShortName(), COMPONENT_DOCUMENT_LIBRARY, DOD5015Model.TYPE_FILE_PLAN, null);
                	}
                    return null;
                }
            }, AuthenticationUtil.getAdminUserName());
        }
	}
}
