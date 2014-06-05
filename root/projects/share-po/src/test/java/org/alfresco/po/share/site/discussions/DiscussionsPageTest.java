package org.alfresco.po.share.site.discussions;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.dashlet.mydiscussions.TopicsListPage;
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
@Test(groups = { "Enterprise-only" })
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

    @Test
    public void addDiscussionsPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.DISCUSSIONS);
        customizeSitePage.addPages(addPageTypes);
        discussionsPage = siteDashBoard.getSiteNav().selectDiscussionsPage().render();
        assertNotNull(discussionsPage);
    }

    @Test(dependsOnMethods = "addDiscussionsPage")
    public void createTopic()
    {
        assertTrue(discussionsPage.isNewTopicEnabled());
        topicViewPage = discussionsPage.createTopic(text).render();
        assertNotNull(topicViewPage.render());
        assertEquals(verifyCreatedTopic(), text);
    }

    @Test(dependsOnMethods = "createTopic")
    public void viewTopic()
    {
        topicViewPage.clickBack();
        assertNotNull(discussionsPage);
        topicViewPage = discussionsPage.viewTopic(text).render();
        assertNotNull(topicViewPage);
    }

    @Test(dependsOnMethods = "viewTopic")
    public void createReply()
    {
        assertTrue(topicViewPage.isReplyLinkDisplayed());
        topicViewPage.createReply(text).render();
        assertEquals(verifyCreatedReply(), text);
    }

    @Test(dependsOnMethods = "viewTopic")
    public void editTopic()
    {
        discussionsPage = topicViewPage.clickBack().render();
        topicViewPage = discussionsPage.editTopic(text, editedText, textLines).render();
        assertEquals(editedText, verifyCreatedTopic());
    }

    @Test(dependsOnMethods = "createReply")
    public void editReply ()
    {
        topicViewPage.editReply(text, editedText).render();
        assertEquals(verifyCreatedReply(), editedText);
    }

    @Test(dependsOnMethods = "editReply")
    public void deleteTopic ()
    {
        discussionsPage = topicViewPage.clickBack().render();
        int expNum = discussionsPage.getTopicCount()-1;
        discussionsPage.deleteTopicWithConfirm(editedText).render();
        assertEquals(discussionsPage.getTopicCount(), expNum);
    }

    private String verifyCreatedTopic()
    {
        try
        {
            return drone.findAndWait(By.cssSelector(".nodeTitle>a")).getText();
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
            return drone.findAndWait(By.cssSelector("div[class='reply']>.nodeContent>div[class*='content']>p")).getText();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find the reply");
        }
    }
}
