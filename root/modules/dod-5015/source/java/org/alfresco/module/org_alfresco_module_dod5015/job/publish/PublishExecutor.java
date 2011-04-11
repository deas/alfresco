/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.job.publish;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * @author Roy Wetherall
 */
public interface PublishExecutor
{
    String getName();
    
    void publish(NodeRef nodeRef);
}
