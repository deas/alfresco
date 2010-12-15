// Ensure Alfresco.forms and validation objects exist
Alfresco.forms = Alfresco.forms || {};
Alfresco.forms.validation = Alfresco.forms.validation || {};

/**
 * Class to represent the forms runtime.
 * 
 * @namespace Alfresco.forms
 * @class Alfresco.forms.Form
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Constructor for a form.
    * 
    * @param {String} formId The HTML id of the form to be managed
    * @return {Alfresco.forms.Form} The new Form instance
    * @constructor
    */
   Alfresco.forms.Form = function(formId)
   {
      this.formId = formId;
      this.validateOnSubmit = true;
      this.validateAllOnSubmit = false;
      this.showSubmitStateDynamically = false;
      this.showSubmitStateDynamicallyErrors = false;
      this.submitAsJSON = false;
      this.submitElements = [];
      this.validations = [];
      this.ajaxSubmit = false;
      this.ajaxSubmitMethod = "POST";
      this.errorContainer = "alert";

      return this;
   };
   
   Alfresco.forms.Form.prototype =
   {

      /**
       * HTML id of the form being represented.
       * 
       * @property formId
       * @type string
       */
      formId: null,

      /**
       * List of ids and/or elements being used to submit the form.
       * 
       * @property submitElements
       * @type object[]
       */
      submitElements: null,
      
      /**
       * Flag to indicate whether the form will validate upon submission, true
       * by default.
       * 
       * @property validateOnSubmit
       * @type boolean
       */
      validateOnSubmit: null,
      
      /**
       * Flag to indicate whether the form will validate all fields upon submission.
       * The default is false which will stop after the first validation failure,
       * true will validate all fields and thus show all errors.
       * 
       * @property validateAllOnSubmit
       * @type boolean
       */
      validateAllOnSubmit: null,

      /**
       * Flag to determine whether the submit elements dynamically update
       * their state depending on the current values in the form.
       * 
       * @property showSubmitStateDynamically
       * @type boolean
       */
      showSubmitStateDynamically: null,
      
      /**
       * Flag to determine whether any errors are shown when the dynamic
       * submit state option is enabled.
       * 
       * @property showSubmitStateDynamicallyErrors
       * @type boolean
       */
      showSubmitStateDynamicallyErrors: null,
      
      /**
       * Flag to determine whether the form will be submitted using an AJAX request.
       * 
       * @property ajaxSubmit
       * @type boolean
       */
      ajaxSubmit: null,
      
      /**
       * String representing where errors should be displayed. 
       * If the value is not "alert" it's presumed the string is the id of an 
       * HTML object to be used as the error container.
       * 
       * @property errorContainer
       * @type string
       */
      errorContainer: null,
      
      /**
       * Object literal containing the abstract function for pre-submission form processing.
       *   fn: function, // The override function.
       *   obj: object, // An object to pass back to the function.
       *   scope: object // The object to use for the scope of the function.
       * 
       * @property doBeforeFormSubmit
       * @type object
       */
      doBeforeFormSubmit:
      {
         fn: function(form, obj){},
         obj: null,
         scope: this
      },
      
      /**
       * Object literal containing the abstract function for intercepting AJAX form submission.
       * Returning false from the override will prevent the Forms Runtime from submitting the data.
       *   fn: function, // The override function.
       *   obj: object, // An object to pass back to the function.
       *   scope: object // The object to use for the scope of the function.
       * 
       * @property doBeforeAjaxRequest
       * @type object
       */
      doBeforeAjaxRequest:
      {
         fn: function(form, obj)
         {
            return true;
         },
         obj: null,
         scope: this
      },
      
      /**
       * Object holding the callback handlers and messages for AJAX submissions.
       * The callback handlers are themselves an object of the form:
       *   fn: function, // The handler to call when the event fires.
       *   obj: object, // An object to pass back to the handler.
       *   scope: object // The object to use for the scope of the handler.
       * 
       * @property ajaxSubmitHandlers
       * @type object
       */
      ajaxSubmitHandlers: null,
      
      /**
       * String representing the http method to be used for the
       * ajax call. Default is POST.
       * 
       * @property ajaxSubmitMethod
       * @type String
       */
      ajaxSubmitMethod: null,
      
      /**
       * Flag to determine whether the form data should be submitted 
       * represented by a JSON structure.
       * 
       * @property submitAsJSON
       * @type boolean
       */
      submitAsJSON: null,
      
      /**
       * List of validations to execute when the form is submitted.
       * 
       * @property validations
       * @type object[]
       */
      validations: null,
      
      /**
       * Sets up the required event handlers and prepares the form for use.
       * NOTE: This must be called after all other setup methods.
       * 
       * @method init
       */
      init: function()
      {
         var form = Dom.get(this.formId);
         if (form !== null)
         {
            if (form.getAttribute("forms-runtime") != "listening")
            {
               // add the event to the form and make the scope of the handler this form.
               Event.addListener(form, "submit", this._submitInvoked, this, true);
               form.setAttribute("forms-runtime", "listening");
               if (this.ajaxSubmit)
               {
                  form.setAttribute("onsubmit", "return false;");
               }
               
               var me = this;
               
               /**
                * Prevent the Enter key from causing a double form submission
                */
               var fnStopEvent = function(id, keyEvent)
               {
                  var event = keyEvent[1],
                     target = event.target ? event.target : event.srcElement;
                  
                  if (target.tagName == "TEXTAREA")
                  {
                     // Allow linefeeds in textareas
                     return false;
                  }
                  else if (target.tagName == "BUTTON" || Dom.hasClass(target, "yuimenuitemlabel"))
                  {
                     // Eventlisteners for buttons and menus must be notified that the enter key was entered
                  }
                  else
                  {
                     var targetName = target.name;
                     if (targetName && (targetName != "-"))
                     {
                        me._submitInvoked(event);
                     }
                     Event.stopEvent(event);
                     return false;
                  }
               };

               var enterListener = new KeyListener(form,
               {
                  keys: KeyListener.KEY.ENTER
               }, fnStopEvent, YAHOO.env.ua.ie > 0 ? KeyListener.KEYDOWN : "keypress");
               enterListener.enable();
            }
            
            // determine if the AJAX and JSON submission should be enabled
            if (form.getAttribute("enctype") && form.getAttribute("enctype") == "application/json")
            {
               this.ajaxSubmit = true;
               this.submitAsJSON = true;
            }
            
            // setup the submit elements if the feature is enabled
            if (this.showSubmitStateDynamically)
            {
               // find the default submit elements if there are no submitIds set
               if (this.submitElements.length == 0)
               {
                  // use a selector to find any submit elements for the form
                  var nodes = Selector.query('#' + this.formId + ' > input[type="submit"]');
                  for (var x = 0, xx = nodes.length; x < xx; x++)
                  {
                     var elem = nodes[x];
                     this.submitElements.push(elem.id);
                  }
               }
               
               // make sure the submit elements start in the correct state
               this.updateSubmitElements();
            }
         }
         else
         {
            this._showInternalError("form with id of '" + this.formId + 
                  "' could not be located, ensure the form is created after the form element is available.");
         }
      },
      
      /**
       * Enables or disables validation when the form is submitted.
       * 
       * @method setValidateOnSubmit
       * @param validate {boolean} true to validate on submission, false
       *        to avoid validation
       */
      setValidateOnSubmit: function(validate)
      {
         this.validateOnSubmit = validate;
      },
      
      /**
       * Sets whether all fields are validated when the form is submitted.
       * 
       * @method setValidateAllOnSubmit
       * @param validate {boolean} true to validate all fields on submission, false
       *        to stop after the first validation failure
       */
      setValidateAllOnSubmit: function(validateAll)
      {
         this.validateAllOnSubmit = validateAll;
      },
      
      /**
       * Sets the list of ids and/or elements being used to submit the form.
       * By default the forms runtime will look for and use the first
       * input field of type submit found in the form being managed.
       * 
       * @method setSubmitElements
       * @param submitElements {object | object[]} Single object or array of objects
       */
      setSubmitElements: function(submitElements)
      {
         if (!YAHOO.lang.isArray(submitElements))
         {
            this.submitElements[0] = submitElements;
         }
         else
         {
            this.submitElements = submitElements;
         }
      },
      
      /**
       * Sets the container where errors will be displayed.
       * 
       * @method setErrorContainer
       * @param position {string} String representing where errors should
       *        be displayed. If the value is not "alert" it's presumed the 
       *        string is the id of an HTML object to be used as the error 
       *        container
       */
      setErrorContainer: function(container)
      {
         this.errorContainer = container;
      },
      
      /**
       * Sets a field as being repeatable, this adds a 'plus' sign after the field 
       * thus allowing multiple values to be entered.
       * 
       * @method setRepeatable
       * @param fieldId {string} Id of the field the validation is for
       * @param containerId {string} Id of the element representing the 
       *        field 'prototype' i.e. the item that will get cloned.
       */
      setRepeatable: function(fieldId, containerId)
      {
         alert("not implemented yet");
      },
      
      /**
       * Sets whether the submit elements dynamically update
       * their state depending on the current values in the form.
       * The visibility of errors can be controlled via the
       * showErrors parameter.
       * 
       * @method setShowSubmitStateDynamically
       * @param showState {boolean} true to have the elements update dynamically
       * @param showErrors {boolean} true to show any validation errors that occur
       */
      setShowSubmitStateDynamically: function(showState, showErrors)
      {
         this.showSubmitStateDynamically = showState;
         
         if (showErrors)
         {
            this.showSubmitStateDynamicallyErrors = showErrors;
         }
      },
      
      /**
       * Enables or disables whether the form submits via an AJAX call.
       * 
       * @method enableAJAXSubmit
       * @param ajaxSubmit {boolean} true to submit using AJAX, false to submit
       *        using the browser's default behaviour
       * @param callbacks {object} Optional object representing callback handlers 
       *        or messages to use, for example
       *        { 
       *           successCallback: yourHandlerObject,
       *           failureCallback: yourHandlerObject,
       *           successMessage: yourMessage,
       *           failureMessage: yourMessage
       *        }
       *        Callback handler objects are of the form:
       *        { 
       *           fn: function, // The handler to call when the event fires.
       *           obj: object, // An object to pass back to the handler.
       *           scope: object // The object to use for the scope of the handler.
       *        }
       */
      setAJAXSubmit: function(ajaxSubmit, callbacks)
      {
         this.ajaxSubmit = ajaxSubmit;
         this.ajaxSubmitHandlers = callbacks;
      },
      
      /**
       * Enables or disables submitting the form data in JSON format.
       * Setting the enctype attribute of the form to "application/json"
       * in Firefox will achieve the same result.
       * 
       * @method setSubmitAsJSON
       * @param submitAsJSON {boolean} true to submit the form data as JSON, 
       *        false to submit one of the standard types "multipart/form-data"
       *        or "application/x-www-form-urlencoded" depending on the enctype
       *        attribute on the form
       */
      setSubmitAsJSON: function(submitAsJSON)
      {
         this.submitAsJSON = submitAsJSON;
      },

      /**
       * Set the http method to use for the AJAX call.
       * 
       * @method setAjaxSubmitMethod
       * @param ajaxSubmitMethod {string} the http method to use for the AJAX call
       */
      setAjaxSubmitMethod: function(ajaxSubmitMethod)
      {
         this.ajaxSubmitMethod = ajaxSubmitMethod;
      },
      
      /**
       * Adds validation for a specific field on the form.
       * 
       * @method addValidation
       * @param fieldId {string} Id of the field the validation is for
       * @param validationHandler {function} Function to call to handle the 
       *        actual validation
       * @param validationArgs {object} Optional object representing the 
       *        arguments to pass to the validation handler function
       * @param when {string} Name of the event the validation should fire on
       *        can be any event applicable for the field for example on a text
       *        field "blur" can be used to fire the validation handler as the 
       *        user leaves the field. If null, the validation is only called
       *        upon form submission
       * @param message Message to be displayed when validation fails, if omitted
                or null the default message in the handler is used
       */
      addValidation: function(fieldId, validationHandler, validationArgs, when, message)
      {
         var field = Dom.get(fieldId);
         if (field == null)
         {
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Ignoring validation for field with id of '" + fieldId + "' as it could not be located.");
            
            return;
         }
         
         if (validationHandler === undefined || validationHandler === null)
         {
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Ignoring validation for field with id of '" + fieldId + "' as a validationHandler was not provided.");
            
            return;
         }
         
         if (message === undefined)
         {
            message = null;
         }
         
         // create object representation of validation
         var validation =
         {
            fieldId: fieldId,
            args: validationArgs,
            handler: validationHandler,
            message: message
         };
         
         // add to list of validations
         this.validations.push(validation);
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Added submit validation for field: " + fieldId +
                                  ", using handler: " + 
                                  (validationHandler.name || YAHOO.lang.dump(validationHandler)) + 
                                  ", args: " + YAHOO.lang.dump(validationArgs));
      
         // if an event has been specified attach an event handler
         if (when && when.length > 0)
         {
            // add the event to the field, pass the validation as a paramter 
            // to the handler and make the scope of the handler this form.
            Event.addListener(field, when, this._validationEventFired, validation, this);
            
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Added field validation for field: " + fieldId +
                                     ", using handler: " + 
                                     (validationHandler.name || YAHOO.lang.dump(validationHandler)) + 
                                     ", args: " + YAHOO.lang.dump(validationArgs) +
                                     ", on event: " + when);
         }
      },
      
      /**
       * Adds an error to the form.
       * 
       * @method addError
       * @param msg {string} The error message to display
       * @param field {object} The element representing the field the error occurred on
       */
      addError: function(msg, field)
      {
         // TODO: Allow an error handler to be plugged in which
         //       would allow for custom error handling
         YAHOO.Bubbling.fire("formValidationError",
         {
            msg: msg,
            field: field
         });
         
         if (this.errorContainer !== null)
         {
            if (this.errorContainer === "alert")
            {
               alert(msg);
            }
            else
            {
               var htmlNode = Dom.get(this.errorContainer);
               if (htmlNode !== null)
               {
                  htmlNode.style.display = "block";
                  
                  var before = htmlNode.innerHTML;
                  var after = htmlNode.innerHTML + "<div>" + msg + "</div>";
                  htmlNode.innerHTML = after;
               }
            }
         }
      },
      
      /**
       * Adds the given submitElement to the list of submit elements
       * for the form.
       * 
       * @method addSubmitElement
       * @param submitElement Object or string representing the submit element
       */
      addSubmitElement: function(submitElement)
      {
         if (submitElement !== null)
         {
            // add the new element to the list
            this.submitElements.push(submitElement);
            
            // force a refresh of the submit state
            this.updateSubmitElements();
         }
      },
      
      /**
       * Retrieves the label text for a field
       * 
       * @method getFieldLabel
       * @param fieldId {string} The id of the field to get the label for
       * @return {string} The label for the field or the fieldId if a label could not be found
       */
      getFieldLabel: function(fieldId)
      {
         var label = null;
         
         // lookup the label using the "for" attribute (use the first if multiple found)
         var nodes = Selector.query('label');
         // NOTE: there seems to be a bug in getting label using 'for' or 'htmlFor'
         //       for now get all labels and find the one we want
         if (nodes.length > 0)
         {
            for (var x = 0, xx = nodes.length; x < xx; x++)
            {
               var elem = nodes[x];
               if (elem["htmlFor"] == fieldId)
               {
                  // get the text for the label
                  label = elem.firstChild.nodeValue;
               }
            }
         }
         
         // default to the field id if the label element was not found
         if (label == null)
         {
            label = fieldId;
         }
         
         return label;
      },
      
      /**
       * Retrieves the data currently held by the form.
       * 
       * @method getFormData
       * @return An object representing the form data
       */
      getFormData: function()
      {
         // get the form element
         var form = Dom.get(this.formId);
         
         // build object representation of the form data
         return this._buildAjaxForSubmit(form);
      },
      
      /**
       * Applies a Key Listener to input fields to ensure tabbing only targets elements
       * that specifically set a "tabindex" attribute.
       * This has only been seen as an issue with the Firefox web browser, so shouldn't be applied otherwise.
       *
       * @method applyTabFix
       */
      applyTabFix: function()
      {
         if (YAHOO.env.ua.gecko > 0)
         {
            /**
             * Ensure the Tab key only focusses relevant fields
             */
            var form = Dom.get(this.formId);
            
            var fnTabFix = function(id, keyEvent)
            {
               var event = keyEvent[1];
               var target = event.target;
               if (!target.hasAttribute("tabindex"))
               {
                  Event.stopEvent(event);
                  Selector.query("[tabindex]", form)[0].focus();
               }
            }
            
            var tabListener = new KeyListener(form,
            {
               keys: KeyListener.KEY.TAB
            },
            fnTabFix, "keyup");
            tabListener.enable();
         }
      },

      /**
       * Updates the state of all submit elements.
       * 
       * @method updateSubmitElements
       */
      updateSubmitElements: function()
      {
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Determining whether submit elements can be enabled...");
         
         // run all validations silently to see if submit elements can be enabled
         var valid = this._runValidations(true);

         // make sure all submit elements show correct state
         for (var x = 0, xx = this.submitElements.length; x < xx; x++)
         {
            var currentItem = this.submitElements[x];
            if (currentItem)
            {
              if (typeof currentItem == "string")
              {
                 // get the element with the id and set the disabled attribute
                 Dom.get(currentItem).disabled = !valid;
              }
              else
              {
                 // TODO: for now if an object is passed presume it's a YUI button
                 currentItem.set("disabled", !valid);
              }              
            }
         }
      },
      
      
      /**
       * Clears any errors displayed by previous validation failures.
       * 
       * @method _clearErrors
       * @private
       */
      _clearErrors: function()
      {
         if (this.errorContainer !== "alert")
         {
            var htmlNode = Dom.get(this.errorContainer);
            if (htmlNode !== null)
            {
               htmlNode.style.display = "none";
               htmlNode.innerHTML = "";
            }
         }
      },
      
      /**
       * Event handler called when a validation event is fired by any registered field.
       * 
       * @method _validationEventFired
       * @param event {object} The event
       * @param validation {object} Object representing the validation to execute, consists of 
       *        'fieldId', 'args', 'handler' and 'message' properties.
       * @private
       */
      _validationEventFired: function(event, validation)
      {
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Event has been fired for field: " + validation.fieldId);
         
         var silent = false;
         
         // if dynamic updating is enabled
         if (this.showSubmitStateDynamically)
         {
            if (this.showSubmitStateDynamicallyErrors)
            {
               // if errors are being shown clear previous ones
               this._clearErrors();
            }
            else
            {
               // otherwise hide errors
               silent = true;
            }
         }
         
         // call handler
         validation.handler(Event.getTarget(event), validation.args, event, this, silent, validation.message);
         
         // update submit elements state, if required
         if (this.showSubmitStateDynamically)
         {
            this.updateSubmitElements();
         }
      },
      
      /**
       * Event handler called when the form is submitted.
       * 
       * @method _submitInvoked
       * @param event {object} The event
       * @private
       */
      _submitInvoked: function(event)
      {
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Submit invoked on formId: ", this.formId);
         
         // clear any errors that may be visible
         this._clearErrors();
         
         if (this.validateOnSubmit)
         {
            var silent = false;
            
            // if dynamic updating is enabled
            if (this.showSubmitStateDynamically && !this.showSubmitStateDynamicallyErrors)
            {
               silent = true;
            }
            
            if (this._runValidations(silent))
            {
               // validation was successful
               // get the form element
               var form = Dom.get(this.formId);

               // call the pre-submit function, passing the form for last-chance processing
               this.doBeforeFormSubmit.fn.call(this.doBeforeFormSubmit.scope, form, this.doBeforeFormSubmit.obj);

               // should submission be done using AJAX, or let 
               // the browser do the submit?
               if (this.ajaxSubmit)
               {
                  // stop the browser from submitting the form
                  Event.stopEvent(event);
                  
                  // get the form's action URL
                  var submitUrl = form.attributes.action.nodeValue;
                  
                  if (Alfresco.logger.isDebugEnabled())
                  {
                     Alfresco.logger.debug("Performing AJAX submission to url: ", submitUrl);
                  }

                  // determine how to submit the form, if the enctype
                  // on the form is set to "application/json" then
                  // package the form data as an AJAX string and post
                  if (form.enctype && form.enctype == "multipart/form-data")
                  {
                     var d = form.ownerDocument;
                     var iframe = d.createElement("iframe");
                     iframe.style.display = "none";
                     Dom.generateId(iframe, "formAjaxSubmit");
                     iframe.name = iframe.id;
                     document.body.appendChild(iframe);

                     // makes it possible to target the frame properly in IE.
                     window.frames[iframe.name].name = iframe.name;

                     form.target = iframe.name;
                     form.submit();
                     return;
                  }
                  
                  // create config object to pass to request helper
                  var config =
                  {
                     method: this.ajaxSubmitMethod,
                     url: submitUrl
                  };

                  if (this.ajaxSubmitHandlers)
                  {
                     config = YAHOO.lang.merge(config, this.ajaxSubmitHandlers);
                  }
                  if (this.submitAsJSON)
                  {
                     var jsonData = this._buildAjaxForSubmit(form);
                     
                     // set up specific config
                     config.dataObj = jsonData;
                     
                     // call the pre-request function, passing the config object for last-chance processing
                     if (this.doBeforeAjaxRequest.fn.call(this.doBeforeAjaxRequest.scope, config, this.doBeforeAjaxRequest.obj))
                     {
                        if (Alfresco.logger.isDebugEnabled())
                           Alfresco.logger.debug("Submitting JSON data: ", config.dataObj);

                        Alfresco.util.Ajax.jsonRequest(config);
                     }
                     else
                     {
                        if (Alfresco.logger.isDebugEnabled())
                           Alfresco.logger.debug("JSON data request cancelled in doBeforeAjaxRequest()");
                     }
                  }
                  else
                  {
                     if (Alfresco.logger.isDebugEnabled())
                        Alfresco.logger.debug("Submitting data in form: ", form.enctype);
                     
                     // set up specific config 
                     config.dataForm = form;
                     Alfresco.util.Ajax.request(config);
                  }
               }
            }
            else
            {
               // stop the event from continuing and sending the form.
               Event.stopEvent(event);
               
               if (Alfresco.logger.isDebugEnabled())
                  Alfresco.logger.debug("Submission prevented as validation failed");
            }
         }
         else
         {
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Ignoring validations as submission validation is disabled");
         }
      },
      
      /**
       * Builds a JSON representation of the current form
       * 
       * @method _buildAjaxForSubmit
       * @param form {object} The form object to build the JSON for
       * @private
       */
      _buildAjaxForSubmit: function(form)
      {
         if (form !== null)
         {
            var formData = {};
            var length = form.elements.length;
            for (var i = 0; i < length; i++)
            {
               var element = form.elements[i];
               var name = element.name;
               if (name == "-" || element.disabled || element.type === "button")
               {
                  continue;
               }
               if (name == undefined || name == "")
               {
                  name = element.id;
               }
               var value = YAHOO.lang.trim(element.value);
               if (name)
               {
                  // check whether the input element is an array value
                  if ((name.length > 2) && (name.substring(name.length - 2) == '[]'))
                  {
                     name = name.substring(0, name.length - 2);
                     if (formData[name] == undefined)
                     {
                        formData[name] = new Array();
                     }
                     formData[name].push(value);
                  }
                  // check whether the input element is an object literal value
                  else if (name.indexOf(".") > 0)
                  {
                     var names = name.split(".");
                     var obj = formData;
                     var index;
                     for (var j = 0, k = names.length - 1; j < k; j++)
                     {
                        index = names[j];
                        if (obj[index] === undefined)
                        {
                           obj[index] = {};
                        }
                        obj = obj[index];
                     }
                     obj[names[j]] = value;
                  }
                  else if (!((element.type === "checkbox" || element.type === "radio") && !element.checked))
                  {
                     if (element.type == "select-multiple")
                     {
                        for (var j = 0, jj = element.options.length; j < jj; j++)
                        {
                           if (element.options[j].selected)
                           {
                              if (formData[name] == undefined)
                              {
                                 formData[name] = new Array();
                              }
                              formData[name].push(element.options[j].value);
                           }
                        }
                     }
                     else
                     {
                        formData[name] = value;
                     }
                  }
               }
            }
            
            return formData;
         }
      },
      
      /**
       * Executes all registered validations and returns result.
       * 
       * @method _runValidations
       * @param silent {boolean} Determines whether the validation checks are run silently
       * @private
       */
      _runValidations: function(silent)
      {
         var atLeastOneFailed = false;
         
         // iterate through the validations
         for (var x = 0, xx = this.validations.length; x < xx; x++)
         {
            var val = this.validations[x];
                  
            var field = Dom.get(val.fieldId);
            if (field !== null && !field.disabled)
            {
               if (!val.handler(field, val.args, null, this, silent))
               {
                  atLeastOneFailed = true;
                  
                  if (!this.validateAllOnSubmit)
                  {
                     // if silent is false set the focus on the field that failed.
                     if (!silent)
                     {
                        field.focus();
                     }
                     
                     // stop if we aren't validating all fields
                     break;
                  }
               }
            }
         }
         
         return !atLeastOneFailed;
      },
      
      /**
       * Displays an internal form error message.
       * 
       * @method _showInternalError
       * @param msg {string} The error message to display
       * @param field {object} The element representing the field the error occurred on
       * @private 
       */
      _showInternalError: function(msg, field)
      {
         this.addError("Internal Form Error: " + msg, field);
      }
   };
})();

/**
 * Mandatory validation handler, tests that the given field has a value.
 * 
 * @method mandatory
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.mandatory = function mandatory(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating mandatory state of field '" + field.id + "'");
   
   var valid = true; 
      
   if (field.type && field.type == "radio")
   {
      // TODO: Do we actually need to support this scenario?
      //       wouldn't a radio button normally have a default
      //       'checked' option?
      
      var formElem = Dom.get(form.formId),
         radios = formElem[field.name],
         anyChecked = false;
      for (var x = 0, xx = radios.length; x < xx; x++)
      {
         if (radios[x].checked)
         {
            anyChecked = true;
            break;
         }
      }
      
      valid = anyChecked;
   }
   else
   {
      valid = YAHOO.lang.trim(field.value).length !== 0;
   }
   
   if (!valid && !silent && form)
   {
      // if the keyCode from the event is the TAB or SHIFT keys don't show the error
      if (event && event.keyCode != 9 && event.keyCode != 16 || !event)
      {
         var msg = (message != null) ? message : "is mandatory.";
         form.addError(form.getFieldLabel(field.id) + " " + msg, field);
      }
   }
   
   return valid; 
};

/**
 * Length validation handler, tests that the given field's value has either
 * a minimum and/or maximum length.
 * 
 * @method length
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Object representing the minimum and maximum length, and whether to crop content
 *        {
 *           min: 3,
 *           max: 10,
 *           crop: true
 *        }
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.length = function length(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating length of field '" + field.id +
                            "' using args: " + YAHOO.lang.dump(args));
   
   var valid = true;
   var myArgs = YAHOO.lang.merge(
   {
      min: -1,
      max: -1,
      crop: false,
      includeWhitespace: true
   }, args);
   
   if (myArgs.minLength)
   {
      myArgs.min = myArgs.minLength;
   }
   
   if (myArgs.maxLength)
   {
      myArgs.max = myArgs.maxLength;
   }

   var length = myArgs.includeWhitespace ? field.value.length : YAHOO.lang.trim(field.value).length;
   
   if (myArgs.min != -1 && length < myArgs.min)
   {
      valid = false;
   }
   
   if (myArgs.max != -1 && length > myArgs.max)
   {
      valid = false;
      if (myArgs.crop)
      {
         if(myArgs.includeWhitespace)
         {
            field.value = YAHOO.lang.trim(field.value);
         }
         if(field.value.length > myArgs.max)
         {
            field.value = field.value.substring(0, myArgs.max);
         }
         if (field.type && field.type == "textarea")
         {
            field.scrollTop = field.scrollHeight;
         }
         valid = true;
      }
   }
   
   if (!valid && !silent && form)
   {
      var msg = (message != null) ? message : "is not the correct length.";
      form.addError(form.getFieldLabel(field.id) + " " + msg, field);
   }
   
   return valid;
};

/**
 * Number validation handler, tests that the given field's value is a number.
 * 
 * @method number
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Optional object containing a "repeating" flag
 *        {
 *           repeating: true
 *        }
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.number = function number(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating field '" + field.id + "' is a number");
   
   var repeating = false;
   
   // determine if field has repeating values
   if (args !== null && args.repeating)
   {
      repeating = true;
   }
   
   var valid = true;
   if (repeating)
   {
      // as it's repeating there could be multiple comma separated values
      var values = field.value.split(",");
      for (var i = 0; i < values.length; i++)
      {
         valid = (isNaN(values[i]) == false);
         
         if (!valid)
         {
            // stop as soon as we find an invalid value
            break;
         }
      }
   }
   else
   {
      valid = (isNaN(field.value) == false);
   }
   
   if (!valid && !silent && form)
   {
      var msg = (message != null) ? message : "is not a number.";
      form.addError(form.getFieldLabel(field.id) + " " + msg, field);
   }
   
   return valid;
};

/**
 * Number range validation handler, tests that the given field's value has either
 * a minimum and/or maximum value.
 * 
 * @method numberRange
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Object representing the minimum and maximum value, for example
 *        {
 *           min: 18;
 *           max: 30;
 *        }
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.numberRange = function numberRange(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating number range of field '" + field.id +
                            "' using args: " + YAHOO.lang.dump(args));
                            
   var valid = true;
   var value = field.value;
   
   if (value.length > 0)
   {
      if (isNaN(value))
      {
         valid = false;
         
         if (!silent && form)
         {
            var msg = (message != null) ? message : "is not a number.";
            form.addError(form.getFieldLabel(field.id) + " " + msg, field);
         }
      }
      else
      {
         var min = -1;
         var max = -1;
         
         if (args.min)
         {
            min = parseInt(args.min);
         }
         
         if (args.minValue)
         {
            min = parseInt(args.minValue);
         }
         
         if (args.max)
         {
            max = parseInt(args.max);
         }
         
         if (args.maxValue)
         {
            max = parseInt(args.maxValue);
         }
         
         if (min != -1 && value < min)
         {
            valid = false;
         }
         
         if (max != -1 && value > max)
         {
            valid = false;
         }
         
         if (!valid && !silent && form)
         {
            var msg = (message != null) ? message : "is not within the allowable range.";
            form.addError(form.getFieldLabel(field.id) + " " + msg, field);
         }
      }
   }
   
   return valid;
};

/**
 * Node name validation handler, tests that the given field's value is a valid
 * name for a node in the repository.
 *
 * @method nodeName
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.nodeName = function nodeName(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating field '" + field.id + "' is a valid node name");

   if (!args)
   {
      args = {};
   }
   
   /**
    * Pattern for disallowing leading and trailing spaces. See CHK-6614
    args.pattern = /([\"\*\\\>\<\?\/\:\|]+)|([\.]?[\.]+$)|(^[ \t]+|[ \t]+$)/;
    */
   args.pattern = /([\"\*\\\>\<\?\/\:\|]+)|([\.]?[\.]+$)/;
   args.match = false;

   return Alfresco.forms.validation.regexMatch(field, args, event, form, silent, message);
};


/**
 * NodeRef validation handler, tests that the given field's value is a valid
 * nodeRef identifier for a node in the repository.
 *
 * @method nodeRef
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.nodeRef = function nodeRef(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating field '" + field.id + "' is a valid noderef");

   if (!args)
   {
      args = {};
   }

   args.pattern = /^[^\:^ ]+\:\/\/[^\:^ ]+\/[^ ]+$/;

   return Alfresco.forms.validation.regexMatch(field, args, event, form, silent, message);
};


/**
 * Email validation handler, tests that the given field's value is a valid
 * email address.
 *
 * @method email
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.email = function email(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating field '" + field.id + "' is a valid email address");

   if (!args)
   {
      args = {};
   }
   
   args.pattern = /(.+@.+\.[a-zA-Z0-9]{2,6})/;
   args.match = true;

   return Alfresco.forms.validation.regexMatch(field, args, event, form, silent, message);
};


/**
 * Time validation handler, tests that the given field's value is a valid time value.
 *
 * @method time
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.time = function time(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating field '" + field.id + "' is a valid time value");

   if (!args)
   {
      args = {};
   }

   args.pattern = /^([0-1]\d|2[0-3]):[0-5]\d(:[0-5]\d)?$/;
   args.match = true;

   return Alfresco.forms.validation.regexMatch(field, args, event, form, silent, message);
};

/**
 * URL validation handler, tests that the given field's value is a valid URL
 *
 * @method url
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.url = function url(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating field '" + field.id + "' is a valid URL");

   var expression = /(ftp|http|https):\/\/[\w\-_]+(\.[\w\-_]+)*([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/,
      valid = true;

   if (field.value.length > 0)
   {
      // Check an empty string replacement returns an empty string
      var pattern = new RegExp(expression);
      valid = field.value.replace(pattern, "") === "";

      // Inform the user if invalid
      if (!valid && !silent && form)
      {
         var msg = (message != null) ? message : "is invalid.";
         form.addError(form.getFieldLabel(field.id) + " " + msg, field);
      }
   }
   
   return valid;
};


/**
 * Regular expression validation handler, tests that the given field's value matches
 * the supplied regular expression.
 * 
 * @method regexMatch
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Object representing the expression.
 * The args object should have the form of:
 * {
 *    pattern: {regexp}, // A regular expression
 *    match: {boolean}   // set to false if the regexp should NOT match the input, default is true
 * }
 * An example to validate a field represents an email address can look like:
 * {
 *    pattern: /(\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,6})/
 * }
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.regexMatch = function regexMatch(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating regular expression of field '" + field.id +
                            "' using args: " + YAHOO.lang.dump(args));
   
   var valid = true;
   
   if (field.value.length > 0)
   {
      // The pattern SHOULD match by default
      if (args.match === undefined)
      {
          args.match = true;
      }

      // Check if the patterns match
      var pattern = new RegExp(args.pattern);
      valid = pattern.test(field.value);

      // Adjust the result if the test wasn't intended to match
      if (!args.match)
      {
         valid = !valid;
      }

      // Inform the user if invalid
      if (!valid && !silent && form)
      {
         var msg = (message != null) ? message : "is invalid.";
         form.addError(form.getFieldLabel(field.id) + " " + msg, field);
      }
   }
   
   return valid;
};


/**
 * Repository regular expression handler, simply used as a pass through to the 
 * standard regexMatch handler after converting the paramater names.
 *
 * @method repoRegexMatch
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.repoRegexMatch = function repoRegexMatch(field, args, event, form, silent, message)
{
   // convert parameters
   args.pattern = args.expression;
   args.match = args.requiresMatch;

   // call the standard regex handler
   return Alfresco.forms.validation.regexMatch(field, args, event, form, silent, message);
};

/**
 * Validation handler for a valid date and time, currently this simply looks for the
 * presence of the 'invalid' class applied to the relevant field. This implies that this
 * validation handler must be added after any other handlers that determine validity.
 *
 * @method validDateTime
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.validDateTime = function validDateTime(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating field '" + field.id + "' has a valid date and time");
   
   return !YAHOO.util.Dom.hasClass(field, "invalid");
};

/**
 * Validation handler for the repository 'list of values' constraint. As the UI
 * handles this by displaying the list of allowable values this handler is a dummy
 * placeholder.
 *
 * @method listOfValues
 * @param field {object} The element representing the field the validation is for
 * @param args {object} The list of allowable values
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.inList = function inList(field, args, event, form, silent, message)
{
   return true;
};

