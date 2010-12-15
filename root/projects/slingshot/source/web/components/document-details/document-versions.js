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
 * Document Details Version component.
 *
 * @namespace Alfresco
 * @class Alfresco.DocumentVersions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Dashboard DocumentVersions constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentVersions} The new component instance
    * @constructor
    */
   Alfresco.DocumentVersions = function DV_constructor(htmlId)
   {
      Alfresco.DocumentVersions.superclass.constructor.call(this, "Alfresco.DocumentVersions", htmlId, ["button", "container"]);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("documentDetailsAvailable", this.onDocumentDetailsAvailable, this);
      YAHOO.Bubbling.on("metadataRefresh", this.onMetadataRefresh, this);

      return this;
   }

   YAHOO.extend(Alfresco.DocumentVersions, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type {object} object literal
       */
      options:
      {
         /**
          * An array with labels in the same order as they are listed in the html template
          *
          * @property versions
          * @type Array an array of object literals of the following form:
          * {
          *    label: {string}, // the version ot revert the node to
          *    createDate: {string}, // the date the version was creted in freemarker?datetime format
          * }
          */
         versions: [],

         /**
          * The version ot revert the node to
          *
          * @property nodeRef
          * @type {string}
          */
         nodeRef: null,

         /**
          * The version ot revert the node to
          *
          * @property filename
          * @type {string}
          */
         filename: null         
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function DV_onReady()
      {
         // Listen on clicks for revert version icons
         var versions = this.options.versions, version, i, j, reverter;
         
         for (i = 0, j = versions.length; i < j; i++)
         {
            reverter = Dom.get(this.id + "-revert-a-" + i);
            if (reverter)
            {
               Event.addListener(reverter, "click", function (event, obj)
               {
                  // Stop browser from using href attribute
                  Event.preventDefault(event);

                  // Find the index of the version link by looking at its id
                  version = versions[obj.versionIndex];

                  // Find the version through the index and display the revert dialog for the version
                  Alfresco.module.getRevertVersionInstance().show(
                  {
                     filename: this.options.filename,
                     nodeRef: this.options.nodeRef,
                     version: version.label,
                     onRevertVersionComplete:
                     {
                        fn: this.onRevertVersionComplete,
                        scope: this
                     }
                  });
               },
               {
                  versionIndex: i
               }, this);
            }

            // Listen on clicks on the version - date row so we can expand and collapse it
            var expander = Dom.get(this.id + "-expand-a-" + i),
               moreVersionInfoDiv = Dom.get(this.id + "-moreVersionInfo-div-" + i);            

            if (expander)
            {               
               Event.addListener(expander, "click", function (event, obj)
               {
                  // Stop browser from using href attribute
                  Event.preventDefault(event)

                  if (obj.moreVersionInfoDiv && Dom.hasClass(obj.expandDiv, "collapsed"))
                  {
                     Alfresco.util.Anim.fadeIn(obj.moreVersionInfoDiv);
                     Dom.removeClass(obj.expandDiv, "collapsed");
                     Dom.addClass(obj.expandDiv, "expanded");
                  }
                  else
                  {
                     Dom.setStyle(obj.moreVersionInfoDiv, "display", "none");
                     Dom.removeClass(obj.expandDiv, "expanded");
                     Dom.addClass(obj.expandDiv, "collapsed");
                  }
               },
               {
                  expandDiv: expander,
                  moreVersionInfoDiv: moreVersionInfoDiv
               }, this);
            }

            // Format and display the createdDate
            Dom.get(this.id + "-createdDate-span-" + i).innerHTML = Alfresco.util.formatDate(versions[i].createdDate);
         }
      },

      /**
       * Fired by the Revert Version component after a successfull revert.
       *
       * @method onRevertVersionComplete
       */
      onRevertVersionComplete: function DV_onRevertVersionComplete()
      {
         Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.revertComplete", this.name)
         });

         window.location.reload();
      },

      /**
       * Event handler called when the "documentDetailsAvailable" event is received
       *
       * @method: onDocumentDetailsAvailable
       */
      onDocumentDetailsAvailable: function DocumentInfo_onDocumentDetailsAvailable(layer, args)
      {
         var workingCopyMode = args[1].workingCopyMode || false;

         if (!workingCopyMode)
         {
            Dom.removeClass(this.id + "-body", "hidden");
            // Check if user has revert permissions
            var roles = args[1].documentDetails.permissions.roles;
            for (var i = 0, il = roles.length; i < il; i++)
            {
               if (Alfresco.util.arrayContains(["SiteManager", "SiteCollaborator"], roles[i].split(";")[2])
                     && roles[i].split(";")[0] == "ALLOWED")
               {
                  var revertEls = Selector.query("a.revert", this.id + "-body");
                  for (i = 0, il = revertEls.length; i < il; i++)
                  {
                     Dom.removeClass(revertEls[i].parentNode, "hidden");
                  }
                  break;
               }
            }
         }
      },
      
      /**
       * Event handler called when the "metadataRefresh" event is received
       *
       * @method: onMetadataRefresh
       */
      onMetadataRefresh: function DV_onMetadataRefresh(layer, args)
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/document-details/document-versions",
            dataObj:
            {
               htmlid: this.id,
               nodeRef: this.options.nodeRef
            },
            successCallback:
            {
               fn: this.onTemplateLoaded,
               scope: this
            },
            execScripts: true
         });
      },
      
      /**
       * Event callback when this component has been reloaded via AJAX call
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function AmSD_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = Dom.get(this.id + "-body").parentNode;
         containerDiv.innerHTML = response.serverResponse.responseText;
      }
   });
})();
