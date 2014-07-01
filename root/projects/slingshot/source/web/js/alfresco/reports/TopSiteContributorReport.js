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
 * TopSiteContributorReport
 *
 * @module alfresco/reports/TopSiteContributorReport
 * @extends alfresco/reports/Report
 * @author Erik Winlöf
 */
define(["dojo/_base/declare",
   "alfresco/core/Core",
   "alfresco/core/I18nUtils",
   "alfresco/reports/Report",
   "dojo/_base/lang",
   "dojo/date",
   "dojo/date/stamp"],
      function(declare, AlfCore, I18nUtils, Report, lang, date, stamp) {

         var startDate = date.add(new Date(), "month", -3);
         startDate = stamp.toISOString(startDate, { selector: "date" });
         var endDate = new Date();
         endDate = stamp.toISOString(endDate, { selector: "date" });
         var i18nScope = "alfresco.reports.TopSiteContributorReport";
         return declare([Report], {

            i18nScope: "alfresco.reports.TopSiteContributorReport",

            /**
             * An array of the i18n files to use with this widget.
             *
             * @instance
             * @type {object[]}
             * @default [{i18nFile: "./i18n/TopSiteContributorReport.properties"}]
             */
            i18nRequirements: [{i18nFile: "./i18n/TopSiteContributorReport.properties"}],

            postMixInProperties: function alfresco_reports_TopSiteContributorReport__postMixInProperties() {
               this.inherited(arguments);
               this.alfSubscribe("REPORT_ITEM_CLICKED", lang.hitch(this, this.onReportItemClick));
            },

            onReportItemClick: function(value){
               alert('Clicked: ' + value);
            },

            /**
             * The widgets to be processed to generate each item in the rendered view.
             *
             * @instance
             * @type {object[]}
             * @default null
             */
            widgets: [
               {
                  name: "alfresco/forms/Form",
                  config: {
                     okButtonPublishTopic: "SHOW_CONTRIBUTORS_BY_DATE",
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
               {
                  name: "alfresco/charts/ccc/ChartsView",
                  config:
                  {
                     dataRequestTopic: "ALF_RETRIEVE_TOP_SITE_CONTRIBUTOR_REPORT",
                     dataRequestPayload: {
                        site: "$$SITE$$",
                        startDate: startDate,
                        endDate: endDate
                     },
                     subscriptionTopic: "SHOW_CONTRIBUTORS_BY_DATE",
                     widgets: [
                        {
                           name: "alfresco/charts/ccc/PieChart",
                           config: {
                              selectable: true,
                              hoverable:  true,
                              clickTopic: "REPORT_ITEM_CLICKED"
                           }
                        }
                     ]
                  }
               }
            ]
         });
      });