/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.workflow;

import java.util.Set;

/**
 * Company Home represents the
 * 
 * @author Shan Nagarajan
 * @since 1.7.1
 */
public class CompanyHome
{

    private Set<Site> sites;

    private Set<Content> contents;

    public Set<Site> getSites()
    {
        return sites;
    }

    public void setSites(Set<Site> sites)
    {
        this.sites = sites;
    }

    public Set<Content> getContents()
    {
        return contents;

    }

    public void setContents (Set<Content> contents)
    {
        this.contents = contents;
    }
}
