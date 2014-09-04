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
 * SiteUsageReport displays a chart showing the most active sites with a breakdown of the number
 * of activities by type.
 *
 * @module alfresco/reports/SiteUsageReport
 * @extends module:alfresco/reports/Report
 * @author Will Abson
 */
define(["dojo/_base/declare",
   "alfresco/core/Core",
   "alfresco/core/I18nUtils",
   "alfresco/reports/Report",
   "alfresco/services/_NavigationServiceTopicMixin",
   "dojo/_base/lang",
   "dojo/date",
   "dojo/date/stamp"],
      function(declare, AlfCore, I18nUtils, Report, _NavigationServiceTopicMixin, lang, date, stamp) {

         // Lets default the report to show data from the last month
         var startDate = stamp.toISOString(date.add(new Date(), "month", -1), { selector: "date" });
         var endDate = stamp.toISOString(new Date(), { selector: "date" });

         var i18nScope = "alfresco.reports.SiteUsageReport";

         return declare([Report, _NavigationServiceTopicMixin], {

            /**
             * An array of the i18n files to use with this widget.
             *
             * @instance
             * @type {object[]}
             * @default [{i18nFile: "./i18n/SiteUsageReport.properties"}]
             */
            i18nRequirements: [{i18nFile: "./i18n/SiteUsageReport.properties"}],

            /**
             * An array of the CSS files to use with this widget.
             *
             * @instance cssRequirements {Array}
             * @type {object[]}
             * @default [{cssFile:"./css/SiteUsageReport.css"}]
             */
            cssRequirements: [{cssFile:"./css/SiteUsageReport.css"}],

            /**
             * The CSS class (or a space separated list of classes) to include in the DOM node.
             *
             * @instance
             * @type {string}
             * @default "alfresco-reports-SiteUsageReport"
             */
            baseClass: "alfresco-reports-SiteUsageReport",

            /**
             * Makes sure we listen to when a user is clicked in the chart
             *
             * @instance
             */
            postMixInProperties: function alfresco_reports_SiteUsageReport__postMixInProperties() {
               this.inherited(arguments);
               //this.alfSubscribe("REPORT_ITEM_CLICKED", lang.hitch(this, this.onReportItemClick));
            },

            /**
             * Called when a user is clicked in the chart and will navigate browser to the clicked user's profile.
             *
             * @instance
             * @param value The name of the clicked user
             */
            onReportItemClick: function(value) {
               this.alfPublish(this.navigateToPageTopic, {
                  type: this.contextRelativePath,
                  url: "page/user/" + encodeURIComponent(value) + "/profile"
               }, true);
            },

            /**
             * The widgets to be processed to generate each item in the rendered view.
             *
             * @instance
             * @type {object[]}
             * @default null
             */
            widgets: [
               /*
               {
                  name: "alfresco/forms/Form",
                  config: {
                     displayButtons: false,
                     validFormValuesPublishTopic: "SHOW_SITE_USAGE_BY_DATE",
                     validFormValuesPublishOnInit: false,
                     widgets: [
                        {
                           name: "alfresco/forms/controls/DojoDateTextBox",
                           config: {
                              name: "startDate",
                              value: startDate,
                              label: I18nUtils.msg(i18nScope, "startDate")
                           }
                        },
                        {
                           name: "alfresco/forms/controls/DojoDateTextBox",
                           config: {
                              name: "endDate",
                              value: endDate,
                              label: I18nUtils.msg(i18nScope, "endDate")
                           }
                        }
                     ]
                  }
               },
               */
               {
                  name: "alfresco/charts/ccc/ChartsView",
                  config:
                  {
                     dataRequestTopic: "ALF_RETRIEVE_SITE_USAGE_REPORT",
                     dataRequestPayload: {
                        site: Alfresco.constants.SITE,
                        startDate: startDate,
                        endDate: endDate
                     },
                     subscriptionTopic: "SHOW_SITE_USAGE_BY_DATE",
                     widgets: [
                        {
                           name: "alfresco/charts/ccc/BarChart",
                           config: {
                              readers: [
                                 { names: 'category', indexes: 0 },
                                 { names: 'value', indexes: 2 }
                              ],
                              selectable: true,
                              hoverable:  true,
                              orientation: "horizontal"
                           }
                        }
                     ]
                  }
               }
            ]
         });
      });