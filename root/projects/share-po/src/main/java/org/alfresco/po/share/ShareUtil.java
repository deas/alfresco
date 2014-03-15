/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;

/**
 * Share page object util 
 * @author Michael Suzuki
 *
 */
public class ShareUtil 
{
    /**
     * Pattern representing url prefix without the /share/*.*
     */
    private static final String BASE_URL_PATTERN = "^*/page.*";
    /**
     * The relative path to logout.
     */
    private static final String LOGOUT_PATTERN = "/page/dologout";
    
    /**
     * Logs user out, by using a restful approach. This has been done as the UI
     * has not labelled the logout with an id or css element to indicate a
     * logout link.
     * @throws Exception 
     */
    public static void logout(final WebDrone drone) 
    {
        String currentUrl = drone.getCurrentUrl();
        String url = currentUrl.replaceFirst(BASE_URL_PATTERN, LOGOUT_PATTERN);
        drone.navigateTo(url);
    }
    /**
     * Logs user into share.
     * @param drone {@link WebDrone}
     * @param url Share url
     * @param userInfo username and password
     * @return {@link HtmlPage} page response
     */
    public static HtmlPage loginAs(final WebDrone drone, final String url, final String ... userInfo)
    {
        drone.navigateTo(url);
        LoginPage lp = new LoginPage(drone).render();
        lp.loginAs(userInfo[0], userInfo[1]);
        return drone.getCurrentPage();
    }

    /**
     *
     * Is the User the Repo Admin?
     *
     * @return boolean
     */
    public static boolean isUserAdmin()
    {
        // TODO: Make this work.
        return true;
    }

    /**
     *
     * Is the User a member of the group
     *
     * @return boolean
     */
    public static boolean isUserInGroup(String group)
    {
        // TODO: Make this work.
        return true;
    }

    /**
     * Check to see the mode Share is in
     */
    public static void cloudCheck(AlfrescoVersion alfrescoVersion)
    {
        if(alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("This option is Enterprise only, not available for cloud");
        }
    }
}
