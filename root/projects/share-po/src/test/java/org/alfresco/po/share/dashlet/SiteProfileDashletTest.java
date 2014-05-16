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
 * Tests for Site Links dashlet web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class SiteProfileDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_PROFILE_DASHLET = "site-profile";
    private SiteProfileDashlet siteProfileDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private static final String expectedHelpBallonMsg = "This dashlet displays the site details. Only the site manager can change this information.";
    private static final String exprectedContent = "Welcome to %s" + "\n"
    + "\n"
    + "%s\n"
    + "Site Manager(s): Administrator\n"
    + "Visibility: %s";

    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "siteprofiledashlettest" + System.currentTimeMillis();
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
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlet.SITE_PROFILE, 1).render();
        siteProfileDashlet = siteDashBoard.getDashlet(SITE_PROFILE_DASHLET).render();
        assertNotNull(siteProfileDashlet);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="instantiateDashlet")
    public void verifyHelpIcon ()
    {
        siteProfileDashlet.clickOnHelpIcon();
        assertTrue(siteProfileDashlet.isBalloonDisplayed());
        String actualHelpBallonMsg = siteProfileDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
        siteProfileDashlet.closeHelpBallon();
        assertFalse(siteProfileDashlet.isBalloonDisplayed());
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="verifyHelpIcon")
    public void getContent ()
    {
        String actualContent = siteProfileDashlet.getContent();
        assertEquals(actualContent, (String.format (exprectedContent, siteName, "description", "Public")));
    }
}
