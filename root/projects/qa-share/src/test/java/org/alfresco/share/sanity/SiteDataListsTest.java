package org.alfresco.share.sanity;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.datalist.DataListTreeMenuNavigation;
import org.alfresco.po.share.site.datalist.NewListForm;
import org.alfresco.po.share.site.datalist.lists.ContactList;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ActivityType;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.po.share.enums.DataLists.*;
import static org.alfresco.po.share.site.datalist.DataListPage.selectOptions.*;
import static org.alfresco.po.share.site.datalist.DataListPage.selectedItemsOptions.*;
import static org.alfresco.po.share.site.datalist.DataListTreeMenuNavigation.ListsMenu.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This class includes Site Data Lists Sanity tests
 *
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
public class SiteDataListsTest extends AbstractUtils
{
    private static final Log logger = LogFactory.getLog(SiteDataListsTest.class);

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
        username = "user";
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_AONE_8236() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);

        //Any user is created
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

        //Any site is created
        ShareUser.login(drone, testUser);
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        //The user is logged in
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();

        //Add Data List page
        List<SitePageType> pagesToAdd = new ArrayList<>();
        pagesToAdd.add(SitePageType.DATA_LISTS);
        customizeSitePage.addPages(pagesToAdd).render();
    }

    /**
     * Check Data Lists activities in the site
     */
    @Test(groups = "Sanity")
    public void AONE_8236() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String editedItem = testName + "edited";
        String activityEntry = "";

        //The user is logged in
        ShareUser.login(drone, testUser);
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName);

        //Data Lists page is opened
        dashboard.getSiteNav().selectDataListPage().render();
        DataListPage dataListPage = new DataListPage(drone).render();

        //Select any Data list type and create a data list
        int expListCount = dataListPage.getListsCount()+1;
        dataListPage.createDataList(CONTACT_LIST, testName, testName).render();
        assertEquals(dataListPage.getListsCount(), expListCount, "Data List wasn't created");

        //Edit the data list
        dataListPage.editDataList(testName, editedItem, editedItem).render();
        assertTrue(dataListPage.getLists().contains(editedItem), "Data List wasn't edited");

        //Add new item to data list
        dataListPage.selectDataList(editedItem);
        ContactList contactList = new ContactList(drone);
        contactList.createItem(testName).render();

        //Verify new item is added
        assertTrue(contactList.isItemDisplayed(testName), "Item isn't available");

        //Edit the added item
        contactList.editItem(testName, editedItem).render();

        //Verify the item is edited
        assertTrue(contactList.isItemDisplayed(editedItem), "Item wasn't edited");

        //Duplicate the added item
        int expCount = contactList.getItemsCount()+1;
        contactList.duplicateAnItem(editedItem);

        //Verify the item was duplicated
        assertEquals(contactList.getItemsCount(), expCount, "The item wasn't duplicated");

        //Delete the added item
        contactList.deleteAnItemWithConfirm(editedItem);

        //Verify the item was deleted
        assertEquals(contactList.getItemsCount(), expCount-1, "The item wasn't deleted");

        //Delete the created data list
        dataListPage.deleteDataListWithConfirm(editedItem).render();

        //Verify the list was deleted
        assertEquals(dataListPage.getListsCount(), expListCount-1, "The list wasn't deleted");
        if(drone.getCurrentPage() instanceof NewListForm)
        {
            NewListForm newListForm = new NewListForm(drone);
            newListForm.clickClose();
        }

        //Go to Site Dashboard activities and ensure all activities are displayed
        ShareUser.openSiteDashboard(drone, siteName).render();

        String[] entries = { FEED_CONTENT_CREATED, FEED_CONTENT_UPDATED, FEED_CONTENT_DELETED };
        String [] itemNames = { testName, editedItem, editedItem };

        for (int i = 0; i < entries.length; i++)
        {
            activityEntry = testUser + " " + DEFAULT_LASTNAME + entries[i] + FEED_FOR_DATA_LIST + itemNames [i] + " (" + CONTACT_LIST.getListName() + ")";
            Boolean entryFound = ShareUser.searchSiteDashBoardWithRetry(drone, SITE_ACTIVITIES, activityEntry, true, siteName, ActivityType.DESCRIPTION);
            assertTrue(entryFound, "Unable to find " + activityEntry);
        }

        //Go to My Dashboard activities and ensure all activities are displayed
        ShareUser.openUserDashboard(drone).render();

        for (int i = 0; i < entries.length; i++)
        {
            activityEntry = testUser + " " + DEFAULT_LASTNAME + entries[i] + FEED_FOR_DATA_LIST + itemNames [i] + " (" + CONTACT_LIST.getListName() + ")"
                + FEED_LOCATION + siteName;
            Boolean entryFound = ShareUser.searchMyDashBoardWithRetry(drone, DASHLET_ACTIVITIES, activityEntry, true);
            assertTrue(entryFound, "Unable to find " + activityEntry);
        }
    }

    @Test(groups = "DataPrepSanity")
    public void dataPrep_AONE_8237() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName)+"1";
        String testUser2 = getUserNameFreeDomain(testName)+"2";
        String siteName = getSiteName(testName);
        String [] items = {"user1_item", "user2_item"};

        //At least 2 users are created
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser2);

        //Any site is created
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);
        SiteDashboardPage siteDashboardPage = ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        //user1 - manager, user2 - at least contributor
        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser1, siteName, UserRole.MANAGER);
        ShareUserMembers.inviteUserToSiteWithRole(drone, ADMIN_USERNAME, testUser2, siteName, UserRole.CONTRIBUTOR);

        //Any datalist is created
        ShareUser.openSiteDashboard(drone, siteName).render();
        CustomizeSitePage customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite();

        List<SitePageType> pagesToAdd = new ArrayList<>();
        pagesToAdd.add(SitePageType.DATA_LISTS);
        customizeSitePage.addPages(pagesToAdd).render();

        siteDashboardPage.getSiteNav().selectDataListPage().render();
        DataListPage dataListPage = new DataListPage(drone);
        dataListPage.createDataList(CONTACT_LIST, testName, testName);

        //At least one item is created by each user
        ShareUser.login(drone, testUser1);
        dataListPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectDataListPage().render();
        dataListPage.selectDataList(testName);
        ContactList contactList = new ContactList(drone);
        contactList.createItem(items[0]).render();

        ShareUser.login(drone, testUser2).render();
        dataListPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectDataListPage().render();
        dataListPage.selectDataList(testName);
        contactList.createItem(items[1]).render();
    }

    /**
     * Check Data Lists items actions in the site
     */
    @Test(groups = "Sanity")
    public void AONE_8237() throws Exception
    {
        String testName = getTestName();
        String testUser1 = getUserNameFreeDomain(testName)+"1";
        String siteName = getSiteName(testName);
        String [] items = {"user1_item", "user2_item"};

        //User1 is logged in
        ShareUser.login(drone, testUser1).render();

        //Data lists is opened. The data list is selected
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(drone, siteName).render();
        DataListPage dataListPage = siteDashboardPage.getSiteNav().selectDataListPage().render();
        dataListPage.selectDataList(testName);

        //Click All in Items section
        DataListTreeMenuNavigation dataListTreeMenuNavigation = dataListPage.getLeftMenus().render();
        dataListTreeMenuNavigation.selectListNode(ALL).render();
        ContactList contactList = new ContactList(drone).render();

        //Verify that All the items are listed
        assertEquals(contactList.getItemsCount(), 2, "Incorrect count of items");
        assertTrue(contactList.isItemDisplayed(items[0]) && contactList.isItemDisplayed(items[1]), "Not ALL items are available");

        //Click Recently Added in Items section
        dataListTreeMenuNavigation.selectListNode(RECENTLY_ADDED).render();

        //Only recently added items are listed
        assertEquals(contactList.getItemsCount(), 2, "Incorrect count of items");
        assertTrue(contactList.isItemDisplayed(items[0]) && contactList.isItemDisplayed(items[1]), "Not ALL items are available");

        //Click Recently Modified in Items section
        dataListTreeMenuNavigation.selectListNode(RECENTLY_MODIFIED).render();

        //Only recently modified items are listed
        assertEquals(contactList.getItemsCount(), 2, "Incorrect count of items");
        assertTrue(contactList.isItemDisplayed(items[0]) && contactList.isItemDisplayed(items[1]), "Not ALL items are available");

        //Click Created by Me in Items section
        dataListTreeMenuNavigation.selectListNode(CREATED_BY_ME).render();

        if(contactList.getItemsCount()>1)
        {
            refreshSharePage(drone).render();
        }

        //Verify Only items created by the current user are displayed
        assertEquals(contactList.getItemsCount(), 1, "Incorrect count of items");
        assertTrue(contactList.isItemDisplayed(items[0]), items[0] + "isn't available");

        dataListTreeMenuNavigation.selectListNode(ALL).render();
        //Click Select > All
        dataListPage.select(SELECT_ALL).render();

        //All the items are selected
        assertTrue(contactList.isCheckBoxSelected(items[0]) && contactList.isCheckBoxSelected(items[1]), "Not all the items are selected");

        //Click Select > None
        dataListPage.select(SELECT_NONE).render();

        //None item is selected
        assertFalse(contactList.isCheckBoxSelected(items[0]) && contactList.isCheckBoxSelected(items[1]), "Some items are selected");

        //Select several items and click Select > Invert Selection
        contactList.selectAnItem(items[0]);
        dataListPage.select(INVERT_SELECT).render();

        //The selection is inverted
        assertTrue(contactList.isCheckBoxSelected(items[1]));
        assertFalse(contactList.isCheckBoxSelected(items[0]));

        //Click Selected Items > Duplicate
        dataListPage.chooseSelectedItemOpt(DUPLICATE).render();

        //The selected items are duplicated
        assertEquals(contactList.getItemsCount(), 3, "The item wasn't duplicated");

        //Click Selected Items > Delete. Confirm deletion
        int expCount = contactList.getItemsCount()-1;
        dataListPage.chooseSelectedItemOpt(DELETE).render();
        contactList.confirmDelete();

        //The selected items are deleted
        assertEquals(contactList.getItemsCount(), expCount, "The item wasn't deleted");

        //Click Selected Items > Deselect All
        dataListPage.chooseSelectedItemOpt(DESELECT_ALL).render();

        //None items are selected
        assertFalse(contactList.isCheckBoxSelected(items[0]) && contactList.isCheckBoxSelected(items[1]), "Some items are selected");
    }
}
