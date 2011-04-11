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
 * DOD5015 DocumentList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsDocumentList
 * @superclass Alfresco.DocumentList
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $links = Alfresco.util.activateLinks,
      $combine = Alfresco.util.combinePaths,
      $jsonDate = Alfresco.util.fromExplodedJSONDate,
      $userProfile = Alfresco.util.userProfileLink,
      $date = function $date(date, format) { return Alfresco.util.formatDate(Alfresco.util.fromISO8601(date), format) },
      $relTime = Alfresco.util.relativeTime;

   /**
    * RecordsDocumentList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsDocumentList} The new Records DocumentList instance
    * @constructor
    */
   Alfresco.RecordsDocumentList = function(htmlId)
   {
      return Alfresco.RecordsDocumentList.superclass.constructor.call(this, htmlId);
   };
   
   /**
    * Extend Alfresco.DocumentList
    */
   YAHOO.extend(Alfresco.RecordsDocumentList, Alfresco.DocumentList);

   /**
    * Augment prototype with RecordsActions module, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentProto(Alfresco.RecordsDocumentList, Alfresco.doclib.RecordsActions, true);
   
   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.RecordsDocumentList.prototype,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       *
       * @method onReady
       * @override
       */
      onReady: function RDL_onReady()
      {
         // Disable drag and drop upload
         this.dragAndDropAllowed = false;

         return Alfresco.RecordsDocumentList.superclass.onReady.apply(this, arguments);
      },

      /**
       * DataTable Cell Renderers
       */

      /**
       * Returns thumbnail custom datacell formatter
       *
       * @method fnRenderCellThumbnail
       */
      fnRenderCellThumbnail: function RDL_fnRenderCellThumbnail()
      {
         var scope = this;
         
         /**
          * Thumbnail custom datacell formatter
          *
          * @method renderCellThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function RDL_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            var record = oRecord.getData(),
               name = record.fileName,
               title = record.title,
               type = record.type,
               isLink = record.isLink,
               locn = record.location,
               extn = name.substring(name.lastIndexOf(".")),
               docDetailsUrl;

            if (scope.options.simpleView)
            {
               /**
                * Simple View
                */
               oColumn.width = 40;
               Dom.setStyle(elCell, "width", oColumn.width + "px");
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               switch (type)
               {
                  case "record-series":
                  case "record-category":
                  case "record-folder":
                  case "metadata-stub-folder":
                  case "transfer-container":
                  case "hold-container":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/' + type + '-32.png" /></a>';
                     break;

                  case "record-nonelec":
                  case "undeclared-record-nonelec":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/non-electronic-32.png" />';
                     break;

                  case "metadata-stub":
                     var id = scope.id + '-preview-' + oRecord.getId();
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + scope.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                     elCell.innerHTML = '<span id="' + id + '" class="icon32">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/meta-stub-32.png" /></a></span>';
                     break;

                  case "folder":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/folder-32.png" /></a>';
                     break;

                  default:
                     var id = scope.id + '-preview-' + oRecord.getId();
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + scope.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                     elCell.innerHTML = '<span id="' + id + '" class="icon32">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(name) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
                  
                     // Preview tooltip
                     scope.previewTooltips.push(id);
                     break;
               }
            }
            else
            {
               /**
                * Detailed View
                */
               oColumn.width = 100;
               Dom.setStyle(elCell, "width", oColumn.width + "px");
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               switch (type)
               {
                  case "record-series":
                  case "record-category":
                  case "record-folder":
                  case "metadata-stub-folder":
                  case "transfer-container":
                  case "hold-container":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/' + type + '-48.png" /></a>';
                     break;

                  case "record-nonelec":
                  case "undeclared-record-nonelec":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/non-electronic-75x100.png" />';
                     break;

                  case "metadata-stub":
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + scope.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                     elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/meta-stub-75x100.png" /></a></span>';
                     break;

                  case "folder":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/folder-48.png" /></a>';
                     break;

                  default:
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + scope.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                     elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.DocumentList.generateThumbnailUrl(oRecord) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
                     break;
               }
            }
         };
      },

      /**
       * Returns description/detail custom datacell formatter
       *
       * @method fnRenderCellDescription
       */
      fnRenderCellDescription: function RDL_fnRenderCellDescription()
      {
         var scope = this;

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function RDL_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var desc = "",
               docDetailsUrl, i, j,
               record = oRecord.getData(),
               type = record.type,
               nodeRef = record.nodeRef,
               isLink = record.isLink,
               locn = record.location,
               dod5015 = record.dod5015,
               fileName = record.fileName,
               title = "",
               description = record.description || scope.msg("details.description.none");
            
            // Use title property if it's available
            if (record.title && record.title !== fileName)
            {
               title = '<span class="title">(' + $html(record.title) + ')</span>';
            }
            
            // Link handling
            if (isLink)
            {
               record.displayName = scope.msg("details.link-to", record.displayName);
            }

            // Identifier
            var rmaIdentifier = $html(dod5015["rma:identifier"]);
            if (rmaIdentifier === "")
            {
               rmaIdentifier = scope.msg("details.description.none");
            }

            switch (type)
            {
               /**
                * Record Series
                */
               case "record-series":
                  desc = '<h3 class="filename"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '" title="' + $html(fileName) + '">';
                  desc += $html(record.displayName) + '</a>' + title + '</h3>';

                  if (scope.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.series.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.created.by") + '</em> ' + $userProfile(record.createdByUser, record.createdBy) + '</span>';
                     desc += '<span class="item-simple"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn) + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + scope.msg("details.series.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + scope.msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail detail-last"><span class="item"><em>' + scope.msg("details.created.by") + '</em> ' + $userProfile(record.createdByUser, record.createdBy) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn) + '</span></div>';
                  }
                  break;
               
               /**
                * Record Category
                */
               case "record-category":
                  desc = '<h3 class="filename"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '" title="' + $html(fileName) + '">';
                  desc += $html(record.displayName) + '</a>' + title + '</h3>';

                  if (scope.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.category.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.created.by") + '</em> ' + $userProfile(record.createdByUser, record.createdBy) + '</span>';
                     desc += '<span class="item-simple"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn) + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + scope.msg("details.category.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     // Disposition Details
                     desc += '<div class="detail"><span class="item"><em>' + scope.msg("details.category.disposition-authority") + '</em> ' + $html(dod5015["rma:dispositionAuthority"]) + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + scope.msg("details.category.disposition-instructions") + '</em> ' + $html(dod5015["rma:dispositionInstructions"]) + '</span></div>';
                     // Vital Record Indicator
                     desc += '<div class="detail"><span class="item"><em>' + scope.msg("details.category.vital-record-indicator") + '</em> ' + scope.msg(dod5015["rma:vitalRecordIndicator"] ? "label.yes" : "label.no") + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail detail-last"><span class="item"><em>' + scope.msg("details.created.by") + '</em> ' + $userProfile(record.createdByUser, record.createdBy) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn) + '</span></div>';
                  }
                  break;
               
               /**
                * Record Folder
                */
               case "record-folder":
               case "metadata-stub-folder":
                  desc = '<h3 class="filename"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '" title="' + $html(fileName) + '">';
                  desc += $html(record.displayName) + '</a>' + title + '</h3>';

                  if (scope.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.folder.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.created.by") + '</em> ' + $userProfile(record.createdByUser, record.createdBy) + '</span>';
                     desc += '<span class="item-simple"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn) + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + scope.msg("details.folder.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + scope.msg("details.folder.vital-record-indicator") + '</em> ' + scope.msg(dod5015["rma:vitalRecordIndicator"] ? "label.yes" : "label.no") + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail detail-last"><span class="item"><em>' + scope.msg("details.created.by") + '</em> ' + $userProfile(record.createdByUser, record.createdBy) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn) + '</span></div>';
                  }
                  break;

               /**
                * Transfer Container
                */
               case "transfer-container":
                  var transferTitle = scope.msg("details.transfer-container.title", $html(record.displayName)),
                     filterObj =
                     {
                        filterId: "transfers",
                        filterData: nodeRef,
                        filterDisplay: transferTitle
                     };

                  // Transfer location if available
                  title = "";
                  if (dod5015["rma:transferLocation"])
                  {
                     title = '<span class="title">(' + $html(dod5015["rma:transferLocation"]) + ')</span>';
                  }
                  desc = '<h3 class="filename"><a class="filter-change" href="#" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '">';
                  desc += $html(transferTitle) + '</a>' + title + '</h3>';

                  if (scope.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.created.on") + '</em> ' + $date(record.createdOn) + '</span>';
                     desc += '<span class="item-simple"><em>' + scope.msg("details.by") + '</em> ' + $userProfile(record.modifiedByUser, record.mofieioedBy) + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + scope.msg("details.created.on") + '</em> ' + $date(record.createdOn) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.by") + '</em> ' + $userProfile(record.modifiedByUser, record.modifiedBy) + '</span></div>';
                     if (dod5015["rma:transferAccessionIndicator"])
                     {
                        desc += '<div class="detail">' + scope.msg("details.transfer-container.is-accession") + '</div';
                     }
                     else
                     {
                        desc += '<div class="detail">&nbsp;</div>';
                     }
                     desc += '<div class="detail detail-last">&nbsp;</div>';
                  }
                  break;

               /**
                * Hold Container
                */
               case "hold-container":
                  var holdDate = record.createdOn,
                     holdReason = $html(dod5015["rma:holdReason"]) || scope.msg("details.hold-container.reason.none"),
                     holdTitle = scope.msg("details.hold-container.title", $date(holdDate, "dd mmmm yyyy"), $date(holdDate, "HH:MM:ss")),
                     filterObj =
                     {
                        filterId: "holds",
                        filterData: nodeRef,
                        filterDisplay: holdTitle
                     };
                  
                  desc = '<h3 class="filename"><a class="filter-change" href="#" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '">' + $html(holdTitle) + '</a></h3>';

                  if (scope.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.hold-container.reason") + '</em> ' + holdReason + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + scope.msg("details.hold-container.reason") + '</em> ' + holdReason + '</span></div>';
                     desc += '<div class="detail">&nbsp;</div>';
                     desc += '<div class="detail detail-last">&nbsp;</div>';
                  }
                  break;

               /**
                * Record
                */
               case "record":
               case "record-nonelec":
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + scope.options.siteId + "/document-details?nodeRef=" + record.nodeRef;

                  desc = '<h3 class="filename"><span id="' + scope.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '" title="' + $html(fileName) + '">';
                  desc += $html(record.displayName) + '</a></span>' + title + '</h3>';
                  
                  if (scope.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.record.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + scope.msg("details.record.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + scope.msg("details.record.date-filed") + '</em> ' + Alfresco.util.formatDate($jsonDate(dod5015["rma:dateFiled"])) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.record.publication-date") + '</em> ' + Alfresco.util.formatDate($jsonDate(dod5015["rma:publicationDate"]), "defaultDateOnly") + '</span></div>';
                     desc += '<div class="detail detail-last">';
                     desc +=    '<span class="item"><em>' + scope.msg("details.record.originator") + '</em> ' + $html(dod5015["rma:originator"]) + '</span>';
                     desc +=    '<span class="item"><em>' + scope.msg("details.record.originating-organization") + '</em> ' + $html(dod5015["rma:originatingOrganization"]) + '</span>';
                     desc += '</div>';
                  }
                  break;

               /**
                * Metadata Stub
                */
               case "metadata-stub":
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + scope.options.siteId + "/document-details?nodeRef=" + record.nodeRef;

                  desc += '<h3 class="filename"><a href="' + docDetailsUrl + '" title="' + $html(fileName) + '">' + $html(record.displayName) + '</a>' + title + '</h3>';
                  
                  if (scope.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.record.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + scope.msg("details.record.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + scope.msg("details.record.date-filed") + '</em> ' + Alfresco.util.formatDate($jsonDate(dod5015["rma:dateFiled"])) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.record.publication-date") + '</em> ' + Alfresco.util.formatDate($jsonDate(dod5015["rma:publicationDate"]), "defaultDateOnly") + '</span></div>';
                     desc += '<div class="detail detail-last">';
                     desc +=    '<span class="item"><em>' + scope.msg("details.record.originator") + '</em> ' + $html(dod5015["rma:originator"]) + '</span>';
                     desc +=    '<span class="item"><em>' + scope.msg("details.record.originating-organization") + '</em> ' + $html(dod5015["rma:originatingOrganization"]) + '</span>';
                     desc += '</div>';
                  }
                  break;

               /**
                * Undeclared Record
                */
               case "undeclared-record":
               case "undeclared-record-nonelec":
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + scope.options.siteId + "/document-details?nodeRef=" + record.nodeRef;

                  desc = '<div class="undeclared-record-info">' + scope.msg("details.undeclared-record.info") + '</div>';
                  desc += '<h3 class="filename"><span id="' + scope.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '" title="' + $html(fileName) + '">';
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
                     desc += '<div class="detail detail-first"><span class="item"><em>' + scope.msg("details.record.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail">';
                     desc +=    '<span class="item"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn) + '</span>';
                     desc +=    '<span class="item"><em>' + scope.msg("details.modified.by") + '</em> ' + $userProfile(record.modifiedByUser, record.modifiedBy) + '</span>';
                     desc +=    '<span class="item"><em>' + scope.msg("details.size") + '</em> ' + Alfresco.util.formatFileSize(record.size) + '</span>';
                     desc += '</div>';
                     desc += '<div class="detail detail-last"><span class="item"><em>' + scope.msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                  }
                  break;


               /**
                * "Normal" Folder
                * Technically not supported in the Records Management world.
                */
               case "folder":
                  desc = '<h3 class="filename"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '" title="' + $html(fileName) + '">';
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
                     desc += '<div class="detail detail-first"><span class="item"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.modified.by") + '</em> ' + $userProfile(record.modifiedByUser, record.modifiedBy) + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + scope.msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                     desc += '</div><div class="detail detail-last">&nbsp;</div>';
                  }
                  break;
               
               /**
                * Documents and Links
                */
               default:
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + scope.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                
                  desc = '<h3 class="filename"><span id="' + scope.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '" title="' + $html(fileName) + '">';
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
                     desc += '<div class="detail detail-first">';
                     desc += '<span class="item"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.modified.by") + '</em> ' + $userProfile(record.modifiedByUser, record.modifiedBy) + '</span>';
                     desc += '<span class="item"><em>' + scope.msg("details.size") + '</em> ' + Alfresco.util.formatFileSize(record.size) + '</span>';
                     desc += '</div><div class="detail detail-last">';
                     desc += '<span class="item"><em>' + scope.msg("details.description") + '</em> ' + $links($html(description)) + '</span>';
                     desc += '</div>';
                  }
                  break;
            }
            elCell.innerHTML = desc;
         };
      },

      /**
       * Returns actions custom datacell formatter
       *
       * @method fnRenderCellActions
       */
      fnRenderCellActions: function RDL_fnRenderCellActions()
      {
         var scope = this;

         /**
          * Actions custom datacell formatter
          *
          * @method renderCellActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function RDL_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            if (scope.options.simpleView)
            {
               /**
                * Simple View
                */
                oColumn.width = 80;
            }
            else
            {
               /**
                * Detailed View
                */
                oColumn.width = 200;
            }
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.addClass(elCell.parentNode, oRecord.getData("type"));

            elCell.innerHTML = '<div id="' + scope.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
         };
      },

      /**
       * DataSource set-up and event registration
       *
       * @override
       * @method _setupDataSource
       */
      _setupDataSource: function RDL__setupDataSource()
      {
         var me = this;

         // DataSource definition
         this.widgets.dataSource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI + "slingshot/doclib/dod5015/doclist/",
         {
            responseType: YAHOO.util.DataSource.TYPE_JSON,
            responseSchema:
            {
               resultsList: "items",
               metaFields:
               {
                  paginationRecordOffset: "startIndex",
                  totalRecords: "totalRecords"
               }
            }
         });

         // Intercept data returned from data webscript to extract custom metadata
         this.widgets.dataSource.doBeforeCallback = function RDL_doBeforeCallback(oRequest, oFullResponse, oParsedResponse)
         {
            me.doclistMetadata = oFullResponse.metadata;

            // Fire event with parent metadata
            YAHOO.Bubbling.fire("doclistMetadata",
            {
               metadata: me.doclistMetadata
            });
            
            // Container userAccess event
            var permissions = me.doclistMetadata.parent.permissions;
            if (permissions && permissions.userAccess)
            {
               YAHOO.Bubbling.fire("userAccess",
               {
                  userAccess: permissions.userAccess
               });
            }
            
            return oParsedResponse;
         };
      },


      /**
       * Public functions
       *
       * Functions designed to be called form external sources
       */

      /**
       * Public function to select files by specified groups
       *
       * @method selectFiles
       * @param p_selectType {string} Can be one of the following:
       * <pre>
       * selectAll - all documents and folders
       * selectNone - deselect all
       * selectInvert - invert selection
       * selectRecords - select all records
       * selectFolders - select all folders
       * </pre>
       */
      selectFiles: function RDL_selectFiles(p_selectType)
      {
         var recordSet = this.widgets.dataTable.getRecordSet(),
            checks = YAHOO.util.Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl()),
            len = checks.length,
            record, i, fnCheck, typeMap;

         var typeMapping =
         {
            selectRecords:
            {
               "record": true,
               "record-nonelec": true
            },
            selectUndeclaredRecords:
            {
               "undeclared-record": true,
               "undeclared-record-nonelec": true
            },
            selectFolders:
            {
               "record-folder": true,
               "record-category": true,
               "record-series": true
            }
         };

         switch (p_selectType)
         {
            case "selectAll":
               fnCheck = function(assetType, isChecked)
               {
                  return true;
               };
               break;
            
            case "selectNone":
               fnCheck = function(assetType, isChecked)
               {
                  return false;
               };
               break;

            case "selectInvert":
               fnCheck = function(assetType, isChecked)
               {
                  return !isChecked;
               };
               break;

            case "selectRecords":
            case "selectUndeclaredRecords":
            case "selectFolders":
               typeMap = typeMapping[p_selectType];
               fnCheck = function(assetType, isChecked)
               {
                  if (typeof typeMap === "object")
                  {
                     return typeMap[assetType];
                  }
                  return assetType == typeMap;
               };
               break;

            default:
               fnCheck = function(assetType, isChecked)
               {
                  return isChecked;
               };
         }

         for (i = 0; i < len; i++)
         {
            record = recordSet.getRecord(i);
            this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = fnCheck(record.getData("type"), checks[i].checked);
         }
         
         YAHOO.Bubbling.fire("selectedFilesChanged");
      },

      /**
       * The urls to be used when creating links in the action cell
       *
       * @method getActionUrls
       * @param recordData {object} Object literal representing the node
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function RDL_getActionUrls(recordData)
      {
         var urlContextSite = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/",
            nodeRef = recordData.nodeRef,
            filePlan = this.doclistMetadata.filePlan.replace(":/", "");

         return (
         {
            downloadUrl: Alfresco.constants.PROXY_URI + recordData.contentUrl + "?a=true",
            documentDetailsUrl: urlContextSite + "document-details?nodeRef=" + nodeRef,
            folderDetailsUrl: urlContextSite + "folder-details?nodeRef=" + nodeRef,
            editMetadataUrl: urlContextSite + "edit-metadata?nodeRef=" + nodeRef,
            recordSeriesDetailsUrl: urlContextSite + "record-series-details?nodeRef=" + nodeRef,
            recordCategoryDetailsUrl: urlContextSite + "record-category-details?nodeRef=" + nodeRef,
            recordFolderDetailsUrl: urlContextSite + "record-folder-details?nodeRef=" + nodeRef,
            transfersZipUrl: Alfresco.constants.PROXY_URI + "api/node/" + filePlan + "/transfers/" + nodeRef.replace(":/", "").split("/")[2],
            managePermissionsUrl: urlContextSite + "rmpermissions?nodeRef=" + nodeRef + "&itemName=" + encodeURIComponent(recordData.displayName) + "&nodeType=" + recordData.type
         });
      }
   }, true);
})();
