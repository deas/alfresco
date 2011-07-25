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
 * ConsoleTrashcan tool component.
 *
 * @namespace Alfresco
 * @class Alfresco.ConsoleTrashcan
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event, Element = YAHOO.util.Element;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * ConsoleChannels constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ConsoleTrashcan} The new ConsoleTrashcan instance
    * @constructor
    */
   Alfresco.ConsoleChannels = function(htmlId)
   {
      this.name = "Alfresco.ConsoleChannels";
      Alfresco.ConsoleChannels.superclass.constructor.call(this, htmlId);
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "paginator", "json", "history"], this.onComponentsLoaded, this);
      
      /* Define panel handlers */
      var parent = this;
      
      // NOTE: the panel registered first is considered the "default" view and is displayed first
      
      /* File List Panel Handler */
      ListPanelHandler = function ListPanelHandler_constructor()
      {
         ListPanelHandler.superclass.constructor.call(this, "list");
      };
      
      YAHOO.extend(ListPanelHandler, Alfresco.ConsolePanelHandler, 
      {
         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onLoad
          */
         onLoad: function onLoad()
         {
            // Buttons
            //parent.widgets.emptyButton = Alfresco.util.createYUIButton(parent, "empty-button", parent.onEmptyClick);
         }
      });
      new ListPanelHandler();
      
      return this;
   };
   
   YAHOO.extend(Alfresco.ConsoleChannels, Alfresco.ConsoleTool, 
   {
      /**
       * Is the UI waiting for a callback from the Auth Scripts?
       *
       * @property isWaiting
       * @value {boolean}
       */
      isWaiting: false,
      
      /**
       * Handle to the window created during channel auth.
       *
       * @property authWindow
       */
      authWindow: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ConsoleChannels_onReady()
      {
         // Call super-class onReady() method
         Alfresco.ConsoleChannels.superclass.onReady.call(this);
         
         // add event listeners
         this.widgets.newChannelButton = new YAHOO.widget.Button(this.id + "-new-button",
			{
				type: "menu",
				menu: this.id + "-newChannel-menu",
				lazyloadmenu: false
			}); 
			
			Event.on(this.id + "-channelTypes", "click", this.onCreateChannel, this, true)
			Event.on(this.id + "-datatable", 'click', this.onChannelInteraction, this, true);
         // Set up list of channels as data table for ease of updating.
         this.widgets.channelDataTable = new Alfresco.util.DataTable(
         {
            dataSource: 
            {
               url: Alfresco.constants.PROXY_URI + "api/publishing/channels",
               doBeforeParseData: this.bind(function(oRequest, oFullResponse)
               {
                  // Channels List needs combining from list of publish and status update channels.
                  var channelList = [], publishChannels = oFullResponse.data.publishChannels, statusUpdateChannels = oFullResponse.data.statusUpdateChannels;
                  
                  channelList = publishChannels.concat(statusUpdateChannels, publishChannels, statusUpdateChannels, publishChannels, statusUpdateChannels)
                  return ({
                     "data": channelList
                  });
               })
            },
            dataTable: 
            {
               container: this.id + "-datatable",
               columnDefinitions: [
               {
                  key: "channel",
                  sortable: false,
                  formatter: this.bind(this.renderCellChannel)
               }],
               config: 
               {
                  MSG_EMPTY: this.msg("message.noVersions")
               }
            }
         });
         
			this.widgets.channelDataTable.widgets.dataTable.subscribe("cellClickEvent", this.onChannelInteraction, this, true); 
			
      },
      
      /**
       *
       * Called by the Alfresco.util.dataTable method to render each channel into the dom.
       *
       * @method renderCellChannel
       */
      renderCellChannel: function consoleChannels_renderCellChannel(elCell, oRecord, oColumn, oData)
      {
         elCell.innerHTML = this.renderChannel(oRecord.getData());
      },
      
      /**
       * Builds the HTML for existing channels
       *
       * @method renderChannel
       * @param {Object} channel
       */
      renderChannel: function consoleChannels_renderChannel(channel)
      {
         var rel = ' rel="' + channel.id + '"', 
			   deleteLink = '<a href=# class="channelAction delete" ' + rel + ' title="' + this.msg("channelAdmin.delete.tooltip") + '">' + this.msg("channelAdmin.delete") + '</a>',
				reauth = "",
				status = "authorised",
				title = this.msg("channelAdmin.authorised.tooltip"),
				image = '<img src="' + Alfresco.constants.PROXY_URI + channel.channelType.icon +  "32" + '" title="' + channel.channelType.title + '"/>'
            html = "";
         
			// Has the channel authorisation failed?
			if (channel.authorised !== "true" ) 
			{
				reauth = '<a href=# class="channelAction reauth" ' + rel + ' title="' + this.msg("channelAdmin.reauth.tooltip") + '">' + this.msg("channelAdmin.reauth") + '</a>';
				status = "notAuthorised";
				title = this.msg("channelAdmin.notAuthorised.tooltip")
			}
			
         html += '<div class="channel ' + status + '" title="' + title + '">' + image + '<span class="channelName">' + channel.name + '</span><span class="channelActions">' + reauth + deleteLink + '</span></div>'
         return html;
      },
      
      /**
       *
       * Called when the new channel button is clicked
       *
       * @method onCreateChannel
       */
      onCreateChannel: function consoleChannels_onCreateChannel(event, args)
      {
         var channelType = event.target.rel, 
			   newChannelURL = Alfresco.constants.PROXY_URI + "api/publishing/channels", 
				channelName = this.msg("channelAdmin.new-channel", channelType)
            params = "?channelType=" + channelType + "&channelName=" + channelName;
         
         Alfresco.util.Ajax.request(
         {
            url: newChannelURL + params,
            method: Alfresco.util.Ajax.POST,
            successCallback: 
            {
               fn: this.onCreateChannelSuccess,
               scope: this
            },
            failureCallback: 
            {
               fn: function consoleChannels_onCreateChannel_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("channelAdmin.failure")
                  });
               },
               scope: this
            }
         });
         
         
      },
      
      onCreateChannelSuccess: function consoleChannels_onCreateChannelSuccess(response)
      {
         var url = response.json.data.authoriseUrl;
         this.authWindow = window.open(url);
         this.isWaiting = true;
      },
      
		/**
		 * 
		 * Called when one of the channel actions is triggered.
		 * 
		 * @method onChannelInteraction
		 * @param {Object} o
		 * @param {Object} args
		 */
      onChannelInteraction: function consoleChannel_onChannelInteraction(o, args)
		{
			if (YAHOO.util.Selector.test(o.event.target, 'a.delete'))
			{
				this.onDeleteChannel(o.event, args);
			}
			else if (YAHOO.util.Selector.test(o.event.target, 'a.reauth')) 
			{
				this.onReauthChannel(o.event, args);
			}
		},
		
		/**
		 * Triggers an API call to delete the channel.
		 * 
		 *  @method onDeleteChannel
		 */
		onDeleteChannel: function consoleChannels_onDeleteChannel(event, args)
		{
			var me = this;
			Alfresco.util.PopupManager.displayPrompt(
         {
            title: me.msg("channelAdmin.delete.title"),
            text: me.msg("channelAdmin.delete.confirm"),
            buttons: [
            {
               text: me.msg("button.ok"),
               handler: function()
               {
                  this.destroy();
						
						var deleteURL = Alfresco.constants.PROXY_URI + "api/publishing/channels/" + event.target.rel.replace("://", "/")
         
			         Alfresco.util.Ajax.request(
			         {
			            url: deleteURL,
			            method: Alfresco.util.Ajax.DELETE,
			            successCallback: 
			            {
			               fn: me.onDeleteChannelSuccess,
			               scope: me
			            },
			            failureCallback: 
			            {
			               fn: function consoleChannels_onDeleteChannel_failure(response)
			               {
			                  Alfresco.util.PopupManager.displayPrompt(
			                  {
			                     text: me.msg("channelAdmin.delete.failure")
			                  });
			               },
			               scope: me
			            }
			         });
						
               }
            },
            {
               text: me.msg("button.cancel"),
               handler: function()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
			
		},
		
		/**
		 * 
		 * Called when the Ajax call to the Delete API succeeds. 
		 * 
		 * @method onDeleteChannelSuccess
		 * @param {Object} response
		 */
		onDeleteChannelSuccess: function consoleChannels_onDeleteChannelSuccess(response)
		{
			Alfresco.util.PopupManager.displayMessage(
         {
            text: this.msg("channelAdmin.delete.success")
         });
			this.refresh;
		},
		
		/**
		 * 
		 * Called by the reauth channelAction
		 * 
		 * @method onReauthChannel
		 * @param {Object} event
		 * @param {Object} args
		 */
		onReauthChannel: function consoleChannels_onReauthChannel(event, args)
		{
			console.log("reauth");
		},
		
      /**
       * Overrides the onStateChanged to listen for token response
       *
       * @method onStateChanged
       */
      onStateChanged: function ConsoleChannels_onStateChanged()
      {
         var hash = window.location.hash;
         if (hash !== "" && this.isWaiting === true) 
         {
            this.isWaiting = false;
            if (hash === "complete") 
            {
               this.onAuthComplete();
            }
            else 
            {
               // submit to authoriseCallback URL & then call onAuthComplete
               this.onAuthComplete();
            }
         }
      },
      
      /**
       * Called when a channel has been created and authentication has finished (regardless to outcome)
       *
       * @method onAuthComplete
       */
      onAuthComplete: function consoleChannels_onAuthComplete()
      {
         console.log("Authentication Complete");
			// reload channels
			this.refresh();
      },
		
		/**
		 * Refreshes the channels
		 * 
		 */
      refresh: function consoleChannels_refresh()
		{
			console.log("refresh");
		}
   });
})();
