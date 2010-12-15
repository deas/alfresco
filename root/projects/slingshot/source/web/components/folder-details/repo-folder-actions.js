/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * Repository Folder actions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RepositoryFolderActions
 */
(function()
{
   /**
    * RepositoryFolderActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RepositoryFolderActions} The new RepositoryFolderActions instance
    * @constructor
    */
   Alfresco.RepositoryFolderActions = function(htmlId)
   {
      return Alfresco.RepositoryFolderActions.superclass.constructor.call(this, htmlId);
   };
   
   /**
    * Extend prototype with main class implementation and overrides
    */
   YAHOO.extend(Alfresco.RepositoryFolderActions, Alfresco.FolderActions,
   {
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @override
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.RepositoryFolderActions} returns 'this' for method chaining
       */
      setOptions: function RepositoryFolderActions_setOptions(obj)
      {
         return Alfresco.RepositoryFolderActions.superclass.setOptions.call(this, YAHOO.lang.merge(
         {
            workingMode: Alfresco.doclib.MODE_REPOSITORY
         }, obj));
      }
   });
})();