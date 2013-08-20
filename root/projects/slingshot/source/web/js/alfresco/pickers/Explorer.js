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
 * @module alfresco/pickers/Explorer
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Explorer.html",
        "dojo/text!./templates/ExplorerTab.html",
        "dojo/text!./templates/ExplorerItem.html",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "dojo/on",
        "dojo/string",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/dom-construct",
        "dojo/dom-style"], 
        function(declare, _WidgetBase, _TemplatedMixin, template, tabTemplate, itemTemplate, AlfCore, CoreXhr, on, stringUtil, array, lang, domConstruct, domStyle) {
   
   return declare([_WidgetBase, _TemplatedMixin, AlfCore, CoreXhr], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/SlidingTabs.css"}]
       */
      cssRequirements: [{cssFile:"./css/Explorer.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * This is used to keep track of all the locations in the hierarchy based on the current path.
       * @instance
       * @type {object}
       */
      currentLocations: null,
      
      /**
       * @instance
       * @type {string}
       * @default ""
       */
      nodeRef: "alfresco/company/home",
      
      /**
       * @instance
       */
      postCreate: function alfresco_pickers_Explorer__postCreate() {
         
         this.currentLocations = [];
         domStyle.set(this.contentFrameNode, "width", this.contentFrameWidth + "px");
         
         // 1. Get some data, call the URL to get some data to display
         // 2. Iterate over the results build a content page for each level.
         
         this.serviceXhr({
            url: Alfresco.constants.PROXY_URI + "api/forms/picker/node/" + this.nodeRef + "/children",
            data: {
               selectableType: "cm:content",
               searchTerm: "",
               size: "1000"
            },
            method: "GET",
            successCallback: this.processLocations,
            callbackScope: this
         });
      },
      
      /**
       * @instance
       * @param {object} response
       * @param {object} originalRequestConfig
       */
      processLocations: function alfresco_pickers_Explorer__processLocations(response, originalRequestConfig) {
         this.appendLocation(response.data.parent, response.data.items);
      },
      
      /**
       * The width of content frame. This frame will "clip" all of the content items so that only one item is shown
       * at a time. The overall width of the node containing the tabs will be equal to this value multiplied by the
       * number of tabs.
       * 
       * @instance
       * @type {number}
       * @default 200
       */
      contentFrameWidth: 500,
      
      /**
       * The attribute of the location data to use as the unique identifier of each location.
       * This defaults to "nodeRef" as it is assumed that each location will be a Node.
       * 
       * @instance
       * @type {string}
       * @default "nodeRef
       */
      idAttr: "nodeRef",
      
      /**
       * The attribute of the location data that should be used as the display value for the location. This
       * will primarily be used in the location tab that gets created.
       * 
       * @instance
       * @type {string}
       * @default "name"
       */
      locationDisplayAttr: "name",
      
      /**
       * Brings the location page into the content frame to display it as the current location.
       * Removes all child locations.
       * 
       * @instance
       * @param {string} locationId The id of the location to select
       */
      selectLocation: function alfresco_pickers_Explorer__selectLocation(locationId) {
         
         // Create a new array to keep track of the locations to be removed. We need
         // to remove the locations after iterating through the array and remove them
         // last first to prevent generating indexing errors...
         var locationsToRemove = [];
         
         var removeLocation = false;
         array.forEach(this.currentLocations, function(location, index) {

            // Remove the current location if the target location has already 
            // been identified. This check needs to be done BEFORE seeing if the
            // current location is the one to be removed otherwise the target
            // location would also get removed!
            if (removeLocation)
            {
               // Insert the location at the beginning of the array so that the last location
               // is removed first...
               locationsToRemove.splice(0,0,location);
            }

            if (location.id == locationId)
            {
               // TODO: Bring the current location into focus
               
               // Set the flag to ensure that all subsequent locations iterated over
               // are removed...
               removeLocation = true;
               this.focusLocation(location);
            }
         }, this);
         
         // Remove locations as required...
         // TODO: Consider adding a timeout here as the nodes are removed before the transition
         // animation effect completes
         // The duration of the timeout should match transition duration in the CSS file...
         setTimeout(lang.hitch(this, "removeLocations", locationsToRemove), 1000);
         
      },
      
      /**
       * @instance
       */
      removeLocations: function alfresco_pickers_Explorer__removeLocations(locations) {
         array.forEach(locations, function(location, index) {
            this.removeLocation(location);
         }, this);
      },
      
      /**
       * Brings the supplied location into focus. This is done by "sliding" the location content
       * node directly within the content frame so that it is displayed. The effect is achieved by
       * changing the position of the content node and allowing the browser to take care of the
       * slide effect using CSS3 transitions.
       * 
       * @instance
       * @param {object} location The location to focus.
       */
      focusLocation: function alfresco_pickers_Explorer__focusLocation(location) {
         
         // Update the content items node to place the currently selected content within the frame...
         var left = "-" + ((location.index) * this.contentFrameWidth) + "px";
         domStyle.set(this.contentItemsNode, "left", left);
      },
      
      /**
       * Appends a new location to the end of the set of current locations. 
       * 
       * @instance  
       * @param {object} locationData Provides information about the location to appends
       * @param {object} items The items contained within the location
       */
      appendLocation: function alfresco_pickers_Explorer__appendLocation(locationData, items) {
         
         if (items == null)
         {
            // If there are no items then we know that although we add the location we don't
            // yet know what it contains. We need to mark the location so that we know that
            // we need to get the data before we display it.
         }
         else
         {
            // Process the items to fill the content...
         }

         // Calculate the index...
         // Use the current length of the currentLocations array because the index will be
         // counted from zero (no need to increment)...
         var index = this.currentLocations.length;

         // Make the content-items node grow to accommodate a new item...
         domStyle.set(this.contentItemsNode, "width", (this.contentFrameWidth * (index + 1)) + "px");
         
         // Get the location id from the location data...
         var locationId = locationData[this.idAttr];
         
         // Increase the size of the content node
         // Add the location tab...
         var locationTab = this.createLocationTab(locationData, index);
         on(locationTab, "click", lang.hitch(this, "selectLocation", locationId));
         domConstruct.place(locationTab, this.navigationNode);
         
         // Add the content tab...
         var locationContent = this.createLocationContent(locationData, items);
         domConstruct.place(locationContent, this.contentItemsNode);
         
         var location = {
            id: locationId,
            index: index,
            data: locationData,
            node: locationContent,
            tab: locationTab
         };
         this.currentLocations.push(location);
         this.focusLocation(location);
      },
      
      /**
       * @instance
       * @param {object} location The location to remove
       */
      removeLocation: function alfresco_pickers_Explorer__removeLocation(location) {
         // TODO: This assumes there are no widgets just DOM nodes
         // TODO: Animation before removal
         this.contentItemsNode.removeChild(location.node);
         this.navigationNode.removeChild(location.tab);
         this.currentLocations.splice(location.index, 1);
      },
      
      /**
       * CSS class or classes to apply to each location tab.
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      tabClass: "",
      
      /**
       * Creates a DOM element to use as a tab for selecting a location (this can be thought of
       * as being a breadcrumb in a breadcrumb trail).
       * 
       * @instance
       * @param {object} locationData The data for the location to create a tab for.
       * @returns {Element} A DOM element representing a new tab.
       */
      createLocationTab: function alfresco_pickers_Explorer__createLocationTab(locationData, index) {
         // Create the 
         var locationTab = domConstruct.toDom(stringUtil.substitute(tabTemplate, {
            tabClass: this.tabClass,
            index: index,
            title: locationData[this.locationDisplayAttr]
         }));
         return locationTab;
      },
      
      /**
       * The attribute to use from content items as the display value.
       *  
       * @instance
       * @type {string}
       * @default "name"
       */
      itemDisplayAttr: "name",
      
      /**
       * @instance
       * @param {object} locationData The data for the location to create the content for
       * @param {object[]} items The items that exist in the location
       */
      createLocationContent: function alfresco_pickers_Explorer__createLocationContent(locationData, items) {
         var contentItemNode = domConstruct.create("div", {className: "content-item", style: {width: this.contentFrameWidth + "px"}});
         
         array.forEach(items, function(item, i) {
            var itemNode = domConstruct.create("div", { innerHTML: item[this.itemDisplayAttr]}, contentItemNode);
//            var itemNode = domConstruct.toDom(stringUtil.substitute(itemTemplate, {
//               itemName: item[this.itemDisplayAttr]
//            }));
            if (item.isContainer == true)
            {
               on(itemNode, "click", lang.hitch(this, "getLocation", item[this.idAttr]));
            }
         }, this);
         
         return contentItemNode;
      },
      
      /**
       * @instance
       * @param {string} locationId The location to retrieve the data for (this is assumed to be a nodeRef)
       */
      getLocation: function alfresco_pickers_Explorer__getLocation(locationId) {
         this.serviceXhr({
            url: Alfresco.constants.PROXY_URI + "api/forms/picker/node/" + locationId.replace(/:\//g, "") + "/children",
            data: {
               selectableType: "cm:content",
               searchTerm: "",
               size: "1000"
            },
            method: "GET",
            successCallback: this.processLocations,
            callbackScope: this
         });
      }
   });
});