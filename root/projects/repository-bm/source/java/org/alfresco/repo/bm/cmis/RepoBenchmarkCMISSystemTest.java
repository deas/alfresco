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
package org.alfresco.repo.bm.cmis;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.bm.AbstractRepoBenchmarkSystemTest;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.sardine.util.SardineException;

/**
 * Repo BenchMark - CMIS
 * 
 * - access Alfresco Repository remotely using Apache Chemistry OpenCMIS client
 * - tests can be run using Apache JMeter (JUnit Request Sampler)
 * 
 * @author janv
 *
 */
public class RepoBenchmarkCMISSystemTest extends AbstractRepoBenchmarkSystemTest
{
    private static Log logger = LogFactory.getLog(RepoBenchmarkCMISSystemTest.class);
    
    private Session session;
    
    
    public RepoBenchmarkCMISSystemTest(String csvProps) throws Exception
    {
        super(csvProps);
        
        try
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("CMISTest: "+csvProps);
            }
            
            // for local sanity check
            if (testBaseUrl == null)
            {
                testBaseUrl = "http://localhost:8080/alfresco/cmisatom";
            }
            
            // OpenCMIS parameter map
            Map<String, String> parameters = new HashMap<String, String>();
            
            if (logger.isDebugEnabled())
            {
                logger.debug("CMISTest: testUserUN = "+testUserUN);
            }
            
            // TODO - improve
            parameters.put("org.apache.chemistry.opencmis.user", testUserUN);
            parameters.put("org.apache.chemistry.opencmis.password", testUserPW);
            parameters.put("org.apache.chemistry.opencmis.binding.spi.type", "atompub"); // TODO make it configurable
            parameters.put("org.apache.chemistry.opencmis.binding.atompub.url", testBaseUrl);
            
            // set the object factory - note: required for Alfresco OpenCMIS Extension (see testItemUpdate)
            parameters.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
            
            if (logger.isDebugEnabled())
            {
                logger.debug("CMISTest: parameters = "+parameters);
            }
            
            // create session (one per test thread) and warm up a bit
            SessionFactory factory = SessionFactoryImpl.newInstance();
            
            session = factory.getRepositories(parameters).get(0).createSession();
            
            /*
            if (properties.containsKey(SessionParameter.REPOSITORY_ID))
            {
                session = factory.createSession(properties);
            } else
            {
                session = factory.getRepositories(properties).get(0).createSession();
            }
            */
            
            session.getDefaultContext().setCacheEnabled(false);
            session.getTypeChildren(null, true);
            session.getRootFolder().getChildren().iterator().next();
        } 
        catch (Exception e)
        {
            print(e);
        }
    }
    
    @Override
    public void setUp() throws Exception
    {
        if (session == null)
        {
            throw new Exception("Session not initialised !");
        }
        
        super.setUp();
    }
    
    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    public void testSetup() throws Exception
    {
        // test setup
    }
    
    /**
     * GetChildren
     */
    public void testGetChildren() throws Exception
    {
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String path = getTestPath();
            
            Folder folder = (Folder) session.getObjectByPath(path);
            
            ItemIterable<CmisObject> children = folder.getChildren();
            
            long cnt = 0;
            
            for (CmisObject item : children)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug(item.getName());
                }
                
                cnt++;
            }
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testGetChildren: "+path+" (found "+cnt+" in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (Exception e)
        {
            print(e);
            throw e;
        }
    }
    
    /**
     * ItemRead - get properties of document or folder
     */
    public void testItemRead() throws Exception
    {
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String path = getTestPath();
            
            Document document = (Document)getItem(path);
            
            if (document == null)
            {
                throw new Exception("testItemRead: not found "+path);
            }
            
            int propCnt = document.getProperties().size();
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testItemRead: "+path+" has "+propCnt+" props (found in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (Exception e)
        {
            print(e);
            throw e;
        }
    }
    
    /**
     * ItemRename - rename document or folder (ie. move within same parent folder)
     */
    public void testItemRename() throws Exception
    {
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String path = getTestPath();
            
            CmisObject object = session.getObjectByPath(path);
            
            String oldName = object.getName();
            String newName = oldName + "-new.txt";
            
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(PropertyIds.NAME, newName);
            
            // rename
            object.updateProperties(properties);
            
            String[] parts = splitBase(path);
            String newPath = extendPath(parts[0], newName);
            
            testPathCurrent = newPath; // for teardown
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testItemRename: "+newPath+" (renamed in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (Exception se)
        {
            print(se);
            throw se;
        }
    }
    
    /**
     * ItemUpdate - update properties of document (or folder)
     */
    public void testItemUpdate() throws Exception
    {
        try
        {
            long start = System.currentTimeMillis();
            
            String path = getTestPath();
            
            CmisObject object = session.getObjectByPath(path);
            
            // note: aspect props require Alfresco OpenCMIS Extension (pending proposed 2nd'ary type support in CMIS 1.1)
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("cm:title", "my title - "+start);
            properties.put("cm:description", "my description - "+start);
            
            // update props 
            object.updateProperties(properties);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testItemUpdate: "+path+" (updated in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (Exception se)
        {
            print(se);
            throw se;
        }
    }
    
    /**
     * ContentUpdate
     */
    public void testContentUpdate() throws Exception
    {
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String path = getTestPath();
            
            Document doc = (Document)session.getObjectByPath(path);
            
            long len = testLen;
            
            if (len <= 0)
            {
                len = doc.getContentStreamLength();
            }
            
            if (len > 0)
            {
                ContentStream content = new ContentStreamImpl(doc.getName(), BigInteger.valueOf(len), "plain/text", getPhantomStream(len));
                doc.setContentStream(content, true);
            }
            else
            {
                throw new Exception("testContentUpdate: not updated (no len specified and empty file) "+path);
            }
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testContentUpdate: "+path+" (updated in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (Exception e)
        {
            print(e);
            throw e;
        }
    }
    
    
    @Override
    protected void deleteItem(String path) throws SardineException
    {
        CmisObject cmisObj = getItem(path);
        if (cmisObj != null)
        {
            cmisObj.delete(true);
        }
    }
    
    private CmisObject getItem(String path) throws CmisObjectNotFoundException
    {
        return session.getObjectByPath(path);
    }
    
    
    @Override
    protected boolean existsItem(String path)
    {
        try
        {
            getItem(path);
            return true;
        }
        catch (CmisObjectNotFoundException e)
        {
            // ignore - does not exist
        }
        return false;
    }
    
    @Override
    protected InputStream getContent(String documentPath) throws Exception
    {
        Document doc = (Document)session.getObjectByPath(documentPath);
        return doc.getContentStream().getStream();
    }
    
    @Override
    protected void print(Exception e)
    {
        super.print(e);
        
        if (e instanceof CmisBaseException)
        {
            System.err.println(((CmisBaseException) e).getErrorContent());
        }
    }
    
    @Override
    protected String createDocument(String parentFolderPath, String name, InputStream is, long len)
    {
        String type = "cmis:document";
        String mimeType = "plain/text";
        
        Folder folder = (Folder)session.getObjectByPath(parentFolderPath);
        
        if (name == null)
        {
            name = "document-" + System.currentTimeMillis() + "-" + random.nextInt(1000);
        }
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, name);
        properties.put(PropertyIds.OBJECT_TYPE_ID, type);
        
        ContentStream content = new ContentStreamImpl(name, BigInteger.valueOf(len), mimeType, is);
        
        folder.createDocument(properties, content, null); // ignore result
        
        return extendPath(parentFolderPath, name);
    }
    
    @Override
    protected String createFolder(String parentFolderPath, String name)
    {
        if (name == null)
        {
            name = "folder-" + System.currentTimeMillis() + "-" + random.nextInt(1000);
        }
        
        String type = "cmis:folder";
        
        Folder folder = (Folder)session.getObjectByPath(parentFolderPath);
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, name);
        properties.put(PropertyIds.OBJECT_TYPE_ID, type);
        
        folder.createFolder(properties); // ignore result
        
        return extendPath(parentFolderPath, name);
    }
}