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
 * Dashboard MyDocuments component.
 * 
 * @namespace Alfresco
 * @class Alfresco.dashlet.MyDocuments
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
      $links = Alfresco.util.activateLinks;


   /**
    * Dashboard MyDocuments constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyDocuments} The new component instance
    * @constructor
    */
   Alfresco.dashlet.MyDocuments = function MyDocuments_constructor(htmlId)
   {
      Alfresco.dashlet.MyDocuments.superclass.constructor.call(this, "Alfresco.dashlet.MyDocuments", htmlId, ["button", "container", "datasource", "datatable", "animation"]);

      this.previewTooltips = [];
      this.metadataTooltips = [];
      
      return this;
   };

   YAHOO.extend(Alfresco.dashlet.MyDocuments, Alfresco.component.Base,
   {
      /**
       * Keeps track of current filter.
       * 
       * @property currentFilter
       * @type string
       */
      currentFilter: "favourites",

      /**
       * Holds IDs to register preview tooltips with.
       * 
       * @property previewTooltips
       * @type array
       */
      previewTooltips: null,

      /**
       * Holds IDs to register metadata tooltips with.
       * 
       * @property metadataTooltips
       * @type array
       */
      metadataTooltips: null,

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function MyDocuments_onReady()
      {
         var me = this;
         
         // Filter buttons
         this.widgets.filterGroup = new YAHOO.widget.ButtonGroup(this.id + "-filters");
         this.widgets.filterGroup.on("checkedButtonChange", this.onFilterChange, this.widgets.filterGroup, this);

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();

         // Tooltip for thumbnail on mouse hover
         this.widgets.previewTooltip = new YAHOO.widget.Tooltip(this.id + "-previewTooltip",
         {
            width: "108px"
         });
         this.widgets.previewTooltip.contextTriggerEvent.subscribe(function(type, args)
         {
            var context = args[0],
               record = me.widgets.dataTable.getRecord(context.id),
               thumbnailUrl = Alfresco.constants.PROXY_URI + "api/node/" + record.getData("nodeRef").replace(":/", "") + "/content/thumbnails/doclib?c=queue&ph=true";
            
            this.cfg.setProperty("text", '<img src="' + thumbnailUrl + '" />');
         });

         // Tooltip for metadata on mouse hover
         this.widgets.metadataTooltip = new YAHOO.widget.Tooltip(this.id + "-metadataTooltip");
         this.widgets.metadataTooltip.contextTriggerEvent.subscribe(function(type, args)
         {
            var context = args[0],
               record = me.widgets.dataTable.getRecord(context.id),
               locn = record.getData("location");
            
            var text = '<em>' + me.msg("label.site") + ':</em> ' + $html(locn.siteTitle) + '<br />';
            text += '<em>' + me.msg("label.path") + ':</em> ' + $html(locn.path);
            
            this.cfg.setProperty("text", text);
         });

         // DataSource definition
         var uriDocList = Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/documents/node/alfresco/sites/home?max=50&filter=";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriDocList,
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

         /**
          * Favourite Documents custom datacell formatter
          */
         var favEventClass = Alfresco.util.generateDomId(null, "fav-doc");
         var renderCellFavourite = function MD_renderCellFavourite(elCell, oRecord, oColumn, oData)
         {
            var nodeRef = oRecord.getData("nodeRef"),
               isFavourite = oRecord.getData("isFavourite");

            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            elCell.innerHTML = '<a class="favourite-document ' + favEventClass + (isFavourite ? ' enabled' : '') + '" title="' + me.msg("tip.favourite-document." + (isFavourite ? 'remove' : 'add')) + '">&nbsp;</a>';
         };

         /**
          * Thumbnail custom datacell formatter
          */
         var renderCellThumbnail = function MD_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            var name = oRecord.getData("fileName"),
               title = oRecord.getData("title"),
               type = oRecord.getData("type"),
               locn = oRecord.getData("location"),
               extn = name.substring(name.lastIndexOf("."));

            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var id = me.id + '-preview-' + oRecord.getId(),
               docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + locn.site + "/document-details?nodeRef=" + oRecord.getData("nodeRef");
            
            elCell.innerHTML = '<span id="' + id + '" class="icon32"><a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(name) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
                  
            // Preview tooltip
            me.previewTooltips.push(id);
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
         var renderCellDescription = function MD_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var type = oRecord.getData("type"),
               locn = oRecord.getData("location");
            
            var id = me.id + '-metadata-' + oRecord.getId(),
               docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + locn.site + "/document-details?nodeRef=" + oRecord.getData("nodeRef");
                
            var desc = '<span id="' + id + '"><a class="theme-color-1" href="' + docDetailsUrl + '">' + $html(oRecord.getData("displayName")) + '</a></span>';
            desc += '<div class="detail">' + $links($html(oRecord.getData("description"))) + '</div>';

            elCell.innerHTML = desc;

            // Metadata tooltip
            me.metadataTooltips.push(id);
         };

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "favourite", label: "Favourite", sortable: false, formatter: renderCellFavourite, width: 16 },
            { key: "thumbnail", label: "Thumbnail", sortable: false, formatter: renderCellThumbnail, width: 32 },
            { key: "description", label: "Description", sortable: false, formatter: renderCellDescription }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
         {
            initialLoad: true,
            initialRequest: this.currentFilter,
            dynamicData: true,
            MSG_EMPTY: this.msg("label.loading")
         });

         // Add animation to row delete
         this.widgets.dataTable._deleteTrEl = function(row)
         { 
            var scope = this,
               trEl = this.getTrEl(row);

            var changeColor = new YAHOO.util.ColorAnim(trEl,
            {
               opacity:
               {
                  to: 0
               }
            }, 0.25);
            changeColor.onComplete.subscribe(function()
            {
               YAHOO.widget.DataTable.prototype._deleteTrEl.call(scope, row);
            });
            changeColor.animate();
         };
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function MD_doBeforeLoadData(sRequest, oResponse, oPayload)
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
                  me.widgets.dataTable.set("MSG_EMPTY", this.msg("label.empty"));
                  me.widgets.dataTable.set("MSG_ERROR", this.msg("label.error"));
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

         // Rendering complete event handler
         this.widgets.dataTable.subscribe("renderEvent", function()
         {
            // Register tooltip contexts
            this.widgets.previewTooltip.cfg.setProperty("context", this.previewTooltips);
            this.widgets.metadataTooltip.cfg.setProperty("context", this.metadataTooltips);
            
            this.widgets.dataTable.set("MSG_EMPTY", this.msg("label.empty"));
         }, this, true);
         
         // Hook favourite document events
         var fnFavouriteHandler = function MD_fnFavouriteHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               me.onFavouriteDocument.call(me, args[1].target.offsetParent, owner);
            }
      		 
            return true;
         };
         YAHOO.Bubbling.addDefaultAction(favEventClass, fnFavouriteHandler);
      },
      
      /**
       * Filter Change button handler
       *
       * @method onFilterChange
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFilterChange: function MD_onFilterChange(p_obj)
      {
         this.currentFilter = p_obj.newValue.get("value");

         // Reset tooltips arrays
         this.previewTooltips = [];
         this.metadataTooltips = [];

         var successHandler = function MD__oFC_success(sRequest, oResponse, oPayload)
         {
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };
         
         var failureHandler = function MD__oFC_failure(sRequest, oResponse)
         {
            try
            {
               var response = YAHOO.lang.JSON.parse(oResponse.responseText);
               this.widgets.dataTable.set("MSG_ERROR", response.message);
               this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
            }
            catch(e)
            {
               // this._setDefaultDataTableErrors(this.widgets.dataTable);
            }
         };
         
         // Update the DataSource
         this.widgets.dataSource.sendRequest(this.currentFilter,
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },

      /**
       * Favourite document event handler
       *
       * @method onFavouriteDocument
       * @param row {object} DataTable row representing file to be actioned
       */
      onFavouriteDocument: function MD_onFavouriteDocument(row)
      {
         var record = this.widgets.dataTable.getRecord(row),
            file = record.getData(),
            nodeRef = file.nodeRef,
            undoIndex;
         
         file.isFavourite = !file.isFavourite;
         
         if (this.currentFilter == "favourites")
         {
            undoIndex = record.getData("index");
            this.widgets.dataTable.deleteRow(record);
         }
         else
         {
            this.widgets.dataTable.updateRow(record, file);
         }
               
         var responseConfig =
         {
            failureCallback:
            {
               fn: function MD_oFD_failure(event, obj)
               {
                  // Reset the flag to it's previous state
                  var record = obj.record,
                     file = record.getData();
                  
                  file.isFavourite = !file.isFavourite;
                  if (this.currentFilter == "favourites")
                  {
                     this.widgets.dataTable.addRow(file, obj.undoIndex);
                  }
                  else
                  {
                     this.widgets.dataTable.updateRow(record, file);
                  }
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.favourite.failure", file.displayName)
                  });
               },
               scope: this,
               obj:
               {
                  record: record,
                  undoIndex: undoIndex
               }
            }
         };

         var fnPref = file.isFavourite ? "add" : "remove";
         this.services.preferences[fnPref].call(this.services.preferences, Alfresco.service.Preferences.FAVOURITE_DOCUMENTS, nodeRef, responseConfig);
      }

   });
})();
