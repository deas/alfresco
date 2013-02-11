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
define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "dijit/_OnDijitClickMixin",
        "dijit/_TemplatedMixin",
        "dijit/_FocusMixin",
        "dojo/text!./templates/SearchBox.html",
        "alfresco/core/Core",
        "alfresco/header/AlfMenuBar",
        "dojo/_base/fx",
        "dojo/dom-attr",
        "dojo/dom-construct",
        "dojo/on"], 
        function(declare, _WidgetBase, _OnDijitClickMixin, _TemplatedMixin, _FocusMixin, template,  AlfCore, AlfMenuBar, fx, domAttr, domConstruct, on) {
   
   return declare([_WidgetBase, _OnDijitClickMixin, _TemplatedMixin, _FocusMixin, AlfCore], {
      
      /**
       * The scope to use for i18n messages.
       * 
       * @property i18nScope {String}
       */
      i18nScope: "org.alfresco.SearchBox",
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/SearchBox.css"}],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @property i18nRequirements {Array}
       */
      i18nRequirements: [{i18nFile: "./i18n/SearchBox.properties"}],
      
      /**
       * The HTML template to use for the widget.
       * @property template {String}
       */
      templateString: template,

      /**
       * @property {object} _searchMenu This should be instantiated with the menu bar widget for the search options.
       * @default null
       */
      _searchMenu: null,
      
      /**
       * @property {integer} _focusedWidth The width of the search box when it is focused (in pixels)
       * @default 250
       */
      _focusedWidth: "250",
      
      /**
       * @property {integer} _blurredWidth The width of the search box when it does not have focus (in pixels)
       * @default 100
       */
      _blurredWidth: "100",
      
      /**
       * @method postCreate
       */
      postCreate: function alfresco_header_SearchBox__postCreate() {
         var _this = this;
         domAttr.set(this._searchTextNode, "value", this.message("search.instruction"));
         on(this._searchTextNode, "keydown", function(evt) {
            _this.onSearchBoxKeyDown(evt);
         });
         
         this._searchMenu = new AlfMenuBar({
            widgets: [
               {
                  name: "alfresco/header/AlfMenuBarPopup",
                  config: {
                     showArrow: false,
                     label: "",
                     iconClass: "alf-search-icon",
                     widgets: [
                        {
                           name: "alfresco/menus/AlfMenuItem",
                           config: {
                              i18nScope: "org.alfresco.SearchBox",
                              label: "search.advanced",
                              targetUrl: "advsearch"
                           }
                        }
                     ]
                  }
               }
            ]
         });
         this._searchMenu.placeAt(this._searchMenuNode);
         this._searchMenu.startup();
      },
      
      /**
       * Handles keydown events that occur on the <input> element used for capturing search terms.
       * @method onSearchBoxKeyDown. If the key pressed is the the "enter" key then the search
       * page will be loaded with the contents of the input element passed as a request parameter.
       * @param {object} evt The keydown event
       */
      onSearchBoxKeyDown: function alfresco_header_SearchBox__onSearchBoxKeyDown(evt) {
         if (evt.charCode == 0 && evt.keyCode == 13)
         {
            this.alfLog("log", "Search request for: ", this._searchTextNode.value);
            this.alfPublish("ALF_NAVIGATE_TO_PAGE", { 
               url: "search?t=" + this._searchTextNode.value,
               type: "SHARE_PAGE_RELATIVE",
               target: "CURRENT"
            });
         }
      },
      
      /**
       * Implements the dijit/_FocusMixin callback to expand the width of the search box input field when this
       * widget gains focus.
       *  
       * @method _onFocus
       */
      _onFocus: function alfresco_header_SearchBox___onFocus() {
         domAttr.set(this._searchTextNode, "value", "");
         fx.animateProperty({
            node: this._searchTextNode,
            properties: {
              width: this._focusedWidth
            }
         }).play();
         this.inherited(arguments);
         this._searchTextNode.focus();
     },
     
     /**
      * Implements the dijit/_FocusMixin callback to contract the width of the search box input field when this
      * widget loses focus.
      * 
      * @method _onBlur
      */
     _onBlur: function alfresco_header_SearchBox___onBlur(){
         fx.animateProperty({
            node: this._searchTextNode,
            properties: {
              width: this._blurredWidth
            }
         }).play();
         this.inherited(arguments);
         domAttr.set(this._searchTextNode, "value", this.message("search.instruction"));
     }
   });
});