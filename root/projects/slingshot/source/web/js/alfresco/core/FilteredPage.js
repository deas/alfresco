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
 * This can be used as the root page widget, but it performs an XHR request to retrieve information about the
 * group membership and relevant permissions for the current user. This information is then procesed against 
 * widget filtering rules to determine whether or not the widgets should be displayed. This allows the page 
 * to vary according to role and permissions.
 * 
 * @module alfresco/core/FilteredPage
 * @extends module:alfresco/core/Page
 * @author Dave Draper
 */
define(["alfresco/core/Page",
        "alfresco/core/CoreXhr",
        "dojo/_base/declare",
        "dojo/_base/array",
        "dojo/_base/lang",
        "alfresco/core/ObjectTypeUtils",
        "service/constants/Default"], 
        function(ProcessWidgets, CoreXhr, declare, array, lang, ObjectTypeUtils, AlfConstants) {
   
   return declare([ProcessWidgets, CoreXhr], {
      
      /**
       * 
       * @instance
       */
      postCreate: function alfresco_core_FilteredPage__postCreate() {

         this.serviceXhr({url : AlfConstants.PROXY_URI + "api/people/" + AlfConstants.USERNAME + "?groups=true",
                          method: "GET",
                          successCallback: this.userGroupsLoaded,
                          failureCallback: this.userGroupsLoadFailure,
                          callbackScope: this});
      },

      /**
       * Handles the successful loading of user information.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      userGroupsLoaded: function alfresco_core_FilteredPage__userGroupsLoaded(response, originalRequestConfig) {
         this.alfLog("log", "User groups loaded", response, originalRequestConfig);
         if (response != null && response.groups != null)
         {
            array.forEach(response.groups, lang.hitch(this, "processUserGroups"));
         }

         // Get the group information from the response and then filter the widgets based on the data...
         this.widgets = this.performPageFiltering(this.widgets);
         if (this.services != null && this.services.length != 0)
         {
            this.processServices(this.services);
         }
         else if (this.widgets != null && this.widgets.length != 0)
         {
            this.processWidgets(this.widgets, this.containerNode);
         }
      },

      /**
       * 
       * @instance
       * @param {object} groupData The current group information
       * @param {number} index The index of the current group data in the groups array
       */
      processUserGroups: function alfresco_core_FilteredPage__processUserGroups(groupData, index) {
         // TODO: Using "currentItem" is temporary and based on how the original _MultiItemRendererMixin performed filtering
         //       before it was refactored out into the WidgetsProcessingFilterMixin (which needs to be updated to make the
         //       property a variable, e.g. allow "currentItem" to be set as something else)
         lang.setObject("currentItem.groups." + groupData.itemName, true, this);
         // this.currentItem.groups[groupData.itemName] = true;
      },

      /**
       * Handles the failure loading of user information.
       * 
       * @instance
       * @param {object} response The response from the request
       * @param {object} originalRequestConfig The configuration passed on the original request
       */
      userGroupsLoadFailure: function alfresco_core_FilteredPage__userGroupsLoadFailure(response, originalRequestConfig) {
         this.alfLog("error", "It was not possible to load the current users group information", response, originalRequestConfig);
      },

      /**
       * @instance
       * @param {object[]} widgets The widgets to be filtered.
       * @returns {object[]} An array of filtered widgets
       */
      performPageFiltering: function alfresco_core_FilteredPage__performPageFiltering(widgets) {
         var filteredWidgets = [];
         array.forEach(widgets, lang.hitch(this, "filterPageWidget", filteredWidgets));
         return filteredWidgets;
      },

      /**
       * @instance
       * @param {object[]} filteredWidgets An array to add widgets that pass the filtering to
       * @param {object} widget The current widget to filter
       * @param {number} index The index of the widget in its original array
       */
      filterPageWidget: function alfresco_core_FilteredPage__filterPageWidget(filteredWidgets, widget, index) {
         var passesFilter = this.filterWidget(widget, index, false);
         if (passesFilter)
         {
            this.alfLog("log", "Widget PASSES page filtering", widget);
            filteredWidgets.push(widget);

            // Process any nested widget configuration...
            var subWidgets = lang.getObject("config.widgets", false, widget);
            if (subWidgets != null && ObjectTypeUtils.isArray(subWidgets))
            {
               widget.config.widgets = this.performPageFiltering(subWidgets);
            }
         }
         else
         {
            this.alfLog("log", "Widget FAILS page filtering", widget);
         }
      }
   });
});