/**
 *
 */
package org.alfresco.share;

import org.alfresco.po.share.enums.TinyMceColourCode;
import org.alfresco.po.share.site.document.*;
import org.alfresco.po.share.site.document.TinyMceEditor.FormatType;
import org.alfresco.share.util.AbstractUtils;
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

import java.util.List;

/**
 * This test class includes Site Document Library Related Tests.
 *
 * @author cbairaajoni
 */

// TODO: specify groups for tests
@Listeners(FailedTestListener.class)
public class SiteDocumentLibraryTest extends AbstractUtils
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
    public void dataPrep_AONE_1813() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

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
    public void AONE_1813()
    {
        DocumentLibraryPage documentLibPage = null;
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
        FileDirectoryInfo fileDirInfo = documentLibPage.getFileDirectoryInfo(fileName);
        fileDirInfo.addTag("1234567890");

        documentLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);
        fileDirInfo = documentLibPage.getFileDirectoryInfo(fileName);

        fileDirInfo.addTag("`¬!£$%^&();{}[]'@#~,");
        documentLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);

        fileDirInfo = documentLibPage.getFileDirectoryInfo(fileName);
        fileDirInfo
            .addTag(
                "abcdefghijklmnopqrstuvwxyzsdsdfknoiwenirnskdnfernlkaniifsdreiwolektkmnsdmfnlksisdlkfnksdnfksnnnnnnnnnnnnnnnnnwsierfweknfknsdfxckvnksdifksdfike");

        documentLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);
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
            documentLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);
            content = documentLibPage.getFileDirectoryInfo(contentName);
        }

        // Click on tags remove link and click on inline edit tags save
        // button
        for (String tagName : contentTags)
        {
            content.clickOnAddTag();
            content.clickOnTagRemoveButton(tagName);
            documentLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);
            documentLibPage.getFileDirectoryInfo(contentName).clickOnTagSaveButton();
            documentLibPage = (DocumentLibraryPage) ShareUser.getSharePage(drone);
            content = documentLibPage.getFileDirectoryInfo(contentName);
        }

        Assert.assertFalse(content.hasTags(), "Tags not removed for the content");
    }

    @Test(groups = { "DataPrepSiteDocumentLibrary" })
    public void dataPrep_AONE_2012() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render(maxWaitTime);

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
    @Test(groups = "WindowsOnly")
    public void AONE_2012()
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

        DocumentDetailsPage docDetailsPage = libraryPage.selectFile(FILE_WITH_IMAP_FORMAT).render();

        Assert.assertTrue(docDetailsPage.getPreviewerClassName().endsWith("PdfJs"));
    }

    @Test(groups = { "DataPrepSiteDocumentLibrary" })
    public void dataPrep_AONE_2179() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

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
     * <li>Add Folder in document library
     * <li>
     * <li>View Folder Details page
     * <li>*
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
    public void AONE_2179() throws Exception
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
        //tinyMceEditor.setTinyMce(drone.findAndWait(TinyMceEditor.FRAME_ID_SELECTOR).getAttribute("id"));
        tinyMceEditor.addContent(comment);

        // test text as BOLD
        tinyMceEditor.clickTextFormatter(FormatType.BOLD);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<strong>" + comment + "</strong>"));
        tinyMceEditor.removeFormatting();

        // test text as ITALIC
        tinyMceEditor.clickTextFormatter(FormatType.ITALIC);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<em>" + comment + "</em>"));
        tinyMceEditor.removeFormatting();

        // test text as UNDERLINED
        tinyMceEditor.clickTextFormatter(FormatType.UNDERLINED);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<span style=\"text-decoration: underline;\">" + comment + "</span>"));
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
        Assert.assertTrue(tinyMceEditor.getContent().contains("<ol style=\"\"><li>" + comment + "</li></ol>"));
        tinyMceEditor.clickTextFormatter(FormatType.NUMBER);
        Assert.assertTrue(tinyMceEditor.getContent().contains("<p>" + comment + "</p>"));

        // test text color as BLUE
        tinyMceEditor.clickColorCode(TinyMceColourCode.BLUE);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<span style=\"color: rgb(0, 0, 255);\">" + comment + "</span>"));
        tinyMceEditor.removeFormatting();

        // test UNDO button on text
        tinyMceEditor.clickColorCode(TinyMceColourCode.BLUE);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<span style=\"color: rgb(0, 0, 255);\">" + comment + "</span>"));
        tinyMceEditor.clickUndo();
        Assert.assertTrue(tinyMceEditor.getContent().contains("<p>" + comment + "</p>"));

        // text REDO button on text
        tinyMceEditor.clickColorCode(TinyMceColourCode.BLUE);
        Assert.assertEquals(comment, tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<span style=\"color: rgb(0, 0, 255);\">" + comment + "</span>"));
        tinyMceEditor.clickUndo();
        Assert.assertTrue(tinyMceEditor.getContent().contains("<p>" + comment + "</p>"));
        tinyMceEditor.clickRedo();
        Assert.assertTrue(tinyMceEditor.getContent().contains("<span style=\"color: rgb(0, 0, 255);\">" + comment + "</span>"));
        tinyMceEditor.removeFormatting();
        folderDetailsPage.clickAddButton();
        Assert.assertTrue(folderDetailsPage.isCommentCorrect(comment));

        // edit the comment
        folderDetailsPage.editComment(comment, comment + testUser);
        folderDetailsPage.saveEditComments();
        Assert.assertTrue(folderDetailsPage.isCommentCorrect(comment + testUser));

        // delete the comment
        folderDetailsPage.checkConfirmDeleteForm(comment + testUser);
        folderDetailsPage.deleteComment(comment + testUser);
        folderDetailsPage = drone.getCurrentPage().render();
        Assert.assertTrue(folderDetailsPage.getComments().isEmpty(), "Comment isn't deleted");
        Assert.assertEquals(folderDetailsPage.getCommentCount(), 0, "Incorrect comment Count: " + folderDetailsPage.getCommentCount());

    }

    @Test(groups = { "DataPrepSiteDocumentLibrary" })
    public void dataPrep_AONE_15007() throws Exception
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
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC).render();

        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        ShareUser.uploadFileInFolder(drone, fileInfo);
    }

    // Duplicates test from DocumentDetailsActionsTest.java

    /**
     * <ul>
     * <li>Login</li>
     * <li>Create Site</li>
     * <li>Open Document library page</li>
     * <li>select comment of the file</li>
     * <li>Add your comment</li>
     * <li>verify the comment counter should be increased in doclib and details page as well
     * <li>
     * <li>Delete the comment in details page
     * <li>
     * <li>verify the comment counter should be decreased in doclib and details page as well
     * <li>
     * </ul>
     *
     * @throws Exception
     */

    /**
     @Test public void AONE_15007() throws Exception
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
     */
}