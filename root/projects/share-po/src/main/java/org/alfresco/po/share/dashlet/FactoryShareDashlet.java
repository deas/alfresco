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
package org.alfresco.po.share.dashlet;

import java.util.NoSuchElementException;

import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;

/**
 * Alfresco Share factory, creates the appropriate page object that corresponds
 * to the browser view.
 * 
 * @author Michael Suzuki
 */
public final class FactoryShareDashlet
{
    private FactoryShareDashlet()
    {
    }

    /**
     * Gets the dashlet HTML element from the dashboard page.
     * 
     * @param drone {@link WebDrone}
     * @return name dashlet title
     */
    public static Dashlet getPage(final WebDrone drone, final String name)
    {
        try
        {
            if ("my-sites".equalsIgnoreCase(name))
            {
                return new MySitesDashlet(drone);
            }
            if ("my-documents".equalsIgnoreCase(name))
            {
                return new MyDocumentsDashlet(drone);
            }
            if ("activities".equalsIgnoreCase(name))
            {
                return new MyActivitiesDashlet(drone);
            }
            if ("tasks".equalsIgnoreCase(name))
            {
                return new MyTasksDashlet(drone);
            }
            if ("site-members".equalsIgnoreCase(name))
            {
                return new SiteMembersDashlet(drone);
            }
            if ("site-contents".equalsIgnoreCase(name))
            {
                return new SiteContentDashlet(drone);
            }
            if ("site-activities".equalsIgnoreCase(name))
            {
                return new SiteActivitiesDashlet(drone);
            }
            if ("welcome-site".equalsIgnoreCase(name))
            {
                return new SiteWelcomeDashlet(drone);
            }
            if ("site-notice".equalsIgnoreCase(name))
            {
                return new SiteNoticeDashlet(drone);
            }
            if ("site-search".equalsIgnoreCase(name))
            {
                return new SiteSearchDashlet(drone);
            }
            if ("my-discussions".equalsIgnoreCase(name))
            {
                return new MyDiscussionsDashlet(drone);
            }
            if ("saved-search".equalsIgnoreCase(name))
            {
                return new SavedSearchDashlet(drone);
            }
            throw new PageException(String.format("%s does not match any known dashlet name", name));
        }
        catch (NoSuchElementException ex)
        {
            throw new PageException("Dashlet can not be matched to an exsisting alfresco dashlet object: " + name, ex);
        }
    }
}
