package org.alfresco.share.clustering;

import org.alfresco.po.share.*;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.systemsummary.ClusterValidationPage;
import org.alfresco.share.util.JmxUtils;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.RemoteUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by maryia.zaichanka on 8/18/14.
 */
@Listeners(FailedTestListener.class)
public class RepositoryServerClusteringTest extends AbstractUtils

{
    private static Log logger = LogFactory.getLog(RepositoryServerClusteringTest.class);
    private static String node1Url;
    private static String node2Url;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    private static final String regexIp = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";
    private static final String regexPort = "(\\d{1,4})";
    private static final String regexDate = "[A-S]([a-z]){2}\\s([0-3][0-9],)\\s(20[0-9][0-9])\\s(1[012]|[1-9])(:[0-5][0-9]){2}\\s(AM|PM)";

    private static final String regexServer = "([a-zA-Z0-9-])+";


    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("\\w+(\\.\\w+)*(\\.\\w{2,})");


    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        logger.info("Starting Tests: " + testName);

        ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
    }

    private static String getAddress(String url)
    {
        checkNotNull(url);
        Matcher m = IP_PATTERN.matcher(url);
        if (m.find()) {
            return m.group();
        } else {
            m = DOMAIN_PATTERN.matcher(url);
            if (m.find()) {
                return m.group();
            }
        }
        throw new PageOperationException(String.format("Can't parse address from url[%s]", url));
    }

    private void setSshHost(String sshHostUrl)
    {
        sshHost = getAddress(sshHostUrl);
    }


    @Test(groups = {"EnterpriseOnly"})
    public void AONE_575() throws Exception
    {


        // Repository Server Clustering page is opened
        SystemSummaryPage sysSummaryPage = drone.getCurrentPage().render();

        // Verify the list of tabs in the left-hand side of the Console
        Assert.assertTrue(sysSummaryPage.isConsoleLinkPresent(AdminConsoleLink.RepositoryServerClustering), "Repository Server Clustering tab isn't present " +
                "in the left-hand side of the Console");

        // Click on the Repository Server Clustering tab
        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        if (clusterMembers.size() >= 2) {
            node1Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(0) + ":" + nodePort);
            node2Url = shareUrl.replaceFirst(regexUrl, clusterMembers.get(1) + ":" + nodePort);
        } else {
            throw new PageOperationException("Number of cluster members is less than two");
        }

    }

    @Test(groups = {"EnterpriseOnly"}, dependsOnMethods = "AONE_575", alwaysRun = true)
    public void AONE_576() throws Exception
    {

        String desc = "Servers connected to the same database instance are usually clustered automatically. " +
                "In most cases no additional configuration is necessary.";
        String ipDesc = "The server IP address.";
        String serverDesc = "The server that you are currently connected to.";

        // Repository Server Clustering page is opened
        RepositoryServerClusteringPage clusteringPage = drone.getCurrentPage().render();

        // Verify the list of components of the page
        // Connected Host section
        Assert.assertTrue(clusteringPage.isServerNamePresent(), "Server name isn't present at Host Server section");
        Assert.assertTrue(clusteringPage.isIpAddressPresent(), "IP Address isn't present at Host Server section");
        Assert.assertTrue(clusteringPage.isClusterIdPresent(), "Cluster IP isn't present at Host Server section");
        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        // Cluster Members section
        List<String> clusterServers = clusteringPage.getServerNames();
        Assert.assertTrue(clusterServers.size() >= 2, "Number of the Cluster servers aren't displayed");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        Assert.assertTrue(clusterMembers.size() >= 2, "Number of the Cluster ips aren't displayed");

        List<String> clusterPorts = clusteringPage.getPorts();
        Assert.assertTrue(clusterPorts.size() >= 2, "Number of the Cluster ports aren't displayed");

        List<String> clusterRegs = clusteringPage.getLastRegs();
        Assert.assertTrue(clusterRegs.size() >= 2, "Number of the Cluster Last Registered aren't displayed");

        // Offline Cluster Members section
        List<String> clusterOffServers = clusteringPage.getOffServerNames();
        Assert.assertTrue(clusterOffServers.size() <= 1, "Number of the Cluster servers aren't displayed");

        List<String> clusterOffMembers = clusteringPage.getOffClusterIps();
        Assert.assertTrue(clusterOffMembers.size() <= 1, "Number of the Cluster ips aren't displayed");

        List<String> clusterOffPorts = clusteringPage.getOffPorts();
        Assert.assertTrue(clusterOffPorts.size() <= 1, "Number of the Cluster ports aren't displayed");

        List<String> clusterOffRegs = clusteringPage.getOffLastRegs();
        Assert.assertTrue(clusterOffRegs.size() <= 1, "Number of the Cluster Last Registered aren't displayed");

        // Connected Non-Clustered Server(s)
        List<String> clusterNonServers = clusteringPage.getNonServerNames();
        Assert.assertTrue(clusterNonServers.size() <= 1, "Number of the Cluster servers aren't displayed");

        List<String> clusterNonMembers = clusteringPage.getNonClusterIps();
        Assert.assertTrue(clusterNonMembers.size() <= 1, "Number of the Cluster ips aren't displayed");

        // Validate Cluster button
        Assert.assertTrue(clusteringPage.isValidateButtonPresent(), "Validate Cluster button isn't present");

        // Verify the description of the page
        Assert.assertTrue(clusteringPage.isAdditionalDescriptionLinkPresent(), "Additional description link isn't present");
        Assert.assertEquals(clusteringPage.getDescriptionText(), desc, "Description of the page isn't present");

        // Verify the description of each control
        Assert.assertEquals(clusteringPage.getIpDescriptionText(), ipDesc, "Description isn't present");
        Assert.assertEquals(clusteringPage.getServerDescriptionText(), serverDesc, "Description isn't present");

        // Compare the values of each field with JMX ones
        String serverName = clusteringPage.getServerNameText();
        String serverNameJMX = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "HostName").toString();
        Assert.assertEquals(serverName, serverNameJMX, "Values aren't identical");

        String ipAddress = clusteringPage.getIpAddressText();
        String ipAddressJMX = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "IPAddress").toString();
        Assert.assertEquals(ipAddress, ipAddressJMX, "Values aren't identical");

        String clusterId = clusteringPage.getClusterIdText();
        String clusterIdJMX = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusterName").toString();
        Assert.assertEquals(clusterId, clusterIdJMX, "Values aren't identical");

        int numberOfMemebers = clusterMembers.size();
        int numberOfMemebersJMX = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "NumClusterMembers").hashCode();
        Assert.assertEquals(numberOfMemebers, numberOfMemebersJMX, "Values aren't identical");

    }

    @Test(groups = {"EnterpriseOnly"}, dependsOnMethods = "AONE_576", alwaysRun = true)
    public void AONE_583() throws Exception
    {

        // Repository Server Clustering page is opened
        RepositoryServerClusteringPage clusteringPage = drone.getCurrentPage().render();

        // Click on Validate Cluster button
        ClusterValidationPage validationPage = clusteringPage.getValidationPage(drone).render();
        drone.switchToFrame("admin-dialog");

        Assert.assertEquals(validationPage.getTitle(), "Cluster Validation", "Title isn't present");
        Assert.assertTrue(validationPage.isCancelButtonPresent(), "Cancel button isn't present");

        List<String> succeedNodes = validationPage.getSucceedNodes();
        Assert.assertTrue(succeedNodes.size() >= 2, "Number of the Cluster nodes isn't correct");

        validationPage.clickClose();
        drone.switchToDefaultContent();


    }

    @Test(groups = {"EnterpriseOnly"}, dependsOnMethods = "AONE_583", alwaysRun = true)
    public void AONE_581() throws Exception
    {

        // Repository Server Clustering page is opened (node1)
        RepositoryServerClusteringPage clusteringPage = drone.getCurrentPage().render();

        // Verify the list of components of the page
        // Connected Host section
        Assert.assertTrue(clusteringPage.isServerNamePresent(), "Server name isn't present at Host Server section");
        Assert.assertTrue(clusteringPage.isIpAddressPresent(), "IP Address isn't present at Host Server section");
        Assert.assertTrue(clusteringPage.isClusterIdPresent(), "Cluster IP isn't present at Host Server section");
        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        // Compare the values of each field with JMX ones
        String serverName = clusteringPage.getServerNameText();
        String serverNameJMX = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "HostName").toString();
        Assert.assertEquals(serverName, serverNameJMX, "Values aren't identical");

        String ipAddress = clusteringPage.getIpAddressText();
        String ipAddressJMX = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "IPAddress").toString();
        Assert.assertEquals(ipAddress, ipAddressJMX, "Values aren't identical");

        String clusterId = clusteringPage.getClusterIdText();
        String clusterIdJMX = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "ClusterName").toString();
        Assert.assertEquals(clusterId, clusterIdJMX, "Values aren't identical");

        ShareUser.logout(drone);

        ShareUtil.navigateToSystemSummary(drone, node2Url, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        SystemSummaryPage sysSummaryPage = drone.getCurrentPage().render();
        clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        // Repository Server Clustering page is opened (node2)
        Assert.assertTrue(clusteringPage.isServerNamePresent(), "Server name isn't present at Host Server section");
        Assert.assertTrue(clusteringPage.isIpAddressPresent(), "IP Address isn't present at Host Server section");
        Assert.assertTrue(clusteringPage.isClusterIdPresent(), "Cluster IP isn't present at Host Server section");
        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        String serverName2 = clusteringPage.getServerNameText();
        String serverName2JMX = JmxUtils.getAlfrescoServerProperty(node2Url, "Alfresco:Name=Cluster,Tool=Admin", "HostName").toString();
        Assert.assertEquals(serverName2, serverName2JMX, "Values aren't identical");

        String ipAddress2 = clusteringPage.getIpAddressText();
        String ipAddress2JMX = JmxUtils.getAlfrescoServerProperty(node2Url, "Alfresco:Name=Cluster,Tool=Admin", "IPAddress").toString();
        Assert.assertEquals(ipAddress2, ipAddress2JMX, "Values aren't identical");

        String clusterId2 = clusteringPage.getClusterIdText();
        String clusterId2JMX = JmxUtils.getAlfrescoServerProperty(node2Url, "Alfresco:Name=Cluster,Tool=Admin", "ClusterName").toString();
        Assert.assertEquals(clusterId2, clusterId2JMX, "Values aren't identical");

        // Cluster ID should be same on both nodes
        Assert.assertEquals(clusterId, clusterId2, "Cluster IDs aren't the same");
    }

    @Test(groups = {"EnterpriseOnly"}, dependsOnMethods = "AONE_581", alwaysRun = true)
    public void AONE_578() throws Exception
    {

        // Repository Server Clustering page is opened (node2)
        RepositoryServerClusteringPage clusteringPage = drone.getCurrentPage().render();

        // Verify the list of components of the page
        // Cluster Members section
        List<String> clusterServers2 = clusteringPage.getServerNames();
        Assert.assertTrue(clusterServers2.size() >= 2, "Number of the Cluster servers aren't displayed");

        List<String> clusterMembers2 = clusteringPage.getClusterMembers();
        Assert.assertTrue(clusterMembers2.size() >= 2, "Number of the Cluster ips aren't displayed");

        List<String> clusterPorts2 = clusteringPage.getPorts();
        Assert.assertTrue(clusterPorts2.size() >= 2, "Number of the Cluster ports aren't displayed");

        List<String> clusterRegs2 = clusteringPage.getLastRegs();
        Assert.assertTrue(clusterRegs2.size() >= 2, "Number of the Cluster Last Registered aren't displayed");

        // Compare the values of each field with JMX ones    !!!!!!!!! check ports?
        String serverName2JMX = JmxUtils.getAlfrescoServerProperty(node2Url, "Alfresco:Name=Cluster,Tool=Admin", "HostName").toString();
        Assert.assertEquals(clusterServers2.get(1).compareToIgnoreCase(serverName2JMX), 0, "Values aren't identical");

        String ipAddress2JMX = JmxUtils.getAlfrescoServerProperty(node2Url, "Alfresco:Name=Cluster,Tool=Admin", "IPAddress").toString();
        Assert.assertEquals(clusterMembers2.get(1).compareToIgnoreCase(ipAddress2JMX), 0, "Values aren't identical");

        int numberOfMemebers2 = clusterMembers2.size();
        int numberOfMemebers2JMX = JmxUtils.getAlfrescoServerProperty(node2Url, "Alfresco:Name=Cluster,Tool=Admin", "NumClusterMembers").hashCode();
        Assert.assertEquals(numberOfMemebers2, numberOfMemebers2JMX, "Values aren't identical");

        ShareUser.logout(drone);

        // node1
        ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
        SystemSummaryPage sysSummaryPage = drone.getCurrentPage().render();
        clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();


        // Verify the list of components of the page
        // Cluster Members section
        List<String> clusterServers = clusteringPage.getServerNames();
        Assert.assertTrue(clusterServers.size() >= 2, "Number of the Cluster servers aren't displayed");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        Assert.assertTrue(clusterMembers.size() >= 2, "Number of the Cluster ips aren't displayed");

        List<String> clusterPorts = clusteringPage.getPorts();
        Assert.assertTrue(clusterPorts2.size() >= 2, "Number of the Cluster ports aren't displayed");

        List<String> clusterRegs = clusteringPage.getLastRegs();
        Assert.assertTrue(clusterRegs.size() >= 2, "Number of the Cluster Last Registered aren't displayed");

        // Compare the values of each field with JMX ones    !!!!!!!!! check ports?
        String serverNameJMX = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "HostName").toString();
        Assert.assertEquals(clusterServers.get(0).compareToIgnoreCase(serverNameJMX), 0, "Values aren't identical");

        String ipAddressJMX = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "IPAddress").toString();
        Assert.assertEquals(clusterMembers.get(0).compareToIgnoreCase(ipAddressJMX), 0, "Values aren't identical");

        int numberOfMemebers = clusterMembers.size();
        int numberOfMemebersJMX = JmxUtils.getAlfrescoServerProperty("Alfresco:Name=Cluster,Tool=Admin", "NumClusterMembers").hashCode();
        Assert.assertEquals(numberOfMemebers, numberOfMemebersJMX, "Values aren't identical");


    }

    @Test(groups = {"EnterpriseOnly"}, dependsOnMethods = "AONE_578", alwaysRun = true)
    public void AONE_580() throws Exception
    {

        String serverDB;

        setSshHost(node1Url);
        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Repository Server Clustering page is opened
        RepositoryServerClusteringPage clusteringPage = drone.getCurrentPage().render();

        // Verify the list of components of the page
        // Offline Cluster Members section
        List<String> clusterOffServers = clusteringPage.getOffServerNames();
        Assert.assertFalse(clusterOffServers.get(0).matches(regexUrl), "Section contains some information");

        List<String> clusterOffMembers = clusteringPage.getOffClusterIps();
        Assert.assertFalse(clusterOffMembers.get(0).matches(regexIp), "Section contains some information");

        List<String> clusterOffPorts = clusteringPage.getOffPorts();
        Assert.assertFalse(clusterOffPorts.get(0).matches(regexPort), "Section contains some information");

        List<String> clusterOffRegs = clusteringPage.getOffLastRegs();
        Assert.assertFalse(clusterOffRegs.get(0).matches(regexDate), "Section contains some information");

        // TODO NOTE: The test-case should be modified. The exact beans, attributes and properties should be specified.
        // Compare the values with JMX

        // Shutdown one node
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
            logger.info("Connection to jmx failed");
        }

        // Check the Cluster Members section
        List<String> clusterServers = clusteringPage.getServerNames();
        Assert.assertTrue(clusterServers.size() >= 2, "Number of the Cluster servers aren't displayed");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        Assert.assertTrue(clusterMembers.size() >= 2, "Number of the Cluster ips aren't displayed");

        List<String> clusterPorts = clusteringPage.getPorts();
        Assert.assertTrue(clusterPorts.size() >= 2, "Number of the Cluster ports aren't displayed");

        List<String> clusterRegs = clusteringPage.getLastRegs();
        Assert.assertTrue(clusterRegs.size() >= 2, "Number of the Cluster Last Registered aren't displayed");

        // Check the Offline Cluster Members section
        clusterOffServers = clusteringPage.getOffServerNames();
        Assert.assertTrue(clusterOffServers.get(0).matches(regexServer), "Section contains no information");

        clusterOffMembers = clusteringPage.getOffClusterIps();
        Assert.assertTrue(clusterOffMembers.get(0).matches(regexIp), "Section contains no information");

        clusterOffPorts = clusteringPage.getOffPorts();
        Assert.assertTrue(clusterOffPorts.get(0).matches(regexPort), "Section contains no information");

        clusterOffRegs = clusteringPage.getOffLastRegs();
        Assert.assertTrue(clusterOffRegs.get(0).matches(regexDate), "Section contains no information");

        // TODO NOTE: The test-case should be modified. The exact beans, attributes and properties should be specified.
        // Compare the values with JMX
    }

    @Test(groups = {"EnterpriseOnly"}, dependsOnMethods = "AONE_580", alwaysRun = true)
    public void AONE_582() throws Exception
    {

        // Repository Server Clustering page is opened    node1
        RepositoryServerClusteringPage clusteringPage = drone.getCurrentPage().render();

        // Click on Remove form List action for the node present in Offline Cluster Members section
        clusteringPage.clickRemove(drone);

        List<String> clusterOffServers = clusteringPage.getOffServerNames();
        Assert.assertFalse(clusterOffServers.get(0).matches(regexServer), "");

        List<String> clusterOffMembers = clusteringPage.getOffClusterIps();
        Assert.assertFalse(clusterOffMembers.get(0).matches(regexIp), "");

        List<String> clusterOffPorts = clusteringPage.getOffPorts();
        Assert.assertFalse(clusterOffPorts.get(0).matches(regexPort), "");

        List<String> clusterOffRegs = clusteringPage.getOffLastRegs();
        Assert.assertFalse(clusterOffRegs.get(0).matches(regexDate), "");

    }

    @Test(groups = {"EnterpriseOnly"}, dependsOnMethods = "AONE_582", alwaysRun = true)
    public void AONE_584() throws Exception
    {

        String clusterMemberOff = "There is only one node in the cluster. " +
                "Two or more nodes are required to test connectivity.";


        try {
            setSshHost(node1Url);
            dronePropertiesMap.get(drone).setShareUrl(node1Url);

            // Repository Server Clustering page is opened
            RepositoryServerClusteringPage clusteringPage = drone.getCurrentPage().render();

            // Click on Validate Cluster button
            ClusterValidationPage validationPage = clusteringPage.getValidationPage(drone).render();
            drone.switchToFrame("admin-dialog");

            Assert.assertEquals(validationPage.getTitle(), "Cluster Validation", "Title isn't present");
            Assert.assertTrue(validationPage.isCancelButtonPresent(), "Cancel button isn't present");

            Assert.assertEquals(validationPage.getValidationResult(), clusterMemberOff, "Data about validation result is wrong");

            validationPage.clickClose();
        }

        finally
        {
            RemoteUtil.removeIpTables(node1Url);
            ShareUser.logout(drone);
        }

    }

}
