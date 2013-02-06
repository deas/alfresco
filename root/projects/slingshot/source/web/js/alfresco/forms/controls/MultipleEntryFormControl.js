define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "alfresco/forms/controls/MultipleEntryCreator",
        "dojo/aspect"], 
        function(BaseFormControl, declare, MultipleEntryCreator, aspect) {
   
   return declare([BaseFormControl], {
      
      pubSubScope: null,
      
      constructor: function(args) {
         declare.safeMixin(this, args);
         this.pubSubScope = this.generateUuid();
      },
      
      getWidgetConfig: function() {
         // Return the configuration for the widget
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value
         };
      },
      
      createFormControl: function(config, domNode) {
         return new MultipleEntryCreator(config);
      },
      
      processValidationRules: function() {
         var valid = true;
         if (this.wrappedWidget && typeof this.wrappedWidget.validate == "function")
         {
            valid = this.wrappedWidget.validate();
         }
         this.alfLog("log", "MultipleEntryFormControl validation result:", valid);
         return valid;
      },
      
      setupChangeEvents: function() {
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