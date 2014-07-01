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
 * @module alfresco/charts/css/Chart
 * @extends dijit/_WidgetBase
 * @mixes dijit/_TemplatedMixin
 * @mixes module:alfresco/core/Core
 * @mixes alfresco/core/CoreWidgetProcessing
 * @author Erik Winlöf
 */
define(["dojo/_base/declare",
   "dijit/_WidgetBase",
   "dijit/_TemplatedMixin",
   "dojo/text!./templates/Chart.html",
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

            baseClass: "alfresco-charts-ccc-Chart",

            pvcChartType: 'Chart',

            chartNode: null,

            dataTopic: null,

            title: null,
            titlePosition: "bottom",

            width: null,
            height: 400,

            legend: false,
            legendPosition: 'left',
            legendAlign: 'middle',

            selectable: false,
            hoverable: false,

            tooltip: {
               enabled: true
            },

            readers: null,
            dimensions: null,

            _currentData: null,
            _currentDataDescriptor: null,

            /**
             * Declare the dependencies on "legacy" JS files that this is wrapping.
             *
             * @instance
             */
            nonAmdDependencies: [
               "/ctools/jquery.js",
               "/ctools/protovis.js",
               "/ctools/protovis-msie.js",
               "/ctools/jquery.tipsy.js",
               "/ctools/tipsy.js",
               "/ctools/def.js",
               "/ctools/pvc.js"
            ],

            /**
             * An array of the CSS files to use with this widget.
             *
             * @instance cssRequirements {Array}
             * @type {object[]}
             * @default [{cssFile:"./css/Chart.css"}]
             */
            cssRequirements: [
               {cssFile:"./css/Chart.css"},
               {cssFile:"/ctools/tipsy.css"}
            ],

            /**
             * The HTML template to use for the widget.
             * @instance
             * @type {String}
             */
            templateString: template,

            /**
             * Subscribe the document list topics.
             *
             * @instance
             */
            postMixInProperties: function alfresco_charts_ccc_Chart__postMixInProperties() {
               if (this.dataTopic) {
                  // Subscribe to the topics that will be published on by the ReportService when retrieving data
                  // that this widget requests...
                  this.alfSubscribe(this.dataTopic + "_SUCCESS", lang.hitch(this, this.onDataLoadSuccess));
                  this.alfSubscribe(this.dataTopic + "_FAILURE", lang.hitch(this, this.onDataLoadFailure));
               }
            },

            createChartConfig: function(){
               var config = {};

               // Common configurable properties
               config.canvas = this.chartNode;

               config.width = this.getWidth();
               config.height = this.height;

               config.title = this.title;
               config.titlePosition = this.titlePosition;

               config.legend = this.legend;
               config.legendPosition = this.legendPosition;
               config.legendAlign = this.legendAlign;

               config.selectable = this.selectable;
               config.hoverable = this.hoverable;

               if (this.readers) {
                  config.readers = this.readers;
               }
               if (this.dimensions) {
                  config.dimensions = this.dimensions;
               }

               if (this.clickTopic)
               {
                  config.clickable = true;
                  config.clickAction = lang.hitch(this, this.onItemClick);
               }

               config.tooltip = this.tooltip;

               var styles = this.resolveCssStyles(this.baseClass + "--color", [1,2,3,4,5,6,7,8], {
                  backgroundColor: ["rgba(0, 0, 0, 0)", "transparent"]
               });
               config.colors = styles.backgroundColor;
               /*
               //config.colors = ["#013564", "#004D71", "#01677E", "#007C86", "#298B8C", "#93A599"];
               //config.colors = ["#B53E1E", "#B24B1E", "#B5692B", "#B99340", "#B7AA4E", "#B8AF76"];
               var el = document.createElement("div");
               el.className = this.baseClass + "--color1";
               //el.innerHTML = "test";
               document.body.appendChild(el);
               var color = getComputedStyle(el)["backgroundColor"];

               config.colors = [color];
               */
               return config;
            },


            createChart: function alfresco_charts_ccc_Chart__createChart(){
               this.chart = new pvc[this.pvcChartType](this.createChartConfig());
            },

            onItemClick: function(scene){
               this.alfPublish(this.clickTopic, scene.atoms.category.rawValue);
            },

            /**
             *
             *
             * @instance
             */
            postCreate: function alfresco_charts_ccc_Chart__postCreate() {
               if (this.dataTopic) {
                  // Set a response topic that is scoped to this widget...
                  var dataTopicPayload = {};
                  dataTopicPayload.alfResponseTopic = this.pubSubScope + this.dataTopic;
                  this.alfPublish(this.dataTopic, dataTopicPayload);
               }

               var me = this;
               var lastResizeEvent;
               var skippedResizeEvents = 0;
               function doResize() {
                  // Avoid re-rendering until the chart has been rendered a first time
                  if (me.chart) {
                     console.log('render chart');
                     me.renderChart();
                  }
               }
               function onResize(){
                  console.log('on resize');
                  if (lastResizeEvent) {
                     clearTimeout(lastResizeEvent);
                     skippedResizeEvents++;
                     if (skippedResizeEvents > 1) {
                        skippedResizeEvents = 0;
                        me.renderChart();
                        return;
                     }
                  }
                  lastResizeEvent = window.setTimeout(doResize, 100);
               }
               dojoOn(window, "resize", onResize);
            },

            setData: function(data, dataDescriptor){
               this._currentData = data;
               this._currentDataDescriptor = dataDescriptor;
               this.renderChart();
            },

            renderChart: function(){
               if (this.getWidth()) {
                  this.performRenderChart();
                  return;
               }

               // This element has not been added to the dom yet and has therefor no width, wait until its set
               var me = this;
               var timeoutId;
               function callPerformRenderChartWhenReady(){
                  if (me.getWidth()) {
                     clearTimeout(timeoutId);
                     me.performRenderChart();
                  }
               }
               timeoutId = window.setInterval(callPerformRenderChartWhenReady, 100);
            },

            performRenderChart: function(){
               this.createChart();
               this.chart.setData(this._currentData, this._currentDataDescriptor);
               this.chart.render(true, true, false);
            },

            getWidth: function(){
               try {
                  var style = domStyle.getComputedStyle(this.domNode);
                  var s = domGeom.getContentBox(this.domNode, style);
                  var w = (s.w + "").split('.')[0];
                  w = w.split('px')[0];
                  w = parseInt(w);
                  return w;
               }
               catch(e) {
                  return null;
               }
            },

            onDataLoadSuccess: function(payload){
               this.setData(payload.response.data, payload.response.dataDescriptor);
            },

            onDataLoadFailure: function(payload){
               this.setData({}, {});
            }

         });
      });