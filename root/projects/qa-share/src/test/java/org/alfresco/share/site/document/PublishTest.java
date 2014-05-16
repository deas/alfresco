/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.share.site.document;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.adminconsole.ChannelManagerPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.share.util.*;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.HashMap;

import static org.alfresco.po.share.adminconsole.Channel.*;

/**
 * @author Roman.Chul
 */
@Test(enabled = true)
@Listeners(FailedTestListener.class)
public class PublishTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(PublishTest.class);
    protected String testUser;
    protected String siteName = "";
    String flickrName = "gogigruzinidze@yahoo.com";
    String flickrPassword = "parkh0useG";

    @Override
    @BeforeClass(alwaysRun = true, timeOut = 400000)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Start Tests in: " + testName);
    }


    public void createChannel()
    {
        // Login as Admin
        SharePage page = ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        ChannelManagerPage channelManagerPage = page.getNav().getChannelManagerPage();
        if (channelManagerPage.isChannelPresent(Flickr) && !channelManagerPage.isChannelAuthorised(Flickr))
        {
            channelManagerPage.deleteChannel(Flickr);
        }
        else if (!channelManagerPage.isChannelPresent(Flickr))
        {
            // Create flickr channel
            channelManagerPage.createFlickrChannel(flickrName, flickrPassword);
        }
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13864() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        //if Flick channel not exits - must create new.
        createChannel();

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = "channel-test-jpg.jpg";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);
    }

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void Enterprise40x_13864() throws Exception
    {
        testName = getTestName();
        
        String siteName = getSiteName(testName);
        String fileName = "channel-test-jpg.jpg";

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName).render();        
        double currentVersion = Double.parseDouble(detailsPage.getDocumentVersion());
        
        // TODO: Create and use util in all tests
        PublishPage publishPage = detailsPage.selectPublish();
        publishPage.selectChannel(Flickr);
        detailsPage = publishPage.selectPublish().render();
        
        // The popup window "<filename.filetype> is queued for publishing to <channel's name>" is displayed.
        Assert.assertTrue(detailsPage.isPublishPopupDisplayed(fileName, Flickr));
        
        // The content is uploaded to the channel.
        Assert.assertTrue(PublishUtil.isContentUploadedToFlickrChannel(drone, fileName, flickrName, flickrPassword));
        
        // The version of the document has increased
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName).render();
        
        double actualVersion = Double.parseDouble(detailsPage.getDocumentVersion());
        Assert.assertTrue(actualVersion > currentVersion);
    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13865() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        //if Flick channel not exits - must create new.
        createChannel();

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = "channel-test-jpg.jpg";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);
    }

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void Enterprise40x_13865() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String fileName = "channel-test-jpg.jpg";
        String editedFileName = getRandomString(5) + ".jpg";

        DocumentLibraryPage documentLibraryPage;

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setName(editedFileName);
        detailsPage = editPropertiesPage.selectSave().render();

        double currentVersion = Double.parseDouble(detailsPage.getDocumentVersion());
        PublishPage publishPage = detailsPage.selectPublish();
        publishPage.selectChannel(Flickr);
        publishPage.selectCancelPublish();
        
        // The content is not uploaded to the channel.
        Assert.assertFalse(PublishUtil.isContentUploadedToFlickrChannel(drone, editedFileName, flickrName, flickrPassword));
        
        // The version of the document hasn't changed
        ShareUser.openDocumentLibrary(drone);
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName).render();
        
        double actualVersion = Double.parseDouble(detailsPage.getDocumentVersion());
        Assert.assertEquals(actualVersion, currentVersion);

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13866() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        //if Flick channel not exits - must create new.
        createChannel();

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = "channel-test-jpg.jpg";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void Enterprise40x_13866() throws Exception
    {
        testName = getTestName();

        String siteName = getSiteName(testName);
        String fileName = "channel-test-jpg.jpg";

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName).render();

        PublishPage publishPage = detailsPage.selectPublish();
        publishPage.selectChannel(Flickr);
        // TODO: Amend selectPublish to return HtmlPage interface instead of SharePage
        detailsPage = publishPage.selectPublish().render();
        
        // The popup window "<filename.filetype> is queued for publishing to <channel's name>" is displayed;
        Assert.assertTrue(detailsPage.isPublishPopupDisplayed(fileName, Flickr));
        
        // Press close button (X): The popup window is disappeared.
        detailsPage.closePublishPopup();
        
        // TODO: Remove thread.sleep. Amend closePublishPopup to render page object
        Thread.sleep(5000);        
        Assert.assertFalse(detailsPage.isPublishPopupDisplayed(fileName, Flickr));

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13867() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        //if Flick channel not exits - must create new.
        createChannel();

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = "channel-test-jpg.jpg";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void Enterprise40x_13867() throws Exception
    {
        /** Start Test */
        testName = getTestName();

        /** Test Data Setup */
        String siteName = getSiteName(testName);
        String fileName = "channel-test-jpg.jpg";

        DocumentLibraryPage documentLibraryPage;

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(fileName).render();
        double currentPublishingVersion = Double.parseDouble(detailsPage.getPublishingVersion());
        PublishPage publishPage = detailsPage.selectPublish();
        publishPage.selectChannel(Flickr);
        publishPage.selectPublish();

        // The content is uploaded to the channel.
        Assert.assertTrue(PublishUtil.isContentUploadedToFlickrChannel(drone, fileName, flickrName, flickrPassword));

        // The publishing version has increased
        ShareUser.openDocumentLibrary(drone);
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName).render();
        
        double actualPublishingVersion = Double.parseDouble(detailsPage.getPublishingVersion());
        Assert.assertTrue(actualPublishingVersion > currentPublishingVersion);
        
        //The history contains information about publishing: document name, channel it was published to and the time it was published
        HashMap<String, String> publishingInfo = detailsPage.getPublishingInfo(Double.toString(actualPublishingVersion));
        Assert.assertEquals(publishingInfo.get("documentName"), fileName);
        Assert.assertEquals(publishingInfo.get("channelName"), String.format("New %s channel", Flickr.getChannelName()));
        Assert.assertTrue(publishingInfo.get("status").matches("Published .* ago") || publishingInfo.get("status").matches("Published just now"));
        Assert.assertEquals(publishingInfo.get("action"), "Unpublish");

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13868() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        //if Flick channel not exits - must create new.
        createChannel();

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = "channel-test-jpg.jpg";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void Enterprise40x_13868() throws Exception
    {
        testName = getTestName();

        String siteName = getSiteName(testName);
        String fileName = "channel-test-jpg.jpg";

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName).render();
        double currentPublishingVersion = Double.parseDouble(detailsPage.getPublishingVersion());
        
        PublishPage publishPage = detailsPage.selectPublish().render();
        publishPage.selectChannel(Flickr);
        publishPage.selectPublish();

        // The content is uploaded to the channel.
        Assert.assertTrue(PublishUtil.isContentUploadedToFlickrChannel(drone, fileName, flickrName, flickrPassword));

        // The publishing version has increased
        ShareUser.openDocumentLibrary(drone);
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName).render();
        
        double actualPublishingVersion = Double.parseDouble(detailsPage.getPublishingVersion());
        Assert.assertTrue(actualPublishingVersion > currentPublishingVersion);

        // Click Un publish from publishing history section
        ConfirmUnpublishPage confirmUnpublishPage = detailsPage.selectUnpublish(Double.toString(actualPublishingVersion));
        Assert.assertEquals(confirmUnpublishPage.getMessage(), "Do you really want to unpublish this content?");
        confirmUnpublishPage.selectAction(ConfirmUnpublishPage.Action.Cancel);
        
        //The version of the document hasn't changed
        ShareUser.openDocumentLibrary(drone);
        detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName).render();
        Assert.assertEquals(actualPublishingVersion, Double.parseDouble(detailsPage.getPublishingVersion()));

    }

    @Test(groups = { "DataPrepDocumentLibrary" })
    public void dataPrep_Enterprise40x_13869() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);

        //if Flick channel not exits - must create new.
        createChannel();

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // Upload File
        String fileName = "channel-test-jpg.jpg";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

    }

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void Enterprise40x_13869() throws Exception
    {
        testName = getTestName();
        
        String siteName = getSiteName(testName);
        String fileName = "channel-test-jpg.jpg";
        String editedFileName = getRandomString(5) + ".jpg";
        DocumentLibraryPage documentLibraryPage;

        // Login as Admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        DocumentDetailsPage detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName).render();
        
        EditDocumentPropertiesPage editPropertiesPage = detailsPage.selectEditProperties().render();
        editPropertiesPage.setName(editedFileName);
        detailsPage = editPropertiesPage.selectSave().render();

        double currentPublishingVersion = Double.parseDouble(detailsPage.getPublishingVersion());

        PublishPage publishPage = detailsPage.selectPublish();
        publishPage.selectChannel(Flickr);
        publishPage.selectPublish();

        // The content is uploaded to the channel.
        Assert.assertTrue(PublishUtil.isContentUploadedToFlickrChannel(drone, editedFileName, flickrName, flickrPassword));

        // The publishing version has increased
        ShareUser.openDocumentLibrary(drone);
        detailsPage = documentLibraryPage.selectFile(editedFileName).render();
        
        double actualPublishingVersion = Double.parseDouble(detailsPage.getPublishingVersion());
        Assert.assertTrue(actualPublishingVersion > currentPublishingVersion);

        // Click Un publish from publishing history section
        ConfirmUnpublishPage confirmUnpublishPage = detailsPage.selectUnpublish(Double.toString(actualPublishingVersion));
        Assert.assertEquals(confirmUnpublishPage.getMessage(), "Do you really want to unpublish this content?");
        confirmUnpublishPage.selectAction(ConfirmUnpublishPage.Action.Unpublish);

        // The content is unpublished from the channel.
        Assert.assertFalse(PublishUtil.isContentUploadedToFlickrChannel(drone, editedFileName, flickrName, flickrPassword));

        //The version of the document hasn't changed
        ShareUser.openDocumentLibrary(drone);
        detailsPage = ShareUserSitePage.openDetailsPage(drone, editedFileName).render();
        Assert.assertEquals(actualPublishingVersion, Double.parseDouble(detailsPage.getPublishingVersion()));
    }

    @AfterMethod(alwaysRun = true)
    public void logout()
    {
        ShareUser.logout(drone);
        logger.info("User logged out - drone.");
    }

}
