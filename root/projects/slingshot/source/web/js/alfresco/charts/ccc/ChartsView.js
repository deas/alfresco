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
 * A base class for charts
 *
 * @module alfresco/charts/css/ChartsView
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @mixes alfresco/core/CoreWidgetProcessing
 * @author Erik Winlöf
 */
define(["dojo/_base/declare",
   "dijit/_WidgetBase",
   "dijit/_TemplatedMixin",
   "dojo/text!./templates/ChartsView.html",
   "./Chart",
   "alfresco/core/Core",
   "alfresco/core/CoreWidgetProcessing",
   "dojo/_base/lang",
   "dojo/_base/array",
   "dojo/dom-construct",
   "dojo/dom-class"],
      function(declare, _WidgetBase, _TemplatedMixin, template, Chart,
               AlfCore, CoreWidgetProcessing, lang, array, domConstruct, domClass) {

         return declare([_WidgetBase, _TemplatedMixin, AlfCore, CoreWidgetProcessing], {

            baseClass: "alfresco-charts-ccc-ChartsView",

            /**
             * The HTML template to use for the widget.
             * @instance
             * @type {String}
             */
            templateString: template,

            chartsNode: null,

            chart: null,
            chartMap: null,

            subscriptionTopic: null,

            dataRequestTopic: null,
            dataRequestPayload: {},
            _currentDataRequestPayload: {},

            chartSelectionTopic: null,
            _currentlySelectedChart: null,

            postMixInProperties: function()
            {
               if (this.subscriptionTopic)
               {
                  this.alfSubscribe(this.subscriptionTopic, lang.hitch(this, this.onSubscriptionTopic));
               }
               if (this.dataRequestTopic)
               {
                  this.alfSubscribe(this.dataRequestTopic + "_SUCCESS", lang.hitch(this, this.onDataRequestTopicSuccess));
                  this.alfSubscribe(this.dataRequestTopic + "_FAILURE", lang.hitch(this, this.onDataRequestTopicFailure));
               }
            },

            /**
             * Iterates over the widgets processed and calls the [registerChart]{@link module:alfresco/charts/ccc/ChartsView#registerChart}
             * function with each one.
             *
             * @instance
             * @param {object[]} The created widgets
             */
            allWidgetsProcessed: function(widgets) {
               array.forEach(widgets, lang.hitch(this, this.registerChart));

               if (this.chartSelectionTopic) {
                  this.alfPublish(this.chartSelectionTopic, {
                     value: this._currentlySelectedChart
                  });
               }

               this._currentDataRequestPayload = lang.mixin({}, this.dataRequestPayload);
               this.requestData();
            },

            postCreate: function()
            {
               this.chartMap = {};

               if (this.widgets) {
                  this.processWidgets(this.widgets);
               }
            },

            requestData: function()
            {
               this._currentDataRequestPayload.alfResponseTopic = this.dataRequestTopic;
               this.alfPublish(this.dataRequestTopic, this._currentDataRequestPayload);
            },

            onDataRequestTopic: function(data, dataDescriptor)
            {
               var chart = this.chartMap[this._currentlySelectedChart];
               if (chart != null) {
                  chart.setData(data, dataDescriptor);
                  this.showChart(chart);
               }
            },

            onDataRequestTopicSuccess: function(payload){
               this.onDataRequestTopic(payload.response.data, payload.response.dataDescriptor);
            },

            onDataRequestTopicFailure: function(){
               this.onDataRequestTopic({}, {});
            },

            onSubscriptionTopic: function(payload)
            {
               lang.mixin(this._currentDataRequestPayload, payload);
               this.requestData();
            },

            registerChart: function (chart, index) {
               if (chart instanceof Chart)
               {
                  this.alfLog("log", "Registering Chart", chart);

                  var chartName = index;

                  // Check if this is the initially requested chart...
                  if (chartName == this.chart || (!this._currentlySelectedChart && !this.chart))
                  {
                     this._currentlySelectedChart = chartName;
                  }

                  // Step 2: Add the chart to the map of known charts...
                  this.chartMap[chartName] = chart;
               }
               else
               {
                  this.alfLog("warn", "The following widget was provided as a chart, but it does not inherit from 'alfresco/charts/ccc/Chart'", chart);
               }
            },

            showChart: function(chart){
               this.hideChildren(this.domNode);
               if (this.chartsNode.children.length > 0)
               {
                  this.chartsNode.removeChild(this.chartsNode.children[0]);
               }

               // Add the new chart...
               domConstruct.place(chart.domNode, this.chartsNode);
               domClass.remove(this.chartsNode, "share-hidden");
            },

            /**
             * Hides all the children of the supplied DOM node by applying the "share-hidden" CSS class to them.
             *
             * @instance
             * @param {Element} targetNode The DOM node to hide the children of.
             */
            hideChildren: function(targetNode) {
               array.forEach(targetNode.children, function(node) {
                  domClass.add(node, "share-hidden");
               });
            }

         });
      });