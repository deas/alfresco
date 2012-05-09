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
 * Dashboard SiteSearch component.
 *
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.SiteSearch
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Dashboard SiteSearch constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.SiteSearch} The new component instance
    * @constructor
    */
   Alfresco.dashlet.SiteSearch = function SiteSearch_constructor(htmlId)
   {
      Alfresco.dashlet.SiteSearch.superclass.constructor.call(this, "Alfresco.dashlet.SiteSearch", htmlId, ["container", "datasource", "datatable"]);

      return this;
   };

   YAHOO.extend(Alfresco.dashlet.SiteSearch, Alfresco.component.SearchBase,
   {
      buildNameWithHref: function(href, name)
      {
         return '<h3 class="itemname"> <a class="theme-color-1" href=' + href + '>' + name + '</a></h3>';
      },

      buildDesc: function(resultType, siteShortName, siteTitle)
      {
         var desc = '';

         var siteId = this.options.siteId;
         if (!(siteId && siteId != null))
         {
            desc = resultType + ' ' + this.msg("message.insite") + ' <a href="' + Alfresco.constants.URL_PAGECONTEXT + 'site/' + siteShortName + '/dashboard">' + $html(siteTitle) + '</a>';
         }

         return desc;
      },

      buildUrl: function()
      {
         var url = Alfresco.constants.PROXY_URI + "slingshot/search?term={term}&maxResults={maxResults}";

         var siteId = this.options.siteId;
         if (siteId && siteId != null)
         {
            url += "&site=" + siteId;
         }

         return YAHOO.lang.substitute(url,
         {
            term: encodeURIComponent(YAHOO.lang.trim(Dom.get(this.id + "-search-text").value)),
            maxResults: YAHOO.lang.trim(Dom.get(this.id + "-resultSize-button").innerHTML)
         });
      },

      doRequest: function doRequest()
      {
         this.widgets.alfrescoDataTable = new Alfresco.util.DataTable(
         {
            dataSource:
            {
               url: this.buildUrl(),
               config:
               {
                  responseSchema:
                  {
                     resultsList: 'items'
                  }
               }
            },
            dataTable:
            {
               container: this.id + "-search-results",
               columnDefinitions:
               [
                  {key: "site", formatter: this.bind(this.renderCellImg), width: 48},
                  {key: "path", formatter: this.bind(this.renderCellDesc)}
               ],
               config:
               {
                  MSG_EMPTY: this.msg("no.result")
               }
            }
         });
      },

      renderCellImg: function(elCell, oRecord, oColumn, oData)
      {
         elCell.innerHTML = this.buildThumbnailHtmlByRecordWithAdditionalParams(oRecord, 48, 48);
      },

      renderCellDesc: function(elCell, oRecord, oColumn, oData)
      {
         var type = oRecord.getData("type"),
            name = oRecord.getData("name"),
            displayName = oRecord.getData("displayName"),
            site = oRecord.getData("site"),
            path = oRecord.getData("path"),
            nodeRef = oRecord.getData("nodeRef"),
            container = oRecord.getData("container"),
            modifiedOn = oRecord.getData("modifiedOn"),
            siteShortName = site.shortName,
            siteTitle = site.title,
            modified = Alfresco.util.formatDate(Alfresco.util.fromISO8601(modifiedOn)),
            resultType = this.buildTextForType(type),
            href = this.getBrowseUrl(name, type, site, path, nodeRef, container, modified);

         elCell.innerHTML = this.buildNameWithHref(href, displayName) + this.buildDesc(resultType, siteShortName, siteTitle) + this.buildPath(type, path, site);
      },

      /**
       * Sets the max number of results. If a search has been done before a new search with
       * the new max number of results will be done
       *
       * @param p_sType {string} The event
       * @param p_aArgs {array} Event arguments
       */
      onResultSizeSelected: function(p_sType, p_aArgs)
      {
         var menuItem = p_aArgs[1];

         if (menuItem)
         {
            this.widgets.resultSizeMenuButton.set("label", menuItem.cfg.getProperty("text"));
            this.widgets.resultSizeMenuButton.value = menuItem.value;

            var dataTable = this.widgets.alfrescoDataTable;
            if (dataTable)
            {
               // change the inital value of "maxResults"
               var dataSource = dataTable.getDataTable().getDataSource();
               var url = dataSource.liveData;
               var urlSplit = url.split("?");
               var params = urlSplit[1].split("&");
               for (var i = 0; i < params.length; i++)
               {
                  if (params[i].split("=")[0] === "maxResults")
                  {
                     params[i] = "maxResults=" + menuItem.value;
                     url = urlSplit[0] + "?" + params.join("&");
                     break;
                  }
               }

               // change the inital url to the new one
               dataSource.liveData = url;

               // load data table
               dataTable.loadDataTable();
            }
         }
      },

      onReady: function onReady()
      {
         // Create result size menu
         this.widgets.resultSizeMenuButton = Alfresco.util.createYUIButton(this, "resultSize", this.onResultSizeSelected,
         {
            type: "menu",
            menu: "resultSize-menu",
            lazyloadmenu: false
         });

         this.widgets.resultSizeMenuButton.set("label", "10");
         this.widgets.resultSizeMenuButton.value = "10";

         // Display the toolbar now that we have selected the filter
         Dom.removeClass(Selector.query(".toolbar div", this.id, true), "hidden");

         var me = this;
         var id = this.id;

         Dom.get(id + "-search-text").onkeypress = function(e)
         {
            if (e.keyCode == YAHOO.util.KeyListener.KEY.ENTER)
            {
               me.doRequest();
            }
         };

         Dom.get(id + "-search-button").onclick = function(e)
         {
            me.doRequest();
         };
      }
   });
})();