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
 * DocumentList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentList
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
      $userProfile = Alfresco.util.userProfileLink,
      $siteURL = Alfresco.util.siteURL,
      $date = function $date(date, format) { return Alfresco.util.formatDate(Alfresco.util.fromISO8601(date), format); };

   /**
    * Preferences
    */
   var PREFERENCES_DOCLIST = "org.alfresco.share.documentList",
      PREF_SHOW_FOLDERS = PREFERENCES_DOCLIST + ".showFolders",
      PREF_SIMPLE_VIEW = PREFERENCES_DOCLIST + ".simpleView";

   /**
    * DocumentList constructor.
    * 
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.DocumentList} The new DocumentList instance
    * @constructor
    */
   Alfresco.DocumentList = function(htmlId)
   {
      Alfresco.DocumentList.superclass.constructor.call(this, "Alfresco.DocumentList", htmlId, ["button", "menu", "container", "datasource", "datatable", "paginator", "json", "history"]);

      // Initialise prototype properties
      this.currentPath = "";
      this.currentPage = 1;
      this.totalRecords = 0;
      this.showingMoreActions = false;
      this.state =
      {
         actionEditOfflineActive: false
      };
      this.currentFilter =
      {
         filterId: "path",
         filterData: ""
      };
      this.actions = {};
      this.selectedFiles = {};
      this.afterDocListUpdate = [];
      this.doclistMetadata = {};
      this.previewTooltips = [];
      this.dynamicControls = [];
      
      /**
       * Decoupled event listeners
       */
      // Specific event handlers
      YAHOO.Bubbling.on("activateDynamicControls", this.onActivateDynamicControls, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
      YAHOO.Bubbling.on("deactivateDynamicControls", this.onDeactivateDynamicControls, this);
      YAHOO.Bubbling.on("metadataRefresh", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("fileRenamed", this.onFileRenamed, this);
      YAHOO.Bubbling.on("changeFilter", this.onChangeFilter, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("folderCreated", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("folderRenamed", this.onFileRenamed, this);
      YAHOO.Bubbling.on("highlightFile", this.onHighlightFile, this);
      // File actions which may be part of a multi-file action set
      YAHOO.Bubbling.on("fileCopied", this.onFileAction, this);
      YAHOO.Bubbling.on("fileDeleted", this.onFileAction, this);
      YAHOO.Bubbling.on("fileMoved", this.onFileAction, this);
      YAHOO.Bubbling.on("filePermissionsUpdated", this.onFileAction, this);
      YAHOO.Bubbling.on("folderCopied", this.onFileAction, this);
      YAHOO.Bubbling.on("folderDeleted", this.onFileAction, this);
      YAHOO.Bubbling.on("folderMoved", this.onFileAction, this);
      YAHOO.Bubbling.on("folderPermissionsUpdated", this.onFileAction, this);
      // Multi-file actions
      YAHOO.Bubbling.on("filesCopied", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("filesDeleted", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("filesMoved", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("filesPermissionsUpdated", this.onDocListRefresh, this);

      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.DocumentList, Alfresco.component.Base);

   /**
    * Augment prototype with constants
    */
   YAHOO.lang.augmentObject(Alfresco.DocumentList,
   {
      MODE_SITE: 0,
      MODE_REPOSITORY: 1
   });

   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.DocumentList, Alfresco.doclib.Actions);

   /**
    * Custom field generator functions
    */

   /**
    * Generate "changeFilter" event mark-up suitable for element attribute.
    *
    * @method generateFilterMarkup
    * @param filter {object} Object literal containing new filter parameters
    * @return {string} Mark-up for use in node attribute
    */
   Alfresco.DocumentList.generateFilterMarkup = function DL_generateFilterMarkup(filter)
   {
      var filterObj = Alfresco.util.cleanBubblingObject(filter),
         markup = YAHOO.lang.substitute("{filterOwner}|{filterId}|{filterData}|{filterDisplay}", filterObj, function(p_key, p_value, p_meta)
         {
            return typeof p_value == "undefined" ? "" : window.escape(p_value);
         });
      
      return markup;
   };

   /**
    * Generate "changeFilter" event mark-up specifically for path changes
    *
    * @method generatePathMarkup
    * @param locn {object} Location object containing path and folder name to navigate to
    * @return {string} Mark-up for use in node attribute
    */
   Alfresco.DocumentList.generatePathMarkup = function DL_generatePathMarkup(locn)
   {
      return Alfresco.DocumentList.generateFilterMarkup(
      {
         filterId: "path",
         filterData: $combine(locn.path, locn.file)
      });
   };
   
   /**
    * Generate URL for a file- or folder-link that may be located within a different Site
    *
    * @method generateFileFolderLinkMarkup
    * @param oRecord {YAHOO.widget.Record} File record
    * @return {string} Mark-up for use in node attribute
    * <pre>
    *       Folders: Navigate into the folder (ajax)
    *       Documents: Navigate to the details page (page)
    *    Links: Same site (or Repository mode)
    *       Links to folders: Navigate into the folder (ajax)
    *       Links to documents: Navigate to the details page (page)
    *    Links: Different site
    *       Links to folders: Navigate into the site & folder (page)
    *       Links to documents: Navigate to the details page within the site (page)
    * </pre>
    */
   Alfresco.DocumentList.generateFileFolderLinkMarkup = function DL_generateFileFolderLinkMarkup(scope, oRecord)
   {
      var record = oRecord.getData(),
         html;

      if (record.isLink && scope.options.workingMode === Alfresco.doclib.MODE_SITE && record.location.site !== scope.options.siteId)
      {
         if (record.isFolder)
         {
            html = $siteURL("documentlibrary?path=" + encodeURIComponent(record.location.path),
            {
               site: record.location.site
            });
         }
         else
         {
            html = scope.getActionUrls(record, record.location.site).documentDetailsUrl;
         }
      }
      else
      {
         if (record.isFolder)
         {
            html = '#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(record.location);
         }
         else
         {
            html = scope.getActionUrls(record).documentDetailsUrl;
         }
      }

      return '<a href="' + html + '">';
   };

   /**
    * Generate URL to thumbnail image
    *
    * @method generateThumbnailUrl
    * @param oRecord {YAHOO.widget.Record} File record
    * @return {string} URL to thumbnail
    */
   Alfresco.DocumentList.generateThumbnailUrl = function DL_generateThumbnailUrl(oRecord)
   {
      var record = oRecord.getData(),
         nodeRef = new Alfresco.util.NodeRef(record.isLink ? record.linkedNodeRef : record.nodeRef);

      return Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/doclib?c=queue&ph=true";
   };

   /**
    * Generate favourite indicator
    *
    * @method generateFavourite
    * @param scope {object} DocumentLibrary instance
    * @param record {object} DataTable record
    * @return {string} HTML mark-up for favourite indicator
    */
   Alfresco.DocumentList.generateFavourite = function DL_generateFavourite(scope, record)
   {
      var id = scope.id + "-fav-" + record.getId(),
         type = record.getData("isFolder") ? "folder" : "document",
         isFavourite = record.getData("isFavourite");

      return '<a id="' + id + '" class="favourite-' + type + (isFavourite ? ' enabled' : '') + '" title="' + scope.msg("tip.favourite-" + type + "." + (isFavourite ? 'remove' : 'add')) + '">&nbsp;</a>';
   };

   
   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.DocumentList.prototype,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Flag indicating whether folders are visible or not.
          * 
          * @property showFolders
          * @type boolean
          */
         showFolders: false,

         /**
          * Flag indicating whether the list shows a detailed view or a simple one.
          * 
          * @property simpleView
          * @type boolean
          */
         simpleView: false,

         /**
          * Flag indicating whether pagination is available or not.
          * 
          * @property usePagination
          * @type boolean
          * @default false
          */
         usePagination: false,

         /**
          * Working mode: Site or Repository.
          * Affects how actions operate, e.g. actvities are not posted in Repository mode.
          * 
          * @property workingMode
          * @type number
          * @default Alfresco.doclib.MODE_SITE
          */
         workingMode: Alfresco.doclib.MODE_SITE,

         /**
          * Current siteId. Not used in Repository working mode.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * ContainerId representing root container. Not used in Repository working mode.
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * Current root node. Not used in Site working mode.
          * 
          * @property rootNode
          * @type string
          */
         rootNode: null,

         /**
          * Initial page to show on load (otherwise taken from URL hash).
          * 
          * @property initialPage
          * @type int
          */
         initialPage: 1,

         /**
          * Number of items per page
          * 
          * @property pageSize
          * @type int
          */
         pageSize: 50,

         /**
          * Initial filter to show on load.
          * 
          * @property initialFilter
          * @type object
          */
         initialFilter: {},
         
         /**
          * Delay time value for "More Actions" popup, in milliseconds
          *
          * @property actionsPopupTimeout
          * @type int
          * @default 500
          */
         actionsPopupTimeout: 500,
         
         /**
          * Delay before showing "loading" message for slow data requests
          *
          * @property loadingMessageDelay
          * @type int
          * @default 1000
          */
         loadingMessageDelay: 1000,

         /**
          * FileName to highlight on initial DataTable render.
          * 
          * @property highlightFile
          * @type string
          */
         highlightFile: null,
         
         /**
          * Holds IDs to register preview tooltips with.
          * 
          * @property previewTooltips
          * @type array
          */
         previewTooltips: null,
         
         /**
          * Number of multi-file uploads before grouping the Activity Post
          *
          * @property groupActivitiesAt
          * @type int
          * @default 5
          */
         groupActivitiesAt: 5,

         /**
          * Valid inline edit mimetypes
          * Currently allowed are plain text, HTML and XML only
          *
          * @property inlineEditMimetypes
          * @type object
          */
         inlineEditMimetypes:
         {
            "text/plain": true,
            "text/html": true,
            "text/xml": true
         },
         
         /**
          * SharePoint (Vti) Server Details
          *
          * @property vtiServer
          * @type object
          */
         vtiServer: {},
         
         /**
          * Replication URL Mapping details
          *
          * @property replicationUrlMapping
          * @type object
          */
         replicationUrlMapping: {}
      },

      /**
       * Keeps track of different states
       */
      state:
      {
         /**
          * True if an an edit offline ajax call is in process
          *
          * @property: actionEditOfflineActive
          * @type: boolean
          * @default: false
          */
         actionEditOfflineActive: false
      },

      /**
       * Current path being browsed.
       * 
       * @property currentPath
       * @type string
       */
      currentPath: null,

      /**
       * Current page being browsed.
       * 
       * @property currentPage
       * @type int
       * @default 1
       */
      currentPage: null,
      
      /**
       * Total number of records (documents + folders) in the currentPath.
       * 
       * @property totalRecords
       * @type int
       * @default 0
       */
      totalRecords: null,

      /**
       * Current filter to filter document list.
       * 
       * @property currentFilter
       * @type object
       */
      currentFilter: null,

      /**
       * FileUpload module instance.
       * 
       * @property fileUpload
       * @type Alfresco.FileUpload
       */
      fileUpload: null,

      /**
       * Object container for storing action markup elements.
       * 
       * @property actions
       * @type object
       */
      actions: null,

      /**
       * Object literal of selected states for visible files (indexed by nodeRef).
       * 
       * @property selectedFiles
       * @type object
       */
      selectedFiles: null,

      /**
       * Current actions menu being shown
       * 
       * @property currentActionsMenu
       * @type object
       * @default null
       */
      currentActionsMenu: null,

      /**
       * Whether "More Actions" pop-up is currently visible.
       * 
       * @property showingMoreActions
       * @type boolean
       * @default false
       */
      showingMoreActions: null,

      /**
       * Deferred actions menu element when showing "More Actions" pop-up.
       * 
       * @property deferredActionsMenu
       * @type object
       * @default null
       */
      deferredActionsMenu: null,

      /**
       * Deferred function calls for after a document list update
       *
       * @property afterDocListUpdate
       * @type array
       */
      afterDocListUpdate: null,

      /**
       * Metadata returned by doclist data webscript
       *
       * @property doclistMetadata
       * @type object
       * @default null
       */
      doclistMetadata: null,

      /**
       * Dynamic controls that take part in the deactivateDynamicControls event
       * 
       * @property dynamicControls
       * @type array
       */
      dynamicControls: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function DL_onReady()
      {
         // Reference to self used by inline functions
         var me = this;

         // Set-up YUI History Managers
         this._setupHistoryManagers();

         // Hide/Show Folders button
         this.widgets.showFolders = Alfresco.util.createYUIButton(this, "showFolders-button", this.onShowFolders);
         if (this.widgets.showFolders !== null)
         {
            this.widgets.showFolders.set("label", this.msg(this.options.showFolders ? "button.folders.hide" : "button.folders.show"));
            this.dynamicControls.push(this.widgets.showFolders);
         }

         // Detailed/Simple List button
         this.widgets.simpleDetailed = new YAHOO.widget.ButtonGroup(this.id + "-simpleDetailed");
         if (this.widgets.simpleDetailed !== null)
         {
            this.widgets.simpleDetailed.check(this.options.simpleView ? 0 : 1);
            this.widgets.simpleDetailed.on("checkedButtonChange", this.onSimpleDetailed, this.widgets.simpleDetailed, this);
            Dom.addClass(this.id + "-simpleView", "simple-view");
            Dom.addClass(this.id + "-detailedView", "detailed-view");
            this.dynamicControls.push(this.widgets.simpleDetailed);
         }

         // File Select menu button
         this.widgets.fileSelect = Alfresco.util.createYUIButton(this, "fileSelect-button", this.onFileSelect,
         {
            type: "menu", 
            menu: "fileSelect-menu"
         });
         if (this.widgets.fileSelect !== null)
         {
            this.dynamicControls.push(this.widgets.fileSelect);
         }

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();
         
         // DataSource set-up and event registration
         this._setupDataSource();
         
         // DataTable set-up and event registration
         this._setupDataTable();

         // Tooltip for thumbnail in Simple View
         this.widgets.previewTooltip = new YAHOO.widget.Tooltip(this.id + "-previewTooltip",
         {
            width: "108px"
         });
         this.widgets.previewTooltip.contextTriggerEvent.subscribe(function(type, args)
         {
            var context = args[0],
               record = me.widgets.dataTable.getRecord(context.id);
            this.cfg.setProperty("text", '<img src="' + Alfresco.DocumentList.generateThumbnailUrl(record) + '" />');
         });
         
         // Hook action events
         var fnActionHandler = function DL_fnActionHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               if (typeof me[owner.className] == "function")
               {
                  args[1].stop = true;
                  var asset = me.widgets.dataTable.getRecord(args[1].target.offsetParent).getData();
                  me[owner.className].call(me, asset, owner);
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);
         YAHOO.Bubbling.addDefaultAction("show-more", fnActionHandler);
         
         // Hook favourite document events
         var fnFavouriteDocumentHandler = function DL_fnFavouriteDocumentHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               me.onFavouriteDocument.call(me, args[1].target.offsetParent, owner);
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("favourite-document", fnFavouriteDocumentHandler);

         // Hook favourite folder events
         var fnFavouriteFolderHandler = function DL_fnFavouriteFolderHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               me.onFavouriteFolder.call(me, args[1].target.offsetParent, owner);
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("favourite-folder", fnFavouriteFolderHandler);
         
         // Hook filter change events
         var fnChangeFilterHandler = function DL_fnChangeFilterHandler(layer, args)
         {
            var owner = args[1].anchor;
            if (owner !== null)
            {
               var filter = owner.rel,
                  filters,
                  filterObj = {};
               if (filter && filter !== "")
               {
                  args[1].stop = true;
                  filters = filter.split("|");
                  filterObj =
                  {
                     filterOwner: window.unescape(filters[0] || ""),
                     filterId: window.unescape(filters[1] || ""),
                     filterData: window.unescape(filters[2] || ""),
                     filterDisplay: window.unescape(filters[3] || "")
                  };
                  Alfresco.logger.debug("DL_fnChangeFilterHandler", "changeFilter =>", filterObj);
                  YAHOO.Bubbling.fire("changeFilter", filterObj);
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("filter-change", fnChangeFilterHandler);

         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions(this.options.workingMode);

         // Continue only when History Manager fires its onReady event
         YAHOO.util.History.onReady(this.onHistoryManagerReady, this, true);

         // Initialize the browser history management library
         try
         {
             YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
         }
         catch(e)
         {
            /*
             * The only exception that gets thrown here is when the browser is
             * not supported (Opera, or not A-grade)
             */
            Alfresco.logger.error(this.name + ": Couldn't initialize HistoryManager.", e);
            this.onHistoryManagerReady();
         }
      },


      /**
       * DataTable Cell Renderers
       */

      /**
       * Returns selector custom datacell formatter
       *
       * @method fnRenderCellSelected
       */
      fnRenderCellSelected: function DL_fnRenderCellSelected()
      {
         var scope = this;
         
         /**
          * Selector custom datacell formatter
          *
          * @method renderCellSelected
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function DL_renderCellSelected(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            
            elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="checkbox" name="fileChecked" value="'+ oData + '"' + (scope.selectedFiles[oData] ? ' checked="checked">' : '>');
         };
      },

      /**
       * Returns status custom datacell formatter
       *
       * @method fnRenderCellStatus
       */
      fnRenderCellStatus: function DL_fnRenderCellStatus()
      {
         var scope = this;
         
         /**
          * Status custom datacell formatter
          *
          * @method renderCellStatus
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         return function DL_renderCellStatus(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var record = oRecord.getData(),
               dataStatus = record.status,
               status,
               tip = "",
               desc = "";

            if (dataStatus.length > 0)
            {
               var statuses = dataStatus.split(",").sort(),
                  s, SPACE = " ", meta,
                  i18n, i18nMeta;

               for (var i = 0, j = statuses.length; i < j; i++)
               {
                  status = statuses[i];
                  meta = "";
                  s = status.indexOf(SPACE);
                  if (s > -1)
                  {
                     meta = status.substring(s + 1);
                     status = status.substring(0, s);
                  }

                  i18n = "tip." + status;
                  i18nMeta = i18n + ".meta";
                  if (meta && scope.msg(i18nMeta) !== i18nMeta)
                  {
                     i18n = i18nMeta;
                  }
                  tip = Alfresco.util.message(i18n, scope.name, meta.split("|")); // Note: deliberate bypass of scope.msg() function
                  desc += '<div class="status"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/' + status + '-indicator-16.png" title="' + tip + '" alt="' + status + '" /></div>';
               }
            }

            // In workflow status
            status = record.activeWorkflows;
            if (status && status !== "")
            {
               tip = scope.msg("tip.active-workflow", status.split(",").length);
               desc += '<div class="status"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/workflow-indicator-16.png" title="' + tip + '" alt="' + tip + '" /></div>';
            }

            elCell.innerHTML = desc;
         };
      },

      /**
       * Returns thumbnail custom datacell formatter
       *
       * @method fnRenderCellThumbnail
       */
      fnRenderCellThumbnail: function DL_fnRenderCellThumbnail()
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
         return function DL_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            var record = oRecord.getData(),
               name = record.fileName,
               title = record.title,
               type = record.type,
               isLink = record.isLink,
               extn = name.substring(name.lastIndexOf("."));

            if (scope.options.simpleView)
            {
               /**
                * Simple View
                */
               oColumn.width = 40;
               Dom.setStyle(elCell, "width", oColumn.width + "px");
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               if (type == "folder")
               {
                  elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, oRecord) + '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/folder-32.png" /></a>';
               }
               else
               {
                  var id = scope.id + '-preview-' + oRecord.getId();
                  elCell.innerHTML = '<span id="' + id + '" class="icon32">' + (isLink ? '<span class="link"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, oRecord) + '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(name) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';

                  // Preview tooltip
                  scope.previewTooltips.push(id);
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

               if (type == "folder")
               {
                  elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, oRecord) + '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/documentlibrary/images/folder-48.png" /></a>';
               }
               else
               {
                  elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, oRecord) + '<img src="' + Alfresco.DocumentList.generateThumbnailUrl(oRecord) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
               }
            }
         };
      },

      /**
       * Returns description/detail custom datacell formatter
       *
       * @method fnRenderCellDescription
       */
      fnRenderCellDescription: function DL_fnRenderCellDescription()
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
         return function DL_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var desc = "", tags, tag, i, j;
            var record = oRecord.getData(),
               type = record.type,
               isLink = record.isLink,
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
               desc += '<h3 class="filename">' + Alfresco.DocumentList.generateFavourite(scope, oRecord) + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, oRecord);
               desc += $html(record.displayName) + '</a>' + title + '</h3>';

               if (scope.options.simpleView)
               {
                  /**
                   * Simple View
                   */
                  desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn, scope.msg("date-format.longDate")) + '</span>';
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
                
               desc += '<h3 class="filename">' + Alfresco.DocumentList.generateFavourite(scope, oRecord) + '<span id="' + scope.id + '-preview-' + oRecord.getId() + '">'+ Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, oRecord);
               desc += $html(record.displayName) + '</a></span>' + title + '</h3>';
               
               if (scope.options.simpleView)
               {
                  /**
                   * Simple View
                   */
                  desc += '<div class="detail"><span class="item-simple"><em>' + scope.msg("details.modified.on") + '</em> ' + $date(record.modifiedOn, scope.msg("date-format.longDate")) + '</span>';
                  desc += '<span class="item-simple"><em>' + scope.msg("details.by") + '</em> ' + $userProfile(record.modifiedByUser, record.modifiedBy) + '</span></div>';
               }
               else
               {
                  /**
                   * Detailed View
                   */
                  if (record.custom && record.custom.isWorkingCopy)
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
       * Returns actions custom datacell formatter
       *
       * @method fnRenderCellActions
       */
      fnRenderCellActions: function DL_fnRenderCellActions()
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
         return function DL_renderCellActions(elCell, oRecord, oColumn, oData)
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

            var record = oRecord.getData();

            /**
             * Configure the Online Edit URL if enabled for this mimetype
             */
            if (scope.doclistMetadata.onlineEditing && (record.mimetype in scope.onlineEditMimetypes))
            {
               var loc = record.location;
               oRecord.setData("onlineEditUrl", window.location.protocol.replace(/https/i, "http") + "//" + scope.options.vtiServer.host + ":" + scope.options.vtiServer.port + "/" + $combine("alfresco", loc.site, loc.container, loc.path, loc.file));
            }
         };
      },

      /**
       * History Manager set-up and event registration
       *
       * @method _setupHistoryManagers
       */
      _setupHistoryManagers: function DL__setupHistoryManagers()
      {
         /**
          * YUI History - filter
          */
         var bookmarkedFilter = YAHOO.util.History.getBookmarkedState("filter");
         bookmarkedFilter = bookmarkedFilter || "path|/";

         try
         {
            while (bookmarkedFilter != (bookmarkedFilter = decodeURIComponent(bookmarkedFilter))){}
         }
         catch (e)
         {
            // Catch "malformed URI sequence" exception
         }
         
         var fnDecodeBookmarkedFilter = function DL_fnDecodeBookmarkedFilter(strFilter)
         {
            var filters = strFilter.split("|"),
               filterObj =
               {
                  filterId: window.unescape(filters[0] || ""),
                  filterData: window.unescape(filters[1] || "")
               };
            
            filterObj.filterOwner = Alfresco.util.FilterManager.getOwner(filterObj.filterId);
            return filterObj;
         };
         
         this.options.initialFilter = fnDecodeBookmarkedFilter(bookmarkedFilter);

         // Register History Manager filter update callback
         YAHOO.util.History.register("filter", bookmarkedFilter, function DL_onHistoryManagerFilterChanged(newFilter)
         {
            Alfresco.logger.debug("HistoryManager: filter changed:" + newFilter);
            // Firefox fix
            if (YAHOO.env.ua.gecko > 0)
            {
               newFilter = window.unescape(newFilter);
               Alfresco.logger.debug("HistoryManager: filter (after Firefox fix):" + newFilter);
            }
            
            this._updateDocList.call(this,
            {
               filter: fnDecodeBookmarkedFilter(newFilter),
               page: this.currentPage
            });
         }, null, this);


         /**
          * YUI History - page
          */
         var handlePagination = function DL_handlePagination(state, me)
         {
            me.widgets.paginator.setState(state);
            YAHOO.util.History.navigate("page", String(state.page));
         };

         if (this.options.usePagination)
         {
            var bookmarkedPage = YAHOO.util.History.getBookmarkedState("page") || "1";
            while (bookmarkedPage != (bookmarkedPage = decodeURIComponent(bookmarkedPage))){}
            this.currentPage = parseInt(bookmarkedPage || this.options.initialPage, 10);

            // Register History Manager page update callback
            YAHOO.util.History.register("page", bookmarkedPage, function DL_onHistoryManagerPageChanged(newPage)
            {
               Alfresco.logger.debug("HistoryManager: page changed:" + newPage);
               // Update the DocList
               if (this.currentPage != newPage)
               {
                  this._updateDocList.call(this,
                  {
                     page: newPage
                  });
               }
               else
               {
                  Alfresco.logger.debug("...page changed event ignored.");
               }
            }, null, this);

            // YUI Paginator definition
            this.widgets.paginator = new YAHOO.widget.Paginator(
            {
               containers: [this.id + "-paginator", this.id + "-paginatorBottom"],
               rowsPerPage: this.options.pageSize,
               initialPage: this.currentPage,
               template: this.msg("pagination.template"),
               pageReportTemplate: this.msg("pagination.template.page-report"),
               previousPageLinkLabel: this.msg("pagination.previousPageLinkLabel"),
               nextPageLinkLabel: this.msg("pagination.nextPageLinkLabel")
            });
            
            this.widgets.paginator.subscribe("changeRequest", handlePagination, this);
            
            // Display the bottom paginator bar
            Dom.setStyle(this.id + "-doclistBarBottom", "display", "block");
         }
      },
      
      /**
       * DataSource set-up and event registration
       *
       * @method _setupDataSource
       * @protected
       */
      _setupDataSource: function DL__setupDataSource()
      {
         var me = this;
         
         // DataSource definition
         this.widgets.dataSource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/",
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
         this.widgets.dataSource.doBeforeCallback = function DL_doBeforeCallback(oRequest, oFullResponse, oParsedResponse)
         {
            me.doclistMetadata = oFullResponse.metadata;
            
            // Fire event with parent metadata
            YAHOO.Bubbling.fire("doclistMetadata",
            {
               metadata: me.doclistMetadata
            });
            
            // Reset onlineEdit flag if correct conditions not met
            if ((YAHOO.env.ua.ie === 0) || (me.options.vtiServer && typeof me.options.vtiServer.port != "number"))
            {
               me.doclistMetadata.onlineEditing = false;
            }
            
            // Container userAccess event
            var permissions = me.doclistMetadata.parent.permissions;
            if (permissions && permissions.userAccess)
            {
               YAHOO.Bubbling.fire("userAccess",
               {
                  userAccess: permissions.userAccess
               });
            }
            
            // Update "Empty" message to reflect subfolders are available but hidden?
            var itemCounts = me.doclistMetadata.itemCounts;
            if (itemCounts.documents === 0 && itemCounts.folders > 0 && !me.options.showFolders)
            {
               var showFoldersLink = '<span class="show-folders-link theme-color-1" onclick="Alfresco.util.ComponentManager.get(\'' + me.id + '\').onShowFolders();">' + me.msg("message.empty.subfolders.link") + '</span>';
               me.widgets.dataTable.set("MSG_EMPTY", me.msg("message.empty.subfolders", showFoldersLink, itemCounts.folders));
            }
            
            return oParsedResponse;
         };
      },
      
      /**
       * DataTable set-up and event registration
       *
       * @method _setupDataTable
       * @protected
       */
      _setupDataTable: function DL__setupDataTable()
      {
         var me = this;

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "nodeRef", label: "Select", sortable: false, formatter: this.fnRenderCellSelected(), width: 16 },
            { key: "status", label: "Status", sortable: false, formatter: this.fnRenderCellStatus(), width: 16 },
            { key: "thumbnail", label: "Preview", sortable: false, formatter: this.fnRenderCellThumbnail(), width: 100 },
            { key: "fileName", label: "Description", sortable: false, formatter: this.fnRenderCellDescription() },
            { key: "actions", label: "Actions", sortable: false, formatter: this.fnRenderCellActions(), width: 200 }
         ];
         
         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: this.options.usePagination ? 16 : Alfresco.util.RENDERLOOPSIZE,
            initialLoad: false,
            dynamicData: true,
            MSG_EMPTY: this.msg("message.loading")
         });

         // Update totalRecords on the fly with value from server
         this.widgets.dataTable.handleDataReturnPayload = function DL_handleDataReturnPayload(oRequest, oResponse, oPayload)
         {
            me.totalRecords = oResponse.meta.totalRecords;
            return oResponse.meta;
         };

         // Custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);

         // Hook tableMsgShowEvent to clear out fixed-pixel width on <table> element (breaks resizer)
         this.widgets.dataTable.subscribe("tableMsgShowEvent", function(oArgs)
         {
            // NOTE: Scope needs to be DataTable
            this._elMsgTbody.parentNode.style.width = "";
         });
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function DL_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  me.widgets.dataTable.set("MSG_ERROR", response.message);
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors(me.widgets.dataTable);
               }
            }
            
            // We don't get an renderEvent for an empty recordSet, but we'd like one anyway
            if (oResponse.results.length === 0)
            {
               this.fireEvent("renderEvent",
               {
                  type: "renderEvent"
               });
            }
            
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         };

         // File checked handler
         this.widgets.dataTable.subscribe("checkboxClickEvent", function(e)
         { 
            var id = e.target.value; 
            this.selectedFiles[id] = e.target.checked;
            YAHOO.Bubbling.fire("selectedFilesChanged");
         }, this, true);
         
         // Rendering complete event handler
         this.widgets.dataTable.subscribe("renderEvent", function()
         {
            Alfresco.logger.debug("DataTable renderEvent");
            
            // IE6 fix for long filename rendering issue
            if (0 < YAHOO.env.ua.ie && YAHOO.env.ua.ie < 7)
            {
               var ie6fix = this.widgets.dataTable.getTableEl().parentNode;
               ie6fix.className = ie6fix.className;
            }
            
            // Update the paginator if it's been created
            if (this.widgets.paginator)
            {
               Alfresco.logger.debug("Setting paginator state: page=" + this.currentPage + ", totalRecords=" + this.totalRecords);

               this.widgets.paginator.setState(
               {
                  page: this.currentPage,
                  totalRecords: this.totalRecords
               });
               this.widgets.paginator.render();
            }
            
            // Need to highlight a file now the data is available?
            if (this.options.highlightFile)
            {
               YAHOO.Bubbling.fire("highlightFile",
               {
                  fileName: this.options.highlightFile
               });
            }
            else
            {
               // Scroll up (only) to the top of the documents
               var yPos = Dom.getY(this.id + "-documents"),
                  yScroll = YAHOO.env.ua.ie > 0 ? ((document.compatMode && document.compatMode != "BackCompat") ? document.documentElement : document.body).scrollTop : window.scrollY;
               
               if (yScroll > yPos)
               {
                  window.scrollTo(0, yPos);
               }
            }

            // Deferred functions specified?
            for (var i = 0, j = this.afterDocListUpdate.length; i < j; i++)
            {
               this.afterDocListUpdate[i].call(this);
            }
            this.afterDocListUpdate = [];
            
            // Register preview tooltips
            this.widgets.previewTooltip.cfg.setProperty("context", this.previewTooltips);
            
            this.widgets.dataTable.set("renderLoopSize", this.options.usePagination ? 16 : Alfresco.util.RENDERLOOPSIZE);
            
         }, this, true);
         
         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);
      },
      
      /**
       * Fired by YUI when History Manager is initialised and available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onHistoryManagerReady
       */
      onHistoryManagerReady: function DL_onHistoryManagerReady()
      {
         // Fire changeFilter event for first-time population
         Alfresco.logger.debug("DL_onHistoryManagerReady", "changeFilter =>", this.options.initialFilter);
         YAHOO.Bubbling.fire("changeFilter", YAHOO.lang.merge(
         {
            doclistFirstTimeNav: true
         }, this.options.initialFilter));
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      /**
       * Public functions
       *
       * Functions designed to be called form external sources
       */

      /**
       * Public function to get array of selected files
       *
       * @method getSelectedFiles
       * @return {Array} Currently selected files
       */
      getSelectedFiles: function DL_getSelectedFiles()
      {
         var files = [],
            recordSet = this.widgets.dataTable.getRecordSet(),
            record;
         
         for (var i = 0, j = recordSet.getLength(); i < j; i++)
         {
            record = recordSet.getRecord(i);
            if (this.selectedFiles[record.getData("nodeRef")])
            {
               files.push(record.getData());
            }
         }
         
         return files;
      },
      
      /**
       * Public function to select files by specified groups
       *
       * @method selectFiles
       * @param p_selectType {string} Can be one of the following:
       * <pre>
       * selectAll - all documents and folders
       * selectNone - deselect all
       * selectInvert - invert selection
       * selectDocuments - select all documents
       * selectFolders - select all folders
       * </pre>
       */
      selectFiles: function DL_selectFiles(p_selectType)
      {
         var recordSet = this.widgets.dataTable.getRecordSet(),
            checks = YAHOO.util.Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl()),
            len = checks.length,
            record, i, fnCheck, typeMap;

         var typeMapping =
         {
            selectDocuments:
            {
               "document": true
            },
            selectFolders:
            {
               "folder": true
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

            case "selectDocuments":
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
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Show/Hide folders button click handler
       *
       * @method onShowFolders
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onShowFolders: function DL_onShowFolders(e, p_obj)
      {
         this.options.showFolders = !this.options.showFolders;
         this.widgets.showFolders.set("label", this.msg(this.options.showFolders ? "button.folders.hide" : "button.folders.show"));
         this.services.preferences.set(PREF_SHOW_FOLDERS, this.options.showFolders);
         YAHOO.Bubbling.fire("metadataRefresh");
         if (e)
         {
            Event.preventDefault(e);
         }
      },
      
      /**
       * Show/Hide detailed list button click handler
       *
       * @method onSimpleView
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSimpleView: function DL_onSimpleView(e, p_obj)
      {
         this.options.simpleView = !this.options.simpleView;
         p_obj.set("label", this.msg(this.options.simpleView ? "button.view.detailed" : "button.view.simple"));

         this.services.preferences.set(PREF_SIMPLE_VIEW, this.options.simpleView);

         YAHOO.Bubbling.fire("metadataRefresh");
         Event.preventDefault(e);
      },

      /**
       * Show/Hide detailed list buttongroup click handler
       *
       * @method onSimpleDetailed
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSimpleDetailed: function DL_onSimpleDetailed(e, p_obj)
      {
         this.options.simpleView = e.newValue.index === 0;
         this.services.preferences.set(PREF_SIMPLE_VIEW, this.options.simpleView);
         YAHOO.Bubbling.fire("metadataRefresh");

         Event.preventDefault(e);
      },
      
      /**
       * Multi-file select button click handler
       *
       * @method onFileSelect
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onFileSelect: function DL_onFileSelect(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0],
            eventTarget = aArgs[1];

         // Select based upon the className of the clicked item
         this.selectFiles(Alfresco.util.findEventClass(eventTarget));
         Event.preventDefault(domEvent);
      },

      /**
       * Custom event handler to highlight row.
       *
       * @method onEventHighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventHighlightRow: function DL_onEventHighlightRow(oArgs)
      {
         // Call through to get the row highlighted by YUI
         this.widgets.dataTable.onEventHighlightRow.call(this.widgets.dataTable, oArgs);

         // elActions is the element id of the active table cell where we'll inject the actions
         var elActions = Dom.get(this.id + "-actions-" + oArgs.target.id);

         // Inject the correct action elements into the actionsId element
         if (elActions && elActions.firstChild === null)
         {
            // Retrieve the actionSet for this asset
            var record = this.widgets.dataTable.getRecord(oArgs.target.id),
               actionSet = record.getData("actionSet");
            
            // Clone the actionSet template node from the DOM
            var clone = Dom.get(this.id + "-actionSet-" + actionSet).cloneNode(true);
            
            // Token replacement
            clone.innerHTML = YAHOO.lang.substitute(window.unescape(clone.innerHTML), this.getActionUrls(record.getData()));

            // Generate an id
            clone.id = elActions.id + "_a";
            
            // Simple or detailed view
            Dom.addClass(clone, this.options.simpleView ? "simple" : "detailed");
            
            // Trim the items in the clone depending on the user's access
            var userAccess = record.getData("permissions").userAccess,
               actionLabels = record.getData("actionLabels") || {};
            
            // Inject special-case permissions
            if (record.getData("mimetype") in this.options.inlineEditMimetypes)
            {
               userAccess["inline-edit"] = true;
            }
            if (record.getData("onlineEditUrl"))
            {
               userAccess["online-edit"] = true;
            }
            if (this.options.repositoryUrl)
            {
               userAccess.repository = true;
            }
            userAccess.portlet = Alfresco.constants.PORTLET;
            
            // Inject the current filterId to allow filter-scoped actions
            userAccess["filter-" + this.currentFilter.filterId] = true;
            
            // Remove any actions the user doesn't have permission for
            var actions = YAHOO.util.Selector.query("div", clone),
               action, aTag, spanTag, actionPermissions, aP, i, ii, j, jj;
            for (i = 0, ii = actions.length; i < ii; i++)
            {
               action = actions[i];
               aTag = action.firstChild;
               spanTag = aTag.firstChild;
               if (spanTag && actionLabels[action.className])
               {
                  spanTag.innerHTML = $html(actionLabels[action.className]);
               }
               
               if (aTag.rel !== "")
               {
                  actionPermissions = aTag.rel.split(",");
                  for (j = 0, jj = actionPermissions.length; j < jj; j++)
                  {
                     aP = actionPermissions[j];
                     // Support "negative" permissions
                     if ((aP.charAt(0) == "~") ? !!userAccess[aP.substring(1)] : !userAccess[aP])
                     {
                        clone.removeChild(action);
                        break;
                     }
                  }
               }
            }
            
            // Need the "More >" container?
            var splitAt = record.getData("type") == "folder" ? 2 : 3;
            actions = YAHOO.util.Selector.query("div", clone);
            if (actions.length > splitAt + (this.options.simpleView ? 0 : 1))
            {
               var moreContainer = Dom.get(this.id + "-moreActions").cloneNode(true);
               var containerDivs = YAHOO.util.Selector.query("div", moreContainer);
               // Insert the two necessary DIVs before the third action item
               Dom.insertBefore(containerDivs[0], actions[splitAt]);
               Dom.insertBefore(containerDivs[1], actions[splitAt]);
               // Now make action items three onwards children of the 2nd DIV
               var index, moreActions = actions.slice(splitAt);
               for (index in moreActions)
               {
                  if (moreActions.hasOwnProperty(index))
                  {
                     containerDivs[1].appendChild(moreActions[index]);
                  }
               }
            }
            
            elActions.appendChild(clone);
         }
         
         if (this.showingMoreActions)
         {
            this.deferredActionsMenu = elActions;
         }
         else if (!Dom.hasClass(document.body, "masked"))
         {
            this.currentActionsMenu = elActions;
            // Show the actions
            Dom.removeClass(elActions, "hidden");
            this.deferredActionsMenu = null;
         }
      },

      /**
       * The urls to be used when creating links in the action cell
       *
       * @method getActionUrls
       * @param recordData {object} Object literal representing the node
       * @param siteId {string} Optional siteId override for site-based locations
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function DL_getActionUrls(recordData, siteId)
      {
         var nodeRef = recordData.isLink ? recordData.linkedNodeRef : recordData.nodeRef,
            nodeRefUri = new Alfresco.util.NodeRef(nodeRef).uri,
            contentUrl = recordData.contentUrl,
            custom = recordData.custom,
            siteObj = YAHOO.lang.isString(siteId) ? { site: siteId } : null,
            fnPageURL = Alfresco.util.bind(function(page)
            {
               return Alfresco.util.siteURL(page, siteObj);
            }, this);

         return (
         {
            downloadUrl: Alfresco.constants.PROXY_URI + contentUrl + "?a=true",
            viewUrl:  Alfresco.constants.PROXY_URI + contentUrl + "\" target=\"_blank",
            documentDetailsUrl: fnPageURL("document-details?nodeRef=" + nodeRef),
            folderDetailsUrl: fnPageURL("folder-details?nodeRef=" + nodeRef),
            folderRulesUrl: fnPageURL("folder-rules?nodeRef=" + nodeRef),
            editMetadataUrl: fnPageURL("edit-metadata?nodeRef=" + nodeRef),
            inlineEditUrl: fnPageURL("inline-edit?nodeRef=" + nodeRef),
            managePermissionsUrl: fnPageURL("manage-permissions?nodeRef=" + nodeRef),
            workingCopyUrl: fnPageURL("document-details?nodeRef=" + (custom.workingCopyNode || nodeRef)),
            viewGoogleDocUrl: custom.googleDocUrl + "\" target=\"_blank",
            originalUrl: fnPageURL("document-details?nodeRef=" + (custom.workingCopyOriginal || nodeRef)),
            explorerViewUrl: $combine(this.options.repositoryUrl, "/n/showSpaceDetails/", nodeRefUri) + "\" target=\"_blank",
            sourceRepositoryUrl: this.viewInSourceRepositoryURL(recordData) + "\" target=\"_blank"
         });
      },

      /**
       * Custom event handler to unhighlight row.
       *
       * @method onEventUnhighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventUnhighlightRow: function DL_onEventUnhighlightRow(oArgs)
      {
         // Call through to get the row unhighlighted by YUI
         this.widgets.dataTable.onEventUnhighlightRow.call(this.widgets.dataTable, oArgs);

         var elActions = Dom.get(this.id + "-actions-" + (oArgs.target.id));

         // Don't hide unless the More Actions drop-down is showing, or a dialog mask is present
         if (elActions && !this.showingMoreActions || Dom.hasClass(document.body, "masked"))
         {
            // Just hide the action links, rather than removing them from the DOM
            Dom.addClass(elActions, "hidden");
            this.deferredActionsMenu = null;
         }
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR ACTIONS
       * Disconnected event handlers for action event notification
       */

      /**
       * Show more actions pop-up.
       *
       * @method onActionShowMore
       * @param asset {object} Unused
       * @param elMore {element} DOM Element of "More Actions" link
       */
      onActionShowMore: function DL_onActionShowMore(asset, elMore)
      {
         var me = this;
         
         // Fix "More Actions" hover style
         Dom.addClass(elMore.firstChild, "highlighted");
         
         // Get the pop-up div, sibling of the "More Actions" link
         var elMoreActions = Dom.getNextSibling(elMore);
         Dom.removeClass(elMoreActions, "hidden");
         me.showingMoreActions = true;
         
         // Hide pop-up timer function
         var fnHidePopup = function DL_oASM_fnHidePopup()
         {
            // Need to rely on the "elMoreActions" enclosed variable, as MSIE doesn't support
            // parameter passing for timer functions.
            Event.removeListener(elMoreActions, "mouseover");
            Event.removeListener(elMoreActions, "mouseout");
            Dom.removeClass(elMore.firstChild, "highlighted");
            Dom.addClass(elMoreActions, "hidden");
            me.showingMoreActions = false;
            if (me.deferredActionsMenu !== null)
            {
               Dom.addClass(me.currentActionsMenu, "hidden");
               me.currentActionsMenu = me.deferredActionsMenu;
               me.deferredActionsMenu = null;
               Dom.removeClass(me.currentActionsMenu, "hidden");
            }
         };

         // Initial after-click hide timer - 5x the mouseOut timer delay
         if (elMoreActions.hideTimerId)
         {
            window.clearTimeout(elMoreActions.hideTimerId);
         }
         elMoreActions.hideTimerId = window.setTimeout(fnHidePopup, me.options.actionsPopupTimeout * 5);
         
         // Mouse over handler
         var onMouseOver = function DLSM_onMouseOver(e, obj)
         {
            // Clear any existing hide timer
            if (obj.hideTimerId)
            {
               window.clearTimeout(obj.hideTimerId);
               obj.hideTimerId = null;
            }
         };
         
         // Mouse out handler
         var onMouseOut = function DLSM_onMouseOut(e, obj)
         {
            var elTarget = Event.getTarget(e);
            var related = elTarget.relatedTarget;

            // In some cases we should ignore this mouseout event
            if ((related != obj) && (!Dom.isAncestor(obj, related)))
            {
               if (obj.hideTimerId)
               {
                  window.clearTimeout(obj.hideTimerId);
               }
               obj.hideTimerId = window.setTimeout(fnHidePopup, me.options.actionsPopupTimeout);
            }
         };
         
         Event.on(elMoreActions, "mouseover", onMouseOver, elMoreActions);
         Event.on(elMoreActions, "mouseout", onMouseOut, elMoreActions);
      },
      
      /**
       * Edit Offline.
       *
       * @override
       * @method onActionEditOffline
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionEditOffline: function DL_onActionEditOffline(asset)
      {
         if (!this.state.actionEditOfflineActive)
         {
            // Make sure we don't call edit offline twice
            this.state.actionEditOfflineActive = true;

            var nodeRef = new Alfresco.util.NodeRef(asset.nodeRef),
               displayName = asset.displayName;

            this.modules.actions.genericAction(
            {
               success:
               {
                  event:
                  {
                     name: "changeFilter",
                     obj:
                     {
                        filterId: "editingMe"
                     }
                  },
                  callback:
                  {
                     fn: function DL_oAEO_success(data)
                     {
                        this.state.actionEditOfflineActive = false;
                        this.options.highlightFile = displayName;

                        // The filterChanged event causes the DocList to update, so we need to run these functions afterwards
                        var fnAfterUpdate = function DL_oAEO_success_afterUpdate()
                        {
                           var downloadUrl = Alfresco.constants.PROXY_URI + data.json.results[0].downloadUrl;
                           if (YAHOO.env.ua.ie > 6)
                           {
                              // MSIE7 blocks the download and gets the wrong URL in the "manual download bar"
                              Alfresco.util.PopupManager.displayPrompt(
                              {
                                 title: this.msg("message.edit-offline.success", displayName),
                                 text: this.msg("message.edit-offline.success.ie7"),
                                 buttons: [
                                 {
                                    text: this.msg("button.download"),
                                    handler: function DL_oAEO_success_download()
                                    {
                                       window.location = downloadUrl;
                                       this.destroy();
                                    },
                                    isDefault: true
                                 },
                                 {
                                    text: this.msg("button.close"),
                                    handler: function DL_oAEO_success_close()
                                    {
                                       this.destroy();
                                    }
                                 }]
                              });
                           }
                           else
                           {
                              Alfresco.util.PopupManager.displayMessage(
                              {
                                 text: this.msg("message.edit-offline.success", displayName)
                              });
                              // Kick off the download 3 seconds after the confirmation message
                              YAHOO.lang.later(3000, this, function()
                              {
                                 window.location = downloadUrl;
                              });
                           }
                        };
                        this.afterDocListUpdate.push(fnAfterUpdate);
                     },
                     scope: this
                  }
               },
               failure:
               {
                  callback:
                  {
                     fn: function DL_oAEO_failure()
                     {
                        this.state.actionEditOfflineActive = false;
                     },
                     scope: this
                  },
                  message: this.msg("message.edit-offline.failure", displayName)
               },
               webscript:
               {
                  method: Alfresco.util.Ajax.POST,
                  name: "checkout/node/{nodeRef}",
                  params:
                  {
                     nodeRef: nodeRef.uri
                  }
               }
            });
         }
      },
      
      /**
       * Checkout to Google Docs.
       *
       * @override
       * @method onActionCheckoutToGoogleDocs
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionCheckoutToGoogleDocs: function DL_onActionCheckoutToGoogleDocs(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef),
            path = asset.location.path,
            fileName = asset.fileName;
            
         var progressPopup = Alfresco.util.PopupManager.displayMessage(
         {
            displayTime: 0,
            effect: null,
            text: this.msg("message.checkout-google.inprogress", displayName)
         });

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "changeFilter",
                  obj:
                  {
                     filterId: "editingMe"
                  }
               },
               callback:
               {
                  fn: function DL_oACTGD_success(data)
                  {
                     progressPopup.destroy();
                     this.options.highlightFile = displayName;
                     
                     // The filterChanged event causes the DocList to update, so we need to run these functions afterwards
                     var fnAfterUpdate = function DL_oACTGD_success_afterUpdate()
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("message.checkout-google.success", displayName)
                        });
                     };
                     this.afterDocListUpdate.push(fnAfterUpdate);
                  },
                  scope: this
               },
               activity:
               {
                  siteId: this.options.siteId,
                  activityType: "google-docs-checkout",
                  page: "document-details",
                  activityData:
                  {
                     fileName: fileName,
                     path: path,
                     nodeRef: nodeRef.toString()
                  }
               }
            },
            failure:
            {
               callback:
               {
                  fn: function DocumentActions_oAEO_failure(data)
                  {
                     progressPopup.destroy();
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.checkout-google.failure", displayName)
                     });
                  },
                  scope: this
               }
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "checkout/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });
      },

      /**
       * Check in a new version from Google Docs.
       *
       * @override
       * @method onActionCheckinFromGoogleDocs
       * @param asset {object} Object literal representing the file to be actioned upon
       */
      onActionCheckinFromGoogleDocs: function DL_onActionCheckinFromGoogleDocs(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef),
            originalNodeRef = new Alfresco.util.NodeRef(asset.custom.workingCopyOriginal),
            path = asset.location.path;

         var progressPopup = Alfresco.util.PopupManager.displayMessage(
         {
            displayTime: 0,
            effect: null,
            text: this.msg("message.checkin-google.inprogress", displayName)
         });

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh"
               },
               callback:
               {
                  fn: function DL_oACFGD_success(data)
                  {
                     progressPopup.destroy();
                     
                     // The filterChanged event causes the DocList to update, so we need to run these functions afterwards
                     var fnAfterUpdate = function DL_oACTGD_success_afterUpdate()
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("message.checkin-google.success", displayName)
                        });
                     };
                     this.afterDocListUpdate.push(fnAfterUpdate);
                  },
                  scope: this
               },
               activity:
               {
                  siteId: this.options.siteId,
                  activityType: "google-docs-checkin",
                  page: "document-details",
                  activityData:
                  {
                     fileName: displayName,
                     path: path,
                     nodeRef: originalNodeRef.toString()
                  }
               }
            },
            failure:
            {
               message: this.msg("message.checkin-google.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "checkin/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });
      },

      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */
      
      /**
       * Generic file action event handler
       *
       * @method onFileAction
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFileAction: function DL_onFileAction(layer, args)
      {
         var obj = args[1];
         if (obj)
         {
            if (!obj.multiple)
            {
               this._updateDocList.call(this);
            }
         }
      },

      /**
       * File or folder renamed event handler
       *
       * @method onFileRenamed
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFileRenamed: function DL_onFileRenamed(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.file !== null))
         {
            var recordFound = this._findRecordByParameter(obj.file.nodeRef, "nodeRef");
            if (recordFound !== null)
            {
               this.widgets.dataTable.updateRow(recordFound, obj.file);
               var el = this.widgets.dataTable.getTrEl(recordFound);
               Alfresco.util.Anim.pulse(el);
            }
         }
      },

      /**
       * DocList Refresh Required event handler
       *
       * @method onDocListRefresh
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onDocListRefresh: function DL_onDocListRefresh(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.highlightFile !== null))
         {
            this.options.highlightFile = obj.highlightFile;
         }
         this._updateDocList.call(this);
      },

      /**
       * DocList View change filter request event handler
       *
       * @method onChangeFilter
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onChangeFilter: function DL_onChangeFilter(layer, args)
      {
         var obj = args[1];

         if ((obj !== null) && (obj.filterId !== null))
         {
            // Should be a filter in the arguments
            var filter = Alfresco.util.cleanBubblingObject(obj),
               strFilter = window.escape(obj.filterId) + (typeof obj.filterData !== "undefined" ? "|" + window.escape(obj.filterData) : "");
            
            Alfresco.logger.debug("DL_onChangeFilter: ", filter);

            var objNav =
            {
               filter: strFilter
            };
            
            // Initial navigation won't fire the History event
            if (obj.doclistFirstTimeNav)
            {
               this._updateDocList.call(this,
               {
                  filter: filter,
                  page: this.currentPage
               });
            }
            else
            {
               if (this.options.usePagination)
               {
                  this.currentPage = 1;
                  objNav.page = "1";
               }

               Alfresco.logger.debug("DL_onChangeFilter: objNav = ", objNav);

               // Do we think the history state will change?
               if (this.options.highlightFile && objNav.filter == YAHOO.util.History.getCurrentState("filter"))
               {
                  YAHOO.Bubbling.fire("highlightFile",
                  {
                     fileName: this.options.highlightFile
                  });
               }
               
               YAHOO.util.History.multiNavigate(objNav);
            }
         }
      },

      /**
       * DocList View Filter changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onFilterChanged: function DL_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            obj.filterOwner = obj.filterOwner || Alfresco.util.FilterManager.getOwner(obj.filterId);

            // Should be a filterId in the arguments
            this.currentFilter = Alfresco.util.cleanBubblingObject(obj);
            Alfresco.logger.debug("DL_onFilterChanged: ", this.currentFilter);
         }
      },

      /**
       * Highlight file event handler
       * Used when a component (including the DocList itself on loading) wants to scroll to and highlight a file
       *
       * @method onHighlightFile
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (filename to be highlighted)
       */
      onHighlightFile: function DL_onHighlightFile(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.fileName !== null))
         {
            Alfresco.logger.debug("DL_onHighlightFile: ", obj.fileName);
            var recordFound = this._findRecordByParameter(obj.fileName, "displayName");
            if (recordFound !== null)
            {
               // Scroll the record into view and highlight it
               var el = this.widgets.dataTable.getTrEl(recordFound);
               var yPos = Dom.getY(el);
               if (YAHOO.env.ua.ie > 0)
               {
                  yPos = yPos - (document.body.clientHeight / 3);
               }
               else
               {
                  yPos = yPos - (window.innerHeight / 3);
               }
               window.scrollTo(0, yPos);
               Alfresco.util.Anim.pulse(el);
               this.options.highlightFile = null;

               // Select the file
               Dom.get("checkbox-" + recordFound.getId()).checked = true;
               this.selectedFiles[recordFound.getData("nodeRef")] = true;
               YAHOO.Bubbling.fire("selectedFilesChanged");
            }
         }
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function DL_onDeactivateAllControls(layer, args)
      {
         var index, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               fnDisable(this.widgets[index]);
            }
         }
      },
      
      /**
       * Deactivate Dynamic Controls event handler
       * Only deactivates specifically defined controls.
       *
       * @method onDeactivateDynamicControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateDynamicControls: function DL_onDeactivateDynamicControls(layer, args)
      {
         var index, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.dynamicControls)
         {
            if (this.dynamicControls.hasOwnProperty(index))
            {
               fnDisable(this.dynamicControls[index]);
            }
         }
      },
      
      /**
       * Activate Dynamic Controls event handler
       * (Re-)Activates controls taking part in dynamic deactivation
       *
       * @method onActivateDynamicControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onActivateDynamicControls: function DL_onActivateDynamicControls(layer, args)
      {
         var index, fnEnable = Alfresco.util.enableYUIButton;
         for (index in this.dynamicControls)
         {
            if (this.dynamicControls.hasOwnProperty(index))
            {
               fnEnable(this.dynamicControls[index]);
            }
         }
      },
      
      /**
       * Favourite document event handler
       *
       * @method onFavouriteDocument
       * @param row {HTMLElement} DOM reference to a TR element (or child thereof)
       */
      onFavouriteDocument: function DL_onFavouriteDocument(row)
      {
         this._favouriteHandler(row, Alfresco.service.Preferences.FAVOURITE_DOCUMENTS);
      },

      /**
       * Favourite folder event handler
       *
       * @method onFavouriteFolder
       * @param row {HTMLElement} DOM reference to a TR element (or child thereof)
       */
      onFavouriteFolder: function DL_onFavouriteFolder(row)
      {
         this._favouriteHandler(row, Alfresco.service.Preferences.FAVOURITE_FOLDERS);
      },
      

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Handler to set/reset favourite for document or folder
       *
       * @method _favouriteHandler
       * @private
       * @param row {HTMLElement} DOM reference to a TR element (or child thereof)
       * @param prefKey {String} The preferences key
       */
      _favouriteHandler: function DL__favouriteHandler(row, prefKey)
      {
         var record = this.widgets.dataTable.getRecord(row),
            file = record.getData(),
            nodeRef = file.nodeRef;
         
         file.isFavourite = !file.isFavourite;
         this.widgets.dataTable.updateRow(record, file);
               
         var responseConfig =
         {
            failureCallback:
            {
               fn: function DL_oFD_failure(event, p_oRecord)
               {
                  // Reset the flag to it's previous state
                  var file = p_oRecord.getData();
                  file.isFavourite = !file.isFavourite;
                  this.widgets.dataTable.updateRow(p_oRecord, file);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.favourite.failure", file.displayName)
                  });
               },
               scope: this,
               obj: record
            }
         };

         var fnPref = file.isFavourite ? "add" : "remove";
         this.services.preferences[fnPref].call(this.services.preferences, prefKey, nodeRef, responseConfig);
      },

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function DL__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.DocumentList"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.DocumentList"));
      },
      
      /**
       * Updates document list by calling data webscript with current site and path
       *
       * @method _updateDocList
       * @param p_obj.filter {object} Optional filter to navigate with
       * @param p_obj.page {string} Optional page to navigate to (defaults to this.currentPage)
       */
      _updateDocList: function DL__updateDocList(p_obj)
      {
         p_obj = p_obj || {};
         Alfresco.logger.debug("DL__updateDocList: ", p_obj.filter, p_obj.page);
         var successFilter = YAHOO.lang.merge({}, p_obj.filter !== undefined ? p_obj.filter : this.currentFilter),
            successPage = p_obj.page !== undefined ? p_obj.page : this.currentPage,
            loadingMessage = null,
            timerShowLoadingMessage = null,
            me = this,
            params =
            {
               filter: successFilter,
               page: successPage
            };
         successFilter.doclistFirstTimeNav = false;
         
         // Clear the current document list if the data webscript is taking too long
         var fnShowLoadingMessage = function DL_fnShowLoadingMessage()
         {
            Alfresco.logger.debug("DL__uDL_fnShowLoadingMessage: slow data webscript detected.");
            // Check the timer still exists. This is to prevent IE firing the event after we cancelled it. Which is "useful".
            if (timerShowLoadingMessage)
            {
               loadingMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  displayTime: 0,
                  text: '<span class="wait">' + $html(this.msg("message.loading")) + '</span>',
                  noEscape: true
               });
               
               if (YAHOO.env.ua.ie > 0)
               {
                  this.loadingMessageShowing = true;
               }
               else
               {
                  loadingMessage.showEvent.subscribe(function()
                  {
                     this.loadingMessageShowing = true;
                  }, this, true);
               }
            }
         };
         
         // Reset the custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         
         // Reset preview tooltips array
         this.previewTooltips = [];
         
         // More Actions menu no longer relevant
         this.showingMoreActions = false;
         
         // Slow data webscript message
         this.loadingMessageShowing = false;
         timerShowLoadingMessage = YAHOO.lang.later(this.options.loadingMessageDelay, this, fnShowLoadingMessage);
         
         var destroyLoaderMessage = function DL__uDL_destroyLoaderMessage()
         {
            if (timerShowLoadingMessage)
            {
               // Stop the "slow loading" timed function
               timerShowLoadingMessage.cancel();
               timerShowLoadingMessage = null;
            }

            if (loadingMessage)
            {
               if (this.loadingMessageShowing)
               {
                  // Safe to destroy
                  loadingMessage.destroy();
                  loadingMessage = null;
               }
               else
               {
                  // Wait and try again later. Scope doesn't get set correctly with "this"
                  YAHOO.lang.later(100, me, destroyLoaderMessage);
               }
            }
         };
         
         var successHandler = function DL__uDL_successHandler(sRequest, oResponse, oPayload)
         {
            destroyLoaderMessage();
            // Updating the Doclist may change the file selection
            var fnAfterUpdate = function DL__uDL_sH_fnAfterUpdate()
            {
               YAHOO.Bubbling.fire("activateDynamicControls");
               YAHOO.Bubbling.fire("selectedFilesChanged");
            };
            this.afterDocListUpdate.push(fnAfterUpdate);
            
            Alfresco.logger.debug("currentFilter was:", this.currentFilter, "now:", successFilter);
            Alfresco.logger.debug("currentPage was [" + this.currentPage + "] now [" + successPage + "]");
            this.currentFilter = successFilter;
            this.currentPage = successPage;
            if (successFilter.filterId == "path")
            {
               Alfresco.logger.debug("currentPath was [" + this.currentPath + "] now [" + successFilter.filterData + "]");
               this.currentPath = successFilter.filterData;
            }
            YAHOO.Bubbling.fire("filterChanged", successFilter);
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };
         
         var failureHandler = function DL__uDL_failureHandler(sRequest, oResponse)
         {
            destroyLoaderMessage();
            // Clear out deferred functions
            this.afterDocListUpdate = [];

            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload(true);
            }
            else
            {
               try
               {
                  if (oResponse.status == 404)
                  {
                     // Folder not found (via the HTTP "404 Not Found" response) - deactivate dynamic controls only
                     YAHOO.Bubbling.fire("deactivateDynamicControls");
                  }
                  else
                  {
                     // Site or container not found (e.g. via the HTTP "410 Gone" response) or more serious - deactivate all controls
                     YAHOO.Bubbling.fire("deactivateAllControls");
                  }

                  var fnAfterFailedUpdate = function DL__uDL_failureHandler_fnAfterUpdate(responseMsg)
                  {
                     return function DL__uDL_failureHandler_afterUpdate()
                     {
                        this.widgets.paginator.setState(
                        {
                           totalRecords: 0
                        });
                        this.widgets.paginator.render();
                        this.widgets.dataTable.set("MSG_ERROR", responseMsg);
                        this.widgets.dataTable.showTableMessage(responseMsg, YAHOO.widget.DataTable.CLASS_ERROR);
                     };
                  };

                  this.afterDocListUpdate.push(fnAfterFailedUpdate(YAHOO.lang.JSON.parse(oResponse.responseText).message));
                  this.widgets.dataTable.initializeTable();
                  this.widgets.dataTable.render();
               }
               catch(e)
               {
                  Alfresco.logger.error(e);
                  this._setDefaultDataTableErrors(this.widgets.dataTable);
               }
            }
         };
         
         // Update the DataSource
         if (params.filter && params.filter.filterId == "path")
         {
            params.path = params.filter.filterData;
         }
         var requestParams = this._buildDocListParams(params);
         Alfresco.logger.debug("DataSource requestParams: ", requestParams);
         this.widgets.dataSource.sendRequest(requestParams,
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },

      /**
       * Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
       * @param p_obj.page {string} Page number
       * @param p_obj.pageSize {string} Number of items per page
       * @param p_obj.path {string} Path to query
       * @param p_obj.type {string} Filetype to filter: "all", "documents", "folders"
       * @param p_obj.site {string} Current site
       * @param p_obj.container {string} Current container
       * @param p_obj.filter {string} Current filter
       */
      _buildDocListParams: function DL__buildDocListParams(p_obj)
      {
         // Essential defaults
         var obj = 
         {
            path: this.currentPath,
            type: this.options.showFolders ? "all" : "documents",
            site: this.options.siteId,
            container: this.options.containerId,
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
         var params = YAHOO.lang.substitute("{type}/site/{site}/{container}" + (obj.filter.filterId == "path" ? "{path}" : ""),
         {
            type: encodeURIComponent(obj.type),
            site: encodeURIComponent(obj.site),
            container: encodeURIComponent(obj.container),
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

         return params;
      },
       
      /**
       * Searches the current recordSet for a record with the given parameter value
       *
       * @private
       * @method _findRecordByParameter
       * @param p_value {string} Value to find
       * @param p_parameter {string} Parameter to look for the value in
       */
      _findRecordByParameter: function DL__findRecordByParameter(p_value, p_parameter)
      {
        var recordSet = this.widgets.dataTable.getRecordSet();
        for (var i = 0, j = recordSet.getLength(); i < j; i++)
        {
           if (recordSet.getRecord(i).getData(p_parameter) == p_value)
           {
              return recordSet.getRecord(i);
           }
        }
        return null;
      }
   }, true);
})();
