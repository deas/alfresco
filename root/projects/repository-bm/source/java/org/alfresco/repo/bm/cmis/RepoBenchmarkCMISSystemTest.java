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
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
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
 * @author jan
 *
 */
public class RepoBenchmarkCMISSystemTest extends AbstractRepoBenchmarkSystemTest
{
    private static Log logger = LogFactory.getLog(RepoBenchmarkCMISSystemTest.class);
    
    private Session session;
    
    private CmisObject objectToDelete; // for tearDown
    
    
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
            
            // create session (one per test thread) and warm up a bit
            SessionFactory factory = SessionFactoryImpl.newInstance();
            
            Map<String, String> opencmisMap = new HashMap<String, String>();
            
            if (logger.isDebugEnabled())
            {
                logger.debug("CMISTest: testUserUN = "+testUserUN);
            }
            
            // TODO - improve
            opencmisMap.put("org.apache.chemistry.opencmis.user", "admin");
            opencmisMap.put("org.apache.chemistry.opencmis.password", testUserPW);
            opencmisMap.put("org.apache.chemistry.opencmis.binding.spi.type", "atompub"); // TODO make it configurable
            opencmisMap.put("org.apache.chemistry.opencmis.binding.atompub.url", testBaseUrl);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("CMISTest: opencmisMap = "+opencmisMap);
            }
            
            session = factory.getRepositories(opencmisMap).get(0).createSession();
            
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
        super.setUp();
        
        if (session == null)
        {
            throw new Exception("Session not initialised !");
        }
        
        objectToDelete = null;
    }
    
    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        if (objectToDelete != null)
        {
            objectToDelete.delete(true);
        }
    }
    
    /**
     * GetChildren
     */
    public void testGetChildren() throws Exception
    {
        // for local sanity check
        if (testPath == null)
        {
            testPath = "/S-35d-600f-1Kb/folder-00000/folder-00000/folder-00000";
        }
        
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String path = getPath();
            
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
     * ItemRead
     */
    public void testItemRead() throws Exception
    {
        // for local sanity check
        if (testPath == null)
        {
            testPath = "/M-35d-600f-200Kb/folder-00000/folder-00000/folder-00000/file-00010.txt";
        }
        
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String path = getPath();
            
            Document document = (Document) session.getObjectByPath(path);
            
            if (logger.isDebugEnabled())
            {
                logger.debug(document.getName());
            }
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testItemRead: "+path+" (found in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (Exception e)
        {
            print(e);
            throw e;
        }
    }
    
    /**
     * ContentUpdate
     */
    public void testContentUpdate() throws Exception
    {
        // for local sanity check
        if (testPath == null)
        {
            testPath = "/S-7d-30000f-1Kb/folder-00000/folder-00000/folder-00000/file-00000.txt";
        }
        
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String path = getPath();
            
            Document doc = (Document)session.getObjectByPath(path);
            
            if (testLen <= 0)
            {
                testLen = doc.getContentStreamLength();
            }
            
            if (testLen > 0)
            {
                ContentStream content = new ContentStreamImpl(doc.getName(), BigInteger.valueOf(testLen), "plain/text", getPhantomStream(testLen));
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
    
    /**
     * DocumentCreate
     */
    public void testDocumentCreate() throws Exception
    {
        // for local sanity check
        if (testPath == null)
        {
            testPath = "/S-35d-600f-1Kb/folder-00000/folder-00000/folder-00000";
        }
        
        if (testLen <= 0)
        {
            testLen = 1024;
        }
        
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String type = "cmis:document";
            
            String path = getPath();
            
            Folder folder = (Folder)session.getObjectByPath(path);
            
            String name = "document-" + System.currentTimeMillis() + "-" + this.hashCode();
            
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(PropertyIds.NAME, name);
            properties.put(PropertyIds.OBJECT_TYPE_ID, type);
            
            ContentStream content = new ContentStreamImpl(name, BigInteger.valueOf(testLen), "plain/text", getPhantomStream(testLen));
            
            CmisObject object = folder.createDocument(properties, content, null);
            objectToDelete = object;
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testDocumentCreate: "+path+" (created in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (Exception e)
        {
            print(e);
            throw e;
        }
    }
    
    /**
     * ContentRead
     */
    public void testContentRead() throws Exception
    {
        // for local sanity check
        if (testPath == null)
        {
            testPath = "/S-7d-30000f-1Kb/folder-00000/folder-00000/folder-00000/file-00000.txt";
        }
        
        if (testLen <= 0)
        {
            testLen = 1024;
        }
        
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String path = getPath();
            
            Document doc = (Document)session.getObjectByPath(path);
            
            InputStream is = doc.getContentStream().getStream();
            
            byte[] buffer = new byte[1024];
            while (true)
            {
                int count = is.read(buffer);
                if (count < 0)
                {
                    break;
                }
            }
            
            is.close();
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testContentRead: "+path+" (read in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (SardineException se)
        {
            print(se);
            throw se;
        }
    }
    
    @Override
    protected String getPath()
    {
        StringBuilder sb = new StringBuilder();
        
        // TODO remove hardcoded "/testdata"
        
        sb.append("/testdata").
           append((testThreadFolder.startsWith("/") ? "" : "/")).append(testThreadFolder).
           append(testPath.startsWith("/") ? "" : "/").append(testPath);
        
        if (logger.isTraceEnabled())
        {
            logger.trace("getPath: "+sb.toString());
        }
        
        return sb.toString();
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
}