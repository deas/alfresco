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
 * ConsoleApplication tool component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ConsoleApplication
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Element = YAHOO.util.Element;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * ConsoleApplication constructor.
    * 
    * @param {String} htmlId The HTML id üof the parent element
    * @return {Alfresco.ConsoleApplication} The new ConsoleApplication instance
    * @constructor
    */
   Alfresco.ConsoleApplication = function(htmlId)
   {
      this.name = "Alfresco.ConsoleApplication";
      Alfresco.ConsoleApplication.superclass.constructor.call(this, htmlId);
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "json", "history"], this.onComponentsLoaded, this);
      
      /* Define panel handlers */
      var parent = this;
      
      // NOTE: the panel registered first is considered the "default" view and is displayed first
      
      /* Options Panel Handler */
      OptionsPanelHandler = function OptionsPanelHandler_constructor()
      {
         OptionsPanelHandler.superclass.constructor.call(this, "options");
      };
      
      YAHOO.extend(OptionsPanelHandler, Alfresco.ConsolePanelHandler,
      {
         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onLoad
          */
         onLoad: function onLoad()
         {
            // Buttons
            parent.widgets.applyButton = Alfresco.util.createYUIButton(parent, "apply-button", null,
            {
               type: "submit"
            });
            
            // Form definition
            var form = new Alfresco.forms.Form(parent.id + "-options-form");
            form.setSubmitElements([parent.widgets.applyButton]);
            form.setSubmitAsJSON(true);
            form.setAJAXSubmit(true,
            {
               successCallback:
               {
                  fn: this.onSuccess,
                  scope: this
               }
            });
            form.init();
         },
         
         /**
          * Successfully applied options event handler
          *
          * @method onSuccess
          * @param response {object} Server response object
          */
         onSuccess: function OptionsPanel_onSuccess(response)
         {
            if (response && response.json)
            {
               if (response.json.success)
               {
                  // refresh the browser to force the themed components to reload
                  window.location.reload(true);
               }
               else if (response.json.message)
               {
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: response.json.message
                  });
               }
            }
            else
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: Alfresco.util.message("message.failure")
               });
            }
         }
      });
      new OptionsPanelHandler();
      
      return this;
   };
   
   YAHOO.extend(Alfresco.ConsoleApplication, Alfresco.ConsoleTool,
   {
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function ConsoleApplication_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ConsoleApplication_onReady()
      {
         // Call super-class onReady() method
         Alfresco.ConsoleApplication.superclass.onReady.call(this);
      },
      
      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function ConsoleApplication__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.ConsoleApplication", Array.prototype.slice.call(arguments).slice(1));
      }
   });
})();