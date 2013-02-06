define(["alfresco/forms/controls/MultipleEntryFormControl",
        "dojo/_base/declare",
        "alfresco/forms/creation/FormCreateCreator",
        "dojo/dom-construct",
        "dojo/on",
        "dojo/_base/array",
        "alfresco/forms/creation/FormPreview",
        "dijit/popup",
        "dojo/aspect"], 
        function(MultipleEntryFormControl, declare, FormCreateCreator, domConstruct, on, array, FormPreview, popup, aspect) {
   
   return declare([MultipleEntryFormControl], {
      
      previewLink: null,
      
      createFormControl: function(config, domNode) {
         var _this = this;
         this.previewLink = domConstruct.create("div", { innerHTML: "Preview",
                                                         style: "float: right;"}, this.containerNode, "first");
         on(this.previewLink, "click", function() {
            _this.generatePreview();
         });
         return new FormCreateCreator(config);
      },
      
      generatePreview: function() {
         // Generate preview...

         var _this = this;
         var value = this.getValue();
         var config = {};
         config.widgets = [];
         array.forEach(value, function(field, index) {
            
            var currWidget = {};
            currWidget.id = field.field;
            currWidget.name = field.name;
            
            var config = {};
            config.name = field.field;
            config.label = field.label;
            config.description = field.description;
            config.unitsLabel = field.unitsLabel;
            config.validationConfig = field.validationConfig;
            
            // Process the fixed options...
            // TODO: Currently the form creator only allows fixed options to be created
            // TODO: Currently the form creator only allows values (not labels) to be created
            var optionsConfig = {};
            optionsConfig.fixed = [];
            
            array.forEach(field.optionsConfig, function(option, index) {
               var currOption = {};
               currOption.label = option.value;
               currOption.value = option.value;
               optionsConfig.fixed.push(currOption);
            });
            config.optionsConfig = optionsConfig;
            
            // Process the rules...
            // TODO: Currently the form creator does not allow for initial values to be specified...
            config.visibilityConfig = _this.processRules(field.visibilityConfig);
            config.disablementConfig = _this.processRules(field.disablementConfig);
            config.requirementConfig = _this.processRules(field.requirementConfig);
            
            currWidget.config = config;
            config.widgets.push(currWidget);
         });
         
         // Create a new form using the configuration constructed...
         var previewForm = new FormPreview({widgets: config.widgets});
         
         popup.moveOffScreen(previewForm);
   
         // if the pop-up has not been started yet, start it now
         if(previewForm.startup && !previewForm._started){
            previewForm.startup();
         }

         // make the pop-up appear around my node
         popup.open({
             parent: this,
             popup: previewForm,
             around: this.previewLink,
             orient: ["below-centered", "above-centered"],
             onExecute: function(){
                 popup.close(previewForm);
             },
             onCancel: function(){
                 popup.close(previewForm);
             },
             onClose: function() {
                previewForm.destroy();
             }
         });
         
         // When the closePreview function is executed on the preview we will close the popup that contains it...
         aspect.after(previewForm, "closePreview", function(deferred) {
            popup.close();
         });
      },

      /**
       * Attempt to generate this structure:
       * 
       * rules: {
       *    "employed" : {
       *       is: ["a"],
       *       isNot: ["u"]
       *    }
       * }
       */
      processRules: function(rawConfig) {
         var rules = {};
         if (rawConfig)
         {
            array.forEach(rawConfig, function(rawRule, index) {
               rules[rawRule.field] = {};
               rules[rawRule.field].is = rawRule.is.split(",");
               rules[rawRule.field].isNot = rawRule.isNot.split(",");
            });
         }
         return rules;
      }
   });
});