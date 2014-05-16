package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.links.LinksDetailsPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for Site Links dashlet web elements
 *
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class SiteLinksDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_LINKS_DASHLET = "site-links";
    private SiteLinksDashlet siteLinksDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private static final String expectedHelpBallonMsg = "This dashlet shows links relevant to this site. The list is compiled by site members. Clicking a link opens it in a new window.";
    LinksDetailsPage linksDetailsPage = null;

    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "sitelinksdashlettest" + System.currentTimeMillis();
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
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlet.SITE_LINKS, 1).render();
        siteLinksDashlet = siteDashBoard.getDashlet(SITE_LINKS_DASHLET).render();
        assertNotNull(siteLinksDashlet);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="instantiateDashlet")
    public void verifyHelpIcon ()
    {
        siteLinksDashlet.clickOnHelpIcon();
        assertTrue(siteLinksDashlet.isBalloonDisplayed());
        String actualHelpBallonMsg = siteLinksDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
        siteLinksDashlet.closeHelpBallon();
        assertFalse(siteLinksDashlet.isBalloonDisplayed());
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="verifyHelpIcon")
    public void createLinkFromDashlet ()
    {
        linksDetailsPage = siteLinksDashlet.createLink("name", "google.com");
        assertNotNull(linksDetailsPage);
    }
}
