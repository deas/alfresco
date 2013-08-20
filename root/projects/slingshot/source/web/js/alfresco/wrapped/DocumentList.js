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
 * @module alfresco/wrapped/DocumentList
 * @extends module:alfresco/core/WrappedShareWidget
 * @mixes dijit/_TemplatedMixin
 * @author Dave Draper
 * @deprecated
 */
define(["dojo/_base/declare",
        "alfresco/core/WrappedShareWidget", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/DocumentList.html",
        "alfresco/wrapped/DocumentListToolbar",
        "dojo/_base/lang",
        "dojo/dom",
        "dojo/dom-construct",
        "dojo/dom-style"], 
        function(declare, WrappedShareWidget, _TemplatedMixin, template, DocumentListToolbar, lang, dom, domConstruct, domStyle) {
   
   return declare([WrappedShareWidget, _TemplatedMixin], {

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       */
      cssRequirements: [{cssFile:"/components/documentlibrary/actions.css"},
                        {cssFile:"/components/documentlibrary/documentlist.css"},
                        {cssFile:"/mediamanagement/components/documentlibrary/documentlist-spreadsheet.css"},
                        {cssFile:"/mediamanagement/components/documentlibrary/documentlist-focus.css"},
                        {cssFile:"/components/preview/web-preview.css"},
                        {cssFile:"/components/preview/WebPreviewerHTML.css"},
                        {cssFile:"/components/preview/Audio.css"},
                        {cssFile:"/components/preview/Image.css"},
                        {cssFile:"./css/DocumentList.css"}],
      
      /**
       * This has to be set the same as the wrapped widget in order for message to be successfully retrieved.
       * This then ensures that the correct scope is set so that the messages can be retrieved as usual. 
       * 
       * @instance
       * @type {string}
       * @default "Alfresco.DocumentList"
       */
      i18nScope: "Alfresco.DocumentList",
             
      /**
       * Specifies the properties file from the WebScript that is used to instantiate the widget. It's only necessary
       * to specify the default property file - the Dojo dependency handler will sort out the locale as necessary
       * 
       * @instance
       * @type {object[]}
       */
      i18nRequirements: [{i18nFile: "/WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/documentlibrary/documentlist.get.properties"}],

      /**
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * The JavaScript file referenced by the DocumentList
       * @instance
       * @type {string[]}
       */
      nonAmdDependencies: ["/components/form/form.js",
                           "/components/form/date.js",
                           "/components/form/date-picker.js",
                           "/components/form/period.js",
                           "/components/object-finder/object-finder.js",
                           "/yui/calendar/calendar.js",
                           "/modules/editors/tiny_mce/tiny_mce.js",
                           "/modules/editors/tiny_mce.js",
                           "/components/form/rich-text.js",
                           "/components/form/content.js",
                           "/components/form/workflow/transitions.js",
                           "/components/form/workflow/activiti-transitions.js",
                           "/components/form/jmx/operations.js",
                           "/modules/documentlibrary/doclib-actions.js",
                           "/modules/simple-dialog.js",
                           "/mediamanagement/components/documentlibrary/yui/carousel/carousel-min.js",
                           "/components/preview/web-preview.js",
                           "/components/preview/WebPreviewer.js",
                           "/js/flash/extMouseWheel.js",
                           "/components/preview/StrobeMediaPlayback.js",
                           "/components/preview/Video.js",
                           "/components/preview/Audio.js",
                           "/components/preview/Flash.js",
                           "/components/preview/Image.js",
                           "/components/documentlibrary/actions.js",
                           "/components/documentlibrary/documentlist-view-detailed.js",
                           "/components/documentlibrary/documentlist-view-gallery.js",
                           "/components/documentlibrary/documentlist-view-simple.js",
                           "/mediamanagement/components/documentlibrary/documentlist-spreadsheet.js",
                           "/mediamanagement/components/documentlibrary/documentlist-focus.js",
                           "/components/documentlibrary/documentlist.js",
                           "/yui/slider/slider.js"],
      
      /**
       * @instance
       * @type {object}
       * @default null
       */
      templateMessages: null,
      
      /**
       * Indicates whether or not the DocumentList has completed initialisation.
       * @instance
       * @type {boolean} 
       * @default false
       */
      _isIntialised: false,
      
      /**
       * This should be set to to the root node that the DocumentList should use.
       * 
       * @instance
       * @type {string} 
       * @default "alfresco://company/home"
       */
      rootNode: "alfresco:\/\/company\/home",
      
      /**
       * Indicates whether or not the current user has permission to upload documents. This is initialised to 
       * false and will remain that way until it is updated by the _setupDataSource function.
       * 
       * @instance
       * @type {boolean}
       * @default false
       */
      userCanUpload: false,
      
      /**
       * @instance
       */
      containerId: null,
      
      /**
       * @instance
       * @type {string}
       * @default ""
       */
      highlightFile: "",
      
      /**
       * When set to a non-null value will indicate the site that should be represented.
       * 
       * @instance
       * @type {string}
       * @default null
       */
      siteId: null,
      
      /**
       * @instance
       * @type {string}
       * @default "{}"
       */
      replicationUrlMapping: "{}",
      
      /**
       * Whether the Repository Browser is in use or not
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      repositoryBrowsing: false,
      
      /**
       * Indicates whether to run the DocumentLibrary in Cloud Sync mode.
       * 
       * @instance
       * @type {string}
       * @default null
       */
      syncMode: null,
      
      /**
       * @instance
       * @type {boolean}
       * @default false
       */
      usePagination: false,
      
      /**
       * Whether the cm:title property is in use or not
       * 
       * @instance
       * @type {boolean}
       * @default true 
       */
      useTitle: true,
      
      /**
       * Indicates whether or not the current user is a manager of the current site
       * @instance
       * @type {boolean} userIsSiteManager
       * @default false 
       */
      userIsSiteManager: false,
      
      /**
       * @instance
       * @type {string}
       * @default "simple"
       */
      viewRendererName: "simple",
         
      /**
       * @instance
       * @type {string[]}
       * @default null
       */
      viewRendererNames: null,
      
      /**
       * The constructor is extended so that we can construct an object containing all the i18n properties to 
       * be substituted by the template. This is because the template can't call functions to obtain data.
       * There are potentially better ways of doing this - but it does at least work.
       * 
       * @instance
       */
      constructor: function alfresco_wrapped_DocumentList__constructor(args) {
         declare.safeMixin(this, args);
         this.templateMessages = {
            no_items_title: this.message("no.items.title"),
            dnd_drop_title: this.message("dnd.drop.title"),
            dnd_drop_doclist_description: this.message("dnd.drop.doclist.description"),
            dnd_drop_folder_description: this.message("dnd.drop.folder.description"),
            standard_upload_title: this.message("standard.upload.title"),
            panel_header_spreadsheetConfig : this.message("panel.header.spreadsheetConfig"),
            button_spreadsheet_config : this.message("button.spreadsheet.config")
         };
      },
      
      /**
       * The wrapped DocumentList widget
       * @instance
       * @type {object}
       * @default null
       */
      _documentList : null,
      
      /**
       * The current filter
       * @instance
       * @type {object} 
       */
      _currentFilter: null,
      
      /**
       * Creates an "Alfresco.DoucmentList" instance and connects all of the associated events to and from it.
       * 
       * @instance
       */
      postCreate: function alfresco_wrapped_DocumentList__postCreate() {
         
         // Create an instance of the wrapped Alfresco.DocListToolbar. We need to refer to this via the
         // wrapping module to ensure that the i18nScope is set correctly for the accessing messages.
         this._toolbar = new DocumentListToolbar({
            siteId: this.siteId,
            rootNode: this.rootNode,
            hideNavBar: this.hideNavBar,
            googleDocsEnabled: this.googleDocsEnabled,
            repositoryBrowsing: this.repositoryBrowsing,
            useTitle: this.useTitle,
            syncMode: this.syncMode,
            createContentByTemplateEnabled: this.createContentByTemplateEnabled,
            createContentActions: this.createContentActions
         });
         this._toolbar.toolbar.onReady();
         
         // It is necessary to capture some events that are fired from the wrapped DocumentList widget.
         // One of these is the "doclistMetadata" event which will then be published on a Dojo topic so
         // that listening services can capture the current location that the DocumentList is displaying.
         // This is essential so that content gets created in the correct destination.
         // Ideally we wouldn't be using the YAHOO Bubbling library - this is only being used because
         // the wrapped DocumentList widget is using it.
         YAHOO.Bubbling.on("doclistMetadata", this.onMetadataChange, this);
         YAHOO.Bubbling.on("selectedFilesChanged", this.onSelectedFilesChanged, this);
         YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
         YAHOO.Bubbling.on("userAccess", this.onUserAccess, this);
         
         // Connect the Alfresco topics to the DocumentList...
         this.alfSubscribe("ALF_DOCLIST_FILE_SELECTION", lang.hitch(this, "onFileSelection"));
         this.alfSubscribe("ALF_DOCLIST_SHOW_FOLDERS", lang.hitch(this, "onShowFolders"));
         this.alfSubscribe("ALF_DOCLIST_SELECT_VIEW", lang.hitch(this, "onViewSelect"));
         this.alfSubscribe("ALF_DOCLIST_SORT", lang.hitch(this, "onSortRequest"));
         this.alfSubscribe("ALF_DOCLIST_SORT_FIELD_SELECTION", lang.hitch(this, "onSortFieldSelection"));
         this.alfSubscribe("ALF_DOCLIST_FILTER_CHANGED", lang.hitch(this, "onWrappedFilterChanged"));
         this.alfSubscribe("ALF_NODE_CREATED", lang.hitch(this, "onNodeCreated"));
         this.alfSubscribe("ALF_NODE_RESIZED", lang.hitch(this, "onResizeEvent"));
         
         // Create the "Alfresco.DocumentList" widget that is to be wrapped...
         this._documentlist = new Alfresco.DocumentList(this.id).setOptions({
            _userCanUpload: this.userCanUpload,
            containerId: this.containerId, 
            highlightFile: this.hightlightFile,
            replicationUrlMapping: this.replicationUrlMapping,
            repositoryBrowsing: this.repositoryBrowsing,
            rootNode: this.rootNode,
            simpleView: "null",
            showFolders: this.showFolders,
            siteId : this.siteId,
            sortAscending: this.sortAscending,
            sortField: this.sortField,
            syncMode: this.syncMode,
            usePagination: this.usePagination,
            userIsSiteManager: this.userIsSiteManager,
            useTitle: this.useTitle,
            viewRendererName: this.viewRendererName,
            viewRendererNames: this.viewRendererNames
         });
         
         Dom.setStyle(this.id + "-body", "visibility", "visible");
         
         // Load in any additional custom action handling dependencies...
         this.loadCustomActionHandlers();
         // TODO: Need to load in the additional CSS dependencies...
      },

      /**
       * 
       * @instance
       */
      loadCustomActionHandlers: function alfresco_wrapped_DocumentList__loadCustomActionHandlers(dependencies) {
         var _this = this;
         if (this.customAggregatedJsResource)
         {
            require([Alfresco.constants.URL_RESCONTEXT + this.customAggregatedJsResource], function() {
               _this.customActionHandlersLoaded();
            });
         }
      },
      
      /**
       * @instance
       */
      customActionHandlersLoaded: function() {
         this.alfLog("log", "Custom action handlers loaded");
      },
      
      /**
       * Listens for file selection requests and delegates to the wrapped DocumentList "selectFiles"
       * function.
       * 
       * @instance
       * @param {object} payload The payload published on the topic
       */
      onFileSelection: function alfresco_wrapped_DocumentList__onFileSelection(payload) {
         this.alfLog("log", "File selected: ", payload);
         if (payload && payload.value != null)
         {
            this._documentlist.selectFiles(payload.value);
         }
      },
      
      /**
       * Listens for folder view toggle requests and delegates to the wrapped DocumentList "onShowFoldersEvent"
       * function.
       * 
       * @instance
       * @param {object} payload The payload published on the topic
       */
      onShowFolders: function alfresco_wrapped_DocumentList__onShowFolders(payload) {
         this.alfLog("log", "Show Folders Request: ", payload);
         if (payload && payload.selected != null)
         {
            this._documentlist.onShowFoldersEvent(payload.selected);
         }
      },
      
      /**
       * Listens for view selection requests and delegates to the wrapped DocumentList "onViewRendererSelectEvent"
       * function.
       * 
       * @instance
       * @param {object} payload The payload published on the topic
       */
      onViewSelect: function alfresco_wrapped_DocumentList__onViewSelect(payload) {
         this.alfLog("log", "View Selected: ", payload);
         if (payload && payload.value)
         {
            this._documentlist.onViewRendererSelectEvent(payload.value);
         }
      },
      
      /**
       * Listens for sort requests requests and delegates to the wrapped DocumentList "onSortEvent"
       * function.
       * 
       * @instance
       * @param {object} payload The payload published on the topic
       */
      onSortRequest: function alfresco_wrapped_DocumentList__onSortRequest(payload) {
         this.alfLog("log", "Sort requested: ", payload);
         if (payload && payload.direction)
         {
            this._documentlist.onSortEvent(payload.direction);
         }
      },
      
      /**
       * Listens for sort requests requests and delegates to the wrapped DocumentList "onSortFieldEvent"
       * function.
       * 
       * @instance
       * @param {object} payload The payload published on the topic
       */
      onSortFieldSelection: function alfresco_wrapped_DocumentList__onSortFieldSelection(payload) {
         this.alfLog("log", "Sort field selected: ", payload);
         if (payload && payload.value)
         {
            this._documentlist.onSortFieldEvent(payload.value, payload.direction);
         }
      },

      /**
       * Handles changes in the filter and delegates to the wrapped DocumentList 
       * @instance
       * @param {object} payload The filter data.
       */
      onWrappedFilterChanged: function alfresco_wrapped_DocumentList__onWrappedFilterChanged(payload) {
         this.alfLog("log", "Filter change detected", payload);
         if (this._currentFilter == null || (payload && this._currentFilter != payload))
         {
            // Pass the payload in an array as that is how the DocumentList widget expects to receive the
            // filter data...
            this.alfLog("info", "Updating DocumentList with new filter:", payload);
            this._documentlist.onChangeFilter(null, [null, payload]);
         }
         else
         {
            this.alfLog("warn", "The current filter matches the published change:", this._currentFilter, payload);
         }
      },
      
      /**
       * 
       * @instance
       */
      onNodeCreated: function alfresco_wrapped_DocumentList__onNodeCreated(payload) {
         this.alfLog("log", "Node creation detected", payload);
         if (payload && this.currentNode && payload.parentNodeRef == this.currentNode.parent.nodeRef)
         {
            this.alfLog("log", "Refreshing DocumentList");
            this._documentlist.onDocListRefresh(null, [payload]);
         }
      },
      
      /**
       * @instance
       * @type {object} currentNode Keeps track of the node currently being displayed by the DocumentList
       */
      currentNode: null,
      
      /**
       * 
       * @instance
       */
      onMetadataChange: function alfresco_wrapped_DocumentList__onMetadataChange(layer, args) {
         this.alfLog("info", "Detected Metadata change")
         var obj = args[1];
         this.alfLog("info", "Detected Metadata change: ", obj);
         if (obj && obj.metadata)
         {
            var doclistMetadata = Alfresco.util.deepCopy(obj.metadata);
            this.currentNode = Alfresco.util.deepCopy(obj.metadata);;
            this.alfPublish("ALF_CURRENT_NODEREF_CHANGED", {
               node: doclistMetadata
            });
         }
      },

      /**
       * 
       * @instance
       */
      onSelectedFilesChanged: function alfresco_wrapped_DocumentList__onSelectedFilesChanged(layer, args) {
         this.alfLog("log", "Selected files changed: ", layer, args);

         var availableFiles = null;
         if (this._documentlist && 
             this._documentlist.widgets && 
             this._documentlist.widgets.dataTable && 
             typeof this._documentlist.widgets.dataTable.getRecordSet === "function" && 
             typeof this._documentlist.widgets.dataTable.getRecordSet().getLength === "function")
         {
            availableFiles = this._documentlist.widgets.dataTable.getRecordSet().getLength();
         }
         
         this.alfPublish("ALF_SELECTED_FILES_CHANGED", {
            selectedFiles: this._documentlist.getSelectedFiles(),
            availableItemCount: availableFiles,
            selectedItemCount: this._documentlist.getSelectedFiles().length
         });
         
         // Firing the file selection event is the last thing that the wrapped document list does 
         // after being initialised. This event can be used an indicator that our document
         // list widget has completed initialisation...
         if (!this._isIntialised)
         {
            this._isIntialised = true;
            this.alfPublish("ALF_DOCLIST_READY", {});
         }
      },
      
      /**
       * @instance
       */
      onFilterChanged: function alfresco_wrapped_DocumentList__onFilterChanged(layer, args) {
         var obj = args[1];
         if (obj && (typeof obj.filterId !== "undefined"))
         {
            obj.filterOwner = obj.filterOwner || Alfresco.util.FilterManager.getOwner(obj.filterId);
         }
         // Set the current filter...
         this.alfLog("info", "Setting current filter", obj);
         this._currentFilter = obj;
         this.alfPublish("ALF_DOCLIST_FILTER_CHANGED", obj);
      },
      
      /**
       * This function handles the YUI Bubbling "userAccess" event that is fired from the DocumentList when the
       * data source is set. This information is important for controlling the actions (in particular the create
       * content actions) as they need to respond to changes in access. 
       * 
       * @instance
       */
      onUserAccess: function alfresco_wrapped_DocumentList__onUserAccess(layer, args) {
         this.alfLog("log", "User access change: ", layer, args);
         var obj = args[1];
         if (obj)
         {
            this.alfPublish("ALF_DOCLIST_USER_ACCESS_CHANGED", obj);
         }
      },
      
      /**
       * This function handles the standard event "ALF_NODE_RESIZED" that is used to indicate that a DOM
       * element (rather than an Alfresco node!) has been resized. This function checks that the DocumentList
       * node is a descendant of the resized DOM node and if it is calls the _resizeRowContainers on the 
       * wrapped documentlist to ensure that it is rendered correctly.
       * 
       * @instance
       * @param {object} payload The payload published on the topic
       */
      onResizeEvent: function alfresco_wrapped_DocumentList__onResizeEvent(payload) {
         if (payload && payload.node && this._documentlist)
         {
            if (dom.isDescendant(this.domNode, payload.node))
            {
               var width = domStyle.get(payload.node, "width");
               this._documentlist._resizeRowContainers(width);
            }
         }
      }
   });
});