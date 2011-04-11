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
 * Alfresco.dashlet.RssFeed
 *
 * Aggregates events from all the sites the user belongs to.
 * For use on the user's dashboard.
 *
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * RssFeed constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.RssFeed} The new RssFeed instance
    * @constructor
    */
   Alfresco.dashlet.RssFeed = function RssFeed_constructor(htmlId)
   {
      Alfresco.dashlet.RssFeed.superclass.constructor.call(this, "Alfresco.dashlet.RssFeed", htmlId);
      
      this.configDialog = null;
      
      return this;
   };

   YAHOO.extend(Alfresco.dashlet.RssFeed, Alfresco.component.Base,
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
          * The component id
          *
          * @property componentId
          * @type string
          * @default ""
          */
         componentId: "",

         /**
          * THe url to the feed to display
          *
          * @property feedURL
          * @type string
          * @default ""
          */
         feedURL: "",

         /**
          * The maximum limit of posts to display
          *
          * @property limit
          * @type string
          * @default "all"
          */
         limit: "all"
      },  
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RF_onReady()
      {
         // Add click handler to config feed link that will be visible if user is site manager.
         var configFeedLink = Dom.get(this.id + "-configFeed-link");
         if (configFeedLink)
         {
            Event.addListener(configFeedLink, "click", this.onConfigFeedClick, this, true);            
         }
      },

      /**
       * Called when the user clicks the config rss feed link.
       * Will open a rss config dialog
       *
       * @method onConfigFeedClick
       * @param e The click event
       */
      onConfigFeedClick: function RF_onConfigFeedClick(e)
      {
         Event.stopEvent(e);
         
         var actionUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/feed/config/" + encodeURIComponent(this.options.componentId);
         
         if (!this.configDialog)
         {
            this.configDialog = new Alfresco.module.SimpleDialog(this.id + "-configDialog").setOptions(
            {
               width: "50em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/feed/config",
               onSuccess:
               {
                  fn: function RssFeed_onConfigFeed_callback(response)
                  {
                     var rss = response.json;

                     // Save url for new config dialog openings
                     this.options.feedURL = (rss && rss.feedURL) ? rss.feedURL : this.options.feedURL;
                     this.options.limit = rss.limit;

                     // Update title and items are with new rss 
                     Dom.get(this.id + "-title").innerHTML = rss ? rss.title : "";
                     Dom.get(this.id + "-scrollableList").innerHTML = (rss && rss.content !== "") ? rss.content : ('<h3>' + this.msg("label.noItems") + '</h3>');
                  },
                  scope: this
               },
               doSetupFormsValidation:
               {
                  fn: function RssFeed_doSetupForm_callback(form)
                  {
                     form.addValidation(this.configDialog.id + "-url", Alfresco.forms.validation.mandatory, null, "keyup");
                     form.addValidation(this.configDialog.id + "-url", Alfresco.forms.validation.url, null, "keyup");
                     form.setShowSubmitStateDynamically(true, false);
                     
                     Dom.get(this.configDialog.id + "-url").value = this.options.feedURL;
                     
                     var select = Dom.get(this.configDialog.id + "-limit"), options = select.options, option, i, j;
                     for (i = 0, j = options.length; i < j; i++)
                     {
                        option = options[i];
                        if (option.value === this.options.limit)
                        {
                           option.selected = true;
                           break;
                        }
                     }
                  },
                  scope: this
               }
            });
         }

         this.configDialog.setOptions(
         {
            actionUrl: actionUrl
         }).show();
      }
   });
})();