define(["alfresco/forms/controls/MultipleEntryCreator",
        "dojo/_base/declare",
        "dojo/text!./templates/WorkflowStepCreator.html",
        "alfresco/dynamic-workflows/WorkflowStepWrapper",
        "alfresco/dynamic-workflows/WorkflowStep",
        "dojo/aspect",
        "dojo/dom-construct"], 
        function(MultipleEntryCreator, declare, template, WorkflowStepWrapper, WorkflowStep, aspect, domConstruct) {
   
   return declare([MultipleEntryCreator], {
      cssRequirements: [{cssFile:"./css/WorkflowStepCreator.css"}],
      i18nRequirements: [{i18nFile: "./i18n/WorkflowStepCreator.properties"}],
      templateString: template,
      
      /**
       * This should be passed as a constructor argument. It will be used to post topics that request
       * new details to be displayed.
       */
      pubSubScope: null,
      
      /**
       * Overrides the default Drag-And-Drop type to prevent other objects being dropped into the 
       * creator (for example, we don't want the options or the rules or anything else to be dropped
       * in).
       */
      getDNDType: function() {
         return "WorkflowStep";
      },
      
      /**
       * Override the default avatar node construction so that we use the name attribute from the value
       * to indicate what is being dragged.
       */
      createDNDAvatarNode: function(widget) {
         return domConstruct.create("div", { innerHTML: (widget && widget.value && widget.value.name) ? widget.value.name : ""});
      },
      
      /**
       */
      createElementWrapper: function(element) {
         // Create the element widget from the element configuration and then create a new
         // wrapper to hold it and add the wrapper at the end of the list of entries...
         var elementWidget = this.createElementWidget(element);
         var wrapper = new WorkflowStepWrapper({creator: this, 
                                                widget: elementWidget,
                                                pubSubScope: this.pubSubScope});
         wrapper.placeAt(this.currentEntries);
         var _this = this;
         aspect.after(wrapper, "validationRequired", function(deferred) {
            _this.alfLog("log", "Wrapper 'validationRequired' function processed");
            _this.validationRequired();
         });
         return wrapper;
      },
      
      /**
       * 
       */
      createElementWidget: function(elementConfig) {
         return new WorkflowStep({elementConfig: elementConfig,
                                  pubSubScope: this.pubSubScope});
      }
   });
});