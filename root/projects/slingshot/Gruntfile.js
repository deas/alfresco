(function () {
   'use strict';


   // Keep all paths in one place
   var alf = {
      cssFiles: 'source/web/**/*.css',
      jsdocFiles: 'source/web/js/alfresco',
      jsInstFiles: 'source/web/js/alfrescoInst',
      jsFiles: ['source/web/js/alfresco/**/*.js'],
      testFiles: ['tests/alfresco/**'],
      htmlFiles: ['source/web/js/alfresco/**/*.html'],
      xmlFiles: ['/**/*.xml'],
      testResourcesDir: 'source/test-resources',
      nodeBinDir: 'node_modules/.bin/',
      coverageDirectory: "code-coverage-reports",
      rootDir: '../../',
      codeDir: '../../../',
      docsDir: 'docs',
      alfWidgetsList: 'alfresco_widgets.json',
      requireEverythingTemplate: 'RequireEverything.template.js',
      requireEverythingWidget: 'tests/testApp/js/aikau/testing/RequireEverything.js',
      requireEverythingWidgetsPrefix: 'source/web/js/',
      requireEverythingWidgetsSuffix: '.js',
      requireEverythingExclusions: [
         'alfresco/forms/controls/AceEditor',
         'aikauTesting/mockservices/SiteMockXhr',
         "aikauTesting/mockservices/CreateContentMockXhr",
         "aikauTesting/mockservices/DocumentLibraryMockXhr",
         "aikauTesting/mockservices/HeaderMockXhr",
         "aikauTesting/mockservices/PaginationMockXhr",
         "aikauTesting/mockservices/PreviewMockXhr",
         "aikauTesting/mockservices/SearchMockXhr",
         "aikauTesting/mockservices/SearchScrollMockXhr",
         "aikauTesting/mockservices/SiteMockXhr",
         "aikauTesting/mockservices/UploadMockXhr",
         "aikauTesting/mockservices/UserMockXhrBad",
         "aikauTesting/mockservices/UserMockXhrGood"
      ],

      testModelFiles: ['tests/alfresco/**/*.json'],
      alfTestModels: 'alfresco_model_files.json'

   };

   module.exports = function(grunt) {

      // Load all grunt modules
      // @see: https://github.com/sindresorhus/load-grunt-tasks
      require('load-grunt-tasks')(grunt);
      grunt.loadNpmTasks('grunt-notify');
      grunt.loadNpmTasks('grunt-folder-list');

      var _ = require('lodash'), // Add the lodash util library
         extend = _.extend,
         config = {},
         path = './grunt/';

      /* Load the external config.
       * External config can call grunt methods, access the alf object and
       * can return an object that gets merged into the initConfig call below.
       * Note: that child objects aren't merged into each other, e.g.:
       * all http config should go into the http.js file.
       * TODO: Fix this limitation?
       */
      require('glob').sync('*', {cwd: path}).forEach(function(configFile) {
         config = extend(config, require(path + configFile)(grunt, alf));
      });

      grunt.initConfig(config);

      /* Task Aliases
       *
       * @see: https://w3.alfresco.com/confluence/display/ENG/Automated+process+scenarios+using+Grunt
       * (other aliases are defined in the imported files)
       */

      // By default we just test
      grunt.registerTask('default', [ 'test' ]);

      // Register JSDocs alias:
      grunt.registerTask('jsdoc', [
         'copy:customJsDocTemplate',
         'shell:jsdoc'
      ]);

      // TODO: Rationalise these once we've got a workflow sorted.
      //   Grunt Work Flow:
      //
      //      grunt d: start existing repo, start share (s -e), brings up vagrant & runs watch server.
      //      grunt cup: performs a clean update from SVN, full build & starts both servers.
      //      grunt d-cup: combines both above. (Clean & Update, Full Builds, Vagrant & Watch)
      //      grunt s: exploded redeploy and server restart
      //      grunt si: incremental build, exploded deploy

      // Dev
      grunt.registerTask('d', [
         'shell:startRepoExistingBuild', // Don't do a repo build, just start an existing one.
         'shell:startShare',
         'dev'
      ]);

      // Dev - as per dev, but with a clean update first.
      grunt.registerTask('d-cup', [
         'cup',
         'dev'
      ]);

      grunt.registerTask('dev', [
         'watch'
      ]);

      // Shortcuts to incremental builds of Share/Repo
      grunt.registerTask('r', [
         'shell:killRepo',
         'shell:startRepo'
      ]);
      grunt.registerTask('s', [
         'shell:se',
         'shell:resetCaches'
      ]);
      grunt.registerTask('si', [
         'shell:killShare',
         'shell:startShareInc'
      ]);

      grunt.registerTask('restartShare', [
         'shell:killShare',
         'shell:startShare'
      ]);

      // Svn up shorthand.
      grunt.registerTask('up', [
         'shell:svnUp'
      ]);

      // Build & start after a Clean & UPdate
      grunt.registerTask('cup', [
         'shell:killRepo',
         'shell:killShare',
         'shell:mvnClean',
         'shell:svnUp',
         'shell:npmInstall',
         'shell:startRepoFull',
         'shell:startShareInc'
      ]);

      grunt.registerTask('sel', [
         'shell:seleniumUp'
      ]);

      grunt.registerTask('down', [
         'shell:killRepo',
         'shell:killShare',
         'vdown'
      ]);

      grunt.registerTask('hideExistingCoverageReports', [
         'copy:coverageReportsToTemp',
         'clean:coverageReports'
      ]);

      grunt.registerTask('showExistingCoverageReports', [
         'copy:coverageReportsFromTemp',
         'clean:coverageReportsTemp'
      ]);

      grunt.registerTask('scb', [
         'shell:scb'
      ]);

      // Re-build test app for development purposes...
      grunt.registerTask('utd', [
         'shell:stopTestApp',
         'shell:se',
         'startUnitTestApp',
         'waitServer'
      ]);

      // Display notifications on test passes and failures...
      var notify = require('./node_modules/grunt-notify/lib/notify-lib');
      grunt.event.on("intern.fail", function(data) {
         notify({
            title: "Unit Test Failed",
            message: data
         });
      });
   };
})();
