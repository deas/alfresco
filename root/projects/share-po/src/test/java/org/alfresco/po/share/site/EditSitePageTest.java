package org.alfresco.po.share.site;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.site.EditSitePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EditSitePageTest extends AbstractTest
{
    @Test(groups="unit")
    public void testPage()
    {
        EditSitePage page = new EditSitePage(drone);
        Assert.assertNotNull(page);
    }
}
