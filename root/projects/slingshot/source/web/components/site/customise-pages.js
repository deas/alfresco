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
 * CustomisePages component.
 *
 * @namespace Alfresco
 * @class Alfresco.CustomisePages
 */
(function()
{

   var Dom = YAHOO.util.Dom, 
      Event = YAHOO.util.Event,
      DDM = YAHOO.util.DragDropMgr;

   /**
    * Alfresco.CustomisePages constructor.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.CustomisePages} The new CustomisePages instance
    * @constructor
    */
   Alfresco.CustomisePages = function(htmlId)
   {
      this.name = "Alfresco.CustomisePages";
      this.id = htmlId;
      
      this.widgets =
      {
         /**
          * Select buttons for each page
          *
          * @property selectButtons
          * @type object Contains other objects of type {pageId: YAHOO.util.Button}
          */
         selectButtons: {}
      }

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.CustomisePages.prototype =
   {
      /**
       * Object container for storing YUI widget instances.
       *
       * @property widgets
       * @type object
       */
      widgets: null,

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {

         /**
          * The id for the site who's pages are configured
          *
          * @property siteId
          * @type {string} The siteId
          */
         siteId: null,

         /**
          * The avaiable layouts
          *
          * @property layouts
          * @type {object} {"page.pageId":{pageId: "", title: "", description: ""}}
          */
         pages: {}
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.CustomisePages} returns 'this' for method chaining
       */
      setOptions: function CP_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this module.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.CustomisePages} returns 'this' for method chaining
       */
      setMessages: function CP_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function CP_onComponentsLoaded()
      {
         // Shortcut for dummy instance
         if (this.id === null)
         {
            return;
         }

         // Save reference to elements so we can hide and show later
         this.widgets.addPagesDiv = Dom.get(this.id + "-addPages-div");
         this.widgets.pagesDiv = Dom.get(this.id + "-pages-div");

         // Create references to control buttons and listen to events
         Event.addListener(this.id + "-closeAddPages-link", "click", this.onCloseAddPagesLinkClick, this, true);
         this.widgets.addPagesButton = Alfresco.util.createYUIButton(this, "addPages-button", this.onAddPagesButtonClick);
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "save-button", this.onSaveButtonClick);
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         // Create the select buttons for all layouts
         this.widgets.selectButtons = [];
         for (var pageId in this.options.pages)
         {
            // Save a reference to each remove image button and listen for click event
            Event.addListener(this.id + "-remove-link-" + pageId, "click", this.onRemoveButtonClick,
            {
               selectedPageId: pageId,
               thisComponent: this
            });

            // Save references and listen to clicks on each select button
            this.widgets.selectButtons[pageId] = Alfresco.util.createYUIButton(this, "select-button-" + pageId, this.onSelectButtonClick);
         }

         // Show or hide empty div labels
         this._adjustEmptyMessages();
      },

      /**
       * Fired when the user clicks one of the select buttons for a page.
       * Adds the selected page to the current pages.
       *
       * @method onSelectButtonClick
       * @param event {object} an "click" event
       */
      onSelectButtonClick: function CP_onSelectButtonClick(event, button)
      {
         // Find out what layout that is chosen by looking at the clicked button's id
         var buttonId = button.get("id");
         var selectedPageId = buttonId.substring((this.id + "-select-button-").length);

         // Hide the selected page from the available pages list and add it last to the current pages list
         Dom.setStyle(this.id + "-page-li-" + selectedPageId, "display", "none");
         var page = Dom.get(this.id + "-currentPage-li-" + selectedPageId);
         var container = page.parentNode;
         container.appendChild(page);
         Alfresco.util.Anim.fadeIn(page);

         /**
          * todo: To handle multiple instances of the same page:
          * - In html.ftl: Don't create unused pages in the currentPages div (now they are created but hidden)
          * - Create a hidden "used page template"-div in html.ftl
          * - In onComponentLoaded store a reference to the template in widgets
          * - Do a widgets.templateDiv.clone(true) and populate the template by using the selected Page's values from options.pages
          * - Insert the page in the end of the list like before  
          */

         // Show or hide empty div labels
         this._adjustEmptyMessages();
      },

      /**
       * Fired when the user clicks one of the add buttons for a page.
       * Displays the pages.
       *
       * @method onAddButtonClick
       * @param event {object} an "click" event
       */
      onAddPagesButtonClick: function CP_onAddPageButtonClick(event, button)
      {
         // Hide add dashlets button and fade in available dashlets
         Dom.setStyle(this.widgets.addPagesDiv, "display", "none");
         Alfresco.util.Anim.fadeIn(this.widgets.pagesDiv);
      },

      /**
       * Fired when the user clicks one of the close link.
       * Hides the pages.
       *
       * @method onCloseAddPagesLinkClick
       * @param event {object} an "click" event
       */
      onCloseAddPagesLinkClick: function CP_onCloseAddPagesLinkClick(event)
      {
         // Show add pages button and hide available pages
         Dom.setStyle(this.widgets.addPagesDiv, "display", "");
         Dom.setStyle(this.widgets.pagesDiv, "display", "none");
         Event.stopEvent(event);
      },

      /**
       * Fired when the user clicks one of the remove buttons for a page.
       * Removes the selected page from the current pages.
       *
       * @method onRemoveButtonClick
       * @param event {object} an "click" event
       */
      onRemoveButtonClick: function CP_onRemoveButtonClick(event, obj)
      {
         // Remove the page from the current pages list and add it last to the available pages list
         Dom.setStyle(obj.thisComponent.id + "-currentPage-li-" + obj.selectedPageId, "display", "none")
         var page = Dom.get(obj.thisComponent.id + "-page-li-" + obj.selectedPageId);
         var container = page.parentNode;
         container.appendChild(page);
         Alfresco.util.Anim.fadeIn(page);

         // Show or hide empty div labels
         obj.thisComponent._adjustEmptyMessages();
         
         Event.stopEvent(event);
      },


      /**
       * Fired when the user clicks the Save/Done button.
       * Saves the dashboard config and takes the user back to the dashboard page.
       *
       * @method onSaveButtonClick
       * @param event {object} a "click" event
       */
      onSaveButtonClick: function CD_onSaveButtonClick(event)
      {
         // Disable buttons to avoid double submits or cancel during post
         this.widgets.saveButton.set("disabled", true);
         this.widgets.cancelButton.set("disabled", true);

         // Loop through the columns to get the pages to save
         var pages = [];
         var children = Dom.getChildrenBy(Dom.get(this.id + "-currentPages-ul"), this._isRealPage);
         for (var i = 0; i < children.length; i++)
         {
            // Find the pageId by extracting part of its Dom id
            var li = children[i];
            var pageId = li.id.substring((this.id + "-currentPage-li-").length);

            // Create a page object to send to the server
            var page =
            {
               pageId: pageId
            };
            pages[pages.length] = page;
         }

         // Prepare the root object to send to the server
         var siteId = this.options.siteId;
         var dataObj =
         {
            siteId: siteId,
            pages: pages
         };

         // Do the request and send the user to the dashboard after wards
         Alfresco.util.Ajax.jsonRequest(
         {
            method: Alfresco.util.Ajax.POST,
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/site/customise-pages",
            dataObj: dataObj,
            successCallback:
            {
               fn: function()
               {
                  // Send the user to the newly configured dashboard
                  document.location.href = Alfresco.constants.URL_PAGECONTEXT + "site/" + siteId + "/dashboard";
               },
               scope: this
            },
            failureMessage: Alfresco.util.message("message.saveFailure", this.name),
            failureCallback:
            {
               fn: function()
               {
                  // Hide spinner
                  this.widgets.feedbackMessage.destroy();
                  
                  // Enable the buttons again
                  this.widgets.saveButton.set("disabled", false);
                  this.widgets.cancelButton.set("disabled", false);
               },
               scope: this
            }
         });

         // Display a spinning save message to the user 
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.saving", this.name),
            spanClass: "wait",
            displayTime: 0
         });

      },

      /**
       * Fired when the user clicks cancel layout button.
       * Takes the user to the sites dashboard
       *
       * @method onCancelButtonClick
       * @param event {object} an "click" event
       */
      onCancelButtonClick: function CP_onCancelButtonClick(event)
      {
         // Take the user back to the sites dashboard
         document.location.href = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/dashboard";
      },

      /**
       * If the current page list or the avialable page list is empty a div
       * with a simple label should be displayed to tell the user of this.
       * This method checks both of the list.
       *
       * @method _adjustEmptyMessages
       * @private
       */
      _adjustEmptyMessages: function CP_adjustEmptyMessages()
      {
         this._adjustEmptyMessage(Dom.get(this.id + "-pages-empty-li"));
         this._adjustEmptyMessage(Dom.get(this.id + "-currentPages-empty-li"));
      },

      /**
       * Takes a div element with an "empty" label and looks if its parent
       * ul node contains any li that represent pages.
       * If it doesnt it displays li/the empty div label.
       *
       * @method _adjustEmptyMessage
       * @param li an HTMLElement of type li
       * @private
       */
      _adjustEmptyMessage: function CP_adjustEmptyMessage(li)
      {
         var parentUl = li.parentNode;
         var children = Dom.getChildrenBy(parentUl, this._isRealPage);
         if (children.length > 0)
         {
            Dom.setStyle(li, "display", "none");
         }
         else
         {
            Dom.setStyle(li, "display", "");
         }
      },


      /**
       * Tests if the li elemenet supplied represents a page.
       *
       * @method _isRealPage
       * @param el an HTMLElement of type li
       * @return true if el represents a page 
       * @private
       */
      _isRealPage: function (el)
      {
         return el.tagName.toLowerCase() == ("li") &&
                !Dom.hasClass(el, "empty") &&
                Dom.getStyle(el, "display") != "none";
      }

   }

})();

/* Dummy instance to load optional YUI components early */
new Alfresco.CustomisePages(null);
