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
 * @module alfresco/forms/controls/MultipleEntryFormControl
 * @extends alfresco/forms/controls/BaseFormControl
 * @author Dave Draper
 */
define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "alfresco/forms/controls/MultipleEntryCreator",
        "dojo/aspect"], 
        function(BaseFormControl, declare, MultipleEntryCreator, aspect) {
   
   return declare([BaseFormControl], {
      
      /**
       * @instance
       */
      constructor: function alfresco_forms_controls_MultipleEntryFormControl__constructor(args) {
         declare.safeMixin(this, args);
      },
      
      /**
       * @instance
       * @returns {object} The widget config.
       */
      getWidgetConfig: function alfresco_forms_controls_MultipleEntryFormControl__getWidgetConfig() {
         // Return the configuration for the widget
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value,
            elementWrapper: (this.elementWrapper != null) ? this.elementWrapper : "alfresco/forms/controls/MultipleEntryElementWrapper",
            elementWidget: (this.elementWidget != null) ? this.elementWidget : "alfresco/forms/controls/MultipleEntryElement"
         };
      },
      
      /**
       * @instance
       * @returns {object} A new alfresco/forms/controls/MultipleEntryCreator
       */
      createFormControl: function alfresco_forms_controls_MultipleEntryFormControl__createFormControl(config, domNode) {
         return this.createWidget({
            name: "alfresco/forms/controls/MultipleEntryCreator",
            config: config
         });
      },
      
      /**
       * @instance
       * @returns {boolean}
       */
      processValidationRules: function alfresco_forms_controls_MultipleEntryFormControl__processValidationRules() {
         var valid = true;
         if (this.wrappedWidget && typeof this.wrappedWidget.validate == "function")
         {
            valid = this.wrappedWidget.validate();
         }
         this.alfLog("log", "MultipleEntryFormControl validation result:", valid);
         return valid;
      },
      
      /**
       * @instance
       */
      setupChangeEvents: function alfresco_forms_controls_MultipleEntryFormControl__setupChangeEvents() {
         var _this = this;
         
         // Whenever a widgets value changes then we need to publish the details out to other form controls (that exist in the
         // same scope so that they can modify their appearance/behaviour as necessary)...
         if (this.wrappedWidget)
         {
            aspect.after(this.wrappedWidget, "validationRequired", function(deferred) {
               _this.alfLog("log", "Wrapper 'validationRequired' function processed");
               _this.validate();
            });
         }
      }
   });
});