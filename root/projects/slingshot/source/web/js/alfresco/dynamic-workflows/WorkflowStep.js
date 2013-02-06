define(["alfresco/forms/controls/MultipleEntryElement",
        "dojo/_base/declare",
        "dojo/text!./templates/WorkflowStep.html",
        "dojo/dom-class"], 
        function(MultipleEntryElement, declare, template, domClass) {
   
   return declare([MultipleEntryElement], {
      cssRequirements: [{cssFile:"./css/WorkflowStep.css"}],
      i18nRequirements: [{i18nFile: "./i18n/WorkflowStep.properties"}],
      templateString: template,
      
      /**
       * This should be passed as a constructor argument. It will be used to post topics that request
       * new details to be displayed.
       */
      pubSubScope: null,
      
      saveTopic: null,
      
      displayTopic: null,
      
      /**
       * This attribute keeps track of the steps validity. It is initialised to true and will be updated each time
       * the step is saved. Because the step doesn't manage it's own edit display (this is deferred to the WorkflowDetails
       * class) it's not possible to perform validation inline. However the validity is returned in the save publication
       * which is how it can be captured.
       */
      _stepValid: true,
      
      /**
       * Provides additional code to generate a subscription topic prefix for receiving notifications of 
       * 
       */
      constructor: function(args) {
         declare.safeMixin(this, args);
         this.displayTopic = this.pubSubScope + "_DISPLAY";
         this.saveTopic = this.generateUuid() + "_SAVE";
         
         var _this = this;
         this.alfSubscribe(this.saveTopic, function(payload) {
            _this.alfLog("log", "Received save publication", payload);
            if (payload.values)
            {
               // Update the value and the validity with the data from the save request...
               _this.value = payload.values;
               _this._stepValid = payload.valid;
               
               // Create the updated read display using the new data...
               _this.createReadDisplay();
               if (_this.wrapper)
               {
                  // Calling the validationRequired function on the wrapper will result in an aspect being
                  // triggered that will pass the validation request up to the overall WorkflowStepCreateControl
                  _this.wrapper.validationRequired();
                  domClass.remove(_this.wrapper.domNode, "workflow-step-selected");
               }
            }
            else
            {
               _this.alfLog("warn", "A publication was received to save a WorkflowStep but no value attribute was included in the payload.");
            }
         });
      },
      
      /**
       * 
       */
      validate: function() {
         return this._stepValid;
      },
      
      createReadDisplay: function() {
         this.readDisplayTitle.innerHTML = (this.value != null && this.value.name != null) ? this.value.name : "";
         this.readDisplayDescription.innerHTML = (this.value != null && this.value.description != null) ? this.value.description : "";
      },
      
      /**
       * The edit display should NEVER be shown as normal. Instead we want to pass the content
       * for another viewer to display it.
       */
      createEditDisplay: function() {
         // There is no edit display as such. We just want to create a subscription to update our value.
      },
      
      /**
       * When the WorkflowStep enters edit mode we want to essentially highlight it. We
       * are not going to reveal the edit content.
       */
      editMode: function(isEditMode) {
         this.alfLog("log", "Toggling edit mode, edit?", isEditMode);
         if (isEditMode)
         {
            // Publish a new event with the details of the step to be displayed...
            if (this.pubSubScope != null)
            {
               // The payload should contain the value of the step. 
               var payload = {
                  structure: this.widgets,
                  values: this.getValue(),
                  saveTopic: this.saveTopic
               };
               this.alfLog("log", "Publishing Workflow display request", payload);
               this.alfPublish(this.displayTopic, payload);
               
               // Add a highlight to the wrapper (this has to be done after the publication because
               // the resulting save will remove the highlight so we need to ensure that when currently
               // selected step is selected we do not remove the highlight).
               if (this.wrapper)
               {
                  domClass.add(this.wrapper.domNode, "workflow-step-selected");
               }
            }
            else
            {
               this.alfLog("error", "No 'pubSubScope' attribute has been set. It is not possible to publish a request to update the workflow details displayed");
            }
         }
         else
         {
            // No action
         }
      },
      
      /**
       * This defines the structure for the step form (including the form creation part of the step).
       * This will get passed to be displayed when a new step is created or edited.
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
               name: "name",
               label: "workflow.step.name.label",
               requirementConfig: {
                  initialValue: true
               }
            }
         },
         {
            id: "description",
            name: "alfresco/forms/controls/DojoTextarea",
            config: {
               name: "description",
               label: "workflow.step.description.label"
            }
         },
         {
            id: "assignee",
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "assignee",
               label: "workflow.step.assignee.label",
               optionsConfig: {
                  fixed: [
                     { label: "workflow.step.assignee.user.label", value: "user"},
                     { label: "workflow.step.assignee.member.label", value: "member"},
                     { label: "workflow.step.assignee.previous.label", value: "previous"}
                  ]
               }
            }
         },
         {
            id: "stepForm",
            name: "alfresco/forms/creation/FormCreateControl",
            config: {
               name: "stepForm",
               label: "workflow.step.form.label"
            }
         }
      ]
   });
});