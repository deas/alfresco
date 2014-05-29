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
 * Renders a thumbnail specific to search results. This is different from the usual thumbnail
 * in that it renders specific images for items that are neither documents nor folders.
 * 
 * @module alfresco/renderers/SearchThumbnail
 * @extends module:alfresco/renderers/Thumbnail
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/renderers/Thumbnail",
        "alfresco/navigation/_HtmlAnchorMixin",
        "alfresco/renderers/_SearchResultLinkMixin",
        "service/constants/Default",
        "dojo/_base/lang",
        "dojo/window"], 
        function(declare, Thumbnail, _HtmlAnchorMixin, _SearchResultLinkMixin, AlfConstants, lang, win) {

   return declare([Thumbnail, _HtmlAnchorMixin, _SearchResultLinkMixin], {

      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/SearchThumbnail.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/SearchThumbnail.properties"}],

      /**
       * Indicates whether documents thumbnail clicks should launch a previewer in a dialog
       * rather than linking directly to the document itself. Default to false.
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      showDocumentPreview: false,

      /**
       * Generates the publication payload by calling the mixed in 
       * [generatePayload]{@link module:alfresco/renderers/_SearchResultLinkMixin#generatePayload}
       * function and then wraps the property in an anchor element by calling the mixed in 
       * [makeAnchor]{@link module:alfresco/navigation/_HtmlAnchorMixin#makeAnchor} function
       *
       * @instance
       */
      postCreate: function alfresco_renderers_SearchThumbnail__postCreate() {
         this.inherited(arguments);
         this.publishPayload = this.generatePayload(this.payload, this.currentItem, null, this.publishPayloadType, this.publishPayloadItemMixin, this.publishPayloadModifiers);
         this.makeAnchor(this.publishPayload.url, this.publishPayload.type);
      },

      /**
       * Extends the function inherited from the [_SearchResultLinkMixin module]{@link module:alfresco/renderers/_SearchResultLinkMixin#generatePayload}
       * to the topic and payload for documents to show a dialog containing the previewer for that document
       *
       * @instance
       * @returns {object} The payload to publish when clicked.
       */
      generatePayload: function alfresco_renderers_SearchThumbnail__generatePayload() {
         var type = lang.getObject("type", false, this.currentItem);
         if (this.showDocumentPreview && type === "document")
         {
            // Because the content of the previewer will load asynchronously it's important that 
            // we set some dimensions for the dialog body, otherwise it will appear off-center
            var vs = win.getBox();
            this.publishTopic = "ALF_CREATE_DIALOG_REQUEST";
            return {
               contentWidth: (vs.w*0.8) + "px",
               contentHeight: (vs.h*0.7) + "px",
               dialogTitle: this.currentItem.name,
               widgetsContent: [
                  {
                     name: "alfresco/documentlibrary/AlfDocument",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/preview/AlfDocumentPreview"
                           }
                        ]
                     }
                  }
               ],
               widgetsButtons: [
                  {
                     name: "alfresco/buttons/AlfButton",
                     config: {
                        label: this.message("searchThumbnail.preview.dialog.close"),
                        publishTopic: "NO_OP"
                     }
                  }
               ],
               publishOnShow: [
                  {
                     publishTopic: "ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST",
                     publishPayload: {
                        nodeRef: this.currentItem.nodeRef
                     }
                  }
               ]
            };
         }
         else
         {
            return this.inherited(arguments);
         }
      },

      /**
       * Overrides the standard fallback to address specific site item types.
       *
       * @instance
       * @returns {string} The URL for the thumbnail.
       */
      generateFallbackThumbnailUrl: function alfresco_renderers_SearchThumbnail__generateFallbackThumbnailUrl() {
         var url;
         switch (this.currentItem.type)
         {
            case "blogpost":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/blog-post.png';
               break;
   
            case "forumpost":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/topic-post.png';
               break;
   
            case "calendarevent":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/calendar-event.png';
               break;
   
            case "wikipage":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/wiki-page.png';
               break;
   
            case "link":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/link.png';
               break;
   
            case "datalist":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/datalist.png';
               break;
   
            case "datalistitem":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/datalistitem.png';
               break;
   
            default:
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/generic-result.png';
               break;
         }
         return url;
      },

      /**
       * Returns an array containing the selector that identifies the span to wrap in an anchor.
       * This overrides the [mixed in function]{@link module:alfresco/navigation/_HtmlAnchorMixin}
       * that just returns an empty array.
       *
       * @instance
       */
      getAnchorTargetSelectors: function alfresco_renderers_SearchThumbnail__getAnchorTargetSelectors() {
         return ["span.inner"];
      }
   });
});