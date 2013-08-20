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
 * Renders the value of a property found in the configured [currentItem]{@link module:alfresco/renderers/Property#currentItem}
 * attribute. The property that will be rendered is determined by the [propertyToRender]{@link module:alfresco/renderers/Property#propertyToRender}
 * attribute which should be defined in "dot-notation" format (e.g. "node.properties.cm:title"). This widget accepts a number
 * of different configuration options that control how the property is ultimately displayed.
 * 
 * @module alfresco/renderers/Property
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/core/ObjectTypeUtils
 * @mixes module:alfresco/renderers/_ItemLinkMixin
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase", 
        "dijit/_TemplatedMixin",
        "alfresco/core/ObjectTypeUtils",
        "dojo/text!./templates/Property.html",
        "alfresco/core/Core",
        "alfresco/renderers/_ItemLinkMixin",
        "dojo/_base/lang",
        "dojo/dom-class"], 
        function(declare, _WidgetBase, _TemplatedMixin, ObjectTypeUtils, template, AlfCore, _ItemLinkMixin, lang, domClass) {

   return declare([_WidgetBase, _TemplatedMixin, AlfCore, _ItemLinkMixin], {
      
      
      /**
       * Declare the dependencies on "legacy" JS files that this is wrapping.
       * 
       * @instance
       * @type {string[]}
       * @default ["/yui/yahoo/yahoo.js","/js/alfresco.js"]
       */
      nonAmdDependencies: ["/yui/yahoo/yahoo.js",
                           "/js/alfresco.js"],
      
      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {{i18nFile: string}[]}
       * @default [{i18nFile: "./i18n/Property.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/Property.properties"}],
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {{cssFile: string, media: string}[]}
       * @default [{cssFile:"./css/Property.css"}]
       */
      cssRequirements: [{cssFile:"./css/Property.css"}],
      
      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {string}
       */
      templateString: template,
      
      /**
       * This is the object that the property to be rendered will be retrieved from.
       * 
       * @instance
       * @type {object}
       * @default null
       */
      currentItem: null,
      
      /**
       * This should be set to the name of the property to render (e.g. "cm:name"). The property is expected
       * to be in the properties map for the item being rendered. 
       * 
       * @instance
       * @type {string}
       * @default null
       */
      propertyToRender: null,
      
      /**
       * This will be set with the rendered value.
       * 
       * @instance
       * @type {string}
       * @default null
       */
      renderedValue: null,
      
      /**
       * This can be set to apply a CSS class to the rendered property value
       * 
       * @instance
       * @type {string}
       * @default "alfresco-renderers-Property"
       */
      renderedValueClass: "alfresco-renderers-Property",
      
      /**
       * A label to go before the rendered value
       * @instance
       * @type {string} 
       * @default ""
       */
      renderedValuePrefix: "",

      /**
       * A label to go after the rendered value
       * @instance
       * @type {string} 
       * @default ""
       */
      renderedValueSuffix: "",
      
      /**
       * By default this is the empty string but will be converted to the HTML for opening an anchor
       * when the property is to be used as a link to access the item
       * @instance
       * @type {string}  
       * @default ""
       */
      anchorOpen: "",
      
      /**
       * By default this is the empty string but will be converted to the HTML for closing an anchor
       * when the property is to be used as a link to access the item
       * @instance
       * @type {string}  
       * @default ""
       */
      anchorClose: "",
      
      /**
       * Indicates whether or not to render this property as a link
       * @instance
       * @type {boolean}
       * @default false 
       */
      renderAsLink: false,
      
      /**
       * Indicates whether or not to hide the property if is not available or not set
       * @instance
       * @type {boolean}
       * @default false 
       */
      warnIfNotAvailable: false,
      
      /**
       * This can be either small, medium or large.
       * @instance
       * @type {string}  
       * @default "medium"
       */
      renderSize: "medium",
      
      /**
       * This indicates whether or not the requestes property can be found for the current item
       * 
       * @instance
       * @type {boolean} 
       * @default true
       */
      renderPropertyNotFound: true,
      
      /**
       * Indicates that this should only be displayed when the item (note: NOT the renderer) is
       * hovered over.
       * 
       * @instance
       * @type {boolean}
       * @default false
       */
      onlyShowOnHover: false,
      
      /**
       * Set up the attributes to be used when rendering the template.
       * 
       * @instance
       */
      postMixInProperties: function alfresco_renderers_Property__postMixInProperties() {
         if (this.renderAsLink)
         {
            var linkDetails = this.generateFileFolderLink();
            var itemLinkHref = linkDetails.itemLinkHref + linkDetails.itemLinkRelative;
            this.anchorOpen = "<a href='" + itemLinkHref + "'>"
            this.anchorClose = "</a>"
         }
         
         if (ObjectTypeUtils.isString(this.propertyToRender) && 
             ObjectTypeUtils.isObject(this.currentItem) && 
             lang.exists(this.propertyToRender, this.currentItem))
         {
            this.renderPropertyNotFound = false;
            this.renderedValue = this.getRenderedProperty(lang.getObject(this.propertyToRender, false, this.currentItem));
         }
         else
         {
            this.alfLog("log", "Property does not exist:", this);
         }

         this.renderedValueClass = this.renderedValueClass + " " + this.renderSize;
         
         // If the renderedValue is not set then display a warning message if requested...
         if (this.renderedValue == null && this.warnIfNotAvailable)
         {
            // Get appropriate message
            // Check message based on propertyToRender otherwise default to sensible alternative
            var warningKey = this.warnIfNoteAvailableMessage,
                warningMessage = "";
            if (warningKey == null)
            {
               warningKey = "no." + this.propertyToRender + ".message";
               warningMessage = this.message(warningKey);
               if (warning == warningKey)
               {
                  warningMessage = this.message("no.property.message", {0:this.propertyToRender});
               }
            }
            else
            {
               warningMessage = this.message(warningKey);
            }
            this.renderedValue = warningMessage;
            this.renderedValueClass += " faded";
         }
         else if (this.renderedValue == "" && !this.warnIfNotAvailable)
         {
            // Reset the prefix and suffix if there's no data to display
            this.requestedValuePrefix = this.renderedValuePrefix;
            this.requestedValueSuffix = this.renderedValueSuffix;
            this.renderedValuePrefix = "";
            this.renderedValueSuffix = "";
         }
      },
      
      /**
       * Determines whether or not the property should only be displayed when the item is hovered over.
       * 
       * @instance
       */
      postCreate: function alfresco_renderers_Property__postCreate() {
         if (this.onlyShowOnHover == true)
         {
            domClass.add(this.domNode, "share-hidden hover-only")
         }
         else
         {
            // No action
         }
      },
      
      /**
       * This currently wraps the Alfresco.util.formatDate function. 
       * 
       * @instance
       */
      renderDate: function(date, format) {
         return Alfresco.util.formatDate(Alfresco.util.fromISO8601(date), format);
      },
      
      /**
       * @instance
       * @param {string} property The name of the property to render
       */
      getRenderedProperty: function alfresco_renderers_Property__getRenderedProperty(property) {
         // html = Alfresco.util.encodeHTML
         // $date = function $date(date, format) { return Alfresco.util.formatDate(Alfresco.util.fromISO8601(date), format); },
         
         var value = "";
         if (property == null)
         {
            // No action required if a property isn't supplied
         }
         else if (ObjectTypeUtils.isString(property))
         {
            value =  this.encodeHTML(property);
         }
         else if (ObjectTypeUtils.isArray(property))
         {
            value =  property.length;
         }
         else if (YAHOO.lang.isBoolean(property))
         {
            value =  property;
         }
         else if (YAHOO.lang.isNumber(property))
         {
            value =  property;
         }
         else if (ObjectTypeUtils.isObject(property))
         {
            if (property.hasOwnProperty("iso8601"))
            {
               value =  this.renderDate(property.iso8601);
            }
            else if (property.hasOwnProperty("userName") && property.hasOwnProperty("displayName"))
            {
               value =  Alfresco.util.userProfileLink(property.userName, property.displayName);
            }
            else if (property.hasOwnProperty("displayName"))
            {
               value =  this.encodeHTML(property.displayName || "");
            }
            else if (property.hasOwnProperty("title"))
            {
               value =  this.encodeHTML(property.title || "");
            }
            else if (property.hasOwnProperty("name"))
            {
               value =  this.encodeHTML(property.name || "");
            }
         }
         return value;
      }
   });
});