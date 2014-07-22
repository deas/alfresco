/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts.documents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.extensions.webscripts.AbstractRuntimeContainer;
import org.springframework.extensions.webscripts.AbstractWebScriptServerTest;
import org.springframework.extensions.webscripts.ArgumentTypeDescription;
import org.springframework.extensions.webscripts.ClassPathStore;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.PackageDescriptionDocument;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.extensions.webscripts.ResourceDescription;
import org.springframework.extensions.webscripts.SchemaDescriptionDocument;
import org.springframework.extensions.webscripts.TypeDescription;
import org.springframework.extensions.webscripts.WebScript;

/**
 * @author drq
 *
 */
public class WebscriptDocumentTest extends AbstractWebScriptServerTest 
{

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.AbstractWebScriptServerTest#setUp()
     */
    public void setUp() throws ServletException
    {
        super.setUp();
        // manually init our classpath store
        getClassPathStore().init();
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.AbstractWebScriptServerTest#getConfigLocations()
     */
    public ArrayList<String> getConfigLocations()
    {
        ArrayList<String> list = super.getConfigLocations();

        list.add("classpath:org/springframework/extensions/webscripts/stores/spring-webscripts-stores-context.xml");

        return list;
    }

    /**
     * @return
     */
    public ClassPathStore getClassPathStore()
    {
        return (ClassPathStore) getTestServer().getApplicationContext().getBean("webscripts.store.test");
    }

    /**
     * @return
     */
    public AbstractRuntimeContainer getWebscriptContainer() {
        return (AbstractRuntimeContainer) (getTestServer().getApplicationContext().getBean("webscripts.container"));
    }

    /**
     * @return
     */
    public Registry getWebscriptRegistry() {
        return getWebscriptContainer().getRegistry();
    }

    /**
     * @throws Exception
     */
    @Test
    public void testPackageDescriptionDocumentCount() throws Exception
    {
        Collection<PackageDescriptionDocument> docs = getWebscriptRegistry().getPackageDescriptionDocuments();
        int numberOfPackageDescipiontDocs = docs.size();
        assertEquals(1, numberOfPackageDescipiontDocs);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testGetPackageDescriptionDocumentByPath() throws Exception
    {
        PackageDescriptionDocument doc = getWebscriptRegistry().getPackageDescriptionDocument("/documents/samples");		
        assertNotNull(doc);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testPackageDescriptionDocumentNameDescription() throws Exception
    {
        PackageDescriptionDocument doc = getWebscriptRegistry().getPackageDescriptionDocument("/documents/samples");		
        assertNotNull(doc);
        String shortName = doc.getShortName();
        assertEquals("Alfresco samples",shortName);
        String description = doc.getDescription();
        assertEquals("Alfresco sample webscripts",description);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testPackageDescriptionDocumentResources() throws Exception
    {
        PackageDescriptionDocument doc = getWebscriptRegistry().getPackageDescriptionDocument("/documents/samples");		
        assertNotNull(doc);
        ResourceDescription [] resources = doc.getResourceDescriptions();
        assertNotNull(resources);
        assertEquals(2,resources.length);
        String shortName = resources[0].getShortName();
        assertEquals("Alfresco sample1",shortName);
        String description = resources[0].getDescription();
        assertEquals("Alfresco sample1 webscript",description);
        String[] scriptIds = resources[0].getScriptIds();
        assertEquals(4,scriptIds.length);
        assertEquals("documents/samples/sample1.get",scriptIds[0]);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSchemaDescriptionDocumentCount()  throws Exception
    {
        Collection<SchemaDescriptionDocument> docs = getWebscriptRegistry().getSchemaDescriptionDocuments();
        int numOfDocs = docs.size();
        assertEquals(1,numOfDocs);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testGetSchemaDescriptionDocumentById()  throws Exception
    {
        SchemaDescriptionDocument doc = getWebscriptRegistry().getSchemaDescriptionDocument("samples.sample1");
        assertNotNull(doc);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSchemaDescriptionDocumentNameDescription() throws Exception
    {
        SchemaDescriptionDocument doc = getWebscriptRegistry().getSchemaDescriptionDocument("samples.sample1");
        assertNotNull(doc);
        String shortName = doc.getShortName();
        assertEquals("Schema for sample1",shortName);
        String description = doc.getDescription();
        assertEquals("Schema for sample1 in package samples",description);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSchemaDescriptionDocumentTypes() throws Exception
    {
        SchemaDescriptionDocument doc = getWebscriptRegistry().getSchemaDescriptionDocument("samples.sample1");
        assertNotNull(doc);
        TypeDescription[] types = doc.getTypeDescriptions();
        assertNotNull(types);
        assertEquals(2,types.length);
        TypeDescription type = types[0];
        assertEquals("samples.sample1.status",type.getId());
        assertEquals("json",type.getFormat());
        assertEquals("status",type.getShortName());
        assertEquals("Sample Status",type.getDescription());
        assertNotNull(type.getDefinition());
        assertTrue(type.getDefinition().trim().length()>0);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testWebscriptDescriptionDocument() throws Exception
    {
        WebScript ws = getWebscriptRegistry().getWebScript("documents/samples/sample1.get");
        assertNotNull(ws);
        Description wsDesc = ws.getDescription();
        assertNotNull(wsDesc);
        ArgumentTypeDescription [] args = wsDesc.getArguments();
        assertNotNull(args);
        assertEquals(3,args.length);
        ArgumentTypeDescription arg = args[0];
        assertEquals("arg1",arg.getShortName());
        assertEquals("argument 1",arg.getDescription());
        assertEquals("arg1",arg.getDefaultValue());
        assertFalse(arg.isRequired());
        TypeDescription [] requestTypes = wsDesc.getRequestTypes();
        assertNotNull(requestTypes);
        assertEquals(1,requestTypes.length);
        TypeDescription requestType = requestTypes[0];
        assertEquals("json",requestType.format);
        assertNotNull(requestType.getDefinition());
        assertTrue(requestType.getDefinition().trim().length()>0);
        TypeDescription [] responseTypes = wsDesc.getResponseTypes();
        assertNotNull(responseTypes);
        assertEquals(2,responseTypes.length);
        TypeDescription responseType = responseTypes[0];
        assertNotNull(responseType);
        assertEquals("json",responseType.getFormat());
        assertEquals("samples.sample1.status",responseType.getId());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testGetTypeDescriptionById() throws Exception
    {
        TypeDescription type = getWebscriptRegistry().getSchemaTypeDescriptionById("samples.sample1.status");
        assertNotNull(type);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void testBadSchemaDescriptionDocuments() throws Exception
    {
        Map <String,String> schemas = getWebscriptRegistry().getFailedSchemaDescriptionsByPath();
        assertNotNull (schemas);
        assertEquals(1,schemas.size());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testBadPackageDescriptionDocuments() throws Exception
    {
        Map <String,String> packages = getWebscriptRegistry().getFailedPackageDescriptionsByPath();
        assertNotNull (packages);
        assertEquals(1,packages.size());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testBadWebscriptDescriptionDocuments() throws Exception
    {
        Map <String,String> ws = getWebscriptRegistry().getFailures();
        assertNotNull (ws);
        assertEquals(3,ws.size());
    }
}
