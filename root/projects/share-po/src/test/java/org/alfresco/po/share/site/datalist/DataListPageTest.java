package org.alfresco.po.share.site.datalist;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.datalist.lists.ContactList;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Holds tests for Data Lists web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class DataListPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    DataListPage dataListPage = null;
    NewListForm newListForm;
    ContactList contactList;
    String text = getClass().getSimpleName();
    String editedText = text + "edited";

    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "datalist" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void addDataListPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.DATA_LISTS);
        customizeSitePage.addPages(addPageTypes);
        newListForm = (NewListForm)siteDashBoard.getSiteNav().selectDataListPage();
        dataListPage = newListForm.clickCancel();
        assertNotNull(dataListPage);
    }

    @Test(dependsOnMethods = "addDataListPage")
    public void createContactDataList()
    {
        assertTrue(dataListPage.isNewListEnabled());
        dataListPage = dataListPage.createDataList(NewListForm.TypeOptions.CONTACT_LIST, text, text);
        assertNotNull(dataListPage);
    }

    @Test(dependsOnMethods = "createContactDataList")
    public void createItem()
    {
        dataListPage.selectDataList(text);
        contactList = new ContactList(drone).createItem(text).render();
        assertNotNull(contactList);
    }

    @Test(dependsOnMethods = "createItem")
    public void duplicateItems()
    {
        assertTrue(contactList.isDuplicateDisplayed(text));
        dataListPage.duplicateAnItem(text);
        assertEquals(contactList.getItemsCount(), 2);
    }

    @Test(dependsOnMethods = "createContactDataList")
    public void editDataList()
    {
        dataListPage.editDataList(text, editedText, editedText);
        dataListPage.selectDataList(editedText);
        assertNotNull(dataListPage);
    }

    @Test(dependsOnMethods = "duplicateItems")
    public void editAnItem()
    {
        assertTrue(contactList.isEditDisplayed(text));
        contactList.editItem(text, editedText);
        assertNotNull(dataListPage);
    }

    @Test(dependsOnMethods = "editAnItem")
    public void deleteItem ()
    {
        int expNum = contactList.getItemsCount();
        contactList.deleteAnItemWithConfirm(editedText);
        assertEquals(contactList.getItemsCount(), expNum-1);
    }

    @Test(dependsOnMethods = "deleteItem")
    public void deleteList ()
    {
        int expNum = dataListPage.getListsCount();
        dataListPage.deleteDataListWithConfirm(editedText);
        assertEquals(dataListPage.getListsCount(), expNum-1);
    }
}
