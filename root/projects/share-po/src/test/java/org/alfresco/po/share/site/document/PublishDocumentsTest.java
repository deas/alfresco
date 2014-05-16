package org.alfresco.po.share.site.document;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.adminconsole.ChannelManagerPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.alfresco.po.share.adminconsole.Channel.Flickr;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class PublishDocumentsTest extends AbstractTest
{
    private static final String SITE_NAME = "site-" + System.currentTimeMillis();
    private static final String FILE_NAME = "img-" + System.currentTimeMillis();
    private File tmpFile;
    private final static String FLICKR_LOGIN_NAME = "gogigruzinidze@yahoo.com";
    private final static String FLICKR_PASSWORD = "parkh0useG";

    /**
     * Pre test setup of a dummy file to upload.
     *
     * @throws Exception
     */
    @BeforeClass(groups="Enterprise-only")
    public void prepare() throws Exception
    {
        DashBoardPage dashBoard = loginAs(username, password);
        createChannel(dashBoard);
        SiteUtil.createSite(drone, SITE_NAME, "description", "Public");
        tmpFile = SiteUtil.prepareJpg(FILE_NAME);
        uploadFile();
    }

    /**
     * Upload File
     *
     * @throws IOException
     */
    private void uploadFile() throws IOException
    {
        SiteDashboardPage siteDashboardPage = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibPage = siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        uploadForm.uploadFile(tmpFile.getCanonicalPath()).render();
    }

    /**
     * create Flickr Channel
     *
     * @param page
     */
    private void createChannel(SharePage page)
    {
        ChannelManagerPage channelManagerPage = page.getNav().getChannelManagerPage();
        if (channelManagerPage.isChannelPresent(Flickr))
        {
            channelManagerPage.deleteChannel(Flickr);
        }
        channelManagerPage.createFlickrChannel(FLICKR_LOGIN_NAME, FLICKR_PASSWORD);
    }

    @Test()
    public void publishFileToFlickr()
    {
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        DocumentDetailsPage detailsPage = documentLibraryPage.selectFile(tmpFile.getName()).render();
        PublishPage publishPage = detailsPage.selectPublish();
        publishPage.selectChannel(Flickr);
        detailsPage = (DocumentDetailsPage) publishPage.selectPublish();
        assertTrue(detailsPage.isPublishPopupDisplayed(tmpFile.getName(), Flickr));
    }

    @Test(dependsOnMethods = "publishFileToFlickr")
    public void checkClosePopUp()
    {
        DocumentDetailsPage detailsPage = drone.getCurrentPage().render();
        detailsPage.closePublishPopup();
        assertFalse(detailsPage.isPublishPopupDisplayed(tmpFile.getName(), Flickr));
    }

    @AfterClass(groups="Enterprise-only")
    public void deleteChannel()
    {
        SharePage page = drone.getCurrentPage().render();
        ChannelManagerPage channelManagerPage = page.getNav().getChannelManagerPage();
        channelManagerPage.deleteChannel(Flickr);
    }

}
