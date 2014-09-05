/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share;

import org.alfresco.po.share.util.PageUtils;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Share page object util
 * 
 * @author Michael Suzuki
 */
public class ShareUtil
{
    private static Log logger = LogFactory.getLog(ShareUtil.class);

    private static final String ADMIN_SYSTEMSUMMARY_PAGE = "alfresco/service/enterprise/admin";
    private static final String BULK_IMPORT_PAGE = "alfresco/service/bulkfsimport";
    private static final String BULK_IMPORT_IN_PLACE_PAGE = "alfresco/service/bulkfsimport/inplace";

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
     * Use Logout on header bar and mimics action of logout on share.
     */
    public static synchronized void logout(final WebDrone drone)
    {
        SharePage page = drone.getCurrentPage().render();
        page.getNav().logout();
    }

    /**
     * Logs user into share.
     * 
     * @param drone {@link WebDrone}
     * @param url Share url
     * @param userInfo username and password
     * @return {@link HtmlPage} page response
     */
    public static HtmlPage loginAs(final WebDrone drone, final String url, final String... userInfo)
    {
        drone.navigateTo(url);
        LoginPage lp = new LoginPage(drone).render();
        lp.loginAs(userInfo[0], userInfo[1]);
        return drone.getCurrentPage();
    }

    /**
     * Logs user into share from the current page.
     * 
     * @param drone
     * @param userInfo
     * @return
     */
    public static HtmlPage logInAs(final WebDrone drone, final String... userInfo)
    {
        LoginPage lp = new LoginPage(drone).render();
        lp.loginAs(userInfo[0], userInfo[1]);
        return drone.getCurrentPage();
    }
    
    
    /**
     * A helper method to check the current running Alfresco version against the
     * required version.
     * 
     * @param alfrescoVersion the currently running Alfresco version
     * @param requiredVersion the required version (CLOUD_ONLY |
     *            ENTERPRISE_ONLY)
     * @throws UnsupportedOperationException if the {@code requiredVersion} differs from the {@code alfrescoVersion}
     * @throws IllegalArgumentException if {@code requiredVersion} is invalid
     */
    public static void validateAlfrescoVersion(AlfrescoVersion alfrescoVersion, RequiredAlfrescoVersion requiredVersion) throws UnsupportedOperationException,
            IllegalArgumentException
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
     * @param drone
     * @param userInfo
     * @return
     */
    public static HtmlPage navigateToSystemSummary(final WebDrone drone, String url, final String... userInfo)
    {
//        String url = drone.getCurrentUrl();
        String protocolVar = PageUtils.getProtocol(url);
        String consoleUrlVar = PageUtils.getAddress(url);
        String systemUrl = String.format("%s%s:%s@%s/" + ADMIN_SYSTEMSUMMARY_PAGE, protocolVar, userInfo[0], userInfo[1], consoleUrlVar);
        try {
            drone.navigateTo(systemUrl);
        } catch (Exception e) {
            if (logger.isDebugEnabled())
            {
                logger.debug("Following exception was occurred" + e + ". Param systemUrl was " + systemUrl);
            }
        }
        return drone.getCurrentPage().render();
    }

    /**
     * Methods for navigation bulk import page
     * 
     * @param drone
     * @param inPlace
     * @param userInfo
     * @return
     */
    public static HtmlPage navigateToBulkImport(final WebDrone drone, boolean inPlace, final String... userInfo)
    {
        String currentUrl = drone.getCurrentUrl();
        String protocolVar = PageUtils.getProtocol(currentUrl);
        String consoleUrlVar = PageUtils.getAddress(currentUrl);
        if (inPlace)
        {
            currentUrl = String.format("%s%s:%s@%s/" + BULK_IMPORT_IN_PLACE_PAGE, protocolVar, userInfo[0], userInfo[1], consoleUrlVar);
            logger.info("Property 'currentUrl' is: " + currentUrl);
        }
        else
        {
            currentUrl = String.format("%s%s:%s@%s/" + BULK_IMPORT_PAGE, protocolVar, userInfo[0], userInfo[1], consoleUrlVar);
            logger.info("Property 'currentUrl' is: " + currentUrl);
        }

        try
        {
            logger.info("Navigate to 'currentUrl': " + currentUrl);
            drone.navigateTo(currentUrl);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Following exception was occurred" + e + ". Param systemUrl was " + currentUrl);
            }
        }
        return drone.getCurrentPage().render();
    }
}
