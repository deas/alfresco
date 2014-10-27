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
 * @module alfresco/forms/controls/SimplePicker
 * @extends module:alfresco/forms/controls/BaseFormControl
 * @author Dave Draper
 */
define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "alfresco/core/CoreWidgetProcessing",
        "alfresco/core/ObjectProcessingMixin",
        "dojo/_base/lang"], 
        function(BaseFormControl, declare, CoreWidgetProcessing, ObjectProcessingMixin, lang) {
   
   return declare([BaseFormControl, CoreWidgetProcessing, ObjectProcessingMixin], {
      
      /**
       * @instance
       */
      getWidgetConfig: function alfresco_forms_controls_SimplePicker__getWidgetConfig() {
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
      createFormControl: function alfresco_forms_controls_SimplePicker__createFormControl(config, domNode) {
         this.lastValue = this.value != null ? this.value : [];

         // Create a specific item selection scope to ensure that the picker publications don't interfere
         // with the surrounding forms...
         this.itemSelectionPubSubScope = this.generateUuid();
         var widgetsForControl = lang.clone(this.widgetsForControl);
         this.processObject(["processInstanceTokens"], widgetsForControl);
         return this.processWidgets(widgetsForControl, this._controlNode);
      },

      /**
       * The picker widgets.
       * 
       * @instance
       * @type {array}
       */
      widgetsForControl: [
         {
            name: "alfresco/pickers/ContainerPicker",
            config: {
               generatePubSubScope: false,
               pubSubScope: "{itemSelectionPubSubScope}",
               singleItemMode: true
            }
         }
      ],
      
      /**
       * Overrides the default change events to use blur events on the text box. This is done so that we can validate
       * on every single keypress. However, we need to keep track of old values as this information is not readily
       * available from the text box itself.
       * 
       * @instance
       */
      setupChangeEvents: function alfresco_forms_controls_SimplePicker__setupChangeEvents() {
         this.alfSubscribe(this.itemSelectionPubSubScope+"ALF_ITEMS_SELECTED", lang.hitch(this, this.onItemsSelected),true);
      },

      /**
       *
       * 
       * @instance
       * @param {object} payload
       */
      onItemsSelected: function alfresco_forms_controls_SimplePicker__onItemsSelected(payload) {
         this.value = lang.clone(payload.pickedItems);
         this.onValueChangeEvent(this.name, this.lastValue, this.value);
         this.lastValue = this.value;
      },

      /**
       * Overides the [inherited function]{@link module:alfresco/forms/controls/BaseFormControl#processValidationRules}
       * since the only validation for a picker is if a value is required the length of the value array must
       * not be 0.
       *
       * @instance
       * @returns {boolean} True if not required or is required and value has length greater than zero
       */
      processValidationRules: function alfresco_forms_controls_SimplePicker__processValidationRules() {
         var valid = true;
         if (this._required === true && this.value.length === 0)
         {
            valid = false;
         }
         return valid;
      },

      /**
       * Overides the [inherited function]{@link module:alfresco/forms/controls/BaseFormControl#getValue}
       * to get the current picked items value.
       * 
       * @instance
       * @param {object} payload
       */
      getValue: function alfresco_forms_controls_SimplePicker__getValue() {
         return this.value;
      },

      /**
       * Overides the [inherited function]{@link module:alfresco/forms/controls/BaseFormControl#setValue}
       * to set the current picked items.
       * 
       * @instance
       * @param {object} payload
       */
      setValue: function alfresco_forms_controls_SimplePicker__setValue(value) {
         this.alfPublish(this.itemSelectionPubSubScope+"ALF_SET_PICKED_ITEMS", {
            pickedItems: value
         }, true);
      }
   });
});