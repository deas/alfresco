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
 * This provides the common capabilities for widget unit tests.
 * 
 * @author Dave Draper
 */
define(["intern/dojo/node!fs",
        "config/Config"], 
       function(fs, Config) {
   return {

      /**
       * This is the path to use to bootstrap tests. It should ONLY be defined here so that
	   * pervasive changes can be made in this one file.
	   *
	   * @instance
	   * @type {string}
	   * @default "/share/page/tp/ws/unit-test-bootstrap"
	   */
      bootstrapPath: "/share/page/tp/ws/unit-test-bootstrap",

      /**
       * This is the URL to use to bootstrap tests. It is composed of the bootstrapPath and
       * the Config.bootstrapUrl which is provided in the config package.
       *
       * @instance
       * @type {string}
       * @default Config.bootstrapBaseUrl + this.bootstrapPath
       */
      bootstrapUrl: function bootstrapUrl(){
    	   return Config.bootstrapBaseUrl + this.bootstrapPath;
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

         // Set browser timeouts - refer to Config files
         // This allows us to use "elementBy..." calls rather than a "waitForElementBy..." which is more efficient...
         browser.setImplicitWaitTimeout(Config.timeout.implicitWait);
         browser.setPageLoadTimeout(Config.timeout.pageLoad);
         browser.setAsyncScriptTimeout(Config.timeout.asyncScript);

         if(testname && browser.environmentType.browserName)
         {
            console.log(">> Starting " + testname + " on " + browser.environmentType.browserName);
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

         return browser.get(this.bootstrapUrl())

         .waitForElementByCss('.alfresco-core-Page.allWidgetsProcessed')
         .safeEval("dijit.registry.byId('UNIT_TEST_MODEL_FIELD').setValue('" + pageModel + "');'set';")
         .end()

         // It's necessary to type an additional space into the text area to ensure that the 
         // text area field validates and populates the form model with the data to be published...
         .elementByCss('#UNIT_TEST_MODEL_FIELD > DIV.control > TEXTAREA')
         .type(" ")
         .end()

         // Find and click on the test button to load the test page...
         .elementByCss("#LOAD_TEST_BUTTON")
         .moveTo()
         .sleep(500)
         .click()
         .sleep(500) // This sleep appears to be needed to prevent errors, but ideally it woudn't be here :(
         .end()
         .waitForElementByCss('.alfresco-core-Page.allWidgetsProcessed')
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

         var row = "last-child"
         if (expectedRow != "last")
         {
            row = "nth-child(" + expectedRow + ")"
         };

         var selector = ".alfresco-testing-SubscriptionLog tr.sl-row:" + row +
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
       * @param {string} expectedRow (optional) A specific row to check for ("last" is an accepted option)
       * @returns {string} The CSS selector
       */
      topicSelector: function(topic, type, expectedRow) {

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
            row = ":nth-child(" + expectedRow + ")"
         }

         var selector = ".alfresco-testing-SubscriptionLog tr.sl-row" + row + " td[data-" + type + "-topic=" + topic + "]";
         // console.log("Topic selector: " + selector);
         return selector;
      },

      /**
       * This function searches for the button to post test coverage results to the "node-coverage"
       * server. It should be called at the end of each unit test
       *
       * @instance
       * @param {object} browser This should be set to a reference to "this.remote" from the unit test
       */
      postCoverageResults: function(browser) {
         if(Config.doCoverageReport)
         {
            browser
            .end()
            .elementByCss('.alfresco-testing-TestCoverageResults input[type=submit]')
            .moveTo()
            .sleep(500)
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
       * @param {string} line The line number
       * @param {string} desc The test description
       */
      log: function(test, line, desc) {
         console.log(">> " + test + " [" + line + "]: " + desc);
      }
   };

});