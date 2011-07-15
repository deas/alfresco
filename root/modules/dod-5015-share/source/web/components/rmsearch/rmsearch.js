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
 * Records Search component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsSearch
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
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * Search constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsSearch} The new RecordsSearch instance
    * @constructor
    */
   Alfresco.RecordsSearch = function(htmlId)
   {
      /* Mandatory properties */
      this.name = "Alfresco.RecordsSearch";
      
      /* Super class constructor call */
      Alfresco.RecordsSearch.superclass.constructor.call(this, htmlId);
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "calendar", "container", "datasource", "datatable", "json", "menu", "tabview"], this.onComponentsLoaded, this);
      
      YAHOO.Bubbling.on("savedSearchAdded", this.onSavedSearchAdded, this);
      YAHOO.Bubbling.on("searchComplete", this.onSearchComplete, this);
      YAHOO.Bubbling.on("PropertyMenuSelected", this.onPropertyMenuSelected, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.RecordsSearch, Alfresco.RecordsResults,
   {
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function RecordsSearch_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsSearch_onReady()
      {
         var me = this;
         
         // Wire up tab component
         this.widgets.tabs = new YAHOO.widget.TabView(this.id + "-tabs");
         
         // Buttons
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.onSearchClick);
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "savesearch-button", this.onSaveSearch);
         this.widgets.newButton = Alfresco.util.createYUIButton(this, "newsearch-button", this.onNewSearch);
         this.widgets.printButton = Alfresco.util.createYUIButton(this, "print-button", this.onPrint);
         this.widgets.printButton.set("disabled", true);
         this.widgets.exportButton = Alfresco.util.createYUIButton(this, "export-button", this.onExport);
         this.widgets.exportButton.set("disabled", true);
         
         // retrieve the public saved searches
         // TODO: user specific searches?
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "slingshot/rmsavedsearches/site/" + this.options.siteId,
            successCallback:
            {
               fn: this.onSavedSearchesLoaded,
               scope: this
            },
            failureMessage: me._msg("message.errorloadsearches")
         });
         
         // construct the date picker calendar
         var theDate = new Date();
         var page = (theDate.getMonth() + 1) + "/" + theDate.getFullYear();
         var selected = (theDate.getMonth() + 1) + "/" + theDate.getDate() + "/" + theDate.getFullYear();   
         this.widgets.calendar = new YAHOO.widget.Calendar(null, this.id + "-date", { title: this._msg("message.selectdate"), close: true });
         this.widgets.calendar.cfg.setProperty("pagedate", page);
         this.widgets.calendar.cfg.setProperty("selected", selected);
         
         // setup date picker events
         this.widgets.calendar.selectEvent.subscribe(this.onDatePickerSelection, this, true);
         Event.addListener(this.id + "-date-icon", "click", function () { this.widgets.calendar.show(); }, this, true);
         
         // render the calendar control
         this.widgets.calendar.render();
         
         // wire up misc events
         Event.on(me.id + "-records", "change", this.onRecordsCheckChanged, this, true);
         
         // Call super class onReady() method
         Alfresco.RecordsSearch.superclass.onReady.call(this);
      },
      
      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */
       
      /**
       * Handles the date being changed in the date picker YUI control.
       * 
       * @method onDatePickerSelection
       * @param type
       * @param args
       * @param obj
       * @private
       */
      onDatePickerSelection: function RecordsSearch_onDatePickerSelection(type, args, obj)
      {
         // update the date field - contains an array of [year, month, day]
         var selected = args[0][0];
         this.widgets.calendar.hide();
         
         // convert to query date format and insert
         var date = YAHOO.lang.substitute('"{year}-{month}-{day}"',
         {
            year: selected[0],
            month: Alfresco.util.pad(selected[1], 2),
            day: Alfresco.util.pad(selected[2], 2)
         });
         Alfresco.util.insertAtCursor(Dom.get(this.id + "-terms"), date);
      },
      
      /**
       * Saved Searches AJAX success callback
       * 
       * @method onSavedSearchesLoaded
       * @param res {object} Server response
       */
      onSavedSearchesLoaded: function RecordsSearch_onSavedSearchesLoaded(res)
      {
         var me = this;
         
         var searches = this.options.savedSearches,
             items = YAHOO.lang.JSON.parse(res.serverResponse.responseText).items,
             index;
         
         for (index in items)
         {
            if (items.hasOwnProperty(index))
            {
               s = items[index];
               searches.push(
               {
                  id: s.name,
                  label: s.name,
                  description: s.description,
                  query: s.query,
                  params: s.params,
                  sort: s.sort
               });
            }
         }
         
         this._initSavedSearchMenu();
      },
      
      /**
       * Records checkbox change event handler
       * 
       * @method onRecordsCheckChanged
       * @param e {object} DomEvent
       */
      onRecordsCheckChanged: function RecordsSearch_onRecordsCheckChanged(e)
      {
         var disable = !(Dom.get(this.id + "-records").checked);
         Dom.get(this.id + "-undeclared").disabled = disable;
         Dom.get(this.id + "-vital").disabled = disable;
      },
      
      /**
       * Search button click event handler
       * 
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onSearchClick: function RecordsSearch_onSearchClick(e, args)
      {
         // switch to results tab
         this._clearSearchResults();
         this.widgets.tabs.selectTab(1);
         
         // execute the search and populate the results
         var query = this._getSearchQuery();
         if (query != null)
         {
        	var filters = this._getSearchFilters();
            this._performSearch(query, filters);
         }
      },
      
      /**
       * Save Search button click event handler
       * 
       * @method onSaveSearch
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onSaveSearch: function RecordsSearch_onSaveSearch(e, args)
      {
         // get values to pass to the module
         var query = this._getSearchQuery();
         if (query != null)
         {
            // build up params to pass to the module
            // query terms
            var termsElem = Dom.get(this.id + "-terms");
            var terms = YAHOO.lang.trim(termsElem.value);
            var params = "terms=" + encodeURIComponent(terms);
            // search components
            params += "&records=" + (Dom.get(this.id + "-records").checked);
            params += "&undeclared=" + (Dom.get(this.id + "-undeclared").checked);
            params += "&vital=" + (Dom.get(this.id + "-vital").checked);
            params += "&folders=" + (Dom.get(this.id + "-folders").checked);
            params += "&categories=" + (Dom.get(this.id + "-categories").checked);
            params += "&series=" + (Dom.get(this.id + "-series").checked);
            params += "&frozen=" + (Dom.get(this.id + "-frozen").checked);
            params += "&cutoff=" + (Dom.get(this.id + "-cutoff").checked);
            
            // TODO: prepopulate dialog with current saved search name if any selected
            
            // display the SaveSearch module dialog
            var module = Alfresco.module.getSaveSearchInstance();
            module.setOptions(
               {
                  siteId: this.options.siteId,
                  query: query,
                  params: params,
                  sort: this._buildSortParam()
               });
            module.show();
         }
      },
      
      /**
       * New Search button click event handler
       * 
       * @method onNewSearch
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onNewSearch: function RecordsSearch_onNewSearch(e, args)
      {
         // reset fields and clear values
         if (this.widgets.savedSearchMenu)
         {
            this.widgets.savedSearchMenu.set("label", this._msg("button.savedsearches"));
         }
         Dom.get(this.id + "-records").checked = true;
         Dom.get(this.id + "-undeclared").disabled = false;
         Dom.get(this.id + "-vital").disabled = false;
         Dom.get(this.id + "-undeclared").checked = false;
         Dom.get(this.id + "-vital").checked = false;
         Dom.get(this.id + "-folders").checked = true;
         Dom.get(this.id + "-categories").checked = false;
         Dom.get(this.id + "-series").checked = false;
         Dom.get(this.id + "-frozen").checked = false;
         Dom.get(this.id + "-cutoff").checked = false;
         Dom.get(this.id + "-terms").value = "";
         
         // reset sorting options
         for (var i=0, j=this.widgets.sortMenus.length; i<j; i++)
         {
            var menu = this.widgets.sortMenus[i],
                menuItems = menu.getMenu().getItems();
            menu.set("label", menuItems[0].cfg.getProperty("text"));
            this.sortby[i].field = menuItems[0].value;
            
            var orderMenu = this.widgets.sortOrderMenus[i],
                orderMenuItems = orderMenu.getMenu().getItems();
            orderMenu.set("label", orderMenuItems[0].cfg.getProperty("text"));
            this.sortby[i].order = orderMenuItems[0].value;
         }
         
         // switch to query builder tab
         this.widgets.tabs.selectTab(0);
      },
      
      /**
       * Saved Search has been added
       *
       * @method onSavedSearchAdded
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSavedSearchAdded: function RecordsSearch_onSavedSearchAdded(layer, args)
      {
         var searchObj = args[1];
         if (searchObj)
         {
            // add to our search to the list - if not already present
            var found = false;
            for (var i=0, j=this.options.savedSearches.length; i<j; i++)
            {
               var search = this.options.savedSearches[i];
               if (search.label === searchObj.label)
               {
                  // replace existing value
                  this.options.savedSearches[i] = searchObj;
                  found = true;
                  break;
               }
            }
            if (!found)
            {
               this.options.savedSearches.push(searchObj);
            }
            
            // rebuild the menu component
            this._initSavedSearchMenu();
            
            // refresh Saved Searches menu button label to the selected item
            this.widgets.savedSearchMenu.set("label", searchObj.label);
         }
      },
      
      /**
       * Search has been complete
       *
       * @method onSearchComplete
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSearchComplete: function RecordsSearch_onSearchComplete(layer, args)
      {
         var resultCount = args[1].count;
         var disable = (resultCount == 0);
         this.widgets.printButton.set("disabled", disable);
         this.widgets.exportButton.set("disabled", disable);
      },
      
      /**
       * Print button click event handler
       * 
       * @method onPrint
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onPrint: function RecordsSearch_onPrint(e, args)
      {
         // simple and quick way to give a "print friendly" layout - the YUI grid print output
         // is acceptable, so remove the header and footer areas from Share and Search screen
         if (Dom.getStyle("alf-hd", "display") !== "none")
         {
            this.widgets.printButton.set("label", this._msg("button.screen"));
            Dom.setStyle("alf-hd", "display", "none");
            Dom.setStyle("alf-ft", "display", "none");
            Dom.setStyle(this.id + "-tabset", "display", "none");
            Dom.setStyle(this.id + "-header", "display", "none");
         }
         else
         {
            this.widgets.printButton.set("label", this._msg("button.print"));
            Dom.setStyle("alf-hd", "display", "");
            Dom.setStyle("alf-ft", "display", "");
            Dom.setStyle(this.id + "-tabset", "display", "");
            Dom.setStyle(this.id + "-header", "display", "");
         }
      },
      
      /**
       * Export button click event handler
       * 
       * @method onExport
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onExport: function RecordsSearch_onExport(e, args)
      {
         // generate a Form as we need to POST the noderefs to the server,
         // the binary response can then be handled by the browser and present
         // the Save As dialog correctly - as an Ajax request would not do this
         var form = document.createElement("form");
         document.body.appendChild(form);
         form.id = this.id + "_export_form";
         form.name = form.id;
         form.style.display = "none";
         form.method = "post";
         form.enctype = "multipart/form-data";
         form.encoding = "multipart/form-data";
         form.action = Alfresco.constants.PROXY_URI + "api/rma/admin/export";
         
         var input = document.createElement("input");
         input.type = "hidden";
         form.appendChild(input);
         input.name = "nodeRefs";
         input.value = this.resultNodeRefs.join(",");
         form.submit();
      },
      
      /**
       * Bubbling event handler called when a value from the field insert selection menu has been picked
       */
      onPropertyMenuSelected: function RecordsSearch_onPropertyMenuSelected(e, args)
      {
         var item = args[1];
         
         // get the namespaced attribute name (e.g. rma:location) and insert
         var attribute = ' ' + item.value + ':';
         Alfresco.util.insertAtCursor(Dom.get(this.id + "-terms"), attribute);
      },
      
      _getSearchFilters: function RecordsSearch__getSearchFilters()
      {
    	  var filters = "";
    	  
    	  filters += "records/";
    	  if (Dom.get(this.id + "-records").checked)
    	  {
    		  filters += "true";
    	  }
    	  else
    	  {
    		  filters += "false";
    	  }
    	  
    	  filters += ",undeclared/";
    	  if (Dom.get(this.id + "-undeclared").checked)
    	  {
    		  filters += "true";
    	  }
    	  else
    	  {
    		  filters += "false";
    	  }

    	  filters += ",vital/";
    	  if (Dom.get(this.id + "-vital").checked)
    	  {
			  filters += "true";
		  }
		  else
		  {
			  filters += "false";
		  }
    	  
    	  filters += ",folders/";
    	  if (Dom.get(this.id + "-folders").checked)
    	  {
    		  filters += "true";
    	  }
    	  else
    	  {
    		  filters += "false";
    	  }
    	  
    	  filters += ",categories/";
    	  if (Dom.get(this.id + "-categories").checked)
    	  {
    		  filters += "true";
    	  }
    	  else
    	  {
    		  filters += "false";
    	  }
    	  
    	  filters += ",series/";
    	  if (Dom.get(this.id + "-series").checked)
    	  {
    		  filters += "true";
    	  }
    	  else
    	  {
    		  filters += "false";
    	  }
    	  
    	  filters += ",frozen/";
    	  if (Dom.get(this.id + "-frozen").checked)
    	  {
    		  filters += "true";
    	  }
    	  else
    	  {
    		  filters += "false";
    	  }
    	  
    	  filters += ",cutoff/";
    	  if ( Dom.get(this.id + "-cutoff").checked)
    	  {
    		  filters += "true";
    	  }
    	  else
    	  {
    		  filters += "false";
    	  }
    	  
    	  return filters;
      },      
      
      /**
       * Gets the search query entered by the user.
       */
      _getSearchQuery: function RecordsSearch__getSearchQuery()
      {
         var queryElem = Dom.get(this.id + "-terms");
         var userQuery = YAHOO.lang.trim(queryElem.value);
         return userQuery;
      },
      
      /**
       * Inits the Saved Searches menu component and handlers.
       *
       * @method _initSavedSearchMenu
       * @private
       */
      _initSavedSearchMenu: function RecordsSearch__initSavedSearchMenu()
      {
         var me = this;
         
         // Saved Searches menu
         this.widgets.savedSearchMenu = new YAHOO.widget.Button(this.id + "-savedsearches-button",
         {
            type: "menu",
            menu: this._buildSavedSearchesMenu()
         });
         
         // Click handler for Saved Search menu items
         this.widgets.savedSearchMenu.getMenu().subscribe("click", function(p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               // Update the menu button label to be the selected Saved Search
               me.widgets.savedSearchMenu.set("label", menuItem.cfg.getProperty("text"));
               
               // Rebuild search UI based on saved search object
               var searchObj = me.options.savedSearches[menuItem.value];
               
               // Sort options are packed into a single string comma separated
               // in "property/dir" packed format i.e. "cm:name/asc,cm:title/desc"
               var sorts = (searchObj.sort ? searchObj.sort.split(",") : []);
               for (var i in sorts)
               {
                  // get the correct sort menu and calculate the selected item from the sort value
                  var pair = sorts[i].split("/"),
                      menu = me.widgets.sortMenus[i],
                      menuItems = menu.getMenu().getItems();
                  for (var m in menuItems)
                  {
                     if (menuItems[m].value === pair[0])
                     {
                        // apply selected sort field to menu
                        menu.set("label", menuItems[m].cfg.getProperty("text"));
                        
                        // also keep track of the current sort field
                        me.sortby[i].field = pair[0];
                        
                        // apply selected sort direction to menu
                        var sortDirIndex = (pair[1] === "asc" ? 0 : 1);
                        var sortDirMenuItem = me.widgets.sortOrderMenus[i].getMenu().getItems()[sortDirIndex];
                        me.widgets.sortOrderMenus[i].set("label", sortDirMenuItem.cfg.getProperty("text"));
                        me.sortby[i].order = pair[1];
                        
                        break;
                     }
                  }
               }
               
               // Params are packed into a single string URL encoded
               var params = (searchObj.params ? searchObj.params.split("&") : []);
               for (var i in params)
               {
                  var pair = params[i].split("=");
                  switch (pair[0])
                  {
                     case "records":
                     {
                        Dom.get(me.id + "-records").checked = (pair[1] === "true");
                        break;
                     }
                     
                     case "undeclared":
                     {
                        Dom.get(me.id + "-undeclared").checked = (pair[1] === "true");
                        break;
                     }
                     
                     case "vital":
                     {
                        Dom.get(me.id + "-vital").checked = (pair[1] === "true");
                        break;
                     }
                     
                     case "folders":
                     {
                        Dom.get(me.id + "-folders").checked = (pair[1] === "true");
                        break;
                     }
                     
                     case "categories":
                     {
                        Dom.get(me.id + "-categories").checked = (pair[1] === "true");
                        break;
                     }
                     
                     case "series":
                     {
                        Dom.get(me.id + "-series").checked = (pair[1] === "true");
                        break;
                     }
                     
                     case "frozen":
                     {
                        Dom.get(me.id + "-frozen").checked = (pair[1] === "true");
                        break;
                     }
                     
                     case "cutoff":
                     {
                        Dom.get(me.id + "-cutoff").checked = (pair[1] === "true");
                        break;
                     }
                     
                     case "terms":
                     {
                        Dom.get(me.id + "-terms").value = decodeURIComponent(pair[1]);
                        break;
                     }
                  }
               }
               
               // switch to query builder tab
               me.widgets.tabs.selectTab(0);
            }
         });
      },
      
      /**
       * Builds the menuitems for the Saved Searches menu based on the list of saved searches.
       *
       * @method _buildSavedSearchesMenu
       * @return {object} Saved Searches menu item objects
       * @private
       */
      _buildSavedSearchesMenu: function RecordsSearch__buildSavedSearchesMenu()
      {
         this.options.savedSearches.sort(this._sortByLabel);
         var searches = [];
         for (var i=0, j=this.options.savedSearches.length; i<j; i++)
         {
            var search = this.options.savedSearches[i];
            searches.push(
            {
               text: search.label,
               value: i
            });
         }
         return searches;
      },
      
      /**
       * Helper to Array.sort() by the 'label' field of an object..
       *
       * @method _sortByLabel
       * @return {Number}
       * @private
       */
      _sortByLabel: function RecordsSearch__sortByLabel(s1, s2)
      {
         var ss1 = s1.label.toLowerCase(), ss2 = s2.label.toLowerCase();
         return (ss1 > ss2) ? 1 : (ss1 < ss2) ? -1 : 0;
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function RecordsSearch__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.RecordsSearch", Array.prototype.slice.call(arguments).slice(1));
      }
   });
})();