package org.alfresco.share.search;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.search.FacetedSearchConfigPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The Class FacetedSearchConfigPageTest.
 *
 * This tests the Faceted Search Config page behaves as one would expect.
 *
 * @author Richard Smith
 */
public class FacetedSearchConfigPageTest extends AbstractUtils
{

    /** The logger. */
    private static Log logger = LogFactory.getLog(FacetedSearchConfigPageTest.class);

    /** Constants */
    private DashBoardPage dashBoardPage;
    private FacetedSearchConfigPage facetedSearchConfigPage;

    /* (non-Javadoc)
     * @see org.alfresco.share.util.AbstractUtils#setup()
     */
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        trace("Starting setup");

        super.setup();

        // Login as admin
        ShareUser.login(drone, ADMIN_USERNAME, ADMIN_PASSWORD);

        // Navigate to the faceted search page
        dashBoardPage = ShareUser.selectMyDashBoard(drone);
        facetedSearchConfigPage = dashBoardPage.getNav().getFacetedSearchConfigPage().render();

        trace("Setup complete");
    }

    /**
     * Render test.
     *
     * @throws Exception
     */
    @Test
    public void renderFacetedSearchConfigTest() throws Exception
    {
        trace("Starting renderFacetedSearchConfigTest");

        // Page should have a title
        Assert.assertTrue(StringUtils.isNotEmpty(facetedSearchConfigPage.getPageTitle()), "The faceted search config page should have a title");

        // Page should have some filters on it
        Assert.assertTrue(facetedSearchConfigPage.getFilters().size() > 0, "The faceted search config page should have a title");
        
        trace("renderFacetedSearchConfigTest complete");
    }

    /* (non-Javadoc)
     * @see org.alfresco.share.util.AbstractUtils#tearDown()
     */
    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        trace("Starting tearDown");

        // Logout
        ShareUser.logout(drone);

        super.tearDown();

        trace("TearDown complete");
    }

    /**
     * Trace.
     *
     * @param msg the msg
     */
    private void trace(String msg)
    {
        if(logger.isTraceEnabled())
        {
            logger.trace(msg);
        }
    }
}