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
 * Grunt tasks to generate coverage reports 
 * 
 * Defines aliases & helper functions
 */

module.exports = function (grunt, alf) {

   // Generate a coverage report using the local machine
   grunt.registerTask('coverage-report', 'A task for collecting code coverage reports', function() {
      grunt.task.run('shell:stopTestApp');
      grunt.option('force', true);
      grunt.task.run('generate-require-everything');
      grunt.task.run('instrument-code');
      grunt.task.run('copy:instrumentedJs');
      grunt.task.run('hideExistingCoverageReports');
      grunt.task.run('start-node-coverage-server');
      grunt.task.run('startUnitTestApp');
      grunt.task.run('waitServer');
      grunt.task.run('intern:local_coverage');
      grunt.task.run('merge-reports');
      grunt.task.run('clean-reports');
      grunt.task.run('showExistingCoverageReports');
      grunt.task.run('copy:uninstrumentedJs');
      // grunt.task.run('http:clearDependencyCaches');
      grunt.task.run('clean:instrumentedCode');
      grunt.task.run('shell:stopTestApp');
   });

   // Generate a coverage report using a vagrant initialised VM
   // The VM is run up by the task
   grunt.registerTask('vm-coverage-report', 'A task for collecting code coverage reports using a vagrant VM', function() {
      grunt.task.run('shell:stopTestApp');
      grunt.option('force', true);
      grunt.task.run('generate-require-everything');
      grunt.task.run('instrument-code');
      grunt.task.run('copy:instrumentedJs');
      grunt.task.run('hideExistingCoverageReports');
      grunt.task.run('start-node-coverage-server');
      grunt.task.run('startUnitTestApp');
      grunt.task.run('waitServer');
      grunt.task.run('intern:dev_coverage');
      grunt.task.run('merge-reports');
      grunt.task.run('clean-reports');
      grunt.task.run('showExistingCoverageReports');
      grunt.task.run('copy:uninstrumentedJs');
      // grunt.task.run('http:clearDependencyCaches');
      grunt.task.run('clean:instrumentedCode');
      grunt.task.run('shell:stopTestApp');
   });

   /* Register additional helper functions 
    * These are used in the above tasks.
    */

   // Generate the RequireEverything widget - a widget listing task, a file write and then a clean of the listing file
   grunt.registerTask('generate-require-everything', 'A task for listing widgets and then generating the RequireEverything widget from the list', function() {
      grunt.task.run('folder_list:alf_widgets');
      grunt.task.run('write-require-everything');
      grunt.task.run('clean:requireEverythingWidgetsList');
   });

   // Generate the RequireEverything widget
   // Should always be run after a 'folder_list:alf_widgets' task
   grunt.registerTask('write-require-everything', 'A task for writing the RequireEverything widget file', function() {

      var template = grunt.file.read(alf.testResourcesDir + "/" + alf.requireEverythingTemplate),
          widgetFiles = grunt.file.readJSON(alf.testResourcesDir + "/" + alf.alfWidgetsList),
          fileList = [];

      // Iterate over widgetFiles, removing unwanted files and topping and tailing the file paths accordingly
      for (var i=0; i<widgetFiles.length; i++)
      {
         var filePath = ((widgetFiles[i].location).replace(alf.requireEverythingWidgetsPrefix, "")).replace(alf.requireEverythingWidgetsSuffix, "");
         if(alf.requireEverythingExclusions.indexOf(filePath) === -1)
         {
            fileList.push("\n\t\"" + filePath + "\"");
         }
      }

      grunt.file.write(alf.requireEverythingWidget, template.replace("{0}", fileList.toString()));
      grunt.log.writeln("Finished writing RequireEverything widget");
   });

   // Merge individual coverage reports in the node coverage server
   // TODO: Give the report a sensible name (e.g. with a timestamp)
   grunt.registerTask('merge-reports', 'A task for merging code coverage reports', function() {
      var done = this.async();
      var mergeArgs = [
         'node_modules/node-coverage/merge.js',
         '-o',
         'code-coverage-reports/Coverage_Report_' + Date.now() + '.json',
      ];

      var reports = grunt.file.expand(['code-coverage-reports/*.json']);
      for (var i=0; i<reports.length; i++)
      {
         mergeArgs.push(reports[i]);
      }

      grunt.util.spawn({
         cmd: 'node',
         args: mergeArgs,
         opts: {
            stdio: 'inherit'
         }
      }, function(error, result, code) {
         grunt.log.writeln("Finished merging reports...");
         done();
      });
   });

   // Remove individual reports from the node coverage server
   grunt.registerTask('clean-reports', 'A task for cleaning out individual code coverage reports', function() {
      var reports = grunt.file.expand(['code-coverage-reports/report_*.json']);
      for (var i=0; i<reports.length; i++)
      {
         grunt.file.delete(reports[i]);
      }
   });

   // Delete existing instrumented code and run the coverage module instrumentation process to replace it
   grunt.registerTask('instrument-code', 'Use the node-coverage module to instrument the widgets with coverage collection data', function() {
      var done = this.async();
      if (grunt.file.isDir(alf.jsInstFiles))
      {
         grunt.file.delete(alf.jsInstFiles, {
            force: true
         })
      }

      var nodeCoverage = grunt.util.spawn({
         cmd: 'node',
         args: ['node_modules/node-coverage/instrument.js',
                'source/web/js/alfresco',
                'source/web/js/alfrescoInst',
                '--function']
      }, function(error, result, code) {
         grunt.log.writeln("Finished instrumenting files...");
         done();
      });
   });

   // Start the node coverage server
   grunt.registerTask('start-node-coverage-server', 'A task to start the node-coverage server (it will stay running)', function() {
      if (!grunt.file.isDir(alf.coverageDirectory))
      {
         grunt.file.mkdir(alf.coverageDirectory);
      }
      
      var tcpPortUsed = require('tcp-port-used');
      tcpPortUsed.check(8082, 'localhost')
      .then(function(inUse) {
         if(!inUse)
         {
            var nodeCoverage = grunt.util.spawn({
               cmd: 'node',
               args: ['node_modules/node-coverage/server.js',
                      '--port',
                      '8082',
                      '--report-dir',
                      alf.coverageDirectory],
               opts: {
                  detached: 'true',
                  stdio : 'inherit'
               }
            }, function(error, result, code) {
               grunt.log.writeln("Finished spawning node-coverage server...");
            });
         }
         else
         {
            grunt.log.writeln("Node coverage server appears to be running already...");
         }
      }, function(err) {
         console.error('Unknown if coverage server is already running:', err.message);
      });
   });
}