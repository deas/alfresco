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

/**
 * This widget is used to wrap individual [multiple entry elements]{@link module:alfresco/forms/controls/MultipleEntryElement}
 * to allow them to be deleted and to switch them between edit and read mode. Read mode is designed to given a condensed human
 * readable description of the what each element represents and the edit mode allows the user to configure the behaviour.
 * 
 * @module alfresco/forms/controls/MultipleEntryElementWrapper
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes dijit/_FocusMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
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
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String} template
       */
      templateString: template,
      
      /**
       * This keeps track of the MultipleEntryCreator that created this wrapper. It is required so that the
       * MultipleEntryCreator can be updated with delete requests.
       * 
       * @instance
       * @default null
       */
      creator: null,
      
      /**
       * @instance
       */
      constructor: function(args) {
         declare.safeMixin(this, args);
      },
      
      /**
       * The widget should be passed as a constructor configuration argument.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      widget: null,
      
      /**
       * @instance
       */
      postCreate: function() {
         
         // Check that a widget has been provided and then add it into the correct node...
         if (this.widget != null)
         {
            this.widget.wrapper = this;
            this.widget.placeAt(this.containerNode);
         }
      },
      
      /**
       * Handles the completion of editing an item
       * 
       * @instance
       */
      doneEditingElement: function() {
         this.alfLog("log", "Done editing buttonclicked", this);
         this.blurWrapper();
      },
      
      /**
       * This called when the edit button is clicked. It switches the element from read to edit mode.
       * 
       * @instance
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
               domClass.remove(this.doneEditingButton, "clear-hide");
            }
            if (this.deleteButton)
            {
               domClass.add(this.deleteButton, "clear-hide");
            }
            if (this.editButton)
            {
               domClass.add(this.editButton, "clear-hide");
            }
         }
      },
      
      /**
       * @instance
       */
      deleteElement: function(e) {
         this.alfLog("log", "Delete element clicked", {});
         
         // When the delete button is clicked the wrapper should be removed and it's data should also be removed from
         // the overall value of the widget.
         this.creator.deleteEntry(this);
      },
      
      /**
       * When the widget loses focus we want to leave edit mode.
       * 
       * @instance
       */
      _onBlur: function(){
         
//         var _this = this;
//         if (array.some(focusUtil.activeStack, function(item) { return item == _this.id; }))
//         {
//            // The wrapped widget is still in the stack so don't leave edit mode yet.
//            this.alfLog("log", "Blur detected but wrapped widget still in active stack");
//         }
//         else
//         {
//            this.alfLog("log", "Lost focus so leaving edit mode", {});
//            this.blurWrapper();
//         }
         this.inherited(arguments);
      },
      
      /**
       * Handles blurring of the wrapper
       * 
       * @instance
       */
      blurWrapper: function() {
         if (this.widget && typeof this.widget.editMode == "function")
         {
            this.widget.editMode(false);
            
            // Show the edit and delete buttons (check for the nodes existences in case an 
            // extension has overridden the template to remove them)...
            if (this.deleteButton)
            {
               domClass.add(this.doneEditingButton, "clear-hide");
            }
            if (this.deleteButton)
            {
               domClass.remove(this.deleteButton, "clear-hide");
            }
            if (this.editButton)
            {
               domClass.remove(this.editButton, "clear-hide");
            }
         }
      }
   });
});