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
 * This test uses a MockXhr service to test the site service responds as required.
 * 
 * @author Richard Smith
 */
define(["intern!object",
        "intern/chai!expect",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!leadfoot/keys"], 
        function (registerSuite, expect, require, TestCommon, keys) {

   registerSuite({
      name: 'Site Service Test',
      'SiteService': function () {

         var browser = this.remote;
         var testname = "SiteServiceTest";
         return TestCommon.loadTestWebScript(this.remote, "/SiteService", testname)

         // Create and edit
         .findById("CREATE_SITE")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_CREATE_SITE");
            })
            .end()

         .findById("EDIT_SITE")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_EDIT_SITE");
            })
            .sleep(1000)
            .end()

         .findByCssSelector("div.yui-simple-dialog span.yui-button button")
            .click()
            .end()

         // Site details
         .findById("GET_SITE_DETAILS")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_SITE_DETAILS");
            })
            .end()

         .findById("GET_SITE_DETAILS_BAD1")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_SITE_DETAILS with a faulty payload 1");
            })
            .end()

         .findById("GET_SITE_DETAILS_BAD2")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_SITE_DETAILS with a faulty payload 2");
            })
            .end()

         .findById("GET_SITE_DETAILS_BAD3")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_SITE_DETAILS with a faulty payload 3");
            })
            .end()

         // Add favourite site
         .findById("ADD_FAVOURITE_SITE")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_ADD_FAVOURITE_SITE");
            })
            .end()

         .findById("ADD_FAVOURITE_SITE_BAD1")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_ADD_FAVOURITE_SITE with a faulty payload 1");
            })
            .end()

         .findById("ADD_FAVOURITE_SITE_BAD2")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_ADD_FAVOURITE_SITE with a faulty payload 2");
            })
            .end()

         // Remove favourite site
         .findById("REMOVE_FAVOURITE_SITE")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_REMOVE_FAVOURITE_SITE");
            })
            .end()

         .findById("REMOVE_FAVOURITE_SITE_BAD1")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_REMOVE_FAVOURITE_SITE with a faulty payload 1");
            })
            .end()

         .findById("REMOVE_FAVOURITE_SITE_BAD2")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_REMOVE_FAVOURITE_SITE with a faulty payload 2");
            })
            .end()

         // Get site memberships
         .findById("GET_SITE_MEMBERSHIPS")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_SITE_MEMBERSHIPS");
            })
            .end()

         .findById("GET_SITE_MEMBERSHIPS_BAD1")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_SITE_MEMBERSHIPS with a faulty payload 1");
            })
            .end()

         .findById("GET_SITE_MEMBERSHIPS_BAD2")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_SITE_MEMBERSHIPS with a faulty payload 2");
            })
            .end()

         .findById("GET_SITE_MEMBERSHIPS_BAD3")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_SITE_MEMBERSHIPS with a faulty payload 3");
            })
            .end()

         // Update site
         .findById("UPDATE_SITE_DETAILS")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_UPDATE_SITE_DETAILS");
            })
            .end()

         .findById("UPDATE_SITE_DETAILS_BAD1")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_UPDATE_SITE_DETAILS with a faulty payload 1");
            })
            .end()

         // Become site manager
         .findById("BECOME_SITE_MANAGER")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_BECOME_SITE_MANAGER");
            })
            .end()

         .findById("BECOME_SITE_MANAGER_BAD1")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_BECOME_SITE_MANAGER with a faulty payload 1");
            })
            .end()

         .findById("BECOME_SITE_MANAGER_BAD2")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_BECOME_SITE_MANAGER with a faulty payload 2");
            })
            .end()

         .findById("BECOME_SITE_MANAGER_BAD3")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_BECOME_SITE_MANAGER with a faulty payload 3");
            })
            .end()

         // Request site membership
         .findById("REQUEST_SITE_MEMBERSHIP")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_REQUEST_SITE_MEMBERSHIP");
            })
            .end()

         .findByCssSelector("div.alfresco-dialog-AlfDialog div.footer span.dijitReset.dijitInline.dijitButtonNode")
            .click()
            .end()

         .findById("REQUEST_SITE_MEMBERSHIP_BAD1")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_REQUEST_SITE_MEMBERSHIP with a faulty payload 1");
            })
            .end()

         .findById("REQUEST_SITE_MEMBERSHIP_BAD2")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_REQUEST_SITE_MEMBERSHIP with a faulty payload 2");
            })
            .end()

         .findById("REQUEST_SITE_MEMBERSHIP_BAD3")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_REQUEST_SITE_MEMBERSHIP with a faulty payload 3");
            })
            .end()

         .findByCssSelector("div.alfresco-dialog-AlfDialog div.footer span.dijitReset.dijitInline.dijitButtonNode")
            .click()
            .end()

         .findById("REQUEST_SITE_MEMBERSHIP_BAD4")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_REQUEST_SITE_MEMBERSHIP with a faulty payload 4");
            })
            .end()

         .findByCssSelector("div.alfresco-dialog-AlfDialog div.footer span.dijitReset.dijitInline.dijitButtonNode")
            .click()
            .end()

         // Recent sites
         .findById("GET_RECENT_SITES")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_RECENT_SITES");
            })
            .end()

         .findById("GET_RECENT_SITES_BAD1")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_RECENT_SITES with a faulty payload 1");
            })
            .end()

         // Favourite sites
         .findById("GET_FAVOURITE_SITES")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_FAVOURITE_SITES");
            })
            .end()

         .findById("GET_FAVOURITE_SITES_BAD1")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_GET_FAVOURITE_SITES with a faulty payload 1");
            })
            .end()

         // Delete site
         .findById("DELETE_SITE")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_DELETE_SITE");
            })
            .end()

         .findByCssSelector("div.alfresco-dialog-AlfDialog div.footer span.dijitReset.dijitInline.dijitButtonNode:first-of-type")
            .click()
            .end()

         // Join site
         .findById("JOIN_SITE")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_JOIN_SITE");
            })
            .end()

         .findById("JOIN_SITE_BAD1")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_JOIN_SITE with a faulty payload 1");
            })
            .end()

         .findById("JOIN_SITE_BAD2")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_JOIN_SITE with a faulty payload 2");
            })
            .end()

         .findById("JOIN_SITE_BAD3")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_JOIN_SITE with a faulty payload 3");
            })
            .end()

         // Leave site
         .findById("LEAVE_SITE_BAD1")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_LEAVE_SITE with a faulty payload 1");
            })
            .end()

         .findByCssSelector("div.dijitDialogPaneContent > div.footer > span:nth-child(2) > span")
            .click()
            .end()

         .findById("LEAVE_SITE_BAD2")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_LEAVE_SITE with a faulty payload 2");
            })
            .end()

         .findByCssSelector("div.dijitDialogPaneContent > div.footer > span:nth-child(2) > span")
            .click()
            .end()

         .findById("LEAVE_SITE")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_LEAVE_SITE");
            })
            .end()

         .findByCssSelector("div.dijitDialogPaneContent > div.footer > span:nth-child(2) > span")
            .click()
            .end()
         
         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         });
      },

      'SiteService - phase2': function () {

         var browser = this.remote;
         var testname = "SiteServiceTest - phase2";
         return TestCommon.loadTestWebScript(this.remote, "/SiteService", testname)

         .end()

         .findById("LEAVE_SITE")
            .click()
            .then(function () {
               TestCommon.log(testname,"Test ALF_LEAVE_SITE");
            })
            .end()

         .findByCssSelector("div.dijitDialogPaneContent > div.footer > span:nth-child(1) > span")
            .click()
            .sleep(500)
            .end()

         .then(function () {
            TestCommon.postCoverageResults(browser, true);
         });

      }
   });
});