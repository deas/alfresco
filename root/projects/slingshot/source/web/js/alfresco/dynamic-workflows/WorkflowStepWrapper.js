define(["alfresco/forms/controls/MultipleEntryElementWrapper",
        "dojo/_base/declare",
        "dojo/text!./templates/WorkflowStepWrapper.html",
        "dojo/dom-class",
        "dojo/_base/event"], 
        function(MultipleEntryElementWrapper, declare, template, domClass, event) {
   
   return declare([MultipleEntryElementWrapper], {
      cssRequirements: [{cssFile:"./css/WorkflowStepWrapper.css"}],
      templateString: template,
      
      /**
       * This should be passed as a constructor argument. It will be used to post topics that request
       * new details to be displayed.
       */
      pubSubScope: null,
      
      /**
       * This topic is used whenever Workflow steps are deleted. 
       */
      deleteTopic: null,
      
      /**
       * Sets up the necessary topics.
       */
      constructor: function(args) {
         declare.safeMixin(this, args);
         this.deleteTopic = this.pubSubScope + "_DELETE";
      },
      
      /**
       * This overrides the inherited function to make sure that the delete control is not hidden in edit
       * mode which is the default behaviour.
       */
      editElement: function(e) {
         this.alfLog("log", "Edit element clicked", {});
         
         // Switch the widget into edit mode...
         if (this.widget && typeof this.widget.editMode == "function")
         {
            this.widget.editMode(true);
         }
         
         // Stop the propagation of any event that is passed...
         if (e != null)
         {
            event.stop(e);
         }
      },
      
      validationRequired: function() {
         // This function intentionally does nothing However, it is used by the creator as an event to request validation.
      },
      
      /**
       * When the delete control on the wrapper is clicked we need to make sure that we publish that the step
       * has been deleted. This is important in case the details are currently being displayed.
       */
      deleteElement: function(e) {
         
         // When a delete request has been made we need to make sure that we notify any displays
         // that are currently showing the step...
         var payload = {
            saveTopic: this.widget.saveTopic
         }
         this.alfPublish(this.deleteTopic, payload);
         
         // Perform the standard delete functions...
         this.inherited(arguments);
         
         // Stop the propagation of any event that is passed...
         if (e != null)
         {
            event.stop(e);
         }
      }
      
   });
});