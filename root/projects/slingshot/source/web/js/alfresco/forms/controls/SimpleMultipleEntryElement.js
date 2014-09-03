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
 * @module alfresco/forms/controls/SimpleMultipleEntryElement
 * @extends module:alfresco/forms/controls/MultipleEntryElement
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/forms/controls/MultipleEntryElement"], 
        function(declare, MultipleEntryElement) {
   
   return declare([MultipleEntryElement], {
      
      /**
       * This is the default function for determining the unique key to identify the the element amongst its
       * peers. This function will most likely need to be overridden by extending classes that handle more
       * complex data types. 
       * 
       * @instance
       */
      determineKeyAndValue: function alfresco_forms_controls_SimpleMultipleEntryElement__determineKeyAndValue() {
         this.alfLog("log", "DetermineKeyAndValue", this);
         if (this.elementConfig != null && this.elementConfig.value)
         {
            this.elementValue = this.elementConfig.value;
         }
         else
         {
            this.elementValue = "";
         }
      },

      /**
       * The default read display simply shows the value of the element.
       * 
       * @instance
       */
      createReadDisplay: function alfresco_forms_controls_SimpleMultipleEntryElement__createReadDisplay() {
         var currentValue = this.getValue();
         this.readDisplay.innerHTML = currentValue;
      },

      /**
       * Sets the value of the internal form.
       * 
       * @instance
       */
      setFormValue: function alfresco_forms_controls_SimpleMultipleEntryElement__setFormValue(value) {
         this.form.setValue({
            value: this.elementValue
         });
      },

      /**
       * Returns the widgets to be used in the form created for edit mode.
       * 
       * @instance
       * @returns {object[]}
       */
      getFormWidgets: function alfresco_forms_controls_SimpleMultipleEntryElement__getFormWidgets() {
         return [
            {
               name: "alfresco/forms/controls/DojoValidationTextBox",
               config: {
                  name: "value",
                  label: "multi.element.value.label",
                  description: "multi.element.value.description",
                  value: this.elementValue,
                  requirementConfig: {
                     initialValue: true
                  }
               }
            }
         ];
      },
      
      /**
       * This is called whenever the text box in the edit view is updated.
       * 
       * @instance
       * @param {string} name The name of the changed element
       * @param {string} oldValue The value before the change
       * @param {string} value The value after the change
       */
      onElementValueChange: function alfresco_forms_controls_SimpleMultipleEntryElement__onElementValueChange(name, oldValue, value) {
         this.elementValue = value;
         this.createReadDisplay();
      },

      /**
       * @instance
       * @returns {object}
       */
      getValue: function alfresco_forms_controls_SimpleMultipleEntryElement__getValue() {
         var value = "";
         if (this.form != null) 
         {
            value = this.form.getValue().value;
         }
         else
         {
            value = this.elementValue;
         }
         return value; 
      }
   });
});