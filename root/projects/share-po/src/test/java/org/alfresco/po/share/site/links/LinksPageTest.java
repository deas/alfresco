package org.alfresco.po.share.site.links;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Holds tests for Links page web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class LinksPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    LinksPage linksPage = null;
    LinksDetailsPage linksDetailsPage = null;
    AddLinkForm addLinkForm = null;
    String text = getClass().getSimpleName();
    String editedText = text + "edited";
    String url = "www.alfresco.com";

    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "links" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(groups = "Enterprise-only")
    public void addLinksPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.LINKS);
        customizeSitePage.addPages(addPageTypes);
        linksPage = siteDashBoard.getSiteNav().selectLinksPage();
        assertNotNull(linksPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "addLinksPage")
    public void createLink()
    {
        assertTrue(linksPage.isCreateLinkEnabled());
        addLinkForm = linksPage.clickNewLink();
        assertNotNull(addLinkForm);
        linksDetailsPage = linksPage.createLink(text, url);
        assertEquals(getLinkTitle(), text);
        assertNotNull(linksDetailsPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "createLink")
    public void editLink()
    {
        linksPage = linksDetailsPage.browseToLinksList();
        linksPage.editLink(text, editedText, editedText, editedText, true);
        assertEquals(getLinkTitle(), editedText);
        assertNotNull(linksDetailsPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "editLink")
    public void deleteLink()
    {
        linksPage = linksDetailsPage.browseToLinksList();
        int expNum = linksPage.getLinksCount()-1;
        linksPage.deleteLinkWithConfirm(editedText);
        assertEquals(linksPage.getLinksCount(), expNum);
    }

    private String getLinkTitle()
    {
        try
        {
            return drone.find(By.cssSelector(".nodeTitle>a")).getText();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find the link");
        }
    }
}
