/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * An abstract view for the Alfresco Share document list. It can be used in JSON page models if 
 * configured with a widgets definition. Otherwise it can be extended to define specific views
 * 
 * @module alfresco/documentlibrary/views/DocumentListRenderer
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes dijit/_KeyNavContainer
 * @mixes module:alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/documentlibrary/_AlfDndDocumentUploadMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "dijit/_KeyNavContainer",
        "dojo/text!./templates/DocumentListRenderer.html",
        "alfresco/documentlibrary/views/layouts/_MultiItemRendererMixin",
        "alfresco/core/Core",
        "alfresco/core/JsNode",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/keys",
        "dojo/dom-construct",
        "dojo/dom-class",
        "dijit/registry"], 
        function(declare, _WidgetBase, _TemplatedMixin, _KeyNavContainer, template, _MultiItemRendererMixin, 
                 AlfCore, JsNode, lang, array, keys, domConstruct, domClass, registry) {
   
   return declare([_WidgetBase, _TemplatedMixin, _KeyNavContainer, _MultiItemRendererMixin, AlfCore], {
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/DocumentListRenderer.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/DocumentListRenderer.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance cssRequirements {Array}
       * @type {object[]}
       * @default [{cssFile:"./css/AlfDialog.css"}]
       */
      cssRequirements: [{cssFile:"./css/DocumentListRenderer.css"}],

      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,
      
      /**
       * The widgets to be processed to generate each item in the rendered view.
       * 
       * @instance 
       * @type {object[]} 
       * @default null
       */
      widgets: null,
      
      /**
       * Implements the widget life-cycle method to add drag-and-drop upload capabilities to the root DOM node.
       * This allows files to be dragged and dropped from the operating system directly into the browser
       * and uploaded to the location represented by the document list. 
       * 
       * @instance
       */
      postCreate: function alfresco_documentlibrary_views_DocumentListRenderer__postCreate() {
         this.inherited(arguments);
         this.setupKeyboardNavigation();
      },
      
      /**
       * This sets up the default keyboard handling for a view. The standard controls are navigation to the
       * next item by pressing the down key and navigation to the previous item by pressing the up key.
       * 
       * @instance
       */
      setupKeyboardNavigation: function alfresco_documentlibrary_views_DocumentListRenderer__setupKeyboardNavigation() {
         this.connectKeyNavHandlers([keys.UP_ARROW], [keys.DOWN_ARROW]);
      },
      
      /**
       * Overrides the _KevNavContainer function to call the "blur" function of the widget that has lost
       * focus (assuming it has one).
       * 
       * @instance
       */
      _onChildBlur: function alfresco_documentlibrary_views_DocumentListRenderer___onChildBlur(widget) {
         if (typeof widget.blur === "function")
         {
            widget.blur();
         }
      }
   });
});