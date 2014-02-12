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
 * @module alfresco/accessibility/AccessibilityMenu
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @author Richard Smith
 */
 define(["dojo/_base/declare",
         "dijit/_WidgetBase", 
         "dijit/_TemplatedMixin",
         "dojo/text!./templates/AccessibilityMenu.html",
         "dojo/_base/lang",
         "alfresco/core/Core",
         "dojo/dom",
         "dojo/dom-construct"], 
         function(declare, _WidgetBase, _TemplatedMixin, template, lang, AlfCore, dom, domConstruct) {
   
   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {

      /**
       * An array of the i18n files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{i18nFile: "./i18n/AccessibilityMenu.properties"}]
       */
      i18nRequirements: [{i18nFile: "./i18n/AccessibilityMenu.properties"}],

      /**
       * An array of the CSS files to use with this widget.
       * 
       * @instance
       * @type {object[]}
       * @default [{cssFile:"./css/AccessibilityMenu.css"}]
       */
      cssRequirements: [{cssFile:"./css/AccessibilityMenu.css"}],

      /**
       * The HTML template to use for the widget.
       * @instance
       * @type {String}
       */
      templateString: template,

      /**
       * The urls, access keys and message identifiers used to render the accessibility links
       * @instance
       * @type {object[]}
       */
      menu: [],

      /**
       * The domid, targetid and positioning used to render the accessibility link targets
       * @instance
       * @type {object[]}
       */
      targets: [],

      /**
       * @instance
       */
      postCreate: function alfresco_accessibility_AccessibilityMenu__postCreate() {

         // Inject access key header
         domConstruct.create("p", {
            innerHTML: this.message("access.key.links.message")
         }, this.accessKeys, "first");

         // Create unordered list for access key items - injected at the end of the accessKeys object
         var ul = domConstruct.create("ul", null, this.accessKeys, "last");

         // Then iterate menu items creating li and a tags accordingly
         for (var i=0, item; item = this.menu[i]; i++)
         {
            this.writeMenuItem(ul, item);
         }

         // Subscribe function generateTargets to run when ALF_WIDGETS_READY publishes
         this.alfSubscribe("ALF_WIDGETS_READY", lang.hitch(this, "generateTargets"));

      },

      /**
       * This function writes a list item for the accessibility menu.
       * @instance
       * @param {object} list The list into which menu items are added
       * @param {object} item The object containing details for the menu item
       */
      writeMenuItem: function alfresco_accessibility_AccessibilityMenu__writeMenuItem(list, item) {

         // If the list container exists
         if(list)
         {
            // Create the list item
            var li = domConstruct.create("li", null, list, "last");

            // Insert an a tag into the list item
            domConstruct.create("a", {
               href: item.url,
               accessKey: item.key,
               innerHTML: this.message(item.msg)
            }, li);

            this.alfLog("log", "Created accessibility menu item '" + item.url + "'", this);
         }
         else
         {
            this.alfLog("error", "List specified as container for 'writeMenuItem' does not exist", this);
         }

      },

      /**
      * This function generates HTML anchors from this.targets. It is called via a
      * subscription to the ALF_WIDGETS_READY channel to make sure that the destination DOM 
      * elements have already been created.
      * @instance
      */
      generateTargets: function alfresco_accessibility_AccessibilityMenu__generateTargets() {

         // Iterate target items creating anchors accordingly
         for (var i=0, item; item = this.targets[i]; i++)
         {
            if(dom.byId(item.domid))
            {
               var pos = item.after ? "after" : "before";
               domConstruct.create("a", {
                  id: item.targetid,
                  innerHTML: " "
               }, item.domid, pos);
               
               this.alfLog("log", "Created accessibility anchor '" + item.targetid + "'", this);
            }
            else
            {
               this.alfLog("error", "Dom element '" + item.domid + "', specified as location for accessibility anchor, does not exist", this);
            }
         }

      }

   });
});