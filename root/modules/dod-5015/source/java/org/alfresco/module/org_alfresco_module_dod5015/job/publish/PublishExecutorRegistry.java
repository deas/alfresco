/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.job.publish;

import java.util.HashMap;
import java.util.Map;

/**
 * Publish executor register
 * @author Roy Wetherall
 */
public class PublishExecutorRegistry
{
    private Map<String, PublishExecutor> publishExectors = new HashMap<String, PublishExecutor>(3);
    
    public void register(PublishExecutor publishExecutor)
    {
        publishExectors.put(publishExecutor.getName(), publishExecutor);
    }
    
    public PublishExecutor get(String name)
    {
        return publishExectors.get(name);
    }
}
