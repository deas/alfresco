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
 * Used to represent a list of documents.
 * @todo Clearly needs more info
 * 
 * @module alfresco/documentlibrary/AlfDocumentList
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreWidgetProcessing
 * @mixes module:alfresco/core/CoreXhr
 * @mixes module:alfresco/core/PathUtils
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @mixes module:alfresco/documentlibrary/_AlfHashMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/AlfDocumentList.html",
        "alfresco/core/Core",
        "alfresco/core/CoreWidgetProcessing",
        "alfresco/core/CoreXhr",
        "alfresco/core/PathUtils",
        "alfresco/core/JsNode",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "alfresco/documentlibrary/_AlfHashMixin",
        "alfresco/core/DynamicWidgetProcessingTopics",
        "alfresco/services/_PreferenceServiceTopicMixin",
        "alfresco/documentlibrary/views/AlfDocumentListView",
        "dojo/_base/array",
        "dojo/_base/lang",
        "alfresco/menus/AlfCheckableMenuItem",
        "dojo/dom-construct",
        "dojo/dom-class"], 
        function(declare, _WidgetBase, _TemplatedMixin, template, AlfCore, CoreWidgetProcessing, AlfCoreXhr, PathUtils, JsNode, _AlfDocumentListTopicMixin, _AlfHashMixin, DynamicWidgetProcessingTopics,
                 _PreferenceServiceTopicMixin, AlfDocumentListView, array, lang, AlfCheckableMenuItem, domConstruct, domClass) {
   
   return declare([_WidgetBase, _TemplatedMixin, AlfCore, CoreWidgetProcessing, AlfCoreXhr, PathUtils, _AlfDocumentListTopicMixin, _AlfHashMixin, DynamicWidgetProcessingTopics, _PreferenceServiceTopicMixin], {
      
      /**
       * Declare the dependencies on "legacy" JS files.
       * 
       * @instance
       * @type {string[]}
       * @default ["/js/alfresco.js"]
       */
      nonAmdDependencies: ["/js/yui-common.js",
                           "/js/alfresco.js"],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/AlfDocumentList.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/AlfDocumentList.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {object[]}
       * @default [{cssFile:"./css/AlfDocumentList.css"}]
       */
      cssRequirements: [{cssFile:"./css/AlfDocumentList.css"}],

      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * A map of views that the DocumentList can switch between.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      viewMap: null,
      
      /**
       * A map of the additional controls that each view requires. This is map is populated as each view
       * is selected (so that the controls are only loaded once) but are then loaded from the map. This
       * allows the same controls to be added and removed as views are switched.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      viewControlsMap: null,
      
      /**
       * The widgets processed by AlfDocumentList should all be instances of "alfresco/documentlibrary/AlfDocumentListView".
       * Any widget that is instantiated that does not inherit from that class will not be included as a view.
       * 
       * @instance
       * @type {object[]}
       * @default
       */
      widgets: null,
      
      /**
       * Indicates whether pagination should be used when requesting documents (e.g. include the page number and the number of
       * results per page)
       * 
       * @instance
       * @type {boolean}
       * @default true
       */
      usePagination: true,

      /**
       * Indicates whether or not folders should be shown in the document library.
       *
       * @instance
       * @type {boolean}
       * @default true
       */
      showFolders: true,

      /**
       * Indicates whether the location should be driven by changes to the browser URL hash
       *
       * @instance
       * @type {boolean}
       * @default false
       */
      useHash: false,

      /**
       * Subscribe the document list topics.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_documentlibrary_AlfDocumentList__postMixInProperties() {
         this.alfPublish(this.getPreferenceTopic, {
            preference: "org.alfresco.share.documentList.documentsPerPage",
            callback: this.setPageSize,
            callbackScope: this
         });

         // Set filter keys that are applicable for DocumentLists...
         // These are the values that are expected to be extracted from a hash fragment
         this.filterKeys = ["filterId","filterData","filterDisplay"];

         this.alfSubscribe(this.viewSelectionTopic, lang.hitch(this, "onViewSelected"));
         this.alfSubscribe(this.documentSelectionTopic, lang.hitch(this, "onDocumentSelection"));
         this.alfSubscribe(this.sortRequestTopic, lang.hitch(this, "onSortRequest"));
         this.alfSubscribe(this.sortFieldSelectionTopic, lang.hitch(this, "onSortFieldSelection"));
         this.alfSubscribe(this.showFoldersTopic, lang.hitch(this, "onShowFolders"));
         this.alfSubscribe(this.pageSelectionTopic, lang.hitch(this, "onPageChange"));
         this.alfSubscribe(this.docsPerpageSelectionTopic, lang.hitch(this, "onDocsPerPageChange"));
         this.alfSubscribe(this.reloadDataTopic, lang.hitch(this, "loadData"));
         
         // Subscribe to the topics that will be published on by the DocumentService when retrieving documents
         // that this widget requests...
         this.alfSubscribe("ALF_RETRIEVE_DOCUMENTS_REQUEST_SUCCESS", lang.hitch(this, "onDataLoadSuccess"));
         this.alfSubscribe("ALF_RETRIEVE_DOCUMENTS_REQUEST_FAILURE", lang.hitch(this, "onDataLoadFailure"));

         // Get the messages for the template...
         this.noViewSelectedMessage = this.message("doclist.no.view.message");
         this.noDataMessage = this.message("doclist.no.data.message");
         this.fetchingDataMessage = this.message("doclist.loading.data.message");
         this.renderingViewMessage = this.message("doclist.rendering.data.message");
      },
      
      /**
       * Sets the number of documents per page
       * 
       * @instance
       * @param {number} value The number of documents per page.
       */
      setPageSize: function alfresco_documentlibrary_AlfDocumentList__setPageSize(value) {
         if (value == null)
         {
            value = 25;
         }
         this.currentPageSize = value;
      },
      
      /**
       * This is the topic that will be subscribed to for responding to item clicks unless [useHash]{@link module:alfresco/documentlibrary/AlfDocumentList#useHash}
       * is set to true. [Views]{@link module:alfresco/documentlibrary/views/AlfDocumentListView} that defined
       * renderers that provide links using the [_ItemLinkMixin]{@link module:alfresco/renderers/_ItemLinkMixin} should
       * be configured to set a matching [linkClickTopic][_ItemLinkMixin]{@link module:alfresco/renderers/_ItemLinkMixin#linkClickTopic}
       * attribute in order to have their actions processed.
       *
       * @instance
       * @type {string}
       * @default "ALF_DOCLIST_NAV"
       */
      linkClickTopic: "ALF_DOCLIST_NAV",

      /**
       * This function is called whenever the [linkClickTopic]{@link module:alfresco/documentlibrary/AlfDocumentList#linkClickTopic}
       * is published. It processes the payload and updates the current filter and then refreshes the current
       * data by calling [loadData]{@link module:alfresco/documentlibrary/AlfDocumentList#loadData}.
       * 
       * @instance
       * @param {object} payload
       */
      onItemLinkClick: function alfresco_documentlibrary_AlfDocumentList__onItemLinkClick(payload) {
         var node = lang.getObject("item.node", false, payload);
         if (node.isContainer == true || node.isLink == true)
         {
            this.onFolderClick(payload);
         }
         else
         {
            this.onDocumentClick(payload);
         }
         
      },

      /**
       * 
       * @instance
       * @param {object} payload
       */
      onFolderClick: function alfresco_documentlibrary_AlfDocumentList__onFolderClick(payload) {
         if (payload.url != null)
         {
            this.currentFilter = this.processFilter(payload.url);
            if (this._readyToLoad) this.loadData();
         }
         else
         {
            this.alfLog("warn", "A 'url' attribute was expected to be provided for an item click", payload, this);
         }
      },

      /**
       * 
       * @instance
       * @param {object} payload
       */
      onDocumentClick: function alfresco_documentlibrary_AlfDocumentList__onDocumentClick(payload) {
         // No action for the moment
      },

      /**
       * @instance
       */
      postCreate: function alfresco_documentlibrary_AlfDocumentList__postCreate() {
         
         // Create a subscription to listen out for all widgets on the page being reported
         // as ready (then we can start loading data)...
         this.alfSubscribe("ALF_WIDGETS_READY", lang.hitch(this, "onPageWidgetsReady"), true);

         // Instantiate a new map to hold all of the views for the DocumentList...
         this.viewMap = {};
         this.viewControlsMap = {};
         
         // Process the array of widgets. Only views should be included as widgets of the DocumentList.
         if (this.widgets)
         {
            this.processWidgets(this.widgets);
         }
      },
      
      /**
       * This is updated by the [onPageWidgetsReady]{@link module:alfresco/documentlibrary/AlfDocumentList#onPageWidgetsReady}
       * function to be true when all widgets on the page have been loaded. It is used to block loading of 
       * data until the page is completely setup. This is done to avoid multiple data loads as other widgets
       * on the page publish the details of their initial state (which would otherwise trigger a call to
       * [loadData]{@link module:alfresco/documentlibrary/AlfDocumentList#onPageWidgetsReady})
       * 
       * @instance
       * @type {boolean}
       * @default false
       */
      _readyToLoad: false,

      /**
       * The AlfDocumentList is intended to work co-operatively with other widgets on a page to assist with
       * setting the data that should be retrieved. As related widgets are created and publish their initial
       * state they may trigger requests to load data. As such, data loading should not be started until
       * all the widgets on the page are ready.
       *
       * @instance
       * @param {object} payload
       */
      onPageWidgetsReady: function alfresco_documentlibrary_AlfDocumentList__onPageWidgetsReady(payload) {
         this._readyToLoad = true;
         if (this.useHash)
         {
            // Only subscribe to filter changes if 'useHash' is set to true. This is because multiple DocLists might
            // be required on the same page and they can't all feed off the hash to drive the location.
            this.alfSubscribe(this.filterChangeTopic, lang.hitch(this, "onChangeFilter"));

            // When using hashes (e.g. a URL fragment in the browser address bar) then we need to 
            // actually get the initial filter and use it to generate the first data set...
            this.initialiseFilter(); // Function provided by the _AlfHashMixin
         }
         else
         {
            // When not using a URL hash (e.g. because this DocList is being used as a secondary item - 
            // maybe as part of a picker, etc) then we need to load the initial data set using the instance
            // variables provided. We also need to subscribe to topics that indicate that the location has 
            // changed. Each view renderer that registers a link will need to set a "linkClickTopic" and this
            // should be matched by the "linkClickTopic" of this instance)
            this.alfSubscribe(this.linkClickTopic, lang.hitch(this, "onItemLinkClick"));
            if (this._readyToLoad) this.loadData();
         }
      },

      /**
       * Iterates over the widgets processed and calls the [registerView]{@link module:alfresco/documentlibrary/AlfDocumentList#registerView}
       * function with each one.
       * 
       * @instance
       * @param {object[]} The created widgets
       */
      allWidgetsProcessed: function alfresco_documentlibrary_AlfDocumentList__allWidgetsProcessed(widgets) {
         array.forEach(widgets, lang.hitch(this, "registerView"));

         // If no default view has been provided, then just use the first...
         if (this._currentlySelectedView == null)
         {
            for (view in this.viewMap)
            {
               this._currentlySelectedView = view;
               break;
            }
         }

         this.alfPublish(this.viewSelectionTopic, {
            value: this._currentlySelectedView
         });
      },
      
      /**
       * This is called from [allWidgetsProcessed]{@link module:alfresco/documentlibrary/AlfDocumentList#allWidgetsProcessed} for
       * each widget defined. Only widgets that inherit from [AlfDocumentListView]{@link module:alfresco/documentlibrary/views/AlfDocumentListView}
       * will be successfully registered.
       * 
       * @instance
       * @param {object} view The view to register
       * @param {number} index
       */
      registerView: function alfresco_documentlibrary_AlfDocumentList__registerView(view, index) {
         if (view.isInstanceOf(AlfDocumentListView))
         {
            this.alfLog("log", "Registering DocumentList view", view);
            
            // Get the menu item for selecting the view. We need to make sure that each view provides a menu item 
            // that can be used for selecting a view otherwise the end user will not be able to select it...
            var viewSelectionConfig = view.getViewSelectionConfig();
            if (viewSelectionConfig == null || !this.isValidViewSelectionConfig(viewSelectionConfig))
            {
               this.alfLog("error", "The following DocumentList view does not provide a valid selection menu item upon request", viewSelectionConfig);
            }
            else
            {
               // Attempt to retrieve a name for the view. If the name returned is null then it indicates
               // that the getViewName method has not been overridden or the abstract view has been used.
               // In this instance it is acceptable to use the registered index as the name.
               var viewName = view.getViewName();
               if (viewName == null)
               {
                  viewName = index;
               }

               // Create a new new menu item using the supplied configuration...
               viewSelectionConfig.value = viewName;
               
               // Check if this is the initially requested view...
               if (viewName == this.view)
               {
                  this._currentlySelectedView = viewName;
                  viewSelectionConfig.checked = true;
               }

               // Publish the additional controls...
               this.publishAdditionalControls(viewName, view);
               
               // Set the value of the publish topic...
               viewSelectionConfig.publishTopic = this.viewSelectionTopic;
               
               // Set a common group for the menu item...
               viewSelectionConfig.group = this.viewSelectionMenuItemGroup;
               
               // Create a new AlfCheckableMenuItem for selecting the view. This will then be published and any menus that have subscribed
               // to the topic defined by "selectionMenuItemTopic" should add the menu item. When the menu item is clicked it will publish
               // the selection on the topic defined by the "viewSelectionTopic" (to which this DocumentList instance subscribes) and the
               // new view will be rendered...
               var selectionMenuItem = new AlfCheckableMenuItem(viewSelectionConfig);
               
               // If the view meets all the required criteria then we can add it for selection...
               // Step 1: Publish the menu item so that the view can be selected
               this.alfPublish(this.selectionMenuItemTopic, {
                  menuItem: selectionMenuItem
               });
               
               // Step 2: Add the view to the map of known views...
               this.viewMap[viewName] = view;
            }
         }
         else
         {
            this.alfLog("warn", "The following widget was provided as a view, but it does not inherit from 'alfresco/documentlibrary/AlfDocumentListView'", view);
         }
      },
      
      /**
       * By default this just ensures that a label has been provided. However this function could be overridden to provide
       * more complete validation if there are specific requirements for view selection configuration.
       * 
       * @instance isValidViewSelectionConfig
       * @param {object} viewSelectionConfig The configuration to validate
       * @return {boolean} Either true or false depending upon the validity of the supplied configuration.
       */
      isValidViewSelectionConfig: function alfresco_documentlibrary_AlfDocumentList__isValidViewSelectionConfig(viewSelectionConfig) {
         return (viewSelectionConfig.label != null && viewSelectionConfig.label != "");
      },
      
      /**
       * Use to keeps track of the [view]{@link module:alfresco/documentlibrary/views/AlfDocumentListView} that is currently selected.
       * 
       * @instance
       * @type {string} 
       * @default null
       */
      _currentlySelectedView: null,
      
      /**
       * Used to keep track of the current data for rendering by [views]{@link module:alfresco/documentlibrary/views/AlfDocumentListView}.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      _currentData: null,
      
      /**
       * Handles requests to switch views. This is called whenever the [viewSelectionTopic]{@link module:alfresco/documentlibrary/_AlfDocumentListTopicMixin#viewSelectionTopic} 
       * topic is published on and expects a payload containing an attribute "value" which should map to a registered 
       * [view]{@link module:alfresco/documentlibrary/views/AlfDocumentListView}. The views are mapped against the index they were configured 
       * with so the value is expected to be an integer.
       * 
       * @instance
       * @param {object} payload The payload published on the view selection topic. 
       */
      onViewSelected: function alfresco_documentlibrary_AlfDocumentList__onViewSelected(payload) {
         if (this._currentData == null)
         {
            this.alfLog("warn", "There is no data to render a view with");
            this.showNoDataMessage();
         }
         else if (payload == null || payload.value == null)
         {
            this.alfLog("warn", "A request was made to select a view, but not enough information was provided", payload);
         }
         else if (this._currentlySelectedView == payload.value)
         {
            // The requested view is the current view. No action required.
         }
         else if (this.viewMap[payload.value] == null)
         {
            // An invalid view was requested. Each view will have been mapped to the order in which it occurred in the
            // the "widgets" array provided for the DocumentList. The payload value attribute should correspond to an
            // index from that array...
            this.alfLog("error", "A request was made to select a non-existent view. Requested view: ", payload.value, " from: ", this.viewMap);
         }
         else
         {
            // Just do some double-checking to be sure...
            if (typeof this.viewMap[payload.value].renderView === "function")
            {
               // Render the selected view...
               this._currentlySelectedView = payload.value;
               var newView = this.viewMap[payload.value];
               this.showRenderingMessage();
               newView.setData(this._currentData);
               newView.renderView();
               this.showView(newView);
            }
            else
            {
               this.alfLog("error", "A view was requested that does not define a 'renderView' function", this.viewMap[payload.value]);
            }
         }
      },
      
      /**
       * Gets the additional controls for a view and publishes them.
       * 
       * @instance
       * @param {string} viewName The name of the view
       * @param {object} view The view to get the controls for.
       */
      publishAdditionalControls: function alfresco_documentlibrary_AlfDocumentList__publishAdditionalControls(viewName, view) {
         // Get any new additional controls (check the map first)
         var newAdditionalControls = this.viewControlsMap[viewName];
         if (newAdditionalControls == null)
         {
            newAdditionalControls = view.getAdditionalControls();
            // Check for Array using: Object.prototype.toString.call( someVar ) === '[object Array]'
            this.viewControlsMap[viewName] = newAdditionalControls;
         }
         
         // Publish the new additional controls for anyone wishing to display them...
         if (newAdditionalControls != null)
         {
            this.alfPublish(this.dynamicallyAddWidgetTopic, {
               targetId: "DOCLIB_TOOLBAR",
               targetPosition: 0,
               widgets: newAdditionalControls
            });
         }
      },
      
      /**
       * Hides all the children of the supplied DOM node by applying the "share-hidden" CSS class to them.
       * 
       * @instance
       * @param {Element} targetNode The DOM node to hide the children of.
       */
      hideChildren: function alfresco_documentlibrary_AlfDocumentList__hideChildren(targetNode) {
         array.forEach(targetNode.children, function(node) {
            domClass.add(node, "share-hidden");
         });
      },
      
      /**
       * If there is no data to render a view with then this function will be called to update the DocumentList
       * view node with a message explaining the situation.
       * 
       * @instance
       */
      showNoDataMessage: function alfresco_documentlibrary_AlfDocumentList__showNoDataMessage() {
         this.hideChildren(this.domNode);
         domClass.remove(this.noDataNode, "share-hidden");
      },
      
      /**
       * This is called before a request to load more data is made so that the user is aware that data
       * is being asynchronously loaded.
       * 
       * @instance
       */
      showLoadingMessage: function alfresco_documentlibrary_AlfDocumentList__showLoadingMessage() {
         this.hideChildren(this.domNode);
         domClass.remove(this.dataLoadingNode, "share-hidden");
      },
      
      /**
       * This is called once data has been loaded but before the view rendering begins. This can be useful
       * when there is a lot of data and the view is complex to render so may not be instantaneous.
       * 
       * @instance
       */
      showRenderingMessage: function alfresco_documentlibrary_AlfDocumentList__showRenderingMessage() {
         this.hideChildren(this.domNode);
         domClass.remove(this.renderingViewNode, "share-hidden");
      },
      
      /**
       * @instance
       * @param {object} view The view to show
       */
      showView: function alfresco_documentlibrary_AlfDocumentList__showView(view) {
         this.hideChildren(this.domNode);
         if (this.viewsNode.children.length > 0)
         {
            this.viewsNode.removeChild(this.viewsNode.children[0]);
         }
         
         // Add the new view...
         domConstruct.place(view.domNode, this.viewsNode);
         domClass.remove(this.viewsNode, "share-hidden");
         
         // Tell the view that it's now on display...
         view.onViewShown();
      },
      
      /**
       * This function is called whenever a request is made to change the node that is displayed in the DocumentList.
       * It may not be possible to set a specific node if some form of filtering is in use (e.g. tags, categories, custom
       * filter, etc).
       * 
       * @instance onChangeDisplayedNode
       */
      onChangeDisplayedNode: function alfresco_documentlibrary_AlfDocumentList__onChangeDisplayedNode(payload) {
         // No action by default
      },
      
      /**
       * This function is called whenever a request is made to change the filtered view. This could be as changing the path
       * from the Document List root or by applying a filter (such as tag or category) to the entire Document List.
       * 
       * The filter to apply will be specified by a fragment identifier (#filter=) where the value is in the form 
       * <filter-type>|<filter-data> (e.g. "path|%25/Folder1"). 
       * 
       * Fixed filter ids are "path", "tag" and "category". Everything else is a custom filter id
       * 
       * Pagination of other filters is handled by adding &page=<page-number> to the end of the fragment
       * 
       * 
       * @instance
       * @param {object} payload
       */
      onChangeFilter: function alfresco_documentlibrary_AlfDocumentList__onChangeFilter(payload) {
         this.currentFilter = payload;
         if (this._readyToLoad) this.loadData();
      },
      
      /**
       * Makes a request to load data for the repository. The request is built by calling the
       * [buildDocListParams]{@link module:alfresco/documentlibrary/AlfDocumentList#buildDocListParams} function.
       * If the request is successful then then the [onDataLoadSuccess]{@link module:alfresco/documentlibrary/AlfDocumentList#onDataLoadSuccess}
       * function will be called. If the request fails then the 
       * [onDataLoadFailure]{@link module:alfresco/documentlibrary/AlfDocumentList#onDataLoadFailure} function will be called.
       * 
       * @instance
       */
      loadData: function alfresco_documentlibrary_AlfDocumentList__loadData() {
         this.showLoadingMessage(); // Commented out because of timing issues...

         var documentPayload = {
            path: this.currentPath,
            type: this.showFolders ? "all" : "documents",
            site: this.siteId,
            container: this.containerId,
            sortAscending: this.sortAscending,
            sortField: this.sortField,
            filter: this.currentFilter,
            libraryRoot: this.rootNode
         };

         if (this.usePagination)
         {
            documentPayload.page = this.currentPage;
            documentPayload.pageSize = this.currentPageSize;
         }
         if ((this.siteId == null || this.siteId == "") && this.nodeRef != null)
         {
            // Repository mode (don't resolve Site-based folders)
            documentPayload.nodeRef = this.nodeRef.toString();
         }

         // Set a response topic that is scoped to this widget...
         documentPayload.alfResponseTopic = this.pubSubScope + "ALF_RETRIEVE_DOCUMENTS_REQUEST";

         this.alfPublish("ALF_RETRIEVE_DOCUMENTS_REQUEST", documentPayload, true);
      },
      
      /**
       * Handles successful calls to get data from the repository.
       * 
       * @instance
       * @param {object} response The response object
       * @param {object} originalRequestConfig The configuration that was passed to the the [serviceXhr]{@link module:alfresco/core/CoreXhr#serviceXhr} function
       */
      onDataLoadSuccess: function alfresco_documentlibrary_AlfDocumentList__onDataLoadSuccess(payload) {
         this.alfLog("log", "Data Loaded", payload, this);
         
         for (var i = 0; i<payload.response.items.length; i++)
         {
            payload.response.items[i].jsNode = new JsNode(payload.response.items[i].node);
         }
         this._currentData = payload.response;
         
         // Publish the details of the loaded documents. The initial use case for this was to allow
         // the selected items menu to know how many items were available for selection but it
         // clearly has many other uses...
         this.alfPublish(this.documentsLoadedTopic, {
            documents: this._currentData.items,
            totalDocuments: this._currentData.totalRecords,
            startIndex: this._currentData.startIndex
         });
         
         // Publish the details of the metadata returned from the data request...
         if (this._currentData.metadata)
         {
            this.alfPublish(this.metadataChangeTopic, {
               node: this._currentData.metadata
            });
         }
         
         // Publish the details of the permissions for the current user. This will 
         // only be available when the a specific node is shown rather than a set
         // of results across multiple nodes (e.g. the result of a filter request)
         var permissions = null;
         if (this._currentData.metadata && 
             this._currentData.metadata.parent && 
             this._currentData.metadata.parent.permissions && 
             this._currentData.metadata.parent.permissions.user)
         {
            this.alfPublish(this.userAccessChangeTopic, {
               userAccess: this._currentData.metadata.parent.permissions.user
            });
         }
         
         // Re-render the current view with the new data...
         var view = this.viewMap[this._currentlySelectedView];
         if (view != null)
         {
            this.showRenderingMessage();
            view.setData(this._currentData);
            view.renderView();
            this.showView(view);
            
            // Force a resize of the sidebar container to take the new height of the view into account...
            this.alfPublish("ALF_RESIZE_SIDEBAR", {});
         }
      },
      
      /**
       * Handles failed calls to get data from the repository.
       * 
       * @instance
       * @param {object} response The response object
       * @param {object} originalRequestConfig The configuration that was passed to the the [serviceXhr]{@link module:alfresco/core/CoreXhr#serviceXhr} function
       */
      onDataLoadFailure: function alfresco_documentlibrary_AlfDocumentList__onDataLoadSuccess(response, originalRequestConfig) {
         this.alfLog("error", "Data Load Failed", response, originalRequestConfig);
         this._currentData = null;
         this.showNoDataMessage(); // TODO: This should probably be a different error message
      },
      
      /**
       * @instance
       * @type {object}
       * @default null
       */
      currentFilter: null,
      
      /**
       * The current page number being shown by the DocumentList
       * 
       * @instance
       * @type {number} 
       * @default 1
       */
      currentPage: 1,
      
      /**
       * The size (or number of items) to be shown on each page.
       * 
       * @instance
       * @type {number} 
       * @default 25
       */
      currentPageSize: 25,
      
      /**
       * The inital sort order.
       * 
       * @instance
       * @type {boolean}
       * @default true
       */
      sortAscending: true,

      /**
       * The initial field to sort results on.
       *
       * @instance
       * @type {string}
       * @default "cm:name"
       */
      sortField: "cm:name",

      /**
       * @instance
       * @param {object} payload The published details of the selected items
       */
      onDocumentSelection: function alfresco_documentlibrary_AlfDocumentList__onDocumentSelection(payload) {
         this.alfLog("log", "Documents Selected: ", payload);
         // TODO!
      },
      
      /**
       * @instance
       * @param {object} payload The details of the request
       */
      onSortRequest: function alfresco_documentlibrary_AlfDocumentList__onSortRequest(payload) {
         this.alfLog("log", "Sort requested: ", payload);
         if (payload && (payload.direction != null || payload.value != null))
         {
            if (payload.direction)
            {
               this.sortAscending = (payload.direction == "ascending");
            }
            if (payload.value)
            {
               this.sortField = payload.value;
            }
            if (this._readyToLoad) this.loadData();
         }
      },
      
      /**
       * @instance
       * @param {object} payload The details of the request
       */
      onSortFieldSelection: function alfresco_documentlibrary_AlfDocumentList__onSortFieldSelection(payload) {
         this.alfLog("log", "Sort field selected: ", payload);
         if (payload && payload.value != null)
         {
            this.sortField = payload.value;
            if (this._readyToLoad) this.loadData();
         }
      },
      
      /**
       * @instance
       * @param {object} payload The details of the request
       */
      onShowFolders: function alfresco_documentlibrary_AlfDocumentList__onShowFolders(payload) {
         this.alfLog("log", "Show Folders Request: ", payload);
         if (payload && payload.selected != null)
         {
            this.showFolders = payload.selected;
            if (this._readyToLoad) this.loadData();
         }
      },
      
      /**
       * @instance
       * @param {object} payload The details of the new page number
       */
      onPageChange: function alfresco_documentlibrary_AlfDocumentList__onPageChange(payload) {
         if (payload && payload.value != null && payload.value != this.currentPage)
         {
            this.currentPage = payload.value;
            if (this._readyToLoad) this.loadData();
         }
      },
      
      /**
       * @instance
       * @param {object} payload The details of the new page size
       */
      onDocsPerPageChange: function alfresco_documentlibrary_AlfDocumentList__onDocsPerPageChange(payload) {
         if (payload && payload.value != null && payload.value != this.currentPageSize)
         {
            // Need to check that there is enough data available for the current page!!! e.g. if we're on page 3 and requesting page 3 will not return any results
            // Is the total number of records less than the requested docs per page multiplied by 1 less than the current page...
            if (this.currentPage == 1)
            {
               // No need to worry, the first page will be requested
            }
            else if ((this._currentData.totalRecords - payload.value) < ((this.currentPage - 1) * payload.value))
            {
               // The current page will be too big, get the last page...
               this.currentPage = Math.ceil(this._currentData.totalRecords/payload.value);
            }
            else
            {
               // No need to worry. The current page is fine for the new page size...
            }
            
            this.currentPageSize = payload.value;
            if (this._readyToLoad) this.loadData();
         }
      }
   });
});