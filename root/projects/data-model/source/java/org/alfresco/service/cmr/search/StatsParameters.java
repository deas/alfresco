package org.alfresco.service.cmr.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchParameters.SortDefinition;

/**
 * Defines Stats search criteria
 *
 * @author Gethin James
 * @since 5.0
 */
public class StatsParameters implements BasicSearchParameters
{ 
    public static final String PARAM_FIELD = "field";
    public static final String PARAM_FACET = "facet";
    public static final String FACET_PREFIX = "@";
    
    private String language;
    private String query;
    private String filterQuery;
    private List<StoreRef> stores = new ArrayList<>();
    private List<Locale> locales = new ArrayList<>();
    private List<SortDefinition> sortDefinitions = new ArrayList<>();
    private Map<String, String> statsParameters = new HashMap<>();
    
    public StatsParameters(String language, String query)
    {
        this(language, query, null);
    }
    
    public StatsParameters(String language, String query, String filterQuery)
    {
        super();
        this.language = language;
        this.query = query;
        this.filterQuery = filterQuery;
    }
    
    public String getLanguage()
    {
        return this.language;
    }
    public String getQuery()
    {
        return this.query;
    }    
    public String getFilterQuery()
    {
        return this.filterQuery;
    }
    public List<StoreRef> getStores()
    {
        return this.stores;
    }
    public List<Locale> getLocales()
    {
        return this.locales;
    }
    public List<SortDefinition> getSortDefinitions()
    {
        return this.sortDefinitions;
    }
    public Map<String, String> getStatsParameters()
    {
        return this.statsParameters;
    }
    
    /**
     * Add a sort definition.
     * 
     * @param sortDefinition - the sort definition to add. 
     */
    public void addSort(SortDefinition sortDefinition)
    {
        sortDefinitions.add(sortDefinition);
    }
    
    /**
     * Add a parameter
     * 
     * @param name
     * @param value
     */
    public void addStatsParameter(String name, String value)
    {
        statsParameters.put(name, value);
    }
    
    /**
     * Add a Store ref
     * 
     * @param store
     */
    public void addStore(StoreRef store)
    {      
        if (stores.size() != 0)
        {
            throw new IllegalStateException("At the moment, there can only be one stats store set for the search");
        }
        stores.add(store);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("StatsParameters [query=").append(this.query).append(", filterquery=")
                    .append(this.filterQuery).append(", language=")
                    .append(this.language).append(", stores=").append(this.stores)
                    .append(", locales=").append(this.locales).append(", sortDefinitions=")
                    .append(this.sortDefinitions).append(", statsParameters=")
                    .append(this.statsParameters).append("]");
        return builder.toString();
    }

}
