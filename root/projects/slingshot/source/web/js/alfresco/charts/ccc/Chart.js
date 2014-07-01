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
   "dojo/_base/lang",
   "dojo/_base/array",
   "dojo/dom-construct",
   "dojo/dom-class",
   "dijit/registry"],
      function(declare, _WidgetBase, _TemplatedMixin, template,
               AlfCore, CoreWidgetProcessing, lang, array, domConstruct, domClass, registry) {
         /*
          alert('$: ' + (typeof $) +
          ', pv: ' + pv +
          ', pv.have_SVG: ' + pv.have_SVG +
          ', jquery.tipsy: ' + (typeof $.fn.tipsy) +
          ', pv.Behavior.tipsy: ' + (typeof pv.Behavior.tipsy) +
          ', def: ' + def +
          ', pvc: ' + pvc);
          */

         return declare([_WidgetBase, _TemplatedMixin, AlfCore, CoreWidgetProcessing], {

            baseClass: "alfresco-charts-ccc-Chart",

            pvcChartType: 'Chart',

            chartsNode: null,

            dataTopic: null,

            title: null,
            titlePosition: "bottom",

            width: 600,
            height: 400,

            legend: false,
            legendPosition: 'left',
            legendAlign: 'middle',

            selectable: false,
            hoverable: false,

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
               config.canvas = this.chartsNode;

               config.width = this.width;
               config.height = this.height;

               config.title = this.title;
               config.titlePosition = this.titlePosition;

               config.legend = this.legend;
               config.legendPosition = this.legendPosition;
               config.legendAlign = this.legendAlign;

               config.selectable = this.selectable;
               config.hoverable = this.hoverable;

               if (this.clickTopic)
               {
                  config.clickable = true;
                  config.clickAction = lang.hitch(this, this.onItemClick);
               }

               config.tooltipEnabled = true; // deprecated
               //config.explodedSliceRadius = 15; // valid option?
               config.valuesVisible = true; // valid option?

               return config;
            },


            createChart: function alfresco_charts_ccc_Chart__createChart(){
               this.chart = new pvc[this.pvcChartType](this.createChartConfig());
            },

            onItemClick: function(scene){
               this.alfPublish(this.clickTopic, scene.atoms.category.rawValue);
            },

            /**
             * Implements the widget life-cycle method to add drag-and-drop upload capabilities to the root DOM node.
             * This allows files to be dragged and dropped from the operating system directly into the browser
             * and uploaded to the location represented by the document list.
             *
             * @instance
             */
            postCreate: function alfresco_charts_ccc_Chart__postCreate() {
               this.createChart();

               if (this.dataTopic) {
                  // Set a response topic that is scoped to this widget...
                  var dataTopicPayload = {};
                  dataTopicPayload.alfResponseTopic = this.pubSubScope + this.dataTopic;
                  this.alfPublish(this.dataTopic, dataTopicPayload);
               }
            },

            setData: function(data, dataDescriptor){
               this.chart.setData(data, dataDescriptor);
            },

            render: function(){
               this.chart.render();
            },

            onDataLoadSuccess: function(payload){
               this.setData(payload.response.data, payload.response.dataDescriptor);
               this.render();
            },

            onDataLoadFailure: function(payload){
               this.setData({}, {});
               this.render();
            }

         });
      });