/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 */

/**
 * This mixin provides additional widget filtering capabilities to determine whether or not a widget should be
 * created even if it is defined in the widget model being processed.
 * 
 * @module alfresco/core/WidgetsProcessingFilterMixin
 * @extends module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/ObjectTypeUtils",
        "dojo/_base/array",
        "dojo/_base/lang"], 
        function(declare, AlfCore, ObjectTypeUtils, array, lang) {
   
   return declare([AlfCore], {

      /**
       * Overrides [filterWidget]{@link module:alfresco/core/Core#filterWidget} to check for a "renderFilter" attribute
       * included in the supplied widget configuration. This is then used to determine whether or not the widget
       * should be created or not.
       *
       * @instance
       * @param {object} widgetConfig The configuration for the widget to be created
       * @returns {boolean} The result of the filter evaluation or true if no "renderFilter" is provided
       */
      filterWidget: function alfresco_core_WidgetsProcessingFilterMixin__filterWidget(widgetConfig) {
         var shouldRender = true;
         if (widgetConfig.config && widgetConfig.config.renderFilter)
         {
            // If filter configuration is provided, then switch the default so that rendering will NOT occur...
            shouldRender = false;
      
            // Check that the object has a the supplied property...
            var renderFilterConfig = widgetConfig.config.renderFilter;
            if (!ObjectTypeUtils.isArray(renderFilterConfig))
            {
               this.alfLog("warn", "A request was made to filter a widget, but the filter configuration was not an array", this, widgetConfig);
            }
            else
            {
               // Check that the widget passes all the filter checks...
               // TODO: Should we provide the ability to switch from AND to OR??
               shouldRender = array.every(renderFilterConfig, lang.hitch(this, "processFilterConfig"));
            }
         }
         else
         {
            this.alfLog("log", "A request was made to filter a widget but the configuration does not have a 'config.renderFilter' attribute.", this, widgetConfig);
         }
         return shouldRender;
      },
      
      /**
       * @instance
       * @param {object} renderFilterConfig The filter configuration to process
       * @param {number} index The index of the filter configuration
       * @returns {boolean} True if the filter criteria have been met and false otherwise.
       */
      processFilterConfig: function alfresco_core_WidgetsProcessingFilterMixin__processFilterConfig(renderFilterConfig, index) {
         var passesFilter = false;
         if (this.filterPropertyExists(renderFilterConfig))
         {
            // Compare the property value against the applicable values... 
            var renderFilterProperty = this.getRenderFilterPropertyValue(renderFilterConfig),
                renderFilterValues = this.getRenderFilterValues(renderFilterConfig);
            passesFilter = array.some(renderFilterValues, lang.hitch(this, "processFilter", renderFilterConfig, renderFilterProperty));
         }
         else if (renderFilterConfig.renderOnAbsentProperty == true)
         {
            passesFilter = true;
         }
         else
         {
            this.alfLog("log", "A request was made to filter a widget but the configured filter is not a property of the current item", this, renderFilterConfig);
         }
         return passesFilter;
      },
      
      /**
       * This is called from the [filterWidget]{@link module:alfresco/core/WidgetsProcessingFilterMixin#filterWidget} function 
       * for each acceptable filter value and compares it against the supplied target value.
       * 
       * @instance
       * @param {object} renderFilterConfig The configuration for the filter
       * @param {string|boolean|number} target The target object to match (ideally this should be a string, boolean or a number 
       * @returns {boolean} true If the supplied value matches the target value and false otherwise.
       */
      processFilter: function alfresco_core_WidgetsProcessingFilterMixin__processFilter(renderFilterConfig, target, currValue) {
         if (ObjectTypeUtils.isString(currValue))
         {
            currValue = lang.trim(currValue);
         }
         if (renderFilterConfig.negate == null || renderFilterConfig.negate == false)
         {
            return currValue == target;
         }
         else
         {
            return currValue != target;
         }
      },
      
      /**
       * Checks to see whether or not the supplied filter property is a genuine attribute of the
       * [currentItem]{@link module:alfresco/core/WidgetsProcessingFilterMixin#currentItem}.
       * 
       * @instance
       * @param {{property: string, values: string[]|string}} renderFilterConfig The filter configuration to process.
       * @returns {boolean} true if the property exists and false if it doesn't.
       */
      filterPropertyExists: function alfresco_core_WidgetsProcessingFilterMixin__filterPropertyExists(renderFilterConfig) {
         return (ObjectTypeUtils.isString(renderFilterConfig.property) && ObjectTypeUtils.isObject(this.currentItem) && lang.exists(renderFilterConfig.property, this.currentItem));
      },
      
      /**
       * Processes the "filterProperty" attribute defined in the filter configuration (which is expected to be a dot notation path to an attribute
       * of the [currentItem]{@link module:alfresco/core/WidgetsProcessingFilterMixin#currentItem}. This 
       * property is then retrieved from [currentItem]{@link module:alfresco/core/WidgetsProcessingFilterMixin#currentItem}
       * and returned so that it can be compared against the "values" configuration. Retrieval of the 
       * 
       * @instance
       * @param {{property: string, values: string[]|string}} renderFilter The filter configuration to process.
       * @returns {object} The property of [currentItem]{@link module:alfresco/core/WidgetsProcessingFilterMixin#currentItem} defined
       * by the "property" attribute of the filter configuration.
       */
      getRenderFilterPropertyValue: function alfresco_core_WidgetsProcessingFilterMixin__getRenderFilterPropertyValue(renderFilterConfig) {
         return lang.getObject(renderFilterConfig.property, false, this.currentItem);
      },
      
      /**
       *
       * @instance
       * @param {{property: string, values: string[]|string}} renderFilter The filter configuration to process.
       * @returns {string} The name of the filter
       */
      getCustomRenderFilterProperty: function alfresco_core_WidgetsProcessingFilterMixin__getCustomRenderFilterProperty(currentItem) {
         var result = null;
         if (currentItem instanceof Boolean || typeof currentItem == "boolean")
         {
            result = currentItem ? "folder" : "document";
         }
         return result;
      },
      
      /**
       * Attempt to convert the supplied filter value into an array. Filter values should be configured as an array of
       * strings but this also allows single strings to be used (which are converted into a single element array) but 
       * if all else fails then an empty array will be returned.
       *
       * @instance
       * @param {{property: string, values: string[]|string}} renderFilter The filter configuration to process.
       * @returns {string[]} An array (assumed to be of strings) that is either empty, the same array supplied as an argument or a single
       * string element supplied as an argument.
       */
      getRenderFilterValues: function alfresco_core_WidgetsProcessingFilterMixin__getRenderFilterValues(renderFilter) {
         var result = null;
         if (ObjectTypeUtils.isArray(renderFilter.values))
         {
            result = renderFilter.values;
         }
         else if (ObjectTypeUtils.isString(renderFilter.values))
         {
            result = [renderFilter.values];
         }
         else
         {
            result = [];
         }
         return result;
      }
   });
});