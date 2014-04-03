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
    BLOG("//li[contains(@id, '_default-page-blog-postlist')]"),
    CALENDER("//li[contains(@id, '_default-page-calendar')]"),
    DATA_LISTS("//li[contains(@id, '_default-page-data-lists')]"),
    DISCUSSIONS("//li[contains(@id, '_default-page-discussions-topiclist')]"),
    DOCUMENT_LIBRARY("//li[contains(@id, '_default-page-documentlibrary')]"),
    LINKS("//li[contains(@id, '_default-page-links')]"),
    WIKI("//li[contains(@id, '_default-page-wiki-page')]");
    
    private String id;
    
    private SitePageType(String id)
    {
        this.id = id;
    }
    
    public By getLocator()
    {
        return By.xpath(id);
    }
    
}