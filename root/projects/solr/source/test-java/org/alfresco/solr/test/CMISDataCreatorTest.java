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
package org.alfresco.solr.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.util.GUID;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.BasicPermissions;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlEntryImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlPrincipalDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.tools.ant.filters.StringInputStream;

/**
 * @author Andy
 *
 */
public class CMISDataCreatorTest extends TestCase
{
    private Session getSession()
    {
 
        
        String user ="admin";
        String pwd = "admin";
        String url = "http://localhost:8080/alfresco/cmisatom";
        
        SessionFactory factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(SessionParameter.USER, user);
        parameter.put(SessionParameter.PASSWORD, pwd);
        parameter.put(SessionParameter.ATOMPUB_URL, url);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        
        List<Repository> repositories = factory.getRepositories(parameter);
        Session session = repositories.get(0).createSession();
        
        return session;
    }
    
    private String getRootFolderName()
    {
        return  "CMIS-Data-Creator-Test-"+System.currentTimeMillis();
    }
    
    private String getUniqueName()
    {
        return  "CMIS-Data-Creator-Test-"+GUID.generate();
    }
    
    public void testCreate()
    {
        Session session = getSession();
        
        String folderName = getRootFolderName();
        Folder root = session.getRootFolder();
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, folderName);

        // create the folder
        Folder newFolder = root.createFolder(properties);
        
        for(int i = 0; i < 50; i++)
        {
            AccessControlPrincipalDataImpl principal = new AccessControlPrincipalDataImpl("user"+i);
            List<String> permissions = new ArrayList<String>(1);
            permissions.add(BasicPermissions.READ);
            List<Ace> addAces = new ArrayList<Ace>(1);
            addAces.add(new AccessControlEntryImpl(principal, permissions));
            newFolder.addAcl(addAces, AclPropagation.PROPAGATE);
            
            Map<String, Object> updateProperties = new HashMap<String, Object>();
            updateProperties.put("cm:title", "Update title "+i);
            newFolder.updateProperties(properties);
            
            if(i % 10 == 0)
            {
                System.out.println("@ "+i);
            }
        }
        ItemIterable<QueryResult> result = session.query("select * from cmis:folder", false);
        assertTrue(result.getTotalNumItems() > 0);
        
        result = session.query("select * from cmis:folder where cmis:name = '"+folderName+"'", false);
        assertTrue(result.getTotalNumItems() > 0);
        
    }
    
    public void testQueryFolderProperties()
    {
        Session session = getSession();
        
        String folderName = getRootFolderName();
        Folder root = session.getRootFolder();
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, folderName);

        // create the folder
        Folder newFolder = root.createFolder(properties);
        
        ItemIterable<QueryResult>  result = session.query("select * from cmis:folder where cmis:name = '"+folderName+"'", false);
        assertEquals(1, result.getTotalNumItems());
        
        
        String uniqueName = getUniqueName();
        Map<String, Object> uProperties = new HashMap<String, Object>();
        uProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        uProperties.put(PropertyIds.NAME, uniqueName);
        Folder uniqueFolder = newFolder.createFolder(uProperties);
        
        result = session.query("select * from cmis:folder where cmis:name = '"+uniqueName+"'", false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:folder where cmis:name = '"+uniqueName+"' AND IN_FOLDER('"+ uniqueFolder.getParentId() + "')" , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:folder where IN_FOLDER('"+ uniqueFolder.getParentId() + "')" , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:folder where cmis:name = '"+uniqueName+"' AND cmis:createdBy = '"+ uniqueFolder.getCreatedBy()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:folder where cmis:name = '"+uniqueName+"' AND cmis:objectId = '"+ uniqueFolder.getId()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:folder where cmis:name = '"+uniqueName+"' AND cmis:lastModifiedBy = '"+ uniqueFolder.getLastModifiedBy()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:folder where cmis:name = '"+ uniqueFolder.getName()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        String creationDate = ISO8601DateFormat.format(uniqueFolder.getCreationDate().getTime());
        result = session.query("select * from cmis:folder where cmis:name = '"+uniqueName+"' AND cmis:creationDate = '"+ creationDate +"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        String modificationDate = ISO8601DateFormat.format(uniqueFolder.getLastModificationDate().getTime());
        result = session.query("select * from cmis:folder where cmis:name = '"+uniqueName+"' AND cmis:lastModificationDate = '"+ modificationDate+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:folder where cmis:name = '"+uniqueName+"' AND cmis:objectTypeId = '"+ uniqueFolder.getType().getQueryName()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:folder where cmis:name = '"+uniqueName+"' AND cmis:baseTypeId = '"+ uniqueFolder.getBaseType().getQueryName()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
    }
    
    public void testQueryDocumentProperties()
    {
        Session session = getSession();
        
        String folderName = getRootFolderName();
        Folder root = session.getRootFolder();
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, folderName);

        // create the folder
        Folder newFolder = root.createFolder(properties);
        
        ItemIterable<QueryResult>  result = session.query("select * from cmis:folder where cmis:name = '"+folderName+"'", false);
        assertEquals(1, result.getTotalNumItems());
        
        
        String uniqueName = getUniqueName();
        Map<String, Object> uProperties = new HashMap<String, Object>();
        uProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        uProperties.put(PropertyIds.NAME, uniqueName);
        ContentStreamImpl contentStream = new ContentStreamImpl();
        contentStream.setFileName("bob");
        contentStream.setStream(new StringInputStream("short"));
        contentStream.setLength(new BigInteger("5"));
        contentStream.setMimeType("text/plain");
        
        Document uniqueDocument = newFolder.createDocument(uProperties, contentStream, VersioningState.MAJOR);
        
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"'", false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"' AND IN_FOLDER('"+ newFolder.getId() + "')" , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:document where IN_FOLDER('"+ newFolder.getId() + "')" , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"' AND cmis:createdBy = '"+ uniqueDocument.getCreatedBy()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"' AND cmis:objectId = '"+ uniqueDocument.getId()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"' AND cmis:lastModifiedBy = '"+ uniqueDocument.getLastModifiedBy()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:document where cmis:name = '"+ uniqueDocument.getName()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        String creationDate = ISO8601DateFormat.format(uniqueDocument.getCreationDate().getTime());
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"' AND cmis:creationDate = '"+ creationDate +"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        String modificationDate = ISO8601DateFormat.format(uniqueDocument.getLastModificationDate().getTime());
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"' AND cmis:lastModificationDate = '"+ modificationDate+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"' AND cmis:objectTypeId = '"+ uniqueDocument.getType().getQueryName()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"' AND cmis:baseTypeId = '"+ uniqueDocument.getBaseType().getQueryName()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"' AND cmis:contentStreamFileName = '"+ uniqueDocument.getContentStreamFileName()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"' AND cmis:contentStreamLength = '"+ uniqueDocument.getContentStreamLength()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
        
        result = session.query("select * from cmis:document where cmis:name = '"+uniqueName+"' AND cmis:contentStreamMimeType = '"+ uniqueDocument.getContentStreamMimeType()+"'"  , false);
        assertEquals(1, result.getTotalNumItems());
    }
    
}
