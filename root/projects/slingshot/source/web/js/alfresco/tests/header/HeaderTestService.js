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
 * @module alfresco/tests/header/HeaderTestService
 * @extends module:alfresco/tests/CommonTestService
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/tests/CommonTestService",
        "doh/runner",
        "dojo/robot",
        "dojo/keys",
        "dijit/registry",
        "dojo/query",
        "dojo/dom-attr",
        "dojo/dom-style",
        "dojo/_base/array",
        "dojo/date/stamp",
        "alfresco/menus/AlfMenuGroup",
        "dojo/domReady!"],
        function(declare, CommonTestService, doh, robot, keys, registry, query, domAttr, domStyle, array, stamp, AlfMenuGroup) {
   
   /**
    * Tests to implement...
    * 
    * 1) Navigation
    *    a) Make sure that both the UserStatus and SearchBox widgets can be accessed (DONE)
    *    b) Make sure that you can traverse the groups in drop down 1 via the keyboard (check that focus of UserStatus is handled) (DONE)
    *    c) Make sure that you can set status via keyboard (DONE)
    *    d) Make sure that you can issue a search via the keyboard (DONE)
    *    e) Make sure that advanced search can be accessed via the keyboard (DONE)
    * 2) Events
    *    a) Make sure that status can be populated by event (DONE)
    *    b) Make sure that search box grows and shrinks with focus (DONE)
    * 3) Rendering
    *    a) Make sure menu bar items include the drop down arrow (DONE)
    *    b) Make sure that user status is emptied when focus is gained (DONE)
    *    c) Make sure that user status is retrieved when status is not posted (DONE)
    * 4) Mouse navigation
    *    a) Post status via mouse (DONE)
    *    b) Search via mouse (DONE)
    *    c) Advanced search via mouse (DONE)
    */
   return declare(CommonTestService, {
      
      /**
       * Register the tests for the Header widgets
       * 
       * @instance
       */
      constructor: function() {
         
         var _this = this;
         
         /* Create an array to keep track of all the status updates that are posted. The
          * UserStatus widget will post to this topic each time a status is updated. Normally
          * this topic will be handled by the UserService.
          */
         this.statusUpdatesPosted = [];
         this.alfSubscribe("ALF_UPDATE_USER_STATUS", function(payload) {
            _this.statusUpdatesPosted.push(payload);
         });
         
         
         doh.register("Header tests", [
            {
               name: "Test setup",
               timeout: 2000,
               runTest: this.testSetup,
               scope: this
            },
            {
               name: "Test Rendering",
               timeout: 2000,
               runTest: this.testRendering,
               scope: this
            },
            {
               name: "Test UserStatus Accessibility",
               timeout: 20000,
               runTest: this.testUserStatusAccessibility,
               scope: this
            },
            {
               name: "Test SearchBox Accessibility",
               timeout: 20000,
               runTest: this.testSearchBoxAccessibility,
               scope: this
            },
            {
               name: "Test Events",
               timeout: 20000,
               runTest: this.testEvents,
               scope: this
            },
            {
               name: "Test Mouse Navigation",
               timeout: 20000,
               runTest: this.testMouseCapabilities,
               scope: this
            },
            {
               name: "Test Clear User Status",
               timeout: 20000,
               runTest: this.testUserStatusClick,
               scope: this
            },
            {
               name: "Test Reset User Status",
               timeout: 20000,
               runTest: this.testUserStatusBlurNoUpdate,
               scope: this
            },
            {
               name: "Test Preserve Partial Status",
               timeout: 20000,
               runTest: this.testUserStatusBlurPartialUpdate,
               scope: this
            }
         ]);
         doh.run();
      },
      
      /**
       * This test makes sure that all the expected objects are in place. It does this 
       * by using the Dijit registry to find all the widgets that are expected to be on the
       * page. These are then stored as objects in the service instance for easy reference
       * later on.
       * @instance
       */
      testSetup: function(doh) {
         this.scope.findTestObjects(["HEADER", 
                                      "LEFT_MENU",
                                      "MENU_1",
                                      "DROP_DOWN_1_GROUP_1", 
                                      "BEFORE_USER_STATUS", 
                                      "USER_STATUS", 
                                      "DROP_DOWN_1_GROUP_2", 
                                      "AFTER_USER_STATUS", 
                                      "RIGHT_MENU", 
                                      "SEARCH_BOX"]);
         
         this.scope.testObjects.SEARCH_BOX_MENU = this.scope.testObjects.SEARCH_BOX._searchMenu.getChildren()[0];
         doh.assertNotEqual(null, this.scope.testObjects.SEARCH_BOX_MENU, "Could not find search box menu");
         this.scope.testObjects.ADVANCED_SEARCH_MENU_ITEM = this.scope.testObjects.SEARCH_BOX_MENU.getChildren()[0].popup.getChildren()[0].getChildren()[0];
         doh.assertNotEqual(null, this.scope.testObjects.ADVANCED_SEARCH_MENU_ITEM, "Could not find search box advanced menu item");
      },
      
      /**
       * This checks rendering of the widgets. At the moment it just checks for the adding drop down arrow in the menu.
       * @instance
       */
      testRendering: function(doh) {
         
         // ...the menu items should have a visible icon node with the right class...
         doh.assertEqual(1, query(".alf-menu-arrow", this.scope.testObjects.LEFT_MENU.domNode).length, "The correct number of arrow classes was not found");
      },
      
      /**
       * This test makes sure that its possible to access the User Status widget (regardless of 
       * it's position within in the menu (e.g. last in a group, with another group). The test
       * placement is intentionally different to the Share implementation for test variation.
       * @instance
       */
      testUserStatusAccessibility: function(doh) {
         
         var d = new doh.Deferred();
         
         // Clear any previous results...
         this.scope.keyboardNavResults = [];
         this.scope.statusUpdatesPosted = [];
         
         var statusUpdate1 = "Status Update 1";
         
         // Check traversal of groups in a single drop-down menu...
         robot.keyPress(keys.TAB, 200);          // Select the menu bar
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the menu (first item selected)
         robot.keyPress(keys.SPACE, 200);        // Select first item
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to second item (User Status)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to first item in SECOND group
         robot.keyPress(keys.SPACE, 200);        // Select 
         robot.keyPress(keys.UP_ARROW, 200);     // Move BACK to last item in FIRST group (User Status)
         
         // Delete the previous status...
         // NOTE: Due to the user status widget being placed inside a menu (which relies on certain keys having
         //       certain values, we cannot automatically clear the previous status with a single click ("space"
         //       is space and "enter" is post - both "click" control keys have uses for this widget).
         for (var i=0; i<22; i++)
         {
            robot.keyPress(keys.DELETE, 50);
         }
         
         robot.typeKeys(statusUpdate1, 500, 2000); // Enter a new user status
         robot.keyPress(keys.ENTER, 200);        // Select 
         robot.keyPress(keys.UP_ARROW, 200);     // Move to first item in FIRST group
         robot.keyPress(keys.SPACE, 200);        // Select 
         
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            
            // Test that the keyboard selection results were as expected. Each keyboard selection should have
            // resulted in a new String being pushed into the keyboardNavResults array. This array can be 
            // compared with the results we were expecting.
            var expectedResults = ["BEFORE_USER_STATUS","AFTER_USER_STATUS","BEFORE_USER_STATUS"];
            doh.assertEqual(expectedResults.length, _this.scope.keyboardNavResults.length, "The number of recorded keyboard selections did not match expectations");
            array.forEach(expectedResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.keyboardNavResults[index], "Unexpected keyboard selection logged");
            });
            
            // Check the status update was received correctly...
            doh.assertEqual(1, _this.scope.statusUpdatesPosted.length, "The number of status updates was not as expected");
            doh.assertEqual(statusUpdate1, _this.scope.statusUpdatesPosted[0].status, "The status update was not posted correctly");
            
         }), 900);
         return d;
      },
      
      /**
       * This test makes sure that it's possible to navigate to the search box and both perform
       * a regular search and request an advanced search simply through key presses.
       * @instance
       */
      testSearchBoxAccessibility: function(doh) {
         var d = new doh.Deferred();
         
         // Clear previous results...
         this.scope.pageRequests = [];
         
         var searchString = "SearchThis";
         
         robot.keyPress(keys.TAB, 200);                // Close Drop Down
         robot.keyPress(keys.TAB, 200);                // Move focus off Left Drop Down menu (from previous test)
         robot.keyPress(keys.TAB, 200);                // Focus Advanced Search drop down
         robot.keyPress(keys.TAB, 200);                // Focus Search box
         robot.typeKeys(searchString, 500, 2000);      // Enter a new search term
         robot.keyPress(keys.ENTER, 200);              // Submit search (page redirection doesn't occur because NavigationService is not present!)
         robot.keyPress(keys.TAB, 200, {shift: true}); // Go back to advanced search menu box
         robot.keyPress(keys.DOWN_ARROW, 200);         // Open menu
         robot.keyPress(keys.SPACE, 200);              // Select 
         
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            
            var expectedPageReqResults = ["search?t=" + searchString, "advsearch"];
            doh.assertEqual(expectedPageReqResults.length, _this.scope.pageRequests.length, "The number of recorded page requests did not match expectations");
            array.forEach(expectedPageReqResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.pageRequests[index], "Unexpected page request logged");
            });
            
         }), 900);
         return d;
      },
      
      /**
       * This test ensures that the User Status can receive updates (these could be posted by the UserService
       * or from other widget) and that the SearchBox grows and shrinks with focus.
       * @instance
       */
      testEvents: function(doh) {
         
         var statusUpdate2 = "Received Status";
         
         // Post a status update (this should get reflected in the UserStatus widget)...
         this.scope.alfPublish("ALF_USER_STATUS_UPDATED", {
            userStatus: statusUpdate2,
            userStatusTime: stamp.toISOString(new Date())
         });
         
         // Test the status has been processed...
         doh.assertEqual(statusUpdate2, this.scope.testObjects.USER_STATUS.userStatus, "The user status was not updated correctly");
      },
      
      /**
       * This test checks that the user status can be updated via mouse and that search and advanced search can be 
       * requested by mouse.
       * @instance
       */
      testMouseCapabilities: function(doh) {
         
         var d = new doh.Deferred();
         
         // Clear previous results...
         this.scope.pageRequests = [];
         this.scope.statusUpdatesPosted = [];
         
         var searchString = "SearchThat",
             statusUpdate = "another Status";
         
         robot.mouseMoveAt(this.scope.testObjects.MENU_1.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open left drop down menu
         robot.mouseMoveAt(this.scope.testObjects.DROP_DOWN_1_GROUP_1.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // 
         robot.mouseMoveAt(this.scope.testObjects.USER_STATUS._userStatusWidget.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Click in user status
         robot.typeKeys(statusUpdate, 500, 2000);                                               // Enter a new user status
         robot.mouseMoveAt(this.scope.testObjects.USER_STATUS._postButtonNode, 500);            // Move to the post button
         robot.mouseClick({left:true}, 500);                                                    // Submit the status
         robot.mouseMoveAt(this.scope.testObjects.SEARCH_BOX._searchTextNode, 500);             // Move to the search box input field
         robot.mouseClick({left:true}, 500);                                                    // Place the cursor in the input field
         robot.typeKeys(searchString, 500, 2000);                                               // Enter a new search term
         robot.keyPress(keys.ENTER, 200);                                                       // Submit search
         robot.mouseMoveAt(this.scope.testObjects.SEARCH_BOX_MENU.domNode, 500);                // Move to the search menu
         robot.mouseClick({left:true}, 500);                                                    // Open left drop down menu
         robot.mouseMoveAt(this.scope.testObjects.ADVANCED_SEARCH_MENU_ITEM.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Select advanced search
         
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            
            // Check the status update was received correctly...
            doh.assertEqual(1, _this.scope.statusUpdatesPosted.length, "The number of status updates was not as expected");
            doh.assertEqual(statusUpdate, _this.scope.statusUpdatesPosted[0].status, "The status update was not posted correctly");
            
            var expectedPageReqResults = ["search?t=" + searchString, "advsearch"];
            doh.assertEqual(expectedPageReqResults.length, _this.scope.pageRequests.length, "The number of recorded page requests did not match expectations");
            array.forEach(expectedPageReqResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.pageRequests[index], "Unexpected page request logged");
            });
            
         }), 900);
         return d;
      },
      
      /**
       * This test makes sure that the previous user status is cleared when clicked
       * @instance
       */
      testUserStatusClick: function(doh) {
         var d = new doh.Deferred();
         
         // Status message to be cleared...
         var statusUpdate = "Status to clear";
         
         // Set a new status...
         this.scope.alfPublish("ALF_USER_STATUS_UPDATED", {
            userStatus: statusUpdate,
            userStatusTime: stamp.toISOString(new Date())
         });
         
         robot.mouseMoveAt(this.scope.testObjects.MENU_1.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open left drop down menu
         robot.mouseMoveAt(this.scope.testObjects.DROP_DOWN_1_GROUP_1.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open left drop down menu
         robot.mouseMoveAt(this.scope.testObjects.USER_STATUS._userStatusWidget.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Click in user status
         
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            var userStatusTextBoxValue = _this.scope.testObjects.USER_STATUS._userStatusWidget.attr("value");
            doh.assertEqual("", userStatusTextBoxValue, "The previous status was not cleared");
            
         }), 900);
         return d;
      },
      
      /**
       * This test makes sure that when you move the mouse away from the UserStatus message it gets reset if there
       * has been no keypresses.
       * @instance
       */
      testUserStatusBlurNoUpdate: function(doh) {
         var d = new doh.Deferred();
         
         var statusUpdate = "Control Status"; // For setting status that we check is reset

         // Set a new status...
         this.scope.alfPublish("ALF_USER_STATUS_UPDATED", {
            userStatus: statusUpdate,
            userStatusTime: stamp.toISOString(new Date())
         });

         // Now clear it
         robot.mouseMoveAt(this.scope.testObjects.USER_STATUS._userStatusWidget.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Click in user status
         
         // Now move away...
         robot.mouseMoveAt(this.scope.testObjects.SEARCH_BOX.domNode, 500); 
         
         // Check the status is reset...
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            var userStatusTextBoxValue = _this.scope.testObjects.USER_STATUS._userStatusWidget.attr("value");
            doh.assertEqual(statusUpdate, userStatusTextBoxValue, "The previous status was not reset");
            
         }), 900);
         return d;
      },
      
      /**
       * This test makes sure that when you move the mouse away from the UserStatus message it gets left if some
       * status has been updated but NOT posted
       * @instance
       */
      testUserStatusBlurPartialUpdate: function(doh) {
         var d = new doh.Deferred();
         
         var statusUpdate = "Partial Stat"; // For setting status that we check is reset

         robot.mouseMoveAt(this.scope.testObjects.MENU_1.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open left drop down menu
         robot.mouseMoveAt(this.scope.testObjects.USER_STATUS._postButtonNode, 500);            // Move to the post button
         robot.mouseClick({left:true}, 500);                                                    // Submit the status
         
         // Start a new status...
         robot.mouseMoveAt(this.scope.testObjects.USER_STATUS._userStatusWidget.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Click in user status
         robot.typeKeys(statusUpdate, 500, 2000);                                               // Enter the start of a status message
         
         // Now move away...
         robot.mouseMoveAt(this.scope.testObjects.SEARCH_BOX.domNode, 500); 
         
         // Check the status is reset...
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            var userStatusTextBoxValue = _this.scope.testObjects.USER_STATUS._userStatusWidget.attr("value");
            doh.assertEqual(statusUpdate, userStatusTextBoxValue, "The previous status was not reset");
            
         }), 900);
         return d;
      }
      
   });
});
