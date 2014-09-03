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
 * @module alfresco/html/Heading
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Richard Smith
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Heading.html",
        "alfresco/core/Core",
        "dojo/dom-class",
        "dojo/dom-construct",
        "dojo/dom-attr"], 
        function(declare, _WidgetBase, _TemplatedMixin, template, AlfCore, domClass, domConstruct, domAttr) {
   
   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {object[]}
       * @default [{cssFile:"./css/Heading.css"}]
       */
      cssRequirements: [{cssFile:"./css/Heading.css"}],

      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,

      /**
       * The optional id to assign to the generated heading
       * 
       * @instance
       * @type {string}
       * @default null
       */
      headingId: null,

      /**
       * The level of heading to generate
       * 
       * @instance
       * @type {number}
       * @default 1
       */
      level: 1,

      /**
       * The heading text to display
       * 
       * @instance
       * @type {string}
       * @default ""
       */
      label: "",

      /**
       * Should the heading be hidden (accessibly)?
       * 
       * @instance
       * @type {boolean}
       * @default false
       */
      isHidden: false,

      /**
       * Class to use when isHidden = true
       * 
       * @instance
       * @type {string}
       * @default "hiddenAccessible"
       */
      _hiddenAccessibleClass: "hiddenAccessible",

      /**
       * Any additional css classes to add.
       * 
       * @instance
       * @type {string}
       * @default null
       */
      additionalCssClasses: null,

      /**
       * @instance
       */
      postCreate: function alfresco_html_Heading__postCreate() {

         if(isNaN(this.level) || this.level < 1 || this.level > 6 || !this.label)
         {
            this.alfLog("error", "A heading must have a numeric level from 1 to 6 and must have a label", this);
         }
         else
         {
            var heading = domConstruct.create("h" + this.level, {
               innerHTML: this.label
            }, this.headingNode);

            if(this.headingId)
            {
               domAttr.set(heading, "id", this.headingId);
            }
         }

         if(this.isHidden)
         {
            domClass.add(this.domNode, this._hiddenAccessibleClass);
         }
         else if (this.additionalCssClasses != null)
         {
            domClass.add(this.domNode, this.additionalCssClasses);
         }

      }
   });
});