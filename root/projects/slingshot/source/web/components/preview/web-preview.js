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
 * Displays the node's content using various plugins depending on the node's content mime type & thumbnails available.
 * Document, video & image previewers are shipped out of the box.
 *
 * Now supports plugins to be able to render custom content, by adding new <plugin-condition> elements in .get.config.xml.
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
      // Note "uploader" is required so we get the YAHOO.deconcept.SWFObject
      Alfresco.WebPreview.superclass.constructor.call(this, "Alfresco.WebPreview", containerId, ["button", "container", "uploader"]);
      this.plugin = null;

      /* Decoupled event listeners are added in setOptions */
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
          * The mimeType of the node to display, needed to decide what plugin that should be used.
          *
          * @property mimeType
          * @type string
          */
         mimeType: "",

         /**
          * A list of previews available for this node, needed to decide which plugin that should be used.
          *
          * @property previews
          * @type Array
          */
         thumbnails: [],

         /**
          * A json representation of the .get.config.xml file.
          * This is evaluated on the client side since we need the plugins to make sure it is supported
          * the user's browser and browser plugins.
          *
          * @property pluginConditions
          * @type Array
          */
         pluginConditions: []
      },

      /**
       * Space for preview "plugins" to register themselves in.
       * To provide a 3rd party plugin:
       *
       * 1. Create a javascript file and make it define a javascript class that defines a "plugin class" in this namespace.
       * 2. Override this component's .get.head.ftl file and make sure your javascript file (and its resources) are included.
       * 3. Override this component's .get.config.xml and define for which mimeTypes or thumbnails it shall be used.
       * 4. To make sure your plugin works in the browser, define a report() method that
       *    returns nothing if the browser is supported and otherwise a string with a message saying the reason the
       *    plugin can't be used in the browser.
       * 5. Define a display() method that will display the browser plugin or simply return a string of markup that shall be inserted.
       *
       * @property Plugins
       * @type Object
       */
      Plugins: {},

      /**
       * If a plugin was found to preview the content it will be stored here, for future reference.
       *
       * @property plugin One of the plugins that have registered themselves in Alfresco.WebPreview.Plugin
       * @type Object
       * @public
       */
      plugin: null,
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function WP_onComponentsLoaded()
      {
          // SWFObject patch to help flash plugins, will ensure all flashvars are URI encoded
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
         this.setupPreview(false);
      },

      /**
       * Will find a previewer and set it up if one existed
       *
       * @method resolvePreviewer
       * @private
       */
      setupPreview: function WP_setupPreview()
      {
         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.widgets.previewerElement = Dom.get(this.id + "-previewer-div");

         // Parameter nodeRef is mandatory
         if (this.options.nodeRef === undefined)
         {
            throw new Error("A nodeRef must be provided");
         }

         if (this.options.size == "0")
         {
            // Shrink the web previewers real estate and tell user that node has no content
            this.widgets.previewerElement.innerHTML = '<div class="message">' + this.msg("label.noContent") + '</div>';
         }
         else
         {
            var condition, pluginDescriptor, plugin, messages = [];
            for (var i = 0, il = this.options.pluginConditions.length; i <il ; i++)
            {
               // Test that all conditions are valid
               condition = this.options.pluginConditions[i];
               if (!this.conditionsMatch(condition))
               {
                  continue;
               }

               // Conditions are valid, now create plugins and make sure they can run in this environment
               for (var pi = 0, pil = condition.plugins.length; pi < pil; pi++)
               {
                  // Create plugin
                  pluginDescriptor = condition.plugins[pi];
                  plugin = new Alfresco.WebPreview.prototype.Plugins[pluginDescriptor.name](this, pluginDescriptor.attributes);

                  // Make sure it may run in this browser...
                  var report = plugin.report();
                  if (report)
                  {
                     // ...the plugin can't be used in this browser, save report and try another plugin
                     messages.push(report);
                  }
                  else
                  {
                     // ...yes, the plugin can be used in this browser, lets store a reference to it.
                     this.plugin = plugin;

                     // Ask the plugin to display the node
                     var markup;
                     try
                     {
                        Dom.addClass(this.widgets.previewerElement, pluginDescriptor.name);
                        markup = plugin.display();
                        if (markup)
                        {
                           // Insert markup if plugin provided it
                           this.widgets.previewerElement.innerHTML = markup;
                        }

                        // Finally! We found a plugin that works and didn't crash
                        return;
                     }
                     catch(e)
                     {
                        // Oops a plugin failure, log it and try the next one instead...
                        Alfresco.logger.error('Error, Alfresco.WebPreview.Plugins.' + pluginDescriptor.name + ' failed to display: ' + e);
                        messages.push(this.msg("label.error", pluginDescriptor.name, e.message));                        
                     }
                  }
               }
            }

            // Tell user that the content can't be displayed
            var message = this.msg("label.noPreview", this.getContentUrl(true));
            for (i = 0, il = messages.length; i < il; i++)
            {
               message += '<br/>' + messages[i];
            }
            this.widgets.previewerElement.innerHTML = '<div class="message">' + message + '</div>';
         }
      },

      /**
       * Checks if the conditions are fulfilled.
       *
       * @method conditionsMatch
       * @param condition {Object} The condition to match gainst this components options
       * @return true of conditions are fulfilled for plugins to be used.
       * @public
       */
      conditionsMatch: function WP_conditionsMatch(condition)
      {
         if (condition.attributes.mimeType && condition.attributes.mimeType != this.options.mimeType)
         {
            return false;
         }
         if (condition.attributes.thumbnail && !Alfresco.util.arrayContains(this.options.thumbnails, condition.attributes.thumbnail))
         {
            return false;
         }
         return true;
      },

      /**
       * Helper method for plugins to create url tp the node's content.
       *
       * @method getContentUrl
       * @param {Boolean} (Optional) Default false. Set to true if the url shall be constructed so it forces the
       *        browser to download the document, rather than displaying it inside the browser. 
       * @return {String} The "main" element holding the actual previewer.
       * @public
       */
      getContentUrl: function WP_getContentUrl(download)
      {
         var nodeRefAsLink = this.options.nodeRef.replace(":/", ""),
            noCache = "noCache=" + new Date().getTime();
         download = download ? "a=true" : "a=false";
         return Alfresco.constants.PROXY_URI + "api/node/content/" + nodeRefAsLink + "/" + this.options.name + "?c=force&" + noCache + "&" + download
      },

      /**
       * Helper method for plugins to create a url to the thumbnail's content.
       *
       * @param thumbnail {String} The thumbnail definition name
       * @param fileSuffix {String} (Optional) I.e. ".png" if shall be inserted in the url to make certain flash
       *        plugins understand the mimetype of the thumbnail.
       * @return {String} The url to the thumbnail content.
       * @public
       */
      getThumbnailUrl: function WP_getThumbnailUrl(thumbnail, fileSuffix)
      {
         var nodeRefAsLink = this.options.nodeRef.replace(":/", ""),
               noCache = "noCache=" + new Date().getTime(),
               force = "c=force";
         return Alfresco.constants.PROXY_URI + "api/node/" + nodeRefAsLink + "/content/thumbnails/" + thumbnail + (fileSuffix ? "/suffix" + fileSuffix : "") + "?" + noCache + "&" + force
      },

      /**
       * Makes it possible for plugins to get hold of the "previewer wrapper" HTMLElement.
       *
       * I.e. Useful for elements that use an "absolute" layout for their plugins (most likely flash), so they have
       * an element in the Dom to position their own elements after.
       *
       * @method getPreviewerElement
       * @return {HTMLElement} The "main" element holding the actual previewer.
       * @public
       */
      getPreviewerElement: function()
      {
         return this.widgets.previewerElement;
      }

   });
})();
