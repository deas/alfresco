/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
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

package org.springframework.extensions.webscripts.servlet.mvc;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Base View Resolver class providing an improved caching strategy over the basic
 * SpringMVC UrlBasedViewResolver. Removes the need to synchronize on the view cache
 * object for each lookup, also caches "null" view lookups which in reality happen
 * more often than not for most view resolvers.
 * 
 * @author Kevin Roast
 */
public abstract class AbstractWebScriptViewResolver extends UrlBasedViewResolver
{
    /** Map from view key to View instance */
    private final Map<Object, View> viewCache = new ConcurrentHashMap<Object, View>(128);
    
    /** True if the resolver should cache "null" lookups */
    protected boolean useNullSentinel = true;
    
    /**
     * Override the SpringMVC default caching strategy with one that uses a ConcurrentHashMap impl.
     */
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception
    {
        if (!isCache())
        {
            return createView(viewName, locale);
        }
        else
        {
            final Object cacheKey = getCacheKey(viewName, locale);
            View view = this.viewCache.get(cacheKey);
            if (view == null)
            {
                // Ask the subclass to create the View object.
                view = createView(viewName, locale);
                if (view == null && this.useNullSentinel)
                {
                    view = ViewSentinel.getInstance();
                    this.viewCache.put(cacheKey, view);
                }
            }
            return view != ViewSentinel.getInstance() ? view : null;
        }
    }
    
    /**
     * Override the SpringMVC default caching strategy with one that uses a ConcurrentHashMap impl.
     */
    @Override
    public void removeFromCache(String viewName, Locale locale)
    {
        if (isCache())
        {
            final Object cacheKey = getCacheKey(viewName, locale);
            this.viewCache.remove(cacheKey);
        }
    }

    /**
     * Override the SpringMVC default caching strategy with one that uses a ConcurrentHashMap impl.
     */
    @Override
    public void clearCache()
    {
        this.viewCache.clear();
    }
    
    /**
     * Sentinel object used for managing "null" view lookups in the view cache.
     */
    private static class ViewSentinel implements View
    {
        private static ViewSentinel instance = new ViewSentinel();
        
        private ViewSentinel()
        {
        }
        
        static ViewSentinel getInstance()
        {
            return instance;
        }
        
        public String getContentType()
        {
            return null;
        }
        
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
                throws Exception
        {
        }
    }
}