package org.alfresco.po.share.search;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.admin.ActionsSet;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FacetedSearchResult
{
    /** Constants. */
    private static final By NAME = By.cssSelector("div.nameAndTitleCell span.alfresco-renderers-Property:first-of-type span.inner a");
    private static final By TITLE = By.cssSelector("div.nameAndTitleCell span.alfresco-renderers-Property:last-of-type span.value");
    private static final By DATE = By.cssSelector("div.dateCell span.inner");
    private static final By DESCRIPTION = By.cssSelector("div.descriptionCell span.value");
    private static final By SITE = By.cssSelector("div.siteCell span.inner");
    private static final By ACTIONS = By.cssSelector("tr td.actionsCell");   
    private static final By IMAGE = By.cssSelector("tbody[id=FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS] td.thumbnailCell img");

    private WebDrone drone;
    private WebElement link;
    private String name;
    private String title;
    private WebElement dateLink;
    private String date;
    private String description;
    private WebElement siteLink;
    private String site;
    private ActionsSet actions;
    private WebElement imageLink;

    /**
     * Instantiates a new faceted search result - some items may be null.
     */
    public FacetedSearchResult(WebDrone drone, WebElement result)
    {
        this.drone = drone;
        if(result.findElements(NAME).size() > 0)
        {
            link = result.findElement(NAME);
            name = link.getText();
        }
        if(result.findElements(TITLE).size() > 0)
        {
            title = result.findElement(TITLE).getText();
        }
        if(result.findElements(DATE).size() > 0)
        {
            dateLink = result.findElement(DATE);
            date = dateLink.getText();
        }
        if(result.findElements(DESCRIPTION).size() > 0)
        {
            description = result.findElement(DESCRIPTION).getText();
        }
        if(result.findElements(SITE).size() > 0)
        {
            siteLink = result.findElement(SITE);
            site = siteLink.getText();
        }
       
        if(result.findElements(IMAGE).size() > 0)
        {
        	imageLink = result.findElement(IMAGE);        	
             
        }        
        actions = new ActionsSet(drone, result.findElement(ACTIONS));
        
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
     * Click a result link.
     *
     * @return the html page
     */
    public HtmlPage clickLink()
    {
        link.click();
        return FactorySharePage.resolvePage(this.drone);
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
     * Gets the result dateLink.
     *
     * @return the dateLink
     */
    public WebElement getDateLink()
    {
        return dateLink;
    }

    /**
     * Click a result dateLink.
     *
     * @return the html page
     */
    public HtmlPage clickDateLink()
    {
        dateLink.click();
        return FactorySharePage.resolvePage(this.drone);
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
     * Gets the result siteLink.
     *
     * @return the siteLink
     */
    public WebElement getSiteLink()
    {
        return siteLink;
    }

    /**
     * Click a result siteLink.
     *
     * @return the html page
     */
    public HtmlPage clickSiteLink()
    {
        siteLink.click();
        return FactorySharePage.resolvePage(this.drone);
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
     * Gets the actions.
     *
     * @return the actions
     */
    public ActionsSet getActions()
    {
        return actions;
    }   
        
    
    /**
     * click the result imageLink.
     *
     * @return the preview pop up window
     */
    public PreViewPopUpPage clickImageLink()
    {
        imageLink.click();
        return new PreViewPopUpPage(drone);
    }  
    
}