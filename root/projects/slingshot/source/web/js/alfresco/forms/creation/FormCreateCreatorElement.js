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
define(["alfresco/forms/controls/MultipleEntryElement",
        "dojo/_base/declare",
        "alfresco/forms/PublishForm",
        "dojo/_base/array",
        "alfresco/forms/controls/DojoValidationTextBox",
        "alfresco/forms/controls/DojoCheckBox",
        "alfresco/forms/controls/DojoRadioButtons",
        "alfresco/forms/controls/DojoSelect"], 
        function(MultipleEntryElement, declare, PublishForm, array, DojoValidationTextBox, DojoCheckBox, DojoRadioButtons, DojoSelect) {
   
   // PLEASE NOTE: We should always ensure the "define" statement contains all of the form controls that we want to be able to select from
   //              this is so that all their CSS and i18n properties files will be loaded.
   
   return declare([MultipleEntryElement], {
      
      i18nRequirements: [{i18nFile: "./i18n/FormCreation.properties"}],
      
      
      /**
       * A Publication/Subcription scope should be set as a constructor argument. This is important so that
       * the parent Creator instance can keep track of changes to properties. In particular it needs to know
       * when the "key" property changes value. This is so that field options can be controlled.
       */
      pubSubScope: null,
      
      /**
       * This is a special scope that is reserved for notifications regarding changes to the values of fields.
       */
      fieldChangePubSubScope: null,
      
      
      form: null,
      
      createReadDisplay: function() {
         var value = this.getValue();
         this.readDisplay.innerHTML = "Field name = " + value.field;
      },

      createEditDisplay: function() {
         
         // Get the currently available fields...
         var availableFieldsToConfigure = this.availableFieldsFunction(this.availableFieldsFunctionContext);
         
         // Iterate through the widget configuration and setup the current list of available widgets...
         var _this = this;
         array.forEach(this.widgets, function(item, index) {
            if (item.id == "visibilityConfig" || item.id == "requirementConfig" || item.id == "disablementConfig")
            {
               // Set the currently available fields as a configuration attribute...
               item.config.parent_alfMultipleElementId = _this.value._alfMultipleElementId;
               item.config.availableFieldsFunction = _this.availableFieldsFunction;
               item.config.availableFieldsFunctionContext = _this.availableFieldsFunctionContext;
            }
         });
         
         if (this.form == null)
         {
            this.form = new PublishForm({
               pubSubScope: this.pubSubScope,
               widgets: this.widgets});
            this.form.placeAt(this.editDisplay);
         }
         
         // The edit display is a form containing the following...
         this.form.setValue(this.value);
         this.form.validate();
      },
      
      /**
       * Overrides the default implementation to return a new object consisting of the current
       * data that has been set through updates to the controls in the associated form.
       */
      getValue: function(meaningful) {
         var value = {};
         // The form is only used for the edit display. If it exists then we will defer to the values
         // contained within the form. If it does not exist we will use the value assigned to the element.
         // The latter condition will be true when the FormCreateCreator has been instantiated from previously
         // stored configuration but the edit mode of the element has not been used.
         if (this.form != null) 
         {
            array.forEach(this.form._processedWidgets, function(widget, index) {
               value[widget.name] = widget.getValue(meaningful);
            });
            
         }
         else
         {
            value = this.value;
         }
         
         // Make sure that we store the value returned from the form so that the next time the edit display
         // is called we will be able to instantiate it with the most up-to-date data...
         if (!meaningful)
         {
            this.value = value;
         }
         return value;
      },
      
      /**
       * Calls the validate() function on form if it exists and returns the result.
       */
      validate: function() {
         var valid = true;
         if (this.form)
         {
            valid = this.form.validate();
         }
         return valid;
      },
      
      /**
       * This is the widget configuration for the Form to use for creating the element.
       */
      widgets: [
         {
            id: "_alfMultipleElementId",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "_alfMultipleElementId",
               label: "_alfMultipleElementId",
               visibilityConfig: {
                  initialValue: false
               }
            }
         },
         {
            id: "name",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "field",
               label: "field.name.label",
               description: "field.name.description",
               requirementConfig: {
                  initialValue: true
               }
            }
         },
         {
            id: "WidgetName",
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "name",
               label: "widget.type.label",
               description: "widget.type.description",
               optionsConfig: {
                  makeXhr: Alfresco.constants.URL_SERVICECONTEXT + "share/widget/options",
                  fixed: [
                     { label: "Text box", value: "alfresco/forms/controls/DojoValidationTextBox"},
                     { label: "Select menu", value: "alfresco/forms/controls/DojoSelect"},
                     { label: "Check box", value: "alfresco/forms/controls/DojoCheckBox"},
                     { label: "Radio buttons", value: "alfresco/forms/controls/DojoRadioButtons"}
                  ]
               }
            }
         },
         {
            id: "label",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "label",
               label: "field.label.label",
               description: "field.label.description"
            }
         },
         {
            id: "description",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "description",
               label: "field.description.label",
               description: "field.description.description"
            }
         },
         {
            id: "unitsLabel",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "unitsLabel",
               label: "field.units.label",
               description: "field.units.description"
            }
         },
         {
            id: "optionsConfig",
            name: "alfresco/forms/creation/FormOptionsConfigControl",
            config: {
               name: "optionsConfig",
               label: "field.options.label",
               description: "field.options.description"
            }
         },
         {
            id: "validationConfig",
            name: "alfresco/forms/creation/FormValidationConfigControl",
            config: {
               name: "validationConfig",
               label: "field.validation.config.label",
               description: "field.validation.config.description"
            }
         },
         {
            id: "visibilityConfig",
            name: "alfresco/forms/creation/FormRulesConfigControl",
            config: {
               name: "visibilityConfig",
               label: "field.visibility.config.label",
               description: "field.visibility.config.description"
            }
         },
         {
            id: "requirementConfig",
            name: "alfresco/forms/creation/FormRulesConfigControl",
            config: {
               name: "requirementConfig",
               label: "field.requirement.config.label",
               description: "field.requirement.config.description"
            }
         },
         {
            id: "disablementConfig",
            name: "alfresco/forms/creation/FormRulesConfigControl",
            config: {
               name: "disablementConfig",
               label: "field.disablement.config.label",
               description: "field.disablement.config.description"
            }
         }
      ]
   });
});