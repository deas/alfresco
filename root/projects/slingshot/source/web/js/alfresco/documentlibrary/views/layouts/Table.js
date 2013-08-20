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
 * Use this widget to render a a table of [rows]{@link module:alfresco/documentlibrary/views/layouts/Row}
 * 
 * @module alfresco/documentlibrary/views/layouts/Table
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Table.html",
        "alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin",
        "alfresco/core/Core"], 
        function(declare, _WidgetBase, _TemplatedMixin, template, _MultiItemRendererMixin, AlfCore) {

   return declare([_WidgetBase, _TemplatedMixin, _MultiItemRendererMixin, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/Table.css"}]
       */
      cssRequirements: [{cssFile:"./css/Table.css"}],
      
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
      postCreate: function alfresco_documentlibrary_views_layouts_Table__postCreate() {
         if (this.widgets)
         {
            this.processWidgets(this.widgets, this.containerNode);
         }
      }
   });
});