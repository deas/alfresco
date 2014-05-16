package org.alfresco.po.share.systemsummary;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class AdvancedAdminConsolePagesTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(AdvancedAdminConsolePagesTest.class);

    @Test(groups = "Enterprise-only")
    public void openConsolePageTest()
    {
        SystemSummaryPage sysSummaryPage = null;
        try {
            sysSummaryPage = (SystemSummaryPage) ShareUtil.navigateToSystemSummary(drone, shareUrl, username, password);
        } catch (Exception e) {
            if (logger.isDebugEnabled())
            {
                logger.debug("Following exception was occurred" + e);
            }
        }
        Assert.notNull(sysSummaryPage, "Expected page not opened. Navigate to SystemSummary page is failed");
        RepositoryServerClusteringPage repositoryServerClusteringPage;
        repositoryServerClusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering);
        Assert.notNull(repositoryServerClusteringPage, "Expected page not opened. Navigate to Repository Server Clustering page is failed");
    }
}
