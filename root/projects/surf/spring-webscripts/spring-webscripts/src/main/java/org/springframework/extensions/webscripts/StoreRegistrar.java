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

package org.springframework.extensions.webscripts;

import java.util.ArrayList;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Registers a store into a search path
 * 
 * @author muzquiano
 */
public class StoreRegistrar implements ApplicationContextAware
{
    private static final String WEBSCRIPTS_SEARCHPATH_ID = "webscripts.searchpath";

    private ApplicationContext applicationContext;  
    protected SearchPath searchPath;
    protected Store store;

    protected boolean prepend = false;

    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }

    /**
     * Sets the store
     * 
     * @param store
     */
    public void setStore(Store store)
    {
        this.store = store;
    }

    /**
     * Sets the search path
     * 
     * @param searchPath
     */
    public void setSearchPath(SearchPath searchPath)
    {
        this.searchPath = searchPath;
    }

    /**
     * Identifies whether to prepend or not
     * 
     * @param prepend
     */
    public void setPrepend(boolean prepend)
    {
        this.prepend = prepend;
    }

    /**
     * Overrides the id of the search path to use
     * 
     * @return
     */
    protected String getSearchPathId()
    {
        return WEBSCRIPTS_SEARCHPATH_ID;
    }

    /**
     * Spring init method
     */
    public void init()
    {
        if (searchPath == null)
        {
            searchPath = (SearchPath) getApplicationContext().getBean(getSearchPathId());
        }
        
        plugin(store, searchPath, prepend);        
    }

    /**
     * Plugs the store into the search path
     * 
     * @param store         store
     * @param searchPath    search path
     * @param prepend       whether to prepend or not
     */
    protected void plugin(Store store, SearchPath searchPath, boolean prepend)
    {
        // prepend to the front
        ArrayList<Store> storeList = new ArrayList(searchPath.getStoresInternal());
        
        if (prepend)
        {
            storeList.add(0, store);
        }
        else
        {
            storeList.add(store);
        }
        
        searchPath.setSearchPath(storeList);
    }
}