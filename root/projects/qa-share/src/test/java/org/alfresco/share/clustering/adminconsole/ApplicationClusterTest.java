package org.alfresco.share.clustering.adminconsole;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ApplicationPageUtil;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * Created by maryia.zaichanka on 5/25/14.
 */
public class ApplicationClusterTest extends AbstractUtils

{
    private static Log logger = LogFactory.getLog(ApplicationClusterTest.class);
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
    public void AONE_9205() throws Exception
    {
        testName = getTestName();
        testUser = getUserNameFreeDomain(testName);

        dronePropertiesMap.get(drone).setShareUrl(node1Url);

        // Login as admin at server A
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // get all themes
        AdminConsolePage.ThemeType[] themes = AdminConsolePage.ThemeType.values();

        // Open Admin console
        for (AdminConsolePage.ThemeType theme : themes)
        {
            // navigate to Admin Console page (Application)
            AdminConsolePage adminConsolePage = ApplicationPageUtil.openApplicationPage(drone);

            // change the Theme
            adminConsolePage.selectTheme(theme).render(maxWaitTime);
            assertTrue(adminConsolePage.isThemeSelected(theme), "New theme isn't applied");
            ShareUser.logout(drone);

            // Log in at server B
            dronePropertiesMap.get(drone).setShareUrl(node2Url);
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

            // go to My Dashboard and verify changes are applied
            DashBoardPage dashBoardPage = ShareUser.openUserDashboard(drone).render();
            String color = dashBoardPage.getColor(ShareUserDashboard.CREATE_SITE_BUTTON, false);
            assertTrue(color.equals(theme.hexTextColor), "New theme isn't applied");
            ShareUser.logout(drone);

            dronePropertiesMap.get(drone).setShareUrl(node1Url);
            ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        }
        ShareUser.logout(drone);

    }



}
