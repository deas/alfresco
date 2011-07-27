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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.CollectionFactory;
import org.alfresco.wcm.client.DictionaryService;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.SectionFactory;
import org.alfresco.wcm.client.Tag;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.WebSiteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory class for creating Sections from the repository
 * 
 * @author Chris Lack
 */
public class SectionFactoryWebscriptImpl implements SectionFactory
{
    private ThreadLocal<List<WebscriptParam>> localParamList = new ThreadLocal<List<WebscriptParam>>()
    {
        @Override
        protected List<WebscriptParam> initialValue()
        {
            return new ArrayList<WebscriptParam>();
        }

        @Override
        public List<WebscriptParam> get()
        {
            List<WebscriptParam> list = super.get();
            list.clear();
            return list;
        }

    };

    private static final String PROPERTY_TAG_SUMMARY = "cm:tagScopeSummary";

    private final static Log log = LogFactory.getLog(SectionFactoryWebscriptImpl.class);

    private long sectionsRefreshAfter;

    /**
     * Map of sections. If a section is present then all of its descendents will
     * be too. (Note: All sections for a website will only be present when
     * rootSectionsByWebsite has also been populated.)
     */
    private Map<String, Section> sectionsById = new TreeMap<String, Section>();

    /** Cache of all sections under a website */
    private Map<String, SectionCache> rootSectionsByWebsite = new TreeMap<String, SectionCache>();

    /** Asset factory */
    private AssetFactory assetFactory;

    /** Dictionary service */
    private DictionaryService dictionaryService;
    private CollectionFactory collectionFactory;
    private WebScriptCaller webscriptCaller;

    /**
     * Set the asset factory
     * 
     * @param assetFactory
     *            asset factory
     */
    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }

    /**
     * Set the dictionary service
     * 
     * @param dictionaryService
     *            dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setCollectionFactory(CollectionFactory collectionFactory)
    {
        this.collectionFactory = collectionFactory;
    }

    public void setWebscriptCaller(WebScriptCaller webscriptCaller)
    {
        this.webscriptCaller = webscriptCaller;
    }

    /**
     * Create a Section from a QueryResult
     * 
     * @param result
     *            query result
     * @return Section section object
     */
    @SuppressWarnings("unchecked")
    protected SectionDetails buildSection(TreeMap<String, Serializable> result)
    {
        SectionDetails sectionDetails = new SectionDetails();

        SectionImpl section = new SectionImpl();

        section.setProperties(result);

        List<String> tagSummary = (List<String>) result.get(PROPERTY_TAG_SUMMARY);
        section.setTags(createTags(tagSummary));
        section.setAssetFactory(assetFactory);
        section.setSectionFactory(this);
        section.setDictionaryService(dictionaryService);
        section.setCollectionFactory(collectionFactory);

        sectionDetails.section = section;
        sectionDetails.objectTypeId = (String) result.get("type");

        // Don't set parent of webroot as it is conceptually the top of the tree
        if (!sectionDetails.objectTypeId.equals("ws:webroot"))
        {
            String parentId = (String) result.get("ws:parentId");
            section.setPrimarySectionId(parentId);
            // TODO keep parent id in SectionDetails too as not accessible from
            // resource. Is this deliberate?
            sectionDetails.parentId = parentId;
        }

        return sectionDetails;
    }

    /**
     * Create list of tag details from separate lists of names and counts
     * 
     * @param tagNames
     *            list of tag names
     * @param tagCounts
     *            list of tag counts
     * @return combined list of tags
     */
    private List<Tag> createTags(List<String> tagSummary)
    {
        List<Tag> tags = new ArrayList<Tag>();
        if (tagSummary != null)
        {
            for (String tag : tagSummary)
            {
                String[] nameCountPair = tag.split("=");
                if (nameCountPair.length != 2)
                {
                    continue;
                }
                try
                {
                    tags.add(new TagImpl(nameCountPair[0], Integer.parseInt(nameCountPair[1])));
                }
                catch (Exception ex)
                {
                    log.warn("Ignoring invalid tag summary data: " + tag);
                }
            }
        }
        return tags;
    }

    /**
     * @see org.alfresco.wcm.client.SectionFactory#getSection(String)
     */
    @Override
    public Section getSection(String id)
    {
        // Try cache
        Section section = sectionsById.get(id);

        // Not in cache so fetch
        if (section == null)
        {
            section = findSectionWithChildren(id);
        }
        return section;
    }

    /**
     * @see org.alfresco.wcm.client.SectionFactory#getSectionFromPathSegments(String,
     *      String[])
     */
    @Override
    public Section getSectionFromPathSegments(String rootSectionId, String[] pathSegments)
    {
        refreshCacheIfRequired(rootSectionId);

        SectionCache cache = rootSectionsByWebsite.get(rootSectionId);
        Section currentSection = cache.rootSection;

        for (String segment : pathSegments)
        {
            if (segment.length() > 0)
                currentSection = currentSection.getSection(segment);
            if (currentSection == null)
                return null;
        }
        return currentSection;
    }

    /**
     * Refreshes the section cache if empty or expired.
     * 
     * @param rootSectionId
     *            the id of the parent web root
     */
    private void refreshCacheIfRequired(String rootSectionId)
    {
        SectionCache cache = rootSectionsByWebsite.get(rootSectionId);
        if (cache == null || cache.isExpired() == true)
        {
            Section rootSection = findSectionWithChildren(rootSectionId);
            SectionCache cachedRootSection = new SectionCache(rootSection);
            rootSectionsByWebsite.put(rootSectionId, cachedRootSection);
        }
    }

    /**
     * Fetch a section and its children.
     * 
     * @param topSectionId
     *            the section id to start from
     * @return the section object with its children populated.
     */
    private Section findSectionWithChildren(String topSectionId)
    {
        List<WebscriptParam> params = localParamList.get();
        params.add(new WebscriptParam("sectionId", topSectionId));
        params.add(new WebscriptParam("includeChildren", "true"));
        WebSite currentSite = WebSiteService.getThreadWebSite();
        if (currentSite != null)
        {
            params.add(new WebscriptParam("siteId", currentSite.getId()));
        }
        AssetDeserializerXmlImpl deserializer = new AssetDeserializerXmlImpl();
        webscriptCaller.post("websection", deserializer, params);
        LinkedList<TreeMap<String, Serializable>> sectionList = deserializer.getAssets();

        List<SectionDetails> orderedList = new ArrayList<SectionDetails>();
        for (TreeMap<String, Serializable> result : sectionList)
        {
            SectionDetails sectionDetails = buildSection(result);
            orderedList.add(sectionDetails);
            sectionsById.put(sectionDetails.section.getId(), sectionDetails.section);
        }

        // Fetch collections folders for sections
        /*
         * Not needed for webscript collection solution String cquery =
         * MessageFormat.format(QUERY_COLLECTION_FOLDERS,
         * SqlUtils.encloseSQLString(topSectionId)); ItemIterable<QueryResult>
         * cresults = session.query(cquery, false);
         * 
         * for (QueryResult cresult : cresults) { String collectionsFolderId =
         * (String)cresult.getPropertyValueById(PropertyIds.OBJECT_ID); String
         * sectionId =
         * (String)cresult.getPropertyValueById(PropertyIds.PARENT_ID); Section
         * section = sectionsById.get(sectionId);
         * ((SectionImpl)section).setCollectionFolderId(collectionsFolderId); }
         */

        // Add child sections to parents
        Section topSection = null;
        for (SectionDetails details : orderedList)
        {
            if (details.section.getId().equals(topSectionId))
            {
                topSection = details.section;
            }
            else
            {
                SectionImpl parent = (SectionImpl) sectionsById.get(details.parentId);
                parent.addChild(details.section);
            }

        }
        return topSection;
    }

    public void setSectionsRefreshAfter(int seconds)
    {
        this.sectionsRefreshAfter = seconds * 1000;
    }

    /**
     * Section with parent id. Used until the section is parented.
     */
    private class SectionDetails
    {
        SectionImpl section;
        String objectTypeId;
        String parentId;
    }

    /**
     * A root section and the time the data was cached.
     */
    private class SectionCache
    {
        Section rootSection;
        long sectionsRefeshedAt;

        SectionCache(Section root)
        {
            this.rootSection = root;
            this.sectionsRefeshedAt = System.currentTimeMillis();
        }

        /**
         * Indicates whether the sections cache has expired or not
         * 
         * @return boolean true if expired, false otherwise
         */
        boolean isExpired()
        {
            long now = System.currentTimeMillis();
            long difference = now - sectionsRefeshedAt;
            return difference > sectionsRefreshAfter;
        }
    }

}
