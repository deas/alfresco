define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dijit/_FocusMixin",
        "dojo/text!./templates/MultipleEntryElementWrapper.html",
        "alfresco/core/Core",
        "dojo/_base/array",
        "dojo/dom-class",
        "dijit/focus"], 
        function(declare, _Widget, _Templated, _FocusMixin, template, AlfCore, array, domClass, focusUtil) {
   
   return declare([_Widget, _Templated, _FocusMixin, AlfCore], {
      templateString: template,
      
      /**
       * This keeps track of the MultipleEntryCreator that created this wrapper. It is required so that the
       * MultipleEntryCreator can be updated with delete requests.
       */
      creator: null,
      
      constructor: function(args) {
         declare.safeMixin(this, args);
         // The data supplied to the widget will be some form of JSON. It should be an array
         // where each element will correspond to a MultipleEntryElement.
         
      },
      
      /**
       * The widget should be passed as a constructor configuration argument.
       */
      widget: null,
      
      postCreate: function() {
         
         // Check that a widget has been provided and then add it into the correct node...
         if (this.widget != null)
         {
            this.widget.wrapper = this;
            this.widget.placeAt(this.containerNode);
         }
      },
      
      /**
       * This called when the edit button is clicked. It switches the element from read to edit mode.
       */
      editElement: function() {
         this.alfLog("log", "Edit element clicked", {});
         
         // Switch the widget into edit mode...
         if (this.widget && typeof this.widget.editMode == "function")
         {
            this.widget.editMode(true);
            
            // Hide the edit and delete buttons...
            if (this.deleteButton)
            {
               domClass.add(this.deleteButton, "multiple-entry-element-clear-hide");
            }
            if (this.editButton)
            {
               domClass.add(this.editButton, "multiple-entry-element-clear-hide");
            }
         }
      },
      
      deleteElement: function(e) {
         this.alfLog("log", "Delete element clicked", {});
         
         // When the delete button is clicked the wrapper should be removed and it's data should also be removed from
         // the overall value of the widget.
         this.creator.deleteEntry(this);
      },
      
      /**
       * When the widget loses focus we want to leave edit mode.
       */
      _onBlur: function(){
         
         var _this = this;
         if (array.some(focusUtil.activeStack, function(item) { return item == _this.id; }))
         {
            // The wrapped widget is still in the stack so don't leave edit mode yet.
            this.alfLog("log", "Blur detected but wrapped widget still in active stack");
         }
         else
         {
            this.alfLog("log", "Lost focus so leaving edit mode", {});
            this.blurWrapper();
         }
         this.inherited(arguments);
      },
      
      /**
       * Handles blurring of the wrapper
       */
      blurWrapper: function() {
         if (this.widget && typeof this.widget.editMode == "function")
         {
            this.widget.editMode(false);
            
            // Show the edit and delete buttons (check for the nodes existences in case an 
            // extension has overridden the template to remove them)...
            if (this.deleteButton)
            {
               domClass.remove(this.deleteButton, "multiple-entry-element-clear-hide");
            }
            if (this.editButton)
            {
               domClass.remove(this.editButton, "multiple-entry-element-clear-hide");
            }
         }
      }
   });
});