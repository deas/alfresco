package org.alfresco.po.share.admin;

import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.SiteVisibility;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The Class ManageSitesPageTest.
 * 
 * @author Richard Smith
 */
public class ManageSitesPageTest extends AbstractTest
{

    private DashBoardPage dashBoardPage;
    private ManageSitesPage manageSitesPage;

    /**
     * Setup.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public void setup() throws Exception
    {
        dashBoardPage = loginAs(username, password);
        manageSitesPage = dashBoardPage.getNav().selectManageSites().render();
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    @Test(expectedExceptions={IllegalArgumentException.class})
    public void constructor()
    {
        ManageSitesPage manageSitesPageNull = new ManageSitesPage(null);
    }

    /**
     * Test the manage sites page has some sites.
     */
    @Test
    public void testHasSites()
    {
        List<ManagedSiteRow> rows = manageSitesPage.getManagedSiteRows();
        Assert.assertTrue(rows.size() > 0);
        Assert.assertTrue(StringUtils.isNotEmpty(rows.get(0).getSiteName()));
    }

    /**
     * Test the manage sites page has pagination.
     */
    @Test
    public void testHasPagination()
    {
        Assert.assertNotNull(manageSitesPage.getPaginator());
    }

    /**
     * Test to find a bootstrapped site by name.
     */
    @Test
    public void testFindBootstrappedSite()
    {
        // TODO: Bootstrap site data
        // TODO: Find a site we have actually bootstrapped
//        String testSiteName = "WAT1";
//        ManagedSiteRow wat1 = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testSiteName);
//        Assert.assertNotNull(wat1);
    }

    /**
     * Test to modify the visibility of a bootstrapped site.
     */
    @Test
    public void testModifyVisibilityOfBootstrappedSite()
    {
        // TODO: Bootstrap site data
        // TODO: Find a site we have actually bootstrapped
//        String testSiteName = "WAT20";
//        ManagedSiteRow wat20 = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testSiteName);
//        Assert.assertNotNull(wat20);
//        wat20.getVisibility().selectValue(SiteVisibility.PRIVATE);
//        wat20 = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testSiteName);
//        Assert.assertEquals(wat20.getVisibility().getValue(), SiteVisibility.PRIVATE.getDisplayValue());
    }

    /**
     * Test to delete a bootstrapped site.
     */
    @Test
    public void testDeleteOfBootstrappedSite()
    {
        // TODO: Bootstrap site data
        // TODO: Find a site we have actually bootstrapped
//        String testSiteName = "WAT22";
//        ManagedSiteRow wat22 = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testSiteName);
//        Assert.assertNotNull(wat22);
//        wat22.getActions().clickActionByNameAndDialogByButtonName("Delete Site", "OK");
//        wat22 = manageSitesPage.findManagedSiteRowByNameFromPaginatedResults(testSiteName);
//        Assert.assertNull(wat22);
    }
}