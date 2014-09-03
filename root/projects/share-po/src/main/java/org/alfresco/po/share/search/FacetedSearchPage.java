package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;


/**
 * The Class FacetedSearchPage.
 * 
 * @author Richard Smith
 */
@SuppressWarnings("unchecked")
public class FacetedSearchPage extends SharePage implements SearchResultPage
{

    /** Constants */
    private static final By SEARCH_INFO_DIV = By.cssSelector("div.info");
    private static final By FACET_GROUP = By.cssSelector("div.alfresco-documentlibrary-AlfDocumentFilters:not(.hidden)");
    private static final By RESULT = By.cssSelector("tr.alfresco-search-AlfSearchResult");
    private static final By CONFIGURE_SEARCH = By.cssSelector("div[id=FCTSRCH_CONFIG_PAGE_LINK]");    
    private static final Log logger = LogFactory.getLog(FacetedSearchPage.class);
    
    private FacetedSearchHeaderSearchForm headerSearchForm;
    private FacetedSearchScopeMenu scopeMenu;
    private FacetedSearchForm searchForm;
    private List<FacetedSearchFacetGroup> facetGroups;
    private FacetedSearchSort sort;
    private FacetedSearchView view;
    private List<SearchResult> results;

    /**
     * Instantiates a new faceted search page.
     * 
     * @param drone WebDriver browser client
     */
    public FacetedSearchPage(WebDrone drone)
    {
        super(drone);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render()
     */
    @Override
    public FacetedSearchPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @Override
    public FacetedSearchPage render(long maxPageLoadingTime)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @Override
    public FacetedSearchPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            try
            {
                
                if (!drone.find(SEARCH_INFO_DIV).isDisplayed())
                {
                    break;
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        loadElements();
        return this;
    } 
       
    /**
     * Gets the header search form.
     * 
     * @return {@link FacetedSearchHeaderSearchForm}
     */
    public FacetedSearchHeaderSearchForm getHeaderSearchForm()
    {
        return headerSearchForm;
    }

    /**
     * Gets the scope menu.
     * 
     * @return {@link FacetedSearchScopeMenu}
     */
    public FacetedSearchScopeMenu getScopeMenu()
    {
        return scopeMenu;
    }

    /**
     * Gets the search form.
     * 
     * @return {@link FacetedSearchForm}
     */
    public FacetedSearchForm getSearchForm()
    {
        return searchForm;
    }

    /**
     * Gets the facet groups.
     * 
     * @return List<{@link FacetedSearchFacetGroup}>
     */
    public List<FacetedSearchFacetGroup> getFacetGroups()
    {
        return this.facetGroups;
    }

    /**
     * Gets the sort.
     * 
     * @return {@link FacetedSearchSort}
     */
    public FacetedSearchSort getSort()
    {
        return sort;
    }

    /**
     * Gets the view.
     * 
     * @return {@link FacetedSearchView}
     */
    public FacetedSearchView getView()
    {
        return view;
    }

    /**
     * Gets the results.
     * 
     * @return List<{@link FacetedSearchResult}>
     */
    public List<SearchResult> getResults()
    {
        return this.results;
    }

    /**
     * Gets a result by its title if it exists.
     *
     * @param title
     * @return the result
     */
	public SearchResult getResultByTitle(String title) 
	{
		try
		{
			for (SearchResult facetedSearchResult : getResults())
			{
				if (facetedSearchResult.getTitle().equals(title)) 
				{
					return facetedSearchResult;
				}
			}
		} 
		catch (TimeoutException e)
		{
			logger.error("Unable to get the title : ", e);
		}

		throw new PageOperationException("Unable to get the title  : ");

	}

    /**
     * Gets a result by its name if it exists.
     *
     * @param name
     * @return the result
     */
    public SearchResult getResultByName(String name)
    {
        try {
			for(SearchResult facetedSearchResult : getResults())
			{
			    if(facetedSearchResult.getName().equals(name))
			    {
			        return facetedSearchResult;
			    }
			}
		} 
		catch (TimeoutException e)
        {
            logger.error("Unable to get the name : ", e);
        }

        throw new PageOperationException("Unable to get the name  : ");			
        
    }

    /**
     * Scroll to page bottom.
     */
    public void scrollSome(int distance)
    {
        this.drone.executeJavaScript("window.scrollTo(0," + distance + ");", "");
    }

    /**
     * Scroll to page bottom.
     */
    public void scrollToPageBottom()
    {
        this.drone.executeJavaScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));", "");
    }

    /**
     * Gets the current url hash.
     *
     * @return the url hash
     */
    public String getUrlHash()
    {
        String url = this.drone.getCurrentUrl();

        // Empty url or no #
        if(StringUtils.isEmpty(url) || !StringUtils.contains(url, "#"))
        {
            return null;
        }

        return StringUtils.substringAfter(url, "#");
    }

    /**
     * Initialises the elements that make up a FacetedSearchPage.
     */
    public void loadElements()
    {
        // Initialise the faceted search form
        this.headerSearchForm = new FacetedSearchHeaderSearchForm(drone);

        // Initialise the faceted search scope menu
        this.scopeMenu = new FacetedSearchScopeMenu(drone);

        // Initialise the faceted search form
        this.searchForm = new FacetedSearchForm(drone);

        // Initialise the faceted search facet groups
        List<WebElement> facetGroups = drone.findAll(FACET_GROUP);
        this.facetGroups = new ArrayList<FacetedSearchFacetGroup>();
        for (WebElement facetGroup : facetGroups)
        {
            this.facetGroups.add(new FacetedSearchFacetGroup(drone, facetGroup));
        }

        // Initialise the faceted search sort
        this.sort = new FacetedSearchSort(drone);
        
        // Initialise the faceted search view
        this.view = new FacetedSearchView(drone);

        // Initialise the faceted search results
        List<WebElement> results = drone.findAll(RESULT);
        this.results = new ArrayList<SearchResult>();
        for (WebElement result : results)
        {
            this.results.add(new FacetedSearchResult(drone, result));
        }
    }
    /**
     * Get the numeric value display on top of search results.
     * The number indicates the total count found for given search.
     * @return
     */
    public int getResultCount()
    {
        String val = drone.find(By.cssSelector("#FCTSRCH_RESULTS_MENU_BAR span.alfresco-html-Label.bold")).getText();
        return Integer.valueOf(val).intValue();
    }
    /**
     * Select the facet that matches the title from the facet grouping. 
     * @param title facet name, eg Microsoft Word
     * @return FacetedSearchPage with filtered results
     */
    public FacetedSearchPage selectFacet(final String title)
    {
        WebDroneUtil.checkMandotaryParam("Facet title", title);
        WebElement facet = drone.find(By.xpath(String.format("//span[@class = 'filterLabel'][contains(., '%s')]",title)));
        facet.click();
        return this;
    }
    
    /**
     * Click on configure search Link.     *
     * @return the FacetedSearchConfigpage
     */
    public FacetedSearchConfigPage clickConfigureSearchLink()
    {
        try {
			WebElement configure_search = drone.find(CONFIGURE_SEARCH);
			if (configure_search.isDisplayed())
			{
			    configure_search.click();        
			}
			return new FacetedSearchConfigPage(drone);
		} catch (TimeoutException e)
        {
            logger.error("Unable to find the link : " + e.getMessage());
        }

        throw new PageException("Unable to find the link : " );
    }   
         
    /**
     * verify configureSearchlink is displayed
     * @param driver
     * @return Boolean
     */
    public Boolean isConfigureSearchDisplayed(WebDrone driver)
    {
        try
        {
            WebElement configure_search = drone.find(CONFIGURE_SEARCH);
            if (configure_search.isDisplayed())
            {
                return true;
            }

        }

        catch (NoSuchElementException te)
        {
        	if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find configure search link");
            }
        }
        return false;
    }
    
    @Override
    public boolean hasResults()
    {
        return !getResults().isEmpty();
    }

    @Override
    public HtmlPage selectItem(String name)
    {
        if (name == null || name.isEmpty())
        {
            throw new IllegalArgumentException("Search row name is required");
        }
        try
        {
            String selector = String.format("//span[@class = 'value'][contains(., '%s')]", name);
            drone.find(By.xpath(selector)).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("Search result %s item not found", name), e);
        }
        
        return FactorySharePage.getUnknownPage(drone);
    }

    @Override
    public HtmlPage selectItem(int number)
    {
        if (number < 0)
        {
            throw new IllegalArgumentException("Value can not be negative");
        }
        number += 1;
        try
        {
            String selector = String.format("tr.alfresco-search-AlfSearchResult:nth-of-type(%d) a", number);
            WebElement row = drone.find(By.cssSelector(selector));
            row.click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("Search result %d item not found", number), e);
        }

        return FactorySharePage.getUnknownPage(drone);
    }
}