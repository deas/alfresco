package org.alfresco.share.cloud.documentlibrary;

import java.io.File;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.UploadLimitCloudMessage;
import org.alfresco.share.dashlet.SiteSearchDashletTest;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.ibm.icu.text.DecimalFormat;

/**
 * @author bogdan.bocancea
 */

@Listeners(FailedTestListener.class)
public class UploadItemsTests extends AbstractUtils
{
    String testUser;
    protected String siteName = "";
    DocumentLibraryPage documentLibraryPage;
    SiteDashboardPage siteDashBoard;
    private static final String TESTFILE_DOC = "Test_12513_largeFile.doc";

    private static Log logger = LogFactory.getLog(SiteSearchDashletTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepDocumentLibraryCloud" })
    public void dataPrep_AONE_12463() throws Exception
    {

        String testName = getTestName();
        String siteName = getSiteName(testName);

        // Create User
        String testUser = getUserNameFreeDomain(testName) + "1";
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

    }

    @Test(groups = "AlfrescoOneCloud")
    public void AONE_12463() throws Exception
    {
        File file = SiteUtil.prepareFile();
        DocumentLibraryPage documentLibraryPage;
        testName = getTestName();
        String siteName = getSiteName(testName);
        String testUser = getUserNameFreeDomain(testName) + "1";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // open site document library page
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        SitePage site = drone.getCurrentPage().render();
        documentLibraryPage = site.getSiteNav().selectSiteDocumentLibrary().render();

        UploadFilePage upLoadPage = documentLibraryPage.getNavigation().selectFileUpload().render();

        upLoadPage.cancel();

        Assert.assertFalse(documentLibraryPage.isFileVisible(file.getName()));

    }

    @Test(groups = { "DataPrepDocumentLibraryCloud" })
    public void dataPrep_AONE_12513() throws Exception
    {

        String testName = getTestName();
        String siteName = getSiteName(testName);

        // Create User
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

    }

    @Test(groups = "AlfrescoOneCloud")
    public void AONE_12513() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName) + "1";
        String siteName = getSiteName(testName);

        String fileLocation = DATA_FOLDER + TESTFILE_DOC;
        File file = new File(fileLocation);
        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // open site document library page
        documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        SitePage site = drone.getCurrentPage().render();
        documentLibraryPage = site.getSiteNav().selectSiteDocumentLibrary().render();

        UploadLimitCloudMessage errorPage = new UploadLimitCloudMessage(drone);

        UploadFilePage uploadPage = documentLibraryPage.getNavigation().selectFileUpload().render();

        uploadPage.upload(fileLocation);

        String errorMessage = errorPage.getMessage();
        uploadPage = errorPage.clickOk().render();

        long fileSizeInBytes = file.length();

        String value = readableFileSize(fileSizeInBytes);

        Assert.assertEquals(errorMessage, "File " + TESTFILE_DOC + " is too big " + "(" + value + ")" + ", maximum file size 50 MB.");

        uploadPage.clickCancel();
        Assert.assertFalse(documentLibraryPage.isFileVisible(TESTFILE_DOC));

    }

    public static String readableFileSize(long size)
    {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        double value = (double) (size / Math.pow(1024, digitGroups));

        DecimalFormat df = new DecimalFormat("#.##");
        value = Double.valueOf(df.format(value));
        int a = (int) Math.round(value);

        return a + " " + units[digitGroups];
    }

}
