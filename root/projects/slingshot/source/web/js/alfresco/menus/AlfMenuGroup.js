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
define(["dojo/_base/declare",
        "dojo/text!./templates/AlfMenuGroup.html",
        "alfresco/core/Core",
        "alfresco/menus/AlfDropDownMenu",
        "dojo/_base/event",
        "dojo/dom-style",
        "dojo/dom-class",
        "dojo/keys",
        "dijit/popup"], 
        function(declare, template, AlfCore, AlfDropDownMenu, event, domStyle, domClass, keys, popup) {
   
   return declare([AlfDropDownMenu, AlfCore], {
      
      // TODO: There's an argument that this should actually extend (rather than wrap) the DropDownMenu to avoid needing to delegate the functions
      
      /**
       * The HTML template to use for the widget.
       * @property template {String}
       */
      templateString: template,
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/AlfMenuGroup.css"}],
      
      /**
       * The label for the group. If this is left as the empty string then the group label node will be
       * hidden completely. The value assigned to label can either be an i18n property key or a value
       * but an attempt will be made to look up the assigned value in the available i18n keys.
       * 
       * @property {string} label#
       * @default ""
       */
      label: "",
      
      constructor: function alfresco_services_NavigationService__constructor(args) {
         this.ddmTemplateString = AlfDropDownMenu.prototype.templateString;
      },
      
      
      /**
       * Sets the group label and creates a new alfresco/menus/AlfDropDownMenu to contain the items 
       * in the group.
       * 
       * @method postCreate
       */
      postCreate: function alf_menus_AlfMenuGroup__postCreate() {
         
         if (this.label == "")
         {
            // If there is no label for the title then hide the title node entirely...
            domStyle.set(this._groupTitleNode, "display", "none");
         }
         else
         {
            // Make sure that an attempt is made to get the localized label...
            this.label = this.message(this.label);
            this._groupTitleNode.innerHTML = this.label;
         }
         
         // Setup the Drop down menu as normal...
         this.inherited(arguments);
      },
      
      /**
       * 
       * @method isFocusable
       */
      isFocusable: function alf_menus_AlfMenuGroup__isFocusable() {
         return this.hasChildren();
      },
      
      /**
       * This function is set as the _itemKeyPress handler on the "alfresco/menus/AlfDropDownMenu" that is created for 
       * this group. It's purpose is to handle keyboard navigation events that occur within the drop down menu. It is 
       * necessary to handle these events explicitly because of the additional widgets that are placed in the menu 
       * stack that allow Alfresco specific behaviour and styling.
       * 
       * 
       * @method dropDownMenuItemKeyPress
       * @param {object} evt The key press event
       */
      _itemKeyPress: function alf_menus_AlfMenuGroup___itemKeyPress(evt) {
         
         this.alfLog("log", "Item key press", evt);
         
         // Set up sensible variables for the various contexts...
         var dropDownMenu = this,
             menuGroup = this,
             groupParent = menuGroup.getParent(); 

         // Get the key code... depending upon how the event has been generated there might be different properties
         // available for accessing the key code. Try the charOrCode attribute first (this comes from Dojo) and then
         // fall back to keyCode...         
         var keyCode = (evt.charOrCode) ? evt.charOrCode : evt.keyCode;
         if (keyCode == menuGroup._openSubMenuKey)
         {
            // Open cascading menu if available but if there is no cascading menu then find a menu bar in the 
            // stack and focus the next item on it.
            if(this.focusedChild && this.focusedChild.popup && !this.focusedChild.disabled)
            {
                // This first block is identical to that of the inherited function...
                this.alfLog("log", "Open cascading menu");
                this.onItemClick(this.focusedChild, evt);
            }
            else 
            {
               // Find the top menu and focus next in it...
               this.alfLog("log", "Try and find a menu bar in the stack and move to next");
               var menuBarAncestor = this.findMenuBarAncestor(groupParent);
               if (menuBarAncestor)
               {
                  this.alfLog("log", "Go to next item in menu bar");
                  menuBarAncestor.focusNext()
               }
            }
         }
         else if (keyCode == menuGroup._closeSubMenuKey)
         {
            // Close sub-menus and if there is no sub-menu but the group is a child of a menu bar then then 
            // move to the previous item in that menu bar...
            if (groupParent.parentMenu && !groupParent.parentMenu._isMenuBar)
            {
               this.alfLog("log", "Close cascading menu");
               this.getParent().parentMenu.focus();
               popup.close(this.getParent());
            }
            else if (groupParent.parentMenu && groupParent.parentMenu._isMenuBar)
            {
               // Focus the previous item of the menu bar...
               this.alfLog("log", "Focus previous item in menu bar");
               groupParent.parentMenu.focusPrev()
            }
         }
      },
      
      /**
       * This function will work up the stack of menus to find the first menu bar in the stack. This 
       * is required because of the additional grouping capabilities that have been added to the basic
       * Dojo menu widgets. In the core Dojo code the "parentMenu" attribute is used to work up the stack
       * but not all widgets in the Alfresco menu stack have this attribute (and it was not possible to
       * set it correctly during the widget processing phase). 
       * 
       * @method findMenuBarAncestor
       * @return Either null if a menu bar cannot be found or a menu bar widget.
       */
      findMenuBarAncestor: function alf_menus_AlfMenuGroup__findMenuBarAncestor(currentMenu) {
         var reachedMenuTop = false;
         while (!reachedMenuTop && !currentMenu._isMenuBar)
         {
            if (currentMenu.parentMenu)
            {
               // The current menu item has a parent menu item - assign it as the current menu...
               currentMenu = currentMenu.parentMenu;
            }
            else
            {
               // Go up the widget stack until we either run out of ancestors or find another parent menu...
               var parent = currentMenu.getParent();
               while (parent && !parent.parentMenu)
               {
                  parent = parent.getParent();
               }
               if (parent && parent.parentMenu)
               {
                  currentMenu = parent.parentMenu;
               }
               reachedMenuTop = (parent == null);
            }
         }
         var menuBar = (currentMenu._isMenuBar) ? currentMenu : null;
         return menuBar;
      }
   });
});