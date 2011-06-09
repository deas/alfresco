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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

/**
 * Abstract Repo BenchMark 
 * 
 * - tests can be run using Apache JMeter (JUnit Request Sampler)
 * 
 * @author janv
 *
 */
public abstract class AbstractRepoBenchmarkSystemTest extends TestCase
{
    private static Log logger = LogFactory.getLog(AbstractRepoBenchmarkSystemTest.class);
    
    public final static String NEW_DOCUMENT_MKR = "{NEW_DOCUMENT_MKR}";
    public final static String NEW_FOLDER_MKR = "{NEW_FOLDER_MKR}";
    
    public final static String DELIM = "/";
    
    protected String testUserUN = null;
    protected String testUserPW = null;
    
    protected String testBaseUrl = null;
    protected String testBaseFolderPath = null;
    protected String testThreadFolder = null;
    protected String testPathIn = null;
    protected long   testLen = -1L;
    
    protected int maxFolders = 5; // eg. t01 to t05
    
    protected Random random;
    
    protected String testPathCurrent;  // note: with markers resolved (if any)
    protected String testPathToDelete; // for teardown
    
    private static boolean testDataImported = false;
    
    
    public AbstractRepoBenchmarkSystemTest() 
    {
        super();
    }
    
    public AbstractRepoBenchmarkSystemTest(String csvProps) throws Exception
    {
        super(csvProps);
        
        random = new Random(System.currentTimeMillis());
        
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
            
            if (propertiesMap.size() == 0)
            {
                // some defaults for local test
                testUserUN = "admin";
                testUserPW = "admin";
                
                testBaseFolderPath = "/testdata"; // relative to "/Company Home"
                testThreadFolder = "/t01";
            }
            else
            {
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
                
                val = propertiesMap.get("basefolderpath");
                if (val != null)
                {
                    testBaseFolderPath = new String(val);
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
                    testPathIn = new String(val);
                }
                
                val = propertiesMap.get("len");
                if (val != null)
                {
                    testLen = new Long(new String(val));
                }
            }
            
            // normalize paths
            testBaseUrl = normalizePath(testBaseUrl);
            testBaseFolderPath = normalizePath(testBaseFolderPath);
            testThreadFolder = normalizePath(testThreadFolder);
            testPathIn = normalizePath(testPathIn);
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
        
        // for local sanity check
        if (testLen <= 0)
        {
            // used by,eg. testDocumentCreate, testItemRename
            testLen = 1024;
        }
        
        String testName = getName();
        
        // for local sanity check
        if (testPathIn == null)
        {
            if (testName.equals("testItemRead") || testName.equals("testContentUpdate") || testName.equals("testContentRead"))
            {
                // file path
                testPathIn = "/M-35d-600f-200Kb/folder-00000/folder-00000/folder-00000/file-00010.txt";
            }
            else if (testName.equals("testItemRename") || testName.equals("testItemDelete"))
            {
                // file path
                testPathIn = "/M-35d-600f-200Kb/folder-00000/folder-00000/folder-00000/"+NEW_DOCUMENT_MKR;
            }
            else
            {
                // folder path
                testPathIn = "/S-31d-60f-1Kb/folder-00000/folder-00000";
            }
        }
        
        if (testDataImported == false)
        {
            if (! existsItem(testBaseFolderPath))
            {
                String userDir = System.getProperty("user.dir");
                
                // note: for now, assume user dir is an eclipse project directory (eg. "repository-bm" or "system-build-test")
                String zipPath = userDir+"/../repository-bm/source/test-resources/testdata_mini.zip";
                expandAndImport("/", zipPath);
                
                System.out.println("Test data dir does NOT exist hence imported: "+zipPath);
            }
            else
            {
                //System.out.println("Test data dir exists hence assume import is NOT required");
            }
            
            testDataImported = true;
        }
        
        testPathCurrent = new String(testPathIn);
        
        if (testName.equals("testItemRename") || testName.equals("testItemDelete"))
        {
            // pre-create item - not measured by JMeter/JUnit
            String path = getTestPath();
            
            String[] parts = splitBase(path);
            String parentFolderPath = parts[0];
            
            if (parts[1].equals(NEW_DOCUMENT_MKR))
            {
                String newPath = createDocument(parentFolderPath, null, testLen);
                
                testPathCurrent = extendPath(splitBase(testPathIn)[0], splitBase(newPath)[1]);
            }
            else if (parts[1].equals(NEW_FOLDER_MKR))
            {
                String newPath = createFolder(parentFolderPath, null);
                
                testPathCurrent = extendPath(splitBase(testPathIn)[0], splitBase(newPath)[1]);
            }
        }
    }
    
    public void oneTimeTearDown() throws Exception
    {
    }
    
    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        if (testPathToDelete != null)
        {
            deleteItem(testPathToDelete);
        }
    }
    
    /**
     * FolderCreate
     */
    public void testFolderCreate() throws Exception
    {
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String parentFolderPath = getTestPath();
            
            String newPath = createFolder(parentFolderPath, null);
            
            testPathToDelete = newPath; // for teardown
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testFolderCreate: "+newPath+" (created in "+(System.currentTimeMillis()-start)+" ms");
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
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String newPath = createDocument(getTestPath(), null, testLen);
            
            testPathToDelete = newPath; // for teardown
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testDocumentCreate: "+newPath+" (created in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (Exception e)
        {
            print(e);
            throw e;
        }
    }
    
    
    /**
     * ItemDelete
     */
    public void testItemDelete() throws Exception
    {
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String path = getTestPath();
            
            deleteItem(path);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("testDelete: "+path+" (created in "+(System.currentTimeMillis()-start)+" ms");
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
        try
        {
            long start = 0;
            if (logger.isDebugEnabled())
            {
                start = System.currentTimeMillis();
            }
            
            String documentPath = getTestPath();
            
            InputStream is = getContent(documentPath);
            
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
                logger.debug("testContentRead: "+documentPath+" (read in "+(System.currentTimeMillis()-start)+" ms");
            }
        } 
        catch (Exception e)
        {
            print(e);
            throw e;
        }
    }
    
    
    /**
     * get (relative) test path
     * 
     * @return
     */
    protected String getTestPath()
    {
        StringBuilder sb = new StringBuilder();
        
        // note: sub-paths have already been normalized
        sb.append(testBaseFolderPath).
           append((testThreadFolder == null ? "" : testThreadFolder)).
           append(testPathCurrent);
        
        if (logger.isTraceEnabled())
        {
            logger.trace("getPath: "+sb.toString());
        }
        
        return sb.toString();
    }
    /**
     * does item exist
     * 
     * @param path path to document or folder
     * @return  true if item (document or folder) exists
     */
    protected abstract boolean existsItem(String path);
    
    
    /**
     * create document - if name is null then generate random name
     * 
     * @param parentFolderUrl
     * @param name
     * @param len
     * @return
     * @throws Exception
     */
    protected String createDocument(String parentFolderUrl, String name, long len) throws Exception
    {
        return createDocument(parentFolderUrl, name, getPhantomStream(len), len);
    }
    
    
    /**
     * create document - if name is null then generate random name
     * 
     * @param parentFolderPath
     * @param name
     * @param is
     * @param len
     * @return
     * @throws Exception
     */
    protected abstract String createDocument(String parentFolderPath, String name, InputStream is, long len) throws Exception;
    
    
    /**
     * create folder - if name is null then generate random name
     * 
     * @param parentFolderPath
     * @param name
     * @return
     * @throws Exception
     */
    protected abstract String createFolder(String parentFolderPath, String name) throws Exception;
    
    
    /**
     * delete item - document or folder
     * 
     * @param path
     * @throws Exception
     */
    protected abstract void deleteItem(String path) throws Exception;
    
    /**
     * get document content
     * 
     * @param documentPath
     * @return
     * @throws Exception
     */
    protected abstract InputStream getContent(String documentPath) throws Exception;
    
    /**
     * normalize path - strip trailing delimiter and add leading delimiter (if not a url)
     * 
     * @param subPath
     * @return
     */
    protected String normalizePath(String subPath)
    {
        if (subPath != null)
        {
            if ((! subPath.startsWith(DELIM)) && (! subPath.contains("://")))
            {
                subPath = DELIM + subPath;
            }
            
            if (subPath.endsWith(DELIM))
            {
                subPath = subPath.substring(0, subPath.length()-1);
            }
        }
        
        return subPath;
    }
    
    /**
     * split path
     * 
     * @param path
     * @return
     */
    protected String[] splitBase(String path)
    {
        int off = path.lastIndexOf(DELIM);
        
        String [] parts = new String[2];
        
        parts[0] = path.substring(0, off);
        parts[1] = path.substring(off + 1);
        
        return parts;
    }
    
    /**
     * extend path
     * 
     * @param parentFolderPath
     * @param name
     * @return
     */
    protected String extendPath(String parentFolderPath, String name)
    {
        return normalizePath(parentFolderPath) + normalizePath(name);
    }
    
    /**
     * get phantom stream
     * 
     * @param size
     * @return
     */
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
    
    /**
     * print stacktrace
     * 
     * @param e
     */
    protected void print(Exception e)
    {
        System.err.println(e);
        e.printStackTrace();
    }
    
    private static final int BUFFER_SIZE = 16384;
    
    /**
     * hierarchically import a zip file
     * 
     * @param parentFolderUrl
     * @param zipFilePath
     * @throws Exception
     */
    protected void expandAndImport(String parentFolderUrl, String zipFilePath) throws Exception
    {
        // do the import here
        File file = new File(zipFilePath);
        if (! file.exists())
        {
            String errorMesg = "File does not exist - cannot import: "+zipFilePath;
            System.out.println(errorMesg);
            throw new Exception(errorMesg);
        }
        ZipFile zipFile = new ZipFile(file);
        
        File tempDir = null;
        try
        {
            tempDir = new File("tmpDir");
            tempDir.mkdirs();
            
            try
            {
                extractFile(zipFile, tempDir.getPath());
                importDirectory(tempDir.getPath(), parentFolderUrl);
            }
            finally
            {
                deleteDirectory(tempDir);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to extract/import ZIP file: ", e);
        }
        finally
        {
            // now the import is done, delete the temporary dir
            if (tempDir != null)
            {
                tempDir.delete();
            }
        }
    }
    @SuppressWarnings("unchecked")
    private void extractFile(ZipFile archive, String extractDir)
    {
        String fileName;
        String destFileName;
        byte[] buffer = new byte[BUFFER_SIZE];
        extractDir = extractDir + File.separator;
        try
        {
            for (Enumeration e = archive.getEntries(); e.hasMoreElements();)
            {
                ZipEntry entry = (ZipEntry) e.nextElement();
                if (!entry.isDirectory())
                {
                    fileName = entry.getName();
                    fileName = fileName.replace('/', File.separatorChar);
                    destFileName = extractDir + fileName;
                    File destFile = new File(destFileName);
                    String parent = destFile.getParent();
                    if (parent != null)
                    {
                        File parentFile = new File(parent);
                        if (!parentFile.exists()) parentFile.mkdirs();
                    }
                    InputStream in = new BufferedInputStream(archive.getInputStream(entry), BUFFER_SIZE);
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(destFileName), BUFFER_SIZE);
                    int count;
                    while ((count = in.read(buffer)) != -1)
                    {
                        out.write(buffer, 0, count);
                    }
                    in.close();
                    out.close();
                }
                else
                {
                    File newdir = new File(extractDir + entry.getName());
                    newdir.mkdirs();
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to extract ZIP file: ", e);
        }
    }
    
    // recursively import from the local filesystem directory
    private void importDirectory(String dir, String parentFolderPath)
    {
        File topdir = new File(dir);
        for (File file : topdir.listFiles())
        {
            try
            {
                String name = file.getName();
                if (file.isFile())
                {
                    InputStream is = null;
                    try
                    {
                        is = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
                        createDocument(parentFolderPath, name, is, file.length());
                    }
                    finally
                    {
                        if (is != null) { is.close(); }
                    }
                }
                else
                {
                    createFolder(parentFolderPath, name);
                    importDirectory(file.getPath(), extendPath(parentFolderPath, name));
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to import ZIP file: ", e);
            }
        }
    }
    
    // recursively delete the local filesystem directory
    private void deleteDirectory(File dir)
    {
        if (dir != null)
        {
            File dirToDelete = new File(dir.getPath());
            
            // listFiles can return null if the path is invalid i.e. already been deleted,
            // therefore check for null before using in loop
            File[] files = dirToDelete.listFiles();
            if (files != null)
            {
                for (File file : files)
                {
                    if (file.isFile()) file.delete();
                    else deleteDirectory(file);
                }
            }
            
            // delete provided directory
            dir.delete();
        }
    }
}