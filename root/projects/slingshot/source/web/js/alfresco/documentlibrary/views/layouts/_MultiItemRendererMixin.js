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
 * This mixin provides the capability to a widget to iterate over the data that is returned by making
 * a request to the Alfresco Repository for a list of nodes to be displayed in a Document Library. This
 * should be mixed into any widget that will process a JSON model of widgets because it extends the
 * [createWidget]{@link module:alfresco/core/Core#createWidget} function to pass the details of the item
 * that is currently being rendered to all of its processed widgets. It also overrides the default 
 * [allWidgetsProcessed]{@link module:alfresco/core/Core#allWidgetsProcessed} function to make a call
 * to render the next item in the [currentData]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentData}
 * attribute (if applicable).
 * 
 * @module alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin
 * @extends module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/ObjectTypeUtils",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "dojo/dom-class",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/dom-style"], 
        function(declare, AlfCore, ObjectTypeUtils, _AlfDocumentListTopicMixin, domClass, array, lang, domStyle) {
   
   return declare([AlfCore, _AlfDocumentListTopicMixin], {

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/_MultiItemRendererMixin.css"}]
       */
      cssRequirements: [{cssFile:"./css/_MultiItemRendererMixin.css"}],
      
      /**
       * This should be set to the data to iterate over. This should be an object that contains
       * an "items" attribute.
       * 
       * @instance
       * @type {{items: Object}[]|null}
       * @default null
       */
      currentData: null,
      
      /**
       * The index of the item in [currentData]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentData}
       * items array that is currently being rendered
       * 
       * @instance
       * @type {number|null} currentIndex The current index within the current data
       * @default null
       */
      currentIndex: null,
      
      /**
       * The current item being rendered
       * 
       * @instance
       * @type {Object|null} 
       * @default null
       */
      currentItem: null,
      
      /**
       * A setter for [currentData]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentData}
       * @instance
       * @param {Object} data The data to set
       */
      setData: function alfresco_documentlibrary_views_AlfDocumentListView__setData(data) {
         this.currentData = data;
      },
      
      /**
       * This function should be called to iterate over new data.
       * It sets the currentData object and resets the index back to zero. When [processWidgets]{@link module:alfresco/core/Core#processWidgets}
       * function is called it will being set [currentItem]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentItem} 
       * as the first element in the items attribute belonging to [currentData]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentData}
       * 
       * @instance
       */
      renderData: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__renderData() {
         
         // Ensure that an array is created to hold the root widget subscriptions...
         if (this.rootWidgetSubscriptions == null)
         {
            this.rootWidgetSubscriptions = [];
         }
         // Iterate over any previously created subscriptions for last data set and remove them...
         // It is important that this it carried out to clean up the previous data set. By default
         // the only subscriptions that will be present are those for selecting items, but extending
         // classes could have added additional subscriptions. If the subscriptions aren't cleaned 
         // up properly then destroyed widgets will try to be actioned.
         array.forEach(this.rootWidgetSubscriptions, function(handle, i) {
            if (typeof handle.remove === "function")
            {
               handle.remove();
            }
         });
         
         if (this.currentData && this.currentData.items)
         {
            this.alfLog("log", "Rendering data", this.currentData.items);
            this.currentIndex = 0;
            this.currentItem = this.currentData.items[this.currentIndex];
            
            array.forEach(this.currentData.items, lang.hitch(this, "renderNextItem"));
            this.allItemsRendered();
         }
         else
         {
            this.alfLog("warn", "No data to render!");
         }
      },
      
      /**
       * This is the widget that acts as the root of the view. By default this will
       * be instantiated as a [Table]{@link module:alfresco/documentlibrary/views/layoutTable}.
       * 
       * @instance
       * @type {Object} 
       * @default
       */
      rootViewWidget: null,
      
      /**
       * Calls [processWidgets]{@link module:alfresco/core/Core#processWidgets} to instantiate the widgets
       * defined in the JSON model for [currentItem]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentItem}
       * @instance
       */
      renderNextItem: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__renderNextItem() {
         // Process the widgets defined using the current item as the data to go into those widgets...
         this.alfLog("log", "Rendering item", this.currentData.items[this.currentIndex]);
         
         // Set a width if provided...
         if (this.width != null)
         {
            domStyle.set(this.domNode, "width", this.width);
         }
         
         if (this.containerNode != null)
         {
            this.processWidgets(this.widgets, this.containerNode);
         }
         else
         {
            this.alfLog("warn", "There is no 'containerNode' for adding an item to");
         }
      },
      
      /**
       * Overrides the default implementation to start the rendering of the next item.
       * 
       * @instance
       * @param {Object[]}
       */
      allWidgetsProcessed: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__allWidgetsProcessed(widgets) {
         
         // Increment the current index and check to see if there are more items to render...
         // Only the root widget(s) will have the currentData object set so we don't start rendering the next item
         // on nested widgets...
         this.currentIndex++;
         if (this.currentData && 
             this.currentData.items &&
             this.currentData.items.length != null)
         {
            array.forEach(widgets, lang.hitch(this, "rootWidgetProcessing"));
            if (this.currentIndex < this.currentData.items.length)
            {
               // Render the next item...
               this.currentItem = this.currentData.items[this.currentIndex];
            }
         }
         else
         {
            // TODO: We need to make sure that we're able to stop rendering if another request arrives before we've completed
         }
      },
      
      /**
       * @instance
       */
      allItemsRendered: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__allItemsRendered() {
         // No action by default.
      },
      
      /**
       * @instance
       * @type {object[]}
       * @default null
       */
      rootWidgetSubscriptions: null,
      
      /**
       * Adds the "alfresco-documentlibrary-views-layout-_MultiItemRendererMixin--item" class to the root DOM node
       * of the widget and additionally subscribes to item selection publications so that additional CSS classes
       * can be added when an item is selected (so that they can be visually indicate selection).
       * 
       * @instance
       * @param {object} widget The widget to add the styling to
       * @param {number} index The index of the widget
       */
      rootWidgetProcessing: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__rootWidgetProcessing(widget, index) {
         domClass.add(widget.domNode, "alfresco-documentlibrary-views-layout-_MultiItemRendererMixin--item");
         this.rootWidgetSubscriptions.push(this.alfSubscribe(this.documentSelectedTopic, lang.hitch(this, "onItemSelection", widget)));
         this.rootWidgetSubscriptions.push(this.alfSubscribe(this.documentDeselectedTopic, lang.hitch(this, "onItemDeselection", widget)));
      },
      
      /**
       * Adds the "selected" CSS class to the root widget if it has been selected.
       * 
       * @instance
       * @param {object} payload The details of the selected item
       */
      onItemSelection: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__onItemSelection(widget, payload) {
         if (this.compareItems(widget.currentItem, payload.value))
         {
            domClass.add(widget.domNode, "selected");
         }
      },

      /**
       * Removes the "selected" CSS class to the root widget if it has been de-selected.
       * 
       * @instance
       * @param {object} payload The details of the selected item
       */
      onItemDeselection: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__onItemDeselection(widget, payload) {
         if (this.compareItems(widget.currentItem, payload.value))
         {
            domClass.remove(widget.domNode, "selected");
         }
      },

      /**
       * Compares the nodeRef attribute of both item arguments. This has been abstracted to a separate function to
       * allow simpler overriding when comparing items. This function is called by the
       * [onItemSelection]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#onItemSelection}
       * function to determine whether the item currently selected is the item represented by the current widget. 
       * 
       * @instance
       * @param {object} item1 The first item to compare
       * @param {object} item2 The second item to compare
       * @returns {boolean} true if the items are the same and false otherwise.
       */
      compareItems: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__compareItems(item1, item2) {
         return (item1.nodeRef != null && (item1.nodeRef == item2.nodeRef));
      },
      
      /**
       * Overrides the default implementation of create widget to update the widget configuration with the
       * current item being rendered. This ensures that each widget has access to all the data about that
       * item.
       * 
       * @instance 
       * @param {Object} config The configuration to pass as an instantiation argument to the widget
       * @param {DOM Element} domNode The DOM node to attach the widget to
       * @param {function} callback A function to call once the widget has been instantiated
       * @param {Array} callbackArgs An array of arguments to pass to the callback function
       */
      createWidget: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__createWidget(config, domNode, callback, callbackArgs) {
         // Only create a widget if there is data to create it with
         if (config == null || config.config == null)
         {
            config.config = {};
         }
         if (this.currentItem != null)
         {
            // This checks if the "jsNode" attribute has been created, and if not will make an attempt
            // to create it. This is in place purely for handling node based items, but shouldn't
            // break anything else...
            if (typeof this.currentItem.jsNode === "undefined" && this.currentItem.node != null)
            {
               this.currentItem.jsNode = new Alfresco.util.Node(this.currentItem.node);
            }
            config.config.currentItem = this.currentItem;
            this.inherited(arguments);
         }
      },
      
      /**
       * Overrides [filterWidget]{@link module:alfresco/core/Core#filterWidget} to check for a "renderFilter" attribute
       * included in the supplied widget configuration. This is then used to determine whether or not the widget
       * should be created for the [currentItem]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentItem}
       * being rendered.
       *
       * @instance
       * @param {object} widgetConfig The configuration for the widget to be created
       * @returns {boolean} The result of the filter evaluation or true if no "renderFilter" is provided
       */
      filterWidget: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__filterWidget(widgetConfig) {
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
       */
      processFilterConfig: function(renderFilterConfig, index) {
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
       * This is called from the [filterWidget]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#filterWidget} function 
       * for each acceptable filter value and compares it against the supplied target value.
       * 
       * @instance
       * @param {object} renderFilterConfig The configuration for the filter
       * @param {string|boolean|number} target The target object to match (ideally this should be a string, boolean or a number 
       * @returns {boolean} true If the supplied value matches the target value and false otherwise.
       */
      processFilter: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__processFilter(renderFilterConfig, target, currValue) {
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
       * [currentItem]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentItem}.
       * 
       * @instance
       * @param {{property: string, values: string[]|string}} renderFilterConfig The filter configuration to process.
       * @returns {boolean} true if the property exists and false if it doesn't.
       */
      filterPropertyExists: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__filterPropertyExists(renderFilterConfig) {
         return (ObjectTypeUtils.isString(renderFilterConfig.property) && ObjectTypeUtils.isObject(this.currentItem) && lang.exists(renderFilterConfig.property, this.currentItem));
      },
      
      /**
       * Processes the "filterProperty" attribute defined in the filter configuration (which is expected to be a dot notation path to an attribute
       * of the [currentItem]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentItem}. This 
       * property is then retrieved from [currentItem]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentItem}
       * and returned so that it can be compared against the "values" configuration. Retrieval of the 
       * 
       * @instance
       * @param {{property: string, values: string[]|string}} renderFilter The filter configuration to process.
       * @returns {object} The property of [currentItem]{@link module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin#currentItem} defined
       * by the "property" attribute of the filter configuration.
       */
      getRenderFilterPropertyValue: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__getRenderFilterPropertyValue(renderFilterConfig) {
         return lang.getObject(renderFilterConfig.property, false, this.currentItem);
      },
      
      /**
       *
       * @instance
       * @param {{property: string, values: string[]|string}} renderFilter The filter configuration to process.
       * @returns {string} The name of the filter
       */
      getCustomRenderFilterProperty: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__getCustomRenderFilterProperty(currentItem) {
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
      getRenderFilterValues: function alfresco_documentlibrary_views_layout__MultiItemRendererMixin__getRenderFilterValues(renderFilter) {
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