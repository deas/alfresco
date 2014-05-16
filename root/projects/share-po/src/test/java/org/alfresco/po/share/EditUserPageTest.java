/*
 * Copyright (C) 2005-2013 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share;

import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.webdrone.exception.PageOperationException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify people finder page elements are in place.
 * 
 * @author Meenal Bhave
 * @since 1.7
 */
@Listeners(FailedTestListener.class)
public class EditUserPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    private String userinfo = "user" + System.currentTimeMillis() + "@test.com";

    @BeforeClass(groups = { "alfresco-one" })
    public void setup() throws Exception
    {
        dashBoard = loginAs(username, password);
        createUser();
    }

    private void createUser() throws Exception
    {
        AlfrescoVersion version = drone.getProperties().getVersion();
        if (!version.isCloud())
        {
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            newPage.inputFirstName(userinfo);
            newPage.inputLastName(userinfo);
            newPage.inputEmail(userinfo);
            newPage.inputUsername(userinfo);
            newPage.inputPassword(userinfo);
            newPage.inputVerifyPassword(userinfo);
            newPage.selectCreateUser().render();

        }
    }

    private EditUserPage navigateToEditUser()
    {
        try
        {
            SharePage sharePage = drone.getCurrentPage().render();
            UserSearchPage userPage = sharePage.getNav().getUsersPage().render();

            userPage = userPage.searchFor(userinfo).render();
            UserProfilePage userProfile = userPage.clickOnUser(userinfo).render();
            EditUserPage editUser = userProfile.selectEditUser().render();
            return editUser;
        }
        catch (UnsupportedOperationException uso)
        {
            throw new UnsupportedOperationException("Can not navigate to Edit User Page");
        }
    }

    @Test(groups = "Cloud-only", expectedExceptions = UnsupportedOperationException.class)
    public void test100navigateToEditUsersOnCloud() throws Exception
    {
        navigateToEditUser();
    }

    @Test(groups = "Enterprise-only")
    public void test101editUserCancel() throws Exception
    {
        String editedUserInfo = "edited" + userinfo;
        EditUserPage editUser = navigateToEditUser();
        editUser.editFirstName(editedUserInfo);
        editUser.editLastName(editedUserInfo);
        editUser.editEmail(editedUserInfo);
        editUser.editPassword(editedUserInfo);
        editUser.editVerifyPassword(editedUserInfo);
        editUser.editQuota("10");
        editUser.selectDisableAccount();
        editUser.cancelEditUser();
    }

    @Test(groups = "Enterprise-only")
    public void test102editUserSave() throws Exception
    {
        String editedUserInfo = "edited" + userinfo;
        String groupToAdd = "ALFRESCO_ADMINISTRATORS";

        EditUserPage editUser = navigateToEditUser();
        editUser.editFirstName(editedUserInfo);
        editUser.editLastName(editedUserInfo);
        editUser.editEmail(editedUserInfo);
        editUser.editPassword(editedUserInfo);
        editUser.editVerifyPassword(editedUserInfo);
        editUser.selectUseDefault().render();
        editUser.searchGroup(groupToAdd).render();
        editUser = editUser.addGroup(groupToAdd).render();
        editUser.saveChanges().render();        
    }

    @Test(groups = "Enterprise-only", expectedExceptions=PageOperationException.class)
    public void test103editGroupNonExisting() throws Exception
    {
        EditUserPage editUser = navigateToEditUser();
        editUser.searchGroup("xxx");
        editUser.addGroup("xxx");
    }
}
