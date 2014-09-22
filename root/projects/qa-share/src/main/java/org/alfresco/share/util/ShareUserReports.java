package org.alfresco.share.util;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.alfresco.events.types.ActivityEvent;
import org.alfresco.events.types.Event;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.api.CreateUserAPI;
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
    
    /**
     * Utility to create events in share for adhoc reports
     * 
     * @param drone
     * @param testName
     * @throws Exception
     */
    public static void userShareInteractions(WebDrone drone, String testName) throws Exception
    {
        String testUser = getUserNameForDomain(testName, DOMAIN_FREE);
        String[] testUserInfo = new String[] { testUser };
        String siteName = getSiteName(testName);

        // Create test user
        CreateUserAPI.createActivateUserAsTenantAdmin(drone, ADMIN_USERNAME, testUserInfo);
        
        // Login as created test user
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        
        // test user creates site
        SiteUtil.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);
        
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        
        //and a folder in sites document library
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        
        //add comment to folder
        ShareUserSitePage.addComment(drone, folderName, "folderComment");
        
        
        //create text file in the folder - file-previewed 
        String fileName = getFileName(testName) + "-" + System.currentTimeMillis();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);

        ShareUserSitePage.createContentInFolder(drone, contentDetails, ContentType.PLAINTEXT, siteName, folderName);
       
        ShareUser.openDocumentLibrary(drone);
        
        //uploads a file in the folder
        String fileName1 = getFileName(testName) + System.currentTimeMillis() + "_1" + ".txt";
        File file1 = newFile(fileName1, fileName1);
        ShareUserSitePage.uploadFile(drone, file1);
       
        //add comment to file - file-previewed
        ShareUserSitePage.addComment(drone, fileName1, "fileComment");
        
        ShareUser.openDocumentLibrary(drone);
        
        //like file
        ShareUserSitePage.likeContent(drone, fileName1);
      
        //share file
        ShareUserSitePage.getFileDirectoryInfo(drone, fileName1).clickShareLink().render();
        
        ContentDetails newContentDetails = new ContentDetails();
        newContentDetails.setContent(testName);
        
        DocumentLibraryPage documentLibPage = ShareUser.openDocumentLibrary(drone);
        
        //edit document inline
        ShareUserSitePage.editTextDocumentInLine(drone, fileName1, newContentDetails).render();
      
        //select all
        documentLibPage.getNavigation().selectAll().render();

        //delete all
        ConfirmDeletePage confirmDeletePage = documentLibPage.getNavigation().selectDelete().render();
        confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete).render();
       
        // add user with write permissions to write to the site
        ShareUserMembers.inviteUserToSiteWithRole(drone, testUser, testName, siteName, UserRole.COLLABORATOR);
        
      
        //change the user role
        ShareUserMembers.assignRoleToSiteMember(drone, testName, siteName, UserRole.CONTRIBUTOR);
        
        // Inviting user logs out
        ShareUser.logout(drone);
        
        //Invited User logs in
        ShareUser.login(drone, testName, DEFAULT_PASSWORD);
       
        //user leaves site
        ShareUserMembers.userRequestToLeaveSite(drone, siteName);
        
    }
    
}
