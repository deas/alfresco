package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests for Site Calendar dashlet web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2", "Enterprise-only" })
public class SiteCalendarDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_CALENDAR_DASHLET = "site-calendar";
    private SiteCalendarDashlet siteCalendarDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows the upcoming events for this site.";

    @BeforeClass
    public void setUp() throws Exception
    {
        siteName = "siteCalendarDashletTest" + System.currentTimeMillis();
        loginAs("admin", "admin");
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.SITE_CALENDAR, 2).render();
        siteCalendarDashlet = siteDashBoard.getDashlet(SITE_CALENDAR_DASHLET).render();
        assertNotNull(siteCalendarDashlet);
    }

    @Test(dependsOnMethods = "instantiateDashlet")
    public void verifyHelpIcon()
    {
        siteCalendarDashlet.clickOnHelpIcon();
        assertTrue(siteCalendarDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = siteCalendarDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        siteCalendarDashlet.closeHelpBallon();
        assertFalse(siteCalendarDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void verifyEventsCount()
    {
        assertEquals(siteCalendarDashlet.getEventsCount(), 0);
    }

    @Test(dependsOnMethods = "verifyEventsCount")
    public void verifyIsEventDisplayed()
    {
        assertFalse(siteCalendarDashlet.isEventsDisplayed("gogno-1235456"));
    }

}
