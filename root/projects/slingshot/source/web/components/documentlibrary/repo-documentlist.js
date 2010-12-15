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
   var $html = Alfresco.util.encodeHTML,
      $links = Alfresco.util.activateLinks,
      $combine = Alfresco.util.combinePaths,
      $userProfile = Alfresco.util.userProfileLink,
      $date = function $date(date, format) { return Alfresco.util.formatDate(Alfresco.util.fromISO8601(date), format) };

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
    * Generate "changeFilter" event mark-up specifically for category changes
    *
    * @method generateCategoryMarkup
    * @param category {Array} category[0] is name, category[1] is qnamePath
    * @return {string} Mark-up for use in node attribute
    */
   Alfresco.DocumentList.generateCategoryMarkup = function RDL_generateCategoryMarkup(category)
   {
      return Alfresco.DocumentList.generateFilterMarkup(
      {
         filterId: "category",
         filterData: $combine(category[1], category[0])
      });
   };

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.RepositoryDocumentList.prototype,
   {
      /**
       * DataTable Cell Renderers
       */

      /**
       * Returns description/detail custom datacell formatter
       *
       * @method fnRenderCellDescription
       * @override
       * @param scope {object} DataTable owner scope
       */
      fnRenderCellDescription: function RDL_fnRenderCellDescription()
      {
         var scope = this;
         
         /**
          * Description/detail custom datacell formatter
          *
          * @method fnRenderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function RDL_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var desc = "", docDetailsUrl, tags, tag, categories, category, i, j;
            var record = oRecord.getData(),
               type = record.type,
               isLink = record.isLink,
               locn = record.location,
               title = "",
               description = record.description || scope.msg("details.description.none");

            // Link handling
            if (isLink)
            {
               oRecord.setData("linkedDisplayName", record.displayName);
               oRecord.setData("displayName", scope.msg("details.link-to", record.displayName));
            }

            // Use title property if it's available
            if (record.title && record.title !== record.displayName)
            {
               title = '<span class="title">(' + $html(record.title) + ')</span>';
            }

            if (type == "folder")
            {
               /**
                * Folders
                */
               desc += '<h3 class="filename">' + Alfresco.DocumentList.generateFavourite(scope, oRecord) + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '">';
               desc += $html(record.displayName) + '</a>' + title + '</h3>';

               if (scope.options.simpleView)
               {
                  /**
                   * Simple View
                   */
                  desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn, "dd mmmm yyyy") + '</span>';
                  desc += '<span class="item-simple"><em>' + scope.msg("details.by") + '</em> ' + $userProfile(record.modifiedByUser, record.modifiedBy) + '</span></div>';
               }
               else
               {
                  /**
                   * Detailed View
                   */
                  desc += '<div class="detail"><span class="item"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn) + '</span>';
                  desc += '<span class="item"><em>' + scope.msg("details.modified.by") + '</em> ' + $userProfile(record.modifiedByUser, record.modifiedBy) + '</span></div>';
                  desc += '<div class="detail"><span class="item"><em>' + scope.msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';

                  /* Categories */
                  categories = record.categories;
                  desc += '<div class="detail"><span class="item category-item"><em>' + scope.msg("details.categories") + '</em> ';
                  if (categories.length > 0)
                  {
                     for (i = 0, j = categories.length; i < j; i++)
                     {
                        category = categories[i];
                        desc += '<span class="category"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generateCategoryMarkup(category) + '">' + $html(category[0]) + '</a></span>' + (j - i > 1 ? ", " : "");
                     }
                  }
                  else
                  {
                     desc += scope.msg("details.categories.none");
                  }
                  desc += '</span></div>';

                  /* Tags */
                  tags = record.tags;
                  desc += '<div class="detail"><span class="item tag-item"><em>' + scope.msg("details.tags") + '</em> ';
                  if (tags.length > 0)
                  {
                     for (i = 0, j = tags.length; i < j; i++)
                     {
                        tag = $html(tags[i]);
                        desc += '<span class="tag"><a href="#" class="tag-link" rel="' + tag + '" title="' + tags[i] + '">' + tag + '</a></span>' + (j - i > 1 ? ", " : "");
                     }
                  }
                  else
                  {
                     desc += scope.msg("details.tags.none");
                  }
                  desc += '</span></div>';
               }
            }
            else
            {
               /**
                * Documents and Links
                */
               docDetailsUrl = scope.getActionUrls(oRecord.getData()).documentDetailsUrl;

               // Locked / Working Copy handling
               if (record.lockedByUser && record.lockedByUser !== "")
               {
                  var lockedByLink = $userProfile(record.lockedByUser, record.lockedBy);

                  /* Google Docs Integration */
                  if (record.custom.googleDocUrl && record.custom.googleDocUrl !== "")
                  {
                     if (record.lockedByUser === Alfresco.constants.USERNAME)
                     {
                        desc += '<div class="info-banner">' + scope.msg("details.banner.google-docs-owner", '<a href="' + record.custom.googleDocUrl + '" target="_blank">' + scope.msg("details.banner.google-docs.link") + '</a>') + '</div>';
                     }
                     else
                     {
                        desc += '<div class="info-banner">' + scope.msg("details.banner.google-docs-locked", lockedByLink, '<a href="' + record.custom.googleDocUrl + '" target="_blank">' + scope.msg("details.banner.google-docs.link") + '</a>') + '</div>';
                     }
                  }
                  /* Regular Working Copy handling */
                  else
                  {
                     if (record.lockedByUser === Alfresco.constants.USERNAME)
                     {
                        desc += '<div class="info-banner">' + scope.msg("details.banner." + (record.actionSet === "lockOwner" ? "lock-owner" : "editing")) + '</div>';
                     }
                     else
                     {
                        desc += '<div class="info-banner">' + scope.msg("details.banner.locked", lockedByLink) + '</div>';
                     }
                  }
               }

               desc += '<h3 class="filename">' + Alfresco.DocumentList.generateFavourite(scope, oRecord) + '<span id="' + scope.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '">';
               desc += $html(record.displayName) + '</a></span>' + title + '</h3>';

               if (scope.options.simpleView)
               {
                  /**
                   * Simple View
                   */
                  desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn, "dd mmmm yyyy") + '</span>';
                  desc += '<span class="item-simple"><em>' + scope.msg("details.by") + '</em> ' + $userProfile(record.modifiedByUser, record.modifiedBy) + '</span></div>';
               }
               else
               {
                  /**
                   * Detailed View
                   */
                  if (record.custom.isWorkingCopy)
                  {
                     /**
                      * Working Copy
                      */
                     desc += '<div class="detail">';
                     desc += '<span class="item"><em>' + scope.msg("details.editing-started.on") + '</em> ' + $date(record.modifiedOn) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.editing-started.by") + '</em> ' + $userProfile(record.modifiedByUser, record.modifiedBy) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.size") + '</em> ' + Alfresco.util.formatFileSize(record.size) + '</span>';
                     desc += '</div><div class="detail">';
                     desc += '<span class="item"><em>' + scope.msg("details.description") + '</em> ' + $links($html(description)) + '</span>';
                     desc += '</div>';
                  }
                  else
                  {
                     /**
                      * Non-Working Copy
                      */
                     desc += '<div class="detail">';
                     desc += '<span class="item"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.modified.by") + '</em> ' + $userProfile(record.modifiedByUser, record.modifiedBy) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.version") + '</em> ' + record.version + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.size") + '</em> ' + Alfresco.util.formatFileSize(record.size) + '</span>';
                     desc += '</div><div class="detail">';
                     desc += '<span class="item"><em>' + scope.msg("details.description") + '</em> ' + $links($html(description)) + '</span>';
                     desc += '</div>';

                     /* Categories */
                     categories = record.categories;
                     desc += '<div class="detail"><span class="item category-item"><em>' + scope.msg("details.categories") + '</em> ';
                     if (categories.length > 0)
                     {
                        for (i = 0, j = categories.length; i < j; i++)
                        {
                           category = categories[i];
                           desc += '<span class="category"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generateCategoryMarkup(category) + '">' + $html(category[0]) + '</a></span>' + (j - i > 1 ? ", " : "");
                        }
                     }
                     else
                     {
                        desc += scope.msg("details.categories.none");
                     }
                     desc += '</span></div>';

                     /* Tags */
                     tags = record.tags;
                     desc += '<div class="detail"><span class="item tag-item"><em>' + scope.msg("details.tags") + '</em> ';
                     if (tags.length > 0)
                     {
                        for (i = 0, j = tags.length; i < j; i++)
                        {
                           tag = $html(tags[i]);
                           desc += '<span class="tag"><a href="#" class="tag-link" rel="' + tag + '" title="' + tags[i] + '">' + tag + '</a></span>' + (j - i > 1 ? ", " : "");
                        }
                     }
                     else
                     {
                        desc += scope.msg("details.tags.none");
                     }
                     desc += '</span></div>';
                  }
               }
            }
            elCell.innerHTML = desc;
         };
      },

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
         
         // No-cache
         params += "&noCache=" + new Date().getTime();
         
         // Repository mode (don't resolve Site-based folders)
         params += "&libraryRoot=" + encodeURIComponent(this.options.rootNode.toString());

         return params;
      }
   }, true);
})();
