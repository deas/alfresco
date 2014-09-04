package org.alfresco.share.reports;

import javax.jms.ConnectionFactory;

import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserReports;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * 
 * Site Content Reads Report tests
 * 
 * @author jcule
 *
 */
@Listeners(FailedTestListener.class)
public class SiteContentReadsReportTest extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(SiteContentReadsReportTest.class);
    
    protected CamelContext camelContext;    
    
    ProducerTemplate template;

    private static String testPassword = DEFAULT_PASSWORD;
    protected String testUser;
    protected String siteName = "";

    //number of files to create in each site's document library
    private static int site1NumberOfCreatedFiles = 10;
    private static int site2NumberOfCreatedFiles = 8;
    private static int site3NumberOfCreatedFiles = 6;
    private static int site4NumberOfCreatedFiles = 4;
    private static int site5NumberOfCreatedFiles = 2;
    
    //number of files to preview in each site's document library
    private static int site1NumberOfPreviewedFiles = 7;
    private static int site2NumberOfPreviewedFiles = 5;
    private static int site3NumberOfPreviewedFiles = 3;
    private static int site4NumberOfPreviewedFiles = 2;
    private static int site5NumberOfPreviewedFiles = 1;  
    
    //Number of days to subtract for time periods
    private static int intNumOfDaysPeriod1 = -15;
    private static int intNumOfDaysPeriod2 = -45;
    
    private static final String WEB_PREVIEW_EVENT = "org.alfresco.documentlibrary.file-added";
        
    private static final String COPY_RULE = "Copy";

    private static final String MOVE_RULE = "Move";
    
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();

        camelContext = new DefaultCamelContext();
        
       /**
        camelContext.addRoutes(new RouteBuilder() {
            public void configure() {
                from("activemq:alfresco.events.raw").to("file:target/reports");
            }
        });
        **/
                
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        camelContext.addComponent("activemq:alfresco.events.raw", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
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
    
    /**
     * DataPreparation method - AONE_32430
     * 
     * Files created in site's document library
     * 
     * 1) Create test user
     * 2) Login as created user
     * 3) Create Site1 
     * 4) create files in Site1 document library
     * 5) create preview events from 15 days ago on the queue
     * 6) create preview events from 45 days ago on the queue
     * 7) repeat steps 3, 4, 5, 6 for site2, site3, site4, site5
     * 8) create site6
     * 9) test user logs out
     *       
     * @throws Exception
     */
    @Test(groups = { "DataPrepSiteContentReadsReport", "EnterpriseOnly" })
    public void dataPrep_SiteContentReadsReport_AONE_32430() throws Exception
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
        
        //create files in Site1 document library
        ShareUserReports.createFilesInSite(drone, testName, site1NumberOfCreatedFiles, siteName1);
        
        //create preview events from 15 days ago
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName1, WEB_PREVIEW_EVENT, site1NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName1, WEB_PREVIEW_EVENT, site1NumberOfPreviewedFiles, intNumOfDaysPeriod2);
        
        // Create Site2
        SiteUtil.createSite(drone, siteName2, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //create files in Site2 document library
        ShareUserReports.createFilesInSite(drone, testName, site2NumberOfCreatedFiles, siteName2);
        
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName2, WEB_PREVIEW_EVENT, site2NumberOfPreviewedFiles, intNumOfDaysPeriod1);
               
        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName2, WEB_PREVIEW_EVENT, site2NumberOfPreviewedFiles, intNumOfDaysPeriod2);
           
        // Create Site3
        SiteUtil.createSite(drone, siteName3, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //create files in Site3 document library
        ShareUserReports.createFilesInSite(drone, testName, site3NumberOfCreatedFiles, siteName3);
        
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName3, WEB_PREVIEW_EVENT, site3NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName3, WEB_PREVIEW_EVENT, site3NumberOfPreviewedFiles, intNumOfDaysPeriod2);
  
        // Create Site4
        SiteUtil.createSite(drone, siteName4, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //create files in Site4 document library
        ShareUserReports.createFilesInSite(drone, testName, site4NumberOfCreatedFiles, siteName4);
        
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName4, WEB_PREVIEW_EVENT, site4NumberOfPreviewedFiles, intNumOfDaysPeriod1);
               
        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName4, WEB_PREVIEW_EVENT, site4NumberOfPreviewedFiles, intNumOfDaysPeriod2);
       
        // Create Site5
        SiteUtil.createSite(drone, siteName5, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //create files in Site5 document library
        ShareUserReports.createFilesInSite(drone, testName, site5NumberOfCreatedFiles, siteName5);
        
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName5, WEB_PREVIEW_EVENT, site5NumberOfPreviewedFiles, intNumOfDaysPeriod1);
               
        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName5, WEB_PREVIEW_EVENT, site5NumberOfPreviewedFiles, intNumOfDaysPeriod2);   
        
        // Create Site6
        SiteUtil.createSite(drone, siteName6, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //logout
        ShareUser.logout(drone);

    }    
    
    /**
     * DataPreparation method - AONE_32440
     * 
     * Files created in site's document library folder without a rule applied
     * 
     * 1) Create test user
     * 2) Login as created user
     * 3) Create Site1 and folder without the rule applied in the site's document library  
     * 4) create files in the folder created in Site1
     * 5) create preview events from 15 days ago on the queue
     * 6) create preview events from 45 days ago on the queue
     * 7) repeat steps 3, 4, 5, 6 for site2, site3, site4, site5
     * 8) create site6
     * 9) test user logs out      
     * 
     * @throws Exception
     */
    @Test(groups = { "DataPrepSiteContentReadsReport", "EnterpriseOnly" })
    public void dataPrep_SiteContentReadsReport_AONE_32440() throws Exception
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
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site1NumberOfCreatedFiles, siteName1, 1, "");
     
        //create preview events from 15 days ago
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName1, WEB_PREVIEW_EVENT, site1NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName1, WEB_PREVIEW_EVENT, site1NumberOfPreviewedFiles, intNumOfDaysPeriod2);
      
        // Create Site2
        SiteUtil.createSite(drone, siteName2, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site2NumberOfCreatedFiles, siteName2, 1, "");
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName2, WEB_PREVIEW_EVENT, site2NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName2, WEB_PREVIEW_EVENT, site2NumberOfPreviewedFiles, intNumOfDaysPeriod2);

        // Create Site3
        SiteUtil.createSite(drone, siteName3, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site3NumberOfCreatedFiles, siteName3, 1, "");
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName3, WEB_PREVIEW_EVENT, site3NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName3, WEB_PREVIEW_EVENT, site3NumberOfPreviewedFiles, intNumOfDaysPeriod2);
   
        // Create Site4
        SiteUtil.createSite(drone, siteName4, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site4NumberOfCreatedFiles, siteName4, 1, "");
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName4, WEB_PREVIEW_EVENT, site4NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName4, WEB_PREVIEW_EVENT, site4NumberOfPreviewedFiles, intNumOfDaysPeriod2);       
        
        // Create Site5
        SiteUtil.createSite(drone, siteName5, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site5NumberOfCreatedFiles, siteName5, 1, "");
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName5, WEB_PREVIEW_EVENT, site5NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName5, WEB_PREVIEW_EVENT, site5NumberOfPreviewedFiles, intNumOfDaysPeriod2);  
        
        // Create Site6
        SiteUtil.createSite(drone, siteName6, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //logout
        ShareUser.logout(drone);
    }
     
    /**
     * DataPreparation method - AONE_32450
     * 
     * Files created in site's document library folder with a Move rule applied
     * 
     * 1) Create test user
     * 2) Login as created user
     * 3) Create Site1 and folder with the Move rule applied in the site's document library  
     * 4) create files in the folder created in Site1
     * 5) create preview events from 15 days ago on the queue
     * 6) create preview events from 45 days ago on the queue
     * 7) repeat steps 3, 4, 5, 6 for site2, site3, site4, site5
     * 8) create site6
     * 9) test user logs out      
     * 
     * @throws Exception
     */    
    @Test(groups = { "DataPrepSiteContentReadsReport", "EnterpriseOnly" })
    public void dataPrep_SiteContentReadsReport_AONE_32450() throws Exception
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
        
        //create folder with move rule applied to it and files in the folder
        ShareUserReports.createFilesInSiteFolder(drone, testName, site1NumberOfCreatedFiles, siteName1, 1, MOVE_RULE);
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName1, WEB_PREVIEW_EVENT, site1NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName1, WEB_PREVIEW_EVENT, site1NumberOfPreviewedFiles, intNumOfDaysPeriod2);
      
        // Create Site2
        SiteUtil.createSite(drone, siteName2, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site2NumberOfCreatedFiles, siteName2, 1, MOVE_RULE);
     
        //create preview events from 15 days ago
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName2, WEB_PREVIEW_EVENT, site2NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName2, WEB_PREVIEW_EVENT, site2NumberOfPreviewedFiles, intNumOfDaysPeriod2);

        // Create Site3
        SiteUtil.createSite(drone, siteName3, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site3NumberOfCreatedFiles, siteName3, 1, MOVE_RULE);
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName3, WEB_PREVIEW_EVENT, site3NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName3, WEB_PREVIEW_EVENT, site3NumberOfPreviewedFiles, intNumOfDaysPeriod2);
  
        // Create Site4
        SiteUtil.createSite(drone, siteName4, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site4NumberOfCreatedFiles, siteName4, 1, MOVE_RULE);
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName4, WEB_PREVIEW_EVENT, site4NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName4, WEB_PREVIEW_EVENT, site4NumberOfPreviewedFiles, intNumOfDaysPeriod2);       
        
        // Create Site5
        SiteUtil.createSite(drone, siteName5, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site5NumberOfCreatedFiles, siteName5, 1, MOVE_RULE);
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName5, WEB_PREVIEW_EVENT, site5NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName5, WEB_PREVIEW_EVENT, site5NumberOfPreviewedFiles, intNumOfDaysPeriod2);  
        
        // Create Site6
        SiteUtil.createSite(drone, siteName6, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //logout
        ShareUser.logout(drone);
        
    }
    
    /**
     * DataPreparation method - AONE_32460
     * 
     * Files created in site's document library folder with a Copy and Transform rule applied
     * 
     * 1) Create test user
     * 2) Login as created user
     * 3) Create Site1 and folder with the Copy and Transform rule applied in the site's document library  
     * 4) create files in the folder created in Site1
     * 5) create preview events from 15 days ago on the queue
     * 6) create preview events from 45 days ago on the queue
     * 7) repeat steps 3, 4, 5, 6 for site2, site3, site4, site5
     * 8) create site6
     * 9) test user logs out      
     * 
     * @throws Exception
     */    
    @Test(groups = { "DataPrepSiteContentReadsReport", "EnterpriseOnly" })
    public void dataPrep_SiteContentReadsReport_AONE_32460() throws Exception
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
        
        //create folder with move rule applied to it and files in the folder
        ShareUserReports.createFilesInSiteFolder(drone, testName, site1NumberOfCreatedFiles, siteName1, 1, COPY_RULE);
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName1, WEB_PREVIEW_EVENT, site1NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName1, WEB_PREVIEW_EVENT, site1NumberOfPreviewedFiles, intNumOfDaysPeriod2);
      
        // Create Site2
        SiteUtil.createSite(drone, siteName2, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site2NumberOfCreatedFiles, siteName2, 1, COPY_RULE);
     
        //create preview events from 15 days ago
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName2, WEB_PREVIEW_EVENT, site2NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName2, WEB_PREVIEW_EVENT, site2NumberOfPreviewedFiles, intNumOfDaysPeriod2);

        // Create Site3
        SiteUtil.createSite(drone, siteName3, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site3NumberOfCreatedFiles, siteName3, 1, COPY_RULE);
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName3, WEB_PREVIEW_EVENT, site3NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName3, WEB_PREVIEW_EVENT, site3NumberOfPreviewedFiles, intNumOfDaysPeriod2);
  
        // Create Site4
        SiteUtil.createSite(drone, siteName4, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site4NumberOfCreatedFiles, siteName4, 1, COPY_RULE);
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName4, WEB_PREVIEW_EVENT, site4NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName4, WEB_PREVIEW_EVENT, site4NumberOfPreviewedFiles, intNumOfDaysPeriod2);       
        
        // Create Site5
        SiteUtil.createSite(drone, siteName5, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        ShareUserReports.createFilesInSiteFolder(drone, testName, site5NumberOfCreatedFiles, siteName5, 1, COPY_RULE);
     
        //create preview events from 15 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName5, WEB_PREVIEW_EVENT, site5NumberOfPreviewedFiles, intNumOfDaysPeriod1);

        //create preview events from 45 days ago on the queue
        ShareUserReports.createHistoricEvents(testName, template, testUser, siteName5, WEB_PREVIEW_EVENT, site5NumberOfPreviewedFiles, intNumOfDaysPeriod2);  
        
        // Create Site6
        SiteUtil.createSite(drone, siteName6, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        //logout
        ShareUser.logout(drone);
        
    }
  
}
