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

package org.alfresco.deployment.impl.dmr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.model.ContentModel;
import org.alfresco.model.WCMAppModel;
import org.alfresco.repo.avm.util.BulkLoader;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.avm.AVMException;
import org.alfresco.service.cmr.avm.AVMNodeDescriptor;
import org.alfresco.service.cmr.avm.AVMService;
import org.alfresco.service.cmr.avm.deploy.DeploymentCallback;
import org.alfresco.service.cmr.avm.deploy.DeploymentEvent;
import org.alfresco.service.cmr.avm.deploy.DeploymentReport;
import org.alfresco.service.cmr.avm.deploy.DeploymentReportCallback;
import org.alfresco.service.cmr.avm.deploy.DeploymentService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.GUID;
import org.alfresco.util.ISO9075;
import org.alfresco.util.NameMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

/**
 * End to end test of deployment to an DM store.
 * 
 * DMR target
 * 
 * @author mrogers
 */
public class DMDeploymentTargetTest extends TestCase
{   
    private String TEST_USER = "admin";
    private String TEST_PASSWORD = "admin";
    private String TEST_TARGET = "alfresco";
    private int    TEST_PORT = 50500;
    
    /**
     * Which store to use for our deployed content
     */
    private String storeRef = "workspace://SpacesStore";
    
    private static Log logger = LogFactory.getLog(DMDeploymentTargetTest.class);
    
    /**
     * The application context.
     */
    protected static ApplicationContext fContext;
    
    /**
     * The AVMService we are testing.
     */
    protected static AVMService avmService;
    protected static AuthenticationService fAuthService;
    protected static SearchService searchService; 
    protected static NodeService nodeService; 
    protected static FileFolderService fileFolderService; 
    protected static RootLocatorImpl rootLocator = new RootLocatorImpl();
    
    DeploymentService service = null;

    protected void setUp() throws Exception
    {
        super.setUp();
        if (fContext == null)
        {
            fContext = ApplicationContextHelper.getApplicationContext();
          
        }
        avmService = (AVMService)fContext.getBean("AVMService");
        service = (DeploymentService)fContext.getBean("DeploymentService");  
        fAuthService = (AuthenticationService)fContext.getBean("AuthenticationService");
        searchService = (SearchService)fContext.getBean("SearchService");
        nodeService = (NodeService)fContext.getBean("NodeService");
        fileFolderService = (FileFolderService)fContext.getBean("FileFolderService");
        fAuthService.authenticate(AuthenticationUtil.getAdminUserName(), "admin".toCharArray());
        rootLocator.setDefaultLocation("/app:company_home/cm:wcm_deployed");

    }
    
    protected void tearDown() throws Exception
    {
        super.tearDown();        
    }
    
    /**
     * Test basic puts the DMR target through its paces.
     * @throws Exception
     */
    public void testBasic()
        throws Exception
    {

        logger.debug("in test basic");
        
            NameMatcher matcher = (NameMatcher)fContext.getBean("globalPathExcluder");
            
            String storeName = GUID.generate();
            
            avmService.createStore(storeName);
            
            /**
             *  set up our test tree
             */
            avmService.createDirectory(storeName + ":/", "a");

            avmService.createDirectory(storeName + ":/a", "b");
            avmService.createDirectory(storeName + ":/a/b", "c");
            
            avmService.createDirectory(storeName + ":/", "d");
            avmService.createDirectory(storeName + ":/d", "e");
            avmService.createDirectory(storeName + ":/d/e", "f");
            
            avmService.createFile(storeName + ":/a/b/c", "foo").close();
            String fooText="I am main:/a/b/c/foo";
            
            {
                ContentWriter writer = avmService.getContentWriter(storeName + ":/a/b/c/foo", true);
                writer.setEncoding("UTF-8");
                writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                writer.putContent("I am main:/a/b/c/foo"); 
            }
            avmService.createFile(storeName + ":/a/b/c", "bar").close();
            {
                ContentWriter writer = avmService.getContentWriter(storeName + ":/a/b/c/bar", true);
            
                // Force a conversion
                writer.setEncoding("UTF-16");
                writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                writer.putContent("I am main:/a/b/c/bar");
            }
            
            String buffyText = "This is test data: Buffy the Vampire Slayer is an Emmy Award-winning and Golden Globe-nominated American cult television series that aired from March 10, 1997 until May 20, 2003. The series was created in 1997 by writer-director Joss Whedon under his production tag, Mutant Enemy Productions with later co-executive producers being Jane Espenson, David Fury, and Marti Noxon. The series narrative follows Buffy Summers (played by Sarah Michelle Gellar), the latest in a line of young women chosen by fate to battle against vampires, demons, and the forces of darkness as the Slayer. Like previous Slayers, Buffy is aided by a Watcher, who guides and trains her. Unlike her predecessors, Buffy surrounds herself with a circle of loyal friends who become known as the Scooby Gang.";
            avmService.createFile(storeName + ":/a/b", "buffy").close();
            {
                ContentWriter writer = avmService.getContentWriter(storeName + ":/a/b/buffy", true);
                // Force a conversion
                writer.setEncoding("UTF-16");
                writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                writer.putContent(buffyText);
            }
            avmService.setNodeProperty(storeName + ":/a/b/buffy", ContentModel.PROP_SUBJECT, new PropertyValue(null, "This is a test"));

            /**
             * The zander file is bigger than one buffer full
             */
            StringBuffer zanderBuffer = new StringBuffer();
            for(int i = 0; i < 30000 ; i++)
            {
                zanderBuffer.append(String.valueOf(i));
            }
            
            avmService.createFile(storeName + ":/a/b", "zander").close();
            {
                ContentWriter writer = avmService.getContentWriter(storeName + ":/a/b/zander", true);
            
                // Force a conversion
                writer.setEncoding("ISO-8859-1");
                writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                writer.putContent(zanderBuffer.toString());
            }
            avmService.createFile(storeName + ":/a/b", "fudge.bak").close();
            
            /**
             * Now set up the deployment report
             */
            DeploymentReport report = new DeploymentReport();
            List<DeploymentCallback> callbacks = new ArrayList<DeploymentCallback>();
            callbacks.add(new DeploymentReportCallback(report));
            
            
            logger.debug("first deployment");
            
            /**
             * Do our first deployment - should deploy the basic tree defined above
             * fudge.bak should be excluded due to the matcher.
             */
            service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
            Set<DeploymentEvent> firstDeployment = new HashSet<DeploymentEvent>();
            firstDeployment.addAll(report.getEvents());
            // validate the deployment report
            assertTrue("first deployment no start", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.START, null, TEST_TARGET)));
            assertTrue("first deployment no finish", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.END, null, TEST_TARGET)));
            assertTrue("first deployment wrong size", firstDeployment.size() == 12);
            assertTrue("Update missing: /a", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.CREATED, null, "/a")));
            assertTrue("Update missing: /a/b", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.CREATED, null, "/a/b")));
            assertTrue("Update missing: /a/b/c", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.CREATED, null, "/a/b/c")));
            assertTrue("Update missing: /d/e", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.CREATED, null, "/d/e")));
            assertTrue("Update missing: /a/b/c/foo", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.CREATED, null, "/a/b/c/foo")));
            assertTrue("Update missing: /a/b/c/bar", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.CREATED, null, "/a/b/c/bar")));
            assertTrue("Update missing: /a/b/buffy", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.CREATED, null, "/a/b/buffy")));
            assertTrue("Update missing: /a/b/zander", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.CREATED, null, "/a/b/zander")));
            assertFalse("Fudge has not been excluded", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.CREATED, null, "/a/b/fudge.bak")));
            
            // Check that files exist on target store
            {
                // Check the "buffy" file.
                NodeRef buffyNodeRef = getDestNode(storeName, "/a/b/buffy");
                assertNotNull("buffy file is null", buffyNodeRef);

                assertTrue("buffy file is not a file", !fileFolderService.getFileInfo(buffyNodeRef).isFolder());
                
                ContentReader buffyContent = fileFolderService.getReader(buffyNodeRef);
                assertNotNull("content URL is null", buffyContent.getContentUrl());
                
                // Check Encoding
                assertEquals("encoding is wrong", "UTF-16", buffyContent.getEncoding());
                assertEquals("mime type is wrong", MimetypeMap.MIMETYPE_TEXT_PLAIN, buffyContent.getMimetype());
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(buffyContent.getContentInputStream(), "UTF-16"));
                String text = reader.readLine();
//                System.out.println("text is" + text);
                assertTrue("UTF-16 buffy text is not correct", buffyText.equals(text));          
                // Check the custom property.
                Map<QName, Serializable> props = nodeService.getProperties(buffyNodeRef);
                assertNotNull("Subject is null", props.get(ContentModel.PROP_SUBJECT));            
            }
            
            {
                // Check the contents of the "foo" file.
                NodeRef fooNodeRef = getDestNode(storeName, "/a/b/c/foo");
                assertNotNull("foo file not created", fooNodeRef);
                assertTrue("foo file is not a file", !fileFolderService.getFileInfo(fooNodeRef).isFolder());
                ContentReader fooContent = fileFolderService.getReader(fooNodeRef);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fooContent.getContentInputStream(), "UTF-8"));
                String text = reader.readLine();
//              System.out.println("text is" + text);
                assertTrue("UTF-8 foo text is not correct", fooText.equals(text));         
            }
            
            {
                // Check the contents of the "zander" file.
                NodeRef zanderNodeRef = getDestNode(storeName, "/a/b/zander");
                assertNotNull("zander file not created", zanderNodeRef);
                assertTrue("zander file is not a file", !fileFolderService.getFileInfo(zanderNodeRef).isFolder());
                ContentReader zanderContent = fileFolderService.getReader(zanderNodeRef);
                BufferedReader reader = new BufferedReader(new InputStreamReader(zanderContent.getContentInputStream(), "ISO-8859-1"));
                assertEquals("encoding is wrong", "ISO-8859-1", zanderContent.getEncoding());
                String text = reader.readLine();
//              System.out.println("text is" + text);
                assertEquals("ISO-8859-1 zander text format is not correct", text, zanderBuffer.toString());
            }
//            for (DeploymentEvent event : report)
//            {
//                System.out.println(event);
//            }
//            
            /**
             *  Now do the same deployment again - should just get start and end events.
             */
            report = new DeploymentReport();
            callbacks = new ArrayList<DeploymentCallback>();
            callbacks.add(new DeploymentReportCallback(report));
            service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
            int count = 0;
            for (DeploymentEvent event : report)
            {
                System.out.println(event);
                count++;
            }
            assertEquals(2, count);
            
            logger.debug("update contents of the buffy file");
            
            /**
             * Now update the contents of the buffy file
             */
            String updatedBuffyText = "Buffy the Vampire Slayer is a 1992 action-comedy-horror film about \"valley girl\" cheerleader Buffy (Kristy Swanson) chosen by fate to fight and kill vampires. The movie is a light parody which plays on the cliches of typical horror films. It also led to the darker and much more popular TV series of the same name, which starred Sarah Michelle Gellar and was created and executive produced by screenwriter Joss Whedon. Whedon often detailed how the TV series was a much closer rendering of his vision than the movie, which was compromised by commercial concerns and differences in interpretation. The film is now considered a relatively minor chapter in the broader Buffy legacy. When the film was first released, it was moderately successful and received mixed reviews from critics[2].";
            {
                ContentWriter writer = avmService.getContentWriter(storeName + ":/a/b/buffy", true);
                // Force a conversion
                writer.setEncoding("UTF-16");
                writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                writer.putContent(updatedBuffyText);
            }
            service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
            {
                // Check the "buffy" file.
                NodeRef buffyNodeRef = getDestNode(storeName, "/a/b/buffy");
                assertNotNull("buffy file is null", buffyNodeRef);
                assertTrue("buffy file is not a file", !fileFolderService.getFileInfo(buffyNodeRef).isFolder());
            
                ContentReader buffyContent = fileFolderService.getReader(buffyNodeRef);
                assertNotNull("content URL is null", buffyContent.getContentUrl());
            
                // Check Encoding
                assertEquals("encoding is wrong", "UTF-16", buffyContent.getEncoding());
                assertEquals("mime type is wrong", MimetypeMap.MIMETYPE_TEXT_PLAIN, buffyContent.getMimetype());
            
                BufferedReader reader = new BufferedReader(new InputStreamReader(buffyContent.getContentInputStream(), "UTF-16"));
                String text = reader.readLine();
                assertTrue("UTF-16 buffy text is not correct", updatedBuffyText.equals(text));
            }         
            
            /**
             * now remove a single file in a deployment
             */
            logger.debug("now remove a single file /a/b/c");
            avmService.removeNode(storeName + ":/a/b/c", "bar");
            report = new DeploymentReport();
            callbacks = new ArrayList<DeploymentCallback>();
            callbacks.add(new DeploymentReportCallback(report));
            service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
               Set<DeploymentEvent> smallUpdate = new HashSet<DeploymentEvent>();
            smallUpdate.addAll(report.getEvents());
            for (DeploymentEvent event : report)
            {
                System.out.println(event);
            }
            assertEquals(3, smallUpdate.size());
            assertTrue("Bar not deleted", smallUpdate.contains(new DeploymentEvent(DeploymentEvent.Type.DELETED, null, "/a/b/c/bar")));
            
            /**
             *  Now create a new dir and file and remove a node in a single deployment 
             */
            logger.debug("create new file (d/jonathan and remove a/b");
            avmService.createFile(storeName + ":/d", "jonathan").close();
            avmService.removeNode(storeName + ":/a/b");
            
            report = new DeploymentReport();
            callbacks = new ArrayList<DeploymentCallback>();
            callbacks.add(new DeploymentReportCallback(report));

            
            service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
            count = 0;
            for (DeploymentEvent event : report)
            {
                System.out.println(event);
                count++;
            }
            assertEquals(4, count);
            
            /**
             * Replace a single directory with a file
             */
            logger.debug("replace a single directory with a file d/e");
            avmService.removeNode(storeName + ":/d/e");
            avmService.createFile(storeName + ":/d", "e").close();
            
            report = new DeploymentReport();
            callbacks = new ArrayList<DeploymentCallback>();
            callbacks.add(new DeploymentReportCallback(report));

            service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
            count = 0;
            for (DeploymentEvent event : report)
            {
                System.out.println(event);
                count++;
            }
            assertEquals(3, count);
            
            /**
             * Create a few files
             * 
             * Also replaces a file (e) with a directory.
             */
            avmService.removeNode(storeName + ":/d/e");
            avmService.createDirectory(storeName + ":/d", "e");
            avmService.createFile(storeName + ":/d/e", "Warren.txt").close();
            avmService.createFile(storeName + ":/d/e", "It's a silly name.txt").close();
            report = new DeploymentReport();
            callbacks = new ArrayList<DeploymentCallback>();
            callbacks.add(new DeploymentReportCallback(report));

            service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
            count = 0;
            for (DeploymentEvent event : report)
            {
                System.out.println(event);
                count++;
            }
            assertEquals(5, count);
    }
    
    /**
     * Test the lifecycle of a property (Create, Update, Delete) on a file and directory
     * @throws Exception
     */
    public void testCRUDProperty() throws Exception
    {
        String storeName = GUID.generate();
        
        avmService.createStore(storeName);
        
        NameMatcher matcher = (NameMatcher)fContext.getBean("globalPathExcluder");
        DeploymentReport report = new DeploymentReport();
        List<DeploymentCallback> callbacks = new ArrayList<DeploymentCallback>();
        callbacks.add(new DeploymentReportCallback(report));
        
        /**
         *  set up our test tree
         */
        avmService.createDirectory(storeName + ":/", "scooby");
        avmService.createFile(storeName + ":/scooby", "willow").close();
        
        String dirSubject = "The scooby gang";
        String fileSubject = "Willow";
        
        List<String> colours = new ArrayList<String>();
        colours.add("Red");
        colours.add("Blue");
        colours.add("Green");
        
        QName coloursQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "colours");

        avmService.setNodeProperty(storeName + ":/scooby", ContentModel.PROP_SUBJECT, new PropertyValue(null, dirSubject)); 
        
        /**
         * Set various properties of type string, ml_text, boolean, date, array<String>
         */
        avmService.setNodeProperty(storeName + ":/scooby/willow", ContentModel.PROP_SUBJECT, new PropertyValue(null, fileSubject));
        avmService.setNodeProperty(storeName + ":/scooby/willow", ContentModel.PROP_TITLE, new PropertyValue(DataTypeDefinition.MLTEXT, "title"));
        avmService.setNodeProperty(storeName + ":/scooby/willow", ContentModel.PROP_DESCRIPTION, new PropertyValue(null, "description"));
        avmService.setNodeProperty(storeName + ":/scooby/willow", ContentModel.PROP_MODEL_PUBLISHED_DATE, new PropertyValue(null, new Date()));
        avmService.setNodeProperty(storeName + ":/scooby/willow", ContentModel.PROP_MODEL_ACTIVE, new PropertyValue(null, Boolean.TRUE));
        avmService.setNodeProperty(storeName + ":/scooby/willow", coloursQName, new PropertyValue(DataTypeDefinition.ANY, (Serializable)colours)); 
        /**
         * Now deploy it
         */
        service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
        
        {

            NodeRef destWillowNodeRef = getDestNode(storeName, "/scooby/willow");
            AVMNodeDescriptor srcWillowFile = avmService.lookup(-1, storeName  +":/scooby/willow"); 
            assertNotNull("dest willow file is null", destWillowNodeRef);
            Map<QName, Serializable> willowProps = nodeService.getProperties(destWillowNodeRef);
            
            assertTrue("Subject is null", willowProps.containsKey(ContentModel.PROP_SUBJECT)); 
            assertTrue("Title is null", willowProps.containsKey(ContentModel.PROP_TITLE));
            assertTrue("Description is null", willowProps.containsKey(ContentModel.PROP_DESCRIPTION));
            assertTrue("Model Published is null", willowProps.containsKey(ContentModel.PROP_MODEL_PUBLISHED_DATE));
            assertTrue("Model Active", willowProps.containsKey(ContentModel.PROP_MODEL_ACTIVE));
            assertTrue("Colours", willowProps.containsKey(coloursQName));
            
            assertEquals("Subject content is not correct", fileSubject, willowProps.get(ContentModel.PROP_SUBJECT).toString());
            
            // check guid of willow file

            assertEquals("Willow guids mismatch", srcWillowFile.getGuid(), willowProps.get(WCMAppModel.PROP_GUID));
            assertTrue("Title is missing", willowProps.containsKey(ContentModel.PROP_TITLE)); 
            assertTrue("Description is missing", willowProps.containsKey(ContentModel.PROP_DESCRIPTION)); 
            assertTrue("Active Property is missing", willowProps.containsKey(ContentModel.PROP_MODEL_ACTIVE)); 
            assertTrue("Published Date property is missing", willowProps.containsKey(ContentModel.PROP_MODEL_PUBLISHED_DATE)); 

            assertEquals(willowProps.get(ContentModel.PROP_TITLE).getClass().getName(), "java.lang.String");
            assertEquals(willowProps.get(ContentModel.PROP_MODEL_ACTIVE).getClass().getName(), "java.lang.Boolean");
            assertEquals(willowProps.get(ContentModel.PROP_MODEL_PUBLISHED_DATE).getClass().getName(), "java.util.Date");
            assertEquals(willowProps.get(coloursQName).getClass().getName(), "java.util.ArrayList");
            
            // Now check the scooby dir
            NodeRef destScoobyNodeRef = getDestNode(storeName, "/scooby");
            assertNotNull("dest scooby dir is null", destScoobyNodeRef);
            AVMNodeDescriptor srcScoobyDir = avmService.lookup(-1, storeName  +":/scooby"); 
            
            Map<QName, Serializable> scoobyProps = nodeService.getProperties(destScoobyNodeRef);
        
            assertTrue("Subject is missing", scoobyProps.containsKey(ContentModel.PROP_SUBJECT)); 
            assertEquals("Subject content is not correct", dirSubject, scoobyProps.get(ContentModel.PROP_SUBJECT));
            
            // check guid of scooby dir
            assertEquals("scooby guids mismatch", srcScoobyDir.getGuid(), scoobyProps.get(WCMAppModel.PROP_GUID));
        }
        
        /**
         * Now update the properties
         */
        avmService.setNodeProperty(storeName + ":/scooby", ContentModel.PROP_SUBJECT, new PropertyValue(null, "update" + dirSubject)); 
        avmService.setNodeProperty(storeName + ":/scooby/willow", ContentModel.PROP_SUBJECT, new PropertyValue(null, "update" + fileSubject));
           service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
        {

            AVMNodeDescriptor srcWillowFile = avmService.lookup(-1, storeName +":/scooby/willow"); 
            NodeRef destWillowNodeRef = getDestNode(storeName, "/scooby/willow");
            Map<QName, Serializable> willowProps = nodeService.getProperties(destWillowNodeRef);
        
            assertTrue("Subject is null", willowProps.containsKey(ContentModel.PROP_SUBJECT)); 
            assertEquals("Subject content is not correct", "update" + fileSubject, willowProps.get(ContentModel.PROP_SUBJECT));
            
            // check guid of willow file
            assertEquals("Willow guids mismatch", srcWillowFile.getGuid(), willowProps.get(WCMAppModel.PROP_GUID));
            AVMNodeDescriptor srcScoobyDir = avmService.lookup(-1, storeName  +":/scooby");
            NodeRef destScoobyNodeRef = getDestNode(storeName, "/scooby");
            
            // check directory
            Map<QName, Serializable> scoobyProps = nodeService.getProperties(destScoobyNodeRef);
            assertTrue("Subject is missing", scoobyProps.containsKey(ContentModel.PROP_SUBJECT)); 
            assertEquals("Subject content is not correct", "update" + dirSubject, scoobyProps.get(ContentModel.PROP_SUBJECT));
            
            // check guid of scooby dir
              assertEquals("scooby guids mismatch", srcScoobyDir.getGuid(), scoobyProps.get(WCMAppModel.PROP_GUID));
        }
           
           /**
            * Now delete the properties
            */
           avmService.deleteNodeProperty(storeName + ":/scooby", ContentModel.PROP_SUBJECT);
           avmService.deleteNodeProperty(storeName + ":/scooby/willow", ContentModel.PROP_SUBJECT);
           service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
        {
            NodeRef destWillowNodeRef = getDestNode(storeName, "/scooby/willow");
            Map<QName, Serializable> willowProps = nodeService.getProperties(destWillowNodeRef);
            assertTrue("Subject still present willowProps", !willowProps.containsKey(ContentModel.PROP_SUBJECT)); 
            
            NodeRef destScoobyNodeRef = getDestNode(storeName, "/scooby");
            Map<QName, Serializable> scoobyProps = nodeService.getProperties(destScoobyNodeRef);
            assertTrue("Subject still present", !scoobyProps.containsKey(ContentModel.PROP_SUBJECT));           
        } 
    }
    
    /**
     * Test the lifecycle of an aspect (Create, Update, Delete)
     * @throws Exception
     */
    public void testCRUDAspect() throws Exception
    {
        String storeName = GUID.generate();
        avmService.createStore(storeName);
        
        NameMatcher matcher = (NameMatcher)fContext.getBean("globalPathExcluder");
        DeploymentReport report = new DeploymentReport();
        List<DeploymentCallback> callbacks = new ArrayList<DeploymentCallback>();
        callbacks.add(new DeploymentReportCallback(report));
        
        /**
         *  set up our test tree
         */
        avmService.createDirectory(storeName + ":/", "scooby");
        avmService.createFile(storeName + ":/scooby", "willow").close();
        
        avmService.setNodeProperty(storeName + ":/scooby/willow", ContentModel.PROP_TITLE, new PropertyValue(null, "title"));
        avmService.setNodeProperty(storeName + ":/scooby/willow", ContentModel.PROP_DESCRIPTION, new PropertyValue(null, "description"));
        service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
        
        /**
         * Now add an aspect
         */
        avmService.addAspect(storeName + ":/scooby/willow", ContentModel.ASPECT_TITLED);
          service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
        {

            AVMNodeDescriptor srcWillowFile = avmService.lookup(-1, storeName +":/scooby/willow"); 
            NodeRef destWillowNodeRef = getDestNode(storeName, "/scooby/willow");
            assertNotNull("deployed file is missing", destWillowNodeRef);  
             Set<QName> willowAspects = nodeService.getAspects(destWillowNodeRef);
            assertEquals("Willow guids mismatch", srcWillowFile.getGuid(), getDestGuid(destWillowNodeRef));
            assertTrue("Titled aspect is missing", willowAspects.contains(ContentModel.ASPECT_TITLED)); 
        }
           
           /**
            * Now delete an aspect
            */
        avmService.removeAspect(storeName + ":/scooby/willow", ContentModel.ASPECT_TITLED);

           service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, matcher, false, false, false, callbacks);
        {
            NodeRef destWillowNodeRef = getDestNode(storeName, "/scooby/willow");
            Set<QName> willowAspects = nodeService.getAspects( destWillowNodeRef);
              assertTrue("Titled aspect is still present", !willowAspects.contains(ContentModel.ASPECT_TITLED)); 
        } 
        
    }
    
    /**
     * Test for ETWOTWO-507
     * 1. In a web project, create files called test01.html and test03.html.
     * 3. Deploy using the FSR.
     * 5. Add a new file called test02.html.
     * 6. Delete the file called test03.html.
     * 8. Deploy using the ASR. 
     */
    public void testEtwoTwo507() throws Exception
    {
        DeploymentReport report = new DeploymentReport();
        List<DeploymentCallback> callbacks = new ArrayList<DeploymentCallback>();
        callbacks.add(new DeploymentReportCallback(report));
        
        String storeName = GUID.generate();
        
        avmService.createStore(storeName);
        
        avmService.createDirectory(storeName + ":/", "a");
        avmService.createFile(storeName +":/a", "test01.html").close();
        avmService.createFile(storeName +":/a", "test03.html").close();
        service.deployDifferenceFS(-1, storeName +":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, null, false, false, false, callbacks);
        
        report = new DeploymentReport();
        callbacks = new ArrayList<DeploymentCallback>();
        callbacks.add(new DeploymentReportCallback(report));
        avmService.createFile(storeName +":/a", "test02.html").close();
        avmService.removeNode(storeName +":/a", "test03.html");

        service.deployDifferenceFS(-1, storeName +":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, null, false, false, false, callbacks);
        Set<DeploymentEvent> firstDeployment = new HashSet<DeploymentEvent>();
        firstDeployment.addAll(report.getEvents());
        
        assertTrue("Update missing: /a/test02.html", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.CREATED, null, "/a/test02.html")));
        assertTrue("delete missing: /a/test03.html", firstDeployment.contains(new DeploymentEvent(DeploymentEvent.Type.DELETED, null, "/a/test03.html")));
    }
 
    /**
     * Wrong password
     * Negative test
     */
    public void testWrongPassword() 
    {
        String storeName = GUID.generate();
        
        avmService.createStore(storeName);

            try {
                service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, "Wrong!", TEST_TARGET, null, false, false, false, null);
                fail("Wrong password should throw exception");
            } 
            catch (AVMException de)
            {
                // pass
                de.printStackTrace();
            }
            avmService.purgeStore(storeName);
    }

  
    /**
     *  Deploy a website, update it, then revert to the first version 
     */
    public void testRevertToPreviousVersion() throws Exception
    {
        DeploymentReport report = new DeploymentReport();
        List<DeploymentCallback> callbacks = new ArrayList<DeploymentCallback>();
        callbacks.add(new DeploymentReportCallback(report));
        
        String storeName = GUID.generate();
        
        avmService.createStore(storeName);
        
        report = new DeploymentReport();
        callbacks = new ArrayList<DeploymentCallback>();
        callbacks.add(new DeploymentReportCallback(report));
        
        avmService.createDirectory(storeName + ":/", "a");
        avmService.createDirectory(storeName + ":/a", "b");
        avmService.createFile(storeName + ":/a/b", "Zander").close();
        avmService.createFile(storeName + ":/a/b", "Cordelia").close();
        avmService.createFile(storeName + ":/a/b", "Buffy").close();
        service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, null, false, false, false, callbacks);
        int version = report.getEvents().get(0).getSource().getFirst();
        assertTrue("version is not set", version > 0);
        
        // Now do some updates
        report = new DeploymentReport();
        callbacks = new ArrayList<DeploymentCallback>();
        callbacks.add(new DeploymentReportCallback(report));
        avmService.createFile(storeName + ":/a/b", "Master").close();
        avmService.createFile(storeName + ":/a/b", "Drusilla").close();
        avmService.removeNode(storeName + ":/a/b", "Zander");
           service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, null, false, false, false, callbacks);
        
        // now do the restore to previous version
        report = new DeploymentReport();
        callbacks = new ArrayList<DeploymentCallback>();
        callbacks.add(new DeploymentReportCallback(report));
        service.deployDifferenceFS(version, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, null, false, false, false, callbacks);
        Set<DeploymentEvent> smallUpdate = new HashSet<DeploymentEvent>();
        smallUpdate.addAll(report.getEvents());       
        for (DeploymentEvent event : report)
        {
            System.out.println(event);
        }
        assertTrue("Update missing", smallUpdate.contains(new DeploymentEvent(DeploymentEvent.Type.CREATED, null, "/a/b/Zander")));
        assertTrue("Update missing", smallUpdate.contains(new DeploymentEvent(DeploymentEvent.Type.DELETED, null, "/a/b/Drusilla")));
        assertTrue("Update missing", smallUpdate.contains(new DeploymentEvent(DeploymentEvent.Type.DELETED, null, "/a/b/Master")));
        assertEquals(5, smallUpdate.size());        
    }
    
    /**
     *  Now load a large number of files.
     *  Do a deployment - should load successfully
     *  
     *  Remove a node and update a file
     *  Do a deployment - should only see start and end events and the two above. 
     */
    public void testBulkLoad() throws Exception
    {
        DeploymentReport report = new DeploymentReport();
        List<DeploymentCallback> callbacks = new ArrayList<DeploymentCallback>();
        callbacks.add(new DeploymentReportCallback(report));
        
        String storeName = GUID.generate();
        
        avmService.createStore(storeName);
        
        BulkLoader loader = new BulkLoader();
        loader.setAvmService(avmService);
        loader.recursiveLoad("../repository/source/java/org/alfresco/repo/avm", storeName + ":/");
        report = new DeploymentReport();
        callbacks = new ArrayList<DeploymentCallback>();
        callbacks.add(new DeploymentReportCallback(report));
        service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT, TEST_USER, TEST_PASSWORD, TEST_TARGET, null, false, false, false, callbacks);
        Set<DeploymentEvent> bigUpdate = new HashSet<DeploymentEvent>();
        bigUpdate.addAll(report.getEvents());
        assertTrue("big update no start", bigUpdate.contains(new DeploymentEvent(DeploymentEvent.Type.START, null, TEST_TARGET)));
        assertTrue("big update no finish", bigUpdate.contains(new DeploymentEvent(DeploymentEvent.Type.END, null, TEST_TARGET)));
        assertTrue("big update too small", bigUpdate.size() > 100);
    
        /**
         * Now do a smaller update and check that just a few files update
         */
        avmService.removeNode(storeName + ":/avm/ibatis");
        avmService.getFileOutputStream(storeName + ":/avm/AVMServiceTest.java").close();
        report = new DeploymentReport();
        callbacks = new ArrayList<DeploymentCallback>();
        callbacks.add(new DeploymentReportCallback(report));
        service.deployDifferenceFS(-1, storeName + ":/", "default", "localhost", TEST_PORT,  TEST_USER, TEST_PASSWORD, TEST_TARGET, null, false, false, false, callbacks);
        
        Set<DeploymentEvent> smallUpdate = new HashSet<DeploymentEvent>();
        smallUpdate.addAll(report.getEvents());
        for (DeploymentEvent event : report)
        {
            System.out.println(event);
        }
        assertEquals(4, smallUpdate.size());
        
        assertTrue("Start missing", smallUpdate.contains(new DeploymentEvent(DeploymentEvent.Type.START, null, TEST_TARGET)));
        assertTrue("End missing", smallUpdate.contains(new DeploymentEvent(DeploymentEvent.Type.DELETED, null, "/avm/ibatis")));
        assertTrue("Update missing", smallUpdate.contains(new DeploymentEvent(DeploymentEvent.Type.UPDATED, null, "/avm/AVMServiceTest.java")));
        assertTrue("Delete Missing", smallUpdate.contains(new DeploymentEvent(DeploymentEvent.Type.END, null, TEST_TARGET)));
    }
    
    private NodeRef getDestNode(String storeName, String path)
    {
        String expr = "/" + ISO9075.encode(storeName) + path ;
        String query =  rootLocator.getRootQuery(storeName) + expr.replace("/", "/cm:");
        ResultSet result = searchService.query(new StoreRef(storeRef), SearchService.LANGUAGE_XPATH, query);
        
        if(result.length() > 1)
        {
            throw new DeploymentException("root path not found or not unique:" + query);
        }
        
        if(result.length() == 1)
        {
            return result.getNodeRef(0);
        }
        
        return null;  
    }
    
    private String getDestGuid(NodeRef nodeRef)
    {
        return (String)nodeService.getProperty(nodeRef, WCMAppModel.PROP_GUID);
    }
    
}
