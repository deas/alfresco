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
 * @module alfresco/creation/DragWidgetPalette
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
        "dojo/dom-construct",
        // Only added temporarily to prevent need to dynamically require them...
        "alfresco/forms/controls/DojoValidationTextBox",
        "alfresco/forms/controls/DojoSelect",
        "alfresco/forms/controls/DojoCheckBox",
        "alfresco/forms/controls/MultipleKeyValuePairFormControl",
        "alfresco/forms/creation/FormRulesConfigControl"], 
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
         palette.insertNodes(false, this.getPaletteItems());
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
            title: item.data.name,
            iconClass: item.data.iconClass
         }));
         return {node: node, data: item, type: ["widget"]};
      },
      
      /**
       * @instance
       * @returns {object[]}
       */
      getPaletteItems: function alfresco_creationDragWidgetPalette__getPaletteItems() {
         return [
            {
               data: {
                  name: "Text",
                  module: "alfresco/forms/controls/DojoValidationTextBox",
                  iconClass: "textbox",
                  defaultConfig: {
                     name: "default",
                     label: "Text box",
                     description: "Default description",
                     unitsLabel: "units"
                  },
                  configWidgets: [
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "name",
                           label: "Post parameter",
                           value: "default",
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "label",
                           label: "Label",
                           value: "Text box"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "description",
                           label: "Description",
                           value: "Default description"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "unitsLabel",
                           label: "Units Label",
                           value: "units"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoCheckBox",
                        config: {
                           name: "visibilityConfig.initialValue",
                           label: "Initially visible",
                           value: true
                        }
                     },
                     {
                        name: "alfresco/forms/creation/FormRulesConfigControl",
                        config: {
                           name: "visibilityConfig.rules",
                           label: "Dynamic visibility behaviour configuration"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoCheckBox",
                        config: {
                           name: "requirementConfig.initialValue",
                           label: "Initially required",
                           value: false
                        }
                     },
                     {
                        name: "alfresco/forms/creation/FormRulesConfigControl",
                        config: {
                           name: "requirementConfig.rules",
                           label: "Dynamic requirement behaviour configuration"
                        }
                     }
                     ,
                     {
                        name: "alfresco/forms/controls/DojoCheckBox",
                        config: {
                           name: "disablementConfig.initialValue",
                           label: "Initially disabled",
                           value: false
                        }
                     },
                     {
                        name: "alfresco/forms/creation/FormRulesConfigControl",
                        config: {
                           name: "disablementConfig.rules",
                           label: "Dynamic disablement behaviour configuration"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoSelect",
                        config: {
                           name: "validationConfig.regex",
                           label: "Validation rules",
                           optionsConfig: {
                              fixed: [
                                 { label: "None", value: ".*"},
                                 { label: "E-mail", value: "^([0-9a-zA-Z]([-.\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\w]*[0-9a-zA-Z]\.)+[a-zA-Z]{2,9})$"},
                                 { label: "Number", value: "^([0-9]+)$"}
                              ]
                           }
                        }
                     }
                  ]
               },
               type: [ "widget" ]
            },
            {
               data: {
                  name: "Textarea",
                  module: "alfresco/forms/controls/DojoTextarea",
                  iconClass: "textarea",
                  defaultConfig: {
                     name: "default",
                     label: "Text area",
                     description: "Default description"
                  },
                  configWidgets: [
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "name",
                           label: "Post parameter",
                           value: "default"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "label",
                           label: "Label",
                           value: "Text area"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoTextarea",
                        config: {
                           name: "description",
                           label: "Description",
                           value: "Default description"
                        }
                     }
                  ]
               },
               type: [ "widget" ]
            },
            {
               data: {
                  name: "DropDown",
                  module: "alfresco/forms/controls/DojoSelect",
                  iconClass: "dropdown",
                  defaultConfig: {
                     name: "default",
                     label: "Drop down",
                     description: "Default description",
                     unitsLabel: "units",
                     optionsConfig: {
                        fixed: [
                           { label: "Option1", value: "Value1"},
                           { label: "Option2", value: "Value2"}
                        ]
                     }
                  },
                  configWidgets: [
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "name",
                           label: "Post parameter",
                           value: "default"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "label",
                           label: "Label",
                           value: "Drop down"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "description",
                           label: "Description",
                           value: "Default description"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "unitsLabel",
                           label: "Units Label",
                           value: "units"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/MultipleKeyValuePairFormControl",
                        config: {
                           name: "optionsConfig.fixed",
                           label: "Options"
                        }
                     }
                  ]
               },
               type: [ "widget" ]
            },
            {
               data: {
                  name: "Check box",
                  module: "alfresco/forms/controls/DojoCheckBox",
                  iconClass: "checkbox",
                  defaultConfig: {
                     name: "default",
                     label: "Check box",
                     description: "Default description"
                  },
                  configWidgets: [
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "name",
                           label: "Post parameter",
                           value: "default"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "label",
                           label: "Label",
                           value: "Check box"
                        }
                     },
                     {
                        name: "alfresco/forms/controls/DojoValidationTextBox",
                        config: {
                           name: "description",
                           label: "Description",
                           value: "Default description"
                        }
                     }
                  ]
               },
               type: [ "widget" ]
            }
         ];
      }
      
   });
});