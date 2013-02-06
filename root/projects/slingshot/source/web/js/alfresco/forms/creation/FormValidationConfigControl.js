define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "alfresco/forms/PublishForm",
        "dojo/_base/array"], 
        function(BaseFormControl, declare, PublishForm, array) {
   
   return declare([BaseFormControl], {
      
      i18nRequirements: [{i18nFile: "./i18n/FormCreation.properties"}],
      
      widgets: [
         {
            id: "regex",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "regex",
               label: "field.validation.regex.label",
               description: "field.validation.regex.description"
            }
         },
         {
            id: "errorMessage",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "errorMessage",
               label: "field.validation.errorMessage.label",
               description: "field.validation.errorMessage.description"
            }
         }
      ],
      
      getWidgetConfig: function() {
         // Return the configuration for the widget
         return {
            id : this.generateUuid(),
            name: this.name,
            widgets: this.widgets
         };
      },
      
      createFormControl: function(config, domNode) {
         return new PublishForm(config);
      },
      
      getValue: function() {
         
         var value = {};
         if (this.wrappedWidget != null) 
         {
            array.forEach(this.wrappedWidget._processedWidgets, function(widget, index) {
               value[widget.name] = widget.getValue();
            });
         }
         return value;
      }
   });
});