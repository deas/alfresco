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
 * @module alfresco/menus/_AlfMenuItemMixin
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreRwd
 * @mixes module:alfresco/menus/_AlfPopupCloseMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "alfresco/core/CoreRwd",
        "alfresco/menus/_AlfPopupCloseMixin",
        "dojo/dom-class", 
        "dojo/dom-style"],
        function(declare, AlfCore, AlfCoreRwd, _AlfPopupCloseMixin, domClass, domStyle) {
   
   return declare([AlfCore, AlfCoreRwd, _AlfPopupCloseMixin], {

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/_AlfMenuItemMixin.css"}]
       */
      cssRequirements: [{cssFile:"./css/_AlfMenuItemMixin.css"}],
      
      /**
       * Defines the image width for the menu item icon. An image is only used if no explicit CSS class is set.
       * 
       * @instance
       * @type {string} 
       * @default "16px"
       */
      iconImageWidth: "16px",
      
      /**
       * Defines the image height for the menu item icon. An image is only used if no explicit CSS class is set.
       * 
       * @instance
       * @type {string} 
       * @default "16px"
       */
      iconImageHeight: "16px",
      
      /**
       * If a 'targetUrl' attribute is provided the value will be passed as a publication event to the NavigationService
       * to reload the page to the URL defined.
       * 
       * @instance
       * @type {string}
       * @default null
       */
      targetUrl: null,
      
      // TODO: It might be nice to retrieve this from the NavigationService itself??
      /**
       * Indicates how the target URL should be handled. This defaults to "SHARE_PAGE_RELATIVE" which means that the URL
       * will be appended to the 'Alfresco.constants.URL_PAGECONTEXT' Global JavaScript constant. This can be overridden
       * on instantiation to indicate that another URL type, such as "FULL_PATH" should be used.
       * 
       * @instance
       * @type {string}
       * @default "SHARE_PAGE_RELATIVE"
       */
      targetUrlType: "SHARE_PAGE_RELATIVE",
      
      /**
       * Indicates whether or not the URL should be opened in the current window/tab or in a new window. 
       * 
       * @instance
       * @type {string}
       * @default "SHARE_PAGE_RELATIVE"
       */
      targetUrlLocation: "CURRENT",
      
      /**
       * It's important to perform label encoding before buildRendering occurs (e.g. before postCreate)
       * to ensure that an unencoded label isn't set and then replaced. 
       * 
       * @instance
       */
      postMixInProperties: function alfresco_menus__AlfMenuItemMixin__postMixInProperties() {
         if (this.label)
         {
            this.label = this.encodeHTML(this.message(this.label));
         }
         this.inherited(arguments);
      },
      
      /**
       * Ensures that the supplied menu item label is translated.
       * @instance
       */
      postCreate: function alfresco_menus__AlfMenuItemMixin__postCreate() {
         this.set("label", this.label);
         this.inherited(arguments);
      },

      /**
       * @instance
       */
      setupIconNode: function alfresco_menus__AlfMenuItemMixin__setupIconNode() {
         if (this.iconClass && this.iconClass != "dijitNoIcon" && this.iconNode)
         {
            domClass.add(this.iconNode, this.iconClass);
         }
         else if (this.iconImage && this.iconNode)
         {
            /* The Dojo CSS class "dijitNoIcon" will automatically have been applied to a menu item
             * if it is not overridden. Therefore in order to ensure that the icon is displayed it
             * is necessary to set the height and width and to ensure that the display is set to
             * block. Because the style is being explicitly set it will take precedence over the
             * Dojo CSS class.
             */
            domStyle.set(this.iconNode, { backgroundImage: "url(" + this.iconImage + ")",
                                          width: this.iconImageWidth,
                                          height: this.iconImageHeight,
                                          display: "block" });
         }
         else
         {
            // If there is no iconClass or iconImage then we need to explicitly set the the
            // parent element of the icon node to have an inherited width. This is because there
            // is a CSS selector that fixes the width of menu items with icons to ensure that 
            // they are all aligned. This means that there would be a space for an icon even if
            // one was not available.
            domStyle.set(this.iconNode.parentNode, {
               width: "auto"
            });
         }
      },
      
      /**
       * Overrides the default onClick function. Currently only supports page navigation.
       * 
       * @instance
       * @param {object} evt The click event
       */
      onClick: function alfresco_menus__AlfMenuItemMixin__onClick(evt) {
         this.alfLog("log", "AlfMenuBarItem clicked");

         // Emit the event to close popups in the stack...
         this.emitClosePopupEvent();
         
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