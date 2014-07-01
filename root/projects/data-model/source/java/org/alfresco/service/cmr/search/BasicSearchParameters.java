package org.alfresco.service.cmr.search;

import java.util.List;
import java.util.Locale;

import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchParameters.SortDefinition;

/**
 * These are high level parameters, for use when searching.
 * 
 * @author Gethin James
 * @since 5.0
 */
public interface BasicSearchParameters
{
    
    public String getLanguage();
    public String getQuery();
    public List<StoreRef> getStores();
    public List<Locale> getLocales();
    public List<SortDefinition> getSortDefinitions();

}
