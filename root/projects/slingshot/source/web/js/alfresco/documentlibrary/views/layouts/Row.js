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
 * Use this widget to render a row of [cells]{@link module:alfresco/documentlibrary/views/layouts/Cell}
 * 
 * @module alfresco/documentlibrary/views/layouts/Row
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Row.html",
        "alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin",
        "alfresco/core/Core",
        "dojo/dom-class"], 
        function(declare, _WidgetBase, _TemplatedMixin, template, _MultiItemRendererMixin, AlfCore, domClass) {

   return declare([_WidgetBase, _TemplatedMixin, _MultiItemRendererMixin, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/Row.css"}]
       */
      cssRequirements: [{cssFile:"./css/Row.css"}],
      
      /**
       * The HTML template to use for the widget.
       * 
       * @instance
       * @type {String} template
       */
      templateString: template,
      
      /**
       * Calls [processWidgets]{@link module:alfresco/core/Core#processWidgets}
       * 
       * @instance postCreate
       */
      postCreate: function alfresco_documentlibrary_views_layouts_Row__postCreate() {
         if (this.widgets)
         {
            this.processWidgets(this.widgets, this.containerNode);
         }
      },

      /**
       * Focuses the domNode. This has been added to support the dijit/_KeyNavContainer functions mixed into 
       * the [document library views]{@link module:alfresco/documentlibrary/views/AlfDocumentListView} to 
       * allow easier keyboard navigation.
       * 
       * @instance
       */
      focus: function alfresco_documentlibrary_views_layouts_Row__focus() {
         this.domNode.focus();
      }
   });
});