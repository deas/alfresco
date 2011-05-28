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
import java.util.List;

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
 * @author jan
 *
 */
public class RepoBenchmarkWebDAVSystemTest extends AbstractRepoBenchmarkSystemTest
{
    private static Log logger = LogFactory.getLog(RepoBenchmarkWebDAVSystemTest.class);
    
    private Sardine sardine;
    
    private String pathToDelete; // for tearDown
    
    
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
                testBaseUrl = "http://localhost:8080/alfresco/webdav/testdata";
            }
            
            sardine = SardineFactory.begin(testUserUN, testUserPW);
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
        super.setUp();
        
        pathToDelete = null;
    }
    
    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        if (pathToDelete != null)
        {
            sardine.delete(pathToDelete);
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
            
            Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
            
            String url = getPath();
            List<DavResource> resources = sardine.getResources(url);
            
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
                logger.debug("testGetChildren: "+url+" (found "+cnt+" in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (SardineException se)
        {
            print(se);
            throw se;
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
            
            Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
            
            String url = getPath();
            List<DavResource> resources = sardine.getResources(url);
            
            if (resources.size() != 1)
            {
                throw new Exception("testItemRead: not found "+url);
            }
            
            if (logger.isDebugEnabled())
            {
                logger.debug(resources.get(0).getName());
            }
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testItemRead: "+url+" (found in "+(System.currentTimeMillis()-start)+" ms");
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
            
            Sardine sardine = SardineFactory.begin(testUserUN, testUserPW);
            
            String url = getPath();
            List<DavResource> resources = sardine.getResources(url);
            
            if (resources.size() != 1)
            {
                throw new Exception("testContentUpdate: not found "+url);
            }
            
            DavResource res = resources.get(0);
            long len = res.getContentLength();
            
            if (len > 0)
            {
                sardine.put(url, getPhantomStream(len), "text/plain");
            }
            else
            {
                throw new Exception("testContentUpdate: empty file "+url);
            }
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testContentUpdate: "+url+" (updated in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (SardineException se)
        {
            print(se);
            throw se;
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
            
            String url = getPath();
            
            String name = "document-" + System.currentTimeMillis() + "-" + this.hashCode();
            
            String path = url + "/" + name;
            
            sardine.put(path, getPhantomStream(testLen), "text/plain");
            
            pathToDelete = path;
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testDocumentCreate: "+path+" (created in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (SardineException se)
        {
            print(se);
            throw se;
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
            
            String url = getPath();
            
            InputStream is = sardine.getInputStream(url);
            
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
                logger.debug("testContentRead: "+url+" (read in "+(System.currentTimeMillis()-start)+" ms");
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
        
        sb.append(testBaseUrl).append(testBaseUrl.endsWith("/") ? "" : "/").
           append(testThreadFolder).
           append(testPath.startsWith("/") ? "" : "/").append(testPath);
        
        if (logger.isTraceEnabled())
        {
            logger.trace("getPath: "+sb.toString());
        }
        
        return sb.toString();
    }
}