package org.alfresco.po.share.dashlet;

import java.io.File;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.SiteUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;

/**
 * Abstract class with common method used in dashlet
 * based test cases.
 * @author Michael Suzuki
 *
 */
public class AbstractSiteDashletTest extends AbstractDocumentTest
{
    private static Log logger = LogFactory.getLog(AbstractSiteDashletTest.class);
    protected String siteName;
    protected String fileName;
    protected SiteDashboardPage siteDashBoard;
    
    /**
     * Creates a site and uploads a document to the site.
     * @throws Exception if error
     */
    protected void uploadDocument()throws Exception
    {
        try
        {
            File file = SiteUtil.prepareFile();
            fileName = file.getName();
            loginAs(username, password);
            SiteUtil.createSite(drone,siteName, 
                     "description",
                     "Public");
            SitePage site = drone.getCurrentPage().render();
            DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
            UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload();
            docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
            DocumentDetailsPage dd = docPage.selectFile(fileName).render();
            dd.selectLike().render();
         }
        catch (Throwable pe)
        {
            saveScreenShot("uploadDodDashlet");
            logger.error("Problem deleting site", pe);
        }
    }
    
    protected void navigateToSiteDashboard()
    {
        String urlPath = String.format("%s/page/site/%s/dashboard", shareUrl, siteName);
        drone.navigateTo(urlPath);
        if(logger.isTraceEnabled())
        {
            logger.trace("navigating to " + urlPath);
        }
        siteDashBoard = drone.getCurrentPage().render();
    }
    
    @AfterClass(alwaysRun=true)
    public void deleteSite()
    {
        try
        {
            SiteUtil.deleteSite(drone, siteName);
            closeWebDrone();
        }
        catch (Exception e)
        {
            logger.error("Problem deleting site", e);
        }
    }
}
