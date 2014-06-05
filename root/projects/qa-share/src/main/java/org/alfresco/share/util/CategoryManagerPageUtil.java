package org.alfresco.share.util;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.adminconsole.CategoryManagerPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;

/**
 * @author Olga Antonik
 */
public class CategoryManagerPageUtil
{

    /**
     * Open Category Manager page from any share page. Work if you are admin.
     * NOT FOR CLOUD!
     * 
     * @param drone
     * @return
     */
    public static CategoryManagerPage openCategoryManagerPage(WebDrone drone)
    {
        try
        {
            SharePage page = drone.getCurrentPage().render();
            return page.getNav().render().getCategoryManagerPage();
        }
        catch (PageRenderTimeException e)
        {
            throw new PageOperationException("Category Manager Page does not render in time. May be you trying use method not as administrator OR it's bug.", e);
        }
    }

}
