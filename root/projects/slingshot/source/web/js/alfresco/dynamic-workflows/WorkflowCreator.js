/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/WorkflowCreator.html",
        "alfresco/core/Core",
        "alfresco/dynamic-workflows/WorkflowStepCreateControl",
        "alfresco/dynamic-workflows/WorkflowDetails",
        "dojo/on",
        "dojo/_base/lang"], 
        function(declare, _Widget, _Templated, template, AlfCore, WorkflowStepCreateControl, WorkflowDetails, on, lang) {
   
   return declare([_Widget, _Templated, AlfCore], {
      cssRequirements: [{cssFile:"./css/WorkflowCreator.css"}],
      i18nRequirements: [{i18nFile: "./i18n/WorkflowCreator.properties"}],
      templateString: template,
      
      /**
       * A reference to the control that will be used to create each of the workflow steps.
       */
      control: null,
      
      /** 
       * A reference to the widget that will display the details of the workflow
       * and the steps. It is updated by publications on the "pubSubScope" 
       */
      workflowDetails: null,
      
      /**
       * This should be initialised in the construction of the widget and is used as a prefix
       * of all pub/sub communications to ensure that it does not communicate outside of its intended
       * scope.
       */
      pubSubScope: null,
      
      saveTopic: null,
      
      displayTopic: null,

      _workflowValue: null,
      _workflowSteps: null,

      /**
       * Initialises the main value of the workflow (this will be rendered in a form describing the
       * details of the workflow) and the individual steps in the workflow (these will be rendered
       * in the WorkflowStepCreateControl.
       */
      _initialiseValues: function() {
         if (this.value == null)
         {
            this.alfLog("log", "No value initial value provided for the Workflow");
            this._workflowValue = {};
            this._workflowSteps = [];
         }
         else
         {
            if (this.value.details && this.value.details instanceof Object)
            {
               this._workflowValue = this.value.details;
            }
            else
            {
               this.alfLog("log", "The value provided for the details of the Workflow was NOT an object:", this.value.details);
               this._workflowValue = {};
            }
            
            if (this.value.steps && this.value.steps instanceof Array)
            {
               this._workflowSteps = this.value.steps;
            }
            else
            {
               this.alfLog("log", "The value provided for the steps of the Workflow was NOT an array:", this.value.steps);
               this._workflowSteps = [];
               
            }
         }
      },
      
      /**
       * Provides additional code to generate a subscription topic prefix for receiving notifications of 
       */
      constructor: function(args) {
         declare.safeMixin(this, args);
         
         // Initialise the values (sets up the workflow details and steps) based on data supplied in the
         // construction of the widget...
         this._initialiseValues();

         // Generate a new unique id to prefix the topics for requesting to display
         // the workflow or step details (it doesn't matter what the value is because
         // it is passed to the widgets for them to re-use)...
         this.pubSubScope = this.generateUuid();
         // Set up the topics for pub/sub behaviour...
         this.displayTopic = this.pubSubScope + "_DISPLAY";
         this.saveTopic = this.generateUuid() + "_SAVE";
         
         // Setup a subscription to handle save events published from the WorkflowDetails widget (or elsewhere)...
         var _this = this;
         this.alfSubscribe(this.saveTopic, function(payload) {
            _this.alfLog("log", "Received save publication", payload);
            if (payload.values)
            {
               _this._workflowValue = payload.values;
               if (_this.wrapper)
               {
                  domClass.remove(_this.wrapper.domNode, "workflow-step-selected");
               }
            }
            else
            {
               _this.alfLog("warn", "A publication was received to save a WorkflowStep but no value attribute was included in the payload.");
            }
         });
      },
      
      postCreate: function() {
         // Create an editor for adding the JSON code...
         this.control = new WorkflowStepCreateControl({name: "workflowSteps",
                                                       value: this._workflowSteps,
                                                       pubSubScope: this.pubSubScope});
         this.control.placeAt(this.workflowStepsNode);
         
         // When the user clicks anywhere inside the control (but outside of a step) then we want to display
         // the details of the overall workflow...
         var _this = this;
         on(this.workflowStepsNode, "click", function() {
            _this.displayWorkflowDetails();
         });
         
         // Create the workflow details form...
         this.workflowDetails = new WorkflowDetails({pubSubScope: this.pubSubScope});
         this.workflowDetails.placeAt(this.workflowDetailsNode);
         
         // Display the overall details when the widget is first created...
         this.displayWorkflowDetails();
      },
      
      /**
       * This publishes an event that requests that the overall Workflow details (i.e. NOT details of the 
       * individual steps) be displayed. 
       */
      displayWorkflowDetails: function() {
         if (this.pubSubScope != null)
         {
            var payload = {
               structure: this.widgets,
               values: this._workflowValue,
               saveTopic: this.saveTopic
            };
            this.alfLog("log", "Publishing Workflow dislay request", payload);
            this.alfPublish(this.displayTopic, payload);
         }
         else
         {
            this.alfLog("warn", "No 'pubSubScope' value provided for WorkflowStepCreator");
         }
      },
      
      /**
       * Defines the structure of the form used to display the Workflow details
       */
      widgets: [
         {
            id: "name",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "name",
               label: "workflow.name.label",
               requirementConfig: {
                  initialValue: true
               }
            }
         },
         {
            id: "status",
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "status",
               label: "workflow.status.label",
               disablementConfig: {
                  initialValue: true
               }
            }
         },
         {
            id: "description",
            name: "alfresco/forms/controls/DojoTextarea",
            config: {
              name: "description",
               label: "workflow.description.label"
            }
         },
         {
            id: "startableBy",
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "startableBy",
               label: "workflow.startableBy.label",
               optionsConfig: {
                  fixed: [
                     { label: "workflow.startableBy.users.label", value: "users"},
                     { label: "workflow.startableBy.groups.label", value: "groups"},
                     { label: "workflow.startableBy.everyone.label", value: "everyone"}
                  ]
               }
            }
         },
         {
            id: "startForm",
            name: "alfresco/forms/creation/FormCreateControl",
            config: {
               name: "startForm",
               label: "workflow.startForm.label"
            }
         }
      ]
   });
});