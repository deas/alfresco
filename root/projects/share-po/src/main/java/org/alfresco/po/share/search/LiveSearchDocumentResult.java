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

package org.alfresco.po.share.search;


import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Holds details of the document search result in the live search dropdown 
 * @author jcule
 *
 */
public class LiveSearchDocumentResult
{

    private static final String DOCUMENT_RESULT_TITLE = "a";
    private static final String DOCUMENT_RESULT_SITE_NAME = "span a:nth-of-type(1)";
    private static final String DOCUMENT_RESULT_USER_NAME = "span a:nth-of-type(2)";
    
    private WebElement webElement;
    private String title;
    private String siteName;
    private String userName;
    
    
    /**
     * Constructor
     * @param element {@link WebElement} 
     * @param drone 
     */
    public LiveSearchDocumentResult(WebElement element)
    {
        webElement = element;
    }

    /**
     * Title of search result document item.
     * @return String title
     */
    public String getTitle()
    {
        if(title == null)
        {
            try
            {
                title = webElement.findElement(By.cssSelector(DOCUMENT_RESULT_TITLE)).getText();
            }
            catch (NoSuchElementException e)
            {
                throw new PageOperationException("Unable to find live search document result title", e);
            }
        }
        return title;
    }
    
    /**
     * Site name of document search result item.
     * @return String siteName
     */
    public String getSiteName()
    {
        if(siteName == null)
        {
            try
            {
                siteName = webElement.findElement(By.cssSelector(DOCUMENT_RESULT_SITE_NAME)).getText();
            }
            catch (NoSuchElementException e)
            {
                throw new PageOperationException("Unable to find live search document result site name", e);
            }
        }
        return siteName;
    }
    
    /**
     * User name of document search result item.
     * @return String userName
     */
    public String getUserName()
    {
        if(userName == null)
        {
            try
            {
                userName = webElement.findElement(By.cssSelector(DOCUMENT_RESULT_USER_NAME)).getText();
            }
            catch (NoSuchElementException e)
            {
                throw new PageOperationException("Unable to find live search document result user name", e);
            }
        }
        return userName;
    }
}
