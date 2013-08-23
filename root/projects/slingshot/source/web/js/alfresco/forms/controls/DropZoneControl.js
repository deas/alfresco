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
 * @module alfresco/forms/controls/DropZoneControl
 * @extends module:alfresco/forms/controls/BaseFormControl
 * @author Dave Draper
 */
define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "alfresco/creation/DropZone",
        "dojo/json",
        "dojo/on",
        "dojo/_base/lang"], 
        function(BaseFormControl, declare, DropZone, dojoJSON, on, lang) {
   
   return declare([BaseFormControl], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/RadioButton.css"}]
       */
      cssRequirements: [{cssFile:"./css/DropZoneControl.css"}],
      
      /**
       * @instance
       */
      getWidgetConfig: function alfresco_forms_controls_DropZoneControl__getWidgetConfig() {
         // Return the configuration for the widget
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value,
            pubSubScope: this.pubSubScope
         };
      },
      
      /**
       * @instance
       */
      createFormControl: function alfresco_forms_controls_DropZoneControl__createFormControl(config, domNode) {
         return new DropZone(config);
      },
      
      /**
       * 
       */
      setupChangeEvents: function alfresco_forms_controls_DropZoneControl__setupChangeEvents() {
         on(this.domNode, "onWidgetUpdate", lang.hitch(this, "validate"));
      },
      
      /**
       * Overrides the [inherited function]{@link module:alfresco/forms/controls/BaseFormControl#getValue} to
       * call the getValue function of the wrapped DropAndPreview control
       * 
       * @instance
       * @returns {string} The widgets defined in the preview pane
       */
      getValue: function alfresco_forms_controls_DropZoneControl__getValue() {
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