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
 * Document actions component - DOD5015 extensions.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsDocumentReferences
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Sel = YAHOO.util.Selector;

   /**
    * RecordsDocumentReferences constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsDocumentReferences} The new RecordsDocumentReferences instance
    * @constructor
    */
   Alfresco.RecordsDocumentReferences = function(htmlId)
   {
      Alfresco.RecordsDocumentReferences.superclass.constructor.call(this, "Alfresco.RecordsDocumentReferences", htmlId);
      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);
      return this;
   };
   
   /**
    * Extend from Alfresco.DocumentActions
    */
   YAHOO.extend(Alfresco.RecordsDocumentReferences, Alfresco.component.Base,
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
          * Fileplan nodeRef
          *
          * @property filePlanNodeRef
          * @type String
          */
         filePlanNodeRef: null,

         /**
          * Document name
          *
          * @property docName
          * @type String
          */
         docName: null
      },
      

      /**
       * Event handler for documentDetailsAvailable bubbling event
       * 
       * @method onDocumentDetailsAvailable
       * @param e {object} Event
       * @param args {object} Event arguments
       */
      onDocumentDetailsAvailable: function RecordsDocumentReferences_onDocumentDetailsAvailable(e, args)
      {
         var docDetails = args[1].documentDetails;

         this.options.parentNodeRef = args[1].metadata.filePlan.replace(':/','');
         this.options.docName = docDetails.displayName;

         if (docDetails.permissions.userAccess.Create && docDetails.type !== "metadata-stub")
         {
            this.widgets.manageRefs.set("disabled", false);
         }
      },
      
      /**
       * Mange References button click handler. Redirects browser to Manage References page
       * 
       * @method onManageReferences
       */
      onManageReferences: function RecordsDocumentReferences_onManageReferences()
      {
         var uriTemplate = Alfresco.constants.URL_PAGECONTEXT + 'site/{site}/rmreferences?nodeRef={nodeRef}&parentNodeRef={parentNodeRef}&docName={docName}',     
            url = YAHOO.lang.substitute(uriTemplate,
            {
               site: encodeURIComponent(this.options.siteId),
               nodeRef: this.options.nodeRef,
               parentNodeRef: this.options.parentNodeRef,
               docName: encodeURIComponent(this.options.docName)
            });

         window.location.href = url;
      },

      doRefresh: function()
      {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);
         var url = 'components/document-details/dod5015/document-references?nodeRef={nodeRef}&container={containerId}';
         url += this.options.siteId ? '&site={siteId}' :  '';
         this.refresh(url);
      }
   });
})();
