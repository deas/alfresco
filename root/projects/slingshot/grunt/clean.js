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

'use strict';

/**
 * Grunt task config for the clean plugin.
 * 
 */

module.exports = function (grunt, alf) {
   return {
      // Clean tasks
      clean: {
         // Used to delete json files in the coverage reports directory
         coverageReports: [
            "code-coverage-reports/*.json"
         ],
         // Used to delete json files in the coverage reports temp directory
         coverageReportsTemp: [
            "code-coverage-reports/temp"
         ],
         // Used to delete the instrumented code directory
         instrumentedCode: [
            "source/web/js/alfrescoInst"
         ]
      }
   }
}