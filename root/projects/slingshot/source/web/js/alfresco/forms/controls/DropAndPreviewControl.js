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
 * @module alfresco/forms/controls/DropAndPreviewControl
 * @extends module:alfresco/forms/controls/BaseFormControl
 * @author Dave Draper
 */
define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "alfresco/creation/DropAndPreview",
        "dojo/json"], 
        function(BaseFormControl, declare, DropAndPreview, dojoJSON) {
   
   return declare([BaseFormControl], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/RadioButton.css"}]
       */
      cssRequirements: [{cssFile:"./css/DropAndPreviewControl.css"}],
      
      /**
       * @instance
       */
      getWidgetConfig: function alfresco_forms_controls_DropAndPreviewControl__getWidgetConfig() {
         // Return the configuration for the widget
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value
         };
      },
      
      /**
       * @instance
       */
      createFormControl: function alfresco_forms_controls_DropAndPreviewControl__createFormControl(config, domNode) {
         this.processWidgets(this.widgets, this._controlNode);
         return this.slideOverlay.layoutWidget.previewWidget;
      },
      
      /**
       * Overrides the [default function]{@link module:alfresco/forms/controls/BaseFormControl#placeWidget}
       * to perform no action as all change events are setup during instantiation of the editor.
       * 
       * @instance
       */
      placeWidget: function alfresco_forms_controls_DropAndPreviewControl__placeWrappedWidget() {
         // No action required. The widget is placed during the createFormControl function.
      },
      
      widgets: [
         {
            name: "alfresco/layout/SlideOverlay",
            assignTo: "slideOverlay",
            className: "alfresco-forms-controls-DropAndPreviewControl",
            config: {
               showTopics: ["ALF_CONFIGURE_WIDGET"],
               hideTopics: ["ALF_UPDATE_RENDERED_WIDGET","ALF_CLEAR_CONFIGURE_WIDGET"],
               adjustHeightTopics: ["ALF_CONFIGURE_WIDGET"],
               widgets: [
                  {
                     name: "alfresco/layout/HorizontalWidgets",
                     align: "underlay",
                     assignTo: "layoutWidget",
                     config: {
                        widgets: [
                           {
                              name: "alfresco/creation/DropAndPreview",
                              assignTo: "previewWidget",
                              config: {
                              }
                           },
                           {
                              name: "alfresco/creation/DragWidgetPalette",
                              config: {
                              }
                           }
                        ]
                     }
                  },
                  {
                     name: "alfresco/creation/WidgetConfig",
                     align: "overlay",
                     assignTo: "configWidget",
                     config: {
                        width: "50%"
                     }
                  }
               ]
            }
         }
      ],
      
      /**
       * Overrides the [inherited function]{@link module:alfresco/forms/controls/BaseFormControl#getValue} to
       * call the getValue function of the wrapped DropAndPreview control
       * 
       * @instance
       * @returns {string} The widgets defined in the preview pane
       */
      getValue: function alfresco_forms_controls_DropAndPreviewControl__getValue() {
         var value = "";
         if (this.wrappedWidget != null)
         {
            value = {
               widgets: this.wrappedWidget.getWidgetDefinitions()
            };
            value = dojoJSON.stringify(value);
         }
         return value;
      }
   });
});