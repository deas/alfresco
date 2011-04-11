/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.recordsManagement;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.importer.ImporterComponent;
import org.alfresco.repo.jscript.ClasspathScriptLocation;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.module.ModuleService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.ScriptLocation;
import org.alfresco.service.cmr.repository.ScriptService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.view.ImporterBinding;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.GUID;

/**
 * Tests the folder record behaviour specified in the record_folder.js script
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementSystemTest extends BaseSpringTest 
{
    private static final String STRING_PROP_VALUE = "A test value";
    
	protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
	
	protected NodeService nodeService;
	protected SearchService searchService;
	protected CopyService copyService;
	protected RuleService ruleService;
	protected TransactionService transactionService;
	protected ContentService contentService;
    protected ActionService actionService;
    protected VersionService versionService;
    protected ScriptService scriptService;
	
	protected NodeRef filePlan;
	
	protected static String CAT_ID = "00-000";
	
	@Override
	protected void onSetUpInTransaction() 
		throws Exception 
	{
		super.onSetUpInTransaction();
        
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
		
		// Get references to the relevant services
		this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
		this.searchService = (SearchService)this.applicationContext.getBean("searchService");
		this.copyService = (CopyService)this.applicationContext.getBean("copyService");
		this.ruleService = (RuleService)this.applicationContext.getBean("ruleService");
		this.transactionService = (TransactionService)this.applicationContext.getBean("transactionComponent");
		this.contentService = (ContentService)this.applicationContext.getBean("contentService");
        this.actionService = (ActionService)this.applicationContext.getBean("actionService");
        this.versionService = (VersionService)this.applicationContext.getBean("versionService");
        this.scriptService = (ScriptService)this.applicationContext.getBean("scriptService");
		
		// Get a reference to the company home node
		ResultSet results1 = this.searchService.query(RecordsManagementSystemTest.SPACES_STORE, SearchService.LANGUAGE_XPATH, "app:company_home");
		NodeRef companyHome = results1.getNodeRefs().get(0);
		results1.close();
		
		Map<QName, Serializable> props = new HashMap<QName, Serializable>(10);
		props.put(RecordsManagementModel.PROP_RECORD_CATEGORY_IDENTIFIER, CAT_ID);
		
		this.filePlan = this.nodeService.createNode(
				companyHome, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
                RecordsManagementModel.TYPE_FILE_PLAN, props).getChildRef();
        
        // Check that the templatable aspect ahs been applied
        assertTrue(this.nodeService.hasAspect(this.filePlan, ContentModel.ASPECT_TEMPLATABLE));
	}
    
    public boolean isRMConfigured()
    {
        ModuleService moduleService = (ModuleService)this.applicationContext.getBean("moduleService");
        return (moduleService.getModule("recordsManagement") != null);
    }   
    
    public void testUpdateTemplatesAndScripts()
    {            
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }        
        
        importFile("alfresco/module/recordsManagement/bootstrap/rm_javascripts.xml", "/app:company_home/app:dictionary/app:scripts");
        importFile("alfresco/module/recordsManagement/bootstrap/rm_templates.xml", "/app:company_home/app:dictionary/app:content_templates");
        
        setComplete();
        endTransaction();
    }
    
    private void importFile(String file, String destination)
    {        
        ImporterComponent importer = (ImporterComponent)this.applicationContext.getBean("importerComponent");
        
        InputStream viewStream = getClass().getClassLoader().getResourceAsStream(file);
        InputStreamReader inputReader = new InputStreamReader(viewStream);
        BufferedReader reader = new BufferedReader(inputReader);

        Location location = new Location(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore"));
        location.setPath(destination);
        
        importer.importView(reader, location, new TempBinding(), null);
    }
	
	public void testApplicationOfRecordAspect()
	{
	    // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
		Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
		props.put(ContentModel.PROP_NAME, "MyDoc" + GUID.generate() + ".txt");
		NodeRef doc1 = this.nodeService.createNode(
				this.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_CONTENT,
				props).getChildRef();		
		assertTrue(this.nodeService.hasAspect(doc1, RecordsManagementModel.ASPECT_RECORD));
		
		NodeRef folder1 = this.nodeService.createNode(
				this.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_FOLDER).getChildRef();
		assertTrue(this.nodeService.hasAspect(folder1, RecordsManagementModel.ASPECT_RECORD));        
	}
	
    public void testEMailRecord()
    {
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_NAME, "test" + GUID.generate() + ".msg");
        NodeRef doc1 = this.nodeService.createNode(
                this.filePlan, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
                ContentModel.TYPE_CONTENT,
                props).getChildRef();       
        assertTrue(this.nodeService.hasAspect(doc1, RecordsManagementModel.ASPECT_RECORD));
        assertFalse(this.nodeService.hasAspect(doc1, ContentModel.ASPECT_EMAILED));
        
        // Now add the content
        InputStream is = getClass().getClassLoader().getResourceAsStream("test.msg");
        assertNotNull(is);
        ContentWriter writer = this.contentService.getWriter(doc1, ContentModel.PROP_CONTENT, true);
        writer.setMimetype("message/rfc822");
        writer.setEncoding("UTF-8");
        writer.putContent(is);
        
        // Do a quick check to ensure that the email details have been extracted
        assertTrue(this.nodeService.hasAspect(doc1, ContentModel.ASPECT_EMAILED));
        String subjectLine = (String)this.nodeService.getProperty(doc1, ContentModel.PROP_SUBJECT);
        assertNotNull(subjectLine);
        assertEquals(subjectLine, this.nodeService.getProperty(doc1, RecordsManagementModel.PROP_SUBJECT));
        
    }
    
	public void testFolderIdGeneration()
	{
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
		// Create a some new sub-folders
		final NodeRef folder1 = this.nodeService.createNode(
				this.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_FOLDER).getChildRef();
		final NodeRef folder2 = this.nodeService.createNode(
				this.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_FOLDER).getChildRef();
		final NodeRef folder3 = this.nodeService.createNode(
				this.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_FOLDER).getChildRef();
			
		assertTrue(((String)this.nodeService.getProperty(folder1, ContentModel.PROP_NAME)).contains("Folder 0001"));
		assertEquals(
				CAT_ID + "-0001", 
				this.nodeService.getProperty(
						folder1, 
						QName.createQName("http://www.alfresco.org/model/record/1.0", "recordIdentifier")));
		
		assertTrue(((String)this.nodeService.getProperty(folder2, ContentModel.PROP_NAME)).contains("Folder 0002"));
		assertEquals(
				CAT_ID + "-0002", 
				this.nodeService.getProperty(
						folder2, 
						QName.createQName("http://www.alfresco.org/model/record/1.0", "recordIdentifier")));
		
		assertTrue(((String)this.nodeService.getProperty(folder3, ContentModel.PROP_NAME)).contains("Folder 0003"));
		assertEquals(
				CAT_ID + "-0003", 
				this.nodeService.getProperty(
						folder3, 
						QName.createQName("http://www.alfresco.org/model/record/1.0", "recordIdentifier")));
							
	}	

	public void testContentIdGeneration()
	{
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
		// Create a some new records
		Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
		props.put(ContentModel.PROP_NAME, "MyDoc" + GUID.generate() + ".txt");
		final NodeRef doc1 = this.nodeService.createNode(
				this.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_CONTENT,
				props).getChildRef();
		Map<QName, Serializable> props2 = new HashMap<QName, Serializable>(1);
		props2.put(ContentModel.PROP_NAME, "MyDoc" + GUID.generate() + ".txt");
		final NodeRef doc2 = this.nodeService.createNode(
				this.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_CONTENT,
				props2).getChildRef();
		Map<QName, Serializable> props3 = new HashMap<QName, Serializable>(1);
		props3.put(ContentModel.PROP_NAME, "MyDoc" + GUID.generate() + ".txt");
		final NodeRef doc3 = this.nodeService.createNode(
				this.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_CONTENT,
				props3).getChildRef();

		System.out.println(this.nodeService.getProperty(doc1, ContentModel.PROP_NAME));

		assertTrue(((String)this.nodeService.getProperty(doc1, ContentModel.PROP_NAME)).contains(CAT_ID + "-0001"));
		assertEquals(
				CAT_ID + "-0001", 
				this.nodeService.getProperty(
						doc1, 
						QName.createQName("http://www.alfresco.org/model/record/1.0", "recordIdentifier")));
		assertTrue(((String)this.nodeService.getProperty(doc2, ContentModel.PROP_NAME)).contains(CAT_ID + "-0002"));
		assertEquals(
				CAT_ID + "-0002", 
				this.nodeService.getProperty(
						doc2, 
						QName.createQName("http://www.alfresco.org/model/record/1.0", "recordIdentifier")));
		assertTrue(((String)this.nodeService.getProperty(doc3, ContentModel.PROP_NAME)).contains(CAT_ID + "-0003"));
		assertEquals(
				CAT_ID + "-0003", 
				this.nodeService.getProperty(
						doc3, 
						QName.createQName("http://www.alfresco.org/model/record/1.0", "recordIdentifier")));
		
	}
	
	public void testVitalRecordIndicatorOnSetup()
	{   
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        // Create a folder and document
        NodeRef folder0 = this.nodeService.createNode(
                this.filePlan, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
                ContentModel.TYPE_FOLDER).getChildRef();
        Map<QName, Serializable> props0 = new HashMap<QName, Serializable>(1);
        props0.put(ContentModel.PROP_NAME, "MyDoc" + GUID.generate() + ".txt");
        NodeRef doc0 = this.nodeService.createNode(
                                        this.filePlan, 
                                        ContentModel.ASSOC_CONTAINS, 
                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
                                        ContentModel.TYPE_CONTENT,
                                        props0).getChildRef();
        
        // Since review period is none the aspect should not have been applied
        assertFalse(this.nodeService.hasAspect(folder0, RecordsManagementModel.ASPECT_VITAL_RECORD));
        assertFalse(this.nodeService.hasAspect(doc0, RecordsManagementModel.ASPECT_VITAL_RECORD));
        
		// Set the vital record details on the file plan
        this.nodeService.setProperty(filePlan, RecordsManagementModel.PROP_VITAL_RECORD_REVIEW_PERIOD_UNIT, RecordsManagementModel.CAT_DATEPERIOD_ANNUALLY);
        this.nodeService.setProperty(filePlan, RecordsManagementModel.PROP_VITAL_RECORD_REVIEW_PERIOD_VALUE, 2);
		this.nodeService.setProperty(filePlan, RecordsManagementModel.PROP_VITAL_RECORD_INDICATOR, true);
		
		// Create a folder and document
		NodeRef folder1 = this.nodeService.createNode(
				this.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_FOLDER).getChildRef();
		Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
		props.put(ContentModel.PROP_NAME, "MyDoc" + GUID.generate() + ".txt");
		NodeRef doc1 = this.nodeService.createNode(
                                		this.filePlan, 
                                		ContentModel.ASSOC_CONTAINS, 
                                		QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
                                		ContentModel.TYPE_CONTENT,
                                		props).getChildRef();
		
		// Check that the vital record aspect has been applied
		assertTrue(RecordsManagementSystemTest.this.nodeService.hasAspect(folder1, RecordsManagementModel.ASPECT_VITAL_RECORD));
		assertTrue(RecordsManagementSystemTest.this.nodeService.hasAspect(doc1, RecordsManagementModel.ASPECT_VITAL_RECORD));
		
		// TODO check the other properties
		
		Calendar now = Calendar.getInstance();
		
		// Check the folders review date
		Date date = (Date)RecordsManagementSystemTest.this.nodeService.getProperty(folder1, RecordsManagementModel.PROP_NEXT_REVIEW_DATE);
		assertNotNull(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		assertEquals(now.get(Calendar.YEAR)+2, calendar.get(Calendar.YEAR));
		
		// Check the documents review date
		Date date1 = (Date)RecordsManagementSystemTest.this.nodeService.getProperty(doc1, RecordsManagementModel.PROP_NEXT_REVIEW_DATE);
		assertNotNull(date1);
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		assertEquals(now.get(Calendar.YEAR)+2, calendar1.get(Calendar.YEAR));		
	}
	
	public void testCutoffScheduleOnSetup()
	{
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
		// Set the vital record details on the file plan
		this.nodeService.setProperty(filePlan, RecordsManagementModel.PROP_PROCESS_CUTOFF, true);
		this.nodeService.setProperty(filePlan, RecordsManagementModel.PROP_CUTOFF_PERIOD_UNIT, RecordsManagementModel.CAT_DATEPERIOD_ANNUALLY);
        this.nodeService.setProperty(filePlan, RecordsManagementModel.PROP_CUTOFF_PERIOD_VALUE, 2);
		
		// Create a folder and document
		final NodeRef folder1 = this.nodeService.createNode(
				this.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_FOLDER).getChildRef();
		Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
		props.put(ContentModel.PROP_NAME, "MyDoc" + GUID.generate() + ".txt");
		final NodeRef doc1 = this.nodeService.createNode(
		this.filePlan, 
		ContentModel.ASSOC_CONTAINS, 
		QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
		ContentModel.TYPE_CONTENT,
		props).getChildRef();

		// Check that the vital record aspect has been applied
		assertTrue(RecordsManagementSystemTest.this.nodeService.hasAspect(folder1, RecordsManagementModel.ASPECT_CUTOFF_SCHEDULE));
		assertTrue(RecordsManagementSystemTest.this.nodeService.hasAspect(doc1, RecordsManagementModel.ASPECT_CUTOFF_SCHEDULE));
		
		// TODO check the other properties
		
		Calendar now = Calendar.getInstance();
		
		// Check the folders review date
		Date date = (Date)RecordsManagementSystemTest.this.nodeService.getProperty(folder1, RecordsManagementModel.PROP_CUTOFF_DATE_TIME);
		assertNotNull(date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		assertEquals(now.get(Calendar.YEAR)+2, calendar.get(Calendar.YEAR));
		
		// Check the documents review date
		Date date1 = (Date)RecordsManagementSystemTest.this.nodeService.getProperty(doc1, RecordsManagementModel.PROP_CUTOFF_DATE_TIME);
		assertNotNull(date1);
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		assertEquals(now.get(Calendar.YEAR)+2, calendar1.get(Calendar.YEAR));

	}
    
    public void testObsolete()
    {
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        // Test obsolete with cuttof off
        NodeRef record0 = createTestRecord();
        this.nodeService.addAspect(record0, RecordsManagementModel.ASPECT_OBSOLETE, null);
        assertTrue(this.nodeService.hasAspect(record0, RecordsManagementModel.ASPECT_OBSOLETE));
        assertFalse(this.nodeService.hasAspect(record0, RecordsManagementModel.ASPECT_CUTOFF));
        
        // Test cutOff on obsolete with process hold == true
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_CUTOFF_ON_OBSOLETE, true);
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_PROCESS_HOLD, true);
        NodeRef record1 = createTestRecord();
        this.nodeService.addAspect(record1, RecordsManagementModel.ASPECT_OBSOLETE, null);
        assertTrue(this.nodeService.hasAspect(record1, RecordsManagementModel.ASPECT_OBSOLETE));
        assertTrue(this.nodeService.hasAspect(record1, RecordsManagementModel.ASPECT_CUTOFF));
        assertTrue(this.nodeService.hasAspect(record1, RecordsManagementModel.ASPECT_HELD));
        
        // test cutOff on obsolete with process hold == false
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_PROCESS_HOLD, false);
        NodeRef record2 = createTestRecord();
        this.nodeService.addAspect(record2, RecordsManagementModel.ASPECT_OBSOLETE, null);
        assertTrue(this.nodeService.hasAspect(record2, RecordsManagementModel.ASPECT_OBSOLETE));
        assertTrue(this.nodeService.hasAspect(record2, RecordsManagementModel.ASPECT_CUTOFF));
        assertFalse(this.nodeService.hasAspect(record2, RecordsManagementModel.ASPECT_HELD));       
    }
    
    public void testSuperseded()
    {
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        // Test superseded with cuttof off
        NodeRef record0 = createTestRecord();
        this.nodeService.addAspect(record0, RecordsManagementModel.ASPECT_SUPERSEDED, null);
        assertTrue(this.nodeService.hasAspect(record0, RecordsManagementModel.ASPECT_SUPERSEDED));
        assertFalse(this.nodeService.hasAspect(record0, RecordsManagementModel.ASPECT_CUTOFF));
        
        // Test cutOff on superseded with process hold == true
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_CUTOFF_ON_SUPERSEDED, true);
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_PROCESS_HOLD, true);
        NodeRef record1 = createTestRecord();
        this.nodeService.addAspect(record1, RecordsManagementModel.ASPECT_SUPERSEDED, null);
        assertTrue(this.nodeService.hasAspect(record1, RecordsManagementModel.ASPECT_SUPERSEDED));
        assertTrue(this.nodeService.hasAspect(record1, RecordsManagementModel.ASPECT_CUTOFF));
        assertTrue(this.nodeService.hasAspect(record1, RecordsManagementModel.ASPECT_HELD));
        
        // test cutOff on superseded with process hold == false
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_PROCESS_HOLD, false);
        NodeRef record2 = createTestRecord();
        this.nodeService.addAspect(record2, RecordsManagementModel.ASPECT_SUPERSEDED, null);
        assertTrue(this.nodeService.hasAspect(record2, RecordsManagementModel.ASPECT_SUPERSEDED));
        assertTrue(this.nodeService.hasAspect(record2, RecordsManagementModel.ASPECT_CUTOFF));
        assertFalse(this.nodeService.hasAspect(record2, RecordsManagementModel.ASPECT_HELD));       
    }
    
    public void testCutoff()
    {
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        // Test cutoff with processHold == true
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_PROCESS_HOLD, true);
        NodeRef record0 = createTestRecord();
        this.nodeService.addAspect(record0, RecordsManagementModel.ASPECT_CUTOFF, null);
        assertTrue(this.nodeService.hasAspect(record0, RecordsManagementModel.ASPECT_CUTOFF));
        assertTrue(this.nodeService.hasAspect(record0, RecordsManagementModel.ASPECT_HELD));
        
        // Test cutoff with processHold == false
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_PROCESS_HOLD, false);
        NodeRef record1 = createTestRecord();
        this.nodeService.addAspect(record1, RecordsManagementModel.ASPECT_CUTOFF, null);
        assertTrue(this.nodeService.hasAspect(record1, RecordsManagementModel.ASPECT_CUTOFF));
        assertFalse(this.nodeService.hasAspect(record1, RecordsManagementModel.ASPECT_HELD));
    }
    
    @SuppressWarnings("deprecation")
    public void xxxtestHeld()
        throws Exception
    {
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        // Add disposition instructions
        addDestroyDispositionInstructions(this.filePlan);
        
        // Test dicretionary hold
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_PROCESS_HOLD, true);
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_DISPOSITION_INSTRUCTIONS, STRING_PROP_VALUE);
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_DISCRETIONARY_HOLD, true);
        NodeRef record0 = createTestRecord();
        this.nodeService.addAspect(record0, RecordsManagementModel.ASPECT_CUTOFF, null);
        assertTrue(this.nodeService.hasAspect(record0, RecordsManagementModel.ASPECT_HELD));
        assertEquals(STRING_PROP_VALUE, this.nodeService.getProperty(record0, RecordsManagementModel.PROP_HOLD_UNTIL_EVENT));
        assertEquals(Boolean.FALSE, this.nodeService.getProperty(record0, RecordsManagementModel.PROP_FROZEN));    
        Date holdUntil = (Date)this.nodeService.getProperty(record0, RecordsManagementModel.PROP_HOLD_UNTIL);
        assertNotNull(holdUntil);
        Calendar temp = Calendar.getInstance();
        temp.setTime(holdUntil);
        assertEquals(Calendar.getInstance().get(Calendar.YEAR)+100, temp.get(Calendar.YEAR));
        
        // Take record off hold and ensure that the immediate destroy takes place
        this.nodeService.removeAspect(record0, RecordsManagementModel.ASPECT_HELD);
        // Wait while the async aciton takes place
        assertFalse(this.nodeService.exists(record0));
        
        // Make disposition not immediate
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_DESTROY_IMMEDIATELY, false);
        
        // Test non-discretionary hold
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_DISCRETIONARY_HOLD, false);
        this.nodeService.setProperty(this.filePlan, RecordsManagementModel.PROP_HOLD_PERIOD_UNIT, RecordsManagementModel.CAT_DATEPERIOD_ANNUALLY);
        NodeRef record1 = createTestRecord();
        this.nodeService.addAspect(record1, RecordsManagementModel.ASPECT_CUTOFF, null);
        assertTrue(this.nodeService.hasAspect(record1, RecordsManagementModel.ASPECT_HELD));
        assertEquals(STRING_PROP_VALUE, this.nodeService.getProperty(record1, RecordsManagementModel.PROP_HOLD_UNTIL_EVENT));
        assertEquals(Boolean.FALSE, this.nodeService.getProperty(record1, RecordsManagementModel.PROP_FROZEN));    
        Date holdUntil1 = (Date)this.nodeService.getProperty(record1, RecordsManagementModel.PROP_HOLD_UNTIL);
        assertNotNull(holdUntil1);
        Calendar temp1 = Calendar.getInstance();
        temp1.setTime(holdUntil1);
        assertEquals(Calendar.getInstance().get(Calendar.YEAR)+1, temp1.get(Calendar.YEAR));
        
        // Take the record off hold and ensure that the immediate destroy does not take place
        this.nodeService.removeAspect(record1, RecordsManagementModel.ASPECT_HELD);
        assertTrue(this.nodeService.exists(record1));
    }
    
    /** Test disposition actions directly **/
    
    public void testDestroyDispositionAction()
    {
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        // Create the action and record
        Action action = this.actionService.createAction("destroyDispositionAction");
        NodeRef record = createTestRecord();
        
        // Create a version history
        this.nodeService.addAspect(record, ContentModel.ASPECT_VERSIONABLE, null);
        this.versionService.createVersion(record, new HashMap<String, Serializable>(1));
        this.versionService.createVersion(record, new HashMap<String, Serializable>(1));
        
        // Just check that we have the node and the version history
        assertTrue(this.nodeService.exists(record));
        assertNotNull(this.versionService.getVersionHistory(record));
        
        // Destory the node by executing disposition action
        this.actionService.executeAction(action, record);
        
        // Check that it is gone
        assertFalse(this.nodeService.exists(record));
        assertNull(this.versionService.getVersionHistory(record));
    }
    
    public void testTransferDispositionAction()
    {
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        // Create the action and record
        Action action = this.actionService.createAction("transferDispositionAction");
        action.setParameterValue("location", "/app:company_home");
        NodeRef record = createTestRecord();
        
        // Execute the action
        this.actionService.executeAction(action, record);
        
        // Check that the record has been moved
        ChildAssociationRef parent = this.nodeService.getPrimaryParent(record);
        assertFalse(parent.getParentRef().equals(this.filePlan));
    }
    
    public void testAccessionDispositionAction()
        throws Exception
    {
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        // Create the action and record
        Action action = this.actionService.createAction("accessionDispositionAction");
        NodeRef record = createTestRecord();
        
        // Execute the action
        this.actionService.executeAction(action, record);
    }    
    
    /** Test date calculations **/
    
    public void testDateCalculations()
    {
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        // None
        Calendar none = calculateDateInterval(RecordsManagementModel.CAT_DATEPERIOD_NONE, 1);
        assertNull(none);
        
        // TBD
        Calendar tbd = calculateDateInterval(RecordsManagementModel.CAT_DATEPERIOD_TBD, 1);
        assertNull(tbd);
        
        // Daily
        Calendar daily = calculateDateInterval(RecordsManagementModel.CAT_DATEPERIOD_DAILY, 24);
        assertNotNull(daily);
        assertEquals(2006, daily.get(Calendar.YEAR));
        assertEquals(3, daily.get(Calendar.MONTH));
        assertEquals(19, daily.get(Calendar.DAY_OF_MONTH));
        
        // Weekly
        Calendar weekly = calculateDateInterval(RecordsManagementModel.CAT_DATEPERIOD_WEEKLY, 2);
        assertNotNull(weekly);
        assertEquals(2006, weekly.get(Calendar.YEAR));
        assertEquals(3, weekly.get(Calendar.MONTH));
        assertEquals(9, weekly.get(Calendar.DAY_OF_MONTH));
        
        // Monthly        
        Calendar monthly = calculateDateInterval(RecordsManagementModel.CAT_DATEPERIOD_MONTHLY, 9);
        assertNotNull(monthly);
        assertEquals(2006, monthly.get(Calendar.YEAR));
        assertEquals(11, monthly.get(Calendar.MONTH));
        assertEquals(26, monthly.get(Calendar.DAY_OF_MONTH));
        
        // Annually
        Calendar annually = calculateDateInterval(RecordsManagementModel.CAT_DATEPERIOD_ANNUALLY, 1);
        assertNotNull(annually);
        assertEquals(2007, annually.get(Calendar.YEAR));
        assertEquals(2, annually.get(Calendar.MONTH));
        assertEquals(26, annually.get(Calendar.DAY_OF_MONTH));
        
        // Month End
        Calendar monthEnd = calculateDateInterval(RecordsManagementModel.CAT_DATEPERIOD_MONTHEND, 2);
        assertNotNull(monthEnd);
        assertEquals(2006, monthEnd.get(Calendar.YEAR));
        assertEquals(3, monthEnd.get(Calendar.MONTH));
        assertEquals(30, monthEnd.get(Calendar.DAY_OF_MONTH));
        
        // Quater End
        Calendar quaterEnd = calculateDateInterval(RecordsManagementModel.CAT_DATEPERIOD_QUATEREND, 2);
        assertNotNull(quaterEnd);
        assertEquals(2006, quaterEnd.get(Calendar.YEAR));
        assertEquals(5, quaterEnd.get(Calendar.MONTH));
        assertEquals(30, quaterEnd.get(Calendar.DAY_OF_MONTH));
        
        // Year End
        Calendar yearEnd = calculateDateInterval(RecordsManagementModel.CAT_DATEPERIOD_YEAREND, 5);
        assertNotNull(yearEnd);
        assertEquals(2010, yearEnd.get(Calendar.YEAR));
        assertEquals(11, yearEnd.get(Calendar.MONTH));
        assertEquals(31, yearEnd.get(Calendar.DAY_OF_MONTH));
        
        // Fin Year End
    }
    
    private Calendar calculateDateInterval(NodeRef unit, int value)
    {
        Calendar cal = null;
        String script = "var cat = utils.getNodeFromString(\"" + unit.toString() + "\");\n" +  
                        "var date = rm.calculateDateInterval(cat, " + value + ", new Date(2006, 2, 26, 12, 0));\n" +
                        "logger.log(\"Calculated date is: \" + date);\n" +
                        "date;";

        Object result = this.scriptService.executeScriptString(script, null);
        if (result != null)
        {
            Date date = (Date)new ValueConverter().convertValueForRepo((Serializable)result);
            cal = Calendar.getInstance();
            cal.setTime(date);
        }
        return cal;
    }
    
    /** Test the sceduled scripts **/
    
    public void testScheduledCutOffScript()
    {
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        
        NodeRef record = createTestRecord();
        this.nodeService.addAspect(record, RecordsManagementModel.ASPECT_CUTOFF_SCHEDULE, null);
        this.nodeService.setProperty(record, RecordsManagementModel.PROP_CUTOFF_DATE_TIME, calendar.getTime());
        
        assertFalse(this.nodeService.hasAspect(record, RecordsManagementModel.ASPECT_CUTOFF));
        
        ScriptLocation location = new ClasspathScriptLocation("alfresco/module/recordsManagement/script/scheduled/scheduled_cutoff.js");
        this.scriptService.executeScript(location, null);   
        
        assertTrue(this.nodeService.hasAspect(record, RecordsManagementModel.ASPECT_CUTOFF));
    }
    
    public void testScheduledRemoveHoldScript()
    {
        // Check whether the records management has been configured in
        if (isRMConfigured() == false)
        {
            return;
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        
        NodeRef record = createTestRecord();
        this.nodeService.addAspect(record, RecordsManagementModel.ASPECT_CUTOFF, null);
        this.nodeService.addAspect(record, RecordsManagementModel.ASPECT_HELD, null);
        this.nodeService.setProperty(record, RecordsManagementModel.PROP_FROZEN, true);
        this.nodeService.setProperty(record, RecordsManagementModel.PROP_HOLD_UNTIL, calendar.getTime());
        
        assertTrue(this.nodeService.hasAspect(record, RecordsManagementModel.ASPECT_HELD));
        
        ScriptLocation location = new ClasspathScriptLocation("alfresco/module/recordsManagement/script/scheduled/scheduled_removeHold.js");
        this.scriptService.executeScript(location, null);        
        
        assertTrue(this.nodeService.hasAspect(record, RecordsManagementModel.ASPECT_HELD));
        this.nodeService.setProperty(record, RecordsManagementModel.PROP_FROZEN, false);
        
        this.scriptService.executeScript(location, null);        
        
        assertFalse(this.nodeService.hasAspect(record, RecordsManagementModel.ASPECT_HELD));
    }

    // Execute every minute
    // 0 0/1 * * * ?
    
    /** Helper methods **/
    
    private NodeRef createTestRecord()
    {
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_NAME, "MyDoc" + GUID.generate() + ".txt");
        NodeRef doc1 = this.nodeService.createNode(
                                        this.filePlan, 
                                        ContentModel.ASSOC_CONTAINS, 
                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
                                        ContentModel.TYPE_CONTENT,
                                        props).getChildRef();
        
        return doc1;
    }

    private void addDestroyDispositionInstructions(NodeRef filePlan)
    {
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(RecordsManagementModel.PROP_DESTROY_IMMEDIATELY, true);        
        this.nodeService.addAspect(filePlan, RecordsManagementModel.ASPECT_DESTROY_INSTRUCTIONS, props);
    }
    
    private static class TempBinding implements ImporterBinding
    {   
        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterBinding#getValue(java.lang.String)
         */
        public String getValue(String key)
        {
            return null;
        }

        /*
         *  (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterBinding#getUUIDBinding()
         */
        public UUID_BINDING getUUIDBinding()
        {
            // always use create new strategy for bootstrap import
            return UUID_BINDING.UPDATE_EXISTING;
        }

        /*
         *  (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterBinding#searchWithinTransaction()
         */
        public boolean allowReferenceWithinTransaction()
        {
            return true;
        }

        /*
         *  (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterBinding#getExcludedClasses()
         */
        public QName[] getExcludedClasses()
        {
            // Note: Do not exclude any classes, we want to import all
            return new QName[] {};
        }
    }
    
}
