package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.datalist.NewListForm;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for Data Lists dashlet web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class SiteDataListsDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_DATA_LISTS_DASHLET = "data-lists";
    private SiteDataListsDashlet siteDataListsDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private NewListForm newListForm = null;
    @SuppressWarnings("unused")
    private DataListPage dataListPage = null;
    private static final String expectedHelpBallonMsg = "This dashlet shows lists relevant to the site. Clicking a list opens it.";

    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "sitedatalistsdashlettest" + System.currentTimeMillis();
    }

    @BeforeClass
    public void setUp() throws Exception
    {
        loginAs("admin", "admin");
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test(groups = "Enterprise-only")
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlet.SITE_DATA_LISTS, 1).render();
        siteDataListsDashlet = siteDashBoard.getDashlet(SITE_DATA_LISTS_DASHLET).render();
        assertNotNull(siteDataListsDashlet);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="instantiateDashlet")
    public void verifyHelpIcon ()
    {
        siteDataListsDashlet.clickOnHelpIcon();
        assertTrue(siteDataListsDashlet.isBalloonDisplayed());
        String actualHelpBallonMsg = siteDataListsDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
        siteDataListsDashlet.closeHelpBallon();
        assertFalse(siteDataListsDashlet.isBalloonDisplayed());
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="verifyHelpIcon")
    public void verifyCreateDataList ()
    {
        assertTrue(siteDataListsDashlet.isCreateDataListDisplayed());
        newListForm = siteDataListsDashlet.clickCreateDataList();
        assertNotNull(newListForm);
        newListForm.clickClose();
    }
}
