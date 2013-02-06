/**
 * This defines an individual control that can be used as part of a form for constructing Workflow steps
 */
define(["alfresco/forms/controls/MultipleEntryFormControl",
        "dojo/_base/declare",
        "alfresco/dynamic-workflows/WorkflowStepCreator"], 
        function(MultipleEntryFormControl, declare, WorkflowStepCreator) {
   
   return declare([MultipleEntryFormControl], {
      
      /**
       * This should be passed as a constructor argument. It will be used to post topics that request
       * new details to be displayed.
       */
      pubSubScope: null,
      
      getWidgetConfig: function() {
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value,
            pubSubScope: this.pubSubScope
         };
      },
      
      createFormControl: function(config, domNode) {
         return new WorkflowStepCreator(config);
      }
   });
});