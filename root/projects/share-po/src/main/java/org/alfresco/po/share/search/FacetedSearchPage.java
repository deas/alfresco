package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The Class FacetedSearchPage.
 * 
 * @author Richard Smith
 */
@SuppressWarnings("unchecked")
public class FacetedSearchPage extends SharePage
{

    /** Constants */
    private static final By FACET_GROUP = By.cssSelector("div.alfresco-documentlibrary-AlfDocumentFilters:not(.hidden)");
    private static final By RESULT = By.cssSelector("tr.alfresco-search-AlfSearchResult");

    private FacetedSearchForm searchForm;
    private List<FacetedSearchFacetGroup> facetGroups;
    private FacetedSearchSort sort;
    private List<FacetedSearchResult> results;

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
    public FacetedSearchPage render(RenderTime maxPageLoadingTime)
    {
        basicRender(maxPageLoadingTime);
        loadElements();
        return this;
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
     * Gets the results.
     * 
     * @return List<{@link FacetedSearchResult}>
     */
    public List<FacetedSearchResult> getResults()
    {
        return this.results;
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

        // Initialise the faceted search results
        List<WebElement> results = drone.findAll(RESULT);
        this.results = new ArrayList<FacetedSearchResult>();
        for (WebElement result : results)
        {
            this.results.add(new FacetedSearchResult(drone, result));
        }
    }
}