package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.TestNGException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class InviteMembersPageTest extends AbstractTest
{
    InviteMembersPage membersPage;
    String user;
    WebElement invitee;
    DashBoardPage dashBoard;
    String siteName;
    List<WebElement> inviteesList;
    String userNameTest;

    @BeforeClass(groups="Enterprise-only")
    public void instantiateMembers() throws Exception
    {
        userNameTest = "user" + System.currentTimeMillis() + "@test.com";
        siteName = "InviteMembersTest" + System.currentTimeMillis();
        dashBoard = loginAs(username, password);

        // Creating new user.
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = page.selectNewUser().render();
        newPage.inputFirstName(userNameTest);
        newPage.inputLastName(userNameTest);
        newPage.inputEmail(userNameTest);
        newPage.inputUsername(userNameTest);
        newPage.inputPassword(userNameTest);
        newPage.inputVerifyPassword(userNameTest);
        UserSearchPage userCreated = newPage.selectCreateUser().render();
        userCreated.searchFor(userNameTest).render();
        Assert.assertTrue(userCreated.hasResults());
        /*
         * TODO: Cloud user creation needs to be implemented
         * Assert.assertTrue(UserUtil.createUser(userName, userName,
         * userName, userName, userName, alfrescoVersion.isCloud(),
         * shareUrl));
         */
        
        // TODO: Cloud user creation needs to be implemented
        // Assert.assertTrue(UserUtil.createUser(userName, userName,
        // userName, userName, userName, alfrescoVersion.isCloud(),
        // shareUrl));

        SitePage site = dashBoard.getNav().selectCreateSite().createNewSite(siteName).render();
        membersPage = site.getSiteNav().selectInvite().render();
    }

    @Test(groups="Enterprise-only")
    public void testIsMembersPageTitlePresent()
    {
        Assert.assertTrue(membersPage.titlePresent());
    }

    @Test(groups="Enterprise-only")
    public void testSearchUser() throws Exception
    {
        List<String> searchUsers = null;
        searchUsers = membersPage.searchUser(userNameTest);
        RenderTime t = new RenderTime(4000);
        try
        {
            while(true)
            {
                t.start();
                try
                {
                    searchUsers = membersPage.searchUser(userNameTest);
                    if(searchUsers != null) 
                    {
                        break;
                    }
                }
                finally
                {
                    t.end();
                }
            }
        }
        catch (PageRenderTimeException e)
        {
            saveScreenShot("InviteMembersPage.testSearchUser");
            throw new TestNGException("failed to render in time");
        }
        user = searchUsers.get(0);
        Assert.assertNotNull(user);
        Assert.assertTrue(user.contains(userNameTest));
    }

    @Test(dependsOnMethods = "testAddingNullUser", groups = "Enterprise-only")
    public void testAddUser()
    {
        Assert.assertNotNull(membersPage.clickAddUser(user));
    }
    @Test(dependsOnMethods = "testSearchUser", groups = "Enterprise-only", expectedExceptions = UnsupportedOperationException.class)
    public void testAddingNullUser()
    {
        Assert.assertNotNull(membersPage.clickAddUser(null));
    }
    
    
    @Test(dependsOnMethods = "testAddUser", groups="Enterprise-only", expectedExceptions = UnsupportedOperationException.class)
    public void testSelectInviteeAndAssignRoleToNull()
    {
        Assert.assertNotNull(membersPage.selectInviteeAndAssignRole(user, null));
    }
    
    @Test(dependsOnMethods = "testSelectInviteeAndAssignRoleToNull", groups="Enterprise-only", expectedExceptions = UnsupportedOperationException.class)
    public void testSelectNullInviteeAndAssignRole()
    {
        Assert.assertNotNull(membersPage.selectInviteeAndAssignRole(null, UserRole.COLLABORATOR));
    }

    @Test(dependsOnMethods = "testSelectNullInviteeAndAssignRole", groups="Enterprise-only")
    public void testSelectInviteeAndAssignRole()
    {
        Assert.assertNotNull(membersPage.selectInviteeAndAssignRole(user, UserRole.COLLABORATOR));
    }
    
    @Test(dependsOnMethods = "testSelectInviteeAndAssignRole", groups="Enterprise-only")
    public void testEnabledInviteButton()
    {
        Assert.assertNotNull(membersPage.clickInviteButton());
    }

    @AfterClass(alwaysRun=true ,groups="Enterprise-only")
    public void deleteSite() throws Exception
    {
        SiteFinderPage siteFinder = dashBoard.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        siteFinder = siteFinder.deleteSite(siteName).render();
    }
}
