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
 * DocumentMetadata
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentMetadata
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      formatDate = Alfresco.util.formatDate,
      fromISO8601 = Alfresco.util.fromISO8601;

   /**
    * DocumentMetadata constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentMetadata} The new DocumentMetadata instance
    * @constructor
    */
   Alfresco.DocumentMetadata = function DocumentMetadata_constructor(htmlId)
   {
      Alfresco.DocumentMetadata.superclass.constructor.call(this, "Alfresco.DocumentMetadata", htmlId);

      // Decoupled event listeners
      YAHOO.Bubbling.on("metadataRefresh", this.doRefresh, this);

      return this;
   };

   YAHOO.extend(Alfresco.DocumentMetadata, Alfresco.component.Base,
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
          * The nodeRefs to load the form for.
          *
          * @property nodeRef
          * @type string
          * @required
          */
         nodeRef: null,

         /**
          * The current site (if any)
          *
          * @property site
          * @type string
          */
         site: null,

         /**
          * The form id for the form to use.
          *
          * @property destination
          * @type string
          */
         formId: null
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DocumentMetadata_onReady()
      {
         // Load the form
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
            dataObj:
            {
               htmlid: this.id + "-formContainer",
               itemKind: "node",
               itemId: this.options.nodeRef,
               formId: this.options.formId,
               mode: "view"
            },
            successCallback:
            {
               fn: this.onFormLoaded,
               scope: this
            },
            failureMessage: this.msg("message.failure"),
            scope: this,
            execScripts: true
         });
      },

      /**
       * Called when a workflow form has been loaded.
       * Will insert the form in the Dom.
       *
       * @method onFormLoaded
       * @param response {Object}
       */
      onFormLoaded: function DocumentMetadata_onFormLoaded(response)
      {
         var formEl = Dom.get(this.id + "-formContainer"),
            me = this;
         formEl.innerHTML = response.serverResponse.responseText;
         Dom.getElementsByClassName("viewmode-value-date", "span", formEl, function()
         {
            var showTime = Dom.getAttribute(this, "data-show-time"),
                fieldValue = Dom.getAttribute(this, "data-date-iso8601"),
                dateFormat = (showTime=='false') ? me.msg("date-format.defaultDateOnly") : me.msg("date-format.default"),
                // MNT-9693 - Pass the ignoreTime flag
                ignoreTime = showTime == 'false',
                theDate = fromISO8601(fieldValue, ignoreTime);
            
            this.innerHTML = formatDate(theDate, dateFormat);
         });
      },

      /**
       * Refresh component in response to metadataRefresh event
       *
       * @method doRefresh
       */
      doRefresh: function DocumentMetadata_doRefresh()
      {
         YAHOO.Bubbling.unsubscribe("metadataRefresh", this.doRefresh, this);
         this.refresh('components/document-details/document-metadata?nodeRef={nodeRef}' + (this.options.siteId ? '&site={siteId}' :  '') + (this.options.formId ? '&formId={formId}' :  ''));
      }
   });

})();
