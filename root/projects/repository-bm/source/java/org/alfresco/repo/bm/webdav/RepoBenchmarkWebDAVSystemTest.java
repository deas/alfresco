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
package org.alfresco.repo.bm.webdav;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.bm.AbstractRepoBenchmarkSystemTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.googlecode.sardine.util.SardineException;

/**
 * Repo BenchMark - WebDAV
 * 
 * - access Alfresco Repository remotely using Sardine WebDAV client
 * - tests can be run using Apache JMeter (JUnit Request Sampler)
 * 
 * @author janv
 *
 */
public class RepoBenchmarkWebDAVSystemTest extends AbstractRepoBenchmarkSystemTest
{
    private static Log logger = LogFactory.getLog(RepoBenchmarkWebDAVSystemTest.class);
    
    public RepoBenchmarkWebDAVSystemTest(String csvProps) throws Exception
    {
        super(csvProps);
        
        try
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("WebDAVTest: "+csvProps);
            }
            
            // for local sanity check
            if (testBaseUrl == null)
            {
                testBaseUrl = "http://localhost:8080/alfresco/webdav";
            }
        }
        catch (Exception e)
        {
            print(e);
            throw e;
        }
    }
    
    @Override
    public void setUp() throws Exception
    {
        // note: setUp time not be measured by JMeter->JUnit
        super.setUp();
    }
    
    @Override
    public void tearDown() throws Exception
    {
        // note: tearDown time not be measured by JMeter->JUnit
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
            
            Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
            
            // TODO make this configurable
            List<DavResource> resources = sardine.getResources(getUrl(path), 1, false);
            //List<DavResource> resources = sardine.getResources(getUrl(path));
            
            long cnt = 0;
            
            for (DavResource res : resources)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug(res.getName());
                }
                cnt++;
            }
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testGetChildren: "+path+" (found "+cnt+" in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (SardineException se)
        {
            print(se);
            throw se;
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
            
            // TODO make this configurable
            DavResource resource = getItem(getUrl(path), 0, true);
            
            if (resource == null)
            {
                throw new Exception("testItemRead: not found "+path);
            }
            
            int propCnt = resource.getCustomProps().size();
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testItemRead: "+path+" has "+propCnt+" props (found in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (SardineException se)
        {
            print(se);
            throw se;
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
            
            String oldPath = getTestPath();
            String newPath = oldPath + "-new";
            
            // rename
            Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
            sardine.move(getUrl(oldPath), getUrl(newPath));
            
            testPathToDelete = newPath; // for teardown
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testItemRename: "+newPath+" (renamed in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (SardineException se)
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
            
            Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
            
            Map<String, String> namespaces = new HashMap<String, String>(1);
            namespaces.put("cm", "http://www.alfresco.org/model/content/1.0");
            
            Map<String, String> addProps = new HashMap<String, String>(2);
            addProps.put("cm:title", "my title - "+start);
            addProps.put("cm:description", "my description - "+start);
            
            // update props
            sardine.setCustomProps(getUrl(path), addProps, null, namespaces);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testItemUpdate: "+path+" (updated in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (SardineException se)
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
            
            // TODO make this configurable
            DavResource resource = getItem(getUrl(path), 0, false);
            
            if (resource == null)
            {
                throw new Exception("testContentUpdate: not found "+path);
            }
            
            Long len = testLen;
            
            if (len <= 0)
            {
                len = resource.getContentLength();
            }
            
            if ((len != null) && (len > 0))
            {
                Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
                sardine.put(getUrl(path), getPhantomStream(len), "text/plain");
            }
            else
            {
                throw new Exception("testContentUpdate: empty file "+path);
            }
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testContentUpdate: "+path+" (updated in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (SardineException se)
        {
            print(se);
            throw se;
        }
    }
    
    @Override
    protected String createDocument(String parentFolderPath, String name, InputStream is, long len) throws Exception
    {
        String mimeType = "text/plain";
        
        Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
        
        if (name == null)
        {
            name = "document-" + System.currentTimeMillis() + "-" + random.nextInt(1000);
        }
        
        String newDocumentPath = extendPath(parentFolderPath, name);
        
        sardine.put(getUrl(newDocumentPath), is, mimeType);
        
        return newDocumentPath;
    }
    
    @Override
    protected String createFolder(String parentFolderPath, String name) throws SardineException
    {
        if (name == null)
        {
            name = "folder-" + System.currentTimeMillis() + "-" + random.nextInt(1000);
        }
        
        String newFolderPath = extendPath(parentFolderPath, name);
        
        Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
        sardine.createDirectory(getUrl(newFolderPath));
        
        return newFolderPath;
    }
    
    @Override
    protected void deleteItem(String itemPath) throws SardineException
    {
        Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
        sardine.delete(getUrl(itemPath));
    }
    
    @Override
    protected InputStream getContent(String documentPath) throws SardineException
    {
        Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
        return sardine.getInputStream(getUrl(documentPath));
    }
    
    private DavResource getItem(String url, Integer depth, Boolean allProps) throws SardineException
    {
        Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
        
        List<DavResource> resources = null;
        
        if ((depth != null) && (allProps != null))
        {
            resources = sardine.getResources(url, 0, allProps);
        }
        else
        {
             // default
            resources = sardine.getResources(url);
        }
        
        if (resources.size() != 1)
        {
            return null;
        }
        
        return resources.get(0);
    }
    
    @Override
    protected boolean existsItem(String path)
    {
        // see https://code.google.com/p/sardine/issues/detail?id=48
        //Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
        //return sardine.exists(url);
        
        try
        {
            getItem(getUrl(path), 0, false);
            return true;
        }
        catch (SardineException se)
        {
            // ignore - does not exist
        }
        
        return false;
    }
    
    private String getUrl(String path)
    {
        return testBaseUrl + path;
    }
}