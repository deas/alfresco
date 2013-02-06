define(["dojo/_base/declare",
        "alfresco/forms/controls/MultipleEntryElement", 
        "dijit/form/TextBox",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dijit/registry"], 
        function(declare, MultipleEntryElement, TextBox, domConstruct, domClass, registry) {
   
   return declare([MultipleEntryElement], {
      
      determineKeyAndValue: function() {
         this.inherited(arguments);
         
         if (typeof this.value.value == "undefined")
         {
            // TODO: We should probably deal with null values better here. For example, we don't really
            // want to have an object with lots of keys that are the empty string. Some form of validation
            // is going to be required.
            this.value.value = {};
            this.value.value._key = "";
            this.value.value._value = "";
         }
      },
      
      /**
       * The default read display simply shows the value of the element.
       */
      createReadDisplay: function() {
         // Set the innerHTML of the read display to be the value...
         this.readDisplay.innerHTML = this.value.value._key + " = " + this.value.value._value;
      },
      
      _keyTextBox: null,
      
      _valueTextBox: null,
      
      createEditDisplay: function() {
         domConstruct.empty(this.editDisplay);
         var _this = this;
         this._keyTextBox = new TextBox({value: this.value.value._key});
         this._keyTextBox.placeAt(this.editDisplay);
         this._keyTextBox.watch("value", function(name, oldValue, value) {
            _this.value.value._key = value;
            _this.createReadDisplay();
         }, this);
         this._valueTextBox = new TextBox({value: this.value.value._value});
         this._valueTextBox.placeAt(this.editDisplay);
         this._valueTextBox.watch("value", function(name, oldValue, value) {
            _this.value.value._value = value; // Best line of JavaScript EVER !!!
            _this.createReadDisplay();
         }, this);
      }
   });
});