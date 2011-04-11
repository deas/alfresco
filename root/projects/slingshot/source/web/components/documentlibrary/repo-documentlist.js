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
 * Repository DocumentList component.
 *
 * @namespace Alfresco
 * @class Alfresco.RepositoryDocumentList
 * @superclass Alfresco.DocumentList
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $combine = Alfresco.util.combinePaths;

   /**
    * RepositoryDocumentList constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RepositoryDocumentList} The new Records DocumentList instance
    * @constructor
    */
   Alfresco.RepositoryDocumentList = function(htmlId)
   {
      return Alfresco.RepositoryDocumentList.superclass.constructor.call(this, htmlId);
   };

   /**
    * Extend Alfresco.DocumentList
    */
   YAHOO.extend(Alfresco.RepositoryDocumentList, Alfresco.DocumentList);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.RepositoryDocumentList.prototype,
   {
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @override
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.RepositoryDocumentList} returns 'this' for method chaining
       */
      setOptions: function RDL_setOptions(obj)
      {
         return Alfresco.RepositoryDocumentList.superclass.setOptions.call(this, YAHOO.lang.merge(
         {
            workingMode: Alfresco.doclib.MODE_REPOSITORY
         }, obj));
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Build URI parameter string for doclist JSON data webscript
       *
       * @override
       * @method _buildDocListParams
       * @param p_obj.page {string} Page number
       * @param p_obj.pageSize {string} Number of items per page
       * @param p_obj.path {string} Path to query
       * @param p_obj.type {string} Filetype to filter: "all", "documents", "folders"
       * @param p_obj.site {string} Current site
       * @param p_obj.container {string} Current container
       * @param p_obj.filter {string} Current filter
       */
      _buildDocListParams: function RDL__buildDocListParams(p_obj)
      {
         // Essential defaults
         var obj =
         {
            path: this.currentPath,
            type: this.options.showFolders ? "all" : "documents",
            filter: this.currentFilter
         };

         // Pagination in use?
         if (this.options.usePagination)
         {
            obj.page = this.widgets.paginator.getCurrentPage() || this.currentPage;
            obj.pageSize = this.widgets.paginator.getRowsPerPage();
         }

         // Passed-in overrides
         if (typeof p_obj == "object")
         {
            obj = YAHOO.lang.merge(obj, p_obj);
         }

         // Build the URI stem
         var params = YAHOO.lang.substitute("{type}/node/alfresco/company/home" + (obj.filter.filterId == "path" || obj.filter.filterId == "category" ? "{path}" : ""),
         {
            type: encodeURIComponent(obj.type),
            path: $combine("/", Alfresco.util.encodeURIPath(obj.path))
         });

         // Filter parameters
         params += "?filter=" + encodeURIComponent(obj.filter.filterId);
         if (obj.filter.filterData && obj.filter.filterId !== "path")
         {
            params += "&filterData=" + encodeURIComponent(obj.filter.filterData);
         }

         // Paging parameters
         if (this.options.usePagination)
         {
            params += "&size=" + obj.pageSize  + "&pos=" + obj.page;
         }

         // Sort parameters
         params += "&sortAsc=" + this.options.sortAscending + "&sortField=" + encodeURIComponent(this.options.sortField);

         // No-cache
         params += "&noCache=" + new Date().getTime();

         // Repository mode (don't resolve Site-based folders)
         params += "&libraryRoot=" + encodeURIComponent(this.options.rootNode.toString());

         return params;
      }
   }, true);
})();
