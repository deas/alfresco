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
package org.alfresco.module.phpIntegration;

import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.jscript.ClasspathScriptLocation;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.ScriptLocation;
import org.alfresco.service.cmr.repository.ScriptService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;

/**
 * @author Roy Wetherall
 */
public class PHPTest extends BaseSpringTest
{
    private static final String CLASSPATH_ROOT = "alfresco/module/phpIntegration/test/";
    
    private static final String TEST_SCRIPT = "<?php return \"SCRIPT_RESULT\" ?>";
    private static final String TEST_TEMPLATE = "<?php echo \"TEMPLATE_RESULT\" ?>"; 
    
    private NodeService nodeService;
    private ContentService contentService;
    @SuppressWarnings("unused")
    private TemplateService templateService;
    private ScriptService scriptService;
    private PHPProcessor phpProcessor;
    
    private StoreRef storeRef;
    private NodeRef rootNode;
    private NodeRef templateNodeRef;
    private NodeRef scriptNodeRef;

    /**
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        
        // Get references to the required beans
        this.nodeService = (NodeService)this.applicationContext.getBean("NodeService");
        this.contentService = (ContentService)this.applicationContext.getBean("ContentService");
        this.templateService = (TemplateService)this.applicationContext.getBean("TemplateService");
        this.scriptService = (ScriptService)this.applicationContext.getBean("scriptService");
        this.phpProcessor = (PHPProcessor)this.applicationContext.getBean("phpProcessor");
        
        // Create nodes used in the tests
        this.storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "phpTest_" + System.currentTimeMillis());
        this.rootNode = this.nodeService.getRootNode(this.storeRef);
        
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_NAME, "testTemplate.php");
        this.templateNodeRef = this.nodeService.createNode(
                this.rootNode, 
                ContentModel.ASSOC_CHILDREN, 
                ContentModel.ASSOC_CHILDREN, 
                ContentModel.TYPE_CONTENT, 
                props).getChildRef();
        ContentWriter contentWriter = this.contentService.getWriter(this.templateNodeRef, ContentModel.PROP_CONTENT, true);
        contentWriter.setEncoding("UTF-8");
        contentWriter.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(CLASSPATH_ROOT + "testTemplate.php");
        contentWriter.putContent(is);
        
        Map<QName, Serializable> props2 = new HashMap<QName, Serializable>(1);
        props2.put(ContentModel.PROP_NAME, "testScript.php");
        this.scriptNodeRef = this.nodeService.createNode(
                this.rootNode, 
                ContentModel.ASSOC_CHILDREN, 
                ContentModel.ASSOC_CHILDREN, 
                ContentModel.TYPE_CONTENT, 
                props2).getChildRef();
        ContentWriter contentWriter2 = this.contentService.getWriter(this.scriptNodeRef, ContentModel.PROP_CONTENT, true);
        contentWriter2.setEncoding("UTF-8");
        contentWriter2.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        InputStream is2 = this.getClass().getClassLoader().getResourceAsStream(CLASSPATH_ROOT + "testScript.php");
        contentWriter2.putContent(is2);        
    }
    
    /** ========= Test template processor implementation ========= */
    
    public void testTemplateFromNodeRef()
    {
        StringWriter out = new StringWriter();
        Map<String, Object> model = new HashMap<String, Object>();
        this.phpProcessor.process(this.templateNodeRef.toString(), model, out);
        
        assertEquals("TEMPLATE_RESULT", out.toString());      
    }
    
    public void testTemplateFromClasspath()
    {
        StringWriter out = new StringWriter();
        Map<String, Object> model = new HashMap<String, Object>();
        this.phpProcessor.process(CLASSPATH_ROOT + "testTemplate.php", model, out);
        
        assertEquals("TEMPLATE_RESULT", out.toString());        
    }
    
    public void testTemplateFromString()
    {
        StringWriter out = new StringWriter();
        Map<String, Object> model = new HashMap<String, Object>();
        this.phpProcessor.processString(TEST_TEMPLATE, model, out);
        
        assertEquals("TEMPLATE_RESULT", out.toString());
    }
    
    /** ========= Test script processor implementation ========= */
    
    public void testScriptExecutionFromScriptLocation()
    {
        Map<String, Object> model = new HashMap<String, Object>(1);
        ScriptLocation scriptLocation = new ClasspathScriptLocation(CLASSPATH_ROOT + "testScript.php");        
        Object result = this.phpProcessor.execute(scriptLocation, model);
        
        assertNotNull(result);
        assertEquals("SCRIPT_RESULT", result.toString());
    }
    
    public void testScriptExecutionFromNodeRef()
    {
        Map<String, Object> model = new HashMap<String, Object>(1);
        Object result = this.phpProcessor.execute(this.scriptNodeRef, ContentModel.PROP_CONTENT, model);
        
        assertNotNull(result);
        assertEquals("SCRIPT_RESULT", result.toString());
        
    }
    
    public void testScriptExecutionFromClasspath()
    {
        Map<String, Object> model = new HashMap<String, Object>(1);
        Object result = this.phpProcessor.execute(CLASSPATH_ROOT + "testScript.php", model);
        
        assertNotNull(result);
        assertEquals("SCRIPT_RESULT", result.toString());
    }
    
    public void testScriptExecutionFromString()
    {
        Map<String, Object> model = new HashMap<String, Object>(1);
        Object result = this.phpProcessor.executeString(TEST_SCRIPT, model);
        
        assertNotNull(result);
        assertEquals("SCRIPT_RESULT", result.toString());
    }    
    
    /** ========= Test execution from template and script services ========= */
    

    /** ========= Execute PHP test scripts ========= */
    
    public void testUnitTestMethods()
    {
        Map<String, Object> model = new HashMap<String, Object>();        
        this.scriptService.executeScript(CLASSPATH_ROOT + "testUnitTestMethods.php", model);
    }
    
    public void testGlobalVariables()
    {
        assertNotNull(this.templateNodeRef);
        
        Map<String, Object> model = new HashMap<String, Object>(6);
        model.put("testStore", this.storeRef);
        model.put("testString", "testString");
        model.put("testNumber", 1);
        model.put("nodeId", this.templateNodeRef.getId());
        model.put("storeId", this.storeRef.getIdentifier());
        model.put("testNode", this.templateNodeRef);
        // TODO test dates and other common types
        
        StringWriter out = new StringWriter();        
        this.phpProcessor.process(CLASSPATH_ROOT + "testModelAndGlobals.php", model, out);
        
        System.out.println("testGlobalVariables output:");
        System.out.println(out.toString());
    }
    
    public void testNamespaceMap()
    {
        Map<String, Object> model = new HashMap<String, Object>(6);   
        StringWriter out = new StringWriter();        
        this.phpProcessor.process(CLASSPATH_ROOT + "testNamespaceMap.php", model, out);
        
        System.out.println("testNamespaceMap output:");
        System.out.println(out.toString());
    }
    
    public void testNode()
    {
        // Create a folder
        Map<QName, Serializable> props3 = new HashMap<QName, Serializable>(1);
        props3.put(ContentModel.PROP_NAME, "testFolder");
        NodeRef testFolder = this.nodeService.createNode(
                this.rootNode, 
                ContentModel.ASSOC_CHILDREN, 
                ContentModel.ASSOC_CHILDREN, 
                ContentModel.TYPE_FOLDER, 
                props3).getChildRef();
        
        // Create the node
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(2);
        props.put(ContentModel.PROP_NAME, "testNode.txt");
        props.put(ContentModel.PROP_AUTHOR, "Roy Wetherall");
        NodeRef nodeRef = this.nodeService.createNode(
                testFolder, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "testNode.txt"), 
                ContentModel.TYPE_CONTENT, 
                props).getChildRef(); 
        
        // Add some test content
        ContentWriter contentWriter = this.contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
        contentWriter.setEncoding("UTF-8");
        contentWriter.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        contentWriter.putContent("test content");
        
        // Add a couple of aspects
        this.nodeService.addAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE, null);
        this.nodeService.addAspect(nodeRef, ContentModel.ASPECT_CLASSIFIABLE, null);     
        
        // Create a second test node
        Map<QName, Serializable> props2 = new HashMap<QName, Serializable>(2);
        props2.put(ContentModel.PROP_NAME, "testNode2.txt");
        props2.put(ContentModel.PROP_AUTHOR, "Roy Wetherall");
        NodeRef nodeRef2 = this.nodeService.createNode(
                testFolder, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "testNode2.txt"), 
                ContentModel.TYPE_CONTENT, 
                props2).getChildRef(); 
        
        // Add association from one node to the other
        this.nodeService.addAspect(nodeRef, ContentModel.ASPECT_REFERENCEABLE, null);
        this.nodeService.createAssociation(nodeRef, nodeRef2, ContentModel.ASSOC_REFERENCES);
        
        // Create the model
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put("testFolder", testFolder);
        model.put("testNode", nodeRef);
        
        System.out.println("Executing testNode.php ... ");
        
        // Process the test script
        StringWriter out = new StringWriter();        
        this.phpProcessor.process(CLASSPATH_ROOT + "testNode.php", model, out);
        System.out.println("testNode output:");
        System.out.println(out.toString());
        
        System.out.println("Executing testNodeSave.php ... ");
        
        StringWriter out2 = new StringWriter();        
        this.phpProcessor.process(CLASSPATH_ROOT + "testNodeSave.php", model, out2);
        System.out.println("testNodeSave output:");
        System.out.println(out2.toString());
        
        // Check the details of the node after the save 
        System.out.println("Checking properties of node " + nodeRef.getId());
        assertEquals("changed.txt", this.nodeService.getProperty(nodeRef, ContentModel.PROP_NAME));
        assertEquals("Mr Trouble", this.nodeService.getProperty(nodeRef, ContentModel.PROP_AUTHOR));
        
        System.out.println("Executing testCopyMove.php ... ");
        
        // Set the titled property value
        this.nodeService.setProperty(nodeRef, ContentModel.PROP_TITLE, "My Title");
        
        StringWriter out3 = new StringWriter();        
        this.phpProcessor.process(CLASSPATH_ROOT + "testCopyMove.php", model, out3);
        System.out.println("testCopyMove output:");
        System.out.println(out3.toString());
        
        System.out.println("Executing testContentProperties.php ... ");
        
        StringWriter out4 = new StringWriter();        
        this.phpProcessor.process(CLASSPATH_ROOT + "testContentProperties.php", model, out4);
        System.out.println("testContentProperties output:");
        System.out.println(out4.toString());
        
//        System.out.println("Tempory ... ");        
//        Map<String, Object> model2 = new HashMap<String, Object>(1);
//        model2.put("document", nodeRef);        
//        StringWriter out5 = new StringWriter();        
//        this.phpProcessor.process("alfresco/module/phpIntegration/script/append_copyright.php", model2, out5);
//        System.out.println("result ...");
//        System.out.println(out5.toString());
//        ContentReader contentReader = this.contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
//        System.out.println("appended content ... " + contentReader.getContentString());
        
    }
    
    public void testFileFolder()
    {
        // Create a folder
        Map<QName, Serializable> props3 = new HashMap<QName, Serializable>(1);
        props3.put(ContentModel.PROP_NAME, "testFolder");
        NodeRef testFolder = this.nodeService.createNode(
                this.rootNode, 
                ContentModel.ASSOC_CHILDREN, 
                ContentModel.ASSOC_CHILDREN, 
                ContentModel.TYPE_FOLDER, 
                props3).getChildRef();
        
        // Create the node
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(2);
        props.put(ContentModel.PROP_NAME, "testNode.txt");
        props.put(ContentModel.PROP_AUTHOR, "Roy Wetherall");
        NodeRef nodeRef = this.nodeService.createNode(
                testFolder, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "testNode.txt"), 
                ContentModel.TYPE_CONTENT, 
                props).getChildRef(); 
        
        // Add some test content
        ContentWriter contentWriter = this.contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
        contentWriter.setEncoding("UTF-8");
        contentWriter.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        contentWriter.putContent("test content");
                
        Map<String, Object> model = new HashMap<String, Object>(2);   
        model.put("file", nodeRef);
        model.put("folder", testFolder);
        StringWriter out = new StringWriter();        
        this.phpProcessor.process(CLASSPATH_ROOT + "testFileFolder.php", model, out);
        
        System.out.println("testFileFolder output:");
        System.out.println(out.toString());
    }

}
