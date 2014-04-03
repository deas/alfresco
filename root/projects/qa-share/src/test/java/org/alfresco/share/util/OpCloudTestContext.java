/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.share.util;
import org.alfresco.share.util.AbstractTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.webdrone.WebDrone;
import org.apache.log4j.Logger;

/**
 * Helper class for qa-share tests.
 * 
 * @author Jamal Kaabi-Mofrad
 */
public class OpCloudTestContext
{
    private static final Logger logger = Logger.getLogger(OpCloudTestContext.class);

    private Set<String> createdUsers;
    private Map<String, Set<String>> createdSites;
    private WebDrone drone;

    public OpCloudTestContext(AbstractTests concreteTest)
    {
        if (concreteTest == null)
        {
            throw new IllegalArgumentException(concreteTest + " is a mandatory parameter.");
        }
        this.createdUsers = new LinkedHashSet<String>();
        this.createdSites = new HashMap<String, Set<String>>();
        this.drone = concreteTest.drone;
    }

    /**
     * Stores the created user names for cleanup purposes.
     * 
     * @param userName the created userName
     */
    public void addUser(String... userName)
    {
        createdUsers.addAll(Arrays.asList(userName));
    }

    /**
     * Stores the created sites for cleanup purposes.
     * 
     * @param siteCreator site creator user name
     * @param site the created site name
     */
    public void addSite(String siteCreator, String... site)
    {
        Set<String> setOfSites = createdSites.get(siteCreator);
        if (setOfSites == null)
        {
            setOfSites = new HashSet<String>(Arrays.asList(site));
        }
        else
        {
            setOfSites.addAll(Arrays.asList(site));
        }
        createdSites.put(siteCreator, setOfSites);
    }

    /**
     * @return the createdUsers
     */
    public Set<String> getCreatedUsers()
    {
        return this.createdUsers;
    }

    /**
     * @return the createdSites
     */
    public Map<String, Set<String>> getCreatedSites()
    {
        return this.createdSites;
    }

    /**
     * @return the createdSites as a list.
     */
    public List<String> getCreatedSitesAsList()
    {
        List<String> sites = new ArrayList<>(createdSites.size());
        for (Map.Entry<String, Set<String>> site : createdSites.entrySet())
        {
            sites.addAll(site.getValue());
        }
        return sites;
    }

    /**
     *
     * Calls the cleanupSites methods for all users.
     * Note: Assumes user was created with default password.
     *
     * @throws Exception
     */
    public void cleanupAllSites() throws Exception
    {
        List<String> users = new ArrayList<> (getCreatedUsers());
        for (String username : users)
        {
            Set<String> sitesToBeDeleted = createdSites.get(username);
            SiteUtil.deleteSitesAsUser(drone, username, sitesToBeDeleted);
        }
    }
}
