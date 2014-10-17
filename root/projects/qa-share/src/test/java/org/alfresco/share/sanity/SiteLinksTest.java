/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.sanity;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.ActivityShareLink;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.links.*;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.alfresco.po.share.dashlet.SiteActivitiesUserFilter.MY_ACTIVITIES;
import static org.alfresco.po.share.enums.UserRole.CONTRIBUTOR;
import static org.alfresco.po.share.site.links.LinksListFilter.FilterOption.*;
import static org.alfresco.po.share.site.links.LinksPage.CheckBoxAction.ALL;
import static org.alfresco.po.share.site.links.LinksPage.CheckBoxAction.INVERT_SELECTION;
import static org.alfresco.po.share.site.links.LinksPage.CheckBoxAction.SELECT_NONE;
import static org.alfresco.po.share.site.links.LinksPage.SelectedAction.DELETE;
import static org.alfresco.po.share.site.links.LinksPage.SelectedAction.DESELECT_ALL;
import static org.testng.Assert.*;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class SiteLinksTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(SiteDiscussionsTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_AONE_8234() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);
        ShareUser.login(drone, testUser);
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(new SitePageType[] { SitePageType.LINKS }));
    }

    @Test(groups = "Sanity")
    public void AONE_8234()
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        String linkName = "Electric tower";
        String linkUrl = "http://electrictower.ru/";
        String linkDescription = testName;
        String tagName = getTagName(testName);
        String commentText = RandomUtil.getRandomString(5);
        String newCommentText = RandomUtil.getRandomString(5);

        ShareUser.login(drone, testUser);
        SiteDashboardPage siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        LinksPage linksPage = siteDashboardPage.getSiteNav().selectLinksPage();

        LinksDetailsPage linksDetailsPage = linksPage.createLink(linkName, linkUrl);
        assertEquals(linksDetailsPage.getUrl(), linkUrl, "Wrong url for created link.");
        assertEquals(linksDetailsPage.getTitle(), linkName, "Wrong link title.");

        linksDetailsPage.editLink(linkUrl, linkName, linkDescription, tagName);
        assertEquals(linksDetailsPage.getUrl(), linkUrl, "Wrong url for created link.");
        assertEquals(linksDetailsPage.getTagName(), tagName, "Tag don't add.");
        assertEquals(linksDetailsPage.getDescription(), linkDescription, "Link description don's changed.");

        linksDetailsPage.clickOnLinkUrl();
        Set<String> setWindowHandles = drone.getWindowHandles();
        String[] windowHandles = setWindowHandles.toArray(new String[setWindowHandles.size()]);
        assertEquals(windowHandles.length, 2, "After clicking on external link new page don't open.");
        drone.switchToWindow(windowHandles[1]);
        drone.closeWindow();
        drone.switchToWindow(windowHandles[0]);

        linksPage = linksDetailsPage.clickOnTag();
        for (int i = 0; i < 1000; i++)
        {
            drone.refresh();
            if (linksPage.getLinksCount() != 0)
            {
                break;
            }
        }
        assertTrue(linksPage.isEditLinkDisplayed(linkName), "After select tag links page don't open.");

        linksDetailsPage = linksPage.clickLink(linkName);
        linksDetailsPage.addComment(commentText);
        LinkComment addedComment = linksDetailsPage.getLinkComment(commentText);
        assertEquals(addedComment.getText(), commentText, "Comment link don't add.");
        assertTrue(addedComment.isCorrect(), "Added comment is broken.");

        linksDetailsPage = addedComment.editComment(newCommentText);
        LinkComment editedComment = linksDetailsPage.getLinkComment(newCommentText);
        assertEquals(editedComment.getText(), newCommentText, "Comment don't edit.");
        assertTrue(editedComment.isCorrect(), "Edited comment is broken.");

        linksDetailsPage = editedComment.deleteComment();
        try
        {
            linksDetailsPage.getLinkComment(newCommentText);
            fail(String.format("Comment[%s] don't delete.", newCommentText));
        }
        catch (PageOperationException e)
        {
            logger.info("Can't found deleted comment. It's OK!");
        }

        linksDetailsPage.deleteLink();
        assertTrue(drone.getCurrentPage().render() instanceof LinksPage, "After link deletion user don't redirect to LinksPage");

        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        SiteActivitiesDashlet siteActivitiesDashlet = siteDashboardPage.getDashlet("site-activities").render();
        siteActivitiesDashlet.selectUserFilter(MY_ACTIVITIES);
        int i = 0;
        for (; i < 1000; i++)
        {
            List<String> descriptions = siteActivitiesDashlet.getSiteActivityDescriptions();
            if (descriptions.size() > 0)
            {
                break;
            }
            else
            {
                siteActivitiesDashlet.selectUserFilter(MY_ACTIVITIES);
            }
        }
        assertTrue(i < 999, "Information about activities with topics don't reflect in site dashlet.");
        DashBoardPage dashBoardPage = ShareUser.openUserDashboard(drone);
        MyActivitiesDashlet myActivitiesDashlet = dashBoardPage.getDashlet("activities").render();
        List<ActivityShareLink> activityShareLinks = myActivitiesDashlet.getActivities();
        assertTrue(activityShareLinks.size() != 0, "Information about activities with topics don't reflect in dashlet.");
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_AONE_8235() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String testUser1 = testUser + 1;
        String testUser2 = testUser + 2;
        String linkWithTag1 = testName + "link1";
        String linkWithTag2 = testName + "link2";
        String linkWithOutTag = testName + "link3";
        String linkUrl = "http://electrictower.ru/";
        String linkFromAnotherUser = testUser2 + "link";
        String tag1 = getTagName(testName) + "1";
        String tag2 = getTagName(testName) + "2";

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        ShareUser.login(drone, testUser1);
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();
        customizeSitePage.addPages(asList(new SitePageType[] { SitePageType.LINKS }));
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser1, testUser2, siteName, CONTRIBUTOR);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        LinksPage linksPage = siteDashboardPage.getSiteNav().selectLinksPage();
        LinksDetailsPage linksDetailsPage = linksPage.createLink(linkWithTag1, linkUrl, tag1);
        linksDetailsPage.browseToLinksList();
        linksDetailsPage = linksPage.createLink(linkWithTag2, linkUrl, tag2);
        linksDetailsPage.browseToLinksList();
        linksPage.createLink(linkWithOutTag, linkUrl);

        ShareUser.login(drone, testUser2);
        siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        linksPage = siteDashboardPage.getSiteNav().selectLinksPage();
        linksPage.createLink(linkFromAnotherUser, linkUrl);
    }

    @Test(groups = "Sanity")
    public void AONE_8235()
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String testUser1 = testUser + 1;
        String testUser2 = testUser + 2;
        String linkWithTag1 = testName + "link1";
        String linkWithTag2 = testName + "link2";
        String linkWithOutTag = testName + "link3";
        String linkNewTitle = testName + "linkNew";
        String linkUrl = "http://electrictower.ru/";
        String linkFromAnotherUser = testUser2 + "link";
        String tag1 = getTagName(testName) + "1";
        String tag2 = getTagName(testName) + "2";

        ShareUser.login(drone, testUser1);
        SiteDashboardPage siteDashboardPage = SiteUtil.openSiteDashboard(drone, siteName);
        LinksPage linksPage = siteDashboardPage.getSiteNav().selectLinksPage();
        LinksListFilter linksListFilter = linksPage.getLinkListFilter();
        linksPage = linksListFilter.select(ALL_LINKS);
        assertEquals(linksPage.getLinksCount(), 4, "Wrong links count with filter ALL.");
        linksListFilter.select(MY_LINKS);
        assertEquals(linksPage.getLinksCount(), 3, "Wrong links count with filter MY.");
        linksListFilter.select(RECENTLY_ADDED);
        assertEquals(linksPage.getLinksCount(), 4, "Wrong links count with filter RECENT.");
        linksListFilter.clickOnTag(tag1);
        assertEquals(linksPage.getLinksCount(), 1, "Wrong links count displayed when tag1 choice.");
        assertTrue(linksPage.isEditLinkDisplayed(linkWithTag1), "Links with tag1 don't displayed.");
        linksListFilter.clickOnTag(tag2);
        assertEquals(linksPage.getLinksCount(), 1, "Wrong links count displayed when tag2 choice.");
        assertTrue(linksPage.isEditLinkDisplayed(linkWithTag2), "Links with tag2 don't displayed.");
        LinkDirectoryInfo linkDirectoryInfo = linksPage.getLinkDirectoryInfo(linkWithTag2);
        linkDirectoryInfo.clickOnTag(tag2);
        assertEquals(linksPage.getLinksCount(), 1, "Wrong links count displayed when tag2 choice.");
        RssFeedPage rssFeedPage = linksPage.selectRssFeed(testUser1, DEFAULT_PASSWORD);
        assertTrue(rssFeedPage.isSubscribePanelDisplay(), "Subscribe panel don't displayed in browser.");
        LinksDetailsPage linksDetailsPage = rssFeedPage.clickOnFeedContent(linkWithTag2).render();
        linksPage = linksDetailsPage.browseToLinksList();
        linksDetailsPage = linksPage.editLink(linkFromAnotherUser, linkNewTitle, linkUrl, linkNewTitle, false);
        linksPage = linksDetailsPage.browseToLinksList();
        assertTrue(linksPage.isEditLinkDisplayed(linkNewTitle), "Edited link don't found");
        linksPage = linksPage.deleteLinkWithConfirm(linkNewTitle);
        try
        {
            linksPage.isDeleteLinkDisplayed(linkNewTitle);
            fail("Message found after deleting.");
        }
        catch (PageException e)
        {
            logger.info("Link don't found on page. It's Ok.");
        }
        linksPage.selectAction(ALL);
        assertEquals(linksPage.getLinksCount(), 3, "Wrong links count displayed.");
        assertTrue(linksPage.getLinkDirectoryInfo(linkWithTag1).isSelected(), "Don't all links selected. 1");
        assertTrue(linksPage.getLinkDirectoryInfo(linkWithTag2).isSelected(), "Don't all links selected. 2");
        assertTrue(linksPage.getLinkDirectoryInfo(linkWithOutTag).isSelected(), "Don't all links selected. 3");
        linksPage.selectAction(SELECT_NONE);
        assertEquals(linksPage.getLinksCount(), 3, "Wrong links count displayed.");
        assertFalse(linksPage.getLinkDirectoryInfo(linkWithTag1).isSelected(), "Don't all links deselected. 1");
        assertFalse(linksPage.getLinkDirectoryInfo(linkWithTag2).isSelected(), "Don't all links deselected. 2");
        assertFalse(linksPage.getLinkDirectoryInfo(linkWithOutTag).isSelected(), "Don't all links deselected. 3");
        linksPage.getLinkDirectoryInfo(linkWithOutTag).clickOnCheckBox();
        linksPage.selectAction(INVERT_SELECTION);
        assertEquals(linksPage.getLinksCount(), 3, "Wrong links count displayed.");
        assertTrue(linksPage.getLinkDirectoryInfo(linkWithTag1).isSelected(), "Don't all links selected. 1");
        assertTrue(linksPage.getLinkDirectoryInfo(linkWithTag2).isSelected(), "Don't all links selected. 2");
        assertFalse(linksPage.getLinkDirectoryInfo(linkWithOutTag).isSelected(), "Don't link deselected. 3");
        linksPage.selectedItemsAction(DELETE);
        assertEquals(linksPage.getLinksCount(), 1, "Selected links don't deleted.");
        linksPage.getLinkDirectoryInfo(linkWithOutTag).clickOnCheckBox();
        linksPage.selectedItemsAction(DESELECT_ALL);
        assertFalse(linksPage.getLinkDirectoryInfo(linkWithOutTag).isSelected(), "DESELECT ALL don't work.");
    }


}
