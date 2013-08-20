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
 * @module alfresco/tests/header/SitesMenuTestService
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
    * 1. Load menu data
    *    - Check load failed
    *    - Check load success
    *       - Check no recent sites hides group
    *       - Check some recent sites adds group
    * 2. Favourites menu data
    *    - Check load failed
    *    - Check load success
    *       - Check no favourites adds "empty" marker
    *       - Check some favourites does not add "empty" marker
    * 3. Add Favourite
    *    - Check option available
    *    - Check add removes "empty" marker
    *    - Check add removes "Add" option
    *    - Check add adds "Remove" option
    * 4. Remove Favourite
    *    - Check option available
    *    - Check remove adds "empty" marker
    *    - Check remove adds "Add" option
    *    - Check remove removes "Remove" option
    */
   return declare(CommonTestService, {
      
      /**
       * Register the tests for the Header widgets
       * 
       * @instance
       */
      constructor: function() {
         
         var _this = this;
         this.favouritesAdded = [];
         this.alfSubscribe("ALF_ADD_FAVOURITE_SITE", function(payload) {
            _this.favouritesAdded.push(payload);
         });
         this.favouritesRemoved = [];
         this.alfSubscribe("ALF_REMOVE_FAVOURITE_SITE", function(payload) {
            _this.favouritesRemoved.push(payload);
         });
         
         doh.register("Site Menu tests", [
            {
               name: "Test setup",
               timeout: 2000,
               runTest: this.testSetup,
               scope: this
            },
            {
               name: "Test Rendering (part 1)",
               timeout: 20000,
               runTest: this.testRenderingPart1,
               scope: this
            },
            {
               name: "Test Rendering (part 2)",
               timeout: 20000,
               runTest: this.testRenderingPart2,
               scope: this
            },
            {
               name: "Test favourite updates",
               timeout: 20000,
               runTest: this.testUpdateFavourites,
               scope: this
            },
            {
               name: "Test favourite notification processing",
               timeout: 2000,
               runTest: this.testProcessFavourites,
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
         
         // There are 3 sites menus...
         // 1) Will intentionally fail to load the data
         // 2) Will load the data but NOT the favourites and has no recent site history, but does have
         //    the option to add a favourite
         // 3) Loads data and favourites and has an option to remove a favourite
         
         this.scope.findTestObjects(["HEADER", 
                                      "SITES_MENU_1",
                                      "SITES_MENU_2",
                                      "SITES_MENU_3"]);
      },
      
      /**
       * This tests that all the data is loaded (or fails to load) as appropriate after clicking on 
       * all the menus.
       * @instance
       */
      testRenderingPart1: function(doh) {
         var d = new doh.Deferred();
         
         robot.mouseMoveAt(this.scope.testObjects.SITES_MENU_1.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open the first menu
         robot.mouseMoveAt(this.scope.testObjects.SITES_MENU_2.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open the second menu
         robot.mouseMoveAt(this.scope.testObjects.SITES_MENU_3.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open the second menu
         
         
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            
            // Site1 should still only have one child...
            var site1Children = _this.scope.testObjects.SITES_MENU_1.popup.getChildren();
            doh.assertEqual(1, site1Children.length, "There was not the expected number of children of the first sites menu popup");
           
            // Get the menu popups...
            _this.scope.testObjects.POPUP1 = _this.scope.testObjects.SITES_MENU_1.popup;
            doh.assertNotEqual(null, _this.scope.testObjects.POPUP1, "No popup for Sites Menu 1");
            _this.scope.testObjects.POPUP2 = _this.scope.testObjects.SITES_MENU_2.popup;
            doh.assertNotEqual(null, _this.scope.testObjects.POPUP2, "No popup for Sites Menu 2");
            _this.scope.testObjects.POPUP3 = _this.scope.testObjects.SITES_MENU_3.popup;
            doh.assertNotEqual(null, _this.scope.testObjects.POPUP2, "No popup for Sites Menu 3");
            
            // Menu 1 should only have 1 child (a group with the failed to load item)
            var popup1Children = _this.scope.testObjects.POPUP1.getChildren();
            doh.assertEqual(1, popup1Children.length, "Expecting an automatically added group as the only child");
            var popup1GrandChildren = popup1Children[0].getChildren();
            doh.assertEqual(1, popup1GrandChildren.length, "Expecting the single 'fail' menu item");

            // Menu 1 should also only have 1 child (the "Useful" group)
            // The Useful group should have 4 items (site-finder, create-site, favourites menu and add favourite)
            var popup2Children = _this.scope.testObjects.POPUP2.getChildren();
            doh.assertEqual(1, popup1Children.length, "Expecting the 'Useful' group as the only child");
            var popup2GrandChildren = popup2Children[0].getChildren();
            doh.assertEqual(4, popup2GrandChildren.length, "Expecting 4 children (site finder, create site, favourites and add)");

            // The 3rd item in the menu should be the favourites cascade...
            _this.scope.testObjects.FAV2 = popup2GrandChildren[2];
            _this.scope.testObjects.ADD2 = popup2GrandChildren[3];
            
            // Menu 3 should have 2 children ("Recent" and "Useful")
            // The Recent group should have 3 children
            var popup3Children = _this.scope.testObjects.POPUP3.getChildren();
            doh.assertEqual(2, popup3Children.length, "Expecting 'Recent' and 'Useful' groups as the children");
            var popup3Recent = popup3Children[0].getChildren();
            doh.assertEqual(3, popup3Recent.length, "Expecting 3 recent items");
            var popup3Useful = popup3Children[1].getChildren();
            doh.assertEqual(4, popup3Useful.length, "Expecting 4 children (site finder, create site, favourites and remove");

            // Create references to objects we want to work with...
            _this.scope.testObjects.REC3 = popup3Recent;
            _this.scope.testObjects.FAV3 = popup3Useful[2];
            _this.scope.testObjects.REM3 = popup3Useful[3];
            
            // Check that the appropriate add and remove items are correct in the menus...
            doh.assertEqual("ALF_ADD_FAVOURITE_SITE", popup2GrandChildren[3].publishTopic, "Menu 2 should offer add favourite");
            doh.assertEqual("ALF_REMOVE_FAVOURITE_SITE", popup3Useful[3].publishTopic, "Menu 3 should offer remove favourite");
            
         }), 900);
         return d;
      },
      
      /**
       * This tests that all the data is loaded (or fails to load) as appropriate after clicking on 
       * all the menus.
       * @instance
       */
      testRenderingPart2: function(doh) {
         var d = new doh.Deferred();
         
         // Click on the favourites menus to load them...
         robot.mouseMoveAt(this.scope.testObjects.SITES_MENU_2.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open the second menu
         robot.mouseMoveAt(this.scope.testObjects.FAV2.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open it's favourites
         robot.mouseMoveAt(this.scope.testObjects.SITES_MENU_3.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open the third menu
         robot.mouseMoveAt(this.scope.testObjects.FAV3.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open it's favourites
         
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            
            // The first favourites list should just have the failure item...
            var fav2Children = _this.scope.testObjects.FAV2.popup.getChildren();
            doh.assertEqual(1, fav2Children.length, "Expecting an automatically added group as the only child");
            var fav2GrandChildren = fav2Children[0].getChildren();
            doh.assertEqual(1, fav2GrandChildren.length, "Expecting the single 'fail' menu item");

            // The second favourites list have 3 entries
            var fav3Children = _this.scope.testObjects.FAV3.popup.getChildren();
            doh.assertEqual(1, fav3Children.length, "Expecting one group containing 3 favourites");
            var fav3List = fav3Children[0].getChildren();
            doh.assertEqual(3, fav3List.length, "Expecting 3 favourites");
            
            // Keep track of the favourites
            _this.scope.testObjects.FAV3_LIST = fav3Children[0];
            _this.scope.testObjects.FAV3_ITEM1 = fav3List[0];
            _this.scope.testObjects.FAV3_ITEM2 = fav3List[1];
            _this.scope.testObjects.FAV3_ITEM3 = fav3List[2];

         }), 900);
         return d;
      },
      
      /**
       * This tests adding and removing favourites
       * @instance
       */
      testUpdateFavourites: function(doh) {
         
         var d = new doh.Deferred();
         
         this.scope.favouritesAdded = [];
         this.scope.favouritesRemoved = [];
         
         robot.mouseMoveAt(this.scope.testObjects.SITES_MENU_2.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open the second menu
         robot.mouseMoveAt(this.scope.testObjects.ADD2.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Click on the add favourite
         robot.mouseMoveAt(this.scope.testObjects.SITES_MENU_3.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Open the third menu
         robot.mouseMoveAt(this.scope.testObjects.REM3.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                                    // Click on the remove favourite
         
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            
            
            // Test that the keyboard selection results were as expected. Each keyboard selection should have
            // resulted in a new String being pushed into the keyboardNavResults array. This array can be 
            // compared with the results we were expecting.
            var expectedResults = ["testsite1"];
            doh.assertEqual(expectedResults.length, _this.scope.favouritesAdded.length, "Expecting just 1 site to be added");
            array.forEach(expectedResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.favouritesAdded[index].site, "Unexpected favourite added");
            });
            
            expectedResults = ["testsite3"];
            doh.assertEqual(expectedResults.length, _this.scope.favouritesRemoved.length, "Expecting just 1 site to be removed");
            array.forEach(expectedResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.favouritesRemoved[index].site, "Unexpected favourite removed");
            });
         }), 900);
         return d;
      },
      
      /**
       * This tests the menus ability to process favourite notifications
       * @instance
       */
      testProcessFavourites: function(doh) {
         this.scope.alfPublish("ALF_FAVOURITE_SITE_REMOVED", {
            site: "testsite2"
         });
         this.scope.alfPublish("ALF_FAVOURITE_SITE_ADDED", {
            site: "testsite2a",
            title: "Test Site 2a"
         });
         
         var _this = this;
         var d = new doh.Deferred();
         setTimeout(d.getTestCallback(function(){
            doh.assertEqual(3, _this.scope.testObjects.FAV3_LIST.getChildren().length, "There should still be 3 children");
            doh.assertEqual("testsite2a", _this.scope.testObjects.FAV3_LIST.getChildren()[1].siteShortName, "Favourite not added as expected");
            doh.assertEqual("testsite3", _this.scope.testObjects.FAV3_LIST.getChildren()[2].siteShortName, "Favourite not removed as expected");
         }), 500);
         return d;
      }
   });
});
