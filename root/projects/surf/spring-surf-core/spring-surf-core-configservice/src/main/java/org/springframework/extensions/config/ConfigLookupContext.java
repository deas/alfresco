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

package org.springframework.extensions.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to hold the context for a config lookup. 
 * 
 * @author gavinc
 */
public class ConfigLookupContext
{
   private boolean includeGlobalSection = true;
   private List<String> areas = new ArrayList<String>();
   private ConfigLookupAlgorithm algorithm;
   
   /**
    * Default constructor
    */
   public ConfigLookupContext()
   {
      this.algorithm = new DefaultLookupAlgorithm();
   }
   
   /**
    * Constructs a lookup context for the given area
    * 
    * @param area The area to search in
    */
   public ConfigLookupContext(String area)
   {
      this();
      this.addArea(area);
   }
   
   /**
    * Constructs a lookup context for the list of the given areas
    * 
    * @param areas The list of areas to search in
    */
   public ConfigLookupContext(List<String> areas)
   {
      this();
      this.setAreas(areas);
   }

   /**
    * @return Returns the lookup algorithm, uses the default implementation if a 
    *         custom algorithm is not supplied
    */
   public ConfigLookupAlgorithm getAlgorithm()
   {
      return this.algorithm;
   }

   /**
    * @param algorithm Sets the lookup algorithm to use
    */
   public void setAlgorithm(ConfigLookupAlgorithm algorithm)
   {
      this.algorithm = algorithm;
   }

   /**
    * @return Returns the list of areas to search within
    */
   public List<String> getAreas()
   {
      return this.areas;
   }

   /**
    * @param areas Sets the lists of areas to search within
    */
   public void setAreas(List<String> areas)
   {
      this.areas = areas;
   }
   
   /**
    * @param area Adds the area to the list of areas to be searched
    */
   public void addArea(String area)
   {
      this.areas.add(area);
   }

   /**
    * @return Determines whether the global section should be included in the 
    *         results, true by default
    */
   public boolean includeGlobalSection()
   {
      return this.includeGlobalSection;
   }

   /**
    * @param includeGlobalSection Sets whether the global section will be 
    *        included in the results
    */
   public void setIncludeGlobalSection(boolean includeGlobalSection)
   {
      this.includeGlobalSection = includeGlobalSection;
   }
}
