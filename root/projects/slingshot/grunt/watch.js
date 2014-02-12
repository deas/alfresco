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
 * Grunt tasks that watch files and do stuff when they change
 */

module.exports = function (grunt, alf) {
   // Return the config. This gets pushed into the grunt.init.config method in Gruntfile.
   return {
      // Watch Commands (when the specified watch is active, task is run when files change.)
      // @see: https://github.com/gruntjs/grunt-contrib-watch
      watch: {
         // Triggers the jsdocs updates & runs the tests when the listed js files change.
         docsAndTests: {
            files: alf.jsFiles,
            tasks: [
               //'jshint' TODO: Make it friendly to our code...
               //'deployJS',
               'concurrent:docsAndTests'
            ]
         },
         // Triggers the jsdocs updates when the listed js files change.
       // DON'T ADD A SAUCE LABS WATCH HERE!
         jsdoc: {
            files: alf.jsFiles,
            tasks: ['shell:jsdoc']
         },
         intern: {
             files: alf.jsFiles,
             tasks: ['intern:dev']
         },
         intern_local: {
             files: alf.jsFiles,
             tasks: ['intern:local']
          },
         // CSS
         css: {
            files: alf.cssFiles,
            tasks: [
               'deployCSS'
               //'csslint'
            ]
         }
      }
   }
}