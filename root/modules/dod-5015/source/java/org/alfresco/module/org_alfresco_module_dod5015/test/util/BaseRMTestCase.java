/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.test.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.disposition.DispositionSchedule;
import org.alfresco.module.org_alfresco_module_dod5015.disposition.DispositionService;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventService;
import org.alfresco.module.org_alfresco_module_dod5015.model.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.model.RmSiteType;
import org.alfresco.module.org_alfresco_module_dod5015.search.RecordsManagementSearchService;
import org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.RetryingTransactionHelperTestCase;
import org.springframework.context.ApplicationContext;

/**
 * Base test case class to use for RM unit tests.
 * 
 * @author Roy Wetherall
 */
public abstract class BaseRMTestCase extends RetryingTransactionHelperTestCase 
                                     implements RecordsManagementModel, ContentModel
{    
    /** Application context */
    protected static final String[] CONFIG_LOCATIONS = new String[] 
    { 
        "classpath:alfresco/application-context.xml",
        "classpath:org/alfresco/module/org_alfresco_module_dod5015/test/util/test-context.xml"
    };
    protected ApplicationContext applicationContext;
    
    /** Test model contants */
    protected String URI = "http://www.alfresco.org/model/rmtest/1.0";
    protected String PREFIX = "rmt";
    protected QName TYPE_CUSTOM_TYPE = QName.createQName(URI, "customType");
    protected QName ASPECT_CUSTOM_ASPECT = QName.createQName(URI, "customAspect");
    protected QName ASPECT_RECORD_META_DATA = QName.createQName(URI, "recordMetaData");
    
    /** Site id */
    protected static final String SITE_ID = "mySite";
    
    /** Services */
    protected NodeService nodeService;
    protected ContentService contentService;
    protected DictionaryService dictionaryService;
    protected RetryingTransactionHelper retryingTransactionHelper;
    protected PolicyComponent policyComponent;
    protected NamespaceService namespaceService;
    protected SearchService searchService;
    protected SiteService siteService;
    
    /** RM Services */
    protected RecordsManagementService rmService;
    protected DispositionService dispositionService;
    protected RecordsManagementEventService eventService;
    protected RecordsManagementAdminService adminService;    
    protected RecordsManagementActionService actionService;
    protected RecordsManagementSearchService rmSearchService;
    protected RecordsManagementSecurityService securityService;
    
    /** test data */
    protected StoreRef storeRef;
    protected NodeRef rootNodeRef;   
    protected SiteInfo siteInfo;
    protected NodeRef folder;
    protected NodeRef rmRootContainer;
    protected NodeRef rmContainer;
    protected DispositionSchedule dispositionSchedule;
    protected NodeRef rmFolder;
    
    /** multi-hierarchy test data 
     *
     *   |--rmRootContainer
     *      |
     *      |--mhContainer
     *         |
     *         |--mhContainer-1-1 (has schedule - folder level)
     *         |  |
     *         |  |--mhContainer-2-1
     *         |     |
     *         |     |--mhContainer-3-1
     *         |         
     *         |--mhContainer-1-2 (has schedule - folder level)
     *            |
     *            |--mhContainer-2-2
     *            |  |
     *            |  |--mhContainer-3-2
     *            |  |
     *            |  |--mhContainer-3-3 (has schedule - record level)
     *            |  
     *            |--mhContainer-2-3 (has schedule - folder level)
     *               |
     *               |--mhContainer-3-4
     *               |     
     *               |--mhContainer-3-5 (has schedule- record level)        
     */
    
    protected NodeRef mhContainer;
    
    protected NodeRef mhContainer11;
    protected DispositionSchedule mhDispositionSchedule11;
    protected NodeRef mhContainer12;
    protected DispositionSchedule mhDispositionSchedule12;
    
    protected NodeRef mhContainer21;
    protected NodeRef mhContainer22;
    protected NodeRef mhContainer23;
    protected DispositionSchedule mhDispositionSchedule23;
    
    protected NodeRef mhContainer31;
    protected NodeRef mhContainer32;
    protected NodeRef mhContainer33;
    protected DispositionSchedule mhDispositionSchedule33;
    protected NodeRef mhContainer34;
    protected NodeRef mhContainer35;
    protected DispositionSchedule mhDispositionSchedule35;
    
    protected NodeRef mhRecordFolder41;
    protected NodeRef mhRecordFolder42;
    protected NodeRef mhRecordFolder43;
    protected NodeRef mhRecordFolder44;
    protected NodeRef mhRecordFolder45;
    
    /** test values */
    protected static final String DEFAULT_DISPOSITION_AUTHORITY = "disposition authority";
    protected static final String DEFAULT_DISPOSITION_INSTRUCTIONS = "disposition instructions";
    protected static final String DEFAULT_DISPOSITION_DESCRIPTION = "disposition action description";
    protected static final String DEFAULT_EVENT_NAME = "case_closed";
    protected static final String PERIOD_NONE = "none|0";
    
    /**
     * Indicates whether this is a multi-hierarchy test or not.  If it is then the multi-hierarchy record
     * taxonomy test data is loaded.
     * @return
     */
    protected boolean isMultiHierarchyTest()
    {
        return false;
    }
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        // Get the application context
        applicationContext = ApplicationContextHelper.getApplicationContext(CONFIG_LOCATIONS);
        
        // Initialise the service beans
        initServices();
        
        // Setup test data
        setupTestData();
        if (isMultiHierarchyTest() == true)
        {
            setupMultiHierarchyTestData();
        }
    }
    
    /**
     * Initialise the service beans.
     */
    protected void initServices()
    {
        // Get services
        nodeService = (NodeService)applicationContext.getBean("NodeService");
        contentService = (ContentService)applicationContext.getBean("ContentService");
        retryingTransactionHelper = (RetryingTransactionHelper)applicationContext.getBean("retryingTransactionHelper");
        namespaceService = (NamespaceService)this.applicationContext.getBean("NamespaceService");
        searchService = (SearchService)this.applicationContext.getBean("SearchService");
        policyComponent = (PolicyComponent)this.applicationContext.getBean("policyComponent");  
        dictionaryService = (DictionaryService)this.applicationContext.getBean("DictionaryService");
        siteService = (SiteService)this.applicationContext.getBean("SiteService");
        
        // Get RM services
        rmService = (RecordsManagementService)applicationContext.getBean("RecordsManagementService");
        dispositionService = (DispositionService)applicationContext.getBean("DispositionService");
        eventService = (RecordsManagementEventService)applicationContext.getBean("RecordsManagementEventService");
        adminService = (RecordsManagementAdminService)applicationContext.getBean("RecordsManagementAdminService");
        actionService = (RecordsManagementActionService)this.applicationContext.getBean("RecordsManagementActionService");
        rmSearchService = (RecordsManagementSearchService)this.applicationContext.getBean("RecordsManagementSearchService");
        securityService = (RecordsManagementSecurityService)this.applicationContext.getBean("RecordsManagementSecurityService");
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Object>()
        {
            @Override
            public Object execute() throws Throwable
            {
                // As system user
                AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
                
                // Do the tear down 
                tearDownImpl();
                
                return null;
            }
        });       
    }
    
    /**
     * Tear down implementation
     */
    protected void tearDownImpl() 
    {
        // Delete the folder
        nodeService.deleteNode(folder);
        
        // Delete the site
        siteService.deleteSite(SITE_ID);
    }
    
    /**
     * @see org.alfresco.util.RetryingTransactionHelperTestCase#getRetryingTransactionHelper()
     */
    @Override
    public RetryingTransactionHelper getRetryingTransactionHelper()
    {
        return retryingTransactionHelper;
    }
    
    /**
     * Setup test data for tests
     */
    protected void setupTestData()
    {
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Object>()
        {
            @Override
            public Object execute() throws Throwable
            {
                AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
                setupTestDataImpl();
                return null;
            }
        });
    }
    
    /**
     * Impl of test data setup
     */
    protected void setupTestDataImpl()
    {
        storeRef = StoreRef.STORE_REF_WORKSPACE_SPACESSTORE;
        rootNodeRef = nodeService.getRootNode(storeRef);
        
        // Create folder
        String containerName = "RM2_" + System.currentTimeMillis();
        Map<QName, Serializable> containerProps = new HashMap<QName, Serializable>(1);
        containerProps.put(ContentModel.PROP_NAME, containerName);
        folder = nodeService.createNode(
              rootNodeRef, 
              ContentModel.ASSOC_CHILDREN, 
              QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, containerName), 
              ContentModel.TYPE_FOLDER,
              containerProps).getChildRef();
        assertNotNull("Could not create base folder", folder);
        
        // Create the site
        siteInfo = siteService.createSite("preset", SITE_ID, "title", "descrition", SiteVisibility.PUBLIC, RecordsManagementModel.TYPE_RM_SITE);
        rmRootContainer = siteService.getContainer(SITE_ID, RmSiteType.COMPONENT_DOCUMENT_LIBRARY);
        assertNotNull("Site document library container was not created successfully.", rmRootContainer);
                        
        // Create RM container
        rmContainer = rmService.createRecordsManagementContainer(rmRootContainer, "rmContainer");
        assertNotNull("Could not create rm container", rmContainer);
        
        // Create disposition schedule
        dispositionSchedule = createBasicDispositionSchedule(rmContainer);
        
        // Create RM folder
        rmFolder = rmService.createRecordFolder(rmContainer, "rmFolder");
        assertNotNull("Could not create rm folder", rmFolder);
    }
    
    /**
     * Setup multi hierarchy test data
     */
    protected void setupMultiHierarchyTestData()
    {
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Object>()
        {
            @Override
            public Object execute() throws Throwable
            {
                // As system user
                AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
                
                // Do setup
                setupMultiHierarchyTestDataImpl();
                
                return null;
            }
        });
    }
    
    /**
     * Impl of multi hierarchy test data
     */
    protected void setupMultiHierarchyTestDataImpl()
    {
        // Create root mh container
        mhContainer = rmService.createRecordsManagementContainer(rmRootContainer, "mhContainer");                
        
        // Level 1
        mhContainer11 = rmService.createRecordsManagementContainer(mhContainer, "mhContainer11");
        mhDispositionSchedule11 = createBasicDispositionSchedule(mhContainer11, "ds11", DEFAULT_DISPOSITION_AUTHORITY, false, true);
        mhContainer12 = rmService.createRecordsManagementContainer(mhContainer, "mhContainer12");
        mhDispositionSchedule12 = createBasicDispositionSchedule(mhContainer12, "ds12", DEFAULT_DISPOSITION_AUTHORITY, false, true);
        
        // Level 2
        mhContainer21 = rmService.createRecordsManagementContainer(mhContainer11, "mhContainer21");
        mhContainer22 = rmService.createRecordsManagementContainer(mhContainer12, "mhContainer22");
        mhContainer23 = rmService.createRecordsManagementContainer(mhContainer12, "mhContainer23");
        mhDispositionSchedule23 = createBasicDispositionSchedule(mhContainer23, "ds23", DEFAULT_DISPOSITION_AUTHORITY, false, true);

        // Level 3
        mhContainer31 = rmService.createRecordsManagementContainer(mhContainer21, "mhContainer31");
        mhContainer32 = rmService.createRecordsManagementContainer(mhContainer22, "mhContainer32");
        mhContainer33 = rmService.createRecordsManagementContainer(mhContainer22, "mhContainer33");
        mhDispositionSchedule33 = createBasicDispositionSchedule(mhContainer33, "ds33", DEFAULT_DISPOSITION_AUTHORITY, true, true);
        mhContainer34 = rmService.createRecordsManagementContainer(mhContainer23, "mhContainer34");
        mhContainer35 = rmService.createRecordsManagementContainer(mhContainer23, "mhContainer35");
        mhDispositionSchedule35 = createBasicDispositionSchedule(mhContainer35, "ds35", DEFAULT_DISPOSITION_AUTHORITY, true, true);
        
        // Record folders
        mhRecordFolder41 = rmService.createRecordFolder(mhContainer31, "mhFolder41");
        mhRecordFolder42 = rmService.createRecordFolder(mhContainer32, "mhFolder42");
        mhRecordFolder43 = rmService.createRecordFolder(mhContainer33, "mhFolder43");
        mhRecordFolder44 = rmService.createRecordFolder(mhContainer34, "mhFolder44");
        mhRecordFolder45 = rmService.createRecordFolder(mhContainer35, "mhFolder45");        
    }
    
    /**
     * 
     * @param container
     * @return
     */
    protected DispositionSchedule createBasicDispositionSchedule(NodeRef container)
    {
        return createBasicDispositionSchedule(container, DEFAULT_DISPOSITION_INSTRUCTIONS, DEFAULT_DISPOSITION_AUTHORITY, false, true);
    }
    
    /**
     * 
     * @param container
     * @param isRecordLevel
     * @param defaultDispositionActions
     * @return
     */
    protected DispositionSchedule createBasicDispositionSchedule(
                                    NodeRef container, 
                                    String dispositionInstructions,
                                    String dispositionAuthority,
                                    boolean isRecordLevel, 
                                    boolean defaultDispositionActions)
    {
        Map<QName, Serializable> dsProps = new HashMap<QName, Serializable>(3);
        dsProps.put(PROP_DISPOSITION_AUTHORITY, dispositionAuthority);
        dsProps.put(PROP_DISPOSITION_INSTRUCTIONS, dispositionInstructions);
        dsProps.put(PROP_RECORD_LEVEL_DISPOSITION, isRecordLevel);
        DispositionSchedule dispositionSchedule = dispositionService.createDispositionSchedule(container, dsProps);                
        assertNotNull(dispositionSchedule);   
        
        if (defaultDispositionActions == true)
        {
            Map<QName, Serializable> adParams = new HashMap<QName, Serializable>(3);
            adParams.put(PROP_DISPOSITION_ACTION_NAME, "cutoff");
            adParams.put(PROP_DISPOSITION_DESCRIPTION, DEFAULT_DISPOSITION_DESCRIPTION);
            
            List<String> events = new ArrayList<String>(1);
            events.add(DEFAULT_EVENT_NAME);
            adParams.put(PROP_DISPOSITION_EVENT, (Serializable)events);
            
            dispositionService.addDispositionActionDefinition(dispositionSchedule, adParams);
            
            adParams = new HashMap<QName, Serializable>(3);
            adParams.put(PROP_DISPOSITION_ACTION_NAME, "destroy");
            adParams.put(PROP_DISPOSITION_DESCRIPTION, DEFAULT_DISPOSITION_DESCRIPTION);
            adParams.put(PROP_DISPOSITION_PERIOD, "immediately|0");            
            
            dispositionService.addDispositionActionDefinition(dispositionSchedule, adParams);
        }
        
        return dispositionSchedule;
    }
    
    protected NodeRef createRecord(NodeRef recordFolder, String name)
    {
        return createRecord(recordFolder, name, null, "Some test content");
    }
    
	protected NodeRef createRecord(NodeRef recordFolder, String name, Map<QName, Serializable> properties, String content)
	{
    	// Create the document
	    if (properties == null)
	    {
	        properties = new HashMap<QName, Serializable>(1);
	    }
        if (properties.containsKey(ContentModel.PROP_NAME) == false)
        {
            properties.put(ContentModel.PROP_NAME, name);
        }
        NodeRef recordOne = this.nodeService.createNode(recordFolder, 
                                                        ContentModel.ASSOC_CONTAINS, 
                                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name), 
                                                        ContentModel.TYPE_CONTENT,
                                                        properties).getChildRef();
        
        // Set the content
        ContentWriter writer = contentService.getWriter(recordOne, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent(content);
        
        return recordOne;
	}   
      
    protected void declareRecord(NodeRef recordOne)
    {
        // Declare record
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_MEDIA_TYPE, "mediaTypeValue"); 
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_FORMAT, "formatValue"); 
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_DATE_RECEIVED, new Date());
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        this.nodeService.setProperty(recordOne, RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        this.nodeService.setProperty(recordOne, ContentModel.PROP_TITLE, "titleValue");
        this.actionService.executeRecordsManagementAction(recordOne, "declareRecord");
	}
}
