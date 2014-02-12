'use strict';

// Keep all paths in one place
var alf = {
      cssFiles: 'source/web/**/*.css',
      jsdocFiles: 'source/web/js/alfresco',
      jsInstFiles: 'source/web/js/alfrescoInst',
      jsFiles: [this.jsdocFiles + '/**/*.js'],
      testResourcesDir: 'source/test-resources',
      nodeBinDir: 'node_modules/.bin/',
      coverageDirectory: "code-coverage-reports"
   };

module.exports = function(grunt) {

   // Load all grunt modules
   // @see: https://github.com/sindresorhus/load-grunt-tasks
   require('load-grunt-tasks')(grunt)

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

   // Standard Dev task - sets up environment and then waits for your awesome code changes
   grunt.registerTask('start', [
      'concurrent:startRepoAndShare',
      'vag-up',
      'watch:docsAndTests'
   ])

   // Dev - bring up vagrant, provision and then 'watch' tests
   grunt.registerTask('dev', [
      'shell:vagrantUp',
      'watch'
   ]);   

}
