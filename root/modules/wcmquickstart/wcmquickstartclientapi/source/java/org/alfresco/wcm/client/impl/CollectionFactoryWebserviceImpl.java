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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.CollectionFactory;
import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.ResourceNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Get collection using a call to a web service. This is done to avoid having to
 * perform two queries - one for the collection meta data and one for the list
 * of assets ids within the collection. Open CMIS allows relationships to be
 * retrieved with an object by setting a parameter of an operational context
 * object passed to the query but this is only allowed if no joins are used.
 * 
 * @author Chris Lack
 */
public class CollectionFactoryWebserviceImpl implements CollectionFactory
{
    private AssetFactory assetFactory;
    private WebScriptCaller webscriptCaller;

    /**
     * Create a ResourceCollection from JSON
     * 
     * @param jsonObject
     *            object representing the json response
     * @return ResourceCollectionImpl collection object
     * @throws JSONException
     */
    private AssetCollectionImpl buildCollection(JSONObject jsonObject) throws JSONException
    {
        AssetCollectionImpl collection = new AssetCollectionImpl();
        collection.setId(jsonObject.getString("id"));
        collection.setName(jsonObject.getString("name"));
        collection.setTitle(jsonObject.getString("title"));
        collection.setDescription(jsonObject.getString("description"));
        return collection;
    }

    /**
     * Build a list of related target node ids for the collection
     * 
     * @param jsonObject
     *            json object
     * @return List<String> list of related resource ids
     * @throws JSONException
     */
    private List<String> buildRelatedAssetList(JSONObject jsonObject) throws JSONException
    {
        List<String> relatedIds = new ArrayList<String>();

        JSONArray relationships = jsonObject.getJSONArray("assets");
        for (int i = 0; i < relationships.length(); i++)
        {
            String targetId = relationships.getString(i);
            relatedIds.add(targetId);
        }
        return relatedIds;
    }

    /**
     * @see org.alfresco.wcm.client.impl.CollectionFactoryWebserviceImpl#getCollection(String,
     *      String)
     */
    @Override
    public AssetCollection getCollection(String sectionId, String collectionName)
    {
        return getCollection(sectionId, collectionName, 0, -1);
    }

    /**
     * @see org.alfresco.wcm.client.impl.CollectionFactoryWebserviceImpl#getCollection(String,
     *      String, int, int)
     */
    @Override
    public AssetCollection getCollection(String sectionId, String collectionName, int resultsToSkip, int maxResults)
    {
        if (sectionId == null || sectionId.length() == 0)
        {
            throw new IllegalArgumentException("sectionId must be supplied");
        }
        if (collectionName == null || collectionName.length() == 0)
        {
            throw new IllegalArgumentException("collectionName must be supplied");
        }

        try
        {
            String scriptUri = "assetcollections/" + URLEncoder.encode(collectionName, "UTF-8");
            WebscriptParam[] params = new WebscriptParam[] { new WebscriptParam("sectionid", sectionId) };
            JSONObject jsonObject = webscriptCaller.getJsonObject(scriptUri, Arrays.asList(params));
            JSONObject data;
            if (jsonObject != null)
            {
                data = (JSONObject) jsonObject.get("data");
            }
            else
            {
                throw new ResourceNotFoundException("No response for " + scriptUri);
            }

            AssetCollectionImpl collection = buildCollection(data);

            // Get the list of ids of assets in the collection
            List<String> assetIds = buildRelatedAssetList(data);

            Query query = new Query();
            query.setSectionId(sectionId);
            query.setMaxResults(maxResults);
            query.setResultsToSkip(resultsToSkip);
            collection.setQuery(query);
            collection.setTotalSize(assetIds.size());

            if (assetIds.size() > 0)
            {
                // If this is a paginated query then select the subset of ids
                // for which the assets should be fetched.
                if (maxResults != -1)
                {
                    int end = resultsToSkip + maxResults;
                    assetIds = assetIds.subList(resultsToSkip, end > assetIds.size() ? assetIds.size() : end);
                }

                // Get the actual asset objects.
                List<Asset> assets = assetFactory.getAssetsById(assetIds);
                collection.setAssets(assets);
            }
            return collection;
        }
        catch (JSONException e)
        {
            throw new RuntimeException("Parsing getCollection ws JSON response failed", e);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Error encoding URL", e);
        }
    }

    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }

    public void setWebscriptCaller(WebScriptCaller webscriptCaller)
    {
        this.webscriptCaller = webscriptCaller;
    }
}
