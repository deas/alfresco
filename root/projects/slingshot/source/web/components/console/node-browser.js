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
 *
 */

/**
 * ConsoleNodeBrowser tool component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ConsoleNodeBrowser
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Element = YAHOO.util.Element;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * ConsoleNodeBrowser constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ConsoleNodeBrowser} The new ConsoleNodeBrowser instance
    * @constructor
    */
   Alfresco.ConsoleNodeBrowser = function(htmlId)
   {
      this.name = "Alfresco.ConsoleNodeBrowser";
      Alfresco.ConsoleNodeBrowser.superclass.constructor.call(this, htmlId);
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("viewNodeClick", this.onViewNodeClick, this);

      /* Define panel handlers */
      var parent = this;
      
      // NOTE: the panel registered first is considered the "default" view and is displayed first
      
      /* Search Panel Handler */
      SearchPanelHandler = function SearchPanelHandler_constructor()
      {
         SearchPanelHandler.superclass.constructor.call(this, "search");
      };
      
      YAHOO.extend(SearchPanelHandler, Alfresco.ConsolePanelHandler,
      {
         /**
          * INSTANCE VARIABLES
          */

         /**
          * Keeps track if this panel is searching or not
          *
          * @property isSearching
          * @type Boolean
          */
         isSearching: false,

         /**
          * PANEL LIFECYCLE CALLBACKS
          */
         
         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onLoad
          */
         onLoad: function onLoad()
         {
            // Buttons
            parent.widgets.searchButton = Alfresco.util.createYUIButton(parent, "search-button", parent.onSearchClick);
            parent.widgets.nodeMenuButton = new YAHOO.widget.Button(parent.id + "-store-menu-button", { 
               type: "menu",
               menu: parent.id + "-store-menu-select"
            });
            
            // DataTable and DataSource setup
            parent.widgets.dataSource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI + "slingshot/node/search",
            {
               responseType: YAHOO.util.DataSource.TYPE_JSON,
               responseSchema:
               {
                  resultsList: "results",
                  metaFields:
                  {
                     recordOffset: "startIndex",
                     totalRecords: "totalResults"
                  }
               }
            });
            
            var me = this;
            
            // Work to be performed after data has been queried but before display by the DataTable
            parent.widgets.dataSource.doBeforeParseData = function PeopleFinder_doBeforeParseData(oRequest, oFullResponse)
            {
               var updatedResponse = oFullResponse;
               
               if (oFullResponse)
               {
                  var items = oFullResponse.results;
                  
                  // initial sort by username field
                  items.sort(function(a, b)
                  {
                     return (a.name > b.name);
                  });
                  
                  // we need to wrap the array inside a JSON object so the DataTable gets the object it expects
                  updatedResponse =
                  {
                     "results": items
                  };
               }
               
               // update Results Bar message with number of results found
               if (items.length < parent.options.maxSearchResults)
               {
                  me._setResultsMessage("message.results", $html(parent.searchTerm), items.length);
               }
               else
               {
                  me._setResultsMessage("message.maxresults", parent.options.maxSearchResults);
               }
               
               return updatedResponse;
            };
            
            // Setup the main datatable
            this._setupDataTable();
            
            // register the "enter" event on the search text field
            var searchText = Dom.get(parent.id + "-search-text");
            
            new YAHOO.util.KeyListener(searchText,
            {
               keys: YAHOO.util.KeyListener.KEY.ENTER
            },
            {
               fn: function() 
               {
                  parent.onSearchClick();
               },
               scope: parent,
               correctScope: true
            }, "keydown").enable();
         },
         
         onShow: function onShow()
         {
            Dom.get(parent.id + "-search-text").focus();
         },
         
         onUpdate: function onUpdate()
         {
            // update the text field - as this event could come from bookmark, navigation or a search button click
            var searchTermElem = Dom.get(parent.id + "-search-text");
            searchTermElem.value = parent.searchTerm;
            
            // check search length again as we may have got here via history navigation
            if (!this.isSearching && parent.searchTerm !== undefined && parent.searchTerm.length >= parent.options.minSearchTermLength)
            {
               this.isSearching = true;

               var me = this;
               
               // Reset the custom error messages
               me._setDefaultDataTableErrors(parent.widgets.dataTable);
               
               // Don't display any message
               parent.widgets.dataTable.set("MSG_EMPTY", parent._msg("message.searching"));
               
               // Empty results table
               parent.widgets.dataTable.deleteRows(0, parent.widgets.dataTable.getRecordSet().getLength());
               
               var successHandler = function ConsoleUsers__ps_successHandler(sRequest, oResponse, oPayload)
               {
                  me._enableSearchUI();                  
                  me._setDefaultDataTableErrors(parent.widgets.dataTable);
                  parent.widgets.dataTable.onDataReturnInitializeTable.call(parent.widgets.dataTable, sRequest, oResponse, oPayload);
               };
               
               var failureHandler = function ConsoleUsers__ps_failureHandler(sRequest, oResponse)
               {
                  me._enableSearchUI();
                  if (oResponse.status == 401)
                  {
                     // Our session has likely timed-out, so refresh to offer the login page
                     window.location.reload();
                  }
                  else
                  {
                     try
                     {
                        var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                        parent.widgets.dataTable.set("MSG_ERROR", response.message);
                        parent.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                        me._setResultsMessage("message.noresults");
                     }
                     catch(e)
                     {
                        me._setDefaultDataTableErrors(parent.widgets.dataTable);
                     }
                  }
               };

               // Send the query to the server
               parent.widgets.dataSource.sendRequest(me._buildSearchParams(parent.searchTerm, parent.searchLanguage),
               {
                  success: successHandler,
                  failure: failureHandler,
                  scope: parent
               });
               me._setResultsMessage("message.searchingFor", $html(parent.searchTerm));

               // Disable search button and display a wait feedback message if the users hasn't been found yet
               parent.widgets.searchButton.set("disabled", true);
               YAHOO.lang.later(2000, me, function(){
                  if (me.isSearching)
                  {
                     if (!me.widgets.feedbackMessage)
                     {
                      me.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
                      {
                         text: Alfresco.util.message("message.searching", parent.name),
                         spanClass: "wait",
                         displayTime: 0
                      });
                     }
                     else if (!me.widgets.feedbackMessage.cfg.getProperty("visible"))
                     {
                      me.widgets.feedbackMessage.show();
                     }
                  }
               }, []);
            }
         },

         /**
          * Enable search button, hide the pending wait message and set the panel as not searching.
          *
          * @method _enableSearchUI
          * @private
          */
         _enableSearchUI: function _enableSearchUI()
         {
            // Enable search button and close the wait feedback message if present
            if (this.widgets.feedbackMessage && this.widgets.feedbackMessage.cfg.getProperty("visible"))
            {
               this.widgets.feedbackMessage.hide();
            }
            parent.widgets.searchButton.set("disabled", false);
            this.isSearching = false;
         },

         /**
          * Setup the YUI DataTable with custom renderers.
          *
          * @method _setupDataTable
          * @private
          */
         _setupDataTable: function _setupDataTable()
         {
            /**
             * DataTable Cell Renderers
             *
             * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
             * These MUST be inline in order to have access to the parent instance (via the "parent" variable).
             */
            
            /**
             * Generic HTML-safe custom datacell formatter
             */
            var renderCellSafeHTML = function renderCellSafeHTML(elCell, oRecord, oColumn, oData)
            {
               elCell.innerHTML = $html(oData);
            };
            
            /**
             * Node name custom datacell formatter
             *
             * @method renderName
             */
            var renderNodeLink = function renderNodeLink(elCell, oRecord, oColumn, oData)
            {
               // Create view userlink
               var viewNodeLink = document.createElement("a");
               Dom.setAttribute(viewNodeLink, "href", "#");
               viewNodeLink.innerHTML = $html(oData);

               // fire the 'viewUserClick' event when the selected user in the list has changed
               YAHOO.util.Event.addListener(viewNodeLink, "click", function(e)
               {
                  YAHOO.util.Event.preventDefault(e);
                  YAHOO.Bubbling.fire('viewNodeClick',
                  {
                     nodeRef: oRecord.getData("nodeRef")
                  });
               }, null, parent);
               elCell.appendChild(viewNodeLink);
            };
            
            // DataTable column defintions
            var columnDefinitions =
            [
               { key: "name", label: parent._msg("label.name"), sortable: true, formatter: renderNodeLink },
               { key: "parentPath", label: parent._msg("label.parent_path"), sortable: true, formatter: renderCellSafeHTML },
               { key: "nodeRef", label: parent._msg("label.node-ref"), sortable: true, formatter: renderNodeLink }
            ];
            
            // DataTable definition
            parent.widgets.dataTable = new YAHOO.widget.DataTable(parent.id + "-datatable", columnDefinitions, parent.widgets.dataSource,
            {
               initialLoad: false,
               renderLoopSize: 32,
               sortedBy:
               {
                  key: "name",
                  dir: "asc"
               },
               MSG_EMPTY: parent._msg("message.empty")
            });
         },
         
         /**
          * Resets the YUI DataTable errors to our custom messages
          * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
          *
          * @method _setDefaultDataTableErrors
          * @param dataTable {object} Instance of the DataTable
          * @private
          */
         _setDefaultDataTableErrors: function _setDefaultDataTableErrors(dataTable)
         {
            dataTable.set("MSG_EMPTY", parent._msg("message.empty", "Alfresco.ConsoleUsers"));
            dataTable.set("MSG_ERROR", parent._msg("message.error", "Alfresco.ConsoleUsers"));
         },
         
         /**
          * Build URI parameters for People List JSON data webscript
          *
          * @method _buildSearchParams
          * @param searchTerm {string} User search term
          * @private
          */
         _buildSearchParams: function _buildSearchParams(searchTerm, searchLanguage)
         {
            return "?q=" + encodeURIComponent(searchTerm) + 
               "&lang=" + searchLanguage + 
               "&maxResults=" + parent.options.maxSearchResults;
         },
         
         /**
          * Set the message in the Results Bar area
          * 
          * @method _setResultsMessage
          * @param messageId {string} The messageId to display
          * @private
          */
         _setResultsMessage: function _setResultsMessage(messageId, arg1, arg2)
         {
            var resultsDiv = Dom.get(parent.id + "-search-bar");
            resultsDiv.innerHTML = parent._msg(messageId, arg1, arg2);
         },
         
         /**
          * Successfully applied options event handler
          *
          * @method onSuccess
          * @param response {object} Server response object
          */
         onSuccess: function OptionsPanel_onSuccess(response)
         {
            if (response && response.json)
            {
               if (response.json.success)
               {
                  // refresh the browser to force the themed components to reload
                  window.location.reload(true);
               }
               else if (response.json.message)
               {
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: response.json.message
                  });
               }
            }
            else
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: Alfresco.util.message("message.failure")
               });
            }
         }
      });
      new SearchPanelHandler();
      
      /* View Panel Handler */
      ViewPanelHandler = function ViewPanelHandler_constructor()
      {
         ViewPanelHandler.superclass.constructor.call(this, "view");
      };
      
      YAHOO.extend(ViewPanelHandler, Alfresco.ConsolePanelHandler,
      {
         onLoad: function onLoad()
         {
            // Buttons
            parent.widgets.gobackButton = Alfresco.util.createYUIButton(parent, "goback-button", parent.onGoBackClick);
            parent.widgets.deleteuserButton = Alfresco.util.createYUIButton(parent, "deleteuser-button", parent.onDeleteUserClick);
            parent.widgets.edituserButton = Alfresco.util.createYUIButton(parent, "edituser-button", parent.onEditUserClick);
         },
         
         onBeforeShow: function onBeforeShow()
         {
            // Hide the main panel area before it is displayed - so we don't show
            // old data to the user before the Update() method paints the results
            Dom.get(parent.id + "-view-title").innerHTML = "";
            Dom.setStyle(parent.id + "-view-main", "visibility", "hidden");
         },
         
         onShow: function onShow()
         {
            window.scrollTo(0, 0);
         },
         
         onUpdate: function onUpdate()
         {
            window.scrollTo(0, 0);
            var success = function(res)
            {
               var fnSetter = function(id, val)
               {
                  Dom.get(parent.id + id).innerHTML = val ? $html(val) : "";
               };
               
               var node = YAHOO.lang.JSON.parse(res.serverResponse.responseText),
                  nodeRef = node.nodeRef;
               
               /**
                * Node link custom datacell formatter
                *
                * @method renderName
                */
               var renderNodeLink = function renderNodeLink(elCell, oRecord, oColumn, oData)
               {
                  // Create view userlink
                  var viewNodeLink = document.createElement("a");
                  YAHOO.util.Dom.setAttribute(viewNodeLink, "href", "#");
                  viewNodeLink.innerHTML = $html(oData);

                  // fire the 'viewUserClick' event when the selected user in the list has changed
                  YAHOO.util.Event.addListener(viewNodeLink, "click", function(e)
                  {
                     YAHOO.util.Event.preventDefault(e);
                     YAHOO.Bubbling.fire('viewNodeClick',
                     {
                        nodeRef: oRecord.getData("nodeRef")
                     });
                  }, null, parent);
                  elCell.appendChild(viewNodeLink);
               };
               
               /**
                * Property value custom datacell formatter
                *
                * @method renderPropertyValue
                */
               var renderPropertyValue = function renderPropertyValue(elCell, oRecord, oColumn, oData)
               {
                  if (oRecord.getData("type") == "d:content")
                  {
                     // Create new link
                     var contentLink = document.createElement("a");
                     contentLink.innerHTML = $html(oData);
                     YAHOO.util.Dom.setAttribute(contentLink, "href", Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.replace("://", "/") + "/content");
                     elCell.appendChild(contentLink);
                  }
                  else
                  {
                     elCell.innerHTML = $html(oData);
                  }
               };

               Dom.get(parent.id + "-view-title").innerHTML = node.name;
               
               // About section fields
               fnSetter("-view-node-ref", node.nodeRef);
               fnSetter("-view-node-path", node.qnamePath);
               fnSetter("-view-node-type", node.type);

               Dom.get(parent.id + "-view-node-parent").innerHTML = "";
               // Add parent noderef link
               if (node.parent !== null)
               {
                  var nodeLink = document.createElement("a");
                  Dom.setAttribute(nodeLink, "href", "#");
                  nodeLink.innerHTML = $html(node.parentNodeRef);
                  YAHOO.util.Event.addListener(nodeLink, "click", function(e)
                  {
                     YAHOO.util.Event.preventDefault(e);
                     YAHOO.Bubbling.fire('viewNodeClick',
                     {
                        nodeRef: node.parentNodeRef
                     });
                  }, null, parent);
                  Dom.get(parent.id + "-view-node-parent").appendChild(nodeLink);
               }
               
               var propsDT = new YAHOO.widget.DataTable(parent.id + "-view-node-properties", 
                  [
                     { key: "name", label: parent.msg("label.properties-name") },
                     { key: "type", label: parent.msg("label.properties-type") },
                     { key: "value", label: parent.msg("label.properties-value"), formatter: renderPropertyValue }
                  ], 
                  new YAHOO.util.LocalDataSource(node.properties)
               );
               
               var aspects = "";
               for ( var i = 0; i < node.aspects.length; i++)
               {
                  aspects += (i != 0 ? "<br />" : "") + $html(node.aspects[i]);
               }
               Dom.get(parent.id + "-view-node-aspects").innerHTML = aspects;
               
               var childrenDT = new YAHOO.widget.DataTable(parent.id + "-view-node-children", 
                  [
                     { key: "name", label: parent.msg("label.children-name"), formatter: renderNodeLink },
                     { key: "type", label: parent.msg("label.children-type") },
                     { key: "nodeRef", label: parent.msg("label.children-node-ref"), formatter: renderNodeLink },
                     { key: "assocType", label: parent.msg("label.children-assoc-type") }
                  ], 
                  new YAHOO.util.LocalDataSource(node.children)
               );

               var parentsDT = new YAHOO.widget.DataTable(parent.id + "-view-node-parents", 
                   [
                     { key: "name", label: parent.msg("label.parents-name"), formatter: renderNodeLink },
                     { key: "type", label: parent.msg("label.parents-type") },
                     { key: "nodeRef", label: parent.msg("label.parents-node-ref"), formatter: renderNodeLink },
                     { key: "assocType", label: parent.msg("label.parents-assoc-type") }
                  ], 
                  new YAHOO.util.LocalDataSource(node.parents)
               );

               var assocsDT = new YAHOO.widget.DataTable(parent.id + "-view-node-assocs", 
                  [
                     { key: "name", label: parent.msg("label.assocs-name"), formatter: renderNodeLink },
                     { key: "type", label: parent.msg("label.assocs-type") },
                     { key: "nodeRef", label: parent.msg("label.assocs-node-ref"), formatter: renderNodeLink },
                     { key: "assocType", label: parent.msg("label.assocs-assoc-type") }
                  ], 
                  new YAHOO.util.LocalDataSource(node.assocs)
               );
               
               var sourceAssocsDT = new YAHOO.widget.DataTable(parent.id + "-view-node-source-assocs", 
                  [
                     { key: "name", label: parent.msg("label.source-assocs-name"), formatter: renderNodeLink },
                     { key: "type", label: parent.msg("label.source-assocs-type") },
                     { key: "nodeRef", label: parent.msg("label.source-assocs-node-ref"), formatter: renderNodeLink },
                     { key: "assocType", label: parent.msg("label.source-assocs-assoc-type") }
                  ], 
                  new YAHOO.util.LocalDataSource(node.sourceAssocs)
               );
               
               var permissionsDT = new YAHOO.widget.DataTable(parent.id + "-view-node-permissions", 
                  [
                     { key: "permission", label: parent.msg("label.permissions-permission") },
                     { key: "authority", label: parent.msg("label.permissions-authority") },
                     { key: "rel", label: parent.msg("label.permissions-access") }
                  ], 
                  new YAHOO.util.LocalDataSource(node.permissions.entries)
               );
               fnSetter("-view-node-inherits-permissions", "" + node.permissions.inherit);
               fnSetter("-view-node-owner", node.permissions.owner);
               
               // Make main panel area visible
               Dom.setStyle(parent.id + "-view-main", "visibility", "visible");
            };
            
            // make an ajax call to get user details
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "slingshot/node/" + parent.currentNodeRef.replace("://", "/"),
               method: Alfresco.util.Ajax.GET,
               successCallback:
               {
                  fn: success,
                  scope: parent
               },
               failureMessage: parent._msg("message.getnode-failure", $html(parent.currentUserId))   
            });
         },
         
         /**
          * View Node event handler
          *
          * @method onNodeClick
          * @param e {object} DomEvent
          * @param args {array} Event parameters (depends on event type)
          */
         onNodeClick: function ConsoleUsers_onNodeClick(e, args)
         {
            var nodeRef = args[1].nodeRef;
            this.refreshUIState({"panel": "view", "nodeRef": nodeRef});
         }
      });
      new ViewPanelHandler();
      
      return this;
   };
   
   YAHOO.extend(Alfresco.ConsoleNodeBrowser, Alfresco.ConsoleTool,
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
          * Number of characters required for a search.
          * 
          * @property minSearchTermLength
          * @type int
          * @default 1
          */
         minSearchTermLength: 1,
         
         /**
          * Maximum number of items to display in the results list
          * 
          * @property maxSearchResults
          * @type int
          * @default 100
          */
         maxSearchResults: 100
      },
      
      /**
       * Current node ref if viewing a node.
       * 
       * @property currentNodeRef
       * @type string
       */
      currentNodeRef: "",
      
      /**
       * Current search term, obtained from form input field.
       * 
       * @property searchTerm
       * @type string
       */
      searchTerm: undefined,
      
      /**
       * Current search language, obtained from drop-down.
       * 
       * @property searchLanguage
       * @type string
       */
      searchLanguage: "fts-alfresco",
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function ConsoleNodeBrowser_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ConsoleNodeBrowser_onReady()
      {
         // Call super-class onReady() method
         Alfresco.ConsoleNodeBrowser.superclass.onReady.call(this);
      },
      
      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * History manager state change event handler (override base class)
       *
       * @method onStateChanged
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onStateChanged: function ConsoleUsers_onStateChanged(e, args)
      {
         var state = this.decodeHistoryState(args[1].state);
         
         // test if panel has actually changed?
         if (state.panel)
         {
            this.showPanel(state.panel);
         }
         
         if (state.search !== undefined && this.currentPanelId === "search")
         {
            // keep track of the last search performed
            var searchTerm = state.search;
            this.searchTerm = searchTerm;
            
            this.updateCurrentPanel();
         }
         
         if (state.nodeRef &&
             (this.currentPanelId === "view"))
         {
            this.currentNodeRef = state.nodeRef;
            
            this.updateCurrentPanel();
         }
      },
      
      /**
       * Search button click event handler
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onSearchClick: function ConsoleUsers_onSearchClick(e, args)
      {
         var searchTermElem = Dom.get(this.id + "-search-text");
         var searchTerm = YAHOO.lang.trim(searchTermElem.value);
         
         // inform the user if the search term entered is too small
         if (searchTerm.length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.minimum-length", this.options.minSearchTermLength)
            });
            return;
         }
         
         this.refreshUIState({"search": searchTerm});
      },
      
      /**
       * View Node event handler
       *
       * @method onViewNodeClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onViewNodeClick: function ConsoleUsers_onViewNodeClick(e, args)
      {
         var nodeRef = args[1].nodeRef;
         this.refreshUIState({"panel": "view", "nodeRef": nodeRef});
      },

      /**
       * Go back button click event handler
       *
       * @method onGoBackClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onGoBackClick: function ConsoleUsers_onGoBackClick(e, args)
      {
         this.refreshUIState({"panel": "search"});
      },
      
      /**
       * Encode state object into a packed string for use as url history value.
       * Override base class.
       * 
       * @method encodeHistoryState
       * @param obj {object} state object
       * @private
       */
      encodeHistoryState: function ConsoleUsers_encodeHistoryState(obj)
      {
         // wrap up current state values
         var stateObj = {};
         if (this.currentPanelId !== "")
         {
            stateObj.panel = this.currentPanelId;
         }
         if (this.currentNodeRef !== "")
         {
            stateObj.nodeRef = this.currentNodeRef;
         }
         if (this.searchTerm !== undefined)
         {
            stateObj.search = this.searchTerm;
         }
         
         // convert to encoded url history state - overwriting with any supplied values
         var state = "";
         if (obj.panel || stateObj.panel)
         {
            state += "panel=" + encodeURIComponent(obj.panel ? obj.panel : stateObj.panel);
         }
         if (obj.nodeRef || stateObj.nodeRef)
         {
            if (state.length !== 0)
            {
               state += "&";
            }
            state += "nodeRef=" + encodeURIComponent(obj.nodeRef ? obj.nodeRef : stateObj.nodeRef);
         }
         if (obj.search !== undefined || stateObj.search !== undefined)
         {
            if (state.length !== 0)
            {
               state += "&";
            }
            state += "search=" + encodeURIComponent(obj.search !== undefined ? obj.search : stateObj.search);
         }
         return state;
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function ConsoleNodeBrowser__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.ConsoleNodeBrowser", Array.prototype.slice.call(arguments).slice(1));
      }
   });
})();