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
 * Renders a standard large thumbnail for a node.
 * 
 * @module alfresco/renderers/Thumbnail
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/renderers/_JsNodeMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "alfresco/renderers/_JsNodeMixin",
        "alfresco/node/DraggableNodeMixin",
        "alfresco/node/NodeDropTargetMixin",
        "dojo/text!./templates/Thumbnail.html",
        "alfresco/core/Core",
        "alfresco/renderers/_ItemLinkMixin",
        "alfresco/documentlibrary/_AlfDndDocumentUploadMixin",
        "service/constants/Default",
        "dojo/_base/lang",
        "alfresco/core/NodeUtils"], 
        function(declare, _WidgetBase, _TemplatedMixin, _JsNodeMixin, DraggableNodeMixin, NodeDropTargetMixin, template, AlfCore, 
                 _ItemLinkMixin, _AlfDndDocumentUploadMixin, AlfConstants, lang, NodeUtils) {

   return declare([_WidgetBase, _TemplatedMixin, _JsNodeMixin, DraggableNodeMixin, NodeDropTargetMixin, AlfCore, _ItemLinkMixin, _AlfDndDocumentUploadMixin], {
      
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
         
         // TODO: These defaults should probably be instance variables?
         this.itemLinkHref = "";
         this.itemLinkClass = "";
         this.itemLinkRelative = "";
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
            if (this.currentItem.type == "folder")
            {
               this.thumbnailUrl =  AlfConstants.URL_RESCONTEXT + "components/search/images/folder.png";
            }
            else
            {
               this.thumbnailUrl = AlfConstants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/doclib/?c=queue&ph=true&lastModified=" + this.currentItem.modifiedOn;
            }
            
         }
      },
      
      /**
       * Returns a URL to the image to use when rendering a folder
       * 
       * @instance
       */
      getFolderImage: function alfresco_renderers_Thumbnail__getDefaultFolderImage() {
         return AlfConstants.URL_RESCONTEXT + "components/documentlibrary/images/folder-64.png";
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
         this.createItemLink(this.domNode);
      }
   });
});