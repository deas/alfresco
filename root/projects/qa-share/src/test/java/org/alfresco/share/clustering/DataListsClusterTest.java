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

package org.alfresco.share.clustering;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.datalist.lists.ContactList;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static org.alfresco.po.share.enums.DataLists.CONTACT_LIST;
import static org.alfresco.po.share.enums.DataLists.TO_DO_LIST;
import static org.testng.Assert.*;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class DataListsClusterTest extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(DataListsClusterTest.class);
    private static String node1Url;
    private static String node2Url;
    private String testUser;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

    protected String siteName = "";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();

        testUser = getUserNameFreeDomain(testName);
        logger.info("Starting Tests: " + testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

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

    /**
     * Test - AONE_9139: Adding New list (To Do list)
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Admin is logged in to the Share at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Click on Data lists link on nodeA</li>
     * <li>Data list is created</li>
     * <li>It is displayed in the "Lists" column on both nodes (nodeA and nodeB)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9139() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String dataListTitle = "dataList_" + getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Data lists opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DATA_LISTS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Browse to Data Lists
        siteDashPage.getSiteNav().selectDataListPage();
        DataListPage dataListPage = new DataListPage(drone).render();
        dataListPage = dataListPage.createDataList(CONTACT_LIST, dataListTitle, testName).render();

        // verify that created Data list is displayed at the server A
        assertTrue(dataListPage.isEditDataListDisplayed(dataListTitle), "Data list " + dataListTitle + " isn't displayed");

        ShareUser.logout(drone);

        // verify that created Data list is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Browse to Data Lists
        siteDashPage.getSiteNav().selectDataListPage();
        dataListPage = drone.getCurrentPage().render();

        // verify that created Data list is displayed at the server B
        assertTrue(dataListPage.isEditDataListDisplayed(dataListTitle), "Data list " + dataListTitle + " isn't displayed");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9140: Edit list details action
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Admin is logged in to the Share at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Click on Data lists link on nodeA</li>
     * <li>Create and edit Data list</li>
     * <li>It is displayed in the "Lists" column on both nodes (nodeA and nodeB)</li>
     * <li>Information is saved and changed correctly on both nodes</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9140() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String dataListTitle = "dataList_" + getRandomString(5);
        String newTitle = "title_" + getRandomString(5);
        String newDescription = "description_" + getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Data lists opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DATA_LISTS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Browse to Data Lists
        siteDashPage.getSiteNav().selectDataListPage();
        DataListPage dataListPage = new DataListPage(drone).render();
        dataListPage = dataListPage.createDataList(TO_DO_LIST, dataListTitle, testName).render();

        // verify that created Data list is displayed at the server A
        assertTrue(dataListPage.isEditDataListDisplayed(dataListTitle), "Data list " + dataListTitle + " isn't displayed");

        // Edit own data list
        dataListPage = dataListPage.editDataList(dataListTitle, newTitle, newDescription);
        // verify that edited Data list is displayed at the server A
        assertNotNull(dataListPage.getDataListDirectoryInfo(newTitle), "Data list '" + newTitle + "' isn't displayed");

        assertTrue(dataListPage.getDataListDescription(newTitle).contains(newDescription), "Data list '" + newTitle + "' description isn't displayed");

        ShareUser.logout(drone);

        // verify that created Data list is displayed at the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Browse to Data Lists
        siteDashPage.getSiteNav().selectDataListPage();
        dataListPage = drone.getCurrentPage().render();

        // verify that edited Data list is displayed at the server B
        assertTrue(dataListPage.isEditDataListDisplayed(newTitle), "Data list '" + newTitle + "' isn't displayed");

        assertTrue(dataListPage.getDataListDescription(newTitle).contains(newDescription), "Data list '" + newTitle + "' description isn't displayed");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9141: Delete list button
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Admin is logged in to the Share at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Click on Data lists link on nodeA</li>
     * <li>Click Delete list button</li>
     * <li>Click Delete button</li>
     * <li>List is deleted on both nodes (nodeA and nodeB)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9141() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String dataListTitle = "dataList_" + getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Data lists opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DATA_LISTS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Browse to Data Lists
        siteDashPage.getSiteNav().selectDataListPage();
        DataListPage dataListPage = new DataListPage(drone).render();
        dataListPage = dataListPage.createDataList(TO_DO_LIST, dataListTitle, testName).render();

        // verify that Data list is deleted on the server A
        assertTrue(dataListPage.isEditDataListDisplayed(dataListTitle), "Data list " + dataListTitle + " isn't displayed");
        // Delete collaborator's data list
        dataListPage.deleteDataListWithConfirm(dataListTitle).render();
        dataListPage = drone.getCurrentPage().render();

        Assert.assertTrue(dataListPage.isNoListFoundDisplayed(), "Information 'No Lists Found' is absent. Server A.");

        ShareUser.logout(drone);

        // verify that Data list is deleted on the server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);
        siteDashPage.getSiteNav().selectDataListPage();

        // Browse to Data Lists
        dataListPage = dataListPage.clickCancel();

        Assert.assertTrue(dataListPage.isNoListFoundDisplayed(), "Information 'No Lists Found' is absent. Server B.");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9142:Adding New Row
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Admin is logged in to the Share at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Click on Data lists link on nodeA</li>
     * <li>Create Data list</li>
     * <li>Verify 'Create New item' window</li>
     * <li>New Row is created and displayed in the row table</li>
     * <li>Only title column is not blank on both nodes (nodeA and nodeB)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9142() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String dataListTitle = "dl_title_" + getRandomString(5);
        String dataListDescription = "dl_description_" + getRandomString(5);
        String contactListName = "contactList_" + getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Data lists opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DATA_LISTS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Browse to Data Lists
        siteDashPage.getSiteNav().selectDataListPage();
        DataListPage dataListPage = new DataListPage(drone).render();
        dataListPage.createDataList(CONTACT_LIST, dataListTitle, dataListDescription).render();

        // Item creation
        ContactList contactList = new ContactList(drone);
        dataListPage.selectDataList(dataListTitle);
        contactList = contactList.createItem(contactListName);

        // Verify New Row is created and displayed in the row table. Server A
        assertTrue(contactList.isItemDisplayed(contactListName), "New Row isn't displayed in the row table. Server A");

        ShareUser.logout(drone);

        // Verify New Row is created and displayed in the row table. Server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Browse to Data Lists
        siteDashPage.getSiteNav().selectDataListPage();
        dataListPage = drone.getCurrentPage().render();

        dataListPage.selectDataList(dataListTitle);

        // Verify New Row is created and displayed in the row table. Server B
        assertTrue(contactList.isItemDisplayed(contactListName), "New Row isn't displayed in the row table. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9143:Edit action
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Admin is logged in to the Share at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Click on Data lists link on nodeA</li>
     * <li>Create Data list</li>
     * <li>Verify 'Create New item' window</li>
     * <li>New Row is created and displayed in the row table</li>
     * <li>Edit new row</li>
     * <li>Changes are saved</li>
     * <li>Row information is changed on both nodes (nodeA and nodeB)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9143() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String dataListTitle = "dl_title_" + getRandomString(5);
        String dataListDescription = "dl_description_" + getRandomString(5);
        String contactListName = "contactList_" + getRandomString(5);
        String newContactListName = "new_contactList_" + getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Data lists opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DATA_LISTS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Browse to Data Lists
        siteDashPage.getSiteNav().selectDataListPage();
        DataListPage dataListPage = new DataListPage(drone).render();
        dataListPage.createDataList(CONTACT_LIST, dataListTitle, dataListDescription).render();

        // Item creation
        ContactList contactList = new ContactList(drone);
        dataListPage.selectDataList(dataListTitle);
        contactList = contactList.createItem(contactListName);

        // Verify New Row is created and displayed in the row table. Server A
        assertTrue(contactList.isItemDisplayed(contactListName), "New Row isn't displayed in the row table. Server A");

        // Edit own item of the list
        contactList.editItem(contactListName, newContactListName);

        // Verify changes are saved, row information is changed on Server A
        assertTrue(contactList.isItemDisplayed(newContactListName), "Row information is changed on Server A");
        assertFalse(contactList.isItemDisplayed(contactListName), "Old Row is displayed in the row table. Server A");

        ShareUser.logout(drone);

        // Verify changes are saved, row information is changed on Server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Browse to Data Lists
        siteDashPage.getSiteNav().selectDataListPage();
        dataListPage = drone.getCurrentPage().render();

        dataListPage.selectDataList(dataListTitle);

        // Verify changes are saved, row information is changed on Server B
        assertTrue(contactList.isItemDisplayed(newContactListName), "Row information is changed on Server B");
        assertFalse(contactList.isItemDisplayed(contactListName), "Old Row is displayed in the row table. Server B");

        ShareUser.logout(drone);
    }

    /**
     * Test - AONE_9144:Delete action
     * <ul>
     * <li>2 servers are working in cluster</li>
     * <li>Admin is logged in to the Share at server A</li>
     * <li>Site1 is created by admin</li>
     * <li>Click on Data lists link on nodeA</li>
     * <li>Create Data list</li>
     * <li>Verify 'Create New item' window</li>
     * <li>New Row is created and displayed in the row table</li>
     * <li>Delete new row</li>
     * <li>Item is successfully deleted on both nodes (nodeA and nodeB)</li>
     * </ul>
     */
    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9144() throws Exception
    {
        String siteName = getSiteName(getTestName() + System.currentTimeMillis());
        String dataListTitle = "dl_title_" + getRandomString(5);
        String dataListDescription = "dl_description_" + getRandomString(5);
        String contactListName = "contactList_" + getRandomString(5);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Any site is created
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Data lists opened
        ShareUserDashboard.addPageToSite(drone, siteName, SitePageType.DATA_LISTS);

        SiteDashboardPage siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Browse to Data Lists
        siteDashPage.getSiteNav().selectDataListPage();
        DataListPage dataListPage = new DataListPage(drone).render();
        dataListPage.createDataList(CONTACT_LIST, dataListTitle, dataListDescription).render();

        // Item creation
        ContactList contactList = new ContactList(drone);
        dataListPage.selectDataList(dataListTitle);
        contactList = contactList.createItem(contactListName);

        // Verify New Row is created and displayed in the row table. Server A
        assertTrue(contactList.isItemDisplayed(contactListName), "New Row isn't displayed in the row table. Server A");

        // Edit own item of the list
        contactList.deleteAnItemWithConfirm(contactListName);

        // Verify Item is successfully deleted on Server A
        assertFalse(contactList.isItemDisplayed(contactListName), "Old Row is displayed in the row table. Server A");

        ShareUser.logout(drone);

        // Verify changes are saved, row information is changed on Server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        siteDashPage = ShareUser.openSiteDashboard(drone, siteName);

        // Browse to Data Lists
        siteDashPage.getSiteNav().selectDataListPage();
        dataListPage = drone.getCurrentPage().render();

        dataListPage.selectDataList(dataListTitle);

        // Verify Item is successfully deleted on Server B
        assertFalse(contactList.isItemDisplayed(contactListName), "Old Row is displayed in the row table. Server B");

        ShareUser.logout(drone);
    }
}