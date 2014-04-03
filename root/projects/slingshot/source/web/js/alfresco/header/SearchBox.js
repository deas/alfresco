/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * @module alfresco/header/SearchBox
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @author Dave Draper
 * @author Kevin Roast
 */
define(["dojo/_base/declare",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dijit/_WidgetBase",
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/SearchBox.html",
        "dojo/text!./templates/LiveSearch.html",
        "dojo/text!./templates/LiveSearchItem.html",
        "alfresco/core/Core",
        "alfresco/core/CoreXhr",
        "alfresco/header/AlfMenuBar",
        "service/constants/Default",
        "dojo/json",
        "dojo/dom-attr",
        "dojo/dom-style",
        "dojo/dom-class",
        "dojo/dom-construct",
        "dojo/date/stamp",
        "dojo/on"], 
        function(declare, lang, array, _Widget, _Templated, SearchBoxTemplate, LiveSearchTemplate, LiveSearchItemTemplate, AlfCore, AlfXhr, AlfMenuBar, AlfConstants, JSON, DomAttr, DomStyle, DomClass, DomConstruct, Stamp, on) {

   /**
    * LiveSearch widget
    */
   var LiveSearch = declare([_Widget, _Templated, AlfCore], {
      
      /**
       * The scope to use for i18n messages.
       * 
       * @instance
       * @type {string}
       */
      i18nScope: "org.alfresco.SearchBox",
      
      searchBox: null,
      
      /**
       * DOM element container for Documents
       * 
       * @instance
       * @type {object}
       */
      containerNodeDocs: null,
      
      /**
       * DOM element container for Sites
       * 
       * @instance
       * @type {object}
       */
      containerNodeSites: null,
      
      /**
       * DOM element container for People
       * 
       * @instance
       * @type {object}
       */
      containerNodePeople: null,
      
      label: null,
      
      /**
       * @instance
       * @type {string}
       */
      templateString: LiveSearchTemplate,
      
      postMixInProperties: function alfresco_header_LiveSearch_postMixInProperties() {
         // construct our I18N labels ready for template
         this.label = {};
         array.forEach(["documents", "sites", "people", "clear", "more"], lang.hitch(this, function(msg) {
            this.label[msg] = this.message("search." + msg);
         }));
      },
      
      onSearchDocsMoreClick: function alfresco_header_LiveSearch_onSearchDocsMoreClick(evt) {
         this.searchBox.liveSearchDocuments(this.searchBox.lastSearchText, this.searchBox.resultsCounts["docs"]);
         evt.preventDefault();
      },
      
      onSearchClearClick: function alfresco_header_LiveSearch_onSearchClearClick(evt) {
         this.searchBox.clearResults();
         evt.preventDefault();
      },
   });
   
   /**
    * LiveSearchItem widget
    */
   var LiveSearchItem = declare([_Widget, _Templated, AlfCore], {

      /**
       * @instance
       * @type {string}
       */
      templateString: LiveSearchItemTemplate
   });

   /**
    * alfresco/header/SearchBox widget
    */ 
   return declare([_Widget, _Templated, AlfCore, AlfXhr], {

      /**
       * The scope to use for i18n messages.
       * 
       * @instance
       * @type {string}
       */
      i18nScope: "org.alfresco.SearchBox",

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/SearchBox.css"}]
       */
      cssRequirements: [{cssFile: "./css/SearchBox.css"}],

      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/SearchBox.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/SearchBox.properties"}],

      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: SearchBoxTemplate,

      /**
       * @instance
       * @type {object}
       * @default null
       */
      _searchMenu: null,

      /**
       * @instance
       * @type {integer}
       * @default 212
       */
      _width: "212",

      /**
       * @instance
       * @type {string}
       * @default null
       */
      site: null,

      /**
       * @instance
       * @type {boolean}
       * @default true
       */
      advancedSearch: true,
      
      /**
       * @instance
       * @type {string}
       * @default null
       */
      lastSearchText: null,
      
      _keyRepeatWait: 250,
      
      _minimumSearchLength: 2,
      
      _resultPageSize: 5,
      
      _LiveSearch: null,
      
      _requests: null,
      
      /**
       * @instance
       * @type {string}
       * @default null
       */
      resultsCounts: null,

      /**
       * @instance
       */
      postCreate: function alfresco_header_SearchBox__postCreate() {

         this._requests = [];
         this.resultsCounts = {};
         
         DomAttr.set(this._searchTextNode, "id", "HEADER_SEARCHBOX_FORM_FIELD");
         DomAttr.set(this._searchTextNode, "placeholder", this.message("search.instruction"));
         on(this._searchTextNode, "keyup", lang.hitch(this, function(evt) {
            this.onSearchBoxKeyUp(evt);
         }));
         
         // construct the optional advanced search menu
         if (this.advancedSearch)
         {
            var currSite = lang.getObject("Alfresco.constants.SITE");

            this._searchMenu = new AlfMenuBar({
               widgets: [
                  {
                     name: "alfresco/header/AlfMenuBarPopup",
                     config: {
                        id: this.id + "_DROPDOWN_MENU",
                        showArrow: false,
                        label: "",
                        iconSrc: "js/alfresco/header/css/images/search-16-gray.png",
                        iconClass: "alf-search-icon",
                        widgets: [
                           {
                              name: "alfresco/menus/AlfMenuItem",
                              config: {
                                 id: this.id + "_ADVANCED_SEARCH",
                                 i18nScope: "org.alfresco.SearchBox",
                                 label: "search.advanced",
                                 targetUrl: (currSite ? "site/" + currSite + "/" : "") + "advsearch"
                              }
                           }
                        ]
                     }
                  }
               ]
            });
            this._searchMenu.placeAt(this._searchMenuNode);
            this._searchMenu.startup();
         }
         
         // construct the live search panel
         this._LiveSearch = new LiveSearch({
            searchBox: this
         });
         this._LiveSearch.placeAt(this._searchLiveNode);
         
         // event handlers to hide/show the panel
         on(window, "click", lang.hitch(this, function(evt) {
            DomStyle.set(this._LiveSearch.containerNode, "display", "none");
         }));
         on(this._searchTextNode, "click", lang.hitch(this, function(evt) {
            if (this.resultsCounts["docs"] > 0 || this.resultsCounts["sites"] > 0 || this.resultsCounts["people"] > 0)
            {
               DomStyle.set(this._LiveSearch.containerNode, "display", "block");
            }
            evt.stopPropagation();
         }));
         on(this._LiveSearch, "click", function(evt) {
            evt.stopPropagation();
         });
         
         this.addAccessibilityLabel();
      },

      /**
       * Handles keyup events that occur on the <input> element used for capturing search terms.
       * @instance
       * @param {object} evt The keyup event
       */
      onSearchBoxKeyUp: function alfresco_header_SearchBox__onSearchBoxKeyUp(evt) {
         var terms = lang.trim(this._searchTextNode.value);
         switch (evt.keyCode)
         {
            case 13:
            {
               if (terms.length !== 0)
               {
                  this.alfLog("log", "Search request for: ", terms);

                  var url = "search?t=" + encodeURIComponent(terms);
                  if (this.site != null)
                  {
                     url = "site/" + this.site + "/" + url;
                  }
   
                  this.alfPublish("ALF_NAVIGATE_TO_PAGE", { 
                     url: url,
                     type: "SHARE_PAGE_RELATIVE",
                     target: "CURRENT"
                  });
               }
               break;
            }
            default:
            {
               if (terms.length >= this._minimumSearchLength && terms !== this.lastSearchText)
               {
                  DomStyle.set(this._LiveSearch.containerNode, "display", "block");
                  
                  this.lastSearchText = terms;
                  
                  // abort previous XHR requests to ensure we don't display results from a previous potentially slower query
                  for (var i=0; i<this._requests.length; i++)
                  {
                     this._requests[i].cancel();
                  }
                  this._requests = [];
                  
                  // execute our live search queries in a few ms if user has not continued typing
                  var then = Date.now();
                  if (this._timeoutHandle)
                  {
                     clearTimeout(this._timeoutHandle);
                  }
                  var _this = this;
                  this._timeoutHandle = setTimeout(function() {
                     _this.liveSearchDocuments(terms, 0);
                     _this.liveSearchSites(terms, 0);
                     _this.liveSearchPeople(terms, 0);
                  }, this._keyRepeatWait);
               }
            }
         }
      },
      
      liveSearchDocuments: function alfresco_header_SearchBox_liveSearchDocuments(terms, startIndex) {
         var _this = this;
         this._documentsWaitTimeout = setTimeout(function() {
            DomClass.add(_this._LiveSearch.titleNodeDocs, "wait");
         }, 2000);
         
         this._requests.push(
            this.serviceXhr({
               url: AlfConstants.PROXY_URI + "slingshot/live-search-docs?t=" + encodeURIComponent(terms) + "&maxResults=" + this._resultPageSize + "&startIndex=" + startIndex,
               method: "GET",
               successCallback: function(response) {
                  clearTimeout(this._documentsWaitTimeout);
                  DomClass.remove(this._LiveSearch.titleNodeDocs, "wait");
                  if (startIndex === 0)
                  {
                     this._LiveSearch.containerNodeDocs.innerHTML = "";
                  }
                  
                  // construct each Document item as a LiveSearchItem widget
                  array.forEach(response.items, function(item) {
                     // construct the meta-data - site information, modified by and title description as tooltip
                     var site = (item.site ? "site/" + item.site.shortName + "/" : "");
                     var info = (item.site ? ("<a href='" + AlfConstants.URL_PAGECONTEXT + site + "documentlibrary'>" + this.encodeHTML(item.site.title) + "</a> | ") : "") + Stamp.fromISOString(item.modifiedOn).toGMTString() + " | <a href='" + AlfConstants.URL_PAGECONTEXT + "user/" + this.encodeHTML(item.modifiedBy) + "/profile'>" + this.encodeHTML(item.modifiedBy) + "</a>";
                     var desc = this.encodeHTML(item.title);
                     if (item.description) desc += (desc.length !== 0 ? "\r\n" : "") + this.encodeHTML(item.description);
                     // build the widget for the item - including the thumbnail url for the document
                     var itemLink = new LiveSearchItem({
                        cssClass: "alf-livesearch-thumbnail",
                        title: desc,
                        label: this.encodeHTML(item.name),
                        link: AlfConstants.URL_PAGECONTEXT + site + "document-details?nodeRef=" + item.nodeRef,
                        icon: AlfConstants.PROXY_URI + "api/node/" + item.nodeRef.replace(":/", "") + "/content/thumbnails/doclib?c=queue&ph=true&lastModified=" + (item.lastThumbnailModification || 1),
                        alt: this.encodeHTML(item.name),
                        meta: info
                     });
                     itemLink.placeAt(this._LiveSearch.containerNodeDocs);
                  }, this);
                  // the more action is added if more results are potentially available
                  DomStyle.set(this._LiveSearch.nodeDocsMore, "display", response.hasMoreRecords ? "block" : "none");
                  // record the count of results
                  if (startIndex === 0)
                  {
                     this.resultsCounts["docs"] = 0;
                  }
                  this.resultsCounts["docs"] += response.items.length;
               },
               failureCallback: function(response) {
                  clearTimeout(this._documentsWaitTimeout);
                  DomClass.remove(this._LiveSearch.titleNodeDocs, "wait");
                  DomStyle.set(this._LiveSearch.nodeDocsMore, "display", "none");
                  if (startIndex === 0)
                  {
                     this._LiveSearch.containerNodeDocs.innerHTML = "";
                     this.resultsCounts["docs"] = 0;
                  }
               },
               callbackScope: this
            }));
      },

      liveSearchSites: function alfresco_header_SearchBox_liveSearchSites(terms, startIndex) {
         var _this = this;
         this._sitesWaitTimeout = setTimeout(function() {
            DomClass.add(_this._LiveSearch.titleNodeSites, "wait");
         }, 2000);
         
         this._requests.push(
            this.serviceXhr({
               url: AlfConstants.PROXY_URI + "slingshot/live-search-sites?t=" + encodeURIComponent(terms) + "&maxResults=" + this._resultPageSize,
               method: "GET",
               successCallback: function(response) {
                  clearTimeout(this._sitesWaitTimeout);
                  DomClass.remove(this._LiveSearch.titleNodeSites, "wait");
                  this._LiveSearch.containerNodeSites.innerHTML = "";
                  
                  // construct each Site item as a LiveSearchItem widget
                  array.forEach(response.items, function(item) {
                     var itemLink = new LiveSearchItem({
                        cssClass: "alf-livesearch-icon",
                        title: this.encodeHTML(item.description),
                        label: this.encodeHTML(item.title),
                        link: AlfConstants.URL_PAGECONTEXT + "site/" + item.shortName + "/dashboard",
                        icon: AlfConstants.URL_RESCONTEXT + "components/images/filetypes/generic-site-32.png",
                        alt: this.encodeHTML(item.title),
                        meta: item.description ? this.encodeHTML(item.description) : "&nbsp;"
                     });
                     itemLink.placeAt(this._LiveSearch.containerNodeSites);
                  }, this);
                  this.resultsCounts["sites"] = response.items.length;
               },
               failureCallback: function(response) {
                  clearTimeout(this._sitesWaitTimeout);
                  DomClass.remove(this._LiveSearch.titleNodeSites, "wait");
                  this._LiveSearch.containerNodeSites.innerHTML = "";
                  this.resultsCounts["sites"] = 0;
               },
               callbackScope: this
            }));
      },
      
      liveSearchPeople: function alfresco_header_SearchBox_liveSearchPeople(terms, startIndex) {
         var _this = this;
         this._peopleWaitTimeout = setTimeout(function() {
            DomClass.add(_this._LiveSearch.titleNodePeople, "wait");
         }, 2000);
         
         this._requests.push(
            this.serviceXhr({
               url: AlfConstants.PROXY_URI + "slingshot/live-search-people?t=" + encodeURIComponent(terms) + "&maxResults=" + this._resultPageSize,
               method: "GET",
               successCallback: function(response) {
                  clearTimeout(this._peopleWaitTimeout);
                  DomClass.remove(this._LiveSearch.titleNodePeople, "wait");
                  this._LiveSearch.containerNodePeople.innerHTML = "";
                  
                  // construct each Person item as a LiveSearchItem widget
                  array.forEach(response.items, function(item) {
                     var fullName = item.firstName + " " + item.lastName;
                     var meta = this.encodeHTML(item.jobtitle) + (item.location ? (", "+this.encodeHTML(item.location)) : "");
                     var itemLink = new LiveSearchItem({
                        cssClass: "alf-livesearch-icon",
                        title: this.encodeHTML(item.jobtitle),
                        label: this.encodeHTML(fullName + " (" + item.userName + ")"),
                        link: AlfConstants.URL_PAGECONTEXT + "user/" + encodeURIComponent(item.userName) + "/profile",
                        icon: AlfConstants.PROXY_URI + "slingshot/profile/avatar/" + encodeURIComponent(item.userName) + "/thumbnail/avatar32",
                        alt: this.encodeHTML(fullName),
                        meta: meta ? meta : "&nbsp;"
                     });
                     itemLink.placeAt(this._LiveSearch.containerNodePeople);
                  }, this);
                  this.resultsCounts["people"] = response.items.length;
               },
               failureCallback: function(response) {
                  clearTimeout(this._peopleWaitTimeout);
                  DomClass.remove(this._LiveSearch.titleNodePeople, "wait");
                  this._LiveSearch.containerNodePeople.innerHTML = "";
                  this.resultsCounts["people"] = 0;
               },
               callbackScope: this
            }));
      },
      
      clearResults: function alfresco_header_SearchBox_clearResults()
      {
         this._searchTextNode.value = "";
         this.lastSearchText = "";
         
         for (var i=0; i<this._requests.length; i++)
         {
            this._requests[i].cancel();
         }
         this._requests = [];
         
         this.resultsCounts = {};
         this._LiveSearch.containerNodeDocs.innerHTML = "";
         this._LiveSearch.containerNodePeople.innerHTML = "";
         this._LiveSearch.containerNodeSites.innerHTML = "";
         DomClass.remove(this._LiveSearch.titleNodeDocs, "wait");
         DomClass.remove(this._LiveSearch.titleNodePeople, "wait");
         DomClass.remove(this._LiveSearch.titleNodeSites, "wait");
         
         DomStyle.set(this._LiveSearch.nodeDocsMore, "display", "none");
         
         DomStyle.set(this._LiveSearch.containerNode, "display", "none");
         
         this._searchTextNode.focus();
      },

      /**
       * When the search box loads, add a label to support accessibility
       * @instance
       */
      addAccessibilityLabel: function alfresco_header_SearchBox__addAccessibilityLabel() {
         DomConstruct.create("label", {
            "for": "HEADER_SEARCHBOX_FORM_FIELD",
            innerHTML: this.message("search.label"),
            "class": "hidden"
         }, this._searchTextNode, "before");
      }
   });
});
