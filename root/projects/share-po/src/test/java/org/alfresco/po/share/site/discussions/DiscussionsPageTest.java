package org.alfresco.po.share.site.discussions;

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
 * Holds tests for Discussion page web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class DiscussionsPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    DiscussionsPage discussionsPage = null;
    TopicViewPage topicViewPage = null;
    String text = getClass().getSimpleName();
    String editedText = text + "edited";
    String textLines = "This is a topic";

    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "discussions" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(groups = "Enterprise-only")
    public void addDiscussionsPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.DISCUSSIONS);
        customizeSitePage.addPages(addPageTypes);
        discussionsPage = siteDashBoard.getSiteNav().selectDiscussionsPage();
        assertNotNull(discussionsPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "addDiscussionsPage")
    public void createTopic()
    {
        assertTrue(discussionsPage.isNewTopicEnabled());
        topicViewPage = discussionsPage.createTopic(text).render();
        assertNotNull(topicViewPage.render());
        assertEquals(verifyCreatedTopic(), text);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "createTopic")
    public void viewTopic()
    {
        topicViewPage.clickBack();
        assertNotNull(discussionsPage);
        topicViewPage = discussionsPage.viewTopic(text);
        assertNotNull(topicViewPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "viewTopic")
    public void createReply()
    {
        assertTrue(topicViewPage.isReplyLinkDisplayed());
        topicViewPage.createReply(text);
        assertEquals(verifyCreatedReply(), text);
    }

    private String verifyCreatedTopic()
    {
        try
        {
            return drone.find(By.cssSelector(".nodeTitle>a")).getText();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find the topic");
        }
    }

    private String verifyCreatedReply()
    {
        try
        {
            return drone.find(By.cssSelector("div[class='reply']>.nodeContent>div[class*='content']>p")).getText();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find the reply");
        }
    }
}
