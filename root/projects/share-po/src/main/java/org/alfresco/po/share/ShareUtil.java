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
     * A simple Enum to request the required Alfresco version.
     * 
     * @author Jamal Kaabi-Mofrad
     */
    public static enum RequiredAlfrescoVersion
    {
        CLOUD_ONLY, ENTERPRISE_ONLY;
    }
    
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
     * A helper method to check the current running Alfresco version against the
     * required version.
     * 
     * @param alfrescoVersion the currently running Alfresco version
     * @param requiredVersion the required version (CLOUD_ONLY |
     *            ENTERPRISE_ONLY)
     * @throws UnsupportedOperationException if the {@code requiredVersion}
     *             differs from the {@code alfrescoVersion}
     * @throws IllegalArgumentException if {@code requiredVersion} is invalid
     */
    public static void validateAlfrescoVersion(AlfrescoVersion alfrescoVersion, RequiredAlfrescoVersion requiredVersion)
                throws UnsupportedOperationException, IllegalArgumentException
    {
        boolean isCloud = alfrescoVersion.isCloud();
        switch (requiredVersion)
        {
            case CLOUD_ONLY:
                if (!isCloud)
                {
                    throw new UnsupportedOperationException("This operation is Cloud only, not available for Enterprise.");
                }
                break;
            case ENTERPRISE_ONLY:
                if (isCloud)
                {
                    throw new UnsupportedOperationException("This operation is Enterprise only, not available for Cloud.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unrecognised Alfresco version: " + requiredVersion);
        }
    }

    /**
     *
     */
    public static void deleteUser(String username)
    {
        //TODO make this work (via API call).
    }
}
