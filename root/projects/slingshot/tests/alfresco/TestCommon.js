/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * This provides the common capabilities for widget unit tests.
 * 
 * @author Dave Draper
 */
define(["intern/dojo/node!fs",
        "config/Config",
        "intern/dojo/node!leadfoot/helpers/pollUntil",
        "intern/lib/args"], 
       function(fs, Config, pollUntil, args) {
   return {

      /**
       * Path configurations.
       */
      paths: {
         bootstrapPath: "/share/page/tp/ws/unit-test-bootstrap",
         moduleDeploymentPath: "/share/page/modules/deploy"
      },

      /**
       * This is the URL to use to bootstrap tests. It is composed of the paths.bootstrapPath and the 
       * Config.urls.bootstrapBaseUrl which is provided in the config package.
       *
       * @instance
       * @type {string}
       * @default Config.url.bootstrapBaseUrl + this.paths.bootstrapPath
       */
      bootstrapUrl: function bootstrapUrl(){
         return Config.urls.bootstrapBaseUrl + this.paths.bootstrapPath;
      },

      /**
       * This is the URL to use to access the module deployment screen. It is composed of the 
       * paths.moduleDeploymentPath and the Config.urls.bootstrapBaseUrl which is provided in the config package.
       *
       * @instance
       * @type {string}
       * @default Config.url.bootstrapBaseUrl + this.paths.moduleDeploymentPath
       */
      moduleDeploymentUrl: function moduleDeploymentUrl(){
         return Config.urls.moduleDeploymentBaseUrl + this.paths.moduleDeploymentPath;
      },

      /**
       * Loads and returns a page mode. This uses the Node provided readFileSync function which
       * is a synchronous call to load the requested resource into a variable. 
       *
       * @instance
       * @param {string} fileName The path to the file containing the page model to be loaded.
       */
      loadPageModel: function(fileName) {
         var fileContent;
         try
         {
            fileContent = fs.readFileSync(fileName, 'utf-8');
         }
         catch (e)
         {
            console.log("############################################");
            console.log("#                                          #");
            console.log("# AN ERROR OCCURRED READING THE PAGE MODEL #");
            console.log("#                                          #");
            console.log("############################################");
            console.log(e);
         }
         return fileContent;
      },

      /**
       * This function should be called at the start of each unit test. It calls the bootstrap test page
       * and enters the test data into the textarea and clicks the "Test" button which will load the 
       * test model in a new page. The unit test can then run against that page.
       *
       * @instance
       * @param {object} browser This should be the the "remote" attribute from the unit test
       * @param {string} testPageDefinitionFile This should be the path to the resource that contains
       * the JSON definition of the page to test.
       * @returns {promise} The promise for continuing the unit test.
       */
      bootstrapTest: function(browser, testPageDefinitionFile, testname) {

         this._applyTimeouts(browser);
         this._maxWindow(browser);

         if(testname && browser.environmentType.browserName)
         {
            console.log(">> Starting '" + testname + "' on " + browser.environmentType.browserName);
         }

         // Load the model definition file
         // It's necessary to remove any carriage returns and new line characters from the page model otherwise the eval statement will cause an error...
         var pageModel;
         try
         {
            pageModel = (this.loadPageModel(testPageDefinitionFile)).replace(/[\n\r]/g, "");
         }
         catch (e)
         {
            console.log("###############################################");
            console.log("#                                             #");
            console.log("# AN ERROR OCCURRED PROCESSING THE PAGE MODEL #");
            console.log("#                                             #");
            console.log("###############################################");
            console.log(e);
         }

         return browser

         .get(this.bootstrapUrl())
         .then(pollUntil('return document.getElementsByClassName("allWidgetsProcessed");'))
         .then(function (element) {}, function (error) {})
         .end()

         .execute("dijit.registry.byId('UNIT_TEST_MODEL_FIELD').setValue('" + pageModel + "');'set';")
         .findByCssSelector('#UNIT_TEST_MODEL_FIELD TEXTAREA')
         .type(" ")
         .end()

         .findById("LOAD_TEST_BUTTON")
         .click()
         .end()

         .then(pollUntil('return document.getElementsByClassName("allWidgetsProcessed");'))
         .then(function (element) {}, function (error) {})
         .end()

      },

      /**
       * This function enables the debug module on the server to make sure debug logging is available for
       * use in functional test.
       *
       * @instance
       * @param {object} browser This should be the the "remote" attribute from the unit test
       * @returns {promise} The promise for continuing the unit test.
       */
      enableDebugModule: function(browser) {

         this._applyTimeouts(browser);

         console.log(">> Enabling debug via Debug Enabler Extension");

         return browser.get(this.moduleDeploymentUrl())
         .end()

         .findByCssSelector("select[name='undeployedModules'] > option[value*='Debug Enabler Extension']")
         .click()
         .end()

         .findByCssSelector("td > input[value='Add']")
         .click()
         .end()

         .findByCssSelector("input[value='Apply Changes']")
         .click()
         .end()
         
//       this._applyTimeouts(browser);
//       this._maxWindow(browser);
//       console.log(">> Enabling debug via Debug Enabler Extension");
//
//       browser.get(this.moduleDeploymentUrl()).end();
//
//       var hasEnabler = true;
//       browser.hasElementByCssSelector("select[name='undeployedModules'] > option[value*='Debug Enabler Extension']")
//       .then(function(has){
//          console.log(has);
//       })
//       .end();
//
//       if(hasEnabler)
//       {
//          browser.findByCssSelector("select[name='undeployedModules'] > option[value*='Debug Enabler Extension']").click().end();
//          browser.findByCssSelector("td > input[value='Add']").click().end();
//          browser.findByCssSelector("input[value='Apply Changes']").click().end();
//       }
//
//       return browser;

      },

      /**
       * This function disables the debug module on the server.
       *
       * @instance
       * @param {object} browser This should be the the "remote" attribute from the unit test
       * @returns {promise} The promise for continuing the unit test.
       */
      disableDebugModule: function(browser) {

         this._applyTimeouts(browser);
         console.log(">> Disabling debug via Debug Enabler Extension");

         return browser.get(this.moduleDeploymentUrl())
         .end()

         .findByCssSelector("select[name='deployedModules'] > option[value*='Debug Enabler Extension']")
         .click()
         .end()

         .findByCssSelector("td > input[value='Remove']")
         .click()
         .end()

         .findByCssSelector("input[value='Apply Changes']")
         .click()
         .end()
         
      },

      /**
       * Set browser timeouts - refer to Config files
       * Allows us to use "elementBy..." calls rather than a "waitForElementBy..." which is more efficient...
       *
       * @instance
       * @param {browser}
       */
      _applyTimeouts: function(browser) {
         browser.setTimeout("script", Config.timeout.base);
         browser.setTimeout("implicit", Config.timeout.base);
         browser.setFindTimeout(Config.timeout.find);
         browser.setPageLoadTimeout(Config.timeout.pageLoad);
         browser.setExecuteAsyncTimeout(Config.timeout.executeAsync);
      },

      /**
       * Maximises the browser window if not already maximised
       *
       * @instance
       * @param {browser}
       */
      _maxWindow: function(browser) {
         // browser.maximizeWindow();
         browser.setWindowSize(null, 1024, 768);
      },

      /**
       * Internal function used to determine whether to use nth-child or last-child pseudo selector
       *
       * @instance
       * @param {number} expectedRow
       * @returns {string} The pseudo selector to use
       */
      _determineRow: function(expectedRow) {
         var row = "last-child"
         if (expectedRow != "last")
         {
            row = "nth-child(" + expectedRow + ")"
         };
         return row;
      },

      /**
       * This generates a CSS selector that attempts to select a publication payload entry from the SubscriptionLog
       * widget. It's looking for a specific publish topic so could return multiple results.
       *
       * @instance
       * @param {string} publishTopic The topic published on
       * @param {string} key The key for the data
       * @param {string} value The value for the data
       * @returns {string} The CSS selector
       */
      pubDataCssSelector: function(publishTopic, key, value) {

         var selector = "td[data-publish-topic='" + publishTopic + "'] + " +
                        "td.sl-data tr.sl-object-row " +
                        "td[data-pubsub-object-key=" + 
                        key + 
                        "]+td[data-pubsub-object-value='" + 
                        value + "']";
         return selector;
      },

      /**
       * This generates a CSS selector that attempts to select a publication payload entry from the SubscriptionLOg
       * widget where the payload contains a nested key/value pair that is the value of a key
       *
       * @instance
       * @param {string} publishTopic The topic published on
       * @param {string} key The key for the data
       * @param {string} nestedKey The key nested as the value for the data
       * @param {string} nestedValue The value of the nested data.
       * @returns {string} The CSS selector
       */
      pubDataNestedValueCssSelector: function(publishTopic, key, nestedKey, nestedValue) {
         var selector = "td[data-publish-topic='" + publishTopic + "'] + " +
                        "td.sl-data tr.sl-object-row " +
                        "td[data-pubsub-object-key=" + 
                        key + 
                        "]+ td td[data-pubsub-object-key='" + 
                        nestedKey + "'] " + 
                        "+ td[data-pubsub-object-value='" + 
                        nestedValue + "']";
         return selector;
      },

      /**
       * This generates a CSS selector that attempts to select a publication payload entry from the SubscriptionLOg
       * widget where the payload contains a nested array that is the value of a key
       *
       * @instance
       * @param {string} publishTopic The topic published on
       * @param {string} key The key for the data
       * @param {string} arrayIndex The index of the nested array
       * @param {string} value The expected value of the nested data.
       * @returns {string} The CSS selector
       */
      pubDataNestedArrayValueCssSelector: function(publishTopic, key, arrayIndex, value) {
         var selector = "td[data-publish-topic='" + publishTopic + "'] + " +
                        "td.sl-data tr.sl-object-row " +
                        "td[data-pubsub-object-key=" + 
                        key + 
                        "]+ td td[data-pubsub-object-value='" + 
                        value + "']:nth-child(" + arrayIndex + ")";
         return selector;
      },

      /**
       * This generates a CSS selector that attempts to select a publication payload entry from the SubscriptionLog
       * widget. It looks on a specific row of the table for an entry with a specific key/value pair. It's important to
       * remember that the first generated row will be 3 (!! THREE !!) because the index starts at 1 NOT 0 and the first
       * row is the header and the second row will be the publication indicating that the page has loaded.
       *
       * @instance
       * @param {number} expectedRow The row that the data is expected to be found in (can be set to "last")
       * @param {string} key The key for the data
       * @param {string} value The value for the data
       * @returns {string} The CSS selector
       */
      pubSubDataCssSelector: function(expectedRow, key, value) {

         var row = ""
         if (expectedRow == "any")
         {
            // Don't specify a row
         }
         else if (expectedRow == "last")
         {
            row = ":last-child"
         }
         else if (expectedRow != "last")
         {
            row = ":nth-child(" + expectedRow + ")"
         };

         var selector = ".alfresco-testing-SubscriptionLog tr.sl-row" + row +
            " td[data-pubsub-object-key=" + 
            key + 
            "]+td[data-pubsub-object-value='" + 
            value + "']";
         // console.log("Topic selector: " + selector);

         return selector;

      },

      /**
       * This generates a CSS selector that gets the topic cell for a specific row
       *
       * @instance
       * @param {number} expectedRow The row to get the topic for
       */
      nthTopicSelector: function(expectedRow) {

         var row = this._determineRow(expectedRow);
         var selector = ".alfresco-testing-SubscriptionLog tr.sl-row:" + row + " td.sl-topic";
         // console.log("Selector: " + selector);

         return selector;

      },

      /**
       * This generates a CSS selector that attempts to find all elements that match a subscription topic entry from the
       * SubscriptionLog widget.
       *
       * @instance
       * @param {string} topic The topic to search
       * @param {string} type (optional) The topic action (e.g. "publish" or "subscribe") defaults to "subscribe"
       * @param {string} expectedRow (optional) A specific row to check for ("last" is an accepted option). Negative numbers trigger a backwards count.
       * @param {string} [matchType="exact"] Choose between a partial, prefix, suffix or exact match on topic.
       * @returns {string} The CSS selector
       */
      topicSelector: function(topic, type, expectedRow, matchType) {

         if (type == null)
         {
            type = "subscribe";
         }

         var row = "";
         if (expectedRow == "any")
         {
            // Don't specify a row
         }
         else if (expectedRow == "last")
         {
            row = ":last-child"
         }
         else if (expectedRow != null)
         {
            var rowSelector = "nth-child";
            if (expectedRow.indexOf("-") !== -1)
            {
               // If the expected row contains a negative number, count backwards. -1 is last, -2 is penultimate, etc.
               rowSelector = "nth-last-child";
               expectedRow = expectedRow.slice(1, expectedRow.length);
            }
            row = ":" + rowSelector + "(" + expectedRow + ")";
         }

         // Allow partial matching, match prefix or suffix.
         matchType = matchType || "exact";
         var comparison = "=";

         switch (matchType) {
            case "partial":
               comparison = "*=";
               break;
            case "prefix":
               comparison = "$=";
               break;
            case "suffix":
               comparison = "^=";
               break;
            default:
               comparison = "=";
         }

         var selector = ".alfresco-testing-SubscriptionLog tr.sl-row" + row + " td[data-" + type + "-topic" + comparison + topic + "]";
         // console.log("Topic selector: " + selector);
         return selector;
      },

      /**
       * This generates an xpath selector that matches the supplied value in the console log.
       *
       * @instance
       * @param {string} value The value to search for
       * @returns {string} The XPATH selector
       */
      consoleXpathSelector: function(value) {
         return "//table[@class=\"log\"]/tbody/tr[@class=\"cl-row\"]/td[contains(.,'" + value + "')]";
      },

      /**
       * This function searches for the button to post test coverage results to the "node-coverage"
       * server. It should be called at the end of each unit test
       *
       * @instance
       * @param {object} browser This should be set to a reference to "this.remote" from the unit test
       */
      postCoverageResults: function(browser) {
         if(args.doCoverage === "true")
         {
            browser.end()

            .findByCssSelector('.alfresco-testing-TestCoverageResults input[type=submit]')
            .click()
            .end();

            console.log(">> Coverage ~~>");
         }
         else
         {
            browser.end();
         }
      },

      /**
       * This function provides common logging for tests
       *
       * @instance
       * @param {string} test The name of the test running
       * @param {string} desc The test description
       */
      log: function(test, desc) {
         console.log(">> " + test + ": " + desc);
      }
   };
});