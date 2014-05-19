package org.alfresco.po.share.search;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FacetedSearchResult
{
    /** Constants. */
    private static final By NAME = By.cssSelector("tr td.nameAndTitleCell span.alfresco-renderers-Property:first-of-type span.inner");
    private static final By TITLE = By.cssSelector("tr td.nameAndTitleCell span.alfresco-renderers-Property:last-of-type span.value");
    private static final By DATE = By.cssSelector("tr td.dateCell span.value");
    private static final By DESCRIPTION = By.cssSelector("tr td.descriptionCell span.value");
    private static final By SITE = By.cssSelector("tr td.siteCell span.value");

    private WebDrone drone;
    private WebElement link;
    private String name;
    private String title;
    private String date;
    private String description;
    private String site;

    /**
     * Instantiates a new faceted search result - some items may be null.
     */
    public FacetedSearchResult(WebDrone drone, WebElement result)
    {
        this.drone = drone;
        if(result.findElements(NAME).size() > 0)
        {
            this.link = result.findElement(NAME);
            this.name = link.getText();
        }
        if(result.findElements(TITLE).size() > 0)
        {
            this.title = result.findElement(TITLE).getText();
        }
        if(result.findElements(DATE).size() > 0)
        {
            this.date = result.findElement(DATE).getText();
        }
        if(result.findElements(DESCRIPTION).size() > 0)
        {
            this.description = result.findElement(DESCRIPTION).getText();
        }
        if(result.findElements(SITE).size() > 0)
        {
            this.site = result.findElement(SITE).getText();
        }
    }

    /**
     * Gets the result link.
     *
     * @return the link
     */
    public WebElement getLink()
    {
        return link;
    }

    /**
     * Gets the result name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the result title.
     *
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Gets the result date.
     *
     * @return the date
     */
    public String getDate()
    {
        return date;
    }

    /**
     * Gets the result description.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Gets the result site.
     *
     * @return the site
     */
    public String getSite()
    {
        return site;
    }

    /**
     * Click a result link.
     *
     * @return the html page
     */
    public HtmlPage clickLink()
    {
        this.link.click();
        return FactorySharePage.resolvePage(this.drone);
    }
}