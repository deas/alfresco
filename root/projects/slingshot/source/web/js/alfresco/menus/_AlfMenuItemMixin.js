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
// This class is intended to be mixed into to all menu item instances to ensure that they all share common behaviour 
define(["dojo/_base/declare",
        "alfresco/core/Core"], 
        function(declare, AlfCore) {
   
   return declare([AlfCore], {

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/_AlfMenuItemMixin.css"}],
      
      /**
       * Defines the image width for the menu item icon. An image is only used if no explicit CSS class is set.
       * 
       * @property {string} iconImageWidth
       * @default "16px"
       */
      iconImageWidth: "16px",
      
      /**
       * Defines the image height for the menu item icon. An image is only used if no explicit CSS class is set.
       * 
       * @property {string} iconImageWidth
       * @default "16px"
       */
      iconImageHeight: "16px",
      
      /**
       * If a 'targetUrl' attribute is provided the value will be passed as a publication event to the NavigationService
       * to reload the page to the URL defined.
       * 
       * @property {string} targetUrl The URL that should be loaded when the menu item is clicked on.
       */
      targetUrl: null,
      
      // TODO: It might be nice to retrieve this from the NavigationService itself??
      /**
       * Indicates how the target URL should be handled. This defaults to "SHARE_PAGE_RELATIVE" which means that the URL
       * will be appended to the 'Alfresco.constants.URL_PAGECONTEXT' Global JavaScript constant. This can be overridden
       * on instantiation to indicate that another URL type, such as "FULL_PATH" should be used.
       * 
       * @property {string} targetUrlType
       * @default "SHARE_PAGE_RELATIVE"
       */
      targetUrlType: "SHARE_PAGE_RELATIVE",
      
      /**
       * Indicates whether or not the URL should be opened in the current window/tab or in a new window. 
       * 
       * @property {string} targetUrlLocation
       * @default "SHARE_PAGE_RELATIVE"
       */
      targetUrlLocation: "CURRENT",
      
      /**
       * Ensures that the supplied menu item label is translated.
       * @method postCreate
       */
      postCreate: function alfresco_menus__AlfMenuItemMixin__postCreate() {
         if (this.label)
         {
            this.set("label", this.encodeHTML(this.message(this.label)));
         }
         this.inherited(arguments);
      },

      /**
       * Overrides the default onClick function. Currently only supports page navigation.
       * 
       * @method onClick
       * @param {object} evt The click event
       */
      onClick: function alfresco_menus__AlfMenuItemMixin__onClick(evt) {
         this.alfLog("log", "AlfMenuBarItem clicked");
         if (this.targetUrl != null)
         {
            // Handle URLs...
            this.alfPublish("ALF_NAVIGATE_TO_PAGE", { url: this.targetUrl,
                                                      type: this.targetUrlType,
                                                      target: this.targetUrlLocation});
         }
         else if (this.publishTopic != null)
         {
            // Handle publish requests...
            var payload = (this.publishPayload) ? this.publishPayload : {};
            this.alfPublish(this.publishTopic, payload);
         }
         else
         {
            this.alfLog("error", "An AlfMenuBarItem was clicked but did not define a 'targetUrl' or 'publishTopic' attribute", evt);
         }
      }
   });
});