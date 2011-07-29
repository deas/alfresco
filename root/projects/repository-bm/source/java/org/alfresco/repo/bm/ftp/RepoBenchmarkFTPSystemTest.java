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
package org.alfresco.repo.bm.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.alfresco.repo.bm.AbstractRepoBenchmarkSystemTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * Repo Bench Mark - FTP
 * 
 * - access Alfresco Repository using FTP client
 * - tests can be run using Apache JMeter (JUnit Request Sampler)
 * 
 * @author mrogers
 *
 */
public class RepoBenchmarkFTPSystemTest extends AbstractRepoBenchmarkSystemTest
{
    private static Log logger = LogFactory.getLog(RepoBenchmarkFTPSystemTest.class);
    
    //private FTPClient ftp = new FTPClient();
    
    private FTPClient ftp;
    
    /**
     * A FTP server has a very limited number of connections. And is stateful.   
     * We can't just go and create hundreds of connections.
     * 
     * This connection pool manages the small number of connections to the ftp server.
     * these unit tests will then contend for a connection.
     */
    private int MAX_CONNECTIONS = 4;

    private class FTPConnectionPool 
    {
        String hostname;
        String userName;
        String password;
        
        ArrayList<FTPClient> connections = new ArrayList<FTPClient>();
        
        BlockingQueue<FTPClient> queue = new ArrayBlockingQueue<FTPClient>(MAX_CONNECTIONS, true);
        
        FTPClient client;
        
        FTPConnectionPool(String hostname, String userName, String password) 
        {
            this.hostname = hostname;
            this.userName = userName;
            this.password = password;
        }
        
        public FTPClient getSession() throws IOException, InterruptedException
        {
            FTPClient connection;
            
            if(connections.size() < MAX_CONNECTIONS)
            {
                connection = createConnection();
                connections.add(connection);
                return connection;
            }
            else
            {
                connection = queue.take();
                return connection;
            }
         }
        
        public void releaseSession(FTPClient session) throws IOException
        {
            queue.add(session);
        }
        
        private FTPClient createConnection() throws IOException
        {
            FTPClient client = new FTPClient();
            
            client.connect(hostname);
  
            boolean result = client.login(userName, password);
      
            if(!result)
            {
                throw new IOException("Unable to login");
            }
            
            return client;
        }
    }
    
    static FTPConnectionPool pool;

    
    public RepoBenchmarkFTPSystemTest(String csvProps) throws Exception
    {
        super(csvProps);
              
        try
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("FTPTest: "+csvProps);
            }
            
            // for local sanity check
            if (testHostname == null)
            {
                testHostname = "localhost";
            }
            
            if(pool == null)
            {
                pool = new FTPConnectionPool(testHostname, testUserUN, testUserPW);
            }
            

        }
        catch (Exception e)
        {
            logger.error("unable to construct test", e);
            print(e);
            throw e;
        }
    }
    

    @Override
    public void setUp() throws Exception
    {
        // note: setUp time not be measured by JMeter->JUnit
        super.setUp();
        
        if(ftp == null)
        {
            ftp = pool.getSession();
        }
    }
    
    @Override
    public void tearDown() throws Exception
    {
        // note: tearDown time not be measured by JMeter->JUnit
        super.tearDown();
        
        if(ftp != null)
        {
            pool.releaseSession(ftp);
        }
        ftp = null;
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
            
            String path2 = getUrl(path);
            
            FTPFile[] files = ftp.listFiles(path2);
            
            int cnt = 0;
            for (FTPFile file : files)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug(file.getName());
                }
                cnt++;
            }
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testGetChildren: "+path+" (found "+cnt+" in "+(System.currentTimeMillis()-start)+" ms");
            }
           
        } 
        catch (Exception se)
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
            
            ftp.list(path);
            
        } 
        catch (IOException se)
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

            ftp.rename(getUrl(oldPath), getUrl(newPath));
            // rename
            
            testPathToDelete = newPath; // for teardown
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testItemRename: "+newPath+" (renamed in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (IOException se)
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
        if (testPathIn == null)
        {
            testPathIn = "/S-7d-30000f-1Kb/folder-00000/folder-00000/folder-00000/file-00000.txt";
        }
        
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String path = getTestPath();
            
            // Path is a full folder/path
            int pos = path.lastIndexOf("/");
            String folder = path.substring(0, pos);
            String fileName = path.substring(pos + 1);
            
            if(logger.isDebugEnabled())
            {
                logger.debug("folder:" + folder + " file:" + fileName);
            }
                        
            boolean cwd = ftp.changeWorkingDirectory(getUrl(folder));
            
            if(!cwd)
            {
                throw new Exception("Unable to change directory to "+folder);
            }
              
            Long len = testLen;
            
            if(len <= 0)
            {
                len = 5000l;
            }
            
            
            if ((len != null) && (len > 0))
            {
                InputStream dummy =  getPhantomStream(len);
                ftp.storeFile(fileName, dummy);
                dummy.close();
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
        catch (IOException se)
        {
            print(se);
            throw se;
        }
    }
    
    @Override
    protected String createDocument(String parentFolderPath, String name, InputStream is, long len) throws Exception
    {   
        logger.debug("createDocument" + parentFolderPath + name);
        
        if (name == null)
        {
            name = "document-" + System.currentTimeMillis() + "-" + random.nextInt(1000);
        }
        
        
        FTPClient session = pool.getSession();
        try
        {
            String url = getUrl(parentFolderPath);
        
            boolean cwd = session.changeWorkingDirectory(url);
        
            if(!cwd)
            {
                throw new Exception("Unable to change directory to: "+parentFolderPath);
            }
        
            session.storeFile(name, is);
        }
        finally
        {
            pool.releaseSession(session);
        }
        
        String newDocumentPath = extendPath(parentFolderPath, name);
        return newDocumentPath;
        
    }
    
    @Override
    protected String createFolder(String parentFolderPath, String name) throws Exception
    {
        if (name == null)
        {
            name = "folder-" + System.currentTimeMillis() + "-" + random.nextInt(1000);
        }
        
        String url = getUrl(parentFolderPath);
        
        FTPClient session = pool.getSession();
        try
        {
            boolean cwd = ftp.changeWorkingDirectory(url);
            ftp.mkd(name);
        } 
        finally
        {
            pool.releaseSession(session);
        }
       
        String newFolderPath = extendPath(parentFolderPath, name);
        return newFolderPath;
    }
    
    @Override
    protected void deleteItem(String itemPath) throws Exception
    {
        FTPClient session = pool.getSession();
        try
        {    
            ftp.deleteFile(getUrl(itemPath));
        }
        finally
        {
            pool.releaseSession(session);
        }
    }
    
    @Override
    protected InputStream getContent(String documentPath) throws IOException
    {

        if(logger.isDebugEnabled())
        {
            logger.debug("getContent:" + documentPath);
        }
        InputStream is = ftp.retrieveFileStream(getUrl(documentPath));
        
        
        FTPInputStream ftpStream = new FTPInputStream(is, ftp);
        
        return ftpStream;
    }
    
    /**
     * FTP Input stream decorates an InputStream to add close of the the ftp 
     * socket on stream.close.
     * 
     * @author mrogers
     */
    private class FTPInputStream extends InputStream
    {
        InputStream is;
        FTPClient ftp;
        
        public FTPInputStream(InputStream is, FTPClient ftp)
        {
            this.is = is;
            this.ftp = ftp;
        }

        @Override
        public int read() throws IOException
        {
            return is.read();

        }
        public int read(byte[] arg0, int arg1, int arg2) throws java.io.IOException
        {
            return is.read(arg0, arg1, arg2);
        }
        
        public void close() throws java.io.IOException
        {
            is.close();
            ftp.completePendingCommand();
        }
    }
    
    @Override
    protected boolean existsItem(String path) 
    {        
        try
        {
            FTPClient session = pool.getSession();
            try
            {
                boolean success = session.changeWorkingDirectory(getUrl(path));
                
                if(success)
                {
                    return true;
                }
            }
            finally
            {
                pool.releaseSession(session);
            }
            return false;
        }
        catch (IOException se)
        {
            // ignore - does not exist
            return false;
        }
        catch(InterruptedException ie)
        {
            return false;
        }
    }
    
    private String getUrl(String path)
    {
        return "alfresco/" + path;
    }
}