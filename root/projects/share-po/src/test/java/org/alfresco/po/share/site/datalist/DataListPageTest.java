package org.alfresco.po.share.site.datalist;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.datalist.lists.ContactList;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.util.SiteUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Holds tests for Data Lists web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class DataListPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    DataListPage dataListPage = null;
    NewListForm newListForm;
    ContactList contactList;
    String text = getClass().getSimpleName();

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

    @Test(groups = "Enterprise-only")
    public void addDataListPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.DATA_LISTS);
        customizeSitePage.addPages(addPageTypes);
        dataListPage = siteDashBoard.getSiteNav().selectDataListPage().render();
        newListForm = new NewListForm(drone);
        dataListPage = newListForm.clickCancel();
        assertNotNull(dataListPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "addDataListPage")
    public void createContactDataList()
    {
        assertTrue(dataListPage.isNewListEnabled());
        dataListPage = dataListPage.createDataList(NewListForm.TypeOptions.CONTACT_LIST, text, text);
        assertNotNull(dataListPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "createContactDataList")
    public void createItem()
    {
        dataListPage.selectDataList(text);
        contactList = new ContactList(drone).createItem(text).render();
        assertNotNull(contactList);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "createItem")
    public void duplicateItems()
    {
        assertTrue(contactList.isDuplicateDisplayed(text));
        dataListPage.duplicateAnItem(text);
        assertEquals(getTheNumOfItems(), 2);
    }

    private int getTheNumOfItems()
    {
        List<WebElement> numOfItems = drone.findAll(By.cssSelector("tbody[class$='data']>tr"));
        return numOfItems.size();
    }
}
