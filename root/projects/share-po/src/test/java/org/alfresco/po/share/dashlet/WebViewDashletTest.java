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
 * Tests for Web View dashlet web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class WebViewDashletTest extends AbstractSiteDashletTest
{
    private static final String WEB_VIEW_DASHLET = "web-view";
    private WebViewDashlet webViewDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private ConfigureWebViewDashletBoxPage configureWebViewDashletBoxPage = null;
    private static final String expectedHelpBallonMsg = "This dashlet shows the website of your choice. Click the edit icon on the dashlet to change the web address.\n"
        + "Clicking the dashlet title opens the website in a separate window.";

    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "webviewdashlettest" + System.currentTimeMillis();
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
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlet.WEB_VIEW, 1).render();
        webViewDashlet = siteDashBoard.getDashlet(WEB_VIEW_DASHLET).render();
        assertNotNull(webViewDashlet);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="instantiateDashlet")
    public void verifyHelpIcon ()
    {
        webViewDashlet.clickOnHelpIcon();
        assertTrue(webViewDashlet.isBalloonDisplayed());
        String actualHelpBallonMsg = webViewDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
        webViewDashlet.closeHelpBallon();
        assertFalse(webViewDashlet.isBalloonDisplayed());
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="instantiateDashlet")
    public void checkConfigureIcon ()
    {
        configureWebViewDashletBoxPage = webViewDashlet.clickConfigure();
        assertNotNull(configureWebViewDashletBoxPage);
    }
}
