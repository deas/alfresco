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
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Title.html",
        "alfresco/core/Core"], 
        function(declare, _WidgetBase, _TemplatedMixin, template,  AlfCore) {
   
   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/Title.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @property template {String}
       */
      templateString: template,
      
      /**
       * @property {string} title The title to be displayed. This should be a localized value.
       */
      label: null,
      
      /**
       * It's important to perform label encoding before buildRendering occurs (e.g. before postCreate)
       * to ensure that an unencoded label isn't set and then replaced. 
       * 
       * @method postMixInProperties
       */
      postMixInProperties: function alfresco_header_Title__postMixInProperties() {
         if (this.label)
         {
            this.label = this.encodeHTML(this.label != null ? this.label : "");
         }
      },
      
      /**
       * @method postCreate
       */
      postCreate: function alfresco_header_Title__postCreate() {
         this.textNode.innerHTML = this.label;
      }
   });
});