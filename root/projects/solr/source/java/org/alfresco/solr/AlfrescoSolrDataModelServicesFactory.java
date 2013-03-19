package org.alfresco.solr;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.opencmis.dictionary.CMISAbstractDictionaryService;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.opencmis.dictionary.FilteredDictionaryComponent;
import org.alfresco.opencmis.dictionary.QNameFilter;
import org.alfresco.opencmis.dictionary.QNameFilterImpl;
import org.alfresco.opencmis.mapping.CMISMapping;
import org.alfresco.opencmis.mapping.CMISMappingWithExclusions;
import org.alfresco.opencmis.mapping.RuntimePropertyLuceneBuilderMapping;
import org.alfresco.repo.cache.MemoryCache;
import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.DictionaryDAOImpl;
import org.alfresco.service.cmr.dictionary.DictionaryService;

/**
 * Basic Factory for creating services for the AlfrescoSolrDataMode.  It always creates a default
 *
 * @author Gethin James
 */
public class AlfrescoSolrDataModelServicesFactory
{
    public static final String DICTIONARY_FILTERED_WITH_EXCLUSIONS = "cmisWithExclusions";
    private static QNameFilter filter = null;

    static {      
        filter = new QNameFilterImpl();
        filter.initFilter();
    }
    
    /**
     * Constructs a dictionary by default.
     * 
     * @param cmisMapping
     * @param dictionaryService
     * @param dictionaryDAO
     * @return Map<String,CMISDictionaryService> 
     */
    public static Map<String,CMISAbstractDictionaryService> constructDictionaries(CMISMapping cmisMapping, DictionaryComponent dictionaryService, DictionaryDAO dictionaryDAO) 
    {
        Map<String,CMISAbstractDictionaryService> dictionaries = new HashMap<String,CMISAbstractDictionaryService>();
        dictionaries.put(CMISStrictDictionaryService.DEFAULT, newInstance(cmisMapping, dictionaryService, dictionaryDAO));
        CMISMappingWithExclusions mappingWithExclusions = newInstanceOfExcludedCMISMapping(cmisMapping, filter);
        dictionaries.put(DICTIONARY_FILTERED_WITH_EXCLUSIONS, newInstance(mappingWithExclusions, dictionaryService, dictionaryDAO));
        return dictionaries;
    }

    
    /**
     * Constructs a dictionary by default.
     * 
     * @param dictionaryDAO
     * @return Map<String,CMISDictionaryService> 
     */
    public static Map<String, DictionaryComponent> constructDictionaryServices(DictionaryDAOImpl dictionaryDAO)
    {
        Map<String,DictionaryComponent> dictionaries = new HashMap<String,DictionaryComponent>();
        DictionaryComponent compo = new DictionaryComponent();
        compo.setDictionaryDAO(dictionaryDAO);
        dictionaries.put(CMISStrictDictionaryService.DEFAULT, compo);
        FilteredDictionaryComponent fdc  = new FilteredDictionaryComponent();
        fdc.setDictionaryDAO(dictionaryDAO);
        fdc.setFilter(filter);
        dictionaries.put(DICTIONARY_FILTERED_WITH_EXCLUSIONS, fdc);
        return dictionaries;
        
    }
    
    private static CMISMappingWithExclusions newInstanceOfExcludedCMISMapping(CMISMapping cmisMapping, QNameFilter filter)
    {
        CMISMappingWithExclusions cmisMappingWithExcl = new CMISMappingWithExclusions();
        cmisMappingWithExcl.setNamespaceService(cmisMapping.getNamespaceService());
        cmisMappingWithExcl.setDictionaryService(cmisMapping.getDictionaryService());
        cmisMappingWithExcl.setFilter(filter);
        cmisMappingWithExcl.afterPropertiesSet();
        return cmisMappingWithExcl;
    }
    
    protected static CMISStrictDictionaryService newInstance(CMISMapping cmisMapping, DictionaryService dictionaryService, DictionaryDAO dictionaryDAO)
    {
        CMISStrictDictionaryService cmisDictionaryService = new CMISStrictDictionaryService();
        cmisDictionaryService.setCmisMapping(cmisMapping);
        cmisDictionaryService.setDictionaryService(dictionaryService);
        cmisDictionaryService.setDictionaryDAO(dictionaryDAO);
        cmisDictionaryService.setSingletonCache(new MemoryCache<String, CMISStrictDictionaryService.DictionaryRegistry>());
        
        RuntimePropertyLuceneBuilderMapping luceneBuilderMapping = new RuntimePropertyLuceneBuilderMapping();
        luceneBuilderMapping.setDictionaryService(dictionaryService);
        luceneBuilderMapping.setCmisDictionaryService(cmisDictionaryService);
        cmisDictionaryService.setPropertyLuceneBuilderMapping(luceneBuilderMapping);
        luceneBuilderMapping.afterPropertiesSet();
        return cmisDictionaryService;
    }

}
