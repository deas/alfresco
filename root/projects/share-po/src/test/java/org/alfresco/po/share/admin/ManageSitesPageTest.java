package org.alfresco.po.share.admin;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The Class ManageSitesPageTest.
 */
public class ManageSitesPageTest extends AbstractTest
{
    
    /**
     * Setup.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public void setup() throws Exception
    {
        DashBoardPage dashBoardPage = loginAs(username, password);
        dashBoardPage.getNav().selectAdminTools().render();
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    @Test(expectedExceptions={IllegalArgumentException.class})
    public void constructor()
    {
        ManageSitesPage manageSitesPage = new ManageSitesPage(null);
    }

}