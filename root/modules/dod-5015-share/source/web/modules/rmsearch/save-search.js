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
 * SaveSearch module
 *
 * A dialog for saving RM searches.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.SaveSearch
 */
(function()
{
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      KeyListener = YAHOO.util.KeyListener;
   
   /**
    * SaveSearch constructor.
    *
    * @param htmlId {string} A unique id for this component
    * @return {Alfresco.SaveSearch} The new Alfresco.SaveSearch instance
    * @constructor
    */
   Alfresco.module.SaveSearch = function(containerId)
   {
      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of Alfresco.module.SaveSearch already exists.");
      }
      
      Alfresco.module.SaveSearch.superclass.constructor.call(this, "Alfresco.module.SaveSearch", containerId, ["button", "container", "connection", "selector", "json"]);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.module.SaveSearch, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",
         
         /**
          * Search query.
          * 
          * @property query
          * @type string
          */
         query: "",
         
         /**
          * Search parameters in URI encoded name/value pair format.
          * 
          * @property params
          * @type string
          */
         params: "",
         
         /**
          * Search sort in comma separated "property/dir" packed format i.e. "cm:name/asc,cm:title/desc"
          * 
          * @property sort
          * @type string
          */
         sort: ""
      },
      
      _form: null,
      
      /**
       * Shows the SaveSearch dialog to the user.
       *
       * @method show
       */
      show: function SS_show()
      {
         if (this.widgets.panel)
         {
            this._showPanel();
         }
         else
         {
            // Load the gui from the server and let the templateLoaded() method handle the rest.
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/rm-save-search",
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               execScripts: true,
               failureMessage: "Could not load Save Search template."
            });
         }
      },

      /**
       * Called when the SaveSearch html template has been returned from the server.
       * Creates the YUI gui objects such as buttons and a panel and shows it.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object 
       */
      onTemplateLoaded: function SS_onTemplateLoaded(response)
      {
         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;
         
         // The panel is created from the HTML returned in the XHR request, not the container
         var panelDiv = Dom.getFirstChild(containerDiv);
         
         this.widgets.panel = Alfresco.util.createYUIPanel(panelDiv);
         
         // Create the cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button",
            function()
            {
               this.widgets.panel.hide();
            }
         );
         
         // Create the Save button, the forms runtime will handle when its clicked
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "save-button", null,
         {
            type: "submit"
         });
         
         // Configure the forms runtime
         var form = new Alfresco.forms.Form(this.id + "-form");
         
         // Name is a node, mandatory and has a maximum length
         form.addValidation(this.id + "-name", Alfresco.forms.validation.nodeName, null, "keyup");
         form.addValidation(this.id + "-name", Alfresco.forms.validation.mandatory, null, "keyup");
         form.addValidation(this.id + "-name", Alfresco.forms.validation.length,
         {
            max: 1024,
            crop: true
         }, "keyup");
         
         // Description has a maximum length
         form.addValidation(this.id + "-description", Alfresco.forms.validation.length,
         {
            max: 1024,
            crop: true
         }, "keyup");
         
         // The Save button is the submit button, and it should be enabled when the form is ready
         form.setShowSubmitStateDynamically(true, false);
         form.setSubmitElements(this.widgets.saveButton);
         form.doBeforeFormSubmit =
         {
            fn: function()
            {
               var formEl = Dom.get(this.id + "-form");
               formEl.attributes.action.nodeValue = Alfresco.constants.PROXY_URI + "slingshot/rmsavedsearches/site/" + this.options.siteId;
               
               this.widgets.saveButton.set("disabled", true);
               this.widgets.cancelButton.set("disabled", true);
               
               this.widgets.panel.hide();
               
               // apply hidden field values for query via module options
               Dom.get(this.id + "-query").value = this.options.query;
               Dom.get(this.id + "-params").value = this.options.params;
               Dom.get(this.id + "-sort").value = this.options.sort;
            },
            obj: null,
            scope: this
         };
         
         // Submit as an ajax submit (not leave the page), in json format
         form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onSaveSearchSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onSaveSearchFailure,
               scope: this
            }
         });
         form.setSubmitAsJSON(true);
         form.applyTabFix();
         form.init();
         this._form = form;
         
         // Show the panel
         this._showPanel();
      },

      /**
       * Called when user clicks on the cancel button.
       * Closes the SaveSearch panel.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function SS_onCancelButtonClick(type, args)
      {
         this.widgets.panel.hide();
      },

      /**
       * Called when a search has been successfully saved to the server.
       *
       * @method onSaveSearchSuccess
       * @param response
       */
      onSaveSearchSuccess: function SS_onSaveSearchSuccess(response)
      {
         if (response.json !== undefined && response.json.success)
         {
            // Fire bubbling event to inform caller search saved
            var name = Dom.get(this.id + "-name").value;
            var obj = {
               id: name,
               label: name,
               description: Dom.get(this.id + "-description").value,
               query: this.options.query,
               params: this.options.params,
               sort: this.options.sort
            };
            YAHOO.Bubbling.fire("savedSearchAdded", obj);
            this.widgets.panel.hide();
         }
         else
         {
            this._adjustGUIAfterFailure(response);
         }
      },

      /**
       * Called when a search failed to save.
       *
       * @method onSaveSearchFailure
       * @param response
       */
      onSaveSearchFailure: function SS_onSaveSearchFailure(response)
      {
         this._adjustGUIAfterFailure(response);
      },

      /**
       * Helper method that restores the gui and displays an error message.
       *
       * @method _adjustGUIAfterFailure
       * @param response
       */
      _adjustGUIAfterFailure: function SS__adjustGUIAfterFailure(response)
      {
         this.widgets.saveButton.set("disabled", false);
         this.widgets.cancelButton.set("disabled", false);
         this.widgets.panel.show();
         var text = Alfresco.util.message("message.failure", this.name);
         if (response.json && response.json.message)
         {
            var tmp = Alfresco.util.message(response.json.message, this.name);
            text = tmp ? tmp : text;
         }
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: text
         });
      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function SS__showPanel()
      {
         // The panel gui has been showed before and its gui has already been loaded and created
         this.widgets.panel.show();
         
         this._form.updateSubmitElements();
         
         // Firefox insertion caret fix
         Alfresco.util.caretFix(this.id + "-form");
         
         // Register the ESC key to close the dialog
         var escapeListener = new KeyListener(document,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.onCancelButtonClick();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();
         
         // Set the focus on the first field
         Dom.get(this.id + "-name").focus();
      }
   });
})();

Alfresco.module.getSaveSearchInstance = function()
{
   var instanceId = "alfresco-SaveSearch-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.module.SaveSearch(instanceId);
};