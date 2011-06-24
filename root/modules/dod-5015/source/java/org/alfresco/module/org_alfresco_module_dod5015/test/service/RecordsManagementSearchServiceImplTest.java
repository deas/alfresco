/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.test.service;

import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.search.SavedSearchDetails;
import org.alfresco.module.org_alfresco_module_dod5015.test.util.BaseRMTestCase;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteVisibility;
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
        return false;
    }
    
    private static final String SITE_ID = "mySite";
    
    private static final String SEARCH1 = "search1";
    private static final String SEARCH2 = "search2";
    private static final String SEARCH3 = "search3";
    private static final String SEARCH4 = "search4";
    
    private static final String USER1 = "user1";
    private static final String USER2 = "user2";
    
    MutableAuthenticationService authenticationService;
    
    private SiteInfo siteInfo;    
    
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
                // Create test site
                siteInfo = siteService.createSite("preset", SITE_ID, "title", "descrition", SiteVisibility.PUBLIC);
                assertNotNull(siteInfo);
                
                // Create test users                
                TestWithUserUtils.createUser(USER1, USER1, rootNodeRef, nodeService, authenticationService);
                TestWithUserUtils.createUser(USER2, USER2, rootNodeRef, nodeService, authenticationService);
                
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
                
                // Delete test site
                siteService.deleteSite(SITE_ID);
                
                return null;
            }
        });
    }
    
    // TODO List<NodeRef> search(String query); 
    
    public void testSaveSearch()
    {
       // Add some saved searches (as admin user)
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                SavedSearchDetails details1 = rmSearchService.saveSearch(SITE_ID, SEARCH1, "description1", "query1", "sort1", "param1", true);
                checkSearchDetails(details1, "mySite", "search1", "description1", "query1", "sort1", "param1", true);
                SavedSearchDetails details2 = rmSearchService.saveSearch(SITE_ID, SEARCH2, "description2", "query2", "sort2", "param2", false);
                checkSearchDetails(details2, "mySite", "search2", "description2", "query2", "sort2", "param2", false);
                
                return null;
            }

        }); 
        
        // Add some saved searches (as user1)
        doTestInTransaction(new Test<Void>()
        {
            @Override
            public Void run()
            {
                SavedSearchDetails details1 = rmSearchService.saveSearch(SITE_ID, SEARCH3, "description3", "query3", "sort3", "param3", false);
                checkSearchDetails(details1, "mySite", SEARCH3, "description3", "query3", "sort3", "param3", false);
                SavedSearchDetails details2 = rmSearchService.saveSearch(SITE_ID, SEARCH4, "description4", "query4", "sort4", "param4", false);
                checkSearchDetails(details2, "mySite", SEARCH4, "description4", "query4", "sort4", "param4", false);
                
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
                assertEquals(2, searches.size());
                
                SavedSearchDetails search1 = rmSearchService.getSavedSearch(SITE_ID, SEARCH1);
                assertNotNull(search1);
                checkSearchDetails(search1, "mySite", "search1", "description1", "query1", "sort1", "param1", true);
                
                SavedSearchDetails search2 = rmSearchService.getSavedSearch(SITE_ID, SEARCH2);
                assertNotNull(search2);
                checkSearchDetails(search2, "mySite", "search2", "description2", "query2", "sort2", "param2", false);
                
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
                assertEquals(3, searches.size());
                
                SavedSearchDetails search1 = rmSearchService.getSavedSearch(SITE_ID, SEARCH1);
                assertNotNull(search1);
                checkSearchDetails(search1, "mySite", "search1", "description1", "query1", "sort1", "param1", true);
                
                SavedSearchDetails search2 = rmSearchService.getSavedSearch(SITE_ID, SEARCH2);
                assertNull(search2);
                
                SavedSearchDetails search3 = rmSearchService.getSavedSearch(SITE_ID, SEARCH3);
                assertNotNull(search3);
                checkSearchDetails(search3, "mySite", SEARCH3, "description3", "query3", "sort3", "param3", false);
                
                SavedSearchDetails search4 = rmSearchService.getSavedSearch(SITE_ID, SEARCH4);
                assertNotNull(search4);
                checkSearchDetails(search4, "mySite", "search4", "description4", "query4", "sort4", "param4", false);
                
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
                checkSearchDetails(search1, SITE_ID, SEARCH1, "description1", "query1", "sort1", "param1", true);
                
                rmSearchService.saveSearch(SITE_ID, SEARCH1, "change", "change", "change", "change", true);
                
                search1 = rmSearchService.getSavedSearch(SITE_ID, SEARCH1);
                assertNotNull(search1);
                checkSearchDetails(search1, SITE_ID, SEARCH1, "change", "change", "change", "change", true);
                
                return null;                
            }
        });
        
        // Delete searches (as admin user)   
    }
    
    private void checkSearchDetails(
                    SavedSearchDetails details, 
                    String siteid, 
                    String name, 
                    String description, 
                    String query, 
                    String sort, 
                    String params, 
                    boolean isPublic)
    {
        assertNotNull(details);
        assertEquals(siteid, details.getSiteId());
        assertEquals(name, details.getName());
        assertEquals(description, details.getDescription());
        assertEquals(query, details.getQuery());
        assertEquals(sort, details.getSort());
        assertEquals(params, details.getParams());
        assertEquals(isPublic, details.isPublic());       
    }
}
