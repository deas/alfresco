package org.alfresco.share.site.document;

import org.alfresco.po.share.preview.PdfJsPlugin;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserPdfJsPreview;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * HTML5 previewer tests
 * 
 * @author wabson
 *
 */
@Listeners(FailedTestListener.class)
public class PdfJsPreviewTest extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(PdfJsPreviewTest.class);    
    
    private static String testPassword = DEFAULT_PASSWORD;
    protected String testUser;
    protected String siteName = "";

    private static final String FILES_PREFIX = "preview" + SLASH;
    private static final String TESTFILE_PDF =  "PreviewTest.pdf";
    private static final String TESTFILE_DOC = "PreviewTest.doc";

    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }
    
    /**
     * DataPreparation method - ACE_1292_01
     *   
     * 1) Login
     * 2) Create User
     * 3) Create Site
     * 4) Create and upload PDF file with content
     * 
     * @throws Exception
     */
    
    @Test(groups={"DataPrepPdfJsPreview"})
    public void dataPrep_PdfJsPreview_ACE_1292_01() throws Exception
    {
        String testName = getTestName();
        String testUser = testName + "@" + DOMAIN_FREE;
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        try
        {
            //Create user
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
 
            //Login as created user 
            ShareUser.login(drone, testUser, testPassword);

            //Create site
            SiteUtil.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // Upload test file
            ShareUser.uploadFileInFolder(drone, new String[] { FILES_PREFIX + TESTFILE_PDF });

            //Created user logs  out
            ShareUser.logout(drone);

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
    
    
    
    /**
     * 
     * 
     * 1) User logs in
     * 2) Performs live search with testName as a search term
     * 3) Checks that the created document is displayed in document search results 
     * 4) Checks that the created site is displayed in site search results
     * 5) Checks that the created user name is displayed in people search results
     * 6) Checks that all the links in live search results work properly
     * 7) User logs out
     */
    @Test(groups={"TestPdfJsPreview"})
    public void pdfJsPreview_ACE_1292_01()
    {
        // Search name is the same as the test name
        testName = getTestName();
        String testUser = testName + "@" + DOMAIN_FREE;
        ShareUser.login(drone, testUser, testPassword);
        ShareUser.openSiteDashboard(drone, getSiteName(testName));
        ShareUser.openDocumentLibrary(drone);
        ShareUser.openDocumentDetailPage(drone, TESTFILE_PDF);

        PdfJsPlugin viewer = ShareUserPdfJsPreview.preview(drone);
        Assert.assertEquals(viewer.getMainViewNumDisplayedPages(), 3);

        ShareUser.logout(drone);
    }
    
    
    /**
     * 
     * DataPreparation method - ACE_1292_02
     * 
     * 1) Login
     * 2) Create User
     * 3) Create Site
     * 4) Create and upload MS Word file with content
     *  
     * @throws Exception
     */
    
    @Test(groups={"DataPrepPdfJsPreview"})
    public void dataPrep_PdfJsPreview_ACE_1292_02() throws Exception
    {
        String testName = getTestName();
        String testUser = testName + "@" + DOMAIN_FREE;
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        try
        {
            //Create user
            CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
 
            //Login as created user 
            ShareUser.login(drone, testUser, testPassword);

            //Create site
            SiteUtil.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // Upload test file
            ShareUser.uploadFileInFolder(drone, new String[] { FILES_PREFIX + TESTFILE_DOC });

            //Created user logs  out
            ShareUser.logout(drone);

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
    
    
    /**
     * Upload a Word document and check that it is displayed correctly in the viewer
     */
    @Test(groups={"TestPdfJsPreview"})
    public void pdfJsPreview_ACE_1292_02()
    {
        // Search name is the same as the test name
        testName = getTestName();
        String testUser = testName + "@" + DOMAIN_FREE;
        ShareUser.login(drone, testUser, testPassword);
        ShareUser.openSiteDashboard(drone, getSiteName(testName));
        ShareUser.openDocumentLibrary(drone);
        ShareUser.openDocumentDetailPage(drone, TESTFILE_DOC);

        PdfJsPlugin viewer = ShareUserPdfJsPreview.preview(drone);
        Assert.assertEquals(viewer.getMainViewNumDisplayedPages(), 3);

        ShareUser.logout(drone);
    }
}
