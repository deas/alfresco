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
 * Folder links component.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderLinks
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
   var $combine = Alfresco.util.combinePaths;
   
   /**
    * FolderLinks constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.FolderLinks} The new FolderLinks instance
    * @constructor
    */
   Alfresco.FolderLinks = function(htmlId)
   {
      Alfresco.FolderLinks.superclass.constructor.call(this, "Alfresco.FolderLinks", htmlId, []);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("folderDetailsAvailable", this.onFolderDetailsAvailable, this);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.FolderLinks, Alfresco.component.Base,
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
          * Repository Url if configured
          *
          * @property repositoryUrl
          * @type string
          */
         repositoryUrl: null
      },

      /**
       * Event handler called when the "folderDetailsAvailable" event is received
       */
      onFolderDetailsAvailable: function FolderLinks_onFolderDetailsAvailable(layer, args)
      {
         var folderData = args[1].folderDetails;
         
         if (this.options.repositoryUrl)
         {
            // WebDAV URL
            this.populateLinkUI(
            {
               name: "webdav",
               url: $combine(this.options.repositoryUrl, folderData.webdavUrl)
            });
         }

         // This page
         this.populateLinkUI(
         {
            name: "page",
            url: window.location.href
         });
      },


      /**
       * Populate a link UI element
       *
       * @method _populateLinkUI
       * @param link {object} Object literal containing link details
       */
      populateLinkUI: function DocumentLinks_populateLinkUI(link)
      {
         var nameId = this.id + "-" + link.name,
            urlId = nameId + "-url",
            copyButtonName = link.name + "-button";

         if (Dom.get(nameId))
         {
            Dom.removeClass(nameId, "hidden");
            Dom.get(urlId).value = link.url;
            if (this.hasClipboard)
            {
               Alfresco.util.createYUIButton(this, copyButtonName, null,
               {
                  onclick:
                  {
                     fn: this._handleCopyClick,
                     obj: urlId,
                     scope: this
                  }
               });
            }

            // Add focus event handler
            Event.addListener(urlId, "focus", this._handleFocus, urlId, this);
         }
      },

      /**
       * Event handler to copy URLs to the system clipboard
       *
       * @method _handleCopyClick
       * @param event {object} The event
       * @param urlId {string} The Dom Id of the element holding the URL to copy
       */
      _handleCopyClick: function DocumentLinks__handleCopyClick(event, urlId)
      {
         clipboardData.setData("Text", Dom.get(urlId).value);
      },

      /**
       * Event handler used to select text in the field when focus is received
       *
       * @method _handleFocus
       * @param event The event
       * @field The Dom Id of the field to select
       */
      _handleFocus: function DocumentLinks__handleFocus(e, fieldId)
      {
         YAHOO.util.Event.stopEvent(e);

         var fieldObj = Dom.get(fieldId);
         if (fieldObj && typeof fieldObj.select == "function")
         {
            fieldObj.select();
         }
      }

   });
})();
