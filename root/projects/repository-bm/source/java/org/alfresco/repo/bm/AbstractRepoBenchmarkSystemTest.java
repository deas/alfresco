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
package org.alfresco.repo.bm;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Abstract Repo BenchMark 
 * 
 * - tests can be run using Apache JMeter (JUnit Request Sampler)
 * 
 * @author jan
 *
 */
public abstract class AbstractRepoBenchmarkSystemTest extends TestCase
{
    protected String testUserUN = "admin";
    protected String testUserPW = "admin";
    
    protected String testBaseUrl = null;
    protected String testThreadFolder = "t01";
    protected String testPath = null;
    protected long   testLen = -1L;
    
    protected int maxFolders = 5; // eg. t01 to t05
    
    public AbstractRepoBenchmarkSystemTest() 
    {
        super();
    }
    
    public AbstractRepoBenchmarkSystemTest(String csvProps) throws Exception
    {
        super(csvProps);
        
        try
        {
            String[] props = csvProps.split(",");
            
            Map<String, String> propertiesMap = new HashMap<String, String>();
            for (String prop : props)
            {
                String[] nameValue = prop.split("=");
                if (nameValue.length != 2)
                {
                    // skip
                    continue;
                }
                propertiesMap.put(nameValue[0], nameValue[1]);
            }
            
            String val = propertiesMap.get("un");
            if (val != null)
            {
                testUserUN = new String(val);
            }
            
            val = propertiesMap.get("pwd");
            if (val != null)
            {
                testUserPW = new String(val);
            }
            
            val = propertiesMap.get("baseurl");
            if (val != null)
            {
                testBaseUrl = new String(val);
            }
            
            val = propertiesMap.get("threadnum");
            if (val != null)
            {
                // eg. 0, 1, 2 ... 99, 100, 101, ...
                int threadNum = new Integer(new String(val));
                
                // eg. convert to t01 to t05 (if maxFolders = 5)
                threadNum = threadNum % maxFolders;
                threadNum++;
                testThreadFolder = (threadNum < 10 ? "t0"+threadNum : "t"+threadNum);
            }
            
            val = propertiesMap.get("path");
            if (val != null)
            {
                testPath = new String(val);
            }
            
            val = propertiesMap.get("len");
            if (val != null)
            {
                testLen = new Long(new String(val));
            }
        }
        catch (Exception e)
        {
            print(e);
            throw e;
        }
    }
    
    public void oneTimeSetUp() throws Exception
    {
    }
    
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
    }
    
    public void oneTimeTearDown() throws Exception
    {
    }
    
    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    // get path (or url)
    abstract protected String getPath();
    
    protected InputStream getPhantomStream(final long size)
    {
        return new InputStream()
        {
            private long counter = -1;
            
            @Override
            public int read() throws IOException
            {
                counter++;
                if (counter >= size)
                {
                    return -1;
                }
                
                return counter % 10 == 0 ? ' ' : '0' + (int) (counter % 10);
            }
        };
    }
    
    protected void print(Exception e)
    {
        System.err.println(e);
        e.printStackTrace();
    }
}