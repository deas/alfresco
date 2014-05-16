package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests for RSS Feed dashlet web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class RssFeedDashletTest extends AbstractSiteDashletTest
{
    private static final String RSS_FEED_DASHLET = "rss-feed";
    private RssFeedDashlet rssFeedDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    RssFeedUrlBoxPage rssFeedUrlBoxPage = null;
    private static final String expectedHelpBallonMsg = "This dashlet shows the latest news from Alfresco Add-ons. Click the edit icon on the dashlet to configure the feed.";

    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "rssfeeddashlettest" + System.currentTimeMillis();
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
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlet.RSS_FEED, 1).render();
        rssFeedDashlet = siteDashBoard.getDashlet(RSS_FEED_DASHLET).render();
        assertNotNull(rssFeedDashlet);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="instantiateDashlet")
    public void verifyHelpIcon ()
    {
        rssFeedDashlet.clickOnHelpIcon();
        assertTrue(rssFeedDashlet.isBalloonDisplayed());
        String actualHelpBallonMsg = rssFeedDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
        rssFeedDashlet.closeHelpBallon();
        assertFalse(rssFeedDashlet.isBalloonDisplayed());
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="verifyHelpIcon")
    public void clickConfigureButton ()
    {
        rssFeedUrlBoxPage = rssFeedDashlet.clickConfigure().render();
        assertNotNull(rssFeedUrlBoxPage);
    }
}
