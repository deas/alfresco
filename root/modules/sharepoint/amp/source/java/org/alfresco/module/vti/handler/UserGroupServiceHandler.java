/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.module.vti.handler;

import java.util.List;

import org.alfresco.module.vti.metadata.model.UserBean;

/**
 * Interface for user group web service handler
 * 
 * @author AndreyAk
 */
public interface UserGroupServiceHandler
{

    /**
     * Returns a user name based on the specified e-mail address.
     * 
     * @param dwsUrl dws url
     * @param emailList list that specifies the e-mail address of the user
     * @return List<UserBean>
     */
    List<UserBean> getUserLoginFromEmail(String dwsUrl, List<String> emailList);

    /**
     * Adds the collection of users to the specified site group.
     * 
     * @param dwsUrl dws url
     * @param roleName name of the site group to add users to
     * @param usersList list that contains information about the users to add ({@link UserBean})
     */
    void addUserCollectionToRole(String dwsUrl, String roleName, List<UserBean> usersList);

    /**
     * Check user on member
     * 
     * @param dwsUrl dws url
     * @param username list that contains information about the users to add
     * @return <i>true</i>, if user is member; otherwise, <i>false</i>
     */
    boolean isUserMember(String dwsUrl, String username);

}
