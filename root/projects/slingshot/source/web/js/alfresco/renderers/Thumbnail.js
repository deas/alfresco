/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * Renders a standard large thumbnail for a node.
 * 
 * @module alfresco/renderers/Thumbnail
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/renderers/_JsNodeMixin
 * @mixes module:alfresco/node/DraggableNodeMixin
 * @mixes module:alfresco/renderers/_PublishPayloadMixin
 * @mixes module:alfresco/node/NodeDropTargetMixin
 * @mixes module:dijit/_OnDijitClickMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "alfresco/renderers/_JsNodeMixin",
        "alfresco/node/DraggableNodeMixin",
        "alfresco/node/NodeDropTargetMixin",
        "alfresco/renderers/_PublishPayloadMixin",
        "dijit/_OnDijitClickMixin",
        "dojo/text!./templates/Thumbnail.html",
        "alfresco/core/Core",
        "alfresco/renderers/_ItemLinkMixin",
        "alfresco/documentlibrary/_AlfDndDocumentUploadMixin",
        "service/constants/Default",
        "dojo/_base/lang",
        "dojo/_base/event",
        "alfresco/core/NodeUtils"], 
        function(declare, _WidgetBase, _TemplatedMixin, _JsNodeMixin, DraggableNodeMixin, NodeDropTargetMixin, 
                 _PublishPayloadMixin, _OnDijitClickMixin, template, AlfCore, _ItemLinkMixin, _AlfDndDocumentUploadMixin, 
                 AlfConstants, lang, event, NodeUtils) {

   return declare([_WidgetBase, _TemplatedMixin, _OnDijitClickMixin, _JsNodeMixin, DraggableNodeMixin, NodeDropTargetMixin, AlfCore, _ItemLinkMixin, _AlfDndDocumentUploadMixin, _PublishPayloadMixin], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/Thumbnail.css"}]
       */
      cssRequirements: [{cssFile:"./css/Thumbnail.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * Additional CSS classes to apply to the main DOM node defined in the template
       * @instance
       * @type {string}
       * @default ""
       */
      customClasses: "",
      
      /**
       * Set up the attributes to be used when rendering the template.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_renderers_Thumbnail__postMixInProperties() {
         
         this.imgId = "";
         this.thumbnailUrl = "";
         this.imgAltText = "";
         this.imgTitle = "";

         if (this.currentItem != null && this.currentItem.node)
         {
            var jsNode = this.currentItem.jsNode;
            this.thumbnailUrl = this.generateThumbnailUrl();
            if (this.currentItem.displayName == null)
            {
               this.currentItem.displayName = jsNode.properties["cm:name"];
            }
            this.imgTitle = this.encodeHTML(this.currentItem.displayName);
            this.imgAltText = (this.currentItem.displayName != null) ? this.currentItem.displayName.substring(this.currentItem.displayName.lastIndexOf(".")) : "";
            this.imgId = jsNode.nodeRef.nodeRef;
         }
         else if (this.currentItem != null && this.currentItem.nodeRef != null)
         {
            // Fallback to just having a nodeRef available... this has been added to handle rendering of 
            // thumbnails in search results where full node information may not be available...
            var nodeRef = NodeUtils.processNodeRef(this.currentItem.nodeRef);
            if (this.currentItem.type === "folder")
            {
               this.thumbnailUrl =  AlfConstants.URL_RESCONTEXT + "components/search/images/folder.png";
            }
            else if (this.currentItem.type === "document")
            {
               this.thumbnailUrl = AlfConstants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/doclib/?c=queue&ph=true&lastModified=" + this.currentItem.modifiedOn;
            }
            else
            {
               this.thumbnailUrl = this.generateFallbackThumbnailUrl();
            }
         }
      },

      /**
       * If a thumbnail URL cannot be determined then fallback to a standard image.
       *
       * @instance
       * @returns {string} The URL for the thumbnail.
       */
      generateFallbackThumbnailUrl: function alfresco_renderers_Thumbnail__generateFallbackThumbnailUrl() {
         return this.thumbnailUrl = AlfConstants.URL_RESCONTEXT + "components/search/images/generic-result.png";
      },
      
      /**
       * The name of the folder image to use. Valid options are: "folder-32.png", "folder-48.png", "folder-64.png"
       * and "folder-256.png". The default is "folder-64.png".
       *
       * @instance
       * @type {string}
       * @default "folder-64.png"
       */
      folderImage: "folder-64.png",

      /**
       * Returns a URL to the image to use when rendering a folder
       * 
       * @instance
       */
      getFolderImage: function alfresco_renderers_Thumbnail__getDefaultFolderImage() {
         return require.toUrl("alfresco/renderers") + "/css/images/" + this.folderImage;
      },
      
      /**
       * The type of rendition to use for the thumbnail
       * @instance
       * @type {string} 
       * @default "doclib"
       */
      renditionName: "doclib",
      
      /**
       * Generates the URL to use as the source of the thumbnail.
       * 
       * @instance
       * @param renditionName
       * @returns {string}
       */
      generateThumbnailUrl: function alfresco_renderers_Thumbnail__generateThumbnailUrl() {
         var url = null;
         if (this.renditionName == null)
         {
            this.renditionName = "doclib";
         }
         if (this.currentItem != null && this.currentItem.node)
         {
            var jsNode = this.currentItem.jsNode;
            if (jsNode.isContainer || (jsNode.isLink && jsNode.linkedNode.isContainer))
            {
               url = this.getFolderImage();
            }
            else
            {
               var nodeRef = jsNode.isLink ? jsNode.linkedNode.nodeRef : jsNode.nodeRef;
               if (jsNode.properties["cm:lastThumbnailModification"])
               {
                  var thumbnailModData = jsNode.properties["cm:lastThumbnailModification"];
                  for (var i = 0; i < thumbnailModData.length; i++)
                  {
                     if (thumbnailModData[i].indexOf(this.renditionName) != -1)
                     {
                        url = AlfConstants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/" + this.renditionName + "?c=queue&ph=true&lastModified=" + thumbnailModData[i];
                        break;
                     }
                  }
               }
            }
         }
         if (url == null)
         {
            url = AlfConstants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/" + this.renditionName + "?c=queue&ph=true";
         }
         return url;
      },
      
      /**
       * 
       * @instance
       */
      postCreate: function alfresco_renderers_Thumbnail__postCreate() {
         this.inherited(arguments);
         var isContainer = lang.getObject("currentItem.jsNode.isContainer", false, this);
         if (isContainer == true)
         {
            this.addUploadDragAndDrop(this.imgNode);
            this.addNodeDropTarget(this.imgNode);
         }
         // this.createItemLink(this.domNode);
      },

      /**
       * Handles the property being clicked. This stops the click event from propogating
       * further through the DOM (to prevent any wrapping anchor elements from triggering
       * browser navigation) and then publishes the configured topic and payload.
       *
       * @instance
       * @param {object} evt The details of the click event
       */
      onLinkClick: function alfresco_renderers_Thumbnail__onLinkClick(evt) {
         event.stop(evt);
         // var publishTopic = this.getPublishTopic();
         if (this.publishTopic == null || lang.trim(this.publishTopic) == "")
         {
            this.alfLog("warn", "No publishTopic provided for PropertyLink", this);
         }
         else
         {
            var publishGlobal = (this.publishGlobal != null) ? this.publishGlobal : false;
            var publishToParent = (this.publishToParent != null) ? this.publishToParent : false;
            this.alfPublish(this.publishTopic, this.getPublishPayload(), publishGlobal, publishToParent);
         }
      },

      /**
       * This function currently returns the configured payload. It needs to be updated to 
       * actually return something contextually relevant to the current item. It has been added
       * to support extending modules.
       *
       * @instance
       * @returns {object} The payload to publish when the thumbnail is clicked.
       */
      getPublishPayload: function alfresco_renderers_Thumbnail__getPublishPayload() {
         return this.generatePayload(this.publishPayload, this.currentItem, null, this.publishPayloadType, this.publishPayloadItemMixin, this.publishPayloadModifiers);
      }
   });
});