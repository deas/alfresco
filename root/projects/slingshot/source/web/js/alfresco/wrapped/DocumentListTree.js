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
 * @module alfresco/wrapped/DocumentListTree
 * @extends module:alfresco/core/WrappedShareWidget
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/documentlibrary/_AlfDocumentListTopicMixin
 * @author Dave Draper
 * @deprecated
 */
define(["dojo/_base/declare",
        "alfresco/core/WrappedShareWidget", 
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/DocumentListTree.html",
        "alfresco/documentlibrary/_AlfDocumentListTopicMixin",
        "dojo/_base/lang",
        "dojo/dom",
        "dojo/dom-construct",
        "dojo/dom-style"], 
        function(declare, WrappedShareWidget, _TemplatedMixin, template, _AlfDocumentListTopicMixin, lang, dom, domConstruct, domStyle) {
   
   return declare([WrappedShareWidget, _TemplatedMixin, _AlfDocumentListTopicMixin], {

      /**
       * The CSS file used by the DocumentList Tree
       * 
       * @instance
       * @type {object[]}
       */
      cssRequirements: [{cssFile:"/components/documentlibrary/tree.css"},
                        {cssFile:"./css/DocumentListTree.css"}],
      
      /**
       * This has to be set the same as the wrapped widget in order for message to be successfully retrieved.
       * This then ensures that the correct scope is set so that the messages can be retrieved as usual. 
       * @instance
       * @type {string}
       * @default "Alfresco.DocListTree"
       */
      i18nScope: "Alfresco.DocListTree",
             
      /**
       * Specifies the properties file from the WebScript that is used to instantiate the widget. It's only necessary
       * to specify the default property file - the Dojo dependency handler will sort out the locale as necessary
       * @instance
       * @type {object[]}
       */
      i18nRequirements: [{i18nFile: "/WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/documentlibrary/tree.get.properties"}],
      
      /**
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * The JavaScript file referenced by the DocumentList Tree
       * @instance
       */
      nonAmdDependencies: ["/components/documentlibrary/tree.js",
                           "/components/documentlibrary/repo-tree.js"],
      /**
       * @instance
       * @type {object}
       * @default null
       */
      templateMessages: null,
      
      /**
       * The constructor is extended so that we can construct an object containing all the i18n properties to 
       * be substituted by the template. This is because the template can't call functions to obtain data.
       * There are potentially better ways of doing this - but it does at least work.
       * @instance
       */
      constructor: function alfresco_wrapped_DocumentListTree__constructor(args) {
         declare.safeMixin(this, args);
         this.templateMessages = {
            header_library: this.message("header.library")
         };
      },
      
      /**
       * @instance
       * @type {string}
       * @default null
       */
      siteId: null,
      
      /**
       * @instance
       * @type {string}
       * @default null
       */
      containerId: null,
      
      /**
       * Creates an "Alfresco.RepositoryDocListTree" instance and connects all of the associated events to and from it.
       * 
       * @instance
       */
      postCreate: function alfresco_wrapped_DocumentListTree__postCreate() {
         
         // It is necessary to capture some events that are fired from the wrapped tree widget.
         // Ideally we wouldn't be using the YAHOO Bubbling library - this is only being used because
         // the wrapped DocumentList widget is using it.
         YAHOO.Bubbling.on("metadataRefresh", this.onLegacyMetadataRefresh, this);
         YAHOO.Bubbling.on("changeFilter", this.onLegacyChangeFilter, this);
         
         this.alfSubscribe(this.filterChangeTopic, lang.hitch(this, "onFilterChanged"));
         
//         // TODO: Need to fire these as necessary...
//         YAHOO.Bubbling.on("folderCopied", this.onFolderCopied, this);
//         YAHOO.Bubbling.on("folderCreated", this.onFolderCreated, this);
//         YAHOO.Bubbling.on("folderDeleted", this.onFolderDeleted, this);
//         YAHOO.Bubbling.on("folderMoved", this.onFolderMoved, this);
//         YAHOO.Bubbling.on("folderRenamed", this.onFolderRenamed, this);
//         YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
         
//         // TODO: Connect these as necessary
//         this.alfSubscribe("ALF_DOCLIST_FILE_SELECTION", lang.hitch(this, "onFileSelection"));
//         this.alfSubscribe("ALF_DOCLIST_SHOW_FOLDERS", lang.hitch(this, "onShowFolders"));
//         this.alfSubscribe("ALF_DOCLIST_SELECT_VIEW", lang.hitch(this, "onViewSelect"));
//         this.alfSubscribe("ALF_DOCLIST_SORT", lang.hitch(this, "onSortRequest"));
//         this.alfSubscribe("ALF_DOCLIST_SORT_FIELD_SELECTION", lang.hitch(this, "onSortFieldSelection"));
//         this.alfSubscribe("ALF_DOCLIST_FILTER_CHANGED", lang.hitch(this, "onWrappedFilterChanged"));
//         this.alfSubscribe("ALF_NODE_CREATED", lang.hitch(this, "onNodeCreated"));
//         this.alfSubscribe("ALF_NODE_RESIZED", lang.hitch(this, "onResizeEvent"));
         
         // TODO: It should be possible to toggle between the wrapping the Repo and Site trees...
         // TODO: Currently this does not respond to events because the tree responds to the YUI event model - we could still stub out the event model though.
         if (this.siteId == null)
         {
            // Create the "Alfresco.RepositoryDocListTree" widget that is to be wrapped...
            this._tree = new Alfresco.RepositoryDocListTree(this.id).setOptions({
               rootNode: this.rootNode,
               evaluateChildFolders: this.evaluateChildFolders,
               maximumFolderCount: this.maximumFolderCount,
               setDropTargets: this.setDropTargets
            });
         }
         else
         {
            // Create the "Alfresco.RepositoryDocListTree" widget that is to be wrapped...
            this._tree = new Alfresco.DocListTree(this.id).setOptions({
               siteId: this.siteId,
               containerId: this.containerId,
               evaluateChildFolders: this.evaluateChildFolders,
               maximumFolderCount: this.maximumFolderCount,
               setDropTargets: this.setDropTargets
            });
         }
      },
      
      /**
       * This function handles YUI events fired out of the wrapped tree and then converts them into 
       * the appropriate publications that Dojo based widgets can interpret. Detection of this event
       * should result in a request to reload the DocumentList
       * 
       * @instance
       */
      onLegacyMetadataRefresh: function alfresco_wrapped_DocumentListTree__onLegacyMetadataRefresh(layer, args) {
         this.alfPublish(this.reloadDataTopic, {});
      },
      
      /**
       * This function handles YUI events fired out of the wrapped tree and then converts them into the
       * appropriate publications that Dojo based widgets can interpret. Detection of this event should
       * result in the filter (e.g. the displayed DocumentList location) being updated.
       * 
       * @instance
       */
      onLegacyChangeFilter: function alfresco_wrapped_DocumentListTree__onLegacyChangeFilter(layer, args) {
         var obj = args[1];
         if (obj != null && obj.filterId && obj.filterData)
         {
            this.alfPublish("ALF_NAVIGATE_TO_PAGE", {
               url: "filter=" + obj.filterId + "|" + obj.filterData + "|",
               type: "HASH"
            });
//            this.alfPublish(this.filterChangeTopic, obj);
         }
      },
      
      /**
       * 
       * @instance
       */
      onFilterChanged: function alfresco_wrapped_DocumentListTree__onFilterChanged(payload) {
         this.alfLog("log", "Filter change detected", payload);
         if (payload != null)
         {
            // Pass the payload in an array as that is how the DocumentList widget expects to receive the
            // filter data...
            this.alfLog("info", "Updating DocumentListTree with new filter:", payload);
            YAHOO.Bubbling.fire("filterChanged", payload);
         }
         else
         {
            this.alfLog("warn", "The current filter matches the published change:", this._currentFilter, payload);
         }
      }
      
   });
});