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
 * RecordsEvents tool component.
 *
 * RM Admin ui for creating, editing and deleting events.
 *
 * @namespace Alfresco
 * @class Alfresco.RecordsEvents
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
    * RecordsEvents constructor.
    *
    * @param {String} htmlId The HTML id üof the parent element
    * @return {Alfresco.RecordsEvents} The new RecordsEvents instance
    * @constructor
    */
   Alfresco.RecordsEvents = function(htmlId)
   {
      this.name = "Alfresco.RecordsEvents";
      Alfresco.RecordsEvents.superclass.constructor.call(this, htmlId);

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("viewEvents", this.onViewEvents, this);
      YAHOO.Bubbling.on("createEvent", this.onCreateEvent, this);
      YAHOO.Bubbling.on("editEvent", this.onEditEvent, this);

      /* Define panel handlers */
      var parent = this;

      // NOTE: the panel registered first is considered the "default" view and is displayed first

      /* View Panel Handler */
      ViewPanelHandler = function ViewPanelHandler_constructor()
      {
         // Initialise prototype properties
         ViewPanelHandler.superclass.constructor.call(this, "view");
      };

      YAHOO.extend(ViewPanelHandler, Alfresco.ConsolePanelHandler,
      {
         /**
          /**
           * Remembers if the panel data has been loaded once
           *
           * @property initialDataLoaded
           * @type boolean
          */
         initialDataLoaded: false,

         /**
          * onLoad ConsolePanel event handler
          *
          * @method onLoad
          */
         onLoad: function ViewPanelHandler_onLoad()
         {
            // widgets
            this.widgets.newEventButton = Alfresco.util.createYUIButton(this, "newevent-button", this.onNewEventClick, {}, parent.id + "-newevent-button");

            // Setup data table
            this._setupDataSource();
            this._setupDataTable();
         },

         /**
          * Setup the datasource for the data table
          *
          * @method _setupDataSource
          * @private
          */
         _setupDataSource: function ViewPanelHandler__setupDataSource()
         {
            // DataSource definition
            var uriSearchResults = Alfresco.constants.PROXY_URI + "api/rma/admin/rmevents";
            this.widgets.dataSource = new YAHOO.util.DataSource(uriSearchResults,
            {
               responseType: YAHOO.util.DataSource.TYPE_JSON,
               connXhrMode: "queueRequests",
               responseSchema:
               {
                   resultsList: "data"
               }
            });
            this.widgets.dataSource.doBeforeParseData = function RecordsEvents_doBeforeParseData(oRequest , oFullResponse)
            {
               if (oFullResponse && oFullResponse.data)
               {
                  var items = oFullResponse.data;

                  // Create an array instead of an object
                  var events = [];
                  for (var key in items)
                  {
                     events.push(items[key]);
                  }

                  // Sort the events by their label
                  events.sort(function (event1, event2)
                  {
                     return (event1.eventDisplayLabel > event2.eventDisplayLabel) ? 1 : (event1.eventDisplayLabel < event2.eventDisplayLabel) ? -1 : 0;
                  });

                  // we need to wrap the array inside a JSON object so the DataTable is happy
                  return {
                     data: events
                  };
               }
               return oFullResponse;
            };
         },

         /**
          * Setup the datatable
          *
          * @method _setupDataTable
          * @private
          */
         _setupDataTable: function ViewPanelHandler__setupDataTable()
         {
            // Save reference for future callbacks
            var me = this;

            /**
             * Description/detail custom datacell formatter
             *
             * @method renderCellDetails
             * @param elCell {object}
             * @param oRecord {object}
             * @param oColumn {object}
             * @param oData {object|string}
             */
            renderCellDetails = function ViewPanelHandler__setupDataTable_renderCellDetails(elCell, oRecord, oColumn, oData)
            {
               var title = document.createElement("h4");
               title.appendChild(document.createTextNode(oRecord.getData("eventDisplayLabel")));
               elCell.appendChild(title);

               var info = document.createElement("div");
               elCell.appendChild(info);
               
               var typeLabel = document.createElement("span");
               Dom.addClass(typeLabel, "event-type-label");
               typeLabel.appendChild(document.createTextNode(parent.msg("label.type") + ":"));
               info.appendChild(typeLabel);

               var typeValue = document.createElement("span");
               typeValue.appendChild(document.createTextNode(parent.options.eventTypes[oRecord.getData("eventType")]));
               info.appendChild(typeValue);
            };

            /**
             * Actions custom datacell formatter
             *
             * @method renderCellActions
             * @param elCell {object}
             * @param oRecord {object}
             * @param oColumn {object}
             * @param oData {object|string}
             */
            renderCellActions = function ViewPanelHandler__setupDataTable_renderCellActions(elCell, oRecord, oColumn, oData)
            {
               // Edit event button
               var editBtn = new YAHOO.widget.Button(
               {
                  container: elCell,
                  label: parent.msg("button.edit")
               });
               editBtn.on("click", me.onEditEventClick, oRecord, me);

               // Delete event button
               var deleteBtn = new YAHOO.widget.Button(
               {
                  container: elCell,
                  label: parent.msg("button.delete")
               });
               deleteBtn.on("click", me.onDeleteEventClick, oRecord, me);
            };

            // DataTable column defintions
            var columnDefinitions =
                  [
                     {
                        key: "event", label: "Event", sortable: false, formatter: renderCellDetails
                     },
                     {
                        key: "actions", label: "Actions", formatter: renderCellActions
                     }
                  ];

            // DataTable definition
            this.widgets.dataTable = new YAHOO.widget.DataTable(parent.id + "-events", columnDefinitions, this.widgets.dataSource,
            {
               renderLoopSize: 32,
               initialLoad: false,
               MSG_EMPTY: parent.msg("message.loadevents.loading")
            });

            // Override abstract function within DataTable to set custom error message
            this.widgets.dataTable.doBeforeLoadData = function RecordsEvents_doBeforeLoadData(sRequest, oResponse, oPayload)
            {
               if (oResponse.error)
               {
                  try
                  {
                     var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                     this.widgets.dataTable.set("MSG_ERROR", response.message);
                  }
                  catch(e)
                  {
                     me._setDefaultDataTableErrors(me.widgets.dataTable);
                  }
               }
               else if (oResponse.results)
               {
                  if (oResponse.results.length === 0)
                  {
                     me.widgets.dataTable.set("MSG_EMPTY", '<span style="white-space: nowrap;">' + parent.msg("message.empty") + '</span>');
                  }
                  me.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko === 1.8) ? 3 : 5;
               }

               // Must return true to have the "Searching..." message replaced by the error message
               return true;
            };
         },

         /**
          * Load the events
          *
          * @method _loadEvents
          * @private
          */
         _loadEvents: function ViewPanelHandler__loadEvents()
         {

            // Reset the custom error messages
            this._setDefaultDataTableErrors(this.widgets.dataTable);

            // empty results table
            this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());

            var successHandler = function ViewPanelHandler__loadEvents_successHandler(sRequest, oResponse, oPayload)
            {
               // Stop list from getting refreshed when not needed
               this.initialDataLoaded = true;

               // Display new data
               this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
            };

            var failureHandler = function ViewPanelHandler__loadEvents_failureHandler(sRequest, oResponse)
            {
               if (oResponse.status == 401)
               {
                  // Our session has likely timed-out, so refresh to offer the login page
                  window.location.reload();
               }
               else
               {
                  try
                  {
                     // Display error message in datatable
                     var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                     this.widgets.dataTable.set("MSG_ERROR", response.message);
                     this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                  }
                  catch(e)
                  {
                     this._setDefaultDataTableErrors(this.widgets.dataTable);
                  }
               }
            };

            // Make the request for the events
            this.widgets.dataSource.sendRequest("",
            {
               success: successHandler,
               failure: failureHandler,
               scope: this
            });
         },

         /**
          * Resets the YUI DataTable errors to our custom messages
          *
          * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
          *
          * @method _setDefaultDataTableErrors
          * @param dataTable {object} Instance of the DataTable
          */
         _setDefaultDataTableErrors: function ViewPanelHandler__setDefaultDataTableErrors(dataTable)
         {
            dataTable.set("MSG_EMPTY", parent.msg("message.empty"));
            dataTable.set("MSG_ERROR", parent.msg("message.error"));
         },

         /**
          * Edit Eventbutton click handler
          *
          * @method onEditEventClick
          * @param e {object} DomEvent
          * @param oRecord {object} Object passed back from addListener method
          */
         onEditEventClick: function ViewPanelHandler_onEditEventClick(e, oRecord)
         {
            // update the current event
            YAHOO.Bubbling.fire("editEvent",
            {
               eventName: oRecord.getData("eventName")
            });
         },

         /**
          * Delete Event button click handler.
          * Displays a confirmation dialog before the deletion is made.
          *
          * @method onDeleteEventClick
          * @param e {object} DomEvent
          * @param oRecord {object} Object passed back from addListener method
          */
         onDeleteEventClick: function ViewPanelHandler_onDeleteEventClick(e, oRecord)
         {
            // Display confirm delete dialog
            var me = this;
            var text = parent.msg("message.confirm.removeevent.text", oRecord.getData("eventDisplayLabel"));
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: parent.msg("message.confirm.removeevent.title"),
               text: text,
               buttons: [
                  {
                     text: parent.msg("button.yes"),
                     handler: function ViewPanelHandler_onDeleteEventClick_confirmYes()
                     {
                        this.destroy();
                        me._deleteEvent.call(me, oRecord);
                     }
                  },
                  {
                     text: parent.msg("button.no"),
                     handler: function ViewPanelHandler_onDeleteEventClick_confirmNo()
                     {
                        this.destroy();
                     },
                     isDefault: true
                  }]
            });
         },

         /**
          * Delete the event
          *
          * @mthod _deleteEvent
          * @param oRecord {object} The event
          */
         _deleteEvent: function ViewPanelHandler__deleteEvent(oRecord)
         {
            // Delete event from server
            Alfresco.util.Ajax.jsonDelete(
            {
               url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmevents/" + oRecord.getData("eventName"),
               successCallback:
               {
                  fn: function(o)
                  {
                     // Reload the events
                     this._loadEvents();

                     // Display success message
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: parent.msg("message.removeevent.success")
                     });
                  },
                  scope: this
               },
               failureMessage: parent.msg("message.removeevent.failure")
            });
         },

         /**
          * onUpdate ConsolePanel event handler
          *
          * @method onUpdate
          */
         onUpdate: function onUpdate()
         {
            if (!this.initialDataLoaded || parent.refresh)
            {
               this._loadEvents();
            }
         },

         /**
          * New event button click handler
          *
          * @method onNewEventClick
          * @param e {object} DomEvent
          * @param obj {object} Object passed back from addListener method
          */
         onNewEventClick: function ViewPanelHandler_onNewEventClick(e, obj)
         {
            // Send avenet so the create panel will be displayed
            YAHOO.Bubbling.fire("createEvent", {});
         }
      });
      new ViewPanelHandler();

      /* Edit Metadata Panel Handler */
      EditPanelHandler = function EditPanelHandler_constructor()
      {
         // Initialise prototype properties

         EditPanelHandler.superclass.constructor.call(this, "edit");
      };

      YAHOO.extend(EditPanelHandler, Alfresco.ConsolePanelHandler,
      {
         /**
          * onLoad ConsolePanel event handler
          *
          * @method onLoad
          */
         onLoad: function EditPanelHandler_onLoad()
         {
            // Buttons
            this.widgets.saveButton = Alfresco.util.createYUIButton(parent, "save-button", null, {
               type: "submit"
            });
            this.widgets.cancelButton = Alfresco.util.createYUIButton(parent, "cancel-button", this.onCancelButtonClick);

            // Form definition
            var form = new Alfresco.forms.Form(parent.id + "-edit-form");
            form.setSubmitElements(this.widgets.saveButton);
            form.setShowSubmitStateDynamically(true);

            // Form field validation
            form.addValidation(parent.id + "-eventDisplayLabel", Alfresco.forms.validation.mandatory, null, "keyup");

            form.doBeforeFormSubmit =
            {
               fn: function(formEl)
               {
                  // Disable buttons during submit
                  this.widgets.saveButton.set("disabled", true);
                  this.widgets.cancelButton.set("disabled", true);

                  // Display a pengding message
                  this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
                  {
                     text: parent.msg("message.saveevent.saving"),
                     spanClass: "wait",
                     displayTime: 0
                  });
               },
               scope: this
            };

            // Submit as an ajax submit (not leave the page), in json format
            form.setAJAXSubmit(true,
            {
               successCallback:
               {
                  fn: function(serverResponse, obj)
                  {
                     // Hide pending dialog and enable buttons
                     this.widgets.feedbackMessage.destroy();
                     this.widgets.saveButton.set("disabled", false);
                     this.widgets.cancelButton.set("disabled", false);

                     // Display the events list again, and refresh data since we change it
                     YAHOO.Bubbling.fire('viewEvents',
                     {
                        refresh: true
                     });

                  },
                  scope: this
               },
               failureCallback:
               {
                  fn: function(serverResponse, obj)
                  {
                     // Hide pending dialog and enable buttons
                     this.widgets.feedbackMessage.destroy();
                     this.widgets.saveButton.set("disabled", false);
                     this.widgets.cancelButton.set("disabled", false);
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        text: parent.msg("message.saveevent.failure")
                     });
                  },
                  scope: this
               }
            });
            form.setSubmitAsJSON(true);

            // Initialise the form
            form.init();

            this.widgets.editForm = form;
         },

         /**
          * onBeforeShow ConsolePanel event handler
          *
          * @method onBeforeShow
          */
         onBeforeShow: function EditPanelHandler_onBeforeShow()
         {
            var editFormEl = Dom.get(parent.id + "-edit-form");
            if (parent.eventName && parent.eventName != "")
            {
               // Existing event, prepare form
               this.widgets.editForm.setAjaxSubmitMethod(Alfresco.util.Ajax.PUT);
               editFormEl.attributes.action.nodeValue = Alfresco.constants.PROXY_URI_RELATIVE + "api/rma/admin/rmevents/" + parent.eventName;
               Dom.removeClass(parent.id + "-edit-title", "hidden");
               Dom.addClass(parent.id + "-create-title", "hidden");

               // Load event data
               Alfresco.util.Ajax.jsonGet(
               {
                  url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmevents/" + parent.eventName,
                  successCallback:
                  {
                     fn: function(serverResponse)
                     {
                        // Apply current event values to form
                        if (serverResponse.json)
                        {
                           var event = serverResponse.json.data;
                           Dom.get(parent.id + "-eventName").value = (event.eventName) ? event.eventName : "";
                           Dom.get(parent.id + "-eventDisplayLabel").value = (event.eventDisplayLabel) ? event.eventDisplayLabel : "";
                           var eventTypeEl = Dom.get(parent.id + "-eventType");
                           Alfresco.util.setSelectedIndex(eventTypeEl, event.eventType);
                        }
                     },
                     scope: this
                  },
                  failureMessage: parent.msg("message.loadevent.failure")
               });
            }
            else
            {
               // New event, clear the form
               this.widgets.editForm.setAjaxSubmitMethod(Alfresco.util.Ajax.POST);
               editFormEl.attributes.action.nodeValue = Alfresco.constants.PROXY_URI_RELATIVE + "api/rma/admin/rmevents";
               Dom.removeClass(parent.id + "-create-title", "hidden");
               Dom.addClass(parent.id + "-edit-title", "hidden");
               Dom.get(parent.id + "-eventName").value = "";
               Dom.get(parent.id + "-eventDisplayLabel").value = "";
               Dom.get(parent.id + "-eventType").selectedIndex = 0;
            }
         },

         /**
          * onShow ConsolePanel event handler
          *
          * @method onShow
          */
         onShow: function EditPanelHandler_onShow()
         {
            // Set focus to label field
            Dom.get(parent.id + "-eventDisplayLabel").focus();
         },

         /**
          * Cancel edit/create event button click handler
          *
          * @method onCancelButtonClick
          * @param e {object} DomEvent
          * @param obj {object} Object passed back from addListener method
          */
         onCancelButtonClick: function EditPanelHandler_onCancelButtonClick(e, obj)
         {
            // Display the events list again, but not need to refresh the data since we canceled
            YAHOO.Bubbling.fire('viewEvents',
            {
               refresh: false
            });
         }

      });
      new EditPanelHandler();
   };

   YAHOO.extend(Alfresco.RecordsEvents, Alfresco.ConsoleTool,
   {
      /**
       * Currently selected event
       *
       * @property eventName
       * @type string
       */
      eventName: null,

      /**
       * True if the data shall be refreshed on the page
       *
       * @property refresh
       * @type object
       */
      refresh: true,

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * EventType:s and their labels.
          * The event type is the key and the event type label is the value.
          *
          * @property eventTypes
          * @type object
          */
         eventTypes: {}
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function RecordsEvents_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsEvents_onReady()
      {
         // Call super-class onReady() method
         Alfresco.RecordsEvents.superclass.onReady.call(this);
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * View events event handler
       *
       * @method onViewEvents
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onViewEvents: function RecordsEvents_onViewEvents(e, args)
      {
         this.refreshUIState(
         {
            "panel": "view",
            "refresh": args[1].refresh + ""
         });
      },

      /**
       * Create event event handler
       *
       * @method onCreateEvent
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onCreateEvent: function RecordsEvents_onCreateEvent(e, args)
      {
         this.refreshUIState(
         {
            "panel": "edit",
            "eventName": ""
         });
      },

      /**
       * Edit event event handler
       *
       * @method onEditEvent
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onEditEvent: function RecordsEvents_onEditEvent(e, args)
      {
         this.refreshUIState(
         {
            "panel": "edit",
            "eventName": args[1].eventName
         });
      },

      /**
       * History manager state change event handler (override base class)
       *
       * @method onStateChanged
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onStateChanged: function RecordsEvents_onStateChanged(e, args)
      {
         // Clear old states
         this.eventName = undefined;
         this.refresh = true;

         var state = this.decodeHistoryState(args[1].state);
         if (state.eventName)
         {
            this.eventName = state.eventName ? state.eventName : "";
         }
         if (state.refresh)
         {
            this.refresh = state.refresh != "false";
         }

         // test if panel has actually changed?
         if (state.panel)
         {
            this.showPanel(state.panel);
         }
         this.updateCurrentPanel();
      },

      /**
       * Encode state object into a packed string for use as url history value.
       * Override base class.
       *
       * @method encodeHistoryState
       * @param obj {object} state object
       * @private
       */
      encodeHistoryState: function RecordsEvents_encodeHistoryState(obj)
      {
         // wrap up current state values
         var stateObj = {};
         if (this.currentPanelId !== "")
         {
            stateObj.panel = this.currentPanelId;
         }

         // convert to encoded url history state - overwriting with any supplied values
         var state = "";
         if (obj.panel || stateObj.panel)
         {
            state += "panel=" + encodeURIComponent(obj.panel ? obj.panel : stateObj.panel);
         }
         if (obj.eventName)
         {
            state += state.length > 0 ? "&" : "";
            state += "eventName=" + encodeURIComponent(obj.eventName);
         }
         if (obj.refresh)
         {
            state += state.length > 0 ? "&" : "";
            state += "refresh=" + encodeURIComponent(obj.refresh);
         }
         return state;
      }

   });
})();