/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.wcm.client.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.DictionaryService;
import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.SearchResults;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.Tag;

/**
 * Base section interface implementation
 * 
 * @author Roy Wetherall
 */
public class SectionImpl extends ResourceBaseImpl implements Section
{
    private static final long serialVersionUID = -443446798048387948L;

    /** Section children */
    private List<Section> sections = new ArrayList<Section>();

    /** Sections by name */
    private Map<String, Section> sectionsByName = new TreeMap<String, Section>();
    
    private ConcurrentMap<String, String> assetIdByAssetName = new ConcurrentHashMap<String, String>(89);

    /** Top Tags */
    private List<Tag> tags = new ArrayList<Tag>();

    /** Collection node id for the section. */
    // private String collectionFolderId;

    /** Configuration map */
    private Map<String, String> configMap;

    /** Dictionary service */
    private DictionaryService dictionaryService;

    /**
     * @see org.alfresco.wcm.client.Section#getSections()
     */
    @Override
    public List<Section> getSections()
    {
        return sections;
    }

    /**
     * @see org.alfresco.wcm.client.Section#getSection(java.lang.String)
     */
    @Override
    public Section getSection(String sectionName)
    {
        return sectionsByName.get(sectionName);
    }

    /**
     * Set the tags used by this section (and below)
     * 
     * @param tags
     *            the tags used by the section
     */
    public void setTags(List<Tag> tags)
    {
        this.tags = tags;
    }

    /**
     * @see org.alfresco.wcm.client.Section#getTags()
     */
    @Override
    public List<Tag> getTags()
    {
        return tags;
    }

    /**
     * Set the dictionary service
     * 
     * @param dictionaryService
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * @see org.alfresco.wcm.client.Section#getTemplateMappings()
     */
    public Map<String, String> getTemplateMappings()
    {
        return configMap;
    }

    @Override
    public boolean getExcludeFromNav()
    {
        Boolean exclude = (Boolean) getProperty(PROPERTY_EXCLUDE_FROM_NAV);
        return (exclude == null) ? false : exclude.booleanValue();
    }

    /**
     * Sets the child sections. Package visibility since this is only used
     * during construction of the section hierarchy.
     * 
     * @param sections
     *            child sections
     */
    /* package */void setSections(List<Section> sections)
    {
        this.sections = sections;
        Map<String, Section> newMap = new TreeMap<String, Section>();
        for (Section section : sections)
        {
            newMap.put(section.getName(), section);
        }
        sectionsByName = newMap;
    }

    /**
     * @see org.alfresco.wcm.client.impl.ResourceBaseImpl#setProperties(java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setProperties(Map<String, Serializable> props)
    {
        super.setProperties(props);

        // Extract the config map for convenience
        configMap = (Map<String, String>) props.get(PROPERTY_SECTION_CONFIG);
    }

    /**
     * Add child to a section
     * 
     * @param section
     *            child section
     */
    void addChild(Section section)
    {
        this.sections.add(section);
        this.sectionsByName.put(section.getName(), section);
    }

    /**
     * @see org.alfresco.wcm.client.Section#getCollectionFolderId()
     */
    /*
     * @Override public String getCollectionFolderId() { return
     * collectionFolderId; }
     */

    /**
     * @see org.alfresco.wcm.client.Section#getCollectionFolderId()
     */
    /*
     * public void setCollectionFolderId(String collectionFolderId) {
     * this.collectionFolderId = collectionFolderId; }
     */

    /**
     * @see org.alfresco.wcm.client.Section#getAsset(java.lang.String)
     */
    @Override
    public Asset getAsset(String resourceName)
    {
        Asset asset;
        if (resourceName == null || resourceName.length() == 0)
        {
            asset = getIndexPage();
        }
        else
        {
            String assetId = assetIdByAssetName.get(resourceName);
            if (assetId == null)
            {
                asset = getAssetFactory().getSectionAsset(getId(), resourceName);
                if (asset != null)
                {
                    assetIdByAssetName.putIfAbsent(resourceName, asset.getId());
                }
            }
            else
            {
                asset = getAssetFactory().getAssetById(assetId);
            }
        }
        return asset;
    }

    /**
     * @see org.alfresco.wcm.client.Section#getIndexPage()
     */
    @Override
    public Asset getIndexPage()
    {
        return getAsset("index.html");
    }

    /**
     * @see org.alfresco.wcm.client.Section#getPath()
     */
    @Override
    public String getPath()
    {
        StringBuilder sb = new StringBuilder("/");
        Section section = this;
        while (section.getContainingSection() != null)
        {
            sb.insert(0, "/" + section.getName());
            section = section.getContainingSection();
        }
        return sb.toString();
    }

    @Override
    public SearchResults search(Query query)
    {
        return getAssetFactory().findByQuery(query);
    }

    @Override
    public SearchResults search(String phrase, int maxResults, int resultsToSkip)
    {
        Query query = createQuery();
        query.setPhrase(phrase);
        query.setMaxResults(maxResults);
        query.setResultsToSkip(resultsToSkip);
        return search(query);
    }

    @Override
    public SearchResults searchByTag(String tag, int maxResults, int resultsToSkip)
    {
        Query query = createQuery();
        query.setTag(tag);
        query.setMaxResults(maxResults);
        query.setResultsToSkip(resultsToSkip);
        return search(query);
    }

    /**
     * @see org.alfresco.wcm.client.Section#createQuery()
     */
    @Override
    public Query createQuery()
    {
        Query query = new Query();
        query.setSectionId(getId());
        return query;
    }

    /**
     * @see org.alfresco.wcm.client.Section#getTemplate(java.lang.String)
     */
    @Override
    public String getTemplate(String type)
    {
        type = dictionaryService.removeTypePrefix(type);
        return findTemplate(this, type);
    }

    @Override
    public AssetCollection getAssetCollection(String name)
    {
        return getCollectionFactory().getCollection(getId(), name);
    }

    @Override
    public AssetCollection getAssetCollection(String name, int resultsToSkip, int maxResults)
    {
        return getCollectionFactory().getCollection(getId(), name, resultsToSkip, maxResults);
    }

    /**
     * Find template for a section by type
     * 
     * @param section
     *            section
     * @param type
     *            content type
     * @return String template based on match
     */
    private String findTemplate(Section section, String type)
    {
        String template = null;

        // See if there is a template match on this section
        template = findTemplate(section.getTemplateMappings(), type);

        // If no template found check parent
        Section parent = section.getContainingSection();
        if (template == null && parent != null)
        {
            template = findTemplate(parent, type);
        }

        return template;
    }

    /**
     * Find the template for a given page type within a template map
     * 
     * @param templateMap
     * @param type
     * @return
     */
    private String findTemplate(Map<String, String> templateMap, String type)
    {
        // Get the template from the map
        String template = templateMap.get(type);

        // If no template is found and we are not already checking for
        // cm:content ...
        if (template == null && dictionaryService.isRootType(type) == false)
        {
            // .. get the parent type name
            String parentType = dictionaryService.getParentType(type, true);
            if (parentType != null)
            {
                // .. and see if we can find a template for that
                template = findTemplate(templateMap, parentType);
            }
        }
        return template;
    }
}
