/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.dashlet;

import static org.alfresco.share.util.ShareUser.openSiteDashboard;
import static org.testng.Assert.*;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.dashlet.SiteContentFilter;
import org.alfresco.po.share.dashlet.sitecontent.DetailedViewInformation;
import org.alfresco.po.share.dashlet.sitecontent.SimpleViewInformation;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.*;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Shan Nagarajan
 */
@Listeners(FailedTestListener.class)
public class RecentlyModifiedDashletTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(RecentlyModifiedDashletTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepDashlets" })
    public void dataPrep_Dashlets_7935() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        String[] fileInfo = { fileName, DOCLIB };
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
        detailsPage.selectFavourite().render();

    }

    @Test
    public void AONE_3417()
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Open Site DashBoard
        openSiteDashboard(drone, siteName);

        SiteContentDashlet siteContentDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_CONTENT).render();

        siteContentDashlet.selectFilter(SiteContentFilter.MY_FAVOURITES);

        siteContentDashlet.clickSimpleView();

//        for (int i = 0; i < retrySearchCount; i++)
//        {
//            try
//            {
//                siteContentDashlet.renderSimpleViewWithContent();
//            }
//            catch (PageRenderTimeException e)
//            {
//                // There is Exception going retry again.
//                continue;
//            }
//            break;
//        }

        siteContentDashlet.renderSimpleViewWithContent();

        List<SimpleViewInformation> informations = siteContentDashlet.getSimpleViewInformation();
        SimpleViewInformation simpleViewInformation = informations.get(0);

        assertTrue(simpleViewInformation.getContentStatus().contains("Created"));
        assertTrue(simpleViewInformation.isPreviewDisplayed());
        assertEquals(simpleViewInformation.getContentDetail().getDescription().trim(), fileName.trim());
        assertNotNull(simpleViewInformation.getThumbnail());

        // Test amended for https://issues.alfresco.com/jira/browse/ALF-20348
        // assertTrue(simpleViewInformation.getUser().toString().contains(testUser));
        //
        // MyProfilePage profilePage = simpleViewInformation.clickUser();
        // assertTrue(profilePage.titlePresent());

        openSiteDashboard(drone, siteName);
        
        siteContentDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_CONTENT).render();
        informations = siteContentDashlet.getSimpleViewInformation();
        simpleViewInformation = informations.get(0);

        DocumentDetailsPage detailsPage = simpleViewInformation.clickContentDetail();
        assertTrue(detailsPage.isDocumentDetailsPage());

    }

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_3418() throws Exception
    {
        testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String testUser1 = getUserNameFreeDomain(testName + 1);
        String siteName = getSiteName(testName)+ System.currentTimeMillis();
        String testContent = getFileName(testName) + System.currentTimeMillis();
        String description = "For testing purposes";
        String fileName = getRandomString(5) + ".txt";
        String comment = getRandomString(5);

        // create two users
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
        testUserInfo = new String[] { testUser1 };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.logout(drone);
        // any site is created  (e.g. by UserA)
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // any content is uploaded to Doc lib (e.g. TestDoc with 6 bytes site and "For testing purposes" description);
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ContentDetails content = new ContentDetails();
        content.setName(testContent);
        content.setDescription(description);
        DocumentLibraryPage documentLibraryPage = ShareUser.createContent(drone, content, ContentType.PLAINTEXT);
        assertTrue(documentLibraryPage.isFileVisible(testContent));

        // the doc is modified several times (e.g was edited offline 2 times and has 1.2 version number)

        // navigate to the details page
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(testContent).render();

        // select upload new version
        for(int i = 0; i < 2; i++)
        {
            UpdateFilePage updatePage = documentDetailsPage.selectUploadNewVersionIcon().render();
            updatePage.selectMinorVersionChange();
            File newFileName = newFile(DATA_FOLDER + (fileName), fileName);
            updatePage.uploadFile(newFileName.getCanonicalPath());
            SitePage sitePage = updatePage.submit().render();
            sitePage.render();
            FileUtils.forceDelete(newFileName);

        }

        // invite another user to the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testUser1, siteName, UserRole.MANAGER);

        ShareUser.login(drone, testUser1, DEFAULT_PASSWORD);

        // the doc is liked by any user (not current user, e.g. UserB)
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        assertTrue(documentLibraryPage.isFileVisible(testContent));
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(drone, testContent);
        fileInfo.selectLike();

        // Site Dashboard page is displayed
        ShareUser.login(drone, testUser);
        ShareUser.openSiteDashboard(drone, siteName);

        // browse to Site Content dashlet, choose I've recently modified filter and click Detailed view icon
        SiteContentDashlet siteContentDashlet = ShareUserDashboard.getDashlet(drone, Dashlets.SITE_CONTENT).render();
        siteContentDashlet.selectFilter(SiteContentFilter.I_HAVE_RECENTLY_MODIFIED);
        siteContentDashlet.clickDetailView();

        siteContentDashlet.renderDetailViewWithContent();

        List<DetailedViewInformation> informations = siteContentDashlet.getDetailedViewInformation();
        DetailedViewInformation detailedViewInformation = informations.get(0);

        // [content / document's name link] [version number]
        assertTrue(detailedViewInformation.getContentStatus().contains("Modified"));
        assertNotNull(detailedViewInformation.getContentDetail());
        assertEquals(detailedViewInformation.getContentDetail().getDescription(), testContent);
        assertEquals(detailedViewInformation.getDescription(), description);
        assertEquals(detailedViewInformation.getVersion(), 1.2);
        assertNotNull(detailedViewInformation.getLike());
        assertNotNull(detailedViewInformation.getFavorite());
        assertNotNull(detailedViewInformation.getComment());
        assertEquals(detailedViewInformation.getLikecount(), 1);
        assertTrue(detailedViewInformation.getFileSize().contains("9 bytes"));

        // go back and click Favorite link
        detailedViewInformation.getFavorite().click().render();

        // click [content / document's name link]
        String href = detailedViewInformation.getContentDetail().getHref();
        drone.navigateTo(href);
        documentDetailsPage.render();
        assertTrue(documentDetailsPage.isFavourite());

        // go back and click Remove document from favorites icon
        ShareUser.openSiteDashboard(drone, siteName);

        informations = siteContentDashlet.getDetailedViewInformation();
        detailedViewInformation = informations.get(0);
        assertTrue(detailedViewInformation.isFavouriteEnabled());

        detailedViewInformation.getFavorite().click().render();

        informations = siteContentDashlet.getDetailedViewInformation();
        detailedViewInformation = informations.get(0);
        assertNotNull(detailedViewInformation.getFavorite());
        assertFalse(detailedViewInformation.isFavouriteEnabled());

        // click on thumbnail icon
        documentDetailsPage = detailedViewInformation.getThumbnail().click().render();
        assertTrue(documentDetailsPage.getTitle().contains("Document Details"));
        assertFalse(documentDetailsPage.isFavourite());

        // go back and click Like link
        ShareUser.openSiteDashboard(drone, siteName);
        informations = siteContentDashlet.getDetailedViewInformation();
        detailedViewInformation = informations.get(0);
        detailedViewInformation.getLike().click().render();

        informations = siteContentDashlet.getDetailedViewInformation();
        detailedViewInformation = informations.get(0);
        assertEquals(detailedViewInformation.getLikecount(), 2);
        assertTrue(detailedViewInformation.isLikeEnabled());

        // click Unlike icon
        detailedViewInformation.getLike().click().render(maxWaitTime);
        informations = siteContentDashlet.getDetailedViewInformation();
        detailedViewInformation = informations.get(0);
        assertEquals(detailedViewInformation.getLikecount(), 1);

        // click Comment link
        documentDetailsPage = detailedViewInformation.clickComment().render();
        assertTrue(documentDetailsPage.getTitle().contains("Document Details"));

        ShareUser.logout(drone);

    }


}
