/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.dashlet;

import static org.alfresco.po.share.dashlet.SiteActivitiesHistoryFilter.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test my activities dashlet page elements.
 * 
 * @author Michael Suzuki
 * @since 1.3
 */
@Listeners(FailedTestListener.class)
public class MyActivitiesDashletTest extends AbstractDashletTest
{
    private static Log logger = LogFactory.getLog(MyActivitiesDashletTest.class);

    @BeforeClass(groups = "alfresco-one")
    public void setup() throws Exception
    {
        siteName = "MyActDashletTests" + System.currentTimeMillis();
        uploadDocument();
        AlfrescoVersion version = drone.getProperties().getVersion();
        if (version.isCloud())
        {
            firstName = anotherUser.getfName();
            lastName = anotherUser.getlName();
        }
    }

    @AfterClass(groups = "alfresco-one")
    public void deleteSite()
    {
        try
        {
            SiteUtil.deleteSite(drone, siteName);
        }
        catch (Exception e)
        {
            logger.error("tear down was unable to delete site", e);
        }

    }

    @Test(groups = "alfresco-one")
    public void instantiateMyActivitiesDashlet()
    {
        MyActivitiesDashlet dashlet = new MyActivitiesDashlet(drone);
        Assert.assertNotNull(dashlet);
    }

    @Test(dependsOnMethods = "instantiateMyActivitiesDashlet", groups = "alfresco-one", expectedExceptions = PageException.class)
    public void selectFake() throws Exception
    {
        MyActivitiesDashlet dashlet = dashBoard.getDashlet("activities").render();
        dashlet.selectActivityDocument("bla");
    }

    /**
     * Test process of accessing my documents
     * dashlet from the dash board view.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "selectFake", groups = "alfresco-one")
    public void selectMyActivityDashlet() throws Exception
    {
        MyActivitiesDashlet dashlet = dashBoard.getDashlet("activities").render();
        final String title = dashlet.getDashletTitle();
        Assert.assertEquals("My Activities", title);
    }

    @Test(dependsOnMethods = "selectMyActivityDashlet", groups = "alfresco-one")
    public void selectActivity() throws Exception
    {
        DocumentDetailsPage page = null;
        ActivityShareLink active = null;
        // This dashlet should not take over a minute to display the target activity.
        long minimunRenderTime = 60000;
        long renderTime = popupRendertime;
        if (renderTime < minimunRenderTime)
        {
            renderTime = minimunRenderTime;
        }
        RenderTime timer = new RenderTime(renderTime);
        while (true)
        {
            timer.start();
            try
            {
                drone.refresh();
                MyActivitiesDashlet dashlet = dashBoard.getDashlet("activities").render();
                active = dashlet.selectLink(fileName);
                break;
            }
            catch (PageException e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        page = active.getDocument().click().render();
        Assert.assertNotNull(page);
        Assert.assertEquals(true, page.isDocumentDetailsPage());
    }

    @Test(groups = "alfresco-one", dependsOnMethods = "selectActivity")
    public void getActivities() throws IOException
    {
        List<ActivityShareLink> activities;
        drone.navigateTo(shareUrl);
        dashBoard = drone.getCurrentPage().render();
        RenderTime timer = new RenderTime(60000);
        while (true)
        {
            timer.start();
            try
            {
                drone.refresh();
                MyActivitiesDashlet dashlet = dashBoard.getDashlet("activities").render();
                activities = dashlet.getActivities();
                if (!activities.isEmpty())
                {
                    break;
                }
            }
            catch (PageException e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        Assert.assertNotNull(activities);
        if (activities.isEmpty())
            saveScreenShot("getActivities.empty");
        Assert.assertFalse(activities.isEmpty());
    }

    @Test(groups = "alfresco-one", dependsOnMethods = "getActivities")
    public void selectAndDisplayActivity()
    {
        dashBoard = dashBoard.getNav().selectMyDashBoard();
        dashBoard.render();
        MyActivitiesDashlet dashlet = dashBoard.getDashlet("activities").render();
        List<ActivityShareLink> activityShareLinks = dashlet.getActivities();
        assertTrue(activityShareLinks.size() == 2, "Number of Activities should be 2 but there is only: " + activityShareLinks.size());
        String expectedPreviewed = String.format("%s %s previewed document %s in %s", firstName, lastName, fileName, siteName);
        assertEquals(activityShareLinks.get(0).getDescription(), expectedPreviewed);
        String expectedAdded = String.format("%s %s added document %s in %s", firstName, lastName, fileName, siteName);
        assertEquals(activityShareLinks.get(1).getDescription(), expectedAdded);
    }

    protected void uploadDocument() throws Exception
    {
        try
        {
            File file = SiteUtil.prepareFile();
            fileName = file.getName();

            if (!alfrescoVersion.isCloud())
            {
                dashBoard = loginAs(username, password);
                // Creating new user.

                UserSearchPage page = dashBoard.getNav().getUsersPage().render();
                NewUserPage newPage = page.selectNewUser().render();
                newPage.inputFirstName(firstName);
                newPage.inputLastName(lastName);
                newPage.inputEmail(userName);
                newPage.inputUsername(userName);
                newPage.inputPassword(userName);
                newPage.inputVerifyPassword(userName);
                UserSearchPage userCreated = newPage.selectCreateUser().render();
                userCreated.searchFor(userName).render();
                Assert.assertTrue(userCreated.hasResults());
                logout(drone);
                loginAs(userName, userName);
            }
            else
                loginAs(username, password);

            SiteUtil.createSite(drone, siteName, "description", "Public");
            SitePage site = drone.getCurrentPage().render();
            DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
            UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
            docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
            DocumentDetailsPage detailsPage = docPage.selectFile(fileName).render();
            dashBoard = detailsPage.getNav().selectMyDashBoard().render();
            // DocumentDetailsPage dd = docPage.selectFile(fileName).render();
            // dd.selectLike();
        }
        catch (Throwable pe)
        {
            saveScreenShot("uploadDodDashlet");
            logger.error("Problem deleting site", pe);
        }
    }

    @Test(dependsOnMethods = "instantiateMyActivitiesDashlet", groups = "alfresco-one")
    public void selectOptionFromUserActivitiesPositiveTest() throws Exception
    {
        MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet("activities").render();
        DashBoardPage dashBoard = activitiesDashlet.selectOptionFromUserActivities("Everyone\'s activities").render();
        Assert.assertTrue(dashBoard.getDashlet("activities") instanceof Dashlet);
    }

    @Test(dependsOnMethods = "instantiateMyActivitiesDashlet", groups = "alfresco-one", expectedExceptions = PageOperationException.class)
    public void selectOptionFromUserActivitiesNegativeTest()
    {
        MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet("activities").render();
        HtmlPage page = activitiesDashlet.selectOptionFromUserActivities("Everyone\'s activities page");
        Assert.assertNotNull(page);
    }

    @Test(dependsOnMethods = "instantiateMyActivitiesDashlet", groups = "alfresco-one")
    public void selectRssFeedPagePositiveTest()
    {
        String currentUrl = drone.getCurrentUrl();
        MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet("activities").render();
        RssFeedPage rssFeedPage = activitiesDashlet.selectRssFeedPage(username, password);
        assertTrue(rssFeedPage.isSubscribePanelDisplay());
        drone.navigateTo(currentUrl);
    }

    @Test(dependsOnMethods = "instantiateMyActivitiesDashlet", groups = "alfresco-one")
    public void isOptionSelectedPositiveTest()
    {
        String option = "Everyone\'s activities";
        MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromUserActivities(option).render();
        Assert.assertTrue(activitiesDashlet.isOptionSelected(option));
    }

    @Test(dependsOnMethods = "instantiateMyActivitiesDashlet", groups = "alfresco-one")
    public void isOptionSelectedNegativeTest()
    {
        String selectedOption = "My activities";
        String verifiedOption = "Everyone\'s activities";
        MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromUserActivities(selectedOption).render();
        Assert.assertFalse(activitiesDashlet.isOptionSelected(verifiedOption));
    }
    
    @Test(dependsOnMethods = "instantiateMyActivitiesDashlet", groups = "alfresco-one")
    public void isHistoryOptionSelectedPositiveTest()
    {
        MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromUserActivities("Everyone\'s activities").render();
        activitiesDashlet.selectOptionFromHistoryFilter(TODAY).render();
        Assert.assertTrue(activitiesDashlet.isHistoryOptionSelected(TODAY));
    }
    
    @Test(dependsOnMethods = "instantiateMyActivitiesDashlet", groups = "alfresco-one")
    public void isHistoryOptionSelectedNegativeTest()
    {
        MyActivitiesDashlet activitiesDashlet = dashBoard.getDashlet("activities").render();
        activitiesDashlet.selectOptionFromUserActivities("Everyone\'s activities").render();
        activitiesDashlet.selectOptionFromHistoryFilter(TODAY).render();
        Assert.assertFalse(activitiesDashlet.isHistoryOptionSelected(SEVEN_DAYS));
    }
}
