define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "dijit/form/Select",
        "dijit/focus"], 
        function(BaseFormControl, declare, Select, focusUtil) {
   
   return declare([BaseFormControl], {
      
      getWidgetConfig: function() {
         // Return the configuration for the widget
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value,
            options: (this.options != null) ? this.options : []
         };
      },
      
      createFormControl: function(config, domNode) {
         return new Select(config);
      }
   });
});