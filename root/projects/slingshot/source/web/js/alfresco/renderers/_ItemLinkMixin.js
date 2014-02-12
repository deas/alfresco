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
 * @module alfresco/renderers/_ItemLinkMixin
 * @extends module:alfresco/core/Core
 * @mixes module:alfresco/core/UrlUtils
 * @mixes module:alfresco/core/PathUtils
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/core/Core",
        "service/constants/Default",
        "alfresco/core/UrlUtils",
        "alfresco/core/PathUtils",
        "dojo/_base/lang",
        "dojo/on",
        "dojo/_base/event",
        "dojo/query",
        "dojo/NodeList",
        "dojo/NodeList-manipulate"], 
        function(declare, AlfCore, AlfConstants, UrlUtils, PathUtils, lang, on, event, query, NodeList, nlManip) {
   
   return declare([AlfCore, UrlUtils, PathUtils], {

      /**
       * This function can be called to create a "link" from an item to another page (e.g. this would
       * typically be used on an item within a collection to navigate to the details of that item)
       * This will wrap the node in an anchor element but will actually publish a navigation when clicked
       * and swallow the click event to prevent the browser from processing it directly.
       *
       * @instance
       * @param {element} domNode The node on which to create the link.
       */
      createItemLink: function alfresco_renderers__ItemLinkMixin__createItemLink(domNode) {

         if (domNode == null) 
         {
            this.alfLog("warn", "A request has been made to create a link for an item, but no DOM node has been provided", this);
         }
         else
         {
            // Generate the link information for the current item...
            var payload = this.generateFileFolderLink();

            // Work out the URL to use in a fake anchor (the anchor click will never be processed)...
            // TODO: This code is based on something similar from "alfresco/menus/_AlfMenuItemMixin" and the two should be merged into
            //       a common helper function - probably in this module...
            var url;
            if (typeof payload.type == "undefined" ||
                payload.type == null ||
                payload.type == "" ||
                payload.type == "SHARE_PAGE_RELATIVE")
            {
               url = AlfConstants.URL_PAGECONTEXT + payload.url;
            }
            else if (payload.type == "CONTEXT_RELATIVE")
            {
               url = AlfConstants.URL_CONTEXT + payload.url;
            }
            else if (payload.type == "FULL_PATH")
            {
               url = payload.url;
            }
            else
            {
               url = window.location.pathname + "#" + payload.url;
            }

            // Attach the click event to the node...
            on(domNode, "click", lang.hitch(this, "onItemLinkClick", payload));
            dojo.query(domNode).wrap("<a class='alfresco-menus-_AlfMenuItemMixin' href='" + url + "'></a>");
         }
      },

      /**
       * This is the topic that will be published when the item is clicked. The default is aimed at being processed
       * by the [NavigationService]{@link module:alfresco/services/NavigationService} but it can be overridden by the
       * widget mixing in this class to set a custom topic to use.
       *
       * @instance
       * @type {string}
       * @default "ALF_NAVIGATE_TO_PAGE"
       */
      linkClickTopic: "ALF_NAVIGATE_TO_PAGE",

      /**
       * This handles the onClick events of the linked item. The supplied payload will be published to the navigation
       * service to redirect the page or update the current URL hash.
       *
       * @instance
       * @param {event} evt The click event
       */
      onItemLinkClick: function alfresco_renderers__ItemLinkMixin__onItemLinkClick(payload, evt) {
         // Stop the event to prevent the browser from processing the clicked anchor...
         event.stop(evt);
         this.alfLog("log", "Item link clicked: ", payload, this);
         this.alfPublish(this.linkClickTopic, payload);
      },

      /**
       * @instance
       */
      generateFileFolderLink: function alfresco_renderers__ItemLinkMixin__generateFileFolderLink() {
         
         var payload = {
            url: "",
            type: "FULL_PATH",
            target: "CURRENT"
         };
         if (this.currentItem != null && this.currentItem.node)
         {
            var jsNode = this.currentItem.jsNode;
            // This was the original if condition - i can't work out why we need to check for a current site??
//            if (jsNode.isLink && 
//                $isValueSet(scope.options.siteId) && 
//                this.currentItem.location.site && 
//                this.currentItem.location.site.name !== scope.options.siteId)
            if (jsNode.isLink && this.currentItem.location.site)
            {
               if (jsNode.isContainer)
               {
                  payload.url = $siteURL("documentlibrary?path=" + encodeURIComponent(this.currentItem.location.path),
                  {
                     site: this.currentItem.location.site.name
                  });
               }
               else
               {
                  payload.url = this.getActionUrls(this.currentItem, this.currentItem.location.site.name).documentDetailsUrl;
               }
            }
            else
            {
               if (jsNode.isContainer)
               {
                  if (this.currentItem.parent.isContainer)
                  {
                     // handle folder parent node
                     payload.type = "HASH";
                     payload.url = this.generatePathMarkup(this.currentItem.location);
                  }
                  else if (this.currentItem.location.path === "/")
                  {
                     // handle Repository root parent node (special store_root type - not a folder)
                     payload.type = "HASH";
                     payload.url = this.generateFilterMarkup({
                        filterId: "path",
                        filterData: $combine(this.currentItem.location.path, "")
                     });
                  }
                  else
                  {
                     // handle unknown parent node types
                     payload.type = "HASH";
                     payload.url = "#";
                  }
               }
               else
               {
                  // TODO: It'll be necessary to get the actual actionUrls - but currently it's to tangled to untangle easily
                  //var actionUrls = this.getActionUrls(this.currentItem);
                  var actionsUrls = this.getActionUrls(this.currentItem);
                  if (jsNode.isLink && jsNode.linkedNode.isContainer)
                  {
                     payload.url = actionUrls.folderDetailsUrl;
                  }
                  else
                  {
                     payload = this.getDetailsPayload();
                  }
               }
            }
         }
         payload.item = this.currentItem;
         return payload;
      },
      
      /**
       * The standard details URL can be optionally overridden to go do a different page
       *
       * @instance
       * @type {string}
       * @default
       */
      customDetailsURL: null,

      /**
       * Retrieves the navigation payload to use for accessing the details page for an item. The defail payload
       * can be overridden by setting a [custom details URL]{@link module:alfresco/renderers/_ItemLinkMixin#customDetailsURL}
       * or by overriding this function in classes that mixin this module.
       *
       * @returns {object} The navigtation payload to use.
       */
      getDetailsPayload: function alfresco_renderers__ItemLinkMixin__getDetailsUrl() {
         var payload = {
            url: "",
            type: "FULL_PATH",
            target: "CURRENT"
         };
         if (this.customDetailsURL == null)
         {
            var actionsUrls = this.getActionUrls(this.currentItem);
            payload.url = actionUrls.documentDetailsUrl;
         }
         else
         {
            // If a custom URL has been provided then use that but append the nodeRef URI on the end
            payload.url = this.customDetailsURL
            if (lang.exists("currentItem.jsNode.nodeRef.uri", this))
            {
               // If the current item is a node with an accessible uri attribute then append it to the URL...
               // We should possibly only do this if a boolean attribute is set to true but at the moment
               // I can't see any cases where you wouldn't want to specify the node...
               payload.url += "/" + this.currentItem.jsNode.nodeRef.uri;
            }
         }
         return payload;
      },

      /**
       * @instance
       * @param {object} filter
       */
      generateFilterMarkup: function alfresco_renderers__ItemLinkMixin__generateFilterMarkup(filter)
      {
         var filterObj = Alfresco.util.cleanBubblingObject(filter);
         return YAHOO.lang.substitute("filter={filterId}|{filterData}|{filterDisplay}", filterObj, function(p_key, p_value, p_meta)
         {
            return typeof p_value === "undefined" ? "" : window.escape(p_value);
         });
      },
      
      /**
       * @instance
       * @param {object} locn
       */
      generatePathMarkup: function alfresco_renderers__ItemLinkMixin__generatePathMarkup(locn)
      {
         return this.generateFilterMarkup(
         {
            filterId: "path",
            filterData: this.combinePaths(locn.path, locn.file)
         });
      }
   });
});