/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.test.service;

import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.search.RecordsManagementSearchParameters;
import org.alfresco.module.org_alfresco_module_dod5015.search.SavedSearchDetails;
import org.alfresco.module.org_alfresco_module_dod5015.test.util.BaseRMTestCase;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.util.TestWithUserUtils;

/**
 * Search service implementation unit test.
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementSearchServiceImplTest extends BaseRMTestCase
{
    @Override
    protected boolean isMultiHierarchyTest()
    {
        return true;
    }
    
    private static final String SEARCH1 = "search1";
    private static final String SEARCH2 = "search2";
    private static final String SEARCH3 = "search3";
    private static final String SEARCH4 = "search4";
    
    private static final String USER1 = "user1";
    private static final String USER2 = "user2";
    
    private NodeRef folderLevelRecordFolder;
    private NodeRef recordLevelRecordFolder;
    
    private NodeRef recordOne;
    private NodeRef recordTwo;
    private NodeRef recordThree;
    private NodeRef recordFour;
    private NodeRef recordFive;
    private NodeRef recordSix;
    
    private MutableAuthenticationService authenticationService;
    
    private int numberOfReports;
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.test.util.BaseRMTestCase#setupTestData()
     */
    @Override
    protected void setupTestData()
    {
        super.setupTestData();
        
        authenticationService = (MutableAuthenticationService)applicationContext.getBean("AuthenticationService");
        
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                // Create test users                
                TestWithUserUtils.createUser(USER1, USER1, rootNodeRef, nodeService, authenticationService);
                TestWithUserUtils.createUser(USER2, USER2, rootNodeRef, nodeService, authenticationService);
                
                // Count the number of pre-defined reports
                List<SavedSearchDetails> searches = rmSearchService.getSavedSearches(SITE_ID);
                assertNotNull(searches);
                numberOfReports = searches.size();
                
                return null;
            }
        });
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.test.util.BaseRMTestCase#setupMultiHierarchyTestData()
     */
    @Override
    protected void setupMultiHierarchyTestData()
    {
        super.setupMultiHierarchyTestData();
        
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                folderLevelRecordFolder = mhRecordFolder42;
                recordLevelRecordFolder = mhRecordFolder43;                
                
                recordOne = createRecord(folderLevelRecordFolder, "recordOne.txt", null, "record one - folder level - elephant");
                recordTwo = createRecord(folderLevelRecordFolder, "recordTwo.txt", null, "record two - folder level - snake");
                recordThree = createRecord(folderLevelRecordFolder, "recordThree.txt", null, "record three - folder level - monkey");
                recordFour = createRecord(recordLevelRecordFolder, "recordFour.txt", null, "record four - record level - elephant");
                recordFive = createRecord(recordLevelRecordFolder, "recordFive.txt", null, "record five - record level - snake");
                recordSix = createRecord(recordLevelRecordFolder, "recordSix.txt", null, "record six - record level - monkey");
                
                return null;
            }
        });
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                // Delete test users
                TestWithUserUtils.deleteUser(USER1, USER1, rootNodeRef, nodeService, authenticationService);
                TestWithUserUtils.deleteUser(USER2, USER2, rootNodeRef, nodeService, authenticationService);
                
                return null;
            }
        });
    }
    
    public void testSearch()
    {
        // Full text search 
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                String query = "keywords:\"elephant\"";                
                List<NodeRef> results = rmSearchService.search(SITE_ID, query, new RecordsManagementSearchParameters());
                assertNotNull(results);
                assertEquals(2, results.size());
                
                return null;
            }
        });
        
        // Property search
        
        // 
    }
    
    public void testSaveSearch()
    {
        // Add some saved searches (as admin user)
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                SavedSearchDetails details1 = rmSearchService.saveSearch(SITE_ID, SEARCH1, "description1", "query1", new RecordsManagementSearchParameters(), true);
                checkSearchDetails(details1, "mySite", "search1", "description1", "query1", new RecordsManagementSearchParameters(), true);
                SavedSearchDetails details2 = rmSearchService.saveSearch(SITE_ID, SEARCH2, "description2", "query2", new RecordsManagementSearchParameters(), false);
                checkSearchDetails(details2, "mySite", "search2", "description2", "query2", new RecordsManagementSearchParameters(), false);
                
                return null;
            }

        }); 
        
        // Add some saved searches (as user1)
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                SavedSearchDetails details1 = rmSearchService.saveSearch(SITE_ID, SEARCH3, "description3", "query3", new RecordsManagementSearchParameters(), false);
                checkSearchDetails(details1, "mySite", SEARCH3, "description3", "query3", new RecordsManagementSearchParameters(), false);
                SavedSearchDetails details2 = rmSearchService.saveSearch(SITE_ID, SEARCH4, "description4", "query4", new RecordsManagementSearchParameters(), false);
                checkSearchDetails(details2, "mySite", SEARCH4, "description4", "query4", new RecordsManagementSearchParameters(), false);
                
                return null;
            }

        }, USER1); 
        
        // Get searches (as admin user)
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                List<SavedSearchDetails> searches = rmSearchService.getSavedSearches(SITE_ID);
                assertNotNull(searches);
                assertEquals(numberOfReports + 2, searches.size());
                
                SavedSearchDetails search1 = rmSearchService.getSavedSearch(SITE_ID, SEARCH1);
                assertNotNull(search1);
                checkSearchDetails(search1, "mySite", "search1", "description1", "query1", new RecordsManagementSearchParameters(), true);
                
                SavedSearchDetails search2 = rmSearchService.getSavedSearch(SITE_ID, SEARCH2);
                assertNotNull(search2);
                checkSearchDetails(search2, "mySite", "search2", "description2", "query2", new RecordsManagementSearchParameters(), false);
                
                SavedSearchDetails search3 = rmSearchService.getSavedSearch(SITE_ID, SEARCH3);
                assertNull(search3);
                
                SavedSearchDetails search4 = rmSearchService.getSavedSearch(SITE_ID, SEARCH4);
                assertNull(search4);
                
                return null;
            }

        }); 
        
        // Get searches (as user1)
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                List<SavedSearchDetails> searches = rmSearchService.getSavedSearches(SITE_ID);
                assertNotNull(searches);
                assertEquals(numberOfReports + 3, searches.size());
                
                SavedSearchDetails search1 = rmSearchService.getSavedSearch(SITE_ID, SEARCH1);
                assertNotNull(search1);
                checkSearchDetails(search1, "mySite", "search1", "description1", "query1", new RecordsManagementSearchParameters(), true);
                
                SavedSearchDetails search2 = rmSearchService.getSavedSearch(SITE_ID, SEARCH2);
                assertNull(search2);
                
                SavedSearchDetails search3 = rmSearchService.getSavedSearch(SITE_ID, SEARCH3);
                assertNotNull(search3);
                checkSearchDetails(search3, "mySite", SEARCH3, "description3", "query3", new RecordsManagementSearchParameters(), false);
                
                SavedSearchDetails search4 = rmSearchService.getSavedSearch(SITE_ID, SEARCH4);
                assertNotNull(search4);
                checkSearchDetails(search4, "mySite", "search4", "description4", "query4", new RecordsManagementSearchParameters(), false);
                
                return null;
            }

        }, USER1);
        
        // Update search (as admin user)
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                SavedSearchDetails search1 = rmSearchService.getSavedSearch(SITE_ID, SEARCH1);
                assertNotNull(search1);
                checkSearchDetails(search1, SITE_ID, SEARCH1, "description1", "query1", new RecordsManagementSearchParameters(), true);
                
                rmSearchService.saveSearch(SITE_ID, SEARCH1, "change", "change", new RecordsManagementSearchParameters(), true);
                
                search1 = rmSearchService.getSavedSearch(SITE_ID, SEARCH1);
                assertNotNull(search1);
                checkSearchDetails(search1, SITE_ID, SEARCH1, "change", "change", new RecordsManagementSearchParameters(), true);
                
                return null;                
            }
        });
        
        // Delete searches (as admin user)  
        // TODO
    }
    
    /**
     * Check the details of the saved search.
     */
    private void checkSearchDetails(
                    SavedSearchDetails details, 
                    String siteid, 
                    String name, 
                    String description, 
                    String query, 
                    RecordsManagementSearchParameters searchParameters, 
                    boolean isPublic)
    {
        assertNotNull(details);
        assertEquals(siteid, details.getSiteId());
        assertEquals(name, details.getName());
        assertEquals(description, details.getDescription());
        assertEquals(query, details.getSearch());
        assertEquals(isPublic, details.isPublic());
        
        assertEquals(searchParameters.getMaxItems(), details.getSearchParameters().getMaxItems());
        assertEquals(searchParameters.isIncludeRecords(), details.getSearchParameters().isIncludeRecords());
        assertEquals(searchParameters.isIncludeUndeclaredRecords(), details.getSearchParameters().isIncludeUndeclaredRecords());
        assertEquals(searchParameters.isIncludeVitalRecords(), details.getSearchParameters().isIncludeVitalRecords());
        assertEquals(searchParameters.isIncludeRecordFolders(), details.getSearchParameters().isIncludeRecordFolders());
        assertEquals(searchParameters.isIncludeFrozen(), details.getSearchParameters().isIncludeFrozen());
        assertEquals(searchParameters.isIncludeCutoff(), details.getSearchParameters().isIncludeCutoff());
        
        // Check the other stuff ....
    }
}
