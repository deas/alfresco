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
 * SiteContentReport
 *
 * @module alfresco/reports/SiteContentReport
 * @extends alfresco/reports/Report
 * @author Erik Winlöf
 */
define(["dojo/_base/declare",
   "alfresco/core/Core",
   "alfresco/core/I18nUtils",
   "alfresco/reports/Report"],
      function(declare, AlfCore, I18nUtils, Report) {

         return declare([Report], {

            i18nScope: "alfresco.reports.SiteContentReport",

            /**
             * An array of the i18n files to use with this widget.
             *
             * @instance
             * @type {object[]}
             * @default [{i18nFile: "./i18n/SiteContentReport.properties"}]
             */
            i18nRequirements: [{i18nFile: "./i18n/SiteContentReport.properties"}],

            /**
             * The widgets to be processed to generate each item in the rendered view.
             *
             * @instance
             * @type {object[]}
             * @default null
             */
            widgets: [
               {
                  name: "alfresco/charts/ccc/PieChart",
                  config: {
                     dataTopic: "ALF_RETRIEVE_SITE_CONTENT_REPORT",
                     dataTopicPayload: {
                        site: Alfresco.constants.SITE // todo replace with $$SITE$$ once supported
                     }
                  }
               }
            ]

         });
      });