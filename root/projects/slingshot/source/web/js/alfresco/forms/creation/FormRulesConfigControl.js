define(["alfresco/forms/controls/MultipleEntryFormControl",
        "dojo/_base/declare",
        "alfresco/forms/creation/FormRulesConfigCreator"], 
        function(MultipleEntryFormControl, declare, FormRulesConfigCreator) {
   
   return declare([MultipleEntryFormControl], {
      
      getWidgetConfig: function() {
         
         // It's important that we set the pubSubScope as being the "fieldChangePubSubScope". This
         // is done so that the widget can listen to changes regards available fields. The list of
         // available fields is important for form rules because they need to be assigned to a field
         // to work against.
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value,
            pubSubScope: this.fieldChangePubSubScope,
            parent_alfMultipleElementId: this.parent_alfMultipleElementId,
            availableFieldsFunction: this.availableFieldsFunction,
            availableFieldsFunctionContext: this.availableFieldsFunctionContext
         };
      },
      
      createFormControl: function(config, domNode) {
         return new FormRulesConfigCreator(config);
      }
   });
});