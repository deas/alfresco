package org.alfresco.share.reports;

import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserReports;
import org.alfresco.share.util.api.CreateUserAPI;
import static org.apache.activemq.camel.component.ActiveMQComponent.activeMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Site Usage report tests
 * 
 * @author jcule
 *
 */
@Listeners(FailedTestListener.class)
public class SiteUsageReportTest extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(SiteUsageReportTest.class);
    
    private static final String FILE_PREVIEW_EVENT = "org.alfresco.documentlibrary.file-previewed";
    
    private static final String FILE_DOWNLOAD_EVENT = "org.alfresco.documentlibrary.file-downloaded";
    
    private static final String FILE_EDIT_EVENT = "org.alfresco.documentlibrary.inline-edit";
    
    private static final String FILE_UPLOAD_EVENT = "org.alfresco.documentlibrary.file-added";
    
    private static final String FILE_CREATE_EVENT = "activity.org.alfresco.documentlibrary.file-created";
    
    private static final String FILE_SHARE_EVENT = "activity.quickshare";
    
    private static final String FILE_DELETE_EVENT = "org.alfresco.documentlibrary.file-deleted";
    
    protected CamelContext camelContext;    
 
    ProducerTemplate template;

    private static String testPassword = DEFAULT_PASSWORD;
    protected String testUser;
    protected String siteName = "";
    
    //Activity1 = File Preview
    private static int site1NumberOfPreviewFiles = 8;
    private static int site2NumberOfPreviewFiles = 4;
    private static int site3NumberOfPreviewFiles = 6;
    private static int site4NumberOfPreviewFiles = 3;
    private static int site5NumberOfPreviewFiles = 2;    
    
    //Activity2 = File Download
    private static int site1NumberOfDownloadFiles = 2;
    private static int site2NumberOfDownloadFiles = 3;
    private static int site3NumberOfDownloadFiles = 5;
    private static int site4NumberOfDownloadFiles = 4;
    private static int site5NumberOfDownloadFiles = 7;    
    
    //Activity3 = File Edit
    private static int site1NumberOfEditFiles = 3;
    private static int site2NumberOfEditFiles = 5;
    private static int site3NumberOfEditFiles = 8;
    private static int site4NumberOfEditFiles = 6;
    private static int site5NumberOfEditFiles = 1;

    //Activity4 = File Upload
    private static int site1NumberOfUploadFiles = 4;
    private static int site2NumberOfUploadFiles = 6;
    private static int site3NumberOfUploadFiles = 3;
    private static int site4NumberOfUploadFiles = 2;
    private static int site5NumberOfUploadFiles = 5;  

    
    //Activity5 = File Create
    private static int site1NumberOfCreateFiles = 6;
    private static int site2NumberOfCreateFiles = 8;
    private static int site3NumberOfCreateFiles = 4;
    private static int site4NumberOfCreateFiles = 7;
    private static int site5NumberOfCreateFiles = 6;  
    
    //Activity6 = File Share
    private static int site1NumberOfShareFiles = 1;
    private static int site2NumberOfShareFiles = 2;
    private static int site3NumberOfShareFiles = 6;
    private static int site4NumberOfShareFiles = 4;
    private static int site5NumberOfShareFiles = 3; 
    
    //Activity7 = File Delete
    private static int site1NumberOfDeleteFiles = 5;
    private static int site2NumberOfDeleteFiles = 7;
    private static int site3NumberOfDeleteFiles = 2;
    private static int site4NumberOfDeleteFiles = 1;
    private static int site5NumberOfDeleteFiles = 4;
    
    //Number of days to subtract for time periods
    private static int intNumOfDaysPeriod1 = -5;
    private static int intNumOfDaysPeriod2 = -15;
    private static int intNumOfDaysPeriod3 = -45;
    private static int intNumOfDaysPeriod4 = -145;
    private static int intNumOfDaysPeriod5 = -245;
    private static int intNumOfDaysPeriod6 = -450;
    
    int [] timePeriods = { intNumOfDaysPeriod1, intNumOfDaysPeriod2, intNumOfDaysPeriod3, intNumOfDaysPeriod4, intNumOfDaysPeriod5, intNumOfDaysPeriod6 };
    

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        camelContext = new DefaultCamelContext();
        /**
        camelContext.addRoutes(new RouteBuilder() {
            public void configure() {
                from("activemq:alfresco.events.raw").to("browse:orderReceived");
            }
        });
        **/
        camelContext.addComponent("activemq:alfresco.events.raw", activeMQComponent("vm://localhost?broker.persistent=false"));
        template = camelContext.createProducerTemplate();
        camelContext.start();
        
        
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }
    
    @AfterClass(alwaysRun = true)
    public void stopCamel() 
    {
        // wait a bit and then stop camel context
        try
        {
            Thread.sleep(1000);
            camelContext.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Error during stopping camel context:" +  e);
        }       
    }
    
      
    @Test(groups = { "DataPrepSiteUsageReport" })
    public void dataPrep_SiteUsageReport_ALF_1533_1() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        String[] testUserInfo = new String[] { testUser };
        
        String siteName1 = getSiteName(testName) + "_1";
        String siteName2 = getSiteName(testName) + "_2";
        String siteName3 = getSiteName(testName) + "_3";
        String siteName4 = getSiteName(testName) + "_4";
        String siteName5 = getSiteName(testName) + "_5";
        String siteName6 = getSiteName(testName) + "_6";
        
        // Create test user
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);

        // Login as created user
        ShareUser.login(drone, testUser, testPassword);

        // Create Site1
        SiteUtil.createSite(drone, siteName1, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        // Create Site2
        SiteUtil.createSite(drone, siteName2, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        // Create Site3
        SiteUtil.createSite(drone, siteName3, AbstractUtils.SITE_VISIBILITY_PUBLIC);      
        // Create Site4
        SiteUtil.createSite(drone, siteName4, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        // Create Site5
        SiteUtil.createSite(drone, siteName5, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        // Create Site6
        SiteUtil.createSite(drone, siteName6, AbstractUtils.SITE_VISIBILITY_PUBLIC);
            
        
        String [][] sitesData = {{ siteName1, FILE_PREVIEW_EVENT, Integer.toString(site1NumberOfPreviewFiles) }, {siteName2, FILE_PREVIEW_EVENT, Integer.toString(site2NumberOfPreviewFiles) }, {siteName3, FILE_PREVIEW_EVENT, Integer.toString(site3NumberOfPreviewFiles) }, {siteName4, FILE_PREVIEW_EVENT, Integer.toString(site4NumberOfPreviewFiles)}, {siteName5, FILE_PREVIEW_EVENT, Integer.toString(site5NumberOfPreviewFiles)},
                                 { siteName1, FILE_DOWNLOAD_EVENT, Integer.toString(site1NumberOfDownloadFiles) }, {siteName2, FILE_DOWNLOAD_EVENT, Integer.toString(site2NumberOfDownloadFiles) }, {siteName3, FILE_DOWNLOAD_EVENT, Integer.toString(site3NumberOfDownloadFiles) }, {siteName4, FILE_DOWNLOAD_EVENT, Integer.toString(site4NumberOfDownloadFiles)}, {siteName5, FILE_DOWNLOAD_EVENT, Integer.toString(site5NumberOfDownloadFiles)},   
                                 { siteName1, FILE_EDIT_EVENT, Integer.toString(site1NumberOfEditFiles) }, {siteName2, FILE_EDIT_EVENT, Integer.toString(site2NumberOfEditFiles) }, {siteName3, FILE_EDIT_EVENT, Integer.toString(site3NumberOfEditFiles) }, {siteName4, FILE_EDIT_EVENT, Integer.toString(site4NumberOfEditFiles)}, {siteName5, FILE_EDIT_EVENT, Integer.toString(site5NumberOfEditFiles)},
                                 { siteName1, FILE_UPLOAD_EVENT, Integer.toString(site1NumberOfUploadFiles) }, {siteName2, FILE_UPLOAD_EVENT, Integer.toString(site2NumberOfUploadFiles) }, {siteName3, FILE_UPLOAD_EVENT, Integer.toString(site3NumberOfUploadFiles) }, {siteName4, FILE_UPLOAD_EVENT, Integer.toString(site4NumberOfUploadFiles)}, {siteName5, FILE_UPLOAD_EVENT, Integer.toString(site5NumberOfUploadFiles)},
                                 { siteName1, FILE_CREATE_EVENT, Integer.toString(site1NumberOfCreateFiles) }, {siteName2, FILE_CREATE_EVENT, Integer.toString(site2NumberOfCreateFiles) }, {siteName3, FILE_CREATE_EVENT, Integer.toString(site3NumberOfCreateFiles) }, {siteName4, FILE_CREATE_EVENT, Integer.toString(site4NumberOfCreateFiles)}, {siteName5, FILE_CREATE_EVENT, Integer.toString(site5NumberOfCreateFiles)},
                                 { siteName1, FILE_SHARE_EVENT, Integer.toString(site1NumberOfShareFiles) }, {siteName2, FILE_SHARE_EVENT, Integer.toString(site2NumberOfShareFiles) }, {siteName3, FILE_SHARE_EVENT, Integer.toString(site3NumberOfShareFiles) }, {siteName4, FILE_SHARE_EVENT, Integer.toString(site4NumberOfShareFiles)}, {siteName5, FILE_SHARE_EVENT, Integer.toString(site5NumberOfShareFiles)},
                                 { siteName1, FILE_DELETE_EVENT, Integer.toString(site1NumberOfDeleteFiles) }, {siteName2, FILE_DELETE_EVENT, Integer.toString(site2NumberOfDeleteFiles) }, {siteName3, FILE_DELETE_EVENT, Integer.toString(site3NumberOfDeleteFiles) }, {siteName4, FILE_DELETE_EVENT, Integer.toString(site4NumberOfDeleteFiles)}, {siteName5, FILE_DELETE_EVENT, Integer.toString(site5NumberOfDeleteFiles)}
                                };
        
        for(int i=0; i < sitesData.length; i++)
        {
            String siteName = sitesData[i][0];
            String activity = sitesData[i][1];
            String numberOfEvents = sitesData[i][2];
            
            for (int j=0; j < timePeriods.length; j++)
            {
                ShareUserReports.createHistoricEvents(testName, template, testUser, siteName, activity, Integer.parseInt(numberOfEvents), timePeriods[j]);
            }
        }
        
        //logout
        ShareUser.logout(drone);
        
    }
}
