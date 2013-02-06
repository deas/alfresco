define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "dijit/form/CheckBox"], 
        function(BaseFormControl, declare, CheckBox) {
   
   return declare([BaseFormControl], {
      
      getWidgetConfig: function() {
         // Return the configuration for the widget
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value
         };
      },
      
      createFormControl: function(config, domNode) {
         return new CheckBox(config);
      }
   });
});