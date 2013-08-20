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
 * @module alfresco/wrapped/HeaderJsWrapper
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes dijit/_KeyNavContainer
 * @mixes dijit/_CssStateMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "dijit/_TemplatedMixin",
        "dijit/_KeyNavContainer",
        "dijit/_CssStateMixin",
        "dojo/text!./templates/HeaderJsWrapper.html",
        "alfresco/core/Core",
        "dojo/keys",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dojo/dom-geometry",
        "dojo/on",
        "dojo/aspect"], 
        function(declare, _WidgetBase, _TemplatedMixin, _KeyNavContainer, _CssStateMixin, template, AlfCore, keys, domConstruct, domClass, domGeom, on, aspect) {
   
   /**
    * @class The purpose of this widget is to provide a way to wrap the site menu defined by the 
    * "old" header configuration into the new framework header menu. 
    */
   return declare([_WidgetBase, _TemplatedMixin, _KeyNavContainer, _CssStateMixin, AlfCore], {

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       */
      cssRequirements: [{cssFile:"./css/HeaderJsWrapper.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * @instance
       * @type {string[]}
       */
      templateMessages: null,
      
      /**
       * A string that can be evaluated into an object to instantiate
       * @instance
       * @type {string}
       * @default null
       */
      objectToInstantiate: null,
      
      /**
       * A reference to the instantiated object.
       * @instance
       * @type {object} 
       * @default null
       */
      instantiatedObject: null,
      
      /**
       * The id of the item.
       * @instance
       * @type {string} 
       * @default null
       */
      itemId: null,
      
      /**
       * The id of the current site
       * @instance
       * @type {string} 
       * @default null
       */
      siteId: null,
      
      /**
       * The label for the header item.
       * @instance
       * @type {string}
       * @default ""
       */
      label: "",
      
      
      /**
       * Creates the supplied JavaScript object that is to be wrapped in a header menu item.
       * 
       * @instance
       */
      postCreate: function alfresco_wrapped_HeaderJsWrapper__postCreate() {
         
         if (this.objectToInstantiate && this.itemId)
         {
            // Create a new DOM node for the wrapped widget to go in (whatever it may be!)
            var containerNode = domConstruct.create("div", { id: this.itemId });
            
            // Instantiate the object supplied and call .setOptions() on it...
            // Although this may seem fairly specific, this is actually how the original header code interpreted the
            // configuration!
            var c = eval(this.objectToInstantiate);
            this.instantiatedObject = new c(this.itemId);
            
            // Check that the item has actually been instantiated and that it has a .setOptions() function (just be safe!)
            if (this.instantiatedObject && typeof this.instantiatedObject.setOptions == "function")
            {
               var options = {
                  siteId: (this.siteId ? this.siteId : "")
               };
               this.instantiatedObject.setOptions(options);
            }
            // If the item exists then call it's onReady method (again, this somewhat assumes the contract defined by 
            // the previous implementation code)...
            if (this.instantiatedObject)
            {
               this.instantiatedObject.onReady();
            }
            
            // Add the new DOM node into the template...
            domConstruct.place(containerNode, this._wrappedWidgetNode);
         }
      },
      
      /**
       * The startup function is overridden here to allow us to use aspect to call a function after the "_moveToPopup" function
       * is called on the parent menu bar. This method will be called when the user uses the down cursor key when the menu bar
       * item has focus and gives us the opportunity to display the menu (which is done via a call to the "showWrappedMenu" function).
       * 
       * This code is quite specific to the original Share implementation however, it is unknown to what extent the Share menu bar
       * has been configured with custom JavaScript code and the extension mechanism is quite tightly specific to this type of menu.
       * If custom JavaScript has been written that this wrapper does not support then it should be a simple exercise to extend
       * it to handle those additional events. 
       * 
       * This is really the best option given the unknowns and the expected (un)likelihood of this being required by any 3rd party code.  
       * @instance
       */
      startup: function alfresco_wrapped_HeaderJsWrapper__startup() {
         var _this = this;
         var parentMenuBar = this.getParent();
         if (parentMenuBar)
         {
            aspect.after(parentMenuBar, "_moveToPopup", function(evt) {
               if (this.focusedChild == _this)
               {
                  _this.showWrappedMenu();
               }
            }, true);
         }
      },
      
      /**
       * Displays the wrapped share menu.
       * 
       * @instance
       */
      showWrappedMenu: function alfresco_wrapped_HeaderJsWrapper__showWrappedMenu() {
         this.alfLog("log", "Opening wrapped menu");
         try
         {
            // Find the position of the menu bar item and use it to set the position of the YUI menu.
            // This is required because without it the menu pops up in unexpected places !!
            var pos = domGeom.position(this.focusNode);
            this.instantiatedObject.widgets.sitesButton.getMenu().moveTo(pos.x, pos.y + pos.h);
            this.instantiatedObject.widgets.sitesButton.getMenu().show();
         }
         catch(e)
         {
            this.alfLog("log", "No site button to get menu for", e);
         }
      },
      
      /**
       * This function is implemented to indicate whether or not the wrapped item can be focused. It is focusable if
       * the item has a focus function that can be called.
       * 
       * @instance
       * @returns {boolean} true if there is a wrapped item and it has a focus function.
       */
      isFocusable: function  alfresco_wrapped_HeaderJsWrapper__isFocusable() {
         var focusable = (this.instantiatedObject);
         this.alfLog("log", "HeaderJsWrapper focusable?", focusable);
         return focusable;
      },

      /**
       * This function is implemented to delegate the handling of focus events to the wrapped item.
       * 
       * @instance
       */
      focus: function alfresco_wrapped_HeaderJsWrapper__focus() {
         this.alfLog("log", "HeaderJsWrapper focus");
         this.focusNode.focus();
      },
      
      /**
       * 
       * @instance
       */
      _onFocus: function alfresco_wrapped_HeaderJsWrapper___onFocus() {
         this.alfLog("log", "Got focus");
         this._setSelected(true);
         this.getParent().focusChild(this);
         this.inherited(arguments);
      },
      
      /**
       * This function is implemented to delegate the handling of _setSelected calls to the wrapped item.
       * 
       * @instance
       * @param {boolean} Indicates whether ot not the item is selected
       */
      _setSelected: function alfresco_wrapped_HeaderJsWrapper___setSelected(selected) {
         this.alfLog("log", "HeaderJsWrapper _setSelected", selected);
         domClass.toggle(this.domNode, "dijitMenuItemSelected", selected);
      },
      
      /**
       * This function is implemented to delegate the handling of onClick calls to the wrapped item.
       * 
       * @instance
       * @param {object} evt The click event
       */
      onClick: function alfresco_wrapped_HeaderJsWrapper__onClick(evt){
         this.showWrappedMenu();
      }
   });
});