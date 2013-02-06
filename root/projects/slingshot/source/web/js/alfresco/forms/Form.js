define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dijit/form/Form",
        "dojo/_base/xhr",
        "alfresco/core/Core",
        "dojo/text!./templates/Form.html",
        "dijit/form/Button",
        "dojo/_base/array",
        "dojo/json"], 
        function(declare, _Widget, _Templated, Form, xhr, AlfCore, template, Button, array, json) {
   
   return declare([_Widget, _Templated, AlfCore], {
      
      templateString: template,
      
      // A reference to the dijit.form.Form that will be created.
      _form: null,
      
      widgets: null,
      
      pubSubScope: null,
      
      // The URL that the form will be posted to
      postUrl: "",
      
      convertFormToJsonString: false,
      
      /**
       * This will be instantiated as an array and used to keep track of any controls that report themselves as being
       * in an invalid state. The "OK" button for submitting the form should only be enabled when this list is empty.
       */
      invalidFormControls: null,
      
      /**
       * A reference to the "OK" button for the form.
       * TODO: It should be possible to configure alternative labels for the button
       */
      _okButton: null,
      
      /**
       * A reference to the "Cancel" button for the form.
       * TODO: It should be possible to configure alternative labels for the button.
       */
      _cancelButton: null,
      
      postCreate: function() {
         
         // Generate a publication/subscription topic scope if one has not been provided...
         if (this.pubSubScope == null)
         {
            this.pubSubScope = this.generateUuid();
         }
         
         var _this = this;
         
         // Setup some arrays for recording the valid and invalid widgets...
         this.invalidFormControls = [];
         
         this._form = new Form({
            id: this.generateUuid()
         }, this._formNode);
         
         
         this.alfSubscribe(this.pubSubScope + "_invalidFormControl", function(payload) {
            // Handle INVALID widget report...
            var alreadyCaptured = array.some(_this._invalidFormControls, function(item) {
               return item == payload.name;
            });
            if (!alreadyCaptured)
            {
               _this.invalidFormControls.push(payload.name);
            }
            if (_this._okButton)
            {
               _this._okButton.set("disabled", "true");
            }
         });

         this.alfSubscribe(this.pubSubScope + "_validFormControl", function(payload) {
            // Handle VALID widget report...
            _this.invalidFormControls = array.filter(_this.invalidFormControls, function(item) {
               return item != payload.name;
            });
            if (_this._okButton)
            {
               _this._okButton.set("disabled", _this.invalidFormControls.length > 0);
            }
         });

         // Add the widgets to the form...
         if (this.widgets)
         {
            // Set the forms pubSubScope for all the widgets it contains...
            array.forEach(this.widgets, function(widget, index) {
               if (widget && !widget.config)
               {
                  widget.config = {};
               }
               widget.config.pubSubScope = _this.pubSubScope;
            });
            
            this.processWidgets(this.widgets, this._form.domNode);
         }
         
         // Create the buttons for the form...
         this.createButtons();
      },
      
      /**
       * Creates the buttons for the form. This can be overridden to change the buttons that are displayed.
       */
      createButtons: function() {
         // Create the "OK" and "Cancel" buttons...
         // In reality we will probably want to allow custom buttons to be created
         this._okButton = new Button({ label: "OK", onClick: function(){
            _this._onOK(); 
         }}, this._okButtonNode);
         this._cancelButton = new Button({ label: "Cancel",onClick: function() {
            _this._onCancel();  
         }}, this._cancelButtonNode);
      },
      
      allWidgetsProcessed: function(widgets) {
         this.validate();
      },
      
      // Handles posting the form...
      _onOK: function() {
         var _this = this;
         var xhrArgs = {
            url: this.postUrl,
            handleAs: "json",
            load: function(response) {
               _this._onPostSuccess(response);
            },
            error: function(response) {
               _this._onPostFailure(response);
            }
         };
         
         // A form can either be posted directly or its contents converted into a JSON string
         // (this has been done to support existing Share WebScripts - such as for site creation)...
         if (this.convertFormToJsonString)
         {
            xhrArgs.headers = { "Content-Type": "application/json"};
            xhrArgs.postData = this._convertFormToJsonString(); 
         }
         else
         {
            xhrArgs.form =  this._form.id;
         }
         
         xhr.post(xhrArgs);
      },
      
      // Converts values of the widgets contained in the form into a JSON string
      _convertFormToJsonString: function() {
         // Construct a JSON string payload
         var payload = {};
         array.forEach(this._form.getChildren(), function(entry, i) {
            var name = entry.get("name");
            if (name)
            {
               var widget = entry.get("_wrappedWidget");
               payload[entry.get("name")] = widget ? (widget.value ? widget.value : "") : "";
            }
         });
         
         return json.stringify(payload);
      },
     
      
      // Handles post success...
      _onPostSuccess: function(response) {
         var payload = {
            response: response,
            form: this._form
         }
         this.alfPublish(this.id + "_POST_SUCCESS", payload);
      },
      
      // Handles post failure...
      _onPostFailure: function(response) {
         this.alfPublish(this.id + "_POST_FAILURE", response);
      },
      
      // Handles cancelling the form...
      _onCancel: function() {
         this.alfPublish(this.id + "_CANCEL", null);
      },
      
      getValue: function(meaningful) {
         var values = {};
         if (this._form)
         {
            array.forEach(this._form.getChildren(), function(entry, i) {
               values[entry.get("name")] = entry.getValue(meaningful);
            });
         }
         this.alfLog("log", "Returning form values: ", values);
         return values;
      },
      
      setValue: function(values) {
         this.alfLog("log", "Setting form values: ", values);
         if (values && values instanceof Object)
         {
            if (this._form)
            {
               array.forEach(this._form.getChildren(), function(entry, i) {
                  entry.setValue(values[entry.get("name")]);
               });
            }
         }
      },
      
      validate: function() {
         this.alfLog("log", "Validating form", this._form);
         
         // THIS IS NOT A TYPO... the publish operation is performed twice. The first time 
         // will initialise the rules engine in each widget with the values of all the other
         // form controls that they have expressed an interest in and the second time will allow
         // the rules to be processed.
         array.forEach(this._processedWidgets, function(widget, i) {
            if (widget.publishValue && typeof widget.publishValue == "function")
            {
               widget.validate(); // Validate the initial value
               widget.publishValue();
            }
         });
         array.forEach(this._processedWidgets, function(widget, i) {
            if (widget.publishValue && typeof widget.publishValue == "function")
            {
               widget.publishValue();
            }
         });
         
         // The form is valid if there are no invalid form controls...
         return this.invalidFormControls.length == 0;
      }
   });
});