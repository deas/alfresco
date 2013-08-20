package org.alfresco.module.vti.management;

/**
 * Management interface for Sharepoint VTI module
 * 
 * package org.alfresco.module.vti.management;
 * @author mrogers
 *
 */

public interface SPP
{
    /**
     * Is SPP/VTI/Sharepoint enabled
     * @return
     */
    boolean isEnabled();
    
    String getHost();
    
    int getPort();

}
