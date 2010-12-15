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
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.CollectionFactory;
import org.alfresco.wcm.client.DictionaryService;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.SectionFactory;
import org.alfresco.wcm.client.Tag;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.alfresco.wcm.client.util.SqlUtils;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory class for creating Sections from the repository
 * 
 * @author Chris Lack
 */
public class SectionFactoryCmisImpl implements SectionFactory
{
    private static final String PROPERTY_TAGS = "ws:topTags";
    private static final String PROPERTY_TAG_COUNTS = "ws:topTagCounts";

    private static final String COLUMNS = " f.cmis:objectId, f.cmis:name, t.cm:title, t.cm:description, f.cmis:objectTypeId, "
            + "f.cmis:parentId, f.ws:sectionConfig, f.ws:excludeFromNavigation, f.ws:topTags, f.ws:topTagCounts ";

    private final static Log log = LogFactory.getLog(SectionFactoryCmisImpl.class);

    private static final String QUERY_SECTION_WITH_CHILDREN = "select " + COLUMNS + ", o.ws:orderIndex as ord "
            + "from ws:section as f " + "join cm:titled as t on t.cmis:objectId = f.cmis:objectId "
            + "join ws:ordered as o on o.cmis:objectId = f.cmis:objectId "
            + "where (in_tree(f, {0}) or f.cmis:objectId = {1}) " + "order by ord";
    /*
     * private static final String QUERY_COLLECTION_FOLDERS =
     * "select f.cmis:objectId, f.cmis:parentId "+ "from cmis:folder as f " +
     * "join ws:webassetCollectionFolder as c on c.cmis:objectId = f.cmis:objectId "
     * + "where in_tree(f, {0})";
     */

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

    /**
     * Create a Section from a QueryResult
     * 
     * @param result
     *            query result
     * @return Section section object
     */
    private SectionDetails buildSection(QueryResult result)
    {
        SectionDetails sectionDetails = new SectionDetails();

        SectionImpl section = new SectionImpl();

        Map<String, Serializable> properties = new TreeMap<String, Serializable>();
        properties.put(PropertyIds.OBJECT_ID, (String) result.getPropertyValueById(PropertyIds.OBJECT_ID));
        properties.put(PropertyIds.NAME, (String) result.getPropertyValueById(PropertyIds.NAME));
        properties.put(Section.PROPERTY_TITLE, (String) result.getPropertyValueById(Section.PROPERTY_TITLE));
        properties.put(Section.PROPERTY_DESCRIPTION, 
                (String) result.getPropertyValueById(Section.PROPERTY_DESCRIPTION));
        properties.put(Section.PROPERTY_EXCLUDE_FROM_NAV, 
                (Boolean) result.getPropertyValueById(Section.PROPERTY_EXCLUDE_FROM_NAV));

        List<String> configList = result.getPropertyMultivalueById(Section.PROPERTY_SECTION_CONFIG);
        properties.put(Section.PROPERTY_SECTION_CONFIG, (Serializable) parseSectionConfig(configList));

        section.setProperties(properties);
        List<String> tagNames = result.getPropertyMultivalueById(PROPERTY_TAGS);
        List<BigInteger> tagCounts = result.getPropertyMultivalueById(PROPERTY_TAG_COUNTS);
        section.setTags(createTags(tagNames, tagCounts));
        section.setAssetFactory(assetFactory);
        section.setSectionFactory(this);
        section.setDictionaryService(dictionaryService);
        section.setCollectionFactory(collectionFactory);

        sectionDetails.section = section;
        sectionDetails.objectTypeId = (String) result.getPropertyValueById(PropertyIds.OBJECT_TYPE_ID);

        // Don't set parent of webroot as it is conceptually the top of the tree
        if (!sectionDetails.objectTypeId.equals("F:ws:webroot"))
        {
            String parentId = (String) result.getPropertyValueById(PropertyIds.PARENT_ID);
            section.setPrimarySectionId(parentId);
            // TODO keep parent id in SectionDetails too as not accessible from
            // resource. Is this deliberate?
            sectionDetails.parentId = parentId;
        }

        return sectionDetails;
    }

    /**
     * Parses the section configuration from the name value pair string list
     * into a map.
     * 
     * @param sectionConfigList
     * @return Map<String, String> map of types and templates
     */
    private Map<String, String> parseSectionConfig(List<String> sectionConfigList)
    {
        Map<String, String> result = new TreeMap<String, String>();
        for (String configValue : sectionConfigList)
        {
            String[] split = configValue.split("=");
            if (split.length == 2)
            {
                String name = split[0];
                String value = split[1];
                result.put(name, value);
                //We cater for either "cmis:document" or "cm:content" interchangeably...
                if ("cmis:document".equals(name))
                {
                    result.put("cm:content", value);
                }
                else if ("cm:content".equals(name))
                {
                    result.put("cmis:document", value);
                }
            }
            else
            {
                // TODO log
            }
        }
        return result;
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
    private List<Tag> createTags(List<String> tagNames, List<BigInteger> tagCounts)
    {
        List<Tag> tags = new ArrayList<Tag>();
        if (tagNames != null)
        {
            for (int i = 0; i < tagNames.size(); i++)
            {
                String name = tagNames.get(i);
                Integer count = (tagCounts != null && i < tagCounts.size() ? tagCounts.get(i).intValue() : null);
                TagImpl tagImpl = new TagImpl(name, count);
                tags.add(tagImpl);
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
        Session session = CmisSessionHelper.getSession();

        // Fetch sections
        String query = MessageFormat.format(QUERY_SECTION_WITH_CHILDREN, SqlUtils.encloseSQLString(topSectionId),
                SqlUtils.encloseSQLString(topSectionId));
        log.debug("About to run CMIS query: " + query);
        ItemIterable<QueryResult> results = session.query(query, false);

        List<SectionDetails> orderedList = new ArrayList<SectionDetails>();
        for (QueryResult result : results)
        {
            SectionDetails sectionDetails = buildSection(result);
            if (!sectionDetails.objectTypeId.equals("F:ws:webroot")
                    && !sectionDetails.objectTypeId.equals("F:ws:section"))
                continue;

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
