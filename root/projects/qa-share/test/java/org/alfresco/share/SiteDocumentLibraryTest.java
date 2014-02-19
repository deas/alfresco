/**
 * 
 */
package org.alfresco.share;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.po.share.site.document.TinyMceEditor.FormatType;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This test class includes Site Document Library Related Tests.
 * 
 * @author cbairaajoni
 */
@Listeners(FailedTestListener.class)
public class SiteDocumentLibraryTest extends AbstractTests
{
    private static Log logger = LogFactory.getLog(SiteDocumentLibraryTest.class);

    // File name needs to be changed according to the file used to test upload.
    // And this file is presents in the testdata folder inside project.
    private static String FILE_WITH_IMAP_FORMAT = "TestPDFImap.pdf";

    protected String testUser;
    protected String siteName = "";

    /**
     * Class includes: Tests from TestLink in Area: Site Document Library Tests
     * <ul>
     * <li>Perform an Activity on Library</li>
     * <li>Site Document library shows documents</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("[Suite ] : Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepSiteDocumentLibrary" })
    public void dataPrep_Enterprise40x_5606() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Enterprise Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        String[] fileInfo = { fileName };
        ShareUser.uploadFileInFolder(drone, fileInfo);
    }

    /**
     * This test includes the Enterprisex-5606 and 5607. This test is valid for
     * Cloud and Enterprise4.1 only. Enterprise4.2 UI development changes are in
     * progress. Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site</li>
     * <li>Open Document library page</li>
     * <li>select added any document</li>
     * <li>Document Library page opens</li>
     * <li>Edit the added tags</li>
     * <li>Click on Remove Tag icon</li>
     * <li>verify removed tags should not be displayed</li>
     * </ul>
     */
    @Test()
    public void enterprise40x_5606()
    {
        DocumentLibraryPage documentLibPage = null;

        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String siteName = getSiteName(testName);
            String testUser = getUserNameFreeDomain(testName);
            String fileName = getFileName(testName);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
            documentLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

            // Adding test tag to folders with same name on both folders.
            documentLibPage.getFileDirectoryInfo(fileName).addTag("1234567890");
            documentLibPage.getFileDirectoryInfo(fileName).addTag("`¬!£$%^&();{}[]'@#~,");
            documentLibPage.getFileDirectoryInfo(fileName).addTag("abcdefghijklmnopqrstuvwxyzsdsdfknoiwenirnskdnfernlkaniifsdreiwolektkmnsdmfnlksisdlkfnksdnfksnnnnnnnnnnnnnnnnnwsierfweknfknsdfxckvnksdifksdfike");

            FileDirectoryInfo content = documentLibPage.getFileDirectoryInfo(fileName);

            // Tag
            String contentName = content.getName();
            List<String> contentTags = content.getTags();
            Assert.assertEquals(contentTags.size(), 3);

            // Verify remove link on tags are present and click on inline
            // edit tags cancel button.
            for (String tagName : contentTags)
            {
                content.clickOnAddTag();
                Assert.assertTrue(content.removeTagButtonIsDisplayed(tagName));
                content.clickOnTagCancelButton();
                content = documentLibPage.getFileDirectoryInfo(contentName);
            }

            // Click on tags remove link and click on inline edit tags save button
            for (String tagName : contentTags)
            {
                content.clickOnAddTag();
                content.clickOnTagRemoveButton(tagName);
                documentLibPage.getFileDirectoryInfo(contentName).clickOnTagSaveButton();
                content = documentLibPage.getFileDirectoryInfo(contentName);
            }

            Assert.assertFalse(content.hasTags(), "Tags not removed for the content");
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepSiteDocumentLibrary" })
    public void dataPrep_Enterprise40x_8958() throws Exception
    {
        String testName = getTestName();
        // TODO: Chiran: Why is suffix _User necessary?
        String testUser = getUserNameFreeDomain(testName + "_User");

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    /**
     * This test includes the Enterprisex-8958. Test:
     * <ul>
     * <li>Login</li>
     * <li>Create Site</li>
     * <li>Open Document library page</li>
     * <li>select added any document</li>
     * <li>Document Library page opens</li>
     * <li>added tags</li>
     * <li>Click on tag link under folder name or Clickon tag link under Tags tree menu list.
     * <li>verify the files or folders are displayed which are tagged with the tagName</li>
     * </ul>
     */
    @Test()
    public void enterprise40x_8958()
    {
        DocumentLibraryPage documentLibPage = null;

        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String testUser = getUserNameFreeDomain(testName + "_User");

            siteName = getSiteName(testName) + System.currentTimeMillis();

            String folderName = "The first folder";
            String folderDescription = String.format("Description of %s", folderName);
            String fileName = getFileName(testName);

            String testTagName = "testTag";

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Site
            ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

            ShareUserSitePage.createFolder(drone, folderName, folderDescription).render();

            String[] fileInfo = { fileName };
            ShareUser.uploadFileInFolder(drone, fileInfo).render(maxWaitTime);

            ShareUser.openDocumentLibrary(drone);

            // Select detailed view icon on DocLib Page.
            documentLibPage = ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW);

            // Adding test tag to folders with same name on both folders.
            //documentLibPage.getFileDirectoryInfo(folderName).addTag(testTagName);
            ShareUserSitePage.addTagsFromDocLib(drone, folderName, Arrays.asList(testTagName));

            // Clicking the tagName link present under folder name
            documentLibPage.getFileDirectoryInfo(folderName).clickOnTagNameLink(testTagName).render();

            // Check that the folder is listed
            Assert.assertTrue(ShareUserSitePage.searchDocumentLibraryWithRetry(drone, folderName, true));

            // Clicking the tagName present under Tags menu tree on Document Library page.
            documentLibPage = documentLibPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(testTagName).render();

            Assert.assertTrue(documentLibPage.isFileVisible(folderName));
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepSiteDocumentLibrary" })
    public void dataPrep_Enterprise40x_8506() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName);
        // TODO: Chiran: Why _User?
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

        ShareUser.openSitesDocumentLibrary(drone, siteName).render(maxWaitTime);

        String[] fileInfo = { FILE_WITH_IMAP_FORMAT };
        ShareUser.uploadFileInFolder(drone, fileInfo);
    }

    /**
     * Test:
     * <ul>
     * <li>Login as new user</li>
     * <li>Create Site</li>
     * <li>Open Document library page</li>
     * <li>select added PDF document</li>
     * <li>Document details page opens</li>
     * <li>
     * <li>verify document preview for several times</li>
     * </ul>
     */
    @Test(groups = "windowsOnly")
    public void enterprise40x_8506()
    {
        DocumentDetailsPage docDetailsPage = null;
        try
        {
            /** Start Test */
            testName = getTestName();

            /** Test Data Setup */
            String siteName = getSiteName(testName);
            String testUser = getUserNameFreeDomain(testName);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Open Site Document Library

            ShareUser.openSiteDashboard(drone, siteName);

            DocumentLibraryPage libraryPage = ShareUser.openDocumentLibrary(drone).render(maxWaitTime);

            docDetailsPage = libraryPage.selectFile(FILE_WITH_IMAP_FORMAT).render();

            Assert.assertTrue(docDetailsPage.isPreviewDisplayed());

        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            testCleanup(drone, testName);
        }
    }

    @Test(groups = { "DataPrepSiteDocumentLibrary" })
    public void dataPrep_Enterprise40x_5673() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        ShareUserSitePage.createFolder(drone, folderName, folderName);
    }

    /**
     * <ul>
     * <li>Login</li>
     * <li>Create Site</li>
     * <li>Open Document library page</li>
     * <li>select added any unsupported document</li>
     * <li>Document details page opens</li>
     * <li>Add Folder in document library<li>
     * <li>View Folder Details page<li>
     * <li>check Title appeared</li>
     * <li>check correct path for folder</li>
     * <li>check modify details present with user and date</li>
     * <li>check comment link is present</li>
     * <li>check Like link and counter is present</li>
     * <li>check Favourite link and counter present</li>
     * <li>check share pane is present</li>
     * <li>check properties present</li>
     * <li>check permissions present</li>
     * <li>check tag pane present</li>
     * <li>check download-as-zip on top right corner for Enterprise4.2</li>
     * <li></li>
     * </ul>
     */
    @Test
    public void enterprise40x_5673() throws Exception
    {
        // dataPrep_Enterprise40x_5673(drone);
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String comment = getComment(folderName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        FolderDetailsPage folderDetailsPage = docLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();

        Assert.assertEquals(folderName, folderDetailsPage.getContentTitle());
        Assert.assertTrue(folderDetailsPage.isCorrectPath(folderName));
        Assert.assertTrue(folderDetailsPage.isModifiedByDetailsPresent());
        Assert.assertTrue(folderDetailsPage.isCommentLinkPresent());

        folderDetailsPage = folderDetailsPage.selectLike().render();
        Assert.assertNotNull(folderDetailsPage.getLikeCount());
        Assert.assertTrue(folderDetailsPage.isLiked());
        folderDetailsPage = folderDetailsPage.selectLike().render();

        folderDetailsPage = folderDetailsPage.selectFavourite().render();
        Assert.assertTrue(folderDetailsPage.isFavourite());
        folderDetailsPage = folderDetailsPage.selectFavourite().render();

        Assert.assertTrue(folderDetailsPage.isCommentAddedAndRemoved(comment));
        Assert.assertTrue(folderDetailsPage.isSharePanePresent());

        Map<String, Object> properties = folderDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), folderName);
        Assert.assertEquals(properties.get("Title"), "(None)");
        if (!isAlfrescoVersionCloud(drone))
        {
            Map<String, String> permissionProperties = folderDetailsPage.getPermissionsOfDetailsPage();
            Assert.assertNotNull(permissionProperties);
            Assert.assertEquals(permissionProperties.get("Managers"), "Manager");
            Assert.assertEquals(permissionProperties.get("Collaborators"), "Collaborator");
            Assert.assertEquals(permissionProperties.get("Contributors"), "Contributor");
            Assert.assertEquals(permissionProperties.get("Consumers"), "Consumer");
            Assert.assertEquals(permissionProperties.get("AllOtherUsers"), "Consumer");
        }
        if (AlfrescoVersion.Enterprise42.equals(drone.getProperties().getVersion()))
            Assert.assertTrue(folderDetailsPage.isDownloadAsZipAtTopRight());

    }

    @Test(groups = { "DataPrepSiteDocumentLibrary" })
    public void dataPrep_Enterprise40x_3987() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        ShareUserSitePage.createFolder(drone, folderName, folderName);
    }

    /**
     * <ul>
     * <li>Login</li>
     * <li>Create Site</li>
     * <li>Open Document library page</li>
     * <li>select added any unsupported document</li>
     * <li>Document details page opens</li>
     * <li>Add Folder in document library<li>
     * <li>View Folder Details page<li>     * 
     * <li>Open Comment text editor</li>
     * <li>click on bold to change text to bold</li>
     * <li>click on italic to change text to italic</li>
     * <li>click on underlined to change text to underlined</li>
     * <li>click on bullet to change text with bullet point</li>
     * <li>click on number to change text with number point</li>
     * <li>click on fore colour to change text with other colour</li>
     * <li>click on undo to change text back</li>
     * <li>click on redo to change text with other colour</li>
     * <li></li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test
    public void enterprise40x_3987() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String comment = getComment(folderName);
        String fontStyle = "<font color=\"#0000FF\">";
        if (alfrescoVersion.isCloud())
        {
            fontStyle = "<font style=\"color: rgb(0, 0, 255);\">";
        }

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        FolderDetailsPage folderDetailsPage = docLibPage.getFileDirectoryInfo(folderName).selectViewFolderDetails().render();
        TinyMceEditor tinyMceEditor = folderDetailsPage.getContentPage();
        tinyMceEditor.setTinyMce(TinyMceEditor.FRAME_ID);
        tinyMceEditor.addContent(comment);

        // test text as BOLD
        tinyMceEditor.clickTextFormatter(FormatType.BOLD);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<b>" + comment + "</b>"));
        tinyMceEditor.removeFormatting();

        // test text as ITALIC
        tinyMceEditor.clickTextFormatter(FormatType.ITALIC);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<i>" + comment + "</i>"));
        tinyMceEditor.removeFormatting();

        // test text as UNDERLINED
        tinyMceEditor.clickTextFormatter(FormatType.UNDERLINED);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<u>" + comment + "</u>"));
        tinyMceEditor.removeFormatting();

        // test BULLET on text
        tinyMceEditor.clickTextFormatter(FormatType.BULLET);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<li>" + comment + "</li>"));
        tinyMceEditor.clickTextFormatter(FormatType.BULLET);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<p>" + comment + "</p>"));

        // test NUMBER on test
        tinyMceEditor.clickTextFormatter(FormatType.NUMBER);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<ol><li>" + comment + "</li></ol>"));
        tinyMceEditor.clickTextFormatter(FormatType.NUMBER);
        Assert.assertTrue(tinyMceEditor.getContent().contains("<p>" + comment + "</p>"));

        // test text color as BLUE
        tinyMceEditor.clickColorCode();
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains(fontStyle + comment + "</font>"));
        tinyMceEditor.removeFormatting();

        // test UNDO button on text
        tinyMceEditor.clickColorCode();
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains(fontStyle + comment + "</font>"));
        tinyMceEditor.clickUndo();
        Assert.assertTrue(tinyMceEditor.getContent().contains("<p>" + comment + "</p>"));

        // text REDO button on text
        tinyMceEditor.clickColorCode();
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains(fontStyle + comment + "</font>"));
        tinyMceEditor.clickUndo();
        Assert.assertTrue(tinyMceEditor.getContent().contains("<p>" + comment + "</p>"));
        tinyMceEditor.clickRedo();
        Assert.assertTrue(tinyMceEditor.getContent().contains(fontStyle + comment + "</font>"));
        tinyMceEditor.removeFormatting();
    }

    @Test(groups = { "DataPrepSiteDocumentLibrary" })
    public void dataPrep_Enterprise40x_5675() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] fileInfo = new String[] { fileName };

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractTests.SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        ShareUser.uploadFileInFolder(drone, fileInfo);
    }

    /**
     * <ul>
     * <li>Login</li>
     * <li>Create Site</li>
     * <li>Open Document library page</li>
     * <li>select comment of the file</li>
     * <li>Add your comment</li>
     * <li>verify the comment counter should be increased in doclib and details page as well<li>
     * <li>Delete the comment in details page<li> 
     * <li>verify the comment counter should be decreased in doclib and details page as well<li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test
    public void enterprise40x_5675() throws Exception
    {

        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String comment = getComment(fileName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertEquals(Integer.valueOf(0), docLibPage.getCommentCount());

        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        detailsPage = detailsPage.addComment(comment).render();

        docLibPage = ShareUser.openDocumentLibrary(drone);

        Assert.assertEquals(Integer.valueOf(1), docLibPage.getCommentCount());

        detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);

        Assert.assertEquals(1, detailsPage.getCommentCount());

        detailsPage.removeComment(comment);

        docLibPage = ShareUser.openDocumentLibrary(drone);

        Assert.assertEquals(Integer.valueOf(0), docLibPage.getCommentCount());
    }
}