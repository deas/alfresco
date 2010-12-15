/*-----------------------------------------------------------------------------
*  Copyright 2007-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    CacheControlFilterInfoBean.java
*----------------------------------------------------------------------------*/

package org.alfresco.filter;

import java.util.*;

public class CacheControlFilterInfoBean
{
    // Spring creates a LinkedHashMap, so rules order is preserved
    private Map<String,String> cacheControlRules_; 

    public void setCacheControlRules( Map<String,String> cacheControlRules )
    {
        cacheControlRules_ = cacheControlRules;
    }

    public Set<Map.Entry<String,String>> getCacheControlRulesEntrySet() 
    { 
        return ( Set<Map.Entry<String,String>>) cacheControlRules_.entrySet();
    }
}


