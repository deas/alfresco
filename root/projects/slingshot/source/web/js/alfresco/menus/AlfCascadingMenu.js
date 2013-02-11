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
        "dijit/PopupMenuItem",
        "alfresco/menus/_AlfMenuItemMixin",
        "alfresco/menus/AlfMenuGroups",
        "alfresco/core/Core",
        "dojo/_base/array",
        "dojo/dom-class",
        "dojo/dom-style"], 
        function(declare, PopupMenuItem, _AlfMenuItemMixin, AlfMenuGroups, AlfCore, array, domClass, domStyle) {
   
   /**
    * Currently this extends the default Dojo implementation of a MenuItem without making any changes. Despite
    * it not providing any additional value-add yet it should still be used such that changes can be applied
    * without needing to modify page definition files.
    */
   return declare([PopupMenuItem, _AlfMenuItemMixin, AlfCore], {
      
      
      /**
       * Ensures that the supplied menu item label is translated.
       * @method postCreate
       */
      postCreate: function alfresco_menus__AlfCascadingMenu__postCreate() {
         this.inherited(arguments);
         
         // Create a popup menu and add children to it...
         this.popup = new AlfMenuGroups({widgets: this.widgets});
      }
   });
});