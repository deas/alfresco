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
 * An Alfresco styled dialog. Extends the default Dojo dialog by adding support for a row of buttons defined
 * by the [widgetButtons]{@link module:alfresco/dialogs/AlfDialog#widgetButtons} attribute. The main body
 * of the dialog can either be defined as simple text assigned to the
 * [textContent]{@link module:alfresco/dialogs/AlfDialog#textContent} attribute or as a JSON model assigned to
 * the [widgetsContent]{@link module:alfresco/dialogs/AlfDialog#widgetsContent} attribute (widgets take
 * precedence over text - it is not possible to mix both).
 * 
 * @module alfresco/dialogs/AlfDialog
 * @extends dijit/Dialog
 * @mixes module:Alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/Dialog",
        "alfresco/core/Core",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dojo/html",
        "dojo/aspect",
        "dijit/registry",
        "dijit/nls/loading",
        "dijit/nls/common"], 
        function(declare, Dialog, AlfCore, lang, array, domConstruct, domClass, html, aspect, registry) {
   
   return declare([Dialog, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/AlfDialog.css"}]
       */
      cssRequirements: [{cssFile:"./css/AlfDialog.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/AlfDialog.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/AlfDialog.properties"}],
      
      /**
       * Basic text content to be added to the dialog.
       * 
       * @instance
       * @type {String} textContent
       * @default ""
       */
      textContent: "",
      
      /**
       * Widgets to be processed into the main node
       * 
       * @instance
       * @type {Object[]}
       * @default null 
       */
      widgetsContent: null,
      
      /**
       * Widgets to be processed into the button bar
       * 
       * @instance
       * @type {Object[]}
       * @default null 
       */
      widgetsButtons: null,

      /**
       * Extends the superclass implementation to set the dialog as not closeable (by clicking an "X"
       * in the corner).
       * 
       * @instance
       */
      postMixInProperties: function alfresco_dialogs_AlfDialog__postMixInProperties() {
         this.inherited(arguments);
      },
      
      /**
       * Extends the superclass implementation to process the widgets defined by 
       * [widgetButtons]{@link module:alfresco/dialogs/AlfDialog#widgetButtons} into the buttons bar
       * and either the widgets defined by [widgetsContent]{@link module:alfresco/dialogs/AlfDialog#widgetsContent}
       * or the text string set as [textContent]{@link module:alfresco/dialogs/AlfDialog#textContent} into
       * the main body of the dialog.
       * 
       * @instance
       */
      postCreate: function alfresco_dialogs_AlfDialog__postCreate() {
         this.inherited(arguments);
         domClass.add(this.domNode, "alfresco-dialog-AlfDialog");
         this.bodyNode = domConstruct.create("div", {
            "class" : "dialog-body"
         }, this.containerNode, "last");
         if (this.widgetsContent != null)
         {
            // Add widget content to the container node...
            var widgetsNode = domConstruct.create("div", {}, this.bodyNode, "last");
            this.processWidgets(this.widgetsContent, widgetsNode);
         }
         else if (this.textContent != null)
         {
            // Add basic text content into the container node. An example of this would be for
            // setting basic text content in an confirmation dialog...
            html.set(this.bodyNode, this.encodeHTML(this.textContent));
         }
         
         if (this.widgetsButtons != null)
         {
            this.buttonsNode = domConstruct.create("div", {
               "class" : "footer"
            }, this.containerNode, "last");
            this.processWidgets(this.widgetsButtons, this.buttonsNode);
         }
      },
      
      /**
       * Extends the inherited function to ensure that all the widgets created in the dialog body are included
       * in any publish payloads. This is important in order that the subscription callback has access to any
       * data that the user may have set. 
       * 
       * @instance
       * @param {DOM element} rootNode The DOM node where the widget should be created.
       * @param {object} widgetConfig The configuration for the widget to be created
       * @param {number} index The index of the widget configuration in the array that it was taken from
       */
      processWidget: function alfresco_dialogs_AlfDialog__processWidget(rootNode, widgetConfig, index) {
         if (this.buttonsNode)
         {
            // Ensure that the contents of the dialog are included in any publication...
            if (widgetConfig.config == null)
            {
               widgetConfig.config = {};
            }
            if (widgetConfig.config.publishPayload == null)
            {
               widgetConfig.config.publishPayload = {};
            }
            
            var widgets = registry.findWidgets(this.containerNode);
            widgetConfig.config.publishPayload.dialogContent = widgets;
         }
         this.inherited(arguments);
      },
      
      /**
       * Iterates over any buttons that are created and calls the [attachButtonHandler]{@link module:alfresco/dialogs/AlfDialog#attachButtonHandler} 
       * function with each of them to ensure that clicking a button always results in the dialog being hidden. It is up to the 
       * buttons defined to publish a request to perform the appropriate action.
       * 
       * @instance
       * @param {Object[]}
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
       * @instance
       * @param {Object} widget The widget update
       * @paran {number} index The index of the widget in the widget array
       */
      attachButtonHandler: function alfresco_dialogs_AlfDialog__attachButtonHandler(widget, index) {
         if (widget != null)
         {
            aspect.before(widget, "onClick", lang.hitch(this, "hide"));
         }
      }
   });
});