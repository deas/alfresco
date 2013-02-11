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
        "alfresco/core/Core",
        "dojo/text!./templates/ProcessWidgets.html",
        "dojo/dom-construct",
        "dojo/_base/array"], 
        function(declare, _Widget, _Templated, AlfCore, template, domConstruct, array) {
   
   /**
    * TODO: It's possible that this widget is not really needed anymore - need to check.
    */
   return declare([_Widget, _Templated, AlfCore], {
      
      /**
       * The HTML template to use for the widget.
       * @property template {String}
       */
      templateString: template,
      
      /**
       * @property {object} config
       * @default null
       */
      config: null,
      
      /**
       * @property {string} configUrl
       * @default ""
       */
      configUrl: "",
      
      /**
       * @property {string} baseClass
       * @default "widgets"
       */
      baseClass: "widgets",
      
      /**
       * @method postCreate
       */
      postCreate: function alfresco_core_ProcessWidgets__postCreate() {
         if (this.widgets)
         {
            this.processWidgets(this.widgets, this.containerNode);
         }
      }
   });
});