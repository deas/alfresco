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
 * 
 * 
 * @module alfresco/creation/DragPalette
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/DragWidgetPalette.html",
        "dojo/text!./templates/WidgetTemplate.html",
        "alfresco/core/Core",
        "dojo/dnd/Source",
        "dojo/_base/lang",
        "dojo/string",
        "dojo/dom-construct"], 
        function(declare, _Widget, _Templated, template, WidgetTemplate, AlfCore, Source, lang, stringUtil, domConstruct) {
   
   return declare([_Widget, _Templated, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/DragWidgetPalette.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/DragWidgetPalette.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type template {String}
       */
      templateString: template,
      
      /**
       * @instance
       * @type {boolean}
       * @default false
       */
      dragWithHandles: false,
      
      /**
       * @instance
       */
      postCreate: function alfresco_creation_DragWidgetPalette__postCreate() {
         var palette = new Source(this.paletteNode, {
            copyOnly: true,
            selfCopy: false,
            creator: lang.hitch(this, "creator"),
            withHandles: this.dragWithHandles
         });
         palette.insertNodes(false, this.widgetsForPalette);
      },
      
      /**
       * Handles the creation of drag'n'drop avatars. This could check the supplied hint parameter
       * to see if an avatar is required, but since the source doesn't allow self-copying and is not
       * a target in itself then this is not necessary.
       * 
       * @instance
       */
      creator: function alfresco_creation_DragWidgetPalette__creator(item, hint) {
         this.alfLog("log", "Creating", item, hint);
         var node = domConstruct.toDom(stringUtil.substitute(WidgetTemplate, {
            title: (item.name != null) ? item.name : "",
            iconClass: (item.iconClass != null) ? item.iconClass : ""
         }));
         return {node: node, data: item, type: ["widget"]};
      },
      
      /**
       * @instance
       * @returns {object[]}
       */
      widgetsForPalette: [
         {
            type: ["widget"],
            name: "Menu Bar",
            module: "alfresco/menus/AlfMenuBar",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               name: "default"
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "name",
                     label: "Name",
                     value: "default",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: true
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Menu Bar Item",
            module: "alfresco/menus/AlfMenuBarItem",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               label: "default"
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "label",
                     label: "Label",
                     value: "default",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: true
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Menu Bar Drop-down menu",
            module: "alfresco/menus/AlfMenuBarPopup",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               label: "default"
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "label",
                     label: "Label",
                     value: "default",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: false
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Menu Group",
            module: "alfresco/menus/AlfMenuGroup",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               label: "default"
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "label",
                     label: "Label",
                     value: "default",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: false
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Menu Item",
            module: "alfresco/menus/AlfMenuItem",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               label: "default"
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "label",
                     label: "Label",
                     value: "default",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: true
                  }
               }
            ]
         },
         {
            type: ["widget"],
            name: "Cascading Menu",
            module: "alfresco/menus/AlfCascadingMenu",
            // This is the initial configuration that will be provided when the widget
            // is dropped into the drop-zone...
            defaultConfig: {
               label: "default"
            },
            // These are the widgets used to configure the dropped widget.
            widgetsForConfig: [
               {
                  name: "alfresco/forms/controls/DojoValidationTextBox",
                  config: {
                     name: "label",
                     label: "Label",
                     value: "default",
                  }
               }
            ],
            // If set to true, then the actual widget will be previewed...
            previewWidget: false,
            // This is the widget structure to use to display the widget.
            widgetsForDisplay: [
               {
                  name: "alfresco/creation/DropZone",
                  config: {
                     horizontal: false
                  }
               }
            ]
         }
      ]
   });
});