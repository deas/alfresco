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
 * @module alfresco/header/SearchBox
 * @extends dijit/_WidgetBase
 * @mixes dijit/_OnDijitClickMixin
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dojo/_base/lang",
        "dijit/_WidgetBase",
        "dijit/_OnDijitClickMixin",
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/SearchBox.html",
        "alfresco/core/Core",
        "alfresco/header/AlfMenuBar",
        "dojo/_base/fx",
        "dojo/dom-attr",
        "dojo/dom-construct",
        "dojo/on"], 
        function(declare, lang, _WidgetBase, _OnDijitClickMixin, _TemplatedMixin, template,  AlfCore, AlfMenuBar, fx, domAttr, domConstruct, on) {

   return declare([_WidgetBase, _OnDijitClickMixin, _TemplatedMixin, AlfCore], {

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
      cssRequirements: [{cssFile:"./css/SearchBox.css"}],

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
      templateString: template,

      /**
       * @instance
       * @type {object}
       * @default null
       */
      _searchMenu: null,

      /**
       * @instance
       * @type {integer}
       * @default 250
       */
      _focusedWidth: "250",

      /**
       * @instance
       * @type {integer}
       * @default 100
       */
      _blurredWidth: "100",

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
       */
      postCreate: function alfresco_header_SearchBox__postCreate() {

         var _this = this;
         domAttr.set(this._searchTextNode, "id", "HEADER_SEARCHBOX_FORM_FIELD");
         domAttr.set(this._searchTextNode, "value", this.message("search.instruction"));
         on(this._searchTextNode, "keydown", function(evt) {
            _this.onSearchBoxKeyDown(evt);
         });

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

         this.addAccessibilityLabel();

      },

      /**
       * Handles keydown events that occur on the <input> element used for capturing search terms.
       * @instance
       * @param {object} evt The keydown event
       */
      onSearchBoxKeyDown: function alfresco_header_SearchBox__onSearchBoxKeyDown(evt) {
         if (evt.keyCode === 13)
         {
            var terms = lang.trim(this._searchTextNode.value);
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
         }
      },

      /**
       * When the search node gains focus then search instruction should be removed. 
       * @instance
       */
      onSearchNodeFocus: function alfresco_header_SearchBox__onSearchNodeFocus() {
         domAttr.set(this._searchTextNode, "value", "");
         this._searchTextNode.focus();
      },

      /**
       * When the search node loses focus the search instruction should be reset.
       * @instance
       */
      onSearchNodeBlur: function alfresco_header_SearchBox__onSearchNodeBlur() {
         domAttr.set(this._searchTextNode, "value", this.message("search.instruction"));
      },

      /**
       * When the search box loads, add a label to support accessibility
       * @instance
       */
      addAccessibilityLabel: function alfresco_header_SearchBox__addAccessibilityLabel() {
         domConstruct.create("label", {
            "for": "HEADER_SEARCHBOX_FORM_FIELD",
            innerHTML: this.message("search.label"),
            "class": "hidden"
         }, this._searchTextNode, "before");
      }

   });
});