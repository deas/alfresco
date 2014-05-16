package org.alfresco.share.unit;

import java.util.HashSet;
import java.util.Set;

import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserRepositoryPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.alfresco.po.share.enums.Encoder;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class UtilsTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(UtilsTest.class);

    protected String testUser;
    protected String testUserPass = DEFAULT_PASSWORD;

    protected String testDomain = "utils";

    /**
     * Class includes: Tests for utilities written for qa-share: Not to be executed in a test run for Ent / Cloud
     * <ul>
     * </ul>
     */
    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testDomain = getDomainName(testDomain);
        testUser = testName + "@" + testDomain;
        logger.info("Starting Tests: " + testName);
    }

    @Test
    public void testDeleteSite_100()
    {
        try
        {
            String testName = getTestName();
            String testUser = getUserNameForDomain(testName, testDomain) + System.currentTimeMillis();
            String[] testUserInfo = new String[] { testUser };

            String siteName = getSiteName(testName) + System.currentTimeMillis();
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            ShareUser.login(drone, testUser, testUserPass);

            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
            ShareUser.createSite(drone, siteName + "M", SITE_VISIBILITY_MODERATED);
            ShareUser.createSite(drone, siteName + "PR", SITE_VISIBILITY_PRIVATE);

            String siteurl = getSiteShortname(siteName);
            SiteDashboardPage sitedash = SiteUtil.openSiteURL(drone, siteurl);
            Assert.assertTrue(sitedash.isSite(siteName));

            DocumentLibraryPage doclib = SiteUtil.openSiteDocumentLibraryURL(drone, siteurl);
            Assert.assertTrue(doclib.isDocumentLibrary());
            
            Set<String> siteCollection = new HashSet<>();
            siteCollection.add(siteName);
            siteCollection.add(siteName + "M");
            siteCollection.add(siteName + "PR");

            SiteUtil.deleteSitesAsUser(drone, testUser, siteCollection);

            ShareUser.createSite(drone, siteName + "PR", SITE_VISIBILITY_PRIVATE);
            ShareUser.createSite(drone, siteName + "M", SITE_VISIBILITY_MODERATED);
            ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

            SiteUtil.deleteSites(drone, siteName);

        }
        catch (Throwable t)
        {
            reportError(drone, testName, t);
        }
    }

    @Test(groups={"Unit","EnterpriseOnly"})
    public void navigateFoldersTest_101() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        String folderName1 = getFolderName(testName) + "test1";
        String folderName2 = getFolderName(testName) + "test2";

        String fileName = getFileName(siteName) + "_file1";
        String fileInfo[];

        // User
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Creating folder
        ShareUserSitePage.createFolder(drone, folderName1, null);

        ShareUserSitePage.navigateToFolder(drone, DOCLIB);
        ShareUserSitePage.navigateToFolder(drone, DOCLIB + SLASH + folderName1);

        ShareUserSitePage.createFolder(drone, folderName2, null);
        ShareUserSitePage.navigateToFolder(drone, folderName1 + SLASH + folderName2);

        // Uploading files in the folder tree.
        fileInfo = new String[] { fileName, DOCLIB + SLASH + folderName1 + SLASH + folderName2 };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        fileInfo = new String[] { fileName, DOCLIB + SLASH + folderName1 };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        fileInfo = new String[] { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Repository Test
        if (!isAlfrescoVersionCloud(drone))
        {
            ShareUserRepositoryPage.openRepositorySimpleView(drone);

            ShareUserSitePage.createFolder(drone, folderName1, null);

            ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO);
            ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName1);

            ShareUserSitePage.createFolder(drone, folderName2, null);
            ShareUserRepositoryPage.navigateToFolderInRepository(drone, REPO + SLASH + folderName1 + SLASH + folderName2);

            // Uploading files in the folder tree.
            fileInfo = new String[] { fileName, REPO + SLASH + folderName1 + SLASH + folderName2 };
            ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo);

            fileInfo = new String[] { fileName, REPO + SLASH + folderName1 };
            ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo);

            fileInfo = new String[] { fileName, REPO };
            ShareUserRepositoryPage.uploadFileInFolderInRepository(drone, fileInfo);
        }
    }
    
    @Test(groups={"Unit","AlfrescoOne"})
    public void addCommentTest_102() throws Exception
    {
        String testName = getTestName() + System.currentTimeMillis();
        String siteName = getSiteName(testName);

        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        String folderName1 = getFolderName(testName) + "test1";

        String fileName = getFileName(siteName) + "_file1";
        String fileInfo[];
        
        String comment= "test comment";
        String xssComment = "";
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<IMG \"\"\">");
        stringBuilder.append("<SCRIPT>alert(\"test\")</SCRIPT>");
        stringBuilder.append("\">");
        xssComment= stringBuilder.toString();

        // User
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

        // Creating folder
        ShareUserSitePage.createFolder(drone, folderName1, null);

        // Uploading files in the folder tree.
        fileInfo = new String[] { fileName, DOCLIB};
        DocumentLibraryPage doclibPage = ShareUser.uploadFileInFolder(drone, fileInfo);

        DocumentDetailsPage docDetails = doclibPage.selectFile(fileName).render();
        
        // Add text comment
        docDetails.addComment(comment);
        docDetails.addComment(comment, null);
        docDetails.addComment(comment, Encoder.ENCODER_NOENCODER);
        docDetails.addComment(comment, Encoder.ENCODER_HTML);
        docDetails.addComment(comment, Encoder.ENCODER_JAVASCRIPT);
        
        Assert.assertTrue(docDetails.getComments().contains(comment),"Error adding comment" + comment);
        
        // Add comment for xss related test        
        docDetails.addComment(xssComment);
        docDetails.addComment(xssComment, null);
        docDetails.addComment(xssComment, Encoder.ENCODER_NOENCODER);
        docDetails.addComment(xssComment, Encoder.ENCODER_HTML);
        docDetails.addComment(xssComment, Encoder.ENCODER_JAVASCRIPT);
        
        Assert.assertTrue(docDetails.getComments().contains(xssComment),"Error adding comment" + xssComment);
        
        doclibPage = ShareUser.openDocumentLibrary(drone);
        
        FolderDetailsPage folderDetails = doclibPage.getFileDirectoryInfo(folderName1).selectViewFolderDetails().render();
        
        // Add text comment
        folderDetails.addComment(comment);
        folderDetails.addComment(comment, null);
        folderDetails.addComment(comment, Encoder.ENCODER_NOENCODER);
        folderDetails.addComment(comment, Encoder.ENCODER_HTML);
        folderDetails.addComment(comment, Encoder.ENCODER_JAVASCRIPT);
        
        Assert.assertTrue(docDetails.getComments().contains(comment),"Error adding comment" + comment);
        
        // Add comment for xss related test        
        folderDetails.addComment(xssComment);
        folderDetails.addComment(xssComment, null);
        folderDetails.addComment(xssComment, Encoder.ENCODER_NOENCODER);
        folderDetails.addComment(xssComment, Encoder.ENCODER_HTML);
        folderDetails.addComment(xssComment, Encoder.ENCODER_JAVASCRIPT);
        
        Assert.assertTrue(docDetails.getComments().contains(xssComment),"Error adding comment" + xssComment);
    }
    
    @Test
    public void testPagination_003() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        
        ShareUser.uploadFileInFolder(drone, new String[] { fileName });

        for (int i = 0; i < 2; i++)
        {
            ShareUser.uploadFileInFolder(drone, new String[] { i + "-" + fileName, DOCLIB }).render();
            ShareUserSitePage.createFolder(drone, i + "-" + folderName, i + "-" + folderName);
        }
        for (int i = 0; i < 6; i++)
        {
            ShareUser.createCopyOfAllContent(drone);
        }
        
        ShareUser.uploadFileInFolder(drone, new String[] { 20 + "-" + fileName, DOCLIB }).render();
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        
        // First file
        FileDirectoryInfo fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(drone, fileName);
        Assert.assertFalse(fileDirectoryInfo.isFolder());
        
        // Last file
        fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(drone, folderName);
        Assert.assertTrue(fileDirectoryInfo.isFolder());
        
        // No such file
        try
        {
            fileDirectoryInfo = ShareUserSitePage.getFileDirectoryInfo(drone, 50 + "-" + fileName);
            Assert.fail("No such file in the Site");
        }
        catch(PageException pe)
        {
            
        } 
    }
    
    @Test
    public void testDetailsPage_004() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, new String[] { testUser });

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        ShareUser.openSitesDocumentLibrary(drone, siteName);
        
        ShareUser.uploadFileInFolder(drone, new String[] { fileName });       

        ShareUserSitePage.createFolder(drone, folderName, folderName);
        
        DetailsPage detailsPage = ShareUserSitePage.openDetailsPage(drone, fileName);
        Assert.assertTrue(detailsPage instanceof DocumentDetailsPage, "Error Opening DocumentDetailsPage");
        
        ShareUser.openDocumentLibrary(drone);
        
        detailsPage = ShareUserSitePage.openDetailsPage(drone, folderName);
        Assert.assertTrue(detailsPage instanceof FolderDetailsPage, "Error Opening FolderDetailsPage");
        
        ShareUser.openDocumentLibrary(drone);

        ShareUserSitePage.selectView(drone, ViewType.GALLERY_VIEW);
        
        ShareUserSitePage.selectContent(drone, folderName);
        
        ShareUser.openDocumentLibrary(drone);

        ShareUserSitePage.selectView(drone, ViewType.SIMPLE_VIEW);
        
        ShareUserSitePage.selectContent(drone, folderName);
        
        ShareUser.openDocumentLibrary(drone);

        ShareUserSitePage.selectView(drone, ViewType.FILMSTRIP_VIEW);
        
        detailsPage = ShareUserSitePage.selectContent(drone, fileName).render();
        
        ShareUser.openDocumentLibrary(drone);

        ShareUserSitePage.selectView(drone, ViewType.DETAILED_VIEW);
        
        detailsPage = ShareUserSitePage.selectContent(drone, fileName).render();
        
    }
        
}
