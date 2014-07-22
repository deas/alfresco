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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;


/**
 * Web Script Storage
 * 
 * @author davidc
 */
public class SearchPath implements ApplicationContextAware, ApplicationListener
{
    private ProcessorLifecycle lifecycle = new ProcessorLifecycle();
    private Collection<Store> searchPath = Collections.emptyList();

    /**
     * @param searchPath
     */
    public void setSearchPath(List<Store> searchPath)
    {
        this.searchPath = searchPath;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        lifecycle.setApplicationContext(applicationContext);
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        lifecycle.onApplicationEvent(event);
    }
    
    /**
     * Hooks into Spring Application Lifecycle
     */
    private class ProcessorLifecycle extends AbstractLifecycleBean
    {
        @Override
        protected void onBootstrap(ApplicationEvent event)
        {
            for (Store store : searchPath)
            {
                store.init();
            }
        }
    
        @Override
        protected void onShutdown(ApplicationEvent event)
        {
        }
    }
    
    /**
     * Gets all Web Script Stores - directly from the underlying list
     * 
     * @return all Web Script Stores in the search path underlying list
     */
    /*package*/ Collection<Store> getStoresInternal()
    {
        return searchPath;
    }

    /**
     * Gets all Web Script Stores
     * 
     * @return  all Web Script Stores
     */
    public Collection<Store> getStores()
    {
        Collection<Store> aliveStores = new ArrayList<Store>(searchPath.size());
        for (Store store : searchPath)
        {
            if (store.exists())
            {
                aliveStores.add(store);
            }
        }
        return aliveStores;
    }

    /**
     * Gets the Web Script Store for the given Store path
     * 
     * @param storePath
     * @return  store (or null, if not found)
     */
    public Store getStore(String storePath)
    {
        Collection<Store> stores = getStores();
        for (Store store : stores)
        {
            if (store.getBasePath().equals(storePath))
            {
                return store;
            }
        }
        return null;
    }
    
    /**
     * Determines if the document exists anywhere on the search path
     * 
     * @param documentPath  document path
     * @return  true => exists, false => does not exist
     * @throws IOException 
     */
    public boolean hasDocument(String documentPath) throws IOException
    {
       for (Store store : getStores())
       {
           if (store.hasDocument(documentPath))
           {
               return true;
           }
       }
       
       return false;
    }

    /**
     * Gets a document from anywhere on the search path. Note a raw InputStream to the
     * content is returned and must be closed by the accessing method.
     * 
     * @param documentPath  document path
     * @return input stream onto document or null if it
     *         does not exist on the search path
     * 
     * @throws IOException
     */
    public InputStream getDocument(String documentPath) throws IOException
    {
       for (Store store : getStores())
       {
           if (store.hasDocument(documentPath))
           {
               return store.getDocument(documentPath);
           }
       }
       
       return null;
    }
}
