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
 * @class Alfresco.RM.NewReference
 */
(function RM_NewReference()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
       $links = Alfresco.util.activateLinks;

   /**
    * RM References component constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyDocuments} The new component instance
    * @constructor
    */
   Alfresco.RM.NewReference = function RM_NewReference_constructor(htmlId)
   {
      Alfresco.RM.NewReference.superclass.constructor.call(this, "Alfresco.RM.NewReference", htmlId, ["button", "menu", "container", "resize", "datatable", "datasource"]);
     
      // this.eventHandlers = {};
      return this;
   };
    
   YAHOO.extend(Alfresco.RM.NewReference, Alfresco.component.Base,
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
          * String Id used for document picker control
          *
          * @property controlId
          * @type String
          */
         controlId: "",
         
         /**
          * String Id used for document picker picker
          *
          * @property pickerId
          * @type String
          */
         pickerId: "",
         
         /**
          * Comma separated value of selected documents (nodeRefs). 
          *
          * @property pickerId
          * @type String
          */
         currentValue: ""
         
      },
      
      /**
       * Initialises event listening and custom events
       * 
       * @method initEvents
       */
      initEvents : function RM_NewReference_initEvents()
      {
         Event.on(this.id, 'click', this.onInteractionEvent, null, this);
         Event.on('new-ref-name', 'keyup', function(e)
         {
            this.checkRequiredFields();
         }, null, this);
         
         this.registerEventHandler('click',
         [
            {
               rule: 'button.cancelCreate',
               o:
               {
                  handler :this._navigateForward,
                  scope: this
               }
            },                       
            {
               rule: 'button.submitCreate',
               o:
               {
                  handler: this.onCreate,
                  scope: this
               }
            }
         ]);
         
         return this;
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * 
       * @method onReady
       */
      onReady: function RM_NewReference_onReady()
      {
         this.initEvents();

         // Create widget button while reassigning classname to src element (since YUI removes classes). 
         // We need the classname so we can identify what action to take when it is interacted with (event delegation).
         var buttons = Sel.query('#submitCreate,#cancelCreate',this.id),
            button, id;

         for (var i = 0, len = buttons.length; i < len; i++)
         {
            button = buttons[i];
            id = button.id;
            this.widgets[id] = new YAHOO.widget.Button(id);
            this.widgets[id]._button.className = button.className;
         }
         
         this.widgets.documentPicker = new Alfresco.module.DocumentPicker(this.id + '-docPicker', Alfresco.module.RM_ObjectRenderer);

         this.widgets.documentPicker.setOptions(
         {
            controlId: this.options.controlId,
            pickerId: this.options.pickerId,
            disabled: false,
            compactMode: false,
            displaySmallAddButtonIcon:true,
            currentValue: this.options.currentValue,
            minSearchTermLength: 3,
            maxSearchResults: 100,
            multipleSelectMode: false,
            parentNodeRef: Alfresco.util.getQueryStringParameter('parentNodeRef'),
            itemFamily: "node",
            showLinkToTarget: false,
            maintainAddedRemovedItems:false,
            mandatory:true,
            docLibNameAlias:this.msg('label.fileplan'),
            restrictParentNavigationToDocLib: true,     
            params:'filterType=rma:dispositionSchedule,rma:dispositionActionDefinition,rma:dispositionAction,rma:hold,rma:transfer,cm:thumbnail'
         });
         YAHOO.Bubbling.on('onDocumentsSelected', this.updateSelectionField, this);
      },
      
      /**
       * Updates UI with details about the selected documents
       * 
       * Also updates the internal value as nodeRefs, so the document picker can
       * reload the selection if user selects the picker again.
       * 
       * @method updateSelectionField
       * @param e {object} Event
       * @param args {object} Event arguments
       */
      updateSelectionField: function RM_NewReference_updateSelectionField(e, args)
      {
         var selectedEl = Dom.get(this.options.pickerId),
            selectedItems = args[1].items;
         if (selectedItems.length > 0)
         {
            // We only need one
            var selectedItem = selectedItems[0],
               docUrl = Alfresco.constants.URL_PAGECONTEXT + 'site/rm/document-details?nodeRef=' + selectedItem.nodeRef,
               docLibPath = selectedItem.displayPath.split('documentLibrary')[1];
            selectedEl.innerHTML = '<a href="'+ docUrl+ '" title="' + $html(selectedItem.description) + '">'+ $html(docLibPath + '/' + selectedItem.name) +'</a>';
            Dom.addClass(selectedEl,'active');
            // Note: if more than one than we must store as comma separated
            this.options.currentValue = selectedItem.nodeRef;
         }
         else
         {
            selectedEl.innerHTML = "";
            Dom.removeClass(selectedEl,'active');
            this.options.currentValue = "";
         }
         this.checkRequiredFields();
      },
      
      /**
       * Updates state of submit button based on required valued
       * 
       * @method checkRequiredFields
       */
      checkRequiredFields: function()
      {
         if (this.options.currentValue != "" && Dom.get('new-ref-name').value != "")
         {
            this.widgets['submitCreate'].set('disabled', false);
         }
         else
         {
            this.widgets['submitCreate'].set('disabled', true);
         }
      },
      
      /**
       * Displays the corresponding details page for the current node
       *
       * @method _navigateForward
       * @private
       */
      _navigateForward: function RM_NewReference__navigateForward()
      {
         this.widgets.documentPicker.resetSelection();
         var uriTemplate = Alfresco.constants.URL_PAGECONTEXT + 'site/{site}/rmreferences?nodeRef={nodeRef}&parentNodeRef={parentNodeRef}&docName={docName}',
            pageUrl = YAHOO.lang.substitute(uriTemplate,
            {
               site: encodeURIComponent(this.options.siteId),
               nodeRef: this.options.nodeRef,
               parentNodeRef: this.options.parentNodeRef,
               docName: encodeURIComponent(this.options.docName)
            });

         window.location.href = pageUrl;
      },
      
      /**
       * Create button event handler
       *
       * @method onCreate
       * @params e {object} Event
       */
      onCreate: function RM_NewReference__onCreate(e)
      {
         var refTypeEl = document.getElementById('record-rel'),
            referenceType = refTypeEl.options[refTypeEl.selectedIndex].value;
         
         Alfresco.util.Ajax.jsonRequest(
         {
            method: Alfresco.util.Ajax.POST,
            url: Alfresco.constants.PROXY_URI + "api/node/" + this.options.nodeRef.replace(':/', '') + '/customreferences',
            dataObj:
            {
               refId: referenceType,
               toNode: this.options.currentValue
            },
            successCallback:
            {
               fn: this._navigateForward,
               scope: this
            },
            failureMessage: this.msg("message.createfail")
         });
         return false;
      }
   });
})();