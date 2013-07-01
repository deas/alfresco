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
// This class is intended to be mixed into to all menu item instances to ensure that they all share common behaviour 
define(["dojo/_base/declare",
        "dijit/Dialog",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dojo/html",
        "dojo/aspect"], 
        function(declare, Dialog, AlfCore, lang, array, domConstruct, domClass, html, aspect) {
   
   return declare([Dialog, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/AlfDialog.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @property i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/AlfDialog.properties"}],
      
      /**
       * @property {string} textContent Basic text content to be added to the dialog.
       */
      textContent: "",
      
      /**
       * @property {array} widgetsContent Widgets to be processed into the main node
       */
      widgetsContent: null,
      
      /**
       * @property {array} widgetsButtons Widgets to be processed into the button bar
       */
      widgetsButtons: null,

      /**
       * 
       * @method postMixInProperties
       */
      postMixInProperties: function alfresco_dialogs_AlfDialog__postMixInProperties() {
         this.inherited(arguments);
         this.closable = false;
      },
      
      /**
       * @method postCreate
       */
      postCreate: function alfresco_dialogs_AlfDialog__postCreate() {
         this.inherited(arguments);
         domClass.add(this.domNode, "alfresco-dialog-AlfDialog");
         if (this.widgetsContent != null)
         {
            // Add widget content to the container node...
            this.processWidgets(this.widgetsContent, this.containerNode);
         }
         else if (this.textContent != null)
         {
            // Add basic text content into the container node. An example of this would be for
            // setting basic text content in an confirmation dialog...
            html.set(this.containerNode, this.encodeHTML(this.textContent));
         }
         
         if (this.widgetsButtons != null)
         {
            this.buttonsNode = domConstruct.create("div", {
               "class" : "footer"
            }, this.domNode, "last");
            this.processWidgets(this.widgetsButtons, this.buttonsNode);
         }
      },
      
      /**
       * Iterates over any buttons that are created and calls the "attachButtonHandler" method with each of them
       * to ensure that clicking a button results in the dialog being hidden.
       * 
       * @method allWidgetsProcessed
       */
      allWidgetsProcessed: function alfresco_dialogs_AlfDialog__allWidgetsProcessed(widgets) {
         if (this.buttonsNode)
         {
            // If buttonsNode exists then we know this has been called after creating buttons...
            array.forEach(widgets, lang.hitch(this, "attachButtonHandler"));
         }
      },
      
      /**
       * Hitches each button click to the "hide" method so that whenever a button is clicked the dialog will be hidden.
       * It's assumed that the buttons will take care of their own business.
       * 
       * @method attachButtonHandler
       */
      attachButtonHandler: function alfresco_dialogs_AlfDialog__attachButtonHandler(widget, index) {
         if (widget != null)
         {
            aspect.before(widget, "onClick", lang.hitch(this, "hide"));
         }
      }
   });
});