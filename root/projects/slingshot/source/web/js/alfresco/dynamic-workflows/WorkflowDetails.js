define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/WorkflowDetails.html",
        "alfresco/core/Core",
        "alfresco/forms/PublishForm"], 
        function(declare, _Widget, _Templated, template, AlfCore, PublishForm) {
   
   return declare([_Widget, _Templated, AlfCore], {
      cssRequirements: [{cssFile:"./css/WorkflowDetails.css"}],
      i18nRequirements: [{i18nFile: "./i18n/WorkflowDetails.properties"}],
      templateString: template,
      
      pubSubScope: null,
      
      /**
       * This will hold a reference to the current form used to display the workflow
       * details.
       */
      form: null,
      
      /**
       * This will be updated with the topic to publish on with the updated value of the form being displayed.
       * It will be different for each form displayed and the topics should be unique to ensure that data is
       * only saved to the correct place (this widget doesn't take responsibility for what gets saved) 
       */
      currentSaveTopic: null,
      
      postCreate: function() {
         
         var pubSubScope = this.generateUuid();
         
         var _this = this;
         var subScope = this.pubSubScope + "_DISPLAY";
         this.alfSubscribe(subScope, function(payload) {
            _this.alfLog("log", "Received Workflow display request", payload);
            if (payload.structure)
            {
               // Save the previous form details (if there is somewhere to save them), destroy
               // the current form and create a new one...
               if (_this.currentSaveTopic != null)
               {
                  // Check to see whether the current form is valid...
                  var valid = true;
                  if (_this.form)
                  {
                     valid = _this.form.validate();
                  }
                  
                  var savePayload = {
                     values: _this.form.getValue(),
                     valid: valid
                  };
                  _this.alfLog("log", "Publishing saving data for current Workflow details", savePayload);
                  _this.alfPublish(_this.currentSaveTopic, savePayload);
               }
               
               if (payload.saveTopic == _this.currentSaveTopic)
               {
                  // Don't delete the form and recreate if the user has clicked the same details. Although 
                  // we have saved the current display details (via the previou publication) we don't need
                  // to recreate the details because they will actually be out-of-date.
                  _this.alfLog("log", "Request made to display the Workflow details currently on display");
               }
               else
               {
                  // Destroy the form...
                  if (_this.form != null)
                  {
                     _this.form.destroy();
                  }

                  // Create a new form using the structure provided by the payload...
                  _this.form = new PublishForm({
                     pubSubScope: pubSubScope,
                     widgets: payload.structure});
                  _this.form.placeAt(_this.formNode);
                  
                  // Set the values of the form from the payload...
                  _this.form.setValue(payload.values);
                  _this.form.validate();
                  
                  // Update the save topic...
                  _this.currentSaveTopic = payload.saveTopic;
               }
            }
         });
         
         // Handle deletion events...
         var deleteTopic = this.pubSubScope + "_DELETE";
         this.alfSubscribe(deleteTopic, function(payload) {
            if (payload.saveTopic && payload.saveTopic == _this.currentSaveTopic && _this.form != null)
            {
               // The currently displayed details have been deleted. We need to therefore remove the current form.
               _this.form.destroy();
               _this.currentSaveTopic = null;
            }
         });
      }
   });
});