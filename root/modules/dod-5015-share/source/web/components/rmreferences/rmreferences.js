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
 * Alfresco top-level RM namespace.
 * 
 * @namespace Alfresco
 * @class Alfresco.RM
 */
Alfresco.RM = Alfresco.RM || {};

/**
 * RM References component
 * 
 * @namespace Alfresco
 * @class Alfresco.RM.References
 */
(function RM_References()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Sel = YAHOO.util.Selector;

   /**
    * RM References component constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RM.References} The new component instance
    * @constructor
    */
   Alfresco.RM.References = function RM_References_constructor(htmlId)
   {
      return Alfresco.RM.References.superclass.constructor.call(this, "Alfresco.RM.References", htmlId);
   };
   
   /**
    * Extend Alfresco.component.Base with class implementation
    */
   YAHOO.extend(Alfresco.RM.References, Alfresco.component.Base,
   {
      /**
       * Initialises event listening and custom events
       * 
       * @method initEvents
       */
      initEvents: function RM_References_initEvents()
      {
         Event.on(this.id, 'click', this.onInteractionEvent, null, this);

         this.registerEventHandler('click',
         [
            {
               rule: 'button.deleteRef',
               o:
               {
                  handler: this.onDeleteReference,
                  scope: this
               }
            },
            {
               rule: 'button.doneRef',
               o:
               {
                  handler: this.onDoneReference,
                  scope: this
               }
            },                       
            {
               rule: 'button.newRef',
               o:
               {
                   handler:this.onNewReference,
                   scope:this
                }
            }
         ]);
         return this;
      },
      
      /**
       * Handler for Done button
       * 
       * @method onDoneReference
       * @param e {object} Event
       * @param args {object} Event arguments
       */
      onDoneReference: function RM_References_onDoneReference(e, args)
      {
         var uriTemplate = 'document-details?nodeRef={nodeRef}',
            pageUrl = YAHOO.lang.substitute(uriTemplate,
            {
               site: encodeURIComponent(this.options.siteId),
               nodeRef: this.options.nodeRef
            });

         window.location.href = pageUrl;
      },
      
      /**
       * Handler for delete button
       * 
       * @method onDeleteReference
       * @param e {object} Event
       * @param args {object} Event arguments
       */
      onDeleteReference: function RM_References_onDeleteReference(e, args)
      {
         var eTarget = Event.getTarget(e),
            refId = this.widgets[eTarget.id.replace('-button', '')].get('value'),
            nodeRefEl = Dom.getAncestorByTagName(eTarget, 'li'),
            href = nodeRefEl.getElementsByTagName('a')[0].href,
            nodeRef = new Alfresco.util.NodeRef(Alfresco.util.getQueryStringParameter("nodeRef", href)),
            queryString = "?st=" + nodeRef.storeType + "&si=" + nodeRef.storeId + "&id=" + nodeRef.id,
            type = (nodeRefEl.className.indexOf('fromRef')!=-1) ? 'from' : 'to';

         Alfresco.util.Ajax.jsonRequest(
         {
            method: Alfresco.util.Ajax.DELETE,
            url: Alfresco.constants.PROXY_URI + "api/node/" + this.options.nodeRef.replace(':/', '') + '/customreferences' + '/' + refId + queryString,
            successCallback:
            {
               fn: function(e)
               {
                  this.onDeleteSuccess(nodeRefEl, type);
               },
               scope: this
            },
            successMessage: Alfresco.util.message("message.delete.success", 'Alfresco.RM.References'),
            failureMessage: Alfresco.util.message("message.delete.fail", 'Alfresco.RM.References')
         });
      },
      
      /**
       * Handler for new reference  button
       *
       * @method onNewReference
       * @param e {object} Event
       * @param args {object} Event arguments
       */
      onNewReference: function RM_References_onNewReference(e, args)
      {
         var uriTemplate = 'new-rmreference?nodeRef={nodeRef}&parentNodeRef={parentNodeRef}&docName={docName}',
            url = YAHOO.lang.substitute(uriTemplate,
            {
               site: encodeURIComponent(this.options.siteId),
               nodeRef: this.options.nodeRef,
               parentNodeRef: this.options.parentNodeRef,
               docName: encodeURIComponent(this.options.docName)
            });

         window.location.href = url; 
      },
      
      /**
       * Handler for deletion success 
       * 
       * @method onDeleteSuccess
       * @param nodeRefEl {string} ID portion of nodeRef that was successfully deleted
       * @param type {string} Type of reference (from or to)
       */
       onDeleteSuccess: function RM_References_onDeleteSuccess(nodeRefEl, type)
       {
          var ul = nodeRefEl.parentNode;

          // Remove list item
          ul.removeChild(nodeRefEl);

          // If no more references, remove list and display message
          if (ul.getElementsByTagName('li').length === 0)
          {
             ul.parentNode.removeChild(ul);
             Dom.addClass(type+"-no-refs", 'active');
          }
       },
       
      /**
       * Fired by YUI when parent element is available for scripting
       * 
       * @method onReady
       * @override
       */
      onReady: function RM_References_onReady()
      {
         this.initEvents();
         
         // Create widget button while reassigning classname to src element (since YUI removes classes). 
         // We need the classname so we can identify what action to take when it is interacted with (event delegation).
         var buttons = Sel.query('button', this.id),
            button, id;
         for (var i = 0, len = buttons.length; i < len; i++)
         {
            button = buttons[i];
            id = button.id;
            this.widgets[id] = new YAHOO.widget.Button(id);
            this.widgets[id]._button.className = button.className;
         }
      }
   });
})();