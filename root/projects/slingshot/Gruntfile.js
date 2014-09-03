'use strict';

// Keep all paths in one place
var alf = {
      cssFiles: 'source/web/**/*.css',
      jsdocFiles: 'source/web/js/alfresco',
      jsInstFiles: 'source/web/js/alfrescoInst',
      jsFiles: ['source/web/js/alfresco/**/*.js'],
      xmlFiles: ['/**/*.xml'],
      testResourcesDir: 'source/test-resources',
      nodeBinDir: 'node_modules/.bin/',
      coverageDirectory: "code-coverage-reports",
      rootDir: '../../',
      codeDir: '../../../',
      docsDir: 'docs'
   };

module.exports = function(grunt) {

   // Load all grunt modules
   // @see: https://github.com/sindresorhus/load-grunt-tasks
   require('load-grunt-tasks')(grunt)
   grunt.loadNpmTasks('grunt-notify');

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

   // TODO: Switch from Dev Env commands (which community don't have) to mvn targets if DevEnv isn't present.

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
      'vup',
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

   // Svn up shorthand.
   grunt.registerTask('up', [
      'shell:svnUp'
   ]);

   // Do a mvn and an ant clean.
   grunt.registerTask('clean', [
      'shell:antClean',
      'shell:mvnClean'
   ]);

   // Build & start after a Clean & UPdate
   grunt.registerTask('cup', [
      'shell:killRepo',
      'shell:killShare',
      'clean',
      'shell:svnUp',
      'shell:startRepo',
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

   // Display notifications on test passes and failures...
   var notify = require('./node_modules/grunt-notify/lib/notify-lib');
   grunt.event.on("intern.pass", function(data) {
      notify({
         title: "Unit Test Passed",
         message: data 
      });
   });
   grunt.event.on("intern.fail", function(data) {
      notify({
         title: "Unit Test Failed",
         message: data 
      });
   });
};
