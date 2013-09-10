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
        "alfresco/services/_NavigationServiceTopicMixin",
        "dojo/dom-class", 
        "dojo/dom-style",
        "dojo/dom-construct",
        "dojo/on",
        "dojo/_base/lang",
        "dojo/_base/event",
        "dojo/query",
        "dojo/NodeList",
        "dojo/NodeList-manipulate"],
        function(declare, AlfCore, AlfCoreRwd, _AlfPopupCloseMixin, _NavigationServiceTopicMixin, domClass, domStyle, domConstruct, on, lang, event, query, NodeList) {
   
   return declare([AlfCore, AlfCoreRwd, _AlfPopupCloseMixin, _NavigationServiceTopicMixin], {

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
       * @instance
       * @type {string}
       * @default ""
       */
      iconAltText: "",
      
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
         
         // Set up a handler for onContextMenu actions (e.g. right-clicks), although by default this will perform no action...
         on(this.domNode, "contextmenu", lang.hitch(this, "onContextMenu"));
         
         // When a targetUrl is specified we want to wrap menu item labels in <a> elements to allow the browsers context menu
         // to access the URL (most commonly used for opening a page in a new tab). However, we aren't going to allow the browser
         // to process the link as we still want it to go via the NavigationService...
         if (this.targetUrl != null)
         {
            // The following code is based on the NavigationService, it should possibly be abstracted to a mixin
            // to prevent future maintenance issues, but given this is "non-functional" code it's not important at the moment.
            // We want to build a URL to set as the "href" attribute of the <a> element.
            var url;
            if (typeof this.targetUrlType == "undefined" ||
                this.targetUrlType == null ||
                this.targetUrlType == "" ||
                this.targetUrlType == this.sharePageRelativePath)
            {
               url = Alfresco.constants.URL_PAGECONTEXT + this.targetUrl;
            }
            else if (this.targetUrlType == this.contextRelativePath)
            {
               url = Alfresco.contants.URL_CONTEXT + this.targetUrl;
            }
            else if (this.targetUrlType == this.fullPath)
            {
               url = this.targetUrl;
            }
            // Add the anchor elements...
            this._addAnchors(url);
         }
      },

      /**
       * This function is called for any menu item with a targetUrl. By default it addresses the known menu items DOM structures
       * of the AlfMenuItem and AlfMenuBarItem. However, it can be extended or updated to handle the DOM structure of additional
       * menu widgets.
       * 
       * @instance
       * @param {string} url The URL to use for the anchor
       */
      _addAnchors: function alfresco_menus__AlfMenuItemMixin___addAnchors(url) {
         dojo.query("td.dijitMenuItemLabel", this.domNode).wrapInner("<a class='alfresco-menus-_AlfMenuItemMixin' href='" + url + "'></a>");
         dojo.query("span.alf-menu-bar-label-node", this.domNode).wrapInner("<a class='alfresco-menus-_AlfMenuItemMixin' href='" + url + "'></a>");
      },
      
      /**
       * Extension point for handling context click events. By default this performs no action.
       * 
       * @instance
       * @param {evt} evt The context menu event
       */
      onContextMenu: function(evt) {
         // No action by default.
      },
      
      /**
       * @instance
       */
      setupIconNode: function alfresco_menus__AlfMenuItemMixin__setupIconNode() {
         if (this.iconClass && this.iconClass != "dijitNoIcon" && this.iconNode)
         {
            var iconNodeParent = this.iconNode.parentNode;
            domConstruct.empty(iconNodeParent);
            this.iconNode = domConstruct.create("img", {
               role:"presentation",
               className: "dijitInline dijitIcon dijitMenuItemIcon " + this.iconClass,
               src: "/share/res/js/alfresco/menus/css/images/transparent-20.png",
               alt: this.message(this.iconAltText)
            }, iconNodeParent);
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
            // Stop the event (to prevent the browser processing <a> elements 
            event.stop(evt);

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