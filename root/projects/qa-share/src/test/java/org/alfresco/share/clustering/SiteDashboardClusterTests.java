package org.alfresco.share.clustering;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteNoticeDashlet;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by maryia.zaichanka on 6/27/14.
 */
public class SiteDashboardClusterTests extends AbstractUtils

{
    private static Log logger = LogFactory.getLog(SiteDashboardClusterTests.class);
    private static String node1Url;
    private static String node2Url;
    private String testUser;
    private static final String regexUrl = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";

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

    @Test(groups = { "EnterpriseOnly" })
    public void AONE_9132() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName + 1);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login as admin at server A
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Create site at server A
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC).render();

        // Open Site dashboard
        SiteDashboardPage siteDashBoard = drone.getCurrentPage().render();

        // Click Customise Dashboard on the node A
        CustomiseSiteDashboardPage customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render(maxWaitTime);
        customiseSiteDashBoard.remove(Dashlets.SITE_ACTIVITIES);

        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render(maxWaitTime);
        List<String> dashkets = customiseSiteDashBoard.getDashletNamesFrom(2);
        Assert.assertFalse(dashkets.contains(Dashlets.SITE_ACTIVITIES), "Dashlet isn't deleted");

        // Add several dashlets
        customiseSiteDashBoard.addDashlet(Dashlets.SITE_SEARCH, 1).render();
        dashkets = customiseSiteDashBoard.getDashletNamesFrom(1);
        Assert.assertFalse(dashkets.contains(Dashlets.SITE_SEARCH), "Dashlet isn't added");

        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render(maxWaitTime);
        customiseSiteDashBoard.addDashlet(Dashlets.SITE_NOTICE, 2).render();
        dashkets = customiseSiteDashBoard.getDashletNamesFrom(2);
        Assert.assertFalse(dashkets.contains(Dashlets.SITE_NOTICE), "Dashlet isn't added");

        ShareUser.logout(drone);

        // Log in at server B
        dronePropertiesMap.get(drone).setShareUrl(node2Url);
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Open Site Dahsboard
        siteDashBoard = ShareUser.openSiteDashboard(drone, siteName).render();

        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        Assert.assertNotNull(siteDashBoard, "Dashlet isn't present");
        Assert.assertEquals(searchDashlet.getTitle(), Dashlets.SITE_SEARCH.getDashletName());

        SiteNoticeDashlet siteNoticeDashlet = ShareUserDashboard.getSiteContentDashlet(drone, siteName);
        Assert.assertNotNull(siteNoticeDashlet, "Dashlet isn't present");
        Assert.assertEquals(siteNoticeDashlet.getTitle(), Dashlets.SITE_NOTICE.getDashletName());

        List<String> dashletTitles = siteDashBoard.getTitlesList();
        Assert.assertFalse(dashletTitles.contains("Site Activities"), "Dashlet isn't deleted");
        ShareUser.logout(drone);

    }

}
