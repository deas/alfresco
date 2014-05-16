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
 * Tests for Wiki dashlet web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class WikiDashletTest extends AbstractSiteDashletTest
{
    private static final String WIKI_DASHLET = "wiki";
    private WikiDashlet wikiDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private SelectWikiDialogueBoxPage selectWikiDialogueBoxPage = null;
    @SuppressWarnings("unused")
    private ConfigureWebViewDashletBoxPage configureWebViewDashletBoxPage = null;
    private static final String expectedHelpBallonMsg = "This dashlet shows a page selected from the site's wiki.\n"
        + "Navigate to the wiki to see all related content.";

    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "wikidashlettest" + System.currentTimeMillis();
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
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlet.WIKI, 1).render();
        wikiDashlet = siteDashBoard.getDashlet(WIKI_DASHLET).render();
        assertNotNull(wikiDashlet);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="instantiateDashlet")
    public void verifyHelpIcon ()
    {
        wikiDashlet.clickOnHelpIcon();
        assertTrue(wikiDashlet.isBalloonDisplayed());
        String actualHelpBallonMsg = wikiDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBallonMsg, expectedHelpBallonMsg);
        wikiDashlet.closeHelpBallon();
        assertFalse(wikiDashlet.isBalloonDisplayed());
    }

    @Test(groups = "Enterprise-only", dependsOnMethods="instantiateDashlet")
    public void verifyConfigureIcon ()
    {
        selectWikiDialogueBoxPage = wikiDashlet.clickConfigure();
        assertNotNull(selectWikiDialogueBoxPage);
    }
}
