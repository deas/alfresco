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
 * A class that listen for resize events and fires a topic with a calculated value.
 *
 * @module alfresco/charts/ccc/ResizeListener
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @mixes module:alfresco/core/CoreWidgetProcessing
 * @abstract
 *
 * @author Erik Winlöf
 */
define(["dojo/_base/declare",
   "dijit/_WidgetBase",
   "dijit/_TemplatedMixin",
   "dojo/text!./templates/ResizeListener.html",
   "alfresco/core/Core",
   "alfresco/core/CoreWidgetProcessing",
   "alfresco/core/DomElementUtils",
   "dojo/_base/lang",
   "dojo/on",
   "dojo/dom-geometry",
   "dojo/dom-style"],
      function(declare, _WidgetBase, _TemplatedMixin, template,
               AlfCore, CoreWidgetProcessing, DomElementUtils, lang, dojoOn, domGeom, domStyle) {

         return declare([_WidgetBase, _TemplatedMixin, AlfCore, CoreWidgetProcessing, DomElementUtils], {

            /**
             * The topic to publish when the window has been resized
             *
             * @instance
             * @type {string}
             */
            resizeTopic: null,

            /**
             * The topic payload to publish when the window has been resized
             *
             * @instance
             * @type {string}
             */
            resizePayload: null,

            /**
             * The topic payload to publish when the window has been resized
             *
             * @instance
             * @type {string}
             */
            resizePayloadCalculation: null,

            /**
             * The HTML template to use for the widget.
             *
             * @instance
             * @type {string}
             */
            templateString: template,

            /**
             * The timeout interval
             *
             * @instance
             * @type {number}
             */
            timeoutInterval: 500,

            /**
             * Sets up topic subscriptions and makes sure the chart is resized when the window is resized.
             *
             * @instance
             */
            postCreate: function alfresco_charts_ccc_Chart__postCreate() {
               var me = this;
               var lastResizeEvent;
               var skippedResizeEvents = 0;
               var timeout = this.timeoutInterval;

               function doPublishResizeTopic() {
                  me._publishResizeTopic();
               }

               function onResize() {
                  if (lastResizeEvent) {
                     clearTimeout(lastResizeEvent);
                     skippedResizeEvents++;
                     if (skippedResizeEvents > 1) {
                        skippedResizeEvents = 0;
                        me._publishResizeTopic();
                        return;
                     }
                  }
                  lastResizeEvent = window.setTimeout(doPublishResizeTopic, timeout);
               }
               dojoOn(window, "resize", onResize);

               me._publishResizeTopic();
            },

            /**
             *
             */
            _publishResizeTopic: function(){
               if (this.resizeTopic) {
                  var payload = this.resizePayload;
                  var calculationConfig = this.resizePayloadCalculation;
                  if (calculationConfig) {
                     payload = calculationConfig.value;
                     if (calculationConfig.calculate) {
                        var c, tmp;
                        for (var i = 0, il = calculationConfig.calculate.length; i < il; i++) {
                           c = calculationConfig.calculate[i];

                           // Find element
                           tmp = document.querySelector(c.selector);

                           // Function
                           if (c["function"] == "rect") {
                              tmp = tmp.getBoundingClientRect();
                           }

                           // Property
                           if (c.property) {
                              tmp = tmp[c.property];
                           }

                           // Operator
                           if (c.operator == "+") {
                              payload += tmp;
                           }
                           else if (c.operator == "-") {
                              payload -= tmp;
                           }
                        }
                     }
                  }
                  this.alfPublish(this.resizeTopic, payload);
               }
            }

         });
      });