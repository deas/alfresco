package org.alfresco.share.util;

import java.util.Calendar;
import java.util.Date;

import org.alfresco.events.types.ActivityEvent;
import org.alfresco.events.types.Event;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.testng.Assert;


/**
 * Utility for reports tests 
 * 
 * @author jcule
 *
 */
public class ShareUserReports extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(ShareUserReports.class);
    
    private static final String COPY_RULE = "Copy";

    private static final String MOVE_RULE = "Move";    
    
    /**
     * 
     * Publish upload events to the queue
     * 
     * @param username
     * @param siteName
     * @param eventType
     * @param numOfDays
     */
    public static void createHistoricEvents(String testName, ProducerTemplate template, String username, String siteName, String eventType, int numberOfUploadEvents, int numOfDays)
    {
        for (int i = 0; i < numberOfUploadEvents; i++)
        {
            String fileName = getFileName(testName + "_" + i + "." + "txt"); 
            Event event = new ActivityEvent(eventType, "a52fbbf9-a297-4fb5-aeea-8f5fe8087c7b", "alfresco.com", getEventDate(numOfDays), username, "e346bfa0-3177-4d64-b86d-05b433307d3b", siteName.toLowerCase(), "{http://www.alfresco.org/model/content/1.0}content", null, "{\"nodeRef\": \"workspace:\\/\\/SpacesStore\\/e346bfa0-3177-4d64-b86d-05b433307d3b\", \"title\": \"Filemiojkli-SiteContentReadsReportTest_0.txt\", \"page\": \"document-details?nodeRef=workspace:\\/\\/SpacesStore\\/e346bfa0-3177-4d64-b86d-05b433307d3b\"}", fileName, "text/plain", 600L, "UTF-8");
            template.sendBody("activemq:alfresco.events.raw", event);
        }
 
    }
    
    
    /**
     * Returns date of the event within the time period
     * 
     * @param numOfDays
     * @return
     */
    private static long getEventDate(int numOfDays)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        //subtract number of days
        cal.add(Calendar.DATE, numOfDays);
        Date date = cal.getTime();
        return date.getTime();
    }

    
    
    
    /**
     * Creates files in site's document library
     * 
     * @param numberOfFiles
     * @param siteName
     * @throws Exception
     */
    public static void createFilesInSite(WebDrone drone, String testName, int numberOfFiles, String siteName) throws Exception
    {
       
        // Create Files
        for (int i = 0; i <= numberOfFiles - 1; i++)
        {
            DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
            
            ContentDetails contentDetails = new ContentDetails();
            String fileName = getFileName(testName + "_" + i + "." + "txt");
            contentDetails.setName(fileName);
            contentDetails.setTitle(testName + " title");
            contentDetails.setDescription(testName + " description");
            contentDetails.setContent(testName + " content");
                        
            CreatePlainTextContentPage contentPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
            contentPage.create(contentDetails).render();
                
        }
    }

    /**
     * 
     * Creates files in a folder in site's document library
     * 
     * @param numberOfFiles
     * @param siteName
     * @param siteNumber
     * @throws Exception
     */
    public static void createFilesInSiteFolder(WebDrone drone, String testName, int numberOfFiles, String siteName, int siteNumber, String rule) throws Exception
    {
 
        //Create folder
        String folderName = getFileName(testName + "_" + siteNumber);         
        DocumentLibraryPage documentLibraryPage = ShareUserSitePage.createFolder(drone, folderName, folderName);
        
        //create rule if rule is set
        if (MOVE_RULE.equalsIgnoreCase(rule))
        {
            FolderRulesPage folderRulesPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManageRules().render();
            Assert.assertTrue(folderRulesPage.isPageCorrect(folderName));

            CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
            createRulePage.fillNameField("New Move Rule Name");
            createRulePage.fillDescriptionField("New Move Rule Description");

            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();

            //move rule
            actionSelectorEnterpImpl.selectMove(siteName, "Documents");

            FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
            Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName));
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        } 
        else if (COPY_RULE.equalsIgnoreCase(rule))
        {
            FolderRulesPage folderRulesPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManageRules().render();
            Assert.assertTrue(folderRulesPage.isPageCorrect(folderName));

            CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
            createRulePage.fillNameField("New Copy and Transform Rule Name");
            createRulePage.fillDescriptionField("New Copy and Transform Rule Description");

            ActionSelectorEnterpImpl actionSelectorEnterpImpl = createRulePage.getActionOptionsObj();

            //copy rule
            actionSelectorEnterpImpl.selectTransformAndCopy("XML", siteName, "Documents");

            FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
            Assert.assertTrue(folderRulesPageWithRules.isPageCorrect(folderName));
            documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
           
        }
        
        documentLibraryPage.selectFolder(folderName).render();
        
        // Create Files
        for (int i = 0; i <= numberOfFiles - 1; i++)
        {
            ContentDetails contentDetails = new ContentDetails();
            String fileName = getFileName(testName + "_" + i + "." + "txt"); 
            contentDetails.setName(fileName);
            contentDetails.setTitle(testName + " title");
            contentDetails.setDescription(testName + " description");
            contentDetails.setContent(testName + " content");                      
            CreatePlainTextContentPage contentPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
            contentPage.create(contentDetails).render();
            ShareUserSitePage.navigateToFolder(drone, folderName);
                
        }
        
    }       
    
}
