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
         Selector = YAHOO.util.Selector,
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
       * Property containing an object of the status update channels and their limit.
       * used by this.checkUpdateLength()
       * 
       * @property updateLimits
       * @type object
       * 
       */
      updateLimits: {},
      
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
       * Sets the update limits for later use in checkUpdateLength()
       */
      setUpdateLimits: function SP_setUpdateLimits(obj) 
      {
         this.updateLimits = obj;
         
         // allow chaining
         return this
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
         // Check the widget has been created. If there are no channels, it won't be:
         if (Dom.get(this.id) === null)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: Alfresco.util.message("socialPublish.noChannels")
            });
            return;
         }
         
         
         // Merge the supplied config with default config and check mandatory properties
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);
         if (this.showConfig.nodeRef === undefined ||
             this.showConfig.filename === undefined)
         {
             throw new Error("A nodeRef & filename must be provided");
         }
         
         // If this.widgets.panel exists, but is for a different nodeRef, start again.
         if (this.widgets.panel) 
         {
            this.widgets.panel.destroy;
         }
         
         // If it hasn't load the gui (template) from the server
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "modules/social-publishing?nodeRef=" + this.showConfig.nodeRef + "&htmlid=" + this.id,
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
         
         this.widgets.selectChannelButton = new YAHOO.widget.Button(this.id + "-channel-select-button",
         {
            type: "menu",
            menu: this.id + "-publishChannel-menu",
            lazyloadmenu: false
         });
         
         this.widgets.selectStatusUpdateChannels = new YAHOO.widget.Button(this.id + "-statusSelect-button",
         {
            type: "menu",
            menu: this.id + "-statusSelect-menu",
            lazyloadmenu: false
         });
         
         
         // Save a reference to HTMLElements
         this.widgets.headerText = Dom.get(this.id + "-header-span");
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);
         this.widgets.publishButton = Alfresco.util.createYUIButton(this, "publish-button", this.onPublishButtonClick);
         this.widgets.formContainer = Dom.get(this.id + "-publish-form");
         this.widgets.statusUpdateText = Dom.get(this.id + "-statusUpdate-text");
         this.widgets.statusUpdateCount = Dom.get(this.id + "-statusUpdate-count");
         this.widgets.statusUpdateChannelCount = Dom.get(this.id + "-statusUpdate-channel-count");
         this.widgets.statusUpdateCountURL = Dom.get(this.id + "-statusUpdate-count-urlMessage");
         this.widgets.statusUpdateCheckboxes = Dom.getElementsByClassName("statusUpdate-checkboxes", "input", this.widgets.formContainer);
         this.widgets.statusUpdateUrlCheckbox = Dom.get(this.id + "-statusUpdate-checkbox-url");
         
         // add key listener to update the character count
         Event.addListener(this.widgets.statusUpdateText, "keydown", this.onStatusUpdateKeypress, {}, this);
         Event.addListener(this.widgets.statusUpdateText, "keyup", this.onStatusUpdateKeypress, {}, this);
         
         // add listener for the status update check box.
         Event.addListener(this.widgets.statusUpdateCheckboxes, "click", this.onStatusCheckboxToggle, {}, this);
         
         // add listener for selection of publish channel:
         Event.addListener(Dom.get(this.id + "-publishChannel-menu"), "click", this.onSelectPublishChannel, {}, this);
         
         // add listener for URL check box
         Event.addListener(this.widgets.statusUpdateUrlCheckbox, "click", this.onStatusURLToggle, {}, this);
         
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
         // create the JSON object for the POST request.
         var selectedChannelId = this.widgets.selectChannelButton.get("value"),
            publishData = 
            {
               "channelId": selectedChannelId,
               "publishNodes": [this.showConfig.nodeRef],
               "statusUpdate": {}
            },
            me = this,
            statusUpdateChannels = []
            includeStatus = false;
         
         // Are any of the status update channels selected?
         for (var i = 0; i < this.widgets.statusUpdateCheckboxes.length; i++) {
            if (this.widgets.statusUpdateCheckboxes[i].checked) {
               statusUpdateChannels.push(this.widgets.statusUpdateCheckboxes[i].value);
               includeStatus = true;
            }
         }
         
         // Build up the JSON object for the statusUpdate
         if (includeStatus) {
            publishData.statusUpdate = 
            {
               "message": this.widgets.statusUpdateText.value,
               "channelIds": statusUpdateChannels
            }
            // If the URL is to be included, add the nodeRef to the statusUpdate obj
            if (this.widgets.statusUpdateUrlCheckbox.checked) {
               publishData.statusUpdate.nodeRef = this.showConfig.nodeRef
            }
         };
         
         // Define success and failure functions inline so they have closeure access to local vars.
         var success = function SP_onPublishSuccess(response)
         {
            // Build up necessary data and HTML.
            // It looks more complicated than it actually is.
            var fileName = me.showConfig.filename, 
               channelName = this.widgets.selectChannelButton.innerHTML,
               docDetailsURL = Alfresco.constants.URL_PAGECONTEXT + "/document-details?nodeRef=" + me.showConfig.nodeRef;
               linkText = Alfresco.util.message("socialPublish.confirm.link", me.name, 
                     {
                        "0": fileName
                     }),
               linkHTML = "<a href='" + docDetailsURL + "'>" + linkText + "</a>",
               channelHTML = "<span class='channel'>" + channelName + "</span>",
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
               doclib = Alfresco.util.ComponentManager.findFirst("Alfresco.DocumentList")
               balloonElement = (doclib) ? Dom.get(doclib.id) : Dom.getElementsByClassName("document-thumbnail")[0];
               balloon = Alfresco.util.createBalloon(balloonElement,
               {
                  html: balloonHTML,
                  width: "48em"
               });
            
            balloon.show();
            // hide the balloon after 10 seconds.
            YAHOO.lang.later(10000, this, function()
            {
               balloon.hide();
            })
         }
         
         // Make the POST request to the publishing queue
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/publishing/queue",
            method: "POST",
            requestContentType: "application/json",
            responseContentType: "application/json",
            dataObj: publishData,
            successCallback:
            {
               fn: success,
               scope: this
            },
            failureMessage: Alfresco.util.message("socialPublish.confirm.failure", this.name)
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
         // TODO...
         
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
      onStatusCheckboxToggle: function SP_onStatusCheckboxToggle(event)
      {
         // apply the selected class
         Alfresco.util.toggleClass(Dom.getAncestorByClassName(Dom.get(event.target), "status-channel"), "selected");
         this.checkUpdateLength();
         
         var selectedChannelsCount = Selector.filter(this.widgets.statusUpdateCheckboxes, ":checked").length 
         this.widgets.statusUpdateChannelCount.innerHTML = Alfresco.util.message("socialPublish.dialogue.statusUpdate.select.count", this.name,
         {
            "0": selectedChannelsCount,
            "1": this.widgets.statusUpdateCheckboxes.length 
         });
         
         if (selectedChannelsCount > 0) {
            // enable status update boxes:
            this.widgets.statusUpdateText.removeAttribute("disabled")
            this.widgets.statusUpdateUrlCheckbox.removeAttribute("disabled")
         } 
         else 
         {
            // disable status update boxes:
            Dom.setAttribute(this.widgets.statusUpdateText, "disabled", "disabled");
            Dom.setAttribute(this.widgets.statusUpdateUrlCheckbox, "disabled", "disabled");
         }
      },
      
      /**
       * Triggered when the user clicks on the checkbox to include the URL or not.
       * 
       * @method onStatusURLToggle
       */
      onStatusURLToggle: function SP_onStatusURLToggle() 
      {
         this.checkUpdateLength();
      },
      
      /**
       * Triggered when the user selects a channel from the drop down list of available publishing channels
       * 
       * @method onSelectPublishChannel
       */
      onSelectPublishChannel: function SP_onSelectPublishChannel(event, obj)
      {
         var el;
         if (Selector.test(event.target, "a.publishChannel")) {
            el = event.target
         } 
         else 
         {
            el = Dom.getAncestorByClassName(event.target, "publishChannel")
         }
         this.widgets.selectChannelButton.set("label", el.innerHTML);
         this.widgets.selectChannelButton.set("value", el.rel);
         event.preventDefault();
      },
      
      /**
       * Checks the update length against the defined maximum (if there is one) and trims the contents to that length.
       * 
       * @method checkUpdateLength
       */
      checkUpdateLength: function SP_checkUpdateLength() 
      {
         var limit = null,
            count = 0,
            charsRemaining = 0,
            checkedUpdateChannels = Selector.filter(this.widgets.statusUpdateCheckboxes, ":checked");
         // Check to see if there are any status update channels selected
         if (checkedUpdateChannels.length > 0) 
         {
            // get the minimum length of the selected channels. This is the number to count down from.
            for (var i = 0; i < checkedUpdateChannels.length; i++) 
            {
               var channelLimit = parseInt(Dom.getAttribute(checkedUpdateChannels[i], "rel"), 10);
               // Check channel limit is greater than zero (zero indicates unlimited) and the count has either not been 
               // set previously or is set to a higher value (since we're looking for the lowest limit)
               if (channelLimit > 0 && (channelLimit < limit || limit === null))
               {
                  limit = channelLimit
               }
            }
            
            // check that at least one of the selected channels set a char limit.
            if (limit !== null) {
                  
               // should the URL length be deducted?
               if (this.widgets.statusUpdateUrlCheckbox.checked) {
                  // get URL length
                  var urlLength = parseInt(Dom.getAttribute(this.widgets.statusUpdateUrlCheckbox, "rel"), 10)
                  // deduct from count
                  count = count + urlLength;
                  // let the user know it's been deducted
                  this.widgets.statusUpdateCountURL.innerHTML = Alfresco.util.message("socialPublish.dialogue.statusUpdate.urlChars", this.name,
                  {
                        "0": urlLength
                  }); 
               } 
               else 
               {
                  // user doesn't need to know about it.
                  this.widgets.statusUpdateCountURL.innerHTML = "";
               }
               
               // deduct the current character count
               count = count + this.widgets.statusUpdateText.value.length;
               
               // chars remaining
               charsRemaining = limit - count;
               
               // display the characters remaining:
               this.widgets.statusUpdateCount.innerHTML = Alfresco.util.message("socialPublish.dialogue.statusUpdate.charsRemaining", this.name,
               {
                     "0": charsRemaining
               });
               
               if (charsRemaining < 0) {
                  if (this.widgets.truncationNotification === null)
                  {
                     // TODO: build up list of truncation channels. Refresh balloon with updates to list of truncation channels.
                     this.widgets.truncationNotification = Alfresco.util.createBalloon(this.widgets.statusUpdateText,
                     {
                        html: "Tuncation will occur!",
                        width: "24em"
                     });
                  
                     this.widgets.truncationNotification.show();
                  }
               } else {
                  if (this.widgets.truncationNotification) {
                     this.widgets.truncationNotification.hide();
                  }
                  this.widgets.truncationNotification = null;
               }
            } 
            else 
            {
               // hide count if there's nothing selected.
               this.widgets.statusUpdateCount.innerHTML = this.widgets.statusUpdateCountURL.innerHTML = "";  
            }
         } 
         else 
         {
            // hide count if there's nothing selected.
            this.widgets.statusUpdateCount.innerHTML = this.widgets.statusUpdateCountURL.innerHTML = "";
         }            
         
      },
      
      /**
       * Gets All Publising Channels
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