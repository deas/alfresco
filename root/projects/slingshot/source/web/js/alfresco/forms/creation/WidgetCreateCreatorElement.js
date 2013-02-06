define(["alfresco/forms/controls/MultipleEntryElement",
        "dojo/_base/declare",
        "alfresco/forms/PublishForm",
        "dojo/_base/array",
        "dojo/_base/lang",
        "alfresco/forms/controls/DojoValidationTextBox",
        "alfresco/forms/controls/DojoCheckBox",
        "alfresco/forms/controls/DojoRadioButtons",
        "alfresco/forms/controls/DojoSelect",
        "alfresco/forms/controls/MultipleEntryFormControl",
        "alfresco/forms/creation/WidgetCreateControl"], 
        function(MultipleEntryElement, declare, PublishForm, array, lang, DojoValidationTextBox, DojoCheckBox, DojoRadioButtons, DojoSelect, MultipleEntryFormControl, WidgetCreateControl) {
   
   return declare([MultipleEntryElement], {
      
      /**
       * 
       */
      i18nRequirements: [{i18nFile: "./i18n/WidgetCreation.properties"}],
      
      /**
       * 
       */
      form: null,
      
      /**
       * 
       */
      createReadDisplay: function() {
         var value = this.getValue();
         this.readDisplay.innerHTML = "'" + value.id + "'";
      },

      /**
       * TODO: This is duplicated across more than one widget - could be abstracted
       */
      createEditDisplay: function() {
         if (this.form == null)
         {
            this.form = new PublishForm({widgets: this.widgets});
            this.form.placeAt(this.editDisplay);
         }
         
         // The edit display is a form containing the following...
         this.form.setValue(this.value);
         this.form.validate();
      },
      
      /**
       * TODO: This is duplicated across more than one widget - could be abstracted
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
            id: "IdField",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "id",
               label: "widget.id.label",
               description: "widget.id.description",
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
                  fixed: [
                     { label: "Horizontal Layout", value: "alfresco/layout/HorizontalWidgets"},
                     { label: "Vertical Layout", value: "alfresco/layout/VerticalWidgets"},
                     { label: "Left and Right Layout", value: "alfresco/layout/LeftAndRight"},
                     { label: "Logo", value: "alfresco/logo/Logo"},
                     { label: "Form", value: "alfresco/forms/Form"},
                     { label: "Menu", value: "alfresco/header/Menu"},
                     { label: "Menu Item", value: "alfresco/header/AlfMenuBarItem"},
                     { label: "Text box", value: "alfresco/forms/controls/DojoValidationTextBox"},
                     { label: "Select menu", value: "alfresco/forms/controls/DojoSelect"},
                     { label: "Check box", value: "alfresco/forms/controls/DojoCheckBox"},
                     { label: "Radio buttons", value: "alfresco/forms/controls/DojoRadioButtons"}
                  ]
               }
            }
         },
         {
            id: "BaseClasses",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "classes",
               label: "widget.classes.label",
               description: "widget.classes.description"
            }
         },
         {
            id: "AdditionalConfiguration",
            name: "alfresco/forms/controls/MultipleKeyValuePairFormControl",
            config: {
               name: "additionalConfig",
               label: "widget.additionalConfig.label",
               description: "widget.additionalConfig.description"
            }
         },
         {
            id: "InstantiationArguments",
            name: "alfresco/forms/controls/MultipleKeyValuePairFormControl",
            config: {
               name: "config",
               label: "widget.config.label",
               description: "widget.config.description"
            }
         },
         {
            id: "Widgets",
            name: "alfresco/forms/creation/WidgetCreateControl",
            config: {
               name: "widgets",
               label: "widget.widgets.label",
               description: "widget.widgets.description"
            }
         }
      ]
   });
});