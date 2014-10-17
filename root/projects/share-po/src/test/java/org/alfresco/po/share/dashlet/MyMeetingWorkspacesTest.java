package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.po.share.dashlet.MyMeetingWorkSpaceDashlet;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify My Meeting Workspaces dash let page elements are in place.
 * 
 * @author Bogdan.Bocancea
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one" })
public class MyMeetingWorkspacesTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    private CustomiseUserDashboardPage customizeUserDash;
    private AlfrescoVersion version;
    private String userName;
    MyMeetingWorkSpaceDashlet dashlet = null;
    
    @BeforeClass(groups = { "alfresco-one" })
    public void setup() throws Exception
    {
        userName = "UserMeeting" + System.currentTimeMillis();
        
        version = drone.getProperties().getVersion();
        if (version.isCloud())
        {
            ShareUtil.loginAs(drone, shareUrl, username, password).render();
        }
        else
        {
            createEnterpriseUser(userName);
            ShareUtil.loginAs(drone, shareUrl, userName, UNAME_PASSWORD).render();
        }
       
    }

    @Test
    public void instantiateMyMeetingWorkspacesDashlet()
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_MEETING_WORKSPACES, 1).render();

        dashlet = new MyMeetingWorkSpaceDashlet(drone);
        Assert.assertNotNull(dashlet);
    }

    @Test(dependsOnMethods = "instantiateMyMeetingWorkspacesDashlet")
    public void getSites() throws Exception
    {
        dashlet = new MyMeetingWorkSpaceDashlet(drone);
        
        boolean isMessage = dashlet.isNoMeetingWorkspaceDisplayed();
        
        Assert.assertTrue(isMessage);
    }
    
    @Test(dependsOnMethods="getSites")
    public void selectMySiteDashlet() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard();
        dashlet = new MyMeetingWorkSpaceDashlet(drone);
        final String title = dashlet.getDashletTitle();            
        Assert.assertEquals("My Meeting Workspaces", title);
    }

}
