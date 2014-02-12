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

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Abstract site navigation for the different types
 * of site navigation, base to collaboration based sites
 *
 * @author Michael Suzuki
 * @version 1.7.1
 */
public abstract class AbstractSiteNavigation extends HtmlElement
{
    protected static final By CUSTOMISE_DASHBOARD_BTN = By.cssSelector("div[class^='page-title']>div>span>span>a[href$='customise-site-dashboard']");
    protected static final By CONFIGURATION_DROPDOWN = By.id("HEADER_SITE_CONFIGURATION_DROPDOWN");
    protected static final By CONFIGURE_ICON = By.id("HEADER_SITE_CONFIGURATION_DROPDOWN");
    protected static final By CUSTOMIZE_SITE = By.cssSelector("#HEADER_CUSTOMIZE_SITE_text");
    protected static final By MORE_BUTTON_LINK = By.cssSelector(".links>div>div>ul>li>a");
    protected static final String SITE_DASHBOARD = "Site Dashboard";
    protected static final String DASHBOARD = "Dashboard";
    protected static final String PROJECT_LIBRARY = "Project Library";
    protected static final String DOCUMENT_LIBRARY = "Document Library";
    protected static final String INVITE_BUTTON = "a[href$='invite']";
    protected static final String CUSTOMIZE_LINK_TEXT = "Customize Site";
    protected static final String WIKI = "Wiki";
    protected static final String SITE_LINK_NAV_PLACEHOLER = "div.site-navigation > span:nth-of-type(%d) > a";
    public static final String LABEL_DOCUMENTLIBRARY_TEXT = "span#HEADER_SITE_DOCUMENTLIBRARY_text";
    public static final String LABEL_DOCUMENTLIBRARY_PLACEHOLDER = "div#HEADER_SITE_DOCUMENTLIBRARY";
    protected final String siteNavPlaceHolder;
    private final String dashboardLink;
    protected AlfrescoVersion alfrescoVersion;
    public AbstractSiteNavigation(WebDrone drone)
    {
        super(drone);
        alfrescoVersion = drone.getProperties().getVersion();
        siteNavPlaceHolder = alfrescoVersion.isDojoSupported() ? "div[id^='alfresco/layout/LeftAndRight']": "div#alf-hd";
        setWebElement(drone.find(By.cssSelector(siteNavPlaceHolder)));
        dashboardLink = alfrescoVersion.isDojoSupported() ? "div#HEADER_SITE_DASHBOARD": String.format(SITE_LINK_NAV_PLACEHOLER,1);
    }
    /**
     * Check if the site navigation link is highlighted.
     * @param By by selector of site nav link
     * @return if link is highlighted
     */
    public boolean isLinkActive(By by)
    {
        if(by == null) { throw new UnsupportedOperationException("By selector is required"); }
        try
        {
            String active = alfrescoVersion.isDojoSupported() ? "Selected" : "active-page";
            WebElement element = getDrone().findAndWait(by);
            String value = element.getAttribute("class");
            if(value != null && !value.isEmpty())
            {
                return value.contains(active);
            }
        }
        catch (TimeoutException e) { }
        return false;
    }
    
    /**
     * Check if the site dashboard navigation link is highlighted.
     * @return if link is highlighted
     */
    public boolean isDashboardActive()
    {
        return isLinkActive(By.cssSelector(dashboardLink));
    }
    /**
     * Action of selecting on Site Dash board link.
     */
    public HtmlPage selectSiteDashBoard()
    {
        find(By.cssSelector(dashboardLink)).click();
        return FactorySharePage.resolvePage(drone);
    }
    /**
     * Checks if dash board link is displayed.
     * @return true if displayed
     */
    public boolean isDashboardDisplayed()
    {
        return isLinkDisplayed(By.cssSelector(dashboardLink));
    }
    /**
     * Select the drop down on the page and clicks on the link.
     * @param title String title label of site nav
     * @return HtmlPage page object result of selecting the link.
     */
    protected HtmlPage select(final String title)
    {
        WebElement link = findElement(By.linkText(title));
        link.click();
        return FactorySharePage.resolvePage(getDrone());
    }
    /**
     * Select the drop down on the page and clicks on the link.
     * @param by css locator
     * @return HtmlPage page object result of selecting the link.
     */
    protected void select(final By by)
    {
        WebElement link = find(by);
        link.click();
    }
    
    /**
     * Checks if item is displayed.
     * @return true if displayed
     */
    public boolean isLinkDisplayed(final By by)
    {
        if(by != null)
        {
            try
            {
                return drone.find(by).isDisplayed();
            }
            catch (NoSuchElementException nse) { }
        }
        return false;
    }
}
