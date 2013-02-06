define(["alfresco/forms/controls/BaseFormControl",
        "dojo/_base/declare",
        "dijit/form/Textarea"], 
        function(BaseFormControl, declare, Textarea) {
   
   return declare([BaseFormControl], {
      
      getWidgetConfig: function() {
         // Return the configuration for the widget
         return {
            id : this.generateUuid(),
            name: this.name,
            value: this.value
         };
      },
      
      createFormControl: function(config, domNode) {
         return new Textarea(config);
      },
      
      /**
       * This will be set to the last known value of the text box before the current keyup event.
       */
      _oldValue: null,
      
      /**
       * This is used as a temporary buffer variable to keep track of changes to the old value. 
       */
      __oldValue: null,

      /**
       * Overrides the default change events to use blur events on the text box. This is done so that we can validate
       * on every single keypress. However, we need to keep track of old values as this information is not readily
       * available from the text box itself.
       */
      setupChangeEvents: function() {
         var _this = this;
         
         if (this.wrappedWidget)
         {
            this.wrappedWidget.on("keyup", function() {
               _this._oldValue = _this.__oldValue; // Set the old value as the last buffer...
               _this.__oldValue = this.getValue(); // Make the last buffer the current value being set
               
               _this.alfLog("log", "keyup - OLD value: " + _this._oldValue + ", NEW value: " + this.getValue());
               _this.formControlValueChange(_this.name, _this._oldValue, this.getValue());
               _this.validate();
            });
         }
      }
   });
});