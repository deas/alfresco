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
package org.alfresco.po.share.site;

import org.openqa.selenium.By;

/**
 * 
 * @author Shan Nagarajan
 * @since  1.7.0
 */
public enum SitePageType
{
    BLOG("#template_x002e_customise-pages_x002e_customise-site_x0023_default-page-blog-postlist"),
    CALENDER("#template_x002e_customise-pages_x002e_customise-site_x0023_default-page-calendar"),
    DATA_LISTS("#template_x002e_customise-pages_x002e_customise-site_x0023_default-page-data-lists"),
    DISCUSSIONS("#template_x002e_customise-pages_x002e_customise-site_x0023_default-page-discussions-topiclist"),
    DOCUMENT_LIBRARY("#template_x002e_customise-pages_x002e_customise-site_x0023_default-page-documentlibrary"),
    LINKS("#template_x002e_customise-pages_x002e_customise-site_x0023_default-page-links"),
    WIKI("#template_x002e_customise-pages_x002e_customise-site_x0023_default-page-wiki-page");
    
    private String id;
    
    private SitePageType(String id)
    {
        this.id = id;
    }
    
    public By getLocator()
    {
        return By.cssSelector(id);
    }
    
}