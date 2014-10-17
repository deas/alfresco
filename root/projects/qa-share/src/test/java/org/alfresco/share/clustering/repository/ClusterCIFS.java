package org.alfresco.share.clustering.repository;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.util.*;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author  Olga Antonik
 */
@Listeners(FailedTestListener.class)
public class ClusterCIFS extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(ClusterCIFS.class);
    private static String node1Url;
    private static String node2Url;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");


    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
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

    private void setSshHost(String sshHostUrl)
    {
        sshHost = getAddress(sshHostUrl);
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

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_9291() throws Exception
    {
        String fileName1 = getRandomString(5) + ".txt";
        String fileName2 = getRandomString(5) + ".txt";
        String fileName3 = getRandomString(5) + ".txt";
        String serverDB;

        try
        {
            setSshHost(node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            // add document 'A' direct to node 1 via CIFS
            CifsUtil.addContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1, fileName1);

            // add document 'B' direct to node 2 via CIFS
            CifsUtil.addContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2, fileName2);

            // check that each node can see both documents
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2));
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1));

            // take node 1 down
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

            // add document 'C' to the node 1 via CIFS
            CifsUtil.addContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName3, fileName3);

            // bring node 1 up
            RemoteUtil.removeIpTables(node1Url);
            checkClusterNumbers();

            // check that documents 'A, 'B' and 'C' can be seen on both node
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1));
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1));
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2));
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2));
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName3));
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName3));
        }
        finally
        {
            for(String item : new String[]{fileName1, fileName2, fileName3})
                if(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/",item))
                    CifsUtil.deleteContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", item);
        }

        ShareUser.logout(drone);

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_9292() throws Exception
    {
        String fileName1 = getRandomString(5) + ".txt";
        String fileName2 = getRandomString(5) + ".txt";
        String fileName3 = getRandomString(5) + ".txt";
        String serverDB;

        try
        {
            setSshHost(node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            // add document 'A' direct to node 1 via CIFS
            CifsUtil.addContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1, fileName1);

            // check that each node can see document
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1));

            // rename document 'A' to document 'B" on node 2 via CIFS
            CifsUtil.renameItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1, fileName2);

            // check that each node can see the updates
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2));
            assertFalse(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1));

            // take node 2 down
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

            // rename document 'B' to document 'C" on node 1 via CIFS
            CifsUtil.renameItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2, fileName3);

            // bring node 2 up
            RemoteUtil.removeIpTables(node1Url);
            checkClusterNumbers();

            // check that each node can see the updates
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName3));
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName3));

            assertFalse(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2));
            assertFalse(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2));
        }

        finally
        {
            for(String item : new String[]{fileName1, fileName2, fileName3})
                if(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/",item))
                    CifsUtil.deleteContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", item);
        }

        ShareUser.logout(drone);

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_9293() throws Exception
    {
        String fileName = getRandomString(5) + ".txt";
        String content1 = getRandomString(5);
        String content2 = getRandomString(5);
        String serverDB;

        try
        {
            setSshHost(node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            // add document 'A' direct to node 1 via CIFS
            CifsUtil.addContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName, fileName);

            // check that each node can see document
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName));

            // edit the document on node 2 via CIFS
            CifsUtil.editContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName, content1);

            // check that each node can see the updates
            assertTrue(CifsUtil.checkContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName, content1));

            // take node 2 down
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

            // edit the document on node 1 via CIFS
            CifsUtil.editContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName, content2);

            // bring node 2 up
            RemoteUtil.removeIpTables(node1Url);
            checkClusterNumbers();

            // check that each node can see the updates
            assertTrue(CifsUtil.checkContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName, content2));
            assertTrue(CifsUtil.checkContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName, content2));
        }

        finally
        {
            if(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName))
                CifsUtil.deleteContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName);

        }

        ShareUser.logout(drone);

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_9294() throws Exception
    {
        String fileName1 = getRandomString(5) + ".txt";
        String fileName2 = getRandomString(5) + ".txt";
        String serverDB;

        try
        {
            setSshHost(node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            // add documents 'A' and 'B' direct to node 1 via CIFS
            CifsUtil.addContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1, fileName1);
            CifsUtil.addContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2, fileName2);

            // check that each node can see both documents
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1));
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2));

            // delete document A from node 2 via CIFS
            CifsUtil.deleteContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1);

            // check that each node can see only document 'B'
            assertFalse(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1));
            assertFalse(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1));
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2));
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2));

            // take node 1 down
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

            // delete document 'B' on node 2 via CIFS
            CifsUtil.deleteContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2);

            // bring node 1 up
            RemoteUtil.removeIpTables(node1Url);
            checkClusterNumbers();

            // check that documents 'A, 'B' can't be seen on both node
            assertFalse(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1));
            assertFalse(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName1));

            assertFalse(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2));
            assertFalse(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName2));
        }
        finally
        {
            for(String item : new String[]{fileName1, fileName2})
                if(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", item))
                    CifsUtil.deleteContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", item);
        }

        ShareUser.logout(drone);

    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_9295() throws Exception
    {
        String fileName = getRandomString(5) + ".txt";
        File file = getFileWithSize(fileName, 1024);
       file.deleteOnExit();
        String folderName = getRandomString(5);
        String serverDB;

        try
        {
            setSshHost(node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            // take node 1 down
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

            // add document 'C' to the node 1 via CIFS
            CifsUtil.uploadContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", file);

            // bring node 1 up
            RemoteUtil.removeIpTables(node1Url);
            checkClusterNumbers();

            // check that documents 'A, 'B' and 'C' can be seen on both node
            assertTrue(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName));
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName));
        }
        finally
        {
            if(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName))
                CifsUtil.deleteContent(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName);

        }

        ShareUser.logout(drone);

    }

    //@Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_9296() throws Exception
    {
        String fileName = getRandomString(5) + ".txt";
        String serverDB;
        StringBuilder builder;
        BufferedReader reader;

        try
        {
            setSshHost(node1Url);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            // add document 'A' direct to node 1 via CIFS
            CifsUtil.addContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName, fileName);

            // check that each node can see document
            assertTrue(CifsUtil.checkItem(node2Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName));

            // open document 'A.doc' for editing on node 1 via CIFS
            String server = PageUtils.getAddress(node1Url).replaceAll("(:\\d{1,5})?", "");
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(ADMIN_USERNAME + ":" + ADMIN_PASSWORD);

            SmbFile sFile = new SmbFile("smb://" + server + "/Alfresco/" + fileName, auth);
            SmbFileOutputStream smbFileOutputStream = new SmbFileOutputStream(sFile);
            SmbFileInputStream fileInputStream = new SmbFileInputStream(sFile);
            byte[] buf = new byte[16 * 1024 * 1024];
            fileInputStream.read(buf);

//            assertTrue(sFile.canWrite());
//            SmbFileOutputStream sfos = new SmbFileOutputStream(sFile);

            // open document 'A.doc' for editing on node 2 via CIFS
            String server1 = PageUtils.getAddress(node2Url).replaceAll("(:\\d{1,5})?", "");
            NtlmPasswordAuthentication auth1 = new NtlmPasswordAuthentication(ADMIN_USERNAME + ":" + ADMIN_PASSWORD);

            SmbFile sFile1 = new SmbFile("smb://" + server1 + "/Alfresco/" + fileName, auth1);
            assertFalse(sFile1.canWrite());

        }
        finally
        {
            if(CifsUtil.checkItem(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName))
                CifsUtil.deleteContent(node1Url, ADMIN_USERNAME, ADMIN_PASSWORD, "Alfresco/", fileName);
        }

        ShareUser.logout(drone);

    }

}
