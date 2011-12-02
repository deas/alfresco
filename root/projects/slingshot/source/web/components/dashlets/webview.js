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
 
/*
 *** Alfresco WebView Dashlet
 *
 * @namespace Alfresco.dashlet
 * @class Alfresco.dashlet.WebView
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
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   Alfresco.dashlet.WebView = function WebView_constructor(htmlId)
   {
      Alfresco.dashlet.WebView.superclass.constructor.call(this, "Alfresco.dashlet.WebView", htmlId);

      // Initialise prototype properties
      this.configDialog = null;

      /**
       * Decoupled event listeners
       */
      YAHOO.Bubbling.on("showPanel", this.onShowPanel, this);
      YAHOO.Bubbling.on("hidePanel", this.onHidePanel, this);

      return this;
   };

   YAHOO.extend(Alfresco.dashlet.WebView, Alfresco.component.Base,
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
          * ComponentId used for saving configuration
          * @property componentId
          * @type string
          */
         componentId: "",
         
         /**
          * URI for the web page to view
          * @property webviewURI
          * @type string
          */
         webviewURI: "",
         
         /**
          * Dashlet title
          * @property webviewTitle
          * @type string
          */
         webviewTitle: "",
         
         /**
          * Default web page
          * @property isDefault
          * @type boolean
          * @default true
          */
         isDefault: true
      },
      
      /**
       * Configuration dialog instance
       *
       * @property configDialog
       * @type object
       */
      configDialog: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initialises components, including YUI widgets.
       *
       * @method onReady
       */
      onReady: function WebView_onReady()
      {
         var configWebViewLink = Dom.get(this.id + "-configWebView-link");
         Event.addListener(configWebViewLink, "click", this.onConfigWebViewClick, this, true);

         /**
          * Save reference to iframe wrapper so we can hide and show it depending
          * on how well the browser handles flash movies.
          */
         this.widgets.iframeWrapper = Dom.get(this.id + "-iframeWrapper");
      },

      /**
       * Event listener for configuration link click.
       *
       * @method onConfigWebViewClick
       * @param e {object} HTML event
       */
      onConfigWebViewClick: function WebView_onConfigWebViewClick(e)
      {
         Event.stopEvent(e);
         
         var actionUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/webview/config/" + encodeURIComponent(this.options.componentId);

         if (!this.configDialog)
         {
            this.configDialog = new Alfresco.module.SimpleDialog(this.id + "-configDialog").setOptions(
            {
               width: "50em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/webview/config",
               onSuccess:
               {
                  fn: function WebView_onConfigWebView_callback(response)
                  {
                     // MSIE6 doesn't redraw the IFRAME correctly, so tell it to refresh the page
                     if (YAHOO.env.ua.ie === 6)
                     {
                        window.location.reload(true);
                     }
                     else
                     {
                        var div = Dom.get(this.id + "-iframeWrapper");
                        div.innerHTML = response.serverResponse.responseText + '<div class="resize-mask"></div>';
                        var iframe = Dom.getFirstChildBy(div, function(node)
                        {
                           return (node.tagName.toUpperCase() == "IFRAME");
                        });
                        if (iframe)
                        {
                           if (iframe.attributes["name"])
                           {
                              var titleLink = Dom.get(this.id + "-title-link");
                              // update iframe and internal config
                              titleLink.href = this.options.webviewURI = iframe.attributes["src"].value;
                              this.options.webviewTitle = iframe.attributes["name"].value;
                              titleLink.innerHTML = $html(this.options.webviewTitle);
                           }
                           this.options.isDefault = false;
                        }
                     }
                  },
                  scope: this
               },
               doSetupFormsValidation:
               {
                  fn: function WebView_doSetupForm_callback(form)
                  {
                     form.addValidation(this.configDialog.id + "-url", Alfresco.forms.validation.mandatory, null, "blur");
                     form.addValidation(this.configDialog.id + "-url", Alfresco.forms.validation.url, null, "keyup");
                     
                     // 511 characters is the maximum length of URL that IE appears to support without causing a page direct
                     // and preventing the user from returning to their dashboard. To avoid this occurring a check on the length
                     // is set. Rather than just adding this for IE it is added for all browsers because it is possible that
                     // a user could edit the URL on one browser to something greater than 511 characters and then attempt
                     // to view the page in another browser.
                     form.addValidation(this.configDialog.id + "-url", function(field, args, event, form, silent, message)
                        {
                           return (field.value.length < 512);
                        }, null, "keyup");
                     form.setShowSubmitStateDynamically(true, false);

                     /* Get the link title */
                     var elem = Dom.get(this.configDialog.id + "-webviewTitle");
                     if (elem)
                     {
                        elem.value = this.options.webviewTitle;
                     }

                     /* Get the url value */
                     elem = Dom.get(this.configDialog.id + "-url");
                     if (elem)
                     {
                        elem.value = this.options.isDefault ? "http://" : this.options.webviewURI;
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
      },

      /**
       * Called when any Panel in share created with createYUIPanel is shown.
       * Will hide the content for browsers that can't handle a flash movies properly,
       * since the flash movie could hide parts of the the panel.
       *
       * @method onShowPanel
       * @param p_layer {object} Event fired (unused)
       * @param p_args {array} Event parameters (unused)
       */
      onShowPanel: function WW_onShowPanel(p_layer, p_args)
      {
         if (this._browserDestroysPanel())
         {
            Dom.setStyle(this.widgets.iframeWrapper, "visibility", "hidden");
         }
      },

      /**
       * Called when any Panel in share created with createYUIPanel is hidden.
       * Will display the content again if it was hidden before.
       *
       * @method onHidePanel
       * @param p_layer {object} Event fired (unused)
       * @param p_args {array} Event parameters (unused)
       */
      onHidePanel: function WW_onHidePanel(p_layer, p_args)
      {
         if (this._browserDestroysPanel())
         {
            Dom.setStyle(this.widgets.iframeWrapper, "visibility", "visible");
         }
      },

      /**
       * Returns true if browser will make flash movie hide parts of a panel
       *
       * @method _browserDestroysPanel
       * @return {boolean} True if browser will let flash movie mess up panel
       */
      _browserDestroysPanel: function WW__browserDestroysPanel()
      {
         // All browsers on Windows (tested w FP 10) and FF2 and below on Mac
         return (navigator.userAgent.indexOf("Windows") !== -1 ||
                 (navigator.userAgent.indexOf("Macintosh") !== -1 && YAHOO.env.ua.gecko > 0 && YAHOO.env.ua.gecko < 1.9));
      }
   });
})();