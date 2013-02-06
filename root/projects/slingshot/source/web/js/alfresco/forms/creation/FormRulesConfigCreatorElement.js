define(["alfresco/forms/controls/MultipleEntryElement",
        "dojo/_base/declare",
        "alfresco/forms/PublishForm",
        "dojo/_base/array"], 
        function(MultipleEntryElement, declare, PublishForm, array) {
   
   return declare([MultipleEntryElement], {
      
      i18nRequirements: [{i18nFile: "./i18n/FormCreation.properties"}],
      
      createReadDisplay: function() {
         this.alfLog("log", "Creating read display for FormRulesConfigCreatorElement");
         
         var value = this.getValue();
         
         var msgType = "";
         if (value.is == "" && value.isNot == "")
         {
            msgType = "rule.display.any";
         }
         else if (value.is != "")
         {
            msgType = "rule.display.mustBe";
         }
         else if (value.isNot != "")
         {
            msgType = "rule.display.mustNotBe";
         }
         else
         {
            msgType = "rule.display.mustAndmustNotBe";
         }

         // Get the selected field name...
         var fieldName = null;
         var availableFields = this.availableFieldsFunction(this.availableFieldsFunctionContext, this.parent_alfMultipleElementId);
         var selectedField = array.forEach(availableFields, function(field, index) {
            if (value.field == field.value)
            {
               fieldName = field.label;
            }
         });
         
         // TODO: If fieldName is null then it means that a previously selected field has been removed and this
         //       will place the current rule into error which we need to handle...
         if (fieldName != null)
         {
            this.readDisplay.innerHTML = this.message(msgType, {"field": fieldName, 
               "mustBe" : value.is,
               "mustNotBe" : value.isNot});
         }
         else
         {
            this.readDisplay.innerHTML = this.message("rule.fieldDeleted.label");
         }
      },
      
      form: null,
      
      createEditDisplay: function() {
         
         // Create a unique Publication/Subscription scope to ensure that we only listen to changes within the local form...
         var pubSubScope = this.generateUuid();
         
         // The edit display is a form containing the following...
         var availableFields = this.availableFieldsFunction(this.availableFieldsFunctionContext, this.parent_alfMultipleElementId);
         array.forEach(this.widgets, function(widget, index) {
            if (widget.id == "FieldSelect")
            {
               widget.config.optionsConfig.fixed = availableFields;
            }
         });
         
         if (this.form == null)
         {
            this.form = new PublishForm({
               pubSubScope: pubSubScope,
               widgets: this.widgets});
            this.form.placeAt(this.editDisplay);
         }
         
         // Set up the subscriptions to listen to changes within the form so that the overall value can be updated...
         var _this = this;
         this.alfSubscribe(pubSubScope + "_valueChangeOf_field", function(payload) {
            _this._currentFieldName = payload.value;
            
            // When a field has been selected we need to publish that a selection has been made so that no
            // other rule element can use that field.
            var fieldSelectPayload = {
               field: payload.value
            };
            _this.alfPublish(_this.fieldSelectPubSubScope + "_fieldSelected", fieldSelectPayload);
         });
         
         // The element is always created from the base widgets which does not actually provide any values
         // so it is important that the values are set afterwards. The elementConfig object is created by
         // the createEntries function of the FormRulesConfigCreator...
         this.form.setValue(this.value);
         this.form.validate();
      },
      
      /**
       * Overrides the default implementation to return a new object consisting of the current
       * data that has been set through updates to the controls in the associated form.
       */
      getValue: function(meaningful) {
         
         var value = new Object();
         if (this.form != null) 
         {
            array.forEach(this.form._processedWidgets, function(widget, index) {
               value[widget.name] = widget.getValue(meaningful);
            });
         }
         else
         {
            value = this.value;
         }
         if (!meaningful)
         {
            this.value = value;
         }
         return value; 
      },
      
      /**
       * Calls the validate() function on form if it exists and returns the result.
       */
      validate: function() {
         var valid = true;
         if (this.form)
         {
            valid = this.form.validate();
         }
         return valid;
      },
      
      /**
       * This is the widget configuration for the Form to use for creating the element.
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
            id: "FieldSelect",
            name: "alfresco/forms/controls/DojoSelect",
            config: {
               name: "field",
               label: "rule.field.label",
               description: "rule.field.description",
               optionsConfig: {
                  fixed: null
               },
               requirementConfig: {
                  initialValue: true
               }
            }
         },
//         {
//            name: "alfresco/forms/controls/DojoSelect",
//            config: {
//               name: "isValid",
//               label: "Validity",
//               description: "How the validity of the field affects the rule.",
//               optionsConfig: {
//                  fixed: [
//                     { label: "Doesn't matter", value: "na"},
//                     { label: "Must be valid", value: "true"},
//                     { label: "Must NOT be valid", value: "false"}
//                  ]
//               }
//            }
//         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "is",
               label: "rule.is.label",
               description: "rule.is.description"
            }
         },
         {
            name: "alfresco/forms/controls/DojoValidationTextBox",
            config: {
               name: "isNot",
               label: "rule.isNot.label",
               description: "rule.isNot.description"
            }
         }
      ]
   });
});