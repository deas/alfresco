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
        "alfresco/renderers/_JsNodeMixin",
        "dojo/text!./templates/Comments.html",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "alfresco/core/UrlUtils",
        "dojo/dom-class"], 
        function(declare, _WidgetBase, _TemplatedMixin, _JsNodeMixin, template, AlfCore, lang, UrlUtils, domClass) {

   return declare([_WidgetBase, _TemplatedMixin, _JsNodeMixin, AlfCore, UrlUtils], {
      
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
         var urlType = (isContainer ? "folderDetailsUrl" : "documentDetailsUrl")
         if (actionUrls[urlType])
         {
            this.url = actionUrls[urlType] + "#comment";
         }
         else
         {
            this.url = "";
         }
         
         // Get the count of comments if there are any...
         this.commentCount = lang.getObject(this.commentCountProperty, false, this.currentItem);
         if (this.commentCount == null)
         {
            this.commentCount = 0;
         }
      },
      
      /**
       * @instance
       */
      postCreate: function alfresco_renderers_Comments__postCreate() {
         if (this.commentCount != null)
         {
            domClass.remove(this.countNode, "hidden");
         }
      }
   });
});