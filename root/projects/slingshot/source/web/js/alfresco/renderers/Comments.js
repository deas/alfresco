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
 * @module alfresco/renderers/Comments
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/core/UrlUtils
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dijit/_OnDijitClickMixin",
        "alfresco/renderers/_JsNodeMixin",
        "alfresco/navigation/_HtmlAnchorMixin",
        "dojo/text!./templates/Comments.html",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "alfresco/core/UrlUtils",
        "dojo/dom-class"], 
        function(declare, _WidgetBase, _TemplatedMixin, _OnDijitClickMixin, _JsNodeMixin, 
                 _HtmlAnchorMixin, template, AlfCore, lang, UrlUtils, domClass) {

   return declare([_WidgetBase, _TemplatedMixin, _OnDijitClickMixin, _JsNodeMixin, _HtmlAnchorMixin, AlfCore, UrlUtils], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/Comments.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/Comments.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/Comments.css"}]
       */
      cssRequirements: [{cssFile:"./css/Comments.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * The dot-notation property to use for the count of comments. Can be overridden.
       *
       * @instance
       * @type {string}
       * @default "node.properties.fm:commentCount"
       */
      commentCountProperty: "node.properties.fm:commentCount",

      /**
       * The type of comment  link URL. Should only be overridden by extending modules
       * not through configuration.
       *
       * @instance
       * @type {string}
       * @default "FULL_PATH"
       */
      targetUrlType: "FULL_PATH",

      /**
       * The comment link target, defaults to opening the link in the current browser
       * tab or window.
       *
       * @instance
       * @type {string}
       * @default "CURRENT"
       */
      linkTarget: "CURRENT",

      /**
       * Set up the attributes to be used when rendering the template.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_renderers_Comments__postMixInProperties() {
         this.commentLabel = this.message("comments.label");
         
         // Get a tooltip appropriate for the node type...
         var isContainer = lang.getObject("node.isContainer", false, this.currentItem);
         if (isContainer)
         {
            this.commentTooltip = this.message("comments.folder.tooltip");
         }
         else
         {
            this.commentTooltip = this.message("comments.document.tooltip");
         }
         
         // Get the URL for making a comment...
         // TODO: This should be replaced by publishing an action to allow greater control over how comments are made
         // TODO: Need to add siteId and repositoryUrl from somewhere
         // TODO: The actionUrls approach is neither performant nor configurable so should be replaced.
         var actionUrls = this.getActionUrls(this.currentItem, null, null);
         var urlType = (isContainer ? "folderDetailsUrl" : "documentDetailsUrl");
         if (actionUrls[urlType])
         {
            this.targetUrl = actionUrls[urlType] + "#comment";
         }
         else
         {
            this.targetUrl = "";
         }
         
         // Get the count of comments if there are any...
         this.commentCount = lang.getObject(this.commentCountProperty, false, this.currentItem);
         if (this.commentCount == null)
         {
            this.commentCount = 0;
         }
      },

      /**
       * Returns an array containing the selector that identifies the span to wrap in an anchor.
       * This overrides the [mixed in function]{@link module:alfresco/navigation/_HtmlAnchorMixin}
       * that just returns an empty array.
       *
       * @instance
       */
      getAnchorTargetSelectors: function alfresco_renderers_SearchResultPropertyLink__getAnchorTargetSelectors() {
         return ["span.comment-link"];
      },
      
      /**
       * @instance
       */
      postCreate: function alfresco_renderers_Comments__postCreate() {
         if (this.commentCount != null)
         {
            domClass.remove(this.countNode, "hidden");
         }
         this.makeAnchor(this.targetUrl, this.targetUrlType);
      },

      /**
       * Handles clicking on the comments link click and issues a navigation request.
       *
       * @instance
       * @param {object} evt The click event
       */
      onCommentClick: function alfresco_renderers_Comments__onCommentClick(evt) {
         this.alfPublish("ALF_NAVIGATE_TO_PAGE", { url: this.targetUrl,
                                                   type: this.targetUrlType,
                                                   target: this.linkTarget});
      }
   });
});