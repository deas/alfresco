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
 * WebPreview component. 
 *
 * @namespace Alfresco
 * @class Alfresco.WebPreview
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element,
      KeyListener = YAHOO.util.KeyListener;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * WebPreview constructor.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.WebPreview} The new WebPreview instance
    * @constructor
    * @private
    */
   Alfresco.WebPreview = function(containerId)
   {
      Alfresco.WebPreview.superclass.constructor.call(this, "Alfresco.WebPreview", containerId, ["button", "container", "uploader"]);

      /* Decoupled event listeners are added in setOptions */
      YAHOO.Bubbling.on("documentDetailsAvailable", this.onDocumentDetailsAvailable, this);
      YAHOO.Bubbling.on("recalculatePreviewLayout", this.onRecalculatePreviewLayout, this);

      return this;
   };

   YAHOO.extend(Alfresco.WebPreview, Alfresco.component.Base,
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
          * Noderef to the content to display
          *
          * @property nodeRef
          * @type string
          */
         nodeRef: "",

         /**
          * The size of the content
          *
          * @property size
          * @type string
          */
         size: "0",

         /**
          * The file name representing root container
          *
          * @property name
          * @type string
          */
         name: "",

         /**
          * The icon displayed in the header of the component
          *
          * @property icon
          * @type string
          */
         icon: "",

         /**
          * The mimeType of the node to display, needed to decide what preview
          * that should be used.
          *
          * @property mimeType
          * @type string
          */
         mimeType: "",

         /**
          * A list of previews available for this component
          *
          * @property previews
          * @type Array
          */
         previews: [],

         /**
          * Decides if the Previewer shall disable the i18n input fix shall be disabled for all browsers.
          * If it shall be disabled for certain a certain os/browser override the disableI18nInputFix() method.
          *
          * Fix solves the Flash i18n input keyCode bug when "wmode" is set to "transparent"
          * http://bugs.adobe.com/jira/browse/FP-479
          * http://issues.alfresco.com/jira/browse/ALF-1351
          *
          * ...see "Browser Testing" on this page to see supported browser/language combinations for AS2 version
          * http://analogcode.com/p/JSTextReader/
          *
          * ... We are using the AS3 version of the same fix
          * http://blog.madebypi.co.uk/2009/04/21/transparent-flash-text-entry/
          *
          * @property disableI18nInputFix
          * @type boolean
          */
         disableI18nInputFix: false
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function WP_onComponentsLoaded()
      {
         /**
          * SWFObject patch
          * Ensures all flashvars are URI encoded
          */
         YAHOO.deconcept.SWFObject.prototype.getVariablePairs = function()
         {
             var variablePairs = [],
                key,
                variables = this.getVariables();
             
             for (key in variables)
             {
                if (variables.hasOwnProperty(key))
                {
                   variablePairs[variablePairs.length] = key + "=" + encodeURIComponent(variables[key]);
                }
             }
             return variablePairs;
          };
         
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function WP_onReady()
      {
         // Setup web preview
         this._setupWebPreview(false);
      },

      /**
       * Called when document details has been available or changed (if the useDocumentDetailsAvailableEvent
       * option was set to true) on the page so the web previewer can remove its old preview and
       * display a new one if available.
       *
       * @method onDocumentDetailsAvailable
       * @param p_layer The type of the event
       * @param p_args Event information
       */
      onDocumentDetailsAvailable: function WP_onDocumentDetailsAvailable(p_layer, p_args)
      {
         // Get the new info about the node and decide if the previewer must be refreshed
         var documentDetails = p_args[1].documentDetails,
            refresh = false;

         // Name
         if (this.options.name != documentDetails.fileName)
         {
            this.options.name = documentDetails.fileName;
            refresh = true;
         }

         // Mime type
         if (this.options.mimeType != documentDetails.mimetype)
         {
            this.options.mimeType = documentDetails.mimetype;
            refresh = true;
         }

         // Size
         if (this.options.size != documentDetails.size)
         {
            this.options.size = documentDetails.size;
            refresh = true;
         }

         // Setup previewer
         if (refresh)
         {
            this._setupWebPreview();
         }
      },

      /**
       * Because the WebPreview content is absolutely positioned, components which alter DOM layout can fire
       * this event to prompt a recalculation of the absolute coordinates.
       *
       * @method onRecalculatePreviewLayout
       * @param p_layer The type of the event
       * @param p_args Event information
       */
      onRecalculatePreviewLayout: function WP_onRecalculatePreviewLayout(p_layer, p_args)
      {
         // Only if not in maximize view
         if (this.widgets.realSwfDivEl.getStyle("height") !== "100%")
         {
            this._positionOver(this.widgets.realSwfDivEl, this.widgets.shadowSfwDivEl);
         }
      },

      /**
       * Will setup the
       *
       * @method _setupWebPreview
       * @private
       */
      _setupWebPreview: function WP__setupWebPreview()
      {
         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.widgets.swfPlayerMessage = Dom.get(this.id + "-swfPlayerMessage-div");
         this.widgets.titleText = Dom.get(this.id + "-title-span");
         this.widgets.titleImg = Dom.get(this.id + "-title-img");

         // Set title and icon         
         this.widgets.titleText.innerHTML = $html(this.options.name);
         this.widgets.titleImg.src = Alfresco.constants.URL_RESCONTEXT + this.options.icon.substring(1);

         // Parameter nodeRef is mandatory
         if (this.options.nodeRef === undefined)
         {
             throw new Error("A nodeRef must be provided");
         }

         /**
          * To support full window mode an extra div (realSwfDivEl) is created with absolute positioning
          * which will have the same position and dimensions as shadowSfwDivEl.
          * The realSwfDivEl element is to make sure the flash move is on top of all other divs and
          * the shadowSfwDivEl element is to make sure the previewer takes the screen real estate it needs.
          */
         if (!this.widgets.realSwfDivEl)
         {
            var realSwfDivEl = new Element(document.createElement("div"));
            realSwfDivEl.set("id", this.id + "-real-swf-div");
            realSwfDivEl.setStyle("position", "absolute");
            realSwfDivEl.addClass("web-preview");
            realSwfDivEl.addClass("real");            
            realSwfDivEl.appendTo(document.body);
            this.widgets.realSwfDivEl = realSwfDivEl;
         }
         this.widgets.shadowSfwDivEl = new Element(this.id + "-shadow-swf-div");

         if (this.options.size == "0")
         {
            // Shrink the web previewers real estate and tell user that node has no content
            this.widgets.shadowSfwDivEl.removeClass("has-content");
            this.widgets.realSwfDivEl.addClass("no-content");
            this.widgets.swfPlayerMessage.innerHTML = this.msg("label.noContent");
         }
         else if (Alfresco.util.hasRequiredFlashPlayer(9, 0, 124))
         {

            // Find the url to the preview
            var previewCtx = this._resolvePreview();
            if (previewCtx)
            {                  
               // Make sure the web previewers real estate is big enough for displaying something
               this.widgets.shadowSfwDivEl.addClass("has-content");
               this.widgets.realSwfDivEl.removeClass("no-content");

               // Create flash web preview by using swfobject
               var swfId = "WebPreviewer_" + this.id;
               var so = new YAHOO.deconcept.SWFObject(Alfresco.constants.URL_CONTEXT + "components/preview/WebPreviewer.swf",
                     swfId, "100%", "100%", "9.0.45");
               so.addVariable("fileName", this.options.name);
               so.addVariable("paging", previewCtx.paging);
               so.addVariable("url", previewCtx.url);
               so.addVariable("jsCallback", "Alfresco.util.ComponentManager.get('" + this.id + "').onWebPreviewerEvent");
               so.addVariable("jsLogger", "Alfresco.util.ComponentManager.get('" + this.id + "').onWebPreviewerLogging");
               so.addVariable("i18n_actualSize", this.msg("preview.actualSize"));
               so.addVariable("i18n_fitPage", this.msg("preview.fitPage"));
               so.addVariable("i18n_fitWidth", this.msg("preview.fitWidth"));
               so.addVariable("i18n_fitHeight", this.msg("preview.fitHeight"));
               so.addVariable("i18n_fullscreen", this.msg("preview.fullscreen"));
               so.addVariable("i18n_fullwindow", this.msg("preview.fullwindow"));
               so.addVariable("i18n_fullwindow_escape", this.msg("preview.fullwindowEscape"));
               so.addVariable("i18n_page", this.msg("preview.page"));
               so.addVariable("i18n_pageOf", this.msg("preview.pageOf"));
               so.addVariable("show_fullscreen_button", true);
               so.addVariable("show_fullwindow_button", true);
               so.addVariable("disable_i18n_input_fix", this.disableI18nInputFix());
               so.addParam("allowScriptAccess", "sameDomain");
               so.addParam("allowFullScreen", "true");
               so.addParam("wmode", "transparent");

               // Finally create (or recreate) the flash web preview in the new div
               this.widgets.swfPlayerMessage.innerHTML = "";
               so.write(this.widgets.realSwfDivEl.get("id"));
               this.widgets.swfObject = so;

               /**
                * FF3 and SF4 hides the browser cursor if the flashmovie uses a custom cursor
                * when the flash movie is placed/hidden under a div (which is what happens if a dialog
                * is placed on top of the web previewer) so we must turn off custom cursor
                * when the html environment tells us to.
                */
               Event.addListener(swfId, "mouseover", function(e)
               {
                  var swf = Dom.get(swfId);
                  if (swf && YAHOO.lang.isFunction(swf.setMode))
                  {
                     Dom.get(swfId).setMode("active");
                  }
               });
               Event.addListener(swfId, "mouseout", function(e)
               {
                  var swf = Dom.get(swfId);
                  if (swf && YAHOO.lang.isFunction(swf.setMode))
                  {
                     Dom.get(swfId).setMode("inactive");
                  }
               });

               // Page unload / unsaved changes behaviour
               Event.addListener(window, "resize", function(e)
               {
                  YAHOO.Bubbling.fire("recalculatePreviewLayout");
               });
            }
            else
            {
               // Shrink the web previewers real estate and tell user that the node has nothing to display
               this.widgets.shadowSfwDivEl.removeClass("has-content");
               this.widgets.realSwfDivEl.addClass("no-content");
               var url = Alfresco.constants.PROXY_URI + "api/node/content/" + this.options.nodeRef.replace(":/", "") + "/" + encodeURIComponent(this.options.name) + "?a=true";
               this.widgets.swfPlayerMessage.innerHTML = this.msg("label.noPreview", url);
            }
         }
         else
         {
            // Shrink the web previewers real estate and tell user that no sufficient flash player is installed
            this.widgets.shadowSfwDivEl.removeClass("has-content");
            this.widgets.realSwfDivEl.addClass("no-content");
            this.widgets.swfPlayerMessage.innerHTML = this.msg("label.noFlash");
         }

         // Place the real flash preview div on top of the shadow div
         this._positionOver(this.widgets.realSwfDivEl, this.widgets.shadowSfwDivEl);
      },


      /**
       *
       * Overriding this method to implement a os/browser version dependent version that decides
       * if the i18n fix described for the disableI18nInputFix option shall be disabled or not.
       *
       * @method disableI18nInputFix
       * @return false
       */
      disableI18nInputFix: function WP__resolvePreview(event)
      {
         // Override this method if you want to turn off the fix for a specific client
         return this.options.disableI18nInputFix;
      },

      /**
       * Helper method for deciding what preview to use, if any
       *
       * @method _resolvePreview
       * @return the name of the preview to use or null if none is appropriate
       */
      _resolvePreview: function WP__resolvePreview(event)
      {
         var ps = this.options.previews,
            webpreview = "webpreview", imgpreview = "imgpreview",
            nodeRefAsLink = this.options.nodeRef.replace(":/", ""),
            argsNoCache = "?c=force&noCacheToken=" + new Date().getTime(),
            preview, url;
         
         if (this.options.mimeType.match(/^image\/jpeg$|^image\/png$|^image\/gif$/))         
         {
            /* The content matches an image mimetype that the web-previewer can handle without a preview */
            url = Alfresco.constants.PROXY_URI + "api/node/" + nodeRefAsLink + "/content" + argsNoCache;
            return (
            {
               url: url,
               paging: false
            });
         }
         else if (this.options.mimeType.match(/application\/x-shockwave-flash/))
         {
            url = Alfresco.constants.PROXY_URI + "api/node/content/" + nodeRefAsLink + argsNoCache + "&a=false";
            return (
            {
               url: url,
               paging: false
            });
         }
         else
         {
            preview = Alfresco.util.arrayContains(ps, webpreview) ? webpreview : (Alfresco.util.arrayContains(ps, imgpreview) ? imgpreview : null);
            if (preview !== null)
            {
               url = Alfresco.constants.PROXY_URI + "api/node/" + nodeRefAsLink + "/content/thumbnails/" + preview + argsNoCache;
               return (
               {
                  url: url,
                  paging: true
               });
            }
            return null;
         }
      },

      /**
       * Called from the WebPreviewer when a log message has been logged.
       *
       * @method onWebPreviewerLogging
       * @param msg {string} The log message
       * @param level {string} The log level
       */
      onWebPreviewerLogging: function WP_onWebPreviewerLogging(msg, level)
      {
         if (YAHOO.lang.isFunction(Alfresco.logger[level]))
         {
            Alfresco.logger[level].call(Alfresco.logger, "WebPreviewer: " + msg);
         }
      },

      /**
       * Called from the WebPreviewer when an event or error is dispatched.
       *
       * @method onWebPreviewerEvent
       * @param event {object} an WebPreview message
       */
      onWebPreviewerEvent: function WP_onWebPreviewerEvent(event)
      {
         if (event.event)
         {
            if (event.event.type == "onFullWindowClick")
            {
               var clientRegion = Dom.getClientRegion();
               this.widgets.realSwfDivEl.setStyle("left", clientRegion.left + "px");
               this.widgets.realSwfDivEl.setStyle("top", clientRegion.top + "px");
               this.widgets.realSwfDivEl.setStyle("width", "100%");
               this.widgets.realSwfDivEl.setStyle("height", "100%");
            }
            else if (event.event.type == "onFullWindowEscape")
            {               
               this._positionOver(this.widgets.realSwfDivEl, this.widgets.shadowSfwDivEl);
            }
         }
         else if (event.error)
         {
            // Inform the user about the failure
            var message = "Error";
            if (event.error.code)
            {
               message = this.msg("error." + event.error.code);
            }
            Alfresco.util.PopupManager.displayMessage(
            {
               text: message
            });

            // Tell other components that the preview failed
            YAHOO.Bubbling.fire("webPreviewFailure",
            {
               error: event.error.code,
               nodeRef: this.showConfig.nodeRef,
               failureUrl: this.showConfig.failureUrl
            });
         }
      },

      /**
       * Positions the one element over another
       *
       * @method _positionOver
       * @param event
       */
      _positionOver: function WP__positionOver(positionedYuiEl, sourceYuiEl)
      {
         var region = Dom.getRegion(sourceYuiEl.get("id"));
         positionedYuiEl.setStyle("left", region.left + "px");
         positionedYuiEl.setStyle("top", region.top + "px");
         positionedYuiEl.setStyle("width", region.width + "px");
         positionedYuiEl.setStyle("height", region.height + "px");
      }
   });
})();