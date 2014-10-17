package org.alfresco.share.util;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains the utils for Application page of Admin Console
 *
 * @author Antonik Olga
 */
public class ApplicationPageUtil {

    private static final Log logger = LogFactory.getLog(ApplicationPageUtil.class);

    /**
     * Open Application page from any share page. Admin user must be logged in
     *
     *
     * @param drone
     * @return
     */
    public static AdminConsolePage openApplicationPage(WebDrone drone)
    {
        try
        {
            SharePage page = drone.getCurrentPage().render();
            return page.getNav().getAdminConsolePage();
        }
        catch (PageRenderTimeException e)
        {
            throw new PageOperationException("Application page does not render in time", e);
        }
    }


}
