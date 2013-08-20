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
 * @module alfresco/users/SingleUserSelect
 * @extends dijit/form/FilteringSelect
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/form/FilteringSelect", 
        "alfresco/core/Core",
        "dojo/store/JsonRest",
        "dojo/text!./templates/UserItem.html",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/string",
        "dojo/dom-class",
        "alfresco/users/SingleUserSelectMenu"], 
        function(declare, FilteringSelect, AlfCore, JsonRest, UserTemplate, lang, array, stringUtil, domClass, SingleUserSelectMenu) {
   
   return declare([FilteringSelect, AlfCore], {
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/SingleUserSelect.css"}],
      
      /**
       * @instance
       * @type {object}
       */
      dropDownClass: SingleUserSelectMenu,

      /**
       * Extends the inherited function to create a JsonRest store that uses the standard Alfresco people REST API for
       * retrieving user information. The URL can be changed by overriding the getRestUrl function. Please be aware
       * that this entire widget is constructed around that API so changing the URL may also require overriding other
       * functions.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_users_SingleUserSelect__postMixInProperties() {
         if (this.store == null)
         {
            this.store = new JsonRest({target: this.getRestUrl()});
         }
         this.inherited(arguments);
      },
      
      /**
       * Returns the URL for the standard Alfresco people REST API.
       * @instance 
       */
      getRestUrl: function alfresco_users_SingleUserSelect__getRestUrl() {
         return Alfresco.constants.PROXY_URI + "api/people";
      },
      
      /**
       * Adds a custom CSS class to the root DOM node of the widget. This ensures that we can tweak the styling of the widget.
       *  
       * @instance
       */
      postCreate: function alfresco_users_SingleUserSelect__postCreate() {
         domClass.add(this.domNode, "alfresco-users-SingleUserSelect");
         this.inherited(arguments);
      },
      
      /**
       * Extends the inherited function to create a label based on the users first and last name.
       * 
       * @instance
       */
      labelFunc: function(item, store) {
         return item.firstName + " " + item.lastName;
      },
      
      /**
       * Extends the inherited function to use a template to construct a richer item display than just the user name. The
       * default template shows the user avatar (if they have one) along with their full name and e-mail address.
       * 
       * @instance
       * @param {object} item The item to be displayed
       */
      _getMenuLabelFromItem: function alfresco_users_SingleUserSelect___getMenuLabelFromItem(item){
         var menuLabel = this.inherited(arguments);
         menuLabel.html = true;
         
         var updatedLabel = stringUtil.substitute(UserTemplate, {
            avatarUrl: (item.avatar ? Alfresco.constants.PROXY_URI + item.avatar : Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png"),
            name: menuLabel.label,
            email: item.email
         })
         
         menuLabel.label = updatedLabel;
         return menuLabel;
      },
      
      /**
       * Extends the inherited function to manipulate the results list. This is necessary for 2 reasons. Firstly the 
       * standard person REST API takes a "filter" request parameter but a "filter" attribute is not included in 
       * each item in the returned list, and secondly the list of items is nested within the returned JSON response.
       * In order for the inherited FilteringSelect capabilities to work it is necessary to make the array the
       * results argument and to iterate over each item in the array and add a "filter" attribute. This filter attribute
       * is set to be the concatenation of the first and last name of the user. This is especially important as
       * the extended FilteringSelect widget uses this information to "announce" the selection to screen readers.
       * 
       * Unfortunately we are extending a function prefixed with the underscore which indicates that it is notionally
       * private an therefore there is a risk that this function may be changed in future releases.
       * 
       * @instance
       * @param {object[]} results The results returned from the REST call.
       * @param {object} query The query object to be processed
       * @param {object} options The search options to process.
       */
      _openResultList: function alfresco_users_SingleUserSelect___openResultList(results, query, options ) {
         var updatedResults = results.people;
         array.forEach(updatedResults, lang.hitch(this, "generateFilterAttr"));
         this.inherited(arguments, [updatedResults, query, options]);
      },
      
      /**
       * Creates a "filter" attribute in the supplied item by combining the "firstName" and "lastName" attributes
       * of the item.
       * 
       * @instance
       * @param {object} item The item to create the filter attribute for
       * @param {number} i The index of the item in the results list.
       */
      generateFilterAttr: function alfresco_users_SingleUserSelect__generateFilterAttr(item, i) {
         item.filter = item.firstName + " " + item.lastName;
      }
   });
});