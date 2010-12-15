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
 * RecordsListOfValues tool component.
 *
 * @namespace Alfresco
 * @class Alfresco.RecordsListOfValues
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Element = YAHOO.util.Element,
       KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * RecordsListOfValues constructor.
    *
    * @param {String} htmlId The HTML id üof the parent element
    * @return {Alfresco.RecordsListOfValues} The new RecordsListOfValues instance
    * @constructor
    */
   Alfresco.RecordsListOfValues = function(htmlId)
   {
      this.name = "Alfresco.RecordsListOfValues";
      Alfresco.RecordsListOfValues.superclass.constructor.call(this, htmlId);

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("viewListOfValues", this.onViewListOfValues, this);
      YAHOO.Bubbling.on("createListOfValue", this.onCreateListOfValue, this);
      YAHOO.Bubbling.on("editListOfValue", this.onEditListOfValue, this);

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
            this.widgets.newListButton = Alfresco.util.createYUIButton(this, "newlist-button", this.onNewListOfValueClick, {}, parent.id + "-newlist-button");

            // Setup data table
            this._setupListDataSource();
            this._setupListDataTable();
         },

         /**
          * Setup the datasource for the data table
          *
          * @method _setupListDataSource
          * @private
          */
         _setupListDataSource: function ViewPanelHandler__setupListDataSource()
         {
            // DataSource definition
            var uriSearchResults = Alfresco.constants.PROXY_URI + "api/rma/admin/rmconstraints";
            this.widgets.listDataSource = new YAHOO.util.DataSource(uriSearchResults,
            {
               responseType: YAHOO.util.DataSource.TYPE_JSON,
               connXhrMode: "queueRequests",
               responseSchema:
               {
                   resultsList: "data"
               }
            });
            this.widgets.listDataSource.doBeforeParseData = function RecordsListOfValues_doBeforeParseData(oRequest , oFullResponse)
            {
               if (oFullResponse && oFullResponse.data)
               {
                  var items = oFullResponse.data;

                  // Sort the lists by their title
                  items.sort(function (list1, list2)
                  {
                     return (list1.constraintTitle > list2.constraintTitle) ? 1 : (list1.constraintTitle < list2.constraintTitle) ? -1 : 0;
                  });

                  // we need to wrap the array inside a JSON object so the DataTable is happy
                  return {
                     data: items
                  };
               }
               return oFullResponse;
            };
         },

         /**
          * Setup the ListDataTable
          *
          * @method _setupListDataTable
          * @private
          */
         _setupListDataTable: function ViewPanelHandler__setupListDataTable()
         {
            var me = this;

            /**
             * Description/detail custom datacell formatter
             *
             * @method renderCellList
             * @param elCell {object}
             * @param oRecord {object}
             * @param oColumn {object}
             * @param oData {object|string}
             */
            renderCellList = function ViewPanelHandler__setupListDataTable_renderCellList(elCell, oRecord, oColumn, oData)
            {
               var title = document.createElement("h4");
               title.appendChild(document.createTextNode(oRecord.getData("constraintTitle")));
               elCell.appendChild(title);
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
            renderCellActions = function ViewPanelHandler__setupListDataTable_renderCellActions(elCell, oRecord, oColumn, oData)
            {
               // Edit list of value
               var editBtn = new YAHOO.widget.Button(
               {
                  container: elCell,
                  label: parent.msg("button.edit")
               });
               editBtn.on("click", me.onEditListOfValueClick, oRecord, me);

               // Rename list of value
               var renameBtn = new YAHOO.widget.Button(
               {
                  container: elCell,
                  label: parent.msg("button.rename")
               });
               renameBtn.on("click", me.onRenameListOfValueClick, oRecord, me);

               // Delete list of value
               // NOTE: removed for temp fix to ETHREEOH-3917 - repository side needs to implement Delete!
               /*var deleteBtn = new YAHOO.widget.Button(
               {
                  container: elCell,
                  label: parent.msg("button.delete")
               });
               deleteBtn.on("click", me.onDeleteListOfValueClick, oRecord, me);*/
            };

            // ListDataTable column defintions
            var columnDefinitions =
                  [
                     {
                        key: "list", label: "List", sortable: false, formatter: renderCellList
                     },
                     {
                        key: "actions", label: "Actions", formatter: renderCellActions
                     }
                  ];

            // ListDataTable definition
            this.widgets.listDataTable = new YAHOO.widget.DataTable(parent.id + "-listofvalues", columnDefinitions, this.widgets.listDataSource,
            {
               renderLoopSize: 32,
               initialLoad: false,
               MSG_EMPTY: parent.msg("message.loading.lists")
            });

            // Override abstract function within ListDataTable to set custom error message
            this.widgets.listDataTable.doBeforeLoadData = function RecordsListOfValues_doBeforeLoadData(sRequest, oResponse, oPayload)
            {
               if (oResponse.error)
               {
                  try
                  {
                     var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                     this.widgets.listDataTable.set("MSG_ERROR", response.message);
                  }
                  catch(e)
                  {
                     parent._setDefaultDataTableErrors(me.widgets.listDataTable, parent.msg("message.empty.lists"));
                  }
               }
               else if (oResponse.results)
               {
                  if (oResponse.results.length === 0)
                  {
                     me.widgets.listDataTable.set("MSG_EMPTY", '<span style="white-space: nowrap;">' + parent.msg("message.empty.lists") + '</span>');
                  }
                  me.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko === 1.8) ? 3 : 5;
               }

               // Must return true to have the "Searching..." message replaced by the error message
               return true;
            };
         },

         /**
          * Load the list of values
          *
          * @method _loadListOfValues
          * @private
          */
         _loadListOfValues: function ViewPanelHandler__loadListOfValues()
         {

            // Reset the custom error messages
            parent._setDefaultDataTableErrors(this.widgets.listDataTable, parent.msg("message.list"));

            // empty results table
            this.widgets.listDataTable.deleteRows(0, this.widgets.listDataTable.getRecordSet().getLength());

            var successHandler = function ViewPanelHandler__loadListOfValues_successHandler(sRequest, oResponse, oPayload)
            {
               // Stop list from getting refreshed when not needed
               this.initialDataLoaded = true;

               // Display new data
               this.widgets.listDataTable.onDataReturnInitializeTable.call(this.widgets.listDataTable, sRequest, oResponse, oPayload);
            };

            var failureHandler = function ViewPanelHandler__loadListOfValues_failureHandler(sRequest, oResponse)
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
                     var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                     this.widgets.listDataTable.set("MSG_ERROR", response.message);
                     this.widgets.listDataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                  }
                  catch(e)
                  {
                     parent._setDefaultDataTableErrors(this.widgets.listDataTable, parent.msg("message.empty.lists"));
                  }
               }
            };

            this.widgets.listDataSource.sendRequest("",
            {
               success: successHandler,
               failure: failureHandler,
               scope: this
            });
         },

         /**
          * Edit List of value button click handler
          *
          * @method onEditListOfValueClick
          * @param e {object} DomEvent
          * @param oRecord {object} Object passed back from addListener method
          */
         onEditListOfValueClick: function ViewPanelHandler_onEditListOfValueClick(e, oRecord)
         {
            // update the current list context
            YAHOO.Bubbling.fire("editListOfValue",
            {
               constraintName: oRecord.getData("constraintName")
            });
         },


         /**
          * Rename list button click handler
          *
          * @method onRenameListOfValueClick
          * @param e {object} DomEvent
          * @param oRecord {object} Object passed back from addListener method
          */
         onRenameListOfValueClick: function ViewPanelHandler_onRenameListOfValueClick(e, oRecord)
         {
            var doSetupFormsValidation = function ViewPanelHandler_onRenameListOfValueClick_doSetupFormsValidation(p_form)
            {
               // Validation
               p_form.addValidation(parent.id + "-renameList-constraintTitle", Alfresco.forms.validation.mandatory, null, "keyup");
               p_form.setShowSubmitStateDynamically(true, false);
            };
            var renameListUrl = Alfresco.constants.PROXY_URI + "api/rma/admin/rmconstraints/" + oRecord.getData("constraintName");
            if (!this.modules.renameList)
            {
               this.modules.renameList = new Alfresco.module.SimpleDialog(parent.id + "-renameList").setOptions(
               {
                  width: "30em",
                  templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/console/rm-list-of-value-properties",
                  actionUrl: renameListUrl,
                  doSetupFormsValidation:
                  {
                     fn: doSetupFormsValidation,
                     scope: this
                  },
                  doBeforeDialogShow:
                  {
                     fn: function ViewPanelHandler_onNewListOfValueClick_SimpleDialog_doBeforeDialogShow(p_config, p_obj)
                     {
                        Dom.get(parent.id + "-renameList-dialogTitle").innerHTML = parent.msg("panel.listproperties.edit");
                        Dom.get(parent.id + "-renameList-constraintTitle").value = oRecord.getData("constraintTitle");
                     }
                  },
                  doBeforeAjaxRequest:
                  {
                     fn: function ViewPanelHandler_onNewListOfValueClick_SimpleDialog_doBeforeAjaxRequest(p_config, p_obj)
                     {
                        p_config.method = Alfresco.util.Ajax.PUT;
                        return true;
                     }
                  },
                  firstFocus: parent.id + "-renameList-constraintTitle",
                  onSuccess:
                  {
                     fn: function ViewPanelHandler_onNewListOfValueClick_SimpleDialog_callback(response)
                     {
                        this._loadListOfValues();
                     },
                     scope: this
                  }
               });
            }
            else
            {
               this.modules.renameList.setOptions(
               {
                  actionUrl: renameListUrl,
                  clearForm: true
               });
            }
            this.modules.renameList.show();
         },

         /**
          * Delete List button click handler.
          * Displays a confirmation dialog before the deletion is made.
          *
          * @method onDeleteListOfValueClick
          * @param e {object} DomEvent
          * @param oRecord {object} Object passed back from addListener method
          */
         onDeleteListOfValueClick: function ViewPanelHandler_onDeleteListOfValueClick(e, oRecord)
         {
            var me = this;
            var text = parent.msg("message.confirm.deletelist.text", oRecord.getData("constraintTitle"));
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: parent.msg("message.confirm.deletelist.title"),
               text: text,
               buttons: [
                  {
                     text: parent.msg("button.yes"),
                     handler: function ViewPanelHandler_onDeleteListOfValueClick_confirmYes()
                     {
                        this.destroy();
                        me._deleteList.call(me, oRecord);
                     }
                  },
                  {
                     text: parent.msg("button.no"),
                     handler: function ViewPanelHandler_onDeleteListOfValueClick_confirmNo()
                     {
                        this.destroy();
                     },
                     isDefault: true
                  }]
            });
         },

         /**
          * Delete the list
          *
          * @method _deleteList
          * @param oRecord {object} The list
          */
         _deleteList: function ViewPanelHandler__deleteList(oRecord)
         {
            Alfresco.util.Ajax.jsonDelete(
            {
               url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmconstraints/" + oRecord.getData("constraintName"),
               successCallback:
               {
                  fn: function(o)
                  {
                     // Reload list of values
                     this._loadListOfValues();

                     // Display success message
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: parent.msg("message.deletelist.success")
                     });
                  },
                  scope: this
               },
               failureMessage: parent.msg("message.deletelist.failure")
            });
         },

         /**
          * onUpdate ConsolePanel event handler
          *
          * @method onUpdate
          */
         onUpdate: function ViewPanelHandler_onUpdate()
         {
            if (!this.initialDataLoaded || parent.refresh)
            {
               this._loadListOfValues();
            }            
         },

         /**
          * New list button click handler
          *
          * @method onNewListOfValueClick
          * @param e {object} DomEvent
          * @param obj {object} Object passed back from addListener method
          */
         onNewListOfValueClick: function ViewPanelHandler_onNewListOfValueClick(e, obj)
         {
            var doSetupFormsValidation = function ViewPanelHandler_onNewListOfValueClick_doSetupFormsValidation(p_form)
            {
               // Validation
               p_form.addValidation(parent.id + "-newList-constraintTitle", Alfresco.forms.validation.mandatory, null, "keyup");
               p_form.setShowSubmitStateDynamically(true, false);
            };
            var createListUrl = Alfresco.constants.PROXY_URI + "api/rma/admin/rmconstraints";
            if (!this.modules.createList)
            {
               this.modules.createList = new Alfresco.module.SimpleDialog(parent.id + "-newList").setOptions(
               {
                  width: "30em",
                  templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/console/rm-list-of-value-properties",
                  actionUrl: createListUrl,
                  doSetupFormsValidation:
                  {
                     fn: doSetupFormsValidation,
                     scope: this
                  },
                  doBeforeDialogShow:
                  {
                     fn: function ViewPanelHandler_onNewListOfValueClick_SimpleDialog_doBeforeDialogShow(p_config, p_obj)
                     {
                        Dom.get(parent.id + "-newList-dialogTitle").innerHTML = parent.msg("panel.listproperties.create");
                     }
                  },
                  doBeforeAjaxRequest:
                  {
                     fn: function ViewPanelHandler_onNewListOfValueClick_SimpleDialog_doBeforeAjaxRequest(p_config, p_obj)
                     {
                        p_config.method = Alfresco.util.Ajax.POST;
                        p_config.dataObj.allowedValues = [];
                        return true;
                     }
                  },
                  firstFocus: parent.id + "-newList-constraintTitle",
                  onSuccess:
                  {
                     fn: function ViewPanelHandler_onNewListOfValueClick_SimpleDialog_callback(response)
                     {
                        this._loadListOfValues();
                     },
                     scope: this
                  }
               });
            }
            else
            {
               this.modules.createList.setOptions(
               {
                  actionUrl: createListUrl,
                  clearForm: true
               });
            }
            this.modules.createList.show();
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
         onLoad: function ViewPanelHandler_onLoad()
         {
            var me = this;

            // Buttons
            this.widgets.newValueButton = Alfresco.util.createYUIButton(this, "newvalue-button", this.onNewValueClick, {}, parent.id + "-newvalue-button");
            this.widgets.addAccessButton = Alfresco.util.createYUIButton(this, "addaccess-button", this.onAddAccessClick, {}, parent.id + "-addaccess-button");
            this.widgets.newDoneButton = Alfresco.util.createYUIButton(this, "done-button", this.onDoneButtonClick, {}, parent.id + "-done-button");

            // Make enter key strokes in new value text input add values
            var newValueInput = Dom.get(parent.id + "-newvalue-input"),
                  keyListener = new KeyListener(newValueInput,
                  {
                     keys:13
                  },
                  {
                     fn: function()
                     {
                        if (!this.widgets.newValueButton.get("disabled"))
                        {
                           me.onNewValueClick(newValueInput);
                        }
                     },
                     scope:this,
                     correctScope:true
                  }, "keydown").enable();

            // Make sure the "Add" value button is is enabled when characters are entered in the textinput
            Event.addListener(newValueInput, "keyup", function()
            {
               this._enableNewValueButton(newValueInput);
            }, null, this);
            this._enableNewValueButton(newValueInput);

            // Setup data table and data sources
            this._setupValuesDataSource();
            this._setupAccessDataSource();
            this._setupValuesDataTable();
            this._setupAccessDataTable();

            // Load in the Authority Finder component from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/authority-finder",
               dataObj:
               {
                  htmlid: parent.id + "-search-authorityfinder"
               },
               successCallback:
               {
                  fn: this.onAuthorityFinderLoaded,
                  scope: this
               },
               failureMessage: "Could not load Authority Finder component",
               execScripts: true
            });

         },

         /**
          * onUpdate ConsolePanel event handler
          *
          * @method onUpdate
          */
         onUpdate: function ViewPanelHandler_onUpdate()
         {
            this._loadValues();

            // empty access table
            this.widgets.accessDataTable.deleteRows(0, this.widgets.accessDataTable.getRecordSet().getLength());
            this.widgets.accessDataTable.set("MSG_EMPTY", "");
            
         },

         /**
          * Called by the ConsolePanelHandler when this panel is shown
          *
          * @method onShow
          */
         onShow: function ViewPanelHandler_onShow()
         {
            this._visible = true;
         },

         /**
          * Called by the ConsolePanelHandler when this panel is hidden
          *
          * @method onHide
          */
         onHide: function ViewPanelHandler_onHide()
         {
            this._visible = false;
         },

         /**
          * Done button click handler
          *
          * @method onDoneButtonClick
          * @param e {object} DomEvent
          * @param obj {object} Object passed back from addListener method
          */
         onDoneButtonClick: function EditPanelHandler_onDoneButtonClick(e, obj)
         {
            // Display the list of values again, but not need to refresh the data since we canceled
            YAHOO.Bubbling.fire('viewListOfValues',
            {
               refresh: false
            });
         },

         /**
          * Values
          */

         /**
          * Validates the user input for a values title and enables/disables the new value button afterwards
          *
          * @method _enableNewValueButton
          * @param newValueInputEl
          * @private
          * @return true if the user input is valid
          */
         _enableNewValueButton: function ViewPanelHandler__enableNewValueButton(newValueInputEl)
         {
            this.widgets.newValueButton.set("disabled", (YAHOO.lang.trim(newValueInputEl.value).length === 0));
         },

         /**
          * Setup the datasource for the values data table
          *
          * @method _setupValuesDataSource
          * @private
          */
         _setupValuesDataSource: function ViewPanelHandler__setupValuesDataSource()
         {
            // DataSource definition
            var uriSearchResults = Alfresco.constants.PROXY_URI + "api/rma/admin/rmconstraints";
            this.widgets.valuesDataSource = new YAHOO.util.DataSource(uriSearchResults,
            {
               responseType: YAHOO.util.DataSource.TYPE_JSON,
               connXhrMode: "queueRequests",
               responseSchema:
               {
                   resultsList: "data.values"
               }
            });
            this.widgets.valuesDataSource.doBeforeParseData = function RecordsListOfValues_doBeforeParseData(oRequest , oFullResponse)
            {
               if (oFullResponse && oFullResponse.data && oFullResponse.data.values)
               {
                  // Display the title of the list in the "edit title"
                  Dom.get(parent.id + "-edittitle").innerHTML = parent.msg("label.edit-listofvalue-title", $html(oFullResponse.data.constraintTitle));

                  // Get the values from the response
                  var values = oFullResponse.data.values;

                  // Sort the values by their title
                  values.sort(function (value1, value2)
                  {
                     return (value1.valueTitle > value2.valueTitle) ? 1 : (value1.valueTitle < value2.valueTitle) ? -1 : 0;
                  });

                  // we need to wrap the array inside a JSON object so the DataTable is happy
                  return (
                  {
                     data:
                     {
                        values: values
                     }
                  });
               }
               return oFullResponse;
            };
         },

         /**
          * Setup the ValuesDataTable
          *
          * @method _setupValuesDataTable
          * @private
          */
         _setupValuesDataTable: function ViewPanelHandler__setupValuesDataTable()
         {
            var me = this;

            /**
             * Description/detail custom datacell formatter
             *
             * @method renderCellDescription
             * @param elCell {object}
             * @param oRecord {object}
             * @param oColumn {object}
             * @param oData {object|string}
             */
            renderCellDescription = function ViewPanelHandler__setupValuesDataTable_renderCellDescription(elCell, oRecord, oColumn, oData)
            {
               elCell.appendChild(document.createTextNode(oRecord.getData("valueTitle")));
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
            renderCellActions = function ViewPanelHandler__setupValuesDataTable_renderCellActions(elCell, oRecord, oColumn, oData)
            {
               var deleteBtn = new YAHOO.widget.Button(
               {
                  container: elCell,
                  label: parent.msg("button.delete")
               });
               deleteBtn.on("click", me.onDeleteValueClick, oRecord, me);
            };

            // ValuesDataTable column defintions
            var columnDefinitions =
                  [
                     {
                        key: "reference", label: "List", sortable: false, formatter: renderCellDescription
                     },
                     {
                        key: "actions", label: "Actions", formatter: renderCellActions
                     }
                  ];

            // ValuesDataTable definition
            this.widgets.valuesDataTable = new YAHOO.widget.DataTable(parent.id + "-values", columnDefinitions, this.widgets.valuesDataSource,
            {
               selectionMode: "single",
               renderLoopSize: 32,
               initialLoad: false,
               MSG_EMPTY: parent.msg("message.loading.values")
            });

            // Listen for when a value is selected so we can reload the access list
            this.widgets.valuesDataTable.subscribe("rowSelectEvent", function(e, o)
            {
               this._loadAccess();
            }, null, this);
            this.widgets.valuesDataTable.subscribe("rowClickEvent", this.widgets.valuesDataTable.onEventSelectRow);

            // Override abstract function within ValuesDataTable to set custom error message
            this.widgets.valuesDataTable.doBeforeLoadData = function RecordsListOfValues_doBeforeLoadData(sRequest, oResponse, oPayload)
            {
               if (oResponse.error)
               {
                  try
                  {
                     var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                     this.widgets.valuesDataTable.set("MSG_ERROR", response.message);
                  }
                  catch(e)
                  {
                     parent._setDefaultDataTableErrors(me.widgets.valuesDataTable, parent.msg("message.empty.values"));
                  }
               }
               else if (oResponse.results)
               {
                  if (oResponse.results.length === 0)
                  {
                     me.widgets.valuesDataTable.set("MSG_EMPTY", '<span style="white-space: nowrap;">' + parent.msg("message.empty.values") + '</span>');
                  }
                  me.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko === 1.8) ? 3 : 5;
               }

               // Must return true to have the "Searching..." message replaced by the error message
               return true;
            };
         },

         /**
          * Load the list of values
          *
          * @method _loadValues
          * @private
          */
         _loadValues: function ViewPanelHandler__loadValues()
         {

            // Reset the custom error messages
            parent._setDefaultDataTableErrors(this.widgets.valuesDataTable, parent.msg("message.empty.values"));

            // empty results table
            this.widgets.valuesDataTable.deleteRows(0, this.widgets.valuesDataTable.getRecordSet().getLength());

            var successHandler = function ViewPanelHandler__loadValues_successHandler(sRequest, oResponse, oPayload)
            {
               /**
                * Note: The "edit title" will be set in the "doBeforeParseData" callback
                * since the "constraintTitle" isn't part of the "tabular data".
                */

               // Display the value list and select the first item so its access authorities are loaded if any
               this.widgets.valuesDataTable.onDataReturnInitializeTable.call(this.widgets.valuesDataTable, sRequest, oResponse, oPayload);
               if (this.widgets.valuesDataTable.getRecordSet().getLength() >  0)
               {
                  this.widgets.valuesDataTable.selectRow(0);
                  this.widgets.addAccessButton.set("disabled", false);
               }
               else
               {
                  this.widgets.accessDataTable.set("MSG_EMPTY", "");                                 
                  this.widgets.addAccessButton.set("disabled", true);
               }
            };

            var failureHandler = function ViewPanelHandler__loadValues_failureHandler(sRequest, oResponse)
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
                     var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                     this.widgets.valuesDataTable.set("MSG_ERROR", response.message);
                     this.widgets.valuesDataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                  }
                  catch(e)
                  {
                     parent._setDefaultDataTableErrors(this.widgets.valuesDataTable, parent.msg("message.empty.values"));
                  }
               }
            };

            this.widgets.valuesDataSource.sendRequest("/" + parent.constraintName + "/values",
            {
               success: successHandler,
               failure: failureHandler,
               scope: this
            });
         },

         /**
          * Delete Value button click handler.
          * Displays a confirmation dialog before the deletion is made.
          *
          * @method onDeleteValueClick
          * @param e {object} DomEvent
          * @param oRecord {object} Object passed back from addListener method
          */
         onDeleteValueClick: function ViewPanelHandler_onDeleteValueClick(e, oRecord)
         {
            var me = this;
            var text = parent.msg("message.confirm.removevalue.text", oRecord.getData("valueTitle"));
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: parent.msg("message.confirm.removevalue.title"),
               text: text,
               buttons: [
                  {
                     text: parent.msg("button.yes"),
                     handler: function ViewPanelHandler_onDeleteValueClick_confirmYes()
                     {
                        this.destroy();
                        me._deleteValue.call(me, oRecord);
                     }
                  },
                  {
                     text: parent.msg("button.no"),
                     handler: function ViewPanelHandler_onDeleteValueClick_confirmNo()
                     {
                        this.destroy();
                     },
                     isDefault: true
                  }]
            });
         },

         /**
          * Remove a value from the list
          *
          * @method _deleteValue
          * @param oRecord {object} The list
          */
         _deleteValue: function ViewPanelHandler__deleteValue(oRecord)
         {
            // Remove value from previous values
            var valueRecords = this.widgets.valuesDataTable.getRecordSet().getRecords(),
                  record,
                  values = [];
            for (var i = 0; i < valueRecords.length; i++)
            {
               record = valueRecords[i];
               if (oRecord.getId() != record.getId())
               {
                  values.push(record.getData("valueTitle"));
               }
            }

            Alfresco.util.Ajax.jsonPut(
            {
               url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmconstraints/" + parent.constraintName,
               dataObj:
               {
                  allowedValues: values
               },
               successCallback:
               {
                  fn: function(o)
                  {
                     // Reload the values
                     this._loadValues();
                  },
                  scope: this
               },
               failureMessage: parent.msg("message.deletevalue.failure")
            });
         },

         /**
          * New list button click handler.
          * Adds the new value and reloads the list.
          *
          * @method onNewValueClick
          * @param e {object} DomEvent
          * @param obj {object} Object passed back from addListener method
          */
         onNewValueClick: function ViewPanelHandler_onNewValueClick(e, obj)
         {
            // Concat previous values with the new one
            var valueRecords = this.widgets.valuesDataTable.getRecordSet().getRecords(),
                  values = [];
            for (var i = 0; i < valueRecords.length; i++)
            {
               values.push(valueRecords[i].getData("valueTitle"));

            }
            var newValue = Dom.get(parent.id + "-newvalue-input").value;
            values.push(newValue);

            // Submit new value to server and add it to the ui after a successful response
            Alfresco.util.Ajax.jsonPut(
            {
               url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmconstraints/" + parent.constraintName,
               dataObj:
               {
                  allowedValues: values
               },
               successCallback:
               {
                  fn: function(o)
                  {
                     // Reload the values and clear the textinput
                     this._loadValues();
                     Dom.get(parent.id + "-newvalue-input").value = "";
                     this.widgets.newValueButton.set("disabled", true);
                  },
                  scope: this
               },
               failureMessage: parent.msg("message.addvalue.failure", newValue)
            });
         },


         /**
          * Access
          */

         /**
          * Called when the authority finder template has been loaded.
          * Creates a dialog and inserts the authority finder for choosing groups and users to add.
          *
          * @method onAuthorityFinderLoaded
          * @param response The server response
          */
         onAuthorityFinderLoaded: function ViewPanelHandler_onAuthorityFinderLoaded(response)
         {
            // Inject the component from the XHR request into it's placeholder DIV element
            var finderDiv = Dom.get(parent.id + "-search-authorityfinder");
            finderDiv.innerHTML = response.serverResponse.responseText;

            // Create the Add Group dialog
            this.widgets.addAccessPanel = Alfresco.util.createYUIPanel(parent.id + "-authoritypicker");

            // Find the Group Finder by container ID
            this.modules.searchAuthorityFinder = Alfresco.util.ComponentManager.get(parent.id + "-search-authorityfinder");

            // Set the correct options for our use
            this.modules.searchAuthorityFinder.setOptions(
            {
               singleSelectMode: true,
               minSearchTermLength: 3
            });

            // Make sure we listen for events when the user selects a group
            YAHOO.Bubbling.on("itemSelected", this.onAuthoritySelected, this);
         },

         /**
          * Group selected event handler.
          * This event is fired from Group picker - so we much ensure
          * the event is for the current panel by checking panel visibility.
          *
          * @method onGroupSelected
          * @param e DomEvent
          * @param args Event parameters (depends on event type)
          */
         onAuthoritySelected: function ViewPanelHandler_onAuthoritySelected(e, args)
         {
            // This is a "global" event so we ensure the event is for the current panel by checking panel visibility.
            if (this._visible)
            {
               // Hide the access picker
               this.widgets.addAccessPanel.hide();

               // Concat previous authorities with the new one
               var authorityRecords = this.widgets.accessDataTable.getRecordSet().getRecords(),
                     authorities = [];
               for (var i = 0; i < authorityRecords.length; i++)
               {
                  authorities.push(authorityRecords[i].getData("authorityName"));
               }
               authorities.push(args[1].itemName);

               // Submit new access authorities to server and add it to the ui after a successful response
               Alfresco.util.Ajax.jsonPost(
               {
                  url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmconstraints/" + parent.constraintName + "/values",
                  dataObj:
                  {
                     values: [
                        {
                           value: this._getSelectedValueName(),
                           authorities: authorities
                        }
                     ]
                  },
                  successCallback:
                  {
                     fn: function(o)
                     {
                        // Reload the values
                        this._loadAccess();
                     },
                     scope: this
                  },
                  failureMessage: parent.msg("message.addaccess.failure")
               });

            }
         },

         /**
          * Setup the datasource for the access data table
          *
          * @method _setupAccessDataSource
          * @private
          */
         _setupAccessDataSource: function ViewPanelHandler__setupAccessDataSource()
         {
            // DataSource definition
            var uriSearchResults = Alfresco.constants.PROXY_URI + "api/rma/admin/rmconstraints";
            this.widgets.accessDataSource = new YAHOO.util.DataSource(uriSearchResults,
            {
               responseType: YAHOO.util.DataSource.TYPE_JSON,
               connXhrMode: "queueRequests",
               responseSchema:
               {
                   resultsList: "data.value.authorities"
               }
            });
            this.widgets.accessDataSource.doBeforeParseData = function RecordsListOfAccess_doBeforeParseData(oRequest , oFullResponse)
            {
               if (oFullResponse && oFullResponse.data && oFullResponse.data.value.authorities)
               {
                  var authorities = oFullResponse.data.value.authorities;

                  // Sort the access authorities by their title
                  authorities.sort(function (authority1, authority2)
                  {
                     return (authority1.authorityTitle > authority2.authorityTitle) ? 1 : (authority1.authorityTitle < authority2.authorityTitle) ? -1 : 0;
                  });

                  // we need to wrap the array inside a JSON object so the DataTable is happy
                  return (
                  {
                     data:
                     {
                        value:
                        {
                           authorities: authorities
                        }
                     }
                  });
               }
               return oFullResponse;
            };
         },

         /**
          * Setup the AccessDataTable
          *
          * @method _setupAccessDataTable
          * @private
          */
         _setupAccessDataTable: function ViewPanelHandler__setupAccessDataTable()
         {
            var me = this;

            /**
             * ListIcon custom datacell formatter
             *
             * @method renderCellAccessIcon
             * @param elCell {object}
             * @param oRecord {object}
             * @param oColumn {object}
             * @param oData {object|string}
             */
            renderCellAccessIcon = function ViewPanelHandler__setupListDataTable_renderCellAccessIcon(elCell, oRecord, oColumn, oData)
            {
               Dom.addClass(elCell, oRecord.getData("authorityName").indexOf("GROUP_") == 0 ? "authority-group":"authority-user");               
               elCell.innerHTML = "&nbsp;";
            };

            /**
             * Description/detail custom datacell formatter
             *
             * @method renderCellDescription
             * @param elCell {object}
             * @param oRecord {object}
             * @param oColumn {object}
             * @param oData {object|string}
             */
            renderCellDescription = function ViewPanelHandler__setupAccessDataTable_renderCellDescription(elCell, oRecord, oColumn, oData)
            {
               elCell.appendChild(document.createTextNode(oRecord.getData("authorityTitle")));
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
            renderCellActions = function ViewPanelHandler__setupAccessDataTable_renderCellActions(elCell, oRecord, oColumn, oData)
            {
               var deleteBtn = new YAHOO.widget.Button(
               {
                  container: elCell,
                  label: parent.msg("button.remove")
               });
               deleteBtn.on("click", me.onRemoveAccessClick, oRecord, me);
            };

            // AccessDataTable column defintions
            var columnDefinitions =
            [
               { key: "accessicon", label: "Icon", sortable: false, formatter: renderCellAccessIcon },                        
               { key: "access", label: "List", sortable: false, formatter: renderCellDescription },
               { key: "actions", label: "Actions", formatter: renderCellActions }
            ];

            // AccessDataTable definition
            this.widgets.accessDataTable = new YAHOO.widget.DataTable(parent.id + "-access", columnDefinitions, this.widgets.accessDataSource,
            {
               renderLoopSize: 32,
               initialLoad: false,
               MSG_EMPTY: ""
            });

            // Override abstract function within AccessDataTable to set custom error message
            this.widgets.accessDataTable.doBeforeLoadData = function RecordsListOfAccess_doBeforeLoadData(sRequest, oResponse, oPayload)
            {
               if (oResponse.error)
               {
                  try
                  {
                     var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                     this.widgets.accessDataTable.set("MSG_ERROR", response.message);
                  }
                  catch(e)
                  {
                     parent._setDefaultDataTableErrors(me.widgets.accessDataTable, parent.msg("message.empty.access"));
                  }
               }
               else if (oResponse.results)
               {
                  if (oResponse.results.length === 0)
                  {
                     me.widgets.accessDataTable.set("MSG_EMPTY", '<span style="white-space: nowrap;">' + parent.msg("message.empty.access") + '</span>');
                  }
                  me.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko === 1.8) ? 3 : 5;
               }

               // Must return true to have the "Searching..." message replaced by the error message
               return true;
            };
         },

         /**
          * Load the list of access depending on what value that is selected
          *
          * @method _loadAccess
          * @private
          */
         _loadAccess: function ViewPanelHandler__loadAccess()
         {
            // Reset the custom error messages
            parent._setDefaultDataTableErrors(this.widgets.accessDataTable, parent.msg("message.loading.access"));

            // empty results table
            this.widgets.accessDataTable.deleteRows(0, this.widgets.accessDataTable.getRecordSet().getLength());

            var successHandler = function ViewPanelHandler__loadAccess_successHandler(sRequest, oResponse, oPayload)
            {
               this.widgets.accessDataTable.onDataReturnInitializeTable.call(this.widgets.accessDataTable, sRequest, oResponse, oPayload);
            };

            var failureHandler = function ViewPanelHandler__loadAccess_failureHandler(sRequest, oResponse)
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
                     var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                     this.widgets.accessDataTable.set("MSG_ERROR", response.message);
                     this.widgets.accessDataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                  }
                  catch(e)
                  {
                     parent._setDefaultDataTableErrors(this.widgets.accessDataTable, parent.msg("message.empty.access"));
                  }
               }
            };

            var selectedValueName = this._getSelectedValueName();
            if (selectedValueName)
            {
               // Note: extra manual encoding of %2F ("/") to enable correct HTTP request generation
               this.widgets.accessDataSource.sendRequest("/" + parent.constraintName + "/values/" + encodeURIComponent(selectedValueName).replace("%2F", "%252F"),
               {
                  success: successHandler,
                  failure: failureHandler,
                  scope: this
               });
            }
            else
            {
               this.widgets.accessDataTable.set("MSG_EMPTY", "");               
            }
         },

         /**
          * Get the valueName of the selected value.
          *
          * @method _getSelectedValueName
          * @private
          */
         _getSelectedValueName: function ViewPanelHandler_getSelectedValueName()
         {
            var selectedRows = this.widgets.valuesDataTable.getSelectedRows(),
                  selectedRow = selectedRows && selectedRows.length > 0 ? selectedRows[0] : null,
                  selectedRecord = this.widgets.valuesDataTable.getRecord(selectedRow);
            return selectedRecord ? selectedRecord.getData("valueName") : null;
         },

         /**
          * Delete Access button click handler.
          * Displays a confirmation dialog before the deletion is made.
          *
          * @method onRemoveAccessClick
          * @param e {object} DomEvent
          * @param oRecord {object} Object passed back from addListener method
          */
         onRemoveAccessClick: function ViewPanelHandler_onRemoveAccessClick(e, oRecord)
         {
            var me = this;
            var text = parent.msg("message.confirm.removeaccess.text", oRecord.getData("authorityTitle"));
            Alfresco.util.PopupManager.displayPrompt(
            {
               title: parent.msg("message.confirm.removeaccess.title"),
               text: text,
               buttons: [
                  {
                     text: parent.msg("button.yes"),
                     handler: function ViewPanelHandler_onRemoveccessClick_confirmYes()
                     {
                        this.destroy();
                        me._removeAccess.call(me, oRecord);
                     }
                  },
                  {
                     text: parent.msg("button.no"),
                     handler: function ViewPanelHandler_onRemoveAccessClick_confirmNo()
                     {
                        this.destroy();
                     },
                     isDefault: true
                  }]
            });
         },

         /**
          * Remove the access
          *
          * @method _removeAccess
          * @param oRecord {object} The list
          */
         _removeAccess: function ViewPanelHandler__removeAccess(oRecord)
         {
            // Concat previous authorities with the new one
            var authorityRecords = this.widgets.accessDataTable.getRecordSet().getRecords(),
                  authority,
                  authorities = [];
            for (var i = 0; i < authorityRecords.length; i++)
            {
               authority = authorityRecords[i];
               if (oRecord.getId() != authority.getId())
               {
                  authorities.push(authorityRecords[i].getData("authorityName"));
               }
            }

            // Send new list of authorities to server and update ui after
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.PROXY_URI + "api/rma/admin/rmconstraints/" + parent.constraintName + "/values",
               dataObj:
               {
                  values: [
                     {
                        value: this._getSelectedValueName(),
                        authorities: authorities
                     }
                  ]
               },
               successCallback:
               {
                  fn: function(o)
                  {
                     // Reload the accesss authorities
                     this._loadAccess();
                  },
                  scope: this
               },
               failureMessage: parent.msg("message.removeaccess.failure")
            });
         },

         /**
          * Add access button click handler
          *
          * @method onAddAccessClick
          * @param e {object} DomEvent
          * @param obj {object} Object passed back from addListener method
          */
         onAddAccessClick: function ViewPanelHandler_onAddAccessClick(e, obj)
         {
            this.modules.searchAuthorityFinder.clearResults();
            this.widgets.addAccessPanel.show();            
         }

      });
      new EditPanelHandler();
   };

   YAHOO.extend(Alfresco.RecordsListOfValues, Alfresco.ConsoleTool,
   {
      /**
       * Currently selected list of value.
       *
       * @property constraintName
       * @type string
       */
      constraintName: null,

      /**
       * True if the data shall be refreshed on the page
       *
       * @property refresh
       * @type object
       */
      refresh: true,

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function RecordsListOfValues_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsListOfValues_onReady()
      {
         // Call super-class onReady() method
         Alfresco.RecordsListOfValues.superclass.onReady.call(this);
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * View List of Values event handler
       *
       * @method onViewListOfValues
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onViewListOfValues: function RecordsListOfValues_onViewListOfValues(e, args)
      {
         this.refreshUIState(
         {
            "panel": "view",
            "refresh": args[1].refresh + ""
         });
      },

      /**
       * Create List of Values event handler
       *
       * @method onCreateListOfValue
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onCreateListOfValue: function RecordsListOfValues_onCreateListOfValue(e, args)
      {
         this.refreshUIState(
         {
            "panel": "edit",
            "constraintName": ""
         });
      },

      /**
       * Edit List of Value event handler
       *
       * @method onEditListOfValue
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onEditListOfValue: function RecordsListOfValues_onEditListOfValue(e, args)
      {
         this.refreshUIState(
         {
            "panel": "edit",
            "constraintName": args[1].constraintName
         });
      },

      /**
       * History manager state change event handler (override base class)
       *
       * @method onStateChanged
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onStateChanged: function RecordsListOfValues_onStateChanged(e, args)
      {
         // Clear old states
         this.constraintName = undefined;
         this.refresh = true;

         var state = this.decodeHistoryState(args[1].state);
         if (state.constraintName)
         {
            this.constraintName = state.constraintName ? state.constraintName : "";
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
      encodeHistoryState: function RecordsListOfValues_encodeHistoryState(obj)
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
         if (obj.constraintName)
         {
            state += state.length > 0 ? "&" : "";
            state += "constraintName=" + encodeURIComponent(obj.constraintName);
         }
         if (obj.refresh)
         {
            state += state.length > 0 ? "&" : "";
            state += "refresh=" + encodeURIComponent(obj.refresh);
         }
         return state;
      },

      /**
       * Resets the YUI DataTable errors to our custom messages
       *
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function RecordsListOfValues__setDefaultDataTableErrors(dataTable, emptyMessage)
      {
         dataTable.set("MSG_EMPTY", emptyMessage);
         dataTable.set("MSG_ERROR", this.msg("message.error"));
      }
   });
})();