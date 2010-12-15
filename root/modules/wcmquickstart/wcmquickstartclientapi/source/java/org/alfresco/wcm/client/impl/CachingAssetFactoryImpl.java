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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.Rendition;
import org.alfresco.wcm.client.SearchResults;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.WebSiteService;
import org.alfresco.wcm.client.impl.cache.SimpleCache;

/**
 * A proxying implementation of the {@link AssetFactory} interface that caches loaded assets
 * @author Brian
 *
 */
public class CachingAssetFactoryImpl implements AssetFactory
{
//    private static final Log log = LogFactory.getLog(CachingAssetFactoryImpl.class);
    
    private AssetFactory delegate;
    private SimpleCache<String, CacheEntry> newCache;
    private long minimumCacheMilliseconds = 30000L;

    public void setDelegate(AssetFactory delegate)
    {
        this.delegate = delegate;
    }

    public void setCache(SimpleCache<String, CacheEntry> newCache)
    {
        this.newCache = newCache;
    }
    
    public void setMinimumCacheSeconds(int seconds)
    {
        minimumCacheMilliseconds = seconds * 1000L;
    }

    public SearchResults findByQuery(Query query)
    {
        return delegate.findByQuery(query);
    }

    public Asset getAssetById(String id, boolean deferredLoad)
    {
        long now = System.currentTimeMillis();
        long refreshCutoffTime = now - getMinimumCacheMilliseconds();
        CacheEntry cacheEntry = newCache.get(id);
        Asset asset = null;
        if (cacheEntry != null)
        {
            //We have found the asset in the cache. How long has it been there? If it has been there 
            //longer than the minimum cache time then we'll check its modified time in the repo
            //to ensure it hasn't become out of date
            asset = cacheEntry.asset;
            if (cacheEntry.cacheTime < refreshCutoffTime)
            {
                Date currentModifiedTime = delegate.getModifiedTimeOfAsset(id);
                Date cachedModifiedTime = (Date)asset.getProperty(Asset.PROPERTY_MODIFIED_TIME);
                if (currentModifiedTime.after(cachedModifiedTime))
                {
                    //This asset has been updated in the repo, so flush this asset from the cache and
                    //forget we ever found it there...
                    newCache.remove(id);
                    asset = null;
                }
                else
                {
                    //The asset has not been modified in the repo since we cached it, so we don't 
                    //have to check it again until the minimum cache time has expired again...
                    cacheEntry.cacheTime = now;
                }
            }
        }
        if (asset == null)
        {
            //We have not found the asset in the cache. Load it using our delegated factory and cache the result
            asset = delegate.getAssetById(id, deferredLoad);
            newCache.put(id, new CacheEntry(asset));
        }
        return asset;
    }

    public Asset getAssetById(String id)
    {
        return getAssetById(id, false);
    }

    public List<Asset> getAssetsById(Collection<String> ids, boolean deferredLoad)
    {
        List<String> idsToLoad = new ArrayList<String>(ids.size());
        Map<String,Asset> assetsToCheck = new TreeMap<String, Asset>();
        Map<String, Asset> foundAssets = new TreeMap<String, Asset>();
        
        long now = System.currentTimeMillis();
        long refreshCutoffTime = now - getMinimumCacheMilliseconds();

        for (String id : ids)
        {
            //For each id that we've been given, see if we have the corresponding
            //asset in our cache.
            CacheEntry cacheEntry = newCache.get(id);
            if (cacheEntry != null)
            {
                //If we find it, work out whether its necessary to check its modified time in the repo.
                //This is the case if we last checked it longer ago than the "minimumCacheMilliseconds"
                if (cacheEntry.cacheTime < refreshCutoffTime)
                {
                    //Yes, we need to check this one. Record it in our collection of assets to check
                    assetsToCheck.put(id, cacheEntry.asset);
                }
                else
                {
                    //No, our cached copy hasn't reached its minimum age yet
                    foundAssets.put(id, cacheEntry.asset);
                }
            }
            else
            {
                idsToLoad.add(id);
            }
        }

        //Check the modified time of those assets found in the cache
        if (!assetsToCheck.isEmpty())
        {
            //Get the modified times from the repo for the assets we need to check
            Map<String,Date> currentModifiedTimes = delegate.getModifiedTimesOfAssets(assetsToCheck.keySet());
            for (Map.Entry<String,Date> currentAssetModifiedTime : currentModifiedTimes.entrySet())
            {
                String assetId = currentAssetModifiedTime.getKey();
                Asset cachedAsset = assetsToCheck.get(assetId);
                Date currentModifiedTime = currentAssetModifiedTime.getValue();
                Date cachedModifiedTime = (Date)cachedAsset.getProperty(Asset.PROPERTY_MODIFIED_TIME);
                if (currentModifiedTime.after(cachedModifiedTime))
                {
                    //This one has been modified since we cached it. Remove it from our cache and add it
                    //to our list of assets to load
                    newCache.remove(assetId);
                    idsToLoad.add(assetId);
                }
                else
                {
                    //This one hasn't been modified since we cached it, so we can use the cached one.
                    foundAssets.put(assetId, cachedAsset);
                    CacheEntry cachedEntry = newCache.get(assetId);
                    if (cachedEntry != null)
                    {
                        //Reset the cache time on the cached asset - we don't need to check it again until
                        //the minimum cache time expires on it again
                        cachedEntry.cacheTime = now;
                    }
                }
            }
        }
        
        //Load any that we haven't found in the cache (or that have been modified since being cached)
        if (!idsToLoad.isEmpty())
        {
            List<Asset> assets = delegate.getAssetsById(idsToLoad, deferredLoad);
            for (Asset asset : assets)
            {
                foundAssets.put(asset.getId(), asset);
                newCache.put(asset.getId(), new CacheEntry(asset));
            }
        }
        
        //Try to retain the correct order as given to us in the originally supplied collection of ids...
        List<Asset> finalResults = new ArrayList<Asset>(foundAssets.size());
        for (String id : ids)
        {
            Asset asset = foundAssets.get(id);
            if (asset != null)
            {
                finalResults.add(asset);
            }
        }
        
        return finalResults;
    }

    public List<Asset> getAssetsById(Collection<String> ids)
    {
        return getAssetsById(ids, false);
    }

    public Date getModifiedTimeOfAsset(String assetId)
    {
        return delegate.getModifiedTimeOfAsset(assetId);
    }

    public Map<String, Date> getModifiedTimesOfAssets(Collection<String> assetIds)
    {
        return delegate.getModifiedTimesOfAssets(assetIds);
    }

    public Map<String, Rendition> getRenditions(String assetId)
    {
        return delegate.getRenditions(assetId);
    }

    public Asset getSectionAsset(String sectionId, String assetName, boolean wildcardsAllowedInName)
    {
        return delegate.getSectionAsset(sectionId, assetName, wildcardsAllowedInName);
    }

    public Asset getSectionAsset(String sectionId, String assetName)
    {
        return delegate.getSectionAsset(sectionId, assetName);
    }

    public Map<String, List<String>> getSourceRelationships(String assetId)
    {
        return delegate.getSourceRelationships(assetId);
    }
    
    private static class CacheEntry
    {
        public long cacheTime;
        public final Asset asset;
        
        public CacheEntry(Asset asset)
        {
            this.asset = asset;
            this.cacheTime = System.currentTimeMillis();
        }
    }
    
    private long getMinimumCacheMilliseconds()
    {
        long result = 0L;
        WebSite currentSite = WebSiteService.getThreadWebSite();
        if (currentSite == null || !currentSite.isEditorialSite())
        {
            result = minimumCacheMilliseconds;
        }
        return result;
    }
}
