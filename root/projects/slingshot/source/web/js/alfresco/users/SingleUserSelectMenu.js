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
 * This widget is referenced from the SingleUserSelect widget and simply adds a custom CSS class to the 
 * DOM node that is created for each item in the menu. This is done so that the styling of the menu 
 * can be controlled. 
 * 
 * @module alfresco/users/SingleUserSelectMenu
 * @extends dijit/form/_ComboBoxMenu
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/form/_ComboBoxMenu", 
        "alfresco/core/Core",
        "dojo/dom-class"], 
        function(declare, _ComboBoxMenu, AlfCore, domClass) {
   
   return declare([_ComboBoxMenu, AlfCore], {
      
      /**
       * Extends the inherited function to add a custom CSS class to the menu item node.
       * 
       * @instance
       */
      _createMenuItem: function alfresco_users_SingleUserSelectMenu___createMenuItem(dataItem) {
         var item = this.inherited(arguments);
         domClass.add(item, "alfresco-users-UserItem");
         return item;
      }
   });
});