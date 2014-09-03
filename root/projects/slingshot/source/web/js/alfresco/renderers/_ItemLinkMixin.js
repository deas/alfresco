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
       * Updates the publication payload and topic for a folder.
       *
       * @instance
       */
      updateFolderLinkPublication: function alfresco_renderers__ItemLinkMIxin__updateFolderLinkPublication() {
         this.linkClickTopic = "ALF_DOCUMENTLIST_PATH_CHANGED";
         this.publishPayload = {};
         var locn = this.currentItem.location;
         if (this.currentItem.parent.isContainer)
         {
            this.publishPayload.path = this.combinePaths(locn.path, locn.file);
         }
         else if (this.currentItem.location.path === "/")
         {
            // handle Repository root parent node (special store_root type - not a folder)
            this.publishPayload.path = this.combinePaths(locn.path, "");
         }
         else
         {
            this.alfLog("warn", "It was not possible to generate a payload for the current item", this.currentItem, this);
         }
      },

      /**
       * @instance
       */
      generateFileFolderLink: function alfresco_renderers__ItemLinkMixin__generateFileFolderLink() {
         if (this.currentItem != null && this.currentItem.node)
         {
            var jsNode = this.currentItem.jsNode;
            if (jsNode.isLink && this.currentItem.location.site)
            {
               if (jsNode.isContainer)
               {
                  this.updateFolderLinkPublication();
               }
               else
               {
                  // TODO: This needs re-writing...
                  payload.url = this.getActionUrls(this.currentItem, this.currentItem.location.site.name).documentDetailsUrl;
               }
            }
            else
            {
               if (jsNode.isContainer)
               {
                  this.updateFolderLinkPublication();
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
                     this.updateDocumentLinkPublication();
                  }
               }
            }
         }
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
      updateDocumentLinkPublication: function alfresco_renderers__ItemLinkMixin__updateDocumentLinkPublication() {
         this.publishTopic = "ALF_NAVIGATE_TO_PAGE";
         this.publishPayload = {
            url: "",
            type: "FULL_PATH",
            target: "CURRENT"
         };
         if (this.customDetailsURL == null)
         {
            var actionsUrls = this.getActionUrls(this.currentItem);
            this.publishPayload.url = actionUrls.documentDetailsUrl;
         }
         else
         {
            // If a custom URL has been provided then use that but append the nodeRef URI on the end
            this.publishPayload.url = this.customDetailsURL
            if (lang.exists("currentItem.jsNode.nodeRef.uri", this))
            {
               // If the current item is a node with an accessible uri attribute then append it to the URL...
               // We should possibly only do this if a boolean attribute is set to true but at the moment
               // I can't see any cases where you wouldn't want to specify the node...
               this.publishPayload.url += "/" + this.currentItem.jsNode.nodeRef.uri;
            }
         }
      }
   });
});