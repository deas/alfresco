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

package org.springframework.extensions.config.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.config.ConfigElement;

/**
 * Adapter class for implementing ConfigElement's. Extend this class and 
 * provide the implementation specific behaviour.
 * 
 * @author gavinc
 */
public abstract class ConfigElementAdapter implements ConfigElement
{
    private static final long serialVersionUID = -2301220755382165891L;
    protected String name;
    protected String value;
    protected Map<String, String> attributes;
    protected List<ConfigElement> children;

    /**
     * Default constructor
     * 
     * @param name Name of the config element
     */
    public ConfigElementAdapter(String name)
    {
        this.name = name;
        this.attributes = new HashMap<String, String>();
        this.children = new ArrayList<ConfigElement>();
    }

    public String getAttribute(String name)
    {
        return attributes.get(name);
    }

    public Map<String, String> getAttributes()
    {
        return Collections.unmodifiableMap(this.attributes);
    }
    
    public int getAttributeCount()
    {
       return this.attributes.size();
    }

    public List<ConfigElement> getChildren()
    {
        return Collections.unmodifiableList(this.children);
    }

    public List<ConfigElement> getChildren(String name)
    {
       List<ConfigElement> result = new LinkedList<ConfigElement>();
       
       if (hasChildren())
       {
          for (ConfigElement ce : this.children)
          {
             if (ce.getName().equals(name))
             {
                result.add(ce);
             }
          }
       }
       
       return Collections.unmodifiableList(result);
    }
    
    public int getChildCount()
    {
       return this.children.size();
    }

    public ConfigElement getChild(String name)
    {
       ConfigElement child = null;
       
       if (hasChildren())
       {
          for (ConfigElement ce : this.children)
          {
             if (ce.getName().equals(name))
             {
                child = ce;
                break;
             }
          }
       }
       
       return child;
    }
    
    public String getChildValue(String name)
    {
       ConfigElement ce = getChild(name);
       return ce != null ? ce.getValue() : null;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<ConfigElement>> getChildrenMap()
    {
       Map<String, List<ConfigElement>> map = new LinkedHashMap<String, List<ConfigElement>>();
       
       if (hasChildren())
       {
          for (ConfigElement ce : this.children)
          {
             String name = ce.getName();
             if (map.containsKey(name))
             {
                List list = map.get(name);
                list.add(ce);
             }
             else
             {
                List<ConfigElement> list = new ArrayList<ConfigElement>();
                list.add(ce);
                map.put(name, list);
             }
          }
       }
       
       return map;
    }
    
    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }

    /**
     * Sets the value of this config element
     * 
     * @param value The value to set.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    public boolean hasAttribute(String name)
    {
        return attributes.containsKey(name);
    }

    public boolean hasChildren()
    {
        return !children.isEmpty();
    }

    public String toString()
    {
        StringBuilder buffer = new StringBuilder(super.toString());
        buffer.append(" (name=").append(this.name).append(")");
        return buffer.toString();
    }
    
    public abstract ConfigElement combine(ConfigElement configElement);
}
