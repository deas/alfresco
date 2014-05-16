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
 * Tests for Site Calendar dashlet web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class SiteCalendarDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_CALENDAR_DASHLET = "site-calendar";
    private SiteCalendarDashlet siteCalendarDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private static final String expectedHelpBallonMsg = "This dashlet shows the upcoming events for this site.";

    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "sitecalendardashlettest" + System.currentTimeMillis();
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
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlet.SITE_CALENDAR, 2).render();
        siteCalendarDashlet = siteDashBoard.getDashlet(SITE_CALENDAR_DASHLET).render();
        assertNotNull(siteCalendarDashlet);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="instantiateDashlet")
    public void verifyHelpIcon ()
    {
        siteCalendarDashlet.clickOnHelpIcon();
        assertTrue(siteCalendarDashlet.isBalloonDisplayed());
        String actualHelpBallonMsg = siteCalendarDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
        siteCalendarDashlet.closeHelpBallon();
        assertFalse(siteCalendarDashlet.isBalloonDisplayed());
    }
}
