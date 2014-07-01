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
 * @module alfresco/dashlets/Dashlet
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @mixes alfresco/core/CoreWidgetProcessing*
 * @author Erik Winlöf
 */
define(["dojo/_base/declare",
   "dijit/_WidgetBase",
   "dijit/_TemplatedMixin",
   "dojo/text!./templates/Dashlet.html",
   "alfresco/core/Core",
   "alfresco/core/CoreWidgetProcessing",
   "dojo/_base/lang",
   "dojo/_base/array",
   "dojo/dom-construct",
   "dojo/dom-class",
   "dijit/registry"],
      function(declare, _WidgetBase, _TemplatedMixin, template,
               AlfCore, CoreWidgetProcessing, lang, array, domConstruct, domClass, registry) {

         return declare([_WidgetBase, _TemplatedMixin, AlfCore, CoreWidgetProcessing], {

            baseClass: "alfresco-dashlets-Dashlet",

            i18nScope: "alfresco.dashlets.Dashlet",

            /**
             * An array of the i18n files to use with this widget.
             *
             * @instance
             * @type {object[]}
             * @default [{i18nFile: "./i18n/Dashlet.properties"}]
             */
            i18nRequirements: [{i18nFile: "./i18n/Dashlet.properties"}],

            /**
             * An array of the CSS files to use with this widget.
             *
             * @instance cssRequirements {Array}
             * @type {object[]}
             * @default [{cssFile:"./css/Dashlet.css"}]
             */
            cssRequirements: [{cssFile:"./css/Dashlet.css"}],

            /**
             * The HTML template to use for the widget.
             * @instance
             * @type {String}
             */
            templateString: template,

            widgetsForTitleBarActions: null,
            widgetsForToolbar: null,
            widgetsForToolbar2: null,
            widgetsForBody: null,

            titleBarActionsNode: null,
            toolbarNode: null,
            toolbar2Node: null,
            bodyNode: null,

            /**
             * Implements the widget life-cycle method to add drag-and-drop upload capabilities to the root DOM node.
             * This allows files to be dragged and dropped from the operating system directly into the browser
             * and uploaded to the location represented by the document list.
             *
             * @instance
             */
            postCreate: function alfresco_dashlets_Dashlet__postCreate() {
               this.processContainer(this.widgetsForTitleBarActions, this.titleBarActionsNode);
               this.processContainer(this.widgetsForToolbar, this.toolbarNode);
               this.processContainer(this.widgetsForToolbar2, this.toolbar2Node);
               this.processContainer(this.widgetsForBody, this.bodyNode);
            },

            processContainer: function alfresco_dashlets_Dashlet__processContainer(widgets, container) {
               // this.inherited(arguments);
               if (widgets != null && widgets.length > 0)
               {
                  this.processWidgets(widgets, container);
                  domClass.add(container, 'alfresco-dashlets-Dashlet--widgets');
               }
               else
               {
                  domClass.remove(container, 'alfresco-dashlets-Dashlet--widgets');
               }
            },

            postMixInProperties: function alfresco_dashlets_Dashlet__postMixInProperties() {
               // construct our I18N labels ready for template
               this.label = {};
               array.forEach(["title"], lang.hitch(this, function(key) {
                  this.label[key] = this.message(key);
               }));
            }

         });
      });