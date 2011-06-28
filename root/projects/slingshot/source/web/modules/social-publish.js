/**
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
 * socialPublishing component.
 *
 * Enables a user to publish a nodeRef to an external channel or view the publish history on a node.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.socialPublishing
 */
 
(function()
{
   
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
         Event = YAHOO.util.Event, 
         KeyListener = YAHOO.util.KeyListener;
   
   /**
    * socialPublishing constructor.
    *
    * socialPublishing is considered a singleton so constructor should be treated as private,
    * please use Alfresco.module.getsocialPublishingInstance() instead.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.module.socialPublishing} The new socialPublishing instance
    * @constructor
    * @private
    */
   Alfresco.module.socialPublishing = function(containerId)
   {
      this.name = "Alfresco.module.socialPublishing";
      this.id = containerId;

      var instance = Alfresco.util.ComponentManager.get(this.id);
      if (instance !== null)
      {
         throw new Error("An instance of Alfresco.module.socialPublishing already exists.");
      }

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container"], this.onComponentsLoaded, this);

      return this;
      
   };

   Alfresco.module.socialPublishing.prototype =
   {

      /**
       * The default config for the gui state for the publish dialog.
       * The user can override these properties in the show() method.
       *
       * @property defaultShowConfig
       * @type object
       */
      defaultShowConfig:
      {
         nodeRef: null,
         filename: null
      },

      /**
       * The merged result of the defaultShowConfig and the config passed in
       * to the show method.
       *
       * @property showConfig
       * @type object
       */
      showConfig: {},
      
      /**
       * Object container for storing YUI widget and HTMLElement instances.
       *
       * @property widgets
       * @type object
       */
      widgets: {},
      
      /**
       * Property containing the maximum length a status update may be.
       * This is dictated by the status update channel & where multiple channels are selected, this property will contain the lowest value.
       * 
       * @property maxStatusUpdateLength
       * @type interger
       * 
       */
      maxStatusUpdateLength: null,
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function SP_onComponentsLoaded()
      {
         // Shortcut for dummy instance
         if (this.id === null)
         {
            return;
         }
      },

      /**
       * Show can be called multiple times and will display the dialog
       * in different ways depending on the config parameter.
       *
       * @method show
       * @param config {object} describes how the dialog should be displayed
       * The config object is in the form of:
       * {
       *    nodeRef: {string},  // the nodeRef
       *    version: {string}   // the version to show properties of
       * }
       */
      show: function SP_show(config)
      {
         // Merge the supplied config with default config and check mandatory properties
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);
         if (this.showConfig.nodeRef === undefined ||
             this.showConfig.filename === undefined)
         {
             throw new Error("A nodeRef & filename must be provided");
         }
         
         // Check if the dialog has been showed before & the nodeRef is the same
         if (this.widgets.panel && this.widgets.panel.nodeRef === this.showConfig.nodeRef)
         {
            // Display it.
            this._showPanel();
         }
         else
         {
            
            // If this.widgets.panel exists, but is for a different nodeRef, start again.
            if (this.widgets.panel) 
            {
               this.widgets.panel.destroy;
            }
            
            // If it hasn't load the gui (template) from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/social-publishing?nodeRef=" + this.showConfig.nodeRef + "&siteId=" + Alfresco.constants.SITE + "&htmlid=" + this.id,
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: Alfresco.util.message("socialPublish.template.error", this.name),
               execScripts: true
            });
            
            // Register the ESC key to close the dialog
            this.widgets.escapeListener = new KeyListener(document,
            {
               keys: KeyListener.KEY.ESCAPE
            },
            {
               fn: this.onCancelButtonClick,
               scope: this,
               correctScope: true
            }); 
            
         }
                  
      },

      /**
       * Called when the dialog html template has been returned from the server.
       * Creates the YIU gui objects such as the panel.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function SP_onTemplateLoaded(response)
      {

         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;
               
         var dialogDiv = YAHOO.util.Dom.getFirstChild(containerDiv);

         // Create the panel from the HTML returned in the server reponse         
         this.widgets.panel = Alfresco.util.createYUIPanel(dialogDiv);

         // associate the panel with a nodeRef so we know when to refresh or redisplay it:
         this.widgets.panel.nodeRef = this.showConfig.nodeRef;
         
         // Save a reference to the HTMLElement displaying texts so we can alter the text later
         this.widgets.headerText = Dom.get(this.id + "-header-span");
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);
         this.widgets.publishButton = Alfresco.util.createYUIButton(this, "publish-button", this.onPublishButtonClick);
         this.widgets.formContainer = Dom.get(this.id + "-publish-form");
         
         // add key listener to update the character count
         this.widgets.statusUpdateText = Dom.get(this.id + "-statusUpdate-text");
         this.widgets.statusUpdateCount = Dom.get(this.id + "-statusUpdate-count");
         Event.addListener(this.widgets.statusUpdateText, "keydown", this.onStatusUpdateKeypress, {}, this);
         Event.addListener(this.widgets.statusUpdateText, "keyup", this.onStatusUpdateKeypress, {}, this);
         
         // add listener for the status update check box.
         this.widgets.statusUpdateCheckboxes = Dom.getElementsByClassName("statusUpdate-checkboxes", "input", this.widgets.formContainer);
         Event.addListener(this.widgets.statusUpdateCheckboxes, "click", this.onStatusCheckboxToggle, {}, this);
         
         // add listener for URL check box
         this.widgets.statusUpdateURL = Dom.get(this.id + "-statusUpdate-checkbox-url");
         Event.addListener(this.widgets.statusUpdateURL, "click", this.onStatusURLToggle, {}, this);
         
         // Show panel
         this._showPanel();
         
      },
      
      /**
       * Fired when the user clicks the cancel button.
       * Closes the panel.
       *
       * @method onCancelButtonClick
       * @param event {object} a Button "click" event
       */
      onCancelButtonClick: function SP_onCancelButtonClick()
      {
         // Hide the panel
         this.widgets.panel.hide();
         
         // Disable the Esc key listener
         this.widgets.escapeListener.disable();
      },
      
      /**
       * Fired when the user clicks the "Publish" button on the dialogue
       * 
       * @method onPublishButtonClick
       */
      onPublishButtonClick: function SP_onPublishButtonClick()
      {
         // Confirmation dialogue
         var publishData = 
            {
               "channelName": Dom.get(this.id + "-channel-select").value,
               "publishNodes": [this.showConfig.nodeRef]
            },
            me = this,
            statusUpdateChannels = []
            includeStatus = false;
         
         for (var i = 0; i < this.widgets.statusUpdateCheckboxes.length; i++) {
            if (this.widgets.statusUpdateCheckboxes[i].checked) {
               statusUpdateChannels.push(this.widgets.statusUpdateCheckboxes[i].value);
               includeStatus = true;
            }
         }
         
         if (includeStatus) {
            publishData.statusUpdate = 
            {
               "message": this.widgets.statusUpdateText.value,
               "nodeRef": this.showConfig.nodeRef,
               "channelNames": statusUpdateChannels
            }
         };
         
         var success = function SP_onPublishSuccess(response)
         {
            var fileName = me.showConfig.filename, 
               channelName = response.config.dataObj.channelName,
               docDetailsURL = Alfresco.constants.URL_PAGECONTEXT + "site/" + Alfresco.constants.SITE + "/document-details?nodeRef=" + me.showConfig.nodeRef;
               linkText = Alfresco.util.message("socialPublish.confirm.link", me.name, 
                     {
                        "0": fileName
                     }),
               linkHTML = "<a href='" + docDetailsURL + "'>" + linkText + "</a>",
               channelHTML = "<span class='channel'><img src='http://www.slideshare.net/favicon.ico' />" + channelName + "</span>",
               trackingText = Alfresco.util.message("socialPublish.confirm.track", me.name,
                  {
                     "0": linkHTML
                  }),
               successText = Alfresco.util.message("socialPublish.confirm.success", me.name,
                  {
                     "0": fileName,
                     "1": channelHTML
                  }),
               balloonHTML = "<div class='publishConfirm'><div class='success'>" + successText + "</div><div class='tracking'>" + trackingText + "</div></div>",
               balloon = Alfresco.util.createBalloon("template_x002e_documentlist_x002e_documentlibrary-doclistBar",
               {
                  html: balloonHTML,
                  width: "48em"
               });
            
            balloon.show();
         },
         failure = function SP_onPublishFailure(response)
         {
            console.log("Error: onPublishFailure");
            alert("This didn't actually work, because the API isn't there yet, but we can pretend.");
            success(response);
         }; 
         
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/publishing/" + Alfresco.constants.SITE + "/queue",
            method: "POST",
            requestContentType: "application/json",
            responseContentType: "application/json",
            dataObj: publishData,
            successCallback:
            {
               fn: success,
               scope: this
            },
            failureCallback:
            {
               fn: failure,
               scope: this
            }
         });
         
         // Hide the panel
         this.widgets.panel.hide();
         
         // Disable the Esc key listener
         this.widgets.escapeListener.disable();
      },

      
      /**
       * Fired when the user clicks the "Unpublish" button on the dialogue
       * 
       * @method onUnpublishButtonClick
       */
      onUnpublishButtonClick: function SP_onUnpublishButtonClick()
      {
         // Confirmation dialogue
         
         // Submit the form to the unpublish API.
      },
      
      /**
       * 
       * Triggered on Keyup event in the status update box
       * 
       * @method onStatusUpdateKeypress
       */
      onStatusUpdateKeypress: function SP_onStatusUpdateKeypress()
      {
         this.checkUpdateLength();
      },
      
      /**
       * 
       * Triggered when a Status Update check box is selected or unselected.
       * 
       * @method onStatusCheckboxToggle
       */
      onStatusCheckboxToggle: function SP_onStatusCheckboxToggle()
      {
         
         // should the publish button be disabled?
         console.log("onStatusCheckboxToggle");
         
      },
      
      /**
       * Triggered when the user clicks on the checkbox to include the URL or not.
       * 
       * @method onStatusURLToggle
       */
      onStatusURLToggle: function SP_onStatusURLToggle() 
      {
         // Read the URL length
         var urlLength = parseInt(this.widgets.statusUpdateURL.rel, 10),
            maxLength = this.maxStatusUpdateLength;
         
         // Check maxLength has been set
         if (maxLength) {
         
            // Is the URL now included or excluded?
            if (this.widgets.statusUpdateURL.checked) 
            {
               //it's just been added, so deduct it's length from the maxStatusUpdateLength
               maxLength = maxLength - urlLength;
               
               // Ensure max length isn't negative!
               if (maxLength < 0) {
                  maxLength = 0;
               }
               
            } 
            else 
            {
               // The URL has just been removed, increase the allowed length of the message:
               maxLength = maxLength + urlLength;
            }
         
         }
         
         // rerun the checkUpdateLength script.
         this.checkUpdateLength();
         
         
         console.log(this.maxStatusUpdateLength);
      },
      
      /**
       * Checks the update length against the defined maximum (if there is one) and trims the contents to that length.
       * 
       * @method checkUpdateLength
       */
      checkUpdateLength: function checkUpdateLength() 
      {
         var statusUpdate = this.widgets.statusUpdateText.value,
            maxLength = this.maxStatusUpdateLength;
      
         // if a maximum length for the status update has been set, truncate the text to that length.
         if (this.maxStatusUpdateLength) 
         {
            statusUpdate = statusUpdate.substring(0, this.maxStatusUpdateLength);
         }
         
         // update UI message.
         this.widgets.statusUpdateCount.innerHTML = Alfresco.util.message("socialPublish.dialogue.statusUpdate.used", this.name,
         {
               "0": "<strong>" + statusUpdate.length + "</strong>"
         });
         
      },
      
      /**
       * Gets All Publising Channels for current site
       * 
       */
      getAllChannels: function SP_getAllChannels()
      {
         // AJAX call to channels Remote API
      },
      
      /**
       * Triggered when getAllChannels returns successfully 
       */
      onChannelsLoaded: function SP_onChannelsLoaded(response)
      {
         //parse the response to retrieve an array of mime-types.
         
         //parse the response to reteieve an array of content-types.
      },
      
      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       */
      _applyConfig: function SP__applyConfig()
      {

         // Set the panel section
         var header = Alfresco.util.message("socialPublish.dialogue.header", this.name,
         {
            "0": "<strong>" + this.showConfig.filename + "</strong>"
         });
         this.widgets.headerText["innerHTML"] = header;

         this.widgets.cancelButton.set("disabled", false);
      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function SP__showPanel()
      {

         // Apply the config before it is showed
         this._applyConfig();

         // Enable the Esc key listener
         this.widgets.escapeListener.enable();
         
         // Show the panel
         this.widgets.panel.show();
      }
   };
})();

Alfresco.module.getSocialPublishingInstance = function()
{
   var instanceId = "alfresco-socialPublishing-instance";
   return Alfresco.util.ComponentManager.get(instanceId) || new Alfresco.module.socialPublishing(instanceId);
}