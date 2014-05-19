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
        "alfresco/renderers/_PublishPayloadMixin",
        "alfresco/navigation/_HtmlAnchorMixin",
        "service/constants/Default",
        "dojo/dom-class", 
        "dojo/dom-style",
        "dojo/dom-construct",
        "dojo/on",
        "dojo/_base/lang",
        "dojo/_base/event"],
        function(declare, AlfCore, AlfCoreRwd, _AlfPopupCloseMixin, _NavigationServiceTopicMixin, _PublishPayloadMixin, 
                 _HtmlAnchorMixin, AlfConstants, domClass, domStyle, domConstruct, on, lang, event) {
   
   return declare([AlfCore, AlfCoreRwd, _AlfPopupCloseMixin, _NavigationServiceTopicMixin, _PublishPayloadMixin, _HtmlAnchorMixin], {

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
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
       * will be appended to the 'AlfConstants.URL_PAGECONTEXT' Global JavaScript constant. This can be overridden
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
         this.makeAnchor(this.targetUrl, this.targetUrlType);
      },

      /**
       * Returns an array containing the selector that identifies the span to wrap in an anchor.
       * This overrides the [mixed in function]{@link module:alfresco/navigation/_HtmlAnchorMixin}
       * that just returns an empty array.
       *
       * @instance
       */
      getAnchorTargetSelectors: function alfresco_renderers_SearchResultPropertyLink__getAnchorTargetSelectors() {
         return ["td.dijitMenuItemLabel","span.alf-menu-bar-label-node"];
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
            domConstruct.destroy(this.iconNode);
            this.iconNode = domConstruct.create("img", {
               role:"presentation",
               className: "dijitInline dijitIcon dijitMenuItemIcon " + this.iconClass,
               src: AlfConstants.URL_RESCONTEXT + "/js/alfresco/menus/css/images/transparent-20.png",
               alt: this.message(this.iconAltText)
            }, iconNodeParent, "first");
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
                                          display: "inline-block",
                                          verticalAlign: "middle" });
         }
         else if (this.iconNode != null)
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
            //var payload = (this.publishPayload) ? this.publishPayload : {};
            var payload = this.generatePayload(this, this.currentItem, null, (this.publishPayload) ? this.publishPayload : {document: this.currentItem});
            this.alfPublish(this.publishTopic, payload);
         }
         else
         {
            this.alfLog("error", "An AlfMenuBarItem was clicked but did not define a 'targetUrl' or 'publishTopic' attribute", evt);
         }
      }
   });
});