/*
 * Copyright (C) 2005-2013s Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.clustering.repository;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.*;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class ClusterFTP extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(ClusterFTP.class);
    private static String node1Url;
    private static String node2Url;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");

    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        // testUser = getUserNameFreeDomain(testName);
        logger.info("Starting Tests: " + testName);

        // String[] testUserInfo = new String[] { testUser };
        // CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        /*
         * String shareJmxPort = getAlfrescoServerProperty("Alfresco:Type=Configuration,Category=sysAdmin,id1=default", "share.port").toString();
         * boolean clustering_enabled_jmx = (boolean) getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusteringEnabled");
         * if (clustering_enabled_jmx)
         * {
         * Object clustering_url = getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusterMembers");
         * try
         * {
         * CompositeDataSupport compData = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[0];
         * String clusterIP = compData.values().toArray()[0] + ":" + shareJmxPort;
         * CompositeDataSupport compData2 = (CompositeDataSupport) ((TabularDataSupport) clustering_url).values().toArray()[1];
         * String clusterIP2 = compData2.values().toArray()[0] + ":" + shareJmxPort;
         * node1Url = shareUrl.replace(shareIP, clusterIP);
         * node2Url = shareUrl.replace(shareIP, clusterIP2);
         * }
         * catch (Throwable ex)
         * {
         * throw new SkipException("Skipping as pre-condition step(s) fail");
         * }
         * }
         */
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        if (clusterMembers.size() >= 2)
        {
            node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
            node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
        }
        else
        {
            throw new PageOperationException("Number of cluster members is less than two");
        }
    }

    private static String getAddress(String url)
    {
        checkNotNull(url);
        Matcher m = IP_PATTERN.matcher(url);
        if (m.find())
        {
            return m.group();
        }
        else
        {
            m = DOMAIN_PATTERN.matcher(url);
            if (m.find())
            {
                return m.group();
            }
        }
        throw new PageOperationException(String.format("Can't parse address from url[%s]", url));
    }

    private void checkClusterNumbers()
    {

        webDriverWait(drone, 150000);
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        if (clusterMembers.size() >= 2)
        {
            node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
            node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
            if (logger.isDebugEnabled())
            {
                logger.debug("Number of cluster members is more than one");
            }
        }
        else
        {
            webDriverWait(drone, 60000);
            drone.refresh();
            clusterMembers = clusteringPage.getClusterMembers();
            Assert.assertTrue(clusterMembers.size() >= 2, "Number of cluster members is less than two");
            if (logger.isDebugEnabled())
            {
                logger.debug("Number of cluster members is less than two");
            }
        }
    }

    private void setSshHost(String sshHostUrl)
    {
        sshHost = getAddress(sshHostUrl);
    }

    /**
     * Test - AONE-9297:Add a document
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Add document 'A' direct to node 1 via FTP</li>
     * <li>Add document 'B' direct to node 2 via FTP</li>
     * <li>Check that each node can see both documents</li>
     * <li>Take node 1 down</li>
     * <li>Add document 'C' to the node 2 via FTP</li>
     * <li>Bring node 1 up</li>
     * <li>Check that documents 'A, 'B' and 'C' can be seen on both node</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9297() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        String fileName1 = getFileName(testName) + "_1";
        String fileName2 = getFileName(testName) + "_2";
        String fileName3 = getFileName(testName) + "_3";
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);
        File file3 = newFile(fileName3, fileName3);
        String folderName = getFolderName(testName) + getRandomString(5);
        String serverDB;

        try
        {
            setSshHost(node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            String folderPath = path + folderName + "/";
            // Add document 'A' direct to node 1 via FTP
            assertTrue(FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, file1, folderPath), "Can't create " + fileName1
                    + " content A direct to node 1 via FTP");
            // Add document 'A' direct to node 2 via FTP
            assertTrue(FtpUtil.uploadContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, file2, folderPath), "Can't create " + fileName2
                    + " content B direct to node 2 via FTP");

            // Check that each node can see both documents
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2 + " content B is not exist. node 1");

            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 2");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2 + " content B is not exist. node 2");

            // setSshHost(node1Url);

            // Take node 1 down
            RemoteUtil.applyIptables(node2Url);
            RemoteUtil.applyIptables(shareUrl);
            try
            {
                String dbURL = JmxUtils.getAlfrescoServerProperty(node2Url, "Alfresco:Name=DatabaseInformation", "URL").toString();
                serverDB = JmxUtils.getAddress(dbURL);
                RemoteUtil.applyIptables(serverDB);
            }
            catch (Exception e)
            {
                logger.info("Connection failed to jmx");
            }

            // Add document 'C' to the node 2 via FTP
            assertTrue(FtpUtil.uploadContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, file3, folderPath), "Can't create " + fileName3 + " content C. node 2");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName3, folderPath), fileName3 + " content C is not exist. node 2");

            // Bring node 1 up
            RemoteUtil.removeIpTables(node2Url);

            checkClusterNumbers();

            // Check that documents 'A, 'B' and 'C' can be seen on both node
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 2");

            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2 + " content B is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2 + " content B is not exist. node 2");

            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName3, folderPath), fileName3 + " content C is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName3, folderPath), fileName3 + " content C is not exist. node 2");

        }
        finally
        {
            // Remove folder with documents
            assertTrue(FtpUtil.DeleteSpace(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");
            file1.delete();
            file2.delete();
            file3.delete();
            RemoteUtil.removeIpTables(node2Url);
        }

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-9298:Metadata updates
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Add document 'A' direct to node 1 via FTP</li>
     * <li>Rename document 'A' to document 'B" on node 2 via FTP</li>
     * <li>Check that each node can see the updates</li>
     * <li>Take node 2 down</li>
     * <li>Rename document 'B' to document 'C' on node 1 via FTP</li>
     * <li>Bring node 2 up</li>
     * <li>Check that each node can see the updates</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9298() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        String fileName1 = getFileName(testName) + "_1";
        String fileName2 = getFileName(testName) + "_2";
        String fileName3 = getFileName(testName) + "_3";
        File file1 = newFile(fileName1, fileName1);
        String folderName = getFolderName(testName) + getRandomString(5);
        String serverDB;

        try
        {

            setSshHost(node2Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            String folderPath = path + folderName + "/";
            // Add document 'A' direct to node 1 via FTP
            assertTrue(FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, file1, folderPath), "Can't create " + fileName1
                    + " content A direct to node 1 via FTP");
            // Rename document 'A' to document 'B" on node 2 via FTP
            assertTrue(FtpUtil.renameFile(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderPath, fileName1, fileName2), "Can't rename " + fileName1
                    + " to content B(" + fileName2 + ") direct to node 2 via FTP");

            // Check that each node can see the updates
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1
                    + " content A (not renamed) is exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2
                    + " content B (not renamed) is not exist. node 1");

            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1
                    + " content A (not renamed) is exist. node 2");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2
                    + " content B (not renamed) is not exist. node 2");

            // setSshHost(node1Url);

            // Take node 1 down
            RemoteUtil.applyIptables(node1Url);
            RemoteUtil.applyIptables(shareUrl);
            try
            {
                String dbURL = JmxUtils.getAlfrescoServerProperty(node1Url, "Alfresco:Name=DatabaseInformation", "URL").toString();
                serverDB = JmxUtils.getAddress(dbURL);
                RemoteUtil.applyIptables(serverDB);
            }
            catch (Exception e)
            {
                logger.info("Connection failed to jmx");
            }

            // Rename document 'B' to document 'C' on node 1 via FTP
            assertTrue(FtpUtil.renameFile(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderPath, fileName2, fileName3), "Can't rename " + fileName2
                    + " to content C(" + fileName3 + ") direct to node 1 via FTP");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName3, folderPath), fileName3
                    + " content C (not renamed) is not exist. node 1");

            // Bring node 1 up
            RemoteUtil.removeIpTables(node1Url);

            checkClusterNumbers();

            // Check that each node can see the updates
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is exist. node 1");
            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is exist. node 2");

            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2 + " content B is exist. node 1");
            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2 + " content B is exist. node 2");

            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName3, folderPath), fileName3 + " content C is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName3, folderPath), fileName3 + " content C is not exist. node 2");

        }
        finally
        {
            // Remove folder with documents
            assertTrue(FtpUtil.DeleteSpace(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");
            file1.delete();
            RemoteUtil.removeIpTables(node2Url);
        }

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-9299:Document updates
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Add document 'A' direct to node 1 via FTP</li>
     * <li>Edit the document on node 2 via FTP</li>
     * <li>Check that each node can see the updates</li>
     * <li>Take node 2 down</li>
     * <li>Edit the document on node 1 via FTP</li>
     * <li>Bring node 2 up</li>
     * <li>Check that each node can see the updates</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9299() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        String fileName1 = getFileName(testName) + "_1";
        String newContent2 = getFileName(testName) + "_2";
        String newContent3 = getFileName(testName) + "_3";
        File file1 = newFile(fileName1, fileName1);
        String folderName = getFolderName(testName) + getRandomString(5);
        String serverDB;

        try
        {
            setSshHost(node2Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            String folderPath = path + folderName + "/";
            // Add document 'A' direct to node 1 via FTP
            assertTrue(FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, file1, folderPath), "Can't create " + fileName1
                    + " content A direct to node 1 via FTP");
            // Edit the document on node 2 via FTP
            assertTrue(FtpUtil.EditContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderPath, fileName1, newContent2), "Can't edit " + fileName1
                    + " on node 2 via FTP(" + newContent2 + ")");

            // Check that each node can see the updates
            assertTrue(FtpUtil.checkContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderPath, fileName1, newContent2), "Node 1 can't see update for file "
                    + fileName1 + " for content (" + newContent2 + ")");
            assertTrue(FtpUtil.checkContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderPath, fileName1, newContent2), "Node 2 can't see update for file "
                    + fileName1 + " for content (" + newContent2 + ")");

            // Take node 2 down
            RemoteUtil.applyIptables(node1Url);
            RemoteUtil.applyIptables(shareUrl);
            try
            {
                String dbURL = JmxUtils.getAlfrescoServerProperty(node1Url, "Alfresco:Name=DatabaseInformation", "URL").toString();
                serverDB = JmxUtils.getAddress(dbURL);
                RemoteUtil.applyIptables(serverDB);
            }
            catch (Exception e)
            {
                logger.info("Connection failed to jmx");
            }
            // Edit the document on node 1 via FTP
            assertTrue(FtpUtil.EditContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderPath, fileName1, newContent3), "Can't edit " + fileName1
                    + " on node 1 via FTP(" + newContent3 + ")");
            assertTrue(FtpUtil.checkContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderPath, fileName1, newContent3), "Node 1 can't see update for file "
                    + fileName1 + " for content (" + newContent3 + ")");

            // Bring node 2 up
            RemoteUtil.removeIpTables(node1Url);
            checkClusterNumbers();

            // Check that each node can see the updates
            assertTrue(FtpUtil.checkContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderPath, fileName1, newContent3), "Node 1 can't see update for file "
                    + fileName1 + " for content (" + newContent3 + ")");
            assertTrue(FtpUtil.checkContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderPath, fileName1, newContent3), "Node 2 can't see update for file "
                    + fileName1 + " for content (" + newContent3 + ")");

        }
        finally
        {
            // Remove folder with documents
            assertTrue(FtpUtil.DeleteSpace(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");
            file1.delete();
            RemoteUtil.removeIpTables(node2Url);
        }

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-9300:Delete a document
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Add documents 'A' and 'B' direct to node 1 via FTP</li>
     * <li>Delete document A from node 2 via FTPs</li>
     * <li>Check that each node can see only document 'B'</li>
     * <li>Take node 1 down</li>
     * <li>Delete document 'B' on node 2 via FTP</li>
     * <li>Bring node 1 up</li>
     * <li>Check that documents 'A, 'B' and 'C' can be seen on both node</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9300() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        String fileName1 = getFileName(testName) + "_1";
        String fileName2 = getFileName(testName) + "_2";
        File file1 = newFile(fileName1, fileName1);
        File file2 = newFile(fileName2, fileName2);
        String folderName = getFolderName(testName) + getRandomString(5);
        String serverDB;

        try
        {
            setSshHost(node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            String folderPath = path + folderName + "/";
            // Add documents 'A' and 'B' direct to node 1 via FTP
            assertTrue(FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, file1, folderPath), "Can't create " + fileName1
                    + " content A direct to node 1 via FTP");
            assertTrue(FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, file2, folderPath), "Can't create " + fileName2
                    + " content B direct to node 1 via FTP");

            // Delete document A from node 2 via FTP
            assertTrue(FtpUtil.deleteContentItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), "Can't create " + fileName2
                    + " content B direct to node 1 via FTP");

            // Check that each node can see only document 'B'
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2 + " content B is not exist. node 1");

            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 2");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2 + " content B is not exist. node 2");

            // Take node 1 down
            RemoteUtil.applyIptables(node2Url);
            RemoteUtil.applyIptables(shareUrl);
            try
            {
                String dbURL = JmxUtils.getAlfrescoServerProperty(node2Url, "Alfresco:Name=DatabaseInformation", "URL").toString();
                serverDB = JmxUtils.getAddress(dbURL);
                RemoteUtil.applyIptables(serverDB);
            }
            catch (Exception e)
            {
                logger.info("Connection failed to jmx");
            }

            // Delete document 'B' on node 2 via FTP
            assertTrue(FtpUtil.deleteContentItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), "Can't delete " + fileName2
                    + " content B direct to node 2 via FTP");

            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2 + " content B is exist. node 2");

            // Bring node 1 up
            RemoteUtil.removeIpTables(node2Url);

            checkClusterNumbers();

            // Check that documents 'A, 'B' can't be seen on both node
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is exist. node 1");
            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is exist. node 2");

            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2 + " content B is exist. node 1");
            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName2, folderPath), fileName2 + " content B is exist. node 2");

        }
        finally
        {
            // Remove folder with documents
            assertTrue(FtpUtil.DeleteSpace(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");
            file1.delete();
            file2.delete();
            RemoteUtil.removeIpTables(node2Url);
        }

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE-9301:Uploading a big-sized file
     * <ul>
     * <li>Start upload a file of 1 GB on node 1</li>
     * <li>at the moment stop the node 2</li>
     * <li>When the file is uploaded start the node 2</li>
     * <li>Check that each node can see the document</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" }, timeOut = 600000)
    public void AONE_9301() throws Exception
    {

        String mainFolder = "Alfresco";
        String path = mainFolder + "/";
        String fileName1 = getFileName(testName) + "_1.txt";
        File file1 = getFileWithSize(fileName1,1024);
        file1.deleteOnExit();
        String folderName = getFolderName(testName) + getRandomString(5);
        String serverDB;

        try
        {
            setSshHost(node2Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            if (FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path))
            {
                FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder);
            }
            assertTrue(FtpUtil.createSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), "Can't create " + folderName + " folder");
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is not exist.");

            // Take node 2 down
            RemoteUtil.applyIptables(node1Url);
            RemoteUtil.applyIptables(shareUrl);
            try
            {
                String dbURL = JmxUtils.getAlfrescoServerProperty(node1Url, "Alfresco:Name=DatabaseInformation", "URL").toString();
                serverDB = JmxUtils.getAddress(dbURL);
                RemoteUtil.applyIptables(serverDB);
            }
            catch (Exception e)
            {
                logger.info("Connection failed to jmx");
            }

            String folderPath = path + folderName + "/";
            // Start upload a file of 1 GB on node 1
            assertTrue(FtpUtil.uploadContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, file1, folderPath), "Can't upload " + fileName1
                    + " content A of 1 GB direct to node 1 via FTP");

            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1
                    + " content A of 1 GB is not exist. node 1");

            // Bring node 2 up
            RemoteUtil.removeIpTables(node1Url);

            checkClusterNumbers();

            // Check that each node can see the document
            assertTrue(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 1");
            assertTrue(FtpUtil.isObjectExists(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, fileName1, folderPath), fileName1 + " content A is not exist. node 2");

        }
        finally
        {
            // Remove folder with documents
            assertTrue(FtpUtil.DeleteSpace(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, mainFolder), "Can't delete " + folderName + " folder");
            assertFalse(FtpUtil.isObjectExists(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, folderName, path), folderName + " folder is exist.");
            file1.deleteOnExit();
            RemoteUtil.removeIpTables(node2Url);
        }

        ShareUser.logout(drone);
    }

}