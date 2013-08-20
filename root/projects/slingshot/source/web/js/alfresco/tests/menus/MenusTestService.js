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
 * @module alfresco/tests/menus/MenusTestService
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
        "alfresco/menus/AlfMenuGroup",
        "dojo/domReady!"],
        function(declare, CommonTestService, doh, robot, keys, registry, query, domAttr, domStyle, array, AlfMenuGroup) {
   
   /**
    * Tests to implement...
    * 1) Check general rendering
    *    a) Are menus/groups/items localized (DONE)
    *    b) Do groups get rendered correctly without a label (DONE)
    *    c) Does a group get created automatically when not specified (DONE)
    *    d) Does a menu item get correctly wrapped when the DOM requires it
    *    e) Do icons get rendered correctly (DONE)
    *    
    * 2) Navigation
    *    a) Can drop down menus be accessed by the keyboard (DONE)
    *    b) Are groups successfully traversed (including items that are wrapped or do not get focus) (DONE)
    *    
    * 3) Actions...
    *    a) Check publication clicks (DONE)
    *    b) Check navigation requests (DONE)
    *    c) Check clicks via keyboard (DONE)
    *    
    * 4) Cascading menus
    *    a) Check keyboard navigation (DONE)
    *    b) Check mouse navigation (DONE)
    */
   return declare(CommonTestService, {
      
      /**
       * @instance
       */
      constructor: function() {
         
         // Create a reference to this Service for capturing and reading tests events...
         doh.register("Menu tests", [
            {
               name: "Test setup",
               timeout: 2000,
               runTest: this.testSetup,
               scope: this
            },
            {
               name: "Test Localization",
               timeout: 2000,
               runTest: this.testLocalization,
               scope: this
            },
            {
               name: "Test Rendering",
               timeout: 2000,
               runTest: this.testRendering,
               scope: this
            },
            {
               name: "Test Grouping",
               timeout: 2000,
               runTest: this.testGrouping,
               scope: this
            },
            {
               name: "Test Group Navigation",
               timeout: 40000,
               runTest: this.testGroupNavigation,
               scope: this
            },
            {
               name: "Test Cascade Kevboard Navigation",
               timeout: 40000,
               runTest: this.testCascadeKeyboardNavigation,
               scope: this
            },
            {
               name: "Test Actions",
               timeout: 40000,
               runTest: this.testActions,
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

         this.scope.findTestObjects(["DROP_DOWN_MENU_1", 
                                     "DROP_DOWN_MENU_2", 
                                     "DROP_DOWN_MENU_3", 
                                     "DROP_DOWN_MENU_4", 
                                     "DROP_DOWN_1_GROUP_1", 
                                     "DROP_DOWN_1_GROUP_2", 
                                     "DROP_DOWN_1_GROUP_3", 
                                     "DROP_DOWN_4_GROUP_1", 
                                     "MENU_ITEM_1",
                                     "MENU_ITEM_2",
                                     "MENU_ITEM_3",
                                     "MENU_ITEM_4",
                                     "MENU_ITEM_5",
                                     "MENU_ITEM_6",
                                     "MENU_ITEM_7",
                                     "MENU_ITEM_8",
                                     "MENU_ITEM_9",
                                     "MENU_ITEM_10",
                                     "MENU_BAR_ITEM_1",
                                     "DROP_DOWN_MENU_5",
                                     "CASCADING_MENU_1",
                                     "CASCADE_MENU_ITEM_1",
                                     "CASCADE_MENU_ITEM_2",
                                     "CASCADE_MENU_ITEM_3",
                                     "CASCADE_SUB_MENU_1",
                                     "CASCADE_MENU_ITEM_4",
                                     "CASCADE_MENU_ITEM_5"]);
      },
      
      /**
       * This test checks that the menu bar items, groups and menu items have been correctly
       * localized. Only one of each is checked (it is not necessary to check all the values)
       * 
       * @instance
       */
      testLocalization: function(doh) {
         doh.assertEqual("Groups With Labels", this.scope.testObjects.DROP_DOWN_MENU_1.label, "Drop down menu label not localized correctly");
         doh.assertEqual("Group 1 (Drop Down 1)", this.scope.testObjects.DROP_DOWN_1_GROUP_1.label, "Drop down menu GROUP label not localized correctly");
         doh.assertEqual("Menu Item 1", this.scope.testObjects.MENU_ITEM_1.label, "Drop down menu ITEM label not localized correctly");
      },
      
      /**
       * This test ensures that everything is rendered correctly. In particular it looks at the CSS classes
       * and styling applied to menu items and pop-ups that have icons
       * 
       * @instance
       */
      testRendering: function(doh) {
         // Test the menu bar popup has an icon...
         var dropDownWithIcons = this.scope.testObjects.DROP_DOWN_MENU_4;
         doh.assertTrue(dropDownWithIcons.domNode.firstChild.nodeName.toUpperCase() == "SPAN", "First child of AlfMenuBarPopup is not a span element");
         doh.assertTrue(domAttr.get(dropDownWithIcons.domNode.firstChild, "class") == "alf-configure-icon", "First child of AlfMenuBarPopup does not have class 'alf-configure-icon' as expected");
         
         // Test the label node is present and a span and that it has the right class for a popup with
         // an icon. This is important as its the best we can do for an automated "inspection" of the visual
         // appearance.
         doh.assertTrue(dropDownWithIcons.domNode.children[1].nodeName.toUpperCase() == "SPAN", "Second child of AlfMenuBarPopup is not a span element");
         doh.assertTrue(domAttr.get(dropDownWithIcons.domNode.children[1], "class") == "alf-menu-bar-label-node alf-menu-bar-popup-label-node", "Second child of AlfMenuBarPopup does not have class 'alf-menu-bar-popup-label-node' as expected");
         
         // We're expecting that the popup of the menu bar popup has a solitary child that is the only group configured...
         doh.assertEqual(1, dropDownWithIcons.popup.getChildren().length, "The AlfMenuBarPopup popup does not have a single child as expected");
         
         // ...and we're expecting it to be an AlfMenuGroup...
         var innerGroup = dropDownWithIcons.popup.getChildren()[0];
         doh.assertTrue(innerGroup.isInstanceOf(AlfMenuGroup), "The AlfMenuBarPopup's popup is NOT a menu group");
         
         // ...it should have 2 items...
         doh.assertEqual(2, innerGroup.getChildren().length, "The menu group does not have 2 children as expected");
         
         // ...the menu items should have a visible icon node with the right class...
         doh.assertEqual(1, query(".dijitIcon.dijitMenuItemIcon.alf-edit-icon", innerGroup.domNode).length, "The right number of edit icon menu items was not found");
         doh.assertEqual(1, query(".dijitIcon.dijitMenuItemIcon.alf-edit-icon", innerGroup.domNode).length, "The right number of cog icon menu items was not found");
         
         // Test that the menu item that does NOT inherit from MenuItem gets wrapped appropriately...
         doh.assertEqual(1, query(".alf-menu-item-wrapper-row").length == 1, "The right number of wrapped menu items was NOT found");
      },
      
      /**
       * This tests the following:
       * - that groups are correctly rendered when no label is provided
       * - that a group is automatically created when not specified in the JSON definition
       * 
       * @instance
       */
      testGrouping: function(doh) {
         
         // Check that the group heading with no label is not displayed...
         var noLabelGroupDisplay = domStyle.get(this.scope.testObjects.DROP_DOWN_1_GROUP_3._groupTitleNode, "display");
         doh.assertEqual("none", noLabelGroupDisplay, "Group heading with no label is displayed");
         
         // Check that a group is created for a menu item that is not explicitly grouped...
         var item7Parent = this.scope.testObjects.MENU_ITEM_7.getParent();
         doh.assertNotEqual(null, item7Parent, "The ungrouped menu item has no parent");
         doh.assertTrue(item7Parent.isInstanceOf(AlfMenuGroup), "The ungrouped menu item has a parent that is NOT a menu group");
      },
      
      /**
       * This test checks that its possible to navigate between multiple groups in a drop
       * down menu just using the keyboard.
       * 
       * @instance
       */
      testGroupNavigation: function(doh) {
         var d = new doh.Deferred();
         
         // Reset the results...
         this.scope.keyboardNavResults = [];
         this.scope.pageRequests = [];
         
         // Check traversal of groups in a single drop-down menu...
         robot.keyPress(keys.TAB, 200);          // Select the menu bar
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the menu (first item selected)
         robot.keyPress(keys.SPACE, 200);        // Select first item
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to second item
         robot.keyPress(keys.ENTER, 200);        // Select second item (use ENTER for variation)
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to first item in SECOND group
         robot.keyPress(keys.SPACE, 200);        // Select 
         robot.keyPress(keys.UP_ARROW, 200);     // Move BACK to last item in FIRST group
         robot.keyPress(keys.ENTER, 200);        // Select 
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to first item in SECOND group
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to second item in SECOND group
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to third item in SECOND group
         robot.keyPress(keys.SPACE, 200);        // Select 
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to first item in THIRD group (has no label)
         robot.keyPress(keys.ENTER, 200);        // Select 
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to FIRST item in FIRST group
         robot.keyPress(keys.SPACE, 200);        // Select 
         robot.keyPress(keys.UP_ARROW, 200);     // Move BACK to only item in THIRD group
         robot.keyPress(keys.SPACE, 200);        // Select 
         
         // Check menu bar navigation...
         robot.keyPress(keys.RIGHT_ARROW, 200);  // Move across to the menu bar item...
         robot.keyPress(keys.SPACE, 200);        // Select the menu bar item
         robot.keyPress(keys.RIGHT_ARROW, 200);  // Move across to the ungrouped items drop down
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the drop down...
         robot.keyPress(keys.SPACE, 200);        // Select the first item
         robot.keyPress(keys.DOWN_ARROW, 200);   // Move to the SECOND item in the drop down...
         robot.keyPress(keys.SPACE, 200);        // Select the SECOND item
         
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            
            // Test that the keyboard selection results were as expected. Each keyboard selection should have
            // resulted in a new String being pushed into the keyboardNavResults array. This array can be 
            // compared with the results we were expecting.
            var expectedResults = ["MENU_ITEM_1","MENU_ITEM_2","MENU_ITEM_3","MENU_ITEM_2","MENU_ITEM_5","MENU_ITEM_6","MENU_ITEM_1","MENU_ITEM_6"];
            doh.assertEqual(expectedResults.length, _this.scope.keyboardNavResults.length, "The number of recorded keyboard selections did not match expectations");
            array.forEach(expectedResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.keyboardNavResults[index], "Unexpected keyboard selection logged");
            });
            
            // Test that the page requests were expected after navigating the menu bar...
            var expectedPageReqResults = ["MENU_BAR_ITEM_1", "MENU_ITEM_7", "MENU_ITEM_8"];
            doh.assertEqual(expectedPageReqResults.length, _this.scope.pageRequests.length, "The number of recorded page requests did not match expectations");
            array.forEach(expectedPageReqResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.pageRequests[index], "Unexpected page request logged");
            });
         }), 900);
         
         return d;
      },
      
      /**
       * This test drives the cascade menu with the keyboard to check that it works
       * 
       * @instance
       */
      testCascadeKeyboardNavigation: function(doh) {
         var d = new doh.Deferred();
         
         // Reset the results...
         this.scope.keyboardNavResults = [];
         this.scope.pageRequests = [];

         // Move to the dropdown with cascades...
         robot.keyPress(keys.RIGHT_ARROW, 200);  
         robot.keyPress(keys.RIGHT_ARROW, 200);  
         robot.keyPress(keys.RIGHT_ARROW, 200);  
         
         // Open the dropdown (should be on the cascade item)
         robot.keyPress(keys.DOWN_ARROW, 200);   
         robot.keyPress(keys.RIGHT_ARROW, 200);  // Open the cascade

         
         robot.keyPress(keys.DOWN_ARROW, 200);   // Go to the second item
         robot.keyPress(keys.SPACE, 200);        // Select the second item
         
         robot.keyPress(keys.DOWN_ARROW, 200);   // Go to the sub-cascade
         robot.keyPress(keys.DOWN_ARROW, 200);   
         robot.keyPress(keys.RIGHT_ARROW, 200);  // Open the sub-cascade 
         
         robot.keyPress(keys.DOWN_ARROW, 200);   // Go to the second item in the sub-cascade
         robot.keyPress(keys.SPACE, 200);        // Select the second item in the sub-cascade
         
         robot.keyPress(keys.LEFT_ARROW, 200);   // Close the sub-cascade 
         robot.keyPress(keys.UP_ARROW, 200);     // Go up to the last item in the first group
         robot.keyPress(keys.SPACE, 200);        // Select the second item in the sub-cascade
         
         robot.keyPress(keys.LEFT_ARROW, 200);   // Close the first-cascade
         robot.keyPress(keys.LEFT_ARROW, 200);   // Move off the menu and to the previous menu
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the menu
         robot.keyPress(keys.SPACE, 200);        // Select first item in that group
         
         robot.keyPress(keys.RIGHT_ARROW, 200);  // Go back to the cascade dropdown
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open the dropdown
         robot.keyPress(keys.RIGHT_ARROW, 200);  // Open the cascade
         robot.keyPress(keys.SPACE, 200);        // Select first item
         robot.keyPress(keys.RIGHT_ARROW, 200);  // Close cascade and go to first dropdown
         
         robot.keyPress(keys.DOWN_ARROW, 200);   // Open dropdown and select first item
         robot.keyPress(keys.SPACE, 200);        // Select the second item
         
         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            
            // Test that the keyboard selection results were as expected. Each keyboard selection should have
            // resulted in a new String being pushed into the keyboardNavResults array. This array can be 
            // compared with the results we were expecting.
            var expectedResults = ["CASCADE_MENU_ITEM_2", "CASCADE_MENU_ITEM_5", "CASCADE_MENU_ITEM_3", "MENU_ITEM_9", "CASCADE_MENU_ITEM_1", "MENU_ITEM_1"];
            doh.assertEqual(expectedResults.length, _this.scope.keyboardNavResults.length, "The number of recorded keyboard selections did not match expectations");
            array.forEach(expectedResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.keyboardNavResults[index], "Unexpected keyboard selection logged");
            });
            
         }), 900);
         
         return d;
      },
      
      /**
       * This test checks the results of clicking on menu items to ensure that the publish
       * and navigate actions are handled appropriately.
       * 
       * @instance
       */
      testActions: function(doh) {
         var d = new doh.Deferred();
         
         // Reset the values logged from the previous keyboard test...
         this.scope.keyboardNavResults = [];
         this.scope.pageRequests = [];
         
         // Click on the first drop down menu...
         robot.mouseMoveAt(this.scope.testObjects.DROP_DOWN_MENU_1.domNode, 500); 
         robot.mouseClick({left:true}, 500);                                    // Open first drop down menu
         robot.mouseMoveAt(this.scope.testObjects.MENU_ITEM_5.domNode, 500);      
         robot.mouseClick({left:true}, 500);                                    // Select item 5
         robot.mouseMoveAt(this.scope.testObjects.DROP_DOWN_MENU_2.domNode, 500);
         robot.mouseClick({left:true}, 500);                                    // Open 2nd drop down menu
         robot.mouseMoveAt(this.scope.testObjects.MENU_ITEM_7.domNode, 500);
         robot.mouseClick({left:true}, 500);                                    // Select item 7 (1st option)
         robot.mouseMoveAt(this.scope.testObjects.MENU_BAR_ITEM_1.domNode, 500);
         robot.mouseClick({left:true}, 500);                                    // Select menu bar item
         
         robot.mouseMoveAt(this.scope.testObjects.DROP_DOWN_MENU_5.domNode, 500);
         robot.mouseClick({left:true}, 500);                                    // Open the cascade dropdown
         robot.mouseMoveAt(this.scope.testObjects.CASCADING_MENU_1.domNode, 500);
         robot.mouseClick({left:true}, 500);                                    // Open the first cascade
         robot.mouseMoveAt(this.scope.testObjects.CASCADE_SUB_MENU_1.domNode, 500);
         robot.mouseClick({left:true}, 500);                                    // Open the second cascade
         robot.mouseMoveAt(this.scope.testObjects.CASCADE_MENU_ITEM_4.domNode, 500);
         robot.mouseClick({left:true}, 500);                                    // Click the second item

         var _this = this;
         robot.sequence(d.getTestCallback(function(){
            var expectedResults = ["MENU_ITEM_5", "CASCADE_MENU_ITEM_4"],
                expectedPageReqResults = ["MENU_ITEM_7", "MENU_BAR_ITEM_1"];
            doh.assertEqual(expectedResults.length, _this.scope.keyboardNavResults.length, "The number of recorded mouse selections did not match expectations");
            array.forEach(expectedResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.keyboardNavResults[index], "Unexpected mouse selection logged");
            });
            doh.assertEqual(expectedPageReqResults.length, _this.scope.pageRequests.length, "The number of recorded page requests did not match expectations");
            array.forEach(expectedPageReqResults, function(expectedResult, index) {
               doh.assertEqual(expectedResult, _this.scope.pageRequests[index], "Unexpected page request logged");
            });
            doh.is(true, true);
         }), 900);
         return d;
      }
   });
});
