/**
 * 
 */
package org.alfresco.share.cloud;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alfresco.po.share.search.SearchResult;
import org.alfresco.po.share.search.SearchResultItem;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.search.SearchKeys;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author cbairaajoni
 * 
 */
@Listeners(FailedTestListener.class)
@Test(groups={"Staging", "CloudOnly"})
public class AdvanceSearchCRMTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(AdvanceSearchCRMTest.class);

    private String salesforceUserName = null;
    private String salesforcePassword = null;
    
    private static String accountName = "helloaccount";
    private static String caseNameOrNumber = "00001006";
    private static String accountIdentifier = "001b0000009aPo1AAE";
    private static String contractNameOrNumber = "00000104";
    private static String oppName = "helloaccountoppor";
    private static String basic_Search_contractNumber = "crm:contractNumber:00000104";
    // TODO: Chiran: Remove if not used
    private static String basic_Search_caseNumber = "crm:caseNumber:00001006";
    private static String basic_Search_accountIdentifier = "crm:accountId:001b0000009aPo1AAE";
    private static String basic_Search_oppName = "crm:opportunityName:helloaccountoppor";
    private static String siteName = "salesforce-public"; 
    private static String attachments_Folder = "Attachments";
    private static String account_Content = "sample.doc";
    private static String contract_Content = "permission screen (Working Copy).png";
    private static String case_Content = "Untitled Document.docx";
    private static String oppo_Content = "ods.ods";
    
    Map<String, String> keyWordSearchText = new HashMap<String, String>();

    @Override
    @BeforeClass(alwaysRun=true)
    public void setup() throws Exception
    {
        super.setup();
        readProperties();
        logger.info("Running Advance Search CRM tests for Staging.");
    }

    /**
     * Reads the properties from properties file and assings to contants.
     * 
     */
    private void readProperties()
    {

        Properties properties = new Properties();
        
        try
        {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("salesforce.properties"));
            
            logger.info("Salesforce properties : " + properties.entrySet());
            
            salesforceUserName = properties.getProperty("salesforce_username");
            salesforcePassword = properties.getProperty("salesforce_password");

            accountName = properties.getProperty("salesforce_crm_advsearch_accountName");
            caseNameOrNumber = properties.getProperty("salesforce_crm_advsearch_caseNameOrNumber");
            accountIdentifier = properties.getProperty("salesforce_crm_advsearch_accountIdentifier");
            contractNameOrNumber = properties.getProperty("salesforce_crm_advsearch_contractNameOrNumber");
            oppName = properties.getProperty("salesforce_crm_advsearch_oppName");
            basic_Search_contractNumber = properties.getProperty("salesforce_crm_advsearch_basic_Search_contractNumber");
            basic_Search_caseNumber = properties.getProperty("salesforce_crm_advsearch_basic_Search_caseNumber");
            basic_Search_accountIdentifier = properties.getProperty("salesforce_crm_advsearch_basic_Search_accountIdentifier");
            basic_Search_oppName = properties.getProperty("salesforce_crm_advsearch_basic_Search_oppName");
            siteName = properties.getProperty("salesforce_crm_advsearch_siteName");
            attachments_Folder = properties.getProperty("salesforce_crm_advsearch_attachments_Folder");
            account_Content = properties.getProperty("salesforce_crm_advsearch_account_Content");
            contract_Content = properties.getProperty("salesforce_crm_advsearch_contract_Content");
            case_Content = properties.getProperty("salesforce_crm_advsearch_case_Content");
            oppo_Content = properties.getProperty("salesforce_crm_advsearch_oppo_Content");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Properties file not found for the given input: " + this, e);
        }
        catch (NullPointerException ne)
        {
            logger.error("No matching properties file was found");
        }

    }
    
    /**
     * Test - Cloud-309:Account Search based on Account name
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search CRM form</li>
     * <li>In the Account Name Search text of the CRM search form enter
     * helloaccount</li>
     * <li>Validate the search results are returned 4 rows of results</li>
     * <li>Validate the search results, any one of the result item should be
     * from helloaccount folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13869() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Account Name
        keyWordSearchText.put(SearchKeys.ACCOUNT_NAME.getSearchKeys(), accountName);
        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);

        Assert.assertTrue(ShareUserSearchPage.isSearchItemAvailable(drone, contract_Content));
        Assert.assertTrue(results.size() == 4);
        
        List<String> folders = new LinkedList<String>();
       
        //Finding the specific result item.
        for(SearchResult searchItem : results)
        {
            if(searchItem.getTitle().equalsIgnoreCase(account_Content))
            {
                // Retrieving the folderNames from folderPath of the first resultItem.
                folders = ((SearchResultItem) searchItem).getFolderNamesFromContentPath();
            }
        }
        Assert.assertTrue(folders.size() > 0);
        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(accountName));
    }

    /**
     * Test - Cloud-2838:Account Search based on Account identifier
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard access the Advance Search CRM form</li>
     * <li>In the Account Identifier Search text of the CRM search form enter
     * 001b0000009aPo1AAE</li>
     * <li>Validate the search results are returned 4 rows of results</li>
     * <li>Validate the search results, any one of the result item should be
     * from helloaccount folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13870() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Account Identifier
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys(), accountIdentifier);
        
        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() == 4);

        List<String> folders = new LinkedList<String>();
        
        //Finding the specific result item.
        for(SearchResult searchItem : results)
        {
            if(searchItem.getTitle().equalsIgnoreCase(account_Content))
            {
                // Retrieving the folderNames from folderPath of the first resultItem.
                folders = ((SearchResultItem) searchItem).getFolderNamesFromContentPath();
            }
        }
        Assert.assertTrue(folders.size() > 0);
        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(accountName));
    }

    /**
     * Test - Cloud-310:Account Search based on both name and identifier
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Account Identifier Search text of the CRM search form, enter
     * 001b0000009aPo1AAE</li>
     * <li>In the Account Name Search text of the CRM search form enter
     * helloaccount</li>
     * <li>Validate the search results are returned 4 rows of results</li>
     * <li>Validate the search results, any one of the result item should be
     * from helloaccount folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13871() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Account Identifier and Account Name
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys(), accountIdentifier);
        keyWordSearchText.put(SearchKeys.ACCOUNT_NAME.getSearchKeys(), accountName);

        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() == 4);

        List<String> folders = new LinkedList<String>();
        
        //Finding the specific result item.
        for(SearchResult searchItem : results)
        {
            if(searchItem.getTitle().equalsIgnoreCase(account_Content))
            {
                // Retrieving the folderNames from folderPath of the first resultItem.
                folders = ((SearchResultItem) searchItem).getFolderNamesFromContentPath();
            }
        }
        Assert.assertTrue(folders.size() > 0);
        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(accountName));
    }

    /**
     * Test - Cloud-2846:Oppor Search based on oppor name
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Opportunity Name Search text of the CRM search form, enter
     * helloaccountoppor</li>
     * <li>Validate the search results, any one of the result item should be
     * from helloaccountoppor folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13872() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Opportunity Name
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.OPPORTUNITY_NAME.getSearchKeys(), oppName);
        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);

        // Retrieving the folderNames from folderPath of the first resultItem.
        List<String> folders = ((SearchResultItem) results.get(0)).getFolderNamesFromContentPath();
        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(oppName));
    }

    /**
     * Test - Cloud-2847:Contract Search based on contract number
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Contract Number Search text of the CRM search form, enter
     * 00000104</li>
     * <li>Validate the search results, any one of the result item should be
     * from 00000104 folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13873() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Contract Number
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.CONTRACT_NUMBER.getSearchKeys(), contractNameOrNumber);
        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);

        // Retrieving the folderNames from folderPath of the first resultItem.
        List<String> folders = ((SearchResultItem) results.get(0)).getFolderNamesFromContentPath();
        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(contractNameOrNumber));
    }

    /**
     * Test - Cloud-2848:Contract Search based on contract name
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Contract Name Search text of the CRM search form, enter
     * 00000104</li>
     * <li>Validate the search results, any one of the result item should be
     * from 00000104 folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13874() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Contract Name
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.CONTRACT_NAME.getSearchKeys(), contractNameOrNumber);
        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);

        // Retrieving the folderNames from folderPath of the first resultItem.
        List<String> folders = ((SearchResultItem) results.get(0)).getFolderNamesFromContentPath();
        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(contractNameOrNumber));
    }

    /**
     * Test - Cloud-2849:Contract Search based on both
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Contract Name Search text of the CRM search form, enter
     * 00000104</li>
     * <li>In the Contract Number Search text of the CRM search form, enter
     * 00000104</li>
     * <li>Validate the search results, any one of the result item should be
     * from 00000104 folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13875() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Contract Name amd Contract Number
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.CONTRACT_NAME.getSearchKeys(), contractNameOrNumber);
        keyWordSearchText.put(SearchKeys.CONTRACT_NUMBER.getSearchKeys(), contractNameOrNumber);

        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);

        // Retrieving the folderNames from folderPath of the first resultItem.
        List<String> folders = ((SearchResultItem) results.get(0)).getFolderNamesFromContentPath();
        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(contractNameOrNumber));
    }

    /**
     * Test - Cloud-2850:Case Search based on Case number
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Case Number Search text of the CRM search form, enter 00001006
     * </li>
     * <li>Validate the search results, any one of the result item should be
     * from 00001006 folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13876() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Case Number
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.CASE_NUMBER.getSearchKeys(), caseNameOrNumber);
        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);

        // Retrieving the folderNames from folderPath of the first resultItem.
        List<String> folders = ((SearchResultItem) results.get(0)).getFolderNamesFromContentPath();
        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(caseNameOrNumber));
    }

    /**
     * Test - AONE-12996:Case Search based on Case name
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Case Name Search text of the CRM search form, enter 00001006</li>
     * <li>Validate the search results, any one of the result item should be
     * from 00001006 folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13877() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Case Name
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.CASE_NAME.getSearchKeys(), caseNameOrNumber);
        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);

        // Retrieving the folderNames from folderPath of the first resultItem.
        List<String> folders = ((SearchResultItem) results.get(0)).getFolderNamesFromContentPath();
        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(caseNameOrNumber));
    }

    /**
     * Test - AONE-12997:Case Search based on both
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Case Name Search text of the CRM search form, enter 00001006</li>
     * <li>In the Case Number Search text of the CRM search form, enter 00001006
     * </li>
     * <li>Validate the search results, any one of the result item should be
     * from 00001006 folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13878() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Case Name and Number
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.CASE_NAME.getSearchKeys(), caseNameOrNumber);
        keyWordSearchText.put(SearchKeys.CASE_NUMBER.getSearchKeys(), caseNameOrNumber);

        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);

        // Retrieving the folderNames from folderPath of the first resultItem.
        List<String> folders = ((SearchResultItem) results.get(0)).getFolderNamesFromContentPath();
        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(caseNameOrNumber));
    }

    /**
     * Test - AONE-12998:Combination - Account and Opportunity
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Account Name Search text of the CRM search form, enter
     * helloaccount</li>
     * <li>In the Opportunity Name Search text of the CRM search form, enter
     * helloaccountoppor</li>
     * <li>In the Account Number Search text of the CRM search form, enter
     * 001b0000009aPo1AAE</li>
     * <li>Validate the search results is same for all combinations of search
     * and any one of the result item should be from helloaccountoppor folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13879() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Account Name and Opportunity Name
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.ACCOUNT_NAME.getSearchKeys(), accountName);
        keyWordSearchText.put(SearchKeys.OPPORTUNITY_NAME.getSearchKeys(), oppName);

        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(oppo_Content));

        // Advance CRM Search with Account Name, Opportunity Name and Account
        // Identifier.
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.ACCOUNT_NAME.getSearchKeys(), accountName);
        keyWordSearchText.put(SearchKeys.OPPORTUNITY_NAME.getSearchKeys(), oppName);
        keyWordSearchText.put(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys(), accountIdentifier);

        results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(oppo_Content));

        // Retrieving the folderNames from folderPath of the first resultItem.
        List<String> folders = ((SearchResultItem) results.get(0)).getFolderNamesFromContentPath();

        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(oppName));

        // Advance CRM Search with Opportunity Name and Account Identifier.
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.OPPORTUNITY_NAME.getSearchKeys(), oppName);
        keyWordSearchText.put(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys(), accountIdentifier);

        results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(oppo_Content));

        // Advance CRM Search with Opportunity Name and Account Identifier.
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.OPPORTUNITY_NAME.getSearchKeys(), oppName);
        keyWordSearchText.put(SearchKeys.ACCOUNT_NAME.getSearchKeys(), accountName);

        results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(oppo_Content));
    }
    
    /**
     * Test - AONE-12999:Combination - Account and Contract
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Account Identifier Search text of the CRM search form, enter
     * 001b0000009aPo1AAE</li>
     * <li>In the Account Name Search text of the CRM search form, enter
     * helloaccount</li>
     * <li>In the Contract Number Search text of the CRM search form, enter
     * 00000104</li>
     * <li>In the Contract Name Search text of the CRM search form, enter
     * 00000104</li>
     * <li>Validate the search results is same for all combinations of search
     * and any one of the result item should be from helloaccountoppor folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13880() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Account Name and Contract Number
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.ACCOUNT_NAME.getSearchKeys(), accountName);
        keyWordSearchText.put(SearchKeys.CONTRACT_NUMBER.getSearchKeys(), contractNameOrNumber);

        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(contract_Content));

        // Advance CRM Search with Account Name, Identifier, Contract Number and Contract Name.
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.ACCOUNT_NAME.getSearchKeys(), accountName);
        keyWordSearchText.put(SearchKeys.CONTRACT_NUMBER.getSearchKeys(), contractNameOrNumber);
        keyWordSearchText.put(SearchKeys.CONTRACT_NAME.getSearchKeys(), contractNameOrNumber);
        keyWordSearchText.put(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys(), accountIdentifier);

        results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(contract_Content));

        // Retrieving the folderNames from folderPath of the first resultItem.
        List<String> folders = ((SearchResultItem) results.get(0)).getFolderNamesFromContentPath();

        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(contractNameOrNumber));

        // Advance CRM Search with Account Identifier and Contract Name.
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.CONTRACT_NAME.getSearchKeys(), contractNameOrNumber);
        keyWordSearchText.put(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys(), accountIdentifier);

        results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(contract_Content));
        
        // Advance CRM Search with Account Identifier and Contract Number.
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.CONTRACT_NUMBER.getSearchKeys(), contractNameOrNumber);
        keyWordSearchText.put(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys(), accountIdentifier);

        results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(contract_Content));
    }
    
    /**
     * Test - AONE-13000:Combination - Account and Case
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Account Identifier Search text of the CRM search form, enter
     * 001b0000009aPo1AAE</li>
     * <li>In the Account Name Search text of the CRM search form, enter
     * helloaccount</li>
     * <li>In the Case Number Search text of the CRM search form, enter
     * 00001006</li>
     * <li>In the Case Name Search text of the CRM search form, enter
     * 00001006</li>
     * <li>Validate the search results is same for all combinations of search
     * and any one of the result item should be from helloaccountoppor folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13881() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Account Name and Contract Number
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.ACCOUNT_NAME.getSearchKeys(), accountName);
        keyWordSearchText.put(SearchKeys.CASE_NUMBER.getSearchKeys(), caseNameOrNumber);

        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(case_Content));

        // Advance CRM Search with Account Name, Identifier, Case Number and Case Name.
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.ACCOUNT_NAME.getSearchKeys(), accountName);
        keyWordSearchText.put(SearchKeys.CASE_NAME.getSearchKeys(), caseNameOrNumber);
        keyWordSearchText.put(SearchKeys.CASE_NUMBER.getSearchKeys(), caseNameOrNumber);
        keyWordSearchText.put(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys(), accountIdentifier);

        results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(case_Content));

        // Retrieving the folderNames from folderPath of the first resultItem.
        List<String> folders = ((SearchResultItem) results.get(0)).getFolderNamesFromContentPath();

        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(caseNameOrNumber));

        // Advance CRM Search with Account Identifier and Case Name.
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.CASE_NAME.getSearchKeys(), caseNameOrNumber);
        keyWordSearchText.put(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys(), accountIdentifier);

        results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(case_Content));
        
        // Advance CRM Search with Account Identifier and Case Number.
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.CASE_NUMBER.getSearchKeys(), caseNameOrNumber);
        keyWordSearchText.put(SearchKeys.ACCOUNT_IDENTIFIER.getSearchKeys(), accountIdentifier);

        results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.size() == 1);
        Assert.assertTrue(results.get(0).getTitle().equalsIgnoreCase(case_Content));
    }
    
    /**
     * Test - AONE-13001:Negative test case for combination
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Oppo Name Search text of the CRM search form, enter
     * helloaccount</li>
     * <li>In the Contract Name Search text of the CRM search form, enter
     * 00000104</li>
     * <li>In the Case Name Search text of the CRM search form, enter
     * 00001006</li>
     * <li>Validate the search results is zero.</li>
     * </ul>
     */
    @Test
    public void AONE_13882() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with Opportunity Name and Contract Name
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.OPPORTUNITY_NAME.getSearchKeys(), oppName);
        keyWordSearchText.put(SearchKeys.CONTRACT_NAME.getSearchKeys(), contractNameOrNumber);

        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() == 0);

        // Advance CRM Search with Contract Name and Case Name.
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.CASE_NAME.getSearchKeys(), caseNameOrNumber);
        keyWordSearchText.put(SearchKeys.CONTRACT_NAME.getSearchKeys(), contractNameOrNumber);

        results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() == 0);
    }
    
    /**
     * Test - AONE-13002:Keyword search for text field
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Advance Search CRM form</li>
     * <li>In the Keyword Search text of the CRM search form, enter
     * crm:accountId:001b0000009aPo1AAE</li>
     * <li>Validate the search results are 4 rows and one result item is from helloaccount folder.</li>
     * </ul>
     */
    @Test
    public void AONE_13883() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance CRM Search with keyword
        keyWordSearchText.clear();
        keyWordSearchText.put(SearchKeys.KEYWORD.getSearchKeys(), basic_Search_accountIdentifier);

        List<SearchResult> results = ShareUserSearchPage.advanceSearchForCRM(drone, keyWordSearchText);
        Assert.assertTrue(results.size() == 4);

        List<String> folders = new LinkedList<String>();
        
        //Finding the specific result item.
        for(SearchResult searchItem : results)
        {
            if(searchItem.getTitle().equalsIgnoreCase(account_Content))
            {
                // Retrieving the folderNames from folderPath of the first resultItem.
                folders = ((SearchResultItem) searchItem).getFolderNamesFromContentPath();
            }
        }
        Assert.assertTrue(folders.size() > 0);
        Assert.assertTrue(folders.get(0).equalsIgnoreCase(attachments_Folder));
        Assert.assertTrue(folders.get(1).equalsIgnoreCase(accountName));
    }
    
    /**
     * Test - AONE-13003: Basic Search for CRM property
     * <ul>
     * <li>Login</li>
     * <li>From My Dashboard, access the Basic Search form</li>
     * <li>In the Search text of the basic search form, enter
     * case name or number</li>
     * <li>Validate the search results are returned.</li>
     * <li>In the Search text of the basic search form, enter
     * contract property</li>
     * <li>Validate that only one search result item is returned.</li>
     * <li>In the Search text of the basic search form, enter
     * account property</li>
     * <li>Validate that only 4 search result items are returned.</li>
     * <li>In the Search text of the basic search form, enter
     * opportunity property</li>
     * <li>Validate that only one search result item is returned.</li>
     * </ul>
     */
    @Test
    public void AONE_13884() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Advance Basic Search with caseNameOrNumber      
        List<SearchResult> results = ShareUserSearchPage.basicSearch(drone, caseNameOrNumber, false);
        Assert.assertTrue(results.size() > 0);
        
        // Advance Basic Search with caseNameOrNumber
        results = ShareUserSearchPage.basicSearch(drone, basic_Search_contractNumber, false);
        Assert.assertTrue(results.size() == 1);
        
        // Advance Basic Search with caseNameOrNumber
        results = ShareUserSearchPage.basicSearch(drone, basic_Search_accountIdentifier, false);
        Assert.assertTrue(results.size() == 4);
        
        // Advance Basic Search with caseNameOrNumber
        results = ShareUserSearchPage.basicSearch(drone, basic_Search_oppName, false);
        Assert.assertTrue(results.size() == 1);
    }
    
    /**
     * Test - AONE-13004: CRM property validation
     * <ul>
     * <li>Login</li>
     * <li>Access the document details page for the account  content created Via SF</li>
     * <li>Verify that document details page should be displayed.</li>
     * <li>Access the document details page for the contract content created Via SF.</li>
     * <li>Verify that document details page should be displayed correctly</li>
     * <li>Access the document details page for the oppor content created Via SF.</li>
     * <li>Verify that document details page should be displayed correctly</li>
     * <li>Access the document details page for the case content created Via SF.</li>
     * <li>Verify that document details page should be displayed correctly</li>
     * </ul>
     */
    @Test
    public void AONE_13885() throws PageException, Exception
    {
        // Login
        ShareUser.login(drone, salesforceUserName, salesforcePassword);

        // Opening Site Document library page
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        
        // Account content details page
        DocumentLibraryPage docLibPage = ShareUserSitePage.navigateToFolder(drone, attachments_Folder + SLASH + accountName);
        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, account_Content);

        // Get the properties from details page
        Map<String, Object> properties = detailsPage.getProperties();

        Assert.assertNotNull(properties);
        Assert.assertFalse(properties.isEmpty());
        Assert.assertTrue(((String)properties.get("AccountIdentifier")).equalsIgnoreCase(accountIdentifier));
        Assert.assertTrue(((String)properties.get("AccountName")).equalsIgnoreCase(accountName));        

        // Opening Site Document library page
        ShareUser.openDocumentLibrary(drone);

        // Contract content details page
        docLibPage = ShareUserSitePage.navigateToFolder(drone, attachments_Folder + SLASH + contractNameOrNumber);
        detailsPage = ShareUser.openDocumentDetailPage(drone, contract_Content);

        // Get the properties from details page
        properties.clear();
        properties = detailsPage.getProperties();

        Assert.assertNotNull(properties);
        Assert.assertFalse(properties.isEmpty());
        Assert.assertTrue(((String)properties.get("AccountIdentifier")).equalsIgnoreCase(accountIdentifier));
        Assert.assertTrue(((String)properties.get("ContractNumber")).equalsIgnoreCase(contractNameOrNumber));
        Assert.assertTrue(((String)properties.get("ContractName")).equalsIgnoreCase(contractNameOrNumber));

        // Opening Site Document library page
        ShareUser.openDocumentLibrary(drone);

        // Opportunity content details page
        docLibPage = ShareUserSitePage.navigateToFolder(drone, attachments_Folder + SLASH + oppName);
        detailsPage = ShareUser.openDocumentDetailPage(drone, oppo_Content);

        // Get the properties from details page
        properties.clear();
        properties = detailsPage.getProperties();

        Assert.assertNotNull(properties);
        Assert.assertFalse(properties.isEmpty());
        Assert.assertTrue(((String)properties.get("AccountIdentifier")).equalsIgnoreCase(accountIdentifier));
        Assert.assertTrue(((String)properties.get("OpportunityName")).equalsIgnoreCase(oppName));
        
        // Opening Site Document library page
        ShareUser.openDocumentLibrary(drone);
        
        // Case content details page
        docLibPage = ShareUserSitePage.navigateToFolder(drone, attachments_Folder + SLASH + caseNameOrNumber);
        detailsPage = ShareUser.openDocumentDetailPage(drone, case_Content);

        // Get the properties from details page
        properties.clear();
        properties = detailsPage.getProperties();

        Assert.assertNotNull(properties);
        Assert.assertFalse(properties.isEmpty());
        Assert.assertTrue(((String)properties.get("AccountIdentifier")).equalsIgnoreCase(accountIdentifier));
        Assert.assertTrue(((String)properties.get("CaseNumber")).equalsIgnoreCase(caseNameOrNumber));
        Assert.assertTrue(((String)properties.get("CaseName")).equalsIgnoreCase(caseNameOrNumber));
    }
}