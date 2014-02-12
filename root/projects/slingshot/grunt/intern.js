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
 * Grunt tasks that use intern to run some tests
 */

module.exports = function (grunt, alf) {

   // Load the Intern task - it isn't "grunt-" prefixed so isn't loaded automatically.
   // @see: https://github.com/theintern/intern/wiki/Using-Intern-with-Grunt#task-options
   grunt.loadNpmTasks('intern');

   // Register a test task that uses Intern
   grunt.registerTask('test', [ 
      'csslint',
      'jshint', // TODO: Make this friendly.
      'intern:dev' // Run all the intern tests
   ]);

   // Register a test task that uses Intern_local
   grunt.registerTask('test_local', [ 
      'csslint',
      'jshint', // TODO: Make this friendly.
      'intern:local' // Run all the intern tests on sauce labs
   ]);

   // Register a test task that uses Intern_sl
   grunt.registerTask('test_sl', [ 
      'intern:sl' // Run all the intern tests on sauce labs
   ]);

   // Return the config. This gets pushed into the grunt.init.config method in Gruntfile.
   return {
      // Use Intern plugin for tests.
      // @see: https://github.com/theintern/intern/wiki/Using-Intern-with-Grunt
      intern: {
         dev: {
            options: {
               runType: 'runner',
               config: 'tests/intern'
            }
         },
         local: {
            options: {
               runType: 'runner',
               config: 'tests/intern_local'
            }
         },
         sl: {
             options: {
                runType: 'runner',
                config: 'tests/intern_sl'
             }
          }
      }
   }
}