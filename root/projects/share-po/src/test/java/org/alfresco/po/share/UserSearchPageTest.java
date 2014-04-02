/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify people finder page elements are in place.
 * 
 * @author Meenal Bhave
 * @since 1.6.1
 */
@Listeners(FailedTestListener.class)
@Test(groups ={"Enterprise-only"})
public class UserSearchPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;

    @BeforeClass(groups ={"Enterprise-only", "Cloud-only"})
    public void setup() throws Exception
    {
        dashBoard = loginAs(username, password);
    }

    @Test(groups = "Cloud-only", expectedExceptions = UnsupportedOperationException.class)
    public void test100searchUsersOnCloud()
    {
        dashBoard.getNav().getUsersPage();
    }
    
    @Test(groups = "Enterprise-only", expectedExceptions=ShareException.class)
    public void test101createDuplicateUser() throws Exception
    {         
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newUser = page.selectNewUser().render();
        newUser.createEnterpriseUser("admin", "admin", "admin", "admin@alfresco.com", "admin");
        SharePopup errPopup = FactorySharePage.resolvePage(drone).render();
        errPopup.handleMessage();
        Assert.fail();
    }
    
    @Test(groups = "Enterprise-only")
    public void test102searchForUser() throws Exception
    {
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            Assert.assertTrue(page.isLogoPresent());
            Assert.assertTrue(page.isTitlePresent());
            UserSearchPage resultsEmpty = page.searchFor("").render();
            Assert.assertFalse(resultsEmpty.hasResults());
            UserSearchPage results = page.searchFor("asdf").render();
            Assert.assertFalse(results.hasResults());
            results = page.searchFor("a").render();
            Assert.assertTrue(results.hasResults());
    }

    @Test(groups = "Enterprise-only")
    public void test103cancelCreateUser() throws Exception
    {
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            newPage.inputQuota("1");
            newPage.searchGroup("a").render();
            newPage.selectDisableAccount();
            UserSearchPage userCreated = newPage.cancelCreateUser().render();
            Assert.assertTrue(userCreated.isTitlePresent());
    }

    @Test(groups = "Enterprise-only")
    public void test104createUser() throws Exception
    {
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            String userinfo = "user" + System.currentTimeMillis() + "@test.com";
            newPage.inputFirstName(userinfo);
            newPage.inputLastName(userinfo);
            newPage.inputEmail(userinfo);
            newPage.inputUsername(userinfo);
            newPage.inputPassword(userinfo);
            newPage.inputVerifyPassword(userinfo);
            UserSearchPage userCreated = newPage.selectCreateUser().render();
            userCreated.searchFor(userinfo).render();
            Assert.assertTrue(userCreated.hasResults());
    }
    
    @Test(groups = "Enterprise-only")
    public void test105createUserWithGroup() throws Exception
    {
        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        String userinfo = "user" + System.currentTimeMillis() + "@test.com";
        newPage.createEnterpriseUserWithGroup(userinfo, userinfo, userinfo, userinfo, userinfo, "ALFRESCO_ADMINISTRATORS");
        userPage = dashBoard.getNav().getUsersPage().render();
        userPage.searchFor(userinfo).render();
        Assert.assertTrue(userPage.hasResults());
    }

    //Once below JIRA resolved we can enable the test
    //https://issues.alfresco.com/jira/browse/ALF-20660
    @Test(groups = "Enterprise-only", enabled=false)
    public void test106createUserUsingCSV() throws Exception
    {
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            UploadFilePage upload = page.selectUploadUserCSVFile().render();
            upload.cancel();
            page = drone.getCurrentPage().render();
            Assert.assertTrue(page.isTitlePresent());
    }

    @Test(groups = "Enterprise-only")
    public void test107createAnotherUser() throws Exception
    {
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            String userinfo = "user" + System.currentTimeMillis() + "@test.com";
            newPage.inputFirstName(userinfo);
            newPage.inputEmail(userinfo);
            newPage.inputUsername(userinfo);
            newPage.inputPassword(userinfo);
            newPage.inputVerifyPassword(userinfo);
            NewUserPage newUser = newPage.selectCreateAnotherUser().render();
            newUser.cancelCreateUser().render();
    }
    
    @Test(groups = "Enterprise-only")
    public void test108editUser() throws Exception
    {
        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        String userinfo = "user" + System.currentTimeMillis() + "@test.com";
        userPage = newPage.createEnterpriseUser(userinfo, userinfo, userinfo, userinfo, "password").render();
        userPage.searchFor(userinfo).render();
        Assert.assertTrue(userPage.hasResults());
        UserProfilePage userProfile = userPage.clickOnUser(userinfo).render();
        EditUserPage editUserPage = userProfile.selectEditUser().render();
        editUserPage.cancelEditUser().render();
    }
    
    @Test(groups = "Enterprise-only", expectedExceptions = UnsupportedOperationException.class)
    public void test109createNullUserMethod() throws Exception
    {
        NewUserPage newPage = new NewUserPage(drone);
        newPage.createEnterpriseUser(null, null, null, null, null);
        Assert.fail("UnsupportedOperationException Expected");
    }

    @Test(groups = "Enterprise-only", expectedExceptions = UnsupportedOperationException.class)
    public void test110searchForNullUserMethod() throws Exception
    {
        UserSearchPage userCreated = new UserSearchPage(drone);
        userCreated.searchFor(null);
        Assert.fail("UnsupportedOperationException Expected");
    }

    @Test(groups = "Enterprise-only")
    public void test111createUserMethod() throws Exception
    {
            UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = userPage.selectNewUser().render();
            String userinfo = "user" + System.currentTimeMillis() + "@test.com";
            newPage.createEnterpriseUser(userinfo, userinfo, userinfo, userinfo, userinfo).render();
            userPage = dashBoard.getNav().getUsersPage().render();
            userPage.searchFor(userinfo).render();
            Assert.assertTrue(userPage.hasResults());
    }

    @Test(groups = "Enterprise-only")
    public void test112createUserMethodWithEmptyLastName() throws Exception
    {
            UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = userPage.selectNewUser().render();
            String userinfo = "user" + System.currentTimeMillis() + "@test.com";
            newPage.createEnterpriseUser(userinfo, userinfo, "", userinfo, userinfo).render();
            userPage = dashBoard.getNav().getUsersPage().render();
            userPage.searchFor(userinfo).render();
            Assert.assertTrue(userPage.hasResults());
    }

    @Test(groups = "Enterprise-only", expectedExceptions = UnsupportedOperationException.class)
    public void test113createEmptyUserMethod() throws Exception
    {
        NewUserPage newPage = new NewUserPage(drone);
        String userinfo = "";
        newPage.createEnterpriseUser(userinfo, userinfo, userinfo, userinfo, userinfo);
    }
    
   
}
